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
package org.buni.meldware.mail.mailbox;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.buni.meldware.common.util.ArrayUtil;
import org.buni.meldware.mail.api.FolderMessage;

/**
 * List of default and custom flags.  Default flags are "optimized" into table members where 
 * custom flags are in a collection (and hence have their own table)
 * 
 * @author Andrew C. Oliver
 * @version $Revision: 1.6 $
 */
public class FlagList {

    public static final String DELETED_FLAG = "\\DELETED";

    public static final String ANSWERED_FLAG = "\\ANSWERED";

    public static final String SEEN_FLAG = "\\SEEN";

    public static final String DRAFT_FLAG = "\\DRAFT";

    public static final String FLAGGED_FLAG = "\\FLAGGED";

    public static final String RECENT_FLAG = "\\RECENT";
    
    public static final String SPAM_FLAG = "JUNK";
    
    public static final String NOT_SPAM_FLAG = "NONJUNK";

    private FolderEntry msg;

    FlagList(FolderEntry msg) {
        this.msg = msg;
    }
    
    private static void addSpamFlag(List<String> ourFlags, FolderEntry fe) {
        if (fe != null && ourFlags != null && fe.getUserSpamState() != null) {
            switch(fe.getUserSpamState()) {
            case SPAM:
                ourFlags.add(FlagList.SPAM_FLAG);
                break;
            case NOT_SPAM:
                ourFlags.add(FlagList.NOT_SPAM_FLAG);
                break;
            }
        }
    }
    
    public List<String> getFlagList() {
        List<String> ourFlags = new ArrayList<String>();
        if (msg.isAnswered()) {
            ourFlags.add(FlagList.ANSWERED_FLAG);
        }
        if (msg.isDeleted()) {
            ourFlags.add(FlagList.DELETED_FLAG);
        }
        if (msg.isRecent()) {
            ourFlags.add(FlagList.RECENT_FLAG);
        }
        if (msg.isSeen()) {
            ourFlags.add(FlagList.SEEN_FLAG);
        }
        if (msg.isFlagged()) {
            ourFlags.add(FlagList.FLAGGED_FLAG);
        }
        if (msg.isDraft()) {
            ourFlags.add(FlagList.DRAFT_FLAG);
        }
        addSpamFlag(ourFlags, msg);
        Iterator i = msg.getFlags().iterator();
        while (i.hasNext()) {
            ourFlags.add(i.next().toString());
        }
        return ourFlags;
    }

    public boolean isSet(String valz) {
        String val = valz.toUpperCase();
        if (val.equals(DELETED_FLAG)) {
            return msg.isDeleted();
        } else if (val.equals(SEEN_FLAG)) {
            return msg.isSeen();
        } else if (val.equals(RECENT_FLAG)) {
            return msg.isRecent();
        } else if (val.equals(ANSWERED_FLAG)) {
            return msg.isAnswered();
        } else if (val.equals(FLAGGED_FLAG)) {
            return msg.isFlagged();
        } else if (val.equals(DRAFT_FLAG)) {
            return msg.isDraft();
        } else if (val.equals(SPAM_FLAG)) {
            return msg.getUserSpamState() == FolderMessage.SpamState.SPAM;
        } else if (val.equals(NOT_SPAM_FLAG)) {
            return msg.getUserSpamState() == FolderMessage.SpamState.NOT_SPAM;
        }

        Iterator<Flag> i = msg.getFlags().iterator();
        while (i.hasNext()) {
            Flag f = i.next();
            String value = f.getValue();
            if (value.equals(val)) {
                return true;
            }
        }
        return false;
    }

    public String toFlagString() {
        List<String> flags = getFlagList();   
        return ArrayUtil.join(flags, " ");
    }

    /**
     * @param b
     * @param flags
     */
    public void setFlags(boolean b, List<String> flags) {
        for (int i = 0; i < flags.size(); i++) {
            String theflag = flags.get(i);
            if (!this.isSet(theflag) && b) {
                this.addFlag(theflag);
            } else if (this.isSet(theflag) && !b) {
                this.removeFlag(theflag);
            }
        }
    }
    
    /**
     * Replace all of the flags in the flag list.  Recent is always maintained.
     * 
     * @param flags
     */
    public void setFlags(List<String> flags) {
        boolean recent = msg.isRecent();
        clearFlags();
        msg.setRecent(recent);
        setFlags(true, flags);
    }
    
    
    private void clearFlags() {
        msg.setRecent(false);
        msg.setAnswered(false);
        msg.setDeleted(false);
        msg.setSeen(false);
        msg.setFlagged(false);
        msg.setDraft(false);
        msg.setUserSpamState(FolderMessage.SpamState.UNKNOWN);
        msg.setSystemSpamState(FolderMessage.SpamState.UNKNOWN);
        msg.flags.clear();
    }

    /**
     * @param theflag
     */
    private void removeFlag(String flag) {
        String theflag = flag.toUpperCase();
        if (theflag.equals(ANSWERED_FLAG)) {
            msg.setAnswered(false);
        } else if (theflag.equals(DELETED_FLAG)) {
            msg.setDeleted(false);
        } else if (theflag.equals(RECENT_FLAG)) {
            msg.setRecent(false);
        } else if (theflag.equals(SEEN_FLAG)) {
            msg.setSeen(false);
        } else if (theflag.equals(FLAGGED_FLAG)) {
            msg.setFlagged(false);
        } else if (theflag.equals(DRAFT_FLAG)) {
            msg.setDraft(false);
        } else if (theflag.equals(SPAM_FLAG) || theflag.equals(NOT_SPAM_FLAG)) {
            msg.setUserSpamState(FolderMessage.SpamState.UNKNOWN);
            msg.setSystemSpamState(FolderMessage.SpamState.UNKNOWN);
        } else {
            this.msg.getFlags().remove(new Flag(theflag));
        }
    }

    /**
     * @param theflag
     */
    private void addFlag(String flag) {
        String theflag = flag.toUpperCase();
        if (theflag.equals(ANSWERED_FLAG)) {
            msg.setAnswered(true);
        } else if (theflag.equals(DELETED_FLAG)) {
            msg.setDeleted(true);
        } else if (theflag.equals(RECENT_FLAG)) {
            msg.setRecent(true);
        } else if (theflag.equals(SEEN_FLAG)) {
            msg.setSeen(true);
        } else if (theflag.equals(FLAGGED_FLAG)) {
            msg.setFlagged(true);
        } else if (theflag.equals(DRAFT_FLAG)) {
            msg.setDraft(true);
        } else if (theflag.equals(SPAM_FLAG)) {
            msg.setUserSpamState(FolderMessage.SpamState.SPAM);
            msg.setSystemSpamState(FolderMessage.SpamState.SPAM);
        } else if (theflag.equals(NOT_SPAM_FLAG)) {
            msg.setUserSpamState(FolderMessage.SpamState.NOT_SPAM);
            msg.setSystemSpamState(FolderMessage.SpamState.NOT_SPAM);
        } else {
            Flag msgFlag = new Flag(theflag);
            msgFlag.setFolderEntry(msg);
            msg.getFlags().add(msgFlag);
        }
    }
}
