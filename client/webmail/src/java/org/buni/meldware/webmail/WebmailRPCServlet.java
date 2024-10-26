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
package org.buni.meldware.webmail;

import static org.buni.meldware.webmail.ServletUtil.getLongParameter;
import static org.buni.meldware.webmail.ServletUtil.getParameter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.buni.meldware.calendar.data.Address;
import org.buni.meldware.calendar.interfaces.PIMService;
import org.buni.meldware.calendar.interfaces.PIMServiceUtil;
import org.buni.meldware.common.logging.Log;
import org.buni.meldware.common.preferences.UserProfile;
import org.buni.meldware.common.preferences.UserProfileService;
import org.buni.meldware.mail.abmounts.SystemABMountsService;
import org.buni.meldware.mail.api.Folder;
import org.buni.meldware.mail.api.FolderBody;
import org.buni.meldware.mail.api.FolderMessage;
import org.buni.meldware.mail.api.Hints;
import org.buni.meldware.mail.api.Mailbox;
import org.buni.meldware.mail.api.Range;
import org.buni.meldware.mail.mailbox.FolderSummary;
import org.buni.meldware.mail.maillistener.actions.ActionConfig;
import org.buni.meldware.mail.maillistener.actions.ConditionConfig;
import org.buni.meldware.mail.maillistener.actions.ServerActionsMailListener;
import org.buni.meldware.mail.maillistener.actions.UserActionSet;
import org.buni.meldware.mail.maillistener.actions.UserActionSets;
import org.buni.meldware.mail.message.MailAddress;
import org.buni.meldware.mail.userapi.MailSender;
import org.buni.meldware.mail.util.MMJMXUtil;
import org.buni.meldware.mail.util.Node;
import org.buni.meldware.mail.util.Visitor;
import org.buni.meldware.mail.util.io.Copier;
import org.buni.meldware.mail.util.io.SimpleCopier;
import org.buni.meldware.webmail.command.AddInvitesCommand;
import org.buni.meldware.webmail.command.AddSharedContactCommand;
import org.buni.meldware.webmail.command.ChangeInviteStatusCommand;
import org.buni.meldware.webmail.command.Command;
import org.buni.meldware.webmail.command.CreateEventCommand;
import org.buni.meldware.webmail.command.GetContactsCommand;
import org.buni.meldware.webmail.command.GetDayEventsCommand;
import org.buni.meldware.webmail.command.GetSystemABMounts;
import org.buni.meldware.webmail.command.GetUserABMounts;
import org.buni.meldware.webmail.command.GetUserCommand;
import org.buni.meldware.webmail.command.SaveUserABMounts;
import org.buni.meldware.webmail.command.SearchSharedContactsCommand;
import org.buni.meldware.webmail.command.UpdateEventCommand;
import org.buni.meldware.webmail.command.wcap.FetchComponentsByRange;
import org.buni.meldware.webmail.command.wcap.GetFreeBusyCommand;
import org.buni.meldware.webmail.command.wcap.StoreEventsCommand;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * this is the servlet which implements WMP (Ward Mail Protocol) ;-)
 * 
 * @author James Ward
 * @author Andrew C. Oliver (acoliver ot buni dat org)
 * @author Aron Sogor
 * @author Michael Barker
 */
public class WebmailRPCServlet extends HttpServlet {
     
    private static final long serialVersionUID = 1L;

    private final static Log log = Log.getLog(WebmailRPCServlet.class);

    String mailSender;

    MailSender sender;

    Hashtable<String, Command> commands = null;

    private ServerActionsMailListener actSetService;

    private String userActionSetService;

    private UserProfileService profileService;

    private SystemABMountsService abMountService;

    final static int INAME = 0;

    final static int IID = 1;

    final static int IPID = 2;

    final static int IPATH = 3;

    final static int IMESSAGES = 4;

    final static int IUNREAD = 5;
    
    final static String MAIL_SENDER_SERVICE = "mailSender";

    private static final String USER_ACTION_SET_SERVICE = "actionSetService";
    
