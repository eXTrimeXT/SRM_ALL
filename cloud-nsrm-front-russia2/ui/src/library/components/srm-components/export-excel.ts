import { defineComponent, h, computed } from 'vue-demi'

import OriginExportExcel from 'lib@/components/export-excel'

export const ExportExcel = defineComponent({
  name: 'ExportExcel',
  inheritAttrs: false,
  props: OriginExportExcel.props,
  setup (props, { attrs, slots, listeners }) {
    const columns = computed(
      () =>
        props.tableHeader?.map(col => ({
          ...col,
          prop: col.field ?? col.prop,
          label: col.title ?? col.label
        })) ?? [],
    )

    return () => {
      return h(
        OriginExportExcel,
        { on: listeners, props: { ...props, tableHeader: columns.value }, attrs },
        slots,
      )
    }
  }
})
