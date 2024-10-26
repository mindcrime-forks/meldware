package org.buni.meldware.mail;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.buni.meldware.common.util.ArrayUtil;

/**
 * Timing service for protocols.  Launches a single threaded executor and
 * updates its performance information asynchronously.
 * 
 * @author Michael Barker
 * @version $Revision: 1.2 $
 *
 */
public class ProtocolTimer {

    Executor e = Executors.newSingleThreadExecutor();
    
    public String printPerformanceStats() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String,TimingInfo> e : timing.entrySet()) {
            sb.append(e.getKey());
            sb.append("\n====\n");
            sb.append(e.getValue());
            sb.append("\n");
            sb.append(ArrayUtil.join(e.getValue().getTop(), "\n"));
            sb.append("\n\n");
        }
        return sb.toString();
    }
    
    ConcurrentMap<String,TimingInfo> timing = 
        new ConcurrentHashMap<String,TimingInfo>();

    public void addTiming(final String cmdName, final String cmdStr, final long l) {
        e.execute(new Runnable() {
            public void run() {
                TimingInfo ti = new TimingInfo();
                TimingInfo currentTi = timing.putIfAbsent(cmdName, ti);
                if (currentTi == null) {
                    currentTi = ti;
                }
                currentTi.add(cmdStr, l);
            }
        });
    }

    private static class TimingInfo {
        long max = 0;
        long min = Long.MAX_VALUE;
        double average = 0;
        long count = 0;
        long total = 0;
        final int MAX_SIZE = 10;
        final CommandTime[] top = new CommandTime[MAX_SIZE];
        ReadWriteLock lock = new ReentrantReadWriteLock();
        
        public TimingInfo() {
            Arrays.fill(top, new CommandTime(0, ""));
        }
        
        public void add(String commandStr, long value) {
            total += value;
            count++;
            average = (double) total / (double) count;
            max = Math.max(max, value);
            min = Math.min(min, value);
            String s = "{" + value + "ms} " + commandStr;
            CommandTime ct = new CommandTime(value, s);
            try {
                lock.writeLock().lock();
                int n = Arrays.binarySearch(top, ct, new Comparator<CommandTime>() {
                    public int compare(CommandTime ct0, CommandTime ct1) {
                        return (int) (ct1.getTime() - ct0.getTime());
                    }
                });
                n = n < 0 ? -n : n + 1;

                if (n <= top.length && n > 0) {
                    System.arraycopy(top, n - 1, top, n, top.length - (n));
                    top[n - 1] = ct;
                }
            } finally {
                lock.writeLock().unlock();
            }
        }
        
        final static String FORMAT = "Requests: %d, Max: %d, Min %d, Average %f2";
        public String toString() {
            return String.format(FORMAT, count, max, min, average);
        }
        
        public String[] getTop() {
            String[] ss = new String[top.length];
            try {
                lock.readLock().lock();
                for (int i = 0; i < top.length; i++) {
                    ss[i] = top[i].getCommand();
                }
            } finally {
                lock.readLock().unlock();
            }
            return ss;
        }
        
    }

    private final static class CommandTime implements Comparable<CommandTime> {
        
        long time;
        String command;
        
        public CommandTime(long time, String command) {
            this.time = time;
            this.command = command;
        }
        
        public long getTime() {
            return time;
        }
        
        public String getCommand() {
            return command;
        }
        
        @Override
        public int hashCode() {
            final int PRIME = 31;
            int result = 1;
            result = PRIME * result + (int) (time ^ (time >>> 32));
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final CommandTime other = (CommandTime) obj;
            if (time != other.time)
                return false;
            return true;
        }

        public int compareTo(CommandTime ct) {
            return (int) (time - ct.time);
        }
    }
}
