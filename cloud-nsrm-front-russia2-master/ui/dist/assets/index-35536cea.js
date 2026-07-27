import{N as NavTabs}from"./index-9a7f2446.js";import{ak as defineComponent,al as usePageHelper,aq as defineSchemas,ad as expression,ah as generateXindexInOrder,ae as i18nExpression,ca as buttonListItemVisibleByPermission,bS as exportExcelSegment,bT as queryFieldStatePropertyExpression,af as yearMonthDaySelectorSegment,ar as RenderEngine,n as normalizeComponent}from"./index-6b6051d8.js";import{r as relationSuppliersDetail}from"./detail-372c31a6.js";const _sfc_main$1=defineComponent({__name:"list",setup(__props){const{emitTabAdd,emitTabRemove,t:$t,app,confirmDeleteMessage}=usePageHelper(),schema=defineSchemas({RelationSupBuyer:{type:"void","x-decorator":"el-container","x-decorator-props":{class:"flex-container",direction:"vertical"},"x-query-engine":{service:"sup",actions:{paginationQuery:{immediate:!0,transformRequest:expression(`(data, headers) => {
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
              }`)}},socialCreditCodeB:{type:"string","x-render-table-column":{title:i18nExpression("cusEntry.vendorMod.socialCreditCodeB"),minWidth:120}},vendorNameB:{type:"string","x-render-table-column":{title:i18nExpression("cusEntry.vendorMod.bCompanyName"),minWidth:120}},associationRemark:{type:"string","x-render-table-column":{title:i18nExpression("cusEntry.vendorMod.relationRemark"),minWidth:120}},createdUserName:{type:"string","x-render-table-column":{title:i18nExpression("common.creator"),minWidth:120}},creationDate:{...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.creationDate, '{y}-{m}-{d}')
              }`)},"x-render-table-column":{title:i18nExpression("common.creationTime"),minWidth:120}},lastUpdateDate:{type:"string","x-hidden":!0,"x-query-engine-sort":"desc"},operation:{type:"void","x-render-table-column":{title:i18nExpression("common.operation"),width:120,fixed:"right"},"x-component":"RenderTableButtonList","x-component-props":{max:2},properties:{edit:{type:"void",title:i18nExpression("common.edit"),"x-component-props":{...buttonListItemVisibleByPermission("sup:relationSuppliers:edit"),type:"text","@click":expression(`({ row }) => {
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
