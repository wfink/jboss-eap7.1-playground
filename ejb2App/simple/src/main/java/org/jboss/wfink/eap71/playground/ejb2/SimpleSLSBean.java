package org.jboss.wfink.eap71.playground.ejb2;

import java.util.logging.Logger;

import javax.ejb.EJBException;
import javax.ejb.SessionContext;
import javax.naming.InitialContext;

/**
 * Legacy EJB2 stateless session bean to test EAP7.1
 */
public class SimpleSLSBean implements javax.ejb.SessionBean {
    private static final Logger log = Logger.getLogger(SimpleSLSBean.class.getName());
    SessionContext context;

    public void ejbActivate() throws EJBException {
    }

    public void ejbPassivate() throws EJBException {
    }

    public void ejbRemove() throws EJBException {
    }

    public void setSessionContext(SessionContext arg0) throws EJBException {
        context = arg0;
    }

    /**
     * Business method to log a message with a local bean invocation.
     */
    public void invokeLocal(String message) {
        LocalSLS bean;
        try {
            bean = ((LocalSLSHome)new InitialContext().lookup("java:module/LocalSLSBean!org.jboss.wfink.eap71.playground.ejb2.LocalSLSHome")).create();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create a reference to LocalSLS", e);
        }

        bean.logMessage(message);
    }
    
    public void logMessage() {
    	log.info("logMessage() invoked");
    }
}
