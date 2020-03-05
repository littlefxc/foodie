# HikariCP数据源

HiKariCP是数据库连接池的一个后起之秀，号称性能最好，可以完美地PK掉其他连接池，是一个高性能的JDBC连接池，基于BoneCP做了不少的改进和优化。

- 字节码精简 ：优化代码，直到编译后的字节码最少，这样，CPU缓存可以加载更多的程序代码；
- 优化代理和拦截器 ：减少代码，例如HikariCP的Statement proxy只有100行代码，只有BoneCP的十分之一；
- 自定义数组类型（FastStatementList）代替ArrayList ：避免每次get()调用都要进行range check，避免调用remove()时的从头到尾的扫描；
- 自定义集合类型（ConcurrentBag ：提高并发读写的效率；
- 其他针对BoneCP缺陷的优化。

```yaml
############################################################
#
# 配置数据源信息
#
############################################################
spring:
  datasource:                                           # 数据源的相关配置
      type: com.zaxxer.hikari.HikariDataSource          # 数据源类型：HikariCP
      driver-class-name: com.mysql.jdbc.Driver          # mysql驱动
      url: jdbc:mysql://localhost:3306/foodie-shop-dev?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true
      username: root
      password: root
    hikari:
      connection-timeout: 30000       # 等待连接池分配连接的最大时长（毫秒），超过这个时长还没可用的连接则发生SQLException， 默认:30秒
      minimum-idle: 5                 # 最小连接数
      maximum-pool-size: 20           # 最大连接数
      auto-commit: true               # 自动提交
      idle-timeout: 600000            # 连接超时的最大时长（毫秒），超时则被释放（retired），默认:10分钟
      pool-name: DateSourceHikariCP     # 连接池名字
      max-lifetime: 1800000           # 连接的生命时长（毫秒），超时而且没被使用则被释放（retired），默认:30分钟 1800000ms
      connection-test-query: SELECT 1
          
############################################################
#
# mybatis 配置
#
############################################################
mybatis:
  type-aliases-package: com.fengxuechao.pojo          # 所有POJO类所在包路径
  mapper-locations: classpath:mapper/*.xml      # mapper映射文件
```