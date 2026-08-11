import{aP as _mergeJSXProps,n as normalizeComponent,ak as defineComponent,c5 as connect,c6 as mapProps,al as usePageHelper,am as useAttrs,an as ref$1,bY as computed,aq as defineSchemas,ad as expression,ae as i18nExpression,bD as changeFieldVisibleByDeps,af as yearMonthDaySelectorSegment,ah as generateXindexInOrder,ai as editTableFormItemValid,ar as RenderEngine,ax as omit,as as performPlanService}from"./index-6b6051d8.js";import{d as deppOmit}from"./util-1e55288f.js";const _sfc_main$1={name:"IFieldView",props:["data","disabled"],data(){return{form:{}}},computed:{},watch:{form:{deep:!0,handler(value){this.data.forEach(i=>{i.filedValue=value[i.fieldCode]}),this.data}},data:{immediate:!0,deep:!0,handler(nVal){this.form=this.data.reduce((last,item)=>(last[item.fieldCode]=item.filedValue||"",last),{})}}},created(){this.data},mounted(){},methods:{validate(){return new Promise(resolve=>{this.$refs.form.validate(flag=>resolve(flag))})},renderFormItems(h,item){const validator={required:item.required,message:this.$t("contract_mod.required")};return h("el-col",{attrs:{span:6},key:item.perAcceptanceId},[h("el-form-item",{attrs:{label:item.fieldName,prop:item.fieldCode,rules:[validator]}},[this.renderComponent(h,item)])])},renderComponent(h,item){const opts=JSON.parse(item.fieldOptions||"{}");if(item.fieldType==="el-select"){const{options="",...rest}=opts;return h("el-select",{key:item.perAcceptanceId,attrs:{disabled:this.disabled,...rest},model:{value:this.form[item.fieldCode],callback:$$v=>{this.$set(this.form,item.fieldCode,$$v)}}},[options.split(",").map(i=>h("el-option",{attrs:{label:i,value:i}}))])}return item.fieldType==="el-input"?h("el-input",{key:item.perAcceptanceId,attrs:{disabled:this.disabled,...opts},model:{value:this.form[item.fieldCode],callback:$$v=>{this.$set(this.form,item.fieldCode,$$v)}}}):item.fieldType==="el-checkbox"?h("el-checkbox",{key:item.perAcceptanceId,class:"form-item-class",attrs:{disabled:this.disabled,...opts},model:{value:this.form[item.fieldCode],callback:$$v=>{this.$set(this.form,item.fieldCode,$$v)}}}):item.fieldType==="el-date-picker"?h("el-date-picker",{key:item.perAcceptanceId,attrs:{disabled:this.disabled,"value-format":"yyyy-MM-dd",format:this.$formatDatePicker,...opts},model:{value:this.form[item.fieldCode],callback:$$v=>{this.$set(this.form,item.fieldCode,$$v)}}}):null}},render(h){return h("el-form",_mergeJSXProps([{},{props:{model:this.form}},{ref:"form"}]),[h("el-row",{attrs:{type:"flex",gutter:27},style:"flex-wrap: wrap;"},[this.data.map(item=>this.renderFormItems(h,item))])])}};const _sfc_render$1=null,_sfc_staticRenderFns$1=null;var __component__$1=normalizeComponent(_sfc_main$1,_sfc_render$1,_sfc_staticRenderFns$1,!1,null,"d432934c",null,null);const OriginIFieldView=__component__$1.exports,_sfc_main=defineComponent({__name:"edit",setup(__props){const IFieldView=connect(OriginIFieldView,mapProps({value:"data"})),{app,emitTabRemove,t,vendor}=usePageHelper(),attrs=useAttrs(),workflowStatus=ref$1("DRAFT"),$disabled=["view","approval"].includes(attrs.params.flag),customUpdateButton=computed(()=>!$disabled&&["SUPPLIER_SUBMITTED"].includes(workflowStatus.value)),viewUpdateButton=computed(()=>!$disabled&&!["APPROVED","SUPPLIER_SUBMITTED"].includes(workflowStatus.value)),disabledUpdateButton=computed(()=>["APPROVING"].includes(workflowStatus.value)),initButtonConfig=$form=>{setTimeout(()=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.buttonConfigInfo.save.view=viewUpdateButton.value,componentInstance.buttonConfigInfo.submit.view=viewUpdateButton.value,componentInstance.buttonConfigInfo.save.disabled=disabledUpdateButton.value,componentInstance.buttonConfigInfo.submit.disabled=disabledUpdateButton.value,componentInstance.buttonConfigInfo.cancel.view=!1,componentInstance.buttonConfigInfo.close.view=!1,componentInstance.buttonCustom.PASS.view=customUpdateButton.value,componentInstance.buttonCustom.REJECT.view=customUpdateButton.value},50)},updateButtonConfig=$form=>{setTimeout(()=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.buttonConfigInfo.save.view=viewUpdateButton.value,componentInstance.buttonConfigInfo.submit.view=viewUpdateButton.value,componentInstance.buttonConfigInfo.save.disabled=disabledUpdateButton.value,componentInstance.buttonConfigInfo.submit.disabled=disabledUpdateButton.value,componentInstance.buttonCustom.PASS.view=customUpdateButton.value,componentInstance.buttonCustom.REJECT.view=customUpdateButton.value,componentInstance.setWorkflowBusinessId($form.values.perAcceptanceId),componentInstance.setWorkflowTabDisabled(["DRAFT","SUPPLIER_SUBMITTED","FIRST_PASS"].includes($form.values.status)),componentInstance.setWorkflowBusinessVariables({})},50)},schema=defineSchemas({PerAcceptance:{type:"void","x-decorator":"QueryEngine","x-component":"el-container","x-component-props":{class:"flex-container",direction:"vertical"},"x-query-engine":{service:"cm",actions:{queryMilestone:{immediate:!0,method:"read",ready:expression(`() => {
            return $attrs.params && $attrs.params.fromContractPerformancePlan
          }`),autoFormatResult:!1,transformRequest:expression(`(data, headers) => {
            data.action = 'queryMilestone'
            data.query = {
              "*":{},
              "perAcceptanceConfList": {'*': {}},
              "perAcceptanceAttList": {'*': {}},
              "perPlanMilestoneId": {'*': {}},
              "perPlanId": {'*': {}},
              "perTemplLineId": { configList: {'*': {}}},
            }
            data.payload = {
              filter: {
                perPlanMilestoneId: {
                  eq: $attrs.params.row.perPlanMilestoneId
                }
              }
            }

            return data
          }`),transformResponse:expression(`(res) => {
            const data = JSON.parse(res)

            if (data.data.ref) {
              const perAcceptanceItem = {}

              // 可以通过 perPlanMilestoneId 查询到对应的履约计划数据
              if (data.data.type === '[PerAcceptance]' && data.data.ref.PerAcceptance) {
                const perAcceptanceId = Object.keys(data.data.ref.PerAcceptance)[0]

                Object.assign(perAcceptanceItem, data.data.ref.PerAcceptance[perAcceptanceId])

                if (perAcceptanceItem.perAcceptanceConfList) {
                  perAcceptanceItem.perAcceptanceConfList = perAcceptanceItem.perAcceptanceConfList.map(id => {
                    return data.data.ref.PerAcceptanceConf[id]
                  })
                }

                if (perAcceptanceItem.perAcceptanceAttList) {
                  perAcceptanceItem.perAcceptanceAttList = perAcceptanceItem.perAcceptanceAttList.map(id => {
                    return data.data.ref.PerAcceptanceAtt[id]
                  })
                }

                if (perAcceptanceItem.perPlanId) {
                  perAcceptanceItem.perPlanId = data.data.ref.PerPlan[perAcceptanceItem.perPlanId]
                }

                if (perAcceptanceItem.perPlanMilestoneId) {
                  perAcceptanceItem.perPlanMilestoneId = data.data.ref.PerPlanMilestone[perAcceptanceItem.perPlanMilestoneId]
                }
              } else {
                // 反之是查不到
                const perPlanId = Object.keys(data.data.ref.PerPlan)[0]
                perAcceptanceItem.perPlanId = data.data.ref.PerPlan[perPlanId]

                const perPlanMilestoneId = Object.keys(data.data.ref.PerPlanMilestone)[0]
                perAcceptanceItem.perPlanMilestoneId =
                  data.data.ref.PerPlanMilestone[perPlanMilestoneId]

                if (perAcceptanceItem.perPlanMilestoneId.perTemplLineId) {
                  const perTemplLine = data.data.ref.PerTemplLine[perAcceptanceItem.perPlanMilestoneId.perTemplLineId]

                  if (perTemplLine && perTemplLine.configList) {
                    perAcceptanceItem.perAcceptanceConfList = perTemplLine.configList.map(id => {
                      return data.data.ref.PerTemplLineConfig[id]
                    })
                  }
                }
              }

              // 设置文本只读
              $form.readPretty = $readOnly || $disabled

              $form.setValues({
                ...perAcceptanceItem,
                status: perAcceptanceItem.status || 'DRAFT',
                perAcceptanceNo: perAcceptanceItem.perAcceptanceNo,
                perPlanMilestoneId: [perAcceptanceItem.perPlanMilestoneId],
                perPlanId: perAcceptanceItem.perPlanId,
                perAcceptanceAttList: perAcceptanceItem.perAcceptanceAttList || [],
                perAcceptanceConfList: perAcceptanceItem.perAcceptanceConfList || [],
              })

              updateButtonConfig($form)
            }

            return data
          }`)},query:{immediate:!0,ready:expression(`() => {
            initButtonConfig($form)

            return !!(
              $attrs.params
              && $attrs.params.row
              && !$attrs.params.fromContractPerformancePlan
              && ($attrs.params.row.perAcceptanceId || $attrs.params.row.perPlanMilestoneId)
            )
          }`),transformRequest:expression(`(data, headers) => {
            if (!data.payload.filter) {
              data.payload.filter = {}

              if ($attrs.params.row) {
                if ($attrs.params.row.perAcceptanceId) {
                  data.payload.filter.perAcceptanceId = {
                    eq: $attrs.params.row.perAcceptanceId
                  }
                }

                // 履约计划调跳转过来生成合同验收单的查询
                if ($attrs.params.row.perPlanMilestoneId) {
                  data.payload.filter.perPlanMilestoneId = {
                    eq: $attrs.params.row.perPlanMilestoneId
                  }
                }
              }
            }

            delete data.payload.page

            data.query['*'] = {}

            return data
          }`),onSuccess:expression(`(res) => {
            // 设置文本只读
            $form.readPretty = $readOnly || $disabled

            if (res.data[0]) {
              // UI 呈现需要是数组，实际是单个对象
              res.data[0].perPlanMilestoneId = [res.data[0].perPlanMilestoneId].filter(Boolean)
            }

            $form.setValues(res.data[0])

            updateButtonConfig($form)
          }`)},vendorSubmit:{transformRequest:expression(`(data, headers) => {
            data.query['*'] = {}

            return data
          }`)},save:{transformRequest:expression(`(data, headers) => {
            data.query['*'] = {}

            return data
          }`)}}},properties:{SchemaWorkflow:{type:"void","x-component":"SchemaWorkflow","x-component-props":{"business-id":expression("$attrs.params.row?.perAcceptanceId || null"),"business-type":"performAcceptance","button-custom":expression("{}"),"@click-handler":expression(`(type) => {
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus)
          }`),"@submit-direct":expression(`(type) => {
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus)
          }`),"@confirm":expression(`(type, comment) => {
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus)
          }`),"@close-tab":expression(`() => {
            $back($bus)
          }`)},items:{type:"object","x-query-engine-skip":!0,properties:{refuse:{type:"void","x-content":i18nExpression("components.approvalHead.headers.refuse"),"x-component":"Button","x-component-props":{type:"default","@click":expression(`() => {
                  console.log($values, '$values')
                  $values.status = 'REJECTED'
                  $queryEngine.request.save($values).then(() => {
                    $message.success(t('common.successSave'))
                    $bus.$emit('PerAcceptance')
                    emitTabRemove($attrs.tabName)
                  })
                }`)},"x-reactions":changeFieldVisibleByDeps(["status"],`
                  $deps[0] === 'SUPPLIER_SUBMITTED' && $attrs.params.flag !== 'view'
                  `)}}},properties:{perPlanId:{type:"string","x-query-engine-skip":!0,"x-query-engine-relation":"perPlanId:*","x-hidden":!0},layout:{type:"void","x-component":"FormContainer",properties:{collapse:{type:"void","x-component":"Collapse","x-component-props":{defaultOpenPanelCount:1},properties:{baseInfo:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("supRisk.baseInfo")},"x-query-engine-skip":!0,properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:4,columnGap:32,rowGap:0},properties:{"perPlanId.contractNo":{type:"string","x-decorator":"FormItem","x-component":"Input",title:i18nExpression("bidMod.compactIndex"),"x-component-props":{disabled:!0}},"perPlanId.processNum":{type:"string","x-decorator":"FormItem",title:i18nExpression("contract_mod.processNum"),"x-component-props":{disabled:!0}},"perPlanId.templateName":{type:"string","x-decorator":"FormItem",title:i18nExpression("contract_mod.templateName"),"x-component-props":{disabled:!0}},"perPlanId.vendorName":{type:"string","x-decorator":"FormItem",title:i18nExpression("common.vendorName"),"x-component-props":{disabled:!0}},"perPlanId.contractClass":{type:"string","x-decorator":"FormItem",title:i18nExpression("contract_mod.contractType"),"x-component":"DictSelect","x-component-props":{disabled:!0,code:"ELEM_CONTRACT_TYPE"}},"perPlanId.buName":{type:"string","x-decorator":"FormItem",title:i18nExpression("bid_mod.businessEntity"),"x-component-props":{disabled:!0}},perAcceptanceNo:{type:"string","x-decorator":"FormItem",title:i18nExpression("bid_mod.perAcceptanceNo"),"x-component-props":{disabled:!0}},status:{type:"string","x-decorator":"FormItem",title:i18nExpression("components.stratProcess.headers.docStatusValue"),"x-component":"DictSelect","x-component-props":{code:"CONTRACT_CHECK_STATUS",disabled:!0}},"perPlanId.perPlanNo":{type:"string","x-decorator":"FormItem",title:i18nExpression("bid_mod.perPlanNo"),"x-component-props":{disabled:!0}},"perPlanId.includeTaxAmount":{type:"string","x-decorator":"FormItem",title:i18nExpression("purSettlementMod.includeTaxAmount"),"x-component-props":{disabled:!0}},"perPlanId.currencyCode":{type:"string","x-component":"DictSelect","x-component-props":{code:"currency",disabled:!0},"x-decorator":"FormItem",title:i18nExpression("vendorMod.currencyCode")},"perPlanId.createdFullName":{type:"string","x-decorator":"FormItem",title:i18nExpression("common.creator"),"x-disabled":!0},"perPlanId.creationDate":{...yearMonthDaySelectorSegment,default:void 0,"x-decorator":"FormItem",title:i18nExpression("common.creationTime"),"x-disabled":!0}}}}},perPlanMilestone:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("contractMod.milestone")},properties:{perPlanMilestoneId:{type:"array","x-component":"RenderTable","x-component-props":{class:"table-view-vxe-table",preColumns:"seq",pagination:!1,maxHeight:"45vh"},"x-query-engine-skip":!0,"x-query-engine-relation":"perPlanMilestoneId:*",properties:generateXindexInOrder({serialNumber:{type:"string",title:i18nExpression("components.processTable.headers.fdNodeName"),"x-render-table-column":{minWidth:130}},milestoneType:{type:"string",title:i18nExpression("contract_mod.processNodeName"),"x-component":"DictSelect","x-component-props":{code:"MILESTONE_SCHEDULE"},"x-render-table-column":{minWidth:130}},nodePersonName:{type:"string",title:i18nExpression("common.nodeLeader"),"x-render-table-column":{minWidth:130}},planStartDate:{title:i18nExpression("perfMod.planStartDate"),...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                                parseTime(row.planStartDate, '{y}-{m}-{d}')
                              }`)},"x-render-table-column":{minWidth:130}},planEndDate:{title:i18nExpression("perfMod.planEndDate"),...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                                parseTime(row.planEndDate, '{y}-{m}-{d}')
                              }`)},"x-render-table-column":{minWidth:130}},nodePlanNum:{type:"string","x-disabled":expression(`
                            $disabled
                              || ['SUPPLIER_SUBMITTED','FIRST_PASS'].includes($values.status)
                              || $form.values.dataCreationType === 'VENDOR'
                            `),"x-render-table-column":{title:i18nExpression("perfMod.numberOfNodesDelivered"),minWidth:130},...editTableFormItemValid},practicallyEndDate:{...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                                parseTime(row.practicallyEndDate, '{y}-{m}-{d}')
                              }`)},"x-disabled":expression(`
                            $disabled
                              || ['SUPPLIER_SUBMITTED','FIRST_PASS'].includes($values.status)
                              || $form.values.dataCreationType === 'VENDOR'
                            `),"x-render-table-column":{title:i18nExpression("perfMod.actualEndTime"),minWidth:130},...editTableFormItemValid},remarks:{type:"string",title:i18nExpression("perfMod.specialRemarks"),"x-render-table-column":{minWidth:130}},fileName:{type:"string","x-hidden":!0,"x-query-engine-skip":!0},fileId:{type:"string",title:i18nExpression("dataConfMod.attachmentTemplate"),"x-component":"SrmCommonFile","x-component-props":{readonly:!0,"default-file":{fileId:"{{$table.getRowByIndex($self.index)?.fileId}}",fileName:"{{$table.getRowByIndex($self.index)?.fileName}}"}},"x-render-table-column":{minWidth:130}}})}}},perAcceptanceConf:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("bidMod.performanceProcessEvaluation")},properties:{perAcceptanceConfList:{type:"string","x-component":"IFieldView","x-query-engine-skip":!0,"x-query-engine-relation":"perAcceptanceConfList:*",default:[],"x-component-props":{disabled:expression("$disabled")}}}},delivery:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("bidMod.deliveryInstructions")},properties:{deliveryExplain:{type:"string","x-decorator":"FormItem","x-component":"Input.TextArea","x-component-props":{disabled:"{{$disabled}}",rows:"3",maxlength:"300","show-word-limit":!0}}}},relevantAttachment:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("accountMod.relevantAttachment")},properties:{toolbar:{type:"void","x-component":"Space","x-component-props":{style:"margin-bottom: 16px"},properties:{add:{type:"void",title:i18nExpression("common.add"),"x-component":"RButton","x-component-props":{type:"primary",disabled:"{{$disabled}}","@click":expression(`(rowIndex) => {
                                $form.query(".perAcceptanceAttList").take().componentProps.componentInstance.addRow()
                              }`)}}}},perAcceptanceAttList:{type:"array","x-component":"RenderTable","x-read-pretty":!0,"x-component-props":{maxHeight:"45vh",class:"table-view-vxe-table",preColumns:"seq",editMode:!0,pagination:!1},"x-query-engine-skip":!0,"x-query-engine-relation":"perAcceptanceAttList:*",properties:generateXindexInOrder({fileName:{type:"string",title:i18nExpression("vendorMod.attachmentUpload"),"x-read-pretty":expression("$disabled && $buyer()"),"x-component":"SrmCommonFile","x-component-props":{"extra-data":{fileModular:"sup",fileFunction:"contractPerformanceCheck",fileType:"images"},"default-file":{fileId:"{{$table.getRowByIndex($self.index).fileId}}",fileName:"{{$self.value}}"},"@on-change":expression(`({file}) => {
                                let row = $table.getRowByIndex($self.index)
                                const { fileId = '', fileName = '' } = file || {}
                                row.fileId = fileId.toString()
                                $self.value = fileName
                                row.createdUserName = file.createdBy
                                row.creationDate = file.creationDate
                              }`)},"x-render-table-column":{minWidth:150}},createdUserName:{type:"string",title:i18nExpression("components.fileupload.uploadUserName"),"x-render-table-column":{minWidth:130}},creationDate:{...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                                parseTime(row.creationDate, '{y}-{m}-{d}')
                              }`)},title:i18nExpression("outsource.creationDate"),"x-render-table-column":{minWidth:150}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{minWidth:120,fixed:"right"},properties:{layout:{type:"void","x-component":"FormButtonGroup",properties:{edit:{type:"void",title:i18nExpression("common.delete"),"x-component":"TableButton","x-component-props":{type:"text",disabled:"{{$disabled}}","@click":expression(`({row, rowIndex}) => {
                                        $table.remove(rowIndex)
                                      }`)}}}}}}})}}}}}}}}}}}}),$back=$bus=>{emitTabRemove(attrs.tabName),$bus.$emit("PerAcceptance")},getFormDetail=(data={},$form,$queryEngine)=>$queryEngine.request.query({perAcceptanceId:data?.perAcceptanceId,perPlanMilestoneId:data?.perPlanMilestoneId?.perPlanMilestoneId}).then(res=>(workflowStatus.value=res.data.status,$form.setValues(res.data),updateButtonConfig($form),res.data)),$saveBill=(type,$form,$queryEngine,$confirm,$message,$bus)=>{let data=deppOmit($form.values,["lastUpdateDate","creationDate","creationDate","contractCreationDate","currentPlanEndDate"]);if(data=omit(data,["perPlanId.contractClass","perPlanId.contractNo","perPlanId.includeTaxAmount","perPlanId.vendorName","perPlanId.buName","perPlanId.currencyName","perPlanId.templateName","perPlanId.perPlanNo","perPlanId.processNum","perPlanMilestoneId.serialNumber","perPlanMilestoneId.milestoneType","perPlanMilestoneId.nodePersonName","perPlanMilestoneId.planStartDate","perPlanMilestoneId.planEndDate","perPlanMilestoneId.remarks","perPlanMilestoneId.fileId","perAcceptanceAttList.fileName","perAcceptanceAttList.createdUserName"]),type==="SAVE")$submitData("save",data,$form,$queryEngine,$confirm,$message,$bus);else if(type==="SUBMIT")$form.validate().then(()=>{$submitData("submit",data,$form,$queryEngine,$confirm,$message,$bus)});else if(type==="PASS"||type==="REJECT"){let{approvePass,rejected}=performPlanService.performAcceptance;(type==="PASS"?approvePass:rejected)(data).then(res=>{$message.success(res.message),$back($bus)})}},$submitData=(type,$values,$form,$queryEngine,$confirm,$message,$bus)=>{if(type==="submit"){$values.status="SUBMITTED";for(let i=0;i<$values.perPlanMilestoneId.length;i+=1)if(!$values.perPlanMilestoneId[i].practicallyEndDate){$message(i18nExpression("cusEntry.supplement20250205.milestoneActualEndTime"));return}}let fileNum=0;($values.perAcceptanceAttList||[]).forEach(row=>{fileNum+=row.fileName?1:0}),($values.perPlanMilestoneId||[]).forEach(row=>{row.fileNum=fileNum}),$values.perPlanMilestoneId=$values.perPlanMilestoneId?.[0],$queryEngine.request.save($values,{customizeAction:vendor()&&type==="submit"?"vendorSubmit":void 0}).then(res=>{type==="save"&&$message.success(t("common.successSave")),getFormDetail(res.data?.[0],$form,$queryEngine).then(()=>{if(type==="save")return;let tabDisabled=!0;tabDisabled=!1;const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.setWorkflowBusinessId(res.data?.[0].perAcceptanceId||null),componentInstance.setWorkflowTabDisabled(tabDisabled),componentInstance.setWorkflowBusinessVariables({}),componentInstance.handlerAfter(type.toUpperCase(),()=>{$bus.$emit("PerAcceptance")})})})};return{__sfc:!0,IFieldView,app,emitTabRemove,t,vendor,attrs,workflowStatus,$disabled,customUpdateButton,viewUpdateButton,disabledUpdateButton,initButtonConfig,updateButtonConfig,schema,$back,getFormDetail,$saveBill,$submitData,scope:{app,t,$attrs:attrs,updateButtonConfig,performPlanService,$disabled,emitTabRemove,getFormDetail,initButtonConfig,$saveBill,$back},components:{IFieldView},RenderEngine}}});var _sfc_render=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{schemaKey:"contractPerformanceCheckDetail",readOnly:_setup.$disabled,pageAttrs:_vm.$attrs,schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const ContractPerformanceCheckDetail=__component__.exports;export{ContractPerformanceCheckDetail as default};
