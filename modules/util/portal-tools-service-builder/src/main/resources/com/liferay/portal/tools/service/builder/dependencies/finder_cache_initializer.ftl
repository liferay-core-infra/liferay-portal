package ${packagePath}.service.persistence.impl;

import ${apiPackagePath}.model.${entity.name};
import ${apiPackagePath}.service.persistence.${entity.name}Persistence;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.util.ServiceLatch;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import ${packagePath}.model.impl.${entity.name}ModelImpl;

/**
* @author ${author}
<#if classDeprecated>
	* @deprecated ${classDeprecatedComment}
</#if>
* @generated
*/

<#if dependencyInjectorDS>
	@Component(immediate = true, service = {${entity.name}FinderCacheInitializer.class})
</#if>
public class ${entity.name}FinderCacheInitializer {

	<#if dependencyInjectorDS>
		@Activate
		protected void activate() {
	<#else>
		public void afterPropertiesSet() {
	</#if>
			<#if !osgiModule>
				ServiceLatch serviceLatch = SystemBundleUtil.newServiceLatch();

				serviceLatch.waitFor(
					FinderCache.class,
					finderCache -> {
			</#if>
					try {
						TransactionInvokerUtil.invoke(
							TransactionConfig.Factory.create(
							Propagation.SUPPORTS, new Class<?>[] {Exception.class}),
							() -> {
								_initializeFinderCache(
									_${entity.variableName}Persistence,
									<#if !osgiModule>
										finderCache
									<#else>
										_finderCache
									</#if>
								);

								return null;
							});
					}
					catch (Throwable throwable) {
						_log.error("Unable to initialize finder cache", throwable);
					}
			<#if !osgiModule>
				});

				serviceLatch.openOn(() -> {});
			</#if>
		}

		private void _initializeFinderCache(${entity.name}Persistence ${entity.variableName}Persistence, FinderCache finderCache) {
			List<${entity.name}> ${entity.variableName}s = ${entity.variableName}Persistence.findAll();

			${entity.name}PersistenceImpl ${entity.variableName}PersistenceImpl = (${entity.name}PersistenceImpl)${entity.variableName}Persistence;

			Map<String, FinderPath> eagerCacheFinderPaths = ${entity.variableName}PersistenceImpl.getEagerCacheFinderPaths();

			<#list entity.eagerCacheEntityFinders as eagerCacheEntityFinder>
				<#if eagerCacheEntityFinder.isCollection()>
					Map<List<Object>, List<${entity.name}>> _findBy${eagerCacheEntityFinder.name}ResultMap = new HashMap<>();
				</#if>
			</#list>

				for (${entity.name} ${entity.variableName} : ${entity.variableName}s) {
					${entity.name}ModelImpl ${entity.variableName}ModelImpl = (${entity.name}ModelImpl)${entity.variableName};

					<#list entity.eagerCacheEntityFinders as eagerCacheEntityFinder>
						<#if eagerCacheEntityFinder.isUnique()>
							finderCache.putResult(
								eagerCacheFinderPaths.get("${eagerCacheEntityFinder.name}"),
								new Object[] {
								<#list eagerCacheEntityFinder.entityColumns as entityColumn>
									<#if stringUtil.equals(entityColumn.type, "boolean")>
										${entity.variableName}ModelImpl.is${entityColumn.methodName}()
									<#elseif stringUtil.equals(entityColumn.type, "Date")>
										_getTime(${entity.variableName}ModelImpl.get${entityColumn.methodName}())
									<#else>
										${entity.variableName}ModelImpl.get${entityColumn.methodName}()
									</#if>

									<#if entityColumn_has_next>
										,
									</#if>
								</#list>
								},
								${entity.variableName}ModelImpl);

						<#elseif eagerCacheEntityFinder.isCollection()>
							List<${entity.name}> _findBy${eagerCacheEntityFinder.name}ResultList = _findBy${eagerCacheEntityFinder.name}ResultMap.computeIfAbsent(
								new ArrayList() {
								{
									<#list eagerCacheEntityFinder.entityColumns as entityColumn>
										add(
										<#if stringUtil.equals(entityColumn.type, "boolean")>
											${entity.variableName}ModelImpl.is${entityColumn.methodName}()
										<#elseif stringUtil.equals(entityColumn.type, "Date")>
											_getTime(${entity.variableName}ModelImpl.get${entityColumn.methodName}())
										<#else>
											${entity.variableName}ModelImpl.get${entityColumn.methodName}()
										</#if>
										);
									</#list>
									}
								},
								(key) -> new ArrayList<>());

								_findBy${eagerCacheEntityFinder.name}ResultList.add(${entity.variableName});

						</#if>
					</#list>
					}

					<#list entity.eagerCacheEntityFinders as eagerCacheEntityFinder>
						<#if eagerCacheEntityFinder.isCollection()>
							for (Map.Entry<List<Object>, List<${entity.name}>> entry : _findBy${eagerCacheEntityFinder.name}ResultMap.entrySet()) {
								List<Object> key = entry.getKey();

								finderCache.putResult(eagerCacheFinderPaths.get("${eagerCacheEntityFinder.name}"), key.toArray(), entry.getValue());
							}
						</#if>
					</#list>
			}

	private static final Log _log = LogFactoryUtil.getLog(${entity.name}FinderCacheInitializer.class);

	<#if dependencyInjectorDS>
		@Reference
		private FinderCache _finderCache;
	<#elseif osgiModule>
		@ServiceReference(type = FinderCache.class)
		private FinderCache _finderCache;
	</#if>

	<#if dependencyInjectorDS>
		@Reference
	<#else>
		@BeanReference(type = ${entity.name}Persistence.class)
	</#if>
	private ${entity.name}Persistence _${entity.variableName}Persistence;

}