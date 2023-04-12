import akka.actor.ActorSystem

import java.io.File
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CSVUtility(implicit val system: ActorSystem) {
  def processCSVFile(fileList: List[File]): Future[Map[String, List[Any]]] = {
    var dataMap: Map[String, List[Any]] = Map()
    var noOfProcessedMeasurements = 0
    var noOfFailedMeasurements = 0
    fileList.foreach(myFile => {
      for(line <- io.Source.fromFile(myFile).getLines().drop(1)){
        val cols = line.split(",").map(_.trim)
        noOfProcessedMeasurements += 1
        if(cols(1) == "NaN") noOfFailedMeasurements += 1
        if(dataMap.contains(cols(0))) {
          dataMap += cols(0) -> (dataMap.get(cols(0)).get ++ List(cols(1)))
        } else {
          dataMap += cols(0) -> List(cols(1))
        }
      }
    })
    println(s"\nNumber of processed measurements $noOfProcessedMeasurements")
    println(s"Number of failed measurements $noOfFailedMeasurements\n")
    Future(dataMap)
  }
}

case class SensorData(
                     sensorId: String,
                     min: String,
                     avg: String,
                     max: String
                     )
