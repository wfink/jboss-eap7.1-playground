if [ -z "$JBOSS_HOME" ];then
  echo "JBOSS_HOME not set"
  exit 1
fi

if [ -z "$JAVA_HOME" ];then
  JAVACMD="java"
else
  JAVACMD="$JAVA_HOME/bin/java"
fi

CLASSPATH="clients/InitialContext/target/eap71-playground-clients-InitialContext.jar:clients/common/target/eap71-playground-clients-common.jar:server/ejb/target/eap71-playground-server-ejb-client.jar"
CLASSPATH="$CLASSPATH:$JBOSS_HOME/bin/client/jboss-client.jar"


echo $CLASSPATH

echo
echo "  ----------  run SimpleClient  ------------------"
echo
$JAVACMD -cp $CLASSPATH org.jboss.wfink.eap71.playground.client.SimpleClient

echo
echo
echo "  ----------  run SimpleSecuredClient  ------------------"
echo
$JAVACMD -cp $CLASSPATH org.jboss.wfink.eap71.playground.client.SimpleSecuredClient

echo
echo
echo "  ----------  run MultipleServerClient  ------------------"
echo
$JAVACMD -cp $CLASSPATH org.jboss.wfink.eap71.playground.client.MultipleServerClient
