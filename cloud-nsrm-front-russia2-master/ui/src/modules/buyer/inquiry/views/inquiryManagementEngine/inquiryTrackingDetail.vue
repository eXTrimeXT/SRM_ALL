<script setup lang="ts">
import $dayjs from 'dayjs'
import { useAttrs } from 'vue'
import { RenderEngine } from 'lib@/components/render-engine'
import {
  defineSchemas,
  i18nExpression,
  generateCharFunctionExpression,
  generateCharExpressionByFunction,
  changeFieldVisibleByDeps
} from '@meicloud/render-engine'
import { FormTab, FormStep } from '@meicloud/render-pix'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import {
  SOU_SCORE_RULE_TYPE_ENUM,
  SOU_ORDER_TYPE_ENUM,
  SOU_PUBLISH_SCOPE_ENUM,
  SOU_PROJECT_STATUS_ENUM
} from 'lib@/compositionEngine/sourcing/enum'
import { AttrsParams } from 'lib@/compositionEngine/sourcing/types'
import { Echarts } from 'lib@/components/srm-components/echarts'
import { $arrangeOnSetChartData } from 'lib@/compositionEngine/sourcing/quoteRecordChart'
// @ts-ignore
import { downloadFileLink } from 'lib@/utils/file'
import { inqBuyerHttp } from 'modb@/inquiry/apiEngine'
import InquiryStepsSegment from './inquiryTrackingDetail/inquirySteps'
import BaseInfoSegment from './inquiryTrackingDetail/baseInfo'
import QuoteTrackingSegment from './inquiryTrackingDetail/quoteTracking'
import QuoteSelectionSegment, { getSearchInfo, quoteSelectionTableRowClick } from './inquiryTrackingDetail/quoteSelection'
import EditQuoteTimeSegment from './inquiryTrackingDetail/editQuoteTime'

const attrs: any = useAttrs()
const attrsParams: AttrsParams = attrs.params
const { emitTabRemove, emitTabAdd } = usePageHelper()

// 注入作用域
const scope = {
  $attrsParams: attrsParams,
  $projectId: attrsParams.flag === 'add' ? '' : (attrsParams.row.projectId || ''),
  $readonly: !!attrsParams.readonly,
  $dayjs,
  $emitTabRemove: emitTabRemove,
  $emitTabAdd: emitTabAdd,
  // 字典枚举
  $enum: {
    SOU_SCORE_RULE_TYPE_ENUM,
    SOU_ORDER_TYPE_ENUM,
    SOU_PUBLISH_SCOPE_ENUM,
    SOU_PROJECT_STATUS_ENUM
  },
  // http api
  $inqBuyerHttp: inqBuyerHttp,
  // 注入代码片段的方法
  $downloadFileLink: downloadFileLink,
  $getSearchInfo: getSearchInfo,
  $quoteSelectionTableRowClick: quoteSelectionTableRowClick,
  $arrangeOnSetChartData
}

const components = {
  FormTab,
  FormStep,
  Echarts
}

