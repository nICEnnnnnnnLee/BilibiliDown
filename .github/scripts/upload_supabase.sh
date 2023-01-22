# 工作目录在项目顶级
# 必须的环境变量： VERSION_NUMBER SUPABASE_ANON_KEY

curl "https://vezfoeoqirnvcqsuiext.supabase.co/storage/v1/object/bili/release/BilibiliDown.v$VERSION_NUMBER.release.zip" \
    -F "file=@BilibiliDown.v$VERSION_NUMBER.release.zip" \
    -F "cacheControl=3600" \
    -H "apikey: $SUPABASE_ANON_KEY" \
    -H "authorization: Bearer $SUPABASE_ANON_KEY"
    
curl "https://vezfoeoqirnvcqsuiext.supabase.co/storage/v1/object/bili/release/BilibiliDown.v$VERSION_NUMBER.release.zip.sha1" \
    -F "file=@BilibiliDown.v$VERSION_NUMBER.release.zip.sha1" \
    -F "cacheControl=3600" \
    -H "apikey: $SUPABASE_ANON_KEY" \
    -H "authorization: Bearer $SUPABASE_ANON_KEY"       