#https://codefresh.io/docs/docs/learn-by-example/java/gradle/
FROM gradle:7.2.0-jdk11-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build -x test --no-daemon --stacktrace

FROM openjdk:11.0-jre-slim
EXPOSE 8000
RUN mkdir /com
#COPY --from=build /home/gradle/src/build/libs/*.jar /com/spring-boot-application.jar
COPY --from=build /home/gradle/src/build/libs/api-medical-record-0.0.1-SNAPSHOT.jar /com/spring-boot-application.jar

#ENTRYPOINT ["java","-jar","/com/spring-boot-application.jar"]
#https://stackoverflow.com/questions/44491257/how-to-reduce-spring-boot-memory-usage
ENTRYPOINT ["java","-Xmx512m","-Xss512k","-XX:+UseSerialGC","-XX:MaxRAM=72m","-jar","/com/spring-boot-application.jar"]
#ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-Djava.security.egd=file:/dev/./urandom","-jar","/com/spring-boot-application.jar"]

