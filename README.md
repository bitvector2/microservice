# microservice_test

Testing the VERT.X library (requires Java 8):

* git clone https://github.com/bitvector2/microservice_test.git
* cd microservice_test
* ./gradlew clean shadowJar
* Edit microservice_test.properties
* ./microservice_test.sh

If you want to permanently enable the Gradle daemon to accelerate Gradle builds:

* echo "org.gradle.daemon=true" >> ~/.gradle/gradle.properties

Use the following command for benchmarking:

* ab -n 10000 -c 64 -k http://<hostname>:8080/products/1

To setup a development environment, download and expand Groovy and Gradle into your home directory at the same level as the Git clone:

* Download and unzip apache-groovy-sdk-2.4.4.zip
* Download and unzip gradle-2.5-all.zip

Home directory should have the following directories and you can configure an external gradle in IntelliJ as well as a Groovy SDK

* gradle-2.5
* groovy-2.4.4
* microservice_test

