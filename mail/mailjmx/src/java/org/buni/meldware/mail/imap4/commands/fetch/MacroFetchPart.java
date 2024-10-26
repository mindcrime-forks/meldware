package org.buni.meldware.mail.imap4.commands.fetch;

import java.io.IOException;
import java.util.Set;

import org.buni.meldware.common.util.ArrayUtil;
import org.buni.meldware.mail.StreamWriteException;
import org.buni.meldware.mail.api.FolderMessage;
import org.buni.meldware.mail.imap4.IMAP4OutputStream;

public class MacroFetchPart extends FetchPart {
    
    Set<String> SUPPORTED = ArrayUtil.asSet("ALL", "FULL", "FAST");

    private String name;

    private static FetchPart[] ALL_FETCH = { 
        new MessagePropertyPart("FLAGS"),
        new MessagePropertyPart("INTERNALDATE"),
        new RFC822PartRequest("SIZE"),
        new MessagePropertyPart("ENVELOPE")
    };
    
    private static FetchPart[] FAST_FETCH = { 
        new MessagePropertyPart("FLAGS"),
        new MessagePropertyPart("INTERNALDATE"),
        new RFC822PartRequest("SIZE"),
    };
    
    private static FetchPart[] FULL_FETCH = { 
        new MessagePropertyPart("FLAGS"),
        new MessagePropertyPart("INTERNALDATE"),
        new RFC822PartRequest("SIZE"),
        new MessagePropertyPart("ENVELOPE"),
        new MessagePropertyPart("BODY")
    };
    
    public MacroFetchPart(String name) {
        this.name = name;
    }
    
    @Override
    public boolean requiresMessage() {
        return true;
    }

    private FetchPart[] expandMacro(String s) {
        if ("ALL".equals(s)) {
            return ALL_FETCH;
        } else if ("FULL".equals(s)) {
            return FULL_FETCH;
        } else if ("FAST".equals(s)) {
            return FAST_FETCH;
        } else {
            throw new UnknownMacroException("Unknown fetch macro: %s", s);
        }        
    }

    @Override
    public void fetch(FolderMessage msg, IMAP4OutputStream out) {
        
        FetchPart[] parts = expandMacro(name);
        for (int i = 0; i < parts.length; i++) {
            FetchPart part = parts[i];
            part.fetch(msg, out);
            if (i < parts.length - 1) {
                try {
                    out.write(' ');
                } catch (IOException e) {
                    throw new StreamWriteException("Error writing fetch", e);
                }
            }
        }
    }

    public String toString() {
        return name;
    }
}
