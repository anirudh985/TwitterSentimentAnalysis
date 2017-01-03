package usingNaiveBayes

import Configure.Conf
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.feature._
import org.apache.spark.sql.DataFrame

import scala.util.{Failure, Success}

/**
  * Created by aj on 12/26/16.
  */
object Preprocess {

  def createTrainingDataFrame(): DataFrame = {
    val spark = Conf.spark
    val sentenceData = spark.createDataFrame(readTrainingData("./training.txt").getOrElse(List()).toList)
                            .toDF("label", "sentence")
    sentenceData
  }

  def getPreprocessingDataModel() = {
    val pipeline = getPreprocessingPipeline()
    pipeline.fit(createTrainingDataFrame())
  }

  def getPreprocessingPipeline() = {
    new Pipeline().setStages(Array(getTokenizer(), getStopWordsRemover(), getCountVectorizer()))
  }

  def getTokenizer(): RegexTokenizer = {
    new RegexTokenizer().setInputCol("sentence").setOutputCol("words").setToLowercase(true).setPattern("\\W+")
  }

  def getStopWordsRemover(): StopWordsRemover = {
    val stopWordsList = StopWordsRemover.loadDefaultStopWords("english")
    stopWordsList ++ Array("da", "vinci", "code", "mission", "impossible", "harry", "potter", "brokeback", "mountain")
    new StopWordsRemover().setInputCol("words").setOutputCol("filtered").setStopWords(stopWordsList)
  }

  def getCountVectorizer(): CountVectorizer = {
    new CountVectorizer().setInputCol("filtered").setOutputCol("features")
  }

  def getIDF(): IDF = {
    new IDF().setInputCol("features").setOutputCol("idfFeatures")
  }

  def readTrainingData(filename: String): Option[Iterator[(Double, String)]] = {
    Conf.readFile(filename) match {
      case Success(lines) => Some(lines.map{ line =>
        val lineContents = line.split("\t")
        (lineContents(0).toDouble, lineContents(1))
      })
      case Failure(f) => {
        println(f)
        None
      }
    }
  }
}
