@echo off 
cd /d %~dp0
echo EasyBackup shutdown...
rem jps -mlvV| findstr easybackup.*.jar |taskkill -F -PID 4468
for /f "tokens=*" %%i in ('jps -mlvV ^| findstr easybackup.*.jar') do set value=%%i 
if "%value%"=="" (goto notrun ) else ( goto shutdown )

:shutdown
rem echo %value%
rem echo %value:~0,5%
set pid=%value:~0,5%
rem echo %pid%
taskkill -F -PID %pid% | findstr x
set value=
set pid=
goto end

:notrun
echo not runing

:end
echo EasyBackup stoped.
pause