    private static final String USER_PROFILE_SERVICE = "userProfileService";
    private static final String SYSTEM_AB_MOUNTS_SERVICE = "abmountService";
    
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        doPost(request, response);
    }

    private Folder getFolder(HttpServletRequest request, String[] path) {
        String user = request.getUserPrincipal().getName();
        Mailbox mailbox = sender.getMailboxManager().createProxy(user, 
                true, new Hints(false, true, true));
        return mailbox.getFolder(path);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
            String s = (String) e.nextElement();
            log.info("Parameter %s:%s", s, request.getParameter(s));
        }
        String contentType = request.getContentType();
        if ((contentType != null) && contentType.equals("application/xml")) {
            WebmailXMLRPC wxr = new WebmailXMLRPC();
            wxr.setMailSenderService(sender);
            wxr.setUserProfileService(this.profileService);
            wxr.setSystemABMountsService(this.abMountService);
            try {
              wxr.process(request, response);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("shit something went wrong with the xml stuff");
            }
            return;
        }

        String op = request.getParameter("op");

        final PrintWriter out = op.equals("GetAttachment") ? null : response.getWriter();

        if (op == null) {
            throw new ServletException("No Operation Specified");
        }

        if (op.equals("logoutUser")) {
            HttpSession ses = request.getSession(false);
            ses.invalidate();
            out.println("<complete/>");
        } else if (op.equals("getFolders")) {
            String user = request.getUserPrincipal().getName();

            //TODO this is the return of some messy auto provisioning.  This is added for purely
            //machiavelian reasons.
            if(sender.getMailboxManager().getMailboxByAlias(user) == null) {
                sender.getMailboxManager().createMailbox(user);
                UserProfile profile = profileService.retrieveUserProfile(user);
                Set<String> aliases = profile.getAliases();
                for (String alias : aliases) {
                    sender.getMailboxManager().createAlias(sender.getMailboxManager().getMailboxIdByAlias(user), alias);
                }
            }
            Node<FolderSummary> root = sender.folderSummary(user);

            Visitor<FolderSummary> printVisitor = new Visitor<FolderSummary>() {
                
                Stack<String> s = new Stack<String>();
                private StringBuilder getParentPath() {
                    StringBuilder sb = new StringBuilder("");
                    for (String str : s) {
                        sb.append(str);
                        sb.append(".");
                    }
                    return sb;
                }
                
                public void visit(Node<FolderSummary> node) {
                    FolderSummary fs = node.getValue();
                    CharSequence path = getParentPath().append(fs.getName());
                    out.print("<folder><id>" + fs.getId() + "</id><name>" + fs.getName() + "</name><path>"
                            + path + "</path>" + "<numTotalEmails>" + fs.getTotal()
                            + "</numTotalEmails>" + "<numUnreadEmails>"
                            + fs.getUnread() + "</numUnreadEmails>" + "</folder>");
                    
                    s.push(fs.getName());
                    for (Node<FolderSummary> child : node) {
                        child.accept(this);
                    }
                    s.pop();
                }
            };
            
            // Don't print the root node.
            for (Node<FolderSummary> n : root) {
                n.accept(printVisitor);
            }            

        } else if (op.equals("getEmailInFolder")) {
            // TODO why doesn't it like our dates? They look the same to me!
            String user = request.getUserPrincipal().getName();

            String pathName = getParameter(request, "folder");
            String[] path = pathName.split("\\.");
            Folder folder = getFolder(request, path);
            
            out.println("<folder path=\"" + pathName + "\"/>");
            
            List<FolderMessage> mails = folder.getMessages();
            DateFormat df = SimpleDateFormat.getDateTimeInstance();
            ((SimpleDateFormat) df).applyPattern("EEE MMM d, yyyy h:mma");

            for (int i = 0; i < mails.size(); i++)
            {
                //FolderMessage mail = mails.get(i);
                FolderMessage mail = folder.getMessage(true, mails.get(i).getUid());
                String id = "" + mail.getUid();
                //String folder = mail.getFolder().getName();
                MailAddress senderma = MailAddress.parseSMTPStyle(mail.getFrom(), 
                        true);
                String sendername = senderma.getPrettyName();
                String sendermail = senderma.getRawAddress();
                String read = mail.isSeen() ? "true" : "false";
                String[] tos = mail.getHeaders("To");
                String[] ccs = mail.getHeaders("Cc");
                String[] bccs = mail.getHeaders("Bcc");
            //    String[] tos = ArrayUtil.toStringArray(mail.getTo());
                // todo cc and bcc
                String subject = mail.getSubject();
                //String date = df.format(mail.getTimestamp());

                out.print("<email><id>" + id + "</id><folderId>" + folder.getId() + "</folderId><folder>" + pathName
                    + "</folder>");
                out.print("<sender><name><![CDATA[" + sendername + "]]></name><address>"
                    + sendermail + "</address></sender>");
                out.print("<read>" + read + "</read>");
                out.print("<recipients>");
                for (int t = 0; t < tos.length; t++)
                {
                    MailAddress ma = MailAddress.parseSMTPStyle(tos[t]);
                    String toname = ma.getPrettyName();
                    String tomail = ma.getRawAddress().replace('<', ' ')
                        .replace('>', ' ').trim();
                    out.print("<to><name>" + toname + "</name><address><![CDATA["
                        + tomail + "]]></address></to>");
                }                
                for (int t = 0; t < ccs.length; t++)
                {
                    MailAddress ma = MailAddress.parseSMTPStyle(ccs[t]);
                    String ccname = ma.getPrettyName();
                    String ccmail = ma.getRawAddress().replace('<', ' ')
                        .replace('>', ' ').trim();
                    out.print("<cc><name>" + ccname + "</name><address><![CDATA["
                        + ccmail + "]]></address></cc>");
                }                
                for (int t = 0; t < bccs.length; t++)
                {
                    MailAddress ma = MailAddress.parseSMTPStyle(bccs[t]);
                    String bccname = ma.getPrettyName();
                    String bccmail = ma.getRawAddress().replace('<', ' ')
                        .replace('>', ' ').trim();
                    out.print("<bcc><name>" + bccname + "</name><address><![CDATA["
                        + bccmail + "]]></address></bcc>");
                }
                Date date = mail.getTimestamp();
                date = date == null ? new Date(1) : date;
                out.print("</recipients>");
                out.print("<subject><![CDATA[" + subject +
                    "]]></subject><date>" +date.getTime() +
                    "</date>");
                out.print("</email>");
            }
        } else if (op.equals("getEmailBody")) {

            long id = getLongParameter(request, "id");
            String pathName = getParameter(request, "folder");
            String[] path = pathName.split("\\.");
            log.info("folder = %s, id = %s", pathName, id);            

            Folder folder = getFolder(request, path);
            FolderMessage message = folder.getMessage(true, id);
            List<FolderBody> parts = message.getBody();
            
            out.print("<email><id>" + id + "</id><folderId>" + folder.getId() + "</folderId><body><![CDATA[");
            if (parts.size() > 0) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                parts.get(0).printText(baos);
                out.print(new String(baos.toByteArray(), "US-ASCII"));
            }
            out.print("]]></body>");
            
            // FIXME: This is broken, needs to recurse the structure.
            List<String> filenameList = new ArrayList<String>();
            for (int i = 1; i < parts.size(); i++) {
                FolderBody part = parts.get(i);
                String filename = part.getFilename();
                if (filename != null) {
                    filenameList.add(filename);
                }
            }
            
            if (!filenameList.isEmpty()) {
            	long messagedataId = message.getId();
                out.print("<attachments>");
                for (int i = 0; i < filenameList.size(); i++) {
                    out.print("<file><filename><![CDATA["+ filenameList.get(i) +
                    		"]]></filename><url><![CDATA["+request.getRequestURL().toString()+"?op=GetAttachment&id="+messagedataId+"&filename="+filenameList.get(i)+
                            "]]></url><id>"+id+"</id></file>");
                }
                out.print("</attachments>");
            }

            out.print("</email>");
        } else if (op.equals("sendEmail")) {
            String user = request.getUserPrincipal().getName();
            // can't we just get the sender information out of the session?
            String from = request.getParameter("from");
            String[] to = request.getParameterValues("to");
            String[] cc = request.getParameterValues("cc");
            String[] bcc = request.getParameterValues("bcc");
            String subject = request.getParameter("subject");
            String body = request.getParameter("body");
            String attachments = request.getParameter("attachments");

            if (to == null)
            {
              to = new String[0];
            }

            if (cc == null)
            {
              cc = new String[0];
            }

            if (bcc == null)
            {
              bcc = new String[0];
            }
            // attachements should already be on the server since we will make
            // the upload happen when the user selects the file the server will
            // pass an attachement id to the client and on sendEmail the client
            // will just send a comma separated list of attachment id's

            // send the email
            System.out.println("sending email:" + "\nto = " + getString(to) + "\ncc = "
                    + getString(cc) + "\nbcc = " + getString(bcc) + "\nsubject = " + subject
                    + "\nbody = " + body);
            sender.send(user, from, to, cc, bcc, subject, body);

            out.println("<complete tabid=\"" + request.getParameter("tabid")
                    + "\"/>");
        } else if (op.equals("deleteEmail")) {
            long id = getLongParameter(request, "id");
            String[] path = getParameter(request, "folder").split("\\.");
            Folder folder = getFolder(request, path);
            folder.setDeleted(true, Range.create(id), true);
            folder.expunge(false);
            out.println("<email><id>" + id + "</id></email>");
        } else if (op.equals("newFolder")) {
            String user = request.getUserPrincipal().getName();
            String foldername = request.getParameter("folder")
                    .replace('.', '/');
            if (sender.createFolder(user, foldername) != null) {
                out.println("<folder><name>" + foldername.replace('/', '.')
                        + "</name></folder>");
            } else {
                // TODO some error thing
            }
        } else if (op.equals("deleteFolder")) {
            String user = request.getUserPrincipal().getName();
            String foldername = request.getParameter("folder")
                    .replace('.', '/');
            sender.deleteFolder(user, foldername);
            out.println("<folder><name>" + foldername.replace('.', '/')
                    + "</name></folder>");
        } else if (op.equals("moveFolder")) {
            String user = request.getUserPrincipal().getName();
            String foldername = request.getParameter("folder")
                    .replace('.', '/');
            String targetname = request.getParameter("targetname").replace('.',
                    '/');
            if (sender.moveFolder(user, foldername, targetname) != null) {
                out.print("<folder><name>" + foldername + "</name></folder>");
                out.print("<folder><name>" + targetname + "</name></folder>");
            } else {
                // TODO some error thing
            }
        } else if (op.equals("saveToFolder")) {
            // can't we just get the sender information out of the session?
            String from = request.getParameter("from");
            String[] to = request.getParameterValues("to");
            String[] cc = request.getParameterValues("cc");
            String[] bcc = request.getParameterValues("bcc");
            String subject = request.getParameter("subject");
            String body = request.getParameter("body");
            String attachments = request.getParameter("attachments");
            String[] path = getParameter(request, "foldername").split("\\.");
            Folder folder = getFolder(request, path);
            
            // send the email
            System.out.println("sending email:" + "\nto = " + to + "\ncc = "
                    + cc + "\nbcc = " + bcc + "\nsubject = " + subject
                    + "\nbody = " + body);
            folder.append(from, to, cc, bcc, subject, body);
            out.println("<complete tabid=\""
                  + request.getParameter("tabid") + "\"/>");
        } else if (op.equals("getAliases")) {
            String user = request.getUserPrincipal().getName();
            List<String> aliases = sender.getAliases(user);
            if (aliases != null) {
                for (int i = 0; i < aliases.size(); i++) {
                    if(aliases.get(i).indexOf("@") > -1) {
                        out.print("<user><email>" + aliases.get(i)
                                + "</email></user>");
                    }
                }
            } else {
                // TODO some error thing
            }
        } else if (op.equals("moveMail")) {
            String[] fromPath = getParameter(request, "fromFolder").split("\\.");
            String[] targetPath = getParameter(request, "targetname").split("\\.");
            long uid = getLongParameter(request, "id");
            
            Folder from = getFolder(request, fromPath);
            Folder target = getFolder(request, targetPath);
            
            Range[] r = Range.create(uid);
            from.copy(target, true, r);
            from.setDeleted(true, r, true);
            from.expunge(false);
            
            out.println("<complete fromFolder=\""
                    + request.getParameter("fromFolder") + "\"/>");
            
        } else if (op.equals("deleteTrash")) {
            
            Folder trash = getFolder(request, new String[] { "Trash" });
            trash.setDeleted(true, Range.ALL, true);
            trash.expunge(false);
            out.println("<complete/>");
            
        } else if (op.equals("saveContact")) {
            Address address = new Address();
            address.setRecordId(Long
                    .parseLong(request.getParameter("recordId")));
            address.setFullName(request.getParameter("contactName"));
            address.setEmailAddress(request.getParameter("email"));
            address.setOrganization(request.getParameter("organization"));
            address.setNickName(request.getParameter("shortName"));
            address.setHomePhone(request.getParameter("homePhone"));
            address.setMobilePhone(request.getParameter("mobilePhone"));
            address.setOfficePhone(request.getParameter("officePhone"));
            address.setFax(request.getParameter("faxNumber"));

            try {
                PIMService pim = PIMServiceUtil.getHome().create();
               /* if (address.getRecordId() < 0) {
                    pim.addAddresses(new Address[] { address });
                } else {
                    address = pim.updateAddresses(new Address[] { address })[0];
                }*/

                out.println("<complete tabid=\""
                        + request.getParameter("tabid") + "\"/>");
                pim.remove();
            } catch (Exception e) {
                // TODO error handling
            }
        } else if (op.equals("deleteContact")) {
            String user = request.getUserPrincipal().getName();
            PIMService pim;
            try {
                pim = PIMServiceUtil.getHome().create();
                pim.removeAddresses(new long[] { Long.parseLong(request
                        .getParameter("recordId")) });
                out.println("<complete/>");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (op.equals("savePreferences")) {
            String contactName = request.getParameter("contactName");
            String primaryEmail = request.getParameter("primaryEmail");
            String timeZone = request.getParameter("timeZone");
        } else if (op.equals("GetAttachment")) {
            String sid = request.getParameter("id");
            String file = request.getParameter("filename"); 
            long id = Long.parseLong(sid);
            InputStream stream = sender.getMailAttachment(id,file);
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition",
                                "attachment; filename="+file);
            Copier copier = new SimpleCopier();
            OutputStream ostream = response.getOutputStream();
            copier.copy(new Base64InputStream(stream), ostream, 10*1024); //todo make configurable block size
            stream.close();
            ostream.flush();
          //  out.flush();
        } else if (op.equals("getUserActionSets")) {
            String user = request.getUserPrincipal().getName();
            UserActionSets uas = actSetService.retrieveUserActionSets(user);
            if (uas == null) {
                actSetService.createUserActionSets(new UserActionSets(actSetService.generateUID(),user), user);
                return;
            }
            List<UserActionSet> ua = uas.getUserActionSets();
            for (UserActionSet set : ua) {
                String name = set.getName();
                List<ActionConfig>actions = set.getActions();
                List<ConditionConfig>conditions = set.getConditions();
                boolean all = set.getAll();
                String conditionsString = "";
                String actionsString = "";
                
                for (ConditionConfig config : conditions) {
                    conditionsString+=field("condition",field("headerName",config.getHeader())+
                                                       field("conditionSymbol", config.getName())+
                                                       field("value",config.getValue()));
                }
                
                for (ActionConfig config : actions) {
                    actionsString += field("action", field("actionName",config.getName())+
                                                    field("folderName",config.getParams()));
                }
                 
                out.println(field("userActionSet", field("name", name)+
                                                   field("all",all) + 
                                                   conditionsString + actionsString));
            }
        } else if (op.equals("saveUserActionSets")) {
            String xml = request.getParameter("actionset");
            String name = request.getParameter("user");
            xml = "<userActionSets>"+xml+"</userActionSets>";
            System.out.println(xml);
            Element e = parse(xml);
            NodeList list = e.getElementsByTagName("userActionSet");
            UserActionSets uas = actSetService.retrieveUserActionSets(name);
            if (uas != null) { 
                actSetService.deleteUserActionSets(uas);
            }
            uas = new UserActionSets(actSetService.generateUID(),name);
            List<UserActionSet> actSetList = new ArrayList<UserActionSet>();
            for (int i = 0; i < list.getLength(); i++) {
                Element asElement = (Element)list.item(i);
                NodeList actlist = asElement.getElementsByTagName("action");
                String asName = asElement.getElementsByTagName("name").item(0).getTextContent();
                String asAll = asElement.getElementsByTagName("all").item(0).getTextContent();
                boolean all = Boolean.parseBoolean(asAll);
                List<ActionConfig> actions = new ArrayList<ActionConfig>();
                for (int x = 0; x < actlist.getLength(); x++) {
                    Element actElement = (Element) actlist.item(x);
                    String actionName = actElement.getElementsByTagName("actionName").item(0).getTextContent();
                    String folderName = actElement.getElementsByTagName("folderName").item(0).getTextContent().replace(".", "/");
                    ActionConfig ac = new ActionConfig(actSetService.generateUID(),actionName,folderName);
                    actions.add(ac);
                }
                NodeList conList = asElement.getElementsByTagName("condition");
                List<ConditionConfig> conditions = new ArrayList<ConditionConfig>();
                for (int x = 0; x < conList.getLength(); x++) {
                    Element conElement = (Element) conList.item(x);
                    String conditionSymbol = conElement.getElementsByTagName("conditionSymbol").item(0).getTextContent();
                    String headerName = conElement.getElementsByTagName("headerName").item(0).getTextContent();
                    String value = conElement.getElementsByTagName("value").item(0).getTextContent();
                    ConditionConfig cc = new ConditionConfig(actSetService.generateUID(),true,false,conditionSymbol,headerName,value);
                    conditions.add(cc);
                }
                UserActionSet ua = new UserActionSet(actSetService.generateUID(),asName,all);
                ua.setConditions(conditions);
                ua.setActions(actions);
                actSetList.add(ua);
            }
            uas.setUserActionSets(actSetList);
            actSetService.createUserActionSets(uas, name);
            out.println("<status><code>0</code><message>Success!</message></status>");
        } else {
            // I hope to refactor to white elefant command framework
            Command command = (Command) this.commands.get(op);
            if (command != null) {
                String result = command.execute(request, out);
                // JAMES ?? How do I trigger the responder onFault?
                if (result.equals(Command.ERROR))
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            } else
                throw new ServletException("Operation Not Recognized");

        }
        
        if (out != null) {
            out.flush();
        }

    }

    public Element parse(String val) {
        try {
            DocumentBuilder bld = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document adoc = bld.parse(new ByteArrayInputStream(val.getBytes()));
            return (Element)adoc.getFirstChild();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void init() throws ServletException {
        mailSender = getInitParameter(MAIL_SENDER_SERVICE); 
        sender = (MailSender) MMJMXUtil.getMBean(mailSender,
                MailSender.class);
        userActionSetService = getInitParameter(USER_ACTION_SET_SERVICE);
        this.actSetService = (ServerActionsMailListener)MMJMXUtil.getMBean(userActionSetService, ServerActionsMailListener.class);
        String userProfileService = getInitParameter(USER_PROFILE_SERVICE);
        this.profileService = (UserProfileService)MMJMXUtil.getMBean(userProfileService, UserProfileService.class);
        String abmounts = getInitParameter(SYSTEM_AB_MOUNTS_SERVICE);
        this.abMountService = (SystemABMountsService)MMJMXUtil.getMBean(abmounts, SystemABMountsService.class);
        this.commands = new Hashtable<String, Command>();
        this.commands.put(CreateEventCommand.COMMAND_NAME,
                new CreateEventCommand(sender));
        this.commands.put(ChangeInviteStatusCommand.COMMAND_NAME,
                new ChangeInviteStatusCommand(sender));
        this.commands.put(GetContactsCommand.COMMAND_NAME,
                new GetContactsCommand(sender));
        GetUserCommand guc =  new GetUserCommand(sender);
        guc.profileService = this.profileService;
        this.commands.put(GetUserCommand.COMMAND_NAME,guc);
        this.commands.put(SearchSharedContactsCommand.COMMAND_NAME,
                new SearchSharedContactsCommand(sender));
        this.commands.put(UpdateEventCommand.COMMAND_NAME,
                new UpdateEventCommand(sender));
        this.commands.put(AddSharedContactCommand.COMMAND_NAME,
                new AddSharedContactCommand(sender));
        this.commands.put(AddInvitesCommand.COMMAND_NAME,
                new AddInvitesCommand(sender));
        this.commands.put(GetDayEventsCommand.COMMAND_NAME,
                new GetDayEventsCommand(sender));
        StoreEventsCommand sec = new StoreEventsCommand(sender);
        sec.profileService = this.profileService;
        this.commands.put(StoreEventsCommand.COMMAND_NAME,
                sec);
                
        this.commands.put(FetchComponentsByRange.COMMAND_NAME,
                new FetchComponentsByRange(sender));  
        this.commands.put(GetFreeBusyCommand.COMMAND_NAME, new GetFreeBusyCommand(sender,profileService));
        
        this.commands.put(GetUserABMounts.COMMAND_NAME, new GetUserABMounts(sender, profileService));

        this.commands.put(SaveUserABMounts.COMMAND_NAME, new SaveUserABMounts(sender, profileService));

        this.commands.put(GetSystemABMounts.COMMAND_NAME, new GetSystemABMounts(sender, profileService, abMountService));
        this.commands.put(GetSuggestions.COMMAND_NAME, new GetSuggestions(sender, profileService, abMountService));
 
    }
    
    private String field(String name, Object data) {
        return "<" + name + ">" + data.toString() + "</" + name + ">";
    }

    private String getString(String[] s)
    {
      String r = "[";
      for (int i = 0; i < s.length; i++)
      {
        r += s[i];
      }
      r += "]";

      return r;
    }
}
