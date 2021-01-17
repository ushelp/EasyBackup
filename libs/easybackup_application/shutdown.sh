echo 'EasyBackup shutdown...'
ps -ef|grep easybackup.*.jar | grep -v grep |awk '{print $2}' | xargs kill -9
echo 'EasyBackup stoped.'
