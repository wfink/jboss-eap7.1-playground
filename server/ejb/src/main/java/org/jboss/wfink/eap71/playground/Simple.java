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

import javax.ejb.Remote;

@Remote
public interface Simple {

    /**
     * @param text This text will be logged in the application
     */
    void logText(String text);

    /**
     * Return the name of the Wildfly/EAP instance where deployed.
     * No security needed.
     * 
     * @return jboss.server.name
     */
	String getJBossServerName();

	/**
	 * Invocation only allowed with role = Admin
	 * @param text this text will be logged
	 */
	void logText4RoleAdmin(String text);

	/**
	 * Check whether the given user name is the principal in security context
	 * @param userName the user name to verify
	 * @return true if the user is the same
	 */
	boolean checkApplicationUser(String userName);

	/**
	 * Default security method, no annotation.
	 * @param text this text will be logged
	 */
	void logTextSecured(String text);

	/**
	 * Method with mandatory transaction.
	 * The method can force an exception or rollback by flags to simulate failure.
	 * A transaction listener will check whether the Tx succeed or fail as expected.
	 * 
	 * @param setRollbackOnly If true the rollback flag will be set
	 * @param throwException If true the method throw a RuntimeException to fail
	 * @param expectedToCommit expectation to be cheked with the TxListener
	 */
	void checkTransactionContext(boolean setRollbackOnly, boolean throwException, boolean expectedToCommit);

	String getJBossServerNameInRunningTx();
}
