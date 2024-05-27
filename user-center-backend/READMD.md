# 用户中心项目

相当于一个用户管理系统。



## 需求分析

1、**登录/注册**

2、用户管理（仅管理员可见） 对用户的查询或者修改 -- 后续可根据需求扩展

3、用户校验 （校验条件可设置为是否收费）  -- 后续可根据需求扩展

## 技术选型

前端：HTML + CSS + JS + React + 组件库 Ant Design + Umi + Ant Design Pro(现成的管理系统)

后端:  

 Java

Spring：依赖注入框架，帮助管理Java对象，集成一些其他的内容

Springmvc：web框架，提供接口访问、restful接口等能力

mybatis：java操作数据库的框架，持久层框架，对jdbc的封装

mtbatis-plus: mybatis的增强版本，实现类基本的CRUD

springboot：快速启动/快速集成项目，不用自己管理spring配置，不用自己整合各种框架

mysql

部署：服务器/容器（平台）

## 项目计划

1. 初始化项目
 	1. 前端初始化   node 16.20.0(最好用指定版本)
          	1. 初始化项目
               	2. 引入一些组件之类
                    	3. 框架介绍/瘦身
 	2. 后端初始化
          	1. 准备环境（MySQL等）
               	2. 引入框架（整合框架）
2. 数据库设计
3. **登录/注册**
	1. 前端
	2. 后端
4. 用户管理（管理员可见） -- 对用户的增删查改
	1. 前端
	2. 后端

## 数据库表设计

有哪些表？ 表中字段？ 字段的类型？ 数据库字段添加索引?

表和表之间的关联

用户表：

id（主键） bigint

username（昵称） varchar

userAccount  登录账号

avatarUrl （头像） varchar

gender（性别） tinyint

password（密码）

phone（电话）

email（邮箱）

userStatus 用户状态   0 正常

isValid（是否有效 -- 比如被封号） tinyint

createTime （创建时间） datetime

updateTime（更新时间） datetime

isDelete （是否删除 --逻辑删除） tinyint

## 自动生成器的使用

MybatisX插件，自动根据数据库表内容生成domain实体对象、mapper、mapper.xml、service。

**注意：如果数据库表字段名和实体属性名一致，则不需要开启mybatis-plus的自动驼峰命名规范** （自动生成的实体默认和数据库字段名一致）

generateAllSetDefault插件，可以在新创建的对象快速设置默认值，用于快速测试，提高开发效率



## 注册逻辑

1.用户在前端输入账户、密码以及校验码（可采用图形验证码等）

2.校验用户账号、密码、校验码是否符合要求

1. 账号不小于四位
2. 密码不小于八位
3. 其他校验
4. **账户不能重复**
5. **账户不包含特殊字符 ** --正则表达式判断
6. **密码和校验密码相同**  

**3.对密码进行加密（密码必须加密，不能以明文存储到数据库中）** -- 采用加密算法

4.向数据库插入用户数据

**5.单元测试 对每项逻辑进行测试**



## 登录功能

**登录接口**

接受参数： 账户、密码（可扩展校验码）

请求参数：POSR   *请求参数很长时不建议使用get*

请求体： JSON格式数据

返回值：用户信息（脱敏）

**逻辑**

1.校验用户账户密码是否合法

1. 非空
2. 长度判断
3. 账户不包含特殊字符

2.校验密码是否输入正确，和数据库中的密文密码去对比

3.返回用户信息

1. 脱敏（不返回关键数据）

4.记录用户的登录状态（session）,将其存储到服务器上（利用SpringBoot框架封装的服务器tomcat记录）

5.单元测试

### 如何判断是哪个用户登录？

1.连接服务器端后，会得到一个session1状态，返回给前端

2.登录成功后，得到了登录成功的session，给session设置相应的值，返回给前端一个设置 cookie的命令，

3.前端收到后端的命令后，设置cookie 存储到浏览器本地

4.前端再次请求服务器时，携带cookie 去访问

5.服务器判断前端传来的cookie，是否和session一致。



## 用户管理接口

用户表扩展一个userRole属性 表示用户的角色，必须鉴权

1.查询用户

1. 允许根据用户名查询

2.删除用户

1. 根据用户名进行删除



**用户登录状态接口**



## 优化

### 后端优化

