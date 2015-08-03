# microservice_test

Testing the VERT.X library:

* git clone [microservice_test](https://github.com/bitvector2/microservice_test.git)
* cd microservice_test
* Edit src/main/resources/microservice_test.properties
* Edit src/main/resources/hibernate.cfg.xml
* ./gradlew clean shadowJar
* ./microservice_test.sh

Use the following commands for benchmarking/fault recovery:

* ab -n 10000 -c 64 -k http://localhost:8080/products/1
* watch --interval 1 curl --silent --show-error --include --max-time 1 http://localhost:8080/products/1

To setup a development environment, download and expand Gradle into your home directory and configure as such in IntelliJ

* Download: [Gradle 2.5](https://services.gradle.org/distributions/gradle-2.5-all.zip)
* Run: echo "org.gradle.daemon=true" >> ~/.gradle/gradle.properties

Technology stack in use:

* Java 8
* Logback
* Jackson
* VERT.X
* Hibernate
* C3P0
* Hazelcast
* PostgreSQL
* Gradle
