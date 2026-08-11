<!-- eslint-disable quotes -->
<script setup lang="ts">
import {
  i18nExpression,
  expression,
  defineSchemas,
  generateXindexInOrder,
  generateCharFunctionExpression,
  toJS
} from '@meicloud/render-engine'
// @ts-ignore
import { useDebounceFn } from '@vueuse/core'
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
// @ts-ignore
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import { FileDynamic } from 'lib@/components/srm-components/file-dynamic'
import FileInfo from './components/collapseItem/fileInfo'
// @ts-ignore
import CompanyDemotionInfo from './components/collapseItem/companyDemotionInfo'
// @ts-ignore
import RelegationCategory from './components/collapseItem/relegationCategory'
// @ts-ignore
import RelegationEntity from './components/collapseItem/relegationEntity'
// @ts-ignore
import VendorImproveColumn from './components/collapseItem/vendorImproveColumn'
// @ts-ignore
import { setRepeatData, throttle } from 'lib@/utils/util'
import { useAttrs, ref } from 'vue-demi'
// @ts-ignore
import { getDictItem } from '@/api/common'
// @ts-ignore
import { adaptDictData } from '@/utils'
// @ts-ignore
import { bus } from 'lib@/components/render-engine/components/bus'

const { emitTabAdd, emitTabRemove, t: $t, app } = usePageHelper()

const $attrs: any = useAttrs()

const $goImprove = (row:any) => {
  app.$router.push({
    name: 'vendorImprovement',
    params: {
      from: 'portrait',
      row
    }
  })
}

const initButtonConfig = ($form: any) => {
  setTimeout(() => {
    const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
    const viewUpdateButton = $form.query('state').get('data').viewUpdateButton
    componentInstance.buttonConfigInfo.save.view = viewUpdateButton
    componentInstance.buttonConfigInfo.submit.view = viewUpdateButton
    componentInstance.buttonConfigInfo.save.name = app.$t('common.staging')
    componentInstance.buttonConfigInfo.submit.name = app.$t('common.submit')
    componentInstance.buttonConfigInfo.cancel.view = viewUpdateButton
    componentInstance.buttonConfigInfo.close.view = !viewUpdateButton
  }, 50)
}

const updateButtonConfig = ($form: any) => {
  setTimeout(() => {
    console.log('status', $form.query('state').get('data').orderStatus)
    const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
    const viewUpdateButton = $form.query('state').get('data').viewUpdateButton
    componentInstance.buttonConfigInfo.save.view = viewUpdateButton
    componentInstance.buttonConfigInfo.submit.view = viewUpdateButton
    componentInstance.buttonConfigInfo.cancel.view = viewUpdateButton
    componentInstance.buttonConfigInfo.close.view = !viewUpdateButton
    componentInstance.setWorkflowBusinessId($attrs.params.row.companyDemotionId)
    componentInstance.setWorkflowTabDisabled($attrs.params.flag !== 'approvalOnly')
    componentInstance.setWorkflowBusinessVariables({})
  }, 600)
}

const $closePageAndRefreshListPageData = ($bus: any) => {
  $bus.$emit('CompanyDemotionList')
  emitTabRemove($attrs.tabName)
}

const $getUserDemandObj = (val: any, $values: any, $form:any) => {
  $form.values.reviewUserIdList = ''
  $form.values.reviewUserNicknames = ''
  if (val.length > 0) {
    $form.values.reviewUserIdList = val.map(v => v.userId)
    $form.values.reviewUserNicknames = val.map(v => v.nickname).join(';')
  } else {
    $form.values.reviewUserIdList = val.userId || ''
    $form.values.reviewUserNicknames = val.nickname || ''
  }
}

const $getCompany = (val: any, $values: any, $form:any) => {
  $values.companyId = val ? val.companyId : ''
  $values.companyCode = val ? val.companyCode : ''
  $values.companyName = val ? val.companyName : ''
  $form.query('companyDemotionOrgs').take().setValue([])
  $form.query('companyDemotionCategories').take().setValue([])
  $setDemotionName($values)
  if (val.companyId) $getImproveData($form, val.companyId)
}

const $setDemotionName = async ($values: any) => {
  if (!$values.companyName || !$values.demotionType) return
  let demotionTypeDict = await getDictItem('DEMOTION_TYPE')
  let demotionTypeList = adaptDictData(demotionTypeDict.data, 'dict')
  let demotionTypeName = (demotionTypeList.find(v => v.value === $values.demotionType) || {}).label
  $values.demotionName = `${$values.companyName}-${demotionTypeName}`
}

