<!-- eslint-disable quotes -->
<script setup lang="ts">
// @ts-ignore
import {
  i18nExpression,
  expression,
  defineSchemas,
  generateXindexInOrder,
  generateCharFunctionExpression,
} from '@meicloud/render-engine'
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
// @ts-ignore
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import { useAttrs, ref } from 'vue-demi'
// @ts-ignore
import {
  formGridSegment,
  requiredValidatorSegment,
} from 'lib@/components/render-engine/schema-segments'
// @ts-ignore
import { useDebounceFn } from '@vueuse/core'

// @ts-ignore
import { getDictItem } from '@/api/common'
// @ts-ignore
import { adaptDictData } from '@/utils'
// @ts-ignore
import { systemUrl } from '@/config/sysConfig'

// eslint-disable-next-line no-undef
const props = defineProps({
  row: {
    type: Object,
    default: () => {
      return {}
    }
  }
})

// 获取内外箱模板
const innerAndOutTemplate = (async () => {
  let inner = await getDictItem('TAG_PRINT_TEMPLATEP_INNER')
  let outer = await getDictItem('TAG_PRINT_TEMPLATEP_OUTER')

  return {
    inner: adaptDictData(inner.data, 'dict'),
    outer: adaptDictData(outer.data, 'dict'),
  }
})()

const { emitTabRemove, app, t, getCurrentUserInfo, vendor } = usePageHelper()

console.log(getCurrentUserInfo, 'getCurrentUserInfo')

const $attrs: any = useAttrs()

// 内箱条码打印模板
// const $getPrintTemplateList = ($form: any) => {
//   console.log(app.$store.getters.userInfo)
//   if (!$form.query('DetailDialog').get('data').printTemplateList.length) {
//     getDictItem('TAG_PRINT_TEMPLATEP_INNER').then((res: any) => {
//       $form.query('DetailDialog').get('data').printTemplateList = adaptDictData(res.data, 'dict')
//     })
//   }
// }

// 获取打印模板数据
const $getPrintTemplateList = ($queryEngine: any, $form: any, val: any) => {
  $queryEngine.request.baseRequest({
    'type': 'TagTemplateRelation',
    'lang': 'zh-cn',
    loading: true,
    "query": {
      "*": {}
    },
    "payload": [{
      "materialCode": val.materialCode,
      "categoryCode": val.categoryCode,
      "type": 'INNER'
    }],
    'action': 'listByMaterialAndCategory'
  }).then((res: any) => {
    if (res.data.length) {
      res.data.forEach((item: any) => {
        item.label = item.templateName
        item.value = item.templateCode
      })
      $form.query('form').get('data').printTemplateList = res.data
    }
  })
}

// 获取条码生成规则
const $getTagRuleList = ($queryEngine: any, $form: any, val: any, flag: any) => {
  if (flag === 'dialog') {
    $form.query('form').get('data').tagRuleList = []
  } else {
    $form.query('form').get('data').queryTagRuleList = []
  }

  $queryEngine.request
    .baseRequest({
      type: 'TagGenerateRuleConfig',
      lang: 'zh-cn',
      query: {
        '*': {},
      },
      payload: [
        {
          materialCode: val.materialCode,
          categoryCode: val.categoryCode,
          ruleType: 'INNER',
        },
      ],
      action: 'listByMaterialAndCategory',
    })
    .then((res: any) => {
      if (res.data.length) {
        res.data.forEach((item: any) => {
          item.label = item.tagRuleName
          item.value = item.tagGenerateRuleId
          if (item.defaultFlag === 'Y') {
            $form.values.form.tagGenerateRuleId = item.tagGenerateRuleId
            $form.values.form.tagRuleCode = item.tagRuleCode
            $form.values.form.tagRuleName = item.tagRuleName
            $form.values.form.tagType = item.tagType
          }
        })
        if (flag === 'dialog') {
          $form.query('form').get('data').tagRuleList = res.data
        } else {
          $form.query('form').get('data').queryTagRuleList = res.data
        }
      }
    })
}

// 物料快查
const $getMaterialByQuick = ($queryEngine: any, $form: any, val: any) => {
  console.log('!!!!!!!!! getMaterialByQuick $queryEngine  :', $queryEngine)
  val.maxBoxQuantity = val.minimumPackagingQuantity || null
  $form.query('form').get('data').tagRuleList = []

  const {
    companyCode: vendorCode,
    companyId: vendorId,
    companyName: vendorName,
  } = app.$store.getters.userInfo
  val = { ...val, vendorCode, vendorId, vendorName }

  delete val.status

  $form.query('form').take((field: any) => {
    field.setValue(val)
  })
  $getTagRuleList($queryEngine, $form, val, 'dialog')

}

