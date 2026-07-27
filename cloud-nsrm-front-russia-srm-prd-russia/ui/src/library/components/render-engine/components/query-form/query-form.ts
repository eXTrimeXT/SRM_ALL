// 普通的方式
import { defineComponent, h, Ref, watch } from 'vue-demi'
import { FieldModel, isFn, useField } from '@meicloud/render-engine'
import { useToggle } from '@vueuse/core'
import { BaseQueryForm } from './base-query-form'

export const QueryForm = defineComponent({
  name: 'QueryForm',
  inheritAttrs: false,
  setup (_, { attrs, listeners }) {
    const fieldRef = useField() as Ref<FieldModel>
    const [loading, loadingToggle] = useToggle()

    const run = (eventName: string) => {
      if (!isFn(listeners[eventName])) {
        return
      }

      loadingToggle(true)

      // @ts-ignore
      // eslint-disable-next-line no-unused-expressions
      listeners[eventName](fieldRef.value.value)?.finally(() => {
        loadingToggle(false)
      })
    }

    watch(loading, (disabled) => {
      fieldRef.value
          .query(fieldRef.value.address.concat('toolbar'))
          .take(field => {
            field.setComponentProps({
              disabled
            })
          })
    })

    const handleQuery = () => {
      run('query')
    }

    const handleReset = () => {
      fieldRef.value.reset()

      run('reset')
    }

    return () => {
      return h(BaseQueryForm, {
        attrs,
        on: {
          query: handleQuery,
          reset: handleReset
        }
      })
    }
  }
})
