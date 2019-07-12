# Akka samples in Scala 

- Basic Actor constructors
- Integrating Akka Actors with Akka Stream based on 
  - https://blog.colinbreck.com/integrating-akka-streams-and-akka-actors-part-ii/)
  - Standard Akka Integration documentation
  
## Next tasks
- Create two Actors which are exchanging messages between themselves via Kafka
  - Each Actor consumes and produces Kafka by using Akka Streams for Kafka (Alpakka Akka)
  - This requires Actor - Akka Streams integration
- Use dynamic akka streams feature and append message into stream opened in Akka Actor
  - Do the same in consumer actor (Akka Streams document exists)