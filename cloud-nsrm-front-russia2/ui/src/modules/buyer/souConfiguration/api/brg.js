/**
 * @description 采购商 - 项目式询价
 * @author donghf3
 */
import http from '@/utils/axios/http'

// 采购商 - 询价管理
const buyerPath = '/api-sou/buyer/brg'

/**
 * process 流程 Controlller
 */
const processPath = `${buyerPath}/process`
const process = {
  // 列表查询
  listPageUrl: `${processPath}/page`,
  // 列表查询
  page: data =>
    http({
      url: `${processPath}/page`,
      method: 'POST',
      data
    }),
  // 编辑保存
  editProcessConfig: data =>
    http({
      url: `${processPath}/editProcessConfig`,
      method: 'POST',
      data,
      loading: true
    }),
  // 生效
  valid: id =>
    http({
      url: `${processPath}/valid/${id}`,
      method: 'POST',
      loading: true
    }),
  // 失效
  invalid: id =>
    http({
      url: `${processPath}/invalid/${id}`,
      method: 'POST',
      loading: true
    }),
  // 删除
  remove: id =>
    http({
      url: `${processPath}/remove/${id}`,
      method: 'DELETE',
      loading: true
    }),
  // 查询询价单关联的流程节点信息
  projectNodes: id =>
    http({
      url: `${processPath}/nodes/${id}`,
      method: 'GET',
      loading: true
    })
}

export default {
  process
}
