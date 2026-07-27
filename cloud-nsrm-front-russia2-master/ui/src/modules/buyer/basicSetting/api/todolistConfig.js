/**
 * 待办列表配置
 */
import http from '@/utils/axios/http'

export default {
  listPageUrl: '/api-base/ext/base/todolistConfig/listPage',
  importExcelUrl: '/api-base/base/todolistConfig/importExcel',
  downloadTemplateUrl: '/api-file/files-anon/file/fileupload/downloadTemplate/TODOLIST_IMPORT',
  // 删除
  remove: id =>
    http({
      url: `/api-base/base/todolistConfig/remove/${id}`,
      method: 'DELETE',
      loading: true
    }),
  // 查询
  get: id =>
    http({
      url: `/api-base/ext/base/todolistConfig/get/${id}`,
      method: 'GET',
      loading: true
    }),
  // 提交
  saveOrUpdate: data =>
    http({
      url: '/api-base/ext/base/todolistConfig/saveOrUpdate',
      method: 'POST',
      data,
      loading: true
    })
}
