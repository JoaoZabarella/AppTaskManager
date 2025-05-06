FROM eclipse-temurin:17-jdk-alpine

# Instala o Maven
RUN apk add --no-cache maven

WORKDIR /app

# Copia o pom.xml e baixa dependências
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia o restante do código
COPY src ./src

# Compila o projeto e gera o .jar
RUN mvn clean package -DskipTests

# Usa um container menor para rodar a aplicação
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copia o .jar do build anterior
COPY --from=0 /app/target/*.jar app.jar

# Porta exposta
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
