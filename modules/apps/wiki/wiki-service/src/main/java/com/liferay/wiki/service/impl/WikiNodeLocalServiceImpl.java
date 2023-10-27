/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.service.impl;

import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.util.AssetHelper;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.util.DLValidatorUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.GroupThreadLocal;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.NotificationThreadLocal;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.ProgressTracker;
import com.liferay.portal.kernel.util.ProgressTrackerThreadLocal;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.xml.Attribute;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.DocumentException;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.kernel.zip.ZipReader;
import com.liferay.portal.kernel.zip.ZipReaderFactory;
import com.liferay.subscription.service.SubscriptionLocalService;
import com.liferay.trash.TrashHelper;
import com.liferay.trash.exception.RestoreEntryException;
import com.liferay.trash.exception.TrashEntryException;
import com.liferay.trash.model.TrashEntry;
import com.liferay.trash.service.TrashEntryLocalService;
import com.liferay.wiki.configuration.WikiGroupServiceConfiguration;
import com.liferay.wiki.constants.WikiConstants;
import com.liferay.wiki.constants.WikiPageConstants;
import com.liferay.wiki.constants.WikiWebKeys;
import com.liferay.wiki.exception.DuplicateNodeNameException;
import com.liferay.wiki.exception.ImportFilesException;
import com.liferay.wiki.exception.NoSuchPageException;
import com.liferay.wiki.exception.NodeNameException;
import com.liferay.wiki.internal.translator.MediaWikiToCreoleTranslator;
import com.liferay.wiki.internal.util.WikiCacheThreadLocal;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.model.WikiPageDisplay;
import com.liferay.wiki.service.WikiPageLocalService;
import com.liferay.wiki.service.base.WikiNodeLocalServiceBaseImpl;
import com.liferay.wiki.service.persistence.WikiPagePersistence;
import com.liferay.wiki.validator.WikiPageTitleValidator;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Provides the local service for accessing, adding, deleting, importing,
 * subscription handling of, trash handling of, and updating wiki nodes.
 *
 * @author Brian Wing Shun Chan
 * @author Charles May
 * @author Raymond Augé
 */
@Component(
	configurationPid = "com.liferay.wiki.configuration.WikiGroupServiceConfiguration",
	property = "model.class.name=com.liferay.wiki.model.WikiNode",
	service = AopService.class
)
public class WikiNodeLocalServiceImpl extends WikiNodeLocalServiceBaseImpl {

