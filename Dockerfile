FROM clojure

ADD . /app
WORKDIR /app

# Run Tests
RUN lein midje
RUN rm -rf test

# Build uberjar, may want to shift artefact out to a mounted volume
RUN lein uberjar

CMD ["java", "-jar", "target/harland-0.1.0-SNAPSHOT-standalone.jar"]
