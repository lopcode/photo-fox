FROM eclipse-temurin:22@sha256:a7f7d688f16d8a93217cef5cae43a3554c6ac3094f15fa40800716489adeb8da

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