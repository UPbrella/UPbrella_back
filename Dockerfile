# 기존의 openjdk 이미지 사용
FROM openjdk:11-jre-slim

# 시간대 설정을 위한 tzdata 패키지 설치
RUN apt-get update && apt-get install -y tzdata

# 환경변수를 통해 시간대 설정
ENV TZ=Asia/Seoul

# JAR 파일 복사
ARG JAR_FILE=build/libs/be-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} upbrella-server.jar

# Java 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "upbrella-server.jar"]
