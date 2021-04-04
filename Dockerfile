FROM openjdk:8-jre-alpine

COPY ./build/robocode-*-setup.jar ./
RUN java -jar robocode-*-setup.jar

ENV USER=https://hub.docker.com/r/zamboch/roborumble

WORKDIR /root/robocode
ENTRYPOINT ["./roborumble.sh"]