import './configDetail.scss'

import { h } from '@meicloud/render-engine'
import { defineComponent, ref } from 'vue-demi'
// @ts-ignore
import { deepClone } from '@/utils'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'

export const ConfigDetail = defineComponent({
  name: 'ConfigDetail',
  props: {
    activeName: [String, Number],
    ruleList: {
      type: Array,
      default: () => []
    }
  },
  setup (props, { attrs, slots, listeners }) {
    const { t: $t } = usePageHelper()

    const tabList = [
      {
        label: $t('businessRuleConfig.tabText1'), // 全局
        name: 'global'
      }
      // {
      //   label: $t('businessRuleConfig.tabText2'), // 按组织
      //   name: 'organization'
      // }
    ]
    const activeNameValue = computed(() => props.activeName)

    const ruleListValue:any = ref([])
    watch(
      () => props.ruleList,
      (val) => {
        ruleListValue.value = deepClone(val)
      },
      { deep: true }
    )

    return () => {
      return h(
        'div',
        {
          class: 'c-config-detail-container'
        },

        {
          default: () => [
            h(
              'el-tabs',
              {
                attrs: {
                  value: activeNameValue.value
                },
                on: {
                  ...listeners
                }
              },
              {
                default: () => tabList.map((item:any) => {
                  return h(
                    'el-tab-pane',
                    {
                      attrs: { ...item }
                    },
                    {}
                  )
                })
              },
            ),

            h(
              'div',
              {
                class: 'rule-block'
              },

              {
                default: () => ruleListValue.value.map((item:any) => [
                  h(
                    'div',
                    {
                      class: 'rule-item'
                    },
                    {
                      default: () => [
                        h(
                          'div',
                          {
                            class: 'rule-item__title'
                          },
                          {
                            default: () => item.title
                          }
                        ),
                        item.content.map((node:any) => [
                          h(
                            'div',
                            {
                              class: 'rule-item__content'
                            },
                            {
                              default: () => [
                                h(
                                  'el-checkbox',
                                  {
                                    attrs: { value: node.value },
                                    on: {
                                      input: (value:any) => {
                                        node.value = value
                                      }
                                    }
                                  },
                                  {

                                    default: () => node.text
                                  }
                                ),
                                h(
                                  'el-tooltip',
                                  {
                                    attrs: { effect: 'dark', content: node.tip, placement: 'top' },
                                    style: {
                                      display: node?.tip ? 'inline-block' : 'none'
                                    }
                                  },
                                  {
                                    default: () => {
                                      return h(
                                        'span',
                                        {
                                          class: 'rule-item__tip'
                                        },
                                        {
                                          default: () => '?'
                                        }
                                      )
                                    }
                                  }
                                )
                              ]

                            }
                          )
                        ])
                      ]
                    }
                  )
                ])
              },
            ),

            h(
              'div',
              {
                class: 'config-btn-block'
              },
              {
                default: () => [
                  h(
                    'el-button',
                    {
                      on: {
                        click: () => {
                          listeners?.reset()
                        }
                      }
                    },
                    {
                      default: () => $t('common.reset') // '重置'
                    }
                  ),
                  h(
                    'AuthorityButton',
                    {
                      attrs: { type: 'primary', code: 'businessRuleConfig:save' },
                      on: {
                        click: () => {
                          listeners?.save(ruleListValue.value)
                        }
                      }
                    },
                    {
                      default: () => $t('common.save') // '保存'
                    }
                  )
                ]
              }
            )
          ]
        },
      )
    }
  }
})
