<script setup lang="tsx">
import { RenderEngine } from 'lib@/components/render-engine'
import {
  dataTimeSelectorSegment,
  yearMonthDaySelectorSegment,
  exportExcelSegment
} from 'lib@/components/render-engine/schema-segments'
import {
  defineSchemas,
  expression,
  i18nExpression,
  generateXindexInOrder,
  queryFieldStatePropertyExpression
} from '@meicloud/render-engine'
// @ts-ignore
import { getValidateFailureSequence } from '@/utils'
// @ts-ignore
import AssignDialog, { assignFetch } from './components/dialog/assignDialog'
// @ts-ignore
import RejectDialog, { rejectFetch } from './components/dialog/rejectDialog'
// @ts-ignore
import CreateInquiryDialog, { createInquiryFetch } from './components/dialog/createInquiryDialog'
// @ts-ignore
import CreateOrderDialog, { createOrderFetch, setQuantity, setPortation, openLadderPriceDialog, supplierConfirmSlot } from './components/dialog/createOrderDialog.tsx'
// @ts-ignore
import FollowUpDialog from './components/dialog/followUpDialog'
// @ts-ignore
import LadderPriceDialog, { ladderPriceDescribe } from 'lib@/compositionEngine/demandPoolManagement/ladderPriceDialog'

import { usePageHelper } from 'lib@/components/composables/usePageHelper'

const { app, t: $t, http: $http } = usePageHelper()

// 获取表格勾选数据
const $getCheckboxRecords = ($form: any, $message: any) => {
  let rows = $form.query('PrRequirementPoolForBuyer.table').take()
    .componentProps
    .componentInstance
    .getCheckboxRecords()

  if (rows.length === 0) {
    $message.warning($t('common.msgSelectData')) // 请勾选数据!
    return false
  }
  return rows
}
// 分配/转办
const $assignOne = ($form: any, $queryEngine: any, $message: any) => {
  const rows = $getCheckboxRecords($form, $message)
  if (!rows) return

  const organizationIds = rows.map((item: any) => item.organizationId)
  const organizationIdsUnique = [...new Set(organizationIds)]
  if (organizationIdsUnique.length > 1) {
    // 请选择相同的库存组织的数据!
    return $message.warning($t('purchaseDemand.openAssignOneTips1'))
  }

  let data = $form.query('PrRequirementPoolForBuyer').get('data')
  data.requirementLineIds = rows.map((item: any) => item.requirementLineId)
  $queryEngine.request.baseRequest({
    'type': 'PrRequirementPoolForBuyer',
    'lang': 'zh-cn',
    'query': {
      '*': {}
    },
    'payload': [{
      'requirementLineIds': data.requirementLineIds,
      'dutys': ['Purchase_Strategy', 'Carry_Out']
    }],
    'action': 'findAvailableUsers'
  }).then((res: any) => {
    data.strategyList = res.originalData?.records[0]?.Purchase_Strategy || [] // 寻源策略下拉列表
    data.carryOutList = res.originalData?.records[0]?.Carry_Out || [] // 订单履行下拉数据
    $form.query('*.assignDialog').take().setComponentProps({ visible: true })
    setTimeout(() => {
      $form.query('*.assignDialog.form').take().reset()
    })
  })
}

// 退回
const $rejectOne = ($form: any, $message: any) => {
  const rows = $getCheckboxRecords($form, $message)
  if (!rows) return

  $form.query('PrRequirementPoolForBuyer').get('data').requirementLineIds = rows.map((item: any) => item.requirementLineId)
  $form.query('*.rejectDialog').take().setComponentProps({ visible: true })
  setTimeout(() => {
    $form.query('*.rejectDialog.form').take().reset()
  })
}

