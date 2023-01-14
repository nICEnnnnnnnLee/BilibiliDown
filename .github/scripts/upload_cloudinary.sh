# 工作目录在项目顶级
# 必须的环境变量： CLOUDINARY_API_KEY CLOUDINARY_API_SECRET ZIP_FILE_NAME SHA1_FILE_NAME

timeStamp=`date '+%s'`
        
param2sign="public_id=bili/$ZIP_FILE_NAME&timestamp=$timeStamp&upload_preset=usf6ttao$CLOUDINARY_API_SECRET"
sig=$(echo -n $param2sign|sha1sum| cut -d' ' -f1)
curl https://api.cloudinary.com/v1_1/dcrcvyjzu/raw/upload \
    -F "file=@$ZIP_FILE_NAME" \
    -F "api_key=$CLOUDINARY_API_KEY" \
    -F "public_id=bili/$ZIP_FILE_NAME" \
    -F "timestamp=$timeStamp" \
    -F "upload_preset=usf6ttao" \
    -F "signature=$sig"
    
param2sign="public_id=bili/$SHA1_FILE_NAME&timestamp=$timeStamp&upload_preset=usf6ttao$CLOUDINARY_API_SECRET"
sig=$(echo -n $param2sign|sha1sum| cut -d' ' -f1)
curl https://api.cloudinary.com/v1_1/dcrcvyjzu/raw/upload \
    -F "file=@$SHA1_FILE_NAME" \
    -F "api_key=$CLOUDINARY_API_KEY" \
    -F "public_id=bili/$SHA1_FILE_NAME" \
    -F "timestamp=$timeStamp" \
    -F "upload_preset=usf6ttao" \
    -F "signature=$sig"     