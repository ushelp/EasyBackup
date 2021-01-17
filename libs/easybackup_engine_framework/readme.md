# EasyBackup Engine Framework 



## 中文

EasyBackup 同时是一个免费开源跨平台的 Java 内容备份引擎框架(**EasyBackup Engine Framework**)，提供统一规范的备份配置和核心调度。

### 使用步骤

1. Maven dependency

 ```XML
 <dependency>
     <groupId>cn.easyproject</groupId>
     <artifactId>easybackup</artifactId>
     <version>3.3.2-RELEASE</version>
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

`cn.easyproject.easybackupBackupRuntime` 提供了备份运行时的信息。

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



## English

EasyBackup  is also a free open source cross-platform Java content backup engine frame (**EasyBackup Engine Framework**), to provide a unified and standard core configuration and backup scheduling.

### Steps for usage

1. Maven dependency

 ```XML
 <dependency>
     <groupId>cn.easyproject</groupId>
     <artifactId>easybackup</artifactId>
     <version>3.3.2-RELEASE</version>
 </dependency>
 ```

2. Custom interface

3. Configuration `easybackup.properties`

4. Start  
  ```JAVA
  public static void main(String[] args) {
      new EasyBackup().start();
  }
  ```
  
### Custom Interface

EasyBackup provides great flexibility, expandability following interfaces, you can customize your backup content:

- **TargetFile target file source**（`cn.easyproject.easybackup.backup.file.TargetFile`）
  
  **return**: custom `java.io.File` target file

  **configuration**: `easybackup.user.NAME.targetFileClass`

  **default**: `cn.easyproject.easybackup.backup.file.impl.DefaultTargtFile`

- **BackupDir backup directory**（`cn.easyproject.easybackup.backup.file.BackupDir`）
  
  **return**: custom `java.io.File` storage backup file directory

  **configuration**: `easybackup.user.NAME.dirClass`

  **default**: `cn.easyproject.easybackup.backup.file.impl.DefaultBackupDir`

- **BackupFile backup file name**（`cn.easyproject.easybackup.backup.file.BackupFile`）
  
  **return**: custom `String` backup file name

  **configuration**: `easybackup.user.NAME.fileClass`

  **default**: `cn.easyproject.easybackup.backup.file.impl.DefaultBackupFile`

- **Backup backup implement**（`cn.easyproject.easybackup.backup.impls.Backup`）
  
  **return**: According to information passed back up files, custom backup processing implementation, returns whether the backup was successful

  **configuration**: `easybackup.user.NAME.backupClass`

  **default**: `cn.easyproject.easybackup.backup.impls.FileBackup`

- **BackupBefore before interceptor**（`cn.easyproject.easybackup.backup.interceptor.BackupBefore`）

  **return**: Returns whether to continue the backup

  **configuration**: `easybackup.user.NAME.beforeClass`

  **default**: 

- **BackupAfter after interceptor**（`cn.easyproject.easybackup.backup.interceptor.BackupAfter`）

  **return**: 

  **configuration**: `easybackup.user.NAME.afterClass`

  **default**: 

- **Sender**（`cn.easyproject.easybackup.sender.Sender`）

  **return**: 

  **configuration**: `easybackup.[file|user].NAME.senders`

  **default**: 


### Custom Properties File and Freemarker Configuration object
To provide more flexibility, EasyBackup permit before starting backup service ** custom Properties File object** ( `easybackup.properties`) and ** mail to send Freemarker Configuration object** (` Configuration`).


```JAVA
// Custom Properties File
EasyBackup.setPropertiesFile(java.io.File propertiesFile);

// Custom mail to send Freemarker Configuration object
MailSender.setFreemarkerConfiguration(freemarker.template.Configuration configuration);
```

Example:

```JAVA
// Custom EasyBackup initialization Parameter

// Custom Properties File
Resource res = new ServletContextResource(sce.getServletContext(), "/easybackup.properties"); 
try {
    // Properties File 
    EasyBackup.setPropertiesFile(res.getFile());
} catch (IOException e) {
    e.printStackTrace();
}

// Custom mail to send Freemarker Configuration object
Configuration cfg= new Configuration(Configuration.VERSION_2_3_23);
cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
cfg.setDefaultEncoding("UTF-8");
cfg.setServletContextForTemplateLoading(sce.getServletContext(), "/template");
// MailSender Configuration
MailSender.setFreemarkerConfiguration(cfg);
```

### Runtime access to information

`cn.easyproject.easybackupBackupRuntime` providing information backup runtime.

```
# Backup controller
start()
stop()

# Information when started
started: Weather started
allBackupsOnStartup
runningBackupsOnStartup
errorJobBackupsOnStartup

# Configuration information
getBackupNames(): All configuration names
getBackupsConfigurations(): All configuration objects
getEnableBackupsConfigurations():All enable（enable=ON） configuration objects
getGlobalBackupsConfiguration(): Global Configuration object
getProperties(): Properties object
getPropertiesFile(): Properties File object
```



## End

