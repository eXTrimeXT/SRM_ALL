<!-- eslint-disable quotes -->
<script setup lang='ts'>
import {
  defineSchemas,
  generateXindexInOrder,
  changeFieldVisibleByDeps,
  expression,
  generateCharFunctionExpression,
  generateCharReactionExpression,
  i18nExpression,
  queryFieldStatePropertyExpression,
  queryFieldValueExpression
} from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import {
  requiredValidatorSegment,
  dataTimeSelectorSegment,
  yearMonthDaySelectorSegment,
  buttonListItemVisibleByPermission,
  exportExcelSegment,
  editTableFormItemValid
} from 'lib@/components/render-engine/schema-segments'
// @ts-ignore
import { useDebounceFn } from '@vueuse/core'
import { bus } from 'lib@/components/render-engine/components/bus'
import sumDialog from './components/sumDialog'
import rejectDialog from './components/rejectDialog'
import batchDialog from './components/batchDialog'
import { shoppingCartApi } from 'modcb@/oneStopShopping/api'
import { getImgSrc } from 'lib@/utils/file'
import { getFileUrl } from '@/library/utils/file'
const { emitTabAdd, t: $t, app } = usePageHelper()

const $ceeaSetSummaryAndNoticeUser = ($form: any, $queryEngine: any) => {
  let selects = $form
    .query('table')
    .take()
    .componentProps.componentInstance.getCheckboxRecords()

  let currentRowsCheck = selects.find(v => v.status !== 'DRAFT')

  if (currentRowsCheck) {
    return app.$message.warning($t('oneStopShopping.createProjectMsg11'))
  }

  if (selects.length < 1) {
    return app.$message.warning($t('oneStopShopping.createProjectMsg12'))
  } else {
    $form.query('sumDialog').take().setComponentProps({ visible: true })
  }
}

const $batchMaintenance = ($form: any, $queryEngine: any) => {
  let selects = $form
    .query('table')
    .take()
    .componentProps.componentInstance.getCheckboxRecords()
  /* 获取已审批的数据 */
  let unDraftList = selects.find(v => v.status !== 'DRAFT')
  if (unDraftList) {
    return app.$message.warning($t('cusEntry.tipMessage.unDraftListMsg'))
  }
  if (selects.length < 1) {
    return app.$message.warning($t('oneStopShopping.createProjectMsg14'))
  } else {
    $form.query('batchDialog').take().setComponentProps({ visible: true })
  }
}

const $createProject = ($form:any, $queryEngine:any) => {
  let selects = $form
    .query('table')
    .take()
    .componentProps.componentInstance.getCheckboxRecords()
  /* 获取已审批的数据 */
  let unApprovalList = selects.find(v => v.status !== 'APPROVED')
  if (unApprovalList) {
    return app.$message.warning($t('cusEntry.tipMessage.unApprovalMsg'))
  }
  /* 校验当前选择数据汇总人是否和当前登录人匹配 */
  const userId = app.$store.getters.userInfo.userId
  const validSummaryUser = selects.findIndex(item => item.summaryUserId !== userId)
  if (validSummaryUser > -1) {
    app.$message.warning($t('cusEntry.tipMessage.loginerNoSummaryUser'))
    return false
  }
  const ids = selects.map(item => item.shopCartId)
  $queryEngine.request.baseRequest({
    loading: true,
    action: "createRequirements",
    type: 'ShopCart',
    payload: ids,
    query: {
      "*": {}
    }
  }).then((res) => {
    if (res.data) {
      if (res.data.length > 0) {
        app.$message.success($t('common.success'))
        bus.$emit('ShopCart')

        app.$confirm($t('oneStopShopping.createProjectMsg4'), '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          app.$router.push({
            name: 'purchaseApplication',
            params: { autoQuery: true }
          })
        }).catch(err => {
          console.log(err)
        })
      } else {
        app.$message.warning($t('oneStopShopping.createProjectMsg5'))
        bus.$emit('ShopCart')
      }
    }
  }).catch(() => {
  })
  // shoppingCartApi.ceeaValidRequiredInfo(ids).then(res => {
  //   if (res.data == $t('oneStopShopping.createProjectMsg1')) {
  //     app.$message.success($t('oneStopShopping.createProjectMsg1'))
  //   } else if (res.data == $t('oneStopShopping.createProjectMsg2')) {
  //     app.$message.success($t('oneStopShopping.createProjectMsg2'))
  //   } else if (res.data == $t('oneStopShopping.createProjectMsg3')) {
  //     app.$message.success($t('oneStopShopping.createProjectMsg3'))
  //   }
  // })
}

