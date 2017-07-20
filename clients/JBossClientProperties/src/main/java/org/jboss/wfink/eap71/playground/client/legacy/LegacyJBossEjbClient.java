package org.jboss.wfink.eap71.playground.client.legacy;

import java.io.IOException;
import java.util.Date;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJBException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.wfink.eap71.playground.Simple;

public class LegacyJBossEjbClient {
	private static final Logger log = Logger.getLogger(LegacyJBossEjbClient.class.getName());

	public static void main(String[] args) throws NamingException {
        final Hashtable<String, String> jndiProperties = new Hashtable<>();
        jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        final Context ic = new InitialContext(jndiProperties);

		Simple proxy = (Simple) ic.lookup("ejb:EAP71-PLAYGROUND-server/ejb/SimpleBean!" + Simple.class.getName());

		try {
			proxy.logText("Simple invocation without security at " + new Date());
			log.info("Expected to work, to get a security failure remove the <local> element from the ApplicationRealm and set no user!");
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

		try {
			proxy.logText4RoleAdmin("Invocation as legacy client " + new Date());
			log.info("Expected to work only as admin user");
		} catch (EJBException e) {
			log.warning("Failure expected if the user is not 'admin'");
		}
	}
}
