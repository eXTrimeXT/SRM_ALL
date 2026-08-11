// 附件配置相关api
import http from '@/utils/axios/http'

// 动态附件组件常调接口
export const sceneFileCompApi = {
  sceneFileListAll: async data =>
    http({
      url: '/api-base/base/scene_file/listAll',
      method: 'POST',
      data,
      loading: false
    }),
  sceneTemplateListAll: async data =>
    http({
      url: '/api-base/base/scene_template/listAll',
      method: 'POST',
      data,
      loading: false
    })
}
