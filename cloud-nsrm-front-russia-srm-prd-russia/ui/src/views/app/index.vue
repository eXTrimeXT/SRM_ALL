<template>
  <div id="app" :class="[device]">
    <RenderEngineConfigProvider v-bind="defaultRenderEngineConfig">
      <QueryEngineConfigProvider v-bind="defaultQueryEngineConfig">
        <DictProvider>
          <router-view />
        </DictProvider>
      </QueryEngineConfigProvider>
    </RenderEngineConfigProvider>
    <SysRemindNew />
  </div>
</template>

<script>
import { mapState } from 'vuex'
import { getStore } from '@/main'
import { getToken } from '@/utils/auth'
import ResizeMixin from '@/layout/mixin/ResizeHandler'
import { setGlobalMessage } from '@/utils/message'
import { QueryEngineConfigProvider, RenderEngineConfigProvider } from '@meicloud/render-engine'
import { DictProvider } from '@/library/components/srm-components/dict'
import { sysPrefix } from '@/config/ipConfig'
import { FILE_UPLOAD, IDE_FILE_UPLOAD, IDE_FILE_DOWNLOAD_PATTERN } from '@/api/common'
import http from '@/utils/axios/http'
import SysRemindNew from '@/components/sysRemindNew'
import { getDownloadFileUrl } from 'lib@/utils/file'

const responseSuccessByCode = (code) => code === '0'

export default {
  name: 'App',
  components: {
    RenderEngineConfigProvider,
    QueryEngineConfigProvider,
    DictProvider,
    SysRemindNew
  },
  mixins: [ResizeMixin],
  computed: {
    ...mapState({
      device: state => state.app.device
    }),
    defaultRenderEngineConfig () {
      return {
        request: (params) => {
          // 精准分流 ide 上传文件服务
          if (params.url === IDE_FILE_UPLOAD) {
            return http({
              ...params,
              url: FILE_UPLOAD,
              headers: {
                ...params.headers,
                Contenttype: 'form-data'
              }
            }).then(response => {
              const { code, data } = response
              if (!responseSuccessByCode(code)) {
                return Promise.reject(data)
              }

              // TODO 映射字段给 render-pix upload 使用 后续可以考虑做在物料配置中
              return Promise.resolve([
                {
                  id: data.fileuploadId,
                  fileOriginalName: data.fileSourceName
                }
              ])
            })
          }

          // 精准分流 ide 下载文件服务
          if (IDE_FILE_DOWNLOAD_PATTERN.test(params.url)) {
            // 从 url 取出 fileId
            const [, fileuploadId] = IDE_FILE_DOWNLOAD_PATTERN.exec(params.url)
            return http({
              ...params,
              method: 'get',
              url: getDownloadFileUrl(fileuploadId),
              responseType: 'arraybuffer',
              returnDirectly: true
            })
          }
        }
      }
    },
    defaultQueryEngineConfig () {
      return {
        // fixedSuffix: false,
        uri: ({ currentQueryEngineConfig, requestBody, actionConfig }) => {
          // 添加loading
          if (actionConfig?.loading !== undefined && requestBody) {
            requestBody.loading = actionConfig.loading
          }

          /**
           * 'x-query-engine': {
           *  # sup 是后端的服务标识，标记的名字可以叫任意名称，这里只是用 service 做例子
           *  service: 'sup'
           * }
           */
          // TODO 等重构了 DynamicTableConfig 的请求后，这里可以统一用 requestBody 的相关值
          const type = requestBody?.type ?? currentQueryEngineConfig.type

          // 合同模块走聚合接口
          if (
            [
              'DynamicFormSchemaData',
              'DynamicTableConfig'
            ].includes(type)
          ) {
            return `${sysPrefix()}/api-base/api-ql/${type}/${requestBody?.action ?? currentQueryEngineConfig.action}`
          }

          const service = (
            actionConfig.service || currentQueryEngineConfig.service || requestBody?.service
          )?.replace(/^api\-/, '')

          return `${sysPrefix()}/api-${service}/api-ql/${type}/${requestBody.action}`
        },
        requestMethod: (param) => {
          return http({
            ...param,
            returnDirectly: true,
            loading: param.data.loading ?? false,
            transformResponse: param.transformResponse && (
              (res) => {
                // TODO 使用更快的 JSON.parse
                const data = JSON.parse(res)
                if (responseSuccessByCode(data.code)) {
                  return param.transformResponse(res)
                }

                return data
              }
            )
          }).then(res => {
            if (!responseSuccessByCode(res.data.code)) {
              return Promise.reject(res.data)
            }

            return res.data.data
          })
        },
        pagination: {
          layout: 'total, prev, pager, next, sizes, jumper',
          pageSizes: [15, 30, 60, 120, 300, 600, 1000, 1500],
          pageSize: 15
        },
        onError: (requestPayload, err) => {
          if (err.message) {
            let code = err.code
            let isCanceled = err.message == 'canceled'
            let message = err.message == 'canceled' ? '会话失效，已取消请求！' : err.message
            if (!isCanceled && !['SRM_COMMON_00025', 'SRM_COMMON_00060', 'ERR_CANCELED', undefined].includes(code)) {
              this.$message.error(message)
            }
            if (code == 'SRM_COMMON_00060') {
              this.$store.dispatch('app/appRegisterFn', err.message)
            }
          }
        }
      }
    }
  },
  watch: {
    // keep-alive模式下，页面跳转前要手动移除残留的tooltip
    $route () {
      setTimeout(() => {
        const vxeTabletips = document.querySelectorAll('.vxe-table--tooltip-wrapper')
        const elTabletips = document.querySelectorAll('.el-tooltip__popper')
        // vxeTable
        if (vxeTabletips.length) {
          Array.from(vxeTabletips).map((node) => document.body.removeChild(node))
        }
        // elTable
        if (elTabletips.length) {
          Array.from(elTabletips).map((node) => document.body.removeChild(node))
        }
      }, 2000)
    }
  },
  created () {
    let store = getStore()
    document.addEventListener('visibilitychange', function () {
      if (store.getters.token !== getToken() &&
        (window.location.hash.search('login') === -1 &&
          window.location.hash.search('portal') === -1 &&
          window.location.hash.search('registered') === -1 &&
          window.location.hash.search('userProtocol') === -1 &&
          window.location.hash.search('forgetPassword') === -1 &&
          window.location.hash.search('noUser') === -1
        )) {
        location.reload()
      }
    })
    // 设置全局message
    setGlobalMessage()
  }
}
</script>