// 创建寻源单据
const $createInquiry = ($form: any, $message: any) => {
  const rows = $getCheckboxRecords($form, $message)
  if (!rows) return

  if (rows.find((item: any) => item.applyStatus !== 'ASSIGNED')) {
    // 请选择状态为[已分配]的数据!
    return $message.warning($t('purchaseDemand.applyStatusTips'))
  }

  let sequences = getValidateFailureSequence(rows, 'sequence', (row: any) => row.ifCreateBid === 'Y')
  if (sequences) {
    // 您选择的申请行x已创建寻源，不能重复创建，请检查
    return $message.warning(`${$t('demandPoolManagement.prompt1')}【${sequences}】${$t('demandPoolManagement.prompt2')}!；`)
  }

  $form.query('PrRequirementPoolForBuyer').get('data').selectedRows = rows
  $form.query('*.createInquiryDialog').take().setComponentProps({ visible: true })
  setTimeout(() => {
    $form.query('*.createInquiryDialog.form').take().reset()
  })
}

// 创建采购订单
const $createOrder = async ($form: any, $queryEngine: any, $message: any) => {
  const rows = $getCheckboxRecords($form, $message)
  if (!rows) return

  if (rows.find((item: any) => item.ceeaIfDirectory === 'N' && (item.haveSupplier === 'N' || item.haveEffectivePrice === 'N'))) {
    return $message.warning($t('purchaseDemand.selectData1')) // 请选择[是否货源供应商]和[是否有效价格]必须为是的数据!
  }

  $queryEngine.request.baseRequest({
    'type': 'PrRequirementPoolForBuyer',
    'lang': 'zh-cn',
    'query': {
      '*': {}
    },
    'payload': [{
      'requirementLineIds': rows.map((item: any) => item.requirementLineId)
    }],
    'action': 'generatePurchaseInfos'
  }).then((res: any) => {
    $form.query('*.createOrderDialog').take().setComponentProps({ visible: true })
    setTimeout(() => {
      $form.query('*.createOrderDialog.*.purOrderList').take((field: any) => {
        field.value = res.data.map((row: any, index: any) => {
          return {
            sortIndex: index,
            ...row,
            ceeaIfSupplierConfirm: 'Y'
          }
        })
      })
    })
  })
}

// 添加暂挂
const $doHold = ($form: any, $queryEngine: any, $message: any) => {
  const rows = $getCheckboxRecords($form, $message)
  if (!rows) return

  let sequences = getValidateFailureSequence(rows, 'sequence', (row: any) => row.ifHold === 'Y')
  if (sequences) {
    return $message.warning($t('demandPoolManagement.prompt3')) // 请选择“是否暂挂”为否的数据
  }

  $queryEngine.request.baseRequest({
    'type': 'PrRequirementPoolForBuyer',
    'lang': 'zh-cn',
    'query': {
      '*': {}
    },
    'payload': [{
      'requirementLineIds': rows.map((item: any) => item.requirementLineId)
    }],
    'action': 'holdPool'
  }).then((res: any) => {
    $message.success($t('common.success'))
    $queryEngine.state.paginationManagement.refresh()
  })
}

// 取消暂挂
const $cancelHold = ($form: any, $queryEngine: any, $message: any) => {
  const rows = $getCheckboxRecords($form, $message)
  if (!rows) return

  let sequences = getValidateFailureSequence(rows, 'sequence', (row: any) => row.ifHold === 'N')
  if (sequences) {
    return $message.warning($t('demandPoolManagement.prompt4')) // 请选择“是否暂挂”为是的数据
  }

  $queryEngine.request.baseRequest({
    'type': 'PrRequirementPoolForBuyer',
    'lang': 'zh-cn',
    'query': {
      '*': {}
    },
    'payload': [{
      'requirementLineIds': rows.map((item: any) => item.requirementLineId)
    }],
    'action': 'releasePool'
  }).then((res: any) => {
    $message.success($t('common.success'))
    $queryEngine.state.paginationManagement.refresh()
  })
}

