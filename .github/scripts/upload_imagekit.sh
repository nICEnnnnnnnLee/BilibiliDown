# 工作目录在项目顶级
# 必须的环境变量： IMAGEKIT_PRIVATE_KEY ZIP_FILE_NAME SHA1_FILE_NAME

curl -X POST "https://upload.imagekit.io/api/v1/files/upload" \
    -u $IMAGEKIT_PRIVATE_KEY: \
    -F "file=@$ZIP_FILE_NAME;type=application/octet-stream" \
    -F "useUniqueFileName=false" \
    -F "folder=release" \
    -F "fileName=$ZIP_FILE_NAME"
    
curl -X POST "https://upload.imagekit.io/api/v1/files/upload" \
    -u $IMAGEKIT_PRIVATE_KEY: \
    -F "file=@$SHA1_FILE_NAME;type=application/octet-stream" \
    -F "useUniqueFileName=false" \
    -F "folder=release" \
    -F "fileName=$SHA1_FILE_NAME"    