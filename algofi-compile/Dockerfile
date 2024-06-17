# Base 이미지로 OpenJDK 17 설정
FROM openjdk:17-alpine

# 작업 디렉토리 설정
WORKDIR /app

# 필요한 파일 복사
COPY ./build/libs/*-SNAPSHOT.jar /app/algofi.jar

# 필요한 패키지 설치
RUN apk add --no-cache gcc g++ make python3 py3-pip nodejs npm

# 9000 포트를 외부에 노출
EXPOSE 9000

# 컨테이너 실행 시 자바 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "algofi.jar"]
