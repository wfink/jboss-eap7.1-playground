package org.jboss.wfink.eap71.playground.ejb2;

public interface SimpleSLS extends javax.ejb.EJBObject {
	/**
     * Business method to log a message with a local bean invocation.
     */
    public void invokeLocal(String message);

	/**
	 * Business method to log a message.
	 */
	public void logMessage();
}
