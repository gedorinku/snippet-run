FROM snippet-run-image-java
MAINTAINER gedorinku <gedorinku@yahoo.co.jp>

# Kotlin
ENV KOTLIN_VERSION 1.2.0

RUN apt update
RUN apt install -y wget
RUN apt install -y unzip

RUN wget https://github.com/JetBrains/kotlin/releases/download/v${KOTLIN_VERSION}/kotlin-compiler-${KOTLIN_VERSION}.zip -O /tmp/kotlin.zip && \
    unzip /tmp/kotlin.zip -d /opt && \
    rm /tmp/kotlin.zip
