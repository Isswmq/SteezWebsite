FROM openjdk:17-jdk

WORKDIR /app

COPY build/libs/steez-0.1.jar /app/steez.jar

EXPOSE 808

ENTRYPOINT ["java", "-jar", "steez.jar"]