<script setup lang="ts">
import $dayjs from 'dayjs'
import { useAttrs } from 'vue'
import { RenderEngine } from 'lib@/components/render-engine'
import {
  defineSchemas,
  generateCharFunctionExpression,
  generateCharExpressionByFunction,
  i18nExpression,
  ViewModel,
  expression
} from '@meicloud/render-engine'
// @ts-ignore
import { FormCollapse, FormTab } from '@meicloud/render-pix'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import { AttrsParams } from 'lib@/compositionEngine/sourcing/types'
import { bigCalcTaxPrice } from 'lib@/compositionEngine/sourcing/unit'
import { saveQuoteOrLadderPrice } from 'lib@/compositionEngine/inquiry/ladderPrice'
// @ts-ignore
import { sceneFileCompApi } from '@/api/fileApi'
import DetailHeaderSegment from 'lib@/compositionEngine/inquiry/quoteDetail/detailHeader'
import ItemInfoSegment from 'lib@/compositionEngine/inquiry/quoteDetail/itemInfo'

const attrs: any = useAttrs()
const attrsParams: AttrsParams = attrs.params
const { app, emitTabRemove: $emitTabRemove, getCurrentUserInfo, t: $t } = usePageHelper()

/* 编排提交参数 */
const arrangeParams = ($form: ViewModel) => {
  // 组装后端需要的数据结构
  return {
    projectId: $form.values.projectId,
    orderId: $form.values.order?.orderId || '',
    orderItemList: $form.values.orderItemList.map((item: any) => {
      console.log(item, 'item')
      return {
        ...item,
        paymentList: item.paymentList || [],
        // 阶梯价
        ...(item.isLadder === 'Y' ? { ladderPriceList: item.ladderPriceList } : {}),
        inqSouOrderItem: {}
      }
    })
  }
}

/* 保存报价 */
const $saveOrders = async ($form: ViewModel, $queryEngine: any, $message: any) => {
  const params = arrangeParams($form)
  console.log(params, 'params')
  const response = await $queryEngine.request.baseRequest({
    action: 'editOrder',
    payload: [{ tempSave: true, orderNoGenerateCode: 'SEQ_SOU_INQ_ORDER_NO', ...params }],
    query: {}
  }).catch(() => {})

  if (response) {
    $message.success($t('common.successSave'))
    $queryEngine.request.read()
  }
}

/* 提交报价 */
const $submitOrders = async ($form: ViewModel, $queryEngine: any, $confirm: any, $bus: any, $message: any) => {
  const params = arrangeParams($form)
  console.log(params, 'params')
  const confirmResult = await $confirm('请确认是否要提交报价？', {
    confirmButtonText: $t('common.confirm'),
    cancelButtonText: $t('common.cancel'),
    type: 'warning'
  }).catch(() => { /* nothing */ })

  if (confirmResult !== 'confirm') {
    return
  }

  const response = await $queryEngine.request.baseRequest({
    action: 'editOrder',
    payload: [{ tempSave: false, orderNoGenerateCode: 'SEQ_SOU_INQ_ORDER_NO', ...params }],
    query: {}
  }).catch(() => {})

  if (response) {
    $message.success($t('common.successSubmit'))
    $bus.$emit('paginationRefresh')
    $emitTabRemove(attrsParams.tabName)
  }
}

// 获取立项信息
const $getProjectInfo = async ($form: any, $queryEngine: any) => {
  let timer: any = null
  if ($form.values?.orderItemList?.length > 0) {
    const res = await $queryEngine.request.baseRequest({
      type: 'InqSouProjectForVendor',
      action: 'getProjectInfo',
      payload: [{ projectId: $form.values.projectId }],
      query: {}
    }).catch(() => {})

    $form.query('InqSouOrderForVendor').take().setData({ projectInfo: res.data[0] })

    clearTimeout(timer)
  } else {
    timer = setTimeout(() => {
      $getProjectInfo($form, $queryEngine)
    }, 300)
  }
}

