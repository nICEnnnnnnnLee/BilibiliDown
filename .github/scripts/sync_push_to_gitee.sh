# 工作目录在项目顶级
# 必须的环境变量： GITEE_AUTH

echo "https://$GITEE_AUTH@gitee.com/" > .credential
git config --local credential.helper "store --file=.credential"
git remote add gitee https://gitee.com/NiceLeee/BilibiliDown.git
branch_name=`git rev-parse --abbrev-ref HEAD`
git fetch --unshallow origin "$branch_name"
git push -f gitee "$branch_name"
rm -f .credential