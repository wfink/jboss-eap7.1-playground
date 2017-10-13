if [ -z "$JBOSS_HOME" ];then
  echo "JBOSS_HOME not set"
  exit 1
fi

if [ -z "$JAVA_HOME" ];then
  JAVACMD="java"
else
  JAVACMD="$JAVA_HOME/bin/java"
fi

CLASSPATH="clients/Server2Server/target/eap71-playground-clients-Server2Server.jar:clients/common/target/eap71-playground-clients-common.jar"
CLASSPATH="$CLASSPATH:mainServer/icApp/ejb/target/eap71-playground-mainServer-icAppEjb-client.jar"
CLASSPATH="$CLASSPATH:mainServer/rocApp/ejb/target/EAP71-PLAYGROUND-MainServer-rocApp-client.jar"
CLASSPATH="$CLASSPATH:$JBOSS_HOME/bin/client/jboss-client.jar"


echo $CLASSPATH

echo
echo " use server2server invocation by having a dedicated InitialContext with properties"
echo " the call check the user  'delegateUser' -> 'delegateUserR'"
read -p  "  run [yn]? " yn
echo
[ "$yn" = "y" ] && $JAVACMD -cp $CLASSPATH org.jboss.wfink.eap71.playground.client.DelegateClient $CLIENT_ARGS

echo
echo " run server2server with dedicated connection and transaction from mainServer to Node"
echo " there are a successful invocation and a setRollbackOnly and RuntimeException on each node"
echo " Check must be manual!"
read -p "  run [yn]? " yn
echo
[ "$yn" = "y" ] && $JAVACMD -cp $CLASSPATH org.jboss.wfink.eap71.playground.client.CheckDelegateTxClient $CLIENT_ARGS

echo
echo "  run server2server invocation with remote-outbound-connection without Transaction"
echo "  the expected user is 'delegateUser' as it should propagated to the backend"
echo "  if there is a cluster it is expected that that invocations are load-balanced"
echo "  expect no error"
read -p "  run [yn]? " yn
echo
[ "$yn" = "y" ] && $JAVACMD -cp $CLASSPATH org.jboss.wfink.eap71.playground.client.DelegateROCClient $CLIENT_ARGS

echo
echo "  run server2server with remote-outbound-connection  [yn] " yn
echo "  expect no error, as the transaction should stick to one node if active the node list should be a single node"
read -p "  run server2server with remote-outbound-connection  [yn] " yn
echo
[ "$yn" = "y" ] && $JAVACMD -cp $CLASSPATH org.jboss.wfink.eap71.playground.client.CheckDelegateTxROCClient $CLIENT_ARGS

echo
echo
echo " run server2server with remote-outbound-connectin and node selector applied - Server2Server LegacyS2SwithNodeSelectorClient"
echo "   Try multiple invocations from server to server and check whether the NodeSelection is working for deployment and cluster node selector"
echo "   for the first invocation after startup or no cluster environment the DeplymentNodeSelector is expected to work"
echo "   for cluster environment the cluster node selection should be used after the first initial invocation"
echo "   check manually the mainServer logfile"
read -p "  run [y]? " yn
echo
[ "$yn" = "y" ] && $JAVACMD -cp ${CLASSPATH} org.jboss.wfink.eap71.playground.client.DelegateROCwithNodeSelectorClient $CLIENT_ARGS

