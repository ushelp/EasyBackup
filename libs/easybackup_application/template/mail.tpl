<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta http-equiv="content-type" content="text/html;charset=utf-8"/>
<style type="text/css">
body{margin: 0;padding: 0}
span{color: #FF9933; font-weight: bold;}
a{font-weight: bold; color: #666666;}
</style> 
</head>
<body style="margin: 0;padding: 0;">
<div style="background-color: #FF9933; color: #ffffff; padding-left: 10px;line-height:45px; font-size: 20px; font-weight:bold; ">
EasyBackup
</div>

<div style="padding: 10px">
<p>Hello,</p>
<p>Your backup service is complete. </p>
<p>
Name: <span>${name} - [${type}]</span>
</p>
<p>
Target: <span>${value}</span>
</p>
<p>
Backup time: <span>${backuptime?datetime}</span>
</p>
<p>
Please check your E-mail attachments.
</p>
</div>
<div style="background-color: #F4F4F4;padding-left: 10px;color: #777777;line-height:35px;">
EasyMonitor by <a href="www.easyproject.cn">easyproject.cn</a> 
</div>
</body>
</html>
