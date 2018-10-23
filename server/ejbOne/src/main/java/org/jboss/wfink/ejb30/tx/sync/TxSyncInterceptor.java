package org.jboss.wfink.ejb30.tx.sync;

import java.util.logging.Logger;

import javax.transaction.Synchronization;

public class TxSyncInterceptor implements Synchronization {
	private static final Logger log = Logger.getLogger(TxSyncInterceptor.class.getName());
	private final boolean failBeforeCommit;

	public TxSyncInterceptor(boolean failBeforeCommit) {
		this.failBeforeCommit = failBeforeCommit;
	}

	@Override
	public void afterCompletion(int txStatus) {
		if(failBeforeCommit) {
			if(txStatus != 4) {  // !ROLLEDBACK
				log.severe("Unexpected state, should be ROLLEDBACK but is : " + txStatus);
			}else{
				log.info("TxSyncInterceptor.afterCompletition called with Tx state : " + txStatus);
			}
		}else{
			if(txStatus != 3) {  // !STATUS_COMMITTED
				log.severe("Unexpected state, should be STATUS_COMMITTED but is : " + txStatus);
			}else{
				log.info("TxSyncInterceptor.afterCompletition called with Tx state : " + txStatus);
			}
		}
		
	}

	@Override
	public void beforeCompletion() {
		log.info("TxSyncInterceptor.beforeCompletition called");
	}

}
