import{N as NavTabs}from"./index-a035e78f.js";import{al as defineComponent,am as usePageHelper,ar as defineSchemas,ae as expression,ai as generateXindexInOrder,af as i18nExpression,bW as dataTimeSelectorSegment,bM as exportExcelSegment,bN as queryFieldStatePropertyExpression,ag as yearMonthDaySelectorSegment,bt as changeFieldVisibleByDeps,c7 as buttonListItemVisibleByPermission,aC as requiredValidatorSegment,as as RenderEngine,n as normalizeComponent}from"./index-17d0ccd5.js";import{v as vendorProfileDetailReadEngine,q as quaOfReviewDetail}from"./quaOfReviewDetail-4ad4a096.js";import"./vendorManagement-dfc64e68.js";import"./TableView-c2798def.js";import"./util-6482eb24.js";import"./index-92e24989.js";import"./drag-5571e5c7.js";import"./basicSetting-f3b18103.js";import"./VendorAccessSteps-2c4195ea.js";import"./vendorProfileDetailReadEngine.vue_vue_type_style_index_0_lang-7d7cf785.js";import"./index-38ab0095.js";/* empty css                                                              */import"./file-dynamic-30cdd411.js";import"./file-dynamic-ab2ff377.js";import"./MainHeader-ef959a5c.js";/* empty css                                                                   */import"./file-dynamic.vue_vue_type_style_index_0_scoped_516fdfc3_lang-e128b539.js";import"./tree-utils-7df6be59.js";import"./BaseTableBind-53264a4f.js";import"./index-4d512f00.js";/* empty css                                                                      */import"./fileApi-3f0be128.js";import"./index-6af40985.js";/* empty css                                              */import"./edit-c1534e20.js";import"./edit.vue_vue_type_style_index_0_lang-0314473d.js";import"./supApi-e5726083.js";import"./vendorManagement-89a77d38.js";import"./util-a92f9f8e.js";import"./sourcingApplicationDetail-4ddb886b.js";import"./index-30d5b85c.js";import"./composition-52c47d7f.js";import"./big-e21bdbb6.js";import"./enum-ea8c1af9.js";import"./index-8507f7f9.js";import"./index-9e67bb9b.js";import"./index-531039c3.js";/* empty css                                                              */import"./mixins-f89b147e.js";import"./datePickerOptions-40ce843f.js";import"./sourcingApplicationDetail-a671e522.js";/* empty css                                                                  *//* empty css                                                                  */import"./categorySelect-d3400b4c.js";const _sfc_main$1=defineComponent({__name:"vendorProfileListEngine",setup(__props){const{emitTabAdd,app}=usePageHelper(),schema=defineSchemas({state:{"x-data":{companyId:null}},CompanyInfo:{type:"void","x-decorator":"QueryEngine","x-query-engine":{service:"sup",actions:{queryPotentialSupplier:{immediate:!0,method:"paginationQuery",ready:expression(`(data, header) => {
            const { dataResources, funName } = app.$route.params
            if (funName) {
              let companyId = dataResources.companyId
                let tab = {
                  component: vendorProfileDetailReadEngine,
                  params: {
                    flag: 'view',
                    companyId: companyId,
                    tabName: 'vendorProfileDetailReadEngine' + dataResources.companyName
                  },
                  title: dataResources.companyName,
                  name: 'vendorProfileDetailReadEngine' + dataResources.companyName
                }
                emitTabAdd(tab)
            }
            return true
          }`),transformRequest:expression(`(data, header) => {
            if (data.payload?.filter) {
              const {
                abnormalDirectory,
                registeredCapitalEnd,
                registeredCapital,
                mainCustom,
                ...otherFilter
              } = data.payload.filter
              if (abnormalDirectory) {
                data.payload.filter = {
                  ...otherFilter,
                  '$or': {
                    isBacklist: 'Y',
                    focusFlag: 'Y',
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
              if (mainCustom) {
                data.query.cateJournalList = {
                  categoryJournalId: {},
                  '$condition': {
                    '$strictQuery': true
                  },
                  npmSerciceCustoms: {
                    serciceCustomId: {},
                    '$condition': {
                      '$strictQuery': true,
                      'filter': {
                        'mainCustom': mainCustom
                      }
                    }
                  }
                }
              }
            }
            return data
          }`)},approve:{autoFormatResult:!1},reject:{autoFormatResult:!1}}},"x-component":"el-container","x-component-props":{class:"flex-container the_contractTemplateList_wrapper",direction:"vertical"},properties:{bus:{type:"void","x-component":"BusEvent","x-component-props":{eventName:"green","@listener":expression(`() => {
            $queryEngine.state.paginationManagement.refresh()
          }`)}},query:{type:"object","x-query-engine-skip":!0,"x-component":"QueryFormByQueryEngine","x-component-props":{immediateQueryForm:!0},properties:generateXindexInOrder({companyName:{type:"string","x-query-engine-query-operator":"contains",title:i18nExpression("common.vendorName")},companyCode:{type:"string","x-query-engine-query-operator":"contains",title:i18nExpression("common.vendorCode")},contactName:{type:"string",title:i18nExpression("cusEntry.vendorMod.contactName"),"x-query-engine-query-operator":"contains","x-query-engine-relation-strict":!0,"x-query-engine-relation":"contactInfos"},ceeaContactMethod:{type:"string",title:i18nExpression("cusEntry.vendorMod.contactPhone"),"x-query-engine-query-operator":"contains","x-query-engine-relation-strict":!0,"x-query-engine-relation":"contactInfos"},lcCode:{type:"string",title:i18nExpression("vendorMod.lcCode"),"x-query-engine-query-operator":"contains"},isBacklist:{type:"string",title:i18nExpression("vendorMod.isBacklist"),"x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"}},overseasRelation:{type:"string",title:i18nExpression("vendorMod.overseasRelation"),"x-component":"DictSelect","x-component-props":{code:"RELATION_NEW"}},companyType:{type:"string",title:i18nExpression("vendorMod.companyType"),"x-component":"DictSelect","x-component-props":{code:"COMPANY_NATURE"}},pjCompanyStatus:{type:"string",title:i18nExpression("cusEntry.vendorMod.companyStatus"),"x-component":"DictSelect","x-component-props":{code:"PJ_COMPANY_STATUS"}},dataSources:{type:"string",title:i18nExpression("vendorMod.dataSources"),"x-component":"DictSelect","x-component-props":{code:"DATA_SOURCE"}},status:{title:i18nExpression("vendorMod.registerStatus"),"x-component":"DictSelect","x-component-props":{code:"SUPPLIER_LIST_STATUS_vendorProfileList"}},mainCustom:{type:"string",title:i18nExpression("cusEntry.vendorMod.mainCustomer"),"x-query-engine-query-operator":"contains"},approvedDate:{type:"date",title:i18nExpression("cusEntry.vendorMod.enterDate"),...dataTimeSelectorSegment,"x-query-engine-query-operator":"between"},companyCreationDate:{type:"date",title:i18nExpression("cusEntry.vendorMod.creationDate"),...dataTimeSelectorSegment,"x-query-engine-query-operator":"between"},authNum:{type:"string",title:i18nExpression("cusEntry.vendorMod.certificateReport"),"x-component":"DictSelect","x-component-props":{code:"CERTIFICATE_TYPE"},"x-query-engine-relation-strict":!0,"x-query-engine-relation":"managementAttaches"},biddingFlag:{type:"string",title:i18nExpression("cusEntry.vendorMod.isCompetition"),"x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"}},keySupervisionFlag:{type:"string",title:i18nExpression("cusEntry.vendorMod.isKeySupervision"),"x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"}},ceeaAgentBrand:{type:"string",title:i18nExpression("cusEntry.vendorMod.brand")},registeredCapital:{type:"string",title:i18nExpression("cusEntry.vendorMod.registeredAmountFrom"),"x-query-engine-query-operator":"gt"},registeredCapitalEnd:{type:"string",title:i18nExpression("cusEntry.vendorMod.registeredAmountEnd")},registCurrency:{type:"string",title:i18nExpression("cusEntry.vendorMod.registeredCurrency"),"x-component":"DictSelect","x-component-props":{code:"currency"}},companyAddress:{type:"string",title:i18nExpression("cusEntry.vendorMod.address"),"x-query-engine-query-operator":"contains"},abnormalDirectory:{type:"string","x-hidden":!0},potentialSuppliers:{type:"string","x-hidden":!0},infoCompleteFlag:{type:"string",title:i18nExpression("vendorMod.infoCompleteFlag"),"x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"}},categoryId:{type:"string",title:i18nExpression("vendorMod.categoryName"),"x-component":"QuickSearchWrapper","x-query-engine-query-operator":"eq","x-component-props":{showKey:"categoryName",name:"scc_base_purchase_category4"},"x-query-engine-relation-strict":!0,"x-query-engine-relation":"cateJournalList"}})},toolbar:{type:"void","x-component":"Space","x-component-props":{style:"margin-bottom: 16px"},properties:{exportExcel:{type:"void","x-component":"ExportExcel","x-component-props":{...exportExcelSegment,type:"default",pageUrl:"/api-sup/api-ql/CompanyInfo/query",tableHeader:queryFieldStatePropertyExpression("CompanyInfo.table","data.columns"),dictCodes:{overseasRelation:"RELATION_NEW",companyType:"COMPANY_NATURE",status:"SUPPLIER_LIST_STATUS",dataSources:"DATA_SOURCE",isBacklist:"YES_OR_NO",focusFlag:"YES_OR_NO",keySupervisionFlag:"YES_OR_NO",positionLimitFlag:"YES_OR_NO",categoryLimitFlag:"YES_OR_NO",timeLimitFlag:"YES_OR_NO",supplierType:"SUPPLIER_TYPE",infoCompleteFlag:"YES_OR_NO"}}},buttonGroup:{type:"void","x-component":"el-button-group",properties:{all:{type:"void","x-component":"Button","x-content":i18nExpression("cusEntry.common.all"),"x-component-props":{"@click":expression(`() => {
                    $form.query('query').take().invoke('resetQuery')
                  }`)}},potentialSuppliers:{type:"void","x-component":"Button","x-content":i18nExpression("cusEntry.common.potentialSuppliers"),"x-component-props":{"@click":expression(`() => {
                    $form.query('.query').get('value').abnormalDirectory = null
                    $form.query('.query').get('value').potentialSuppliers = null
                    $form.query('.query').get('value').status = 'SUBMITTED'
                    $form.query('.query').get('value').pjCompanyStatus = 'POTENTIAL_SUPPLIER'
                    $form.query('.query').get('value').dataSources = 'ONESELF_REGISTER'
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
                  }`)}}}}}},table:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",class:"table-view-vxe-table",openCustomTable:!0,editMode:!0},properties:generateXindexInOrder({allowClearWithoutSealFlag:{type:"string","x-hidden":!0},allowBidWithoutSealFlag:{type:"string","x-hidden":!0},allowQuotationWithoutSealFlag:{type:"string","x-hidden":!0},companyCode:{type:"string","x-read-pretty":!0,title:i18nExpression("common.vendorCode"),"x-render-table-column":{width:120}},companyName:{"x-component":"TableButton","x-component-props":{type:"text","@click":expression(`({row}) => {
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
              }`)},"x-render-table-column":{title:i18nExpression("common.vendorName"),minWidth:150,customRender:!0}},lcCode:{type:"string","x-read-pretty":!0,title:i18nExpression("vendorMod.lcCode"),"x-render-table-column":{minWidth:150}},isBacklist:{type:"string","x-read-pretty":!0,title:i18nExpression("vendorMod.isBacklist"),"x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"},"x-render-table-column":{width:120},"x-reactions":{target:".focusFlag",when:"{{$self.value === 'Y'}}",fulfill:{run:"$self.value = '111'"},otherwise:{run:"console.log($self)"}}},focusFlag:{type:"string","x-read-pretty":!0,title:i18nExpression("cusEntry.vendorMod.ifFocus"),"x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"},"x-render-table-column":{width:120}},keySupervisionFlag:{type:"string","x-read-pretty":!0,title:i18nExpression("cusEntry.vendorMod.keySupervisionFlag"),"x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"},"x-render-table-column":{width:120}},positionLimitFlag:{type:"string","x-read-pretty":!0,title:i18nExpression("cusEntry.vendorMod.ifLimitUnit"),"x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"},"x-render-table-column":{width:120}},categoryLimitFlag:{type:"string","x-read-pretty":!0,title:i18nExpression("cusEntry.vendorMod.ifLimitCategory"),"x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"},"x-render-table-column":{width:120}},timeLimitFlag:{type:"string","x-read-pretty":!0,title:i18nExpression("cusEntry.vendorMod.ifLimitTime"),"x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"},"x-render-table-column":{width:120}},biddingFlag:{type:"string","x-read-pretty":!0,title:i18nExpression("cusEntry.vendorMod.isCompetition"),"x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"},"x-render-table-column":{width:100}},contactName:{type:"string","x-read-pretty":!0,title:i18nExpression("cusEntry.vendorMod.contactName"),"x-render-table-column":{width:120}},ceeaContactMethod:{type:"string","x-read-pretty":!0,title:i18nExpression("cusEntry.vendorMod.contactPhone"),"x-render-table-column":{width:120}},pjCompanyStatus:{type:"string","x-read-pretty":!0,title:i18nExpression("cusEntry.vendorMod.companyStatus"),"x-component":"DictSelect","x-component-props":{code:"PJ_COMPANY_STATUS"},"x-render-table-column":{width:100}},status:{type:"string","x-read-pretty":!0,title:i18nExpression("vendorMod.registerStatus"),"x-component":"DictSelect","x-component-props":{code:"SUPPLIER_LIST_STATUS"},"x-render-table-column":{width:100}},gscpStatus:{type:"string","x-read-pretty":!0,title:i18nExpression("cusEntry.vendorMod.gscpStatus"),"x-render-table-column":{width:100}},approvedDate:{"x-read-pretty":!0,title:i18nExpression("vendorMod.permitDate"),...yearMonthDaySelectorSegment,"x-render-table-column":{width:130}},dataSources:{type:"string","x-read-pretty":!0,title:i18nExpression("vendorMod.dataSources"),"x-component":"DictSelect","x-component-props":{code:"DATA_SOURCE"},"x-render-table-column":{width:100}},infoCompleteFlag:{type:"string",title:i18nExpression("vendorMod.infoCompleteFlag"),"x-read-pretty":!0,"x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"},"x-render-table-column":{width:120}},companyId:{type:"string","x-hidden":!0},lastUpdateDate:{type:"string","x-hidden":"true","x-query-engine-sort":"desc"},operation:{type:"void",title:i18nExpression("common.operation"),"x-render-table-column":{width:200,fixed:"right"},"x-component":"RenderTableButtonList","x-component-props":{max:2},properties:{view:{type:"void",title:i18nExpression("cusEntry.common.approval"),"x-component":"QuickSearchWrapper","x-component-props":{name:"scc_npm_inspect_vendor_history",showButton:"true",btnTitle:"考察历史",btnType:"text","pre-query-data":"{{{'t.vendor_id': $table.getRowByIndex($self.index).companyId}}}"}},edit:{type:"void",title:i18nExpression("cusEntry.common.approval"),"x-reactions":changeFieldVisibleByDeps([".status",".dataSources"],"['SUBMITTED'].includes($deps[0]) && !['MANUALLY_CREATE'].includes($deps[1])"),"x-component-props":{type:"text","@click":expression(`({row}) => {
                    let tab = {
                      component: vendorProfileDetailReadEngine,
                      params: {
                        flag: 'edit',
                        companyId: row.companyId,
                        tabName: 'vendorProfileDetailReadEngine' + row.companyName
                      },
                      title: row.companyName,
                      name: 'vendorProfileDetailReadEngine' + row.companyName
                    }
                    emitTabAdd(tab)
                  }`)}},refuse:{type:"void",title:i18nExpression("purchaseDemand.refuse"),"x-reactions":changeFieldVisibleByDeps([".status",".dataSources"],"['SUBMITTED'].includes($deps[0]) && !['MANUALLY_CREATE'].includes($deps[1])"),"x-component-props":{type:"text","@click":expression(`({ row }) => {
                    app.$prompt('', '驳回原因', {
                      confirmButtonText: '确定',
                      cancelButtonText: '取消',
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
                  }`)}},createdQuaofReview:{type:"void",title:i18nExpression("vendorMod.createQua"),"x-reactions":changeFieldVisibleByDeps([".status",".isBacklist"],"['APPROVED'].includes($deps[0]) && ['N'].includes($deps[1])"),"x-component-props":{type:"text",...buttonListItemVisibleByPermission("sup:vendorpRroList:createQua"),"@click":expression(`({ row }) => {
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
                  }`)}},bidTag:{type:"void",title:i18nExpression("cusEntry.common.bidTag"),"x-component-props":{type:"text",...buttonListItemVisibleByPermission("sup:vendorpRroList:bidTag"),"@click":expression(`({row}) => {
                    const companyId = row.companyId
                    $form.query('state').get('data').companyId = companyId
                    $openBidTag($form)
                  }`)},"x-reactions":changeFieldVisibleByDeps([".pjCompanyStatus"],"['QUASI_SUPPLIER','OFFICIAL_SUPPLIER'].includes($deps[0])")},covenantCertification:{type:"void",title:i18nExpression("cusEntry.common.covenantCertification"),"x-component-props":{type:"text",...buttonListItemVisibleByPermission("sup:vendorpRroList:covenantCertification"),"@click":expression(`({row}) => {
                    const companyId = row.companyId
                    $form.query('state').get('data').companyId = companyId
                    $openCovenantCertification($form, row)
                  }`)},"x-reactions":changeFieldVisibleByDeps([".pjCompanyStatus"],"['QUASI_SUPPLIER','OFFICIAL_SUPPLIER'].includes($deps[0])")},abnormalRegister:{type:"void",title:i18nExpression("cusEntry.common.abnormalRegister"),"x-component-props":{type:"text",...buttonListItemVisibleByPermission("sup:vendorpRroList:abnormalRegister"),"@click":expression(`({row}) => {
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
          }`)},properties:{abnormalRegisterForm:{type:"object","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:{keySupervisionFlag:{type:"string",title:i18nExpression("cusEntry.vendorMod.keySupervisionFlag"),"x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"},"x-decorator":"FormItem",...requiredValidatorSegment},npmCompanyExceptionInfos:{type:"string",title:i18nExpression("cusEntry.vendorMod.keySupervisionRemark"),"x-decorator":"FormItem"}}}}}}}}),$openBidTag=$form=>{$form.query("bidTag").take().setComponentProps({visible:!0})},$openCovenantCertification=($form,row)=>{$form.query("contractVerificationDialog").take().setComponentProps({visible:!0}),setTimeout(()=>{$form.query("contractVerificationForm").take().value=row})},$openAbnormalRegister=$form=>{$form.query("abnormalRegisterDialog").take().setComponentProps({visible:!0})};return{__sfc:!0,emitTabAdd,app,schema,$openBidTag,$openCovenantCertification,$openAbnormalRegister,scope:{emitTabAdd,app,i18nExpression,vendorProfileDetailReadEngine,quaOfReviewDetail,$openBidTag,$openCovenantCertification,$openAbnormalRegister},components:{},RenderEngine}}});var _sfc_render$1=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{staticClass:"contractPaymentType",attrs:{schemaKey:"vendorProfileList",schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns$1=[],__component__$1=normalizeComponent(_sfc_main$1,_sfc_render$1,_sfc_staticRenderFns$1,!1,null,null,null,null);const vendorProfileListEngine=__component__$1.exports,_sfc_main={name:"VendorProfile",components:{NavTabs},data(){return{activeTab:"vendorProfileListEngine",tabs:[{title:this.$t("vendorMod.vendorProfileList"),name:"vendorProfileListEngine",component:vendorProfileListEngine,closable:!1}]}}};var _sfc_render=function(){var _vm=this,_c=_vm._self._c;return _c("NavTabs",{ref:"tabs",attrs:{"tabs-list":_vm.tabs,"cur-tab":_vm.activeTab}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const index=__component__.exports;export{index as default};
