#!/bin/bash
targetDir=/e/develop-merge/cloud-nsrm-front/download
publishRestful=127.0.0.1:10082/service/rest/v1/components?repository=public-srm
echo ">>> 文件所在目录：$targetDir <<<"
dir=$(ls -l $targetDir | awk '/.tgz$/ {print $NF}')
cd $targetDir

for file in $dir
do
  echo ">>> $targetDir/$file 上传开始 \n"
  ret=`curl -u 私有仓库账号:私有仓库密码 -X POST "$publishRestful" -H "Accept: application/json" -H "Content-Type: multipart/form-data" -F "npm.asset=@$file;type=application/x-compressed"`
  echo $ret
  echo ">>> $targetDir/$file 上传完成 \n"
done
