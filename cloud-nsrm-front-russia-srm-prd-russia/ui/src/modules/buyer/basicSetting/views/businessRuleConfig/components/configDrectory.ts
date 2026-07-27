import './configDrectory.scss'

import { h } from '@meicloud/render-engine'
import { defineComponent, getCurrentInstance, watch, ref } from 'vue-demi'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import { orderConfig } from '@/config/orderConfig'

export const ConfigDrectory = defineComponent({
  name: 'ConfigDrectory',
  setup (props, { attrs, slots, listeners }) {
    const { t: $t } = usePageHelper()
    // 获取当前实例
    const instance = getCurrentInstance()

    const inputValue = ref('')

    const data = [{
      label: $t('businessRuleConfig.configText1'), // 订单履行配置
      id: 1,
      children: [{
        label: $t('businessRuleConfig.configText2'), // 采购订单
        id: 10
      }]
    },
    {
      label: $t('businessRuleConfig.configText4'), // 委外管理
      id: 2
    }]
    if (orderConfig.deliveryNoticeByRow === 'Y') {
      // @ts-ignore
      data[0].children.push({
        label: $t('businessRuleConfig.configText3'), // 送货通知单
        id: 20
      })
    }

    const propsValue = {
      children: 'children',
      label: 'label'
    }

    const filterNode = (value:any, data:any) => {
      if (!value) return true
      return data.label.indexOf(value) !== -1
    }

    watch(
      inputValue,
      val => {
        const treeNode = instance?.proxy?.$children[1]
        if (treeNode) {
          treeNode?.filter(val)
        }
      },
      { immediate: true },
    )

    return () => {
      return h(
        'div',
        {
          class: 'c-config-drectory-container'
        },
        {
          default: () => [
            h(
              'p',
              {
                class: 'c-config-drectory-container__title'
              },
              {
                default: () => $t('businessRuleConfig.configDrectory') // '配置目录'
              },
            ),
            h(
              'el-input',
              {
                attrs: {
                  placeholder: $t('businessRuleConfig.prompt1'), // 请输入关键词搜索
                  'prefix-icon': 'el-icon-search',
                  value: inputValue.value
                },
                on: {
                  input: (value:any) => {
                    inputValue.value = value
                  }
                }

              },
              {},
            ),
            h(
              'el-tree',
              {
                attrs: { props: propsValue, data, 'filter-node-method': filterNode, 'node-key': 'id', 'highlight-current': true, 'current-node-key': 10, 'default-expand-all': true },
                on: {
                  ...listeners
                }

              },
              {},
            )
          ]
        },
      )
    }
  }
})
