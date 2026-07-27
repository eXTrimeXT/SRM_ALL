import{af as i18nExpression,ae as expression,ai as generateXindexInOrder,aC as requiredValidatorSegment,al as defineComponent,am as usePageHelper,an as useAttrs,M as getDictItem,a4 as adaptDictData,ar as defineSchemas,bt as changeFieldVisibleByDeps,as as RenderEngine,cj as bus,bv as systemUrl,n as normalizeComponent}from"./index-17d0ccd5.js";import{e as editEngine}from"./edit-engine-e0a8719e.js";const DetailDialog={type:"void",title:i18nExpression("buyerDeliveryOrder.createOuterBox"),"x-component":"RDialog","x-component-props":{"close-on-click-modal":!1,"destroy-on-close":!0,size:"large",footerButtonList:expression(`(_, { cancelButton,okButton }) => {
      return [
        cancelButton,
        { text: $t('buyerDeliveryOrder.nextStep'), 
          click: (done) =>{
            $setDeliveryForm($message,$form,$queryEngine,$values)
            
          },
          type:'primary',
          visible: $form.query('TagOuterBox').get('data').detailDialogStep === 1
        },

        { text: $t('buyerDeliveryOrder.previousStep'), 
          click: (done) =>{
            $toPrev($form)
            
          },
          visible: $form.query('TagOuterBox').get('data').detailDialogStep === 2
        },

        {text: $t('buyerDeliveryOrder.preview'),click: (done) =>{
            $review($form, $self, $queryEngine, done)
          },
          visible: $form.query('TagOuterBox').get('data').detailDialogStep === 2
        },
        
        {...okButton,visible: !$form.query('TagOuterBox').get('data').readonly && $form.query('TagOuterBox').get('data').detailDialogStep === 2,loading: $form.query('TagOuterBox').get('data').okBtnLoading }
      ]
        
      }`),beforeClose:expression(`(done, type) => {
      if ( type === 'ok') {
        
        $self.query('*.detailDialog.form').take().submit(values => {
          $form.query('TagOuterBox').get('data').okBtnLoading = true
          $queryEngine.request.baseRequest({
            loading: true,
            action: "save",
            type: 'TagOuterBox',
            payload: [{...values}],
            query: {
              "*":{}
            }
          }).then((res) => {
            $form.query('TagOuterBox').get('data').okBtnLoading = false
            app.$message.success($t('common.success'))
            // 刷新送货单行明细
            $bus.$emit('TagManage')
            // $queryEngine.state.paginationManagement.refresh()
            
            done()
          }).catch(()=>{
            $form.query('TagOuterBox').get('data').okBtnLoading = false
          })
        })
      } else {
        done()
        }

      }
    `)},properties:{steps:{type:"void","x-decorator":"div","x-decorator-props":{class:"stepDiv"},"x-component":"Steps","x-component-props":{alignCenter:!0,finishStatus:"success"},"x-reactions":expression(`field => {
        field.componentProps.active = $form.query('TagOuterBox').get('data').detailDialogStep === 1? 0 : 1
      }`),properties:{step1:{type:"void","x-component":"el-step","x-component-props":{title:i18nExpression("buyerDeliveryOrder.selectDeliveryNoteDetails")}},step2:{type:"void","x-component":"el-step","x-component-props":{title:i18nExpression("buyerDeliveryOrder.createOuterBox")}},step3:{type:"void","x-component":"el-step","x-component-props":{title:i18nExpression("buyerDeliveryOrder.complete")}}}},deliveryOrderDetail:{type:"void","x-decorator":"QueryEngine",properties:{deliveryOrderTable:{type:"array","x-component":"RenderTable","x-component-props":{class:"table-view-vxe-table",style:{height:"50vh",display:"{{($form.query('TagOuterBox').get('data').detailDialogStep === 1 ? 'block' : 'none')}}"},preColumns:"seq",pagination:!1,openCustomTable:!1,"@current-change":expression(`({ row }) => {
              console.log('单选')
              console.log('!!!选择送货单明细行 row',row)
              $form.query('TagOuterBox').get('data').currentDeliveryNoteDetail = row
            }`)},properties:generateXindexInOrder({orderDetailId:{type:"string","x-hidden":!0,"x-query-engine-primary-key":!0},orderNumber:{type:"string",title:i18nExpression("purSettlementMod.orderNumber"),"x-render-table-column":{minWidth:130},"x-read-pretty":!0},orderLineNum:{type:"string",title:i18nExpression("orderMod.orderLineNum"),"x-render-table-column":{minWidth:100},"x-read-pretty":!0},deliveryNumber:{type:"string",title:i18nExpression("orderMod.deliveryNumber"),"x-render-table-column":{minWidth:130},"x-read-pretty":!0},deliveryLine:{type:"string",title:i18nExpression("orderMod.deliveryLine"),"x-render-table-column":{minWidth:100},"x-read-pretty":!0},deliveryNoticeNumber:{type:"string",title:i18nExpression("orderMod.deliveryNoticeNumber"),"x-render-table-column":{minWidth:130},"x-read-pretty":!0},deliveryNoticeLineNum:{type:"string",title:i18nExpression("orderMod.deliveryLineNum"),"x-render-table-column":{minWidth:100},"x-read-pretty":!0},materialCode:{type:"string",title:i18nExpression("common.materialCode"),"x-render-table-column":{minWidth:100},"x-read-pretty":!0,"x-query-engine-relation":"detailList.orderDetailId"},materialName:{type:"string",title:i18nExpression("common.materialName"),"x-render-table-column":{minWidth:100},"x-read-pretty":!0}})}}},form:{type:"object","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{style:{display:"{{($form.query('TagOuterBox').get('data').detailDialogStep === 2 ? 'grid' :'none' )}}"},maxColumns:3,columnGap:32,rowGap:0},"x-query-engine-skip":!0,readPretty:"{{$form.query('TagOuterBox').get('data').readonly}}",properties:{deliveryNumber:{type:"string",title:i18nExpression("orderMod.deliveryNumber"),"x-decorator":"FormItem","x-component-props":{disabled:!0},...requiredValidatorSegment},deliveryLine:{type:"string",title:i18nExpression("orderMod.deliveryLine"),"x-decorator":"FormItem","x-component-props":{disabled:!0},...requiredValidatorSegment},materialCode:{type:"string",title:i18nExpression("common.materialCode"),"x-decorator":"FormItem","x-component-props":{disabled:!0},...requiredValidatorSegment},materialName:{type:"string",title:i18nExpression("common.materialName"),"x-decorator":"FormItem","x-component-props":{disabled:!0},...requiredValidatorSegment},categoryCode:{type:"string",title:i18nExpression("common.categoryCode"),"x-decorator":"FormItem","x-component-props":{disabled:!0},...requiredValidatorSegment},categoryName:{type:"string",title:i18nExpression("common.categoryName"),"x-decorator":"FormItem","x-component-props":{disabled:!0},...requiredValidatorSegment},vendorName:{type:"string",title:i18nExpression("common.vendorName"),"x-decorator":"FormItem","x-component-props":{disabled:!0},...requiredValidatorSegment},tagGenerateRuleId:{type:"string",title:i18nExpression("buyerDeliveryOrder.barcodeGenerationRules"),"x-decorator":"FormItem",enum:expression("$form.query('TagOuterBox').get('data').tagRuleList"),"x-component":"Select","x-component-props":{disabled:expression("$form.query('TagOuterBox').get('data').readonly"),"@change":expression(`(val, item) => {
                 if (!val) return
  
                 const option = $self.dataSource.find(item => item.value === val)
                 $form.values.form.tagRuleCode = option.tagRuleCode
                 $form.values.form.tagRuleName = option.tagRuleName
                 $form.values.form.tagType = option.tagType
                }`)},...requiredValidatorSegment},tagType:{type:"string",title:i18nExpression("buyerDeliveryOrder.barcodeStyle"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"TAG_TYPE",disabled:expression("$form.readPretty ? undefined : true")},...requiredValidatorSegment},deliveryQuantity:{type:"string",title:i18nExpression("buyerDeliveryOrder.deliveryQuantity"),"x-decorator":"FormItem","x-component-props":{disabled:!0},...requiredValidatorSegment},generateTotalQuantity:{type:"string",title:i18nExpression("buyerDeliveryOrder.quantityGenerated"),"x-decorator":"FormItem","x-component-props":{disabled:!0},...requiredValidatorSegment},generateQuantity:{type:"number",title:i18nExpression("buyerDeliveryOrder.quantityGeneratedCurrent"),"x-decorator":"FormItem","x-decorator-props":{tooltip:i18nExpression("buyerDeliveryOrder.prompt23"),tooltipLayout:"icon"},"x-validator":{required:!0,validator:expression(`(value, rule) => {
              const n = +$form.values.form.deliveryQuantity - +$form.values.form.generateTotalQuantity
              if(value > n){
                return $t('buyerDeliveryOrder.prompt24')+ n
              }
            }`)}},maxBoxQuantity:{type:"number",title:i18nExpression("buyerDeliveryOrder.outerBoxNum"),"x-decorator":"FormItem","x-component-props":{disabled:expression("$form.values.form.maxBoxQuantityDisabled ? true : undefined")},"x-decorator-props":{tooltip:i18nExpression("buyerDeliveryOrder.prompt25"),tooltipLayout:"icon"},...requiredValidatorSegment},generateTagQuantity:{type:"string",title:i18nExpression("buyerDeliveryOrder.generateTagQuantity"),"x-decorator":"FormItem","x-component-props":{disabled:expression("$form.readPretty ? undefined : true")},"x-decorator-props":{tooltip:i18nExpression("buyerDeliveryOrder.prompt26"),tooltipLayout:"icon"},"x-reactions":expression(`() => {
              $self.value = Math.ceil(+$form.values.form.generateQuantity / +$form.values.form.maxBoxQuantity) || 0
            }`),...requiredValidatorSegment},tailBoxNum:{type:"string",title:i18nExpression("buyerDeliveryOrder.tailBoxNum"),"x-decorator":"FormItem","x-component-props":{disabled:expression("$form.readPretty ? undefined : true")},"x-reactions":expression(`() => {
              if(!$form.values.form.generateQuantity || !$form.values.form.maxBoxQuantity){
                $self.value = 0
              }else{
                let num = $form.values.form.generateQuantity
                let x = String(num).indexOf(".")+1;//得到小数点的位置
                let y = String(num).length - x;//小数点的位数
                if(x!=0 && y>0){
                  $self.value = (+$form.values.form.generateQuantity % +$form.values.form.maxBoxQuantity).toFixed(y)
                }else{
                  $self.value = +$form.values.form.generateQuantity % +$form.values.form.maxBoxQuantity
                }
              }
            }`),...requiredValidatorSegment},boundInnerBoxFlag:{type:"string",title:i18nExpression("buyerDeliveryOrder.boundInnerBoxFlag"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"YES_OR_NO",disabled:expression("$form.values.form.hasBoundInnerBoxFlag ? true : undefined")},...requiredValidatorSegment},templateCode:{type:"string",title:i18nExpression("buyerDeliveryOrder.templateCode"),"x-decorator":"FormItem",enum:expression("$form.query('TagOuterBox').get('data').printTemplateList"),"x-component":"Select","x-component-props":{"@change":expression(`(val, item) => {
                 if (!val) return
                 const option = $self.dataSource.find(item => item.value === val)
                 $form.values.form.templateName = option.label
                 $form.values.form.templatePath = option.templatePath
                }`)},...requiredValidatorSegment},templateName:{type:"string","x-hidden":!0},templatePath:{type:"string","x-hidden":!0}}}}},_sfc_main=defineComponent({__name:"tagManage",setup(__props){const{emitTabAdd,t:$t,app,vendor}=usePageHelper(),$attrs=useAttrs(),innerAndOutTemplate=(async()=>{let inner=await getDictItem("TAG_PRINT_TEMPLATEP_INNER"),outer=await getDictItem("TAG_PRINT_TEMPLATEP_OUTER");return{inner:adaptDictData(inner.data,"dict"),outer:adaptDictData(outer.data,"dict")}})(),$abandonOne=(rows,$queryEngine,$message,$form,$bus)=>{if(rows[0].boundInnerBoxQuantity>0)return $message.warning($t("buyerDeliveryOrder.prompt5"));let params=rows.map(item=>({outerBoxId:item.outerBoxId}));$queryEngine.request.baseRequest({type:"TagOuterBox",lang:"zh-cn",loading:!0,payload:params,action:"disable"}).then(res=>{$message.success($t("common.success")),$bus.$emit("TagManage")})},$review=($form,$self,$queryEngine,done)=>{$self.query("*.detailDialog.form").take().submit(values=>{$queryEngine.request.baseRequest({type:"TagOuterBoxView",lang:"zh-cn",query:{"*":{}},payload:[{...values,type:"DELIVERY_NOTE"}],action:"view"}).then(res=>{$printRowsSingle(res.data,$form.values.form.templatePath,"outerBoxId")})})},$printRowsSingle=async(rows,path,key)=>{const ids=rows.map(item=>item[key]).join(","),params=encodeURIComponent(`ids=${ids}`);bus.$emit("TagManage"),$openPrint(path,params)},$batchPrintInner=async($self,$message,$queryEngine)=>{const rows=$self.query("TagInnertable").take().componentProps.componentInstance.getCheckboxRecords();if(!rows.length)return $message.warning($t("buyerDeliveryOrder.prompt6"));let templatePathList=rows.map(item=>item.templatePath),setTemplatePath=[...new Set(templatePathList)];if(setTemplatePath.length!=1)return app.$message.warning($t("buyerDeliveryOrder.prompt7"));let printParams=rows.map(item=>({innerBoxId:item.innerBoxId}));vendor()?(await $queryEngine.request.baseRequest({type:"TagInnerBox",action:"print",lang:"zh-cn",query:{"*":{}},payload:printParams})).data&&$printRows(rows,"inner","innerBoxId",setTemplatePath[0]):$printRows(rows,"inner","innerBoxId",setTemplatePath[0])},$batchPrintOuter=async($self,$message,$queryEngine)=>{const rows=$self.query("TagOutertable").take().componentProps.componentInstance.getCheckboxRecords();if(!rows.length)return $message.warning($t("buyerDeliveryOrder.prompt6"));let templatePathList=rows.map(item=>item.templatePath),setTemplatePath=[...new Set(templatePathList)];if(setTemplatePath.length!=1)return app.$message.warning($t("buyerDeliveryOrder.prompt8"));let printParams=rows.map(item=>({outerBoxId:item.outerBoxId}));vendor()?(await $queryEngine.request.baseRequest({type:"TagOuterBox",action:"print",lang:"zh-cn",query:{"*":{}},payload:printParams})).data&&$printRows(rows,"outer","outerBoxId",setTemplatePath[0]):$printRows(rows,"outer","outerBoxId",setTemplatePath[0])},$printRows=async(rows,type,key,templatePath)=>{const ids=rows.map(item=>item[key]).join(","),params=encodeURIComponent(`ids=${ids}`);bus.$emit("TagManage"),$openPrint(templatePath,params)},$openPrint=(pdfName,params)=>{const xml=encodeURIComponent(pdfName),url=`${systemUrl}/#/pdfPrint?xml=${xml}&params=${params}`;window.open(url)},unbindRequest=(payload,$queryEngine,$form,$message,$bus)=>{$queryEngine.request.baseRequest({loading:!0,type:"TagOuterBox",action:"unbound",lang:"zh-cn",query:{"*":{}},payload}).then(()=>{app.$message.success($t("common.success")),bus.$emit("TagManage")})},batchUnbind=($form,$queryEngine,$message,$bus)=>{let selects=$form.query("TagOutertable").take().componentProps.componentInstance.getCheckboxRecords();if(selects.length<1)return app.$message.warning($t("barcodeManageNew.selectUnbindData"));if(selects.some(item=>item.boundFlag==="N"))return app.$message.warning($t("barcodeManageNew.selectBindData"));let payload=selects.map(row=>({outerBoxId:row.outerBoxId}));unbindRequest(payload,$queryEngine)},$bindInnerBox=(row,$form)=>{$form.query("barcodeRelationDialog").take().setComponentProps({visible:!0}),setTimeout(()=>{$form.query("barcodeRelation").take().setComponentProps({flag:"bind",showType:"readOnly",row})})},$addOne=($form,$queryEngine)=>{$form.query("TagOuterBox").get("data").detailDialogStep=1,$form.query("TagOuterBox").get("data").currentDeliveryNoteDetail={},$form.query("*.detailDialog").take().setComponentProps({visible:!0}),setTimeout(()=>{$form.query("deliveryOrderTable").take().setComponentProps({loading:!0})}),$queryEngine.request.baseRequest({type:"DeliveryNoteVendor",lang:"zh-cn",query:{fileUploads:{"*":{}},"*":{},detailList:{orderDetailId:{orderId:{"*":{}},"*":{}},deliveryNoticeDetailId:{deliveryNoticeId:{"*":{}},"*":{}},fileUploads:{"*":{}},"*":{}}},payload:{filter:{deliveryNoteId:$attrs.params.row.deliveryNoteId}},action:"query"}).then(res=>{let data=res.originalData?.ref?.DeliveryNoteDetailVendor||{},orderDetailNode=res.originalData?.ref?.OrderDetailVendor||{},orderNode=res.originalData?.ref?.OrderVendor||{},DeliveryNoteVendor=res.originalData?.ref?.DeliveryNoteVendor||{},list=[];for(let k in data){let deliveryNoteResult=data[k],vendorName=DeliveryNoteVendor[deliveryNoteResult.deliveryNoteId].vendorName,orderDetailResult=orderDetailNode[deliveryNoteResult.orderDetailId],orderNodeResult=orderNode[orderDetailResult.orderId],all={deliveryNoteDetailId:deliveryNoteResult.deliveryNoteDetailId,lineNum:deliveryNoteResult.lineNum,deliveryNumber:$attrs.params.row.deliveryNumber,deliveryLine:deliveryNoteResult.lineNum,orderDetailId:deliveryNoteResult.orderDetailId,orderNumber:orderNodeResult.orderNumber,orderLineNum:orderDetailResult.lineNum,deliveryQuantity:deliveryNoteResult.deliveryQuantity,deliveryNoticeNumber:deliveryNoteResult.deliveryNoticeNumber,deliveryNoticeLineNum:deliveryNoteResult.deliveryNoticeLineNum,materialCode:orderDetailResult.materialCode,materialName:orderDetailResult.materialName,materialId:orderDetailResult.materialId,categoryCode:orderDetailResult.categoryCode,categoryName:orderDetailResult.categoryName,categoryId:orderDetailResult.categoryId,vendorName,vendorId:orderNodeResult.vendorId,vendorCode:orderNodeResult.vendorCode,unit:orderDetailResult.unit,type:"DELIVERY_NOTE"};list.push(all)}$form.query("TagOuterBox").get("data").DeliveryNoteCache=list,setTimeout(()=>{$form.query("deliveryOrderTable").take().setValue(list),$form.query("*.detailDialog.form").take(field=>{field.reset()}),$form.query("deliveryOrderTable").take().setComponentProps({loading:!1})})})},$toPrev=$form=>{$form.query("TagOuterBox").get("data").detailDialogStep=1},$setDeliveryForm=async($message,$form,$queryEngine,$values)=>{let row=$form.query("TagOuterBox").get("data").currentDeliveryNoteDetail;if(!row.deliveryNoteDetailId)return $message.warning($t("buyerDeliveryOrder.prompt9"));let params={...row,deliveryNumber:$attrs.params.row.deliveryNumber,deliveryLine:row.lineNum,deliveryQuantity:row.deliveryQuantity||0,generateTotalQuantity:row.generateTotalQuantity||0,maxBoxQuantity:void 0};await $getTotalQuantity($queryEngine,$form,params),await $getMaxBoxQuantity($queryEngine,$form,params),await $getMaterialByQuick($queryEngine,$form,params),$setBoundInnerBoxFlag($form,params,$values),await $getPrintTemplateList($queryEngine,$form,params),$form.query("*.detailDialog.form").take(field=>{field.setValue({...params})}),$form.query("TagOuterBox").get("data").detailDialogStep=2},$setBoundInnerBoxFlag=($form,params,$values)=>{let current=$values.DeliveryTable.find(item=>item.deliveryLine==params.deliveryLine);current?.boundInnerBoxFlag&&(params.hasBoundInnerBoxFlag=!0,params.boundInnerBoxFlag=current.boundInnerBoxFlag)},$getTotalQuantity=async($queryEngine,$form,params)=>{let totalQuantityList=await $queryEngine.request.baseRequest({type:"TagOuterBox",lang:"zh-cn",loading:!0,query:{"*":{}},payload:[{deliveryNumber:$attrs.params.row.deliveryNumber,deliveryLine:params.deliveryLine}],action:"queryGenerateTotalQuantity"});if(totalQuantityList.data&&totalQuantityList.data.length){let data=totalQuantityList.data[0];params.generateTotalQuantity=data.generateTotalQuantity||0,params.generateQuantity=+params.deliveryQuantity-+data.generateTotalQuantity}return params},$getMaxBoxQuantity=async($queryEngine,$form,params)=>{let res=await app.$http({url:"/api-base/material/materialItem/ceeaGet",method:"GET",params:{id:params.materialId},loading:!0});if(res.data){let data=res.data||{};params.maxBoxQuantity=data?.materialItem?.outboxMinPackagingQuantity||void 0,params.maxBoxQuantity>0&&(params.maxBoxQuantityDisabled=!0)}},$getPrintTemplateList=($queryEngine,$form,val)=>{$queryEngine.request.baseRequest({type:"TagTemplateRelation",lang:"zh-cn",loading:!0,query:{"*":{}},payload:[{materialCode:val.materialCode,categoryCode:val.categoryCode,type:"OUTER"}],action:"listByMaterialAndCategory"}).then(res=>{res.data.length&&(res.data.forEach(item=>{item.label=item.templateName,item.value=item.templateCode}),$form.query("TagOuterBox").get("data").printTemplateList=res.data,res.data)})},$getMaterialByQuick=async($queryEngine,$form,params)=>{$form.query("TagOuterBox").get("data").tagRuleList=[];let res=await $queryEngine.request.baseRequest({type:"TagGenerateRuleConfig",lang:"zh-cn",loading:!0,query:{"*":{}},payload:[{materialCode:params.materialCode,categoryCode:params.categoryCode,ruleType:"OUTER"}],action:"listByMaterialAndCategory"});return res.data&&res.data.length&&(res.data.forEach(item=>{item.label=item.tagRuleName,item.value=item.tagGenerateRuleId,item.defaultFlag==="Y"&&(params.tagGenerateRuleId=item.tagGenerateRuleId,params.tagRuleCode=item.tagRuleCode,params.tagRuleName=item.tagRuleName,params.tagType=item.tagType)}),$form.query("TagOuterBox").get("data").tagRuleList=res.data),params},$getInnerBox=($queryEngine,row,$message,$form,$bus)=>{$form.query("tagContainer").get("data").currentOuterBox=row,$form.query("TagInnertable").take().setComponentProps({loading:!0}),$bus.$emit("TagInnerBox")},$clearOuterBox=$form=>{$form.query("TagOutertable").take()?.setValue([])},$clearInnerBox=$form=>{$form.query("TagInnertable").take()?.setValue([])},$getOuterBox=($queryEngine,$message,$form,$bus)=>{let row=$form.query("tagContainer").get("data").currentDeliveryLine;row&&($form.query("tagContainer").get("data").showInnerBox=row.boundInnerBoxFlag=="Y",$form.query("TagOutertable").take().setComponentProps({loading:!0}),bus.$emit("TagOutertable"+$attrs.params.row.deliveryNoteId))},scope={app,$t,$attrs,$getOuterBox,$getInnerBox,$addOne,$printRows,$openPrint,$review,$getMaterialByQuick,$setDeliveryForm,$toPrev,$batchPrintOuter,$bindInnerBox,batchUnbind,$clearOuterBox,$clearInnerBox,$abandonOne,innerAndOutTemplate,$vendor:vendor,unbindRequest,$setBoundInnerBoxFlag,$batchPrintInner,$getPrintTemplateList,$status:$attrs.params?.status},components={DetailDialog,barcodeRelation:editEngine},schema=defineSchemas({barcodeRelationDialog:{type:"void",title:"{{$t('orderMod.bindInnerBox')}}","x-component":"RDialog","x-component-props":{class:"tagmanage-barcodeRelation-dialog","close-on-click-modal":!1,destroyOnClose:!0,size:"large",footer:!1,beforeClose:expression(`(done, type) => {
        if ( type === 'ok') {
          done()
          $clearInnerBox($form)
        } else {
          done()
          }
        }
      `)},properties:{barcodeRelation:{type:"void","x-component":"barcodeRelation","x-component-props":{}}}},TagOuterBox:{type:"void","x-data":{DeliveryNoteCache:[],currentDeliveryNoteDetail:{},detailDialogStep:1,readonly:!1,tagRuleList:[],printTemplateList:[],okBtnLoading:!1},"x-component":"el-container","x-component-props":{class:"flex-container-aside tag-manage-container",direction:"row"},"x-decorator":"QueryEngine","x-query-engine":{service:"sup-ce",actions:{paginationQuery:{action:"queryAll",immediate:!0,ready:expression(`() => {

            return $attrs.params.row.deliveryNumber
          }`),transformRequest:expression(`(data, headers) => {
              data.query = {
                '*': {}
              }
              let materialCode = $form.query('materialCodeQuickSearch').take().value || undefined
              let obj = {
                "deliveryNumber": $attrs.params.row.deliveryNumber
              }
              if(materialCode){
                obj.materialCode = materialCode
              }
              data.payload = [obj]
              return data
            }`),onSuccess:expression(`(res) => {
            $form.query('DeliveryTable').take().setComponentProps({ loading: false })
            setTimeout(()=>{
              // 重新选中左侧DeliveryTable选中的送货单行
              let row = $form.query('tagContainer').get('data').currentDeliveryLine

              if(row){
                let current = $values.DeliveryTable.find((item)=>{
                  return item.deliveryLine == row.deliveryLine
                })
                $form.query('DeliveryTable').take().componentProps.componentInstance.setCurrentRow(current)

                $getOuterBox({},$message,$form,$bus)
                }else{
                  // 清空外箱条码
                  $clearOuterBox($form)
                  // 清空内箱条码
                  $clearInnerBox($form)
                }

                // $bus.$emit('TagOutertable')
            },300)




          }`)}}},properties:{bus:{type:"void","x-component":"BusEvent","x-component-props":{eventName:"TagManage","@listener":expression(`() => {
            $form.query('barcodeRelationDialog').take().setComponentProps({ visible: false })
            $form.query('DeliveryTable').take().setComponentProps({ loading: true })
            console.log('eventName $queryEngine',$queryEngine)
            $queryEngine.state.paginationManagement.refresh()

          }`)}},tagContainer:{type:"void","x-decorator":"el-aside","x-decorator-props":{style:{"padding-top":"0",width:"50%"}},"x-component":"HTMLElement","x-component-props":{style:{height:"100%"}},"x-data":{showInnerBox:!1,currentDeliveryLine:{},currentOuterBox:{}},properties:{buttonArea:{type:"void","x-decorator":"FormLayout","x-decorator-props":{class:"buttonArea",style:{display:"flex","justify-content":"space-between","margin-bottom":"16px"}},properties:{add:{type:"void",title:i18nExpression("buyerDeliveryOrder.createOuterBox"),"x-component":"RButton","x-component-props":{style:{display:"{{($status === 'CREATE' && $vendor())?'block':'none'}}",height:"28px"},type:"primary","@click":expression("() => $addOne($form,$queryEngine)")}},quickSearchWrap:{type:"void","x-component":"FormGrid","x-component-props":{maxColumns:2,columnGap:8,rowGap:0,style:{display:"flex",gap:"0px 8px !important"}},properties:{lable:{type:"void","x-decorator":"span","x-decorator-props":{style:{"font-size":"12px"}},"x-content":$t("buyerDeliveryOrder.materialName")},materialCodeQuickSearch:{type:"string",title:$t("buyerDeliveryOrder.materialName"),"x-component":"QuickSearchWrapper","x-component-props":{width:"200px",showKey:"materialName",propKey:"materialCode",name:"{{$vendor()?'purchase_catalog_material':'scc_base_material_item'}}","@close-quicksearch":expression(`
                      ()=>{
                        $form.query('DeliveryTable').take().setComponentProps({ loading: true })
                        $queryEngine.state.paginationManagement.refresh()
                      }


                    `)}}}}}},DeliveryTable:{type:"array","x-component":"RenderTable","x-component-props":{loading:!0,class:"table-view-vxe-table",style:"flex: 1;height:92%",preColumns:"seq",pagination:!1,"@current-change":expression(`({ row }) => {

                $form.query('tagContainer').get('data').currentDeliveryLine = row
                $getOuterBox($queryEngine,$message,$form,$bus)
                $clearInnerBox($form)
              }`)},properties:generateXindexInOrder({deliveryNumber:{type:"string","x-render-table-column":{title:"{{$t('orderMod.deliveryNumber')}}",minWidth:120},"x-query-engine-primary-key":!0},deliveryLine:{type:"string","x-render-table-column":{title:"{{$t('orderMod.deliveryLine')}}",minWidth:120}},materialName:{type:"string","x-render-table-column":{title:"{{$t('orderMod.materialName')}}",minWidth:120}},materialCode:{type:"string","x-render-table-column":{title:"{{$t('orderMod.materialCode')}}",minWidth:120}},unit:{type:"string","x-render-table-column":{title:"{{$t('orderMod.unit')}}",minWidth:120}},deliveryQuantity:{type:"string","x-render-table-column":{title:"{{$t('orderMod.deliveryQuantity')}}",minWidth:120}},boxedMaterialQuantity:{type:"string","x-render-table-column":{title:"{{$t('orderMod.boxedMaterialQuantity')}}",minWidth:120,"title-prefix":{content:`①${$t("buyerDeliveryOrder.prompt10")}
②${$t("buyerDeliveryOrder.prompt11")}`}}},generatedOutBoxTagQuantity:{type:"string","x-render-table-column":{title:"{{$t('orderMod.generatedOutBoxTagQuantity')}}",minWidth:120,"title-prefix":{content:$t("buyerDeliveryOrder.prompt12")}}},boundInnerBoxFlag:{type:"string","x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"},"x-render-table-column":{title:"{{$t('orderMod.boundInnerBoxFlag')}}",minWidth:120}},boundInnerBoxQuantity:{type:"string","x-render-table-column":{title:"{{$t('orderMod.boundInnerBoxQuantity')}}",minWidth:120,"title-prefix":{content:$t("buyerDeliveryOrder.prompt13")}}}})}}},rightContainer:{type:"void","x-decorator":"el-container","x-decorator-props":{class:"flex-container",direction:"vertical"},"x-component":"HTMLElement","x-component-props":{style:{height:"100%"}},properties:{TagOuterBox:{type:"void","x-decorator":"QueryEngine","x-query-engine":{service:"sup-ce",actions:{paginationQuery:{transformRequest:expression(`(data, headers) => {
                      data.type = 'TagOuterBox'
                      data.query = {
                        '*': {}
                      }
                      data.payload.filter = {
                        "deliveryNumber": {
                            "eq": $form.query('tagContainer').get('data').currentDeliveryLine.deliveryNumber
                          },
                          "deliveryLine": {
                            "eq": $form.query('tagContainer').get('data').currentDeliveryLine.deliveryLine
                          },
                      }
                      data.payload.page.sort = 'creationDate desc,tagNo desc'
                      return data
                    }`),onSuccess:expression(`(res) => {
                      $form.query('TagOutertable').take().setComponentProps({ loading: false })

                      setTimeout(()=>{
                        console.log('!!!TagOuterBox $queryEngine',$queryEngine)
                        // 重新选中之前的外箱条码
                        let row = $form.query('tagContainer').get('data').currentOuterBox
                        if(row){
                          let current = $values.TagOutertable.find((item)=>{
                            return item.outerBoxId == row.outerBoxId
                          })
                          $form.query('TagOutertable').take().componentProps.componentInstance.setCurrentRow(current)
                          $getInnerBox($queryEngine,row,$message,$form,$bus)
                        }
                      },300)


                    }`)}}},properties:{bus:{type:"void","x-component":"BusEvent","x-component-props":{eventName:"{{'TagOutertable' + $attrs.params.row.deliveryNoteId}}","@listener":expression(`() => {
                    $queryEngine.state.paginationManagement.refresh()


                  }`)}},toolbar:{type:"void","x-component":"Space","x-component-props":{style:"margin-bottom: 16px;height:28px;"},properties:{print:{type:"void","x-content":i18nExpression("buyerDeliveryOrder.batchPrintOuterBox"),"x-reactions":expression(`field => {
                        let flag = $values.TagOutertable && $values.TagOutertable.length
                        field.visible = flag && ['CREATE','DELIVERED'].includes($status)
                      }`),"x-component":"RButton","x-component-props":{type:"primary","@click":expression("() => {$batchPrintOuter($self,$message,$queryEngine)}")}},batchUnbind:{type:"void","x-content":i18nExpression("buyerDeliveryOrder.batchUnbinding"),"x-component":"RButton","x-reactions":expression(`field => {
                        let flag = $values.TagOutertable && $values.TagOutertable.length
                        field.visible = ['CREATE','CANCELLED'].includes($status)  && $vendor() && flag
                      }`),"x-component-props":{type:"primary","@click":expression(`() => {
                        console.log('解绑')
                        batchUnbind($form, $queryEngine,$message,$bus)
                      }`)}}}},TagOutertable:{type:"array","x-component":"RenderTable","x-component-props":{class:"table-view-vxe-table",style:{flex:1,height:"{{$form.query('tagContainer').get('data').showInnerBox? '42%': '92%'}}"},preColumns:"checkbox,seq","@current-change":expression(`({ row }) => {
                    $getInnerBox($queryEngine,row,$message,$form,$bus)
                  }`)},properties:generateXindexInOrder({outerBoxId:{type:"number","x-hidden":!0,"x-query-engine-primary-key":!0},outerBoxCode:{type:"string","x-render-table-column":{title:"{{$t('orderMod.outerBoxCode')}}",minWidth:120}},relationMaterialQuantity:{type:"string","x-render-table-column":{title:"{{$t('orderMod.relationMaterialQuantity')}}",minWidth:120}},tagNo:{type:"string","x-query-engine-sort":"desc","x-render-table-column":{title:$t("buyerDeliveryOrder.prompt14"),minWidth:160,"title-prefix":{content:$t("buyerDeliveryOrder.prompt15")}}},generateQuantity:{type:"string","x-render-table-column":{title:"{{$t('orderMod.generateQuantity')}}",minWidth:120,"title-prefix":{content:"该送货单明细行本次生成外箱条码的物料数量"}}},boundInnerBoxQuantity:{type:"string","x-render-table-column":{title:"{{$t('orderMod.boundInnerBoxQuantity2')}}",minWidth:120,"title-prefix":{content:$t("buyerDeliveryOrder.prompt17")}}},leftMaterialQuantity:{type:"string","x-render-table-column":{title:"{{$t('orderMod.leftMaterialQuantity')}}",minWidth:120,"title-prefix":{content:$t("buyerDeliveryOrder.prompt18")}}},boundFlag:{type:"string","x-component":"Select",enum:[{label:$t("buyerDeliveryOrder.bound"),value:"Y"},{label:$t("buyerDeliveryOrder.unbound"),value:"N"}],"x-render-table-column":{title:"{{$t('orderMod.boundFlag')}}",minWidth:120}},deliveryNoteStatus:{type:"string","x-component":"DictSelect","x-component-props":{code:"DELIVERY_NOTE_STATUS"},"x-render-table-column":{title:$t("buyerDeliveryOrder.deliveryStatus"),minWidth:120},"x-query-engine-skip":!0},status:{type:"string","x-component":"DictSelect","x-component-props":{code:"TAG_STATUS"},"x-render-table-column":{title:$t("buyerDeliveryOrder.barcodeStatus"),minWidth:120}},printedFlag:{type:"string","x-component":"DictSelect","x-component-props":{code:"PRINT_STATUS"},"x-render-table-column":{title:$t("buyerDeliveryOrder.barcodePrinting"),minWidth:120}},creationDate:{type:"string","x-render-table-column":{title:"{{$t('common.creationDate')}}",minWidth:120},"x-query-engine-sort":"desc"},operation:{type:"void","x-visible":"{{!$readOnly  && $vendor()}}","x-render-table-column":{title:i18nExpression("common.operation"),width:160,fixed:"right",sortable:!1},properties:{layout:{type:"void","x-component":"Space",properties:{unbind:{type:"void",title:"{{$t('orderMod.unbind')}}","x-component":"TableButton","x-reactions":changeFieldVisibleByDeps([".boundFlag"],"$deps[0] === 'Y'  && ['CREATE','CANCELLED'].includes($status)"),"x-component-props":{showPopconfirm:!0,title:$t("buyerDeliveryOrder.prompt19"),"@confirm":expression("({ row }) => unbindRequest([{outerBoxId:row.outerBoxId}], $queryEngine, $form,$message,$bus)")}},abandon:{type:"void",title:"{{$t('orderMod.abandon')}}","x-component":"TableButton","x-reactions":changeFieldVisibleByDeps([".boundFlag",".status"],"$deps[0] !== 'Y' && $deps[1] !== 'N'   && ['CREATE'].includes($status)"),"x-component-props":{showPopconfirm:!0,title:$t("buyerDeliveryOrder.prompt20"),"@confirm":expression("({ row }) => $abandonOne([row], $queryEngine, $message,$form,$bus)")}},bindInnerBox:{type:"void",title:"{{$t('orderMod.bindInnerBox')}}","x-component":"TableButton","x-reactions":changeFieldVisibleByDeps([".leftMaterialQuantity",".status"],"$deps[0] > 0 &&  $deps[1] === 'Y'  && ['CREATE'].includes($status) && $form.query('tagContainer').get('data').showInnerBox"),"x-component-props":{type:"text","@click":expression("({ row }) => $bindInnerBox(row,$form)")}}}}}}})}}},TagInnerBox:{type:"void","x-decorator":"QueryEngine","x-decorator-props":{style:{display:"{{$form.query('tagContainer').get('data').showInnerBox?'block':'none'}}"}},"x-query-engine":{service:"sup-ce",actions:{paginationQuery:{transformRequest:expression(`(data, headers) => {
                    data.type = 'TagInnerBox'
                      data.query = {
                        '*': {}
                      }
                      data.payload.filter = {
                        "outerBoxId": {
                          "eq": $form.query('tagContainer').get('data').currentOuterBox.outerBoxId
                        },
                      }
                    data.payload.page.sort = 'creationDate desc,tagNo desc'
                      return data
                    }`),onSuccess:expression(`(res) => {
                      $form.query('TagInnertable').take().setComponentProps({ loading: false })
                    }`)}}},properties:{bus:{type:"void","x-component":"BusEvent","x-component-props":{eventName:"TagInnerBox","@listener":expression(`() => {
                    setTimeout(()=>{
                      let current = $form.query('tagContainer').get('data').currentOuterBox || {}
                      if(current.outerBoxId){
                        $queryEngine.state.paginationManagement.refresh()
                      }
                    })

                  }`)}},toolbar:{type:"void","x-component":"Space","x-component-props":{style:"margin-bottom: 16px;height:28px;"},properties:{printInner:{type:"void","x-reactions":expression(`field => {
                        let flag = $values.TagInnertable && $values.TagInnertable.length
                        field.visible = flag && ['CREATE','DELIVERED'].includes($status)
                      }`),title:i18nExpression("buyerDeliveryOrder.batchPrintInnerBox"),"x-component":"RButton","x-component-props":{type:"primary","@click":expression("() => {$batchPrintInner($self,$message,$queryEngine)}")}}}},TagInnertable:{type:"array","x-component":"RenderTable","x-component-props":{class:"table-view-vxe-table",style:"flex: 1;height:42%",preColumns:"checkbox,seq"},properties:generateXindexInOrder({innerBoxCode:{type:"string","x-render-table-column":{title:"{{$t('orderMod.innerBoxCode')}}",minWidth:120}},materialName:{type:"string","x-render-table-column":{title:"{{$t('orderMod.materialName')}}",minWidth:120}},materialCode:{type:"string","x-render-table-column":{title:"{{$t('orderMod.materialCode')}}",minWidth:120}},categoryName:{type:"string","x-render-table-column":{title:"{{$t('orderMod.categoryName')}}",minWidth:120}},categoryCode:{type:"string","x-render-table-column":{title:"{{$t('orderMod.categoryCode')}}",minWidth:120}},vendorCode:{type:"string","x-render-table-column":{title:"{{$t('orderMod.vendorCode')}}",minWidth:120}},vendorName:{type:"string","x-render-table-column":{title:"{{$t('orderMod.vendorName')}}",minWidth:120}},unit:{type:"string","x-render-table-column":{title:"{{$t('orderMod.unit')}}",minWidth:120}},relationMaterialQuantity:{type:"string","x-render-table-column":{title:"{{$t('orderMod.relationMaterialQuantity')}}",minWidth:120,"title-prefix":{content:$t("buyerDeliveryOrder.prompt21")}}},tagNo:{type:"string","x-query-engine-sort":"desc","x-render-table-column":{title:$t("buyerDeliveryOrder.innerBoxBarcodesNum"),minWidth:160,"title-prefix":{content:$t("buyerDeliveryOrder.prompt22")}}},materialQuantity:{type:"string","x-render-table-column":{title:"{{$t('orderMod.materialQuantity')}}",minWidth:120}},tagRuleName:{type:"string","x-render-table-column":{title:"{{$t('orderMod.tagRuleName')}}",minWidth:120}},tagType:{type:"string","x-render-table-column":{title:"{{$t('orderMod.tagType')}}",minWidth:120}},printCount:{type:"string","x-render-table-column":{title:"{{$t('orderMod.printCount')}}",minWidth:120}},status:{type:"string","x-component":"Select",enum:[{label:$t("buyerDeliveryOrder.takeEffect"),value:"Y"},{label:$t("buyerDeliveryOrder.abandon"),value:"N"}],"x-render-table-column":{title:"{{$t('orderMod.tagStatus')}}",minWidth:120}},boundFlag:{type:"string","x-component":"Select",enum:[{label:$t("buyerDeliveryOrder.bound"),value:"Y"},{label:$t("buyerDeliveryOrder.unbound"),value:"N"}],"x-render-table-column":{title:"{{$t('orderMod.boundFlag')}}",minWidth:120}},printedFlag:{type:"string","x-component":"Select",enum:[{label:$t("buyerDeliveryOrder.printed"),value:"Y"},{label:$t("buyerDeliveryOrder.unprinted"),value:"N"}],"x-render-table-column":{title:"{{$t('orderMod.printedFlag')}}",minWidth:120}},creationDate:{type:"string","x-render-table-column":{title:"{{$t('common.creationDate')}}",minWidth:120},"x-query-engine-sort":"desc"}})}}}}},detailDialog:{...DetailDialog}}}});return{__sfc:!0,emitTabAdd,$t,app,vendor,$attrs,innerAndOutTemplate,$abandonOne,$review,$printRowsSingle,$batchPrintInner,$batchPrintOuter,$printRows,$openPrint,unbindRequest,batchUnbind,$bindInnerBox,$addOne,$toPrev,$setDeliveryForm,$setBoundInnerBoxFlag,$getTotalQuantity,$getMaxBoxQuantity,$getPrintTemplateList,$getMaterialByQuick,$getInnerBox,$clearOuterBox,$clearInnerBox,$getOuterBox,scope,components,schema,RenderEngine}}});var _sfc_render=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{pageAttrs:_setup.$attrs,schema:_setup.schema,scope:_setup.scope,components:_setup.components,schemaKey:"TagOuterBox"}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const tagManage=__component__.exports;export{tagManage as t};
