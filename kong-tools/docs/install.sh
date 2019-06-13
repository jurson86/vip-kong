#!/bin/bash
#$1--空配置文件 $2--插件名称
#添加插件配置到kong配置文件
ok=$(sed -n '/^custom_plugins/=' $1)
if [ ! $ok ]; then
  echo "$1 配置添加插件..............." 
  $(sed -i "/^#custom_plugins/a custom_plugins=$2" $1)
else
  echo "$1 配置添加插件..............." 
  $(sed -i "/^custom_plugins/s/$/,$2/" $1)
fi
# 安装插件
#luarocks make
#显示安装信息
#luarocks show kong-plugin-$2
#启动kong
# $(kong restart)
