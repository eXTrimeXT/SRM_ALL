import http from '@/utils/axios/http'

const basePath = '/api-sou/api-ql/ExtSouExpertApplyForBuyer'

const commonType = 'ExtSouExpertApplyForBuyer'

const expertInfo = {
  getApplyInfoByUserId: data =>
    http({
      url: `${basePath}/getLatestApplyInfoByUserId`,
      method: 'POST',
      data,
      loading: true
    }),
  getBuOrganizationByOuOrgCode: params =>
    http({
      url: '/api-pj/pj-anon/user/getBuOrganizationByOuOrgCode',
      method: 'GET',
      params,
      loading: true
    }),
  getHrUserOrgnizationByUsername: params =>
    http({
      url: '/api-pj/pj-anon/user/getHrUserOrgnizationByUsername',
      method: 'GET',
      params,
      loading: true
    }),
  getHrUserInfo: params =>
    http({
      url: '/api-pj/pj-anon/hrUser/getHrUserInfoWithoutErr',
      method: 'GET',
      params,
      loading: true
    })
}

const expertApply = {
  type: 'ExpertApply',
  listPageUrl: `${basePath}/query`,
  // 暂存
  save: data =>
    http({
      url: `${basePath}/tempSaveApply`,
      method: 'POST',
      data,
      loading: true
    }),
  // 提交
  submit: data =>
    http({
      url: `${basePath}/submitApply`,
      method: 'POST',
      data,
      loading: true
    }),
  delete: data =>
    http({
      url: `${basePath}/removeApply`,
      method: 'POST',
      data,
      loading: true
    }),
  // 查询详情
  read: data =>
    http({
      url: `${basePath}/getApplyInfoByApplyId`,
      method: 'POST',
      data,
      loading: true
    })
}

const extBasePath = '/api-sou/api-ql/ExtSouExpertForBuyer'

const extCommonType = 'ExtSouExpertForBuyer'

const expertDatabase = {
  listPageUrl: `${extBasePath}/queryExperts`,
  frozenExpert: data =>
    http({
      url: `${extBasePath}/frozenExpert`,
      method: 'POST',
      data,
      loading: true
    }),
  unFrozenExpert: data =>
    http({
      url: `${extBasePath}/unFrozenExpert`,
      method: 'POST',
      data,
      loading: true
    }),
  quiteExpert: data =>
    http({
      url: `${extBasePath}/quiteExpert`,
      method: 'POST',
      data,
      loading: true
    }),
  // 冻结确认
  frozenExpertConfirm: data =>
    http({
      url: `${extBasePath}/frozenExpertConfirm`,
      method: 'POST',
      data,
      loading: true
    }),
  // 拒绝冻结
  frozenExpertReject: data =>
    http({
      url: `${extBasePath}/frozenExpertReject`,
      method: 'POST',
      data,
      loading: true
    }),
  // 解冻确认
  unfrozenExpertConfirm: data =>
    http({
      url: `${extBasePath}/unfrozenExpertConfirm`,
      method: 'POST',
      data,
      loading: true
    }),
  // 拒绝解冻
  unfrozenExpertReject: data =>
    http({
      url: `${extBasePath}/unfrozenExpertReject`,
      method: 'POST',
      data,
      loading: true
    })
}

const expertReview = {
  listPageUrl: '/api-sou/api-ql/ExtSouExpertScoreForBuyer/queryExpertScores',
  expertDoScore: data =>
    http({
      url: '/api-sou/api-ql/ExtSouExpertScoreForBuyer/expertDoScore',
      method: 'POST',
      data,
      loading: true
    })
}

export {
  commonType,
  extCommonType,
  expertInfo as expInfoHttp,
  expertApply as expApplyHttp,
  expertDatabase as expDataHttp,
  expertReview as expReviewHttp
}