const $getImproveData = ($form: any, vendorId: any) => {
  app
    .$http({
      url: '/api-pef/vendorImprove/getImproveFormDtoByVendorId',
      method: 'GET',
      params: {
        vendorId
      }
    })
    .then((res: any) => {
      console.log('res:::', res)
      $form.query('companyDemotionImproveList').take().setValue(res.data || [])
    })
}

const $setDemotionType = ($values: any, val: any, $form:any) => {
  $form.query('companyDemotionOrgs').take().setValue([])
  $form.query('companyDemotionCategories').take().setValue([])
  $values.demotionType = val
  $setDemotionName($values)
}

const $catSelectHandel = ($values: any, data: any, $form: any) => {
  if (data.length > 0) {
    let categoryIdArr = $values.companyDemotionCategories.map(v => v.categoryId)
    for (let item of data) {
      if (!categoryIdArr.includes(item.categoryId)) {
        delete item.companyDemotionCategoryId
        $values.companyDemotionCategories.push(item)
        $form.query('companyDemotionCategories').take().setValue($values.companyDemotionCategories)
      }
    }
    app
      .$http({
        url: '/api-sup/demotion/company-demotion-org/queryOrgsByParam',
        method: 'POST',
        data: {
          companyId: $values.companyId,
          categoryIds: $values.companyDemotionCategories.map(v => v.categoryId),
          demotionType: $values.demotionType
        },
        loading: true
      })
      .then(res => {
        let data = res.data.map(({ enableFlag, ...rest }) => {
          return {
            ...rest,
            enableFlag: 'Y'
          }
        })

        $form.query('companyDemotionOrgs').take().setValue(data)
      })
  }
}

const $viewDetail = (row:any, $values:any, $form:any) => {
  let companyId = $values.companyId
  if (!companyId) return
  app.$http({
    url: '/api-pef/scoring/perfOverallScore/listPerfByCompanyIdAndOrgIdListAndCategoryIdList',
    method: 'POST',
    data: { ...row, companyId, orgIdList: [row.orgId], categoryIdList: [row.categoryId] },
    loading: true
  }).then(res => {
    console.log('res:::', res)
    let { data = [] } = res
    // this.performenceDialog.tableData = data
    $form.query('ScoreDialog').take().setComponentProps({ visible: true })
    console.log($form)
    $form.values.scoreTable = data
  })
}

// 保存
const $saveBill = throttle(
  async (type: any, $form: any, $queryEngine: any, $confirm: any, $message: any, $bus: any,) => {
    const values = $form.values
    if (type === 'SAVE') {
      $submitData(type, values, $form, $queryEngine)
    } else if (type === 'SUBMIT') {
      $form
        .validate()
        .then(() => {
          if ($form.values.companyDemotionCategories.length === 0) {
            // '请添加升降级品类'
            app.$message.error(i18nExpression('perfMod.addGradeCategory'))
          } else {
            app.$confirm($t('vendorMod.relegation.OUrange'), i18nExpression('components.approvalHead.tips.tip'), {
              confirmButtonText: i18nExpression('common.confirm'),
              cancelButtonText: i18nExpression('components.common.cancel'),
              type: 'warning'
            }).then(() => {
              $submitData(type, values, $form, $queryEngine)
            }).catch((err) => {
              console.log(err)
            })
          }
        })
        .catch((err: any) => {
          console.log(err, 'err')
        })
    }
  },
  300,
)

const $submitData = (type: string, $values: any, $form: any, $queryEngine: any) => {
  const form = toJS($values)
  $queryEngine.request
    .baseRequest({
      type: 'CompanyDemotion',
      lang: 'zh-cn',
      loading: true,
      payload: [form],
      query: { '*': {} },
      action: 'save'
    })
    .then((res: any) => {
      if (res.data && res.data.length > 0) {
        app.$message.success($t('common.successSave'))
        const companyDemotionId = res.originalData?.records[0] || ''
        $form.values.companyDemotionId = companyDemotionId
        if (type === 'SUBMIT') {
          const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
          componentInstance.setWorkflowBusinessId(companyDemotionId)
          componentInstance.setWorkflowTabDisabled(false)
          componentInstance.setWorkflowBusinessVariables({})
          componentInstance.handlerAfter(type.toUpperCase(), () => {
            $cancel()
          })
          setTimeout(() => {
            $form.readPretty = true
            componentInstance.buttonConfigInfo.save.view = false
            componentInstance.buttonConfigInfo.submit.view = false
          }, 100)
        } else {
          emitTabRemove($attrs.tabName)
        }
        bus.$emit('CompanyDemotionList')
      }
    })
}
const $cancel = () => {
  // @ts-ignore
  emitTabRemove($attrs.tabName)
  bus.$emit('CompanyDemotionList')
}

