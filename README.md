# 关于项目
项目部署地址:www.39questions.cn

# 后端

后端系统使用jdk17 + springboot3.0.2编写，请务必确保版本一致。

后端系统使用了redis和rabbitmq中间件，请务必确保已完成各中间件的安装。

请根据自己的具体情况修改后端系统的配置文件application.yml中"${}"的内容

项目依赖的图片文件夹在项目文件resource下的res文件夹中，请根据需求自行调整

# 前端

前端系统使用vue3编写，请务必确保版本一致。

开发环境请将vite.config.js中的${your-server-port}替换成后端服务器的端口号

# 数据库

本系统使用mysql作为系统的数据库，数据库脚本文件在sql文件夹中
