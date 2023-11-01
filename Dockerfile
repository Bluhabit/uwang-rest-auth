FROM openjdk:17-jdk-alpine
WORKDIR /app
EXPOSE 7001
#COPY --from=build /home/gradle/src/build/libs/*.jar /com/spring-boot-application.jar
COPY build/libs/uwang-rest-api-*.jar uwang-app.jar

#ENTRYPOINT ["java","-jar","/com/spring-boot-application.jar"]
#https://stackoverflow.com/questions/44491257/how-to-reduce-spring-boot-memory-usage
ENTRYPOINT ["java","-Xmx512m","-Xss512k","-XX:+UseSerialGC","-XX:MaxRAM=72m","-jar","/app/uwang-app.jar"]
