import{aP as _mergeJSXProps,n as normalizeComponent,ak as defineComponent,c5 as connect,c6 as mapProps,al as usePageHelper,am as useAttrs,an as ref$1,bY as computed,aq as defineSchemas,ad as expression,ae as i18nExpression,bD as changeFieldVisibleByDeps,af as yearMonthDaySelectorSegment,ah as generateXindexInOrder,ai as editTableFormItemValid,ar as RenderEngine,ax as omit,as as performPlanService,bF as systemUrl}from"./index-6b6051d8.js";import{A as ApprovalProcess}from"./index-5f54905d.js";import{d as deppOmit}from"./util-1e55288f.js";const _sfc_main$1={name:"IFieldView",props:["data","disabled"],data(){return{form:{}}},computed:{},watch:{form:{deep:!0,handler(value){this.data.forEach(i=>{i.filedValue=value[i.fieldCode]}),this.data}},data:{immediate:!0,deep:!0,handler(nVal){this.form=this.data.reduce((last,item)=>(last[item.fieldCode]=item.filedValue||"",last),{})}}},created(){this.data},mounted(){},methods:{validate(){return new Promise(resolve=>{this.$refs.form.validate(flag=>resolve(flag))})},renderFormItems(h,item){const validator={required:item.required,message:this.$t("contract_mod.required")};return h("el-col",{attrs:{span:6},key:item.perAcceptanceId},[h("el-form-item",{attrs:{label:item.fieldName,prop:item.fieldCode,rules:[validator]}},[this.renderComponent(h,item)])])},renderComponent(h,item){const opts=JSON.parse(item.fieldOptions||"{}");if(item.fieldType==="el-select"){const{options="",...rest}=opts;return h("el-select",{key:item.perAcceptanceId,attrs:{disabled:this.disabled,...rest},model:{value:this.form[item.fieldCode],callback:$$v=>{this.$set(this.form,item.fieldCode,$$v)}}},[options.split(",").map(i=>h("el-option",{attrs:{label:i,value:i}}))])}return item.fieldType==="el-input"?h("el-input",{key:item.perAcceptanceId,attrs:{disabled:this.disabled,...opts},model:{value:this.form[item.fieldCode],callback:$$v=>{this.$set(this.form,item.fieldCode,$$v)}}}):item.fieldType==="el-checkbox"?h("el-checkbox",{key:item.perAcceptanceId,class:"form-item-class",attrs:{disabled:this.disabled,...opts},model:{value:this.form[item.fieldCode],callback:$$v=>{this.$set(this.form,item.fieldCode,$$v)}}}):item.fieldType==="el-date-picker"?h("el-date-picker",{key:item.perAcceptanceId,attrs:{disabled:this.disabled,"value-format":"yyyy-MM-dd",format:this.$formatDatePicker,...opts},model:{value:this.form[item.fieldCode],callback:$$v=>{this.$set(this.form,item.fieldCode,$$v)}}}):null}},render(h){return h("el-form",_mergeJSXProps([{},{props:{model:this.form}},{ref:"form"}]),[h("el-row",{attrs:{type:"flex",gutter:27},style:"flex-wrap: wrap;"},[this.data.map(item=>this.renderFormItems(h,item))])])}};const _sfc_render$1=null,_sfc_staticRenderFns$1=null;var __component__$1=normalizeComponent(_sfc_main$1,_sfc_render$1,_sfc_staticRenderFns$1,!1,null,"10b528e8",null,null);const OriginIFieldView=__component__$1.exports,_sfc_main=defineComponent({__name:"edit",setup(__props){const IFieldView=connect(OriginIFieldView,mapProps({value:"data"})),{http,app,emitTabRemove,t,vendor}=usePageHelper(),attrs=useAttrs(),workflowStatus=ref$1("DRAFT"),$disabled=["view","approval"].includes(attrs.params.flag),customUpdateButton=computed(()=>!$disabled&&["SUPPLIER_SUBMITTED"].includes(workflowStatus.value)),viewUpdateButton=computed(()=>!$disabled&&!["APPROVED","SUPPLIER_SUBMITTED"].includes(workflowStatus.value)),disabledUpdateButton=computed(()=>["APPROVING"].includes(workflowStatus.value)),schema=defineSchemas({pdfIframe:{type:"void","x-component":"iframe","x-component-props":{id:"pdfIframe",style:{display:"none"}}},PerAcceptance:{type:"void","x-decorator":"QueryEngine","x-component":"el-container","x-component-props":{class:"flex-container",direction:"vertical"},"x-query-engine":{service:"cm",actions:{queryMilestone:{immediate:!0,method:"read",ready:expression(`() => {
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
                perAcceptanceId: perAcceptanceItem.perAcceptanceId,
                perPlanMilestoneId: [perAcceptanceItem.perPlanMilestoneId],
                perPlanId: perAcceptanceItem.perPlanId,
                perAcceptanceAttList: perAcceptanceItem.perAcceptanceAttList || [],
                perAcceptanceConfList: perAcceptanceItem.perAcceptanceConfList || [],
              })
              if (['DRAFT', 'SUPPLIER_SUBMITTED', 'WITHDRAW', 'REJECTED'].includes($form.values.status)) {
                const xData = $form.query('PerAcceptance').get('data')
                xData.showButtonConfig = {
                  saveAndNextStep: true
                }
                xData.showTabConfig = {
                  approval: true
                }
              }
            }

            return data
          }`)},query:{immediate:!0,ready:expression(`() => {
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
            if (res.data[0] && ['DRAFT', 'SUPPLIER_SUBMITTED', 'WITHDRAW', 'REJECTED'].includes(res.data[0].status)) {
              const xData = $form.query('PerAcceptance').get('data')
              xData.showButtonConfig = {
                saveAndNextStep: true
              }
              xData.showTabConfig = {
                approval: true
              }
            }
            $form.setValues(res.data[0])
          }`)},vendorSubmit:{transformRequest:expression(`(data, headers) => {
            data.query['*'] = {}

            return data
          }`)},save:{transformRequest:expression(`(data, headers) => {
            data.query['*'] = {}
            return data
          }`),onSuccess:expression(`res => {
            $form.values.perAcceptanceId = res.data[0]?.perAcceptanceId
          }`)}}},"x-data":{showButtonConfig:{saveAndNextStep:!1},showTabConfig:{}},properties:{SchemaWorkflow:{type:"void","x-component":"ApprovalProcess","x-component-props":{"business-id":expression("$form.values.perAcceptanceId || null"),"business-type":"performAcceptance",approvalStatus:expression("$form.values.status || 'DRAFT'"),"operation-pre-options":expression("$wrapper($preOptions, $root)"),"show-button-config":expression("$form.query('PerAcceptance').get('data').showButtonConfig"),"show-tab-config":expression("$form.query('PerAcceptance').get('data').showTabConfig"),readonly:expression("$attrs.params.flag === 'view'"),"@approval-handler-callback":expression(`(type) => {
            $approvalHanlder(type, $form, $queryEngine, $bus)
          }`)},properties:{customButtonList:{type:"void","x-component":"ButtonList","x-component-props":{style:{"margin-right":"8px"}},"x-slot":"custom",properties:{refuse:{type:"void","x-content":i18nExpression("components.approvalHead.headers.refuse"),"x-component":"Button","x-component-props":{type:"default","@click":expression(`() => {
                    console.log($values, '$values')
                    $values.status = 'FIRST_REJECTED'
                    $queryEngine.request.save($values).then(() => {
                      $message.success(t('common.successSave'))
                      $bus.$emit('PerAcceptance')
                      emitTabRemove($attrs.tabName)
                    })
                  }`)},"x-reactions":changeFieldVisibleByDeps(["status"],`
                    $deps[0] === 'SUPPLIER_SUBMITTED' && $attrs.params.flag !== 'view'
                    `)},pdfPrint:{type:"void","x-content":i18nExpression("route.pdfPrint"),"x-component":"Button","x-component-props":{type:"default",style:{"margin-left":"8px"},"@click":expression(`() => {
                    $openPrint($form)
                  }`)},"x-reactions":changeFieldVisibleByDeps(["status"],`
                    $deps[0] === 'APPROVED'
                `)}}},perPlanId:{type:"string","x-query-engine-skip":!0,"x-query-engine-relation":"perPlanId:*","x-hidden":!0},layout:{type:"void","x-component":"FormContainer",properties:{collapse:{type:"void","x-component":"Collapse","x-component-props":{defaultOpenPanelCount:1,id:"printContent"},properties:{baseInfo:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("supRisk.baseInfo")},"x-query-engine-skip":!0,properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:4,columnGap:32,rowGap:0},properties:{"perPlanId.contractNo":{type:"string","x-decorator":"FormItem","x-component":"Input",title:"合同序号","x-component-props":{disabled:!0}},"perPlanId.processNum":{type:"string","x-decorator":"FormItem",title:i18nExpression("contract_mod.processNum"),"x-component-props":{disabled:!0}},"perPlanId.templateName":{type:"string","x-decorator":"FormItem",title:i18nExpression("contract_mod.templateName"),"x-component-props":{disabled:!0}},"perPlanId.vendorName":{type:"string","x-decorator":"FormItem",title:i18nExpression("common.vendorName"),"x-component-props":{disabled:!0}},"perPlanId.contractClass":{type:"string","x-decorator":"FormItem",title:i18nExpression("contract_mod.contractType"),"x-component":"DictSelect","x-component-props":{disabled:!0,code:"ELEM_CONTRACT_TYPE"}},"perPlanId.buName":{type:"string","x-decorator":"FormItem",title:i18nExpression("cusEntry.vendorMod.orgName"),"x-component-props":{disabled:!0}},perAcceptanceNo:{type:"string","x-decorator":"FormItem",title:"合同验收单号","x-component-props":{disabled:!0}},status:{type:"string","x-decorator":"FormItem",title:"状态","x-component":"DictSelect","x-component-props":{code:"CONTRACT_CHECK_STATUS",disabled:!0}},"perPlanId.perPlanNo":{type:"string","x-decorator":"FormItem",title:"合同履约计划单号","x-component-props":{disabled:!0}},"perPlanId.includeTaxAmount":{type:"string","x-decorator":"FormItem",title:"合同总金额（含税）","x-component-props":{disabled:!0}},"perPlanId.currencyCode":{type:"string","x-component":"DictSelect","x-component-props":{code:"currency",disabled:!0},"x-decorator":"FormItem",title:"币种"},"perPlanId.createdFullName":{type:"string","x-decorator":"FormItem",title:i18nExpression("common.creator"),"x-disabled":!0},"perPlanId.creationDate":{...yearMonthDaySelectorSegment,default:void 0,"x-decorator":"FormItem",title:i18nExpression("common.creationTime"),"x-disabled":!0}}}}},perPlanMilestone:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("contractMod.milestone")},properties:{perPlanMilestoneId:{type:"array","x-component":"RenderTable","x-component-props":{class:"table-view-vxe-table",preColumns:"seq",pagination:!1,maxHeight:"45vh"},"x-query-engine-skip":!0,"x-query-engine-relation":"perPlanMilestoneId:*",properties:generateXindexInOrder({serialNumber:{type:"string",title:i18nExpression("components.processTable.headers.fdNodeName"),"x-render-table-column":{minWidth:130}},milestoneType:{type:"string",title:i18nExpression("contract_mod.processNodeName"),"x-component":"DictSelect","x-component-props":{code:"MILESTONE_SCHEDULE"},"x-render-table-column":{minWidth:130}},nodePersonName:{type:"string",title:"节点负责人","x-render-table-column":{minWidth:130}},planStartDate:{title:i18nExpression("perfMod.planStartDate"),...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                                parseTime(row.planStartDate, '{y}-{m}-{d}')
                              }`)},"x-render-table-column":{minWidth:130}},planEndDate:{title:"计划结束时间",...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                                parseTime(row.planEndDate, '{y}-{m}-{d}')
                              }`)},"x-render-table-column":{minWidth:130}},nodePlanNum:{type:"number","x-disabled":expression(`
                            $disabled
                              || ['SUPPLIER_SUBMITTED','FIRST_PASS'].includes($values.status)
                              || $form.values.dataCreationType === 'VENDOR'
                            `),"x-render-table-column":{title:"节点交付数量",minWidth:130},...editTableFormItemValid},practicallyEndDate:{...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                                parseTime(row.practicallyEndDate, '{y}-{m}-{d}')
                              }`)},"x-disabled":expression(`
                            $disabled
                              || ['SUPPLIER_SUBMITTED','FIRST_PASS'].includes($values.status)
                              || $form.values.dataCreationType === 'VENDOR'
                            `),"x-render-table-column":{title:"实际结束时间",minWidth:130},...editTableFormItemValid},remarks:{type:"string",title:"特殊备注","x-render-table-column":{minWidth:130}},fileName:{type:"string","x-hidden":!0,"x-query-engine-skip":!0},fileId:{type:"string","x-hidden":!0,title:i18nExpression("dataConfMod.attachmentTemplate"),"x-component":"SrmCommonFile","x-component-props":{readonly:!0,"default-file":{fileId:"{{$table.getRowByIndex($self.index)?.fileId}}",fileName:"{{$table.getRowByIndex($self.index)?.fileName}}"}},"x-render-table-column":{minWidth:130}}})}}},perAcceptanceConf:{type:"void","x-component":"CollapseItem","x-component-props":{title:"履约过程评价"},"x-hidden":!0,properties:{perAcceptanceConfList:{type:"string","x-component":"IFieldView","x-query-engine-skip":!0,"x-query-engine-relation":"perAcceptanceConfList:*",default:[],"x-component-props":{disabled:expression("$disabled")}}}},delivery:{type:"void","x-component":"CollapseItem","x-component-props":{title:"验收意见"},properties:{deliveryExplain:{type:"string","x-decorator":"FormItem","x-component":"Input.TextArea","x-component-props":{disabled:"{{$disabled}}",rows:"3",maxlength:"300","show-word-limit":!0}}}},relevantAttachment:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("accountMod.relevantAttachment")},properties:{toolbar:{type:"void","x-component":"Space","x-component-props":{style:"margin-bottom: 16px"},properties:{add:{type:"void",title:i18nExpression("common.add"),"x-component":"RButton","x-component-props":{type:"primary",disabled:"{{$disabled}}","@click":expression(`(rowIndex) => {
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
                                      }`)}}}}}}})}}}}}}}}}}}}),$back=$bus=>{emitTabRemove(attrs.tabName),$bus.$emit("PerAcceptance")},getFormDetail=(data={},$form,$queryEngine)=>$queryEngine.request.query({perAcceptanceId:data?.perAcceptanceId,perPlanMilestoneId:data?.perPlanMilestoneId?.perPlanMilestoneId}).then(res=>(workflowStatus.value=res.data.status,$form.setValues(res.data),res.data)),$saveBill=async(type,$form,$queryEngine,$bus)=>{let validResult=!0,data=deppOmit($form.values,["lastUpdateDate","creationDate","creationDate","contractCreationDate","currentPlanEndDate"]);if(data=omit(data,["perPlanId.contractClass","perPlanId.contractNo","perPlanId.includeTaxAmount","perPlanId.vendorName","perPlanId.buName","perPlanId.currencyName","perPlanId.templateName","perPlanId.perPlanNo","perPlanId.processNum","perPlanMilestoneId.serialNumber","perPlanMilestoneId.milestoneType","perPlanMilestoneId.nodePersonName","perPlanMilestoneId.planStartDate","perPlanMilestoneId.planEndDate","perPlanMilestoneId.remarks","perPlanMilestoneId.fileId","perAcceptanceAttList.fileName","perAcceptanceAttList.createdUserName"]),type==="SAVE")$submitData("save",data,$form,$queryEngine);else if(type==="SUBMIT")await $form.validate().then(async()=>{validResult=await $submitData("submit",data,$form,$queryEngine)}).catch(err=>{validResult=!1});else if(type==="PASS"||type==="REJECT"){let{approvePass,rejected}=performPlanService.performAcceptance;(type==="PASS"?approvePass:rejected)(data).then(res=>{app.$message.success(res.message),$back($bus)})}return validResult},$submitData=async(type,$values,$form,$queryEngine,$bus)=>{if(type==="submit"){for(let i=0;i<$values.perPlanMilestoneId.length;i+=1)if(!$values.perPlanMilestoneId[i].practicallyEndDate){app.$message.warning("请填写里程碑---实际结束时间");return}}let fileNum=0;return($values.perAcceptanceAttList||[]).forEach(row=>{fileNum+=row.fileName?1:0}),($values.perPlanMilestoneId||[]).forEach(row=>{row.fileNum=fileNum}),$values.perPlanMilestoneId=$values.perPlanMilestoneId?.[0],await $queryEngine.request.save($values,{customizeAction:vendor()&&type==="submit"?"vendorSubmit":void 0}).then(res=>{type==="save"&&app.$message.success(t("common.successSave"))}),!0},$getPdfFile=async(flag=!1)=>{let htmlBody=(document.getElementById("printContent")?.innerHTML??"").replace('disabled="disabled"'," ");const res=await http.post("/egg/upload",{options:{format:"a4",margin:{left:"1cm",top:"1cm",right:"1cm",bottom:"1cm"}},htmlString:'<div style="page-break-inside: avoid;overflow: hidden;font-family: simsun;">'+htmlBody+"</div>"},{responseType:"arraybuffer",loading:!0,baseURL:"",returnDirectly:!0}),blob=new Blob([res.data],{type:"application/pdf"}),formData=new FormData;formData.append("file",blob,"myfile.pdf");const pdf=await http.post("/api-base/pdf/pdfAddWatermark",formData,{headers:{contentType:"form-data"},responseType:"arraybuffer",loading:!0,returnDirectly:!0});let blobs=new Blob([pdf.data],{type:"application/pdf"});if(flag){const iframeNode=document.getElementById("pdfIframe");iframeNode&&(iframeNode.src=URL.createObjectURL(blobs),setTimeout(()=>{iframeNode.contentWindow.print()},1e3))}return blobs},$openPrint=$form=>{const{perAcceptanceId,perPlanMilestoneId}=$form.values;let planMilestoneId=null;perPlanMilestoneId&&perPlanMilestoneId.length&&(planMilestoneId=perPlanMilestoneId[0].perPlanMilestoneId);const xml=encodeURIComponent("database:合同验收.ureport.xml"),params=encodeURIComponent(`perAcceptanceId=${perAcceptanceId}&perPlanMilestoneId=${planMilestoneId}`),url=`${systemUrl}/#/pdfPrint?xml=${xml}&params=${params}`;window.open(url)},$wrapper=(options,$root)=>Object.keys(options).reduce((acc,key)=>(acc[key]=options[key].bind($root),acc),{}),$preOptions={nextStep:async function(){return await $saveBill("SUBMIT",this.$form,this.$queryEngine,this.$bus)}},$approvalHanlder=(type,$form,$queryEngine,$bus)=>{switch(type){case"save":if($form.values.contractStatus==="SUPPLIER_SUBMITTED")return app.$message.warning(t("cusEntry.approval.supplierConfirmed")),!1;$saveBill("SAVE",$form,$queryEngine,$bus);break;case"submit":$back($bus);break;case"abandon":$back($bus);break;case"recall":$back($bus);break;case"pass":$back($bus);break}};return{__sfc:!0,IFieldView,http,app,emitTabRemove,t,vendor,attrs,workflowStatus,$disabled,customUpdateButton,viewUpdateButton,disabledUpdateButton,schema,$back,getFormDetail,$saveBill,$submitData,$getPdfFile,$openPrint,$wrapper,$preOptions,$approvalHanlder,scope:{$wrapper,$preOptions,$approvalHanlder,app,t,$attrs:attrs,performPlanService,$disabled,emitTabRemove,getFormDetail,$saveBill,$back,$getPdfFile,$openPrint},components:{IFieldView,ApprovalProcess},RenderEngine}}});var _sfc_render=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{schemaKey:"contractPerformanceCheckDetail",readOnly:_setup.$disabled,pageAttrs:_vm.$attrs,schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const ContractPerformanceCheckDetail=__component__.exports;export{ContractPerformanceCheckDetail as default};
