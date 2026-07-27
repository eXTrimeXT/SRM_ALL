import{N as NavTabs}from"./index-9a7f2446.js";import{n as normalizeComponent,T as QuickSearch,G as CPagination,ae as i18nExpression,ad as expression,ah as generateXindexInOrder,af as yearMonthDaySelectorSegment,ak as defineComponent,al as usePageHelper,am as useAttrs,bB as useDebounceFn,an as ref$1,bY as computed,aq as defineSchemas,aj as feedbackLayoutIsPopover,ar as RenderEngine,a5 as downloadFileLink,bu as downloadFileLinkByPost,ab as parseTime,az as elementUi_commonExports,a8 as Import,bV as orderConfig,cl as onActivated}from"./index-6b6051d8.js";import{_ as _pick}from"./pick-b4f398be.js";import{s as setWarningTip}from"./util-d962b17f.js";import{B as BomVersionSearch}from"./bomVersionSearch-b974e627.js";const _sfc_main$4={name:"BatchMaintainDialog",components:{QuickSearch,CPagination},props:{visible:{type:Boolean,default:!1},requirementHead:{type:Object,default:()=>{}},queryForm:{type:Object,default:()=>({categoryCode:null,categoryId:null,categoryName:null,materialCode:null,materialId:null,materialName:null,orgId:null,organizationId:null,organizationName:null})},pageInfo:{type:Object,default:()=>({pageTotal:0,pageNum:1,pageSize:15})},displayItemTable:{type:Array,default:()=>[]}},data(){return{visibleDialog:!1,materialParam:{},multipleSelection:[],viewIndex:1,viewSize:15,form:{categoryCode:null,categoryId:null,categoryName:null,materialCode:null}}},watch:{visible(sign){this.visibleDialog=sign}},methods:{beforeOpenMaterial(){this.materialParam["t.STRUCT"]=this.requirementHead.categoryId},getCategoryNameObj(val,scope){scope.categoryId=val?val.categoryId:"",scope.categoryCode=val?val.categoryCode:"",scope.categoryName=val?val.categoryName:""},queryContent(){this.$emit("queryContent",this.form)},addOneContent(){this.$emit("addOneContent",this.multipleSelection)},handleItemSelection(val){this.multipleSelection=val},handleItemDBClick(val){this.multipleSelection=[val],this.addOneContent()},changeCurrentIndex(currentNum){this.$emit("changeCurrentIndex",currentNum)},changeCurrentSize(currentSize){this.$emit("changeCurrentSize",currentSize)}}};var _sfc_render$4=function(){var _vm=this,_c=_vm._self._c;return _c("srm-dialog",_vm._g(_vm._b({attrs:{visible:_vm.visibleDialog,title:_vm.$t("purchaseDemand.materialDetailSelect"),size:"large","destroy-on-close":"","close-on-click-modal":!1},on:{"update:visible":function($event){_vm.visibleDialog=$event}}},"srm-dialog",_vm.$attrs,!1),_vm.$listeners),[_vm.visibleDialog?_c("el-form",{ref:"queryForm",attrs:{"label-width":"60px","label-position":"left",inline:!0,model:_vm.form}},[_c("srm-row",[_c("srm-col",{attrs:{initCol:3}},[_c("el-form-item",{attrs:{label:_vm.$t("purchaseDemand.itemCode")}},[_c("el-input",{model:{value:_vm.form.materialCode,callback:function($$v){_vm.$set(_vm.form,"materialCode",$$v)},expression:"form.materialCode"}})],1)],1),_c("srm-col",{attrs:{initCol:3}},[_c("el-form-item",{attrs:{label:_vm.$t("purchaseDemand.materialCateSub")}},[_c("QuickSearch",{attrs:{"pre-query-data":_vm.materialParam,"show-input":_vm.form.categoryName,"show-key":"categoryName","scope-data":_vm.form,name:"scc_base_purchase_category4"},on:{"before-open":_vm.beforeOpenMaterial,"close-quicksearch":_vm.getCategoryNameObj}})],1)],1),_c("srm-col",{attrs:{initCol:3}},[_c("div",{staticStyle:{"text-align":"right"}},[_c("el-button",{attrs:{type:"primary"},on:{click:_vm.queryContent}},[_vm._v(" "+_vm._s(_vm.$t("common.search"))+" ")]),_c("el-button",{attrs:{type:"primary"},on:{click:_vm.addOneContent}},[_vm._v(" "+_vm._s(_vm.$t("common.confirm"))+" ")])],1)])],1)],1):_vm._e(),_c("el-table",{staticStyle:{width:"100%"},attrs:{data:_vm.displayItemTable,border:"",height:"345px","highlight-current-row":""},on:{"row-dblclick":_vm.handleItemDBClick,"selection-change":_vm.handleItemSelection}},[_c("el-table-column",{attrs:{type:"selection",width:"55"}}),_c("el-table-column",{attrs:{align:"center",type:"index",label:_vm.$t("contractMod.tabindex"),width:"60"}}),_c("el-table-column",{attrs:{align:"center",prop:"materialCode",label:_vm.$t("purchaseDemand.itemCode"),width:"120","show-overflow-tooltip":!0}}),_c("el-table-column",{attrs:{align:"center",prop:"materialName",label:_vm.$t("purchaseDemand.itemName"),"min-width":"150","show-overflow-tooltip":!0}}),_c("el-table-column",{attrs:{align:"center",prop:"categoryName",label:_vm.$t("purchaseDemand.materialCateSub"),width:"120","show-overflow-tooltip":!0}}),_c("el-table-column",{attrs:{align:"center",prop:"unitName",label:_vm.$t("purchaseDemand.unitCode"),width:"120"}}),_c("el-table-column",{attrs:{align:"center",prop:"categoryFullName",label:_vm.$t("purchaseDemand.categoryFullName"),"min-width":"150","show-overflow-tooltip":!0}})],1),_c("srm-row",[_c("srm-col",{attrs:{initCol:1}},[_c("CPagination",{ref:"queryPagination",staticClass:"c-query-table-pagination",staticStyle:{margin:"5px"},attrs:{total:_vm.pageInfo.pageTotal,"page-num":_vm.pageInfo.pageNum,"page-size":_vm.pageInfo.pageSize},on:{"current-change":_vm.changeCurrentIndex,"size-change":_vm.changeCurrentSize}})],1)],1)],1)},_sfc_staticRenderFns$4=[],__component__$4=normalizeComponent(_sfc_main$4,_sfc_render$4,_sfc_staticRenderFns$4,!1,null,null,null,null);const MaterialSelectDialog=__component__$4.exports,_sfc_main$3={name:"BatchMaintainDialog",components:{},props:{visible:{type:Boolean,default:!1},requirementHead:{type:Object,default:()=>{}}},data(){return{visibleDialog:!1,batchMaintainForm:{requirementQuantity:null,requirementDate:null,dmandLineRequest:null,receiveAddress:null},pickerOptions:{disabledDate(time){const today=new Date;return today.setHours(0),today.setMinutes(0),today.setSeconds(0),today.setMilliseconds(0),time.getTime()<today.getTime()}}}},watch:{visible(sign){this.visibleDialog=sign,sign&&(this.batchMaintainForm={})}},methods:{batchMaintainSubmit(){this.$emit("submit",this.batchMaintainForm)},changeSiteInfo(row,{element}){this.$set(row,"receiveContact",element?element.receiver:""),this.$set(row,"receiveTelephone",element?element.receiverPhone:""),this.$set(row,"receiveAddress",element?element.siteName:"")}}};var _sfc_render$3=function(){var _vm=this,_c=_vm._self._c;return _c("srm-dialog",_vm._g(_vm._b({attrs:{visible:_vm.visibleDialog,title:_vm.$t("vendorMod.batchMaintain"),size:"middle","destroy-on-close":"","close-on-click-modal":!1},on:{"update:visible":function($event){_vm.visibleDialog=$event}}},"srm-dialog",_vm.$attrs,!1),_vm.$listeners),[_c("el-form",{ref:"batchMaintainRef",attrs:{model:_vm.batchMaintainForm,"label-width":"80px","label-position":"top"}},[_c("el-form-item",{attrs:{label:_vm.$t("purchaseDemand.requirementQuantity"),prop:"requirementQuantity"}},[_c("el-input",{attrs:{type:"Number"},model:{value:_vm.batchMaintainForm.requirementQuantity,callback:function($$v){_vm.$set(_vm.batchMaintainForm,"requirementQuantity",$$v)},expression:"batchMaintainForm.requirementQuantity"}})],1),_c("el-form-item",{attrs:{label:_vm.$t("purchaseDemand.requirementDate"),prop:"requirementDate"}},[_c("el-date-picker",{attrs:{type:"date",format:_vm.$formatDatePicker,"picker-options":_vm.pickerOptions,"value-format":"yyyy-MM-dd"},model:{value:_vm.batchMaintainForm.requirementDate,callback:function($$v){_vm.$set(_vm.batchMaintainForm,"requirementDate",$$v)},expression:"batchMaintainForm.requirementDate"}})],1),_c("el-form-item",{attrs:{label:_vm.$t("purchaseDemand.dmandLineRequest"),prop:"dmandLineRequest"}},[_c("dict-select",{attrs:{code:"DMAND_LINE_REQUEST"},model:{value:_vm.batchMaintainForm.dmandLineRequest,callback:function($$v){_vm.$set(_vm.batchMaintainForm,"dmandLineRequest",$$v)},expression:"batchMaintainForm.dmandLineRequest"}})],1),_c("el-form-item",{attrs:{label:_vm.$t("purchaseDemand.ceeaDeliveryPlaceOut"),prop:"receiveAddress"}},[_c("DictSelect",{attrs:{code:_vm.requirementHead.organizationId,"custom-select-type":"RECEIVE_ADDRESS"},on:{"change-value":(val,element)=>_vm.changeSiteInfo(_vm.batchMaintainForm,element)},model:{value:_vm.batchMaintainForm.receiveAddress,callback:function($$v){_vm.$set(_vm.batchMaintainForm,"receiveAddress",$$v)},expression:"batchMaintainForm.receiveAddress"}})],1)],1),_c("div",{staticClass:"topComment",attrs:{slot:"footer"},slot:"footer"},[_c("el-button",{on:{click:function($event){_vm.visibleDialog=!1}}},[_vm._v(" "+_vm._s(_vm.$t("common.cancel"))+" ")]),_c("el-button",{attrs:{type:"primary"},on:{click:_vm.batchMaintainSubmit}},[_vm._v(" "+_vm._s(_vm.$t("common.confirm"))+" ")])],1)],1)},_sfc_staticRenderFns$3=[],__component__$3=normalizeComponent(_sfc_main$3,_sfc_render$3,_sfc_staticRenderFns$3,!1,null,null,null,null);const BatchMaintainDialog=__component__$3.exports,viewVersion=(row,app,$form)=>{$form.query("*.bomVersionDialog").take().setComponentProps({visible:!1}),app.$router.push({name:"outsourcingBomNew",params:{from:"order",bomHeadId:row.bomHeadId}})},BomVersionDialog={type:"void","x-decorator":"QueryEngine",title:i18nExpression("purchaseApplication.bomVersionTitle"),"x-component":"RDialog","x-component-props":{size:"large","close-on-click-modal":!1,beforeClose:`{{(done, type, closeLoading) => { 
        if ( type === 'ok') { 
          const row = $form.query('*.bomVersionDialog.*.bomVersionList').take()
          .componentProps
          .componentInstance
          .getRadioRecord()
          console.log(row,112)
          if (!row) {
            $message.warning($t('common.msgSelectData'))
            closeLoading()
          } else {
            $selBomVersion($form,done)
          }
          
        } else {
            done()
        }
    }}}`},properties:{layout:{type:"void",properties:{bomVersionList:{type:"array","x-query-engine-skip":!0,"x-component":"RenderTable","x-component-props":{height:"450px",editMode:!0,preColumns:"radio,seq",pagination:!1,sortable:!1,"radio-config":{trigger:"row"},"@cell-dblclick":expression(`() => {
                  $selBomVersion($form)
            }`)},"x-read-pretty":!0,properties:generateXindexInOrder({materialCode:{type:"string",title:i18nExpression("purchaseDemand.itemCode"),"x-render-table-column":{minWidth:120}},materialName:{type:"string",title:i18nExpression("purchaseDemand.itemName"),"x-render-table-column":{minWidth:120}},versionCode:{type:"string","x-component":"TableButton","x-component-props":{type:"text",disabled:!1,"@click":expression("({ row }) => $viewVersion(row, app, $form)")},"x-render-table-column":{minWidth:150,title:i18nExpression("dataConfMod.version"),customRender:!0}},bomDetailDescription:{type:"string",title:i18nExpression("purchaseApplication.bomDetailDescription"),"x-render-table-column":{minWidth:120}},createdFullName:{type:"string",title:i18nExpression("purchaseDemand.createdBy1"),"x-render-table-column":{minWidth:120}},creationDate:{...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                  parseTime(row.creationDate, '{y}-{m}-{d}')
                }`)},"x-render-table-column":{title:i18nExpression("orderMod.buyerOrderSynergy.creationDate"),minWidth:120}}})}}},pagination:{type:"void","x-component":"CPagination","x-component-props":{pageNum:expression("$form.query('PrRequirementForBuyer').get('data').bomVersionListPageNum"),pageSize:expression("$form.query('PrRequirementForBuyer').get('data').bomVersionListPageSize"),total:expression("$form.query('PrRequirementForBuyer').get('data').bomVersionListTotal"),pageSizes:[5,15,30,60,120,300,600,1e3,1500],"@current-change":expression(`(num) => {
                $form.query('PrRequirementForBuyer').get('data').bomVersionListPageNum = num
                $getBomVersionList($form)
              }`),"@size-change":expression(`(size) => {
                $form.query('PrRequirementForBuyer').get('data').bomVersionListPageSize = size
                $getBomVersionList($form)
              }`)}}}},BomDetailDialog={type:"void","x-decorator":"QueryEngine",title:i18nExpression("purchaseApplication.bomDetail"),"x-component":"RDialog","x-component-props":{size:"large","close-on-click-modal":!1,footerButtonList:expression(`(_, { cancelButton,okButton }) => {
      return [
        {...cancelButton, text: $t('bidMod.backTo')}
      ]
    }`)},properties:{layout:{type:"void",properties:{bomDetailList:{type:"array","x-query-engine-skip":!0,"x-component":"RenderTable","x-component-props":{height:"450px",editMode:!1,preColumns:"seq",pagination:!1,sortable:!1},properties:generateXindexInOrder({materialCode:{type:"string",title:i18nExpression("outsourcingBomNew.materialCode"),"x-render-table-column":{minWidth:120}},materialName:{type:"string",title:i18nExpression("outsourcingBomNew.materialName"),"x-render-table-column":{minWidth:120}},unitName:{type:"string",title:i18nExpression("purchaseApplication.unitName"),"x-render-table-column":{minWidth:120}},baseMaterialNum:{type:"string",title:i18nExpression("outsourcingBomNew.num"),"x-render-table-column":{minWidth:120}},componentQuantity:{type:"string",title:i18nExpression("purchaseApplication.componentQuantity"),"x-render-table-column":{minWidth:120}}})}}},pagination:{type:"void","x-component":"CPagination","x-component-props":{pageNum:expression("$form.query('PrRequirementForBuyer').get('data').bomDetailListPageNum"),pageSize:expression("$form.query('PrRequirementForBuyer').get('data').bomDetailListPageSize"),total:expression("$form.query('PrRequirementForBuyer').get('data').bomDetailListTotal"),pageSizes:[5,15,30,60,120,300,600,1e3,1500],"@current-change":expression(`(num) => {
                $form.query('PrRequirementForBuyer').get('data').bomDetailListPageNum = num
                $getBomDetailListList($form)
              }`),"@size-change":expression(`(size) => {
                $form.query('PrRequirementForBuyer').get('data').bomDetailListPageSize = size
                $getBomDetailListList($form)
              }`)}}}},_sfc_main$2=defineComponent({__name:"edit-engine",setup(__props){const{emitTabRemove,t,app,http,buyer,getCurrentUserInfo,confirmMessage}=usePageHelper(),attrs=useAttrs(),isReadOnly=(()=>!["add","edit"].includes(attrs.params.flag))(),$closeTab=$bus=>{$bus.$emit("PrRequirementForBuyer"),emitTabRemove(attrs.tabName)},getTotalAmount=(n,$form)=>{$form.query("PrRequirementForBuyer").get("data").copyInit.bol&&($form.query("PrRequirementForBuyer").get("data").copyInit.num=+n,$form.query("PrRequirementForBuyer").get("data").copyInit.bol=!1)},setTotalAmount=useDebounceFn(async($form,row)=>{if(row.applyStatus==="RETURNING"&&row.requirementQuantity>=$form.query("PrRequirementForBuyer").get("data").copyInit.num)return row.requirementQuantity=$form.query("PrRequirementForBuyer").get("data").copyInit.num,app.$message.warning(t("purchaseDemand.mustBeLessEqual"));if(row.requirementQuantity&&row.requirementQuantity<=0)return app.$message.warning(t("purchaseDemand.setTotalAmountTips1"));row.totalAmount=Number(Number(row.notaxPrice||0)*Number(row.requirementQuantity||0)).toFixed(2),setTimeout(()=>{const totalBudget=$form.values.reqLineList.map(v=>v.totalAmount||0).reduce((p,c)=>(Number(p)||0)+(Number(c)||0));$form.values.totalBudget=totalBudget},100)},300),workflowStatus=ref$1("DRAFT"),viewUpdateButton=computed(()=>buyer()&&!isReadOnly&&workflowStatus.value!=="APPROVED"),disabledUpdateButton=computed(()=>["SUBMITTED","APPROVING"].includes(workflowStatus.value)),getButtonConfig=$form=>{setTimeout(()=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.setWorkflowBusinessId($form.values.requirementHeadId||""),componentInstance.setWorkflowTabDisabled(attrs.params.flag!=="approvalOnly"),componentInstance.buttonConfigInfo.save.view=viewUpdateButton.value,componentInstance.buttonConfigInfo.submit.view=viewUpdateButton.value,componentInstance.buttonConfigInfo.save.disabled=disabledUpdateButton.value,componentInstance.buttonConfigInfo.submit.disabled=disabledUpdateButton.value,componentInstance.buttonConfigInfo.cancel.view=!isReadOnly,componentInstance.buttonConfigInfo.close.view=isReadOnly})},checkMaterialList=(categoryId,$form)=>{const lastCategoryName=$form.query("PrRequirementForBuyer").get("data").lastCategoryName;return new Promise(resolve=>{$form.values.reqLineList.length&&lastCategoryName.categoryId!==categoryId?confirmMessage(t("purchaseDemand.checkMaterialListConfirm")).then(()=>{$form.values.reqLineList=[],$form.query(".reqLineList").take(),app.$message.info(t("purchaseDemand.checkMaterialListTips1")),resolve(!0)}).catch(()=>{app.$message.info(t("purchaseDemand.checkMaterialListTips2")),resolve(!1)}):resolve(!0)})},getCategoryObj=async(val,$form,instance)=>{let lastCategoryName=$form.query("PrRequirementForBuyer").get("data").lastCategoryName;await checkMaterialList(val?val.categoryId:"",$form)?($form.values.categoryId=val?val.categoryId:"",$form.values.categoryCode=val?val.categoryCode:"",$form.values.categoryName=val?val.categoryName:"",lastCategoryName.categoryId=val?val.categoryId:"",lastCategoryName.categoryCode=val?val.categoryCode:"",lastCategoryName.categoryName=val?val.categoryName:""):($form.values.categoryId=lastCategoryName.categoryId,$form.values.categoryCode=lastCategoryName.categoryCode,$form.values.categoryName=lastCategoryName.categoryName,instance.$refs.quickSearchRef.setInputModel(lastCategoryName.categoryName))},resetForm=form=>{for(let i in form)form[i]=""},queryContent=(queryForm,$form)=>{let pageInfo=$form.query("PrRequirementForBuyer").get("data").pageInfo;http({url:"/api-base/material/materialItem/listMaterialByPurchaseCategoryNew",method:"POST",data:{categoryId:queryForm.categoryId||$form.values.categoryId,materialCode:queryForm.materialCode,materialName:queryForm.materialName,organizationId:$form.values.organizationId,organizationName:$form.values.organizationName,ceeaPurchaseType:$form.values.ceeaPurchaseType,pageSize:pageInfo.pageSize,pageNum:pageInfo.pageNum},loading:!0}).then(res=>{res&&res.data&&(Object.assign(pageInfo,{pageTotal:res.data.total,pageNum:pageInfo.pageNum,pageSize:res.data.pageSize}),$form.query(".MaterialSelectDialog").take().setComponentProps({displayItemTable:res.data.list}))})},openDialog=$form=>{if(!$form.values.orgId||!$form.values.organizationId)return app.$message.warning(t("purchaseDemand.openDialogWarning1"));if(!$form.values.categoryName)return app.$message.warning(t("purchaseDemand.openDialogWarning2"));resetForm($form.query("PrRequirementForBuyer").get("data").queryForm),$form.query("PrRequirementForBuyer").get("data").queryForm.organizationName=$form.values.organizationName,$form.query("PrRequirementForBuyer").get("data").queryForm.inputLevel=$form.values.categoryName,queryContent($form.query("PrRequirementForBuyer").get("data").queryForm,$form),$form.query(".MaterialSelectDialog").take().setComponentProps({visible:!0})},addOneContent=(multipleSelection2,$form)=>{if(multipleSelection2.length===0)return;const obj={},newArr=multipleSelection2.map(item=>({...item,unit:item.unitName,unitCode:item.unit,totalAmount:(item.notaxPrice||0)*(item.requirementQuantity||0),ceeaIe:item.ceeaIfDirectory==="Y"?"true":"false",businessSmall:$form.values.businessSmall,requirementQuantity:null,orgId:$form.values.orgId,orgCode:$form.values.orgCode,orgName:$form.values.orgName,organizationId:$form.values.organizationId,organizationCode:$form.values.organizationCode,organizationName:$form.values.organizationName})).map(row=>{const o=JSON.parse(JSON.stringify(obj));return Object.keys(row).forEach(key=>{o[key]=row[key]}),o});let reqLineList=$form.values.reqLineList;$form.values.reqLineList=[...newArr,...reqLineList],$form.query(".MaterialSelectDialog").take().setComponentProps({visible:!1})},beforeUpload=$form=>{let otherParams=_pick($form.values,["categoryCode","categoryId","categoryName","ceeaAssetType","ceeaProjectUserNickname","ceeaDepartmentName","ceeaPurchaseType","demandType","orgId","orgName","orgCode","organizationCode","organizationId","organizationName","requirementHeadId","requirementHeadNum"]);$form.query("PrRequirementForBuyer").get("data").extraData={...$form.query("PrRequirementForBuyer").get("data").extraData,...otherParams}},downloadTemplate=()=>{downloadFileLink("/api-sup-ce/pr/requirementLine/v2/downloadTemplate",t("purchaseDemand.importMaterialItemModelDownload")).catch(()=>{app.$message.error(t("purchaseDemand.downloadFail"))})},handleSuccess=(res,$form)=>{if(res.status==="Y"){let resData=res.data,reqLineList=$form.values.reqLineList;resData.forEach(row=>{row.totalAmount=Number(Number(row.notaxPrice||0)*Number(row.requirementQuantity||0)).toFixed(2)}),$form.values.reqLineList=[...resData,...reqLineList],$form.values.demandType=="NONPRODUCTIVE_DEMAND"&&setTimeout(()=>{const totalBudget=$form.values.reqLineList.map(v=>v.totalAmount||0).reduce((p,c)=>(Number(p)||0)+(Number(c)||0));$form.values.totalBudget=totalBudget},100)}},exportFile=async()=>{downloadFileLinkByPost(`/api-sup-ce/pr/requirementLine/excelExport?requirementHeadId=${attrs.params.row.requirementHeadId}`,`${t("purchaseApplication.materialDetail")}${parseTime(new Date)}.xlsx`).catch(err=>{app.$message.error(err.message)})},reSubmit=($form,$queryEngine)=>{let rows=$form.query("reqLineList").take().componentProps.componentInstance.getCheckboxRecords();if(rows.length===0){app.$message({type:"warning",message:t("purchaseDemand.pleaseSelectDetailRow")});return}for(const item of rows)if(item.applyStatus!=="RETURNING"){app.$message({type:"warning",message:t("purchaseDemand.reSubmitTips1")});return}const params={requirementHead:$form.values,requirementLineList:rows};http({url:"/api-sup-ce/pr/requirementLine/resubmit",method:"POST",data:params,loading:!0}).then(()=>{app.$message.success(t("common.success")),getFormDetail(attrs.params.row.requirementHeadId,$form,$queryEngine)}).catch(err=>{})},submitCheck=async $form=>$form.values.reqLineList.length===0?(app.$message.warning(t("purchaseDemand.saveBillTips5")),!1):!($form.values.ceeaPurchaseType==="APPOINT"&&await confirmMessage(t("purchaseDemand.saveBillConfirm1"))!=="confirm"),getFormDetail=async(requirementHeadId,$form,$queryEngine)=>{$queryEngine.request.read(requirementHeadId).then(res=>{$form.setValues(res.data[0])})},submitEvent=async($form,$queryEngine,$bus)=>{if(!await submitCheck($form))return;let{data}=await $queryEngine.request.baseRequest({action:"submitRequirement",payload:[{...$form.values,sourceFromType:"HAND_MAKE"}],loading:!0});$form.setValues(data[0]),app.$message.success(t("common.success")),$closeTab($bus)},saveBill=($form,$queryEngine)=>{$queryEngine.request.baseRequest({action:"tempSaveRequirement",payload:[{...$form.values,sourceFromType:"HAND_MAKE"}],loading:!0}).then(res=>{app.$message.success(t("common.success"));let requirementHeadId=res.data[0].requirementHeadId;getFormDetail(requirementHeadId,$form,$queryEngine)})},saveOrSubmitBill=async(type,$form,$queryEngine,$bus)=>{$form.validate().then(res=>{if($form.values.ceeaPrType==="01"&&$form.values.reqAttachList.length===0){app.$message.error(t("purchaseDemand.saveBillTips3"));return}$form.values.reqLineList.map(v=>{v.orgId=$form.values.orgId,v.orgCode=$form.values.orgCode,v.orgName=$form.values.orgName}),type==="SUBMIT"?submitEvent($form,$queryEngine,$bus):saveBill($form,$queryEngine)}).catch(err=>{err.forEach(item=>{item.path.includes("vendorName")&&(item.messages[0]=t("purchaseDemand.selectVendor")),item.path.includes("notaxPrice")&&(item.messages[0]=t("purchaseDemand.prompt2"))}),setWarningTip(err)})},importSlot=$form=>({functional:!0,render(h){return h(elementUi_commonExports.Tooltip,{props:{placement:"top",content:t("purchaseDemand.itemInfoTooltip"),disabled:isReadOnly||!!$form.values.requirementHeadNum}},[h(Import,{attrs:{type:"primary",title:t("common.excelImport"),upLoadUrl:"/api-sup-ce/pr/requirementLine/v2/import",disabled:isReadOnly||$form.values.auditStatus==="APPROVED"||!$form.values.categoryId||!$form.values.organizationId||!$form.values.orgId,extraData:$form.query("PrRequirementForBuyer").get("data").extraData},on:{beforeUpload:()=>beforeUpload($form),downloadTemplate:()=>downloadTemplate(),handleSuccess:res=>handleSuccess(res,$form)}})])}}),$openBomVersionDialog=($form,$queryEngine,row)=>{$form.query("*.bomVersionDialog").take().setComponentProps({visible:!0}),$getBomVersionList($form,$queryEngine,row)},$getBomVersionList=($form,$queryEngine,row)=>{$queryEngine.request.baseRequest({type:"BomHead",lang:"zh-cn",query:{"*":{}},payload:{filter:{materialId:{eq:row.materialId},organizationId:{eq:$form.values.organizationId},status:{eq:"Y"}},page:{sort:"creationDate desc",pageNum:$form.query("PrRequirementForBuyer").get("data").bomVersionListPageNum,pageSize:$form.query("PrRequirementForBuyer").get("data").bomVersionListPageSize}},action:"listBomByParam"}).then(res=>{$form.query("*.bomVersionDialog.*.bomVersionList").take(field=>{field.value=res.data}),$form.query("PrRequirementForBuyer").get("data").bomVersionListTotal=res.originalData.payload.total})},$selBomVersion=($form,done)=>{const row=$form.query("*.bomVersionDialog.*.bomVersionList").take().componentProps.componentInstance.getRadioRecord();$form.values.reqLineList[$form.query("PrRequirementForBuyer").get("data").detailListCurrentIndex].bomVersionCode=row.versionCode,$form.values.reqLineList[$form.query("PrRequirementForBuyer").get("data").detailListCurrentIndex].bomHeadId=row.bomHeadId,done?done():$form.query("*.bomVersionDialog").take().setComponentProps({visible:!1})},$openBomVDetailDialog=($form,row,$queryEngine)=>{$form.query("*.bomDetailDialog").take().setComponentProps({visible:!0}),$getBomDetailList($form,row,$queryEngine)},$getBomDetailList=($form,row,$queryEngine)=>{$queryEngine.request.baseRequest({type:"BomLine",lang:"zh-cn",query:{"*":{}},payload:{filter:{bomHeadId:{eq:row.bomHeadId},distributeFlag:{eq:"Y"}},page:{sort:"creationDate desc",pageNum:$form.query("PrRequirementForBuyer").get("data").bomDetailListPageNum,pageSize:$form.query("PrRequirementForBuyer").get("data").bomDetailListPageSize}},action:"listBomLineByParam"}).then(res=>{res.data.forEach(item=>{item.componentQuantity=row.requirementQuantity||row.requirementQuantity===0?item.baseMaterialNum*+row.requirementQuantity:null}),$form.query("*.bomDetailDialog.*.bomDetailList").take(field=>{field.value=res.data}),$form.query("PrRequirementForBuyer").get("data").bomDetailListTotal=res.originalData.payload.total})},scope={t,app,http,$attrs:attrs,isReadOnly,emitTabRemove,$closeTab,getTotalAmount,setTotalAmount,getButtonConfig,workflowStatus,getCurrentUserInfo,getCategoryObj,openDialog,queryContent,addOneContent,beforeUpload,downloadTemplate,handleSuccess,exportFile,reSubmit,saveOrSubmitBill,importSlot,$viewVersion:viewVersion,$selBomVersion,$openBomVersionDialog,$getBomVersionList,$openBomVDetailDialog,orderConfig},components={MImport:Import,MaterialSelectDialog,BatchMaintainDialog,Tooltip:elementUi_commonExports.Tooltip,BomVersionDialog,BomDetailDialog,CPagination,BomVersionSearch},schema=defineSchemas({PrRequirementForBuyer:{type:"void","x-read-pretty":expression("$form.readPretty"),"x-component":"el-container","x-component-props":{class:"flex-container PrRequirementForBuyer",direction:"vertical"},"x-decorator":"QueryEngine","x-data":{bomVersionListPageNum:1,bomVersionListPageSize:15,bomVersionListTotal:0,bomDetailListPageNum:1,bomDetailListPageSize:15,bomDetailListTotal:0,detailListCurrentIndex:null,lastCategoryName:{categoryCode:"",categoryId:"",categoryName:"xxx"},displayItemTable:[],extraData:{fileModular:"pm",fileFunction:"purchaseApplication",fileType:"excel"},pageInfo:{pageTotal:0,pageNum:1,pageSize:15},queryForm:{categoryCode:null,categoryId:null,categoryName:null,materialCode:null,materialId:null,materialName:null,orgId:null,organizationId:null,organizationName:null,inputLevel:null},copyInit:{num:0,bol:!0}},"x-query-engine":{service:"sup-ce",actions:{submitRequirement:{loading:!0,cascadeDeletion:!0,transformRequest:expression(`(data, headers) => {
            console.log('submitRequirement=>', data, headers)
            data.query = {
              '*': {}
            }

            return data
          }`)},tempSaveRequirement:{loading:!0,cascadeDeletion:!0,transformRequest:expression(`(data, headers) => {
            console.log('tempSaveRequirement=>', data, headers)
            data.query = {
              '*': {}
            }

            return data
          }`)},read:{loading:!0,action:"getRequirementInfo",immediate:!0,ready:expression(`(v) => {
            console.log('ready=>', $form)
            getButtonConfig($form)
            $values.requirementHeadId = $attrs.params?.row?.requirementHeadId
            const { nickname, ceeaDeptId, department } = getCurrentUserInfo()
            $values.createdFullName = nickname
            $values.ceeaDepartmentId = ceeaDeptId
            $values.ceeaDepartmentName = department
            return !!$attrs.params?.row?.requirementHeadId
          }`),transformRequest:expression(`(data, headers) => {
            console.log('transformRequest=>', data, headers)
            data.payload = [{requirementHeadId: $attrs?.params?.row?.requirementHeadId || data.payload[0]}]

            data.query = {
              '*': {}
            }

            return data
          }`),onSuccess:expression(`(res) => {
            console.log('onsuccess=>', res.data)
            let data = res.data[0]
            workflowStatus.value = data.auditStatus
            $form.setValues(data)
            $self.data.lastCategoryName = {
              categoryId: data.categoryId,
              categoryName: data.categoryName,
              categoryCode: data.categoryCode,
            }
            $form.readPretty = isReadOnly
          }`)}}},properties:{BatchMaintainDialog:{type:"void","x-component":"BatchMaintainDialog","x-component-props":{requirementHead:"{{$form.values}}","@submit":expression(`(form) => {
            let rows = $form.query('reqLineList').take()
            .componentProps
            .componentInstance
            .getCheckboxRecords()
            rows.forEach(row => {
              Object.keys(form).forEach(key => {
                row[key] = form[key]
              })
            })
            $form.query('.BatchMaintainDialog').take().setComponentProps({
              visible: false
            })
          }`),"@close":expression(`() => {
            $form.query('.BatchMaintainDialog').take().setComponentProps({
              visible: false
            })
          }`)}},MaterialSelectDialog:{type:"void","x-component":"MaterialSelectDialog","x-component-props":{requirementHead:"{{$form.values}}",queryForm:"{{$form.query('PrRequirementForBuyer').get('data').queryForm}}",pageInfo:"{{$form.query('PrRequirementForBuyer').get('data').pageInfo}}",displayItemTable:"{{$form.query('PrRequirementForBuyer').get('data').displayItemTable}}","@queryContent":expression(`obj => {
            $form.query('PrRequirementForBuyer').get('data').queryForm = Object.assign($form.query('PrRequirementForBuyer').get('data').queryForm, obj)
            queryContent($form.query('PrRequirementForBuyer').get('data').queryForm, $form)
          }`),"@changeCurrentIndex":expression(`(currentIndex) => {
            $form.query('PrRequirementForBuyer').get('data').pageInfo.pageNum = currentIndex
            queryContent($form.query('PrRequirementForBuyer').get('data').queryForm, $form)
          }`),"@changeCurrentSize":expression(`(currentSize) => {
            $form.query('PrRequirementForBuyer').get('data').pageInfo.pageSize = currentSize
            queryContent($form.query('PrRequirementForBuyer').get('data').queryForm, $form)
          }`),"@addOneContent":expression(`(multipleSelection2) => {
            addOneContent(multipleSelection2, $form)
          }`),"@close":expression(`() => {
            $form.query('.MaterialSelectDialog').take().setComponentProps({
              visible: false
            })
          }`)}},SchemaWorkflow:{type:"void","x-component":"SchemaWorkflow","x-component-props":{params:{activeWorkflowTab:!0},"business-id":expression("$form.values.requirementHeadId || null"),"business-type":"MQL_PR_REQUIREMENT_INIT","button-custom":expression("{}"),"@click-handler":expression(`(type) => {
            saveOrSubmitBill(type, $form, $queryEngine, $bus)
          }`),"@submit-direct":expression(`(type) => {
            saveOrSubmitBill(type, $form, $queryEngine, $bus)
          }`),"@confirm":expression(`(type, comment) => {
            saveOrSubmitBill(type, $form, $queryEngine, $bus)
          }`),"@close-tab":expression(`() => {
            $closeTab($bus)
          }`),"@update-integration-mode":expression(`(integrationMode) => {
            console.log('update-integration-mode', integrationMode)
            // updateButtonConfig($form)
          }`)},properties:{collapse:{type:"void","x-component":"Collapse","x-component-props":{defaultOpenPanelCount:1},properties:{form:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("supRisk.baseInfo")},properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{minColumns:1,maxColumns:4,columnGap:32,rowGap:0},properties:{requirementHeadNum:{type:"string",title:i18nExpression("purchaseDemand.requirementHeadNum"),"x-component-props":{disabled:!0},"x-decorator":"FormItem"},auditStatus:{type:"string",title:i18nExpression("purchaseDemand.applyStatus"),"x-component":"DictSelect","x-component-props":{disabled:!0,code:"APPROVAL_STATUS"},"x-decorator":"FormItem"},createdFullName:{type:"string","x-decorator":"FormItem",title:i18nExpression("purchaseDemand.applicant"),"x-component-props":{disabled:!0}},ceeaDepartmentName:{type:"string",title:i18nExpression("purchaseDemand.ceeaDepartment"),"x-component-props":{disabled:!0},"x-decorator":"FormItem"},applyDate:{...yearMonthDaySelectorSegment,title:i18nExpression("purchaseDemand.applyDate"),"x-decorator":"FormItem","x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],disabled:!0}},orgId:{type:"string",title:i18nExpression("purchaseDemand.businessEntity"),"x-decorator":"FormItem","x-component":"OrganizationSelector","x-component-props":{readPretty:"{{$form.readPretty}}","parent-id":-1,"node-type":"OU",disabled:`{{
                            isReadOnly ||
                            $values.auditStatus === 'APPROVED' ||
                            !!$values?.reqLineList?.length
                          }}`,"@select":expression(`(node) => {
                            $values.orgId = node ? String(node.organizationId) : null
                            $values.orgCode = node ? String(node.organizationCode) : null
                            $values.orgName = node ? node.organizationName : null

                            if($form.values.organizationId){
                              $form.values.organizationId = null
                              $form.values.organizationCode = null
                              $form.values.organizationName = null
                            }
                          }`)},"x-validator":{required:!0,message:i18nExpression("purchaseDemand.orgIdTips")}},organizationId:{type:"string",title:i18nExpression("purchaseDemand.invOrg"),"x-decorator":"FormItem","x-component":"OrganizationSelector","x-component-props":{readPretty:"{{$form.readPretty}}","parent-id":"{{$values.orgId}}","node-type":"INV",disabled:`{{
                            isReadOnly ||
                            $values.auditStatus === 'APPROVED' ||
                            !!$values?.reqLineList?.length
                          }}`,"@select":expression(`(node) => {
                            $values.organizationId = node ? String(node.organizationId) : null
                            $values.organizationCode = node ? String(node.organizationCode) : null
                            $values.organizationName = node ? node.organizationName : null
                          }`)},"x-validator":{required:!0,message:i18nExpression("purchaseDemand.organizationIdTips")}},demandType:{type:"string",title:i18nExpression("purchaseDemand.demandType"),"x-component":"DictSelect","x-component-props":{code:"DEMAND_TYPE",disabled:"{{isReadOnly || $values.auditStatus === 'APPROVED'}}","@change":expression(`(val) => {
                            // 不是非生产性需求清空预算编号
                            if ($values.demandType !== 'NONPRODUCTIVE_DEMAND') {
                              $values.budgetManagementNum = null
                              $values.budgetManagementId = null
                              $values.totalBudget = null
                              $values.usedBudget = null
                              $values.unusedBudget = null
                              $values.reqLineList.forEach(item => {
                                item.totalAmount = 0
                                item.notaxPrice = 0
                              })
                            }
                          }`)},"x-decorator":"FormItem","x-validator":{required:!0,message:i18nExpression("purchaseDemand.selectRequireType")}},ceeaPurchaseType:{type:"string",title:i18nExpression("purchaseDemand.purchaseType"),"x-component":"DictSelect","x-component-props":{disabled:`
                            {{
                              isReadOnly ||
                              $values.auditStatus === 'APPROVED' ||
                              !!$values?.reqLineList?.length
                            }}
                          `,code:"PURCHASE_TYPE"},"x-decorator":"FormItem","x-validator":{required:!0,message:i18nExpression("purchaseDemand.purchaseTypeTips")}},categoryName:{type:"string","x-decorator":"FormItem",title:i18nExpression("purchaseDemand.materialCate"),"x-component":"QuickSearchWrapper","x-component-props":{readPretty:"{{$form.readPretty}}",showKey:"categoryName",showInput:"{{$values.categoryName}}",disabled:`{{
                            isReadOnly || $values.auditStatus === 'APPROVED'
                          }}`,name:"scc_base_purchase_category3","@close-quicksearch":expression(`(val, instance) => {
                            getCategoryObj(val, $form, instance)
                          }`)},"x-validator":{required:!0,message:i18nExpression("purchaseDemand.inputCategoryName")}},purchaseProject:{type:"string","x-decorator":"FormItem",title:i18nExpression("purchaseDemand.purchaseItem"),"x-component-props":{maxlength:"50",showWordLimit:!0,disabled:`{{
                            isReadOnly || $values.auditStatus === 'APPROVED'
                          }}`}},budgetManagementNum:{type:"string","x-decorator":"FormItem","x-hidden":"{{$values.demandType !== 'NONPRODUCTIVE_DEMAND'}}",title:i18nExpression("purchaseDemand.budgetNumber"),"x-component":"QuickSearch","x-component-props":{disabled:`{{
                            isReadOnly || $values.auditStatus === 'APPROVED'
                          }}`,readPretty:"{{$form.readPretty}}","show-key":"budgetManagementNumber","show-input":"{{$values.budgetManagementNum}}",name:"scc_pb_budget_management_effective","@close-quicksearch":expression(`(val) => {
                            $values.budgetManagementNum = val ? val.budgetManagementNumber : ''
                            $values.budgetManagementId = val ? val.budgetManagementId : ''
                          }`)},"x-validator":{required:!0,message:"请选择预算编号"}},totalBudget:{type:"string","x-decorator":"FormItem","x-hidden":"{{$values.demandType !== 'NONPRODUCTIVE_DEMAND'}}",title:i18nExpression("purchaseDemand.ceeaTotalBudget"),"x-component-props":{disabled:!0}},usedBudget:{type:"string","x-decorator":"FormItem","x-hidden":"{{$values.demandType !== 'NONPRODUCTIVE_DEMAND'}}",title:i18nExpression("purchaseDemand.actualAmountUsed"),"x-component-props":{disabled:!0}},unusedBudget:{type:"string","x-decorator":"FormItem","x-hidden":"{{$values.demandType !== 'NONPRODUCTIVE_DEMAND'}}",title:i18nExpression("purchaseDemand.availableBudget"),"x-component-props":{disabled:!0}},createdId:{type:"void","x-decorator":"FormItem","x-visible":!0,"x-query-engine-skip":!0},comments:{type:"string","x-decorator":"FormItem","x-decorator-props":{gridSpan:4},title:i18nExpression("contractMod.remark"),"x-component-props":{type:"textarea",maxlength:"500",showWordLimit:!0,disabled:`{{
                            isReadOnly || $values.auditStatus === 'APPROVED'
                          }}`,autosize:{minRows:2,maxRows:5}}},ceeaUrgencyExplain:{type:"string","x-decorator":"FormItem","x-decorator-props":{gridSpan:4},"x-hidden":"{{$values.ceeaPurchaseType !== 'URGENT'}}",title:i18nExpression("purchaseDemand.ceeaUrgencyExplain"),"x-component-props":{type:"textarea",maxlength:"500",showWordLimit:!0,disabled:`{{
                            isReadOnly || $values.auditStatus === 'APPROVED'
                          }}`,rows:2},"x-validator":{required:!0,message:i18nExpression("purchaseDemand.ceeaUrgencyExplainTips")}},ceeaAppointReason:{type:"string","x-decorator":"FormItem","x-decorator-props":{gridSpan:4},"x-hidden":"{{$values.ceeaPurchaseType !== 'APPOINT'}}",title:i18nExpression("purchaseDemand.ceeaAppointReason"),"x-component-props":{type:"textarea",maxlength:"500",showWordLimit:!0,disabled:`{{
                            isReadOnly || $values.auditStatus === 'APPROVED'
                          }}`,rows:2},"x-validator":{required:!0,message:i18nExpression("purchaseDemand.ceeaAppointReasonTips")}}}}}},lineList:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("purchaseDemand.itemInfo")},properties:{toolbar:{type:"void","x-component":"Space","x-component-props":{style:"margin-bottom: 12px"},"x-reactions":expression(`(field) => {
                      field.visible = !$form.readPretty
                    }`),properties:{add:{type:"void","x-component":"RButton","x-hidden":"{{$values.auditStatus === 'APPROVED'}}",title:i18nExpression("common.add"),"x-component-props":{type:"primary",disabled:"{{isReadOnly}}","@click":expression(`() => {
                            openDialog($form)
                            // $self.query('.reqLineList').take().componentProps.componentInstance.addRow('unshift')
                          }`)}},import:{type:"void",title:i18nExpression("common.excelImport"),"x-content":{default:"{{importSlot($form)}}"}},export:{type:"void","x-component":"RButton","x-hidden":"{{$values?.reqLineList?.length < 1 || $attrs.params.flag === 'add'}}",title:i18nExpression("purchaseDemand.export"),"x-component-props":{type:"primary",disabled:"{{isReadOnly || $values.auditStatus === 'APPROVED'}}","@click":expression("() => exportFile()")}},reSubmit:{type:"void","x-component":"RButton","x-hidden":"{{$values?.reqLineList?.length < 1 || $values?.reqLineList?.find(v => v.applyStatus !== 'RETURNING')}}",title:i18nExpression("purchaseDemand.resubmit"),"x-component-props":{type:"primary",style:{"margin-left":0},disabled:"{{isReadOnly}}","@click":expression("() => reSubmit($form, $queryEngine)")}},batchMaintain:{type:"void","x-component":"RButton","x-hidden":"{{!['edit', 'add'].includes($attrs.params.flag)}}",title:i18nExpression("vendorMod.batchMaintain"),"x-component-props":{type:"primary","@click":expression(`() => {
                            let rows = $form.query('reqLineList').take()
                            .componentProps
                            .componentInstance
                            .getCheckboxRecords()
                            if (rows.length < 1) {
                              app.$message.warning(t('purchaseDemand.selectAtLeastOneData'))
                              return
                            }
                            $form.query('.BatchMaintainDialog').take().setComponentProps({
                              visible: true
                            })
                          }`)}}}},reqLineList:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"checkbox",height:250,pagination:!1,sortable:!1,primaryKey:"requirementLineId",cascadeDeletion:!0,scrollY:{enabled:"{{$form.readPretty ? true : false}}"}},"x-query-engine-skip":!0,"x-query-engine-relation":"reqLineList:*",properties:generateXindexInOrder({requirementLineId:{type:"string","x-hidden":!0},rowNum:{type:"string","x-component":"RenderTableIndex",title:i18nExpression("purchaseDemand.lineNum"),"x-render-table-column":{width:80}},applyStatus:{type:"string",title:i18nExpression("purchaseDemand.applicationBankStatus"),"x-component":"DictSelect","x-component-props":{code:"APPLICATION_STATUS"},"x-render-table-column":{width:120}},materialCode:{type:"string",title:i18nExpression("purchaseDemand.itemCode"),"x-render-table-column":{width:120}},materialName:{type:"string",title:i18nExpression("purchaseDemand.itemName"),"x-render-table-column":{width:130}},unitCode:{type:"string",title:i18nExpression("purchaseDemand.unitCode"),"x-component":"DictSelect","x-component-props":{code:"unit"},"x-render-table-column":{width:100}},requirementQuantity:{type:"string","x-component":"el-input-number","x-component-props":{controls:!1,class:"input-number-precision",disabled:`{{
                            isReadOnly ||
                            $values.auditStatus === 'APPROVED' &&
                            $table.getRowByIndex($self.index).applyStatus !== 'RETURNING'
                          }}`,"@focus":"{{getTotalAmount($table.getRowByIndex($self.index)?.requirementQuantity, $form)}}","@change":"{{() => setTotalAmount($form, $table.getRowByIndex($self.index))}}"},"x-render-table-column":{width:90,customRender:!0,title:i18nExpression("purchaseDemand.requirementQuantity")},...feedbackLayoutIsPopover,"x-validator":{required:!0,message:i18nExpression("purchaseDemand.selectRequireQuantity")}},ceeaExecutedQuantity:{type:"string",title:i18nExpression("purchaseDemand.ceeaExecutedQuantity"),"x-render-table-column":{width:100}},requirementDate:{...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                            parseTime(row.creationDate, '{y}-{m}-{d}')
                          }`),disabled:`{{
                            isReadOnly ||
                            $values.auditStatus === 'APPROVED' &&
                            $table.getRowByIndex($self.index).applyStatus !== 'RETURNING'
                          }}`,"picker-options":{disabledDate(time){const today=new Date;return today.setHours(0),today.setMinutes(0),today.setSeconds(0),today.setMilliseconds(0),time.getTime()<today.getTime()}}},"x-render-table-column":{width:150,customRender:!0,title:i18nExpression("purchaseDemand.requirementDate")},...feedbackLayoutIsPopover,"x-validator":{required:!0,message:i18nExpression("purchaseDemand.selectRequireDate")}},receiveAddress:{type:"string","x-component":"DictSelect","x-component-props":{code:"{{String($values.organizationId)}}",disabled:`{{
                            isReadOnly ||
                            $values.auditStatus === 'APPROVED' ||
                            $table.getRowByIndex($self.index).applyStatus === 'RETURNING'
                          }}`,"custom-select-type":"{{$values.organizationId ? 'RECEIVE_ADDRESS' : ''}}","@change-value":expression(`(val, {element}) => {
                            let row = $table.getRowByIndex($self.index)
                            row.receiveContact = element ? element.receiver : ''
                            row.receiveTelephone = element ? element.receiverPhone : ''
                            row.receiveAddress = element ? element.siteName : ''
                          }`)},"x-render-table-column":{width:150,customRender:!0,title:i18nExpression("purchaseDemand.ceeaDeliveryPlaceOut")},...feedbackLayoutIsPopover,"x-validator":{required:!0,message:i18nExpression("purchaseDemand.selectReceiveAddress")}},bomVersionCode:{type:"string","x-visible":"{{orderConfig.showBom === 'Y' && $form.values.ceeaPurchaseType === 'OUTSOURCING'}}","x-render-table-column":{minWidth:150,title:"Bom版本",customRender:!0},"x-component":"BomVersionSearch","x-component-props":{disabled:"{{isReadOnly}}",inputModel:"{{$self.value}}","@clear":expression(`value => {
                            $form.query('PrRequirementForBuyer').get('data').detailListCurrentIndex = $self.index
                            $form.values.reqLineList[$self.index].bomVersionCode = ''
                            $form.values.reqLineList[$self.index].bomHeadId = null
                          }`),"@openDialog":expression(`() => {
                            $form.query('PrRequirementForBuyer').get('data').detailListCurrentIndex = $self.index
                            $openBomVersionDialog($form, $queryEngine,$table.getRowByIndex($self.index))
                          }`)}},bomDetail:{type:"void","x-visible":"{{orderConfig.showBom === 'Y' && $form.values.ceeaPurchaseType === 'OUTSOURCING'}}","x-render-table-column":{customRender:!0,title:i18nExpression("purchaseApplication.bomDetail"),minWidth:100,sortable:!1},properties:{layout:{type:"void","x-component":"Space",properties:{viewFollowUp:{type:"void",title:i18nExpression("purchaseApplication.detail"),"x-component":"TableButton","x-hidden":"{{!$table.getRowByIndex($self.index).bomVersionCode}}","x-component-props":{type:"text","@click":expression("({row}) => {$openBomVDetailDialog($form,row, $queryEngine)}")}}}}}},comments:{type:"string",title:i18nExpression("purchaseDemand.comments"),"x-component-props":{maxlength:50,showWordLimit:!0,disabled:"{{isReadOnly || $values.auditStatus === 'APPROVED'}}"},"x-render-table-column":{customRender:!0,width:150}},vendorName:{type:"string","x-component":"QuickSearch","x-component-props":{preQueryData:"{{{'c.CATEGORY_ID':$table.getRowByIndex($self.index).categoryId}}}",showInput:"{{$table.getRowByIndex($self.index).vendorName}}",showKey:"companyName",name:"scc_sup_company_info_all",disabled:`{{
                            isReadOnly ||
                            $table.getRowByIndex($self.index).ceeaIfDirectory === 'Y' ||
                            $values.auditStatus === 'APPROVED' ||
                            $values.ceeaPurchaseType !== 'APPOINT'
                          }}`,"@close-quicksearch":expression(`(val) => {
                            let row = $table.getRowByIndex($self.index)
                            row.vendorId = val ? val.companyId : ''
                            row.vendorCode = val ? val.companyCode : ''
                            row.vendorName = val ? val.companyName : ''
                          }`)},"x-render-table-column":{customRender:!0,required:"{{$form.values.ceeaPurchaseType  === 'APPOINT'}}",width:150,title:i18nExpression("purchaseDemand.awardedSupplierName")},...feedbackLayoutIsPopover,"x-validator":{required:"{{$form.values.ceeaPurchaseType  === 'APPOINT'}}",messages:i18nExpression("purchaseDemand.selectVendor")}},dmandLineRequest:{type:"string",title:i18nExpression("purchaseDemand.dmandLineRequest"),"x-component":"DictSelect","x-component-props":{code:"DMAND_LINE_REQUEST",disabled:`{{
                            isReadOnly ||
                            ($values.auditStatus === 'APPROVED' &&
                            $table.getRowByIndex($self.index).applyStatus !== 'RETURNING')
                          }}`},"x-render-table-column":{customRender:!0,width:150}},notaxPrice:{type:"string","x-hidden":"{{$values.demandType !== 'NONPRODUCTIVE_DEMAND'}}","x-component-props":{"v-input-format":{type:"float"},disabled:`{{
                            isReadOnly ||
                            $table.getRowByIndex($self.index).ceeaIfDirectory === 'Y' ||
                            $values.auditStatus === 'APPROVED'
                          }}`,"@change":"{{() => setTotalAmount($form, $table.getRowByIndex($self.index))}}"},"x-render-table-column":{customRender:!0,width:150,title:i18nExpression("purchaseDemand.priceIncludingTax")},...feedbackLayoutIsPopover,"x-validator":{triggerType:"onBlur",required:"{{$form.values.demandType  === 'NONPRODUCTIVE_DEMAND'}}",messages:i18nExpression("purchaseDemand.selectVendor")}},totalAmount:{type:"string","x-hidden":"{{$values.demandType !== 'NONPRODUCTIVE_DEMAND'}}",title:i18nExpression("purchaseDemand.totalAmount"),"x-render-table-column":{width:150}},categoryName:{type:"string",title:i18nExpression("purchaseDemand.materialCateSub"),"x-component-props":{disabled:!0},"x-render-table-column":{width:150}},ceeaIfDirectory:{type:"string","x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"},"x-render-table-column":{width:150,title:i18nExpression("purchaseDemand.ceeaIfCatalogMaterial")}},rejectReason:{type:"string",title:i18nExpression("purchaseDemand.returnReason"),"x-render-table-column":{width:150}},operation:{type:"void","x-visible":"{{!isReadOnly}}",title:i18nExpression("common.operation"),"x-render-table-column":{width:80,fixed:"right"},"x-component":"RenderTableButtonList","x-reactions":expression(`(field) => {
                          field.visible = !$form.readPretty
                        }`),properties:{delete:{type:"void",title:i18nExpression("common.delete"),"x-component-props":{type:"text",disabled:"{{isReadOnly}}","@click":expression(`
                                ({ row, rowIndex }) => {
                                  $table.remove(rowIndex)
                                }
                              `)}}}}})}}},attachmentList:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("purSettlementMod.addUploadFile")},properties:{toolbar:{type:"void","x-hidden":"{{$values.auditStatus === 'APPROVED'}}","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},"x-reactions":expression(`(field) => {
                      field.visible = !$form.readPretty
                    }`),properties:{add:{type:"void",title:i18nExpression("common.add"),"x-component-props":{type:"primary","@click":expression(`() => {
                            $form.query('.reqAttachList').take().componentProps.componentInstance.addRow('unshift')
                          }`)}}}},reqAttachList:{type:"array","x-component":"RenderTable","x-query-engine-skip":!0,"x-query-engine-relation":"reqAttachList:*","x-component-props":{preColumns:"checkbox, seq",editMode:!0,maxHeight:400,pagination:!1,sortable:!1,primaryKey:"attachId",cascadeDeletion:!0},properties:generateXindexInOrder({attachId:{type:"string","x-hidden":!0},attachName:{type:"void",title:i18nExpression("purchaseApplication.attachName"),"x-component":"SrmCommonFile","x-read-pretty":!0,"x-reactions":expression(`() => {
                          $self.setComponentProps({
                            defaultFile: {
                              fileId: $table.getRowByIndex($self.index)?.fileuploadId,
                              fileName: $table.getRowByIndex($self.index)?.attachName
                            }
                          })
                        }`),"x-component-props":{"extra-data":{uploadType:"DEF",sourceType:"WEB_APP",fileModular:"sup",fileFunction:"purchaseApplicationEngine",fileType:"images"},readonly:!1,"@on-change":expression(`({file}) => {
                            const row = $table.getRowByIndex($self.index)
                            row.fileuploadId = file.fileId.toString()
                            row.attachName = file.fileName
                            row.createdFullName = file.createdFullName
                            row.createdBy = file.createdBy
                            row.creationDate = file.creationDate
                          }`)},"x-render-table-column":{}},createdFullName:{type:"string","x-read-pretty":!0,title:i18nExpression("purchaseDemand.attachmentCreatedBy"),"x-render-table-column":{}},createdBy:{type:"string","x-read-pretty":!0,title:i18nExpression("vendorMod.account"),"x-render-table-column":{}},creationDate:{...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                            parseTime(row.creationDate, '{y}-{m}-{d}')
                          }`)},"x-read-pretty":!0,"x-query-engine-sort":"desc",title:i18nExpression("purchaseDemand.attachmentCreatedDate"),"x-render-table-column":{}},operation:{type:"void",title:i18nExpression("common.operation"),"x-render-table-column":{width:80,fixed:"right"},"x-component":"RenderTableButtonList","x-reactions":expression(`(field) => {
                          field.visible = !$form.readPretty
                        }`),properties:{delete:{type:"void",title:i18nExpression("common.delete"),"x-component-props":{type:"text","@click":expression(`
                                ({ row, rowIndex }) => {
                                  $table.remove(rowIndex)
                                }
                              `)}}}}})}}}}}}},bomVersionDialog:{...BomVersionDialog},bomDetailDialog:{...BomDetailDialog}}}});return{__sfc:!0,emitTabRemove,t,app,http,buyer,getCurrentUserInfo,confirmMessage,attrs,isReadOnly,$closeTab,getTotalAmount,setTotalAmount,workflowStatus,viewUpdateButton,disabledUpdateButton,getButtonConfig,checkMaterialList,getCategoryObj,resetForm,queryContent,openDialog,addOneContent,beforeUpload,downloadTemplate,handleSuccess,exportFile,reSubmit,submitCheck,getFormDetail,submitEvent,saveBill,saveOrSubmitBill,importSlot,$openBomVersionDialog,$getBomVersionList,$selBomVersion,$openBomVDetailDialog,$getBomDetailList,scope,components,schema,RenderEngine}}});var _sfc_render$2=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{schemaKey:"purchaseApplicationDetail",schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns$2=[],__component__$2=normalizeComponent(_sfc_main$2,_sfc_render$2,_sfc_staticRenderFns$2,!1,null,"2fbe5dba",null,null);const purchaseApplicationDetail=__component__$2.exports,_sfc_main$1=defineComponent({__name:"list-engine",setup(__props){const{emitTabAdd,t,app,http,confirmMessage,getGlobalNickname}=usePageHelper();getGlobalNickname(),onActivated(()=>{let{from,funName,fdSubject}=app.$route.params;if(from==="fromFun"&&funName==="purchaseApplication"){const requirementHeadId=Number(app.$route.params.formId),formNo=app.$route.params.formNo,row={...app.$route.params,requirementHeadId,requirementHeadNum:formNo};$openDetailTag("approveNumber",row)}if(from==="demandPoolManagement"&&funName==="purchaseApplication"){const row={...app.$route.params,requirementHeadId:fdSubject.requirementHeadId,requirementHeadNum:fdSubject.requirementHeadNum};$openDetailTag("approveNumber",row)}});const $openDetailTag=(type,row)=>{const mapInfo=new Map([["edit",{component:purchaseApplicationDetail,params:{flag:"edit",row,tabName:"purchaseApplicationDetail"+row?.requirementHeadNum},title:row?.requirementHeadNum,name:"purchaseApplicationDetail"+row?.requirementHeadNum}],["approvalOnly",{component:purchaseApplicationDetail,params:{flag:"approvalOnly",row,showType:"readOnly",tabName:"purchaseApplicationDetail"+row?.requirementHeadNum,activeWorkflowTab:!0},title:row?.requirementHeadNum,name:"purchaseApplicationDetail"+row?.requirementHeadNum}],["add",{component:purchaseApplicationDetail,params:{flag:"add",tabName:"purchaseApplicationDetail"},title:t("purchaseDemand.addPurApplication"),name:"purchaseApplicationDetail"}],["approveNumber",{component:purchaseApplicationDetail,params:{flag:"approveNumber",row,showType:"readOnly",tabName:"purchaseApplicationDetail"+row?.requirementHeadNum},title:row?.requirementHeadNum,name:"purchaseApplicationDetail"+row?.requirementHeadNum}]]);emitTabAdd(mapInfo.get(type))},$approvalOneItem=(row,$queryEngine)=>{http({url:"/api-sup-ce/pr/requirementHead/approval",method:"GET",params:{requirementHeadId:row.requirementHeadId},loading:!0}).then(()=>{app.$message.success(t("common.success")),$queryEngine.state.paginationManagement.refresh()})},$abandonOne=async(row,$queryEngine)=>{await confirmMessage(t("common.confirmAbandon"))==="confirm"&&$queryEngine.request.baseRequest({action:"abandonRequirement",payload:[{requirementHeadId:row.requirementHeadId}],query:{"*":{}}}).then(()=>{app.$message.success(t("common.success")),$queryEngine.state.paginationManagement.refresh()})},$budgetRelease=async(row,$queryEngine)=>{await confirmMessage(t("purchaseDemand.sureReleaseBugget"))==="confirm"&&$queryEngine.request.baseRequest({type:"PrRequirementForBuyer",action:"releaseBudget",payload:[{requirementHeadId:row.requirementHeadId}],query:{"*":{}}}).then(()=>{app.$message.success(t("common.success")),$queryEngine.state.paginationManagement.refresh()})},integrationMode=(async()=>{let{data}=await app.$api.base.flowAPI.getFlowIntegrationMode({businessType:"MQL_PR_REQUIREMENT_INIT"});return data})(),listQueryTodo=async()=>{let{data}=await app.$api.base.flowAPI.queryTodo({businessType:"MQL_PR_REQUIREMENT_INIT"});return data},scope={$t:t,app,parseTime,purchaseApplicationDetail,$approvalOneItem,$abandonOne,$budgetRelease,integrationMode,listQueryTodo,getGlobalNickname,$openDetailTag},components={purchaseApplicationDetail},schema=defineSchemas({PrRequirementForBuyer:{type:"void","x-query-engine":{service:"sup-ce",actions:{paginationQuery:{immediate:!0,action:"listRequirements",onSuccess:expression(`async (res) => {
            let mode = await integrationMode
            let tableData = res.data
            tableData.forEach(item => (item.integrationMode = mode))

            if (app.notSearchTodoMode.includes(mode)) {
              $form.values.materialDetail = tableData
              return
            }

            let queryTodoList = await listQueryTodo()

            const maps = []
            queryTodoList.forEach(item => maps.push(item.businessId))
            tableData.forEach(row => {
              let tempId = String(row.requirementHeadId)
              if (maps.includes(tempId)) {
                row.workflowAuditStatus = 'WAIT'
                row.arroverId = tempId
              }
            })

            $form.values.materialDetail = tableData
          }`)}}},"x-component":"QueryEngine","x-decorator":"el-container","x-decorator-props":{class:"flex-container",direction:"vertical"},properties:{bus:{type:"void","x-component":"BusEvent","x-component-props":{eventName:"PrRequirementForBuyer","@listener":expression(`() => {
            $queryEngine.state.paginationManagement.refresh()
          }`)}},query:{type:"object","x-query-engine-skip":!0,"x-component":"QueryFormByQueryEngine",properties:generateXindexInOrder({requirementHeadNum:{type:"string",title:i18nExpression("purchaseDemand.requirementHeadNum")},ceeaPurchaseType:{type:"string",title:i18nExpression("purchaseDemand.purchaseType"),"x-component":"DictSelect","x-component-props":{code:"PURCHASE_TYPE"}},auditStatus:{type:"string",title:i18nExpression("purchaseDemand.applyStatus"),"x-component":"DictSelect","x-component-props":{code:"APPROVAL_STATUS"}},orgId:{type:"string",title:i18nExpression("dataConfMod.orgId"),"x-component":"OrganizationSelector","x-component-props":{"node-type":"OU","parent-id":-1}},organizationId:{type:"string",title:i18nExpression("dataConfMod.organizationId"),"x-component":"OrganizationSelector","x-component-props":{"parent-id":"{{$form.values.query.orgId}}","node-type":"INV",scope:"{{ $form.values.query }}"}},purchaseProject:{type:"string",title:i18nExpression("purchaseDemand.purchaseItem")},applyDate:{type:"date",title:i18nExpression("purchaseDemand.applyDate"),"x-component-props":{type:"daterange","value-format":"yyyy-MM-dd"},"x-query-engine-query-operator":"between"},ceeaDepartmentId:{type:"string",title:i18nExpression("purchaseDemand.ceeaDepartment"),"x-component":"QuickSearchWrapper","x-component-props":{showKey:"descr",propKey:"deptid",name:"ceea_base_dept"}},createdFullName:{type:"string",title:i18nExpression("purchaseDemand.applicant"),"x-component":"QuickSearchWrapper","x-component-props":{showKey:"nickname",name:"scc_rbac_user_display","@close-quicksearch":expression(`(val) => {
                $self.value = val ? val.nickname : ''
              }`)}},categoryId:{type:"number",title:i18nExpression("purchaseDemand.materialCate"),"x-component":"QuickSearchWrapper","x-component-props":{showKey:"categoryName",propKey:"categoryId",name:"scc_base_purchase_category3"}},demandType:{type:"string",title:i18nExpression("purchaseDemand.demandType"),"x-component":"DictSelect","x-component-props":{code:"DEMAND_TYPE"}},budgetManagementId:{type:"string",title:i18nExpression("purchaseDemand.budgetNumber"),"x-component":"QuickSearchWrapper","x-component-props":{showKey:"budgetManagementNumber",propKey:"budgetManagementId",name:"scc_pb_budget_management_effective"}}})},toolbar:{type:"void","x-query-engine-skip":!0,"x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void",title:i18nExpression("common.add"),"x-component-props":{type:"primary","@click":expression(`() => {
                $openDetailTag('add')
              }`)}}}},materialDetail:{type:"array","x-component":"RenderTable","x-component-props":{style:"flex: 1",preColumns:"seq",openCustomTable:!0,editMode:"multi-row"},properties:{requirementHeadId:{type:"string","x-hidden":!0},lastUpdateDate:{type:"string","x-hidden":!0,"x-query-engine-sort":"desc"},requirementHeadNum:{type:"string","x-component":"TableButton","x-component-props":{type:"text",disabled:!1,"@click":expression(`({ row }) => {
                $openDetailTag('approveNumber', row)
              }`)},"x-render-table-column":{width:150,title:i18nExpression("purchaseDemand.requirementHeadNum")}},budgetManagementNum:{type:"string",title:i18nExpression("purchaseDemand.budgetNumber"),"x-render-table-column":{width:150}},demandType:{type:"string",title:i18nExpression("purchaseDemand.demandType"),"x-component":"DictSelect","x-component-props":{code:"DEMAND_TYPE"},"x-render-table-column":{width:120}},ceeaPurchaseType:{type:"string",title:i18nExpression("purchaseDemand.purchaseType"),"x-component":"DictSelect","x-component-props":{code:"PURCHASE_TYPE"},"x-render-table-column":{width:120}},auditStatus:{type:"string",title:i18nExpression("purchaseDemand.applyStatus"),"x-component":"DictSelect","x-component-props":{code:"APPROVAL_STATUS"},"x-render-table-column":{width:100}},applyDate:{...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.applyDate, '{y}-{m}-{d}')
              }`)},title:i18nExpression("purchaseDemand.applyDate"),"x-render-table-column":{width:100}},orgName:{type:"string",title:i18nExpression("purchaseDemand.businessEntity"),"x-render-table-column":{width:120}},organizationName:{type:"string",title:i18nExpression("purchaseDemand.invOrg"),"x-render-table-column":{width:100}},purchaseProject:{type:"string",title:i18nExpression("purchaseDemand.purchaseItem"),"x-render-table-column":{width:100}},ceeaDepartmentName:{type:"string",title:i18nExpression("purchaseDemand.ceeaDepartment"),"x-render-table-column":{width:100}},createdFullName:{type:"string",title:i18nExpression("purchaseDemand.applicant"),"x-render-table-column":{width:100}},categoryName:{type:"string",title:i18nExpression("purchaseDemand.materialCate"),"x-render-table-column":{width:120}},comments:{type:"string",title:i18nExpression("common.remark"),"x-render-table-column":{width:120}},createdBy:{type:"string","x-hidden":!0},sourceSystem:{type:"string","x-hidden":!0},operation:{type:"void","x-render-table-column":{title:i18nExpression("common.operation"),width:130,fixed:"right"},"x-component":"RenderTableButtonList",properties:{edit:{type:"void",title:i18nExpression("common.edit"),"x-component-props":{"@click":expression(`({ row, rowIndex }) => {
                    $openDetailTag('edit', row)
                  }`)},"x-reactions":`{{
                  (field) => {
                    field.visible =
                      ['DRAFT', 'WITHDRAW', 'REJECTED'].includes($table.getRowByIndex($self.index).auditStatus) &&
                      (
                        $table.getRowByIndex($self.index).createdBy === getGlobalNickname() ||
                        $table.getRowByIndex($self.index).sourceSystem === 'MRP'
                      )
                  }
                }}`},approval:{type:"void",title:i18nExpression("common.approve"),"x-component-props":{"@click":expression(`({ row, rowIndex }) => {
                    $openDetailTag('approvalOnly', row)
                  }`)},"x-reactions":`{{
                  (field) => {
                    field.visible =
                      app.flowWithTabMode.includes($table.getRowByIndex($self.index).integrationMode) &&
                      (
                        $table.getRowByIndex($self.index).auditStatus === 'SUBMITTED' ||
                        (
                          $table.getRowByIndex($self.index).auditStatus === 'APPROVING' &&
                          !!$table.getRowByIndex($self.index).arroverId
                        )
                      )
                  }
                }}`},approvalPass:{type:"void",title:i18nExpression("purchaseDemand.approved"),"x-component-props":{"@click":expression(`({ row, rowIndex }) => {
                      $approvalOneItem(row, $queryEngine)
                    }`)},"x-reactions":`{{
                  (field) => {
                    field.visible =
                      ['SUBMITTED', 'APPROVING'].includes($table.getRowByIndex($self.index).auditStatus) &&
                      app.srmFlowMode.includes($table.getRowByIndex($self.index).integrationMode) &&
                      !$table.getRowByIndex($self.index).workflowAuditStatus
                  }
                }}`},delete:{type:"void",title:i18nExpression("common.delete"),"x-component-props":{popconfirm:{title:i18nExpression("common.confirmDelete")},"@click":expression(`() => {
                    const row = $table.getRowByIndex($self.index)
                    console.log(row,'row')

                    $queryEngine.request.baseRequest({
                      action: 'removeRequirement',
                      payload: [{ requirementHeadId: row.requirementHeadId }],
                      query: { '*': {} }
                    })
                      .then((res) => {
                        $message.success($t('common.successDelete'))
                        $queryEngine.state.paginationManagement.refresh()
                      })
                  }`)},"x-reactions":`{{
                  (field) => {
                    field.visible =
                      ['DRAFT'].includes($table.getRowByIndex($self.index).auditStatus) &&
                      (
                        $table.getRowByIndex($self.index).createdBy === getGlobalNickname() ||
                        $table.getRowByIndex($self.index).sourceSystem === 'MRP'
                      )
                  }
                }}`},abandon:{type:"void",title:i18nExpression("common.abandon"),"x-component-props":{"@click":expression(`({ row, rowIndex }) => {
                      $abandonOne(row, $queryEngine)
                    }`)},"x-reactions":`{{
                  (field) => {
                    field.visible =
                      ['WITHDRAW', 'REJECTED', 'REFUSED', 'UNDER_APPROVAL'].includes($table.getRowByIndex($self.index).auditStatus) &&
                      (
                        $table.getRowByIndex($self.index).createdBy === getGlobalNickname() ||
                        $table.getRowByIndex($self.index).sourceSystem === 'MRP'
                      )
                  }
                }}`},budgetRelease:{type:"void",title:i18nExpression("purchaseDemand.budgetRelease"),"x-component-props":{"@click":expression(`({ row, rowIndex }) => {
                      $budgetRelease(row, $queryEngine)
                    }`)},"x-reactions":`{{
                  (field) => {
                    field.visible =
                      ['APPROVED', 'ABANDONED'].includes($table.getRowByIndex($self.index).auditStatus) &&
                      $table.getRowByIndex($self.index).demandType === 'NONPRODUCTIVE_DEMAND' &&
                      $table.getRowByIndex($self.index).unusedBudget > 0 &&
                      (
                        $table.getRowByIndex($self.index).createdBy === getGlobalNickname() ||
                        $table.getRowByIndex($self.index).sourceSystem === 'MRP'
                      )
                  }
                }}`}}}}}}}});return{__sfc:!0,emitTabAdd,t,app,http,confirmMessage,getGlobalNickname,$openDetailTag,$approvalOneItem,$abandonOne,$budgetRelease,integrationMode,listQueryTodo,scope,components,schema,RenderEngine}}});var _sfc_render$1=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{scope:_setup.scope,components:_setup.components,schema:_setup.schema,schemaKey:"PrRequirementForBuyerListPage"}})},_sfc_staticRenderFns$1=[],__component__$1=normalizeComponent(_sfc_main$1,_sfc_render$1,_sfc_staticRenderFns$1,!1,null,null,null,null);const listEngine=__component__$1.exports,_sfc_main={name:"PurchaseApplication",components:{NavTabs},data(){return{activeTab:"listEngine",tabs:[{title:this.$t("purchaseDemand.purchaseApplication"),name:"listEngine",component:listEngine,closable:!1}]}}};var _sfc_render=function(){var _vm=this,_c=_vm._self._c;return _c("NavTabs",{ref:"tabs",attrs:{"tabs-list":_vm.tabs,"cur-tab":_vm.activeTab}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const index=__component__.exports;export{index as default};
