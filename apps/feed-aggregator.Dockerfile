FROM amazoncorretto:17-al2-jdk AS build
WORKDIR /app
COPY .mvn .mvn
COPY feed-aggregator/pom.xml feed-aggregator/
COPY feed-aggregator/src feed-aggregator/src
COPY feed-common/pom.xml feed-common/
COPY feed-common/src feed-common/src
COPY feed-publisher/pom.xml feed-publisher/
COPY mvnw .
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 ./mvnw -pl feed-aggregator -am clean package -DskipTests

FROM amazoncorretto:17-al2-jdk
ENV JAVA_ARGS=""
WORKDIR /app
COPY --from=build /app/feed-aggregator/target/*.jar ./app.jar
ENTRYPOINT java $JAVA_ARGS -jar app.jar