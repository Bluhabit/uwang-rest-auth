#https://codefresh.io/docs/docs/learn-by-example/java/gradle/
FROM gradle:7.6-jdk17-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build -x test --no-daemon --stacktrace

FROM openjdk:17-slim
RUN mkdir /app

LABEL maintener="Trian Damai <trian@bluhabit.id>"

COPY --from=build /home/gradle/src/build/libs/uwang-rest-api.jar /app/uwang-app.jar

#ENTRYPOINT ["java","-jar","/com/spring-boot-application.jar"]
#https://stackoverflow.com/questions/44491257/how-to-reduce-spring-boot-memory-usage
# docker inspect --format='{{.LogPath}}' uwang-rest-api-dev
EXPOSE 7001
#ENTRYPOINT ["java","-Djavax.persistence.jdbc.url=jdbc:postgresql://host.docker.internal:6500/uwang-dev","-jar","/app/uwang-app.jar"]
ENTRYPOINT ["java","-jar","/app/uwang-app.jar","--spring.config.location=/data/resources/application.properties"]


