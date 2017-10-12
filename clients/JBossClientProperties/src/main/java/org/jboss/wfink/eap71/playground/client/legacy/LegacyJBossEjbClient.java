package org.jboss.wfink.eap71.playground.client.legacy;

import java.util.Date;
import java.util.Hashtable;
import java.util.logging.Logger;

import javax.ejb.EJBException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.wfink.eap71.playground.Simple;
import org.jboss.wfink.eap71.playground.client.logging.AbstractLoggerMain;

public class LegacyJBossEjbClient extends AbstractLoggerMain {
	private static final Logger log = Logger.getLogger(LegacyJBossEjbClient.class.getName());

	public static void main(String[] args) throws NamingException {
		checkArgs(args);
        final Hashtable<String, String> jndiProperties = new Hashtable<>();
        jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        final Context ic = new InitialContext(jndiProperties);

		Simple proxy = (Simple) ic.lookup("ejb:EAP71-PLAYGROUND-server/ejb/SimpleBean!" + Simple.class.getName());

		try {
			proxy.logText("Simple invocation without security at " + new Date());
			log.info("Expected to work for @PermitAll, to get a security failure remove the <local> element from the ApplicationRealm and set no user!");
		} catch (Exception e) {
			log.severe("Expected to fail if no <local> configuration in ApplicationRealm " + e.getMessage());
			e.printStackTrace();
		}

		try {
			proxy.logText4RoleAdmin("Invocation as legacy client " + new Date());
			log.info("Expected to work only as admin user");
		} catch (EJBException e) {
			log.warning("Failure expected if the user is not 'admin' " + e.getMessage());
			e.printStackTrace();
		}
	}
}
