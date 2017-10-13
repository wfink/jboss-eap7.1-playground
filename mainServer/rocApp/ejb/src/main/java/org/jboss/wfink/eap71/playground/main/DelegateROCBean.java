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
import java.util.HashSet;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.TransactionSynchronizationRegistry;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.wfink.eap71.playground.Simple;
import org.jboss.wfink.ejb30.tx.sync.TxSyncInterceptor;

/**
 * <p>Simple Bean to show invocation to another server</p>
 *
 * @author <a href="mailto:wfink@redhat.com">Wolf-Dieter Fink</a>
 */
@Stateless
@SecurityDomain("other")
public class DelegateROCBean implements DelegateROC {
    private static final Logger log = Logger.getLogger(DelegateROCBean.class.getName());
    @Resource
    SessionContext context;

    @Resource
    TransactionSynchronizationRegistry txRegistry;

    @EJB(lookup = "ejb:EAP71-PLAYGROUND-server/ejb/SimpleBean!org.jboss.wfink.eap71.playground.Simple")
    Simple proxy;

    @Override
    @PermitAll
    public String getJBossServerName() {
        Principal caller = context.getCallerPrincipal();
        log.info("[" + caller.getName() + "] getJBossServerName");

        return System.getProperty("jboss.server.name");
    }

    @RolesAllowed({"Application"})
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    @Override
    public void checkApplicationUserWithRemoteOutboundConnection(String userName, int invocations) {
        Principal caller = context.getCallerPrincipal();
        
        if(!userName.equals(caller.getName())) {
        	log.severe("Given user name '" + userName + "' not equal to real use name '" + caller.getName() + "'");
        }else{
        	log.info("Try to invoke remote SimpleBean with user '" + userName + "' " + invocations + " times");
        	try {
				Simple proxy = (Simple)new InitialContext().lookup("ejb:EAP71-PLAYGROUND-server/ejb/SimpleBean!" + Simple.class.getName());
				for(int i = 0 ; i < invocations ; i++) {
					proxy.checkApplicationUser(userName);
				}
			} catch (NamingException e) {
				throw new RuntimeException("No target Bean found!", e);
			}
        }
        return;
    }

    @PermitAll
    @Override
    public void checkMultipleInvocations() throws NamingException {
    	HashSet<String> servers = new HashSet<String>();
    	
    	for(int i = 0 ; i <10 ; i++) {
    		final String server = proxy.getJBossServerName();
    		log.fine("Invocation routed to server " + server);
    		servers.add(server);
    	}
    	log.info("Invocations routed to the following servers : " + servers);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @PermitAll
    @Override
    public void checkTransactionBehaviour(boolean setLocalRollbackOnly, boolean throwLocalException, boolean setRemoteRollbackOnly, boolean throwRemoteException, boolean expectedToCommit) throws NamingException {
        txRegistry.registerInterposedSynchronization(new TxSyncInterceptor(setLocalRollbackOnly|setRemoteRollbackOnly));

    	proxy.checkTransactionContext(setRemoteRollbackOnly, throwRemoteException, expectedToCommit);
    	
    	if(setLocalRollbackOnly) {
    		context.setRollbackOnly();
    		log.warning("Rollback set!");
    	}
    	if(throwLocalException) {
    		throw new RuntimeException("Forced failure!");
    	}
    	log.info("Method done");
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @PermitAll
    @Override
    public void checkTransactionStickyness() {

        HashSet<String> servers = new HashSet<String>();
        for(int i = 0 ; i <20 ; i++) {
        	servers.add(proxy.getJBossServerNameInRunningTx());
        }
    	if(servers.size() != 1) {
    		log.severe("Unexpected list of target servers : " + servers);
    		throw new RuntimeException("Tx seems not to be sticky servers are : " + servers);
    	}
    	log.info("Method done");
    }
}
