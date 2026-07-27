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
import pageInfo from 'lib@/compositionEngine/oneStopShopping/purchaseCatalogOnOrOff/pageInfo'
// @ts-ignore
import { setWarningTip } from 'lib@/utils/util'

const { app, t, emitTabRemove, confirmMessage } = usePageHelper()

const attrs: any = useAttrs()

const isReadOnly = ($form: any) => {
  const data = $form.query('CatalogOnShelves').get('data')
  return data.flag === 'view' || ['TO_BE_APPROVED', 'ON_SHELVES', 'REJECTED'].includes($form.values.status)
}

const $closeTab = ($bus: any) => {
  $bus.$emit('PurchaseCatalogOnOrOff')
  emitTabRemove(attrs.tabName)
}

const showBtn = ($form: any) => {
  const data = $form.query('CatalogOnShelves').get('data')
  return {
    toggleEdit: data.flag === 'view' &&
      !['TO_BE_APPROVED', 'ON_SHELVES', 'REJECTED', 'TO_BE_SUBMIT'].includes($form.values.status),
    saveBill:
      data.flag === 'add' ||
      (['TO_BE_ON_SHELVES'].includes($form.values.status) && data.flag !== 'view'),
    onBill:
      data.flag === 'add' ||
      (['TO_BE_ON_SHELVES', 'OFF_SHELVES'].includes($form.values.status) &&
        data.flag !== 'view'),
    passBill:
      ['TO_BE_APPROVED'].includes($form.values.status) && data.flag !== 'view',
    rejectBill:
      ['TO_BE_APPROVED'].includes($form.values.status) && data.flag !== 'view',
    offBill: ['ON_SHELVES'].includes($form.values.status) && data.flag !== 'view'
  }
}

// 管理
const toggleEdit = ($form: any) => {
  const data = $form.query('CatalogOnShelves').get('data')

  const readOnly = ['TO_BE_APPROVED', 'ON_SHELVES', 'REJECTED'].includes($form.values.status)
  if (!readOnly) data.flag = 'edit'
  // setTimeout(() => {
  //   $form.query('priceLibraryNo').take().setComponentProps({
  //     disabled: $form.query('CatalogOnShelves').get('data').isReadOnly || $form.values?.status === 'OFF_SHELVES'
  //   })
  // })
}

// 校验是否有图片上传并且有默认主图
const hasDefaultImg = ($form: any) => {
  const attaches = $form.query('CatalogOnShelves').get('data').catalogOnShelvesAttaches
  if (attaches.length < 1) {
    // '请至少上传一张物料图片！'
    app.$message.warning(i18nExpression('cusEntry.supplement20250205.uploadAtLeastOneMaterialImage'))
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
    // '请设置一张默认主图！'
    app.$message.warning(i18nExpression('cusEntry.supplement20250205.defaultMainImage'))
    return false
  }
  return true
}