1、返回通用对象 

目的: 给返回对象补充一些信息，告诉前端在业务上是否请求成功，如果请求失败 返回相应提示

```json
{
	"code": 0, //业务状态码
    "data": {
        //数据信息 列表或者其他
        
    },
    "message": ""  
}
```

2、封装全局异常处理

1. 定义业务异常类

   1. 扩展运行时异常

      1. 自定义构造函数，更灵活的设置字段

   2. ​	编写全局异常处理器

      作用：

      1.捕捉代码中的所有异常（业务异常和运行时异常），记录日志，内部消化，返回给前端通用的返回对象信息

      2.同时屏蔽掉代码项目框架本身的异常

      3.集中处理，比如记录日志

   实现：AOP（面向切面编程）在调用方法前后进行额外的处理

3、全局请求日志和登录校验



## 项目部署

### 多环境

同一套项目在不同阶段需要根据实际情况来调整部署配置到不同的机器上

为什么需要？ 

1. 每个环境互不影响
2. 区分不同的阶段：开发 / 测试 / 生产
3. 对项目进行优化
   1. 本地日志级别： 调整日志配置 区分不同阶段下的信息。
   2. 精简项目，节省项目体积
   3. 项目的环境 / 参数可以调整，比如 JVM参数

针对不同的环境做不同的事情。



多环境分类：

1. 本地环境（自己的电脑） localhost
2. 开发环境（远程开发）大家连接同一台机器，为了大家开发方便
3. 测试环境（测试） 开发 / 测试 / 产品，单元测试  / 性能测试 / 功能测试 / 系统集成测试，独立的数据库、独立的服务器
4. 预发布环境（体验服） 和正式环境一致，正式数据库，更严谨，方便查出问题
5. 正式环境（线上，公开对外访问的项目）尽量不改动代码，保证上线前的代码是 “ 完美 ”运行
6. 沙箱环境（实验环境） 一般是针对某个功能的实验



### 前端多环境实战

请求地址

1.  开发环境：localhost:8000
2. 线上环境：自己的域名或者后端服务器ip地址

Umi框架，build时会自动传入NODE_ENV == production参数， start NODE_ENV参数为development

启动方式

1.  开发环境： npm run start （本地启动，监听端口，自动更新）
2. 线上环境： npm run build （项目构建打包） 可以使用serve工具启动（npm -i serve安装）

项目配置 ： 不同的项目（框架的配置文件不同），umi的配置文件是config，可以在配置文件后添加对应的环境名称后缀来区分开发环境

和生产环境，参考文档： https://umijs.org/zh-CN/docs/deployment

1.  开发环境： config.dev.ts
2. 生产环境： config.prod.ts
3. 公告配置：config.ts 不带后缀



### 后端多环境实战

SpringBoot项目，通过application.yml 添加不同后缀来区分配置文件

```yaml
application-prod.yaml
```

可以在启动项目时传入环境变量,指定spring对应的配置文件

```java
java -jar .\user-center-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

主要修改

1. 依赖的环境地址
   1. 数据库地址
   2. 缓存地址
   3. 消息队列地址
   4. 项目端口号
2. 服务器配置

SpringBoot配置文件加载有优先级，当存在两个不同的配置文件时，指定的配置文件中如果和其他配置文件有相同的部分，则会覆盖相同的部分，以指定的配置文件为准， 与其他配置文件不相同的内容共存。

**本质上是相同覆盖，不同合并**



### 云服务器部署

需要Linux服务器（建议CentOS8 or CentOS 7.6以上）

#### 原始部署

不借用其他工具，手动部署服务

**前端**

需要web服务器： **nginx**、apache、tomcat

安装nginx、

1.  使用服务器自带的命令下载， 链接下载地址  比如:centOS的yum
2. 本地下载好nginx包，上传至服务器上，解压
3. 在服务器上配置nginx环境变量，配置完成之后，执行 mvn-v 查看是否安装成功

**如果链接安装较慢，推荐使用本地包上传，更快。**

nginx启动前端

1. 本地上传前端的代码压缩包至服务器解压
2. 配置nginx.conf文件，指定监听端口80时 打开的路径文件（修改路径文件为解压后的前端项目）

nginx 启动前端项目

**后端**

需要安装java（jdk）、maven

安装java：使用系统自带命令安装 -- **yum install -y java-1.8.0-openjdk* **     或者 上传JDK包解压

安装完成java之后，需要配置环境变量，在etc/profile中

也可以使用git clone 下载github或者gitlab上的代码 ，解压（不推荐，下载速度慢）

```
//mvn打包构建命令，跳过测试 
mvn package -DskipTests

