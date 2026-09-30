# 비밀값을 넣지 않은 jar로 이미지를 만든다. 비밀값은 실행할 때 환경변수와 마운트한 키 파일로 넣는다.
FROM eclipse-temurin:17-jre-jammy

# 시간대 설정(tzdata)과 컨테이너 헬스체크(curl)
RUN apt-get update \
    && apt-get install -y --no-install-recommends tzdata curl \
    && rm -rf /var/lib/apt/lists/*

# 환경변수를 통해 시간대 설정
ENV TZ=Asia/Seoul

# JAR 파일 복사
ARG JAR_FILE=build/libs/be-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} upbrella-server.jar

# Java 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "upbrella-server.jar"]
