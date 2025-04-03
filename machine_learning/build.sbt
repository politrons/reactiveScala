name := "machine_learning"
version := "0.1"

// Usa Scala 2.12.x (o 2.13.x si prefieres, pero 2.12 es más común)
scalaVersion := "2.12.15"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core"  % "3.3.1",
  "org.apache.spark" %% "spark-sql"   % "3.3.1",
  "org.apache.spark" %% "spark-mllib" % "3.3.1"
)