//使用命令启动后端项目    指定项目优先配置文件为prod生产环境下的配置
java -jar ./项目jar包  --spring.profiles.active=prod 
```



#### 宝塔部署

Linux运维面板

官方安装： https://www.bt.cn/new/download.html

方便管理服务器、方便安装软件



#### docker容器部署

首先需要下载docker  -- 可以利用宝塔界面下载， 或者使用命令下载

**Dockfile 用于指定构建镜像的方法**

利用Dockerfile文件编写相应代码 构建前端后端镜像，再通过镜像来启动容器

Dockerfile编写：

1.  **FROM  -- 依赖的基础镜像**
2. **WORKDIR -- 工作目录**
3. **COPY -- 从本机复制**
4. **RUN  -- 执行命令**
5. **CMD / ENTRYOPOINT  (附件额外参数) 指定允许容器时默认执行的命令**



##### **前端docker部署**

使用Dockerfile文件构建前端项目镜像

**1、基于dist打包之后的前端项目构建项目镜像**

1. 在云服务器上创建目录（或者指定一个目录用来上传打包后的dist前端项目）
2. 上传Dockerfile文件到目录下，以及自己的nginx.conf配置文件和dist项目文件到目录下
3. 执行构建镜像命令  **docker build -t user-center-front:v0.0.1 .**
4. 基于镜像创建容器

示例： 假设我们指定云服务器的home目录，在此下面创建目录为 user-center-front 用来存放dist文件 、Dockerfile文件、nginx.conf配置文件

```
#在云服务器home目录下创建一个目录用来存放构建前端镜像文件
mkdir user-center-front

#上传dist文件 nginx.conf文件 Dockerfile文件到 user-center-front目录下
在user-center-front目录下输入 docker build -t user-center-front:v0.0.1 . 命令来根据文件构建镜像
user-center-front 为镜像名称 可以自定义 :v0.0.1 为版本号，  最后一个 .(点) 表示Dockerfile文件在当前目录下
-t 表示给当前镜像打标签

```

**Dockerfile文件内容**

```dockerfile
FROM nginx #依赖的基础镜像

WORKDIR /usr/share/nginx/html/    #nginx容器工作目录 一般不用改
USER root        #用户 可以不写

COPY ./nginx.conf /etc/nginx/conf.d/default.conf    #复制当前目录下的conf配置到nginx容器目录中，之后可直接修改conf配置，等同于修改nginx容器中的conf

COPY ./dist  /usr/share/nginx/html/     #将打包的dist目录放到nginx容器目录中
 
EXPOSE 80    #暴露端口

CMD ["nginx", "-g", "daemon off;"]  #启动命令

```

**构建前端项目镜像**

```docker
docker build -t user-center-frontend:v0.0.1 .
```

**利用前端项目镜像创建容器**

```
docker run -d --name user-center-frontend -p 80:80   user-center-fronted:v0.0.1
```

**命令解释:** run 表示运行一个容器，  -d 表示后台运行， --name 表示为这个容器取名， -p 指定宿主机与容器映射的端口号（前者为宿主机端口也就是云服务器端口，后者为容器端口）， 最后指定镜像名称表示利用哪个镜像来构建容器.



**2、基于git clone 直接拉取前端项目构建前端项目镜像**

如果是利用git clone 克隆github或者gitlab上的代码拉取到服务器上，那么需要在前端项目中指定Dockerfile配置 以及nginxconf配置文件。

```dockerfile
#git clone 项目中的Dockerfile文件内容
FROM nginx #依赖的基础镜像

WORKDIR /usr/share/nginx/html/    #nginx容器工作目录 一般不用改
USER root        #用户 可以不写

COPY ./docker/nginx.conf /etc/nginx/conf.d/default.conf    #复制docker目录下的conf配置到nginx容器目录中，之后可直接修改conf配置，等同于修改nginx容器中的conf

