FROM devops01.icico.net.ir/amazoncorretto:17-alpine
COPY target/*.jar /app.jar
EXPOSE 8080
ENV PROFILE=uat JVM_ARGS="" TZ="Asia/Tehran"
VOLUME /var/log/nicico /var/nicico
ENTRYPOINT java $JVM_ARGS  -Djava.security.egd=file:/dev/urandom -Dspring.profiles.active=$PROFILE -Dspring.redis.host=redis -jar /app.jar
