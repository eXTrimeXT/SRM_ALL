import{n as normalizeComponent,ae as expression,af as i18nExpression,aB as generateCharExpressionByFunction,ag as yearMonthDaySelectorSegment,ah as radioGroupByYOrNSegment,aC as requiredValidatorSegment,ai as generateXindexInOrder,aj as editTableFormItemValid,ak as feedbackLayoutIsPopover,al as defineComponent,am as usePageHelper,an as useAttrs,ao as ref$1,ap as useAutoMountInstanceToField,aq as h,ar as defineSchemas,as as RenderEngine,at as performPlanService,au as DictSelect,av as observer,v as validEmail,a as validatePhone,aw as CommonFile,a6 as CCategorySelect}from"./index-17d0ccd5.js";import{C as CAddress}from"./index-38ab0095.js";import{F as FileDynamic}from"./file-dynamic-30cdd411.js";import{C as CFillProgress}from"./index-6af40985.js";import{r as resolve}from"./path-4ced4e54.js";import{t as transformMQL}from"./util-a92f9f8e.js";import{v as vendorOptCommonApi}from"./index-b8c9566a.js";import{s as sceneFileApi}from"./basicSetting-f3b18103.js";/* empty css                                                              */import"./file-dynamic-ab2ff377.js";import"./MainHeader-ef959a5c.js";/* empty css                                                                   */import"./file-dynamic.vue_vue_type_style_index_0_scoped_516fdfc3_lang-e128b539.js";import"./tree-utils-7df6be59.js";import"./BaseTableBind-53264a4f.js";import"./util-6482eb24.js";import"./index-4d512f00.js";/* empty css                                                                      */import"./fileApi-3f0be128.js";/* empty css                                              */const _sfc_main$6={components:{},data(){return{}},computed:{},async created(){},mounted(){},updated(){},methods:{goTo(where){this.$emit("goToWhere",where)}}};var _sfc_render$6=function(){var _vm=this,_c=_vm._self._c;return _c("el-container",[_c("el-main",{staticClass:"main-stepOne"},[_c("section",{staticClass:"boxs"},[_c("i",{staticClass:"el-icon-warning icon-red icon-big margin"}),_c("span",{staticClass:"margin mainFont"},[_vm._v(_vm._s(_vm.$t("vendorMod.companyInformationNotCertified")))]),_c("span",{staticClass:"margin greyFont"},[_vm._v(_vm._s(_vm.$t("vendorMod.sorryCompanyInformationNotCertified")))]),_c("button",{staticClass:"button-click margin",on:{click:function($event){return _vm.goTo("company-nature")}}},[_vm._v(" "+_vm._s(_vm.$t("vendorMod.continueFilling"))+" ")])])])],1)},_sfc_staticRenderFns$6=[],__component__$6=normalizeComponent(_sfc_main$6,_sfc_render$6,_sfc_staticRenderFns$6,!1,null,"5b317bbc",null,null);const stepOne=__component__$6.exports;const _sfc_main$5={components:{},props:{status:{type:String,default:"APPROVED"},flowRemark:{type:String,default:""}},data(){return{source:"注册撤回"}},computed:{},async created(){},mounted(){},updated(){},methods:{monitorIpAddress(data){return this.$http({url:"/api-sou/bids/ip/address/ipAddress/save",method:"POST",data})},recall(){this.$prompt("","撤回原因",{confirmButtonText:"确定",cancelButtonText:"取消",inputType:"textarea"}).then(({value})=>{let saveData=transformMQL.save("CompanyInfo",[{companyId:this.$store.getters.userInfo.companyId,flowRemark:value}],"vendorWithdraw");vendorOptCommonApi.withdrawCompanyMQL(saveData).then(async()=>{this.$message({message:"成功撤回",type:"success"}),this.backToMain();const{companyName,companyCode,companyId}=this.$store.getters.userInfo;await this.monitorIpAddress({supplierId:companyId,supplierCode:companyCode,supplierName:companyName,source:this.source})})})},backToSystem(){let saveData=transformMQL.save("CompanyInfo",[{companyId:this.$store.getters.userInfo.companyId}],"updateFirstLog");vendorOptCommonApi.saveCompanyMQL(saveData).then(()=>{this.$store.dispatch("user/initSystem").then(()=>{this.$router.push({path:resolve("/dashboard")})})})},backToMain(){this.$emit("goToWhere","main")}}};var _sfc_render$5=function(){var _vm=this,_c=_vm._self._c;return _c("el-container",{staticClass:"successContainer"},[_vm.status=="SUBMITTED"?_c("el-main",{staticClass:"main-stepOne"},[_c("section",{staticClass:"boxs"},[_c("span",{staticClass:"success-icon margin"},[_c("i",{staticClass:"el-icon-success"})]),_c("span",{staticClass:"margin mainFont"},[_vm._v(_vm._s(_vm.$t("提交成功")))]),_c("span",{staticClass:"margin greyFont"},[_vm._v(_vm._s(_vm.$t("企业信息已提交成功请等待审批")))]),_c("section",{staticClass:"flex"},[_c("el-button",{staticClass:"margin button-click",on:{click:_vm.recall}},[_vm._v(" "+_vm._s(_vm.$t("撤回提交信息"))+" ")]),_c("el-button",{staticClass:"margin button-click",attrs:{type:"primary"},on:{click:_vm.backToMain}},[_vm._v(" "+_vm._s(_vm.$t("查看企业信息"))+" ")])],1)])]):_vm._e(),_vm.status=="APPROVED"?_c("el-main",{staticClass:"main-stepOne"},[_c("section",{staticClass:"boxs"},[_c("span",{staticClass:"success-icon margin"},[_c("i",{staticClass:"el-icon-success"})]),_c("span",{staticClass:"margin mainFont"},[_vm._v(_vm._s(_vm.$t("common.successRegister")))]),_c("span",{staticClass:"margin greyFont"},[_vm._v(_vm._s(_vm.$t("vendorMod.companyInformationSuccess")))]),_c("el-button",{staticClass:"margin button-click",attrs:{type:"primary"},on:{click:_vm.backToSystem}},[_vm._v(" "+_vm._s(_vm.$t("vendorMod.enterSystem"))+" ")])],1)]):_vm._e(),_vm.status=="REJECTED"?_c("el-main",{staticClass:"main-stepOne"},[_c("section",{staticClass:"boxs"},[_c("i",{staticClass:"el-icon-error icon-grey icon-big margin"}),_c("span",{staticClass:"margin mainFont"},[_vm._v(_vm._s(_vm.$t("common.failedRegister")))]),_c("span",{staticClass:"margin greyFont"},[_vm._v(_vm._s(_vm.$t("vendorMod.companyInformationFailed"))+_vm._s(_vm.flowRemark))]),_c("el-button",{staticClass:"margin button-click",attrs:{type:"primary"},on:{click:_vm.backToMain}},[_vm._v(" "+_vm._s(_vm.$t("vendorMod.backCompanyInformation"))+" ")])],1)]):_vm._e()],1)},_sfc_staticRenderFns$5=[],__component__$5=normalizeComponent(_sfc_main$5,_sfc_render$5,_sfc_staticRenderFns$5,!1,null,"71ff2b0d",null,null);const success=__component__$5.exports,companyNatureEngine={steps:{type:"void","x-decorator":"div","x-decorator-props":{class:"comInfosteps"},"x-component":"Steps","x-component-props":{alignCenter:!0,finishStatus:"success"},"x-reactions":{dependencies:["status"],fulfill:{state:{"component[1].active":2}}},"x-visible":!1,properties:{step1:{type:"void","x-component":"el-step","x-component-props":{title:expression("$t('vendorMod.registrationPolicy')")}},step2:{type:"void","x-component":"el-step","x-component-props":{title:expression("$t('vendorMod.registerAccount')")}},step3:{type:"void","x-component":"el-step","x-component-props":{title:expression("$t('vendorMod.registrationType')")}},step4:{type:"void","x-component":"el-step","x-component-props":{title:expression("$t('vendorMod.authenticationInformation')")}},step5:{type:"void","x-component":"el-step","x-component-props":{title:expression("$t('vendorMod.pendingApproval')")}},step6:{type:"void","x-component":"el-step","x-component-props":{title:expression("$t('vendorMod.informationAuthentication')")}}}},formCompanyNature:{type:"object","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical",class:"boxs-row"},"x-component":"FormGrid","x-component-props":{maxColumns:1,columnGap:32,rowGap:0},"x-query-engine-skip":!0,properties:{overseasRelation:{type:"string",default:"INSIDE","x-decorator":"FormItem","x-component":"natureChose","x-component-props":{style:"margin:18px 0 15px 0;","@change":expression(`(who) => {
            $self.value = who
          }`)},title:expression("$t('vendorMod.overseasRelation')"),"x-validator":{required:!0,message:i18nExpression("common.requiredField")}}}}};const _sfc_main$4={components:{},props:{stepsActive:{type:Number},status:{type:String,default:"DRAFT"},flowRemark:{type:String},registered:{type:Boolean,default:!1}},data(){return{}},computed:{setFlowRemark(){const replaceRemark=this.flowRemark?.replace(/\s+/g,"; ")??"";return replaceRemark?replaceRemark.includes(";")?replaceRemark.slice(0,replaceRemark.length-2):replaceRemark:""}},async created(){},mounted(){},updated(){},methods:{goTo(where){this.$emit("goToWhere",where)}}};var _sfc_render$4=function(){var _vm=this,_c=_vm._self._c;return _c("div",[_vm.registered?_vm._e():_c("el-steps",{staticClass:"comInfosteps",attrs:{active:_vm.stepsActive,"finish-status":"success","align-center":!0}},[_c("el-step",{attrs:{title:_vm.$t("vendorMod.registrationPolicy")}}),_c("el-step",{attrs:{title:_vm.$t("vendorMod.registerAccount")}}),_c("el-step",{attrs:{title:_vm.$t("vendorMod.registrationType")}}),_c("el-step",{attrs:{title:_vm.$t("vendorMod.authenticationInformation")}}),_c("el-step",{attrs:{title:_vm.$t("vendorMod.pendingApproval")}}),_c("el-step",{attrs:{title:_vm.$t("vendorMod.informationAuthentication")}})],1),_vm.status=="APPROVED"?_c("section",{staticClass:"boxs_success boxs_heater"},[_c("div",[_c("i",{staticClass:"el-icon-circle-check"}),_c("span",[_vm._v(_vm._s(_vm.$t("mainHeater.boxsHeater")))])]),_c("div",{staticClass:"small_font"},[_vm._v(" "+_vm._s(_vm.$t("mainHeater.boxsHeater2"))+" ")])]):_vm._e(),_vm.status=="REJECTED"?_c("section",{staticClass:"boxs_rejected boxs_heater"},[_c("div",[_c("i",{staticClass:"el-icon-circle-close"}),_c("span",[_vm._v(_vm._s(_vm.$t("mainHeater.reject")))])]),_c("div",{staticClass:"small_font"},[_vm._v(" "+_vm._s(_vm.$t("mainHeater.reject2"))+" ")]),_c("div",{staticClass:"small_font"},[_vm._v(" "+_vm._s(_vm.$t("cusEntry.vendorMod.rejectReason"))+_vm._s(_vm.flowRemark)+" ")])]):_vm._e(),_vm.status=="SUBMITTED"?_c("section",{staticClass:"boxs_submitted boxs_heater"},[_c("div",[_c("i",{staticClass:"el-icon-warning-outline"}),_c("span",[_vm._v(_vm._s(_vm.$t("mainHeater.approval")))])]),_c("div",{staticClass:"small_font"},[_vm._v(" "+_vm._s(_vm.$t("mainHeater.approval2"))+" ")])]):_vm._e()],1)},_sfc_staticRenderFns$4=[],__component__$4=normalizeComponent(_sfc_main$4,_sfc_render$4,_sfc_staticRenderFns$4,!1,null,"989d97c8",null,null);const MainHerder=__component__$4.exports,companyType={companyTypeAll:{type:"void","x-component":"CollapseItem","x-component-props":{title:`{{observer(
        {
          render(h) {
            return h($$components.Note, {
              props: {
                title: t('vendorMod.companyType'),
                value: $form.values.extRejectAttribute1,
                readonly: true
              }
            })
          }
        }
      )}}`},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").overseasRelation!=="PERSONAL"),"x-query-engine-skip":!0,properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:{flowRemark:{type:"string","x-decorator":"FormItem","x-hidden":!0},status:{type:"string",default:"DRAFT","x-hidden":!0},overseasRelation:{type:"string","x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"RELATION",disabled:!0},title:expression("$t('vendorMod.overseasRelation')"),"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},companyType:{type:"string","x-decorator":"FormItem","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},"x-reactions":{dependencies:["overseasRelation"],fulfill:{state:{visible:expression('$deps[0] == "INSIDE"')}}},title:expression("$t('cusEntry.vendorMod.vendorType')"),"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},extUseType:{type:"string",title:i18nExpression("cusEntry.vendorMod.extUseType"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"SUPPLIER_USE",disabled:expression("$form.query('state').get('data').$disabled")}}}}}}},companyInfo={companyInfo:{type:"void","x-component":"CollapseItem","x-component-props":{title:`{{observer(
        {
          render(h) {
            return h($$components.Note, {
              props: {
                title: t('vendorMod.enterpriseThreeCertificates'),
                value: $form.values.extRejectAttribute2,
                readonly: true
              }
            })
          }
        }
      )}}`},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").overseasRelation!=="PERSONAL"),"x-query-engine-skip":!0,properties:{div:{type:"void","x-component":"div","x-component-props":{class:"companyInfo"},properties:{businessLicense:{type:"string","x-hidden":!0},businessLicenseFileId:{type:"string","x-component":"SrmCommonFile","x-component-props":{readonly:expression("$form.query('state').get('data').$disabled"),"list-type":"picture-card",style:{width:"33%","padding-right":"25px"},defaultFile:{fileId:expression("$self.value"),fileName:expression("$form.query('businessLicense').get('value')")},"dragger-options":{width:"100%",height:"345px"},limit:1,drag:"drag","@on-change":expression(`({ file }) => {
                if (!file) {
                  $form.query('.businessLicenseFileId').take().value = null
                  $form.query('.businessLicense').take().value = null
                  return false
                }
                const { fileId, fileName } = file || {}
                // 判断是否需要OCR识别 境内供应商 && 开启OCR
                if ($form.query('.overseasRelation').take().value === 'INSIDE') {
                  // 读取图片信息
                  app.$http({
                    url: '/api-pj/ocr/recognizeLcImage',
                    method: 'GET',
                    params: { fileuploadId: fileId },
                    loading: true
                  }).then(res => {
                    const {
                      regNum,
                      person,
                      name,
                      address,
                      business,
                      businessEndDate,
                      businessStartDate,
                      capital,
                      period,
                      setDate,
                      type
                    } = res.data
                    $form.query('companyType').take().value = type
                    $form.query('.companyName').take().value = name
                    $form.query('.legalPerson').take().value = person
                    $form.query('.lcCode').take().value = regNum
                    $form.query('businessDate').take().value = [businessStartDate, businessEndDate]
                    // $form.query('.registeredCapital').take().value = licenseData.registeredCapital
                    // $form.query('.registCurrency').take().value = licenseData.registCurrency
                    $form.query('.companyAddress').take().value = address
                    $form.query('.businessScope').take().value = business
                    const [year, month, day] = setDate.replace(/[^\\d]/g, '-').split('-')
                    const createDate = year + '-' + month + '-' + day
                    $form.query('.companyCreationDate').take().value = app.$dayjs(createDate).format('YYYY-MM-DD')
                    // 校验供应商是否已注册
                    if (regNum) {
                      app.$http({
                        url: '/api-rbac/extUser/lcCodeVerify',
                        method: 'GET',
                        params: { lcCode: regNum, isPersonalAccount: 'N' }
                      }).then(res => {
                        if (res.code + '' === '0') {
                          const data = res.data
                          if (data.isRemind === 'Y') {
                            let maskedName = ''
                            if(data.nickname.length==2){
                              maskedName=data.nickname.substring(0,1)+'*'
                            }else if(data.nickname.length==3){
                              maskedName=data.nickname.substring(0,1)+"*"+data.nickname.substring(2,3)
                            }else if(data.nickname.length>3){
                              maskedName=data.nickname.substring(0,1)+"*"+'*'+data.nickname.substring(3,data.nickname.length)
                            }
                            const maskedPhone = data.phone.replace(data.phone.substring(3,7),'****')
                            const content = '该供应商已被注册，请联系' + maskedName + '，账号：' + maskedPhone + '，进行子账号维护操作。'
                            app.$alert(content, '', {
                              confirmButtonText: '确定',
                              callback: action => {}
                            });
                          }
                        }
                      })
                    }
                  })
                  .catch(err => {
                    console.log(err)
                  })
                }
                $form.query('.businessLicenseFileId').take().value = fileId.toString()
                $form.query('.businessLicense').take().value = fileName
              }`)},title:""},layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical",style:{width:"67%","padding-left":"20px"}},"x-component":"FormGrid","x-component-props":{maxColumns:2,columnGap:32,rowGap:0},properties:{companyName:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.companyName')"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled || !$form.values.businessLicenseFileId")},"x-validator":{required:!0,message:i18nExpression("vendorMod.msgCompanyName")}},legalPerson:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.legalPerson')"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled || !$form.values.businessLicenseFileId")},"x-validator":{required:!0,message:i18nExpression("vendorMod.msgLegalPerson")}},lcCode:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.lcCode')"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled || !$form.values.businessLicenseFileId"),"@blur":expression(`(value) => {
                    if ($self.value) {
                      app.$http({
                        url: '/api-rbac/extUser/lcCodeVerify',
                        method: 'GET',
                        params: { lcCode: $self.value, isPersonalAccount: 'N' }
                      }).then(res => {
                        if (res.code + '' === '0') {
                          const data = res.data
                          if (data.isRemind === 'Y') {
                            let maskedName = ''
                            if(data.nickname.length==2){
                              maskedName=data.nickname.substring(0,1)+'*'
                            }else if(data.nickname.length==3){
                              maskedName=data.nickname.substring(0,1)+"*"+data.nickname.substring(2,3)
                            }else if(data.nickname.length>3){
                              maskedName=data.nickname.substring(0,1)+"*"+'*'+data.nickname.substring(3,data.nickname.length)
                            }
                            const maskedPhone = data.phone.replace(data.phone.substring(3,7),'****')
                            const content = '该供应商已被注册，请联系' + maskedName + '，账号：' + maskedPhone + '，进行子账号维护操作。'
                            app.$alert(content, '', {
                              confirmButtonText: '确定',
                              callback: action => {}
                            });
                          }
                        }
                      })
                    }
                  }`)},"x-validator":{required:!0,message:i18nExpression("vendorMod.msgLcCode")}},registCurrency:{type:"string","x-hidden":!0},registeredCapital:{type:"number","x-visible":expression("$form.query('.companyType').take().value != 'GETI'"),"x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.registeredCapital')"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled || !$form.values.businessLicenseFileId"),class:"input-with-select","@change":expression(`(value) => {
                    $self.value = value.replace(/[^\\d.]/g, '')
                  }`)},"x-content":{append:expression(`observer(
                    {
                      render(h) {
                        const targetField = $self.query('.registCurrency').take()
                        return h("div", {class: "bzBox"}, [
                          h("label", {class: "bzTitle"}, "币种"),
                            h(DictSelect, {
                              props: {
                                value: targetField.value,
                                code: 'currency',
                              },
                              attrs: {
                                disabled: $form.query('state').get('data').$disabled || !$form.values.businessLicenseFileId,
                              },
                              on: {
                                'change-value': (value) => {
                                  targetField.value = value
                                }
                              }
                            }),
                          ])
                        }
                      }
                    )
                  `)},"x-validator":{required:expression("!['GETI','FEIYINGLI'].includes($form.query('.companyType').take().value)"),message:i18nExpression("vendorMod.msgRegisteredCapital")}},companyCreationDate:{type:"date",default:null,"x-decorator":"FormItem","x-component-props":{placeholder:i18nExpression("common.pleaseSelectDate"),format:"yyyy-MM-dd","value-format":"yyyy-MM-dd",disabled:expression("$form.query('state').get('data').$disabled || !$form.values.businessLicenseFileId")},title:expression("$t('vendorMod.creationDate')"),"x-validator":{required:expression("!['FEIYINGLI'].includes($form.query('.companyType').take().value)"),message:i18nExpression("vendorMod.msgCreationDate")}},businessStartDate:{type:"date","x-hidden":!0},businessEndDate:{type:"date","x-hidden":!0},ifLongPeriod:{type:"string",title:i18nExpression("cusEntry.vendorMod.ifLongTermSupplier"),"x-component":"DictSelect","x-component-props":{code:"YES_OR_NO",disabled:expression("$form.query('state').get('data').$disabled || !$form.values.businessLicenseFileId")},default:"N","x-decorator":"FormItem","x-validator":{required:!0,message:i18nExpression("cusEntry.tipMessage.ifLongPeriodMsg")}},businessDate:{type:"string","x-component":"DatePicker","x-decorator":"FormItem","x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],type:"daterange",disabled:expression("$form.query('state').get('data').$disabled || !$form.values.businessLicenseFileId")},title:expression("$t('vendorMod.dateBusiness')"),"x-validator":{required:expression("$form.query('ifLongPeriod').take().value === 'N'"),message:i18nExpression("vendorMod.msgCreationDate")}},companyShortName:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.companyShortName')"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),maxlength:100,"show-word-limit":!0}},companyEnName:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('cusEntry.vendorMod.companyEnName')"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},businessScope:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.businessScope')"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),type:"textarea",maxlength:2e3},"x-decorator-props":{gridSpan:3}}}}}}}}},companyBaseInfo={companyBaseInfo:{type:"void","x-component":"CollapseItem","x-component-props":{title:`{{observer(
        {
          render(h) {
            return h($$components.Note, {
              props: {
                title: t('vendorMod.companyBaseInfo2'),
                value: $form.values.extRejectAttribute3,
                readonly: true
              }
            })
          }
        }
      )}}`},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").overseasRelation!=="PERSONAL"),"x-query-engine-skip":!0,properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:{ceeaBusinessModel:{type:"string","x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),code:"BIZ_MODEL",multiple:!0},title:expression("$t('vendorMod.bizModel')")},ceeaIfListed:{title:i18nExpression("vendorMod.ifListed"),...radioGroupByYOrNSegment,"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},ceeaListedTime:{type:"date",default:null,"x-visible":"{{$form.query('.ceeaIfListed').take().value == 'Y'}}","x-decorator":"FormItem","x-component-props":{placeholder:i18nExpression("common.pleaseSelectDate"),format:"yyyy-MM-dd","value-format":"yyyy-MM-dd",disabled:expression("$form.query('state').get('data').$disabled")},title:expression("$t('vendorMod.listedDate')"),"x-validator":{required:!0,message:i18nExpression("请选择上市时间")}},categoryName:{type:"string","x-hidden":!0},cateJournalList:{type:"Array","x-hidden":!0},companyCountry:{type:"string",title:i18nExpression("components.address.country"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"country",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$form.query('state').get('data').$disabled"),"@change":expression(`(val) => {
                let row = $form.values
                // 选择国外就清理省市区，并且禁用
                if (row.companyCountry !== 'CN') {
                  row.companyProvince = null
                  row.companyCity = null
                }
              }`)},...requiredValidatorSegment},companyProvince:{type:"string",title:i18nExpression("components.address.area"),"x-reactions":expression(`() => {
              $self.visible = $form.values.companyCountry === 'CN'
            }`),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"PROVINCE","custom-select-type":"PROVINCE",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$form.query('state').get('data').$disabled")},...requiredValidatorSegment},companyCity:{type:"string",title:i18nExpression("components.address.city"),"x-reactions":expression(`() => {
              $self.visible = $form.values.companyCountry === 'CN'
            }`),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:expression("$form.values.companyProvince"),"custom-select-type":"CITY",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$form.query('state').get('data').$disabled")},...requiredValidatorSegment},companyAddress:{type:"string","x-decorator":"FormItem","x-component":"Input",title:i18nExpression("cusEntry.vendorMod.detailAddress"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},"x-validator":{required:!0,message:i18nExpression("vendorMod.msgDetailAddr")}},ceeaHasParentCompany:{title:i18nExpression("cusEntry.vendorMod.ifParentCompany"),...radioGroupByYOrNSegment,"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},ceeaParentCompanyName:{type:"string","x-decorator":"FormItem","x-component":"Input","x-visible":"{{$form.query('.ceeaHasParentCompany').take().value == 'Y'}}",title:i18nExpression("cusEntry.vendorMod.parentCompanyName"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},"x-validator":{required:!0,message:i18nExpression("请输入集团名称")}},groupCountry:{type:"string",title:i18nExpression("cusEntry.vendorMod.parentCompanyCountry"),"x-visible":"{{$form.query('.ceeaHasParentCompany').take().value == 'Y'}}","x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"country",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$form.query('state').get('data').$disabled")},"x-validator":{required:!0,message:i18nExpression("cusEntry.tipMessage.parentCompanyCountryMsg")}},ceeaCompanyIntro:{type:"string","x-decorator":"FormItem","x-component":"Input.TextArea",title:expression("$t('vendorMod.companyProfile')"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),maxlength:2e3},"x-decorator-props":{gridSpan:3}}}}}}},contactInfoList={contactInfoList:{type:"void","x-component":"CollapseItem","x-component-props":{title:`{{observer(
        {
          render(h) {
            return h($$components.Note, {
              props: {
                title: t('vendorMod.contactInfo'),
                value: $form.values.extRejectAttribute4,
                readonly: true
              }
            })
          }
        }
      )}}`},"x-query-engine-skip":!0,properties:{toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},"x-visible":expression("!$form.query('state').get('data').$disabled"),properties:{add:{type:"void",title:i18nExpression("common.new"),"x-component-props":{type:"primary","@click":expression(`() => {
                 $self.query('.contactInfos')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
              }`)}}}},contactInfos:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,maxHeight:400,pagination:!1,sortable:!1,primaryKey:"contactInfoId",cascadeDeletion:!0},"x-query-engine-skip":!0,properties:generateXindexInOrder({contactName:{type:"string",title:i18nExpression("vendorMod.nickname"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},...editTableFormItemValid},ceeaGender:{type:"string",title:i18nExpression("vendorMod.sex"),"x-render-table-column":{minWidth:100},"x-component":"DictSelect","x-component-props":{code:"GENDER",disabled:expression("$form.query('state').get('data').$disabled")}},ceeaDeptName:{type:"string",title:i18nExpression("vendorMod.department"),"x-render-table-column":{minWidth:100},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},position:{type:"string",title:i18nExpression("dataConfMod.position"),"x-render-table-column":{minWidth:100},"x-component":"DictSelect","x-component-props":{code:"POSITION",disabled:expression("$form.query('state').get('data').$disabled")}},ceeaContactMethod:{type:"string",title:i18nExpression("vendorMod.contactMethod"),"x-render-table-column":{minWidth:100},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},"x-validator":{required:!0,message:i18nExpression("请输入格式正确的电话号码"),validator:expression(`(value, rule) => {
                if(value && !validatePhone(value)) {
                  return '请输入格式正确的电话号码'
                }
              }`)},...feedbackLayoutIsPopover},email:{type:"string",title:i18nExpression("vendorMod.email"),"x-render-table-column":{minWidth:100},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},"x-validator":{required:!0,message:i18nExpression("请输入格式正确的邮箱地址"),validator:expression(`(value, rule) => {
                if(value && !validEmail(value)){
                  return '请输入格式正确的邮箱地址'
                }
              }`)},...feedbackLayoutIsPopover},ceeaDefaultContact:{type:"string",title:i18nExpression("dataConfMod.isDefault"),"x-render-table-column":{minWidth:100},"x-component":"Checkbox","x-component-props":{"true-label":"Y","false-label":"N",disabled:expression("$form.query('state').get('data').$disabled"),"@change":expression(`() => {
                const row = $table.getRowByIndex($self.index)
                if(row.ceeaDefaultContact == 'Y'){
                  let data = $form.query('contactInfos').get('value')
                  let index = 0;
                  for(let item of data){
                    if(index != $self.index){
                      item.ceeaDefaultContact = ''
                    }
                    index++
                  }
                }
              }`)}},socialSecurityCertificateFileId:{type:"string","x-render-table-column":{minWidth:100},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").overseasRelation!=="PERSONAL"),title:i18nExpression("cusEntry.vendorMod.socialSecurityCertificate"),"x-component":"SrmCommonFile","x-component-props":{"extra-data":{uploadType:"DEF",sourceType:"WEB_APP",fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},"default-file":{fileId:expression("$table.getRowByIndex($self.index)?.socialSecurityCertificateFileId"),fileName:expression("$table.getRowByIndex($self.index)?.socialSecurityCertificateFileName")},"@on-change":expression(`({file}) => {
                const { fileId = null, fileName = null } = file || {}
                let row = $table.getRowByIndex($self.index)
                row.socialSecurityCertificateFileId = fileId
                row.socialSecurityCertificateFileName = fileName
                row.socialSecurityCertificateFileId = fileId
              }`),readonly:expression("$form.query('state').get('data').$disabled")}},ceeaComments:{type:"string",title:i18nExpression("dataConfMod.remark"),"x-render-table-column":{minWidth:100},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:60,fixed:"right"},"x-component":"RenderTableButtonList","x-visible":expression("!$form.query('state').get('data').$disabled"),properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),type:"text","@click":expression(`({ row }) => {
                      $table.remove($self.index)
                  }`)}}}}})}}}},bankInfoList={bankInfoList:{type:"void","x-component":"CollapseItem","x-component-props":{title:`{{observer(
        {
          render(h) {
            return h($$components.Note, {
              props: {
                title: t('vendorMod.bankInfo'),
                value: $form.values.extRejectAttribute5,
                readonly: true
              }
            })
          }
        }
      )}}`},"x-query-engine-skip":!0,properties:{toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},"x-visible":expression("!$form.query('state').get('data').$disabled"),properties:{add:{type:"void",title:i18nExpression("common.new"),"x-component-props":{type:"primary",disabled:expression("$form.query('state').get('data').$disabled"),"@click":expression(`() => {
                 $self.query('bankInfos')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
              }`)}}}},bankInfos:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,maxHeight:400,pagination:!1,sortable:!1,primaryKey:"bankInfoId",cascadeDeletion:!0},"x-query-engine-skip":!0,properties:generateXindexInOrder({branchBankId:{type:"number","x-hidden":!0},bankCode:{type:"string",title:i18nExpression("components.bank.bankCode"),"x-render-table-column":{minWidth:120},"x-component":"QuickSearchWrapper","x-component-props":{showKey:"bankNum",propKey:"bankNum",name:"ceea_base_erp_branch_bank_info",preQueryData:expression("{'t.attr1': 'Y'}"),disabled:expression("$form.query('state').get('data').$disabled"),"@close-quicksearch":expression(`(val) => {
                let row = $table.getRowByIndex($self.index)
                row.branchBankId = val ? val.branchBankId : ''
                row.bankCode = val ? val.bankNum : ''
                row.bankName = val ? val.bankName : '' // 银行名称
                row.unionCode = val ? val.branchBankNum : '' // 分行编号
                row.openingBank = val ? val.branchBankName : '' // 分行名称[开户行名称]
              }
              `)},...feedbackLayoutIsPopover,"x-validator":{required:generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").overseasRelation!="OUT"),message:i18nExpression("common.requiredField")}},bankName:{type:"string",title:i18nExpression("components.bank.bankName"),"x-render-table-column":{minWidth:120},"x-read-pretty":!0,...feedbackLayoutIsPopover,"x-validator":{required:generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").overseasRelation!="OUT"),message:i18nExpression("common.requiredField")}},unionCode:{type:"string",title:i18nExpression("components.bank.unionCode"),"x-render-table-column":{minWidth:120},"x-read-pretty":!0,...feedbackLayoutIsPopover,"x-validator":{required:generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").overseasRelation!="OUT"),message:i18nExpression("common.requiredField")}},openingBank:{type:"string",title:i18nExpression("components.bank.branchBankName"),"x-render-table-column":{minWidth:120},"x-read-pretty":!0,...feedbackLayoutIsPopover,"x-validator":{required:generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").overseasRelation!="OUT"),message:i18nExpression("common.requiredField")}},bankAccountName:{type:"string",title:i18nExpression("components.bank.accountName"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},...feedbackLayoutIsPopover,"x-validator":{required:generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").overseasRelation!="OUT"),message:i18nExpression("common.requiredField")}},bankAccount:{type:"string",title:i18nExpression("components.bank.bankAccount"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},...feedbackLayoutIsPopover,"x-validator":{required:generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").overseasRelation!="OUT"),message:i18nExpression("common.requiredField")}},currencyCode:{type:"string",title:i18nExpression("vendorMod.currencyCode"),"x-render-table-column":{minWidth:120},"x-component":"DictSelect","x-component-props":{code:"currency",disabled:expression("$form.query('state').get('data').$disabled")},...feedbackLayoutIsPopover,"x-validator":{required:generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").overseasRelation!=="OUT"),message:i18nExpression("common.requiredField")}},ceeaMainAccount:{type:"string",title:i18nExpression("components.bank.isMain"),"x-render-table-column":{minWidth:100},"x-component":"Checkbox","x-component-props":{"true-label":"Y","false-label":"N",disabled:expression("$form.query('state').get('data').$disabled"),"@change":expression(`(value) => {
                if (value === 'Y') {
                  $self.query('.ceeaEnabled').take().value = 'Y'
                }
              }`)},...feedbackLayoutIsPopover,"x-validator":{required:generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").overseasRelation!="OUT"),message:i18nExpression("common.requiredField")}},ceeaEnabled:{type:"string",title:i18nExpression("components.bank.isActive"),"x-render-table-column":{minWidth:100},"x-component":"Checkbox","x-component-props":{"true-label":"Y","false-label":"N",disabled:expression("$form.query('state').get('data').$disabled")}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:60,fixed:"right"},"x-visible":expression("!$form.query('state').get('data').$disabled"),"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),type:"text","@click":expression(`({ row }) => {
                      $table.remove($self.index)
                  }`)}}}}})}}}},companySizesList={companySizesList:{type:"void","x-component":"CollapseItem","x-component-props":{title:`{{observer(
        {
          render(h) {
            return h($$components.Note, {
              props: {
                title: t('vendorMod.companySize'),
                value: $form.values.extRejectAttribute7,
                readonly: true
              }
            })
          }
        }
      )}}`},"x-query-engine-skip":!0,"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").overseasRelation!=="PERSONAL"),properties:{companySizes:{type:"array","x-component":"RenderTable",default:[{type:"人数"}],"x-component-props":{preColumns:"seq",editMode:!0,maxHeight:250,pagination:!1,sortable:!1},"x-query-engine-skip":!0,properties:generateXindexInOrder({totalNumber:{type:"string",title:i18nExpression("cusEntry.vendorMod.totalNum"),"x-render-table-column":{minWidth:120},"x-component-props":{"v-input-format":{type:"integer",negative:!1},disabled:expression("$form.query('state').get('data').$disabled")}},socialSecurityNumber:{type:"string",title:i18nExpression("cusEntry.vendorMod.socialSecurity"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),"v-input-format":{type:"integer",negative:!1}}},managementNumber:{type:"string",title:i18nExpression("cusEntry.vendorMod.managerNum"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),"v-input-format":{type:"integer",negative:!1}}},developerNumber:{type:"string",title:i18nExpression("cusEntry.vendorMod.developmentNum"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),"v-input-format":{type:"integer",negative:!1}}},productionNumber:{type:"string",title:i18nExpression("cusEntry.vendorMod.productNum"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),"v-input-format":{type:"integer",negative:!1}}},overUndergraduateNumber:{type:"string",title:i18nExpression("cusEntry.vendorMod.bachelorDegreeOrAbove"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),"v-input-format":{type:"integer",negative:!1}}}})}}}},financeInfoList={financeInfo:{type:"void","x-component":"CollapseItem","x-component-props":{title:`{{observer(
        {
          render(h) {
            return h($$components.Note, {
              props: {
                title: t('cusEntry.vendorMod.financeReport'),
                value: $form.values.extRejectAttribute6,
                readonly: true
              }
            })
          }
        }
      )}}`},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").overseasRelation!=="PERSONAL"),"x-query-engine-skip":!0,properties:{financeInfoForm:{type:"void","x-query-engine-skip":!0,properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:{totalAssets:{type:"number",title:i18nExpression("cusEntry.vendorMod.totalAssets"),"x-decorator":"FormItem","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},currentAssets:{type:"number",title:i18nExpression("cusEntry.vendorMod.workingCapital"),"x-decorator":"FormItem","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},fixedAssets:{type:"number",title:i18nExpression("cusEntry.vendorMod.fixedAssets"),"x-decorator":"FormItem","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},avgAnnualOutput:{type:"number",title:i18nExpression("cusEntry.vendorMod.threeYearsOutput"),"x-decorator":"FormItem","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},avgAnnualProfit:{type:"number",title:i18nExpression("cusEntry.vendorMod.threeYearsNetProfits"),"x-decorator":"FormItem","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}}}}}},financeInfoAfterTag:{type:"void","x-component":"p","x-content":i18nExpression("cusEntry.vendorMod.threeYearsReportFile")},toolbar:{type:"void","x-component":"Space","x-component-props":{class:"list-form__toolbar"},"x-visible":expression("!$form.query('state').get('data').$disabled"),properties:{add:{type:"void",title:i18nExpression("common.new"),"x-component":"RButton","x-component-props":{type:"primary","@click":expression(`() => {
                 $self.query('.npmFinanceReports')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
              }`)}}}},npmFinanceReports:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,maxHeight:400,pagination:!1,sortable:!1,primaryKey:"financeReportId",cascadeDeletion:!0},"x-query-engine-skip":!0,properties:generateXindexInOrder({year:{type:"string",title:i18nExpression("cusEntry.vendorMod.year"),"x-render-table-column":{minWidth:120},"x-component":"DatePicker","x-component-props":{type:"year",format:"yyyy","value-format":"yyyy",disabled:expression("$form.query('state').get('data').$disabled")}},remark:{type:"string",title:i18nExpression("cusEntry.vendorMod.remark"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},fileId:{type:"string",title:i18nExpression("cusEntry.vendorMod.file"),"x-render-table-column":{minWidth:120},"x-component":"SrmCommonFile","x-component-props":{"extra-data":{uploadType:"DEF",sourceType:"WEB_APP",fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},"default-file":{fileId:expression("$table.getRowByIndex($self.index)?.fileId"),fileName:expression("$table.getRowByIndex($self.index)?.fileName")},"@on-change":expression(`({file}) => {
                const { fileId, fileName } = file || {}
                $table.getRowByIndex($self.index).fileId = fileId
                $table.getRowByIndex($self.index).fileName = fileName
              }`),readonly:expression("$form.query('state').get('data').$disabled")}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:60,fixed:"right"},"x-visible":expression("!$form.query('state').get('data').$disabled"),"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{disabled:expression("$form.readPretty"),type:"text","@click":expression(`({ row }) => {
                      $table.remove($self.index)
                  }`)}}}}})},financeInfoAfterRemark:{type:"void","x-component":"p","x-content":i18nExpression("cusEntry.vendorMod.financeInfoRemark")}}}},serviceRange={serviceRange:{type:"void","x-component":"CollapseItem","x-component-props":{title:`{{observer(
        {
          render(h) {
            return h($$components.Note, {
              props: {
                title: t('cusEntry.vendorMod.serviceRange'),
                value: $form.values.extRejectAttribute8,
                readonly: true
              }
            })
          }
        }
      )}}`},"x-query-engine-skip":!0,properties:{toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},"x-visible":expression("!$form.query('state').get('data').$disabled"),properties:{add:{type:"void",title:i18nExpression("common.add"),"x-component-props":{type:"primary","@click":expression(`() => {
                $form.query('serviceRangeList').take(field => {
                  field.invoke('add', 'push')
                })
              }`)}}}},serviceRangeList:{type:"array","x-component":"ArrayItems",items:{type:"void",properties:{tableForm:{type:"object",properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:{categoryName:{type:"string","x-decorator":"FormItem","x-component":"QuickSearchWrapper","x-component-props":{dialogLabel:i18nExpression("cusEntry.vendorMod.categoryNameTitle"),showKey:"categoryName",name:"scc_base_purchase_category4","@close-quicksearch":expression(`val => {
                          let form = $form.query($self.parent.parent.address.toString()).take().value
                          form.categoryCode = val?.categoryCode || ''
                          form.categoryName = val?.categoryName || ''
                          form.categoryId = val?.categoryId || ''
                          const [oneLevel, twoLevel] = val?.categoryFullName?.split('-')
                          form.categoryFullName = oneLevel + '-' + twoLevel || ''
                        }`),disabled:expression("$form.query('state').get('data').$disabled")},...requiredValidatorSegment,title:"{{t('cusEntry.vendorMod.category', {index: $self.index + 1})}}"},formBtn:{type:"void","x-component":"ButtonList","x-component-props":{style:{"margin-top":"5px"}},"x-visible":expression("!$form.query('state').get('data').$disabled"),properties:{add:{type:"void","x-component-props":{type:"primary","@click":expression(`() => {
                              $form.query($self.parent.parent.parent.parent.address.concat($self.index).concat('list')).take(field => {
                                field.componentProps.componentInstance.addRow()
                              })
                            }`)},title:i18nExpression("cusEntry.common.addCustomer")},delete:{type:"void",title:i18nExpression("cusEntry.common.deleteCategory"),"x-component-props":{type:"primary","@click":expression(`() => {
                              const { categoryJournalId = null } = $form.query('serviceRangeList').get('value')[$self.index]?.tableForm || {}
                              $form.query('serviceRangeList').take(field => {
                                const name = 'cateJournalList'
                                if (categoryJournalId) {
                                  if (!$queryEngine.dataCollection.value.relationTableCascadeDeletions[name]) {
                                    $queryEngine.dataCollection.value.relationTableCascadeDeletions[name] = new Set()
                                  }
                                  $queryEngine.dataCollection.value.relationTableCascadeDeletions[name].add(categoryJournalId)
                                }
                                field.remove($self.index)
                              })
                            }`)}}}}}}}},list:{type:"array","x-component":"RenderTable","x-query-engine-skip":!0,"x-component-props":{preColumns:"seq",editMode:!0,maxHeight:250,pagination:!1,sortable:!1},properties:generateXindexInOrder({performanceAmount:{type:"number",title:i18nExpression("cusEntry.vendorMod.performance"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},mainCustom:{type:"string",title:i18nExpression("cusEntry.vendorMod.mainCustomer"),"x-render-table-column":{minWidth:120},"x-validator":{required:!0,message:i18nExpression("cusEntry.tipMessage.required")},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},fileId:{type:"string",title:i18nExpression("cusEntry.vendorMod.achievement"),"x-render-table-column":{minWidth:120},"x-component":"SrmCommonFile","x-component-props":{extraData:{fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},defaultFile:{fileId:expression("$table.getRowByIndex($self.index)?.fileId"),fileName:expression("$table.getRowByIndex($self.index)?.fileName")},readonly:expression("$form.query('state').get('data').$disabled"),"@on-change":expression(`({file}) => {
                      const { fileId = '', fileName = '' } = file || {}
                      let row = $table.getRowByIndex($self.index)
                      row.fileId = fileId
                      row.fileName = fileName
                    }`)},...editTableFormItemValid},operation:{type:"void",title:i18nExpression("common.operation"),"x-render-table-column":{width:60,fiexd:"right"},"x-visible":expression("!$form.query('state').get('data').$disabled"),"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:i18nExpression("common.delete"),"x-component-props":{type:"text","@click":expression(`() => {
                          const { serciceCustomId = null } = $table.getRowByIndex($self.index) || {}
                          if (serciceCustomId) {
                            let serciceCustomDelList = $form.query('state').get('data').serciceCustomDelList
                            serciceCustomDelList.push({
                              $delete: serciceCustomId
                            })
                          }
                          $table.remove($self.index)
                        }`)}}}}})}}}}}}},qualificationInformation={qualificationInformation:{type:"void","x-component":"CollapseItem","x-component-props":{title:`{{observer(
        {
          render(h) {
            return h($$components.Note, {
              props: {
                title: t('cusEntry.vendorMod.qualificationInformation'),
                value: $form.values.extRejectAttribute9,
                readonly: true
              }
            })
          }
        }
      )}}`},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").overseasRelation!=="PERSONAL"),"x-query-engine-skip":!0,properties:{toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},"x-visible":expression("!$form.query('state').get('data').$disabled"),properties:{add:{type:"void",title:i18nExpression("common.add"),"x-component-props":{type:"primary","@click":expression(`() => {
                $self.query('qualificationInfo')
                  .take(field => {
                    field.componentProps.componentInstance.addRow('push', {})
                  })
              }`)}}}},qualificationInfo:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,maxHeight:400,pagination:!1,sortable:!1,primaryKey:"managementAttachId",cascadeDeletion:!0},"x-query-engine-skip":!0,"x-query-engine-relation":"managementAttaches:*",properties:generateXindexInOrder({authNum:{type:"string","x-component":"DictSelect","x-component-props":{code:"CERTIFICATE_TYPE",disabled:expression("$form.query('state').get('data').$disabled")},title:i18nExpression("cusEntry.vendorMod.certificateType"),"x-render-table-column":{minWidth:120}},startDate:{type:"date",title:i18nExpression("cusEntry.vendorMod.startTime"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},endDate:{type:"date",title:i18nExpression("cusEntry.vendorMod.endTime"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},fileuploadId:{type:"string","x-component":"SrmCommonFile","x-component-props":{extraData:{fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},defaultFile:{fileId:expression("$table.getRowByIndex($self.index)?.fileuploadId"),fileName:expression("$table.getRowByIndex($self.index)?.authType")},readonly:expression("$form.query('state').get('data').$disabled"),"@on-change":expression(`({file}) => {
                const { fileId = '', fileName = '' } = file || {}
                let row = $table.getRowByIndex($self.index)
                row.fileuploadId = fileId
                row.authType = fileName
              }`)},title:i18nExpression("cusEntry.vendorMod.file"),"x-render-table-column":{minWidth:120}},authDescription:{type:"string",title:i18nExpression("cusEntry.vendorMod.remark"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},operation:{type:"void","x-visible":expression("!$form.query('state').get('data').$disabled"),title:i18nExpression("common.operation"),"x-render-table-column":{width:60,fixed:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:i18nExpression("common.delete"),"x-component-props":{type:"text","@click":expression(`() => {
                    $table.remove($self.index)
                  }`)}}}}})}}}},personBaseInfo={person:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("cusEntry.vendorMod.baseInfo")},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").overseasRelation==="PERSONAL"),properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:{personBaseInfo:{type:"object","x-query-engine-skip":!0,properties:{businessLicenseFileId:{type:"string",title:i18nExpression("cusEntry.vendorMod.frontOfIdCard"),"x-decorator":"FormItem","x-component":"SrmCommonFile","x-component-props":{"extra-data":{uploadType:"DEF",sourceType:"WEB_APP",fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},"default-file":{fileId:expression("$form.query('personBaseInfo').get('value').businessLicenseFileId"),fileName:expression("$form.query('personBaseInfo').get('value').businessLicense")},"@on-change":expression(`({file}) => {
                     const { fileId = null, fileName = null } = file || {}
                     $form.query('personBaseInfo').get('value').businessLicenseFileId = fileId
                     $form.query('personBaseInfo').get('value').businessLicense = fileName
                     if (file) {
                      // 读取图片信息
                      app.$http({
                        url: '/api-pj/ocr/recognizeIDCardFront',
                        method: 'GET',
                        params: { fileuploadId: fileId },
                        loading: true
                      }).then(res => {
                        const {
                          birth,
                          idNum,
                          name,
                          sex
                        } = res.data
                        let form = $form.query('personBaseInfo').get('value')
                        form.companyName = name
                        form.lcCode = idNum
                        form.extSex = sex
                        // 校验供应商是否已注册
                        if (idNum) {
                          app.$http({
                            url: '/api-rbac/extUser/lcCodeVerify',
                            method: 'GET',
                            params: { lcCode: idNum, isPersonalAccount: 'Y' }
                          }).then(res => {
                            if (res.code + '' === '0') {
                              const data = res.data
                              if (data.isRemind === 'Y') {
                                let maskedName = ''
                                if(data.nickname.length==2){
                                  maskedName=data.nickname.substring(0,1)+'*'
                                }else if(data.nickname.length==3){
                                  maskedName=data.nickname.substring(0,1)+"*"+data.nickname.substring(2,3)
                                }else if(data.nickname.length>3){
                                  maskedName=data.nickname.substring(0,1)+"*"+'*'+data.nickname.substring(3,data.nickname.length)
                                }
                                const maskedPhone = data.phone.replace(data.phone.substring(3,7),'****')
                                const content = '该供应商已被注册，请联系' + maskedName + '，账号：' + maskedPhone + '，进行子账号维护操作。'
                                app.$alert(content, '', {
                                  confirmButtonText: '确定',
                                  callback: action => {}
                                });
                              }
                            }
                          })
                        }
                      })
                     }
                  }`),readonly:expression("$form.query('state').get('data').$disabled")},...requiredValidatorSegment},extIdCardOppositeFileName:{type:"string","x-hidden":!0},extIdCardOppositeFileId:{type:"string",title:i18nExpression("cusEntry.vendorMod.backOfIdCard"),"x-decorator":"FormItem","x-component":"SrmCommonFile","x-component-props":{"extra-data":{uploadType:"DEF",sourceType:"WEB_APP",fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},"default-file":{fileId:expression("$form.query('personBaseInfo').get('value').extIdCardOppositeFileId"),fileName:expression("$form.query('personBaseInfo').get('value').extIdCardOppositeFileName")},"@on-change":expression(`({file}) => {
                    const { fileId = null, fileName = null } = file || {}
                    $form.query('personBaseInfo').get('value').extIdCardOppositeFileId = fileId
                    $form.query('personBaseInfo').get('value').extIdCardOppositeFileName = fileName
                    if (file) {
                      // 读取图片信息
                      app.$http({
                        url: '/api-pj/ocr/recognizeIDCardBack',
                        method: 'GET',
                        params: { fileuploadId: fileId },
                        loading: true
                      }).then(res => {
                        const {
                          businessEndDate,
                          businessStartDate
                        } = res.data
                        $form.query('personBaseInfo').get('value').validityPeriodOfCard = [businessStartDate, businessEndDate]
                      })
                     }
                  }`),readonly:expression("$form.query('state').get('data').$disabled")},...requiredValidatorSegment},companyName:{type:"string","x-decorator":"FormItem",title:i18nExpression("cusEntry.vendorMod.companyNameOrPersonName"),...requiredValidatorSegment,"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},companyShortName:{type:"string","x-decorator":"FormItem",title:i18nExpression("cusEntry.vendorMod.personalAbbreviation"),...requiredValidatorSegment,"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),maxlength:100,"show-word-limit":!0}},businessLicense:{type:"string","x-hidden":!0},idNumber:{type:"string","x-decorator":"FormItem",title:i18nExpression("cusEntry.vendorMod.idNo"),...requiredValidatorSegment,"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),"@blur":expression(`(value) => {
                    if ($self.value) {
                      app.$http({
                        url: '/api-rbac/extUser/lcCodeVerify',
                        method: 'GET',
                        params: { lcCode: $self.value, isPersonalAccount: 'Y' }
                      }).then(res => {
                        if (res.code + '' === '0') {
                          const data = res.data
                          if (data.isRemind === 'Y') {
                            let maskedName = ''
                            if(data.nickname.length==2){
                              maskedName=data.nickname.substring(0,1)+'*'
                            }else if(data.nickname.length==3){
                              maskedName=data.nickname.substring(0,1)+"*"+data.nickname.substring(2,3)
                            }else if(data.nickname.length>3){
                              maskedName=data.nickname.substring(0,1)+"*"+'*'+data.nickname.substring(3,data.nickname.length)
                            }
                            const maskedPhone = data.phone.replace(data.phone.substring(3,7),'****')
                            const content = '该供应商已被注册，请联系' + maskedName + '，账号：' + maskedPhone + '，进行子账号维护操作。'
                            app.$alert(content, '', {
                              confirmButtonText: '确定',
                              callback: action => {}
                            });
                          }
                        }
                      })
                    }
                  }`)}},validityPeriodOfCard:{type:"date","x-decorator":"FormItem",title:i18nExpression("cusEntry.vendorMod.validityPeriodOfCard"),"x-component-props":{type:"daterange",disabled:expression("$form.query('state').get('data').$disabled")},...requiredValidatorSegment},extSex:{type:"string","x-decorator":"FormItem",title:i18nExpression("cusEntry.vendorMod.sex"),"x-component":"DictSelect","x-component-props":{code:"GENDER",disabled:expression("$form.query('state').get('data').$disabled")},...requiredValidatorSegment},businessScope:{type:"string",title:i18nExpression("cusEntry.vendorMod.mainBusinessScope"),"x-decorator":"FormItem",...requiredValidatorSegment,"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},companyCountry:{type:"string",title:i18nExpression("components.address.country"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"country",disabled:expression("$form.query('state').get('data').$disabled"),placeholder:expression("$t('common.pleaseSelect')"),"@change":expression(`(val) => {
                    // 选择国外就清理省市区，并且禁用
                    if ($form.query('personBaseInfo.companyCountry').take().value !== 'CN') {
                      $form.query('personBaseInfo.companyProvince').take().value = ''
                      $form.query('personBaseInfo.companyCity').take().value = ''
                    }
                  }`)},...requiredValidatorSegment},companyProvince:{type:"string",title:i18nExpression("components.address.area"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"PROVINCE",disabled:expression("$form.query('state').get('data').$disabled"),"custom-select-type":"PROVINCE",placeholder:expression("$t('common.pleaseSelect')")},"x-visible":"{{$form.query('personBaseInfo.companyCountry').take().value == 'CN'}}",...requiredValidatorSegment},companyCity:{type:"string",title:i18nExpression("components.address.city"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:expression("$form.query('personBaseInfo.companyProvince').take()?.value"),"custom-select-type":"CITY",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$form.query('state').get('data').$disabled")},"x-visible":"{{$form.query('personBaseInfo.companyCountry').take().value == 'CN'}}",...requiredValidatorSegment},companyAddress:{type:"string",title:i18nExpression("cusEntry.vendorMod.detailAddress"),"x-decorator":"FormItem",...requiredValidatorSegment,"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}}}}}}}}};const _sfc_main$3={name:"Note",props:{title:{type:String,default:""},value:{type:String,default:""},readonly:{type:Boolean,default:!1}},data(){return{visible:!1,newValue:this.value}},watch:{value(newValue,oldValue){newValue!==oldValue&&(this.newValue=newValue)}},methods:{valueChange(value){this.$emit("change",value)}}};var _sfc_render$3=function(){var _vm=this,_c=_vm._self._c;return _c("div",{staticClass:"collapse-item-note"},[_c("span",[_vm._v(_vm._s(_vm.title))]),_c("el-popover",{attrs:{placement:"top",width:"400",trigger:"manual"},model:{value:_vm.visible,callback:function($$v){_vm.visible=$$v},expression:"visible"}},[_c("el-input",{attrs:{type:"textarea",maxlength:"100",disabled:_vm.readonly},on:{change:_vm.valueChange},model:{value:_vm.newValue,callback:function($$v){_vm.newValue=$$v},expression:"newValue"}}),_c("el-button",{attrs:{slot:"reference",type:"text"},on:{click:function($event){$event.stopPropagation(),_vm.visible=!_vm.visible}},slot:"reference"},[_vm._v(" "+_vm._s(_vm.$t("cusEntry.common.note"))+" ")])],1)],1)},_sfc_staticRenderFns$3=[],__component__$3=normalizeComponent(_sfc_main$3,_sfc_render$3,_sfc_staticRenderFns$3,!1,null,null,null,null);const Note=__component__$3.exports,_sfc_main$2=defineComponent({__name:"mainEngine",props:{formCompanyNature:{type:Object,default:()=>({})},type:{type:String,default:()=>""}},setup(__props){const props=__props,{app,emitTabRemove,t,vendor,http}=usePageHelper(),attrs=useAttrs(),workflowStatus=ref$1("DRAFT");let $disabled=!1;const newAddress=defineComponent({name:"newAddress",props:CAddress.props,setup(props2,{listeners,attrs:attrs2,slots}){return useAutoMountInstanceToField(),()=>h(CAddress,{props:{...attrs2,...props2},on:listeners,ref:"address"},slots)}}),query={"*":{},bankInfos:{"*":{}},contactInfos:{"*":{}},orgCategorys:{"*":{}},orgInfos:{"*":{}},operationInfo:{"*":{}},fileUploads:{"*":{}},supplierLeaderList:{"*":{}},siteInfos:{"*":{}},npmCompanySizes:{"*":{}},managementAttaches:{"*":{}},cateJournalList:{"*":{},npmSerciceCustoms:{"*":{}}},npmFinanceReports:{"*":{}},npmCompanyExceptionInfos:{"*":{}}},$managementChange=(value,name,$form)=>{try{if(value){let data=$form.query(".managementAttaches").take().value;if(value=="Y"){let bold=1;data.forEach(e=>{e.documentInspection==name&&(bold=0)}),bold&&$form.query(".managementAttaches").take().invoke("addRow","unshift",{documentInspection:name,managementAttachId:null,managementInfoId:null,companyId:null,fileuploadId:null,authType:"",authDescription:"",authNum:"",authDate:null,authOrg:"",endDate:null})}else data.forEach((e,index)=>{e.documentInspection==name&&$form.query(".managementAttaches").take().invoke("remove",index)})}}catch{}},schema=defineSchemas({state:{type:"void","x-component":"Fragment","x-hidden":!0,"x-data":{companyId:app.$store.getters.userInfo.companyId||null,$disabled:!1,serciceCustomDelList:[],overseasRelation:"",status:"",deleAttr:[],deleFileUploads:[],type:""}},CompanyInfo:{type:"void","x-decorator":"QueryEngine","x-component":"el-container","x-component-props":{class:expression('$form.query("state").get("data").type !== "registered" ? "flex-container companyInfos" : "flex-container registered"'),direction:"vertical"},"x-query-engine":{service:"sup",actions:{query:{immediate:!0,loading:!0,ready:expression(`() => {
            // const params = {
            //   pageSize: 15,
            //   pageNum: 1,
            //   sceneCode: 'SCENE_COMPANY_SUNSHINE_FILE',
            //   sceneModuleCode: 'SCENE_COMPANY_SUNSHINE_FILE_ATTACHMENT'
            // }
            // sceneFileApi.listPage(params).then(res => {
            //   const {
            //     attachmentName,
            //     templateFileId
            //   } = res.data?.list?.[0]
            //   $form.values.protocolTemplateName = attachmentName
            //   $form.values.protocolTemplateId = templateFileId
            // })
            setTimeout(() => {
             $form.query('state').get('data').overseasRelation = $props.formCompanyNature.value?.overseasRelation
             $form.query('state').get('data').type = $props.type
             $form.query('.overseasRelation').take().value = $props.formCompanyNature.value?.overseasRelation
             $form.query('.companyType').take().value = $props.formCompanyNature.value?.companyType
            })
            $form.values.serviceRangeList = [{
              tableForm: {},
              list: [{}]
            }]
            $form.values.contactInfos = [
              {
                position: 'SALES_MANAGER'
              },
              {
                position: 'SENIOR_LEADER'
              },
            ]
            return app.$store.getters.userInfo.companyId && $buyer()
          }`),autoFormatResult:!1,transformRequest:expression(`(data, headers) => {
            data.tree = true
            data.query = query
            $form.query('state').get('data').companyId = app.$store.getters.userInfo.companyId
            data.payload = {
              "filter": {
                  "companyId": {
                      eq: app.$store.getters.userInfo.companyId
                  }
              }
            }
            return data
          }`),transformResponse:expression(`(res) => {
            const data = JSON.parse(res).data.records[0]
            const status = data.status
            if (['APPROVED','SUBMITTED'].includes(status)) {
              $form.query('state').get('data').$disabled = true
            } else {
              $form.query('state').get('data').$disabled = false
            }
            const state = $form.query('state').get('data')
            state.status = status
            const mainHerder = $form.query('.mainHerder').take()
            if (status) {
               mainHerder.componentProps.status = status
               if (status == 'SUBMITTED') {
                 mainHerder.componentProps.stepsActive = 4
               }
               if (status == 'APPROVED') {
                 mainHerder.componentProps.stepsActive = 6
               }
               mainHerder.componentProps.flowRemark = data.flowRemark
            }

            if (!$props.formCompanyNature.value?.overseasRelation) {
              $form.query('state').get('data').overseasRelation = data.overseasRelation
              const overseasRelation = $form.query('state').get('data').overseasRelation
              if (overseasRelation == 'PERSONAL') {
                $form.query('.companyTypeAll').take().visible = false
                $form.query('.companyInfo').take().visible = false
                $form.query('.companyBaseInfo').take().visible = false

                data.companyName2 = data.companyName
              } else {
                $form.query('.companyTypeAll').take().visible = true
                $form.query('.companyInfo').take().visible = true
                $form.query('.companyBaseInfo').take().visible = true
              }
            }

            $form.setValues(data)
            $form.values.ceeaBusinessModel = data.ceeaBusinessModel ? data.ceeaBusinessModel.split(',') : []
            if (data.businessStartDate) {
              $form.query('.businessDate').take().value = [data.businessStartDate, data.businessEndDate]
            }
            setTimeout(() => {
                $form.query('fileUploads').take(field => {
                  field.visible = true
                  field.componentProps.componentInstance.reLoadFileInfo()
                })
              }, 5000)

            return data
          }`)},vendorRead:{immediate:!0,loading:!0,method:"read",ready:expression(`() => {
            setTimeout(() => {
              $form.query('overseasRelation').take().value = $props.formCompanyNature.value?.overseasRelation
              $form.query('state').get('data').overseasRelation = $props.formCompanyNature.value?.overseasRelation
              // $form.query('companyType').take().value = $props.formCompanyNature.value?.companyType
            })
            if(!app.$store.getters.userInfo.companyId && !$buyer()){
              $form.query('fileUploads').take(field => {
                field.visible = true
              })
            }
            $form.values.serviceRangeList = [{
              tableForm: {},
              list: [{}]
            }]
            $form.values.contactInfos = $props.formCompanyNature.value?.overseasRelation !== 'PERSONAL' ? [
              {
                position: 'SALES_MANAGER'
              },
              {
                position: 'SENIOR_LEADER'
              }
            ] : [{ position: 'SALES_MANAGER' }]
            return app.$store.getters.userInfo.companyId && !$buyer()
          }`),autoFormatResult:!1,transformRequest:expression(`(data, headers) => {
            data.tree = true
            data.action = 'vendorRead'
            data.query = query
            $form.query('state').get('data').companyId = app.$store.getters.userInfo.companyId
            data.payload = {
              "filter": {
                  "companyId": {
                      eq: app.$store.getters.userInfo.companyId
                  }
              }
            }
            return data
          }`),onSuccess:expression(`(res) => {
            const data = res[0]
            const status = data.status
            if (['APPROVED','SUBMITTED'].includes(status)) {
              $form.query('state').get('data').$disabled = true
            } else {
              $form.query('state').get('data').$disabled = false
            }
            const state = $form.query('state').get('data')
            state.status = status
            const mainHerder = $form.query('.mainHerder').take()
            if (status) {
               mainHerder.componentProps.status = status
               if (status == 'SUBMITTED') {
                 mainHerder.componentProps.stepsActive = 4
               }
               if (status == 'APPROVED') {
                 mainHerder.componentProps.stepsActive = 6
               }
               mainHerder.componentProps.flowRemark = data.flowRemark
            }
            $form.query('companySizes').take().value = data.npmCompanySizes || []
            const serviceRange = data.cateJournalList.map(item => {
              const {
                npmSerciceCustoms,
                ...form
              } = item
              return {
                list: npmSerciceCustoms,
                tableForm: form
              }
            })
            $form.query('serviceRangeList').take().value = serviceRange
            $form.query('qualificationInfo').take().value = data.managementAttaches
            if (!$props.formCompanyNature.value?.overseasRelation) {
              $form.query('state').get('data').overseasRelation = data.overseasRelation
              const overseasRelation = state.overseasRelation
            }
            if ($props.formCompanyNature.value?.overseasRelation) {
              data.overseasRelation = $props.formCompanyNature.value?.overseasRelation
              // data.companyType = $props.formCompanyNature.value?.companyType
            }
            if (data.overseasRelation !== 'OUT') {
              data.bankInfos = data.bankInfos.length ? data.bankInfos : [{}]
            }
            $form.setValues({
              ...data
            })
            $form.values.ceeaBusinessModel = data.ceeaBusinessModel ? data.ceeaBusinessModel.split(',') : []
            if (data.businessStartDate) {
              $form.query('.businessDate').take().value = [data.businessStartDate, data.businessEndDate]
            }
            if (state.overseasRelation === 'PERSONAL') {
              /* 获取个人信息, 后面优化个人信息赋值 */
              const {
                companyName,
                companyShortName,
                businessLicense,
                businessLicenseFileId,
                extIdCardOppositeFileName,
                extIdCardOppositeFileId,
                idNumber,
                extSex,
                businessScope,
                companyCountry,
                companyProvince,
                companyCity,
                companyAddress,
                businessStartDate,
                businessEndDate
              } = data
              $form.values.personBaseInfo = { 
                companyName, 
                companyShortName,
                businessLicense,
                businessLicenseFileId,
                extIdCardOppositeFileName,
                extIdCardOppositeFileId,
                idNumber,
                validityPeriodOfCard: businessStartDate ? [businessStartDate, businessEndDate] : [],
                extSex,
                businessScope,
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
            let deleFileUploads = [] // 附件要删除的列表
            data.fileUploads.forEach(e => {
              deleFileUploads.push({$delete:e.sceneFileId})
            })
            $form.query('state').get('data').deleFileUploads = deleFileUploads
          }`)},vendorSave:{method:"read",autoFormatResult:!1,cascadeDeletion:!0,loading:!0},vendorWithdraw:{autoFormatResult:!1,loading:!0},vendorSubmit:{autoFormatResult:!1,cascadeDeletion:!0,loading:!0}}},properties:{layout:{type:"void","x-component":"FormContainer",items:{type:"object",properties:{prevOne:{type:"void","x-content":i18nExpression("common.prevOne"),"x-component":"Button","x-visible":expression('["", "DRAFT", "WITHDRAW", "REJECTED"].includes($form.query("state").get("data").status)'),"x-component-props":{type:"default","@click":expression(`async (values) => {
                  let overseasRelation = null
                  overseasRelation = $form.query('state').get('data').overseasRelation
                  app.$emit('whatOverseasRelation', overseasRelation)
                }`)}},staging:{type:"void","x-content":i18nExpression("common.staging"),"x-component":"Button","x-visible":expression('["", "DRAFT", "WITHDRAW"].includes($form.query("state").get("data").status)'),"x-component-props":{type:"default","@click":expression(`async (values) => {
                  $saveBill('staging', $form, $queryEngine, $confirm, $message, $bus, $t)
                }`)}},submit:{type:"void","x-content":i18nExpression("common.submit"),"x-component":"Button","x-visible":expression('["", "DRAFT", "WITHDRAW", "REJECTED"].includes($form.query("state").get("data").status)'),"x-component-props":{"@click":expression(`async (values) => {
                  $saveBill('submit', $form, $queryEngine, $confirm, $message, $bus, $t)
                }`)}},recall:{type:"void","x-content":i18nExpression("common.recall"),"x-component":"Button","x-visible":expression('["SUBMITTED"].includes($form.query("state").get("data").status)'),"x-component-props":{"@click":expression(`async (values) => {
                  $saveBill('recall', $form, $queryEngine, $confirm, $message, $bus, $t)
                }`)}}}},properties:{mainHerder:{type:"void","x-component":"MainHerder","x-component-props":{stepsActive:3,flowRemark:"",registered:expression('$form.query("state").get("data").type == "registered"')}},collapse:{type:"void","x-component":"Collapse","x-component-props":{defaultOpenPanelCount:1},properties:{...companyType,...companyInfo,...companyBaseInfo,...personBaseInfo,...contactInfoList,...bankInfoList,...companySizesList,...financeInfoList,...serviceRange,...qualificationInformation,fileUploadsList:{type:"void","x-component":"CollapseItem","x-component-props":{title:`{{observer({
                    render(h) {
                      return h($$components.Note, {
                        props: {
                          title: t('vendorMod.sceneAttachmentInfo2'),
                          value: $form.values.extRejectAttribute11,
                          readonly: true
                        }
                      })
                    }
                  })}}`},"x-visible":expression('$form.query("state").get("data").overseasRelation !== "PERSONAL"'),"x-query-engine-skip":!0,properties:{fileUploads:{"x-query-engine-relation":"fileUploads:*",type:"array","x-component":"FileDynamic","x-component-props":{"scene-module-code":"SCENE_SUPPLIER_ATTACHMENT",businessId:expression("$form.values.companyId || null"),editable:expression('!$form.query("state").get("data").$disabled'),"need-init":!1}}}}}}}},CFillProgress:{type:"void","x-component":"CFillProgress","x-component-props":{class:"contract-progress",ref:"contractProgress",nodeName:i18nExpression("logisticsMod.contractInfo"),data:expression('$nodeList($form.query("state").get("data").overseasRelation)'),percentage:"{{true}}","@index-click":`{{ (code) => {
              let anchorEle = document.querySelector('#collapse_' + code)
              if (anchorEle) {
                anchorEle.scrollIntoView(true)
              }
           } }}`}}}}}),$saveBill=async(type,$form,$queryEngine,$confirm,$message,$bus,$t)=>{const overseasRelation=$form.values.overseasRelation||$form.query("state").get("data").overseasRelation;let{personBaseInfo:personBaseInfo2={},...values}=JSON.parse(JSON.stringify($form.values));if(personBaseInfo2.validityPeriodOfCard){const[businessStartDate,businessEndDate]=personBaseInfo2.validityPeriodOfCard;personBaseInfo2.businessStartDate=businessStartDate,personBaseInfo2.businessEndDate=businessEndDate}values.npmCompanySizes=$form.query("companySizes").get("value");const serciceCustomDelList=$form.query("state").get("data").serciceCustomDelList||[];let serviceRange2=$form.query("serviceRangeList").get("value").map(item=>{const{list,tableForm}=item;return{...tableForm,npmSerciceCustoms:[...list,...serciceCustomDelList]}});values.cateJournalList=serviceRange2,values.managementAttaches=$form.query("qualificationInfo").get("value");const businessDate=$form.query(".businessDate").take()?.value;if(businessDate&&businessDate?.length>0&&(values.businessStartDate=businessDate[0],values.businessEndDate=businessDate[1]),$form.query("state").get("data")?.deleFileUploads&&$form.query("state").get("data").overseasRelation!=="PERSONAL"&&(values.fileUploads.forEach(e=>{delete e.sceneFileId}),values.fileUploads=[...values.fileUploads,...$form.query("state").get("data")?.deleFileUploads]),values.contactInfos.length>1){let num=0;if(values.contactInfos.forEach((e,index)=>{e.ceeaDefaultContact=="Y"&&num++}),num>1)return app.$message.error($t("dataConfMod.isDefaultMsg")),!1}if(type=="submit"){let validate=!0;if(await $form.validate().then().catch(eq=>{app.$message.error(eq[0].messages[0]),validate=!1}),!validate)return!1;let validFlag=!0,validContact=!0,overseasRelation2=$form.query("state").get("data").overseasRelation,positionList=[],companyNameFlag=!1,ceeaEnabled="",str="",bankInfosRequiredKeys=[{key:"bankCode",message:"第$index行缺少银行代码"},{key:"bankAccountName",message:"第$index行缺少账号名称"},{key:"bankAccount",message:"第$index行缺少银行账号"},{key:"currencyCode",message:"第$index行缺少币种"}];!values.registCurrency&&overseasRelation2!=="PERSONAL"&&(str+=$t("vendorMod.msgCurrencyCode")+`
`),overseasRelation2!=="PERSONAL"&&overseasRelation2=="INSIDE"&&(values.contactInfos.some(item=>{if(item.ceeaDefaultContact==="Y"&&!item.socialSecurityCertificateFileId)return validContact=!1,!0}),positionList=values.contactInfos.filter(item=>!!item.position).map(itm=>itm.position)),serviceRange2.some(item=>{if(!item.categoryId)return validFlag=!1,!0});const categoryIdList=new Set(serviceRange2.map(item=>item.categoryId));if(serviceRange2.length!==categoryIdList.size){let nameRecords=[];for(let id of categoryIdList){const record=serviceRange2.filter(item=>item.categoryId===id);record.length>1&&nameRecords.push(record[0].categoryName)}return $message.warning($t("cusEntry.tipMessage.serviceRangeCategoryRepeat",{name:nameRecords.join(";")})),!1}if($form.query("state").get("data").overseasRelation!=="OUT"){const{companyName,bankInfos}={...values,...personBaseInfo2},mainAccountRow=bankInfos.find(item=>item.ceeaMainAccount==="Y"),mainAccountBankAccountName=mainAccountRow?.bankAccountName;companyNameFlag=companyName!==mainAccountBankAccountName,ceeaEnabled=mainAccountRow?.ceeaEnabled;for(const[index,item]of new Map(values.bankInfos.map((item2,index2)=>[index2,item2]))){let errorItem=bankInfosRequiredKeys.find(keyItem=>!item[keyItem.key]);errorItem&&(str+=`银行信息${errorItem.message.replace("$index",index+1)}
`)}}if(overseasRelation2=="INSIDE"&&!(positionList.includes("SALES_MANAGER")&&positionList.includes("SENIOR_LEADER"))&&(str+=$t("cusEntry.tipMessage.atLeastManageAndLeader")+`
`),(validate||!validFlag||serviceRange2.length===0||values.contactInfos.length===0||!validContact||values.bankInfos?.length===0||companyNameFlag||ceeaEnabled!=="Y")&&(!validFlag&&(str+=$t("cusEntry.tipMessage.serviceRangeCategoryRequired")+`
`),serviceRange2.length===0&&(str+=$t("cusEntry.tipMessage.atLeastCategory")+`
`),values.contactInfos.length===0&&(str+=$t("dashboard.addContactInformation")+`
`),overseasRelation2=="INSIDE"&&!validContact&&(str+=$t("cusEntry.tipMessage.socialSecurityCertificateMsg")+`
`),overseasRelation2!="OUT"&&(values.bankInfos?.length===0&&(str+="请添加银行信息"),companyNameFlag&&(str+=$t("cusEntry.tipMessage.companyAndBankAccount")+`
`),ceeaEnabled!=="Y"&&(str+=$t("cusEntry.tipMessage.ceeaEnabled")+`
`))),str.length)return $message.error(str),!1;const{lcCode,idNumber}={...values,...personBaseInfo2},res=await integritySystem(lcCode||idNumber);if(res.data===integritySystemResultMap.get("forbid"))return $message.warning($t("cusEntry.tipMessage.blackForbid")),!1;res.data===integritySystemResultMap.get("focus")&&(values.focusFlag="Y",values.npmCompanyExceptionInfos.push({exceptionType:"FOCUS_FLAG"}))}values.supplierType==""&&(values.supplierType=null);const companyId=app.$store.getters.userInfo.companyId||null,status=$form.query(".status").take().value;values.ceeaBusinessModel=values.ceeaBusinessModel?.length?values.ceeaBusinessModel.join():null,type=="staging"?[null,void 0,"DRAFT"].includes(status)?(values.status="DRAFT",$queryEngine.request.save({...values,...personBaseInfo2,overseasRelation},{query,tree:!0,action:"vendorSave"}).then(res=>{if($message.success($t("common.successSave")),!app.$store.getters.userInfo.companyId){let userInfo=app.$store.getters.userInfo;userInfo.companyId=res[0].companyId,app.$store.commit("user/SET_USER_INFO",userInfo),app.$store.commit("user/SET_COMPANYID",res[0].companyId)}$queryEngine.request.baseRequest({action:"vendorRead"})}).catch(err=>{$form.values.ceeaBusinessModel=values.ceeaBusinessModel?values.ceeaBusinessModel.split(","):[]})):$queryEngine.request.save({...values,...personBaseInfo2,overseasRelation},{query,action:"vendorSave"}).then(res=>{if($message.success($t("common.successSave")),!app.$store.getters.userInfo.companyId){let userInfo=app.$store.getters.userInfo;userInfo.companyId=res[0].companyId,app.$store.commit("user/SET_USER_INFO",userInfo),app.$store.commit("user/SET_COMPANYID",res[0].companyId)}$queryEngine.request.baseRequest({action:"vendorRead"}).catch(err=>{$form.values.ceeaBusinessModel=values.ceeaBusinessModel?values.ceeaBusinessModel.split(","):[]})}):type=="recall"?app.$prompt("","撤回原因",{confirmButtonText:"确定",cancelButtonText:"取消",inputType:"textarea"}).then(({value})=>{let obj={companyId,flowRemark:value};$queryEngine.request.save(obj,{query,action:"vendorWithdraw"}).then(async res=>{app.$message({message:"成功撤回",type:"success"}),$form.query(".fileUploads").take(field=>{field.visible=!1}),$queryEngine.request.baseRequest({action:"vendorRead"})})}):(app.$store.getters.userInfo!="BUYER"&&(values.potentialFlag="Y"),$queryEngine.request.save({...values,...personBaseInfo2,overseasRelation},{query,tree:!0,action:"vendorSubmit"}).then(async res=>{if(!app.$store.getters.userInfo.companyId){let userInfo=app.$store.getters.userInfo;userInfo.companyId=res[0].companyId,app.$store.commit("user/SET_USER_INFO",userInfo),app.$store.commit("user/SET_COMPANYID",res[0].companyId)}$form.values.companyId=res[0].companyId;const{companyId:companyId2,companyName,companyCode}=$form.values;await $monitorIpAddress({supplierId:companyId2,supplierCode:companyCode,supplierName:companyName,source:source.get("registerSubmit")}),app.$emit("saveAll")}).catch(e=>{$form.values.ceeaBusinessModel=values.ceeaBusinessModel?values.ceeaBusinessModel.split(","):[]}))},$nodeList=userType=>{const company=[{code:"companyTypeAll",name:t("vendorMod.companyType"),percentage:0},{code:"companyInfo",name:t("vendorMod.enterpriseThreeCertificates"),percentage:0},{code:"companyBaseInfo",name:t("vendorMod.companyBaseInfo"),percentage:0},{code:"contactInfoList",name:t("vendorMod.contactInfo"),percentage:0},{code:"bankInfoList",name:t("vendorMod.bankInfo"),percentage:0},{code:"companySizesList",name:t("vendorMod.companySize"),percentage:0},{code:"financeInfo",name:t("cusEntry.vendorMod.financeReport"),percentage:0},{code:"serviceRange",name:t("cusEntry.vendorMod.serviceRange"),percentage:0},{code:"qualificationInformation",name:t("cusEntry.vendorMod.qualificationInformation"),percentage:0},{code:"fileUploadsList",name:t("vendorMod.sceneAttachmentInfo2"),percentage:0}],person=[{code:"person",name:t("cusEntry.vendorMod.baseInfo"),percentage:0},{code:"contactInfoList",name:t("vendorMod.contactInfo"),percentage:0},{code:"bankInfoList",name:t("vendorMod.bankInfo"),percentage:0},{code:"serviceRange",name:t("cusEntry.vendorMod.serviceRange"),percentage:0}];return userType==="PERSONAL"?person:company},integritySystem=lcCode=>http({url:"/api-sup/pj/companyInfo/queryIfBlackCompany",method:"POST",data:{lcCode}}),$monitorIpAddress=data=>http({url:"/api-sou/bids/ip/address/ipAddress/save",method:"POST",data}),integritySystemResultMap=new Map([["forbid","禁止合作"],["focus","重点关注"]]),source=new Map([["registerSubmit","注册提交"],["registerUndo","注册撤回"]]),$showSunFile=$self=>{const fileList=[];if($self.value){const fileIdList=$self.value?.split(","),fileNameList=$self.query(".sunshineFileName").get("value")?.split(",");fileIdList.forEach((item,index)=>{fileList.push({fileId:item,fileName:fileNameList?.[index]})})}$self.setComponentProps({fileList})};return{__sfc:!0,props,app,emitTabRemove,t,vendor,http,attrs,workflowStatus,$disabled,newAddress,query,$managementChange,schema,$saveBill,$nodeList,integritySystem,$monitorIpAddress,integritySystemResultMap,source,$showSunFile,scope:{app,http,t,$props:props,$attrs:attrs,performPlanService,$disabled,emitTabRemove,$saveBill,DictSelect,observer,$managementChange,query,validEmail,validatePhone,$nodeList,sceneFileApi,integritySystem,integritySystemResultMap,$showSunFile,source,$monitorIpAddress},components:{SrmCommonFile:CommonFile,CAddress,CCategorySelect,FileDynamic,CFillProgress,newAddress,MainHerder,Note},RenderEngine}}});var _sfc_render$2=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{schemaKey:"companyInfoMain",pageAttrs:_vm.$attrs,schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns$2=[],__component__$2=normalizeComponent(_sfc_main$2,_sfc_render$2,_sfc_staticRenderFns$2,!1,null,null,null,null);const mainEngine=__component__$2.exports,__$_require_71f3b21f__="/srm/assets/domestic-87aa8782.svg",__$_require_f42902c8__="/srm/assets/abroad-a81170c1.svg",__$_require_9d4a9af0__="/srm/assets/person-1cfed7ec.svg";const _sfc_main$1={components:{},props:{value:{type:String}},data(){return{}},watch:{value:{handler(){this.value},deep:!0}},async created(){},mounted(){},updated(){},methods:{clickOne(how){this.choseWhat=how,this.$emit("change",how)}}};var _sfc_render$1=function(){var _vm=this,_c=_vm._self._c;return _c("div",{staticClass:"box-warp"},[_c("div",{staticClass:"box-one",class:{clickClass:_vm.value=="INSIDE"},staticStyle:{"margin-left":"0"},on:{click:function($event){return _vm.clickOne("INSIDE")}}},[_c("img",{attrs:{src:__$_require_71f3b21f__,alt:""}}),_vm._m(0)]),_c("div",{staticClass:"box-one",class:{clickClass:_vm.value=="OUT"},on:{click:function($event){return _vm.clickOne("OUT")}}},[_c("img",{attrs:{src:__$_require_f42902c8__,alt:""}}),_vm._m(1)]),_c("div",{staticClass:"box-one",class:{clickClass:_vm.value=="PERSONAL"},on:{click:function($event){return _vm.clickOne("PERSONAL")}}},[_c("img",{attrs:{src:__$_require_9d4a9af0__,alt:""}}),_vm._m(2)])])},_sfc_staticRenderFns$1=[function(){var _vm=this,_c=_vm._self._c;return _c("div",{staticClass:"box-center"},[_c("div",{staticClass:"blueOne"},[_vm._v(" 境内企业 ")]),_c("div",{staticClass:"comments"},[_vm._v(" 境内企业是指在国内指在中国境内依法设立的企业，包括外商投资外商独资企业外商投资企业境内投资。 ")])])},function(){var _vm=this,_c=_vm._self._c;return _c("div",{staticClass:"box-center"},[_c("div",{staticClass:"blueOne"},[_vm._v(" 境外企业 ")]),_c("div",{staticClass:"comments"},[_vm._v(" 境外企业是指在中国境外依法设立的企业，不是依据我国的法律设立的一般都是境外企业。 ")])])},function(){var _vm=this,_c=_vm._self._c;return _c("div",{staticClass:"box-center"},[_c("div",{staticClass:"blueOne"},[_vm._v(" 个人 ")]),_c("div",{staticClass:"comments"},[_vm._v(" 个体户一般指个体工商户。个体工商户是指在法律允许的范围内，依法经核准登记，从事工商经营活动的自然人或家庭。 ")])])}],__component__$1=normalizeComponent(_sfc_main$1,_sfc_render$1,_sfc_staticRenderFns$1,!1,null,"073d2934",null,null);const natureChose=__component__$1.exports,_sfc_main=defineComponent({__name:"index",props:{type:{type:String,default:()=>""}},setup(__props){const props=__props,{app,emitTabRemove,t,vendor}=usePageHelper(),schema=defineSchemas({state:{type:"void","x-component":"Fragment","x-hidden":!0,"x-data":{companyId:app.$store.getters.userInfo.companyId||null,status:null,wheres:"",type:"",userType:"",flowRemark:""}},CompanyInfo:{type:"void","x-decorator":"QueryEngine","x-query-engine":{service:"sup",actions:{query:{immediate:!0,ready:expression(`() => {
            const state = $form.query('state').get('data')
            if (!app.$store.getters.userInfo.companyId) {
              state.wheres = 'company-nature'
            }
            $form.query('state').get('data').type = $props.type
            if($props.type != 'registered' && app.$store.getters.userInfo.companyId) {
              state.wheres = 'main'
              return false
            }
            return app.$store.getters.userInfo.companyId && $buyer()
          }`),autoFormatResult:!1,transformRequest:expression(`(data, headers) => {
            data.tree = true
            data.query = {
              status: {}
            }
            data.payload = {
              "filter": {
                  "companyId": {
                      eq: app.$store.getters.userInfo.companyId
                  }
              }
            }
            return data
          }`),onSuccess:expression(`(res) => {
            const data = res[0]
            const state = $form.query('state').get('data')
            state.status = data.status
            if (data.status == 'SUBMITTED') {
              state.wheres = 'success'
            } else {
              state.wheres = 'main'
            }
          }`)},vendorRead:{immediate:!0,loading:!0,method:"read",ready:expression(`() => {
            const state = $form.query('state').get('data')
            if (!app.$store.getters.userInfo.companyId) {
              state.wheres = 'company-nature'
            }
            $form.query('state').get('data').type = $props.type
            if($props.type != 'registered' && app.$store.getters.userInfo.companyId) {
              state.wheres = 'main'
              return false
            }
            return app.$store.getters.userInfo.companyId && !$buyer()
          }`),autoFormatResult:!1,transformRequest:expression(`(data, headers) => {
            data.tree = true
            data.action = 'vendorRead'
            data.query = {
              status: {},
              flowRemark: {}
            }
            data.payload = {
              "filter": {
                  "companyId": {
                      eq: app.$store.getters.userInfo.companyId
                  }
              }
            }
            return data
          }`),onSuccess:expression(`(res) => {
            const data = res[0]
            const state = $form.query('state').get('data')
            state.status = data.status
            state.flowRemark = data.flowRemark
            if (data.status == 'SUBMITTED' && $form.query('state').get('data').type == 'registered') { // 已提交的时候节点跳到第四个
              app.$emit('goToWhere', 4)
              app.$emit('companyInfoIsSuccess', 'Y')
              state.wheres = 'success'
              return false
            }
            if (['APPROVED','REJECTED'].includes(data.status) && $form.query('state').get('data').type == 'registered') { // 审批通过的时候节点跳到第五个
              app.$emit('goToWhere', 5)
              app.$emit('companyInfoIsSuccess', 'Y')
              state.wheres = 'success'
            } else {
              state.wheres = 'main'
            }
          }`)}}},"x-component":"PageContainer","x-component-props":{class:"flex-container companyInfoMaintain",direction:"vertical"},properties:{stepOne:{type:"void","x-hidden":expression("$form.query('state').get('data').wheres != 'stepOne'"),"x-component":"stepOne","x-component-props":{"@goToWhere":expression(`(where) => {
            $form.query('state').get('data').wheres = where
          }`)}},success:{type:"void","x-hidden":expression("$form.query('state').get('data').wheres != 'success'"),"x-component":"success","x-component-props":{status:expression("$form.query('state').get('data').status"),flowRemark:expression("$form.query('state').get('data').flowRemark"),"@goToWhere":expression(`(where) => {
            $form.query('state').get('data').wheres = where
            app.$emit('companyInfoIsSuccess', 'N')
          }`)}},companyNature:{type:"void","x-decorator":"FormContainer","x-decorator-props":{class:"companyNature"},items:{type:"object",properties:{submit:{type:"void","x-content":i18nExpression("common.nextOne"),"x-component":"Button","x-component-props":{"@click":expression(`async (values) => {
                  $form.validate().then(e => {
                    $form.query('state').get('data').userType = $form.query('formCompanyNature.overseasRelation').get('value')
                    $form.query('state').get('data').wheres = 'main'
                    app.$emit('goToWhere', 3)
                  })
                }`)}}}},"x-hidden":expression("$form.query('state').get('data').wheres != 'company-nature'"),properties:{...companyNatureEngine}},mainEngine:{type:"void","x-hidden":expression("$form.query('state').get('data').wheres != 'main'"),"x-component":"mainEngine","x-component-props":{"@goToWhere":expression(`(where) => {
            $form.query('state').get('data').wheres = where
          }`),"@saveAll":expression(`(where) => {
            app.$emit('goToWhere', 4)
            app.$emit('companyInfoIsSuccess', 'Y')
            $form.query('state').get('data').status = 'SUBMITTED'
            $form.query('state').get('data').wheres = 'success'
          }`),"@whatOverseasRelation":expression(`(overseasRelation) => {
            $form.query('state').get('data').wheres = 'company-nature'
            $form.query('.formCompanyNature.overseasRelation').take().props.value = overseasRelation
            app.$emit('goToWhere', 2)
          }`),formCompanyNature:expression("$form.query('.formCompanyNature').take() || {}"),type:expression("$form.query('state').get('data').type"),status:expression("$form.query('state').get('data').status")}}}}});return{__sfc:!0,app,emitTabRemove,t,vendor,props,schema,scope:{$props:props,app,t,DictSelect,observer},components:{CAddress,CCategorySelect,FileDynamic,CFillProgress,stepOne,success,mainEngine,natureChose},RenderEngine}}});var _sfc_render=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{staticClass:"contractPaymentType",attrs:{schemaKey:"companyInfoMaintain",schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const CompanyInfo=__component__.exports;export{CompanyInfo as default};
