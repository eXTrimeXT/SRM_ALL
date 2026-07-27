import{N as NavTabs}from"./index-9a7f2446.js";import"./mixins-edc77a54.js";import{T as TableView}from"./TableView-eb18d7e8.js";/* empty css                                                                   */import{ac as createDictClass,h as http,n as normalizeComponent,H as FormWrapper,bq as lodashExports,ak as defineComponent,aq as defineSchemas,ad as expression,ah as generateXindexInOrder,ca as buttonListItemVisibleByPermission,ae as i18nExpression,bS as exportExcelSegment,c0 as queryFieldValueExpression,bT as queryFieldStatePropertyExpression,af as yearMonthDaySelectorSegment,bD as changeFieldVisibleByDeps,al as usePageHelper,cl as onActivated,bB as useDebounceFn,cb as action,ar as RenderEngine}from"./index-6b6051d8.js";import"./index-3aa48b38.js";import"./enum-13a06f7c.js";import"./z-material-table-78c4aedd.js";import{w as wrapper}from"./axios-cache-plugin-1edad216.js";import"./big-e21bdbb6.js";import{c as contractManagement}from"./index-2e6ba8f1.js";import contractInformation from"./edit-engine-2924ba4a.js";import{t as transformMQL}from"./util-d962b17f.js";import{i as inviteHttp}from"./index-ec74e7e7.js";import"./index-5f54905d.js";import"./index-0a035acc.js";/* empty css                                              */import"./util-1e55288f.js";import"./index-d31c36cb.js";import"./drag-5571e5c7.js";import"./index-7952cb71.js";import"./index-e416f1ab.js";import"./index-f806b430.js";import"./index-2c71d18e.js";import"./number-2d936dc6.js";import"./validate-bf972fb1.js";import"./uniqueId-f496b65a.js";createDictClass({tax:[]});wrapper(http,{ttl:6e4*5});const _sfc_main$2={name:"InviteHistoryDialog",components:{FormWrapper,TableView},props:{visible:{type:Boolean,default:!1},mode:{type:String,default:"collect"}},data(){return{searchFormConfig:[{prop:"projectName",label:this.$t("cusEntry.inq.priceOrderNo")},{prop:"buId",label:this.$t("components.organization.COMPANY"),type:"OUorganizationSelector"},{prop:"vendorName",label:this.$t("common.vendor"),type:"quicksearch",showKey:"companyName",propKey:"companyName",name:"scc_sup_company_info_all"},{prop:"itemCode",label:$t("common.materialCode")}],tableData:[],tableHeader:[],queryParam:{},tableViewUrl:inviteHttp.hisListPageUrl,selectedRows:[]}},watch:{visible:{handler(nVal){nVal&&this.getQueryData()},immediate:!0,deep:!0}},created(){this.tableHeader=[{prop:"projectName",label:$t("cusEntry.inq.priceOrderNo")},{prop:"buName",label:$t("components.organization.COMPANY")},{prop:"vendorName",label:$t("common.vendor")},{prop:"itemCode",label:$t("common.materialCode")},{prop:"itemName",label:$t("common.materialName")},{prop:"noTaxPrice",label:$t("bid_mod.untaxedPrice")},{prop:"quality",label:$t("bid_mod.quantity")},{prop:"noTaxTotalPrice",label:$t("competition.orderNotaxTotalPrice")}]},methods:{getQueryData(params={}){this.queryParam=transformMQL.listPageData({type:"SouInviteHistoryBuyer",action:"query",params}),this.$nextTick(()=>{this.$refs.list.query()})},handleCurrentChange(val){this.selectedRows=val},close(){this.$emit("close")},confirm(){if(!this.selectedRows||!this.selectedRows.length)return this.$message.warning(this.$t("outsource.pleaseCheckList"));if(lodashExports.uniqBy(this.selectedRows,"buName").length>1)return this.$message.warning(this.$t("cusEntry.supplement20250205.sameCompanyCanCreateTogether"));if(lodashExports.uniqBy(this.selectedRows,"vendorName"),lodashExports.uniqBy(this.selectedRows,"vendorName").length>1)return this.$message.warning($t("cusEntry.supplement20250205.sameSupplierCanCreateTogether"));this.$emit("confirm",this.selectedRows)}}};var _sfc_render$2=function(){var _vm=this,_c=_vm._self._c;return _c("SrmDialog",_vm._g(_vm._b({attrs:{title:_vm.$t("cusEntry.supplement20250121.viewInvitationHistory"),size:"large",visible:_vm.visible,"close-on-click-modal":!1,"before-close":_vm.close},on:{"update:visible":function($event){_vm.visible=$event}}},"SrmDialog",_vm.$attrs,!1),_vm.$listeners),[_c("FormWrapper",{attrs:{colLength:3,"form-array":_vm.searchFormConfig},on:{getFormData:_vm.getQueryData}}),_c("TableView",{ref:"list",attrs:{"table-data":_vm.tableData,"table-header":_vm.tableHeader,"pre-query-data":_vm.queryParam,checkbox:!0,checkChange:_vm.handleCurrentChange,"com-active":_vm.$attrs.changeTab,"open-custom-tabl":!1,url:_vm.tableViewUrl,adeptMeiQl:!0}}),_c("div",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[_c("el-button",{on:{click:_vm.close}},[_vm._v(" "+_vm._s(_vm.$t("components.common.cancel"))+" ")]),_c("el-button",{attrs:{type:"primary"},on:{click:_vm.confirm}},[_vm._v(" "+_vm._s(_vm.$t("common.confirm"))+" ")])],1)],1)},_sfc_staticRenderFns$2=[],__component__$2=normalizeComponent(_sfc_main$2,_sfc_render$2,_sfc_staticRenderFns$2,!1,null,null,null,null);const ContractDetailDialog=__component__$2.exports,_sfc_main$1=defineComponent({__name:"list-engine",setup(__props){const generateAddOneEventExpression=type=>expression(`() => {
  $addOne('${type}', $self.query('ContractHead.table').take().componentProps.componentInstance.getCheckboxRecords())
}`),$adjustDialogConfirm=($form,values,$message,$confirm,$queryEngine,done,closeLoading)=>{const data={...$form.query("state").get("data").currentRows[0],...values};$queryEngine.request.save(data).then(res=>{res&&($message.success(t("common.success")),$queryEngine.state.paginationManagement.refresh(),done())})},$reponsiblityDialogConfirm=($form,values,$message,$confirm,$queryEngine,done,closeLoading)=>{const data={...$form.query("state").get("data").currentRows[0],...values};$queryEngine.request.save(data).then(res=>{res&&($message.success(t("common.success")),$queryEngine.state.paginationManagement.refresh(),done())})},schema=defineSchemas({state:{type:"void","x-component":"Fragment","x-hidden":!0,"x-data":{contractDialogVisible:!1,contractDialogMode:"collect",currentRows:[],signRow:{}}},ContractHead:{type:"void","x-query-engine":{service:"cm",actions:{paginationQuery:{}}},"x-decorator":"el-container","x-decorator-props":{class:"flex-container",direction:"vertical"},"x-component":"QueryEngine",properties:{bus:{type:"void","x-component":"BusEvent","x-component-props":{eventName:"ContractHead","@listener":expression(`() => {
            $queryEngine.state.paginationManagement.refresh()
          }`)}},query:{type:"object","x-query-engine-skip":!0,"x-component":"QueryFormByQueryEngine","x-component-props":{immediateQueryForm:!0},properties:generateXindexInOrder({contractNo:{type:"string",title:"{{$t('contractMod.contractNo')}}","x-query-engine-query-operator":"contains"},contractName:{type:"string",title:"{{$t('contractMod.contractName')}}","x-query-engine-query-operator":"contains"},contractStatus:{type:"string",title:"{{$t('orderMod.buyerOrderSynergy.contractStatus')}}","x-component":"DictSelect","x-component-props":{code:"CONTRACT_STATUS"},"x-reactions":{effects:["onFieldInit"],fulfill:{state:{value:expression("app.$route?.params?.from === 'workCount' ? 'SUPPLIER_CONFIRMING' : ''")}}}},buName:{type:"string",title:"我方签约主体","x-query-engine-query-operator":"contains"},contractClass:{type:"string",title:"{{$t('contractMod.contractType')}}","x-component":"DictSelect","x-component-props":{code:"ELEM_CONTRACT_TYPE"}},vendorId:{type:"string",title:"{{$t('contractMod.vendorName')}}","x-hidden":"{{ $vendor() }}","x-component":"QuickSearchWrapper","x-component-props":{showKey:"companyName",propKey:"companyId",name:"scc_sup_company_info_display_buyer"}},createdBy:{type:"string",title:"{{$t('common.creator')}}","x-query-engine-query-operator":"contains"},categoryName:{type:"string",title:"采购品类","x-query-engine-relation":"contractMaterials","x-query-engine-relation-strict":!0,"x-component":"CCategorySelect","x-component-props":{showKey:"categoryName"}},extContractHandlerId:{type:"string",title:"合同经办人","x-component":"QuickSearchWrapper","x-component-props":{showKey:"nickname",propKey:"userId",name:"scc_rbac_user_display"}},extInviteHeadId:{type:"string",title:"招标专家","x-component":"QuickSearchWrapper","x-component-props":{showKey:"nickname",propKey:"userId",name:"scc_rbac_user_display"}}})},toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void",title:"{{$t('contractMod.addContract')}}","x-visible":expression("$buyer()"),"x-component-props":{type:"primary",...buttonListItemVisibleByPermission("cm:contractManager:add"),"@click":expression("() => $addOne('MIAN_CONTRACT_ADD')")}},addCollect:{type:"void","x-visible":expression("$buyer()"),"x-component":"QuickSearchWrapper","x-component-props":{showButton:!0,multiSelect:!0,class:"quickBtn",btnTitle:"新建集采合同",name:"sou_purfix_price_contract",...buttonListItemVisibleByPermission("cm:contractManager:addCollect"),"@close-quicksearch":expression(`(val) => {
                $addContractOne('collect',$form,$queryEngine,val)
              }`)}},addTemp:{type:"void","x-visible":expression("$buyer()"),"x-component":"QuickSearchWrapper","x-component-props":{showButton:!0,multiSelect:!0,class:"quickBtn",btnTitle:"新建临采合同",name:"scc_npm_sou_fix_price_pass",...buttonListItemVisibleByPermission("cm:contractManager:addTemp"),"@close-quicksearch":expression(`(val) => {
                $addContractOne('temp',$form,$queryEngine,val)
              }`)}},alter:{type:"void",title:"{{$t('contractMod.contractChange')}}","x-visible":!1,"x-component-props":{...buttonListItemVisibleByPermission("cm:contractManager:alter"),"@click":generateAddOneEventExpression("MIAN_CONTRACT_ALTER")}},contractChange2:{type:"void",title:"{{$t('contractMod.contractChange2')}}","x-visible":expression("$buyer()"),"x-component-props":{"@click":generateAddOneEventExpression("SUPPLEMENTAL_AGREEMENT")}},importExcel:{type:"void","x-component":"ImportExcel","x-visible":!1,"x-component-props":{title:i18nExpression("common.excelImport"),type:"default",extraData:{fileModular:"cm",fileFunction:"contractMaintainList",fileType:"excel"},upLoadUrl:"/api-cm/contract/contractHead/importExcel",downloadTemplateOptions:{downloadUrl:"/api-cm/contract/contractHead/importModelDownload",fileName:expression("$t('contractMod.contractManageImp')")},"@handleSuccess":expression(`() => {
                $bus.$emit('ContractHead')
              }`)}},exportExcel:{type:"void","x-component":"ExportExcel","x-component-props":{...exportExcelSegment,type:"default",pageUrl:"/api-cm/api-ql/ContractHead/query",filterParams:queryFieldValueExpression("query"),tableHeader:queryFieldStatePropertyExpression("ContractHead.table","data.columns"),dictCodes:{contractStatus:"CONTRACT_STATUS",contractType:"CONTRACT_TYPE",contractClass:"ELEM_CONTRACT_TYPE",formal:"CONTRACT_FORM2"}}},bulkMaintainFwAgreement:{type:"void",title:"{{$t('bidMod.bulkMaintainFwAgreement')}}","x-visible":!1,"x-component-props":{"@click":expression(`() => {
                const rows = $self.query('ContractHead.table').take()
                  .componentProps
                  .componentInstance
                  .getCheckboxRecords()

                if (!rows.length) {
                  $message.error($t('contractMod.msgSelData'))
                  return
                }

                for (let i = 0; i < rows.length; i += 1) {
                  const item = rows[i]
                  if (item.contractStatus != 'ARCHIVED') {
                    // 请选择已归档的数据
                    $message.error($t('bidMod.selSameVendor2'))
                    return
                  }

                  // 选择的数据必须是同一个供应商
                  if (i > 0 && item.vendorId !== rows[0].vendorId) {
                    $message.error($t('bidMod.selSameVendor'))
                    return
                  }
                }

                $form.query('bulkMaintainFwAgreementDialog').take().setComponentProps({ visible: true })
                setTimeout(() => {
                  $reactiveAction(() => {
                    const queryDataField = $form.query('bulkMaintainFwAgreementDialog.queryData').take()
                    queryDataField.value.vendorName = rows[0].vendorName
                    queryDataField.data.vendorId = rows[0].vendorId
                    queryDataField.data.globalcontractIds = rows.map(item => item.contractHeadId)
                  })
                })
              }`)}},operatorAdjust:{type:"void",title:"经办人调整","x-visible":expression("$buyer()"),"x-component-props":{...buttonListItemVisibleByPermission("cm:contractManager:operatorAdjust"),"@click":expression(`() => {
                let rows = $form.query('ContractHead.table').take().componentProps.componentInstance.getCheckboxRecords()
                console.log('rows',rows)
                if(!rows.length){
                  return $message.warning('请选择合同')
                }
                if(rows.length > 1){
                  return $message.warning('只能选择一个合同')
                }
                $form.query('state').get('data').currentRows = rows
                $form.query('operatorAdjustDialog').take().setComponentProps({ visible: true })
              }`)}},reponsiblityAdjust:{type:"void",title:"招标专家调整","x-visible":expression("$buyer()"),"x-component-props":{...buttonListItemVisibleByPermission("cm:contractManager:reponsiblityAdjust"),"@click":expression(`() => {
                let rows = $form.query('ContractHead.table').take().componentProps.componentInstance.getCheckboxRecords()
                console.log('rows',rows)
                if(!rows.length){
                  return $message.warning('请选择合同')
                }
                if(rows.length > 1){
                  return $message.warning('只能选择一个合同')
                }
                $form.query('state').get('data').currentRows = rows
                $form.query('reponsiblityAdjustDialog').take().setComponentProps({ visible: true })
              }`)}}}},table:{type:"array","x-component":"RenderTable","x-component-props":{class:"table-view-vxe-table",style:"flex: 1",preColumns:"checkbox, seq",openCustomTable:!0},properties:generateXindexInOrder({contractHeadId:{type:"string","x-hidden":!0},sealId:{type:"string","x-hidden":!0},vendorId:{type:"string","x-hidden":!0},stampContractFileuploadId:{type:"string","x-hidden":!0},extContractHandlerAccount:{type:"string","x-hidden":!0},contractNo:{type:"string","x-component":"RenderTableLink","x-component-props":{type:"text","@click":expression("({ row }) => $readOne(row,'view')")},"x-render-table-column":{title:i18nExpression("contractMod.contractNo"),minWidth:200,customRender:!0}},contractName:{type:"string",title:i18nExpression("contractMod.contractName"),"x-render-table-column":{minWidth:160}},contractStatus:{type:"string",title:"{{$t('common.status')}}","x-component":"DictSelect","x-component-props":{code:"CONTRACT_STATUS"},"x-render-table-column":{width:100}},contractType:{type:"string",title:"{{$t('contractMod.operationType')}}","x-component":"DictSelect","x-component-props":{code:"CONTRACT_TYPE"},"x-render-table-column":{width:120}},contractClass:{type:"string",title:"{{$t('contractMod.contractType')}}","x-component":"DictSelect","x-component-props":{code:"ELEM_CONTRACT_TYPE"},"x-render-table-column":{width:120}},formal:{type:"string",title:"{{$t('contractMod.signingMethod')}}","x-component":"DictSelect","x-component-props":{code:"CONTRACT_FORM2"},"x-render-table-column":{width:120}},buName:{type:"string",title:"我方签约主体","x-render-table-column":{width:150}},vendorCode:{type:"string",title:"{{$t('common.vendorCode')}}","x-render-table-column":{minWidth:130}},vendorName:{type:"string",title:"{{$t('common.vendorName')}}","x-render-table-column":{minWidth:150}},contractOldCode:{type:"string","x-component":"RenderTableLink","x-component-props":{type:"text","@click":expression(`({ row }) => {
                $queryEngine.request.query({
                  contractNo: {
                    eq: row.contractOldCode
                  }
                }).then(res => {
                  if (res.data[0]) {
                    $readOne(res.data[0])
                  }
                })
              }`)},"x-render-table-column":{title:"{{$t('contractMod.contractOldCode')}}",minWidth:150,customRender:!0}},sourceType:{type:"string",title:"来源类型","x-component":"DictSelect","x-component-props":{code:"CONTRACT_SOURCE_TYPE"},"x-render-table-column":{width:120}},planStatus:{type:"string",title:"履约状态","x-query-engine-skip":!0,"x-component":"DictSelect","x-component-props":{code:"CONTRACT_HEAD_PLAN_STATUS"},"x-render-table-column":{width:120}},contractAgreementCode:{type:"string",title:"{{$t('contractMod.annexId')}}","x-render-table-column":{width:150}},modelName:{type:"string",title:"{{$t('contractMod.templHeadId')}}","x-render-table-column":{width:150}},effectiveDateFrom:{title:"{{$t('contractMod.constartDate')}}",...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.effectiveDateFrom, '{y}-{m}-{d}')
              }`)},"x-render-table-column":{width:130}},contractTerminationCode:{type:"string",title:"{{$t('contractMod.terminationId')}}","x-render-table-column":{width:150}},endDate:{title:"{{$t('qualitySynergy.endDate2')}}",...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.endDate, '{y}-{m}-{d}')
              }`)},"x-render-table-column":{width:150}},extContractHandlerId:{type:"string","x-hidden":!0},extContractHandlerName:{type:"string",title:"合同经办人","x-render-table-column":{width:100}},extInviteHeadId:{type:"string","x-hidden":!0},extInviteHeadName:{type:"string",title:"招标专家","x-render-table-column":{width:100}},createdBy:{type:"string","x-hidden":!0},createdUserName:{type:"string",title:"{{$t('common.creator')}}","x-query-engine-skip":!0,"x-render-table-column":{width:100}},creationDate:{title:"{{$t('common.creationTime')}}","x-query-engine-sort":"desc",...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.creationDate, '{y}-{m}-{d}')
              }`)},"x-render-table-column":{width:150}},lastUpdatedBy:{type:"string","x-hidden":!0},lastUpdatedUserName:{type:"string",title:"{{$t('contractMod.lastUpdatedBy')}}","x-query-engine-skip":!0,"x-render-table-column":{width:130}},lastUpdateDate:{title:"{{$t('contractMod.lastUpdateDate')}}",...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.lastUpdateDate, '{y}-{m}-{d}')
              }`)},"x-render-table-column":{width:150}},vendorConfirmDate:{...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.vendorConfirmDate, '{y}-{m}-{d}')
              }`)},title:"{{$t('contractMod.vendorConfirmDate')}}","x-render-table-column":{width:140}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:180,fixed:"right"},"x-component":"RenderTableButtonList","x-component-props":{max:3},properties:{management:{type:"void",title:"{{$t('bidMod.management')}}","x-component-props":{"@click":expression(`({ row }) => {
                    console.log(row.contractType, 'contractType')
                    if (row.contractType == 'TERMINATION') {
                      $goToTermination(row, 2)
                    } else {
                      $editOne(row, 'management')
                    }
                  }`)},"x-reactions":changeFieldVisibleByDeps([".contractStatus"],"$detectContractInformationRightByRow($table.getRowByIndex($self.index))")},stopEdit:{type:"void",title:"{{$t('bidMod.management')}}","x-component-props":{"@click":expression("({ row }) => $goToTermination(row, 1)")},"x-reactions":changeFieldVisibleByDeps([".contractStatus",".contractType"],`(
                      (
                        // 这部分逻辑可以提取出去
                        $buyer() &&
                        (
                          ['DRAFT', 'REJECTED', 'REFUSED', 'WITHDRAW'].includes($deps[0]) &&
                          $deps[1] == 'TERMINATION'
                        ) &&
                        $createdUserIsCurrentUserByRow($table.getRowByIndex($self.index))
                      ) ||
                      ($vendor() && $deps[0] === 'SUPPLIER_CONFIRMING')
                    ) &&
                    $deps[1] === 'TERMINATION'
                  `)},publish:{type:"void",title:"{{$t('common.publish')}}","x-component-props":{"@click":expression(`({ row }) => {
                    $queryEngine.request.save(row.contractHeadId, { customizeAction: 'publish' })
                      .then(() => {
                        $message.success($t('common.successPublish'))
                        $queryEngine.state.paginationManagement.refresh()
                      })
                  }`)},"x-reactions":changeFieldVisibleByDeps([".contractStatus"],"$buyer() && $deps[0] === 'UNPUBLISHED' && $createdUserIsCurrentUserByRow($table.getRowByIndex($self.index))")},terminationContract:{type:"void",title:i18nExpression("contractMod.terminationContract"),"x-component-props":{"@click":expression("({ row }) => $goToTermination(row, 2)")},"x-reactions":changeFieldVisibleByDeps([".contractStatus"],"$buyer() && ['ARCHIVED'].includes($deps[0])")},approve:{type:"void",title:i18nExpression("common.approve"),"x-component-props":{type:"text","@click":expression("({ row }) => $readOne(row, '', 'approve')")},"x-reactions":changeFieldVisibleByDeps([".contractStatus"],"$buyer() && ['UNDER_REVIEW', 'SUPPLIER_CONFIRMED'].includes($deps[0])")},archive:{type:"void",title:i18nExpression("contractMod.archive"),"x-component-props":{type:"text","@click":expression(`({ row }) => {
                    $archiveOne(row)
                    // $form.query('contractFilingDialog').take().setComponentProps({ visible: true })
                    // setTimeout(() => {
                    //   const field = $form.query('contractFilingDialog.fileInfo').take()

                    //   // 重置
                    //   field.data = {}
                    //   field.data.contractHeadId = row.contractHeadId
                    //   field.data.fileuploadId = ''
                    //   field.data.fileSourceName = ''
                    // })
                  }`)},"x-reactions":changeFieldVisibleByDeps([".contractStatus"],"$buyer() && $deps[0] === 'UN_ARCHIVED' && $createdUserIsCurrentUserByRow($table.getRowByIndex($self.index))")},delete:{type:"void",title:"{{$t('common.delete')}}","x-reactions":changeFieldVisibleByDeps([".contractStatus"],"['DRAFT', 'ABANDONED'].includes($deps[0]) && $createdUserIsCurrentUserByRow($table.getRowByIndex($self.index))"),"x-component-props":{popconfirm:{title:i18nExpression("common.confirmDeleteRow")},"@click":expression(`
                    ({ row }) => $queryEngine.request.delete(row.contractHeadId)
                        .then(() => {
                          $message.success($t('common.successDelete'))
                          $queryEngine.state.paginationManagement.refresh()
                        })

                  `)}},abandon:{type:"void",title:i18nExpression("common.abandon"),"x-component-props":{"@click":expression(`({ row }) => {
                    return $queryEngine.request.save({
                      contractHeadId: row.contractHeadId,
                      contractStatus: 'ABANDONED',
                    }).then(() => {
                      $message.success($t('common.successAbandon'))
                      $queryEngine.state.paginationManagement.refresh()
                    })
                  }`)},"x-reactions":changeFieldVisibleByDeps([".contractStatus"],"['REJECTED', 'REFUSED'].includes($deps[0]) && $createdUserIsCurrentUserByRow($table.getRowByIndex($self.index))")},signatures:{type:"void",title:"电子签章","x-component-props":{"@click":expression(`({row}) => {
                    $form.query('signDialog').take().setComponentProps({visible:true})
                    $form.query('state').get('data').signRow = row
                  }`)},"x-reactions":changeFieldVisibleByDeps([".contractStatus",".formal",".extContractHandlerAccount"],`['APPROVAL'].includes($deps[0]) &&
                    ['ELECTRONIC_CONTRACT'].includes($deps[1]) && $buyer() &&
                    ($createdUserIsCurrentUserByRow($table.getRowByIndex($self.index)) || $deps[2] === $userInfo.username)
                  `)}}}})}}},bulkMaintainFwAgreementDialog:{type:"void",title:i18nExpression("bidMod.bulkMaintainFwAgreement"),"x-component":"RDialog","x-component-props":{footer:!1},"x-decorator":"QueryEngine","x-query-engine":{service:"cm",type:"ContractHead",transformRequest:expression(`(data, headers) => {
        data.query.vendorId = {}
        data.query.ceeaIfVirtual = {}

        return data
      }`)},properties:{queryData:{type:"object",default:{},"x-query-engine-skip":!0,"x-data":{vendorId:void 0,globalcontractIds:[]},"x-decorator":"FormLayout","x-decorator-props":{layout:"horizontal"},"x-component":"FormGrid","x-component-props":{maxColumns:4,columnGap:32,rowGap:0},properties:{vendorId:{type:"string","x-hidden":!0},vendorName:{type:"string",title:i18nExpression("common.vendor"),"x-decorator":"FormItem","x-component-props":{disabled:!0}},isFrameworkAgreement:{type:"string",title:i18nExpression("contractMod.status"),default:"Y","x-decorator":"FormItem","x-component":"Checkbox","x-component-props":{disabled:!0,trueLabel:"Y",falseLabel:"N"}}}},dialogTable:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",performanceMode:!0,pagination:!1,maxHeight:"45vh"},"x-reactions":expression(`(field) => {
          const queryDataField = field.query('bulkMaintainFwAgreementDialog.queryData').take()

          $effect(() => {
            if (queryDataField.value.vendorName) {
              $queryEngine.state.paginationManagement.configState.value.pageSize = 999
              $queryEngine.state.paginationManagement.queryParams.value = {
                vendorId: { eq: queryDataField.data.vendorId },
                isFrameworkAgreement: { eq: queryDataField.value.isFrameworkAgreement },
                vendorName: { eq: queryDataField.value.vendorName },
                contractStatus: { eq: 'ARCHIVED' },
              }

              $queryEngine.state.paginationManagement.refresh()
            }
          }, [queryDataField.data.vendorId])
        }`),properties:generateXindexInOrder({vendorId:{type:"string","x-hidden":!0},contractCode:{type:"string",title:i18nExpression("contractMod.contractCode"),"x-render-table-column":{}},contractName:{type:"string",title:i18nExpression("contractMod.contractName"),"x-render-table-column":{}},contractHeadId:{type:"string"},creationDate:{...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.creationDate, '{y}-{m}-{d}')
              }`)},"x-query-engine-sort":"desc"},operation:{type:"void",title:i18nExpression("common.operation"),"x-render-table-column":{performanceMode:!1,sortable:!1,width:60},properties:{save:{type:"void",title:i18nExpression("common.save"),"x-component":"TableButton","x-component-props":{type:"text","@click":expression(`({ row }) => {
                  const queryDataField = $form.query('bulkMaintainFwAgreementDialog.queryData').take()
                     $queryEngine.request.save({
                        contractHeadId: row.contractHeadId,
                        contractCode: row.contractCode,
                        contractName: row.contractName,
                        contractIds: $form.query('bulkMaintainFwAgreementDialog.queryData')
                          .take().data.globalcontractIds,
                        vendorId: queryDataField.data.vendorId
                      }, { customizeAction: 'bulkMaintenanceFramework' }).then(() => {
                        $message.success($t('common.success'))
                        $closed()

                        $bus.$emit('ContractHead')
                      })
                    }`)}}}}})}}},contractFilingDialog:{type:"void",title:i18nExpression("contractMod.contractFiling"),"x-component":"RDialog","x-component-props":{beforeClose:expression(`(done, type, closeLoading) => {
        if (type !== 'ok') {
          done()
          return
        }

        const fieldData = $self.query('contractFilingDialog.fileInfo').get('data')

        if (!fieldData.fileuploadId) {
          $message.error($t('contractMod.msgUploadFile'))
          closeLoading()
          return
        }

        $contractManagement.contract
          .paperArchiveConfirm(fieldData)
          .then((data) => {
            $message.success($t('contractMod.archiveConfirmSuccess'))

            done()

            $bus.$emit('ContractHead')
          })
          .catch((err) => {
            console.log(err)
          })
      }`)},properties:{fileInfo:{type:"object",title:i18nExpression("contractMod.bothPartiesUpload"),"x-decorator":"FormItem","x-component":"SrmCommonFile","x-component-props":{extraData:{fileModular:"cm",fileFunction:"contractManager",fileType:"excel/word"},readonly:!1,defaultFile:{fileId:expression("$self.data && $self.data.fileuploadId"),fileName:expression("$self.data && $self.data.fileSourceName")},"@on-change":expression(`({ file }) => {
            const { fileId = '', fileName = '', fileType = '' } = file || {}
            $self.data.fileuploadId = fileId.toString()
            $self.data.fileSourceName = fileName
            $self.data.fileType = fileType
          }`)}}}},adjustQuery:{type:"void","x-component":"QueryEngine","x-query-engine":{service:"cm",type:"ContractHead"},properties:{operatorAdjustDialog:{type:"void",title:"经办人选择","x-component":"RDialog","x-component-props":{class:"the-adjust-dialog",size:"small","close-on-click-modal":!1,beforeClose:expression(`(done,type,closeLoading) => {
            if(type === 'ok'){
              $self.query('adjustQuery.operatorAdjustDialog.form').take().submit(values => {
                console.log('values:::',values)
                $adjustDialogConfirm($form,values,$message,$confirm,$queryEngine,done,closeLoading).catch(() => {
                  closeLoading()
                })
              }).catch(() => {
                closeLoading()
              })
            }else{
                done()
              }
            }`)},properties:{form:{type:"object","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:2,columnGap:32,rowGap:0},properties:generateXindexInOrder({extContractHandlerName:{type:"string",title:"经办人","x-decorator":"FormItem",required:!0,"x-component":"QuickSearchWrapper","x-component-props":{name:"scc_rbac_user_display",showKey:"nickname",propKey:"nickname","@close-quicksearch":expression(`(val) => {
                    console.log('val',val)
                    $self.query('adjustQuery.operatorAdjustDialog.form').take().value.extContractHandlerName = val ? val.nickname : null
                    $self.query('adjustQuery.operatorAdjustDialog.form').take().value.extContractHandlerAccount = val ? val.username : null
                    $self.query('adjustQuery.operatorAdjustDialog.form').take().value.extContractHandlerId = val ? val.userId : null
                  }`)}}})}}}}},responsiblityQuery:{type:"void","x-component":"QueryEngine","x-query-engine":{service:"cm",type:"ContractHead"},properties:{reponsiblityAdjustDialog:{type:"void",title:"招标专家选择","x-component":"RDialog","x-component-props":{class:"the-adjust-dialog",size:"small","close-on-click-modal":!1,beforeClose:expression(`(done,type,closeLoading) => {
            if(type === 'ok'){
              $self.query('responsiblityQuery.reponsiblityAdjustDialog.form').take().submit(values => {
                console.log('values:::',values)
                $reponsiblityDialogConfirm($form,values,$message,$confirm,$queryEngine,done,closeLoading).catch(() => {
                  closeLoading()
                })
              }).catch(() => {
                closeLoading()
              })
            }else{
                done()
              }
            }`)},properties:{form:{type:"object","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:2,columnGap:32,rowGap:0},properties:generateXindexInOrder({extContractHandlerName:{type:"string",title:"招标专家","x-decorator":"FormItem",required:!0,"x-component":"QuickSearchWrapper","x-component-props":{name:"scc_rbac_user_display",showKey:"nickname",propKey:"nickname","@close-quicksearch":expression(`(val) => {
                    console.log('val',val)
                    $self.query('responsiblityQuery.reponsiblityAdjustDialog.form').take().value.extInviteHeadName = val ? val.nickname : null
                    $self.query('responsiblityQuery.reponsiblityAdjustDialog.form').take().value.extInviteHeadAccount = val ? val.username : null
                    $self.query('responsiblityQuery.reponsiblityAdjustDialog.form').take().value.extInviteHeadId = val ? val.userId : null
                  }`)}}})}}}}},signQuery:{type:"void","x-component":"QueryEngine","x-query-engine":{service:"cm",type:"ContractHead"},properties:{signDialog:{type:"void",title:"电子签章","x-component":"RDialog","x-component-props":{size:"middle","close-on-click-modal":!1,beforeClose:expression(`(done,type,closeLoading) => {
            if(type === 'ok'){
              $self.query('signQuery.signDialog.form').take().submit(values => {
                console.log('values:::',values)
                $signDialogConfirm(values,$form,$message,closeLoading,done,$queryEngine)
              })
            }else{
              done()
            }
          }`)},properties:{form:{type:"object","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:2,columnGap:32,rowGap:0},properties:generateXindexInOrder({extStampSignSeq:{type:"string",title:"盖章顺序","x-decorator":"FormItem",required:!0,default:"VENDOR_FIRST","x-component":"DictSelect","x-component-props":{code:"EXT_CONTRACT_SIGN_SEQ",disabled:expression("!!$form.query('state').get('data').signRow.stampContractFileuploadId")}},signLocation:{type:"void",title:"设置盖章位置","x-query-engine-skip":!0,"x-decorator":"FormItem","x-component":"Button","x-content":"点击设置","x-component-props":{type:"primary",style:{width:"100%"},"@click":expression(`() => {
                    $setSignLocatiton($self,$form,$message)
                  }`)}}})}}}}},contractDetailDialog:{type:"void","x-decorator":"QueryEngine","x-component":"ContractDetailDialog","x-component-props":{visible:expression("$form.query('state').get('data').contractDialogVisible"),mode:expression("$form.query('state').get('data').contractDialogMode"),"@close":expression(`() => {
        $form.query('state').get('data').contractDialogVisible = false
      }`),"@confirm":expression(`(data) => {
        console.log('$$$',data)
      }`)}}}),{emitTabAdd,createdUserIsCurrentUserByRow,buyer,vendor,t,app,getCurrentUserInfo}=usePageHelper();onActivated(()=>{const{from,funName,formId,formNo}=app.$route.params;from==="fromFun"&&funName==="contractMaintainList"&&$editOne({contractHeadId:formId,contractNo:formNo})});const $userInfo=getCurrentUserInfo(),$goToTermination=(row,num=1)=>{let str=num==1?"edit":"termination";emitTabAdd({component:contractInformation,params:{termination:!0,flag:str,row,isReadOnly:!1,contractType:"TERMINATION"},title:row.contractName,name:"termination"+row.contractName})},$detectContractInformationRightByRow=row=>(["DRAFT","REJECTED","REFUSED","WITHDRAW","SUPPLIER_REJECTED"].includes(row.contractStatus)&&buyer()&&(createdUserIsCurrentUserByRow(row)||row.extContractHandlerAccount===$userInfo.username)||vendor()&&row.contractStatus==="SUPPLIER_CONFIRMING")&&row.contractType!=="TERMINATION",$readOne=(row,types,type)=>{let flag=types=="view"?"view":"edit";if(row.contractType=="TERMINATION"){emitTabAdd({component:contractInformation,params:{termination:!0,flag,row,isReadOnly:!0,contractType:"TERMINATION"},title:row.contractName,name:"termination"+row.contractName});return}emitTabAdd({component:contractInformation,params:{flag,row,isReadOnly:!$detectContractInformationRightByRow(row),buttonType:type},title:row.contractName,name:"contractInformation"+row.contractName})},$editOne=(row,buttonType)=>{emitTabAdd({component:contractInformation,params:{flag:"edit",row,isReadOnly:!1,buttonType},contractType:row.contractType,title:row.contractName,name:"contractInformation"+row.contractName})},$addOne=useDebounceFn(async(contractType,selectedRows=[])=>{let rowId=null,contractOldCode=null,mainContractNo=null,isReadOnly=!1;if(contractType!=="MIAN_CONTRACT_ADD"){const name=t(contractType==="MIAN_CONTRACT_ALTER"?"contractMod.changeInContract":"contractMod.supplementalAgreement");if(!selectedRows.length){app.$message.warning(t("contractMod.msgContractManage[0]")+`${name}`);return}if(selectedRows.length>1){app.$message.warning(t("contractMod.msgContractManage[1]")+`${name}`+t("contractMod.msgContractManage[2]"));return}if(selectedRows[0].contractStatus!=="ARCHIVED"){app.$message.warning(t("contractMod.msgContractManage[3]")+`${name}`);return}rowId=selectedRows[0].contractHeadId,contractOldCode=selectedRows[0].contractCode,mainContractNo=selectedRows[0].contractNo}emitTabAdd({component:contractInformation,params:{flag:"add",rowId,contractType,contractOldCode,mainContractNo,isReadOnly,row:selectedRows},title:t("contractMod.createContract"),name:"contractInformation"})},216),$addContractOne=async(type,$form,$queryEngine,val)=>{if(!val.length)return app.$message.warngin("请勾选数据");let orgOuIdList=val.map(item=>item.orgOuId),vendorIdList=val.map(item=>item.vendorId);if(Array.from(new Set(orgOuIdList)).length>1)return app.$message.warning("同一公司才能一起创建");if(Array.from(new Set(vendorIdList)).length>1)return app.$message.warning("同一供应商才能一起创建");let response;if(type==="temp"){let fixPriceLineIdList=val.filter(item=>item.fixPriceLineId).map(item=>item.fixPriceLineId);if(!fixPriceLineIdList.length)return;response=await app.$http({url:"/api-cm/contractHead/ext/createTempProcure",method:"POST",data:fixPriceLineIdList,loading:!0})}else{let purFixPriceLineIdList=val.filter(item=>item.purFixPriceLineId).map(item=>item.purFixPriceLineId);if(!purFixPriceLineIdList.length)return;response=await app.$http({url:"/api-cm/contractHead/ext/createCentPurchase",method:"POST",data:purFixPriceLineIdList,loading:!0})}if(response&&response.data&&response.data.length){let contractHeadId=response.data[0];$editOne({contractHeadId})}},$setSignLocatiton=async($self,$form,$message)=>{let extStampSignSeq=$self.query(".extStampSignSeq").take().value;const row=$form.query("state").get("data").signRow,{contractHeadId}=row;if(!extStampSignSeq)return $message.warning("请选择盖章顺序");const response=await app.$http({url:"/api-cm/contractInterface/ext/getUrlById",method:"GET",params:{contractHeadId,extStampSignSeq},loading:!0});if(response&&response.data){let href=response.data;window.open(href,"_blank")}},$signDialogConfirm=async(values,$form,$message,closeLoading,done,$queryEngine)=>{const row=$form.query("state").get("data").signRow,{contractHeadId}=row;await app.$http({url:"/api-cm/contractInterface/ext/confirm",method:"GET",params:{contractHeadId,...values},loading:!0}).finally(()=>{closeLoading()})&&(done(),app.$message.success(t("common.success")),$queryEngine.state.paginationManagement.refresh())},$archiveOne=row=>{emitTabAdd({component:contractInformation,params:{flag:"archive",row,isReadOnly:!0},title:row.contractName,name:"contractInformation"+row.contractName})},$createdUserIsCurrentUserByRow=row=>createdUserIsCurrentUserByRow(row)||row.extContractHandlerAccount===$userInfo.username,scope={$buyer:buyer,$vendor:vendor,$goToTermination,$contractManagement:contractManagement,$addOne,$readOne,$editOne,$prompt:app.$prompt,$detectContractInformationRightByRow,$createdUserIsCurrentUserByRow,$reactiveAction:action,$addContractOne,$adjustDialogConfirm,$signDialogConfirm,$setSignLocatiton,$archiveOne,$userInfo,$reponsiblityDialogConfirm,app};return{__sfc:!0,generateAddOneEventExpression,$adjustDialogConfirm,$reponsiblityDialogConfirm,schema,emitTabAdd,createdUserIsCurrentUserByRow,buyer,vendor,t,app,getCurrentUserInfo,$userInfo,$goToTermination,$detectContractInformationRightByRow,$readOne,$editOne,$addOne,$addContractOne,$setSignLocatiton,$signDialogConfirm,$archiveOne,$createdUserIsCurrentUserByRow,scope,components:{ContractDetailDialog},RenderEngine}}});var _sfc_render$1=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{pageAttrs:_vm.$attrs,components:_setup.components,schema:_setup.schema,scope:_setup.scope,schemaKey:"ContractHead"}})},_sfc_staticRenderFns$1=[],__component__$1=normalizeComponent(_sfc_main$1,_sfc_render$1,_sfc_staticRenderFns$1,!1,null,"fe62dec4",null,null);const contractListEngine=__component__$1.exports,_sfc_main={name:"ContractMaintainList",components:{NavTabs},data(){return{activeTab:"contractListEngine",tabs:[{title:this.$t("route.contractMaintainList"),name:"contractListEngine",component:contractListEngine,closable:!1}]}}};var _sfc_render=function(){var _vm=this,_c=_vm._self._c;return _c("NavTabs",{ref:"tabs",attrs:{"tabs-list":_vm.tabs,"cur-tab":_vm.activeTab}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const index=__component__.exports;export{index as default};
