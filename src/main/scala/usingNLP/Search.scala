package usingNLP

import twitter4j.Twitter
import twitter4j.TwitterFactory
import twitter4j.TwitterFactory
import twitter4j.Query
import scala.collection.JavaConversions._

object Search {
  def main(args: Array[String]): Unit = {
    
    System.setProperty("twitter4j.oauth.consumerKey", "Hlv1Y4rbBWkKf64y26naovFlP")
    System.setProperty("twitter4j.oauth.consumerSecret", "wBkFEpEylkPE02vuxiRVXVOTaQmeM2n5oPsat02S24tGzQFicF")
    System.setProperty("twitter4j.oauth.accessToken", "791735447638638592-xcUTuiFi6K8vwjVD6h05RVdlW3nm8NW")
    System.setProperty("twitter4j.oauth.accessTokenSecret", "1nBPaIlYc4b6xuMdsdCpTQVSUgje3xT7vN7Sxv3kojAc1")
    
    val twitterHandle = new TwitterFactory().getInstance()
    val query: Query = new Query("trump")
    val queryResult = twitterHandle.search(query)
    queryResult.getTweets().foreach{ status =>
      val sentiment = SentimentAnalysisUtils.detectSentiment(status.getText)
      println(s"${status.getUser.getScreenName}: ${status.getText}\nIt's sentiment is: ${sentiment.toString}")
    }
  }
}