import java.sql.{DriverManager, SQLException}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.sys.process._

/**
  * Created by pabloperezgarcia on 18/07/2017.
  */
object H2Runner extends App {

  initH2()
  createTable()

  def initH2(): Future[Int] = {
    Future {
      val currentDirectory = new java.io.File(".").getCanonicalPath + "/h2/src/main/resources"
      val RUN_H2_COMMAND = "java -cp h2-1.3.161.jar org.h2.tools.Server -ifExists -tcp -web -tcpAllowOthers"
      Process(Seq("bash", "-c", s"cd $currentDirectory && $RUN_H2_COMMAND")).!
    }
  }

  def createTable() {
    runSQLScript(0, "CREATE TABLE IF NOT EXISTS DG2_F2E_ENTITY(ENTITY_ID INT, TITLE VARCHAR(255),DESCRIPTION VARCHAR(255) )")
    println("Table created complete.....")
  }

  def cleanTable() {
    runSQLScript(0, "DELETE FROM DG2_F2E_ENTITY")
    println("Table deleted complete.....")
  }

  private def runSQLScript(retries: Int, script: String) {
    val conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/sakila", "sa", "")
    val st = conn.createStatement
    try {
      st.execute(script)
    } catch {
      case e: SQLException =>
        if (retries > 3) {
          println(s"Error initializing h2 ${e.printStackTrace()}")
        } else {
          Thread.sleep(1000)
          runSQLScript(retries + 1, script)
        }
    } finally {
      if (conn != null) conn.close()
      if (st != null) st.close()
    }
  }

}