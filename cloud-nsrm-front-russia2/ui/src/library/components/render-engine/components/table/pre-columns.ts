import { computed, h, type Ref } from 'vue-demi'
import { useExpressionScope, Schema, i18nExpression } from '@meicloud/render-engine'
import type { ColumnCellRenderParams } from 'vxe-table'
import { Checkbox, Radio } from '@meicloud/element-ui'

type CheckboxCellParams = ColumnCellRenderParams & { checked: boolean; indeterminate: boolean }

export const usePreColumns = (preColumnsRef: Ref<string | undefined>) => {
  const expressionScope = useExpressionScope()

  const columnMap = () => ({
    checkbox: h('vxe-column', {
      key: '__checkbox__',
      props: { type: 'checkbox', width: 42, fixed: 'left' },
      scopedSlots: {
        header: ({ checked, $table, indeterminate }: CheckboxCellParams) => {
          return h(Checkbox, {
            props: { value: checked, indeterminate },
            on: {
              change: (event: any) => $table.triggerCheckAllEvent(event, !checked),
            },
          })
        },
        checkbox: (params: CheckboxCellParams) => {
          return h(Checkbox, {
            props: { value: params.checked },
            on: {
              change: (event: any) => params.$table.handleToggleCheckRowEvent(event, params),
            },
          })
        },
      },
    }),
    radio: h('vxe-column', {
      key: '__radio__',
      props: { type: 'radio', width: 54, fixed: 'left' },
      scopedSlots: {
        radio: (params: CheckboxCellParams) => {
          return h(Radio, {
            props: { value: params.checked },
            on: {
              change: (event: any) => params.$table.triggerRadioRowEvent(event, params),
            },
          })
        },
      },
    }),
    seq: h('vxe-column', {
      key: '__seq__',
      props: {
        type: 'seq',
        width: 54,
        title: Schema.shallowCompile(i18nExpression('common.sort'), expressionScope!.value),
        fixed: 'left',
      },
    }),
  })

  return computed(() => {
    if (!preColumnsRef.value) return []

    return preColumnsRef.value.split(',').map(key => columnMap()[key.trim() as 'seq'])
  })
}
