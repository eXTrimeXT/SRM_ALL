import{N as NavTabs}from"./index-a035e78f.js";import{af as i18nExpression,ae as expression,n as normalizeComponent,H as FormWrapper,h as http,al as defineComponent,am as usePageHelper,an as useAttrs,ao as ref$1,ap as useAutoMountInstanceToField,aq as h,ar as defineSchemas,ai as generateXindexInOrder,as as RenderEngine,c7 as buttonListItemVisibleByPermission,bt as changeFieldVisibleByDeps}from"./index-17d0ccd5.js";import{a as blackComApi}from"./black-88f03b85.js";import{u as uniqueId}from"./uniqueId-bf6f89eb.js";const forms={vendorId:{type:"string","x-decorator":"FormItem","x-hidden":!0},vendorCode:{type:"string","x-decorator":"FormItem","x-hidden":!0},vendorName:{type:"string",title:i18nExpression("common.vendorName"),"x-decorator":"FormItem","x-component":"QuickSearchWrapper","x-component-props":{readPretty:"{{$form.readPretty}}",showKey:"vendorName",propKey:"vendorName",name:"scc_sup_company_info2","@close-quicksearch":expression(`async (val) => {
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
        // 获取当前控制范围
        const rangeData = $form.query('rangeData').take().value
        // 获取当前控制明细
        const rangeDetail = $form.query('state').get('data').listDataAll
        // 获取删除数据行
        let rangeDeleteList = $form.query('state').get('data').rangeDeleteList
        rangeData?.forEach(item => {
          if (item.orgCatFormCategoryId) {
            rangeDeleteList.push({
              $delete: item.orgCatFormCategoryId
            })
          }
        })
        // 获取删除数据明细行
        let rangeDetailDeleteList = $form.query('state').get('data').rangeDetailDeleteList
        rangeDetail?.forEach(item => {
          if (item.orgCatFormCategoryId) {
            rangeDetailDeleteList.push({
              $delete: item.orgCatFormCategoryId
            })
          }
        })
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
        } else if (['POSITION_LIMIT_FLAG_REMOVE', 'CATEGORY_LIMIT_FLAG_REMOVE'].includes(type)){
          if (type === 'CATEGORY_LIMIT_FLAG_REMOVE') {
            obj.supplierControlType = type
          }
          result = await orgCatForm.orgOrCategoryLimitRemove(obj)
        } else if (['CATEGORY_LIMIT_FLAG', 'POSITION_LIMIT_FLAG','CATEGORY_EXIT','ORGANIZATION_EXIT','OVERALL_EXIT'].includes(type)){
          result = await orgCatForm.orgOrCategoryLimit(obj)
        } else {
          result = await orgCatForm.listOrgCategory(obj)
        }

        const { data } = result
        if (['ORGANIZATION_EXIT', 'ORGANIZATION_FORZEN', 'ORGANIZATION_THAW', 'POSITION_LIMIT_FLAG', 'POSITION_LIMIT_FLAG_REMOVE'].includes(type)) { // 组织退出 组织受限 组织受限解除
          $form.query('.rangeData').take().value = data.orgRangeList // 组织数据
        } else if (['CATEGORY_EXIT', 'CATEGORY_FORZEN', 'CATEGORY_THAW', 'CATEGORY_LIMIT_FLAG', 'CATEGORY_LIMIT_FLAG_REMOVE'].includes(type)) { // 品类退出 品类受限
          $form.query('.rangeData').take().value = data.categoryRangeList // 品类数据
        }
        $form.query('state').get('data').listDataAll = data.detailList
      }`)}},startDate:{type:"date",title:i18nExpression("vendorMod.startDate"),"x-decorator":"FormItem","x-validator":{required:!0,message:i18nExpression("common.requiredField")}},orgCatFormNumber:{type:"string",title:i18nExpression("vendorMod.controlNumber"),"x-decorator":"FormItem","x-component-props":{disabled:!0}},approveStatus:{type:"string",title:i18nExpression("vendorMod.operationStatus"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"APPROVE_STATUS_TYPE",disabled:!0}},createdBy:{type:"string",title:i18nExpression("common.creator"),"x-decorator":"FormItem","x-component-props":{disabled:!0}},creationDate:{type:"string",title:i18nExpression("common.creationTime"),"x-decorator":"FormItem","x-component-props":{disabled:!0}},timeLimitDate:{type:"date",title:i18nExpression("cusEntry.vendorMod.limitDeadLine"),"x-decorator":"FormItem","x-validator":{required:expression("$form.values.supplierControlType === 'TIME_LIMIT_FLAG'"),message:i18nExpression("cusEntry.tipMessage.limitDeadLineMsg")},"x-visible":"{{$form.query('.supplierControlType').take().value == 'TIME_LIMIT_FLAG'}}"},cooperationEndedReasonType:{type:"string",title:i18nExpression("cusEntry.vendorMod.cooperationEndedReasonType"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"SUPPLIER_LIMIT_TYPE"}}};const _sfc_main$3={name:"Range",components:{FormWrapper},props:{value:{type:Array,default:()=>[]},listDataAll:{type:Array,default:()=>[]},rangeType:{type:String,default:()=>null},curOpt:{type:String,default:()=>null}},data(){return{queryForm:{},queryConfig:[{prop:"companyName",label:()=>this.$t("cusEntry.vendorMod.companyName")}],queryOrgConfig:[{prop:"companyName",label:()=>this.$t("cusEntry.vendorMod.companyName")}],paginationInfo:{pageSize:10,pageNum:1},categoryList:[],rangeList:[],targetList:[],listData:[],showOrgRecords:[],listDataShow:[],listPageNum:1,activeDims:["1","2","3","4","5","6"],bol:0}},watch:{listData:{handler(newValue){if(this.listData.length>10){let listDataShow=JSON.parse(JSON.stringify(this.listData));listDataShow=listDataShow.slice(0,10),this.listDataShow=listDataShow}else this.listDataShow=this.listData;this.targetList=newValue},deep:!0},listDataAll:{handler(){["OVERALL_EXIT","OVERALL_FORZEN","OVERALL_THAW"].includes(this.rangeType)?this.listData=this.listDataAll:this.listData=[]},deep:!0},value:{immediate:!0,deep:!0,handler(newValue){this.showOrgRecords=newValue||[];let selectedArr=[];newValue.forEach(datass=>{(datass.selected=="Y"||this.rangeList.map(item=>item.categoryCode).includes(datass.categoryCode))&&selectedArr.push(datass)}),this.$nextTick(()=>{selectedArr.forEach(selectedItem=>{selectedItem&&(this.$refs.multipleTable.toggleRowSelection(selectedItem),setTimeout(()=>{this.$refs.multipleTable.doLayout()},100)),["CATEGORY_EXIT","CATEGORY_FORZEN","CATEGORY_THAW","CATEGORY_LIMIT_FLAG","CATEGORY_LIMIT_FLAG_REMOVE"].includes(this.rangeType)&&this.listDataAll.forEach(catItem=>{selectedItem.categoryId==catItem.categoryId&&this.listData.push(catItem)}),["ORGANIZATION_EXIT","ORGANIZATION_FORZEN","ORGANIZATION_THAW"].includes(this.rangeType)&&this.listDataAll.forEach(orgItem=>{selectedItem.orgId==orgItem.orgId&&this.listData.push(orgItem)})}),["CATEGORY_EXIT","CATEGORY_FORZEN","CATEGORY_THAW","CATEGORY_LIMIT_FLAG","CATEGORY_LIMIT_FLAG_REMOVE"].includes(this.rangeType)&&this.listData.forEach(catItem=>{catItem.selected=="Y"&&(this.$refs.category.toggleRowSelection(catItem),setTimeout(()=>{this.$refs.category.doLayout()},100))})})}}},mounted(){},created(){},methods:{getRowKey(row,key){return uniqueId(`${row[key]}_${Date.now()}`)},filterHandler(queryForm){this.queryForm=queryForm;const{companyName}=queryForm||{};if(companyName){const{pageSize,pageNum}=this.paginationInfo,targetList=this.listData.filter(item=>item.orgName.includes(companyName));this.targetList=targetList,this.listDataShow=targetList.slice((pageNum-1)*pageSize,pageNum*pageSize)}else this.targetList=this.listData,this.listDataShow=this.targetList.slice(0,10)},filterOrgHandler(queryForm){const{companyName}=queryForm||{};if(companyName){const targetList=this.value.filter(item=>item.orgName.includes(companyName));this.showOrgRecords=targetList}else this.showOrgRecords=this.value},getCategoryList(){return this.$refs.category.selection||[]},getRangeList(){return this.$refs.multipleTable.selection||[]},setSelectable(){return this.curOpt=="add"||this.curOpt=="edit"},handleCurrentChange(pageNum){this.paginationInfo.pageNum=pageNum;const pageSize=this.paginationInfo.pageSize;this.listDataShow=this.targetList.slice((pageNum-1)*pageSize,pageNum*pageSize)},sizeChange(pageSize){this.paginationInfo.pageSize=pageSize;const pageNum=this.paginationInfo.pageNum;this.listDataShow=this.targetList.slice((pageNum-1)*pageSize,pageNum*pageSize)},handleSelectionChange2(val){this.categoryList=val,this.$emit("listData",val)},handleSelectionChange(val){const listDataAll=this.listDataAll;let listData=[];this.rangeList=val,listDataAll.forEach(datas=>{["CATEGORY_EXIT","CATEGORY_FORZEN","CATEGORY_THAW","CATEGORY_LIMIT_FLAG","CATEGORY_LIMIT_FLAG_REMOVE"].includes(this.rangeType)&&val.forEach(datas2=>{datas.categoryId==datas2.categoryId&&listData.push(datas)}),["ORGANIZATION_EXIT","ORGANIZATION_FORZEN","ORGANIZATION_THAW"].includes(this.rangeType)&&val.forEach(datas2=>{datas.orgId==datas2.orgId&&listData.push(datas)})}),this.listData=listData,this.categoryList=[]}}};var _sfc_render$3=function(){var _vm=this,_c=_vm._self._c;return _c("div",[_c("el-collapse",{staticClass:"tab-form-style",model:{value:_vm.activeDims,callback:function($$v){_vm.activeDims=$$v},expression:"activeDims"}},[["ORGANIZATION_EXIT","ORGANIZATION_FORZEN","ORGANIZATION_THAW","CATEGORY_EXIT","CATEGORY_FORZEN","CATEGORY_THAW","CATEGORY_LIMIT_FLAG","CATEGORY_LIMIT_FLAG_REMOVE","POSITION_LIMIT_FLAG","POSITION_LIMIT_FLAG_REMOVE"].includes(_vm.rangeType)?_c("el-collapse-item",{attrs:{title:_vm.$t("vendorMod.controlRange"),name:"3"}},[["ORGANIZATION_EXIT","ORGANIZATION_FORZEN","ORGANIZATION_THAW","POSITION_LIMIT_FLAG","POSITION_LIMIT_FLAG_REMOVE"].includes(_vm.rangeType)?_c("FormWrapper",{attrs:{"form-array":_vm.queryOrgConfig},on:{getFormData:_vm.filterOrgHandler}}):_vm._e(),["ORGANIZATION_EXIT","ORGANIZATION_FORZEN","ORGANIZATION_THAW","POSITION_LIMIT_FLAG","POSITION_LIMIT_FLAG_REMOVE"].includes(_vm.rangeType)?_c("el-table",{ref:"multipleTable",staticStyle:{width:"100%"},attrs:{"row-key":row=>_vm.getRowKey(row,"categoryCode"),data:_vm.showOrgRecords,border:""},on:{select:_vm.handleSelectionChange,"select-all":_vm.handleSelectionChange}},[_c("el-table-column",{attrs:{prop:"selected",type:"selection",width:"50",align:"center",selectable:_vm.setSelectable}}),_c("el-table-column",{attrs:{type:"index",label:_vm.$t("vendorMod.numericalOrder"),width:"55",align:"center"}}),_c("el-table-column",{attrs:{prop:"orgName",label:_vm.$t("cusEntry.vendorMod.orgName")}})],1):_vm._e(),["CATEGORY_EXIT","CATEGORY_FORZEN","CATEGORY_THAW","CATEGORY_LIMIT_FLAG","CATEGORY_LIMIT_FLAG_REMOVE","CATEGORY_LIMIT_FLAG"].includes(_vm.rangeType)?_c("el-table",{ref:"multipleTable",staticStyle:{width:"100%"},attrs:{"row-key":row=>_vm.getRowKey(row,"categoryCode"),data:_vm.showOrgRecords,border:""},on:{select:_vm.handleSelectionChange,"select-all":_vm.handleSelectionChange}},[_c("el-table-column",{attrs:{prop:"selected",type:"selection",width:"55",align:"center",selectable:_vm.setSelectable}}),_c("el-table-column",{attrs:{type:"index",label:_vm.$t("vendorMod.numericalOrder"),width:"55",align:"center"}}),_c("el-table-column",{attrs:{prop:"categoryName",label:_vm.$t("vendorMod.categoryName")}})],1):_vm._e()],1):_vm._e(),["CATEGORY_EXIT","ORGANIZATION_EXIT","OVERALL_EXIT","CATEGORY_LIMIT_FLAG","CATEGORY_LIMIT_FLAG_REMOVE"].includes(_vm.rangeType)?_c("el-collapse-item",{attrs:{title:_vm.$t("vendorMod.controlDetail"),name:"4"}},[_c("FormWrapper",{attrs:{"form-array":_vm.queryConfig},on:{getFormData:_vm.filterHandler}}),_c("el-table",{ref:"category",staticStyle:{width:"100%"},attrs:{data:_vm.listDataShow,border:"","row-key":row=>_vm.getRowKey(row,"orgId")},on:{select:_vm.handleSelectionChange2,"select-all":_vm.handleSelectionChange2}},[["CATEGORY_EXIT","CATEGORY_FORZEN","CATEGORY_THAW","CATEGORY_LIMIT_FLAG","CATEGORY_LIMIT_FLAG_REMOVE"].includes(_vm.rangeType)?_c("el-table-column",{attrs:{prop:"selected",type:"selection",width:"50",align:"center","reserve-selection":"",selectable:_vm.setSelectable}}):_vm._e(),_c("el-table-column",{attrs:{type:"index",label:_vm.$t("vendorMod.numericalOrder"),width:"55",align:"center"}}),_c("el-table-column",{attrs:{prop:"orgName",label:_vm.$t("cusEntry.vendorMod.orgName")}}),_c("el-table-column",{attrs:{prop:"categoryName",label:_vm.$t("vendorMod.categoryName")}})],1),_c("el-pagination",{staticClass:"paginationStyle",attrs:{background:"",layout:"prev, pager, next, sizes, jumper",total:_vm.targetList.length,"current-page":_vm.listPageNum,"page-sizes":[10,20,30,40,50,100,500,1e3]},on:{"current-change":_vm.handleCurrentChange,"size-change":_vm.sizeChange}})],1):_vm._e()],1)],1)},_sfc_staticRenderFns$3=[],__component__$3=normalizeComponent(_sfc_main$3,_sfc_render$3,_sfc_staticRenderFns$3,!1,null,"de0c53b7",null,null);const range=__component__$3.exports,getUrl=path=>`${path}`,orgCatForm={orgOrCategoryLimitRemove:async data=>http({url:getUrl("/api-sup/pj/orgCatForm/listOrgCategoryInfoByVendorId"),method:"POST",data,loading:!0}),orgOrCategoryLimit:async data=>http({url:getUrl("/api-sup/pj/orgCatForm/listOrgCategoryInfoByVendorIdForLimt"),method:"POST",data,loading:!0}),listOrgCategory:async data=>http({url:getUrl("/api-sup/orgcategory/orgCatForm/listOrgCategoryInfoByVendorId"),method:"POST",data,loading:!0}),listForzenOrgCategory:async data=>http({url:getUrl("/api-sup/orgcategory/orgCatForm/listForzenOrgCategoryInfoByVendorId"),method:"POST",data,loading:!0}),listByTime:async data=>http({url:getUrl("/api-sup/sup/categoryState/listByTime"),method:"POST",data,loading:!0}),getDetail:async id=>http({url:getUrl("/api-sup/orgcategory/orgCatForm/get"),method:"GET",params:{id},loading:!0})},_sfc_main$2=defineComponent({__name:"cooperationEndedDetail",setup(__props){const{app,emitTabRemove,t,vendor}=usePageHelper(),attrs=useAttrs(),workflowStatus=ref$1("DRAFT"),viewUpdateButton=$form=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;return["None",null].includes(componentInstance.workflowParamsInfo.integrationMode)?attrs.params.flag!="view"?!0:attrs.params.flag=="view"&&$form.query(".approveStatus").take().value=="PUBLISH":attrs.params.flag!="view"},disabledUpdateButton=()=>!(attrs.params.flag=="view"),updateWorkflowconfig=(componentInstance,businessId,tabDisabled,businessVariables)=>{componentInstance.setWorkflowBusinessId(businessId),componentInstance.setWorkflowTabDisabled(tabDisabled),componentInstance.setWorkflowBusinessVariables(businessVariables)},handleButtonConfig=($form,componentInstance)=>{const orderId=attrs.params.orderId||"",approveStatus=attrs.params.row?.approveStatus||null,tabDisabled=approveStatus!="SUBMITTED";componentInstance.setWorkflowApproveStatus(approveStatus),updateWorkflowconfig(componentInstance,orderId,tabDisabled,{procTitle:$form.values.orgCatFormNumber})},initButtonConfig=$form=>{setTimeout(()=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.buttonConfigInfo.save.view=viewUpdateButton($form),componentInstance.buttonConfigInfo.submit.view=viewUpdateButton($form),componentInstance.buttonConfigInfo.cancel.view=!0,componentInstance.buttonConfigInfo.close.view=!1,handleButtonConfig($form,componentInstance)},50)},updateButtonConfig=$form=>{setTimeout(()=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.buttonConfigInfo.save.view=viewUpdateButton($form),componentInstance.buttonConfigInfo.submit.view=viewUpdateButton($form),componentInstance.buttonConfigInfo.cancel.view=!1,componentInstance.buttonConfigInfo.close.view=!1,(attrs.params.row?.approveStatus||null)=="SUBMITTED"&&componentInstance.workflowParamsInfo.integrationMode=="Push"&&(componentInstance.buttonConfigInfo.withdraw.view=!0),handleButtonConfig($form,componentInstance)},50)},newRange=defineComponent({name:"newRange",props:range.props,setup(props,{listeners,attrs:attrs2,slots}){return useAutoMountInstanceToField(),()=>h(range,{props:{...attrs2,...props},on:listeners,ref:"range"},slots)}}),schema=defineSchemas({state:{type:"void","x-component":"Fragment","x-hidden":!0,"x-data":{listDataAll:[],rangeDeleteList:[],rangeDetailDeleteList:[]}},OrgCatForm:{type:"void","x-decorator":"QueryEngine","x-component":"el-container","x-component-props":{class:"flex-container siteAssessment",direction:"vertical"},"x-query-engine":{service:"sup",actions:{save:{cascadeDeletion:!0},query:{immediate:!0,tree:!0,ready:expression(`() => {
            $form.readPretty = $attrs.params.flag === 'view'
            initButtonConfig($form)
            return $attrs.params.flag != 'add' && $attrs.params.orderId
          }`),autoFormatResult:!1,transformRequest:expression(`(data, headers) => {
            data.query = {
              "*":{},
              'rangeList': {'*': {}},
              'detailList': {'*': {}},
              'fileList': {'*': {}}
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
            $form.query('.rangeData').take().value = data.rangeList
            $form.query('state').get('data').listDataAll = data.detailList
            return ress
          }`)}}},properties:{SchemaWorkflow:{type:"void","x-component":"SchemaWorkflow","x-component-props":{"business-id":expression("$attrs.params.row?.siteFormId || null"),"business-type":"supplierLimitation","@click-handler":expression(`(type) => {
            $submits(type, $form, $queryEngine, $message, $t, $bus)
          }`),"@submit-direct":expression(`(type) => {
            $submits(type, $form, $queryEngine, $message, $t, $bus)
          }`),"@confirm":expression(`(type, comment) => {
            $submits(type, $form, $queryEngine, $message, $t, $bus)
          }`),"@close-tab":expression(`() => {
            $back($bus)
          }`),"@update-integration-mode":expression(`(integrationMode) => {
            updateButtonConfig($form)
          }`)},properties:{layout:{type:"void","x-component":"FormContainer",properties:{layout:{type:"void","x-component":"FormContainer",properties:{collapse:{type:"void","x-component":"Collapse",properties:generateXindexInOrder({cooperationEndForm:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("cusEntry.vendorMod.cooperationEndedForm")},"x-query-engine-skip":!0,properties:{coopForm:{type:"void","x-query-engine-skip":!0,"x-component":"FormGrid","x-component-props":{maxColumns:4,columnGap:32,rowGap:0},properties:{...forms}}}}})},rangeData:{type:"Array","x-component":"newRange","x-component-props":{style:"margin-top:-5px;","range-type":expression("$form.query('.supplierControlType').take()?.value"),"list-data-all":expression("$form.query('state').get('data')?.listDataAll"),"cur-opt":expression("$attrs.params.flag")}},collapseBusiness:{type:"void","x-component":"Collapse","x-component-props":{style:"margin-top:-5px;border-top:0"},properties:generateXindexInOrder({businessForm:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("cusEntry.vendorMod.other")},"x-query-engine-skip":!0,properties:{businessList:{type:"void","x-component":"FormGrid","x-component-props":{maxColumns:1,columnGap:32,rowGap:0},properties:{otherExplain:{type:"string",title:i18nExpression("vendorMod.cooperationEndedDetail[5]"),"x-decorator":"FormItem","x-component-props":{type:"textarea"},"x-validator":{required:!0,message:i18nExpression("common.requiredField")}}}}}},file:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("cusEntry.vendorMod.fileList")},"x-query-engine-skip":!0,properties:{toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},"x-visible":expression("!$form.readPretty"),properties:{add:{type:"void",title:i18nExpression("common.new"),"x-component-props":{type:"primary","@click":expression(`() => {
                                    $self.query('.fileList').take(field => {
                                      field.componentProps.componentInstance.addRow('unshift', {
                                        fileId: null,
                                        fileName: null
                                      }) 
                                    })
                                  }`)}}}},fileList:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,maxHeight:400,pagination:!1,sortable:!1,primaryKey:"orgCatFormFileId",cascadeDeletion:!0},properties:generateXindexInOrder({fileId:{type:"string",title:i18nExpression("cusEntry.vendorMod.fileName"),"x-component":"SrmCommonFile","x-component-props":{"default-file":{fileId:"{{$table.getRowByIndex($self.index)?.fileId}}",fileName:"{{$table.getRowByIndex($self.index)?.fileName}}"},"extra-data":{fileModular:"sup",fileFunction:"SUPPLIER_RESTRICTION",fileType:"images"},"@on-change":expression(`({file}) => {
                                    const { fileId, fileName } = file || {}
                                    const row = $table.getRowByIndex($self.index)
                                    row.fileId = fileId?.toString() || null
                                    row.fileName = fileName || null
                                  }`)},"x-render-table-column":{minWidth:120}},remark:{type:"string",title:i18nExpression("cusEntry.vendorMod.remark"),"x-render-table-column":{minWidth:120}},operation:{type:"void",title:i18nExpression("common.operation"),"x-render-table-column":{width:60,fixed:"right"},"x-visible":expression("!$form.readPretty"),"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:i18nExpression("common.delete"),"x-component-props":{type:"text","@click":expression(`({ row }) => {
                                        $table.remove($self.index)
                                      }`)}}}}})}}}})}}}}}}}}}}),$back=$bus=>{emitTabRemove(attrs.tabName),$bus.$emit("siteA")},$submits=async(type,$form,$queryEngine,$message,$t,$bus)=>{if(type=="WITHDRAW"){emitTabRemove(attrs.tabName),$bus.$emit("siteA");return}$form.validate().then(e=>{const submitData=JSON.parse(JSON.stringify($form.values));let rangeData=submitData.rangeData,listDataAll=JSON.parse(JSON.stringify($form.query("state").get("data").listDataAll));const rangeDatas=$form.query(".rangeData").take().componentProps.componentInstance.$refs.range;if(["CATEGORY_EXIT","CATEGORY_FORZEN","CATEGORY_THAW","CATEGORY_LIMIT_FLAG","CATEGORY_LIMIT_FLAG_REMOVE"].includes(submitData.supplierControlType)){let bol2=0,optSelectRange=rangeDatas.rangeList.length>0?rangeDatas.rangeList:rangeDatas.getRangeList();if(rangeData.forEach(datas=>{let categoryId=datas.categoryId;optSelectRange.findIndex(i=>i.categoryId==categoryId)>-1?(datas.selected="Y",bol2+=1):datas.selected="N"}),bol2==0)return app.$message.warning(app.$t("dataConfMod.msgInputCate")),!1;if(["CATEGORY_EXIT","CATEGORY_FORZEN","CATEGORY_THAW","CATEGORY_LIMIT_FLAG","CATEGORY_LIMIT_FLAG_REMOVE"].includes(submitData.supplierControlType)){let bol1=0,selectedCategoryList=rangeDatas.categoryList.length>0?rangeDatas.categoryList:rangeDatas.getCategoryList();if(listDataAll.forEach(datas=>{let rowKey=datas.orgId+"_"+datas.categoryId;selectedCategoryList.findIndex(i=>i.orgId+"_"+i.categoryId==rowKey)>-1?(datas.selected="Y",bol1+=1):datas.selected="N"}),bol1==0)return app.$message.warning(app.$t("dataConfMod.msgInputUnit2")),!1}}else if(["ORGANIZATION_EXIT","ORGANIZATION_FORZEN","ORGANIZATION_THAW","POSITION_LIMIT_FLAG","POSITION_LIMIT_FLAG_REMOVE"].includes(submitData.supplierControlType)){let bol1=0,optSelectRange=rangeDatas.rangeList.length>0?rangeDatas.rangeList:rangeDatas.getRangeList();if(rangeData.forEach(datas=>{let orgId=datas.orgId;optSelectRange.findIndex(i=>i.orgId==orgId)>-1?(datas.selected="Y",bol1+=1):datas.selected="N"}),bol1==0)return app.$message.warning(app.$t("dataConfMod.msgInputUnit2")),!1;let rangeObj={};rangeData.forEach(rangeItem=>{rangeItem.selected=="Y"&&(rangeObj[rangeItem.orgId]=rangeItem)}),listDataAll.forEach(datas=>{rangeObj.hasOwnProperty(datas.orgId)?datas.selected="Y":datas.selected="N"})}else rangeData=[],listDataAll.forEach(datas=>{datas.selected="Y"});const rangeDeleteList=$form.query("state").get("data").rangeDeleteList,rangeDetailDeleteList=$form.query("state").get("data").rangeDetailDeleteList;return submitData.rangeList=[...rangeData,...rangeDeleteList],submitData.detailList=[...listDataAll,...rangeDetailDeleteList],delete submitData.rangeData,(async()=>{[null,void 0,"DRAFT"].includes(submitData.approveStatus)&&(submitData.approveStatus="DRAFT"),type=="SAVE"?$queryEngine.request.save(submitData,{query:{"*":{},OrgCatFormCategoryRange:{"*":{}},OrgCatFormCategoryDetail:{"*":{}}},loading:!0}).then(res=>{$message.success($t("common.successSave")),$bus.$emit("cooperationEnd"),emitTabRemove(attrs.tabName)}).catch(err=>{}):$queryEngine.request.save(submitData,{query:{"*":{},OrgCatFormCategoryRange:{"*":{}},OrgCatFormCategoryDetail:{"*":{}}},tree:!0,loading:!0}).then(res=>{$form.values.orgCatFormId=res.data[0].orgCatFormId;const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.setWorkflowBusinessId(res.data[0]?.orgCatFormId||null),componentInstance.setWorkflowTabDisabled(!0),componentInstance.setWorkflowBusinessVariables({procTitle:$form.values.orgCatFormNumber}),componentInstance.handlerAfter(type.toUpperCase(),()=>{$bus.$emit("cooperationEnd"),emitTabRemove(attrs.tabName)})})})()})};return{__sfc:!0,app,emitTabRemove,t,vendor,attrs,workflowStatus,viewUpdateButton,disabledUpdateButton,updateWorkflowconfig,handleButtonConfig,initButtonConfig,updateButtonConfig,newRange,schema,$back,$submits,scope:{app,t,$attrs:attrs,emitTabRemove,initButtonConfig,updateButtonConfig,$back,$submits,blackComApi,orgCatForm},components:{newRange},RenderEngine}}});var _sfc_render$2=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{schemaKey:"siteAssessmentDetail",pageAttrs:_vm.$attrs,schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns$2=[],__component__$2=normalizeComponent(_sfc_main$2,_sfc_render$2,_sfc_staticRenderFns$2,!1,null,null,null,null);const endDetail=__component__$2.exports,_sfc_main$1=defineComponent({__name:"cooperationEndedList",setup(__props){const{emitTabAdd,app}=usePageHelper(),schema=defineSchemas({OrgCatForm:{type:"void","x-decorator":"QueryEngine","x-query-engine":{service:"sup",actions:{paginationQuery:{immediate:!0}}},"x-component":"el-container","x-component-props":{class:"flex-container the_contractTemplateList_wrapper",direction:"vertical"},properties:{bus:{type:"void","x-component":"BusEvent","x-component-props":{eventName:"cooperationEnd","@listener":expression(`() => {
            $queryEngine.state.paginationManagement.refresh()
          }`)}},query:{type:"object","x-query-engine-skip":!0,"x-component":"QueryFormByQueryEngine",properties:generateXindexInOrder({orgCatFormNumber:{type:"string",title:i18nExpression("vendorMod.controlNumber"),"x-query-engine-query-operator":"contains"},vendorId:{type:"string",title:i18nExpression("common.vendorName"),"x-component":"QuickSearchWrapper","x-component-props":{showKey:"companyName",propKey:"companyId",name:"scc_sup_company_info_all"}},approveStatus:{type:"string",title:i18nExpression("common.status"),"x-component":"DictSelect","x-component-props":{code:"APPROVE_STATUS_TYPE"}},supplierControlType:{type:"string",title:i18nExpression("vendorMod.controlType"),"x-component":"DictSelect","x-component-props":{code:"SUPPLIER_CONTROL_TYPE2"}},cooperationEndedReasonType:{type:"string",title:i18nExpression("cusEntry.vendorMod.cooperationEndedReasonType"),"x-component":"DictSelect","x-component-props":{code:"SUPPLIER_LIMIT_TYPE"}}})},toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void",title:i18nExpression("common.add"),"x-component-props":{type:"primary",...buttonListItemVisibleByPermission("sup:cooperationEndList:add"),"@click":expression(`() => {
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
                    tabName: 'CooperationEndedDetail' + row.vendorName,
                    row,
                  },
                  title: row.vendorName,
                  name: 'CooperationEndedDetail' + row.vendorName
                }
                emitTabAdd(tab)
              }`)},"x-render-table-column":{title:i18nExpression("vendorMod.controlNumber"),minWidth:180,customRender:!0}},supplierControlType:{type:"string",title:i18nExpression("vendorMod.controlType"),"x-component":"DictSelect","x-component-props":{code:"SUPPLIER_CONTROL_TYPE2"},"x-render-table-column":{width:120}},createdUserName:{type:"string",title:i18nExpression("common.creator"),"x-render-table-column":{width:200}},createdBy:{type:"string","x-hidden":!0},creationDate:{type:"string",title:i18nExpression("common.creationTime"),"x-render-table-column":{width:120}},startDate:{type:"string",title:i18nExpression("common.effectTime"),"x-render-table-column":{width:120}},lastUpdateDate:{type:"string","x-hidden":!0,"x-query-engine-sort":"desc","x-render-table-column":{width:120}},operation:{type:"void",title:i18nExpression("common.operation"),"x-render-table-column":{width:150,fixed:"right"},properties:{edit:{type:"void",title:i18nExpression("common.edit"),"x-component":"TableButton","x-reactions":changeFieldVisibleByDeps([".approveStatus"],"['DRAFT', 'WITHDRAW', 'REJECTED'].includes($deps[0])"),"x-component-props":{type:"text","@click":expression(`({ row }) => {
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
                  }`)}},delete:{type:"void",title:i18nExpression("common.delete"),"x-component":"TableButton","x-reactions":changeFieldVisibleByDeps([".approveStatus"],"['DRAFT', 'WITHDRAW', 'REJECTED'].includes($deps[0])"),"x-component-props":{style:"margin-left: 8px",showPopconfirm:!0,"@confirm":expression(`({ row }) => {
                    $queryEngine.request.delete(row.orgCatFormId).then(() => {
                      $message.success($t('common.successDelete'))
                      $queryEngine.state.paginationManagement.refresh()
                    })
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
                  }`)}}}}})}}}});return{__sfc:!0,emitTabAdd,app,schema,scope:{emitTabAdd,app,i18nExpression,endDetail},components:{},RenderEngine}}});var _sfc_render$1=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{staticClass:"contractPaymentType",attrs:{schemaKey:"cooperationList",schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns$1=[],__component__$1=normalizeComponent(_sfc_main$1,_sfc_render$1,_sfc_staticRenderFns$1,!1,null,null,null,null);const cooperationEndedList=__component__$1.exports,_sfc_main={name:"CooperationEnded",components:{NavTabs},data(){return{activeTab:"cooperationEndedList",tabs:[{title:()=>this.$t("cusEntry.vendorMod.cooperationEnded"),name:"cooperationEndedList",component:cooperationEndedList,closable:!1}]}}};var _sfc_render=function(){var _vm=this,_c=_vm._self._c;return _c("NavTabs",{ref:"tabs",attrs:{"tabs-list":_vm.tabs,"cur-tab":_vm.activeTab}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const index=__component__.exports;export{index as default};
