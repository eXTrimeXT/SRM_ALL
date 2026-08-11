import{ak as defineComponent,M as getDictItem,a4 as adaptDictData,al as usePageHelper,am as useAttrs,aq as defineSchemas,ad as expression,ah as generateXindexInOrder,ae as i18nExpression,aD as requiredValidatorSegment,ar as RenderEngine,bF as systemUrl,n as normalizeComponent,bB as useDebounceFn,cf as formGridSegment,af as yearMonthDaySelectorSegment}from"./index-6b6051d8.js";const _sfc_main$1=defineComponent({__name:"createInnerBox",props:{row:{type:Object,default:()=>({})}},setup(__props){const props=__props,innerAndOutTemplate=(async()=>{let inner=await getDictItem("TAG_PRINT_TEMPLATEP_INNER"),outer=await getDictItem("TAG_PRINT_TEMPLATEP_OUTER");return{inner:adaptDictData(inner.data,"dict"),outer:adaptDictData(outer.data,"dict")}})(),{emitTabRemove,app,t,getCurrentUserInfo,vendor}=usePageHelper(),$attrs=useAttrs(),$getPrintTemplateList=($queryEngine,$form,val)=>{$queryEngine.request.baseRequest({type:"TagTemplateRelation",lang:"zh-cn",loading:!0,query:{"*":{}},payload:[{materialCode:val.materialCode,categoryCode:val.categoryCode,type:"INNER"}],action:"listByMaterialAndCategory"}).then(res=>{res.data.length&&(res.data.forEach(item=>{item.label=item.templateName,item.value=item.templateCode}),$form.query("form").get("data").printTemplateList=res.data)})},$getTagRuleList=($queryEngine,$form,val,flag)=>{flag==="dialog"?$form.query("form").get("data").tagRuleList=[]:$form.query("form").get("data").queryTagRuleList=[],$queryEngine.request.baseRequest({type:"TagGenerateRuleConfig",lang:"zh-cn",query:{"*":{}},payload:[{materialCode:val.materialCode,categoryCode:val.categoryCode,ruleType:"INNER"}],action:"listByMaterialAndCategory"}).then(res=>{res.data.length&&(res.data.forEach(item=>{item.label=item.tagRuleName,item.value=item.tagGenerateRuleId,item.defaultFlag==="Y"&&($form.values.form.tagGenerateRuleId=item.tagGenerateRuleId,$form.values.form.tagRuleCode=item.tagRuleCode,$form.values.form.tagRuleName=item.tagRuleName,$form.values.form.tagType=item.tagType)}),flag==="dialog"?$form.query("form").get("data").tagRuleList=res.data:$form.query("form").get("data").queryTagRuleList=res.data)})},$getMaterialByQuick=($queryEngine,$form,val)=>{val.maxBoxQuantity=val.minimumPackagingQuantity||null,$form.query("form").get("data").tagRuleList=[];const{companyCode:vendorCode,companyId:vendorId,companyName:vendorName}=app.$store.getters.userInfo;val={...val,vendorCode,vendorId,vendorName},delete val.status,$form.query("form").take(field=>{field.setValue(val)}),$getTagRuleList($queryEngine,$form,val,"dialog")},$review=($form,$self,$queryEngine)=>{$self.query("form").take().submit(values=>{$queryEngine.request.baseRequest({type:"TagInnerBoxView",lang:"zh-cn",query:{"*":{}},payload:[{...values}],action:"view"}).then(res=>{$printRowsSingle(res.data,$form.values.form.templatePath,"outerBoxId")})})},$printRowsSingle=async(rows,path,key)=>{const ids=rows.map(item=>item[key]).join(","),params=encodeURIComponent(`ids=${ids}`);$openPrint(path,params)},$printRows=async(rows,type,key)=>{const temp=await innerAndOutTemplate,ids=rows.map(item=>item[key]).join(","),params=encodeURIComponent(`ids=${ids}`);$openPrint(temp[type][0].desc,params)},$openPrint=(pdfName,params)=>{const xml=encodeURIComponent(pdfName),url=`${systemUrl}/#/pdfPrint?xml=${xml}&params=${params}`;window.open(url)},scope={$props:props,$attrs,app,t,getCurrentUserInfo:getCurrentUserInfo(),$getPrintTemplateList,$getTagRuleList,$getMaterialByQuick,$openPrint,innerAndOutTemplate,$review},components={},schema=defineSchemas({createInnerBox:{type:"void","x-decorator":"QueryEngine","x-query-engine":{service:"sup-ce",actions:{read:{immediate:!0,ready:expression(`async ()=>{
            console.log('ready')
            return await new Promise((res) => {
              console.log('ready2')
              setTimeout(() => {

                let row =  $props.row

                console.log('!!!row',row)
                app.$http({
                  url: '/api-base/material/materialItem/ceeaGet',
                  method: 'GET',
                  params: {id: row.materialId},
                  loading: true,
                }).then(res=>{
                  console.log(res.data)
                  let result = res?.data?.materialItem
                  let materialObj = {
                    ...row,
                    ...result,
                    maxBoxQuantity: result.minimumPackagingQuantity || 0,
                    maxBoxQuantityDisabled: result.minimumPackagingQuantity> 0? true: false,
                    status: undefined,
                    outerBoxId: undefined
                  }

                  $form.query('form').take().setValue(materialObj)
                  $values.categoryCode = result.categoryCode
                  $values.materialCode = row.materialCode


                })


              })
            })
          }`)}}},properties:{form:{type:"object","x-data":{printTemplateList:[],tagRuleList:[],queryTagRuleList:[]},"x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:2,columnGap:32,rowGap:0},properties:generateXindexInOrder({innerBoxId:{type:"string","x-hidden":!0},materialCode:{type:"string",title:i18nExpression("common.materialCode"),"x-decorator":"FormItem","x-reactions":expression(`()=>{
              if($values.materialCode){
                console.log('!!!$values',$values)
                $getTagRuleList($queryEngine, $form, $values, 'dialog')
                $getPrintTemplateList($queryEngine,$form, $values)
              }

            }`),"x-component":"QuickSearchWrapper","x-component-props":{disabled:expression("$form.readPretty ? undefined : true"),readPretty:"{{$form.readPretty}}",showKey:"materialCode",propKey:"materialCode",name:"purchase_catalog_material_valid","@close-quicksearch":expression(`(val, scope) => {
            console.log('$queryEngine',$queryEngine)
                  $getMaterialByQuick($queryEngine,$form,val)
               }`)},...requiredValidatorSegment},materialName:{type:"string",title:i18nExpression("common.materialName"),"x-decorator":"FormItem","x-component-props":{disabled:expression("$form.readPretty ? undefined : true")},...requiredValidatorSegment},categoryCode:{type:"string",title:i18nExpression("components.category.categoryCode"),"x-decorator":"FormItem","x-component-props":{disabled:expression("$form.readPretty ? undefined : true")},...requiredValidatorSegment},categoryName:{type:"string",title:i18nExpression("components.category.categoryName"),"x-decorator":"FormItem","x-component-props":{disabled:expression("$form.readPretty ? undefined : true")},...requiredValidatorSegment},vendorName:{type:"string",title:i18nExpression("common.companyName"),"x-decorator":"FormItem","x-component-props":{disabled:expression("$form.readPretty ? undefined : true")},...requiredValidatorSegment},tagGenerateRuleId:{type:"string",title:i18nExpression("orderMod.tagRuleName"),"x-decorator":"FormItem",enum:expression("$form.query('form').get('data').tagRuleList"),"x-component":"Select","x-component-props":{"@change":expression(`(val, item) => {
                 if (!val) return

                 const option = $self.dataSource.find(item => item.value === val)
                 console.log(option)
                 $form.values.form.tagRuleCode = option.tagRuleCode
                 $form.values.form.tagRuleName = option.tagRuleName
                 $form.values.form.tagType = option.tagType
                }`)},...requiredValidatorSegment},tagType:{type:"string",title:i18nExpression("orderMod.tagType"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"TAG_TYPE",disabled:expression("$form.readPretty ? undefined : true")},...requiredValidatorSegment},materialQuantity:{type:"number",title:i18nExpression("orderMod.materialQuantity"),"x-decorator":"FormItem",...requiredValidatorSegment},maxBoxQuantity:{type:"number",title:i18nExpression("hierarchical.maximum"),"x-component-props":{disabled:expression("$form.values.form.maxBoxQuantityDisabled ? true : undefined")},"x-decorator":"FormItem","x-decorator-props":{tooltip:i18nExpression("cusEntry.supplement20250211.maxBoxQuantityTip"),tooltipLayout:"icon"},...requiredValidatorSegment},generateTagQuantity:{type:"string",title:i18nExpression("buyerDeliveryOrder.innerBoxBarcodesNum"),"x-decorator":"FormItem","x-component-props":{disabled:expression("$form.readPretty ? undefined : true")},"x-decorator-props":{tooltip:i18nExpression("cusEntry.supplement20250211.generateTagQuantityTip"),tooltipLayout:"icon"},"x-reactions":expression(`() => {
              $self.value = Math.ceil(+$form.values.form.materialQuantity / +$form.values.form.maxBoxQuantity) || 0
            }`),...requiredValidatorSegment},tailBoxNum:{type:"string",title:i18nExpression("buyerDeliveryOrder.tailBoxNum"),"x-decorator":"FormItem","x-query-engine-skip":!0,"x-component-props":{disabled:expression("$form.readPretty ? undefined : true")},"x-reactions":expression(`() => {
              if(!$form.values.form.materialQuantity || !$form.values.form.maxBoxQuantity){
                $self.value = 0
              }else{
                let num = $form.values.form.materialQuantity
                let x = String(num).indexOf(".")+1;//得到小数点的位置
                let y = String(num).length - x;//小数点的位数
                if(x!=0 && y>0){
                  $self.value = (+$form.values.form.materialQuantity % +$form.values.form.maxBoxQuantity).toFixed(y)
                }else{
                  $self.value = +$form.values.form.materialQuantity % +$form.values.form.maxBoxQuantity
                }
              }
            }`),...requiredValidatorSegment},templateCode:{type:"string",title:i18nExpression("buyerDeliveryOrder.templateCode"),"x-decorator":"FormItem",enum:expression("$form.query('form').get('data').printTemplateList"),"x-component":"Select","x-component-props":{"@change":expression(`(val, item) => {
                 if (!val) return
                 const option = $self.dataSource.find(item => item.value === val)
                 $form.values.form.templateName = option.label
                 $form.values.form.templatePath = option.templatePath
                }`)},...requiredValidatorSegment},templateName:{type:"string","x-hidden":!0},templatePath:{type:"string","x-hidden":!0}})},toolbar:{type:"void","x-component":"Space","x-component-props":{style:"margin-bottom: 16px;display:flex;justify-content:flex-end"},properties:{cancelBtn:{type:"void",title:"{{$t('components.common.cancel')}}","x-component":"RButton","x-component-props":{type:"primary","@click":expression(`() => {
                 $bus.$emit('closeBarcodeRelationDialog')
              }`)}},reviewBtn:{type:"void",title:"{{$t('common.preview')}}","x-component":"RButton","x-component-props":{type:"primary","@click":expression(`() => {
                 $review($form, $self, $queryEngine)

              }`)}},addBtn:{type:"void",title:"{{$t('common.submit')}}","x-component":"RButton","x-component-props":{type:"primary","@click":expression(`() => {
                $form.query('form').take().submit(values => {
                  $form.query('addBtn').take().setComponentProps({ loading: true })
                  $queryEngine.request.baseRequest({
                    'type': 'TagInnerBox',
                    'lang': 'zh-cn',
                    "query": {
                      "*": {}
                    },
                    "payload": [{...values}],
                    'action': 'save'
                  }).then((res) => {
                    $form.query('addBtn').take().setComponentProps({ loading: false })
                    app.$message.success($t('common.success'))
                    $bus.$emit('closeBarcodeRelationDialog')
                  }).catch(()=>{
                    $form.query('addBtn').take().setComponentProps({ loading: false })
                  })
              })

              }`)}}}}}}});return{__sfc:!0,props,innerAndOutTemplate,emitTabRemove,app,t,getCurrentUserInfo,vendor,$attrs,$getPrintTemplateList,$getTagRuleList,$getMaterialByQuick,$review,$printRowsSingle,$printRows,$openPrint,scope,components,schema,RenderEngine}}});var _sfc_render$1=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{pageAttrs:_setup.$attrs,schema:_setup.schema,scope:_setup.scope,components:_setup.components,schemaKey:"TagInnerBoxDetail"}})},_sfc_staticRenderFns$1=[],__component__$1=normalizeComponent(_sfc_main$1,_sfc_render$1,_sfc_staticRenderFns$1,!1,null,null,null,null);const CreateInnerBoxDialog=__component__$1.exports,_sfc_main=defineComponent({__name:"edit-engine",props:{showType:{type:String,default:""},row:{type:Object,default:()=>({})},flag:{type:String,default:""}},setup(__props){const props=__props,{emitTabRemove,app,t}=usePageHelper(),$attrs=useAttrs(),$closePageAndRefreshListPageData=$bus=>{$bus.$emit("TagOuterBox"),emitTabRemove($attrs.tabName)},bindSubmit=($form,$queryEngine,$bus)=>{let selects=$form.query(".detailList").take().componentProps.componentInstance.getCheckboxRecords();if(!selects.length)return app.$message.warning(t("cusEntry.supplement20250211.message2"));let payload=selects.map(row=>({outerBoxId:$form.values.outerBoxId,innerBoxId:row.innerBoxId}));$queryEngine.request.baseRequest({loading:!0,type:"TagInnerBox",action:"bound",lang:"zh-cn",query:{"*":{}},payload}).then(res=>{app.$message.success(t("common.success")),props.flag==="bind"?$bus.$emit("TagManage"):$closePageAndRefreshListPageData($bus)})},setSearchParams=$form=>{let params={outerBoxId:{eq:$form.values.outerBoxId},vendorId:{eq:$form.values.vendorId},vendorCode:{eq:$form.values.vendorCode},vendorName:{eq:$form.values.vendorName},materialCode:{eq:$form.values.materialCode},materialId:{eq:$form.values.materialId},innerBoxCode:{contains:$form.values.searchInner.innerBoxCode}};for(let key in params)!params[key]?.eq&&!params[key]?.contains&&delete params[key];return params},searchInnerBox=useDebounceFn(($form,$queryEngine)=>{if(!$form.values.outerBoxId)return app.$message.warning(t("cusEntry.supplement20250211.message3"));$queryEngine.request.baseRequest({type:"TagInnerBox",action:"queryUnRel",lang:"zh-cn",query:{"*":{}},payload:{filter:setSearchParams($form),page:{...$form.query("TagInnerBox").get("data").pageInfo,sort:"creationDate desc,tagNo desc"}}}).then(res=>{Object.assign($form.query("TagInnerBox").get("data").pageInfo,{pageNum:res.originalData.payload.pageNum,pageSize:res.originalData.payload.pageSize,total:res.originalData.payload.total}),$form.query(".detailList").take().setValue(res.data)})},300),selectOuterCodeAfter=(val,$form,$queryEngine)=>{$form.values.materialName=val?.materialName,$form.values.materialId=val?.materialId,$form.values.materialCode=val?.materialCode,$form.values.vendorId=val?.vendorId,$form.values.vendorCode=val?.vendorCode,$form.values.vendorName=val?.vendorName,$form.values.relationMaterialQuantity=val?.relationMaterialQuantity||0,$form.values.leftMaterialQuantity=val?.leftMaterialQuantity||0,$form.values.boundInnerBoxQuantity=val?.boundInnerBoxQuantity||0,$form.values.outerBoxId=val?.outerBoxId,$form.values.outerBoxCode=val?.outerBoxCode,searchInnerBox($form,$queryEngine)},scope={$props:props,$attrs,app,t,emitTabRemove,$closePageAndRefreshListPageData,bindSubmit,searchInnerBox,selectOuterCodeAfter,setSearchParams},components={CreateInnerBoxDialog},schema=defineSchemas({TagInnerBox:{type:"void","x-decorator":"QueryEngine","x-component":"FormContainer","x-component-props":{class:"the-barcodeRelation-detail",direction:"vertical"},"x-data":{pageInfo:{pageNum:1,pageSize:15,total:0}},"x-query-engine":{service:"sup-ce",actions:{read:{immediate:!0,ready:expression(`async () => {
            return await new Promise((res) => {
              setTimeout(() => {
                console.log('ready=>',$form, $values)

                // 增加一个外来控制头部表单只读
                if($props.showType === 'readOnly'){
                  $form.readPretty = true
                  $form.query('outerBoxCode').take().setComponentProps({'readPretty':true})
                }

                let row =  $attrs?.params?.row || $props.row
                console.log('!!!row',row)
                // 点击行绑定进来赋值
                if ($attrs?.params?.flag === 'bind' || $props.flag === 'bind' ) {
                  $values.vendorName = row.vendorName
                  $values.vendorId = row.vendorId
                  $values.vendorCode = row.vendorCode
                  $values.outerBoxCode =  row.outerBoxCode
                  $values.outerBoxId =  row.outerBoxId
                  $values.materialName =  row.materialName
                  $values.materialId =  row.materialId
                  $values.materialCode =  row.materialCode
                  $values.relationMaterialQuantity =  row.relationMaterialQuantity || 0
                  $values.leftMaterialQuantity =  row.leftMaterialQuantity || 0
                  $values.boundInnerBoxQuantity =  row.boundInnerBoxQuantity || 0
                }
                if($props.flag === 'bind'){
                  $bus.$emit('searchInnerBox')
                }
              })
            })  
          }`)},save:{cascadeDeletion:!0}}},items:{type:"void",properties:{cancel:{type:"void","x-content":i18nExpression("common.cancel"),"x-component":"Button","x-component-props":{type:"default","@click":expression(`() => {
              $bus.$emit('TagManage')
              $closePageAndRefreshListPageData($bus)
            }`)}},bindSubmit:{type:"void","x-content":i18nExpression("orderMod.bindCommit"),"x-component":"Button","x-component-props":{type:"primary","@click":expression(`() => {
              bindSubmit($form, $queryEngine, $bus)
            }`)}}}},properties:{bus:{type:"void","x-component":"BusEvent","x-component-props":{eventName:"closeBarcodeRelationDialog","@listener":expression(`() => {
            $form.query('createInnerBoxDialog').take().setComponentProps({ visible: false })
            searchInnerBox($form, $queryEngine)
          }`)}},collapse:{type:"void","x-component":"Collapse","x-reactions":expression(`() => {
          
        }`),properties:generateXindexInOrder({bindForm:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("cusEntry.supplement20250211.bindableOuterBoxBarcode")},"x-query-engine-skip":!0,properties:{layout:{type:"void",...formGridSegment,properties:{outerBoxCode:{type:"string",title:i18nExpression("orderMod.outerBoxBarcode"),"x-decorator":"FormItem",...requiredValidatorSegment,"x-component":"QuickSearchWrapper","x-component-props":{readPretty:"{{$props.showType === 'readOnly'}}",showKey:"outerBoxCode",showInput:"{{$values.outerBoxCode}}","scope-data":"{{$values}}",name:expression("$attrs.params?.type === 'MATERIAL' ? 'scc_sc_tag_outer_box_relation_vendor_m' : 'scc_sc_tag_outer_box_relation_vendor'"),"@close-quicksearch":expression(`async (val) => {
                        selectOuterCodeAfter(val, $form, $queryEngine)
                      }`)}},vendorName:{type:"string","x-decorator":"FormItem",title:i18nExpression("orderMod.buyerOrderSynergy.vendorName"),"x-component-props":{disabled:!0}},materialName:{type:"string","x-decorator":"FormItem",title:i18nExpression("purchaseDemand.itemName"),"x-component-props":{disabled:!0}},relationMaterialQuantity:{type:"string","x-decorator":"FormItem",title:i18nExpression("hierarchical.associated"),"x-decorator-props":{tooltip:i18nExpression("cusEntry.supplement20250211.relationMaterialQuantityTip")},"x-component-props":{disabled:!0}},leftMaterialQuantity:{type:"string","x-decorator":"FormItem",title:i18nExpression("orderMod.leftMaterialQuantity"),"x-decorator-props":{tooltip:i18nExpression("cusEntry.supplement20250211.leftMaterialQuantityTip")},"x-component-props":{disabled:!0}},boundInnerBoxQuantity:{type:"string","x-decorator":"FormItem",title:i18nExpression("cusEntry.supplement20250211.boundInnerBoxQuantity"),"x-decorator-props":{tooltip:i18nExpression("buyerDeliveryOrder.prompt17")},"x-component-props":{disabled:!0}}}}}},deliveryOrderInfo:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("orderMod.innerBoxBarcode")},"x-read-pretty":!1,properties:{searchInner:{type:"object","x-decorator":"FormLayout","x-decorator-props":{layout:"horizontal"},"x-component":"FormGrid","x-component-props":{minColumns:1,columnGap:32,rowGap:0},"x-reactions":expression(`() => {
                  if ($values.outerBoxId && ($attrs?.params?.flag === 'bind')) { 
                    searchInnerBox($form, $queryEngine)
                  }
                }`),properties:generateXindexInOrder({innerBoxCode:{type:"string",title:i18nExpression("orderMod.innerBoxBarcode"),"x-decorator":"FormItem","x-decorator-props":{gridSpan:1}},innerSearchBtn:{type:"void","x-component":"RButton","x-content":i18nExpression("common.search"),"x-decorator":"FormItem","x-decorator-props":{gridSpan:2},"x-component-props":{type:"primary","@click":expression(`() => {
                        searchInnerBox($form, $queryEngine)
                      }`)}}})},toolbar:{type:"void","x-component":"Space","x-component-props":{style:"margin-bottom: 16px"},properties:{add:{type:"void",title:"{{$t('orderMod.createdInnerBox')}}","x-component":"RButton","x-component-props":{type:"primary","@click":expression(`() => {
                        $form.query('createInnerBoxDialog').take().setComponentProps({ visible: true })
                        setTimeout(()=>{
                          $form.query('CreateInnerBoxDialogWrap').take().setComponentProps({
                            row: $values
                          })
                        })
                      }`)}}}},TagInnerBox:{type:"void","x-query-engine":{service:"sup-ce",actions:{paginationQuery:{action:"queryUnRel",transformRequest:expression(`(data, headers) => {
                        console.log(data, headers,'data, headers')
                        data.query = {
                          '*': {}
                        }

                        data.payload.filter = setSearchParams($form)
                        data.payload.page.sort = 'creationDate desc,tagNo desc'

                        // data.payload = {
                        //   filter: setSearchParams($form),
                        //   page: {
                        //     ...$form.query('TagInnerBox').get('data').pageInfo,
                        //     sort: 'creationDate desc,tagNo desc'
                        //   }
                        // }

                        return data
                      }`),onSuccess:expression(`async (res) => {
                        console.log(res,'success')

                        Object.assign($form.query('TagInnerBox').get('data').pageInfo, {
                          // pageNum: res.originalData.payload.pageNum,
                          // pageSize: res.originalData.payload.pageSize,
                          total: res.originalData.payload.total
                        })
                      }`)}}},"x-component":"QueryEngine","x-query-engine-skip":!0,properties:{bus:{type:"void","x-component":"BusEvent","x-component-props":{eventName:"searchInnerBox","@listener":expression(`() => {
                        console.log('get bus')
                        searchInnerBox($form, $queryEngine)
                      }`)}},detailList:{type:"array","x-component":"RenderTable","x-component-props":{height:300,class:"table-view-vxe-table",preColumns:"seq, checkbox",sortable:!1,primaryKey:"innerBoxId",cascadeDeletion:!0,pagination:"{{$form.query('TagInnerBox').get('data').pageInfo}}"},"x-query-engine-skip":!0,properties:generateXindexInOrder({innerBoxCode:{type:"string",title:i18nExpression("orderMod.innerBoxBarcode"),"x-render-table-column":{minWidth:100},"x-query-engine-query-operator":"contains"},materialName:{type:"string",title:i18nExpression("common.materialName"),"x-render-table-column":{minWidth:100}},materialCode:{type:"string",title:i18nExpression("common.materialCode"),"x-render-table-column":{minWidth:100}},relationMaterialQuantity:{type:"string",title:i18nExpression("hierarchical.associated"),"x-render-table-column":{minWidth:160,icon:"el-icon-question",description:i18nExpression("buyerDeliveryOrder.prompt21")}},tagNo:{type:"string",title:i18nExpression("buyerDeliveryOrder.innerBoxBarcodesNum"),"x-render-table-column":{minWidth:150}},tagRuleName:{type:"string",title:i18nExpression("orderMod.tagRuleName"),"x-render-table-column":{minWidth:160}},tagType:{type:"string",title:i18nExpression("orderMod.tagType"),"x-component":"DictSelect","x-component-props":{code:"TAG_TYPE"},"x-render-table-column":{minWidth:100}},creationDate:{...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                            parseTime(row.creationDate, '{y}-{m}-{d}')
                          }`)},title:i18nExpression("common.creationDate"),"x-render-table-column":{minWidth:100}},createdBy:{type:"string",title:i18nExpression("common.creator"),"x-render-table-column":{minWidth:100}},boundFlag:{type:"string",title:i18nExpression("orderMod.bindingState"),"x-component":"Select",enum:[{label:i18nExpression("buyerDeliveryOrder.bound"),value:"Y"},{label:i18nExpression("orderMod.unbound"),value:"N"}],"x-render-table-column":{minWidth:100}}})}}}}}})}}},createInnerBoxDialog:{type:"void",title:"{{$t('orderMod.createInnerBox')}}","x-decorator":"QueryEngine","x-component":"RDialog","x-component-props":{class:"tagmanage-barcodeRelation-dialog","close-on-click-modal":!1,"destroy-on-close":!0,footer:!1,beforeClose:expression(`(done, type) => {
        if ( type === 'ok') {
          done()
          
        } else {
          done()
          }
        }
      `)},properties:{CreateInnerBoxDialogWrap:{type:"void","x-component":"CreateInnerBoxDialog","x-component-props":{}}}}});return{__sfc:!0,emitTabRemove,app,t,$attrs,props,$closePageAndRefreshListPageData,bindSubmit,setSearchParams,searchInnerBox,selectOuterCodeAfter,scope,components,schema,RenderEngine}}});var _sfc_render=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{pageAttrs:_setup.$attrs,schema:_setup.schema,scope:_setup.scope,components:_setup.components,schemaKey:"TagInnerBoxDetail"}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const editEngine=__component__.exports;export{editEngine as e};
