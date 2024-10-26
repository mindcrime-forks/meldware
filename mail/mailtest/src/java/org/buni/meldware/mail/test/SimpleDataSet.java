/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2006, Bunisoft Inc., and individual contributors as
 * indicated by the @authors tag.  See the copyright.txt in the 
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; version 2.1 of
 * the License.
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
package org.buni.meldware.mail.test;

import junit.framework.TestCase;

import org.buni.meldware.mail.TestConstants;
import org.buni.meldware.mail.mailbox.Mailbox;
import org.buni.meldware.mail.mailbox.MailboxService;
import org.buni.meldware.mail.util.MMJMXUtil;

/**
 * A base data set to use when testing meldware.
 * 
 * @author Michael.Barker
 *
 */
public class SimpleDataSet extends TestCase implements DataSet {

    private final static String HOST = "localhost";
    private final static int IMAP_PORT = 9143;
    private final static int POP_PORT = 9110;
    private final static int SMTP_PORT = 9025;
    private String user;
    private String pass;
    private String alias;
    
    public SimpleDataSet(String user, String pass, String alias) {
        this.user = user;
        this.pass = pass;
        this.alias = alias;
    }
    
    /**
     * @return the alias
     */
    public String getAlias() {
        return alias;
    }


    /**
     * @param alias the alias to set
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }



    /**
     * @return the pass
     */
    public String getPass() {
        return pass;
    }



    /**
     * @param pass the pass to set
     */
    public void setPass(String pass) {
        this.pass = pass;
    }



    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    public String getHost() {
        return HOST;
    }
    
    public int getImapPort() {
        return IMAP_PORT;
    }
    
    public void setup() {
        MailboxService ms = MMJMXUtil.getMBean(TestConstants.MAILBOX_SERVICE_MBEAN, 
                MailboxService.class);
        
        if (ms.getMailboxByAlias(getUser()) != null) {
            Mailbox m = ms.createMailbox(getUser());
            ms.createAlias(m.getId(), getAlias());            
        }
    }

    public int getPopPort() {
        return 9110;
    }
    
}
