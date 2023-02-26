#!/bin/bash

# cd 到脚本所在目录
cd $(dirname $0)
# java -Dfile.encoding=utf-8 -Dhttps.protocols=TLSv1.2 -jar INeedBiliAV.jar
java -Dfile.encoding=utf-8 -Dhttps.protocols=TLSv1.2 -jar launch.jar