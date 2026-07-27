<!--
 * @Author: linyk7 linyk7@meicloud.com
 * @Date: 2023-03-10 10:24:41
 * @LastEditors: linyk7 linyk7@meicloud.com
 * @LastEditTime: 2023-06-16 15:15:25
 * @FilePath: \ui\src\modules\buyer\purchasingDemand\views\purchaseApplicationEngine\edit-engine.vue
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
-->
<!-- eslint-disable quotes -->
<script setup lang="ts">
// @ts-ignore
import { i18nExpression, expression, defineSchemas, generateXindexInOrder, observable } from '@meicloud/render-engine'
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
// @ts-ignore
import { usePageHelper } from "lib@/components/composables/usePageHelper"
// @ts-ignore
import MImport from 'lib@/components/import'
// @ts-ignore
import MaterialSelectDialog from './components/materialSelectDialog'
// @ts-ignore
import BatchMaintainDialog from './components/batchMaintainDialog'
// @ts-ignore
import BomVersionDialog, { viewVersion } from './components/bomVersionDialog'
// @ts-ignore
import BomDetailDialog from './components/bomDetailDialog'
// @ts-ignore
import { useAttrs, computed, ref, reactive, toRefs } from 'vue-demi'
// @ts-ignore
import { useDebounceFn } from '@vueuse/core'
// @ts-ignore
import _pick from 'lodash/pick'
// @ts-ignore
import { downloadFileLink, downloadFileLinkByPost } from 'lib@/utils/file'
// @ts-ignore
import { parseTime } from '@/utils'
// @ts-ignore
import { setWarningTip } from 'lib@/utils/util'
// @ts-ignore
import { Tooltip } from '@meicloud/element-ui'
// @ts-ignore
import { editTableFormItemValid, feedbackLayoutIsPopover } from 'lib@/components/render-engine/schema-segments'
// @ts-ignore
import CPagination from 'lib@/components/c-pagination'
// @ts-ignore
import { orderConfig } from '@/config/orderConfig'
// @ts-ignore
import BomVersionSearch from './components/bomVersionSearch'

const { emitTabRemove, t, app, http, buyer, getCurrentUserInfo, confirmMessage } = usePageHelper()

const attrs: any = useAttrs()

const isReadOnly = (() => {
  return !['add', 'edit'].includes(attrs.params.flag)
})()
console.log(isReadOnly, 'isReadOnly')

const $closeTab = ($bus: any) => {
  $bus.$emit('PrRequirementForBuyer')
  emitTabRemove(attrs.tabName)
}

const getTotalAmount = (n: any, $form: any) => {
  // 存起来一个初始需求数量
  if ($form.query('PrRequirementForBuyer').get('data').copyInit.bol) {
    $form.query('PrRequirementForBuyer').get('data').copyInit.num = +n
    $form.query('PrRequirementForBuyer').get('data').copyInit.bol = false
  }
}

const setTotalAmount = useDebounceFn(async ($form: any, row: any) => {
  if (row.applyStatus === 'RETURNING' && row.requirementQuantity >= $form.query('PrRequirementForBuyer').get('data').copyInit.num) {
    row.requirementQuantity = $form.query('PrRequirementForBuyer').get('data').copyInit.num
    return app.$message.warning(t('purchaseDemand.mustBeLessEqual'))
  }
  if (row.requirementQuantity && row.requirementQuantity <= 0) {
    return app.$message.warning(t('purchaseDemand.setTotalAmountTips1'))
  }
  row.totalAmount = Number(
    Number(row.notaxPrice || 0) * Number(row.requirementQuantity || 0),
  ).toFixed(2)
  setTimeout(() => {
    const totalAmountArr = $form.values.reqLineList.map((v:any) => v.totalAmount || 0)
    const totalBudget = totalAmountArr.reduce((p, c) => (Number(p) || 0) + (Number(c) || 0))
    $form.values.totalBudget = totalBudget
  }, 100)
}, 300)

const workflowStatus = ref('DRAFT')

const viewUpdateButton = computed(() => {
  return buyer() && !isReadOnly && workflowStatus.value !== 'APPROVED'
})
const disabledUpdateButton = computed(() => ['SUBMITTED', 'APPROVING'].includes(workflowStatus.value))

const getButtonConfig = ($form: any) => {
  setTimeout(() => {
    const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
    componentInstance.setWorkflowBusinessId($form.values.requirementHeadId || '')
    componentInstance.setWorkflowTabDisabled(attrs.params.flag !== 'approvalOnly')
    componentInstance.buttonConfigInfo.save.view = viewUpdateButton.value
    componentInstance.buttonConfigInfo.submit.view = viewUpdateButton.value
    componentInstance.buttonConfigInfo.save.disabled = disabledUpdateButton.value
    componentInstance.buttonConfigInfo.submit.disabled = disabledUpdateButton.value
    componentInstance.buttonConfigInfo.cancel.view = !isReadOnly
    componentInstance.buttonConfigInfo.close.view = isReadOnly
  })
}

const checkMaterialList = (categoryId: any, $form: any) => {
  const lastCategoryName = $form.query('PrRequirementForBuyer').get('data').lastCategoryName
  return new Promise(resolve => {
    if (
      $form.values.reqLineList.length && lastCategoryName.categoryId !== categoryId
    ) {
      confirmMessage(t('purchaseDemand.checkMaterialListConfirm'))
        .then(() => {
          // 不能只是清空数据，要触发下table的remove的方法，否则不能删除数据库
          $form.values.reqLineList = []
          console.log($form.query('.reqLineList').take(), 'reqLineList')
          app.$message.info(t('purchaseDemand.checkMaterialListTips1'))
          resolve(true)
        })
        .catch(() => {
          app.$message.info(t('purchaseDemand.checkMaterialListTips2'))
          resolve(false)
        })
    } else {
      resolve(true)
    }
  })
}

const getCategoryObj = async (val: any, $form: any, instance: any) => {
  let lastCategoryName = $form.query('PrRequirementForBuyer').get('data').lastCategoryName

  const flag = await checkMaterialList(val ? val.categoryId : '', $form)
  if (flag) {
    $form.values.categoryId = val ? val.categoryId : ''
    $form.values.categoryCode = val ? val.categoryCode : ''
    $form.values.categoryName = val ? val.categoryName : ''

    lastCategoryName.categoryId = val ? val.categoryId : ''
    lastCategoryName.categoryCode = val ? val.categoryCode : ''
    lastCategoryName.categoryName = val ? val.categoryName : ''
  } else {
    $form.values.categoryId = lastCategoryName.categoryId
    $form.values.categoryCode = lastCategoryName.categoryCode
    $form.values.categoryName = lastCategoryName.categoryName

    instance.$refs.quickSearchRef.setInputModel(lastCategoryName.categoryName)
  }
}

// 重置对象条件
const resetForm = (form: any) => {
  for (let i in form) {
    form[i] = ''
  }
}

const queryContent = (queryForm: any, $form: any) => {
  let pageInfo = $form.query('PrRequirementForBuyer').get('data').pageInfo
  http({
    url: '/api-base/material/materialItem/listMaterialByPurchaseCategoryNew',
    method: 'POST',
    data: {
      categoryId: queryForm.categoryId || $form.values.categoryId,
      materialCode: queryForm.materialCode,
      materialName: queryForm.materialName,
      organizationId: $form.values.organizationId,
      organizationName: $form.values.organizationName,
      ceeaPurchaseType: $form.values.ceeaPurchaseType,
      pageSize: pageInfo.pageSize,
      pageNum: pageInfo.pageNum
    },
    loading: true
  }).then((res: any) => {
    if (res && res.data) {
      Object.assign(pageInfo, {
        pageTotal: res.data.total,
        pageNum: pageInfo.pageNum,
        pageSize: res.data.pageSize
      })
      $form.query('.MaterialSelectDialog').take().setComponentProps({
        displayItemTable: res.data.list
      })
    }
  })
}