// 跳转采购申请
const $readPurchaseApplication = (row: any) => {
  app.$router.push({
    name: 'purchaseApplication',
    params: {
      from: 'demandPoolManagement',
      funName: 'purchaseApplication',
      fdSubject: row
    }
  })
}

// 查看后续单据
const $viewFollowUp = ($form: any, row: any, $queryEngine: any) => {
  // $http({
  //   url: '/api-sup-ce/documents/subsequentDocuments/subsequentDocumentsList',
  //   method: 'POST',
  //   data: { requirementLineId: row.requirementLineId },
  //   loading: true
  // }).then((res: any) => {
  //   $form.query('*.followUpDialog').take().setComponentProps({ visible: true })
  //   setTimeout(() => {
  //     $form.query('*.followUpDialog.*.followOrderList').take((field: any) => {
  //       field.value = res.data
  //     })
  //   })
  // })
  $queryEngine.request.baseRequest({
    'type': 'PrRequirementPoolForBuyer',
    'lang': 'zh-cn',
    'query': {
      '*': {}
    },
    'payload': [{ requirementLineId: row.requirementLineId }],
    'action': 'querySubseqList'
  }).then((res: any) => {
    $form.query('*.followUpDialog').take().setComponentProps({ visible: true })
    setTimeout(() => {
      $form.query('*.followUpDialog.*.followOrderList').take((field: any) => {
        field.value = res.data
      })
    })
  })
}
const $holdButtonSlot = ($form:any, $t:any, $queryEngine:any, $message:any) => {
  return {
    functional: true,
    render: (h: any) => {
      return (
        <el-dropdown style="margin:0 11px">
          <el-button>
            {/* 是否暂挂 */}
            { $t('purchaseDemand.ifHold') }
            <i class="el-icon-arrow-down el-icon--right" />
          </el-button>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item>
              <div style="padding-bottom: 5px;">
                <el-button v-on:click={() => $doHold($form, $queryEngine, $message)}>
                  {/* 添加暂挂  */}
                  { $t('purchaseDemand.newHold') }
                </el-button>
              </div>
            </el-dropdown-item>
            <el-dropdown-item>
              <el-button v-on:click={() => $cancelHold($form, $queryEngine, $message)}>
                {/* 取消暂挂 */}
                { $t('purchaseDemand.cancelHold') }
              </el-button>
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      )
    }
  }
}

const $getLadderPriceDescribeRow = ($form:any) => {
  return $form.query('PrRequirementPoolForBuyer').get('data').ladderPriceDescribeRow
}

const scope = {
  $t,
  $http,
  app,
  $assignOne,
  $assignFetch: assignFetch,
  $rejectOne,
  $rejectFetch: rejectFetch,
  $createInquiry,
  $createInquiryFetch: createInquiryFetch,
  $createOrder,
  $createOrderFetch: createOrderFetch,
  $setQuantity: setQuantity,
  $setPortation: setPortation,
  $openLadderPriceDialog: openLadderPriceDialog,
  $supplierConfirmSlot: supplierConfirmSlot,
  $ladderPriceDescribe: ladderPriceDescribe,
  $doHold,
  $cancelHold,
  $viewFollowUp,
  $readPurchaseApplication,
  $holdButtonSlot,
  $getLadderPriceDescribeRow
}

const components = {
  AssignDialog,
  RejectDialog,
  CreateInquiryDialog,
  CreateOrderDialog,
  FollowUpDialog,
  LadderPriceDialog
}

