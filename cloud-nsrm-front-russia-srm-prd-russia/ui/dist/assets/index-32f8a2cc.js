import{N as NavTabs}from"./index-a035e78f.js";import{al as defineComponent,am as usePageHelper,an as useAttrs,ar as defineSchemas,ae as expression,ai as generateXindexInOrder,af as i18nExpression,cc as formGridSegment,aC as requiredValidatorSegment,as as RenderEngine,n as normalizeComponent,c7 as buttonListItemVisibleByPermission,bM as exportExcelSegment,bN as queryFieldStatePropertyExpression}from"./index-17d0ccd5.js";const _sfc_main$2=defineComponent({__name:"detail",setup(__props){const{emitTabAdd,emitTabRemove,t:$t,app}=usePageHelper();let $attrs=useAttrs();const $saveBill=(type,$form,$queryEngine,$confirm,$message,$bus)=>{$form.validate().then(res=>{const{socialCreditCodeA,socialCreditCodeB}=$form.values;if(socialCreditCodeA===socialCreditCodeB)return $message.warning($t("cusEntry.tipMessage.aCompanyAndbCompanySame")),!1;const associationId=$form.query("RelationSupBuyer").get("data").associationId||null;$queryEngine.request.save({...$form.values,associationId}).then(res2=>{const{associationId:associationId2}=res2.data?.[0];$form.query("RelationSupBuyer").get("data").associationId=associationId2,type==="SAVE"?($message.success($t("common.successSave")),$queryEngine.request.query()):($message.success($t("common.successSubmit")),emitTabRemove($attrs.params.tabName),$bus.$emit("relation"))})})},initButtonConfig=$form=>{setTimeout(()=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance,viewUpdateButton=$form.query("RelationSupBuyer").get("data").viewUpdateButton;componentInstance.buttonConfigInfo.save.view=viewUpdateButton&&!$form.readPretty,componentInstance.buttonConfigInfo.submit.view=viewUpdateButton&&!$form.readPretty,componentInstance.buttonConfigInfo.save.name=app.$t("common.staging"),componentInstance.buttonConfigInfo.submit.name=app.$t("common.submit"),componentInstance.buttonConfigInfo.cancel.view=!1,componentInstance.buttonConfigInfo.close.view=viewUpdateButton},50)},updateButtonConfig=$form=>{setTimeout(()=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance,viewUpdateButton=$form.query("RelationSupBuyer").get("data").viewUpdateButton;componentInstance.buttonConfigInfo.save.view=viewUpdateButton&&!$form.readPretty,componentInstance.buttonConfigInfo.submit.view=viewUpdateButton&&!$form.readPretty,componentInstance.buttonConfigInfo.cancel.view=!1,componentInstance.buttonConfigInfo.close.view=viewUpdateButton,componentInstance.setWorkflowTabDisabled($form.query("RelationSupBuyer").get("data").orderStatus==="DRAFT")},50)},schema=defineSchemas({RelationSupBuyer:{type:"void","x-component":"el-container","x-decorator":"QueryEngine","x-component-props":{class:"flex-container",direction:"vertical"},"x-data":{viewUpdateButton:!0,associationId:null},"x-query-engine":{service:"sup",actions:{save:{action:"saveOrUpdate",cascadeDeletion:!0},query:{action:"getRelationSupById",immediate:!0,ready:expression(`() => {
            initButtonConfig($form)
            $form.readPretty = $attrs.params.flag === 'view'
            return $attrs.params?.row?.associationId || null
          }`),transformRequest:expression(`(data, headers) => {
            data.loading = true
            data.payload = [
              {
                "associationId": $attrs.params?.row?.associationId || $form.query('RelationSupBuyer').get('data').associationId
              }
            ]
            return data
          }`),onSuccess:expression(`res => {
            let detailData = res.data[0]
            $form.setValues({
              ...detailData
            })
          }`)}}},properties:{SchemaWorkflow:{type:"void","x-component":"SchemaWorkflow","x-component-props":{"business-id":expression("$attrs.params.row?.associationId || null"),"business-type":"relationSuppliers","button-custom":expression("{}"),"@click-handler":expression(`(type) => {
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus)
          }`),"@submit-direct":expression(`(type) => {
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus)
          }`),"@confirm":expression(`(type, comment) => {
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus)
          }`),"@close-tab":expression(`() => {
            emitTabRemove($attrs.tabName)
          }`),"@update-integration-mode":expression(`(integrationMode) => {
            updateButtonConfig($form)
          }`)},properties:{collapse:{type:"void","x-component":"Collapse",properties:generateXindexInOrder({baseInfo:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("cusEntry.vendorMod.relationSuppliers")},"x-query-engine-skip":!0,properties:{layoutOne:{type:"void","x-component":"FormLayout","x-decorator-props":{layout:"vertical"},...formGridSegment,properties:{vendorCodeA:{type:"string","x-decorator":"FormItem",title:i18nExpression("cusEntry.vendorMod.aCompanyCode"),"x-component":"QuickSearchWrapper","x-component-props":{readPretty:"{{$form.readPretty}}",showKey:"companyCode",propKey:"companyCode",name:"scc_sup_company_info2","@close-quicksearch":expression(`value => {
                            $form.values.vendorCodeA = value?.companyCode || ''
                            $form.values.vendorNameA = value?.companyName || ''
                            $form.values.vendorIdA = value?.companyId || ''
                            $form.values.socialCreditCodeA = value?.lcCode || ''
                          }`)}},vendorIdA:{type:"string","x-hidden":!0},socialCreditCodeA:{type:"string",title:i18nExpression("cusEntry.vendorMod.socialCreditCodeA"),"x-decorator":"FormItem",...requiredValidatorSegment},vendorNameA:{type:"string",title:i18nExpression("cusEntry.vendorMod.aCompanyName"),"x-decorator":"FormItem",...requiredValidatorSegment},createdUserName:{type:"string","x-decorator":"FormItem",title:i18nExpression("common.creator"),"x-component-props":{disabled:!0}},vendorCodeB:{type:"string","x-decorator":"FormItem",title:i18nExpression("cusEntry.vendorMod.bCompanyCode"),"x-component":"QuickSearchWrapper","x-component-props":{readPretty:"{{$form.readPretty}}",showKey:"companyCode",propKey:"companyCode",name:"scc_sup_company_info2","@close-quicksearch":expression(`value => {
                            $form.values.vendorCodeB = value?.companyCode || ''
                            $form.values.vendorNameB = value?.companyName || ''
                            $form.values.vendorIdB = value?.companyId || ''
                            $form.values.socialCreditCodeB = value?.lcCode || ''
                          }`)}},vendorIdB:{type:"string","x-hidden":!0},socialCreditCodeB:{type:"string",title:i18nExpression("cusEntry.vendorMod.socialCreditCodeB"),"x-decorator":"FormItem",...requiredValidatorSegment},vendorNameB:{type:"string",title:i18nExpression("cusEntry.vendorMod.bCompanyName"),"x-decorator":"FormItem",...requiredValidatorSegment},creationDate:{type:"string","x-decorator":"FormItem",title:i18nExpression("common.creationTime"),"x-component-props":{disabled:!0}}}},layoutTwo:{type:"void","x-component":"FormLayout","x-decorator-props":{layout:"vertical"},...formGridSegment,properties:{associationRemark:{type:"string",title:i18nExpression("cusEntry.vendorMod.relationRemark"),"x-decorator":"FormItem","x-component-props":{type:"textarea",autosize:{minRows:2,maxRows:3}},"x-decorator-props":{gridSpan:4}}}}}}})}}}}}});return{__sfc:!0,emitTabAdd,emitTabRemove,$t,app,$attrs,$saveBill,initButtonConfig,updateButtonConfig,schema,scope:{$attrs,$saveBill,emitTabRemove,initButtonConfig,updateButtonConfig},components:{},RenderEngine}}});var _sfc_render$2=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{schemaKey:"relationSuppliers",pageAttrs:_setup.$attrs,schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns$2=[],__component__$2=normalizeComponent(_sfc_main$2,_sfc_render$2,_sfc_staticRenderFns$2,!1,null,null,null,null);const relationSuppliersDetail=__component__$2.exports,_sfc_main$1=defineComponent({__name:"list",setup(__props){const{emitTabAdd,emitTabRemove,t:$t,app,confirmDeleteMessage}=usePageHelper(),schema=defineSchemas({RelationSupBuyer:{type:"void","x-decorator":"el-container","x-decorator-props":{class:"flex-container",direction:"vertical"},"x-query-engine":{service:"sup",actions:{paginationQuery:{immediate:!0,transformRequest:expression(`(data, headers) => {
            data.query = {
              '*': {}
            }
            return data
          }`)},delete:{action:"delRelationSup"}}},"x-component":"QueryEngine",properties:{bus:{type:"void","x-component":"BusEvent","x-component-props":{eventName:"relation","@listener":expression(`() => {
            $queryEngine.state.pagenationManagement.refresh()
          }`)}},query:{type:"object","x-component":"QueryFormByQueryEngine","x-query-engine-skip":!0,properties:generateXindexInOrder({vendorNameA:{type:"string",title:i18nExpression("cusEntry.vendorMod.aCompanyName"),"x-query-engine-query-operator":"contains"},vendorNameB:{type:"string",title:i18nExpression("cusEntry.vendorMod.bCompanyName"),"x-query-engine-query-operator":"contains"},createdId:{type:"string",title:i18nExpression("cusEntry.vendorMod.applyer"),"x-component":"QuickSearchWrapper","x-component-props":{name:"scc_rbac_user_display",showKey:"nickname",propKey:"userId"}},creationDate:{type:"string",title:i18nExpression("common.creationTime"),"x-query-engine-query-operator":"between","x-component":"DatePicker","x-component-props":{type:"daterange",valueFormat:"yyyy-MM-dd"}}})},toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void",title:i18nExpression("common.add"),"x-component":"Button","x-component-props":{type:"primary",...buttonListItemVisibleByPermission("sup:relationSuppliers:add"),"@click":expression(`() => {
                emitTabAdd({
                  component: relationSuppliersDetail,
                  name: 'relationSuppliersDetail',
                  params: {
                    flag: 'add',
                    tabName: 'relationSuppliersDetail'
                  },
                  title: $t('cusEntry.vendorMod.addRelationSuppilers')
                })
              }`)}},exportExcel:{type:"void","x-component":"ExportExcel","x-component-props":{...exportExcelSegment,type:"default",pageUrl:"/api-sup/api-ql/RelationSupBuyer/query",tableHeader:queryFieldStatePropertyExpression("RelationSupBuyer.table","data.columns"),dictCodes:{type:"RELATION_TYPE"}}}}},table:{type:"array","x-component":"RenderTable","x-component-props":{class:"table-view-vxe-table",style:"flex: 1",preColumns:"seq",openCustomTable:!0},properties:generateXindexInOrder({vendorCodeA:{type:"string","x-render-table-column":{title:i18nExpression("cusEntry.vendorMod.aCompanyCode"),minWidth:120,customRender:!0},"x-component":"TableButton","x-component-props":{type:"text","@click":expression(`({row}) => {
                emitTabAdd({
                  component: relationSuppliersDetail,
                  name: 'relationSuppliersDetail' + row.vendorCodeA,
                  params: {
                    flag: 'view',
                    row,
                    tabName: 'relationSuppliersDetail' + row.vendorCodeA
                  },
                  title: row.vendorCodeA
                })
              }`)}},socialCreditCodeA:{type:"string","x-render-table-column":{title:i18nExpression("cusEntry.vendorMod.socialCreditCodeA"),minWidth:120}},vendorNameA:{type:"string","x-render-table-column":{title:i18nExpression("cusEntry.vendorMod.aCompanyName"),minWidth:120}},vendorCodeB:{type:"string","x-render-table-column":{title:i18nExpression("cusEntry.vendorMod.bCompanyCode"),minWidth:120,customRender:!0},"x-component":"TableButton","x-component-props":{type:"text","@click":expression(`({row}) => {
                emitTabAdd({
                  component: relationSuppliersDetail,
                  name: 'relationSuppliersDetail' + row.vendorCodeB,
                  params: {
                    flag: 'view',
                    row,
                    tabName: 'relationSuppliersDetail' + row.vendorCodeB
                  },
                  title: row.vendorCodeB
                })
              }`)}},socialCreditCodeB:{type:"string","x-render-table-column":{title:i18nExpression("cusEntry.vendorMod.socialCreditCodeB"),minWidth:120}},vendorNameB:{type:"string","x-render-table-column":{title:i18nExpression("cusEntry.vendorMod.bCompanyName"),minWidth:120}},associationRemark:{type:"string","x-render-table-column":{title:i18nExpression("cusEntry.vendorMod.relationRemark"),minWidth:120}},createdUserName:{type:"string","x-render-table-column":{title:i18nExpression("common.creator"),minWidth:120}},creationDate:{type:"string","x-render-table-column":{title:i18nExpression("common.creationTime"),minWidth:120}},lastUpdateDate:{type:"string","x-hidden":!0,"x-query-engine-sort":"desc"},operation:{type:"void","x-render-table-column":{title:i18nExpression("common.operation"),width:120,fixed:"right"},"x-component":"RenderTableButtonList","x-component-props":{max:2},properties:{edit:{type:"void",title:i18nExpression("common.edit"),"x-component-props":{...buttonListItemVisibleByPermission("sup:relationSuppliers:edit"),type:"text","@click":expression(`({ row }) => {
                    emitTabAdd({
                      component: relationSuppliersDetail,
                      name: 'relationSuppliersDetail',
                      params: {
                        flag: 'edit',
                        row,
                        tabName: 'relationSuppliersDetail'
                      },
                      title: $t('cusEntry.vendorMod.addRelationSuppilers')
                    })
                  }`)}},delete:{type:"void",title:i18nExpression("common.delete"),"x-component-props":{...buttonListItemVisibleByPermission("sup:relationSuppliers:delete"),"@click":expression(`({ row }) => {
                    const Message = confirmDeleteMessage()
                    Message.then(res => {
                      $queryEngine.request.delete(row.associationId).then(() => {
                        $message.success($t('common.successDelete'))
                        $queryEngine.state.paginationManagement.refresh()
                      }).catch((e) => {
                        console.log(e)
                      })
                    }).catch(() => {})
                  }`)}}}}})}}}});return{__sfc:!0,emitTabAdd,emitTabRemove,$t,app,confirmDeleteMessage,schema,scope:{relationSuppliersDetail,emitTabAdd,$t,app,confirmDeleteMessage},components:{},RenderEngine}}});var _sfc_render$1=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{schemaKey:"relationSuppliers",scope:_setup.scope,schema:_setup.schema,components:_setup.components}})},_sfc_staticRenderFns$1=[],__component__$1=normalizeComponent(_sfc_main$1,_sfc_render$1,_sfc_staticRenderFns$1,!1,null,null,null,null);const relationSuppliersList=__component__$1.exports,_sfc_main={name:"RelationSuppliers",components:{NavTabs},data(){return{activeTab:"relationSuppliersList",tabs:[{title:()=>this.$t("cusEntry.route.relationSuppliers"),name:"relationSuppliersList",component:relationSuppliersList,closable:!1}]}}};var _sfc_render=function(){var _vm=this,_c=_vm._self._c;return _c("NavTabs",{ref:"tabs",attrs:{"tabs-list":_vm.tabs,"cur-tab":_vm.activeTab}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const index=__component__.exports;export{index as default};
