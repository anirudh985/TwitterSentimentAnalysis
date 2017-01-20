package scala

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.twitter.TwitterUtils

object Main {
  
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("TwitterSentimentAnalysis").setMaster("local[2]").set("spark.executor.memory","1g");
    val sc = new SparkContext(conf)
    sc.setLogLevel("WARN")
    
    val ssc = new StreamingContext(sc, Seconds(1))
    
    System.setProperty("twitter4j.oauth.consumerKey", "#######################")
    System.setProperty("twitter4j.oauth.consumerSecret", "#########################################3")
    System.setProperty("twitter4j.oauth.accessToken", "###############################")
    System.setProperty("twitter4j.oauth.accessTokenSecret", "###################################")
    
    val stream = TwitterUtils.createStream(ssc, None)
    
    
    val tags = stream.flatMap(status => status.getHashtagEntities
                                              .map(entity => entity.getText))
    tags.countByValue()
        .foreachRDD { rdd => 
          val now = org.joda.time.DateTime.now()
          rdd.sortBy(_._2)
          .map(x => (x, now))
          println(rdd)
        }
  }
}
