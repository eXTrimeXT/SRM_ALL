import{ae as i18nExpression,ad as expression,ah as generateXindexInOrder,ak as defineComponent,al as usePageHelper,am as useAttrs,aq as defineSchemas,cf as formGridSegment,c9 as yearMonthDayHourMinuteSecondSelectorSegment,ar as RenderEngine,bC as toJS,cK as getHeaderField,cm as bus,n as normalizeComponent}from"./index-6b6051d8.js";import{F as FileDynamic}from"./file-dynamic-25a093c4.js";import{c as throttle}from"./util-d962b17f.js";import"./file-dynamic-7fc2d358.js";import"./MainHeader-2d842985.js";/* empty css                                                                   */import"./file-dynamic.vue_vue_type_style_index_0_scoped_516fdfc3_lang-f2c1a82d.js";import"./tree-utils-7df6be59.js";import"./basicSetting-fc46a2d9.js";import"./BaseTableBind-b1f76fc9.js";import"./util-1e55288f.js";import"./index-4d512f00.js";/* empty css                                                                      */import"./fileApi-05bbbbcc.js";const blackDetails={type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("black.blacklistSupplierDetails")},properties:{toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},"x-reactions":expression(`(field) => {
           field.visible = !$form.readPretty
       }`),properties:{addSrmCompany:{type:"void",title:"{{$t('common.new')}}","x-component-props":{type:"primary","@click":expression(`() => {
              $form.query('state').get('data').isSrmCompany = true
              $form.query('addDialog').take().setComponentProps({ visible: true })
              $clearFormField($form)
              setTimeout(() => {
                $values.configForm.oldId  = null
                $values.configForm.index = null
              })
             }`)}}}},blackCompanyList:{type:"array","x-component":"RenderTable","x-component-props":{class:"table-view-vxe-table",editMode:!0,preColumns:expression("$form.readPretty ? 'seq' : 'seq'"),pagination:!1,sortable:!1,primaryKey:"blackCompanyId",cascadeDeletion:!0},"x-query-engine-skip":!0,"x-read-pretty":!0,"x-query-engine-relation":"blackCompanyList:*",properties:generateXindexInOrder({blackCompanyId:{type:"string","x-hidden":!0},companyName:{type:"string",title:"{{$t('common.vendorName')}}","x-render-table-column":{minWidth:120}},socialCreditCode:{type:"string",title:"{{$t('vendorMod.lcCode')}}","x-render-table-column":{minWidth:120}},legalPerson:{type:"string",title:"{{$t('vendorMod.legalPerson')}}","x-render-table-column":{minWidth:120}},shareholder:{type:"string","x-render-table-column":{minWidth:150,title:i18nExpression("cusEntry.vendorMod.shareholder")}},reason:{type:"string","x-render-table-column":{title:"{{$t('black.blackType')}}",minWidth:100}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:120,fixed:"right"},"x-component":"RenderTableButtonList","x-reactions":expression(`(field) => {
                field.visible = !$form.readPretty
            }`),properties:{edit:{type:"void",title:"{{$t('common.edit')}}","x-component-props":{type:"text","@click":expression(`
                       ({ row,rowIndex }) => {
                          $form.query('state').get('data').isSrmCompany = true
                          $form.query('addDialog').take().setComponentProps({ visible: true })
                          $clearFormField($form)
                          setTimeout(() => {
                            $values.configForm.companyId = row.companyId
                            $values.configForm.companyCode = row.companyCode
                            $values.configForm.companyName = row.companyName
                            $values.configForm.companyType = row.companyType
                            $values.configForm.socialCreditCode = row.socialCreditCode
                            $values.configForm.legalPerson = row.legalPerson
                            $values.configForm.registeredCapital = row.registeredCapital
                            $values.configForm.companyCountry = row.companyCountry
                            $values.configForm.companyProvince = row.companyProvince
                            $values.configForm.companyCity = row.companyCity
                            $values.configForm.companyCreationDate = row.companyCreationDate
                            $values.configForm.shareholder = row.shareholder
                            $values.configForm.reason = row.reason
                            $values.configForm.oldId  = row.companyId
                            $values.configForm.index = rowIndex
                          })
                       }
                   `)}},delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{type:"text","@click":expression(`
                       ({ rowIndex }) => {
                          $table.remove(rowIndex)
                       }
                   `)}}}}})}}},FileInfo={type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("bidMod.fileInfo")},properties:{fileUploads:{type:"array","x-query-engine-relation":"fileUploads:*","x-component":"FileDynamic","x-component-props":{primaryKey:"blackId",cascadeDeletion:!0,"scene-module-code":"SCENE_BLACK_ATTACHMENT","business-id":`{{
          $attrs?.params?.row?.blackId?.blackId || $form.values?.blackId || null
        }}`,editable:"{{!$form.readPretty}}","need-init":!0}}}},_sfc_main=defineComponent({__name:"edit-engine",setup(__props){const{emitTabAdd,emitTabRemove,t:$t,app,vendor}=usePageHelper(),$attrs=useAttrs(),$closePageAndRefreshListPageData=$bus=>{$bus.$emit("DeliveryNoteHead"),emitTabRemove($attrs.tabName)},$clearFormField=$form=>{setTimeout(()=>{$form.query("configForm").take().reset()})},$addCompanyByImportExcel=($form,list)=>{let newArray=$form.query("blackCompanyList").get("value");list.forEach(item=>{newArray.push(item)}),$form.query("blackCompanyList").take().setValue(newArray)},$addCompanyOneItem=($form,$values,done,closeLoading)=>{if($values.configForm.oldId==$values.configForm.companyId){let newArray=$form.query("blackCompanyList").get("value"),newObj={shareholder:$values.configForm.shareholder,reason:$values.configForm.reason,companyId:$values.configForm.companyId,companyName:$values.configForm.companyName||$values.configForm.vendorName,companyCode:$values.configForm.companyCode,companyType:$values.configForm.companyType,socialCreditCode:$values.configForm.socialCreditCode,legalPerson:$values.configForm.legalPerson,registeredCapital:$values.configForm.registeredCapital,companyCountry:$values.configForm.companyCountry,companyProvince:$values.configForm.companyProvince,companyCity:$values.configForm.companyCity,companyCreationDate:$values.configForm.companyCreationDate};newArray.splice($values.configForm.index,1,newObj),$form.query("blackCompanyList").take().setValue(newArray),done();return}$form.query(".configForm").take().validate().then(()=>{if($values.blackCompanyList&&$values.blackCompanyList.length&&$values.blackCompanyList.find(item=>item.socialCreditCode===$values.configForm.socialCreditCode))return app.$message.warning($t("black.msgLcCode"));var checkData={...$form.values,blackCompanyList:[$values.configForm]};app.$http({url:"/api-sup/sup/black/checkSubmitData",method:"POST",data:checkData,loading:!0}).then(res=>{let newObj={shareholder:$values.configForm.shareholder,reason:$values.configForm.reason,companyId:$values.configForm.companyId,companyName:$values.configForm.companyName||$values.configForm.vendorName,companyCode:$values.configForm.companyCode,companyType:$values.configForm.companyType,socialCreditCode:$values.configForm.socialCreditCode,legalPerson:$values.configForm.legalPerson,registeredCapital:$values.configForm.registeredCapital,companyCountry:$values.configForm.companyCountry,companyProvince:$values.configForm.companyProvince,companyCity:$values.configForm.companyCity,companyCreationDate:$values.configForm.companyCreationDate},newArray=$form.query("blackCompanyList").get("value");$values.configForm.oldId?newArray.splice($values.configForm.index,1,newObj):newArray.push(newObj),$form.query("blackCompanyList").take().setValue(newArray),done()}).catch(()=>{closeLoading()})}).catch(()=>{closeLoading()})},$saveBill=throttle(async(type,$form,$queryEngine,$message,$bus)=>{if(type=="WITHDRAW"){emitTabRemove($attrs.tabName),bus.$emit("BlackList");return}const values=$form.values;type==="SAVE"?$form.validate().then(()=>{$submitData(type,values,$form,$queryEngine)}):type==="SUBMIT"&&$form.validate().then(()=>{app.$confirm($t("vendorMod.blackListTips"),$t("common.sureSubmit"),{confirmButtonText:$t("common.confirm"),cancelButtonText:$t("common.cancel"),type:"warning"}).then(()=>{$submitData(type,values,$form,$queryEngine)})}).catch(err=>{})},300),$submitData=(type,$values,$form,$queryEngine)=>{const form=toJS($values);delete form.configForm,form.isAllowSourcing="N",form.isAllowCreateOrder="N",form.isAllowWarehousing="N",form.isAllowFinance="N",form.isAllowPayment="N",$attrs.params.flag==="add"&&(form.approveStatus="DRAFT"),$queryEngine.request.baseRequest({type:"Black",lang:"zh-cn",loading:!0,tree:!0,payload:[form],query:{"*":{},blackCompanyList:{"*":{}},fileUploads:{"*":{}}},action:"save"}).then(res=>{if(res.data&&res.data.length>0){const datas=res.data[0];let formHeaderValue=getHeaderField(datas);if(app.$message.success($t("common.successSave")),type==="SUBMIT"){const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.setWorkflowBusinessId(datas.blackId||""),componentInstance.setWorkflowTabDisabled(!1),componentInstance.setWorkflowBusinessVariables({type:"1",formData:{formNo:form?.blackCode},procTitleObj:formHeaderValue}),componentInstance.handlerAfter(type.toUpperCase(),()=>{emitTabRemove($attrs.params.tabName),bus.$emit("BlackList")}),setTimeout(()=>{$form.readPretty=!0,componentInstance.buttonConfigInfo.save.view=!1,componentInstance.buttonConfigInfo.submit.view=!1},100)}else $form.values.blackId=datas.blackId||"",$queryEngine.request.read(),bus.$emit("BlackList")}})},$cancel=()=>{emitTabRemove($attrs.tabName),bus.$emit("BlackList")},initButtonConfig=$form=>{setTimeout(()=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance,viewUpdateButton=$form.query("state").get("data").viewUpdateButton;componentInstance.buttonConfigInfo.save.view=viewUpdateButton,componentInstance.buttonConfigInfo.submit.view=viewUpdateButton,componentInstance.buttonConfigInfo.cancel.view=viewUpdateButton,componentInstance.buttonConfigInfo.close.view=!viewUpdateButton;const disabled=!["SUBMITTED","REJECTED","WITHDRAW","ABANDONED","APPROVED"].includes($attrs?.params?.row.approveStatus);$attrs?.params?.row?.blackId,componentInstance.setWorkflowBusinessId($attrs?.params?.row?.blackId?.blackId),componentInstance.setWorkflowTabDisabled(disabled),componentInstance.setWorkflowBusinessVariables({})},50)},updateButtonConfig=$form=>{setTimeout(()=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance,viewUpdateButton=$form.query("state").get("data").viewUpdateButton;componentInstance.buttonConfigInfo.save.view=viewUpdateButton,componentInstance.buttonConfigInfo.submit.view=viewUpdateButton,componentInstance.buttonConfigInfo.cancel.view=viewUpdateButton,componentInstance.buttonConfigInfo.close.view=!viewUpdateButton,($attrs.params.row?.approveStatus||null)=="SUBMITTED"&&componentInstance.workflowParamsInfo.integrationMode=="Push"&&(componentInstance.buttonConfigInfo.withdraw.view=!0)},50)},scope={$attrs,app,emitTabRemove,$closePageAndRefreshListPageData,$vendor:vendor,$addCompanyOneItem,$clearFormField,$saveBill,updateButtonConfig,initButtonConfig,$addCompanyByImportExcel,$cancel},components={FileDynamic},schema=defineSchemas({state:{type:"void","x-component":"Fragment","x-hidden":!0,"x-data":{isSrmCompany:!1,viewUpdateButton:!0,orderStatus:"DRAFT"}},Black:{type:"void","x-decorator":"QueryEngine","x-component":"el-container","x-component-props":{class:"flex-container",direction:"vertical"},"x-query-engine":{service:"sup",actions:{read:{immediate:!0,ready:expression(`() => {
            $form.readPretty = $attrs.params.flag === 'view' || ['SUBMITTED', 'ABANDONED', 'APPROVED'].includes($attrs?.params?.row.approveStatus)

            console.log($form.readPretty)
            console.log(typeof $form.readPretty)
            initButtonConfig($form)

            return $attrs.params.row.blackId
          }`),transformRequest:expression(`(data, headers) => {
            data.payload = [$attrs?.params?.row?.blackId || $form.values.blackId || '']
            data.query['*'] = {}
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

          }`),cascadeDeletion:!0}}},properties:{SchemaWorkflow:{type:"void","x-component":"SchemaWorkflow","x-component-props":{"business-id":expression("$form.values.blackId || null"),"business-type":"black","button-custom":expression("{}"),"@click-handler":expression(`(type) => {
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus)
          }`),"@submit-direct":expression(`(type) => {
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus)
          }`),"@confirm":expression(`(type, comment) => {
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus)
          }`),"@close-tab":expression(`() => {
            $cancel()
          }`),"@update-integration-mode":expression(`(integrationMode) => {
            updateButtonConfig($form)
          }`)},properties:{collapse:{type:"void","x-component":"Collapse","x-read-pretty":expression("$form.readPretty"),properties:generateXindexInOrder({baseInfo:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.receiptInfo")},"x-query-engine-skip":!0,properties:{layout:{type:"void",...formGridSegment,properties:{blackCode:{type:"string","x-decorator":"FormItem",title:"{{$t('black.blacklistApprovalNumber')}}","x-component-props":{disabled:!0}},createdBy:{type:"string","x-decorator":"FormItem",title:"{{$t('common.creator')}}","x-component-props":{disabled:!0}},creationDate:{...yearMonthDayHourMinuteSecondSelectorSegment,"x-decorator":"FormItem",title:"{{$t('common.creationTime')}}","x-component-props":{...yearMonthDayHourMinuteSecondSelectorSegment["x-component-props"],disabled:!0}}}}}},blackDetail:{...blackDetails},riskControl:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("black.riskControl")},"x-query-engine-skip":!0,"x-hidden":!0,properties:{layout:{type:"void",...formGridSegment,properties:{isImmediatelyBox:{type:"void",title:i18nExpression("black.isImmediately"),"x-decorator":"FormItem","x-query-engine-skip":!0,properties:{isImmediately:{type:"string",default:"N","x-component":"Checkbox","x-component-props":{label:i18nExpression("black.immediate"),"true-label":"Y","false-label":"N"}},excessiveTime:{type:"number",default:0,"x-component":"Radio.Group","x-reactions":expression(`field => {
                            field.visible = $values.isImmediately === 'N'
                          }`),"x-component-props":{style:{"margin-left":"20px"}},enum:[{label:i18nExpression("vendorMod.day30"),value:30},{label:i18nExpression("vendorMod.day60"),value:60}]}}}}}}},fileInfo:{...FileInfo}})}}}}},addDialog:{type:"void",title:i18nExpression("black.impoblackCompanyAddrt"),"x-decorator":"QueryEngine","x-component":"RDialog","x-component-props":{"close-on-click-modal":!1,destroyOnClose:!0,size:"large",footerButtonList:expression(`(_, { cancelButton,okButton }) => {
        return [
          cancelButton,
          okButton
        ]

        }`),beforeClose:expression(`(done, type,closeLoading) => {
          if ( type === 'ok') {
            $addCompanyOneItem($form,$values,done,closeLoading)
          } else {
            done()
            }
          }
        `)},properties:{configForm:{type:"object","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:{companyCode:{type:"string",title:"{{$t('common.vendorCode')}}","x-decorator":"FormItem","x-component":"QuickSearchWrapper","x-reactions":expression(`field => {
                 field.visible = $form.query('state').get('data').isSrmCompany
               }`),"x-component-props":{showKey:"companyCode",propKey:"companyCode",name:"scc_sup_company_info2","@close-quicksearch":expression(`(val, scope) => {
                  $values.configForm.companyId = val ? val.companyId : null
                  $values.configForm.companyCode = val ? val.companyCode : ''
                  $values.configForm.companyName = val ? val.companyName : ''
                  $values.configForm.companyType = val ? val.companyType : ''
                  $values.configForm.socialCreditCode = val ? val.lcCode : ''
                  $values.configForm.legalPerson = val ? val.legalPerson : ''
                  $values.configForm.registeredCapital = val ? val.registeredCapital : ''
                  $values.configForm.companyCountry = val ? val.companyCountry : ''
                  $values.configForm.companyProvince = val ? val.companyProvince : ''
                  $values.configForm.companyCity = val ? val.companyCity : ''
                  $values.configForm.companyCreationDate = val ? val.companyCreationDate : ''

                }`)}},companyName:{type:"string","x-decorator":"FormItem",title:i18nExpression("common.vendorName"),"x-validator":{required:"true",message:i18nExpression("common.requiredField")}},socialCreditCode:{type:"string","x-decorator":"FormItem",title:"{{$t('vendorMod.socialCreditCode')}}","x-validator":{required:"true",message:i18nExpression("common.requiredField")}},legalPerson:{type:"string","x-decorator":"FormItem",title:"{{$t('vendorMod.corporateRepresentative')}}","x-validator":{required:"{{!$form.query('state').get('data').isSrmCompany}}",message:i18nExpression("common.requiredField")}},shareholder:{type:"string","x-decorator":"FormItem",title:i18nExpression("cusEntry.vendorMod.shareholder")},reason:{type:"string","x-decorator":"FormItem",title:i18nExpression("black.blackType"),"x-validator":{required:"true",message:i18nExpression("common.requiredField")}}}}}}});return{__sfc:!0,emitTabAdd,emitTabRemove,$t,app,vendor,$attrs,$closePageAndRefreshListPageData,$clearFormField,$addCompanyByImportExcel,$addCompanyOneItem,$saveBill,$submitData,$cancel,initButtonConfig,updateButtonConfig,scope,components,schema,RenderEngine}}});var _sfc_render=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{pageAttrs:_setup.$attrs,schema:_setup.schema,scope:_setup.scope,components:_setup.components,schemaKey:"BlackDetail"}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const edit=__component__.exports;export{edit as default};
