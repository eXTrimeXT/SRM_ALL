import { merge } from 'lodash'
import { meiqlCtrlPj } from './configCus/pjMeiqlConf'
/**
 * meiql 改造功能使用版本控制
 * 使用meiql版本设置 Y | 使用传统vue设置 N
*/
export const meiqlCtrlDefault = {
  bargainQa: 'N', // 项目式询价 质疑澄清 -未准出-关闭
  bidQa: 'N', // 招标管理 质疑澄清 -未准出-关闭
  inquiryManagement: 'N', // 询价管理 -未准出-关闭
  inquiryVendorOrders: 'N', // 简易询价协同 - 询价单 -未准出-关闭
  siteReviewPlanConfirm: 'N', // 计划落实管理
  siteReviewPlan: 'N', // 现场评审计划管理
  priceApproval: 'Y',
  quaOfReview: 'N', // 资质审查
  inviteSupplier: 'Y', // 邀请供应商
  crossOrgImport: 'N', // 跨组织引入
  buyerDeliveryOrder: 'N', // 送货单
  vendorDeliveryOrder: 'N', // 送货单（供应商
  buyerDeliveryNotice: 'N', // 送货通知单
  vendorDeliveryNotice: 'N', // 送货通知单（供应商）
  orderStorage: 'N', // 订单入库
  sampleConfirmed: 'N', // 样品确认单
  vendorSampleConfirmed: 'N', // 样品确认单（供应商）
  materialTrial: 'N', // 物料试用 M2
  vendorMaterialTrial: 'N', // 物料试用（供应商）
  warehousingAndReturnGoods: 'N', // 订入库退货明细
  warehousReturnGoodsVendor: 'N', // 入库退货明细（供应商）
  purchaseApplication: 'N', // 采购申请
  buyerPurchaseOrder: 'N', // 采购订单
  shoppingCart: 'N', // 购物车
  vendorGreenChannel: 'Y', // 绿色通道
  vendorProfile: 'N', // 供应商清单
  companyInfoMaintain: 'Y', // 企业信息
  vendorPurchaseOrder: 'N', // 采购订单协同
  vendorDemotion: 'N', // 供应商升降级
  risk: 'N', // 供应商风险
  agentOnlineInvoice: 'N', // 开票管理
  siteAssessment: 'N', // 现场评审
  purPaymentApply: 'N', // 付款申请单
  buyerCarInfo: 'N', // 车辆信息维护
  vendorCarInfo: 'N', // 车辆信息维护
  categoryRelationship: 'N', // 供应商品类关系
  categoryResponsibility: 'N', // 供应商对接人
  financialInforChanges: 'N', // 财务信息变更
  purInvoiceSupplier: 'N', // 开票协同
  complaintinfo: 'N', // 供应商投诉（供应商端）
  complaintReview: 'N', // 供应商投诉处理（采购商端）
  purchaseDirectory: 'N', // 货源清单（采购商）
  vendorPurchaseDirectory: 'N', // 货源清单（供应商）
  purchaseDirectoryChange: 'N', // 货源变更（采购商）
  vendorPurchaseDirectoryChange: 'N', // 货源变更（供应商）
  vendorInfoChange: 'Y', // 供应商变更
  black: 'N', // 黑名单明细 M4
  demandPoolManagement: 'N', // 需求池
  deliveryAppointment: 'N', // 送货预约（采购商）
  deliveryAppointmentSupplier: 'N', // 送货预约（供应商）
  purchaseCatalogOnOrOff: 'N', // 采购目录上下架（采购商）
  purchaseCatalogOnOrOffSupplier: 'N', // 采购目录上下架（供应商）
  budgetManagementList: 'N', // 预算管理列表
  budgetManagementHistory: 'N', // 预算调整历史记录
  drawingshead: 'N', // 物料图纸(采购商)
  potentialSupplierMaterial: 'N', // 潜在供应商
  cooperationEnded: 'N', // 合作终止
  purchaseCatalog: 'N', // 采购目录
  purInvoice: 'N', // 对账管理（采购商）
  purStatementBillSupplier: 'N', // 对账管理协同（供应商）
  advancePayment: 'N', // 预付款申请
  returnedGoodsNoticeBuyer: 'N', // 退货单（采购商）
  returnedGoodsNoticeSupplier: 'N', // 退货单（供应商）
  purchaseOrderChangeBuyer: 'N', // 采购订单变更（采购商）
  purchaseOrderChangeSupplier: 'N', // 采购订单变更（供应商）
  accessFlowSetting: 'N' // 准入流程配置（Y为新的，N为旧的）
}

export const meiqlCtrl = merge(meiqlCtrlDefault, meiqlCtrlPj)
