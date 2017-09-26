/**
 * 
 */
package org.jboss.wfink.eap71.playground.wildfly.client;

import java.util.logging.Logger;

import javax.ejb.EJBException;
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
 * There are several options to create the initial context. Here we use java.naming.InitialContext and pass jndi.properties and wildfly-config.xml.<br/>
 * Other option are documented in <a href="https://github.com/wildfly/wildfly-naming-client#usage">WildFly Naming Client</a>
 * </p>
 *
 * @author <a href="mailto:wfink@redhat.com">Wolf-Dieter Fink</a>
 */
public class SimpleWildFlyConfigClient extends AbstractLoggerMain {
	private static final Logger log = Logger.getLogger(SimpleWildFlyConfigClient.class.getName());
	
	public static void main(String[] args) throws NamingException {
		checkArgs(args);

		// One option is to use the java context, the INITIAL_CONTEXT_FACTORY is added by jndi.properties
		// the URI and credentials by wildfly-config.xml
		InitialContext ic = new InitialContext();

		Simple proxy = (Simple) ic.lookup("ejb:EAP71-PLAYGROUND-server/ejb/SimpleBean!" + Simple.class.getName());
		
		try {
			if(proxy.checkApplicationUser("user1")) {
				log.info("Expected 'user1'");
			} else {
				log.severe("Unexpected user, see server.log");
			}
		} catch (EJBException e) {
			throw e;
		}
	}

}
