# EasyBackup

A free, open source, cross-platform content backup, content compression,  remote sending task scheduling application integration framework and engine based on Java.

基于 Java 的免费开源跨平台内容备份，压缩，远程发送一体化任务调度应用及引擎框架。

![EasyBackup](doc/images/EasyBackup.png)

## English

EasyBackup is a free open-source Java-based, cross-platform content backup, content compression, remote sending  task scheduling application and engine framework. 

Provide full support for the need to backup the content: Collection of content-based task scheduling backups, content compression, remote sending, command execution and more. And all this, just a simple configuration management to complete.

Please choose according to purpose:
- **EasyBackup Application**: Can run as application (`libs/easybackup_application`)
- **EasyBackup Engine Framework**:  Can be extended as the content backup engine framework for Java (`libs/easybackup_engine_framework`)


[English Docuemnt](doc_en.md)



## 中文

EasyBackup 是一个基于 Java 的免费开源跨平台内容备份压缩远程发送一体化任务调度应用及引擎框架。

能够为需要进行内容备份的程序场景提供一体化支持：集合了基于任务调度的内容备份，内容压缩，远程发送，命令执行等等功能。而这一切，仅需进行简单的配置管理即可完成。

请根据用途选择：
- **EasyBackup Application**: 开箱即用版本，作为应用运行(`libs/easybackup_application`)
- **EasyBackup Engine Framework**: 作为 Java 内容备份引擎框架扩展(`libs/easybackup_engine_framework`)


[中文文档](doc_zh_CN.md)



## Maven - EasyBackup Engine Framework

```XML
<!-- EasyBackup Engine Framework Maven dependency -->
<dependency>
   <groupId>cn.easyproject</groupId>
   <artifactId>easybackup</artifactId>
   <version>3.3.2-RELEASE</version>
</dependency>

<!-- EasyBackup Plugins -->
<!-- Based on the EasyBackup mysql database backup plugin Maven dependency -->
<dependency>
   <groupId>cn.easyproject</groupId>
   <artifactId>easybackup-mysql</artifactId>
   <version>3.2.0-RELEASE</version>
</dependency>
```


## EasyBackup Web Manger

[EasyBackup Web Manger](https://github.com/ushelp/EasyBackup-Web "Goto EasyBackup Web Manger") 

A Java Web Manager based on the EasyBackup framework Engine, you can complete the backup configuration, start and stop control.

(一个基于 EasyBackup 框架引擎的 Java Web 管理器，可以在 Web UI 下完成备份配置管理，备份启动，停止控制。)

![EasyBackup web manager](doc/images/dashboard.png)

![EasyBackup web manager](doc/images/dashboard_zh_CN.png)


## End

Email：<inthinkcolor@gmail.com>

[http://www.easyproject.cn](http://www.easyproject.cn "EasyProject Home")


**Donation/捐助:**

<a href="http://www.easyproject.cn/donation">
<img alt="
支付宝/微信/QQ/云闪付/PayPal 扫码支付" src="http://www.easyproject.cn/thanks/donation.png"  title="支付宝/微信/QQ/云闪付/PayPal 扫码支付"  height="320" width="320"></img></a>
<div>支付宝/微信/QQ/云闪付/PayPal</div>

<br/>

我们相信，每个人的点滴贡献，都将是推动产生更多、更好免费开源产品的一大步。

**感谢慷慨捐助，以支持服务器运行和鼓励更多社区成员。**

We believe that the contribution of each bit by bit, will be driven to produce more and better free and open source products a big step.

**Thank you donation to support the server running and encourage more community members.**
