# cd 到脚本所在目录
cd $(dirname $0)
cd ../..

# 复制整个文件夹
mkdir target-launcher
cp -r src-launcher/. target-launcher

# 删除不需要的java文件
# rm -rf ./target-launcher/nicelee/test

# 获取java文件列表
cd target-launcher
find `pwd` -name "*.java" > ../sources.txt
cd ..

# 编译java
javac -encoding UTF-8 @sources.txt

# 删除所有.java文件
cd target-launcher
find . -name "*.java" |xargs rm -rf {}
cd ..

# 打包
jar cvfe launch.jar nicelee.memory.App -C ./target-launcher .

rm -rf target-launcher
rm -f sources.txt
