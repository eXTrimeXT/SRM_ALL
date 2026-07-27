#!/bin/bash
#
ansible_enable=0
product=bpmp
env=$1
ns=$2

dir_name=`dirname $0`
cd $dir_name
lang=$(basename $(pwd))
if [ ${ansible_enable} == "0" ];then
modules=$(ls ${product}-${lang}-common/values-* | awk -F '-' '{print $4}'|awk -F '.' {'print $1}')
else
modules=$(ls ${product}-${lang}-common/values-*-ansible.* |awk -F '-' '{print $4}'|awk -F '.' {'print $1}')
fi
fullpath=`pwd`
echo env: $env lang:$lang dir_name:$dir_name fullpath:$fullpath modules:$modules

if [ -z $env ] || [ -z $ns ]; then
  echo env can not empty
  echo example: install.sh env ns
  echo install.sh dev ns-dev
  echo install.sh sit ns-sit
  echo install.sh uat ns-ust
  echo install.sh pet ns-pet
  exit;
fi
cd ${fullpath}

rm -rf ./*.tgz
echo =========${#modules[@]}
sh ${fullpath}/helm_deploy_single.sh ${product}-${lang}-common ${env} package $ns
for module in  ${modules[@]};
do
  echo ============deploy ${module} ${env}
  sh ${fullpath}/helm_deploy_single.sh ${module} ${env} uninstall $ns
  sh ${fullpath}/helm_deploy_single.sh ${module} ${env} install  $ns
done