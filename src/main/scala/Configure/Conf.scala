package Configure

import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.sql.SparkSession

import scala.io.Source
import scala.util.{Failure, Success, Try}

object Conf {

  val spark = SparkSession.builder().master("local[2]").appName("TwitterSentimentAnalysis")
                                    .config("spark.sql.warehouse.dir", "file:///c:/tmp/spark-warehouse") // only in windows
                                    .getOrCreate()
  spark.sparkContext.setCheckpointDir("./checkpointDir")

  def getStream() = {
    spark.conf.set("spark.executor.memory","1g")
    val sc = spark.sparkContext
    sc.setLogLevel("WARN")
    
    val ssc = new StreamingContext(sc, Seconds(5))

    val twitterCredentialsFile = "./twitterCredentials"

    setPropertiesFromFile(twitterCredentialsFile)

    val stream = TwitterUtils.createStream(ssc, None)
    (stream,ssc)
  }

  def readFile(filename: String): Try[Iterator[String]] = {
    Try(Source.fromFile(filename).getLines)
  }

  def loadPropertiesFromFile(filename: String): List[String] = {
    readFile(filename) match {
      case Success(lines) => lines.map(line => line.split(":")(1)).toList
      case Failure(f) => {
        println(f)
        List.empty
      }
    }
  }

  def setPropertiesFromFile(filename: String) = {
    val props: List[String] = loadPropertiesFromFile(filename)
    System.setProperty("twitter4j.oauth.consumerKey", props(0))
    System.setProperty("twitter4j.oauth.consumerSecret", props(1))
    System.setProperty("twitter4j.oauth.accessToken", props(2))
    System.setProperty("twitter4j.oauth.accessTokenSecret", props(3))
  }
}