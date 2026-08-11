import{N as NavTabs}from"./index-9a7f2446.js";import{ak as defineComponent,al as usePageHelper,am as useAttrs,aq as defineSchemas,ae as i18nExpression,ad as expression,ah as generateXindexInOrder,af as yearMonthDaySelectorSegment,ar as RenderEngine,n as normalizeComponent,cf as formGridSegment,ab as parseTime,cC as transformDetailQuery,cD as transformDetailDetailListItem,cG as transformQuery}from"./index-6b6051d8.js";import{s as setWarningTip,a as setRepeatData}from"./util-d962b17f.js";const _sfc_main$3=defineComponent({__name:"selectDeliveryDialog",props:{id:{type:Array,default:()=>[]},visible:{type:Boolean,default:!1},form:{type:Object,default:()=>{}}},emits:["close"],setup(__props,{emit:emits}){const $defineProps=__props,{t:$t,getCurrentUserInfo}=usePageHelper(),{companyId}=getCurrentUserInfo(),$attrs=useAttrs(),getSearchParams=params=>{let keys=[],newParams={};for(let[key,value]of Object.entries(params))value&&Object.values(value)[0]&&keys.push(key);for(let k in params)keys.includes(k)&&(newParams[k]=params[k]);return newParams},$searchParentOrg=async($form,$queryEngine,pageNum,pageSize)=>{let params={deliveryNumber:{eq:$form.values.searchInner.deliveryNumber},deliveryDate:{between:$form.values.searchInner.deliveryDate}};const newParams=getSearchParams(params),res=await $queryEngine.request.baseRequest({type:"DeliveryNoteVendor",action:"query",lang:"zh-cn",payload:{page:{pageNum:pageNum||$form.query("DeliveryNoteVendor").get("data").pageInfo.pageNum,pageSize:pageSize||$form.query("DeliveryNoteVendor").get("data").pageInfo.pageSize,sort:"lastUpdateDate desc"},filter:{...$form.query("DeliveryNoteVendor").get("data").filter,...newParams}},query:{"*":{}}});$form.values.tableList=res.data,Object.assign($form.query("DeliveryNoteVendor").get("data").pageInfo,{pageNum:res.originalData.payload.pageNum,pageSize:res.originalData.payload.pageSize,total:res.originalData.payload.total})},scope={emits,$attrs,$t,$defineProps,$searchParentOrg,companyId},components={},schema=defineSchemas({DeliveryNoteVendor:{type:"void",title:i18nExpression("orderMod.selDeliveryNote"),"x-component":"RDialog","x-decorator":"QueryEngine","x-query-engine":{service:"sup-ce",actions:{paginationQuery:{immediate:!0,ready:expression(`async () => {
            let promise = await new Promise(res => {
              setTimeout(() => {
                $form.query('DeliveryNoteVendor').get('data').filter = {
                  vendorId: { eq: companyId },
                  deliveryNoteStatus: { eq: 'DELIVERED' },
                  ifCreateDeliveryAppointment: { eq: 'N' },
                  orgId: { eq: $defineProps.form.orgId },
                  organizationId: { eq: $defineProps.form.organizationId }
                }

                res(true)
              })
            })
            return promise
          }`),transformRequest:expression(`(data, headers) => {
            data.payload = {
              page: {
                pageNum: 1,
                pageSize: 15,
                sort: "lastUpdateDate desc"
              },
              filter: $form.query('DeliveryNoteVendor').get('data').filter
            }

            data.query['*'] = {}

            return data
          }`),onSuccess:expression(`(res) => {
            console.log('paginationQuery onSuccess=>', res.data)
            $form.values.tableList = res.data
            Object.assign($form.query('DeliveryNoteVendor').get('data').pageInfo, {
              total: res.originalData.payload.total
            })
          }`)}}},"x-reactions":expression(`(field) => {
      setTimeout(() => {
        field.setComponentProps({
          visible: $defineProps.visible
        })
      },500)
    }`),"x-component-props":{class:"dialogMain",size:"large",appendToBody:!0,closeOnClickModal:!1,okButtonText:i18nExpression("common.submit"),beforeClose:expression(`(done, type) => {
        if (type === 'ok') {
          const field = $form.query('.tableList').take()
          const selections = field.componentProps.componentInstance.getCheckboxRecords()
          if (selections.length < 1) {
            return $message.warning($t('common.pleaseSelectMinOne'))
          }

          emits('confirm', selections)
        }
        emits('close')
        done()
      }`),"@opened":expression(`() => {
        if ($defineProps.visible) {
          // $searchParentOrg($form, $queryEngine)
        }
      }`)},"x-data":{pageInfo:{pageNum:1,pageSize:15,total:0,pageSizes:[15,30,60,120,300,600,1e3,1500]},filter:{}},properties:{formEngine:{type:"void",properties:{searchInner:{type:"object","x-decorator":"FormLayout","x-decorator-props":{layout:"horizontal"},"x-component":"FormGrid","x-component-props":{minColumns:1,columnGap:10,rowGap:0},properties:generateXindexInOrder({deliveryNumber:{type:"string",title:i18nExpression("orderMod.buyerOrderSynergy.deliveryNumber"),"x-decorator":"FormItem","x-decorator-props":{gridSpan:1}},deliveryDate:{type:"date","x-decorator":"FormItem",title:i18nExpression("purchaseDemand.applyDate"),"x-component-props":{type:"daterange",format:"yyyy-MM-dd","value-format":"yyyy-MM-dd"},"x-query-engine-query-operator":"between"},searchGroup:{type:"void","x-component":"div","x-decorator":"FormItem","x-query-engine-skip":!0,"x-component-props":{style:"display: flex; justify-content: flex-end;"},properties:{searchBtn:{type:"void","x-component":"RButton","x-content":i18nExpression("common.search"),"x-decorator":"FormItem","x-query-engine-skip":!0,"x-component-props":{type:"primary",style:"margin-right: 8px;","@click":expression(`() => {
                        console.log($form,'form')
                        $searchParentOrg($form, $queryEngine)
                      }`)}},resetBtn:{type:"void","x-component":"RButton","x-content":i18nExpression("common.reset"),"x-decorator":"FormItem","x-query-engine-skip":!0,"x-component-props":{type:"default","@click":expression(`() => {
                        $form.values.searchInner = {}
                        $searchParentOrg($form, $queryEngine)
                      }`)}}}}})},tableList:{type:"array","x-component":"NormalRenderTable","x-component-props":{height:300,preColumns:"checkbox, seq",class:"table-view-vxe-table",openCustomTable:!1,sortable:!1,editMode:!1,pagination:expression("$form.query('DeliveryNoteVendor').get('data').pageInfo"),"@pageChange":expression(`(currentPage) => {
                console.log('pageChange')
                $searchParentOrg($form, $queryEngine, currentPage)
              }`),"@pageSizeChange":expression(`(pageSize) => {
                console.log('pageSizeChange')
                $searchParentOrg($form, $queryEngine, null, pageSize)
              }`)},properties:generateXindexInOrder({deliveryNumber:{type:"string","x-render-table-column":{title:i18nExpression("orderMod.buyerOrderSynergy.deliveryNumber"),minWidth:100}},deliveryDate:{...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                    parseTime(row.deliveryDate, '{y}-{m}-{d}')
                  }`)},"x-render-table-column":{title:i18nExpression("orderMod.buyerOrderSynergy.entryTime"),minWidth:100}},comments:{type:"string","x-render-table-column":{title:i18nExpression("common.remark"),minWidth:100}}})}}}}}});return{__sfc:!0,$t,getCurrentUserInfo,emits,companyId,$attrs,$defineProps,getSearchParams,$searchParentOrg,scope,components,schema,RenderEngine}}});var _sfc_render$3=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{schemaKey:"DeliveryAppointSupplierDialog",pageAttrs:_setup.$attrs,schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns$3=[],__component__$3=normalizeComponent(_sfc_main$3,_sfc_render$3,_sfc_staticRenderFns$3,!1,null,null,null,null);const SelectDeliveryDialog=__component__$3.exports,_sfc_main$2=defineComponent({__name:"edit-engine",setup(__props){const{emitTabRemove,t:$t,http,app,getCurrentUserInfo}=usePageHelper(),$attrs=useAttrs(),$userInfo=getCurrentUserInfo(),isReadOnly=(()=>$attrs.params.flag==="view")(),editPage=(()=>$attrs.params.flag==="add"||$attrs.params.flag==="edit")(),$closeTabDetail=$bus=>{$bus.$emit("DeliveryAppointVendor"),emitTabRemove($attrs.tabName)},$remoteMethod=($self,licensePlate)=>{const params={pageNum:1,pageSize:15,status:"EFFECTIVE"};licensePlate&&(params.licensePlate=licensePlate),http({url:"/api-sup-ce/order/carInfo/listPage",method:"POST",data:params}).then(res=>{$self.setDataSource(res.data.list.map(i=>({id:i.carInfoId,value:i.licensePlate,label:i.licensePlate,type:i.carType})))})},$selectlicensePlate=(licensePlate,$self,$values)=>{const item=$self.dataSource.find(i=>i.value===licensePlate);item&&($values.carType=item.type)},$disabledDate=time=>{const today=new Date;return today.setHours(0),today.setMinutes(0),today.setSeconds(0),today.setMilliseconds(0),time.getTime()<today.getTime()},handleSave=async($form,$queryEngine)=>{$form.values.deliveryAppointStatus="DRAFT";const{data}=await $queryEngine.request.baseRequest({action:"saveOrUpdate",lang:"zh-cn",query:{"*":{}},payload:[$form.values]});$queryEngine.request.read([data[0].deliveryAppointId]),app.$message.success($t("common.successSave"))},handleSubmit=($form,$queryEngine,$bus)=>{$form.validate().then(async()=>{if($form.values.appointDeliveryNotes.length<1)return app.$message.warning($t("deliveryAppointment.prompt1"));if($form.values.deliveryAppointVisitors.length<1)return app.$message.warning($t("deliveryAppointment.prompt2"));$form.values.deliveryAppointStatus="WAITING_CONFIRM",await $queryEngine.request.baseRequest({action:"saveOrUpdate",lang:"zh-cn",query:{"*":{}},payload:[$form.values]}),app.$message.success($t("common.success")),$closeTabDetail($bus)}).catch(err=>{setWarningTip(err)})},$solveHandler=(type,$form,$queryEngine,$bus)=>{type==="SAVE"&&handleSave($form,$queryEngine),type==="SUBMIT"&&handleSubmit($form,$queryEngine,$bus)},addAppointDelivery=$form=>{if(!$form.values.orgId&&!$form.values.organizationId)return app.$message.warning($t("purchaseDemand.openDialogWarning1"));$form.query(".SelectDeliveryDialog").take(field=>{field.visible=!0,field.setComponentProps({id:$form.values.appointDeliveryNotes.map(item=>item.deliveryAppointId),visible:!0,form:$form.values})})},scope={$t,$attrs,$userInfo,emitTabRemove,$closeTabDetail,$remoteMethod,$selectlicensePlate,$disabledDate,isReadOnly,editPage,$solveHandler,setRepeatData,addAppointDelivery,$transformDetailQuery:transformDetailQuery,$transformDetailDetailListItem:transformDetailDetailListItem},components={SelectDeliveryDialog},schema=defineSchemas({DeliveryAppointVendor:{type:"void","x-component":"FormContainer","x-decorator":"QueryEngine","x-decorator-props":{onMounted:`{{() => {
        const licensePlateSelf = $form.query('licensePlate').take()
        $remoteMethod(licensePlateSelf)
      }}}`},"x-query-engine":{service:"sup-ce",actions:{read:{immediate:!0,ready:expression(`() => {
            const { companyId, companyName, companyCode } = $userInfo
            Object.assign($form.values, {
              vendorName: companyName,
              vendorCode: companyCode,
              vendorId: companyId
            })
            return !!$attrs?.params?.row?.deliveryAppointId
          }`),transformRequest:expression(`(data, headers) => {
            data.payload = [$attrs.params?.row?.deliveryAppointId || $form.values.deliveryAppointId || '']

            data.query['*'] = {}
            data.query.appointDeliveryNotes = {
              deliveryNoteId: {}
            }
            data.query = $transformDetailQuery(data.query, ['appointDeliveryNotes.deliveryNoteId'])

            return data
          }`),onSuccess:expression(`(res) => {
            $form.readPretty = isReadOnly
            let { appointDeliveryNotes } = res.data[0]
            if(res.originalData.ref?.DeliveryNoteVendor && appointDeliveryNotes.length){
              appointDeliveryNotes.forEach((item, index) =>{
                const {appointDeliveryNoteItem,deliveryNoteItem} = $transformDetailDetailListItem(item, res.originalData.ref,['AppointDeliveryNoteVendor.DeliveryNoteVendor'],'Vendor')
           
                appointDeliveryNotes.splice(index,1,{ ...appointDeliveryNoteItem,...deliveryNoteItem})
              })
            }
            $form.setValues({
              ...res.data[0]
            })
           
          }`)},saveOrUpdate:{loading:!0,cascadeDeletion:!0,transformRequest:expression(`(data, headers) => {
            console.log('save=>', data, headers)
            return data
          }`),onSuccess:expression(`(res) => {
            $form.readPretty = isReadOnly
            let { appointDeliveryNotes } = res.data[0]
            if(res.originalData.ref?.DeliveryNoteVendor && appointDeliveryNotes.length){
              appointDeliveryNotes.forEach((item, index) =>{
                const {appointDeliveryNoteItem,deliveryNoteItem} = $transformDetailDetailListItem(item, res.originalData.ref,['AppointDeliveryNoteVendor.DeliveryNoteVendor'],'Vendor')
           
                appointDeliveryNotes.splice(index,1,{ ...appointDeliveryNoteItem,...deliveryNoteItem})
              })
            }
           
            $form.setValues({
              ...res.data[0]
            })
          }`)}}},items:{type:"void",properties:{cancel:{type:"void","x-content":'{{ editPage ? $t("common.cancel") : $t("common.close") }}',"x-component":"Button","x-component-props":{type:"default","@click":expression(`() => {
              $closeTabDetail($bus)
            }`)}},save:{type:"void","x-visible":"{{editPage}}","x-content":i18nExpression("common.staging"),"x-component":"RButton","x-component-props":{type:"primary","@click":expression(`() => {
              $solveHandler('SAVE', $form, $queryEngine, $bus)
            }`)}},submit:{type:"void","x-visible":"{{editPage}}","x-content":i18nExpression("common.submit"),"x-component":"RButton","x-component-props":{type:"primary","@click":expression(`() => {
              $solveHandler('SUBMIT', $form, $queryEngine, $bus)
            }`)}}}},properties:{SelectDeliveryDialog:{type:"void","x-visible":!1,"x-query-engine-skip":!0,"x-component":"SelectDeliveryDialog","x-component-props":{"@close":expression(`(field) => {
            $self.visible = false
            $self.setComponentProps({
              visible: false
            })
          }`),"@confirm":expression(`(selection) => {
            setRepeatData($form.values.appointDeliveryNotes, selection, 'deliveryNoteId')
          }`)}},collapse:{type:"void","x-component":"Collapse",properties:generateXindexInOrder({deliveryAppointForm:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("supRisk.baseInfo")},"x-query-engine-skip":!0,properties:{layout:{type:"void",...formGridSegment,properties:{vendorName:{type:"string",title:i18nExpression("orderMod.buyerOrderSynergy.vendorName"),"x-decorator":"FormItem","x-component-props":{disabled:!0}},orgId:{type:"string",title:i18nExpression("purchaseDemand.businessEntity"),"x-decorator":"FormItem","x-component":"OrganizationSelector","x-component-props":{readPretty:"{{$form.readPretty}}","parent-id":-1,"node-type":"OU","@select":expression(`(node) => {
                        $values.orgId = node ? String(node.organizationId) : null
                        $values.orgCode = node ? String(node.organizationCode) : null
                        $values.orgName = node ? node.organizationName : null

                        if (!$values.organizationId) return
                        $values.organizationId = null
                        $values.organizationName = null
                        $values.organizationCode = null
                      }`)},"x-validator":{required:!0,message:i18nExpression("purchaseDemand.orgIdTips")}},organizationId:{type:"string",title:i18nExpression("purchaseDemand.invOrg"),"x-decorator":"FormItem","x-component":"OrganizationSelector","x-component-props":{readPretty:"{{$form.readPretty}}","parent-id":"{{$values.orgId}}","node-type":"INV","@select":expression(`(node) => {
                        $values.organizationId = node ? String(node.organizationId) : null
                        $values.organizationCode = node ? String(node.organizationCode) : null
                        $values.organizationName = node ? node.organizationName : null

                        if (!node) {
                          $values.receiveContact = ''
                          $values.receiveTelephone = ''
                          $values.receiveAddress = ''
                        }
                      }`)},"x-validator":{required:!0,message:i18nExpression("purchaseDemand.organizationIdTips")}},receiveAddress:{type:"string",title:i18nExpression("oneStopShopping.receiveAddress"),"x-component":"DictSelect","x-decorator":"FormItem","x-component-props":{code:"{{String($values.organizationId)}}","custom-select-type":"{{$values.organizationId ? 'RECEIVE_ADDRESS' : ''}}","@change-value":expression(`(val, {element}) => {
                        $values.receiveContact = element ? element.receiver : ''
                        $values.receiveTelephone = element ? element.receiverPhone : ''
                        $values.receiveAddress = element ? element.siteName : ''
                      }`)}},respondents:{type:"string","x-decorator":"FormItem",title:i18nExpression("orderMod.buyerOrderSynergy.respondents"),"x-component":"QuickSearchWrapper","x-component-props":{readPretty:"{{$form.readPretty}}",propKey:"nickname",showKey:"nickname",showInput:"{{$values.respondents}}",name:"scc_rbac_user_display","@close-quicksearch":expression(`(val) => {
                        $form.values.respondents = val ? val.nickname : ''
                        $form.values.respondentsNo = val ? val.username : ''
                        $form.values.respondentsPhone = val ? val.phone : ''
                        $form.values.respondentsGound = val ? val.department : ''
                        $form.values.respondentsGoundNumber = val ? val.ceeaDeptid : ''
                      }`)},"x-validator":{required:!0,message:i18nExpression("orderMod.msgOrder[27]")}},respondentsNo:{type:"string",title:i18nExpression("orderMod.buyerOrderSynergy.respondentsNo"),"x-decorator":"FormItem","x-component-props":{disabled:!0}},respondentsPhone:{type:"string",title:i18nExpression("orderMod.buyerOrderSynergy.respondentsPhone"),"x-decorator":"FormItem","x-component-props":{disabled:!0}},respondentsGound:{type:"string",title:i18nExpression("orderMod.buyerOrderSynergy.respondentsGound"),"x-decorator":"FormItem","x-component-props":{disabled:!0}},carType:{type:"string",title:i18nExpression("orderMod.buyerOrderSynergy.carType"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"CAR_TYPE"},"x-validator":{required:!0,message:i18nExpression("orderMod.msgOrder[28]")}},licensePlate:{type:"string",title:i18nExpression("orderMod.buyerOrderSynergy.licensePlate"),"x-decorator":"FormItem","x-component":"Select","x-component-props":{filterable:!0,remote:!0,clearable:!0,"automatic-dropdown":!0,"remote-method":"{{(licensePlate) => $remoteMethod($self, licensePlate)}}","@change":"{{(licensePlate) => $selectlicensePlate(licensePlate, $self, $values)}}"},"x-validator":{required:!0,message:i18nExpression("orderMod.msgOrder[29]")}},entryTime:{...yearMonthDaySelectorSegment,default:parseTime(new Date,"{y}-{m}-{d}",!0),"x-decorator":"FormItem",title:i18nExpression("orderMod.buyerOrderSynergy.entryTime"),"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],"value-format":"yyyy-MM-dd","picker-options":expression(`{
                        disabledDate: $disabledDate
                      }`)},"x-validator":{required:!0,message:i18nExpression("orderMod.msgOrder[30]")}},deliveryLocation:{type:"string",title:i18nExpression("orderMod.deliveryLocation"),"x-decorator":"FormItem","x-validator":{required:!0,message:i18nExpression("orderMod.msgOrder[31]")}},deliveryAppointStatus:{type:"string",default:"DRAFT","x-hidden":'{{$attrs.params.flag === "add"}}',title:i18nExpression("common.status"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"DELIVERY_APPOINT_STATUS",disabled:!0}},createdFullName:{type:"string","x-visible":'{{$attrs.params.flag !== "add"}}',title:i18nExpression("common.creator"),"x-decorator":"FormItem","x-component-props":{disabled:!0}},creationDate:{...yearMonthDaySelectorSegment,"x-decorator":"FormItem","x-visible":'{{$attrs.params.flag !== "add"}}',title:i18nExpression("orderMod.buyerOrderSynergy.creationDate"),"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],"value-format":"yyyy-MM-dd",disabled:!0}},lastUpdatedFullName:{type:"string","x-visible":'{{$attrs.params.flag !== "add"}}',title:i18nExpression("orderMod.buyerOrderSynergy.lastUpdateBy"),"x-decorator":"FormItem","x-component-props":{disabled:!0}},lastUpdateDate:{...yearMonthDaySelectorSegment,"x-decorator":"FormItem","x-visible":'{{$attrs.params.flag !== "add"}}',title:i18nExpression("orderMod.buyerOrderSynergy.lastUpdateDate"),"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],"value-format":"yyyy-MM-dd",disabled:!0}},comments:{type:"string","x-decorator":"FormItem","x-decorator-props":{gridSpan:4},title:i18nExpression("contractMod.remark"),"x-component-props":{type:"textarea",maxlength:"500",showWordLimit:!0,autosize:{minRows:2,maxRows:5}}}}}}},deliveryAppointDetail:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("orderMod.buyerOrderSynergy.appointDeliveryNotesList")},properties:{toolbar:{type:"void","x-component":"Space","x-reactions":expression(`(field) => {
                  field.visible = !$form.readPretty
                }`),properties:{add:{type:"void","x-component":"RButton",title:i18nExpression("common.add"),"x-component-props":{type:"primary",style:"margin-bottom: 10px;",disabled:"{{isReadOnly}}","@click":expression(`() => {
                        addAppointDelivery($form)
                      }`)}}}},appointDeliveryNotes:{type:"array","x-component":"RenderTable","x-component-props":{class:"table-view-vxe-table",preColumns:"seq",editMode:!1,pagination:!1,sortable:!1,primaryKey:"appointDeliveryNoteId",cascadeDeletion:!0},"x-query-engine-skip":!0,properties:generateXindexInOrder({appointDeliveryNoteId:{type:"string","x-hidden":!0},deliveryNumber:{type:"string","x-decorator":"FormItem","x-render-table-column":{title:i18nExpression("orderMod.buyerOrderSynergy.deliveryNumber"),minWidth:120}},deliveryDate:{...yearMonthDaySelectorSegment,"x-decorator":"FormItem","x-render-table-column":{title:i18nExpression("orderMod.buyerOrderSynergy.deliveryDate2"),minWidth:160},"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],"value-format":"yyyy-MM-dd"}},comments:{type:"string","x-decorator":"FormItem","x-render-table-column":{title:i18nExpression("orderMod.buyerOrderSynergy.comments"),minWidth:120}},operation:{type:"void",title:i18nExpression("common.operation"),"x-render-table-column":{width:60,fixed:"right"},"x-component":"RenderTableButtonList","x-reactions":expression(`(field) => {
                      field.visible = !$form.readPretty
                    }`),properties:{delete:{type:"void",title:i18nExpression("common.delete"),"x-component-props":{type:"text","@click":expression(`
                            ({ rowIndex }) => {
                              $table.remove(rowIndex)
                            }
                          `)}}}}})}}},visitorsDetail:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("orderMod.buyerOrderSynergy.visitorsList")},properties:{visitorsToolbar:{type:"void","x-component":"Space","x-visible":"{{!$form.readPretty}}",properties:{addVisitors:{type:"void","x-component":"RButton",title:i18nExpression("common.add"),"x-component-props":{type:"primary",style:"margin-bottom: 10px;",disabled:"{{isReadOnly}}","@click":expression(`() => {
                        $self.query('.deliveryAppointVisitors').take().componentProps.componentInstance.addRow('unshift')
                      }`)}}}},deliveryAppointVisitors:{type:"array","x-component":"RenderTable","x-component-props":{class:"table-view-vxe-table",editMode:"{{!isReadOnly}}",preColumns:"seq",pagination:!1,sortable:!1,primaryKey:"deliveryAppointVisitorId",cascadeDeletion:!0},"x-query-engine-skip":!0,"x-query-engine-relation":"deliveryAppointVisitors:*",properties:generateXindexInOrder({deliveryAppointVisitorId:{type:"string","x-hidden":!0},visitorName:{type:"string","x-render-table-column":{title:i18nExpression("orderMod.buyerOrderSynergy.visitorName"),minWidth:120},"x-decorator":"FormItem","x-decorator-props":{feedbackLayout:"popover"},"x-validator":{required:!0,message:i18nExpression("vendorMod.msgNickname")}},idType:{type:"string","x-component":"DictSelect","x-component-props":{code:"ID_TYPE"},"x-render-table-column":{title:i18nExpression("orderMod.buyerOrderSynergy.idType"),minWidth:120},"x-decorator":"FormItem","x-decorator-props":{feedbackLayout:"popover"},"x-validator":{required:!0,message:i18nExpression("orderMod.msgOrder[23]")}},idNo:{type:"string","x-render-table-column":{title:i18nExpression("orderMod.buyerOrderSynergy.idNo"),minWidth:120},"x-decorator":"FormItem","x-decorator-props":{feedbackLayout:"popover"},"x-validator":{required:!0,message:i18nExpression("orderMod.msgOrder[24]")}},linkPhone:{type:"string","x-render-table-column":{title:i18nExpression("orderMod.buyerOrderSynergy.linkPhone"),minWidth:120},"x-decorator":"FormItem","x-decorator-props":{feedbackLayout:"popover"},"x-validator":{required:!0,message:i18nExpression("orderMod.msgOrder[22]")}},comments:{type:"string",title:i18nExpression("orderMod.buyerOrderSynergy.comments"),"x-render-table-column":{minWidth:120}},operation:{type:"void",title:i18nExpression("common.operation"),"x-render-table-column":{width:60,fixed:"right"},"x-component":"RenderTableButtonList","x-reactions":expression(`(field) => {
                        field.visible = !$form.readPretty
                    }`),properties:{delete:{type:"void",title:i18nExpression("common.delete"),"x-component-props":{type:"text","@click":expression(`
                            ({ rowIndex }) => {
                                $table.remove(rowIndex)
                            }
                          `)}}}}})}}}})}}}});return{__sfc:!0,emitTabRemove,$t,http,app,getCurrentUserInfo,$attrs,$userInfo,isReadOnly,editPage,$closeTabDetail,$remoteMethod,$selectlicensePlate,$disabledDate,handleSave,handleSubmit,$solveHandler,addAppointDelivery,scope,components,schema,RenderEngine}}});var _sfc_render$2=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{schemaKey:"DeliveryAppointSupplierDetail",pageAttrs:_setup.$attrs,schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns$2=[],__component__$2=normalizeComponent(_sfc_main$2,_sfc_render$2,_sfc_staticRenderFns$2,!1,null,null,null,null);const editEngine=__component__$2.exports,_sfc_main$1=defineComponent({__name:"list-engine",setup(__props){const{emitTabAdd,t,app,confirmMessage}=usePageHelper(),getSubmitOrDeleteParams=async(list,isSubmit)=>{let message="",filters=[];return list.length<1&&(message=app.$message.warning(t("common.pleaseSelectMinOne"))),isSubmit?(filters=list.filter(item=>!["DRAFT","REJECT"].includes(item.deliveryAppointStatus)),filters.length&&(message=t("purchaseDemand.have")+filters.length+t("orderMod.msgVendorOrder[12]"))):(filters=list.filter(item=>item.deliveryAppointStatus!="DRAFT"),filters.length&&(message=t("purchaseDemand.have")+filters.length+t("orderMod.msgVendorOrder[13]"))),message?(app.$message.warning(message),[]):!isSubmit&&await confirmMessage(t("common.confirmDelete"))!=="confirm"?[]:list.map(row=>({deliveryAppointId:row.deliveryAppointId}))},handleSubmit=async($form,$queryEngine,type,row)=>{const selections=$form.query(".table").take().componentProps.componentInstance.getCheckboxRecords(),ids=await getSubmitOrDeleteParams(type==="mutil"?selections:[row],!0);ids.length&&(await $queryEngine.request.baseRequest({type:"DeliveryAppointVendor",action:"submit",payload:ids,query:{"*":{}}}),app.$message.success(t("common.successSubmit")),$queryEngine.state.paginationManagement.refresh())},handleDelete=async($form,$queryEngine,type,row)=>{const selections=$form.query(".table").take().componentProps.componentInstance.getCheckboxRecords(),ids=await getSubmitOrDeleteParams(type==="mutil"?selections:[row],!1);ids.length&&(await $queryEngine.request.delete(ids),app.$message.success(t("common.successDelete")),$queryEngine.state.paginationManagement.refresh())},openDetailTag=(type,row)=>{const mapInfo=new Map([["view",{component:editEngine,params:{flag:"view",row},title:t("orderMod.deliveryAppointmentReceipt")+row?.deliveryAppointNumber,name:"deliveryAppointmentDetail_supplier"+row?.deliveryAppointId}],["add",{component:editEngine,params:{flag:"add"},title:t("orderMod.addAppointmentDeliveryNote2"),name:"deliveryAppointmentDetail_supplier"}],["edit",{component:editEngine,params:{flag:"edit",row},title:t("orderMod.deliveryAppointmentReceipt")+row?.deliveryAppointNumber,name:"deliveryAppointmentDetail_supplier"+row?.deliveryAppointId}]]);emitTabAdd(mapInfo.get(type))},scope={handleSubmit,handleDelete,openDetailTag,transformQuery},components={},schema=defineSchemas({DeliveryAppointVendor:{type:"void","x-component":"QueryEngine","x-decorator":"el-container","x-decorator-props":{class:"flex-container",direction:"vertical"},"x-query-engine":{service:"sup-ce",actions:{paginationQuery:{immediate:!0,transformRequest:expression(`(data) => {
            console.log('transformRequest=>', data)
            
            if ($values.query.deliveryNumber) {
              data.query = transformQuery(data.query,['appointDeliveryNotes.deliveryNoteId'])  
            }
            data.query['*'] = {}
            return data
          }`)},delete:{loading:!0,cascadeDeletion:!0,transformRequest:expression(`(data, headers) => {
            console.log('delete=>', data, headers)
            data.query = {
              '*': {}
            }

            return data
          }`)}}},properties:{bus:{type:"void","x-component":"BusEvent","x-component-props":{eventName:"DeliveryAppointVendor","@listener":expression(`() => {
            $queryEngine.state.paginationManagement.refresh()
          }`)}},query:{type:"object","x-query-engine-skip":!0,"x-component":"QueryFormByQueryEngine","x-decorator":"FormItem","x-validator":[],"x-component-props":{minWidth:100,minColumns:0,maxColumns:3,columnGap:10,rowGap:5,colWrap:!0,labelWidth:80,immediateQueryForm:!1,style:{opacity:1}},properties:{orgId:{type:"string",title:i18nExpression("dataConfMod.orgId"),"x-component":"OrganizationSelector","x-component-props":{"node-type":"OU","parent-id":-1}},organizationId:{type:"string",title:i18nExpression("dataConfMod.organizationId"),"x-component":"OrganizationSelector","x-component-props":{"parent-id":"{{$form.values.query.orgId}}","node-type":"INV",scope:"{{ $form.values.query }}"}},deliveryAppointNumber:{type:"string",title:i18nExpression("orderMod.buyerOrderSynergy.deliveryAppointNumber"),"x-decorator":"FormItem"},deliveryNumber:{type:"string",title:i18nExpression("orderMod.buyerOrderSynergy.deliveryNumber"),"x-decorator":"FormItem","x-query-engine-query-operator":"contains","x-query-engine-relation":"appointDeliveryNotes.deliveryNoteId:*"},deliveryAppointStatus:{type:"string",title:i18nExpression("common.status"),"x-component":"DictSelect","x-component-props":{code:"DELIVERY_APPOINT_STATUS"}}}},toolbar:{type:"void","x-component":"ButtonList","x-component-props":{style:"margin-bottom: 16px"},properties:{delivery:{type:"void",title:i18nExpression("orderMod.addDelivery"),"x-component-props":{type:"primary","@click":`{{() => {    
                  openDetailTag('add')
                }
              }}`}},submit:{type:"void",title:i18nExpression("common.submit"),"x-component-props":{type:"default","@click":`{{() => {
                  handleSubmit($form, $queryEngine, 'mutil')
                }
              }}`}},delete:{type:"void",title:i18nExpression("common.delete"),"x-component-props":{type:"default","@click":`{{() => {
                handleDelete($form, $queryEngine, 'mutil')
                }
              }}`}}}},table:{type:"array","x-component":"RenderTable","x-validator":[],"x-component-props":{style:"flex: 1",preColumns:"checkbox, seq",openCustomTable:!0,editMode:"multi-row",primaryKey:"deliveryAppointId",cascadeDeletion:!0},properties:{deliveryAppointId:{type:"void","x-hidden":!0},deliveryAppointNumber:{type:"string",default:"xxxxxx","x-component":"TableButton","x-component-props":{type:"text",disabled:!1,"@click":expression('({ row }) => openDetailTag("view", row)')},"x-render-table-column":{width:150,title:i18nExpression("orderMod.buyerOrderSynergy.deliveryAppointNumber")}},orgName:{type:"string",title:i18nExpression("purchaseDemand.businessEntity"),"x-render-table-column":{width:150}},organizationName:{type:"string",title:i18nExpression("purchaseDemand.invOrg"),"x-render-table-column":{width:150}},receiveAddress:{type:"string",title:i18nExpression("oneStopShopping.receiveAddress"),"x-render-table-column":{width:150}},vendorName:{type:"string",title:i18nExpression("common.vendorName"),"x-render-table-column":{width:150}},entryTime:{title:i18nExpression("orderMod.buyerOrderSynergy.entryTime"),...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.entryTime, '{y}-{m}-{d}')
              }`)},"x-render-table-column":{width:130}},respondents:{type:"string",title:i18nExpression("orderMod.buyerOrderSynergy.respondents"),"x-render-table-column":{width:150}},deliveryLocation:{type:"string",title:i18nExpression("orderMod.deliveryLocation"),"x-render-table-column":{width:150}},licensePlate:{type:"string",title:i18nExpression("orderMod.buyerOrderSynergy.licensePlate"),"x-render-table-column":{width:150}},carType:{type:"string",title:i18nExpression("orderMod.buyerOrderSynergy.carType"),"x-component":"DictSelect","x-component-props":{code:"CAR_TYPE"},"x-render-table-column":{width:150}},comments:{type:"string",title:i18nExpression("orderMod.buyerOrderSynergy.comments"),"x-render-table-column":{width:150}},deliveryAppointStatus:{type:"string",title:i18nExpression("orderMod.buyerOrderSynergy.status"),"x-component":"DictSelect","x-component-props":{code:"DELIVERY_APPOINT_STATUS"},"x-render-table-column":{width:100}},refusedReason:{type:"string",title:i18nExpression("oneStopShopping.refusedReason"),"x-render-table-column":{width:120}},createdFullName:{type:"string",title:i18nExpression("orderMod.buyerOrderSynergy.createdBy"),"x-render-table-column":{width:120}},creationDate:{title:i18nExpression("orderMod.buyerOrderSynergy.creationDate"),...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.creationDate, '{y}-{m}-{d}')
              }`)},"x-render-table-column":{width:130}},lastUpdatedFullName:{type:"string",title:i18nExpression("orderMod.buyerOrderSynergy.lastUpdateBy"),"x-render-table-column":{width:130}},lastUpdateDate:{title:i18nExpression("orderMod.buyerOrderSynergy.lastUpdateDate"),...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.lastUpdateDate, '{y}-{m}-{d}')
              }`)},"x-query-engine-sort":"desc","x-render-table-column":{width:130}},operation:{type:"void","x-component":"RenderTableButtonList","x-component-props":{max:2},"x-render-table-column":{title:i18nExpression("common.operation"),width:150,fixed:"right"},properties:{edit:{type:"void",title:i18nExpression("common.edit"),"x-component-props":{"@click":expression(`({ row, rowIndex }) => {
                    openDetailTag('edit', row)
                  }`)},"x-reactions":`{{
                  (field) => {
                    const row = $table.getRowByIndex($self.index)
                    field.visible = ['DRAFT', 'REJECT'].includes(row.deliveryAppointStatus)
                  }
                }}`},submit:{type:"void",title:i18nExpression("common.submit"),"x-component-props":{"@click":expression(`({ row, rowIndex }) => {
                    handleSubmit($form, $queryEngine, 'one', row)
                  }`)},"x-reactions":`{{
                  (field) => {
                    const row = $table.getRowByIndex($self.index)
                    field.visible = ['DRAFT', 'REJECT'].includes(row.deliveryAppointStatus)
                  }
                }}`},delete:{type:"void",title:i18nExpression("common.delete"),"x-component-props":{popconfirm:{title:i18nExpression("common.confirmDelete")},"@click":expression(`({ row, rowIndex }) => {
                    handleDelete($form, $queryEngine, 'one', row)
                  }`)},"x-reactions":`{{
                  (field) => {
                    const row = $table.getRowByIndex($self.index)
                    field.visible = row.deliveryAppointStatus === 'DRAFT'
                  }
                }}`}}}}}}}});return{__sfc:!0,emitTabAdd,t,app,confirmMessage,getSubmitOrDeleteParams,handleSubmit,handleDelete,openDetailTag,scope,components,schema}}});var _sfc_render$1=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c("RenderEngine",{attrs:{scope:_setup.scope,components:_setup.components,schema:_setup.schema,schemaKey:"DeliveryAppointSupplier"}})},_sfc_staticRenderFns$1=[],__component__$1=normalizeComponent(_sfc_main$1,_sfc_render$1,_sfc_staticRenderFns$1,!1,null,null,null,null);const listEngine=__component__$1.exports,_sfc_main={name:"DeliveryAppointments",components:{NavTabs},data(){return{activeTab:"listEngine",tabs:[{title:this.$t("orderMod.deliveryAppointmentList"),name:"listEngine",component:listEngine,closable:!1}]}}};var _sfc_render=function(){var _vm=this,_c=_vm._self._c;return _c("NavTabs",{ref:"tabs",attrs:{"tabs-list":_vm.tabs,"cur-tab":_vm.activeTab}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const index=__component__.exports;export{index as default};
