package org.jboss.wfink.eap71.playground.legacy;

public interface SimpleSLS extends javax.ejb.EJBObject {
	/**
	 * Business method to log a message.
	 */
	public void logMessage(String message);
}
