package org.buni.meldware.wcapadapter.bean;

import org.buni.meldware.calendar.data.Invite;

public class Status {
	private Invite invite = null;

	private int statusCode = SUCCESS;

	private int operation = OPERTATION_STORE;

	public static final int SUCCESS = 1;

	public static final int FAILED = 2;

	public static final int OPERTATION_STORE = 1;

	public static final int OPERTATION_DELETE = 2;

	public Status(Invite invite, int statusCode, int operation) {
		super();
		this.invite = invite;
		this.statusCode = statusCode;
		this.operation = operation;
	}

	public String getICalStatus() {
		return printStatus(this.statusCode, this.operation);
	}

	public Invite getInvite() {
		return invite;
	}

	public static String printStatus(int status, int operation) {
		switch (status) {
		case SUCCESS:
			return "2.0;Success.  " + printOperation(operation)
					+ " successful.";
		case FAILED:
			// needs to get this confirmed
			return "1.0;Failed.  " + printOperation(operation) + " failed.";
		default:
			break;
		}
		throw new RuntimeException("unknown status:" + status);
	}

	public static String printOperation(int operation) {
		switch (operation) {
		case OPERTATION_STORE:
			return "Store";
		case OPERTATION_DELETE:
			return "Delete";
		}
		throw new RuntimeException("unknown operation:" + operation);

	}
}
