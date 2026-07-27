import{N as NavTabs}from"./index-a035e78f.js";import{al as defineComponent,am as usePageHelper,ar as defineSchemas,ae as expression,ai as generateXindexInOrder,bW as dataTimeSelectorSegment,bM as exportExcelSegment,bN as queryFieldStatePropertyExpression,af as i18nExpression,ag as yearMonthDaySelectorSegment,bt as changeFieldVisibleByDeps,as as RenderEngine,n as normalizeComponent}from"./index-17d0ccd5.js";import{v as vendorProfileDetailReadEngine}from"./vendorProfileDetailReadEngine-54c1249b.js";import quaOfReviewDetail from"./quaOfReviewDetail-c68f6086.js";import quaOfReviewDetail$1 from"./quaOfReviewDetail-77ef57a4.js";import"./index-38ab0095.js";/* empty css                                                              */import"./file-dynamic-30cdd411.js";import"./file-dynamic-ab2ff377.js";import"./MainHeader-ef959a5c.js";/* empty css                                                                   */import"./file-dynamic.vue_vue_type_style_index_0_scoped_516fdfc3_lang-e128b539.js";import"./tree-utils-7df6be59.js";import"./basicSetting-f3b18103.js";import"./BaseTableBind-53264a4f.js";import"./util-6482eb24.js";import"./index-4d512f00.js";/* empty css                                                                      */import"./fileApi-3f0be128.js";import"./index-6af40985.js";/* empty css                                              */import"./vendorInfoChangeDetail-d132d8dc.js";import"./mixins-f89b147e.js";/* empty css                                                                               */import"./vue-treeselect.cjs-c5077c9c.js";import"./noop-51892efa.js";import"./modelConfig-5e099c06.js";/* empty css                                                         *//* empty css                       */import"./tableExtend-3c8bc0ee.js";/* empty css                                                    */import"./workflow-common-a1e483a5.js";import"./vendorManagement-dfc64e68.js";import"./index-b8c9566a.js";import"./tableDialog-839abeb4.js";import"./TableView-c2798def.js";import"./index-92e24989.js";import"./drag-5571e5c7.js";/* empty css                                                    */import"./vendorAccessAttachment-c3ab363b.js";/* empty css                                                                          */import"./vendorProfileDetailRead-2667c734.js";import"./index-e52a8dd3.js";/* empty css                                                              */import"./index-ba3f7b5f.js";import"./pick-3f5f8490.js";import"./index.vue_vue_type_style_index_0_scoped_4a574896_lang-4ed993c7.js";import"./questManagementDetail-732b9d61.js";import"./renderForm-13d6e236.js";/* empty css                                                                   *//* empty css                                                                              *//* empty css                                                                                */import"./VendorAccessSteps-2c4195ea.js";import"./pay-plan-da61805c.js";import"./index-531039c3.js";/* empty css                                                 */import"./sourcingApplicationDetail-4ddb886b.js";import"./index-30d5b85c.js";import"./composition-52c47d7f.js";import"./big-e21bdbb6.js";import"./enum-ea8c1af9.js";import"./index-8507f7f9.js";import"./index-9e67bb9b.js";import"./supApi-e5726083.js";/* empty css                                                              */import"./datePickerOptions-40ce843f.js";import"./sourcingApplicationDetail-a671e522.js";/* empty css                                                                  *//* empty css                                                                  *//* empty css                                                                          */const _sfc_main$1=defineComponent({__name:"vendorProfileListEngine",setup(__props){const{emitTabAdd,app}=usePageHelper(),schema=defineSchemas({CompanyInfo:{type:"void","x-decorator":"QueryEngine","x-query-engine":{service:"sup",actions:{paginationQuery:{immediate:!0},approve:{autoFormatResult:!1},reject:{autoFormatResult:!1}}},"x-component":"el-container","x-component-props":{class:"flex-container the_contractTemplateList_wrapper",direction:"vertical"},properties:{bus:{type:"void","x-component":"BusEvent","x-component-props":{eventName:"green","@listener":expression(`() => {
            $queryEngine.state.paginationManagement.refresh()
          }`)}},query:{type:"object","x-query-engine-skip":!0,"x-component":"QueryFormByQueryEngine",properties:generateXindexInOrder({companyName:{type:"string",title:"{{$t('common.vendorName')}}","x-component":"QuickSearchWrapper","x-component-props":{showKey:"companyName",propKey:"companyName",name:"scc_sup_company_info_all"}},lcCode:{type:"string",title:"{{$t('vendorMod.lcCode')}}","x-query-engine-query-operator":"contains"},isBacklist:{type:"string",title:"{{$t('vendorMod.isBacklist')}}","x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"}},overseasRelation:{type:"string",title:"{{$t('vendorMod.overseasRelation')}}","x-component":"DictSelect","x-component-props":{code:"RELATION_NEW"}},companyType:{type:"string",title:"{{$t('vendorMod.companyType')}}","x-component":"DictSelect","x-component-props":{code:"COMPANY_NATURE"}},potentialFlag:{type:"string",title:"{{$t('vendorMod.potentialSupplier')}}","x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"}},dataSources:{type:"string",title:"{{$t('vendorMod.dataSources')}}","x-component":"DictSelect","x-component-props":{code:"DATA_SOURCE"}},status:{title:"{{$t('vendorMod.registerStatus')}}","x-component":"DictSelect","x-component-props":{code:"SUPPLIER_LIST_STATUS_vendorProfileList"}},supplierType:{type:"string",title:"{{$t('supplierRating.supplierType')}}","x-component":"DictSelect","x-component-props":{code:"SUPPLIER_TYPE"}},legalPerson:{type:"string",title:"{{$t('vendorMod.legalPerson')}}","x-query-engine-query-operator":"contains"},approvedDate:{type:"date",title:"准入日期",...dataTimeSelectorSegment,"x-query-engine-query-operator":"between"}})},toolbar:{type:"void","x-component":"Space","x-component-props":{style:"margin-bottom: 16px"},properties:{exportExcel:{type:"void","x-component":"ExportExcel","x-component-props":{...exportExcelSegment,type:"default",pageUrl:"/api-sup/api-ql/CompanyInfo/query",tableHeader:queryFieldStatePropertyExpression("CompanyInfo.table","data.columns"),dictCodes:{overseasRelation:"RELATION_NEW",companyType:"COMPANY_NATURE",status:"SUPPLIER_LIST_STATUS",dataSources:"DATA_SOURCE",isBacklist:"YES_OR_NO",quitFlag:"YES_OR_NO",supplierType:"SUPPLIER_TYPE",forzenFlag:"YES_OR_NO",potentialFlag:"YES_OR_NO"}}}}},table:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",class:"table-view-vxe-table",openCustomTable:!0},properties:generateXindexInOrder({companyId:{type:"string","x-hidden":!0},companyCode:{type:"string",title:"{{$t('common.vendorCode')}}","x-render-table-column":{width:120}},companyName:{"x-component":"TableButton","x-component-props":{type:"text","@click":expression(`({row}) => {
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
              }`)},"x-render-table-column":{title:i18nExpression("common.vendorName"),minWidth:150,customRender:!0}},supplierType:{type:"string",title:"{{$t('supplierRating.supplierType')}}","x-component":"DictSelect","x-component-props":{code:"SUPPLIER_TYPE"},"x-render-table-column":{width:150}},overseasRelation:{type:"string",title:"{{$t('vendorMod.overseasRelation')}}","x-component":"DictSelect","x-component-props":{code:"RELATION_NEW"},"x-render-table-column":{width:150}},companyType:{type:"string",title:"{{$t('vendorMod.companyType')}}","x-component":"DictSelect","x-component-props":{code:"COMPANY_NATURE"},"x-render-table-column":{width:100}},lcCode:{type:"string",title:"{{$t('vendorMod.lcCode')}}","x-render-table-column":{width:150}},legalPerson:{type:"string",title:"{{$t('vendorMod.legalPerson')}}","x-render-table-column":{width:150}},isBacklist:{type:"string",title:"{{$t('vendorMod.isBacklist')}}","x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"},"x-render-table-column":{width:120}},potentialFlag:{type:"string",title:"{{$t('vendorMod.potentialSupplier')}}","x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"},"x-render-table-column":{width:100}},status:{type:"string",title:"{{$t('vendorMod.registerStatus')}}","x-component":"DictSelect","x-component-props":{code:"SUPPLIER_LIST_STATUS"},"x-render-table-column":{width:100}},approvedDate:{title:"{{$t('vendorMod.permitDate')}}",...yearMonthDaySelectorSegment,"x-render-table-column":{width:130}},dataSources:{type:"string",title:"{{$t('vendorMod.dataSources')}}","x-component":"DictSelect","x-component-props":{code:"DATA_SOURCE"},"x-render-table-column":{width:100}},forzenFlag:{type:"string",title:"{{$t('vendorMod.forzenFlag')}}","x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"},"x-render-table-column":{width:100}},quitFlag:{type:"string",title:"{{$t('bidMod.quitFlag')}}","x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"},"x-render-table-column":{width:100}},lastUpdateDate:{type:"string","x-hidden":!0,"x-query-engine-sort":"desc"},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:200,fixed:"right"},"x-component":"RenderTableButtonList","x-component-props":{max:2},properties:{doApprovalPass:{type:"void",title:"{{$t('purchaseDemand.confirm')}}","x-reactions":changeFieldVisibleByDeps([".status",".dataSources"],"['SUBMITTED'].includes($deps[0]) && !['MANUALLY_CREATE'].includes($deps[1])"),"x-component-props":{type:"text","@click":expression(`({ row }) => {
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
