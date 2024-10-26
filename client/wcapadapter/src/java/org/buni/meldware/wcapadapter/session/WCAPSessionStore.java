package org.buni.meldware.wcapadapter.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class WCAPSessionStore extends TimerTask{

	private static final WCAPSessionStore instance = new WCAPSessionStore();
	//instance valiables
	private Map store = null;
	
	private WCAPSessionStore() {
		super();
		this.store = new HashMap<String, WCAPSession>();

	    //perform the task once a day at 4 a.m., starting tomorrow morning
	    //(other styles are possible as well)
	    Timer timer = new Timer();
	    int interval = 60000;
	    timer.scheduleAtFixedRate(this, System.currentTimeMillis() + interval , interval);	
	}

	/**
	 * Cleaner task is run to collect old sessions.
	 * 
	 * @see java.util.TimerTask#run()
	 */
	@Override
	public void run() {
	
	}
	
	public static final WCAPSessionStore getInstance()
	{
		return instance;
	}
	
	public WCAPSession createSession(String userId,String userName,String password){
		WCAPSession session = new WCAPSession(userId,userName,password,this);
		this.store.put(session.sessionId, session);
		return session;
	}
	
	public WCAPSession getSession(String id)
	{
		return (WCAPSession)this.store.get(id);
	}
	
	void remove(WCAPSession expired)
	{
		this.store.remove(expired.sessionId);
	}
}
