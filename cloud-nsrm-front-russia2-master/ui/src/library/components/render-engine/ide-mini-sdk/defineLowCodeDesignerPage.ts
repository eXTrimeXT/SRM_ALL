import './style.scss'

import axios from 'axios'
import { defineComponent } from 'vue-demi'
import { uid } from '@meicloud/render-engine'
import {
  Selector,
  FormTab,
  FormCollapse,
  FormBottomFixed,
  Upload,
  Link,
} from '@meicloud/render-pix'
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
import { FilePreviewWrapper } from 'lib@/components/filePreview/file-preview-wrapper'
import { useSetupContext } from '@meicloud/render-pix/dist/esm/__builtins__'

export interface DefineLowCodeDesignerPageOptions {
  form?: Record<string, any>
  scope?: Record<string, any>
  schema: Record<string, any>
  components?: Record<string, any>
}

const development = import.meta.env.MODE == 'development'

// 注意 📢📢📢 不要将业务逻辑注入到这里
export const defineLowCodeDesignerPage = (
  options: DefineLowCodeDesignerPageOptions,
  config?: {
    pageName?: string
    interceptFormat?: (
      options: DefineLowCodeDesignerPageOptions,
    ) => DefineLowCodeDesignerPageOptions
    pageId?: string
  },
) => {
  const formatOptions = (opts: DefineLowCodeDesignerPageOptions) =>
    config?.interceptFormat?.(opts) ?? opts

  return defineComponent({
    name: config?.pageName ?? 'LowCodeDesignerPage',
    setup() {
      const { currentInstance } = useSetupContext()

      // TODO 配置异常警告 ⚠️
      const innerOptionsRef = shallowRef<ReturnType<typeof formatOptions> | null>(
        development ? (config?.pageId ? null : formatOptions(options)) : formatOptions(options),
      )

      if (development) {
        const getRemoteConfiguration = () => {
          if (!config?.pageId) {
            return
          }

          axios.get(`/ide/form/function/design/${config.pageId}`).then(res => {
            if (res.data.data?.configContent) {
              const configJson = JSON.parse(res.data.data.configContent)

              innerOptionsRef.value = formatOptions(configJson)
            }
          })
        }

        getRemoteConfiguration()

        // document.addEventListener('visibilitychange', () => {
        //   if (document.visibilityState === 'visible') {
        //     getRemoteConfiguration()
        //   }
        // })
      }

      return () => {
        return h(
          'div',
          {
            class: 'lowcode-page',
          },
          development && !innerOptionsRef.value
            ? [
                h(
                  'div',
                  {
                    style:
                      'width: 100%;height: 93vh;background-color: #edeff2;font-size: 28px;display: flex;justify-content: center;align-items: center;',
                  },
                  // @ts-ignore
                  // 正在获取页面配置
                  currentInstance.$t('cusEntry.library.getPageConfig'),
                ),
              ]
            : [
                h(RenderEngine, {
                  props: {
                    useGlobalDialog: true,
                    layoutProps: innerOptionsRef.value!.form,
                    events: innerOptionsRef.value!.form!.events,
                    schema:
                      innerOptionsRef.value!.schema.properties ?? innerOptionsRef.value!.schema,
                    scope: {
                      $generateUid: uid,
                      ...innerOptionsRef.value!.scope,
                      // @ts-ignore
                      $openFilePreview: (params: any) =>
                        // @ts-ignore
                        currentInstance.$refs.filePreviewWrapper.open(params),
                    },
                    components: {
                      Link,
                      Upload,
                      FormTab,
                      Selector,
                      FormCollapse,
                      FormBottomFixed,
                      ...innerOptionsRef.value!.components,
                    },
                  },
                }),
                h(FilePreviewWrapper, {
                  ref: 'filePreviewWrapper',
                }),
              ],
        )
      }
    },
  })
}
