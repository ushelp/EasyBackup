echo 'EasyBackup starting...'
echo 'The log in easybackup.out'
nohup java -jar easybackup-2.2.0-RELEASE-APPLICATION.jar >/dev/null 2>&1 &
echo 'EasyBackup started.'
