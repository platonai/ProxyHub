# First stage: Build stage
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder

# Set working directory
WORKDIR /build

# Copy entire project
COPY . .

# If there is no `target` directory, it means the project has not been built yet.
# Build the application only if the `target` directory is absent
RUN if [ ! -f "target/ProxyHub.jar" ]; then mvn clean install -DskipTests; else echo "Target directory exists, skipping build"; fi

# Copy JAR for use in next stage
RUN cp $(find /build -name "*.jar" | grep "ProxyHub.jar") /build/app.jar

# Second stage: Runtime stage
FROM eclipse-temurin:21-jre-alpine AS runner

# Set working directory
WORKDIR /app

# Set timezone
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Copy build artifacts
COPY --from=builder /build/app.jar app.jar

EXPOSE 8190

# Create non-root user
RUN addgroup --system --gid 1001 appuser \
    && adduser --system --uid 1001 --ingroup appuser appuser

# Set directory permissions
RUN chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Add build parameters
LABEL maintainer="Vincent Zhang <ivincent.zhang@gmail.com>"
LABEL description="PulsarProxy: A lightweight proxy for Platon Pulsar."

# Startup command, supports dynamic port configuration
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]