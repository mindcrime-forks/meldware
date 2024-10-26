/**
 * 
 */
package org.buni.meldware.mail.protocol;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.bsf.BSFException;
import org.buni.meldware.mail.util.io.IOUtil;

/**
 * @author Michael.Barker
 *
 */
public class RubyProtocolRunner extends ProtocolRunner {

    /**
     * @param host
     * @param port
     */
    public RubyProtocolRunner() {
    }

    /**
     * @see org.jboss.mail.protocol.ProtocolRunner#getClassName()
     */
    @Override
    public String getClassName() {
        return "org.jruby.javasupport.bsf.JRubyEngine";
    }

    /**
     * @see org.jboss.mail.protocol.ProtocolRunner#getExtensions()
     */
    @Override
    public String[] getExtensions() {
        return new String[]{ "rb" };
    }

    /**
     * @see org.jboss.mail.protocol.ProtocolRunner#getInclude()
     */
    @Override
    public String getInclude() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try {
            return IOUtil.toString(cl.getResourceAsStream("org/buni/meldware/mail/protocol/base.rb"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see org.jboss.mail.protocol.ProtocolRunner#getName()
     */
    @Override
    public String getName() {
        return "ruby";
    }
    
    
    public static void run(String host, int port, String script, 
            Map<String,Object> params) throws BSFException {
        
        ProtocolRunner ph = new RubyProtocolRunner();
        ph.run(script, params);
    }


    public static void run(String host, int port, File f, 
            Map<String,Object> params) throws BSFException {
        
        ErrorReporter er = new JUnitErrorReporter();
        ProtocolRunner pr = new RubyProtocolRunner();
        params.put("host", host);
        params.put("port", port);
        try {
            System.out.printf("-- Running: %s --\n", f.getName());
            String script = pr.loadFile(f);
            pr.run(script, params);
        } catch (BSFException e) {
            e.printStackTrace();
            er.report(getMessage(e));
            //throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static String getMessage(BSFException e) {
        String result;
        result = e.getMessage();
        if (e.getTargetException() != null) {
            result = e.getTargetException().getMessage();
            if (e.getTargetException().getCause() != null) {
                result = e.getTargetException().getCause().getMessage();
            }
        }
        return result;
    }

    public static void run(String host, int port, File f) throws BSFException {
        if (!f.exists()) {
            throw new RuntimeException(f.getName() + " does not exist");
        }
        run(host, port, f, new HashMap<String,Object>(0));
    }

}
