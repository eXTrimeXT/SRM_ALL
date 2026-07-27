<script setup lang="ts">
import { RenderEngine } from 'lib@/components/render-engine'

import {
  defineSchemas,
  expression
} from '@meicloud/render-engine'
// @ts-ignore
import { useAttrs, computed, ref, defineComponent } from 'vue'
import { ConfigDrectory } from './components/configDrectory'
import { ConfigDetail } from './components/configDetail'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import { deepClone } from '@/utils'

const { http, t: $t } = usePageHelper()

const $getRuleList = (data: any, $form: any) => {
  $form.query('PurchaseConfig').get('data').currentId = data.id
  if (data.id === 10) {
    // 采购订单配置
    $getOrderConfig($form)
  } else if (data.id === 20) {
    // 送货通知单配置
    $getDeliveryNoticeConfig($form)
  } else if (data.id === 2) {
    // 委外管理配置
    $getOutsourcingConfig($form)
  }
}

const $getOrderConfig = ($form: any) => {
  http({
    url: '/api-sup-ce/purchaseConfig/get/order',
    method: 'GET',
    loading: true
  }).then((res: any) => {
    if (res.code === '0') {
      $form.query('PurchaseConfig').get('data').initData = res.data
      $form.query('PurchaseConfig').get('data').ruleList = [
        {
          title: $t('businessRuleConfig.baseRule'), // 基础规则
          content: [{
            value: res.data.configValue.vendorRefuse,
            text: $t('businessRuleConfig.orderRule1'), // 允许供应商拒绝采购订单
            tip: $t('businessRuleConfig.orderRuleTip1') // 勾选后供应商确认订单时，订单数量可以修改为0
          },
          {
            value: res.data.configValue.vendorModifyCount,
            text: $t('businessRuleConfig.orderRule2'), // 允许供应商修改订单数量（仅针对生产性需求）
            tip: $t('businessRuleConfig.orderRuleTip2') // 勾选后供应商确认订单时，可以修改订单数量
          },
          {
            value: res.data.configValue.vendorSplitReply,
            text: $t('businessRuleConfig.orderRule3'), // 允许供应商分批次确认交期
            tip: $t('businessRuleConfig.orderRuleTip3') // 勾选后供应商确认订单时，同一订单行可以分批确认供方承诺到货日期
          }]
        }
        // {
        //   title: 'xx规则',
        //   content: []
        // }
      ]
      $form.query('PurchaseConfig').get('data').initRuleList = deepClone($form.query('PurchaseConfig').get('data').ruleList)
    }
  })
}

const $getOutsourcingConfig = ($form: any) => {
  http({
    url: '/api-sup-ce/purchaseConfig/get/outsourcing',
    method: 'GET',
    loading: true
  }).then((res: any) => {
    if (res.code === '0') {
      $form.query('PurchaseConfig').get('data').initData = res.data
      $form.query('PurchaseConfig').get('data').ruleList = [
        {
          title: $t('businessRuleConfig.baseRule'), // 基础规则
          content: [{
            value: res.data.configValue.materialChange,
            text: $t('businessRuleConfig.outsourcingRule1') // 启用委外用料清单变更单
          },
          {
            value: res.data.configValue.receiveConfirm,
            text: $t('businessRuleConfig.outsourcingRule2') // 启用委外领料单供应商确认功能
          }]
        }
      ]
      $form.query('PurchaseConfig').get('data').initRuleList = deepClone($form.query('PurchaseConfig').get('data').ruleList)
    }
  })
}

