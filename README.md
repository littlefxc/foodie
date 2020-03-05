# foodie

模块介绍

- foodie-api: 提供 restful 接口，依赖 foodie-service 模块
- foodie-service: 业务层，依赖 foodie-mapper 模块
- foodie-mapper: 持久层，依赖 foodie-pojo
- foodie-pojo: 实体包，依赖 foodie-common
- foodie-common: 公共包

## 目录

[数据库物理外键移除原因](docs/数据库物理外键移除原因.md)

[HikariCP数据源](docs/HikariCP数据源.md)