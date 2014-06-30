---
layout: post
title:  "Integrating Neo4j 2.x And JBoss WildFly"
---

Last week there was a discussion about how Neo4j could be integrated into a Java EE environment. Looking at JPA there are two main problems to be solved:

1. Mapping of Java classes to tables
2. Injection of an EntityManager as a (pseudo-)transactional resource into beans (e.g. session beans)

Where the first problem is already being addressed by eXtended Objects (probably better than JPA does...) it would be good to know how the second one could be solved for implementing the feature requests [#72](https://github.com/buschmais/extended-objects/issues/72) and [#92](https://github.com/buschmais/extended-objects/issues/92).

There's good news about that: it's quite easy getting the transaction stuff working if you use the [Neo4j JDBC driver](https://github.com/neo4j-contrib/neo4j-jdbc). Let's demonstrate a setup which has been tested with JBoss AS 7.1.1.Final and JBoss WildFly 8.1.0.Final.

## Using a Neo4j datasource within JBoss WildFly

1. Download the Neo4j JDBC driver which includes all required dependencies. Currently the latest greatest available artifact is [neo4j-jdbc-2.0.2-jar-with-dependencies.jar](http://m2.neo4j.org/content/repositories/releases/org/neo4j/neo4j-jdbc/2.0.2/neo4j-jdbc-2.0.2-jar-with-dependencies.jar).
2. Copy the downloaded file to the deployment directory of your JBoss installation (e.g. JBOSS_HOME/standalone/deployments).
3. Add the following datasource declaration to your JBoss configuration (e.g. JBOSS_HOME/standalone/configuration):

```xml
<datasource jndi-name="java:jboss/datasources/Neo4jDS"
			pool-name="Neo4jDS" 
			enabled="true"
			use-java-context="true">
	<connection-url>jdbc:neo4j://localhost:7474/</connection-url>
    <driver>neo4j-jdbc-2.0.2-jar-with-dependencies.jar</driver>
</datasource>
```

*Note:* The driver name must match the file you dropped into the JBoss deployment directory.

4. You can now inject Neo4j datasources into your beans and work with them:

```java
@Stateless
public class SessionBean {

    @Resource(mappedName = "java:jboss/datasources/Neo4jDS")
	private DataSource dataSource;
	
	public void doSomething() {
		try (Connection connection = dataSource.getConnection()) {
		    String cypher = "create (n) return n";
			PreparedStatement statement = connection.prepareStatement(cypher);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
			  // do something with your result
			}
			resultSet.close();
			statement.close();
		} catch (SQLException e) {
			throw new EJBException("Cannot execute cypher statement.", e);
		} 
	}
}
```


## What you should know
1. Currently only neo4j:// URLs can be used, i.e. only connections to remote Neo4j servers are supported. The JDBC driver currently is missing some required dependencies for embedded database modes (i.e. "neo4j:file" and "neo4j:mem" URLs). This is already reported to the maintainer and will probably be fixed soon.
2. The JDBC driver participates in the transaction managed by the container as a local resource. This means that everything is ok as long as no other local resource (e.g. another non-XA JDBC driver) is used during the same transaction. Thus you can use the Neo4j datasource together with JMS connections as long as you're using/injecting a connection factory which supports XA:


```java
	@Resource(mappedName = "java:/JmsXA")
	private ConnectionFactory connectionFactory;
```


This approach of mixing local and XA resources within the same transaction is supported by modern application servers and known as the "Last Resource Gambit" or "Last Resource Commit Optimization (LRCO)". You can find a description of this approach [here](https://access.redhat.com/site/documentation/en-US/JBoss_Enterprise_Application_Platform/5/html/Administration_And_Configuration_Guide/lrco-overview.html). 


Have fun!