FROM ubuntu:17.10
MAINTAINER gedorinku <gedorinku@yahoo.co.jp>

RUN apt update
RUN apt install -y time
RUN apt install -y curl
RUN apt install -y unzip
RUN apt install -y wget
RUN useradd container
RUN mkdir /tmp/workspace
RUN chmod 777 /tmp/workspace


# C/C++
RUN apt install -y g++

# Python3
RUN apt install -y python3

# Java
RUN apt install -y openjdk-8-jdk

# Kotlin
ENV KOTLIN_VERSION 1.2.0
RUN wget https://github.com/JetBrains/kotlin/releases/download/v${KOTLIN_VERSION}/kotlin-compiler-${KOTLIN_VERSION}.zip -O /tmp/kotlin.zip && \
    unzip /tmp/kotlin.zip -d /opt && \
    rm /tmp/kotlin.zip