// 保存
const saveBill = async ($form: any, $queryEngine: any) => {
  const { data } = await $queryEngine.request.baseRequest({
    type: 'CatalogOnShelves',
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
  const data = $form.query('CatalogOnShelves').get('data')

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

// 上架
const onBill = async ($form: any, $queryEngine: any, $bus: any) => {
  // 是否确认上架
  const confirmResult = await confirmMessage(t('dataConfMod.isSureOnshelves'))
  if (confirmResult !== 'confirm') return false

  await $queryEngine.request.baseRequest({
    type: 'CatalogOnShelves',
    action: 'onShelves',
    lang: 'zh-cn',
    payload: [$form.values],
    'query': {
      '*': {}
    }
  })
  app.$message.success(t('bidMod.onShelfSucc'))
  $closeTab($bus)
}

// 下架
const offBill = async ($form: any, $queryEngine: any, $bus: any) => {
  // 请输入拒绝原因
  let { value, action } = await app.$prompt(
    '',
    // '请输入下架原因'
    i18nExpression('cusEntry.supplement20250211.inputRemoveReason'),
    {
      confirmButtonText: t('common.confirm'),
      cancelButtonText: t('common.cancel')
    }
  )
  if (action !== 'confirm') return

  $form.values.offShelvesReason = value

  await $queryEngine.request.baseRequest({
    type: 'CatalogOnShelves',
    action: 'offShelves',
    lang: 'zh-cn',
    payload: [{
      catalogOnShelvesId: $form.values.catalogOnShelvesId,
      offShelvesReason: $form.values.offShelvesReason
    }],
    'query': {
      '*': {}
    }
  })
  app.$message.success(t('dataConfMod.offShevels'))
  $closeTab($bus)
}

// 通过
const passBill = async ($form: any, $queryEngine: any, $bus: any) => {
  // 是否确认通过
  const confirmResult = await confirmMessage(t('dataConfMod.surePass'))
  if (confirmResult !== 'confirm') return false

  await $queryEngine.request.baseRequest({
    type: 'CatalogOnShelves',
    action: 'accept',
    lang: 'zh-cn',
    payload: [$form.values.catalogOnShelvesId],
    'query': {
      '*': {},
      'catalogOnShelvesAttaches': {
          '*': {}
      }
    }
  })
  app.$message.success(t('dataConfMod.passed'))
  $closeTab($bus)
}

// 驳回
const rejectBill = async ($form: any, $queryEngine: any, $bus: any) => {
  // 是否确认驳回
  const confirmResult = await confirmMessage(t('dataConfMod.isReject'))
  if (confirmResult !== 'confirm') return false

  await $queryEngine.request.baseRequest({
    type: 'CatalogOnShelves',
    action: 'refuse',
    lang: 'zh-cn',
    payload: [$form.values.catalogOnShelvesId],
    'query': {
      '*': {},
      'catalogOnShelvesAttaches': {
          '*': {}
      }
    }
  })
  app.$message.success(t('dataConfMod.rejected'))
  $closeTab($bus)
}

// 事件处理钩子
const handleBtnCilck = async (method: any, $form?: any, $queryEngine?: any, $bus?: any) => {
  const methods: any = {
    saveBill,
    onBill,
    offBill,
    passBill,
    rejectBill
  }

  $form.validate().then(() => {
    if (!hasDefaultImg($form)) return
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
  CatalogOnShelves: {
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

            const data = $form.query('CatalogOnShelves').get('data')
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
          'x-reactions': expression(`() => {
            $self.visible = $form.query("CatalogOnShelves").get("data").showBtn.toggleEdit
          }`),
          // 'x-visible': '{{$form.query("CatalogOnShelves").get("data").showBtn.toggleEdit}}',
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
          'x-visible': '{{$form.query("CatalogOnShelves").get("data").showBtn.saveBill}}',
          'x-component-props': {
            type: 'primary',
            '@click': expression(`async (values) => {
              handleBtnCilck('saveBill', $form, $queryEngine)
           }`)
          }
        },
        // 上架
        onBill: {
          type: 'void',
          'x-content': i18nExpression('bidMod.shelvesUp'),
          'x-component': 'Button',
          'x-visible': '{{$form.query("CatalogOnShelves").get("data").showBtn.onBill}}',
          'x-component-props': {
            type: 'primary',
            '@click': expression(`async (values) => {
              handleBtnCilck('onBill', $form, $queryEngine, $bus)
           }`)
          }
        },
        // 通过
        passBill: {
          type: 'void',
          'x-content': i18nExpression('common.toApprove'),
          'x-component': 'Button',
          'x-visible': '{{$form.query("CatalogOnShelves").get("data").showBtn.passBill}}',
          'x-component-props': {
            type: 'primary',
            '@click': expression(`async (values) => {
              handleBtnCilck('passBill', $form, $queryEngine, $bus)
           }`)
          }
        },
        // 驳回
        rejectBill: {
          type: 'void',
          'x-content': i18nExpression('common.toRefuse'),
          'x-component': 'Button',
          'x-visible': '{{$form.query("CatalogOnShelves").get("data").showBtn.rejectBill}}',
          'x-component-props': {
            type: 'primary',
            '@click': expression(`async (values) => {
              handleBtnCilck('rejectBill', $form, $queryEngine, $bus)
           }`)
          }
        },
        // 下架
        offBill: {
          type: 'void',
          'x-content': i18nExpression('bidMod.shelvesDown'),
          'x-component': 'Button',
          'x-visible': '{{$form.query("CatalogOnShelves").get("data").showBtn.offBill}}',
          'x-component-props': {
            type: 'primary',
            '@click': expression(`async (values) => {
              handleBtnCilck('offBill', $form, $queryEngine, $bus)
           }`)
          }
        }
      }
    },
    properties: {
      ...pageInfo(scope, components, {
        statusJudge: 'OFF_SHELVES'
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
