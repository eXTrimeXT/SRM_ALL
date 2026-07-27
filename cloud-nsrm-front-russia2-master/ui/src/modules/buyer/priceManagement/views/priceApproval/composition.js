/**
 * 根据页面类型，返回保存提交url
 * @param pageType
 */
export const getSaveAndSubmitApiUrl = pageType => {
  if (pageType.isInquiry) {
    // 简易询价
    return {
      saveApi: '/api-inq/price/approval/tempSaveFromInquiry',
      submitApi: '/api-inq/price/approval/submitFromInquiry'
    }
  }
  if (pageType.isHandMake) {
    // 手动创建
    return {
      saveApi: '/api-inq/price/approval/tempSaveFromHandMake',
      submitApi: '/api-inq/price/approval/submitFromHandMake'
    }
  }
  if (pageType.isBiding) {
    // 招标
    return {
      saveApi: '/api-inq/price/approval/tempSaveFromBiding',
      submitApi: '/api-inq/price/approval/submitFromBiding'
    }
  }
  if (pageType.isBargain) {
    // 项目式询价
    return {
      saveApi: '/api-inq/price/approval/tempSaveFromBargain',
      submitApi: '/api-inq/price/approval/submitFromBargain'
    }
  }
  if (pageType.isCompetition) {
    // 竞价
    return {
      saveApi: '/api-inq/price/approval/tempSaveFromComp',
      submitApi: '/api-inq/price/approval/submitFromComp'
    }
  }
  // 给默认值，手动创建
  return {
    saveApi: '/api-inq/price/approval/tempSaveFromHandMake',
    submitApi: '/api-inq/price/approval/submitFromHandMake'
  }
}