const $submitGet = ($form:any, $queryEngine:any) => {
  let ids = []
  let arrCurrentRows = []
  let selects = $form
    .query('table')
    .take()
    .componentProps.componentInstance.getCheckboxRecords()
  /* 校验需要勾选提交的数据 */
  if (selects.length === 0) {
    app.$message.warning($t('cusEntry.tipMessage.selectRowsMsg'))
    return false
  }
  /* 校验提交状态为未提交状态 */
  const index = selects.findIndex(item => !['DRAFT', 'WITHDRAW', 'REJECTED'].includes(item.status))
  if (index > -1) {
    app.$message.warning($t('cusEntry.tipMessage.selectRowStatusNotUnsubmit'))
    return false
  }
  /* 校验必填项 */
  let validTag = true
  let tipMessage = ''
  selects.some(item => {
    if (!item.deptLeaderUserId || !item.requirementDate || !item.requirementNum || !item.materialName || !item.extDepartmentcode || !item.extReferencePrice || !item.currencyCode || !item.unit || !item.extUseTo || !item.extUserPhone || !item.extAddressName || !item.extReceiver) {
      validTag = false
      tipMessage = $t('cusEntry.tipMessage.materialRowRequired', { materialCode: item.materialCode })
      return true
    } else if (Number(item.requirementNum) < Number(item.extOrderQuantityMinimum)) {
      /* 校验需求量大于等于最小起订量 */
      validTag = false
      tipMessage = $t('cusEntry.tipMessage.extOrderQuantityMinimumMsg', { materialCode: item.materialCode })
      return true
    }
  })
  if (!validTag) {
    app.$message.warning(tipMessage)
    return false
  }
  let shopCarts = [...selects]
  $queryEngine.request.baseRequest({
    loading: true,
    action: "submit",
    type: 'ShopCart',
    payload: shopCarts,
    query: {
      "*": {}
    }
  }).then((res) => {
    app.$message.success($t('common.success'))
    bus.$emit('ShopCart')
  }).catch(() => {
  })
}
/* 审批 */
const $approval = ($form:any, $queryEngine:any) => {
  /* 获取勾选的数据 */
  const selects = $form.query('table').take().componentProps.componentInstance.getCheckboxRecords()
  /* 校验需要勾选提交的数据 */
  if (selects.length === 0) {
    app.$message.warning($t('cusEntry.tipMessage.selectRowsMsg'))
    return false
  }
  /* 校验提交状态为已提交状态 */
  const index = selects.findIndex(item => item.status !== 'SUBMITTED')
  if (index > -1) {
    app.$message.warning($t('cusEntry.tipMessage.onlySelectApprovalRows'))
    return false
  }
  /* 校验当前选择审批数据的部门领导是否和当前登录人匹配 */
  const userId = app.$store.getters.userInfo.userId
  const validLeader = selects.findIndex(item => item.deptLeaderUserId !== userId)
  if (validLeader > -1) {
    app.$message.warning($t('cusEntry.tipMessage.loginerNoLeader'))
    return false
  }
  $queryEngine.request.baseRequest({
    type: 'ShopCart',
    action: 'approved',
    lang: 'zh-cn',
    payload: selects,
    query: {
      '*': {}
    }
  }).then(res => {
    app.$message.success($t('common.success'))
    bus.$emit('ShopCart')
  })
}
const $keep = ($form:any, $queryEngine:any) => {
  let selects = $form
    .query('table')
    .take()
    .componentProps.componentInstance.getCheckboxRecords()

  if (!selects.length) return app.$message.warning($t('cusEntry.tipMessage.selectRowsMsg'))
  /* 获取未提交的数据行 */
  const saveData = selects.filter(item => ['DRAFT', 'WITHDRAW', 'REJECTED'].includes(item.status))
  $queryEngine.request.baseRequest({
    loading: true,
    action: "add",
    type: 'ShopCart',
    payload: saveData,
    query: {
      "*": {}
    }
  }).then((res) => {
    app.$message.success($t('common.success'))
    bus.$emit('ShopCart')
  }).catch(() => {
  })
}
const $closeByIds = ($form:any, $queryEngine:any) => {
  let selects = $form
    .query('table')
    .take()
    .componentProps.componentInstance.getCheckboxRecords()
  /* 校验需要勾选提交的数据 */
  if (selects.length === 0) {
    app.$message.warning($t('cusEntry.tipMessage.selectRowsMsg'))
    return false
  }
  /* 获取已审批的数据 */
  let unApprovalList = selects.find(v => v.status !== 'APPROVED')
  if (unApprovalList) {
    return app.$message.warning($t('cusEntry.tipMessage.unApprovalMsg'))
  }
  /* 校验当前选择审批数据的汇总人是否和当前登录人匹配 */
  const userId = app.$store.getters.userInfo.userId
  const validSummaryUser = selects.findIndex(item => item.summaryUserId !== userId)
  if (validSummaryUser > -1) {
    app.$message.warning($t('cusEntry.tipMessage.loginerNoSummaryUser'))
    return false
  }
  app.$prompt('', $t('cusEntry.tipMessage.closeTip'), {
    confirmButtonText: $t('common.confirm'),
    cancelButtonText: $t('common.cancel'),
    inputType: 'textarea',
    inputValidator: (value) => {
      if (!value) {
        return $t('cusEntry.tipMessage.closeReason')
      }
      return true
    }
  }).then(({ value }) => {
    $queryEngine.request.baseRequest({
      loading: true,
      action: "close",
      type: 'ShopCart',
      payload: selects.map(item => ({ ...item, extCloseComment: value })),
      query: {
        "*": {}
      }
    }).then((res) => {
      app.$message.success($t('common.success'))
      bus.$emit('ShopCart')
    })
  })
}
const $deleteByIds = ($form:any, $queryEngine:any) => {
  let selects = $form
    .query('table')
    .take()
    .componentProps.componentInstance.getCheckboxRecords()

  let delIds = true
  selects.forEach(item => {
    if (!['DRAFT', 'WITHDRAW', 'REJECTED'].includes(item.status)) {
      delIds = false
    }
  })
  let ids = []
  selects.forEach(elm => {
    ids.push(elm.shopCartId)
  })
  if (!delIds || ids.length === 0) {
    return app.$message.warning($t('oneStopShopping.createProjectMsg11'))
  }
  /* 校验当前选择审批数据的创建人是否和当前登录人匹配 */
  const username = app.$store.getters.userInfo.username
  const validLeader = selects.findIndex(item => item.createdBy !== username)
  if (validLeader > -1) {
    app.$message.warning($t('cusEntry.tipMessage.loginerNoCreatedBy'))
    return false
  }
  $queryEngine.request.baseRequest({
    loading: true,
    action: "delete",
    type: 'ShopCart',
    payload: ids,
    query: {
      "*": {}
    }
  }).then((res) => {
    app.$message.success($t('common.success'))
    bus.$emit('ShopCart')
  }).catch(() => {
  })
}

const $submitOut = ($form:any, $queryEngine:any) => {
  let userId = app.$store.getters.userInfo.userId
  let selects = $form
    .query('table')
    .take()
    .componentProps.componentInstance.getCheckboxRecords()

  let currentRowsCheck = selects.find(v => v.status !== 'SUBMITTED')
  let userIdCheck = selects.find(v => v.summaryUserId !== userId)

  if (currentRowsCheck) {
    return app.$message.warning($t('oneStopShopping.submitOutMsg1'))
  }
  if (userIdCheck) {
    return app.$message.warning($t('oneStopShopping.submitOutMsg2'))
  }

  if (selects.length < 1) {
    return app.$message.warning($t('components.userSelection.selectData'))
  }

  $form.query('rejectDialog').take().setComponentProps({ visible: true })
}

const $rejectSubmit = ($form: any, $queryEngine: any, done:any) => {
  $form.query('rejectForm').take().submit(values => {
    let selects = $form
      .query('table')
      .take()
      .componentProps.componentInstance.getCheckboxRecords()

    let ids = []
    selects.forEach(item => {
      ids.push(item.shopCartId)
    })
    let submitData = {
      ids,
      returnReason: values.returnReason
    }

    $queryEngine.request.baseRequest({
      loading: true,
      action: "withdraw",
      type: 'ShopCart',
      payload: [submitData],
      query: {
        "*": {}
      }
    }).then((res) => {
      app.$message.success($t('common.success'))
      bus.$emit('ShopCart')

      done()
    }).catch(() => {
    })
  })
}

