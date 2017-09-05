package com.diamondq.reactions.api.tests;

import com.diamondq.reactions.api.Action;
import com.diamondq.reactions.api.JobContext;

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

	@SuppressWarnings("null")
	@Test
	public void test() {
		JobContext jc = new MockJobContext();
		// @formatter:off
		jc.newJobBuilder().method(this::simpleFunc)
			.triggerCollection(TimeRecord.class).action(Action.CHANGE).build()
			.variable(String.class, "Employer").build()
			.variable(String.class, "Employee").build()
			.param(Credentials.class).name("Dropbox Credentials").isPersistent().stateByVariable("Employer").build()
			.param(String.class).name("Dropbox Folder").isPersistent().stateByVariable("Employer").build()
			.param(TimeRecord.class).name("Timesheet").stateByVariable("Employer").stateByVariable("Employee").build()
		.build();

		/* Retrieve the credentials from the database */

		jc.newJobBuilder().method(this::simpleFunc)
			.result(Credentials.class).name("Dropbox Credentials").isPersistent().build()
			.variable(String.class, "Employer").build()
			.guard(this::check)
		.build();

		/* Otherwise, store new credentials after getting them from the user */

		jc.newJobBuilder().method(this::simpleFunc)
			.result(Credentials.class).name("Dropbox Credentials").isPersistent().build()
			.variable(String.class, "Employer").build()
			.param(Credentials.class).name("Dropbox Credentials").isTransient().state("Validated").stateByVariable("Employer").build()
		.build();

		/* Validate that the user provided credentials are 'good' */

		jc.newJobBuilder().method(this::simpleFunc)
			.result(Credentials.class).name("Dropbox Credentials").isTransient().state("Validated").asParam().build()
			.variable(String.class, "Employer").build()
			.param(Credentials.class).name("Dropbox Credentials").isTransient().missingState("Validated").stateByVariable("Employer").build()
		.build();

		/* Send Dropbox Credentials Request Data Form to User */

		jc.newJobBuilder().method(this::simpleFunc)
			.result(CredentialForm.class).isTransient().build()
			.variable(String.class, "Employer").build()
			.param(String.class).state("Online").state("Subscribed").stateByVariable("Employer").name("Jid").build()
			.param(String.class).state("LoggedIn").name("XMPP Client").build()
			.prepResult(Credentials.class).name("Dropbox Credentials").isTransient().stateByVariable("Employer").build()
		.build();

		//@formatter:on
	}

}
