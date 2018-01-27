FROM maven:3-jdk-8

COPY src/ ./src/
COPY include/ ./include/
COPY pom.xml .

RUN mvn -DskipTests clean package

COPY config* ./

CMD java -cp target/accountsservice-1.0-SNAPSHOT.jar:include/eventbusclient-1.0-SNAPSHOT.jar \
  uk.ac.gla.sed.clients.accountsservice.AccountsServiceApplication \
  server config.prod.yml 
