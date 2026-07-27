import{N as NavTabs}from"./index-9a7f2446.js";import{ak as defineComponent,al as usePageHelper,aq as defineSchemas,ad as expression,ah as generateXindexInOrder,ae as i18nExpression,b$ as dataTimeSelectorSegment,bS as exportExcelSegment,af as yearMonthDaySelectorSegment,ar as RenderEngine,cE as transformColumns,n as normalizeComponent}from"./index-6b6051d8.js";const _sfc_main$1=defineComponent({__name:"list",setup(__props){const{app,vendor}=usePageHelper(),scope={app,transformColumns,$vendor:vendor},schema=defineSchemas({StorageReturnVendor:{type:"void","x-component":"QueryEngine","x-decorator":"el-container","x-decorator-props":{class:"flex-container",direction:"vertical"},"x-query-engine":{service:"sup-ce",actions:{paginationQuery:{immediate:!0,transformRequest:expression(`(data, headers) => {  
            if($vendor()){
              data.payload.filter = {
                vendorId: {eq: app.$store.getters.userInfo.companyId},
                ...data.payload.filter
              }
            }     
            if(data.payload.filter?.dealDate?.between?.length){
              const between = data.payload.filter.dealDate.between
              data.payload.filter['$or'] = {
                receiveDate:{between},
                returnToSupplierDate:{between}
              }
              delete data.payload.filter.dealDate
            }
            data.payload.page['sort'] = 'warehousingReturnDetailId desc'
            return data
            }`),onSuccess:expression(`async (res) => {
            const materialCodes = res.data.map(item => item.itemCode) || []
            const resData = await app.$http({
              url: '/api-base/material/materialItem/ext/multilingual',
              method: 'POST',
              data: { materialCodes, language: app.$i18n.locale },
              loading: true
            })
            
            const list = res.data.map(item => {
              const data = resData.data.find(it => it.material === item.itemCode)
              return {
                ...item,
                materialNameShow: data?.materialName,
                handleDate: item.type === 'RECEIVE' ? item.receiveDate : item.returnToSupplierDate
              }
            })
            setTimeout(() => {
              $form.values.table = list
            })
          }`)}}},properties:{query:{type:"object","x-query-engine-skip":!0,"x-component":"QueryFormByQueryEngine",properties:generateXindexInOrder({type:{type:"string",title:i18nExpression("orderMod.transactionType"),"x-component":"DictSelect","x-component-props":{code:"WAREHOURING_RETURN_DETAIL"}},dealDate:{title:i18nExpression("warehousingAndReturnGoods.dealDate"),"x-query-engine-query-operator":"between",...dataTimeSelectorSegment},receiveOrderNo:{type:"string",title:i18nExpression("orderMod.receiveOrderNo"),"x-query-engine-query-operator":"contains"},orgId:{type:"string",title:i18nExpression("oneStopShopping.businessEntity"),"x-component":"OrganizationSelector","x-component-props":{"parent-id":-1,"node-type":"OU","select-type":"input",placeholder:i18nExpression("common.pleaseSelect"),multiple:!0,"@select":expression(`(node) => {
                if($form.values.query.organizationId){
                  $form.values.query.organizationId = null
                }
              }`)},"x-query-engine-query-operator":"in"},organizationId:{type:"string",title:i18nExpression("purchaseDemand.invOrg"),"x-component":"OrganizationSelector","x-component-props":{"node-type":"INV","select-type":"input",placeholder:i18nExpression("common.pleaseSelect"),multiple:!0,"parent-id":expression("$form.values.query.orgId?.length ? $form.values.query.orgId : -1")},"x-query-engine-query-operator":"in"},categoryName:{type:"string",title:i18nExpression("purchaseDemand.materialCateSub"),"x-component":"QuickSearchWrapper","x-component-props":{showKey:"categoryName",propKey:"categoryName",name:"scc_base_purchase_category"}},itemName:{type:"string",title:i18nExpression("orderMod.buyerOrderSynergy.materialName"),"x-component":"QuickSearchWrapper","x-component-props":{showKey:"materialName",propKey:"materialName",name:"scc_base_material_item"}},vendorId:{type:"string","x-hidden":"{{$vendor()}}",title:i18nExpression("orderMod.buyerOrderSynergy.vendorName"),"x-component":"QuickSearchWrapper","x-component-props":{showKey:"companyName",propKey:"companyId",name:"scc_sup_company_info_all"}},orderNumber:{type:"string",title:i18nExpression("orderMod.buyerOrderSynergy.orderNumber"),"x-query-engine-query-operator":"contains"},requirementHeadNum:{type:"string",title:i18nExpression("purchaseDemand.purRequisitionNum"),"x-query-engine-query-operator":"contains"}})},toolbar:{type:"void","x-component":"Space","x-component-props":{style:"margin-bottom: 16px"},properties:{exportExcel:{type:"void","x-component":"ExportExcel","x-component-props":{...exportExcelSegment,pageUrl:"/api-sup-ce/api-ql/StorageReturnVendor/query",dictCodes:{type:"WAREHOURING_RETURN_DETAIL",sourceData:"TRANSACTION_SOURCE"}},"x-reactions":expression(`(field) => {
              $form.query('StorageReturnVendor.table').take(fields => {
                let columns = fields?.data?.columns ?? []
                field.componentProps.tableHeader = transformColumns(columns,[{
                  targetFiled: 'materialNameShow',
                  field: 'itemName',
                  title: $t('purchaseDemand.itemName')
                }])
             })
            }`)}}},table:{type:"array","x-component":"RenderTable","x-component-props":{class:"table-view-vxe-table",style:"flex: 1",preColumns:"seq",openCustomTable:!0},properties:generateXindexInOrder({orgName:{type:"string","x-render-table-column":{title:i18nExpression("oneStopShopping.businessEntity"),minWidth:120}},organizationName:{type:"string","x-render-table-column":{title:i18nExpression("purchaseDemand.invOrg"),minWidth:120}},vendorCode:{type:"string","x-render-table-column":{title:i18nExpression("purchaseDemand.vendorCode"),minWidth:120}},vendorName:{type:"string","x-render-table-column":{title:i18nExpression("purchaseDemand.vendorName"),minWidth:120}},receiveOrderNo:{type:"string","x-render-table-column":{title:i18nExpression("cusEntry.orderMod.erpOrderNumber"),minWidth:120}},type:{type:"string","x-component":"DictSelect","x-component-props":{code:"WAREHOURING_RETURN_DETAIL"},"x-render-table-column":{title:i18nExpression("orderMod.transactionType"),minWidth:120}},sourceData:{type:"string","x-component":"DictSelect","x-component-props":{code:"TRANSACTION_SOURCE"},"x-render-table-column":{title:i18nExpression("orderMod.sourceData"),minWidth:120}},receiveOrderLineNo:{type:"string","x-render-table-column":{title:i18nExpression("cusEntry.supplement20250314.erpOrderLineNumber"),minWidth:120}},categoryName:{type:"string","x-render-table-column":{title:i18nExpression("purchaseDemand.materialCateSub"),minWidth:120}},itemCode:{type:"string","x-render-table-column":{title:i18nExpression("purchaseDemand.itemCode"),minWidth:120}},itemName:{type:"string","x-hidden":!0},materialNameShow:{type:"string","x-render-table-column":{title:i18nExpression("purchaseDemand.itemName"),minWidth:120},"x-query-engine-skip":!0},unit:{type:"string","x-render-table-column":{title:i18nExpression("purchaseDemand.unitCode"),minWidth:120}},receiveNum:{type:"string","x-render-table-column":{title:i18nExpression("orderMod.transactionsNumber"),minWidth:120}},requirementHeadNum:{type:"string","x-render-table-column":{title:i18nExpression("purchaseDemand.purRequisitionNum"),minWidth:120}},rowNum:{type:"string","x-render-table-column":{title:i18nExpression("purchaseDemand.rowNum"),minWidth:120}},orderNumber:{type:"string","x-render-table-column":{title:i18nExpression("purSettlementMod.orderNumber"),minWidth:120}},lineNum:{type:"string","x-render-table-column":{title:i18nExpression("orderMod.orderLineNum"),minWidth:120}},createdFullName:{type:"string","x-render-table-column":{title:i18nExpression("cusEntry.orderMod.handler"),minWidth:120}},returnToSupplierDate:{"x-hidden":!0,"x-render-table-column":{title:i18nExpression("orderMod.transactionDate"),minWidth:120}},receiveDate:{"x-hidden":!0,"x-render-table-column":{title:i18nExpression("orderMod.transactionDate"),minWidth:120}},handleDate:{...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.handleDate, '{y}-{m}-{d}')
              }`)},"x-render-table-column":{title:i18nExpression("orderMod.transactionDate"),minWidth:120},"x-query-engine-skip":!0}})}}}});return{__sfc:!0,app,vendor,scope,schema,RenderEngine}}});var _sfc_render$1=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{schema:_setup.schema,scope:_setup.scope,schemaKey:"StorageReturnVendor"}})},_sfc_staticRenderFns$1=[],__component__$1=normalizeComponent(_sfc_main$1,_sfc_render$1,_sfc_staticRenderFns$1,!1,null,null,null,null);const warehousReturnGoodsVendorList=__component__$1.exports,_sfc_main={name:"WarehousReturnGoodsVendor",components:{NavTabs},data(){return{activeTab:"warehousReturnGoodsVendorList",tabs:[{title:this.$t("route.warehousingAndReturnGoods"),name:"warehousReturnGoodsVendorList",component:warehousReturnGoodsVendorList,closable:!1}]}},activated(){this.currentTab==="warehousReturnGoodsVendorList"&&this.dolayout()}};var _sfc_render=function(){var _vm=this,_c=_vm._self._c;return _c("NavTabs",{ref:"tabs",attrs:{"tabs-list":_vm.tabs,"cur-tab":_vm.activeTab}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const index=__component__.exports;export{index as default};
