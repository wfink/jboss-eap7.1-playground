package org.jboss.wfink.eap71.playground.ejb2;

import java.rmi.RemoteException;
import java.util.logging.Logger;

import javax.ejb.EJBException;
import javax.ejb.SessionContext;

/**
 * Legacy EJB2 stateless session bean to test EAP7.1
 */
public class LocalSLSBean implements javax.ejb.SessionBean {
    private static final Logger log = Logger.getLogger(LocalSLSBean.class.getName());

    public void ejbActivate() throws EJBException, RemoteException {
    }

    public void ejbPassivate() throws EJBException, RemoteException {
    }

    public void ejbRemove() throws EJBException, RemoteException {
    }

    public void setSessionContext(SessionContext arg0) throws EJBException, RemoteException {
    }

    public void logMessage(String message) {
        log.info(message);
    }
}
