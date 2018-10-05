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
    private static final String rcal = "ejb:/eap71-playground-legacy-ejb21-simple/SimpleSLS!org.jboss.wfink.eap71.playground.ejb2.SimpleSLSHome";
    private static SimpleSLS slsbRemote;
    private static InitialContext context;

	private SimpleInvocationTest(String host, String port) throws NamingException, RemoteException, CreateException {
        // suppress output of client messages
        Logger.getLogger("org.jboss").setLevel(Level.ALL);
        Logger.getLogger("org.xnio").setLevel(Level.ALL);

        Properties p = new Properties();
		p.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        p.put(InitialContext.PROVIDER_URL, "http-remoting://" + host + ":" + port);
        context = new InitialContext(p);

        System.out.println("Lookup  => " + rcal);
        slsbRemote = ((SimpleSLSHome) context.lookup(rcal)).create();
    }

    public void shutdown() throws NamingException {
        context.close();
    }

    public void invoke() throws RemoteException {
    	slsbRemote.logMessage();  // test remote bean
        slsbRemote.invokeLocal("log a message from client at " + new Date());  // test invocation to another local bean
    }
    
    public static void main(String[] args) throws RemoteException, NamingException, CreateException {
    	String host = "localhost";
    	String port = "8080";
    	
    	if(args.length > 0) {
    		host = args[0];
    		if(args.length > 1) port = args[1];
    	}
    	
    	SimpleInvocationTest test = new SimpleInvocationTest(host,port);
    	
    	test.invoke();
    	test.shutdown();
    }

}
