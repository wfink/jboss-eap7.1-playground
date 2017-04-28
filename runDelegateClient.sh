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
echo "  ----------  run server2server with dedicated connection  ------------------"
echo
$JAVACMD -cp $CLASSPATH org.jboss.wfink.eap71.playground.client.DelegateClient

echo
echo "  ----------  run server2server with remote-outbound-connection  ------------------"
echo
$JAVACMD -cp $CLASSPATH org.jboss.wfink.eap71.playground.client.DelegateROCClient
