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
public interface DelegateROC {

    /**
     * Return the name of the Wildfly/EAP instance where deployed.
     * No security needed.
     * 
     * @return jboss.server.name
     */
	String getJBossServerName();

	/**
	 * <p>Pass the invocation to a second remote instance.</p>
	 * <p>A remote-outbound-connection is required for this!!!</br>
	 * The r-o-c should not have used a user for the connection with "Application" role to check whether the 
	 * security context for the application is propagated!</p> 
	 * <p>It is expected that the user is the same as the one used by client for the remote EJB</p> 
	 * 
	 * @param userName  this name is checked local whether the security context caller name is correct
	 * @param invocations Number of invocations to test cluster loadbalancing
	 * @throws NamingException InitialContext could not created
	 */
	void checkApplicationUserWithRemoteOutboundConnection(String userName, int invocations);

	void checkTransactionBehaviour(boolean setLocalRollbackOnly, boolean throwLocalException,
			boolean setRemoteRollbackOnly, boolean throwRemoteException, boolean expectedToCommit)
			throws NamingException;

	void checkTransactionStickyness();

	void checkMultipleInvocations() throws NamingException;
}
