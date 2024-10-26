package org.buni.meldware.mail;

import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;

public class IMAPUtil {
    private Session session;
    private Store store;
    private Folder folder;
    public void connect(String server, int port, String user, String pass) throws Exception {
        this.session = Session.getDefaultInstance(new Properties());
        this.store = session.getStore("imap");
        store.connect(server, port, user, pass);        
    }
    
    public String[] listMailboxes() throws Exception {
        Folder[] folders = store.getDefaultFolder().list();
        String[] ret = new String[folders.length];
        for (int i = 0; i < folders.length; i++) {
            ret[i] = folders[i].getFullName();
        }
        return ret;
    }
    
    public Message[] getMessages(String folderName) throws Exception {
        folder = store.getFolder(folderName);
        folder.open(Folder.READ_ONLY);
      //  Message[] ms = folder.getMessages();
        
        return folder.getMessages();
    }
    
    public void writeMails(String folderName, Message[] m) throws Exception {
        System.out.println("writing messages" + m.length +" to "+folderName);
        folder = store.getFolder(folderName);
        folder.open(Folder.READ_WRITE);
        for (Message message : m) {
            try {
                if(message.getSize() > 4096) {
                    System.out.println("writing a "+message.getSize()+" message with subject "+message.getSubject()); 
                }
                folder.appendMessages(new Message[]{message});
            } catch (Exception e) {
                e.printStackTrace();
                try {
                System.err.println(message.getAllHeaders());
                System.err.println(message.getContent().toString());
                
                } catch (Exception e2) {
                    System.err.println("Exception while printing error");
                }
            }
        }
        System.out.println("closing folder "+folderName);
        folder.close(false);
    }
    
    public void closeFolder() throws Exception {
        folder.close(false);
    }
    
    public void closeStore() throws Exception {
        store.close();
    }
    
    public static void main(String args[]) {
        try {
            IMAPUtil source = new IMAPUtil(); 
            IMAPUtil dest = new IMAPUtil();
            source.connect("mail.buni.org", 143, "acoliver", "fuckit");
            dest.connect("localhost", 9143, "tom", "tom");
            String[] boxes = source.listMailboxes();
            for (int i = 0; i < boxes.length; i++) {
                if (!boxes[i].equals("")) {
                    System.out.println(boxes[i]);
                    dest.writeMails(boxes[i], source.getMessages(boxes[i]));
                    source.closeFolder();
                }
            }
            source.closeFolder();
            dest.closeFolder();
            source.closeStore();
            dest.closeStore();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
