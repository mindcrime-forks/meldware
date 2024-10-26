/*
 * Please add apropriate header/license info!
 */
package com.activescript.guid;

/**
 * 
 * @author WoodcockS
 * @version 1.0
 */
public class GUIDException extends java.lang.Exception {

	static final long serialVersionUID = "$Id: GUIDException.java,v 1.2 2007/03/12 01:37:18 andy Exp $".hashCode(); //$NON-NLS-1$

	/**
	 * Creates new <code>GUIDException</code> without detail message.
	 */
	public GUIDException() {
		// empty
	}

	/**
	 * Constructs an <code>GUIDException</code> with the specified detail
	 * message.
	 * 
	 * @param msg
	 *            the detail message.
	 */
	public GUIDException(String msg) {
		super(msg);
	}
}
