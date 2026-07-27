import{N as NavTabs}from"./index-9a7f2446.js";import{ak as defineComponent,al as usePageHelper,aq as defineSchemas,ad as expression,ah as generateXindexInOrder,af as yearMonthDaySelectorSegment,bD as changeFieldVisibleByDeps,ar as RenderEngine,n as normalizeComponent}from"./index-6b6051d8.js";import{s as siteAssessmentDetail}from"./siteAssessmentDetailEngine-70d606b9.js";import quaOfReviewDetail from"./quaOfReviewDetail-7ad28e52.js";import"./vendorAccessAttachment-04264be9.js";/* empty css                                                                          */import"./supApi-98b2a23d.js";import"./vendorManagement-b6129894.js";import"./index-2c71d18e.js";/* empty css                                              */import"./printer-c24f03b5.js";import"./mixins-edc77a54.js";import"./MainHeader-2d842985.js";/* empty css                                                                   */import"./index-b99a582f.js";import"./TableView-eb18d7e8.js";import"./util-1e55288f.js";import"./index-d31c36cb.js";import"./drag-5571e5c7.js";import"./vendorProfileDetailRead-1f26c07e.js";import"./index-ba69fec0.js";import"./modelConfig-f4bd3e07.js";/* empty css                                                         *//* empty css                                                              */import"./index-baa5f2f5.js";/* empty css                                                              */import"./index-1e8533e5.js";import"./pick-b4f398be.js";import"./index.vue_vue_type_style_index_0_scoped_10b87ab8_lang-4ed993c7.js";import"./file-dynamic-7fc2d358.js";import"./file-dynamic.vue_vue_type_style_index_0_scoped_516fdfc3_lang-f2c1a82d.js";import"./tree-utils-7df6be59.js";import"./basicSetting-fc46a2d9.js";import"./BaseTableBind-b1f76fc9.js";import"./index-4d512f00.js";/* empty css                                                                      */import"./fileApi-05bbbbcc.js";import"./index-4aa0cc9f.js";import"./tableExtend-5e62371d.js";/* empty css                                                    */import"./vendorInfoChangeDetail-0bff0ae6.js";/* empty css                                                                               */import"./vue-treeselect.cjs-8e155c19.js";import"./noop-51892efa.js";/* empty css                       */import"./workflow-common-8e5f8543.js";import"./questManagementDetail-eacac8a8.js";import"./renderForm-74ecb667.js";/* empty css                                                                   *//* empty css                                                                              *//* empty css                                                                                */import"./VendorAccessSteps-c17f3cd8.js";import"./pay-plan-2bd9f833.js";import"./index-e416f1ab.js";/* empty css                                                 */import"./sourcingApplicationDetail-25c4086a.js";import"./index-830562a6.js";import"./composition-34efbd9d.js";import"./big-e21bdbb6.js";import"./enum-ea8c1af9.js";import"./index-be901e24.js";import"./index-46e21ee4.js";/* empty css                                                              */import"./datePickerOptions-40ce843f.js";import"./sourcingApplicationDetail-62d86f42.js";/* empty css                                                                  *//* empty css                                                                  */import"./tableDialog-d4aa1cc2.js";/* empty css                                                                          */const _sfc_main$1=defineComponent({__name:"siteAssessmentListEngine",setup(__props){const{emitTabAdd,app}=usePageHelper(),schema=defineSchemas({SiteForm:{type:"void","x-query-engine":{service:"sup",actions:{paginationQuery:{immediate:!0}}},"x-decorator":"QueryEngine","x-component":"el-container","x-component-props":{class:"flex-container the_contractTemplateList_wrapper",direction:"vertical"},properties:{bus:{type:"void","x-component":"BusEvent","x-component-props":{eventName:"siteA","@listener":expression(`() => {
            $queryEngine.state.paginationManagement.refresh()
          }`)}},query:{type:"object","x-query-engine-skip":!0,"x-component":"QueryFormByQueryEngine",properties:generateXindexInOrder({siteFormNumber:{type:"string",title:"{{$t('vendorMod.siteOrderInfoV')}}"},reviewFormNumber:{type:"string",title:"{{$t('vendorMod.quaNum')}}"},approveStatus:{type:"string",title:"{{$t('vendorMod.orderStatus')}}","x-component":"DictSelect","x-component-props":{code:"SUPPLIER_APPROVE_STATUS_TYPE"}},assessmentType:{type:"string",title:"{{$t('vendorMod.siteTypeV')}}","x-component":"DictSelect","x-component-props":{code:"CEEA_ASSESSMENT_TYPE"}},reviewResult:{type:"string",title:"{{$t('vendorMod.certificationResult')}}","x-component":"DictSelect","x-component-props":{code:"CEEA_RESULT_TYPE"}},vendorName:{type:"string",title:"{{$t('common.vendorName')}}","x-component":"QuickSearchWrapper","x-component-props":{showKey:"companyName",propKey:"companyName",name:"scc_sup_company_info_all"}}})},toolbar:{type:"void","x-component":"Space","x-component-props":{style:"margin-bottom: 16px"},properties:{add:{type:"void",title:"{{$t('common.add')}}","x-component":"RButton","x-component-props":{type:"primary","@click":expression(`() => {
                let tab = {
                  component: siteAssessmentDetail,
                  params: {
                    flag: 'add',
                    tabName: 'siteAssessmentDetail'
                  },
                  title: $t('vendorMod.addSite'),
                  name: 'siteAssessmentDetail'
                }
                emitTabAdd(tab)
              }`)}}}},table:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",class:"table-view-vxe-table",openCustomTable:!0},properties:generateXindexInOrder({siteFormId:{type:"string","x-hidden":!0},siteFormNumber:{type:"string","x-component":"TableButton","x-component-props":{type:"text","@click":expression(`({row}) => {
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
              }`)},"x-render-table-column":{title:"{{$t('vendorMod.siteOrderInfoV')}}",minWidth:140,customRender:!0}},vendorName:{type:"string",title:"{{$t('vendorMod.vendorName')}}","x-render-table-column":{minWidth:150}},approveStatus:{type:"string",title:"{{$t('vendorMod.orderStatus')}}","x-component":"DictSelect","x-component-props":{code:"SUPPLIER_APPROVE_STATUS_TYPE"},"x-render-table-column":{minWidth:90}},vendorCode:{type:"string",title:"{{$t('common.vendorCode')}}","x-render-table-column":{minWidth:120}},assessmentType:{type:"string",title:"{{$t('vendorMod.siteTypeV')}}","x-component":"DictSelect","x-component-props":{code:"CEEA_ASSESSMENT_TYPE"},"x-render-table-column":{minWidth:150}},reviewProcess:{type:"string",title:"{{$t('vendorMod.reviewProcess')}}","x-render-table-column":{minWidth:150}},reviewResult:{type:"string",title:"{{$t('vendorMod.certificationResult')}}","x-component":"DictSelect","x-component-props":{code:"CEEA_RESULT_TYPE"},"x-render-table-column":{minWidth:100}},reviewFormId:{type:"string","x-hidden":!0},reviewFormNumber:{type:"string","x-component":"TableButton","x-component-props":{type:"text","@click":expression(`({row}) => {
                let tab = {
                  component: quaOfReviewDetail,
                  params: {
                    flag: 'view',
                    row: row,
                    tabName: 'quaOfReviewDetail' + row.reviewFormNumber
                  },
                  title: () => app.$t('vendorMod.checkQuaOrderInfo'), // '查看资质审查单',
                  name: 'quaOfReviewDetail' + row.reviewFormNumber
                }
                emitTabAdd(tab)
              }`)},"x-render-table-column":{title:"{{$t('vendorMod.quaNum')}}",minWidth:140,customRender:!0}},createdBy:{type:"string","x-hidden":!0},createdFullName:{type:"string",title:"{{$t('common.creator')}}","x-render-table-column":{minWidth:110}},creationDate:{title:"{{$t('vendorMod.creationDate2')}}",...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.creationDate, '{y}-{m}-{d}')
              }`)},"x-render-table-column":{width:130}},lastUpdateDate:{type:"string","x-hidden":!0,"x-query-engine-sort":"desc"},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:150,fixed:"right",sortable:!1},properties:{appraisal:{type:"void","x-component":"TableButton",title:"{{$t('vendorMod.appraisal')}}","x-reactions":changeFieldVisibleByDeps([".approveStatus",".reviewResult"],"['PUBLISH'].includes($deps[0])"),"x-component-props":{type:"text","@click":expression(`({ row }) => {
                    const tab = {
                      component: siteAssessmentDetail,
                      params: {
                        flag: 'appraisal',
                        row: row,
                        siteFormId: row.siteFormId,
                        tabName: 'siteAssessmentDetail' + row.siteFormNumber || row.siteFormId
                      },
                      title: row.siteFormNumber,
                      name: 'siteAssessmentDetail' + row.siteFormNumber || row.siteFormId
                    }
                    emitTabAdd(tab)
                  }`)}},recall:{type:"void",title:"{{$t('common.recall')}}","x-component":"TableButton","x-reactions":changeFieldVisibleByDeps([".approveStatus",".reviewResult",".createdId"],"['PUBLISH'].includes($deps[0]) && $createdUserIsCurrentUserByRow($table.getRowByIndex($self.index))"),"x-component-props":{type:"text","@click":expression(`({ row }) => {
                    $queryEngine.request.save(row.siteFormId, { customizeAction: 'withdraw' })
                      .then(() => {
                        $message.success($t('common.successWithdraw'))
                        $queryEngine.state.paginationManagement.refresh()
                      })
                  }`)}},edit:{type:"void",title:"{{$t('common.edit')}}","x-component":"TableButton","x-reactions":changeFieldVisibleByDeps([".approveStatus"],"['DRAFT', 'WITHDRAW', 'REJECTED'].includes($deps[0])"),"x-component-props":{type:"text","@click":expression(`({ row }) => {
                    const tab = {
                      component: siteAssessmentDetail,
                      params: {
                        flag: 'edit',
                        row: row,
                        siteFormId: row.siteFormId,
                        tabName: 'siteAssessmentDetail' + row.siteFormNumber || row.siteFormId
                      },
                      title: row.siteFormNumber,
                      name: 'siteAssessmentDetail' + row.siteFormNumber || row.siteFormId
                    }
                    emitTabAdd(tab)
                  }`)}},doApproval:{type:"void",title:"{{$t('vendorMod.doApproval')}}","x-component":"TableButton","x-reactions":changeFieldVisibleByDeps([".approveStatus",".reviewResult"],"['SUBMITTED'].includes($deps[0])"),"x-component-props":{type:"text","@click":expression(`({ row }) => {
                    const tab = {
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
                  }`)}},delete:{type:"void",title:"{{$t('common.delete')}}","x-component":"TableButton","x-reactions":changeFieldVisibleByDeps([".approveStatus"],"$deps[0] === 'DRAFT'"),"x-component-props":{style:"margin-left: 8px",showPopconfirm:!0,"@confirm":expression(`({ row }) => {
                    $queryEngine.request.delete(row.siteFormId).then(() => {
                      $message.success($t('common.successDelete'))
                      $queryEngine.state.paginationManagement.refresh()
                    })
                  }`)}},abandon:{type:"void",title:"{{$t('common.abandon')}}","x-reactions":changeFieldVisibleByDeps([".approveStatus"],"['REJECTED', 'WITHDRAW'].includes($deps[0])"),"x-component-props":{type:"text","@click":expression(`({ row }) => {
                    const tab = {
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
                  }`)}}}}})}}}});return{__sfc:!0,emitTabAdd,app,schema,scope:{emitTabAdd,siteAssessmentDetail,quaOfReviewDetail,app},components:{},RenderEngine}}});var _sfc_render$1=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{staticClass:"contractPaymentType",attrs:{schemaKey:"siteAssessmentList",schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns$1=[],__component__$1=normalizeComponent(_sfc_main$1,_sfc_render$1,_sfc_staticRenderFns$1,!1,null,null,null,null);const SiteAssessmentListEngine=__component__$1.exports,_sfc_main={name:"SiteAssessment",components:{NavTabs},data(){return{activeTab:"SiteAssessmentListEngine",tabs:[{title:this.$t("route.siteAssessmentV"),name:"SiteAssessmentListEngine",component:SiteAssessmentListEngine,closable:!1}]}}};var _sfc_render=function(){var _vm=this,_c=_vm._self._c;return _c("NavTabs",{ref:"tabs",attrs:{"tabs-list":_vm.tabs,"cur-tab":_vm.activeTab}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const index=__component__.exports;export{index as default};
