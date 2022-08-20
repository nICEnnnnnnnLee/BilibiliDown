# cd 到脚本所在目录
cd $(dirname $0)

# 复制整个文件夹
mkdir target
cp -r src/. target

# 删除不需要的java文件
rm -rf ./target/nicelee/test

# 获取java文件列表
cd target
find `pwd` -name "*.java" > ../sources.txt
cd ..

# 获取环境变量,解压lib包
cd libs
find `pwd` -name "*.jar" > ../libs.txt
cat ../libs.txt
cd ../target
for jar in  `cat ../libs.txt`
do
    jclasspath=$jar:$jclasspath
    jar xvf $jar
done
cd ..

# 编译java
javac -cp $jclasspath -encoding UTF-8 @sources.txt

# 删除所有.java文件
cd target
find . -name "*.java" |xargs rm -rf {}
cd ..

# 打包
jar cvfe INeedBiliAV.jar nicelee.ui.FrameMain -C ./target .

rm -rf target
rm -f sources.txt
rm -f libs.txt