COPY ./dist  /usr/share/nginx/html/     #将打包的dist目录放到nginx容器目录中
 
EXPOSE 80    #暴露端口

CMD ["nginx", "-g", "daemon off;"]  #启动命令
```

**Dockerfile文件中 COPY属性 主要是是nginx.conf和 dist路径问题。**

最后在存放项目目录下 执行 **docker build -t user-center-front:v0.0.1 .**   命令 构建前端项目镜像



##### 后端docker部署

**1、基于jar包构建后端项目镜像**

1. 首先利用maven在本地将后端项目打包，生成项目jar包
2. 在云服务器上创建一个目录用来存放构建后端项目镜像所需要的文件
3. 上传打包后的后端项目jar包到 云服务器指定目录
4. 上传Dockerfile文件
5. 基于Dockerfile文件构建镜像
6. 基于镜像创建容器

**Dockerfile文件内容**

```dockerfile
#使用的基础镜像，这里是jdk1.8
FROM java:8u20        
#维持者不用修改 可以不用
MAINTAINER docker_maven docker_maven@email.com   
#项目工作目录 一般不修改  镜像工作目录 可以去ROOT工作目录
WORKDIR /app        
#项目打包的jar包（记得改为实际项目jar包） 以及别名 可以自定义项目名
ADD server-1.0-SNAPSHOT.jar server.jar 
#cmd查看项目版本 一般不用改
CMD ["java", "-version"]   
#项目运行的命令 根据实际项目别名更改最后一项
ENTRYPOINT ["java", "-Dfile.encoding=utf-8","-jar", "server.jar","--spring.profiles.active=prod"]    
 #暴露的端口 根据实际情况更改
EXPOSE 8080                                     
```

**在云服务器上使用命令构建后端项目镜像** 

```
docker build -t user-center-backend:v0.0.1 .
```

**基于镜像构建docker容器**

```
docker run -d --name user-center-backend -p 8080:8080 user-center-backend:v0.0.1
```

**命令解释:** run 表示运行一个容器，  -d 表示后台运行， --name 表示为这个容器取名， -p 指定宿主机与容器映射的端口号（前者为宿主机端口也就是云服务器端口，后者为容器端口）， 最后指定镜像名称表示利用哪个镜像来构建容器



**2、git clone仓库代码构建后端镜像**

1. **在项目路径中编写Dockerfile文件，与项目中的pom.xml同级目录**
2. **从代码仓库拉取代码至云服务器指定目录或者路径**
3. **运行构建镜像命令，创建镜像**
4. **基于镜像创建项目容器**

项目中的Dockerfile文件

```dockerfile
# Docker 镜像构建 jdk8 以及maven打包环境， 由于代码还未是jar包，所有我们需要在构建镜像的时候进行代码，需要用到maven构建
FROM maven:3.5-jdk-8-alpine as builder 

# Copy local code to the container image.
WORKDIR /app   #项目工作目录 一般是容器目录 可以更改 可以不动，相当于项目根目录
COPY pom.xml .   #将项目中的pom文件 复制到容器中 .(点)表示容器目录
COPY src ./src   #将项目的src目录下内容  赋值到容器中的src目录下

# Build a release artifact.
RUN mvn package -DskipTests   #利用maven构建时，跳过单元测试

# Run the web service on container startup.  指定运行命令,jar包路径以及 激活的配置文件
CMD ["java","-jar","/app/target/user-center-backend-0.0.1-SNAPSHOT.jar","--spring.profiles.active=prod"]

```

构建镜像命令以及构建容器命令上面重复过，这里不再赘述.



##### 注意事项

在docker中，容器和容器之间存在的网络隔离，当我们创建容器的时候，如果没有为容器指定网络，那么docker会将容器自动加入到默认网络，默认网络有缺陷，所以我们自定义一个网络，将需要运行互联的容器加入到同一个网络中，实现访问

```
#docker创建网络
docker network create 网络名
#将容器加入到网络中
docker network connect 网络名 容器名
```

如果我们是将前端、后端、数据库、redis等部署到一个云服务器上，那么我们最好把他们都加入到一个网络中，这样**在前端后端项目配置文件配置数据库地址或者配置后端地址时，我们可以直接填写容器名**，因为它们都在一个网络中，可以通过容器名访问项目，不需要填写地址，方便维护。

