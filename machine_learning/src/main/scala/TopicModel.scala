
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.ml.feature._
import org.apache.spark.ml.clustering.LDA

object TopicModel {
  def main(args: Array[String]): Unit = {

    // 1. Create SparkSession
    val spark = SparkSession.builder()
      .appName("LDATopicModeling")
      .master("local[*]")
      .getOrCreate()

    spark.sparkContext.setLogLevel("WARN")

    // 2. Read the documents CSV: (id, text)
    val resourcePath = getClass.getResource("/topics.csv").getPath
    val rawDF = spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv(resourcePath)

    println("=== Sample of loaded documents ===")
    rawDF.show(false)

    // 3. Preprocessing: Tokenize, remove stopwords, convert to feature vectors
    // (a) Tokenize
    val tokenizer = new Tokenizer()
      .setInputCol("text")
      .setOutputCol("words")

    // (b) Remove stopwords
    val stopWordsRemover = new StopWordsRemover()
      .setInputCol("words")
      .setOutputCol("filteredWords")

    // (c) Convert words to term frequency (CountVectorizer)
    //     or use HashingTF. CountVectorizer learns a vocabulary from the data.
    val cv = new CountVectorizer()
      .setInputCol("filteredWords")
      .setOutputCol("features")
      .setVocabSize(1000)

    // 4. Build the transformations
    val wordsDF = tokenizer.transform(rawDF)
    val filteredDF = stopWordsRemover.transform(wordsDF)

    // Fit CountVectorizer to get vocabulary, then transform
    val cvModel = cv.fit(filteredDF)
    val countVectors = cvModel.transform(filteredDF)
    // For LDA, we typically rename "features" to "features" indeed. We'll keep it as is.

    println("=== After CountVectorizer (features) ===")
    countVectors.select("id", "filteredWords", "features").show(false)

    // 5. Train an LDA model with 2 topics
    val k = 2
    val lda = new LDA()
      .setK(k)
      .setMaxIter(10)
      .setFeaturesCol("features")

    val ldaModel = lda.fit(countVectors)

    // 6. Describe topics
    // The vocabulary comes from cvModel.vocabulary
    val vocab = cvModel.vocabulary

    val topics = ldaModel.describeTopics(maxTermsPerTopic = 5)
    println("=== Top terms per topic ===")
    topics.show(false)

    // Convert term indices to terms (words)
    val topicsArray = topics.collect()
    for (row <- topicsArray) {
      val topicIdx = row.getAs[Int]("topic")
      val termIndices = row.getAs[Seq[Int]]("termIndices")
      val termWeights = row.getAs[Seq[Double]]("termWeights")

      println(s"\n--- Topic $topicIdx ---")
      termIndices.zip(termWeights).foreach { case (termIdx, weight) =>
        println(s"  ${vocab(termIdx)}\tweight: $weight")
      }
    }

    // 7. Transform documents to see topic distribution
    val transformedDF = ldaModel.transform(countVectors)
    println("=== Topic distribution for each document ===")
    transformedDF.select("id", "text", "topicDistribution").show(false)

    // 8. Stop Spark
    spark.stop()
  }
}
