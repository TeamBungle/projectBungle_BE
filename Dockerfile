FROM openjdk:8-jdk-alpine
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
#// FROM amazoncorretto:11 ==> amazon corretto 11 사용할 경우
#// ENTRYPOINT ["java","-jar","-Dspring.profiles.active=prod","/app.jar"]
#// => 설정파일을 분리해서 사용할 때
#// java -jar -Dspring.profiles.active=prod app.jar