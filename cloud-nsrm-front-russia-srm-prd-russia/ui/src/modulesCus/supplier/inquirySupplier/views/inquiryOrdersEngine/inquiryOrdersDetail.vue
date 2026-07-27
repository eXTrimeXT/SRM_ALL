<script setup lang="ts">
import { useAttrs } from 'vue'
import { RenderEngine } from 'lib@/components/render-engine'
import {
  defineSchemas,
  generateCharExpressionByFunction,
  generateCharFunctionExpression,
  i18nExpression
} from '@meicloud/render-engine'
// @ts-ignore
import { FormCollapse, FormTab } from '@meicloud/render-pix'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import { SOU_SCORE_RULE_TYPE_ENUM, SOU_ORDER_TYPE_ENUM } from 'lib@/compositionEngine/sourcing/enum'
import { AttrsParams } from 'lib@/compositionEngine/sourcing/types'
import DetailHeaderSegment from 'lib@/compositionEngine/inquiry/quoteDetail/detailHeader'
import BaseInfoSegment from './inquiryOrdersDetail/baseInfo'
import EnclosuresWrapSegment from './inquiryOrdersDetail/enclosuresWrap'
import QuoteCurrencySegment from 'lib@/compositionEngine/sourcing/quoteCurrency'
import ContactInfoSegment from 'lib@/compositionEngine/sourcing/contactInfo'
import ItemInfoSegment from 'lib@/compositionEngine/inquiry/quoteDetail/itemInfo'

const attrs: any = useAttrs()
const attrsParams: AttrsParams = attrs.params
const { app, emitTabRemove, getCurrentUserInfo, t: $t } = usePageHelper()

// 注入作用域
const scope = {
  app,
  emitTabRemove,
  getCurrentUserInfo,
  $attrsParams: attrsParams,
  $projectId: attrsParams.flag === 'add' ? '' : attrsParams.row.projectId,
  $readonly: attrsParams.readonly,
  // 字典枚举
  $enum: {
    SOU_SCORE_RULE_TYPE_ENUM,
    SOU_ORDER_TYPE_ENUM
  }
}

const components = {
  FormCollapse,
  FormTab
}

const schema = defineSchemas({
  InqSouOrderForVendor: {
    type: 'void',
    'x-component': 'FormContainer',
    'x-decorator': 'QueryEngine',
    'x-query-engine': {
      service: 'sou',
      actions: {
        read: {
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
            data.payload = [$form.values.projectId]
            data.tree = true
            data.query = {
              '*': {},
              souProject: { '*': {} },
              souFileList: { '*': {} },
              currencyList: { '*': {} }
            }

            return data
          }),
          onSuccess: generateCharFunctionExpression(({ $form }, response) => {
            const value = response.data[0]

            // TODO 后端把子实体查询主实体的关联关系给映射成 1:N，目前先由前端中转一下
            value.souProject = Array.isArray(value.souProject) ? value.souProject[0] : value.souProject

            $form.setValues({ ...value })
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
          // 项目信息
          baseInfo: {
            type: 'void',
            'x-component': 'CollapseItem',
            'x-component-props': {
              title: i18nExpression('bidMod.inquiryInfo')
            },
            properties: {
              ...BaseInfoSegment
            }
          },

          // 商务信息
          quoteCurrency: {
            type: 'void',
            'x-component': 'CollapseItem',
            'x-component-props': {
              title: i18nExpression('bidMod.businessInfo')
            },
            properties: {
              ...QuoteCurrencySegment({ isVendorView: true })
            }
          },

          // 查看附件
          enclosuresWrap: {
            type: 'void',
            'x-component': 'CollapseItem',
            'x-component-props': {
              title: i18nExpression('bidMod.fileList')
            },
            properties: {
              ...EnclosuresWrapSegment
            }
          },

          // 联系方式
          contactInfo: {
            type: 'void',
            'x-component': 'CollapseItem',
            'x-component-props': {
              title: i18nExpression('bidMod.contactInfo')
            },
            properties: {
              ...ContactInfoSegment({ setDefault: false })
            }
          },

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
    items: {
      type: 'void',
      properties: {
        // 取消，返回
        goBack: {
          type: 'void',
          'x-content': generateCharExpressionByFunction(({ $readonly }) => $t($readonly ? 'common.backTo' : 'components.common.cancel')),
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
    schemaKey="inquiryOrdersDetail"
    :schema="schema"
    :scope="scope"
    :components="components"
  />
</template>
