# 工作目录在项目顶级
# 必须的环境变量： RAILWAY_AUTH ZIP_FILE_NAME SHA1_FILE_NAME

curl -X POST "https://bili.up.railway.app/upload" \
    --cookie "auth=$RAILWAY_AUTH" \
    -F "file=@$ZIP_FILE_NAME;type=application/octet-stream"  
    
curl -X POST "https://bili.up.railway.app/upload" \
    --cookie "auth=$RAILWAY_AUTH" \
    -F "file=@$SHA1_FILE_NAME;type=application/octet-stream"  