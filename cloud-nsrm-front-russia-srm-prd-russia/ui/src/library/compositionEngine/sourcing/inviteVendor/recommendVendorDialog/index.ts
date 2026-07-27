// 推荐供应商
import { generateXindexInOrder, i18nExpression, generateCharFunctionExpression, generateCharFunctionExpressionAsIs } from '@meicloud/render-engine'
import { $arrangeOnSetChartData } from '../../quoteRecordChart'

// 获取智能推荐供应商
const getAiRecommend = ($form: any, $http: any, $message: any) => {
  console.log('获取智能推荐供应商')
  console.log($form, '$form')
  console.log($form.query('.recommendVendorList').take(), 'recommendVendorList')
  const recommendVendorList = $form.query('.recommendVendorList').take()
  const { itemList, inqSouProject } = $form.values
  console.log(itemList, 'itemList')
  if (!itemList.length) {
    return $message.warning('请先添加需求物料！')
  }

  $http({
    url: '/api-sup/vendorOrgCategory/aiRecommend',
    method: 'POST',
    data: {
      // [业务实体ID: 需求品类ID] 键值对象
      orgCategoryIds: itemList.reduce((prev: any, next: any, index: any) => {
        return {
          ...(index === 0 ? { [prev.orgOuId]: prev.categoryId } : prev),
          [next.orgOuId.toString()]: next.categoryId
        }
      }, itemList[0]),
      // 是否需要排除黑名单供应商
      excludeBlackVendors: inqSouProject.excludeBlackVendors,
      // 是否需要排除非本业务实体内的供应商
      excludeNoCurrentOrgVendors: inqSouProject.excludeNoCurrentOrgVendors,
      // 是否需要排除业务实体退出的供应商
      excludeOrgQuitVendors: inqSouProject.excludeOrgQuitVendors,
      // 是否需要排除指定品类状态的供应商
      excludeOrgCategoryStatus: inqSouProject.excludeOrgCategoryStatus
    },
    loading: true
  }).then((res: any) => {
    if (res && res.data) {
      recommendVendorList.setValue((res.data || []).map((item: any) => {
        // 重构数据结构，只取需要的字段
        const vendor = item.vendor || {}
        return {
          vendorId: vendor.companyId,
          vendorCode: vendor.companyCode,
          vendorName: vendor.companyName,
          contactName: vendor.contactName,
          ceeaContactMethod: vendor.ceeaContactMethod,
          email: vendor.email,
          availableCategoryIds: item.availableCategoryIds || [],
          availableOrgIds: item.availableOrgIds || []
        }
      }))
    }
  })
}

/* 提交供应商 */
const saveRecommendVendor = ($form: any, $self: any, $message: any, props: any, listeners: any, $bus: any, $t: any) => {
  console.log($self, '提交供应商 self')
  console.log($self.query('.recommendVendorList').take(), '提交供应商 recommendVendorList')

  const selections = $self.query('.recommendVendorList').take().componentProps.componentInstance.getCheckboxRecords()

  const vendorsData = listeners.getSuppliersPermissionData($form, props, $t)
  console.log(vendorsData, 'vendorsData')

  if (selections.length === 0) {
    $message.warning('请先勾选供应商!')
    return
  }

  for (let o = 0; o < selections.length; o++) {
    const id = selections[o].vendorId
    if (id && (vendorsData || []).map((v: any) => v.vendorId).includes(id)) {
      const name = selections[o].vendorName
      $message.warning('\'' + name + '\'已存在,请勿重复添加!')
      return
    }
  }

  $bus.$emit('saveRecommendVendorBus', selections.map((item: any) => {
    return {
      vendorId: item.vendorId,
      vendorCode: item.vendorCode,
      vendorName: item.vendorName,
      contactName: item.contactName,
      ceeaContactMethod: item.ceeaContactMethod,
      email: item.email,
      availableCategoryIds: item.availableCategoryIds || [],
      availableOrgIds: item.availableOrgIds || []
    }
  }))

  $self.setComponentProps({ visible: false })
}

export default ({ scope, props, listeners }: any) => {
  // 注册方法
  Object.assign(scope, {
    getAiRecommend,
    saveRecommendVendor,
    props,
    listeners
  })
  return {
    type: 'void',
    title: i18nExpression('bidMod.recommendVendor'),
    'x-component': 'RDialog',
    'x-component-props': {
      footer: true,
      beforeClose: generateCharFunctionExpression(({ $form, $message, $self, $bus, $t }, done, type) => {
        console.log(type, 'type')
        if (type === 'ok') {
          saveRecommendVendor($form, $self, $message, props, listeners, $bus, $t)
        }
        done()
      }),
      '@opened': generateCharFunctionExpression(({ $form, $http, $message }) => {
        getAiRecommend($form, $http, $message)
      })
    },
    properties: {
      recommendVendorList: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          class: 'table-view-vxe-table',
          preColumns: 'seq, checkbox',
          pagination: false,
          sortable: false,
          height: 300,
          primaryKey: 'vendorId'
        },
        'x-query-engine-skip': true,
        properties: generateXindexInOrder({
          // 供应商编码
          vendorCode: {
            type: 'string',
            title: i18nExpression('bidMod.vendorCode'),
            'x-render-table-column': {
              minWidth: 150
            }
          },
          // 供应商名称
          vendorName: {
            type: 'string',
            title: i18nExpression('bidMod.vendorName'),
            'x-render-table-column': {
              minWidth: 150
            }
          }
        })
      }
    }
  }
}
