FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mkdir -p bin && \
    find src/main/java -name "*.java" > sources.txt && \
    javac -encoding UTF-8 -d bin @sources.txt

EXPOSE 10000

CMD ["sh", "-c", "java -cp bin com.resort.billing.BillingApplication ${PORT:-10000}"]
