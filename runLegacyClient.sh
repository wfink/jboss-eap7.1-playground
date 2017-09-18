if [ -z "$JBOSS_HOME" ];then
  echo "JBOSS_HOME not set"
  exit 1
fi

if [ -z "$JAVA_HOME" ];then
  JAVACMD="java"
else
  JAVACMD="$JAVA_HOME/bin/java"
fi

CLASSPATH="clients/JBossClientProperties/target/eap71-playground-clients-JBossEjbClientConfig.jar:server/ejb/target/eap71-playground-server-ejb-client.jar"
CLASSPATH="$CLASSPATH:$JBOSS_HOME/bin/client/jboss-client.jar"


echo $CLASSPATH

echo
read -p "  run without any jboss-ejb-client.properties  [y] " yn
echo
[ "$yn" = "y" ] && $JAVACMD -cp $CLASSPATH org.jboss.wfink.eap71.playground.client.legacy.LegacyJBossEjbClient

echo
echo
read -p "  run with basic properties noSecurity - without any user defined  [y] " yn
echo
[ "$yn" = "y" ] && $JAVACMD -cp ${CLASSPATH}:clients/JBossClientProperties/properties/noSecurity org.jboss.wfink.eap71.playground.client.legacy.LegacyJBossEjbClient

echo
echo
read -p "  run with basic properties as user1  [y] " yn
echo
[ "$yn" = "y" ] && $JAVACMD -cp ${CLASSPATH}:clients/JBossClientProperties/properties/user1 org.jboss.wfink.eap71.playground.client.legacy.LegacyJBossEjbClient

echo
echo
read -p "  run with basic properties as admin  [y] " yn
echo
[ "$yn" = "y" ] && $JAVACMD -cp ${CLASSPATH}:clients/JBossClientProperties/properties/admin org.jboss.wfink.eap71.playground.client.legacy.LegacyJBossEjbClient

echo
echo
read -p "  run with basic properties as user with one server URL test loadbalancing  [y] " yn
echo
[ "$yn" = "y" ] && $JAVACMD -cp ${CLASSPATH}:clients/JBossClientProperties/properties/user1 org.jboss.wfink.eap71.playground.client.legacy.LegacyClusterJBossEjbClient

echo
echo
read -p "  run with basic properties as user with two server URL test loadbalancing  [y] " yn
echo
[ "$yn" = "y" ] && $JAVACMD -cp ${CLASSPATH}:clients/JBossClientProperties/properties/twoServer org.jboss.wfink.eap71.playground.client.legacy.LegacyClusterJBossEjbClient
