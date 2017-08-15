package com.diamondq.common.reaction.api.tests;

import com.diamondq.common.reaction.api.Action;
import com.diamondq.common.reaction.api.JobContext;

import org.junit.Test;

public class LargerConstructionTest {

	public String simpleFunc(String pInput) {
		return pInput + "--";
	}

	public Boolean check(Object pInput) {
		return false;
	}

	public String simpleSupplier() {
		return "Basic";
	}

	public static class Credentials {

	}

	public static class CredentialForm {

	}

	public static class TimeRecord {

	}

	@Test
	public void test() {
		JobContext jc = new MockJobContext();
		// @formatter:off
		jc.newJobBuilder().method(this::simpleFunc)
			.triggerCollection(TimeRecord.class).action(Action.CHANGE).variable("Employer").variable("Employee").build()
			.param(Credentials.class).name("Dropbox Credentials").isPersistent().stateByVariable("Employer").build()
			.param(String.class).name("Dropbox Folder").isPersistent().stateByVariable("Employer").build()
			.param(TimeRecord.class).name("Timesheet").stateByVariable("Employer").stateByVariable("Employee").build()
		.build();
		
		/* Retrieve the credentials from the database */
		
		jc.newJobBuilder().method(this::simpleFunc)
			.result(Credentials.class).name("Dropbox Credentials").isPersistent().variable("Employer").build()
			.guard(this::check)
		.build();
		
		/* Otherwise, store new credentials after getting them from the user */
		
		jc.newJobBuilder().method(this::simpleFunc)
			.result(Credentials.class).name("Dropbox Credentials").isPersistent().variable("Employer").build()
			.param(Credentials.class).name("Dropbox Credentials").isTransient().state("Validated").stateByVariable("Employer").build()
		.build();

		/* Validate that the user provided credentials are 'good' */
		
		jc.newJobBuilder().method(this::simpleFunc)
			.result(Credentials.class).name("Dropbox Credentials").isTransient().state("Validated").variable("Employer").asParam().build()
			.param(Credentials.class).name("Dropbox Credentials").isTransient().missingState("Validated").stateByVariable("Employer").build()
		.build();
		
		/* Send Dropbox Credentials Request Data Form to User */
		
		jc.newJobBuilder().method(this::simpleFunc)
			.result(CredentialForm.class).variable("Employer").isTransient().build()
			.param(String.class).state("Online").state("Subscribed").stateByVariable("Employer").name("Jid").build()
			.param(String.class).state("LoggedIn").name("XMPP Client").build()
			.prepResult(Credentials.class).name("Dropbox Credentials").isTransient().stateByVariable("Employer").build()
		.build();

		//@formatter:on
	}

}
