# orderbookcoinbase

This is a spring boot command line application, which connects to Coinbase Pro websocket API, and process the L2 feeds

To run:
1/ in one step: .\gradlew bootRun --args BTC-USD
2/ in 2 steps (allows the extra arguments to display the orderBook at each or update or not:
  - .\gradlew bootJar
  - java -jar .\build\libs\gsrcoin-0.0.1-SNAPSHOT.jar BTC-USD false
