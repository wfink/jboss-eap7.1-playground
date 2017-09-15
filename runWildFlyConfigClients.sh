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
read -p "  run SimpleClient with wildfly-confix.xml [user1] [y] " yn
echo
#echo "$JAVACMD -cp $CLASSPATH:clients/WildFlyConfig/config/user1 org.jboss.wfink.eap71.playground.wildfly.client.SimpleWildFlyConfigClient"
[ "$yn" = "y" ] && $JAVACMD -cp $CLASSPATH:clients/WildFlyConfig/config/user1 org.jboss.wfink.eap71.playground.wildfly.client.SimpleWildFlyConfigClient

echo
read -p "  run wildfly-config client and override the user [y] " yn
echo
[ "$yn" = "y" ] && #$JAVACMD -cp $CLASSPATH:clients/WildFlyConfig/config/user1 org.jboss.wfink.eap71.playground.wildfly.client.UserOverrideWildFlyConfigClient

echo
read -p "  run wildfly-config client without any property [y] " yn
echo
[ "$yn" = "y" ] && #$JAVACMD -cp $CLASSPATH:clients/WildFlyConfig/config/admin org.jboss.wfink.eap71.playground.wildfly.client.WildFlyICConfigClient

echo
read -p "  run wildfly-config client with many URL's should show the two servers (even if run in non HA mode) [y] " yn
echo
[ "$yn" = "y" ] && $JAVACMD -cp $CLASSPATH:clients/WildFlyConfig/config/twoServers org.jboss.wfink.eap71.playground.wildfly.client.MultipleServerWildFlyConfigURIClient -log 2229

echo
read -p "  run wildfly-config client with one URL should succeed if run against cluster [user1]  [y] " yn
echo
[ "$yn" = "y" ] && $JAVACMD -cp $CLASSPATH:clients/WildFlyConfig/config/user1 org.jboss.wfink.eap71.playground.wildfly.client.MultipleServerWildFlyConfigURIClient -log 2229
