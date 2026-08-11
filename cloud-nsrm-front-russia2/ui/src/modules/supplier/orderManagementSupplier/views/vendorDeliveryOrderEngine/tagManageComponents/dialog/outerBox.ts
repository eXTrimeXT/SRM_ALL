import {
  expression,
  generateXindexInOrder,
  i18nExpression
} from '@meicloud/render-engine'

import {
  requiredValidatorSegment
} from 'lib@/components/render-engine/schema-segments'

export default {
  type: 'void',
  title: i18nExpression('buyerDeliveryOrder.createOuterBox'),
  'x-component': 'RDialog',
  'x-component-props': {
    'close-on-click-modal': false,
    'destroy-on-close': true,
    'size': 'large',
    footerButtonList: expression(`(_, { cancelButton,okButton }) => {
      return [
        cancelButton,
        { text: $t('buyerDeliveryOrder.nextStep'), 
          click: (done) =>{
            $setDeliveryForm($message,$form,$queryEngine,$values)
            
          },
          type:'primary',
          visible: $form.query('TagOuterBox').get('data').detailDialogStep === 1
        },

        { text: $t('buyerDeliveryOrder.previousStep'), 
          click: (done) =>{
            $toPrev($form)
            
          },
          visible: $form.query('TagOuterBox').get('data').detailDialogStep === 2
        },

        {text: $t('buyerDeliveryOrder.preview'),click: (done) =>{
            $review($form, $self, $queryEngine, done)
          },
          visible: $form.query('TagOuterBox').get('data').detailDialogStep === 2
        },
        
        {...okButton,visible: !$form.query('TagOuterBox').get('data').readonly && $form.query('TagOuterBox').get('data').detailDialogStep === 2,loading: $form.query('TagOuterBox').get('data').okBtnLoading }
      ]
        
      }`),
    beforeClose: expression(`(done, type) => {
      if ( type === 'ok') {
        
        $self.query('*.detailDialog.form').take().submit(values => {
          $form.query('TagOuterBox').get('data').okBtnLoading = true
          $queryEngine.request.baseRequest({
            loading: true,
            action: "save",
            type: 'TagOuterBox',
            payload: [{...values}],
            query: {
              "*":{}
            }
          }).then((res) => {
            $form.query('TagOuterBox').get('data').okBtnLoading = false
            app.$message.success($t('common.success'))
            // 刷新送货单行明细
            $bus.$emit('TagManage')
            // $queryEngine.state.paginationManagement.refresh()
            
            done()
          }).catch(()=>{
            $form.query('TagOuterBox').get('data').okBtnLoading = false
          })
        })
      } else {
        done()
        }

      }
    `)
  },
  properties: {
    steps: {
      type: 'void',
      'x-decorator': 'div',
      'x-decorator-props': {
        class: 'stepDiv'
      },
      'x-component': 'Steps',
      'x-component-props': {
        alignCenter: true,
        finishStatus: 'success'
      },
      'x-reactions': expression(`field => {
        field.componentProps.active = $form.query('TagOuterBox').get('data').detailDialogStep === 1? 0 : 1
      }`),
      properties: {
        step1: {
          type: 'void',
          'x-component': 'el-step',
          'x-component-props': {
            title: i18nExpression('buyerDeliveryOrder.selectDeliveryNoteDetails')
          }
        },
        step2: {
          type: 'void',
          'x-component': 'el-step',
          'x-component-props': {
            title: i18nExpression('buyerDeliveryOrder.createOuterBox')
          }
        },
        step3: {
          type: 'void',
          'x-component': 'el-step',
          'x-component-props': {
            title: i18nExpression('buyerDeliveryOrder.complete')
          }
        }

      }
    },
    deliveryOrderDetail: {
      type: 'void',
      'x-decorator': 'QueryEngine',
      properties: {
        deliveryOrderTable: {
          type: 'array',
          'x-component': 'RenderTable',
          'x-component-props': {
            class: 'table-view-vxe-table',

            style: {
              height: '50vh',
              display: '{{($form.query(\'TagOuterBox\').get(\'data\').detailDialogStep === 1 ? \'block\' : \'none\')}}'
            },
            preColumns: 'seq',
            pagination: false,
            openCustomTable: false,
            // 'row-id':'orderDetailId',
            // 'radio-config':{strict: false},
            '@current-change': expression(`({ row }) => {
              $form.query('TagOuterBox').get('data').currentDeliveryNoteDetail = row
            }`)
          },
          properties: generateXindexInOrder({
            orderDetailId: {
              type: 'string',
              'x-hidden': true,
              'x-query-engine-primary-key': true
            },
            orderNumber: {
              type: 'string',
              title: i18nExpression('purSettlementMod.orderNumber'), // 采购订单号
              'x-render-table-column': {
                minWidth: 130
              },
              'x-read-pretty': true
            },
            orderLineNum: {
              type: 'string',
              title: i18nExpression('orderMod.orderLineNum'), // 订单行号
              'x-render-table-column': {
                minWidth: 100
              },
              'x-read-pretty': true
            },
            deliveryNumber: {
              type: 'string',
              title: i18nExpression('orderMod.deliveryNumber'), // 送货单号
              'x-render-table-column': {
                minWidth: 130
              },
              'x-read-pretty': true
            },
            deliveryLine: {
              type: 'string',
              title: i18nExpression('orderMod.deliveryLine'), // 送货单行号
              'x-render-table-column': {
                minWidth: 100
              },
              'x-read-pretty': true
            },
            deliveryNoticeNumber: {
              type: 'string',
              title: i18nExpression('orderMod.deliveryNoticeNumber'), // 送货通知单编号
              'x-render-table-column': {
                minWidth: 130
              },
              'x-read-pretty': true

            },
            deliveryNoticeLineNum: {
              type: 'string',
              title: i18nExpression('orderMod.deliveryLineNum'), // 送货通知单行号
              'x-render-table-column': {
                minWidth: 100
              },
              'x-read-pretty': true
            },

            materialCode: {
              type: 'string',
              title: i18nExpression('common.materialCode'), // 物料编码
              'x-render-table-column': {
                minWidth: 100
              },
              'x-read-pretty': true,
              'x-query-engine-relation': 'detailList.orderDetailId'
            },
            materialName: {
              type: 'string',
              title: i18nExpression('common.materialName'), // 物料名称
              'x-render-table-column': {
                minWidth: 100
              },
              'x-read-pretty': true
            }
          })

        }
      }
    },
    form: {
      type: 'object',
      'x-decorator': 'FormLayout',
      'x-decorator-props': {
        layout: 'vertical'
      },
      'x-component': 'FormGrid',
      'x-component-props': {
        // style:{flex:1},
        style: {
          display: '{{($form.query(\'TagOuterBox\').get(\'data\').detailDialogStep === 2 ? \'grid\' :\'none\' )}}'
          // display: undefined
        },
        maxColumns: 3,
        columnGap: 32,
        rowGap: 0
      },
      // 'x-visible': expression(`$form.query('TagOuterBox').get('data').detailDialogStep === 2`),
      'x-query-engine-skip': true,
      readPretty: '{{$form.query(\'TagOuterBox\').get(\'data\').readonly}}',
      properties: {
        deliveryNumber: {
          type: 'string',
          title: i18nExpression('orderMod.deliveryNumber'),
          'x-decorator': 'FormItem',
          'x-component-props': {
            disabled: true
          },
          ...requiredValidatorSegment
        },
        deliveryLine: {
          type: 'string',
          title: i18nExpression('orderMod.deliveryLine'),
          'x-decorator': 'FormItem',
          'x-component-props': {
            disabled: true
          },
          ...requiredValidatorSegment
        },
        materialCode: {
          type: 'string',
          title: i18nExpression('common.materialCode'),
          'x-decorator': 'FormItem',
          'x-component-props': {
            disabled: true
          },
          ...requiredValidatorSegment
        },
        materialName: {
          type: 'string',
          title: i18nExpression('common.materialName'),
          'x-decorator': 'FormItem',
          'x-component-props': {
            disabled: true
          },
          ...requiredValidatorSegment
        },
        categoryCode: {
          type: 'string',
          title: i18nExpression('common.categoryCode'),
          'x-decorator': 'FormItem',
          'x-component-props': {
            disabled: true
          },
          ...requiredValidatorSegment
        },
        categoryName: {
          type: 'string',
          title: i18nExpression('common.categoryName'),
          'x-decorator': 'FormItem',
          'x-component-props': {
            disabled: true
          },
          ...requiredValidatorSegment
        },
        vendorName: {
          type: 'string',
          title: i18nExpression('common.vendorName'),
          'x-decorator': 'FormItem',
          'x-component-props': {
            disabled: true
          },
          ...requiredValidatorSegment
        },
        tagGenerateRuleId: {
          type: 'string',
          title: i18nExpression('buyerDeliveryOrder.barcodeGenerationRules'),
          'x-decorator': 'FormItem',
          enum: expression('$form.query(\'TagOuterBox\').get(\'data\').tagRuleList'),
          'x-component': 'Select',
          'x-component-props': {
            disabled: expression('$form.query(\'TagOuterBox\').get(\'data\').readonly'),
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
          title: i18nExpression('buyerDeliveryOrder.barcodeStyle'),
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
          title: i18nExpression('buyerDeliveryOrder.deliveryQuantity'),
          'x-decorator': 'FormItem',
          'x-component-props': {
            disabled: true
          },
          ...requiredValidatorSegment
        },
        generateTotalQuantity: {
          type: 'string',
          title: i18nExpression('buyerDeliveryOrder.quantityGenerated'),
          'x-decorator': 'FormItem',
          'x-component-props': {
            disabled: true
          },
          ...requiredValidatorSegment
        },
        generateQuantity: {
          type: 'number',
          title: i18nExpression('buyerDeliveryOrder.quantityGeneratedCurrent'),
          'x-decorator': 'FormItem',
          'x-decorator-props': {
            tooltip: i18nExpression('buyerDeliveryOrder.prompt23'),
            tooltipLayout: 'icon'
          },
          'x-validator': {
            required: true,
            validator: expression(`(value, rule) => {
              const n = +$form.values.form.deliveryQuantity - +$form.values.form.generateTotalQuantity
              if(value > n){
                return $t('buyerDeliveryOrder.prompt24')+ n
              }
            }`)
          }
        },
        maxBoxQuantity: {
          type: 'number',
          title: i18nExpression('buyerDeliveryOrder.outerBoxNum'),
          'x-decorator': 'FormItem',
          'x-component-props': {
            disabled: expression('$form.values.form.maxBoxQuantityDisabled ? true : undefined')
          },
          'x-decorator-props': {
            tooltip: i18nExpression('buyerDeliveryOrder.prompt25'),
            tooltipLayout: 'icon'
          },
          ...requiredValidatorSegment
        },
        generateTagQuantity: {
          type: 'string',
          title: i18nExpression('buyerDeliveryOrder.generateTagQuantity'),
          'x-decorator': 'FormItem',
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')
          },
          'x-decorator-props': {
            tooltip: i18nExpression('buyerDeliveryOrder.prompt26'),
            tooltipLayout: 'icon'
          },
          'x-reactions': expression(`() => {
              $self.value = Math.ceil(+$form.values.form.generateQuantity / +$form.values.form.maxBoxQuantity) || 0
            }`),
          ...requiredValidatorSegment
        },
        tailBoxNum: {
          type: 'string',
          title: i18nExpression('buyerDeliveryOrder.tailBoxNum'),
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
          title: i18nExpression('buyerDeliveryOrder.boundInnerBoxFlag'),
          'x-decorator': 'FormItem',

          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'YES_OR_NO',
            disabled: expression('$form.values.form.hasBoundInnerBoxFlag ? true : undefined')
          },
          ...requiredValidatorSegment
        },
        templateCode: {
          type: 'string',
          title: i18nExpression('buyerDeliveryOrder.templateCode'),
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
      }
    }
  }
}
