import{N as NavTabs}from"./index-a035e78f.js";import{af as i18nExpression,ae as expression,cc as formGridSegment,ai as generateXindexInOrder,aj as editTableFormItemValid,al as defineComponent,am as usePageHelper,an as useAttrs,ar as defineSchemas,c7 as buttonListItemVisibleByPermission,bt as changeFieldVisibleByDeps,as as RenderEngine,bG as markRaw,n as normalizeComponent}from"./index-17d0ccd5.js";const BaseInfo={type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("outsource.materialReqNum")},"x-read-pretty":expression("$form.readPretty"),properties:{baseInfo:{type:"void",...formGridSegment,properties:{materialReqId:{type:"string","x-hidden":!0},materialReqNum:{type:"string",title:i18nExpression("outsource.materialReqNum"),"x-decorator":"FormItem","x-component-props":{disabled:!0}},handleStatus:{type:"string",title:i18nExpression("outsource.handleStatus"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{disabled:!0,code:"OS_MATERIAL_REQUISITION_STATUS"}},createdFullName:{type:"string",title:i18nExpression("common.createdFullName"),"x-decorator":"FormItem","x-component-props":{disabled:!0}},creationDate:{type:"string",title:i18nExpression("common.creationDate"),"x-decorator":"FormItem","x-component-props":{disabled:!0}},orgId:{type:"string",title:i18nExpression("dataConfMod.orgId"),"x-decorator":"FormItem","x-component":"OrganizationSelector","x-component-props":{readPretty:"{{$form.readPretty}}","parent-id":-1,"node-type":"OU",disabled:!0,"@select":expression(`(node) => {
              $values.orgId = node ? String(node.organizationId) : null
              $values.orgCode = node ? String(node.organizationCode) : null
              $values.orgName = node ? node.organizationName : null
              if($form.values.organizationId){
                $form.values.organizationId = null
                $form.values.organizationCode = null
                $form.values.organizationName = null
              }
            }`)},"x-validator":{required:!0,message:i18nExpression("purchaseDemand.orgIdTips")}},orgCode:{type:"string","x-hidden":!0},orgName:{type:"string","x-hidden":!0},organizationId:{type:"string",title:i18nExpression("dataConfMod.organizationId"),"x-decorator":"FormItem","x-component":"OrganizationSelector","x-component-props":{readPretty:"{{$form.readPretty}}","parent-id":"{{$values.orgId}}","node-type":"INV",disabled:!0,"@select":expression(`(node) => {
              $values.organizationId = node ? String(node.organizationId) : null
              $values.organizationCode = node ? String(node.organizationCode) : null
              $values.organizationName = node ? node.organizationName : null
            }`)},"x-validator":{required:!0,message:i18nExpression("purchaseDemand.organizationIdTips")}},organizationCode:{type:"string","x-hidden":!0},organizationName:{type:"string","x-hidden":!0},vendorName:{type:"string","x-decorator":"FormItem",title:i18nExpression("common.vendor"),"x-component-props":{disabled:!0}},vendorCode:{type:"string","x-hidden":!0},vendorId:{type:"string","x-hidden":!0},buyerSpecialRemarks:{type:"string","x-decorator":"FormItem","x-decorator-props":{gridSpan:4},title:i18nExpression("contractMod.remark"),"x-component-props":{type:"textarea",maxlength:"500",showWordLimit:!0,disabled:!0,autosize:{minRows:2,maxRows:5}}}}}}},MaterialsDetails={type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("outsource.materialReqItem")},properties:{detailList:{type:"array","x-component":"RenderTable","x-component-props":{class:"table-view-vxe-table",maxHeight:400,editMode:!0,pagination:!1,sortable:!1,primaryKey:"materialReqDetailId",cascadeDeletion:!0},"x-query-engine-skip":!0,"x-query-engine-relation":"detailList:*",properties:generateXindexInOrder({materialReqDetailId:{type:"string","x-hidden":!0},rowNum:{type:"string","x-read-pretty":!0,title:i18nExpression("purchaseDemand.lineNum"),"x-render-table-column":{minWidth:80}},rowStatus:{type:"string","x-hidden":!0,"x-read-pretty":!0,"x-component":"DictSelect","x-component-props":{code:"OrderDetailStatus"},"x-render-table-column":{title:i18nExpression("outsource.materialReqRowStatus"),minWidth:100}},materialLineId:{type:"string","x-hidden":!0},materialHeadNum:{type:"string","x-read-pretty":!0,"x-render-table-column":{title:i18nExpression("outsource.materialHeadNum"),minWidth:120}},materialRowNum:{type:"string","x-read-pretty":!0,"x-render-table-column":{title:i18nExpression("outsource.materialRowNum"),minWidth:120}},orderDetailId:{type:"string","x-hidden":!0},orderNumber:{type:"string","x-read-pretty":!0,"x-render-table-column":{title:i18nExpression("outsource.orderNum"),minWidth:120}},orderRowNum:{type:"string","x-read-pretty":!0,"x-render-table-column":{title:i18nExpression("outsource.requirementHeadNum"),minWidth:100}},materialId:{type:"string","x-hidden":!0},materialCode:{type:"string","x-read-pretty":!0,title:i18nExpression("purchaseDemand.itemCode"),"x-render-table-column":{minWidth:100}},materialName:{type:"string","x-read-pretty":!0,title:i18nExpression("purchaseDemand.itemName"),"x-render-table-column":{minWidth:150}},materialUnit:{type:"string","x-read-pretty":!0,"x-render-table-column":{minWidth:100,title:i18nExpression("purchaseDemand.unitCode")}},orderNum:{type:"string","x-read-pretty":!0,title:i18nExpression("outsource.orderNum"),"x-render-table-column":{minWidth:100}},baseMaterialId:{type:"string","x-hidden":!0},baseMaterialCode:{type:"string","x-read-pretty":!0,title:i18nExpression("outsource.baseMaterialCode"),"x-render-table-column":{minWidth:100}},baseMaterialName:{type:"string","x-read-pretty":!0,title:i18nExpression("outsource.baseMaterialName"),"x-render-table-column":{minWidth:100}},baseMaterialUnit:{type:"string","x-read-pretty":!0,title:i18nExpression("outsource.baseMaterialUnit"),"x-render-table-column":{minWidth:100}},baseMaterialNum:{type:"string","x-read-pretty":!0,title:i18nExpression("outsource.baseMaterialNum"),"x-render-table-column":{minWidth:100}},returnQuantity:{type:"number","x-read-pretty":!0,"x-render-table-column":{title:i18nExpression("outsource.returnQuantity"),minWidth:120,titlePrefix:{content:i18nExpression("outsource.materialsReturnQuantityTip")}}},receivedQuantity:{type:"number","x-read-pretty":!0,"x-render-table-column":{title:i18nExpression("outsource.receivedQuantity"),minWidth:120,titlePrefix:{content:i18nExpression("outsource.receivedQuantityTitlePrefix")}}},unreceivedQuantity:{type:"string",title:i18nExpression("outsource.unreceivedQuantity"),"x-read-pretty":!0,"x-render-table-column":{minWidth:100}},thisReceivedQuantity:{type:"number","x-render-table-column":{title:i18nExpression("outsource.thisReceivedQuantity"),minWidth:125},"x-component-props":{disabled:!0}},vendorReceiptQty:{type:"number",title:i18nExpression("outsource.vendorReceiptQty"),"x-render-table-column":{minWidth:130},"x-read-pretty":"{{ $form.readPretty }}","x-reactions":expression(`() => {
            let row = $table.getRowByIndex($self.index)
            // 第一次点确认 才默认赋值，后续驳回不做赋值
            let thisReceivedQuantity = row?.thisReceivedQuantity
            let vendorReceiptQty = row?.vendorReceiptQty
            let handleStatus = $form.values.handleStatus // SUBMIT
            if (thisReceivedQuantity && !vendorReceiptQty && handleStatus=='SUBMIT') {
              row.vendorReceiptQty = thisReceivedQuantity // 赋值
              let vendorReceiptQtyN = row?.vendorReceiptQty
              let vendorDiffQty = thisReceivedQuantity - vendorReceiptQtyN
              row.vendorDiffQty = vendorDiffQty // 赋值
            }
          }`),"x-hidden":expression("$receiveConfirmCtrl($form)"),...editTableFormItemValid,"x-component-props":{min:0,"@change":expression(`(val) => {
              if(val>-1) {
                let fileVal = val
                let row = $table.getRowByIndex($self.index)
                let thisReceivedQuantity = row?.thisReceivedQuantity
                let vendorDiffQty = thisReceivedQuantity - fileVal
                row.vendorDiffQty = vendorDiffQty
              }
            }`)}},vendorDiffQty:{type:"number",title:i18nExpression("outsource.vendorDiffQty"),"x-render-table-column":{minWidth:110},"x-component-props":{disabled:!0},"x-hidden":expression("$receiveConfirmCtrl($form)")},receivedAddress:{type:"string",title:i18nExpression("outsource.receivedAddress"),"x-render-table-column":{minWidth:100,customRender:!0},"x-component-props":{disabled:!0}},receivedLinkman:{type:"string",title:i18nExpression("outsource.receivedLinkman"),"x-render-table-column":{minWidth:100,customRender:!0},"x-component-props":{disabled:!0}},receivedPhone:{type:"string",title:i18nExpression("outsource.receivedPhone"),"x-render-table-column":{minWidth:100,customRender:!0},"x-component-props":{disabled:!0}},buyerRemark:{type:"string",title:i18nExpression("purchaseDemand.comments"),"x-component-props":{maxlength:50,showWordLimit:!0,disabled:!0},"x-render-table-column":{minWidth:150,customRender:!0}}})},reback:{type:"void",...formGridSegment,"x-component-props":{style:"margin-top:20px;"},properties:{vendorDiffDescription:{type:"string","x-decorator":"FormItem","x-decorator-props":{gridSpan:12},title:i18nExpression("outsource.vendorDiffDescription"),"x-component-props":{type:"textarea",maxlength:"500",showWordLimit:!0,disabled:!1,autosize:{minRows:2,maxRows:5}},"x-hidden":expression('$formInputRebackFlag($form,["CREATE"])')},buyerAdditionalRemarks:{type:"string","x-decorator":"FormItem","x-decorator-props":{gridSpan:12},title:i18nExpression("outsource.buyerAdditionalRemarks"),"x-component-props":{type:"textarea",maxlength:"500",showWordLimit:!0,autosize:{minRows:2,maxRows:5},disabled:!0},"x-hidden":expression('$formInputRebackFlag($form,["CREATE"])')}}}}},FileInfo={type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("outsource.materialReqFile")},properties:{toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},"x-reactions":expression(`(field) => {
        field.visible = !$form.readPretty
      }`),properties:{addFile:{type:"void",title:'{{$t("common.add")}}',"x-component-props":{type:"primary",disabled:!1,"@click":expression(`() => {
              $self.query('.attachList').take(field => {
                field.componentProps.componentInstance.addRow()
              })
            }`)}}}},attachList:{type:"array","x-component":"RenderTable","x-component-props":{class:"table-view-vxe-table",maxHeight:400,pagination:!1,sortable:!1,editMode:!0,preColumns:"seq",primaryKey:"attachId",cascadeDeletion:!0},"x-query-engine-skip":!0,"x-query-engine-relation":"attachList:*",properties:generateXindexInOrder({materialReqId:{type:"string","x-hidden":!0},attachId:{type:"string","x-hidden":!0},fileuploadId:{type:"string","x-hidden":!0},attachName:{type:"string",title:i18nExpression("outsource.attachName"),"x-component":"SrmCommonFile","x-component-props":{readonly:expression('$table.getRowByIndex($self.index).attachId && ![undefined, "", "CREATE"].includes($form.values.handleStatus)'),"extra-data":{fileModular:"sup",fileFunction:"contractPerformanceCheck",fileType:"images"},"default-file":{fileId:"{{$table.getRowByIndex($self.index).fileuploadId}}",fileName:"{{$self.value}}"},"@on-change":expression(`({file}) => {
              let row = $table.getRowByIndex($self.index)
              const { fileId = '', fileName = '' } = file || {}
              row.fileuploadId = fileId.toString()
              $self.value = fileName
              row.createdFullName = file.createdBy
              row.creationDate = file.creationDate
            }`)},"x-render-table-column":{minWidth:130}},createdFullName:{type:"string","x-render-table-column":{title:i18nExpression("outsource.createdFullName"),minWidth:120},"x-component-props":{disabled:!0}},creationDate:{type:"string","x-render-table-column":{title:i18nExpression("outsource.creationDate")},"x-component-props":{disabled:!0}},operation:{type:"void",title:i18nExpression("common.operation"),"x-render-table-column":{width:120,fixed:"right"},"x-component":"RenderTableButtonList","x-reactions":expression(`(field) => {
            field.visible = !$form.readPretty
          }`),properties:{delete:{type:"void",title:i18nExpression("common.delete"),"x-component-props":{type:"text",disabled:expression('$table.getRowByIndex($self.index).attachId && ![undefined, "", "CREATE"].includes($form.values.handleStatus)'),"@click":expression(`() => {
                    $table.remove($self.index)
                  }
                `)}}}}})}}},_sfc_main$2=defineComponent({__name:"edit",setup(__props){const{emitTabRemove,t:$t,app}=usePageHelper();let $attrs=useAttrs();const $saveFormBill=(type,status,$form,$queryEngine,$confirm,$message,$bus)=>{const values=$form.values;type==="saveOrUpdate"?$submitData(type,status,values,$form,$queryEngine,$confirm,$message,$bus):$form.validate().then(()=>{if(type=="vendorRefuse"&&!values.vendorDiffDescription)return $message.warning($t("outsource.fillVendorDiffDescription"));$submitData(type,status,values,$form,$queryEngine,$confirm,$message,$bus)}).catch(err=>{})},$submitData=(action,status,$values,$form,$queryEngine,$confirm,$message,$bus)=>{const form=$values;$queryEngine.request.baseRequest({type:"OsMaterialReq",action,service:"sup-ce",loading:!0,payload:[{...form,handleStatus:status}],query:{"*":{}}}).then(res=>{const materialReqId=res.originalData?.records[0]||"";action=="saveOrUpdate"?($message.success($t("common.successSave")),$form.values.materialReqId=materialReqId,$queryEngine.request.read(materialReqId,{action:"getDetail"})):($message.success($t("common.successSave")),emitTabRemove($attrs.tabName)),$bus.$emit("osMaterialReqList")}).catch(err=>{})},$formInputEditFlag=($form,ctrlStatus)=>{let status=$form.values.handleStatus;return ctrlStatus.includes(status)},$receiveConfirmCtrl=($form,$queryEngine)=>{let receiveConfirm=$form.query("state").get("data").receiveConfirm,handleStatus=$form.values.handleStatus;return!receiveConfirm||receiveConfirm&&!["","CREATE","SUBMIT","REFUSE","VALID"].includes(handleStatus)},$formInputRebackFlag=($form,ctrlStatus)=>{let receiveConfirm=$form.query("state").get("data").receiveConfirm,status=$form.values.handleStatus;return!receiveConfirm||receiveConfirm&&ctrlStatus.includes(status)},outsourceMaterialsDetailSchema=defineSchemas({state:{type:"void","x-component":"Fragment","x-hidden":!0,"x-data":{receiveConfirm:!1}},OsMaterialReq:{type:"void","x-component":"FormContainer","x-decorator":"QueryEngine","x-query-engine":{service:"sup-ce",type:"OsMaterialReq",actions:{getDetail:{immediate:!0,loading:!0,ready:expression(`() => {
            $form.readPretty = $readOnly
            $form.query('state').get('data').receiveConfirm = $attrs.params.receiveConfirm
            let id = $attrs.params.materialReqId
            $form.values.materialReqId = id
            return !!id
          }`),method:"read",autoFormatResult:!1,transformRequest:expression(`(data, headers) => {
            let materialReqId = $attrs.params.materialReqId || data.payload[0]
            data.action = 'getDetail'
            data.tree = true
            data.loading = true
            data.payload = {
              "filter": {
                  "materialReqId": {
                      eq: materialReqId
                  }
              }
            }
            return data
          }`),onSuccess:expression(`(res) => {
            const data = res.records[0]
            $form.setValues({
              ...data
            })
          }`)},saveOrUpdate:{cascadeDeletion:!0},submit:{cascadeDeletion:!0}}},properties:{collapse:{type:"void","x-component":"Collapse","x-component-props":{defaultOpenPanelCount:1},properties:{receiptInfo:{...BaseInfo},orderDetail:{...MaterialsDetails},relevantAttachment:{...FileInfo}}}},items:{type:"void",properties:{buttonList:{type:"void","x-component":"ButtonList",properties:{goBack:{type:"void",title:expression('$t($readOnly ? "common.backTo" : "common.cancel")'),"x-component-props":{messageBox:expression(`$readOnly ? undefined : () => ({
                  type: 'warning',
                  title: $t("common.tips"),
                  message: $t('outsource.goBackConfirm'),
                  showCancelButton: true,
                  beforeClose: (action, dom, done) => {
                    // action的值有cancel confirm
                    if (action === 'confirm') {
                      $bus.$emit('osMaterialReqList')
                      emitTabRemove($attrs.tabName)
                    }
                    done()
                  }
                })`),"@click":expression(`()=> {
                  if($readOnly){
                    emitTabRemove($attrs.tabName)
                    return
                  }
                }`)}},reject:{type:"void",title:'{{$t("common.toRefuse")}}',"x-component-props":{...buttonListItemVisibleByPermission("outsourceMaterialsSup:reject"),"@click":expression(`(values) => {
                  // 驳回的时候差异说明必填
                  $saveFormBill('vendorRefuse','REFUSE', $form, $queryEngine, $confirm, $message, $bus)
                }`)},"x-reactions":changeFieldVisibleByDeps(["handleStatus"],`
                  $deps[0] === 'SUBMIT' && !$form.readPretty
                `)},accept:{type:"void",title:'{{$t("common.accept")}}',"x-component-props":{type:"primary",...buttonListItemVisibleByPermission("outsourceMaterialsSup:accept"),"@click":expression(`async (values) => {
                  $saveFormBill('vendorAccept','VALID', $form, $queryEngine, $confirm, $message, $bus)
                }`)},"x-reactions":changeFieldVisibleByDeps(["handleStatus"],`
                  $deps[0] === 'SUBMIT' && !$form.readPretty
              `)}}}}}}});return{__sfc:!0,emitTabRemove,$t,app,$attrs,$saveFormBill,$submitData,$formInputEditFlag,$receiveConfirmCtrl,$formInputRebackFlag,outsourceMaterialsDetailSchema,scope:{emitTabRemove,app,$attrs,$markRaw:markRaw,$saveFormBill,$submitData,$formInputEditFlag,$receiveConfirmCtrl,$formInputRebackFlag},components:{BaseInfo,MaterialsDetails,FileInfo},RenderEngine}}});var _sfc_render$2=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{schemaKey:"outsourceMaterialsDetail",pageAttrs:_setup.$attrs,schema:_setup.outsourceMaterialsDetailSchema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns$2=[],__component__$2=normalizeComponent(_sfc_main$2,_sfc_render$2,_sfc_staticRenderFns$2,!1,null,null,null,null);const outsourceMaterialsDetail=__component__$2.exports,_sfc_main$1=defineComponent({__name:"list",setup(__props){const{emitTabAdd,app}=usePageHelper(),$getReceiveConfirm=async $form=>{let res=await app.$http({url:"/api-sup-ce/purchaseConfig/get/outsourcing/receiveConfirm",method:"GET",loading:!1});res.data,$form.query("state").get("data").receiveConfirm=res.data||!1},outsourceMaterialsListSup2=defineSchemas({state:{type:"void","x-component":"Fragment","x-hidden":!0,"x-data":{receiveConfirm:!1}},OsMaterialReq:{type:"void","x-decorator":"QueryEngine","x-query-engine":{service:"sup-ce",actions:{vendorQuery:{immediate:!0,method:"paginationQuery",ready:expression(`() => {
            $getReceiveConfirm($form)
            return true
          }`),transformRequest:expression(`(data, headers) => {
            return data
          }`)}}},"x-component":"el-container","x-component-props":{class:"flex-container the_contractTemplateList_wrapper",direction:"vertical"},properties:{bus:{type:"void","x-component":"BusEvent","x-component-props":{eventName:"osMaterialReqList","@listener":expression(`() => {
            $queryEngine.state.paginationManagement.refresh()
          }`)}},query:{type:"object","x-query-engine-skip":!0,"x-component":"QueryFormByQueryEngine",properties:generateXindexInOrder({materialReqNum:{type:"string",title:i18nExpression("outsource.materialReqNum")},handleStatus:{type:"string",title:i18nExpression("outsource.handleStatus"),"x-component":"DictSelect","x-component-props":{filterItem:["CREATE"],code:"OS_MATERIAL_REQUISITION_STATUS"}},orgId:{type:"string",title:i18nExpression("dataConfMod.orgId"),"x-component":"OrganizationSelector","x-component-props":{"node-type":"OU","parent-id":-1,placeholder:i18nExpression("common.pleaseSelect")}},organizationId:{type:"string",title:i18nExpression("dataConfMod.organizationId"),"x-component":"OrganizationSelector","x-component-props":{"node-type":"INV",placeholder:i18nExpression("common.pleaseSelect"),"parent-id":expression("$form.values.query.orgId || -1")}},creationDate:{title:i18nExpression("common.creationDate"),"x-query-engine-query-operator":"between",type:"date","x-component-props":{placeholder:i18nExpression("common.pleaseSelectDate"),"value-format":"yyyy-MM-dd",type:"datetimerange",format:"yyyy-MM-dd HH:mm:ss","default-time":["00:00:00","23:59:59"]}},createdFullName:{type:"string",title:i18nExpression("common.createdFullName"),"x-query-engine-query-operator":"contains"}})},toolbar:{type:"void","x-component":"ButtonList","x-component-props":{style:"margin-bottom: 16px; height:28px"},properties:{}},table:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",class:"table-view-vxe-table",openCustomTable:!0},properties:generateXindexInOrder({materialReqId:{type:"string","x-hidden":!0},materialReqNum:{"x-component":"TableButton","x-component-props":{type:"text","@click":expression(`({row}) => {
                let materialReqId = row.materialReqId
                let tab = {
                  component: outsourceMaterialsDetail,
                  params: {
                    flag: 'view',
                    materialReqId: materialReqId,
                    receiveConfirm: $form.query('state').get('data').receiveConfirm,
                    tabName: 'outsourceMaterialsDetail' + row.materialReqNum,
                    row
                  },
                  title: row.materialReqNum,
                  name: 'outsourceMaterialsDetail' + row.materialReqNum
                }
                emitTabAdd(tab)
              }`)},"x-render-table-column":{title:i18nExpression("outsource.materialReqNum"),minWidth:130,customRender:!0}},handleStatus:{type:"string",title:i18nExpression("outsource.handleStatus"),"x-component":"DictSelect","x-component-props":{code:"OS_MATERIAL_REQUISITION_STATUS"},"x-render-table-column":{minWidth:100}},orgName:{type:"string",title:i18nExpression("dataConfMod.orgId"),"x-render-table-column":{minWidth:150}},orgCode:{type:"string","x-hidden":!0},orgId:{type:"string","x-hidden":!0},organizationName:{type:"string",title:i18nExpression("dataConfMod.organizationId"),"x-render-table-column":{minWidth:150}},organizationCode:{type:"string","x-hidden":!0},organizationId:{type:"string","x-hidden":!0},vendorCode:{type:"string",title:i18nExpression("common.vendorCode"),"x-render-table-column":{minWidth:100}},vendorName:{type:"string",title:i18nExpression("common.vendorName"),"x-render-table-column":{minWidth:150}},createdFullName:{type:"string",title:i18nExpression("common.creationDate"),"x-render-table-column":{minWidth:120}},creationDate:{type:"string",title:i18nExpression("common.creationDate"),"x-render-table-column":{minWidth:140}},lastUpdateDate:{type:"string","x-query-engine-sort":"desc",title:i18nExpression("common.lastUpdateDate"),"x-render-table-column":{minWidth:140}},operation:{type:"void",title:'{{$t("common.operation")}}',"x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList","x-component-props":{max:2},properties:{vendorReply:{type:"void",title:'{{$t("common.reply")}}',"x-reactions":changeFieldVisibleByDeps([".handleStatus"],'["SUBMIT"].includes($deps[0]) && $form.query("state").get("data").receiveConfirm'),"x-component-props":{...buttonListItemVisibleByPermission("outsourceMaterialsSup:vendorReply"),type:"text","@click":expression(`({ row }) => {
                    let materialReqId = row.materialReqId
                    let tab = {
                      component: outsourceMaterialsDetail,
                      params: {
                        flag: 'approve',
                        materialReqId: materialReqId,
                        row,
                        receiveConfirm: $form.query('state').get('data').receiveConfirm,
                        tabName: 'outsourceMaterialsDetail' + row.materialReqNum
                      },
                      title: row.materialReqNum,
                      name: 'outsourceMaterialsDetail' + row.materialReqNum
                    }
                    emitTabAdd(tab)
                  }`)}}}}})}}}});return{__sfc:!0,emitTabAdd,app,$getReceiveConfirm,outsourceMaterialsListSup:outsourceMaterialsListSup2,scope:{emitTabAdd,app,i18nExpression,$getReceiveConfirm,outsourceMaterialsDetail},components:{},RenderEngine}}});var _sfc_render$1=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{schemaKey:"outsourceMaterialsListSup",schema:_setup.outsourceMaterialsListSup,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns$1=[],__component__$1=normalizeComponent(_sfc_main$1,_sfc_render$1,_sfc_staticRenderFns$1,!1,null,null,null,null);const outsourceMaterialsListSup=__component__$1.exports,_sfc_main={name:"OutsourceMaterials",components:{NavTabs},data(){return{activeTab:"outsourceMaterialsListSup",tabs:[{title:()=>this.$t("route.outsourceMaterialsSup"),name:"outsourceMaterialsListSup",component:outsourceMaterialsListSup,closable:!1}]}}};var _sfc_render=function(){var _vm=this,_c=_vm._self._c;return _c("NavTabs",{ref:"tabs",attrs:{"tabs-list":_vm.tabs,"cur-tab":_vm.activeTab}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const index=__component__.exports;export{index as default};
