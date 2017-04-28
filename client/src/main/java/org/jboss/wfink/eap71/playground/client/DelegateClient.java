/**
 * 
 */
package org.jboss.wfink.eap71.playground.client;

import java.util.Properties;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.wfink.eap71.playground.main.Delegate;
import org.wildfly.naming.client.WildFlyInitialContextFactory;

/**
 * <p>Simple client to show ejb invocation from server to server</p>
 *
 * @author <a href="mailto:wfink@redhat.com">Wolf-Dieter Fink</a>
 */
public class DelegateClient extends AbstractLoggerMain {
	private static final Logger log = Logger.getLogger(DelegateClient.class.getName());
	
	public static void main(String[] args) throws NamingException {
		checkArgs(args);
		
		Properties p = new Properties();
		
		p.put(Context.INITIAL_CONTEXT_FACTORY, WildFlyInitialContextFactory.class.getName());
		p.put(Context.PROVIDER_URL, "http-remoting://localhost:9080");
		
		p.put(Context.SECURITY_PRINCIPAL, "delegateUser");
		p.put(Context.SECURITY_CREDENTIALS, "delegateUser");
		InitialContext ic = new InitialContext(p);
		
		Delegate proxy = (Delegate) ic.lookup("ejb:EAP71-PLAYGROUND-MainServer/ejb/DelegateBean!" + Delegate.class.getName());
		proxy.checkApplicationUser4DedicatedConnection("delegateUser", "delegateUserR");
		
		ic.close();
	}

}
