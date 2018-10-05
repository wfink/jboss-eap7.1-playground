/**
 * 
 */
package org.jboss.wfink.eap71.playground.client;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJBAccessException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.wfink.eap71.playground.Simple;
import org.jboss.wfink.eap71.playground.client.logging.AbstractLoggerMain;
import org.wildfly.naming.client.WildFlyInitialContextFactory;

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
		p.put(Context.PROVIDER_URL, "http-remoting://" + AbstractLoggerMain.server);
		
		
		//  Invocation with an unknown user
		
		p.put(Context.SECURITY_PRINCIPAL, "unknownUser");
		p.put(Context.SECURITY_CREDENTIALS, "no");
		InitialContext ic = new InitialContext(p);
		
		Simple proxy = (Simple) ic.lookup("ejb:EAP71-PLAYGROUND-server/ejb/SimpleBean!" + Simple.class.getName());
		try {
			log.info("Invocation of @PermitAll method with unknownUser");
			proxy.checkApplicationUser("unknownUser");
			log.warning("SUCCSESS: If there is a $local configuration (default) and the invocation is to a local server success is expected");
		} catch (Exception e) {
			log.warning("FAILED: If the $local configuration (default) is removed the invocation failure is expected");
		}

		// Invocations with a know user without roles

		p.put(Context.SECURITY_PRINCIPAL, "user1");
		p.put(Context.SECURITY_CREDENTIALS, "user1+");
		ic = new InitialContext(p);

		// !! new lookup is needed, otherwise the old context will be used !!
		proxy = (Simple) ic.lookup("ejb:EAP71-PLAYGROUND-server/ejb/SimpleBean!" + Simple.class.getName());
		try {
			log.info("Invocation of @PermitAll method");
			if(proxy.checkApplicationUser("user1")) {
				log.fine("Expected success as the method is not secured ");
			} else {
				log.warning("Unexpected as was invoked with the wrong user, see server.log");
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "Simple @PermitAll Invocation failed",e);
			e.printStackTrace();
		}


		// Invocation as 'user1' here role 'Admin' is missing
		try {
			log.info("Invocation of @RoleAllowed(Admin) method with missing role");
			proxy.logText4RoleAdmin("test");
			log.severe("Unexpected success as user1 should not have the Admin role!");
		} catch (EJBAccessException e) {
			log.fine("Expected failure as user1 does not have the Admin role");
		} catch (Exception e) {
			log.severe("Unexpected failure!");
			e.printStackTrace();
		}


		// Invocation with a user that contains roles

		p.put(Context.SECURITY_PRINCIPAL, "admin");
		p.put(Context.SECURITY_CREDENTIALS, "admin");
		ic = new InitialContext(p);
		
		// !! new lookup is needed, otherwise the old context will be used !!
		proxy = (Simple) ic.lookup("ejb:EAP71-PLAYGROUND-server/ejb/SimpleBean!" + Simple.class.getName());
		
		try {
			log.info("Invocation of @RoleAllowed(Admin) method with correct role assigned");
			proxy.logText4RoleAdmin("invocation as admin");
			log.fine("Expected behaviour as admin have the Admin role");
		} catch (EJBAccessException e) {
			log.log(Level.SEVERE, "Unexpected behaviour as admin should have the Admin role!", e);
		}
	}
}