const $batchSubmit = ($form: any, $queryEngine: any, done:any) => {
  $form.query('batchUpdateForm').take().submit(values => {
    let selects = $form
      .query('table')
      .take()
      .componentProps.componentInstance.getCheckboxRecords()
    const submitData = selects.map(item => ({
      ...item,
      ...values
    }))
    $queryEngine.request.baseRequest({
      loading: true,
      action: "add",
      type: 'ShopCart',
      payload: submitData,
      query: {
        "*": {}
      }
    }).then((res) => {
      app.$message.success($t('common.success'))
      bus.$emit('ShopCart')
      done()
    }).catch(() => {
    })
  })
}

const $confirmSum = ($form:any, $queryEngine:any, done:any) => {
  $form.query('sumForm').take().submit(values => {
    console.log(values)
    let selects = $form
      .query('table')
      .take()
      .componentProps.componentInstance.getCheckboxRecords()
    const submitData = selects.map(item => ({
      ...item,
      ...values
    }))
    $queryEngine.request.baseRequest({
      loading: true,
      action: "add",
      type: 'ShopCart',
      payload: submitData,
      query: {
        "*": {}
      }
    }).then((res) => {
      app.$message.success($t('common.success'))
      bus.$emit('ShopCart')
      done()
    }).catch(() => {
    })
  })
}

const $detailOne = (type: string, row: any) => {

}

const $readOne = (row: any) => {
  $detailOne('view', row)
}

const $editOne = (row: any) => {
  $detailOne('edit', row)
}

