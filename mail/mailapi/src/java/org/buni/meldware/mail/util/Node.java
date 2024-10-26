/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2006, Bunisoft LLC., and individual contributors as
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
package org.buni.meldware.mail.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Generic Node structure for use in a tree.
 * 
 * @author Michael Barker
 *
 */
public class Node<T> implements Iterable<Node<T>> {

    private T value;
    private List<Node<T>> children = new ArrayList<Node<T>>();
    
    public Node(T value) {
        this.value = value;
    }
    
    public T getValue() {
        return value;
    }
    
    public void addChild(Node<T> child) {
        children.add(child);
    }
    
    public Iterator<Node<T>> getChildren() {
        return children.iterator();
    }
    
    public Iterator<Node<T>> iterator() {
        return children.iterator();
    }
    
    public void accept(Visitor<T> vistor) {
        vistor.visit(this);
    }
    
}
