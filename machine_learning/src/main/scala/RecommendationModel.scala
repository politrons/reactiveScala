
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.DataFrame
import org.apache.spark.ml.recommendation.ALS
import org.apache.spark.ml.evaluation.RegressionEvaluator

object RecommendationModel {

  def main(args: Array[String]): Unit = {
    val spark = createSparkSession
    spark.sparkContext.setLogLevel("WARN")

    val resourcePath = getClass.getResource("/ratings.csv").getPath
    val ratingsDF = readRatings(spark, resourcePath)

    println("=== Show read data ===")
    ratingsDF.show()
    ratingsDF.printSchema()

    // Div in Train and Test
    val Array(training, test) = ratingsDF.randomSplit(Array(0.8, 0.2), seed = 123L)

    val als = configureALS

    // Train model
    val model = als.fit(training)

    // Make predictions
    val predictions = model.transform(test)
    println("=== Predictions on the Set of Test ===")
    predictions.show()

    val evaluator = createEvaluator

    val rmse = evaluator.evaluate(predictions)
    println(s"RMSE in Test = $rmse")

    // Obtain recommendations: Top 3 items per user
    val userRecs = model.recommendForAllUsers(3)
    println("=== Recommendations ===")
    userRecs.show(truncate = false)

    spark.stop()
  }

  private def createEvaluator = {
    new RegressionEvaluator()
      .setMetricName("rmse")
      .setLabelCol("rating")
      .setPredictionCol("prediction")
  }

  private def configureALS = {
    new ALS()
      .setUserCol("userId")
      .setItemCol("itemId")
      .setRatingCol("rating")
      .setMaxIter(10)
      .setRank(10)
      .setRegParam(0.1)
      .setColdStartStrategy("drop")
  }

  private def readRatings(spark: SparkSession, resourcePath: String) = {
    spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv(resourcePath)
  }

  private def createSparkSession = {
    SparkSession.builder()
      .appName("ALSExample")
      .master("local[*]") // "local[*]" use all cores from machine
      .getOrCreate()
  }
}
