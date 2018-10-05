package org.jboss.wfink.eap71.playground.ejb2;

public interface LocalSLSHome extends javax.ejb.EJBLocalHome
{
	public org.jboss.wfink.eap71.playground.ejb2.LocalSLS create() throws javax.ejb.CreateException;

}
