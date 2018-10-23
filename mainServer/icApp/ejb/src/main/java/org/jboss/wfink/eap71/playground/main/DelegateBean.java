/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.wfink.eap71.playground.main;

import java.security.Principal;
import java.util.Properties;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.TransactionSynchronizationRegistry;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.wfink.eap71.playground.Simple;
import org.jboss.wfink.ejb30.tx.sync.TxSyncInterceptor;
import org.wildfly.naming.client.WildFlyInitialContextFactory;

/**
 * <p>
 * Simple Bean to show invocation to another server.
 * The invocation is done by InitialContext with properties
 * </p>
 *
 * @author <a href="mailto:wfink@redhat.com">Wolf-Dieter Fink</a>
 */
@Stateless
@SecurityDomain("other")
public class DelegateBean implements Delegate {
    private static final Logger log = Logger.getLogger(DelegateBean.class.getName());
    @Resource
    SessionContext context;

    @Resource
    TransactionSynchronizationRegistry txRegistry;
    
    @Override
    @PermitAll
    public String getJBossServerName() {
        Principal caller = context.getCallerPrincipal();
        log.info("[" + caller.getName() + "] getJBossServerName");

        return System.getProperty("jboss.server.name");
    }


    @RolesAllowed({"Application"})
    @Override
    public void checkApplicationUser4DedicatedConnection(String localUserName, String remoteUserName) throws NamingException {
        Principal caller = context.getCallerPrincipal();
        
        if(!localUserName.equals(caller.getName())) {
        	log.severe("Given user name '" + localUserName + "' not equal to real use name '" + caller.getName() + "'");
        }else{
        	log.info("Expected user '" + localUserName + "'. Try to invoke remote SimpleBean with user '" + remoteUserName + "'");
        	Properties p = new Properties();
    		p.put(Context.INITIAL_CONTEXT_FACTORY, WildFlyInitialContextFactory.class.getName());
    		p.put(Context.PROVIDER_URL, "http-remoting://localhost:8080");
    		
    		p.put(Context.SECURITY_PRINCIPAL, remoteUserName);
    		p.put(Context.SECURITY_CREDENTIALS, remoteUserName);

        	InitialContext ic = new InitialContext(p);
        	Simple localProxy = (Simple)ic.lookup("ejb:EAP71-PLAYGROUND-server/ejbOne/SimpleBean!" + Simple.class.getName());
        	if (!localProxy.checkApplicationUser(remoteUserName)) {
        		log.severe("Remote bean was not invoked with the correct user!");
        		throw new RuntimeException("Remote bean was not invoked with the correct user!");
        	}
        	
        }
        return;
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @PermitAll
    @Override
    public void checkTransactionBehaviour(boolean setLocalRollbackOnly, boolean throwLocalException, boolean setRemoteRollbackOnly, boolean throwRemoteException, boolean expectedToCommit) throws NamingException {
    	Properties p = new Properties();
		p.put(Context.INITIAL_CONTEXT_FACTORY, WildFlyInitialContextFactory.class.getName());
		p.put(Context.PROVIDER_URL, "http-remoting://localhost:8080");
		
		p.put(Context.SECURITY_PRINCIPAL, "delegateUserR");
		p.put(Context.SECURITY_CREDENTIALS, "delegateUser");

    	InitialContext ic = new InitialContext(p);
    	Simple localProxy = (Simple)ic.lookup("ejb:EAP71-PLAYGROUND-server/ejbOne/SimpleBean!" + Simple.class.getName());
    	
        //TransactionSynchronizationRegistry txRegistry = (TransactionSynchronizationRegistry) context.lookup("java:jboss/TransactionSynchronizationRegistry");
        txRegistry.registerInterposedSynchronization(new TxSyncInterceptor(setLocalRollbackOnly|setRemoteRollbackOnly));

    	localProxy.checkTransactionContext(setRemoteRollbackOnly, throwRemoteException, expectedToCommit);
    	
    	if(setLocalRollbackOnly) {
    		context.setRollbackOnly();
    		log.warning("Rollback set!");
    	}
    	if(throwLocalException) {
    		throw new RuntimeException("Forced failure!");
    	}
    	log.info("Method done");
    }
}
