import akka.actor.ActorSystem

import java.io.File
import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global

object SensorStatisticsReport extends App {
  implicit val system: ActorSystem = ActorSystem()

  print("\nEnter file path: ") //Give file path like D://Files/
  val dirName = scala.io.StdIn.readLine()
  if(new File(dirName).exists()) {
    val filesList = new File(dirName).listFiles.filter(_.getName.endsWith(".csv")).toList
    if (filesList.isEmpty) {
      println("\nNo files to be processed in the given directory")
      system.terminate()
    }
    var highestAvgDataMap: Map[String, Double] = Map()
    var resultList: ListBuffer[SensorData] = ListBuffer(SensorData("Sensor-id","Min","Avg","Max"))
    new CSVUtility().processCSVFile(filesList)
      .map(sensorData => {
        if (!sensorData.isEmpty) {
          sensorData.map(x => {
            val valueList = x._2.filter(_.!=("NaN")).map(_.toString.toInt)
            if (!valueList.isEmpty) {
              highestAvgDataMap += x._1 -> valueList.sum / valueList.size
              resultList += SensorData(x._1,valueList.min.toString,(valueList.sum/valueList.size).toString,valueList.max.toString)
            } else {
              resultList += SensorData(x._1,"NaN","NaN","NaN")
            }
          })
          resultList.foreach(x => println(s"${x.sensorId},${x.min},${x.avg},${x.max}"))
          println(s"\nNumber of processed files: ${filesList.size}")
          println(s"Sensor with highest average and humidity: ${highestAvgDataMap.max._1}")
        } else {
          println("File processing failed with corrupted data")
        }
        system.terminate()
      })
      .recover({
        case _: Exception => println(s"Error in file processing")
          system.terminate()
      })
  } else {
    println("Directory not exist")
    system.terminate()
  }
}
