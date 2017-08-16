package com.diamondq.reactions.network.arp;

import com.diamondq.common.lambda.future.ExtendedCompletableFuture;
import com.diamondq.reactions.api.ConfigureReaction;
import com.diamondq.reactions.api.JobContext;
import com.diamondq.reactions.api.info.AbstractNoParamsJobInfo;
import com.diamondq.reactions.common.process.ProcessLauncher;
import com.diamondq.reactions.common.process.ProcessLauncher.Result;

import java.util.Collections;
import java.util.Date;

import javax.enterprise.context.ApplicationScoped;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class NMapArpScan extends AbstractNoParamsJobInfo {
	private static final Logger					sLogger					= LoggerFactory.getLogger(NMapArpScan.class);

	@ConfigureReaction
	public void setup(JobContext pContext) {
		// @formatter:off
		pContext.registerJob(pContext.newJobBuilder()
			.method(this::execute).info(this)
			.param(ProcessLauncher.class).name("nmap").missingState("arpScan").build()
		.build());
		// @formatter:on	
	}

	public ExtendedCompletableFuture<@Nullable Void> execute(ProcessLauncher pNMapLauncher) {
		sLogger.info("Execute");
		

		/* Run the command */

		Date date = new Date();
		Result result = pNMapLauncher.launch(null, Collections.singletonList("-a"));
		if (result.code != 0) {
			sLogger.warn("Unable to get the arp data");
			throw new IllegalArgumentException("Unable to get the arp data");
		}

		String msg = new String(result.data);

		sLogger.debug("Result of ARP: {}", msg);

		return ExtendedCompletableFuture.completedFuture(null);
	}

}
