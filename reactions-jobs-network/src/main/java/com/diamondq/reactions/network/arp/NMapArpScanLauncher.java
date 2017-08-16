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
public class NMapArpScanLauncher extends AbstractNoParamsJobInfo {

	@ConfigureReaction
	public void setup(JobContext pContext) {
		// @formatter:off
		pContext.registerJob(pContext.newJobBuilder()
			.method(this::getLauncher).info(this)
			.result(ProcessLauncher.class).isTransient().name("nmap").state("arpScan").build()
			.param(ProcessLauncher.class).name("nmap").missingState("arpScan").build()
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
		return pNMapLauncher.extend(null, constant("-sn"), constant("-PR"), constant("-n"), constant("-oX"),
			constant("-"), variable("TARGET"));
	}
}
