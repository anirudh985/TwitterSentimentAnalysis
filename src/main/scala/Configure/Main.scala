package Configure

import org.apache.spark.ml.classification.NaiveBayesModel
import usingNLP.SentimentAnalysisUtils
import usingNaiveBayes.NaiveBayesModelUtil

object Main {

  def main(args: Array[String]): Unit = {
    val (stream,ssc) = Conf.getStream()

    NaiveBayesModelUtil.buildAndSaveNaiveBayesModel()
//    testing()

    val tweets1 = stream.filter(status => status.getLang == "en")
      .filter {t =>
        val tags = t.getText.split(" ").filter(x => x.startsWith("#") || x.startsWith("@")).map(_.toLowerCase)
        tags.length > 0
      }

    val data = tweets1.map { status =>
      val regexPattern = "\\b[A-Za-z][A-Za-z0-9]+\\b".r
      val tweetWithoutJunk: String = regexPattern.findAllIn(status.getText).toList.mkString(" ")
      println(tweetWithoutJunk)
      val nlpSentiment = SentimentAnalysisUtils.detectSentiment(tweetWithoutJunk)
      val nbSentiment = naiveBayesPrediction(tweetWithoutJunk)
      (status.getText, tweetWithoutJunk, nlpSentiment.toString, nbSentiment)
    }

    data.foreachRDD {rdd =>
      rdd.foreach {case (tweet, tweetWithoutJunk, nlpSentiment, nbSentiment) => println(s"The tweet is :  $tweet\nThe processed tweet is : $tweetWithoutJunk\nSentiment derived by Stanford Core NLP:  $nlpSentiment\nSentiment derived by Naive Bayes: $nbSentiment") }
    }

    ssc.start()
    ssc.awaitTerminationOrTimeout(100000)
  }

  def naiveBayesPrediction(tweet: String): String = {
    val naiveBayesModel: NaiveBayesModel = NaiveBayesModelUtil.getNaiveBayesModel()
    val preprocessingModel = NaiveBayesModelUtil.getPreprocessingPipelineModel()
    val idfModel = NaiveBayesModelUtil.getIDFModel()
    val tweetDF = Conf.spark.createDataFrame(Seq(Tuple1(tweet))).toDF("sentence")
    val termFrequencyDF = preprocessingModel.transform(tweetDF)
    val idfFeaturesDF = idfModel.transform(termFrequencyDF)
    val predictionsDF = naiveBayesModel.transform(idfFeaturesDF)

    import Conf.spark.implicits._
    val predictedLabel = predictionsDF.map(row => row.getAs[Double]("predictedLabel")).head()
    predictedLabel match {
      case 1.0 => "POSITIVE"
      case 0.0 => "NEGATIVE"
      case _ => "NEUTRAL"
    }
  }

  def testing() = {
    val regexPattern = "\\b[A-Za-z][a-z0-9]+\\b".r
    val tweetWithoutJunk: String = regexPattern.findAllIn("@AvalynnSmith @Trump_Regrets @realDonaldTrump @CNN @NBCNews @FoxNews You couldn't see this coming before the election?").toList.mkString(" ")
    val tweetWithoutJunk1: String = regexPattern.findAllIn("@AvalynnSmith @Trump_Regrets @realDonaldTrump @CNN @NBCNews @FoxNews I am love enjoy happy that I am the president").toList.mkString(" ")
    println(tweetWithoutJunk)
    println(tweetWithoutJunk1)
    //    val nbSentiment = naiveBayesPrediction(tweetWithoutJunk)
    //    val nbSentiment1 = naiveBayesPrediction(tweetWithoutJunk1)
    val nlpSentiment = SentimentAnalysisUtils.detectSentiment(tweetWithoutJunk)
    val nlpSentiment1 = SentimentAnalysisUtils.detectSentiment(tweetWithoutJunk1)
    //    println(nbSentiment)
    //    println(nbSentiment1)
    println(nlpSentiment)
    println(nlpSentiment1)
  }

}