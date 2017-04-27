/**
 * 
 */
package org.jboss.wfink.eap71.playground.client;

import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJBAccessException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.wildfly.naming.client.WildFlyInitialContextFactory;

import org.jboss.wfink.eap71.playground.Simple;

/**
 * <p>Simple client to show ejb invocation</p>
 *
 * @author <a href="mailto:wfink@redhat.com">Wolf-Dieter Fink</a>
 */
public class SimpleSecuredClient extends AbstractLoggerMain {
	private static final Logger log = Logger.getLogger(SimpleSecuredClient.class.getName());
	
	public static void main(String[] args) throws NamingException {
		checkArgs(args);
		
		Properties p = new Properties();
		
		p.put(Context.INITIAL_CONTEXT_FACTORY, WildFlyInitialContextFactory.class.getName());
		p.put(Context.PROVIDER_URL, "http-remoting://localhost:8080");
		
		p.put(Context.SECURITY_PRINCIPAL, "user1");
		p.put(Context.SECURITY_CREDENTIALS, "user1+");
		InitialContext ic = new InitialContext(p);
		
		Simple proxy = (Simple) ic.lookup("ejb:EAP71-PLAYGROUND-server/ejb/SimpleBean!" + Simple.class.getName());
		proxy.logText("Simple invocation without security at " + new Date());
		
		try {
			proxy.logText4RoleAdmin("test");
			log.severe("Unexpected behaviour as user1 should not have the Admin role");
		} catch (EJBAccessException e) {
			log.fine("Expected behaviour as user1 does not have the Admin role");
		}
		
		ic.close();

		// create a new Context with Admin
		p.put(Context.SECURITY_PRINCIPAL, "admin");
		p.put(Context.SECURITY_CREDENTIALS, "admin");
		ic = new InitialContext(p);
		
		// !! new lookup is needed, otherwise the old context will be used !!
		proxy = (Simple) ic.lookup("ejb:EAP71-PLAYGROUND-server/ejb/SimpleBean!" + Simple.class.getName());
		
		try {
			proxy.logText4RoleAdmin("test");
			log.fine("Expected behaviour as admin have the Admin role");
		} catch (EJBAccessException e) {
			log.log(Level.SEVERE, "Unexpected behaviour as admin should have the Admin role!", e);
		}
	}

}
