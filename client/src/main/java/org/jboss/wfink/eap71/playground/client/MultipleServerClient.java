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
		p.put(Context.PROVIDER_URL, "http-remoting://localhost:8080");
		p.put(Context.SECURITY_PRINCIPAL, "user1");
		p.put(Context.SECURITY_CREDENTIALS, "user1+");
		InitialContext ic = new InitialContext(p);
		
		Simple proxy = (Simple) ic.lookup("ejb:EAP71-PLAYGROUND-server/ejb/SimpleBean!" + Simple.class.getName());
		
		HashSet<String> serverList = new HashSet<>();
		
		for (int i = 0; i < 20; i++) {
			serverList.add(proxy.getJBossServerName());
		}
		
		if(serverList.size() > 1) {
			log.info("Server should be part of a cluster as the invocation was executed on the following servers : " + serverList);
		}else if(serverList.size() == 1) {
			log.info("Server is not part of a cluster as the invocation was executed on a single server : " + new ArrayList<String>(serverList).get(0));
		}else{
			throw new RuntimeException("Unexpected result, no server list!");
		}
		
		if(serverList.size() == 1) {
			serverList.clear();
			p.put(Context.PROVIDER_URL, "http-remoting://localhost:8080,http-remoting://localhost:8180");
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
