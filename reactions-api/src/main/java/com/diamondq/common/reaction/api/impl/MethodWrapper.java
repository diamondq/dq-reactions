package com.diamondq.common.reaction.api.impl;

import com.diamondq.common.lambda.interfaces.Consumer1;
import com.diamondq.common.lambda.interfaces.Consumer2;
import com.diamondq.common.lambda.interfaces.Consumer3;
import com.diamondq.common.lambda.interfaces.Consumer4;
import com.diamondq.common.lambda.interfaces.Consumer5;
import com.diamondq.common.lambda.interfaces.Consumer6;
import com.diamondq.common.lambda.interfaces.Consumer7;
import com.diamondq.common.lambda.interfaces.Consumer8;
import com.diamondq.common.lambda.interfaces.Consumer9;
import com.diamondq.common.lambda.interfaces.Function0;
import com.diamondq.common.lambda.interfaces.Function1;
import com.diamondq.common.lambda.interfaces.Function2;
import com.diamondq.common.lambda.interfaces.Function3;
import com.diamondq.common.lambda.interfaces.Function4;
import com.diamondq.common.lambda.interfaces.Function5;
import com.diamondq.common.lambda.interfaces.Function6;
import com.diamondq.common.lambda.interfaces.Function7;
import com.diamondq.common.lambda.interfaces.Function8;
import com.diamondq.common.lambda.interfaces.Function9;

import java.lang.reflect.Method;

import org.checkerframework.checker.nullness.qual.Nullable;

public class MethodWrapper {

	public final @Nullable Consumer1<?>								mConsumer1;

	public final @Nullable Consumer2<?, ?>							mConsumer2;

	public final @Nullable Consumer3<?, ?, ?>						mConsumer3;

	public final @Nullable Consumer4<?, ?, ?, ?>					mConsumer4;

	public final @Nullable Consumer5<?, ?, ?, ?, ?>					mConsumer5;

	public final @Nullable Consumer6<?, ?, ?, ?, ?, ?>				mConsumer6;

	public final @Nullable Consumer7<?, ?, ?, ?, ?, ?, ?>			mConsumer7;

	public final @Nullable Consumer8<?, ?, ?, ?, ?, ?, ?, ?>		mConsumer8;

	public final @Nullable Consumer9<?, ?, ?, ?, ?, ?, ?, ?, ?>		mConsumer9;

	public final @Nullable Function0<?>								mFunction0;

	public final @Nullable Function1<?, ?>							mFunction1;

	public final @Nullable Function2<?, ?, ?>						mFunction2;

	public final @Nullable Function3<?, ?, ?, ?>					mFunction3;

	public final @Nullable Function4<?, ?, ?, ?, ?>					mFunction4;

	public final @Nullable Function5<?, ?, ?, ?, ?, ?>				mFunction5;

	public final @Nullable Function6<?, ?, ?, ?, ?, ?, ?>			mFunction6;

	public final @Nullable Function7<?, ?, ?, ?, ?, ?, ?, ?>		mFunction7;

	public final @Nullable Function8<?, ?, ?, ?, ?, ?, ?, ?, ?>		mFunction8;

	public final @Nullable Function9<?, ?, ?, ?, ?, ?, ?, ?, ?, ?>	mFunction9;

	private final Object											mCallback;

	private final Method											mMethod;

	private final Class<?>											mCallbackClass;

	private final boolean											mHasReturn;

	private static Method findMethod(Object pCallback) {
		Class<?> clazz = pCallback.getClass();
		Method[] methods = clazz.getMethods();
		for (Method m : methods) {
			if (("accept".equals(m.getName())) || ("apply".equals(m.getName()))) {
				return m;
			}
		}
		throw new IllegalArgumentException("No accept or apply method");
	}

