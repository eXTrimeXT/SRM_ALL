#!/bin/bash
#
env=$1
ns=$2
ansible_enable=0
pkgName=srm-common

dir_name=`pwd | awk -F "/" '{print $NF}'`
fullpath=`pwd $dir_name`
echo env: $env  dir_name:$dir_name fullpath:$fullpath

#新增$ns判断
if [ -z $env ] || [ -z $ns ]; then
  echo env can not empty
  echo example: install.sh dev ns
  echo install.sh sit ns-sit
  echo install.sh uat ns-uat
  echo install.sh pet ns-pet
  exit;
fi
cd ${fullpath}

rm -rf ./*.tgz
helm -n ${ns} package ${pkgName}
helm -n ${ns} uninstall ${pkgName}-${env}
#新增ansible开关逻辑
if [ ${ansible_enable} == "0" ];then
echo "helm -n ${ns} install ${pkgName}-${env} ${pkgName}-*.tgz  -f  ${fullpath}/${pkgName}/values-${env}.yaml"
helm -n ${ns} install ${pkgName}-${env} ${pkgName}-*.tgz  -f  ${fullpath}/${pkgName}/values-${env}.yaml
else
echo "helm -n ${ns} install ${pkgName}-${env} ${pkgName}-*.tgz  -f  ${fullpath}/${pkgName}/values-ansible.yaml"
helm -n ${ns} install ${pkgName}-${env} ${pkgName}-*.tgz  -f  ${fullpath}/${pkgName}/values-ansible.yaml
fi