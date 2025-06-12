FROM sbtscala/scala-sbt:eclipse-temurin-21.0.6_7_1.10.11_2.13.16

WORKDIR /zio-http
COPY zio-http zio-http
COPY src src
COPY project project
COPY .git .git
COPY build.sbt build.sbt
RUN sbt assembly

EXPOSE 8080
CMD java -Xms2G -Xmx2G -server -jar /zio-http/target/scala-2.13/zio-http-assembly-1.0.0.jar
