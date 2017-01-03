name := "TwitterSentimentAnalysis"

version := "1.0"

scalaVersion := "2.11.8"

val sparkVersion = "2.0.0"

val stanfordNLPVersion = "3.6.0"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-streaming" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" %% "spark-mllib" % sparkVersion,
  "org.apache.bahir" %% "spark-streaming-twitter" % sparkVersion,
  "joda-time" % "joda-time" % "2.9.7",
  "edu.stanford.nlp" % "stanford-corenlp" % stanfordNLPVersion,
  "edu.stanford.nlp" % "stanford-corenlp" % stanfordNLPVersion classifier "models"
)
