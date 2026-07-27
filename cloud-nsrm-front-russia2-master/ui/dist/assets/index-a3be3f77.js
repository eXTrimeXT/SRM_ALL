import{N as NavTabs}from"./index-9a7f2446.js";import{n as normalizeComponent,ak as defineComponent,al as usePageHelper,am as useAttrs,aq as defineSchemas,ad as expression,ae as i18nExpression,c9 as yearMonthDayHourMinuteSecondSelectorSegment,ah as generateXindexInOrder,ai as editTableFormItemValid,aj as feedbackLayoutIsPopover,ar as RenderEngine,bD as changeFieldVisibleByDeps,ci as performPlan}from"./index-6b6051d8.js";import{i as isNil}from"./isNil-2a068d83.js";const _sfc_main$3={name:"IFieldOptions",model:{event:"change",prop:"value"},props:{fieldType:{required:!0,type:String},value:{type:String,default:"{}"},disabled:{type:Boolean}},data(){return{formatValue:{},attributes:{},componentOptions:{"el-input":[{label:this.$t("contract_mod.isCleared"),prop:"clearable",type:"el-switch",default:!0,id:0},{label:this.$t("contract_mod.inputType"),options:[{id:0,value:"",label:this.$t("contract_mod.normalDropDown")},{id:1,value:"textarea",label:this.$t("contract_mod.textField")}],prop:"type",type:"el-select",default:"",id:1}],"el-select":[{id:0,label:this.$t("contract_mod.dropDownOptions"),prop:"options",type:"el-input",default:"",placeholder:this.$t("contract_mod.separateMultipleOptions")},{id:1,label:this.$t("contract_mod.isCleared"),prop:"clearable",type:"el-switch",default:!0}],"el-date-picker":[{id:0,label:this.$t("contract_mod.isCleared"),prop:"clearable",type:"el-switch",default:!0}],"el-checkbox":[{id:0,label:this.$t("contract_mod.valueSelected"),prop:"true-label",type:"el-input",default:"Y"},{id:0,label:this.$t("contract_mod.notSelectValue"),prop:"false-label",type:"el-input",default:"N"}]}}},computed:{compOption(){return this.componentOptions[this.fieldType]}},watch:{attributes:{deep:!0,handler(){this.attributes,this.updateValue()}},value(){this.initData()}},created(){this.initData()},mounted(){},methods:{initData(){this.formatValue=JSON.parse(this.value||"{}"),this.attributes=this.compOption.reduce((last,item)=>{let defaultValue=item.default;typeof item.default=="function"&&(defaultValue=defaultValue());const formatValue=this.formatValue[item.prop];return last[item.prop]=isNil(formatValue)?defaultValue:formatValue,last},{})},updateValue(){this.formatValue=JSON.parse(JSON.stringify(this.attributes||{})),this.$emit("change",JSON.stringify(this.formatValue))}}};var _sfc_render$3=function(){var _vm=this,_c=_vm._self._c;return _c("div",{staticClass:"form-container"},[_c("el-form",{attrs:{model:_vm.attributes}},[_c("el-row",{attrs:{gutter:32}},_vm._l(_vm.compOption,function(item){return _c("el-col",{key:item.id,attrs:{span:8}},[_c("el-form-item",{attrs:{label:item.label,prop:item.prop}},[item.type==="el-select"?_c("el-select",{attrs:{disabled:_vm.disabled},model:{value:_vm.attributes[item.prop],callback:function($$v){_vm.$set(_vm.attributes,item.prop,$$v)},expression:"attributes[item.prop]"}},_vm._l(item.options,function(i){return _c("el-option",{key:i.id,attrs:{label:i.label,value:i.value}})}),1):item.type==="el-input"?_c("el-input",{attrs:{placeholder:item.placeholder,disabled:_vm.disabled},model:{value:_vm.attributes[item.prop],callback:function($$v){_vm.$set(_vm.attributes,item.prop,$$v)},expression:"attributes[item.prop]"}}):item.type==="el-switch"?_c("el-switch",{staticClass:"switch-class",attrs:{disabled:_vm.disabled},model:{value:_vm.attributes[item.prop],callback:function($$v){_vm.$set(_vm.attributes,item.prop,$$v)},expression:"attributes[item.prop]"}}):_vm._e()],1)],1)}),1)],1)],1)},_sfc_staticRenderFns$3=[],__component__$3=normalizeComponent(_sfc_main$3,_sfc_render$3,_sfc_staticRenderFns$3,!1,null,"f98237f9",null,null);const IFieldOptions=__component__$3.exports,_sfc_main$2=defineComponent({__name:"edit",setup(__props){const{emitTabRemove,t:$t}=usePageHelper(),attrs=useAttrs(),$closePageAndRefreshListPageData=$bus=>{$bus.$emit("PerTemplHead"),emitTabRemove(attrs.tabName)},$submitFormData=async(values,type,$queryEngine,$message,$bus,$form)=>{let data=values,bolErr=!1;if(await $form.validate().catch(err=>(bolErr=!0,$message.error($t("common.pleasefinishRequired")))),bolErr)return!1;if(type==="submit"&&data.perTemplLineList.some(i=>!i.milestoneType))return $message.error(""+$t("contract_mod.processNodeName")+$t("vendorMod.and")+$t("contract_mod.fileTpl")+$t("contract_mod.required"));data.perTemplLineList.forEach((item,index2)=>{item.serialNumber=index2+1}),data.status=type==="submit"?"DRAFT":data.status||"DRAFT",attrs.params.flag==="add"&&(data.perTemplHeadId&&delete data.perTemplHeadId,data.perTemplLineList.forEach(e=>{delete e.perTemplHeadId,delete e.perTemplLineId})),$queryEngine.request.baseRequest({action:type==="submit"?"submit":"save",payload:[data]}).then(()=>{$closePageAndRefreshListPageData($bus)}).catch(()=>{})},scope={$submitFormData},components={IFieldOptions},schema=defineSchemas({PerTemplHead:{type:"void","x-component":"FormContainer","x-decorator":"QueryEngine","x-data":{curPerformTemplLineRow:-1,curConfigListRow:-1,curFieldOptionType:""},"x-query-engine":{service:"cm",actions:{read:{immediate:!0,ready:expression(`() => {
            return $attrs.params && !!$attrs.params.row.perTemplHeadId
          }`),transformRequest:expression(`(data, headers) => {
            data.payload = [$attrs.params.row.perTemplHeadId]

            data.query.perTemplHeadId = {}
            data.query.perTemplLineList.configList = { '*': {} }

            return data
          }`),onSuccess:expression(`(res) => {
            // 单纯文本只读状态
            $form.readPretty = $readOnly

            res.data[0].perTemplLineList.forEach(item => {
              item.configList = item.configList.map(id => res.originalData.ref.PerTemplLineConfig[id])
            })

            $form.setValues(res.data[0])
          }`)},save:{cascadeDeletion:!0}}},items:{type:"void",properties:{cancel:{type:"void","x-content":i18nExpression("common.cancel"),"x-component":"Button","x-component-props":{type:"default","@click":expression(`async (values) => {
              $bus.$emit('ModelHead')
              $emitTabRemove($attrs.tabName)
            }`)}},staging:{type:"void","x-hidden":"{{$attrs.params.flag === 'view'}}","x-content":i18nExpression("common.staging"),"x-component":"Button","x-component-props":{"@click":expression("() => $submitFormData($values, 'staging', $queryEngine, $message, $bus, $form)")}},submit:{type:"void","x-hidden":"{{$attrs.params.flag === 'view'}}","x-content":i18nExpression("common.submit"),"x-component":"Button","x-component-props":{"@click":expression(`() => {
              $form.validate().then(() => {
                $submitFormData($values, 'submit', $queryEngine, $message, $bus, $form)
              }).catch(err => {
                  console.log(err)
                  $message.warning($t('common.pleasefinishRequired'))
              })
           }`)}}}},properties:{layoutForm:{type:"void","x-component":"FormGrid","x-component-props":{maxColumns:4,columnGap:20,rowGap:0},properties:{contractType:{type:"string",title:i18nExpression("contract_mod.contractType"),"x-component":"DictSelect","x-component-props":{code:"ELEM_CONTRACT_TYPE",disabled:"{{$attrs.params.flag === 'view'}}"},"x-decorator":"FormItem","x-validator":{required:!0,message:i18nExpression("contract_mod.required")}},processNum:{type:"string",title:i18nExpression("contract_mod.processNum"),"x-component-props":{disabled:!0},"x-decorator":"FormItem"},status:{type:"string",title:i18nExpression("contract_mod.configStatus"),"x-component":"DictSelect","x-component-props":{disabled:!0,code:"PERFORMANCE_OF_CONTRACT"},"x-decorator":"FormItem"},templateName:{type:"string",title:i18nExpression("contract_mod.templateName"),"x-component-props":{maxlength:"30","show-word-limit":!0,disabled:"{{$attrs.params.flag === 'view'}}"},"x-decorator":"FormItem","x-validator":{required:!0,message:i18nExpression("contract_mod.required")}},createdBy:{type:"string",default:"",title:i18nExpression("common.creator"),"x-component-props":{disabled:!0},"x-decorator":"FormItem"},creationDate:{title:i18nExpression("contract_mod.contractType"),"x-decorator":"FormItem",...yearMonthDayHourMinuteSecondSelectorSegment,"x-component-props":{...yearMonthDayHourMinuteSecondSelectorSegment["x-component-props"],disabled:!0}}}},toolbar:{type:"void","x-component":"Space","x-component-props":{style:"margin-bottom: 16px"},"x-visible":expression("!$readOnly"),properties:{add:{type:"void",title:i18nExpression("contract_mod.addProcessNode"),"x-component":"RButton","x-component-props":{type:"primary","@click":expression('() => $form.query("perTemplLineList").take().componentProps.componentInstance.addRow("push")')}}}},perTemplLineList:{type:"array","x-component":"RenderTable","x-component-props":{class:"table-view-vxe-table",preColumns:"seq",pagination:!1,editMode:!0,primaryKey:"perTemplLineId",cascadeDeletion:!0},"x-query-engine-relation":"perTemplLineList:*","x-query-engine-skip":!0,properties:generateXindexInOrder({milestoneType:{type:"string","x-render-table-column":{title:i18nExpression("contract_mod.processNodeName"),minWidth:150},"x-component":"DictSelect","x-component-props":{code:"MILESTONE_SCHEDULE",disabled:"{{$attrs.params.flag === 'view'}}"},...editTableFormItemValid,"x-validator":{required:!0,message:i18nExpression("contract_mod.processNodeName")}},fileId:{type:"string","x-hidden":!0,"x-render-table-column":{title:i18nExpression("contract_mod.fileTpl"),minWidth:150},"x-component":"SrmCommonFile",default:null,"x-component-props":{"extra-data":{fileModular:"sup",fileFunction:"contractPerformanceProcessConfigEdit",fileType:"images"},"default-file":{fileId:expression("$self.value"),fileName:expression("$table.getRowByIndex($self.index).fileName || ''")},readonly:"{{$readOnly}}","@on-change":expression(`({ file }) => {
                const row = $table.getRowByIndex($self.index)
                const { fileId = '', fileName = '' } = file || {}
                row.fileId = fileId.toString()
                row.fileName = fileName
              }`)},"x-decorator":"FormItem",...feedbackLayoutIsPopover,"x-decorator-props":{...feedbackLayoutIsPopover["x-decorator-props"],fullness:!1,disabled:"{{$attrs.params.flag === 'view'}}"},"x-validator":{required:"{{$attrs.params.flag === 'add'}}",triggerType:"onChange",message:i18nExpression("contract_mod.contractType")}},configList:{type:"array","x-hidden":!0,default:[],title:expression("$t($readOnly ? 'common.view' : 'common.edit')"),"x-render-table-column":{title:i18nExpression("contract_mod.payTpl"),minWidth:150},"x-component":"TableButton","x-component-props":{type:"text",disabled:!1,"@click":expression(`({ rowIndex }) => {

                const curData = $form.query('.PerTemplHead').take().data || {}
                curData.curPerformTemplLineRow = rowIndex
                $form.query('.PerTemplHead').take().setData(curData)

                const row = $table.getRowByIndex(rowIndex)
                if (!row.configList) {
                  row.configList = []
                }
                $form.query('configListDialog').take().setComponentProps({ visible: true })
                setTimeout(() => {
                  $form.query('.configListTable').take().value.splice(0)
                  row.configList.forEach(item => $form.query('.configListTable').take().value.push(item))
                })
              }`)}},operation:{type:"void",title:i18nExpression("common.operation"),"x-hidden":"{{$attrs.params.flag === 'view'}}","x-render-table-column":{width:150,fixed:"right"},properties:{delete:{type:"void",title:i18nExpression("common.delete"),"x-render-table-column":{title:i18nExpression("components.headers.operation"),minWidth:150},"x-component":"TableButton","x-component-props":{type:"text","@click":expression("({ rowIndex }) => $table.remove(rowIndex)")}}}}})}}},configListDialog:{type:"void",title:expression("$t($readOnly ? 'common.view' : 'common.edit') + $t('contract_mod.payTpl')"),"x-component":"RDialog","x-component-props":{width:"1080px",footer:"{{$attrs.params.flag !== 'view'}}",beforeOkClose:expression(`async () => {
        const configListTable = $form.query('configListTable').get('value')
        if (configListTable.some((i) => !i.fieldType || !i.fieldCode || !i.fieldName)) {
          $message.error(
            '【' + $t('contract_mod.fieldType') + '】和【' + $t('contract_mod.fieldCode') + '】和【' + $t('contract_mod.fieldName') + '】' + $t('contract_mod.required')
          )

          return Promise.reject()
        }

        configListTable.splice(0)
      }`),"@ok":expression(`() => {
        const perTemplLineList = $form.query('perTemplLineList').get('value')
        const configListTable = $form.query('configListTable').get('value')
        const curPerformTemplLineRow = $form.query('.PerTemplHead').get('data').curPerformTemplLineRow
        perTemplLineList[curPerformTemplLineRow].configList = JSON.parse(JSON.stringify(configListTable))
      }`),"@cancel":expression(`() => {
        console.log('cancel')
      }`)},properties:{toolbar:{type:"void","x-component":"Space","x-component-props":{style:"margin-bottom: 16px"},properties:{add:{type:"void","x-hidden":"{{$attrs.params.flag === 'view'}}",title:i18nExpression("common.add"),"x-component":"RButton","x-component-props":{type:"primary",disabled:"{{$attrs.params.flag === 'view'}}","@click":expression('({ rowIndex }) => $form.query(".configListTable").take().componentProps.componentInstance.addRow()')}}}},layout:{type:"void",properties:{configListTable:{type:"array","x-decorator":"QueryEngine","x-component":"RenderTable","x-component-props":{class:"table-view-vxe-table",style:"height: 58vh;",preColumns:"seq",editMode:"{{$attrs.params.flag !== 'view'}}",pagination:!1},properties:{fieldType:{type:"string",title:i18nExpression("contract_mod.fieldType"),"x-render-table-column":{minWidth:150},"x-component":"DictSelect","x-component-props":{code:"FIELD_TYPE",disabled:"{{$readOnly}}"}},fieldOptions:{type:"string",title:expression("$t($readOnly ? 'common.view' : 'common.edit')"),"x-render-table-column":{title:i18nExpression("contract_mod.fieldOptions"),minWidth:150},"x-component":"TableButton","x-component-props":{type:"text",disabled:!1,"@click":expression(`({ row, rowIndex }) => {
                    const fieldType = row.fieldType

                    if (!fieldType) {
                      return $message.error(
                        '【' + $t('contract_mod.fieldType') + '】' + $t('contract_mod.required')
                      )
                    }

                    const curData = $form.query('.PerTemplHead').take().data || {}
                    curData.curConfigListRow = rowIndex
                    curData.curFieldOptionType = fieldType
                    $form.query('.PerTemplHead').take().setData(curData)

                    $form.query('.fieldOptionDialog').take().setComponentProps({
                      visible: true
                    })

                    setTimeout(() => {
                      $form.query('fieldOptionDialog.fieldOptions').take(field => {
                        console.log(row.fieldOptions, 'row')
                        field.value = row.fieldOptions
                      })
                    })
                  }`)}},fieldName:{type:"string","x-query-engine-skip":!0,title:i18nExpression("contract_mod.fieldName"),"x-render-table-column":{minWidth:150},"x-component-props":{disabled:"{{$readOnly}}"}},fieldCode:{type:"string","x-query-engine-skip":!0,title:i18nExpression("contract_mod.fieldCode"),"x-render-table-column":{minWidth:150},"x-component-props":{disabled:"{{$readOnly}}"}},required:{type:"number",default:1,"x-decorator":"FormItem","x-component":"Checkbox","x-render-table-column":{minWidth:100,title:i18nExpression("contract_mod.required")},"x-component-props":{disabled:"{{$readOnly}}",trueLabel:1,falseLabel:0}},operation:{type:"void","x-hidden":"{{$attrs.params.flag === 'view'}}",title:i18nExpression("common.operation"),"x-render-table-column":{width:130,fixed:"right"},properties:{delete:{type:"void",title:i18nExpression("common.delete"),"x-render-table-column":{title:i18nExpression("components.headers.operation"),minWidth:150},"x-component":"TableButton","x-component-props":{type:"text",disabled:"{{$attrs.params.flag === 'view'}}","@click":expression("({ rowIndex }) => $table.remove(rowIndex)")}}}}}}}}}},fieldOptionDialog:{type:"void",title:expression("$t($readOnly ? 'common.view' : 'common.edit') + $t('contract_mod.fieldOptions')"),"x-component":"RDialog","x-component-props":{beforeClose:expression(`(done, type) => {
        const curConfigListRow = $form.query('.PerTemplHead').take().data.curConfigListRow
        const configListTable = $form.query('.configListTable').take().value
        const row = configListTable[curConfigListRow]
        if (type === 'ok') {
          row.fieldOptions = row.newFieldOptions || ''
        }
        delete row.newFieldOptions
        done()
      }`)},properties:{fieldOptions:{type:"string",default:"","x-component":"IFieldOptions","x-component-props":{disabled:"{{$readOnly}}","field-type":"{{$form.query('.PerTemplHead').take().data.curFieldOptionType}}","@change":expression(`(fieldOptions) => {
            const curConfigListRow = $form.query('.PerTemplHead').take().data.curConfigListRow
            const configListTable = $form.query('.configListTable').take().value
            const row = configListTable[curConfigListRow]
            row.newFieldOptions = fieldOptions
          }`)}}}}});return{__sfc:!0,emitTabRemove,$t,attrs,$closePageAndRefreshListPageData,$submitFormData,scope,components,schema,RenderEngine}}});var _sfc_render$2=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{schemaKey:"contractPerformanceProcessConfigDetail",pageAttrs:_vm.$attrs,schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns$2=[],__component__$2=normalizeComponent(_sfc_main$2,_sfc_render$2,_sfc_staticRenderFns$2,!1,null,null,null,null);const Edit=__component__$2.exports,_sfc_main$1=defineComponent({__name:"list",setup(__props){const{emitTabAdd,t:$t,app}=usePageHelper(),$handleOne=(row,flag)=>{let tab={component:Edit,params:{row,flag,tabName:"contractPerformanceProcessConfigEdit"+(row.perTemplHeadId||"")},title:row.processNum,name:`${flag}_${row.perTemplHeadId?row.perTemplHeadId:""}`};flag==="add"&&(tab.title=$t("common.add")),flag==="view"&&(tab.title=`${$t("common.view")} - ${row.processNum}`),emitTabAdd(tab)},schema=defineSchemas({PerTemplHead:{type:"void","x-query-engine":{service:"cm",actions:{paginationQuery:{immediate:!0}}},"x-decorator":"QueryEngine","x-component":"el-container","x-component-props":{class:"flex-container",direction:"vertical"},properties:{bus:{type:"void","x-component":"BusEvent","x-component-props":{eventName:"PerTemplHead","@listener":expression("() => $queryEngine.state.paginationManagement.refresh()")}},query:{type:"object","x-query-engine-skip":!0,"x-component":"QueryFormByQueryEngine","x-component-props":{labelCol:9},properties:generateXindexInOrder({contractType:{type:"string",title:i18nExpression("contract_mod.contractType"),"x-component":"DictSelect","x-component-props":{code:"ELEM_CONTRACT_TYPE"}},status:{type:"string",title:i18nExpression("contract_mod.configStatus"),"x-component":"DictSelect","x-component-props":{code:"PERFORMANCE_OF_CONTRACT"}},processNum:{type:"string",title:i18nExpression("contract_mod.processNum"),"x-query-engine-query-operator":"contains"},templateName:{type:"string",title:i18nExpression("contract_mod.templateName"),"x-query-engine-query-operator":"contains"}})},toolbar:{type:"void","x-component":"Space","x-component-props":{style:"margin-bottom: 16px"},properties:{add:{type:"void",title:i18nExpression("common.add"),"x-component":"RButton","x-component-props":{type:"primary","@click":expression('() => $handleOne({}, "add")')}}}},table:{type:"array","x-component":"RenderTable","x-component-props":{class:"table-view-vxe-table",preColumns:"seq",editMode:"row",openCustomTable:!0},properties:generateXindexInOrder({perTemplHeadId:{type:"string","x-hidden":!0},contractType:{type:"string","x-render-table-column":{title:i18nExpression("contract_mod.contractType"),minWidth:150},"x-component":"DictSelect","x-component-props":{code:"ELEM_CONTRACT_TYPE"}},processNum:{type:"string","x-render-table-column":{title:i18nExpression("contract_mod.processNum"),minWidth:150,customRender:!0},"x-component":"TableButton","x-component-props":{type:"text","@click":expression('({ row }) => $handleOne(row, "view")')},"x-query-engine-sort":"desc"},templateName:{type:"string","x-render-table-column":{title:i18nExpression("contract_mod.templateName"),minWidth:150}},status:{type:"string","x-render-table-column":{title:i18nExpression("contract_mod.configStatus"),minWidth:120},"x-component":"DictSelect","x-component-props":{code:"PERFORMANCE_OF_CONTRACT"}},createdFullName:{type:"string","x-render-table-column":{title:i18nExpression("common.creator"),minWidth:100}},creationDate:{...yearMonthDayHourMinuteSecondSelectorSegment,"x-component-props":{...yearMonthDayHourMinuteSecondSelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.creationDate, '{y}-{m}-{d} {h}:{i}:{s}')
              }`)},"x-query-engine-sort":"desc","x-render-table-column":{title:i18nExpression("common.creationTime"),minWidth:150}},lastUpdatedFullName:{type:"string","x-render-table-column":{title:i18nExpression("contract_mod.updateBy"),minWidth:100}},lastUpdateDate:{"x-render-table-column":{title:i18nExpression("contract_mod.updateDate"),minWidth:150},...yearMonthDayHourMinuteSecondSelectorSegment,"x-component-props":{...yearMonthDayHourMinuteSecondSelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.lastUpdateDate, '{y}-{m}-{d} {h}:{i}:{s}')
              }`)}},operation:{type:"void",title:i18nExpression("common.operation"),"x-render-table-column":{width:170,fixed:"right"},"x-component":"RenderTableButtonList","x-component-props":{max:2},properties:{edit:{type:"void",title:"{{$t('common.edit')}}","x-reactions":changeFieldVisibleByDeps([".status"],"['DRAFT'].includes($deps[0])"),"x-component-props":{"@click":expression('({ row }) => $handleOne(row, "edit")')}},delete:{type:"void",title:"{{$t('common.delete')}}","x-reactions":changeFieldVisibleByDeps([".status"],"['DRAFT'].includes($deps[0])"),"x-component-props":{popconfirm:{title:i18nExpression("common.confirmDeleteRow")},"@click":expression(`
                    ({ row }) => $queryEngine.request.delete(row.perTemplHeadId).then(() => {
                        $message.success($t('common.successDelete'))
                        $queryEngine.state.paginationManagement.refresh()
                      })

                  `)}},failure:{type:"void",title:"{{$t('contract_mod.invalid')}}","x-reactions":changeFieldVisibleByDeps([".status"],"['VALID'].includes($deps[0])"),"x-component-props":{"@click":expression(`
                    ({ row }) => $confirm($t('contractMod.validConfirm'), {
                      confirmButtonText: $t('common.confirm'),
                      cancelButtonText: $t('common.cancel'),
                      type: 'warning',
                    }).then(() => {
                      performanceTpl.performanceTpl.failure(row.perTemplHeadId).then((res) => {
                        $message.success(res?.message || '')
                        $queryEngine.state.paginationManagement.refresh()
                      })
                    })
                  `)}},view:{type:"void",title:"{{$t('common.view')}}","x-reactions":changeFieldVisibleByDeps([".status"],"['INVALID'].includes($deps[0])"),"x-component-props":{"@click":expression('({ row }) => $handleOne(row, "view")')}},copy:{type:"void",title:i18nExpression("common.copy"),"x-reactions":changeFieldVisibleByDeps([".status"],"['DRAFT'].includes($deps[0])"),"x-component-props":{"@click":expression(`
                    ({ row }) => {
                      const tab = {
                        component: Edit,
                        params: {
                          row,
                          flag: 'add',
                          tabName: 'contractPerformanceProcessConfigEdit'
                        },
                        title: '复制新增',
                        name: 'contractPerformanceProcessConfigEdit'
                      }
                      emitTabAdd(tab)
                    }
                  `)}}}}})}}}});return{__sfc:!0,emitTabAdd,$t,app,$handleOne,schema,scope:{$handleOne,performanceTpl:performPlan,app,Edit,emitTabAdd},RenderEngine}}});var _sfc_render$1=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{schemaKey:"contractPerformanceProcessConfigList",pageAttrs:_vm.$attrs,schema:_setup.schema,scope:_setup.scope}})},_sfc_staticRenderFns$1=[],__component__$1=normalizeComponent(_sfc_main$1,_sfc_render$1,_sfc_staticRenderFns$1,!1,null,null,null,null);const contractPerformanceProcessConfigList=__component__$1.exports,_sfc_main={name:"ContractPerformanceProcessConfig",components:{NavTabs},data(){return{activeTab:"contractPerformanceProcessConfigList",tabs:[{title:this.$t("route.contractPerformanceProcessConfig"),name:"contractPerformanceProcessConfigList",component:contractPerformanceProcessConfigList,closable:!1}]}},activated(){this.activeTab==="contractPerformanceProcessConfigList"&&this.dolayout()},methods:{dolayout(){this.$nextTick(()=>{const data={name:"contractPerformanceProcessConfigList",methods:"dolayout",params:null,random:Math.random()};this.$store.commit("navTabs/SET_NAV_TABS_TODO",data)})},tabChange(tab){tab==="contractPerformanceProcessConfigList"&&this.dolayout(),this.activeTab=tab}}};var _sfc_render=function(){var _vm=this,_c=_vm._self._c;return _c("nav-tabs",{ref:"tabs",attrs:{"tabs-list":_vm.tabs,"cur-tab":_vm.activeTab},on:{"tab-change":_vm.tabChange}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const index=__component__.exports;export{index as default};
