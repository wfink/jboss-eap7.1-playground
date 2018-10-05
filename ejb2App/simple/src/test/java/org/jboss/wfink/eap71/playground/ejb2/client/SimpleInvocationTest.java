package org.jboss.wfink.eap71.playground.ejb2.client;


import java.rmi.RemoteException;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.CreateException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.wfink.eap71.playground.ejb2.SimpleSLS;
import org.jboss.wfink.eap71.playground.ejb2.SimpleSLSHome;


public class SimpleInvocationTest {
    private static final String remoteHomeName = "ejb:/eap71-playground-legacy-ejb21-simple/SimpleSLS!org.jboss.wfink.eap71.playground.ejb2.SimpleSLSHome";
    private static final String remoteName = "ejb:/eap71-playground-legacy-ejb21-simple/SimpleSLS!org.jboss.wfink.eap71.playground.ejb2.SimpleSLS";
    private static SimpleSLS slsbRemote;
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

    public void invoke() {
        try {
			System.out.println("Lookup  => " + remoteHomeName);
			slsbRemote = ((SimpleSLSHome) context.lookup(remoteHomeName)).create();
			slsbRemote.logMessage();  // test remote bean
			slsbRemote.invokeLocal("log a message from client at " + new Date());  // test invocation to another local bean
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private void invokeBusinessInterface() {
        System.out.println("Lookup  => " + remoteName);
        SimpleSLS proxy;
		try {
			proxy = (SimpleSLS) context.lookup(remoteName);
	        proxy.logMessage();
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
    	
    	test.invoke();
    	test.invokeBusinessInterface();
    	test.shutdown();
    }

}
