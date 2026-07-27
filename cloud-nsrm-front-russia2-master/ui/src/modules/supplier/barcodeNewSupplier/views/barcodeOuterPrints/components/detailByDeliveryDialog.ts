import {
  expression,
  i18nExpression,
  generateXindexInOrder
} from '@meicloud/render-engine'

import {
  requiredValidatorSegment
} from 'lib@/components/render-engine/schema-segments'

export default {
  type: 'void',
  title: i18nExpression('orderMod.createOuterBoxBarcodes'), // 创建外箱条码
  'x-component': 'RDialog',
  'x-component-props': {
    size: 'middle',
    'close-on-click-modal': false,
    class: 'the-barcodePrint',
    footerButtonList: expression(`(_, { cancelButton,okButton }) => {

        return [
          cancelButton,
          {text: $t('common.preview'), click: (done) =>{
            $review($form, $self, $queryEngine, done)
          }},
          {...okButton,visible: !$form.readPretty }
        ]
      }`),
    beforeClose: `{{(done, type) => {
      $form.readPretty = false
      if ( type === 'ok') {
        $self.query('*.detailDialog.form').take().submit(values => {
          $confirm($t('cusEntry.supplement20250211.message1'), { // 送货单明细绑定条码后无法再次编辑送货明细行，请确认送货明细行无误
            confirmButtonText: $t('common.confirm'),
            cancelButtonText: $t('common.cancel'),
              type: 'warning'
            }).then(() => {
              $save($form, $queryEngine, values, done)
            }).catch(() => {})
          
        })
      } else {
        done()
      }
    }}}`
  },
  properties: {
    form: {
      type: 'object',
      'x-decorator': 'FormLayout',
      'x-decorator-props': {
         layout: 'vertical' // 'horizontal'
      },
      'x-component': 'FormGrid',
      'x-component-props': {
        maxColumns: 2,
        columnGap: 32,
        rowGap: 0
      },
      'x-query-engine-skip': true,
      properties: generateXindexInOrder({
        deliveryNumber: {
          type: 'string',
          title: i18nExpression('orderMod.deliveryNumber'), // 送货单号
          'x-decorator': 'FormItem',
          'x-component': 'QuickSearchWrapper',
          'x-component-props': {
            readPretty: '{{$form.readPretty}}',
            showKey: 'deliveryNumber',
            propKey: 'deliveryNumber',
            'name': 'scc_sc_delivery_note_tag',
            '@close-quicksearch': expression(`(val, scope) => {
                $getBeliverInfoByQuick($queryEngine,$form,val)
              }`)
          },
          ...requiredValidatorSegment
        },
        deliveryLine: {
          type: 'string',
          title: i18nExpression('orderMod.buyerOrderSynergy.deliveryLineNum'), // 送货单行号
          'x-decorator': 'FormItem',
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')
          },
          ...requiredValidatorSegment
        },
        materialCode: {
          type: 'string',
          title: i18nExpression('common.materialCode'), // 物料编码
          'x-decorator': 'FormItem',
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')
          },
          ...requiredValidatorSegment
        },
        materialName: {
          type: 'string',
          title: i18nExpression('common.materialName'), // 物料名称
          'x-decorator': 'FormItem',
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')
          },
          ...requiredValidatorSegment
        },
        categoryCode: {
          type: 'string',
          title: i18nExpression('components.category.categoryCode'), // 品类编码
          'x-decorator': 'FormItem',
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')
          },
          ...requiredValidatorSegment
        },
        categoryName: {
          type: 'string',
          title: i18nExpression('components.category.categoryName'), // 品类名称
          'x-decorator': 'FormItem',
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')
          },
          ...requiredValidatorSegment
        },
        vendorName: {
          type: 'string',
          title: i18nExpression('common.companyName'), // 供应商名称
          'x-decorator': 'FormItem',
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')
          },
          ...requiredValidatorSegment
        },
        tagGenerateRuleId: {
          type: 'string',
          title: i18nExpression('orderMod.tagRuleName'), // 条码生成规则
          'x-decorator': 'FormItem',
          enum: expression('$form.query(\'TagOuterBox\').get(\'data\').tagRuleList'),
          'x-component': 'Select',
          'x-component-props': {
            '@change': expression(`(val, item) => {
                 if (!val) return

                 const option = $self.dataSource.find(item => item.value === val)
                 $form.values.form.tagRuleCode = option.tagRuleCode
                 $form.values.form.tagRuleName = option.tagRuleName
                 $form.values.form.tagType = option.tagType
                }`)
          },
          ...requiredValidatorSegment
        },
        tagType: {
          type: 'string',
          title: i18nExpression('orderMod.tagType'), // 条码样式
          'x-decorator': 'FormItem',
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'TAG_TYPE',
            disabled: expression('$form.readPretty ? undefined : true')
          },
          ...requiredValidatorSegment
        },
        deliveryQuantity: {
          type: 'string',
          title: i18nExpression('orderMod.buyerOrderSynergy.deliveryQuantity'), // 送货数量
          'x-decorator': 'FormItem',
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')
          },
          ...requiredValidatorSegment
        },
        generateTotalQuantity: {
          type: 'string',
          title: i18nExpression('buyerDeliveryOrder.quantityGenerated'), // 已生成数量
          'x-decorator': 'FormItem',
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')
          },
          ...requiredValidatorSegment
        },
        generateQuantity: {
          type: 'number',
          title: i18nExpression('orderMod.generateQuantity'), // 本次生成数量
          'x-decorator': 'FormItem',
          'x-decorator-props': {
            tooltip: i18nExpression('buyerDeliveryOrder.prompt23'), // 默认为送货数量-已生成数量，可手动减少，不可大于默认值
            tooltipLayout: 'icon'
          },
          'x-validator': {
            required: true,
            message: i18nExpression('common.requiredField'),
            validator: expression(`(value, rule) => {
              const n = parseInt(+$form.values.form.deliveryQuantity - +$form.values.form.generateTotalQuantity)
              if(value > n){
                return $t('buyerDeliveryOrder.prompt24') + n
              }
            }`)
          }
        },
        maxBoxQuantity: {
          type: 'number',
          title: i18nExpression('dataConfMod.maxPackingCarton'), // 外箱最大包装量
          'x-decorator': 'FormItem',
          'x-decorator-props': {
            tooltip: i18nExpression('buyerDeliveryOrder.prompt25'), // 1、当没有配置该物料外箱最大包装量时，则需自行填写；2、当采购商规定了该物料单个外箱包装量时，则自动带出。
            tooltipLayout: 'icon'
          },
          ...requiredValidatorSegment
        },
        generateTagQuantity: {
          type: 'string',
          title: i18nExpression('buyerDeliveryOrder.prompt14'), // 生成外箱条码数量
          'x-decorator': 'FormItem',
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')
          },
          'x-decorator-props': {
            tooltip: i18nExpression('buyerDeliveryOrder.prompt26'), // 本次生成数量/外箱最大包装量，若有余数则自动+1，即尾箱不装满
            tooltipLayout: 'icon'
          },
          'x-reactions': expression(`() => {
            if(!$form.values.form.generateQuantity || !$form.values.form.maxBoxQuantity){
              $self.value = 0
            }else{
              $self.value = Math.ceil(+$form.values.form.generateQuantity / +$form.values.form.maxBoxQuantity)
              
            }
            }`),
          ...requiredValidatorSegment
        },
        tailBoxNum: {
          type: 'string',
          title: i18nExpression('buyerDeliveryOrder.tailBoxNum'), // 尾箱数量
          'x-decorator': 'FormItem',
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')
          },
          'x-reactions': expression(`() => {
            if(!$form.values.form.generateQuantity || !$form.values.form.maxBoxQuantity){
              $self.value = 0
            }else{
              let num = $form.values.form.generateQuantity
              let x = String(num).indexOf(".")+1;//得到小数点的位置
              let y = String(num).length - x;//小数点的位数
              if(x!=0 && y>0){
                $self.value = (+$form.values.form.generateQuantity % +$form.values.form.maxBoxQuantity).toFixed(y)
              }else{
                $self.value = +$form.values.form.generateQuantity % +$form.values.form.maxBoxQuantity
              }
            }
            }`),
          ...requiredValidatorSegment
        },
        boundInnerBoxFlag: {
          type: 'string',
          title: i18nExpression('orderMod.boundInnerBoxFlag'), // 是否绑定内箱
          'x-decorator': 'FormItem',

          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'YES_OR_NO'
          },
          ...requiredValidatorSegment
        },
        templateCode: {
          type: 'string',
          title: i18nExpression('buyerDeliveryOrder.templateCode'), // 打印模板
          'x-decorator': 'FormItem',
          enum: expression('$form.query(\'TagOuterBox\').get(\'data\').printTemplateList'),
          'x-component': 'Select',
          'x-component-props': {
            '@change': expression(`(val, item) => {
                 if (!val) return
                 const option = $self.dataSource.find(item => item.value === val)
                 $form.values.form.templateName = option.label
                 $form.values.form.templatePath = option.templatePath
                }`)
          },
          ...requiredValidatorSegment
        },
        templateName: {
          type: 'string',
          'x-hidden': true
        },
        templatePath: {
          type: 'string',
          'x-hidden': true
        }
      })
    }
  }
}
