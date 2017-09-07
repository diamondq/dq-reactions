package com.diamondq.reactions.common.process;

import com.diamondq.reactions.api.ConfigureReaction;
import com.diamondq.reactions.api.JobContext;
import com.diamondq.reactions.api.JobInfo;
import com.diamondq.reactions.api.JobParamsBuilder;
import com.diamondq.reactions.api.errors.ReactionsNoResultNotErrorException;
import com.diamondq.reactions.common.process.KnownProcessLookup.KnownProcessLookupBuilder;
import com.diamondq.reactions.common.process.ProcessLauncher.Result;
import com.google.common.base.Charsets;

import java.io.File;
import java.util.Collections;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * This helper is used to find the full path to a known process (ie. ssh)
 */
@ApplicationScoped
public class KnownProcessLookup implements JobInfo<@Nullable Void, KnownProcessLookupBuilder> {

	public static class KnownProcessLookupBuilder implements JobParamsBuilder {

		private @Nullable String name;

		public KnownProcessLookupBuilder process(String pName) {
			name = pName;
			return this;
		}

		@Nullable
		String getName() {
			return name;
		}

		/**
		 * @see com.diamondq.reactions.api.JobParamsBuilder#setParam(java.lang.String, java.lang.Object)
		 */
		@Override
		public void setParam(String pKey, Object pValue) {
			if (pKey.equals("name"))
				name = (String) pValue;
			else
				throw new IllegalArgumentException("Unrecognized param key: " + pKey);
		}
	}

	private final ProcessLauncher mWhereIsLauncher;

	@Inject
	public KnownProcessLookup(ProcessWhereIsCommand pWhereIsCommand) {
		mWhereIsLauncher = pWhereIsCommand.getLauncher();
	}

	/**
	 * @see com.diamondq.reactions.api.JobInfo#newParamsBuilder()
	 */
	@Override
	public KnownProcessLookupBuilder newParamsBuilder() {
		return new KnownProcessLookupBuilder();
	}

	@ConfigureReaction
	public void setup(JobContext pContext) {
		// @formatter:off
		pContext.registerJob(pContext.newJobBuilder()
			.method(this::lookupProcess).info(this)
			.result(ProcessLauncher.class).isStored().state("base-launcher").build()
			.variable(String.class, "name").valueByResultName().build()
			.param(String.class).valueByVariable("name").build()
		.build());
		// @formatter:on
	}

	public ProcessLauncher lookupProcess(String pName) {

		/* On Windows, it's the WHERE /Q XXXX command. On Linux, it's the which XXXX command */

		Result result = mWhereIsLauncher.launch(Collections.singletonMap("COMMAND", pName), null);
		if (result.code == 0)
			return new ProcessLauncher(new String(result.data, Charsets.UTF_8).trim());
		else {

			/* Check if Cygwin is installed in the default location */

			File[] roots = File.listRoots();
			for (File root : roots) {
				File cygwinDir = new File(new File(root, "cygwin"), "bin");
				if (cygwinDir.exists() == false) {
					cygwinDir = new File(new File(root, "cygwin64"), "bin");
					if (cygwinDir.exists() == false)
						continue;
				}

				ProcessLauncher cygwinLauncher =
					new ProcessLauncher(ProcessLauncher.constant("CMD"), ProcessLauncher.constant("/C"), ProcessLauncher
						.constant("\"SET PATH=" + cygwinDir.getAbsolutePath() + ";%PATH% && WHERE " + pName + "\""));
				result = cygwinLauncher.launch(null, null);
				if (result.code == 0)
					return new ProcessLauncher(new String(result.data, Charsets.UTF_8).trim());
				else
					throw new ReactionsNoResultNotErrorException(new String(result.data, Charsets.UTF_8));

			}
			throw new ReactionsNoResultNotErrorException(new String(result.data, Charsets.UTF_8));
		}

	}

}
