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
 * under the terms of the GNU Lesser General License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.buni.meldware.mail.maillistener.actions;

import org.buni.meldware.mail.MailException;
import org.buni.meldware.mail.mailbox.MailboxService;
import org.buni.meldware.mail.message.Message;
import org.jboss.resource.adapter.jdbc.remote.WrapperDataSourceServiceMBean;

/**
 * ServerActionsMailListener performs the server-side equivilent of Thunderbird style "Mail Filters" 
 * it matches usually headers against conditions and then sets target folders.  Based on those target 
 * folders the LocalDelivery service puts the mails in the appropriate folders.  The ServerActionsMailListener
 * also handles persistence of a user's mail listeners.
 * 
 * TODO: fix it to work on all aliases for an account rather than the goofy "get user name" logic and assume that
 * is enough.
 * 
 * @author Andrew C. Oliver (acoliver ot buni dat org)
 * @version $Revision: 1.2 $
 */
public interface ServerActionsMailListener {
    // required to be an mbean service
    void create();

    void start();

    void stop();

    void destroy();

    void setCreateUserActionSetsQuery(String query);
    String getCreateUserActionSetsQuery();

    void setCreateActionSetsQuery(String query);
    String getCreateActionSetsQuery();
    
    void setCreateConditionsQuery(String query);
    String getCreateConditionsQuery();
    
    void setCreateActionsQuery(String query);
    String getCreateActionsQuery();
    
    void setRetrieveUserActionSetsQuery(String query);
    String getRetrieveUserActionSetsQuery();
    
    void setRetrieveConditionsQuery(String query);
    String getRetrieveConditionsQuery();
    
    void setRetrieveActionsQuery(String query);
    String getRetrieveActionsQuery();
    
    void setDataSource(WrapperDataSourceServiceMBean wds);
    WrapperDataSourceServiceMBean getDataSource();
    
    Message send(Message msg) throws MailException;

    String getInsertActionQuery();    
    void setInsertActionQuery(String insertActionQuery);
    String getInsertActionSetQuery();
    void setInsertActionSetQuery(String insertActionSetQuery);
    String getInsertConditionQuery();
    void setInsertConditionQuery(String insertConditionQuery);
    String getInsertUserActionSetsQuery();
    void setInsertUserActionSetsQuery(String insertUserActionSetsQuery);
    
    String getDeleteActionQuery();
    void setDeleteActionQuery(String deleteActionQuery);
    
    String getDeleteConditionQuery();
    void setDeleteConditionQuery(String deleteConditionQuery);

    String getDeleteActionSetQuery();
    void setDeleteActionSetQuery(String deleteActionSetQuery);
    
    String getDeleteUserActionSetsQuery();
    void setDeleteUserActionSetsQuery(String deleteUserActionSetsQuery);
    
    String generateUID();
    
    boolean createUserActionSets(UserActionSets uas, String user);

    UserActionSets retrieveUserActionSets(String user);

    void deleteUserActionSets(UserActionSets uas);

    Condition getCondition(ConditionConfig config);

    void setMailboxService(MailboxService mbs);

    public MailboxService getMailboxService();
}
