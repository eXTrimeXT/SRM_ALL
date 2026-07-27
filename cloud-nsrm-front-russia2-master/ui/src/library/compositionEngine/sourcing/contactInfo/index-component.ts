import { h, useField } from '@meicloud/render-engine'
import { defineComponent, watch } from 'vue-demi'

export const ContactInfo = defineComponent({
  name: 'ContactInfo',
  props: {
    // 是否设置默认联系方式
    setDefault: {
      type: Boolean,
      default: false
    }
  },
  setup (props) {
    const field = useField()
    console.log(field)

    watch(
      () => props.setDefault,
      val => {
        if (val) {
          console.log('!-')
        }
        // fieldRef.value.form.clearFormGraph(`${fieldRef.value.address}.*`)
      },
      { immediate: true }
    )

    return () => {
      return h(
        'div',
        {},
        {
          default: () =>
            h(
              'div',
              {},
              {},
            )
        },
      )
    }
  }
})