const schema = defineSchemas({
  PrRequirementPoolForBuyer: {
    type: 'void',
    'x-data': {
      requirementLineIds: [],
      selectedRows: [],
      strategyList: [],
      carryOutList: [],
      sourceTypeList: [
        // 简易询价
        {
          label: $t('purchaseDemand.simpleInquiry'),
          value: 'inq',
          componentName: 'inquiryManagement'
        },
        // 项目式询价
        // {
        //   label: $t('purchaseDemand.inquiryByProjectListBuyer'),
        //   value: 'BARGAINING',
        //   componentName: 'bargainManagement'
        // },
        // 招标
        {
          label: $t('purchaseDemand.bidding'),
          value: 'bid',
          componentName: 'biddingManagementLTS'
        },
        // 竞价
        {
          label: $t('purchaseDemand.priceBidding'),
          value: 'comp',
          componentName: 'competitionManagement'
        },
        // 新版竞价
        {
          label: this.$t('cusEntry.supplement20250211.auctionPro'),  // '竞价pro'
          value: 'auct',
          componentName: 'competitionManageBuyer'
        }
      ],
      ladderPriceDescribeRow: {}
    },
    'x-decorator': 'el-container',
    'x-decorator-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    'x-component': 'QueryEngine',
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        paginationQuery: {
          action: 'listPrRequirementPool',
          transformRequest: expression(`(data, headers) => {
            const {orderQuantity, ifHold} = $form.values.query
            if(ifHold){
              data.payload.filter = {
                ...data.payload.filter,
                ifHold: ifHold === 'Y' ? {eq: 'Y'} : {NE: 'Y'}
              }
            }
            if(orderQuantity){
              data.payload.filter = {
                ...data.payload.filter,
                orderQuantity: orderQuantity === 'Y' ? {gt: 0} :  {le: 0}
              }
            }
              
            data.payload.page['sort'] = 'requirementHeadId desc'
            data.query = {
              ...data.query,
              requirementHeadId: data.query.requirementHeadId,
              reqSubDocs: data.query.reqSubDocs
            }
            return data
          }`),
          onSuccess: expression(`(res) => {
            res.data.forEach((item,index) =>{
              item.sequence = index + 1
            })
          }`)
        }
      }
    },
    properties: {
      query: {
        type: 'object',
        'x-query-engine-skip': true,
        'x-component': 'QueryFormByQueryEngine',
        'x-component-props': {
          immediateQueryForm: true
        },
        properties: generateXindexInOrder({
          auditStatus: {
            type: 'string',
            'x-hidden': true,
            default: 'APPROVED',
            'x-query-engine-relation': 'requirementHeadId', // 子表字段
            'x-query-engine-relation-strict': true
          },
          orgId: {
            type: 'string',
            title: i18nExpression('oneStopShopping.businessEntity'), // 业务实体
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              'parent-id': -1,
              'node-type': 'OU',
              'select-type': 'input',
              placeholder: i18nExpression('common.pleaseSelect'),
              multiple: true,
              '@select': expression(`(node) => {
                if($form.values.query.organizationId){
                  $form.values.query.organizationId = null
                }
              }`)
            },
            'x-query-engine-query-operator': 'in'
          },
          organizationId: {
            type: 'string',
            title: i18nExpression('purchaseDemand.invOrg'), // 库存组织
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              'node-type': 'INV',
              'select-type': 'input',
              placeholder: i18nExpression('common.pleaseSelect'),
              multiple: true,
              'parent-id': expression('$form.values.query.orgId?.length ? $form.values.query.orgId : -1')
            },
            'x-query-engine-query-operator': 'in'
          },
          ceeaPurchaseType: {
            type: 'string',
            title: i18nExpression('purchaseDemand.purchaseType'), // 采购类型
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'PURCHASE_TYPE'
            },
            'x-query-engine-relation': 'requirementHeadId', // 子表字段
            'x-query-engine-relation-strict': true
          },
          materialId: {
            type: 'string',
            'x-hidden': true
          },
          materialCode: {
            type: 'string',
            title: i18nExpression('purchaseDemand.itemCode'), // 物料编码
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'materialCode',
              propKey: 'materialCode',
              name: 'scc_base_material_item',
              '@close-quicksearch': expression(`(val) => {
                $form.values.query.materialId = val ? val.materialId : null
              }`)
            }
          },
          categoryId: {
            type: 'string',
            title: i18nExpression('purchaseDemand.materialCateSub'), // 物料小类
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'categoryName',
              propKey: 'categoryId',
              name: 'scc_base_purchase_category4'
            }
          },
          requirementHeadNum: {
            type: 'string',
            title: i18nExpression('purchaseDemand.requirementHeadNum'), // 申请编号
            'x-query-engine-query-operator': 'contains'
          },
          applyStatus: {
            type: 'string',
            title: i18nExpression('purchaseDemand.applyStatus'), // 单据状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'APPLICATION_STATUS'
            }
          },
          applyDate: {
            title: i18nExpression('purchaseDemand.applyDate'), // 申请日期
            'x-query-engine-query-operator': 'between',
            ...dataTimeSelectorSegment,
            'x-query-engine-relation': 'requirementHeadId', // 子表字段
            'x-query-engine-relation-strict': true
          },
          ceeaStrategyUserNickname: {
            type: 'string',
            title: i18nExpression('purchaseDemand.ceeaStrategyUser'), // 寻源策略
            'x-query-engine-query-operator': 'contains'
          },
          ceeaPerformUserNickname: {
            type: 'string',
            title: i18nExpression('purchaseDemand.performUserNickname'), // 订单履行
            'x-query-engine-query-operator': 'contains'
          },
          ifCreateFollowForm: {
            type: 'string',
            title: i18nExpression('purchaseDemand.ifCreateFollowForm'), // 是否创建后续单据
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            }
          },
          ceeaDepartmentName: {
            type: 'string',
            title: i18nExpression('purchaseDemand.ceeaDepartment'), // 申请部门
            'x-query-engine-query-operator': 'contains',
            'x-query-engine-relation': 'requirementHeadId', // 子表字段
            'x-query-engine-relation-strict': true
          },
          createdFullName: {
            type: 'string',
            title: i18nExpression('purchaseDemand.applicant'), // 申请人
            'x-query-engine-query-operator': 'contains'
          },
          orderQuantity: {
            type: 'string',
            default: 'Y',
            title: i18nExpression('purchaseDemand.ifHaveOrderQuantity'), // 是否有剩余下单数量
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            }
          },
          ceeaIfDirectory: {
            type: 'string',
            title: i18nExpression('purchaseDemand.ceeaIfCatalogMaterial'), // 是否目录化
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            }
          },
          subsequentDocumentsNumber: {
            type: 'string',
            title: i18nExpression('purchaseDemand.followFormCode'), // 后续单据编号
            'x-query-engine-query-operator': 'contains',
            'x-query-engine-relation': 'reqSubDocs', // 子表字段
            'x-query-engine-relation-strict': true
          },
          dmandLineRequest: {
            type: 'string',
            title: i18nExpression('purchaseDemand.dmandLineRequest'), // 需求部门
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'DMAND_LINE_REQUEST'
            }
          },
          ifCreateOrder: {
            type: 'string',
            title: i18nExpression('purchaseDemand.ifCreateOrder'), // 是否已创建订单
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            }
          },
          ifCreateBid: {
            type: 'string',
            title: i18nExpression('purchaseDemand.ifCreateBid'), // 是否已创建寻源
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            }
          },
          ifHold: {
            type: 'string',
            default: 'N',
            title: i18nExpression('purchaseDemand.ifHold'), // 是否暂挂
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            }
          },
          demandType: {
            type: 'string',
            title: i18nExpression('purchaseDemand.demandType'), // 需求类型
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'DEMAND_TYPE'
            },
            'x-query-engine-relation': 'requirementHeadId', // 子表字段
            'x-query-engine-relation-strict': true
          }
        })
      },
      toolbar: {
        type: 'void',
        'x-component': 'Space',
        'x-component-props': {
          style: 'margin-bottom: 16px'
        },
        properties: {
          assign: {
            type: 'void',
            title: i18nExpression('purchaseDemand.distributionOrTransfer'), // 分配/转办
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              '@click': expression('() => $assignOne($form,$queryEngine,$message)')
            }
          },
          reject: {
            type: 'void',
            title: i18nExpression('purchaseDemand.reject'), // 退回
            'x-component': 'RButton',
            'x-component-props': {
              type: 'default',
              '@click': expression('() => $rejectOne($form,$message)')
            }
          },
          createInquiry: {
            type: 'void',
            title: i18nExpression('purchaseDemand.createInquiry'), // 创建寻源单据
            'x-component': 'RButton',
            'x-component-props': {
              type: 'default',
              '@click': expression('() => $createInquiry($form,$message)')
            }
          },
          createOrder: {
            type: 'void',
            title: i18nExpression('purchaseDemand.createOrder'), // 创建采购订单
            'x-component': 'RButton',
            'x-component-props': {
              type: 'default',
              '@click': expression('() => $createOrder($form,$queryEngine,$message)')
            }
          },
          // 是否暂挂
          hold: {
            type: 'void',
            'x-content': '{{$holdButtonSlot($form,$t,$queryEngine, $message)}}'
          },
          // 自定义导出
          exportExcel: {
            type: 'void',
            'x-component': 'ExportExcel',
            'x-component-props': {
              ...exportExcelSegment,
              type: 'default',
              pageUrl: '/api-sup-ce/api-ql/PrRequirementPoolForBuyer/listPrRequirementPool',
              tableHeader: queryFieldStatePropertyExpression('PrRequirementPoolForBuyer.table', 'data.columns'),
              dictCodes: {
                ceeaPurchaseType: 'PURCHASE_TYPE',
                applyStatus: 'APPLICATION_STATUS',
                ceeaIfDirectory: 'YES_OR_NO',
                haveSupplier: 'YES_OR_NO',
                haveEffectivePrice: 'YES_OR_NO',
                ifCreateBid: 'YES_OR_NO',
                ifCreateOrder: 'YES_OR_NO',
                ifHold: 'YES_OR_NO',
                demandType: 'DEMAND_TYPE',
                dmandLineRequest: 'DMAND_LINE_REQUEST'
              }
            }
          }
        }
      },
      table: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          class: 'table-view-vxe-table',
          style: 'flex: 1',
          preColumns: 'checkbox, seq',
          openCustomTable: true
        },
        properties: generateXindexInOrder({
          requirementLineId: { // 主表主键
            type: 'string',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          requirementHeadId: { // 子表主键
            type: 'string',
            'x-hidden': true,
            'x-query-engine-relation': 'requirementHeadId' // 子表字段
          },
          orgId: {
            type: 'string',
            'x-hidden': true
          },
          vendorId: {
            type: 'string',
            'x-hidden': true
          },
          orgCode: {
            type: 'string',
            'x-hidden': true
          },
          orgName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.businessEntity'), // 业务实体
              minWidth: 120
            }
          },
          organizationId: {
            type: 'string',
            'x-hidden': true
          },
          organizationCode: {
            type: 'string',
            'x-hidden': true
          },
          organizationName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.invOrg'), // 库存组织
              minWidth: 120
            }
          },
          requirementHeadNum: {
            type: 'string',
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression('({ row }) => $readPurchaseApplication(row)')
            },
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.requirementHeadNum'), // 采购申请编号
              minWidth: 120,
              customRender: true
            }
          },
          applyStatus: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'APPLICATION_STATUS'
            },
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.applyStatus'), // 单据状态
              minWidth: 120
            }
          },
          materialId: {
            type: 'string',
            'x-hidden': true
          },
          materialCode: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.itemCode'), // 物料编码
              minWidth: 120
            }
          },
          materialName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.itemName'), // 物料名称
              minWidth: 120
            }
          },
          requirementQuantity: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.requirementQuantity'), // 需求数量
              minWidth: 120
            }
          },
          orderQuantity: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.orderQuantity'), // 剩余可下单数量
              minWidth: 120
            }
          },
          requirementDate: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.requirementDate, '{y}-{m}-{d}')
              }`)
            },
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.requirementDate'), // 需求日期
              minWidth: 120
            }
          },
          haveSupplier: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            },
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.ifHaveSupplier'), // 是否货源
              minWidth: 120
            }
          },
          haveEffectivePrice: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            },
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.ifHaveEffectivePrice'), // 有效价格
              minWidth: 120
            }
          },
          categoryId: {
            type: 'string',
            'x-hidden': true
          },
          categoryCode: {
            type: 'string',
            'x-hidden': true
          },
          categoryName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.materialCateSub'), // 物料小类
              minWidth: 120
            }
          },
          demandType: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'DEMAND_TYPE'
            },
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.demandType'), // 需求类型
              minWidth: 120
            },
            'x-query-engine-relation': 'requirementHeadId' // 子表字段
          },
          ceeaPurchaseType: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'PURCHASE_TYPE'
            },
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.purchaseType'), // 采购类型
              minWidth: 120
            },
            'x-query-engine-relation': 'requirementHeadId' // 子表字段
          },
          ceeaStrategyUserNickname: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.ceeaStrategyUser'), // 寻源策略
              minWidth: 120
            }
          },
          ceeaPerformUserNickname: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.performUserNickname'), // 订单履行
              minWidth: 120
            }
          },
          applyDate: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.applyDate, '{y}-{m}-{d}')
              }`)
            },
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.applyDate'), // 申请日期
              minWidth: 120
            },
            'x-query-engine-relation': 'requirementHeadId' // 子表字段
          },
          unit: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.unitCode'), // 单位
              minWidth: 120
            }
          },
          ceeaIfDirectory: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            },
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.ceeaIfCatalogMaterial'), // 是否目录化
              minWidth: 120
            }
          },
          ifCreateOrder: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            },
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.ifCreateOrder'), // 是否创建订单
              minWidth: 120
            }
          },
          ifCreateBid: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            },
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.ifCreateBid'), // 是否创建寻源
              minWidth: 120
            }
          },
          ifHold: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            },
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.ifHold'), // 是否暂挂
              minWidth: 120
            }
          },
          ceeaDepartmentName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.ceeaDepartment'), // 申请部门
              minWidth: 120
            },
            'x-query-engine-relation': 'requirementHeadId'
          },
          createdFullName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.applicant'), // 申请人
              minWidth: 120
            }
          },
          followUp: {
            type: 'void',
            'x-read-pretty': false,
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.followForm'), // 后续单据
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
                    title: i18nExpression('common.view'), // 查看
                    'x-component': 'TableButton',
                    'x-component-props': {
                      type: 'text',
                      '@click': expression('({row}) => $viewFollowUp($form,row,$queryEngine)')
                    }
                  }
                }
              }
            }
          },
          dmandLineRequest: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'DMAND_LINE_REQUEST'
            },
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.dmandLineRequest'), // 需求部门
              minWidth: 120
            }
          },
          comments: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.comments1'), // 备注
              minWidth: 120
            }
          },
          rejectReason: {
            type: 'string',
            'x-render-table-column': {
              title: '退回原因', // 退回原因
              minWidth: 120
            }
          }
        })
      },
      // 分配/转办弹框
      assignDialog: {
        ...AssignDialog
      },
      // 退回
      rejectDialog: {
        ...RejectDialog
      },
      // 创建寻源单据
      createInquiryDialog: {
        ...CreateInquiryDialog
      },
      // 创建采购订单
      createOrderDialog: {
        ...CreateOrderDialog
      },
      // 后续单据
      followUpDialog: {
        ...FollowUpDialog
      },
      // 阶梯价
      ladderPriceDialog: {
        ...LadderPriceDialog
      }
    }
  }
})
</script>

<template>
  <RenderEngine :schema="schema" :scope="scope" schemaKey="PrRequirementPoolForBuyer" :components="components" />
</template>