const schema = defineSchemas({
  InqSouProjectForBuyer: {
    type: 'void',
    'x-component': 'FormContainer',
    'x-decorator': 'QueryEngine',
    'x-query-engine': {
      service: 'sou',
      actions: {
        read: {
          immediate: true,
          action: 'queryInqOrderManagement',
          tree: true,
          autoRelationTableMappingConversion: false,
          // 返回false不会执行查询
          ready: generateCharFunctionExpression(({ $form, $readonly, $projectId }) => {
            $form.readPretty = $readonly
            $form.values.projectId = $projectId
            return !!$projectId
          }),
          transformRequest: generateCharFunctionExpression(({ $form, $projectId }, data) => {
            data.payload = [$projectId]
            data.query = {
              '*': {}
            }

            return data
          }),
          onSuccess: generateCharFunctionExpression(({ $form }, response) => {
            const value = response.data[0]
            $form.setValues({ ...value })
          })
        }
      }
    },

    'x-data': {
      // 报价时间动态截止
      isDeadline: false
    },

    properties: {
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'getTrackingDetail',
          '@listener': generateCharFunctionExpression(({ $queryEngine }) => {
            $queryEngine.request.read()
          })
        }
      },

      // 顶部按钮
      toolbar: {
        type: 'void',
        'x-component': 'Space',
        'x-component-props': {
          style: 'margin-bottom: 16px; justify-content: end;'
        },
        properties: {
          // 立即开始
          start: {
            type: 'void',
            title: this.$t('bidMod.startNow'),  // '立即开始'
            // 报价未开始
            'x-reactions': changeFieldVisibleByDeps(
              ['header.extProjectStatus'],
              '$deps[0] === $enum.SOU_PROJECT_STATUS_ENUM.ORDER_NOT_START'
            ),
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              '@click': generateCharFunctionExpression(async ({ $queryEngine, $projectId, $message }) => {
                const response = await $queryEngine.request.baseRequest({
                  action: 'changeOrderStartTime',
                  payload: [
                    {
                      // 询价单ID
                      projectId: $projectId,
                      // 是否立即结束(Y/N)
                      startNow: true,
                      souType: 'inq'
                    }
                  ],
                  query: {}
                }).catch(() => {})

                if (response) {
                  $message.success(i18nExpression('bidMod.changeQuoteTime'))  // '修改报价开始时间成功!'
                  $queryEngine.request.read()
                }
              })
            }
          },
          // 立即结束
          end: {
            type: 'void',
            title: i18nExpression('bidMod.endNow'),  // '立即结束'
            // isDeadline 已截止不显示
            'x-visible': generateCharExpressionByFunction(({ $form }) => !$form.query('InqSouProjectForBuyer').get('data').isDeadline),
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              '@click': generateCharFunctionExpression(async ({ $queryEngine, $projectId, $message }) => {
                const response = await $queryEngine.request.baseRequest({
                  action: 'changeOrderEndTime',
                  payload: [
                    {
                      // 询价单ID
                      projectId: $projectId,
                      // 是否立即结束(Y/N)
                      endNow: true,
                      souType: 'inq'
                    }
                  ],
                  query: {}
                }).catch(() => {})

                if (response) {
                  $message.success(i18nExpression('cusEntry.supplement20250211.quoteSubmissionSuccess'))  // '报价截止成功!'
                  $queryEngine.request.read()
                }
              })

            }
          },
          // 修改报价时间
          editQuoteTime: {
            type: 'void',
            title: i18nExpression('bidMod.editQuoteTime'),
            // isDeadline 已截止不显示
            'x-visible': generateCharExpressionByFunction(({ $form }) => !$form.query('InqSouProjectForBuyer').get('data').isDeadline),
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              '@click': generateCharFunctionExpression(({ $form }) => {
                $form.query('editQuoteTimeDialog').take(field => {
                  field.setComponentProps({ visible: true })
                })
              })
            }
          },
          // 刷新
          refresh: {
            type: 'void',
            title: i18nExpression('common.refresh'),
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              '@click': generateCharFunctionExpression(({ $queryEngine }) => {
                $queryEngine.request.read()
              })
            }
          },
          // 返回
          goBack: {
            type: 'void',
            title: i18nExpression('common.backTo'),
            'x-component': 'RButton',
            'x-component-props': {
              '@click': generateCharFunctionExpression(({ $emitTabRemove, $attrsParams }) => {
                $emitTabRemove($attrsParams.tabName)
              })
            }
          }
        }
      },

      // 步骤条
      ...InquiryStepsSegment,

      // 报价截止倒计时
      dynamicCutoffTime: {
        type: 'void',
        'x-decorator': 'div',
        'x-component': 'DynamicCutoffTime',
        'x-component-props': {
          label: i18nExpression('bidMod.inDeadlineInfo2'),
          deadlineTime: generateCharExpressionByFunction(({ $values }) => {
            return $values.header?.orderEndTime || ''
          }),
          '@isDeadline': generateCharFunctionExpression(({ $form }, value) => {
            const state = $form.query('InqSouProjectForBuyer').get('data')
            state.isDeadline = value
          })
        }
      },

      // 详细信息区域
      ...BaseInfoSegment,

      // 折叠面板
      projectInfoCollapse: {
        type: 'void',
        'x-component': 'Collapse',
        'x-component-props': {
          defaultOpenPanelCount: 1
        },
        properties: {
          // 报价跟踪
          baseInfo: {
            type: 'void',
            'x-component': 'CollapseItem',
            'x-component-props': {
              title: i18nExpression('bidMod.quoteTrack')
            },
            properties: {
              ...QuoteTrackingSegment(scope)
            }
          },

          // 评选
          quoteSelection: {
            type: 'void',
            'x-visible': generateCharExpressionByFunction(({ $values, $enum }) => {
              return [
                $enum.SOU_PROJECT_STATUS_ENUM.ORDER_END,
                $enum.SOU_PROJECT_STATUS_ENUM.EVALUATING,
                $enum.SOU_PROJECT_STATUS_ENUM.PRICING,
                $enum.SOU_PROJECT_STATUS_ENUM.PRICE_END,
                $enum.SOU_PROJECT_STATUS_ENUM.PRICE_REJECT
              ].includes($values.header?.extProjectStatus)
            }),
            'x-component': 'CollapseItem',
            'x-component-props': {
              title: i18nExpression('bidMod.quoteEvaluation')
            },
            properties: {
              ...QuoteSelectionSegment({ scope, components })
            }
          }
        }
      },

      // 修改报价结束时间弹窗
      ...EditQuoteTimeSegment
    }
  }
})
</script>

<template>
  <RenderEngine
    schemaKey="inquiryTrackingDetail"
    :schema="schema"
    :scope="scope"
    :components="components"
  />
</template>
