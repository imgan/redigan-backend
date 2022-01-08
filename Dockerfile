FROM maven:3.6.2-jdk-11-slim

WORKDIR /usr/src/maven

COPY pom.xml .
RUN mvn -B dependency:resolve-plugins dependency:resolve

COPY . .
CMD ["mvn","spring-boot:run"]
