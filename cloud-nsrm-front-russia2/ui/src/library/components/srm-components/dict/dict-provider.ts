import { defineComponent } from 'vue-demi'
import { FragmentComponent, h } from '@meicloud/render-engine'
import { RenderTableGlobalSetup } from '@meicloud/render-table'

import { useDictProvider } from './context'

RenderTableGlobalSetup.defaultStaticNodeComponents = ['Input', 'DatePicker', 'DictSelect']
RenderTableGlobalSetup.addFormat('DictSelect', (_, params) => {
  return params.cellValue
    ? params.scope.$dict.findPreviewText(params.cellValue, {
        code: params.componentProps.code,
        customSelectType: params.componentProps.customSelectType,
      })
    : ''
})

export const DictProvider = defineComponent({
  name: 'DictProvider',
  setup(_, { slots }) {
    useDictProvider()

    return () => h(FragmentComponent, {}, slots)
  },
})