const openDialog = ($form: any) => {
  if (!$form.values.orgId || !$form.values.organizationId) {
    return app.$message.warning(t('purchaseDemand.openDialogWarning1'))
  }
  if (!$form.values.categoryName) {
    return app.$message.warning(t('purchaseDemand.openDialogWarning2'))
  }
  resetForm($form.query('PrRequirementForBuyer').get('data').queryForm)
  $form.query('PrRequirementForBuyer').get('data').queryForm.organizationName = $form.values.organizationName
  $form.query('PrRequirementForBuyer').get('data').queryForm.inputLevel = $form.values.categoryName
  queryContent($form.query('PrRequirementForBuyer').get('data').queryForm, $form)
  $form.query('.MaterialSelectDialog').take().setComponentProps({
    visible: true
  })
}

const addOneContent = (multipleSelection2: any, $form: any) => {
  if (multipleSelection2.length === 0) {
    return
  }

  const obj = {}
  const dataMap = multipleSelection2.map((item: any) => {
    const selectItem = {
      ...item,
      unit: item.unitName,
      unitCode: item.unit,
      totalAmount: (item.notaxPrice || 0) * (item.requirementQuantity || 0),
      ceeaIe: item.ceeaIfDirectory === 'Y' ? 'true' : 'false',
      businessSmall: $form.values.businessSmall,
      requirementQuantity: null,
      orgId: $form.values.orgId,
      orgCode: $form.values.orgCode,
      orgName: $form.values.orgName,
      organizationId: $form.values.organizationId,
      organizationCode: $form.values.organizationCode,
      organizationName: $form.values.organizationName
    }
    return selectItem
  })
  // 拿到最终的添加数据数组
  const newArr = dataMap.map((row: any) => {
    const o = JSON.parse(JSON.stringify(obj))
    Object.keys(row).forEach(key => {
      o[key] = row[key]
    })
    return o
  })
  let reqLineList = $form.values.reqLineList
  // 赋值表格
  $form.values.reqLineList = [
      ...newArr,
      ...reqLineList
    ]
  $form.query('.MaterialSelectDialog').take().setComponentProps({
    visible: false
  })
}

const beforeUpload = ($form: any) => {
  let otherParams = _pick($form.values, [
    'categoryCode',
    'categoryId',
    'categoryName',
    'ceeaAssetType',
    'ceeaProjectUserNickname',
    'ceeaDepartmentName',
    'ceeaPurchaseType',
    'demandType',
    'orgId',
    'orgName',
    'orgCode',
    'organizationCode',
    'organizationId',
    'organizationName',
    'requirementHeadId',
    'requirementHeadNum'
  ])
  $form.query('PrRequirementForBuyer').get('data').extraData = { ...$form.query('PrRequirementForBuyer').get('data').extraData, ...otherParams }
}

// 下载模板
const downloadTemplate = () => {
  downloadFileLink(
    '/api-sup-ce/pr/requirementLine/v2/downloadTemplate',
    t('purchaseDemand.importMaterialItemModelDownload'),
  ).catch(() => {
    app.$message.error(t('purchaseDemand.downloadFail'))
  })
}

// 上传成功
const handleSuccess = (res: any, $form: any) => {
  // 导入成功就刷新界面
  if (res.status === 'Y') {
    let resData = res.data
    let reqLineList = $form.values.reqLineList
    resData.forEach((row: any) => {
      // 计算行总价
      row.totalAmount = Number(
        Number(row.notaxPrice || 0) * Number(row.requirementQuantity || 0),
      ).toFixed(2)
    })
    // 赋值表格
    $form.values.reqLineList = [
      ...resData,
      ...reqLineList
    ]
    // 非生产性采购需要计算表头 总预算
    if ($form.values.demandType == 'NONPRODUCTIVE_DEMAND') {
      setTimeout(() => {
        const totalAmountArr = $form.values.reqLineList.map((v: any) => v.totalAmount || 0)
        const totalBudget = totalAmountArr.reduce((p: any, c: any) => (Number(p) || 0) + (Number(c) || 0))
        $form.values.totalBudget = totalBudget
      }, 100)
    }
  }
}

// 物料明细导出
const exportFile = async () => {
  downloadFileLinkByPost(
    `/api-sup-ce/pr/requirementLine/excelExport?requirementHeadId=${
      attrs.params.row.requirementHeadId
    }`,
    `${t('purchaseApplication.materialDetail')}${parseTime(new Date())}.xlsx` // 物料明细
  ).catch((err: any) => {
    app.$message.error(err.message)
  })
}

// 重新提交
const reSubmit = ($form: any, $queryEngine: any) => {
  let rows = $form.query('reqLineList').take()
    .componentProps
    .componentInstance
    .getCheckboxRecords()

  if (rows.length === 0) {
    app.$message({
      type: 'warning',
      message: t('purchaseDemand.pleaseSelectDetailRow')
    })
    return
  }
  for (const item of rows) {
    if (item.applyStatus !== 'RETURNING') {
      app.$message({
        type: 'warning',
        message: t('purchaseDemand.reSubmitTips1')
      })
      return
    }
  }

  const params = {
    requirementHead: $form.values,
    requirementLineList: rows
  }
  http({
    url: '/api-sup-ce/pr/requirementLine/resubmit',
    method: 'POST',
    data: params,
    loading: true
  })
    .then(() => {
      app.$message.success(t('common.success'))
      getFormDetail(attrs.params.row.requirementHeadId, $form, $queryEngine)
    })
    .catch((err: any) => {
      console.log(err)
    })
}

// 提交校验
const submitCheck = async ($form: any) => {
  if ($form.values.reqLineList.length === 0) {
    app.$message.warning(t('purchaseDemand.saveBillTips5'))
    return false
  }

  if ($form.values.ceeaPurchaseType === 'APPOINT') {
    // 此申请为指定采购，确认则提交采购申请单！
    const confirmSelectValue = await confirmMessage(t('purchaseDemand.saveBillConfirm1'))
    if (confirmSelectValue !== 'confirm') return false
  }

  return true
}

const getFormDetail = async (requirementHeadId: any, $form: any, $queryEngine: any) => {
  $queryEngine.request.read(requirementHeadId).then((res: any) => {
    $form.setValues(res.data[0])
  })
}

const submitEvent = async ($form: any, $queryEngine: any, $bus: any) => {
  const sign = await submitCheck($form)
  if (!sign) return

  let { data } = await $queryEngine.request.baseRequest({
    action: 'submitRequirement',
    payload: [{ ...$form.values, sourceFromType: "HAND_MAKE" }],
    loading: true
  })
  $form.setValues(data[0])

  app.$message.success(t('common.success'))
  $closeTab($bus)

  // const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
  // console.log(componentInstance, 'componentInstance')

  // if (componentInstance.srmFlowMode.includes(componentInstance.workflowParamsInfo.integrationMode)) {
  //   app.$message.success(t('common.success'))
  //   $closeTab($bus)
  // } else {
  //   componentInstance.setWorkflowBusinessId($form.values.requirementHeadId)
  //   componentInstance.setWorkflowTabDisabled(attrs.params.flag !== 'approvalOnly')
  //   componentInstance.setWorkflowBusinessVariables({})
  //   componentInstance.handlerAfter('SUBMIT', () => {
  //     $closeTab($bus)
  //   })
  //   console.log('handlerAfter')
  // }
}