	public <T> MethodWrapper(Consumer1<T> pConsumer) {
		mCallback = pConsumer;
		mMethod = findMethod(mCallback);
		mCallbackClass = Consumer1.class;
		mConsumer1 = pConsumer;
		mHasReturn = false;
		mConsumer2 = null;
		mConsumer3 = null;
		mConsumer4 = null;
		mConsumer5 = null;
		mConsumer6 = null;
		mConsumer7 = null;
		mConsumer8 = null;
		mConsumer9 = null;
		mFunction0 = null;
		mFunction1 = null;
		mFunction2 = null;
		mFunction3 = null;
		mFunction4 = null;
		mFunction5 = null;
		mFunction6 = null;
		mFunction7 = null;
		mFunction8 = null;
		mFunction9 = null;
	}

	public <T1, T2> MethodWrapper(Consumer2<T1, T2> pConsumer) {
		mCallback = pConsumer;
		mMethod = findMethod(mCallback);
		mCallbackClass = Consumer2.class;
		mHasReturn = false;
		mConsumer1 = null;
		mConsumer2 = pConsumer;
		mConsumer3 = null;
		mConsumer4 = null;
		mConsumer5 = null;
		mConsumer6 = null;
		mConsumer7 = null;
		mConsumer8 = null;
		mConsumer9 = null;
		mFunction0 = null;
		mFunction1 = null;
		mFunction2 = null;
		mFunction3 = null;
		mFunction4 = null;
		mFunction5 = null;
		mFunction6 = null;
		mFunction7 = null;
		mFunction8 = null;
		mFunction9 = null;
	}

	public <T1, T2, T3> MethodWrapper(Consumer3<T1, T2, T3> pConsumer) {
		mCallback = pConsumer;
		mMethod = findMethod(mCallback);
		mCallbackClass = Consumer3.class;
		mHasReturn = false;
		mConsumer1 = null;
		mConsumer2 = null;
		mConsumer3 = pConsumer;
		mConsumer4 = null;
		mConsumer5 = null;
		mConsumer6 = null;
		mConsumer7 = null;
		mConsumer8 = null;
		mConsumer9 = null;
		mFunction0 = null;
		mFunction1 = null;
		mFunction2 = null;
		mFunction3 = null;
		mFunction4 = null;
		mFunction5 = null;
		mFunction6 = null;
		mFunction7 = null;
		mFunction8 = null;
		mFunction9 = null;
	}

	public <T1, T2, T3, T4> MethodWrapper(Consumer4<T1, T2, T3, T4> pConsumer) {
		mCallback = pConsumer;
		mMethod = findMethod(mCallback);
		mCallbackClass = Consumer4.class;
		mHasReturn = false;
		mConsumer1 = null;
		mConsumer2 = null;
		mConsumer3 = null;
		mConsumer4 = pConsumer;
		mConsumer5 = null;
		mConsumer6 = null;
		mConsumer7 = null;
		mConsumer8 = null;
		mConsumer9 = null;
		mFunction0 = null;
		mFunction1 = null;
		mFunction2 = null;
		mFunction3 = null;
		mFunction4 = null;
		mFunction5 = null;
		mFunction6 = null;
		mFunction7 = null;
		mFunction8 = null;
		mFunction9 = null;
	}

	public <T1, T2, T3, T4, T5> MethodWrapper(Consumer5<T1, T2, T3, T4, T5> pConsumer) {
		mCallback = pConsumer;
		mMethod = findMethod(mCallback);
		mCallbackClass = Consumer5.class;
		mHasReturn = false;
		mConsumer1 = null;
		mConsumer2 = null;
		mConsumer3 = null;
		mConsumer4 = null;
		mConsumer5 = pConsumer;
		mConsumer6 = null;
		mConsumer7 = null;
		mConsumer8 = null;
		mConsumer9 = null;
		mFunction0 = null;
		mFunction1 = null;
		mFunction2 = null;
		mFunction3 = null;
		mFunction4 = null;
		mFunction5 = null;
		mFunction6 = null;
		mFunction7 = null;
		mFunction8 = null;
		mFunction9 = null;
	}

