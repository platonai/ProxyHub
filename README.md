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

### Run ProxyHub

1. Run with Docker:
   ```bash
   docker run -e DEEPSEEK_API_KEY=${YOUR-DEEPSEEK_API_KEY} galaxyeye88/proxy-hub:latest
   ```

2. Or run from source code:
   ```bash
   ./mvnw -D DEEPSEEK_API_KEY=${YOUR-DEEPSEEK_API_KEY} spring-boot:run
   ```

### Retrieve IPs

```bash
curl "http://localhost:8190/api/get-proxy"
```

### Manage Proxy Providers

A proxy provider is a URL that returns a list of IPs. The system supports various formats through vendor-specific parsers.

#### Provider URL Format

Provider URL:
```
https://provider.example.com/api/get-proxies
```

#### Example Provider URL

```
https://tps.kdlapi.com/api/gettps/?secret_id=oniicxyglsjicadd4oj9&signature=jotm8jn6syleypxqf2yfam85v1e8xqx6&num=1&pt=2&format=json&sep=1 -vendor kuaidaili -fmt json
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
curl -X POST "http://localhost:8190/api/add-provider" -d '
https://your.ip.provider/proxy-rotation-link
'
```

Remove a provider:
```bash
curl -X DELETE "http://localhost:8190/api/remove-provider" -d '
https://your.ip.provider/proxy-rotation-link
'
```

List all providers:
```bash
curl "http://localhost:8190/api/list-providers"
```
