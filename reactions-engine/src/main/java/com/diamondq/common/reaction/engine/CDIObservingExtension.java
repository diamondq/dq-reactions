package com.diamondq.common.reaction.engine;

import com.diamondq.common.reaction.api.ConfigureReaction;
import com.diamondq.common.reaction.engine.suppliers.CDISupplier;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessManagedBean;

public class CDIObservingExtension implements Extension {

	private final Set<MethodSupplierPair<?>>										mJobs			=
		Sets.newConcurrentHashSet();

	private final ConcurrentMap<BeanManager, Function<Class<?>, Supplier<?>>>	mCachedFuncMap	=
		Maps.newConcurrentMap();

	public static class MethodSupplierPair<C> {
		public final Class<C>							clazz;

		public final Method								method;

		public final Function<Class<C>, Supplier<C>>	supplier;

		public MethodSupplierPair(Class<C> pClazz, Method pMethod, Function<Class<C>, Supplier<C>> pSupplier) {
			super();
			clazz = pClazz;
			method = pMethod;
			supplier = pSupplier;
		}

	}

	/**
	 * Called by the CDI system whenever a new bean is registered. Used to keep the job directory up-to-date
	 * 
	 * @param pBean the bean
	 * @param pBeanManager the bean manager
	 */
	protected <X> void notifyOfBean(@Observes ProcessManagedBean<X> pBean, BeanManager pBeanManager) {

		@SuppressWarnings({"cast", "unchecked", "rawtypes"})
		Class<X> beanClass = (Class<X>) (Class) pBean.getBean().getBeanClass();

		/* Get the function that will return suppliers for the given bean manager */

		Function<Class<?>, Supplier<?>> function = mCachedFuncMap.get(pBeanManager);
		if (function == null) {
			Function<Class<X>, Supplier<X>> newFunction = new Function<Class<X>, Supplier<X>>() {

				@Override
				public Supplier<X> apply(Class<X> pT) {
					return new CDISupplier<X>(pBeanManager, pT);
				}
			};
			@SuppressWarnings({"cast", "unchecked", "rawtypes"})
			Function<Class<?>, Supplier<?>> castedNewFunction =
				(Function<Class<?>, Supplier<?>>) (Function) newFunction;
			if ((function = mCachedFuncMap.putIfAbsent(pBeanManager, castedNewFunction)) == null)
				function = castedNewFunction;
		}
		@SuppressWarnings({"cast", "unchecked", "rawtypes"})
		Function<Class<X>, Supplier<X>> castedF = (Function<Class<X>, Supplier<X>>) (Function) function;

		/* Now determine the jobs that are present in this class */

		@SuppressWarnings({"cast", "unchecked", "rawtypes"})
		Class<X> castedBeanClass = (Class<X>) (Class) beanClass;
		for (Method m : castedBeanClass.getMethods()) {
			if (m.isAnnotationPresent(ConfigureReaction.class) == false)
				continue;
			mJobs.add(new MethodSupplierPair<X>(castedBeanClass, m, castedF));
		}
	}

	public Set<MethodSupplierPair<?>> getJobs() {
		return mJobs;
	}

}