	@Override
	public WikiNode addDefaultNode(long userId, ServiceContext serviceContext)
		throws PortalException {

		return addNode(
			userId, _wikiGroupServiceConfiguration.initialNodeName(),
			StringPool.BLANK, serviceContext);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link #addNode(String,
	 *             long, String, String, ServiceContext)}
	 */
	@Deprecated
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public WikiNode addNode(
			long userId, String name, String description,
			ServiceContext serviceContext)
		throws PortalException {

		return addNode(null, userId, name, description, serviceContext);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public WikiNode addNode(
			String externalReferenceCode, long userId, String name,
			String description, ServiceContext serviceContext)
		throws PortalException {

		// Node

		User user = _userLocalService.getUser(userId);

		long groupId = serviceContext.getScopeGroupId();

		_validate(groupId, name);

		long nodeId = counterLocalService.increment();

		WikiNode node = wikiNodePersistence.create(nodeId);

		node.setUuid(serviceContext.getUuid());
		node.setExternalReferenceCode(externalReferenceCode);
		node.setGroupId(groupId);
		node.setCompanyId(user.getCompanyId());
		node.setUserId(user.getUserId());
		node.setUserName(user.getFullName());
		node.setName(name);
		node.setDescription(description);

		try {
			node = wikiNodePersistence.update(node);
		}
		catch (SystemException systemException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Add failed, fetch {groupId=", groupId, ", name=", name,
						"}"));
			}

			node = wikiNodePersistence.fetchByG_N(groupId, name, false);

			if (node == null) {
				throw systemException;
			}

			return node;
		}

		// Resources

		if (serviceContext.isAddGroupPermissions() ||
			serviceContext.isAddGuestPermissions()) {

			addNodeResources(
				node, serviceContext.isAddGroupPermissions(),
				serviceContext.isAddGuestPermissions());
		}
		else {
			addNodeResources(node, serviceContext.getModelPermissions());
		}

		return node;
	}

	@Override
	public void addNodeResources(
			long nodeId, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		WikiNode node = wikiNodePersistence.findByPrimaryKey(nodeId);

		addNodeResources(node, addGroupPermissions, addGuestPermissions);
	}

	@Override
	public void addNodeResources(
			WikiNode node, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		_resourceLocalService.addResources(
			node.getCompanyId(), node.getGroupId(), node.getUserId(),
			WikiNode.class.getName(), node.getNodeId(), false,
			addGroupPermissions, addGuestPermissions);
	}

	@Override
	public void addNodeResources(
			WikiNode node, ModelPermissions modelPermissions)
		throws PortalException {

		_resourceLocalService.addModelResources(
			node.getCompanyId(), node.getGroupId(), node.getUserId(),
			WikiNode.class.getName(), node.getNodeId(), modelPermissions);
	}

	@Override
	public void deleteNode(long nodeId) throws PortalException {
		WikiNode node = wikiNodePersistence.findByPrimaryKey(nodeId);

		wikiNodeLocalService.deleteNode(node);
	}

	@Override
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public void deleteNode(WikiNode node) throws PortalException {

		// Pages

		boolean clearCache = WikiCacheThreadLocal.isClearCache();

		try {
			WikiCacheThreadLocal.setClearCache(false);

			_wikiPageLocalService.deletePages(node.getNodeId());
		}
		finally {
			WikiCacheThreadLocal.setClearCache(clearCache);

			_portalCache.removeAll();
		}

		// Node

		wikiNodePersistence.remove(node);

		// Resources

		_resourceLocalService.deleteResource(
			node.getCompanyId(), WikiNode.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL, node.getNodeId());

		// Attachments

		long folderId = node.getAttachmentsFolderId();

		if (folderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			_portletFileRepository.deletePortletFolder(folderId);
		}

		// Subscriptions

		_subscriptionLocalService.deleteSubscriptions(
			node.getCompanyId(), WikiNode.class.getName(), node.getNodeId());

		// Indexer

		Indexer<WikiNode> indexer = _indexerRegistry.nullSafeGetIndexer(
			WikiNode.class);

		indexer.delete(node);

		if (node.isInTrash()) {
			node.setName(_trashHelper.getOriginalTitle(node.getName()));

			// Trash

			_trashEntryLocalService.deleteEntry(
				WikiNode.class.getName(), node.getNodeId());
		}
	}

	@Override
	public void deleteNodes(long groupId) throws PortalException {
		List<WikiNode> nodes = wikiNodePersistence.findByGroupId(groupId);

		for (WikiNode node : nodes) {
			wikiNodeLocalService.deleteNode(node);
		}

		_portletFileRepository.deletePortletRepository(
			groupId, WikiConstants.SERVICE_NAME);
	}

	@Override
	public WikiNode fetchNode(long groupId, String name) {
		return wikiNodePersistence.fetchByG_N(groupId, name);
	}

	@Override
	public WikiNode fetchNodeByUuidAndGroupId(String uuid, long groupId) {
		return wikiNodePersistence.fetchByUUID_G(uuid, groupId);
	}

	@Override
	public List<WikiNode> getCompanyNodes(long companyId, int start, int end) {
		return wikiNodePersistence.findByC_S(
			companyId, WorkflowConstants.STATUS_APPROVED, start, end);
	}

	@Override
	public List<WikiNode> getCompanyNodes(
		long companyId, int status, int start, int end) {

		return wikiNodePersistence.findByC_S(companyId, status, start, end);
	}

	@Override
	public int getCompanyNodesCount(long companyId) {
		return wikiNodePersistence.countByC_S(
			companyId, WorkflowConstants.STATUS_APPROVED);
	}

	@Override
	public int getCompanyNodesCount(long companyId, int status) {
		return wikiNodePersistence.countByC_S(companyId, status);
	}

	@Override
	public WikiNode getNode(long nodeId) throws PortalException {
		return wikiNodePersistence.findByPrimaryKey(nodeId);
	}

	@Override
	public WikiNode getNode(long groupId, String nodeName)
		throws PortalException {

		return wikiNodePersistence.findByG_N(groupId, nodeName);
	}

	@Override
	public List<WikiNode> getNodes(long groupId) throws PortalException {
		return getNodes(groupId, WorkflowConstants.STATUS_APPROVED);
	}

	@Override
	public List<WikiNode> getNodes(long groupId, int status)
		throws PortalException {

		List<WikiNode> nodes = wikiNodePersistence.findByG_S(groupId, status);

		if (nodes.isEmpty()) {
			nodes = _addDefaultNode(groupId);
		}

		return nodes;
	}

	@Override
	public List<WikiNode> getNodes(long groupId, int start, int end)
		throws PortalException {

		return getNodes(groupId, WorkflowConstants.STATUS_APPROVED, start, end);
	}

	@Override
	public List<WikiNode> getNodes(long groupId, int status, int start, int end)
		throws PortalException {

		List<WikiNode> nodes = wikiNodePersistence.findByG_S(
			groupId, status, start, end);

		if (nodes.isEmpty()) {
			nodes = _addDefaultNode(groupId);
		}

		return nodes;
	}

	@Override
	public int getNodesCount(long groupId) {
		return wikiNodePersistence.countByG_S(
			groupId, WorkflowConstants.STATUS_APPROVED);
	}

	@Override
	public int getNodesCount(long groupId, int status) {
		return wikiNodePersistence.countByG_S(groupId, status);
	}

	@Override
	public void importPages(
			long userId, long nodeId, InputStream[] inputStreams,
			Map<String, String[]> options)
		throws PortalException {

		WikiNode node = getNode(nodeId);

		boolean notificationsEnabled = NotificationThreadLocal.isEnabled();
		boolean clearCache = WikiCacheThreadLocal.isClearCache();

		try {
			NotificationThreadLocal.setEnabled(false);
			WikiCacheThreadLocal.setClearCache(false);

			_importPages(userId, node, inputStreams, options);
		}
		finally {
			NotificationThreadLocal.setEnabled(notificationsEnabled);
			WikiCacheThreadLocal.setClearCache(clearCache);

			_portalCache.removeAll();
		}
	}

	@Override
	public WikiNode moveNodeToTrash(long userId, long nodeId)
		throws PortalException {

		WikiNode node = wikiNodePersistence.findByPrimaryKey(nodeId);

		return moveNodeToTrash(userId, node);
	}

	@Override
	public WikiNode moveNodeToTrash(long userId, WikiNode node)
		throws PortalException {

		// Node

		if (node.isInTrash()) {
			throw new TrashEntryException();
		}

		int oldStatus = node.getStatus();

		node = updateStatus(
			userId, node, WorkflowConstants.STATUS_IN_TRASH,
			new ServiceContext());

		// Trash

		TrashEntry trashEntry = _trashEntryLocalService.addTrashEntry(
			userId, node.getGroupId(), WikiNode.class.getName(),
			node.getNodeId(), node.getUuid(), null, oldStatus, null,
			UnicodePropertiesBuilder.put(
				"title", node.getName()
			).build());

		node.setName(_trashHelper.getTrashTitle(trashEntry.getEntryId()));

		node = wikiNodePersistence.update(node);

		// Pages

		_moveDependentsToTrash(node.getNodeId(), trashEntry.getEntryId());

		return node;
	}

	@Override
	public void restoreNodeFromTrash(long userId, WikiNode node)
		throws PortalException {

		// Node

		if (!node.isInTrash()) {
			throw new RestoreEntryException(
				RestoreEntryException.INVALID_STATUS);
		}

		node.setName(_trashHelper.getOriginalTitle(node.getName()));

		node = wikiNodePersistence.update(node);

		TrashEntry trashEntry = _trashEntryLocalService.getEntry(
			WikiNode.class.getName(), node.getNodeId());

		updateStatus(
			userId, node, trashEntry.getStatus(), new ServiceContext());

		// Pages

		_restoreDependentsFromTrash(userId, node.getNodeId());

		// Trash

		_trashEntryLocalService.deleteEntry(trashEntry);
	}

	@Override
	public void subscribeNode(long userId, long nodeId) throws PortalException {
		WikiNode node = getNode(nodeId);

		_subscriptionLocalService.addSubscription(
			userId, node.getGroupId(), WikiNode.class.getName(), nodeId);
	}

	@Override
	public void unsubscribeNode(long userId, long nodeId)
		throws PortalException {

		_subscriptionLocalService.deleteSubscription(
			userId, WikiNode.class.getName(), nodeId);
	}

	@Override
	public WikiNode updateNode(
			long nodeId, String name, String description,
			ServiceContext serviceContext)
		throws PortalException {

		WikiNode node = wikiNodePersistence.findByPrimaryKey(nodeId);

		_validate(nodeId, node.getGroupId(), name);

		node.setName(name);
		node.setDescription(description);

		return wikiNodePersistence.update(node);
	}

	@Override
	public WikiNode updateStatus(
			long userId, WikiNode node, int status,
			ServiceContext serviceContext)
		throws PortalException {

		// Node

		User user = _userLocalService.getUser(userId);

		node.setStatus(status);
		node.setStatusByUserId(userId);
		node.setStatusByUserName(user.getFullName());
		node.setStatusDate(new Date());

		node = wikiNodePersistence.update(node);

		// Indexer

		Indexer<WikiNode> indexer = _indexerRegistry.nullSafeGetIndexer(
			WikiNode.class);

		indexer.reindex(node);

		return node;
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_wikiGroupServiceConfiguration = ConfigurableUtil.createConfigurable(
			WikiGroupServiceConfiguration.class, properties);

		_portalCache = _multiVMPool.getPortalCache(
			WikiPageDisplay.class.getName());
	}

	private List<WikiNode> _addDefaultNode(long groupId)
		throws PortalException {

		Group group = _groupLocalService.getGroup(groupId);

		long guestUserId = _userLocalService.getGuestUserId(
			group.getCompanyId());

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);
		serviceContext.setScopeGroupId(groupId);

		WikiNode node = wikiNodeLocalService.addDefaultNode(
			guestUserId, serviceContext);

		return ListUtil.fromArray(node);
	}

	private String _getCreoleRedirectContent(String redirectTitle) {
		return StringPool.DOUBLE_OPEN_BRACKET + redirectTitle +
			StringPool.DOUBLE_CLOSE_BRACKET;
	}

	private long _getUserId(
		long userId, WikiNode node, String author,
		Map<String, String> usersMap) {

		User user = null;

		String emailAddress = usersMap.get(author);

		if (Validator.isNotNull(emailAddress)) {
			user = _userLocalService.fetchUserByEmailAddress(
				node.getCompanyId(), emailAddress);
		}
		else {
			user = _userLocalService.fetchUserByScreenName(
				node.getCompanyId(), StringUtil.toLowerCase(author));
		}

		if (user != null) {
			return user.getUserId();
		}

		return userId;
	}

	private void _importPage(
			long userId, String author, WikiNode node, String title,
			String content, String summary, Map<String, String> usersMap,
			boolean strictImportMode)
		throws PortalException {

		try {
			long authorUserId = _getUserId(userId, node, author, usersMap);

			String parentTitle = _readParentTitle(content);

			String redirectTitle = _readRedirectTitle(content);

			if (Validator.isNotNull(redirectTitle)) {
				content = _getCreoleRedirectContent(redirectTitle);
			}
			else {
				content = _translateMediaWikiToCreole(
					content, strictImportMode);
				content = _translateMediaLinks(node, content);
			}

			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setAddGroupPermissions(true);
			serviceContext.setAddGuestPermissions(true);
			serviceContext.setAssetTagNames(
				_readAssetTagNames(userId, node, content));

			WikiPage page = null;

			try {
				page = _wikiPageLocalService.getPage(node.getNodeId(), title);
			}
			catch (NoSuchPageException noSuchPageException) {
				if (_log.isDebugEnabled()) {
					_log.debug(noSuchPageException);
				}

				page = _wikiPageLocalService.addPage(
					authorUserId, node.getNodeId(), title,
					WikiPageConstants.NEW, null, true, serviceContext);
			}

			_wikiPageLocalService.updatePage(
				authorUserId, node.getNodeId(), title, page.getVersion(),
				content, summary, true, "creole", parentTitle, redirectTitle,
				serviceContext);
		}
		catch (Exception exception) {
			throw new PortalException(
				"Error importing page " + title, exception);
		}
	}

	private void _importPages(
			long userId, WikiNode node, InputStream[] inputStreams,
			Map<String, String[]> options)
		throws PortalException {

		if ((inputStreams.length < 1) || (inputStreams[0] == null)) {
			throw new PortalException("The pages file is mandatory");
		}

		InputStream pagesInputStream = inputStreams[0];

		InputStream usersInputStream = null;

		if (inputStreams.length > 1) {
			usersInputStream = inputStreams[1];
		}

		InputStream imagesInputStream = null;

		if (inputStreams.length > 2) {
			imagesInputStream = inputStreams[2];
		}

		try {
			Document document = SAXReaderUtil.read(pagesInputStream);

			Map<String, String> usersMap = _readUsersFile(usersInputStream);

			Element rootElement = document.getRootElement();

			List<String> specialNamespaces = _readSpecialNamespaces(
				rootElement);

			_processImages(userId, node, imagesInputStream);

			_processSpecialPages(userId, node, rootElement, specialNamespaces);
			_processRegularPages(
				userId, node, rootElement, specialNamespaces, usersMap,
				imagesInputStream, options);

			_moveFrontPage(userId, node, options);
		}
		catch (DocumentException documentException) {
			throw new ImportFilesException(
				"Invalid XML file provided", documentException);
		}
		catch (IOException ioException) {
			throw new ImportFilesException(
				"Error reading the files provided", ioException);
		}
		catch (PortalException portalException) {
			throw portalException;
		}
		catch (Exception exception) {
			throw new PortalException(exception);
		}
	}

	private boolean _isSpecialMediaWikiPage(
		String title, List<String> specialNamespaces) {

		for (String namespace : specialNamespaces) {
			if (title.startsWith(namespace + StringPool.COLON)) {
				return true;
			}
		}

		return false;
	}

	private boolean _isValidImage(String[] paths, InputStream inputStream) {
		if (_specialMediaWikiDirs.contains(paths[0]) ||
			((paths.length > 1) && _specialMediaWikiDirs.contains(paths[1]))) {

			return false;
		}

		String fileName = paths[paths.length - 1];

		try {
			DLValidatorUtil.validateFileName(fileName);

			DLValidatorUtil.validateFileExtension(fileName);

			DLValidatorUtil.validateFileSize(
				GroupThreadLocal.getGroupId(), fileName,
				MimeTypesUtil.getContentType(fileName), inputStream);
		}
		catch (PortalException | SystemException exception) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return false;
		}

		return true;
	}

	private void _moveDependentsToTrash(long nodeId, long trashEntryId)
		throws PortalException {

		boolean clearCache = WikiCacheThreadLocal.isClearCache();

		try {
			WikiCacheThreadLocal.setClearCache(false);

			List<WikiPage> pages = _wikiPagePersistence.findByN_H(nodeId, true);

			for (WikiPage page : pages) {
				_wikiPageLocalService.moveDependentToTrash(page, trashEntryId);
			}
		}
		finally {
			WikiCacheThreadLocal.setClearCache(clearCache);

			_portalCache.removeAll();
		}
	}

	private void _moveFrontPage(
		long userId, WikiNode node, Map<String, String[]> options) {

		String frontPageTitle = MapUtil.getString(
			options, WikiWebKeys.OPTIONS_FRONT_PAGE);

		if (Validator.isNull(frontPageTitle)) {
			return;
		}

		frontPageTitle = _wikiPageTitleValidator.normalize(frontPageTitle);

		try {
			int count = _wikiPageLocalService.getPagesCount(
				node.getNodeId(), frontPageTitle, true);

			if (count > 0) {
				ServiceContext serviceContext = new ServiceContext();

				serviceContext.setAddGroupPermissions(true);
				serviceContext.setAddGuestPermissions(true);

				_wikiPageLocalService.renamePage(
					userId, node.getNodeId(), frontPageTitle,
					_wikiGroupServiceConfiguration.frontPageName(), false,
					serviceContext);
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Could not move ",
						_wikiGroupServiceConfiguration.frontPageName(),
						" to the title provided: ", frontPageTitle),
					exception);
			}
		}
	}

	private String _normalize(String categoryName, int length) {
		categoryName = _toWord(categoryName.trim());

		return StringUtil.shorten(categoryName, length);
	}

	private void _processImages(
			long userId, WikiNode node, InputStream imagesInputStream)
		throws Exception {

		if (imagesInputStream == null) {
			return;
		}

		ZipReader zipReader = _zipReaderFactory.getZipReader(imagesInputStream);

		List<String> entries = zipReader.getEntries();

		if (entries == null) {
			throw new ImportFilesException();
		}

		ProgressTracker progressTracker =
			ProgressTrackerThreadLocal.getProgressTracker();

		int count = 0;

		int total = entries.size();

		if (total > 0) {
			try {
				_wikiPageLocalService.getPage(
					node.getNodeId(), WikiPageConstants.SHARED_IMAGES_TITLE);
			}
			catch (NoSuchPageException noSuchPageException) {
				if (_log.isDebugEnabled()) {
					_log.debug(noSuchPageException);
				}

				ServiceContext serviceContext = new ServiceContext();

				serviceContext.setAddGroupPermissions(true);
				serviceContext.setAddGuestPermissions(true);

				_wikiPageLocalService.addPage(
					userId, node.getNodeId(),
					WikiPageConstants.SHARED_IMAGES_TITLE, "See attachments",
					null, true, serviceContext);
			}
		}

		List<ObjectValuePair<String, InputStream>> inputStreamOVPs =
			new ArrayList<>();

		try {
			int percentage = 50;

			for (int i = 0; i < entries.size(); i++) {
				String entry = entries.get(i);

				String key = entry;

				InputStream inputStream = zipReader.getEntryAsInputStream(
					entry);

				String[] paths = StringUtil.split(key, CharPool.SLASH);

				if (!_isValidImage(paths, inputStream)) {
					if (_log.isInfoEnabled()) {
						_log.info("Ignoring " + key);
					}

					continue;
				}

				String fileName = StringUtil.toLowerCase(
					paths[paths.length - 1]);

				ObjectValuePair<String, InputStream> inputStreamOVP =
					new ObjectValuePair<>(fileName, inputStream);

				inputStreamOVPs.add(inputStreamOVP);

				count++;

				if ((i % 5) == 0) {
					_wikiPageLocalService.addPageAttachments(
						userId, node.getNodeId(),
						WikiPageConstants.SHARED_IMAGES_TITLE, inputStreamOVPs);

					inputStreamOVPs.clear();

					if (progressTracker != null) {
						percentage = Math.min(50 + ((i * 50) / total), 99);

						progressTracker.setPercent(percentage);
					}
				}
			}

			if (!inputStreamOVPs.isEmpty()) {
				_wikiPageLocalService.addPageAttachments(
					userId, node.getNodeId(),
					WikiPageConstants.SHARED_IMAGES_TITLE, inputStreamOVPs);
			}
		}
		finally {
			for (ObjectValuePair<String, InputStream> inputStreamOVP :
					inputStreamOVPs) {

				try (InputStream inputStream = inputStreamOVP.getValue()) {
				}
				catch (IOException ioException) {
					if (_log.isWarnEnabled()) {
						_log.warn(ioException);
					}
				}
			}
		}

		zipReader.close();

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Imported ", count, " images into ", node.getName()));
		}
	}

	private void _processRegularPages(
		long userId, WikiNode node, Element rootElement,
		List<String> specialNamespaces, Map<String, String> usersMap,
		InputStream imagesInputStream, Map<String, String[]> options) {

		boolean importLatestVersion = MapUtil.getBoolean(
			options, WikiWebKeys.OPTIONS_IMPORT_LATEST_VERSION);
		boolean strictImportMode = MapUtil.getBoolean(
			options, WikiWebKeys.OPTIONS_STRICT_IMPORT_MODE);

		ProgressTracker progressTracker =
			ProgressTrackerThreadLocal.getProgressTracker();

		int count = 0;

		int percentage = 10;

		int maxPercentage = 50;

		if (imagesInputStream == null) {
			maxPercentage = 99;
		}

		List<Element> pageElements = rootElement.elements("page");

		for (int i = 0; i < pageElements.size(); i++) {
			Element pageElement = pageElements.get(i);

			String title = pageElement.elementText("title");

			if (_isSpecialMediaWikiPage(title, specialNamespaces)) {
				continue;
			}

			title = _wikiPageTitleValidator.normalize(title);

			percentage = Math.min(
				10 + ((i * (maxPercentage - percentage)) / pageElements.size()),
				maxPercentage);

			progressTracker.setPercent(percentage);

			List<Element> revisionElements = pageElement.elements("revision");

			if (importLatestVersion) {
				Element lastRevisionElement = revisionElements.get(
					revisionElements.size() - 1);

				revisionElements = new ArrayList<>();

				revisionElements.add(lastRevisionElement);
			}

			for (Element revisionElement : revisionElements) {
				Element contributorElement = revisionElement.element(
					"contributor");

				String author = contributorElement.elementText("username");

				String content = revisionElement.elementText("text");
				String summary = revisionElement.elementText("comment");

				try {
					_importPage(
						userId, author, node, title, content, summary, usersMap,
						strictImportMode);
				}
				catch (Exception exception) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Page with title " + title +
								" could not be imported",
							exception);
					}
				}
			}

			count++;
		}

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Imported ", count, " pages into ", node.getName()));
		}
	}

	private void _processSpecialPages(
			long userId, WikiNode node, Element rootElement,
			List<String> specialNamespaces)
		throws PortalException {

		ProgressTracker progressTracker =
			ProgressTrackerThreadLocal.getProgressTracker();

		List<Element> pageElements = rootElement.elements("page");

		for (int i = 0; i < pageElements.size(); i++) {
			Element pageElement = pageElements.get(i);

			String title = pageElement.elementText("title");

			if (!title.startsWith("Category:")) {
				if (_isSpecialMediaWikiPage(title, specialNamespaces)) {
					rootElement.remove(pageElement);
				}

				continue;
			}

			String categoryName = title.substring("Category:".length());

			categoryName = _normalize(categoryName, 75);

			_assetTagLocalService.checkTags(
				userId, node.getGroupId(), new String[] {categoryName});

			if ((i % 5) == 0) {
				progressTracker.setPercent((i * 10) / pageElements.size());
			}
		}
	}

	private String[] _readAssetTagNames(
			long userId, WikiNode node, String content)
		throws PortalException {

		Matcher matcher = _categoriesPattern.matcher(content);

		List<String> assetTagNames = new ArrayList<>();

		while (matcher.find()) {
			String categoryName = matcher.group(1);

			categoryName = _normalize(categoryName, 75);

			List<AssetTag> assetTags = _assetTagLocalService.checkTags(
				userId, node.getGroupId(), new String[] {categoryName});

			assetTagNames.addAll(
				ListUtil.toList(assetTags, AssetTag.NAME_ACCESSOR));
		}

		if (content.contains(_WORK_IN_PROGRESS)) {
			assetTagNames.add(_WORK_IN_PROGRESS_TAG);
		}

		return assetTagNames.toArray(new String[0]);
	}

	private String _readParentTitle(String content) {
		Matcher matcher = _parentPattern.matcher(content);

		String redirectTitle = StringPool.BLANK;

		if (matcher.find()) {
			redirectTitle = matcher.group(1);

			redirectTitle = _wikiPageTitleValidator.normalize(redirectTitle);

			redirectTitle += " (disambiguation)";
		}

		return redirectTitle;
	}

	private String _readRedirectTitle(String content) {
		Matcher matcher = _redirectPattern.matcher(content);

		String redirectTitle = StringPool.BLANK;

		if (matcher.find()) {
			redirectTitle = matcher.group(1);

			redirectTitle = _wikiPageTitleValidator.normalize(redirectTitle);
		}

		return redirectTitle;
	}

	private List<String> _readSpecialNamespaces(Element root)
		throws ImportFilesException {

		Element siteinfoElement = root.element("siteinfo");

		if (siteinfoElement == null) {
			throw new ImportFilesException("Invalid pages XML file");
		}

		List<String> namespaces = new ArrayList<>();

		Element namespacesElement = siteinfoElement.element("namespaces");

		List<Element> namespaceElements = namespacesElement.elements(
			"namespace");

		for (Element namespaceElement : namespaceElements) {
			Attribute attribute = namespaceElement.attribute("key");

			String value = attribute.getValue();

			if (!value.equals("0")) {
				namespaces.add(namespaceElement.getText());
			}
		}

		return namespaces;
	}

	private Map<String, String> _readUsersFile(InputStream usersInputStream)
		throws IOException {

		if (usersInputStream == null) {
			return Collections.emptyMap();
		}

		Map<String, String> usersMap = new HashMap<>();

		UnsyncBufferedReader unsyncBufferedReader = new UnsyncBufferedReader(
			new InputStreamReader(usersInputStream));

		String line = unsyncBufferedReader.readLine();

		while (line != null) {
			String[] array = StringUtil.split(line);

			if ((array.length == 2) && Validator.isNotNull(array[0]) &&
				Validator.isNotNull(array[1])) {

				usersMap.put(array[0], array[1]);
			}
			else {
				if (_log.isInfoEnabled()) {
					_log.info(
						"Ignoring line " + line +
							" because it does not contain exactly 2 columns");
				}
			}

			line = unsyncBufferedReader.readLine();
		}

		return usersMap;
	}

	private void _restoreDependentsFromTrash(long userId, long nodeId)
		throws PortalException {

		List<WikiPage> pages = _wikiPagePersistence.findByN_H(nodeId, true);

		for (WikiPage page : pages) {
			if (!_trashHelper.isInTrashImplicitly(page)) {
				continue;
			}

			_wikiPageLocalService.restorePageFromTrash(userId, page);
		}
	}

	private String _toWord(String text) {
		if (Validator.isNull(text)) {
			return text;
		}

		char[] textCharArray = text.toCharArray();

		for (int i = 0; i < textCharArray.length; i++) {
			char c = textCharArray[i];

			for (char invalidChar : AssetHelper.INVALID_CHARACTERS) {
				if (c == invalidChar) {
					textCharArray[i] = CharPool.SPACE;

					break;
				}
			}
		}

		return new String(textCharArray);
	}

	private String _translateMediaLinks(WikiNode node, String content) {
		try {
			StringBuffer sb = new StringBuffer();

			WikiPage sharedImagesPage = _wikiPageLocalService.getPage(
				node.getNodeId(), WikiPageConstants.SHARED_IMAGES_TITLE);

			Company company = _companyLocalService.getCompany(
				node.getCompanyId());

			String portalURL = company.getPortalURL(node.getGroupId());

			Matcher matcher = _mediaLinkPattern.matcher(content);

			while (matcher.find()) {
				String fileName = matcher.group(2);

				FileEntry fileEntry =
					_portletFileRepository.fetchPortletFileEntry(
						node.getGroupId(),
						sharedImagesPage.getAttachmentsFolderId(), fileName);

				if (fileEntry == null) {
					matcher.appendReplacement(sb, matcher.group());

					continue;
				}

				String fileEntryURL =
					_portletFileRepository.getPortletFileEntryURL(
						null, fileEntry, StringPool.BLANK);

				String linkLabel = matcher.group(3);

				if (linkLabel == null) {
					linkLabel = StringPool.PIPE + fileName;
				}

				matcher.appendReplacement(
					sb,
					StringBundler.concat(
						"[[", portalURL, fileEntryURL, linkLabel, "]]"));
			}

			matcher.appendTail(sb);

			return sb.toString();
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}

			return content;
		}
	}

	private String _translateMediaWikiToCreole(
		String content, boolean strictImportMode) {

		_translator.setStrictImportMode(strictImportMode);

		return _translator.translate(content);
	}

	private void _validate(long nodeId, long groupId, String name)
		throws PortalException {

		if (StringUtil.equalsIgnoreCase(name, "tag")) {
			throw new NodeNameException(name + " is reserved");
		}

		if (Validator.isNull(name)) {
			throw new NodeNameException();
		}

		if (Validator.isNumber(name)) {
			throw new NodeNameException("Node name cannot be a number");
		}

		WikiNode node = wikiNodePersistence.fetchByG_N(groupId, name);

		if ((node != null) && (node.getNodeId() != nodeId)) {
			throw new DuplicateNodeNameException("{nodeId=" + nodeId + "}");
		}
	}

	private void _validate(long groupId, String name) throws PortalException {
		_validate(0, groupId, name);
	}

	private static final String _WORK_IN_PROGRESS = "{{Work in progress}}";

	private static final String _WORK_IN_PROGRESS_TAG = "work in progress";

	private static final Log _log = LogFactoryUtil.getLog(
		WikiNodeLocalServiceImpl.class);

	private static final Pattern _categoriesPattern = Pattern.compile(
		"\\[\\[[Cc]ategory:([^\\]]*)\\]\\][\\n]*");
	private static final Pattern _mediaLinkPattern = Pattern.compile(
		"\\[\\[(Media:)([^\\]\\|]*)(\\|[^\\]]*)?\\]\\]", Pattern.DOTALL);
	private static final Pattern _parentPattern = Pattern.compile(
		"\\{{2}OtherTopics\\|([^\\}]*)\\}{2}");
	private static final Pattern _redirectPattern = Pattern.compile(
		"#REDIRECT \\[\\[([^\\]]*)\\]\\]");
	private static final Set<String> _specialMediaWikiDirs = SetUtil.fromArray(
		"archive", "temp", "thumb");

	@Reference
	private AssetTagLocalService _assetTagLocalService;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private IndexerRegistry _indexerRegistry;

	@Reference
	private MultiVMPool _multiVMPool;

	private PortalCache<?, ?> _portalCache;

	@Reference
	private PortletFileRepository _portletFileRepository;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private SubscriptionLocalService _subscriptionLocalService;

	private final MediaWikiToCreoleTranslator _translator =
		new MediaWikiToCreoleTranslator();

	@Reference
	private TrashEntryLocalService _trashEntryLocalService;

	@Reference
	private TrashHelper _trashHelper;

	@Reference
	private UserLocalService _userLocalService;

	private WikiGroupServiceConfiguration _wikiGroupServiceConfiguration;

	@Reference
	private WikiPageLocalService _wikiPageLocalService;

	@Reference
	private WikiPagePersistence _wikiPagePersistence;

	@Reference
	private WikiPageTitleValidator _wikiPageTitleValidator;

	@Reference
	private ZipReaderFactory _zipReaderFactory;

}