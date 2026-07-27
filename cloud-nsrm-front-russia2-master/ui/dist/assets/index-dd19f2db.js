import{N as NavTabs}from"./index-9a7f2446.js";import{ae as i18nExpression,ad as expression,cf as formGridSegment,af as yearMonthDaySelectorSegment,aD as requiredValidatorSegment,ca as buttonListItemVisibleByPermission,ah as generateXindexInOrder,aj as feedbackLayoutIsPopover,ai as editTableFormItemValid,bD as changeFieldVisibleByDeps,ak as defineComponent,al as usePageHelper,am as useAttrs,aq as defineSchemas,ar as RenderEngine,bN as markRaw,n as normalizeComponent}from"./index-6b6051d8.js";const baseInfo={type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("outsource.returnReqBaseInfo")},"x-read-pretty":expression("$form.readPretty"),properties:{baseInfo:{type:"void",...formGridSegment,properties:{returnId:{type:"string","x-hidden":!0},returnNum:{type:"string",title:i18nExpression("outsource.returnNum"),"x-decorator":"FormItem","x-component-props":{disabled:!0}},status:{type:"string",title:i18nExpression("outsource.outsourceReturnStatus"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{disabled:!0,code:"OS_MATERIAL_RETURN_ORDER_STATUS"}},createdFullName:{type:"string",title:i18nExpression("common.createdFullName"),"x-decorator":"FormItem","x-component-props":{disabled:!0}},creationDate:{...yearMonthDaySelectorSegment,title:i18nExpression("common.creationDate"),"x-decorator":"FormItem","x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],disabled:!0}},orgId:{type:"string",title:i18nExpression("dataConfMod.orgId"),"x-decorator":"FormItem","x-component":"OrganizationSelector","x-component-props":{readPretty:"{{$form.readPretty}}","parent-id":-1,"node-type":"OU",disabled:expression('$form.readPretty ? undefined : ![undefined, "", "DRAFT"].includes($form.values.status) || ($form.values.detailList?.length > 0)'),"@select":expression(`(node) => {
              $values.orgId = node ? String(node.organizationId) : null
              $values.orgCode = node ? String(node.organizationCode) : null
              $values.orgName = node ? node.organizationName : null

              if($form.values.organizationId){
                $form.values.organizationId = null
                $form.values.organizationCode = null
                $form.values.organizationName = null
                $form.values.detailList = []
              }
            }`)},...requiredValidatorSegment},organizationId:{type:"string",title:i18nExpression("dataConfMod.organizationId"),"x-decorator":"FormItem","x-component":"OrganizationSelector","x-component-props":{readPretty:"{{$form.readPretty}}","parent-id":"{{$values.orgId}}","node-type":"INV",disabled:expression('$form.readPretty ? undefined : ![undefined, "", "DRAFT"].includes($form.values.status) || ($form.values.detailList?.length > 0)'),"@select":expression(`(node) => {
              $values.organizationId = node ? String(node.organizationId) : null
              $values.organizationCode = node ? String(node.organizationCode) : null
              $values.organizationName = node ? node.organizationName : null
              $form.values.detailList = []
            }`)},...requiredValidatorSegment},vendorName:{type:"string","x-decorator":"FormItem",title:i18nExpression("common.vendor"),default:expression("app.$store.getters.userInfo.companyName"),"x-component-props":{disabled:!0}},vendorCode:{type:"string",default:expression("app.$store.getters.userInfo.companyCode"),"x-hidden":!0},vendorId:{type:"string",default:expression("app.$store.getters.userInfo.companyId"),"x-hidden":!0},comments:{type:"string","x-decorator":"FormItem","x-decorator-props":{gridSpan:4},title:i18nExpression("contractMod.remark"),"x-component-props":{type:"textarea",maxlength:"500",showWordLimit:!0,disabled:expression("$formEditFlag($form)"),autosize:{minRows:2,maxRows:5}}}}}}},materialsDetails={type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("outsource.returnReqMtDetail")},properties:{toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},"x-reactions":expression(`(field) => {
          field.visible = !$form.readPretty
      }`),properties:{addMaterial:{type:"void",title:'{{$t("common.add")}}',"x-component-props":{...buttonListItemVisibleByPermission("outsourceReturnMaterialsSup:create"),type:"primary",disabled:expression("$formEditFlag($form)"),"@click":expression(`() => {
              const {orgId, organizationId, vendorId} = $form.values
              if (orgId && organizationId && vendorId) {
                $form.query('returnMaterialsDailog').take().setComponentProps({ visible: true })
              } else {
                return $message.warning($t('cusEntry.supplement20250211.message20')) // 请维护业务实体、库存组织
              }
            }`)}}}},detailList:{type:"array","x-component":"RenderTable","x-component-props":{class:"table-view-vxe-table",maxHeight:400,pagination:!1,sortable:!1,editMode:!0,primaryKey:"returnLineId",cascadeDeletion:!0},"x-query-engine-skip":!0,"x-query-engine-relation":"detailList:*",properties:generateXindexInOrder({returnLineId:{type:"string","x-hidden":!0},rowNum:{type:"string",title:i18nExpression("purchaseDemand.lineNum"),"x-read-pretty":!0,"x-render-table-column":{minWidth:80}},returnId:{type:"string","x-hidden":!0},rowStatus:{type:"string","x-hidden":!0,"x-read-pretty":!0,"x-component":"DictSelect","x-component-props":{code:"OrderDetailStatus"},"x-render-table-column":{title:i18nExpression("outsource.returnRowStatus"),minWidth:100}},materialReqNum:{type:"string","x-read-pretty":!0,"x-render-table-column":{title:i18nExpression("outsource.materialReqNum"),minWidth:120}},materialReqRow:{type:"string","x-read-pretty":!0,"x-render-table-column":{title:i18nExpression("outsource.materialReqRow"),minWidth:120}},materialReqDetailId:{type:"string","x-hidden":!0},materialLineId:{type:"string","x-hidden":!0},materialHeadNum:{type:"string","x-read-pretty":!0,"x-render-table-column":{title:i18nExpression("outsource.materialHeadNum"),minWidth:120}},materialRowNum:{type:"string","x-read-pretty":!0,"x-render-table-column":{title:i18nExpression("outsource.materialRowNum"),minWidth:120}},orderDetailId:{type:"string","x-hidden":!0},orderNumber:{type:"string","x-read-pretty":!0,"x-render-table-column":{title:i18nExpression("outsource.orderNumber"),minWidth:120}},orderDetailRow:{type:"number","x-read-pretty":!0,"x-render-table-column":{title:i18nExpression("outsource.requirementHeadNum"),minWidth:100}},materialCode:{type:"string","x-read-pretty":!0,title:i18nExpression("purchaseDemand.itemCode"),"x-render-table-column":{minWidth:100}},materialName:{type:"string","x-read-pretty":!0,title:i18nExpression("purchaseDemand.itemName"),"x-render-table-column":{minWidth:150}},materialUnit:{type:"string","x-read-pretty":!0,"x-render-table-column":{minWidth:100,title:i18nExpression("purchaseDemand.unitCode")}},orderQuantity:{type:"string","x-read-pretty":!0,title:i18nExpression("outsource.orderNum"),"x-render-table-column":{minWidth:100}},baseMaterialId:{type:"string","x-hidden":!0},baseMaterialCode:{type:"string","x-read-pretty":!0,title:i18nExpression("outsource.baseMaterialCode"),"x-render-table-column":{minWidth:100}},baseMaterialName:{type:"string","x-read-pretty":!0,title:i18nExpression("outsource.baseMaterialName"),"x-render-table-column":{minWidth:100}},baseMaterialUnit:{type:"string","x-read-pretty":!0,title:i18nExpression("outsource.baseMaterialUnit"),"x-render-table-column":{minWidth:100}},baseMaterialNum:{type:"string","x-read-pretty":!0,title:i18nExpression("outsource.baseMaterialNum"),"x-render-table-column":{minWidth:100}},receivedQuantity:{type:"number","x-read-pretty":!0,"x-render-table-column":{title:i18nExpression("outsource.returnReceivedQuantity"),minWidth:120}},returnQuantity:{type:"string",title:i18nExpression("outsource.returnQuantity"),"x-read-pretty":!0,"x-render-table-column":{minWidth:100,titlePrefix:{content:i18nExpression("outsource.materialsReturnQuantityTip")}}},thisReturnQuantity:{type:"number","x-render-table-column":{title:i18nExpression("outsource.thisReturnQuantity"),minWidth:120,customRender:!0},"x-component-props":{min:0},...feedbackLayoutIsPopover,"x-validator":{required:!0,triggerType:"onBlur",message:i18nExpression("common.requiredField")}},returnReason:{type:"string",title:i18nExpression("outsource.rowReturnReason"),"x-render-table-column":{minWidth:130,customRender:!0},"x-component":"DictSelect","x-component-props":{code:"OS_MATERIAL_RETURN_REASON_TYPE"},...editTableFormItemValid},isUpdateUnreceived:{type:"string",default:"Y",title:i18nExpression("outsource.isUpdateUnreceived"),"x-render-table-column":{minWidth:150,customRender:!0},"x-component":"Checkbox","x-component-props":{"true-label":"Y","false-label":"N"},...editTableFormItemValid},detailComments:{type:"string",title:i18nExpression("purchaseDemand.comments"),"x-component-props":{maxlength:50,showWordLimit:!0},"x-render-table-column":{minWidth:150,customRender:!0}},operation:{type:"void",title:i18nExpression("common.operation"),"x-render-table-column":{width:100,fixed:"right"},"x-component":"RenderTableButtonList","x-reactions":expression(`(field) => {
            field.visible = !$form.readPretty
          }`),properties:{delete:{type:"void",title:i18nExpression("common.delete"),"x-component-props":{type:"text",disabled:expression("$formEditFlag($form)"),"@click":expression(`
                    ({ rowIndex }) => {
                      $table.remove(rowIndex)
                    }
                `)}}}}})},reback:{type:"void",...formGridSegment,"x-component-props":{style:"margin-top:20px;"},properties:{rejectReason:{type:"string","x-decorator":"FormItem","x-decorator-props":{gridSpan:24},title:i18nExpression("outsource.replyBuyerRejectReason"),"x-component-props":{type:"textarea",maxlength:"500",showWordLimit:!0,disabled:!0,autosize:{minRows:2,maxRows:5}},"x-reactions":changeFieldVisibleByDeps([".status"],'(["WAITING_BUYER_CONFIRM","BUYER_REJECT","VALID"].includes($deps[0]))')},vendorAdditionalExp:{type:"string","x-decorator":"FormItem","x-decorator-props":{gridSpan:24},title:i18nExpression("outsource.vendorAdditionalExp"),"x-component-props":{type:"textarea",maxlength:"500",showWordLimit:!0,disabled:!1,autosize:{minRows:2,maxRows:5}},"x-reactions":changeFieldVisibleByDeps([".status"],'(["BUYER_REJECT","VALID"].includes($deps[0]))'),...requiredValidatorSegment}}}}},ReturnMaterialsDailogSegment={materialsSelect:{type:"void","x-query-engine":{service:"sup-ce",type:"OsMaterialReqDetail",actions:{paginationQuery:{immediate:!1,action:"query",method:"paginationQuery"}}},"x-decorator":"el-container","x-component":"QueryEngine","x-decorator-props":{class:"flex-container",direction:"vertical"},properties:{returnMaterialsDailog:{type:"object",title:i18nExpression("outsource.returnMaterialsDailog"),"x-component":"RDialog","x-component-props":{class:"dialogMain",size:"large","close-on-click-modal":!1,"@opened":expression(`() => {
            $form.query($self.address.concat('query')).take((field) => {
              field.invoke('query')
            })
          }`),beforeClose:expression(`(done, type, closeLoading) => {
            if(type === 'ok'){
              const selection = $form.query($self.address.concat('returnMtDetailList')).take().invoke('getCheckboxRecords')
              if (!selection.length) {
                closeLoading()
                return $message.error($t('contractMod.msgSelData'))
              }
              $setMaterialData(selection,$form,$message)
              closeLoading()
            }else{
              done()
            }
          }`)},"x-data":{viewRow:null},properties:{query:{type:"object","x-query-engine-skip":!0,"x-component":"QueryFormByQueryEngine","x-component-props":{immediateQueryForm:!1},properties:generateXindexInOrder({materialReqNum:{type:"string",title:i18nExpression("outsource.materialReqNum"),"x-query-engine-query-operator":"contains","x-query-engine-relation":"materialReqId","x-query-engine-relation-strict":!0},orderNumber:{type:"string",title:i18nExpression("outsource.orderNumber"),"x-query-engine-query-operator":"contains"},materialCode:{type:"string",title:i18nExpression("purchaseDemand.itemCode"),"x-query-engine-query-operator":"contains"},baseMaterialCode:{type:"string",title:i18nExpression("outsource.baseMaterialCode"),"x-query-engine-query-operator":"contains"},orgId:{type:"string","x-hidden":!0,default:expression("$form.values.orgId"),"x-query-engine-relation":"materialReqId","x-query-engine-relation-strict":!0},organizationId:{type:"string","x-hidden":!0,default:expression("$form.values.organizationId"),"x-query-engine-relation":"materialReqId","x-query-engine-relation-strict":!0},vendorId:{type:"string","x-hidden":!0,default:expression("$form.values.vendorId"),"x-query-engine-relation":"materialReqId","x-query-engine-relation-strict":!0},handleStatus:{type:"string","x-hidden":!0,default:"VALID","x-query-engine-relation":"materialReqId","x-query-engine-relation-strict":!0},haveReturnQuantity:{type:"string","x-hidden":!0,default:"N","x-query-engine-query-operator":"ne"}})},returnMtDetailList:{type:"array","x-component":"RenderTable","x-component-props":{class:"table-view-vxe-table",preColumns:"checkbox, seq",maxHeight:350,pagination:!0,sortable:!1},properties:generateXindexInOrder({materialReqDetailId:{type:"string","x-hidden":!0},orgId:{type:"string","x-hidden":!0,"x-query-engine-relation":"materialReqId"},organizationId:{type:"string","x-hidden":!0,"x-query-engine-relation":"materialReqId"},vendorId:{type:"string","x-hidden":!0,"x-query-engine-relation":"materialReqId"},handleStatus:{type:"string","x-hidden":!0,"x-query-engine-relation":"materialReqId"},materialReqId:{type:"string","x-query-engine-relation":"materialReqId","x-hidden":!0},materialReqNum:{type:"string","x-query-engine-relation":"materialReqId","x-render-table-column":{title:i18nExpression("outsource.materialReqNum"),minWidth:150}},rowNum:{type:"string","x-render-table-column":{title:i18nExpression("outsource.materialReqRow"),minWidth:120}},materialLineId:{type:"string","x-hidden":!0},materialHeadNum:{type:"string","x-render-table-column":{title:i18nExpression("outsource.materialHeadNum"),minWidth:150}},materialRowNum:{type:"string","x-render-table-column":{title:i18nExpression("outsource.materialRowNum"),minWidth:150}},orderDetailId:{type:"string","x-hidden":!0},orderNumber:{type:"string","x-render-table-column":{title:i18nExpression("outsource.orderNumber"),minWidth:150}},orderRowNum:{type:"string","x-render-table-column":{title:i18nExpression("outsource.requirementHeadNum"),minWidth:100}},materialId:{type:"string","x-hidden":!0},materialCode:{type:"string",title:i18nExpression("purchaseDemand.itemCode"),"x-render-table-column":{minWidth:150}},materialName:{type:"string",title:i18nExpression("purchaseDemand.itemName"),"x-render-table-column":{minWidth:150}},materialUnit:{type:"string","x-render-table-column":{minWidth:100,title:i18nExpression("purchaseDemand.unitCode")}},orderNum:{type:"string",title:i18nExpression("purchaseDemand.requirementQuantity"),"x-render-table-column":{minWidth:100}},baseMaterialId:{type:"string","x-hidden":!0},baseMaterialCode:{type:"string",title:i18nExpression("outsource.baseMaterialCode"),"x-render-table-column":{minWidth:100}},baseMaterialName:{type:"string",title:i18nExpression("outsource.baseMaterialName"),"x-render-table-column":{minWidth:100}},baseMaterialUnit:{type:"string",title:i18nExpression("outsource.baseMaterialUnit"),"x-render-table-column":{minWidth:100}},baseMaterialNum:{type:"string",title:i18nExpression("outsource.baseMaterialNum"),"x-render-table-column":{minWidth:100}},thisReceivedQuantity:{type:"number",title:i18nExpression("outsource.thisReceivedQuantity"),"x-render-table-column":{minWidth:100},"x-hidden":!0},vendorDiffQty:{type:"number",title:i18nExpression("outsource.vendorDiffQty"),"x-render-table-column":{minWidth:100},"x-hidden":!0},receivedQuantity:{type:"number",title:i18nExpression("outsource.returnReceivedQuantity"),"x-render-table-column":{minWidth:100},"x-reactions":expression(`() => {
                  let row = $table.getRowByIndex($self.index)
                  if (row) {
                    let thisReceivedQuantity = row?.thisReceivedQuantity
                    let vendorDiffQty = row?.vendorDiffQty || 0
                    // 本次领料数量 - 差异数量
                    if (thisReceivedQuantity) {
                      let receivedQuantity = thisReceivedQuantity-vendorDiffQty
                      row.receivedQuantity = receivedQuantity // 赋值
                    } else {
                      row.receivedQuantity = null // 赋值
                    }
                  }
                }`)},returnQuantity:{type:"string",title:i18nExpression("outsource.returnQuantity"),"x-render-table-column":{minWidth:100}},lastUpdateDate:{type:"string","x-hidden":!0,"x-query-engine-sort":"desc"},haveReturnQuantity:{type:"string","x-hidden":!0}})}}}}}},fileInfo={type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("outsource.materialReqFile")},properties:{toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},"x-reactions":expression(`(field) => {
          field.visible = !$form.readPretty
      }`),properties:{addFile:{type:"void",title:'{{$t("common.add")}}',"x-component-props":{type:"primary",disabled:!1,"@click":expression(`() => {
              $self.query('.attachList').take().componentProps.componentInstance.addRow()
            }`)}}}},attachList:{type:"array","x-component":"RenderTable","x-component-props":{class:"table-view-vxe-table",maxHeight:400,pagination:!1,sortable:!1,editMode:!0,preColumns:"seq",primaryKey:"attachId",cascadeDeletion:!0},"x-query-engine-skip":!0,"x-query-engine-relation":"attachList:*",properties:generateXindexInOrder({returnId:{type:"string","x-hidden":!0},attachId:{type:"string","x-hidden":!0},fileuploadId:{type:"string","x-hidden":!0},attachName:{type:"string",title:i18nExpression("outsource.attachName"),"x-component":"SrmCommonFile","x-component-props":{readonly:expression('$table.getRowByIndex($self.index).attachId && ![undefined, "", "DRAFT"].includes($form.values.status)'),"extra-data":{fileModular:"sup",fileFunction:"contractPerformanceCheck",fileType:"images"},"default-file":{fileId:"{{$table.getRowByIndex($self.index).fileuploadId}}",fileName:"{{$self.value}}"},"@on-change":expression(`({file}) => {
              let row = $table.getRowByIndex($self.index)
              const { fileId = '', fileName = '' } = file || {}
              row.fileuploadId = fileId.toString()
              $self.value = fileName
              row.createdFullName = file.createdBy
              row.creationDate = file.creationDate
            }`)},"x-render-table-column":{minWidth:130}},createdFullName:{type:"string","x-render-table-column":{title:i18nExpression("outsource.createdFullName"),minWidth:120},"x-component-props":{disabled:!0}},creationDate:{...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
              parseTime(row.lastUpdateDate, '{y}-{m}-{d}')
            }`),disabled:!0},"x-render-table-column":{title:i18nExpression("outsource.creationDate")}},operation:{type:"void",title:i18nExpression("common.operation"),"x-render-table-column":{width:120,fixed:"right"},"x-component":"RenderTableButtonList","x-reactions":expression(`(field) => {
            field.visible = !$form.readPretty
          }`),properties:{delete:{type:"void",title:i18nExpression("common.delete"),"x-component-props":{type:"text",disabled:expression('$table.getRowByIndex($self.index).attachId && ![undefined, "", "DRAFT"].includes($form.values.status)'),"@click":expression(`() => {
                    $table.remove($self.index)
                  }
                `)}}}}})}}},_sfc_main$2=defineComponent({__name:"edit",setup(__props){const{emitTabRemove,t:$t,app}=usePageHelper();let $attrs=useAttrs();const $saveFormBill=(type,status,$form,$queryEngine,$confirm,$message,$bus)=>{const{returnMaterialsDailog:returnMaterialsDailog2,...values}=$form.values;type==="saveOrUpdate"?$submitData(type,status,values,$form,$queryEngine,$confirm,$message,$bus):$form.validate("*(!returnMaterialsDailog)").then(()=>{if(values.detailList.length==0)return $message.warning($t("outsource.fillReturnDetailList")),!1;$submitData(type,status,values,$form,$queryEngine,$confirm,$message,$bus)}).catch(err=>{})},$submitData=(action,billStatus,$values,$form,$queryEngine,$confirm,$message,$bus)=>{const form=$values;$queryEngine.request.baseRequest({type:"OsMaterialReturn",action,service:"sup-ce",loading:!0,payload:[{...form,status:billStatus}],query:{"*":{}}}).then(res=>{const returnId=res.originalData?.records[0]||"";action=="saveOrUpdate"?($message.success($t("common.successSave")),$form.values.returnId=returnId,$queryEngine.request.read(returnId,{action:"getDetail"})):($message.success($t("common.successSave")),emitTabRemove($attrs.tabName)),$bus.$emit("osgMaterialReturnList")}).catch(err=>{})},$setRepeatData=(ids,data,selection,condition,lineSet)=>{let getCondition=row=>typeof condition=="function"?condition(row):row[condition],dataArr=[],isTip=!1;if(selection.forEach((row,i)=>{if(ids.includes(getCondition(row)))isTip=!0;else{let otherFiled=lineSet?lineSet(row):{};dataArr.push({...otherFiled})}}),data.push(...dataArr),data.forEach((row,i)=>row.rowNum=Number(i+1)),isTip)return app.$message.warning($t("orderMod.checkDataRowUnique"))},$setMaterialData=(selections,$form,$message)=>{if(selections.length===0)return $message.warning($t("common.msgSelectData"));const ids=$form.values.detailList.map(item=>{if(!item.materialReqDetailIdXXX)return item.materialCode});$setRepeatData(ids,$form.values.detailList,selections,"materialReqDetailIdXXX",v=>({materialLineId:v.materialLineId,materialHeadNum:v.materialHeadNum,materialReqRow:v.rowNum,orderDetailId:v.orderDetailId,orderNumber:v.orderNumber,orderDetailRow:v.orderRowNum,materialReqNum:v.materialReqNum,materialReqDetailId:v.materialReqDetailId,materialId:v.materialId,materialCode:v.materialCode,materialName:v.materialName,materialUnit:v.materialUnit,baseMaterialId:v.baseMaterialId,baseMaterialCode:v.baseMaterialCode,baseMaterialName:v.baseMaterialName,baseMaterialUnit:v.baseMaterialUnit,baseMaterialNum:v.baseMaterialNum,materialRowNum:v.materialRowNum,returnQuantity:v.returnQuantity,receivedQuantity:v.receivedQuantity,orderQuantity:v.orderNum,thisReturnQuantity:null,isUpdateUnreceived:"Y",returnReason:"",detailComments:""})),$form.query(".returnMaterialsDailog").take().setComponentProps({visible:!1})},$formEditFlag=($form,$queryEngine)=>{let status=$form.values.status;return![void 0,"","DRAFT"].includes(status)},questionDetailSchema=defineSchemas({state:{type:"void","x-component":"Fragment","x-hidden":!0,"x-data":{}},OsMaterialReturn:{type:"void","x-component":"FormContainer","x-decorator":"QueryEngine","x-query-engine":{service:"sup-ce",type:"OsMaterialReturn",actions:{getDetail:{immediate:!0,loading:!0,ready:expression(`() => {
            $form.readPretty = $readOnly
            let id = $attrs.params.returnId
            $form.values.returnId = id
            return !!id
          }`),method:"read",autoFormatResult:!1,transformRequest:expression(`(data, headers) => {
            let returnId = $attrs.params.returnId || data.payload[0]
            data.action = 'getDetail'
            data.tree = true
            data.loading = true
            data.payload = {
              "filter": {
                  "returnId": {
                      eq: returnId
                  }
              }
            }
            return data
          }`),onSuccess:expression(`(res) => {
            const data = res.records[0]
            $form.setValues({
              ...data
            })
          }`)},saveOrUpdate:{cascadeDeletion:!0},vendorSubmit:{cascadeDeletion:!0}}},properties:{collapse:{type:"void","x-component":"Collapse","x-component-props":{defaultOpenPanelCount:1},properties:{receiptInfo:{...baseInfo},orderDetail:{...materialsDetails},relevantAttachment:{...fileInfo}}}},items:{type:"void",properties:{buttonList:{type:"void","x-component":"ButtonList",properties:{goBack:{type:"void",title:expression('$t($readOnly ? "common.backTo" : "common.cancel")'),"x-component-props":{messageBox:expression(`$readOnly ? undefined : () => ({
                  type: 'warning',
                  title: $t("common.tips"),
                  message: $t('outsource.goBackConfirm'),
                  showCancelButton: true,
                  beforeClose: (action, dom, done) => {
                    // action的值有cancel confirm
                    if (action === 'confirm') {
                      $bus.$emit('osgMaterialReturnList')
                      emitTabRemove($attrs.tabName)
                    }
                    done()
                  }
                })`),"@click":expression(`()=> {
                  if($readOnly){
                    emitTabRemove($attrs.tabName)
                    return
                  }
                }`)}},save:{type:"void",title:i18nExpression("common.staging"),"x-component-props":{...buttonListItemVisibleByPermission("outsourceReturnMaterialsSup:create"),type:"default",plain:"plain","@click":expression(`async (values) => {
                  $saveFormBill('saveOrUpdate','DRAFT', $form, $queryEngine, $confirm, $message, $bus)
                }`)},"x-reactions":changeFieldVisibleByDeps(["status"],`
                  [undefined,'DRAFT'].includes($deps[0]) && !$form.readPretty
              `)},submit:{type:"void",title:i18nExpression("common.submit"),"x-component-props":{type:"primary",...buttonListItemVisibleByPermission("outsourceReturnMaterialsSup:create"),"@click":expression(`async (values) => {
                  $saveFormBill('vendorSubmit','WAITING_BUYER_CONFIRM', $form, $queryEngine, $confirm, $message, $bus)
                }`)},"x-reactions":changeFieldVisibleByDeps(["status"],`
                  [undefined,'DRAFT'].includes($deps[0]) && !$form.readPretty
              `)},replay:{type:"void",title:i18nExpression("common.reply"),"x-component-props":{type:"primary",...buttonListItemVisibleByPermission("outsourceReturnMaterialsSup:reply"),"@click":expression(`async (values) => {
                  $saveFormBill('vendorSubmit','WAITING_BUYER_CONFIRM', $form, $queryEngine, $confirm, $message, $bus)
                }`)},"x-reactions":changeFieldVisibleByDeps(["status"],`
                  ['BUYER_REJECT'].includes($deps[0]) && !$form.readPretty
              `)}}}}}},...ReturnMaterialsDailogSegment});return{__sfc:!0,emitTabRemove,$t,app,$attrs,$saveFormBill,$submitData,$setRepeatData,$setMaterialData,$formEditFlag,questionDetailSchema,scope:{emitTabRemove,app,$attrs,$markRaw:markRaw,$saveFormBill,$submitData,$formEditFlag,$setRepeatData,$setMaterialData},components:{},RenderEngine}}});var _sfc_render$2=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{schemaKey:"outsourceReturnMaterialsDetailSup",pageAttrs:_setup.$attrs,schema:_setup.questionDetailSchema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns$2=[],__component__$2=normalizeComponent(_sfc_main$2,_sfc_render$2,_sfc_staticRenderFns$2,!1,null,null,null,null);const outsourceReturnMaterialsDetail=__component__$2.exports,_sfc_main$1=defineComponent({__name:"list",setup(__props){const{emitTabAdd,t:$t,app}=usePageHelper(),schema=defineSchemas({OsMaterialReturn:{type:"void","x-decorator":"QueryEngine","x-query-engine":{service:"sup-ce",actions:{vendorQuery:{immediate:!0,method:"paginationQuery",transformRequest:expression(`(data, headers) => {
            return data
          }`)}}},"x-component":"el-container","x-component-props":{class:"flex-container the_contractTemplateList_wrapper",direction:"vertical"},properties:{bus:{type:"void","x-component":"BusEvent","x-component-props":{eventName:"osgMaterialReturnList","@listener":expression(`() => {
            $queryEngine.state.paginationManagement.refresh()
          }`)}},query:{type:"object","x-query-engine-skip":!0,"x-component":"QueryFormByQueryEngine",properties:generateXindexInOrder({returnNum:{type:"string",title:i18nExpression("outsource.returnNum")},status:{type:"string",title:i18nExpression("outsource.outsourceReturnStatus"),"x-component":"DictSelect","x-component-props":{code:"OS_MATERIAL_RETURN_ORDER_STATUS"}},orgId:{type:"string",title:i18nExpression("dataConfMod.orgId"),"x-component":"OrganizationSelector","x-component-props":{"node-type":"OU","parent-id":-1,placeholder:i18nExpression("common.pleaseSelect")}},organizationId:{type:"string",title:i18nExpression("dataConfMod.organizationId"),"x-component":"OrganizationSelector","x-component-props":{"node-type":"INV",placeholder:i18nExpression("common.pleaseSelect"),"parent-id":expression("$form.values.query.orgId || -1")}},creationDate:{title:i18nExpression("common.creationDate"),"x-query-engine-query-operator":"between",type:"date","x-component-props":{placeholder:i18nExpression("common.pleaseSelectDate"),"value-format":"yyyy-MM-dd",type:"datetimerange",format:"yyyy-MM-dd HH:mm:ss","default-time":["00:00:00","23:59:59"]}},createdFullName:{type:"string",title:i18nExpression("common.createdFullName"),"x-query-engine-query-operator":"contains"}})},toolbar:{type:"void","x-component":"ButtonList","x-component-props":{style:"margin-bottom: 16px"},properties:{add:{type:"void",title:'{{$t("common.add")}}',"x-component":"RButton","x-component-props":{...buttonListItemVisibleByPermission("outsourceReturnMaterialsSup:create"),type:"primary","@click":expression(`() => {
                let tab = {
                  component: outsourceReturnMaterialsDetail,
                  params: {
                    flag: 'add',
                    tabName: 'outsourceReturnMaterialsDetail'
                  },
                  ctrlHeight: true,
                  title: $t('outsource.addReturnMaterialReqNum'),// '新增委外退料单'
                  name: 'outsourceReturnMaterialsDetail'
                }
                emitTabAdd(tab)
              }`)}}}},table:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",class:"table-view-vxe-table",openCustomTable:!0},properties:generateXindexInOrder({returnId:{type:"string","x-hidden":!0},returnNum:{"x-component":"TableButton","x-component-props":{type:"text","@click":expression(`({row}) => {
                let returnId = row.returnId
                let tab = {
                  component: outsourceReturnMaterialsDetail,
                  params: {
                    flag: 'view',
                    returnId: returnId,
                    tabName: 'outsourceReturnMaterialsDetail' + row.returnNum,
                    row
                  },
                  title: row.returnNum,
                  name: 'outsourceReturnMaterialsDetail' + row.returnNum
                }
                emitTabAdd(tab)
              }`)},"x-render-table-column":{title:i18nExpression("outsource.returnNum"),minWidth:130,customRender:!0}},status:{type:"string",title:i18nExpression("outsource.outsourceReturnStatus"),"x-component":"DictSelect","x-component-props":{code:"OS_MATERIAL_RETURN_ORDER_STATUS"},"x-render-table-column":{minWidth:100}},orgName:{type:"string",title:i18nExpression("dataConfMod.orgId"),"x-render-table-column":{minWidth:150}},orgCode:{type:"string","x-hidden":!0},orgId:{type:"string","x-hidden":!0},organizationName:{type:"string",title:i18nExpression("dataConfMod.organizationId"),"x-render-table-column":{minWidth:150}},organizationCode:{type:"string","x-hidden":!0},organizationId:{type:"string","x-hidden":!0},vendorCode:{type:"string",title:i18nExpression("common.vendorCode"),"x-render-table-column":{minWidth:100}},vendorName:{type:"string",title:i18nExpression("common.vendorName"),"x-render-table-column":{minWidth:150}},createdFullName:{type:"string",title:i18nExpression("common.createdFullName"),"x-render-table-column":{minWidth:120}},creationDate:{...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.creationDate, '{y}-{m}-{d}')
              }`)},title:i18nExpression("common.creationDate"),"x-render-table-column":{minWidth:140}},lastUpdateDate:{type:"string","x-hidden":!0,"x-query-engine-sort":"desc"},operation:{type:"void",title:'{{$t("common.operation")}}',"x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList","x-component-props":{max:2},properties:{edit:{type:"void",title:"{{$t('common.edit')}}","x-reactions":changeFieldVisibleByDeps([".status"],'["DRAFT"].includes($deps[0])'),"x-component-props":{...buttonListItemVisibleByPermission("outsourceReturnMaterialsSup:create"),type:"text","@click":expression(`({ row }) => {
                    let returnId = row.returnId
                    let tab = {
                      component: outsourceReturnMaterialsDetail,
                      params: {
                        flag: 'edit',
                        returnId: returnId,
                        tabName: 'outsourceReturnMaterialsDetail' + row.returnNum,
                        row
                      },
                      title: row.returnNum,
                      name: 'outsourceReturnMaterialsDetail' + row.returnNum
                    }
                    emitTabAdd(tab)
                  }`)}},delete:{type:"void",title:"{{$t('common.delete')}}","x-reactions":changeFieldVisibleByDeps([".status"],'["DRAFT"].includes($deps[0])'),"x-component-props":{...buttonListItemVisibleByPermission("outsourceReturnMaterialsSup:create"),popconfirm:{title:i18nExpression("common.confirmDelete")},"@click":expression(`({ row }) => {
                    $queryEngine.request.delete(row.returnId).then(() => {
                      $message.success($t('common.successDelete'))
                      $queryEngine.state.paginationManagement.refresh()
                    })
                  }`)}},reply:{type:"void",title:'{{$t("common.reply")}}',"x-reactions":changeFieldVisibleByDeps([".status"],'["BUYER_REJECT"].includes($deps[0])'),"x-component-props":{...buttonListItemVisibleByPermission("outsourceReturnMaterialsSup:create"),type:"text","@click":expression(`({ row }) => {
                    let returnId = row.returnId
                    let tab = {
                      component: outsourceReturnMaterialsDetail,
                      params: {
                        flag: 'approve',
                        returnId: returnId,
                        row,
                        tabName: 'outsourceReturnMaterialsDetail' + row.returnNum
                      },
                      title: row.returnNum,
                      name: 'outsourceReturnMaterialsDetail' + row.returnNum
                    }
                    emitTabAdd(tab)
                  }`)}}}}})}}}});return{__sfc:!0,emitTabAdd,$t,app,schema,scope:{emitTabAdd,app,i18nExpression,$t,outsourceReturnMaterialsDetail},components:{},RenderEngine}}});var _sfc_render$1=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{schemaKey:"outsourceReturnMaterialsListSup",schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns$1=[],__component__$1=normalizeComponent(_sfc_main$1,_sfc_render$1,_sfc_staticRenderFns$1,!1,null,null,null,null);const OutsourceReturnMaterialsList=__component__$1.exports,_sfc_main={name:"OutsourceMaterialsSup",components:{NavTabs},data(){return{activeTab:"outsourceReturnMaterialsList",tabs:[{title:this.$t("route.outsourceReturnMaterialsSup"),name:"outsourceReturnMaterialsList",component:OutsourceReturnMaterialsList,closable:!1}]}}};var _sfc_render=function(){var _vm=this,_c=_vm._self._c;return _c("NavTabs",{ref:"tabs",attrs:{"tabs-list":_vm.tabs,"cur-tab":_vm.activeTab}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const index=__component__.exports;export{index as default};
