package com.diamondq.reactions.network.arp;

import com.diamondq.common.config.Config;
import com.diamondq.common.lambda.future.ExtendedCompletableFuture;
import com.diamondq.reactions.api.ConfigureReaction;
import com.diamondq.reactions.api.JobContext;
import com.diamondq.reactions.api.ReactionsEngine;
import com.diamondq.reactions.api.info.AbstractNoParamsJobInfo;
import com.diamondq.reactions.common.process.ProcessLauncher;
import com.diamondq.reactions.common.process.ProcessLauncher.Result;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The ArpScan is responsible for running the ARP command, parsing the results, and passing the individual records on
 * for processing. This job does not take any input or return any values directly.
 */
@ApplicationScoped
public class ArpScan extends AbstractNoParamsJobInfo {
	private static final Logger					sLogger					= LoggerFactory.getLogger(ArpScan.class);

	private final File							mStoragePath;

	private final ThreadLocal<SimpleDateFormat>	mDateFolderFormatter	=
		ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));

	private final ThreadLocal<SimpleDateFormat>	mDateFileFormatter		=
		ThreadLocal.withInitial(() -> new SimpleDateFormat("HH-mm-ss"));

	private final ReactionsEngine				mEngine;

	@Inject
	public ArpScan(Config pConfig, ReactionsEngine pEngine) {
		mEngine = pEngine;
		String storagePathStr = pConfig.bind("timely.arp.local-storage.path", String.class);
		if (storagePathStr == null)
			throw new IllegalArgumentException("The config key timely.arp.local-storage.path is required");
		File storagePath = new File(storagePathStr);
		if (storagePath.exists() == false) {
			if (storagePath.mkdirs() == false)
				throw new IllegalArgumentException("Unable to create the storage directory " + storagePathStr);
			if (storagePath.exists() == false)
				throw new IllegalArgumentException("Unable to create the storage directory " + storagePathStr);
		}

		mStoragePath = storagePath;
	}

	@ConfigureReaction
	public void setup(JobContext pContext) {
		// @formatter:off
		pContext.registerJob(pContext.newJobBuilder()
			.method(this::execute).info(this)
			.param(ProcessLauncher.class).isTransient().name("arp").state("arpAll").build()
		.build());
		// @formatter:on
	}

	public ExtendedCompletableFuture<@Nullable Void> execute(ProcessLauncher pArpLauncher) {
		sLogger.info("Execute");

		/* Run the command */

		Date date = new Date();
		Result result = pArpLauncher.launch(null, Collections.singletonList("-a"));
		if (result.code != 0) {
			sLogger.warn("Unable to get the arp data");
			throw new IllegalArgumentException("Unable to get the arp data");
		}

		String msg = new String(result.data);

		sLogger.debug("Result of ARP: {}", msg);

		ArpParser parserJob = mEngine.findMandatoryJob(ArpParser.class);
		ExtendedCompletableFuture<Map<InetAddress, Collection<ArpRecord>>> parseResult =
			mEngine.submit(parserJob, parserJob.newParamsBuilder().date(date).message(msg));

		return parseResult.thenAccept((map) -> {
			sLogger.debug("Result of ARP Parsing: {}", map);

			/* Write the arp record to disk */

			File storageFolder = new File(mStoragePath, mDateFolderFormatter.get().format(date));
			if (storageFolder.exists() == false) {
				if (storageFolder.mkdirs() == false)
					throw new IllegalArgumentException(
						"Unable to create the storage directory " + storageFolder.getAbsolutePath());
				if (storageFolder.exists() == false)
					throw new IllegalArgumentException(
						"Unable to create the storage directory " + storageFolder.getAbsolutePath());
			}

			File storageFile = new File(storageFolder, "arp-" + mDateFileFormatter.get().format(date) + ".txt");
			try {
				try (FileWriter fw = new FileWriter(storageFile)) {
					fw.write(map.toString());
				}
			}
			catch (IOException ex) {
				throw new RuntimeException(ex);
			}

		});

	}

}
