/**
 * @description 邀请供应商
 */
import {
  expression,
  generateCharFunctionExpression,
  generateXindexInOrder,
  i18nExpression,
  ArrayFieldModel
} from '@meicloud/render-engine'
// @ts-ignore
import { mappingPropByBusinessTypeAndKey } from './utils'
// @ts-ignore
import { BUSINESS_TYPE_ENUM } from 'lib@/composition/origin/enum'
// @ts-ignore
import { getContactInfoByCompanyId } from 'modb@/vendorManagementBuyer/api/supApi'
// @ts-ignore
import recommendVendorDialog from 'lib@/compositionEngine/sourcing/inviteVendor/recommendVendorDialog/index'

const mapKeyType = () => {
  const mappingProp = (key: string) => {
    return mappingPropByBusinessTypeAndKey(BUSINESS_TYPE_ENUM.BARGAIN_LTS, key)
  }
  return {
    targetId: mappingProp('targetId'),
    targetNum: mappingProp('targetNum'),
    targetDesc: mappingProp('targetDesc'),
    quantity: mappingProp('quantity'),
    requirementLineId: mappingProp('requirementLineId'),
    linkManName: mappingProp('linkManName'),
    authList: mappingProp('authList'),
    phone: mappingProp('phone'),
    quoteForbid: mappingProp('quoteForbid')
  }
}

// 新增供应商
const addOneSuppliers = async (val: any, $form: any, $message: any) => {
  const data = $form.query('.recommendToolbar').get('data')
  const vendorList = $form.values.vendorList
  if (vendorList.find((item: any) => val.companyId === item.vendorId)) {
    // 供应商已存在，请勿重复添加
    $message.warning(`${val ? val.companyName || '' : ''} ${i18nExpression('bidMod.common.vendorRepeatMsg')}`)
    return
  }

  const vendor = {
    vendorId: val.companyId,
    vendorCode: val.companyCode,
    vendorName: val.companyName,
    [data.keyMap.linkManName]: '',
    [data.keyMap.phone]: '',
    email: ''
  }

  const response = await getContactInfoByCompanyId(val.companyId)
  if (Array.isArray((response || {}).data) && (response || {}).data.length > 0) {
    vendor[data.keyMap.linkManName] = response.data[0].contactName
    vendor[data.keyMap.phone] = response.data[0].ceeaContactMethod
    vendor.email = response.data[0].email
  }

  vendorList.push(vendor)
}

/* 选择供应商，冗余数据 */
const setVendorObj = async (val: any, row: any, $form: any, $message: any) => {
  const data = $form.query('.recommendToolbar').get('data')
  const {
    companyId = '',
    companyCode = '',
    companyName = ''
  }: any = val || {}

  if ($form.values.vendorList.find((item: any) => companyId && companyId === item.vendorId)) {
    // 供应商已存在，请勿重复添加
    $message.warning(`${companyName} ${i18nExpression('bidMod.common.vendorRepeatMsg')}`)
    row.vendorId = ''
    row.vendorCode = ''
    row.vendorName = ''
    row[data.keyMap.linkManName] = ''
    row[data.keyMap.phone] = ''
    row.email = ''
    return
  }

  row.vendorId = companyId
  row.vendorCode = companyCode
  row.vendorName = companyName
  if (!val) {
    row[data.keyMap.linkManName] = ''
    row[data.keyMap.phone] = ''
    row.email = ''
  } else {
    const response = await getContactInfoByCompanyId(companyId)
    if (Array.isArray((response || {}).data) && (response || {}).data.length > 0) {
      row[data.keyMap.linkManName] = response.data[0].contactName
      row[data.keyMap.phone] = response.data[0].ceeaContactMethod
      row.email = response.data[0].email
    }
  }
}

/* 选择联系人，冗余数据 */
const setContactObj = (val: any, $table: any, index: any, $form: any) => {
  const data = $form.query('.recommendToolbar').get('data')
  const vendorList = $form.values.vendorList
  const row = $table.getRowByIndex(index)
  if (typeof val === 'string') {
    row[data.keyMap.linkManName] = val || ''
    row[data.keyMap.phone] = ''
    row.email = ''
    vendorList.splice(index, 1, row)
    return
  }
  const {
    contactName = '',
    mobileNumber = '',
    ceeaContactMethod = '',
    email = ''
  } = val || {}
  row[data.keyMap.linkManName] = contactName
  row[data.keyMap.phone] = mobileNumber || ceeaContactMethod || ''
  row.email = email
  vendorList.splice(index, 1, row)
}

