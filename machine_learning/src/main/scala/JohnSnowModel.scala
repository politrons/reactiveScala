

import org.apache.spark.sql.SparkSession
import com.johnsnowlabs.nlp.pretrained.PretrainedPipeline

object JohnSnowModel {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("SparkNlpExample")
      .master("local[*]")
      .getOrCreate()

    spark.sparkContext.setLogLevel("WARN")

    import spark.implicits._

    // Sample DataFrame with text
    val data = Seq(
      (1, "Barack Obama was born in Hawaii."),
      (2, "Spark NLP is an open-source library by John Snow Labs."),
      (3, "I love using Scala for Big Data and AI projects!"),
      (4, "The developer Politrons love experiment new technologies like Spark or AI models"),
      (5, "Politrons is living in a House in Madrid")


    )
    val df = data.toDF("id", "text")

    // Download a pretrained pipeline; e.g. "explain_document_dl"
    val pipeline = PretrainedPipeline("explain_document_dl", lang = "en")

    // Instead of annotate(df, "text"), do pipeline.transform(df)
    val resultDF = pipeline.transform(df)

    // Now check the new columns it created (e.g. "entities", "document", "token", etc.)
    resultDF.printSchema()
    resultDF.select("id", "text", "entities.result").show(false)

    spark.stop()
  }
}
