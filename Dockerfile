FROM ubuntu
MAINTAINER gedorinku <gedorinku@yahoo.co.jp>

RUN apt install update
RUN apt install -y time

# C/C++
RUN apt install -y g++
