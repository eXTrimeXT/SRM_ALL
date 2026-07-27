import{ae as expression,af as i18nExpression,ai as generateXindexInOrder,ag as yearMonthDaySelectorSegment,cc as formGridSegment,al as defineComponent,am as usePageHelper,an as useAttrs,ao as ref$1,bS as computed,as as RenderEngine,ar as defineSchemas,n as normalizeComponent}from"./index-17d0ccd5.js";import{v as vendorAccessAttachment}from"./vendorAccessAttachment-c3ab363b.js";import{s as supCommonApi}from"./supApi-e5726083.js";import{c as siteReviewModel,q as quaApi}from"./vendorManagement-dfc64e68.js";import{C as CFillProgress}from"./index-6af40985.js";import{P as Printer}from"./printer-20ef2763.js";const Steps={steps:{type:"void","x-decorator":"div","x-decorator-props":{class:"stepDiv"},"x-component":"Steps","x-component-props":{alignCenter:!0,finishStatus:"success"},"x-reactions":{dependencies:["approveStatus"],fulfill:{state:{"component[1].active":expression(`
            ['DRAFT', 'WITHDRAW', 'REJECTED'].includes($deps[0])
            ? 0
            : ['PUBLISH'].includes($deps[0])
            ? 1
            : ['SUBMITTED'].includes($deps[0])
            ? 2
            : ['APPROVED'].includes($deps[0])
            ? 3 : 0
          `)}}},properties:{step1:{type:"void","x-component":"el-step","x-component-props":{title:expression("$t('填写现场评审')")}},step2:{type:"void","x-component":"el-step","x-component-props":{title:expression("$t('工作人员评审')")}},step3:{type:"void","x-component":"el-step","x-component-props":{title:expression("$t('评审报告提交')")}},step4:{type:"void","x-component":"el-step","x-component-props":{title:expression("$t('结果审批')")}}}}},siteAssessmentFormList={siteFormId:{type:"string","x-hidden":!0},createdId:{type:"string","x-hidden":!0},assessmentType:{type:"string",title:i18nExpression("vendorMod.siteType2"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"CEEA_ASSESSMENT_TYPE",disabled:expression("$form.readPretty || $attrs.params.flag == 'appraisal' || $form.query('state').get('data').titleDisabled"),"@change":expression(`
          (val) => {
            $self.query('.vendorName').take().setValue('')
            if (val && val !== 'ACCESS_ASSESSMENT') {
              $self.query('.reviewFormId').take().setValue(null) // 资质审查id
              $self.query('.reviewFormNumber').take().setValue('') // 资质审查单号
            } else {

            }
          }
      `)},"x-reactions":expression(`(field) => {
      const state = $form.query('state').get('data')
      const approveStatus = $self.query('approveStatus').get('value')
      field.disabled = ['PUBLISH','SUBMITTED','APPROVED','WITHDRAW','REJECTED'].includes(approveStatus) || state.bolType != 1 || state.disVendorName
    }`),"x-validator":{required:!0}},vendorId:{type:"string","x-hidden":!0},vendorCode:{type:"string","x-hidden":!0},vendorName:{type:"string",title:i18nExpression("common.vendorName"),"x-decorator":"FormItem","x-component":"QuickSearchWrapper","x-component-props":{disabled:expression("$form.readPretty || $attrs.params.flag == 'appraisal' || $form.query('state').get('data').titleDisabled"),readPretty:"{{$form.readPretty}}",showKey:"companyName",propKey:"companyName",name:"scc_sup_company_info2","@close-quicksearch":expression(`(val) => {
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
      }`)},"x-reactions":expression(`(field) => {
      const state = $form.query('state').get('data')
      const approveStatus = $self.query('approveStatus').get('value')
      const assessmentType = $self.query('assessmentType').get('value')
      field.disabled = ['PUBLISH','SUBMITTED','APPROVED','WITHDRAW','REJECTED'].includes(approveStatus) || state.bolType != 1 || state.disVendorName || !assessmentType || $attrs.params.flag == 'appraisal || state.titleDisabled'
    }`),"x-validator":{required:!0}},reviewFormId:{type:"number",default:null,"x-hidden":!0},quaReviewType:{type:"string","x-hidden":!0},reviewFormNumber:{type:"string",title:i18nExpression("vendorMod.quaNum"),"x-decorator":"FormItem","x-component":"QuickSearchWrapper","x-component-props":{readPretty:"{{$form.readPretty}}",showKey:"reviewFormNumber",name:"scc_sup_auth_review_form","pre-query-data":expression("$form.query('state').get('data').reviewForm"),"@close-quicksearch":expression(`(val) => {
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
      `)},"x-reactions":expression(`(field) => {
      // const isQuaFormIdDisabled = $attrs.params.flag === 'add'
      const isQuaFormIdDisabled = true
      const state = $form.query('state').get('data')
      const assessmentType = field.query('.assessmentType').get('value')
      field.disabled = !isQuaFormIdDisabled || state.bolType != 1 || $attrs.params.flag == 'appraisal'
      field.visible = (assessmentType == 'ACCESS_ASSESSMENT')
    }`),"x-validator":{required:!0}},siteFormNumber:{type:"string","x-decorator":"FormItem",title:i18nExpression("vendorMod.siteOrderInfoV"),"x-query-engine-skip":!0,"x-component-props":{disabled:!0}},approveStatus:{type:"string",title:i18nExpression("vendorMod.approveStatus"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"SUPPLIER_APPROVE_STATUS_TYPE",disabled:!0},"x-query-engine-skip":!0},createdUserName:{type:"string","x-decorator":"FormItem",title:i18nExpression("common.creator"),"x-query-engine-skip":!0,"x-component-props":{disabled:!0}},creationDate:{type:"string","x-decorator":"FormItem",title:i18nExpression("common.creationTime"),"x-query-engine-skip":!0,"x-component-props":{disabled:!0}},reviewModelId:{type:"string",title:i18nExpression("dataConfMod.templateName"),"x-decorator":"FormItem","x-component":"Select","x-component-props":{disabled:expression("$form.query('state').get('data').bolType != 1 || $attrs.params.flag == 'appraisal'"),"@change":expression(`(val) => {
          const siteFormPersonList = $form.query('.siteFormPersonList').take().value
          siteFormPersonList.forEach(e => {
            e.reviewModelId = val
          })
       }`)},"x-reactions":[expression(`(field) => {
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
      }`)]},siteReviewPlanId:{type:"string","x-hidden":!0},planName:{type:"string",title:i18nExpression("vendorMod.planName3"),"x-decorator":"FormItem","x-component":"QuickSearchWrapper","x-component-props":{readPretty:"{{$form.readPretty}}",showKey:"planName",propKey:"planName",disabled:expression("$form.query('state').get('data').bolType != 1 || $attrs.params.flag == 'appraisal' || $form.query('state').get('data').titleDisabled"),name:"scc_sup_site_review_plan","@close-quicksearch":expression(`(val, scope) => {
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
      }`)}},supplierViewFlag:{type:"string","x-decorator":"FormItem",title:i18nExpression("vendorMod.supplierViewFlag"),"x-component":"DictSelect","x-component-props":{disabled:expression("$form.query('state').get('data').bolType != 1 || $attrs.params.flag == 'appraisal'"),code:"YES_OR_NO"}},siteFormExplain:{type:"string","x-decorator":"FormItem",title:i18nExpression("vendorMod.siteExplainV"),"x-component-props":{type:"textarea",disabled:expression("$form.readPretty || $attrs.params.flag == 'appraisal'")},"x-decorator-props":{gridSpan:2},"x-reactions":expression(`(field) => {
      const state = $form.query('state').get('data')
      field.disabled = state.bolType != 1
    }`)}},siteFormHistoryList={historyTableAll:{type:"void","x-decorator":"QueryEngine","x-query-engine":{service:"sup",type:"SiteForm",actions:{paginationQuery:{immediate:!0,ready:expression(`() => {
            try {
              return !!$attrs.params.row.vendorName || !!$form.query('.vendorName').take().value ? true : false
            } catch (e) {
              return false
            }
          }`),transformRequest:expression(`(data, headers) => {
            let vendorName = $form.query('.vendorName').take().value
            if (!vendorName || vendorName == '') {
              vendorName = $attrs.params.row.vendorName
            }
            data.payload.filter = {
              "vendorName": {
                eq: vendorName
              }
            }
            return data
          }`)}}},properties:{historyTable:{type:"array","x-component":"RenderTable","x-component-props":{maxHeight:400,preColumns:"seq",class:"table-view-vxe-table",openCustomTable:!1},"x-reactions":expression(`(field) => {
              // 查找历史记录
              const vendorName = $form.query('.vendorName').take().value
              if (!vendorName || vendorName == '' || vendorName == {}){
                return false
              }

              $queryEngine.state.paginationManagement.query()
            }`),properties:generateXindexInOrder({approveStatus:{type:"string",title:"{{$t('vendorMod.orderStatus')}}","x-component":"DictSelect","x-component-props":{code:"SUPPLIER_APPROVE_STATUS_TYPE"},"x-render-table-column":{minWidth:90}},siteFormNumber:{type:"string",title:"{{$t('vendorMod.siteFormNumber')}}","x-render-table-column":{minWidth:120}},assessmentType:{type:"string",title:"{{$t('vendorMod.assessmentType')}}","x-component":"DictSelect","x-component-props":{code:"CEEA_ASSESSMENT_TYPE"},"x-render-table-column":{minWidth:150}},reviewFormNumber:{type:"string",title:"{{$t('vendorMod.reviewFormNumber2')}}","x-render-table-column":{minWidth:150}},vendorName:{type:"string",title:"{{$t('vendorMod.vendorName')}}","x-render-table-column":{minWidth:100}},siteAdress:{type:"string",title:"{{$t('vendorMod.siteAdress')}}","x-render-table-column":{minWidth:140}},createdFullName:{type:"string",title:"{{$t('vendorMod.createdFullName')}}","x-render-table-column":{minWidth:110}},creationDate:{title:"{{$t('vendorMod.creationDate2')}}",...yearMonthDaySelectorSegment,"x-render-table-column":{width:130}},reviewResult:{type:"string",title:"{{$t('vendorMod.reviewResult')}}","x-component":"DictSelect","x-component-props":{code:"CEEA_RESULT_TYPE"},"x-render-table-column":{minWidth:150}}})}}}},authBaseInfo={toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},"x-reactions":expression(`(field) => {
      field.visible = !$form.readPretty
    }`),properties:{add:{type:"void",title:i18nExpression("common.add"),"x-component-props":{disabled:expression("$disabledAdd($form)"),type:"primary","@click":expression(`() => {
           $self.query('siteFormAddressList')
             .take(field => {
               field.componentProps.componentInstance.addRow()
           })
          }`)}}}},siteFormAddressList:{type:"array","x-component":"RenderTable","x-component-props":{primaryKey:"siteFormAddressId",cascadeDeletion:!0,editMode:!0,maxHeight:400,pagination:!1,sortable:!1},"x-query-engine-skip":!0,properties:generateXindexInOrder({authFlag:{type:"string",title:i18nExpression("本次认证地址(请勾选)"),"x-render-table-column":{minWidth:150},"x-component":"Checkbox","x-component-props":{trueLabel:"Y",falseLabel:"N",disabled:expression("$disabledAdd($form)")}},country:{type:"string",title:i18nExpression("components.address.country"),"x-render-table-column":{minWidth:120},"x-component":"DictSelect","x-component-props":{code:"country",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$disabledAdd($form)"),"@change":expression(`(val) => {
             let row = $table.getRowByIndex($self.index)
             // 选择国外就清理省市区，并且禁用
             if (row.plantCountry !== 'CN') {
               row.province = null
               row.city = null
             }
          }`)}},province:{type:"string",title:i18nExpression("components.address.area"),"x-render-table-column":{minWidth:120},"x-component":"DictSelect","x-component-props":{code:"PROVINCE","custom-select-type":"PROVINCE",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$disabledAdd($form) || $table.getRowByIndex($self.index).country!='CN'")}},city:{type:"string",title:i18nExpression("components.address.city"),"x-render-table-column":{minWidth:120},"x-component":"DictSelect","x-component-props":{code:expression("$table.getRowByIndex($self.index).province"),"custom-select-type":"CITY",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$disabledAdd($form) || $table.getRowByIndex($self.index).country!='CN'")}},addressDetail:{type:"string",title:i18nExpression("components.address.detailAddress"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$disabledAdd($form)")}},postCode:{type:"string",title:i18nExpression("components.address.postalCode"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$disabledAdd($form)")}},siteComment:{type:"string",title:i18nExpression("components.address.remark"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$disabledAdd($form)")}},enableFlag:{type:"string",title:i18nExpression("vendorMod.enableFlag"),"x-render-table-column":{minWidth:120},"x-component":"Checkbox","x-component-props":{"true-label":"true","false-label":"false",disabled:expression("$disabledAdd($form)")}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{disabled:expression("$disabledAdd($form)"),type:"text","@click":expression(`({ row }) => {
                  $table.remove($self.index)
              }`)}}}}})}},authOrganization={orgCateJournals:{type:"array","x-component":"RenderTable","x-component-props":{maxHeight:400,pagination:!1,sortable:!1},"x-query-engine-skip":!0,properties:generateXindexInOrder({quoted:{type:"string",title:i18nExpression(" "),"x-render-table-column":{width:45},"x-component":"Checkbox","x-component-props":{trueLabel:"Y",falseLabel:"N",disabled:expression("$disabledAdd($form)")}},orgName:{type:"string",title:i18nExpression("vendorMod.orgName2"),"x-render-table-column":{minWidth:200}},categoryName:{type:"string",title:i18nExpression("vendorMod.categoryName"),"x-render-table-column":{minWidth:200}}})}},siteFormPersonList={toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},"x-reactions":expression(`(field) => {
      field.visible = !$form.readPretty
    }`),properties:{add:{type:"void",title:i18nExpression("common.add"),"x-component-props":{disabled:expression("$disabledAdd($form)"),type:"primary","@click":expression(`() => {
           $self.query('siteFormPersonList')
             .take(field => {
               field.componentProps.componentInstance.addRow()
           })
          }`)}}}},siteFormPersonList:{type:"array","x-component":"RenderTable","x-component-props":{primaryKey:"siteFormPersonId",cascadeDeletion:!0,preColumns:"seq",editMode:!0,maxHeight:400,pagination:!1,sortable:!1},"x-query-engine-skip":!0,properties:generateXindexInOrder({personType:{type:"string",title:i18nExpression("vendorMod.personType"),"x-render-table-column":{minWidth:120},"x-component":"DictSelect","x-component-props":{code:"PERSON_TYPE",disabled:expression("$disabledAdd($form)")}},userAccount:{type:"string",title:i18nExpression("vendorMod.userAccount"),"x-render-table-column":{minWidth:120},"x-component":"QuickSearchWrapper","x-component-props":{name:expression("$table.getRowByIndex($self.index).personType == 'GRAND_JURY' ? 'scc_rbac_user_vendor_display' : 'scc_rbac_user_display'"),showKey:"username",disabled:expression("$disabledAdd($form)"),"@close-quicksearch":expression(`(val) => {
              $table.getRowByIndex($self.index).userAccount = val ? val.username : ''
              $table.getRowByIndex($self.index).userName = val ? val.nickname : ''
              $table.getRowByIndex($self.index).userId = val ? val.userId : ''
              $table.getRowByIndex($self.index).userTel = val ? val.phone : ''
              $table.getRowByIndex($self.index).userEmail = val ? val.email : ''
           }`)}},userName:{type:"string",title:i18nExpression("vendorMod.userName2"),"x-render-table-column":{minWidth:100},"x-read-pretty":!0},userTel:{type:"string",title:i18nExpression("vendorMod.mobilePhone"),"x-render-table-column":{minWidth:100},"x-read-pretty":!0},userEmail:{type:"string",title:i18nExpression("vendorMod.emailAddress"),"x-render-table-column":{minWidth:100},"x-read-pretty":!0},userPost:{type:"string",title:i18nExpression("bidMod.position"),"x-render-table-column":{minWidth:100},"x-component-props":{disabled:expression("$disabledAdd($form)")}},reviewModelId:{type:"number",default:null,title:i18nExpression("vendorMod.reviewModelId"),"x-render-table-column":{minWidth:100},"x-component-props":{disabled:expression("$disabledAdd($form)")},"x-component":"Select","x-reactions":[expression(`(field) => {
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
              $self.dataSource = datas
            })
          }`)]},onSiteFlag:{type:"string",title:i18nExpression("vendorMod.onSiteFlag"),"x-render-table-column":{minWidth:120},"x-component":"Checkbox","x-component-props":{"true-label":"Y","false-label":"N",disabled:expression("$disabledAdd($form)")}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{disabled:expression("$disabledAdd($form)"),type:"text","@click":expression(`({ row }) => {
                  $table.remove($self.index)
              }`)}}}}})}},results={modelResultList:{type:"array","x-hidden":!0},Dialog:{type:"void",title:"132","x-component":"RDialog","x-component-props":{beforeClose:expression(`(done, type) => {
            if ( type === 'ok') {
              const state = $form.query('state').get('data')
              const fileRecordId = state.fileRecordId
              let reviewFormStandardDimList = []
              console.log($form.query('.printer').take())
              const tableDatas = $form.query('.printer').take().componentProps.tableData
              const editableTabs = $form.query('.printer').take().componentProps.editableTabs
              let modelResultList = $form.query('.modelResultList').take().value
              let tableData = JSON.stringify(tableDatas)
              editableTabs.forEach(datas1 => {
                let d1 = {
                  dimName: datas1.title,
                  dimType: datas1.type,
                  dimId: datas1.dimId,
                  siteReviewModelId: datas1.siteReviewModelId
                }
                let attr = []
                datas1.content.forEach(datas2 => {
                  let d2
                  if (datas1.type == 'TABLE') {
                    for (let i = 0;i<modelResultList.length;i++){
                        if (modelResultList[i].fieldCode == datas2.fieldCode && fileRecordId == modelResultList[i].fileRecordId){
                            modelResultList[i] = {
                              fieldName: datas2.fieldName,
                              fieldCode: datas2.fieldCode,
                              fieldValue: tableData,
                              dictCode: datas2.dictCode,
                              fieldType: datas2.fieldType,
                              assemblyType: datas2.assemblyType,
                              necessaryFlag: datas2.necessaryFlag,
                              dimFieldId: datas2.dimFieldId,
                              siteReviewModelId: datas2.siteReviewModelId,
                              dimId: datas2.dimId
                            }
                        }
                    }
                  } else {
                      for (let i = 0;i<modelResultList.length;i++){
                        if (modelResultList[i].fieldCode == datas2.fieldCode && fileRecordId == modelResultList[i].fileRecordId){
                            modelResultList[i] = {
                              fieldName: datas2.fieldName,
                              fieldValue: datas2.fieldContent,
                              fieldCode: datas2.fieldCode,
                              dictCode: datas2.dictCode,
                              fieldType: datas2.fieldType,
                              assemblyType: datas2.assemblyType,
                              necessaryFlag: datas2.necessaryFlag,
                              dimFieldId: datas2.dimFieldId,
                              siteReviewModelId: datas2.siteReviewModelId,
                              dimId: datas2.dimId
                            }
                        }
                    }
                  }
                })
              })
              $form.query('.modelResultList').take().value = modelResultList
              done()
            } else {
              done()
            }
      }`)},properties:{printer:{type:"array","x-component":"Printer","x-component-props":{editableTabs:expression("$form.query('state').get('data').editableTabs"),"read-only":"false",tableData:expression("$form.query('state').get('data').tableData")},"x-query-engine-skip":!0}}},fileRecords:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,maxHeight:400,pagination:!1,sortable:!1},"x-query-engine-skip":!0,properties:generateXindexInOrder({reviewModelId:{type:"string","x-hidden":!0,"x-render-table-column":{}},reviewModelName:{type:"string","x-component":"TableButton","x-component-props":{type:"text","@click":expression(`({row}) => {
              const id = $form.query('.reviewModelId').take().value
              siteReviewModel.getDetail(id).then(res => {
                  $form.query('.Dialog').take().title = res.data.reviewModelName
                  const modelResultList = $form.query('.modelResultList').take().value
                  const state = $form.query('state').get('data')
                  console.log(modelResultList)

                  let editableTabs = []
                  res.data.dimList.forEach((datas, indexs) => {
                    const index = String(indexs + '1')
                    console.log(index,'index')
                    let d1 = {
                      dimId: datas.dimId,
                      siteReviewModelId: datas.siteReviewModelId,
                      title: datas.dimName,
                      name: index,
                      type: datas.dimType,
                      content: []
                    }
                    let attr = []
                    if (datas.dimType == 'TABLE') {
                      let tableIndex = 0
                      datas.fieldList.forEach(datas2 => {
                        let d2 = {
                          fieldCode: datas2.fieldCode,
                          fieldName: datas2.fieldName,
                          dictCode: datas2.dictCode,
                          fieldType: datas2.fieldType,
                          assemblyType: datas2.assemblyType,
                          necessaryFlag: datas2.necessaryFlag,
                          dimFieldId: datas2.dimFieldId,
                          siteReviewModelId: datas2.siteReviewModelId,
                          dimId: datas2.dimId
                        }
                        attr.push(d2)
                        for (let i = 0;i < modelResultList.length; i++) {
                          if (modelResultList[i].fieldCode == datas2.fieldCode && modelResultList[i].fileRecordId == row.fileRecordId) {
                            tableIndex = i
                          }
                        }
                      })
                      try {
                        state.tableData = JSON.parse(modelResultList[tableIndex].fieldValue)
                      } catch (error) {
                        console.log(error)
                      }
                    } else {
                      datas.fieldList.forEach(datas2 => {
                        for (let i = 0;i < modelResultList.length; i++) {
                          if (modelResultList[i].fieldCode == datas2.fieldCode && modelResultList[i].fileRecordId == row.fileRecordId) {
                            let d2 = {
                              fieldCode: datas2.fieldCode,
                              fieldName: datas2.fieldName,
                              dictCode: datas2.dictCode,
                              fieldContent: modelResultList[i].fieldValue,
                              fieldType: datas2.fieldType,
                              assemblyType: datas2.assemblyType,
                              necessaryFlag: datas2.necessaryFlag
                            }
                            attr.push(d2)
                          }
                        }
                      })
                    }
                    d1.content = attr
                    editableTabs.push(d1)
                  })
                  state.editableTabs = editableTabs
                  state.fileRecordId = row.fileRecordId
                  $form.query('.Dialog').take().setComponentProps({ visible: true })
              })
          }`)},"x-reactions":expression(`() => {
            setTimeout(() => {
                const modelList = $form.query('state').get('data').modelList
                const id = $form.query('.reviewModelId').take().value
                let obj = ''
                modelList.forEach(datas => {
                  if (datas.value == id) {
                    obj = datas.label
                  }
                })
                $self.value = obj
            })
        }`),"x-render-table-column":{title:"{{$t('vendorMod.reviewModelId')}}",minWidth:140,customRender:!0}},fileName:{type:"string","x-hidden":!0,"x-render-table-column":{}},fileId:{type:"string",title:"{{$t('vendorMod.attachmentUpload')}}","x-component":"SrmCommonFile","x-component-props":{"extra-data":{uploadType:"DEF",sourceType:"WEB_APP",fileModular:"sup",fileFunction:"siteAssessment",fileType:"images"},"default-file":expression(`{
            fileId: $table.getRowByIndex($self.index).fileId,
            fileName: $table.getRowByIndex($self.index).fileName
          }`),readonly:expression("$attrs.params.flag == 'view' || $form.query('state').get('data').bolType == 3 "),"@on-change":expression(`({file}) => {
            const { fileId = null, fileName = '' } = file || {}
            let row = $table.getRowByIndex($self.index)
            row.fileId = fileId.toString()
            row.fileName = fileName
          }`)},"x-render-table-column":{minWidth:140}},userName:{type:"string","x-hidden":!0},reviewPeopleName:{type:"string",title:i18nExpression("vendorMod.reviewPeopleName"),"x-component-props":{disabled:!0},"x-render-table-column":{minWidth:100},"x-reactions":expression(`() => {
            const userName = $table.getRowByIndex($self.index)?.userName
            $self.value = userName
          }`)},userPost:{type:"string",title:i18nExpression("vendorMod.userPost"),"x-component-props":{disabled:!0},"x-render-table-column":{minWidth:100}},reviewDate:{type:"string",title:i18nExpression("vendorMod.reviewTime"),"x-component-props":{disabled:!0},"x-render-table-column":{minWidth:100}},score:{type:"string",title:i18nExpression("vendorMod.score"),"x-component-props":{disabled:expression("$form.query('state').get('data').bolType == 3 || $table.getRowByIndex($self.index)?.disable == true")},"x-render-table-column":{minWidth:100}},authResult:{type:"string",title:i18nExpression("vendorMod.result"),"x-component-props":{disabled:expression("$form.query('state').get('data').bolType == 3 || $table.getRowByIndex($self.index)?.disable == true")},"x-render-table-column":{minWidth:100}},remark:{type:"string",title:i18nExpression("common.remark"),"x-component-props":{disabled:expression("$form.query('state').get('data').bolType == 3 || $table.getRowByIndex($self.index)?.disable == true")},"x-render-table-column":{minWidth:100}}})},certificationResultForm:{type:"void",...formGridSegment,properties:{reviewResult:{type:"string",title:i18nExpression("vendorMod.finalResult"),"x-decorator":"FormItem","x-component":"DictSelect","x-decorator-props":{gridSpan:1},"x-component-props":{code:"CEEA_RESULT_TYPE"},"x-reactions":expression(`() => {
            const state = $form.query('state').get('data')
            const approveStatus = $form.query('.approveStatus').get('value')
            const accountNameId = app.$store.getters.userId
            const createdId = $form.query('.createdId').take().value
            if ((state.bolType == 2 && accountNameId != createdId) || approveStatus == 'APPROVED' || $form.readPretty){
              $self.disabled = true
            } else {
              $self.disabled = false
            }
          }`)},ceeaResultExplain:{type:"string",title:i18nExpression("vendorMod.resultExplain"),"x-decorator":"FormItem","x-decorator-props":{gridSpan:2},"x-component-props":{type:"textarea",rows:"2"},"x-reactions":expression(`() => {
            const state = $form.query('state').get('data')
            const approveStatus = $form.query('.approveStatus').get('value')
            const accountNameId = app.$store.getters.userId
            const createdId = $form.query('.createdId').take().value
            if ((state.bolType == 2 && accountNameId != createdId) || approveStatus == 'APPROVED' || $form.readPretty){
              $self.disabled = true
            } else {
              $self.disabled = false
            }
          }`)}}}},collapseMain={type:"void","x-component":"Collapse",properties:generateXindexInOrder({siteAssessmentForm:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("route.siteAssessment2")},"x-query-engine-skip":!0,properties:{siteAssessmentFormList:{type:"void",...formGridSegment,"x-reactions":expression(`() => {
              setTimeout(() => {
                const { row } = $attrs.params
                console.log($attrs,'datas')
                $self.form.setValues(row)
              })
            }`),properties:{...siteAssessmentFormList}}}},siteFormHistoryList:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.historySiteAssessment")},"x-query-engine-skip":!0,properties:{...siteFormHistoryList}},authBaseInfo:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.authBaseInfo")},"x-query-engine-skip":!0,properties:{...authBaseInfo}},authOrganization:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.authOrganization")},"x-query-engine-skip":!0,properties:{...authOrganization}},workingGroupStaff:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.workingGroupStaff")},"x-query-engine-skip":!0,properties:{...siteFormPersonList}},attachment:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.attachment")},"x-query-engine-skip":!0,properties:{fileList:{type:"array","x-component":"vendorAccessAttachment","x-component-props":{"sence-code":"QUA",attOpt:expression("$form.readPretty || $attrs.params.flag == 'appraisal' ? 'view' : 'add'"),"up-file-info":{uploadType:"DEF",sourceType:"WEB_APP",fileModular:"sup",fileFunction:"siteAssessment",fileType:"images"}}}}},certificationResult:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.certificationResult")},"x-query-engine-skip":!0,properties:{...results}}})},_sfc_main=defineComponent({__name:"siteAssessmentDetailEngine",setup(__props){const{app,emitTabRemove,t,vendor}=usePageHelper(),attrs=useAttrs(),workflowStatus=ref$1("DRAFT"),$disabledFlag=computed(()=>!0),customUpdateButton=computed(()=>!$disabledFlag.value&&["SUPPLIER_SUBMITTED"].includes(workflowStatus.value)),viewUpdateButton=$form=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;return componentInstance.workflowParamsInfo.integrationMode,["None",null].includes(componentInstance.workflowParamsInfo.integrationMode),attrs.params.flag!="view"},disabledUpdateButton=()=>!(attrs.params.flag=="view"),$disabledAdd=$form=>{const states=$form.query("state").get("data");return states.addressReadOnly||states.bolType==4||["PUBLISH","SUBMITTED","APPROVED"].includes(states.orderStatus)||$form.readPretty},initButtonConfig=$form=>{setTimeout(()=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.buttonConfigInfo.save.view=viewUpdateButton($form),componentInstance.buttonConfigInfo.submit.view=viewUpdateButton($form),componentInstance.buttonConfigInfo.cancel.view=!0,componentInstance.buttonConfigInfo.close.view=!1;const reviewResult=$form.query(".reviewResult").take().value;(!reviewResult||reviewResult=="")&&(componentInstance.buttonConfigInfo.save.name="暂存",componentInstance.buttonConfigInfo.submit.name="提交")},50)},updateButtonConfig=$form=>{setTimeout(()=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.buttonConfigInfo.save.view=viewUpdateButton($form),componentInstance.buttonConfigInfo.submit.view=viewUpdateButton($form),componentInstance.buttonConfigInfo.cancel.view=!1,componentInstance.buttonConfigInfo.close.view=!1},50)},schema=defineSchemas({state:{type:"void","x-component":"Fragment","x-hidden":!0,"x-data":{bolType:1,disVendorName:!1,reviewForm:{"t.vendor_id":"","t.approve_status":"APPROVED","t.CEEA_IF_VENDOR_AUTH":"Y"},addressReadOnly:!1,modelList:[],editableTabs:[],tableData:[],fileRecordId:0,titleDisabled:!1}},SiteForm:{type:"void","x-decorator":"QueryEngine","x-component":"el-container","x-component-props":{class:"flex-container siteAssessment",direction:"vertical"},"x-query-engine":{service:"sup",actions:{save:{cascadeDeletion:!0},query:{immediate:!0,ready:expression(`() => {
            $form.readPretty = $attrs.params.flag === 'view'
            initButtonConfig($form)
            console.log($attrs.params, 'params')
            if ($attrs.params.flag == 'adds') {
              let row = $attrs.params.row
              row.assessmentType = row.planType
              $form.setValues($attrs.params.row)
              $form.query('state').get('data').reviewForm = {
              // 资质审查单入参
              't.vendor_name': row.vendorName,
              't.approve_status': 'APPROVED',
              't.CEEA_IF_VENDOR_AUTH': 'Y'
            }
              $form.query('state').get('data').titleDisabled = true
            }
            return $attrs.params.flag != 'add' && $attrs.params.row?.siteFormId
          }`),autoFormatResult:!1,transformRequest:expression(`(data, headers) => {
            if (data.typeGet == "history") {
              delete data.typeGet
              return data
            }
            data.query = {
              "*":{},
              "siteFormPersonList": {'*': {}},
              "siteFormAddressList": {'*': {}},
              "fileRecords": {
                '*': {},
                modelResultList: {'*': {}}
              },
              "fileList": {'*': {}},
              "orgCateJournals": {'*': {}},
            }
            data.payload = {
              "filter": {
                  "siteFormId": {
                      eq: $attrs.params.row.siteFormId
                  }
              }
            }
            return data
          }`),transformResponse:expression(`(res) => {
            const ress = JSON.parse(res)

            if (ress.data.records.length > 1) {
              return ress
            }

            const { readOnly } = $attrs.params

            let datas = ress.data.ref.SiteForm[$attrs.params.row.siteFormId]
            // 设置文本只读
            // $form.readPretty = readOnly || ['VENDOR_CONFIRMED'].includes(datas.approveStatus)
            // 流程按钮是否置灰判断
            const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
            const tabDisabled = !(datas.reviewResult && datas.approveStatus != 'DRAFT')
            componentInstance.setWorkflowTabDisabled(tabDisabled)

            let tablePersons = []
            let tableFileRecords = []
            let tableOrgCateJournals = []
            let tableSiteFormAddressList = []
            let tableFileList = []
            let tableModelResultList = []
            const personsList = datas.siteFormPersonList
            const fileRecords = datas.fileRecords
            const orgCateJournals = datas.orgCateJournals
            const siteFormAddressList = datas.siteFormAddressList
            const fileList = datas.fileList
            personsList.forEach((e) => {
              tablePersons.push(ress.data.ref.SiteFormPerson[e])
            })
            fileRecords.forEach((e) => {
              tableFileRecords.push(ress.data.ref.FileRecords[e])
              ress.data.ref.FileRecords[e].modelResultList.forEach(eModel => {
                tableModelResultList.push(ress.data.ref.SiteReviewModelResult[eModel])
              })
            })
            orgCateJournals.forEach((e) => {
              tableOrgCateJournals.push(ress.data.ref.OrgCateJournal[e])
            })
            siteFormAddressList.forEach((e) => {
              tableSiteFormAddressList.push(ress.data.ref.SiteFormAddress[e])
            })
            fileList.forEach((e) => {
              tableFileList.push(ress.data.ref.FileList[e])
            })

            $form.setValues(datas)
            $form.query('.siteFormPersonList').take().setValue(tablePersons)
            $form.query('.fileRecords').take().setValue(tableFileRecords)
            $form.query('.orgCateJournals').take().setValue(tableOrgCateJournals)
            $form.query('.siteFormAddressList').take().setValue(tableSiteFormAddressList)
            $form.query('.fileList').take().setValue(tableFileList)
            $form.query('.modelResultList').take().setValue(tableModelResultList)

            // 确定是那个评审状态
              let cMen = 0// 是否创建人
              let pMen = 0// 是否评审人
              let fMen = 0// 当前人在fileRecords是否有值
              const accountId = app.$store.getters.userId
              const createdId = $form.query('.createdId').take().value
              console.log(accountId,'accountId')
              console.log(createdId,'createdId')
              if (accountId == createdId) {
                cMen = 1
              }
              let fileRecordsAll = []
              let fileRecordsR = $form.query('.fileRecords').take().value
              if (fileRecordsR) {
                fileRecordsR.forEach(e => {
                  if (e.reviewPeople == accountId) {
                    fMen = 1
                  }
                })
              }
              console.log(fMen, 'fMen')
              if (!fileRecordsR || fileRecordsR == [] || fileRecordsR == '' || fMen == 0){
                const siteFormPersonList = $form.query('.siteFormPersonList').take().value
                if (cMen == 1) {
                  fileRecordsR?.forEach(e => {
                      e.disable = true
                      fileRecordsAll.push(e)
                  })
                }
                siteFormPersonList.forEach(datas => {
                  if (cMen == 1) {
                    if (accountId == datas.userId) {
                      datas.reviewPeople = accountId
                      fileRecordsAll.push(datas)
                    }
                  } else if (datas.userId == accountId) {
                    datas.reviewPeople = accountId
                    fileRecordsAll.push(datas)
                  }
                })
                console.log(fileRecordsAll, 'fileRecordsAll1')
                $form.query('.fileRecords').take().value = fileRecordsAll
              } else {
                fileRecordsR.forEach(datas => {
                  if (cMen == 1) {
                    if (datas.score && datas.authResult) {
                      if (accountId != datas.reviewPeople) {
                        datas.disable = true
                      }
                      fileRecordsAll.push(datas)
                    } else {
                      if (accountId == datas.reviewPeople) {
                        fileRecordsAll.push(datas)
                      }
                    }
                  } else if (datas.reviewPeople == accountId) {
                    fileRecordsAll.push(datas)
                  }
                })
                console.log(fileRecordsAll, 'fileRecordsAll')
                $form.query('.fileRecords').take().value = fileRecordsAll
              }

              const approveStatus = $form.query('.approveStatus').take().value
              const states = $form.query('state').get('data')
              if ($attrs.params.flag == 'add' || approveStatus == 'DRAFT') {
                states.bolType = 1
              } else if ($attrs.params.flag == 'edit') {
                tablePersons.forEach(datas => {
                  if (datas.userId == accountId) {
                    states.bolType = 2
                    states.addressReadOnly = true
                  }
                })

                let scoreBol = 0 // 检查是否有写得分
                let authResultBol = 0 // 检查是否有写结果
                fileRecordsR.forEach(datas => {
                  if (datas.score) {
                    scoreBol = 1
                  }
                  if (datas.authResult) {
                    authResultBol = 1
                  }
                })

                if (
                  accountId == createdId &&
                  scoreBol == 1 &&
                  authResultBol == 1 && states.bolType != 2
                ) {
                  states.bolType = 3
                }
              } else {
                states.bolType = 4
              }
            setTimeout(() => {
              if (states.bolType == 1) {
                $form.query('.fileRecords').take().visible = false
              } else {
                $form.query('.fileRecords').take().visible = true
              }
            })

            return ress
          }`)}}},properties:{SchemaWorkflow:{type:"void","x-component":"SchemaWorkflow","x-component-props":{"business-id":expression("$attrs.params.row?.siteFormId || null"),"business-type":"SUPPLIER","@click-handler":expression(`(type) => {
            $submits(type, $form, $queryEngine, $message, $t, $bus)
          }`),"@submit-direct":expression(`(type) => {
            $submits(type, $form, $queryEngine, $message, $t, $bus)
          }`),"@confirm":expression(`(type, comment) => {
            $submits(type, $form, $queryEngine, $message, $t, $bus)
          }`),"@close-tab":expression(`() => {
            $back($bus)
          }`),"@update-integration-mode":expression(`(integrationMode) => {
            if (integrationMode.integrationMode == "None") {
              updateButtonConfig($form)
            }
          }`)},properties:{layout:{type:"void","x-component":"FormContainer",properties:{...Steps,layout:{type:"void","x-component":"FormContainer",properties:{collapse:{...collapseMain}}}}}}},CFillProgress:{type:"void","x-component":"CFillProgress","x-component-props":{class:"contract-progress",ref:"contractProgress",nodeName:"$t('logisticsMod.contractInfo')",data:`{{[
              {
                code: 'siteAssessmentForm',
                name: $t('route.siteAssessment2'),
                percentage: 0
              },
              {
                code: 'siteFormHistoryList',
                name: $t('vendorMod.historySiteAssessment'),
                percentage: 0
              },
              {
                code: 'authBaseInfo',
                name: $t('vendorMod.authBaseInfo'),
                percentage: 0
              },
              {
                code: 'authOrganization',
                name: $t('vendorMod.authOrganization'),
                percentage: 0
              },
              {
                code: 'workingGroupStaff',
                name: $t('vendorMod.workingGroupStaff'),
                percentage: 0
              },
              {
                code: 'attachment',
                name: $t('vendorMod.attachment'),
                percentage: 0
              }
            ]}}`,percentage:"{{true}}","@index-click":`{{ (code) => {
              let anchorEle = document.querySelector('#collapse_' + code)
              if (anchorEle) {
                anchorEle.scrollIntoView(true)
              }
           } }}`}}}}}),$back=$bus=>{emitTabRemove(attrs.tabName),$bus.$emit("siteA")},$submits=async(type,$form,$queryEngine,$message,$t,$bus)=>{let values=$form.values;const tablefileList=$form.query(".fileList").get("value"),tableorgCateJournals=$form.query(".orgCateJournals").get("value"),tablesiteFormAddressList=$form.query(".siteFormAddressList").get("value"),tablesiteFormPersonList=$form.query(".siteFormPersonList").get("value");values.fileList=tablefileList,values.orgCateJournals=tableorgCateJournals,values.siteFormAddressList=tablesiteFormAddressList,values.siteFormPersonList=tablesiteFormPersonList;let bolOrgCate=1;if(tableorgCateJournals.forEach(datas=>{datas.quoted=="Y"&&(bolOrgCate=0)}),bolOrgCate)return app.$message.error(app.$t("请选择认证组织和品类")),!1;attrs.params.row?.siteFormId;const approveStatus=$form.query(".approveStatus").take()?.value;return(async()=>{if(type=="SAVE")[null,void 0,"DRAFT"].includes(approveStatus)?(values.approveStatus="DRAFT",$queryEngine.request.save(values,{query:{"*":{},siteFormPersonList:{"*":{}},siteFormAddressList:{"*":{}},fileRecords:{"*":{}},fileList:{"*":{}},orgCateJournals:{"*":{}}}}).then(()=>{$message.success($t("common.successSave")),$bus.$emit("siteA"),emitTabRemove(attrs.tabName)}).catch(err=>{})):$queryEngine.request.save(values,{query:{"*":{},siteFormPersonList:{"*":{}},siteFormAddressList:{"*":{}},fileRecords:{"*":{}},fileList:{"*":{}},orgCateJournals:{"*":{}}}}).then(()=>{$message.success($t("common.successSave")),$bus.$emit("siteA"),emitTabRemove(attrs.tabName)}).catch(err=>{});else{const reviewResult=$form.query(".reviewResult").take()?.value;!reviewResult||reviewResult==""?(values.approveStatus="PUBLISH",$queryEngine.request.save(values,{query:{"*":{}},siteFormPersonList:{"*":{}},siteFormAddressList:{"*":{}},fileRecords:{"*":{}},fileList:{"*":{}},orgCateJournals:{"*":{}}}).then(()=>{$message.success($t("common.successSubmit")),$bus.$emit("siteA"),emitTabRemove(attrs.tabName)})):$queryEngine.request.save(values,{query:{"*":{},siteFormPersonList:{"*":{}},siteFormAddressList:{"*":{}},fileRecords:{"*":{}},fileList:{"*":{}},orgCateJournals:{"*":{}}}}).then(res=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.setWorkflowBusinessId(res.data[0]?.siteFormId||null),componentInstance.setWorkflowTabDisabled(!0),componentInstance.setWorkflowBusinessVariables({}),componentInstance.handlerAfter(type.toUpperCase(),()=>{$bus.$emit("siteA"),emitTabRemove(attrs.tabName)})})}})()},$getorgCateJournalByFormId=(reviewFormId,isAdd=!1,$form)=>{app.$http({url:"/api-sup/review/reviewForm/listOrgAndCategoryByReviewId",method:"GET",params:{reviewFormId},loading:!0}).then(res=>{if(res.data){const orgData=res.data.orgInfos,catData=res.data.orgCategorys;if(isAdd){let orgCateJournalsArr=[];orgData.forEach(oItem=>{catData.forEach(cItem=>{let orgCateObj={categoryCode:cItem.categoryCode,categoryFullName:cItem.categoryFullName,categoryId:cItem.categoryId,categoryName:cItem.categoryName,orgCode:oItem.orgCode,orgId:oItem.orgId,orgName:oItem.orgName,quoted:"N"};orgCateJournalsArr.push(orgCateObj)})}),$form.query(".orgCateJournals").take().value=orgCateJournalsArr}}}).catch(err=>{})};return{__sfc:!0,app,emitTabRemove,t,vendor,attrs,workflowStatus,$disabledFlag,customUpdateButton,viewUpdateButton,disabledUpdateButton,$disabledAdd,initButtonConfig,updateButtonConfig,schema,$back,$submits,$getorgCateJournalByFormId,scope:{app,t,$attrs:attrs,$disabledFlag,emitTabRemove,initButtonConfig,$back,supCommonApi,siteReviewModel,quaApi,$submits,$getorgCateJournalByFormId,$disabledAdd},components:{vendorAccessAttachment,CFillProgress,Printer},RenderEngine}}});var _sfc_render=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{schemaKey:"siteAssessmentDetail",pageAttrs:_vm.$attrs,schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const siteAssessmentDetail=__component__.exports;export{siteAssessmentDetail as s};
