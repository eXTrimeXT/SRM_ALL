import{ak as defineComponent,al as usePageHelper,am as useAttrs,aq as defineSchemas,ad as expression,ah as generateXindexInOrder,ae as i18nExpression,cf as formGridSegment,af as yearMonthDaySelectorSegment,ar as RenderEngine,bC as toJS,cK as getHeaderField,cm as bus,n as normalizeComponent}from"./index-6b6051d8.js";import{F as FileDynamic}from"./file-dynamic-25a093c4.js";import{c as throttle}from"./util-d962b17f.js";import"./file-dynamic-7fc2d358.js";import"./MainHeader-2d842985.js";/* empty css                                                                   */import"./file-dynamic.vue_vue_type_style_index_0_scoped_516fdfc3_lang-f2c1a82d.js";import"./tree-utils-7df6be59.js";import"./basicSetting-fc46a2d9.js";import"./BaseTableBind-b1f76fc9.js";import"./util-1e55288f.js";import"./index-4d512f00.js";/* empty css                                                                      */import"./fileApi-05bbbbcc.js";const _sfc_main=defineComponent({__name:"edit-engine",setup(__props){const{emitTabAdd,emitTabRemove,t:$t,app,vendor}=usePageHelper(),$attrs=useAttrs(),$saveBill=throttle(async(type,$form,$queryEngine,$message,$bus)=>{const values=$form.values;type==="SAVE"?$submitData(type,values,$form,$queryEngine):type==="SUBMIT"&&$form.validate().then(()=>{app.$confirm($t("vendorMod.blackListTips2"),$t("common.sureSubmit"),{confirmButtonText:$t("common.confirm"),cancelButtonText:$t("common.cancel"),type:"warning"}).then(()=>{$submitData(type,values,$form,$queryEngine)})}).catch(err=>{})},300),$submitData=(type,$values,$form,$queryEngine)=>{const form=toJS($values);return $attrs.params.flag==="add"&&(form.approveStatus="DRAFT"),$queryEngine.request.baseRequest({type:"BlackRescind",lang:"zh-cn",loading:!0,tree:!0,payload:[form],query:{"*":{},blackRescindCompanyList:{"*":{}},fileUploads:{"*":{}}},action:"save"}).then(res=>{if(res.data&&res.data.length>0){const datas=res.data[0];if($form.values.rescindId=datas.rescindId,app.$message.success($t("common.successSave")),type==="SUBMIT"){const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;let formHeaderValue=getHeaderField(datas);componentInstance.setWorkflowBusinessId(datas.rescindId||""),componentInstance.setWorkflowTabDisabled(!1),componentInstance.setWorkflowBusinessVariables({procTitleObj:formHeaderValue}),componentInstance.handlerAfter(type.toUpperCase(),()=>{$cancel()}),setTimeout(()=>{$form.readPretty=!0,componentInstance.buttonConfigInfo.save.view=!1,componentInstance.buttonConfigInfo.submit.view=!1},100)}else $queryEngine.request.read();bus.$emit("BlackRescind")}})},$cancel=()=>{emitTabRemove($attrs.tabName),bus.$emit("BlackRescind")},initButtonConfig=$form=>{setTimeout(()=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance,viewUpdateButton=$form.query("state").get("data").viewUpdateButton;componentInstance.buttonConfigInfo.save.view=viewUpdateButton,componentInstance.buttonConfigInfo.submit.view=viewUpdateButton,componentInstance.buttonConfigInfo.cancel.view=viewUpdateButton,componentInstance.buttonConfigInfo.close.view=!viewUpdateButton},50)},updateButtonConfig=$form=>{setTimeout(()=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance,viewUpdateButton=$form.query("state").get("data").viewUpdateButton;let formHeaderValue=getHeaderField($form.values);componentInstance.buttonConfigInfo.save.view=viewUpdateButton,componentInstance.buttonConfigInfo.submit.view=viewUpdateButton,componentInstance.buttonConfigInfo.cancel.view=viewUpdateButton,componentInstance.buttonConfigInfo.close.view=!viewUpdateButton,componentInstance.setWorkflowBusinessId($form.values.rescindId),componentInstance.setWorkflowBusinessVariables({procTitleObj:toJS(formHeaderValue)}),componentInstance.setWorkflowTabDisabled($form.query("state").get("data").orderStatus==="DRAFT")},50)},scope={$attrs,app,emitTabRemove,$saveBill,updateButtonConfig,initButtonConfig,$cancel},components={FileDynamic},schema=defineSchemas({state:{type:"void","x-component":"Fragment","x-hidden":!0,"x-data":{isSrmCompany:!1,viewUpdateButton:!0,orderStatus:"DRAFT"}},BlackRescind:{type:"void","x-decorator":"QueryEngine","x-component":"el-container","x-component-props":{class:"flex-container",direction:"vertical"},"x-query-engine":{service:"sup",actions:{read:{immediate:!0,tree:!0,ready:expression(`() => {
            $form.readPretty = $attrs.params.flag === 'view'
            initButtonConfig($form)

            return $attrs.params.row.rescindId || $form.values.rescindId
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
            setTimeout(() => {
              $form.query('fileUploads').take(field => {
                field.componentProps.componentInstance.reLoadFileInfo()
              })
            },100)
           
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
            $cancel()
          }`),"@update-integration-mode":expression(`(integrationMode) => {
            updateButtonConfig($form)
          }`)},properties:{collapse:{type:"void","x-component":"Collapse","x-read-pretty":expression("$form.readPretty"),properties:generateXindexInOrder({baseInfo:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("black.blackSecureDetail")},"x-query-engine-skip":!0,properties:{layout:{type:"void",...formGridSegment,properties:{rescindName:{type:"string",title:i18nExpression("vendorMod.relegation.billName"),"x-decorator":"FormItem","x-validator":{required:!0}},approveStatus:{type:"string",title:i18nExpression("vendorMod.relegation.documentStatus"),"x-component":"DictSelect","x-component-props":{code:"PJ_APPROVE_STATUS_TYPE",disabled:!0},"x-decorator":"FormItem"},createdBy:{type:"string","x-decorator":"FormItem",title:i18nExpression("common.creator"),"x-component-props":{disabled:!0}},creationDate:{...yearMonthDaySelectorSegment,"x-decorator":"FormItem",title:i18nExpression("common.creationTime"),"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],disabled:!0}},rescindContent:{type:"string","x-decorator":"FormItem",title:i18nExpression("vendorMod.relegation.sketch"),"x-component-props":{type:"textarea",autosize:expression("{ minRows: 3, maxRows: 4}")},"x-decorator-props":{gridSpan:4},"x-validator":{required:!0}}}}}},rangeList:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("black.rangeList")},"x-query-engine-skip":!0,properties:{add:{type:"void","x-hidden":"{{$form.readPretty}}","x-component":"QuickSearchWrapper","x-component-props":{style:"margin:0 0 15px 0","show-key":"username",name:"scc_sup_company_info_is_black",multiSelect:!0,showButton:!0,btnTitle:"{{$t('bidMod.addVendor')}}","@close-quicksearch":expression(`(val)=>{
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
                    }`)}},blackRescindCompanyList:{type:"array","x-component":"RenderTable","x-component-props":{primaryKey:"rescindCompanyId",cascadeDeletion:!0,preColumns:"seq",class:"table-view-vxe-table",editMode:!1,pagination:!1,sortable:!1,height:"250px"},"x-query-engine-skip":!0,properties:generateXindexInOrder({companyId:{type:"number",default:null,"x-hidden":!0},companyName:{type:"string",title:i18nExpression("vendorMod.vendorName"),"x-render-table-column":{minWidth:150}},companyCode:{type:"string",title:i18nExpression("vendorMod.vendorCode"),"x-render-table-column":{minWidth:150}},lcCode:{type:"string",title:i18nExpression("vendorMod.lcCode"),"x-render-table-column":{minWidth:150}},legalPerson:{type:"string",title:i18nExpression("vendorMod.corporateRepresentative"),"x-render-table-column":{minWidth:150}},supplierType:{type:"string",title:i18nExpression("supplierRating.supplierType"),"x-component":"DictSelect","x-render-table-column":{minWidth:150},"x-component-props":{code:"SUPPLIER_TYPE"}},companyType:{type:"string",title:i18nExpression("vendorMod.companyType"),"x-component":"DictSelect","x-render-table-column":{minWidth:150},"x-component-props":{code:"COMPANY_NATURE_NEW"}},agent:{type:"string",title:i18nExpression("vendorMod.managerAgent"),"x-render-table-column":{minWidth:150}},endDate:{title:i18nExpression("supplierRating.blacklistDeadline"),...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                            parseTime(row.endDate, '{y}-{m}-{d}')
                          }`)},"x-render-table-column":{minWidth:150}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{disabled:expression("$form.readPretty"),type:"text","@click":expression(`({ row }) => {
                                $table.remove($self.index)
                            }`)}}}}})}}},fileUploadsCollapse:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("sourcingBuyer.attach")},"x-query-engine-skip":!0,properties:{fileUploads:{type:"array","x-query-engine-relation":"fileUploads:*","x-component":"FileDynamic","x-component-props":{"scene-module-code":"SCENE_BLACK_RESCIND_ATTACHMENT","business-id":"{{$attrs.params.row.rescindId || $form.values.rescindId}}",editable:"{{!$readOnly}}","need-init":!0}}}}})}}}}}});return{__sfc:!0,emitTabAdd,emitTabRemove,$t,app,vendor,$attrs,$saveBill,$submitData,$cancel,initButtonConfig,updateButtonConfig,scope,components,schema,RenderEngine}}});var _sfc_render=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{pageAttrs:_setup.$attrs,schema:_setup.schema,scope:_setup.scope,components:_setup.components,schemaKey:"BlackDetail"}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const edit=__component__.exports;export{edit as default};
