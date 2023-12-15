FROM openjdk:17
MAINTAINER Stanislav Tyunin <tynin22@gmail.com>
LABEL version="1.0" description="Dostavim project"

ENV APP_HOME=/app
WORKDIR $APP_HOME

COPY . $APP_HOME

RUN ./mvnw clean package

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "target/dostavim.jar"]