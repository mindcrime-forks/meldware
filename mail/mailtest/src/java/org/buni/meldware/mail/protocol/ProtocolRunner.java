/**
 * 
 */
package org.buni.meldware.mail.protocol;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;


/**
 * @author Michael.Barker
 *
 */
public abstract class ProtocolRunner {

    public ProtocolRunner() {
    }
    
    public abstract String getName();
    public abstract String getClassName();
    public abstract String[] getExtensions();
    public abstract String getInclude();
    
    public void run(File f, Map<String,Object> params) throws BSFException, IOException {
        run(loadFile(f), params);
    }
    
    public void run(String script, Map<String,Object> params) 
        throws BSFException {
        
        String name = getName();
        String clsName = getClassName();
        String[] ext = getExtensions();
        String include = getInclude();
        
        BSFManager.registerScriptingEngine(name, clsName, ext);
        BSFManager mgr = new BSFManager();
        ProtocolHandler ph = new ProtocolHandler();
        mgr.declareBean("ph", ph, ProtocolHandler.class);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            Class cls = entry.getValue().getClass();
            mgr.declareBean(entry.getKey(), entry.getValue(), cls);
            //mgr.registerBean(entry.getKey(), entry.getValue());
        }
        mgr.exec(name, "(java)", 1, 1, include);
        mgr.exec(name, "(java)", 1, 1, script);

    }

    /**
     * @param f
     * @return
     * @throws IOException 
     */
    public String loadFile(File f) throws IOException {
        StringBuilder sb = new StringBuilder();
        Reader r = new FileReader(f);
        char[] buf = new char[8192];
        int numRead = 0;
        while ((numRead = r.read(buf)) != -1) {
            sb.append(buf, 0, numRead);
        }
        return sb.toString();
    }
    
    
}
