import{N as NavTabs}from"./index-a035e78f.js";import{af as i18nExpression,ae as expression,n as normalizeComponent,al as defineComponent,am as usePageHelper,an as useAttrs,ao as ref$1,ap as useAutoMountInstanceToField,aq as h,ar as defineSchemas,ai as generateXindexInOrder,as as RenderEngine,bt as changeFieldVisibleByDeps}from"./index-17d0ccd5.js";import{a as blackComApi}from"./black-88f03b85.js";import{o as orgCatForm}from"./supApi-e5726083.js";const forms={vendorId:{type:"string","x-decorator":"FormItem","x-hidden":!0},vendorCode:{type:"string","x-decorator":"FormItem","x-hidden":!0},vendorName:{type:"string",title:i18nExpression("common.vendorName"),"x-decorator":"FormItem","x-component":"QuickSearchWrapper","x-component-props":{readPretty:"{{$form.readPretty}}",showKey:"vendorName",propKey:"vendorName",name:"scc_sup_company_info2","@close-quicksearch":expression(`async (val) => {
          if (val) {
            let res = await blackComApi.findByCompanyIdAndStatus(val.companyId)
            if (res.data.length == 0) {
              if (val.companyId) {
                $form.query('.vendorId').take().value = val.companyId
              }
              if (val.companyCode) {
                $form.query('.vendorCode').take().value = val.companyCode
              }
              if (val.companyName) {
                $form.query('.vendorName').take().value = val.companyName
              } else {
                $form.query('.vendorName').take().value = ''
              }
              $form.query('.supplierControlType').take().value = null
            } else {
              $form.query('.vendorName').take().value = ''
              return app.$message({
                type: 'warning',
                message: '当前供应商存在在途的，或已生效的黑名单明细或合作终止单据，无法重复新增'
              })
            }
          } else {
            $form.query('.vendorId').take().value = ''
            $form.query('.vendorCode').take().value = ''
            $form.query('.vendorName').take().value = ''
            $form.query('.supplierControlType').take().value = null
          }
      }`)}},supplierControlType:{type:"string",title:i18nExpression("vendorMod.controlType"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"SUPPLIER_CONTROL_TYPE2","@change":expression(`async(type) => {
        const vendorId = $form.query('.vendorId').take()?.value
        if (!vendorId) {
          app.$message({
            message: $t('vendorMod.msgVendor'), // '请选择供应商',
            type: 'warning'
          })
          return
        }
        const obj = { vendorId: vendorId }
        let result = null
        // 解冻类 品类解冻 组织解冻 整体解冻
        if (['CATEGORY_THAW', 'ORGANIZATION_THAW', 'OVERALL_THAW'].includes(type)) {
          result = await orgCatForm.listForzenOrgCategory(obj)
        } else {
          result = await orgCatForm.listOrgCategory(obj)
        }

        const { data } = result
        if (['ORGANIZATION_EXIT', 'ORGANIZATION_FORZEN', 'ORGANIZATION_THAW'].includes(type)) { // 组织退出 组织冻结
          console.log(data.orgRangeList, 'orgRangeList')
          $form.query('.rangeData').take().value = data.orgRangeList // 组织数据
        } else if (['CATEGORY_EXIT', 'CATEGORY_FORZEN', 'CATEGORY_THAW'].includes(type)) { // 品类退出 品类冻结 品类解冻
          console.log(data.categoryRangeList, 'categoryRangeList')
          $form.query('.rangeData').take().value = data.categoryRangeList // 品类数据
        }
        $form.query('state').get('data').listDataAll = data.detailList
      }`)}},startDate:{type:"string",title:i18nExpression("vendorMod.startDate"),"x-component-props":{disabled:!0},"x-decorator":"FormItem"},orgCatFormNumber:{type:"string",title:i18nExpression("vendorMod.controlNumber"),"x-decorator":"FormItem","x-component-props":{disabled:!0}},approveStatus:{type:"string",title:i18nExpression("vendorMod.operationStatus"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"APPROVE_STATUS_TYPE",disabled:!0}},createdBy:{type:"string",title:i18nExpression("common.creator"),"x-decorator":"FormItem","x-component-props":{disabled:!0}},creationDate:{type:"string",title:i18nExpression("common.creationTime"),"x-decorator":"FormItem","x-component-props":{disabled:!0}}};const _sfc_main$3={name:"Range",components:{},props:{value:{type:Array,default:()=>[]},listDataAll:{type:Array,default:()=>[]},rangeType:{type:String,default:()=>null},curOpt:{type:String,default:()=>null}},data(){return{categoryList:[],rangeList:[],listData:[],listDataShow:[],listDataTotal:0,listPageNum:1,activeDims:["1","2","3","4","5","6"],bol:0}},watch:{listData:{handler(){let listNum=this.listData.length;if(listNum>10){let listDataShow=JSON.parse(JSON.stringify(this.listData));listDataShow=listDataShow.slice(0,10),this.listDataShow=listDataShow}else this.listDataShow=this.listData;this.listDataTotal=listNum},deep:!0},listDataAll:{handler(){["OVERALL_EXIT","OVERALL_FORZEN","OVERALL_THAW"].includes(this.rangeType)?this.listData=this.listDataAll:this.listData=[];let selectedArr=[];this.value.forEach(datass=>{datass.selected=="Y"&&selectedArr.push(datass)}),this.$nextTick(()=>{selectedArr.forEach(selectedItem=>{selectedItem&&this.bol!=1&&this.$refs.multipleTable.toggleRowSelection(selectedItem),["CATEGORY_EXIT","CATEGORY_FORZEN","CATEGORY_THAW"].includes(this.rangeType)&&this.listDataAll.forEach(catItem=>{selectedItem.categoryId==catItem.categoryId&&this.listData.push(catItem)}),["ORGANIZATION_EXIT","ORGANIZATION_FORZEN","ORGANIZATION_THAW"].includes(this.rangeType)&&this.listDataAll.forEach(orgItem=>{selectedItem.orgId==orgItem.orgId&&this.listData.push(orgItem)})}),selectedArr?.length&&(this.bol=1),["CATEGORY_EXIT","CATEGORY_FORZEN","CATEGORY_THAW"].includes(this.rangeType)&&this.listData.forEach(catItem=>{catItem.selected=="Y"&&this.$refs.category.toggleRowSelection(catItem)})})},deep:!0}},mounted(){},created(){},methods:{getCategoryList(){return this.$refs.category.selection||[]},getRangeList(){return this.$refs.multipleTable.selection||[]},setSelectable(){return this.curOpt=="add"||this.curOpt=="edit"},handleCurrentChange(val){let num=(val-1)*10,num10=num+10,listDataShow=this.listData;listDataShow=listDataShow.slice(num,num10),this.listDataShow=listDataShow},handleSelectionChange2(val){this.categoryList=val,this.$emit("listData",val)},handleSelectionChange(val){const listDataAll=this.listDataAll;let listData=[];this.rangeList=val,listDataAll.forEach(datas=>{["CATEGORY_EXIT","CATEGORY_FORZEN","CATEGORY_THAW"].includes(this.rangeType)&&val.forEach(datas2=>{datas.categoryId==datas2.categoryId&&listData.push(datas)}),["ORGANIZATION_EXIT","ORGANIZATION_FORZEN","ORGANIZATION_THAW"].includes(this.rangeType)&&val.forEach(datas2=>{datas.orgId==datas2.orgId&&listData.push(datas)})}),this.listData=listData,this.categoryList=[]}}};var _sfc_render$3=function(){var _vm=this,_c=_vm._self._c;return _c("div",[_c("el-collapse",{staticClass:"tab-form-style",model:{value:_vm.activeDims,callback:function($$v){_vm.activeDims=$$v},expression:"activeDims"}},[["ORGANIZATION_EXIT","ORGANIZATION_FORZEN","ORGANIZATION_THAW","CATEGORY_EXIT","CATEGORY_FORZEN","CATEGORY_THAW"].includes(_vm.rangeType)?_c("el-collapse-item",{attrs:{title:_vm.$t("vendorMod.controlRange"),name:"3"}},[["ORGANIZATION_EXIT","ORGANIZATION_FORZEN","ORGANIZATION_THAW"].includes(_vm.rangeType)?_c("el-table",{ref:"multipleTable",staticStyle:{width:"100%"},attrs:{data:_vm.value,border:""},on:{select:_vm.handleSelectionChange,"select-all":_vm.handleSelectionChange}},[_c("el-table-column",{attrs:{prop:"selected",type:"selection",width:"50",align:"center",selectable:_vm.setSelectable}}),_c("el-table-column",{attrs:{type:"index",label:_vm.$t("vendorMod.numericalOrder"),width:"55",align:"center"}}),_c("el-table-column",{attrs:{prop:"orgName",label:_vm.$t("common.orgName")}})],1):_vm._e(),["CATEGORY_EXIT","CATEGORY_FORZEN","CATEGORY_THAW"].includes(_vm.rangeType)?_c("el-table",{ref:"multipleTable",staticStyle:{width:"100%"},attrs:{data:_vm.value,border:""},on:{select:_vm.handleSelectionChange,"select-all":_vm.handleSelectionChange}},[_c("el-table-column",{attrs:{prop:"selected",type:"selection",width:"55",align:"center",selectable:_vm.setSelectable}}),_c("el-table-column",{attrs:{type:"index",label:_vm.$t("vendorMod.numericalOrder"),width:"55",align:"center"}}),_c("el-table-column",{attrs:{prop:"categoryName",label:_vm.$t("vendorMod.categoryName")}})],1):_vm._e()],1):_vm._e(),_c("el-collapse-item",{attrs:{title:_vm.$t("vendorMod.controlDetail"),name:"4"}},[_c("el-table",{ref:"category",staticStyle:{width:"100%"},attrs:{data:_vm.listDataShow,border:""},on:{select:_vm.handleSelectionChange2,"select-all":_vm.handleSelectionChange2}},[["CATEGORY_EXIT","CATEGORY_FORZEN","CATEGORY_THAW"].includes(_vm.rangeType)?_c("el-table-column",{attrs:{prop:"selected",type:"selection",width:"50",align:"center",selectable:_vm.setSelectable}}):_vm._e(),_c("el-table-column",{attrs:{type:"index",label:_vm.$t("vendorMod.numericalOrder"),width:"55",align:"center"}}),_c("el-table-column",{attrs:{prop:"orgName",label:_vm.$t("common.orgName")}}),_c("el-table-column",{attrs:{prop:"categoryName",label:_vm.$t("vendorMod.categoryName")}})],1),_c("el-pagination",{staticClass:"paginationStyle",attrs:{background:"",layout:"prev, pager, next, sizes, jumper",total:_vm.listDataTotal,"current-page":_vm.listPageNum},on:{"current-change":_vm.handleCurrentChange}})],1)],1)],1)},_sfc_staticRenderFns$3=[],__component__$3=normalizeComponent(_sfc_main$3,_sfc_render$3,_sfc_staticRenderFns$3,!1,null,"5ba9d212",null,null);const range=__component__$3.exports,_sfc_main$2=defineComponent({__name:"cooperationEndedDetail",setup(__props){const{app,emitTabRemove,t,vendor}=usePageHelper(),attrs=useAttrs(),workflowStatus=ref$1("DRAFT"),viewUpdateButton=$form=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;return componentInstance.workflowParamsInfo.integrationMode,["None",null].includes(componentInstance.workflowParamsInfo.integrationMode)?attrs.params.flag!="view"?!0:attrs.params.flag=="view"&&$form.query(".approveStatus").take().value=="PUBLISH":attrs.params.flag!="view"},disabledUpdateButton=()=>!(attrs.params.flag=="view"),updateWorkflowconfig=(componentInstance,businessId,tabDisabled,businessVariables)=>{componentInstance.setWorkflowBusinessId(businessId),componentInstance.setWorkflowTabDisabled(tabDisabled),componentInstance.setWorkflowBusinessVariables(businessVariables)},handleButtonConfig=($form,componentInstance)=>{const orderId=attrs.params.orderId||"",tabDisabled=(attrs.params.row?.approveStatus||null)!="SUBMITTED";updateWorkflowconfig(componentInstance,orderId,tabDisabled,{procTitle:$form.values.orgCatFormNumber})},initButtonConfig=$form=>{setTimeout(()=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.buttonConfigInfo.save.view=viewUpdateButton($form),componentInstance.buttonConfigInfo.submit.view=viewUpdateButton($form),componentInstance.buttonConfigInfo.cancel.view=!0,componentInstance.buttonConfigInfo.close.view=!1,handleButtonConfig($form,componentInstance)},50)},updateButtonConfig=$form=>{setTimeout(()=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.buttonConfigInfo.save.view=viewUpdateButton($form),componentInstance.buttonConfigInfo.submit.view=viewUpdateButton($form),componentInstance.buttonConfigInfo.cancel.view=!1,componentInstance.buttonConfigInfo.close.view=!1,handleButtonConfig($form,componentInstance)},50)},newRange=defineComponent({name:"newRange",props:range.props,setup(props,{listeners,attrs:attrs2,slots}){return useAutoMountInstanceToField(),()=>h(range,{props:{...attrs2,...props},on:listeners,ref:"range"},slots)}}),schema=defineSchemas({state:{type:"void","x-component":"Fragment","x-hidden":!0,"x-data":{listDataAll:[]}},OrgCatForm:{type:"void","x-decorator":"QueryEngine","x-component":"el-container","x-component-props":{class:"flex-container siteAssessment",direction:"vertical"},"x-query-engine":{service:"sup",actions:{save:{cascadeDeletion:!0},query:{immediate:!0,tree:!0,ready:expression(`() => {
            $form.readPretty = $attrs.params.flag === 'view'
            initButtonConfig($form)
            return $attrs.params.flag != 'add' && $attrs.params.orderId
          }`),autoFormatResult:!1,transformRequest:expression(`(data, headers) => {
            data.query = {
              "*":{},
              'rangeList': {'*': {}},
              'detailList': {'*': {}}
            }
            data.payload = {
              "filter": {
                  "orgCatFormId": {
                      eq: $attrs.params.orderId
                  }
              }
            }
            return data
          }`),transformResponse:expression(`(res) => {
            const ress = JSON.parse(res)
            const data = ress.data.records[0]
            $form.setValues(data)

            $form.query('.businessMatterType').take().value = data.businessMatterType?.split(',')
            $form.query('.rangeData').take().value = data.rangeList
            $form.query('state').get('data').listDataAll = data.detailList

            return ress
          }`)}}},properties:{SchemaWorkflow:{type:"void","x-component":"SchemaWorkflow","x-component-props":{"business-id":expression("$attrs.params.row?.siteFormId || null"),"business-type":"COOPERATIONEND","@click-handler":expression(`(type) => {
            $submits(type, $form, $queryEngine, $message, $t, $bus)
          }`),"@submit-direct":expression(`(type) => {
            $submits(type, $form, $queryEngine, $message, $t, $bus)
          }`),"@confirm":expression(`(type, comment) => {
            $submits(type, $form, $queryEngine, $message, $t, $bus)
          }`),"@close-tab":expression(`() => {
            $back($bus)
          }`),"@update-integration-mode":expression(`(integrationMode) => {
            if (integrationMode.integrationMode == "None") {
              updateButtonConfig($form)
            }
          }`)},properties:{layout:{type:"void","x-component":"FormContainer",properties:{layout:{type:"void","x-component":"FormContainer",properties:{collapse:{type:"void","x-component":"Collapse",properties:generateXindexInOrder({cooperationEndForm:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.cooperationEndedForm")},"x-query-engine-skip":!0,properties:{coopForm:{type:"void","x-query-engine-skip":!0,"x-component":"FormGrid","x-component-props":{maxColumns:4,columnGap:32,rowGap:0},properties:{...forms}}}}})},rangeData:{type:"Array","x-component":"newRange","x-component-props":{style:"margin-top:-5px;","range-type":expression("$form.query('.supplierControlType').take()?.value"),"list-data-all":expression("$form.query('state').get('data')?.listDataAll"),"cur-opt":expression("$attrs.params.flag")}},collapseBusiness:{type:"void","x-component":"Collapse","x-component-props":{style:"margin-top:-5px;border-top:0"},properties:generateXindexInOrder({businessForm:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.businessMatter")},"x-query-engine-skip":!0,properties:{businessList:{type:"void","x-component":"FormGrid","x-component-props":{maxColumns:1,columnGap:32,rowGap:0},properties:{businessMatterType:{type:"array",title:"","x-decorator":"FormItem","x-component":"Checkbox.Group",enum:[{label:i18nExpression("vendorMod.cooperationEndedDetail[0]"),value:"20"},{label:i18nExpression("vendorMod.cooperationEndedDetail[1]"),value:"50"},{label:i18nExpression("vendorMod.cooperationEndedDetail[2]"),value:"30"},{label:i18nExpression("vendorMod.cooperationEndedDetail[3]"),value:"40"},{label:i18nExpression("vendorMod.cooperationEndedDetail[4]"),value:"10"}]},otherExplain:{type:"string",title:i18nExpression("vendorMod.cooperationEndedDetail[5]"),"x-decorator":"FormItem","x-component-props":{type:"textarea"}}}}}}})}}}}}}}}}}),$back=$bus=>{emitTabRemove(attrs.tabName),$bus.$emit("siteA")},$submits=async(type,$form,$queryEngine,$message,$t,$bus)=>{const submitData=JSON.parse(JSON.stringify($form.values));let rangeData=submitData.rangeData,listDataAll=JSON.parse(JSON.stringify($form.query("state").get("data").listDataAll));const rangeDatas=$form.query(".rangeData").take().componentProps.componentInstance.$refs.range;if(["CATEGORY_EXIT","CATEGORY_FORZEN","CATEGORY_THAW"].includes(submitData.supplierControlType)){let bol2=0,optSelectRange=rangeDatas.rangeList.length>0?rangeDatas.rangeList:rangeDatas.getRangeList();if(rangeData.forEach(datas=>{let categoryId=datas.categoryId;optSelectRange.findIndex(i=>i.categoryId==categoryId)>-1?(datas.selected="Y",bol2+=1):datas.selected="N"}),bol2==0)return app.$message.warning(app.$t("dataConfMod.msgInputCate")),!1;let bol1=0,selectedCategoryList=rangeDatas.categoryList.length>0?rangeDatas.categoryList:rangeDatas.getCategoryList();if(listDataAll.forEach(datas=>{let rowKey=datas.orgId+"_"+datas.categoryId;selectedCategoryList.findIndex(i=>i.orgId+"_"+i.categoryId==rowKey)>-1?(datas.selected="Y",bol1+=1):datas.selected="N"}),bol1==0)return app.$message.warning(app.$t("dataConfMod.msgInputUnit2")),!1}else if(["ORGANIZATION_EXIT","ORGANIZATION_FORZEN","ORGANIZATION_THAW"].includes(submitData.supplierControlType)){let bol1=0,optSelectRange=rangeDatas.rangeList.length>0?rangeDatas.rangeList:rangeDatas.getRangeList();if(rangeData.forEach(datas=>{let orgId=datas.orgId;optSelectRange.findIndex(i=>i.orgId==orgId)>-1?(datas.selected="Y",bol1+=1):datas.selected="N"}),bol1==0)return app.$message.warning(app.$t("dataConfMod.msgInputUnit2")),!1;let rangeObj={};rangeData.forEach(rangeItem=>{rangeItem.selected=="Y"&&(rangeObj[rangeItem.orgId]=rangeItem)}),listDataAll.forEach(datas=>{rangeObj.hasOwnProperty(datas.orgId)?datas.selected="Y":datas.selected="N"})}else rangeData=[],listDataAll.forEach(datas=>{datas.selected="Y"});if(submitData.rangeList=rangeData,submitData.detailList=listDataAll,$form.query(".businessMatterType").take()?.value){const businessMatterType=JSON.parse(JSON.stringify($form.query(".businessMatterType").take()?.value));submitData.businessMatterType=businessMatterType.join(",")}return delete submitData.rangeData,(async()=>{[null,void 0,"DRAFT"].includes(submitData.approveStatus)&&(submitData.approveStatus="DRAFT"),type=="SAVE"?$queryEngine.request.save(submitData,{query:{"*":{},OrgCatFormCategoryRange:{"*":{}},OrgCatFormCategoryDetail:{"*":{}}},loading:!0}).then(()=>{$message.success($t("common.successSave")),$bus.$emit("cooperationEnd"),emitTabRemove(attrs.tabName)}).catch(err=>{}):$queryEngine.request.save(submitData,{query:{"*":{},OrgCatFormCategoryRange:{"*":{}},OrgCatFormCategoryDetail:{"*":{}}},tree:!0,loading:!0}).then(res=>{submitData.orgCatFormId=res.data[0].orgCatFormId;const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.setWorkflowBusinessId(res.data[0]?.orgCatFormId||null),componentInstance.setWorkflowTabDisabled(!0),componentInstance.setWorkflowBusinessVariables({procTitle:$form.values.orgCatFormNumber}),componentInstance.handlerAfter(type.toUpperCase(),()=>{$bus.$emit("cooperationEnd"),emitTabRemove(attrs.tabName)})})})()};return{__sfc:!0,app,emitTabRemove,t,vendor,attrs,workflowStatus,viewUpdateButton,disabledUpdateButton,updateWorkflowconfig,handleButtonConfig,initButtonConfig,updateButtonConfig,newRange,schema,$back,$submits,scope:{app,t,$attrs:attrs,emitTabRemove,initButtonConfig,$back,$submits,blackComApi,orgCatForm},components:{newRange},RenderEngine}}});var _sfc_render$2=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{schemaKey:"siteAssessmentDetail",pageAttrs:_vm.$attrs,schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns$2=[],__component__$2=normalizeComponent(_sfc_main$2,_sfc_render$2,_sfc_staticRenderFns$2,!1,null,null,null,null);const endDetail=__component__$2.exports,_sfc_main$1=defineComponent({__name:"cooperationEndedList",setup(__props){const{emitTabAdd,app}=usePageHelper(),schema=defineSchemas({OrgCatForm:{type:"void","x-decorator":"QueryEngine","x-query-engine":{service:"sup",actions:{paginationQuery:{immediate:!0}}},"x-component":"el-container","x-component-props":{class:"flex-container the_contractTemplateList_wrapper",direction:"vertical"},properties:{bus:{type:"void","x-component":"BusEvent","x-component-props":{eventName:"cooperationEnd","@listener":expression(`() => {
            $queryEngine.state.paginationManagement.refresh()
          }`)}},query:{type:"object","x-query-engine-skip":!0,"x-component":"QueryFormByQueryEngine",properties:generateXindexInOrder({orgCatFormNumber:{type:"string",title:i18nExpression("vendorMod.controlNumber"),"x-query-engine-query-operator":"contains"},vendorId:{type:"string",title:i18nExpression("common.vendorName"),"x-component":"QuickSearchWrapper","x-component-props":{showKey:"companyName",propKey:"companyId",name:"scc_sup_company_info_all"}},approveStatus:{type:"string",title:i18nExpression("common.status"),"x-component":"DictSelect","x-component-props":{code:"APPROVE_STATUS_TYPE"}},supplierControlType:{type:"string",title:i18nExpression("vendorMod.controlType"),"x-component":"DictSelect","x-component-props":{code:"SUPPLIER_CONTROL_TYPE2"}}})},toolbar:{type:"void","x-component":"Space","x-component-props":{style:"margin-bottom: 16px"},properties:{add:{type:"void",title:i18nExpression("common.add"),"x-component":"RButton","x-component-props":{type:"primary","@click":expression(`() => {
                let tab = {
                  component: endDetail,
                  params: {
                    flag: 'add',
                    tabName: 'endDetail'
                  },
                  title: $t('vendorMod.addSite'),
                  name: 'endDetail'
                }
                emitTabAdd(tab)
              }`)}}}},table:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",class:"table-view-vxe-table",openCustomTable:!0},properties:generateXindexInOrder({approveStatus:{type:"string",title:i18nExpression("common.status"),"x-component":"DictSelect","x-component-props":{code:"APPROVE_STATUS_TYPE"},"x-render-table-column":{width:120}},vendorId:{type:"string","x-hidden":!0,"x-render-table-column":{width:120}},vendorCode:{type:"string",title:i18nExpression("common.vendorCode"),"x-render-table-column":{width:120}},vendorName:{type:"string",title:i18nExpression("common.vendorName"),"x-render-table-column":{width:120}},orgCatFormId:{type:"string","x-hidden":!0,"x-render-table-column":{width:120}},orgCatFormNumber:{"x-component":"TableButton","x-component-props":{type:"text","@click":expression(`({row}) => {
                let orgCatFormId = row.orgCatFormId
                let tab = {
                  component: endDetail,
                  params: {
                    flag: 'view',
                    orderId: orgCatFormId,
                    tabName: 'CooperationEndedDetail' + row.vendorName
                  },
                  title: row.vendorName,
                  name: 'CooperationEndedDetail' + row.vendorName
                }
                emitTabAdd(tab)
              }`)},"x-render-table-column":{title:i18nExpression("vendorMod.controlNumber"),minWidth:200,customRender:!0}},supplierControlType:{type:"string",title:i18nExpression("vendorMod.controlType"),"x-component":"DictSelect","x-component-props":{code:"SUPPLIER_CONTROL_TYPE2"},"x-render-table-column":{width:120}},createdBy:{type:"string",title:i18nExpression("common.creator"),"x-render-table-column":{width:120}},creationDate:{type:"string",title:i18nExpression("common.creationTime"),"x-render-table-column":{width:120}},startDate:{type:"string",title:i18nExpression("common.effectTime"),"x-render-table-column":{width:120}},lastUpdateDate:{type:"string","x-hidden":!0,"x-query-engine-sort":"desc","x-render-table-column":{width:120}},operation:{type:"void",title:i18nExpression("common.operation"),"x-render-table-column":{width:200,fixed:"right"},properties:{edit:{type:"void",title:i18nExpression("common.edit"),"x-component":"TableButton","x-reactions":changeFieldVisibleByDeps([".approveStatus"],"['DRAFT', 'WITHDRAW', 'REJECTED'].includes($deps[0])"),"x-component-props":{type:"text","@click":expression(`({ row }) => {
                    const orgCatFormId = row.orgCatFormId
                    const tab = {
                      component: endDetail,
                      params: {
                        flag: 'edit',
                        orderId: orgCatFormId,
                        tabName: 'CooperationEndedDetail' + row.vendorName,
                        row
                      },
                      title: row.vendorName,
                      name: 'CooperationEndedDetail' + row.vendorName
                    }
                    emitTabAdd(tab)
                  }`)}},delete:{type:"void",title:i18nExpression("common.delete"),"x-component":"TableButton","x-reactions":changeFieldVisibleByDeps([".approveStatus"],"$deps[0] === 'DRAFT'"),"x-component-props":{style:"margin-left: 8px",showPopconfirm:!0,"@confirm":expression(`({ row }) => {
                    $queryEngine.request.delete(row.orgCatFormId).then(() => {
                      $message.success($t('common.successDelete'))
                      $queryEngine.state.paginationManagement.refresh()
                    })
                  }`)}},doApproval:{type:"void",title:i18nExpression("vendorMod.doApproval"),"x-component":"TableButton","x-reactions":changeFieldVisibleByDeps([".approveStatus"],"['SUBMITTED'].includes($deps[0]) && $buyer()"),"x-component-props":{type:"text","@click":expression(`({ row }) => {
                    const orgCatFormId = row.orgCatFormId
                    const tab = {
                      component: endDetail,
                      params: {
                        flag: 'view',
                        orderId: orgCatFormId,
                        tabName: 'CooperationEndedDetail' + row.vendorName,
                        row
                      },
                      title: row.vendorName,
                      name: 'CooperationEndedDetail' + row.vendorName
                    }
                    emitTabAdd(tab)
                  }`)}},abandon:{type:"void",title:i18nExpression("common.abandon"),"x-reactions":changeFieldVisibleByDeps([".approveStatus"],"['REJECTED', 'WITHDRAW'].includes($deps[0])"),"x-component-props":{type:"text","@click":expression(`({ row }) => {
                    const orgCatFormId = row.orgCatFormId
                    const tab = {
                      component: endDetail,
                      params: {
                        flag: 'view',
                        orderId: orgCatFormId,
                        tabName: 'CooperationEndedDetail' + row.vendorName,
                        row
                      },
                      title: row.vendorName,
                      name: 'CooperationEndedDetail' + row.vendorName
                    }
                    emitTabAdd(tab)
                  }`)}}}}})}}}});return{__sfc:!0,emitTabAdd,app,schema,scope:{emitTabAdd,app,i18nExpression,endDetail},components:{},RenderEngine}}});var _sfc_render$1=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{staticClass:"contractPaymentType",attrs:{schemaKey:"cooperationList",schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns$1=[],__component__$1=normalizeComponent(_sfc_main$1,_sfc_render$1,_sfc_staticRenderFns$1,!1,null,null,null,null);const cooperationEndedList=__component__$1.exports,_sfc_main={name:"CooperationEnded",components:{NavTabs},data(){return{activeTab:"cooperationEndedList",tabs:[{title:()=>this.$t("vendorMod.cooperationEnded"),name:"cooperationEndedList",component:cooperationEndedList,closable:!1}]}}};var _sfc_render=function(){var _vm=this,_c=_vm._self._c;return _c("NavTabs",{ref:"tabs",attrs:{"tabs-list":_vm.tabs,"cur-tab":_vm.activeTab}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const index=__component__.exports;export{index as default};
