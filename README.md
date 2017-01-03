The project uses Apache Spark 2.0.0, Apache Bahir Spark Twitter Streaming, Stanford CoreNLP 3.6.0 and Scala 2.11.8

The following project compares the results of Sentiment Analysis of Tweets using Stanford CoreNLP and custom build NaiveBayesModel using the training data provided at https://inclass.kaggle.com/c/si650winter11

ToDo:
1. When run in a cluster environment, the Naive Bayes Model which was obtained as a result of training on the data could be broadcasted to all the nodes before prediction.
2. The model is yet to be built with the data from http://www.sananalytics.com/lab/twitter-sentiment/