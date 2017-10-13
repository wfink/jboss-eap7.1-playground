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


copy the application to mainNode

    cp mainServer/rocApp/ear/target/EAP71-PLAYGROUND-MainServer-rocApp.ear JBOSS_HOME/mainNode/deployments



Test remote-oubound-connections with user forwarding
----------------------------------------------------

   Since 7.1 it is possible to use Elytron to forward the user to the backend server.
   See documentation and https://access.redhat.com/solutions/3166981
   Note that the configuration use a "connectionUser" with cleartext password (here 'connectionUser') to secure the connection.
   But the user shown in the application is forwarded from the client-server.

   1) Add the client configuration for elytron to forward the security
 
      bin/jboss-cli.sh -c --controller=localhost:9990

      ?NOT NEEDED? /subsystem=ejb3/application-security-domain=other:add(security-domain=ApplicationDomain)

      /subsystem=elytron/authentication-configuration=forwardit:add(authentication-name=connectionUser, security-domain=ApplicationDomain, forwarding-mode=authorization, realm=ApplicationRealm, credential-reference={clear-text=connectionUser!})
      /subsystem=elytron/authentication-context=forwardctx:add(match-rules=[{match-no-user=true,authentication-configuration=forwardit}])
      /subsystem=elytron:write-attribute(name=default-authentication-context, value=forwardctx)

      ??? NOT NEEDED  simple-permission mapper ????

      /subsystem=remoting/http-connector=http-remoting-connector:write-attribute(name=sasl-authentication-factory, value=application-sasl-authentication)

      ?NOT NEEDED?  /subsystem=undertow/application-security-domain=other:add(http-authentication-factory=application-http-authentication)


   2) Add the server configuration for ejb/elytron to handle the security forwarding

      bin/jboss-cli.sh -c --controller=localhost:10990
      ?NOT NEEDED? /subsystem=ejb3/application-security-domain=other:add(security-domain=ApplicationDomain)

      ?NOT NEEDED  to delete: mapping-mode=first?

      /subsystem=elytron/simple-permission-mapper=default-permission-mapper:list-add(index=0, name=permission-mappings,\
        value={principals=[connectionUser],\
        permissions=[{class-name=org.wildfly.security.auth.permission.RunAsPrincipalPermission, target-name=*},\
          {class-name=org.wildfly.security.auth.permission.LoginPermission},\
          {class-name=org.wildfly.extension.batch.jberet.deployment.BatchPermission, module=org.wildfly.extension.batch.jberet, target-name=*},\
          {class-name=org.wildfly.transaction.client.RemoteTransactionPermission, module=org.wildfly.transaction.client},\
          {class-name=org.jboss.ejb.client.RemoteEJBPermission, module=org.jboss.ejb-client}\
        ]})

      /subsystem=remoting/http-connector=http-remoting-connector:write-attribute(name=sasl-authentication-factory, value=application-sasl-authentication)

      ?NOT NEEDED?  /subsystem=undertow/application-security-domain=other:add(http-authentication-factory=application-http-authentication)




Enable client side logging
---------------------------

The environment $CLIENT_ARGS can be set to "-log WCRT"
where the position is the category
  W  org.wildfly
  C  org.jboss.ejb.client
  R  org.xnio org.jboss.remoting
  T  org.jboss.wfink

and the number is the level
  0  Off
  1  Warn
  2 Info
  3 Fine
  4 Finer
  5 Finest
  9 ALL

If not give it defaults to "-log 9" to show the messages from the test classes
