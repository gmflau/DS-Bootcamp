/**
  * Created by gilbertlau on 9/5/16.
  */
package demoIOT


//import com.datastax.spark.connector.rdd.{CassandraTableScanRDD, EmptyCassandraRDD, ReadConf, ValidRDDType}
import com.datastax.spark.connector._
import org.apache.spark.{SparkConf, SparkContext}
import org.joda.time.{DateTime, DateTimeZone}


object DailyRollup {

    def main(args: Array[String]) {

      val NUM_DAYS_BEFORE = 1

      val utcTime_Now = DateTime.now().withZone(DateTimeZone.UTC)
      val xDaysBefore = utcTime_Now.minusDays(NUM_DAYS_BEFORE-1).withTimeAtStartOfDay().withZone(DateTimeZone.UTC)
      val xDaysAfter = utcTime_Now.minusDays(NUM_DAYS_BEFORE).withTimeAtStartOfDay().withZone(DateTimeZone.UTC)


      println("Now is = " + utcTime_Now)
      println("The 1 days' Date = " + xDaysBefore)
      println("The 1 days' Date = " + xDaysAfter)

      // Create Spark context
      val sparkConf = new SparkConf().setAppName("DailyRollup")
      val sc = new SparkContext(sparkConf)
      val sqlContext = new org.apache.spark.sql.SQLContext(sc)

      // Read sensor data from Cassandra exactly x Days from today
      val sensor_data_table = sc.cassandraTable("iot", "sensor_stat_tw30_sw1")

      val sensor_data_xDay_rdd = sensor_data_table.filter( x => x.getDateTime("reading_time").isAfter(xDaysAfter) &&
        x.getDateTime("reading_time").isBefore(xDaysBefore))

      println(">>>>>>>   No. of rows = ", sensor_data_xDay_rdd.count())

      val min_temp_xDay_rdd = sensor_data_xDay_rdd.map(x => (x.getString("sensor_id"), x.getFloat("min_temperature"))).
        reduceByKey((x, y) => math.min(x,y)).map( x => (x._1, x._2, xDaysBefore))
      val max_temp_xDay_rdd = sensor_data_xDay_rdd.map(x => (x.getString("sensor_id"), x.getFloat("max_temperature"))).
        reduceByKey((x, y) => math.max(x,y)).map( x => (x._1, x._2, xDaysBefore))
      val avg_temp_xDay_rdd = sensor_data_xDay_rdd.map(x => (x.getString("sensor_id"), x.getFloat("avg_temperature"))).
        mapValues(x => (x, 1)).reduceByKey((x, y) => (x._1 + y._1, x._2 + y._2)).
        mapValues{case (sum, count) => sum / count }.
        map( x => (x._1, x._2, xDaysBefore))

      val min_humidity_xDay_rdd = sensor_data_xDay_rdd.map(x => (x.getString("sensor_id"), x.getFloat("min_humidity"))).
        reduceByKey((x, y) => math.min(x,y)).map( x => (x._1, x._2, xDaysBefore))
      val max_humidity_xDay_rdd = sensor_data_xDay_rdd.map(x => (x.getString("sensor_id"), x.getFloat("max_humidity"))).
        reduceByKey((x, y) => math.max(x,y)).map( x => (x._1, x._2, xDaysBefore))
      val avg_humidity_xDay_rdd = sensor_data_xDay_rdd.map(x => (x.getString("sensor_id"), x.getFloat("avg_humidity"))).
        mapValues(x => (x, 1)).reduceByKey((x, y) => (x._1 + y._1, x._2 + y._2)).
        mapValues{case (sum, count) => sum / count }.
        map( x => (x._1, x._2, xDaysBefore))

      val min_movement_xDay_rdd = sensor_data_xDay_rdd.map(x => (x.getString("sensor_id"), x.getFloat("min_movement"))).
        reduceByKey((x, y) => math.min(x,y)).map( x => (x._1, x._2, xDaysBefore))
      val max_movement_xDay_rdd = sensor_data_xDay_rdd.map(x => (x.getString("sensor_id"), x.getFloat("max_movement"))).
        reduceByKey((x, y) => math.max(x,y)).map( x => (x._1, x._2, xDaysBefore))
      val avg_movement_xDay_rdd = sensor_data_xDay_rdd.map(x => (x.getString("sensor_id"), x.getFloat("avg_movement"))).
         mapValues(x => (x, 1)).reduceByKey((x, y) => (x._1 + y._1, x._2 + y._2)).
        mapValues{case (sum, count) => sum / count }.
        map( x => (x._1, x._2, xDaysBefore))


//      for (item <- min_temp_xDay_rdd.collect()){
//        println("Next min_temp elements Record: ")
//        print(item.toString())
//        println()
//      }


      // Saving temperature data into Cassandra
      if (min_temp_xDay_rdd.count() > 0) {
        min_temp_xDay_rdd.saveToCassandra("iot", "daily_rollup",
          SomeColumns("sensor_id", "min_temperature", "date"))
      }

      if (max_temp_xDay_rdd.count() > 0) {
        max_temp_xDay_rdd.saveToCassandra("iot", "daily_rollup",
          SomeColumns("sensor_id", "max_temperature", "date"))
      }

      if (avg_temp_xDay_rdd.count() > 0) {
        avg_temp_xDay_rdd.saveToCassandra("iot", "daily_rollup",
          SomeColumns("sensor_id", "avg_temperature", "date"))
      }

      // Saving humidity data into Cassandra
      if (min_humidity_xDay_rdd.count() > 0) {
        min_humidity_xDay_rdd.saveToCassandra("iot", "daily_rollup",
          SomeColumns("sensor_id", "min_humidity", "date"))
      }

      if (max_humidity_xDay_rdd.count() > 0) {
        max_humidity_xDay_rdd.saveToCassandra("iot", "daily_rollup",
          SomeColumns("sensor_id", "max_humidity", "date"))
      }

      if (avg_humidity_xDay_rdd.count() > 0) {
        avg_humidity_xDay_rdd.saveToCassandra("iot", "daily_rollup",
          SomeColumns("sensor_id", "avg_humidity", "date"))
      }

      // Saving movement data into Cassandra
      if (min_movement_xDay_rdd.count() > 0) {
        min_movement_xDay_rdd.saveToCassandra("iot", "daily_rollup",
          SomeColumns("sensor_id", "min_movement", "date"))
      }

      if (max_movement_xDay_rdd.count() > 0) {
        max_movement_xDay_rdd.saveToCassandra("iot", "daily_rollup",
          SomeColumns("sensor_id", "max_movement", "date"))
      }

      if (avg_movement_xDay_rdd.count() > 0) {
        avg_movement_xDay_rdd.saveToCassandra("iot", "daily_rollup",
          SomeColumns("sensor_id", "avg_movement", "date"))
      }

    }

}
