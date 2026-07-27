import {expression, i18nExpression} from '@meicloud/render-engine'

import {
  formGridSegment
} from 'lib@/components/render-engine/schema-segments'

export const formMain = {
  type: 'object',
  'x-query-engine-skip': true,
  ...formGridSegment,
  properties: {
    changeId: {
      type: 'number',
      'x-hidden': true,
      'x-decorator': 'FormItem'
    },
    changeStatus: {
      type: 'string',
      'x-hidden': true,
      'x-decorator': 'FormItem'
    },
    changeApplyNo: {
      type: 'string',
      title: i18nExpression('vendorMod.changeApplyNo'), // 变更单号
      'x-component-props': {
        disabled: true
      },
      'x-decorator': 'FormItem'
    },
    companyId: {
      type: 'number',
      'x-hidden': true
    },
    companyCode: {
      type: 'string',
      'x-hidden': true
    },
    companyName: {
      type: 'string',
      title: i18nExpression('common.vendorName'), // 供应商名称
      'x-component': 'QuickSearchWrapper',
      'x-hidden': true,
      'x-component-props': {

      },
      'x-decorator': 'FormItem',
      'x-validator': {
        required: true,
        message: i18nExpression('common.requiredField')
      }
    },
    noticeById: {
      type: 'string',
      'x-hidden': true,
      'x-decorator': 'FormItem'
    },
    noticeByName: {
      type: 'string',
      title: i18nExpression('vendorMod.noticeByName'), // 通知业务人员
      'x-hidden': expression(`$buyer()`),
      'x-component-props': {
        disabled: true
      },
      'x-decorator': 'FormItem'
    },
    enable4mChange: {
      type: 'number',
      title: i18nExpression('vendorMod.enable4MChange'), // 是否是4M变更
      'x-decorator': 'FormItem',
      'x-component': 'DictSelect',
      'x-component-props': {
        code: 'YES_OR_NO'
      },
      'x-validator': {
        required: true,
        message: i18nExpression('common.requiredField')
      }
    },
    changeFileId: {
      type: 'string',
      'x-hidden': true
    },
    changeFileName: {
      type: 'string',
      'x-hidden': true
    },
    changeFile: {
      type: 'string',
      title: i18nExpression('vendorMod.changeFile'), // 变更附件
      'x-component': 'SrmCommonFile',
      'x-component-props': {
        'extra-data': {
          uploadType: 'DEF',
          sourceType: 'WEB_APP',
          fileModular: 'sup',
          fileFunction: 'companyInfoMaintain',
          fileType: 'images'
        },
        'default-file': {
          fileId: expression(`$form.query('.form.changeFileId').take()?.value`),
          fileName: expression(`$form.query('.form.changeFileName').take()?.value`)
        },
        '@on-change': expression(`(file) => {
          if (file) {
            const { fileId, fileName } = file.file || {}
            $form.query('.form.changeFileId').take().value = fileId.toString()
            $form.query('.form.changeFileName').take().value = fileName
          } else {
            $form.query('.form.changeFileId').take().value = null
            $form.query('.form.changeFileName').take().value = null
          }
        }`)
      },
      'x-decorator': 'FormItem'
    },
    changeExplain: {
      type: 'string',
      title: i18nExpression('vendorMod.changeExplain'), // 变更说明
      'x-component': 'Input.TextArea',
      'x-component-props': {
        autosize: { minRows: 3, maxRows: 4 }
      },
      'x-decorator': 'FormItem',
      'x-decorator-props': {
        gridSpan: 4
      },
      'x-validator': {
        required: true,
        message: i18nExpression('common.requiredField')
      }
    }
  }
}
