/**
 * 
 */
package org.jboss.wfink.eap71.playground.client;

import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.wfink.eap71.playground.Simple;
import org.jboss.wfink.eap71.playground.client.logging.AbstractLoggerMain;
import org.wildfly.naming.client.WildFlyInitialContext;

/**
 * <p>Simple client to show ejb invocation</p>
 * <p>no security (use/passwd) is set.
 * So it is expected to fail if the server has no application user set.
 * The configuration is secure by default and the user need to be added like followed : <br/>
 *   bin/add-user.sh -a -u user1 -p user1+
 * </p>
 *
 * @author <a href="mailto:wfink@redhat.com">Wolf-Dieter Fink</a>
 */
public class SimpleWildFlyInitialContextClient extends AbstractLoggerMain {
	private static final Logger log = Logger.getLogger(SimpleWildFlyInitialContextClient.class.getName());
	
	public static void main(String[] args) throws NamingException {
		checkArgs(args);
		
		Properties p = new Properties();
		
		p.put(Context.PROVIDER_URL, "http-remoting://localhost:8080");
		InitialContext ic = new WildFlyInitialContext(p);
		
		Simple proxy = (Simple) ic.lookup("ejb:EAP71-PLAYGROUND-server/ejb/SimpleBean!" + Simple.class.getName());
		
		try {
			proxy.logText("Simple invocation without security at " + new Date());
			log.info("Expected to work with the default configuration, to get a security failure remote the <local> element from the ApplicationRealm!");
		} catch (Exception e) {
			log.severe("Expected to fail if no <local> configuration in ApplicationRealm, unfortunately no good Exception");
			e.printStackTrace();
		}
	}

}
