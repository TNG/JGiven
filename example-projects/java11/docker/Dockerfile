FROM jgivenimages.azurecr.io/openjdk-11:latest

ENV APP_DIRECTORY='/app'

COPY ./startup.sh /usr/local/bin
RUN chmod +x /usr/local/bin/startup.sh

COPY ./jgiven-example-maven-project-0.0.1-SNAPSHOT.jar "${APP_DIRECTORY}/jgiven-example.jar"

CMD ["startup.sh"]