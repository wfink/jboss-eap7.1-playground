/**
 * 
 */
package org.jboss.wfink.eap71.playground.client;

import java.util.Properties;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.wfink.eap71.playground.client.logging.AbstractLoggerMain;
import org.jboss.wfink.eap71.playground.main.Delegate;
import org.wildfly.naming.client.WildFlyInitialContextFactory;

/**
 * <p>Simple client to show ejb invocation from server to server</p>
 *
 * @author <a href="mailto:wfink@redhat.com">Wolf-Dieter Fink</a>
 */
public class CheckDelegateTxClient extends AbstractLoggerMain {
	private static final Logger log = Logger.getLogger(CheckDelegateTxClient.class.getName());
	
	public static void main(String[] args) throws NamingException, InterruptedException {
		checkArgs(args);
		
		Properties p = new Properties();
		
		p.put(Context.INITIAL_CONTEXT_FACTORY, WildFlyInitialContextFactory.class.getName());
		p.put(Context.PROVIDER_URL, "http-remoting://localhost:9080");
		
		p.put(Context.SECURITY_PRINCIPAL, "delegateUser");
		p.put(Context.SECURITY_CREDENTIALS, "delegateUser");
		InitialContext ic = new InitialContext(p);
		
		Delegate proxy = (Delegate) ic.lookup("ejb:EAP71-PLAYGROUND-MainServer-icApp/ejb/DelegateBean!" + Delegate.class.getName());
//		log.info("try invocation with successful Tx");
//		proxy.checkTransactionBehaviour(false, false, false, false, true);
//		Thread.sleep(1100);
//		log.info("try invocation with set rollback on caller");
//		proxy.checkTransactionBehaviour(true, false, false, false, false);
//		Thread.sleep(1100);
//		try {
//			log.info("try invocation with RuntimeException on caller");
//			proxy.checkTransactionBehaviour(false, true, false, false, false);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		log.info("try invocation with set rollback on target");
		Thread.sleep(1100);
		try {
			proxy.checkTransactionBehaviour(false, false, true, false, false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Thread.sleep(1100);
		try {
			log.info("try invocation with RuntimeException on target");
			proxy.checkTransactionBehaviour(false, false, false, true, false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
