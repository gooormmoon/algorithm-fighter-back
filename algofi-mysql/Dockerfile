# MySQL 8.0 버전을 베이스 이미지로 사용
FROM mysql:8.0

# init 파일을 /docker-entrypoint-initdb.d/로 복사
# /docker-entrypoint-initdb.d/에 있는 SQL 스크립트는 컨테이너가 처음 실행 시 자동 실행됨
#COPY init/create_table.sql /docker-entrypoint-initdb.d/

# 사용자 정의 설정 파일을 /etc/mysql/custom.cnf로 복사
COPY ./config/mysql.conf /etc/mysql/conf.d/custom.cnf

#권한: mysql.conf 파일에 대한 읽기 권한이 있는지 확인하세요.
#chmod -R 644 /etc/mysql/conf.d/custom.cnf

# 계정 정보 설정
ENV MYSQL_ROOT_PASSWORD=mypassword
ENV MYSQL_USER=myuser
ENV MYSQL_PASSWORD=mypassword
ENV MYSQL_DATABASE=mydb

# MySQL이 사용하는 3306 포트를 노출
EXPOSE 3306

# MySQL 서버 실행, 사용자 정의 설정 파일 지정
CMD ["mysqld"]
#,"--defaults-file=/etc/mysql/conf.d/custom.cnf"