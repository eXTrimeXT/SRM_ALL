import{N as NavTabs}from"./index-9a7f2446.js";import{ak as defineComponent,al as usePageHelper,aq as defineSchemas,ad as expression,ah as generateXindexInOrder,ae as i18nExpression,ca as buttonListItemVisibleByPermission,bS as exportExcelSegment,c0 as queryFieldValueExpression,bT as queryFieldStatePropertyExpression,af as yearMonthDaySelectorSegment,ai as editTableFormItemValid,bD as changeFieldVisibleByDeps,ar as RenderEngine,n as normalizeComponent}from"./index-6b6051d8.js";const _sfc_main$1=defineComponent({__name:"list-engine",setup(__props){const{emitTabAdd,t:$t,app}=usePageHelper(),$submitData=($form,$queryEngine,$table,$index,row)=>{$form.validate().then(()=>{$queryEngine.request.save(row,{query:{"*":{}},loading:!0}).then(res=>{res.data.length&&app.$message.success($t("common.success")),$table.cancelEditRow($index),$queryEngine.state.paginationManagement.refresh()})}).catch(err=>{app.$message.warning($t("common.pleasefinishRequired"))})},$createNegotiation=($form,$queryEngine,$table,$index,row)=>{let payload={progressNo:row.progressNo,progressId:row.progressId,vendorCode:row.vendorCode,vendorId:row.vendorId,vendorName:row.vendorName};$queryEngine.request.baseRequest({type:"ReduceNegotiation",lang:"zh-cn",payload:[payload],action:"save",query:{"*":{}}}).then(res=>{res.data.length&&(app.$message.success($t("common.success")),$queryEngine.state.paginationManagement.refresh())})},$toNegotiation=row=>{app.$router.push({name:"reduceNegotiation",params:{from:"fromFun",funName:"reduceProgress",formId:row.progressId,formNo:row.progressNo,row}})},$addProgress=(val,$form)=>{let key=["reduceYear","carCode","carName","orgId","orgCode","orgName","invOrgId","invOrgCode","invOrgName","materialCode","materialName","categoryCode","categoryName","vendorCode","vendorName","categoryName","priceType","effectiveDate","expirationDate","assembleCoefficient","referBasicPointPrice","confirBasicPointPrice"],obj={};for(let k of key)obj[k]=val[k]||void 0;obj.approveStatus="DRAFT",$form.query("table").take().componentProps.componentInstance.addRow("unshift",obj)},$approve=($form,$queryEngine)=>{let selects=$form.query("table").take().componentProps.componentInstance.getCheckboxRecords();if(selects.some(item=>item.approveStatus!="DRAFT")||!selects.length)return app.$message.warning("请选择状态为拟定的数据");app.$confirm($t("是否确认提交审批"),"提示",{confirmButtonText:"确定",cancelButtonText:"取消",type:"warning"}).then(()=>{let payload=selects.map(row=>row.progressId);app.$http({url:"/api-cost/reduce/progress/submitApproval",method:"POST",data:{progressIds:payload},loading:!0}).then(res=>{app.$message.success($t("common.success")),$queryEngine.state.paginationManagement.refresh()})}).catch(err=>{})},schema=defineSchemas({ReduceProgress:{type:"void","x-decorator":"QueryEngine","x-component":"el-container","x-component-props":{class:"flex-container",direction:"vertical"},"x-query-engine":{service:"cost",actions:{paginationQuery:{immediate:!0,transformRequest:expression(`(data, headers) => {
            data.query['*'] = {}
            return data
          }`),onSuccess:expression(`(res) => {

          }`)}}},properties:{bus:{type:"void","x-component":"BusEvent","x-component-props":{eventName:"BarCode","@listener":expression(`() => {
            $queryEngine.state.paginationManagement.refresh()
          }`)}},query:{type:"object","x-query-engine-skip":!0,"x-component":"QueryFormByQueryEngine",properties:generateXindexInOrder({orgId:{type:"string",title:"{{$t('common.orgId')}}","x-component":"OrganizationSelector","x-component-props":{"parent-id":-1,"node-type":"OU","select-type":"input",placeholder:"{{$t('common.pleaseSelect')}}",multiple:!1,"@select":expression(`(node) => {
                  if (!$form.values.query.invOrgId) return
                  $form.values.query.invOrgId = null
              }`)}},invOrgId:{type:"string",title:"{{$t('common.invOrg')}}","x-component":"OrganizationSelector","x-component-props":{"node-type":"INV","select-type":"input",placeholder:"{{$t('common.pleaseSelect')}}",multiple:!1,disabled:expression("!$form.values.query.orgId"),"parent-id":expression("$form.values.query.orgId")}},reduceYear:{type:"string",title:"{{$t('reduce.reduceYear')}}","x-component":"DatePicker","x-component-props":{type:"year","value-format":"yyyy"}},approveStatus:{type:"string",title:"{{$t('reduce.approveStatus')}}","x-component":"DictSelect","x-component-props":{code:"APPROVE_STATUS"}}})},toolbar:{type:"void","x-component":"Space","x-component-props":{style:"margin-bottom:16px;"},properties:{add:{type:"void",title:i18nExpression("common.new"),"x-component":"QuickSearchWrapper","x-query-engine-skip":!0,"x-component-props":{...buttonListItemVisibleByPermission("reduce:reduceProgress:add"),showButton:!0,multiSelect:!1,btnTitle:"{{$t('common.add')}}",name:"scc_cost_reduce_basic_price","@close-quicksearch":expression(`(val, scope) => {
                $addProgress(val,$form)
              }`)}},exportExcel:{type:"void","x-component":"ExportExcel","x-component-props":{...buttonListItemVisibleByPermission("reduce:reduceProgress:export"),type:"default",pageUrl:"/api-cost/api-ql/ReduceProgress/query",...exportExcelSegment,meiqlKey:"ReduceProgress",filterParams:queryFieldValueExpression("query"),tableHeader:queryFieldStatePropertyExpression("ReduceProgress.table","data.columns"),dictCodes:{approveStatus:"APPROVE_STATUS"}}},approve:{type:"void",title:"{{$t('reduce.approve')}}","x-component":"RButton","x-component-props":{type:"primary",...buttonListItemVisibleByPermission("reduce:reduceProgress:approve"),"@click":expression(`() => {
                $approve($form,$queryEngine)
              }`)}}}},table:{type:"array","x-component":"RenderTable","x-component-props":{class:"table-view-vxe-table",style:"flex: 1",preColumns:"checkbox,seq",openCustomTable:!0,editMode:"multi-row"},properties:generateXindexInOrder({progressId:{type:"number","x-hidden":!0,"x-query-engine-primary-key":!0},progressNo:{type:"string","x-render-table-column":{title:"{{$t('reduce.progressNo')}}",minWidth:150,skipEditable:!0}},reduceYear:{type:"string","x-render-table-column":{title:"{{$t('reduce.reduceYear')}}",minWidth:120,skipEditable:!0}},orgName:{type:"string","x-render-table-column":{title:"{{$t('common.orgId')}}",minWidth:130,skipEditable:!0}},invOrgName:{type:"string","x-render-table-column":{title:"{{$t('common.invOrg')}}",minWidth:130,skipEditable:!0}},materialCode:{type:"string","x-render-table-column":{title:"{{$t('reduce.materialCode')}}",minWidth:120,skipEditable:!0}},materialName:{type:"string","x-render-table-column":{title:"{{$t('reduce.materialName')}}",minWidth:120,skipEditable:!0}},vendorCode:{type:"string","x-render-table-column":{title:"{{$t('reduce.vendorCode')}}",minWidth:120,skipEditable:!0}},vendorName:{type:"string","x-render-table-column":{title:"{{$t('reduce.vendorName')}}",minWidth:150,skipEditable:!0}},categoryName:{type:"string","x-render-table-column":{title:"{{$t('reduce.categoryName')}}",minWidth:150,skipEditable:!0}},confirBasicPointPrice:{type:"string","x-render-table-column":{title:"{{$t('reduce.basePrice')}}",minWidth:120,skipEditable:!0}},priceType:{type:"string","x-component":"DictSelect","x-component-props":{code:"PRICE_TYPE"},"x-render-table-column":{title:"{{$t('reduce.priceType')}}",minWidth:120,skipEditable:!0}},effectiveDate:{"x-render-table-column":{title:"{{$t('reduce.effectiveDate')}}",minWidth:120,skipEditable:!0},...yearMonthDaySelectorSegment},expirationDate:{"x-render-table-column":{title:"{{$t('reduce.expirationDate')}}",minWidth:120,skipEditable:!0},...yearMonthDaySelectorSegment},progressProportionStr:{type:"string","x-render-table-column":{title:"{{$t('reduce.progressProportion')}}",width:120,skipEditable:!0},"x-reactions":expression(`(field) => {
              let row = $table.getRowByIndex($self.index)
              let num = '-'
              if(row && row.progressProportion){
                num = parseFloat((row.progressProportion*100).toFixed(4)) + '%'
              }
              setTimeout(()=>{
                field.setValue(num)
              })
            }`)},progressProportion:{type:"string","x-hidden":!0,"x-render-table-column":{title:"{{$t('reduce.progressProportion')}}",width:120},"x-reactions":expression(`(field) => {
            }`)},progressReduceAmount:{type:"string",...editTableFormItemValid,"x-render-table-column":{title:"{{$t('reduce.progressReduceAmount')}}",width:120},"x-reactions":expression(`(field) => {
                let isRowEditable = $table.getSelfRowEditable($self)
                setTimeout(()=>{
                  $self.editable = $table.getRowByIndex($self.index)?.approveStatus == 'DRAFT' && isRowEditable
                })

                let row = $table.getRowByIndex($self.index)
                if(row && row.progressReduceAmount != undefined && row.confirBasicPointPrice != undefined ){
                  let value = (row.progressReduceAmount - row.confirBasicPointPrice)/row.confirBasicPointPrice
                  setTimeout(()=>{
                    row.progressProportion = parseFloat(value.toFixed(4))
                  })
                }
            }`)},progressReduceChangeValue:{type:"string","x-render-table-column":{title:"{{$t('reduce.progressReduceChangeValue')}}",width:120,skipEditable:!0},"x-reactions":expression(`(field) => {
              let row = $table.getRowByIndex($self.index)
              if(row && row.progressReduceAmount != undefined && row.confirBasicPointPrice != undefined ){
                let value = row.progressReduceAmount - row.confirBasicPointPrice
                setTimeout(()=>{
                  field.setValue(parseFloat(value.toFixed(4)))
                })
              }
            }`)},startDate:{...editTableFormItemValid,"x-render-table-column":{title:"{{$t('reduce.startDate')}}",minWidth:140},"x-reactions":expression(`(field) => {
                let isRowEditable = $table.getSelfRowEditable($self)
              setTimeout(()=>{
                $self.editable = $table.getRowByIndex($self.index)?.approveStatus == 'DRAFT' && isRowEditable
              })
            }`),type:"date",default:null,"x-component-props":{style:{width:"120px"},placeholder:i18nExpression("common.pleaseSelectDate"),format:"yyyy-MM-dd","value-format":"yyyy-MM-dd"}},endDate:{...editTableFormItemValid,"x-render-table-column":{title:"{{$t('reduce.endDate')}}",minWidth:140},"x-reactions":expression(`(field) => {
                let isRowEditable = $table.getSelfRowEditable($self)
              setTimeout(()=>{
                $self.editable = $table.getRowByIndex($self.index)?.approveStatus == 'DRAFT' && isRowEditable
              })
            }`),type:"date",default:null,"x-component-props":{style:{width:"120px"},placeholder:i18nExpression("common.pleaseSelectDate"),format:"yyyy-MM-dd","value-format":"yyyy-MM-dd"}},negotiationResult:{type:"string","x-render-table-column":{title:"{{$t('reduce.negotiationResult')}}",minWidth:120},"x-reactions":expression(`(field) => {
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
            }`),"x-render-table-column":{minWidth:120}},remarks:{type:"string","x-render-table-column":{title:"{{$t('reduce.remarks')}}",width:120},"x-reactions":expression(`(field) => {
                let isRowEditable = $table.getSelfRowEditable($self)
              setTimeout(()=>{
                $self.editable = $table.getRowByIndex($self.index)?.approveStatus == 'DRAFT' && isRowEditable
              })
            }`)},approveStatus:{type:"string","x-component":"DictSelect","x-component-props":{code:"APPROVE_STATUS"},"x-render-table-column":{title:"{{$t('reduce.approveStatus')}}",minWidth:100,skipEditable:!0}},createdUserName:{type:"string","x-render-table-column":{title:"{{$t('common.creator')}}",width:120,skipEditable:!0}},creationDate:{title:"{{ $t('common.creationTime') }}",...yearMonthDaySelectorSegment,"x-render-table-column":{width:150,skipEditable:!0}},lastUpdatedUserName:{type:"string","x-render-table-column":{title:"{{$t('common.updatePeople')}}",width:120,skipEditable:!0}},lastUpdateDate:{type:"string",...yearMonthDaySelectorSegment,"x-query-engine-sort":"desc","x-render-table-column":{title:"{{$t('common.updateTime')}}",width:120,skipEditable:!0}},operation:{type:"void",title:"{{$t('common.operation')}}","x-component":"RenderTableButtonList","x-component-props":{max:2},"x-render-table-column":{fixed:"right",width:120},properties:{edit:{type:"void",title:"{{$t('common.edit')}}","x-reactions":changeFieldVisibleByDeps([".approveStatus"],"['DRAFT'].includes($deps[0]) && !$table.getSelfRowEditable($self) "),"x-component-props":{"@click":expression(`({rowIndex}) => {
                    $table.editRowByIndex(rowIndex)
                  }`)}},cancel:{type:"void",title:"{{$t('common.cancel')}}","x-reactions":changeFieldVisibleByDeps([".approveStatus"],"$table.getSelfRowEditable($self) "),"x-component-props":{"@click":expression(`({rowIndex}) => {
                    $table.cancelEditRow(rowIndex)
                  }`)}},save:{type:"void",title:"{{$t('common.save')}}","x-reactions":changeFieldVisibleByDeps([".approveStatus"],"['DRAFT'].includes($deps[0]) && $table.getSelfRowEditable($self) "),"x-component-props":{"@click":expression(`({ row }) => {
                    $submitData($form,$queryEngine,$table,$self.index,row)
                  }`)}}}}})}}}});return{__sfc:!0,emitTabAdd,$t,app,$submitData,$createNegotiation,$toNegotiation,$addProgress,$approve,schema,components:{},scope:{$approve,$submitData,$addProgress,$toNegotiation,$createNegotiation},RenderEngine}}});var _sfc_render$1=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{pageAttrs:_vm.$attrs,scope:_setup.scope,components:_setup.components,schema:_setup.schema,schemaKey:"reduceReportMaterial"}})},_sfc_staticRenderFns$1=[],__component__$1=normalizeComponent(_sfc_main$1,_sfc_render$1,_sfc_staticRenderFns$1,!1,null,null,null,null);const reduceProgressList=__component__$1.exports,_sfc_main={name:"ReduceProgress",components:{NavTabs},data(){return{activeTab:"reduceProgressList",tabs:[{title:this.$t("route.reduceProgress"),name:"reduceProgressList",component:reduceProgressList,closable:!1}]}}};var _sfc_render=function(){var _vm=this,_c=_vm._self._c;return _c("NavTabs",{attrs:{"tabs-list":_vm.tabs,"cur-tab":_vm.activeTab}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const index=__component__.exports;export{index as default};
