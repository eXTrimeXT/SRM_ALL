import{N as NavTabs}from"./index-a035e78f.js";import{al as defineComponent,am as usePageHelper,ar as defineSchemas,ae as expression,ai as generateXindexInOrder,af as i18nExpression,bW as dataTimeSelectorSegment,ag as yearMonthDaySelectorSegment,bt as changeFieldVisibleByDeps,as as RenderEngine,n as normalizeComponent}from"./index-17d0ccd5.js";import{d as detail}from"./edit-c1534e20.js";import"./edit.vue_vue_type_style_index_0_lang-0314473d.js";import"./supApi-e5726083.js";import"./file-dynamic-30cdd411.js";import"./file-dynamic-ab2ff377.js";import"./MainHeader-ef959a5c.js";/* empty css                                                                   */import"./file-dynamic.vue_vue_type_style_index_0_scoped_516fdfc3_lang-e128b539.js";import"./tree-utils-7df6be59.js";import"./basicSetting-f3b18103.js";import"./BaseTableBind-53264a4f.js";import"./util-6482eb24.js";import"./index-4d512f00.js";/* empty css                                                                      */import"./fileApi-3f0be128.js";import"./vendorManagement-89a77d38.js";import"./util-a92f9f8e.js";const _sfc_main$1=defineComponent({__name:"list",setup(__props){const{emitTabAdd,app}=usePageHelper(),schema=defineSchemas({InfoChange:{type:"void","x-decorator":"QueryEngine","x-query-engine":{service:"sup",actions:{queryInfoChangePage:{method:"paginationQuery"}}},"x-component":"el-container","x-component-props":{class:"flex-container the_contractTemplateList_wrapper",direction:"vertical"},properties:{bus:{type:"void","x-component":"BusEvent","x-component-props":{eventName:"vendorInfoChange","@listener":expression(`() => {
            $queryEngine.state.paginationManagement.refresh()
          }`)}},query:{type:"object","x-query-engine-skip":!0,"x-component":"QueryFormByQueryEngine",properties:generateXindexInOrder({changeApplyNo:{type:"string",title:i18nExpression("vendorMod.changeApplyNo"),"x-query-engine-query-operator":"contains"},companyName:{type:"string",title:i18nExpression("common.vendorName"),"x-component":"QuickSearchWrapper","x-component-props":{showKey:"companyName",propKey:"companyName",name:"scc_sup_company_info_display_buyer"},"x-query-engine-relation":"companyInfoChange","x-query-engine-relation-strict":!0},changeStatus:{type:"string",title:i18nExpression("vendorMod.changeStatus"),"x-component":"DictSelect","x-component-props":{code:"INFO_CHANGE_STATUS"},default:"VENDOR_SUBMITTED"},creationDate:{title:i18nExpression("common.creationTime"),...dataTimeSelectorSegment,"x-query-engine-query-operator":"between"},legalPerson:{type:"string",title:i18nExpression("vendorMod.legalPerson"),"x-query-engine-query-operator":"contains","x-query-engine-relation":"companyInfoChange","x-query-engine-relation-strict":!0},lcCode:{type:"string",title:i18nExpression("vendorMod.lcCode"),"x-query-engine-query-operator":"contains","x-query-engine-relation":"companyInfoChange","x-query-engine-relation-strict":!0}})},toolbar:{type:"void","x-component":"Space","x-component-props":{style:"margin-bottom: 16px"},properties:{add:{type:"void",title:i18nExpression("common.add"),"x-component":"RButton","x-component-props":{type:"primary","@click":expression(`() => {
               const tab = {
                  component: detail,
                  params: {
                    flag: 'add',
                    tabName: 'detail'
                  },
                  title: $t('cusEntry.vendorMod.addInfoChange'), // '新增供应商',
                  name: 'detail'
                }
               emitTabAdd(tab)
              }`)}}}},table:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",class:"table-view-vxe-table",openCustomTable:!0},"x-reactions":expression(`() => {
            $effect(() => {
              $queryEngine.state.paginationManagement.queryParams.value = {
              changeStatus: { eq: 'VENDOR_SUBMITTED' },
              }
              $queryEngine.state.paginationManagement.refresh()  
            })
        }`),properties:generateXindexInOrder({changeId:{type:"string","x-hidden":!0},changeStatus:{type:"string",title:i18nExpression("vendorMod.changeStatus"),"x-component":"DictSelect","x-component-props":{code:"INFO_CHANGE_STATUS"},"x-render-table-column":{width:100}},changeApplyNo:{"x-component":"TableButton","x-component-props":{type:"text","@click":expression(`({row}) => {
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
              }`)},"x-render-table-column":{title:i18nExpression("vendorMod.changeApplyNo"),minWidth:150,customRender:!0}},companyCode:{type:"string",title:i18nExpression("common.vendorCode"),"x-query-engine-relation":"companyInfoChange","x-render-table-column":{width:120}},companyName:{type:"string",title:i18nExpression("common.vendorName"),"x-query-engine-relation":"companyInfoChange","x-render-table-column":{width:150}},overseasRelation:{type:"string",title:i18nExpression("vendorMod.overseasRelation"),"x-query-engine-relation":"companyInfoChange","x-component":"DictSelect","x-component-props":{code:"RELATION_NEW"},"x-render-table-column":{width:150}},lcCode:{type:"string",title:i18nExpression("cusEntry.vendorMod.lcCode"),"x-query-engine-relation":"companyInfoChange","x-render-table-column":{width:180}},legalPerson:{type:"string",title:i18nExpression("cusEntry.vendorMod.legalPerson"),"x-query-engine-relation":"companyInfoChange","x-render-table-column":{width:160}},lastUpdateDate:{title:i18nExpression("vendorMod.changeApprovedDate"),...yearMonthDaySelectorSegment,"x-query-engine-sort":"desc","x-render-table-column":{width:130}},createdFullName:{type:"string",title:i18nExpression("common.creator"),"x-render-table-column":{width:120}},creationDate:{title:i18nExpression("common.creationTime"),...yearMonthDaySelectorSegment,"x-render-table-column":{width:130}},operation:{type:"void",title:i18nExpression("common.operation"),"x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList","x-component-props":{max:2},properties:{edit:{type:"void",title:i18nExpression("common.edit"),"x-reactions":changeFieldVisibleByDeps([".changeStatus",".userType"],`((app.$store.getters.userType == $deps[1] || $deps[1] == null) && ['DRAFT'].includes($deps[0])) ||
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
                  }`)}}}}})}}}});return{__sfc:!0,emitTabAdd,app,schema,scope:{emitTabAdd,app,i18nExpression,detail},components:{},RenderEngine}}});var _sfc_render$1=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{staticClass:"contractPaymentType",attrs:{schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns$1=[],__component__$1=normalizeComponent(_sfc_main$1,_sfc_render$1,_sfc_staticRenderFns$1,!1,null,null,null,null);const vendorInfoChangeList=__component__$1.exports,_sfc_main={name:"VendorInfoChange",components:{NavTabs},data(){return{activeTab:"vendorInfoChangeList",tabs:[{title:()=>this.$t("vendorMod.vendorInfoChange"),name:"vendorInfoChangeList",component:vendorInfoChangeList,closable:!1}]}}};var _sfc_render=function(){var _vm=this,_c=_vm._self._c;return _c("NavTabs",{ref:"tabs",attrs:{"tabs-list":_vm.tabs,"cur-tab":_vm.activeTab}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const index=__component__.exports;export{index as default};
