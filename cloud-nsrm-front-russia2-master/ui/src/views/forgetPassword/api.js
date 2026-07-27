import http from '@/utils/axios/http'

const forgetPwdApi = {
  getImgCode: params =>
    http({
      url: '/api-rbac/rbac-anon/forget/imgCode',
      method: 'GET',
      params
    }),
  sliderCheck: data =>
    http({
      url: '/api-rbac/rbac-anon/forget/pj/sliderCheck',
      method: 'POST',
      data
    }),
  getUser: data =>
    http({
      url: '/api-rbac/rbac-anon/forget/pj/getUser',
      method: 'POST',
      data,
      loading: true
    }),
  sendMsgCode: data =>
    http({
      url: '/api-rbac/rbac-anon/forget/sendMsgCode',
      method: 'POST',
      data,
      loading: true
    }),
  checkMsgCode: data =>
    http({
      url: '/api-rbac/rbac-anon/forget/checkMsgCode',
      method: 'POST',
      data,
      loading: true
    }),
  changePwd: data =>
    http({
      url: '/api-rbac/rbac-anon/forget/changePwd',
      method: 'POST',
      data,
      loading: true
    })

}

export default forgetPwdApi
