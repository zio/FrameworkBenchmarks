FROM hseeberger/scala-sbt:11.0.12_1.5.5_2.13.6

RUN wget https://www.yourkit.com/download/docker/YourKit-JavaProfiler-2022.9-docker.zip -P /tmp/ && \
  unzip /tmp/YourKit-JavaProfiler-2022.9-docker.zip -d /usr/local && \
  rm /tmp/YourKit-JavaProfiler-2022.9-docker.zip

WORKDIR /zio-http
COPY src src
COPY project project
COPY build.sbt build.sbt
RUN sbt assembly

EXPOSE 8080
CMD java -Xms2G -Xmx2G -server -agentpath:/usr/local/YourKit-JavaProfiler-2022.9/bin/linux-x86-64/libyjpagent.so=port=10001,listen=all -jar /zio-http/target/scala-2.13/zio-http-assembly-1.0.0.jar
