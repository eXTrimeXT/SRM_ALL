import http from '@/utils/axios/http'
// 建群
export function createChatgroup (data) {
  return http({
    url: '/api-base/chat/chatgroup/createChatgroup',
    method: 'POST',
    data: data
  })
}
// 添加成员
export function addChatgroupMember (data) {
  return http({
    url: '/api-base/chat/chatgroup/addChatgroupMember',
    method: 'POST',
    data: data
  })
}
// 移除成员
export function removeChatgroupMember (data) {
  return http({
    url: '/api-base/chat/chatgroup/removeChatgroupMember',
    method: 'POST',
    data: data
  })
}
// 获取群信息，带有成员信息
export function getChatgroup (data) {
  return http({
    url: '/api-base/chat/chatgroup/getChatgroup',
    method: 'POST',
    data: data,
    loading: true
  })
}
// 查询群列表，跟自己相关的
export function querytChatgroupSelf (data) {
  return http({
    url: '/api-base/chat/chatgroup/querytChatgroupSelf',
    method: 'POST',
    data: data
  })
}
// 创建消息
export function createChatdata (data) {
  return http({
    url: '/api-base/chat/chatdata/createChatdata',
    method: 'POST',
    data: data
  })
}
// 查询消息，自己能看的
export function queryChatdataSelf (data) {
  return http({
    url: '/api-base/chat/chatdata/queryChatdataSelf',
    method: 'POST',
    data: data
  })
}
// 更新已读状态
export function updateChatdataRead (data) {
  return http({
    url: '/api-base/chat/chatdata/updateChatdataRead',
    method: 'POST',
    data: data
  })
}
// 撤回
export function withdrowChatdata (data) {
  return http({
    url: '/api-base/chat/chatdata/withdrowChatdata',
    method: 'POST',
    data: data
  })
}
