# Build stage
FROM gradle:8.10.1-jdk17 AS build

ARG FILE_DIRECTORY
WORKDIR /app

# Gradle wrapper를 사용하여 프로젝트 파일을 복사합니다.
COPY gradle /app/gradle
#COPY gradle-wrapper.jar /app/gradle-wrapper.jar
COPY gradlew /app/gradlew
COPY settings.gradle /app/settings.gradle
COPY build.gradle /app/build.gradle

#COPY $FILE_DIRECTORY /app/$FILE_DIRECTORY
COPY . /app

## 각 모듈별 빌드파일 복사
#COPY eureka/build.gradle /app/eureka/build.gradle
#COPY gateway/build.gradle /app/gateway/build.gradle
#COPY user/build.gradle /app/user/build.gradle
#COPY product/build.gradle /app/product/build.gradle
#COPY productbatch/build.gradle /app/productbatch/build.gradle
#COPY api/build.gradle /api/user/build.gradle
#COPY common/build.gradle /api/common/build.gradle
#
## 각 모듈 소스 코드를 복사
#COPY eureka /app/eureka
#COPY gateway /app/gateway
#COPY user /app/user
#COPY product /app/product
#COPY productbatch /app/productbatch
#COPY api /app/api
#COPY common /app/common

# Gradle 명령어로 빌드 실행
RUN ./gradlew clean build -x test
#RUN ./gradlew :eureka:build -x test
#RUN ./gradlew :$FILE_DIRECTORY:build --no-daemon -x test


## 실제 실행할 이미지
FROM openjdk:17-jdk-slim

# ARG로 전달된 FILE_DIRECTORY를 환경 변수로 설정
ARG FILE_DIRECTORY
ENV FILE_DIRECTORY=$FILE_DIRECTORY

# JAR 파일을 빌드에서 복사합니다.
COPY --from=build /app/$FILE_DIRECTORY/build/libs/*-SNAPSHOT.jar /app/$FILE_DIRECTORY.jar

# 애플리케이션 실행
#CMD ["java", "-jar", "/app/$FILE_DIRECTORY.jar"]

# 애플리케이션 실행 시 쉘을 통해 변수를 확장하도록 수정
ENTRYPOINT ["sh", "-c", "java -jar /app/$FILE_DIRECTORY.jar"]

