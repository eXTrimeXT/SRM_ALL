import{N as NavTabs}from"./index-a035e78f.js";import{cc as formGridSegment,af as i18nExpression,ae as expression,ai as generateXindexInOrder,n as normalizeComponent,al as defineComponent,am as usePageHelper,an as useAttrs,ao as ref$1,bS as computed,as as RenderEngine,ar as defineSchemas,ag as yearMonthDaySelectorSegment,bt as changeFieldVisibleByDeps}from"./index-17d0ccd5.js";import{s as supCommonApi}from"./supApi-e5726083.js";import{f as financeInfoChangeApi}from"./vendorManagement-dfc64e68.js";import{F as FileDynamic}from"./file-dynamic-30cdd411.js";import"./file-dynamic-ab2ff377.js";import"./MainHeader-ef959a5c.js";/* empty css                                                                   */import"./file-dynamic.vue_vue_type_style_index_0_scoped_516fdfc3_lang-e128b539.js";import"./tree-utils-7df6be59.js";import"./basicSetting-f3b18103.js";import"./BaseTableBind-53264a4f.js";import"./util-6482eb24.js";import"./index-4d512f00.js";/* empty css                                                                      */import"./fileApi-3f0be128.js";const formMain={type:"object","x-query-engine-skip":!0,...formGridSegment,properties:{changeHeaderId:{type:"number","x-hidden":!0,"x-decorator":"FormItem"},changeHeaderName:{type:"string",title:i18nExpression("vendorMod.relegation.billName"),"x-component-props":{},"x-decorator":"FormItem","x-validator":{required:!0}},changeHeaderCode:{type:"string",title:i18nExpression("dataConfMod.sequenceCode"),"x-component-props":{disabled:!0},"x-decorator":"FormItem"},approveStatus:{type:"string",title:i18nExpression("dataConfMod.triggerState"),"x-component":"DictSelect","x-component-props":{code:"APPROVE_STATUS_TYPE",disabled:!0},"x-decorator":"FormItem"},orgId:{type:"number",title:i18nExpression("vendorMod.changeOrganization"),"x-decorator":"FormItem","x-component":"OrganizationSelector","x-component-props":{"read-pretty":expression("$form.readPretty"),nodeType:"OU"},"x-validator":{required:!0}},createdFullName:{type:"string",title:i18nExpression("common.creator"),"x-component-props":{disabled:!0},"x-decorator":"FormItem"},department:{type:"string",title:i18nExpression("purchaseDemand.ceeaDepartment"),"x-component-props":{},"x-decorator":"FormItem"},creationDate:{type:"string",title:i18nExpression("purchaseDemand.creationDate"),"x-component-props":{disabled:!0},"x-decorator":"FormItem"},null:{type:"void","x-decorator":"FormItem"},remark:{type:"string",title:i18nExpression("components.eio.headers.remark"),"x-component":"Input.TextArea","x-component-props":{autosize:{minRows:2,maxRows:4}},"x-decorator":"FormItem","x-decorator-props":{gridSpan:4}},advice:{type:"string",title:i18nExpression("vendorMod.advice"),"x-component-props":{autosize:{minRows:2,maxRows:4},type:"textarea"},"x-decorator":"FormItem","x-decorator-props":{gridSpan:4}}}},collapseMain={type:"void","x-component":"Collapse",properties:generateXindexInOrder({financialInforChangesInfor:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.financialInforChangesInfor")},"x-query-engine-skip":!0,properties:{form:{...formMain}}},accountingChangeDetails:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.accountingChangeDetails")},"x-query-engine-skip":!0,properties:{add:{type:"void","x-hidden":"{{$form.readPretty}}","x-component":"QuickSearchWrapper","x-component-props":{"show-key":"username",name:"scc_sup_company_info2",multiSelect:!0,showButton:!0,btnTitle:"{{$t('common.new')}}","@close-quicksearch":expression(`(val)=>{
                    let companyIdList = []
                    const orgId = $form.query('.form.orgId').take().value

                    if (!orgId) {
                      app.$message.warning($t('dataConfMod.msgInputUnit2'))
                      return false
                    }
                    try {
                      val.forEach(e => {
                        companyIdList.push(e.companyId)
                      })
                    } catch (err) {
                      companyIdList.push(val.companyId)
                    }
                    let obj = {
                      companyIdList: companyIdList,
                      orgId
                    }

                    financeInfoChangeApi.listByCompanyIdAndOrgId(obj).then(res => {
                      if (res.code == '0') {
                        if (res.data.length > 0) {
                          $form.query('.changeBeforeList').take().value = JSON.parse(JSON.stringify(res.data))
                          $form.query('.changeBeforeList').take().value.forEach((val) => {
                            val.changeFlag = 'BEFORE'
                          })
                          $form.query('.changeAfterList').take().value = JSON.parse(JSON.stringify(res.data))
                          $form.query('.changeAfterList').take().value.forEach((val) => {
                            val.changeFlag = 'AFTER'
                          })
                        } else {
                          app.$message.error($t('vendor.financialInformationIsEmpty'))
                        }
                      } else {
                        this.$message.error(res.message)
                      }
                    })
                  }`)}},beforeChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.beforeChange"}},changeBeforeList:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",class:"table-view-vxe-table",editMode:!1,pagination:!1,sortable:!1,height:"250px"},"x-query-engine-skip":!0,properties:generateXindexInOrder({changeFlag:{type:"string",default:"BEFORE","x-hidden":!0,"x-render-table-column":{minWidth:100}},financeInfoId:{type:"number",default:null,"x-hidden":!0},companyId:{type:"number",default:null,"x-hidden":!0},companyCode:{type:"string",title:i18nExpression("vendorMod.vendorCode"),"x-render-table-column":{minWidth:150}},companyName:{type:"string",title:i18nExpression("vendorMod.vendorName"),"x-render-table-column":{minWidth:150}},factoryCode:{type:"string",title:i18nExpression("vendorMod.factoryCode"),"x-render-table-column":{minWidth:100}},clearCurrency:{type:"string",title:i18nExpression("vendorMod.clearCurrency"),"x-component":"DictSelect","x-render-table-column":{minWidth:100},"x-component-props":{code:"BID_TENDER_CURRENCY"}},paymentMethod:{type:"string",title:i18nExpression("vendorMod.paymentMethod"),"x-component":"DictSelect","x-component-props":{code:"PAYMENT_METHOD"},"x-render-table-column":{minWidth:100}},paymentTerms:{type:"string",title:i18nExpression("vendorMod.paymentTerms"),"x-component":"DictSelect","x-component-props":{code:"PAYMENT_TERMS"},"x-render-table-column":{minWidth:100}}})},addAfter:{type:"void","x-hidden":"{{$form.readPretty}}","x-component":"Button","x-content":i18nExpression("common.new"),"x-component-props":{style:"margin-top:20px",type:"primary","@click":expression(`({ rowIndex }) => {
                    console.log($form.query(".changeAfterList").take().componentProps.componentInstance)
                    $form.query(".changeAfterList").take().componentProps.componentInstance.addRow("unshift",{Etype:'1',changeFlag:'AFTER'})
              }`)}},afterChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.afterChange"}},changeAfterListDele:{type:"array","x-hidden":!0},changeAfterList:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",class:"table-view-vxe-table",editMode:!0,pagination:!1,sortable:!1,height:"250px"},"x-query-engine-skip":!0,properties:generateXindexInOrder({changeFlag:{type:"string",default:"AFTER","x-hidden":!0,"x-render-table-column":{minWidth:100}},financeInfoId:{type:"number",default:null,"x-hidden":!0,"x-render-table-column":{minWidth:100}},companyId:{type:"number",default:null,"x-hidden":!0,"x-render-table-column":{minWidth:100}},companyCode:{type:"string",title:i18nExpression("vendorMod.vendorCode"),"x-read-pretty":"{{true}}","x-render-table-column":{minWidth:150}},companyName:{type:"string",title:i18nExpression("vendorMod.vendorName"),"x-render-table-column":{minWidth:150},"x-reactions":expression(`(field) => {
                    const row = $table.getRowByIndex($self.index)
                    const changeBeforeList = $form.query('.changeBeforeList').get('value')
                    let Etype = 0
                    try {
                      Etype = row.Etype
                    } catch (e) {}
                    console.log(field, 'field')
                    if (Etype == '1' && changeBeforeList.length > 0) {
                      field.setComponent('Select')
                      const changeBeforeListC = showCompany(changeBeforeList)
                      $self.dataSource = (changeBeforeListC || []).map(item => {
                        return {
                          key: item.companyId,
                          label: item.companyName,
                          value: item.companyId
                        }
                      })
                      field.setComponentProps({
                        disabled: $form.readPretty || $form.query('.form.approveStatus').take().value === 'SUBMITTED',
                        '@change':()=>{
                            changeBeforeList.forEach(e => {
                              if (e.companyId == row.companyName) {
                                row.companyName = e.companyName
                                row.companyCode = e.companyCode
                                row.companyId = e.companyId
                              }
                            })
                        }
                      })
                    } else if (Etype == '1' && changeBeforeList.length == 0) {
                      field.setComponent('QuickSearchWrapper')
                      field.setComponentProps({
                        'show-input':row.companyName,
                        'show-key':"companyName",
                        'scope-data':row,
                        name:"scc_sup_company_info2",
                        disabled: $form.readPretty,
                        '@close-quicksearch': (val)=>{
                          console.log(val)
                          row.companyName = val ? val.companyName : ''
                          row.companyCode = val ? val.companyCode : ''
                          row.companyId = val ? val.companyId : ''
                        }
                      })
                    } else {
                      field.setComponentProps({readOnly:true,disabled:true})
                    }
                }`)},factoryCode:{type:"string",title:i18nExpression("vendorMod.factoryCode"),"x-render-table-column":{minWidth:100}},clearCurrency:{type:"string",title:i18nExpression("vendorMod.clearCurrency"),"x-component":"DictSelect","x-render-table-column":{minWidth:100},"x-component-props":{code:"BID_TENDER_CURRENCY"}},paymentMethod:{type:"string",title:i18nExpression("vendorMod.paymentMethod"),"x-component":"DictSelect","x-component-props":{code:"PAYMENT_METHOD"},"x-render-table-column":{minWidth:100}},paymentTerms:{type:"string",title:i18nExpression("vendorMod.paymentTerms"),"x-component":"DictSelect","x-component-props":{code:"PAYMENT_TERMS"},"x-render-table-column":{minWidth:100}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void","x-hidden":"{{$form.readPretty}}",title:"{{$t('common.delete')}}","x-component-props":{type:"text","@click":expression(`({ row }) => {
                        if(row?.financeChangeId){
                          $table.remove($self.index)
                          $form.query('.changeAfterListDele').take().value.push({ $delete: row.financeChangeId })
                        } else {
                          $table.remove($self.index)
                        }
                      }`)}}}}})}}},relevantAttachment:{type:"void","x-query-engine-skip":!0,"x-component":"CollapseItem","x-component-props":{title:i18nExpression("bidMod.attachment")},properties:{fileUploads:{type:"array","x-component":"FileDynamic","x-component-props":{primaryKey:"sceneFileId",cascadeDeletion:!0,"scene-module-code":"SCENE_FINANCE_INFO_CHANGE_HEADER","business-id":expression("$attrs.params.row?.changeHeaderId"),editable:expression("!$form.readPretty && $form.query('.approveStatus').take()?.value != 'SUBMITTED'"),"need-init":!1}}}}})};const _sfc_main$3={name:"changeTitle",props:{language:{type:String,default:()=>""}}};var _sfc_render$3=function(){var _vm=this,_c=_vm._self._c;return _c("div",{staticClass:"changeTitle changeTitleTop"},[_c("i"),_vm._v(_vm._s(_vm.$t(_vm.language))+" ")])},_sfc_staticRenderFns$3=[],__component__$3=normalizeComponent(_sfc_main$3,_sfc_render$3,_sfc_staticRenderFns$3,!1,null,"4032f9ae",null,null);const changeTitle=__component__$3.exports,_sfc_main$2=defineComponent({__name:"edit",setup(__props){const{app,emitTabRemove,t,vendor}=usePageHelper(),attrs=useAttrs(),workflowStatus=ref$1("DRAFT"),$disabledFlag=computed(()=>!0),customUpdateButton=computed(()=>!$disabledFlag.value&&["SUPPLIER_SUBMITTED"].includes(workflowStatus.value)),viewUpdateButton=$form=>{const flag=attrs.params.flag;return["approved","view"].includes(flag)?($form.readPretty=!0,!1):!0},disabledUpdateButton=()=>!(attrs.params.flag=="view"),initButtonConfig=$form=>{setTimeout(()=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.buttonConfigInfo.save.view=viewUpdateButton($form),componentInstance.buttonConfigInfo.submit.view=viewUpdateButton($form),componentInstance.buttonConfigInfo.cancel.view=!1,componentInstance.buttonConfigInfo.close.view=!0,componentInstance.setWorkflowBusinessId(attrs.params?.row?.changeHeaderId),componentInstance.setWorkflowTabDisabled(["DRAFT"].includes(attrs.params?.row?.approveStatus)),componentInstance.setWorkflowBusinessVariables({})},50)},updateButtonConfig=$form=>{setTimeout(()=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.buttonConfigInfo.save.view=disabledUpdateButton(),componentInstance.buttonConfigInfo.submit.view=disabledUpdateButton(),componentInstance.buttonConfigInfo.cancel.view=!1,componentInstance.buttonConfigInfo.close.view=!1},50)},showCompany=arr=>{let newArr=arr,bolArr=[];return arr.length>0&&newArr.forEach(e=>{let bol=1;bolArr.forEach(u=>{e.companyId==u.companyId&&(bol=0)}),bol==1&&bolArr.push(e)}),bolArr},schema=defineSchemas({FinanceInfoChangeHeader:{type:"void","x-decorator":"QueryEngine","x-component":"el-container","x-component-props":{class:"flex-container siteReviewPlanConfirm",direction:"vertical"},"x-query-engine":{service:"sup",actions:{query:{immediate:!0,tree:!0,ready:expression(`() => {
            initButtonConfig($form)
            return $attrs.params && $attrs.params?.row?.changeHeaderId
          }`),autoFormatResult:!1,transformRequest:expression(`(data, headers) => {
            data.query = {
              "*":{},
              "fileUploads": {'*': {}},
              "financeInfoChangeList": {'*': {}},
            }
            let req = {
              "filter": {
                  "changeHeaderId": {
                      eq: $attrs.params.row.changeHeaderId
                  }
              }
            }
            data.payload = req
            return data
          }`),transformResponse:expression(`(res) => {
            const ress = JSON.parse(res)
            if (ress.code != '0') {
              app.$message.warning(ress.message)
              return false
            }
            const data = ress.data.records[0]
            const financeInfoChangeList = data.financeInfoChangeList
            const fileUploads = data.fileUploads
            setTimeout(() => {
              $form.query('.form').take().value = data
              const before = financeInfoChangeList.filter(i => i.changeFlag == "BEFORE")
              $form.query('.changeBeforeList').take().value = before
              const after = financeInfoChangeList.filter(i => i.changeFlag == "AFTER")
              $form.query('.changeAfterList').take().value = after

            })

            return ress
          }`),onSuccess:expression(`(res) => {
            $form.query('fileUploads').take(field => {
              field.value = res.records[0].fileUploads
              field.componentProps.componentInstance.reLoadFileInfo()
            })
          }`)}}},properties:{SchemaWorkflow:{type:"void","x-component":"SchemaWorkflow","x-component-props":{"business-id":expression("$attrs.params?.row?.changeHeaderId || null"),"business-type":"FINANCECHANGE","@click-handler":expression(`(type) => {
            $submits(type, $form, $queryEngine, $message, $t, $bus)
          }`),"@submit-direct":expression(`(type) => {
            $submits(type, $form, $queryEngine, $message, $t, $bus)
          }`),"@confirm":expression(`(type, comment) => {
            $submits(type, $form, $queryEngine, $message, $t, $bus)
          }`),"@close-tab":expression(`() => {
            $back($bus)
          }`),"@update-integration-mode":expression(`(integrationMode) => {
            console.log('update-integration-mode', integrationMode)
            if (integrationMode.integrationMode == "None") {
              updateButtonConfig($form)
            }
          }`)},properties:{layout:{type:"void","x-component":"FormContainer",properties:{layout:{type:"void","x-component":"FormContainer",properties:{collapse:{...collapseMain}}}}}}}}}}),$back=$bus=>{emitTabRemove(attrs.tabName),$bus.$emit("financialChange")},$submits=(type,$form,$queryEngine,$message,$t,$bus)=>{let values=$form.values.form;const changeBeforeList=$form.query(".changeBeforeList").get("value"),changeAfterList=$form.query(".changeAfterList").get("value");values.financeInfoChangeList=[...changeBeforeList,...changeAfterList],values.financeInfoChangeList=values.financeInfoChangeList.concat($form.query(".changeAfterListDele").take().value),values.fileUploads=$form.query(".fileUploads").get("value");const approveStatus=attrs.params.row?.approveStatus||null;type=="SAVE"?[null,"DRAFT"].includes(approveStatus)?(values.approveStatus="DRAFT",$queryEngine.request.save(values,{query:{"*":{}}}).then(()=>{$message.success($t("common.successSave")),$bus.$emit("financialChange"),emitTabRemove(attrs.tabName)})):$queryEngine.request.save(values,{query:{"*":{}}}).then(()=>{$message.success($t("common.successSave")),$bus.$emit("financialChange"),emitTabRemove(attrs.tabName)}):[null,"DRAFT"].includes(approveStatus)?(values.approveStatus="DRAFT",$queryEngine.request.save(values,{query:{"*":{}}}).then(res=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.setWorkflowBusinessId(res.data[0]?.changeHeaderId||null),componentInstance.setWorkflowTabDisabled(!0),componentInstance.setWorkflowBusinessVariables({}),componentInstance.handlerAfter(type.toUpperCase(),()=>{$bus.$emit("financialChange")}),setTimeout(()=>{$form.readPretty=!0,componentInstance.buttonConfigInfo.save.view=!1,componentInstance.buttonConfigInfo.submit.view=!1},100)})):$queryEngine.request.save(values,{query:{"*":{}}}).then(res=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.setWorkflowBusinessId(res.data[0]?.changeHeaderId||null),componentInstance.setWorkflowTabDisabled(!0),componentInstance.setWorkflowBusinessVariables({}),componentInstance.handlerAfter(type.toUpperCase(),()=>{$bus.$emit("financialChange")}),setTimeout(()=>{$form.readPretty=!0,componentInstance.buttonConfigInfo.save.view=!1,componentInstance.buttonConfigInfo.submit.view=!1},100)})};return{__sfc:!0,app,emitTabRemove,t,vendor,attrs,workflowStatus,$disabledFlag,customUpdateButton,viewUpdateButton,disabledUpdateButton,initButtonConfig,updateButtonConfig,showCompany,schema,$back,$submits,scope:{app,t,$attrs:attrs,$disabledFlag,emitTabRemove,initButtonConfig,$back,supCommonApi,$submits,financeInfoChangeApi,showCompany},components:{changeTitle,FileDynamic},RenderEngine}}});var _sfc_render$2=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{schemaKey:"financialInforChangesDetail",pageAttrs:_vm.$attrs,schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns$2=[],__component__$2=normalizeComponent(_sfc_main$2,_sfc_render$2,_sfc_staticRenderFns$2,!1,null,null,null,null);const editDetail=__component__$2.exports,_sfc_main$1=defineComponent({__name:"list",setup(__props){const{emitTabAdd,app}=usePageHelper(),schema=defineSchemas({FinanceInfoChangeHeader:{type:"void","x-decorator":"QueryEngine","x-query-engine":{service:"sup",actions:{paginationQuery:{immediate:!0}}},"x-component":"el-container","x-component-props":{class:"flex-container the_contractTemplateList_wrapper",direction:"vertical"},properties:{bus:{type:"void","x-component":"BusEvent","x-component-props":{eventName:"financialChange","@listener":expression(`() => {
            $queryEngine.state.paginationManagement.refresh()
          }`)}},query:{type:"object","x-query-engine-skip":!0,"x-component":"QueryFormByQueryEngine",properties:generateXindexInOrder({changeHeaderCode:{type:"string",title:"{{$t('vendorMod.inviteVendorNo')}}","x-query-engine-query-operator":"contains"},changeHeaderName:{type:"string",title:"{{$t('bidMod.documentTitle')}}","x-query-engine-query-operator":"contains"},createdBy:{type:"string",title:"{{$t('purchaseDemand.applicant')}}","x-component":"QuickSearchWrapper","x-component-props":{showKey:"nickname",propKey:"username",name:"scc_rbac_user_display"}},approveStatus:{type:"string",title:"{{$t('bidMod.status')}}","x-component":"DictSelect","x-component-props":{code:"APPROVE_STATUS_TYPE"}}})},toolbar:{type:"void","x-component":"Space","x-component-props":{style:"margin-bottom: 16px"},properties:{add:{type:"void",title:"{{$t('common.add')}}","x-component":"RButton","x-component-props":{type:"primary","@click":expression(`() => {
               const tab = {
                  component: editDetail,
                  params: {
                    flag: 'add',
                    tabName: 'addDetail'
                  },
                  title: $t('common.add'), // '新增供应商',
                  name: 'addDetail'
                }
               emitTabAdd(tab)
              }`)}}}},table:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",class:"table-view-vxe-table",openCustomTable:!0},properties:generateXindexInOrder({changeHeaderId:{type:"string","x-hidden":!0},changeHeaderCode:{type:"string",title:"{{$t('vendorMod.inviteVendorNo')}}","x-render-table-column":{width:120}},changeHeaderName:{"x-component":"TableButton","x-component-props":{type:"text","@click":expression(`({row}) => {
                editTab('view', row)
              }`)},"x-render-table-column":{title:i18nExpression("bidMod.documentTitle"),customRender:!0}},approveStatus:{type:"string",title:"{{$t('dataConfMod.triggerState')}}","x-component":"DictSelect","x-component-props":{code:"APPROVE_STATUS_TYPE"},"x-render-table-column":{width:150}},createdFullName:{type:"string",title:"{{$t('purchaseDemand.applicant')}}","x-render-table-column":{width:120}},creationDate:{title:"{{$t('purchaseDemand.creationDate')}}",...yearMonthDaySelectorSegment,"x-render-table-column":{width:130}},approveTime:{title:"{{$t('supplierRating.approvalTime')}}",...yearMonthDaySelectorSegment,"x-render-table-column":{width:130}},lastUpdateDate:{type:"string","x-hidden":!0,"x-query-engine-sort":"desc"},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList","x-component-props":{max:2},properties:{edit:{type:"void",title:"{{$t('common.edit')}}","x-reactions":changeFieldVisibleByDeps([".approveStatus"],"['DRAFT', 'WITHDRAW', 'REJECTED'].includes($deps[0])"),"x-component-props":{type:"text","@click":expression(`({ row }) => {
                    editTab('edit', row)
                  }`)}},abandon:{type:"void",title:"{{$t('common.abandon')}}","x-reactions":changeFieldVisibleByDeps([".approveStatus"],"['WITHDRAW', 'REJECTED'].includes($deps[0])"),"x-component-props":{type:"text","@click":expression(`({ row }) => {
                    editTab('approved', row)
                  }`)}},doApproval:{type:"void",title:"{{$t('vendorMod.doApproval')}}","x-reactions":changeFieldVisibleByDeps([".approveStatus"],"['SUBMITTED'].includes($deps[0])"),"x-component-props":{type:"text","@click":expression(`({ row }) => {
                    editTab('approved', row)
                  }`)}},view:{type:"void",title:"{{$t('vendorMod.check')}}","x-reactions":changeFieldVisibleByDeps([".approveStatus"],"['APPROVED'].includes($deps[0])"),"x-component-props":{type:"text","@click":expression(`({ row }) => {
                    editTab('view', row)
                  }`)}},delete:{type:"void",title:"{{$t('common.delete')}}","x-reactions":changeFieldVisibleByDeps([".approveStatus"],"['DRAFT'].includes($deps[0])"),"x-component-props":{popconfirm:{title:i18nExpression("common.confirmDeleteRow")},"@click":expression(`({ row }) => {
                    $queryEngine.request.delete(row.changeHeaderId).then(() => {
                      $message.success($t('common.successDelete'))
                      $queryEngine.state.paginationManagement.refresh()
                    })
                  }`)}}}}})}}}}),editTab=(type,row)=>{const tab={component:editDetail,params:{flag:type,row,tabName:"editDetail"+row.changeHeaderName},title:row.changeHeaderName,name:"editDetail"+row.changeHeaderName};emitTabAdd(tab)};return{__sfc:!0,emitTabAdd,app,schema,editTab,scope:{emitTabAdd,app,i18nExpression,editDetail,editTab},components:{},RenderEngine}}});var _sfc_render$1=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{staticClass:"contractPaymentType",attrs:{schemaKey:"financialInforChangesList",schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns$1=[],__component__$1=normalizeComponent(_sfc_main$1,_sfc_render$1,_sfc_staticRenderFns$1,!1,null,null,null,null);const financialInforChangesList=__component__$1.exports,_sfc_main={name:"FinancialInforChanges",components:{NavTabs},data(){return{activeTab:"financialInforChangesList",tabs:[{title:this.$t("route.financialInforChanges"),name:"financialInforChangesList",component:financialInforChangesList,closable:!1}]}},activated(){this.activeTab==="sitereviewplanList"&&this.dolayout()},methods:{dolayout(){this.$nextTick(()=>{const data={name:"sitereviewplanList",methods:"dolayout",params:null,random:Math.random()};this.$store.commit("navTabs/SET_NAV_TABS_TODO",data)})},tabChange(tab){tab==="financialInforChangesList"&&this.dolayout(),this.activeTab=tab}}};var _sfc_render=function(){var _vm=this,_c=_vm._self._c;return _c("NavTabs",{ref:"tabs",attrs:{"tabs-list":_vm.tabs,"cur-tab":_vm.activeTab},on:{"tab-change":_vm.tabChange}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const index=__component__.exports;export{index as default};
