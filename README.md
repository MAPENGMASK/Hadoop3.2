# Hadoop 整体概述
- Hadoop是一个适合海量数据存储和计算的平台
- 主要基于谷歌的三驾马车：GoogleFS,MapReduce,BigTable而实现的

## 三大核心组件
- HDFS

分布式存储，支持主从结构，支持多个NameNode，从节点支持多个DataNode

**NameNode:**负责接收用户请求，是整个文件系统的管理节点，维护文件系统的文件目录树，文件/目录的元信息,每个文件对应的数据块列表。

fsimage ：内存映射，元数据快照

edits ：文件修改日志

seed_txid ：修改顺序记录

VERSION ：版本信息

**SecondaryNameNode:**定期把edits文件中的内容合并到fsimage中


**DataNode:**主要负责存储数据，提供真实文件数据的存储服务，HDFS将文件划分成块，默认Block大小为128MB

---
- MapReduce

分布式计算框架

编程模型，主要负责海量数据计算，主要由两阶段组成：Map和Reduce

Map阶段是一个独立程序，会在很多节点上同时执行，每个节点处理一部分数据

Reduce阶段是一个单独的聚合程序

---
- Yarn

统一资源管理和调度

支持主从架构，主节点最多两个，从节点可以有多个

主节点ResourceManager进程主要负责集群资源的分配和管理

从节点NodeManager主要负责单节点资源管理

## Hadoop3.2集群部署
- Hadoop发行版介绍

官方版本：Apache Hadoop，开源，集群安装维护比较麻烦

第三方发行版本：Cloudera Hadoop（CDH），提供商业支持，收费，使用Cloudera Manager安装维护比较方便

第三方发行版本：HortonWorks（HDP），开源，使用Ambari安装维护比较方便

## 集群安装方式
- 分布式

使用三台Linux机器部署(一主两从)

1，修改hosts文件
> vim /etc/hosts

2，免密码登录，实现主节点可以免密码登录到其他节点
>ssh-keygen -t rsa

>cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys

>ssh-copy-id -i xxxxx

3，jdk1.8 解压配置
>配置全局文件/etc/profile

4，Hadoop3.2解压配置
>配置hadoop-3.2.0/etc/hadoop文件夹

>core-site.xml

>hdfs-site.xml 

>mapred-site.xml

>yarn-site.xml

>hadoop-env.sh

>start-dfs.sh、stop-dfs.sh

>start-yarn.sh、stop-yarn.sh



---
- 伪分布式

使用一台Linux机器部署

1，修改 /etc/hosts 文件
>添加 ip 与 主机名 映射

2, 防火墙关闭
> ufw 查看并关闭

3，ssh免密码登录
>  ssh-keygen -t rsa
>  
> ~/.ssh/id_rsa.pub > >  ~/.ssh/authorized_keys

4，jdk1.8解压配置
>配置全局文件/etc/profile

5，Hadoop3.2解压配置

>配置hadoop-3.2.0/etc/hadoop文件夹

>core-site.xml

>hdfs-site.xml 

>mapred-site.xml

>yarn-site.xml

>hadoop-env.sh

>start-dfs.sh、stop-dfs.sh

>start-yarn.sh、stop-yarn.sh

## HDFS的Shell操作

- bin/hdfs dfs -xxx schema://authority/path
>schema 是hdfs，authority是namenode的节点ip和端口，path是路径名
- -ls :查询

- -put ：从本地上传文件

- -cat ：查看HDFS文件内容

- -get ：下载文件到本地

- -mkdir 【P】 ：创建文件夹

- -rm 【r】 ：删除文件/文件夹



## MapReduce执行过程
- map 阶段

1，把输入文件划分成多个split，默认一个block对应一个split。通过RecordReader类，把split解析成多个<k1,v1>,默认把每一行解析成一个<k1,v1>

2，调用Mapper类中的map(...)函数，形参为<k1,v1>，输出为<k2,v2>,每个split对应一个map task

3，对ma函数输出的<k2,v2>进行分区。不同分区由不同的reduce task处理

4，对每个分区中的数据，按照k2排序，分组（将相同的k2分为一组）

5，把map task输出的<k2,v2>写入到Linux的磁盘文件中

---
- reduce 阶段

1，按照不同分区通过网络copy到不同的reduce节点

2，对reduce端收到的相同分区的<k2,v2>数据进行合并，排序，分组

3，调用Reduce类中的reduce方法，输入<k2 v2列表>，输出<k3,v3>

4，把reduce的输出结果输出保存到HDFS中


## yarn详解
- yarn支持的三种调度器

>FIFO Scheduler：先进先出调度策略，只有一个队列，每个任务占用所有资源

>CapacityScheduler：多队列版本，每个队列分配到不同的资源

>FairScheduler：多队列，共享资源

- 停止Hadoop集群中的任务
>yarn application -kill <appplication_id>

