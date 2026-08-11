import{N as NavTabs}from"./index-9a7f2446.js";import{ae as i18nExpression,ad as expression,cf as formGridSegment,af as yearMonthDaySelectorSegment,aD as requiredValidatorSegment,ah as generateXindexInOrder,aj as feedbackLayoutIsPopover,ai as editTableFormItemValid,bD as changeFieldVisibleByDeps,ak as defineComponent,al as usePageHelper,am as useAttrs,aq as defineSchemas,ca as buttonListItemVisibleByPermission,ar as RenderEngine,bN as markRaw,n as normalizeComponent}from"./index-6b6051d8.js";const baseInfo={type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("outsource.returnReqBaseInfo")},"x-read-pretty":expression("$form.readPretty"),properties:{baseInfo:{type:"void",...formGridSegment,properties:{returnId:{type:"string","x-hidden":!0},returnNum:{type:"string",title:i18nExpression("outsource.returnNum"),"x-decorator":"FormItem","x-component-props":{disabled:!0}},status:{type:"string",title:i18nExpression("outsource.outsourceReturnStatus"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{disabled:!0,code:"OS_MATERIAL_RETURN_ORDER_STATUS"}},createdFullName:{type:"string",title:i18nExpression("common.createdFullName"),"x-decorator":"FormItem","x-component-props":{disabled:!0}},creationDate:{...yearMonthDaySelectorSegment,title:i18nExpression("common.creationDate"),"x-decorator":"FormItem","x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],disabled:!0}},orgId:{type:"string",title:i18nExpression("dataConfMod.orgId"),"x-decorator":"FormItem","x-component":"OrganizationSelector","x-component-props":{readPretty:"{{$form.readPretty}}","parent-id":-1,"node-type":"OU",disabled:!0,"@select":expression(`(node) => {
              $values.orgId = node ? String(node.organizationId) : null
              $values.orgCode = node ? String(node.organizationCode) : null
              $values.orgName = node ? node.organizationName : null
              if($form.values.organizationId){
                $form.values.organizationId = null
                $form.values.organizationCode = null
                $form.values.organizationName = null
              }
            }`)},...requiredValidatorSegment},organizationId:{type:"string",title:i18nExpression("dataConfMod.organizationId"),"x-decorator":"FormItem","x-component":"OrganizationSelector","x-component-props":{readPretty:"{{$form.readPretty}}","parent-id":"{{$values.orgId}}","node-type":"INV",disabled:!0,"@select":expression(`(node) => {
              $values.organizationId = node ? String(node.organizationId) : null
              $values.organizationCode = node ? String(node.organizationCode) : null
              $values.organizationName = node ? node.organizationName : null
            }`)},...requiredValidatorSegment},vendorName:{type:"string","x-decorator":"FormItem",title:i18nExpression("common.vendor"),"x-component-props":{disabled:!0}},vendorCode:{type:"string","x-hidden":!0},vendorId:{type:"string","x-hidden":!0},comments:{type:"string","x-decorator":"FormItem","x-decorator-props":{gridSpan:4},title:i18nExpression("contractMod.remark"),"x-component-props":{type:"textarea",maxlength:"500",showWordLimit:!0,disabled:!0,autosize:{minRows:2,maxRows:5}}}}}}},materialsDetails={type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("outsource.returnReqMtDetail")},properties:{detailList:{type:"array","x-component":"RenderTable","x-component-props":{class:"table-view-vxe-table",maxHeight:400,pagination:!1,sortable:!1,editMode:!0,primaryKey:"returnLineId",cascadeDeletion:!0},"x-query-engine-skip":!0,"x-query-engine-relation":"detailList:*",properties:generateXindexInOrder({returnLineId:{type:"string","x-hidden":!0},rowNum:{type:"string",title:i18nExpression("purchaseDemand.lineNum"),"x-read-pretty":!0,"x-render-table-column":{minWidth:80}},returnId:{type:"string","x-hidden":!0},rowStatus:{type:"string","x-hidden":!0,"x-read-pretty":!0,"x-component":"DictSelect","x-component-props":{code:"OrderDetailStatus"},"x-render-table-column":{title:i18nExpression("outsource.returnRowStatus"),minWidth:100}},materialReqNum:{type:"string","x-read-pretty":!0,"x-render-table-column":{title:i18nExpression("outsource.materialReqNum"),minWidth:120}},materialReqRow:{type:"string","x-read-pretty":!0,"x-render-table-column":{title:i18nExpression("outsource.materialReqRow"),minWidth:120}},materialReqDetailId:{type:"string","x-hidden":!0},materialLineId:{type:"string","x-hidden":!0},materialHeadNum:{type:"string","x-read-pretty":!0,"x-render-table-column":{title:i18nExpression("outsource.materialHeadNum"),minWidth:120}},materialRowNum:{type:"string","x-read-pretty":!0,"x-render-table-column":{title:i18nExpression("outsource.materialRowNum"),minWidth:120}},orderDetailId:{type:"string","x-hidden":!0},orderNumber:{type:"string","x-read-pretty":!0,"x-render-table-column":{title:i18nExpression("outsource.orderNumber"),minWidth:120}},orderDetailRow:{type:"number","x-read-pretty":!0,"x-render-table-column":{title:i18nExpression("outsource.requirementHeadNum"),minWidth:100}},materialCode:{type:"string","x-read-pretty":!0,title:i18nExpression("purchaseDemand.itemCode"),"x-render-table-column":{minWidth:100}},materialName:{type:"string","x-read-pretty":!0,title:i18nExpression("purchaseDemand.itemName"),"x-render-table-column":{minWidth:150}},materialUnit:{type:"string","x-read-pretty":!0,"x-render-table-column":{minWidth:100,title:i18nExpression("purchaseDemand.unitCode")}},orderQuantity:{type:"string","x-read-pretty":!0,title:i18nExpression("outsource.orderNum"),"x-render-table-column":{minWidth:100}},baseMaterialId:{type:"string","x-hidden":!0},baseMaterialCode:{type:"string","x-read-pretty":!0,title:i18nExpression("outsource.baseMaterialCode"),"x-render-table-column":{minWidth:100}},baseMaterialName:{type:"string","x-read-pretty":!0,title:i18nExpression("outsource.baseMaterialName"),"x-render-table-column":{minWidth:100}},baseMaterialUnit:{type:"string","x-read-pretty":!0,title:i18nExpression("outsource.baseMaterialUnit"),"x-render-table-column":{minWidth:100}},baseMaterialNum:{type:"string","x-read-pretty":!0,title:i18nExpression("outsource.baseMaterialNum"),"x-render-table-column":{minWidth:100}},receivedQuantity:{type:"number","x-read-pretty":!0,"x-render-table-column":{title:i18nExpression("outsource.returnReceivedQuantity"),minWidth:120}},returnQuantity:{type:"string",title:i18nExpression("outsource.returnQuantity"),"x-read-pretty":!0,"x-render-table-column":{minWidth:100,titlePrefix:{content:i18nExpression("outsource.materialsReturnQuantityTip")}}},thisReturnQuantity:{type:"number","x-render-table-column":{title:i18nExpression("outsource.thisReturnQuantity"),minWidth:120,customRender:!0},"x-component-props":{disabled:!0},...feedbackLayoutIsPopover,"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},returnReason:{type:"string",title:i18nExpression("outsource.rowReturnReason"),"x-render-table-column":{minWidth:130,customRender:!0},"x-component":"DictSelect","x-component-props":{disabled:!0,code:"OS_MATERIAL_RETURN_REASON_TYPE"},...editTableFormItemValid},isUpdateUnreceived:{type:"string",title:i18nExpression("outsource.isUpdateUnreceived"),"x-render-table-column":{minWidth:150,customRender:!0},"x-component":"Checkbox","x-component-props":{disabled:!0,"true-label":"Y","false-label":"N"},...editTableFormItemValid},detailComments:{type:"string",title:i18nExpression("purchaseDemand.comments"),"x-component-props":{maxlength:50,showWordLimit:!0,disabled:!0},"x-render-table-column":{minWidth:150,customRender:!0}}})},reback:{type:"void",...formGridSegment,"x-component-props":{style:"margin-top:20px;"},properties:{rejectReason:{type:"string","x-decorator":"FormItem","x-decorator-props":{gridSpan:24},title:i18nExpression("outsource.replyBuyerRejectReason"),"x-component-props":{type:"textarea",maxlength:"500",showWordLimit:!0,disabled:!1,autosize:{minRows:2,maxRows:5}},"x-reactions":changeFieldVisibleByDeps([".status"],'(["WAITING_BUYER_CONFIRM","VALID"].includes($deps[0]))')},vendorAdditionalExp:{type:"string","x-decorator":"FormItem","x-decorator-props":{gridSpan:24},title:i18nExpression("outsource.vendorAdditionalExp"),"x-component-props":{type:"textarea",maxlength:"500",showWordLimit:!0,disabled:!0,autosize:{minRows:2,maxRows:5}},"x-reactions":changeFieldVisibleByDeps([".status"],'(["BUYER_REJECT","WAITING_BUYER_CONFIRM","VALID"].includes($deps[0]))')}}}}},fileInfo={type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("outsource.materialReqFile")},properties:{toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},"x-reactions":expression(`(field) => {
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
                parseTime(row.creationDate, '{y}-{m}-{d}')
              }`),disabled:!0},"x-render-table-column":{title:i18nExpression("outsource.creationDate")}},operation:{type:"void",title:i18nExpression("common.operation"),"x-render-table-column":{width:120,fixed:"right"},"x-component":"RenderTableButtonList","x-reactions":expression(`(field) => {
            field.visible = !$form.readPretty
          }`),properties:{delete:{type:"void",title:i18nExpression("common.delete"),"x-component-props":{type:"text",disabled:expression('$table.getRowByIndex($self.index).attachId && ![undefined, "", "DRAFT"].includes($form.values.status)'),"@click":expression(`() => {
                    $table.remove($self.index)
                  }
                `)}}}}})}}},_sfc_main$2=defineComponent({__name:"edit",setup(__props){const{emitTabRemove,t:$t,app}=usePageHelper();let $attrs=useAttrs();const $saveFormBill=(type,status,$form,$queryEngine,$confirm,$message,$bus)=>{const{returnMaterialsDailog,...values}=$form.values;type==="saveOrUpdate"?$submitData(type,status,values,$form,$queryEngine,$confirm,$message,$bus):$form.validate("*(!returnMaterialsDailog)").then(()=>{if(type=="buyerReject"&&!values.rejectReason)return $message.warning($t("outsource.fillRejectReason"));$submitData(type,status,values,$form,$queryEngine,$confirm,$message,$bus)}).catch(err=>{})},$submitData=(action,billStatus,$values,$form,$queryEngine,$confirm,$message,$bus)=>{const form=$values;$queryEngine.request.baseRequest({type:"OsMaterialReturn",action,service:"sup-ce",loading:!0,payload:[{...form,status:billStatus}],query:{"*":{}}}).then(res=>{const returnId=res.originalData?.records[0]||"";action=="saveOrUpdate"?($message.success($t("common.successSave")),$form.values.returnId=returnId,$queryEngine.request.read(returnId,{action:"getDetail"})):($message.success($t("common.successSave")),emitTabRemove($attrs.tabName)),$bus.$emit("osgMaterialReturnListBuyer")}).catch(err=>{})},$formEditFlag=($form,$queryEngine)=>{let status=$form.values.handleStatus;return![void 0,"","DRAFT"].includes(status)},questionDetailSchema=defineSchemas({state:{type:"void","x-component":"Fragment","x-hidden":!0,"x-data":{}},OsMaterialReturn:{type:"void","x-component":"FormContainer","x-decorator":"QueryEngine","x-query-engine":{service:"sup-ce",type:"OsMaterialReturn",actions:{getDetail:{immediate:!0,loading:!0,ready:expression(`() => {
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
          }`)},saveOrUpdate:{cascadeDeletion:!0}}},properties:{collapse:{type:"void","x-component":"Collapse","x-component-props":{defaultOpenPanelCount:1},properties:{receiptInfo:{...baseInfo},orderDetail:{...materialsDetails},relevantAttachment:{...fileInfo}}}},items:{type:"void",properties:{buttonList:{type:"void","x-component":"ButtonList",properties:{goBack:{type:"void",title:expression('$t($readOnly ? "common.backTo" : "common.cancel")'),"x-component-props":{messageBox:expression(`$readOnly ? undefined : () => ({
                  type: 'warning',
                  title: $t("common.tips"),
                  message: $t('outsource.goBackConfirm'),
                  showCancelButton: true,
                  beforeClose: (action, dom, done) => {
                    // action的值有cancel confirm
                    if (action === 'confirm') {
                      $bus.$emit('osgMaterialReturnListBuyer')
                      emitTabRemove($attrs.tabName)
                    }
                    done()
                  }
                })`),"@click":expression(`()=> {
                  if($readOnly){
                    emitTabRemove($attrs.tabName)
                    return
                  }
                }`)}},refuse:{type:"void",title:'{{$t("common.toRefuse")}}',"x-component-props":{...buttonListItemVisibleByPermission("outsourceReturnMaterials:refuse"),"@click":expression(`async (values) => {
                  $saveFormBill('buyerReject','BUYER_REJECT', $form, $queryEngine, $confirm, $message, $bus)
                }`)},"x-reactions":changeFieldVisibleByDeps(["status"],`
                  $deps[0] === 'WAITING_BUYER_CONFIRM' && !$form.readPretty
              `)},accept:{type:"void",title:'{{$t("common.accept")}}',"x-component-props":{...buttonListItemVisibleByPermission("outsourceReturnMaterials:accept"),type:"primary","@click":expression(`async (values) => {
                  $saveFormBill('buyerAccept', 'VALID', $form, $queryEngine, $confirm, $message, $bus)
                }`)},"x-reactions":changeFieldVisibleByDeps(["status"],`
                  $deps[0] === 'WAITING_BUYER_CONFIRM' && !$form.readPretty
              `)}}}}}}});return{__sfc:!0,emitTabRemove,$t,app,$attrs,$saveFormBill,$submitData,$formEditFlag,questionDetailSchema,scope:{emitTabRemove,app,$attrs,$markRaw:markRaw,$saveFormBill,$submitData,$formEditFlag},components:{},RenderEngine}}});var _sfc_render$2=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{schemaKey:"outsourceReturnMaterialsDetail",pageAttrs:_setup.$attrs,schema:_setup.questionDetailSchema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns$2=[],__component__$2=normalizeComponent(_sfc_main$2,_sfc_render$2,_sfc_staticRenderFns$2,!1,null,null,null,null);const outsourceReturnMaterialsDetail=__component__$2.exports,_sfc_main$1=defineComponent({__name:"list",setup(__props){const{emitTabAdd,app}=usePageHelper(),schema=defineSchemas({OsMaterialReturn:{type:"void","x-decorator":"QueryEngine","x-query-engine":{service:"sup-ce",actions:{paginationQuery:{immediate:!0,action:"query",method:"paginationQuery",transformRequest:expression(`(data, headers) => {
            // 添加过滤条件 去除拟定条件
            if (!data.payload?.filter?.status) {
              data.payload.filter = {
                status: { ne: 'DRAFT'},
                ...data.payload.filter
              }
            }
            return data
          }`)}}},"x-component":"el-container","x-component-props":{class:"flex-container the_contractTemplateList_wrapper",direction:"vertical"},properties:{bus:{type:"void","x-component":"BusEvent","x-component-props":{eventName:"osgMaterialReturnListBuyer","@listener":expression(`() => {
            $queryEngine.state.paginationManagement.refresh()
          }`)}},query:{type:"object","x-query-engine-skip":!0,"x-component":"QueryFormByQueryEngine",properties:generateXindexInOrder({returnNum:{type:"string",title:i18nExpression("outsource.returnNum")},status:{type:"string",title:i18nExpression("outsource.outsourceReturnStatus"),"x-component":"DictSelect","x-component-props":{filterItem:["DRAFT"],code:"OS_MATERIAL_RETURN_ORDER_STATUS"}},orgId:{type:"string",title:i18nExpression("dataConfMod.orgId"),"x-component":"OrganizationSelector","x-component-props":{"node-type":"OU","parent-id":-1,placeholder:i18nExpression("common.pleaseSelect")}},organizationId:{type:"string",title:i18nExpression("dataConfMod.organizationId"),"x-component":"OrganizationSelector","x-component-props":{"node-type":"INV",placeholder:i18nExpression("common.pleaseSelect"),"parent-id":expression("$form.values.query.orgId || -1")}},vendorName:{type:"string",title:i18nExpression("common.vendorName"),"x-component":"QuickSearchWrapper","x-component-props":{showKey:"companyName",propKey:"companyName",name:"scc_sup_company_info_all"}},creationDate:{title:i18nExpression("common.createdFullName"),"x-query-engine-query-operator":"between",type:"date","x-component-props":{placeholder:i18nExpression("common.pleaseSelectDate"),"value-format":"yyyy-MM-dd",type:"datetimerange",format:"yyyy-MM-dd HH:mm:ss","default-time":["00:00:00","23:59:59"]}},createdFullName:{type:"string",title:i18nExpression("common.createdFullName"),"x-query-engine-query-operator":"contains"}})},toolbar:{type:"void","x-component":"ButtonList","x-component-props":{style:"margin-bottom: 16px;height:28px;"},properties:{}},table:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",class:"table-view-vxe-table",openCustomTable:!0},properties:generateXindexInOrder({returnId:{type:"string","x-hidden":!0},returnNum:{"x-component":"TableButton","x-component-props":{type:"text","@click":expression(`({row}) => {
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
                }`)},title:i18nExpression("common.creationDate"),"x-render-table-column":{minWidth:140}},lastUpdateDate:{type:"string","x-hidden":!0,"x-query-engine-sort":"desc"},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList","x-component-props":{max:2},properties:{reply:{type:"void",title:'{{$t("common.reply")}}',"x-reactions":changeFieldVisibleByDeps([".status"],'["WAITING_BUYER_CONFIRM"].includes($deps[0])'),"x-component-props":{...buttonListItemVisibleByPermission("outsourceReturnMaterials:reply"),type:"text","@click":expression(`({ row }) => {
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
                  }`)}}}}})}}}});return{__sfc:!0,emitTabAdd,app,schema,scope:{emitTabAdd,app,i18nExpression,outsourceReturnMaterialsDetail},components:{},RenderEngine}}});var _sfc_render$1=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{schemaKey:"outsourceReturnMaterialsList",schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns$1=[],__component__$1=normalizeComponent(_sfc_main$1,_sfc_render$1,_sfc_staticRenderFns$1,!1,null,null,null,null);const OutsourceReturnMaterialsList=__component__$1.exports,_sfc_main={name:"OutsourceMaterials",components:{NavTabs},data(){return{activeTab:"OutsourceReturnMaterialsList",tabs:[{title:()=>this.$t("route.outsourceReturnMaterials"),name:"OutsourceReturnMaterialsList",component:OutsourceReturnMaterialsList,closable:!1}]}}};var _sfc_render=function(){var _vm=this,_c=_vm._self._c;return _c("NavTabs",{ref:"tabs",attrs:{"tabs-list":_vm.tabs,"cur-tab":_vm.activeTab}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const index=__component__.exports;export{index as default};
