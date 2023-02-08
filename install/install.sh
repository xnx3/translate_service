#!/bin/bash 
# 
# 常规方式安装translate.js的后端翻译接口服务，比如可以在阿里云、腾讯云、等别的云服务上进行安装本系统。在 CentOS 7.4 (没7.4的话7.6等应该也行) 上的安装命令:  
# wget https://gitee.com/HuaweiCloudDeveloper/huaweicloud-solution-build-wangmarketcms/raw/master/shell/normal_install.sh -O install.sh && chmod -R 777 install.sh && sh ./install.sh
# 安装完成后，会将一些账号密码信息，如数据库的，保存到 /root/account.txt 中
#

# properties.jar 操作properties使用。说明：https://gitee.com/mail_osc/properties 
export propertiesDownUrl=http://down.zvo.cn/properties/properties-1.0.1.jar


##### JDK、Tomcat安装开始####
wget https://gitee.com/HuaweiCloudDeveloper/huaweicloud-solution-build-wangmarketcms/raw/master/shell/tomcat.sh -O tomcat.sh
chmod -R 777 tomcat.sh
source ./tomcat.sh
echo "加载tomcat.sh完毕"
##### JDK、Tomcat安装结束####

####### 安装应用包 #########
cd /mnt/tomcat8/webapps/ROOT/
wget https://mail_osc.gitee.io/translate_service/install/translate.war -O translate.war
unzip translate.war
rm -rf translate.war


# 初始化创建相关文件夹
mkdir /mnt/tomcat8/fileupload/
# 设置application.properties配置
java -cp ~/properties.jar Properties -path=/mnt/tomcat8/webapps/ROOT/WEB-INF/classes/application.properties -set log.datasource.file.path=/mnt/tomcat8/logs/
java -cp ~/properties.jar Properties -path=/mnt/tomcat8/webapps/ROOT/WEB-INF/classes/application.properties -set fileupload.storage.local.path=/mnt/tomcat8/fileupload/

# 删除 tomcat.sh 等通用模块
rm -rf /root/tomcat.sh
systemctl stop firewalld.service
# 启动tomcat
echo "启动tomcat"
/mnt/tomcat8/bin/startup.sh
#看启动日志
#echo "看启动日志"
echo "自动部署完成，您可以正常使用了！打开浏览器，访问ip即可使用"