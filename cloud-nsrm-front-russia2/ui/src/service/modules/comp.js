/**
 * 竞价管理
 */
import http from '@/utils/axios/http'

// 采购商端
export const buyer = {
  // 竞价管理
  CM: {
    // 列表查询
    listPageUrl: '/api-comp/competition/init/listPage',
    // 废弃
    abandon: data =>
      http({
        url: '/api-comp/competition/init/abandon',
        method: 'POST',
        data,
        loading: true
      }),
    // 删除
    delete: id =>
      http({
        url: `/api-comp/competition/init/delete/${id}`,
        method: 'DELETE',
        loading: true
      }),
    // 发起报价
    startComp: data =>
      http({
        url: '/api-comp/compControl/startComp',
        method: 'POST',
        data,
        loading: true
      }),
    // 立即结束报价 / 调整截止时间
    changeCompEndTime: data =>
      http({
        url: '/api-comp/compControl/changeCompEndTime',
        method: 'POST',
        data,
        loading: true
      }),
    // 作废报价
    cancelQuote: data =>
      http({
        url: '/api-comp/businessProposal/cancelOrder',
        method: 'POST',
        data,
        loading: true
      }),
    // 查询竞价单详情
    getProjectInfo: id =>
      http({
        url: `/api-comp/competition/init/getProjectInfo/${id}`,
        method: 'GET',
        loading: true
      }),
    // 获取流程节点启用信息
    getCompProcessConfig: params =>
      http({
        url: '/api-comp/compProcessConfig/get',
        method: 'GET',
        params,
        loading: true
      }),
    // 获取流程节点完成信息
    getProcessNodeById: params =>
      http({
        url: '/api-comp/processNode/listByCompetitionId',
        method: 'GET',
        params,
        loading: true
      }),
    // 暂存-提交 项目信息
    tempSaveOrSubmitProjectInfo: data =>
      http({
        url: '/api-comp/competition/init/tempSaveOrSubmitProjectInfo',
        method: 'POST',
        data,
        loading: true
      }),
    // 暂存-提交 需求信息
    tempSaveOrSubmitRequireInfo: data =>
      http({
        url: '/api-comp/competition/init/tempSaveOrSubmitRequireInfo',
        method: 'POST',
        data,
        loading: true
      }),
    // 获取流程节点完成信息
    getRequireInfo: id =>
      http({
        url: `/api-comp/competition/init/getRequireInfo/${id}`,
        method: 'GET',
        loading: true
      }),
    // 查询邀请供应商数据
    getInviteSupplier: id =>
      http({
        url: `/api-comp/competition/init/getInviteSupplier/${id}`,
        method: 'GET',
        loading: true
      }),
    // 保存邀请供应商数据
    tempSaveOrSubmitInviteSupplier: data =>
      http({
        url: '/api-comp/competition/init/tempSaveOrSubmitInviteSupplier',
        method: 'POST',
        data,
        loading: true
      }),
    // 查询供应商报名
    querySignUpInfosUrl: '/api-comp/signUpManagement/querySignUpInfos',
    // 结束报名
    stopSignUp: data =>
      http({
        url: '/api-comp/signUpManagement/changeSignUpEndTime',
        method: 'POST',
        data,
        loading: true
      }),
    // 报价控制 - 查询供应商投标详情
    queryPriceControlInfo: id =>
      http({
        url: `/api-comp/compControl/queryInfos/${id}`,
        method: 'GET',
        loading: true
      }),
    // 查询商务标评标列表
    queryBusinessOrder: id =>
      http({
        url: `/api-comp/businessProposal/queryOrders/${id}`,
        method: 'GET',
        loading: true
      }),
    // 商务标管理 - 查询报价详情
    queryOrderDetails: params =>
      http({
        url: '/api-comp/businessProposal/queryOrderDetails',
        method: 'GET',
        params,
        loading: true
      }),
    // 获取具体物料的竞价比价信息
    queryHallInfoDetails: params =>
      http({
        url: '/api-comp/compHall/getHallInfoDetails',
        method: 'GET',
        params,
        loading: true
      }),
    // 评选列表
    queryEvaluationPageUrl: '/api-comp/evaluation/queryEvaluationPage',
    // 智能评选
    intelligentEvaluation: id =>
      http({
        url: `/api-comp/evaluation/intelligentEvaluation/${id}`,
        method: 'POST',
        loading: true
      }),
    // 提交中标数量修改
    changeQuotaQuantity: data =>
      http({
        url: '/api-comp/evaluation/changeQuotaQuantity',
        method: 'POST',
        data,
        loading: true
      }),
    // 提交中标数量修改
    createPricingApproval: id =>
      http({
        url: `/api-comp/evaluation/createPricingApproval/${id}`,
        method: 'POST',
        loading: true
      }),
    // 复制单据，用于测试
    copyComp: id =>
      http({
        url: `/api-comp/competition/init/copyComp/${id}`,
        method: 'POST',
        loading: true
      })
  }
}

// 供应商端
export const supplier = {
  // 竞价项目
  CP: {
    // 列表查询
    listPageUrl: '/api-comp/supplierCooperate/queryComp',
    // 暂存/提交 报名
    tempSaveOrSubmitSignUp: data =>
      http({
        url: '/api-comp/signUpManagement/tempSaveOrSubmitSignUp',
        method: 'POST',
        data,
        loading: true
      }),
    // 查询报价明细
    getOrderDetails: data =>
      http({
        url: '/api-comp/supplierCooperate/getComp/orderDetails',
        method: 'POST',
        data,
        loading: true
      }),
    // 提交报价
    submitOrder: data =>
      http({
        url: '/api-comp/supplierCooperate/submitOrder',
        method: 'POST',
        data,
        loading: true
      }),
    // 查询项目信息
    queryProjectInfo: id =>
      http({
        url: `/api-comp/supplierCooperate/getComp/projectInfo/${id}`,
        method: 'GET',
        loading: true
      }),
    // 查询项目需求
    queryRequireInfo: id =>
      http({
        url: `/api-comp/supplierCooperate/getComp/requireInfo/${id}`,
        method: 'GET',
        loading: true
      }),
    // 查询报名信息
    querySignUpInfo: id =>
      http({
        url: `/api-comp/supplierCooperate/getComp/signUpInfo/${id}`,
        method: 'GET',
        loading: true
      }),
    // 查询报价明细
    queryOrderDetails: data =>
      http({
        url: '/api-comp/supplierCooperate/getComp/orderDetails',
        method: 'POST',
        data,
        loading: true
      })
  }
}

// 共用模块
export const share = {
  // 采购商/供应商端  查询报名资料详情
  querySignUpDetailInfo: params =>
    http({
      url: '/api-comp/signUpManagement/querySignUpDetailInfo',
      method: 'GET',
      params,
      loading: true
    }),
  // 采购商/供应商端  查询竞价大厅
  queryHallInfo: id =>
    http({
      url: `/api-sou/vendor/comp/hall/getHallInfo/${id}`,
      method: 'GET',
      loading: true
    })
}
