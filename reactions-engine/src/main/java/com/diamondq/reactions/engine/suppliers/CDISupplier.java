package com.diamondq.reactions.engine.suppliers;

import java.lang.annotation.Annotation;
import java.util.function.Supplier;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class CDISupplier<T> implements Supplier<T> {

	private @Nullable Instance<@Nullable T>	mInstance;

	private final CDI<Object>				mCDI;

	private final Class<@Nullable T>		mClass;

	@SuppressWarnings("null")
	public CDISupplier(CDI<Object> pCDI, Class<T> pClass) {
		mCDI = pCDI;
		mClass = (Class<@Nullable T>) pClass;
	}

	/**
	 * @see java.util.function.Supplier#get()
	 */
	@SuppressWarnings("null")
	@Override
	public T get() {
		@Nullable
		Instance<@Nullable T> i = mInstance;
		if (i == null) {
			Instance<@NonNull T> a = mCDI.select(mClass, (Annotation[]) null);
			i = a;
			mInstance = i;
		}

		return i.get();
	}

}
