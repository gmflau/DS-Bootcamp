# DS-Bootcamp

### This is my bootcamp project at DataStax

Presentation folder -	contains the presentation for positioning DSE as the IoT data platform

datamodel	- contains cql to populate the required cassandra tables

demoIOT	- contains Scala code for spark streaming for ingesting IoT device data, transforming it and storing it into Cassandra tables; contains Scala code for spark job for processing daily rollup

python	- contains Python code to generate smart device metadata information.

sensor_data_feeder	- contains Java code to generate smart device data every 5 seconds and send them to a sparking streaming port

solr- contains the updated solr schema for the Cassandra tables for index-based searching using solr_query in CQLSH

zeppelin/notebok	- contains Zeppelin notebooks to extract device data from the Cassandra table using CQL and Solr Queries



### The content below to be developed

### Prerequisites:
#### - DSE 5.0 has already been installed
#### - Your DSE is running in Search/Analytics mode (Solr/Spark turned on)
<br>

#### Procedures to set up the environment:
##### 1. Create the corresponding Cassandra tables using [this cql file](./datamodel/demo_app_data_model.cql)
##### 2. ...


