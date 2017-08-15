package com.diamondq.common.reaction.engine.suppliers;

import java.util.function.Supplier;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;

import org.checkerframework.checker.nullness.qual.Nullable;

public class CDISupplier<T> implements Supplier<T> {

	private @Nullable Instance<@Nullable T>	mInstance;

	private final BeanManager				mBeanManager;

	private final Class<@Nullable T>		mClass;

	@SuppressWarnings("null")
	public CDISupplier(BeanManager pBeanManager, Class<T> pClass) {
		mBeanManager = pBeanManager;
		mClass = (Class<@Nullable T>) pClass;
	}

	/**
	 * @see java.util.function.Supplier#get()
	 */
	@Override
	public T get() {
		@Nullable
		Instance<@Nullable T> i = mInstance;
		if (i == null) {
			Instance<@Nullable Object> allInstances = mBeanManager.createInstance();
			Instance<@Nullable T> a = allInstances.select(mClass);
			i = a;
			mInstance = i;
		}

		return i.get();
	}

}
