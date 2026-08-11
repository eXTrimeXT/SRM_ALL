import{N as NavTabs}from"./index-9a7f2446.js";import{ak as defineComponent,al as usePageHelper,am as useAttrs,an as ref$1,bY as computed,aq as defineSchemas,ad as expression,ae as i18nExpression,aD as requiredValidatorSegment,af as yearMonthDaySelectorSegment,ah as generateXindexInOrder,ar as RenderEngine,bC as toJS,n as normalizeComponent,bD as changeFieldVisibleByDeps}from"./index-6b6051d8.js";import{a as crossOrgImport}from"./supApi-98b2a23d.js";import{F as FileDynamic}from"./file-dynamic-25a093c4.js";import"./file-dynamic-7fc2d358.js";import"./MainHeader-2d842985.js";/* empty css                                                                   */import"./file-dynamic.vue_vue_type_style_index_0_scoped_516fdfc3_lang-f2c1a82d.js";import"./tree-utils-7df6be59.js";import"./basicSetting-fc46a2d9.js";import"./BaseTableBind-b1f76fc9.js";import"./util-1e55288f.js";import"./index-4d512f00.js";/* empty css                                                                      */import"./fileApi-05bbbbcc.js";const _sfc_main$2=defineComponent({__name:"edit_engine",setup(__props){const{emitTabRemove,emitTabAdd,t,app}=usePageHelper();let attrs=useAttrs();const workflowStatus=ref$1("DRAFT"),$disabledFlag=computed(()=>!!["view","approval","manage"].includes(attrs.params.flag)),viewUpdateButton=computed(()=>["DRAFT","REJECTED","WITHDRAW"].includes(workflowStatus.value)),disabledUpdateButton=computed(()=>["APPROVING"].includes(workflowStatus.value)),initButtonConfig=$form=>{setTimeout(()=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.buttonConfigInfo.save.view=viewUpdateButton.value,componentInstance.buttonConfigInfo.submit.view=viewUpdateButton.value,componentInstance.buttonConfigInfo.save.disabled=disabledUpdateButton.value,componentInstance.buttonConfigInfo.submit.disabled=disabledUpdateButton.value,componentInstance.buttonConfigInfo.cancel.view=!$disabledFlag.value,componentInstance.buttonConfigInfo.close.view=$disabledFlag.value},50)},updateButtonConfig=$form=>{setTimeout(()=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.buttonConfigInfo.save.view=viewUpdateButton.value,componentInstance.buttonConfigInfo.submit.view=viewUpdateButton.value,componentInstance.buttonConfigInfo.save.disabled=disabledUpdateButton.value,componentInstance.buttonConfigInfo.submit.disabled=disabledUpdateButton.value,componentInstance.setWorkflowBusinessId($form.values.importId),componentInstance.setWorkflowTabDisabled(["DRAFT"].includes($form.values.importStatus)),componentInstance.setWorkflowBusinessVariables({})},50)},$selectHandler=(node,value,$table,$self)=>{const row=$table.getRowByIndex($self.index);row.orgId=node?node.organizationId:null,row.orgCode=node?node.organizationCode:"",row.orgName=node?node.organizationName:null,node&&node.organizationId?crossOrgImport.getBuByOrgId(node.organizationId).then(data=>{row.division=data.data.organizationCode}).catch(err=>{}):row.division=null},$saveBill=(type,$form,$queryEngine,$confirm,$message,$bus)=>{const values=$form.values;type==="SAVE"?$form.validate().then(()=>{$submitData("save",values,$form,$queryEngine,$confirm,$message,$bus)}):$form.validate().then(()=>{$submitData("submit",values,$form,$queryEngine,$confirm,$message,$bus)})},$submitData=(type,$values,$form,$queryEngine,$confirm,$message,$bus)=>{const form=toJS($values);if(delete form.categoryList,!form.vendorImportDetails.length){$message.warning(i18nExpression("vendorMod.atLeastOneBusiness"));return}form.status||(form.status="DRAFT"),$queryEngine.request.save(form).then(res=>{if($message.success(t("common.successSave")),res.data&&res.data.length){let importId=res.data[0].importId;$form.values.importId=importId,$queryEngine.request.read(importId).then(()=>{if(type==="submit"){const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.setWorkflowBusinessId(importId),componentInstance.setWorkflowTabDisabled(!1),componentInstance.setWorkflowBusinessVariables({}),componentInstance.handlerAfter(type.toUpperCase(),()=>{emitTabRemove(attrs.tabName),$bus.$emit("VendorImport")})}else emitTabRemove(attrs.tabName),$bus.$emit("VendorImport")})}})},scope={emitTabRemove,emitTabAdd,app,$selectHandler,$saveBill,$submitData,$crossOrgImport:crossOrgImport,initButtonConfig,workflowStatus,updateButtonConfig,$disabledFlag},components={FileDynamic},schema=defineSchemas({VendorImport:{type:"void","x-component":"el-container","x-component-props":{class:"flex-container",direction:"vertical"},"x-decorator":"QueryEngine","x-query-engine":{service:"sup",actions:{save:{transformRequest:expression(`(data,headers) => {
            data.query['*'] = {}
            data.query['vendorImportDetails'] = {
              '*':{}
            }
            return data
          }`)},read:{immediate:!0,ready:expression(`() => {
            initButtonConfig($form)
            let id = $attrs.params.row.importId
            $form.values.importId = id
            return !!id
          }`),transformRequest:expression(`(data,headers) => {
            data.payload = [$form.values.importId]
            data.query['*'] = {}
          }`),onSuccess:expression(`(res) => {
            console.log('res:::',res)
            $form.readPretty = $readOnly
            const value = res.data[0]
            workflowStatus.value = value.importStatus
            const {vendorImportDetails,fileUploads,...rest} = value
            $form.setValues({
              ...rest,
              fileUploads
            })
            updateButtonConfig($form)
            if(value.vendorId && value.oldOrgId){
              $crossOrgImport.listOrgCategoryByParam({
                companyId:value.vendorId,
                orgId:value.oldOrgId
              }).then(result => {
                $form.values.categoryList = result.data || []
              }).catch(err => console.log(err))
            }
            $form.query('fileUploads').take(field => {
              field.componentProps.componentInstance.reLoadFileInfo()
            })
          }`)}}},properties:{SchemaWorkflow:{type:"void","x-component":"SchemaWorkflow","x-component-props":{"business-id":expression("$attrs.params.row?.importId || null"),"business-type":"vendorImport","ref-name":"workflowMulti","button-custom":expression("{}"),"@click-handler":expression(`(type) => {
            console.log('click-handler', type, $form, $confirm, $message)
            $saveBill(type,$form,$queryEngine,$confirm,$message,$bus)
          }`),"@submit-direct":expression(`(type) => {
            console.log('submit-direct', type)
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus)
          }`),"@confirm":expression(`(type, comment) => {
            console.log('confirm', type)
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus)
          }`),"@close-tab":expression(`() => {
            emitTabRemove($attrs.tabName)
            $bus.$emit('VendorImport')
          }`)},"x-reactions":expression(`field => {
          if(!$values.importId) return
          $queryEngine.request.baseRequest({
              type:'VendorImportDetail',
              action:'getDetail',
              payload:[$values.importId],
              query:{
                '*':{}
              }
            }).then(response => {
              console.log('response:::',response)
              $form.setValues({
                vendorImportDetails:response.data
              })
            })
        }`),properties:{collapse:{type:"void","x-component":"Collapse","x-component-props:":{defaultOpenPanelCount:1},properties:{vendorExpansion:{type:"void","x-query-engine-skip":!0,"x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.vendorExpansion")},properties:{vendorImport:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:4,columnGap:32,rowGap:0},properties:{vendorId:{type:"number","x-hidden":!0},vendorName:{type:"string","x-decorator":"FormItem",title:i18nExpression("common.vendorName"),"x-component":"QuickSearchWrapper","x-component-props":{disabled:expression("$readOnly"),"read-pretty":"{{$readOnly}}","show-key":"companyName","prop-key":"companyName",name:"scc_sup_company_info2","@close-quicksearch":expression(`(val) => {
                            console.log('val',val)
                            let {companyId,companyCode,companyName} = val || {}
                            if(companyId) {
                              $values.vendorId = companyId
                              $values.vendorCode = companyCode
                              $values.vendorName = companyName
                            }
                          }`)},...requiredValidatorSegment},importNum:{type:"string","x-decorator":"FormItem",title:i18nExpression("vendorMod.importNum"),"x-component-props":{disabled:!0}},createdUserName:{type:"string","x-decorator":"FormItem",title:i18nExpression("common.creator"),"x-component-props":{disabled:!0}},oldOrgId:{type:"string","x-decorator":"FormItem",title:i18nExpression("vendorMod.oldOrg"),"x-component":"Select","x-component-props":{"@change":expression(`(val) => {
                            let options = $self.dataSource
                            if(val){
                              let obj = options.find(item => item.value === val) || {}
                              $values.oldOrgCode = obj.code
                              $values.oldOrgName = obj.label
                              if(!$values.vendorId) return
                              let data = {
                                companyId:$values.vendorId,
                                orgId:val
                              }
                              $crossOrgImport.listOrgCategoryByParam(data).then(result => {
                                $form.values.categoryList = result.data || []
                              }).catch(err => console.log(err))
                            }
                          }`)},"x-reactions":expression(`(field) => {
                          const vendorId = field.query('vendorId').get('value')
                          if(!vendorId) return
                          $crossOrgImport.getOrgByVendorId(vendorId).then(res => {
                            $self.dataSource = (res.data || []).map(item => {
                              return {
                                value: item.orgId,
                                label: item.orgName,
                                code: item.orgCode
                              }
                            })
                          })
                          .catch(err => {
                            console.log(err)
                          })
                        }`),...requiredValidatorSegment},importStatus:{type:"string",default:"DRAFT","x-decorator":"FormItem",title:i18nExpression("common.status"),"x-component":"DictSelect","x-component-props":{code:"VENDORIMPORTSTATUS",disabled:!0}},creationDate:{...yearMonthDaySelectorSegment,"x-decorator":"FormItem",title:i18nExpression("common.creationTime"),"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],disabled:!0}},importExplain:{type:"string","x-decorator":"FormItem",title:i18nExpression("vendorMod.vendorImportExplain"),"x-component-props":{type:"textarea",disabled:expression("$readOnly")}}}}}},importOrg:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.importOrg")},properties:{toolbar:{type:"void","x-component":"Space","x-component-props":{style:"margin-bottom:10px"},properties:{add:{type:"void",title:"{{$t('common.new')}}","x-component":"RButton","x-component-props":{type:"primary",disabled:"{{$readOnly}}","@click":expression(`() => {
                            let {vendorId,oldOrgId} = $form.values
                            if(!vendorId || !oldOrgId){
                              $message.warning($t('vendorMod.msgVendorAndOldOrg'))
                              return
                            }
                            $form.query('vendorImportDetails').take(field => {
                              field.value.push({
                                importId: null,
                                importDetailId: null,
                                orgId: null,
                                orgCode: null,
                                orgName: null,
                                division: null
                              })
                            })
                          }`)}}}},vendorImportDetails:{type:"array","x-query-engine-skip":!0,"x-query-engine-relation":"vendorImportDetails:*","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,pagination:!1,maxHeight:"58vh",sortable:!1},properties:generateXindexInOrder({orgId:{type:"string",title:"{{$t('vendorMod.importOU')}}","x-render-table-column":{minWidth:200},"x-component":"OrganizationSelector","x-component-props":{"node-type":"OU","parent-id":-1,placeholder:"{{$t('common.pleaseSelect')}}",scope:expression("$table.getRowByIndex($self.index)"),"@select":expression("(node,value) => $selectHandler(node,value,$table,$self)")}},division:{type:"string",title:"{{$t('vendorMod.buName')}}","x-render-table-column":{minWidth:200},"x-read-pretty":!0},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:80},"x-component":"RenderTableButtonList","x-reactions":expression(`(field) => {
                          field.visible = !$readOnly
                        }`),properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{type:"text","@click":expression(`({row,rowIndex}) => {
                                $table.remove(rowIndex)
                                // if(row.importDetailId){
                                //   $crossOrgImport.deleteOneList(row.importDetailId).then(res => {
                                //     $table.remove(rowIndex)
                                //   }).catch(err => console.log(err))
                                // }else{
                                //   $table.remove(rowIndex)
                                // }
                              }`)}}}}})}}},expandCate:{type:"void","x-component":"CollapseItem","x-component-props":{title:"{{$t('vendorMod.expandCate')}}"},properties:{categoryList:{type:"array","x-query-engine-skip":!0,"x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!1,pagination:!1,maxHeight:"58vh",sortable:!1},properties:generateXindexInOrder({categoryName:{type:"string",title:"{{$t('common.category')}}","x-render-table-column":{minWidth:200}}})}}},accessory:{type:"void","x-component":"CollapseItem","x-component-props":{title:"{{$t('vendorMod.relegation.accessory')}}"},properties:{fileUploads:{type:"array","x-query-engine-relation":"fileUploads:*","x-component":"FileDynamic","x-component-props":{"scene-module-code":"SCENE_ORG_IMPORT_ATTACHMENT","business-id":"{{$attrs.params.row.importId}}",editable:"{{!$readOnly}}","need-init":!0}}}}}}}}}}});return{__sfc:!0,emitTabRemove,emitTabAdd,t,app,attrs,workflowStatus,$disabledFlag,viewUpdateButton,disabledUpdateButton,initButtonConfig,updateButtonConfig,$selectHandler,$saveBill,$submitData,scope,components,schema,RenderEngine}}});var _sfc_render$2=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{schemaKey:"crossOrgImportDetail",pageAttrs:_vm.$attrs,schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns$2=[],__component__$2=normalizeComponent(_sfc_main$2,_sfc_render$2,_sfc_staticRenderFns$2,!1,null,null,null,null);const Edit=__component__$2.exports,_sfc_main$1=defineComponent({__name:"list_engine",setup(__props){const schema=defineSchemas({VendorImport:{type:"void","x-query-engine":{service:"sup",actions:{paginationQuery:{immediate:!0}}},"x-decorator":"el-container","x-decorator-props":{class:"flex-container the_dictionary_wrapper",direction:"vertical"},"x-component":"QueryEngine",properties:{query:{type:"object","x-query-engine-skip":!0,"x-component":"QueryFormByQueryEngine",properties:generateXindexInOrder({importNum:{type:"string",title:"{{$t('vendorMod.importNum')}}","x-query-engine-query-operator":"contains"},vendorId:{type:"string",title:"{{$t('common.vendorName')}}","x-component":"QuickSearchWrapper","x-component-props":{name:"scc_sup_company_info_all",showKey:"companyName",propKey:"companyId"}},importStatus:{type:"string",title:"{{$t('vendorMod.orderStatus')}}","x-component":"DictSelect","x-component-props":{code:"VENDORIMPORTSTATUS"}}})},toolbar:{type:"void","x-component":"Space","x-component-props":{style:"margin-bottom: 16px"},properties:{add:{type:"void",title:"{{$t('common.add')}}","x-component":"RButton","x-component-props":{type:"primary","@click":'{{() => $edit({},"add")}}'}}}},table:{type:"array","x-component":"RenderTable","x-component-props":{class:"table-view-vxe-table",style:"flex:1",preColumns:"seq",openCustomTable:!0},properties:generateXindexInOrder({importId:{type:"number","x-hidden":!0,"x-query-engine-primary-key":!0},vendorId:{type:"number","x-hidden":!0},lastUpdateDate:{type:"string","x-hidden":!0,"x-query-engine-sort":"desc"},importStatus:{type:"string",title:"{{$t('vendorMod.orderStatus')}}","x-component":"DictSelect","x-component-props":{code:"VENDORIMPORTSTATUS"},"x-render-table-column":{minWidth:90}},vendorCode:{type:"string",title:"{{$t('common.vendorCode')}}","x-render-table-column":{minWidth:150}},vendorName:{type:"string",title:"{{$t('common.vendorName')}}","x-render-table-column":{minWidth:150}},importNum:{type:"string","x-component":"TableButton","x-component-props":{type:"text","@click":expression('({row}) => $edit(row,"view")')},"x-render-table-column":{title:"{{$t('vendorMod.importNum')}}",minWidth:150,customRender:!0}},createdFullName:{type:"string",title:"{{$t('common.creator')}}","x-render-table-column":{minWidth:120}},creationDate:{...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.creationDate, '{y}-{m}-{d}')
              }`)},title:"{{$t('common.creationTime')}}","x-render-table-column":{minWidth:120}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:204,fixed:"right"},"x-component":"RenderTableButtonList","x-component-props":{max:2},properties:{edit:{type:"void",title:"{{$t('common.edit')}}","x-component-props":{code:"sup:crossOrgImportList:edit","@click":expression('({row}) => $edit(row, "edit")')},"x-reactions":changeFieldVisibleByDeps([".importStatus"],"['DRAFT', 'REJECTED', 'WITHDRAW'].includes($deps[0])")},delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{code:"sup:crossOrgImportList:delete",popconfirm:{title:i18nExpression("common.confirmDeleteRow")},"@click":expression("({row}) => $delete(row, $queryEngine)")},"x-reactions":changeFieldVisibleByDeps([".importStatus"],"['DRAFT'].includes($deps[0])")}}}})}}}}),{emitTabAdd,t}=usePageHelper(),$edit=(row,flag)=>{let name,title;flag==="add"?(name="crossOrgImportDetail",title=t("vendorMod.addCrossImport")):flag==="edit"?(name="crossOrgImportDetail"+row.importNum||row.importId,title=row.importNum):(name="crossOrgImportDetail"+row.importNum,title=row.importNum),emitTabAdd({component:Edit,params:{row,flag,tabName:name},title,name})},$delete=(row,queryEngine)=>{queryEngine.request.baseRequest({type:"VendorImport",action:"delete",payload:[{$delete:row.importId,vendorImport:[{$delete:"*"}],fileUploads:[{$delete:"*"}]}],query:{"*":{}}}).then(()=>{queryEngine.state.paginationManagement.refresh()})};return{__sfc:!0,schema,emitTabAdd,t,$edit,$delete,scope:{$edit,$delete},RenderEngine}}});var _sfc_render$1=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{schemaKey:"crossOrgImportList",pageAttrs:_vm.$attrs,schema:_setup.schema,scope:_setup.scope}})},_sfc_staticRenderFns$1=[],__component__$1=normalizeComponent(_sfc_main$1,_sfc_render$1,_sfc_staticRenderFns$1,!1,null,null,null,null);const crossOrgImportList=__component__$1.exports,_sfc_main={name:"CrossOrgImport",components:{NavTabs},data(){return{activeTab:"crossOrgImportList",tabs:[{title:()=>this.$t("route.crossOrgImport"),name:"crossOrgImportList",component:crossOrgImportList,closable:!1}]}}};var _sfc_render=function(){var _vm=this,_c=_vm._self._c;return _c("NavTabs",{ref:"tabs",attrs:{"tabs-list":_vm.tabs,"cur-tab":_vm.activeTab}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const index=__component__.exports;export{index as default};