// 打印

// @ts-ignore
const scope = {
  $attrs,
  app,
  emitTabRemove,
  $closePageAndRefreshListPageData,
  $saveBill,
  $getCompany,
  $setDemotionType,
  $catSelectHandel,
  initButtonConfig,
  updateButtonConfig,
  $getUserDemandObj,
  $cancel,
  $goImprove,
  $viewDetail
}
// @ts-ignore
const components = {
  CompanyDemotionInfo,
  RelegationCategory,
  RelegationEntity,
  VendorImproveColumn,
  FileInfo,
  FileDynamic
}

// @ts-ignore
const schema = defineSchemas({
  state: {
    type: 'void',
    'x-component': 'Fragment',
    'x-hidden': true,
    'x-data': {
      checkEnableFlag: 'Y',
      viewUpdateButton: true,
      orderStatus: 'DRAFT'
    }
  },
  // 基本信息
  CompanyDemotion: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component': 'el-container',
    'x-component-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    'x-data': {},
    'x-query-engine': {
      service: 'sup',
      actions: {
        read: {
          immediate: true,
          ready: expression(`() => {
            $form.readPretty = ['view', 'approvalOnly'].includes($attrs.params.flag)
            console.log('$form.readPretty',$form.readPretty)
            initButtonConfig($form)
            return $attrs.params.row.demotionNumber
          }`),
          transformRequest: expression(`(data, headers) => {
            data.payload = [
              $attrs?.params?.row?.companyDemotionId || $form.values.companyDemotionId,
            ]
            data.query['*'] = {}
            return data
          }`),
          onSuccess: expression(`(res) => {
            let detailData = res.data[0]
            $form.query('state').get('data').orderStatus = detailData.status
            $form.query('state').get('data').viewUpdateButton = ['DRAFT', 'REJECTED', 'WITHDRAW'].includes(detailData.status) || $attrs.params.flag === 'add'
            updateButtonConfig($form)
            $form.setValues({
              ...detailData
            })  
            $form.query('fileUploads').take(field => {
              field.componentProps.componentInstance.reLoadFileInfo()
            })
          }`)
        },
        save: {
          transformRequest: expression(`(data, headers) => {
             data.query['*'] = {}
             return data
          }`),
          onSuccess: expression(`(res) => {
            
          }`),
          // 标记当前 action 需要消费底层储存的级联删除数据
          cascadeDeletion: true
        }
      }
    },
    properties: {
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'CompanyDemotionEdit',
          '@listener': expression(`() => {
            $queryEngine.request.read()
          }`)
        }
      },
      SchemaWorkflow: {
        type: 'void',
        'x-component': 'SchemaWorkflow',
        'x-component-props': {
          params: {
            activeWorkflowTab: true
          },
          'business-id': expression('$form.values.companyDemotionId || null'),
          'business-type': 'companyDemotion',
          'ref-name': 'workflowMulti',
          'button-custom': expression('{}'),
          '@click-handler': expression(`(type) => {
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus)
          }`),
          '@submit-direct': expression(`(type) => {
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus)
          }`),
          '@confirm': expression(`(type, comment) => {
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus)
          }`),
          '@close-tab': expression(`() => {
            emitTabRemove($attrs.tabName)
          }`),
          '@update-integration-mode': expression(`(integrationMode) => {
            
          }`),
          '@afterProcessActionSuccess': expression(`(data)=>{
              // $bus.$emit('CompanyDemotionEdit')
            }`)
        },
        properties: {
          collapse: {
            type: 'void',
            'x-component': 'Collapse',
            properties: generateXindexInOrder({
              companyDemotionInfo: {
                ...CompanyDemotionInfo
              },
              relegationCategory: {
                ...RelegationCategory
              },
              relegationEntity: {
                ...RelegationEntity
              },
              vendorImproveColumn: {
                ...VendorImproveColumn
              },
              // 附件
              fileInfo: {
                ...FileInfo
              }
            })
          }
        }
      }
    }
  }
})
</script>

<template>
  <RenderEngine
    schemaKey="vendorDemotionDetail"
    :pageAttrs="$attrs"
    :schema="schema"
    :scope="scope"
    :components="components"
  />
</template>
<style  >
.demotiontooltip {
  background-color: rgba(250, 193, 7, 0.993) !important;
  font-size: 14px !important;
  color: black;
  font-weight: 400;
  white-space: pre-wrap;
}
</style>
