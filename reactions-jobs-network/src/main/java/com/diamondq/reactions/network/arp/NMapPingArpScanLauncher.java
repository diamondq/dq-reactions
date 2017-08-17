package com.diamondq.reactions.network.arp;

import com.diamondq.reactions.api.ConfigureReaction;
import com.diamondq.reactions.api.JobContext;
import com.diamondq.reactions.api.info.AbstractNoParamsJobInfo;
import com.diamondq.reactions.common.process.ProcessLauncher;
import static com.diamondq.reactions.common.process.ProcessLauncher.*;

import javax.enterprise.context.ApplicationScoped;

/**
 * This job is designed to return a ProcessLauncher that will execute a Arp Scan on a given subnet using NMap
 */
@ApplicationScoped
public class NMapPingArpScanLauncher extends AbstractNoParamsJobInfo {

	@ConfigureReaction
	public void setup(JobContext pContext) {
		// @formatter:off
		pContext.registerJob(pContext.newJobBuilder()
			.method(this::getLauncher).info(this)
			.result(ProcessLauncher.class).isTransient().name("nmap").state("pingArpScan").build()
			.param(ProcessLauncher.class).isStored().name("nmap").missingState("pingArpScan").build()
		.build());
		// @formatter:on	
	}

	/**
	 * Generates a new ProcessLauncher that issues an arp scan on the TARGET destination <br/>
	 * <br/>
	 * -sn host scan only<br/>
	 * -PR ARP Ping probes<br/>
	 * -n no DNS<br/>
	 * -oX - XML output to stdout<br/>
	 * 
	 * @param pNMapLauncher the existing basic nmap launcher
	 * @return the new Launcher that returns all existing records.
	 */
	public ProcessLauncher getLauncher(ProcessLauncher pNMapLauncher) {
		return pNMapLauncher.extend(null, constant("-sn"), constant("-PR"), constant("-PP"), constant("-PE"),
			constant("-n"), constant("-oX"), constant("-"), variable("TARGET"));
	}
}
