package com.diamondq.reactions.scheduling;

import com.diamondq.reactions.api.ReactionsEngine;
import com.diamondq.reactions.api.ReactionsEngineInitializedEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.DirectSchedulerFactory;
import org.quartz.simpl.RAMJobStore;
import org.quartz.simpl.SimpleThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class SchedulerProvider {

	private static final Logger				sLogger				= LoggerFactory.getLogger(SchedulerProvider.class);

	private @Nullable Scheduler				mScheduler;

	private volatile boolean				mEngineInitialized	= false;

	private Event<SchedulerStartedEvent>	mStartedEvent;

	@Inject
	public SchedulerProvider(Event<SchedulerStartedEvent> pStartedEvent) {
		mStartedEvent = pStartedEvent;
	}

	@Produces
	@Singleton
	public Scheduler createScheduler(ReactionsEngine pEngine) throws SchedulerException {
		synchronized (this) {
			if (mScheduler != null)
				return mScheduler;
			DirectSchedulerFactory factory = DirectSchedulerFactory.getInstance();
			SimpleThreadPool threadPool = new SimpleThreadPool(5, Thread.NORM_PRIORITY);
			threadPool.setMakeThreadsDaemons(true);
			threadPool.setInstanceName("Scheduler");
			factory.createScheduler(threadPool, new RAMJobStore());
			Scheduler sched = factory.getScheduler();
			if (sched == null)
				throw new IllegalStateException("No scheduler found");
			mScheduler = sched;
			if (mEngineInitialized == true) {
				sLogger.debug("Reaction ReactionsEngine is already initialized, so starting scheduler");
				sched.start();
				mStartedEvent.fire(new SchedulerStartedEvent());
			}
			else
				sLogger.debug("Reaction ReactionsEngine is not initialized, so skipping scheduler start");
			return sched;
		}
	}

	public void init(@Observes ReactionsEngineInitializedEvent pEvent) {
		synchronized (this) {
			try {
				mEngineInitialized = true;
				Scheduler sched = mScheduler;
				if (sched != null) {
					sLogger.debug("Received Reaction ReactionsEngine Initialization Event, so starting scheduler");
					sched.start();
					mStartedEvent.fire(new SchedulerStartedEvent());
				}
				else
					sLogger.debug("Received Reaction ReactionsEngine Initialization Event, but no schedular yet");
			}
			catch (SchedulerException ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	public void cleanupScheduler(@Disposes Scheduler pScheduler) throws SchedulerException {
		mScheduler = null;
		pScheduler.shutdown(true);
	}
}
