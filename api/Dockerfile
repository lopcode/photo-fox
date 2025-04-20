FROM eclipse-temurin:24-jdk-noble

ARG jar
ARG libs
ARG version="test"
ARG static_assets
ARG template_assets

RUN mkdir /opt/app
COPY $jar /opt/app/app.jar
COPY $libs /opt/app/libs/
COPY $static_assets /opt/app/static/
COPY $template_assets /opt/app/templates/

ENV JAVA_OPTS=""
ENV APP_PORT=8080
ENV APP_HOST=127.0.0.1

LABEL org.opencontainers.image.source="https://github.com/lopcode/photo-fox"
LABEL org.opencontainers.image.revision="$version"

RUN apt update && apt install -y libvips-dev \
  && rm -rf /var/lib/apt/lists/*

WORKDIR /opt/app
RUN mkdir -p /opt/app/jte-classes

# sense checks
RUN ls -al /opt/app
RUN java -version

ENTRYPOINT exec java ${JAVA_OPTS} -jar /opt/app/app.jar