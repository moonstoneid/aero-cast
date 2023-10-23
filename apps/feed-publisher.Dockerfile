FROM amazoncorretto:17-al2-jdk AS build
WORKDIR /app
COPY .mvn .mvn
COPY feed-aggregator/pom.xml feed-aggregator/
COPY feed-common/pom.xml feed-common/
COPY feed-common/src feed-common/src
COPY feed-publisher/pom.xml feed-publisher/
COPY feed-publisher/src feed-publisher/src
COPY mvnw .
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 ./mvnw -pl feed-publisher -am clean package -DskipTests

FROM amazoncorretto:17-al2-jdk
ENV JAVA_ARGS=""
WORKDIR /app
COPY --from=build /app/feed-publisher/target/*.jar ./app.jar
ENTRYPOINT java $JAVA_ARGS -jar app.jar