const $getDeliveryNoticeConfig = ($form: any) => {
  http({
    url: '/api-sup-ce/purchaseConfig/get/deliveryNotice',
    method: 'GET',
    loading: true
  }).then((res: any) => {
    if (res.code === '0') {
      $form.query('PurchaseConfig').get('data').initData = res.data
      $form.query('PurchaseConfig').get('data').ruleList = [
        {
          title: $t('businessRuleConfig.baseRule'), // 基础规则
          content: [{
            value: res.data.configValue.vendorSplitReply,
            text: $t('businessRuleConfig.deliveryNoticeRule1'), // 允许供应商拆行确认交期
            tip: $t('businessRuleConfig.deliveryNoticeRuleTip1') // 勾选后供应商确认送货通知单时，同一送货单明细行可以拆行确认供方承诺到货日期
          }
          // {
          //   value: res.data.configValue.vendorRowReply,
          //   text: $t('businessRuleConfig.deliveryNoticeRule2'), // 允许供应商按行确认送货通知单
          //   tip: $t('businessRuleConfig.deliveryNoticeRuleTip2') // 勾选后供应商确认送货通知单时，可以按行确认，否则整单进行确认
          // }
          ]
        }
      ]
      $form.query('PurchaseConfig').get('data').initRuleList = deepClone($form.query('PurchaseConfig').get('data').ruleList)
    }
  })
}

const $save = (data: any, $form: any, $message: any) => {
  let params = { ...$form.query('PurchaseConfig').get('data').initData }
  if ($form.query('PurchaseConfig').get('data').currentId === 10) {
    params.configValue.vendorRefuse = data[0].content[0].value
    params.configValue.vendorModifyCount = data[0].content[1].value
    params.configValue.vendorSplitReply = data[0].content[2].value
  } else if ($form.query('PurchaseConfig').get('data').currentId === 20) {
    params.configValue.vendorSplitReply = data[0].content[0].value
    // params.configValue.vendorRowReply = data[0].content[1].value
  } else if ($form.query('PurchaseConfig').get('data').currentId === 2) {
    params.configValue.materialChange = data[0].content[0].value
    params.configValue.receiveConfirm = data[0].content[1].value
  }

  http({
    url: '/api-sup-ce/purchaseConfig/save',
    method: 'POST',
    data: params,
    loading: true
  }).then((res: any) => {
    if (res.code === '0') {
      $message.success($t('common.successSave'))
      $form.query('PurchaseConfig').get('data').initRuleList = deepClone(data)
    }
  })
}

const scope = {
  $getRuleList,
  deepClone,
  $save,
  $getOrderConfig
}

const components = {
  ConfigDrectory,
  ConfigDetail
}

const schema = defineSchemas({
  PurchaseConfig: {
    type: 'void',
    'x-data': {
      currentId: 10,
      ruleList: [],
      initRuleList: [],
      initData: {}
    },
    'x-reactions': expression(`async () => {
      $getOrderConfig($form)
    }`),
    'x-decorator': 'el-container',
    properties: {
      leftContainer: {
        type: 'void',
        'x-component': 'el-aside',
        'x-component-props': {
          width: '200px',
          style: {
            'background-color': '#fff',
            'border-right': '1px solid #DCDDDE',
            'margin-bottom': '0'
          }
        },
        properties: {
          item: {
            type: 'void',
            'x-component': 'ConfigDrectory',
            'x-component-props': {
              '@node-click': expression(`(data) => {
                 $getRuleList(data,$form)
              }`)
            }
          }
        }
      },
      rightContainer: {
        type: 'void',
        'x-component': 'el-container',
        properties: {
          item: {
            type: 'void',
            'x-component': 'ConfigDetail',
            'x-component-props': {
              activeName: 'global',
              ruleList: '{{$form.query(\'PurchaseConfig\').get(\'data\').ruleList}}',
              '@input': expression(`(value) => {
                $self.setComponentProps({ activeName: value })
              }`),
              '@tab-click': expression(`(tabName) => {
              }`),
              '@reset': expression(`() => {
                $form.query('PurchaseConfig').get('data').ruleList = deepClone($form.query('PurchaseConfig').get('data').initRuleList)
              }`),
              '@save': expression(`(data) => {
                $save(data,$form, $message)
              }`)
            }
          }
        }
      }

    }
  }
})

</script>

<template>
  <RenderEngine schemaKey="PurchaseConfig" :schema="schema" :scope="scope" :components="components" />
</template>
