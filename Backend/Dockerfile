FROM openjdk:21
ARG JAR_FILE=target/backend.jar
COPY ${JAR_FILE} backend.jar
ENTRYPOINT ["java","-jar","/backend.jar"]