	public <T1, T2, T3, T4, T5, T6> MethodWrapper(Consumer6<T1, T2, T3, T4, T5, T6> pConsumer) {
		mCallback = pConsumer;
		mMethod = findMethod(mCallback);
		mCallbackClass = Consumer6.class;
		mHasReturn = false;
		mConsumer1 = null;
		mConsumer2 = null;
		mConsumer3 = null;
		mConsumer4 = null;
		mConsumer5 = null;
		mConsumer6 = pConsumer;
		mConsumer7 = null;
		mConsumer8 = null;
		mConsumer9 = null;
		mFunction0 = null;
		mFunction1 = null;
		mFunction2 = null;
		mFunction3 = null;
		mFunction4 = null;
		mFunction5 = null;
		mFunction6 = null;
		mFunction7 = null;
		mFunction8 = null;
		mFunction9 = null;
	}

	public <T1, T2, T3, T4, T5, T6, T7> MethodWrapper(Consumer7<T1, T2, T3, T4, T5, T6, T7> pConsumer) {
		mCallback = pConsumer;
		mMethod = findMethod(mCallback);
		mCallbackClass = Consumer7.class;
		mHasReturn = false;
		mConsumer1 = null;
		mConsumer2 = null;
		mConsumer3 = null;
		mConsumer4 = null;
		mConsumer5 = null;
		mConsumer6 = null;
		mConsumer7 = pConsumer;
		mConsumer8 = null;
		mConsumer9 = null;
		mFunction0 = null;
		mFunction1 = null;
		mFunction2 = null;
		mFunction3 = null;
		mFunction4 = null;
		mFunction5 = null;
		mFunction6 = null;
		mFunction7 = null;
		mFunction8 = null;
		mFunction9 = null;
	}

	public <T1, T2, T3, T4, T5, T6, T7, T8> MethodWrapper(Consumer8<T1, T2, T3, T4, T5, T6, T7, T8> pConsumer) {
		mCallback = pConsumer;
		mMethod = findMethod(mCallback);
		mCallbackClass = Consumer8.class;
		mHasReturn = false;
		mConsumer1 = null;
		mConsumer2 = null;
		mConsumer3 = null;
		mConsumer4 = null;
		mConsumer5 = null;
		mConsumer6 = null;
		mConsumer7 = null;
		mConsumer8 = pConsumer;
		mConsumer9 = null;
		mFunction0 = null;
		mFunction1 = null;
		mFunction2 = null;
		mFunction3 = null;
		mFunction4 = null;
		mFunction5 = null;
		mFunction6 = null;
		mFunction7 = null;
		mFunction8 = null;
		mFunction9 = null;
	}

	public <T1, T2, T3, T4, T5, T6, T7, T8, T9> MethodWrapper(Consumer9<T1, T2, T3, T4, T5, T6, T7, T8, T9> pConsumer) {
		mCallback = pConsumer;
		mMethod = findMethod(mCallback);
		mCallbackClass = Consumer9.class;
		mHasReturn = false;
		mConsumer1 = null;
		mConsumer2 = null;
		mConsumer3 = null;
		mConsumer4 = null;
		mConsumer5 = null;
		mConsumer6 = null;
		mConsumer7 = null;
		mConsumer8 = null;
		mConsumer9 = pConsumer;
		mFunction0 = null;
		mFunction1 = null;
		mFunction2 = null;
		mFunction3 = null;
		mFunction4 = null;
		mFunction5 = null;
		mFunction6 = null;
		mFunction7 = null;
		mFunction8 = null;
		mFunction9 = null;
	}

	public <R> MethodWrapper(Function0<R> pFunction) {
		mCallback = pFunction;
		mMethod = findMethod(mCallback);
		mCallbackClass = Function0.class;
		mHasReturn = true;
		mConsumer1 = null;
		mConsumer2 = null;
		mConsumer3 = null;
		mConsumer4 = null;
		mConsumer5 = null;
		mConsumer6 = null;
		mConsumer7 = null;
		mConsumer8 = null;
		mConsumer9 = null;
		mFunction0 = pFunction;
		mFunction1 = null;
		mFunction2 = null;
		mFunction3 = null;
		mFunction4 = null;
		mFunction5 = null;
		mFunction6 = null;
		mFunction7 = null;
		mFunction8 = null;
		mFunction9 = null;
	}

