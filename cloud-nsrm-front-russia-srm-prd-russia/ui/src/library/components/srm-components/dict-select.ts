import { connect, mapReadPretty, observer } from '@meicloud/render-engine'
import { defineComponent, h, ref, watch } from 'vue-demi'
// @ts-ignore
import { usePlaceholder } from '@meicloud/render-pix'

// @ts-ignore
import OrginDictSelect from 'lib@/components/c-select/dict-select'
import { useDict } from './dict/context'

const commonProps: Record<string, object> = {
  // 多选情况下，可以把value转成字符串
  valueIsString: {
    type: Boolean,
    default: false,
  },
  // 把value转成字符串的分割符
  valueStringSeparator: {
    type: String,
    default: ',',
  },
}

export const DictSelectPreview = observer(
  defineComponent({
    name: 'DictSelectPreview',
    props: {
      value: [Number, String, Boolean, Array],
      code: {
        type: String,
        required: true,
      },
      customSelectType: String,
      ...commonProps,
    },
    setup(props) {
      const text = ref('')
      const placeholder = usePlaceholder(text)

      const { getDictByCode, previewText } = useDict()

      const findValueByDict = async () => {
        text.value =
          !props.value || !props.code
            ? ''
            : await getDictByCode(props.code, props.customSelectType).then((data: any) =>
                previewText(props.value, data),
              )
      }

      watch(
        () => [props.value, props.code],
        () => {
          findValueByDict()
        },
        { immediate: true },
      )

      return () => {
        return h('span', {}, placeholder.value)
      }
    },
  }),
)

const DictSelectWrapper = defineComponent({
  name: 'DictSelect',
  props: {
    ...OrginDictSelect.props,
    beforeChange: Function,
    ...commonProps,
  },
  setup(props, { attrs, listeners }) {
    let cacheValue = props.value

    // 这里是以 change 作为双向绑定中的事件处理
    const handleChange = async (value: any) => {
      try {
        if (props.beforeChange) {
          await props.beforeChange(value)
        }

        const innerValue =
          props.valueIsString && Array.isArray(value)
            ? value.join(props.valueStringSeparator)
            : value

        ;(listeners.change as Function | undefined)?.(innerValue)

        // 储存新的值
        cacheValue = innerValue
      } catch {
        // 如果不通过，那么就回滚旧值
        ;(listeners.change as Function | undefined)?.(cacheValue)
      }
    }

    const innerValue = computed(() =>
      props.value && props.valueIsString && !Array.isArray(props.value)
        ? props.value.toString().split(props.valueStringSeparator)
        : props.value,
    )

    return () => {
      return h(OrginDictSelect, {
        attrs,
        props: {
          ...props,
          value: innerValue.value,
        },
        on: {
          ...listeners,
          change: handleChange,
        },
      })
    }
  },
})

export const DictSelect = connect(DictSelectWrapper, mapReadPretty(DictSelectPreview))
