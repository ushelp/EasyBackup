# EasyBackup

EasyBackup 是一个基于 Java 的免费开源跨平台内容备份压缩远程发送一体化任务调度应用及引擎框架。即可开箱即用(**EasyBackup Application**)，又可以作为内容备份引擎框架扩展(**EasyBackup Engine Framework**)。

EasyBackup 能够为需要进行内容备份的程序场景提供一体化支持。集合了基于任务调度的内容备份，内容压缩，远程发送，命令执行等等功能。而这一切，仅需进行简单的配置管理即可完成。

**最新版本**： `3.2.0-RELEASE`

**插件**：

- `easybackup-mysql-plugin-3.2.0-RELEASE.jar`  MySQL 数据库备份插件

### EasyBackup Web Manger

[EasyBackup Web Manger](https://github.com/ushelp/EasyBackup-Web "移步 EasyBackup Web Manger") 是一个基于 EasyBackup 框架引擎的 Java Web 管理器，可以在 Web UI 下完成备份配置管理，备份启动，停止控制。

## EasyBackup 特点

- **开箱即用的内容压缩备份发送框架**。
- **多种可选压缩格式**。支持 `ZIP`，`GZIP`，`TAR` 等格式
- **备份远程发送**。支持将备份发送到邮箱，网络存储等等
- **灵活的调度配置**。支持基于 `CronExpression` 的监控任务调度
- **插件式扩展增强**。可扩展实现数据库备份等等；自定义目标文件来源（`TargetFile`）和备份文件对象（`BackupDir`, `BackupFile`），自定义备份实现（`Backup`），自定义拦截器（`Interceptor`），自定义发送器（`Sender`）。
- **拦截器支持**。支持基于命令和自定义类的前置后置拦截器
- **自定义通知模板**。基于 `freemarker` 的自定义通知模板，支持内置变量使用，更新模板自动重新加载
- **热加载**。支持运行期间热修改 `easybackup.properties`，修改监控配置参数无需重启 EasyBackup
- **多目录支持**。可同时将目标文件备份到多个目录
- **多服务支持**。可同时运行多个备份压缩发送服务
- **运行时信息反馈**。获得备份运行时状况
- **配置简单灵活**。


## EasyBackup 主要组件

- **Backup（备份）**
 可以配置针对 `File`（文件备份）， `USER`（用户自定义备份，如数据库备份等等） 两种类型的备份服务。备份方式支持文件直接备份和压缩备份。

- **Compression（压缩）**
 支持将目标文件压缩为 `ZIP`, `GZIP`， `TAR` 等格式。

- **Sender（发送者）**
 当备份完成，将备份结果发送到 `Email`、 `Web Storage` 等等，一个备份服务可以有多个 Sender。

- **Commander&Class Interceptor（命令执行和拦截器）**
 在备份开始前和完成后，自动执行相应拦截命令和类。

- **Task Schedule（任务调度器）**
 基于 `CronExpression` 的备份任务调度。

![EasyBackup principal components](images/EasyBackup.png)



## BackupConfiguration

- **备份术语**：

 - `targetFile`：待备份的目标文件
 
 - `dir`, `backupDir`：备份文件存储目录
 
 - `file`, `backupFile`：备份文件名


- **BackupConfiguration 对象**

 封装了每一项备份服务所需的相关数据和配置信息（`basic`），完全配置信息（`properties`），调度信息（`task schedure`），拦截器信息（`interceptor`），备份文件信息（`targetFile`, `backupDir`, `backupFile`），压缩信息（`compress`）和发送者（`senders`）。

 ![BackupConfiguration](images/BackupConfiguration.png)

- **BackupJob**
 
 EasyBackup 会根据 `BackupConfiguration` 对象，创建监控 Job。

 ![EasyBackup Job](images/BackupConfigurationJob.png)

## Properties

`easybackup.properties` 是 EasyBackup 的核心配置文件，配置了内容备份所需的所有信息。

### 配置结构

![EasyBackup Properties](images/EasyBackupProperties.png)

### - **全局配置**（作为监控服务配置的全局默认值，可选）

```properties
# Backup ON or OFF, default is ON
# 备份服务默认是否打开，默认为 ON
easybackup.enable=ON

# Backup trigger Cron-Expressions
# 备份服务的默认调度 Cron-Expressions； 默认为 '0 30 * * * ?'
easybackup.cronexpression=0 30 * * * ?

# Directory to store the backup file
# You can specify more than one, separated by a ;
# 文件备份目录，多个备份目录使用 ; 分隔
easybackup.dir=/user/backup/
# backup file name under 'easybackup.backup.dir' directory
# If not specified, the automatically generated file name is 'targetFileName-yyyyMMddHHmmssS[.zip|tar|tar.gz]'
# you can use variable: ${targetFileName}, ${name}, ${type}, ${value}, ${backuptime}, ${backupConfiguration.XXX}
# 在文件备份目录下的备份的文件名
# 默认为 'targetFileName-yyyyMMddHHmmssS[.zip|tar|tar.gz]'
# 可以使用以下变量：${targetFileName}, ${name}, ${type}, ${value}, ${backuptime?string("yyyyMMddHHmmssS")}, ${backupConfiguration.XXX}
easybackup.file=

# Whether backup compression, default is OFF
# 默认是否使用压缩备份，默认为 OFF
easybackup.compress=OFF
# Compression format: TAR(*.tar), ZIP(*.zip), GZIP(*.tar.gz), default is ZIP
# 压缩备份使用的压缩方式，默认为 ZIP
easybackup.compressType=ZIP
# Comprssion encoding
# 压缩文件使用的编码格式
easybackup.compressEncoding=

# Whether delete targetFile after backup complete, default is OFF
# 备份完成后，是否删除已备份的目标文件，默认为 OFF
easybackup.deleteTargetFile=OFF

# Receive Backup file mail address
# You can specify more than one, separated by a ;
# 接收备份信息的邮箱列表，多个邮箱使用 ; 分隔
easybackup.mail.receiver=yourmail@domain.com;youmail2@domai2.org

# Send Mail Account Config
# 邮箱发送者账户配置
# Send Mail Account
easybackup.mail.sender=sendermail@domain.com
# Send Mail password
# 邮箱发送者密码
easybackup.mail.sender.passowrd=mailpassword
# Send Mail SMTP host
# 邮箱发送者 host
easybackup.mail.sender.host=smtp.163.com
# Send Mail SMTP port; default is 25
# 邮箱发送者 host 端口
easybackup.mail.sender.port=25
# Send Mail Whether use SSL; default is false
# 是否使用了 SSL 协议
easybackup.mail.sender.ssl=false
# Send Mail title
# you can use variable: ${targetFileName}, ${name}, ${type}, ${value}, ${backuptime?datetime}, ${backupConfiguration.XXX}
# 邮件发送标题
# 可以使用以下变量：${targetFileName}, ${name}, ${type}, ${value}, ${backuptime?datetime}, ${backupConfiguration.XXX}
easybackup.mail.sender.title=Backup ${targetFileName}- EasyBackup
# The send mail content freemarker template in template directory, default is 'mail.tpl'
# template 目录下的邮件发送模板，默认为 mail.tpl
easybackup.mail.sender.template=mail.tpl

# Auto delete when send email complete ON or OFF, default is OFF
# 备份发送到邮箱后，是否删除备份文件，默认为 OFF
easybackup.mail.deleteBackup=OFF

# Execute Command when backup before or after
# You can specify more than one, separated by a ;
# 备份开始前或完成后执行的前置和后置命令脚本，多个命令脚本使用 ; 分隔
easybackup.cmd.before=
easybackup.cmd.after=

# Execute Interceptor when backup before or after
# You can specify more than one, separated by a ;
# 备份开始前或完成后执行的前置和后置处理类，多个类完全限定名使用 ; 分隔
easybackup.beforeClass=
easybackup.afterClass=
```

### **备份服务配置**（可以覆盖全局配置的默认值）
- **file** 文件备份服务配置
- **user** 自定义备份服务配置

`NAME` 是自定义的监控服务名称，每个监控服务由一组相同 `NAME` 的配置项组成。


```properties
######################## Backup Service configuration
easybackup.[file|user].NAME=value
easybackup.[file|user].NAME.enable=ON | OFF
easybackup.[file|user].NAME.cronexpression=0/10 * * * * ?

easybackup.[file|user].NAME.dir=/user/backup/;/user/backup2
easybackup.[file|user].NAME.file=
easybackup.[file|user].NAME.compress=ON | OFF
easybackup.[file|user].NAME.compressType=ZIP | GZIP | TAR
easybackup.[file|user].NAME.compressEncoding=
easybackup.[file|user].NAME.deleteTargetFile=ON | OFF

easybackup.user.NAME.targetFileClass=package.YourTargetFile
easybackup.user.NAME.dirClass=package.YourBackupDir
easybackup.user.NAME.fileClass=package.YourBackupFile
easybackup.user.NAME.backupClass=package.YourBackup

easybackup.[file|user].NAME.cmd.before=/user/backupBefore.sh;/user/backupBefore2.sh
easybackup.[file|user].NAME.cmd.after=/user/backcAfter.sh;/user/backcAfter2.sh
easybackup.[file|user].NAME.beforeClass=package.BackupBefore;package.BackupBefore2
easybackup.[file|user].NAME.afterClass=package.BackupAfter;package.BackupAfter2

easybackup.[file|user].NAME.mail.receiver=receivermail@domain.com;receivermail1@domain.com

easybackup.[file|user].NAME.mail.sender=sendermail@domain.com
easybackup.[file|user].NAME.mail.sender.passowrd=sendermail_password
easybackup.[file|user].NAME.mail.sender.host=sendermail_host
easybackup.[file|user].NAME.mail.sender.port=sendermail_port
easybackup.[file|user].NAME.mail.sender.ssl=sendermail_ssh
easybackup.[file|user].NAME.mail.sender.title=sendermail_title
easybackup.[file|user].NAME.mail.sender.template=mail.tpl
easybackup.[file|user].NAME.mail.deleteBackup=ON | OFF

easybackup.[file|user].NAME.senders=package.userSenderClass;package.userSenderClass2
```

### User 自定义配置说明

- `targetFileClass`：获取目标文件的自定义处理类，需要实现 `TargetFile` 接口，默认为 `cn.easyproject.easybackup.backup.file.impl.DefaultTargtFile`

- `dirClass`：获取备份目录的自定义处理类，需要实现 `BackupDir` 接口，默认为 `cn.easyproject.easybackup.backup.file.impl.DefaultBackupDir`

- `fileClass`：获取备份文件名的自定义处理类，需要实现 `BackupFile` 接口，默认为 `cn.easyproject.easybackup.backup.file.impl.DefaultBackupFile`

- `backupClass`：完成备份实现的处理类，需要实现 `Badkup` 接口，默认为`cn.easyproject.easybackup.backup.impls.FileBackup`


### 变量使用

- **备份文件名配置**

  默认的备份文件名为 `${targetFileName}-yyyyMMddHHmmssS[.zip|tar|tar.gz]`，备份文件名可以包含以下 `Freemarker` 变量：

 ```
 ${targetFileName}：目标文件名
 ${type}：备份类型（FILE、USER）
 ${name}：备份服务名称
 ${value}：备份服务值
 ${backuptime?string("yyyyMMddHHmmssS")}：备份时间
 ${backupConfiguration.XXX}：备份配置对象属性
 ```

 示例：
  
 ```properties
 easybackup.file=${targetFileName}-${type}-${backuptime?string("yyyyMMddHHmmss")}.bkp
 easybackup.[file|user].NAME.file=file-${backuptime?string("yyyyMMddHHmmss")}.bkp
 ```




- **邮件标题配置**

 邮件标题可以包含以下 `Freemarker` 变量：

 ```
 ${targetFileName}：目标文件名
 ${backupFileName}：备份文件名
 ${type}：备份类型（FILE、USER）
 ${name}：备份服务名称
 ${value}：备份服务值
 ${backuptime?datetime}：备份时间
 ${backupConfiguration.XXX}：备份配置对象属性
 ```

 示例：

 ```properties
 easybackup.mail.sender.title=Backup ${targetFileName}- EasyBackup
 easybackup.[file|user].NAME.sender.title=Backup ${targetFileName}- EasyBackup
 ```



## 完全配置示例
```properties
######################## Global Config(Optional) 全局配置（可选）

# Backup ON or OFF, default is ON
# 备份服务默认是否打开，默认为 ON
easybackup.enable=ON

# Backup trigger Cron-Expressions
# 备份服务的默认调度 Cron-Expressions； 默认为 '0 30 * * * ?'
easybackup.cronexpression=0 30 * * * ?

# Directory to store the backup file
# You can specify more than one, separated by a ;
# 文件备份目录，多个备份目录使用 ; 分隔
easybackup.dir=/user/backup/
# backup file name under 'easybackup.backup.dir' directory
# If not specified, the automatically generated file name is 'targetFileName-yyyyMMddHHmmssS[.zip|tar|tar.gz]'
# you can use variable: ${targetFileName}, ${name}, ${type}, ${value}, ${backuptime}, ${backupConfiguration.XXX}
# 在文件备份目录下的备份的文件名
# 默认为 'targetFileName-yyyyMMddHHmmssS[.zip|tar|tar.gz]'
# 可以使用以下变量：${targetFileName}, ${name}, ${type}, ${value}, ${backuptime?string("yyyyMMddHHmmssS")}, ${backupConfiguration.XXX}
easybackup.file=

# Whether backup compression, default is OFF
# 默认是否使用压缩备份，默认为 OFF
easybackup.compress=OFF
# Compression format: TAR(*.tar), ZIP(*.zip), GZIP(*.tar.gz), default is ZIP
# 压缩备份使用的压缩方式，默认为 ZIP
easybackup.compressType=ZIP
# Comprssion encoding
# 压缩文件使用的编码格式
easybackup.compressEncoding=

# Whether delete targetFile after backup complete, default is OFF
# 备份完成后，是否删除已备份的目标文件，默认为 OFF
easybackup.deleteTargetFile=OFF

# Receive Backup file mail address
# You can specify more than one, separated by a ;
# 接收备份信息的邮箱列表，多个邮箱使用 ; 分隔
easybackup.mail.receiver=yourmail@domain.com;youmail2@domai2.org

# Send Mail Account Config
# 邮箱发送者账户配置
# Send Mail Account
easybackup.mail.sender=sendermail@domain.com
# Send Mail password
# 邮箱发送者密码
easybackup.mail.sender.passowrd=mailpassword
# Send Mail SMTP host
# 邮箱发送者 host
easybackup.mail.sender.host=smtp.163.com
# Send Mail SMTP port; default is 25
# 邮箱发送者 host 端口
easybackup.mail.sender.port=25
# Send Mail Whether use SSL; default is false
# 是否使用了 SSL 协议
easybackup.mail.sender.ssl=false
# Send Mail title
# you can use variable: ${targetFileName}, ${name}, ${type}, ${value}, ${backuptime?datetime}, ${backupConfiguration.XXX}
# 邮件发送标题
# 可以使用以下变量：${targetFileName}, ${name}, ${type}, ${value}, ${backuptime?datetime}, ${backupConfiguration.XXX}
easybackup.mail.sender.title=Backup ${targetFileName}- EasyBackup
# The send mail content freemarker template in template directory, default is 'mail.tpl'
# template 目录下的邮件发送模板，默认为 mail.tpl
easybackup.mail.sender.template=mail.tpl

# Auto delete when send email complete ON or OFF, default is OFF
# 备份发送到邮箱后，是否删除备份文件，默认为 OFF
easybackup.mail.deleteBackup=OFF

# Execute Command when backup before or after
# You can specify more than one, separated by a ;
# 备份开始前或完成后执行的前置和后置命令脚本，多个命令脚本使用 ; 分隔
easybackup.cmd.before=
easybackup.cmd.after=

# Execute Interceptor when backup before or after
# You can specify more than one, separated by a ;
# 备份开始前或完成后执行的前置和后置处理类，多个类完全限定名使用 ; 分隔
easybackup.beforeClass=
easybackup.afterClass=


########################  Backup Service Configuration 监控配置

############ File Backup(can override global config)
## format: 
## easybackup.file.NAME=value
## easybackup.file.NAME.enable=ON | OFF
## easybackup.file.NAME.cronexpression=0/10 * * * * ?
## easybackup.file.NAME.dir=/user/backup/;/user/backup2
## easybackup.file.NAME.file=
## easybackup.file.NAME.compress=ON | OFF
## easybackup.file.NAME.compressType=ZIP | GZIP | TAR
## easybackup.file.NAME.compressEncoding=
## easybackup.file.NAME.deleteTargetFile=ON | OFF
## easybackup.file.NAME.cmd.before=/user/backupBefore.sh;/user/backupBefore2.sh
## easybackup.file.NAME.cmd.after=/user/backcAfter.sh;/user/backcAfter2.sh
## easybackup.file.NAME.beforeClass=package.BackupBefore;package.BackupBefore2
## easybackup.file.NAME.afterClass=package.BackupAfter;package.BackupAfter2
## easybackup.file.NAME.mail.receiver=receivermail@domain.com;receivermail1@domain.com
## easybackup.file.NAME.mail.sender=sendermail@domain.com
## easybackup.file.NAME.mail.sender.passowrd=sendermail_password
## easybackup.file.NAME.mail.sender.host=sendermail_host
## easybackup.file.NAME.mail.sender.port=sendermail_port
## easybackup.file.NAME.mail.sender.ssl=sendermail_ssh
## easybackup.file.NAME.mail.sender.title=sendermail_title
## easybackup.file.NAME.mail.sender.template=mail.tpl
## easybackup.file.NAME.mail.deleteBackup=ON | OFF
## easybackup.file.NAME.senders=package.userSenderClass;package.userSenderClass2



# Example:
easybackup.file.LOG=D:/log
easybackup.file.LOG.enable=ON
easybackup.file.LOG.dir=D:/backup/logfile/;E:/backup/logfile
easybackup.file.LOG.cronexpression=0 30 * * * ?
easybackup.file.LOG.compress=ON
easybackup.file.LOG.compressType=ZIP
#easybackup.file.LOG.mail.receiver=mail@domain.com


############ Port Monitor(can override global config)
## format: 
## easybackup.user.NAME=value
## easybackup.user.NAME.enable=ON | OFF
## easybackup.user.NAME.cronexpression=0/10 * * * * ?
## easybackup.user.NAME.dir=/user/backup/;/user/backup2
## easybackup.user.NAME.file=
## easybackup.user.NAME.compress=ON | OFF
## easybackup.user.NAME.compressType=ZIP | GZIP | TAR
## easybackup.user.NAME.compressEncoding=
## easybackup.user.NAME.deleteTargetFile=ON | OFF
## easybackup.user.NAME.targetFileClass=package.YourTargetFile
## easybackup.user.NAME.dirClass=package.YourBackupDir
## easybackup.user.NAME.fileClass=package.YourBackupFile
## easybackup.user.NAME.backupClass=package.YourBackup
## easybackup.user.NAME.cmd.before=/user/backupBefore.sh;/user/backupBefore2.sh
## easybackup.user.NAME.cmd.after=/user/backcAfter.sh;/user/backcAfter2.sh
## easybackup.user.NAME.beforeClass=package.BackupBefore;package.BackupBefore2
## easybackup.user.NAME.afterClass=package.BackupAfter;package.BackupAfter2
## easybackup.user.NAME.mail.receiver=receivermail@domain.com;receivermail1@domain.com
## easybackup.user.NAME.mail.sender=sendermail@domain.com
## easybackup.user.NAME.mail.sender.passowrd=sendermail_password
## easybackup.user.NAME.mail.sender.host=sendermail_host
## easybackup.user.NAME.mail.sender.port=sendermail_port
## easybackup.user.NAME.mail.sender.ssl=sendermail_ssh
## easybackup.user.NAME.mail.sender.title=sendermail_title
## easybackup.user.NAME.mail.sender.template=mail.tpl
## easybackup.user.NAME.mail.deleteBackup=ON | OFF
## easybackup.user.NAME.senders=package.userSenderClass;package.userSenderClass2

# Example:
easybackup.user.MySQL=mysqldump -uroot -proot demoDB 
easybackup.user.MySQL.targetFileClass=cn.easyproject.easybackup.mysql.MySQLTargetFile
easybackup.user.MySQL.dir=D:/backup/;E:/backupdb
# default targetFileName is 'demoDB-yyyyMMddHHmmssS.sql'
# you can use variable: ${targetFileName}, ${name}, ${type}, ${value}, ${backuptime?string("yyyyMMddHHmmssS")}, ${backupConfiguration.XXX}
easybackup.user.MySQL.file= demodb-${backuptime?string("yyyyMMddHHmmss")}-backup.sql
easybackup.user.MySQL.compress=ON
easybackup.user.MySQL.compressType=ZIP
easybackup.user.MySQL.cronexpression=0 0 2 * * ?
#easybackup.user.MySQL.mail.receiver=mail@domain.com
```



## 邮件模板配置
EasyBackup 使用了 `freemarker` 模板技术进行邮件内容渲染，模板必须存放在 `template` 目录下，默认使用 `mail.tpl` 模板。


![mail.tpl](images/mail.png)


### 自定义模板配置

您可以直接修改模板内容，或者编写您自己的邮件发送模板。

```properties
### Global configuration
easybackup.mail.sender.template=yourmail.tpl

### Monitor Service configuration
easybackup.[file|user].NAME.mail.sender.template=yourmail.tpl
```

### 模板内置变量
```
${targetFileName}：备份的模板文件名
${type}：备份类型（FILE、USER）
${name}：备份服务名称
${value}：备份服务值
${backuptime}：备份时间
${backupConfiguration.XXX}：备份配置对象属性
```


## EasBackup Application 

**EasBackup Application** 是开箱即用的 EasBackup 备份应用。按需配置您的备份服务，启动即可。


EasyBackup Application 已经包含了当前官方的而所有插件。如果您开发了新的插件，可以将插件的内容直接添加到 `easybackup-X.X.X-RELEASE-APPLICATION.jar` 中。  

**内置插件**：

- `easybackup-mysql-plugin-3.2.0-RELEASE.jar`: **MySQL 数据库备份插件**

### 使用步骤
 
1. **配置备份服务**

 参考 `easybackup.properties` 中的示例，配置您的备份服务。

2. **运行 EasyBackup**

 - **Windows**(Sometimes you must '`Run as Administrator`')
 
   ```
   Start:  startup.bat
   Stop:   shutdown.bat
   ```

 - **Linux** 
 
   ```
   Start:  ./startup.sh
   Stop:   ./shutdown.sh
   ```

3. **可选配置**
  1. **邮件模板 Mail template**  
  
     修改 '`template/mail.tpl`' 定制您的邮件内容。

  2. **日志 Logger**  
  
     配置 `log4j.properties` 日志输出。 



## EasyBackup Engine Framework

EasyBackup 同时是一个免费开源跨平台的 Java 内容备份引擎框架(**EasyBackup Engine Framework**)，提供统一规范的备份配置和核心调度。

### 使用步骤

1. Maven dependency

 ```XML
 <dependency>
     <groupId>cn.easyproject</groupId>
     <artifactId>easybackup</artifactId>
     <version>3.2.0-RELEASE</version>
 </dependency>
 ```

2. 自定义接口实现

3. 配置 `easybackup.properties`

4. 启动  
  ```JAVA
  public static void main(String[] args) {
      new EasyBackup().start();
  }
  ```
  
### 自定义接口

EasyBackup 提供了极大的灵活性，扩展以下接口，即可自定义您的备份内容：

- **TargetFile 目标文件来源**（`cn.easyproject.easybackup.backup.file.TargetFile`）
  
  **返回值**: 自定义的 `java.io.File` 目标文件对象

  **配置**: `easybackup.user.NAME.targetFileClass`

  **参数默认值**: `cn.easyproject.easybackup.backup.file.impl.DefaultTargtFile`

- **BackupDir 备份文件目录**（`cn.easyproject.easybackup.backup.file.BackupDir`）
  
  **返回值**: 自定义的 `java.io.File` 备份目录对象

  **配置**: `easybackup.user.NAME.dirClass`

  **参数默认值**: `cn.easyproject.easybackup.backup.file.impl.DefaultBackupDir`

- **BackupFile 备份文件名称**（`cn.easyproject.easybackup.backup.file.BackupFile`）
  
  **返回值**: 自定义的 `String` 备份文件名

  **配置**: `easybackup.user.NAME.fileClass`

  **参数默认值**: `cn.easyproject.easybackup.backup.file.impl.DefaultBackupFile`

- **Backup 备份实现**（`cn.easyproject.easybackup.backup.impls.Backup`）
  
  **返回值**: 根据传入备份文件信息，自定义备份处理实现，返回是否备份成功

  **配置**: `easybackup.user.NAME.backupClass`

  **参数默认值**: `cn.easyproject.easybackup.backup.impls.FileBackup`

- **BackupBefore 前置拦截器**（`cn.easyproject.easybackup.backup.interceptor.BackupBefore`）

  **返回值**: 返回是否继续备份

  **配置**: `easybackup.user.NAME.beforeClass`

  **参数默认值**: 

- **BackupAfter 后置拦截器**（`cn.easyproject.easybackup.backup.interceptor.BackupAfter`）

  **返回值**: 

  **配置**: `easybackup.user.NAME.afterClass`

  **参数默认值**: 

- **Sender 发送器**（`cn.easyproject.easybackup.sender.Sender`）

  **返回值**: 

  **配置**: `easybackup.[file|user].NAME.senders`

  **参数默认值**: 


### 自定义配置文件和 freemarker 配置对象
为了提供更多灵活性，EasyBackup 允许在启动备份服务前自定义**配置文件对象 Properties File**（`easybackup.properties`） 和邮件发送时的 **freemarker 配置对象**（`Configuration`）。

```JAVA
// 自定义配置文件对象
EasyBackup.setPropertiesFile(java.io.File propertiesFile);

// 自定义邮件发送的 freemarker 配置对象
MailSender.setFreemarkerConfiguration(freemarker.template.Configuration configuration);
```

示例：

```JAVA
// Custom EasyBackup initialization Parameter

// 自定义配置文件对象
Resource res = new ServletContextResource(sce.getServletContext(), "/easybackup.properties"); 
try {
    // Properties File 
    EasyBackup.setPropertiesFile(res.getFile());
} catch (IOException e) {
    e.printStackTrace();
}

// 自定义邮件发送的 freemarker 配置对象
Configuration cfg= new Configuration(Configuration.VERSION_2_3_23);
cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
cfg.setDefaultEncoding("UTF-8");
cfg.setServletContextForTemplateLoading(sce.getServletContext(), "/template");
// MailSender Configuration
MailSender.setFreemarkerConfiguration(cfg);
```

### 运行时信息获取

`cn.easyproject.easybackup.BackupRuntime` 提供了备份运行时的信息。

```
# 备份控制
start()：启动
stop()：停止

# 启动后的运行状态信息
started: 是否启动
allBackupsOnStartup：所有配置的服务
errorJobBackupsOnStartup：任务启动失败的服务
runningBackupsOnStartup：正在运行的服务

# 配置信息
getBackupNames()：所有配置的服务名称
getBackupsConfigurations()：所有备份服务配置对象
getEnableBackupsConfigurations()：所有设为启用（enable=ON）的备份服务配置对象
getGlobalBackupsConfiguration()：全局备份配置对象
getProperties()：Properties对象
getPropertiesFile()：Properties File 对象
```



## EasyBackup Web Manger

[EasyBackup Web Manger](https://github.com/ushelp/EasyBackup-Web "移步 EasyBackup Web Manger") 是一个基于 EasyBackup 框架引擎的 Java Web 管理器，可以在 Web UI 下完成备份配置管理，备份启动，停止控制。

![EasyBackup web manager](images/dashboard_zh_CN.png)


## End

[官方主页](http://www.easyproject.cn/easybackup/zh-cn/index.jsp '官方主页')

[留言评论](http://www.easyproject.cn/easybackup/zh-cn/index.jsp#donation '留言评论')

如果您有更好意见，建议或想法，请联系我。

Email：<inthinkcolor@gmail.com>

[http://www.easyproject.cn](http://www.easyproject.cn "EasyProject Home")


**支付宝钱包扫一扫捐助：**

我们相信，每个人的点滴贡献，都将是推动产生更多、更好免费开源产品的一大步。

**感谢慷慨捐助，以支持服务器运行和鼓励更多社区成员。**

<img alt="支付宝钱包扫一扫捐助" src="http://www.easyproject.cn/images/s.png"  title="支付宝钱包扫一扫捐助"  height="256" width="256"></img>

