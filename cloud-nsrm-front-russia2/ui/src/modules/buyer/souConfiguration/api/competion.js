/**
 * @description 采购商 - 竞价流程配置
 * @author chengwei
 */
import http from '@/utils/axios/http'

// 采购商 - 竞价管理
const buyerPath = '/api-sou/api-ql'

/**
 * process 流程 Controlller
 */
const processPath = `${buyerPath}/AuctSouProjectForBuyer`
const process = {
  // 列表查询
  listPageUrl: `${buyerPath}/SouProcessConfig/query`,
  // 列表查询
  page: data =>
    http({
      url: `${buyerPath}/SouProcessConfig/query`,
      method: 'POST',
      data,
      loading: true
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
  validProcessConfig: data =>
    http({
      url: `${processPath}/validProcessConfig`,
      method: 'POST',
      data,
      loading: true
    }),
  // 失效
  invalidProcessConfig: data =>
    http({
      url: `${processPath}/invalidProcessConfig`,
      method: 'POST',
      data,
      loading: true
    }),
  // 删除
  removeProcessConfig: data =>
    http({
      url: `${processPath}/removeProcessConfig`,
      method: 'POST',
      data,
      loading: true
    }),
  // mql竞价流程配置详情查询
  getProcessConfig: data =>
    http({
      url: `${processPath}/getProcessConfig`,
      method: 'POST',
      data,
      loading: true
    })
}

export default {
  process
}