const $review = ($form: any, $self: any, $queryEngine: any) => {
  $self.query('form').take().submit((values: any) => {
    $queryEngine.request.baseRequest({
        'type': 'TagInnerBoxView',
        'lang': 'zh-cn',
        "query": {
          "*": {}
        },
        "payload": [{ ...values}],
        'action': 'view'
      }).then((res: any) => {
        console.log('res',res)
        $printRowsSingle(res.data, $form.values.form.templatePath, 'outerBoxId')
      })
  })

}
const $printRowsSingle = async (rows: any, path: any, key: any) => {
  const ids = rows.map((item: any) => item[key]).join(',')
  const params = encodeURIComponent(`ids=${ids}`)
  $openPrint(path, params)
}

// 打印
const $printRows = async (rows: any, type: any, key: any) => {
  const temp: any = await innerAndOutTemplate
  const ids = rows.map((item: any) => item[key]).join(',')
  console.log(temp, ids)
  const params = encodeURIComponent(`ids=${ids}`)
  $openPrint(temp[type][0].desc, params)
}


// 打开pdf
const $openPrint = (pdfName: any, params: any) => {
  const xml = encodeURIComponent(pdfName)
  const url = `${systemUrl}/#/pdfPrint?xml=${xml}&params=${params}`
  window.open(url)
}

const scope = {
  $props: props,
  $attrs,
  app,
  t,
  getCurrentUserInfo: getCurrentUserInfo(),
  $getPrintTemplateList,
  $getTagRuleList,
  $getMaterialByQuick,
  $openPrint,
  innerAndOutTemplate,
  $review
}

const components = {}

