# 启动 Zipkin Server

```shell
nohup java -jar zipkin.jar --STORAGE_TYPE=elasticsearch --DES_HOSTS=http://127.0.0.1:9200 &
```

# zipkin-dependencies 搭建

```shell
STORAGE_TYPE=elasticsearch ES_HOSTS=127.0.0.1:9200 java -jar zipkin-dependencies.jar
```