// 打开智能供应商
const openRecommendVendorDialog = ($form: any, $queryEngine: any) => {
  $form.query('.recommendDialog').take().setComponentProps({
    visible: true
  })
}

/* 初始化编排权限表格数据 */
const getInviteSuppliersData = ($form: any, props: any, data: any, vendorList: any) => {
  if (!props.showSuppliersPermission) {
    return false
  }

  return $form.values.itemList.map((item: any) => {
    // 权限编排
    const permission: any = {}

    vendorList.forEach((vendorItem: any) => {
      const auth = (vendorItem[data.keyMap.authList] || []).find((authItem: any) => {
        return authItem[data.keyMap.requirementLineId] === item[data.keyMap.requirementLineId]
      })
      permission[vendorItem.vendorId] = (auth || {})[data.keyMap.quoteForbid] || 'N'
    })
    console.log(permission, 'permission')

    return {
      [data.keyMap.targetId]: item[data.keyMap.targetId],
      [data.keyMap.targetNum]: item[data.keyMap.targetNum],
      [data.keyMap.targetDesc]: item[data.keyMap.targetDesc],
      categoryId: item.categoryId,
      categoryCode: item.categoryCode,
      categoryName: item.categoryName,
      orgOuId: item.orgOuId,
      orgOuName: item.orgOuName,
      unit: item.unit,
      souItemId: item.souItemId,
      [data.keyMap.requirementLineId]: item[data.keyMap.requirementLineId],
      [data.keyMap.quantity]: item[data.keyMap.quantity],
      ...permission
    }
  })
}

/* 返回当前数据 父组件外部调用 */
const getSuppliersPermissionData = ($form: any, props: any, $t: any) => {
  const vendorList = $form.values.vendorList
  const { showSuppliersPermission } = props
  const data = $form.query('.recommendToolbar').get('data')
  const authList = $form.query('.authList').take()
  authList.setValue(getInviteSuppliersData($form, props, data, vendorList))
  const permissionVendor = vendorList.map((item: any) => {
    let resultItem = {
      ...item
    }

    if (showSuppliersPermission) {
      resultItem = {
        ...resultItem,
        [data.keyMap.authList]: authList.value.map((permissionItem: any) => {
          // 删除冗余的数据
          delete permissionItem.availableCategoryIds
          delete permissionItem.availableOrgIds
          return {
            ...permissionItem,
            // 默认 N
            [data.keyMap.quoteForbid]: permissionItem[item.vendorId] || 'N'
          }
        })
      }
    }

    return resultItem
  })

  return permissionVendor
}

/* 保存智能推荐供应商 */
const saveRecommendVendorBus = async (vendor = [], $form: any, props: any, $t: any) => {
  const data = $form.query('.recommendToolbar').get('data')
  const vendorList = $form.values.vendorList
  const souVendorVnod = $form.query('.vendorList').take()

  souVendorVnod.setValue([...vendorList, ...vendor.map((item: any) => {
    return {
      ...item,
      // 冗余空值，否则会导致表格输入框组件无法绑定bind
      [data.keyMap.linkManName]: item.contactName || '',
      [data.keyMap.phone]: item.ceeaContactMethod || '',
      email: item.email || ''
    }
  })])

  if (!props.showSuppliersPermission) {
    return
  }
  const authList = $form.query('.authList').take()
  const suppliersPermissionData = await getInviteSuppliersData($form, props, data, vendorList)
  console.log(suppliersPermissionData, 'suppliersPermissionData')

  authList.setValue(suppliersPermissionData.map((item: any) => {
    let suggest = {}
    vendor.forEach((vendorItem: any) => {
      if (
        !(vendorItem.availableCategoryIds.includes(item.categoryId) &&
          vendorItem.availableOrgIds.includes(item.orgOuId))
      ) {
        // 判断当前物料中，供应商的品类list 以及 实体list中，需要同时存在，如不符合，则建议禁止报价，打勾
        suggest = {
          ...suggest,
          [vendorItem.vendorId]: 'Y'
        }
      }
    })
    return {
      ...item,
      ...suggest
    }
  }))

  console.log(authList, 'authList')
}
const isInit = true

