import{N as NavTabs}from"./index-9a7f2446.js";import{n as normalizeComponent,cf as formGridSegment,ae as i18nExpression,ad as expression,ah as generateXindexInOrder,ak as defineComponent,al as usePageHelper,am as useAttrs,an as ref$1,aq as defineSchemas,bD as changeFieldVisibleByDeps,af as yearMonthDaySelectorSegment,ar as RenderEngine,au as observer,at as DictSelect,cx as FormTab,b$ as dataTimeSelectorSegment}from"./index-6b6051d8.js";import{s as supCommonApi}from"./supApi-98b2a23d.js";import{c as companyType,e as enterpriseThreeCertificates,a as companyBaseInfo,b as contactData,d as bankInfo,s as siteInfos,f as sceneAttachmentInfo,g as attachFile}from"./attachFile-7303fcd3.js";import{F as FileDynamic}from"./file-dynamic-25a093c4.js";import{v as vendorOptCommonApi}from"./index-4aa0cc9f.js";import"./file-dynamic-7fc2d358.js";import"./MainHeader-2d842985.js";/* empty css                                                                   */import"./file-dynamic.vue_vue_type_style_index_0_scoped_516fdfc3_lang-f2c1a82d.js";import"./tree-utils-7df6be59.js";import"./basicSetting-fc46a2d9.js";import"./BaseTableBind-b1f76fc9.js";import"./util-1e55288f.js";import"./index-4d512f00.js";/* empty css                                                                      */import"./fileApi-05bbbbcc.js";const _sfc_main$3={name:"changeTitle",props:{language:{type:String,default:()=>""}}};var _sfc_render$3=function(){var _vm=this,_c=_vm._self._c;return _c("div",{staticClass:"changeTitle changeTitleTop"},[_c("i"),_vm._v(_vm._s(_vm.$t(_vm.language))+" ")])},_sfc_staticRenderFns$3=[],__component__$3=normalizeComponent(_sfc_main$3,_sfc_render$3,_sfc_staticRenderFns$3,!1,null,"e4925666",null,null);const changeTitle=__component__$3.exports,formMain={type:"object","x-query-engine-skip":!0,...formGridSegment,properties:{changeId:{type:"number","x-hidden":!0,"x-decorator":"FormItem"},changeStatus:{type:"string","x-hidden":!0,"x-decorator":"FormItem"},changeApplyNo:{type:"string",title:i18nExpression("vendorMod.changeApplyNo"),"x-component-props":{disabled:!0},"x-decorator":"FormItem"},companyId:{type:"number","x-hidden":!0},companyCode:{type:"string","x-hidden":!0},companyName:{type:"string",title:i18nExpression("common.vendorName"),"x-component":"QuickSearchWrapper","x-component-props":{showKey:"companyName",name:"scc_sup_company_info_display_buyer",disabled:expression("!['',null,undefined].includes($attrs.params.row?.changeStatus || $form.values?.changeStatus)"),"read-pretty":"{{$form.readPretty}}","@close-quicksearch":expression(`(val) => {
          $form.query('.form.companyId').take().value = val ? val.companyId : null
          $form.query('.form.companyCode').take().value = val ? val.companyCode : ''
          console.log(val, 'val')
          if (val?.companyId) {
            fatchCompanyData(val.companyId, $form) // 查询公司信息
          }
        }`)},"x-decorator":"FormItem","x-validator":{required:!0,message:i18nExpression("common.requiredField")}},noticeById:{type:"string","x-hidden":!0,"x-decorator":"FormItem"},noticeByName:{type:"string",title:i18nExpression("vendorMod.noticeByName"),"x-hidden":expression("$buyer()"),"x-component-props":{disabled:!0},"x-decorator":"FormItem"},enable4mChange:{type:"string",title:i18nExpression("vendorMod.enable4MChange"),"x-decorator":"FormItem","x-component":"DictSelect","x-decorator-props":{tooltip:i18nExpression("vendorMod.tipsOf4M")},"x-component-props":{code:"YES_OR_NO"},"x-validator":{required:!0}},changeFileId:{type:"string","x-hidden":!0,"x-decorator":"FormItem"},changeFileName:{type:"string","x-decorator":"FormItem","x-hidden":!0},changeFile:{type:"string",title:i18nExpression("vendorMod.changeFile"),"x-component":"SrmCommonFile","x-component-props":{"extra-data":{uploadType:"DEF",sourceType:"WEB_APP",fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},"default-file":{fileId:expression("$form.query('.form.changeFileId').take()?.value"),fileName:expression("$form.query('.form.changeFileName').take()?.value")},"@on-change":expression(`(file) => {
          if (file) {
            const { fileId, fileName } = file.file || {}
            $form.query('.form.changeFileId').take().value = fileId.toString()
            $form.query('.form.changeFileName').take().value = fileName
          } else {
            $form.query('.form.changeFileId').take().value = null
            $form.query('.form.changeFileName').take().value = null
          }
        }`)},"x-decorator":"FormItem"},changeExplain:{type:"string",title:i18nExpression("vendorMod.changeExplain"),"x-component":"Input.TextArea","x-component-props":{autosize:{minRows:3,maxRows:4}},"x-decorator":"FormItem","x-decorator-props":{gridSpan:4},"x-validator":{required:!0,message:i18nExpression("common.requiredField")}}}},financeInfo={beforeChange:{type:"void","x-component":"div","x-component-props":{class:""},properties:{beforeChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.beforeChange"}},financeInfoBefore:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!1,maxHeight:400,pagination:!1,sortable:!1},"x-query-engine-skip":!0,properties:generateXindexInOrder({orgCode:{type:"string","x-hidden":!0},orgName:{type:"string",title:i18nExpression("vendorMod.ceeaOrgName2"),"x-render-table-column":{minWidth:120}},orgId:{type:"string",title:i18nExpression("vendorMod.ceeaOrgName2"),"x-render-table-column":{minWidth:120},"x-hidden":!0,"x-component":"Select","x-component-props":{disabled:!0,"@change":expression(`(val) => {
                  const orgCategorys = $form.query('.orgCategorys').take()?.value
                  let datas = []
                  orgCategorys?.forEach(resData => {
                    const objs = {
                      key:resData.orgId,
                      label:resData.orgName,
                      value:resData.orgId
                    }
                    datas.push(objs)
                  })
                  let dictItem = datas.find(i => i.orgId === val) || {}
                  let row = $table.getRowByIndex($self.index)
                  row.orgCode = dictItem.orgCode
                  row.orgName = dictItem.orgName
               }`)},"x-reactions":[expression(`(field) => {
                const orgCategorys = $form.query('.orgCategorys').take()?.value
                let datas = []
                orgCategorys?.forEach(resData => {
                  const objs = {
                    key:resData.orgId,
                    label:resData.orgName,
                    value:resData.orgId
                  }
                  datas.push(objs)
                })
                // 去重
                let attrId = []
                let attr = []
                datas?.forEach((dataE) => {
                  if (!attrId.includes(dataE.orgId)) {
                    attrId.push(dataE.orgId)
                    attr.push(dataE)
                  }
                })
                $self.dataSource = attr
              }`)],"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},factoryCode:{type:"string",title:i18nExpression("vendorMod.factoryCode"),"x-render-table-column":{minWidth:150},"x-component-props":{disabled:!0,maxlength:"50","show-word-limit":!0,"@onKeyUp":"value=value.replace(/[^w\\/]/ig,'')"}},clearCurrency:{type:"string",title:i18nExpression("vendorMod.clearCurrency"),"x-render-table-column":{minWidth:150},"x-component":"DictSelect","x-component-props":{code:"BID_TENDER_CURRENCY",disabled:!0},"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},paymentMethod:{type:"string",title:i18nExpression("vendorMod.paymentMethod"),"x-render-table-column":{minWidth:150},"x-component":"DictSelect","x-component-props":{code:"PAYMENT_METHOD",disabled:!0},"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},paymentTerms:{type:"string",title:i18nExpression("vendorMod.paymentTerms"),"x-render-table-column":{minWidth:150},"x-component":"DictSelect","x-component-props":{code:"PAYMENT_TERMS",disabled:!0},"x-validator":{required:!0,message:i18nExpression("common.requiredField")}}})}}},afterChange:{type:"void","x-component":"div","x-component-props":{class:""},properties:{afterChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.afterChange"}},toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void",title:i18nExpression("common.add"),"x-component-props":{type:"primary","@click":expression(`() => {
                 $self.query('financeInfoChanges')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
              }`)}}}},financeInfoChanges:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,maxHeight:400,pagination:!1,sortable:!1,primaryKey:"financeChangeId",cascadeDeletion:!0},"x-query-engine-skip":!0,properties:generateXindexInOrder({orgCode:{type:"string","x-hidden":!0},orgName:{type:"string","x-hidden":!0},orgId:{type:"string",title:i18nExpression("vendorMod.ceeaOrgName2"),"x-render-table-column":{minWidth:120},"x-component":"OrganizationSelector","x-reactions":expression(`() => {
                const oldData = $form.query('financeInfoBefore').get('value')[$self.index]?.orgId || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),"x-component-props":{nodeType:"OU",placeholder:i18nExpression("common.pleaseSelect"),"read-pretty":expression("$form.readPretty"),"@select":expression(`(val) => {
                  const row = $table.getRowByIndex($self.index)
                  row.orgId = val ? val.organizationId : null
                  row.orgCode = val ? val.organizationCode : ''
                  row.orgName = val ? val.organizationName : ''
              }`)},"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},factoryCode:{type:"string",title:i18nExpression("vendorMod.factoryCode"),"x-render-table-column":{minWidth:150},"x-reactions":expression(`() => {
                const oldData = $form.query('financeInfoBefore').get('value')[$self.index]?.factoryCode || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),"x-component-props":{disabled:expression("$form.readPretty"),maxlength:"50","show-word-limit":!0,"@onKeyUp":"value=value.replace(/[^w\\/]/ig,'')"}},clearCurrency:{type:"string",title:i18nExpression("vendorMod.clearCurrency"),"x-render-table-column":{minWidth:150},"x-component":"DictSelect","x-reactions":expression(`() => {
                const oldData = $form.query('financeInfoBefore').get('value')[$self.index]?.clearCurrency || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),"x-component-props":{code:"BID_TENDER_CURRENCY",disabled:expression("$form.readPretty")},"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},paymentMethod:{type:"string",title:i18nExpression("vendorMod.paymentMethod"),"x-render-table-column":{minWidth:150},"x-component":"DictSelect","x-reactions":expression(`() => {
                const oldData = $form.query('financeInfoBefore').get('value')[$self.index]?.paymentMethod || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),"x-component-props":{code:"PAYMENT_METHOD",disabled:expression("$form.readPretty")},"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},paymentTerms:{type:"string",title:i18nExpression("vendorMod.paymentTerms"),"x-render-table-column":{minWidth:150},"x-reactions":expression(`() => {
                const oldData = $form.query('financeInfoBefore').get('value')[$self.index]?.paymentTerms || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),"x-component":"DictSelect","x-component-props":{code:"PAYMENT_TERMS",disabled:expression("$form.readPretty")},"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{disabled:expression("$form.readPretty"),type:"text","@click":expression(`({ row }) => {
                      $table.remove($self.index)
                  }`)}}}}})}}}},tabs={tab1:{type:"void","x-component":"FormTab.TabPane","x-component-props":{ref:"companyType",label:i18nExpression("vendorMod.companyType")},properties:{div:{type:"void","x-component":"div","x-component-props":{class:"formClassWrap"},properties:{...companyType}}}},tab2:{type:"void","x-component":"FormTab.TabPane","x-component-props":{ref:"enterpriseThreeCertificates",label:i18nExpression("vendorMod.enterpriseThreeCertificates")},properties:{div:{type:"void","x-component":"div","x-component-props":{class:"formClassWrap"},properties:{...enterpriseThreeCertificates}}}},tab3:{type:"void","x-component":"FormTab.TabPane","x-component-props":{ref:"companyBaseInfo",label:i18nExpression("vendorMod.companyBaseInfo")},properties:{div:{type:"void","x-component":"div","x-component-props":{class:"formClassWrap"},properties:{...companyBaseInfo}}}},tab4:{type:"void","x-component":"FormTab.TabPane","x-component-props":{ref:"contactInfo",label:i18nExpression("vendorMod.contactInfo"),class:""},properties:{...contactData}},tab5:{type:"void","x-component":"FormTab.TabPane","x-component-props":{ref:"bankInfo",label:i18nExpression("vendorMod.bankInfo"),class:""},properties:{...bankInfo}},tab6:{type:"void","x-component":"FormTab.TabPane","x-component-props":{ref:"financeInfo",label:i18nExpression("vendorMod.financeInfo"),class:""},properties:{...financeInfo}},tab7:{type:"void","x-component":"FormTab.TabPane","x-component-props":{ref:"financeInfo",label:i18nExpression("vendorMod.vendorSiteInfos"),class:""},properties:{...siteInfos}},tab8:{type:"void","x-component":"FormTab.TabPane","x-component-props":{ref:"financeInfo",label:i18nExpression("vendorMod.sceneAttachmentInfo"),class:""},properties:{...sceneAttachmentInfo}},tab9:{type:"void","x-component":"FormTab.TabPane","x-component-props":{ref:"financeInfo",label:i18nExpression("vendorMod.otherAttachInfo"),class:""},properties:{...attachFile}}},_sfc_main$2=defineComponent({__name:"edit",setup(__props){const{app,emitTabRemove,t,vendor}=usePageHelper(),attrs=useAttrs(),workflowStatus=ref$1("DRAFT"),redFunction=(oldData,newData)=>{let className="";return oldData!=newData&&(className="redColorFont"),className},viewUpdateButton=$form=>{let bol;const changeStatus=attrs.params.row?.changeStatus||null;return changeStatus==="APPROVED"||changeStatus==="SUBMITTED"||changeStatus==="ABANDONED"||attrs.params?.flag==="view"?bol=!1:bol=!0,bol},initButtonConfig=$form=>{setTimeout(()=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance,changeStatus=attrs.params.row?.changeStatus||null;componentInstance.buttonConfigInfo.save.view=viewUpdateButton(),componentInstance.buttonConfigInfo.submit.view=viewUpdateButton()||changeStatus=="VENDOR_SUBMITTED",componentInstance.buttonConfigInfo.cancel.view=changeStatus!="VENDOR_SUBMITTED",componentInstance.buttonConfigInfo.close.view=!1;const approveStatus=attrs.params.row?.changeStatus||null;[null,"DRAFT"].includes(approveStatus)&&(componentInstance.buttonConfigInfo.save.name="暂存",componentInstance.buttonConfigInfo.submit.name="提交"),componentInstance.setWorkflowBusinessId(attrs.params.row?.changeId),componentInstance.setWorkflowTabDisabled(!["APPROVED","SUBMITTED","REJECTED","ABANDONED","VENDOR_SUBMITTED"].includes(attrs.params.row?.changeStatus)),componentInstance.setWorkflowBusinessVariables({})},50)},updateButtonConfig=$form=>{setTimeout(()=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.buttonConfigInfo.save.view=viewUpdateButton(),componentInstance.buttonConfigInfo.submit.view=viewUpdateButton(),componentInstance.buttonConfigInfo.cancel.view=!1,componentInstance.buttonConfigInfo.close.view=!1,componentInstance.setWorkflowBusinessId(attrs.params.row?.changeId),componentInstance.setWorkflowTabDisabled(!["APPROVED","SUBMITTED","REJECTED","ABANDONED"].includes(attrs.params.row?.changeStatus)),componentInstance.setWorkflowBusinessVariables({})},50)},schema=defineSchemas({state:{type:"void","x-component":"Fragment","x-hidden":!0,"x-data":{beforeChangeJson:null,companyChangeId:null}},InfoChange:{type:"void","x-decorator":"QueryEngine","x-component":"el-container","x-component-props":{class:"flex-container infoChange",direction:"vertical"},"x-query-engine":{service:"sup",actions:{query:{immediate:!0,tree:!0,ready:expression(`() => {
            initButtonConfig($form)
            return $attrs.params && $attrs.params?.changeId
          }`),autoFormatResult:!1,transformRequest:expression(`(data, headers) => {
            data.query = {
              "*":{},
              companyInfoChange: {'*': {}},
              contactInfoChanges: {'*': {}},
              bankInfoChanges: {'*': {}},
              siteInfoChanges: {'*': {}},
              financeInfoChanges: {'*': {}},
              managementAttachChanges: {'*': {}},
              fileuploadChanges: {'*': {}},
              operatingLogs: {'*': {}}
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

            setTimeout(() => {
              $form.query('.bankInfoAfter').take().value = datas.bankInfoChanges
              $form.query('.contactDataAfter').take().value = datas.contactInfoChanges
              $form.query('.financeInfoChanges').take().value = datas.financeInfoChanges
              $form.query('.siteInfosAfter').take().value = datas.siteInfoChanges
              $form.query('.sceneAttachmentInfoAfter').take().value = datas.managementAttachChanges
              $form.query('.attachFileAfter').take().value = datas.fileuploadChanges
              $form.query('.operatingLogsData').take().value = datas.operatingLogs

              $form.query('state').get('data').companyChangeId = datas?.companyInfoChange?.companyChangeId

              $form.query('.attachFileAfter').take(field => {
                  field.componentProps.componentInstance.reLoadFileInfo()
                })
              $form.query('.attachFileBefore').take(field => {
                  field.componentProps.componentInstance.reLoadFileInfo()
                })

              let datasForm = JSON.parse(JSON.stringify(datas))
              delete datasForm.bankInfoChanges
              delete datasForm.companyInfoChange
              delete datasForm.contactInfoChanges
              delete datasForm.financeInfoChanges
              delete datasForm.siteInfoChanges
              delete datasForm.managementAttachChanges
              delete datasForm.fileuploadChanges
              delete datasForm.beforeChangeJson
              $form.query('form').take().value = datasForm
              if ($attrs.params?.flag == 'view' || ['APPROVED', 'SUBMITTED', 'REJECTED', 'ABANDONED'].includes($attrs.params.row?.changeStatus)) {
                $form.readPretty = true
              }
              $form.query('state').get('data').beforeChangeJson = datas.beforeChangeJson

              const beforeChangeJson = JSON.parse(datas.beforeChangeJson)

              if (beforeChangeJson.companyInfo) {
                let companyInfo = beforeChangeJson.companyInfo
                $form.query('.companyTypeBefore').take().value = companyInfo

                $form.query('.enterpriseThreeCertificatesBefore').take().value = companyInfo

                $form.query('.companyBaseInfoBefore').take().value = companyInfo
              }

              $form.query('.siteInfosBefore').take().value = beforeChangeJson.siteInfos || []

              $form.query('.bankInfoBefore').take().value = beforeChangeJson.bankInfos || []

              $form.query('.sceneAttachmentInfoBefore').take().value = beforeChangeJson.managementAttaches || []

              $form.query('.contactDataBefore').take().value = beforeChangeJson.contactInfos || []

              $form.query('.financeInfoBefore').take().value = beforeChangeJson.financeInfos || []

                $form.query('.companyTypeAfter').take().value = datas?.companyInfoChange
                $form.query('.enterpriseThreeCertificatesAfter').take().value = datas.companyInfoChange
                $form.query('.companyBaseInfoAfter').take().value = datas.companyInfoChange

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
                      inputValidator: value => !(!value || value.length > 500),
                      inputErrorMessage: '驳回原因必填并且长度不能超过500字符！'
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
              `)}}},properties:{layout:{type:"void","x-component":"FormContainer","x-component-props":{class:"vendorInfoChange"},properties:{collapse:{type:"void","x-component":"Collapse",properties:generateXindexInOrder({vendorInfo:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.vendorInfo")},"x-query-engine-skip":!0,properties:{form:{...formMain}}}})},layout:{type:"void","x-component":"FormContainer",properties:{tabs:{type:"void","x-component":"FormTab","x-component-props":{type:"card",class:"changeTab",activeKey:"tab1"},properties:{...tabs}}}},collapse2:{type:"void","x-component":"Collapse",properties:generateXindexInOrder({operatingLogs:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("common.operationRecord")},"x-query-engine-skip":!0,properties:{operatingLogsData:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!1,maxHeight:400,pagination:!1,sortable:!1},"x-query-engine-skip":!0,properties:generateXindexInOrder({creationDate:{...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                                parseTime(row.creationDate, '{y}-{m}-{d}')
                              }`)},title:i18nExpression("common.operationTime"),"x-render-table-column":{minWidth:120}},operation:{type:"string",title:i18nExpression("contractMod.operationType"),"x-component":"DictSelect","x-component-props":{code:"OPERATING_TYPE"},"x-render-table-column":{minWidth:120}},reason:{type:"string",title:i18nExpression("common.explanationOfReasons"),"x-render-table-column":{minWidth:120}},createdFullName:{type:"string",title:i18nExpression("common.operator"),"x-render-table-column":{minWidth:120}}})}}}})}}}}}}}}),$back=$bus=>{emitTabRemove(attrs.tabName),$bus.$emit("ModelHead")},fatchCompanyData=(companyId,$form)=>{companyId&&vendorOptCommonApi.getCompanyForEdit({companyId}).then(res=>{if(res){if($form.query("state").get("data").beforeChangeJson=JSON.stringify(res.data),res.data.infoChange&&($form.query(".form.noticeByName").take().value=res.data.infoChange.noticeByName,$form.query(".form.noticeById").take().value=res.data.infoChange.noticeById),res.data.fileUploads){for(let i=0;i<res.data.fileUploads.length;i++){const fileItem=res.data.fileUploads[i];fileItem.originalBusinessId=fileItem.businessId,fileItem.sceneFileId=null,fileItem.businessId=null,fileItem.__edit_key__=!0,fileItem.__add_key__=!0,fileItem.__update_key__=!0}res.data.fileUploads,$form.query(".attachFileBefore").take().value=res.data.fileUploads,$form.query(".attachFileAfter").take().value=JSON.parse(JSON.stringify(res.data.fileUploads))}if(res.data.companyInfo){let companyInfo=res.data.companyInfo;$form.query(".companyTypeAfter").take().value=companyInfo,$form.query(".companyTypeBefore").take().value=JSON.parse(JSON.stringify(companyInfo)),$form.query(".enterpriseThreeCertificatesAfter").take().value=companyInfo,$form.query(".enterpriseThreeCertificatesBefore").take().value=JSON.parse(JSON.stringify(companyInfo)),$form.query(".companyBaseInfoAfter").take().value=companyInfo,$form.query(".companyBaseInfoBefore").take().value=JSON.parse(JSON.stringify(companyInfo))}$form.query(".siteInfosBefore").take().value=res.data.siteInfos||[],$form.query(".siteInfosAfter").take().value=JSON.parse(JSON.stringify(res.data.siteInfos))||[],$form.query(".bankInfoBefore").take().value=res.data.bankInfos||[],$form.query(".bankInfoAfter").take().value=JSON.parse(JSON.stringify(res.data.bankInfos))||[],$form.query(".sceneAttachmentInfoBefore").take().value=res.data.managementAttaches||[],$form.query(".sceneAttachmentInfoAfter").take().value=JSON.parse(JSON.stringify(res.data.managementAttaches))||[],$form.query(".contactDataBefore").take().value=res.data.contactInfos||[],$form.query(".contactDataAfter").take().value=JSON.parse(JSON.stringify(res.data.contactInfos))||[],$form.query(".financeInfoBefore").take().value=res.data.financeInfos||[],$form.query(".financeInfoChanges").take().value=JSON.parse(JSON.stringify(res.data.financeInfos))||[]}})},$submits=(type,$form,$queryEngine,$message,$t,$bus)=>{let values=$form.values;const companyTypeAfter=$form.query(".companyTypeAfter").get("value"),enterpriseThreeCertificatesAfter=$form.query(".enterpriseThreeCertificatesAfter").get("value");let userType=values.form?.userType;(values.form?.userType==null||values.form?.userType==null)&&(userType=app.$store.getters.userType);let allData={beforeChangeJson:$form.query("state").get("data").beforeChangeJson,...values.form,userType,companyInfoChange:Object.assign({companyChangeId:$form.query("state").get("data")?.companyChangeId},companyTypeAfter,enterpriseThreeCertificatesAfter),contactInfoChanges:$form.query(".contactDataAfter").get("value"),bankInfoChanges:$form.query(".bankInfoAfter").get("value"),siteInfoChanges:$form.query(".siteInfosAfter").get("value"),financeInfoChanges:$form.query(".financeInfoChanges").get("value"),managementAttachChanges:$form.query(".sceneAttachmentInfoAfter").get("value"),fileuploadChanges:$form.query(".attachFileAfter").get("value")};if(allData.changeId&&(allData.companyInfoChange.changeId=allData.changeId,allData.contactInfoChanges?.forEach(e=>{e.changeId=allData.changeId}),allData.fileuploadChanges?.forEach(e=>{e.businessId=allData.changeId}),allData.siteInfoChanges?.forEach(e=>{e.changeId=allData.changeId}),allData.bankInfoChanges?.forEach(e=>{e.changeId=allData.changeId}),allData.financeInfoChanges?.forEach(e=>{e.changeId=allData.changeId})),!allData.enable4mChange)return app.$message.error($t("vendorMod.msgSelect4M"));if(allData.companyInfoChange.registCurrency==""||allData.companyInfoChange.registCurrency==null)return app.$message.error("请输入三证信息的币种"),!1;let checkSiteInfoChangesOrgid=!1,checkSiteInfoChangesvendorSiteCode=!1,checkSiteInfoChangescountry=!1,checkSiteInfoChangesaddressDetail=!1;if(allData.siteInfoChanges&&allData.siteInfoChanges.forEach(item=>{item.orgId||(checkSiteInfoChangesOrgid=!0),item.vendorSiteCode||(checkSiteInfoChangesvendorSiteCode=!0),item.country||(checkSiteInfoChangescountry=!0),item.addressDetail||(checkSiteInfoChangesaddressDetail=!0)}),checkSiteInfoChangesOrgid)return app.$message({type:"warning",message:$t("vendorMod.msgWriteVOrg")}),!1;if(checkSiteInfoChangesvendorSiteCode)return app.$message({type:"warning",message:$t("vendorMod.msgWriteVAddressName")}),!1;if(checkSiteInfoChangescountry)return app.$message({type:"warning",message:$t("vendorMod.msgWriteVCountry")}),!1;if(checkSiteInfoChangesaddressDetail)return app.$message({type:"warning",message:$t("vendorMod.msgWriteVAddressDetail")}),!1;const changeStatus=attrs.params.row?.changeStatus||null;if(type=="SAVE")[null,"DRAFT"].includes(changeStatus)?(allData.changeStatus="DRAFT",$queryEngine.request.save(allData,{customizeAction:"saveTemporary",query:{"*":{}}}).then(()=>{$message.success($t("common.successSave")),$bus.$emit("vendorInfoChange"),emitTabRemove(attrs.tabName)})):$queryEngine.request.save(allData,{customizeAction:"saveTemporary",query:{"*":{}}}).then(()=>{$message.success($t("common.successSave")),$bus.$emit("vendorInfoChange"),emitTabRemove(attrs.tabName)});else if([null,"DRAFT","SUBMITTED"].includes(changeStatus)&&(allData.changeStatus="DRAFT"),changeStatus=="VENDOR_SUBMITTED"){const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.setWorkflowBusinessId(allData.changeId||null),componentInstance.setWorkflowTabDisabled(!0),componentInstance.setWorkflowBusinessVariables({}),componentInstance.handlerAfter(type.toUpperCase(),()=>{$bus.$emit("vendorInfoChange")})}else $queryEngine.request.save(allData,{customizeAction:"submit",query:{"*":{}}}).then(res=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.setWorkflowBusinessId(res.records[0]||null),componentInstance.setWorkflowTabDisabled(!0),componentInstance.setWorkflowBusinessVariables({}),componentInstance.handlerAfter(type.toUpperCase(),()=>{$bus.$emit("vendorInfoChange")})})};return{__sfc:!0,app,emitTabRemove,t,vendor,attrs,workflowStatus,redFunction,viewUpdateButton,initButtonConfig,updateButtonConfig,schema,$back,fatchCompanyData,$submits,scope:{app,t,$attrs:attrs,emitTabRemove,initButtonConfig,$back,supCommonApi,$submits,observer,DictSelect,fatchCompanyData,redFunction},components:{FormTab,changeTitle,FileDynamic},RenderEngine}}});var _sfc_render$2=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{pageAttrs:_vm.$attrs,schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns$2=[],__component__$2=normalizeComponent(_sfc_main$2,_sfc_render$2,_sfc_staticRenderFns$2,!1,null,null,null,null);const detail=__component__$2.exports,_sfc_main$1=defineComponent({__name:"list",setup(__props){const{emitTabAdd,app,createdUserIsCurrentUserByRow}=usePageHelper(),schema=defineSchemas({InfoChange:{type:"void","x-decorator":"QueryEngine","x-query-engine":{service:"sup",actions:{paginationQuery:{immediate:!0}}},"x-component":"el-container","x-component-props":{class:"flex-container the_contractTemplateList_wrapper",direction:"vertical"},properties:{bus:{type:"void","x-component":"BusEvent","x-component-props":{eventName:"vendorInfoChange","@listener":expression(`() => {
            $queryEngine.state.paginationManagement.refresh()
          }`)}},query:{type:"object","x-query-engine-skip":!0,"x-component":"QueryFormByQueryEngine",properties:generateXindexInOrder({changeApplyNo:{type:"string",title:i18nExpression("vendorMod.changeApplyNo"),"x-query-engine-query-operator":"contains"},companyName:{type:"string",title:i18nExpression("common.vendorName"),"x-component":"QuickSearchWrapper","x-component-props":{showKey:"companyName",propKey:"companyName",name:"scc_sup_company_info_display_buyer"},"x-query-engine-relation":"companyInfoChange","x-query-engine-relation-strict":!0},creationDate:{title:i18nExpression("common.creationTime"),...dataTimeSelectorSegment,"x-query-engine-query-operator":"between"},changeStatus:{type:"string",title:i18nExpression("vendorMod.changeStatus"),"x-component":"DictSelect","x-component-props":{code:"INFO_CHANGE_STATUS"}},legalPerson:{type:"string",title:i18nExpression("vendorMod.legalPerson"),"x-query-engine-query-operator":"contains","x-query-engine-relation":"companyInfoChange","x-query-engine-relation-strict":!0},lcCode:{type:"string",title:i18nExpression("vendorMod.lcCode"),"x-query-engine-query-operator":"contains","x-query-engine-relation":"companyInfoChange","x-query-engine-relation-strict":!0}})},toolbar:{type:"void","x-component":"Space","x-component-props":{style:"margin-bottom: 16px"},properties:{add:{type:"void",title:i18nExpression("common.add"),"x-component":"RButton","x-component-props":{type:"primary","@click":expression(`() => {
               const tab = {
                  component: detail,
                  params: {
                    flag: 'add',
                    tabName: 'detail'
                  },
                  title: $t('vendorMod.addVendor'), // '新增供应商',
                  name: 'detail'
                }
               emitTabAdd(tab)
              }`)}}}},table:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",class:"table-view-vxe-table",openCustomTable:!0},properties:generateXindexInOrder({changeId:{type:"string","x-hidden":!0},changeStatus:{type:"string",title:i18nExpression("vendorMod.changeStatus"),"x-component":"DictSelect","x-component-props":{code:"INFO_CHANGE_STATUS"},"x-render-table-column":{width:100}},changeApplyNo:{"x-component":"TableButton","x-component-props":{type:"text","@click":expression(`({row}) => {
                let changeId = row.changeId
                let tab = {
                  component: detail,
                  params: {
                    flag: 'view',
                    changeId,
                    tabName: 'detail' + row.companyName
                  },
                  title: row.companyName,
                  name: 'detail' + row.companyName
                }
                emitTabAdd(tab)
              }`)},"x-render-table-column":{title:i18nExpression("vendorMod.changeApplyNo"),minWidth:150,customRender:!0}},companyCode:{type:"string",title:i18nExpression("common.vendorCode"),"x-query-engine-relation":"companyInfoChange","x-render-table-column":{width:120}},companyName:{type:"string",title:i18nExpression("common.vendorName"),"x-query-engine-relation":"companyInfoChange","x-render-table-column":{width:150}},overseasRelation:{type:"string",title:i18nExpression("vendorMod.overseasRelation"),"x-query-engine-relation":"companyInfoChange","x-component":"DictSelect","x-component-props":{code:"RELATION_NEW"},"x-render-table-column":{width:150}},lcCode:{type:"string",title:i18nExpression("vendorMod.lcCode"),"x-query-engine-relation":"companyInfoChange","x-render-table-column":{width:160}},legalPerson:{type:"string",title:i18nExpression("vendorMod.legalPerson"),"x-query-engine-relation":"companyInfoChange","x-render-table-column":{width:120}},lastUpdateDate:{title:i18nExpression("vendorMod.changeApprovedDate"),...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.lastUpdateDate, '{y}-{m}-{d}')
              }`)},"x-query-engine-sort":"desc","x-render-table-column":{width:130}},createdFullName:{type:"string",title:i18nExpression("common.creator"),"x-render-table-column":{width:120}},createdBy:{type:"string","x-hidden":!0,"x-render-table-column":{title:i18nExpression("bidMod.bidingCreatedBy"),minWidth:120}},creationDate:{title:i18nExpression("common.creationTime"),...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.creationDate, '{y}-{m}-{d}')
              }`)},"x-render-table-column":{width:130}},userType:{type:"string",title:i18nExpression("创建人类型"),"x-component":"DictSelect","x-component-props":{code:"USER_TYPE"},"x-render-table-column":{width:130}},operation:{type:"void",title:i18nExpression("common.operation"),"x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList","x-component-props":{max:2},properties:{edit:{type:"void",title:i18nExpression("common.edit"),"x-reactions":changeFieldVisibleByDeps([".changeStatus",".userType"],`((app.$store.getters.userType == $deps[1] || $deps[1] == null) && ['DRAFT'].includes($deps[0])) ||
                      ($buyer() && ['REJECTED', 'WITHDRAW'].includes($deps[0])) ||
                      ($vendor() && ['VENDOR_WITHDRAW', 'VENDOR_REJECTED'].includes($deps[0]))`),"x-component-props":{type:"text","@click":expression(`({ row }) => {
                    let changeId = row.changeId
                    let tab = {
                      component: detail,
                      params: {
                        flag: 'edit',
                        changeId,
                        row,
                        tabName: 'detail' + row.companyName
                      },
                      title: row.companyName,
                      name: 'detail' + row.companyName
                    }
                    emitTabAdd(tab)
                  }`)}},delete:{type:"void",title:i18nExpression("common.delete"),"x-reactions":changeFieldVisibleByDeps([".changeStatus",".userType"],"((app.$store.getters.userType == $deps[1] || $deps[1] == null) && ['DRAFT'].includes($deps[0]))"),"x-component-props":{popconfirm:{title:i18nExpression("common.confirmDeleteRow")},"@click":expression(`({ row }) => {
                     $queryEngine.request.delete(row.changeId).then(() => {
                       $message.success($t('common.successDelete'))
                       $queryEngine.state.paginationManagement.refresh()
                     })
                  }`)}},doApproval:{type:"void",title:i18nExpression("vendorMod.doApproval"),"x-reactions":changeFieldVisibleByDeps([".changeStatus"],"$buyer() && ['SUBMITTED'].includes($deps[0])"),"x-component-props":{type:"text","@click":expression(`({ row }) => {
                    let changeId = row.changeId
                    let tab = {
                      component: detail,
                      params: {
                        flag: 'doApproval',
                        changeId,
                        row,
                        tabName: 'detail' + row.companyName
                      },
                      title: row.companyName,
                      name: 'detail' + row.companyName
                    }
                    emitTabAdd(tab)
                  }`)}},abandon:{type:"void",title:i18nExpression("common.abandon"),"x-reactions":changeFieldVisibleByDeps([".changeStatus"],"['WITHDRAW', 'REJECTED'].includes($deps[0])"),"x-component-props":{type:"text","@click":expression(`({ row }) => {
                    let changeId = row.changeId
                    let tab = {
                      component: detail,
                      params: {
                        flag: 'doApproval',
                        changeId,
                        row,
                        tabName: 'detail' + row.companyName
                      },
                      title: row.companyName,
                      name: 'detail' + row.companyName
                    }
                    emitTabAdd(tab)
                  }`)}},manage:{type:"void",title:i18nExpression("contractMod.manage"),"x-reactions":changeFieldVisibleByDeps([".changeStatus"],"$buyer() && ['VENDOR_SUBMITTED'].includes($deps[0])"),"x-component-props":{type:"text","@click":expression(`({ row }) => {
                    let changeId = row.changeId
                    let tab = {
                      component: detail,
                      params: {
                        flag: 'view',
                        changeId,
                        row,
                        tabName: 'detail' + row.companyName
                      },
                      title: row.companyName,
                      name: 'detail' + row.companyName
                    }
                    emitTabAdd(tab)
                  }`)}}}}})}}}});return{__sfc:!0,emitTabAdd,app,createdUserIsCurrentUserByRow,schema,scope:{emitTabAdd,app,i18nExpression,detail,$createdUserIsCurrentUserByRow:createdUserIsCurrentUserByRow},components:{},RenderEngine}}});var _sfc_render$1=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{staticClass:"contractPaymentType",attrs:{schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns$1=[],__component__$1=normalizeComponent(_sfc_main$1,_sfc_render$1,_sfc_staticRenderFns$1,!1,null,null,null,null);const vendorInfoChangeList=__component__$1.exports,_sfc_main={name:"VendorInfoChange",components:{NavTabs},data(){return{activeTab:"vendorInfoChangeList",tabs:[{title:()=>this.$t("vendorMod.vendorInfoChange"),name:"vendorInfoChangeList",component:vendorInfoChangeList,closable:!1}]}}};var _sfc_render=function(){var _vm=this,_c=_vm._self._c;return _c("NavTabs",{ref:"tabs",attrs:{"tabs-list":_vm.tabs,"cur-tab":_vm.activeTab}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const index=__component__.exports;export{index as default};
