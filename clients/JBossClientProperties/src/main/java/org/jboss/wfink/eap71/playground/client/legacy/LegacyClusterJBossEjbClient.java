package org.jboss.wfink.eap71.playground.client.legacy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.wfink.eap71.playground.client.logging.AbstractLoggerMain;
import org.jboss.wfink.eap71.playground.Simple;

public class LegacyClusterJBossEjbClient extends AbstractLoggerMain {
	private static final Logger log = Logger.getLogger(LegacyClusterJBossEjbClient.class.getName());

	public static void main(String[] args) throws NamingException {
		checkArgs(args);
		final Hashtable<String, String> jndiProperties = new Hashtable<>();
		jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
		final Context ic = new InitialContext(jndiProperties);

		Simple proxy = (Simple) ic.lookup("ejb:EAP71-PLAYGROUND-server/ejb/SimpleBean!" + Simple.class.getName());

		HashSet<String> serverList = new HashSet<>();

		for (int i = 0; i < 20; i++) {
			serverList.add(proxy.getJBossServerName());
		}

		if (serverList.size() > 1) {
			log.info("Server should be part of a cluster or multiple URL's as the invocation was executed on the following servers : " + serverList);
		} else if (serverList.size() == 1) {
			log.warning("Server is not part of a cluster with multiple nodes, or did not have multiple PROVIDER URL's, as the invocation was executed on a single server : " + new ArrayList<String>(serverList).get(0));
		} else {
			throw new RuntimeException("Unexpected result, no server list!");
		}
	}
}
