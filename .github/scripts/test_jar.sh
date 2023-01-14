# 工作目录在项目顶级
# 必须的环境变量： VERSION_NUMBER

version=`java -Dfile.encoding=utf-8 -jar INeedBiliAV.jar -v 2>&1 | tail -n 1`
if [ "$version" != "v$VERSION_NUMBER" ]  ; then
    echo $version
    echo "v$VERSION_NUMBER"
    echo '::warning:: Query version of INeedBiliAV.jar failed...'
    exit 1
fi