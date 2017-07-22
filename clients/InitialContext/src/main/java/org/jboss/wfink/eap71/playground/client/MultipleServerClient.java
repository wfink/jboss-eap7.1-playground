/**
 * 
 */
package org.jboss.wfink.eap71.playground.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;
import java.util.logging.Logger;

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
public class MultipleServerClient extends AbstractLoggerMain {
	private static final Logger log = Logger.getLogger(MultipleServerClient.class.getName());
	
	public static void main(String[] args) throws NamingException {
		checkArgs(args);

		Properties p = new Properties();
		
		p.put(Context.INITIAL_CONTEXT_FACTORY, WildFlyInitialContextFactory.class.getName());
		p.put(Context.PROVIDER_URL, "http-remoting://localhost:8080,http-remoting://localhost:8180");
		p.put(Context.SECURITY_PRINCIPAL, "user1");
		p.put(Context.SECURITY_CREDENTIALS, "user1+");
		InitialContext ic = new InitialContext(p);
		
		final String lookup = "ejb:EAP71-PLAYGROUND-server/ejb/SimpleBean!" + Simple.class.getName();
		Simple proxy = (Simple) ic.lookup(lookup);
		log.fine("Proxy is : " + proxy);
		
		HashSet<String> serverList = new HashSet<>();
		
		try {
			for (int i = 0; i < 20; i++) {
				serverList.add(proxy.getJBossServerName());
			}
		} catch (Exception e) {
			log.severe("Invocation failed! " + e.getMessage());
		}
		
		if(serverList.size() > 1) {
			log.info("Server should be part of a cluster as the invocation was executed on the following servers : " + serverList);
		}else if(serverList.size() == 1) {
			log.info("Server is not part of a cluster as the invocation was executed on a single server : " + new ArrayList<String>(serverList).get(0));
		}else{
			log.severe("No successfull invocation");
		}
		
		// check whether an offset with 200 works, this is to check whether dynamically changing the IC will work
		// current this is not expected to work
		if(serverList.size() <= 1) {
			serverList.clear();
			p.put(Context.PROVIDER_URL, "http-remoting://localhost:8080,http-remoting://localhost:8280");
			ic = new InitialContext(p);
			proxy = (Simple) ic.lookup("ejb:EAP71-PLAYGROUND-server/ejb/SimpleBean!" + Simple.class.getName());

			for (int i = 0; i < 20; i++) {
				serverList.add(proxy.getJBossServerName());
			}
			
			if(serverList.size() > 1) {
				log.info("Multiple servers in PROVIDER_URL are used to loadbalance invocations as the invocation was executed on the following servers : " + serverList);
			}else if(serverList.size() == 1) {
				log.info("Multiple servers in PROVIDER_URL does not invoke multiple servers only server : " + new ArrayList<String>(serverList).get(0));
			}else{
				throw new RuntimeException("Unexpected result, no server list!");
			}
		}
	}

}
