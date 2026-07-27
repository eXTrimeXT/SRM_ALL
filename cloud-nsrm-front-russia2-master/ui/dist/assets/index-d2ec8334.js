import{N as NavTabs}from"./index-9a7f2446.js";import{ak as defineComponent,al as usePageHelper,cl as onActivated,cm as bus,aq as defineSchemas,ad as expression,ah as generateXindexInOrder,ae as i18nExpression,b$ as dataTimeSelectorSegment,bS as exportExcelSegment,c0 as queryFieldValueExpression,bT as queryFieldStatePropertyExpression,af as yearMonthDaySelectorSegment,bD as changeFieldVisibleByDeps,ar as RenderEngine,n as normalizeComponent}from"./index-6b6051d8.js";const _sfc_main$1=defineComponent({__name:"list-engine",setup(__props){const{emitTabAdd,t:$t,app}=usePageHelper(),$submitData=($form,$queryEngine,$table,$index,row)=>{$queryEngine.request.save(row,{query:{"*":{}},loading:!0}).then(res=>{res.data.length&&app.$message.success($t("common.success")),$table.cancelEditRow($index),$queryEngine.state.paginationManagement.refresh()})},$delete=($form,$queryEngine)=>{let selects=$form.query("table").take().componentProps.componentInstance.getCheckboxRecords();app.$confirm($t("是否确认删除"),"提示",{confirmButtonText:"确定",cancelButtonText:"取消",type:"warning"}).then(()=>{let payload=selects.map(row=>({negotiationId:row.negotiationId}));$queryEngine.request.delete(payload,{loading:!0}).then(res=>{app.$message.success($t("common.successDelete")),$queryEngine.state.paginationManagement.refresh()})}).catch(err=>{})},$approve=($form,$queryEngine)=>{let selects=$form.query("table").take().componentProps.componentInstance.getCheckboxRecords();selects.some(item=>item.approveStatus!="DRAFT")&&app.$message.warning("请选择状态为拟定的数据"),app.$confirm($t("是否确认提交审批"),"提示",{confirmButtonText:"确定",cancelButtonText:"取消",type:"warning"}).then(()=>{let payload=selects.map(row=>({carTargetId:row.carTargetId}));$queryEngine.request.baseRequest({type:"ReduceCarTarget",lang:"zh-cn",loading:!0,payload:[payload],action:""}).then(res=>{app.$message.success($t("common.success")),$queryEngine.state.paginationManagement.refresh()})}).catch(err=>{})};onActivated(()=>{let{from,funName,row}=app.$route.params;from==="fromFun"&&funName==="reduceProgress"&&bus.$emit("ReduceNegotiationByProgressNo")});const schema=defineSchemas({ReduceNegotiation:{type:"void","x-decorator":"QueryEngine","x-component":"el-container","x-component-props":{class:"flex-container",direction:"vertical"},"x-query-engine":{service:"cost",actions:{paginationQuery:{immediate:!0,transformRequest:expression(`(data, headers) => {
            data.query['*'] = {}
            return data
          }`),onSuccess:expression(`(res) => {

          }`)}}},properties:{bus:{type:"void","x-component":"BusEvent","x-component-props":{eventName:"ReduceNegotiation","@listener":expression(`() => {
            $queryEngine.state.paginationManagement.refresh()
          }`)}},bus2:{type:"void","x-component":"BusEvent","x-component-props":{eventName:"ReduceNegotiationByProgressNo","@listener":expression(`() => {
            let { from, funName, row,formNo } = app.$route.params
            $form.query('query.progressNo').take().setValue(formNo)
            setTimeout(()=>{
              console.log($form.query('query.progressNo').take())
              $queryEngine.state.paginationManagement.queryParams.value = {
                progressNo: { contains: formNo }
              }
              $queryEngine.state.paginationManagement.refresh()
            })

          }`)}},query:{type:"object","x-query-engine-skip":!0,"x-component":"QueryFormByQueryEngine",properties:generateXindexInOrder({progressNo:{type:"string",title:"{{$t('reduce.progressNo')}}","x-query-engine-query-operator":"contains"},vendorCode:{type:"string",title:i18nExpression("common.vendorCode"),"x-component":"QuickSearchWrapper","x-component-props":{showKey:"companyCode",propKey:"companyCode",name:"scc_sup_company_info_all"}},createdFullName:{type:"string",title:"{{$t('supRisk.createdName')}}","x-component":"QuickSearchWrapper","x-component-props":{name:"scc_rbac_user_display",showKey:"nickname",propKey:"nickname"}},creationDate:{title:"{{$t('common.creationTime')}}",...dataTimeSelectorSegment,"x-query-engine-query-operator":"between"}})},toolbar:{type:"void","x-component":"Space","x-component-props":{style:"margin-bottom:16px;"},properties:{add:{type:"void",title:"{{$t('common.add')}}","x-component":"RButton","x-component-props":{type:"primary","@click":expression(`() => {
                $form.query("table").take().componentProps.componentInstance.addRow("unshift",{})
              }`)}},delete:{type:"void",title:"{{$t('common.delete')}}","x-component":"RButton","x-component-props":{type:"primary","@click":expression(`() => {
                $delete($form,$queryEngine)
              }`)}},exportExcel:{type:"void","x-component":"ExportExcel","x-component-props":{type:"default",pageUrl:"/api-cost/api-ql/ReduceNegotiation/query",...exportExcelSegment,meiqlKey:"ReduceNegotiation",filterParams:queryFieldValueExpression("query"),tableHeader:queryFieldStatePropertyExpression("ReduceNegotiation.table","data.columns"),dictCodes:{}}},importExcel:{type:"void","x-component":"ImportExcel","x-component-props":{title:"{{$t('common.import')}}",type:"default",extraData:{fileModular:"sup",fileFunction:"purchaseCatalog",fileType:"excel"},upLoadUrl:"/api-cost/reduce/negotiation/importExcel",downloadTemplateOptions:{downloadUrl:"/api-cost/reduce/negotiation/exportExcelTemplate",fileName:"{{$t('logisticsMod.importTemplateXLSX')}}"},"@handleSuccess":expression(`() => {
                $bus.$emit('ReduceNegotiation')
              }`)}}}},table:{type:"array","x-component":"RenderTable","x-component-props":{class:"table-view-vxe-table",style:"flex: 1",preColumns:"checkbox,seq",openCustomTable:!0,editMode:"multi-row"},properties:generateXindexInOrder({negotiationId:{type:"number","x-hidden":!0,"x-query-engine-primary-key":!0},progressNo:{type:"string",title:"{{$t('reduce.progressNo')}}","x-component":"QuickSearchWrapper","x-component-props":{showKey:"progressNo",propKey:"progressNo",name:"scc_cost_reduce_progress",preQueryData:expression("{'t.car_level': 2}"),"@close-quicksearch":expression(`(val, scope) => {
                const row = $table.getRowByIndex($self.index)
                row.vendorId = val ? val.vendorId : ''
                row.vendorCode = val ? val.vendorCode : ''
                row.vendorName = val ? val.vendorName : ''
                row.progressNo = val ? val.progressNo : ''
              }`)},"x-reactions":expression(`(field) => {
                let isRowEditable = $table.getSelfRowEditable($self)
              setTimeout(()=>{
                $self.editable =  isRowEditable && !$table.getRowByIndex($self.index)?.progressNo
              })
            }`),"x-render-table-column":{minWidth:180}},negotiationDate:{"x-render-table-column":{title:"{{$t('reduce.negotiationDate')}}",minWidth:140},"x-reactions":expression(`(field) => {
                let isRowEditable = $table.getSelfRowEditable($self)
              setTimeout(()=>{
                $self.editable = $table.getRowByIndex($self.index)?.approveStatus == 'DRAFT' && isRowEditable
              })
            }`),type:"date",default:null,"x-component-props":{style:{width:"120px"},placeholder:i18nExpression("common.pleaseSelectDate"),format:"yyyy-MM-dd","value-format":"yyyy-MM-dd"}},vendorCode:{type:"string","x-render-table-column":{title:"{{$t('reduce.vendorCode')}}",minWidth:120,skipEditable:!0}},vendorName:{type:"string","x-render-table-column":{title:"{{$t('reduce.vendorName')}}",minWidth:120,skipEditable:!0}},negotiationResult:{type:"string","x-render-table-column":{title:"{{$t('reduce.negotiationResult')}}",minWidth:120},"x-reactions":expression(`(field) => {
                let isRowEditable = $table.getSelfRowEditable($self)
              setTimeout(()=>{
                $self.editable = isRowEditable
              })
            }`)},ourParty:{type:"string","x-reactions":expression(`(field) => {
                let isRowEditable = $table.getSelfRowEditable($self)
              setTimeout(()=>{
                $self.editable =  isRowEditable
              })
            }`),"x-render-table-column":{title:"{{$t('reduce.ourParty')}}",minWidth:120}},fileName:{type:"void",title:"{{$t('reduce.negotiationFile')}}","x-component":"SrmCommonFile","x-component-props":{"extra-data":{fileModular:"sup",fileFunction:"vendorBiddingManagement",fileType:"images"},defaultFile:{fileId:"{{$table.getRowByIndex($self.index)?.fileId}}",fileName:"{{$table.getRowByIndex($self.index)?.fileName}}"},readonly:!1,"@on-change":expression(`({file}) => {
                const row = $table.getRowByIndex($self.index)
                row.fileId = file.fileId.toString()
                row.fileName = file.fileName
              }`)},"x-reactions":expression(`(field) => {
                let isRowEditable = $table.getSelfRowEditable($self)
              setTimeout(()=>{
                $self.componentProps.readonly = !isRowEditable
              })
            }`),"x-render-table-column":{minWidth:120}},createdFullName:{type:"string","x-render-table-column":{title:"{{$t('common.creator')}}",width:120,skipEditable:!0}},creationDate:{title:"{{ $t('common.creationTime') }}",...yearMonthDaySelectorSegment,"x-render-table-column":{width:150,skipEditable:!0}},lastUpdatedFullName:{type:"string","x-render-table-column":{title:"{{$t('common.updatePeople')}}",width:120,skipEditable:!0}},lastUpdateDate:{"x-query-engine-sort":"desc",...yearMonthDaySelectorSegment,"x-render-table-column":{title:"{{$t('common.updateTime')}}",skipEditable:!0,width:120}},operation:{type:"void",title:"{{$t('common.operation')}}","x-component":"RenderTableButtonList","x-component-props":{max:2},"x-render-table-column":{fixed:"right",width:120},properties:{edit:{type:"void",title:"{{$t('common.edit')}}","x-reactions":changeFieldVisibleByDeps([".approveStatus"],"!$table.getSelfRowEditable($self) "),"x-component-props":{"@click":expression(`({rowIndex}) => {
                    $table.editRowByIndex(rowIndex)
                  }`)}},cancel:{type:"void",title:"{{$t('common.cancel')}}","x-reactions":changeFieldVisibleByDeps([".approveStatus"],"$table.getSelfRowEditable($self) "),"x-component-props":{"@click":expression(`({rowIndex}) => {
                    $table.cancelEditRow(rowIndex)
                  }`)}},save:{type:"void",title:"{{$t('common.save')}}","x-reactions":changeFieldVisibleByDeps([".approveStatus"],"$table.getSelfRowEditable($self) "),"x-component-props":{"@click":expression(`({ row }) => {
                    $submitData($form,$queryEngine,$table,$self.index,row)
                  }`)}}}}})}}}});return{__sfc:!0,emitTabAdd,$t,app,$submitData,$delete,$approve,schema,components:{},scope:{app,$submitData,$delete,$approve},RenderEngine}}});var _sfc_render$1=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{pageAttrs:_vm.$attrs,scope:_setup.scope,components:_setup.components,schema:_setup.schema,schemaKey:"reduceNegotiation"}})},_sfc_staticRenderFns$1=[],__component__$1=normalizeComponent(_sfc_main$1,_sfc_render$1,_sfc_staticRenderFns$1,!1,null,null,null,null);const reduceNegotiationList=__component__$1.exports,_sfc_main={name:"ReduceNegotiation",components:{NavTabs},data(){return{activeTab:"reduceNegotiationList",tabs:[{title:this.$t("reduce.reduceNegotiation"),name:"reduceNegotiationList",component:reduceNegotiationList,closable:!1}]}}};var _sfc_render=function(){var _vm=this,_c=_vm._self._c;return _c("NavTabs",{attrs:{"tabs-list":_vm.tabs,"cur-tab":_vm.activeTab}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const index=__component__.exports;export{index as default};
