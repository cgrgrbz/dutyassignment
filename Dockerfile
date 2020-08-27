# Build an image:
# docker build -t cagrigurbuz/dutyassignment .
#
# Run the image with default profile (using in-memory H2 database):
# docker run -p 8080:8080 --rm -it cagrigurbuz/dutyassignment

FROM adoptopenjdk/maven-openjdk11:latest as builder
WORKDIR /usr/src/cagrigurbuz
COPY . .
RUN mvn clean install -DskipTests

FROM adoptopenjdk/openjdk8:ubi-minimal-jre
RUN mkdir /opt/app
COPY --from=builder /usr/src/cagrigurbuz/dutyassignment/target/*-exec.jar /opt/app/dutyassignment.jar
CMD ["java", "-jar", "/opt/app/dutyassignment.jar"]
EXPOSE 8080
