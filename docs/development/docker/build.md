# Proxy Hub Docker

Build Image:

```sh
docker build -t proxy-hub:0.0.1 .
```

```sh
docker build -t proxy-hub:latest .
```

Push to Docker Hub:

```sh
docker tag proxy-hub:0.0.1 galaxyeye88/proxy-hub:0.0.1
docker push galaxyeye88/proxy-hub:0.0.1
```

```sh
docker tag proxy-hub:latest galaxyeye88/proxy-hub:latest
docker push galaxyeye88/proxy-hub:latest
```
