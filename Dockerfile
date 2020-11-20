FROM openjdk:11
RUN mkdir app
ARG JAR_FILE
ADD /target/${JAR_FILE} /app/decrypt-0.0.1.jar
WORKDIR /app
ENTRYPOINT java -jar decrypt-0.0.1.jar