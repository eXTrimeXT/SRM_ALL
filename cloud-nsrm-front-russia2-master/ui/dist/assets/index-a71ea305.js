import{N as NavTabs}from"./index-9a7f2446.js";import{ak as defineComponent,aq as defineSchemas,ad as expression,ah as generateXindexInOrder,aD as requiredValidatorSegment,ai as editTableFormItemValid,ae as i18nExpression,al as usePageHelper,am as useAttrs,bB as useDebounceFn,bC as toJS,ar as RenderEngine,n as normalizeComponent,af as yearMonthDaySelectorSegment,bD as changeFieldVisibleByDeps}from"./index-6b6051d8.js";const _sfc_main$2=defineComponent({__name:"edit",setup(__props){const schema=defineSchemas({TagRuleConfig:{type:"void","x-decorator":"QueryEngine","x-component":"FormContainer","x-query-engine":{service:"sup-ce",actions:{save:{cascadeDeletion:!0,transformRequest:expression(`(data,headers) => {
            data.query['*'] = {}
          }`)},read:{immediate:!0,ready:expression(`() => {
            let id = $attrs.params?.row?.tagRuleId
            $values.tagRuleId = id
            return !!id
          }`),transformRequest:expression(`(data,headers) => {
            data.query['*'] = {}
            data.payload = [$values.tagRuleId]
            return data
          }`),onSuccess:expression(`(res) => {
            console.log('read:::',res)
            $form.readPretty = $readOnly
            const value = res.data[0]
            $form.setValues({
              ...value
            })
          }`)}}},items:{type:"object",properties:{back:{type:"void","x-content":"{{$readOnly ? $t('common.close') : $t('common.cancel')}}","x-component":"Button","x-component-props":{type:"default","@click":expression(`() => {
              if($readOnly){
                emitTabRemove($attrs.tabName)
                return
              }
              // '此次修改并未保存是否取消'
              app.$confirm(i18nExpression('outsource.goBackConfirm'), i18nExpression('components.approvalHead.tips.tip'), {
                confirmButtonText: i18nExpression('common.confirm'),
                cancelButtonText: i18nExpression('components.common.cancel'),
                type: 'warning'
              }).then(() => {
                emitTabRemove($attrs.tabName)
              }).catch(() => {
              });
            }`)}},save:{type:"void","x-content":"{{$t('common.staging')}}","x-component":"Button","x-visible":expression("!$readOnly"),"x-component-props":{type:"default","@click":expression(`() => {
              $submit('save',$form,$queryEngine,$confirm,$message,$bus)
            }`)}},submit:{type:"void","x-content":"{{$t('common.submit')}}","x-component":"Button","x-visible":expression("!$readOnly"),"x-component-props":{"@submit":expression(`() => {
              $submit('submit',$form,$queryEngine,$confirm,$message,$bus)
            }`)}}}},properties:{collapse:{type:"void","x-component":"Collapse","x-component-props":{defaultOpenPanelCount:1},properties:{orderInfo:{type:"void","x-query-engine-skip":!0,"x-component":"CollapseItem","x-component-props":{title:"{{$t('barcodeManageNew.baseInfo')}}"},properties:{orderForm:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:generateXindexInOrder({tagRuleId:{type:"number","x-hidden":!0},tagRuleCode:{type:"string",title:"{{$t('barcodeManageNew.ruleCode')}}","x-decorator":"FormItem","x-component-props":{disabled:!0}},tagRuleName:{type:"string",title:"{{$t('barcodeManageNew.ruleName')}}","x-decorator":"FormItem",...requiredValidatorSegment},tagRuleType:{type:"string",title:"{{$t('barcodeManageNew.ruleType')}}","x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"TAG_RULE_TYPE"},...requiredValidatorSegment},tagRuleDesc:{type:"string",title:"{{$t('barcodeManageNew.ruleDes')}}","x-decorator":"FormItem","x-decorator-props":{gridSpan:4},"x-component-props":{type:"textarea"}}})}}},tagLine:{type:"void","x-component":"CollapseItem","x-component-props":{title:"{{$t('barcodeManageNew.tagLine')}}"},properties:{toolbar:{type:"void","x-component":"Space","x-component-props":{style:"margin-bottom:5px;display:block;"},"x-reactions":expression(`field => {
                  field.visible = !$readOnly
                }`),properties:{add:{type:"void",title:"{{$t('common.add')}}","x-component":"RButton","x-component-props":{type:"primary","@click":expression(`() => {
                        $form.query('tagRuleConfigLineList').take(field => {
                          field.value.push({
                            columnType:null,
                            columnValue:null
                          })
                        })
                      }`)}}}},tagRuleConfigLineList:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,pagination:!1,maxHeight:"58vh",sortable:!1,primaryKey:"tagRuleLineId",cascadeDeletion:!0},"x-query-engine-skip":!0,"x-query-engine-relation":"tagRuleConfigLineList:*",properties:generateXindexInOrder({tagRuleLineId:{type:"number","x-hidden":!0},columnType:{type:"string",title:"{{$t('barcodeManageNew.columnType')}}","x-render-table-column":{"min-width":150},"x-component":"DictSelect","x-component-props":{code:"TAG_RULE_COLUMN_TYPE","@change":expression(`val => {
                        console.log('$$$$',val,$self.query('.columnValue').take())
                        if(['FIXED_VALUE','SERIAL_CODE'].includes(val)){
                          $self.query('.columnValue').take(field => field.value = null)
                        }else{
                          $self.query('.columnValue').take(field => field.value = '系统取值')
                        }
                      }`)},...editTableFormItemValid},columnValue:{type:"string",title:"{{$t('barcodeManageNew.columnValue')}}","x-render-table-column":{"min-width":150},"x-reactions":[{dependencies:[".columnType"],fulfill:{state:{disabled:expression("!['FIXED_VALUE','SERIAL_CODE'].includes($deps[0]) || $readOnly"),"component[1].placeholder":expression(`$deps[0] === 'SERIAL_CODE' ? ${i18nExpression("cusEntry.supplement20250211.inputWaterCodeDigits")} 
                            : ${i18nExpression("common.pleaseInput")}`)}}}],...editTableFormItemValid},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:120},"x-component":"RenderTableButtonList","x-reactions":expression(`field => {
                      field.visible = !$readOnly
                    }`),properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{"@click":expression(`({rowIndex}) => {
                            $table.remove(rowIndex)
                          }`)}}}}})}}}}}}}}),{emitTabRemove,t,app}=usePageHelper(),attrs=useAttrs(),$submit=useDebounceFn((type,$form,$queryEngine,$confirm,$message,$bus)=>{const form=toJS($form.values),{tagRuleName,tagRuleType,tagRuleConfigLineList}=form;if(type==="submit"&&(!tagRuleConfigLineList||!tagRuleConfigLineList.length)){$message.warning(t("barcodeManageNew.notNull"));return}for(let item of tagRuleConfigLineList){if(item.columnType==="SERIAL_CODE"&&!/^[1-9]\d*$/.test(item.columnValue)){$message.warning(t("barcodeManageNew.tips3"));return}if(item.columnType==="FIXED_VALUE"&&item.columnValue.toString().length>50){$message.warning(t("cusEntry.supplement20250211.fixedValueFieldLengthLimit"));return}}$queryEngine.request.baseRequest({type:"TagRuleConfig",lang:"zh-cn",loading:!0,query:{"*":{}},payload:[{...form,status:type==="submit"?"Y":"DRAFT"}],action:"save"}).then(res=>{$message.success(t("common.successSave")),type==="save"?($form.values.tagRuleId=res?.data[0].tagRuleId,$queryEngine.request.read()):(emitTabRemove(attrs.params.tabName),$bus.$emit("TagRuleConfig"))})},216);return{__sfc:!0,schema,emitTabRemove,t,app,attrs,$submit,scope:{emitTabRemove,app,$submit},RenderEngine}}});var _sfc_render$2=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{schemaKey:"barcodeRuleConfigDetail",pageAttrs:_vm.$attrs,scope:_setup.scope,schema:_setup.schema}})},_sfc_staticRenderFns$2=[],__component__$2=normalizeComponent(_sfc_main$2,_sfc_render$2,_sfc_staticRenderFns$2,!1,null,null,null,null);const BarcodeRuleConfigDetail=__component__$2.exports,_sfc_main$1=defineComponent({__name:"list",setup(__props){const schema=defineSchemas({TagRuleConfig:{type:"void","x-decorator":"QueryEngine","x-component":"el-container","x-component-props":{class:"flex-container",direction:"vertical"},"x-query-engine":{service:"sup-ce",actions:{paginationQuery:{immediate:!0}}},properties:{bus:{type:"void","x-component":"BusEvent","x-component-props":{eventName:"TagRuleConfig","@listener":expression(`() => {
            $queryEngine.state.paginationManagement.refresh()
          }`)}},query:{type:"object","x-query-engine-skip":!0,"x-component":"QueryFormByQueryEngine",properties:generateXindexInOrder({tagRuleName:{type:"string",title:"{{$t('barcodeManageNew.ruleName')}}","x-query-engine-query-operator":"contains"},tagRuleCode:{type:"string",title:"{{$t('barcodeManageNew.ruleCode')}}","x-query-engine-query-operator":"contains"},tagRuleType:{type:"string",title:"{{$t('barcodeManageNew.ruleType')}}","x-component":"DictSelect","x-component-props":{code:"TAG_RULE_TYPE"}},status:{type:"string",title:"{{$t('barcodeManageNew.status')}}","x-component":"DictSelect","x-component-props":{code:"TAG_RULE_STATUS"}}})},toolbar:{type:"void","x-component":"Space","x-component-props":{style:"margin-bottom:16px;"},properties:{add:{type:"void",title:"{{$t('common.add')}}","x-component":"RButton","x-component-props":{type:"primary","@click":expression(`() => {
                $editTab('add',{})
              }`)}}}},table:{type:"array","x-component":"RenderTable","x-component-props":{class:"table-view-vxe-table",style:"flex: 1",preColumns:"seq",openCustomTable:!0},properties:generateXindexInOrder({tagRuleId:{type:"number","x-hidden":!1,"x-query-engine-primary-key":!0},tagRuleCode:{type:"string",title:"{{$t('barcodeManageNew.ruleCode')}}","x-render-table-column":{"min-width":120}},tagRuleName:{type:"string","x-render-table-column":{"min-width":150,title:"{{$t('barcodeManageNew.ruleName')}}",customRender:!0},"x-component":"TableButton","x-component-props":{type:"text","@click":expression(`({row}) => {
                $editTab('view',row)
              }`)}},tagRuleDesc:{type:"string",title:"{{$t('barcodeManageNew.ruleDes')}}","x-render-table-column":{"min-width":180}},tagRuleType:{type:"string",title:"{{$t('barcodeManageNew.ruleType')}}","x-render-table-column":{"min-width":120},"x-component":"DictSelect","x-component-props":{code:"TAG_RULE_TYPE"}},status:{type:"string",title:"{{$t('barcodeManageNew.status')}}","x-render-table-column":{"min-width":100},"x-component":"DictSelect","x-component-props":{code:"TAG_RULE_STATUS"}},creationDate:{...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.creationDate, '{y}-{m}-{d}')
              }`)},title:"{{$t('barcodeManageNew.creationDate')}}","x-render-table-column":{"min-width":120},"x-query-engine-sort":"desc"},createdFullName:{type:"string",title:"{{$t('barcodeManageNew.createdFullName')}}","x-render-table-column":{"min-width":120}},operation:{type:"void",title:"{{$t('common.operation')}}","x-component":"RenderTableButtonList","x-render-table-column":{fixed:"right",width:160},"x-component-props":{max:2},properties:{edit:{type:"void",title:"{{$t('common.edit')}}","x-component-props":{"@click":expression(`({row}) => {
                    $editTab('edit',row)
                  }`)},"x-reactions":changeFieldVisibleByDeps([".status"],"['DRAFT','N'].includes($deps[0])")},enable:{type:"void",title:"{{$t('common.enable')}}","x-component-props":{"@click":expression(`({row}) => {
                    $enable($queryEngine,row)
                  }`)},"x-reactions":changeFieldVisibleByDeps([".status"],"['N'].includes($deps[0])")},disable:{type:"void",title:"{{$t('common.disable')}}","x-component-props":{"@click":expression(`({row}) => {
                    $disable($queryEngine,row)
                  }`)},"x-reactions":changeFieldVisibleByDeps([".status"],"['Y'].includes($deps[0])")}}}})}}}}),{emitTabAdd,t,app}=usePageHelper(),$editTab=(type,row)=>{let name,title;type==="add"?(name="barcodeRuleConfigDetail",title=t("barcodeManageNew.barcodeManageNewAdd")):(name="barcodeRuleConfigDetail"+row.tagRuleCode,title=row.tagRuleCode),emitTabAdd({component:BarcodeRuleConfigDetail,params:{flag:type,row,tabName:name},title,name})},changeStatus=($queryEngine,row,status)=>{$queryEngine.request.save({...row,status}).then(()=>{app.$message.success(t("common.success")),$queryEngine.state.paginationManagement.refresh()})},$enable=($queryEngine,row)=>{changeStatus($queryEngine,row,"Y")},$disable=($queryEngine,row)=>{changeStatus($queryEngine,row,"N")};return{__sfc:!0,schema,emitTabAdd,t,app,$editTab,changeStatus,$enable,$disable,scope:{$editTab,$enable,$disable},RenderEngine}}});var _sfc_render$1=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{schemaKey:"barcodeRuleConfig",pageAttrs:_vm.$attrs,scope:_setup.scope,schema:_setup.schema}})},_sfc_staticRenderFns$1=[],__component__$1=normalizeComponent(_sfc_main$1,_sfc_render$1,_sfc_staticRenderFns$1,!1,null,null,null,null);const BarcodeRuleConfig=__component__$1.exports,_sfc_main={name:"BarcodeRuleConfig",components:{NavTabs},data(){return{activeTab:"barcodeRuleConfigList",tabs:[{title:()=>this.$t("barcodeManageNew.barcodeRuleConfig"),name:"barcodeRuleConfigList",component:BarcodeRuleConfig,clsable:!1}]}}};var _sfc_render=function(){var _vm=this,_c=_vm._self._c;return _c("NavTabs",{ref:"tabs",attrs:{"tabs-list":_vm.tabs,"cur-tab":_vm.activeTab}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const index=__component__.exports;export{index as default};
