import{N as NavTabs}from"./index-9a7f2446.js";import{ad as expression,ae as i18nExpression,af as yearMonthDaySelectorSegment,ah as generateXindexInOrder,cf as formGridSegment,ak as defineComponent,al as usePageHelper,am as useAttrs,an as ref$1,bY as computed,ar as RenderEngine,aq as defineSchemas,n as normalizeComponent}from"./index-6b6051d8.js";import{v as vendorAccessAttachment}from"./vendorAccessAttachment-04264be9.js";import{s as supCommonApi}from"./supApi-98b2a23d.js";import{c as siteReviewModel,q as quaApi}from"./vendorManagement-b6129894.js";import{C as CFillProgress}from"./index-2c71d18e.js";import{P as Printer}from"./printer-c24f03b5.js";import quaOfReviewDetail from"./quaOfReviewDetail-7ad28e52.js";/* empty css                                                                          *//* empty css                                              */import"./mixins-edc77a54.js";import"./MainHeader-2d842985.js";/* empty css                                                                   */import"./index-b99a582f.js";import"./TableView-eb18d7e8.js";import"./util-1e55288f.js";import"./index-d31c36cb.js";import"./drag-5571e5c7.js";import"./vendorProfileDetailRead-1f26c07e.js";import"./index-ba69fec0.js";import"./modelConfig-f4bd3e07.js";/* empty css                                                         *//* empty css                                                              */import"./index-baa5f2f5.js";/* empty css                                                              */import"./index-1e8533e5.js";import"./pick-b4f398be.js";import"./index.vue_vue_type_style_index_0_scoped_10b87ab8_lang-4ed993c7.js";import"./file-dynamic-7fc2d358.js";import"./file-dynamic.vue_vue_type_style_index_0_scoped_516fdfc3_lang-f2c1a82d.js";import"./tree-utils-7df6be59.js";import"./basicSetting-fc46a2d9.js";import"./BaseTableBind-b1f76fc9.js";import"./index-4d512f00.js";/* empty css                                                                      */import"./fileApi-05bbbbcc.js";import"./index-4aa0cc9f.js";import"./tableExtend-5e62371d.js";/* empty css                                                    */import"./vendorInfoChangeDetail-0bff0ae6.js";/* empty css                                                                               */import"./vue-treeselect.cjs-8e155c19.js";import"./noop-51892efa.js";/* empty css                       */import"./workflow-common-8e5f8543.js";import"./questManagementDetail-eacac8a8.js";import"./renderForm-74ecb667.js";/* empty css                                                                   *//* empty css                                                                              *//* empty css                                                                                */import"./VendorAccessSteps-c17f3cd8.js";import"./pay-plan-2bd9f833.js";import"./index-e416f1ab.js";/* empty css                                                 */import"./sourcingApplicationDetail-25c4086a.js";import"./index-830562a6.js";import"./composition-34efbd9d.js";import"./big-e21bdbb6.js";import"./enum-ea8c1af9.js";import"./index-be901e24.js";import"./index-46e21ee4.js";/* empty css                                                              */import"./datePickerOptions-40ce843f.js";import"./sourcingApplicationDetail-62d86f42.js";/* empty css                                                                  *//* empty css                                                                  */import"./tableDialog-d4aa1cc2.js";/* empty css                                                                          */const Steps={steps:{type:"void","x-decorator":"div","x-decorator-props":{class:"stepDiv"},"x-component":"Steps","x-component-props":{alignCenter:!0,finishStatus:"success"},"x-reactions":{dependencies:["approveStatus"],fulfill:{state:{"component[1].active":expression(`
            ['DRAFT', 'WITHDRAW', 'REJECTED'].includes($deps[0])
            ? 0
            : ['PUBLISH'].includes($deps[0])
            ? 1
            : ['SUBMITTED'].includes($deps[0])
            ? 2
            : ['APPROVED'].includes($deps[0])
            ? 3 : 0
          `)}}},properties:{step1:{type:"void","x-component":"el-step","x-component-props":{title:i18nExpression("common.fillOutReview")}},step2:{type:"void","x-component":"el-step","x-component-props":{title:i18nExpression("common.staffReview")}},step3:{type:"void","x-component":"el-step","x-component-props":{title:i18nExpression("common.reviewReport")}},step4:{type:"void","x-component":"el-step","x-component-props":{title:i18nExpression("vendorMod.relsultApproval")}}}}},siteAssessmentFormList={siteFormId:{type:"string","x-hidden":!0},createdId:{type:"string","x-hidden":!0},assessmentType:{type:"string",title:i18nExpression("vendorMod.siteType2"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"CEEA_ASSESSMENT_TYPE","@change":expression(`
          (val) => {
            $self.query('.vendorName').take().setValue('')
            if (val && val !== 'ACCESS_ASSESSMENT') {
              $self.query('.reviewFormId').take().setValue('') // 资质审查id
              // $self.query('.quaReviewType').take().setValue('')
              $self.query('.reviewFormNumber').take().setValue('') // 资质审查单号
            } else {

            }
          }
      `)},"x-reactions":expression(`(field) => {
      const state = $form.query('state').get('data')
      const approveStatus = $self.query('approveStatus').get('value')
      field.disabled = ['PUBLISH','SUBMITTED','APPROVED','WITHDRAW','REJECTED'].includes(approveStatus) || state.bolType != 1 || state.disVendorName
    }`),"x-validator":{required:!0}},vendorId:{type:"string","x-hidden":!0},vendorCode:{type:"string","x-hidden":!0},vendorName:{type:"string",title:i18nExpression("common.vendorName"),"x-decorator":"FormItem","x-component":"QuickSearchWrapper","x-component-props":{readPretty:"{{$form.readPretty}}",showKey:"companyName",propKey:"companyName",name:"scc_sup_company_info2","@close-quicksearch":expression(`(val) => {
          if (val) {
            console.log(val)
            $self.query('.vendorId').value = val.companyId
            $self.query('.vendorCode').value = val.companyCode
            $self.query('.vendorName').value = val.companyName

            const assessmentType = $self.query('assessmentType').get('value')
            // 根据供应商ID查询供应商档案组织与品类状态
            if (assessmentType && assessmentType != 'ACCESS_ASSESSMENT') {
              // this.getorgCateJournal(this.allParams.siteForm.vendorId)
              app.$http({
                url: '/api-sup/info/orgCategory/listPageOrgCategoryByParam',
                method: 'POST',
                data: { companyId: val.companyId , companyStatus: 'QUALIFIED', serviceStatus: 'QUALIFIED', pageNum: 1, pageSize: 999 },
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

            // 将地址带到下面
            let address = {
              country: val.companyCountry,
              addressDetail: val.companyAddress,
              city: val.companyCity,
              province: val.companyProvince
            }
            $form.query('.siteFormAddressList').take().value = address

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
      field.disabled = ['PUBLISH','SUBMITTED','APPROVED','WITHDRAW','REJECTED'].includes(approveStatus) || state.bolType != 1 || state.disVendorName || !assessmentType
    }`),"x-validator":{required:!0}},reviewFormId:{type:"number","x-hidden":!0},quaReviewType:{type:"string","x-hidden":!0},reviewFormNumber:{type:"string",title:i18nExpression("vendorMod.quaNum"),"x-decorator":"FormItem","x-component":"QuickSearchWrapper","x-component-props":{readPretty:"{{$form.readPretty}}",showKey:"reviewFormNumber",name:"scc_sup_auth_review_form","pre-query-data":expression("$form.query('state').get('data').reviewForm"),"@close-quicksearch":expression(`(val) => {
          let quaReviewType = val ? val.quaReviewType : ''
          $self.query('.quaReviewType').value = quaReviewType
          $self.query('.reviewFormNumber').value = val ? val.reviewFormNumber : ''
          $self.query('.reviewFormId').value = val ? val.reviewFormId : ''
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
                  fileId: '',
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
      const isQuaFormIdDisabled = true
      const state = $form.query('state').get('data')
      const assessmentType = field.query('.assessmentType').get('value')
      field.disabled = !isQuaFormIdDisabled || state.bolType != 1
      field.visible = (assessmentType == 'ACCESS_ASSESSMENT')
    }`),"x-validator":{required:!0}},siteFormNumber:{type:"string","x-decorator":"FormItem",title:i18nExpression("vendorMod.siteOrderInfoV"),"x-query-engine-skip":!0,"x-component-props":{disabled:!0}},approveStatus:{type:"string",title:i18nExpression("vendorMod.approveStatus"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"SUPPLIER_APPROVE_STATUS_TYPE",disabled:!0},"x-query-engine-skip":!0,"x-validator":{required:!0}},createdUserName:{type:"string","x-decorator":"FormItem",title:i18nExpression("common.creator"),"x-query-engine-skip":!0,"x-component-props":{disabled:!0}},creationDate:{...yearMonthDaySelectorSegment,"x-decorator":"FormItem",title:i18nExpression("common.creationTime"),"x-query-engine-skip":!0,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],disabled:!0}},reviewModelId:{type:"string",title:i18nExpression("dataConfMod.templateName"),"x-decorator":"FormItem","x-component":"Select","x-component-props":{disabled:expression("$form.query('state').get('data').bolType != 1 || false"),"@change":expression(`(val) => {
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
      }`)]},siteReviewPlanId:{type:"string","x-hidden":!0},planName:{type:"string",title:i18nExpression("vendorMod.planName3"),"x-decorator":"FormItem","x-component":"QuickSearchWrapper","x-component-props":{readPretty:"{{$form.readPretty}}",showKey:"planName",propKey:"planName",disabled:expression("$form.query('state').get('data').bolType != 1 || false"),name:"scc_sup_site_review_plan","@close-quicksearch":expression(`(val, scope) => {
        $self.query('.siteReviewPlanId').take().setValue(val.siteReviewPlanId)
        if (!val) {
          return false
        }
        let id = val.siteReviewPlanId
        siteReviewModel.address(id).then(res => {
          const addressList = res.data.addressList || []
          const personList = res.data.personList || []
          let siteFormAddressList = $form.query('.siteFormAddressList').take()
          console.log(res.data)

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
      }`)},"x-validator":{required:!0}},supplierViewFlag:{type:"string","x-decorator":"FormItem",title:i18nExpression("vendorMod.supplierViewFlag"),"x-component":"DictSelect","x-component-props":{disabled:expression("$form.query('state').get('data').bolType != 1 || false"),code:"YES_OR_NO"},"x-validator":{required:!0}},siteFormExplain:{type:"string","x-decorator":"FormItem",title:i18nExpression("vendorMod.siteExplainV"),"x-component-props":{type:"textarea"},"x-decorator-props":{gridSpan:2},"x-reactions":expression(`(field) => {
      const state = $form.query('state').get('data')
      field.disabled = state.bolType != 1
    }`)}},siteFormHistoryList={historyTableAll:{type:"void","x-decorator":"QueryEngine","x-query-engine":{service:"sup",type:"SiteFormVendor",actions:{paginationQuery:{immediate:!0,ready:expression(`() => {
            return !!$attrs.params.row.vendorName || $form.query('.vendorName').take().value
          }`),transformRequest:expression(`(data, headers) => {
            data.payload.filter = {
              "vendorName": {
                eq: $attrs.params.row.vendorName || $form.query('.vendorName').take().value
              }
            }
            return data
          }`)}}},properties:{historyTable:{type:"array","x-component":"RenderTable","x-component-props":{maxHeight:400,preColumns:"seq",class:"table-view-vxe-table",openCustomTable:!1},properties:generateXindexInOrder({approveStatus:{type:"string",title:"{{$t('vendorMod.orderStatus')}}","x-component":"DictSelect","x-component-props":{code:"SUPPLIER_APPROVE_STATUS_TYPE"},"x-render-table-column":{minWidth:90}},siteFormNumber:{type:"string",title:"{{$t('vendorMod.siteFormNumber')}}","x-render-table-column":{minWidth:120}},assessmentType:{type:"string",title:"{{$t('vendorMod.assessmentType')}}","x-component":"DictSelect","x-component-props":{code:"CEEA_ASSESSMENT_TYPE"},"x-render-table-column":{minWidth:150}},reviewFormNumber:{type:"string",title:"{{$t('vendorMod.reviewFormNumber2')}}","x-render-table-column":{minWidth:150}},vendorName:{type:"string",title:"{{$t('vendorMod.vendorName')}}","x-render-table-column":{minWidth:100}},siteAdress:{type:"string",title:"{{$t('vendorMod.siteAdress')}}","x-render-table-column":{minWidth:140}},createdFullName:{type:"string",title:"{{$t('vendorMod.createdFullName')}}","x-render-table-column":{minWidth:110}},creationDate:{...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.creationDate, '{y}-{m}-{d}')
              }`)},title:"{{$t('vendorMod.creationDate2')}}","x-render-table-column":{width:130}},reviewResult:{type:"string",title:"{{$t('vendorMod.reviewResult')}}","x-component":"DictSelect","x-component-props":{code:"CEEA_RESULT_TYPE"},"x-render-table-column":{minWidth:150}}})}}}},authBaseInfo={toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},"x-reactions":expression(`(field) => {
      field.visible = !$form.readPretty
    }`),properties:{add:{type:"void",title:i18nExpression("common.add"),"x-component-props":{disabled:expression("$disabledAdd($form)"),type:"primary","@click":expression(`() => {
           $self.query('siteFormAddressList')
             .take(field => {
               field.componentProps.componentInstance.addRow()
           })
          }`)}}}},siteFormAddressList:{type:"array","x-component":"RenderTable","x-component-props":{editMode:!0,maxHeight:400,pagination:!1,sortable:!1},"x-query-engine-skip":!0,properties:generateXindexInOrder({authFlag:{type:"string",title:i18nExpression("vendorMod.authFlag"),"x-render-table-column":{minWidth:150},"x-component":"Checkbox","x-component-props":{trueLabel:"Y",falseLabel:"N",disabled:expression("$disabledAdd($form)")}},country:{type:"string",title:i18nExpression("components.address.country"),"x-render-table-column":{minWidth:120},"x-component":"DictSelect","x-component-props":{code:"country",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$disabledAdd($form)")}},province:{type:"string",title:i18nExpression("components.address.area"),"x-render-table-column":{minWidth:120},"x-component":"DictSelect","x-component-props":{code:"PROVINCE","custom-select-type":"PROVINCE",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$disabledAdd($form) || $table.getRowByIndex($self.index).country!='CN'")}},city:{type:"string",title:i18nExpression("components.address.city"),"x-render-table-column":{minWidth:120},"x-component":"DictSelect","x-component-props":{code:expression("$table.getRowByIndex($self.index).province"),"custom-select-type":"CITY",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$disabledAdd($form) || $table.getRowByIndex($self.index).country!='CN'")}},addressDetail:{type:"string",title:i18nExpression("components.address.detailAddress"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$disabledAdd($form)")}},postCode:{type:"string",title:i18nExpression("components.address.postalCode"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$disabledAdd($form)")}},siteComment:{type:"string",title:i18nExpression("components.address.remark"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$disabledAdd($form)")}},enableFlag:{type:"string",title:i18nExpression("vendorMod.enableFlag"),"x-render-table-column":{minWidth:120},"x-component":"Checkbox","x-component-props":{"true-label":"true","false-label":"false",disabled:expression("$disabledAdd($form)")}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{disabled:expression("$disabledAdd($form)"),type:"text","@click":expression(`({ row }) => {
                  $table.remove($self.index)
              }`)}}}}})}},authOrganization={orgCateJournals:{type:"array","x-component":"RenderTable","x-component-props":{maxHeight:400,pagination:!1,sortable:!1},"x-query-engine-skip":!0,properties:generateXindexInOrder({quoted:{type:"string",title:i18nExpression(" "),"x-render-table-column":{width:45},"x-component":"Checkbox","x-component-props":{trueLabel:"Y",falseLabel:"N",disabled:expression("$disabledAdd($form)")}},orgName:{type:"string",title:i18nExpression("vendorMod.orgName2"),"x-render-table-column":{minWidth:200}},categoryName:{type:"string",title:i18nExpression("vendorMod.categoryName"),"x-render-table-column":{minWidth:200}}})}},siteFormPersonList={toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},"x-reactions":expression(`(field) => {
      field.visible = !$form.readPretty
    }`),properties:{}},siteFormPersonList:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,maxHeight:400,pagination:!1,sortable:!1},"x-query-engine-skip":!0,properties:generateXindexInOrder({personType:{type:"string",title:i18nExpression("vendorMod.personType"),"x-render-table-column":{minWidth:120},"x-component":"DictSelect","x-component-props":{code:"PERSON_TYPE",disabled:expression("$disabledAdd($form)")}},userAccount:{type:"string",title:i18nExpression("vendorMod.userAccount"),"x-render-table-column":{minWidth:120},"x-component":"QuickSearchWrapper","x-component-props":{name:expression("$table.getRowByIndex($self.index).personType == 'GRAND_JURY' ? 'scc_rbac_user_vendor_display' : 'scc_rbac_user_display'"),showKey:"username",disabled:expression("$disabledAdd($form)"),"@close-quicksearch":expression(`(val) => {
              $table.getRowByIndex($self.index).userAccount = val ? val.username : ''
              $table.getRowByIndex($self.index).userName = val ? val.nickname : ''
              $table.getRowByIndex($self.index).userId = val ? val.userId : ''
              $table.getRowByIndex($self.index).userTel = val ? val.phone : ''
              $table.getRowByIndex($self.index).userEmail = val ? val.email : ''
           }`)}},userName:{type:"string",title:i18nExpression("vendorMod.userName2"),"x-render-table-column":{minWidth:100},"x-read-pretty":!0},userTel:{type:"string",title:i18nExpression("vendorMod.mobilePhone"),"x-render-table-column":{minWidth:100},"x-read-pretty":!0},userEmail:{type:"string",title:i18nExpression("vendorMod.emailAddress"),"x-render-table-column":{minWidth:100},"x-read-pretty":!0},userPost:{type:"string",title:i18nExpression("bidMod.position"),"x-render-table-column":{minWidth:100},"x-component-props":{disabled:expression("$disabledAdd($form)")}},reviewModelId:{type:"string",title:i18nExpression("vendorMod.reviewModelId"),"x-render-table-column":{minWidth:100},"x-component-props":{disabled:expression("$disabledAdd($form)")},"x-component":"Select","x-reactions":[expression(`(field) => {
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
          }`)]},enableFlag:{type:"string",title:i18nExpression("vendorMod.enableFlag"),"x-render-table-column":{minWidth:120},"x-component":"Checkbox","x-component-props":{"true-label":"true","false-label":"false",disabled:expression("$disabledAdd($form)")}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{disabled:expression("$disabledAdd($form)"),type:"text","@click":expression(`({ row }) => {
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
      }`)},properties:{printer:{type:"array","x-component":"Printer","x-component-props":{editableTabs:expression("$form.query('state').get('data').editableTabs"),"read-only":"false",tableData:expression("$form.query('state').get('data').tableData")},"x-query-engine-skip":!0}}},certificationResultForm:{type:"void",...formGridSegment,properties:{reviewResult:{type:"string",title:i18nExpression("vendorMod.finalResult"),"x-decorator":"FormItem","x-component":"DictSelect","x-decorator-props":{gridSpan:1},"x-component-props":{code:"CEEA_RESULT_TYPE"},"x-reactions":expression(`() => {
            const state = $form.query('state').get('data')
            const approveStatus = $form.query('.approveStatus').get('value')
            const accountNameId = app.$store.getters.userId
            const createdId = $form.query('.createdId').take().value
            if ((state.bolType == 2 && accountNameId != createdId) || state.bolType == 4 || approveStatus == 'APPROVED'){
              $self.disabled = true
            } else {
              $self.disabled = false
            }
          }`)},ceeaResultExplain:{type:"string",title:i18nExpression("vendorMod.resultExplain"),"x-decorator":"FormItem","x-decorator-props":{gridSpan:2},"x-component-props":{type:"textarea",rows:"2"},"x-reactions":expression(`() => {
            const state = $form.query('state').get('data')
            const approveStatus = $form.query('.approveStatus').get('value')
            const accountNameId = app.$store.getters.userId
            const createdId = $form.query('.createdId').take().value
            if ((state.bolType == 2 && accountNameId != createdId) || state.bolType == 4 || approveStatus == 'APPROVED'){
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
            }`),properties:{...siteAssessmentFormList}}}},siteFormHistoryList:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.historySiteAssessment")},"x-query-engine-skip":!0,properties:{...siteFormHistoryList}},authBaseInfo:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.authBaseInfo")},"x-query-engine-skip":!0,properties:{...authBaseInfo}},authOrganization:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.authOrganization")},"x-query-engine-skip":!0,properties:{...authOrganization}},workingGroupStaff:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.workingGroupStaff")},"x-query-engine-skip":!0,properties:{...siteFormPersonList}},attachment:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.attachment")},"x-query-engine-skip":!0,properties:{fileList:{type:"array","x-query-engine-skip":!0,"x-component":"vendorAccessAttachment","x-component-props":{"sence-code":"QUA",attOpt:"view","up-file-info":{uploadType:"DEF",sourceType:"WEB_APP",fileModular:"sup",fileFunction:"siteAssessment",fileType:"images"}}}}},certificationResult:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.certificationResult")},"x-query-engine-skip":!0,properties:{...results}}})},_sfc_main$2=defineComponent({__name:"siteAssessmentDetailEngine",setup(__props){const{app,emitTabRemove,t,vendor}=usePageHelper(),attrs=useAttrs(),workflowStatus=ref$1("DRAFT"),$disabledFlag=computed(()=>!0),customUpdateButton=computed(()=>!$disabledFlag.value&&["SUPPLIER_SUBMITTED"].includes(workflowStatus.value)),viewUpdateButton=$form=>$form.query(".SchemaWorkflow").take().componentProps.componentInstance.workflowParamsInfo.integrationMode!=="None"?attrs.params.flag!="view":attrs.params.flag!="view"?!0:attrs.params.flag=="view"&&$form.query(".approveStatus").take().value=="PUBLISH",disabledUpdateButton=()=>!(attrs.params.flag=="view"),$disabledAdd=$form=>{const states=$form.query("state").get("data");return states.addressReadOnly||states.bolType==4||["PUBLISH","SUBMITTED","APPROVED"].includes(states.orderStatus)},initButtonConfig=$form=>{setTimeout(()=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.buttonConfigInfo.save.view=viewUpdateButton($form),componentInstance.buttonConfigInfo.submit.view=viewUpdateButton($form),componentInstance.buttonConfigInfo.cancel.view=!0,componentInstance.buttonConfigInfo.close.view=!1;const reviewResult=$form.query(".reviewResult").take().value;(!reviewResult||reviewResult=="")&&(componentInstance.buttonConfigInfo.save.name=t("common.staging"),componentInstance.buttonConfigInfo.submit.name=t("common.submit"))},50)},updateButtonConfig=$form=>{setTimeout(()=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.buttonConfigInfo.save.view=viewUpdateButton($form),componentInstance.buttonConfigInfo.submit.view=viewUpdateButton($form),componentInstance.buttonConfigInfo.cancel.view=!1,componentInstance.buttonConfigInfo.close.view=!1},50)},schema=defineSchemas({state:{type:"void","x-component":"Fragment","x-hidden":!0,"x-data":{bolType:1,disVendorName:!1,reviewForm:{"t.vendor_id":"","t.approve_status":"APPROVED","t.CEEA_IF_VENDOR_AUTH":"Y"},addressReadOnly:!1,modelList:[],editableTabs:[],tableData:[],fileRecordId:0}},SiteFormVendor:{type:"void","x-decorator":"QueryEngine","x-component":"el-container","x-component-props":{class:"flex-container siteAssessment",direction:"vertical"},"x-query-engine":{service:"sup",actions:{query:{immediate:!0,ready:expression(`() => {
            initButtonConfig($form)
            return $attrs.params.flag != 'add' && $attrs.params.row.siteFormId
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

            $form.readPretty = true

            let datas = ress.data.ref.SiteFormVendor[$attrs.params.row.siteFormId]
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
            $form.query('.orgCateJournals').take().setValue(tableOrgCateJournals)
            $form.query('.siteFormAddressList').take().setValue(tableSiteFormAddressList)
            $form.query('.fileList').take().setValue(tableFileList)

            // 确定是那个评审状态
              let cMen = 0// 是否创建人
              let pMen = 0// 是否评审人
              const accountId = app.$store.getters.userId
              const createdId = $form.query('.createdId').take().value
              if (accountId == createdId) {
                cMen = 1
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
                  authResultBol == 1
                ) {
                  states.bolType = 3
                }
              } else {
                states.bolType = 4
              }

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
           } }}`}}}}}),$back=$bus=>{emitTabRemove(attrs.tabName),$bus.$emit("siteA")},$submits=(type,$form,$queryEngine,$message,$t,$bus)=>{let values=$form.values;const tablefileList=$form.query(".fileList").take().value,tablefileRecords=$form.query(".fileRecords").get("value"),tableorgCateJournals=$form.query(".orgCateJournals").get("value"),tablesiteFormAddressList=$form.query(".siteFormAddressList").get("value"),tablesiteFormPersonList=$form.query(".siteFormPersonList").get("value");values.fileList=tablefileList,values.fileRecords=tablefileRecords,values.orgCateJournals=tableorgCateJournals,values.siteFormAddressList=tablesiteFormAddressList,values.siteFormPersonList=tablesiteFormPersonList,attrs.params.row?.siteFormId;const approveStatus=$form.query(".approveStatus").take().value;if(type=="SAVE")[null,"DRAFT"].includes(approveStatus)?(values.approveStatus="DRAFT",$queryEngine.request.save(values,{query:{"*":{},siteFormPersonList:{"*":{}},siteFormAddressList:{"*":{}},fileRecords:{"*":{}},fileList:{"*":{}},orgCateJournals:{"*":{}}}}).then(()=>{$message.success($t("common.successSave")),$bus.$emit("siteA"),emitTabRemove(attrs.tabName)}).catch(err=>{})):$queryEngine.request.save(values,{query:{"*":{},siteFormPersonList:{"*":{}},siteFormAddressList:{"*":{}},fileRecords:{"*":{}},fileList:{"*":{}},orgCateJournals:{"*":{}}}}).then(()=>{$message.success($t("common.successSave")),$bus.$emit("siteA"),emitTabRemove(attrs.tabName)}).catch(err=>{});else{const reviewResult=$form.query(".reviewResult").take().value;!reviewResult||reviewResult==""?(values.approveStatus="PUBLISH",$queryEngine.request.save(values,{query:{"*":{}},siteFormPersonList:{"*":{}},siteFormAddressList:{"*":{}},fileRecords:{"*":{}},fileList:{"*":{}},orgCateJournals:{"*":{}}}).then(()=>{$message.success($t("common.successSubmit")),$bus.$emit("siteA"),emitTabRemove(attrs.tabName)})):$queryEngine.request.save(values,{query:{"*":{},siteFormPersonList:{"*":{}},siteFormAddressList:{"*":{}},fileRecords:{"*":{}},fileList:{"*":{}},orgCateJournals:{"*":{}}}}).then(res=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.setWorkflowBusinessId(res.data[0]?.planConfirmId||null),componentInstance.setWorkflowTabDisabled(!0),componentInstance.setWorkflowBusinessVariables({}),componentInstance.handlerAfter(type.toUpperCase(),()=>{$bus.$emit("siteA"),emitTabRemove(attrs.tabName)})})}},$getorgCateJournalByFormId=(reviewFormId,isAdd=!1,$form)=>{app.$http({url:"/api-sup/review/reviewForm/listOrgAndCategoryByReviewId",method:"GET",params:{reviewFormId},loading:!0}).then(res=>{if(res.data){const orgData=res.data.orgInfos,catData=res.data.orgCategorys;if(isAdd){let orgCateJournalsArr=[];orgData.forEach(oItem=>{catData.forEach(cItem=>{let orgCateObj={categoryCode:cItem.categoryCode,categoryFullName:cItem.categoryFullName,categoryId:cItem.categoryId,categoryName:cItem.categoryName,orgCode:oItem.orgCode,orgId:oItem.orgId,orgName:oItem.orgName,quoted:"N"};orgCateJournalsArr.push(orgCateObj)})}),$form.query(".orgCateJournals").take().value=orgCateJournalsArr}}}).catch(err=>{})};return{__sfc:!0,app,emitTabRemove,t,vendor,attrs,workflowStatus,$disabledFlag,customUpdateButton,viewUpdateButton,disabledUpdateButton,$disabledAdd,initButtonConfig,updateButtonConfig,schema,$back,$submits,$getorgCateJournalByFormId,scope:{app,t,$attrs:attrs,$disabledFlag,emitTabRemove,initButtonConfig,$back,supCommonApi,siteReviewModel,quaApi,$submits,$getorgCateJournalByFormId,$disabledAdd},components:{vendorAccessAttachment,CFillProgress,Printer},RenderEngine}}});var _sfc_render$2=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{schemaKey:"siteAssessmentDetail",pageAttrs:_vm.$attrs,schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns$2=[],__component__$2=normalizeComponent(_sfc_main$2,_sfc_render$2,_sfc_staticRenderFns$2,!1,null,null,null,null);const siteAssessmentDetail=__component__$2.exports,_sfc_main$1=defineComponent({__name:"siteAssessmentListEngine",setup(__props){const{emitTabAdd,app}=usePageHelper(),schema=defineSchemas({SiteFormVendor:{type:"void","x-query-engine":{service:"sup",actions:{paginationQuery:{immediate:!0}}},"x-decorator":"QueryEngine","x-component":"el-container","x-component-props":{class:"flex-container the_contractTemplateList_wrapper",direction:"vertical"},properties:{bus:{type:"void","x-component":"BusEvent","x-component-props":{eventName:"siteA","@listener":expression(`() => {
            $queryEngine.state.paginationManagement.refresh()
          }`)}},query:{type:"object","x-query-engine-skip":!0,"x-component":"QueryFormByQueryEngine",properties:generateXindexInOrder({siteFormNumber:{type:"string",title:"{{$t('vendorMod.siteOrderInfoV')}}"},reviewFormNumber:{type:"string",title:"{{$t('vendorMod.quaNum')}}"},approveStatus:{type:"string",title:"{{$t('vendorMod.orderStatus')}}","x-component":"DictSelect","x-component-props":{code:"SUPPLIER_APPROVE_STATUS_TYPE"}},assessmentType:{type:"string",title:"{{$t('vendorMod.siteTypeV')}}","x-component":"DictSelect","x-component-props":{code:"CEEA_ASSESSMENT_TYPE"}},reviewResult:{type:"string",title:"{{$t('vendorMod.certificationResult')}}","x-component":"DictSelect","x-component-props":{code:"CEEA_RESULT_TYPE"}},vendorName:{type:"string",title:"{{$t('common.vendorName')}}","x-component":"QuickSearchWrapper","x-component-props":{showKey:"companyName",propKey:"companyName",name:"scc_sup_company_info_all"}}})},table:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",class:"table-view-vxe-table",openCustomTable:!1},properties:generateXindexInOrder({approveStatus:{type:"string",title:"{{$t('vendorMod.orderStatus')}}","x-component":"DictSelect","x-component-props":{code:"SUPPLIER_APPROVE_STATUS_TYPE"},"x-render-table-column":{minWidth:90}},vendorCode:{type:"string",title:"{{$t('common.vendorCode')}}","x-render-table-column":{minWidth:120}},vendorName:{type:"string",title:"{{$t('vendorMod.vendorName')}}","x-render-table-column":{minWidth:150}},siteFormNumber:{type:"string","x-component":"TableButton","x-component-props":{type:"text","@click":expression(`({row}) => {
                let tab = {
                  component: siteAssessmentDetail,
                  params: {
                    flag: 'view',
                    row: row,
                    siteFormId: row.siteFormId,
                    tabName: 'siteAssessmentDetail' + row.siteFormNumber || row.siteFormId
                  },
                  title: row.siteFormNumber,
                  name: 'siteAssessmentDetail' + row.siteFormNumber || row.siteFormId
                }
                emitTabAdd(tab)
              }`)},"x-render-table-column":{title:"{{$t('vendorMod.siteOrderInfoV')}}",minWidth:140,customRender:!0}},siteFormId:{type:"string","x-hidden":!0},assessmentType:{type:"string",title:"{{$t('vendorMod.siteTypeV')}}","x-component":"DictSelect","x-component-props":{code:"CEEA_ASSESSMENT_TYPE"},"x-render-table-column":{minWidth:150}},reviewResult:{type:"string",title:"{{$t('vendorMod.certificationResult')}}","x-component":"DictSelect","x-component-props":{code:"CEEA_RESULT_TYPE"},"x-render-table-column":{minWidth:100}},createdFullName:{type:"string",title:"{{$t('common.creator')}}","x-render-table-column":{minWidth:110}},creationDate:{title:"{{$t('vendorMod.creationDate2')}}",...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.creationDate, '{y}-{m}-{d}')
              }`)},"x-render-table-column":{width:130}},lastUpdateDate:{type:"string","x-hidden":!0,"x-query-engine-sort":"desc"}})}}}});return{__sfc:!0,emitTabAdd,app,schema,scope:{emitTabAdd,siteAssessmentDetail,quaOfReviewDetail,app},components:{},RenderEngine}}});var _sfc_render$1=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{staticClass:"contractPaymentType",attrs:{schemaKey:"siteAssessmentListVendor",schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns$1=[],__component__$1=normalizeComponent(_sfc_main$1,_sfc_render$1,_sfc_staticRenderFns$1,!1,null,null,null,null);const SiteAssessmentListEngine=__component__$1.exports,_sfc_main={name:"SiteAssessment",components:{NavTabs},data(){return{activeTab:"SiteAssessmentListEngine",tabs:[{title:this.$t("route.siteAssessmentV"),name:"SiteAssessmentListEngine",component:SiteAssessmentListEngine,closable:!1}]}}};var _sfc_render=function(){var _vm=this,_c=_vm._self._c;return _c("NavTabs",{ref:"tabs",attrs:{"tabs-list":_vm.tabs,"cur-tab":_vm.activeTab}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const index=__component__.exports;export{index as default};
