FROM maven:3.8.6-openjdk-11-slim as build

RUN apt-get update && apt-get -y install git

RUN git clone https://github.com/mig-frankfurt/maven.git /maven
WORKDIR /maven
RUN mvn install -Dmaven.javadoc.skip=true -Dmaven.test.skip=true

RUN git clone https://github.com/mig-frankfurt/maven.spring.git /maven-spring
WORKDIR /maven-spring
RUN mvn install -Dmaven.javadoc.skip=true -Dmaven.test.skip=true

RUN git clone https://github.com/mig-frankfurt/dataelementhub.maven.git /dehub-parent
WORKDIR /dehub-parent
RUN mvn install -Dmaven.javadoc.skip=true -Dmaven.test.skip=true

RUN git clone https://github.com/mig-frankfurt/dataelementhub.maven.spring.git /dehub-maven-spring
WORKDIR /dehub-maven-spring
RUN git checkout develop && mvn install -Dmaven.javadoc.skip=true -Dmaven.test.skip=true

RUN git clone https://github.com/mig-frankfurt/dataelementhub.dal.git /dehub-dal
WORKDIR /dehub-dal
RUN git checkout develop && mvn install -Dmaven.javadoc.skip=true -Dmaven.test.skip=true

RUN git clone https://github.com/mig-frankfurt/dataelementhub.model.git /dehub-model
WORKDIR /dehub-model
RUN git checkout develop && sed -i 's/2.2.0/2.3.0-SNAPSHOT/' pom.xml
RUN mvn install -Dmaven.javadoc.skip=true -Dmaven.test.skip=true

COPY $PWD /mtbimporter
WORKDIR /mtbimporter

RUN mvn install -Dmaven.javadoc.skip=true -Dmaven.test.skip=true

FROM r-base:4.2.0

RUN apt-get update && apt-get install -y default-jre

RUN wget https://download.docker.com/linux/debian/dists/bullseye/pool/stable/amd64/docker-ce-cli_20.10.12~3-0~debian-bullseye_amd64.deb && dpkg -i docker-ce-cli_*.deb && rm docker-ce-cli_*.deb

COPY --from=build /mtbimporter/target/mtbimporter-*-jar-with-dependencies.jar /app/mtbimporter.jar
ENTRYPOINT ["java", "-jar", "/app/mtbimporter.jar"]