const saveBill = ($form: any, $queryEngine: any) => {
  $queryEngine.request.baseRequest({ action: 'tempSaveRequirement', payload: [{ ...$form.values, sourceFromType: "HAND_MAKE" }], loading: true })
    .then((res: any) => {
      app.$message.success(t('common.success'))
      let requirementHeadId = res.data[0].requirementHeadId
      getFormDetail(requirementHeadId, $form, $queryEngine)
    })
}

const saveOrSubmitBill = async (type: string, $form: any, $queryEngine: any, $bus: any) => {
  $form.validate().then((res: any) => {
    if ($form.values.ceeaPrType === '01' && $form.values.reqAttachList.length === 0) {
      app.$message.error(t('purchaseDemand.saveBillTips3'))
      return
    }

    $form.values.reqLineList.map((v: any) => {
      v.orgId = $form.values.orgId
      v.orgCode = $form.values.orgCode
      v.orgName = $form.values.orgName
    })

    if (type === 'SUBMIT') {
      submitEvent($form, $queryEngine, $bus)
    } else {
      saveBill($form, $queryEngine)
    }
  }).catch((err: any) => {
    err.forEach((item: any) => {
      if (item.path.includes('vendorName')) {
        item.messages[0] = t('purchaseDemand.selectVendor')
      }
      if (item.path.includes('notaxPrice')) {
        item.messages[0] = t('purchaseDemand.prompt2') // 请输入预估含税单价
      }
    })
    setWarningTip(err)
  })
}

const importSlot = ($form: any) => {
  return {
    functional: true,
    render (h: any) {
      return h(Tooltip,
        {
          props: {
            placement: 'top',
            content: t('purchaseDemand.itemInfoTooltip'),
            disabled: isReadOnly || !!$form.values.requirementHeadNum
          }
        },
        [
          h(MImport,
            {
              attrs: {
                type: 'primary',
                title: t('common.excelImport'),
                upLoadUrl: '/api-sup-ce/pr/requirementLine/v2/import',
                disabled: isReadOnly ||
                  $form.values.auditStatus === 'APPROVED' ||
                  !$form.values.categoryId ||
                  !$form.values.organizationId ||
                  !$form.values.orgId,
                extraData: $form.query('PrRequirementForBuyer').get('data').extraData
              },
              on: {
                'beforeUpload': () => beforeUpload($form),
                'downloadTemplate': () => downloadTemplate(),
                'handleSuccess': (res: any) => handleSuccess(res, $form)
              }
            }
          )
        ]
      )
    }
  }
}

const $openBomVersionDialog = ($form:any, $queryEngine:any, row:any) => {
  $form.query('*.bomVersionDialog').take().setComponentProps({ visible: true })
  $getBomVersionList($form, $queryEngine, row)
}

// 获取bom版本
const $getBomVersionList = ($form:any, $queryEngine:any, row:any) => {
  $queryEngine.request.baseRequest({
    'type': 'BomHead',
    'lang': 'zh-cn',
    'query': {
      '*': {}
    },
    'payload': {
      "filter": {
        "materialId": {
          "eq": row.materialId
        },
        "organizationId": {
          "eq": $form.values.organizationId
        },
        "status": {
          "eq": "Y"// 直接传Y
        }
      },
      'page': {
        "sort": "creationDate desc",
        'pageNum': $form.query('PrRequirementForBuyer').get('data').bomVersionListPageNum,
        'pageSize': $form.query('PrRequirementForBuyer').get('data').bomVersionListPageSize
      }
    },
    'action': 'listBomByParam'
  }).then((res: any) => {
    $form.query('*.bomVersionDialog.*.bomVersionList').take((field: any) => {
      field.value = res.data
    })
    $form.query('PrRequirementForBuyer').get('data').bomVersionListTotal = res.originalData.payload.total
    // console.log($form.query('*.bomVersionDialog.*.bomVersionList').take()
    //   .componentProps
    //   .componentInstance.setRadioRow)
  })
}

// 设置bom版本
const $selBomVersion = ($form: any, done?: any) => {
  const row = $form.query('*.bomVersionDialog.*.bomVersionList').take()
    .componentProps
    .componentInstance
    .getRadioRecord()

  $form.values.reqLineList[$form.query('PrRequirementForBuyer').get('data').detailListCurrentIndex].bomVersionCode = row.versionCode
  $form.values.reqLineList[$form.query('PrRequirementForBuyer').get('data').detailListCurrentIndex].bomHeadId = row.bomHeadId
  if (done) {
    done()
  } else {
    $form.query('*.bomVersionDialog').take().setComponentProps({ visible: false })
  }
}

const $openBomVDetailDialog = ($form:any, row:any, $queryEngine:any) => {
  $form.query('*.bomDetailDialog').take().setComponentProps({ visible: true })
  $getBomDetailList($form, row, $queryEngine)
}

// 获取bom明细
const $getBomDetailList = ($form:any, row:any, $queryEngine:any) => {
  $queryEngine.request.baseRequest({
    'type': 'BomLine',
    'lang': 'zh-cn',
    'query': {
      '*': {}
    },
    'payload': {
      "filter": {
        "bomHeadId": {
          "eq": row.bomHeadId
        },
        "distributeFlag": {
          "eq": "Y"// 直接传Y
        }
      },
      'page': {
        "sort": "creationDate desc",
        'pageNum': $form.query('PrRequirementForBuyer').get('data').bomDetailListPageNum,
        'pageSize': $form.query('PrRequirementForBuyer').get('data').bomDetailListPageSize
      }
    },
    'action': 'listBomLineByParam'
  }).then((res: any) => {
    res.data.forEach((item:any) => {
      item.componentQuantity = row.requirementQuantity || row.requirementQuantity === 0 ? item.baseMaterialNum * +row.requirementQuantity : null
    })
    $form.query('*.bomDetailDialog.*.bomDetailList').take((field: any) => {
      field.value = res.data
    })
    $form.query('PrRequirementForBuyer').get('data').bomDetailListTotal = res.originalData.payload.total
  })
}

const scope = {
  t,
  app,
  http,
  $attrs: attrs,
  isReadOnly,
  emitTabRemove,
  $closeTab,
  getTotalAmount,
  setTotalAmount,
  getButtonConfig,
  workflowStatus,
  getCurrentUserInfo,
  getCategoryObj,
  openDialog,
  queryContent,
  addOneContent,
  beforeUpload,
  downloadTemplate,
  handleSuccess,
  exportFile,
  reSubmit,
  saveOrSubmitBill,
  importSlot,
  $viewVersion: viewVersion,
  $selBomVersion,
  $openBomVersionDialog,
  $getBomVersionList,
  $openBomVDetailDialog,
  orderConfig
}

const components = {
  MImport,
  MaterialSelectDialog,
  BatchMaintainDialog,
  Tooltip,
  BomVersionDialog,
  BomDetailDialog,
  CPagination,
  BomVersionSearch
}