const setPermissionListField = ($form: any, field: any, vendorList: any, $t: any, props: any) => {
  console.log(vendorList, 'setPermissionListField vendorList')
  const data = $form.query('.recommendToolbar').get('data')

  const addProperties: any = {
    isQuoteForbid: {
      type: 'void',
      title: $t('bidMod.isquoteForbid'),
      'x-component-props': {
        'align': 'center'
      },
      'x-component': 'RenderTable.Group',
      properties: {}
    }
  }
  for (const item of vendorList) {
    addProperties.isQuoteForbid.properties[item.vendorId] = {
      type: 'string',
      title: item.vendorName,
      'x-component': 'Checkbox',
      'x-component-props': {
        trueLabel: 'Y',
        falseLabel: 'N',
        disabled: false
      },
      'x-render-table-column': {
        minWidth: 160
      }
    }
  }
  setTimeout(() => {
    field.invoke('addProperties', addProperties)
    field.setValue(getInviteSuppliersData($form, props, data, vendorList))
  })
}

interface InviteVendorProps {
  showSuppliersPermission?: boolean;
  // 是否需要智能推荐供应商
  showRecommendVendor?: boolean;
  // 是否需要报价权限功能
  showVendorPermission?: boolean;
  // 是否支持多选
  multiSelect?: boolean;
  // 额外内容插槽
  toolbarSlot?: object;
  // 额外按钮插槽
  buttonsSlot?: object;
  scope: object;
}

