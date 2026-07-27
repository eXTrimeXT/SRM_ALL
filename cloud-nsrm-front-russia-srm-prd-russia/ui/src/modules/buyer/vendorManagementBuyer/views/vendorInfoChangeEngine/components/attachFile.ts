import {expression, generateXindexInOrder, i18nExpression} from '@meicloud/render-engine'

import {
  checkboxByYOrNSegment,
  formGridSegment, radioGroupByYOrNSegment, requiredValidatorSegment
} from 'lib@/components/render-engine/schema-segments'

export const attachFile = {
  beforeChange: {
    type: 'void',
    'x-component': 'div',
    'x-component-props': {
      class: ''
    },
    properties: {
      // 变更前
      beforeChangeTitle: {
        type: 'void',
        'x-component': 'changeTitle',
        'x-component-props': {
          language: 'supplierChange.beforeChange'
        }
      },
      attachFileBefore: {
        type: 'array',
        'x-component': 'FileDynamic',
        'x-component-props': {
          'scene-module-code': "SCENE_SUPPLIER_ATTACHMENT",
          'businessId': expression(`$attrs.params.companyId || null`),
          'editable': false,
          'need-init': false
        }
      }
    }
  },
  afterChange: {
    type: 'void',
    'x-component': 'div',
    'x-component-props': {
      class: ''
    },
    properties: {
      // 变更后
      afterChangeTitle: {
        type: 'void',
        'x-component': 'changeTitle',
        'x-component-props': {
          language: 'supplierChange.afterChange'
        }
      },
      attachFileAfter: {
        type: 'array',
        'x-component': 'FileDynamic',
        'x-component-props': {
          'scene-module-code': "SCENE_SUPPLIER_ATTACHMENT",
          'businessId': expression(`$attrs.params?.changeId`),
          'editable': expression(`$attrs.params.flag != 'view'`),
          'need-init': true
        }
      }
    }
  }
}
