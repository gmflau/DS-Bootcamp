/**
  * Created by gilbertlau on 9/6/16.
  */

package demoIOT

import com.datastax.spark.connector._
import org.apache.spark.{SparkConf, SparkContext}


//import com.datastax.spark.connector.rdd.{CassandraTableScanRDD, EmptyCassandraRDD, ReadConf, ValidRDDType}

object SensorMetadataIngestion {

  def main(args: Array[String]): Unit = {

    // Create the context with a 1 second batch size
    val sparkConf = new SparkConf().setAppName("SensorMetadataIngestion")
    val sc = new SparkContext(sparkConf)

    val sensor_rdd = sc.textFile("file:///Users/gilbertlau/demo/sensor_gen_inventory.csv").
      map(x => x.split(",")).
      map(x => (x(0), x(1), x(2), x(3), x(4), x(5), x(6), x(7), x(8), x(9), x(10) + ',' + x(11)))

    sensor_rdd.saveToCassandra("iot", "sensor_metadata",
      SomeColumns("vendor_id", "sensor_id", "manufacture_date", "deployment_date", "maintenance_history",
        "retirement_date", "sensor_type", "floor", "wing", "hive", "geolocation"))

  }

}
