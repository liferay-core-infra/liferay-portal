/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.store.s3;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;

import com.liferay.document.library.kernel.exception.AccessDeniedException;
import com.liferay.document.library.kernel.exception.NoSuchFileException;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.petra.io.unsync.UnsyncFilterInputStream;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.concurrent.ThreadPoolExecutor;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.store.s3.configuration.S3StoreConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;

import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.DeleteObjectsResponse;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.model.StorageClass;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Publisher;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.CompletedFileUpload;
import software.amazon.awssdk.transfer.s3.model.FileUpload;

/**
 * @author Brian Wing Shun Chan
 * @author Sten Martinez
 * @author Edward C. Han
 * @author Vilmos Papp
 * @author Máté Thurzó
 * @author Manuel de la Peña
 * @author Daniel Sanz
 */
@Component(
	configurationPid = "com.liferay.portal.store.s3.configuration.S3StoreConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	property = "store.type=com.liferay.portal.store.s3.S3Store",
	service = Store.class
)
public class S3Store implements Store {

	public void abortMultipartUploads(Date startDate) {
		_transferManager.abortMultipartUploads(_bucketName, startDate);
	}

	@Override
	public void addFile(
		long companyId, long repositoryId, String fileName, String versionLabel,
		InputStream inputStream) {

		if (hasFile(companyId, repositoryId, fileName, versionLabel)) {
			deleteFile(companyId, repositoryId, fileName, versionLabel);
		}

		try {
			File file = FileUtil.createTempFile(inputStream);

			String key = S3KeyTransformerUtil.getFileVersionKey(
				companyId, repositoryId, fileName, versionLabel);

			try {
				FileUpload fileUpload = _s3TransferManager.uploadFile(
					uploadFileBuilder -> {
						uploadFileBuilder.putObjectRequest(
							putObjectBuilder -> {
								putObjectBuilder.bucket(_bucketName);
								putObjectBuilder.key(key);
								putObjectBuilder.storageClass(_storageClass);
							});
						uploadFileBuilder.source(file);
					});

				CompletableFuture<CompletedFileUpload> completableFuture =
					fileUpload.completionFuture();

				completableFuture.join();
			}
			catch (CancellationException cancellationException) {
				if (_log.isDebugEnabled()) {
					_log.debug(cancellationException);
				}
			}
			catch (CompletionException completionException) {
				throw _transform(completionException.getCause());
			}
			finally {
				FileUtil.delete(file);
			}
		}
		catch (IOException ioException) {
			throw new SystemException(ioException);
		}
	}

	@Override
	public void deleteDirectory(
		long companyId, long repositoryId, String dirName) {

		List<ObjectIdentifier> objectIdentifiers = new ArrayList<>(_DELETE_MAX);

		List<S3Object> s3Objects = _getS3Objects(
			S3KeyTransformerUtil.getDirectoryKey(
				companyId, repositoryId, dirName));

		Iterator<S3Object> iterator = s3Objects.iterator();

		try {
			while (iterator.hasNext()) {
				for (int i = 0; i < _DELETE_MAX; i++) {
					if (iterator.hasNext()) {
						S3Object s3Object = iterator.next();

						objectIdentifiers.add(
							ObjectIdentifier.builder(
							).key(
								s3Object.key()
							).build());
					}
				}

				CompletableFuture<DeleteObjectsResponse> completableFuture =
					_s3AsyncClient.deleteObjects(
						builder -> {
							builder.bucket(_bucketName);
							builder.delete(
								deleteBuilder -> deleteBuilder.objects(
									objectIdentifiers));
						});

				completableFuture.join();

				objectIdentifiers.clear();
			}
		}
		catch (CompletionException completionException) {
			throw _transform(completionException.getCause());
		}
	}

	@Override
	public void deleteFile(
		long companyId, long repositoryId, String fileName,
		String versionLabel) {

		try {
			CompletableFuture<DeleteObjectResponse> completableFuture =
				_s3AsyncClient.deleteObject(
					builder -> {
						builder.bucket(_bucketName);
						builder.key(
							S3KeyTransformerUtil.getFileVersionKey(
								companyId, repositoryId, fileName,
								versionLabel));
					});

			completableFuture.join();
		}
		catch (CompletionException completionException) {
			throw _transform(completionException.getCause());
		}
	}

