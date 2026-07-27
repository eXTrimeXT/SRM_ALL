import{N as NavTabs}from"./index-a035e78f.js";import{n as normalizeComponent,L as axios,bZ as getMenuInfo,s as sysPrefix,l as getToken,al as defineComponent,am as usePageHelper,an as useAttrs,ar as defineSchemas,ae as expression,af as i18nExpression,bt as changeFieldVisibleByDeps,aC as requiredValidatorSegment,as as RenderEngine,bG as markRaw,bO as $dayjs,ai as generateXindexInOrder}from"./index-17d0ccd5.js";import{P as Parser,f as fixedElem}from"./index-a2568972.js";import{T as Tinymce}from"./index-8e1a8504.js";import{u as uniqueId}from"./uniqueId-bf6f89eb.js";import{c as contractManagement}from"./index-6a91ec6d.js";import"./enum-d9c76693.js";import"./z-material-table-78984a65.js";import"./validate-8a9c1e8f.js";import"./index-531039c3.js";import"./util-6482eb24.js";const _sfc_main$3={name:"Preview",components:{},data(){return{eidtable:!1,pdfUrl:"",materialListData:[],mergeForm:{contractClass:"NPM",contractCode:"HT2021010100001",certificateNo:"ZS2021010100001",partyA:"广东美云智数科技有限公司"},partnerData:[],editorInstance:null,childContext:null,flag:"preview",html:null}},computed:{isEditing(){return!!(this.childContext||{}).editable}},mounted(){this.compile()},methods:{edit(){this.$attrs.params.row,this.childContext.editable=!0},preview(){this.childContext.editable=!1},addWatermark(pdf,name,options){const{watermark_height,watermark_width,watermark_x=.2,watermark_y=.2,watermark_y_space=1.5,watermark_x_space=1.5}=options,angle=45;for(let i=0;i<10;i++){const y=watermark_y+(watermark_y_space+watermark_height)*i;for(let j=0;j<10;j++){const x=watermark_x+(watermark_width+watermark_x_space)*j;pdf.text(name,x,y,{angle})}}},getParagraphs(element){const childNodes=element.childNodes,count=Math.floor(childNodes.length/130),html=Array.from(childNodes).map(node=>node.outerHTML);return{count,html}},group(array,subGroupLength){let index2=0,newArray=[];for(;index2<array.length;)newArray.push(array.slice(index2,index2+=subGroupLength));return newArray},printPdf(){this.getPdfFile(!0)},async getPdfFile(flag=!1){let htmlBody=this.$refs.preview_wrapper.innerHTML;const breakPageMatcher=/_ueditor_page_break_tag_/g;htmlBody=htmlBody.replace(breakPageMatcher,($0,$1)=>'<div class="breakPage" style="break-after: page;"></div>');const res=await axios({url:"/egg/upload",method:"POST",loading:!0,data:{options:{format:"a4",margin:{left:"1cm",top:"1cm",right:"1cm",bottom:"1cm"}},htmlString:'<div style="page-break-inside: avoid;overflow: hidden;font-family: simsun;">'+htmlBody+"</div>"},responseType:"arraybuffer"}),blob=new Blob([res.data],{type:"application/pdf"}),formData=new FormData;formData.append("file",blob,"myfile.pdf");let menuInfo=getMenuInfo();const pdf=await axios({url:`${sysPrefix()}/api-base/pdf/pdfAddWatermark`,method:"POST",data:formData,headers:{Authorization:"Bearer "+getToken(),contentType:"form-data","X-Fun-Info":menuInfo.secretKey},responseType:"arraybuffer",loading:!0});pdf.data instanceof ArrayBuffer;let blobs=new Blob([pdf.data],{type:"application/pdf"});return flag&&(this.pdfUrl=URL.createObjectURL(blobs),setTimeout(()=>{this.$refs.iframe.contentWindow.print()},1e3)),blobs},compile(){const{content}=this.$attrs.params.row;if(!content)return;let contentBody=content;const breakPageMatcher=/_ueditor_page_break_tag_/g;contentBody=contentBody.replace(breakPageMatcher,($0,$1)=>'<div class="breakPage" style="break-after: page;"></div>');const{vueTemplate,elementCodes}=Parser.replacer(contentBody);this.eidtable=!!Object.keys(elementCodes||{}).length;const wrapper=this.$refs.preview_wrapper,$el=Parser.generateComponent({html:vueTemplate,elemKeys:elementCodes,context:this,wrapper});this.$refs.markedContent.appendChild($el)}}};var _sfc_render$3=function(){var _vm=this,_c=_vm._self._c;return _c("el-container",{staticClass:"the_contractTemplatePreview_wrapper",attrs:{direction:"vertical"}},[_c("el-main",[_c("el-alert",{attrs:{title:_vm.$t("contractMod.alertEdit"),type:"warning","show-icon":""}}),_c("div",{staticClass:"button-group"},[_vm.isEditing?_c("el-button",{attrs:{type:"primary"},on:{click:_vm.preview}},[_vm._v(" "+_vm._s(_vm.$t("contractMod.exitEditMode"))+" ")]):_c("el-button",{attrs:{type:"primary",disabled:!_vm.eidtable},on:{click:_vm.edit}},[_vm._v(" "+_vm._s(_vm.$t("contractMod.enterEditMode"))+" ")]),_c("el-button",{attrs:{type:"primary"},on:{click:_vm.printPdf}},[_vm._v(" "+_vm._s(_vm.$t("route.pdfPrint"))+" ")])],1),_c("div",{ref:"preview_wrapper",staticClass:"preview_wrapper",attrs:{id:"preview_wrapper"}},[_c("div",{ref:"markedContent",staticStyle:{position:"relative"},attrs:{id:"print"}})])],1),_c("iframe",{ref:"iframe",staticStyle:{display:"none"},attrs:{src:_vm.pdfUrl}})],1)},_sfc_staticRenderFns$3=[],__component__$3=normalizeComponent(_sfc_main$3,_sfc_render$3,_sfc_staticRenderFns$3,!1,null,"f01c8aa2",null,null);const Preview=__component__$3.exports,_sfc_main$2=defineComponent({__name:"edit_engine",setup(__props){const{emitTabRemove,emitTabAdd,t,app}=usePageHelper(),attrs=useAttrs(),scope={$markRaw:markRaw,uniqueId,contractManagement,$attrs:attrs,emitTabRemove,app,emitTabAdd,Preview,$dayjs},schema=defineSchemas({ModelHead:{type:"void","x-component":"FormContainer","x-decorator":"QueryEngine","x-query-engine":{service:"cm",actions:{read:{immediate:!0,ready:expression(`() => {
            $form.readPretty = $readOnly
            let startDate = $attrs.params.row.startDate || ''
            let endDate = $attrs.params.row.endDate || ''
            if ($attrs.params.row) {
              $form.setValues({
                content: '',
                ...$attrs.params.row,
                allDate: [startDate, endDate]
              })
            }

            return false
          }`)}}},items:{type:"object",properties:{goBack:{type:"void","x-content":expression("$t($readOnly ? 'common.backTo' : 'components.common.cancel')"),"x-component":"Button","x-component-props":{type:"default","@click":expression(`async (values) => {
              app.$confirm('此次修改并未保存是否取消', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
              }).then(() => {
                emitTabRemove($attrs.tabName)
              }).catch(() => {
              });
            }`)}},view:{type:"void","x-content":i18nExpression("common.preview"),"x-component":"Button","x-component-props":{type:"default","@click":expression(`async (values) => {
              const row = $values
              const tab = {
                component: Preview,
                params: { row: row },
                title: $t('common.preview') + (row.modelName ? '-' + row.modelName : ''),
                name: 'preview_' + (row.modelHeadId ? row.modelHeadId : '')
              }
              emitTabAdd(tab)
            }`)}},submit:{type:"void","x-content":i18nExpression("common.staging"),"x-component":"Button","x-reactions":changeFieldVisibleByDeps([".status"],"!$readOnly"),"x-component-props":{type:"default",plain:"plain","@submit":expression(`async (values) => {
              values.startDate = values.allDate && values.allDate[0] != '' ? values.allDate[0] : null
              values.endDate = values.allDate && values.allDate[1] != '' ? values.allDate[1] : null
              $queryEngine.request.save({
                ...values,
                status: values.status || 'DRAFT',
              }).then(() => {
                $bus.$emit('ModelHead')
                emitTabRemove($attrs.tabName)
              })
           }`)}},active:{type:"void","x-content":i18nExpression("common.active"),"x-component":"Button","x-reactions":changeFieldVisibleByDeps([".status"],"!$readOnly"),"x-component-props":{"@submit":expression(`async (values) => {
              values.startDate = values.allDate && values.allDate[0] != '' ? values.allDate[0] : null
              values.endDate = values.allDate && values.allDate[1] != '' ? values.allDate[1] : null
              $queryEngine.request.save({
                ...values,
                status: 'VALID'
              }).then(() => {
                $bus.$emit('ModelHead')
                emitTabRemove($attrs.tabName)
              })
           }`)}}}},properties:{layout:{type:"void","x-component":"FormLayout","x-component-props":{layout:"vertical",gridRowGap:0},properties:{layout:{type:"void","x-component":"FormGrid","x-component-props":{maxColumns:4,columnGap:20,rowGap:0},properties:{modelName:{type:"string",title:i18nExpression("contractMod.templHeadId"),"x-decorator":"FormItem","x-component-props":{disabled:"{{$readOnly}}"},...requiredValidatorSegment},status:{type:"string",title:i18nExpression("contractMod.contractStatus"),"x-component":"DictSelect","x-component-props":{code:"CONTRACT_MODEL_STATUS",disabled:!0},"x-decorator":"FormItem"},modelType:{type:"string",title:i18nExpression("contractMod.contractType"),"x-component":"DictSelect","x-component-props":{code:"ELEM_CONTRACT_TYPE",disabled:"{{$readOnly}}"},"x-decorator":"FormItem",...requiredValidatorSegment},startDate:{type:"string",default:null,"x-hidden":!0},endDate:{type:"string",default:null,"x-hidden":!0},allDate:{type:"string",title:i18nExpression("dataConfMod.expiryDate"),"x-query-engine-skip":!0,"x-component":"DatePicker","x-component-props":{type:"daterange",disabled:"{{$readOnly}}"},"x-decorator":"FormItem"}}}}},toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},properties:{fixed:{type:"void",title:"{{$t('contractMod.fixedElement')}}","x-component-props":{type:"primary",disabled:expression("$readOnly"),"@click":expression(`() => {
                $form.query('fixedElementDialog').take().setComponentProps({ visible: true })
              }`)}},fixed2:{type:"void",title:"{{$t('contractMod.contractTypeElement')}}","x-component-props":{disabled:expression("$readOnly"),"@click":expression(`() => {
                $form.query('TypeRange').take().setComponentProps({ visible: true })
              }`)}}}},content:{type:"string","x-component":"Tinymce","x-component-props":{id:"tinymceContractMode",height:460,"@setup":expression(`(editorInstance) => {
            const { flag, row } = $attrs.params
            if (flag === 'view') {
              editorInstance.setMode('readonly')
            }
            $self.setData({
              editorInstance: $markRaw(editorInstance)
            })
          }`)}}}},fixedElementDialog:{type:"void",title:i18nExpression("contractMod.conpayType"),"x-component":"RDialog","x-component-props":{footer:!1},properties:{fixedElementDialogTable:{type:"array","x-decorator":"QueryEngine","x-component":"RenderTable","x-component-props":{preColumns:"seq",performanceMode:!0,pagination:!1,maxHeight:"58vh"},default:fixedElem,properties:{elemName:{type:"string","x-query-engine-skip":!0,title:"{{$t('contractMod.elemName')}}","x-render-table-column":{minWidth:150}},elemCode:{type:"string","x-query-engine-skip":!0,title:"{{$t('contractMod.elemCode')}}","x-render-table-column":{minWidth:150}},operation:{type:"void",title:i18nExpression("common.operation"),"x-render-table-column":{width:130,fixed:"right",performanceMode:!1},"x-component":"RenderTableButtonList",properties:{insertFixedElem:{type:"void",title:i18nExpression("contractMod.insert"),"x-component-props":{"@click":expression(`({ row }) => {
                    const { elemName, elemCode } = row
                    const _uniqueId = uniqueId('key_'+Date.now()+'_')
                    let content2 = ['$','{', '[', elemName, ']', elemCode, ':', _uniqueId, '}' ]
                    const content = content2.join("")

                    const { editorInstance } = $form.query('.content')
                        .get('data')

                    editorInstance.focus()
                    editorInstance.execCommand('mceInsertRawHTML', false, content)
                    $closed()
                  }`)}}}}}}}},TypeRange:{type:"void",title:i18nExpression("contractMod.conpayType"),"x-component":"RDialog","x-component-props":{footer:!1},"x-decorator":"QueryEngine","x-query-engine":{service:"cm",actions:{queryMaintain:{autoFormatResult:!1,transformResponse:expression(`(res) => {
            const data = JSON.parse(res)

            if (data.data.records && data.data.records.length) {
              $form.query('TypeRange.table').take(field => {
                field.setValue(
                  data.data.records.map(id => data.data.ref.ElemMaintain[id])
                )
              })
            }

            return data
          }`)}}},properties:{table:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",performanceMode:!0,pagination:!1,maxHeight:"58vh"},"x-reactions":expression(`() => {
          const value = $form.query('.modelType').take().value

          if (!value) {
            $self.setValue([])
            return
          }

          $queryEngine.request.baseRequest({
            action: 'queryMaintain',
            payload: {
              filter: {
                contractType: { eq: value  },
              }
            },
          })
        }`),properties:{addMethod:{type:"string","x-hidden":!0,"x-query-engine-relation":"elemMaintainId"},elemName:{type:"string",title:"{{$t('contractMod.elemName')}}","x-render-table-column":{minWidth:150}},elemCode:{type:"string",title:"{{$t('contractMod.elemCode')}}","x-render-table-column":{minWidth:150}},operation:{type:"void",title:i18nExpression("common.operation"),"x-render-table-column":{width:130,fixed:"right",performanceMode:!1},"x-query-engine-skip":!0,"x-component":"RenderTableButtonList",properties:{insertFixedElem:{type:"void",title:i18nExpression("contractMod.insert"),"x-component-props":{"@click":expression(`({ row }) => {
                    const { elemName, elemCode } = row
                    const _uniqueId = uniqueId('key_'+Date.now()+'_')
                    let content2 = ['$','{', '[', elemName, ']', elemCode, ':', _uniqueId, '}' ]
                    const content = content2.join("")

                    const { editorInstance } = $form.query('.content')
                      .get('data')

                    editorInstance.focus()
                    editorInstance.execCommand('mceInsertRawHTML', false, content)
                    $closed()
                  }`)}}}}}}}}});return{__sfc:!0,emitTabRemove,emitTabAdd,t,app,attrs,scope,schema,components:{Tinymce},RenderEngine}}});var _sfc_render$2=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{schemaKey:"contractModeDetail",pageAttrs:_vm.$attrs,schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns$2=[],__component__$2=normalizeComponent(_sfc_main$2,_sfc_render$2,_sfc_staticRenderFns$2,!1,null,null,null,null);const Edit=__component__$2.exports,_sfc_main$1=defineComponent({__name:"list",setup(__props){const schema=defineSchemas({ModelHead:{type:"void","x-query-engine":{service:"cm",actions:{paginationQuery:{immediate:!0}}},"x-decorator":"el-container","x-decorator-props":{class:"flex-container the_contractTemplateList_wrapper",direction:"vertical"},"x-component":"QueryEngine",properties:{bus:{type:"void","x-component":"BusEvent","x-component-props":{eventName:"ModelHead","@listener":expression(`() => {
            $queryEngine.state.paginationManagement.refresh()
          }`)}},query:{type:"object","x-query-engine-skip":!0,"x-component":"QueryFormByQueryEngine",properties:generateXindexInOrder({modelCode:{type:"string",title:"{{$t('dataConfMod.templateCode')}}","x-query-engine-query-operator":"contains"},modelName:{type:"string",title:"{{$t('contractMod.templHeadId')}}","x-query-engine-query-operator":"contains"},status:{type:"string",title:"{{$t('common.status')}}","x-component":"DictSelect","x-component-props":{code:"CONTRACT_MODEL_STATUS"}}})},toolbar:{type:"void","x-component":"Space","x-component-props":{style:"margin-bottom: 16px"},properties:{add:{type:"void",title:"{{$t('common.add')}}","x-component":"RButton","x-component-props":{type:"primary","@click":'{{() => $edit({}, "add")}}'}}}},table:{type:"array","x-component":"RenderTable","x-component-props":{class:"table-view-vxe-table",style:"flex: 1",preColumns:"seq",openCustomTable:!0},properties:generateXindexInOrder({modelHeadId:{type:"string","x-hidden":!0,"x-query-engine-primary-key":!0},creationDate:{type:"string","x-hidden":!0,"x-query-engine-primary-key":!0},lastUpdateDate:{type:"string","x-query-engine-sort":"desc","x-hidden":!0,"x-query-engine-primary-key":!0},content:{type:"string","x-hidden":!0,"x-query-engine-primary-key":!0},modelCode:{type:"string","x-component":"TableButton","x-component-props":{type:"text","@click":expression('({row}) => $edit(row, "view")')},"x-render-table-column":{title:"{{$t('dataConfMod.templateCode')}}",minWidth:130,customRender:!0}},modelName:{type:"string",title:"{{$t('contractMod.templHeadId')}}","x-render-table-column":{minWidth:150}},modelType:{type:"string",title:"{{$t('contractMod.templType')}}","x-component":"DictSelect","x-component-props":{code:"ELEM_CONTRACT_TYPE"},"x-render-table-column":{width:120}},startDate:{type:"string",title:"{{$t('basicPrice.effectiveDateFrom')}}","x-render-table-column":{width:100}},endDate:{type:"string",title:"{{$t('basicPrice.effectiveDateTo')}}","x-render-table-column":{width:100}},status:{type:"string",title:"{{$t('contractMod.status')}}","x-component":"DictSelect","x-component-props":{code:"CONTRACT_MODEL_STATUS",showType:"statusCol",statusList:{green:["VALID"],red:[],orange:["INVALID"],invalid:["FREEZE"]}},"x-render-table-column":{width:100}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:170,fixed:"right",showOverflow:!1},"x-component":"RenderTableButtonList","x-component-props":{max:2},properties:{copy:{type:"void",title:"{{$t('common.copy')}}","x-component-props":{"@click":expression("(row) => $copy(row, $queryEngine)")}},preview:{type:"void",title:"{{$t('common.preview')}}","x-component-props":{"@click":expression("({ row }) => $preview(row)")}},edit:{type:"void",title:"{{$t('common.edit')}}","x-component-props":{"@click":expression('({ row }) => $edit(row, "edit")')},"x-reactions":changeFieldVisibleByDeps([".status"],"['DRAFT', 'FREEZE'].includes($deps[0])")},active:{type:"void",title:"{{$t('common.active')}}","x-component-props":{"@click":expression(`({ row })=>
                    $queryEngine.request.update({
                      modelHeadId: row.modelHeadId,
                      status: 'VALID'
                    }).then(() => {
                      $queryEngine.state.paginationManagement.refresh()
                    })
                  `)},"x-reactions":changeFieldVisibleByDeps([".status"],"['DRAFT', 'FREEZE'].includes($deps[0])")},inactive:{type:"void",title:"{{$t('common.inactive')}}","x-component-props":{"@click":expression("({row}) => $inactive(row, $queryEngine)")},"x-reactions":changeFieldVisibleByDeps([".status"],"['VALID'].includes($deps[0])")},freeze:{type:"void",title:"{{$t('contractMod.freeze')}}","x-component-props":{"@click":expression("({row}) => $freeze(row, $queryEngine)")},"x-reactions":changeFieldVisibleByDeps([".status"],"['VALID'].includes($deps[0])")},delete:{type:"void",title:"{{$t('common.delete')}}","x-reactions":changeFieldVisibleByDeps([".status"],"['DRAFT'].includes($deps[0])"),"x-component-props":{popconfirm:{title:i18nExpression("common.confirmDeleteRow")},"@click":expression("({row}) => $delete(row, $queryEngine)")}}}}})}}}}),{emitTabAdd,t}=usePageHelper(),$preview=row=>{emitTabAdd({component:Preview,params:{row},title:t("common.preview")+`${row.modelName?"-"+row.modelName:""}`,name:`preview_${row.modelHeadId?row.modelHeadId:""}`})},$edit=(row,flag)=>{let tab={component:Edit,params:{row,flag},title:t("common.edit")+`${row.modelName?"-"+row.modelName:""}`,name:`${flag}_${row.modelHeadId?row.modelHeadId:""}`};flag==="add"&&(tab.title=t("common.add"),tab.name="add"),flag==="view"&&(tab.title=t("common.view")),emitTabAdd(tab)},$inactive=(row,queryEngine)=>{let obj={modelHeadId:row.modelHeadId,status:"INVALID"};queryEngine.request.update(obj).then(()=>{queryEngine.state.paginationManagement.refresh()})},$freeze=(row,queryEngine)=>{let obj={modelHeadId:row.modelHeadId,status:"FREEZE"};queryEngine.request.update(obj).then(()=>{queryEngine.state.paginationManagement.refresh()})},$delete=(row,queryEngine)=>{queryEngine.request.delete(row.modelHeadId).then(()=>{queryEngine.state.paginationManagement.refresh()})},$copy=(row,queryEngine)=>{queryEngine.request.read(row.row.modelHeadId,{query:{"*":{}}}).then(res=>{const data=res.data[0];data.status="DRAFT",delete data.modelHeadId,queryEngine.request.create(data).then(()=>{queryEngine.state.paginationManagement.refresh()})})};return{__sfc:!0,schema,emitTabAdd,t,$preview,$edit,$inactive,$freeze,$delete,$copy,scope:{$preview,$edit,$inactive,$freeze,$delete,$copy},RenderEngine}}});var _sfc_render$1=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{schemaKey:"contractModeList",pageAttrs:_vm.$attrs,schema:_setup.schema,scope:_setup.scope}})},_sfc_staticRenderFns$1=[],__component__$1=normalizeComponent(_sfc_main$1,_sfc_render$1,_sfc_staticRenderFns$1,!1,null,null,null,null);const contractTemplateList=__component__$1.exports,_sfc_main={name:"ContractTemplateManagement",components:{NavTabs},data(){return{activeTab:"contractTemplateList",tabs:[{title:this.$t("contractMod.contractTemplateList"),name:"contractTemplateList",component:contractTemplateList,closable:!1}],currentTab:null}},activated(){this.currentTab==="contractTemplateList"&&this.dolayout()},methods:{dolayout(){this.$nextTick(()=>{const data={name:"contractTemplateList",methods:"dolayout",params:null,random:Math.random()};this.$store.commit("navTabs/SET_NAV_TABS_TODO",data)})},tabChange(tab){tab==="contractTemplateList"&&this.dolayout(),this.currentTab=tab}}};var _sfc_render=function(){var _vm=this,_c=_vm._self._c;return _c("nav-tabs",{ref:"tabs",attrs:{"tabs-list":_vm.tabs,"cur-tab":_vm.activeTab},on:{"tab-change":_vm.tabChange}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const index=__component__.exports;export{index as default};
