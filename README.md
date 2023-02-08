translate.js 的后端服务，正在完善中，敬请期待.

# 部署
### 1. 购买服务器

* 核心：1核
* 内存：1G
* 区域：新加坡
* 操作系统：CentOS 7.4
* 系统盘：默认的系统盘就行。无需数据盘
* 弹性公网IP：按流量计费（带宽大小10MB）
* 其他的未注明的，都按照怎么省钱怎么来选即可。

**备注**  
1. 这里会有多个型号，比如什么s3、s6、t6的，你就选最便宜的就行。（一般t6是最便宜的，选它就行）  
1. 安全组：要开放22、80这两个端口


### 2. 一键安装
执行shell命令进行一键部署安装

````
wget https://gitee.com/mail_osc/translate_service/raw/master/install/install.sh -O install.sh && chmod -R 777 install.sh && sh ./install.sh
````

# 使用

在 ````translate.execute();```` 之前，加入一行代码，如下所示：

````
translate.apiHost='http://121.121.121.121/'; //将这里面的ip地址换成你服务器的ip，注意开头，及结尾还有个 / 别拉下
translate.execute();
````

如此，翻译请求接口就会走您自己服务器了。

# 其他
### https场景
如果你网站使用的是https协议，那翻译接口你也要变为https协议，不然会请求不到。  
比如可以使用华为云的全站加速服务，然后在此服务中配置SSL证书使之支持https  
将http变为https方式很多，这里只是提的一种比较方便的方式，其他具体的可以自行尝试，也或者我们出技术人员帮您操作，收几百人工费。

