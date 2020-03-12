# 使用MyBatis数据库逆向生成工具

[MyBatis Generator 插件](https://mapperhelper.github.io/docs/3.usembg/)

1. 在pom中引入通用mapper工具

  ```xml
  <!-- 通用mapper逆向工具 -->
  <dependency>
      <groupId>tk.mybatis</groupId>
      <artifactId>mapper-spring-boot-starter</artifactId>
      <version>2.1.5</version>
  </dependency>
  ```
   
2. 在yml中引入通用mapper配置
 
  ```yaml
  ############################################################
  #
  # mybatis mapper 配置
  #
  ############################################################
  # 通用 Mapper 配置
  mapper:
    mappers: com.fengxuechao.my.mapper.MyMapper
    not-empty: false
    identity: MYSQL
  ```

3. 引入MyMapper接口类

  ```java
  package com.fengxuechao.my.mapper;
  
  import tk.mybatis.mapper.common.Mapper;
  import tk.mybatis.mapper.common.MySqlMapper;
  
  /**
   * 继承自己的MyMapper
   */
  public interface MyMapper<T> extends Mapper<T>, MySqlMapper<T> {
  }
  ```