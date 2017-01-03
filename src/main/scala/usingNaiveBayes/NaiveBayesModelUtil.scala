package usingNaiveBayes

import org.apache.spark.ml.PipelineModel
import org.apache.spark.ml.classification.{NaiveBayes, NaiveBayesModel}
import org.apache.spark.ml.feature.IDFModel
import org.apache.spark.sql.{Dataset, Row}

/**
  * Created by aj on 12/28/16.
  */
object NaiveBayesModelUtil {

  var optNaiveBayesModel: Option[NaiveBayesModel] = None
  var optPreprocessingPipelineModel: Option[PipelineModel] = None
  var optIDFModel: Option[IDFModel] = None

  def splitTrainingTestingData() = {
    val completeData = Preprocess.createTrainingDataFrame()
//    val Array(trainingData, testingData) = completeData.randomSplit(Array(0.8, 0.2), seed = 1234L)
//    (completeData, trainingData, testingData)
    completeData
  }

  def buildAndSaveNaiveBayesModel() = {
//    val (completeData, trainingData, testingData) = splitTrainingTestingData()
    val completeData = splitTrainingTestingData()
    optPreprocessingPipelineModel = Some(Preprocess.getPreprocessingPipeline().fit(completeData))
    val preprocessingPipelineModel = optPreprocessingPipelineModel.get
    val termFrequencyDF = preprocessingPipelineModel.transform(completeData)
    optIDFModel = Some(Preprocess.getIDF().fit(termFrequencyDF))
    val idfModel = optIDFModel.get
    val Array(trainingData, testingData) = termFrequencyDF.randomSplit(Array(0.8, 0.2), seed = 1234L)
    val idfFeaturesDF = idfModel.transform(trainingData)
    val naiveBayesModel = new NaiveBayes().setLabelCol("label").setFeaturesCol("idfFeatures").setSmoothing(1.0).setPredictionCol("predictedLabel").fit(idfFeaturesDF)

    println(s"The accuracy of the model is: ${computeAccuracy(naiveBayesModel, idfModel, testingData)}")
    optNaiveBayesModel = Some(naiveBayesModel)
    naiveBayesModel.write.overwrite().save("./naiveBayesModel")
  }

  def computeAccuracy(model: NaiveBayesModel, idfModel: IDFModel, data: Dataset[Row]): Double = {
    val idfFeaturesDF = idfModel.transform(data)
    val predictionsDF = model.transform(idfFeaturesDF).cache()
    val totalCount = predictionsDF.count()
    val correctlyPredicted = predictionsDF.filter{ row =>
      row.getAs[Double]("label") == row.getAs[Double]("predictedLabel")
    }.count()
    correctlyPredicted.toDouble/totalCount.toDouble
  }

  def loadNaiveBayesModel() = {
    PipelineModel.load("./naiveBayesModel").asInstanceOf[NaiveBayesModel]
  }

  def getNaiveBayesModel(): NaiveBayesModel = {
    optNaiveBayesModel match {
      case Some(model) => model
      case None => loadNaiveBayesModel()
    }
  }

  def getPreprocessingPipelineModel(): PipelineModel = {
    optPreprocessingPipelineModel match {
      case Some(model) => model
      case None => Preprocess.getPreprocessingDataModel()
    }
  }

  def getIDFModel(): IDFModel = {
    optIDFModel match {
      case Some(model) => model
      case None => {
        val completeData = splitTrainingTestingData()
        optPreprocessingPipelineModel = Some(Preprocess.getPreprocessingPipeline().fit(completeData))
        val preprocessingPipelineModel = optPreprocessingPipelineModel.get
        val termFrequencyDF = preprocessingPipelineModel.transform(completeData)
        optIDFModel = Some(Preprocess.getIDF().fit(termFrequencyDF))
        optIDFModel.get
      }
    }
  }

}
