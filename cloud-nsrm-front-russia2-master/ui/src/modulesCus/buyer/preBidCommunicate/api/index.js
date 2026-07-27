import http from '@/utils/axios/http'

const baseNoticePath = '/api-sou/api-ql/PreBidNoticeBuyer/'

const method = 'POST'

const communicateNoticeBuyer = {
  listPageUrl: `${baseNoticePath}query`,
  save: data =>
    http({
      url: `${baseNoticePath}save`,
      method,
      data,
      loading: true
    }),
  submit: data =>
    http({
      url: `${baseNoticePath}submit`,
      method,
      data,
      loading: true
    }),
  read: data =>
    http({
      url: `${baseNoticePath}read`,
      method,
      data,
      loading: true
    }),
  delete: data =>
    http({
      url: `${baseNoticePath}delete`,
      method,
      data,
      loading: true
    }),
  getVendorList: data =>
    http({
      url: `${baseNoticePath}getVendorList`,
      method,
      data,
      loading: true
    })
}

const baseFeedbackPath = '/api-sou/api-ql/PreBidFeedbackBuyer/'

const communicateFeedbackBuyer = {
  listPageUrl: `${baseFeedbackPath}query`,
  save: data =>
    http({
      url: `${baseFeedbackPath}save`,
      method,
      data,
      loading: true
    }),
  submit: data =>
    http({
      url: `${baseFeedbackPath}submit`,
      method,
      data,
      loading: true
    }),
  read: data =>
    http({
      url: `${baseFeedbackPath}read`,
      method,
      data,
      loading: true
    }),
  delete: data =>
    http({
      url: `${baseFeedbackPath}delete`,
      method,
      data,
      loading: true
    }),
  reject: data =>
    http({
      url: `${baseFeedbackPath}reject`,
      method,
      data,
      loading: true
    })
}

export {
  communicateNoticeBuyer as commuNoticeBuyerHttp,
  communicateFeedbackBuyer as commuFeedbackHttp
}

