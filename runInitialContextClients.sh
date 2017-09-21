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
echo " Simple client use InitialContext without credentials @8080  - testclient clients-InitialContext SimpleClient"
read -p "  run [y]? " yn
echo
[ "$yn" = "y" ] && $JAVACMD -cp $CLASSPATH org.jboss.wfink.eap71.playground.client.SimpleClient

echo
echo " Simple client use InitialContext, try to invoke @8080  - testclient clients-InitialContext  SimpleSecuredClient"
echo "   1. [unknownUser] @PermitAll might succeed if $local default is configured"
echo "   2. [user1] @PermitAll is expected to work"
echo "   3. [user1] @Role admin  is expected to fail, error is shown only if behaviour is unexpected"
echo "   4. [admin] @Role admin  is expected to work, error is shown only if behaviour is unexpected"
read -p "  run [y]? " yn
echo
[ "$yn" = "y" ] && $JAVACMD -cp $CLASSPATH org.jboss.wfink.eap71.playground.client.SimpleSecuredClient

echo
echo " Simple client use InitialContext, try to invoke multiple nodes  - testclient clients-InitialContext MultipleServerProvideURLClient"
echo "  1. PROVIDER_URL '@8080 and @8180', this should return both nodes for multiple invocations -- if LB work without cluster"
echo "     otherwise is should work even if only one server is started!"
read -p "  run [y]? " yn
echo
[ "$yn" = "y" ] && $JAVACMD -cp $CLASSPATH org.jboss.wfink.eap71.playground.client.MultipleServerProviderURLClient
echo
echo " Legacy remote-naming settings with deprecated InitialContextFactory with @8080 @8180 in PROVIDER_URL  - testclient clients-InitialContext  LegacyMultipleServerRemoteNamingClient"
read -p "  run [y]? " yn
echo
[ "$yn" = "y" ] && $JAVACMD -cp $CLASSPATH org.jboss.wfink.eap71.playground.client.remote.naming.LegacyMultipleServerRemoteNamingClient
