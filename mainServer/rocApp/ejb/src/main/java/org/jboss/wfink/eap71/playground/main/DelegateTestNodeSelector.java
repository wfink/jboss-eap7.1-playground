/*
 * The following information has been provided by Red Hat, but is outside the scope
 * of the posted Service Level Agreements and support procedures
 * (https://access.redhat.com/support/offerings/production/).
 * The information is provided as-is and any configuration settings or installed
 * applications made from the information in this article could make the
 * Operating System unsupported by Red Hat Global Support Services.
 * The intent of this article is to provide information to accomplish the system's needs.
 * Use of the information in this article at the user's own risk.
 */
package org.jboss.wfink.eap71.playground.main;

import java.util.Arrays;
import java.util.Random;

import org.jboss.ejb.client.ClusterNodeSelector;
import org.jboss.ejb.client.DeploymentNodeSelector;
import org.jboss.logging.Logger;

/**
 * <p>
 * A combined selector for ejb-client node selection, should be used as DeploymentNodeSelector and ClusterNodeSelector in the jboss-ejb-client.*
 * configuration to ensure a proper function.<br/>
 * More information can be found in this <a href="https://access.redhat.com/solutions/1355733">Red Hat Customer Portal Knowledgebase article</a>
 * </p>
 * <p>
 * Simple show the invocation.
 * </p>
 *
 * @author <a href="mailto:wfink@redhat.com">Wolf-Dieter Fink</a>
 */
public class DelegateTestNodeSelector implements DeploymentNodeSelector, ClusterNodeSelector {
  private static final Logger log = Logger.getLogger(DelegateTestNodeSelector.class);


  /**
   * Select a node from the already used nodes if possible.
   *
   * @param nodes The current node list to choose
   * @return a preferred node or <code>null</code> if no preferred or no match with nodes
   */
  public String choosePreferredNode(String[] nodes) {
    String node = null;

    if (nodes.length == 1) {
      // Just a single node available, no choice just return it
      node = nodes[0];
    }
    if (node == null) {
      final Random random = new Random();
      final int randomSelection = random.nextInt(nodes.length);
      node = nodes[randomSelection];
    }
    return node;
  }

  @Override
  public String selectNode(String[] eligibleNodes, String appName, String moduleName, String distinctName) {
    String node = null;

    if(log.isTraceEnabled()) {
      log.tracef("selectNode for application = %s modul = %s distinctName = %s nodes [%s]", appName, moduleName, distinctName, Arrays.deepToString(eligibleNodes));
    }

    node = choosePreferredNode(eligibleNodes);

    log.infof("selected node is %s for app %s out of %s", node, appName, Arrays.deepToString(eligibleNodes));
    return node;
  }

  @Override
  public String selectNode(final String clusterName, final String[] connectedNodes, final String[] availableNodes) {
    String node = null;

    if(log.isTraceEnabled()) {
      log.tracef("selectNode for cluster = %s connected nodes [%s] available nodes [%s]", clusterName, Arrays.deepToString(connectedNodes), Arrays.deepToString(availableNodes));
    }

    node = choosePreferredNode(availableNodes);

    log.infof("selected node is %s for cluster %s out of %s", node, clusterName, Arrays.deepToString(availableNodes));
    return node;
  }
}
