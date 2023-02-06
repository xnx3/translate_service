#!/bin/bash 
# 
# 常规方式安装translate.js的后端翻译接口服务，比如可以在阿里云、腾讯云、等别的云服务上进行安装本系统。在 CentOS 7.4 (没7.4的话7.6等应该也行) 上的安装命令:  
# wget https://gitee.com/HuaweiCloudDeveloper/huaweicloud-solution-build-wangmarketcms/raw/master/shell/normal_install.sh -O install.sh && chmod -R 777 install.sh && sh ./install.sh
# 安装完成后，会将一些账号密码信息，如数据库的，保存到 /root/account.txt 中
#

# 应用包下载地址 
export appDownUrl=http://down.zvo.cn/wangmarket/version/v5.7/wangmarket-5.7.zip

# 加载资源文件下载路径
cd ~
wget https://gitee.com/HuaweiCloudDeveloper/huaweicloud-solution-build-wangmarketcms/raw/master/shell/file.sh -O file.sh
chmod -R 777 file.sh
source ./file.sh
echo "加载资源文件下载路径完毕"

##### JDK、Tomcat安装开始####
wget https://gitee.com/HuaweiCloudDeveloper/huaweicloud-solution-build-wangmarketcms/raw/master/shell/tomcat.sh -O tomcat.sh
chmod -R 777 file.sh
source ./tomcat.sh
echo "加载tomcat.sh完毕"
##### JDK、Tomcat安装结束####

# 删除 file.sh 、mysql.sh 等通用模块
rm -rf /root/file.sh
rm -rf /root/mysql.sh
rm -rf /root/tomcat.sh
rm -rf /root/wangmarket.sh

systemctl stop firewalld.service
# 启动tomcat
echo "启动tomcat"
/mnt/tomcat8/bin/startup.sh
#看启动日志
#echo "看启动日志"
echo "自动部署完成，您可以正常使用了！打开浏览器，访问ip即可使用"