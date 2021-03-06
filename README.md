# RADAR-MongoDbConnector

[![Build Status](https://travis-ci.org/RADAR-CNS/RADAR-MongoDbConnector.svg?branch=master)](https://travis-ci.org/RADAR-CNS/RADAR-MongoDbConnector)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/64eb2330ca7146fcb0b823816f44fcb8)](https://www.codacy.com/app/RADAR-CNS/RADAR-MongoDbConnector?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=RADAR-CNS/RADAR-MongoDbConnector&amp;utm_campaign=Badge_Grade)

The MongoDB sink connector is a tool for scalably and reliably streaming data between Apache Kafka and MongoDB. It exports Avro data from Kafka topics into the MongoDB.
 
Currently it supports only two types of data:
 - [singleton double aggregator](https://github.com/RADAR-CNS/RADAR-Backend/blob/master/src/main/resources/avro/aggregator/double_aggregator.avsc)
 - [array double aggregator](https://github.com/RADAR-CNS/RADAR-Backend/blob/master/src/main/resources/avro/aggregator/double_array_aggregator.avsc)

The current version proofs how to extract data coming from an Empatica E4 device using the [RADAR-CNS Android application](https://github.com/RADAR-CNS/RADAR-AndroidApplication) and analysed by the [RADAR-CNS Kafka Backend](https://github.com/RADAR-CNS/RADAR-Backend) 

## Dependencies

The following assumes you have Kafka and the Confluent Schema Registry running.

## Quickstart for RADAR-CNS

- Build the project. Go inside the project folder and run
```shell
./gradlew clean build
```
- Modify `sink.properties` file according your cluster. The following properties are supported:

<table class="data-table"><tbody>
<tr>
<th>Name</th>
<th>Description</th>
<th>Type</th>
<th>Default</th>
<th>Valid Values</th>
<th>Importance</th>
</tr>
<tr>
<td>mongo.database</td><td>MongoDB database name</td><td>string</td><td></td><td></td><td>high</td></tr>
<tr>
<td>mongo.host</td><td>MongoDB host name to write data to</td><td>string</td><td></td><td></td><td>high</td></tr>
<tr>
<td>topics</td><td>List of topics to be streamed.</td><td>list</td><td></td><td></td><td>high</td></tr>
<tr>
<td>collection.format</td><td>A format string for the destination collection name, which may contain `${topic}`as a placeholder for the originating topic name.
For example, `kafka_${topic}` for the topic `orders` will map to the collection name `kafka_orders`.</td><td>string</td><td>{$topic}</td><td></td><td>medium</td></tr>
<tr>
<td>mongo.password</td><td>Password to connect to MongoDB database. If not set, no credentials are used.</td><td>string</td><td>null</td><td></td><td>medium</td></tr>
<tr>
<td>mongo.username</td><td>Username to connect to MongoDB database. If not set, no credentials are used.</td><td>string</td><td>null</td><td></td><td>medium</td></tr>
<tr>
<td>record.converter.class</td><td>RecordConverterFactory that returns classes to convert Kafka SinkRecords to BSON documents.</td><td>class</td><td>class org.radarcns.serialization.RecordConverterFactory</td><td></td><td>medium</td></tr>
<tr>
<td>buffer.capacity</td><td>Maximum number of items in a MongoDB writer buffer. Once the buffer becomes full,the task fails.</td><td>int</td><td>20000</td><td>[1,...]</td><td>low</td></tr>
<tr>
<td>mongo.port</td><td>MongoDB port</td><td>int</td><td>27017</td><td>[1,...]</td><td>low</td></tr>
</tbody></table>

- (optional) Modify `standalone.properties` and `standalone.properties` file according your cluster instances. You may need to update the bootstraps and Schema Registry locations.

```ini
bootstrap.servers=
key.converter.schema.registry.url=
```

- Copy your jar file inside your Kafka Server
- Copy all configuration files inside your Kafka Server
  - sink.properties 
  - standalone.properties (optional)
  - cluster.properties (optional)
- Put the connector `build/libs/kafka-connect-mongodb-sink-*.jar` in the folder `share/java`.
  - standalone mode

  ```shell
  /bin/connect-standalone standalone.properties sink.properties
  ```
  - distributed mode

  ```shell
  /bin/connect-distributed cluster.properties sink.properties
  ```
- stop your connector using `CTRL-C`

To use further data types, extend `org.radarcns.serialization.RecordConverterFactory` and set the new class name in the `record.converter.class` property.
 
### Tuning
The only available setting is the number of records returned in a single call to `poll()` (i.e. `consumer.max.poll.records` param inside `standalone.properties`)

### Note
Connectors can be run inside any machine where Kafka has been installed. Therefore, you can fire them also inside a machine that does not host a Kafka broker.

## Reset
To reset a connector running in `standalone mode` you have to stop it and then modify `name` and `offset.storage.file.filename` respectively inside `sink.properties` and `standalone.properties`
