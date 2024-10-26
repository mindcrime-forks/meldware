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
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
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
package org.buni.meldware.mail.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.buni.meldware.mail.util.io.IOUtil;

/**
 * Refactored version of the Mail Headers.  Contains 3 lists of MailHeader items
 * One containing the trace headers, one containing resent headers and one containing
 * all other headers.  Order is maintained within each of these lists and the
 * iterator will ensure that the order of the lists is trace, resent, other.
 * A hash map containing a index of the headers by header name is also maintained
 * to speed random access to header items.  Header name indexing is case insensitive.
 * 
 * @author Michael Barker
 * @version $Revision: 1.4 $
 * 
 */
public class MailHeadersImpl implements MailHeaders, Iterable<String>, Serializable {

    private static final long serialVersionUID = -5172463342345104968L;

    private final static Set<String> TRACE_HEADER_KEYS;

    private final static Set<String> RESENT_HEADER_KEYS;

    static {
        Set<String> traceTmp = new HashSet<String>();
        traceTmp.add("Received");
        traceTmp.add("Return-Path");
        TRACE_HEADER_KEYS = Collections.unmodifiableSet(traceTmp);

        Set<String> resentTmp = new HashSet<String>();
        resentTmp.add("Resent-Date");
        resentTmp.add("Resent-From");
        resentTmp.add("Resent-Sender");
        resentTmp.add("Resent-To");
        resentTmp.add("Resent-Cc");
        resentTmp.add("Resent-Bcc");
        resentTmp.add("Resent-Message-Id");
        RESENT_HEADER_KEYS = Collections.unmodifiableSet(resentTmp);
    }

    private final List<MailHeader> traceHeaders;

    private final List<MailHeader> resentHeaders;

    private final List<MailHeader> headers;

    private final Map<String, List<MailHeader>> headerIndex;

    public MailHeadersImpl() {
        traceHeaders = new ArrayList<MailHeader>();
        resentHeaders = new ArrayList<MailHeader>();
        headers = new ArrayList<MailHeader>();
        headerIndex = new HashMap<String, List<MailHeader>>();
    }

    private final static String[] EMPTY = {};
    /**
     * Gets the values for a given header name.
     * 
     * @see org.buni.meldware.mail.message.MailHeaders#getHeader(java.lang.String)
     */
    public String[] getHeader(String name) {
        String lname = name.toLowerCase();
        List<MailHeader> headers = headerIndex.get(lname);
        String[] result = EMPTY;
        if (headers != null) {
            result = new String[headers.size()];
            for (int i = 0; i < result.length; i++) {
                result[i] = headers.get(i).getValue();
            }            
        }
        
        return result;
    }

    /**
     * Adds a new header to this set of mail headers
     * 
     * @see org.buni.meldware.mail.message.MailHeaders#addHeader(java.lang.String,
     *      java.lang.String)
     */
    public int addHeader(String name, String value) {
        MailHeader mh = new MailHeader(name, value);
        add(mh);
        return numHeaders();
    }

    /**
     * Adds a mail header. This will not update an existing header.
     * 
     * @param mh
     */
    public void add(MailHeader mh) {
        indexHeader(mh);
        getHeaderList(mh.getName()).add(mh);
    }

    /**
     * Adds the name/value pair as a header if it does not exist.
     * 
     * @param name
     * @param value
     */
    public boolean addIfAbsent(String name, String value) {
        String lname = name.toLowerCase();
        if (!headerIndex.containsKey(lname)) {
            addHeader(name, value);
            return true;
        }
        return false;
    }

    /**
     * Adds the associated header if it does not exist.
     * 
     * @param mh
     */
    public boolean addIfAbsent(MailHeader mh) {
        if (headerIndex.containsKey(mh.getName())) {
            add(mh);
            return true;
        }
        return false;
    }

    /**
     * Add a header to the index.
     * 
     * @param mh
     */
    private void indexHeader(MailHeader mh) {
        String lname = mh.getName().toLowerCase();
        List<MailHeader> values = headerIndex.get(lname);
        if (values == null) {
            values = new ArrayList<MailHeader>();
            headerIndex.put(lname, values);
        }
        values.add(mh);
    }

    /**
     * Return the total number of headers.
     * 
     * @return
     */
    public int numHeaders() {
        return traceHeaders.size() + resentHeaders.size() + headers.size();
    }

    /**
     * Return the total size of the headers in bytes.
     * 
     * TODO: This could be cached.
     * 
     * @return
     * @throws UnsupportedEncodingException 
     */
    public int size(String encoding) throws UnsupportedEncodingException {
        int size = 0;
        for (String header : this) {
            size += header.getBytes(encoding).length + 2;
        }
        return size;
    }
    
