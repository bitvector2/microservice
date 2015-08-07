# microservice

Testing VERT.X, Hibernate, and Hazelcast on a cluster of Raspberry Pi 2B servers:

* git clone https://github.com/bitvector2/microservice.git
* cd microservice
* Edit src/main/resources/microservice.properties
* Edit src/main/resources/hibernate.cfg.xml
* ./gradlew clean shadowJar
* ./microservice.sh

Use the following commands for benchmarking/fault recovery:

* ab -n 10000 -c 64 -k https://www.bitvector.org/products/1
* watch --interval 1 curl --silent --show-error --include --max-time 1 https://www.bitvector.org/products/1

To setup a development environment, download and expand Gradle into your home directory and configure as such in IntelliJ

* Download: [Gradle 2.5](https://services.gradle.org/distributions/gradle-2.5-all.zip)
* Run: echo "org.gradle.daemon=true" >> ~/.gradle/gradle.properties

Technology stack in use:

* Java 8
* VERT.X
* Hibernate
* HikariCP
* Hazelcast
* Gradle