const $delete = ($queryEngine: any, row: any, $message: any) => {
  $queryEngine.request['delete']([row.tagTemplateRelationId]).then((res: any) => {
    $message.success($t('common.successDelete'))
    $queryEngine.state.paginationManagement.refresh()
  })
}
// 列表模式图片展示插槽
const smallImgSlot = ($form: any, row: any) => {
  const data = $form.query('ShopCart').get('data')
  return {
    functional: true,
    render: (h: any) => {
      return h('div',
        {
          attrs: {
            class: 'table-img'
          }
        },
        [
          h('div',
            {
              attrs: {
                class: 'logo-small'
              },
              style: {
                padding: row.extShelvesAttachId ? 0 : '0 4px',
                // backgroundColor: row.extShelvesAttachId ? 'none' : '#75C8FF',
                display: data.listShowType === 'list' ? 'block' : 'none'
              }
            },
            [
              h('img',
                {
                  attrs: {
                    width: '100%',
                    src: row.imgUrl,
                    alt: row.materialName
                  }
                }
              )
            ]
          )
        ]
      )
    }
  }
}
// 获取图片
const formatImgUrl = (id: string, $form: any) => {
  if (id) {
    return getImgSrc(id)
  } else {
    return getFileUrl($form.query('ShopCart').get('data').defaultLogo)
  }
}
// 数据处理
const adaptData = (data: any, $form: any) => {
  return data.map((item: any) => {
    let imgUrl = formatImgUrl(item.extShelvesAttachId, $form) // 获取图片信息链接
    return {
      ...item,
      imgUrl: imgUrl // 图片链接
    }
  })
}
const schema = defineSchemas({
  state: {
    type: 'void',
    'x-component': 'Fragment',
    'x-hidden': true,
    'x-data': {}
  },
  ShopCart: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component': 'el-container',
    'x-component-props': {
      class: 'flex-container shop-cart',
      direction: 'vertical'
    },
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        paginationQuery: {
          // action: 'query',
          immediate: true,
          ready: expression(`async () => {
            /* 获取业务实体和默认部门 */
            const username = app.$store.getters.userInfo.username
            const respone = await shoppingCartApi.getUserOrgAndDept(username)
            if (respone) {
              const {
                ouOrganization,
                departmentOrganization
              } = respone.data
              $form.query('ShopCart').get('data').defaultOrgInfo = ouOrganization
              $form.query('ShopCart').get('data').defaultDeptInfo = departmentOrganization
              /* 获取业务实体下的部门 */
              const params = {
                parentId: ouOrganization?.organizationId
              }
              shoppingCartApi.getDept(params).then(res => {
                if (res.data) {
                  $form.query('ShopCart').get('data').deptList = res.data.map(item => ({
                    label: item.organizationName,
                    value: item.organizationCode,
                    id: item.organizationId
                  })) || []
                }
              })
              /* 获取默认收货地址 */
              if (departmentOrganization?.organizationId) {
                shoppingCartApi.getDeptAddress(departmentOrganization?.organizationId).then(res => {
                  if (res.data) {
                    /* 获取默认收货地址&收货人*/
                    $form.query('ShopCart').get('data').defaultAddress= res.data.find(item => item.isDefault === 'Y') || {}
                  }
                })
              }
            }
            return true
          }`),
          transformRequest: expression(`(data, headers) => {
            const {
              filter
            } = data.payload
            if (!filter) {
              data.payload.filter = {
                status: {
                  ne: 'CLOSE'
                }
              }
            } else if (!filter.status) {
              data.payload.filter.status = {
                ne: 'CLOSE'
              }
            }
            data.query['*'] = {}
            return data
          }`),
          onSuccess: expression(`(res) => {
            let list = adaptData(res.data, $form)
            setTimeout(() => {
              $form.values.table = list
            })
          }`)
        }
      }
    },
    'x-data': {
      listShowType: 'list',
      smallLogo: 'images/gwn.png',
      bigLogo: 'images/gwn.png',
      defaultLogo: 'images/gwn.png',
      defaultOrgInfo: {},
      defaultDeptInfo: {},
      defaultAddress: {},
      deptList: [],
      addressList: []
    },
    properties: {
      sumDialog: {
        ...sumDialog
      },
      rejectDialog: {
        ...rejectDialog
      },
      batchDialog: {
        ...batchDialog
      },
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'ShopCart',
          '@listener': expression(`() => {
            $queryEngine.state.paginationManagement.refresh()
          }`)
        }
      },
      query: {
        type: 'object',
        'x-query-engine-skip': true,
        'x-component': 'QueryFormByQueryEngine',
        properties: generateXindexInOrder({
          materialId: {
            type: 'string',
            title: "{{$t('common.materialCode')}}",
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              name: 'scc_base_material_item_contract',
              showKey: 'materialCode',
              propKey: 'materialId'
            }
          },
          status: {
            type: 'string',
            title: "{{$t('common.status')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SHOP_CART_STATUS'
            },
            'x-reactions': {
              effects: ['onFieldInit'],
              fulfill: {
                state: {
                  value: expression('app.$route?.params?.from === \'fromFun\' ? app.$route?.params?.row.configCode === \'NPM_SHOP_CART_WAIT_APPROVE\' ? \'SUBMITTED\' : \'APPROVED\' : \'\'')
                },
                run: '$initDept($form)'
              }
            }
          },
          orgId: {
            type: 'string',
            title: "{{$t('quota.org')}}",
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              'parent-id': -1,
              'node-type': 'OU',
              'select-type': 'input',
              placeholder: "{{$t('common.pleaseSelect')}}",
              multiple: true,
              '@select': expression(`(node) => {
                  if (!$form.values.query.organizationIds) return
                  $form.values.query.organizationIds = null
              }`)
            },
            'x-query-engine-query-operator': 'in'
          },
          categoryName: {
            type: 'string',
            title: "{{$t('common.category')}}",
            'x-component': 'CCategorySelect',
            'x-component-props': {
              showKey: 'categoryName'
            }
          },
          summaryNickname: {
            type: 'string',
            title: "{{$t('oneStopShopping.summaryUser')}}",
            'x-query-engine-query-operator': 'contains'
          },
          // noticeNickname: {
          //   type: 'string',
          //   title: "{{$t('oneStopShopping.noticeUser')}}",
          //   'x-query-engine-query-operator': 'contains'
          // },
          createdFullName: {
            type: 'string',
            title: "{{$t('common.creator')}}",
            'x-query-engine-query-operator': 'contains'
          },
          creationDate: {
            title: "{{$t('common.creationTime')}}",
            ...dataTimeSelectorSegment,
            'x-query-engine-query-operator': 'between'
          },
          extDepartment: {
            type: 'string',
            title: i18nExpression('cusEntry.sup.extDepartment'),
            'x-query-engine-query-operator': 'contains'
          }
        })
      },
      toolbar: {
        type: 'void',
        'x-component': 'ButtonList',
        'x-component-props': {
          class: 'list-form__toolbar'
        },
        properties: {
          add: {
            type: 'void',
            title: i18nExpression('common.add'),
            'x-component-props': {
              type: 'primary',
              '@click': expression(`() => {
                /* 获取业务实体和部门 */
                const state = $form.query('ShopCart').get('data')
                const {
                  organizationName,
                  organizationCode,
                  organizationId
                } = state.defaultOrgInfo || {}
                const {
                  organizationName: extDepartment,
                  organizationId: extCeeaDeptid,
                  organizationCode: extDepartmentcode
                } = state.defaultDeptInfo || {}
                const {
                  siteId,
                  siteName,
                  siteDesc,
                  receiver,
                  addressRegion,
                  receiverPhone
                } = state.defaultAddress || {}
                $self.query('.table').take(field => {
                  field.componentProps.componentInstance.addRow('unshift', {
                    status: 'DRAFT',
                    extIsGoods: 'N',
                    summaryNickname: app.$store.getters.userInfo.nickname,
                    summaryUserId: app.$store.getters.userInfo.userId,
                    extDepartment,
                    extCeeaDeptid,
                    extDepartmentcode,
                    imgUrl: formatImgUrl(null, $form),
                    orgName: organizationName,
                    orgCode: organizationCode,
                    orgId: organizationId,
                    extAddressId: siteId,
                    extAddressName: siteName,
                    extAreaCode: addressRegion,
                    extAddress: siteDesc,
                    extReceiver: receiver,
                    extReceiverContact:receiverPhone,
                    currencyCode: 'RMB',
                    extAttachId:null,
                    extAttachName:null
                  })
                })
              }`),
              ...buttonListItemVisibleByPermission('pr:shoppingCart:add')
            }
          },
          importExcel: {
            type: 'void',
            'x-component': 'ImportExcel',
            'x-component-props': {
              title: i18nExpression('cusEntry.inq.importShopCart'),
              type: 'primary',
              extraData: {
                fileModular: 'sup',
                fileFunction: 'shopCartImportExcel',
                fileType: 'excel'
              },
              upLoadUrl: '/api-sup-ce/shopCart/ext/importShopCartExcel',
              downloadTemplateOptions: {
                downloadUrl: '/api-sup-ce/shopCart/ext/importShopCartModelDownload',
                fileName: expression('$t(\'cusEntry.inq.shopCartImportTemplateXLXS\')')
              },
              class: 'export-excel',
              '@handleSuccess': expression(`() => {
                $queryEngine.state.paginationManagement.refresh()
              }`)
            }
          },
          delete: {
            type: 'void',
            title: "{{$t('common.delete')}}",
            'x-component-props': {
              type: 'primary',
              '@click': expression(`() => {
                $deleteByIds($form, $queryEngine)
              }`),
              ...buttonListItemVisibleByPermission('pr:shoppingCart:ceeaDeleteByIds')
            }
          },
          ceeaSetSummaryAndNoticeUser: {
            type: 'void',
            title: "{{$t('oneStopShopping.setSummaryAndNoticeUser')}}",
            'x-component-props': {
              type: 'primary',
              '@click': expression(`() => {
                $ceeaSetSummaryAndNoticeUser($form, $queryEngine)
              }`),
              ...buttonListItemVisibleByPermission('pr:shoppingCart:ceeaSetSummaryAndNoticeUser')
            }
          },
          batchMaintenance: {
            type: 'void',
            title: "{{$t('vendorMod.batchMaintain')}}",
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              '@click': expression(`() => {
                $batchMaintenance($form, $queryEngine)
              }`),
              ...buttonListItemVisibleByPermission('pr:shoppingCart:Batch_maintenance')
            }
          },
          keep: {
            type: 'void',
            title: "{{$t('common.save')}}",
            'x-component-props': {
              type: 'primary',
              '@click': expression(`() => {
                $keep($form, $queryEngine)
              }`),
              ...buttonListItemVisibleByPermission('pr:shoppingCart:save')
            }
          },
          submitSummaryUser: {
            type: 'void',
            title: i18nExpression('common.submit'),
            'x-component-props': {
              type: 'primary',
              ...buttonListItemVisibleByPermission('pr:shoppingCart:submitSummaryUser'),
              '@click': expression(`() => {
                $submitGet($form, $queryEngine)
              }`)
            }
          },
          recall: {
            type: 'void',
            title: i18nExpression('common.recall'),
            'x-component-props': {
              type: 'primary',
              '@click': expression(`() => {
                $recall($form, $queryEngine)
              }`),
              ...buttonListItemVisibleByPermission('pr:shoppingCart:recall')
            }
          },
          exportExcel: {
            type: 'void',
            'x-component': 'ExportExcel',
            'x-component-props': {
              type: 'default',
              pageUrl: "/api-sup-ce/api-ql/ShopCart/query",
              ...exportExcelSegment,
              meiqlKey: "ShopCart", // meiQl 表格key
              filterParams: queryFieldValueExpression('query'),
              tableHeader: queryFieldStatePropertyExpression('ShopCart.table', 'data.columns'),
              dictCodes: {
                status: 'SHOP_CART_STATUS',
                purchaseType: 'PURCHASE_TYPE'
              },
              class: 'export-excel'
            }
          },
          createProject: {
            type: 'void',
            title: "{{$t('oneStopShopping.createProject')}}",
            'x-component-props': {
              type: 'default',
              '@click': expression(`() => {
                $createProject($form, $queryEngine)
              }`),
              ...buttonListItemVisibleByPermission('pr:shoppingCart:createProjectDetail')
            }
          },
          close: {
            type: 'void',
            title: "{{$t('common.close')}}",
            'x-component-props': {
              type: 'default',
              '@click': expression(`() => {
                $closeByIds($form, $queryEngine)
              }`),
              ...buttonListItemVisibleByPermission('pr:shoppingCart:close')
            }
          },
          approval: {
            type: 'void',
            title: i18nExpression('cusEntry.common.flowApproval'),
            'x-component-props': {
              type: 'primary',
              ...buttonListItemVisibleByPermission('pr:shoppingCart:approval'),
              '@click': expression(`() => {
                $approval($form, $queryEngine)
              }`)
            }
          },
          refuse: {
            type: 'void',
            title: i18nExpression('common.toRefuse'),
            'x-component-props': {
              type: 'primary',
              '@click': expression(`() => {
                $refuse($form, $queryEngine)
              }`),
              ...buttonListItemVisibleByPermission('pr:shoppingCart:refuse')
            }
          }
        }
      },
      table: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          class: 'table-view-vxe-table',
          preColumns: 'seq, checkbox',
          openCustomTable: true,
          editMode: true,
          'scroll-y': {
            gt: 10,
            oSize: 10
          },
          '@current-change': expression(`({ row, rowIndex }) => {
            if (!row.canEdit) {
              row.canEdit = true
              let otherList = $form.query('table').take().value.filter((item, index) => index !== rowIndex)
              otherList.forEach(item => {
                item.canEdit = false
              })
            }
          }`)
        },
        properties: generateXindexInOrder({
          shopCartId: {
            type: 'number',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          status: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SHOP_CART_STATUS'
            },
            'x-read-pretty': true,
            'x-render-table-column': {
              title: "{{$t('common.status')}}",
              minWidth: 100
            }
          },
          // 图片
          imgSmall: {
            type: 'object',
            'x-query-engine-skip': true,
            'x-content': '{{smallImgSlot($form, $table.getRowByIndex($self.index))}}',
            'x-read-pretty': false,
            'x-render-table-column': {
              minWidth: 90,
              sortable: false,
              'show-overflow': false,
              title: i18nExpression('cusEntry.sup.img')
            }
          },
          requirementHeadNum: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('purchaseDemand.requirementHeadNum')}}",
              minWidth: 120,
              static: true
            },
            'x-read-pretty': true
          },
          summaryUserId: {
            type: 'number',
            'x-hidden': true
          },
          summaryNickname: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('oneStopShopping.summaryUser')}}",
              minWidth: 120
            },
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'nickname',
              name: 'scc_rbac_user_display',
              '@close-quicksearch': expression(`val => {
                const {
                  department,
                  nickname,
                  userId,
                  username
                } = val || {}
                let row = $table.getRowByIndex($self.index)
                row.summaryNickname = nickname || ''
                row.summaryUserId = userId || ''
              }`)
            },
            'x-reactions': expression(`() => {
              const row = $table.getRowByIndex($self.index)
              const {
                status,
                extIsGoods,
                canEdit
              } = row || {}
              $self.readPretty = !(['WITHDRAW', 'DRAFT', 'REJECTED'].includes(status) && canEdit)
            }`),
            ...editTableFormItemValid
          },
          deptLeaderUserId: {
            type: 'number',
            'x-hidden': true
          },
          deptLeaderUserNickname: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('cusEntry.inq.departmentLeader')}}",
              minWidth: 120
            },
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'nickname',
              name: 'scc_rbac_user_display',
              '@close-quicksearch': expression(`val => {
                const {
                  userId,
                  nickname
                } = val || {}
                let row = $table.getRowByIndex($self.index)
                row.deptLeaderUserId = userId || ''
                row.deptLeaderUserNickname = nickname || ''
              }`)
            },
            'x-reactions': expression(`() => {
              const row = $table.getRowByIndex($self.index)
              const {
                status,
                extIsGoods,
                canEdit
              } = row || {}
              $self.readPretty = !(['WITHDRAW', 'DRAFT', 'REJECTED'].includes(status) && canEdit)
            }`),
            ...editTableFormItemValid
          },
          requirementDate: {
            'x-render-table-column': {
              title: "{{$t('purchaseDemand.requirementDate')}}",
              minWidth: 150
            },
            'x-reactions': expression(`() => {
              const row = $table.getRowByIndex($self.index)
              const {
                status,
                extIsGoods,
                canEdit
              } = row || {}
              $self.readPretty = !(['WITHDRAW', 'DRAFT', 'REJECTED'].includes(status) && canEdit)
            }`),
            type: 'date',
            default: null,
            'x-component-props': {
              placeholder: i18nExpression('common.pleaseSelectDate'),
              format: 'yyyy-MM-dd',
              'value-format': 'yyyy-MM-dd',
              style: {
                width: '120px'
              },
              'picker-options': expression('$cannotLessCurrentTimeOptions')
            },
            ...editTableFormItemValid
          },
          materialCode: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('common.materialCode')}}",
              minWidth: 120
            },
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'materialCode',
              name: 'scc_base_material_item_contract',
              '@close-quicksearch': expression(`val => {
                const {
                  materialName,
                  materialCode,
                  materialId,
                  materialType,
                  categoryName,
                  categoryCode,
                  categoryId,
                  unit,
                  unitName
                } = val || {}
                let row = $table.getRowByIndex($self.index)
                row.materialName = materialName || ''
                row.materialCode = materialCode || ''
                row.materialId = materialId || null
                row.categoryName = categoryName || ''
                row.categoryId = categoryId || null
                row.categoryCode = categoryCode || ''
                row.specification = materialType || ''
                row.unitName = unitName || ''
                row.unit = unit || ''
                if (val) {
                  app.$http({
                    url: 'api-sup-ce/shopCart/ext/extAdd',
                    method: 'POST',
                    data: {
                      materialCode,
                      orgId: row.orgId,
                      extAreaCode: row.extAreaCode
                    }
                  }).then(res => {
                    if (res.data) {
                      const {
                        extReferencePrice,
                        orderQuantityMinimum
                      } = res.data
                      row.extReferencePrice = extReferencePrice
                      row.orderQuantityMinimum = orderQuantityMinimum
                      row.extIsGoods = 'Y'
                    } else {
                      row.extIsGoods = 'N'
                    }
                  })
                }
              }`)
            },
            'x-reactions': expression(`() => {
              const row = $table.getRowByIndex($self.index)
              const {
                status,
                extIsGoods,
                canEdit
              } = row || {}
              $self.readPretty = !(['WITHDRAW', 'DRAFT', 'REJECTED'].includes(status) && canEdit) || extIsGoods === 'Y'
            }`),
            ...editTableFormItemValid
          },
          materialName: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('materialMainData.materialDesc')}}",
              minWidth: 120,
              static: true
            },
            'x-read-pretty': true,
            ...editTableFormItemValid
          },
          extOrderQuantityMinimum: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('cusEntry.sup.extOrderQuantityMinimum'),
              minWidth: 120,
              static: true
            },
            'x-read-pretty': true
          },
          extSecondCategoryName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('cusEntry.sup.extSecondCategoryName'),
              minWidth: 120,
              static: true
            },
            'x-read-pretty': true
          },
          categoryName: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('cusEntry.sup.lastLevelCategory')}}",
              minWidth: 120
            },
            'x-read-pretty': true,
            ...editTableFormItemValid
          },
          specification: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('common.specification')}}",
              minWidth: 120,
              static: true
            },
            'x-read-pretty': true
          },
          unit: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('dataConfMod.unit')}}",
              minWidth: 120
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'unit'
            },
            'x-read-pretty': true,
            ...editTableFormItemValid
          },
          requirementNum: {
            type: 'number',
            'x-render-table-column': {
              title: "{{$t('purchaseDemand.requirementQuantity')}}",
              minWidth: 120
            },
            'x-component-props': {
              min: 0,
              '@change': expression(`(value) => {
                  setTimeout(() => {
                    const ifPrecision = value?.toString().includes('.')
                    if (ifPrecision) {
                      /* 获取小数点 */
                      const [integer, precision] = value?.toString().split('.')
                      $self.value = Number(integer + '.' +  precision.toString().slice(0, 4))
                    }
                  })
              }`)
            },
            'x-reactions': expression(`() => {
              const row = $table.getRowByIndex($self.index)
              const {
                status,
                extIsGoods,
                canEdit
              } = row || {}
              $self.readPretty = !(['WITHDRAW', 'DRAFT', 'REJECTED'].includes(status) && canEdit)
            }`),
            ...editTableFormItemValid
          },
          orgName: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('purchaseDemand.businessEntity')}}",
              minWidth: 120,
              static: true
            },
            'x-read-pretty': true
          },
          extUserPhone: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('cusEntry.sup.extUserPhone'),
              minWidth: 140
            },
            'x-reactions': expression(`() => {
              const row = $table.getRowByIndex($self.index)
              const {
                status,
                extIsGoods,
                canEdit
              } = row || {}
              $self.readPretty = !(['WITHDRAW', 'DRAFT', 'REJECTED'].includes(status) && canEdit)
            }`),
            ...editTableFormItemValid
          },
          extDepartment: {
            type: 'string',
            'x-hidden': true
          },
          extDepartmentcode: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('cusEntry.sup.extDepartment'),
              minWidth: 120
            },
            'x-component': 'Select',
            'x-component-props': {
              '@change': expression(`field => {
                let state = $form.query('ShopCart').get('data')
                let row = $table.getRowByIndex($self.index)
                let deptItem = state.deptList.find(item=> item.value == field)
                row.extCeeaDeptid = deptItem?.id
                row.extDepartment = deptItem?.label
                if (field) {
                  if (row.extIsGoods === 'N') {
                    shoppingCartApi.getDeptAddress(deptItem.id).then(res => {
                      if (res.data) {
                        state.addressList = res.data.map(item =>({
                          receiver: item.receiver,
                          phone: item.receiverPhone,
                          id: item.siteId,
                          label: item.siteName,
                          code: item.siteDesc,
                          value: item.siteId,
                          areaCode: item.addressRegion
                        }))
                        /* 获取默认收货地址&收货人*/
                        const defaultItem = res.data.find(item => item.isDefault === 'Y')
                        if (defaultItem) {
                          row.extAddressName = defaultItem.siteName
                          row.extAddressId = defaultItem.siteId
                          row.extAddress = defaultItem.siteDesc
                          row.extReceiver = defaultItem.receiver
                          row.extReceiverContact = defaultItem.phone
                          row.extAreaCode = defaultItem.addressRegion
                        }
                      }
                    })
                  }
                } else {
                  row.extReceiver = ''
                  row.extAddressName = ''
                  row.extAddressId = ''
                  row.extAddress = ''
                  row.extAreaCode = ''
                  row.extReceiverContact = ''
                  state.addressList = []
                }
              }`)
            },
            'x-reactions': expression(`() => {
              const row = $table.getRowByIndex($self.index)
              const {
                status,
                extIsGoods,
                canEdit
              } = row || {}
              $self.readPretty = !(['WITHDRAW', 'DRAFT', 'REJECTED'].includes(status) && canEdit)
            }`),
            enum: expression('$form.query(\'ShopCart\').get(\'data\').deptList'),
            ...editTableFormItemValid
          },
          extAddressName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('cusEntry.sup.extAddress'),
              minWidth: 120
            },
            'x-reactions': expression(`() => {
              const row = $table.getRowByIndex($self.index)
              const {
                status,
                extIsGoods,
                canEdit
              } = row || {}
              $self.readPretty = !(['WITHDRAW', 'DRAFT', 'REJECTED'].includes(status) && canEdit)
            }`),
            'x-component': 'Select',
            'x-component-props': {
              '@visible-change': expression(`value => {
                let state = $form.query('ShopCart').get('data')
                let row = $table.getRowByIndex($self.index)
                if (value && row.extDepartmentcode) {
                  let deptItem = state.deptList.find(item=> item.value == row.extDepartmentcode)
                  if (deptItem) {
                    shoppingCartApi.getDeptAddress(deptItem.id).then(res => {
                      if (res.data) {
                        state.addressList = res.data.map(item =>({
                          receiver: item.receiver,
                          phone: item.receiverPhone,
                          id: item.siteId,
                          label: item.siteName,
                          code: item.siteDesc,
                          value: item.siteId,
                          areaCode: item.addressRegion
                        }))
                      }
                    })
                  } else {
                    row.extReceiver = ''
                    row.extAddressName = ''
                    row.extAddressId = ''
                    row.extAreaCode = ''
                    row.extAddress = ''
                    row.extReceiverContact = ''
                  }
                }
              }`),
              '@change': expression(`field => {
                let row = $table.getRowByIndex($self.index)
                let state = $form.query('ShopCart').get('data')
                const addressItem = state.addressList.find(item => item.id === field)
                row.extAddressName = addressItem.label
                row.extAddressId = addressItem.id
                row.extAddressCode = addressItem.code
                row.extReceiver = addressItem.receiver
                row.extAreaCode = addressItem.areaCode
                row.extReceiverContact = addressItem.phone
              }`)
            },
            enum: expression('$form.query(\'ShopCart\').get(\'data\').addressList'),
            ...editTableFormItemValid
          },
          extReceiver: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('cusEntry.sup.extReceiver'),
              minWidth: 120,
              static: true
            },
            'x-read-pretty': true,
            ...editTableFormItemValid
          },
          extAreaCode: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('cusEntry.sup.area'),
              minWidth: 120
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'REGION'
            },
            'x-read-pretty': true
          },
          extReferencePrice: {
            type: 'number',
            'x-render-table-column': {
              title: i18nExpression('cusEntry.sup.extReferencePrice'),
              minWidth: 120
            },
            'x-component-props': {
              min: 0
            },
            'x-reactions': expression(`() => {
              const row = $table.getRowByIndex($self.index)
              const {
                status,
                extIsGoods,
                canEdit
              } = row || {}
              $self.readPretty = !(['WITHDRAW', 'DRAFT', 'REJECTED'].includes(status) && canEdit) || extIsGoods === 'Y'
            }`),
            ...editTableFormItemValid
          },
          currencyCode: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('purchaseDemand.currency')}}",
              minWidth: 120
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'currency'
            },
            'x-reactions': expression(`() => {
              const row = $table.getRowByIndex($self.index)
              const {
                status,
                extIsGoods,
                canEdit
              } = row || {}
              $self.readPretty = !(['WITHDRAW', 'DRAFT', 'REJECTED'].includes(status) && canEdit) || extIsGoods === 'Y'
            }`),
            ...editTableFormItemValid
          },
          extIsGoods: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            },
            'x-render-table-column': {
              title: i18nExpression('cusEntry.sup.extIsGoods'),
              minWidth: 120
            },
            'x-read-pretty': true
          },
          extRejectReason: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('cusEntry.sup.extRejectReason'),
              minWidth: 120,
              static: true
            },
            'x-read-pretty': true
          },
          extUseTo: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('cusEntry.sup.extUseTo')}}",
              width: 160
            },
            'x-reactions': expression(`() => {
              const row = $table.getRowByIndex($self.index)
              const {
                status,
                extIsGoods,
                canEdit
              } = row || {}
              $self.readPretty = !(['WITHDRAW', 'DRAFT', 'REJECTED'].includes(status) && canEdit)
            }`),
            ...requiredValidatorSegment
          },
          brand: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('cusEntry.vendorMod.brand')}}",
              width: 160
            },
            'x-reactions': expression(`() => {
              const row = $table.getRowByIndex($self.index)
              const {
                status,
                extIsGoods,
                canEdit
              } = row || {}
              $self.readPretty = !(['WITHDRAW', 'DRAFT', 'REJECTED'].includes(status) && canEdit)
            }`)
          },
          extBuyTypeComment: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('bidMod.remark')}}",
              width: 160
            },
            'x-reactions': expression(`() => {
              const row = $table.getRowByIndex($self.index)
              const {
                status,
                extIsGoods,
                canEdit
              } = row || {}
              $self.readPretty = !(['WITHDRAW', 'DRAFT', 'REJECTED'].includes(status) && canEdit)
            }`)
          },
          extAttachName: {
            type: 'string',
            title: i18nExpression('dataConfMod.attachment'),
            'x-component': 'SrmCommonFile',
            'x-component-props': {
              defaultFile: {
                fileId: '{{$table.getRowByIndex($self.index).extAttachId}}',
                fileName: '{{$self.value}}'
              },
              '@on-change': expression(`({file}) => {
                let row = $table.getRowByIndex($self.index)
                const { fileId = null, fileName = null } = file || {}
                row.extAttachId = fileId
                row.extAttachName = fileName
                $self.value = fileName          
              }`)
            },
            'x-render-table-column': {
              minWidth: 150
            },
            'x-reactions': expression(`() => {
              const row = $table.getRowByIndex($self.index)
              const {
                status,
                extIsGoods,
                canEdit
              } = row || {}
              $self.readPretty = !(['WITHDRAW', 'DRAFT', 'REJECTED'].includes(status) && canEdit)
            }`)
          },
          createdUserName: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('common.creator')}}",
              width: 120,
              static: true
            },
            'x-read-pretty': true
          },
          createdFullName: {
            type: 'string',
            'x-hidden': true
          },
          creationDate: {
            title: "{{ $t('common.creationTime') }}",
            ...yearMonthDaySelectorSegment,
            'x-render-table-column': {
              width: 150,
              static: true
            },
            'x-read-pretty': true
          },
          extCloseComment: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('cusEntry.sup.extCloseReason'),
              minWidth: 120,
              static: true
            },
            'x-read-pretty': true
          },
          lastUpdateDate: {
            type: 'string',
            'x-query-engine-sort': 'desc',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          }

        })
      }
    }
  }
})
/* 撤回 */
const $recall = ($form: any, $queryEngine: any) => {
  /* 获取勾选的数据 */
  const selects = $form.query('table').take().componentProps.componentInstance.getCheckboxRecords()
  /* 校验需要勾选提交的数据 */
  if (selects.length === 0) {
    app.$message.warning($t('cusEntry.tipMessage.selectRowsMsg'))
    return false
  }
  /* 校验提交状态为已提交状态 */
  const index = selects.findIndex(item => item.status !== 'SUBMITTED')
  if (index > -1) {
    app.$message.warning($t('cusEntry.tipMessage.onlySelectRecallRows'))
    return false
  }
  /* 校验当前选择审批数据的创建人是否和当前登录人匹配 */
  const username = app.$store.getters.userInfo.username
  const validLeader = selects.findIndex(item => item.createdBy !== username)
  if (validLeader > -1) {
    app.$message.warning($t('cusEntry.tipMessage.loginerNoCreatedBy'))
    return false
  }
  $queryEngine.request.baseRequest({
    loading: true,
    action: "withdraw",
    type: 'ShopCart',
    payload: selects,
    query: {
      "*": {}
    }
  }).then((res) => {
    app.$message.success($t('common.success'))
    bus.$emit('ShopCart')
  })
}
/* 驳回 */
const $refuse = ($form: any, $queryEngine: any) => {
  /* 获取勾选的数据 */
  const selects = $form.query('table').take().componentProps.componentInstance.getCheckboxRecords()
  /* 校验需要勾选提交的数据 */
  if (selects.length === 0) {
    app.$message.warning($t('cusEntry.tipMessage.selectRowsMsg'))
    return false
  }
  /* 校验提交状态为已提交状态 */
  const index = selects.findIndex(item => item.status !== 'SUBMITTED')
  if (index > -1) {
    app.$message.warning($t('cusEntry.tipMessage.onlySelectRefuseRows'))
    return false
  }
  /* 校验当前选择审批数据的部门领导是否和当前登录人匹配 */
  const userId = app.$store.getters.userInfo.userId
  const validLeader = selects.findIndex(item => item.deptLeaderUserId !== userId)
  if (validLeader > -1) {
    app.$message.warning($t('cusEntry.tipMessage.loginerNoLeader'))
    return false
  }
  app.$prompt('', $t('cusEntry.tipMessage.refuseTip'), {
    confirmButtonText: $t('common.confirm'),
    cancelButtonText: $t('common.cancel'),
    inputType: 'textarea',
    inputValidator: (value) => {
      if (!value) {
        return $t('cusEntry.tipMessage.rejectReason')
      }
      return true
    }
  }).then(({ value }) => {
    $queryEngine.request.baseRequest({
      loading: true,
      action: "reject",
      type: 'ShopCart',
      payload: selects.map(item => ({ ...item, extRejectReason: value })),
      query: {
        "*": {}
      }
    }).then((res) => {
      app.$message.success($t('common.success'))
      bus.$emit('ShopCart')
    })
  })
}
/* 限制只能选择今日之后的日期 */
const $cannotLessCurrentTimeOptions = {
  disabledDate: time => {
    const nowDate = new Date()
    nowDate.setHours(0)
    nowDate.setMinutes(0)
    nowDate.setSeconds(0)
    nowDate.setMilliseconds(0)
    return time.getTime() < nowDate.getTime()
  }
}
const $initDept = async ($form: any) => {
  /* 获取业务实体和默认部门 */
  const username = app.$store.getters.userInfo.username
  const respone = await shoppingCartApi.getUserOrgAndDept(username)
  if (respone) {
    const {
      ouOrganization,
      departmentOrganization
    } = respone.data
    $form.query('ShopCart').get('data').defaultOrgInfo = ouOrganization
    $form.query('ShopCart').get('data').defaultDeptInfo = departmentOrganization
    /* 获取业务实体下的部门 */
    const params = {
      parentId: ouOrganization?.organizationId
    }
    shoppingCartApi.getDept(params).then(res => {
      if (res.data) {
        $form.query('ShopCart').get('data').deptList = res.data.map(item => ({
          label: item.organizationName,
          value: item.organizationCode,
          id: item.organizationId
        })) || []
      }
    })
    /* 获取默认收货地址 */
    if (departmentOrganization?.organizationId) {
      shoppingCartApi.getDeptAddress(departmentOrganization?.organizationId).then(res => {
        if (res.data) {
          /* 获取默认收货地址&收货人 */
          $form.query('ShopCart').get('data').defaultAddress = res.data.find(item => item.isDefault === 'Y') || {}
        }
      })
    }
  }
}
// @ts-ignore
const components = {
}

