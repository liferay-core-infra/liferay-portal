/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.store.s3;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.StorageClass;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;

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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;

import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.transfer.s3.S3TransferManager;

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

	@Override
	public void addFile(
		long companyId, long repositoryId, String fileName, String versionLabel,
		InputStream inputStream) {

		if (hasFile(companyId, repositoryId, fileName, versionLabel)) {
			deleteFile(companyId, repositoryId, fileName, versionLabel);
		}

		File file = null;

		try {
			file = FileUtil.createTempFile(inputStream);

			Upload upload = null;

			try {
				String key = S3KeyTransformerUtil.getFileVersionKey(
					companyId, repositoryId, fileName, versionLabel);

				PutObjectRequest putObjectRequest = new PutObjectRequest(
					_bucketName, key, file);

				putObjectRequest.withStorageClass(_storageClass);

				upload = _transferManager.upload(putObjectRequest);

				upload.waitForCompletion();
			}
			catch (AmazonClientException amazonClientException) {
				throw _transform(amazonClientException);
			}
			catch (InterruptedException interruptedException) {
				if (_log.isDebugEnabled()) {
					_log.debug(interruptedException);
				}

				upload.abort();

				Thread thread = Thread.currentThread();

				thread.interrupt();
			}
		}
		catch (IOException ioException) {
			throw new SystemException(ioException);
		}
		finally {
			FileUtil.delete(file);
		}
	}

	@Override
	public void deleteDirectory(
		long companyId, long repositoryId, String dirName) {

		String key = S3KeyTransformerUtil.getDirectoryKey(
			companyId, repositoryId, dirName);

		try {
			String[] keys = new String[_DELETE_MAX];

			List<S3ObjectSummary> s3ObjectSummaries = _getS3ObjectSummaries(
				key);

			Iterator<S3ObjectSummary> iterator = s3ObjectSummaries.iterator();

			while (iterator.hasNext()) {
				DeleteObjectsRequest deleteObjectsRequest =
					new DeleteObjectsRequest(_bucketName);

				for (int i = 0; i < keys.length; i++) {
					if (iterator.hasNext()) {
						S3ObjectSummary s3ObjectSummary = iterator.next();

						keys[i] = s3ObjectSummary.getKey();
					}
					else {
						keys = Arrays.copyOfRange(keys, 0, i);

						break;
					}
				}

				deleteObjectsRequest.withKeys(keys);

				_amazonS3.deleteObjects(deleteObjectsRequest);
			}
		}
		catch (AmazonClientException amazonClientException) {
			throw _transform(amazonClientException);
		}
	}

	@Override
	public void deleteFile(
		long companyId, long repositoryId, String fileName,
		String versionLabel) {

		try {
			String key = S3KeyTransformerUtil.getFileVersionKey(
				companyId, repositoryId, fileName, versionLabel);

			DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(
				_bucketName, key);

			_amazonS3.deleteObject(deleteObjectRequest);
		}
		catch (AmazonClientException amazonClientException) {
			throw _transform(amazonClientException);
		}
	}

	public String getBucketName() {
		return _bucketName;
	}

	@Override
	public InputStream getFileAsStream(
			long companyId, long repositoryId, String fileName,
			String versionLabel)
		throws PortalException {

		try {
			if (Validator.isNull(versionLabel)) {
				versionLabel = _getHeadVersionLabel(
					companyId, repositoryId, fileName);
			}

			String key = S3KeyTransformerUtil.getFileVersionKey(
				companyId, repositoryId, fileName, versionLabel);

			GetObjectRequest getObjectRequest = new GetObjectRequest(
				_bucketName, key);

			S3Object s3Object = _amazonS3.getObject(getObjectRequest);

			if (s3Object == null) {
				throw new NoSuchFileException(
					companyId, repositoryId, fileName, versionLabel);
			}

			InputStream s3InputStream = s3Object.getObjectContent();

			if (s3InputStream == null) {
				throw new IOException("S3 object input stream is null");
			}

			return new UnsyncFilterInputStream(s3InputStream) {

				@Override
				public void close() throws IOException {
					super.close();

					s3Object.close();
				}

			};
		}
		catch (AmazonClientException amazonClientException) {
			if (_isFileNotFound(amazonClientException)) {
				throw new NoSuchFileException(
					companyId, repositoryId, fileName, versionLabel);
			}

			throw _transform(amazonClientException);
		}
		catch (IOException ioException) {
			throw new SystemException(ioException);
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

		List<S3ObjectSummary> s3ObjectSummaries = _getS3ObjectSummaries(key);

		Iterator<S3ObjectSummary> iterator = s3ObjectSummaries.iterator();

		String[] fileNames = new String[s3ObjectSummaries.size()];

		for (int i = 0; i < fileNames.length; i++) {
			S3ObjectSummary s3ObjectSummary = iterator.next();

			fileNames[i] = S3KeyTransformerUtil.getFileName(
				s3ObjectSummary.getKey());
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

		GetObjectMetadataRequest getObjectMetadataRequest =
			new GetObjectMetadataRequest(_bucketName, key);

		ObjectMetadata objectMetadata = _amazonS3.getObjectMetadata(
			getObjectMetadataRequest);

		if (objectMetadata == null) {
			throw new NoSuchFileException(companyId, repositoryId, fileName);
		}

		return objectMetadata.getContentLength();
	}

	@Override
	public String[] getFileVersions(
		long companyId, long repositoryId, String fileName) {

		String key = S3KeyTransformerUtil.getFileKey(
			companyId, repositoryId, fileName);

		List<S3ObjectSummary> s3ObjectSummaries = _getS3ObjectSummaries(key);

		if (s3ObjectSummaries.isEmpty()) {
			return StringPool.EMPTY_ARRAY;
		}

		String[] versions = new String[s3ObjectSummaries.size()];

		for (int i = 0; i < s3ObjectSummaries.size(); i++) {
			S3ObjectSummary s3ObjectSummary = s3ObjectSummaries.get(i);

			String versionKey = s3ObjectSummary.getKey();

			versions[i] = versionKey.substring(
				versionKey.lastIndexOf(CharPool.SLASH) + 1);
		}

		Arrays.sort(versions, DLUtil::compareVersions);

		return versions;
	}

	public TransferManager getTransferManager() {
		return _transferManager;
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

			return _amazonS3.doesObjectExist(_bucketName, key);
		}
		catch (AmazonClientException amazonClientException) {
			if (_isFileNotFound(amazonClientException)) {
				return false;
			}

			throw _transform(amazonClientException);
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
			_storageClass = StorageClass.Standard;

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

		String key = S3KeyTransformerUtil.getFileKey(
			companyId, repositoryId, fileName);

		List<S3ObjectSummary> s3ObjectSummaries = _getS3ObjectSummaries(key);

		Iterator<S3ObjectSummary> iterator = s3ObjectSummaries.iterator();

		String[] keys = new String[s3ObjectSummaries.size()];

		for (int i = 0; i < keys.length; i++) {
			S3ObjectSummary s3ObjectSummary = iterator.next();

			keys[i] = s3ObjectSummary.getKey();
		}

		if (keys.length > 0) {
			Arrays.sort(keys);

			String headVersionKey = keys[keys.length - 1];

			int x = headVersionKey.lastIndexOf(CharPool.SLASH);

			return headVersionKey.substring(x + 1);
		}

		throw new NoSuchFileException(companyId, repositoryId, fileName);
	}

	private List<S3ObjectSummary> _getS3ObjectSummaries(String prefix) {
		try {
			ListObjectsRequest listObjectsRequest = new ListObjectsRequest();

			listObjectsRequest.withBucketName(_bucketName);
			listObjectsRequest.withPrefix(prefix);

			ObjectListing objectListing = _amazonS3.listObjects(
				listObjectsRequest);

			List<S3ObjectSummary> s3ObjectSummaries = new ArrayList<>(
				objectListing.getMaxKeys());

			while (true) {
				s3ObjectSummaries.addAll(objectListing.getObjectSummaries());

				if (objectListing.isTruncated()) {
					objectListing = _amazonS3.listNextBatchOfObjects(
						objectListing);
				}
				else {
					break;
				}
			}

			return s3ObjectSummaries;
		}
		catch (AmazonClientException amazonClientException) {
			throw _transform(amazonClientException);
		}
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

	private boolean _isFileNotFound(
		AmazonClientException amazonClientException) {

		if (amazonClientException instanceof AmazonServiceException) {
			AmazonServiceException amazonServiceException =
				(AmazonServiceException)amazonClientException;

			String errorCode = amazonServiceException.getErrorCode();

			if (errorCode.equals(_ERROR_CODE_FILE_NOT_FOUND) &&
				(amazonServiceException.getStatusCode() ==
					_STATUS_CODE_FILE_NOT_FOUND)) {

				return true;
			}
		}

		return false;
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

	private static final String _ERROR_CODE_FILE_NOT_FOUND = "NoSuchKey";

	private static final int _STATUS_CODE_FILE_NOT_FOUND = 404;

	private static final Log _log = LogFactoryUtil.getLog(S3Store.class);

	private static volatile S3StoreConfiguration _s3StoreConfiguration;

	private AmazonS3 _amazonS3;
	private String _bucketName;
	private S3AsyncClient _s3AsyncClient;
	private S3TransferManager _s3TransferManager;
	private StorageClass _storageClass;
	private TransferManager _transferManager;

}