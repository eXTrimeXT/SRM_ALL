<script setup lang="ts">
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
// @ts-ignore
import {
  defineSchemas,
  expression,
  i18nExpression
} from '@meicloud/render-engine'
// @ts-ignore
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import pageInfo from 'lib@/compositionEngine/oneStopShopping/purchaseCatalogOnOrOff/pageInfoVendor'
// @ts-ignore
import { setWarningTip } from 'lib@/utils/util'

const { app, t, emitTabRemove, confirmMessage } = usePageHelper()

const attrs: any = useAttrs()

const isReadOnly = ($form: any) => {
  const data = $form.query('CatalogOnShelvesVendor').get('data')
  return data.flag === 'view'
}

const $closeTab = ($bus: any) => {
  $bus.$emit('PurchaseCatalogOnOrOff')
  emitTabRemove(attrs.tabName)
}

const showBtn = ($form: any) => {
  const data = $form.query('CatalogOnShelvesVendor').get('data')
  return {
    toggleEdit: data.flag === 'view' &&
      ['TO_BE_SUBMIT', 'REJECTED'].includes($form.values.status),
    saveBill:
      data.flag === 'add' ||
      (['TO_BE_SUBMIT', 'REJECTED'].includes($form.values.status) && data.flag !== 'view'),
    submitBill:
      data.flag === 'add' ||
      (['TO_BE_SUBMIT', 'REJECTED'].includes($form.values.status) &&
        data.flag !== 'view')
  }
}

// 管理
const toggleEdit = ($form: any) => {
  const data = $form.query('CatalogOnShelvesVendor').get('data')
  console.log(data, 'data')
  if (data.flag === 'view') {
    data.flag = 'edit'
  }
  // setTimeout(() => {
  //   $form.query('priceLibraryNo').take().setComponentProps({
  //     disabled: $form.query('CatalogOnShelvesVendor').get('data').isReadOnly || $form.values?.status === 'REJECTED'
  //   })
  // })
}

// 校验是否有图片上传并且有默认主图
const hasDefaultImg = ($form: any) => {
  const attaches = $form.query('CatalogOnShelvesVendor').get('data').catalogOnShelvesAttaches
  if (attaches.length < 1) {
    // 请至少上传一张物料图片！
    app.$message.warning(t('cusEntry.supplement20250205.uploadAtLeastOneMaterialImage'))
    return false
  }
  // 如果只有一张则默认为主图
  if (attaches.length === 1) {
    attaches[0].response.data.ifDefaultPicture = 'Y'
    return true
  }
  const sign = attaches.some(
    (item: any) => item.response.data.ifDefaultPicture === 'Y',
  )
  if (!sign) {
    // 请设置一张默认主图！
    app.$message.warning(t('cusEntry.supplement20250205.defaultMainImage'))
    return false
  }
  return true
}

// 保存
const saveBill = async ($form: any, $queryEngine: any) => {
  const { data } = await $queryEngine.request.baseRequest({
    type: 'CatalogOnShelvesVendor',
    action: 'save',
    lang: 'zh-cn',
    payload: [$form.values],
    'query': {
      '*': {},
      'tagRuleConfigLineList': {
        '*': {}
      }
    }
  })
  app.$message.success(t('common.success'))
  $queryEngine.request.read(data[0].catalogOnShelvesId)
}

// 处理物料图片
const setPictureParams = ($form: any) => {
  const data = $form.query('CatalogOnShelvesVendor').get('data')

  $form.values.catalogOnShelvesAttaches = data.catalogOnShelvesAttaches.map((item: any) => {
    return {
      ...item.response.data,
      catalogOnShelvesId: $form.values.catalogOnShelvesId
    }
  })

  const deleteIds = data.attachesDeleteList.reduce((acc: any, item: any) => {
    const deleteItem = { $delete: item.response.data.attachId }
    if (deleteItem.$delete) {
      acc.push(deleteItem)
    }
    return acc
  }, [])

  $form.values.orderQuantityMinimum = $form.values.orderQuantityMinimum ? +$form.values.orderQuantityMinimum : null

  $form.values.catalogOnShelvesAttaches = [...$form.values.catalogOnShelvesAttaches, ...deleteIds]

  data.attachesDeleteList = [] // 每次操作完图片之后清空缓存的删除id

  console.log($form.values.catalogOnShelvesAttaches, 'catalogOnShelvesAttaches')
}

// 提交审核
const submitBill = async ($form: any, $queryEngine: any, $bus: any) => {
  // 是否确认提交审批
  const confirmResult = await confirmMessage(t('bidMod.submitApprovalSure'))
  if (confirmResult !== 'confirm') return false

  await $queryEngine.request.baseRequest({
    type: 'CatalogOnShelvesVendor',
    action: 'submit',
    lang: 'zh-cn',
    payload: [$form.values],
    'query': {
      '*': {},
      'tagRuleConfigLineList': {
        '*': {}
      }
    }
  })
  app.$message.success(t('common.successSubmit'))
  $closeTab($bus)
}

