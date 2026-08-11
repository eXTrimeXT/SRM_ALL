import{N as NavTabs}from"./index-9a7f2446.js";import{ak as defineComponent,al as usePageHelper,aq as defineSchemas,ad as expression,ah as generateXindexInOrder,ae as i18nExpression,af as yearMonthDaySelectorSegment,bD as changeFieldVisibleByDeps,ar as RenderEngine,at as DictSelect,n as normalizeComponent}from"./index-6b6051d8.js";import{D as DialogMainCategory,s as sitereviewplanconfirmEdit}from"./edit-engine-d56bd3cf.js";import{s as siteAssessmentDetail}from"./siteAssessmentDetailEngine-70d606b9.js";import"./supApi-98b2a23d.js";import"./vendorAccessAttachment-04264be9.js";/* empty css                                                                          */import"./vendorManagement-b6129894.js";import"./index-2c71d18e.js";/* empty css                                              */import"./printer-c24f03b5.js";const _sfc_main$1=defineComponent({__name:"list-engine",setup(__props){const{emitTabAdd,app}=usePageHelper(),schema=defineSchemas({SiteReviewPlanConfirm:{type:"void","x-query-engine":{service:"sup",actions:{paginationQuery:{immediate:!0,transformRequest:expression(`(data, headers) => {
              data.query = {
                '*': {}
              }
              return data
            }`)}}},"x-decorator":"QueryEngine","x-component":"el-container","x-component-props":{class:"flex-container the_contractTemplateList_wrapper",direction:"vertical"},properties:{bus:{type:"void","x-component":"BusEvent","x-component-props":{eventName:"SiteReviewPlanConfirm","@listener":expression(`() => {
            $queryEngine.state.paginationManagement.refresh()
          }`)}},query:{type:"object","x-query-engine-skip":!0,"x-component":"QueryFormByQueryEngine",properties:generateXindexInOrder({planName:{type:"string",title:"{{$t('vendorMod.planName')}}","x-query-engine-query-operator":"contains"},orgId:{type:"string",title:"{{$t('vendorMod.orgId')}}","x-component":"OrganizationSelector","x-component-props":{multiple:!1}},planType:{type:"string",title:"{{$t('vendorMod.planType')}}","x-component":"DictSelect","x-component-props":{code:"planType"}},approveStatus:{type:"string",title:"{{$t('vendorMod.planStatus')}}","x-component":"DictSelect","x-component-props":{code:"approveStatus"}}})},toolbar:{type:"void","x-component":"Space","x-component-props":{style:"margin-bottom: 16px"},properties:{add:{type:"void",title:"{{$t('common.add')}}","x-component":"RButton","x-component-props":{type:"primary","@click":expression(`() => {
                  const tab = {
                    component: sitereviewplanconfirmEdit,
                    params: {
                      flag: 'add'
                    },
                    title: $t('vendorMod.planConfirmAdd'), // 计划落实管理新增
                    name: 'sitereviewplanconfirmEdit'
                  }
                  emitTabAdd(tab)
              }`)}}}},table:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",class:"table-view-vxe-table",openCustomTable:!0},properties:generateXindexInOrder({planConfirmId:{type:"string","x-hidden":!0},planConfirmCode:{type:"string","x-component":"TableButton","x-component-props":{type:"text","@click":expression(`({row}) => {
                 const tab = {
                  component: sitereviewplanconfirmEdit,
                  params: {
                    readOnly: true,
                    row,
                    flag: 'view'
                  },
                  title: $t('vendorMod.planConfirmCheck'), // 计划落实管理查看
                  name: 'sitereviewplanconfirmEdit' + row.planConfirmId
                }
                emitTabAdd(tab)
              }`)},"x-render-table-column":{title:i18nExpression("vendorMod.planConfirmCode2"),minWidth:120,customRender:!0}},vendorName:{type:"string","x-query-engine-skip":!0,title:"{{$t('vendorMod.vendorName')}}","x-render-table-column":{minWidth:120}},vendorCode:{type:"string","x-query-engine-skip":!0,title:"{{$t('vendorMod.vendorCode')}}","x-render-table-column":{minWidth:120}},orgName:{type:"string","x-query-engine-skip":!0,title:"{{$t('vendorMod.orgName')}}","x-render-table-column":{width:100}},categoryName:{title:"{{$t('vendorMod.viewCategory')}}","x-component":"TableButton","x-query-engine-skip":!0,"x-component-props":{type:"text","@click":expression(`({row}) => {
                $form.query('DialogCategory').take().setComponentProps({ visible: true })
                $form.query('*.DialogCategory.siteReviewPlanCategoryList').take((field) => {
                  field.reset()
                })
                $queryEngine.request.baseRequest({
                  action: "read",
                  payload: [row.siteReviewPlanId],
                  type: "SiteReviewPlan",
                  query: {
                    "siteReviewPlanCategorys":{
                       "*":{}
                    }
                  }
                }).then((res) => {
                  console.log(res)
                  $form.query('*.DialogCategory.categoryList').take().setValue(res.data[0].siteReviewPlanCategorys)
                }).catch((err) => {
                  console.log(err)
                  $message.error(err.message)
                })
              }`)},"x-render-table-column":{title:i18nExpression("vendorMod.categoryName"),minWidth:100,customRender:!0}},planType:{type:"string","x-query-engine-skip":!0,title:"{{$t('vendorMod.planType')}}","x-component":"DictSelect","x-component-props":{code:"planType"},"x-render-table-column":{width:100}},planName:{type:"string",title:"{{$t('vendorMod.planName')}}","x-render-table-column":{width:100}},creationDate:{title:"{{$t('vendorMod.creationDate2')}}","x-query-engine-sort":"desc",...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.creationDate, '{y}-{m}-{d}')
              }`)},"x-render-table-column":{width:130}},personList:{title:"{{$t('vendorMod.check')}}","x-component":"TableButton","x-query-engine-skip":!0,"x-component-props":{type:"text","@click":expression(`({row}) => {
                  const tab = {
                  component: sitereviewplanconfirmEdit,
                  params: {
                    readOnly: true,
                    row,
                    flag: 'view'
                  },
                  title: $t('vendorMod.planConfirmCheck'), // 计划落实管理查看
                  name: 'sitereviewplanconfirmEdit' + row.planConfirmId
                }
                emitTabAdd(tab)
              }`)},"x-render-table-column":{title:i18nExpression("vendorMod.personList"),minWidth:130,customRender:!0}},vendorContact:{type:"string",title:"{{$t('vendorMod.vendorContact')}}","x-render-table-column":{width:130}},vendorContactTel:{type:"string",title:"{{$t('vendorMod.vendorContactTel')}}","x-render-table-column":{width:130}},planSetOutTime:{title:"{{$t('vendorMod.planSetOutTime')}}",...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.planSetOutTime, '{y}-{m}-{d}')
              }`)},"x-render-table-column":{width:130}},planVisitTime:{title:"{{$t('vendorMod.planVisitTime')}}",...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.planVisitTime, '{y}-{m}-{d}')
              }`)},"x-render-table-column":{width:130}},visitDays:{type:"string",title:"{{$t('vendorMod.visitDays')}}","x-render-table-column":{width:130}},siteReviewCode:{type:"string",title:"{{$t('评审单编码')}}","x-render-table-column":{width:130}},approveStatus:{type:"string",title:"{{$t('vendorMod.status')}}","x-component":"DictSelect","x-component-props":{code:"approveStatus"},"x-render-table-column":{width:120}},rejectReason:{type:"string",title:"{{$t('vendorMod.rejectReason')}}","x-render-table-column":{width:120}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:150,fixed:"right"},properties:{createSiteReview:{type:"void","x-component":"TableButton",title:"{{$t('vendorMod.createSiteReviewCode')}}","x-reactions":changeFieldVisibleByDeps([".approveStatus",".siteReviewCode"],"$deps[0] === 'PASS' && ($deps[1] == null || $deps[1] == '')"),"x-component-props":{type:"text","@click":expression(`({ row }) => {
                    const tab = {
                      component: siteAssessmentDetail,
                      params: {
                        readOnly: false,
                        row,
                        flag: 'adds'
                      },
                      title: $t('vendorMod.createSiteReviewCode'), // 创建评审单
                      name: 'siteAssessmentDetail'
                    }
                    emitTabAdd(tab)
                  }`)}},edit:{type:"void","x-component":"TableButton",title:"{{$t('common.edit')}}","x-reactions":changeFieldVisibleByDeps([".approveStatus"],"['DRAFT','VENDOR_REJECT','REJECT'].includes($deps[0])"),"x-component-props":{type:"text","@click":expression(`({ row }) => {
                    const tab = {
                      component: sitereviewplanconfirmEdit,
                      params: {
                        readOnly: false,
                        row,
                        flag: 'edit'
                      },
                      title: $t('vendorMod.planConfirmEdit'), // 计划落实管理编辑
                      name: 'sitereviewplanconfirmEdit' + row.planConfirmId
                    }
                    emitTabAdd(tab)
                  }`)}},publish:{type:"void","x-component":"TableButton",title:"{{$t('common.publish')}}","x-reactions":changeFieldVisibleByDeps([".approveStatus"],"['DRAFT','VENDOR_REJECT','REJECT'].includes($deps[0])"),"x-component-props":{type:"text","@click":expression(`({ row }) => {
                    // RELEASED  已发布
                    let datas = row
                    datas.approveStatus = 'RELEASED'
                    $queryEngine.request.save(datas).then(() => {
                      $message.success($t('common.successPublish'))
                      $queryEngine.state.paginationManagement.refresh()
                    })
                  }`)}},delete:{type:"void",title:"{{$t('common.delete')}}","x-component":"TableButton","x-reactions":changeFieldVisibleByDeps([".approveStatus"],"$deps[0] === 'DRAFT'"),"x-component-props":{style:"margin-left: 8px",showPopconfirm:!0,"@confirm":expression(`({ row }) => {
                    $queryEngine.request.delete(row.planConfirmId).then(() => {
                      $message.success($t('common.successDelete'))
                      $queryEngine.state.paginationManagement.refresh()
                    })
                  }`)}},approve:{type:"void",title:"{{$t('common.approve')}}","x-component":"TableButton","x-reactions":changeFieldVisibleByDeps([".approveStatus"],"$deps[0] === 'VENDOR_CONFIRMED'"),"x-component-props":{type:"text","@click":expression(`({ row }) => {
                    const tab = {
                      component: sitereviewplanconfirmEdit,
                      params: {
                        readOnly: false,
                        row,
                        flag: 'approve'
                      },
                      title: $t('vendorMod.planConfirmEdit'), // 计划落实管理编辑
                      name: 'sitereviewplanconfirmEdit' + row.planConfirmId
                    }
                    emitTabAdd(tab)
                  }`)}}}}})},DialogCategory:{...DialogMainCategory}}}});return{__sfc:!0,emitTabAdd,app,schema,scope:{emitTabAdd,app,siteAssessmentDetail,sitereviewplanconfirmEdit},components:{Cselect:DictSelect},RenderEngine}}});var _sfc_render$1=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{staticClass:"contractPaymentType",attrs:{schemaKey:"siteReviewPlanConfirmList",schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns$1=[],__component__$1=normalizeComponent(_sfc_main$1,_sfc_render$1,_sfc_staticRenderFns$1,!1,null,null,null,null);const sitereviewplanconfirmList_engine=__component__$1.exports,_sfc_main={name:"Sitereviewplanconfirm",components:{NavTabs},data(){return{activeTab:"sitereviewplanconfirmList_engine",tabs:[{title:this.$t("vendorMod.planConfirmManagement"),name:"sitereviewplanconfirmList_engine",component:sitereviewplanconfirmList_engine,closable:!1}]}},activated(){this.activeTab==="sitereviewplanconfirmList_engine"&&this.dolayout()},methods:{dolayout(){this.$nextTick(()=>{const data={name:"sitereviewplanconfirmList_engine",methods:"dolayout",params:null,random:Math.random()};this.$store.commit("navTabs/SET_NAV_TABS_TODO",data)})},tabChange(tab){tab==="sitereviewplanconfirmList_engine"&&this.dolayout(),this.activeTab=tab}}};var _sfc_render=function(){var _vm=this,_c=_vm._self._c;return _c("NavTabs",{ref:"tabs",attrs:{"tabs-list":_vm.tabs,"cur-tab":_vm.activeTab},on:{"tab-change":_vm.tabChange}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const index=__component__.exports;export{index as default};
