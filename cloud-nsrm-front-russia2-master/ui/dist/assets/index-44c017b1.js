import{N as NavTabs}from"./index-9a7f2446.js";import{ak as defineComponent,al as usePageHelper,aq as defineSchemas,ad as expression,ah as generateXindexInOrder,ae as i18nExpression,b$ as dataTimeSelectorSegment,bS as exportExcelSegment,bT as queryFieldStatePropertyExpression,af as yearMonthDaySelectorSegment,bD as changeFieldVisibleByDeps,ca as buttonListItemVisibleByPermission,aD as requiredValidatorSegment,ar as RenderEngine,n as normalizeComponent}from"./index-6b6051d8.js";import vendorProfileDetailReadEngine from"./vendorProfileDetailReadEngine-a7dbbbbe.js";import quaOfReviewDetail from"./quaOfReviewDetail-7144db3b.js";import"./vendorProfileDetailReadEngine.vue_vue_type_style_index_0_lang-d2c087b5.js";import"./index-baa5f2f5.js";/* empty css                                                              */import"./file-dynamic-25a093c4.js";import"./file-dynamic-7fc2d358.js";import"./MainHeader-2d842985.js";/* empty css                                                                   */import"./file-dynamic.vue_vue_type_style_index_0_scoped_516fdfc3_lang-f2c1a82d.js";import"./tree-utils-7df6be59.js";import"./basicSetting-fc46a2d9.js";import"./BaseTableBind-b1f76fc9.js";import"./util-1e55288f.js";import"./index-4d512f00.js";/* empty css                                                                      */import"./fileApi-05bbbbcc.js";import"./index-2c71d18e.js";/* empty css                                              */import"./edit-bd7f1b0f.js";import"./edit.vue_vue_type_style_index_0_lang-d3e478c4.js";import"./supApi-98b2a23d.js";import"./vendorManagement-96246714.js";import"./util-d962b17f.js";import"./detail-372c31a6.js";import"./vendorManagement-b6129894.js";import"./TableView-eb18d7e8.js";import"./index-d31c36cb.js";import"./drag-5571e5c7.js";import"./VendorAccessSteps-c17f3cd8.js";import"./sourcingApplicationDetail-25c4086a.js";import"./index-830562a6.js";import"./composition-34efbd9d.js";import"./big-e21bdbb6.js";import"./enum-ea8c1af9.js";import"./index-be901e24.js";import"./index-46e21ee4.js";import"./index-e416f1ab.js";/* empty css                                                              */import"./mixins-edc77a54.js";import"./datePickerOptions-40ce843f.js";import"./sourcingApplicationDetail-62d86f42.js";/* empty css                                                                  *//* empty css                                                                  */import"./categorySelect-fd2521a2.js";const _sfc_main$1=defineComponent({__name:"vendorProfileListEngine",setup(__props){const{emitTabAdd,app}=usePageHelper(),schema=defineSchemas({state:{"x-data":{companyId:null}},CompanyInfo:{type:"void","x-decorator":"el-container","x-decorator-props":{class:"flex-container the_contractTemplateList_wrapper",direction:"vertical"},"x-component":"QueryEngine","x-component-props":{"@activated":expression(`() => {
        app.$nextTick(() => {
          $form.query('query').take(field => {
            const { from, funName, formId, formNo, taskIndex, row } = app.$route.params
            // 区分供应商注册和附件审批待办跳转
            if (from === 'fromFun' && funName === 'vendorProfile') {
              let flag = 'view'
              if (row.configCode === 'ONESELF_REGISTER_SUBMITTED') {
                // configCode: "ONESELF_REGISTER_SUBMITTED"是供应商注册
                flag = 'passRegister'
              } else {
                // configCode: "GYSKTBBH"是附件审批
                flag = taskIndex === 1 ? 'startFileApproval' : 'view'
              }
              // 待办跳转
              let tab = {
                component: vendorProfileDetailReadEngine,
                params: {
                  flag,
                  companyId: formId,
                  tabName: 'vendorProfileDetailReadEngine' + formNo
                },
                title: formNo,
                name: 'vendorProfileDetailReadEngine' + formNo
              }
              emitTabAdd(tab)
            }
          })
        })
      }`)},"x-query-engine":{service:"sup",actions:{queryPotentialSupplier:{immediate:!0,method:"paginationQuery",transformRequest:expression(`(data, header) => {
            if (data.payload?.filter) {
              const {
                abnormalDirectory,
                registeredCapitalEnd,
                registeredCapital,
                ...otherFilter
              } = data.payload.filter
              if (abnormalDirectory) {
                data.payload.filter = {
                  ...otherFilter,
                  '$or': {
                    isBacklist: 'Y',
                    keySupervisionFlag: 'Y',
                    categoryLimitFlag: 'Y',
                    timeLimitFlag: 'Y',
                    positionLimitFlag: 'Y'
                  }
                }
              }
              if (registeredCapital && registeredCapitalEnd) {
                const from = registeredCapital.gt
                const to = registeredCapitalEnd.eq
                delete data.payload.filter.registeredCapital
                delete data.payload.filter.registeredCapitalEnd
                data.payload.filter.registeredCapital = {
                  between: [from, to]
                }
              } else if (registeredCapitalEnd) {
                const to = registeredCapitalEnd.eq
                delete data.payload.filter.registeredCapitalEnd
                data.payload.filter.registeredCapital = {
                  le: to
                }
              }
            }
            return data
          }`),onSuccess:expression(`async (res) => {
            const queryTodoRes = await app.$api.base.flowAPI.queryTodo()
            let queryTodoList = queryTodoRes.data || []
            $form.values.table = res.data.map(item => {
              let obj = queryTodoList.find(todoItem => item.companyId + '' === todoItem.businessId + '')
              return { ...item, isApprover: obj ? 'Y' : 'N' }
            })
          }`)},approve:{autoFormatResult:!1},reject:{autoFormatResult:!1}}},properties:{bus:{type:"void","x-component":"BusEvent","x-component-props":{eventName:"green","@listener":expression(`() => {
            $queryEngine.state.paginationManagement.refresh()
          }`)}},query:{type:"object","x-query-engine-skip":!0,"x-component":"QueryFormByQueryEngine","x-component-props":{immediateQueryForm:!0},properties:generateXindexInOrder({companyName:{type:"string","x-query-engine-query-operator":"contains",title:i18nExpression("common.vendorName")},companyCode:{type:"string","x-query-engine-query-operator":"contains",title:i18nExpression("common.vendorCode")},contactName:{type:"string",title:i18nExpression("cusEntry.vendorMod.contactName"),"x-query-engine-query-operator":"contains","x-query-engine-relation-strict":!0,"x-query-engine-relation":"contactInfos"},ceeaContactMethod:{type:"string",title:i18nExpression("cusEntry.vendorMod.contactPhone"),"x-query-engine-query-operator":"contains","x-query-engine-relation-strict":!0,"x-query-engine-relation":"contactInfos"},lcCode:{type:"string",title:i18nExpression("vendorMod.lcCode"),"x-query-engine-query-operator":"contains"},isBacklist:{type:"string",title:i18nExpression("vendorMod.isBacklist"),"x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"}},overseasRelation:{type:"string",title:i18nExpression("vendorMod.overseasRelation"),"x-component":"DictSelect","x-component-props":{code:"RELATION"}},companyType:{type:"string",title:i18nExpression("vendorMod.companyType"),"x-component":"DictSelect","x-component-props":{code:"COMPANY_NATURE_NEW"}},pjCompanyStatus:{type:"string",title:i18nExpression("cusEntry.vendorMod.companyStatus"),"x-component":"DictSelect","x-component-props":{code:"PJ_COMPANY_STATUS"}},dataSources:{type:"string",title:i18nExpression("vendorMod.dataSources"),"x-component":"DictSelect","x-component-props":{code:"DATA_SOURCE"}},status:{title:i18nExpression("vendorMod.registerStatus"),"x-component":"DictSelect","x-component-props":{code:"SUPPLIER_LIST_STATUS_vendorProfileList"}},domesticAndForeignRelations:{type:"string",title:i18nExpression("cusEntry.vendorMod.domesticAndForeignRelations"),"x-component":"DictSelect","x-component-props":{code:"RELATION_NEW"}},approvedDate:{title:i18nExpression("cusEntry.vendorMod.enterDate"),...dataTimeSelectorSegment,"x-query-engine-query-operator":"between"},companyCreationDate:{title:i18nExpression("cusEntry.vendorMod.creationDate"),...dataTimeSelectorSegment,"x-query-engine-query-operator":"between"},categoryId:{type:"string",title:i18nExpression("vendorMod.categoryName"),"x-component":"QuickSearchWrapper","x-query-engine-query-operator":"eq","x-component-props":{showKey:"categoryName",name:"scc_base_purchase_category4"},"x-query-engine-relation-strict":!0,"x-query-engine-relation":"cateJournalList"},keySupervisionFlag:{type:"string",title:i18nExpression("cusEntry.vendorMod.isKeySupervision"),"x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"}},registCurrency:{type:"string",title:i18nExpression("cusEntry.vendorMod.registeredCurrency"),"x-component":"DictSelect","x-component-props":{code:"currency"}},registeredCapital:{type:"string",title:i18nExpression("cusEntry.vendorMod.registeredAmountFrom"),"x-query-engine-query-operator":"gt"},registeredCapitalEnd:{type:"string",title:i18nExpression("cusEntry.vendorMod.registeredAmountEnd")},infoCompleteFlag:{type:"string",title:i18nExpression("cusEntry.vendorMod.infoCompleteFlag"),"x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"}},companyAddress:{type:"string",title:i18nExpression("cusEntry.vendorMod.address"),"x-query-engine-query-operator":"contains"},extLegalOpinion:{type:"string",title:i18nExpression("cusEntry.supplement20250218.lgNodePassed"),"x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"}},extFinancialOpinion:{type:"string",title:i18nExpression("cusEntry.supplement20250218.fdNodePassed"),"x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"}},extSecurityAndRiskOpinion:{type:"string",title:i18nExpression("cusEntry.supplement20250218.secNodePassed"),"x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"}},abnormalDirectory:{type:"string","x-hidden":!0},potentialSuppliers:{type:"string","x-hidden":!0}})},toolbar:{type:"void","x-component":"Space","x-component-props":{style:"margin-bottom: 16px"},properties:{exportExcel:{type:"void","x-component":"ExportExcel","x-component-props":{...exportExcelSegment,type:"default",pageUrl:"/api-sup/api-ql/CompanyInfo/query",tableHeader:queryFieldStatePropertyExpression("CompanyInfo.table","data.columns"),dictCodes:{extIsQualifiedFileUpload:"YES_OR_NO",extIsQualifiedStatus:"QUALIFIED_UPLOAD_STATUS",isBacklist:"YES_OR_NO",keySupervisionFlag:"YES_OR_NO",status:"SUPPLIER_LIST_STATUS",dataSources:"DATA_SOURCE",positionLimitFlag:"YES_OR_NO",categoryLimitFlag:"YES_OR_NO",timeLimitFlag:"YES_OR_NO",supplierType:"SUPPLIER_TYPE",infoCompleteFlag:"YES_OR_NO"}}},buttonGroup:{type:"void","x-component":"el-button-group",properties:{all:{type:"void","x-component":"Button","x-content":i18nExpression("cusEntry.common.all"),"x-component-props":{"@click":expression(`() => {
                    $form.query('query').take().invoke('resetQuery')
                  }`)}},potentialSuppliers:{type:"void","x-component":"Button","x-content":i18nExpression("cusEntry.common.potentialSuppliers"),"x-component-props":{"@click":expression(`() => {
                    $form.query('.query').get('value').abnormalDirectory = null
                    $form.query('.query').get('value').potentialSuppliers = null
                    $form.query('.query').get('value').status = null
                    $form.query('.query').get('value').pjCompanyStatus = 'POTENTIAL_SUPPLIER'
                    $form.query('.query').get('value').dataSources = null
                    $form.query('.query').take().invoke('query')
                  }`)}},quasiSupplier:{type:"void","x-component":"Button","x-content":i18nExpression("cusEntry.common.quasiSupplier"),"x-component-props":{"@click":expression(`() => {
                    $form.query('.query').get('value').abnormalDirectory = null
                    $form.query('.query').get('value').potentialSuppliers = null
                    $form.query('.query').get('value').status = null
                    $form.query('.query').get('value').pjCompanyStatus = 'QUASI_SUPPLIER'
                    $form.query('.query').get('value').dataSources = null
                    $form.query('.query').take().invoke('query')
                  }`)}},officialSupplier:{type:"void","x-component":"Button","x-content":i18nExpression("cusEntry.common.officialSupplier"),"x-component-props":{"@click":expression(`() => {
                    $form.query('.query').get('value').abnormalDirectory = null
                    $form.query('.query').get('value').potentialSuppliers = null
                    $form.query('.query').get('value').status = null
                    $form.query('.query').get('value').pjCompanyStatus = 'OFFICIAL_SUPPLIER'
                    $form.query('.query').get('value').dataSources = null
                    $form.query('.query').take().invoke('query')
                  }`)}},sluggishSupplier:{type:"void","x-component":"Button","x-content":i18nExpression("cusEntry.common.sluggishSupplier"),"x-component-props":{"@click":expression(`() => {
                    $form.query('.query').get('value').abnormalDirectory = null
                    $form.query('.query').get('value').potentialSuppliers = null
                    $form.query('.query').get('value').status = null
                    $form.query('.query').get('value').pjCompanyStatus = 'SLUGGISH_SUPPLIER'
                    $form.query('.query').get('value').dataSources = null
                    $form.query('.query').take().invoke('query')
                  }`)}},invalidupSplier:{type:"void","x-component":"Button","x-content":i18nExpression("cusEntry.common.invalidupSplier"),"x-component-props":{"@click":expression(`() => {
                    $form.query('.query').get('value').abnormalDirectory = null
                    $form.query('.query').get('value').potentialSuppliers = null
                    $form.query('.query').get('value').status = null
                    $form.query('.query').get('value').pjCompanyStatus = 'INVALID_SUPPLIER'
                    $form.query('.query').get('value').dataSources = null
                    $form.query('.query').take().invoke('query')
                  }`)}},abnormalDirectory:{type:"void","x-component":"Button","x-content":i18nExpression("cusEntry.common.abnormalDirectory"),"x-component-props":{"@click":expression(`() => {
                    $form.query('.query').get('value').potentialSuppliers = null
                    $form.query('.query').get('value').abnormalDirectory = 'Y'
                    $form.query('.query').take().invoke('query')
                  }`)}}}}}},table:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",class:"table-view-vxe-table",openCustomTable:!0,editMode:!0},properties:generateXindexInOrder({allowClearWithoutSealFlag:{type:"string","x-hidden":!0},allowBidWithoutSealFlag:{type:"string","x-hidden":!0},allowQuotationWithoutSealFlag:{type:"string","x-hidden":!0},createdId:{type:"string","x-hidden":!0},companyCode:{type:"string","x-read-pretty":!0,title:i18nExpression("common.vendorCode"),"x-render-table-column":{width:120}},companyName:{"x-component":"TableButton","x-component-props":{type:"text","@click":expression(`({row}) => {
                let companyId = row.companyId
                let tab = {
                  component: vendorProfileDetailReadEngine,
                  params: {
                    flag: 'view',
                    companyId: companyId,
                    tabName: 'vendorProfileDetailReadEngine' + row.companyName
                  },
                  title: row.companyName,
                  name: 'vendorProfileDetailReadEngine' + row.companyName
                }
                emitTabAdd(tab)
              }`)},"x-render-table-column":{title:i18nExpression("common.vendorName"),minWidth:150,customRender:!0}},lcCode:{type:"string","x-read-pretty":!0,title:i18nExpression("vendorMod.lcCode"),"x-render-table-column":{minWidth:150}},extIsQualifiedFileUpload:{type:"string","x-read-pretty":!0,title:i18nExpression("cusEntry.vendorMod.extIsQualifiedFileUpload"),"x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"},"x-render-table-column":{width:150}},extIsQualifiedStatus:{type:"string","x-read-pretty":!0,title:i18nExpression("cusEntry.vendorMod.extIsQualifiedStatus"),"x-component":"DictSelect","x-component-props":{code:"QUALIFIED_UPLOAD_STATUS"},"x-render-table-column":{width:150}},isBacklist:{type:"string","x-read-pretty":!0,title:i18nExpression("vendorMod.isBacklist"),"x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"},"x-render-table-column":{width:120}},keySupervisionFlag:{type:"string","x-read-pretty":!0,title:i18nExpression("cusEntry.vendorMod.isKeySupervision"),"x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"},"x-render-table-column":{width:120}},positionLimitFlag:{type:"string","x-read-pretty":!0,title:i18nExpression("cusEntry.vendorMod.ifLimitUnit"),"x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"},"x-render-table-column":{width:120}},categoryLimitFlag:{type:"string","x-read-pretty":!0,title:i18nExpression("cusEntry.vendorMod.ifLimitCategory"),"x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"},"x-render-table-column":{width:120}},timeLimitFlag:{type:"string","x-read-pretty":!0,title:i18nExpression("cusEntry.vendorMod.ifLimitTime"),"x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"},"x-render-table-column":{width:120}},contactName:{type:"string","x-read-pretty":!0,title:i18nExpression("cusEntry.vendorMod.contactName"),"x-render-table-column":{width:120}},ceeaContactMethod:{type:"string","x-read-pretty":!0,title:i18nExpression("cusEntry.vendorMod.contactPhone"),"x-render-table-column":{width:120}},pjCompanyStatus:{type:"string","x-read-pretty":!0,title:i18nExpression("cusEntry.vendorMod.companyStatus"),"x-component":"DictSelect","x-component-props":{code:"PJ_COMPANY_STATUS"},"x-render-table-column":{width:100}},status:{type:"string","x-read-pretty":!0,title:i18nExpression("vendorMod.registerStatus"),"x-component":"DictSelect","x-component-props":{code:"SUPPLIER_LIST_STATUS"},"x-render-table-column":{width:100}},gscpStatus:{type:"string","x-read-pretty":!0,title:i18nExpression("cusEntry.vendorMod.gscpStatus"),"x-render-table-column":{width:100}},approvedDate:{"x-read-pretty":!0,title:i18nExpression("vendorMod.permitDate"),...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.approvedDate, '{y}-{m}-{d}')
              }`)},"x-render-table-column":{width:130}},dataSources:{type:"string","x-read-pretty":!0,title:i18nExpression("vendorMod.dataSources"),"x-component":"DictSelect","x-component-props":{code:"DATA_SOURCE"},"x-render-table-column":{width:100}},infoCompleteFlag:{type:"string",title:i18nExpression("cusEntry.vendorMod.infoCompleteFlag"),"x-read-pretty":!0,"x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"},"x-render-table-column":{width:120}},companyId:{type:"string","x-hidden":!0},lastUpdateDate:{type:"string","x-hidden":"true","x-query-engine-sort":"desc"},operation:{type:"void",title:i18nExpression("common.operation"),"x-render-table-column":{width:200,fixed:"right"},"x-component":"RenderTableButtonList","x-component-props":{max:2},properties:{passRegister:{type:"void",title:i18nExpression("common.toApprove"),"x-reactions":changeFieldVisibleByDeps([".companyCode"],"!$deps[0]"),"x-component-props":{type:"text","@click":expression(`({row}) => {
                    let tab = {
                      component: vendorProfileDetailReadEngine,
                      params: {
                        flag: 'passRegister',
                        companyId: row.companyId,
                        tabName: 'vendorProfileDetailReadEngine' + row.companyName
                      },
                      title: row.companyName,
                      name: 'vendorProfileDetailReadEngine' + row.companyName
                    }
                    emitTabAdd(tab)
                  }`)}},refuse:{type:"void",title:i18nExpression("purchaseDemand.refuse"),"x-reactions":changeFieldVisibleByDeps([".companyCode"],"!$deps[0]"),"x-component-props":{type:"text","@click":expression(`({ row }) => {
                    app.$prompt('', $t('vendorMod.rejectReason'), {
                      confirmButtonText: $t('common.confirm'),
                      cancelButtonText: $t('common.cancel'),
                      inputType: 'textarea'
                    }).then(({ value }) => {
                      let values = {
                        flowRemark: value,
                        companyId: row.companyId
                      }
                      $queryEngine.request.save(values, { query: { '*':{} }, action: 'reject' }).then((res) => {
                        $message.success($t('bidMod.toRefuseSuccess'))
                        $queryEngine.state.paginationManagement.refresh()
                      })
                    })
                  }`)}},startFileApproval:{type:"void",title:i18nExpression("cusEntry.supplement20250211.submitFileApprove"),"x-reactions":expression(`(field) => {
                  const row = $table.getRowByIndex($self.index)
                  field.visible = ['SUBMITTED', 'WITHDRAW', 'REJECTED'].includes(row.extIsQualifiedStatus)
                }`),"x-component-props":{type:"text","@click":expression(`({row}) => {
                    let tab = {
                      component: vendorProfileDetailReadEngine,
                      params: {
                        flag: 'startFileApproval',
                        companyId: row.companyId,
                        tabName: 'vendorProfileDetailReadEngine' + row.companyName
                      },
                      title: row.companyName,
                      name: 'vendorProfileDetailReadEngine' + row.companyName
                    }
                    emitTabAdd(tab)
                  }`)}},rejectFile:{type:"void",title:i18nExpression("cusEntry.supplement20250211.rejectFile"),"x-reactions":expression(`(field) => {
                  const row = $table.getRowByIndex($self.index)
                  field.visible = ['SUBMITTED', 'WITHDRAW', 'REJECTED'].includes(row.extIsQualifiedStatus)
                }`),"x-component-props":{type:"text","@click":expression(`({ row }) => {
                    app.$prompt('', $t('vendorMod.rejectReason'), {
                      confirmButtonText: $t('common.confirm'),
                      cancelButtonText: $t('common.cancel'),
                      inputType: 'textarea',
                      inputValidator: value => !!value,
                      inputErrorMessage: $t('common.pleaseInput')
                    }).then(({ value }) => {
                      let values = {
                        extRejectQualificationReason: value,
                        companyId: row.companyId
                      }
                      app.$http({
                        url: '/api-sup/pj/companyInfo/rejectQualificationReason',
                        method: 'POST',
                        data: { ...values },
                        loading: true
                      }).then(res => {
                        $message.success($t('bidMod.toRefuseSuccess'))
                        $queryEngine.state.paginationManagement.refresh()
                      }).catch(() => {})
                    })
                  }`)}},approval:{type:"void",title:i18nExpression("common.approve"),"x-reactions":expression(`(field) => {
                  const row = $table.getRowByIndex($self.index)
                  field.visible = ['APPROVING'].includes(row.extIsQualifiedStatus) && (app.$store.getters.userInfo.userId == row.createdId || row.isApprover == 'Y')
                }`),"x-component-props":{type:"text","@click":expression(`({row}) => {
                    let tab = {
                      component: vendorProfileDetailReadEngine,
                      params: {
                        flag: 'approval',
                        companyId: row.companyId,
                        tabName: 'vendorProfileDetailReadEngine' + row.companyName
                      },
                      title: row.companyName,
                      name: 'vendorProfileDetailReadEngine' + row.companyName
                    }
                    emitTabAdd(tab)
                  }`)}},view:{type:"void","x-component":"QuickSearchWrapper","x-component-props":{name:"scc_npm_inspect_vendor_history",showButton:!0,btnTitle:i18nExpression("cusEntry.vendorMod.checkHistory"),btnType:"text","pre-query-data":"{{{'t.vendor_id': $table.getRowByIndex($self.index).companyId}}}"}},createdQuaofReview:{type:"void",title:i18nExpression("vendorMod.createQua"),"x-reactions":changeFieldVisibleByDeps([".status",".isBacklist"],"['APPROVED'].includes($deps[0]) && ['N'].includes($deps[1])"),"x-component-props":{type:"text",...buttonListItemVisibleByPermission("sup:vendorpRroList:createQua"),"@click":expression(`({ row }) => {
                    let tab = {
                      component: quaOfReviewDetail,
                      params: {
                        flag: 'add',
                        row,
                        tabName: 'quaOfReviewDetail'
                      },
                      title: () => $t('vendorMod.addQua'), // '资质审查新增',
                      name: 'quaOfReviewDetail'
                    }
                    emitTabAdd(tab)
                  }`)}},abnormalRegister:{type:"void",title:i18nExpression("cusEntry.common.abnormalRegister"),"x-component-props":{type:"text",...buttonListItemVisibleByPermission("sup:vendorpRroList:abnormalRegister"),"@click":expression(`({row}) => {
                    const companyId = row.companyId
                    $form.query('state').get('data').companyId = companyId
                    $openAbnormalRegister($form)
                  }`)}}}}})},bidTag:{type:"void","x-component":"RDialog",title:i18nExpression("cusEntry.vendorMod.bidTag"),"x-component-props":{size:"middle",footer:!0,beforeClose:expression(`(done, type) => {
            if ( type === 'ok') {
              const companyId = $form.query('state').get('data').companyId
              const value = $form.query('biddingFlag').get('value')
              if (value) {
                $queryEngine.request.update({ companyId, biddingFlag: value}).then((res) => {
                  $message.success($t('cusEntry.tipMessage.tagSuccess'))
                  $queryEngine.state.paginationManagement.refresh()
                })
              }
            }
            done()
        }`)},properties:{biddingFlag:{type:"string",title:i18nExpression("cusEntry.vendorMod.isCompetition"),"x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"},"x-decorator":"FormItem",...requiredValidatorSegment}}},contractVerificationDialog:{type:"void","x-component":"RDialog",title:i18nExpression("cusEntry.vendorMod.covenantCertification"),"x-component-props":{size:"middle",footer:!0,beforeClose:expression(`(done, type) => {
            if ( type === 'ok') {
              const companyId = $form.query('state').get('data').companyId
              const values = $form.query('contractVerificationForm').get('value')
              const {
                allowClearWithoutSealFlag,
                allowBidWithoutSealFlag,
                allowQuotationWithoutSealFlag
              } = values
              if (allowClearWithoutSealFlag || allowBidWithoutSealFlag || allowQuotationWithoutSealFlag) {
                $queryEngine.request.update({ companyId, ...values}).then((res) => {
                  $message.success($t('cusEntry.tipMessage.covenantCertificationSuccess'))
                  $queryEngine.state.paginationManagement.refresh()
                })
              }
            }
            done()
        }`)},properties:{contractVerificationForm:{type:"object","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},"x-query-engine-skip":!0,properties:{allowClearWithoutSealFlag:{type:"string",title:i18nExpression("cusEntry.vendorMod.allowClearWithoutSealFlag"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"}},allowBidWithoutSealFlag:{type:"string",title:i18nExpression("cusEntry.vendorMod.allowBidWithoutSealFlag"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"}},allowQuotationWithoutSealFlag:{type:"string",title:i18nExpression("cusEntry.vendorMod.allowQuotationWithoutSealFlag"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"}}}}}},abnormalRegisterDialog:{type:"void","x-component":"RDialog",title:i18nExpression("cusEntry.vendorMod.abnormalRegister"),"x-component-props":{size:"middle",footer:!0,beforeClose:expression(`(done, type) => {
            if ( type === 'ok') {
              const companyId = $form.query('state').get('data').companyId
              console.log($form.query('abnormalRegisterForm').get('value'))
              const { keySupervisionFlag, npmCompanyExceptionInfos } = $form.query('abnormalRegisterForm').get('value') || {}
              if (keySupervisionFlag) {
                const payLoad = {
                  companyId,
                  keySupervisionFlag,
                  npmCompanyExceptionInfos: [{exceptionRemark: npmCompanyExceptionInfos}]
                }
                $queryEngine.request.save(payLoad, { action: 'saveExceptionInfo' }).then((res) => {
                  $message.success($t('cusEntry.tipMessage.abnormalRegisterSuccess'))
                  $queryEngine.state.paginationManagement.refresh()
                })
              }
            }
            done()
          }`)},properties:{abnormalRegisterForm:{type:"object","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:{keySupervisionFlag:{type:"string",title:i18nExpression("cusEntry.vendorMod.isKeySupervision"),"x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"},"x-decorator":"FormItem",...requiredValidatorSegment},npmCompanyExceptionInfos:{type:"string",title:i18nExpression("cusEntry.vendorMod.keySupervisionRemark"),"x-decorator":"FormItem"}}}}}}}}),$openBidTag=$form=>{$form.query("bidTag").take().setComponentProps({visible:!0})},$openCovenantCertification=($form,row)=>{$form.query("contractVerificationDialog").take().setComponentProps({visible:!0}),setTimeout(()=>{$form.query("contractVerificationForm").take().value=row})},$openAbnormalRegister=$form=>{$form.query("abnormalRegisterDialog").take().setComponentProps({visible:!0})};return{__sfc:!0,emitTabAdd,app,schema,$openBidTag,$openCovenantCertification,$openAbnormalRegister,scope:{emitTabAdd,app,i18nExpression,vendorProfileDetailReadEngine,quaOfReviewDetail,$openBidTag,$openCovenantCertification,$openAbnormalRegister},components:{},RenderEngine}}});var _sfc_render$1=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{staticClass:"contractPaymentType",attrs:{schemaKey:"vendorProfileList",schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns$1=[],__component__$1=normalizeComponent(_sfc_main$1,_sfc_render$1,_sfc_staticRenderFns$1,!1,null,null,null,null);const vendorProfileListEngine=__component__$1.exports,_sfc_main={name:"VendorProfile",components:{NavTabs},data(){return{activeTab:"vendorProfileListEngine",tabs:[{title:this.$t("vendorMod.vendorProfileList"),name:"vendorProfileListEngine",component:vendorProfileListEngine,closable:!1}]}}};var _sfc_render=function(){var _vm=this,_c=_vm._self._c;return _c("NavTabs",{ref:"tabs",attrs:{"tabs-list":_vm.tabs,"cur-tab":_vm.activeTab}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const index=__component__.exports;export{index as default};