const schema = defineSchemas({
  createInnerBox: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-query-engine': {
      service: 'sup-ce',
      actions:{
        read:{
          immediate: true,
          ready: expression(`async ()=>{
            console.log('ready')
            return await new Promise((res) => {
              console.log('ready2')
              setTimeout(() => {

                let row =  $props.row

                console.log('!!!row',row)
                app.$http({
                  url: '/api-base/material/materialItem/ceeaGet',
                  method: 'GET',
                  params: {id: row.materialId},
                  loading: true,
                }).then(res=>{
                  console.log(res.data)
                  let result = res?.data?.materialItem
                  let materialObj = {
                    ...row,
                    ...result,
                    maxBoxQuantity: result.minimumPackagingQuantity || 0,
                    maxBoxQuantityDisabled: result.minimumPackagingQuantity> 0? true: false,
                    status: undefined,
                    outerBoxId: undefined
                  }

                  $form.query('form').take().setValue(materialObj)
                  $values.categoryCode = result.categoryCode
                  $values.materialCode = row.materialCode


                })


              })
            })
          }`)
        }
      }
    },
    properties: {
      form: {
        type: 'object',
        'x-data': {
          printTemplateList: [],
          tagRuleList: [],
          queryTagRuleList: [],
        },
        'x-decorator': 'FormLayout',
        'x-decorator-props': {
          layout: 'vertical'
        },
        'x-component': 'FormGrid',
        'x-component-props': {
          maxColumns: 2,
          columnGap: 32,
          rowGap: 0,
        },
        properties: generateXindexInOrder({
          innerBoxId: {
            type: 'string',
            'x-hidden': true,
          },
          // 物料编码
          materialCode: {
            type: 'string',
            title: i18nExpression('common.materialCode'),
            'x-decorator': 'FormItem',
            'x-reactions': expression(`()=>{
              if($values.materialCode){
                console.log('!!!$values',$values)
                $getTagRuleList($queryEngine, $form, $values, 'dialog')
                $getPrintTemplateList($queryEngine,$form, $values)
              }

            }`),
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              disabled: expression('$form.readPretty ? undefined : true'),
              readPretty: '{{$form.readPretty}}',
              showKey: 'materialCode',
              propKey: 'materialCode',
              name: 'purchase_catalog_material_valid',
              '@close-quicksearch': expression(`(val, scope) => {
            console.log('$queryEngine',$queryEngine)
                  $getMaterialByQuick($queryEngine,$form,val)
               }`),
            },
            ...requiredValidatorSegment,
          },
          // 物料名称
          materialName: {
            type: 'string',
            title: i18nExpression('common.materialName'),
            'x-decorator': 'FormItem',
            'x-component-props': {
              disabled: expression('$form.readPretty ? undefined : true'),
            },
            ...requiredValidatorSegment,
          },
          // 品类编码
          categoryCode: {
            type: 'string',
            title: i18nExpression('components.category.categoryCode'),
            'x-decorator': 'FormItem',
            'x-component-props': {
              disabled: expression('$form.readPretty ? undefined : true'),
            },
            ...requiredValidatorSegment,
          },
          // 品类名称
          categoryName: {
            type: 'string',
            title: i18nExpression('components.category.categoryName'),
            'x-decorator': 'FormItem',
            'x-component-props': {
              disabled: expression('$form.readPretty ? undefined : true'),
            },
            ...requiredValidatorSegment,
          },
          // 供应商名称
          vendorName: {
            type: 'string',
            title: i18nExpression('common.companyName'),
            'x-decorator': 'FormItem',
            'x-component-props': {
              disabled: expression('$form.readPretty ? undefined : true'),
            },
            ...requiredValidatorSegment,
          },
          // 条码生成规则
          tagGenerateRuleId: {
            type: 'string',
            title: i18nExpression('orderMod.tagRuleName'),
            'x-decorator': 'FormItem',
            enum: expression("$form.query('form').get('data').tagRuleList"),
            'x-component': 'Select',
            'x-component-props': {
              '@change': expression(`(val, item) => {
                 if (!val) return

                 const option = $self.dataSource.find(item => item.value === val)
                 console.log(option)
                 $form.values.form.tagRuleCode = option.tagRuleCode
                 $form.values.form.tagRuleName = option.tagRuleName
                 $form.values.form.tagType = option.tagType
                }`),
            },
            ...requiredValidatorSegment,
          },
          // 条码样式
          tagType: {
            type: 'string',
            title: i18nExpression('orderMod.tagType'),
            'x-decorator': 'FormItem',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'TAG_TYPE',
              disabled: expression('$form.readPretty ? undefined : true'),
            },
            ...requiredValidatorSegment,
          },
          // 物料数量
          materialQuantity: {
            type: 'number',
            title: i18nExpression('orderMod.materialQuantity'),
            'x-decorator': 'FormItem',
            ...requiredValidatorSegment,
          },
          // 内箱最大包装量
          maxBoxQuantity: {
            type: 'number',
            title: i18nExpression('hierarchical.maximum'),
            'x-component-props': {
              disabled: expression('$form.values.form.maxBoxQuantityDisabled ? true : undefined')
            },
            'x-decorator': 'FormItem',
            'x-decorator-props': {
              // 1、当没有配置该物料内箱最大包装量时，则需自行填写；2、当采购商规定了该物料单个内箱包装量时，则自动带出。
              tooltip: i18nExpression('cusEntry.supplement20250211.maxBoxQuantityTip'),
              tooltipLayout: 'icon',
            },
            ...requiredValidatorSegment,
          },
          // 生成内箱条码数量
          generateTagQuantity: {
            type: 'string',
            title: i18nExpression('buyerDeliveryOrder.innerBoxBarcodesNum'),
            'x-decorator': 'FormItem',
            'x-component-props': {
              disabled: expression('$form.readPretty ? undefined : true'),
            },
            'x-decorator-props': {
              // 生成内箱条码数量=物料数量/内箱最大包装量，若有余数则自动+1，即尾箱不装满
              tooltip: i18nExpression('cusEntry.supplement20250211.generateTagQuantityTip'),
              tooltipLayout: 'icon',
            },
            'x-reactions': expression(`() => {
              $self.value = Math.ceil(+$form.values.form.materialQuantity / +$form.values.form.maxBoxQuantity) || 0
            }`),
            ...requiredValidatorSegment,
          },
          // 尾箱数量
          tailBoxNum: {
            type: 'string',
            title: i18nExpression('buyerDeliveryOrder.tailBoxNum'),
            'x-decorator': 'FormItem',
            'x-query-engine-skip': true,
            'x-component-props': {
              disabled: expression('$form.readPretty ? undefined : true'),
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
            ...requiredValidatorSegment,
          },
          // 打印模板
          templateCode: {
          type: 'string',
          title: i18nExpression('buyerDeliveryOrder.templateCode'),
          'x-decorator': 'FormItem',
          enum: expression('$form.query(\'form\').get(\'data\').printTemplateList'),
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
            'x-hidden': true,
          },
          templatePath: {
            type: 'string',
            'x-hidden': true,
          },
        }),
      },
      toolbar: {
        type: 'void',
        'x-component': 'Space',
        'x-component-props': {
          style: 'margin-bottom: 16px;display:flex;justify-content:flex-end',

        },
        properties: {
          cancelBtn:{
            type: 'void',
            title: `{{$t('components.common.cancel')}}`,
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              '@click': expression(`() => {
                 $bus.$emit('closeBarcodeRelationDialog')
              }`),
            },
          },
          reviewBtn:{
            type: 'void',
            title: `{{$t('common.preview')}}`,
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              '@click': expression(`() => {
                 $review($form, $self, $queryEngine)

              }`),
            },

          },
          addBtn: {
            type: 'void',
            title: `{{$t('common.submit')}}`,
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              '@click': expression(`() => {
                $form.query('form').take().submit(values => {
                  $form.query('addBtn').take().setComponentProps({ loading: true })
                  $queryEngine.request.baseRequest({
                    'type': 'TagInnerBox',
                    'lang': 'zh-cn',
                    "query": {
                      "*": {}
                    },
                    "payload": [{...values}],
                    'action': 'save'
                  }).then((res) => {
                    $form.query('addBtn').take().setComponentProps({ loading: false })
                    app.$message.success($t('common.success'))
                    $bus.$emit('closeBarcodeRelationDialog')
                  }).catch(()=>{
                    $form.query('addBtn').take().setComponentProps({ loading: false })
                  })
              })

              }`),
            },
          },
        },
      },
    },
  },

})
</script>

<template>
  <RenderEngine
    :pageAttrs="$attrs"
    :schema="schema"
    :scope="scope"
    :components="components"
    schemaKey="TagInnerBoxDetail"
  />
</template>

<style lang="scss">
</style>
