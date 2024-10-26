/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2007, Bunisoft LLC, and individual contributors as
 * indicated by the @authors tag.  See the copyright.txt in the 
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; version 2.1 of
 * the License.
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
package org.buni.meldware.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Ye ol Hash Bucket Thing is mainly for storing LDAP structures without worrying real
 * hard about things like how many are there...
 * @author andy
 */
public class HashBucketThing<K, V> {
    Map<K,List<V>> map;
    List<K> keys;
    Map<K,Integer> changes;
    
    //constants for getUpdateTypes these should be the same as OpenDS service but we don't want to create a compile time binding here.
    int DELETE = 2;
    int ADD = 1;
    int UPDATE = 0;
    int NONE = -1;

    public HashBucketThing() {
        this.map = new HashMap<K,List<V>>();
        this.changes = new HashMap<K, Integer>();
    }
    
    public void put(K key, V value) {
        if (value != null && value.equals("")) {
            value = null;
        }
        keys = null;
        List<V> values = map.get(key);
        if (map.get(key) == null && value != null) {
            changes.put(key, ADD);
        } else if(map.get(key) != null && value != null){
            changes.put(key, UPDATE);
        } else if( map.get(key) != null && value == null ) {
            changes.put(key, DELETE);
            map.remove(key);
            return;
        } else {
            return;//noop
        }
        values = new ArrayList<V>();
        map.put(key, values);
        values.add(value);   
    }
    
    public void put(K key, List<V> values) {
        if (map.get(key) == null && values != null) {
            changes.put(key, ADD);
        } else if(map.get(key) != null && values != null){
            changes.put(key, UPDATE);
        } else if( map.get(key) != null && values == null ) {
            changes.put(key, DELETE);
            map.remove(key);
            return;
        } else {
            return;
        }
        this.map.put(key,values);
    }
    
    public void putMore(K key, List<V> vals) {
        keys = null;
        List<V> values = map.get(key);
        if (values == null && vals != null) {
            changes.put(key, ADD);
        } else if(values != null && vals != null){
            changes.put(key, UPDATE);
        } else {
            return; //noop
        }
        if(values == null) {
            values = new ArrayList<V>();
            map.put(key, values);
        }
        values.addAll(vals);        
    }
    
    public void putAnother(K key, V value) {
        if (value == null) {
            return;
        }
        keys = null;
        List<V> values = map.get(key);
        changes.put(key, ADD);
        if(values == null) {
            values = new ArrayList<V>();
            map.put(key, values);
        }
        values.add(value);
    }
    
    public V get(K key) {
        List<V> values = map.get(key);
        if(values != null && values.size() > 0) {
            return values.get(0);
        }   
        return null;
    }
    
    public List<V> getAll(K key) {
        return map.get(key);
    }

    /**
     * flattens a hashmap out into a list of keys including duplciates, this should be 
     * parallel to getValues.  This parallelness is only guaranteed so long as you have't
     * called a "put" in between.
     * @return
     */
    public List<K> getKeys() {
        if(keys == null) {
            keys = new ArrayList<K>();
            Set<K> ks = map.keySet();
            for (K key: ks) {
                List<V> vals = map.get(key);
                for (V v : vals) {
                    keys.add(key);
                }
            }
        }
        return keys;
    }
    
    public List<V> getValues() {
        K lastKey = null;
        List<V> vals = new ArrayList<V>();
        if(keys == null) {
            this.getKeys();
        }
        for(K key:keys) {
            if(lastKey != null && lastKey.equals(key)) {
                
            }
            List<V> kvals = map.get(key);
            for (V v : kvals) {
                vals.add(v);
            }
            lastKey = key;
        }
        return vals;
    }
    
    public Map<K,Integer> getChanges() {
        return this.changes;
    }

    public List<K> getUpdatedKeys() {
        List<K> retval = new ArrayList<K>();
        if (this.keys == null) {
            getKeys();
        }
        for (K key : this.keys) {
            if (changes.containsKey(key)) {
                retval.add(key);
            }
        }
        return retval;
    }

    public List<V> getUpdatedValues() {
        List<V> retval = new ArrayList<V>();
        if(this.keys == null) {
            getKeys();
        }
        for (K key : this.keys) {
            if (changes.containsKey(key)) {
                retval.addAll(this.map.get(key));
            }
        }
        return retval;
    }

    public Map<K, List<V>> getMap() {
        return this.map;
    }
    
    public void coalesce() {
        for (K key: changes.keySet()) {
            changes.put(key, NONE);
        }
    }
}
