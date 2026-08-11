import { defineComponent, h } from 'vue-demi'
import { useSetupContext } from '../../../composables/useSetupContext'

const { currentInstance } = useSetupContext()
export const Loading = defineComponent({
  name: 'RenderTableLoading',
  setup () {
    return () => {
      return h('div', { staticClass: 'el-loading-spinner' }, [
        h('div', { staticClass: 'el-spin el-spin-spinning' }, [
          h('span', { staticClass: 'el-spin-dot el-spin-dot-spin' }, [
            h('i', { staticClass: 'el-spin-dot-item' }),
            h('i', { staticClass: 'el-spin-dot-item' }),
            h('i', { staticClass: 'el-spin-dot-item' }),
            h('i', { staticClass: 'el-spin-dot-item' })
          ])
        ]),
        // i18n 加载中
        h('p', { staticClass: 'el-loading-text' }, currentInstance.$t('hierarchical.Loading'))
      ])
    }
  }
})
