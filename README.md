# microservice_test

Testing the VERT.X library (requires Java 8):

* git clone [microservice_test](https://github.com/bitvector2/microservice_test.git)
* cd microservice_test
* Edit src/main/resources/microservice_test.properties
* Edit src/main/resources/logback.groovy
* Edit src/main/resources/hibernate.properties
* ./gradlew clean shadowJar
* ./microservice_test.sh

Use the following command for benchmarking:

* ab -n 10000 -c 64 -k http://<hostname>:8080/products/1

Use the following for restart/fault recovery testing:

* watch --interval 1 curl --silent --show-error --include --max-time 1 http://<hostname>:8080/products

To setup a development environment, download and expand Gradle and Groovy into your home directory at the same level as the Git clone:

* Download and unzip [Gradle 2.5](https://services.gradle.org/distributions/gradle-2.5-all.zip)
* Download and unzip [Groovy 2.4.4](http://dl.bintray.com/groovy/maven/apache-groovy-sdk-2.4.4.zip)

Home directory should have the following directories and you can configure an external gradle in IntelliJ as well as a Groovy SDK

* gradle-2.5
* groovy-2.4.4
* microservice_test

If you want to permanently enable the Gradle daemon to accelerate Gradle builds:

* echo "org.gradle.daemon=true" >> ~/.gradle/gradle.properties
