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
read -p "  run SimpleClient without credentials [y] " yn
echo
[ "$yn" = "y" ] && $JAVACMD -cp $CLASSPATH org.jboss.wfink.eap71.playground.client.SimpleClient

echo
echo
read -p "  run SimpleSecuredClient with unknown user [y] " yn
echo
[ "$yn" = "y" ] && $JAVACMD -cp $CLASSPATH org.jboss.wfink.eap71.playground.client.SimpleSecuredClient

echo
echo
read -p "  run MultipleServerClient  connect @8080&&@8180 [y] " yn
echo
[ "$yn" = "y" ] && $JAVACMD -cp $CLASSPATH org.jboss.wfink.eap71.playground.client.MultipleServerProviderURLClient
echo
echo
read -p "  run Legacy remote naming client use deprecated InitialContextFactory with @8080&&@8180 in PROVIDER_URL [y] " yn
echo
[ "$yn" = "y" ] && $JAVACMD -cp $CLASSPATH org.jboss.wfink.eap71.playground.client.remote.naming.LegacyMultipleServerRemoteNamingClient