export default function (props?: InviteVendorProps): Record<any, any> {
  const {
    showSuppliersPermission = true,
    showRecommendVendor = true,
    multiSelect = false,
    toolbarSlot = {},
    buttonsSlot = {},
    scope = {}
  } = props || {}

  // 注册方法
  Object.assign(scope, {
    addOneSuppliers,
    setVendorObj,
    setContactObj,
    openRecommendVendorDialog,
    saveRecommendVendorBus,
    setPermissionListField,
    getInviteSuppliersData
  })

  const listeners = {
    getSuppliersPermissionData
  }

  return {
    recommendDialog: {
      'x-decorator': 'QueryEngine',
      ...recommendVendorDialog({ scope, props, listeners })
    },
    saveRecommendVendorBus: {
      type: 'void',
      'x-component': 'BusEvent',
      'x-component-props': {
        eventName: 'saveRecommendVendorBus',
        '@listener': generateCharFunctionExpression(({ $form, $t }, vendorList) => {
          saveRecommendVendorBus(vendorList, $form, props, $t)
        })
      }
    },
    // 按钮区
    recommendToolbar: {
      type: 'void',
      // FIXME 只读隐藏
      // 'x-visible': false,
      'x-component': 'Space',
      'x-component-props': {
        style: 'margin-bottom: 10px'
      },
      // 记录动态计算变量
      'x-data': {
        keyMap: mapKeyType(),
        permissionProperties: {}
      },
      properties: {
        ...toolbarSlot,
        // 智能推荐
        smartRecommend: {
          type: 'void',
          'x-visible': showRecommendVendor,
          title: i18nExpression('bidMod.smartRecommond'),
          'x-component': 'RButton',
          'x-component-props': {
            type: 'primary',
            '@click': generateCharFunctionExpression(({ $form, $queryEngine }) => {
              openRecommendVendorDialog($form, $queryEngine)
            })
          }
        },
        // 新增供应商
        companyCode: {
          type: 'void',
          'x-component': 'QuickSearch',
          'x-component-props': {
            showKey: 'companyCode',
            name: 'scc_sup_company_info',
            showButton: true,
            btnTitle: i18nExpression('vendorMod.addVendor'),
            multiSelect: multiSelect,
            style: 'display: inline-block; vertical-align: top; margin: 0 10px;',
            '@close-quicksearch': expression('(val) => addOneSuppliers(val, $form, $message)')
          }
        },
        ...buttonsSlot
      }
    },

    // 邀请供应商列表
    vendorList: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-component-props': {
        preColumns: 'seq',
        class: 'table-view-vxe-table',
        sortable: false,
        editMode: true
      },
      properties: generateXindexInOrder({
        // 供应商编码
        vendorCode: {
          type: 'string',
          title: i18nExpression('bidMod.vendorCode'),
          'x-render-table-column': {
            minWidth: 100
          },
          'x-read-pretty': '{{$readonly}}',
          'x-component': 'QuickSearchWrapper',
          'x-component-props': {
            showKey: 'companyCode',
            name: 'scc_sup_company_info',
            '@close-quicksearch': generateCharFunctionExpression(({ $form, $table, $self, $message }, val) => {
              setVendorObj(val, $table.getRowByIndex($self.index), $form, $message)
            })
          }
        },
        // 供应商名称
        vendorName: {
          type: 'string',
          'x-read-pretty': true,
          title: i18nExpression('bidMod.vendorName'),
          'x-render-table-column': {
            minWidth: 150
          }
        },
        // 联系人
        linkmanName: {
          type: 'string',
          title: i18nExpression('bidMod.linkMan'),
          'x-render-table-column': {
            minWidth: 150
          },
          'x-component': 'QuickSearchWrapper',
          'x-read-pretty': '{{$readonly}}',
          'x-component-props': {
            showKey: 'contactName',
            name: 'scc_sup_contact_info',
            preQueryData: { 't.COMPANY_ID': 'vendorId' },
            allowInput: true,
            '@close-quicksearch': generateCharFunctionExpression(({ $form, $table, $self }, val) => {
              setContactObj(val, $table, $self.index, $form)
            })
          }
        },
        // 电话
        phone: {
          type: 'string',
          title: i18nExpression('bidMod.phone'),
          'x-render-table-column': {
            minWidth: 150
          }
        },
        // 邮箱
        email: {
          type: 'string',
          title: i18nExpression('bidMod.email2'),
          'x-render-table-column': {
            minWidth: 180
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
          properties: {
            // 删除
            delete: {
              type: 'void',
              title: i18nExpression('common.delete'),
              'x-component-props': {
                '@click': generateCharFunctionExpression(({ $table, $self }) => {
                  $table.remove($self.index)
                })
              }
            }
          }
        }
      })
    },
    // 报价权限
    vendorPermissionTableTips: {
      type: 'void',
      'x-reactions': generateCharFunctionExpression(({ $form, props }, field) => {
        field.visible = props.showSuppliersPermission && $form.values.vendorList.length
      }),
      'x-component': 'p',
      'x-content': i18nExpression('bidMod.offerPermissions')
    },
    // 报价权限列表
    authList: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-reactions': generateCharFunctionExpression(({ $form, props, $t }, field) => {
        field.visible = props.showSuppliersPermission && $form.values.vendorList.length
        console.log(field, 'authList')
        if (field.visible) {
          setPermissionListField($form, field, $form.values.vendorList, $t, props)
        }
      }),
      'x-component-props': {
        preColumns: 'seq',
        class: 'table-view-vxe-table',
        sortable: false,
        editMode: false
      },
      properties: {
        // 物料编码
        itemCode: {
          type: 'string',
          title: i18nExpression('bidMod.itemCode'),
          'x-render-table-column': {
            minWidth: 150
          }
        },
        // 物料描述
        itemDesc: {
          type: 'string',
          title: i18nExpression('bidMod.itemDesc'),
          'x-render-table-column': {
            minWidth: 150
          }
        },
        // 物料分类
        categoryName: {
          type: 'string',
          title: i18nExpression('bidMod.categoryName'),
          'x-render-table-column': {
            minWidth: 150
          }
        },
        // 单位
        unit: {
          type: 'string',
          title: i18nExpression('bidMod.unit'),
          'x-render-table-column': {
            minWidth: 150
          }
        },
        // 预计数量
        requireQuantity: {
          type: 'string',
          title: i18nExpression('bidMod.demandQuantity'),
          'x-render-table-column': {
            minWidth: 100
          }
        }
      }
    }
  }
}
