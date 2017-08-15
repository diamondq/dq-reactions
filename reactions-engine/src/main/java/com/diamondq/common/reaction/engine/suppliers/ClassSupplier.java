package com.diamondq.common.reaction.engine.suppliers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.Nullable;

public class ClassSupplier<T> implements Supplier<T> {

	private final Class<T>					mClass;

	private final @Nullable Constructor<T>	mConstructor;

	public ClassSupplier(Class<T> pClass) {
		mClass = pClass;
		@Nullable
		Constructor<T> constructor = null;
		try {
			constructor = mClass.getConstructor((Class<?>[]) null);
		}
		catch (NoSuchMethodException | SecurityException ex) {
		}
		mConstructor = constructor;
	}

	@Override
	public T get() {
		Constructor<T> constructor = mConstructor;
		if (constructor == null)
			throw new IllegalStateException();
		try {
			return constructor.newInstance((Object[]) null);
		}
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException
			| InvocationTargetException ex) {
			throw new RuntimeException(ex);
		}
	}

}
