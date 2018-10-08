package org.jboss.wfink.eap71.playground.legacy.ejb3;

import javax.ejb.Remote;

/**
 * The simple EJB3 remote business interface for the EJB3 SLSB
 * 
 * @author wfink
 */
@Remote
public interface SimpleEJB3SLS {
	/**
	 * Business method to log a message.
	 */
	public void logMessage(String message);
}
