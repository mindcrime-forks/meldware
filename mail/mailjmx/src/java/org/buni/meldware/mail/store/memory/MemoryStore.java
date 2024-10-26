package org.buni.meldware.mail.store.memory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.buni.meldware.mail.store.Store;
import org.buni.meldware.mail.store.StoreException;
import org.buni.meldware.mail.store.StoreItem;

public class MemoryStore implements Store {
    
    private volatile long seq = 1;

    private Map<Long,ByteArrayOutputStream> data = 
        new ConcurrentHashMap<Long,ByteArrayOutputStream>();
    
    protected synchronized Long doCreate() throws StoreException {
        Long l = new Long(seq);
        seq++;
        data.put(l, new ByteArrayOutputStream());
        return l;
    }

    public void cleanUp(List<Long> bods) {
    }

    public void commit() {
    }

    public StoreItem createStoreItem() throws StoreException {
        return new MemoryStoreItem(this, doCreate());
    }
    
    private static class MemoryStoreItem implements StoreItem {

        private Long id;
        private MemoryStore store;

        public MemoryStoreItem(MemoryStore store, Long id) {
            this.id = id;
            this.store = store;
        }
        
        public void delete() throws StoreException {
            store.delete(id);
        }

        public Long getId() {
            return id;
        }

        public InputStream getInputStream() throws StoreException {
            return new ByteArrayInputStream(store.data.get(id).toByteArray());
        }

        public OutputStream getOuputStream() throws StoreException {
            return store.data.get(id);
        }

        public int getSize() throws StoreException {
            return store.data.get(id).size();
        }

        public long getStartIndex() {
            return 0;
        }
        
    }


    public void delete(Long id) throws StoreException {
        data.remove(id);
    }

    public int getBufferSize() {
        return 8192;
    }

    public boolean getCompress() {
        return false;
    }

    public int getCompressBufferSize() {
        return 8192;
    }

    public boolean getHashed() {
        return false;
    }

    public int getPageSize() {
        return 8192;
    }

    public int getStartIndex() {
        return 0;
    }

    public StoreItem getStoreItem(Long id) throws StoreException {
        if (data.containsKey(id)) {
            return new MemoryStoreItem(this, id);
        } else {
            throw new StoreException("Item not found: " + id);
        }
    }

    public String listMetaData() {
        return "";
    }

    public String toString(Long id) {
        return new String(data.get(id).toByteArray());
    }

	public int purge() {
		int size = data.size();
		data.clear();
		return size;
	}
}
