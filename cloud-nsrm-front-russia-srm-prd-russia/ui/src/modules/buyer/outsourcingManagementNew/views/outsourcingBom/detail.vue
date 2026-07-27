<script setup lang="ts">
import { i18nExpression, expression, defineSchemas, generateXindexInOrder, toJS } from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import OutsourcingBomInfo from './components/collapseItem/outsourcingBomInfo'
import OutsourcingBomDetail from './components/collapseItem/outsourcingBomDetail'
import {
  buttonListItemVisibleByPermission
} from 'lib@/components/render-engine/schema-segments'

import { useAttrs } from 'vue-demi'

const { emitTabRemove, t: $t } = usePageHelper()

const $attrs: any = useAttrs()

// 保存
const $saveData = ($form: any, $queryEngine: any, $message: any, $bus: any) => {
  const params = toJS($form.values)

  $form.validate().then(() => {
    if (params.bomLines.length === 0) {
      // 请选择明细
      return $message.error($t('outsourcingBomNew.prompt1'))
    }
    let arr = params.bomLines.map((item:any) => item.materialCode)
    if (Array.from(new Set(arr)).length < arr.length) {
      // 明细行存在重复的委外组件，请先删除再保存
      return $message.warning($t('outsourcingBomNew.prompt2'))
    }
    saveFetch(params, $queryEngine, $message, $bus)
  }).catch((err: any) => {
    console.log(err, 'err')
  })
}

const saveFetch = (params: any, $queryEngine: any, $message: any, $bus: any) => {
  $queryEngine.request.save(params, { loading: true }).then((res: any) => {
    if (res.data && res.data.length > 0) {
      $message.success($t('common.successSave'))
      $closePageAndRefreshListPageData($bus)
    }
  })
}
const $closePageAndRefreshListPageData = ($bus: any) => {
  $bus.$emit('BomHeadBus')
  emitTabRemove($attrs.tabName)
}

const scope = {
  $attrs,
  $t,
  emitTabRemove,
  $closePageAndRefreshListPageData,
  $saveData
}

const components = {
  OutsourcingBomInfo,
  OutsourcingBomDetail
}

const schema = defineSchemas({
  // 基本信息
  BomHead: {
    type: 'void',
    'x-component': 'FormContainer',
    'x-data': {
      dialogInit: false,
      queryData: {},
      selectedData: {}
    },
    'x-decorator': 'QueryEngine',
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        read: {
          immediate: true,
          ready: expression(`() => {
            $form.readPretty = $attrs.params.flag === 'view'

            return $attrs.params.row.bomHeadId
          }`),
          transformRequest: expression(`(data, headers) => {
            data.payload = [$attrs?.params?.row?.bomHeadId || $form.values.bomHeadId]
            data.query['*'] = {}

            return data
          }`),
          onSuccess: expression(`(res) => {
            $form.setValues({
              ...res.data[0]
            })
                       
          }`)
        },
        save: {
          // 标记当前 action 需要消费底层储存的级联删除数据
          cascadeDeletion: true
        }
      }
    },
    // 按钮操作
    items: {
      type: 'void',
      properties: {
        buttonList: {
          type: 'void',
          'x-component': 'ButtonList',
          properties: {
            cancel: {
              type: 'void',
              title: '{{$form.readPretty ? $t(\'common.close\') : $t(\'common.cancel\') }}',
              'x-component-props': {
                type: 'default',
                '@click': expression(`() => {
                  $closePageAndRefreshListPageData($bus)
                }`)
              }
            },
            // 保存
            save: {
              type: 'void',
              'x-hidden': '{{$form.readPretty}}',
              title: i18nExpression('common.save'),
              'x-component-props': {
                ...buttonListItemVisibleByPermission('outsourcingBomNew:save'),
                type: 'primary',
                '@click': expression(`() => {
                  $saveData($form, $queryEngine, $message, $bus)
                }`)
              }
            }
          }
        }
      }
    },
    properties: {
      collapse: {
        type: 'void',
        'x-component': 'Collapse',
        properties: generateXindexInOrder({
          // 基本信息
          outsourcingBomInfo: {
            ...OutsourcingBomInfo
          },
          // 明细信息
          outsourcingBomDetail: {
            ...OutsourcingBomDetail
          }
        })
      }

    }
  }
})
</script>

<template>
  <RenderEngine
    :pageAttrs="$attrs"
    :schema="schema"
    :scope="scope"
    :components="components"
    schemaKey="BomHeadDetail"
  />
</template>
