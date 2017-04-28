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
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.wfink.eap71.playground.Simple;
import org.wildfly.naming.client.WildFlyInitialContextFactory;

/**
 * <p>Simple Bean to show invocation to another server</p>
 *
 * @author <a href="mailto:wfink@redhat.com">Wolf-Dieter Fink</a>
 */
@Stateless
@SecurityDomain("other")
public class DelegateBean implements Delegate {
    private static final Logger log = Logger.getLogger(DelegateBean.class.getName());
    @Resource
    SessionContext context;

    @EJB
    Simple proxy;

    @Override
    @PermitAll
    public String getJBossServerName() {
        Principal caller = context.getCallerPrincipal();
        log.info("[" + caller.getName() + "] getJBossServerName");

        return System.getProperty("jboss.server.name");
    }

    @RolesAllowed({"Application"})
    @Override
    public void checkApplicationUserWithRemoteOutboundConnection(String userName) {
        Principal caller = context.getCallerPrincipal();
        
        if(!userName.equals(caller.getName())) {
        	log.severe("Given user name '" + userName + "' not equal to real use name '" + caller.getName() + "'");
        }else{
        	log.fine("Try to invoke remote SimpleBean with user '" + userName + "'");
        	proxy.checkApplicationUser(userName);
        }
        return;
    }


    @RolesAllowed({"Application"})
    @Override
    public void checkApplicationUser4DedicatedConnection(String localUserName, String remoteUserName) throws NamingException {
        Principal caller = context.getCallerPrincipal();
        
        if(!localUserName.equals(caller.getName())) {
        	log.severe("Given user name '" + localUserName + "' not equal to real use name '" + caller.getName() + "'");
        }else{
        	log.info("Try to invoke remote SimpleBean with user '" + remoteUserName + "'");
        	Properties p = new Properties();
    		p.put(Context.INITIAL_CONTEXT_FACTORY, WildFlyInitialContextFactory.class.getName());
    		p.put(Context.PROVIDER_URL, "http-remoting://localhost:8080");
    		
    		p.put(Context.SECURITY_PRINCIPAL, remoteUserName);
    		p.put(Context.SECURITY_CREDENTIALS, remoteUserName);

        	InitialContext ic = new InitialContext(p);
        	Simple localProxy = (Simple)ic.lookup("ejb:EAP71-PLAYGROUND-server/ejb/SimpleBean!" + Simple.class.getName());
        	localProxy.checkApplicationUser(remoteUserName);
        }
        return;
    }
}
