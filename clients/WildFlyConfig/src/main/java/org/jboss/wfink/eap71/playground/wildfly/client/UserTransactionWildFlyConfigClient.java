/**
 * 
 */
package org.jboss.wfink.eap71.playground.wildfly.client;

import java.util.HashSet;
import java.util.Properties;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.jboss.wfink.eap71.playground.Simple;
import org.jboss.wfink.eap71.playground.client.logging.AbstractLoggerMain;
import org.wildfly.naming.client.WildFlyInitialContextFactory;

/**
 * <p>Client which lookup a UserTransaction and check whether the invocations within the same transaction are automatically sticky<br/>
 * This is to ensure that one application is invoked always on the same server as cached data of JPA entities might become stale otherwise</p>
 * <p>
 * Test make current no sense because of the behaviour of Transaction lookup
 * </p>
 * 
 * @author <a href="mailto:wfink@redhat.com">Wolf-Dieter Fink</a>
 */
public class UserTransactionWildFlyConfigClient extends AbstractLoggerMain {
	private static final Logger log = Logger.getLogger(UserTransactionWildFlyConfigClient.class.getName());
	
	public static void main(String[] args) throws NamingException, NotSupportedException, SystemException {
		checkArgs(args);

		// Set I_C_F with properties and use wildfly-config.xml to set the server URI and credentials
		Properties p = new Properties();
		p.put(Context.INITIAL_CONTEXT_FACTORY, WildFlyInitialContextFactory.class.getName());
		InitialContext ic = new InitialContext(p);
		HashSet<String> serverList = new HashSet<>();

		Simple proxy = (Simple) ic.lookup("ejb:EAP71-PLAYGROUND-server/ejb/SimpleBean!" + Simple.class.getName());

		try {
			for (int i = 0; i < 20; i++) {
				serverList.add(proxy.getJBossServerName());
			}
		} catch (Exception e) {
			throw new RuntimeException("Invocation test failed!", e);
		}
		
		if(serverList.size() > 1) {
			log.info("Multiple servers as expected used (no Tx), the invocation was executed on the following servers : " + serverList);
		}else{
			throw new RuntimeException("No multiple servers used, test for Tx stickyness is useless!");
		}
		
		if(serverList.size() > 1) {
			serverList.clear();
			UserTransaction uTx = (UserTransaction) ic.lookup("txn:UserTransaction");
			log.finer("UTX is " + uTx);
			boolean multipleServers = false;

			try {
				String txServerName = null;
				// test in a loop whether 
				// 1) the Tx is sticky to one node
				// 2) not only one node is used for different transactions
				for(int i = 0 ; i <10 && !multipleServers; i++) {
					uTx.begin();
					for (int j = 0; j < 20; j++) {
						serverList.add(proxy.getJBossServerName());
					}
					uTx.commit();
					if(serverList.size() > 1) {
						log.severe("Multiple servers are used, transacion is not STICKY!  " + serverList);
						throw new RuntimeException("Transaction is not sticky, multiple servers are used " + serverList);
					}else if(serverList.size() != 1) {
						throw new RuntimeException("Unexpected result, no server list!");
					}

					if(txServerName != null) {
						if(!serverList.contains(txServerName)) {
							multipleServers = true;	// server name is different for different transactions
						}
					}
					txServerName = serverList.iterator().next();  // store server name
					serverList.clear();
				}
			} catch (SystemException | SecurityException | IllegalStateException | RollbackException | HeuristicMixedException | HeuristicRollbackException | NotSupportedException e) {
				try {
					uTx.rollback();
				} catch (IllegalStateException | SecurityException | SystemException e1) {
					log.severe("UTx rollback failed!!");
					e1.printStackTrace();
				}
				throw new RuntimeException("Transaction failure!",e);
			}
			log.info("Transactions seems sticky, " + (multipleServers ? "multiple servers are used for different transactions" : "ONLY ONE server is used for different transactions"));
		}
	}
}
