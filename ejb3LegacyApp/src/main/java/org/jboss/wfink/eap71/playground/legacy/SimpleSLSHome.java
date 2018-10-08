package org.jboss.wfink.eap71.playground.legacy;

public interface SimpleSLSHome extends javax.ejb.EJBHome {
	public org.jboss.wfink.eap71.playground.legacy.SimpleSLS create() throws javax.ejb.CreateException,java.rmi.RemoteException;
}
