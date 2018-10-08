package org.jboss.wfink.eap71.playground.legacy.ejb3;

import java.util.logging.Logger;

import javax.ejb.EJBException;
import javax.ejb.Init;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.naming.InitialContext;

/**
 * Legacy EJB3 stateless session bean to test legacy EJB2 clients with EAP7.1
 */
@Stateless
public class SimpleEJB3SLSBean implements SimpleEJB3SLS {
    private static final Logger log = Logger.getLogger(SimpleEJB3SLSBean.class.getName());

    public void logMessage(String message) {
    	log.info("logMessage() invoked : " +message);
    }
}
