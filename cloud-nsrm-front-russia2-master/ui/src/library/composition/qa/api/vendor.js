/**
 * @description 供应商 - 质疑澄清 /api-sou/vendor/bargainQa
 * @description 路径：$api.sou.vendor.bargainQa
 * @author donghf3
 */
import http from '@/utils/axios/http'

// 供应商 - 质疑澄清
const vendorQaPath = '/api-sou/vendor/qa'

/**
 * souAnswer 澄清 Controlller
 */
const souAnswerPath = `${vendorQaPath}/souAnswer`
const souAnswer = {
  // 列表查询
  getListPageUrl: souType => `${souAnswerPath}/listPage/${souType}`,
  // 查询详情
  getDetail: (souType, id) =>
    http({
      url: `${souAnswerPath}/getDetail/${souType}/${id}`,
      method: 'GET',
      loading: true
    }),
  // 接受澄清
  vendorAccept: (souType, id) =>
    http({
      url: `${souAnswerPath}/vendorAccept/${souType}/${id}`,
      method: 'POST',
      loading: true
    })
}

/**
 * souQuestion 质疑 Controlller
 */
const souQuestionPath = `${vendorQaPath}/souQuestion`
const souQuestion = {
  // 列表查询
  getListPageUrl: souType => `${souQuestionPath}/listPage/${souType}`,
  // 撤回
  reject: (souType, data) =>
    http({
      url: `${souQuestionPath}/reject/${souType}`,
      method: 'POST',
      data,
      loading: true
    }),
  // 删除
  delete: (souType, params) =>
    http({
      url: `${souQuestionPath}/delete/${souType}`,
      method: 'GET',
      params,
      loading: true
    }),
  // 详情
  getDetail: (souType, params) =>
    http({
      url: `${souQuestionPath}/getBidingQuestionById/${souType}`,
      method: 'GET',
      params,
      loading: true
    }),
  // 保存 save
  save: (souType, data) =>
    http({
      url: `${souQuestionPath}/save/${souType}`,
      method: 'POST',
      data,
      loading: true
    }),
  // 发布 submit
  submit: (souType, data) =>
    http({
      url: `${souQuestionPath}/submit/${souType}`,
      method: 'POST',
      data,
      loading: true
    })
}

// 供应商
const vendorPath = '/api-sou/vendor/qa'

// order 流程 Controlller
const orderPath = `${vendorPath}/souOrder`
const order = {
  // 寻源供应商报价(供应商端) 不做全局刷新
  page: data =>
    http({
      url: `${orderPath}/page`,
      method: 'POST',
      data,
      loading: false
    })
}

// 根据后端不同的 Controlller 区分不同的对象拆分
export default {
  souAnswer,
  souQuestion,
  order
}
