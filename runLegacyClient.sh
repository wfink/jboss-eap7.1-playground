if [ -z "$JBOSS_HOME" ];then
  echo "JBOSS_HOME not set"
  exit 1
fi

if [ -z "$JAVA_HOME" ];then
  JAVACMD="java"
else
  JAVACMD="$JAVA_HOME/bin/java"
fi

CLASSPATH="clients/JBossClientProperties/target/eap71-playground-clients-JBossEjbClientConfig.jar"
CLASSPATH="$CLASSPATH:clients/common/target/eap71-playground-clients-common.jar"
CLASSPATH="$CLASSPATH:server/ejb/target/eap71-playground-server-ejb-client.jar"
CLASSPATH="$CLASSPATH:$JBOSS_HOME/bin/client/jboss-client.jar"

#BYTEMAN="-javaagent:/data/app/byteman-3.0.5/lib/byteman.jar=script:/home/wfink/examples/byteman/EJBClient.NodeSelectorTracking.btm,sys:/data/app/byteman-3.0.5/lib/byteman.jar"


echo $CLASSPATH

echo
echo " run client without any jboss-ejb-client.properties  - JBossEJBClientConfig LegacyJBossEjbClient"
echo "  both invocations are expected to fail as there is no target server"
read -p "  run [y]? " yn
echo
[ "$yn" = "y" ] && $JAVACMD -cp $CLASSPATH org.jboss.wfink.eap71.playground.client.legacy.LegacyJBossEjbClient $CLIENT_ARGS

echo
echo
echo " run client with basic properties noSecurity (no user defined)  - JBossEJBClientConfig LegacyJBossEjbClient"
echo "   first invocation is expected to work if the server is configured with local user"
echo "   second invocation is expected to fail (EJBAccessException) as the ROLE is missing without user"
read -p "  run [y]? " yn
echo
[ "$yn" = "y" ] && $JAVACMD -cp ${CLASSPATH}:clients/JBossClientProperties/properties/noSecurity org.jboss.wfink.eap71.playground.client.legacy.LegacyJBossEjbClient $CLIENT_ARGS

echo
echo
echo " run client with basic properties user1 defined  - JBossEJBClientConfig LegacyJBossEjbClient"
echo "   first invocation is expected to work as the method is annotated @PermitAll"
echo "   second invocation is expected to fail (EJBAccessException) as the ROLE(admin) is missing with user1"
read -p "  run [y]? " yn
echo
[ "$yn" = "y" ] && $JAVACMD -cp ${CLASSPATH}:clients/JBossClientProperties/properties/user1 org.jboss.wfink.eap71.playground.client.legacy.LegacyJBossEjbClient $CLIENT_ARGS

echo
echo
echo " run client with basic properties and user=admin- JBossEJBClientConfig LegacyJBossEjbClient"
echo "   first invocation is expected to work"
echo "   second invocation is expected to work as the user admin has the ROLE(admin)"
read -p "  run [y]? " yn
echo
[ "$yn" = "y" ] && $JAVACMD -cp ${CLASSPATH}:clients/JBossClientProperties/properties/admin org.jboss.wfink.eap71.playground.client.legacy.LegacyJBossEjbClient $CLIENT_ARGS

echo
echo
echo " run client with basic properties @8080 and user=user1 - JBossEJBClientConfig LegacyClusterJBossEjbClient"
echo "   Try multiple invocations with only one initial connection and check whether there are multiple instances used"
echo "   this should be the case if the connected server is part of the cluster, without cluster only one node is expected"
read -p "  run [y]? " yn
echo
[ "$yn" = "y" ] && $JAVACMD -cp ${CLASSPATH}:clients/JBossClientProperties/properties/user1 org.jboss.wfink.eap71.playground.client.legacy.LegacyClusterJBossEjbClient $CLIENT_ARGS

echo
echo
echo " run client with basic properties @8080 @8180 and user=user1 - JBossEJBClientConfig LegacyClusterJBossEjbClient"
echo "   Try multiple invocations with only one initial connection and check whether there are multiple instances used"
echo "   this should be the case if the connected server is part of the cluster, without cluster only one node is expected"
read -p "  run with basic properties as user with two server URL test loadbalancing  [y] " yn
echo
[ "$yn" = "y" ] && $JAVACMD -cp ${CLASSPATH}:clients/JBossClientProperties/properties/twoServer org.jboss.wfink.eap71.playground.client.legacy.LegacyClusterJBossEjbClient $CLIENT_ARGS

echo
echo
echo " run client with basic properties @8080 and user=user1 - JBossEJBClientConfig LegacyClusterJBossEjbClient"
echo "   Try multiple invocations with only one initial connection and check whether the NodeSelector is used"
echo "   if a cluster is connected the Deployment- and ClusterNodeSelector must be used"
read -p "  run [y]? " yn
echo
[ "$yn" = "y" ] && $JAVACMD -cp ${CLASSPATH}:clients/JBossClientProperties/properties/nodeSelector $BYTEMAN org.jboss.wfink.eap71.playground.client.legacy.LegacyClusterJBossEjbClient $CLIENT_ARGS

