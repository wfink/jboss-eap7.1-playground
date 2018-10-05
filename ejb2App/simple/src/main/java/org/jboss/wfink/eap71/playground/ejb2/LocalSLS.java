package org.jboss.wfink.eap71.playground.ejb2;

public interface LocalSLS {

	/**
	 * Business method to log a message with a local bean invocation.
	 */
	public void logMessage(String message);
}
