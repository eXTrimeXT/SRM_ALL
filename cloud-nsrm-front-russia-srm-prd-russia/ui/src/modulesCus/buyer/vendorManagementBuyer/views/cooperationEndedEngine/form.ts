import { expression, i18nExpression } from '@meicloud/render-engine'

export const forms = {
  vendorId: {
    type: 'string',
    'x-decorator': 'FormItem',
    'x-hidden': true
  },
  vendorCode: {
    type: 'string',
    'x-decorator': 'FormItem',
    'x-hidden': true
  },
  vendorName: {
    type: 'string',
    title: i18nExpression('common.vendorName'), // 供应商名称
    'x-decorator': 'FormItem',
    'x-component': 'QuickSearchWrapper',
    'x-component-props': {
      readPretty: '{{$form.readPretty}}',
      showKey: 'vendorName',
      propKey: 'vendorName',
      name: 'scc_sup_company_info2',
      '@close-quicksearch': expression(`async (val) => {
          if (val) {
            let res = await blackComApi.findByCompanyIdAndStatus(val.companyId)
            if (res.data.length == 0) {
              if (val.companyId) {
                $form.query('.vendorId').take().value = val.companyId
              }
              if (val.companyCode) {
                $form.query('.vendorCode').take().value = val.companyCode
              }
              if (val.companyName) {
                $form.query('.vendorName').take().value = val.companyName
              } else {
                $form.query('.vendorName').take().value = ''
              }
              $form.query('.supplierControlType').take().value = null
            } else {
              $form.query('.vendorName').take().value = ''
              return app.$message({
                type: 'warning',
                message: '当前供应商存在在途的，或已生效的黑名单明细或合作终止单据，无法重复新增'
              })
            }
          } else {
            $form.query('.vendorId').take().value = ''
            $form.query('.vendorCode').take().value = ''
            $form.query('.vendorName').take().value = ''
            $form.query('.supplierControlType').take().value = null
          }
      }`)
    }
  },
  supplierControlType: {
    type: 'string',
    title: i18nExpression('vendorMod.controlType'), // 控制类型
    'x-decorator': 'FormItem',
    'x-component': 'DictSelect',
    'x-component-props': {
      code: 'SUPPLIER_CONTROL_TYPE2',
      '@change': expression(`async(type) => {
        // 获取当前控制范围
        const rangeData = $form.query('rangeData').take().value
        // 获取当前控制明细
        const rangeDetail = $form.query('state').get('data').listDataAll
        // 获取删除数据行
        let rangeDeleteList = $form.query('state').get('data').rangeDeleteList
        rangeData?.forEach(item => {
          if (item.orgCatFormCategoryId) {
            rangeDeleteList.push({
              $delete: item.orgCatFormCategoryId
            })
          }
        })
        // 获取删除数据明细行
        let rangeDetailDeleteList = $form.query('state').get('data').rangeDetailDeleteList
        rangeDetail?.forEach(item => {
          if (item.orgCatFormCategoryId) {
            rangeDetailDeleteList.push({
              $delete: item.orgCatFormCategoryId
            })
          }
        })
        const vendorId = $form.query('.vendorId').take()?.value
        if (!vendorId) {
          app.$message({
            message: $t('vendorMod.msgVendor'), // '请选择供应商',
            type: 'warning'
          })
          return
        }
        const obj = { vendorId: vendorId }
        let result = null
        // 解冻类 品类解冻 组织解冻 整体解冻
        if (['CATEGORY_THAW', 'ORGANIZATION_THAW', 'OVERALL_THAW'].includes(type)) {
          result = await orgCatForm.listForzenOrgCategory(obj)
        } else if (['POSITION_LIMIT_FLAG_REMOVE', 'CATEGORY_LIMIT_FLAG_REMOVE'].includes(type)){
          if (type === 'CATEGORY_LIMIT_FLAG_REMOVE') {
            obj.supplierControlType = type
          }
          result = await orgCatForm.orgOrCategoryLimitRemove(obj)
        } else if (['CATEGORY_LIMIT_FLAG', 'POSITION_LIMIT_FLAG','CATEGORY_EXIT','ORGANIZATION_EXIT','OVERALL_EXIT'].includes(type)){
          result = await orgCatForm.orgOrCategoryLimit(obj)
        } else {
          result = await orgCatForm.listOrgCategory(obj)
        }

        const { data } = result
        if (['ORGANIZATION_EXIT', 'ORGANIZATION_FORZEN', 'ORGANIZATION_THAW', 'POSITION_LIMIT_FLAG', 'POSITION_LIMIT_FLAG_REMOVE'].includes(type)) { // 组织退出 组织受限 组织受限解除
          $form.query('.rangeData').take().value = data.orgRangeList // 组织数据
        } else if (['CATEGORY_EXIT', 'CATEGORY_FORZEN', 'CATEGORY_THAW', 'CATEGORY_LIMIT_FLAG', 'CATEGORY_LIMIT_FLAG_REMOVE'].includes(type)) { // 品类退出 品类受限
          $form.query('.rangeData').take().value = data.categoryRangeList // 品类数据
        }
        $form.query('state').get('data').listDataAll = data.detailList
      }`)
    }
  },
  startDate: {
    type: 'date',
    title: i18nExpression('vendorMod.startDate'), // 生效日期
    // 'x-component-props': {
    //   disabled: true
    // },
    'x-decorator': 'FormItem',
    'x-validator': {
      required: true,
      message: i18nExpression('common.requiredField')
    },
  },
  orgCatFormNumber: {
    type: 'string',
    title: i18nExpression('vendorMod.controlNumber'), // 控制单号
    'x-decorator': 'FormItem',
    'x-component-props': {
      disabled: true
    }
  },
  approveStatus: {
    type: 'string',
    title: i18nExpression('vendorMod.operationStatus'), // 状态
    'x-decorator': 'FormItem',
    'x-component': 'DictSelect',
    'x-component-props': {
      code: 'APPROVE_STATUS_TYPE',
      disabled: true
    }
  },
  createdBy: {
    type: 'string',
    title: i18nExpression('common.creator'), // 创建人
    'x-decorator': 'FormItem',
    'x-component-props': {
      disabled: true
    }
  },
  creationDate: {
    type: 'string',
    title: i18nExpression('common.creationTime'), // 创建时间
    'x-decorator': 'FormItem',
    'x-component-props': {
      disabled: true
    }
  },
  timeLimitDate: {
    type: 'date',
    title: i18nExpression('cusEntry.vendorMod.limitDeadLine'),
    'x-decorator': 'FormItem',
    'x-validator': {
      required: expression('$form.values.supplierControlType === \'TIME_LIMIT_FLAG\''),
      message: i18nExpression('cusEntry.tipMessage.limitDeadLineMsg')
    },
    'x-visible': `{{$form.query('.supplierControlType').take().value == 'TIME_LIMIT_FLAG'}}`,
  },
  cooperationEndedReasonType: {
    type: 'string',
    title: i18nExpression('cusEntry.vendorMod.cooperationEndedReasonType'),
    'x-decorator': 'FormItem',
    'x-component': 'DictSelect',
    'x-component-props': {
      code: 'SUPPLIER_LIMIT_TYPE'
    }
  }
}
