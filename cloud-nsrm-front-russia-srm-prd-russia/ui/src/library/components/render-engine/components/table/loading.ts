import { defineComponent, h } from 'vue-demi'

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
        // TODO i18n
        h('p', { staticClass: 'el-loading-text' }, '加载中')
      ])
    }
  }
})
