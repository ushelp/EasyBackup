@echo off 
echo EasyBackup starting...
echo The log in easybackup.out
cd /d %~dp0
java -jar easybackup-application.jar
echo EasyBackup started.