# EasyBackup Application 



## 中文


**EasBackup Application** 是开箱即用的 EasBackup 备份应用。按需配置您的备份服务，启动即可。


EasyBackup Application 已经包含了当前官方的而所有插件。如果您开发了新的插件，可以将插件的内容直接添加到 `easybackup-X.X.X-RELEASE-APPLICATION.jar` 中。 

### 内置插件：

- `easybackup-mysql-plugin-2.2.0-RELEASE.jar`: **MySQL 数据库备份插件**


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



## English

**EasBackup Application** can run as application. Configure your backup service as needed, can be activated.

EasyBackup Application already contains the current official and all plug-ins. If you develop a new plug-in, plug-in content can be added directly to the `easybackup-X.X.X-RELEASE-APPLICATION.jar` in.

### Plugins:

- `easybackup-mysql-plugin-2.2.0-RELEASE.jar`: **MySQL Database backup plugin**

### Steps for usage
 
1. **Configuration Backup Service**

 `easybackup.properties` the reference sample, configure your backup service.

2. **Run EasyBackup**

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

3. **Optional**
  1. **Mail template**  
  
     edit '`template/mail.tpl`' to custom your mail content.

  2. **Logger**  
  
     configuration `log4j.properties` log out. 



## End

