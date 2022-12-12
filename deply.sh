#!/bin/bash
mvn clean package -Pprod && \
      cp target/wx-api.jar /opt/wx-api/ && \
      cd /opt/wx-api/ && \
      ./tool restart && \
      ./tool log
