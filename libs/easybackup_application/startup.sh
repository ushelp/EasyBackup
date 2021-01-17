echo 'EasyBackup starting...'
echo 'The log in easybackup.out'
nohup java -jar  easybackup-application.jar >/dev/null 2>&1 &
echo 'EasyBackup started.'
