FROM eclipse-temurin:23@sha256:507b4bccd77c57a5414153ef5dc145c23faf470d65ad29d88fc06094ee4f8577

ARG jar
ARG version="test"
ARG static_assets
ARG template_assets

RUN mkdir /opt/app
COPY $jar /opt/app/app.jar
COPY $static_assets /opt/app/static/
COPY $template_assets /opt/app/templates/

ENV JAVA_OPTS=""
ENV APP_PORT=8080
ENV APP_HOST=127.0.0.1

LABEL org.opencontainers.image.source="https://github.com/lopcode/photo-fox"
LABEL org.opencontainers.image.revision="$version"

USER nobody
WORKDIR /opt/app

# sense checks
RUN ls -al /opt/app
RUN java -version

ENTRYPOINT exec java ${JAVA_OPTS} -jar /opt/app/app.jar