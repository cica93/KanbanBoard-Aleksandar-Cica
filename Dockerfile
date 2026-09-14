# Use the official OpenJDK image as a base image
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/quarkus-app/lib/ /app/lib/
COPY target/quarkus-app/*.jar /app/
COPY target/quarkus-app/app/ /app/app/
COPY target/quarkus-app/quarkus/ /app/quarkus/

EXPOSE 3033

ENV QUARKUS_HTTP_PORT=3033
ENV QUARKUS_HTTP_HOST=0.0.0.0

ENV QUARKUS_DATASOURCE_DB_KIND=mysql
ENV QUARKUS_DATASOURCE_USERNAME=root
ENV QUARKUS_DATASOURCE_PASSWORD=Cica1206!
ENV QUARKUS_DATASOURCE_JDBC_URL="jdbc:mysql://host.docker.internal:3306/shema2?connectionTimeZone=UTC&serverTimezone=UTC&useSSL=false"
ENV QUARKUS_DATASOURCE_JDBC_MAX_SIZE=16

ENV QUARKUS_HIBERNATE_ORM_DATABASE_GENERATION=none
ENV QUARKUS_HIBERNATE_ORM_DIALECT=org.hibernate.dialect.MySQL8Dialect
ENV QUARKUS_HIBERNATE_ORM_LOG_SQL=true
ENV QUARKUS_HIBERNATE_ORM_JDBC_TIMEZONE=UTC

CMD ["java", "-jar", "quarkus-run.jar"]

