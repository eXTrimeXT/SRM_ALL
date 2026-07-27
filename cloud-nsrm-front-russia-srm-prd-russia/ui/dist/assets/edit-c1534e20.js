import{n as normalizeComponent,al as defineComponent,am as usePageHelper,an as useAttrs,ao as ref$1,ar as defineSchemas,ae as expression,af as i18nExpression,bt as changeFieldVisibleByDeps,ai as generateXindexInOrder,aB as generateCharExpressionByFunction,as as RenderEngine,av as observer,au as DictSelect,v as validEmail,a as validatePhone,cx as FormTab}from"./index-17d0ccd5.js";import{f as formMain,t as tabs}from"./edit.vue_vue_type_style_index_0_lang-0314473d.js";import{s as supCommonApi}from"./supApi-e5726083.js";import{F as FileDynamic}from"./file-dynamic-30cdd411.js";import{v as vendorGreenApi}from"./vendorManagement-89a77d38.js";import{t as transformMQL}from"./util-a92f9f8e.js";import{s as sceneFileApi}from"./basicSetting-f3b18103.js";const _sfc_main$1={name:"changeTitle",props:{language:{type:String,default:()=>""}}};var _sfc_render$1=function(){var _vm=this,_c=_vm._self._c;return _c("div",{staticClass:"changeTitle changeTitleTop"},[_c("i"),_vm._v(_vm._s(_vm.$t(_vm.language))+" ")])},_sfc_staticRenderFns$1=[],__component__$1=normalizeComponent(_sfc_main$1,_sfc_render$1,_sfc_staticRenderFns$1,!1,null,"ea0fcd72",null,null);const changeTitle=__component__$1.exports,_sfc_main=defineComponent({__name:"edit",setup(__props){const{app,emitTabRemove,t,vendor}=usePageHelper(),attrs=useAttrs(),workflowStatus=ref$1("DRAFT"),redFunction=(oldData,newData)=>{let className="";return oldData!=newData&&(className="redColorFont"),className},viewUpdateButton=$form=>{let bol;const changeStatus=attrs.params.row?.changeStatus||null;return changeStatus==="APPROVED"||changeStatus==="SUBMITTED"||changeStatus==="ABANDONED"||attrs.params?.flag==="view"?bol=!1:bol=!0,bol},initButtonConfig=$form=>{setTimeout(()=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance,changeStatus=attrs.params.row?.changeStatus||null;componentInstance.buttonConfigInfo.save.view=viewUpdateButton(),componentInstance.buttonConfigInfo.submit.view=viewUpdateButton()||changeStatus=="VENDOR_SUBMITTED",componentInstance.buttonConfigInfo.cancel.view=changeStatus!="VENDOR_SUBMITTED",componentInstance.buttonConfigInfo.close.view=!1;const approveStatus=attrs.params.row?.changeStatus||null;[null,"DRAFT"].includes(approveStatus)&&(componentInstance.buttonConfigInfo.save.name="暂存",componentInstance.buttonConfigInfo.submit.name="提交"),componentInstance.setWorkflowBusinessId(attrs.params.row?.changeId),componentInstance.setWorkflowTabDisabled(!["APPROVED","SUBMITTED","REJECTED","ABANDONED","VENDOR_SUBMITTED"].includes(attrs.params.row?.changeStatus)),componentInstance.setWorkflowBusinessVariables({})},50)},updateButtonConfig=$form=>{setTimeout(()=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.buttonConfigInfo.save.view=viewUpdateButton(),componentInstance.buttonConfigInfo.submit.view=viewUpdateButton(),componentInstance.buttonConfigInfo.cancel.view=!1,componentInstance.buttonConfigInfo.close.view=!1,componentInstance.setWorkflowBusinessId(attrs.params.row?.changeId),componentInstance.setWorkflowTabDisabled(!["APPROVED","SUBMITTED","REJECTED","ABANDONED"].includes(attrs.params.row?.changeStatus)),componentInstance.setWorkflowBusinessVariables({})},50)},schema=defineSchemas({state:{type:"void","x-component":"Fragment","x-hidden":!0,"x-data":{beforeChangeJson:null,companyChangeId:null,userType:"",serciceCustomDelList:[],companyId:null}},InfoChange:{type:"void","x-decorator":"QueryEngine","x-component":"el-container","x-component-props":{class:"flex-container infoChange",direction:"vertical"},"x-query-engine":{service:"sup",actions:{query:{immediate:!0,tree:!0,ready:expression(`() => {
            initButtonConfig($form)
            // const params = {
            //   pageSize: 15,
            //   pageNum: 1,
            //   sceneCode: 'SCENE_COMPANY_SUNSHINE_FILE',
            //   sceneModuleCode: 'SCENE_COMPANY_SUNSHINE_FILE_ATTACHMENT'
            // }
            // sceneFileApi.listPage(params).then(res => {
            //   const {
            //     attachmentName,
            //     templateFileId
            //   } = res.data?.list?.[0]
            //   $form.values.authInfoBefore.protocolTemplateName = attachmentName
            //   $form.values.authInfoBefore.protocolTemplateId = templateFileId
            //   $form.values.authInfoAfter.protocolTemplateName = attachmentName
            //   $form.values.authInfoAfter.protocolTemplateId = templateFileId
            // })
            return $attrs.params && $attrs.params?.changeId
          }`),autoFormatResult:!1,transformRequest:expression(`(data, headers) => {
            data.query = {
              "*":{},
              companyInfoChange: {'*': {}},
              contactInfoChanges: {'*': {}},
              bankInfoChanges: {'*': {}},
              siteInfoChanges: {'*': {}},
              financeInfoChanges: {'*': {}},
              fileuploadChanges: {'*': {}},
              operatingLogs: {'*': {}},
              npmFinanceReportChanges: { '*': {}},
              npmCompanySizeChanges: { '*': {}},
              npmCateJournalChanges: { '*': {}, npmSerciceCustomChanges: { '*': {}} },
              managementAttachChanges: { '*': {}}
            }
            let req = {
              "filter": {
                  "changeId": {
                      eq: $attrs.params.changeId
                  }
              }
            }
            data.payload = req
            return data
          }`),transformResponse:expression(`(res) => {
            const ress = JSON.parse(res)
            const datas = ress.data.records[0]
            const isPerson = datas?.companyInfoChange?.overseasRelation === 'PERSONAL'
            const beforeChangeJson = JSON.parse(datas.beforeChangeJson)
            $form.query('state').get('data').beforeChangeJson = beforeChangeJson
            const {
              bankInfos,
              contactInfos,
              fileUploads,
              managementAttaches,
              npmCompanySizes,
              npmFinanceReports,
              cateJournalList,
              ...beforeCompanyInfo
            } = beforeChangeJson
            const {
              bankInfoChanges,
              companyInfoChange,
              contactInfoChanges,
              siteInfoChanges,
              financeInfoChanges,
              managementAttachChanges,
              fileuploadChanges,
              operatingLogs,
              npmFinanceReportChanges,
              npmCompanySizeChanges,
              npmCateJournalChanges,
              ...companyInfo
            } = datas
            const {
              totalAssets,
              currentAssets,
              fixedAssets,
              avgAnnualOutput,
              avgAnnualProfit,
              sunshineFileName,
              sunshineFileId
            } = companyInfoChange
            $form.query('form').take().value = companyInfo
            $form.query('state').get('data').companyId = companyInfo.companyId
            $form.query('state').get('data').userType = companyInfo.overseasRelation
            $form.query('state').get('data').companyChangeId = datas?.companyInfoChange?.companyChangeId
            setTimeout(() => {
              if ($attrs.params?.flag == 'view' || ['APPROVED', 'SUBMITTED', 'REJECTED', 'ABANDONED'].includes($attrs.params.row?.changeStatus)) {
                $form.readPretty = true
              }
              if (!isPerson) {
                $form.query('.companyTypeBefore').take().value = beforeCompanyInfo
                beforeCompanyInfo.ceeaBusinessModel = beforeCompanyInfo.ceeaBusinessModel ? beforeCompanyInfo.ceeaBusinessModel.split(',') : []
                $form.query('.companyBaseInfoBefore').take().value = beforeCompanyInfo
                $form.query('.financeInfoBeforeForm').take().value = beforeCompanyInfo
                $form.query('.financeReport').take().value = npmFinanceReports || []
                $form.query('.siteInfosBefore').take().value = npmCompanySizes || []
                $form.query('.qualificationInfoBefore').take().value = managementAttaches || []
                $form.query('.attachFileBefore').take().value = fileUploads || []
                $form.query('.companyTypeAfter').take().value = companyInfoChange
                $form.query('.enterpriseThreeCertificatesAfter').take().value = companyInfoChange
                companyInfoChange.ceeaBusinessModel = companyInfoChange.ceeaBusinessModel ? companyInfoChange.ceeaBusinessModel.split(',') : []
                $form.query('.companyBaseInfoAfter').take().value = companyInfoChange
                $form.query('financeInfoAfterForm').take().value = { totalAssets, currentAssets, fixedAssets, avgAnnualOutput, avgAnnualProfit }
                $form.query('.qualificationInfoAfter').take().value = managementAttachChanges
                $form.query('.financeReportChange').take().value = npmFinanceReportChanges
                $form.query('.siteInfosAfter').take().value = npmCompanySizeChanges
                $form.query('attachFileAfter').take(field => {
                  field.componentProps.componentInstance.reLoadFileInfo()
                })
                $form.query('.attachFileBefore').take(field => {
                  field.componentProps.componentInstance.reLoadFileInfo()
                })
              } else {
                const {
                  businessStartDate,
                  businessEndDate
                } = beforeCompanyInfo
                beforeCompanyInfo.validityPeriodOfCard = [businessStartDate, businessEndDate]
                $form.query('.personBaseInfoBefore').take().value = beforeCompanyInfo
                companyInfoChange.validityPeriodOfCard = [companyInfoChange.businessStartDate, companyInfoChange.businessEndDate]
                $form.query('.personBaseInfoAfter').take().value = companyInfoChange
              }
              // $form.query('.authInfoBefore').take().value = { sunshineFileName: beforeCompanyInfo.sunshineFileName, sunshineFileId: beforeCompanyInfo.sunshineFileId }
              // $form.query('.authInfoAfter').take().value = { sunshineFileName, sunshineFileId }
              $form.query('.enterpriseThreeCertificatesBefore').take().value = beforeCompanyInfo
              $form.query('.bankInfoBefore').take().value = bankInfos || []
              $form.query('.bankInfoAfter').take().value = bankInfoChanges
              $form.query('.contactDataBefore').take().value = contactInfos || []
              $form.query('.contactInfoChanges').take().value = contactInfoChanges
              $form.query('.attachFileAfter').take().value = fileuploadChanges
              $form.query('.operatingLogsData').take().value = operatingLogs
              const serviceRange = cateJournalList.map(item => {
                const {
                  npmSerciceCustoms,
                  ...form
                } = item
                return {
                  list: npmSerciceCustoms,
                  tableForm: form
                }
              })
              $form.query('.serviceRangeBefore').take().value = serviceRange || []
              $form.query('.serviceRangeAfter').take().value = npmCateJournalChanges.map(item => {
                const {
                  npmSerciceCustomChanges,
                  ...form
                } = item
                return { list: npmSerciceCustomChanges, tableForm: form}
              })
            })
            return ress
          }`)},submit:{autoFormatResult:!1,cascadeDeletion:!0},saveTemporary:{autoFormatResult:!1,cascadeDeletion:!0},save:{cascadeDeletion:!0}}},properties:{SchemaWorkflow:{type:"void","x-component":"SchemaWorkflow","x-component-props":{"business-id":expression("$attrs.params.row?.planConfirmId || null"),"business-type":"SUPPLIERINFOCHANGE","@click-handler":expression(`(type) => {
            $submits(type, $form, $queryEngine, $message, $t, $bus)
          }`),"@submit-direct":expression(`(type) => {
            $submits(type, $form, $queryEngine, $message, $t, $bus)
          }`),"@confirm":expression(`(type, comment) => {
            $submits(type, $form, $queryEngine, $message, $t, $bus)
          }`),"@close-tab":expression(`() => {
            $back($bus)
          }`),"@update-integration-mode":expression(`(integrationMode) => {
            if (integrationMode?.integrationMode == "None") {
              updateButtonConfig($form)
            }
          }`)},items:{type:"object","x-query-engine-skip":!0,properties:{buyerReject:{type:"void","x-content":i18nExpression("common.toRefuse"),"x-component":"Button","x-component-props":{type:"default","@click":expression(`() => {
                   app.$prompt('', '驳回原因', {
                      confirmButtonText: '确定',
                      cancelButtonText: '取消',
                      inputType: 'textarea'
                    }).then(({ value }) => {
                      app.$http({
                        url: '/api-sup/change/infoChange/buyerReject',
                        method: 'POST',
                        data: {
                          changeId: $attrs.params.changeId,
                          flowRemark: value
                        },
                        loading: true
                      }).then(() => {
                        $message.success($t('common.success'))
                        $bus.$emit('vendorInfoChange')
                        emitTabRemove($attrs.tabName)
                      })
                    })
                }`)},"x-reactions":changeFieldVisibleByDeps(["form.changeStatus"],`
                  $deps[0] == 'VENDOR_SUBMITTED'
              `)}}},properties:{layout:{type:"void","x-component":"FormContainer","x-component-props":{class:"vendorInfoChange"},properties:{collapse:{type:"void","x-component":"Collapse",properties:generateXindexInOrder({vendorInfo:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.vendorInfo")},"x-query-engine-skip":!0,properties:{form:{...formMain}}}})},layout:{type:"void","x-component":"FormContainer",properties:{tabs:{type:"void","x-component":"FormTab","x-component-props":{type:"card",class:"changeTab",activeKey:generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").userType!=="PERSONAL"?"tab1":"tab11")},properties:{...tabs}}}},collapse2:{type:"void","x-component":"Collapse",properties:generateXindexInOrder({operatingLogs:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("common.operationRecord")},"x-query-engine-skip":!0,properties:{operatingLogsData:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!1,maxHeight:400,pagination:!1,sortable:!1},"x-query-engine-skip":!0,properties:generateXindexInOrder({creationDate:{type:"string",title:i18nExpression("common.operationTime"),"x-render-table-column":{minWidth:120}},operation:{type:"string",title:i18nExpression("contractMod.operationType"),"x-component":"DictSelect","x-component-props":{code:"OPERATING_TYPE"},"x-render-table-column":{minWidth:120}},reason:{type:"string",title:i18nExpression("common.explanationOfReasons"),"x-render-table-column":{minWidth:120}},createdFullName:{type:"string",title:i18nExpression("common.operator"),"x-render-table-column":{minWidth:120}}})}}}})}}}}}}}}),$back=$bus=>{emitTabRemove(attrs.tabName),$bus.$emit("ModelHead")},fatchCompanyData=async(companyId,$form)=>{if(companyId){const payload={filter:{companyId:{eq:companyId}}},query={"*":{},bankInfos:{"*":{}},contactInfos:{"*":{}},fileUploads:{"*":{}},managementAttaches:{"*":{}},npmCompanySizes:{"*":{}},npmFinanceReports:{"*":{}},operatingLogs:{"*":{}},cateJournalList:{"*":{},npmSerciceCustoms:{"*":{}}}},transformParams=transformMQL.save("CompanyInfo",payload,"query",query),response=await vendorGreenApi.getCompanyInfo(transformParams);if(response&&response.data&&response.data.records.length){const{bankInfos,contactInfos,fileUploads,managementAttaches,cateJournalList,npmCompanySizes,npmFinanceReports,operatingLogs,...baseInfo}=response.data.records[0],{companyId:companyId2,companyName,companyCode,totalAssets,currentAssets,fixedAssets,avgAnnualOutput,avgAnnualProfit,sunshineFileName,sunshineFileId}=baseInfo;$form.query("state").get("data").userType=baseInfo.overseasRelation;const isPerson=baseInfo.overseasRelation==="PERSONAL";if($form.query("state").get("data").beforeChangeJson=JSON.stringify(response.data.records[0]),fileUploads){for(let i=0;i<fileUploads.length;i++){const fileItem=fileUploads[i];fileItem.originalBusinessId=fileItem.businessId,fileItem.sceneFileId=null,fileItem.businessId=null,fileItem.__edit_key__=!0,fileItem.__add_key__=!0,fileItem.__update_key__=!0}isPerson||($form.query(".attachFileBefore").take().value=fileUploads,$form.query("attachFileBefore").take(field=>{field.componentProps.componentInstance.reLoadFileInfo()}),$form.query(".attachFileAfter").take().value=JSON.parse(JSON.stringify(fileUploads)))}baseInfo&&(isPerson||($form.query(".companyTypeAfter").take().value=baseInfo,$form.query(".companyTypeBefore").take().value=JSON.parse(JSON.stringify(baseInfo)),$form.query(".financeInfoBeforeForm").take().value=baseInfo,$form.query(".financeInfoAfterForm").take().value=JSON.parse(JSON.stringify({totalAssets,currentAssets,fixedAssets,avgAnnualOutput,avgAnnualProfit})),$form.query(".enterpriseThreeCertificatesAfter").take().value=baseInfo,$form.query(".enterpriseThreeCertificatesBefore").take().value=JSON.parse(JSON.stringify(baseInfo))),baseInfo.ceeaBusinessModel=baseInfo.ceeaBusinessModel?baseInfo.ceeaBusinessModel.split(","):[],$form.query(".companyBaseInfoAfter").take().value=baseInfo,$form.query(".companyBaseInfoBefore").take().value=JSON.parse(JSON.stringify(baseInfo))),isPerson?($form.query(".personBaseInfoBefore").take().value=baseInfo,$form.query(".personBaseInfoAfter").take().value=JSON.parse(JSON.stringify(baseInfo))):($form.query(".siteInfosBefore").take().value=npmCompanySizes||[],$form.query(".siteInfosAfter").take().value=npmCompanySizes.map(item=>{const{totalNumber,socialSecurityNumber,managementNumber,developerNumber,productionNumber,overUndergraduateNumber,...other}=item;return{...other,totalNumber:totalNumber||null,socialSecurityNumber:socialSecurityNumber||null,developerNumber:developerNumber||null,productionNumber:productionNumber||null,overUndergraduateNumber:overUndergraduateNumber||null}})||[],$form.query(".financeReport").take().value=npmFinanceReports||[],$form.query(".financeReportChange").take().value=JSON.parse(JSON.stringify(npmFinanceReports))||[],$form.query(".qualificationInfoBefore").take().value=managementAttaches||[],$form.query(".qualificationInfoAfter").take().value=JSON.parse(JSON.stringify(managementAttaches))||[]),$form.query(".bankInfoBefore").take().value=bankInfos||[],$form.query(".bankInfoAfter").take().value=JSON.parse(JSON.stringify(bankInfos))||[],$form.query(".contactDataBefore").take().value=contactInfos||[],$form.query(".contactInfoChanges").take().value=JSON.parse(JSON.stringify(contactInfos))||[];const serviceRange=cateJournalList.map(item=>{const{npmSerciceCustoms,formId,...form}=item;return{list:npmSerciceCustoms,tableForm:{formId:null,...form}}});$form.query(".serviceRangeBefore").take().value=serviceRange||[],$form.query(".serviceRangeAfter").take().value=JSON.parse(JSON.stringify(serviceRange))||[]}}},$submits=async(type,$form,$queryEngine,$message,$t,$bus)=>{let values=$form.values;const isPerson=$form.query("state").get("data").userType==="PERSONAL",personFormAfter=isPerson&&$form.query(".personBaseInfoAfter").get("value")||{};if(personFormAfter?.validityPeriodOfCard?.length){const[businessStartDate,businessEndDate]=personFormAfter.validityPeriodOfCard;personFormAfter.businessStartDate=businessStartDate,personFormAfter.businessEndDate=businessEndDate}const companyTypeAfter=$form.query(".companyTypeAfter").get("value"),enterpriseThreeCertificatesAfter=$form.query(".enterpriseThreeCertificatesAfter").get("value"),financeInfoAfterForm=$form.query(".financeInfoAfterForm").get("value"),financeReportChange=$form.query(".financeReportChange").get("value"),companySizes=$form.query(".siteInfosAfter").get("value"),serciceCustomDelList=$form.query("state").get("data").serciceCustomDelList||[];let serviceRange=$form.query(".serviceRangeAfter").get("value").map(item=>{const{list,tableForm}=item;return{...tableForm,npmSerciceCustomChanges:[...list,...serciceCustomDelList]}});const managementAttachChanges=$form.query(".qualificationInfoAfter").get("value");let userType=values.form?.userType;(values.form?.userType==null||values.form?.userType==null)&&(userType=app.$store.getters.userType);let allData={beforeChangeJson:$form.query("state").get("data").beforeChangeJson,...values.form,userType,companyInfoChange:Object.assign({companyChangeId:$form.query("state").get("data")?.companyChangeId},companyTypeAfter,enterpriseThreeCertificatesAfter,financeInfoAfterForm,personFormAfter),contactInfoChanges:$form.query(".contactInfoChanges").get("value"),bankInfoChanges:$form.query(".bankInfoAfter").get("value"),siteInfoChanges:$form.query(".siteInfosAfter").get("value"),financeInfoChanges:$form.query(".financeInfoChanges").get("value"),fileuploadChanges:$form.query(".attachFileAfter").get("value"),npmFinanceReportChanges:financeReportChange,npmCompanySizeChanges:companySizes,npmCateJournalChanges:serviceRange,managementAttachChanges};if(allData.changeId&&(allData.companyInfoChange.changeId=allData.changeId,allData.contactInfoChanges||[].forEach(e=>{e.changeId=allData.changeId}),allData.fileuploadChanges||[].forEach(e=>{e.businessId=allData.changeId}),allData.siteInfoChanges||[].forEach(e=>{e.changeId=allData.changeId}),allData.bankInfoChanges||[].forEach(e=>{e.changeId=allData.changeId}),allData.financeReportChange||[].forEach(e=>{e.changeId=allData.changeId})),!isPerson&&(allData.companyInfoChange.registCurrency==""||allData.companyInfoChange.registCurrency==null))return app.$message.error("请输入三证信息的币种"),!1;allData.companyInfoChange.ceeaBusinessModel=allData.companyInfoChange.ceeaBusinessModel.length?allData.companyInfoChange.ceeaBusinessModel.join():"";const changeStatus=attrs.params.row?.changeStatus||null;if(type=="SAVE")[null,"DRAFT"].includes(changeStatus)?(allData.changeStatus="DRAFT",$queryEngine.request.save(allData,{customizeAction:"saveTemporary",query:{"*":{}}}).then(()=>{$message.success($t("common.successSave")),$bus.$emit("vendorInfoChange"),emitTabRemove(attrs.tabName)}).catch(()=>{allData.companyInfoChange.ceeaBusinessModel=allData.companyInfoChange.ceeaBusinessModel?allData.companyInfoChange.ceeaBusinessModel.split(","):[]})):$queryEngine.request.save(allData,{customizeAction:"saveTemporary",query:{"*":{}}}).then(()=>{$message.success($t("common.successSave")),$bus.$emit("vendorInfoChange"),emitTabRemove(attrs.tabName)}).catch(()=>{allData.companyInfoChange.ceeaBusinessModel=allData.companyInfoChange.ceeaBusinessModel?allData.companyInfoChange.ceeaBusinessModel.split(","):[]});else{let overseasRelation=$form.query("state").get("data").userType,validate=0;if(await $form.validate().then().catch(eq=>{app.$message.error(eq[0].messages[0]),validate=1}),validate)return!1;let positionList=[];if(overseasRelation=="INSIDE"){if(positionList=allData.contactInfoChanges.filter(item=>!!item.position).map(itm=>itm.position),!(positionList.includes("SALES_MANAGER")&&positionList.includes("SENIOR_LEADER"))){$message.warning($t("cusEntry.tipMessage.atLeastManageAndLeader"));return}let validContact=!0;if(allData.contactInfoChanges.some(item=>{item.ceeaDefaultContact==="Y"&&!item.socialSecurityCertificateFileId&&(validContact=!1)}),!validContact){$message.warning($t("cusEntry.tipMessage.socialSecurityCertificateMsg"));return}}if(overseasRelation!=="OUT"){let ceeaDefaultList=allData.contactInfoChanges.filter(item=>item.ceeaDefaultContact==="Y");if(!ceeaDefaultList.length||ceeaDefaultList.length>1){$message.warning("联系人默认联系人只能有一个");return}const companyName=allData.companyInfoChange.companyName,mainAccountBankAccountName=allData.bankInfoChanges.find(item=>item.ceeaMainAccount==="Y")?.bankAccountName;if(!mainAccountBankAccountName&&overseasRelation!="OUT")return $message.warning("银行信息必须有一个主账号"),!1;if(companyName!==mainAccountBankAccountName){$message.warning($t("cusEntry.tipMessage.companyAndBankAccount"));return}}if(serviceRange.length===0)return $message.warning($t("cusEntry.tipMessage.atLeastCategory")),!1;let validFlag=!0;if(serviceRange.some(item=>{if(!item.categoryId)return validFlag=!1,!0}),!validFlag)return $message.warning($t("cusEntry.tipMessage.serviceRangeCategoryRequired")),!1;const categoryIdList=new Set(serviceRange.map(item=>item.categoryId));if(serviceRange.length!==categoryIdList.size)return $message.warning($t("cusEntry.tipMessage.serviceRangeCategoryRepeat")),!1;if([null,"DRAFT","SUBMITTED"].includes(changeStatus)&&(allData.changeStatus="DRAFT"),changeStatus=="VENDOR_SUBMITTED"){const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.setWorkflowBusinessId(allData.changeId||null),componentInstance.setWorkflowTabDisabled(!0),componentInstance.setWorkflowBusinessVariables({}),componentInstance.handlerAfter(type.toUpperCase(),()=>{$bus.$emit("vendorInfoChange")})}else $queryEngine.request.save(allData,{customizeAction:"submit",query:{"*":{}}}).then(res=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.setWorkflowBusinessId(res.records[0]||null),componentInstance.setWorkflowTabDisabled(!0),componentInstance.setWorkflowBusinessVariables({}),componentInstance.handlerAfter(type.toUpperCase(),()=>{$bus.$emit("vendorInfoChange")})}).catch(()=>{allData.companyInfoChange.ceeaBusinessModel=allData.companyInfoChange.ceeaBusinessModel?allData.companyInfoChange.ceeaBusinessModel.split(","):[]})}},$showSunFile=$self=>{setTimeout(()=>{const newData=$self.query(".sunshineFileName").get("value")?.split(",")||[],oldData=$self.query($self.parent.parent.parent.address.concat("beforeChange.authInfoBefore.sunshineFileName")).get("value")?.split(",")||[];let className="";(new Set([...newData,...oldData]).size!==oldData.length||newData.length!==oldData.length)&&(className="redColorFont");const fileList=[];if($self.value){const fileIdList=$self.value?.split(","),fileNameList=$self.query(".sunshineFileName").get("value")?.split(",");fileIdList.forEach((item,index)=>{fileList.push({fileId:item,fileName:fileNameList?.[index]})})}$self.setComponentProps({fileList,class:className})})},$showBeforeSunFile=$self=>{const fileList=[];if($self.value){const fileIdList=$self.value?.split(","),fileNameList=$self.query(".sunshineFileName").get("value")?.split(",");fileIdList.forEach((item,index)=>{fileList.push({fileId:item,fileName:fileNameList?.[index]})})}$self.setComponentProps({fileList})};return{__sfc:!0,app,emitTabRemove,t,vendor,attrs,workflowStatus,redFunction,viewUpdateButton,initButtonConfig,updateButtonConfig,schema,$back,fatchCompanyData,$submits,$showSunFile,$showBeforeSunFile,scope:{app,t,$attrs:attrs,emitTabRemove,initButtonConfig,$back,supCommonApi,$submits,observer,DictSelect,fatchCompanyData,redFunction,validEmail,validatePhone,sceneFileApi,$showSunFile,$showBeforeSunFile},components:{FormTab,changeTitle,FileDynamic},RenderEngine}}});var _sfc_render=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{pageAttrs:_vm.$attrs,schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const detail=__component__.exports;export{detail as d};
