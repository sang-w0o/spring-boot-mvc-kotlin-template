FROM openjdk:8-jdk-alpine AS builder
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY src src
RUN chmod +x ./gradlew
RUN ./gradlew build -x test

FROM openjdk:8-jdk-alpine
COPY --from=builder build/libs/*.jar *.jar
ENV TZ Asia/Seoul
ENTRYPOINT ["java","-jar","*.jar"]