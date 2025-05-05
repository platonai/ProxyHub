# Proxy Hub

[![Docker Pulls](https://img.shields.io/docker/pulls/galaxyeye88/proxy-hub?style=flat-square)](https://hub.docker.com/r/galaxyeye88/proxy-hub)
[![License: MIT](https://img.shields.io/badge/license-MIT-green?style=flat-square)](LICENSE)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen?style=flat-square)](https://spring.io/projects/spring-boot)
[![OpenAPI Validated](https://img.shields.io/badge/OpenAPI-validated-blue?style=flat-square)](https://swagger.io/specification/)

---

## 🚀 Overview

**Proxy Hub** is an AI-Powered, Unified Proxy Aggregation & Delivery Platform.

It collects IP proxies from multiple providers and delivers them in a standardized format, making integration easy for all kinds of clients and services.

### ✨ Key Features
- 🔮 **AI-powered parsing** — no manual rules needed
- 🌍 **Multi-source proxy aggregation**
- 📦 **Unified API response format**
- 🔌 **Seamless integration** via REST API & microservices

---

## 🛠️ Getting Started

### 1. Run with Docker

```bash
docker run -e DEEPSEEK_API_KEY=${DEEPSEEK_API_KEY} galaxyeye88/proxy-hub:latest
```

---

### 2. Run with IDE

- Create an `application-private.properties` file in the project root:

```properties
DEEPSEEK_API_KEY=${DEEPSEEK_API_KEY}
```

- Open the project in your IDE
- Run the `ProxyApplication` main class

🔗 [Choose Another LLM Provider](https://github.com/platonai/PulsarRPA/blob/3.0.x/docs/config/llm/llm-config.md)

---

### 3. Run from Source Code

```shell
git clone https://github.com/platonai/ProxyHub.git
cd ProxyHub
```

```shell
./mvnw -DDEEPSEEK_API_KEY=${DEEPSEEK_API_KEY} spring-boot:run
```

---

## 🌐 API Usage

### Retrieve Proxies

```bash
curl http://localhost:8192/api/proxies
```

---

### Manage Proxy Providers

**Proxy providers** are URLs that return a list of IPs. Proxy Hub automatically handles vendor-specific formats.

#### Provider URL Example

```
https://tps.kdlapi.com/api/gettps/?secret_id={YOUR-SECRET-ID}&signature=xxx&num=1&pt=2&format=json&sep=1
```

Example JSON response:

```json
{
  "msg": "",
  "code": 0,
  "data": {
    "count": 1,
    "proxy_list": [
      "a585.proxy.com:20818",
      "157.185.157.151:26589"
    ]
  }
}
```

---

#### Provider Management APIs

✅ Add a provider:

```bash
curl -X POST "http://localhost:8192/api/providers/add" -d '
https://your.ip.provider/proxy-rotation-link
'
```

✅ Remove a provider:

```bash
curl -X DELETE "http://localhost:8192/api/providers/remove" -d '
https://your.ip.provider/proxy-rotation-link
'
```

✅ List all providers:

```bash
curl "http://localhost:8192/api/providers"
```

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).