const schema = defineSchemas({
  // 基本信息
  PrRequirementForBuyer: {
    type: 'void',
    'x-read-pretty': expression('$form.readPretty'),
    'x-component': 'el-container',
    'x-component-props': {
      class: 'flex-container PrRequirementForBuyer',
      direction: 'vertical'
    },
    'x-decorator': 'QueryEngine',
    'x-data': {
      bomVersionListPageNum: 1,
      bomVersionListPageSize: 15,
      bomVersionListTotal: 0,
      bomDetailListPageNum: 1,
      bomDetailListPageSize: 15,
      bomDetailListTotal: 0,
      detailListCurrentIndex: null,
      lastCategoryName: {
        categoryCode: '',
        categoryId: '',
        categoryName: 'xxx'
      },
      displayItemTable: [],
      extraData: {
        fileModular: 'pm',
        fileFunction: 'purchaseApplication',
        fileType: 'excel'
      },
      pageInfo: {
        pageTotal: 0,
        pageNum: 1,
        pageSize: 15
      },
      queryForm: {
        categoryCode: null,
        categoryId: null,
        categoryName: null,
        materialCode: null,
        materialId: null,
        materialName: null,
        orgId: null,
        organizationId: null,
        organizationName: null,
        inputLevel: null
      },
      copyInit: {
        num: 0,
        bol: true
      } // 需求数量初始值
    },
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        submitRequirement: {
          loading: true,
          // 标记当前 action 需要消费底层储存的级联删除数据
          cascadeDeletion: true,
          transformRequest: expression(`(data, headers) => {
            console.log('submitRequirement=>', data, headers)
            data.query = {
              '*': {}
            }

            return data
          }`)
        },
        tempSaveRequirement: {
          loading: true,
          // 标记当前 action 需要消费底层储存的级联删除数据
          cascadeDeletion: true,
          transformRequest: expression(`(data, headers) => {
            console.log('tempSaveRequirement=>', data, headers)
            data.query = {
              '*': {}
            }

            return data
          }`)
        },
        read: {
          loading: true,
          action: 'getRequirementInfo',
          immediate: true,
          ready: expression(`(v) => {
            console.log('ready=>', $form)
            getButtonConfig($form)
            $values.requirementHeadId = $attrs.params?.row?.requirementHeadId
            const { nickname, ceeaDeptId, department } = getCurrentUserInfo()
            $values.createdFullName = nickname
            $values.ceeaDepartmentId = ceeaDeptId
            $values.ceeaDepartmentName = department
            return !!$attrs.params?.row?.requirementHeadId
          }`),
          transformRequest: expression(`(data, headers) => {
            console.log('transformRequest=>', data, headers)
            data.payload = [{requirementHeadId: $attrs?.params?.row?.requirementHeadId || data.payload[0]}]

            data.query = {
              '*': {}
            }

            return data
          }`),
          onSuccess: expression(`(res) => {
            console.log('onsuccess=>', res.data)
            let data = res.data[0]
            workflowStatus.value = data.auditStatus
            $form.setValues(data)
            $self.data.lastCategoryName = {
              categoryId: data.categoryId,
              categoryName: data.categoryName,
              categoryCode: data.categoryCode,
            }
            $form.readPretty = isReadOnly
          }`)
        }
      }
    },
    properties: {
      BatchMaintainDialog: {
        type: 'void',
        'x-component': 'BatchMaintainDialog',
        'x-component-props': {
          requirementHead: '{{$form.values}}',
          '@submit': expression(`(form) => {
            let rows = $form.query('reqLineList').take()
            .componentProps
            .componentInstance
            .getCheckboxRecords()
            rows.forEach(row => {
              Object.keys(form).forEach(key => {
                row[key] = form[key]
              })
            })
            $form.query('.BatchMaintainDialog').take().setComponentProps({
              visible: false
            })
          }`),
          '@close': expression(`() => {
            $form.query('.BatchMaintainDialog').take().setComponentProps({
              visible: false
            })
          }`)
        }
      },
      // 物料明细新增弹窗
      MaterialSelectDialog: {
        type: 'void',
        'x-component': 'MaterialSelectDialog',
        'x-component-props': {
          requirementHead: '{{$form.values}}',
          queryForm: `{{$form.query('PrRequirementForBuyer').get('data').queryForm}}`,
          pageInfo: `{{$form.query('PrRequirementForBuyer').get('data').pageInfo}}`,
          displayItemTable: `{{$form.query('PrRequirementForBuyer').get('data').displayItemTable}}`,
          '@queryContent': expression(`obj => {
            $form.query('PrRequirementForBuyer').get('data').queryForm = Object.assign($form.query('PrRequirementForBuyer').get('data').queryForm, obj)
            queryContent($form.query('PrRequirementForBuyer').get('data').queryForm, $form)
          }`),
          '@changeCurrentIndex': expression(`(currentIndex) => {
            $form.query('PrRequirementForBuyer').get('data').pageInfo.pageNum = currentIndex
            queryContent($form.query('PrRequirementForBuyer').get('data').queryForm, $form)
          }`),
          '@changeCurrentSize': expression(`(currentSize) => {
            $form.query('PrRequirementForBuyer').get('data').pageInfo.pageSize = currentSize
            queryContent($form.query('PrRequirementForBuyer').get('data').queryForm, $form)
          }`),
          '@addOneContent': expression(`(multipleSelection2) => {
            addOneContent(multipleSelection2, $form)
          }`),
          '@close': expression(`() => {
            $form.query('.MaterialSelectDialog').take().setComponentProps({
              visible: false
            })
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
          'business-id': expression(`$form.values.requirementHeadId || null`),
          'business-type': 'MQL_PR_REQUIREMENT_INIT',
          'button-custom': expression(`{}`),
          '@click-handler': expression(`(type) => {
            saveOrSubmitBill(type, $form, $queryEngine, $bus)
          }`),
          '@submit-direct': expression(`(type) => {
            saveOrSubmitBill(type, $form, $queryEngine, $bus)
          }`),
          '@confirm': expression(`(type, comment) => {
            saveOrSubmitBill(type, $form, $queryEngine, $bus)
          }`),
          '@close-tab': expression(`() => {
            $closeTab($bus)
          }`),
          '@update-integration-mode': expression(`(integrationMode) => {
            console.log('update-integration-mode', integrationMode)
            // updateButtonConfig($form)
          }`)
        },
        properties: {
          collapse: {
            type: 'void',
            'x-component': 'Collapse',
            'x-component-props': {
              defaultOpenPanelCount: 1
            },
            properties: {
              // 基础信息
              form: {
                type: 'void',
                'x-component': 'CollapseItem',
                'x-component-props': {
                  title: i18nExpression('supRisk.baseInfo')
                },
                properties: {
                  layout: {
                    type: 'void',
                    'x-decorator': 'FormLayout',
                    'x-decorator-props': {
                      layout: 'vertical'
                    },
                    'x-component': 'FormGrid',
                    'x-component-props': {
                      minColumns: 1,
                      maxColumns: 4,
                      columnGap: 32,
                      rowGap: 0
                    },
                    properties: {
                      // 申请编号
                      requirementHeadNum: {
                        type: 'string',
                        title: i18nExpression('purchaseDemand.requirementHeadNum'),
                        'x-component-props': {
                          disabled: true
                        },
                        'x-decorator': 'FormItem'
                      },
                      // 单据状态
                      auditStatus: {
                        type: 'string',
                        title: i18nExpression('purchaseDemand.applyStatus'),
                        'x-component': 'DictSelect',
                        'x-component-props': {
                          disabled: true,
                          code: 'APPROVAL_STATUS'
                        },
                        'x-decorator': 'FormItem'
                      },
                      // 申请人
                      createdFullName: {
                        type: 'string',
                        'x-decorator': 'FormItem',
                        title: i18nExpression('purchaseDemand.applicant'),
                        'x-component-props': {
                          disabled: true
                        }
                      },
                      // 申请部门
                      ceeaDepartmentName: {
                        type: 'string',
                        title: i18nExpression('purchaseDemand.ceeaDepartment'),
                        'x-component-props': {
                          disabled: true
                        },
                        'x-decorator': 'FormItem'
                      },
                      // 申请日期
                      applyDate: {
                        type: 'date',
                        title: i18nExpression('purchaseDemand.applyDate'),
                        'x-decorator': 'FormItem',
                        'x-component-props': {
                          format: 'yyyy-MM-dd',
                          disabled: true
                        }
                      },
                      // 业务实体
                      orgId: {
                        type: 'string',
                        title: i18nExpression('purchaseDemand.businessEntity'),
                        'x-decorator': 'FormItem',
                        'x-component': 'OrganizationSelector',
                        'x-component-props': {
                          readPretty: '{{$form.readPretty}}',
                          'parent-id': -1,
                          'node-type': 'OU',
                          disabled: `{{
                            isReadOnly ||
                            $values.auditStatus === 'APPROVED' ||
                            !!$values?.reqLineList?.length
                          }}`,
                          '@select': expression(`(node) => {
                            $values.orgId = node ? String(node.organizationId) : null
                            $values.orgCode = node ? String(node.organizationCode) : null
                            $values.orgName = node ? node.organizationName : null

                            if($form.values.organizationId){
                              $form.values.organizationId = null
                              $form.values.organizationCode = null
                              $form.values.organizationName = null
                            }
                          }`)
                        },
                        'x-validator': {
                          required: true,
                          message: i18nExpression('purchaseDemand.orgIdTips')
                        }
                      },
                      // 库存组织
                      organizationId: {
                        type: 'string',
                        title: i18nExpression('purchaseDemand.invOrg'),
                        'x-decorator': 'FormItem',
                        'x-component': 'OrganizationSelector',
                        'x-component-props': {
                          readPretty: '{{$form.readPretty}}',
                          'parent-id': '{{$values.orgId}}',
                          'node-type': 'INV',
                          disabled: `{{
                            isReadOnly ||
                            $values.auditStatus === 'APPROVED' ||
                            !!$values?.reqLineList?.length
                          }}`,
                          '@select': expression(`(node) => {
                            $values.organizationId = node ? String(node.organizationId) : null
                            $values.organizationCode = node ? String(node.organizationCode) : null
                            $values.organizationName = node ? node.organizationName : null
                          }`)
                        },
                        'x-validator': {
                          required: true,
                          message: i18nExpression('purchaseDemand.organizationIdTips')
                        }
                      },
                      // 需求类型
                      demandType: {
                        type: 'string',
                        title: i18nExpression('purchaseDemand.demandType'),
                        'x-component': 'DictSelect',
                        'x-component-props': {
                          code: 'DEMAND_TYPE',
                          disabled: `{{isReadOnly || $values.auditStatus === 'APPROVED'}}`,
                          '@change': expression(`(val) => {
                            // 不是非生产性需求清空预算编号
                            if ($values.demandType !== 'NONPRODUCTIVE_DEMAND') {
                              $values.budgetManagementNum = null
                              $values.budgetManagementId = null
                              $values.totalBudget = null
                              $values.usedBudget = null
                              $values.unusedBudget = null
                              $values.reqLineList.forEach(item => {
                                item.totalAmount = 0
                                item.notaxPrice = 0
                              })
                            }
                          }`)
                        },
                        'x-decorator': 'FormItem',
                        'x-validator': {
                          required: true,
                          message: i18nExpression('purchaseDemand.selectRequireType')
                        }
                      },
                      // 采购类型
                      ceeaPurchaseType: {
                        type: 'string',
                        title: i18nExpression('purchaseDemand.purchaseType'),
                        'x-component': 'DictSelect',
                        'x-component-props': {
                          disabled: `
                            {{
                              isReadOnly ||
                              $values.auditStatus === 'APPROVED' ||
                              !!$values?.reqLineList?.length
                            }}
                          `,
                          code: 'PURCHASE_TYPE'
                        },
                        'x-decorator': 'FormItem',
                        'x-validator': {
                          required: true,
                          message: i18nExpression('purchaseDemand.purchaseTypeTips')
                        }
                      },
                      // 物料大类
                      categoryName: {
                        type: 'string',
                        'x-decorator': 'FormItem',
                        title: i18nExpression('purchaseDemand.materialCate'),
                        'x-component': 'QuickSearchWrapper',
                        'x-component-props': {
                          readPretty: '{{$form.readPretty}}',
                          showKey: 'categoryName',
                          showInput: '{{$values.categoryName}}',
                          disabled: `{{
                            isReadOnly || $values.auditStatus === 'APPROVED'
                          }}`,
                          name: 'scc_base_purchase_category3',
                          '@close-quicksearch': expression(`(val, instance) => {
                            getCategoryObj(val, $form, instance)
                          }`)
                        },
                        'x-validator': {
                          required: true,
                          message: i18nExpression('purchaseDemand.inputCategoryName')
                        }
                      },
                      // 采购项目
                      purchaseProject: {
                        type: 'string',
                        'x-decorator': 'FormItem',
                        title: i18nExpression('purchaseDemand.purchaseItem'),
                        'x-component-props': {
                          maxlength: '50',
                          showWordLimit: true,
                          disabled: `{{
                            isReadOnly || $values.auditStatus === 'APPROVED'
                          }}`
                        }
                      },
                      // 预算编号
                      budgetManagementNum: {
                        type: 'string',
                        'x-decorator': 'FormItem',
                        'x-hidden': `{{$values.demandType !== 'NONPRODUCTIVE_DEMAND'}}`,
                        title: i18nExpression('purchaseDemand.budgetNumber'),
                        'x-component': 'QuickSearch',
                        'x-component-props': {
                          disabled: `{{
                            isReadOnly || $values.auditStatus === 'APPROVED'
                          }}`,
                          readPretty: '{{$form.readPretty}}',
                          'show-key': 'budgetManagementNumber',
                          'show-input': '{{$values.budgetManagementNum}}',
                          name: 'scc_pb_budget_management_effective',
                          '@close-quicksearch': expression(`(val) => {
                            $values.budgetManagementNum = val ? val.budgetManagementNumber : ''
                            $values.budgetManagementId = val ? val.budgetManagementId : ''
                          }`)
                        },
                        'x-validator': {
                          required: true,
                          message: '请选择预算编号'
                        }
                      },
                      // 预算金额
                      totalBudget: {
                        type: 'string',
                        'x-decorator': 'FormItem',
                        'x-hidden': `{{$values.demandType !== 'NONPRODUCTIVE_DEMAND'}}`,
                        title: i18nExpression('purchaseDemand.ceeaTotalBudget'),
                        'x-component-props': {
                          disabled: true
                        }
                      },
                      // 实际已用金额
                      usedBudget: {
                        type: 'string',
                        'x-decorator': 'FormItem',
                        'x-hidden': `{{$values.demandType !== 'NONPRODUCTIVE_DEMAND'}}`,
                        title: i18nExpression('purchaseDemand.actualAmountUsed'),
                        'x-component-props': {
                          disabled: true
                        }
                      },
                      // 剩余可用预算
                      unusedBudget: {
                        type: 'string',
                        'x-decorator': 'FormItem',
                        'x-hidden': `{{$values.demandType !== 'NONPRODUCTIVE_DEMAND'}}`,
                        title: i18nExpression('purchaseDemand.availableBudget'),
                        'x-component-props': {
                          disabled: true
                        }
                      },
                      createdId: {
                        type: 'void',
                        'x-decorator': 'FormItem',
                        'x-visible': true,
                        'x-query-engine-skip': true
                      },
                      // 备注
                      comments: {
                        type: 'string',
                        'x-decorator': 'FormItem',
                        'x-decorator-props': { gridSpan: 4 },
                        title: i18nExpression('contractMod.remark'),
                        'x-component-props': {
                          type: 'textarea',
                          maxlength: '500',
                          showWordLimit: true,
                          disabled: `{{
                            isReadOnly || $values.auditStatus === 'APPROVED'
                          }}`,
                          autosize: { minRows: 2, maxRows: 5 }
                        }
                      },
                      // 紧急情况说明
                      ceeaUrgencyExplain: {
                        type: 'string',
                        'x-decorator': 'FormItem',
                        'x-decorator-props': { gridSpan: 4 },
                        'x-hidden': `{{$values.ceeaPurchaseType !== 'URGENT'}}`,
                        title: i18nExpression('purchaseDemand.ceeaUrgencyExplain'),
                        'x-component-props': {
                          type: 'textarea',
                          maxlength: '500',
                          showWordLimit: true,
                          disabled: `{{
                            isReadOnly || $values.auditStatus === 'APPROVED'
                          }}`,
                          rows: 2
                        },
                        'x-validator': {
                          required: true,
                          message: i18nExpression('purchaseDemand.ceeaUrgencyExplainTips')
                        }
                      },
                      // 指定原因
                      ceeaAppointReason: {
                        type: 'string',
                        'x-decorator': 'FormItem',
                        'x-decorator-props': { gridSpan: 4 },
                        'x-hidden': `{{$values.ceeaPurchaseType !== 'APPOINT'}}`,
                        title: i18nExpression('purchaseDemand.ceeaAppointReason'),
                        'x-component-props': {
                          type: 'textarea',
                          maxlength: '500',
                          showWordLimit: true,
                          disabled: `{{
                            isReadOnly || $values.auditStatus === 'APPROVED'
                          }}`,
                          rows: 2
                        },
                        'x-validator': {
                          required: true,
                          message: i18nExpression('purchaseDemand.ceeaAppointReasonTips')
                        }
                      }
                    }
                  }
                }
              },
              // 物料明细
              lineList: {
                type: 'void',
                'x-component': 'CollapseItem',
                'x-component-props': {
                  title: i18nExpression('purchaseDemand.itemInfo')
                },
                properties: {
                  toolbar: {
                    type: 'void',
                    'x-component': 'Space',
                    'x-component-props': {
                      style: 'margin-bottom: 12px'
                    },
                    'x-reactions': expression(`(field) => {
                      field.visible = !$form.readPretty
                    }`),
                    properties: {
                      // 新增
                      add: {
                        type: 'void',
                        'x-component': 'RButton',
                        'x-hidden': `{{$values.auditStatus === 'APPROVED'}}`,
                        title: i18nExpression('common.add'),
                        'x-component-props': {
                          type: 'primary',
                          disabled: '{{isReadOnly}}',
                          '@click': expression(`() => {
                            openDialog($form)
                            // $self.query('.reqLineList').take().componentProps.componentInstance.addRow('unshift')
                          }`)
                        }
                      },
                      import: {
                        type: 'void',
                        title: i18nExpression('common.excelImport'),
                        'x-content': {
                          default: `{{importSlot($form)}}`
                        }
                      },
                      // 导出
                      export: {
                        type: 'void',
                        'x-component': 'RButton',
                        'x-hidden': `{{$values?.reqLineList?.length < 1 || $attrs.params.flag === 'add'}}`,
                        title: i18nExpression('purchaseDemand.export'),
                        'x-component-props': {
                          type: 'primary',
                          disabled: `{{isReadOnly || $values.auditStatus === 'APPROVED'}}`,
                          '@click': expression(`() => exportFile()`)
                        }
                      },
                      // 重新提交
                      reSubmit: {
                        type: 'void',
                        'x-component': 'RButton',
                        'x-hidden': `{{$values?.reqLineList?.length < 1 || $values?.reqLineList?.find(v => v.applyStatus !== 'RETURNING')}}`,
                        title: i18nExpression('purchaseDemand.resubmit'),
                        'x-component-props': {
                          type: 'primary',
                          style: {
                            'margin-left': 0
                          },
                          disabled: `{{isReadOnly}}`,
                          '@click': expression(`() => reSubmit($form, $queryEngine)`)
                        }
                      },
                      // 批量维护
                      batchMaintain: {
                        type: 'void',
                        'x-component': 'RButton',
                        'x-hidden': `{{!['edit', 'add'].includes($attrs.params.flag)}}`,
                        title: i18nExpression('vendorMod.batchMaintain'),
                        'x-component-props': {
                          type: 'primary',
                          '@click': expression(`() => {
                            let rows = $form.query('reqLineList').take()
                            .componentProps
                            .componentInstance
                            .getCheckboxRecords()
                            if (rows.length < 1) {
                              app.$message.warning(t('purchaseDemand.selectAtLeastOneData'))
                              return
                            }
                            $form.query('.BatchMaintainDialog').take().setComponentProps({
                              visible: true
                            })
                          }`)
                        }
                      }
                    }
                  },
                  reqLineList: {
                    type: 'array',
                    'x-component': 'RenderTable',
                    'x-component-props': {
                      preColumns: 'checkbox',
                      height: 250,
                      pagination: false, // { static: false }, // 开启前端分页
                      sortable: false,
                      // 联表主键的 key
                      primaryKey: 'requirementLineId',
                      // 启用级联删除的储值行为
                      cascadeDeletion: true,
                      scrollY: {
                        enabled: '{{$form.readPretty ? true : false}}'
                      }
                    },
                    'x-query-engine-skip': true,
                    'x-query-engine-relation': 'reqLineList:*',
                    properties: generateXindexInOrder({
                      requirementLineId: {
                        // 主键ID
                        type: 'string',
                        'x-hidden': true
                      },
                      // 行号
                      rowNum: {
                        type: 'string',
                        'x-component': 'RenderTableIndex',
                        title: i18nExpression('purchaseDemand.lineNum'),
                        'x-render-table-column': {
                          width: 80
                        }
                      },
                      // 行状态
                      applyStatus: {
                        type: 'string',
                        title: i18nExpression('purchaseDemand.applicationBankStatus'),
                        'x-component': 'DictSelect',
                        'x-component-props': {
                          code: 'APPLICATION_STATUS'
                        },
                        'x-render-table-column': {
                          width: 120
                        }
                      },
                      materialCode: {
                        // 物料编码
                        type: 'string',
                        title: i18nExpression('purchaseDemand.itemCode'),
                        'x-render-table-column': {
                          width: 120
                        }
                      },
                      materialName: {
                        // 物料名称
                        type: 'string',
                        title: i18nExpression('purchaseDemand.itemName'),
                        'x-render-table-column': {
                          width: 130
                        }
                      },
                      unitCode: {
                        // 单位
                        type: 'string',
                        title: i18nExpression('purchaseDemand.unitCode'),
                        'x-component': 'DictSelect',
                        'x-component-props': {
                          code: 'unit'
                        },
                        'x-render-table-column': {
                          width: 100
                        }
                      },
                      requirementQuantity: {
                        // 需求数量
                        type: 'string',
                        'x-component': 'el-input-number',
                        'x-component-props': {
                          controls: false,
                          class: 'input-number-precision',
                          disabled: `{{
                            isReadOnly ||
                            $values.auditStatus === 'APPROVED' &&
                            $table.getRowByIndex($self.index).applyStatus !== 'RETURNING'
                          }}`,
                          '@focus': `{{getTotalAmount($table.getRowByIndex($self.index)?.requirementQuantity, $form)}}`,
                          '@change': `{{() => setTotalAmount($form, $table.getRowByIndex($self.index))}}`
                        },
                        'x-render-table-column': {
                          width: 90,
                          customRender: true,
                          title: i18nExpression('purchaseDemand.requirementQuantity')
                        },
                        ...feedbackLayoutIsPopover,
                        'x-validator': {
                          required: true,
                          message: i18nExpression('purchaseDemand.selectRequireQuantity')
                        }
                      },
                      ceeaExecutedQuantity: {
                        // 已下单数量
                        type: 'string',
                        title: i18nExpression('purchaseDemand.ceeaExecutedQuantity'),
                        'x-render-table-column': {
                          width: 100
                        }
                      },
                      requirementDate: {
                        // 需求日期
                        type: 'date',
                        'x-component-props': {
                          disabled: `{{
                            isReadOnly ||
                            $values.auditStatus === 'APPROVED' &&
                            $table.getRowByIndex($self.index).applyStatus !== 'RETURNING'
                          }}`
                        },
                        'x-render-table-column': {
                          width: 150,
                          customRender: true,
                          title: i18nExpression('purchaseDemand.requirementDate'),
                          format: 'yyyy-MM-dd',
                          'value-format': 'yyyy-MM-dd',
                          'picker-options': {
                            disabledDate (time: any) {
                              const today = new Date()
                              today.setHours(0)
                              today.setMinutes(0)
                              today.setSeconds(0)
                              today.setMilliseconds(0)
                              return time.getTime() < today.getTime()
                            }
                          }
                        },
                        ...feedbackLayoutIsPopover,
                        'x-validator': {
                          required: true,
                          message: i18nExpression('purchaseDemand.selectRequireDate')
                        }
                      },
                      receiveAddress: {
                        // 收货地址
                        type: 'string',
                        'x-component': 'DictSelect',
                        'x-component-props': {
                          code: `{{String($values.organizationId)}}`,
                          disabled: `{{
                            isReadOnly ||
                            $values.auditStatus === 'APPROVED' ||
                            $table.getRowByIndex($self.index).applyStatus === 'RETURNING'
                          }}`,
                          'custom-select-type': `{{$values.organizationId ? 'RECEIVE_ADDRESS' : ''}}`,
                          '@change-value': expression(`(val, {element}) => {
                            let row = $table.getRowByIndex($self.index)
                            row.receiveContact = element ? element.receiver : ''
                            row.receiveTelephone = element ? element.receiverPhone : ''
                            row.receiveAddress = element ? element.siteName : ''
                          }`)
                        },
                        'x-render-table-column': {
                          width: 150,
                          customRender: true,
                          title: i18nExpression('purchaseDemand.ceeaDeliveryPlaceOut')
                        },
                        ...feedbackLayoutIsPopover,
                        'x-validator': {
                          required: true,
                          message: i18nExpression('purchaseDemand.selectReceiveAddress')
                        }
                      },
                      bomVersionCode: {
                        type: 'string',
                        'x-visible': '{{orderConfig.showBom === \'Y\' && $form.values.ceeaPurchaseType === \'OUTSOURCING\'}}',
                        'x-render-table-column': {
                          minWidth: 150,
                          title: 'Bom版本', // Bom版本
                          customRender: true
                        },
                        'x-component': 'BomVersionSearch',
                        'x-component-props': {
                          disabled: `{{isReadOnly}}`,
                          inputModel: `{{$self.value}}`,
                          '@clear': expression(`value => {
                            $form.query('PrRequirementForBuyer').get('data').detailListCurrentIndex = $self.index
                            $form.values.reqLineList[$self.index].bomVersionCode = ''
                            $form.values.reqLineList[$self.index].bomHeadId = null
                          }`),
                          '@openDialog': expression(`() => {
                            $form.query('PrRequirementForBuyer').get('data').detailListCurrentIndex = $self.index
                            $openBomVersionDialog($form, $queryEngine,$table.getRowByIndex($self.index))
                          }`)
                        }
                      },
                      bomDetail: {
                        type: 'void',
                        'x-visible': '{{orderConfig.showBom === \'Y\' && $form.values.ceeaPurchaseType === \'OUTSOURCING\'}}',
                        'x-render-table-column': {
                          customRender: true,
                          title: i18nExpression('purchaseApplication.bomDetail'), // 'BOM明细'
                          minWidth: 100,
                          sortable: false
                        },
                        properties: {
                          layout: {
                            type: 'void',
                            'x-component': 'Space',
                            properties: {
                              viewFollowUp: {
                                type: 'void',
                                title: i18nExpression('purchaseApplication.detail'), // 详情
                                'x-component': 'TableButton',
                                'x-hidden': `{{!$table.getRowByIndex($self.index).bomVersionCode}}`,
                                'x-component-props': {
                                  type: 'text',
                                  '@click': expression('({row}) => {$openBomVDetailDialog($form,row, $queryEngine)}')
                                }
                              }
                            }
                          }
                        }
                      },
                      comments: {
                        // 备注
                        type: 'string',
                        title: i18nExpression('purchaseDemand.comments'),
                        'x-component-props': {
                          maxlength: 50,
                          showWordLimit: true,
                          disabled: `{{isReadOnly || $values.auditStatus === 'APPROVED'}}`
                        },
                        'x-render-table-column': {
                          customRender: true,
                          width: 150
                        }
                      },
                      vendorName: {
                        // 指定供应商
                        type: 'string',
                        'x-component': 'QuickSearch',
                        'x-component-props': {
                          preQueryData: `{{{'c.CATEGORY_ID':$table.getRowByIndex($self.index).categoryId}}}`,
                          showInput: `{{$table.getRowByIndex($self.index).vendorName}}`,
                          showKey: 'companyName',
                          name: 'scc_sup_company_info_all',
                          disabled: `{{
                            isReadOnly ||
                            $table.getRowByIndex($self.index).ceeaIfDirectory === 'Y' ||
                            $values.auditStatus === 'APPROVED' ||
                            $values.ceeaPurchaseType !== 'APPOINT'
                          }}`,
                          '@close-quicksearch': expression(`(val) => {
                            let row = $table.getRowByIndex($self.index)
                            row.vendorId = val ? val.companyId : ''
                            row.vendorCode = val ? val.companyCode : ''
                            row.vendorName = val ? val.companyName : ''
                          }`)
                        },
                        'x-render-table-column': {
                          customRender: true,
                          required: `{{$form.values.ceeaPurchaseType  === 'APPOINT'}}`,
                          width: 150,
                          title: i18nExpression('purchaseDemand.awardedSupplierName')
                        },
                        ...feedbackLayoutIsPopover,
                        'x-validator': {
                          required: `{{$form.values.ceeaPurchaseType  === 'APPOINT'}}`,
                          messages: i18nExpression('purchaseDemand.selectVendor')
                        }
                      },
                      dmandLineRequest: {
                        // 需求部门
                        type: 'string',
                        title: i18nExpression('purchaseDemand.dmandLineRequest'),
                        'x-component': 'DictSelect',
                        'x-component-props': {
                          code: 'DMAND_LINE_REQUEST',
                          disabled: `{{
                            isReadOnly ||
                            ($values.auditStatus === 'APPROVED' &&
                            $table.getRowByIndex($self.index).applyStatus !== 'RETURNING')
                          }}`
                        },
                        'x-render-table-column': {
                          customRender: true,
                          width: 150
                        }
                      },
                      notaxPrice: {
                        // 预估含税单价
                        type: 'string',
                        'x-hidden': `{{$values.demandType !== 'NONPRODUCTIVE_DEMAND'}}`,
                        'x-component-props': {
                          'v-input-format': { type: 'float' },
                          disabled: `{{
                            isReadOnly ||
                            $table.getRowByIndex($self.index).ceeaIfDirectory === 'Y' ||
                            $values.auditStatus === 'APPROVED'
                          }}`,
                          '@change': `{{() => setTotalAmount($form, $table.getRowByIndex($self.index))}}`
                        },
                        'x-render-table-column': {
                          customRender: true,
                          width: 150,
                          title: i18nExpression('purchaseDemand.priceIncludingTax')
                        },
                        ...feedbackLayoutIsPopover,
                        'x-validator': {
                          triggerType: 'onBlur',
                          required: `{{$form.values.demandType  === 'NONPRODUCTIVE_DEMAND'}}`,
                          messages: i18nExpression('purchaseDemand.selectVendor')
                        }
                      },
                      totalAmount: {
                        // 预估总金额
                        type: 'string',
                        'x-hidden': `{{$values.demandType !== 'NONPRODUCTIVE_DEMAND'}}`,
                        title: i18nExpression('purchaseDemand.totalAmount'),
                        'x-render-table-column': {
                          width: 150
                        }
                      },
                      categoryName: {
                        // 物料小类
                        type: 'string',
                        title: i18nExpression('purchaseDemand.materialCateSub'),
                        'x-component-props': {
                          disabled: true
                        },
                        'x-render-table-column': {
                          width: 150
                        }
                      },
                      ceeaIfDirectory: {
                        // 是否目录化
                        type: 'string',
                        'x-component': 'DictSelect',
                        'x-component-props': {
                          code: 'YES_OR_NO'
                        },
                        'x-render-table-column': {
                          width: 150,
                          title: i18nExpression('purchaseDemand.ceeaIfCatalogMaterial')
                        }
                      },
                      rejectReason: {
                        // 退回原因
                        type: 'string',
                        title: i18nExpression('purchaseDemand.returnReason'),
                        'x-render-table-column': {
                          width: 150
                        }
                      },
                      operation: {
                        type: 'void',
                        'x-visible': `{{!isReadOnly}}`,
                        title: i18nExpression('common.operation'),
                        'x-render-table-column': {
                          width: 80,
                          fixed: 'right'
                        },
                        'x-component': 'RenderTableButtonList',
                        'x-reactions': expression(`(field) => {
                          field.visible = !$form.readPretty
                        }`),
                        properties: {
                          delete: {
                            type: 'void',
                            title: i18nExpression('common.delete'),
                            'x-component-props': {
                              type: 'text',
                              disabled: '{{isReadOnly}}',
                              '@click': expression(`
                                ({ row, rowIndex }) => {
                                  $table.remove(rowIndex)
                                }
                              `)
                            }
                          }
                        }
                      }
                    })
                  }
                }
              },
              // 附件
              attachmentList: {
                type: 'void',
                'x-component': 'CollapseItem',
                'x-component-props': {
                  title: i18nExpression('purSettlementMod.addUploadFile')
                },
                properties: {
                  toolbar: {
                    type: 'void',
                    'x-hidden': `{{$values.auditStatus === 'APPROVED'}}`,
                    'x-component': 'ButtonList',
                    'x-component-props': {
                      class: 'list-form__toolbar'
                    },
                    'x-reactions': expression(`(field) => {
                      field.visible = !$form.readPretty
                    }`),
                    properties: {
                      add: {
                        type: 'void',
                        title: i18nExpression('common.add'),
                        'x-component-props': {
                          type: 'primary',
                          '@click': expression(`() => {
                            $form.query('.reqAttachList').take().componentProps.componentInstance.addRow('unshift')
                          }`)
                        }
                      }
                    }
                  },
                  reqAttachList: {
                    type: 'array',
                    'x-component': 'RenderTable',
                    'x-query-engine-skip': true,
                    'x-query-engine-relation': 'reqAttachList:*',
                    'x-component-props': {
                      preColumns: 'checkbox, seq',
                      editMode: true,
                      maxHeight: 400,
                      pagination: false,
                      sortable: false,
                      // 联表主键的 key
                      primaryKey: 'attachId',
                      // 启用级联删除的储值行为
                      cascadeDeletion: true
                    },
                    properties: generateXindexInOrder({
                      attachId: {
                        // 主键ID,附件ID
                        type: 'string',
                        'x-hidden': true
                      },
                      attachName: {
                        // 附件名称
                        type: 'void',
                        title: i18nExpression('purchaseApplication.attachName'), // 附件名称
                        'x-component': 'SrmCommonFile',
                        'x-read-pretty': true,
                        'x-reactions': expression(`() => {
                          $self.setComponentProps({
                            defaultFile: {
                              fileId: $table.getRowByIndex($self.index)?.fileuploadId,
                              fileName: $table.getRowByIndex($self.index)?.attachName
                            }
                          })
                        }`),
                        'x-component-props': {
                          'extra-data': {
                            uploadType: 'DEF',
                            sourceType: 'WEB_APP',
                            fileModular: 'sup',
                            fileFunction: 'purchaseApplicationEngine',
                            fileType: 'images'
                          },
                          readonly: false,
                          '@on-change': expression(`({file}) => {
                            const row = $table.getRowByIndex($self.index)
                            row.fileuploadId = file.fileId.toString()
                            row.attachName = file.fileName
                            row.createdFullName = file.createdFullName
                            row.createdBy = file.createdBy
                            row.creationDate = file.creationDate
                          }`)
                        },
                        'x-render-table-column': {
                        }
                      },
                      createdFullName: {
                        // 上传人
                        type: 'string',
                        'x-read-pretty': true,
                        title: i18nExpression('purchaseDemand.attachmentCreatedBy'),
                        'x-render-table-column': {
                        }
                      },
                      createdBy: {
                        // 账号
                        type: 'string',
                        'x-read-pretty': true,
                        title: i18nExpression('vendorMod.account'),
                        'x-render-table-column': {
                        }
                      },
                      creationDate: {
                        // 上传时间
                        type: 'date',
                        'x-read-pretty': true,
                        'x-query-engine-sort': 'desc',
                        title: i18nExpression('purchaseDemand.attachmentCreatedDate'),
                        'x-render-table-column': {
                          format: 'yyyy-MM-dd'
                        }
                      },
                      operation: {
                        type: 'void',
                        title: i18nExpression('common.operation'),
                        'x-render-table-column': {
                          width: 80,
                          fixed: 'right'
                        },
                        'x-component': 'RenderTableButtonList',
                        'x-reactions': expression(`(field) => {
                          field.visible = !$form.readPretty
                        }`),
                        properties: {
                          delete: {
                            type: 'void',
                            title: i18nExpression('common.delete'),
                            'x-component-props': {
                              type: 'text',
                              '@click': expression(`
                                ({ row, rowIndex }) => {
                                  $table.remove(rowIndex)
                                }
                              `)
                            }
                          }
                        }
                      }
                    })
                  }
                }
              }
            }
          }
        }
      },
      bomVersionDialog: {
        ...BomVersionDialog
      },
      bomDetailDialog: {
        ...BomDetailDialog
      }
    }
  }
})
</script>

<template>
  <RenderEngine schemaKey="purchaseApplicationDetail" :schema="schema" :scope="scope" :components="components" />
</template>

<style lang="scss" scoped>
:deep(.base-import+.el-button) {
  margin-left: 0
}
.input-number-precision {
  width: 100%;
  :deep(.el-input__inner) {
    text-align: left;
    padding-left: 8px;
  }
}
</style>
