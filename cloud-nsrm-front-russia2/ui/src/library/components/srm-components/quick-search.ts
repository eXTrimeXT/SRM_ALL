import { defineComponent, h, shallowRef, getCurrentInstance } from 'vue-demi'
import { observer, useField, FieldModel, useFieldSchema } from '@meicloud/render-engine'
import QuickSearch from 'lib@/components/QuickSearch'

export const QuickSearchWrapper = observer(
  defineComponent({
    name: 'QuickSearchWrapper',
    props: {
      ...QuickSearch.props,
      propKey: String,
    },
    inheritAttrs: false,
    setup(props, { listeners, emit }) {
      const instance = getCurrentInstance()?.proxy
      const field = useField<FieldModel>()
      const fieldSchema = useFieldSchema()

      const selectedObj = shallowRef<Record<string, any>>({})

      const handleClose = (val: any = {}, ...args: any[]) => {
        console.log(val, 'val')
        const value = val?.[props.propKey ?? fieldSchema.value.name]
        const valueKey = props.showKey // 组件绑定取值优选为propKey，不设置取showKey // props.propKey || props.showKey
        selectedObj.value = value === undefined ? {} : { [value]: val[valueKey] }

        try {
          field.value.onInput(value)
        } catch (e) {

        }

        emit('close-quicksearch', val, instance, ...args)
      }

      return () => {
        return h(QuickSearch, {
          ref: 'quickSearchRef',
          props: {
            ...props,
            readPretty: field.value?.componentProps?.readPretty ?? field.value.readPretty,
            showInput: selectedObj.value[field.value.value as unknown as string] ?? field.value.value,
          },
          on: {
            ...listeners,
            'close-quicksearch': handleClose,
          },
        })
      }
    },
  }),
)
