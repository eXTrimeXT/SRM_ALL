import {
  expression,
  i18nExpression
} from '@meicloud/render-engine'
import {
  requiredValidatorSegment,
  formGridSegment,
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'

export default {
  type: 'void',
  'x-component': 'CollapseItem',
  'x-component-props': {
    title: i18nExpression('supRisk.baseInfo') // 基础信息
  },
  'x-query-engine-skip': true,
  'x-read-pretty': expression('$form.readPretty'),
  properties: {
    layout: {
      type: 'void',
      ...formGridSegment,
      properties: {
        orgId: {
          type: 'number',
          'x-decorator': 'FormItem',
          title: i18nExpression('oneStopShopping.businessEntity'), // 业务实体
          'x-component': 'OrganizationSelector',
          'x-component-props': {
            readPretty: '{{$form.readPretty}}',
            'parent-id': -1,
            'node-type': 'OU',
            'select-type': 'input',
            placeholder: i18nExpression('common.pleaseSelect'),
            multiple: false,
            disabled: expression('$form.readPretty ? undefined : !!$form.values.bomLines?.length'),
            '@select': expression(`(node) => {
                $form.values.orgId = node ? node.organizationId : null
                $form.values.orgCode = node ? node.organizationCode : null
                $form.values.orgName = node ? node.organizationName : null
                if($form.values.organizationId){
                  $form.values.organizationId = null
                  $form.values.organizationCode = null
                  $form.values.organizationName = null
                }
                if($form.values.materialId){
                  $form.values.unit = null
                  $form.values.unitName = null
                  $form.values.materialCode = null
                  $form.values.materialName = null
                  $form.values.materialId = null
                }
              }`)
          },
          ...requiredValidatorSegment
        },
        organizationId: {
          type: 'number',
          'x-decorator': 'FormItem',
          title: i18nExpression('dataConfMod.organizationId'), // 库存组织
          'x-component': 'OrganizationSelector',
          'x-component-props': {
            readPretty: '{{$form.readPretty}}',
            'node-type': 'INV',
            'select-type': 'input',
            placeholder: i18nExpression('common.pleaseSelect'),
            multiple: false,
            'parent-id': expression('$form.values.orgId || -1'),
            disabled: expression('$form.readPretty ? undefined : !!$form.values.bomLines?.length'),
            '@select': expression(`(node) => {
                $form.values.organizationId = node ? node.organizationId : null
                $form.values.organizationCode = node ? node.organizationCode : null
                $form.values.organizationName = node ? node.organizationName : null
                if($form.values.materialId){
                  $form.values.unit = null
                  $form.values.unitName = null
                  $form.values.materialCode = null
                  $form.values.materialName = null
                  $form.values.materialId = null
                }
              }`)
          },
          ...requiredValidatorSegment
        },
        materialCode: {
          type: 'string',
          title: i18nExpression('orderMod.buyerOrderSynergy.materialCode'), // 物料编码
          'x-decorator': 'FormItem',
          'x-component': 'QuickSearchWrapper',
          'x-component-props': {
            readPretty: '{{$form.readPretty}}',
            showKey: 'materialCode',
            propKey: 'materialCode',
            'name': 'scc_base_material_item_bom',
            disabled: '{{!$form.values.organizationId || !!$form.values.bomLines?.length}}',
            preQueryData: '{{{ \'invOrgCodes\': [$form.values.organizationCode], enabled: \'Y\', \'materialType\': \'OP\' }}}',
            '@close-quicksearch': expression(`(val, scope) => {
              $form.values.unit = val ? val.unit : ''
              $form.values.unitName = val ? val.unitName : ''
              $form.values.materialCode = val ? val.materialCode : ''
              $form.values.materialName = val ? val.materialName : ''
              $form.values.materialId = val ? val.materialId : ''
            }`)
          },
          ...requiredValidatorSegment
        },
        materialName: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('orderMod.buyerOrderSynergy.materialName'), // 物料名称
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')
          }
        },
        unitName: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('orderMod.buyerOrderSynergy.unit'), // 单位
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')
          }
        },
        versionCode: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('outsourcingBomNew.versionCode'), // 版本号
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')
          }
        },
        status: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('outsourcingBomNew.isValid'), // 是否生效
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'YES_OR_NO'
          },
          ...requiredValidatorSegment
        },
        createdFullName: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('common.creator'), // 创建人
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')
          }
        },
        creationDate: {
          'x-decorator': 'FormItem',
          title: i18nExpression('common.creationDate'), // 创建日期,
          ...yearMonthDaySelectorSegment,
          'x-component-props': {
            ...yearMonthDaySelectorSegment['x-component-props'],
            disabled: expression('$form.readPretty ? undefined : true')
          }
        },
        bomDetailDescription: {
          type: 'string',
          title: i18nExpression('outsourcingBomNew.bomDetailDescription'), // BOM明细说明
          'x-decorator': 'FormItem',
          'x-decorator-props': {
            gridSpan: 4
          },
          'x-component-props': {
            type: 'textarea',
            autosize: { minRows: 2, maxRows: 4 },
            maxlength: 50,
            showWordLimit: true
          }

        }
      }
    }
  }
}
