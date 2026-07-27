<script setup lang="ts">
import { i18nExpression, expression, defineSchemas, generateXindexInOrder } from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import BaseInfo from './components/collapseItem/baseInfo'
import Detail from './components/collapseItem/detail'

import { useAttrs } from 'vue-demi'

const { emitTabRemove, t: $t } = usePageHelper()

const $attrs: any = useAttrs()

const scope = {
  emitTabRemove,
  $attrs,
  $t
}

const components = {
  BaseInfo,
  Detail
}

const schema = defineSchemas({
  // 基本信息
  OsMaterial: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component': 'FormContainer',
    'x-data': {
      dialogInit: false,
      queryData: {},
      selectedData: []
    },
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        read: {
          immediate: true,
          action: 'getDetail',
          ready: expression(`() => {
            $form.readPretty = $attrs.params.flag === 'view'

            return $attrs.params.row.materialHeadId
          }`),
          transformRequest: expression(`(data, headers) => {
            data.payload = {filter: {materialHeadId: $attrs?.params?.row?.materialHeadId || $form.values.materialHeadId || '' }}

            data.query['*'] = {}

            return data
          }`),
          onSuccess: expression(`(res) => {
            $form.setValues({
              ...res.data[0]
            })
          }`)
        }
      }
    },
    items: {
      type: 'void',
      properties: {
        cancel: {
          type: 'void',
          'x-content': i18nExpression('common.close'),
          'x-component': 'Button',
          'x-component-props': {
            type: 'default',
            '@click': expression(`() => {
               emitTabRemove($attrs.tabName)
            }`)
          }
        }
      }
    },
    properties: {
      collapse: {
        type: 'void',
        'x-component': 'Collapse',
        properties: generateXindexInOrder({
          baseInfo: {
            ...BaseInfo
          },
          detail: {
            ...Detail
          }
        })
      }

    }
  }
})
</script>

<template>
  <RenderEngine
    schemaKey="OutsourceMaterialDetail"
    :pageAttrs="$attrs"
    :schema="schema"
    :scope="scope"
    :components="components"
  />
</template>
