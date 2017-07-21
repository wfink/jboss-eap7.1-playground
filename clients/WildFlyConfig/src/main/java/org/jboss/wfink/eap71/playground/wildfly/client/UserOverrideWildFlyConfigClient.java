/**
 * 
 */
package org.jboss.wfink.eap71.playground.wildfly.client;

import java.util.Properties;
import java.util.logging.Logger;

import javax.ejb.EJBException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.wfink.eap71.playground.Simple;
import org.jboss.wfink.eap71.playground.client.logging.AbstractLoggerMain;

/**
 * <p>Simple client to show ejb invocation with wildfly-config.xml, connection and user are set within the XML</p>
 * <p>
 * So it is expected to fail if the server has no application user set.
 * The configuration is secure by default and the user need to be added like followed : <br/>
 *   bin/add-user.sh -a -u user1 -p user1+
 * </p>
 * <p>
 * There are several options to create the initial context. Here we use jndi.properties and wildfly-config.xml.<br/>
 * Other option are documented in <a href="https://github.com/wildfly/wildfly-naming-client#usage">WildFly Naming Client</a>
 * </p>
 * <p>
 * Here the wildfly-config user is overriden with a different user to demonstrate that the InitialContext setting take preference
 * </p>
 *
 * @author <a href="mailto:wfink@redhat.com">Wolf-Dieter Fink</a>
 */
public class UserOverrideWildFlyConfigClient extends AbstractLoggerMain {
	private static final Logger log = Logger.getLogger(UserOverrideWildFlyConfigClient.class.getName());
	
	public static void main(String[] args) throws NamingException {
		checkArgs(args);
		
		Properties p = new Properties();
		p.put(Context.SECURITY_PRINCIPAL, "admin");
		p.put(Context.SECURITY_CREDENTIALS, "admin");

		InitialContext ic = new InitialContext(p);
		
		Simple proxy = (Simple) ic.lookup("ejb:EAP71-PLAYGROUND-server/ejb/SimpleBean!" + Simple.class.getName());
		
		try {
			if(proxy.checkApplicationUser("admin")) {
				log.info("Expected 'admin'");
			} else {
				log.severe("Unexpected user, see server.log");
			}
		} catch (EJBException e) {
			throw e;
		}
	}

}
