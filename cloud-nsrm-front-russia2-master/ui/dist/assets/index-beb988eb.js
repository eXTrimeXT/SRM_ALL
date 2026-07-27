import{N as NavTabs}from"./index-9a7f2446.js";import{ak as defineComponent,al as usePageHelper,aq as defineSchemas,ad as expression,ah as generateXindexInOrder,ae as i18nExpression,ca as buttonListItemVisibleByPermission,af as yearMonthDaySelectorSegment,ar as RenderEngine,n as normalizeComponent}from"./index-6b6051d8.js";import endDetail from"./cooperationEndedDetail-ff6dc716.js";import"./black-6d4b8132.js";import"./uniqueId-f496b65a.js";const _sfc_main$1=defineComponent({__name:"cooperationEndedList",setup(__props){const{emitTabAdd,app}=usePageHelper(),schema=defineSchemas({OrgCatForm:{type:"void","x-decorator":"QueryEngine","x-query-engine":{service:"sup",actions:{paginationQuery:{immediate:!0,transformRequest:expression(`(data, headers) => {
            data.query['*'] = {}
            return data
          }`),onSuccess:expression(`async (res) => {
            const queryTodoRes = await app.$api.base.flowAPI.queryTodo()
            let queryTodoList = queryTodoRes.data || []
            $form.values.table = res.data.map(item => {
              let obj = queryTodoList.find(todoItem => item.orgCatFormId + '' === todoItem.businessId + '')
              return { ...item, isApprover: obj ? 'Y' : 'N' }
            })
          }`)}}},"x-component":"el-container","x-component-props":{class:"flex-container the_contractTemplateList_wrapper",direction:"vertical"},properties:{bus:{type:"void","x-component":"BusEvent","x-component-props":{eventName:"cooperationEnd","@listener":expression(`() => {
            $queryEngine.state.paginationManagement.refresh()
          }`)}},query:{type:"object","x-query-engine-skip":!0,"x-component":"QueryFormByQueryEngine",properties:generateXindexInOrder({orgCatFormNumber:{type:"string",title:i18nExpression("vendorMod.controlNumber"),"x-query-engine-query-operator":"contains"},vendorId:{type:"string",title:i18nExpression("common.vendorName"),"x-component":"QuickSearchWrapper","x-component-props":{showKey:"companyName",propKey:"companyId",name:"scc_sup_company_info_all"}},approveStatus:{type:"string",title:i18nExpression("common.status"),"x-component":"DictSelect","x-component-props":{code:"PJ_APPROVE_STATUS_TYPE"}},supplierControlType:{type:"string",title:i18nExpression("vendorMod.controlType"),"x-component":"DictSelect","x-component-props":{code:"SUPPLIER_CONTROL_TYPE2"}}})},toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void",title:i18nExpression("common.add"),"x-component-props":{type:"primary",...buttonListItemVisibleByPermission("sup:cooperationEndList:add"),"@click":expression(`() => {
                let tab = {
                  component: endDetail,
                  params: {
                    flag: 'add',
                    tabName: 'endDetail'
                  },
                  title: $t('vendorMod.addSite'),
                  name: 'endDetail'
                }
                emitTabAdd(tab)
              }`)}}}},table:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",class:"table-view-vxe-table",openCustomTable:!0},properties:generateXindexInOrder({approveStatus:{type:"string",title:i18nExpression("common.status"),"x-component":"DictSelect","x-component-props":{code:"PJ_APPROVE_STATUS_TYPE"},"x-render-table-column":{width:120}},vendorId:{type:"string","x-hidden":!0,"x-render-table-column":{width:120}},vendorCode:{type:"string",title:i18nExpression("common.vendorCode"),"x-render-table-column":{width:120}},vendorName:{type:"string",title:i18nExpression("common.vendorName"),"x-render-table-column":{width:120}},orgCatFormId:{type:"string","x-hidden":!0,"x-render-table-column":{width:120}},orgCatFormNumber:{"x-component":"TableButton","x-component-props":{type:"text","@click":expression(`({row}) => {
                let orgCatFormId = row.orgCatFormId
                let tab = {
                  component: endDetail,
                  params: {
                    flag: 'view',
                    orderId: orgCatFormId,
                    tabName: 'CooperationEndedDetail' + row.vendorName,
                    row,
                  },
                  title: row.vendorName,
                  name: 'CooperationEndedDetail' + row.vendorName
                }
                emitTabAdd(tab)
              }`)},"x-render-table-column":{title:i18nExpression("vendorMod.controlNumber"),minWidth:180,customRender:!0}},supplierControlType:{type:"string",title:i18nExpression("vendorMod.controlType"),"x-component":"DictSelect","x-component-props":{code:"SUPPLIER_CONTROL_TYPE2"},"x-render-table-column":{width:120}},createdUserName:{type:"string",title:i18nExpression("common.creator"),"x-render-table-column":{width:200}},createdBy:{type:"string","x-hidden":!0},creationDate:{...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.lastUpdateDate, '{y}-{m}-{d}')
              }`)},title:i18nExpression("common.creationTime"),"x-render-table-column":{width:120}},lastUpdateDate:{type:"string","x-hidden":!0,"x-query-engine-sort":"desc","x-render-table-column":{width:120}},operation:{type:"void",title:i18nExpression("common.operation"),"x-render-table-column":{width:150,fixed:"right"},properties:{edit:{type:"void",title:i18nExpression("common.edit"),"x-component":"TableButton","x-reactions":expression(`(field) => {
                  const row = $table.getRowByIndex($self.index)
                  field.visible = ['DRAFT', 'REJECTED', 'WITHDRAW'].includes(row.approveStatus) && app.$store.getters.userInfo.userId == row.createdId
                }`),"x-component-props":{type:"text","@click":expression(`({ row }) => {
                    const orgCatFormId = row.orgCatFormId
                    const tab = {
                      component: endDetail,
                      params: {
                        flag: 'edit',
                        orderId: orgCatFormId,
                        tabName: 'CooperationEndedDetail' + row.vendorName,
                        row
                      },
                      title: row.vendorName,
                      name: 'CooperationEndedDetail' + row.vendorName
                    }
                    emitTabAdd(tab)
                  }`)}},delete:{type:"void",title:i18nExpression("common.delete"),"x-component":"TableButton","x-reactions":expression(`(field) => {
                  const row = $table.getRowByIndex($self.index)
                  field.visible = ['DRAFT'].includes(row.approveStatus) && app.$store.getters.userInfo.userId == row.createdId
                }`),"x-component-props":{style:"margin-left: 8px",showPopconfirm:!0,"@confirm":expression(`({ row }) => {
                    $queryEngine.request.delete(row.orgCatFormId).then(() => {
                      $message.success($t('common.successDelete'))
                      $queryEngine.state.paginationManagement.refresh()
                    })
                  }`)}},approve:{type:"void",title:i18nExpression("common.approve"),"x-component":"TableButton","x-reactions":expression(`(field) => {
                  const row = $table.getRowByIndex($self.index)
                  field.visible = ['SUBMITTED'].includes(row.approveStatus) && (app.$store.getters.userInfo.userId == row.createdId || row.isApprover == 'Y')
                }`),"x-component-props":{type:"text","@click":expression(`({ row }) => {
                    const orgCatFormId = row.orgCatFormId
                    const tab = {
                      component: endDetail,
                      params: {
                        flag: 'view',
                        orderId: orgCatFormId,
                        tabName: 'CooperationEndedDetail' + row.vendorName,
                        row
                      },
                      title: row.vendorName,
                      name: 'CooperationEndedDetail' + row.vendorName
                    }
                    emitTabAdd(tab)
                  }`)}}}}})}}}});return{__sfc:!0,emitTabAdd,app,schema,scope:{emitTabAdd,app,i18nExpression,endDetail},components:{},RenderEngine}}});var _sfc_render$1=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{staticClass:"contractPaymentType",attrs:{schemaKey:"cooperationList",schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns$1=[],__component__$1=normalizeComponent(_sfc_main$1,_sfc_render$1,_sfc_staticRenderFns$1,!1,null,null,null,null);const cooperationEndedList=__component__$1.exports,_sfc_main={name:"CooperationEnded",components:{NavTabs},data(){return{activeTab:"cooperationEndedList",tabs:[{title:()=>this.$t("cusEntry.vendorMod.cooperationEnded"),name:"cooperationEndedList",component:cooperationEndedList,closable:!1}]}}};var _sfc_render=function(){var _vm=this,_c=_vm._self._c;return _c("NavTabs",{ref:"tabs",attrs:{"tabs-list":_vm.tabs,"cur-tab":_vm.activeTab}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const index=__component__.exports;export{index as default};
