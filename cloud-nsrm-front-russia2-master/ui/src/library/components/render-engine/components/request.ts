import { defineComponent, onActivated, onMounted } from 'vue-demi'
import {
  FragmentComponent,
  h,
  useExpressionScope,
  useFieldSchema,
  Schema,
  isFn,
  lazyMerge,
  SchemaExpressionScopeSymbol,
  useField,
  markRaw
} from '@meicloud/render-engine'
import http from '@/utils/axios/http'

export const REQUEST_PROVIDER_KEY = 'x-request'

// TODO 内置请求器
/**
 * // 是否手动
 * manual: boolean
 * // 触发的声明周期
 * lifeCycle: 'created' | 'mounted' | 'activated'
 * method: 'get' | 'post' | '...'
  url: string
  params: object | function
  config: object
  onSuccess: function
  onError: function
 * Ready
 * refreshDeps
 * refreshOnWindowFocus
 * debounceWait
 * throttleWait
 * cacheKey
 * staleTime
 * retryCount
 * loadingDelay
 */

/**
 @example
 'x-request': {
    // axios 的参数，所有的属性都可以写表达式
    method: 'get',
    url: 'aaa',
    config: {
      // 传入其他配置，比如 headers、transformResponse，具体看 axios 的request
    },
    // 指定执行的声明周期，默认 created
    // lifeCycle: 'created',
    // 是否手动执行，默认自动执行，下级节点可以通过 $requestProvider.run() 就可以手动执行或者是重复执行
    // manual: false,
    // 可以获取某的字段值，获取路由参数
    params: expression(`() => {
      console.log($route, '$route');
      return {}
    }`),
    data: expression(`() => {
      console.log($route, '$route');
      return {}
    }`),
    onSuccess: expression(`(res) => {
      console.log(res);

      // 设置到某一个字段去
      // $self.query('.xxxx').take().setValue(res.data)
      // $self.query('.xxxx').take().setComponentProps({ options: res.data })
      // $self.query('.xxxx').take().setDataSource( res.data) // select 组件的 options，底层转化
    }`)
  },
 */
export const RequestProvider = defineComponent({
  name: 'RequestProvider',
  setup(_, { slots }) {
    const fieldRef = useField()
    const fieldSchema = useFieldSchema()
    const scopeRef = useExpressionScope()

    const requestConfig = fieldSchema.value[REQUEST_PROVIDER_KEY] ?? {}
    const compileRequestConfig = Object.keys(requestConfig).reduce(
      (acc, key) => ({
        ...acc,
        [key]: Schema.shallowCompile(requestConfig[key], scopeRef.value),
      }),
      {},
    ) as Record<string, any>

    // TODO 更多判断
    // TODO 防抖节流、loading、缓存等处理
    const submitRequest = (customParameters = {}) => {
      const params =
        compileRequestConfig.params && isFn(compileRequestConfig.params)
          ? compileRequestConfig.params()
          : compileRequestConfig.params
      const data =
        compileRequestConfig.data && isFn(compileRequestConfig.data)
          ? compileRequestConfig.data()
          : compileRequestConfig.data

      compileRequestConfig?.onToggle?.(true)

      return http
        .request({
          method: compileRequestConfig.method,
          url: compileRequestConfig.url,
          params,
          data,
          ...(compileRequestConfig.config ?? {}),
          ...customParameters,
        })
        .then(res => {
          compileRequestConfig?.onSuccess?.(res)
        })
        .catch(err => {
          compileRequestConfig?.onError?.(err?.response.data.data)
        })
        .finally(() => {
          compileRequestConfig?.onToggle?.(false)
          compileRequestConfig?.onFinally?.()
        })
    }

    const expressionScopeRef = computed(() =>
      lazyMerge(scopeRef!.value, {
        $requestProvider: {
          run: submitRequest,
        },
      }),
    )

    provide(SchemaExpressionScopeSymbol, expressionScopeRef)

    const run = () => {
      if (compileRequestConfig.manual) {
        return
      }

      submitRequest()
    }

    if (!compileRequestConfig.lifeCycle || compileRequestConfig.lifeCycle === 'created') {
      run()
    }

    onMounted(() => {
      fieldRef.value.setData({
        requestProvider: markRaw({
          run: submitRequest,
        })
      })

      if (compileRequestConfig.lifeCycle === 'mounted') {
        run()
      }
    })

    onActivated(() => {
      if (compileRequestConfig.lifeCycle === 'activated') {
        run()
      }
    })

    return () => {
      return h(FragmentComponent, {}, slots)
    }
  },
})
