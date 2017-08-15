package com.diamondq.common.reaction.engine.tests;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class TestExecutors {

	@Produces
	@ApplicationScoped
	public ExecutorService getExecutor() {
		return Executors.newScheduledThreadPool(0);
	}
}