    /**
     * Gets the approriate list of headers based on the name of the key.
     * 
     * @param key
     * @return
     */
    private List<MailHeader> getHeaderList(String key) {
        List<MailHeader> result;
        if (isTraceHeader(key)) {
            result = traceHeaders;
        } else if (isResentHeader(key)) {
            result = resentHeaders;
        } else {
            result = headers;
        }
        return result;
    }

    /**
     * Creates an iterator that returns all of the header lines in the
     * appropriate order.
     */
    @SuppressWarnings("unchecked")
    public Iterator getAllHeaderLines() {
        return new HeaderIterator(traceHeaders, resentHeaders, headers);
    }
    
    @SuppressWarnings("unchecked")
    public Iterator<String> iterator() {
        return new HeaderIterator(traceHeaders, resentHeaders, headers);
    }

    /**
     * Removes all headers of the specified name.
     * 
     * @see org.buni.meldware.mail.message.MailHeaders#removeHeader(java.lang.String)
     */
    public void removeHeader(String name) {
        String lname = name.toLowerCase();
        List<MailHeader> indexedHeaders = headerIndex.remove(lname);
        if (indexedHeaders != null) {
            List<MailHeader> headers = getHeaderList(name);
            for (MailHeader header : indexedHeaders) {
                headers.remove(header);
            }            
        }
    }

    private static boolean isResentHeader(String key) {
        return RESENT_HEADER_KEYS.contains(key);
    }

    private static boolean isTraceHeader(String key) {
        return TRACE_HEADER_KEYS.contains(key);
    }
    
    /**
     * Returns the headers as a string.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        for (String header : this) {
            sb.append(header);
            sb.append("\r\n");
        }
        
        return sb.toString();
    }
    
    /**
     * Find the mime boundry string in the content-type header
     * @param header from ih which contains "boundary" potentially
     * @return null if there is NO boundary entry or the actual boundry entry.
     */
    public String getBoundary() {
        
        String retval = null;
        String[] header = getHeader("Content-Type");
        
        try {
            for (int i = 0; header != null && i < header.length; i++) {
                if( header[i].indexOf("boundary=\"") >-1) {
                    String[] temp =  header[i].split("boundary\\=\\\"");
                    String boundary = temp[temp.length-1];
                    boundary = "--"+boundary.split("\\\"")[0];
                    retval = boundary;
                    break;
                }
            }
        } catch (Exception e) {
            //don't do anything we'll just NOT parse the mime.
        }
        return retval;
    }
    
    /**
     * Concatenate max n headers together.
     * 
     * @param name
     * @param max
     * @return
     */
    private String getHeader(String name, int max) {
        assert max > 0;
        
        String[] headers = getHeader(name);
        String header = null;
        
        if (headers != null && headers.length > 0) {
            if (max == 1) {
                header = headers[0];                
            } else if (max > 1){
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < max && i < headers.length; i++) {
                    sb.append(headers[i]);
                    int nextVal = i + 1;
                    if (nextVal < max && nextVal < headers.length) {
                        sb.append(" ");
                    }
                }
            }
            // else header == null.
        }
        