const scope = {
  $t,
  $delete,
  $ceeaSetSummaryAndNoticeUser,
  $confirmSum,
  $submitGet,
  $submitOut,
  $rejectSubmit,
  $keep,
  $deleteByIds,
  $closeByIds,
  $batchMaintenance,
  $batchSubmit,
  $createProject,
  smallImgSlot,
  formatImgUrl,
  getImgSrc,
  adaptData,
  getFileUrl,
  app,
  $approval,
  shoppingCartApi,
  $recall,
  $refuse,
  $cannotLessCurrentTimeOptions,
  $initDept
}
</script>
<template>
  <RenderEngine
    :pageAttrs="$attrs"
    :scope="scope"
    :components="components"
    :schema="schema"
    schemaKey="ShopCartList"
  />
</template>

<style lang="scss">
.export-excel {
  margin-left: 0px !important;
}
.shop-cart {
  .table-img {
    height: 80px;
    display: flex;
    align-items: center;
    justify-content: center;
    .logo-small {
      height: 60px;
      width: 94px;
      display: flex;
      justify-content: center;
      align-items: center;
      overflow: hidden;
      // background-image: linear-gradient(to right, #95A5C9 , #8295BF);
    }
  }
  .vxe-cell {
    max-height: fit-content !important;
  }
}
.shop-cart-table {
  line-height: 90px;
}
</style>
