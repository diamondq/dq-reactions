package com.diamondq.reactions.network.arp;

import com.diamondq.reactions.api.ConfigureReaction;
import com.diamondq.reactions.api.JobContext;
import com.diamondq.reactions.api.info.AbstractNoParamsJobInfo;
import com.diamondq.reactions.common.process.ProcessLauncher;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ArpLauncherWindows extends AbstractNoParamsJobInfo {

	@ConfigureReaction
	public void setup(JobContext pContext) {
		// @formatter:off
		pContext.registerJob(pContext.newJobBuilder()
			.method(this::getLauncher).info(this)
			.result(ProcessLauncher.class).isTransient().name("arp").state("arpAll").build()
			.param(ProcessLauncher.class).isStored().name("arp").missingState("arpAll").build()
		.build());
		// @formatter:on	
	}

	/**
	 * Generates a new ProcessLauncher that returns all the Windows ARP table records
	 * 
	 * @param pArpLauncher the existing basic arp launcher
	 * @return the new Launcher that returns all existing records.
	 */
	public ProcessLauncher getLauncher(ProcessLauncher pArpLauncher) {
		return pArpLauncher.extend(null, ProcessLauncher.constant("-a"));
	}
}
