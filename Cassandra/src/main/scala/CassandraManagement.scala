import com.datastax.driver.core.{Cluster, Metadata, Session}

import scala.collection.JavaConversions._


/**
  * Simple cassandra client, following the datastax documentation
  * (http://www.datastax.com/documentation/developer/java-driver/2.0/java-driver/quick_start/qsSimpleClientCreate_t.html).
  */
object CassandraManagement extends App {

  val defaultTable =
    s"""CREATE TABLE f2e_integration.dg2_f2e_entity (
       |  title varchar,
       |  description varchar,
       |  PRIMARY KEY (title )
       |);"""

  var cluster: Cluster = _
  var session: Session = _

  initCluster("localhost")
  createSchema("f2e_integration")
  createTable()
  close()

  def initCluster(node: String): Unit = {
    cluster = Cluster.builder().addContactPoint(node).build()
    logInit(cluster.getMetadata)
    session = cluster.connect()
  }

  private def logInit(metadata: Metadata): Unit = {
    println(s"Connected to cluster: ${metadata.getClusterName}")
    for (host <- metadata.getAllHosts) {
      println(s"Datatacenter: ${host.getDatacenter}; Host: ${host.getAddress}; Rack: ${host.getRack}")
    }
  }

  def createSchema(keySpace: String): Unit = {
    session.execute(s"CREATE KEYSPACE IF NOT EXISTS $keySpace WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};")
    println(s"Keyspace $keySpace created")
  }

  def createTable(script: String = defaultTable): Unit = {
    session.execute(script)
    println(s"Table created")
  }

  def loadData(script: String): Unit = {
    session.execute(script)
  }

  def deleteData(keySpace: String, table: String): Unit = {
    session.execute(s"""truncate  $keySpace.$table""")
    println(s"Table $keySpace.$table deleted")
  }

  def close() {
    session.close()
    cluster.close()
  }

}