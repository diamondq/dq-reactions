package com.diamondq.common.reaction.engine.tests.simple_trigger;

import com.diamondq.common.lambda.future.ExtendedCompletableFuture;
import com.diamondq.common.reaction.api.Action;
import com.diamondq.common.reaction.api.Engine;

import java.util.Collections;
import java.util.concurrent.ExecutionException;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.junit4.WeldInitiator;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestSimpleAddToCollection {

	private static Logger	sLogger	= LoggerFactory.getLogger(TestSimpleAddToCollection.class);

	@Rule
	public WeldInitiator	weld	= WeldInitiator.of(new Weld());

	@Test
	public void submit() throws InterruptedException, ExecutionException {
		sLogger.warn("Here");
		Engine engine = weld.select(Engine.class).get();
		ExtendedCompletableFuture<@Nullable Void> result = engine.addToCollection(new Record("test"), Action.INSERT,
			"testName", Collections.singletonMap("state1Key", "state1Value"));
		result.get();
	}

}
