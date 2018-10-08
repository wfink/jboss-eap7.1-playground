package org.jboss.wfink.eap71.playground.legacy.ejb3.client;


import java.rmi.RemoteException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.CreateException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.wfink.eap71.playground.legacy.SimpleSLS;
import org.jboss.wfink.eap71.playground.legacy.SimpleSLSHome;
import org.jboss.wfink.eap71.playground.legacy.ejb3.SimpleEJB3SLS;


public class SimpleInvocationTest {
    private static final String remoteHomeName = "ejb:/eap71-playground-legacy-ejb3/SimpleLegacySLS!org.jboss.wfink.eap71.playground.legacy.SimpleSLSHome";
    private static final String remoteName = "ejb:/eap71-playground-legacy-ejb3/SimpleLegacySLS!org.jboss.wfink.eap71.playground.legacy.SimpleSLS";
    private static final String remoteEJB3Name = "ejb:/eap71-playground-legacy-ejb3/SimpleEJB3SLSBean!org.jboss.wfink.eap71.playground.legacy.ejb3.SimpleEJB3SLS";
    private static InitialContext context;

	private SimpleInvocationTest(String host, String port, String user, String passwd) throws NamingException {
        // suppress output of client messages
        Logger.getLogger("org.jboss").setLevel(Level.ALL);
        Logger.getLogger("org.xnio").setLevel(Level.ALL);

        Properties p = new Properties();
		p.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        p.put(InitialContext.PROVIDER_URL, "http-remoting://" + host + ":" + port);
        if(user != null) {
        	if(passwd == null || passwd.length() < 1) throw new IllegalArgumentException("A password is mandatory!");
    		p.put(Context.SECURITY_PRINCIPAL, user);
    		p.put(Context.SECURITY_CREDENTIALS, passwd);
        }
        context = new InitialContext(p);
    }

    public void shutdown() throws NamingException {
        context.close();
    }

    public void invokeViaEJB2Home() {
        try {
			System.out.println("Lookup  => " + remoteHomeName);
			SimpleSLS slsbRemote = ((SimpleSLSHome) context.lookup(remoteHomeName)).create();
			slsbRemote.logMessage("Invocation using EJB2 legacy RemoteHome .create() and Remote interface");  // test remote bean
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private void invokeViaEJB2BusinessInterface() {
        System.out.println("Lookup  => " + remoteName);
        SimpleSLS proxy;
		try {
			proxy = (SimpleSLS) context.lookup(remoteName);
	        proxy.logMessage("Invocation using EJB2 legacy Remote interface directly without *Home lookup");
		} catch (NamingException e) {
			e.printStackTrace();
		}
    }
    
    private void invokeEJB3() {
        System.out.println("Lookup  => " + remoteEJB3Name);
        SimpleEJB3SLS proxy;
		try {
			proxy = (SimpleEJB3SLS) context.lookup(remoteEJB3Name);
	        proxy.logMessage("Invocation using EJB3 business interface");
		} catch (NamingException e) {
			e.printStackTrace();
		}
    }
    
    public static void main(String[] args) throws RemoteException, NamingException, CreateException {
    	String host = "localhost";
    	String port = "8080";
    	String user = null;
    	String passwd = null;
    	
    	if(args.length > 0) {
    		host = args[0];
    		if(args.length > 1) port = args[1];
    	}
    	if(args.length == 3) {
    		user = args[2];
    		passwd = args[3];
    	}
    	
    	SimpleInvocationTest test = new SimpleInvocationTest(host,port, user, passwd);
    	
    	test.invokeEJB3();
    	test.invokeViaEJB2Home();
    	test.invokeViaEJB2BusinessInterface();
    	test.shutdown();
    }

}
