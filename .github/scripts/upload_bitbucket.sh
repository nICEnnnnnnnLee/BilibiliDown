# 工作目录在项目顶级
# 必须的环境变量： VERSION_NUMBER BITBUCKET_TOKEN

curl "https://api.bitbucket.org/2.0/repositories/niceleeee/BilibiliDown/downloads" \
    -F "files=@BilibiliDown.v$VERSION_NUMBER.release.zip" \
    -H "Authorization: Bearer $BITBUCKET_TOKEN"
    
curl "https://api.bitbucket.org/2.0/repositories/niceleeee/BilibiliDown/downloads" \
    -F "files=@BilibiliDown.v$VERSION_NUMBER.release.zip.sha1" \
    -H "Authorization: Bearer $BITBUCKET_TOKEN"       