FROM maven:3.8.1-openjdk-11-slim as build

COPY $PWD /mtbimporter
WORKDIR /mtbimporter

RUN mvn install

FROM r-base:4.1.0

RUN apt-get update && apt-get install -y default-jre

RUN wget https://download.docker.com/linux/debian/dists/bullseye/pool/stable/amd64/docker-ce-cli_20.10.7~3-0~debian-bullseye_amd64.deb && dpkg -i docker-ce-cli_20.10.7~3-0~debian-bullseye_amd64.deb && rm docker-ce-cli_20.10.7~3-0~debian-bullseye_amd64.deb

COPY --from=build /mtbimporter/target/mtbimporter-*-jar-with-dependencies.jar /app/mtbimporter.jar
CMD ["java", "-jar", "/app/mtbimporter.jar"]