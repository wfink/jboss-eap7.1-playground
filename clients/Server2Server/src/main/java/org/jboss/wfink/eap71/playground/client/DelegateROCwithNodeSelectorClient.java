package org.jboss.wfink.eap71.playground.client;

import java.util.Properties;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.wfink.eap71.playground.client.logging.AbstractLoggerMain;
import org.jboss.wfink.eap71.playground.main.DelegateROC;

public class DelegateROCwithNodeSelectorClient extends AbstractLoggerMain {
	private static final Logger log = Logger.getLogger(DelegateROCwithNodeSelectorClient.class.getName());

	public static void main(String[] args) throws NamingException {
		checkArgs(args);
		Properties p = new Properties();

		p.put(Context.INITIAL_CONTEXT_FACTORY, org.wildfly.naming.client.WildFlyInitialContextFactory.class.getName());
		p.put(Context.PROVIDER_URL, "http-remoting://localhost:9080");
		
		p.put(Context.SECURITY_PRINCIPAL, "delegateUser");
		p.put(Context.SECURITY_CREDENTIALS, "delegateUser");
		InitialContext ic = new InitialContext(p);

		DelegateROC proxy = (DelegateROC) ic.lookup("ejb:eap71-playground-mainServer-rocAppSelectorEar/ejb/DelegateROCBean!" + DelegateROC.class.getName());

		try {
			proxy.checkMultipleInvocations();
			log.info("check multiple invocations with remote-outbound-connections -check logfiles according to the test case!");
		} catch (Exception e) {
			log.severe("Not expected to fail" + e.getMessage());
			e.printStackTrace();
		}
	}
}
