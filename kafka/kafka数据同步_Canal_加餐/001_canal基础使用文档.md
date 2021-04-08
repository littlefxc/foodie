### Canal基础使用

------



- ###### Canal基础使用：

  - 对于自建 MySQL , 需要先开启 Binlog 写入功能，配置 binlog-format 为 ROW 模式，my.cnf 中配置如下：

    ```shell
    ## mysql配置修改文件：
    vim /etc/my.cnf
    
    [mysqld]
    log-bin=mysql-bin # 开启 binlog
    binlog-format=ROW # 选择 ROW 模式
    server_id=1 # 配置 MySQL replaction 需要定义，不要和 canal 的 slaveId 重复
    
    ## 重启服务
    ## systemctl stop mariadb  
    ## systemctl start mariadb
    mysql -uroot -proot
    show variables like '%log_bin%';
    
    ## 授权 canal 链接 MySQL 账号具有作为 MySQL slave 的权限, 如果已有账户可直接 grant
    CREATE USER root IDENTIFIED BY 'root';  
    GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY 'root' WITH GRANT OPTION;
    
    -- CREATE USER canal IDENTIFIED BY 'canal';  
    -- GRANT ALL PRIVILEGES ON *.* TO 'canal'@'%' IDENTIFIED BY 'canal' WITH GRANT OPTION;
    -- GRANT SELECT, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'canal'@'%';
    
    FLUSH PRIVILEGES;
    
    ## 日志存放目录
    cd /var/lib/mysql
    
    ## 创建文件夹并 解压 canal
    mkdir /usr/local/canal
    tar -zxvf canal.deployer-1.1.4.tar.gz -C /usr/local/canal/
    
    ## 配置文件
    vim /usr/local/canal/conf/canal.properties
    ## java程序连接端口
    canal.port = 11111
    
    vim /usr/local/canal/conf/example/instance.properties
    ## 不能与已有的mysql节点server-id重复
    canal.instance.mysql.slaveId=1001
    ## mysql master的地址
    canal.instance.master.address=192.168.11.31:3306
    
    ## 修改内容如下:
    canal.instance.dbUsername=root #指定连接mysql的用户密码
    canal.instance.dbPassword=root
    canal.instance.connectionCharset = UTF-8 #字符集
    
    ## 启动canal
    cd /usr/local/canal/bin
    ./startup.sh
    
    ## 验证服务
    cat /usr/local/canal/logs/canal/canal.log
    ## 查看实例日志
    tail -f -n 100 /usr/local/canal/logs/example/example.log
    ```

  - canal properties配置：

    ```json
    # position info
    canal.instance.master.address=172.16.210.74:3306  #指定要读取binlog的MySQL的IP地址和端口
    canal.instance.master.journal.name= #从指定的binlog文件开始读取数据
    canal.instance.master.position= #指定偏移量，做过主从复制的应该都理解这两个参数。
                                     #tips：binlog和偏移量也可以不指定，则canal-server会从当前的位置开始读取。我建议不设置
    canal.instance.master.timestamp=
    canal.instance.master.gtid=
    # rds oss binlog
    canal.instance.rds.accesskey=
    canal.instance.rds.secretkey=
    canal.instance.rds.instanceId=
    # table meta tsdb info
    canal.instance.tsdb.enable=true
    #canal.instance.tsdb.url=jdbc:mysql://127.0.0.1:3306/canal_tsdb
    #canal.instance.tsdb.dbUsername=canal
    #canal.instance.tsdb.dbPassword=canal
    #这几个参数是设置高可用配置的，可以配置mysql从库的信息
    #canal.instance.standby.address =
    #canal.instance.standby.journal.name =
    #canal.instance.standby.position =
    #canal.instance.standby.timestamp =
    #canal.instance.standby.gtid=
    # username/password
    canal.instance.dbUsername=canal #指定连接mysql的用户密码
    canal.instance.dbPassword=canal
    canal.instance.connectionCharset = UTF-8 #字符集
    # enable druid Decrypt database password
    canal.instance.enableDruid=false
    #canal.instance.pwdPublicKey=MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBALK4BUxdDltRRE5/zXpVEVPUgunvscYFtEip3pmLlhrWpacX7y7GCMo2/JM6LeHmiiNdH1FWgGCpUfircSwlWKUCAwEAAQ==
    # table regex
    #canal.instance.filter.regex=.*\\..*
    canal.instance.filter.regex=risk.canal,risk.cwx #这个是比较重要的参数，匹配库表白名单，比如我只要test库的user表的增量数据，则这样写 test.user
    # table black regex
    canal.instance.filter.black.regex=
    # table field filter(format: schema1.tableName1:field1/field2,schema2.tableName2:field1/field2)
    #canal.instance.filter.field=test1.t_product:id/subject/keywords,test2.t_company:id/name/contact/ch
    # table field black filter(format: schema1.tableName1:field1/field2,schema2.tableName2:field1/field2)
    #canal.instance.filter.black.field=test1.t_product:subject/product_image,test2.t_company:id/name/contact/ch
    # mq config
    canal.mq.topic=example
    # dynamic topic route by schema or table regex
    #canal.mq.dynamicTopic=mytest1.user,mytest2\\..*,.*\\..*
    canal.mq.partition=0
    # hash partition config
    #canal.mq.partitionsNum=3
    #canal.mq.partitionHash=test.table:id^name,.*\\..*
    #################################################
    ```

  - 1

  - 1

  - 1

  - 1