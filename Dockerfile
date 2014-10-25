FROM clojure

ADD . /app
WORKDIR /app

# Run Tests
RUN lein midje
RUN rm -rf test

CMD ["lein" "run"]
