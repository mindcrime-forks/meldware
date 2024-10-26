/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2006, Bunisoft Inc.,
 *
 * Portions of this software are Copyright 2006, JBoss Inc., and 
 * individual contributors as indicated by the @authors tag.
 * See the copyright.txt in the distribution for a full listing of
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.buni.meldware.mail.maillistener.actions;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.buni.meldware.common.util.ArrayUtil;
import org.buni.meldware.mail.MailException;
import org.buni.meldware.mail.MailListener;
import org.buni.meldware.mail.mailbox.MailboxService;
import org.buni.meldware.mail.message.EnvelopedAddress;
import org.buni.meldware.mail.message.Mail;
import org.buni.meldware.mail.message.MailAddress;
import org.buni.meldware.mail.message.Message;
import org.jboss.resource.adapter.jdbc.remote.WrapperDataSourceServiceMBean;

/**
 * @author acoliver ot buni dat org 
 * @version $Revision: 1.4 $
 */
public class ServerActionsMailListenerImpl implements ServerActionsMailListener, MailListener {

    private String createActionSetsQuery;

    private String createActionsQuery;

    private String createConditionsQuery;

    private String createUserActionSetsQuery;

    private String retriveActionsQuery;

    private String retriveConditionsQuery;

    private String retrieveUserActionSetsQuery;
    
    private String insertUserActionSetsQuery;
    
    private String insertActionSetQuery;
    
    private String insertConditionQuery;
    
    private String insertActionQuery;

    private WrapperDataSourceServiceMBean wds;

    private DataSource ds;

    private String deleteActionQuery;

    private String deleteActionSetQuery;

    private String deleteConditionQuery;

    private String deleteUserActionSetsQuery;
    
    //YAGNI configurable set of conditions
    private Map<String, Condition> conditionObjects;

    private Map<String, Action> actionObjects;

    private static final Logger log = Logger.getLogger(ServerActionsMailListenerImpl.class);

    private MailboxService mbs;
    
    // required to be an mbean service
    public void create() {
    }

    public void start() {
        try {
            initConditions(); // we could do this in create but create is usually useless anyhow
            initActions();
            String jndiName = this.wds.getBindName();
            Context ctx;
            ctx = new InitialContext();
            this.ds=(DataSource) ctx.lookup(jndiName);
            
            this.executeStatement(this.createUserActionSetsQuery, new String[]{});
            this.executeStatement(this.createActionSetsQuery, new String[]{});
            this.executeStatement(this.createActionsQuery, new String[]{});
            this.executeStatement(this.createConditionsQuery, new String[]{});
        } catch (Exception e) {
            log.debug("could not create one of the server actions tables ",e);
            //e.printStackTrace();
        }
    }

    public void stop() {
    }

    public void destroy() {
    }
    