        return header;
    }
    
    public String getTo() {
        return getHeader(StandardMailHeaders.TO, 1);
    }
    
    public String getCC() {
        return getHeader(StandardMailHeaders.CC, 1);
    }
    
    public String getBCC() {
        return getHeader(StandardMailHeaders.BCC, 1);
    }
    
    public String getFrom() {
        return getHeader(StandardMailHeaders.FROM, 1);
    }
    
    public String getSender() {
        return getHeader(StandardMailHeaders.SENDER, 1);
    }
    
    public String getSubject() {
        return getHeader(StandardMailHeaders.SUBJECT, 1);
    }
    
    public String getMessageId() {
        return getHeader(StandardMailHeaders.MESSAGE_ID, 1);
    }
    
    public String getInReplyTo() {
        return getHeader(StandardMailHeaders.IN_REPLY_TO, 1);
    }
    
    public String getReplyTo() {
        return getHeader(StandardMailHeaders.REPLY_TO, 1);
    }
    
    public String getDate() {
        return getHeader(StandardMailHeaders.DATE, 1);
    }
    
    
    /**
     * Iterates through and array of MailHeader lists in order that the lists
     * are passed to the constructor.
     * 
     * @author Michael Barker
     * 
     */
    private static class HeaderIterator implements Iterator<String> {

        private List<MailHeader>[] lists;

        private int listIndex = 0;

        private Iterator<MailHeader> currentItr = null;

        public HeaderIterator(List<MailHeader>... lists) {
            this.lists = lists;
        }

        /**
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext() {

            boolean result;

            if (lists.length == 0) {
                result = false;
            } else {
                if (currentItr == null) {
                    currentItr = lists[listIndex].iterator();
                    listIndex++;
                }

                if (!currentItr.hasNext()) {
                    result = false;
                    while (listIndex < lists.length && !currentItr.hasNext()) {
                        currentItr = lists[listIndex].iterator();
                        listIndex++;
                        if (currentItr.hasNext()) {
                            result = true;
                        }
                    }
                } else {
                    result = true;
                }
            }

            return result;
        }

        /**
         * @see java.util.Iterator#next()
         */
        public String next() {

            if (hasNext()) {
                MailHeader mh = currentItr.next();
                return mh.toString();
            } else {
                throw new NoSuchElementException();
            }

        }
        
        public MailHeader nextHeader() {
            
            if (hasNext()) {
                return currentItr.next();
            } else {
                throw new NoSuchElementException();
            }
            
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

    /**
     * An individual mail header with a name and a value.
     * 
     * @author Michael Barker
     *
     */
    private static class MailHeader implements Serializable {

        private static final long serialVersionUID = 4191813405715324590L;

        private String name;

        private String value;

        public MailHeader(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public MailHeader(String name) {
            this(name, "");
        }

        public void append(String value) {
            this.value += value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }

        /**
         * Returns the header line
         */
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(name);
            sb.append(": ");
            sb.append(value);
            //return TextUtil.foldLine(sb, 76, ' ').toString();
            return sb.toString();
        }
    }


    /**
     * Loads the headers in this MailHeaderImpl.
     */
    public int load(InputStream in, String charSet) throws IOException {
        int numRead = 0;
        
        StringBuilder sb = new StringBuilder();
        
        // Skip any empty lines.
        // Return if we only received empty lines followed by EOF.
        while (sb.length() == 0) {
            int bytesRead = IOUtil.appendLine(sb, in, charSet);
            if (bytesRead == 0) {
                return numRead;
            }
            numRead += bytesRead;
        }
        
        MailHeader mh = null;
        
        do {
            String s = sb.toString();
            if (s.startsWith(" ") || s.startsWith("\t")) {
                if (mh != null) {
                    // Maintain the newline.
                    mh.append("\r\n");
                    mh.append(s);
                } else {
                    throw new MalformedHeaderException("Malformed Header: " + s);
                }
            } else {
                int idx = s.indexOf(':');
                if (idx > -1) {
                    String name = s.substring(0, idx);
                    String value = s.substring(idx + 1).trim();
                    mh = new MailHeader(name, value);
                    add(mh);
                } else {
                    throw new MalformedHeaderException("Malformed Header: " + s);
                }
            }
            
            sb.delete(0, sb.length());
            numRead += IOUtil.appendLine(sb, in, charSet);
        } while (sb.length() > 0);
        
        return numRead;
    }
    
    /**
     * Loads the headers from an array of strings.  This should
     * not contain any body information.  Empty lines are skipped.
     * 
     * @param lines
     * @return
     */
    public void load(String[] lines) {
        
        MailHeader mh = null;
        
        for (String s : lines) {
            if (s.trim().length() != 0) {
                if (s.startsWith(" ") || s.startsWith("\t")) {
                    if (mh != null) {
                        // Maintain the newline.
                        mh.append("\r\n");
                        mh.append(s);
                    } else {
                        throw new MalformedHeaderException("Malformed Header: " + s);
                    }
                } else {
                    int idx = s.indexOf(':');
                    if (idx > -1) {
                        String name = s.substring(0, idx);
                        String value = s.substring(idx + 1).trim();
                        mh = new MailHeader(name, value);
                        add(mh);
                    } else {
                        throw new MalformedHeaderException("Malformed Header: " + s);
                    }
                }
            }
        }
        
    }
    
    public static MailHeaders create() {
        return new MailHeadersImpl();
    }
    
    /**
     * Creates Mail Headers from the input stream.
     * 
     * XXX Pass in CharSet
     * 
     * @param in
     * @param additionalHeaders
     * @param traceEntry
     * @return
     * @throws IOException
     */
    public static MailHeadersImpl create(InputStream in) throws IOException {        
        MailHeadersImpl mhs = new MailHeadersImpl();
        mhs.load(in, Mail.ENCODING);
        return mhs;
    }
    
    /**
     * Creates a mail headers object from an array of strings.  This
     * array should not contain the body.  Empty lines are skipped.
     * 
     * @param headers
     * @return
     */
    public static MailHeadersImpl create(String[] headers) {
        MailHeadersImpl mhs = new MailHeadersImpl();
        mhs.load(headers);
        return mhs;
    }


}
