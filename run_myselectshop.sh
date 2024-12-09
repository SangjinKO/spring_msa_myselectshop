#!/bin/bash

# Database 실행
echo "Running docker-compose-database.yml..."
docker-compose -f docker-compose-database.yml up --build -d
if [ $? -ne 0 ]; then
  echo "Failed to start docker-compose-database.yml"
  exit 1
fi

# Kafka Server 실행 (docker-compose-database.yml이 완료된 후)
echo "Running docker-compose-kafka.yml..."
docker-compose -f docker-compose-kafka.yml up --build -d
if [ $? -ne 0 ]; then
  echo "Failed to start docker-compose-kafka.yml"
  exit 1
fi

# docker-compose-service.yml 실행 (docker-compose-kafka.yml이 완료된 후)
echo "Running docker-compose-service.yml...It takes a long time..."
docker-compose -f docker-compose-service.yml up --build -d
if [ $? -ne 0 ]; then
  echo "Failed to start docker-compose-service.yml"
  exit 1
fi

echo "All docker-compose files have been successfully started."
