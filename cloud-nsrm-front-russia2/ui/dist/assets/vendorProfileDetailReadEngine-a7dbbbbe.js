import{n as normalizeComponent,ak as defineComponent,al as usePageHelper,am as useAttrs,an as ref$1,ao as useAutoMountInstanceToField,ap as h,aq as defineSchemas,ad as expression,ae as i18nExpression,aC as generateCharExpressionByFunction,as as performPlanService,at as DictSelect,au as observer,ac as createDictClass,ar as RenderEngine,cK as getHeaderField,av as CommonFile,a6 as CCategorySelect}from"./index-6b6051d8.js";import{u as userInfoForm,c as companyType,a as companyInfo,b as companyBaseInfo,p as personBaseInfo,d as contactInfoList,s as serviceRange,v as vendorSiteInfoList,e as authInfo,r as relationSuppliers,q as qualificationInformation}from"./vendorProfileDetailReadEngine.vue_vue_type_style_index_0_lang-d2c087b5.js";import{C as CAddress}from"./index-baa5f2f5.js";import{F as FileDynamic}from"./file-dynamic-25a093c4.js";import{C as CFillProgress}from"./index-2c71d18e.js";import{d as detail}from"./edit-bd7f1b0f.js";import{r as relationSuppliersDetail}from"./detail-372c31a6.js";import{s as sceneFileApi}from"./basicSetting-fc46a2d9.js";import{t as transformMQL}from"./util-d962b17f.js";/* empty css                                                              */import"./file-dynamic-7fc2d358.js";import"./MainHeader-2d842985.js";/* empty css                                                                   */import"./file-dynamic.vue_vue_type_style_index_0_scoped_516fdfc3_lang-f2c1a82d.js";import"./tree-utils-7df6be59.js";import"./BaseTableBind-b1f76fc9.js";import"./util-1e55288f.js";import"./index-4d512f00.js";/* empty css                                                                      */import"./fileApi-05bbbbcc.js";/* empty css                                              */import"./edit.vue_vue_type_style_index_0_lang-d3e478c4.js";import"./supApi-98b2a23d.js";import"./vendorManagement-96246714.js";const _sfc_main$1={name:"Note",props:{title:{type:String,default:""},value:{type:String,default:""},readonly:{type:Boolean,default:!1}},data(){return{visible:!1,newValue:this.value}},watch:{value(newValue,oldValue){newValue!==oldValue&&(this.newValue=newValue)}},methods:{valueChange(value){this.$emit("change",value)}}};var _sfc_render$1=function(){var _vm=this,_c=_vm._self._c;return _c("div",{staticClass:"collapse-item-note"},[_c("span",[_vm._v(_vm._s(_vm.title))]),_c("el-popover",{attrs:{placement:"top",width:"400",trigger:"manual"},model:{value:_vm.visible,callback:function($$v){_vm.visible=$$v},expression:"visible"}},[_c("el-input",{attrs:{type:"textarea",maxlength:"100",disabled:_vm.readonly},on:{change:_vm.valueChange},model:{value:_vm.newValue,callback:function($$v){_vm.newValue=$$v},expression:"newValue"}}),_c("el-button",{attrs:{slot:"reference",type:"text"},slot:"reference"},[_vm._v(" "+_vm._s(_vm.$t("cusEntry.common.note"))+" ")])],1)],1)},_sfc_staticRenderFns$1=[],__component__$1=normalizeComponent(_sfc_main$1,_sfc_render$1,_sfc_staticRenderFns$1,!1,null,null,null,null);const Note=__component__$1.exports,_sfc_main=defineComponent({__name:"vendorProfileDetailReadEngine",setup(__props){const{app,emitTabRemove,t,vendor,emitTabAdd,http}=usePageHelper(),attrs=useAttrs(),workflowStatus=ref$1("DRAFT"),$disabled=!0,newAddress=defineComponent({name:"newAddress",props:CAddress.props,setup(props,{listeners,attrs:attrs2,slots}){return useAutoMountInstanceToField(),()=>h(CAddress,{props:{...attrs2,...props},on:listeners,ref:"address"},slots)}}),query={"*":{},contactInfos:{"*":{}},orgCategorys:{"*":{}},orgInfos:{"*":{}},operationInfo:{"*":{}},fileUploads:{"*":{}},supplierLeaderList:{"*":{}},companyAddressInfos:{"*":{}},managementAttaches:{"*":{}},cateJournalList:{"*":{},npmSerciceCustoms:{"*":{}}},userInfoList:{"*":{}},npmCompanyExceptionInfos:{"*":{},$condition:{$strictQuery:!1,filter:{deleteFlag:{eq:"N"}}}}},$managementChange=(value,name,$form)=>{if(value){let data=$form.query(".managementAttaches").take().value;if(value=="Y"){let bold=1;data.forEach(e=>{e.documentInspection==name&&(bold=0)}),bold&&data.unshift({documentInspection:name,managementAttachId:null,managementInfoId:null,companyId:null,fileuploadId:null,authType:"",authDescription:"",authNum:"",authDate:"",authOrg:"",endDate:""})}else data.forEach((e,index)=>{e.documentInspection==name&&data.splice(index,1)});$form.query(".managementAttaches").take().value=data}},$ifPersonal=$form=>{$form.query(".personal").take().visible=!0,$form.query(".companyTypeAll").take().visible=!1,$form.query(".companyInfo").take().visible=!1,$form.query(".companyBaseInfo").take().visible=!1,$form.query(".factoryInfo").take().visible=!1,$form.query(".operatingPerformancesList").take().visible=!1,$form.query(".overallStrengthList").take().visible=!1,$form.query(".rdCapableList").take().visible=!1,$form.query(".qualityControlList").take().visible=!1,$form.query(".equipmentInformationList").take().visible=!1,$form.query(".productCapableInfosList").take().visible=!1,$form.query(".ceeaAfterSalesAbilityList").take().visible=!1,$form.query(".clientStatus").take().visible=!1,$form.query(".managementInfoList").take().visible=!1,$form.query(".fileUploadsList").take().visible=!1,$form.query(".questSupplier").take().visible=!1},schema=defineSchemas({state:{type:"void","x-component":"Fragment","x-hidden":!0,"x-data":{companyId:attrs.params.companyId||null,userType:"",progressData:expression("$nodeList($form.query('state').get('data').userType)"),activeNavIndex:0}},CompanyInfo:{type:"void","x-decorator":"QueryEngine","x-component":"el-container","x-component-props":{class:"flex-container vendorGreen",direction:"vertical"},"x-query-engine":{service:"sup",actions:{query:{immediate:!0,loading:!0,ready:expression(`() => {
            initButtonConfig($form)
            return $attrs.params && $attrs.params.companyId
          }`),method:"read",autoFormatResult:!1,transformRequest:expression(`(data, headers) => {
            data.tree = true
            data.query = query
            $form.query('state').get('data').companyId = $attrs.params.companyId
            data.payload = {
              "filter": {
                  "companyId": {
                      eq: $attrs.params.companyId
                  }
              }
            }
            return data
          }`),transformResponse:expression(`(res) => {
            const data = JSON.parse(res).data.records[0]
            const {
              userInfoList,
              cateJournalList,
              ...other
            } = data
            $form.setValues({
              ...other
            })
            // 获取关联供应商数据
            http({
              url: '/api-sup/api-ql/RelationSupBuyer/query',
              method: 'POST',
              data: transformMQL.listPageData({
                type: 'RelationSupBuyer',
                params: { vendorIdA: other.companyId },
                filterOperator: { vendorIdA: 'eq' },
                query: { '*': {} },
                action: 'query',
                pageNum: 1,
                pageSize: 10000
              }),
              loading: true
            }).then(res => {
              if ($form.query('relationSuppliersList').take()) {
                $form.query('relationSuppliersList').take().value = res?.data?.records || []
              }
              $form.values.relationSuppliersList = res?.data?.records || []
            })

            const serviceRange = cateJournalList.map(item => {
              const {
                npmSerciceCustoms,
                ...form
              } = item
              return {
                list: npmSerciceCustoms,
                tableForm: form
              }
            })
            $form.query('CompanyInfo').get('data').totalServiceRangeList = serviceRange
            $form.query('serviceRangeList').take().value = serviceRange.slice(0, 10)
            $form.values.userInfo = userInfoList
            $form.query('state').get('data').userType = other.overseasRelation
             if (other.overseasRelation === 'PERSONAL') {
              /* 获取个人信息, 后面优化个人信息赋值 */
              const {
                companyName,
                companyShortName,
                businessLicense,
                businessLicenseFileId,
                extIdCardOppositeFileName,
                extIdCardOppositeFileId,
                idNumber,
                businessScope,
                lcCode,
                enterpriseNo,
                companyCountry,
                companyProvince,
                companyCity,
                companyAddress,
                businessStartDate,
                businessEndDate
              } = other
              $form.values.personBaseInfo = { 
                companyName, 
                companyShortName,
                businessLicense,
                businessLicenseFileId,
                extIdCardOppositeFileName,
                extIdCardOppositeFileId,
                idNumber,
                validityPeriodOfCard: businessStartDate ? [businessStartDate, businessEndDate] : [],
                businessScope,
                lcCode,
                enterpriseNo,
                companyCountry,
                companyProvince,
                companyCity,
                companyAddress
              }
            }
            setTimeout(() => {
              $form.query('fileUploads').take(field => {
                field.visible = true
                field.componentProps.componentInstance.reLoadFileInfo()
              })
            }, 1000)
            setTimeout(() => {
              $addScrollEvent($form)
              updateButtonConfig($form)
            }, 1000)
            
            return data
          }`)}}},"x-data":{totalServiceRangeList:[],flowData:{taskName:""}},properties:{SchemaWorkflow:{type:"void","x-component":"SchemaWorkflow","x-component-props":{"business-id":expression("$form.values.companyId || null"),"business-type":"InviteVendor","button-custom":expression("{}"),showTopBtn:expression("!['view'].includes($attrs.params.flag)"),viewType:expression("['startFileApproval', 'approval', 'view'].includes($attrs.params.flag) ? 'WORKFLOW' : 'SINGLE'"),showUnpass:expression("['法务部/Юридический отдел', '财务部/Финансовый отдел', '安全部/Отдел безопасности', '区域负责人/Региональный менеджер по России', '总经理/Генеральный директор'].includes($form.query('state').get('data').flowData?.taskName)"),beforeApprove:expression(`(flowData, type) => {
            return new Promise((rs, rj) => {
              if (['法务部/Юридический отдел', '财务部/Финансовый отдел', '安全部/Отдел безопасности', '区域负责人/Региональный менеджер по России', '总经理/Генеральный директор'].includes(flowData.taskName)) {
                let submitData = {
                  companyId: $form.values.companyId
                }
                let res = type == 'approveNo' ? 'N' : 'Y'
                switch (flowData.taskName) {
                  case '法务部/Юридический отдел':
                    submitData.extLegalOpinion = res
                    break
                  case '财务部/Финансовый отдел':
                    submitData.extFinancialOpinion = res
                    break
                  case '安全部/Отдел безопасности':
                    submitData.extSecurityAndRiskOpinion = res
                    break
                  case '区域负责人/Региональный менеджер по России':
                  case '总经理/Генеральный директор':
                    submitData.extIfFinalApprove = res
                    break
                }
                app.$http({
                  url: '/api-sup/pj/companyInfo/updateApprovingOpinion',
                  method: 'POST',
                  data: submitData,
                  loading: true
                }).then(res => {
                  rs(true)
                }).catch(err => {
                  rs(false)
                })
              } else {
                rs(true)
              }
            })
          }`),"@click-handler":expression(`(type) => {
            $startApprovalHandle(type, $form, $bus)
          }`),"@submit-direct":expression(`(type) => {
            $startApprovalHandle(type, $form, $bus)
          }`),"@confirm":expression(`(type, comment) => {
            $startApprovalHandle(type, $form, $bus)
          }`),"@close-tab":expression(`() => {
            $back($bus)
          }`),"@update-integration-mode":expression("(integrationMode) => {}"),"@updateFlowData":expression(`flowData => {
            $form.query('state').get('data').flowData = flowData
          }`)},properties:{layout:{type:"void","x-component":"FormContainer",items:{type:"object",properties:{close:{type:"void","x-content":i18nExpression("common.close"),"x-component":"Button","x-component-props":{type:"default","@click":expression(`() => {
                      $back($bus)
                    }`)}},getMdmCode:{type:"void","x-visible":generateCharExpressionByFunction(`({ $form }) => {
                    return $form.values.status === 'APPROVED' && !$form.values.companyCode && $attrs.params.flag === 'approval'
                  }`),"x-content":i18nExpression("cusEntry.common.getMdmCode"),"x-component":"Button","x-component-props":{type:"primary","@click":expression(`() => {
                      http({
                        url: '/api-sup/pj/companyInfo/getMdmCodeByCompanyId',
                        method: 'GET',
                        params: {
                          companyId: $form.values.companyId
                        },
                        loading: true
                      }).then(res => {
                        $message.success($t('cusEntry.tipMessage.getMdmCodeSuccess'))
                        $back($bus)
                      })
                    }`)}},submit:{type:"void","x-content":i18nExpression("common.toApprove"),"x-component":"Button","x-visible":expression("$attrs.params.flag === 'passRegister'"),"x-component-props":{"@click":expression(`() => {
                      let values = {
                        companyId: $form.values.companyId
                      }
                      $queryEngine.request.save(values, { query: { '*':{} }, action: 'approve' }).then((res) => {
                        $message.success($t('purchaseDemand.confirm'))
                        $back($bus)
                      })
                    }`)}},refuse:{type:"void","x-visible":expression("$attrs.params.flag === 'passRegister'"),"x-content":i18nExpression("purchaseDemand.refuse"),"x-component":"Button","x-component-props":{"@click":expression(`async () => {
                      $form.query('rejectDialog').take().setComponentProps({
                        visible: true
                      })
                      setTimeout(() => {
                        $form.query('reason').take().value = $getNoteValue($form.values)
                      })
                    }`)}}}},properties:{collapse:{type:"void","x-component":"Collapse","x-component-props":{defaultOpenPanelCount:1,class:"scroll-area"},properties:{...userInfoForm,...companyType,...companyInfo,...companyBaseInfo,...personBaseInfo,...contactInfoList,...serviceRange,...vendorSiteInfoList,...authInfo,...relationSuppliers,...qualificationInformation,fileUploadsList:{type:"void","x-component":"CollapseItem","x-component-props":{title:`{{observer({
                        render(h) {
                          return h($$components.Note, {
                            props: {
                              title: t('vendorMod.sceneAttachmentInfo2'),
                              value: $form.values.extRejectAttribute11,
                              readonly: !($form.values.status === 'SUBMITTED' && $form.values.dataSources !== 'MANUALLY_CREATE' && $attrs.params.flag === 'approval')
                            },
                            on: {
                              change: value => {
                                $form.values.extRejectAttribute11 = value
                              }
                            }
                          })
                        }
                      })}}`},"x-query-engine-skip":!0,"x-visible":generateCharExpressionByFunction(({$attrs})=>["approval","view"].includes($attrs.params.flag)),properties:{fileUploads:{"x-query-engine-relation":"fileUploads:*",type:"array","x-component":"FileDynamic","x-component-props":{"scene-module-code":"SCENE_SUPPLIER_ATTACHMENT",businessId:expression("$attrs.params.companyId || null"),editable:expression("!$disabled"),"need-init":!1}}}}}}}},CFillProgress:{type:"void","x-component":"CFillProgress","x-component-props":{activeNavIndex:expression("$form.query('state').get('data').activeNavIndex"),class:"contract-progress",ref:"contractProgress",nodeName:i18nExpression("logisticsMod.contractInfo"),data:expression("$form.query('state').get('data').progressData"),percentage:"{{true}}","@index-click":`{{ (code) => {
                  let anchorEle = document.querySelector('#collapse_' + code)
                  if (anchorEle) {
                    anchorEle.scrollIntoView(true)
                  }
              } }}`}},rejectDialog:{type:"void","x-component":"RDialog","x-component-props":{title:i18nExpression("cusEntry.vendorMod.reject"),size:"middle",footer:!0,"close-on-click-modal":!1,cancelText:i18nExpression("common.cancel"),beforeClose:expression(`(done, type, closeLoading) => {
                if ( type === 'ok') {
                  $form.validate('CompanyInfo.rejectDialog.reason').then(res => {
                    let noteObj = $getNoteObj($form.values)
                    let values = {
                      flowRemark: $self.query('reason').take().value,
                      companyId: $form.values.companyId,
                      ...noteObj
                    }
                    $queryEngine.request.save(values, { query: { '*':{} }, action: 'reject' }).then((res) => {
                      done()
                      $message.success($t('bidMod.toRefuseSuccess'))
                      $back($bus)
                    })
                  }).finally(() => {
                    closeLoading()
                  })
                } else {
                  done()
                }
              }`)},properties:{reason:{type:"string","x-decorator":"FormItem",title:i18nExpression("cusEntry.vendorMod.rejectReasonNote"),"x-component-props":{type:"textarea",maxlength:1e3,rows:5},"x-validator":{required:!0,message:i18nExpression("cusEntry.tipMessage.rejectReason")}}}}}}}}}),$nodeList=userType=>{let nodeList1=[{code:"relationSuppliers",name:t("cusEntry.vendorMod.relationSuppliers"),percentage:0}],nodeList2=[{code:"qualificationInformation",name:t("cusEntry.vendorMod.qualificationInformation"),percentage:0},{code:"fileUploadsList",name:t("vendorMod.sceneAttachmentInfo2"),percentage:0}],company=[{code:"userInfoForm",name:t("vendorMod.vendorUserInfo"),percentage:0},{code:"companyTypeAll",name:t("vendorMod.companyType"),percentage:0},{code:"companyInfo",name:t("vendorMod.enterpriseThreeCertificates"),percentage:0},{code:"companyBaseInfo",name:t("vendorMod.companyBaseInfo"),percentage:0},{code:"contactInfoList",name:t("vendorMod.contactInfo"),percentage:0},{code:"serviceRange",name:t("cusEntry.vendorMod.serviceRange"),percentage:0},{code:"vendorSiteInfo",name:t("vendorMod.vendorSiteInfos2"),percentage:0},{code:"authInfo",name:t("cusEntry.vendorMod.authInfo"),percentage:0}],person=[{code:"userInfoForm",name:t("vendorMod.vendorUserInfo"),percentage:0},{code:"person",name:t("cusEntry.vendorMod.baseInfo"),percentage:0},{code:"contactInfoList",name:t("vendorMod.contactInfo"),percentage:0},{code:"serviceRange",name:t("cusEntry.vendorMod.serviceRange"),percentage:0}];return["passRegister"].includes(attrs.params.flag)?(company=company.concat(nodeList1),person=person.concat(nodeList1)):["startFileApproval","approval"].includes(attrs.params.flag)?(company=company.concat(nodeList2),person=person.concat(nodeList2)):(company=company.concat(nodeList1).concat(nodeList2),person=person.concat(nodeList1).concat(nodeList2)),userType==="PERSONAL"?person:company},$back=$bus=>{emitTabRemove(attrs.tabName),$bus.$emit("green")},$showSunFile=$self=>{const fileList=[];if($self.value){const fileIdList=$self.value?.split(","),fileNameList=$self.query(".sunshineFileName").get("value")?.split(",");fileIdList.forEach((item,index)=>{fileList.push({fileId:item,fileName:fileNameList?.[index]})})}$self.setComponentProps({fileList})},CollapseItemTitle={functional:!0,render(h2){return h2("div",{style:{display:"flex",justifyContent:"space-between",alignItems:"center"}},[h2("span",{},[t("vendorMod.vendorUserInfo")]),h2("el-button",{props:{type:"text"},on:{click:event=>{event.stopPropagation()}}},[t("cusEntry.common.note")])])}},$getNoteValue=values=>{let resultString="",resultMap=new Map;for(let i=1;i<11;i++)values[`extRejectAttribute${i}`]&&resultMap.set($noteType.get(`extRejectAttribute${i}`),values[`extRejectAttribute${i}`]);if(resultMap.size)for(let[key,value]of resultMap)resultString=`${resultString}${key}:${value}
`;return resultString},$noteType=new Map([["extRejectAttribute1",t("vendorMod.companyType")],["extRejectAttribute2",t("vendorMod.enterpriseThreeCertificates")],["extRejectAttribute3",t("vendorMod.companyBaseInfo2")],["extRejectAttribute4",t("vendorMod.contactInfo")],["extRejectAttribute5",t("vendorMod.bankInfo")],["extRejectAttribute6",t("cusEntry.vendorMod.financeReport")],["extRejectAttribute7",t("vendorMod.companySize")],["extRejectAttribute8",t("cusEntry.vendorMod.serviceRange")],["extRejectAttribute9",t("cusEntry.vendorMod.qualificationInformation")],["extRejectAttribute10",t("cusEntry.vendorMod.authInfo")],["extRejectAttribute11",t("vendorMod.sceneAttachmentInfo2")]]),$getNoteObj=values=>{let noteObj={};for(let i=1;i<11;i++)values[`extRejectAttribute${i}`]&&(noteObj[`extRejectAttribute${i}`]=values[`extRejectAttribute${i}`]);return noteObj},$addScrollEvent=$form=>{const navNodes=$nodeList($form.query("state").get("data").userType),offsetTopArr=[];navNodes.forEach(node=>{const element=document.getElementById(`collapse_${node.code}`);offsetTopArr.push(element.offsetTop)}),window.addEventListener("scroll",$throttle($scrollHandler,100,$form,offsetTopArr),!0)},$throttle=(fn,delay,$form,offsetTopArr)=>{let timer=null;return()=>{timer||setTimeout(()=>{fn($form,offsetTopArr),clearTimeout(timer),timer=null},delay)}},$scrollHandler=($form,offsetTopArr)=>{const scrollTop=document.getElementsByClassName("render-form-container")[0].scrollTop;let navIndex=0;offsetTopArr.findIndex(item=>item>=scrollTop);for(let n=0;n<offsetTopArr.length;n++)scrollTop>=offsetTopArr[n]&&(navIndex=n);$form.query("state").get("data")&&($form.query("state").get("data").activeNavIndex=navIndex)},initButtonConfig=$form=>{let isView=["view"].includes(attrs.params.flag);setTimeout(()=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.buttonConfigInfo.save.view=!1,componentInstance.buttonConfigInfo.submit.view=["startFileApproval"].includes(attrs.params.flag),componentInstance.buttonConfigInfo.submit.name=t("cusEntry.supplement20250211.submitFileApprove"),componentInstance.buttonConfigInfo.cancel.view=!isView,componentInstance.buttonConfigInfo.close.view=isView},50)},updateButtonConfig=$form=>{let isView=["view"].includes(attrs.params.flag);setTimeout(()=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.buttonConfigInfo.save.view=!1,componentInstance.buttonConfigInfo.submit.view=["startFileApproval"].includes(attrs.params.flag),componentInstance.buttonConfigInfo.submit.name=t("cusEntry.supplement20250211.submitFileApprove"),componentInstance.buttonConfigInfo.cancel.view=!isView,componentInstance.buttonConfigInfo.close.view=isView,componentInstance.setWorkflowBusinessId($form.values.companyId),getHeaderField($form.values),componentInstance.setWorkflowBusinessVariables({procTitleObj:{companyCode:$form.values.companyCode}})},50)},$startApprovalHandle=(type,$form,$bus)=>{if(type!="SUBMIT")return;const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.setWorkflowBusinessId($form.values.companyId),componentInstance.setWorkflowTabDisabled(!1),getHeaderField($form.values),componentInstance.setWorkflowBusinessVariables({procTitleObj:{companyCode:$form.values.companyCode}}),componentInstance.handlerAfter(type.toUpperCase(),()=>{$back($bus)})},scope={app,t,http,$attrs:attrs,initButtonConfig,updateButtonConfig,performPlanService,$disabled,emitTabRemove,$back,DictSelect,observer,$managementChange,query,vendorInfoChangeDetail:detail,emitTabAdd,$ifPersonal,$nodeList,sceneFileApi,$showSunFile,CollapseItemTitle,$getNoteValue,$noteType,$getNoteObj,$throttle,$addScrollEvent,$scrollHandler,transformMQL,relationSuppliersDetail,$startApprovalHandle,$taxDictClass:createDictClass({country:[]})};return{__sfc:!0,app,emitTabRemove,t,vendor,emitTabAdd,http,attrs,workflowStatus,$disabled,newAddress,query,$managementChange,$ifPersonal,schema,$nodeList,$back,$showSunFile,CollapseItemTitle,$getNoteValue,$noteType,$getNoteObj,$addScrollEvent,$throttle,$scrollHandler,initButtonConfig,updateButtonConfig,$startApprovalHandle,scope,components:{SrmCommonFile:CommonFile,CAddress,CCategorySelect,FileDynamic,CFillProgress,newAddress,Note},RenderEngine}}});var _sfc_render=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{schemaKey:"vendorProfileDetail",pageAttrs:_vm.$attrs,schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const vendorProfileDetailReadEngine=__component__.exports;export{vendorProfileDetailReadEngine as default};
