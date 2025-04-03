
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.feature._
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator

object FeedbackModel {
  def main(args: Array[String]): Unit = {

    // 1. Create SparkSession (local mode)
    val spark = SparkSession.builder()
      .appName("SentimentCastExample")
      .master("local[*]")
      .getOrCreate()

    spark.sparkContext.setLogLevel("WARN")

    // 2. Read the CSV where label is a string
    //    Example CSV columns: (id, text, label="0" or "1")
    val resourcePath = getClass.getResource("/sentences.csv").getPath
    val rawDF = spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv(resourcePath)

    println("=== Raw Data ===")
    rawDF.show(false)
    rawDF.printSchema()

    // 3. Cast the 'label' column from string to double
    //    This is crucial so that LogisticRegression sees a numeric label.
    val df = rawDF.withColumn("label", col("label").cast("double"))

    println("=== After Casting Label to Double ===")
    df.show(false)
    df.printSchema()

    // 4. Split into train/test sets
    val Array(trainDF, testDF) = df.randomSplit(Array(0.8, 0.2), seed = 123L)

    // 5. Define the stages of our pipeline

    // (a) Tokenize the 'text' column
    val tokenizer = new Tokenizer()
      .setInputCol("text")
      .setOutputCol("words")

    // (b) Remove stop words
    val stopWordsRemover = new StopWordsRemover()
      .setInputCol("words")
      .setOutputCol("filteredWords")

    // (c) Use HashingTF to convert text to numeric features
    val hashingTF = new HashingTF()
      .setInputCol("filteredWords")
      .setOutputCol("rawFeatures")
      .setNumFeatures(1000)

    // (d) Optional: Use IDF to re-weight term frequencies
    val idf = new IDF()
      .setInputCol("rawFeatures")
      .setOutputCol("features")

    // (e) Logistic Regression
    val lr = new LogisticRegression()
      .setLabelCol("label")
      .setFeaturesCol("features")
      .setMaxIter(10)

    // 6. Build the pipeline
    val pipeline = new Pipeline()
      .setStages(Array(tokenizer, stopWordsRemover, hashingTF, idf, lr))

    // 7. Train the pipeline model
    val model = pipeline.fit(trainDF)

    // 8. Predict on the test set
    val predictions = model.transform(testDF)
    println("=== Predictions on Test Data ===")
    predictions.select("id", "text", "label", "prediction", "probability").show(false)

    // 9. Evaluate (accuracy)
    val evaluator = new MulticlassClassificationEvaluator()
      .setLabelCol("label")
      .setPredictionCol("prediction")
      .setMetricName("accuracy")

    val accuracy = evaluator.evaluate(predictions)
    println(s"Test Accuracy: $accuracy")

    // 10. Example inference on new data
    val newData = Seq(
      (999, "I really dislike this experience"),
      (1000, "What amazing movie"),
      (1001, "total crap movie"),
      (1002, "not bad movie")

    )

    import spark.implicits._
    val newDF = newData.toDF("id", "text")
      // Provide a dummy label so the schema matches
      .withColumn("label", lit(0.0))

    val newPredictions = model.transform(newDF)
    println("=== Predictions on New Data ===")
    newPredictions.select("id", "text", "prediction", "probability").show(false)

    spark.stop()
  }
}

