package org.buni.meldware.mail.mailbox;

import java.util.Iterator;

import org.buni.meldware.mail.api.Range;
import org.buni.meldware.mail.api.SearchKey;
import org.buni.meldware.mail.util.ArrayHolder;

public final class FolderInfo {

    private final ArrayHolder uids;
    private final long recent;
    private final long unseen;
    private final long firstUnseen;
    
    public FolderInfo(long[] uids, long recent, long unseen, long firstUnseen) {
        this.uids = new ArrayHolder(uids);
        this.recent = recent;
        this.unseen = unseen;
        this.firstUnseen = firstUnseen;
    }
    
    public long getExists() {
        return uids.size();
    }
    
    public long getRecent() {
        return recent;
    }

    public long getUnseen() {
        return unseen;
    }
    
    public long getFirstUnseen() {
        return firstUnseen;
    }

    public long getLikelyUid() {
        return uids.last() + 1;
    }
    
    public long getMaxUid() {
        return uids.last() + 1;
    }
    
    public int getSeqNum(long uid) {
        return uids.indexOf(uid) + 1;
    }
    
    public int[] getSeqNums(long[] subset) {
        return uids.indexOf(subset, 1);
    }
    
    public boolean isValidSeqNum(int seqNum, long uid) {
        return uids.isValidIndex(seqNum, uid);
        //return seqNum > 0 && seqNum <= uids.length && uid == uids[seqNum-1];
    }
    
    public SearchKey normalise(SearchKey searchKey) {
        searchKey.normalise(uids.getValues());
        return searchKey;
    }
    
    public long normalise(boolean isUid, long id) {
        long maxValue = uids.last();
        long normalised;
        if (id == Range.UNBOUND) {
            normalised = maxValue;
        } else if (isUid) {
            normalised = id;
        } else {
            normalised = getUid(uids.getValues(), (int) id, maxValue);
        }
        return normalised;
    }
    
    private static long getUid(long[] uids, int seqNum, long maxValue) {
        long uid;
        if (seqNum > uids.length) {
            uid = Long.MAX_VALUE;
        } else if (seqNum == -1) {
            uid = maxValue;
        } else if (seqNum < 1) {
            uid = uids[0];
        } else {
            // Remember that sequence numbers are 1 indexed.
            uid = uids[seqNum - 1];
        }
        return uid;
    }
    
    public Range[] normalise(boolean isUid, Range[] ranges) {
        Range[] normalised;
        long maxUid = uids.last();
        if (isUid) {
            normalised = normalise(ranges, maxUid);
        } else {
            normalised = Range.normaliseToUid(ranges, uids.getValues());
        }
        return normalised;
    }
    
    private static Range[] normalise(Range[] ranges, long maxValue) {
        Range[] normalised = new Range[ranges.length];
        for (int i = 0; i < ranges.length; i++) {
            normalised[i] = ranges[i].normalise(maxValue);
        }
        return normalised;
    }

    public Iterator<Long> iterator() {
        return new Iterator<Long>() {
            int idx = 0;

            public boolean hasNext() {
                return idx < uids.size();
            }

            public Long next() {
                Long l = uids.get(idx);
                idx++;
                return l;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public long getUid(int seqNum) {
        int idx = seqNum - 1;
        if (idx >= 0 && idx < uids.size()) {
            return uids.get(idx);
        } else {
            return -1;
        }
    }
    

    public long[] getExpunged(FolderInfo newFolderInfo) {
        return uids.exclude(newFolderInfo.uids);
    }


}
