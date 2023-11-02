#https://codefresh.io/docs/docs/learn-by-example/java/gradle/
FROM gradle:7.6-jdk17-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build -x test --no-daemon --stacktrace

FROM openjdk:17-slim
EXPOSE 7001
RUN mkdir /app

#COPY --from=build /home/gradle/src/build/libs/*.jar /com/spring-boot-application.jar
COPY --from=build /home/gradle/src/build/libs/uwang-rest-api.jar /app/uwang-app.jar
ADD /src/main/resources/application.properties /app/application.properties
ADD /src/main/resources/ /app/resources/

#ENTRYPOINT ["java","-jar","/com/spring-boot-application.jar"]
#https://stackoverflow.com/questions/44491257/how-to-reduce-spring-boot-memory-usage
ENTRYPOINT ["java","-jar","--spring.config.location=classpath:file:/app/application-properties","/app/uwang-app.jar"]
