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
#COPY --from=build /home/gradle/src/build/ /app/

#ENTRYPOINT ["java","-jar","/com/spring-boot-application.jar"]
#https://stackoverflow.com/questions/44491257/how-to-reduce-spring-boot-memory-usage
#"-Dspring.config.location=classpath:file:/app/resourapplication-properties"
# docker inspect --format='{{.LogPath}}' uwang-rest-api-dev
ENTRYPOINT ["java","-jar","/app/uwang-rest-api.jar"]
