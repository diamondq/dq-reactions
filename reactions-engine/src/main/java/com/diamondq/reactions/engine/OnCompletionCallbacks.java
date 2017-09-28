package com.diamondq.reactions.engine;

import com.diamondq.common.lambda.future.ExtendedCompletableFuture;
import com.google.common.collect.Lists;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiFunction;

import org.checkerframework.checker.nullness.qual.Nullable;

public class OnCompletionCallbacks<A> implements BiFunction<A, @Nullable Throwable, A> {

	private final CopyOnWriteArrayList<ExtendedCompletableFuture<Boolean>>	mCallOnComplete;

	private final EngineImpl												mEngine;

	private final String													mIdentifier;

	public OnCompletionCallbacks(EngineImpl pEngine, String pIdentifier) {
		mEngine = pEngine;
		mIdentifier = pIdentifier;
		mCallOnComplete = Lists.newCopyOnWriteArrayList();
	}

	/**
	 * @see java.util.function.BiFunction#apply(java.lang.Object, java.lang.Object)
	 */
	@Override
	public A apply(A pValue, @Nullable Throwable pThrowable) {

		mEngine.processCallbacks(mIdentifier, mCallOnComplete);

		if (pThrowable != null) {
			if (pThrowable instanceof RuntimeException)
				throw (RuntimeException) pThrowable;
			throw new RuntimeException(pThrowable);
		}
		return pValue;
	}

	public void addCallback(ExtendedCompletableFuture<Boolean> pCallback) {
		mCallOnComplete.add(pCallback);
	}
}
