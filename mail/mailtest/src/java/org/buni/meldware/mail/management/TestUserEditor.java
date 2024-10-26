package org.buni.meldware.mail.management;

import javax.naming.Context;
import javax.naming.InitialContext;

import junit.framework.TestCase;

import org.jboss.jmx.adaptor.rmi.RMIAdaptor;


public class TestUserEditor extends TestCase {
    private static final String MBEAN_USER_EDITOR = null;
 //   UserEditor userEditor;
    
    protected void setUp() throws Exception {
        Context ctx = new InitialContext();
        //  Context ctx = new InitialContext();
        RMIAdaptor rmi = (RMIAdaptor) ctx.lookup("jmx/rmi/RMIAdaptor");
 //       userEditor = (UserEditor) ProxyMaker.makeProxy(rmi, UserEditor.class, new ObjectName(MBEAN_USER_EDITOR));
    }

}
