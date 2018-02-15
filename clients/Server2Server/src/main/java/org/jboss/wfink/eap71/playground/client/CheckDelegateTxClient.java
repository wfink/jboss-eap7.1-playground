package org.jboss.wfink.eap71.playground.client;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.wfink.eap71.playground.client.logging.AbstractLoggerMain;
import org.jboss.wfink.eap71.playground.main.Delegate;
import org.wildfly.naming.client.WildFlyInitialContextFactory;

/**
 * <p>
 * Simple client to show ejb invocation from server to server.
 * The client try to invoke the server and the Tx will fail in different levels
 * </p>
 *
 * @author <a href="mailto:wfink@redhat.com">Wolf-Dieter Fink</a>
 */
public class CheckDelegateTxClient extends AbstractLoggerMain {
	private static final Logger log = Logger.getLogger(CheckDelegateTxClient.class.getName());
	
	public static void main(String[] args) throws NamingException, InterruptedException {
		int test = 0;
		checkArgs(args);
		
		if(args.length > 0) {
			try {
				log.info("ARG = " + args[args.length-1]);
				test = Integer.parseInt(args[args.length-1]);
			} catch (NumberFormatException e) {
				log.warning(e.getMessage());
			}
		}
		
		log.finest("TEST flag is "+test);
		
		Properties p = new Properties();
		
		p.put(Context.INITIAL_CONTEXT_FACTORY, WildFlyInitialContextFactory.class.getName());
		p.put(Context.PROVIDER_URL, "http-remoting://localhost:9080");
		
		p.put(Context.SECURITY_PRINCIPAL, "delegateUser");
		p.put(Context.SECURITY_CREDENTIALS, "delegateUser");
		InitialContext ic = new InitialContext(p);
		
		Delegate proxy = (Delegate) ic.lookup("ejb:EAP71-PLAYGROUND-MainServer-icApp/ejb/DelegateBean!" + Delegate.class.getName());

		if(test == 0 || test == 1) {
			log.info("try invocation with successful Tx  -- No Exception expected");
			proxy.checkTransactionBehaviour(false, false, false, false, true);
			if (test==0) Thread.sleep(1100);
		}
		if(test == 0 || test == 2) {
			log.info("try invocation with set rollback on caller");
			proxy.checkTransactionBehaviour(true, false, false, false, false);
			if (test==0) Thread.sleep(1100);
		}
		if(test == 0 || test == 3) {
			try {
				log.info("try invocation with RuntimeException on caller");
				proxy.checkTransactionBehaviour(false, true, false, false, false);
			} catch (Exception e) {
				log.log(Level.SEVERE,"Invocation failed!", e);
			}
			if (test==0) Thread.sleep(1100);
		}
		if(test == 0 || test == 4) {
			log.info("try invocation with set rollback on target");
			Thread.sleep(1100);
			try {
				proxy.checkTransactionBehaviour(false, false, true, false, false);
			} catch (Exception e) {
				log.log(Level.SEVERE,"Invocation failed!", e);
			}
			if (test==0) Thread.sleep(1100);
		}
		if(test == 0 || test == 5) {
			try {
				log.info("try invocation with RuntimeException on target");
				proxy.checkTransactionBehaviour(false, false, false, true, false);
			} catch (Exception e) {
				log.log(Level.SEVERE,"Invocation failed!", e);
			}
		}
	}
}
