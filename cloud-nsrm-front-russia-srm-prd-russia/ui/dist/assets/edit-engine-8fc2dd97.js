import{al as defineComponent,am as usePageHelper,an as useAttrs,d4 as createForm,br as useDebounceFn,c8 as action,c6 as DictClass,ac as createDictClass,bx as FILE_UPLOAD,c9 as nextTick,ca as loadJS,ar as defineSchemas,af as i18nExpression,cb as checkboxByYOrNSegment,ae as expression,ai as generateXindexInOrder,cc as formGridSegment,aC as requiredValidatorSegment,bt as changeFieldVisibleByDeps,c5 as yearMonthDayHourMinuteSecondSelectorSegment,ag as yearMonthDaySelectorSegment,ah as radioGroupByYOrNSegment,aj as editTableFormItemValid,bX as queryFieldValueExpression,ak as feedbackLayoutIsPopover,as as RenderEngine,aa as cloneDeep,K as Secret,ce as markRaw,bs as toJS,n as normalizeComponent}from"./index-17d0ccd5.js";import{A as ApprovalProcess}from"./index-a2e65ccc.js";import{c as contractManagement}from"./index-374e2d45.js";import{P as Parser}from"./index-2fe75bb8.js";import{C as CFillProgress}from"./index-6af40985.js";import{n as numericUppercase}from"./number-f5ee71d6.js";import{b as bundle_cjs}from"./bundle.cjs-544db961.js";import"./enum-d9c76693.js";import"./validate-8a9c1e8f.js";import"./index-531039c3.js";import"./util-6482eb24.js";import"./uniqueId-bf6f89eb.js";/* empty css                                              */import"./big-e21bdbb6.js";const _sfc_main=defineComponent({__name:"edit-engine",setup(__props){const{http,emitTabRemove,app,t,eqY,eqN,buyer,vendor}=usePageHelper(),attrs=useAttrs(),form=createForm(),buttonShowFlag=window.location.href.indexOf("flowTaskViewBase"),$wrapper=(options,$root)=>Object.keys(options).reduce((acc,key)=>(acc[key]=options[key].bind($root),acc),{}),$showLockSeal=$form=>{let visible=!1;if(!$form.values.stampContractFileuploadId)return!1;const partnerData=$form.query("partnerData").take()?.value||[];if(partnerData.length){const partA=partnerData.find(parter=>parter.partnerType=="甲方");if(partA&&partA.extEmployeeNumber===app.$store.getters.userInfo.username){const extStampSignSeq=$form.values.extStampSignSeq;extStampSignSeq==="COMPANY_FIRST"?visible=partA.extStampStatus==="UNSTAMP":extStampSignSeq==="VENDOR_FIRST"&&(visible=partnerData.find(parter=>parter.partnerType=="乙方").extStampStatus==="STAMP"&&partA.extStampStatus==="UNSTAMP")}}return visible},$preOptions={nextStep:async function(){let checkResult=!0;const{data}=await http({url:"/api-cm/contractHead/ext/checkVendor",method:"GET",params:{vendorId:this.$form.values.vendorId}}),{isBlack,focusFlag}=data;return isBlack==="Y"?(app.$message.warning(t("cusEntry.tipMessage.isBackgMsg")),!1):(focusFlag==="Y"&&await app.$confirm(t("cusEntry.tipMessage.isFocusFlag"),"提示",{confirmButtonText:"确定",cancelButtonText:"取消",type:"warning"}).then(()=>{checkResult=!0}).catch(()=>{checkResult=!1}),checkResult?this.$form.values.contractStatus==="SUPPLIER_CONFIRMED"?!0:await $saveBill(this.$form,this.$queryEngine,this.$bus,this.$eqY,"submit"):!1)}},viewUpdateButtonSave=$form=>attrs.params.flag=="view"?!1:$form.values.needVendorConfirm!="Y"&&(["DRAFT","WITHDRAW","REJECTED"].includes($form.values.contractStatus)||(attrs.params?attrs.params.flag==="add"||(attrs.params.flag==="termination"||attrs.params?.contractType=="TERMINATION")&&$form.values.contractStatus==="ARCHIVED":null)),viewUpdateButtonsubmit=$form=>{if(attrs.params.flag=="view")return!1;const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;return attrs.params?.flag==="termination"||attrs.params?.contractType=="TERMINATION"?buyer()&&["SUPPLIER_CONFIRMED"].includes(attrs.params?.row?.contractStatus)?!0:$form.values.needVendorConfirm!="Y"&&buyer()&&(["DRAFT","WITHDRAW","REJECTED"].includes($form.values.contractStatus)||(attrs.params?(attrs.params.flag==="termination"||attrs.params?.contractType=="TERMINATION")&&$form.values.contractStatus==="ARCHIVED":null)):componentInstance.workflowParamsInfo.integrationMode!=="None"?buyer()&&($form.values.needVendorConfirm!="Y"&&(["DRAFT","WITHDRAW","REJECTED"].includes($form.values.contractStatus)||(attrs.params?attrs.params.flag==="add":null))||$form.values.needVendorConfirm==="Y"&&["SUPPLIER_CONFIRMED"].includes($form.values.contractStatus)):$form.values.needVendorConfirm!="Y"?!!(buyer()&&(["DRAFT","WITHDRAW","REJECTED","SUPPLIER_CONFIRMED"].includes($form.values.contractStatus)||attrs.params&&attrs.params.flag==="add")):!!(buyer()&&["SUPPLIER_CONFIRMED"].includes($form.values.contractStatus))},disabledUpdateButton=$form=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance,state=$form.values.contractStatus;return componentInstance.workflowParamsInfo.integrationMode=="None"&&["SUPPLIER_CONFIRMED"].includes(state)?!1:attrs.params.flag==="view"?!0:(attrs.params.flag==="add"||attrs.params.flag==="termination"||!buyer()&&state==="SUPPLIER_CONFIRMING"||state&&["DRAFT","REFUSED","WITHDRAW"].includes(state),!1)},updateWorkflowconfig=(componentInstance,businessId,tabDisabled,businessVariables)=>{componentInstance.setWorkflowBusinessId(businessId),componentInstance.setWorkflowTabDisabled(tabDisabled),componentInstance.setWorkflowBusinessVariables(businessVariables)},updateButtonConfig=$form=>{setTimeout(()=>{$form.query("state").get("data").showButtonConfig={saveAndNextStep:$form.values.needVendorConfirm==="N"}},50)},$compileMarkedContent=async($form,modeContent,isInit=!0)=>{const markedContentNode=document.getElementById("markedContent");if(!markedContentNode)return;markedContentNode.innerHTML="";const initialize=isInit&&attrs.params.flag==="add",breakPageMatcher=/_ueditor_page_break_tag_/g;if(modeContent=modeContent.replace(breakPageMatcher,()=>'<div class="breakPage" style="break-after: page;"></div>'),modeContent){const{vueTemplate,elementCodes}=Parser.replacer(modeContent,initialize),elemKeys=isInit?$form.values.modelLines.reduce((obj,i)=>{const{modelKey,modelValue}=i;let value=modelValue;try{isNaN(modelValue)&&(value=JSON.parse(modelValue))}catch{}return obj[modelKey]=value,obj},{}):elementCodes;let extContentFinal=generateComponent({node:markedContentNode,$form,html:vueTemplate,elemKeys,onInit:componentInstance=>{$form.query("state").take(field=>{field.setData({contractTemplateComponentInstance:markRaw(componentInstance)})})}});$form.query("state").get("data").extContentFinal=extContentFinal}},initData=$form=>{if($form.values.contractType=attrs.params.contractType,attrs.params.flag==="add"){if($form.values.sourceType="MANUALLY_CREATED",["MIAN_CONTRACT_ALTER","SUPPLEMENTAL_AGREEMENT"].includes($form.values.contractType))$form.values.contractHeadId=attrs.params.rowId;else return!1;$form.values.contractType==="SUPPLEMENTAL_AGREEMENT"&&app.$http({url:"/api-cm/contractHead/ext/generateExtContractCode",method:"GET",params:{contractHeadId:attrs.params.rowId}}).then(response=>{})}else $form.values.contractHeadId=attrs.params.row.contractHeadId;return!0},$cancel=$bus=>{emitTabRemove(attrs.params?.flag==="add"?"contractInformation":attrs.params?.flag==="termination"||attrs.params?.contractType==="TERMINATION"?"termination"+attrs.params?.row.contractName:"contractInformation"+attrs.params?.row.contractName),$bus.$emit("ContractHead")},$getPdfFile=async(flag=!1)=>{let htmlBody=(document.getElementById("printContent")?.innerHTML??"").replace('disabled="disabled"'," ");const res=await http.post("/egg/upload",{options:{format:"a4",margin:{left:"1cm",top:"1cm",right:"1cm",bottom:"1cm"}},htmlString:'<div style="page-break-inside: avoid;overflow: hidden;font-family: simsun;">'+htmlBody+"</div>"},{responseType:"arraybuffer",loading:!0,baseURL:"",returnDirectly:!0}),blob=new Blob([res.data],{type:"application/pdf"}),formData=new FormData;formData.append("file",blob,"myfile.pdf");const pdf=await http.post("/api-cm/contractHead/ext/pdfAddPage",formData,{headers:{contentType:"form-data"},responseType:"arraybuffer",loading:!0,returnDirectly:!0});let blobs=new Blob([pdf.data],{type:"application/pdf"});if(flag){const iframeNode=document.getElementById("pdfIframe");iframeNode&&(iframeNode.src=URL.createObjectURL(blobs),setTimeout(()=>{iframeNode.contentWindow.print()},1e3))}return blobs},$calcIncludeTaxAmount=useDebounceFn($form=>{if($form.values.contractStatus==="ARCHIVED")return;const materialListData=$form.query("materialListData").get("value");if(!materialListData?.length)return;const totalAmount=materialListData.reduce((sum,item)=>Number(sum)+Number(item.amount),0);Number.isNaN(totalAmount)||($form.values.totalItems=totalAmount+"元",$form.values.totalMaterialAmount=numericUppercase(totalAmount),$form.query("includeTaxAmount").take(field=>{field.value=Number(totalAmount).toFixed(2)}))},1e3),$archive=async($form,$queryEngine,$bus,$message)=>{const{stampAnnexes,contractHeadId}=$form.values;if(!stampAnnexes||!stampAnnexes.length)return $message.warning("盖章附件信息不能为空");await $queryEngine.request.save({contractHeadId,stampAnnexes}),await app.$http({url:"/api-cm/contractInterface/ext/contractFiling",method:"GET",params:{contractHeadId},loading:!0})&&($form.values.isFrameworkAgreement!=="Y"?$message({type:"success",message:"归档成功，请及时创建合同履约计划",duration:5e3}):$message.success(t("common.success")),$cancel($bus))},$isTermination=attrs.params?.flag==="termination"||attrs.params?.contractType==="TERMINATION",$handleSubmit=async($form,$queryEngine,$bus,type="submit")=>{let validResult=!0;const temporaryData=type===""||type==="savePublish",run=async values=>{$calcIncludeTaxAmount($form);const state=$form.query("state").get("data"),workFlow=["approval","publish"].includes(type);if(eqY(values.ceeaIsPortableContract)&&workFlow&&!temporaryData&&values.includeTaxAmount>2e4){app.$message.warning(t("contractMod.msgContractManage[10]")),values.ceeaIsPortableContract="N",validResult=!1;return}if(!temporaryData&&values.effectiveDateFrom&&new Date(values.effectiveDateFrom.replace(/-/g,"/"))>new Date(values.effectiveDateTo.replace(/-/g,"/")))return validResult=!1,app.$message.error(t("合同有效期有误"));let bolpartnerType=0,bolpartnerType2=0;if(values.partnerData.forEach(item=>{item.partnerType=="甲方"&&bolpartnerType++,item.partnerType=="乙方"&&bolpartnerType2++}),!temporaryData&&bolpartnerType>1)return validResult=!1,app.$message.error(t("只能有一个甲方"));if(!temporaryData&&bolpartnerType2>1)return validResult=!1,app.$message.error(t("只能有一个乙方"));if(!temporaryData){for(let item of values.partnerData)if(item.phone&&!bundle_cjs.RuleRegExp.Mobile.test(item.phone))return validResult=!1,app.$message.error(`合作伙伴-${item.partnerType}联系电话格式不正确`)}let isNull=!values.fileUploads.length||values.fileUploads.some(i=>!i.fileuploadId);if(!temporaryData&&isNull&&($isTermination||workFlow&&values.modelEnable=="N"))return validResult=!1,app.$message.error(t("contractMod.msgContractManage[11]"));if(!temporaryData&&!$isTermination&&values.contractType!=="SUPPLEMENTAL_AGREEMENT"){let fileMessage2="合同附件信息-请上传阳光协议附件";if(values.fileUploads.every(item=>item.fileType!=="SUNSHINE_PROTOCOL"))return validResult=!1,app.$message.error(fileMessage2);for(let item of values.fileUploads)if(item.fileType==="SUNSHINE_PROTOCOL"&&!item.fileuploadId)return validResult=!1,app.$message.error(fileMessage2)}const modelLines=[],elemKeys=state.contractTemplateComponentInstance?.elemKeys;if(elemKeys)for(const[key,value]of Object.entries(elemKeys))try{const modelLineId=(values.modelLines.find(i=>key===i.modelKey)||{}).modelLineId;value&&(Array.isArray(value)?modelLines.push({modelLineId:modelLineId||null,modelKey:key,modelValue:JSON.stringify(value)}):modelLines.push({modelLineId:modelLineId||null,modelKey:key,modelValue:value}))}catch{}let finalHTML=null;try{finalHTML=Parser.unReplacer(document.getElementById("markedContent")?.innerHTML)}catch{}let extContentFinal=$form.query("state").get("data").extContentFinal;if(workFlow&&eqY(state.ceeaIfVirtual)&&!temporaryData&&!values.frameworkAgreementCode)return validResult=!1,app.$message.error(t("contractMod.msgContractManage[12]"));if(eqN(values.isFrameworkAgreement)&&workFlow){values.payPlanData.reduce((sum,item)=>sum+Number(item.paymentRatio),0);const totalMoney=values.payPlanData.reduce((sum,item)=>sum+Number(item.stagePaymentAmount)*1e3,0);if(!$isTermination){const includeTaxAmount=Number($form.query(".includeTaxAmount").take().value)*1e3;if(!temporaryData&&includeTaxAmount!=totalMoney&&eqN(values.isFrameworkAgreement))return app.$message.error("阶段付款金额总和应与合同总金额相等"),validResult=!1,!1}if(!temporaryData&&!values.materialListData.length&&!$isTermination)return validResult=!1,app.$message.error(t("contractMod.msgContractManage[27]"));if(!temporaryData&&!values.payPlanData.length&&workFlow&&!$isTermination)return validResult=!1,app.$message.error(t("contractMod.msgContractManage[15]"));const payPlanDataBol=values.payPlanData.some(e=>!(e.paymentPeriod&&e.paymentStage&&e.payExplain&&e.dateNum&&e.plannedPaymentDate&&e.payMethod));if(!temporaryData&&payPlanDataBol&&workFlow&&!$isTermination){app.$message.error(t("contractMod.payPlanDataBol")),validResult=!1;return}for(let item of values.payPlanData){if(["HONOUR","WIRE_AND_HONOUR"].includes(item.payMethod)&&!item.extAcceptanceDate){app.$message.error("付款计划-付款方式为承兑、电汇+承兑时，承兑期限必填"),validResult=!1;return}if(item.payMethod==="WIRE_AND_HONOUR"&&Number(item.extAcceptanceRatio)+Number(item.extWireTransferRatio)!=100){app.$message.error("付款计划-付款方式为电汇+承兑时，承兑比例和电汇比例之和必须等于100"),validResult=!1;return}}let materialListDataBol=values.materialListData.some(e=>!(e.untaxedPrice&&e.contractQuantity&&e.taxRate!=null&&e.taxRate!==""));if(!temporaryData&&materialListDataBol&&workFlow&&!$isTermination){app.$message.error(t("contractMod.materialListDataBol")),validResult=!1;return}}const{fileUploads,payPlanData,partnerData,materialListData,...rest}=toJS(values);if(["MIAN_CONTRACT_ALTER","SUPPLEMENTAL_AGREEMENT","TERMINATION"].includes($form.values.contractType)&&(attrs.params?.flag=="add"||attrs.params?.flag=="termination")&&(values.fileUploads.forEach(e=>{delete e.annexId,delete e.contractHeadId}),values.payPlanData.forEach(e=>{delete e.payPlanId,delete e.contractHeadId}),materialListData.forEach(e=>{delete e.contractMaterialId,delete e.contractHeadId}),values.partnerData.forEach(e=>{delete e.partnerId,delete e.contractHeadId}),modelLines.forEach(e=>{delete e.modelLineId})),workFlow){let checkResult=!0;const{data:data2}=await http({url:"/api-cm/contractHead/ext/checkVendor",method:"GET",params:{vendorId:$form.values.vendorId}}),{isBlack,focusFlag}=data2;if(isBlack==="Y")return app.$message.warning(t("cusEntry.tipMessage.isBackgMsg")),!1;if(focusFlag==="Y"&&await app.$confirm(t("cusEntry.tipMessage.isFocusFlag"),"提示",{confirmButtonText:"确定",cancelButtonText:"取消",type:"warning"}).then(()=>{checkResult=!0}).catch(()=>{checkResult=!1}),!checkResult)return!1}const data={...rest,modelLines,annexes:values.fileUploads,payPlans:values.payPlanData,contractMaterials:materialListData,contractPartners:values.partnerData};finalHTML&&(data.content=finalHTML),extContentFinal&&(data.extContentFinal=extContentFinal.innerHTML),data.isDeleteLine="Y",data.isSavePerCheck="Y",buyer()?type==="approval"?data.contractStatus=data.contractStatus??"DRAFT":type==="publish"?data.contractStatus="SUPPLIER_CONFIRMING":data.contractStatus="DRAFT":type==="SUPPLIER_CONFIRMING"?data.contractStatus="SUPPLIER_CONFIRMED":type==="SUPPLIER_REFUSE"?data.contractStatus="SUPPLIER_REJECTED":data.contractStatus="SUPPLIER_CONFIRMING";const resetContractHeadId=obj=>{obj?.contractHeadId&&(obj.contractHeadId=null)};["MIAN_CONTRACT_ALTER","SUPPLEMENTAL_AGREEMENT","TERMINATION"].includes(attrs.params.contractType)&&(data.mainContractNo=attrs.params.mainContractNo,attrs.params.flag!=="edit"&&(data.contractType=attrs.params.contractType,data.contractOldCode=values.contractNo,data.ceeaContractOldId=values.contractHeadId,resetContractHeadId(data),["modelLines","annexes","payPlans","contractMaterials","contractPartners","basisAnnexes","stampAnnexes","operationLogs"].forEach(relationTable=>{const relationTableData=data[relationTable];if(Array.isArray(relationTableData)&&relationTableData.length){data[relationTable].forEach(item=>{resetContractHeadId(item)});return}resetContractHeadId(relationTableData)}))),attrs.params.termination&&(data.contractType="TERMINATION",attrs.params?.flag!=="edit"&&type==="SUPPLIER_CONFIRMING"&&(data.contractStatus="TERMINATED"));const res=await $queryEngine.request.save(data,{customizeAction:vendor()&&type==="publish"?"publish":void 0}),contractHeadId=res.originalData.records[0]||values.contractHeadId;if($form.values.contractHeadId=contractHeadId,$form.setValues({contractHeadId,...res.data[0],contractType:attrs.params.contractType}),type==="approval"){const tabDisabled=!["SUPPLIER_CONFIRMED","REJECTED","WITHDRAW","APPROVAL","UNDER_REVIEW","UN_ARCHIVED","SIGNATUREING","ARCHIVED","TERMINATED","ABANDONED"].includes(values.contractStatus)||values.contractStatus==="DRAFT",componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.setWorkflowBusinessId(contractHeadId),componentInstance.setWorkflowTabDisabled(tabDisabled),componentInstance.setWorkflowBusinessVariables({}),componentInstance.handlerAfter("SUBMIT");return}if(["publish","SUPPLIER_CONFIRMING","SUPPLIER_REFUSE"].includes(type)){$cancel($bus);return}};return type&&type!=="savePublish"?(await $form.validate(),await run($form.values)):await run($form.values),validResult},$handlePreview=$form=>{const state=$form.query("state").get("data");state.contractTemplateComponentInstance&&(state.contractTemplateComponentInstance.editable=!1),state.contenteditable=!1},$saveBill=async($form,$queryEngine,$bus,$eqY,type="")=>{const modelHeadId=$form.query("modelHeadId").get("value"),contractType=$form.query("contractType").get("value"),modelEnable=$form.query("modelEnable").get("value"),state=$form.query("state").get("data"),contractStatus=$form.query("contractStatus").get("value");return modelHeadId&&state.contractTemplateComponentInstance&&state.contractTemplateComponentInstance.editable&&(contractType==="MIAN_CONTRACT_ADD"||contractType==="MIAN_CONTRACT_ALTER"||contractType==="SUPPLEMENTAL_AGREEMENT")&&(contractStatus==="DRAFT"||contractStatus===""||contractStatus===null)&&$eqY(modelEnable)&&attrs.params.flag!=="view"?(app.$message.warning(t("现在合同为编辑模式,请切换为浏览模式")),!1):($handlePreview($form),await $handleSubmit($form,$queryEngine,$bus,type))},generateComponent=({node,$form,html,elemKeys,onInit})=>{const mergeForm=cloneDeep($form.values),$el=Parser.generateComponent({html,elemKeys,onInit,context:{mergeForm,partnerData:mergeForm.partnerData??[],materialEditableRows:mergeForm.materialListData??[]},wrapper:node});return node.appendChild($el),node},$refuseDialogConfirm=async(values,$form,$queryEngine,$message,$bus,closeLoading,done)=>{await $queryEngine.request.save({contractStatus:"SUPPLIER_REJECTED",contractHeadId:$form.values.contractHeadId,...values}).finally(()=>{done()})&&($message.success(t("common.success")),$cancel($bus))},$approvalHanlder=(type,$form,$queryEngine,$bus,$eqY)=>{switch(type){case"save":if($form.values.contractStatus==="SUPPLIER_CONFIRMED")return app.$message.warning(t("cusEntry.approval.supplierConfirmed")),!1;$saveBill($form,$queryEngine,$bus,$eqY,"");break;case"submit":$cancel($bus);break;case"abandon":$cancel($bus);break;case"recall":$cancel($bus);break;case"pass":$cancel($bus);break}},$lockSealHandler=$form=>{const{stampContractFileuploadId}=$form.values,{contactName,partnerName,extEmployeeNumber}=$form.query("partnerData").take().value.find(item=>item.partnerType=="甲方");app.$http({url:`/api-pj/external/ContractLock/signUrl2?contractId=${stampContractFileuploadId}&tenantName=${partnerName}&tenantType=COMPANY&receiverNumber=${extEmployeeNumber}`,method:"POST",loading:!0}).then(res=>{res.data&&window.open(res.data,"_blank")})},$onlyOfficeView=async(row,$form,isApproval)=>{if(!row.fileuploadId)return;let list=[];isApproval&&(list=(await app.$http({url:`/api-pj/bpmFlow/findTaskListNew?businessType=CONTRACT&businessId=${$form.values.contractHeadId}`,method:"GET",loading:!0})).data||[]);const flag=list.find(item=>item.actionName==="正在办理"&&item.activityName==="经办人清稿"&&item.createUserName===app.$store.getters.userInfo.nickname);app.$http({url:`/api-file/edit/onlyoffice/api/edit?fileuploadId=${Secret.getValue(row.fileuploadId)}`,method:"GET",loading:!0}).then(res=>{if(res.data){const pathname=window.location.pathname,systemUrl=window.location.origin+pathname.substring(0,pathname.length-1),{storage,key,title,fileType}=res.data.document,{callbackUrl}=res.data.documentEditParam,callbackUrlParams=callbackUrl+`&fileuploadId=${Secret.getValue(row.fileuploadId)}`;window.open(`${systemUrl}/#/onlyoffice?${flag?"comment=clear&revision=clear&":""}url=${btoa(encodeURIComponent(storage))}&key=${key}&title=${title}&fileType=${fileType}&callbackUrl=${btoa(encodeURIComponent(callbackUrlParams))}`,"_blank")}})},scope={$onlyOfficeView,$lockSealHandler,$wrapper,$showLockSeal,$preOptions,$attrs:attrs,$eqY:eqY,$eqN:eqN,app,$reactiveAction:action,$dictClass:DictClass,$taxDictClass:createDictClass({tax:[]}),$isAdd:attrs.params?.flag==="add",$isTermination,$illegal:attrs.params?.flag,$illegalNotView:attrs.params?.illegal!=="view",$readOnly:!!attrs.params?.isReadOnly,$jumpLogin:!attrs.params?.jumpLogin,$buttonType:attrs.params?.buttonType?attrs.params?.buttonType:buttonShowFlag>-1?"approve":"",$contractManagement:contractManagement,numericUppercase,$calcIncludeTaxAmount,$handleSubmit,$cancel,$compileMarkedContent,$refuseDialogConfirm,$uploadPDF:async()=>{const blob=await $getPdfFile(),file=new window.File([blob],"myfile.pdf",{type:"application/pdf"}),data={file,uploadType:"DEF",sourceType:"WEB_APP",fileModular:"api-cm",fileFunction:"contractInformation",fileType:"pdf"},formData=new FormData;formData.append("file",file);for(const[key,value]of Object.entries(data))formData.append(key,value);const{data:file_data}=await http.post(FILE_UPLOAD,formData,{headers:{contentType:"form-data"},loading:!0});return file_data},$handlePreview,$html2diff:$form=>{nextTick(async()=>{const modelHeadId=$form.query("modelHeadId").get("value");$form.query("state").get("data");const res1=await contractManagement.getById(modelHeadId),res2=await contractManagement.modelLine.getModelLine(modelHeadId),initialModelValue=(attrs.params?.flag==="add"?res2.data:$form.values.modelLines).reduce((obj,i)=>{const{modelKey,modelValue}=i;let value=modelValue;try{value=JSON.parse(modelValue)}catch{}return obj[modelKey]=value,obj},{});let content=res1.data.content;const breakPageMatcher=/_ueditor_page_break_tag_/g;content=content?.replace(breakPageMatcher,()=>'<div class="breakPage" style="break-after: page;"></div>')??"";const templateNode=document.getElementById("templateNode");if(templateNode){templateNode.innerHTML="";const{vueTemplate}=Parser.replacer(content,!1);generateComponent({node:templateNode,$form,html:vueTemplate,elemKeys:initialModelValue})}const oldContent=templateNode?.innerHTML??"",newContent=document.getElementById("markedContent")?.innerHTML||"",open=textHtml=>{textHtml&&($form.query("diffChangeDialog").take()?.setComponentProps({visible:!0}),setTimeout(()=>{const diffChangeContentNode=document.getElementById("diffChangeContent");diffChangeContentNode&&(diffChangeContentNode.innerHTML=textHtml)}))};if(typeof Worker>"u")loadJS("./htmldiff.js",()=>{open(getHTMLDiff(oldContent,newContent))});else{const worker=new Worker("./htmldiff.js");worker.postMessage({newVersion:newContent,oldVersion:oldContent}),worker.onmessage=evt=>{open(evt.data)}}})},$getPdfFile,$calcMaterialTaxedPrice:($form,row)=>{if($calcIncludeTaxAmount($form),row.untaxedPrice&&row.contractQuantity){const unAmount=parseFloat(row.untaxedPrice)*parseFloat(row.contractQuantity);if(row.unAmount=unAmount.toFixed(2),row.taxRate){const amount=Number((unAmount*(1+row.taxRate/100)).toFixed(2)),num=Number(row.contractQuantity);row.amount=amount,row.taxQuota=Number(amount-unAmount).toFixed(2),row.taxedPrice=amount/num}return}if(row.taxedPrice&&row.contractQuantity){const amount=parseFloat(row.taxedPrice)*parseFloat(row.contractQuantity);if(row.amount=amount.toFixed(2),row.taxRate){const unAmount=Number((amount/(1+row.taxRate/100)).toFixed(2)),num=Number(row.contractQuantity);row.unAmount=unAmount,row.taxQuota=Number(amount-unAmount).toFixed(2),row.untaxedPrice=unAmount/num}}},updateButtonConfig,viewUpdateButtonSave,$saveBill,initData,$archive,$approvalHanlder},vendorReadPrettyCollapseItemSegment={"x-read-pretty":expression("$vendor() || $form.readPretty")},schema=defineSchemas({pdfIframe:{type:"void","x-component":"iframe","x-component-props":{id:"pdfIframe",style:{display:"none"}}},templateNode:{type:"void","x-component":"div","x-component-props":{id:"templateNode",style:{display:"none"}}},diffChangeDialog:{type:"void",title:i18nExpression("contractMod.compareChange"),"x-component":"RDialog",properties:{diffChangeContainer:{type:"void","x-component":"HTMLElement","x-component-props":{style:{overflow:"hidden"}},properties:{diffChangeContent:{type:"void","x-component":"div","x-component-props":{id:"diffChangeContent",class:"conetnt paper",style:{width:"98%"}}}}}}},frameworkAgreementDialog:{type:"void",title:i18nExpression("contractMod.maintainFrameworkAgreement"),"x-component":"RDialog","x-component-props":{footer:!1},properties:{queryData:{type:"object",default:{},"x-data":{pageSize:9999,pageNum:1,vendorId:void 0,globalcontractIds:[]},"x-decorator":"FormLayout","x-decorator-props":{layout:"horizontal"},"x-component":"FormGrid","x-component-props":{maxColumns:4,columnGap:32,rowGap:0},properties:{vendorName:{type:"string",title:i18nExpression("common.vendor"),"x-decorator":"FormItem","x-disabled":!0},isFrameworkAgreement:{title:i18nExpression("bidMod.isFrameworkAgreement"),"x-disabled":!0,...checkboxByYOrNSegment,default:"Y"}}},queryEngine:{type:"void","x-decorator":"QueryEngine","x-query-engine":{service:"cm",type:"ContractHead",transformRequest:expression(`(data, headers) => {
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
      }`)},properties:{releaseParams:{type:"object",...formGridSegment,"x-read-pretty":!1,properties:{name:{type:"string",title:i18nExpression("dataConfMod.userName"),"x-decorator":"FormItem",...requiredValidatorSegment},phone:{type:"string",title:i18nExpression("contractMod.phone"),"x-decorator":"FormItem",...requiredValidatorSegment},email:{type:"string",title:i18nExpression("dataConfMod.email"),"x-decorator":"FormItem",...requiredValidatorSegment}}}}},refuseQuery:{type:"void","x-component":"QueryEngine","x-query-engine":{service:"cm",type:"ContractHead"},properties:{refuseDialog:{type:"void",title:"驳回","x-component":"RDialog","x-component-props":{size:"small","close-on-click-modal":!1,beforeClose:expression(`(done,type,closeLoading) => {
            if(type === 'ok'){
              $self.query('refuseQuery.refuseDialog.form').take().submit(values => {
                console.log('values',values)
                $refuseDialogConfirm(values,$form,$queryEngine,$message,$bus,closeLoading,done)
              }).catch(() => { closeLoading() })
            }else{
              done()
            }
          }`)},properties:{form:{type:"object","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:2,columnGap:32,rowGap:0},properties:generateXindexInOrder({vendorRejectReason:{type:"string",title:"驳回原因说明","x-decorator":"FormItem",required:!0,"x-component-props":{type:"textarea",autosize:{minRows:4,maxRows:6}}}})}}}}},state:{type:"void","x-component":"Fragment","x-hidden":!0,"x-data":{showButtonConfig:{saveAndNextStep:!1},showTabConfig:{},extContentFinal:null,locateList:[],contractTemplateComponentInstance:null,contenteditable:!1,ceeaIfVirtual:"N",progress:[{code:"contractInfo",name:t("logisticsMod.contractInfo"),percentage:0},{code:"otherInfo",name:t($isTermination?"contractMod.terminationInformation":"vendorMod.otherInfo"),percentage:0},{code:"basisFileInfo",name:"合同依据",percentage:0},{code:"contractFinancialInformation",name:t("contractMod.contractFinancialInformation"),percentage:0},{code:"itemInfo",name:"合同签约明细",percentage:0},{code:"paymentPlan",name:t("cusEntry.contractMod.paymentPlan"),percentage:0},{code:"partner",name:t("contractMod.partner"),percentage:0},{code:"fileInfo",name:t("contractMod.fileInfo"),percentage:0},{code:"sealFileInfo",name:"盖章附件信息",percentage:0},{code:"operateRecord",name:"操作记录",percentage:0},{code:"contractContent",name:t("contractMod.contractContent"),percentage:0}],progressNo:[{code:"contractInfo",name:t("logisticsMod.contractInfo"),percentage:0},{code:"otherInfo",name:t($isTermination?"contractMod.terminationInformation":"vendorMod.otherInfo"),percentage:0},{code:"basisFileInfo",name:"合同依据",percentage:0},{code:"contractFinancialInformation",name:t("contractMod.contractFinancialInformation"),percentage:0},{code:"itemInfo",name:"合同签约明细",percentage:0},{code:"paymentPlan",name:t("contractMod.paymentPlan"),percentage:0},{code:"partner",name:t("contractMod.partner"),percentage:0},{code:"fileInfo",name:t("contractMod.fileInfo"),percentage:0},{code:"sealFileInfo",name:"盖章附件信息",percentage:0},{code:"operateRecord",name:"操作记录",percentage:0}]}},ContractHead:{type:"void","x-component":"el-container","x-component-props":{class:"flex-container contractMg",direction:"vertical"},"x-decorator":"QueryEngine","x-query-engine":{service:"cm",actions:{read:{immediate:!0,ready:expression(`() => {
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

            console.log('value:::',value)

            // const {buId} = value || {}

            // // 合同签约明细 - 收货地点取值
            // // if(buId){
            // //   $http({
            // //     url: '/api-pj/organization/organization/getOrganization',
            // //     method: 'GET',
            // //     loading: true,
            // //     params: {
            // //       organizationId: buId,
            // //     }
            // //   }).then(result => {
            // //     if(result.data.siteList.length > 0){
            // //       let siteList = result.data.siteList.filter(item => item.siteName).map(item => item.siteName)
            // //       if(siteList.length){
            // //         siteList = Array.from(new Set(siteList))
            // //         $form.query('state').get('data').locateList = []
            // //         for(let item of siteList){
            // //           $form.query('state').get('data').locateList.push({
            // //             value:item,
            // //             label:item
            // //           })
            // //         }
            // //       }
            // //     }
            // //   })
            // // }


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
            // 补充协议新增-清掉操作记录、合同附件信息、合同依据、契约锁id
            if($attrs.params?.flag === 'add' && $attrs.params?.contractType === 'SUPPLEMENTAL_AGREEMENT'){
              value.operationLogs = []
              value.stampContractFileuploadId = null
              value.fileUploads = []
              value.basisAnnexes = []
              value.stampAnnexes = []
              value.contractStatus = 'DRAFT'
            }
            value.totalItems = value?.includeTaxAmount
            value.totalMaterialAmount = numericUppercase(value?.includeTaxAmount)

            $form.setValues({
              ...value,
              contractType: $attrs.params.contractType || value.contractType
            })

            // if($attrs.params.flag === 'add' && $attrs.params?.contractType === 'SUPPLEMENTAL_AGREEMENT'){
            //   app.$http({
            //     url:'/api-cm/contractHead/ext/generateExtContractCode',
            //     method:'GET',
            //     params:{
            //       contractHeadId:$attrs.params.rowId
            //     }
            //   }).then(response => {
            //     if(response.data){
            //       $form.values.contractNo = response.data
            //     }
            //   })
            // }

            // updateButtonConfig($form)

            // 单纯文本只读状态
            $form.readPretty = $readOnly || $attrs.params.flag === 'view'
            if (['SUPPLIER_CONFIRMED'].includes($attrs.params.row.contractStatus)) {
              $form.readPretty = true
              // 控制审批流按钮权限
              $buyer() && setTimeout(() => {
                let stateData = $form.query('state').get('data')
                stateData.showButtonConfig = {
                  saveAndNextStep: true
                }
                stateData.showTabConfig = {
                  approval: true
                }
              }, 50)
            } else if (['ARCHIVED'].includes($attrs.params.row.contractStatus)) {
              // 控制审批流按钮权限
              $buyer() && setTimeout(() => {
                let stateData = $form.query('state').get('data')
                stateData.showButtonConfig = {
                  saveAndNextStep: true
                }
                stateData.showTabConfig = {
                  approval: true
                }
              }, 50)
            } else {
              $buyer() && setTimeout(() => {
                let stateData = $form.query('state').get('data')
                stateData.showButtonConfig = {
                  saveAndNextStep : $form.values.needVendorConfirm === 'N' && ['DRAFT', 'WITHDRAW', 'REJECTED'].includes($form.values.contractStatus)
                }
              }, 50)
            }
            if ($attrs.params.flag === 'add') {
              $form.query('.contractStatus').take(filed => {
                filed.value = 'DRAFT'
              })
            } else {
              if ($form.values.extContractHandlerId && !$form.values.extHandlerBuId) {
                $contractManagement.contract.getDepartment($form.values.extContractHandlerAccount).then(res => {
                  if (res.data) {
                   $form.values.extHandlerBuName = res.data.ouOrganization?.organizationName
                   $form.values.extHandlerBuId = res.data.ouOrganization?.organizationId
                   $form.values.extHandlerBuCode = res.data.ouOrganization?.organizationCode
                  }
                })
              }
            }

            setTimeout(() => {
              $compileMarkedContent($form, value.content || '', true)
            })
          }`)},save:{cascadeDeletion:!0,loading:!0}}},properties:{SchemaWorkflow:{type:"void","x-component":"ApprovalProcess","x-component-props":{"business-id":expression("$form.values.contractHeadId || null"),"business-type":"CONTRACT","show-approval-tab-record":expression("!$vendor()"),"approval-status":expression("$form.values.contractStatus || 'DRAFT'"),"operation-pre-options":expression("$wrapper($preOptions, $root)"),"show-button-config":expression("$form.query('state').get('data').showButtonConfig"),"show-tab-config":expression("$form.query('state').get('data').showTabConfig"),"status-map":expression(`{
            DRAFT: 'DRAFT', // 拟定
            SUBMITTED: 'UNDER_REVIEW', // 已提交
            APPROVED: 'APPROVED', // 审批通过
            REJECTED: 'REJECTED', // 已驳回
            WITHDRAW: 'WITHDRAW', // 已撤回
            ABANDONED: 'ABANDONED' // 已废弃
          }`),readonly:expression("$attrs.params.flag === 'view'"),"@approval-handler-callback":expression(`(type) => {
            $approvalHanlder(type, $form, $queryEngine, $bus, $eqY)
          }`)},properties:{customButtonList:{type:"void","x-component":"ButtonList","x-component-props":{style:{"margin-right":"8px"}},"x-slot":"custom",properties:{cancel:{type:"void","x-content":i18nExpression("common.cancel"),"x-component":"Button","x-component-props":{type:"default","@click":expression(`() => {
                    $cancel($bus)
                  }`)}},pdfPrint:{type:"void","x-content":i18nExpression("route.pdfPrint"),"x-component":"Button","x-component-props":{type:"default",style:{"margin-left":"8px"},"@click":expression(`() => {
                        $handlePreview($form)
                        $getPdfFile(true)
                      }`)},"x-reactions":changeFieldVisibleByDeps(["modelHeadId"],`
                    !!$deps[0] && $illegalNotView && $attrs.params.flag !== 'view' && $attrs.params.flag !== 'archive'
                    `)},editContractDetail:{type:"void","x-content":i18nExpression("contractMod.editContractDetail"),"x-component":"Button","x-component-props":{style:{"margin-left":"8px"},"@click":expression(`() => {
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
                            `)}}}]},previewContractDetail:{type:"void","x-content":i18nExpression("contractMod.previewContractDetail"),"x-component":"Button","x-component-props":{style:{"margin-left":"8px"},type:"default","@click":expression(`() => {
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
                      `)}}}},compareChange:{type:"void","x-content":i18nExpression("contractMod.compareChange"),"x-component":"Button","x-component-props":{style:{margin:"8px"},"@click":expression(`() => {
                    $handlePreview($form)
                    $html2diff($form)
                  }`)},"x-reactions":changeFieldVisibleByDeps(["modelHeadId","enable","modelEnable"],`
                    (
                      (!!$deps[0] && $eqN($deps[1])) || $illegal === 'view'
                    ) && $eqY($deps[2]) && !$isTermination && $attrs.params.flag !== 'view'
                  `)},releaseSignPlatform:{type:"void","x-content":i18nExpression("contractMod.releaseSignPlatform"),"x-component":"Button","x-hidden":!0,"x-component-props":{"@click":expression(`() => {
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
                `)},archive:{type:"void","x-content":"归档","x-component":"Button","x-component-props":{style:{"margin-left":"8px"},"@click":expression(`() => {
                    $archive($form, $queryEngine, $bus, $message)
                  }`)},"x-reactions":changeFieldVisibleByDeps(["contractStatus"],`
                  $buyer() && $deps[0] === 'UN_ARCHIVED' && $attrs.params.flag === 'archive'
                `)},confirm:{type:"void","x-content":i18nExpression("orderMod.buyerOrderSynergy.confirm"),"x-component":"Button","x-component-props":{style:{"margin-left":"8px"},"@click":expression('() => $handleSubmit($form, $queryEngine, $bus, "SUPPLIER_CONFIRMING")')},"x-reactions":changeFieldVisibleByDeps(["contractStatus"],`
                $deps[0] === 'SUPPLIER_CONFIRMING' && !$buyer() && $attrs.params.flag !== 'view'
                `)},refuse:{type:"void","x-content":i18nExpression("components.approvalHead.headers.refuse"),"x-component":"Button","x-component-props":{style:{"margin-left":"8px"},"@click":expression(`() => {
                    $form.query('refuseQuery.refuseDialog').take().setComponentProps({visible:true})
                    // $handleSubmit($form, $queryEngine, $bus, "SUPPLIER_REFUSE")
                  }`)},"x-reactions":changeFieldVisibleByDeps(["contractStatus"],`
                $deps[0] === 'SUPPLIER_CONFIRMING' && !$buyer() && $attrs.params.flag !== 'view'
                `)}}},lockSeal:{type:"void","x-component":"ElButton","x-component-props":{type:"primary",style:{position:"fixed",top:"150px",right:"222px","z-index":999},"@click":expression(`() => {
                $lockSealHandler($form)
              }`)},"x-visible":expression("$showLockSeal($form)"),"x-content":i18nExpression("cusEntry.common.lockSeal")},steps:{type:"void","x-decorator":"div","x-decorator-props":{class:"stepDiv"},"x-component":"Steps","x-component-props":{alignCenter:!0,finishStatus:"success"},"x-reactions":{dependencies:["contractStatus"],fulfill:{state:{"component[1].active":expression(`
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
                              ? 5
                              : $deps[0] === 'APPROVAL'
                                ? 3
                                :$deps[0] === 'UN_ARCHIVED'
                                ? 4
                                : ['UNDER_REVIEW', 'REFUSED', 'SIGNATUREING', 'SUPPLIER_CONFIRMED'].includes($deps[0])
                                  ? 2
                                  : ['SUPPLIER_CONFIRMED', 'SUPPLIER_REJECTED'].includes($deps[0])
                                    ? 1
                                    : 0
                          )
                      `)}}},properties:{step1:{type:"void","x-component":"el-step","x-component-props":{title:expression("$t($attrs.params.termination ? 'contractMod.terminationRelease' : 'contractMod.contractRelease')")}},step2:{type:"void","x-component":"el-step","x-component-props":{title:expression("$t($attrs.params.termination ? 'contractMod.terminationDetermine' : 'contractMod.contractConfirmation')")},"x-reactions":{dependencies:["needVendorConfirm"],fulfill:{state:{visible:expression("$eqY($deps[0])")}}}},step3:{type:"void","x-component":"el-step","x-component-props":{title:expression("$t($attrs.params.termination ? 'contractMod.terminationApproval' : 'contractMod.contractApproval')")}},step4:{type:"void","x-component":"el-step","x-component-props":{title:expression("$t($attrs.params.termination ? 'contractMod.termination' : 'contractMod.contractSigning')")}},step5:{type:"void","x-component":"el-step","x-component-props":{title:i18nExpression("contractMod.contractFiling")},"x-visible":expression("!$attrs.params.termination")}}},collapse:{type:"void","x-component":"FormCollapse",properties:generateXindexInOrder({contractInfo:{type:"void","x-component":"FormCollapse.Item","x-component-props":{title:i18nExpression("logisticsMod.contractInfo"),id:"contractInfo"},"x-query-engine-skip":!0,"x-read-pretty":expression("$vendor() || $isTermination || $form.readPretty"),properties:{contractType:{type:"string",default:"MIAN_CONTRACT_ADD","x-hidden":!0},layout:{type:"void",...formGridSegment,properties:{contractNo:{type:"string",title:i18nExpression("contractMod.contractNo"),"x-decorator":"FormItem","x-disabled":expression("$form.readPretty ? undefined : true")},contractStatus:{type:"string",title:i18nExpression("contractMod.status"),"x-hidden":expression("$isTermination"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"CONTRACT_STATUS",disabled:expression("$form.readPretty ? undefined : true"),"@change":expression("() => updateButtonConfig($form)")}},createdFullName:{type:"string",title:i18nExpression("contractMod.createdBy"),"x-visible":expression("!$isTermination"),"x-decorator":"FormItem","x-disabled":expression("$form.readPretty ? undefined : true")},creationDate:{title:i18nExpression("contractMod.creationDate"),"x-visible":expression("!$isTermination"),"x-decorator":"FormItem","x-disabled":expression("$form.readPretty ? undefined : true"),...yearMonthDayHourMinuteSecondSelectorSegment},contractName:{type:"string",title:i18nExpression("contractMod.contractName"),"x-decorator":"FormItem","x-component-props":{maxLength:100,showWordLimit:!0,disabled:expression("$form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'")},...requiredValidatorSegment},buName:{type:"string",title:"我方签约主体","x-decorator":"FormItem","x-component":"QuickSearchWrapper","x-component-props":{showKey:"companyName",propKey:"companyName",name:"scc_pj_bpm_incorporated_company","read-pretty":"{{$form.readPretty || $vendor()}}","@close-quicksearch":expression(`(val, scope) => {
                                $values.buId = val ? val.bpmIncorporatedCompanyId : null
                                $values.buCode = val ? val.companyNo : null
                                $values.buName = val ? val.companyName : null
                                if(val.companyName){
                                  const partnerData = $form.query('partnerData').take()
                                  partnerData.value.forEach((e, index) => {
                                    if (e.partnerType == '甲方') {
                                      partnerData.remove(index)
                                    }
                                  })
                                  const partnerDataItem = {
                                    partnerType: '甲方',
                                    ouId: val.bpmIncorporatedCompanyId,
                                    partnerName: val.companyName,
                                    taxPayer: val.creditCode
                                  }
                                  partnerData.push(partnerDataItem)
                                }
                                // 清空印章
                                 $values.sealName = ''
                                 $values.sealId = null
                              }`)},...requiredValidatorSegment},buId:{type:"string",title:i18nExpression("contractMod.fullPathId"),"x-hidden":!0},vendorName:{type:"string",title:i18nExpression("contractMod.vendorName"),"x-decorator":"FormItem","x-component":"QuickSearchWrapper","x-component-props":{showKey:"companyName",propKey:"companyName","read-pretty":"{{$form.readPretty || $vendor()}}",disabled:expression("$form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'"),name:"scc_sup_company_info_new","@close-quicksearch":expression(`(val, scope) => {
                                $values.vendorId = val ? val.companyId : ''
                                $values.vendorCode = val ? val.companyCode : ''
                                $values.erpVendorCode = val ? val.erpVendorCode : ''
                                $values.erpVendorId = val ? val.erpVendorId : ''
                                if (val.companyName) {
                                  const partnerData = $form.query('partnerData').take()
                                  partnerData.value.forEach((e, index) => {
                                    if (e.partnerType == '乙方') {
                                      partnerData.remove(index)
                                    }
                                  })

                                  if (!partnerData.value.some(item => item.partnerType === '乙方')) {
                                    partnerData.value.push({
                                      partnerType: '乙方',
                                      partnerName: val.companyName,
                                      bankName: val.bankName,
                                      bankCode: val.bankCode,
                                      bankAccount: val.bankAccount,
                                      taxPayer: val.lcCode,
                                      address: val.address,
                                      lcCode: val.lcCode
                                    })
                                  }
                                }
                              }`)},"x-reactions":{dependencies:["contractType"],fulfill:{schema:{"component[1].disabled":expression('$deps[0] !== "MIAN_CONTRACT_ADD"')}}},...requiredValidatorSegment},formal:{type:"string",title:i18nExpression("contractMod.signingMethod"),"x-visible":expression("!$isTermination"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"CONTRACT_FORM2",disabled:expression("$form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'")},...requiredValidatorSegment},effectiveDateFrom:{title:i18nExpression("contractMod.contractValidFrom"),"x-decorator":"FormItem",...yearMonthDaySelectorSegment,...requiredValidatorSegment},effectiveDateTo:{title:i18nExpression("contractMod.contractValidTo"),"x-decorator":"FormItem",...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],"picker-options":expression(`{
                                disabledDate: (time) => {
                                  const effectiveDateFrom = $self.query('.effectiveDateFrom').get('value')

                                  return time.getTime() < (new Date(effectiveDateFrom)).getTime()
                                }
                              }`)},...requiredValidatorSegment},contractClass:{type:"string",title:i18nExpression("contractMod.mgsContractType"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"ELEM_CONTRACT_TYPE",disabled:expression("$form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'"),beforeChange:expression(`(value) => {
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
                                    `)}}}],...requiredValidatorSegment},ceeaControlMethod:{type:"string",title:i18nExpression("vendorMod.controlMethod"),"x-visible":expression("!$isTermination"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"MANAGEMENT_CONTROL_MODEL",disabled:expression("$form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'")}},extContractHandlerName:{type:"string",title:"合同经办人","x-decorator":"FormItem","x-component":"QuickSearchWrapper","x-component-props":{name:"scc_rbac_user_display",showKey:"nickname",propKey:"userId","@close-quicksearch":expression(`(val) => {
                                $values.extContractHandlerId = val ? val.userId : null
                                $values.extContractHandlerName = val ? val.nickname : null
                                $values.extContractHandlerAccount = val ? val.username : null
                                if (val?.username) {
                                  $contractManagement.contract.getDepartment(val.username).then(res => {
                                    if (res.data) {
                                      $values.extHandlerBuName = res.data.ouOrganization?.organizationName
                                      $values.extHandlerBuId = res.data.ouOrganization?.organizationId
                                      $values.extHandlerBuCode = res.data.ouOrganization?.organizationCode
                                    }
                                  })
                                } else {
                                  $values.extHandlerBuName = null
                                  $values.extHandlerBuId = null
                                  $values.extHandlerBuCode = null
                                }
                              }`)},...requiredValidatorSegment},extHandlerBuId:{type:"string",title:"经办人部门","x-decorator":"FormItem","x-component":"OrganizationSelector","x-component-props":{"parent-id":-1,"node-type":"OU","@select":expression(`(node) => {
                                $values.extHandlerBuName = node ? node.organizationName : null
                                $values.extHandlerBuId = node ? node.organizationId : null
                                $values.extHandlerBuCode = node ? node.organizationCode : null
                              }`)}},isFrameworkAgreement:{title:i18nExpression("contractMod.isFrameworkAgreement"),...radioGroupByYOrNSegment,...requiredValidatorSegment,default:"N","x-component-props":{disabled:expression("$vendor() || $isTermination || $form.readPretty || $form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'"),"@change":expression(`(value) => {
                                if(value === 'Y'){
                                  $values.ceeaControlMethod = 'CERTAION_AMOUNT'
                                }
                              }`)}},sourceType:{type:"string","x-hidden":!0},extPricePoolFlag:{type:"string",title:"是否进价格库","x-decorator":"FormItem","x-decorator-props":{tooltip:"进入价格库物资，协议期内采购无需询比价",tooltipLayout:"icon"},"x-component":"DictSelect","x-component-props":{code:"YES_OR_NO",disabled:expression("$values.sourceType === 'BID_NOTICE' || $form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'")},...requiredValidatorSegment},extInvestNo:{type:"string",title:"投资编号","x-decorator":"FormItem","x-component-props":{disabled:expression("$form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'")}},sealName:{type:"string",title:"我方印章","x-decorator":"FormItem","x-component":"QuickSearchWrapper","x-component-props":{name:"scc_pj_contract_seal",showKey:"sealName","pre-query-data":expression(`{
                                't.SIGN_COMPANY_NAME': $form.values.buName
                              }`),"@before-open":expression(`(value, callback) => {
                                if (!$form.values.buName) {
                                  $message.warning($t('cusEntry.tipMessage.selectBuName'))
                                  callback()
                                }
                              }`),"@close-quicksearch":expression(`(val) => {
                                $values.sealName = val ? val.sealName : ''
                                $values.sealId = val ? val.sealId : null
                              }`)},...requiredValidatorSegment},extIncome:{type:"string",title:"收支方向","x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"CONTRACT_EXT_INCOME"},...requiredValidatorSegment},extRent:{type:"string",title:"是否租赁","x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"},...requiredValidatorSegment},extCycle:{type:"string",title:"是否周期合同","x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"},...requiredValidatorSegment},signingAddress:{type:"string",title:i18nExpression("contractMod.signingAddress"),"x-visible":expression("!$isTermination"),"x-decorator":"FormItem","x-decorator-props":{gridSpan:2},"x-component":"Input.TextArea","x-component-props":{placeholder:i18nExpression("common.pleaseTypeContents"),disabled:expression("$form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'")}}}}}},otherInfo:{type:"void","x-component":"FormCollapse.Item","x-component-props":{title:expression('$t($isTermination ? "contractMod.terminationInformation" : "vendorMod.otherInfo")')},"x-query-engine-skip":!0,"x-read-pretty":expression("$vendor() || $form.readPretty"),properties:{layout:{type:"void",...formGridSegment,properties:{needVendorConfirm:{title:i18nExpression("contractMod.supplierConfirmation"),"x-hidden":"{{$vendor()}}",...radioGroupByYOrNSegment,...requiredValidatorSegment,"x-component-props":{"@change":expression("() => updateButtonConfig($form)")}},modelEnable:{title:i18nExpression("vendorMod.enableFlagModel"),...radioGroupByYOrNSegment,...requiredValidatorSegment,default:"N","x-visible":expression("!$isTermination"),"x-component-props":{"@change":expression(`(value) => {
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
                                  $form.values.modelHeadId = null
                                  $form.values.modelName = ''
                                  return
                                }

                                fileUploads.value.forEach((row, index) => {
                                  if (row.fileType == 'CONTRACT_AGREEMENT') {
                                    fileUploads.remove(index)
                                  }
                                })
                              }`)}},modelName:{type:"string","x-hidden":!0},modelHeadId:{type:"string",title:i18nExpression("dataConfMod.templateName"),"x-decorator":"FormItem","x-hidden":expression("$isTermination"),"x-component":"Select","x-component-props":{"@change":expression(`(val) => {
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
                                    $self.readPretty || !['MIAN_CONTRACT_ALTER','MIAN_CONTRACT_ADD'].includes($deps[0]) || $vendor()
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
                                  `)}}},...requiredValidatorSegment}}},layout2:{type:"void",...formGridSegment,properties:{contractRemark:{type:"string",title:i18nExpression("bid_mod.remark"),"x-visible":expression("!$isTermination"),"x-component":"Input.TextArea","x-component-props":{rows:2,placeholder:i18nExpression("common.pleaseTypeContents")},"x-decorator":"FormItem","x-decorator-props":{gridSpan:4}},contractTerminationReason:{type:"string",title:i18nExpression("contractMod.reasonTermination"),"x-visible":expression("$isTermination"),"x-component":"Input.TextArea","x-component-props":{rows:2,maxlength:300,"show-word-limit":!0,placeholder:i18nExpression("common.pleaseTypeContents")},"x-decorator":"FormItem","x-decorator-props":{gridSpan:4},...requiredValidatorSegment},drafterOpinion:{type:"string",title:i18nExpression("vendorMod.loggerComment"),"x-component":"Input.TextArea","x-component-props":{placeholder:i18nExpression("common.pleaseTypeContents"),rows:2},"x-decorator":"FormItem","x-decorator-props":{gridSpan:4}}}}}},basisFileInfo:{type:"void","x-visible":expression("!$isTermination && $buyer()"),"x-component":"FormCollapse.Item","x-component-props":{title:"合同依据（长城内部可见，用于合同发起的背景说明或技术需求）"},properties:{toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},"x-reactions":expression(`(field) => {
                          field.visible = !$form.readPretty && $buyer()
                        }`),properties:{add:{type:"void",title:i18nExpression("common.add"),"x-component-props":{type:"primary",disabled:expression("$vendor()"),"@click":expression(`() => {
                                $self.query($getFieldParentFieldFormPath($self, 2).concat('basisAnnexes'))
                                  .take(field => {
                                    field.componentProps.componentInstance.addRow()
                                  })
                              }`)}}}},basisAnnexes:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",maxHeight:400,editMode:!0,pagination:!1,sortable:!1,primaryKey:"basisAnnexId",cascadeDeletion:!0},"x-query-engine-skip":!0,"x-query-engine-relation":"basisAnnexes:*",properties:generateXindexInOrder({fileSourceName:{type:"string",title:i18nExpression("bidMod.fileName"),"x-component":"SrmCommonFile","x-component-props":{disabled:expression("$form.readPretty || $vendor()"),defaultFile:{fileId:expression(`
                                  !$self.value ? undefined : $table.getRowByIndex($self.index).fileuploadId
                                `),fileName:expression("$self.value")},"@on-change":expression(`({file}) => {
                                const row = $table.getRowByIndex($self.index)
                                row.fileuploadId = file.fileId
                                row.fileSourceName = file.fileName
                              }`)},"x-read-pretty":expression("$readOnly"),"x-render-table-column":{minWidth:150},...editTableFormItemValid},creationDate:{type:"string",title:i18nExpression("purchaseDemand.attachmentCreatedDate"),"x-render-table-column":{minWidth:150},"x-read-pretty":!0},remark:{type:"string",title:"备注","x-render-table-column":{minWidth:150},"x-read-pretty":expression("$readOnly || $vendor()")},operation:{type:"void",title:i18nExpression("common.operation"),"x-render-table-column":{width:60,fixed:"right"},"x-component":"RenderTableButtonList","x-reactions":expression(`field => {
                              field.visible = !$form.readPretty
                            }`),properties:{delete:{type:"void",title:i18nExpression("common.delete"),"x-component-props":{type:"text",disabled:expression("$form.readPretty || $vendor()"),"@click":expression(`
                                    ({ rowIndex }) => {
                                      $table.remove(rowIndex)
                                    }
                                  `)}}}}})}}},contractFinancialInformation:{type:"void","x-component":"FormCollapse.Item","x-component-props":{title:i18nExpression("contractMod.contractFinancialInformation")},"x-reactions":expression(`field => {
                      field.visible = !$isTermination

                      if (field.visible) {
                        field.readPretty = $vendor() || $form.readPretty || $form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'
                      }
                    }`),properties:{layout:{type:"void",...formGridSegment,"x-query-engine-skip":!0,properties:{includeTaxAmount:{type:"number",title:i18nExpression("cusEntry.contractMod.totalAmountTax1"),default:0,"x-decorator":"FormItem","x-component-props":{controls:!1,precision:2}},currencyCode:{type:"string",title:i18nExpression("contractMod.currencyCode"),default:"RMB","x-component":"DictSelect","x-component-props":{code:"currency"},"x-decorator":"FormItem",...requiredValidatorSegment}}}}},itemInfo:{type:"void","x-component":"FormCollapse.Item","x-component-props":{title:i18nExpression("合同签约明细")},"x-visible":expression("!$isTermination"),"x-read-pretty":expression("$form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'"),properties:{toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},"x-reactions":expression(`(field) => {
                          // 来源类型为中标通知书和手工新增显示
                          field.visible = !$form.readPretty  && ['BID_NOTICE','MANUALLY_CREATED'].includes($values.sourceType)
                        }`),properties:{add:{type:"void",title:i18nExpression("common.add"),"x-component-props":{type:"primary",disabled:expression("$vendor() || $form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'"),"@click":expression(`() => {
                                let contractQuantity = $eqY($values.isFrameworkAgreement) ? 1 : undefined
                                $self.query($getFieldParentFieldFormPath($self, 2).concat('materialListData'))
                                  .take(field => {
                                    field.componentProps.componentInstance.addRow('push',{
                                      contractQuantity
                                    })
                                  })
                              }`)}},importExcel:{type:"void","x-component":"ImportExcel","x-component-props":{title:i18nExpression("common.import"),type:"default",disabled:expression("$vendor() || $form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'"),extraData:{fileModular:"cm",fileFunction:"contractItemImportExcel",fileType:"excel"},upLoadUrl:"/api-cm/contractHead/ext/analyzeExcel",downloadTemplateOptions:{downloadUrl:"/api-cm/contractHead/ext/downloadMaterialModel",fileName:"合同明细导入模板.xlsx"},"@handleSuccess":expression(`(data) => {
                                console.log('data',data)
                                if(data && data.length){
                                  const materialListData = $form.query('materialListData').take()
                                  for(let item of data){
                                    materialListData.value.push(item)
                                  }
                                }
                              }`)}}}},materialListData:{type:"array","x-component":"RenderTable","x-component-props":{editMode:!0,maxHeight:400,pagination:!1,sortable:!1,primaryKey:"contractMaterialId",cascadeDeletion:!0},"x-query-engine-skip":!0,"x-query-engine-relation":"contractMaterials:*","x-read-pretty":expression("$vendor() || $form.readPretty"),properties:generateXindexInOrder({contractMaterialId:{type:"number","x-hidden":!0},lineNumber:{type:"number","x-hidden":!0},invName:{type:"string",title:i18nExpression("contractMod.invId"),"x-hidden":expression("$buyer()"),"x-render-table-column":{minWidth:180,static:!0}},invId:{type:"number",default:null,title:i18nExpression("contractMod.invId"),"x-render-table-column":{minWidth:180},"x-hidden":!0,"x-component":"OrganizationSelector","x-component-props":{jumpLogin:expression("$jumpLogin"),"read-pretty":expression("$vendor() || $form.readPretty || $form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'"),nodeType:"INV",parentId:queryFieldValueExpression("buId"),scope:expression("$table.getRowByIndex($self.index)"),"@select":expression(`(node, _) => {
                                const row = $table.getRowByIndex($self.index)
                                row.invCode = node && node.organizationCode
                                row.invName = node && node.organizationName
                                row.invFullPathId = node && node.fullPathId
                              }`)},...editTableFormItemValid},tradingLocations:{type:"string",title:"收货地点","x-render-table-column":{minWidth:300},"x-read-pretty":expression("$vendor() || $form.readPretty || $form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'")},materialCode:{type:"number",default:void 0,title:i18nExpression("contractMod.materialCode"),"x-render-table-column":{minWidth:140},"x-component":"QuickSearchWrapper","x-component-props":{disabled:expression("$form.readPretty || $vendor() || $form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'"),"show-input":expression("$self.value"),"read-pretty":"{{$form.readPretty}}","show-key":"materialCode",name:"scc_base_material_item_contract","@close-quicksearch":expression(`(val, scope) => {
                                const row = $table.getRowByIndex($self.index)

                                row.materialId = val ? val.materialId : null
                                row.materialName = val ? val.materialName : null
                                row.categoryName = val ? val.categoryName : null
                                row.categoryId = val ? val.categoryId : null
                                row.categoryCode = val ? val.categoryCode : null
                                row.specification = val ? val.materialType : null
                                row.unitCode = val ? val.unit : null
                                row.unitName = val ? val.unitName : null
                              }`)}},materialName:{type:"string",title:i18nExpression("contractMod.materialName"),"x-render-table-column":{minWidth:140},"x-component-props":{disabled:expression("!!$table.getRowByIndex($self.index)?.materialCode")},...editTableFormItemValid},categoryName:{type:"string",title:i18nExpression("common.categoryName"),"x-render-table-column":{minWidth:140},"x-component":"CCategorySelect","x-component-props":{showKey:"categoryName","@select":expression(`(node) => {
                                let row = $table.getRowByIndex($self.index)
                                row.categoryId = node ? node.categoryId : null
                                row.categoryName = node ? node.categoryName : ''
                                row.categoryCode = node ? node.categoryCode : ''
                              }`)}},taxKey:{type:"string",title:i18nExpression("contractMod.taxRate"),"x-render-table-column":{minWidth:120},"x-component":"DictSelect","x-component-props":{code:"tax","@change":expression(`(value) => {
                                const data = $taxDictClass.getDictDetail('tax', value)

                                const row = $table.getRowByIndex($self.index)

                                if (data) {
                                  row.taxRate = data.key
                                }

                                $calcMaterialTaxedPrice($form, row)
                              }`)},...editTableFormItemValid},untaxedPrice:{type:"number",default:void 0,title:i18nExpression("bid_mod.untaxedPrice"),"x-render-table-column":{minWidth:120},"x-component-props":{"@change":expression(`() => {
                                $calcMaterialTaxedPrice($form, $table.getRowByIndex($self.index))
                              }`)},...editTableFormItemValid},taxedPrice:{type:"number",default:void 0,title:i18nExpression("bid_mod.taxUnitPrice"),"x-render-table-column":{minWidth:120,static:!0},"x-component-props":{controls:!1,precision:2}},contractQuantity:{type:"number",default:void 0,title:i18nExpression("contractMod.contractQuantity"),"x-render-table-column":{minWidth:120},"x-component-props":{"@change":expression(`() => {
                                $calcMaterialTaxedPrice($form, $table.getRowByIndex($self.index))
                              }`)},...editTableFormItemValid},amount:{type:"number",default:void 0,title:i18nExpression("contractMod.amount2"),"x-render-table-column":{minWidth:120,static:!0}},unitName:{type:"string",title:i18nExpression("contractMod.unitName"),"x-render-table-column":{minWidth:120},"x-component":"DictSelect","x-component-props":{code:"unit"}},taxRate:{type:"string","x-hidden":!0},unAmount:{type:"number",default:void 0,title:i18nExpression("contractMod.unAmount"),"x-render-table-column":{minWidth:120,static:!0}},taxQuota:{type:"number",default:void 0,title:i18nExpression("contractMod.taxQuota"),"x-render-table-column":{minWidth:120,static:!0}},extInvoiceType:{type:"string",title:"发票类型","x-render-table-column":{minWidth:150},"x-component":"DictSelect","x-component-props":{code:"EXT_SOU_INQ_ORDER_INVOICE_TYPE"},...editTableFormItemValid},specification:{type:"string",title:i18nExpression("contractMod.specification"),"x-render-table-column":{minWidth:120}},manufacturer:{type:"string",title:i18nExpression("contractMod.manufacturer"),"x-render-table-column":{minWidth:120}},shelfLife:{type:"number",default:void 0,title:"质保期(自然日)","x-component-props":{controls:!1},"x-render-table-column":{minWidth:120}},lineRemark:{type:"string",title:i18nExpression("contractMod.lineRemark"),"x-render-table-column":{minWidth:120}},itemNumber:{type:"number",default:void 0,title:i18nExpression("contractMod.itemNumber"),"x-render-table-column":{minWidth:120}},operation:{type:"void",title:i18nExpression("common.operation"),"x-render-table-column":{width:60,fixed:"right"},"x-component":"RenderTableButtonList","x-reactions":expression(`(field) => {
                              field.visible = !$form.readPretty
                            }`),properties:{delete:{type:"void",title:i18nExpression("common.delete"),"x-component-props":{type:"text",disabled:expression("$vendor() || $form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'"),"@click":expression(`
                                    () => {
                                      $table.remove($self.index)
                                    }
                                  `)}}}}})}}},paymentPlan:{type:"void","x-component":"FormCollapse.Item","x-component-props":{title:i18nExpression("cusEntry.contractMod.paymentPlan")},"x-visible":expression("!$isTermination"),properties:{toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},"x-reactions":expression(`(field) => {
                          field.visible = !$form.readPretty
                        }`),properties:{add:{type:"void",title:i18nExpression("common.add"),"x-component-props":{type:"primary",disabled:expression("$vendor()"),"@click":expression(`() => {
                                $self.query($getFieldParentFieldFormPath($self, 2).concat('payPlanData'))
                                  .take(field => {
                                    field.componentProps.componentInstance.addRow()
                                  })
                              }`)}}}},payPlanData:{type:"array","x-component":"RenderTable","x-component-props":{editMode:!0,maxHeight:400,pagination:!1,sortable:!1,primaryKey:"payPlanId",cascadeDeletion:!0},"x-query-engine-skip":!0,"x-query-engine-relation":"payPlans:*","x-read-pretty":expression("$vendor() || $form.readPretty"),properties:generateXindexInOrder({paymentPeriod:{type:"string",title:i18nExpression("cusEntry.contractMod.paymentPeriod"),"x-render-table-column":{minWidth:120,align:"center"},"x-component":"RenderTableIndex","x-component-props":{"@changeIndex":expression(`(index) => {
                                $self.value = index + 1
                              }`)}},paymentStage:{type:"string",title:i18nExpression("cusEntry.contractMod.paymentStage"),"x-render-table-column":{minWidth:130},"x-component":"DictSelect","x-component-props":{code:"PAYMENT_STAGE"},...editTableFormItemValid},payExplain:{type:"string",title:i18nExpression("cusEntry.contractMod.payExplain"),"x-render-table-column":{minWidth:150},"x-component":"DictSelect","x-component-props":{code:"payExplain","custom-select-type":"payExplain"},...editTableFormItemValid},dateNum:{type:"number",title:i18nExpression("contractMod.dateNum"),"x-render-table-column":{minWidth:80},"x-component-props":{controls:!1},...editTableFormItemValid},stagePaymentAmount:{type:"number",default:void 0,title:i18nExpression("cusEntry.contractMod.stagePaymentAmount"),"x-render-table-column":{minWidth:150},"x-component-props":{controls:!1},...editTableFormItemValid},plannedPaymentDate:{title:i18nExpression("cusEntry.contractMod.plannedPaymentDate"),"x-render-table-column":{minWidth:130},type:"date",default:null,"x-component-props":{placeholder:i18nExpression("common.pleaseSelectDate"),format:"yyyy-MM-dd","value-format":"yyyy-MM-dd"},...editTableFormItemValid},payMethod:{type:"string",title:i18nExpression("cusEntry.contractMod.paymentMethod"),"x-render-table-column":{minWidth:140},"x-component":"DictSelect","x-component-props":{code:"PAYMENT_MODE"},...editTableFormItemValid},extAcceptanceDate:{type:"date",title:"承兑期限","x-render-table-column":{minWidth:120},default:null,"x-component-props":{placeholder:i18nExpression("common.pleaseSelectDate"),format:"yyyy-MM-dd","value-format":"yyyy-MM-dd"}},extAcceptanceRatio:{type:"number",default:void 0,title:"承兑比例%","x-render-table-column":{minWidth:120},"x-component-props":{controls:!1},...feedbackLayoutIsPopover,"x-validator":{required:!0,message:i18nExpression("common.requiredField")},"x-reactions":{dependencies:[".payMethod",".extWireTransferRatio"],fulfill:{state:{value:expression(`
                                    $deps[0] === 'HONOUR' ? 100 : (
                                      $deps[0] === 'WIRE_TRANSFER' ? 0 : (100-$deps[1])
                                    )
                                  `)}}}},extWireTransferRatio:{type:"number",default:void 0,title:"电汇比例%","x-render-table-column":{minWidth:120},"x-component-props":{controls:!1},...feedbackLayoutIsPopover,"x-validator":{required:!0,message:i18nExpression("common.requiredField")},"x-reactions":{dependencies:[".payMethod",".extAcceptanceRatio"],fulfill:{state:{value:expression(`
                                    $deps[0] === 'WIRE_TRANSFER' ? 100 : (
                                      $deps[0] === 'HONOUR' ? 0 : (100-$deps[1])
                                    )
                                  `)}}}},operation:{type:"void",title:i18nExpression("common.operation"),"x-render-table-column":{width:60,fixed:"right"},"x-component":"RenderTableButtonList","x-reactions":expression(`(field) => {
                              field.visible = !$form.readPretty
                            }`),properties:{delete:{type:"void",title:i18nExpression("common.delete"),"x-component-props":{type:"text",disabled:expression("$vendor() || $form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'"),"@click":expression(`
                                    ({ rowIndex }) => {
                                      $table.remove(rowIndex)
                                    }
                                  `)}}}}})}}},partner:{type:"void","x-component":"FormCollapse.Item","x-component-props":{title:i18nExpression("contractMod.partner")},"x-visible":expression("!$isTermination"),"x-read-pretty":expression("$form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'"),properties:{partnerData:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,maxHeight:400,pagination:!1,sortable:!1,primaryKey:"partnerId",cascadeDeletion:!0},"x-query-engine-skip":!0,"x-query-engine-relation":"contractPartners:*","x-read-pretty":expression("$vendor() || $form.readPretty"),properties:generateXindexInOrder({partnerType:{type:"string","x-read-pretty":!0,title:i18nExpression("contractMod.partnerType"),enum:[{label:i18nExpression("contractMod.owner"),value:"甲方"},{label:i18nExpression("contractMod.partyB"),value:"乙方"},{label:i18nExpression("contractMod.partyC"),value:"丙方"}],"x-component":"Select","x-component-props":{},"x-render-table-column":{minWidth:100},...editTableFormItemValid},partnerName:{type:"string",title:i18nExpression("contractMod.partnerName"),"x-read-pretty":!0,"x-render-table-column":{minWidth:150},"x-component":"OrganizationSelector","x-component-props":{multiple:!1,nodeType:"COMPANY","read-pretty":expression("$vendor() || $form.readPretty"),scope:expression("$table.getRowByIndex($self.index)"),"@select":expression(`(node, value, scope) => {
                                const row = $table.getRowByIndex($self.index)
                                $self.value = node ? node.organizationName : null
                                row.ouId = node ? node.organizationId : null
                                row.ouCode = node ? node.organizationCode : null
                                row.ouName = node ? node.organizationName : null
                              }`)},"x-reactions":{dependencies:[".partnerType"],fulfill:{schema:{"x-component":expression(`
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
                                  `)}}},...editTableFormItemValid},contactName:{type:"string",title:"授权代表","x-render-table-column":{minWidth:120},"x-reactions":{dependencies:[".partnerType"],fulfill:{schema:{"x-read-pretty":expression("$form.readPretty || $vendor()"),"x-component":expression(`
                                    $self.index === undefined ? '' :
                                    ($deps[0] === '甲方' ?
                                    'QuickSearchWrapper' : 'Input')
                                  `),"x-component-props":expression(`
                                    $deps[0] === '甲方' ?
                                    {
                                      showKey:'nickname',
                                      propKey:'contactName',
                                      name:'scc_rbac_user_display',
                                      '@close-quicksearch': (val,scope) => {
                                        const row = $table.getRowByIndex($self.index)
                                        $self.value = val ? val.nickname : null
                                        row.contactName = val ? val.nickname : null
                                        row.extEmployeeNumber = val ? val.username : null
                                      }
                                    }:{}
                                  `)}}},...editTableFormItemValid},phone:{type:"string",title:i18nExpression("contractMod.mobileNumber"),"x-render-table-column":{minWidth:150},...editTableFormItemValid},address:{type:"string",title:i18nExpression("components.address.addressInfo"),"x-render-table-column":{minWidth:150}},bankName:{type:"string",title:i18nExpression("contractMod.openingBank"),"x-render-table-column":{minWidth:150}},bankAccount:{type:"string",title:i18nExpression("contractMod.bankAccount"),"x-render-table-column":{minWidth:150}},taxPayer:{type:"string",title:i18nExpression("dataConfMod.taxPayer"),"x-render-table-column":{minWidth:130},...editTableFormItemValid},extStampStatus:{type:"string",default:"UNSTAMP","x-hidden":expression("$form.values.formal !== 'ELECTRONIC_CONTRACT'"),title:i18nExpression("cusEntry.dataConfMod.extStampStatus"),"x-render-table-column":{minWidth:130},"x-component":"DictSelect","x-component-props":{code:"CONTRACT_EXT_STAMP_STATUS",disabled:!0}}})}}},fileInfo:{type:"void","x-component":"FormCollapse.Item","x-component-props":{title:"合同附件信息（内外部均可见，仅用于传递合同盖章文件）"},properties:{toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},"x-reactions":expression(`(field) => {
                          field.visible = !$form.readPretty && $buyer()
                        }`),properties:{add:{type:"void",title:i18nExpression("common.add"),"x-component-props":{type:"primary",disabled:expression("$vendor()"),"@click":expression(`() => {
                                $self.query($getFieldParentFieldFormPath($self, 2).concat('fileUploads'))
                                  .take(field => {
                                    field.componentProps.componentInstance.addRow()
                                  })
                              }`)}}}},fileUploads:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",maxHeight:400,editMode:!0,pagination:!1,sortable:!1,primaryKey:"annexId",cascadeDeletion:!0},"x-query-engine-skip":!0,"x-query-engine-relation":"annexes:*",properties:generateXindexInOrder({fileType:{type:"string",title:i18nExpression("dataConfMod.attachmentType"),default:"OTHER_AGREEMENT","x-component":"DictSelect","x-component-props":{disabled:expression("$vendor() || $form.readPretty"),code:"CONTRACT_AGREEMENT_ATTACHMENT"},"x-render-table-column":{minWidth:150}},fileSourceName:{type:"string",title:i18nExpression("bidMod.fileName"),"x-component":"SrmCommonFile","x-component-props":{disabled:expression("$vendor() || $form.readPretty"),extraData:{fileModular:"sup",fileFunction:"vendorBiddingManagement",fileType:"images"},defaultFile:{fileId:expression(`
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
                              }`)},"x-read-pretty":expression("$readOnly"),"x-render-table-column":{minWidth:150},...editTableFormItemValid},createdFullName:{type:"string",title:i18nExpression("purchaseDemand.attachmentCreatedBy"),"x-read-pretty":!0,"x-render-table-column":{minWidth:150}},creationDate:{type:"string",title:i18nExpression("purchaseDemand.attachmentCreatedDate"),"x-read-pretty":!0,"x-render-table-column":{minWidth:150}},operation:{type:"void",title:i18nExpression("common.operation"),"x-render-table-column":{width:120,fixed:"right"},"x-component":"RenderTableButtonList","x-reactions":expression(`(field) => {
                              $buttonType == 'management' || $buttonType == 'approve'
                            }`),properties:{delete:{type:"void",title:i18nExpression("common.delete"),"x-component-props":{type:"text",disabled:expression("$buttonType !== 'management'"),"@click":expression(`
                                    ({ rowIndex }) => {
                                      $table.remove(rowIndex)
                                    }`)}},edit:{type:"void",title:i18nExpression("common.edit"),"x-component-props":{type:"text",disabled:expression("$buttonType !== 'approve'"),"@click":expression(`
                                    ({ rowIndex }) => {
                                      const row = $table.getRowByIndex(rowIndex)
                                      $onlyOfficeView(row, $form, $buttonType === 'approve')
                                    }`)}}}}})}}},sealFileInfo:{type:"void","x-visible":expression("!$isTermination"),"x-component":"FormCollapse.Item","x-component-props":{title:"盖章附件信息"},properties:{toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},"x-reactions":expression(`(field) => {
                          field.visible = ($attrs.params.flag === 'archive' && $buyer() && $values.formal === 'PAPER_CONTRACT')
                        }`),properties:{add:{type:"void",title:i18nExpression("common.add"),"x-component-props":{type:"primary",disabled:expression("$vendor()"),"@click":expression(`() => {
                                $self.query($getFieldParentFieldFormPath($self, 2).concat('stampAnnexes'))
                                  .take(field => {
                                    field.componentProps.componentInstance.addRow()
                                  })
                              }`)}}}},stampAnnexes:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",maxHeight:400,editMode:!0,pagination:!1,sortable:!1,primaryKey:"stampAnnexId",cascadeDeletion:!0},"x-query-engine-skip":!0,"x-query-engine-relation":"stampAnnexes:*",properties:generateXindexInOrder({fileType:{type:"string",title:i18nExpression("dataConfMod.attachmentType"),"x-read-pretty":expression("!($attrs.params.flag === 'archive' && $buyer() && $values.formal === 'PAPER_CONTRACT')"),"x-component":"DictSelect","x-component-props":{code:"CONTRACT_AGREEMENT_ATTACHMENT"},"x-render-table-column":{minWidth:150}},fileSourceName:{type:"string",title:i18nExpression("bidMod.fileName"),"x-component":"SrmCommonFile","x-component-props":{disabled:expression("!($attrs.params.flag === 'archive' && $buyer() && $values.formal === 'PAPER_CONTRACT')"),extraData:{fileModular:"sup",fileFunction:"vendorBiddingManagement",fileType:"images"},defaultFile:{fileId:expression(`
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
                              }`)},"x-read-pretty":expression("$readOnly"),"x-render-table-column":{minWidth:150},...editTableFormItemValid},creationDate:{type:"string","x-read-pretty":!0,title:i18nExpression("purchaseDemand.attachmentCreatedDate"),"x-render-table-column":{minWidth:150}},operation:{type:"void",title:i18nExpression("common.operation"),"x-render-table-column":{width:60,fixed:"right"},"x-component":"RenderTableButtonList","x-reactions":expression(`(field) => {
                              field.visible = ($attrs.params.flag === 'archive' && $buyer() && $values.formal === 'PAPER_CONTRACT')
                            }`),properties:{delete:{type:"void",title:i18nExpression("common.delete"),"x-component-props":{type:"text","@click":expression(`
                                    ({ rowIndex }) => {
                                      $table.remove(rowIndex)
                                    }`)}}}}})}}},operateRecord:{type:"void","x-visible":expression("!$isTermination"),"x-component":"FormCollapse.Item","x-component-props":{title:"操作记录"},"x-read-pretty":!0,properties:{operationLogs:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!1,maxHeight:400,pagination:!1,sortable:!1,primaryKey:"operationLogId",cascadeDeletion:!0},"x-query-engine-skip":!0,"x-query-engine-relation":"operationLogs:*","x-read-pretty":!0,properties:generateXindexInOrder({operationType:{type:"string",title:"操作类型","x-render-table-column":{minWidth:130},"x-component":"DictSelect","x-component-props":{code:"CONTRACT_STATUS"}},operationDesc:{type:"string",title:"描述","x-render-table-column":{minWidth:180}},creationDate:{type:"string",title:"操作时间","x-render-table-column":{minWidth:130}},createdFullName:{type:"string",title:"操作人","x-render-table-column":{minWidth:130}}})}}},supplementaryAgreement:{type:"void","x-component":"FormCollapse.Item","x-component-props":{title:i18nExpression("contractMod.supplementaryAgreement")},"x-visible":expression("!$isTermination"),"x-reactions":changeFieldVisibleByDeps(["contractType"],"$deps[0] === 'SUPPLEMENTAL_AGREEMENT'"),properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:4,columnGap:32,rowGap:0},properties:{supplementAgreementExplain:{type:"string",title:i18nExpression("common.pleaseTypeContents"),"x-component":"Input.TextArea","x-component-props":{rows:4,maxlength:200,"show-word-limit":!0,placeholder:i18nExpression("common.pleaseTypeContents")}}}}}},contractContent:{type:"void","x-component":"FormCollapse.Item","x-component-props":{title:i18nExpression("contractMod.contractContent")},"x-reactions":changeFieldVisibleByDeps(["modelEnable"],"!$isTermination && $eqY($deps[0])"),properties:{printContent:{type:"void","x-component":"HTMLElement","x-component-props":{id:"printContent",style:{width:"794px",margin:"0 auto",position:"relative"}},properties:{markedContent:{type:"void","x-component":"div","x-component-props":{id:"markedContent",style:{width:"100%"},contenteditable:queryFieldValueExpression("state","data.contenteditable")}}}}}}})}}},CFillProgress:{type:"void","x-component":"CFillProgress","x-component-props":{class:"contract-progress",ref:"contractProgress",nodeName:"$t('logisticsMod.contractInfo')",data:expression("($attrs.params?.flag === 'termination' || $attrs.params?.contractType === 'TERMINATION') || $form.query('modelEnable').take().value == 'Y'?$form.query('state').get('data').progress:$form.query('state').get('data').progressNo"),percentage:"{{true}}","@index-click":`{{ (code) => {
              let anchorEle = document.querySelector('#collapse_' + code)
              if (anchorEle) {
                anchorEle.scrollIntoView(true)
              }
           } }}`}}}}});return{__sfc:!0,http,emitTabRemove,app,t,eqY,eqN,buyer,vendor,attrs,form,buttonShowFlag,$wrapper,$showLockSeal,$preOptions,viewUpdateButtonSave,viewUpdateButtonsubmit,disabledUpdateButton,updateWorkflowconfig,updateButtonConfig,$compileMarkedContent,initData,$cancel,$getPdfFile,$calcIncludeTaxAmount,$archive,$isTermination,$handleSubmit,$handlePreview,$saveBill,generateComponent,$refuseDialogConfirm,$approvalHanlder,$lockSealHandler,$onlyOfficeView,scope,vendorReadPrettyCollapseItemSegment,schema,components:{CFillProgress,ApprovalProcess},RenderEngine}}});var _sfc_render=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{viewModel:_setup.form,schemaKey:"contractManagerDetail",pageAttrs:_vm.$attrs,schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const contractInformation=__component__.exports;export{contractInformation as default};