	@Override
	public InputStream getFileAsStream(
			long companyId, long repositoryId, String fileName,
			String versionLabel)
		throws PortalException {

		if (Validator.isNull(versionLabel)) {
			versionLabel = _getHeadVersionLabel(
				companyId, repositoryId, fileName);
		}

		String key = S3KeyTransformerUtil.getFileVersionKey(
			companyId, repositoryId, fileName, versionLabel);

		CompletableFuture<ResponseInputStream<GetObjectResponse>>
			completableFuture = _s3AsyncClient.getObject(
				builder -> {
					builder.bucket(_bucketName);
					builder.key(key);
				},
				AsyncResponseTransformer.toBlockingInputStream());

		try {
			return new UnsyncFilterInputStream(completableFuture.join());
		}
		catch (CompletionException completionException) {
			Throwable throwable = completionException.getCause();

			if (throwable instanceof NoSuchKeyException) {
				throw new NoSuchFileException(
					companyId, repositoryId, fileName, versionLabel);
			}

			throw _transform(throwable);
		}
	}

	@Override
	public String[] getFileNames(
		long companyId, long repositoryId, String dirName) {

		String key = null;

		if (Validator.isNull(dirName)) {
			key = S3KeyTransformerUtil.getRepositoryKey(
				companyId, repositoryId);
		}
		else {
			key = S3KeyTransformerUtil.getDirectoryKey(
				companyId, repositoryId, dirName);
		}

		List<S3Object> s3Objects = _getS3Objects(key);

		Iterator<S3Object> iterator = s3Objects.iterator();

		String[] fileNames = new String[s3Objects.size()];

		for (int i = 0; i < fileNames.length; i++) {
			S3Object s3Object = iterator.next();

			fileNames[i] = S3KeyTransformerUtil.getFileName(s3Object.key());
		}

		return fileNames;
	}

	@Override
	public long getFileSize(
			long companyId, long repositoryId, String fileName,
			String versionLabel)
		throws PortalException {

		if (Validator.isNull(versionLabel)) {
			versionLabel = _getHeadVersionLabel(
				companyId, repositoryId, fileName);
		}

		String key = S3KeyTransformerUtil.getFileVersionKey(
			companyId, repositoryId, fileName, versionLabel);

		CompletableFuture<HeadObjectResponse> completableFuture =
			_s3AsyncClient.headObject(
				builder -> {
					builder.bucket(_bucketName);
					builder.key(key);
				});

		try {
			HeadObjectResponse headObjectResponse = completableFuture.join();

			return headObjectResponse.contentLength();
		}
		catch (CompletionException completionException) {
			Throwable throwable = completionException.getCause();

			if (throwable instanceof NoSuchKeyException) {
				throw new NoSuchFileException(
					companyId, repositoryId, fileName);
			}

			throw _transform(throwable);
		}
	}

	@Override
	public String[] getFileVersions(
		long companyId, long repositoryId, String fileName) {

		String key = S3KeyTransformerUtil.getFileKey(
			companyId, repositoryId, fileName);

		List<S3Object> s3Objects = _getS3Objects(key);

		if (s3Objects.isEmpty()) {
			return StringPool.EMPTY_ARRAY;
		}

		String[] versions = new String[s3Objects.size()];

		for (int i = 0; i < s3Objects.size(); i++) {
			S3Object s3ObjectSummary = s3Objects.get(i);

			String versionKey = s3ObjectSummary.key();

			versions[i] = versionKey.substring(
				versionKey.lastIndexOf(CharPool.SLASH) + 1);
		}

		Arrays.sort(versions, DLUtil::compareVersions);

		return versions;
	}

