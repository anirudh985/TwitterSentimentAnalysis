package scala

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.twitter.TwitterUtils

object Conf {
  
  def getStream() = {
    val conf = new SparkConf().setAppName("TwitterSentimentAnalysis")
    val sc = new SparkContext(conf)
    sc.setLogLevel("WARN")
    
    val ssc = new StreamingContext(sc, Seconds(5))
    
    System.setProperty("twitter4j.oauth.consumerKey", "Hlv1Y4rbBWkKf64y26naovFlP")
    System.setProperty("twitter4j.oauth.consumerSecret", "wBkFEpEylkPE02vuxiRVXVOTaQmeM2n5oPsat02S24tGzQFicF")
    System.setProperty("twitter4j.oauth.accessToken", "791735447638638592-xcUTuiFi6K8vwjVD6h05RVdlW3nm8NW")
    System.setProperty("twitter4j.oauth.accessTokenSecret", "1nBPaIlYc4b6xuMdsdCpTQVSUgje3xT7vN7Sxv3kojAc1")
    
    val stream = TwitterUtils.createStream(ssc, None)
    stream
  }
  
  
}