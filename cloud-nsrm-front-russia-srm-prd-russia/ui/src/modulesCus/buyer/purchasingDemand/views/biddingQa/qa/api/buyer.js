/**
 * @description 采购商 - 质疑澄清 /api-sou/buyer/bargainQa
 * @description 路径：$api.sou.buyer.bargainQa
 * @author donghf3
 */
import http from '@/utils/axios/http'

// 采购商 - 询价管理
const buyerPath = '/api-sou/buyer/qa'

/**
 * souAnswer 澄清 Controlller
 */
const souAnswerPath = `${buyerPath}/souAnswer`
const souAnswer = {
  // 列表查询
  getListPageUrl: souType => `${souAnswerPath}/listPage/${souType}`,
  // 删除
  delete: (souType, id) =>
    http({
      url: `${souAnswerPath}/delete/${souType}/${id}`,
      method: 'DELETE',
      loading: true
    }),
  // 撤回
  withdraw: (souType, id) =>
    http({
      url: `${souAnswerPath}/withDraw/${souType}/${id}`,
      method: 'POST',
      loading: true
    }),
  // 发布
  publish: (souType, id) =>
    http({
      url: `${souAnswerPath}/publish/${souType}/${id}`,
      method: 'POST',
      loading: true
    }),
  // 暂存/提交
  tempSaveOrSubmit: (souType, data) =>
    http({
      url: `${souAnswerPath}/tempSaveOrSubmit/${souType}`,
      method: 'POST',
      data,
      loading: true
    }),
  // 查询详情
  getDetail: (souType, id) =>
    http({
      url: `${souAnswerPath}/getDetail/${souType}/${id}`,
      method: 'GET',
      loading: true
    })
}

/**
 * souQuestion 质疑 Controlller
 */
const souQuestionPath = `${buyerPath}/souQuestion`
const souQuestion = {
  // 列表查询
  getListPageUrl: souType => `${souQuestionPath}/listPage/${souType}`,
  // 详情
  getDetail: (souType, params) =>
    http({
      url: `${souQuestionPath}/getBidingQuestionById/${souType}`,
      method: 'GET',
      params,
      loading: true
    }),
  // 驳回
  reject: (souType, data) =>
    http({
      url: `${souQuestionPath}/reject/${souType}`,
      method: 'POST',
      data,
      loading: true
    })
}

// 根据后端不同的 Controlller 区分不同的对象拆分
export default {
  souAnswer,
  souQuestion
}
