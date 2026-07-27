import{N as NavTabs}from"./index-9a7f2446.js";import{ak as defineComponent,al as usePageHelper,aq as defineSchemas,ad as expression,ah as generateXindexInOrder,b$ as dataTimeSelectorSegment,bS as exportExcelSegment,bT as queryFieldStatePropertyExpression,ae as i18nExpression,af as yearMonthDaySelectorSegment,bD as changeFieldVisibleByDeps,ar as RenderEngine,n as normalizeComponent}from"./index-6b6051d8.js";import{v as vendorProfileDetailReadEngine}from"./vendorProfileDetailReadEngine-5806067a.js";import quaOfReviewDetail from"./quaOfReviewDetail-fd123986.js";import quaOfReviewDetail$1 from"./quaOfReviewDetail-7ad28e52.js";import"./index-baa5f2f5.js";/* empty css                                                              */import"./file-dynamic-25a093c4.js";import"./file-dynamic-7fc2d358.js";import"./MainHeader-2d842985.js";/* empty css                                                                   */import"./file-dynamic.vue_vue_type_style_index_0_scoped_516fdfc3_lang-f2c1a82d.js";import"./tree-utils-7df6be59.js";import"./basicSetting-fc46a2d9.js";import"./BaseTableBind-b1f76fc9.js";import"./util-1e55288f.js";import"./index-4d512f00.js";/* empty css                                                                      */import"./fileApi-05bbbbcc.js";import"./index-2c71d18e.js";/* empty css                                              */import"./vendorInfoChangeDetail-0bff0ae6.js";import"./mixins-edc77a54.js";/* empty css                                                                               */import"./vue-treeselect.cjs-8e155c19.js";import"./noop-51892efa.js";import"./modelConfig-f4bd3e07.js";/* empty css                                                         *//* empty css                       */import"./tableExtend-5e62371d.js";/* empty css                                                    */import"./workflow-common-8e5f8543.js";import"./vendorManagement-b6129894.js";import"./index-4aa0cc9f.js";import"./index-b99a582f.js";import"./TableView-eb18d7e8.js";import"./index-d31c36cb.js";import"./drag-5571e5c7.js";import"./vendorAccessAttachment-04264be9.js";/* empty css                                                                          */import"./vendorProfileDetailRead-1f26c07e.js";import"./index-ba69fec0.js";/* empty css                                                              */import"./index-1e8533e5.js";import"./pick-b4f398be.js";import"./index.vue_vue_type_style_index_0_scoped_10b87ab8_lang-4ed993c7.js";import"./questManagementDetail-eacac8a8.js";import"./renderForm-74ecb667.js";/* empty css                                                                   *//* empty css                                                                              *//* empty css                                                                                */import"./VendorAccessSteps-c17f3cd8.js";import"./pay-plan-2bd9f833.js";import"./index-e416f1ab.js";/* empty css                                                 */import"./tableDialog-d4aa1cc2.js";import"./sourcingApplicationDetail-25c4086a.js";import"./index-830562a6.js";import"./composition-34efbd9d.js";import"./big-e21bdbb6.js";import"./enum-ea8c1af9.js";import"./index-be901e24.js";import"./index-46e21ee4.js";import"./supApi-98b2a23d.js";/* empty css                                                              */import"./datePickerOptions-40ce843f.js";import"./sourcingApplicationDetail-62d86f42.js";/* empty css                                                                  *//* empty css                                                                  *//* empty css                                                                          */const _sfc_main$1=defineComponent({__name:"vendorProfileListEngine",setup(__props){const{emitTabAdd,app}=usePageHelper(),schema=defineSchemas({CompanyInfo:{type:"void","x-decorator":"QueryEngine","x-query-engine":{service:"sup",actions:{paginationQuery:{immediate:!0},approve:{autoFormatResult:!1},reject:{autoFormatResult:!1}}},"x-component":"el-container","x-component-props":{class:"flex-container the_contractTemplateList_wrapper",direction:"vertical"},properties:{bus:{type:"void","x-component":"BusEvent","x-component-props":{eventName:"green","@listener":expression(`() => {
            $queryEngine.state.paginationManagement.refresh()
          }`)}},query:{type:"object","x-query-engine-skip":!0,"x-component":"QueryFormByQueryEngine",properties:generateXindexInOrder({companyName:{type:"string",title:"{{$t('common.vendorName')}}","x-component":"QuickSearchWrapper","x-component-props":{showKey:"companyName",propKey:"companyName",name:"scc_sup_company_info_all"}},lcCode:{type:"string",title:"{{$t('vendorMod.lcCode')}}","x-query-engine-query-operator":"contains"},isBacklist:{type:"string",title:"{{$t('vendorMod.isBacklist')}}","x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"}},overseasRelation:{type:"string",title:"{{$t('vendorMod.overseasRelation')}}","x-component":"DictSelect","x-component-props":{code:"RELATION_NEW"}},companyType:{type:"string",title:"{{$t('vendorMod.companyType')}}","x-component":"DictSelect","x-component-props":{code:"COMPANY_NATURE"}},potentialFlag:{type:"string",title:"{{$t('vendorMod.potentialSupplier')}}","x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"}},dataSources:{type:"string",title:"{{$t('vendorMod.dataSources')}}","x-component":"DictSelect","x-component-props":{code:"DATA_SOURCE"}},status:{title:"{{$t('vendorMod.registerStatus')}}","x-component":"DictSelect","x-component-props":{code:"SUPPLIER_LIST_STATUS_vendorProfileList"}},supplierType:{type:"string",title:"{{$t('supplierRating.supplierType')}}","x-component":"DictSelect","x-component-props":{code:"SUPPLIER_TYPE"}},legalPerson:{type:"string",title:"{{$t('vendorMod.legalPerson')}}","x-query-engine-query-operator":"contains"},approvedDate:{title:"准入日期",...dataTimeSelectorSegment,"x-query-engine-query-operator":"between"}})},toolbar:{type:"void","x-component":"Space","x-component-props":{style:"margin-bottom: 16px"},properties:{exportExcel:{type:"void","x-component":"ExportExcel","x-component-props":{...exportExcelSegment,type:"default",pageUrl:"/api-sup/api-ql/CompanyInfo/query",tableHeader:queryFieldStatePropertyExpression("CompanyInfo.table","data.columns"),dictCodes:{overseasRelation:"RELATION_NEW",companyType:"COMPANY_NATURE",status:"SUPPLIER_LIST_STATUS",dataSources:"DATA_SOURCE",isBacklist:"YES_OR_NO",quitFlag:"YES_OR_NO",supplierType:"SUPPLIER_TYPE",forzenFlag:"YES_OR_NO",potentialFlag:"YES_OR_NO"}}}}},table:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",class:"table-view-vxe-table",openCustomTable:!0},properties:generateXindexInOrder({companyId:{type:"string","x-hidden":!0},companyCode:{type:"string",title:"{{$t('common.vendorCode')}}","x-render-table-column":{width:120}},companyName:{"x-component":"TableButton","x-component-props":{type:"text","@click":expression(`({row}) => {
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
              }`)},"x-render-table-column":{title:i18nExpression("common.vendorName"),minWidth:150,customRender:!0}},supplierType:{type:"string",title:"{{$t('supplierRating.supplierType')}}","x-component":"DictSelect","x-component-props":{code:"SUPPLIER_TYPE"},"x-render-table-column":{width:150}},overseasRelation:{type:"string",title:"{{$t('vendorMod.overseasRelation')}}","x-component":"DictSelect","x-component-props":{code:"RELATION_NEW"},"x-render-table-column":{width:150}},companyType:{type:"string",title:"{{$t('vendorMod.companyType')}}","x-component":"DictSelect","x-component-props":{code:"COMPANY_NATURE"},"x-render-table-column":{width:100}},lcCode:{type:"string",title:"{{$t('vendorMod.lcCode')}}","x-render-table-column":{width:150}},legalPerson:{type:"string",title:"{{$t('vendorMod.legalPerson')}}","x-render-table-column":{width:150}},isBacklist:{type:"string",title:"{{$t('vendorMod.isBacklist')}}","x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"},"x-render-table-column":{width:120}},potentialFlag:{type:"string",title:"{{$t('vendorMod.potentialSupplier')}}","x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"},"x-render-table-column":{width:100}},status:{type:"string",title:"{{$t('vendorMod.registerStatus')}}","x-component":"DictSelect","x-component-props":{code:"SUPPLIER_LIST_STATUS"},"x-render-table-column":{width:100}},approvedDate:{title:"{{$t('vendorMod.permitDate')}}",...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.approvedDate, '{y}-{m}-{d}')
              }`)},"x-render-table-column":{width:130}},dataSources:{type:"string",title:"{{$t('vendorMod.dataSources')}}","x-component":"DictSelect","x-component-props":{code:"DATA_SOURCE"},"x-render-table-column":{width:100}},forzenFlag:{type:"string",title:"{{$t('vendorMod.forzenFlag')}}","x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"},"x-render-table-column":{width:100}},quitFlag:{type:"string",title:"{{$t('bidMod.quitFlag')}}","x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"},"x-render-table-column":{width:100}},lastUpdateDate:{type:"string","x-hidden":!0,"x-query-engine-sort":"desc"},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:200,fixed:"right"},"x-component":"RenderTableButtonList","x-component-props":{max:2},properties:{doApprovalPass:{type:"void",title:"{{$t('purchaseDemand.confirm')}}","x-reactions":changeFieldVisibleByDeps([".status",".dataSources"],"['SUBMITTED'].includes($deps[0]) && !['MANUALLY_CREATE'].includes($deps[1])"),"x-component-props":{type:"text","@click":expression(`({ row }) => {
                    let values = {
                      companyId: row.companyId
                    }
                    $queryEngine.request.save(values, { query: { '*':{} }, action: 'approve' }).then((res) => {
                      $message.success($t('purchaseDemand.confirm'))
                      $queryEngine.state.paginationManagement.refresh()
                    })
                  }`)}},refuse:{type:"void",title:"{{$t('purchaseDemand.refuse')}}","x-reactions":changeFieldVisibleByDeps([".status",".dataSources"],"['SUBMITTED'].includes($deps[0]) && !['MANUALLY_CREATE'].includes($deps[1])"),"x-component-props":{type:"text","@click":expression(`({ row }) => {
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
                  }`)}},createdQuaofReview:{type:"void",title:"{{$t('vendorMod.createQua')}}","x-reactions":changeFieldVisibleByDeps([".status",".isBacklist"],"['APPROVED'].includes($deps[0]) && ['N'].includes($deps[1])"),"x-component-props":{type:"text","@click":expression(`({ row }) => {
                    if (row.supplierType === 'NO_MATERIAL') {
                      let row2 = {
                        vendorId: row.companyId,
                        vendorCode: row.companyCode,
                        vendorName: row.companyName
                      }
                      let tab = {
                        component: nonQuaOfReviewDetail,
                        params: {
                          flag: 'add',
                          row: row2,
                          tabName: 'quaOfReviewDetail'
                        },
                        title: () => $t('vendorMod.noAddQua'), // '资质审查新增',
                        name: 'quaOfReviewDetail'
                      }
                      emitTabAdd(tab)
                    } else {
                      let row2 = {
                        vendorId: row.companyId,
                        vendorCode: row.companyCode,
                        vendorName: row.companyName
                      }
                      let tab = {
                        component: quaOfReviewDetail,
                        params: {
                          flag: 'add',
                          row: row2,
                          tabName: 'quaOfReviewDetail'
                        },
                        title: () => $t('vendorMod.addQua'), // '资质审查新增',
                        name: 'quaOfReviewDetail'
                      }
                      emitTabAdd(tab)
                    }
                  }`)}},createdQuestionnaire:{type:"void",title:"{{$t('quest.createdQuestionnaire')}}","x-reactions":changeFieldVisibleByDeps([".status",".isBacklist"],"['APPROVED', 'SUBMITTED'].includes($deps[0]) && ['N'].includes($deps[1])"),"x-component-props":{type:"text","@click":expression(`({ row }) => {
                    app.$router.push('/vendorManagement/questManagement')
                  }`)}}}}})}}}});return{__sfc:!0,emitTabAdd,app,schema,scope:{emitTabAdd,app,i18nExpression,vendorProfileDetailReadEngine,nonQuaOfReviewDetail:quaOfReviewDetail,quaOfReviewDetail:quaOfReviewDetail$1},components:{},RenderEngine}}});var _sfc_render$1=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{staticClass:"contractPaymentType",attrs:{schemaKey:"vendorProfileList",schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns$1=[],__component__$1=normalizeComponent(_sfc_main$1,_sfc_render$1,_sfc_staticRenderFns$1,!1,null,null,null,null);const vendorProfileListEngine=__component__$1.exports,_sfc_main={name:"VendorProfile",components:{NavTabs},data(){return{activeTab:"vendorProfileListEngine",tabs:[{title:this.$t("vendorMod.vendorProfileList"),name:"vendorProfileListEngine",component:vendorProfileListEngine,closable:!1}]}}};var _sfc_render=function(){var _vm=this,_c=_vm._self._c;return _c("NavTabs",{ref:"tabs",attrs:{"tabs-list":_vm.tabs,"cur-tab":_vm.activeTab}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const index=__component__.exports;export{index as default};
