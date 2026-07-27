## 目的

 本文档主要用于描述美的SRM云的技术选型、大致功能、开发规范，为前端开发人员和测试人员提供文档依据;

## 目标

 实现SRM的系统功能实现f
 1、基础数据配置
 2、权限管理
 3、菜单管理
 4、组织管理
 5、SRM其他相关的功能
 6、路由配置注意事项 （路由在权限云平台那边配置）
   fdKey 对应route下面的name值
   fdFrontLink  对应route 下面的路由路径
   fdFrontIcon  对应的是icon 的class 阿里图标的 class值
   fdName  对应配置route的mate.title值

## 技术选型

- vue(v2.7) + vue-element + axios + vue-router + vuex 技术栈

## node版本要求 16版本

## 功能文件夹解析
  src/modules 产品标准功能
  src/modulesCar 汽车包
  src/modulesQa 品质云相关功能,主要供应商使用
  src/modulesCus 二开文件夹

### Getting started

```bash
# clone the project

# enter the project directory
cd ui

# install dependency
npm install

# develop
npm run dev
```

This will automatically open <http://localhost:8080>

### 命令说明

```bash
# 本地运行
npm run dev  # 全量运行
npm run buyer  #运行 buyer 采购商
npm run supplier  #运行 supplier 供应商


# 打包部署命令
# 全量打包
npm run build:sit // sit dev打包
npm run build:uat // uat打包
npm run build:sit // prod打包

# 单独打包采购商和公共模块
npm run build:buyer:sit // sit dev打包
npm run build:buyer:uat // uat打包
npm run build:buyer:prod // prod打包

# 单独打包供应商和公共模块
npm run build:supplier:sit // sit dev打包
npm run build:supplier:uat // uat打包
npm run build:supplier:prod // prod打包

```

## Advanced

```bash
#  预览发布环境效果
npm run preview

# 预览发布环境效果+静态资源分析
npm run preview -- --report

# 代码格式检查
npm run lint

# 代码格式检查和自动修复
npm run lint -- --fix
```

## meiql项目二开
  - 路径：ui\public\custom-package
  - 项目定制开启：ui\src\main.js 设置 enable 为true
  ```
    const packages = {
      // 项目二开定制插件包
      'plugins': [
        {
          url: `${packagesHost}/custom-package/index.js`,
          enable: false // 默认关闭，项目上再开启
        }
      ],
      // 行业包
      'industries': []
    }
  ```
  - 开发文档 最新的文档待补充

### 接口适配

 为了方便以后有调用多个系统的接口、迁移功能到别的系统会改变系统接口前缀
 设置了前缀适配的灵活方法
 系统前缀定义在：src/config/ipConfig.js
 调用的时候：

 ```
  import { sysPrefix } from '@/config/ipConfig'
  export function login (params) {
    return http({
      url: sysPrefix() + '/sys/login',
      method: 'post',
      params: params
    })
  }
 ```

## 接口返回code参考

```
  ("0", "操作成功"), // R000
  ("1", "系统异常，请联系系统管理员"), // R001
  ("SRM_COMMON_00002", "会话失效，请重新登录"), // R002
  ("SRM_COMMON_00003", "输入参数有误，请重试"),
  ("SRM_COMMON_00004", "请求资源未找到"),
  ("SRM_COMMON_00005", "不支持的请求方法格式"),
  ("SRM_COMMON_00006", "不支持媒体类型"),
  ("SRM_COMMON_00007", "不接受媒体类型"),
  ("SRM_COMMON_00008", "缺失必要的路径变量"),
  ("SRM_COMMON_00009", "缺失必要的请求参数"),
  ("SRM_COMMON_00010", "请求绑定异常"),
  ("SRM_COMMON_00011", "不支持的参数转换"),
  ("SRM_COMMON_00012", "参数类型匹配有误"),
  ("SRM_COMMON_00013", "不可读的请求参数类型"),
  ("SRM_COMMON_00014", "不可写的请求参数类型"),
  ("SRM_COMMON_00015", "参数校验失败"),
  ("SRM_COMMON_00016", "必要参数未传递"),
  ("SRM_COMMON_00017", "数据绑定失败"),
  ("SRM_COMMON_00018", "未找到相关处理器"),
  ("SRM_COMMON_00019", "调用请求超时"),
  ("SRM_COMMON_00020", "操作失败"),
  ("SRM_COMMON_00021", "上传失败"),
  ("SRM_COMMON_00022", "下载失败"),
  ("SRM_COMMON_00023", "导入失败"),
  ("SRM_COMMON_00024", "导出失败"),
  ("SRM_COMMON_00025", "没有权限"),
  ("SRM_COMMON_00026", "导入模板错误"),
  ("SRM_COMMON_00027", "远程调用失败"),
  ("SRM_COMMON_00028", "账号或密码错误"),
  ("SRM_COMMON_00029", "认证失败"),
  ("SRM_COMMON_00030", "基材价格重复"),
  ("SRM_COMMON_00031", "基材编码不存在"),
  ("SRM_COMMON_00032", "字段类型的公式元素缺失要素id"),
  ("SRM_COMMON_00033", "物料主属性不能为空"),
  ("SRM_COMMON_00034","物料主属性名已存在"),
  ("SRM_COMMON_00035","要素名称已存在"),
  ("SRM_COMMON_00036","状态错误，无法操作"),
  ("SRM_COMMON_00037","物料不存在"),
  ("SRM_COMMON_00038","公式不存在或未生效"),
  ("SRM_COMMON_00080", "第一次登陆,请重置密码"),
  ("SRM_COMMON_00039","密码过期"),
  ("SRM_COMMON_00040","密码已超过30天未修改,请修改密码"),
  ("SRM_COMMON_00041","不能上传空文件");

```

## 私有仓库初始化与使用

```
说明-脚本依赖添加:
  npm install json5
  npm install shelljs

1、nexus创建npm类型hosted类型的仓库
public-srm

2、本地下载前端仓库

3、本地联网执行
  npm install
  作用：生成package-lock.json

4、下载私仓依赖包
  进入 cloud-nsrm-front
  手动创建文件夹：download/downloaded
  mkdir -p download/downloaded

  进入 cloud-nsrm-front/ui目录，执行：
  cd ui
  node npm-download-tgz.js

5、全量发布到远程仓库
  进入 cloud-nsrm-front/ui目录
  修改npm-publish.sh 中的私有仓库地址和账号密码
  执行命令 sh npm-publish.sh

6、完成私有仓库的初始化

7、新增的依赖，可手动下载tgz包，上传到私有仓库中


```