    public UserActionSets retrieveUserActionSets(String user) {
        long fid = mbs.getMailboxIdByAlias(user);
        
        UserActionSets sets = null;
        ResultSet rs = null;
        try {
            rs = executeResultStatement(this.getRetrieveUserActionSetsQuery(), new Long[] {fid});
            if (rs.next()) {
                sets = new UserActionSets(rs);
            }
            List<UserActionSet> uas = sets != null ? sets.getUserActionSets() : null;
            if (uas != null) {
                for (UserActionSet set : uas) {
                    List<ActionConfig> acs = this.getActionConfigs(set);
                    List<ConditionConfig> ccs = this.getConditions(set);
                    set.setActions(acs);
                    set.setConditions(ccs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Could not get Action Sets for User "+user, e);
        } finally {
            close(rs);
        }
        return sets;
    }

    /**
     * handle a message.
     * 
     * @param msg
     *            the message object.
     * @return the message containing what could not be handled by this
     *         listener, null if the whole message was consumed
     */
    public Message send(Message msg) throws MailException {
        for (MailAddress address : msg.getTo()) {
            EnvelopedAddress ea = new EnvelopedAddress(address);
            if (ea.getLocal()) {
                
                String user = ea.getRawAddress(); // this is wrong should get the user for the alias
                user = user.replace("<", "");
                user = user.replace(">", "");
                user = user.trim();
                
                UserActionSets uas = this.retrieveUserActionSets(user.toLowerCase());
                if (uas == null) {
                    return msg;
                } else {
                    List<UserActionSet>ua = uas.getUserActionSets();
                    for (UserActionSet set : ua) {
                        List<ConditionConfig> conditions = getConditions(set);
                        set.setConditions(conditions);
                        set.setConditionFactory(this);
                        set.setActionFactory(this);
                        if (set.evaluate((Mail)msg)) {
                            List<ActionConfig> actions = getActionConfigs(set);
                            for (ActionConfig config : actions) {
                                String name = config.getName();
                                String param = config.getParams();
                                Action a = this.actionObjects.get(config.getName());
                                a.perform(ArrayUtil.commaDelToArray(param), (Mail)msg, ea);
                                System.err.println("ACTION "+name+" param "+param);
                            }
                        }
                    }
                }
            }
        }
        return msg;
    }

    private List<ActionConfig> getActionConfigs(UserActionSet set) {
        ResultSet rs = null;
        List<ActionConfig> configs = new ArrayList<ActionConfig>();
        try {
            rs = executeResultStatement(this.getRetrieveActionsQuery(), new String[] {set.getId()});
            while (rs.next()) {
                ActionConfig config = new ActionConfig(rs);
                configs.add(config);
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not get actions for set "+set.getId());
        } finally {
            close(rs);
        }
        return configs;
    }

    private List<ConditionConfig> getConditions(UserActionSet set) {
        ResultSet rs = null;
        List<ConditionConfig> configs = new ArrayList<ConditionConfig>();
        try {
            rs = executeResultStatement(this.getRetrieveConditionsQuery(), new String[] {set.getId()});
            while (rs.next()) {
                ConditionConfig config = new ConditionConfig(rs);
                configs.add(config);
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not get Conditions for set "+set.getId());
        } finally {
            close(rs);
        }
        return configs;
    }
    
    public boolean createUserActionSets(UserActionSets uas, String user) {
        UserActionSets uas2 = this.retrieveUserActionSets(user);
        if (uas2 != null) {
            throw new RuntimeException("user already has an action sets object");
        }
        
        String uasid = createUAS(uas, user);
        uas.setId(uasid);
        List<UserActionSet> sets = uas.getUserActionSets() == null ? new ArrayList<UserActionSet>() : uas.getUserActionSets();
        int i=0;
        for (UserActionSet set : sets) {
            String aid = createActionSet(set, uasid, i);
            set.setId(aid);
            i++;
        }
        return true;
    }

    private String createActionSet(UserActionSet set, String uasid, int order) {
        String asid = createAS(set, uasid, order);
        List<ConditionConfig> conditions = set.getConditions();
        int i=0;
        for (ConditionConfig config : conditions) {
            String cid = this.createCondition(asid, config, i);
            config.setId(cid);
            i++;
        }
        i = 0;
        List<ActionConfig> actions = set.getActions();
        for (ActionConfig config : actions) {
            String aid = this.createAction(asid, config, i);
            config.setId(aid);
            i++;
        }
        return asid;
    }

    private String createAction(String asid, ActionConfig config, int order) {
        String id = generateUID();
        this.executeStatement(insertActionQuery, new Object[] {id, asid, order, config.getName(), config.getParams()});
        return id;
    }

    private String createCondition(String asid, ConditionConfig config, int order) {
        String id = generateUID();
        this.executeStatement(this.insertConditionQuery, new Object[]{id, asid, order, config.getIgnoreCase(), config.getNegation(),
                                                                       config.getName(), config.getHeader(), config.getValue()});
        return id;
    }

    private String createAS(UserActionSet set, String uasid, int order) {
        String id = this.generateUID();
        this.executeStatement(insertActionSetQuery, new Object[] {id, uasid, set.getName(), order, set.getAll()}); 
        return id;
    }

    private String createUAS(UserActionSets uas, String user) {
        String id = this.generateUID();
        this.executeStatement(insertUserActionSetsQuery, new String[]{id,user});
        return id;
    }

    public String getCreateActionSetsQuery() {
        return this.createActionSetsQuery;
    }

    public String getCreateActionsQuery() {
        return this.createActionsQuery;
    }

    public String getCreateConditionsQuery() {
        return this.createConditionsQuery;
    }

    public String getCreateUserActionSetsQuery() {
        return this.createUserActionSetsQuery;
    }

    public String getRetrieveActionsQuery() {
        return this.retriveActionsQuery;
    }

    public String getRetrieveConditionsQuery() {
        return this.retriveConditionsQuery;
    }

    public String getRetrieveUserActionSetsQuery() {
        return this.retrieveUserActionSetsQuery;
    }

    public void setCreateActionSetsQuery(String query) {
        this.createActionSetsQuery = query;
    }

    public void setCreateActionsQuery(String query) {
        this.createActionsQuery = query;
    }

    public void setCreateConditionsQuery(String query) {
        this.createConditionsQuery = query;
    }

    public void setCreateUserActionSetsQuery(String query) {
        this.createUserActionSetsQuery = query;
    }

    public void setRetrieveActionsQuery(String query) {
        this.retriveActionsQuery = query;
    }

    public void setRetrieveConditionsQuery(String query) {
        this.retriveConditionsQuery = query;
    }

    public void setRetrieveUserActionSetsQuery(String query) {
        this.retrieveUserActionSetsQuery = query;
    }
    
    public void setDataSource(WrapperDataSourceServiceMBean wds) {
        this.wds = wds; 
    }
    
    public WrapperDataSourceServiceMBean getDataSource() {
        return this.wds;
    }    
    
    private void executeStatement(String query, Object[] objects) {
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = this.ds.getConnection();
            stmt = con.prepareStatement(query);
            for (int i = 0; i < objects.length; i++) {
               stmt.setObject(i+1, objects[i]);               
            }          
            stmt.execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {stmt.close();} catch (Exception e2) {}
            try {con.close();} catch (Exception e2) {}
        }       
    }
    
    private ResultSet executeResultStatement(String query, Object[] parms) throws SQLException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        con = this.ds.getConnection();
        stmt = con.prepareStatement(query);
        for (int i = 0; i < parms.length; i++) {
           stmt.setObject(i+1, parms[i]);               
        }          
        stmt.execute();
        rs = stmt.getResultSet();
 
        return rs;
    }
     
    private void close(ResultSet rs) {
        Statement stmt = null;
        Connection conn = null; 
        try {
            stmt = rs.getStatement();
        } catch (Exception e) {
            
        }
        
        try {
            conn = stmt != null ? stmt.getConnection() : null;            
        } catch (Exception e) {
        }
        
        try {
            rs.close();
        } catch (Exception e) {
        }
        
        try {
            stmt.close();
        } catch (Exception e) {
        }

        try {
            conn.close();
        } catch (Exception e) {
        }
        
    }

    
    public String getInsertActionQuery() {
        return insertActionQuery;
    }

    
    public void setInsertActionQuery(String insertActionQuery) {
        this.insertActionQuery = insertActionQuery;
    }

    
    public String getInsertActionSetQuery() {
        return insertActionSetQuery;
    }

    
    public void setInsertActionSetQuery(String insertActionSetQuery) {
        this.insertActionSetQuery = insertActionSetQuery;
    }

    
    public String getInsertConditionQuery() {
        return insertConditionQuery;
    }

    
    public void setInsertConditionQuery(String insertConditionQuery) {
        this.insertConditionQuery = insertConditionQuery;
    }

    
    public String getInsertUserActionSetsQuery() {
        return insertUserActionSetsQuery;
    }

    
    public void setInsertUserActionSetsQuery(String insertUserActionSetsQuery) {
        this.insertUserActionSetsQuery = insertUserActionSetsQuery;
    }

    public String getDeleteActionQuery() {
        return this.deleteActionQuery;
    }

    public String getDeleteActionSetQuery() {
        return this.deleteActionSetQuery;
    }

    public String getDeleteConditionQuery() {
        return deleteConditionQuery;
    }

    public String getDeleteUserActionSetsQuery() {
        return deleteUserActionSetsQuery;
    }

    public void setDeleteUserActionSetsQuery(String deleteUserActionSetsQuery) {
        this.deleteUserActionSetsQuery = deleteUserActionSetsQuery;
    }

    public void setDeleteActionQuery(String deleteActionQuery) {
        this.deleteActionQuery = deleteActionQuery;
    }

    public void setDeleteActionSetQuery(String deleteActionSetQuery) {
        this.deleteActionSetQuery = deleteActionSetQuery;
    }

    public void setDeleteConditionQuery(String deleteConditionQuery) {
        this.deleteConditionQuery = deleteConditionQuery;
    }
        
    public String generateUID() {
        UUID uuid = UUID.randomUUID();
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        byte[] resultBytes = new byte[16];
        for (int i = 7; i >= 0; i--)
        {
          resultBytes[i+8] = (byte)lsb;
            lsb >>>= 8;
            resultBytes[i] = (byte)lsb;
            msb >>>= 8;
        }
        BigInteger bigint = new BigInteger(1, resultBytes);
        return bigint.toString(32);       
    }

    public void deleteUserActionSets(UserActionSets uas) {
        List<UserActionSet> sets = uas.getUserActionSets();
        for (UserActionSet set : sets) {
            List<ActionConfig> acs = set.getActions();
            for (ActionConfig config : acs) {
                this.executeStatement(this.getDeleteActionQuery(), new String[]{config.getId()});
            }
            List<ConditionConfig> ccs = set.getConditions();
            for (ConditionConfig config : ccs) {
                this.executeStatement(this.getDeleteConditionQuery(), new String[]{config.getId()}); 
            }
            this.executeStatement(this.getDeleteActionSetQuery(), new String[]{set.getId()});
        }
        this.executeStatement(this.getDeleteUserActionSetsQuery(), new String[]{uas.getUser()});
    }
    
    private void initConditions() {
        Equals equals = new Equals(false);
        Equals notEquals = new Equals(true);
        Contains contains = new Contains(false);
        Contains doesntContain = new Contains(true);
        EndsWith endsWith = new EndsWith(false);
        EndsWith doesntEndWith = new EndsWith(true);
        StartsWith startsWith = new StartsWith(false);
        StartsWith doesntStartWith = new StartsWith(true);
        
        this.conditionObjects = new HashMap<String, Condition>();
        this.conditionObjects.put(equals.getSymbol(), equals);
        this.conditionObjects.put(notEquals.getAntiSymbol(), notEquals);
        this.conditionObjects.put(contains.getSymbol(), contains);
        this.conditionObjects.put(doesntContain.getAntiSymbol(), doesntContain);
        this.conditionObjects.put(endsWith.getSymbol(), endsWith);
        this.conditionObjects.put(doesntEndWith.getAntiSymbol(), doesntEndWith);
        this.conditionObjects.put(startsWith.getSymbol(), startsWith);
        this.conditionObjects.put(doesntStartWith.getAntiSymbol(), doesntStartWith);
    }
    
    private void initActions() {
        MoveAction move = new MoveAction();
        CopyAction copy = new CopyAction();
        DeleteAction delete = new DeleteAction();

        this.actionObjects = new HashMap<String, Action>();
        this.actionObjects.put(MoveAction.NAME, move);
        this.actionObjects.put(CopyAction.NAME, copy);
        this.actionObjects.put(DeleteAction.NAME, delete);
    }

    public Condition getCondition(ConditionConfig config) {
        String symbol = config.getName(); //TODO handle diff between symbol and name,etc
        return conditionObjects.get(symbol);
    }
    
    public void setMailboxService(MailboxService mbs) {
        this.mbs = mbs;
    }
    
    public MailboxService getMailboxService() {
        return this.mbs;
    }

}