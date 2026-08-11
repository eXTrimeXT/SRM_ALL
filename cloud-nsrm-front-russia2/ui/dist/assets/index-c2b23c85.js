import{N as NavTabs}from"./index-9a7f2446.js";import{ak as defineComponent,al as usePageHelper,aq as defineSchemas,ad as expression,ah as generateXindexInOrder,ca as buttonListItemVisibleByPermission,bS as exportExcelSegment,c0 as queryFieldValueExpression,bT as queryFieldStatePropertyExpression,ae as i18nExpression,ai as editTableFormItemValid,af as yearMonthDaySelectorSegment,bD as changeFieldVisibleByDeps,aD as requiredValidatorSegment,ar as RenderEngine,n as normalizeComponent}from"./index-6b6051d8.js";const _sfc_main$1=defineComponent({__name:"list-engine",setup(__props){const{emitTabAdd,t:$t,app}=usePageHelper(),$submitData=($form,$queryEngine,$table,$index,row)=>{$form.validate().then(()=>{$queryEngine.request.save(row,{query:{"*":{}},loading:!0}).then(res=>{res.data.length&&app.$message.success($t("common.success")),$table.cancelEditRow($index),$queryEngine.state.paginationManagement.refresh()})}).catch(err=>{app.$message.warning($t("common.pleasefinishRequired"))})},$approve=($form,$queryEngine)=>{let selects=$form.query("table").take().componentProps.componentInstance.getCheckboxRecords();if(selects.some(item=>item.approveStatus!="DRAFT")||!selects.length)return app.$message.warning("请选择状态为拟定的数据");app.$confirm($t("是否确认提交审批"),"提示",{confirmButtonText:"确定",cancelButtonText:"取消",type:"warning"}).then(()=>{let payload=selects.map(row=>row.basicPriceId);app.$http({url:"/api-cost/reduce/basic/price/submitApproval",method:"POST",data:{basicPriceIds:payload},loading:!0}).then(res=>{app.$message.success($t("common.success")),$queryEngine.state.paginationManagement.refresh()})}).catch(err=>{})},$getFirstDraft=($form,$queryEngine,$values,done,$bus)=>{$form.query("*.Dialog.form").take().submit(values=>{app.$http({url:"/api-cost/reduce/basic/price/generate",method:"POST",data:{...values},loading:!0}).then(res=>{app.$message.success($t("数据生成成功，请前往“配置中心-导出中心”进行下载")),$bus.$emit("ReduceBasePrice"),done()}).catch(()=>{done()})})},$delete=($form,$queryEngine)=>{let selects=$form.query("table").take().componentProps.componentInstance.getCheckboxRecords();selects.some(item=>item.approveStatus!="DRAFT")&&app.$message.warning("请选择状态为拟定的数据"),app.$confirm($t("是否确认删除"),"提示",{confirmButtonText:"确定",cancelButtonText:"取消",type:"warning"}).then(()=>{let payload=selects.map(row=>({basicPriceId:row.basicPriceId}));$queryEngine.request.delete(payload,{loading:!0}).then(res=>{app.$message.success($t("common.successDelete")),$queryEngine.state.paginationManagement.refresh()})}).catch(err=>{})},schema=defineSchemas({ReduceBasicPrice:{type:"void","x-decorator":"QueryEngine","x-component":"el-container","x-component-props":{class:"flex-container",direction:"vertical"},"x-query-engine":{service:"cost",actions:{paginationQuery:{immediate:!0,transformRequest:expression(`(data, headers) => {
            data.query['*'] = {}
            return data
          }`),transformResponse:res=>{const data=JSON.parse(res);return data.data?.ref?.ReduceBasicPrice&&Object.keys(data.data.ref.ReduceBasicPrice??{}).forEach(key=>{const item=data.data.ref.ReduceBasicPrice[key];item.reduceYear=String(item.reduceYear)}),data},onSuccess:expression(`(res) => {
            console.log('success',$form.query('ReduceBasicPrice.table').take())
          }`)}}},properties:{bus:{type:"void","x-component":"BusEvent","x-component-props":{eventName:"ReduceBasePrice","@listener":expression(`() => {
            $queryEngine.state.paginationManagement.refresh()
          }`)}},query:{type:"object","x-query-engine-skip":!0,"x-component":"QueryFormByQueryEngine",properties:generateXindexInOrder({materialCode:{type:"string",title:"{{$t('reduce.materialCode')}}","x-query-engine-query-operator":"contains"},orgId:{type:"string",title:"{{$t('common.orgId')}}","x-component":"OrganizationSelector","x-component-props":{"parent-id":-1,"node-type":"OU","select-type":"input",placeholder:"{{$t('common.pleaseSelect')}}",multiple:!1,"@select":expression(`(node) => {
                  if (!$form.values.query.invOrgId) return
                  $form.values.query.invOrgId = null
              }`)}},invOrgId:{type:"string",title:"{{$t('common.invOrg')}}","x-component":"OrganizationSelector","x-component-props":{"node-type":"INV","select-type":"input",placeholder:"{{$t('common.pleaseSelect')}}",multiple:!1,disabled:expression("!$form.values.query.orgId"),"parent-id":expression("$form.values.query.orgId")}},carCode:{type:"string",title:"{{$t('reduce.carCode')}}","x-component":"QuickSearchWrapper","x-component-props":{name:"scc_cost_car",preQueryData:expression("{'t.car_level': 2}"),showKey:"carCode",propKey:"carCode"}},approveStatus:{type:"string",title:"{{$t('reduce.approveStatus')}}","x-component":"DictSelect","x-component-props":{code:"APPROVE_STATUS"}},reduceYear:{type:"string",title:"{{$t('reduce.reduceYear')}}","x-component":"DatePicker","x-component-props":{type:"year","value-format":"yyyy"}}})},toolbar:{type:"void","x-component":"Space","x-component-props":{style:"margin-bottom:16px;"},properties:{getFirstDraft:{type:"void",title:"{{$t('reduce.getFirstDraft')}}","x-component":"RButton","x-component-props":{type:"primary",...buttonListItemVisibleByPermission("reduce:reduceBasePrice:getFirstDraft"),visible:expression("false"),"@click":expression(`() => {
                $form.query('Dialog').take().setComponentProps({ visible: true })
                setTimeout(() => {
                  $form.query('form').take().reset()
                })
              }`)}},add:{type:"void",title:"{{$t('common.add')}}","x-component":"RButton","x-component-props":{type:"primary",...buttonListItemVisibleByPermission("reduce:reduceBasePrice:add"),"@click":expression(`() => {
                $form.query("table").take().componentProps.componentInstance.addRow("unshift",{approveStatus: 'DRAFT'})
              }`)}},delete:{type:"void",title:"{{$t('common.delete')}}","x-component":"RButton","x-component-props":{type:"primary",...buttonListItemVisibleByPermission("reduce:reduceBasePrice:delete"),"@click":expression(`() => {
                $delete($form,$queryEngine)
              }`)}},exportExcel:{type:"void","x-component":"ExportExcel","x-component-props":{type:"default",...buttonListItemVisibleByPermission("reduce:reduceBasePrice:export"),pageUrl:"/api-cost/api-ql/ReduceBasicPrice/query",...exportExcelSegment,meiqlKey:"ReduceBasicPrice",filterParams:queryFieldValueExpression("query"),tableHeader:queryFieldStatePropertyExpression("ReduceBasicPrice.table","data.columns"),dictCodes:{approveStatus:"APPROVE_STATUS"}}},importExcel:{type:"void","x-component":"ImportExcel","x-component-props":{title:"{{$t('common.import')}}",type:"default",...buttonListItemVisibleByPermission("reduce:reduceBasePrice:import"),extraData:{fileModular:"sup",fileFunction:"purchaseCatalog",fileType:"excel"},upLoadUrl:"/api-cost/reduce/basic/price/importExcel",downloadTemplateOptions:{downloadUrl:"/api-cost/reduce/basic/price/exportExcelTemplate",fileName:"{{$t('logisticsMod.importTemplateXLSX')}}"},"@handleSuccess":expression(`() => {
                $bus.$emit('ReduceBasePrice')
              }`)}},approve:{type:"void",title:"{{$t('reduce.approve')}}","x-component":"RButton","x-component-props":{type:"primary",...buttonListItemVisibleByPermission("reduce:reduceBasePrice:approve"),"@click":expression(`() => {
                $approve($form,$queryEngine)
              }`)}}}},table:{type:"array","x-component":"RenderTable","x-component-props":{class:"table-view-vxe-table",style:"flex: 1",preColumns:"checkbox,seq",openCustomTable:!0,editMode:"multi-row"},properties:generateXindexInOrder({basicPriceId:{type:"number","x-hidden":!0,"x-query-engine-primary-key":!0},reduceYear:{"x-render-table-column":{title:i18nExpression("reduce.reduceYear"),minWidth:120},"x-reactions":expression(`(field) => {
                let isRowEditable = $table.getSelfRowEditable($self)
              setTimeout(()=>{
                $self.editable = $table.getRowByIndex($self.index)?.approveStatus == 'DRAFT' && isRowEditable
              })
            }`),"x-decorator":"FormItem",...editTableFormItemValid,type:"date",default:null,"x-component-props":{type:"year",placeholder:i18nExpression("common.pleaseSelectDate"),format:"yyyy","value-format":"yyyy"}},orgId:{type:"string",...editTableFormItemValid,"x-component":"OrganizationSelector","x-component-props":{"parent-id":-1,"node-type":"OU","select-type":"input",placeholder:"{{$t('common.pleaseSelect')}}",multiple:!1,"@select":expression(`(node, val) => {
                const row = $table.getRowByIndex($self.index)
                const { organizationId = '', organizationCode = '', organizationName = '' } = node || {}

                if (val && row.orgId === organizationId) {
                  // 避免重复执行
                  return
                }
                row.orgId = organizationId
                row.orgCode = organizationCode
                row.orgName = organizationName
                console.log('!!!!!!!!!  row  :', row)
                // 清空库存组织
                row.invOrgId = ''
                row.invOrgCode = ''
                row.invOrgName = ''

              }`)},"x-reactions":expression(`(field) => {
                let isRowEditable = $table.getSelfRowEditable($self)
              setTimeout(()=>{
                $self.editable = $table.getRowByIndex($self.index)?.approveStatus == 'DRAFT' && isRowEditable
              })
            }`),"x-render-table-column":{title:"{{$t('common.orgId')}}",minWidth:160}},invOrgId:{type:"string",...editTableFormItemValid,"x-component":"OrganizationSelector","x-component-props":{"node-type":"INV","select-type":"input",placeholder:"{{$t('bid_mod.inv')}}",multiple:!1,parentId:expression("$table.getRowByIndex($self.index)?.orgId || '' "),"@select":expression(`(node, val) => {
                const row = $table.getRowByIndex($self.index)
                const { organizationId = '', organizationCode = '', organizationName = '' } = node || {}
                if (val && row.invOrgId === organizationId) {
                  // 避免重复执行
                  return
                }
                row.invOrgId = organizationId
                row.invOrgCode = organizationCode
                row.invOrgName = organizationName
              }`)},"x-reactions":expression(`(field) => {
                let isRowEditable = $table.getSelfRowEditable($self)
              setTimeout(()=>{
                $self.editable = $table.getRowByIndex($self.index)?.approveStatus == 'DRAFT' && isRowEditable
              })
            }`),"x-render-table-column":{title:"{{$t('common.invOrg')}}",minWidth:160}},carCode:{type:"string",...editTableFormItemValid,title:"{{$t('reduce.carCode')}}","x-component":"QuickSearchWrapper","x-component-props":{showKey:"carCode",propKey:"carCode",name:"scc_cost_car",preQueryData:expression("{'t.car_level': 2}"),"@close-quicksearch":expression(`(val, scope) => {
                const row = $table.getRowByIndex($self.index)
                row.carId = val ? val.carId : ''
                row.carCode = val ? val.carCode : ''
                row.carName = val ? val.carName : ''
                row.parentCarId = val ? val.parentCarId : ''
                row.parentCarCode = val ? val.parentCarCode : ''
                row.parentCarName = val ? val.parentCarName : ''
              }`)},"x-reactions":expression(`(field) => {
                let isRowEditable = $table.getSelfRowEditable($self)
              setTimeout(()=>{
                $self.editable = $table.getRowByIndex($self.index)?.approveStatus == 'DRAFT' && isRowEditable
              })
            }`),"x-render-table-column":{minWidth:100}},materialCode:{type:"string",...editTableFormItemValid,title:"{{$t('reduce.materialCode')}}","x-component":"QuickSearchWrapper","x-component-props":{name:"scc_cost_reduce_material",showKey:"materialCode",propKey:"materialCode","@close-quicksearch":expression(`(val, scope) => {
                const row = $table.getRowByIndex($self.index)
                row.materialName = val ? val.materialName : ''
                row.materialId = val ? val.materialId : ''
                row.materialCode = val ? val.materialCode : ''

                // 带出其他值
                row.categoryName = val ? val.categoryName : ''
                row.categodryId = val ? val.categodryId : ''
                row.categodryCode = val ? val.categodryCode : ''

                row.priceType = val ? val.priceType : ''
                row.referBasicPointPrice = val ? val.referBasicPointPrice : ''
                row.effectiveDate = val ? val.effectiveDate : ''
                row.expirationDate = val ? val.expirationDate : ''
                // 装配系数
                row.assembleCoefficient = val ? val.assembleCoefficient : ''

              }`)},"x-reactions":expression(`(field) => {
              let isRowEditable = $table.getSelfRowEditable($self)
              setTimeout(()=>{
                $self.editable = $table.getRowByIndex($self.index)?.approveStatus == 'DRAFT' && isRowEditable
              })
            }`),"x-render-table-column":{minWidth:150}},materialName:{type:"string","x-render-table-column":{title:"{{$t('reduce.materialName')}}",minWidth:120,skipEditable:!0}},vendorCode:{type:"string",...editTableFormItemValid,title:"{{$t('reduce.vendorCode')}}","x-component":"QuickSearchWrapper","x-component-props":{name:"scc_sup_company_info_all",showKey:"companyCode",propKey:"companyCode","@close-quicksearch":expression(`(val, scope) => {
                const row = $table.getRowByIndex($self.index)
                row.vendorName = val ? val.companyName : ''
                row.vendorId = val ? val.companyId : ''
                row.vendorCode = val ? val.companyCode : ''
              }`)},"x-reactions":expression(`(field) => {
                let isRowEditable = $table.getSelfRowEditable($self)
              setTimeout(()=>{
                $self.editable = $table.getRowByIndex($self.index)?.approveStatus == 'DRAFT' && isRowEditable
              })
            }`),"x-render-table-column":{title:"{{$t('reduce.vendorCode')}}",minWidth:150}},vendorName:{type:"string","x-render-table-column":{title:"{{$t('reduce.vendorName')}}",minWidth:120,skipEditable:!0}},categoryName:{type:"string","x-render-table-column":{title:"{{$t('reduce.categoryName')}}",minWidth:120,skipEditable:!0}},priceType:{type:"string","x-component":"DictSelect","x-component-props":{code:"PRICE_TYPE"},"x-reactions":expression(`(field) => {
                let isRowEditable = $table.getSelfRowEditable($self)
              setTimeout(()=>{
                $self.editable = $table.getRowByIndex($self.index)?.approveStatus == 'DRAFT' && isRowEditable
              })
            }`),"x-render-table-column":{title:"{{$t('reduce.priceType')}}",minWidth:120,skipEditable:!0}},referBasicPointPrice:{type:"string","x-reactions":expression(`(field) => {
                let isRowEditable = $table.getSelfRowEditable($self)
              setTimeout(()=>{
                $self.editable = $table.getRowByIndex($self.index)?.approveStatus == 'DRAFT' && isRowEditable
              })
            }`),"x-render-table-column":{title:"{{$t('reduce.referBasicPointPrice')}}",minWidth:120,skipEditable:!0}},effectiveDate:{title:"{{ $t('reduce.effectiveDate') }}",...yearMonthDaySelectorSegment,"x-reactions":expression(`(field) => {
                let isRowEditable = $table.getSelfRowEditable($self)
              setTimeout(()=>{
                $self.editable = $table.getRowByIndex($self.index)?.approveStatus == 'DRAFT' && isRowEditable
              })
            }`),"x-render-table-column":{width:150,skipEditable:!0}},expirationDate:{title:"{{ $t('reduce.expirationDate') }}",...yearMonthDaySelectorSegment,"x-reactions":expression(`(field) => {
                let isRowEditable = $table.getSelfRowEditable($self)
              setTimeout(()=>{
                $self.editable = $table.getRowByIndex($self.index)?.approveStatus == 'DRAFT' && isRowEditable
              })
            }`),"x-render-table-column":{width:150,skipEditable:!0}},assembleCoefficient:{type:"string","x-render-table-column":{title:"{{$t('reduce.assembleCoefficient')}}",minWidth:120},"x-reactions":expression(`(field) => {
                let isRowEditable = $table.getSelfRowEditable($self)
              setTimeout(()=>{
                $self.editable = $table.getRowByIndex($self.index)?.approveStatus == 'DRAFT' && isRowEditable
              })
            }`)},confirBasicPointPrice:{type:"string",...editTableFormItemValid,"x-render-table-column":{title:"{{$t('reduce.confirBasicPointPrice')}}",minWidth:120}},approveTime:{title:"{{ $t('reduce.approveTime') }}",...yearMonthDaySelectorSegment,"x-render-table-column":{width:160,skipEditable:!0}},createdFullName:{type:"string","x-render-table-column":{title:"{{$t('common.creator')}}",width:120,skipEditable:!0}},creationDate:{title:"{{ $t('common.creationTime') }}",...yearMonthDaySelectorSegment,"x-render-table-column":{width:150,skipEditable:!0}},lastUpdatedFullName:{type:"string","x-render-table-column":{title:"{{$t('common.updatePeople')}}",width:120,skipEditable:!0}},lastUpdateDate:{"x-query-engine-sort":"desc",...yearMonthDaySelectorSegment,"x-render-table-column":{title:"{{$t('common.updateTime')}}",skipEditable:!0,width:120}},approveStatus:{type:"string","x-component":"DictSelect","x-component-props":{code:"APPROVE_STATUS"},"x-render-table-column":{title:"{{$t('reduce.approveStatus')}}",skipEditable:!0,minWidth:100}},operation:{type:"void",title:"{{$t('common.operation')}}","x-component":"RenderTableButtonList","x-component-props":{max:2},"x-render-table-column":{fixed:"right",width:120},properties:{edit:{type:"void",title:"{{$t('common.edit')}}","x-reactions":changeFieldVisibleByDeps([".approveStatus"],"['DRAFT'].includes($deps[0]) && !$table.getSelfRowEditable($self) "),"x-component-props":{"@click":expression(`({rowIndex}) => {
                    $table.editRowByIndex(rowIndex)
                  }`)}},cancel:{type:"void",title:"{{$t('common.cancel')}}","x-reactions":changeFieldVisibleByDeps([".approveStatus"],"$table.getSelfRowEditable($self) "),"x-component-props":{"@click":expression(`({rowIndex}) => {
                    $table.cancelEditRow(rowIndex)
                  }`)}},save:{type:"void",title:"{{$t('common.save')}}","x-reactions":changeFieldVisibleByDeps([".approveStatus"],"['DRAFT'].includes($deps[0]) && $table.getSelfRowEditable($self) "),"x-component-props":{"@click":expression(`({ row }) => {
                    $submitData($form,$queryEngine,$table,$self.index,row)
                  }`)}}}}})},Dialog:{type:"void",title:i18nExpression("reduce.getFirstDraft"),"x-component":"RDialog","x-component-props":{"close-on-click-modal":!1,destroyOnClose:!0,footerButtonList:expression(`(_, { cancelButton,okButton }) => {
          return [
            cancelButton,
            {
              ...okButton,
              text: '生成数据',
              type:'primary',
            },
          ]
          }`),beforeClose:expression(`(done, type) => {
            if ( type === 'ok') {
              $getFirstDraft($form,$queryEngine,$values,done,$bus)
            } else {
              done()
              }
            }
          `)},properties:{form:{type:"object","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:2,columnGap:32,rowGap:0},properties:{reduceYear:{"x-decorator":"FormItem",title:"{{$t('reduce.reduceYear')}}","x-component":"DatePicker","x-component-props":{type:"year","value-format":"yyyy"},...requiredValidatorSegment},carCode:{type:"string",title:"{{$t('reduce.carCode')}}","x-decorator":"FormItem","x-component":"QuickSearchWrapper","x-component-props":{showKey:"carCode",propKey:"carCode",name:"scc_cost_car",preQueryData:expression("{'t.car_level': 2}"),"@close-quicksearch":expression(`(val, scope) => {
                    $values.form.carId = val ? val.carId : ''
                    $values.form.carCode = val ? val.carCode : ''
                    $values.form.carName = val ? val.carName : ''
                  }`)},...requiredValidatorSegment},orgId:{type:"string",title:"{{$t('common.orgId')}}","x-decorator":"FormItem","x-component":"OrganizationSelector","x-component-props":{"parent-id":-1,"node-type":"OU","select-type":"input",placeholder:"{{$t('common.pleaseSelect')}}",multiple:!1,"@select":expression(`(node, val) => {
                    const row = $form.values.form
                    const { organizationId = '', organizationCode = '', organizationName = '' } = node || {}

                    if (node && row.orgId === organizationId) {
                      // 避免重复执行
                      return
                    }
                    row.orgId = organizationId
                    row.orgCode = organizationCode
                    row.orgName = organizationName
                    // 清空库存组织
                    row.invOrgId = ''
                    row.invOrgCode = ''
                    row.invOrgName = ''

                  }`)}},invOrgId:{type:"string",title:"{{$t('common.invOrg')}}","x-decorator":"FormItem","x-component":"OrganizationSelector","x-component-props":{"node-type":"INV","select-type":"input",placeholder:"{{$t('common.pleaseSelect')}}",multiple:!1,disabled:expression("!$form.values?.form.orgId"),parentId:expression("$form.values?.form.orgId || -1"),"@select":expression(`(node, val) => {

                      const row = $form.values.form
                      const { organizationId = '', organizationCode = '', organizationName = '' } = node || {}

                      if (node && row.orgInvId === organizationId) {
                        // 避免重复执行
                        return
                      }

                      row.invOrgId = organizationId
                      row.invOrgCode = organizationCode
                      row.invOrgName = organizationName

                  }`)}}}}}}}}});return{__sfc:!0,emitTabAdd,$t,app,$submitData,$approve,$getFirstDraft,$delete,schema,components:{},scope:{$delete,$getFirstDraft,$approve,$submitData},RenderEngine}}});var _sfc_render$1=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{pageAttrs:_vm.$attrs,scope:_setup.scope,components:_setup.components,schema:_setup.schema,schemaKey:"reduceBasePrice"}})},_sfc_staticRenderFns$1=[],__component__$1=normalizeComponent(_sfc_main$1,_sfc_render$1,_sfc_staticRenderFns$1,!1,null,null,null,null);const reduceBasePriceList=__component__$1.exports,_sfc_main={name:"ReduceBasePrice",components:{NavTabs},data(){return{activeTab:"reduceBasePriceList",tabs:[{title:this.$t("reduce.reduceBasePrice"),name:"reduceBasePriceList",component:reduceBasePriceList,closable:!1}]}}};var _sfc_render=function(){var _vm=this,_c=_vm._self._c;return _c("NavTabs",{attrs:{"tabs-list":_vm.tabs,"cur-tab":_vm.activeTab}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const index=__component__.exports;export{index as default};
