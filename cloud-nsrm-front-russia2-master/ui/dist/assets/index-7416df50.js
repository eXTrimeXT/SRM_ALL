import{N as NavTabs}from"./index-9a7f2446.js";import{ak as defineComponent,aq as defineSchemas,ad as expression,ae as i18nExpression,af as yearMonthDaySelectorSegment,ah as generateXindexInOrder,ai as editTableFormItemValid,am as useAttrs,al as usePageHelper,ar as RenderEngine,ax as omit,ck as composeFormCollapse,a9 as OrganizationSelector,T as QuickSearch,n as normalizeComponent,b$ as dataTimeSelectorSegment,c9 as yearMonthDayHourMinuteSecondSelectorSegment,bD as changeFieldVisibleByDeps,cl as onActivated,cm as bus}from"./index-6b6051d8.js";import"./TableView-eb18d7e8.js";/* empty css                                                                   */import"./mixins-edc77a54.js";import"./i-order-detail.vue_vue_type_style_index_0_scoped_12170117_lang-4ed993c7.js";import{d as deppOmit}from"./util-1e55288f.js";import"./index-d31c36cb.js";import"./drag-5571e5c7.js";const _sfc_main$2=defineComponent({__name:"edit-engine",setup(__props){const handleSubmit=(type="submit",$values,$queryEngine)=>{if(type==="submit"){for(let i=0;i<$values.perPlanMilestoneId.length;i+=1)if(!$values.perPlanMilestoneId[i].practicallyEndDate){app.__jump_error__("perPlanMilestone","component","请填写里程碑---实际结束时间");return}}let data=deppOmit($values,["lastUpdateDate","creationDate","creationDate","contractCreationDate","currentPlanEndDate"]);return data=omit(data,["perPlanId.contractClass","perPlanId.contractNo","perPlanId.includeTaxAmount","perPlanId.vendorName","perPlanId.buName","perPlanId.currencyName","perPlanId.templateName","perPlanId.perPlanNo","perPlanId.processNum","perPlanMilestoneId.serialNumber","perPlanMilestoneId.milestoneType","perPlanMilestoneId.nodePersonName","perPlanMilestoneId.planStartDate","perPlanMilestoneId.planEndDate","perPlanMilestoneId.remarks","perPlanMilestoneId.fileId","perAcceptanceAttList.fileName","perAcceptanceAttList.createdUserName"]),$queryEngine.request.save({...data,perPlanMilestoneId:data.perPlanMilestoneId&&data.perPlanMilestoneId[0]},{customizeAction:type==="submit"?"vendorSubmit":void 0})},schema=defineSchemas({PerAcceptance:{type:"void","x-component":"FormContainer","x-decorator":"QueryEngine","x-query-engine":{service:"cm",actions:{vendorSubmit:{autoFormatResult:!1,transformRequest:expression(`(data, headers) => {
            data.query['*'] = {}

            return data
          }`)},save:{transformRequest:expression(`(data, headers) => {
            data.query['*'] = {}

            return data
          }`)},queryMilestone:{immediate:!0,ready:expression(`() => {
            return $attrs.params && $attrs.params.fromContractPerformancePlan
          }`),method:"read",autoFormatResult:!1,transformRequest:expression(`(data, headers) => {
            data.action = 'queryMilestone'
            data.query = {
              "*":{},
              "perAcceptanceConfList": {'*': {}},
              "perAcceptanceAttList": {'*': {}},
              "perPlanMilestoneId": {'*': {}},
              "perPlanId": {'*': {}},
              "perTemplLineId": { configList: {'*': {}}},
            }
            data.payload = {
              filter: {
                perPlanMilestoneId: {
                  eq: $attrs.params.row.perPlanMilestoneId
                }
              }
            }

            return data
          }`),transformResponse:expression(`(res) => {
            const data = JSON.parse(res)

            if (data.data.ref) {
              const perAcceptanceItem = {}

              // 可以通过 perPlanMilestoneId 查询到对应的履约计划数据
              if (data.data.type === '[PerAcceptance]' && data.data.ref.PerAcceptance) {
                const perAcceptanceId = Object.keys(data.data.ref.PerAcceptance)[0]

                Object.assign(perAcceptanceItem, data.data.ref.PerAcceptance[perAcceptanceId])

                if (perAcceptanceItem.perAcceptanceConfList) {
                  perAcceptanceItem.perAcceptanceConfList = perAcceptanceItem.perAcceptanceConfList.map(id => {
                    return data.data.ref.PerAcceptanceConf[id]
                  })
                }

                if (perAcceptanceItem.perAcceptanceAttList) {
                  perAcceptanceItem.perAcceptanceAttList = perAcceptanceItem.perAcceptanceAttList.map(id => {
                    return data.data.ref.PerAcceptanceAtt[id]
                  })
                }

                if (perAcceptanceItem.perPlanId) {
                  perAcceptanceItem.perPlanId = data.data.ref.PerPlan[perAcceptanceItem.perPlanId]
                }

                if (perAcceptanceItem.perPlanMilestoneId) {
                  perAcceptanceItem.perPlanMilestoneId = data.data.ref.PerPlanMilestone[perAcceptanceItem.perPlanMilestoneId]
                }
              } else {
                // 反之是查不到
                const perPlanId = Object.keys(data.data.ref.PerPlan)[0]
                perAcceptanceItem.perPlanId = data.data.ref.PerPlan[perPlanId]

                const perPlanMilestoneId = Object.keys(data.data.ref.PerPlanMilestone)[0]
                perAcceptanceItem.perPlanMilestoneId =
                  data.data.ref.PerPlanMilestone[perPlanMilestoneId]

                if (perAcceptanceItem.perPlanMilestoneId.perTemplLineId) {
                  const perTemplLine = data.data.ref.PerTemplLine[perAcceptanceItem.perPlanMilestoneId.perTemplLineId]

                  if (perTemplLine && perTemplLine.configList) {
                    perAcceptanceItem.perAcceptanceConfList = perTemplLine.configList.map(id => {
                      return data.data.ref.PerTemplLineConfig[id]
                    })
                  }
                }
              }

              // 设置文本只读
              $form.readPretty = $readOnly || $disabled

              $form.setValues({
                ...perAcceptanceItem,
                perAcceptanceNo: perAcceptanceItem.perAcceptanceNo,
                status: perAcceptanceItem.status || 'DRAFT',
                perPlanMilestoneId: [perAcceptanceItem.perPlanMilestoneId],
                perPlanId: perAcceptanceItem.perPlanId,
                perAcceptanceAttList: perAcceptanceItem.perAcceptanceAttList || [],
                perAcceptanceConfList: perAcceptanceItem.perAcceptanceConfList || [],
              })
            }

            return data
          }`)},query:{immediate:!0,ready:expression(`() => {
            return !!(
              $attrs.params
              && $attrs.params.row
              && !$attrs.params.fromContractPerformancePlan
              && ($attrs.params.row.perAcceptanceId || $attrs.params.row.perPlanMilestoneId)
            )
          }`),transformRequest:expression(`(data, headers) => {
            if (!data.payload.filter) {
              data.payload.filter = {}

              if ($attrs.params.row) {
                if ($attrs.params.row.perAcceptanceId) {
                  data.payload.filter.perAcceptanceId = {
                    eq: $attrs.params.row.perAcceptanceId
                  }
                }

                // 履约计划调跳转过来生成合同验收单的查询
                if ($attrs.params.row.perPlanMilestoneId) {
                  data.payload.filter.perPlanMilestoneId = {
                    eq: $attrs.params.row.perPlanMilestoneId
                  }
                }
              }
            }

            delete data.payload.page

            data.query['*'] = {}

            return data
          }`),onSuccess:expression(`(res) => {
          // 设置文本只读
          $form.readPretty = $readOnly || $disabled

          if (res.data[0]) {
            // UI 呈现需要是数组，实际是单个对象
            res.data[0].perPlanMilestoneId = [res.data[0].perPlanMilestoneId].filter(Boolean)
          }

          $form.setValues(res.data[0])
        }`)}}},items:{type:"object",properties:{back:{type:"void","x-content":i18nExpression("common.cancel"),"x-component":"Button","x-component-props":{type:"default","@submit":expression(`(values) => {
              $bus.$emit('PerAcceptance')
              emitTabRemove($attrs.tabName)
            }`)}},save:{type:"void","x-hidden":expression("$disabled"),"x-content":i18nExpression("common.save"),"x-component":"Button","x-component-props":{type:"primary","@submit":expression(`(values) => {
              $handleSubmit('save', $values, $queryEngine).then((res) => {
                app.$message.success($t('common.successSave'))
              })
            }`)}},submit:{type:"void","x-hidden":expression("$disabled"),"x-content":i18nExpression("common.submit"),"x-component":"Button","x-component-props":{type:"primary","@submit":expression(`(values) => {
              $handleSubmit('submit', $values, $queryEngine).then(() => {
                app.$message.success($t('common.successSubmit'))
                $bus.$emit('PerAcceptance')
                emitTabRemove($attrs.tabName)
              })
            }`)}}}},properties:{perPlanId:{type:"string","x-query-engine-skip":!0,"x-query-engine-relation":"perPlanId:*","x-hidden":!0},collapse:{type:"void","x-component":"Collapse","x-component-props":{defaultOpenPanelCount:1},properties:{baseInfo:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("supRisk.baseInfo")},"x-query-engine-skip":!0,properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:4,columnGap:32,rowGap:0},properties:{"perPlanId.contractNo":{type:"string","x-decorator":"FormItem","x-query-engine-skip":!0,title:"合同序号","x-component-props":{disabled:!0}},"perPlanId.processNum":{type:"string","x-decorator":"FormItem","x-query-engine-skip":!0,title:i18nExpression("contract_mod.processNum"),"x-component-props":{disabled:!0}},"perPlanId.templateName":{type:"string","x-decorator":"FormItem","x-query-engine-skip":!0,title:i18nExpression("contract_mod.templateName"),"x-component-props":{disabled:!0}},"perPlanId.vendorName":{type:"string","x-decorator":"FormItem",title:i18nExpression("common.vendorName"),"x-query-engine-skip":!0,"x-component-props":{disabled:!0}},"perPlanId.contractClass":{type:"string","x-decorator":"FormItem",title:i18nExpression("contract_mod.contractType"),"x-component":"DictSelect","x-query-engine-skip":!0,"x-component-props":{disabled:!0,code:"ELEM_CONTRACT_TYPE"}},"perPlanId.buName":{type:"string","x-decorator":"FormItem","x-query-engine-skip":!0,title:i18nExpression("cusEntry.vendorMod.orgName"),"x-component-props":{disabled:!0}},perAcceptanceNo:{type:"string","x-decorator":"FormItem",title:"合同验收单号","x-component-props":{disabled:!0}},status:{type:"string","x-decorator":"FormItem",title:"状态","x-component":"DictSelect","x-component-props":{code:"CONTRACT_CHECK_STATUS",disabled:!0}},"perPlanId.perPlanNo":{type:"string","x-decorator":"FormItem","x-query-engine-skip":!0,title:"合同履约计划单号","x-component-props":{disabled:!0}},"perPlanId.includeTaxAmount":{type:"string","x-decorator":"FormItem",title:"合同总金额（含税）","x-query-engine-skip":!0,"x-component-props":{disabled:!0}},"perPlanId.currencyCode":{type:"string","x-component":"DictSelect","x-component-props":{code:"currency",disabled:!0},"x-decorator":"FormItem",title:"币种"},"perPlanId.createdFullName":{type:"string","x-decorator":"FormItem",title:i18nExpression("common.creator"),"x-component-props":{disabled:!0}},"perPlanId.creationDate":{...yearMonthDaySelectorSegment,"x-decorator":"FormItem",title:i18nExpression("common.creationTime"),"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],disabled:!0}}}}}},perPlanMilestone:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("contractMod.milestone")},properties:{perPlanMilestoneId:{type:"array","x-component":"RenderTable","x-component-props":{class:"table-view-vxe-table",preColumns:"seq",pagination:!1,maxHeight:"45vh"},"x-query-engine-skip":!0,"x-query-engine-relation":"perPlanMilestoneId:*",properties:generateXindexInOrder({serialNumber:{type:"string",title:i18nExpression("components.processTable.headers.fdNodeName"),"x-query-engine-skip":!0,"x-render-table-column":{minWidth:130}},milestoneType:{type:"string",title:i18nExpression("contract_mod.processNodeName"),"x-component":"DictSelect","x-query-engine-skip":!0,"x-component-props":{code:"MILESTONE_SCHEDULE"},"x-render-table-column":{minWidth:130}},nodePersonName:{type:"string",title:"节点负责人","x-query-engine-skip":!0,"x-render-table-column":{minWidth:130}},planStartDate:{...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                        parseTime(row.planStartDate, '{y}-{m}-{d}')
                      }`)},"x-query-engine-skip":!0,title:i18nExpression("perfMod.planStartDate"),"x-render-table-column":{minWidth:130}},planEndDate:{...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                        parseTime(row.planEndDate, '{y}-{m}-{d}')
                      }`)},"x-query-engine-skip":!0,title:"计划结束时间","x-render-table-column":{minWidth:130}},nodePlanNum:{type:"string","x-query-engine-skip":!0,"x-disabled":expression(`
                      $disabled
                      || ['SUPPLIER_SUBMITTED','FIRST_PASS'].includes($values.status)
                    `),"x-render-table-column":{title:"节点交付数量",minWidth:130},...editTableFormItemValid},practicallyEndDate:{"x-query-engine-skip":!0,...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                        parseTime(row.practicallyEndDate, '{y}-{m}-{d}')
                      }`)},"x-disabled":expression(`
                      $disabled
                      || ['SUPPLIER_SUBMITTED','FIRST_PASS'].includes($values.status)
                    `),"x-render-table-column":{title:"实际结束时间",minWidth:130},...editTableFormItemValid},remarks:{type:"string",title:"特殊备注","x-query-engine-skip":!0,"x-render-table-column":{minWidth:130}},fileId:{type:"string","x-hidden":!0,title:i18nExpression("dataConfMod.attachmentTemplate"),"x-component":"SrmCommonFile","x-query-engine-skip":!0,"x-component-props":{readonly:!0,"default-file":{fileId:"{{$table.getRowByIndex($self.index)?.fileId}}",fileName:"{{$table.getRowByIndex($self.index)?.fileName}}"}},"x-render-table-column":{minWidth:130}}})}}},relevantAttachment:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("accountMod.relevantAttachment")},properties:{toolbar:{type:"void","x-component":"Space","x-component-props":{style:"margin-bottom: 16px"},properties:{add:{type:"void",title:i18nExpression("common.add"),"x-component":"RButton","x-component-props":{type:"primary",disabled:expression("$disabled"),"@click":expression(`(rowIndex) => {
                        $form.query(".perAcceptanceAttList").take().componentProps.componentInstance.addRow("unshift")
                      }`)}}}},perAcceptanceAttList:{type:"array","x-component":"RenderTable","x-component-props":{maxHeight:"45vh",class:"table-view-vxe-table",preColumns:"seq",editMode:!0,pagination:!1},"x-read-pretty":!0,"x-query-engine-skip":!0,"x-query-engine-relation":"perAcceptanceAttList:*",properties:generateXindexInOrder({fileName:{type:"string",title:i18nExpression("vendorMod.attachmentUpload"),"x-read-pretty":expression("$disabled || $table.getRowByIndex($self.index)?.uploadType==='VENDOR'"),"x-component":"SrmCommonFile","x-component-props":{"extra-data":{fileModular:"sup",fileFunction:"contractPerformanceCheck",fileType:"images"},"default-file":{fileId:"{{$table.getRowByIndex($self.index)?.fileId}}",fileName:"{{$self.value}}"},"@on-change":expression(`({file}) => {
                        let row = $table.getRowByIndex($self.index)
                        const { fileId = '', fileName = '' } = file || {}
                        row.fileId = fileId.toString()
                        row.fileName = fileName
                        row.createdUserName = file.createdBy
                        row.creationDate = file.creationDate
                      }`)},"x-render-table-column":{minWidth:150}},createdUserName:{type:"string",title:i18nExpression("components.fileupload.uploadUserName"),"x-query-engine-skip":!0,"x-render-table-column":{minWidth:130}},creationDate:{...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                        parseTime(row.creationDate, '{y}-{m}-{d}')
                      }`)},title:i18nExpression("outsource.creationDate"),"x-render-table-column":{minWidth:150}},operation:{type:"void",title:i18nExpression("common.operation"),"x-render-table-column":{minWidth:120,fixed:"right"},properties:{layout:{type:"void","x-component":"FormButtonGroup",properties:{edit:{type:"void",title:"删除","x-component":"TableButton","x-component-props":{type:"text",disabled:expression("$disabled"),"@click":expression(`({row, rowIndex}) => {
                                $table.remove(rowIndex)
                              }`)}}}}}}})}}}}}}}}),attrs=useAttrs(),{app,emitTabRemove}=usePageHelper(),$disabled=["view","approval"].includes(attrs.params?.flag??"");return{__sfc:!0,handleSubmit,schema,attrs,app,emitTabRemove,$disabled,scope:{app,$handleSubmit:handleSubmit,$disabled,emitTabRemove},components:{FormCollapse:composeFormCollapse,OrganizationSelector,QuickSearch},RenderEngine}}});var _sfc_render$2=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{schemaKey:"contractPerformanceCheckVendorDetail",readOnly:_setup.$disabled,pageAttrs:_vm.$attrs,schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns$2=[],__component__$2=normalizeComponent(_sfc_main$2,_sfc_render$2,_sfc_staticRenderFns$2,!1,null,null,null,null);const ContractPerformanceCheckDetailEngine=__component__$2.exports,_sfc_main$1=defineComponent({__name:"list-engine",setup(__props){const schema=defineSchemas({PerAcceptance:{type:"void","x-query-engine":{service:"cm",actions:{paginationQuery:{immediate:!0,transformResponse:expression(`(res) => {
            const data = JSON.parse(res)

            // TODO 后端把子实体查询主实体的关联关系给映射成 1:N，目前先由前端中转一下
            if (data.data.ref && data.data.ref.PerAcceptance) {
              Object.keys(data.data.ref.PerAcceptance).forEach((id) => {
                const item = data.data.ref.PerAcceptance[id]

                item.perPlanId = Array.isArray(item.perPlanId)
                  ? item.perPlanId[0]
                  : item.perPlanId
              })
            }

            return data
          }`)}}},"x-decorator":"QueryEngine","x-component":"el-container","x-component-props":{class:"flex-container",direction:"vertical"},properties:{bus:{type:"void","x-component":"BusEvent","x-component-props":{eventName:"PerAcceptance","@listener":expression(`(params) => {
            try{
              if (params && params.reCalcContainerHeight) {
                $form.query('query').take(field => {
                  field.data.reCalcContainerHeight += 1
                })
              }
            } catch (e) {
              // console.log(e, 'catch')
            }

            $queryEngine.state.paginationManagement.refresh()
          }`)}},query:{type:"object","x-query-engine-skip":!0,"x-component":"QueryFormByQueryEngine",properties:generateXindexInOrder({perAcceptanceNo:{type:"string",title:"合同验收单号","x-query-engine-query-operator":"contains"},buId:{type:"string",title:"{{$t('cusEntry.vendorMod.orgName')}}","x-component":"OrganizationSelector","x-component-props":{multiple:!1},"x-query-engine-relation":"perPlanId"},createdFullName:{type:"string",title:"{{$t('common.creator')}}","x-query-engine-query-operator":"contains"},contractNo:{type:"string",title:"{{$t('contractMod.contractNo_1')}}","x-query-engine-query-operator":"contains","x-query-engine-relation":"perPlanId","x-query-engine-relation-strict":!0},status:{type:"string",title:"单据状态","x-component":"DictSelect","x-component-props":{code:"CONTRACT_CHECK_STATUS"}},creationDate:{title:"创建日期",...dataTimeSelectorSegment,"x-query-engine-query-operator":"between"}})},toolbar:{type:"void","x-query-engine-skip":!0,"x-component":"Space","x-component-props":{style:"margin-bottom: 16px;height:28px;"},properties:{void:{}}},table:{type:"array","x-component":"RenderTable","x-component-props":{class:"table-view-vxe-table",style:"flex: 1",preColumns:"seq",openCustomTable:!0},properties:generateXindexInOrder({perAcceptanceId:{type:"string","x-hidden":!0,"x-query-engine-primary-key":!0},lastUpdateDate:{type:"string","x-hidden":!0,"x-query-engine-primary-key":!0},perAcceptanceNo:{type:"string","x-component":"TableButton","x-component-props":{type:"text","@click":expression(`({ row }) => {
                let type = ['SUBMITTED', 'APPROVED'].includes(row.status) ? 'approval' : 'view'
                $editTab(type, row)
              }`)},"x-render-table-column":{title:"合同验收单号",minWidth:150,customRender:!0}},perPlanNo:{type:"string",title:"合同履约计划单号","x-query-engine-relation":"perPlanId","x-render-table-column":{minWidth:150}},status:{type:"string",title:"单据状态","x-component":"DictSelect","x-component-props":{code:"CONTRACT_CHECK_STATUS"},"x-render-table-column":{minWidth:110}},buName:{type:"string",title:"{{$t('cusEntry.vendorMod.orgName')}}","x-query-engine-relation":"perPlanId","x-render-table-column":{minWidth:130}},vendorName:{type:"string",title:"{{$t('common.vendorName')}}","x-query-engine-relation":"perPlanId","x-render-table-column":{minWidth:150}},vendorCode:{type:"string",title:"{{$t('common.vendorCode')}}","x-query-engine-relation":"perPlanId","x-render-table-column":{minWidth:120}},contractNo:{type:"string",title:"合同序号","x-query-engine-relation":"perPlanId","x-render-table-column":{minWidth:100}},nodePersonName:{type:"string",title:"节点负责人","x-query-engine-relation":"perPlanMilestoneId","x-render-table-column":{minWidth:130}},createdFullName:{type:"string",title:"创建人","x-render-table-column":{minWidth:120}},creationDate:{"x-query-engine-sort":"desc",title:"创建时间",...yearMonthDayHourMinuteSecondSelectorSegment,"x-component-props":{...yearMonthDayHourMinuteSecondSelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.creationDate, '{y}-{m}-{d}')
              }`)},default:void 0,"x-render-table-column":{minWidth:150}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{minWidth:120,fixed:"right"},"x-component":"RenderTableButtonList",properties:{deliver:{type:"void",title:"交付","x-reactions":changeFieldVisibleByDeps([".status"],"['DRAFT'].includes($deps[0])"),"x-component-props":{type:"text","@click":expression(`({ row }) => {
                      $editTab('edit', row)
                    }`)}},edit:{type:"void",title:"编辑","x-reactions":changeFieldVisibleByDeps([".status"],"['FIRST_REJECTED'].includes($deps[0])"),"x-component-props":{type:"text","@click":expression(`({ row }) => {
                      $editTab('edit', row)
                    }`)}}}}})}}}}),{emitTabAdd,app}=usePageHelper(),$editTab=(type,row)=>{let name=row.perAcceptanceNo||"";emitTabAdd({component:ContractPerformanceCheckDetailEngine,params:{flag:type,row:{...row,perPlanMilestoneId:row.perPlanMilestoneId?.perPlanMilestoneId??row.perPlanMilestoneId},tabName:"合同验收"+name,fromContractPerformancePlan:app.$route.params?.from==="contractPerformancePlan"},title:name?"合同验收"+name:"新增合同验收",name:"合同验收"+name})};return onActivated(()=>{app.$route.params?.from==="contractPerformancePlan"&&app.$route.params?.row&&$editTab("edit",app.$route.params.row)}),{__sfc:!0,schema,emitTabAdd,app,$editTab,scope:{$editTab},RenderEngine}}});var _sfc_render$1=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{pageAttrs:_vm.$attrs,schema:_setup.schema,scope:_setup.scope,schemaKey:"PerAcceptance"}})},_sfc_staticRenderFns$1=[],__component__$1=normalizeComponent(_sfc_main$1,_sfc_render$1,_sfc_staticRenderFns$1,!1,null,null,null,null);const ContractPerformanceCheckListEngine=__component__$1.exports,activeTab="ContractPerformanceCheckListEngine",_sfc_main={name:"ContractPerformanceCheckVendor",components:{NavTabs},data(){return{activeTab,tabs:[{title:()=>this.$t("contractMod.contractAcceptance"),name:activeTab,component:ContractPerformanceCheckListEngine,closable:!1}]}},methods:{firstTabActive(){this.$nextTick(()=>{bus.$emit("PerAcceptance",{reCalcContainerHeight:!0})})},handleTabRemove(data){data.activeTab===activeTab&&this.firstTabActive()}}};var _sfc_render=function(){var _vm=this,_c=_vm._self._c;return _c("NavTabs",{ref:"tabs",attrs:{"tabs-list":_vm.tabs,"cur-tab":_vm.activeTab},on:{"first-tab-active":_vm.firstTabActive,"tab-remove":_vm.handleTabRemove}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const index=__component__.exports;export{index as default};
