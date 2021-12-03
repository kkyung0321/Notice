FROM openjdk:8-jdk-alpine
WORKDIR /usr/src/app
EXPOSE 8080
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ARG JAR_FILE=gitignore/jenkins/workspace/jenkins_ci_cd/build/libs/studyWithMe-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","app.jar"]
