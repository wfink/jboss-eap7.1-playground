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
 * <p>
 * Client to show ejb invocation with passing PROVIDER_URL with multiple servers to InitialContext.
 * Servers should run with a non HA configuration.
 * The test should be successful if server @8080 is active
 * Show that 2 servers are used if @8080 and @8180 servers are up
 * Show that both test pass with one server if @8180 and @8280 are up
 * </p>
 *
 * @author <a href="mailto:wfink@redhat.com">Wolf-Dieter Fink</a>
 */
public class MultipleServerProviderURLClient extends AbstractLoggerMain {
	private static final Logger log = Logger.getLogger(MultipleServerProviderURLClient.class.getName());
	
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
		log.fine("Proxy after lookup is : " + proxy);
		
		HashSet<String> serverList = new HashSet<>();

		log.info("Try to invoke SimpleBean with server @8080 @8180");
		try {
			for (int i = 0; i < 20; i++) {
				serverList.add(proxy.getJBossServerName());
				if(i == 0) log.fine("Proxy after first invocation is : " + proxy);
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
			log.info("First attempt does not use 2 servers, try to invoke SimpleBean with server @8080 @8280");
			serverList.clear();
			p.put(Context.PROVIDER_URL, "http-remoting://localhost:8080,http-remoting://localhost:8280");
			ic = new InitialContext(p);
			proxy = (Simple) ic.lookup("ejb:EAP71-PLAYGROUND-server/ejb/SimpleBean!" + Simple.class.getName());
			log.fine("Proxy after lookup is : " + proxy);

			for (int i = 0; i < 20; i++) {
				serverList.add(proxy.getJBossServerName());
				if(i == 0) log.fine("Proxy after first invocation is : " + proxy);
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
