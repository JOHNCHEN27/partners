FROM nginx #依赖的基础镜像

WORKDIR /usr/share/nginx/html/    #nginx容器工作目录 一般不用改
USER root        #用户 可以不写

COPY ./nginx.conf /etc/nginx/conf.d/default.conf    #复制当前目录下的conf配置到nginx容器目录中，之后可直接修改conf配置，等同于修改nginx容器中的conf

COPY ./dist  /usr/share/nginx/html/     #将打包的dist目录放到nginx容器目录中
 
EXPOSE 80    #暴露端口

CMD ["nginx", "-g", "daemon off;"]  #启动命令