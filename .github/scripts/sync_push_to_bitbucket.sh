# 工作目录在项目顶级
# 必须的环境变量： BITBUCKET_TOKEN

echo "https://x-token-auth:$BITBUCKET_TOKEN@bitbucket.org/" > .credential
git config --local credential.helper "store --file=.credential"
git remote add bitbucket https://bitbucket.org/niceleeee/bilibilidown.git
branch_name=`git rev-parse --abbrev-ref HEAD`
git fetch --unshallow origin "$branch_name"
git push -f bitbucket "$branch_name"
rm -f .credential