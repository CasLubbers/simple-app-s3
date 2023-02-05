FROM maven:3.8.5-openjdk-17 as builder

WORKDIR /data/

COPY . .

RUN mvn clean package

FROM openjdk:17

WORKDIR /data/
COPY --from=builder /data/target/*.jar /data/app.jar

ENV AWS_ACCESS_KEY="aws-access-key"
ENV AWS_SECRET_KEY=:"aws-secret-key"
ENV S3_BUCKET_NAME="s3-bucket-name"

ENTRYPOINT ["java","-jar","/data/app.jar"]
