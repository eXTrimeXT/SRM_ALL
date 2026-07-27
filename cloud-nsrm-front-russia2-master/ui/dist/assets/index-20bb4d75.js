import{N as NavTabs}from"./index-9a7f2446.js";import"./mixins-edc77a54.js";import"./TableView-eb18d7e8.js";/* empty css                                                                   */import{ak as defineComponent,aq as defineSchemas,ad as expression,ah as generateXindexInOrder,ca as buttonListItemVisibleByPermission,ae as i18nExpression,bS as exportExcelSegment,c0 as queryFieldValueExpression,bT as queryFieldStatePropertyExpression,af as yearMonthDaySelectorSegment,bD as changeFieldVisibleByDeps,al as usePageHelper,bB as useDebounceFn,cb as action,ar as RenderEngine,n as normalizeComponent}from"./index-6b6051d8.js";import"./edit.vue_vue_type_style_index_0_scoped_1455836b_lang-c49bc650.js";import"./index-3aa48b38.js";import"./enum-13a06f7c.js";import"./z-material-table-78c4aedd.js";import"./big-e21bdbb6.js";import{c as contractManagement}from"./index-2e6ba8f1.js";import contractInformation from"./edit-engine-7770351a.js";/* empty css                                              */import"./util-1e55288f.js";import"./index-d31c36cb.js";import"./drag-5571e5c7.js";import"./index-e416f1ab.js";import"./axios-cache-plugin-1edad216.js";import"./index-7952cb71.js";import"./index-b545d9fb.js";import"./validate-bf972fb1.js";import"./uniqueId-f496b65a.js";import"./index-2c71d18e.js";import"./number-2d936dc6.js";const _sfc_main$1=defineComponent({__name:"list-engine",setup(__props){const generateAddOneEventExpression=type=>expression(`() => {
  $addOne('${type}', $self.query('ContractHead.table').take().componentProps.componentInstance.getCheckboxRecords())
}`),schema=defineSchemas({ContractHead:{type:"void","x-query-engine":{service:"cm",actions:{paginationQuery:{immediate:!0}}},"x-decorator":"el-container","x-decorator-props":{class:"flex-container",direction:"vertical"},"x-component":"QueryEngine",properties:{bus:{type:"void","x-component":"BusEvent","x-component-props":{eventName:"ContractHead","@listener":expression(`() => {
            $queryEngine.state.paginationManagement.refresh()
          }`)}},query:{type:"object","x-query-engine-skip":!0,"x-component":"QueryFormByQueryEngine",properties:generateXindexInOrder({contractNo:{type:"string",title:"{{$t('contractMod.contractNo')}}","x-query-engine-query-operator":"contains"},sourceNumber:{type:"string",title:"{{$t('bidMod.approvalNo')}}","x-query-engine-query-operator":"contains"},contractType:{type:"string",title:"{{$t('contractMod.operationType')}}","x-component":"DictSelect","x-component-props":{code:"CONTRACT_TYPE"}},contractStatus:{type:"string",title:"{{$t('orderMod.buyerOrderSynergy.contractStatus')}}","x-component":"DictSelect","x-component-props":{code:"CONTRACT_STATUS"}},buId:{type:"string",title:"{{$t('contractMod.buId')}}","x-component":"OrganizationSelector","x-component-props":{multiple:!1}},contractClass:{type:"string",title:"{{$t('contractMod.contractType')}}","x-component":"DictSelect","x-component-props":{code:"ELEM_CONTRACT_TYPE"}},contractName:{type:"string",title:"{{$t('contractMod.contractName')}}","x-query-engine-query-operator":"contains"},vendorId:{type:"string",title:"{{$t('contractMod.vendorName')}}","x-hidden":"{{ $vendor() }}","x-component":"QuickSearchWrapper","x-component-props":{showKey:"companyName",propKey:"companyId",name:"scc_sup_company_info_display_buyer"}},frameworkAgreementCode:{type:"string",title:"{{$t('contractMod.frameworkAgreementCode')}}","x-query-engine-query-operator":"contains"},frameworkAgreementName:{type:"string",title:"{{$t('contractMod.frameworkAgreementName')}}","x-query-engine-query-operator":"contains"},createdBy:{type:"string",title:"{{$t('common.creator')}}","x-query-engine-query-operator":"contains"},formal:{type:"string",title:"{{$t('contractMod.signingMethod')}}","x-component":"DictSelect","x-component-props":{code:"CONTRACT_FORM2"}}})},toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void",title:"{{$t('contractMod.addContract')}}","x-visible":expression("$buyer()"),"x-component-props":{type:"primary",...buttonListItemVisibleByPermission("cm:contractManager:add"),"@click":expression("() => $addOne('MIAN_CONTRACT_ADD')")}},alter:{type:"void",title:"{{$t('contractMod.contractChange')}}","x-visible":expression("$buyer()"),"x-component-props":{...buttonListItemVisibleByPermission("cm:contractManager:alter"),"@click":generateAddOneEventExpression("MIAN_CONTRACT_ALTER")}},contractChange2:{type:"void",title:"{{$t('contractMod.contractChange2')}}","x-visible":expression("$buyer()"),"x-component-props":{"@click":generateAddOneEventExpression("SUPPLEMENTAL_AGREEMENT")}},importExcel:{type:"void","x-component":"ImportExcel","x-visible":expression("$buyer()"),"x-component-props":{title:i18nExpression("common.excelImport"),type:"default",extraData:{fileModular:"cm",fileFunction:"contractMaintainList",fileType:"excel"},upLoadUrl:"/api-cm/contract/contractHead/importExcel",downloadTemplateOptions:{downloadUrl:"/api-cm/contract/contractHead/importModelDownload",fileName:expression("$t('contractMod.contractManageImp')")},"@handleSuccess":expression(`() => {
                $bus.$emit('ContractHead')
              }`)}},exportExcel:{type:"void","x-component":"ExportExcel","x-component-props":{...exportExcelSegment,type:"default",pageUrl:"/api-cm/api-ql/ContractHead/query",filterParams:queryFieldValueExpression("query"),tableHeader:queryFieldStatePropertyExpression("ContractHead.table","data.columns"),dictCodes:{contractStatus:"CONTRACT_STATUS",contractType:"CONTRACT_TYPE",contractClass:"ELEM_CONTRACT_TYPE",formal:"CONTRACT_FORM2"}}},bulkMaintainFwAgreement:{type:"void",title:"{{$t('bidMod.bulkMaintainFwAgreement')}}","x-visible":expression("$buyer()"),"x-component-props":{"@click":expression(`() => {
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
              }`)}}}},table:{type:"array","x-component":"RenderTable","x-component-props":{class:"table-view-vxe-table",style:"flex: 1",preColumns:"checkbox, seq",openCustomTable:!0},properties:generateXindexInOrder({contractHeadId:{type:"string","x-hidden":!0},vendorId:{type:"string","x-hidden":!0},contractNo:{type:"string",title:i18nExpression("contractMod.contractNo"),"x-render-table-column":{minWidth:200}},contractName:{type:"string","x-component":"RenderTableLink","x-component-props":{type:"text","@click":expression("({ row }) => $readOne(row,'view')")},"x-render-table-column":{title:i18nExpression("contractMod.contractName"),minWidth:160,customRender:!0}},sourceNumber:{type:"string",title:"{{$t('bidMod.approvalNo')}}","x-render-table-column":{width:170}},contractStatus:{type:"string",title:"{{$t('common.status')}}","x-component":"DictSelect","x-component-props":{code:"CONTRACT_STATUS"},"x-render-table-column":{width:100}},contractType:{type:"string",title:"{{$t('contractMod.operationType')}}","x-component":"DictSelect","x-component-props":{code:"CONTRACT_TYPE"},"x-render-table-column":{width:120}},contractClass:{type:"string",title:"{{$t('contractMod.contractType')}}","x-component":"DictSelect","x-component-props":{code:"ELEM_CONTRACT_TYPE"},"x-render-table-column":{width:120}},formal:{type:"string",title:"{{$t('contractMod.signingMethod')}}","x-component":"DictSelect","x-component-props":{code:"CONTRACT_FORM2"},"x-render-table-column":{width:120}},buName:{type:"string",title:"{{$t('contractMod.buId')}}","x-render-table-column":{width:150}},vendorCode:{type:"string",title:"{{$t('common.vendorCode')}}","x-render-table-column":{minWidth:130}},vendorName:{type:"string",title:"{{$t('common.vendorName')}}","x-render-table-column":{minWidth:150}},frameworkAgreementCode:{type:"string",title:"{{$t('contractMod.frameworkAgreementCode')}}","x-render-table-column":{width:150}},frameworkAgreementName:{type:"string",title:"{{$t('contractMod.frameworkAgreementName')}}","x-render-table-column":{width:150}},contractOldCode:{type:"string","x-component":"RenderTableLink","x-component-props":{type:"text","@click":expression(`({ row }) => {
                $queryEngine.request.query({
                  contractNo: {
                    eq: row.contractOldCode
                  }
                }).then(res => {
                  if (res.data[0]) {
                    $readOne(res.data[0])
                  }
                })
              }`)},"x-render-table-column":{title:"{{$t('contractMod.contractOldCode')}}",minWidth:150,customRender:!0}},contractChangeCode:{type:"string",title:"{{$t('contractMod.contractChangeCode')}}","x-render-table-column":{width:150}},contractAgreementCode:{type:"string",title:"{{$t('contractMod.annexId')}}","x-render-table-column":{width:150}},modelName:{type:"string",title:"{{$t('contractMod.templHeadId')}}","x-render-table-column":{width:150}},effectiveDateFrom:{title:"{{$t('contractMod.constartDate')}}",...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.effectiveDateFrom, '{y}-{m}-{d}')
              }`)},"x-render-table-column":{width:130}},contractTerminationCode:{type:"string",title:"{{$t('contractMod.terminationId')}}","x-render-table-column":{width:150}},endDate:{title:"{{$t('qualitySynergy.endDate2')}}",...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.endDate, '{y}-{m}-{d}')
              }`)},"x-render-table-column":{width:150}},createdBy:{type:"string","x-hidden":!0},createdUserName:{type:"string",title:"{{$t('common.creator')}}","x-query-engine-skip":!0,"x-render-table-column":{width:100}},creationDate:{title:"{{$t('common.creationTime')}}","x-query-engine-sort":"desc",...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.creationDate, '{y}-{m}-{d}')
              }`)},"x-render-table-column":{width:150}},lastUpdatedBy:{type:"string","x-hidden":!0},lastUpdatedUserName:{type:"string",title:"{{$t('contractMod.lastUpdatedBy')}}","x-query-engine-skip":!0,"x-render-table-column":{width:130}},lastUpdateDate:{title:"{{$t('contractMod.lastUpdateDate')}}",...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.lastUpdateDate, '{y}-{m}-{d}')
              }`)},"x-render-table-column":{width:150}},vendorConfirmDate:{...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.vendorConfirmDate, '{y}-{m}-{d}')
              }`)},title:"{{$t('contractMod.vendorConfirmDate')}}","x-render-table-column":{width:140}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:170,fixed:"right"},"x-component":"RenderTableButtonList","x-component-props":{max:2},properties:{management:{type:"void",title:"{{$t('bidMod.management')}}","x-component-props":{"@click":expression(`({ row }) => {
                    console.log(row.contractType, 'contractType')
                    if (row.contractType == 'TERMINATION') {
                      $goToTermination(row, 2)
                    } else {
                      $editOne(row)
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
                  }`)},"x-reactions":changeFieldVisibleByDeps([".contractStatus"],"$buyer() && $deps[0] === 'UNPUBLISHED' && $createdUserIsCurrentUserByRow($table.getRowByIndex($self.index))")},terminationContract:{type:"void",title:i18nExpression("contractMod.terminationContract"),"x-component-props":{"@click":expression("({ row }) => $goToTermination(row, 2)")},"x-reactions":changeFieldVisibleByDeps([".contractStatus"],"$buyer() && ['ARCHIVED'].includes($deps[0])")},releaseSignPlatform:{type:"void",title:i18nExpression("contractMod.releaseSignPlatform"),"x-component-props":{"@click":expression("({ row }) => $readOne(row)")},"x-reactions":changeFieldVisibleByDeps([".contractStatus"],`$buyer() && ['APPROVAL'].includes($deps[0]) &&
                      ['MIAN_CONTRACT_ADD', 'MIAN_CONTRACT_ALTER', 'SUPPLEMENTAL_AGREEMENT'].includes($table.getRowByIndex($self.index).contractType) &&
                      ['ELECTRONIC_CONTRACT'].includes($table.getRowByIndex($self.index).formal)`)},approve:{type:"void",title:i18nExpression("common.approve"),"x-component-props":{type:"text","@click":expression("({ row }) => $readOne(row)")},"x-reactions":changeFieldVisibleByDeps([".contractStatus"],"$buyer() && ['UNDER_REVIEW', 'SUPPLIER_CONFIRMED'].includes($deps[0])")},approvalRefuse:{type:"void",title:i18nExpression("bidMod.approvalRefuse"),"x-component-props":{type:"text","@click":expression(`({ row }) => {
                    $prompt($t('contractMod.msgRefuseReason'), $t('oneStopShopping.refusedReason'), {
                      confirmButtonText: $t('common.confirm'),
                      cancelButtonText: $t('common.cancel')
                    }).then(({ value }) => {
                      return $queryEngine.request.save({
                          contractHeadId: row.contractHeadId,
                          approvalAdvice: value,
                          contractStatus: 'REFUSED',
                        }).then(() => {
                          $queryEngine.state.paginationManagement.refresh()
                        })
                    }).then(() => {
                      $message.success($t('common.success'))
                    }).catch((err) => {
                      console.error('approvalRefuse', err)
                    })
                  }`)},"x-reactions":changeFieldVisibleByDeps([".contractStatus"],"$buyer() && ['UNDER_REVIEW'].includes($deps[0])")},archive:{type:"void",title:i18nExpression("contractMod.archive"),"x-component-props":{type:"text","@click":expression(`({ row }) => {
                    $form.query('contractFilingDialog').take().setComponentProps({ visible: true })
                    setTimeout(() => {
                      const field = $form.query('contractFilingDialog.fileInfo').take()

                      // 重置
                      field.data = {}
                      field.data.contractHeadId = row.contractHeadId
                      field.data.fileuploadId = ''
                      field.data.fileSourceName = ''
                    })
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
                  }`)},"x-reactions":changeFieldVisibleByDeps([".contractStatus"],"['REJECTED', 'REFUSED'].includes($deps[0]) && $createdUserIsCurrentUserByRow($table.getRowByIndex($self.index))")}}}})}}},bulkMaintainFwAgreementDialog:{type:"void",title:i18nExpression("bidMod.bulkMaintainFwAgreement"),"x-component":"RDialog","x-component-props":{footer:!1},"x-decorator":"QueryEngine","x-query-engine":{service:"cm",type:"ContractHead",transformRequest:expression(`(data, headers) => {
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
          }`)}}}}}),{emitTabAdd,createdUserIsCurrentUserByRow,buyer,vendor,t,app}=usePageHelper(),$goToTermination=(row,num=1)=>{let str=num==1?"edit":"termination";emitTabAdd({component:contractInformation,params:{termination:!0,flag:str,row,isReadOnly:!1,contractType:"TERMINATION"},title:row.contractName,name:"termination"+row.contractName})},$detectContractInformationRightByRow=row=>(["DRAFT","REJECTED","REFUSED","WITHDRAW","SUPPLIER_REJECTED"].includes(row.contractStatus)&&buyer()&&createdUserIsCurrentUserByRow(row)||vendor()&&row.contractStatus==="SUPPLIER_CONFIRMING")&&row.contractType!=="TERMINATION",$readOne=(row,types)=>{let flag=types=="view"?"view":"edit";if(row.contractType=="TERMINATION"){emitTabAdd({component:contractInformation,params:{termination:!0,flag,row,isReadOnly:!0,contractType:"TERMINATION"},title:row.contractName,name:"contractInformation"+row.contractName});return}emitTabAdd({component:contractInformation,params:{flag,row,isReadOnly:!$detectContractInformationRightByRow(row)},title:row.contractName,name:"contractInformation"+row.contractName})},$editOne=row=>{emitTabAdd({component:contractInformation,params:{flag:"edit",row,isReadOnly:!1},contractType:row.contractType,title:row.contractName,name:"contractInformation"+row.contractName})},$addOne=useDebounceFn(async(contractType,selectedRows=[])=>{let rowId=null,contractOldCode=null,mainContractNo=null,isReadOnly=!1;if(contractType!=="MIAN_CONTRACT_ADD"){const name=t(contractType==="MIAN_CONTRACT_ALTER"?"contractMod.changeInContract":"contractMod.supplementalAgreement");if(!selectedRows.length){app.$message.warning(t("contractMod.msgContractManage[0]")+`${name}`);return}if(selectedRows.length>1){app.$message.warning(t("contractMod.msgContractManage[1]")+`${name}`+t("contractMod.msgContractManage[2]"));return}if(selectedRows[0].contractStatus!=="ARCHIVED"){app.$message.warning(t("contractMod.msgContractManage[3]")+`${name}`);return}if(rowId=selectedRows[0].contractHeadId,contractOldCode=selectedRows[0].contractCode,mainContractNo=selectedRows[0].contractNo,(await contractManagement.changePreCheck({ceeaContractOldId:rowId})).code!=="0")return!1}emitTabAdd({component:contractInformation,params:{flag:"add",rowId,contractType,contractOldCode,mainContractNo,isReadOnly,row:selectedRows},title:t("contractMod.createContract"),name:"contractInformation"})},216),scope={$buyer:buyer,$vendor:vendor,$goToTermination,$contractManagement:contractManagement,$addOne,$readOne,$editOne,$prompt:app.$prompt,$detectContractInformationRightByRow,$createdUserIsCurrentUserByRow:createdUserIsCurrentUserByRow,$reactiveAction:action};return{__sfc:!0,generateAddOneEventExpression,schema,emitTabAdd,createdUserIsCurrentUserByRow,buyer,vendor,t,app,$goToTermination,$detectContractInformationRightByRow,$readOne,$editOne,$addOne,scope,RenderEngine}}});var _sfc_render$1=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{pageAttrs:_vm.$attrs,schema:_setup.schema,scope:_setup.scope,schemaKey:"ContractHead"}})},_sfc_staticRenderFns$1=[],__component__$1=normalizeComponent(_sfc_main$1,_sfc_render$1,_sfc_staticRenderFns$1,!1,null,null,null,null);const contractListEngine=__component__$1.exports,_sfc_main={name:"ContractMaintainList",components:{NavTabs},data(){return{activeTab:"contractListEngine",tabs:[{title:this.$t("route.contractMaintainList"),name:"contractListEngine",component:contractListEngine,closable:!1}]}}};var _sfc_render=function(){var _vm=this,_c=_vm._self._c;return _c("NavTabs",{ref:"tabs",attrs:{"tabs-list":_vm.tabs,"cur-tab":_vm.activeTab}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const index=__component__.exports;export{index as default};
