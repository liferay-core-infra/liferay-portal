/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.test.rule;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.UserBag;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.MethodTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.junit.runner.Description;

/**
 * @author Tom Wang
 */
public class PermissionCheckerMethodTestRule extends MethodTestRule<Void> {

	public static final PermissionCheckerMethodTestRule INSTANCE =
		new PermissionCheckerMethodTestRule();

	@Override
	public void afterMethod(Description description, Void c, Object target)
		throws Throwable {

		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);

		PrincipalThreadLocal.setName(_originalName);
	}

	@Override
	public Void beforeMethod(Description description, Object target)
		throws Exception {

		setUpPermissionThreadLocal();
		setUpPrincipalThreadLocal();

		return null;
	}

	protected void setUpPermissionThreadLocal() throws Exception {
		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionChecker permissionChecker = new TestPermissionChecker();

		permissionChecker.init(TestPropsValues.getUser());

		PermissionThreadLocal.setPermissionChecker(
			(PermissionChecker)ProxyUtil.newProxyInstance(
				PermissionChecker.class.getClassLoader(),
				new Class<?>[] {PermissionChecker.class},
				(proxy, method, args) -> {
					if (Objects.equals(
							method.getName(), "hasOwnerPermission")) {

						return true;
					}

					return method.invoke(permissionChecker, args);
				}));
	}

	protected void setUpPrincipalThreadLocal() throws Exception {
		_originalName = PrincipalThreadLocal.getName();

		PrincipalThreadLocal.setName(TestPropsValues.getUserId());
	}

	private PermissionCheckerMethodTestRule() {
	}

	private String _originalName;
	private PermissionChecker _originalPermissionChecker;

	private static class TestPermissionChecker implements PermissionChecker {

		@Override
		public TestPermissionChecker clone() {
			return new TestPermissionChecker();
		}

		@Override
		public long getCompanyId() {
			return user.getCompanyId();
		}

		@Override
		public long[] getGuestUserRoleIds() {
			return PermissionChecker.DEFAULT_ROLE_IDS;
		}

		@Override
		public long getOwnerRoleId() {
			return ownerRole.getRoleId();
		}

		@Override
		public Map<Object, Object> getPermissionChecksMap() {
			return _permissionChecksMap;
		}

		@Override
		public long[] getRoleIds(long userId, long groupId) {
			return PermissionChecker.DEFAULT_ROLE_IDS;
		}

		@Override
		public User getUser() {
			return user;
		}

		@Override
		public UserBag getUserBag() {
			return null;
		}

		@Override
		public long getUserId() {
			return user.getUserId();
		}

		@Override
		public boolean hasOwnerPermission(
			long companyId, String name, long primKey, long ownerId,
			String actionId) {

			return hasOwnerPermission(
				companyId, name, String.valueOf(primKey), ownerId, actionId);
		}

		@Override
		public boolean hasOwnerPermission(
			long companyId, String name, String primKey, long ownerId,
			String actionId) {

			return hasPermission(actionId);
		}

		@Override
		public boolean hasPermission(
			Group group, String name, long primKey, String actionId) {

			return hasPermission(
				group, name, String.valueOf(primKey), actionId);
		}

		@Override
		public boolean hasPermission(
			Group group, String name, String primKey, String actionId) {

			return hasPermission(actionId);
		}

		@Override
		public boolean hasPermission(
			long groupId, String name, long primKey, String actionId) {

			return hasPermission(
				GroupLocalServiceUtil.fetchGroup(groupId), name,
				String.valueOf(primKey), actionId);
		}

		@Override
		public boolean hasPermission(
			long groupId, String name, String primKey, String actionId) {

			return hasPermission(
				GroupLocalServiceUtil.fetchGroup(groupId), name, primKey,
				actionId);
		}

		@Override
		public void init(User user) {
			this.user = user;

			if (user.isGuestUser()) {
				guestUserId = user.getUserId();
				signedIn = false;
			}
			else {
				try {
					guestUserId = UserLocalServiceUtil.getGuestUserId(
						user.getCompanyId());
				}
				catch (Exception exception) {
					_log.error(exception);
				}

				signedIn = true;
			}

			try {
				ownerRole = RoleLocalServiceUtil.getRole(
					user.getCompanyId(), RoleConstants.OWNER);
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}

		@Override
		public boolean isCheckGuest() {
			return checkGuest;
		}

		@Override
		public boolean isCompanyAdmin() {
			return signedIn;
		}

		@Override
		public boolean isCompanyAdmin(long companyId) {
			return signedIn;
		}

		@Override
		public boolean isContentReviewer(long companyId, long groupId) {
			return signedIn;
		}

		@Override
		public boolean isGroupAdmin(long groupId) {
			return signedIn;
		}

		@Override
		public boolean isGroupMember(long groupId) {
			return signedIn;
		}

		@Override
		public boolean isGroupOwner(long groupId) {
			return signedIn;
		}

		@Override
		public boolean isOmniadmin() {
			if (omniadmin == null) {
				ClassLoader portalClassLoader =
					PortalClassLoaderUtil.getClassLoader();

				try {
					omniadmin = Boolean.valueOf(
						ReflectionTestUtil.invoke(
							portalClassLoader.loadClass(
								"com.liferay.portlet.admin.util.OmniadminUtil"),
							"isOmniadmin", new Class<?>[] {User.class},
							getUser()));
				}
				catch (Exception exception) {
					throw new RuntimeException(exception);
				}
			}

			return omniadmin.booleanValue();
		}

		@Override
		public boolean isOrganizationAdmin(long organizationId) {
			return signedIn;
		}

		@Override
		public boolean isOrganizationOwner(long organizationId) {
			return signedIn;
		}

		@Override
		public boolean isSignedIn() {
			return signedIn;
		}

		protected boolean hasPermission(String actionId) {
			if (signedIn || actionId.equals(ActionKeys.VIEW)) {
				return true;
			}

			return false;
		}

		protected boolean checkGuest = GetterUtil.getBoolean(
			PropsUtil.get(PropsKeys.PERMISSIONS_CHECK_GUEST_ENABLED));
		protected long guestUserId;
		protected Boolean omniadmin;
		protected Role ownerRole;
		protected boolean signedIn;
		protected User user;

		private static final Log _log = LogFactoryUtil.getLog(
			TestPermissionChecker.class);

		private final Map<Object, Object> _permissionChecksMap =
			new HashMap<>();

	}

}