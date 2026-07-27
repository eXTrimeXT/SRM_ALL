import{N as NavTabs}from"./index-a035e78f.js";import{al as defineComponent,am as usePageHelper,an as useAttrs,ar as defineSchemas,ae as expression,ai as generateXindexInOrder,af as i18nExpression,cc as formGridSegment,ag as yearMonthDaySelectorSegment,as as RenderEngine,bs as toJS,cj as bus,n as normalizeComponent,bM as exportExcelSegment,bN as queryFieldStatePropertyExpression,bt as changeFieldVisibleByDeps,c7 as buttonListItemVisibleByPermission}from"./index-17d0ccd5.js";import{F as FileDynamic}from"./file-dynamic-30cdd411.js";import{c as throttle}from"./util-a92f9f8e.js";import"./file-dynamic-ab2ff377.js";import"./MainHeader-ef959a5c.js";/* empty css                                                                   */import"./file-dynamic.vue_vue_type_style_index_0_scoped_516fdfc3_lang-e128b539.js";import"./tree-utils-7df6be59.js";import"./basicSetting-f3b18103.js";import"./BaseTableBind-53264a4f.js";import"./util-6482eb24.js";import"./index-4d512f00.js";/* empty css                                                                      */import"./fileApi-3f0be128.js";const _sfc_main$2=defineComponent({__name:"edit-engine",setup(__props){const{emitTabAdd,emitTabRemove,t:$t,app,vendor}=usePageHelper(),$attrs=useAttrs(),$saveBill=throttle(async(type,$form,$queryEngine,$message,$bus)=>{const values=$form.values;type==="SAVE"?$submitData(type,values,$form,$queryEngine):type==="SUBMIT"&&$form.validate().then(()=>{let tips="提交审批后，供应商还是正式黑名单状态，审批通过后，供应商进入合格供应商，原来的组织及品类更新为原来的合格状态，不需重新引入。";app.$confirm(tips,"是否确认提交",{confirmButtonText:"确定",cancelButtonText:"取消",type:"warning"}).then(()=>{$submitData(type,values,$form,$queryEngine)})}).catch(err=>{})},300),$submitData=(type,$values,$form,$queryEngine)=>{const form=toJS($values);return $attrs.params.flag==="add"&&(form.approveStatus="DRAFT"),$queryEngine.request.baseRequest({type:"BlackRescind",lang:"zh-cn",loading:!0,tree:!0,payload:[form],query:{"*":{},blackRescindCompanyList:{"*":{}},fileUploads:{"*":{}}},action:"save"}).then(res=>{if(res.data&&res.data.length>0){const datas=res.data[0];if(app.$message.success($t("common.successSave")),type==="SUBMIT"){const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.setWorkflowBusinessId(datas.rescindId||""),componentInstance.setWorkflowTabDisabled(!1),componentInstance.setWorkflowBusinessVariables({}),componentInstance.handlerAfter(type.toUpperCase(),()=>{$cancel()}),setTimeout(()=>{$form.readPretty=!0,componentInstance.buttonConfigInfo.save.view=!1,componentInstance.buttonConfigInfo.submit.view=!1},100)}else $form.setValues(datas),$queryEngine.request.read();bus.$emit("BlackRescind")}})},$cancel=()=>{emitTabRemove($attrs.tabName),bus.$emit("BlackRescind")},initButtonConfig=$form=>{setTimeout(()=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance,viewUpdateButton=$form.query("state").get("data").viewUpdateButton;componentInstance.buttonConfigInfo.save.view=viewUpdateButton,componentInstance.buttonConfigInfo.submit.view=viewUpdateButton,componentInstance.buttonConfigInfo.cancel.view=viewUpdateButton,componentInstance.buttonConfigInfo.close.view=!viewUpdateButton},50)},updateButtonConfig=$form=>{setTimeout(()=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance,viewUpdateButton=$form.query("state").get("data").viewUpdateButton;componentInstance.buttonConfigInfo.save.view=viewUpdateButton,componentInstance.buttonConfigInfo.submit.view=viewUpdateButton,componentInstance.buttonConfigInfo.cancel.view=viewUpdateButton,componentInstance.buttonConfigInfo.close.view=!viewUpdateButton,componentInstance.setWorkflowBusinessId($form.values.rescindId),componentInstance.setWorkflowTabDisabled($form.query("state").get("data").orderStatus==="DRAFT")},50)},scope={$attrs,app,emitTabRemove,$saveBill,updateButtonConfig,initButtonConfig},components={FileDynamic},schema=defineSchemas({state:{type:"void","x-component":"Fragment","x-hidden":!0,"x-data":{isSrmCompany:!1,viewUpdateButton:!0,orderStatus:"DRAFT"}},BlackRescind:{type:"void","x-decorator":"QueryEngine","x-component":"el-container","x-component-props":{class:"flex-container",direction:"vertical"},"x-query-engine":{service:"sup",actions:{read:{immediate:!0,tree:!0,ready:expression(`() => {
            $form.readPretty = $attrs.params.flag === 'view'
            initButtonConfig($form)

            return $attrs.params.row.rescindId
          }`),transformRequest:expression(`(data, headers) => {
            data.payload = [$attrs?.params?.row?.rescindId || $form.values.rescindId || '']
            data.query = {
              '*': {},
              blackRescindCompanyList: {'*': {}},
              fileUploads: {'*': {}}
            }
            return data
          }`),onSuccess:expression(`(res) => {
            let detailData = res.data[0]
            $form.query('state').get('data').orderStatus = detailData.approveStatus

            $form.query('state').get('data').viewUpdateButton = ['DRAFT', 'REJECTED', 'WITHDRAW'].includes(detailData.approveStatus) || $attrs.params.flag === 'add'
            updateButtonConfig($form)

            $form.setValues({
              ...detailData
            })
            // 附件
            $form.query('fileUploads').take(field => {
              field.componentProps.componentInstance.reLoadFileInfo()
            })
          }`)},save:{transformRequest:expression(`(data, headers) => {
             data.query['*'] = {}
             return data
          }`),onSuccess:expression(`(res) => {

          }`),cascadeDeletion:!0}}},properties:{SchemaWorkflow:{type:"void","x-component":"SchemaWorkflow","x-component-props":{"business-id":expression("$form.values.blackId || null"),"business-type":"BlackRescind","button-custom":expression("{}"),"@click-handler":expression(`(type) => {
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus)
          }`),"@submit-direct":expression(`(type) => {
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus)
          }`),"@confirm":expression(`(type, comment) => {
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus)
          }`),"@close-tab":expression(`() => {
            emitTabRemove($attrs.tabName)
          }`),"@update-integration-mode":expression(`(integrationMode) => {
            updateButtonConfig($form)
          }`)},properties:{collapse:{type:"void","x-component":"Collapse","x-read-pretty":expression("$form.readPretty"),properties:generateXindexInOrder({baseInfo:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("black.blackSecureDetail")},"x-query-engine-skip":!0,properties:{layout:{type:"void",...formGridSegment,properties:{rescindName:{type:"string",title:i18nExpression("vendorMod.relegation.billName"),"x-decorator":"FormItem","x-validator":{required:!0}},approveStatus:{type:"string",title:i18nExpression("vendorMod.relegation.documentStatus"),"x-component":"DictSelect","x-component-props":{code:"APPROVE_STATUS_TYPE",disabled:!0},"x-decorator":"FormItem"},createdBy:{type:"string","x-decorator":"FormItem",title:i18nExpression("common.creator"),"x-component-props":{disabled:!0}},creationDate:{type:"string","x-decorator":"FormItem",title:i18nExpression("common.creationTime"),"x-component-props":{disabled:!0}},rescindContent:{type:"string","x-decorator":"FormItem",title:i18nExpression("vendorMod.relegation.sketch"),"x-component-props":{type:"textarea",autosize:expression("{ minRows: 3, maxRows: 4}")},"x-decorator-props":{gridSpan:4},"x-validator":{required:!0}}}}}},rangeList:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("black.rangeList")},"x-query-engine-skip":!0,properties:{add:{type:"void","x-hidden":"{{$form.readPretty}}","x-component":"QuickSearchWrapper","x-component-props":{style:"margin:0 0 15px 0","show-key":"username",name:"scc_sup_company_info_is_black",multiSelect:!0,showButton:!0,btnTitle:"{{$t('bidMod.addVendor')}}","@close-quicksearch":expression(`(val)=>{
                      let companyNameList = []
                      try {
                        val.forEach(e => {
                          companyNameList.push(e.companyName)
                        })
                      } catch (err) {
                        companyNameList.push(val.companyName)
                      }
                      console.log(companyNameList, 'companyNameList')
                      $queryEngine.request.baseRequest({
                          type: 'BlackCompany',
                          lang: 'zh-cn',
                          loading: true,
                          tree: true,
                          "query": {
                            "*": {}
                          },
                          "payload": {
                            "filter": {
                              "companyName": {
                                "in": companyNameList
                              }
                            },
                            page: {
                              pageNum: 1,
                              pageSize: 15,
                              sort: "lastUpdateDate desc"
                            }
                          },
                          action: 'query'
                        }).then((res) => {
                          let datas = []
                          if (res.data) {
                            res.data.forEach((e) => {
                              if (datas.length<1) {
                                datas.push(e)
                              }
                              // 去重
                              let bol = 1
                              datas.forEach((eD) => {
                                if (e.companyName == eD.companyName) {
                                  bol = 0
                                }
                              })
                              if (bol) {
                                  datas.push(e)
                              }
                            })
                          }
                          if (val) {
                            // 插入经办人跟截止时间
                            val.forEach((eVal) => {
                              const eDatas = datas.find(item => item.companyName == eVal.companyName)
                              console.log(eDatas)
                              eVal.endDate = eDatas?.endDate
                              eVal.agent = eDatas?.createdFullName
                            })
                          }
                          $form.query('.blackRescindCompanyList').take().value = val
                        })
                    }`)}},blackRescindCompanyList:{type:"array","x-component":"RenderTable","x-component-props":{primaryKey:"rescindCompanyId",cascadeDeletion:!0,preColumns:"seq",class:"table-view-vxe-table",editMode:!1,pagination:!1,sortable:!1,height:"250px"},"x-query-engine-skip":!0,properties:generateXindexInOrder({companyId:{type:"number",default:null,"x-hidden":!0},companyName:{type:"string",title:i18nExpression("vendorMod.vendorName"),"x-render-table-column":{minWidth:150}},companyCode:{type:"string",title:i18nExpression("vendorMod.vendorCode"),"x-render-table-column":{minWidth:150}},lcCode:{type:"string",title:i18nExpression("统一信用代码"),"x-render-table-column":{minWidth:150}},legalPerson:{type:"string",title:i18nExpression("法人代表"),"x-render-table-column":{minWidth:150}},supplierType:{type:"string",title:i18nExpression("供应商类型"),"x-component":"DictSelect","x-render-table-column":{minWidth:150},"x-component-props":{code:"SUPPLIER_TYPE"}},companyType:{type:"string",title:i18nExpression("企业性质"),"x-component":"DictSelect","x-render-table-column":{minWidth:150},"x-component-props":{code:"COMPANY_NATURE"}},agent:{type:"string",title:i18nExpression("经办人"),"x-render-table-column":{minWidth:150}},endDate:{title:i18nExpression("黑名单截止日期"),...yearMonthDaySelectorSegment,"x-render-table-column":{minWidth:150}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{disabled:expression("$form.readPretty"),type:"text","@click":expression(`({ row }) => {
                                $table.remove($self.index)
                            }`)}}}}})}}},fileUploadsCollapse:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("sourcingBuyer.attach")},"x-query-engine-skip":!0,properties:{fileUploads:{type:"array","x-query-engine-relation":"fileUploads:*","x-component":"FileDynamic","x-component-props":{"scene-module-code":"SCENE_BLACK_RESCIND_ATTACHMENT","business-id":"{{$attrs.params.row.importId}}",editable:"{{!$readOnly}}","need-init":!0}}}}})}}}}}});return{__sfc:!0,emitTabAdd,emitTabRemove,$t,app,vendor,$attrs,$saveBill,$submitData,$cancel,initButtonConfig,updateButtonConfig,scope,components,schema,RenderEngine}}});var _sfc_render$2=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{pageAttrs:_setup.$attrs,schema:_setup.schema,scope:_setup.scope,components:_setup.components,schemaKey:"BlackDetail"}})},_sfc_staticRenderFns$2=[],__component__$2=normalizeComponent(_sfc_main$2,_sfc_render$2,_sfc_staticRenderFns$2,!1,null,null,null,null);const edit=__component__$2.exports,_sfc_main$1=defineComponent({__name:"list-engine",setup(__props){const{emitTabAdd,t:$t,app}=usePageHelper(),$addOne=()=>{$detailOne("add",{})},$detailOne=(type,row)=>{let tabName=type=="add"?"blackEdit":"blackEdit"+row.blackId;emitTabAdd({component:edit,params:{flag:type,row,tabName},title:type=="add"?"新增单据":row.blackCode,name:tabName})},$editOne=row=>{$detailOne("edit",row)},$delete=($queryEngine,row,$message)=>{$queryEngine.request.delete([row.rescindId]).then(res=>{$message.success($t("common.successDelete")),$queryEngine.state.paginationManagement.refresh()})},schema=defineSchemas({BlackRescind:{type:"void","x-decorator":"QueryEngine","x-component":"el-container","x-component-props":{class:"flex-container",direction:"vertical"},"x-query-engine":{service:"sup",actions:{paginationQuery:{immediate:!0,transformRequest:expression(`(data, headers) => {
            data.query['*'] = {}
            return data
          }`),onSuccess:expression(`(res) => {

          }`)}}},properties:{bus:{type:"void","x-component":"BusEvent","x-component-props":{eventName:"BlackRescind","@listener":expression(`() => {
            $queryEngine.state.paginationManagement.refresh()
          }`)}},query:{type:"object","x-query-engine-skip":!0,"x-component":"QueryFormByQueryEngine",properties:generateXindexInOrder({rescindCode:{type:"string",title:i18nExpression("vendorMod.relegation.receiptNum"),"x-query-engine-query-operator":"contains"},rescindName:{type:"string",title:i18nExpression("vendorMod.relegation.billName"),"x-query-engine-query-operator":"contains"},approveStatus:{type:"string",title:i18nExpression("vendorMod.relegation.documentStatus"),"x-component":"DictSelect","x-component-props":{code:"APPROVE_STATUS_TYPE"}}})},toolbar:{type:"void","x-component":"Space","x-component-props":{style:"margin-bottom:16px;"},properties:{add:{type:"void",title:"{{$t('common.addSecure')}}","x-component":"RButton","x-component-props":{type:"primary","@click":expression(`() => {
                $addOne()
              }`)}},exportExcel:{type:"void","x-component":"ExportExcel","x-component-props":{...exportExcelSegment,type:"default",pageUrl:"/api-sup/api-ql/BlackRescind/query",tableHeader:queryFieldStatePropertyExpression("BlackRescind.table","data.columns"),dictCodes:{approveStatus:"APPROVE_STATUS_TYPE"}}},tips:{type:"void","x-component":"div","x-component-props":{style:{display:"inline-block",color:"#D9001B"}},"x-content":"本功能用于黑名单供应商在列黑后的申诉与黑名单的解除。"}}},table:{type:"array","x-component":"RenderTable","x-component-props":{class:"table-view-vxe-table",style:"flex: 1",preColumns:"seq",openCustomTable:!0},properties:generateXindexInOrder({rescindId:{type:"number","x-hidden":!0,"x-query-engine-primary-key":!0},rescindCode:{type:"string","x-component":"TableButton","x-component-props":{type:"text","@click":expression(`({row}) => {
                let tab = {
                  component: edit,
                  params: {
                    flag: 'view',
                    row: row,
                    tabName: 'edit' + row.rescindCode || row.rescindId
                  },
                  title: row.rescindName,
                  name: 'edit' + row.rescindCode
                }
                emitTabAdd(tab)
              }`)},"x-render-table-column":{title:i18nExpression("vendorMod.relegation.receiptNum"),minWidth:120,customRender:!0}},rescindName:{type:"string","x-render-table-column":{title:i18nExpression("vendorMod.relegation.billName"),minWidth:120}},rescindContent:{type:"string","x-render-table-column":{title:i18nExpression("vendorMod.relegation.sketch"),minWidth:120}},approveStatus:{type:"string","x-component":"DictSelect","x-component-props":{code:"APPROVE_STATUS_TYPE"},"x-render-table-column":{title:"{{$t('vendorMod.relegation.documentStatus')}}",minWidth:100}},createdBy:{type:"string","x-render-table-column":{title:"{{$t('common.creator')}}",width:120}},creationDate:{title:"{{ $t('common.creationTime') }}",...yearMonthDaySelectorSegment,"x-render-table-column":{width:150}},lastUpdateDate:{type:"string","x-query-engine-sort":"desc","x-hidden":!0,"x-query-engine-primary-key":!0},operation:{type:"void",title:"{{$t('common.operation')}}","x-component":"RenderTableButtonList","x-component-props":{max:2},"x-render-table-column":{fixed:"right",width:120},properties:{edit:{type:"void",title:"{{$t('common.edit')}}","x-reactions":changeFieldVisibleByDeps([".approveStatus"],"['DRAFT'].includes($deps[0])"),"x-component-props":{...buttonListItemVisibleByPermission("base:black:edit"),"@click":expression(`({row}) => {
                    let tab = {
                      component: edit,
                      params: {
                        flag: 'edit',
                        row: row,
                        tabName: 'edit' + row.rescindCode || row.rescindId
                      },
                      title: row.rescindName,
                      name: 'edit' + row.rescindCode
                    }
                    emitTabAdd(tab)
                  }`)}},delete:{type:"void",title:"{{$t('common.delete')}}","x-reactions":changeFieldVisibleByDeps([".approveStatus"],"$deps[0] === 'DRAFT'"),"x-component-props":{popconfirm:{title:"{{$t('common.confirmDeleteRow')}}"},"@click":expression(`({row}) => {
                    $delete($queryEngine,row,$message)
                  }`)}}}}})}}}});return{__sfc:!0,emitTabAdd,$t,app,$addOne,$detailOne,$editOne,$delete,schema,components:{},scope:{$addOne,$editOne,$delete,$detailOne,emitTabAdd,edit},RenderEngine}}});var _sfc_render$1=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{pageAttrs:_vm.$attrs,scope:_setup.scope,components:_setup.components,schemaKey:"BlackList",schema:_setup.schema}})},_sfc_staticRenderFns$1=[],__component__$1=normalizeComponent(_sfc_main$1,_sfc_render$1,_sfc_staticRenderFns$1,!1,null,null,null,null);const Black=__component__$1.exports,_sfc_main={name:"BlackSecure",components:{NavTabs},data(){return{activeTab:"blackSecure",tabs:[{title:()=>this.$t("route.blackSecure"),name:"blackSecure",component:Black,closable:!1}]}}};var _sfc_render=function(){var _vm=this,_c=_vm._self._c;return _c("NavTabs",{ref:"tabs",attrs:{"tabs-list":_vm.tabs,"cur-tab":_vm.activeTab}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const index=__component__.exports;export{index as default};
