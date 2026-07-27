import{ah as generateXindexInOrder,cf as formGridSegment,ad as expression,ae as i18nExpression,af as yearMonthDaySelectorSegment,ak as defineComponent,al as usePageHelper,am as useAttrs,an as ref$1,bY as computed,ar as RenderEngine,aq as defineSchemas,n as normalizeComponent}from"./index-6b6051d8.js";import{s as supCommonApi}from"./supApi-98b2a23d.js";const DialogMainCategory={type:"void",title:"{{$t('dataConfMod.categoryDetails')}}","x-decorator":"QueryEngine","x-component":"RDialog","x-component-props":{class:"dialogMain",size:"middle",footer:!1},properties:{categoryList:{type:"array","x-component":"RenderTable","x-component-props":{style:"height:250px",preColumns:"seq",pagination:!1},"x-query-engine-skip":!0,properties:generateXindexInOrder({categoryCode:{type:"string",title:"{{$t('common.categoryCode')}}","x-component":"QuickSearchWrapper","x-component-props":{disabled:!0,showKey:"companyName",propKey:"companyName",name:"scc_base_purchase_category2"},"x-render-table-column":{minWidth:150,skipEditable:!0}},categoryName:{type:"string",title:"{{$t('common.categoryName')}}","x-render-table-column":{minWidth:120,skipEditable:!0}}})}}},formMain={type:"void",...formGridSegment,"x-reactions":expression(`() => {
    setTimeout(() => {
      const { datas } = $attrs.params
      $self.form.setValues(datas)
    })
  }`),properties:{planName:{type:"string",title:i18nExpression("vendorMod.planName2"),"x-decorator":"FormItem","x-component":"QuickSearchWrapper","x-component-props":{readPretty:"{{$form.readPretty}}",showKey:"planName",propKey:"planName","read-pretty":"{{$form.readPretty}}",name:"scc_sup_site_review_plan","@close-quicksearch":expression(`(val, scope) => {
              console.log(val)
              console.log($values)

              $values.siteReviewPlanId =  val?.siteReviewPlanId
              $values.vendorName = val ? val.vendorName : ''
              $values.orgName = val ? val.orgName : ''
              $values.categoryName = val ? val.categoryName : ''
              $values.planType = val ? val.planType : ''
              const id = val ? val.siteReviewPlanId : ''
              console.log($form.query('.visitingAddress').take())
              supCommonApi.findCategory(val.vendorId).then(res => {
                const data = res.data.companyInfo
                let obj = [{
                  addressDetail: data.companyAddress,
                  city: data.companyCity,
                  country: data.companyCountry,
                  province: data.companyProvince
                }]

                $form.query('.tableAddress').take().setValue(obj)
              })
          }`)},"x-validator":{required:!0}},vendorName:{type:"string",title:i18nExpression("vendorMod.vendorName"),"x-component-props":{disabled:!0},"x-decorator":"FormItem"},orgName:{type:"string",title:i18nExpression("vendorMod.orgName"),"x-component-props":{disabled:!0},"x-decorator":"FormItem"},planType:{type:"string",title:i18nExpression("vendorMod.planType"),"x-component":"DictSelect","x-component-props":{code:"planType",disabled:!0},"x-decorator":"FormItem"},planSetOutTime:{title:i18nExpression("vendorMod.planSetOutTime"),...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],disabled:"{{$form.readPretty}}"},"x-decorator":"FormItem"},planVisitTime:{title:i18nExpression("vendorMod.planVisitTime"),...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],disabled:"{{$form.readPretty}}"},"x-decorator":"FormItem"},visitDays:{type:"string",title:i18nExpression("vendorMod.visitDays"),"x-component-props":{disabled:"{{$form.readPretty}}"},"x-decorator":"FormItem"}}},collapseMain={type:"void","x-component":"Collapse",properties:generateXindexInOrder({workingGroupStaff:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.workingGroupStaff")},"x-query-engine-skip":!0,properties:{toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void","x-hidden":"{{$form.readPretty}}",title:i18nExpression("common.add"),"x-component-props":{type:"primary","@click":expression('({ rowIndex }) => $form.query(".tableStaff").take().componentProps.componentInstance.addRow("unshift")')}}}},tableStaff:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",class:"table-view-vxe-table",editMode:!0,pagination:!1,sortable:!1},"x-query-engine-skip":!0,"x-query-engine-relation":"siteReviewPlanConfirmPersons:*",properties:generateXindexInOrder({userAccount:{type:"string",title:i18nExpression("vendorMod.userAccount"),"x-render-table-column":{minWidth:100},"x-component":"QuickSearchWrapper","x-component-props":{"show-input":expression("$self.value"),"read-pretty":"{{$form.readPretty}}","show-key":"username",name:"scc_rbac_user_display","@close-quicksearch":expression(`(val, scope) => {
                    const row = $table.getRowByIndex($self.index)
                    row.userAccount = val ? val.username : ''
                    row.userName = val ? val.nickname : ''
                    row.userId = val ? val.userId : ''
                    row.userTel = val ? val.phone : ''
                    row.userEmail = val ? val.email : ''
                  }`)}},userName:{type:"string",title:i18nExpression("vendorMod.userName2"),"x-render-table-column":{minWidth:100},"x-component-props":{disabled:!0}},userTel:{type:"string",title:i18nExpression("vendorMod.mobilePhone"),"x-render-table-column":{minWidth:100},"x-component-props":{disabled:!0}},userEmail:{type:"string",title:i18nExpression("vendorMod.emailAddress"),"x-render-table-column":{minWidth:100},"x-component-props":{disabled:!0}},userPost:{type:"string",title:i18nExpression("bidMod.position"),"x-component-props":{disabled:"{{$form.readPretty}}"},"x-render-table-column":{minWidth:100}},onSiteFlag:{type:"string",title:i18nExpression("vendorMod.onSiteFlag"),"x-component":"Checkbox","x-component-props":{"true-label":"Y","false-label":"N",disabled:"{{$form.readPretty}}"},"x-render-table-column":{minWidth:100}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-hidden":"{{$form.readPretty}}","x-component-props":{type:"text","@click":expression(`({ row }) => {
                        $table.remove($self.index)
                      }`)}}}}})}}},visitingAddress:{type:"void","x-component":"CollapseItem","x-component-props":{title:expression('$t("vendorMod.visitingAddress")')},"x-query-engine-skip":!0,properties:{toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void","x-hidden":"{{$form.readPretty}}",title:i18nExpression("common.add"),"x-component-props":{type:"primary","@click":expression('({ rowIndex }) => $form.query(".tableAddress").take().componentProps.componentInstance.addRow("unshift")')}}}},tableAddress:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",class:"table-view-vxe-table",editMode:!0,pagination:!1,sortable:!1},"x-query-engine-skip":!0,"x-query-engine-relation":"siteReviewPlanConfirmAddress:*",properties:generateXindexInOrder({country:{type:"string",title:i18nExpression("components.address.country"),"x-component":"DictSelect","x-component-props":{disabled:"{{$form.readPretty}}",code:"country",filterable:!0,"@change-value":expression(`(_, node) => {
                    const row = $table.getRowByIndex($self.index)
                    // 选择国外就清理省市区，并且禁用
                      if (row.country !== 'CN') {
                        row.province = null
                        row.city = null
                      }
                  }`)},"x-render-table-column":{minWidth:100}},province:{type:"string",title:i18nExpression("components.address.area"),"x-component":"DictSelect","x-component-props":{code:"PROVINCE","custom-select-type":"PROVINCE",disabled:"{{ $table.getRowByIndex($self.index).country !== 'CN' ||  $form.readPretty}}"},"x-render-table-column":{minWidth:100}},city:{type:"string",title:i18nExpression("components.address.city"),"x-component":"DictSelect","x-component-props":{code:"{{ $table.getRowByIndex($self.index).province }}","custom-select-type":"CITY",disabled:"{{ $table.getRowByIndex($self.index).country !== 'CN' ||  $form.readPretty}}"},"x-render-table-column":{minWidth:100}},addressDetail:{type:"string",title:i18nExpression("components.address.detailAddress"),"x-component-props":{disabled:"{{$form.readPretty}}"},"x-render-table-column":{minWidth:100}},postCode:{type:"string",title:i18nExpression("components.address.postalCode"),"x-component-props":{disabled:"{{$form.readPretty}}"},"x-render-table-column":{minWidth:100}},siteComment:{type:"string",title:i18nExpression("components.address.remark"),"x-component-props":{disabled:"{{$form.readPretty}}"},"x-render-table-column":{minWidth:100}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void","x-hidden":"{{$form.readPretty}}",title:"{{$t('common.delete')}}","x-component-props":{type:"text","@click":expression(`({ row }) => {
                        $table.remove($self.index)
                      }`)}}}}})}}}})},_sfc_main=defineComponent({__name:"edit-engine",setup(__props){const{app,emitTabRemove,t,vendor}=usePageHelper(),attrs=useAttrs(),workflowStatus=ref$1("DRAFT"),$disabledFlag=computed(()=>!0),customUpdateButton=computed(()=>!$disabledFlag.value&&["SUPPLIER_SUBMITTED"].includes(workflowStatus.value)),viewUpdateButton=()=>{const flag=attrs.params.flag,readOnly=attrs.params.readOnly;return flag=="approve"||!readOnly},disabledUpdateButton=()=>!attrs.params.readOnly,initButtonConfig=$form=>{setTimeout(()=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.buttonConfigInfo.save.view=viewUpdateButton(),componentInstance.buttonConfigInfo.submit.view=viewUpdateButton(),componentInstance.buttonConfigInfo.cancel.view=!0,componentInstance.buttonConfigInfo.close.view=!1;const approveStatus=attrs.params.row?.approveStatus||null;[null,"DRAFT"].includes(approveStatus)&&(componentInstance.buttonConfigInfo.save.name="暂存",componentInstance.buttonConfigInfo.submit.name="提交")},50)},updateButtonConfig=$form=>{setTimeout(()=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.buttonConfigInfo.save.view=disabledUpdateButton(),componentInstance.buttonConfigInfo.submit.view=disabledUpdateButton(),componentInstance.buttonConfigInfo.cancel.view=!1,componentInstance.buttonConfigInfo.close.view=!1},50)},schema=defineSchemas({SiteReviewPlanConfirm:{type:"void","x-decorator":"QueryEngine","x-component":"el-container","x-component-props":{class:"flex-container siteReviewPlanConfirm",direction:"vertical"},"x-query-engine":{service:"sup",actions:{query:{immediate:!0,ready:expression(`() => {
            initButtonConfig($form)
            return $attrs.params && ($attrs.params.row.planConfirmId || $attrs.params.row.planConfirmCode)
          }`),autoFormatResult:!1,transformRequest:expression(`(data, headers) => {
            data.query = {
              "*":{},
              "siteReviewPlanConfirmAddress": {'*': {}},
              "siteReviewPlanConfirmPersons": {'*': {}},
            }
            let req = {
              "filter": {
                  "planConfirmId": {
                      eq: $attrs.params.row.planConfirmId
                  }
              }
            }
            if (!$attrs.params.row.planConfirmId) {
              req = {
                "filter": {
                  "planConfirmCode": {
                      eq: $attrs.params.row.planConfirmCode
                  }
                }
              }
            }
            data.payload = req
            return data
          }`),transformResponse:expression(`(res) => {
            const ress = JSON.parse(res)
            const { readOnly } = $attrs.params

            const row = $attrs.params.row
            console.log(row, 'row')
            if (row) {
              $form.query('.vendorName').take().setValue(row.vendorName)
              $form.query('.orgName').take().setValue(row.orgName)
              $form.query('.planType').take().setValue(row.planType)
            }

            let datas = ress.data.ref.SiteReviewPlanConfirm[ress.data.records[0]]
            // 设置文本只读
            $form.readPretty = readOnly || ['VENDOR_CONFIRMED'].includes(datas.approveStatus)
            const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
            const tabDisabled = !['ABANDONED', 'REJECT', 'PASS', 'VENDOR_CONFIRMED'].includes(datas.approveStatus)
            componentInstance.setWorkflowTabDisabled(tabDisabled)
            let tableAddress = []
            let tableStaff = []
            const addressList = datas.siteReviewPlanConfirmAddress
            const personsList = datas.siteReviewPlanConfirmPersons
            addressList.forEach((e) => {
              tableAddress.push(ress.data.ref.SiteReviewPlanConfirmAddress[e])
            })
            personsList.forEach((e) => {
              tableStaff.push(ress.data.ref.SiteReviewPlanConfirmPerson[e])
            })
            $form.setValues(datas)
            $form.query('.tableAddress').take().setValue(tableAddress)
            $form.query('.tableStaff').take().setValue(tableStaff)
            return ress
          }`)}}},properties:{SchemaWorkflow:{type:"void","x-component":"SchemaWorkflow","x-component-props":{"business-id":expression("$attrs.params.row?.planConfirmId || null"),"business-type":"PLANCONFIRM","@click-handler":expression(`(type) => {
            $submits(type, $form, $queryEngine, $message, $t, $bus)
          }`),"@submit-direct":expression(`(type) => {
            $submits(type, $form, $queryEngine, $message, $t, $bus)
          }`),"@confirm":expression(`(type, comment) => {
            $submits(type, $form, $queryEngine, $message, $t, $bus)
          }`),"@close-tab":expression(`() => {
            $back($bus)
          }`),"@update-integration-mode":expression(`(integrationMode) => {
            console.log('update-integration-mode', integrationMode)
            if (integrationMode.integrationMode == "None") {
              updateButtonConfig($form)
            }
          }`)},properties:{layout:{type:"void","x-component":"FormContainer",properties:{form:{...formMain},layout:{type:"void","x-component":"FormContainer",properties:{collapse:{...collapseMain}}}}}}}}}}),$back=$bus=>{emitTabRemove(attrs.tabName),$bus.$emit("ModelHead")},$submits=(type,$form,$queryEngine,$message,$t,$bus)=>{let values=$form.values;const tableAddress=$form.query(".tableAddress").get("value"),tableStaff=$form.query(".tableStaff").get("value");values.siteReviewPlanConfirmAddress=tableAddress,values.siteReviewPlanConfirmPersons=tableStaff;const approveStatus=attrs.params.row?.approveStatus||null;type=="SAVE"?[null,"DRAFT"].includes(approveStatus)?(values.approveStatus="DRAFT",$queryEngine.request.save(values,{query:{"*":{}}}).then(()=>{$message.success($t("common.successSave")),$bus.$emit("SiteReviewPlanConfirm"),emitTabRemove(attrs.tabName)})):$queryEngine.request.save(values,{query:{"*":{}}}).then(()=>{$message.success($t("common.successSave")),$bus.$emit("SiteReviewPlanConfirm"),emitTabRemove(attrs.tabName)}):[null,"DRAFT","RELEASED","VENDOR_REJECT"].includes(approveStatus)?(values.approveStatus="RELEASED",$queryEngine.request.save(values,{query:{"*":{}}}).then(()=>{$message.success($t("common.successSubmit")),$bus.$emit("SiteReviewPlanConfirm"),emitTabRemove(attrs.tabName)})):$queryEngine.request.save(values,{query:{"*":{}}}).then(res=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.setWorkflowBusinessId(res.data[0]?.planConfirmId||null),componentInstance.setWorkflowTabDisabled(!0),componentInstance.setWorkflowBusinessVariables({}),componentInstance.handlerAfter(type.toUpperCase(),()=>{$bus.$emit("SiteReviewPlanConfirm")})})};return{__sfc:!0,app,emitTabRemove,t,vendor,attrs,workflowStatus,$disabledFlag,customUpdateButton,viewUpdateButton,disabledUpdateButton,initButtonConfig,updateButtonConfig,schema,$back,$submits,scope:{app,t,$attrs:attrs,$disabledFlag,emitTabRemove,initButtonConfig,$back,supCommonApi,$submits},components:{},RenderEngine}}});var _sfc_render=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{schemaKey:"siteReviewPlanConfirmDetail",pageAttrs:_vm.$attrs,schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const sitereviewplanconfirmEdit=__component__.exports;export{DialogMainCategory as D,sitereviewplanconfirmEdit as s};
