/**
 * 
 */
package org.buni.meldware.mail.protocol;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * @author Michael.Barker
 *
 */
public class ProtocolHandler {

    private static final long serialVersionUID = 1L;
    private static final byte[] ENDL = { '\r', '\n' };
    private Socket s;
    private InputStream in;
    private OutputStream out;
    private InputReader ir;
    private final static long TIMEOUT = 15000;
    private Thread t;

    public ProtocolHandler() {
    }
    
    public void connect(String host, int port) {
        try {
            System.out.println("* Open Socket *");
            s = new Socket(host, port);
            in = s.getInputStream();
            out = s.getOutputStream();
            ir = new InputReader(in);
            t = new Thread(ir);
            t.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }        
    }
    
    public void disconnect() throws IOException {
        t.interrupt();
        s.close();
    }
    
    /**
     * Sends a string to a remote server
     * 
     * @param s
     */
    public void send(String s) throws IOException {
        
        for (String line : s.split("\n")) {
            out.write(line.trim().getBytes());
            out.write(ENDL);
            System.out.println("C: " + line);            
        }
    }
    
    public void sendFile(String filename) throws IOException {
        
        BufferedReader r = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = r.readLine()) != null) {
            send(line);
        }
    }
    
    public void dumpFile(String filename) throws IOException {
        
        InputStream in = new FileInputStream(filename);
        byte[] data = new byte[8192];
        for (int numRead = 0; (numRead = in.read(data)) != -1;) {
            out.write(data, 0, numRead);
        }
    }
    
    public String next() throws InterruptedException, IOException {
        return ir.next();
    }
    
    public String current() {
        return ir.current();
    }
    
    public void fail(String msg) throws ValidationException {
        throw new ValidationException(msg);
    }
    
    public void expect(String pattern) 
        throws InterruptedException, IOException, ValidationException {
        
        String line = ir.next();
        if (!line.matches(pattern)) {
            String msg = String.format("Expected: %s, Actual: %s", pattern, line);
            throw new ValidationException(msg);
        }
    }
    
    public void scan(String pattern) throws InterruptedException, IOException {
        String input;
        do {
            input = ir.next();
        } while (!Pattern.matches(pattern, input));
    }
        
    private static class InputReader implements Runnable {

        private InputStream in;
        private final BlockingQueue<String> q = new ArrayBlockingQueue<String>(20);
        private boolean hasError = false;
        private String msg;
        private String current;

        public InputReader(InputStream in) {
            this.in = in;
        }
        
        public String next() throws InterruptedException, IOException {
            //String val = null;
            if (!hasError) {
                //return q.take();                
                current = q.poll(TIMEOUT, TimeUnit.MILLISECONDS);
                System.out.println("S: " + current);
                if (current == null) {
                    throw new IOException("Timeout exceed while wait on response");
                }
            } else {
                throw new IOException("Failed to read server response: " + msg);
            }
            return current;
        }
        
        public String current() {
            return current;
        }
        
        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {
            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            String line;
            try {
                while ((line = r.readLine()) != null) {
                    //System.out.println("S: " + line);
                    q.put(line);
                }                
            } catch (SocketException e) {
                System.out.println("* Closed Socket *");
            } catch (Exception e) {
                hasError = true;
                msg = e.getMessage();
                e.printStackTrace();
            }
        }
        
    }
}