	public <T, R> MethodWrapper(Function1<T, R> pFunction) {
		mCallback = pFunction;
		mMethod = findMethod(mCallback);
		mCallbackClass = Function1.class;
		mHasReturn = true;
		mConsumer1 = null;
		mConsumer2 = null;
		mConsumer3 = null;
		mConsumer4 = null;
		mConsumer5 = null;
		mConsumer6 = null;
		mConsumer7 = null;
		mConsumer8 = null;
		mConsumer9 = null;
		mFunction0 = null;
		mFunction1 = pFunction;
		mFunction2 = null;
		mFunction3 = null;
		mFunction4 = null;
		mFunction5 = null;
		mFunction6 = null;
		mFunction7 = null;
		mFunction8 = null;
		mFunction9 = null;
	}

	public <T1, T2, R> MethodWrapper(Function2<T1, T2, R> pFunction) {
		mCallback = pFunction;
		mMethod = findMethod(mCallback);
		mCallbackClass = Function2.class;
		mHasReturn = true;
		mConsumer1 = null;
		mConsumer2 = null;
		mConsumer3 = null;
		mConsumer4 = null;
		mConsumer5 = null;
		mConsumer6 = null;
		mConsumer7 = null;
		mConsumer8 = null;
		mConsumer9 = null;
		mFunction0 = null;
		mFunction1 = null;
		mFunction2 = pFunction;
		mFunction3 = null;
		mFunction4 = null;
		mFunction5 = null;
		mFunction6 = null;
		mFunction7 = null;
		mFunction8 = null;
		mFunction9 = null;
	}

	public <T1, T2, T3, R> MethodWrapper(Function3<T1, T2, T3, R> pFunction) {
		mCallback = pFunction;
		mMethod = findMethod(mCallback);
		mCallbackClass = Function3.class;
		mHasReturn = true;
		mConsumer1 = null;
		mConsumer2 = null;
		mConsumer3 = null;
		mConsumer4 = null;
		mConsumer5 = null;
		mConsumer6 = null;
		mConsumer7 = null;
		mConsumer8 = null;
		mConsumer9 = null;
		mFunction0 = null;
		mFunction1 = null;
		mFunction2 = null;
		mFunction3 = pFunction;
		mFunction4 = null;
		mFunction5 = null;
		mFunction6 = null;
		mFunction7 = null;
		mFunction8 = null;
		mFunction9 = null;
	}

	public <T1, T2, T3, T4, R> MethodWrapper(Function4<T1, T2, T3, T4, R> pFunction) {
		mCallback = pFunction;
		mMethod = findMethod(mCallback);
		mCallbackClass = Function4.class;
		mHasReturn = true;
		mConsumer1 = null;
		mConsumer2 = null;
		mConsumer3 = null;
		mConsumer4 = null;
		mConsumer5 = null;
		mConsumer6 = null;
		mConsumer7 = null;
		mConsumer8 = null;
		mConsumer9 = null;
		mFunction0 = null;
		mFunction1 = null;
		mFunction2 = null;
		mFunction3 = null;
		mFunction4 = pFunction;
		mFunction5 = null;
		mFunction6 = null;
		mFunction7 = null;
		mFunction8 = null;
		mFunction9 = null;
	}

	public <T1, T2, T3, T4, T5, R> MethodWrapper(Function5<T1, T2, T3, T4, T5, R> pFunction) {
		mCallback = pFunction;
		mMethod = findMethod(mCallback);
		mCallbackClass = Function5.class;
		mHasReturn = true;
		mConsumer1 = null;
		mConsumer2 = null;
		mConsumer3 = null;
		mConsumer4 = null;
		mConsumer5 = null;
		mConsumer6 = null;
		mConsumer7 = null;
		mConsumer8 = null;
		mConsumer9 = null;
		mFunction0 = null;
		mFunction1 = null;
		mFunction2 = null;
		mFunction3 = null;
		mFunction4 = null;
		mFunction5 = pFunction;
		mFunction6 = null;
		mFunction7 = null;
		mFunction8 = null;
		mFunction9 = null;
	}

