import {
  expression,
  i18nExpression,
  generateXindexInOrder
} from '@meicloud/render-engine'

import {
  feedbackLayoutIsPopover,
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'

export default {
  type: 'void',
  'x-component': 'CollapseItem',
  'x-component-props': {
    title: "{{ $t('perfMod.improveNo2') }}"
  },
  properties: {
    companyDemotionImproveList: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-component-props': {
        class: 'table-view-vxe-table',
        editMode: true,
        preColumns: expression('$form.readPretty ? \'seq\' : \'seq\''),
        pagination: false,
        sortable: false,
        // 联表主键的 key
        primaryKey: 'companyDemotionId',
        // 启用级联删除的储值行为
        cascadeDeletion: true
      },
      'x-query-engine-skip': true,
      'x-read-pretty': true,
      'x-query-engine-relation': 'companyDemotionImproveList:*',
      properties: generateXindexInOrder({
        companyDemotionId: {
          type: 'string',
          'x-hidden': true

        },
        organizationName: {
          type: 'string',
          title: "{{$t('bidMod.businessEntity')}}",
          'x-render-table-column': {
            minWidth: 120
          }
        },
        categoryName: {
          type: 'string',
          title: "{{$t('relegationEntity.key12')}}",
          'x-render-table-column': {
            minWidth: 120
          }
        },
        improveTitle: {
          type: 'string',
          title: "{{$t('perfMod.improveTitle')}}",
          'x-render-table-column': {
            minWidth: 120
          }
        },
        improveProject: {
          type: 'string',
          'x-read-pretty': false,
          'x-component': 'TableButton',
          'x-component-props': {
            type: 'text',
            '@click': expression('({ row }) => {$goImprove(row)}'),
          },
          'x-render-table-column': {
            title: "{{$t('perfMod.improveProject')}}",
            minWidth: 120,
            customRender: true,
          }
        },
        explanation: {
          type: 'string',
          title: "{{$t('bidMod.illustrate')}}",
          'x-render-table-column': {
            minWidth: 120
          }
        },
        respFullName: {
          type: 'string',
          title: i18nExpression('relegationEntity.key22'),
          'x-render-table-column': {
            minWidth: 120
          }
        },
        status: {
          type: 'string',
          title: "{{$t('priceFormula.formulaStatus')}}",
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'VENDOR_IMPROVE_STATUS',
          },
          'x-render-table-column': {
            minWidth: 120
          }
        },
      })
    }
  }
}
