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
package org.jboss.wfink.eap71.playground;

import java.security.Principal;
import java.util.logging.Logger;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.NamingException;
import javax.transaction.TransactionSynchronizationRegistry;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.wfink.ejb30.tx.sync.TxSyncInterceptor;

/**
 * <p>Simple Bean to show client invocation</p>
 *
 * @author <a href="mailto:wfink@redhat.com">Wolf-Dieter Fink</a>
 */
@Stateless
@SecurityDomain("other")
public class SimpleBean implements Simple {
    private static final Logger log = Logger.getLogger(SimpleBean.class.getName());
    @Resource
    SessionContext context;
    @Resource
    TransactionSynchronizationRegistry txRegistry;

    @Override
    @PermitAll
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public String getJBossServerNameInRunningTx() {
    	return getJBossServerName();
    }
    
    @Override
    @PermitAll
    public String getJBossServerName() {
        Principal caller = context.getCallerPrincipal();
        String serverName = System.getProperty("jboss.server.name");
        
        log.info("[" + caller.getName() + "] ServerName is " + serverName);

        return serverName;
    }

    @Override
    @PermitAll
    public void logText(String text) {
        Principal caller = context.getCallerPrincipal();
        log.info("[" + caller.getName() + "] Invocation granted with @permitAll  message: " + text);

        return;
    }
    
    @Override
    public void logTextSecured(String text) {
        Principal caller = context.getCallerPrincipal();
        log.info("[" + caller.getName() + "] Invocation granted without annotation  message: " + text);

        return;
    }
    
    @RolesAllowed({"Admin"})
    @Override
    public void logText4RoleAdmin(String text) {
        Principal caller = context.getCallerPrincipal();
        log.info("[" + caller.getName() + "] Invocation granted for Role=Admin  message: " + text);

        return;
    }
    
    @PermitAll
    @Override
    public boolean checkApplicationUser(String userName) {
        Principal caller = context.getCallerPrincipal();
        
        if(!userName.equals(caller.getName())) {
        	log.warning("Given user name '" + userName + "' not equal to real use name '" + caller.getName() + "'");
        	return false;
        }else{
        	log.fine("SimpleBean invoked with expected user '" + userName + "'");
            return true;
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    @PermitAll
    @Override
    public void checkTransactionContext(boolean setRollbackOnly, boolean throwException, boolean expectedToCommit) {
    	log.info("checkTransactionContext(" + setRollbackOnly + ", " + throwException + ", " + expectedToCommit + ") called");
        txRegistry.registerInterposedSynchronization(new TxSyncInterceptor(setRollbackOnly));

    	if(setRollbackOnly) {
    		context.setRollbackOnly();
    		log.warning("Rollback set!");
    	}
    	if(throwException) {
    		throw new RuntimeException("Forced failure!");
    	}
    	return;
    }
}
