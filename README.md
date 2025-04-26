# Pulsar Proxy Management

## Overview

Supported proxy providers:

* [KuaiDaiLi](https://www.kuaidaili.com/)

## Usage

1. Run docker:
   ```bash
   docker run galaxyeye88/pulsar-hub:latest
   ```

2. Or you can run from source code:
   ```bash
   ./mvnw spring-boot:run
   ```

Once pulsar-proxy server is running, you can access IPs from `http://localhost:8190/api/get-proxy`.