// 事件处理钩子
const handleBtnCilck = async (method: any, $form?: any, $queryEngine?: any, $bus?: any) => {
  if (!hasDefaultImg($form)) return

  const methods: any = {
    saveBill,
    submitBill
  }

  $form.validate().then(() => {
    setPictureParams($form)
    methods[method]($form, $queryEngine, $bus)
  }).catch((err: any) => {
    setWarningTip(err)
  })
}

let scope: any = {
  app,
  attrs,
  $closeTab,
  isReadOnly,
  emitTabRemove,
  showBtn,
  toggleEdit,
  handleBtnCilck
}

const components = {}

const schema = defineSchemas({
  CatalogOnShelvesVendor: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        read: {
          immediate: true,
          ready: expression(`() => {
            return !!attrs?.params?.row?.catalogOnShelvesId
          }`),
          transformRequest: expression(`(data, headers) => {
            data.payload = [attrs?.params?.row?.catalogOnShelvesId || data.payload[0]]

            data.query = {
              '*': {},
              catalogOnShelvesAttaches:{
                "*":{}
              }
            }

            return data
          }`),
          onSuccess: expression(`(res) => {
            console.log('onSuccess=>', res.data[0])
            $form.setValues(res.data[0])

            const data = $form.query('CatalogOnShelvesVendor').get('data')
            data.catalogOnShelvesAttaches = res.data[0].catalogOnShelvesAttaches.map(item => {
              return {
                ifDefaultPicture: item.ifDefaultPicture,
                response: {
                  data: item
                }
              }
            })
          }`)
        }
      }
    },
    'x-component': 'FormContainer',
    'x-data': {
      flag: attrs.params.flag,
      isReadOnly: '{{isReadOnly($form)}}',
      catalogOnShelvesAttaches: [],
      showBtn: expression('showBtn($form)'),
      attachesDeleteList: []
    },
    items: {
      type: 'object',
      properties: {
        // 返回
        goBack: {
          type: 'void',
          'x-content': i18nExpression('vendorMod.goBack'),
          'x-component': 'Button',
          'x-component-props': {
            type: 'default',
            '@click': expression(`async (values) => {
              emitTabRemove(attrs.tabName)
            }`)
          }
        },
        // 管理
        toggleEdit: {
          type: 'void',
          'x-content': i18nExpression('purchaseDemand.manage'),
          'x-component': 'Button',
          'x-visible': '{{$form.query("CatalogOnShelvesVendor").get("data").showBtn.toggleEdit}}',
          'x-component-props': {
            type: 'primary',
            '@click': expression(`async () => {
              toggleEdit($form)
            }`)
          }
        },
        // 暂存
        saveBill: {
          type: 'void',
          'x-content': i18nExpression('common.staging'),
          'x-component': 'Button',
          'x-visible': '{{$form.query("CatalogOnShelvesVendor").get("data").showBtn.saveBill}}',
          'x-component-props': {
            type: 'primary',
            '@click': expression(`async (values) => {
              handleBtnCilck('saveBill', $form, $queryEngine)
           }`)
          }
        },
        // 提交审核
        submitBill: {
          type: 'void',
          'x-content': i18nExpression('dataConfMod.submitCheck'),
          'x-component': 'Button',
          'x-visible': '{{$form.query("CatalogOnShelvesVendor").get("data").showBtn.submitBill}}',
          'x-component-props': {
            type: 'primary',
            '@click': expression(`async (values) => {
              handleBtnCilck('submitBill', $form, $queryEngine, $bus)
           }`)
          }
        }
      }
    },
    properties: {
      ...pageInfo(scope, components, {
        statusJudge: 'REJECTED',
        priceSearchUrl: '/api-sup-ce/sup/catalogOnShelves/getPriceLibraryForCatalogOnShelves'
      })
    }
  }
})
</script>

<template>
  <RenderEngine :pageAttrs="attrs" :schema="schema" :components="components" :scope="scope" />
</template>

<style scoped lang="scss">
.off-cursor {
  cursor: pointer;
}

.search-po {
  float: right;
}

:deep(.el-input__clear) {
  font-size: 12px;
  width: 12px;
}

:deep(.el-input__suffix) {
  height: 28px;
  padding: 0 4px;
  color: #96999c;
  line-height: 28px;
  margin: 1px 0;

  &:hover {
    color: #0077ff;
    border-color: #96999c;
    background-color: #f6f6f6;
  }
}
</style>
