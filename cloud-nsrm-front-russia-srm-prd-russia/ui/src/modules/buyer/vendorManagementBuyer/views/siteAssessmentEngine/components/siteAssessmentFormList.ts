import {expression,methodExpression , i18nExpression} from "@meicloud/render-engine";


export const siteAssessmentFormList = {
  siteFormId: {
    type: 'string',
    'x-hidden': true
  },
  createdId: {
    type: 'string',
    'x-hidden': true
  },
  assessmentType: {
    type: 'string',
    title: i18nExpression('vendorMod.siteType2'), // 供应商评审类型
    'x-decorator': 'FormItem',
    'x-component': 'DictSelect',
    'x-component-props': {
      code: 'CEEA_ASSESSMENT_TYPE',
      disabled: expression(`$form.readPretty || $attrs.params.flag == 'appraisal' || $form.query('state').get('data').titleDisabled`),
      '@change': expression(`
          (val) => {
            $self.query('.vendorName').take().setValue('')
            if (val && val !== 'ACCESS_ASSESSMENT') {
              $self.query('.reviewFormId').take().setValue(null) // 资质审查id
              $self.query('.reviewFormNumber').take().setValue('') // 资质审查单号
            } else {

            }
          }
      `)
    },
    'x-reactions': expression(`(field) => {
      const state = $form.query('state').get('data')
      const approveStatus = $self.query('approveStatus').get('value')
      field.disabled = ['PUBLISH','SUBMITTED','APPROVED','WITHDRAW','REJECTED'].includes(approveStatus) || state.bolType != 1 || state.disVendorName
    }`),
    'x-validator': {
      required: true
    }
  },
  vendorId: {
    type: 'string',
    'x-hidden': true
  },
  vendorCode: {
    type: 'string',
    'x-hidden': true
  },
  vendorName: {
    type: 'string',
    title: i18nExpression('common.vendorName'), // 供应商名称
    'x-decorator': 'FormItem',
    'x-component': 'QuickSearchWrapper',
    'x-component-props': {
      disabled:  expression(`$form.readPretty || $attrs.params.flag == 'appraisal' || $form.query('state').get('data').titleDisabled`),
      readPretty: '{{$form.readPretty}}',
      showKey: 'companyName',
      propKey: 'companyName',
      name: 'scc_sup_company_info2',
      '@close-quicksearch': expression(`(val) => {
          if (val) {
            console.log(val)
            $form.query('.vendorId').take().value = val.companyId
            $self.query('.vendorCode').take().value = val.companyCode
            $self.query('.vendorName').take().value = val.companyName

            const assessmentType = $self.query('assessmentType').get('value')
            console.log(assessmentType, 'assessmentType')
            // 根据供应商ID查询供应商档案组织与品类状态
            if (assessmentType && assessmentType != 'ACCESS_ASSESSMENT') {
              // this.getorgCateJournal(this.allParams.siteForm.vendorId)
              app.$http({
                url: '/api-sup/info/orgCategory/listPageOrgCategoryByParam',
                method: 'POST',
                data: { companyCode: val.companyCode , companyStatus: 'QUALIFIED', serviceStatus: 'QUALIFIED', pageNum: 1, pageSize: 999 },
                loading: true
              })
                .then(res => {
                  let result = res.data.list || []
                  let orgCateObjList = []
                    result.forEach(item => {
                      const orgCateObj = {
                        categoryCode: item.categoryCode,
                        categoryFullName: item.categoryFullName,
                        categoryId: item.categoryId,
                        categoryName: item.categoryName,
                        orgCode: item.orgCode,
                        orgId: item.orgId,
                        orgName: item.orgName,
                        quoted: 'N'
                      }
                      orgCateObjList.push(orgCateObj)
                    })
                  $form.query('.orgCateJournals').take().value = orgCateObjList
                })
                .catch(err => {
                  console.log(err)
                })
            }

            // // 将地址带到下面
            let address = {
              country: val.companyCountry,
              addressDetail: val.companyAddress,
              city: val.companyCity,
              province: val.companyProvince
            }
            let attr = []
            attr.push(address)
            $form.query('.siteFormAddressList').take().value = attr

            $form.query('state').get('data').reviewForm = {
              // 资质审查单入参
              't.vendor_id': val.companyId,
              't.approve_status': 'APPROVED',
              't.CEEA_IF_VENDOR_AUTH': 'Y'
            }
          }
      }`)
    },
    'x-reactions': expression(`(field) => {
      const state = $form.query('state').get('data')
      const approveStatus = $self.query('approveStatus').get('value')
      const assessmentType = $self.query('assessmentType').get('value')
      field.disabled = ['PUBLISH','SUBMITTED','APPROVED','WITHDRAW','REJECTED'].includes(approveStatus) || state.bolType != 1 || state.disVendorName || !assessmentType || $attrs.params.flag == 'appraisal || state.titleDisabled'
    }`),
    'x-validator': {
      required: true
    }
  },
  reviewFormId: {
    type: 'number',
    default: null,
    'x-hidden': true
  },
  quaReviewType: {
    type: 'string',
    'x-hidden': true
  },
  reviewFormNumber: {
    type: 'string',
    title: i18nExpression('vendorMod.quaNum'), // 资质审查单号
    'x-decorator': 'FormItem',
    'x-component': 'QuickSearchWrapper',
    'x-component-props': {
      readPretty: '{{$form.readPretty}}',
      showKey: 'reviewFormNumber',
      name: 'scc_sup_auth_review_form',
      'pre-query-data': expression(`$form.query('state').get('data').reviewForm`),
      '@close-quicksearch': expression(`(val) => {
          let quaReviewType = val ? val.quaReviewType : ''
          $self.query('.quaReviewType').value = quaReviewType
          $self.query('.reviewFormNumber').value = val ? val.reviewFormNumber : ''
          $self.query('.reviewFormId').take().value = val ? val.reviewFormId : ''
          const state = $form.query('state').get('data')
          // 获取组织品类关系
          if (val) {
            $self.query('.vendorCode').value = val.vendorCode
            $self.query('.vendorId').value = val.vendorId
            $self.query('.vendorName').value = val.vendorName
            state.disVendorName = true
            $getorgCateJournalByFormId(val.reviewFormId, true, $form) // 根据资质审查单Id查询组织和品类信息
            let query = { reviewFormId: val.reviewFormId, type: 'AUTH' }
            quaApi.getTemplateFilesByReviewFormId(query).then(res => {
              if (res.data && res.data.length > 0) {
                $form.query('.fileList').take().value = res.data.map(i => ({
                  ...i,
                  fileId: null,
                  fileName: ''
                }))
              } else {
                $form.query('.fileList').take().value = []
              }
            })
          } else {
            $form.query('.fileRecords').take().value = []
            state.disVendorName = false
          }
        }
      `)
    },
    'x-reactions': expression(`(field) => {
      // const isQuaFormIdDisabled = $attrs.params.flag === 'add'
      const isQuaFormIdDisabled = true
      const state = $form.query('state').get('data')
      const assessmentType = field.query('.assessmentType').get('value')
      field.disabled = !isQuaFormIdDisabled || state.bolType != 1 || $attrs.params.flag == 'appraisal'
      field.visible = (assessmentType == 'ACCESS_ASSESSMENT')
    }`),
    'x-validator': {
      required: true
    }
  },
  siteFormNumber: {
    type: 'string',
    'x-decorator': 'FormItem',
    title: i18nExpression('vendorMod.siteOrderInfoV'), // 供应商评审单号
    'x-query-engine-skip': true,
    'x-component-props': {
      disabled: true
    }
  },
  approveStatus: {
    type: 'string',
    title: i18nExpression('vendorMod.approveStatus'), // 审批状态
    'x-decorator': 'FormItem',
    'x-component': 'DictSelect',
    'x-component-props': {
      code: 'SUPPLIER_APPROVE_STATUS_TYPE',
      disabled: true
    },
    'x-query-engine-skip': true,
  },
  createdUserName: {
    type: 'string',
    'x-decorator': 'FormItem',
    title: i18nExpression('common.creator'), // 创建人
    'x-query-engine-skip': true,
    'x-component-props': {
      disabled: true
    }
  },
  creationDate: {
    type: 'string',
    'x-decorator': 'FormItem',
    title: i18nExpression('common.creationTime'), // 创建时间
    'x-query-engine-skip': true,
    'x-component-props': {
      disabled: true
    }
  },
  // 评审模板
  reviewModelId: {
    type: 'string',
    title: i18nExpression('dataConfMod.templateName'),
    'x-decorator': 'FormItem',
    'x-component': 'Select',
    'x-component-props': {
      disabled: expression(`$form.query('state').get('data').bolType != 1 || $attrs.params.flag == 'appraisal'`),
      '@change': expression(`(val) => {
          const siteFormPersonList = $form.query('.siteFormPersonList').take().value
          siteFormPersonList.forEach(e => {
            e.reviewModelId = val
          })
       }`)
    },
    'x-reactions': [
      expression(`(field) => {
      siteReviewModel.listAll().then(res => {
        let attr = []
        res.data.forEach(datas => {
          if (datas.approveStatus == 'ENABLE') {
            attr.push(datas)
          }
        })
        let datas = []
        attr.forEach(resData => {
          const objs = {
            key:resData.reviewModelId,
            label:resData.reviewModelName,
            value:resData.reviewModelId
          }
          datas.push(objs)
        })
        $form.query('state').get('data').modelList = datas
        $self.dataSource = datas
      })
      }`)
    ]
  },
  siteReviewPlanId: {
    type: 'string',
    'x-hidden': true
  },
  planName: {
    type: 'string',
    title: i18nExpression('vendorMod.planName3'), // 关联计划
    'x-decorator': 'FormItem',
    'x-component': 'QuickSearchWrapper',
    'x-component-props': {
      readPretty: '{{$form.readPretty}}',
      showKey: 'planName',
      propKey: 'planName',
      // disabled: expression(`$form.query('state').get('data').bolType != 1 || $attrs.params.flag == 'adds'`),
      disabled: expression(`$form.query('state').get('data').bolType != 1 || $attrs.params.flag == 'appraisal' || $form.query('state').get('data').titleDisabled`),
      name: 'scc_sup_site_review_plan',
      '@close-quicksearch': expression(`(val, scope) => {
        $self.query('.siteReviewPlanId').take().setValue(val.siteReviewPlanId)
        if (!val) {
          return false
        }
        let id = val.siteReviewPlanId
        siteReviewModel.address(id).then(res => {
          const addressList = res.data.addressList || []
          const personList = res.data.personList || []
          let siteFormAddressList = $form.query('.siteFormAddressList').take()
          console.log(res.data , 'data')
          console.log($form.values, 'value')
          $form.values.vendorName = res.data?.vendorName
          $form.values.vendorId = res.data?.vendorId
          $form.values.vendorCode = res.data?.vendorCode

          personList.forEach(datas => {
            datas.reviewModelId = this.allParams.siteForm.reviewModelId
          })
          $form.query('.siteFormPersonList').take().setValue(personList)

          addressList.forEach(datas => {
            const obj = {
              city: datas.city,
              addressDetail: datas.addressDetail,
              country: datas.country,
              province: datas.province,
              postCode: datas.postCode
            }
            if (!siteFormAddressList.value[0].country) {
              siteFormAddressList.value.splice(0, 1)
              siteFormAddressList.value.push(obj)
            } else {
              if (datas.addressDetail != siteFormAddressList.value[0].addressDetail) {
                siteFormAddressList.value.push(obj)
              }
            }
          })
        })
      }`)
    }
  },
  supplierViewFlag: {
    type: 'string',
    'x-decorator': 'FormItem',
    title: i18nExpression('vendorMod.supplierViewFlag'), // 是否允许供方查看结果
    'x-component': 'DictSelect',
    'x-component-props': {
      disabled: expression(`$form.query('state').get('data').bolType != 1 || $attrs.params.flag == 'appraisal'`),
      code: 'YES_OR_NO'
    }
  },
  siteFormExplain: {
    type: 'string',
    'x-decorator': 'FormItem',
    title: i18nExpression('vendorMod.siteExplainV'), // 供应商评审说明
    'x-component-props': {
      type: "textarea",
      disabled: expression(`$form.readPretty || $attrs.params.flag == 'appraisal'`)
    },
    'x-decorator-props': { gridSpan: 2 },
    'x-reactions': expression(`(field) => {
      const state = $form.query('state').get('data')
      field.disabled = state.bolType != 1
    }`)
  }
}
