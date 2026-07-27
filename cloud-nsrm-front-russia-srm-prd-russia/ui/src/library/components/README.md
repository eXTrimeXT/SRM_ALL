## 组件快速引入说明

```js
// 操作按钮区域 (使用场景：列表页、详情页页内操作按钮)
import MainHeader from "lib@/components/Table/MainHeader";
// 查询条件配置 (使用场景：列表条件查询、其他地方查询)
import FormWrapper from "lib@/components/Table/FormWrapper";
// 使用场景 列表页、其他页面数据显示
import TableView from "lib@/components/Table/TableView";
// 视窗底部按钮区域 使用场景：详情页底部的操作按钮 如暂存提交等
import CToolbar from "lib@/components/c-toolbar";
// 上传 下载
import CUploadFile from "@/library/components/c-upload-file";
import CDownloadLink from "lib@/components/c-download-link";
// 导入 可配置模板下载接口 导入接口 额外条件参数  延迟等
import MImport from "lib@/components/import";
// 自定义导出 额外条件参数  延迟等
import ExportExcel from "lib@/components/export-excel";
// 直接导出 额外条件参数  延迟等
import ExportDirect from "lib@/components/export-direct";
// 快速查询 可额外传参
import QuickSearch from "lib@/components/QuickSearch";
// 组织选择组件
import OrganizationSelector from "lib@/components/organization-selector";
// 动态配置表单项
import BaseForm from "lib@/components/BaseForm";
// 动态配置表格列
import BaseTable from "lib@/components/BaseTable/baseTable";
// 单表追加模式使用组件
import EasyTable from "lib@/components/BaseTable/EasyTable";
// 品类选择器 支持查询层级设置级数的节点数据(只查回第三级节点数据)、品类分工
import CCategorySelect from "lib@/components/c-category-select";
// 品类选择器 联级形式选择方式 接口一次性返回，如果数据量不是很大的情况可以考虑使用
import CategoryCascader from "lib@/components/category-cascader";
// 分页组件
import CPagination from "lib@/components/c-pagination";
// 人员选择器 可选择采购商、供应商
import CPeopleSelector from "@/library/components/c-people-selector";
// tab组件 使用场景是 列表页单据打开的tab形式
import NavTabs from "lib@/components/NavTabs";
// 审批流的提交按钮 使用场景是提交工作流的联调
import WorkflowButton from "lib@/components/workflow-button";
// 省市联级组件 ProviceCity 编辑下使用 RenderAsyncText 查看下使用 具体可搜索本系统招标里面的项目需求里面的交货地点用法
// 省市联级选择组件 对应model 保存的是省和市的编号 如选北京-北京市 得到的值是 ["110000", "110100"]
import ProviceCity from "lib@/components/provice-city";
// 省市联级组件回显 
import RenderAsyncText from "@/library/components/provice-city/renderAsyncText";


```

 

