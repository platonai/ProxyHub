# Proxy Hub

## Overview

Unified Proxy Aggregation & Delivery Platform

This project aggregates IP proxies from multiple providers and delivers them to requesters in a standardized, unified
format.

Key Features:

- 🌍 Multi-source IP collection
- 🔄 Parse IPs from various providers using LLM
- 📦 Unified response format for all clients
- 🛠️ Easy integration via REST API

## Usage

#### Run ProxyHub

1. Run with Docker:
   ```bash
   docker run -e DEEPSEEK_API_KEY=${YOUR-DEEPSEEK_API_KEY} galaxyeye88/proxy-hub:latest
   ```

#### 🛠️ Run with IDE

Open the project in your IDE.

Open `ProxyApplication` in the editor and click `run`.

#### Run from source code:
   ```bash
   ./mvnw -D DEEPSEEK_API_KEY=${YOUR-DEEPSEEK_API_KEY} spring-boot:run
   ```

### Retrieve IPs

```bash
curl "http://localhost:8192/api/proxyies"
```

### Manage Proxy Providers

A proxy provider is a URL that returns a list of IPs. The system supports various formats through vendor-specific parsers.

#### Provider URL Format

Provider URL:

```
https://tps.kdlapi.com/api/gettps/?secret_id={YOUR-SECRET-ID}&signature=jotm8jn6syleypxqf2yfam85v1e8xqx6&num=1&pt=2&format=json&sep=1 -vendor kuaidaili -fmt json
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

#### Managing Providers

Add a provider:
```bash
curl -X POST "http://localhost:8192/api/providers" -d '
https://your.ip.provider/proxy-rotation-link
'
```

Remove a provider:
```bash
curl -X DELETE "http://localhost:8192/api/providers" -d '
https://your.ip.provider/proxy-rotation-link
'
```

List all providers:
```bash
curl "http://localhost:8192/api/providers"
```
