# Docker 镜像构建
FROM java:8u20

# Copy local code to the container image.
WORKDIR /app
ADD finding-partners-backend-0.0.1-SNAPSHOT.jar server.jar

CMD ["java", "-version"]


ENTRYPOINT ["java", "-Dfile.encoding=utf-8","-jar", "server.jar","--spring.profiles.active=prod"]

EXPOSE 8080