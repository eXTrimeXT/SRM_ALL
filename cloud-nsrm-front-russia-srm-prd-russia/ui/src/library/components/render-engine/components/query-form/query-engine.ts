import { defineComponent, h } from 'vue-demi'
import {
  QueryEngineBlockContainer,
  useQueryEngineBlock,
  useQueryEngine
} from '@meicloud/render-engine'

import { BaseQueryForm } from './base-query-form'
import { useAutoRunByDebounce } from '@/library/components/composables/useAutoRunByDebounce'

const Main = defineComponent({
  name: 'QueryFormByQueryEngine',
  setup (_, { attrs }) {
    const queryEngine = useQueryEngine()
    const queryEngineBlock = useQueryEngineBlock()

    const handleQuery = () => {
      queryEngineBlock.queryData()
    }

    const handleReset = () => {
      queryEngineBlock.resetQueryData()
    }

    // 会有响应的场景吗？
    if (attrs.action) {
      // @ts-ignore
      queryEngine.state.paginationManagement.actionConfig.value.action = attrs.action
      // @ts-ignore
      queryEngine.state.paginationManagement.actionConfig.value.actionConfigKey =
        attrs.actionConfigKey
    }

    useAutoRunByDebounce(
      () => {
        return queryEngineBlock.field.value.loading
      },
      disabled => {
        queryEngineBlock.field.value
          .query(queryEngineBlock.field.value.address.concat('toolbar'))
          .take(field => {
            field.setComponentProps({
              disabled
            })
          })
      },
    )

    return () => {
      return h(BaseQueryForm, {
        props: attrs,
        on: {
          query: handleQuery,
          reset: handleReset
        }
      })
    }
  }
})

export const QueryFormByQueryEngine = defineComponent({
  name: 'QueryFormByQueryEngineWrapper',
  inheritAttrs: false,
  setup (_, { attrs }) {
    return () => {
      return h(QueryEngineBlockContainer, {}, [h(Main, { attrs })])
    }
  }
})
