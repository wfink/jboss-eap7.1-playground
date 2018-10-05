package org.jboss.wfink.eap71.playground.ejb2;

public interface SimpleSLSHome extends javax.ejb.EJBHome {
	public org.jboss.wfink.eap71.playground.ejb2.SimpleSLS create() throws javax.ejb.CreateException,java.rmi.RemoteException;
}
