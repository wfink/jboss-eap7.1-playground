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

import javax.ejb.Remote;
import javax.naming.NamingException;

/**
 * <p>Interface for  DelegateBean to show invocation to another server</p>
 *
 * @author <a href="mailto:wfink@redhat.com">Wolf-Dieter Fink</a>
 */
@Remote
public interface Delegate {

    /**
     * Return the name of the Wildfly/EAP instance where deployed.
     * No security needed.
     * 
     * @return jboss.server.name
     */
	String getJBossServerName();

	/**
	 * Pass the invocation to a second remote instance by creating a dedicated InitialContext and maybe a different user.
	 * 
	 * @param localUserName  this name is checked local whether the security context caller name is correct
	 * @param remoteUserName  this is the used (and password) used for the InitialContext to establish the remote connection 
	 * @throws NamingException InitialContext could not created
	 */
	void checkApplicationUser4DedicatedConnection(String localUserName, String remoteUserName) throws NamingException;

	void checkTransactionBehaviour(boolean setLocalRollbackOnly, boolean throwLocalException,
			boolean setRemoteRollbackOnly, boolean throwRemoteException, boolean expectedToCommit)
			throws NamingException;
}
