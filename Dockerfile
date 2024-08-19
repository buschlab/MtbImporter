FROM maven:3-eclipse-temurin-21 as build

RUN apt-get update && apt-get -y install git

RUN git clone https://github.com/imi-frankfurt/maven.git /maven
WORKDIR /maven
RUN git checkout 061d99ff7da75f53b2b27b6193dcf2a9d254ed93 && mvn install -Dmaven.javadoc.skip=true -Dmaven.test.skip=true

RUN git clone https://github.com/imi-frankfurt/maven.spring.git /maven-spring
WORKDIR /maven-spring
RUN git checkout 322fa274c9532b89b61d69b1785060e9c391aee4 && mvn install -Dmaven.javadoc.skip=true -Dmaven.test.skip=true

RUN git clone https://github.com/imi-frankfurt/dataelementhub.maven.git /dehub-parent
WORKDIR /dehub-parent
RUN git checkout bff16bc88486891d0c8d3ba3ec22d4ad7c539749 && sed -i '8 i <version>11.0.0</version>' pom.xml
RUN mvn install -Dmaven.javadoc.skip=true -Dmaven.test.skip=true

RUN git clone https://github.com/imi-frankfurt/dataelementhub.maven.spring.git /dehub-maven-spring
WORKDIR /dehub-maven-spring
RUN git checkout d3e6bed8ead13396e0a52127fef9f685aebbec84 && mvn install -Dmaven.javadoc.skip=true -Dmaven.test.skip=true

RUN git clone https://github.com/imi-frankfurt/dataelementhub.dal.git /dehub-dal
WORKDIR /dehub-dal
RUN git checkout 079da7a05cd4ab29cc45ee2c19749c6baa82b3ea && mvn install -Dmaven.javadoc.skip=true -Dmaven.test.skip=true -Dmaven.compiler.release=21

RUN git clone https://github.com/imi-frankfurt/dataelementhub.model.git /dehub-model
WORKDIR /dehub-model
RUN git checkout d67f3654b371b94394d2c7eaf8b1a83188303df4 && mvn install -Dmaven.javadoc.skip=true -Dmaven.test.skip=true

COPY $PWD /mtbimporter
WORKDIR /mtbimporter

RUN mvn install -Dmaven.javadoc.skip=true -Dmaven.test.skip=true

FROM r-base:4.4.1

RUN apt-get update && apt-get install -y openjdk-21-jre docker-cli

COPY --from=build /mtbimporter/target/mtbimporter-*-jar-with-dependencies.jar /app/mtbimporter.jar
ENTRYPOINT ["java", "-jar", "/app/mtbimporter.jar"]