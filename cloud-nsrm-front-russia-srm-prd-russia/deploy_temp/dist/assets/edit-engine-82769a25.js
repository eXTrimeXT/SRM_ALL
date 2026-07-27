import{al as defineComponent,am as usePageHelper,an as useAttrs,br as useDebounceFn,c8 as action,c6 as DictClass,ac as createDictClass,bx as FILE_UPLOAD,c9 as nextTick,ca as loadJS,ar as defineSchemas,af as i18nExpression,cb as checkboxByYOrNSegment,ae as expression,ai as generateXindexInOrder,cc as formGridSegment,aC as requiredValidatorSegment,bt as changeFieldVisibleByDeps,c5 as yearMonthDayHourMinuteSecondSelectorSegment,ag as yearMonthDaySelectorSegment,ah as radioGroupByYOrNSegment,bX as queryFieldValueExpression,aj as editTableFormItemValid,cd as selectByYOrNSegment,as as RenderEngine,aa as cloneDeep,ce as markRaw,bs as toJS,n as normalizeComponent}from"./index-17d0ccd5.js";import{c as contractManagement}from"./index-6a91ec6d.js";import{P as Parser}from"./index-a2568972.js";import{C as CFillProgress}from"./index-6af40985.js";import{n as numericUppercase}from"./number-f5ee71d6.js";import"./enum-d9c76693.js";import"./z-material-table-78984a65.js";import"./index-531039c3.js";import"./util-6482eb24.js";import"./validate-8a9c1e8f.js";import"./uniqueId-bf6f89eb.js";/* empty css                                              */import"./big-e21bdbb6.js";const _sfc_main=defineComponent({__name:"edit-engine",setup(__props){const{http,emitTabRemove,app,t,eqY,eqN,buyer,vendor}=usePageHelper(),attrs=useAttrs(),viewUpdateButtonSave=$form=>attrs.params.flag=="view"?!1:$form.values.needVendorConfirm!="Y"&&(["DRAFT","WITHDRAW","REJECTED"].includes($form.values.contractStatus)||(attrs.params?attrs.params.flag==="add"||attrs.params.flag==="termination"||attrs.params?.contractType=="TERMINATION":null)),viewUpdateButtonsubmit=$form=>{if(attrs.params.flag=="view")return!1;const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;return attrs.params?.flag==="termination"||attrs.params?.contractType=="TERMINATION"?buyer()&&["SUPPLIER_CONFIRMED"].includes(attrs.params?.row?.contractStatus)?!0:$form.values.needVendorConfirm!="Y"&&buyer()&&(["DRAFT","WITHDRAW","REJECTED"].includes($form.values.contractStatus)||(attrs.params?attrs.params.flag==="termination"||attrs.params?.contractType=="TERMINATION":null)):componentInstance.workflowParamsInfo.integrationMode!=="None"?$form.values.needVendorConfirm!="Y"&&buyer()&&(["DRAFT","WITHDRAW","REJECTED"].includes($form.values.contractStatus)||(attrs.params?attrs.params.flag==="add":null)):$form.values.needVendorConfirm!="Y"?!!(buyer()&&(["DRAFT","WITHDRAW","REJECTED","SUPPLIER_CONFIRMED"].includes($form.values.contractStatus)||attrs.params&&attrs.params.flag==="add")):!!(buyer()&&["SUPPLIER_CONFIRMED"].includes($form.values.contractStatus))},disabledUpdateButton=$form=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance,state=$form.values.contractStatus;return componentInstance.workflowParamsInfo.integrationMode=="None"&&["SUPPLIER_CONFIRMED"].includes(state)?!1:attrs.params.flag==="view"?!0:(attrs.params.flag==="add"||attrs.params.flag==="termination"||!buyer()&&state==="SUPPLIER_CONFIRMING"||state&&["DRAFT","REFUSED","WITHDRAW"].includes(state),!1)},updateWorkflowconfig=(componentInstance,businessId,tabDisabled,businessVariables)=>{componentInstance.setWorkflowBusinessId(businessId),componentInstance.setWorkflowTabDisabled(tabDisabled),componentInstance.setWorkflowBusinessVariables(businessVariables)},handleButtonConfig=($form,componentInstance)=>{componentInstance.buttonConfigInfo.save.view=viewUpdateButtonSave($form),componentInstance.buttonConfigInfo.submit.view=viewUpdateButtonsubmit($form),componentInstance.buttonConfigInfo.save.disabled=disabledUpdateButton($form),componentInstance.buttonConfigInfo.submit.disabled=disabledUpdateButton($form);const contractHeadId=$form.values.contractHeadId||"",tabDisabled=!["SUPPLIER_CONFIRMED","REJECTED","WITHDRAW","APPROVAL","UNDER_REVIEW","UN_ARCHIVED","SIGNATUREING","ARCHIVED","TERMINATED","ABANDONED"].includes($form.values.contractStatus)||$form.values.contractStatus==="DRAFT";updateWorkflowconfig(componentInstance,contractHeadId,tabDisabled,{})},initButtonConfig=$form=>{setTimeout(()=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.buttonConfigInfo.save.name="暂存",componentInstance.buttonConfigInfo.cancel.view=!1,componentInstance.buttonConfigInfo.close.view=!1,handleButtonConfig($form,componentInstance)},50)},updateButtonConfig=$form=>{setTimeout(()=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;handleButtonConfig($form,componentInstance)},50)},$compileMarkedContent=async($form,modeContent,isInit=!0)=>{const markedContentNode=document.getElementById("markedContent");if(!markedContentNode)return;markedContentNode.innerHTML="";const initialize=isInit&&attrs.params.flag==="add",breakPageMatcher=/_ueditor_page_break_tag_/g;if(modeContent=modeContent.replace(breakPageMatcher,()=>'<div class="breakPage" style="break-after: page;"></div>'),modeContent){const{vueTemplate,elementCodes}=Parser.replacer(modeContent,initialize),elemKeys=isInit?$form.values.modelLines.reduce((obj,i)=>{const{modelKey,modelValue}=i;let value=modelValue;try{isNaN(modelValue)&&(value=JSON.parse(modelValue))}catch{}return obj[modelKey]=value,obj},{}):elementCodes;generateComponent({node:markedContentNode,$form,html:vueTemplate,elemKeys,onInit:componentInstance=>{$form.query("state").take(field=>{field.setData({contractTemplateComponentInstance:markRaw(componentInstance)})})}})}},initData=$form=>{if($form.values.contractType=attrs.params.contractType,attrs.params.flag==="add")if(["MIAN_CONTRACT_ALTER","SUPPLEMENTAL_AGREEMENT"].includes($form.values.contractType))$form.values.contractHeadId=attrs.params.rowId;else return!1;else $form.values.contractHeadId=attrs.params.row.contractHeadId;return updateButtonConfig($form),!0},$cancel=$bus=>{emitTabRemove(attrs.params?.flag==="add"?"contractInformation":attrs.params?.flag==="termination"||attrs.params?.contractType==="TERMINATION"?"termination"+attrs.params?.row.contractName:"contractInformation"+attrs.params?.row.contractName),$bus.$emit("ContractHead")},$getPdfFile=async(flag=!1)=>{let htmlBody=(document.getElementById("printContent")?.innerHTML??"").replace('disabled="disabled"'," ");const res=await http.post("/egg/upload",{options:{format:"a4",margin:{left:"1cm",top:"1cm",right:"1cm",bottom:"1cm"}},htmlString:'<div style="page-break-inside: avoid;overflow: hidden;font-family: simsun;">'+htmlBody+"</div>"},{responseType:"arraybuffer",loading:!0,baseURL:"",returnDirectly:!0}),blob=new Blob([res.data],{type:"application/pdf"}),formData=new FormData;formData.append("file",blob,"myfile.pdf");const pdf=await http.post("/api-base/pdf/pdfAddWatermark",formData,{headers:{contentType:"form-data"},responseType:"arraybuffer",loading:!0,returnDirectly:!0});let blobs=new Blob([pdf.data],{type:"application/pdf"});if(flag){const iframeNode=document.getElementById("pdfIframe");iframeNode&&(iframeNode.src=URL.createObjectURL(blobs),setTimeout(()=>{iframeNode.contentWindow.print()},1e3))}return blobs},$calcIncludeTaxAmount=useDebounceFn($form=>{if($form.values.contractStatus==="ARCHIVED")return;const materialListData=$form.query("materialListData").get("value");if(!materialListData?.length)return;const totalAmount=materialListData.reduce((sum,item)=>Number(sum)+Number(item.amount),0);Number.isNaN(totalAmount)||($form.values.totalItems=totalAmount+"元",$form.values.totalMaterialAmount=numericUppercase(totalAmount),$form.query("includeTaxAmount").take(field=>{field.value=Number(totalAmount).toFixed(2)}))},1e3),$isTermination=attrs.params?.flag==="termination"||attrs.params?.contractType==="TERMINATION",$handleSubmit=async($form,$queryEngine,$bus,type="submit")=>{const temporaryData=type==="savePublish",run=async values=>{$calcIncludeTaxAmount($form);const state=$form.query("state").get("data"),workFlow=["approval","publish"].includes(type);if(eqY(values.ceeaIsPortableContract)&&workFlow&&!temporaryData&&values.includeTaxAmount>2e4){app.$message.warning(t("contractMod.msgContractManage[10]")),values.ceeaIsPortableContract="N";return}if(!temporaryData&&new Date(values.effectiveDateFrom.replace(/-/g,"/"))>new Date(values.effectiveDateTo.replace(/-/g,"/")))return app.$message.error(t("合同有效期有误"));let bolpartnerType=0,bolpartnerType2=0;if(values.partnerData.forEach(item=>{item.partnerType=="甲方"&&bolpartnerType++,item.partnerType=="乙方"&&bolpartnerType2++}),!temporaryData&&bolpartnerType>1)return app.$message.error(t("只能有一个甲方"));if(!temporaryData&&bolpartnerType2>1)return app.$message.error(t("只能有一个乙方"));let isNull=!values.fileUploads.length||values.fileUploads.some(i=>!i.fileuploadId);if(!temporaryData&&isNull&&($isTermination||workFlow&&values.modelEnable=="N"))return app.$message.error(t("contractMod.msgContractManage[11]"));const modelLines=[],elemKeys=state.contractTemplateComponentInstance?.elemKeys;if(elemKeys)for(const[key,value]of Object.entries(elemKeys))try{const modelLineId=(values.modelLines.find(i=>key===i.modelKey)||{}).modelLineId;value&&(Array.isArray(value)?modelLines.push({modelLineId:modelLineId||null,modelKey:key,modelValue:JSON.stringify(value)}):modelLines.push({modelLineId:modelLineId||null,modelKey:key,modelValue:value}))}catch{}let finalHTML=null;try{finalHTML=Parser.unReplacer(document.getElementById("markedContent")?.innerHTML)}catch{}if(workFlow&&eqY(state.ceeaIfVirtual)&&!temporaryData&&!values.frameworkAgreementCode)return app.$message.error(t("contractMod.msgContractManage[12]"));if(eqN(values.isFrameworkAgreement)&&workFlow){const totalPercent=values.payPlanData.reduce((sum,item)=>sum+Number(item.paymentRatio),0),totalMoney=values.payPlanData.reduce((sum,item)=>sum+Number(item.stagePaymentAmount),0);if(!temporaryData&&totalPercent!==100&&!$isTermination){app.$message.error("付款比例之和必须等于100！");return}if(!$isTermination){const includeTaxAmount=$form.query(".includeTaxAmount").take().value;if(!temporaryData&&includeTaxAmount!=totalMoney)return app.$message.error("阶段付款金额总和应与合同总金额相等"),!1}if(!temporaryData&&!values.materialListData.length&&!$isTermination)return app.$message.error(t("contractMod.msgContractManage[27]"));if(!temporaryData&&!values.payPlanData.length&&workFlow&&!$isTermination)return app.$message.error(t("contractMod.msgContractManage[15]"));const payPlanDataBol=values.payPlanData.some(e=>!(e.paymentPeriod&&e.paymentStage&&e.payExplain&&e.dateNum&&e.paymentRatio&&e.plannedPaymentDate&&e.payMethod));if(!temporaryData&&payPlanDataBol&&workFlow&&!$isTermination){app.$message.error(t("contractMod.payPlanDataBol"));return}let materialListDataBol=values.materialListData.some(e=>!(e.invId&&e.tradingLocations&&e.materialCode&&e.untaxedPrice&&e.contractQuantity&&e.taxRate));if(!temporaryData&&materialListDataBol&&workFlow&&!$isTermination){app.$message.error(t("contractMod.materialListDataBol"));return}}const{fileUploads,payPlanData,partnerData,materialListData,...rest}=toJS(values);["MIAN_CONTRACT_ALTER","SUPPLEMENTAL_AGREEMENT","TERMINATION"].includes($form.values.contractType)&&(attrs.params?.flag=="add"||attrs.params?.flag=="termination")&&(values.fileUploads.forEach(e=>{delete e.annexId,delete e.contractHeadId}),values.payPlanData.forEach(e=>{delete e.payPlanId,delete e.contractHeadId}),materialListData.forEach(e=>{delete e.contractMaterialId,delete e.contractHeadId}),values.partnerData.forEach(e=>{delete e.partnerId,delete e.contractHeadId}),modelLines.forEach(e=>{delete e.modelLineId}));const data={...rest,modelLines,annexes:values.fileUploads,payPlans:values.payPlanData,contractMaterials:materialListData,contractPartners:values.partnerData};finalHTML&&(data.content=finalHTML),data.isDeleteLine="Y",data.isSavePerCheck="Y",buyer()?type==="approval"?data.contractStatus=data.contractStatus??"DRAFT":type==="publish"?data.contractStatus="SUPPLIER_CONFIRMING":data.contractStatus="DRAFT":type==="SUPPLIER_CONFIRMING"?data.contractStatus="SUPPLIER_CONFIRMED":type==="SUPPLIER_REFUSE"?data.contractStatus="SUPPLIER_REJECTED":data.contractStatus="SUPPLIER_CONFIRMING";const resetContractHeadId=obj=>{obj?.contractHeadId&&(obj.contractHeadId=null)};["MIAN_CONTRACT_ALTER","SUPPLEMENTAL_AGREEMENT","TERMINATION"].includes(attrs.params.contractType)&&(data.mainContractNo=attrs.params.mainContractNo,attrs.params.flag!=="edit"&&(data.contractType=attrs.params.contractType,data.contractOldCode=values.contractNo,data.ceeaContractOldId=values.contractHeadId,resetContractHeadId(data),["modelLines","annexes","payPlans","contractMaterials","contractPartners"].forEach(relationTable=>{const relationTableData=data[relationTable];if(Array.isArray(relationTableData)&&relationTableData.length){data[relationTable].forEach(item=>{resetContractHeadId(item)});return}resetContractHeadId(relationTableData)}))),attrs.params.termination&&(data.contractType="TERMINATION",attrs.params?.flag!=="edit"&&type==="SUPPLIER_CONFIRMING"&&(data.contractStatus="TERMINATED"));const res=await $queryEngine.request.save(data,{customizeAction:vendor()&&type==="publish"?"publish":void 0}),contractHeadId=res.originalData.records[0]||values.contractHeadId;if(values.contractHeadId||$form.setValues({contractHeadId,...res.data[0],contractType:attrs.params.contractType}),type==="approval"){const tabDisabled=!["SUPPLIER_CONFIRMED","REJECTED","WITHDRAW","APPROVAL","UNDER_REVIEW","UN_ARCHIVED","SIGNATUREING","ARCHIVED","TERMINATED","ABANDONED"].includes(values.contractStatus)||values.contractStatus==="DRAFT",componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.setWorkflowBusinessId(contractHeadId),componentInstance.setWorkflowTabDisabled(tabDisabled),componentInstance.setWorkflowBusinessVariables({}),componentInstance.handlerAfter("SUBMIT");return}if(["savePublish","publish","SUPPLIER_CONFIRMING","SUPPLIER_REFUSE",""].includes(type)){$cancel($bus);return}};type!=="savePublish"&&await $form.validate(),run($form.values)},$handlePreview=$form=>{const state=$form.query("state").get("data");state.contractTemplateComponentInstance&&(state.contractTemplateComponentInstance.editable=!1),state.contenteditable=!1},$saveBill=(type,$form,$queryEngine,$confirm,$message,$bus,$eqY)=>{const modelHeadId=$form.query("modelHeadId").get("value"),contractType=$form.query("contractType").get("value"),modelEnable=$form.query("modelEnable").get("value"),state=$form.query("state").get("data"),contractStatus=$form.query("contractStatus").get("value");if(modelHeadId&&state.contractTemplateComponentInstance&&state.contractTemplateComponentInstance.editable&&(contractType==="MIAN_CONTRACT_ADD"||contractType==="MIAN_CONTRACT_ALTER"||contractType==="SUPPLEMENTAL_AGREEMENT")&&(contractStatus==="DRAFT"||contractStatus===""||contractStatus===null)&&$eqY(modelEnable)&&attrs.params.flag!=="view")return app.$message.warning(t("现在合同为编辑模式,请切换为浏览模式")),!1;type==="SUBMIT"?($handlePreview($form),setTimeout(()=>{$handleSubmit($form,$queryEngine,$bus,"approval")},50)):type==="SAVE"&&($handlePreview($form),setTimeout(()=>{$handleSubmit($form,$queryEngine,$bus,"")},50))},generateComponent=({node,$form,html,elemKeys,onInit})=>{const mergeForm=cloneDeep($form.values),$el=Parser.generateComponent({html,elemKeys,onInit,context:{mergeForm,partnerData:mergeForm.partnerData??[],materialEditableRows:mergeForm.materialListData??[]},wrapper:node});node.appendChild($el)},scope={$attrs:attrs,$eqY:eqY,$eqN:eqN,app,$reactiveAction:action,$dictClass:DictClass,$taxDictClass:createDictClass({tax:[]}),$isAdd:attrs.params?.flag==="add",$isTermination,$illegal:attrs.params?.flag,$illegalNotView:attrs.params?.illegal!=="view",$readOnly:!!attrs.params?.isReadOnly,$jumpLogin:!attrs.params?.jumpLogin,$contractManagement:contractManagement,numericUppercase,$calcIncludeTaxAmount,$handleSubmit,$cancel,$compileMarkedContent,$uploadPDF:async()=>{const blob=await $getPdfFile(),file=new window.File([blob],"myfile.pdf",{type:"application/pdf"}),data={file,uploadType:"DEF",sourceType:"WEB_APP",fileModular:"api-cm",fileFunction:"contractInformation",fileType:"pdf"},formData=new FormData;formData.append("file",file);for(const[key,value]of Object.entries(data))formData.append(key,value);const{data:file_data}=await http.post(FILE_UPLOAD,formData,{headers:{contentType:"form-data"},loading:!0});return file_data},$handlePreview,$html2diff:$form=>{nextTick(async()=>{const modelHeadId=$form.query("modelHeadId").get("value");$form.query("state").get("data");const res1=await contractManagement.getById(modelHeadId),res2=await contractManagement.modelLine.getModelLine(modelHeadId),initialModelValue=(attrs.params?.flag==="add"?res2.data:$form.values.modelLines).reduce((obj,i)=>{const{modelKey,modelValue}=i;let value=modelValue;try{value=JSON.parse(modelValue)}catch{}return obj[modelKey]=value,obj},{});let content=res1.data.content;const breakPageMatcher=/_ueditor_page_break_tag_/g;content=content?.replace(breakPageMatcher,()=>'<div class="breakPage" style="break-after: page;"></div>')??"";const templateNode=document.getElementById("templateNode");if(templateNode){templateNode.innerHTML="";const{vueTemplate}=Parser.replacer(content,!1);generateComponent({node:templateNode,$form,html:vueTemplate,elemKeys:initialModelValue})}const oldContent=templateNode?.innerHTML??"",newContent=document.getElementById("markedContent")?.innerHTML||"",open=textHtml=>{textHtml&&($form.query("diffChangeDialog").take()?.setComponentProps({visible:!0}),setTimeout(()=>{const diffChangeContentNode=document.getElementById("diffChangeContent");diffChangeContentNode&&(diffChangeContentNode.innerHTML=textHtml)}))};if(typeof Worker>"u")loadJS("./htmldiff.js",()=>{open(getHTMLDiff(oldContent,newContent))});else{const worker=new Worker("./htmldiff.js");worker.postMessage({newVersion:newContent,oldVersion:oldContent}),worker.onmessage=evt=>{open(evt.data)}}})},$getPdfFile,$calcMaterialTaxedPrice:($form,row)=>{if($calcIncludeTaxAmount($form),row.untaxedPrice&&row.contractQuantity){const unAmount=parseFloat(row.untaxedPrice)*parseFloat(row.contractQuantity);if(row.unAmount=unAmount.toFixed(2),row.taxRate){const amount=Number((unAmount*(1+row.taxRate/100)).toFixed(2)),num=Number(row.contractQuantity);row.amount=amount,row.taxQuota=Number(amount-unAmount).toFixed(2),row.taxedPrice=amount/num}return}if(row.taxedPrice&&row.contractQuantity){const amount=parseFloat(row.taxedPrice)*parseFloat(row.contractQuantity);if(row.amount=amount.toFixed(2),row.taxRate){const unAmount=Number((amount/(1+row.taxRate/100)).toFixed(2)),num=Number(row.contractQuantity);row.unAmount=unAmount,row.taxQuota=Number(amount-unAmount).toFixed(2),row.untaxedPrice=unAmount/num}}},initButtonConfig,updateButtonConfig,viewUpdateButtonSave,$saveBill,initData},vendorReadPrettyCollapseItemSegment={"x-read-pretty":expression("$vendor() || $form.readPretty")},schema=defineSchemas({pdfIframe:{type:"void","x-component":"iframe","x-component-props":{id:"pdfIframe",style:{display:"none"}}},templateNode:{type:"void","x-component":"div","x-component-props":{id:"templateNode",style:{display:"none"}}},diffChangeDialog:{type:"void",title:i18nExpression("contractMod.compareChange"),"x-component":"RDialog",properties:{diffChangeContainer:{type:"void","x-component":"HTMLElement","x-component-props":{style:{overflow:"hidden"}},properties:{diffChangeContent:{type:"void","x-component":"div","x-component-props":{id:"diffChangeContent",class:"conetnt paper",style:{width:"98%"}}}}}}},frameworkAgreementDialog:{type:"void",title:i18nExpression("contractMod.maintainFrameworkAgreement"),"x-component":"RDialog","x-component-props":{footer:!1},properties:{queryData:{type:"object",default:{},"x-data":{pageSize:9999,pageNum:1,vendorId:void 0,globalcontractIds:[]},"x-decorator":"FormLayout","x-decorator-props":{layout:"horizontal"},"x-component":"FormGrid","x-component-props":{maxColumns:4,columnGap:32,rowGap:0},properties:{vendorName:{type:"string",title:i18nExpression("common.vendor"),"x-decorator":"FormItem","x-disabled":!0},isFrameworkAgreement:{title:i18nExpression("bidMod.isFrameworkAgreement"),"x-disabled":!0,...checkboxByYOrNSegment,default:"Y"}}},queryEngine:{type:"void","x-decorator":"QueryEngine","x-query-engine":{service:"cm",type:"ContractHead",transformRequest:expression(`(data, headers) => {
            data.query.vendorId = {}
            data.query.ceeaIfVirtual = {}

            return data
          }`)},"x-query-engine-skip":!0,properties:{dialogTable:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",pagination:!1,maxHeight:"45vh"},"x-reactions":expression(`(field) => {
              const queryDataField = field.query('frameworkAgreementDialog.queryData').take()

              $effect(() => {
                if (queryDataField.value.vendorName) {
                  $queryEngine.state.paginationManagement.configState.value.pageSize = 999
                  $queryEngine.state.paginationManagement.queryParams.value = {
                    vendorId: { eq: queryDataField.data.vendorId },
                    isFrameworkAgreement: { eq: queryDataField.value.isFrameworkAgreement },
                    vendorName: { eq: queryDataField.value.vendorName },
                    contractStatus: { eq: 'ARCHIVED' },
                  }

                  $queryEngine.state.paginationManagement.refresh()
                }
              }, [queryDataField.data.vendorId])
            }`),properties:generateXindexInOrder({contractCode:{type:"string",title:i18nExpression("contractMod.contractCode"),"x-render-table-column":{}},contractName:{type:"string",title:i18nExpression("contractMod.contractName"),"x-render-table-column":{}},operation:{type:"void",title:i18nExpression("common.operation"),"x-render-table-column":{width:60},"x-component":"RenderTableButtonList",properties:{save:{type:"void",title:i18nExpression("common.save"),"x-component-props":{type:"text","@click":expression(`({ row }) => {
                        $values.frameworkAgreementId = row.contractHeadId
                        $values.frameworkAgreementName = row.contractName
                        $values.frameworkAgreementCode = row.contractCode

                        const state = $form.query('state').get('data')
                        // 虚拟合同 合同编号和框架协议编号一样
                        if ($eqY(state.ceeaIfVirtual)) {
                          $form.query('contractCode').take(field => {
                            field.value = row.contractCode
                          })
                        }

                        $closed()
                      }`)}}}}})}}}}},releaseSignPlatformDialog:{type:"void",title:i18nExpression("contractMod.releaseSignPlatform1"),"x-component":"RDialog","x-component-props":{beforeClose:expression(`(done, type) => {
        if (type !== 'ok') {
          done()
          return
        }

        $form.query('releaseSignPlatformDialog.releaseParams').take().submit(async (params) => {
          const modelEnable = $form.query('modelEnable').get('value')

          const data = {
            contractHeadId: $form.values.contractHeadId,
            ...params
          }

          if ($eqY(modelEnable)) {
            const fileData = await $uploadPDF()
            const { fileuploadId } = fileData

            data.fileuploadId = fileuploadId
          }

          // TODO MeiQL 接口改造
          // $queryEngine.request.save(data).then(() => {
          //   done()

          //   // 发布到签章平台成功！
          //   $message.success($t('contractMod.successPostSignPlatform'))

          //   $cancel($bus)
          // })

          $contractManagement.contract.release(data)
          .then(res => {
            done()

            // 发布到签章平台成功！
            $message.success($t('contractMod.successPostSignPlatform'))

            $cancel($bus)
          })
        })
      }`)},properties:{releaseParams:{type:"object",...formGridSegment,"x-read-pretty":!1,properties:{name:{type:"string",title:i18nExpression("dataConfMod.userName"),"x-decorator":"FormItem",...requiredValidatorSegment},phone:{type:"string",title:i18nExpression("contractMod.phone"),"x-decorator":"FormItem",...requiredValidatorSegment},email:{type:"string",title:i18nExpression("dataConfMod.email"),"x-decorator":"FormItem",...requiredValidatorSegment}}}}},state:{type:"void","x-component":"Fragment","x-hidden":!0,"x-data":{contractTemplateComponentInstance:null,contenteditable:!1,ceeaIfVirtual:"N",progress:[{code:"contractInfo",name:t("logisticsMod.contractInfo"),percentage:0},{code:"otherInfo",name:t($isTermination?"contractMod.terminationInformation":"vendorMod.otherInfo"),percentage:0},{code:"contractFinancialInformation",name:t("contractMod.contractFinancialInformation"),percentage:0},{code:"itemInfo",name:t("purchaseDemand.itemInfo"),percentage:0},{code:"paymentPlan",name:t("contractMod.paymentPlan"),percentage:0},{code:"partner",name:t("contractMod.partner"),percentage:0},{code:"fileInfo",name:t("contractMod.fileInfo"),percentage:0},{code:"contractContent",name:t("contractMod.contractContent"),percentage:0}],progressNo:[{code:"contractInfo",name:t("logisticsMod.contractInfo"),percentage:0},{code:"otherInfo",name:t($isTermination?"contractMod.terminationInformation":"vendorMod.otherInfo"),percentage:0},{code:"contractFinancialInformation",name:t("contractMod.contractFinancialInformation"),percentage:0},{code:"itemInfo",name:t("purchaseDemand.itemInfo"),percentage:0},{code:"paymentPlan",name:t("contractMod.paymentPlan"),percentage:0},{code:"partner",name:t("contractMod.partner"),percentage:0},{code:"fileInfo",name:t("contractMod.fileInfo"),percentage:0}]}},ContractHead:{type:"void","x-component":"el-container","x-component-props":{class:"flex-container contractMg",direction:"vertical"},"x-decorator":"QueryEngine","x-query-engine":{service:"cm",actions:{read:{immediate:!0,ready:expression(`() => {
            initButtonConfig($form)

            return initData($form)
          }`),transformRequest:expression(`(data, headers) => {
            data.payload = [$form.values.contractHeadId]

            data.query['*'] = {}
            data.query.modelLines = {
              '*': {},
            }

            // http.js 封装的 loading 不合理，这里手动维护
            $form.query('state').take(field => {
              field.setData({
                loadingInstance: app.$loading({
                  lock: true,
                  text: '加载中',
                  background: 'rgba(0, 0, 0, 0.4)'
                })
              })
            })

            return data
          }`),onSuccess:expression(`(res) => {
            // requestIdleCallback(() => {
              $form.query('state').take(field => {
                field.data.loadingInstance?.close()
              })
            // })

            const value = res.data[0]

            // 合同终止
            if ($attrs.params?.flag === 'termination') {
              value.fileUploads = [{
                fileuploadId: null,
                fileSourceName: '',
                fileType:
                'TERMINATION_AGREEMENT',
                del: 'N'
              }]
            }
            // 合作终止右方描点修改
            if ($attrs.params?.flag === 'termination' || $attrs.params?.contractType === 'TERMINATION') {
              let progress = [
                {
                  code: 'contractInfo',
                  name: $t('logisticsMod.contractInfo'),
                  percentage: 0
                },
                {
                  code: 'otherInfo',
                  name: $t($isTermination ? "contractMod.terminationInformation" : "vendorMod.otherInfo"),
                  percentage: 0
                },
                {
                  code: 'fileInfo',
                  name: $t('contractMod.fileInfo'),
                  percentage: 0
                }
              ]
              $form.query('state').get('data').progress = progress
            }
            value.totalItems = value?.includeTaxAmount
            value.totalMaterialAmount = numericUppercase(value?.includeTaxAmount)

            $form.setValues({
              ...value,
              contractType: $attrs.params.contractType || value.contractType
            })

            updateButtonConfig($form)

            // 单纯文本只读状态
            $form.readPretty = $readOnly || $attrs.params.flag === 'view'
            if (['SUPPLIER_CONFIRMED'].includes($attrs.params.row.contractStatus)) {
              $form.readPretty = true
            }
            if ($attrs.params.flag === 'add') {
              $form.query('.contractStatus').take(filed => {
                filed.value = ''
              })
            }

            setTimeout(() => {
              $compileMarkedContent($form, value.content || '', true)
            })
          }`)},save:{cascadeDeletion:!0,loading:!0}}},properties:{SchemaWorkflow:{type:"void","x-component":"SchemaWorkflow","x-component-props":{"business-id":expression("$form.values.contractHeadId || null"),"business-type":"CONTRACT","button-custom":expression("{}"),"@click-handler":expression(`(type) => {
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus, $eqY)
          }`),"@submit-direct":expression(`(type) => {
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus, $eqY)
          }`),"@confirm":expression(`(type, comment) => {
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus, $eqY)
          }`),"@close-tab":expression(`() => {
            $cancel($bus)
          }`),"@update-integration-mode":expression(`(integrationMode) => {
            updateButtonConfig($form)
          }`)},items:{type:"object","x-query-engine-skip":!0,properties:{cancel:{type:"void","x-content":i18nExpression("common.cancel"),"x-component":"Button","x-component-props":{type:"default","@click":expression(`() => {
                 $cancel($bus)
                }`)}},pdfPrint:{type:"void","x-content":i18nExpression("route.pdfPrint"),"x-component":"Button","x-component-props":{type:"default","@click":expression(`() => {
                      $handlePreview($form)
                      $getPdfFile(true)
                    }`)},"x-reactions":changeFieldVisibleByDeps(["modelHeadId"],`
                  !!$deps[0] && $illegalNotView && $attrs.params.flag !== 'view'
                  `)},editContractDetail:{type:"void","x-content":i18nExpression("contractMod.editContractDetail"),"x-component":"Button","x-component-props":{style:{margin:"8px"},"@click":expression(`() => {
                      const state = $form.query('state').get('data')

                      if (state.contractTemplateComponentInstance) {
                        state.contractTemplateComponentInstance.editable = true
                        $self.query('.previewContractDetail').take().visible = true
                        $self.visible = false
                      }

                      if ($eqN($values.enable)) {
                        state.contenteditable = true
                      }
                    }`)},"x-reactions":[expression(`(field) => {
                      const modelHeadId = $form.query('modelHeadId').get('value')
                      const contractType = $form.query('contractType').get('value')
                      const modelEnable = $form.query('modelEnable').get('value')
                      const state = $form.query('state').get('data')
                      const contractStatus = $form.query('contractStatus').get('value')

                      field.visible = !!modelHeadId
                        && (state.contractTemplateComponentInstance && !state.contractTemplateComponentInstance.editable)
                        && (contractType === 'MIAN_CONTRACT_ADD' || contractType === 'MIAN_CONTRACT_ALTER' || contractType === 'SUPPLEMENTAL_AGREEMENT')
                        && (contractStatus === 'DRAFT' || contractStatus === '' || contractStatus === null)
                        && $illegalNotView
                        && $eqY(modelEnable) && $attrs.params.flag !== 'view'
                    }`),{dependencies:["contractStatus"],fulfill:{state:{disabled:expression(`
                            // TODO 接入流程
                            // if (app.workflowParamsInfo.integrationMode == 'None' && ['SUPPLIER_CONFIRMED'].includes(state)) {
                            //   return false
                            // }

                            $readOnly || $isAdd
                              ? false
                              : (!$buyer() && $deps[0] === 'SUPPLIER_CONFIRMING')
                                ? false
                                : !['DRAFT', 'REFUSED', 'WITHDRAW'].includes($deps[0])
                          `)}}}]},previewContractDetail:{type:"void","x-content":i18nExpression("contractMod.previewContractDetail"),"x-component":"Button","x-component-props":{style:{margin:"8px"},type:"default","@click":expression(`() => {
                  $handlePreview($form)
                  $self.query('.editContractDetail').take().visible = true
                  $self.visible = false
                }`)},"x-reactions":{dependencies:["modelHeadId","contractType","modelEnable","contractStatus"],fulfill:{state:{visible:expression(`
                        !!$deps[0]
                        && $deps[3]
                        && $deps[1] === 'MIAN_CONTRACT_ADD'
                        && $deps[4] === 'DRAFT'
                        && $illegalNotView
                        && $eqY($deps[2]) && $attrs.params.flag !== 'view'
                    `)}}}},compareChange:{type:"void","x-content":i18nExpression("contractMod.compareChange"),"x-component":"Button","x-component-props":{style:{"margin-right":"8px"},"@click":expression(`() => {
                      $handlePreview($form)
                      $html2diff($form)
                    }`)},"x-reactions":changeFieldVisibleByDeps(["modelHeadId","enable","modelEnable"],`
                    (
                      (!!$deps[0] && $eqN($deps[1])) || $illegal === 'view'
                    ) && $eqY($deps[2]) && !$isTermination && $attrs.params.flag !== 'view'
                  `)},releaseSignPlatform:{type:"void","x-content":i18nExpression("contractMod.releaseSignPlatform"),"x-component":"Button","x-component-props":{"@click":expression(`() => {
                      $form.query('releaseSignPlatformDialog').take().setComponentProps({ visible: true })
                    }`)},"x-reactions":expression(`(field) => {
                    const contractType = field.query('contractType').get('value')
                    const contractStatus = field.query('contractStatus').get('value')
                    const formal = field.query('formal').get('value')

                    field.visible = $buyer()
                      && ['MIAN_CONTRACT_ADD', 'MIAN_CONTRACT_ALTER','SUPPLEMENTAL_AGREEMENT'].includes(contractType)
                      && contractStatus === 'APPROVAL'
                      && formal === 'ELECTRONIC_CONTRACT' && $attrs.params.flag !== 'view'
                  }`)},staging:{type:"void","x-content":i18nExpression("common.staging"),"x-component":"Button","x-component-props":{style:{"margin-left":"8px"},"@click":expression('() => $handleSubmit($form, $queryEngine, $bus, "savePublish")')},"x-reactions":changeFieldVisibleByDeps(["needVendorConfirm","contractStatus"],`
                    $eqY($deps[0]) && $buyer() &&
                    (['DRAFT', 'SUPPLIER_REJECTED', 'WITHDRAW', 'REJECTED'].includes($deps[1]) || $isAdd || $isTermination) && $attrs.params.flag !== 'view' && !['SUPPLIER_CONFIRMED'].includes($deps[1])
                  `)},releaseSupplier:{type:"void","x-content":i18nExpression("contractMod.releaseSupplier"),"x-component":"Button","x-component-props":{"@click":expression('() => $handleSubmit($form, $queryEngine, $bus, "publish")')},"x-reactions":changeFieldVisibleByDeps(["needVendorConfirm","contractStatus"],`
                    $eqY($deps[0]) && $buyer() &&
                    (['DRAFT', 'SUPPLIER_REJECTED', 'WITHDRAW', 'REJECTED'].includes($deps[1]) || $isAdd || $isTermination) && $attrs.params.flag !== 'view' && !['SUPPLIER_CONFIRMED'].includes($deps[1])
                  `)},confirm:{type:"void","x-content":i18nExpression("orderMod.buyerOrderSynergy.confirm"),"x-component":"Button","x-component-props":{"@click":expression('() => $handleSubmit($form, $queryEngine, $bus, "SUPPLIER_CONFIRMING")')},"x-reactions":changeFieldVisibleByDeps(["contractStatus"],`
                  $deps[0] === 'SUPPLIER_CONFIRMING' && !$buyer() && $attrs.params.flag !== 'view'
                  `)},refuse:{type:"void","x-content":i18nExpression("components.approvalHead.headers.refuse"),"x-component":"Button","x-component-props":{"@click":expression('() => $handleSubmit($form, $queryEngine, $bus, "SUPPLIER_REFUSE")')},"x-reactions":changeFieldVisibleByDeps(["contractStatus"],`
                  $deps[0] === 'SUPPLIER_CONFIRMING' && !$buyer() && $attrs.params.flag !== 'view'
                  `)}}},properties:{steps:{type:"void","x-decorator":"div","x-decorator-props":{class:"stepDiv"},"x-component":"Steps","x-component-props":{alignCenter:!0,finishStatus:"success"},"x-reactions":{dependencies:["contractStatus"],fulfill:{state:{"component[1].active":expression(`
                        $attrs.params.termination
                          ? (
                            ['DRAFT', '', 'ARCHIVED'].includes($deps[0])
                              ? 0
                              : $deps[0] === 'SUPPLIER_CONFIRMED'
                                ? 1
                                : $deps[0] === 'UNDER_REVIEW'
                                  ? 2
                                  : $deps[0] === 'TERMINATED'
                                    ? 3
                                    : 0
                          )
                          : (
                            $deps[0] === 'ARCHIVED'
                              ? 4
                              : $deps[0] === 'APPROVAL'
                                ? 3
                                : ['UNDER_REVIEW', 'REFUSED', 'SIGNATUREING', 'SUPPLIER_CONFIRMED'].includes($deps[0])
                                  ? 2
                                  : ['SUPPLIER_CONFIRMED', 'SUPPLIER_REJECTED'].includes($deps[0])
                                    ? 1
                                    : 0
                          )
                      `)}}},properties:{step1:{type:"void","x-component":"el-step","x-component-props":{title:expression("$t($attrs.params.termination ? 'contractMod.terminationRelease' : 'contractMod.contractRelease')")}},step2:{type:"void","x-component":"el-step","x-component-props":{title:expression("$t($attrs.params.termination ? 'contractMod.terminationDetermine' : 'contractMod.contractConfirmation')")},"x-reactions":{dependencies:["needVendorConfirm"],fulfill:{state:{visible:expression("$eqY($deps[0])")}}}},step3:{type:"void","x-component":"el-step","x-component-props":{title:expression("$t($attrs.params.termination ? 'contractMod.terminationApproval' : 'contractMod.contractApproval')")}},step4:{type:"void","x-component":"el-step","x-component-props":{title:expression("$t($attrs.params.termination ? 'contractMod.termination' : 'contractMod.contractSigning')")}},step5:{type:"void","x-component":"el-step","x-component-props":{title:i18nExpression("contractMod.contractFiling")},"x-visible":expression("!$attrs.params.termination")}}},collapse:{type:"void","x-component":"FormCollapse",properties:generateXindexInOrder({contractInfo:{type:"void","x-component":"FormCollapse.Item","x-component-props":{title:i18nExpression("logisticsMod.contractInfo"),id:"contractInfo"},"x-query-engine-skip":!0,"x-read-pretty":expression("$vendor() || $isTermination || $form.readPretty || $form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'"),properties:{contractType:{type:"string",default:"MIAN_CONTRACT_ADD","x-hidden":!0},layout:{type:"void",...formGridSegment,properties:{contractNo:{type:"string",title:i18nExpression("contractMod.contractNo"),"x-decorator":"FormItem","x-disabled":expression("$form.readPretty ? undefined : true")},contractStatus:{type:"string",title:i18nExpression("contractMod.status"),"x-visible":expression("!$isTermination"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"CONTRACT_STATUS",disabled:expression("$form.readPretty ? undefined : true"),"@change":expression("() => updateButtonConfig($form)")}},createdFullName:{type:"string",title:i18nExpression("contractMod.createdBy"),"x-visible":expression("!$isTermination"),"x-decorator":"FormItem","x-disabled":expression("$form.readPretty ? undefined : true")},creationDate:{title:i18nExpression("contractMod.creationDate"),"x-visible":expression("!$isTermination"),"x-decorator":"FormItem","x-disabled":expression("$form.readPretty ? undefined : true"),...yearMonthDayHourMinuteSecondSelectorSegment},contractName:{type:"string",title:i18nExpression("contractMod.contractName"),"x-decorator":"FormItem","x-component-props":{maxLength:100,showWordLimit:!0},...requiredValidatorSegment},buId:{type:"string",title:i18nExpression("contractMod.fullPathId"),"x-visible":expression("$buyer()"),"x-decorator":"FormItem","x-component":"OrganizationSelector","x-component-props":{placeholder:i18nExpression("common.pleaseSelect"),"read-pretty":"{{$form.readPretty || $vendor()}}","parent-id":-1,jumpLogin:expression("$jumpLogin"),"node-type":"OU",disabled:expression('$isTermination || $form.readPretty || $values.contractType !== "MIAN_CONTRACT_ADD"'),"@select":expression(`(node, value) => {
                                const { organizationCode, organizationName, fullPathId, organizationId } = node

                                // 设置到 form.values 上
                                $values.buCode = organizationCode
                                $values.buName = organizationName
                                $values.buFullPathId = fullPathId

                                const partnerData = $form.query('partnerData').take()

                                partnerData.value.forEach((e, index) => {
                                  if (e.partnerType == '甲方') {
                                    partnerData.remove(index)
                                  }
                                })

                                $self.disabled = true

                                $http({
                                  url: '/api-base/organization/organization/getCompanyByOuId',
                                  method: 'GET',
                                  loading: true,
                                  params: {
                                    organizationId: organizationId,
                                  }
                                }).then((res) => {
                                  if (res.code != '0') {
                                    $self.value = ''
                                  }

                                  const partnerDataItem = {
                                    partnerType: '甲方',
                                    ouId: res.data?.organization?.organizationId,
                                    partnerName: res.data?.organization?.organizationName,
                                    taxPayer: res.data?.orgCompany?.taxNumber
                                  }

                                  if (res.data.orgCompanyBankList.length > 0) {
                                    Object.assign(partnerDataItem, {
                                      partnerName: res.data.organization.organizationName,
                                      bankAccount: res.data.orgCompanyBankList[0].bankAccount,
                                      bankName: res.data.orgCompanyBankList[0].bankName
                                    })
                                  }
                                  if (res.data.orgCompanyPersonList.length > 0) {
                                    Object.assign(partnerDataItem, {
                                      contactName: res.data.orgCompanyPersonList[0].name
                                    })
                                  }
                                  if (res.data.orgCompanyAddressList.length > 0) {
                                    res.data.orgCompanyAddressList.forEach(datas=>{
                                      if (datas.isActive == 'Y') {
                                        Object.assign(partnerDataItem, {
                                          postCode : datas?.postalCode,
                                          phone: datas?.phone,
                                          address : datas?.address
                                        })
                                        return false
                                      }
                                    })
                                  }

                                  partnerData.push(partnerDataItem)
                                }, (resOrr) => {
                                  $self.value = ''
                                  $values.buCode = ''
                                  $values.buName = ''
                                  $values.buFullPathId = ''
                                }).finally(() => {
                                  $self.disabled = false
                                })
                              }`)},...requiredValidatorSegment},buName:{type:"string",title:i18nExpression("contractMod.fullPathId"),"x-hidden":expression("$buyer()"),"x-decorator":"FormItem"},vendorName:{type:"string",title:i18nExpression("contractMod.vendorName"),"x-decorator":"FormItem","x-component":"QuickSearchWrapper","x-component-props":{showKey:"companyName",propKey:"companyName","read-pretty":"{{$form.readPretty || $vendor()}}",name:"scc_sup_company_info_new","@close-quicksearch":expression(`(val, scope) => {
                                $values.vendorId = val ? val.companyId : ''
                                $values.vendorCode = val ? val.companyCode : ''
                                $values.erpVendorCode = val ? val.erpVendorCode : ''
                                $values.erpVendorId = val ? val.erpVendorId : ''
                                if (val.companyName) {
                                  const partnerData = $form.query('partnerData').take()

                                  if (!partnerData.value.some(item => item.partnerType === '乙方')) {
                                    partnerData.value.push({
                                      partnerType: '乙方',
                                      partnerName: val.companyName,
                                      lcCode: val.lcCode
                                    })
                                  }
                                }
                              }`)},"x-reactions":{dependencies:["contractType"],fulfill:{schema:{"component[1].disabled":expression('$deps[0] !== "MIAN_CONTRACT_ADD"')}}},...requiredValidatorSegment},formal:{type:"string",title:i18nExpression("contractMod.signingMethod"),"x-visible":expression("!$isTermination"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"CONTRACT_FORM2"},...requiredValidatorSegment},effectiveDateFrom:{title:i18nExpression("contractMod.contractValidFrom"),"x-decorator":"FormItem",...yearMonthDaySelectorSegment,...requiredValidatorSegment},effectiveDateTo:{title:i18nExpression("contractMod.contractValidTo"),"x-decorator":"FormItem",...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],"picker-options":expression(`{
                                disabledDate: (time) => {
                                  const effectiveDateFrom = $self.query('.effectiveDateFrom').get('value')

                                  return time.getTime() < (new Date(effectiveDateFrom)).getTime()
                                }
                              }`)},...requiredValidatorSegment},contractClass:{type:"string",title:i18nExpression("contractMod.mgsContractType"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"ELEM_CONTRACT_TYPE",beforeChange:expression(`(value) => {
                                const modelHeadIdField = $form.query('modelHeadId').take()

                                if (modelHeadIdField?.value) {
                                  return $confirm($t('contractMod.clearModelMsg'))
                                    .then(() => {
                                      // 重置模板名称
                                      modelHeadIdField.value = undefined
                                      document.getElementById('markedContent').innerHTML = ''
                                    })
                                }

                                return Promise.resolve()
                              }`)},"x-reactions":[{dependencies:["contractType"],fulfill:{schema:{"component[1].disabled":expression(`
                                      $deps[0] === 'MIAN_CONTRACT_ALTER'
                                        ? false
                                        : $deps[0] !== 'MIAN_CONTRACT_ADD'
                                          ? !!$self.value
                                          : false
                                    `)}}}],...requiredValidatorSegment},ceeaControlMethod:{type:"string",title:i18nExpression("vendorMod.controlMethod"),"x-visible":expression("!$isTermination"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"MANAGEMENT_CONTROL_MODEL"}},signingAddress:{type:"string",title:i18nExpression("contractMod.signingAddress"),"x-visible":expression("!$isTermination"),"x-decorator":"FormItem","x-decorator-props":{gridSpan:2},"x-component":"Input.TextArea","x-component-props":{placeholder:i18nExpression("common.pleaseTypeContents")}}}}}},otherInfo:{type:"void","x-component":"FormCollapse.Item","x-component-props":{title:expression('$t($isTermination ? "contractMod.terminationInformation" : "vendorMod.otherInfo")')},"x-query-engine-skip":!0,"x-read-pretty":expression("$vendor() || $form.readPretty || $form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'"),properties:{layout:{type:"void",...formGridSegment,properties:{needVendorConfirm:{title:i18nExpression("contractMod.supplierConfirmation"),"x-hidden":"{{$vendor()}}",...radioGroupByYOrNSegment,...requiredValidatorSegment,"x-component-props":{"@change":expression("() => updateButtonConfig($form)")}},modelEnable:{title:i18nExpression("vendorMod.enableFlagModel"),...radioGroupByYOrNSegment,...requiredValidatorSegment,"x-visible":expression("!$isTermination"),"x-component-props":{"@change":expression(`(value) => {
                                const fileUploads = $form.query('fileUploads').take()

                                if ($eqN(value)) {
                                  if (!fileUploads.value.some(row => row.fileType === 'CONTRACT_AGREEMENT')) {
                                    fileUploads.push({
                                      fileuploadId: null,
                                      fileSourceName: '',
                                      fileType: 'CONTRACT_AGREEMENT',
                                      del: 'N'
                                    })
                                  }

                                  return
                                }

                                fileUploads.value.forEach((row, index) => {
                                  if (row.fileType == 'CONTRACT_AGREEMENT') {
                                    fileUploads.remove(index)
                                  }
                                })
                              }`)}},modelName:{type:"string","x-hidden":!0},modelHeadId:{type:"string",title:i18nExpression("dataConfMod.templateName"),"x-decorator":"FormItem","x-component":"Select","x-component-props":{"@change":expression(`(val) => {
                                if (!val) return

                                const option = $self.dataSource.find(item => item.value === val)
                                $form.query('.modelName').take().value = option.label

                                $compileMarkedContent($form, (option && option.content) || '', false)
                              }`)},"x-reactions":[{dependencies:["modelEnable"],fulfill:{state:{visible:expression("!$isTermination && $eqY($deps[0])")}}},expression(`(field) => {
                                const contractClass = field.query('contractClass').get('value')
                                if (contractClass) {
                                  $queryEngine.request.query(
                                    { modelType: { eq: contractClass }, status: {eq: "VALID"}},
                                    { pageSize: 999, pageNum: 1 },
                                    {
                                      type: 'ModelHead',
                                      query: {
                                        modelCode: {}, modelName: {}, modelHeadId: {}, content: {}
                                    }
                                  }).then(res => {
                                    if (!res.data) {
                                      return
                                    }

                                    $self.dataSource = res.data.map(i => ({
                                      ...i,
                                      id: i.modelCode,
                                      label: i.modelName,
                                      value: i.modelHeadId,
                                      type: i.modelType
                                    }))
                                  })
                                }
                              }`)],...requiredValidatorSegment},enable:{type:"string",title:i18nExpression("contractMod.standardContract"),"x-decorator":"FormItem","x-component":"DictSelect",default:"Y","x-read-pretty":expression("$form.readPretty"),"x-component-props":{code:"YES_OR_NO","@change":expression(`(val) => {
                                if (val === 'Y') {
                                  $form.query('state').get('data').contenteditable = false
                                } else {
                                  $form.query('state').get('data').contenteditable = true
                                }
                              }`)},"x-visible":expression("!$isTermination"),"x-reactions":{dependencies:["contractType","modelEnable"],fulfill:{schema:{"x-disabled":expression(`
                                    $self.readPretty || !['MIAN_CONTRACT_ALTER','MIAN_CONTRACT_ADD'].includes($deps[0])
                                  `)},state:{visible:expression("!$isTermination && $eqY($deps[1])")}}},...requiredValidatorSegment},isInvalidOldContract:{title:i18nExpression("vendorMod.enableFlagInvalid"),...radioGroupByYOrNSegment,default:"N","x-decorator-props":{gridSpan:4},"x-component-props":{"@change":expression(`(value) => {
                                const fileUploads = $form.query('fileUploads').take()

                                if (value === 'Y') {
                                  const attr = fileUploads.value.map(item => item.fileType)

                                  if (!attr.includes('TERMINATION_AGREEMENT')) {
                                    fileUploads.push({
                                      fileuploadId: null,
                                      fileSourceName: '',
                                      fileType: 'TERMINATION_AGREEMENT',
                                      del: 'N'
                                    })
                                  }
                                } else {
                                  fileUploads.value.forEach((e, index) => {
                                    if (e.fileType == 'TERMINATION_AGREEMENT') {
                                      fileUploads.remove(index)
                                    }
                                  })
                                }
                              }`)},"x-reactions":{dependencies:["contractType"],fulfill:{schema:{"x-visible":expression(`
                                    $deps[0] === 'MIAN_CONTRACT_ALTER'
                                  `)}}},...requiredValidatorSegment}}},layout2:{type:"void",...formGridSegment,properties:{contractRemark:{type:"string",title:i18nExpression("bid_mod.remark"),"x-visible":expression("!$isTermination"),"x-component":"Input.TextArea","x-component-props":{rows:2,placeholder:i18nExpression("common.pleaseTypeContents")},"x-decorator":"FormItem","x-decorator-props":{gridSpan:4}},contractTerminationReason:{type:"string",title:i18nExpression("contractMod.reasonTermination"),"x-visible":expression("$isTermination"),"x-component":"Input.TextArea","x-component-props":{rows:2,maxlength:300,"show-word-limit":!0,placeholder:i18nExpression("common.pleaseTypeContents")},"x-decorator":"FormItem","x-decorator-props":{gridSpan:4},...requiredValidatorSegment},drafterOpinion:{type:"string",title:i18nExpression("vendorMod.loggerComment"),"x-component":"Input.TextArea","x-component-props":{placeholder:i18nExpression("common.pleaseTypeContents"),rows:2},"x-decorator":"FormItem","x-decorator-props":{gridSpan:4}}}}}},contractFinancialInformation:{type:"void","x-component":"FormCollapse.Item","x-component-props":{title:i18nExpression("contractMod.contractFinancialInformation")},"x-reactions":expression(`field => {
                      field.visible = !$isTermination

                      if (field.visible) {
                        field.readPretty = $vendor() || $form.readPretty || $form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'
                      }
                    }`),properties:{layout:{type:"void",...formGridSegment,"x-query-engine-skip":!0,properties:{includeTaxAmount:{type:"number",title:i18nExpression("contractMod.totalAmountTax1"),default:0,"x-decorator":"FormItem","x-component-props":{controls:!1,precision:2}},currencyCode:{type:"string",title:i18nExpression("contractMod.currencyCode"),default:"CNY","x-component":"DictSelect","x-component-props":{code:"currency"},"x-decorator":"FormItem",...requiredValidatorSegment},isFrameworkAgreement:{title:i18nExpression("contractMod.isFrameworkAgreement"),...radioGroupByYOrNSegment,...requiredValidatorSegment},frameworkAgreementCode:{type:"string",title:i18nExpression("contractMod.frameworkAgreementCode"),"x-decorator":"FormItem","x-content":expression(`{
                              append: {
                                functional: true,
                                render(h) {
                                  return $vendor() || $form.readPretty
                                    ? undefined
                                    : h('el-button', {
                                        props: { type: 'primary', icon: 'el-icon-search', disabled:$form.readPretty || $form.values.contractType == 'SUPPLEMENTAL_AGREEMENT' },
                                        on: {
                                          click: () => {
                                            const vendorNameField = $form.query('vendorName').take()

                                            if (!$values.vendorId || !vendorNameField.value) {
                                              // 请先选择供应商
                                              return $message.error($t('bid_mod.setPermissionError'))
                                            }

                                            $form.query('frameworkAgreementDialog').take().setComponentProps({ visible: true })

                                            setTimeout(() => {
                                              $reactiveAction(() => {
                                                const queryDataField = $form.query('frameworkAgreementDialog.queryData').take()
                                                queryDataField.value.vendorName = vendorNameField.value
                                                queryDataField.data.vendorId = $values.vendorId
                                              })
                                            })
                                          }
                                        }
                                      })
                                    }
                                  }
                                }
                            `)}}}}},itemInfo:{type:"void","x-component":"FormCollapse.Item","x-component-props":{title:i18nExpression("purchaseDemand.itemInfo")},"x-visible":expression("!$isTermination"),"x-read-pretty":expression("$form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'"),properties:{toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},"x-reactions":expression(`(field) => {
                          field.visible = !$form.readPretty
                        }`),properties:{add:{type:"void",title:i18nExpression("common.add"),"x-component-props":{type:"primary",disabled:expression("$vendor() || $form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'"),"@click":expression(`() => {
                                $self.query($getFieldParentFieldFormPath($self, 2).concat('materialListData'))
                                  .take(field => {
                                    field.componentProps.componentInstance.addRow()
                                  })
                              }`)}}}},materialListData:{type:"array","x-component":"RenderTable","x-component-props":{editMode:!0,maxHeight:400,pagination:!1,sortable:!1,primaryKey:"contractMaterialId",cascadeDeletion:!0},"x-query-engine-skip":!0,"x-query-engine-relation":"contractMaterials:*","x-read-pretty":expression("$vendor() || $form.readPretty"),properties:generateXindexInOrder({contractMaterialId:{type:"number","x-hidden":!0},lineNumber:{type:"number","x-hidden":!0},invName:{type:"string",title:i18nExpression("contractMod.invId"),"x-hidden":expression("$buyer()"),"x-render-table-column":{minWidth:180,static:!0}},invId:{type:"number",default:null,title:i18nExpression("contractMod.invId"),"x-render-table-column":{minWidth:180},"x-hidden":expression("!$buyer()"),"x-component":"OrganizationSelector","x-component-props":{jumpLogin:expression("$jumpLogin"),"read-pretty":expression("$vendor() || $form.readPretty || $form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'"),nodeType:"INV",parentId:queryFieldValueExpression("buId"),scope:expression("$table.getRowByIndex($self.index)"),"@select":expression(`(node, _) => {
                                const row = $table.getRowByIndex($self.index)
                                row.invCode = node && node.organizationCode
                                row.invName = node && node.organizationName
                                row.invFullPathId = node && node.fullPathId
                              }`)},...editTableFormItemValid},tradingLocations:{type:"string",title:i18nExpression("contractMod.tradingLocations"),"x-render-table-column":{minWidth:300},"x-component":"DictSelect","x-read-pretty":expression("$vendor() || $form.readPretty || $form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'"),"x-component-props":{code:expression("String($self.query('.invId').get('value') || '')"),"custom-select-type":expression(`
                                $self.query('.invId').get('value') ? 'RECEIVE_ADDRESS' : ''
                              `),style:{width:"100%"},"@change-value":expression(`(_, node) => {
                                const row = $table.getRowByIndex($self.index)
                                row.receiveContact = node.receiver
                                row.receiveTelephone = node.receiverPhone
                                // row.tradingLocations = node.siteName
                              }`)},...editTableFormItemValid},materialCode:{type:"number",default:void 0,title:i18nExpression("contractMod.materialCode"),"x-render-table-column":{minWidth:140},"x-component":"QuickSearchWrapper","x-component-props":{disabled:expression("$form.readPretty || !$self.query('.invId').get('value') || $vendor() || $form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'"),"show-input":expression("$self.value"),"read-pretty":"{{$form.readPretty}}","show-key":"materialCode",name:"scc_base_material_item_contract","pre-query-data":expression(`{
                                'o.ORGANIZATION_ID': $self.query('.invId').get('value')
                              }`),"@close-quicksearch":expression(`(val, scope) => {
                                const row = $table.getRowByIndex($self.index)

                                row.materialId = val ? val.materialId : null
                                row.materialName = val ? val.materialName : null
                                row.categoryName = val ? val.categoryName : null
                                row.categoryId = val ? val.categoryId : null
                                row.categoryCode = val ? val.categoryCode : null
                                row.specification = val ? val.specification : null
                                row.unitCode = val ? val.unit : null
                                row.unitName = val ? val.unitName : null
                              }`)},...editTableFormItemValid},materialName:{type:"string",title:i18nExpression("contractMod.materialName"),"x-render-table-column":{minWidth:140,static:!0}},categoryName:{type:"string",title:i18nExpression("contractMod.categoryName"),"x-render-table-column":{minWidth:140,static:!0}},untaxedPrice:{type:"number",default:void 0,title:i18nExpression("bid_mod.untaxedPrice"),"x-render-table-column":{minWidth:120},"x-component-props":{"@change":expression(`() => {
                                $calcMaterialTaxedPrice($form, $table.getRowByIndex($self.index))
                              }`)},...editTableFormItemValid},taxedPrice:{type:"number",default:void 0,title:i18nExpression("bid_mod.taxUnitPrice"),"x-render-table-column":{minWidth:120,static:!0},"x-component-props":{controls:!1,precision:2}},contractQuantity:{type:"number",default:void 0,title:i18nExpression("contractMod.contractQuantity"),"x-render-table-column":{minWidth:120},"x-component-props":{"@change":expression(`() => {
                                $calcMaterialTaxedPrice($form, $table.getRowByIndex($self.index))
                              }`)},...editTableFormItemValid},amount:{type:"number",default:void 0,title:i18nExpression("contractMod.amount2"),"x-render-table-column":{minWidth:120,static:!0}},unitName:{type:"string",title:i18nExpression("contractMod.unitName"),"x-render-table-column":{minWidth:120,static:!0}},taxRate:{type:"string","x-hidden":!0},taxKey:{type:"string",title:i18nExpression("contractMod.taxRate"),"x-render-table-column":{minWidth:120},"x-component":"DictSelect","x-component-props":{code:"tax","@change":expression(`(value) => {
                                const data = $taxDictClass.getDictDetail('tax', value)

                                const row = $table.getRowByIndex($self.index)

                                if (data) {
                                  row.taxRate = data.key
                                }

                                $calcMaterialTaxedPrice($form, row)
                              }`)},...editTableFormItemValid},unAmount:{type:"number",default:void 0,title:i18nExpression("contractMod.unAmount"),"x-render-table-column":{minWidth:120,static:!0}},taxQuota:{type:"number",default:void 0,title:i18nExpression("contractMod.taxQuota"),"x-render-table-column":{minWidth:120,static:!0}},ceeaUsedAmount:{type:"number",title:i18nExpression("contractMod.usedAmount"),default:0,"x-component-props":{controls:!1,precision:2},"x-render-table-column":{minWidth:150}},ceeaUsedNumber:{type:"number",title:i18nExpression("contractMod.usedNumber"),default:0,"x-component-props":{controls:!1,precision:2},"x-render-table-column":{minWidth:150}},startDate:{title:i18nExpression("bid_mod.priceStartTime"),"x-render-table-column":{minWidth:230},type:"date",default:null,"x-component-props":{placeholder:i18nExpression("common.pleaseSelectDate"),format:"yyyy-MM-dd","value-format":"yyyy-MM-dd"}},endDate:{title:i18nExpression("bid_mod.priceEndTime"),"x-render-table-column":{minWidth:230},type:"date",default:null,"x-component-props":{placeholder:i18nExpression("common.pleaseSelectDate"),format:"yyyy-MM-dd","value-format":"yyyy-MM-dd"}},specification:{type:"string",title:i18nExpression("contractMod.specification"),"x-render-table-column":{minWidth:120}},manufacturer:{type:"string",title:i18nExpression("contractMod.manufacturer"),"x-render-table-column":{minWidth:120}},isDangerChemistry:{type:"string",title:i18nExpression("contractMod.isDangerChemistry"),"x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"},"x-render-table-column":{minWidth:120}},placeOfOrigin:{type:"string",title:i18nExpression("contractMod.placeOfOrigin"),"x-render-table-column":{minWidth:120}},isInstallDebug:{title:i18nExpression("contractMod.isInstallDebug"),"x-render-table-column":{minWidth:140},...selectByYOrNSegment,"x-decorator":""},shelfLife:{type:"number",default:void 0,title:i18nExpression("contractMod.shelfLife"),"x-component-props":{controls:!1},"x-render-table-column":{minWidth:120}},lineRemark:{type:"string",title:i18nExpression("contractMod.lineRemark"),"x-render-table-column":{minWidth:120}},itemNumber:{type:"number",default:void 0,title:i18nExpression("contractMod.itemNumber"),"x-render-table-column":{minWidth:120}},itemName:{type:"string",title:i18nExpression("contractMod.itemName"),"x-render-table-column":{minWidth:120}},taskNumber:{type:"number",default:void 0,title:i18nExpression("contractMod.taskNumber"),"x-render-table-column":{minWidth:120}},taskName:{type:"string",title:i18nExpression("contractMod.taskName"),"x-render-table-column":{minWidth:120}},shipFrom:{type:"string",title:i18nExpression("contractMod.shipFrom"),"x-render-table-column":{minWidth:120}},destination:{type:"string",title:i18nExpression("contractMod.destination"),"x-render-table-column":{minWidth:120}},tradeTerm:{type:"string",title:i18nExpression("bidMod.tradeTerm"),"x-render-table-column":{minWidth:120},"x-component":"DictSelect","x-component-props":{code:"trade_clause"}},sourceNumber:{type:"number",default:void 0,title:i18nExpression("contractMod.sourceNumber"),"x-render-table-column":{minWidth:120,static:!0}},operation:{type:"void",title:i18nExpression("common.operation"),"x-render-table-column":{width:60,fixed:"right"},"x-component":"RenderTableButtonList","x-reactions":expression(`(field) => {
                              field.visible = !$form.readPretty
                            }`),properties:{delete:{type:"void",title:i18nExpression("common.delete"),"x-component-props":{type:"text",disabled:expression("$vendor() || $form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'"),"@click":expression(`
                                    () => {
                                      $table.remove($self.index)
                                    }
                                  `)}}}}})}}},paymentPlan:{type:"void","x-component":"FormCollapse.Item","x-component-props":{title:i18nExpression("contractMod.paymentPlan")},"x-visible":expression("!$isTermination"),"x-read-pretty":expression("$form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'"),properties:{toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},"x-reactions":expression(`(field) => {
                          field.visible = !$form.readPretty
                        }`),properties:{add:{type:"void",title:i18nExpression("common.add"),"x-component-props":{type:"primary",disabled:expression("$vendor() || $form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'"),"@click":expression(`() => {
                                $self.query($getFieldParentFieldFormPath($self, 2).concat('payPlanData'))
                                  .take(field => {
                                    field.componentProps.componentInstance.addRow()
                                  })
                              }`)}}}},payPlanData:{type:"array","x-component":"RenderTable","x-component-props":{editMode:!0,maxHeight:400,pagination:!1,sortable:!1,primaryKey:"payPlanId",cascadeDeletion:!0},"x-query-engine-skip":!0,"x-query-engine-relation":"payPlans:*","x-read-pretty":expression("$vendor() || $form.readPretty"),properties:generateXindexInOrder({paymentPeriod:{type:"string",title:i18nExpression("contractMod.paymentPeriod"),"x-render-table-column":{minWidth:80,align:"center"},"x-component":"RenderTableIndex","x-component-props":{"@changeIndex":expression(`(index) => {
                                $self.value = index + 1
                              }`)}},paymentStage:{type:"string",title:i18nExpression("contractMod.paymentStage"),"x-render-table-column":{minWidth:130},"x-component":"DictSelect","x-component-props":{code:"PAYMENT_STAGE"},...editTableFormItemValid},payExplain:{type:"string",title:i18nExpression("contractMod.payExplain"),"x-render-table-column":{minWidth:150},"x-component":"DictSelect","x-component-props":{code:"payExplain","custom-select-type":"payExplain"},...editTableFormItemValid},dateNum:{type:"string",title:i18nExpression("contractMod.dateNum"),"x-render-table-column":{minWidth:80},"x-component":"DictSelect","x-component-props":{code:"PAYMENT_PERIOD"},...editTableFormItemValid},paymentRatio:{type:"number",default:void 0,title:i18nExpression("contractMod.paymentRatio"),"x-render-table-column":{minWidth:100},"x-component-props":{controls:!1,"@change":expression(`(value) => {
                                const includeTaxAmount = $form.query('includeTaxAmount').get('value')

                                if (value && includeTaxAmount) {
                                  const stagePaymentAmount = (includeTaxAmount * value) / 100

                                  $self.query('.stagePaymentAmount').take(field => {
                                    field.value = stagePaymentAmount
                                  })
                                }
                              }`)},...editTableFormItemValid},stagePaymentAmount:{type:"number",default:void 0,title:i18nExpression("contractMod.stagePaymentAmount"),"x-render-table-column":{minWidth:100},"x-component-props":{controls:!1},...editTableFormItemValid},plannedPaymentDate:{title:i18nExpression("contractMod.plannedPaymentDate"),"x-render-table-column":{minWidth:100},type:"date",default:null,"x-component-props":{placeholder:i18nExpression("common.pleaseSelectDate"),format:"yyyy-MM-dd","value-format":"yyyy-MM-dd"},...editTableFormItemValid},payMethod:{type:"string",title:i18nExpression("contractMod.paymentMethod"),"x-render-table-column":{minWidth:140},"x-component":"DictSelect","x-component-props":{code:"PAYMENT_MODE"},...editTableFormItemValid},operation:{type:"void",title:i18nExpression("common.operation"),"x-render-table-column":{width:60,fixed:"right"},"x-component":"RenderTableButtonList","x-reactions":expression(`(field) => {
                              field.visible = !$form.readPretty
                            }`),properties:{delete:{type:"void",title:i18nExpression("common.delete"),"x-component-props":{type:"text",disabled:expression("$vendor() || $form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'"),"@click":expression(`
                                    ({ rowIndex }) => {
                                      $table.remove(rowIndex)
                                    }
                                  `)}}}}})}}},partner:{type:"void","x-component":"FormCollapse.Item","x-component-props":{title:i18nExpression("contractMod.partner")},"x-visible":expression("!$isTermination"),"x-read-pretty":expression("$form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'"),properties:{toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},"x-reactions":expression(`(field) => {
                          field.visible = !$form.readPretty
                        }`),properties:{add:{type:"void",title:i18nExpression("common.add"),"x-component-props":{type:"primary",disabled:expression("$vendor() || $form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'"),"@click":expression(`() => {
                                $self.query($getFieldParentFieldFormPath($self, 2).concat('partnerData'))
                                  .take(field => {
                                    field.componentProps.componentInstance.addRow()
                                  })
                              }`)}}}},partnerData:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,maxHeight:400,pagination:!1,sortable:!1,primaryKey:"partnerId",cascadeDeletion:!0},"x-query-engine-skip":!0,"x-query-engine-relation":"contractPartners:*","x-read-pretty":expression("$vendor() || $form.readPretty"),properties:generateXindexInOrder({partnerType:{type:"string",title:i18nExpression("contractMod.partnerType"),enum:[{label:i18nExpression("contractMod.owner"),value:"甲方"},{label:i18nExpression("contractMod.partyB"),value:"乙方"},{label:i18nExpression("contractMod.partyC"),value:"丙方"}],"x-component":"Select","x-component-props":{},"x-render-table-column":{minWidth:100}},partnerName:{type:"string",title:i18nExpression("contractMod.partnerName"),"x-render-table-column":{minWidth:150},"x-component":"OrganizationSelector","x-component-props":{multiple:!1,nodeType:"COMPANY","read-pretty":expression("$vendor() || $form.readPretty"),scope:expression("$table.getRowByIndex($self.index)"),"@select":expression(`(node, value, scope) => {
                                const row = $table.getRowByIndex($self.index)
                                $self.value = node ? node.organizationName : null
                                row.ouId = node ? node.organizationId : null
                                row.ouCode = node ? node.organizationCode : null
                                row.ouName = node ? node.organizationName : null
                              }`)},"x-reactions":{dependencies:[".partnerType"],fulfill:{schema:{"x-read-pretty":expression("$form.readPretty || ($deps[0] !== '乙方' && !!$self.value) || $form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'"),"x-component":expression(`
                                    $self.index === undefined
                                      ? ''
                                      : $deps[0] === '乙方' || !!$self.value
                                        ? 'Input'
                                        : $deps[0] === '丙方'
                                        ? 'QuickSearchWrapper'
                                        : 'OrganizationSelector'
                                  `),"x-component-props":expression(`
                                    $deps[0] === '丙方'
                                    ? {
                                      showKey: 'companyName',
                                      propKey: 'companyName',
                                      'name': 'scc_sup_company_info_new',
                                    } : {
                                      multiple: false,
                                      nodeType: 'COMPANY',
                                      disabled: $vendor() || $form.readPretty,
                                      scope: $table.getRowByIndex($self.index),
                                      '@select': (node, value, scope) => {
                                        const row = $table.getRowByIndex($self.index)
                                        $self.value = node ? node.organizationName : null
                                        row.ouId = node ? node.organizationId : null
                                        row.ouCode = node ? node.organizationCode : null
                                        row.ouName = node ? node.organizationName : null
                                      }
                                    }
                                  `)}}}},contactName:{type:"string",title:i18nExpression("contractMod.represent"),"x-render-table-column":{minWidth:120}},phone:{type:"string",title:i18nExpression("contractMod.mobileNumber"),"x-render-table-column":{minWidth:150}},address:{type:"string",title:i18nExpression("components.address.addressInfo"),"x-render-table-column":{minWidth:150}},fax:{type:"string",title:i18nExpression("contractMod.fax"),"x-render-table-column":{minWidth:150}},bankName:{type:"string",title:i18nExpression("contractMod.openingBank"),"x-render-table-column":{minWidth:150}},bankAccount:{type:"string",title:i18nExpression("contractMod.bankAccount"),"x-render-table-column":{minWidth:150}},postCode:{type:"string",title:i18nExpression("contractMod.postcode"),"x-render-table-column":{minWidth:100}},taxPayer:{type:"string",title:i18nExpression("dataConfMod.taxPayer"),"x-render-table-column":{minWidth:100}},operation:{type:"void",title:i18nExpression("common.operation"),"x-render-table-column":{width:60,fixed:"right"},"x-component":"RenderTableButtonList","x-reactions":expression(`(field) => {
                              field.visible = !$form.readPretty
                            }`),properties:{delete:{type:"void",title:i18nExpression("common.delete"),"x-component-props":{type:"text","x-component-props":{disabled:expression("$vendor()")},"@click":expression(`
                                    ({ rowIndex }) => {
                                      $table.remove(rowIndex)
                                    }
                                  `)},"x-reactions":expression(`(field) => {
                                  const contractType = field.query('contractType').get('value')
                                  const row = $table.getRowByIndex(field.index)

                                  const visible = () => {
                                    if (!$buyer()) {
                                      return false
                                    }

                                    // 新增
                                    if (contractType === 'MIAN_CONTRACT_ADD') {
                                      return true
                                    }

                                    // 变更
                                    if (contractType === 'MIAN_CONTRACT_ALTER') {
                                      return !row.sourceId
                                    }

                                    if (contractType === 'SUPPLEMENTAL_AGREEMENT') {
                                      return false
                                    }

                                    return true
                                  }

                                  field.visible = visible()
                                }`)},failure:{type:"void",title:i18nExpression("common.inactive"),"x-component-props":{type:"text",disabled:expression("$vendor()"),"@click":expression(`
                                    ({ rowIndex }) => {
                                      const row = $table.getRowByIndex(field.index)

                                      row.enable = 'N'
                                    }
                                  `)},"x-reactions":expression(`(field) => {
                                  const contractType = field.query('contractType').get('value')
                                  const row = $table.getRowByIndex(field.index)

                                  const visible = () => {
                                    if (!$buyer()) {
                                      return false
                                    }

                                    // 新增
                                    if (contractType === 'MIAN_CONTRACT_ADD') {
                                      return false
                                    }

                                    // 变更
                                    if (contractType === 'MIAN_CONTRACT_ALTER') {
                                      return (
                                        row.sourceId &&
                                        (row.partnerType === '丙方' ||
                                        row.partnerType === '甲方')
                                      )
                                    }

                                    if (contractType === 'SUPPLEMENTAL_AGREEMENT') {
                                      return false
                                    }

                                    return true
                                  }

                                  field.visible = visible()
                                }`)}}}})}}},fileInfo:{type:"void","x-component":"FormCollapse.Item","x-component-props":{title:i18nExpression("contractMod.fileInfo")},properties:{toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},"x-reactions":expression(`(field) => {
                          field.visible = !$form.readPretty && $buyer()
                        }`),properties:{add:{type:"void",title:i18nExpression("common.add"),"x-component-props":{type:"primary",disabled:expression("$vendor()"),"@click":expression(`() => {
                                $self.query($getFieldParentFieldFormPath($self, 2).concat('fileUploads'))
                                  .take(field => {
                                    field.componentProps.componentInstance.addRow()
                                  })
                              }`)}}}},fileUploads:{type:"array","x-component":"RenderTable","x-read-pretty":!0,"x-component-props":{preColumns:"seq",maxHeight:400,editMode:!0,pagination:!1,sortable:!1,primaryKey:"annexId",cascadeDeletion:!0},"x-query-engine-skip":!0,"x-query-engine-relation":"annexes:*",properties:generateXindexInOrder({fileType:{type:"string",title:i18nExpression("dataConfMod.attachmentType"),default:"OTHER_AGREEMENT","x-component":"DictSelect","x-component-props":{disabled:expression("$vendor() || $form.readPretty"),code:"CONTRACT_AGREEMENT_ATTACHMENT"},"x-render-table-column":{minWidth:150}},fileSourceName:{type:"string",title:i18nExpression("bidMod.fileName"),"x-component":"SrmCommonFile","x-component-props":{disabled:expression("$vendor() || $form.readPretty"),extraData:{fileModular:"sup",fileFunction:"vendorBiddingManagement",fileType:"images"},defaultFile:{fileId:expression(`
                                !$self.value
                                  ? undefined
                                  : $table.getRowByIndex($self.index).fileuploadId
                                `),fileName:expression("$self.value && String($self.value)")},"@on-change":expression(`({ file }) => {
                                const row = $table.getRowByIndex($self.index)
                                row.fileuploadId = String(file.fileId)
                                row.createdFullName = file.createdFullName
                                row.creationDate = file.creationDate

                                setTimeout(() => {
                                  $self.value = String(file.fileName)
                                })
                              }`)},"x-read-pretty":expression("$readOnly"),"x-render-table-column":{minWidth:150}},createdFullName:{type:"string",title:i18nExpression("purchaseDemand.attachmentCreatedBy"),"x-render-table-column":{minWidth:150}},creationDate:{type:"string",title:i18nExpression("purchaseDemand.attachmentCreatedDate"),"x-render-table-column":{minWidth:150}},operation:{type:"void",title:i18nExpression("common.operation"),"x-render-table-column":{width:60,fixed:"right"},"x-component":"RenderTableButtonList","x-reactions":expression(`(field) => {
                              field.visible = !$form.readPretty
                            }`),properties:{delete:{type:"void",title:i18nExpression("common.delete"),"x-component-props":{type:"text",disabled:expression("$vendor() || $form.readPretty"),"@click":expression(`
                                    ({ rowIndex }) => {
                                      $table.remove(rowIndex)
                                    }
                                  `)}}}}})}}},supplementaryAgreement:{type:"void","x-component":"FormCollapse.Item","x-component-props":{title:i18nExpression("contractMod.supplementaryAgreement")},"x-visible":expression("!$isTermination"),"x-reactions":changeFieldVisibleByDeps(["contractType"],"$deps[0] === 'SUPPLEMENTAL_AGREEMENT'"),properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:4,columnGap:32,rowGap:0},properties:{supplementAgreementExplain:{type:"string",title:i18nExpression("common.pleaseTypeContents"),"x-component":"Input.TextArea","x-component-props":{rows:4,maxlength:200,"show-word-limit":!0,placeholder:i18nExpression("common.pleaseTypeContents")}}}}}},contractContent:{type:"void","x-component":"FormCollapse.Item","x-component-props":{title:i18nExpression("contractMod.contractContent")},"x-reactions":changeFieldVisibleByDeps(["modelEnable"],"!$isTermination && $eqY($deps[0])"),properties:{printContent:{type:"void","x-component":"HTMLElement","x-component-props":{id:"printContent",style:{width:"794px",margin:"0 auto",position:"relative"}},properties:{markedContent:{type:"void","x-component":"div","x-component-props":{id:"markedContent",style:{width:"100%"},contenteditable:queryFieldValueExpression("state","data.contenteditable")}}}}}}})}}},CFillProgress:{type:"void","x-component":"CFillProgress","x-component-props":{class:"contract-progress",ref:"contractProgress",nodeName:"$t('logisticsMod.contractInfo')",data:expression("($attrs.params?.flag === 'termination' || $attrs.params?.contractType === 'TERMINATION') || $form.query('modelEnable').take().value == 'Y'?$form.query('state').get('data').progress:$form.query('state').get('data').progressNo"),percentage:"{{true}}","@index-click":`{{ (code) => {
              let anchorEle = document.querySelector('#collapse_' + code)
              if (anchorEle) {
                anchorEle.scrollIntoView(true)
              }
           } }}`}}}}});return{__sfc:!0,http,emitTabRemove,app,t,eqY,eqN,buyer,vendor,attrs,viewUpdateButtonSave,viewUpdateButtonsubmit,disabledUpdateButton,updateWorkflowconfig,handleButtonConfig,initButtonConfig,updateButtonConfig,$compileMarkedContent,initData,$cancel,$getPdfFile,$calcIncludeTaxAmount,$isTermination,$handleSubmit,$handlePreview,$saveBill,generateComponent,scope,vendorReadPrettyCollapseItemSegment,schema,components:{CFillProgress},RenderEngine}}});var _sfc_render=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{schemaKey:"contractManagerDetail",pageAttrs:_vm.$attrs,schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const contractInformation=__component__.exports;export{contractInformation as default};
