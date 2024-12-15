FROM gradle:8.4-jdk21-alpine AS build
WORKDIR /app
COPY . /app
RUN gradle buildFatJar --no-daemon

FROM openjdk:21-slim-buster
EXPOSE 8080
COPY --from=build /app/build/libs/*-all.jar /app/synergyspace.jar
CMD ["java", "-jar", "/app/synergyspace.jar"]