// 注入作用域
const scope = {
  app,
  $emitTabRemove,
  getCurrentUserInfo,
  $dayjs,
  $attrsParams: attrsParams,
  $projectId: attrsParams.row.projectId,
  $readonly: attrsParams.readonly,
  $saveOrders,
  $submitOrders,
  // 注入工具方法
  $bigCalcTaxPrice: bigCalcTaxPrice,
  // 注入子代码块方法
  $saveQuoteOrLadderPrice: saveQuoteOrLadderPrice,
  // http api
  $sceneFileCompApi: sceneFileCompApi,
  $getProjectInfo
}

const components = {
  FormTab
}

const schema = defineSchemas({
  InqSouOrderForVendor: {
    type: 'void',
    'x-component': 'QueryEngine',
    // 'x-decorator': 'FormContainer',
    'x-component-props': {
      '@created': `{{(queryEngine) => {
        console.log(queryEngine, "created")
        console.log($form.query('InqSouOrderForVendor').take(),'InqSouOrderForVendor form')
        console.log($self.query('InqSouOrderForVendor').take(),'InqSouOrderForVendor self')
        $getProjectInfo($form, queryEngine)
      }}}`
    },
    'x-data': {
      projectInfo: {}
    },
    'x-query-engine': {
      service: 'sou',
      actions: {
        read: {
          action: 'getSouOrderInfo',
          immediate: true,
          tree: true,
          autoRelationTableMappingConversion: false,
          // 返回false不会执行查询
          ready: generateCharFunctionExpression(({ $form, $readonly, $attrsParams }) => {
            $form.readPretty = $readonly
            $form.values.projectId = $attrsParams?.row?.souProject || $attrsParams?.row?.projectId
            return !!$form.values.projectId
          }),
          transformRequest: generateCharFunctionExpression(({ $form }, data) => {
            data.payload = [
              { projectId: $form.values.projectId }
            ]
            data.query = {
              '*': {}
            }

            return data
          }),
          onSuccess: generateCharFunctionExpression(async ({ $form, $queryEngine }, response) => {
            const value = response.data[0]
            $form.setValues({ ...value })
            console.log(value, 'onSuccess => $value')
          })
        }
      }
    },
    properties: {
      // 详情头
      ...DetailHeaderSegment(),

      projectInfoCollapse: {
        type: 'void',
        'x-component': 'Collapse',
        'x-component-props': {
          defaultOpenPanelCount: 1
        },
        properties: {
          // 物料信息 共用
          itemInfo: {
            type: 'void',
            'x-component': 'CollapseItem',
            'x-component-props': {
              title: i18nExpression('bidMod.itemInfo')
            },
            properties: {
              ...ItemInfoSegment(scope)
            }
          }
        }
      }
    },
    // 底部按钮
    items: {
      type: 'object',
      'x-visible': generateCharExpressionByFunction(({ $attrsParams }) => $attrsParams.flag !== 'approve'),
      properties: {
        // 保存
        save: {
          type: 'void',
          'x-content': i18nExpression('common.save'),
          'x-component': 'Button',
          'x-component-props': {
            type: 'primary',
            '@click': generateCharFunctionExpression(({ $form, $queryEngine, $message }) => {
              $saveOrders($form, $queryEngine, $message)
            })
          }
        },
        // 提交
        submit: {
          type: 'void',
          'x-content': i18nExpression('common.submit'),
          'x-component': 'Button',
          'x-component-props': {
            type: 'primary',
            '@click': generateCharFunctionExpression(({ $form, $queryEngine, $confirm, $bus, $message }) => {
              $submitOrders($form, $queryEngine, $confirm, $bus, $message)
            })
          }
        },
        // 取消，返回
        goBack: {
          type: 'void',
          'x-content': i18nExpression('common.backTo'),
          'x-component': 'Button',
          'x-component-props': {
            type: 'default',
            '@click': generateCharFunctionExpression(({ $attrsParams, $emitTabRemove }) => {
              $emitTabRemove($attrsParams.tabName)
            })
          }
        }
      }
    }
  }
})
</script>

<template>
  <RenderEngine
    schemaKey="inquiryOrdersQuote"
    :schema="schema"
    :scope="scope"
    :components="components"
  />
</template>
