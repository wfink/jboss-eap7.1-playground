# jboss-eap7.1-playground
Different solutions to show implementation, use and behaviour of applications deployed inside Red Hat JBoss EAP 7.1 container.
As clients there are 

- standalone client which use pure InitialContext with properties
- standalone client which use jboss-ejb-client.properties as legacy configuration



Create two instances by use a fresh EAP7.1.x instance

Add users:

   bin/add-user.sh -a -u user1 -p user1+
   bin/add-user.sh -a -u admin -p admin -g Admin


copy standalone folder to node1 and node2

Start the instance  depend on the test with standalone or standalone-ha profile :

   bin/standalone.sh -Djboss.server.base.dir=node1 -Djboss.node.name=node1 -Djboss.server.name=node1 -Djboss.socket.binding.port-offset=0 -c standalone.xml

   bin/standalone.sh -Djboss.server.base.dir=node2 -Djboss.node.name=node2 -Djboss.server.name=node2 -Djboss.socket.binding.port-offset=100 -c standalone.xml

Copy the server application to both nodes

   cp server/ear/target/EAP71-PLAYGROUND-server.ear EAP_HOME/node?/deployments


Run the client's with th script runAllClients.sh.

Clients can be run with the following scripts.
You need to set JBOSS_HOME and maybe JAVA_HOME if needed.

   - Client using properties passed to InitialContext, run the script runInitialContextClients.sh
   - Client using wildfly-config.xml, run script runWildFlyConfigClients.sh
   - Client using legacy property file jboss-ejb-client.properties, run script runLegacyClient.sh



Server configurations:
-----------------------
It is also possible to check the behaviour with different server configurations, like 
- removing <local> to prevent from unknown users
- removing security from http-connector
- change user and role configuration

Legacy Client:
---------------
This client will use jboss-ejb-client.properties file as already known in EAP6 to connect the server.
The different properties are located in the properties directory and can be used via classpath setting for the client to show different use cases.



Test server 2 server communication
==================================

To test server-server communication we need to add another node

copy standalone folder to mainNode and add another user

    bin/add-user.sh -a -u delegateUser -p delegateUser -g Application -sc mainNode/configuration
    bin/add-user.sh -a -u delegateUserR -p delegateUser -g Application -sc node1/configuration
    bin/add-user.sh -a -u delegateUserR -p delegateUser -g Application -sc node2/configuration

copy the application to mainNode

    cp mainServer/icApp/ear/target/EAP71-PLAYGROUND-MainServer-icApp.ear JBOSS_HOME/mainNode/deployments

Start the instance:

    bin/standalone.sh -Djboss.server.base.dir=mainNode -Djboss.node.name=mainNode -Djboss.server.name=mainNode -Djboss.socket.binding.port-offset=1000

Test the delegation with InitialContext connection:

    ./runDelegateClient.sh

The DelegateROC client will not work unless the configuration is added, see next section



Test remote-outbound-connections
-------------------------------

Add a connection user to the target node(s):

    bin/add-user.sh -a -u connectionUser -p connectionUser -sc node1/configuration
    bin/add-user.sh -a -u connectionUser -p connectionUser -sc node2/configuration

Add the remote outbound connection via CLI to the mainNode:

    /socket-binding-group=standard-sockets/remote-destination-outbound-socket-binding=remote-ejb:add(host=localhost, port=8080)
    /core-service=management/security-realm=ejb-security-realm:add()
    /core-service=management/security-realm=ejb-security-realm/server-identity=secret:add(value="Y29ubmVjdGlvblVzZXI=")
    /subsystem=remoting/remote-outbound-connection=remote-ejb-connection:add(outbound-socket-binding-ref=remote-ejb, protocol=http-remoting, security-realm=ejb-security-realm, username=connectionUser)
    /subsystem=remoting/remote-outbound-connection=remote-ejb-connection/property=SASL_POLICY_NOANONYMOUS:add(value=false)
    /subsystem=remoting/remote-outbound-connection=remote-ejb-connection/property=SSL_ENABLED:add(value=false)


