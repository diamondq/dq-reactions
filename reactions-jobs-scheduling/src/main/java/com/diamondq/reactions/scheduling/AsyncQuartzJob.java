package com.diamondq.reactions.scheduling;

import com.diamondq.reactions.api.JobInfo;
import com.diamondq.reactions.api.JobParamsBuilder;
import com.diamondq.reactions.api.ReactionsEngine;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DisallowConcurrentExecution
public class AsyncQuartzJob implements Job {

	private static final Logger sLogger = LoggerFactory.getLogger(AsyncQuartzJob.class);

	/**
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override
	public void execute(JobExecutionContext pContext) throws JobExecutionException {

		sLogger.debug("Executing job");

		try {
			ReactionsEngine engine = (ReactionsEngine) pContext.getMergedJobDataMap().get("engine");
			if (engine == null)
				throw new IllegalStateException("The engine job parameter must not be null");
			String jobClassStr = (String) pContext.getMergedJobDataMap().get("jobClass");
			if (jobClassStr == null)
				throw new IllegalStateException("The jobClass job parameter must not be null");

			@SuppressWarnings("unchecked")
			Map<String, Object> params = (Map<String, Object>) pContext.getMergedJobDataMap().get("params");

			try {
				@SuppressWarnings("unchecked")
				Class<? extends JobInfo<?, ? extends JobParamsBuilder>> jobClass =
					(Class<? extends JobInfo<?, ? extends JobParamsBuilder>>) Class.forName(jobClassStr);
				JobInfo<?, ? extends JobParamsBuilder> jobInfo = engine.findMandatoryJob(jobClass);
				JobParamsBuilder builder = null;
				if ((params != null) && (params.isEmpty() == false)) {
					builder = jobInfo.newParamsBuilder();
					for (Map.Entry<String, Object> pair : params.entrySet()) {
						builder.setParam(pair.getKey(), pair.getValue());
					}
				}
				submit(engine, jobInfo, builder);
			}
			catch (ClassNotFoundException ex) {
				JobExecutionException jee = new JobExecutionException(ex);
				jee.setUnscheduleAllTriggers(true);
				throw jee;
			}
		}
		catch (RuntimeException ex) {
			sLogger.error("Error during job, but continuing", ex);
		}
	}

	private <RESULT, JPB extends JobParamsBuilder, T extends JobInfo<RESULT, JPB>> void submit(ReactionsEngine pEngine,
		T pInfo, @Nullable JobParamsBuilder pBuilder) {
		try {
			@SuppressWarnings("unchecked")
			JPB builder = (pBuilder == null ? pInfo.newParamsBuilder() : (JPB) pBuilder);
			pEngine.submit(pInfo, builder).get();
		}
		catch (InterruptedException ex) {
			throw new RuntimeException(ex);
		}
		catch (ExecutionException ex) {
			Throwable cause = ex.getCause();
			if (cause instanceof RuntimeException)
				throw (RuntimeException) cause;
			throw new RuntimeException(cause);
		}
	}

}