	public <T1, T2, T3, T4, T5, T6, R> MethodWrapper(Function6<T1, T2, T3, T4, T5, T6, R> pFunction) {
		mCallback = pFunction;
		mMethod = findMethod(mCallback);
		mCallbackClass = Function6.class;
		mHasReturn = true;
		mConsumer1 = null;
		mConsumer2 = null;
		mConsumer3 = null;
		mConsumer4 = null;
		mConsumer5 = null;
		mConsumer6 = null;
		mConsumer7 = null;
		mConsumer8 = null;
		mConsumer9 = null;
		mFunction0 = null;
		mFunction1 = null;
		mFunction2 = null;
		mFunction3 = null;
		mFunction4 = null;
		mFunction5 = null;
		mFunction6 = pFunction;
		mFunction7 = null;
		mFunction8 = null;
		mFunction9 = null;
	}

	public <T1, T2, T3, T4, T5, T6, T7, R> MethodWrapper(Function7<T1, T2, T3, T4, T5, T6, T7, R> pFunction) {
		mCallback = pFunction;
		mMethod = findMethod(mCallback);
		mCallbackClass = Function7.class;
		mHasReturn = true;
		mConsumer1 = null;
		mConsumer2 = null;
		mConsumer3 = null;
		mConsumer4 = null;
		mConsumer5 = null;
		mConsumer6 = null;
		mConsumer7 = null;
		mConsumer8 = null;
		mConsumer9 = null;
		mFunction0 = null;
		mFunction1 = null;
		mFunction2 = null;
		mFunction3 = null;
		mFunction4 = null;
		mFunction5 = null;
		mFunction6 = null;
		mFunction7 = pFunction;
		mFunction8 = null;
		mFunction9 = null;
	}

	public <T1, T2, T3, T4, T5, T6, T7, T8, R> MethodWrapper(Function8<T1, T2, T3, T4, T5, T6, T7, T8, R> pFunction) {
		mCallback = pFunction;
		mMethod = findMethod(mCallback);
		mCallbackClass = Function8.class;
		mHasReturn = true;
		mConsumer1 = null;
		mConsumer2 = null;
		mConsumer3 = null;
		mConsumer4 = null;
		mConsumer5 = null;
		mConsumer6 = null;
		mConsumer7 = null;
		mConsumer8 = null;
		mConsumer9 = null;
		mFunction0 = null;
		mFunction1 = null;
		mFunction2 = null;
		mFunction3 = null;
		mFunction4 = null;
		mFunction5 = null;
		mFunction6 = null;
		mFunction7 = null;
		mFunction8 = pFunction;
		mFunction9 = null;
	}

	public <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> MethodWrapper(
		Function9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R> pFunction) {
		mCallback = pFunction;
		mMethod = findMethod(mCallback);
		mCallbackClass = Function9.class;
		mHasReturn = true;
		mConsumer1 = null;
		mConsumer2 = null;
		mConsumer3 = null;
		mConsumer4 = null;
		mConsumer5 = null;
		mConsumer6 = null;
		mConsumer7 = null;
		mConsumer8 = null;
		mConsumer9 = null;
		mFunction0 = null;
		mFunction1 = null;
		mFunction2 = null;
		mFunction3 = null;
		mFunction4 = null;
		mFunction5 = null;
		mFunction6 = null;
		mFunction7 = null;
		mFunction8 = null;
		mFunction9 = pFunction;
	}

	public Object getCallback() {
		return mCallback;
	}

	public Method getMethod() {
		return mMethod;
	}

	public Class<?> getCallbackClass() {
		return mCallbackClass;
	}

	public boolean getHasReturn() {
		return mHasReturn;
	}
}
