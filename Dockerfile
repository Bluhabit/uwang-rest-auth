#FROM openjdk:17-slim
#WORKDIR /app
#EXPOSE 7001
##COPY --from=build /home/gradle/src/build/libs/*.jar /com/spring-boot-application.jar
#COPY build/libs/uwang-rest-api-*.jar uwang-app.jar
#
##ENTRYPOINT ["java","-jar","/com/spring-boot-application.jar"]
##https://stackoverflow.com/questions/44491257/how-to-reduce-spring-boot-memory-usage
#ENTRYPOINT ["java","-Xmx512m","-Xss512k","-XX:+UseSerialGC","-XX:MaxRAM=72m","-jar","/app/uwang-app.jar"]


# base image to build a JRE
FROM amazoncorretto:17.0.3-alpine as corretto-jdk

# required for strip-debug to work
RUN apk add --no-cache binutils

# Build small JRE image
RUN $JAVA_HOME/bin/jlink \
         --verbose \
         --add-modules ALL-MODULE-PATH \
         --strip-debug \
         --no-man-pages \
         --no-header-files \
         --compress=2 \
         --output /customjre

# main app image
FROM alpine:latest
ENV JAVA_HOME=/jre
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# copy JRE from the base image
COPY --from=corretto-jdk /customjre $JAVA_HOME

# Add app user
ARG APPLICATION_USER=appuser
RUN adduser --no-create-home -u 1000 -D $APPLICATION_USER

# Configure working directory
RUN mkdir /app && \
    chown -R $APPLICATION_USER /app

USER 1000

#COPY --chown=1000:1000 ./app.jar /app/app.jar
COPY build/libs/uwang-rest-api-*.jar /app/uwang-app.jar
WORKDIR /app

EXPOSE 8080
ENTRYPOINT [ "/jre/bin/java", "-jar", "/app/uwang-app.jar" ]