	@Override
	public boolean hasFile(
		long companyId, long repositoryId, String fileName,
		String versionLabel) {

		try {
			if (Validator.isNull(versionLabel)) {
				versionLabel = _getHeadVersionLabel(
					companyId, repositoryId, fileName);
			}

			String key = S3KeyTransformerUtil.getFileVersionKey(
				companyId, repositoryId, fileName, versionLabel);

			CompletableFuture<HeadObjectResponse> completableFuture =
				_s3AsyncClient.headObject(
					builder -> {
						builder.bucket(_bucketName);
						builder.key(key);
					});

			completableFuture.join();

			return true;
		}
		catch (CompletionException completionException) {
			Throwable throwable = completionException.getCause();

			if (throwable instanceof NoSuchKeyException) {
				return false;
			}

			throw _transform(throwable);
		}
		catch (NoSuchFileException noSuchFileException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(noSuchFileException);
			}

			return false;
		}
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_s3StoreConfiguration = ConfigurableUtil.createConfigurable(
			S3StoreConfiguration.class, properties);

		_amazonS3 = _getAmazonS3(_getAWSCredentialsProvider());
		_bucketName = _s3StoreConfiguration.bucketName();
		_transferManager = _getTransferManager(_amazonS3);

		try {
			_storageClass = StorageClass.fromValue(
				_s3StoreConfiguration.s3StorageClass());
		}
		catch (IllegalArgumentException illegalArgumentException) {
			_storageClass = StorageClass.STANDARD;

			if (_log.isWarnEnabled()) {
				_log.warn(
					_s3StoreConfiguration.s3StorageClass() +
						" is not a valid value for the storage class",
					illegalArgumentException);
			}
		}
	}

	@Deactivate
	protected void deactivate() {
		_s3TransferManager.close();
		_s3AsyncClient.close();

		_amazonS3 = null;
		_bucketName = null;
		_s3AsyncClient = null;
		_s3StoreConfiguration = null;
		_s3TransferManager = null;
	}

	private void _configureProxySettings(
		ClientConfiguration clientConfiguration) {

		String proxyHost = _s3StoreConfiguration.proxyHost();

		if (Validator.isNull(proxyHost)) {
			return;
		}

		clientConfiguration.setProxyHost(proxyHost);
		clientConfiguration.setProxyPort(_s3StoreConfiguration.proxyPort());

		String proxyAuthType = _s3StoreConfiguration.proxyAuthType();

		if (proxyAuthType.equals("username-password")) {
			clientConfiguration.setProxyPassword(
				_s3StoreConfiguration.proxyPassword());
			clientConfiguration.setProxyUsername(
				_s3StoreConfiguration.proxyUsername());
		}
	}

	private AmazonS3 _getAmazonS3(
		AWSCredentialsProvider awsCredentialsProvider) {

		if (Validator.isNotNull(_s3StoreConfiguration.s3Endpoint()) &&
			Validator.isNotNull(_s3StoreConfiguration.s3Region())) {

			return AmazonS3ClientBuilder.standard(
			).withCredentials(
				awsCredentialsProvider
			).withClientConfiguration(
				_getClientConfiguration()
			).withEndpointConfiguration(
				new AwsClientBuilder.EndpointConfiguration(
					_s3StoreConfiguration.s3Endpoint(),
					_s3StoreConfiguration.s3Region())
			).withPathStyleAccessEnabled(
				_s3StoreConfiguration.s3PathStyle()
			).build();
		}

		AmazonS3ClientBuilder amazonS3ClientBuilder =
			AmazonS3ClientBuilder.standard(
			).withCredentials(
				awsCredentialsProvider
			).withClientConfiguration(
				_getClientConfiguration()
			).withPathStyleAccessEnabled(
				_s3StoreConfiguration.s3PathStyle()
			);

		if (Validator.isNotNull(_s3StoreConfiguration.s3Region())) {
			amazonS3ClientBuilder.setRegion(_s3StoreConfiguration.s3Region());
		}

		return amazonS3ClientBuilder.build();
	}

	private AWSCredentialsProvider _getAWSCredentialsProvider() {
		if (Validator.isNotNull(_s3StoreConfiguration.accessKey()) &&
			Validator.isNotNull(_s3StoreConfiguration.secretKey())) {

			AWSCredentials awsCredentials = new BasicAWSCredentials(
				_s3StoreConfiguration.accessKey(),
				_s3StoreConfiguration.secretKey());

			return new AWSStaticCredentialsProvider(awsCredentials);
		}

		return new DefaultAWSCredentialsProviderChain();
	}

	private ClientConfiguration _getClientConfiguration() {
		ClientConfiguration clientConfiguration = new ClientConfiguration();

		clientConfiguration.setConnectionTimeout(
			_s3StoreConfiguration.connectionTimeout());
		clientConfiguration.setMaxConnections(
			_s3StoreConfiguration.httpClientMaxConnections());
		clientConfiguration.setMaxErrorRetry(
			_s3StoreConfiguration.httpClientMaxErrorRetry());

		_configureProxySettings(clientConfiguration);

		return clientConfiguration;
	}

	private String _getHeadVersionLabel(
			long companyId, long repositoryId, String fileName)
		throws NoSuchFileException {

		List<S3Object> s3Objects = _getS3Objects(
			S3KeyTransformerUtil.getFileKey(companyId, repositoryId, fileName));

		if (s3Objects.isEmpty()) {
			throw new NoSuchFileException(companyId, repositoryId, fileName);
		}

		String headVersionKey = null;

		for (S3Object s3Object : s3Objects) {
			if ((headVersionKey == null) ||
				(headVersionKey.compareTo(s3Object.key()) < 0)) {

				headVersionKey = s3Object.key();
			}
		}

		return headVersionKey.substring(
			headVersionKey.lastIndexOf(CharPool.SLASH) + 1);
	}

	private List<S3Object> _getS3Objects(String prefix) {
		ListObjectsV2Publisher listObjectsV2Publisher =
			_s3AsyncClient.listObjectsV2Paginator(
				builder -> {
					builder.bucket(_bucketName);
					builder.prefix(prefix);
				});

		List<S3Object> s3Objects = new ArrayList<>();

		CompletableFuture<Void> completableFuture =
			listObjectsV2Publisher.subscribe(
				response -> s3Objects.addAll(response.contents()));

		try {
			completableFuture.join();
		}
		catch (CompletionException completionException) {
			throw _transform(completionException.getCause());
		}

		return s3Objects;
	}

	private TransferManager _getTransferManager(AmazonS3 amazonS3) {
		return TransferManagerBuilder.standard(
		).withS3Client(
			amazonS3
		).withExecutorFactory(
			() -> new ThreadPoolExecutor(
				_s3StoreConfiguration.corePoolSize(),
				_s3StoreConfiguration.maxPoolSize())
		).withMinimumUploadPartSize(
			(long)_s3StoreConfiguration.minimumUploadPartSize()
		).withMultipartUploadThreshold(
			(long)_s3StoreConfiguration.multipartUploadThreshold()
		).withShutDownThreadPools(
			false
		).build();
	}

	private SystemException _transform(Throwable throwable) {
		if (throwable instanceof AwsServiceException) {
			AwsServiceException awsServiceException =
				(AwsServiceException)throwable;

			AwsErrorDetails awsErrorDetails =
				awsServiceException.awsErrorDetails();

			String errorCode = awsErrorDetails.errorCode();

			StringBundler sb = new StringBundler(9);

			sb.append("{errorCode=");
			sb.append(errorCode);
			sb.append(", message=");
			sb.append(awsServiceException.getMessage());
			sb.append(", requestId=");
			sb.append(awsServiceException.requestId());
			sb.append(", statusCode=");
			sb.append(awsServiceException.statusCode());
			sb.append("}");

			if (errorCode.equals("AccessDenied")) {
				return new AccessDeniedException(sb.toString());
			}

			return new SystemException(sb.toString());
		}

		return new SystemException(throwable.getMessage(), throwable);
	}

	private static final int _DELETE_MAX = 1000;

	private static final Log _log = LogFactoryUtil.getLog(S3Store.class);

	private static volatile S3StoreConfiguration _s3StoreConfiguration;

	private AmazonS3 _amazonS3;
	private String _bucketName;
	private S3AsyncClient _s3AsyncClient;
	private S3TransferManager _s3TransferManager;
	private StorageClass _storageClass;
	private TransferManager _transferManager;

}