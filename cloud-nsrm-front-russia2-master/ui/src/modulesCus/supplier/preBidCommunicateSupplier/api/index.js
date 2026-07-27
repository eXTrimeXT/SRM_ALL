import http from '@/utils/axios/http'

const baseNoticePath = '/api-sou/api-ql/PreBidNotice/'

const method = 'POST'

const communicateNotice = {
  listPageUrl: `${baseNoticePath}query`,
  save: data =>
    http({
      url: `${baseNoticePath}save`,
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
    })
}

const baseFeedbackPath = '/api-sou/api-ql/PreBidFeedback/'

const communicateFeedback = {
  listPageUrl: `${baseFeedbackPath}query`,
  save: data =>
    http({
      url: `${baseFeedbackPath}save`,
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
    })
}

export {
  communicateNotice as commuNoticeHttp,
  communicateFeedback as commuFeedbackHttp
}

