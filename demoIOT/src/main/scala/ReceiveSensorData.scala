/**
  * Created by gilbertlau on 9/3/16.
  */
package demoIOT

import com.datastax.spark.connector._
import org.apache.spark.SparkConf
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Duration, Seconds, StreamingContext}
import org.joda.time.{DateTime, DateTimeZone}


object ReceiveSensorData {
  def main(args: Array[String]) {

    var WINDOW_LENGTH = Duration(600 * 1000)   //  time window
    var SLIDE_INTERVAL = Duration(60 * 1000)   //  slide interval

    // Create the context with a 1 second batch size
    val sparkConf = new SparkConf().setAppName("ReceiveSensorData")
    val ssc = new StreamingContext(sparkConf, Seconds(1))

    val lines = ssc.socketTextStream("localhost", 9999, StorageLevel.MEMORY_AND_DISK_SER)

    // Collecting window RDDs
    val windowLines = lines.window(WINDOW_LENGTH, SLIDE_INTERVAL)

    // Splitting the command seperated lines into tuple of tokens
    val words = windowLines.map(_.split(","))

    words.foreachRDD( rdd => {


//      val utcTime = DateTime.now().withZone(DateTimeZone.UTC)
//      val utcTime = DateTime.now().minusDays(0).withZone(DateTimeZone.UTC)
//      val utcTime = DateTime.now().plusDays(0).withZone(DateTimeZone.UTC)
      val utcTime = DateTime.now().minusDays(1).withZone(DateTimeZone.UTC)
      println("Sensor collection = " + utcTime.toString())

      // Calculate avg temperature
      val temp_rdd = rdd.filter(x =>  x(2) == "temperature")
      val average_temp_rdd = temp_rdd.map(x => (x(0), x(3))).
        mapValues(x => (x.toFloat, 1)).reduceByKey((x, y) => (x._1 + y._1, x._2 + y._2)).
        mapValues{case (sum, count) => sum / count }.
        map( x => (x._1, x._2, utcTime))
      val min_temp_rdd = temp_rdd.map(x => (x(0), x(3))).
        mapValues(x => x.toFloat).
        reduceByKey((x, y) => math.min(x,y)).
        map( x => (x._1 , x._2, utcTime))
      val max_temp_rdd = temp_rdd.map(x => (x(0), x(3))).
        mapValues(x => x.toFloat).
        reduceByKey((x, y) => math.max(x,y)).
        map( x => (x._1 , x._2, utcTime))

      // Calculate avg movement
      val movement_rdd = rdd.filter(x =>  x(2) == "movement")
      val average_movement_rdd = movement_rdd.map(x => (x(0), x(3))).
        mapValues(x => (x.toFloat, 1)).reduceByKey((x, y) => (x._1 + y._1, x._2 + y._2)).
        mapValues{case (sum, count) => sum / count }.
        map( x => (x._1, x._2, utcTime))
      val min_movement_rdd = movement_rdd.map(x => (x(0), x(3))).
        mapValues(x => x.toFloat).
        reduceByKey((x, y) => math.min(x,y)).
        map( x => (x._1 , x._2, utcTime))
      val max_movement_rdd = movement_rdd.map(x => (x(0), x(3))).
        mapValues(x => x.toFloat).
        reduceByKey((x, y) => math.max(x,y)).
        map( x => (x._1 , x._2, utcTime))


      // Calculate avg humidity
      val humidity_rdd = rdd.filter(x =>  x(2) == "humidity")
      val average_humidity_rdd = humidity_rdd.map(x => (x(0), x(3))).
        mapValues(x => (x.toFloat, 1)).reduceByKey((x, y) => (x._1 + y._1, x._2 + y._2)).
        mapValues{case (sum, count) => sum / count }.
        map( x => (x._1, x._2, utcTime))
      val min_humidity_rdd = humidity_rdd.map(x => (x(0), x(3))).
        mapValues(x => x.toFloat).
        reduceByKey((x, y) => math.min(x,y)).
        map( x => (x._1 , x._2, utcTime))
      val max_humidity_rdd = humidity_rdd.map(x => (x(0), x(3))).
        mapValues(x => x.toFloat).
        reduceByKey((x, y) => math.max(x,y)).
        map( x => (x._1 , x._2, utcTime))


//      for (item <- average_temp_rdd.collect()){
//          println("Next 3 temperature elements Record")
//          print(item.toString())
//          println()
//      }

      // Saving temperature data into Cassandra
      average_temp_rdd.saveToCassandra("iot", "sensor_stat_tw30_sw1",
        SomeColumns("sensor_id", "avg_temperature", "reading_time"))

      min_temp_rdd.saveToCassandra("iot", "sensor_stat_tw30_sw1",
        SomeColumns("sensor_id", "min_temperature", "reading_time"))

      max_temp_rdd.saveToCassandra("iot", "sensor_stat_tw30_sw1",
        SomeColumns("sensor_id", "max_temperature", "reading_time"))

      // Saving movement data into Cassandra
      average_movement_rdd.saveToCassandra("iot", "sensor_stat_tw30_sw1",
        SomeColumns("sensor_id", "avg_movement", "reading_time"))

      min_movement_rdd.saveToCassandra("iot", "sensor_stat_tw30_sw1",
        SomeColumns("sensor_id", "min_movement", "reading_time"))

      max_movement_rdd.saveToCassandra("iot", "sensor_stat_tw30_sw1",
        SomeColumns("sensor_id", "max_movement", "reading_time"))

      // Saving humidity data into Cassandra
      average_humidity_rdd.saveToCassandra("iot", "sensor_stat_tw30_sw1",
        SomeColumns("sensor_id", "avg_humidity", "reading_time"))

      min_humidity_rdd.saveToCassandra("iot", "sensor_stat_tw30_sw1",
        SomeColumns("sensor_id", "min_humidity", "reading_time"))

      max_humidity_rdd.saveToCassandra("iot", "sensor_stat_tw30_sw1",
        SomeColumns("sensor_id", "max_humidity", "reading_time"))

    })

    ssc.start()
    ssc.awaitTermination()

  }

}