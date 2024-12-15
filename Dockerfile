FROM gradle:8.4-jdk21-alpine AS build
WORKDIR /app
COPY --chown=gradle:gradle . /app
RUN gradle buildFatJar --no-daemon

FROM openjdk:21-slim-buster
WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/synergyspace.jar
EXPOSE 8080
CMD ["java", "-jar", "synergyspace.jar"]
