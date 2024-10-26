package org.buni.meldware.calendar.eventbus.mail;

import org.buni.meldware.calendar.eventbus.Event;

public class StatusChange extends Event{

	private int action;

	private String eventGUID;

	/** status ACCEPTED */
	public static final int ACCEPTED = 1;

	/** status DECLINED */
	public static final int DECLINED = 2;
	
	public StatusChange(String actor, int action, String eventGUID) {
		super(actor);
		this.action = action;
		this.eventGUID = eventGUID;
	}

	public int getAction() {
		return action;
	}

	public String getEventGUID() {
		return eventGUID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return this.actor + "-" + eventGUID + "-" + action;
	}
}
