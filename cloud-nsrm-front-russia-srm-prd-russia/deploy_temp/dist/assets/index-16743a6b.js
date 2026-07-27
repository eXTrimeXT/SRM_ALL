import{N as NavTabs}from"./index-a035e78f.js";import{ae as expression,af as i18nExpression,ag as yearMonthDaySelectorSegment,aC as requiredValidatorSegment,ai as generateXindexInOrder,al as defineComponent,am as usePageHelper,ar as defineSchemas,bM as exportExcelSegment,bN as queryFieldStatePropertyExpression,bt as changeFieldVisibleByDeps,as as RenderEngine,au as DictSelect,n as normalizeComponent}from"./index-17d0ccd5.js";import{D as DialogMainCategory,s as sitereviewplanconfirmEdit}from"./edit-engine-f6cc7fed.js";import{s as siteAssessmentDetail}from"./siteAssessmentDetailEngine-dfefa6b8.js";import"./supApi-e5726083.js";import"./vendorAccessAttachment-c3ab363b.js";/* empty css                                                                          */import"./vendorManagement-dfc64e68.js";import"./index-6af40985.js";/* empty css                                              */import"./printer-20ef2763.js";const DialogMain={type:"void","x-query-engine":{service:"sup",type:"SiteReviewPlan",actions:{save:{cascadeDeletion:!0}}},"x-component":"QueryEngine",properties:{Dialog:{type:"void",title:"{{$t('vendorMod.detail')}}","x-component":"RDialog","x-component-props":{class:"dialogMain",size:"large",cancelText:'{{ $t("common.staging") }}',okText:'{{ $t("common.submit") }}',beforeClose:expression(`(done, type,closeLoading) => {
            const siteReviewPlanCategorys = $self.query('siteReviewPlanCategorys').take().value
            if ( type === 'ok') {
              if (!siteReviewPlanCategorys || siteReviewPlanCategorys.length == 0) {
                app.$message.warning($t('vendorMod.pleaseFillInTheCategory'))
                return false
              }
              return $self.query('*.*.Dialog.form').take().submit(values => {
                values.planProcessStatus = 'NOT_STARTED'
                return $queryEngine.request.save({
                  ...values,
                  siteReviewPlanCategorys: siteReviewPlanCategorys,
                  planStatus: 'SUBMITTED'
                }).then(() => {
                  app.$message.success($t('common.success'))
                  $queryEngine.state.paginationManagement.refresh()
                  done()
                }).catch(() => {closeLoading()})
              })
            } else if ( type === 'cancel' ) {
              return $self.query('*.*.Dialog.form').take().submit(values => {
                values.planProcessStatus = 'NOT_STARTED'
                return $queryEngine.request.save({
                  ...values,
                  siteReviewPlanCategorys: siteReviewPlanCategorys,
                  planStatus: 'DRAFT'
                }).then(() => {
                  app.$message.success($t('common.success'))
                  $queryEngine.state.paginationManagement.refresh()
                  done()
                })
              })
            } else {
              done()
            }
      }`)},properties:{form:{type:"object","x-component":"FormGrid","x-component-props":{maxColumns:4,columnGap:32,rowGap:0},properties:{planName:{type:"string",title:"{{$t('vendorMod.planName')}}",required:!0,"x-decorator":"FormItem"},vendorName:{type:"string",title:"{{$t('vendorMod.vendorId')}}",required:!0,"x-component":"QuickSearchWrapper","x-component-props":{showKey:"companyName",propKey:"companyName",name:"scc_sup_company_info2","@close-quicksearch":expression(`(val, scope) => {
              if (val) {
                $values.form.vendorId = val.companyId
                $values.form.vendorName = val.companyName
                $values.form.vendorCode = val.companyCode
              } else {
                $values.form.vendorId = null
                $values.form.vendorName = null
                $values.form.vendorCode = null
              }
            }`)},"x-decorator":"FormItem"},orgId:{type:"string",title:"{{$t('vendorMod.orgName')}}","x-component":"OrganizationSelector",required:!0,"x-component-props":{"node-type":"OU",multiple:!1,"@select":expression(`(node, value) => {
              $values.form.orgId = node.organizationId
              $values.form.orgCode = node.organizationCode
              $values.form.orgName = node.organizationName
            }`)},"x-decorator":"FormItem"},planType:{type:"string",title:"{{$t('vendorMod.planType')}}",required:!0,"x-component":"DictSelect","x-component-props":{code:"CEEA_ASSESSMENT_TYPE"},"x-decorator":"FormItem"},planStartDate:{title:i18nExpression("vendorMod.planStartDate"),"x-decorator":"FormItem",...yearMonthDaySelectorSegment,...requiredValidatorSegment}}},toolbar:{type:"void","x-component":"Space","x-component-props":{style:"margin-bottom: 16px"},properties:{dialogAdd:{type:"void",title:"{{$t('common.add')}}","x-component":"RButton","x-component-props":{style:"margin-top:12px",type:"primary","@click":expression(`() => {
              $form.query("siteReviewPlanCategorys").take().componentProps.componentInstance.addRow("unshift")
            }`)}}}},siteReviewPlanCategorys:{type:"array","x-component":"RenderTable","x-component-props":{primaryKey:"reviewPlanCategoryId",cascadeDeletion:!0,preColumns:"seq",pagination:!1,editMode:"multi-row"},"x-query-engine-skip":!0,"x-query-engine-relation":"siteReviewPlanCategorys:*",properties:generateXindexInOrder({categoryCode:{type:"string",title:"{{$t('common.categoryCode')}}","x-component":"QuickSearchWrapper","x-component-props":{disabled:!1,showKey:"companyName",propKey:"companyName",name:"scc_base_purchase_category2","@close-quicksearch":expression(`(val, scope) => {
                const row = $table.getRowByIndex($self.index)
                row.categoryId = val ? val.categoryId : ''
                row.categoryCode = val ? val.categoryCode : ''
                row.categoryName = val ? val.categoryName : ''
            }`)},"x-render-table-column":{minWidth:150}},categoryName:{type:"string",title:"{{$t('common.categoryName')}}","x-render-table-column":{minWidth:120,skipEditable:!0}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{type:"text","@click":expression(`({ row }) => {
                    $table.remove($self.index)
                  }`)}}}}})}}}}},_sfc_main$1=defineComponent({__name:"list-engine",setup(__props){const{emitTabAdd,app}=usePageHelper(),schema=defineSchemas({SiteReviewPlan:{type:"void","x-query-engine":{service:"sup",actions:{paginationQuery:{immediate:!0}}},"x-decorator":"QueryEngine","x-component":"el-container","x-component-props":{class:"flex-container the_contractTemplateList_wrapper",direction:"vertical"},properties:{query:{type:"object","x-query-engine-skip":!0,"x-component":"QueryFormByQueryEngine",properties:generateXindexInOrder({vendorId:{type:"string",title:"{{$t('vendorMod.vendorId')}}","x-component":"QuickSearchWrapper","x-component-props":{showKey:"companyName",propKey:"companyId",name:"scc_sup_company_info_all"}},orgId:{type:"string",title:"{{$t('vendorMod.orgId')}}","x-component":"OrganizationSelector","x-component-props":{multiple:!1}},planType:{type:"string",title:"{{$t('vendorMod.planType')}}","x-component":"DictSelect","x-component-props":{code:"CEEA_ASSESSMENT_TYPE"}},categoryCode:{type:"string",title:"{{$t('vendorMod.categoryCode')}}","x-component":"QuickSearchWrapper","x-component-props":{showKey:"categoryName",propKey:"categoryCode",name:"scc_base_purchase_category2"}},planStatus:{type:"string",title:"{{$t('vendorMod.planStatus')}}","x-component":"DictSelect","x-component-props":{code:"planStatus"}},planProcessStatus:{type:"string",title:"{{$t('vendorMod.planProcessStatus')}}","x-component":"DictSelect","x-component-props":{code:"planProcessStatus"}}})},toolbar:{type:"void","x-component":"Space","x-component-props":{style:"margin-bottom: 16px"},properties:{add:{type:"void",title:"{{$t('common.add')}}","x-component":"RButton","x-component-props":{type:"primary","@click":expression(`() => {
                $form.query('Dialog').take(field => {
                  field.title = $t('vendorMod.siteReviewProgramManagementAdded')
                  field.component[1].visible = true
                })
                setTimeout(() => {
                  $form.query('*.*.Dialog.form').take((field) => {
                    field.reset()
                  })
                  $form.query('*.*.Dialog.siteReviewPlanCategorys').take((field) => {
                    field.reset()
                  })
                })
              }`)}},exportExcel:{type:"void","x-component":"ExportExcel","x-component-props":{...exportExcelSegment,type:"default",pageUrl:"/api-sup/api-ql/SiteReviewPlan/query",tableHeader:queryFieldStatePropertyExpression("SiteReviewPlan.table","data.columns"),dictCodes:{planType:"CEEA_ASSESSMENT_TYPE",planStatus:"planStatus",planProcessStatus:"planProcessStatus"}}}}},table:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",class:"table-view-vxe-table",openCustomTable:!0},properties:generateXindexInOrder({siteReviewPlanId:{type:"string","x-hidden":!0},planConfirmId:{type:"string","x-hidden":!0},siteFormId:{type:"string","x-hidden":!0},planName:{type:"string",title:"{{$t('vendorMod.planName')}}","x-render-table-column":{minWidth:100}},vendorName:{type:"string",title:"{{$t('vendorMod.vendorName')}}","x-render-table-column":{minWidth:120}},vendorCode:{type:"string",title:"{{$t('vendorMod.vendorCode')}}","x-render-table-column":{minWidth:120}},orgName:{type:"string",title:"{{$t('vendorMod.orgName')}}","x-render-table-column":{width:100}},categoryName:{title:"{{$t('vendorMod.viewCategory')}}","x-component":"TableButton","x-component-props":{type:"text","@click":expression(`({row}) => {
                $form.query('DialogCategory').take().setComponentProps({ visible: true })
                $form.query('*.DialogCategory.siteReviewPlanCategorys').take((field) => {
                  field.reset()
                })
                $queryEngine.request.baseRequest({
                  action: "read",
                  payload: [row.siteReviewPlanId],
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
              }`)},"x-render-table-column":{title:i18nExpression("vendorMod.categoryName"),minWidth:100,customRender:!0}},planType:{type:"string",title:"{{$t('vendorMod.planType')}}","x-component":"DictSelect","x-component-props":{code:"CEEA_ASSESSMENT_TYPE"},"x-render-table-column":{width:100}},creationDate:{title:"{{$t('vendorMod.creationDate2')}}","x-query-engine-sort":"desc",...yearMonthDaySelectorSegment,"x-render-table-column":{width:130}},planStartDate:{title:"{{$t('vendorMod.planStartDate')}}",...yearMonthDaySelectorSegment,"x-render-table-column":{width:130}},planProcessStatus:{title:"{{$t('vendorMod.planProcessStatus')}}","x-component":"DictSelect","x-component-props":{code:"planProcessStatus"},"x-render-table-column":{width:120}},planConfirmCode:{title:"{{$table.getRowByIndex($self.index)?.planStatus == 'DRAFT' ? '' : $self.value ? $self.value : $t('vendorMod.createPlanConfirm')}}","x-component":"TableButton","x-component-props":{type:"text","@click":expression(`({row}) => {
                if (row.planConfirmCode == '' || row.planConfirmCode == null) {
                  const datas = {
                    siteReviewPlanId: row.siteReviewPlanId,
                    vendorName: row.vendorName,
                    orgName: row.orgName,
                    categoryName: row.categoryName,
                    planName: row.planName,
                    planType: row.planType
                  }
                  const tab = {
                    component: siteReviewPlanConfirm,
                    params: {
                      row,
                      flag: 'add',
                      datas: datas
                    },
                    title: $t('vendorMod.planConfirmManagement'), // 计划落实管理
                    name: 'siteReviewPlanConfirm'
                  }
                  emitTabAdd(tab)
                } else {
                  const tab = {
                    component: siteReviewPlanConfirm,
                    params: {
                      row,
                      flag: 'view',
                      readOnly: true,
                    },
                    title: $t('vendorMod.planConfirmManagement'), // 计划落实管理
                    name: 'siteReviewPlanConfirm'
                  }
                  emitTabAdd(tab)
                }
              }`)},"x-render-table-column":{title:i18nExpression("vendorMod.planConfirmCode"),minWidth:130,customRender:!0}},siteReviewCode:{title:"{{$table.getRowByIndex($self.index)?.planStatus == 'DRAFT' ? '' : $self.value ? $self.value : $t('vendorMod.createSiteReviewCode')}}","x-component":"TableButton","x-component-props":{type:"text","@click":expression(`({row}) => {
                if (row?.siteReviewCode == '' || row?.siteReviewCode == null) {
                  const tab = {
                    component: siteAssessment,
                    params: {
                      flag: 'adds',
                      row
                    },
                    title: $t('vendorMod.appraisal'), // 评审
                    name: 'siteAssessment'
                  }
                  emitTabAdd(tab)
                } else {
                  const tab = {
                    component: siteAssessment,
                    params: {
                      siteFormId: row.siteFormId,
                      flag: 'view'
                    },
                    title: $t('vendorMod.appraisal'), // 评审
                    name: 'siteAssessment'
                  }
                  emitTabAdd(tab)
                }
              }`)},"x-render-table-column":{title:i18nExpression("vendorMod.siteReviewCode"),minWidth:130,customRender:!0}},planStatus:{type:"string",title:"{{$t('vendorMod.planStatus')}}","x-component":"DictSelect","x-component-props":{code:"planStatus"},"x-render-table-column":{width:120}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:150,fixed:"right",sortable:!1},properties:{edit:{type:"void",title:"{{$t('common.edit')}}","x-component":"TableButton","x-reactions":changeFieldVisibleByDeps([".planStatus"],"$deps[0] === 'DRAFT'"),"x-component-props":{type:"text","@click":expression(`({ row }) => {
                    $form.query('*.*.Dialog').take(field =>{
                      field.setComponentProps({ visible: true })
                      field.title = $t('vendorMod.siteReviewProgramManagementEditor')
                    })

                    setTimeout(() => {
                      $form.query('*.*.Dialog.form').take().setValue(row)
                      $form.query('*.*.Dialog.siteReviewPlanCategoryList').take((field) => {
                        field.reset()
                      })
                      $queryEngine.request.baseRequest({
                        action: "read",
                        payload: [row.siteReviewPlanId],
                        query: {
                          "siteReviewPlanCategorys":{
                            "*":{}
                           }
                        }
                      }).then((res) => {
                        console.log(res)
                        $form.query('*.*.Dialog.siteReviewPlanCategorys').take().setValue(res.data[0].siteReviewPlanCategorys)
                      }).catch((err) => {
                        console.log(err)
                        $message.error(err.message)
                      })
                    });
                  }`)}},submit:{type:"void",title:"{{$t('common.submit')}}","x-component":"TableButton","x-reactions":changeFieldVisibleByDeps([".planStatus"],"$deps[0] === 'DRAFT'"),"x-component-props":{type:"text","@click":expression(`({ row }) => {
                    $form.query('*.*.Dialog').take(field => {
                      field.componentProps.visible = true
                      field.title = $t('vendorMod.siteReviewProgramManagementEditor')
                    })

                    setTimeout(() => {
                      $form.query('*.*.Dialog.form').take().setValue(row)
                      $form.query('*.*.Dialog.siteReviewPlanCategorys').take((field) => {
                        field.reset()
                      })
                      $queryEngine.request.baseRequest({
                        action: "read",
                        payload: [row.siteReviewPlanId],
                        query: {
                          "siteReviewPlanCategorys":{
                            "*":{}
                           }
                        }
                      }).then((res) => {
                        console.log(res)
                        $form.query('*.*.Dialog.siteReviewPlanCategorys').take().setValue(res.data[0].siteReviewPlanCategorys)
                      }).catch((err) => {
                        console.log(err)
                        $message.error(err.message)
                      })
                    });
                  }`)}},delete:{type:"void",title:"{{$t('common.delete')}}","x-component":"TableButton","x-reactions":changeFieldVisibleByDeps([".planStatus"],"$deps[0] === 'DRAFT'"),"x-component-props":{style:"margin-left: 8px",showPopconfirm:!0,type:"text","@confirm":expression(`({ row }) => {
                    $queryEngine.request.delete(row.siteReviewPlanId).then(() => {
                       $message.success($t('common.successDelete'))
                       $queryEngine.state.paginationManagement.refresh()
                    })
                  }`)}}}}})},DialogAll:{...DialogMain},DialogCategory:{...DialogMainCategory}}}});return{__sfc:!0,emitTabAdd,app,schema,scope:{emitTabAdd,app,siteReviewPlanConfirm:sitereviewplanconfirmEdit,siteAssessment:siteAssessmentDetail},components:{Cselect:DictSelect},RenderEngine}}});var _sfc_render$1=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{staticClass:"contractPaymentType",attrs:{schemaKey:"siteReviewPlanList",schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns$1=[],__component__$1=normalizeComponent(_sfc_main$1,_sfc_render$1,_sfc_staticRenderFns$1,!1,null,null,null,null);const sitereviewplanList_engine=__component__$1.exports,_sfc_main={name:"Sitereviewplan",components:{NavTabs},data(){return{activeTab:"sitereviewplanList_engine",tabs:[{title:this.$t("vendorMod.siteReviewPlan"),name:"sitereviewplanList_engine",component:sitereviewplanList_engine,closable:!1}]}},activated(){this.activeTab==="sitereviewplanList_engine"&&this.dolayout()},methods:{dolayout(){this.$nextTick(()=>{const data={name:"sitereviewplanList_engine",methods:"dolayout",params:null,random:Math.random()};this.$store.commit("navTabs/SET_NAV_TABS_TODO",data)})},tabChange(tab){tab==="sitereviewplanList_engine"&&this.dolayout(),this.activeTab=tab}}};var _sfc_render=function(){var _vm=this,_c=_vm._self._c;return _c("NavTabs",{ref:"tabs",attrs:{"tabs-list":_vm.tabs,"cur-tab":_vm.activeTab},on:{"tab-change":_vm.tabChange}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const index=__component__.exports;export{index as default};
