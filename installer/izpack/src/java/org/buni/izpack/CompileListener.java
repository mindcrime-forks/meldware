/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2007, Bunisoft LLC.,
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
package org.buni.izpack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import net.n3.nanoxml.XMLElement;

import com.izforge.izpack.compiler.CompilerException;
import com.izforge.izpack.compiler.Packager;
import com.izforge.izpack.event.CompilerListener;

public class CompileListener implements CompilerListener {

	public void notify(String position, int state, XMLElement data,
			Packager packager) {
	}

	public Map reviseAdditionalDataMap(Map additionals, XMLElement element)
			throws CompilerException {
		if (additionals == null) {
			additionals = new HashMap();
		}
		unwrapElement(element, additionals);
		return additionals;
	}

	public void unwrapElement(XMLElement element, Map additionals) {
		if (element.getName().equals("additionaldata")) {
			addTemplates(element, additionals);
		} else if (element.hasChildren()) {
			unwrapChildren(element.getChildren(), additionals);
		}
	}

	public void unwrapChildren(Vector children, Map additionals) {
		Iterator it = children.iterator();
		while (it.hasNext()) {
			XMLElement element = (XMLElement) it.next();
			unwrapElement(element, additionals);
		}
	}

	public void addTemplates(XMLElement element, Map additionals) {
		String key = element.getAttribute("key");
		String value = element.getAttribute("value");
		if ((key != null)) {
			if (key.equals("template")) {
				additionals.put(key, value);
			}
			if (key.equals("templatefilter")) {
				String filter = element.getContent();
				additionals.put(key, filter);
			}
		}
	}
}
