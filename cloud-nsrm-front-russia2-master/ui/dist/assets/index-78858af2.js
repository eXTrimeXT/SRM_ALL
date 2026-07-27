import{N as NavTabs}from"./index-9a7f2446.js";import{ak as defineComponent,al as usePageHelper,aq as defineSchemas,ad as expression,ah as generateXindexInOrder,ae as i18nExpression,bS as exportExcelSegment,bT as queryFieldStatePropertyExpression,af as yearMonthDaySelectorSegment,ca as buttonListItemVisibleByPermission,ar as RenderEngine,n as normalizeComponent}from"./index-6b6051d8.js";import edit from"./edit-engine-6b390936.js";import"./file-dynamic-25a093c4.js";import"./file-dynamic-7fc2d358.js";import"./MainHeader-2d842985.js";/* empty css                                                                   */import"./file-dynamic.vue_vue_type_style_index_0_scoped_516fdfc3_lang-f2c1a82d.js";import"./tree-utils-7df6be59.js";import"./basicSetting-fc46a2d9.js";import"./BaseTableBind-b1f76fc9.js";import"./util-1e55288f.js";import"./index-4d512f00.js";/* empty css                                                                      */import"./fileApi-05bbbbcc.js";import"./util-d962b17f.js";const _sfc_main$1=defineComponent({__name:"list-engine",setup(__props){const{emitTabAdd,t:$t,app}=usePageHelper(),$addOne=()=>{$detailOne("add",{})},$detailOne=(type,row)=>{let tabName=type=="add"?"blackEdit":"blackEdit"+row.blackId;emitTabAdd({component:edit,params:{flag:type,row,tabName},title:type=="add"?$t("vendorMod.addDocument"):row.blackCode,name:tabName})},$editOne=row=>{$detailOne("edit",row)},$delete=($queryEngine,row,$message)=>{$queryEngine.request.delete([row.rescindId]).then(res=>{$message.success($t("common.successDelete")),$queryEngine.state.paginationManagement.refresh()})},schema=defineSchemas({BlackRescind:{type:"void","x-decorator":"QueryEngine","x-component":"el-container","x-component-props":{class:"flex-container",direction:"vertical"},"x-query-engine":{service:"sup",actions:{paginationQuery:{immediate:!0,transformRequest:expression(`(data, headers) => {
            data.query['*'] = {}
            return data
          }`),onSuccess:expression(`async (res) => {
            const queryTodoRes = await app.$api.base.flowAPI.queryTodo()
            let queryTodoList = queryTodoRes.data || []
            $form.values.table = res.data.map(item => {
              let obj = queryTodoList.find(todoItem => item.rescindId + '' === todoItem.businessId + '')
              return { ...item, isApprover: obj ? 'Y' : 'N' }
            })
          }`)}}},properties:{bus:{type:"void","x-component":"BusEvent","x-component-props":{eventName:"BlackRescind","@listener":expression(`() => {
            $queryEngine.state.paginationManagement.refresh()
          }`)}},query:{type:"object","x-query-engine-skip":!0,"x-component":"QueryFormByQueryEngine",properties:generateXindexInOrder({rescindCode:{type:"string",title:i18nExpression("vendorMod.relegation.receiptNum"),"x-query-engine-query-operator":"contains"},rescindName:{type:"string",title:i18nExpression("vendorMod.relegation.billName"),"x-query-engine-query-operator":"contains"},approveStatus:{type:"string",title:i18nExpression("vendorMod.relegation.documentStatus"),"x-component":"DictSelect","x-component-props":{code:"PJ_APPROVE_STATUS_TYPE"}}})},toolbar:{type:"void","x-component":"Space","x-component-props":{style:"margin-bottom:16px;"},properties:{add:{type:"void",title:"{{$t('common.addSecure')}}","x-component":"RButton","x-component-props":{type:"primary","@click":expression(`() => {
                $addOne()
              }`)}},exportExcel:{type:"void","x-component":"ExportExcel","x-component-props":{...exportExcelSegment,type:"default",pageUrl:"/api-sup/api-ql/BlackRescind/query",tableHeader:queryFieldStatePropertyExpression("BlackRescind.table","data.columns"),dictCodes:{approveStatus:"PJ_APPROVE_STATUS_TYPE"}}},tips:{type:"void","x-component":"div","x-component-props":{style:{display:"inline-block",color:"#D9001B"}},"x-content":i18nExpression("vendorMod.blackListTips3")}}},table:{type:"array","x-component":"RenderTable","x-component-props":{class:"table-view-vxe-table",style:"flex: 1",preColumns:"seq",openCustomTable:!0},properties:generateXindexInOrder({rescindId:{type:"number","x-hidden":!0,"x-query-engine-primary-key":!0},rescindCode:{type:"string","x-component":"TableButton","x-component-props":{type:"text","@click":expression(`({row}) => {
                let tab = {
                  component: edit,
                  params: {
                    flag: 'view',
                    row: row,
                    tabName: 'edit' + row.rescindCode || row.rescindId
                  },
                  title: row.rescindName,
                  name: 'edit' + row.rescindCode
                }
                emitTabAdd(tab)
              }`)},"x-render-table-column":{title:i18nExpression("vendorMod.relegation.receiptNum"),minWidth:120,customRender:!0}},rescindName:{type:"string","x-render-table-column":{title:i18nExpression("vendorMod.relegation.billName"),minWidth:120}},rescindContent:{type:"string","x-render-table-column":{title:i18nExpression("vendorMod.relegation.sketch"),minWidth:120}},approveStatus:{type:"string","x-component":"DictSelect","x-component-props":{code:"PJ_APPROVE_STATUS_TYPE"},"x-render-table-column":{title:"{{$t('vendorMod.relegation.documentStatus')}}",minWidth:100}},createdBy:{type:"string","x-render-table-column":{title:"{{$t('common.creator')}}",width:120}},creationDate:{title:"{{ $t('common.creationTime') }}",...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.creationDate, '{y}-{m}-{d}')
              }`)},"x-render-table-column":{width:150}},lastUpdateDate:{type:"string","x-query-engine-sort":"desc","x-hidden":!0,"x-query-engine-primary-key":!0},operation:{type:"void",title:"{{$t('common.operation')}}","x-component":"RenderTableButtonList","x-component-props":{max:2},"x-render-table-column":{fixed:"right",width:120},properties:{edit:{type:"void",title:"{{$t('common.edit')}}","x-reactions":expression(`(field) => {
                  const row = $table.getRowByIndex($self.index)
                  field.visible = ['DRAFT', 'REJECTED', 'WITHDRAW'].includes(row.approveStatus) && app.$store.getters.userInfo.userId == row.createdId
                }`),"x-component-props":{...buttonListItemVisibleByPermission("base:black:edit"),"@click":expression(`({row}) => {
                    let tab = {
                      component: edit,
                      params: {
                        flag: 'edit',
                        row: row,
                        tabName: 'edit' + row.rescindCode || row.rescindId
                      },
                      title: row.rescindName,
                      name: 'edit' + row.rescindCode
                    }
                    emitTabAdd(tab)
                  }`)}},delete:{type:"void",title:"{{$t('common.delete')}}","x-reactions":expression(`(field) => {
                  const row = $table.getRowByIndex($self.index)
                  field.visible = ['DRAFT'].includes(row.approveStatus) && app.$store.getters.userInfo.userId == row.createdId
                }`),"x-component-props":{popconfirm:{title:"{{$t('common.confirmDeleteRow')}}"},"@click":expression(`({row}) => {
                    $delete($queryEngine,row,$message)
                  }`)}},approve:{type:"void",title:i18nExpression("common.approve"),"x-reactions":expression(`(field) => {
                  const row = $table.getRowByIndex($self.index)
                  field.visible = ['SUBMITTED'].includes(row.approveStatus) && (app.$store.getters.userInfo.userId == row.createdId || row.isApprover == 'Y')
                }`),"x-component-props":{type:"text","@click":expression(`({ row }) => {
                    let tab = {
                      component: edit,
                      params: {
                        flag: 'view',
                        row: row,
                        tabName: 'view' + row.rescindCode || row.rescindId
                      },
                      title: row.rescindName,
                      name: 'view' + row.rescindCode
                    }
                    emitTabAdd(tab)
                  }`)}}}}})}}}});return{__sfc:!0,emitTabAdd,$t,app,$addOne,$detailOne,$editOne,$delete,schema,components:{},scope:{app,$addOne,$editOne,$delete,$detailOne,emitTabAdd,edit},RenderEngine}}});var _sfc_render$1=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{pageAttrs:_vm.$attrs,scope:_setup.scope,components:_setup.components,schemaKey:"BlackList",schema:_setup.schema}})},_sfc_staticRenderFns$1=[],__component__$1=normalizeComponent(_sfc_main$1,_sfc_render$1,_sfc_staticRenderFns$1,!1,null,null,null,null);const Black=__component__$1.exports,_sfc_main={name:"BlackSecure",components:{NavTabs},data(){return{activeTab:"blackSecure",tabs:[{title:()=>this.$t("route.blackSecure"),name:"blackSecure",component:Black,closable:!1}]}}};var _sfc_render=function(){var _vm=this,_c=_vm._self._c;return _c("NavTabs",{ref:"tabs",attrs:{"tabs-list":_vm.tabs,"cur-tab":_vm.activeTab}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const index=__component__.exports;export{index as default};
