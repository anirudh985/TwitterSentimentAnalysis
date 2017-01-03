package usingNLP

import java.util.Properties
import edu.stanford.nlp.pipeline.StanfordCoreNLP
import edu.stanford.nlp.ling.CoreAnnotations
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations
import scala.collection.mutable.ListBuffer
import scala.collection.JavaConversions._

trait SENTIMENT_TYPE
  case object VERY_NEGATIVE extends SENTIMENT_TYPE
  case object NEGATIVE extends SENTIMENT_TYPE
  case object NEUTRAL extends SENTIMENT_TYPE
  case object POSITIVE extends SENTIMENT_TYPE
  case object VERY_POSITIVE extends SENTIMENT_TYPE
  case object NOT_UNDERSTOOD extends SENTIMENT_TYPE
  
  
object SentimentAnalysisUtils {

  val pipeline = new StanfordCoreNLP(nlpProps)
  
  val nlpProps = {
    val props = new Properties()
    props.setProperty("annotators", "tokenize, ssplit, parse, sentiment")
    props
  }
  
  def detectSentiment(msg: String): SENTIMENT_TYPE = {
    val annotation = pipeline.process(msg)
    
    var sentiments: ListBuffer[Double] = ListBuffer()
    var sizes: ListBuffer[Int] = ListBuffer()
    
    var longestPartSentence = 0
    var mainSentiment = 0
    
    for(sentence <- annotation.get(classOf[CoreAnnotations.SentencesAnnotation])){
      val tree = sentence.get(classOf[SentimentCoreAnnotations.SentimentAnnotatedTree])
      val sentiment = RNNCoreAnnotations.getPredictedClass(tree)
      val partText = sentence.toString
      
      if (partText.length() > longestPartSentence) {
        mainSentiment = sentiment
        longestPartSentence = partText.length()
      }

      sentiments += sentiment.toDouble
      sizes += partText.length

//      println("debug: " + sentiment)
//      println("size: " + partText.length)
    }
    
    val averageSentiment:Double = {
      if(sentiments.size > 0) sentiments.sum / sentiments.size
      else -1
    }

    val weightedSentiments = (sentiments, sizes).zipped.map((sentiment, size) => sentiment * size)
    var weightedSentiment = weightedSentiments.sum / (sizes.fold(0)(_ + _))

    if(sentiments.size == 0) {
      mainSentiment = -1
      weightedSentiment = -1
    }


//    println("debug: main: " + mainSentiment)
//    println("debug: avg: " + averageSentiment)
//    println("debug: weighted: " + weightedSentiment)
    
    weightedSentiment match {
      case s if s <= 0.0 => NOT_UNDERSTOOD
      case s if s < 1.0 => VERY_NEGATIVE
      case s if s < 2.0 => NEGATIVE
      case s if s < 3.0 => NEUTRAL
      case s if s < 4.0 => POSITIVE
      case s if s < 5.0 => VERY_POSITIVE
      case s if s > 5.0 => NOT_UNDERSTOOD
    }
  }
}