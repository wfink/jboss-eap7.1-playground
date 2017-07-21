if [ -z "$JBOSS_HOME" ];then
  echo "JBOSS_HOME not set"
  exit 1
fi

if [ -z "$JAVA_HOME" ];then
  JAVACMD="java"
else
  JAVACMD="$JAVA_HOME/bin/java"
fi

CLASSPATH="clients/WildFlyConfig/target/eap71-playground-clients-WildFlyConfigClient.jar:clients/common/target/eap71-playground-clients-common.jar:server/ejb/target/eap71-playground-server-ejb-client.jar"
CLASSPATH="$CLASSPATH:$JBOSS_HOME/bin/client/jboss-client.jar"


echo $CLASSPATH

echo
echo "  ----------  run SimpleClient with wildfly-confix.xml ------------------"
echo
echo "$JAVACMD -cp $CLASSPATH:clients/WildFlyConfig/config/user1 org.jboss.wfink.eap71.playground.wildfly.client.SimpleWildFlyConfigClient"
$JAVACMD -cp $CLASSPATH:clients/WildFlyConfig/config/user1 org.jboss.wfink.eap71.playground.wildfly.client.SimpleWildFlyConfigClient

echo
echo "  ----------  run wildfly-config client and override the user ------------------"
echo
$JAVACMD -cp $CLASSPATH:clients/WildFlyConfig/config/user1 org.jboss.wfink.eap71.playground.wildfly.client.UserOverrideWildFlyConfigClient

echo
echo "  ----------  run wildfly-config client without any property ------------------"
echo
$JAVACMD -cp $CLASSPATH:clients/WildFlyConfig/config/admin org.jboss.wfink.eap71.playground.wildfly.client.WildFlyICConfigClient
