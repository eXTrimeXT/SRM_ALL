import { defineComponent, h } from 'vue-demi'
import { Button } from '@meicloud/element-ui'

// TODO 从作用域或者是配置中取
import { useI18n } from '../../../composables/useI18n'

export const Collapsible = defineComponent({
  name: 'QueryFormCollapsible',
  props: {
    targetContainerDom: Object,
  },
  setup(_, { emit }) {
    const [expanded, toggle] = useToggle()
    const { t } = useI18n()

    const handleToggle = (e: MouseEvent) => {
      e.preventDefault()

      toggle()

      emit('toggle', expanded.value)
    }

    return () => {
      return h(
        Button,
        {
          style: 'display: flex;',
          props: {
            type: 'text',
          },
          on: {
            click: handleToggle,
          },
        },
        [
          (expanded.value ? t('common.collapseForm') : t('common.expandForm')) as string,
          h('i', {
            staticClass: 'el-icon-arrow-down',
            style: `margin-left: 4px; transform: rotate(${
              expanded.value ? -180 : 0
            }deg); transition: transform .3s;`,
          }),
        ],
      )
    }
  },
})
