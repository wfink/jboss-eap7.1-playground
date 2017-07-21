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
import org.wildfly.naming.client.WildFlyInitialContext;

/**
 * <p>Simple client to show ejb invocation with wildfly-config.xml, connection and user are set within the XML</p>
 * <p>
 * So it is expected to fail if the server has no application user set.
 * The configuration is secure by default and the user need to be added like followed : <br/>
 *   bin/add-user.sh -a -u user1 -p user1+
 * </p>
 * <p>
 * There are several options to create the initial context. Here we use WildFlyInitialContext and wildfly-config.xml, this prevent from have
 * any properties passed to InitialContext.<br/>
 * Other option are documented in <a href="https://github.com/wildfly/wildfly-naming-client#usage">WildFly Naming Client</a>
 * </p>
 *
 * @author <a href="mailto:wfink@redhat.com">Wolf-Dieter Fink</a>
 */
public class WildFlyICConfigClient extends AbstractLoggerMain {
	private static final Logger log = Logger.getLogger(WildFlyICConfigClient.class.getName());
	
	public static void main(String[] args) throws NamingException {
		checkArgs(args);
		
		InitialContext ic = new WildFlyInitialContext();
		
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
