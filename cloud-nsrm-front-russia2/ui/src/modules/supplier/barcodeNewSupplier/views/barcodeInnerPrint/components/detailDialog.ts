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
  title: i18nExpression('orderMod.createInnerBoxBarcode'), // 创建内箱条码
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
              $save($form, $queryEngine, values, done)         
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
        layout: 'vertical'
      },
      'x-component': 'FormGrid',
      'x-component-props': {
        maxColumns: 2,
        columnGap: 32,
        rowGap: 0
      },
      properties: generateXindexInOrder({
        innerBoxId: {
          type: 'string',
          'x-hidden': true
        },
        unit: {
          type: 'string',
          'x-hidden': true
        },
        materialCode: {
          type: 'string',
          title: i18nExpression('common.materialCode'), // 物料编码
          'x-decorator': 'FormItem',
          'x-component': 'QuickSearchWrapper',
          'x-component-props': {
            readPretty: '{{$form.readPretty}}',
            showKey: 'materialCode',
            propKey: 'materialCode',
            'name': 'purchase_catalog_material_valid',
            '@close-quicksearch': expression(`(val, scope) => {
                  $getMaterialByQuick($queryEngine,$form,val)
               }`)
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
          enum: expression('$form.query(\'TagInnerBox\').get(\'data\').tagRuleList'),
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
        materialQuantity: {
          type: 'number',
          title: i18nExpression('orderMod.materialQuantity'), // 物料数量
          'x-decorator': 'FormItem',
          ...requiredValidatorSegment
        },
        maxBoxQuantity: {
          type: 'number',
          title: i18nExpression('hierarchical.maximum'), // 内箱最大包装量
          'x-decorator': 'FormItem',
          'x-decorator-props': {
            // 1、当没有配置该物料内箱最大包装量时，则需自行填写；2、当采购商规定了该物料单个内箱包装量时，则自动带出。
            tooltip: i18nExpression('cusEntry.supplement20250211.maxBoxQuantityTip'),
            tooltipLayout: 'icon'
          },
          ...requiredValidatorSegment
        },
        generateTagQuantity: {
          type: 'string',
          title: i18nExpression('buyerDeliveryOrder.innerBoxBarcodesNum'), // 生成内箱条码数量
          'x-decorator': 'FormItem',
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')
          },
          'x-decorator-props': {
            // 生成内箱条码数量=物料数量/内箱最大包装量，若有余数则自动+1，即尾箱不装满
            tooltip: i18nExpression('cusEntry.supplement20250211.generateTagQuantityTip'),
            tooltipLayout: 'icon'
          },
          'x-reactions': expression(`() => {
              if(!$form.values.form.materialQuantity || !$form.values.form.maxBoxQuantity){
                $self.value = 0
              }else{
                $self.value = Math.ceil(+$form.values.form.materialQuantity / +$form.values.form.maxBoxQuantity)
              }
              
            }`),
          ...requiredValidatorSegment
        },
        tailBoxNum: {
          type: 'string',
          title: i18nExpression('buyerDeliveryOrder.tailBoxNum'), // 尾箱数量
          'x-decorator': 'FormItem',
          'x-query-engine-skip': true,
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')
          },
          'x-reactions': expression(`() => {
              if(!$form.values.form.materialQuantity || !$form.values.form.maxBoxQuantity){
                $self.value = 0
              }else{
                let num = $form.values.form.materialQuantity
                let x = String(num).indexOf(".")+1;//得到小数点的位置
                let y = String(num).length - x;//小数点的位数
                if(x!=0 && y>0){
                  $self.value = (+$form.values.form.materialQuantity % +$form.values.form.maxBoxQuantity).toFixed(y)
                }else{
                  $self.value = +$form.values.form.materialQuantity % +$form.values.form.maxBoxQuantity
                }
              }
            }`),
          ...requiredValidatorSegment
        },
        templateCode: {
          type: 'string',
          title: i18nExpression('buyerDeliveryOrder.templateCode'), // 打印模板
          'x-decorator': 'FormItem',
          enum: expression('$form.query(\'TagInnerBox\').get(\'data\').printTemplateList'),
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
