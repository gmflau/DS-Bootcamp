CREATE KEYSPACE IF NOT EXISTS iot
	WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };


CREATE TABLE  IF NOT EXISTS iot.sensor_metadata (
    vendor_id text,
    sensor_id text,
    manufacture_date timestamp,
    deployment_date timestamp,
    maintenance_history text,
    retirement_date timestamp,
    sensor_type text,
    floor text,
    wing text,
    hive text,
    geolocation text,
    PRIMARY KEY ((sensor_id), sensor_type, hive, wing, floor, geolocation)
);

CREATE MATERIALIZED VIEW IF NOT EXISTS iot.sensor_by_location_by_hive AS
    SELECT sensor_id FROM sensor_metadata
    WHERE sensor_id IS NOT NULL AND
    	  vendor_id IS NOT NULL AND
          manufacture_date IS NOT NULL AND
          deployment_date IS NOT NULL AND
          maintenance_history IS NOT NULL AND
          retirement_date IS NOT NULL AND
          sensor_type IS NOT NULL AND
          floor IS NOT NULL AND
          wing IS NOT NULL AND
          hive IS NOT NULL AND
          geolocation IS NOT NULL
          PRIMARY KEY (hive, wing, floor, sensor_id, sensor_type, geolocation)
          WITH CLUSTERING ORDER BY (wing ASC, floor ASC, sensor_id ASC);


CREATE MATERIALIZED VIEW IF NOT EXISTS iot.sensor_by_location_by_type AS
    SELECT sensor_id FROM sensor_metadata
    WHERE sensor_id IS NOT NULL AND
    	  vendor_id IS NOT NULL AND
          manufacture_date IS NOT NULL AND
          deployment_date IS NOT NULL AND
          maintenance_history IS NOT NULL AND
          retirement_date IS NOT NULL AND
          sensor_type IS NOT NULL AND
          floor IS NOT NULL AND
          wing IS NOT NULL AND
          hive IS NOT NULL AND
          geolocation IS NOT NULL
          PRIMARY KEY (sensor_type, sensor_id, hive, wing, floor, geolocation)
          WITH CLUSTERING ORDER BY (sensor_id ASC);


CREATE MATERIALIZED VIEW IF NOT EXISTS iot.sensor_by_sensor_id AS
    SELECT sensor_id FROM sensor_metadata
    WHERE sensor_id IS NOT NULL AND
    	  vendor_id IS NOT NULL AND
          manufacture_date IS NOT NULL AND
          deployment_date IS NOT NULL AND
          maintenance_history IS NOT NULL AND
          retirement_date IS NOT NULL AND
          sensor_type IS NOT NULL AND
          floor IS NOT NULL AND
          wing IS NOT NULL AND
          hive IS NOT NULL AND
          geolocation IS NOT NULL
          PRIMARY KEY (sensor_id, hive, wing, floor, sensor_type, geolocation)
          WITH CLUSTERING ORDER BY (hive ASC, wing ASC, floor ASC, sensor_type ASC);
          

CREATE MATERIALIZED VIEW IF NOT EXISTS iot.sensor_by_geolocation AS
    SELECT sensor_id FROM sensor_metadata
    WHERE sensor_id IS NOT NULL AND
    	  vendor_id IS NOT NULL AND
          manufacture_date IS NOT NULL AND
          deployment_date IS NOT NULL AND
          maintenance_history IS NOT NULL AND
          retirement_date IS NOT NULL AND
          sensor_type IS NOT NULL AND
          floor IS NOT NULL AND
          wing IS NOT NULL AND
          hive IS NOT NULL AND
          geolocation IS NOT NULL
          PRIMARY KEY (geolocation, hive, wing, floor, sensor_type, sensor_id)
          WITH CLUSTERING ORDER BY (hive ASC, wing ASC, floor ASC, sensor_type ASC);
          

CREATE TABLE IF NOT EXISTS iot.sensor_stat_tw30_sw1 (
	sensor_id text,
    isActive boolean,
    reading_time timestamp,
	min_temperature float,
	max_temperature float,
	avg_temperature float,
	min_humidity float,
	max_humidity float,
	avg_humidity float,	
 	min_movement float,
	max_movement float,
	avg_movement float,	 
	PRIMARY KEY ((sensor_id), reading_time)
) WITH CLUSTERING ORDER BY (reading_time DESC);


CREATE TABLE IF NOT EXISTS iot.daily_rollup (
	sensor_id text,
	date timestamp,
    isActive boolean,
	min_temperature float,
	max_temperature float,
	avg_temperature float,
	min_humidity float,
	max_humidity float,
	avg_humidity float,	
 	min_movement float,
	max_movement float,
	avg_movement float,	 
	PRIMARY KEY ((sensor_id), date )
) WITH CLUSTERING ORDER BY (date DESC);

