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



Clients can be run with the following scripts.
You need to set JBOSS_HOME and maybe JAVA_HOME if needed.

   - Client using properties passed to InitialContext, run the script runAllClients.sh
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
