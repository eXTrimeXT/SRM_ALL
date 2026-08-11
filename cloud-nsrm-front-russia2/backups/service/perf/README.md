# api-perf 绩效模块API

> @author 伟龙


- 根据后端服务 Controller 重构的api接口目录
- 按照这个架构，整个api请求路径清晰，最大的优点是编辑器可以识别import引用关系，可以直接点击跳转！
- 这里做了拆分，区分供应商和采购商api
- 建议最长层级4级到访问具体方法
- 在js调用也很方便，直接引入最低层级文件即可 `import main from '@/service/modules/perf/buyer/main'`

## buyer.js
采购商接口
目录：/perf/buyer
apiUrl: $api.perf.buyer.main.perf.buyer

## vendor.js
供应商接口
目录：/perf/vendor
apiUrl: $api.perf.buyer.main.perf.vendor
