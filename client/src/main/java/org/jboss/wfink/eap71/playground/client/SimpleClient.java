/**
 * 
 */
package org.jboss.wfink.eap71.playground.client;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJBException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.wildfly.naming.client.WildFlyInitialContextFactory;

import org.jboss.wfink.eap71.playground.Simple;

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
public class SimpleClient extends AbstractLoggerMain {
	private static final Logger log = Logger.getLogger(SimpleClient.class.getName());
	
	public static void main(String[] args) throws NamingException {
		checkArgs(args);
		
		Properties p = new Properties();
		
		p.put(Context.INITIAL_CONTEXT_FACTORY, WildFlyInitialContextFactory.class.getName());
		p.put(Context.PROVIDER_URL, "http-remoting://localhost:8080");
		InitialContext ic = new InitialContext(p);
		
		Simple proxy = (Simple) ic.lookup("ejb:EAP71-PLAYGROUND-server/ejb/SimpleBean!" + Simple.class.getName());
		
		try {
			proxy.logText("Simple invocation without security at " + new Date());
			log.info("Expected to work with the default configuration, to get a security failure remote the <local> element from the ApplicationRealm!");
		} catch (EJBException e) {
			if(e.getCause() instanceof IOException) {
				if(e.getCause().getMessage().startsWith("WFNAM00047")) {
					// expected message failed to connect
					// CHECK: unfortunately the SecurityException is not directly to catch
					log.log(Level.SEVERE, "Expected to fail if no <local> configuration in ApplicationRealm, unfortunately no SecurityException but text message in stacktrace", e.getCause());
				}
			} else {
				throw e;
			}
		}
	}

}
