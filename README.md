# microservice_test

Testing the VERT.X library (requires Java 8):

* git clone https://github.com/bitvector2/microservice_test.git
* cd microservice_test
* ./gradlew clean shadowJar
* Edit microservice_test.properties
* ./microservice_test.sh

If you want to permanently enable the Gradle daemon to accelerate Gradle builds:

* echo "org.gradle.daemon=true" >> ~/.gradle/gradle.properties
