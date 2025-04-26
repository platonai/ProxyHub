# Pulsar Proxy Management

## Overview

Unified Proxy Aggregation & Delivery Platform

This project aggregates IP proxies from multiple providers and delivers them to requesters in a standardized, unified
format.

Key Features:

🌍 Multi-source IP collection
🔄 Parse IPs from various providers using LLM
📦 Unified response format for all clients
🛠️ Easy integration via REST API

### Setup proxy provider

The proxy provider is a URL that returns a list of IPs in JSON format. The response should be like this:

```json
{
  "msg": "",
  "code": 0,
  "data": {
    "count": 1,
    "proxy_list": [
      "a585.kdltps.com:20818"
    ]
  }
}
```

```shell
echo "https://your.ip.provider/proxy-rotation-link" > ~/.pulsar/proxy/providers-enabled/provider-name.txt
```

For example:

```shell
echo "https://tps.kdlapi.com/api/gettps/?secret_id=oniicxyglsjicadd4oj9&signature=jotm8jn6syleypxqf2yfam85v1e8xqx6&num=1&pt=2&format=json&sep=1" > ~/.pulsar/proxy/providers-enabled/kdlapi.txt
```

## Usage

1. Run docker:
   ```bash
   docker -e DEEPSEEK_API_KEY=${YOUR-DEEPSEEK_API_KEY} run galaxyeye88/pulsar-hub:latest
   ```

2. Or you can run from source code:
   ```bash
   ./mvnw spring-boot:run -D DEEPSEEK_API_KEY=${YOUR-DEEPSEEK_API_KEY}
   ```

Once pulsar-proxy server is running, you can access IPs from `http://localhost:8190/api/get-proxy`.
