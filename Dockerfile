FROM ubuntu:17.10
MAINTAINER gedorinku <gedorinku@yahoo.co.jp>

RUN apt update
RUN apt install -y time
RUN useradd container
RUN mkdir /tmp/workspace
RUN chmod 777 /tmp/workspace


# C/C++
RUN apt install -y g++

# Python3
RUN apt install -y python3

# Java
RUN apt install -y openjdk-8-jdk
