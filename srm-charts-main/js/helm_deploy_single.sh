#!/bin/bash

product=srm
ansible_enable=0
cd `dirname $0`
lang=$(basename $(pwd))
module=$1
env=$2
action=$3
ns=$4

fullpath=`pwd`
pkgName=${product}-${lang}-common
charsPrefix=${product}-${lang}-${module}
echo env: ${env} lang:$lang serviceName:${module} action:${action} fullpath:$fullpath

#新增${ns}判断
if [ -z ${env}  ] || [ -z ${module} ] || [ -z ${ns} ]; then
  echo module and env can not empty
  echo example: helm_install_single.sh module env ns
  echo helm_install_single.sh mxbase dev ns-dev
  echo helm_install_single.sh mxbase sit ns-sit
  echo helm_install_single.sh mxbase uat ns-uat
  echo helm_install_single.sh mxbase pet ns-pet
  exit;
fi
cd ${fullpath}
if  [ -z ${action}  ] ||[ ${action} == "package" ]; then
  echo =====package
  rm -rf ./*.tgz
  echo helm -n ${ns} package ${pkgName}
  helm -n ${ns} package ${pkgName}
fi
if  [ -z ${action}  ] ||[ $action == "uninstall" ]; then
  echo =====uninstall
  helm -n ${ns} uninstall ${charsPrefix}-${env}
fi
if  [ -z ${action}  ] ||[ $action == "install" ]; then
  echo =====install

#  if [ ${env} == "pet" ]; then
#    echo 'tolerations:
#  enabled: true
#  keys:
#    - effect: NoSchedule
#      key: pet
#      value: true' >> ${valuesFile}
#  fi
if [ ${ansible_enable} == "0" ];then
valuesFile=${fullpath}/${pkgName}/values-${module}.yaml
echo helm -n ${ns} install ${charsPrefix}-${env} ${pkgName}-*.tgz  -f  ${valuesFile}  --set global.env=${env} --set global.namespace=${ns}
helm -n ${ns} install ${charsPrefix}-${env} ${pkgName}-*.tgz  -f  ${valuesFile}  --set global.env=${env} --set global.namespace=${ns}
else
valuesFile=${fullpath}/${pkgName}/values-${module}-ansible.yaml
echo helm -n ${ns} install ${charsPrefix}-${env} ${pkgName}-*.tgz  -f  ${valuesFile}  --set global.env=${env} --set global.namespace=${ns}
helm -n ${ns} install ${charsPrefix}-${env} ${pkgName}-*.tgz  -f  ${valuesFile}  --set global.env=${env} --set global.namespace=${ns}
fi
fi
