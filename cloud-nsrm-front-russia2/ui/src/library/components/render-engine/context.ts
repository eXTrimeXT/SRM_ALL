import { inject, provide, ref } from 'vue-demi'
import { useQueryEngineConfig } from '@meicloud/render-engine'

export const useRenderEngineConfigProviderSymbol = Symbol('useRenderEngineConfigProviderSymbol')

export const useRenderEngineConfigProvider = (props: any) => {
  const queryEngineConfig = useQueryEngineConfig()

  const configRef = ref(props)

  const baseRequest = (config: any) =>
    // @ts-ignore
    queryEngineConfig.value.requestMethod({
      method: 'post',
      baseURL: queryEngineConfig.value.uri({
        currentQueryEngineConfig: {
          // 定制返回URL
          special: true,
          service: config.service,
          type: config.type,
          action: config.action
        }
      }),
      data: {
        lang: 'zh-cn',
        query: {
          '*': {}
        },
        ...config
      }
    })

  const provideValues = {
    queryEngineConfig,
    baseRequest,
    configRef
  }

  provide(useRenderEngineConfigProviderSymbol, provideValues)

  return provideValues
}

export const useRenderEngineConfig = () => {
  const storeContext = inject<ReturnType<typeof useRenderEngineConfigProvider>>(
    useRenderEngineConfigProviderSymbol,
  )

  if (!storeContext) {
    throw new Error('Please mount useRenderEngineConfigProvider first')
  }

  return storeContext
}
