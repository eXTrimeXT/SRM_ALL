import { h, defineComponent } from 'vue-demi'
// @ts-ignore
import { Pagination as ElPagination } from '@meicloud/element-ui'
import { useQueryEngineConfig } from '@meicloud/render-engine'

export const RenderTablePagination = defineComponent({
  name: 'RenderTablePagination',
  props: {
    ...ElPagination.props,
    pageSizes: {
      type: Array,
    },
    layout: {
      type: String,
    },
  },
  setup(props, { attrs, listeners }) {
    // TODO 解耦
    const queryEngineConfig = useQueryEngineConfig()

    return () => {
      return h(ElPagination, {
        props: {
          ...props,
          layout:
            props.layout ??
            queryEngineConfig.value!.pagination?.layout ??
            'total, prev, pager, next, sizes, jumper',
          pageSizes: props.pageSizes ?? queryEngineConfig.value!.pagination?.pageSizes,
          pageSize: props.pageSize ?? queryEngineConfig.value!.pagination?.pageSize,
        },
        style: attrs?.style ?? 'padding-top: 16px; padding-bottom: 4px',
        on: listeners,
      })
    }
  },
})
