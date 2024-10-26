package org.buni.meldware.webadmin;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.buni.meldware.common.logging.Log;
import org.buni.meldware.common.preferences.UserProfileService;
import org.buni.meldware.mail.abmounts.SystemABMountsService;
import org.buni.meldware.mail.mailbox.MailboxService;
import org.buni.meldware.mail.management.AdminTool;
import org.buni.meldware.webadmin.command.xml.XMLCommand;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class AdminXMLRPC {
   // MailSender sender;

	private UserProfileService ups;

	private SystemABMountsService sabms;

    private AdminTool adminService;

    private MailboxService mbs;
    
    private final static Log log = Log.getLog(AdminXMLRPC.class);
    private final static String BASE_PKG = AdminXMLRPC.class.getPackage().getName();
    private final static String PKG_EXT = ".command.xml.";
    private final static String FULL_PKG = BASE_PKG+PKG_EXT;
    private final static String COMMAND_EXT = "Command";
    

    public void process(HttpServletRequest request, HttpServletResponse response) throws IOException, ParserConfigurationException, SAXException {
        InputStream stream = new BufferedInputStream(request.getInputStream());
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = db.parse(stream);
        Element e = (Element)doc.getElementsByTagName("op").item(0);
        String op = e.getTextContent();
        log.debug("opname= %s",op);
        op = (""+op.charAt(0)).toUpperCase() + op.substring(1);
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Class handlerClass = null;
        XMLCommand cmd = null; 
        try {
            String classname = FULL_PKG+op+COMMAND_EXT;
            handlerClass = loader.loadClass(classname);
            cmd = (XMLCommand) handlerClass.newInstance();
        } catch (Exception ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        PrintWriter writer = response.getWriter();
        try {
        	Method m = handlerClass.getMethod("setUserProfile", new Class[]{UserProfileService.class});
        	m.invoke(cmd, new Object[]{this.ups});
        } catch (Exception m1) {
        	
        }
        try {
        	Method m = handlerClass.getMethod("setSystemABMounts", new Class[]{SystemABMountsService.class});
        	m.invoke(cmd, new Object[]{this.sabms});
        } catch (Exception m2) {
        	
        }
        try {
            Method m = handlerClass.getMethod("setAdminService", new Class[]{AdminTool.class});
            m.invoke(cmd, new Object[]{this.adminService});
        } catch (Exception m2) {
            
        }
        try {
            Method m = handlerClass.getMethod("setMailboxService", new Class[]{MailboxService.class});
            m.invoke(cmd, new Object[]{this.mbs});
        } catch (Exception m2) {
            
        }    
        
        if(cmd.execute(request, doc, writer).equals(XMLCommand.SUCCESS)) {
            writer.println("<status><code>0</code><message>Success!</message></status>");
        } else {
            writer.println("<status><code>-1</code><message>Failed!</message></status>");
        }
    }
    
	public void setUserProfileService(UserProfileService profileService) {
		this.ups = profileService;
		
	}
	public void setSystemABMountsService(SystemABMountsService abMountService) {
		this.sabms = abMountService;	
	}
    
    public void setAdminService(AdminTool admin) {
        this.adminService = admin;
        
    }
    public void setMailboxService(MailboxService mbs) {
        this.mbs = mbs;
        
    }

}
