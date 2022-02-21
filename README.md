
//Create the Producer Spring Boot Kafka project
curl https://start.spring.io/starter.tgz  \
  -d artifactId=producer-commodity-api \
  -d groupId=org.efirenet.net \
  -d dependencies=web,kafka,actuator \
  -d language=java \
  -d type=maven-project \
  -d baseDir=producer-commodity-api \
  -d bootVersion=2.3.6.RELEASE \
  -d packageName=org.efire.net \
| tar -xzvf -


//Create the Consumer Spring Boot Kafka project
curl https://start.spring.io/starter.tgz  \
  -d artifactId=consumer-commodity \
  -d groupId=org.efire.net \
  -d dependencies=web,kafka,actuator,lombok \
  -d language=java \
  -d type=maven-project \
  -d baseDir=consumer-commodity \
  -d bootVersion=2.3.6.RELEASE \
  -d packageName=org.efire.net \
| tar -xzvf -



//Start zookeeper
cd lib-tool/kafka_2.13-2.6.0/bin
zookeeper-server-start.sh ../config/zookeeper.properties

//Start Kakfka
cd lib-tool/kafka_2.13-2.6.0/bin
kafka-server-start.sh ../config/server.properties


//Create a kafka topic commodity-input-topic, partitions 1
cd lib-tool/kafka_2.13-2.6.0/bin

./kafka-topics.sh --zookeeper localhost:2181 --create \
   --topic commodity-input-topic \
  --partitions 1 --replication-factor 1

./kafka-topics.sh --zookeeper localhost:2181 --create \
   --topic commodity-input-filter-topic \
  --partitions 1 --replication-factor 1

//Consumer console
./kafka-console-consumer.sh --bootstrap-server localhost:9092 \
   --topic commodity-input-topic \
   --offset earliest \
   --partition 0

 //Rebalancing, upon starting the Producer and Consumer Commodity,
 // Alter the topic commodity-input-topic to add another partition
 ./kafka-topics.sh --zookeeper localhost:2181 --alter \
    --topic commodity-input-topic \
   --partitions 2

   ./kafka-topics.sh --zookeeper localhost:2181 --alter \
      --topic commodity-input-topic \
     --partitions 3

 // Describe topic commodity-input-topic
 ./kafka-topics.sh --zookeeper localhost:2181 --describe \
    --topic commodity-input-topic
