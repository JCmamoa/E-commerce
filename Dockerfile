# Usa a imagem oficial do Eclipse Temurin para Java 21
FROM eclipse-temurin:21-jdk-alpine

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia todos os arquivos do projeto para o container
COPY app_build/ ./app_build/

# Entra no diretório app_build e compila o código
RUN cd app_build && \
    mkdir -p bin && \
    javac -encoding UTF-8 -d bin $(find src/main/java -name "*.java")

# Expõe a porta que o servidor vai rodar
EXPOSE 8080

# Comando para iniciar o servidor
CMD ["java", "-cp", "app_build/bin", "com.ecommerce.Main", "8080"]
