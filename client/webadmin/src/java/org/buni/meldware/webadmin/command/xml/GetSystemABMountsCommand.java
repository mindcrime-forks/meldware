package org.buni.meldware.webadmin.command.xml;

import java.io.PrintWriter;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.buni.meldware.mail.abmounts.ABMount;
import org.buni.meldware.mail.abmounts.SystemABMountsService;
import org.w3c.dom.Document;

public class GetSystemABMountsCommand extends AbstractXMLCommand implements XMLCommand {
    SystemABMountsService sabs;
    public String execute(HttpServletRequest request, Document doc, PrintWriter out) {
        Set<ABMount> mounts = this.sabs.getAllABMounts();
        XMLizer.toXML("systemABMount", mounts, out);
       //     out.write(toElement("systemABMount",name));
        return SUCCESS;
    }
    
    public void setSystemABMounts(SystemABMountsService sabs) {
        this.sabs = sabs;
    }

}
