import{n as normalizeComponent,ad as expression,ae as i18nExpression,aC as generateCharExpressionByFunction,af as yearMonthDaySelectorSegment,ag as radioGroupByYOrNSegment,aD as requiredValidatorSegment,ah as generateXindexInOrder,ai as editTableFormItemValid,aj as feedbackLayoutIsPopover,ak as defineComponent,al as usePageHelper,am as useAttrs,an as ref$1,ao as useAutoMountInstanceToField,ap as h,aq as defineSchemas,as as performPlanService,at as DictSelect,au as observer,v as validEmail,a as validatePhone,ac as createDictClass,ar as RenderEngine,aE as DictClass,av as CommonFile,a6 as CCategorySelect}from"./index-6b6051d8.js";import{C as CAddress}from"./index-baa5f2f5.js";import{F as FileDynamic}from"./file-dynamic-25a093c4.js";import{C as CFillProgress}from"./index-2c71d18e.js";import{r as resolve}from"./path-4ced4e54.js";import{t as transformMQL}from"./util-d962b17f.js";import{v as vendorOptCommonApi}from"./index-4aa0cc9f.js";import{s as sceneFileApi}from"./basicSetting-fc46a2d9.js";import{n as natureChose}from"./natureChose-d499cf51.js";/* empty css                                                              */import"./file-dynamic-7fc2d358.js";import"./MainHeader-2d842985.js";/* empty css                                                                   */import"./file-dynamic.vue_vue_type_style_index_0_scoped_516fdfc3_lang-f2c1a82d.js";import"./tree-utils-7df6be59.js";import"./BaseTableBind-b1f76fc9.js";import"./util-1e55288f.js";import"./index-4d512f00.js";/* empty css                                                                      */import"./fileApi-05bbbbcc.js";/* empty css                                              */const _sfc_main$5={components:{},data(){return{}},computed:{},async created(){},mounted(){},updated(){},methods:{goTo(where){this.$emit("goToWhere",where)}}};var _sfc_render$5=function(){var _vm=this,_c=_vm._self._c;return _c("el-container",[_c("el-main",{staticClass:"main-stepOne"},[_c("section",{staticClass:"boxs"},[_c("i",{staticClass:"el-icon-warning icon-red icon-big margin"}),_c("span",{staticClass:"margin mainFont"},[_vm._v(_vm._s(_vm.$t("vendorMod.companyInformationNotCertified")))]),_c("span",{staticClass:"margin greyFont"},[_vm._v(_vm._s(_vm.$t("vendorMod.sorryCompanyInformationNotCertified")))]),_c("button",{staticClass:"button-click margin",on:{click:function($event){return _vm.goTo("company-nature")}}},[_vm._v(" "+_vm._s(_vm.$t("vendorMod.continueFilling"))+" ")])])])],1)},_sfc_staticRenderFns$5=[],__component__$5=normalizeComponent(_sfc_main$5,_sfc_render$5,_sfc_staticRenderFns$5,!1,null,"5b317bbc",null,null);const stepOne=__component__$5.exports;const _sfc_main$4={components:{},props:{status:{type:String,default:"APPROVED"},flowRemark:{type:String,default:""}},data(){return{source:this.$t("cusEntry.vendorMod.withdrawRegister")}},computed:{},async created(){},mounted(){},updated(){},methods:{monitorIpAddress(data){return this.$http({url:"/api-sou/bids/ip/address/ipAddress/save",method:"POST",data})},recall(){this.$prompt("",this.$t("bidMod.withdrawReason"),{confirmButtonText:this.$t("common.confirm"),cancelButtonText:this.$t("common.cancel"),inputType:"textarea"}).then(({value})=>{let saveData=transformMQL.save("CompanyInfo",[{companyId:this.$store.getters.userInfo.companyId,flowRemark:value}],"vendorWithdraw");vendorOptCommonApi.withdrawCompanyMQL(saveData).then(async()=>{this.$message({message:this.$t("dashboard.withdrawSuccess"),type:"success"}),this.backToMain();const{companyName,companyCode,companyId}=this.$store.getters.userInfo;await this.monitorIpAddress({supplierId:companyId,supplierCode:companyCode,supplierName:companyName,source:this.source})})})},backToSystem(){let saveData=transformMQL.save("CompanyInfo",[{companyId:this.$store.getters.userInfo.companyId}],"updateFirstLog");vendorOptCommonApi.saveCompanyMQL(saveData).then(()=>{this.$store.dispatch("user/initSystem").then(()=>{this.$router.push({path:resolve("/dashboard")})})})},backToMain(){this.$emit("goToWhere","main")}}};var _sfc_render$4=function(){var _vm=this,_c=_vm._self._c;return _c("el-container",{staticClass:"successContainer"},[_vm.status=="SUBMITTED"?_c("el-main",{staticClass:"main-stepOne"},[_c("section",{staticClass:"boxs"},[_c("span",{staticClass:"success-icon margin"},[_c("i",{staticClass:"el-icon-success"})]),_c("span",{staticClass:"margin mainFont"},[_vm._v(_vm._s(_vm.$t("common.successSubmit")))]),_c("span",{staticClass:"margin greyFont"},[_vm._v(_vm._s(_vm.$t("dashboard.companyInfoSubmitted")))]),_c("section",{staticClass:"flex"},[_c("el-button",{staticClass:"margin button-click",on:{click:_vm.recall}},[_vm._v(" "+_vm._s(_vm.$t("dashboard.withdrawSubmissionInfo"))+" ")]),_c("el-button",{staticClass:"margin button-click",attrs:{type:"primary"},on:{click:_vm.backToMain}},[_vm._v(" "+_vm._s(_vm.$t("vendorMod.viewCompanyInformation"))+" ")])],1)])]):_vm._e(),_vm.status=="APPROVED"?_c("el-main",{staticClass:"main-stepOne"},[_c("section",{staticClass:"boxs"},[_c("span",{staticClass:"success-icon margin"},[_c("i",{staticClass:"el-icon-success"})]),_c("span",{staticClass:"margin mainFont"},[_vm._v(_vm._s(_vm.$t("common.successRegister")))]),_c("span",{staticClass:"margin greyFont"},[_vm._v(_vm._s(_vm.$t("vendorMod.companyInformationSuccess")))]),_c("el-button",{staticClass:"margin button-click",attrs:{type:"primary"},on:{click:_vm.backToSystem}},[_vm._v(" "+_vm._s(_vm.$t("vendorMod.enterSystem"))+" ")])],1)]):_vm._e(),_vm.status=="REJECTED"?_c("el-main",{staticClass:"main-stepOne"},[_c("section",{staticClass:"boxs"},[_c("i",{staticClass:"el-icon-error icon-grey icon-big margin"}),_c("span",{staticClass:"margin mainFont"},[_vm._v(_vm._s(_vm.$t("common.failedRegister")))]),_c("span",{staticClass:"margin greyFont"},[_vm._v(_vm._s(_vm.$t("vendorMod.companyInformationFailed"))+_vm._s(_vm.flowRemark))]),_c("el-button",{staticClass:"margin button-click",attrs:{type:"primary"},on:{click:_vm.backToMain}},[_vm._v(" "+_vm._s(_vm.$t("vendorMod.backCompanyInformation"))+" ")])],1)]):_vm._e()],1)},_sfc_staticRenderFns$4=[],__component__$4=normalizeComponent(_sfc_main$4,_sfc_render$4,_sfc_staticRenderFns$4,!1,null,"26f26eac",null,null);const success=__component__$4.exports,companyNatureEngine={steps:{type:"void","x-decorator":"div","x-decorator-props":{class:"comInfosteps"},"x-component":"Steps","x-component-props":{alignCenter:!0,finishStatus:"success"},"x-reactions":{dependencies:["status"],fulfill:{state:{"component[1].active":2}}},"x-visible":!1,properties:{step1:{type:"void","x-component":"el-step","x-component-props":{title:expression("$t('vendorMod.registrationPolicy')")}},step2:{type:"void","x-component":"el-step","x-component-props":{title:expression("$t('vendorMod.registerAccount')")}},step3:{type:"void","x-component":"el-step","x-component-props":{title:expression("$t('vendorMod.registrationType')")}},step4:{type:"void","x-component":"el-step","x-component-props":{title:expression("$t('vendorMod.authenticationInformation')")}},step5:{type:"void","x-component":"el-step","x-component-props":{title:expression("$t('vendorMod.pendingApproval')")}},step6:{type:"void","x-component":"el-step","x-component-props":{title:expression("$t('vendorMod.informationAuthentication')")}}}},formCompanyNature:{type:"object","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical",class:"boxs-row"},"x-component":"FormGrid","x-component-props":{maxColumns:1,columnGap:32,rowGap:0},"x-query-engine-skip":!0,properties:{overseasRelation:{type:"string",default:"INSIDE","x-decorator":"FormItem","x-component":"natureChose","x-component-props":{style:"margin:18px 0 15px 0;","@change":expression(`(who) => {
            $self.value = who
          }`)},title:expression("$t('vendorMod.overseasRelation')"),"x-validator":{required:!0,message:i18nExpression("common.requiredField")}}}}};const _sfc_main$3={components:{},props:{stepsActive:{type:Number},status:{type:String,default:"DRAFT"},fileStatus:{type:String,default:"DRAFT"},flowRemark:{type:String},registered:{type:Boolean,default:!1}},data(){return{}},computed:{setFlowRemark(){const replaceRemark=this.flowRemark?.replace(/\s+/g,"; ")??"";return replaceRemark?replaceRemark.includes(";")?replaceRemark.slice(0,replaceRemark.length-2):replaceRemark:""},approvedTips(){let res={};return this.fileStatus=="APPROVED"?res={tips1:this.$t("cusEntry.mainHeater.boxsHeater"),tips2:this.$t("cusEntry.mainHeater.boxsHeater2")}:res={tips1:this.$t("mainHeater.boxsHeater"),tips2:this.$t("mainHeater.boxsHeater2")},res},rejectTips(){let res={};return this.fileStatus=="REJECTED"?res={tips1:this.$t("cusEntry.mainHeater.reject"),tips2:this.$t("cusEntry.mainHeater.reject2")}:res={tips1:this.$t("mainHeater.reject"),tips2:this.$t("mainHeater.reject2")},res},approvingTips(){let res={};return this.fileStatus=="SUBMITTED"?res={tips1:this.$t("cusEntry.mainHeater.approval"),tips2:this.$t("cusEntry.mainHeater.approval2")}:res={tips1:this.$t("mainHeater.approval"),tips2:this.$t("mainHeater.approval2")},res}},async created(){},mounted(){},updated(){},methods:{goTo(where){this.$emit("goToWhere",where)}}};var _sfc_render$3=function(){var _vm=this,_c=_vm._self._c;return _c("div",[_vm.registered?_vm._e():_c("el-steps",{staticClass:"comInfosteps",attrs:{active:_vm.stepsActive,"finish-status":"success","align-center":!0}},[_c("el-step",{attrs:{title:_vm.$t("vendorMod.registrationPolicy")}}),_c("el-step",{attrs:{title:_vm.$t("vendorMod.registerAccount")}}),_c("el-step",{attrs:{title:_vm.$t("vendorMod.registrationType")}}),_c("el-step",{attrs:{title:_vm.$t("vendorMod.authenticationInformation")}}),_c("el-step",{attrs:{title:_vm.$t("vendorMod.pendingApproval")}}),_c("el-step",{attrs:{title:_vm.$t("vendorMod.informationAuthentication")}})],1),_vm.status=="APPROVED"?_c("section",{staticClass:"boxs_success boxs_heater"},[_c("div",[_c("i",{staticClass:"el-icon-circle-check"}),_c("span",[_vm._v(_vm._s(_vm.approvedTips.tips1))])]),_c("div",{staticClass:"small_font"},[_vm._v(" "+_vm._s(_vm.approvedTips.tips2)+" ")])]):_vm._e(),_vm.status=="REJECTED"?_c("section",{staticClass:"boxs_rejected boxs_heater"},[_c("div",[_c("i",{staticClass:"el-icon-circle-close"}),_c("span",[_vm._v(_vm._s(_vm.rejectTips.tips1))])]),_c("div",{staticClass:"small_font"},[_vm._v(" "+_vm._s(_vm.rejectTips.tips2)+" ")]),_c("div",{staticClass:"small_font"},[_vm._v(" "+_vm._s(_vm.$t("cusEntry.vendorMod.rejectReason"))+_vm._s(_vm.flowRemark)+" ")])]):_vm._e(),_vm.status=="SUBMITTED"?_c("section",{staticClass:"boxs_submitted boxs_heater"},[_c("div",[_c("i",{staticClass:"el-icon-warning-outline"}),_c("span",[_vm._v(_vm._s(_vm.approvingTips.tips1))])]),_c("div",{staticClass:"small_font"},[_vm._v(" "+_vm._s(_vm.approvingTips.tips2)+" ")])]):_vm._e()],1)},_sfc_staticRenderFns$3=[],__component__$3=normalizeComponent(_sfc_main$3,_sfc_render$3,_sfc_staticRenderFns$3,!1,null,"5314e19d",null,null);const MainHerder=__component__$3.exports,userInfoForm={userInfoForm:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.vendorUserInfo")},"x-query-engine-skip":!0,properties:{userInfo:{type:"object","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},"x-query-engine-skip":!0,properties:{username:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.userName')"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled || $form.values.dataSources === 'ONESELF_REGISTER' || $form.query('state').get('data').dataFrom === 'vendor'"),placeholder:i18nExpression("userInfo.onlyNumOrEn"),"@change":expression(`() => {
                                let values = $self.value
                                console.log($self)
                                $self.setValue(values.replace(/[\\W]/g, ''))
                              }`)},"x-validator":{required:!0,message:i18nExpression("vendorMod.enterUserName")}},phone:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.contactPhone')"),"x-component-props":{disabled:!0},"x-validator":{required:!0,message:i18nExpression("dataConfMod.msgContactPhone"),validator:expression(`(value, rule) => {
                if(value && !validatePhone(value)){
                  return $t('vendorMod.correctPhoneNumber')
                }
              }`)}},email:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('common.email')"),"x-component-props":{disabled:!0},"x-validator":{required:!0,message:i18nExpression("vendorMod.pleaseInputEmail"),validator:expression(`(value, rule) => {
                if(value && !validEmail(value)){
                  return $t('vendorMod.correctEmail')
                }
              }`)}},lcCode:{type:"void",title:expression("$t('cusEntry.supplement20250218.lcCode')"),"x-component":"el-tooltip","x-component-props":{content:expression("$t('cusEntry.supplement20250218.lcCode')")},properties:{lcCode:{type:"string",title:expression("$t('vendorMod.lcCode')"),"x-decorator":"FormItem","x-component":"Input","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled || $form.values.dataSources == 'MANUALLY_CREATE'"),"@blur":expression(`(value) => {
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
                            // 该供应商已被注册，请联系' + maskedName + '，账号：' + maskedPhone + '，进行子账号维护操作。
                            app.$alert($t('cusEntry.vendorMod.registerTips', { maskedName, maskedPhone }), '', {
                              confirmButtonText: $t('common.confirm'),
                              callback: action => {}
                            });
                          }
                        }
                      })
                    }
                  }`)},"x-validator":{required:!0,message:i18nExpression("vendorMod.msgLcCode")}}}},accountGroup:{type:"string",default:"Z001","x-decorator":"FormItem","x-component":"Input",title:expression("$t('cusEntry.vendorMod.accountGroup')"),"x-component-props":{disabled:!0}}}}}}},companyType={companyTypeAll:{type:"void","x-component":"CollapseItem","x-component-props":{title:`{{observer(
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
      )}}`},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").overseasRelation!=="PERSONAL"),"x-query-engine-skip":!0,properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:{flowRemark:{type:"string","x-decorator":"FormItem","x-hidden":!0},status:{type:"string",default:"DRAFT","x-hidden":!0},domesticAndForeignRelations:{type:"string","x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"RELATION_NEW",disabled:expression("$form.query('state').get('data').$disabled")},title:expression("$t('cusEntry.vendorMod.domesticAndForeignRelations')"),"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},companyType:{type:"string","x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"COMPANY_NATURE_NEW",disabled:expression("$form.query('state').get('data').$disabled")},title:expression("$t('cusEntry.vendorMod.vendorType')"),"x-validator":{required:!0,message:i18nExpression("common.requiredField")}}}}}}},companyInfo={companyInfo:{type:"void","x-component":"CollapseItem","x-component-props":{title:`{{observer(
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
                // if ($form.query('state').get('data').overseasRelation === 'INSIDE') {
                //   // 读取图片信息
                //   app.$http({
                //     url: '/api-pj/ocr/recognizeLcImage',
                //     method: 'GET',
                //     params: { fileuploadId: fileId },
                //     loading: true
                //   }).then(res => {
                //     const {
                //       regNum,
                //       person,
                //       name,
                //       address,
                //       business,
                //       businessEndDate,
                //       businessStartDate,
                //       capital,
                //       period,
                //       setDate,
                //       type
                //     } = res.data
                //     $form.query('.companyType').take().value = type
                //     $form.query('.companyName').take().value = name
                //     $form.query('.legalPerson').take().value = person
                //     $form.query('.lcCode').take().value = regNum
                //     // $form.query('.registeredCapital').take().value = licenseData.registeredCapital
                //     // $form.query('.registCurrency').take().value = licenseData.registCurrency
                //     $form.query('.companyAddress').take().value = address
                //     $form.query('.businessScope').take().value = business
                //     const [year, month, day] = setDate.replace(/[^\\d]/g, '-').split('-')
                //     const createDate = year + '-' + month + '-' + day
                //     $form.query('.companyCreationDate').take().value = app.$dayjs(createDate).format('YYYY-MM-DD')
                //     // 校验供应商是否已注册
                //     if (regNum) {
                //       app.$http({
                //         url: '/api-rbac/extUser/lcCodeVerify',
                //         method: 'GET',
                //         params: { lcCode: regNum, isPersonalAccount: 'N' }
                //       }).then(res => {
                //         if (res.code + '' === '0') {
                //           const data = res.data
                //           if (data.isRemind === 'Y') {
                //             let maskedName = ''
                //             if(data.nickname.length==2){
                //               maskedName=data.nickname.substring(0,1)+'*'
                //             }else if(data.nickname.length==3){
                //               maskedName=data.nickname.substring(0,1)+"*"+data.nickname.substring(2,3)
                //             }else if(data.nickname.length>3){
                //               maskedName=data.nickname.substring(0,1)+"*"+'*'+data.nickname.substring(3,data.nickname.length)
                //             }
                //             const maskedPhone = data.phone.replace(data.phone.substring(3,7),'****')
                //             // '该供应商已被注册，请联系' + maskedName + '，账号：' + maskedPhone + '，进行子账号维护操作。'
                //             app.$alert($t('cusEntry.vendorMod.registerTips', { maskedName, maskedPhone }), '', {
                //               confirmButtonText: $t('common.confirm'),
                //               callback: action => {}
                //             });
                //           }
                //         }
                //       })
                //     }
                //   })
                //   .catch(err => {
                //     console.log(err)
                //   })
                // }
                $form.query('.businessLicenseFileId').take().value = fileId.toString()
                $form.query('.businessLicense').take().value = fileName
              }`)},title:""},layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical",style:{width:"67%","padding-left":"20px"}},"x-component":"FormGrid","x-component-props":{maxColumns:2,columnGap:32,rowGap:0},properties:{companyName:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.companyName')"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},"x-validator":{required:!0,message:i18nExpression("vendorMod.msgCompanyName")}},companyShortName:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.companyShortName')"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},"x-validator":{required:!0,message:i18nExpression("vendorMod.msgCompanyShortName")}},companyEnName:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('cusEntry.vendorMod.companyEnName')"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},enterpriseNo:{type:"string",title:i18nExpression("cusEntry.vendorMod.enterpriseNo"),"x-decorator":"FormItem","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},"x-validator":{required:!0,message:i18nExpression("vendorMod.msgLegalPerson")}},extKpp:{type:"string",title:i18nExpression("cusEntry.vendorMod.extKpp"),"x-decorator":"FormItem","x-component":"Input","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},legalPerson:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.legalPerson')"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},"x-validator":{required:!0,message:i18nExpression("vendorMod.msgLegalPerson")}},nickname:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('dataConfMod.userName')"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},"x-validator":{required:!0,message:i18nExpression("vendorMod.msgInputNickname")}},position:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('components.orgPositionSel.position')"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},registCurrency:{type:"string","x-hidden":!0},registeredCapital:{type:"number","x-visible":expression("$form.query('.companyType').take().value != 'GETI'"),"x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.registeredCapital')"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),class:"input-with-select","@change":expression(`(value) => {
                    $self.value = value.replace(/[^\\d.]/g, '')
                  }`)},"x-content":{append:expression(`observer(
                    {
                      render(h) {
                        const targetField = $self.query('.registCurrency').take()
                        return h("div", {class: "bzBox"}, [
                          h("label", {class: "bzTitle"}, $t('vendorMod.currencyCode')),
                            h(DictSelect, {
                              props: {
                                value: targetField.value,
                                code: 'currency',
                              },
                              attrs: {
                                disabled: $form.query('state').get('data').$disabled,
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
                  `)},"x-validator":{required:expression("!['GETI','FEIYINGLI'].includes($form.query('.companyType').take().value)"),message:i18nExpression("vendorMod.msgRegisteredCapital")}},ifLongPeriod:{type:"string",title:i18nExpression("cusEntry.vendorMod.ifLongTermSupplier"),"x-component":"DictSelect","x-component-props":{code:"YES_OR_NO",disabled:expression("$form.query('state').get('data').$disabled")},default:"Y","x-decorator":"FormItem","x-validator":{required:!0,message:i18nExpression("cusEntry.tipMessage.ifLongPeriodMsg")}},businessEndDate:{type:"date","x-hidden":!0},businessStartDate:{...yearMonthDaySelectorSegment,"x-decorator":"FormItem","x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],disabled:expression("$form.query('state').get('data').$disabled")},title:expression("$t('vendorMod.dateBusiness')"),"x-validator":{required:expression("$form.query('ifLongPeriod').take().value === 'N'"),message:i18nExpression("vendorMod.msgCreationDate")}},companyCreationDate:{...yearMonthDaySelectorSegment,"x-decorator":"FormItem","x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],disabled:expression("$form.query('state').get('data').$disabled")},title:expression("$t('vendorMod.creationDate')"),"x-validator":{required:expression("!['FEIYINGLI'].includes($form.query('.companyType').take().value)"),message:i18nExpression("vendorMod.msgCreationDate")}},businessScope:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.businessScope')"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),type:"textarea",maxlength:2e3},"x-decorator-props":{gridSpan:3}}}}}}}}},companyBaseInfo={companyBaseInfo:{type:"void","x-component":"CollapseItem","x-component-props":{title:`{{observer(
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
      )}}`},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").overseasRelation!=="PERSONAL"),"x-query-engine-skip":!0,properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical",feedbackLayout:"terse"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:{ceeaBusinessModel:{type:"string","x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),code:"BIZ_MODEL",multiple:!0},title:expression("$t('vendorMod.bizModel')")},ceeaIfListed:{title:i18nExpression("vendorMod.ifListed"),...radioGroupByYOrNSegment,"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},ceeaListedTime:{...yearMonthDaySelectorSegment,"x-visible":"{{$form.query('.ceeaIfListed').take().value == 'Y'}}","x-decorator":"FormItem","x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],disabled:expression("$form.query('state').get('data').$disabled")},title:expression("$t('vendorMod.listedDate')"),"x-validator":{required:!0,message:i18nExpression("common.marketTime")}},categoryName:{type:"string","x-hidden":!0},cateJournalList:{type:"Array","x-hidden":!0},companyCountry:{type:"string",title:i18nExpression("components.address.country"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"country",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$form.query('state').get('data').$disabled"),"@change":expression(`(val) => {
                let row = $form.values
                row.companyProvince = null
                row.companyCity = null
              }`)},"x-reactions":expression(`() => {
              const data = $taxDictClass.getDictDetail('country', $form.values.companyCountry)
              $form.query('.companyProvince').take()?.setComponentProps({ code: data ? data.description : undefined })
            }`),...requiredValidatorSegment},companyProvince:{type:"string",title:i18nExpression("components.address.area"),"x-reactions":expression(`() => {
              $self.visible = ['CN', 'RU'].includes($form.values.companyCountry)
            }`),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{"custom-select-type":"PROVINCE",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$form.query('state').get('data').$disabled"),"@change":expression(`(val) => {
                $form.values.companyCity = null
              }`)},...requiredValidatorSegment},companyCity:{type:"string",title:i18nExpression("components.address.city"),"x-reactions":expression(`() => {
              $self.visible = ['CN', 'RU'].includes($form.values.companyCountry)
            }`),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:expression("$form.values.companyProvince || ''"),"custom-select-type":"CITY",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$form.query('state').get('data').$disabled || !$form.values.companyProvince")},...requiredValidatorSegment},companyAddress:{type:"string","x-decorator":"FormItem","x-component":"Input",title:i18nExpression("cusEntry.vendorMod.detailAddress"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},"x-validator":{required:!0,message:i18nExpression("vendorMod.msgDetailAddr")}},ceeaHasParentCompany:{title:i18nExpression("cusEntry.vendorMod.ifParentCompany"),...radioGroupByYOrNSegment,"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},ceeaParentCompanyName:{type:"string","x-decorator":"FormItem","x-component":"Input","x-visible":"{{$form.query('.ceeaHasParentCompany').take().value == 'Y'}}",title:i18nExpression("cusEntry.vendorMod.parentCompanyName"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},"x-validator":{required:!0,message:i18nExpression("cusEntry.vendorMod.parentCompanyNameEnterTips")}},groupCountry:{type:"string",title:i18nExpression("cusEntry.vendorMod.parentCompanyCountry"),"x-visible":"{{$form.query('.ceeaHasParentCompany').take().value == 'Y'}}","x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"country",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$form.query('state').get('data').$disabled")},"x-validator":{required:!0,message:i18nExpression("cusEntry.tipMessage.parentCompanyCountryMsg")}},ceeaCompanyIntro:{type:"string","x-decorator":"FormItem","x-component":"Input.TextArea",title:expression("$t('vendorMod.companyProfile')"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),maxlength:2e3},"x-decorator-props":{gridSpan:3}}}}}}},contactInfoList={contactInfoList:{type:"void","x-component":"CollapseItem","x-component-props":{title:`{{observer(
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
              }`)}}}},contactInfos:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,maxHeight:400,pagination:!1,sortable:!1,primaryKey:"contactInfoId",cascadeDeletion:!0},"x-query-engine-skip":!0,properties:generateXindexInOrder({contactName:{type:"string",title:i18nExpression("vendorMod.nickname"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},...editTableFormItemValid},ceeaDeptName:{type:"string",title:i18nExpression("vendorMod.department"),"x-render-table-column":{minWidth:100},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},position:{type:"string",title:i18nExpression("dataConfMod.position"),"x-render-table-column":{minWidth:100},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},ceeaContactMethod:{type:"string",title:i18nExpression("vendorMod.contactMethod"),"x-render-table-column":{minWidth:100},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},"x-validator":{required:!0,message:i18nExpression("vendorMod.correctPhoneNumber"),validator:expression(`(value, rule) => {
                if(value && !validatePhone(value)) {
                  return $t('vendorMod.correctPhoneNumber')
                }
              }`)},...feedbackLayoutIsPopover},email:{type:"string",title:i18nExpression("vendorMod.email"),"x-render-table-column":{minWidth:100},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},"x-validator":{required:!0,message:i18nExpression("vendorMod.correctEmail"),validator:expression(`(value, rule) => {
                if(value && !validEmail(value)){
                  return $t('vendorMod.correctEmail')
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
              }`)}},ceeaComments:{type:"string",title:i18nExpression("dataConfMod.remark"),"x-render-table-column":{minWidth:100},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:60,fixed:"right"},"x-component":"RenderTableButtonList","x-visible":expression("!$form.query('state').get('data').$disabled"),properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{type:"text","@click":expression(`({ row }) => {
                      $table.remove($self.index)
                  }`)}}}}})}}}},serviceRange={serviceRange:{type:"void","x-component":"CollapseItem","x-component-props":{title:`{{observer(
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
              }`)}}}},serviceRangeList:{type:"array","x-component":"ArrayItems","x-reactions":expression(`() => {
          $self.visible = $form.values.serviceRangeList.length > 0
        }`),items:{type:"void",properties:{tableForm:{type:"object",properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:{categoryName:{type:"string","x-decorator":"FormItem","x-component":"QuickSearchWrapper","x-component-props":{dialogLabel:i18nExpression("cusEntry.vendorMod.categoryNameTitle"),showKey:"categoryName",name:"scc_base_purchase_category4","@close-quicksearch":expression(`val => {
                          let form = $form.query($self.parent.parent.address.toString()).take().value
                          form.categoryCode = val?.categoryCode || ''
                          form.categoryName = val?.categoryName || ''
                          form.categoryId = val?.categoryId || null
                          const [oneLevel, twoLevel] = val?.categoryFullName?.split('-')
                          form.categoryFullName = oneLevel + '-' + twoLevel || ''
                        }`),disabled:expression("$form.query('state').get('data').$disabled")},title:"{{t('cusEntry.vendorMod.category', {index: $self.index + 1})}}"},formBtn:{type:"void","x-component":"ButtonList","x-component-props":{style:{"margin-top":"5px"}},"x-visible":expression("!$form.query('state').get('data').$disabled"),properties:{add:{type:"void","x-component-props":{type:"primary","@click":expression(`() => {
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
                            }`)}}}}}}}},list:{type:"array","x-component":"RenderTable","x-query-engine-skip":!0,"x-component-props":{preColumns:"seq",editMode:!0,maxHeight:250,pagination:!1,sortable:!1},properties:generateXindexInOrder({performanceAmount:{type:"number",title:i18nExpression("cusEntry.vendorMod.performance"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},mainCustom:{type:"string",title:i18nExpression("cusEntry.vendorMod.mainCustomer"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},fileId:{type:"string",title:i18nExpression("cusEntry.vendorMod.achievement"),"x-render-table-column":{minWidth:120},"x-component":"SrmCommonFile","x-component-props":{extraData:{fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},defaultFile:{fileId:expression("$table.getRowByIndex($self.index)?.fileId"),fileName:expression("$table.getRowByIndex($self.index)?.fileName")},readonly:expression("$form.query('state').get('data').$disabled"),"@on-change":expression(`({file}) => {
                      const { fileId = '', fileName = '' } = file || {}
                      let row = $table.getRowByIndex($self.index)
                      row.fileId = fileId
                      row.fileName = fileName
                    }`)}},operation:{type:"void",title:i18nExpression("common.operation"),"x-render-table-column":{width:60,fiexd:"right"},"x-visible":expression("!$form.query('state').get('data').$disabled"),"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:i18nExpression("common.delete"),"x-component-props":{type:"text","@click":expression(`() => {
                          const { serciceCustomId = null } = $table.getRowByIndex($self.index) || {}
                          if (serciceCustomId) {
                            let serciceCustomDelList = $form.query('state').get('data').serciceCustomDelList
                            serciceCustomDelList.push({
                              $delete: serciceCustomId
                            })
                          }
                          $table.remove($self.index)
                        }`)}}}}})}}}}}}},authInfo={authInfo:{type:"void","x-component":"CollapseItem","x-component-props":{title:`{{observer(
        {
          render(h) {
            return h($$components.Note, {
              props: {
                title: t('cusEntry.vendorMod.authInfo'),
                value: $form.values.extRejectAttribute10,
                readonly: true
              }
            })
          }
        }
      )}}`},"x-visible":expression("$form.query('state').get('data').overseasRelation != 'PERSONAL'"),properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:{certifiedContact:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('cusEntry.vendorMod.certifiedContact')"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),maxlength:100}},certifiedContactPhone:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('cusEntry.vendorMod.certifiedContactPhone')"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},"x-validator":{message:i18nExpression("dataConfMod.msgContactPhone"),validator:expression(`(value, rule) => {
                if(value && !validatePhone(value)){
                  return $t('vendorMod.correctPhoneNumber')
                }
              }`)}}}}}}},personBaseInfo={person:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("cusEntry.vendorMod.baseInfo")},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").overseasRelation==="PERSONAL"),properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:{personBaseInfo:{type:"object","x-query-engine-skip":!0,properties:{businessLicenseFileId:{type:"string",title:i18nExpression("cusEntry.vendorMod.frontOfIdCard"),"x-decorator":"FormItem","x-component":"SrmCommonFile","x-component-props":{"extra-data":{uploadType:"DEF",sourceType:"WEB_APP",fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},"default-file":{fileId:expression("$form.query('personBaseInfo').get('value').businessLicenseFileId"),fileName:expression("$form.query('personBaseInfo').get('value').businessLicense")},"@on-change":expression(`({file}) => {
                     const { fileId = null, fileName = null } = file || {}
                     $form.query('personBaseInfo').get('value').businessLicenseFileId = fileId
                     $form.query('personBaseInfo').get('value').businessLicense = fileName
                  }`),readonly:expression("$form.query('state').get('data').$disabled")},...requiredValidatorSegment},extIdCardOppositeFileName:{type:"string","x-hidden":!0},extIdCardOppositeFileId:{type:"string",title:i18nExpression("cusEntry.vendorMod.backOfIdCard"),"x-decorator":"FormItem","x-component":"SrmCommonFile","x-component-props":{"extra-data":{uploadType:"DEF",sourceType:"WEB_APP",fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},"default-file":{fileId:expression("$form.query('personBaseInfo').get('value').extIdCardOppositeFileId"),fileName:expression("$form.query('personBaseInfo').get('value').extIdCardOppositeFileName")},"@on-change":expression(`({file}) => {
                    const { fileId = null, fileName = null } = file || {}
                    $form.query('personBaseInfo').get('value').extIdCardOppositeFileId = fileId
                    $form.query('personBaseInfo').get('value').extIdCardOppositeFileName = fileName
                  }`),readonly:expression("$form.query('state').get('data').$disabled")},...requiredValidatorSegment},companyName:{type:"string","x-decorator":"FormItem",title:i18nExpression("cusEntry.vendorMod.companyNameOrPersonName"),...requiredValidatorSegment,"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},companyShortName:{type:"string","x-decorator":"FormItem",title:i18nExpression("cusEntry.vendorMod.personalAbbreviation"),...requiredValidatorSegment,"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),maxlength:100,"show-word-limit":!0}},businessLicense:{type:"string","x-hidden":!0},idNumber:{type:"string","x-decorator":"FormItem",title:i18nExpression("cusEntry.vendorMod.idNo"),...requiredValidatorSegment,"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},validityPeriodOfCard:{...yearMonthDaySelectorSegment,"x-decorator":"FormItem",title:i18nExpression("cusEntry.vendorMod.validityPeriodOfCard"),"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],type:"daterange",disabled:expression("$form.query('state').get('data').$disabled")},...requiredValidatorSegment},businessScope:{type:"string",title:i18nExpression("cusEntry.vendorMod.mainBusinessScope"),"x-decorator":"FormItem","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},enterpriseNo:{type:"string",title:i18nExpression("cusEntry.vendorMod.enterpriseNo"),"x-decorator":"FormItem","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},...requiredValidatorSegment},extKpp:{type:"string",title:i18nExpression("cusEntry.vendorMod.extKpp"),"x-decorator":"FormItem","x-component":"Input","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},companyCountry:{type:"string",title:i18nExpression("components.address.country"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"country",disabled:expression("$form.query('state').get('data').$disabled"),placeholder:expression("$t('common.pleaseSelect')"),"@change":expression(`(val) => {
                    $form.query('personBaseInfo.companyProvince').take().value = ''
                    $form.query('personBaseInfo.companyCity').take().value = ''
                  }`)},"x-reactions":expression(`() => {
                  const data = $taxDictClass.getDictDetail('country', $self.value)
                  $form.query('personBaseInfo.companyProvince').take()?.setComponentProps({ code: data ? data.description : undefined })
                }`),...requiredValidatorSegment},companyProvince:{type:"string",title:i18nExpression("components.address.area"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),"custom-select-type":"PROVINCE",placeholder:expression("$t('common.pleaseSelect')"),"@change":expression(`(val) => {
                    $form.query('personBaseInfo.companyCity').take().value = ''
                  }`)},"x-visible":"{{ ['CN', 'RU'].includes($form.query('personBaseInfo.companyCountry').take()?.value) }}",...requiredValidatorSegment},companyCity:{type:"string",title:i18nExpression("components.address.city"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:expression("$form.query('personBaseInfo.companyProvince').take()?.value || ''"),"custom-select-type":"CITY",emptyOptionCanCreat:!0,placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$form.query('state').get('data').$disabled || !$form.query('personBaseInfo.companyProvince').take()?.value")},"x-visible":"{{ ['CN', 'RU'].includes($form.query('personBaseInfo.companyCountry').take()?.value) }}",...requiredValidatorSegment},companyAddress:{type:"string",title:i18nExpression("cusEntry.vendorMod.detailAddress"),"x-decorator":"FormItem",...requiredValidatorSegment,"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},position:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('components.orgPositionSel.position')"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}}}}}}}}},vendorSiteInfoList={vendorSiteInfo:{type:"void","x-component":"CollapseItem","x-component-props":{title:`{{observer(
        {
          render(h) {
            return h($$components.Note, {
              props: {
                title: t('vendorMod.vendorSiteInfos2'),
                value: '',
                readonly: true
              }
            })
          }
        }
      )}}`},"x-query-engine-skip":!0,properties:{toolbar:{type:"void","x-component":"Space","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void",title:i18nExpression("common.new"),"x-component":"RButton","x-component-props":{type:"primary",disabled:expression("$form.query('state').get('data').$disabled"),"@click":expression(`() => {
                 $self.query('companyAddressInfos')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
              }`)}}}},companyAddressInfos:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,maxHeight:400,pagination:!1,sortable:!1,primaryKey:"companyAddressId",cascadeDeletion:!0},"x-query-engine-skip":!0,properties:generateXindexInOrder({country:{type:"string",title:i18nExpression("components.address.country"),"x-render-table-column":{minWidth:120},"x-component":"DictSelect","x-component-props":{code:"country",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$form.query('state').get('data').$disabled"),"@change":expression(`(val) => {
                let row = $table.getRowByIndex($self.index)
                row.area = null
                row.city = null
              }`)},"x-reactions":expression(`() => {
              const data = $taxDictClass.getDictDetail('country', $self.value)
              $form.query('CompanyInfo.layout.collapse.vendorSiteInfo.companyAddressInfos.' + [$self.index] + '.area').take()?.setComponentProps({ code: data ? data.description : undefined })
            }`),"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},area:{type:"string",title:i18nExpression("components.address.area"),"x-render-table-column":{minWidth:120},"x-component":"DictSelect","x-component-props":{"custom-select-type":"PROVINCE",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$form.query('state').get('data').$disabled || !['CN', 'RU'].includes($table.getRowByIndex($self.index).country)"),"@change":expression(`(val) => {
                $table.getRowByIndex($self.index).city = null
              }`)}},city:{type:"string",title:i18nExpression("components.address.city"),"x-render-table-column":{minWidth:120},"x-component":"DictSelect","x-component-props":{code:expression("$table.getRowByIndex($self.index).area || ''"),"custom-select-type":"CITY",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$form.query('state').get('data').$disabled || !$table.getRowByIndex($self.index).area")}},address:{type:"string",title:i18nExpression("components.address.detailAddress"),"x-render-table-column":{minWidth:150},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},postalCode:{type:"string",title:i18nExpression("components.address.postalCode"),"x-render-table-column":{minWidth:150},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},remark:{type:"string",title:i18nExpression("components.address.remark"),"x-render-table-column":{minWidth:150},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},isActive:{type:"string",title:i18nExpression("common.enable"),"x-render-table-column":{minWidth:100},"x-component":"Checkbox","x-component-props":{"true-label":"Y","false-label":"N",disabled:expression("$form.query('state').get('data').$disabled")}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),type:"text","@click":expression(`({ row }) => {
                      $table.remove($self.index)
                  }`)}}}}})}}}},qualificationInformation={qualificationInformation:{type:"void","x-component":"CollapseItem","x-component-props":{title:`{{observer(
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
      )}}`},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").supplementAble||$form.values.extIsQualifiedFileUpload=="Y"||$form.values.dataSources=="MANUALLY_CREATE"),"x-query-engine-skip":!0,properties:{qualificationInfo:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:expression("$form.query('state').get('data').supplementAble || ($form.values.dataSources == 'MANUALLY_CREATE' && $form.values.ifSupplierCompleteInfo != 'Y')"),height:350,pagination:!1,sortable:!1,primaryKey:"managementAttachId",cascadeDeletion:!0},"x-query-engine-skip":!0,"x-query-engine-relation":"managementAttaches:*",properties:generateXindexInOrder({authNum:{type:"string","x-component":"DictSelect","x-component-props":{code:expression("'CERTIFICATE_TYPE_' + $form.query('state').get('data').overseasRelation")},title:i18nExpression("cusEntry.vendorMod.certificateType"),"x-render-table-column":{minWidth:200},"x-read-pretty":!0},startDate:{...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.startDate, '{y}-{m}-{d}')
              }`),"@change":expression(`(value) => {
                if(!($table.getRowByIndex($self.index).endDate && $table.getRowByIndex($self.index).startDate)){
                  $table.getRowByIndex($self.index).extCertificatePeriod = null
                  return
                }
                let date1 = new Date($table.getRowByIndex($self.index).endDate)
                let date2 = new Date($table.getRowByIndex($self.index).startDate)
                let timeDifference = date1 - date2
                const dayDifference = Math.floor(timeDifference / (1000 * 60 * 60 * 24));
                if(dayDifference < 0){
                  $table.getRowByIndex($self.index).extCertificatePeriod = 0+$t('bidMod.heaven')
                  return
                }
                $table.getRowByIndex($self.index).extCertificatePeriod = dayDifference+$t('bidMod.heaven')
              }`)},title:i18nExpression("cusEntry.vendorMod.startTime"),"x-render-table-column":{minWidth:180},...editTableFormItemValid},extIfEndDateRequired:{type:"string",title:i18nExpression("cusEntry.vendorMod.extIfEndDateRequired"),"x-render-table-column":{minWidth:180},"x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"},...editTableFormItemValid},endDate:{type:"void","x-component":"Space",title:i18nExpression("cusEntry.vendorMod.endTime"),"x-render-table-column":{minWidth:180},properties:{endDate:{...yearMonthDaySelectorSegment,"x-visible":expression("$table.getRowByIndex($self.index).extIfEndDateRequired === 'Y'"),"x-component-props":{style:"width: 100%",...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                    parseTime(row.endDate, '{y}-{m}-{d}')
                  }`),"@change":expression(`(value) => {
                    if(!($table.getRowByIndex($self.index).endDate && $table.getRowByIndex($self.index).startDate)){
                      $table.getRowByIndex($self.index).extCertificatePeriod = null
                      return
                    }
                    let date1 = new Date($table.getRowByIndex($self.index).endDate)
                    let date2 = new Date($table.getRowByIndex($self.index).startDate)
                    let timeDifference = date1 - date2
                    const dayDifference = Math.floor(timeDifference / (1000 * 60 * 60 * 24))
                    if(dayDifference < 0){
                      $table.getRowByIndex($self.index).extCertificatePeriod = 0 + $t('bidMod.heaven')
                      return
                    }
                    $table.getRowByIndex($self.index).extCertificatePeriod = dayDifference + $t('bidMod.heaven')
                  }`)}}}},extCertificatePeriod:{type:"string",title:i18nExpression("cusEntry.vendorMod.extCertificatePeriod"),"x-render-table-column":{minWidth:120},"x-read-pretty":!0},extIsMandatory:{type:"string","x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"},title:i18nExpression("dataConfMod.isRequested"),"x-render-table-column":{minWidth:120},"x-read-pretty":!0},fileuploadId:{type:"void","x-component":"Space",title:i18nExpression("cusEntry.vendorMod.file"),"x-render-table-column":{minWidth:150},properties:{starFlag:{type:"void","x-component":"Span","x-component-props":{style:"color: red"},"x-content":expression("$table.getRowByIndex($self.index).extIsMandatory === 'Y' ? '*' : null")},fileuploadId:{type:"number","x-component":"SrmCommonFile","x-component-props":{extraData:{fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},defaultFile:{fileId:expression("$table.getRowByIndex($self.index)?.fileuploadId"),fileName:expression("$table.getRowByIndex($self.index)?.authType")},readonly:expression("!($form.query('state').get('data').supplementAble || ($form.values.dataSources == 'MANUALLY_CREATE' && $form.values.ifSupplierCompleteInfo != 'Y'))"),"@on-change":expression(`({file}) => {
                    const { fileId = null, fileName = '' } = file || {}
                    let row = $table.getRowByIndex($self.index)
                    row.fileuploadId = fileId
                    row.authType = fileName
                  }`)}}}},authDescription:{type:"string",title:i18nExpression("cusEntry.vendorMod.remark"),"x-render-table-column":{minWidth:120}}})}}}};const _sfc_main$2={name:"Note",props:{title:{type:String,default:""},value:{type:String,default:""},readonly:{type:Boolean,default:!1}},data(){return{visible:!1,newValue:this.value}},watch:{value(newValue,oldValue){newValue!==oldValue&&(this.newValue=newValue)}},methods:{valueChange(value){this.$emit("change",value)}}};var _sfc_render$2=function(){var _vm=this,_c=_vm._self._c;return _c("div",{staticClass:"collapse-item-note"},[_c("span",[_vm._v(_vm._s(_vm.title))]),_c("el-popover",{attrs:{placement:"top",width:"400",trigger:"manual"},model:{value:_vm.visible,callback:function($$v){_vm.visible=$$v},expression:"visible"}},[_c("el-input",{attrs:{type:"textarea",maxlength:"100",disabled:_vm.readonly},on:{change:_vm.valueChange},model:{value:_vm.newValue,callback:function($$v){_vm.newValue=$$v},expression:"newValue"}}),_c("el-button",{attrs:{slot:"reference",type:"text"},slot:"reference"},[_vm._v(" "+_vm._s(_vm.$t("cusEntry.common.note"))+" ")])],1)],1)},_sfc_staticRenderFns$2=[],__component__$2=normalizeComponent(_sfc_main$2,_sfc_render$2,_sfc_staticRenderFns$2,!1,null,null,null,null);const Note=__component__$2.exports,_sfc_main$1=defineComponent({__name:"mainEngine",props:{formCompanyNature:{type:Object,default:()=>({})},type:{type:String,default:()=>""}},setup(__props){const props=__props,{app,emitTabRemove,t,vendor,http}=usePageHelper(),attrs=useAttrs(),workflowStatus=ref$1("DRAFT");let $disabled=!1;const newAddress=defineComponent({name:"newAddress",props:CAddress.props,setup(props2,{listeners,attrs:attrs2,slots}){return useAutoMountInstanceToField(),()=>h(CAddress,{props:{...attrs2,...props2},on:listeners,ref:"address"},slots)}}),query={"*":{},userInfo:{"*":{}},contactInfos:{"*":{}},orgCategorys:{"*":{}},orgInfos:{"*":{}},operationInfo:{"*":{}},supplierLeaderList:{"*":{}},companyAddressInfos:{"*":{}},cateJournalList:{"*":{},npmSerciceCustoms:{"*":{}}},qualificationInfo:{"*":{}},fileUploads:{"*":{}},npmCompanyExceptionInfos:{"*":{}}},$managementChange=(value,name,$form)=>{try{if(value){let data=$form.query(".managementAttaches").take().value;if(value=="Y"){let bold=1;data.forEach(e=>{e.documentInspection==name&&(bold=0)}),bold&&$form.query(".managementAttaches").take().invoke("addRow","unshift",{documentInspection:name,managementAttachId:null,managementInfoId:null,companyId:null,fileuploadId:null,authType:"",authDescription:"",authNum:"",authDate:null,authOrg:"",endDate:null})}else data.forEach((e,index)=>{e.documentInspection==name&&$form.query(".managementAttaches").take().invoke("remove",index)})}}catch{}},schema=defineSchemas({state:{type:"void","x-component":"Fragment","x-hidden":!0,"x-data":{dataFrom:"buyer",userInfoObj:{},companyId:app.$store.getters.userInfo.companyId||null,$disabled:!1,serciceCustomDelList:[],overseasRelation:"",status:"",deleAttr:[],type:"",supplementAble:!1,deleFileUploads:[]}},CompanyInfo:{type:"void","x-decorator":"QueryEngine","x-component":"el-container","x-component-props":{class:expression('$form.query("state").get("data").type !== "registered" ? "flex-container companyInfos" : "flex-container registered"'),direction:"vertical"},"x-query-engine":{service:"sup",actions:{query:{immediate:!0,loading:!0,ready:expression(`async () => {
            if (!app.$store.getters.userInfo.companyId) {
              const url = '/api-rbac/extUser/getByUserIdForVendor?id=' + app.$store.getters?.user?.userInfo.userId
              const res = await app.$http({
                url,
                method: 'GET',
                loading: true
              })
              $form.query('state').get('data').dataFrom = 'vendor'
              $form.query('state').get('data').userInfoObj = {
                username: res.data.username,
                phone: res.data.phone,
                email: res.data.email
              }
            }
            
            setTimeout(() => {
             $form.query('state').get('data').overseasRelation = $props.formCompanyNature.value?.overseasRelation
             $form.query('state').get('data').type = $props.type
             $form.query('.companyType').take().value = $props.formCompanyNature.value?.companyType
              if (!app.$store.getters.userInfo.companyId) {
                $form.query('.userInfo.username').take().value = $form.query('state').get('data').userInfoObj?.username
                $form.query('.userInfo.phone').take().value = $form.query('state').get('data').userInfoObj?.phone
                $form.query('.userInfo.email').take().value = $form.query('state').get('data').userInfoObj?.email
              }
            })
            $form.values.serviceRangeList = [{
              tableForm: {},
              list: [{}]
            }]
            $form.values.contactInfos = [{}]
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
              if (data.dataSources == 'MANUALLY_CREATE' && data.ifSupplierCompleteInfo != 'Y') {
                $form.query('state').get('data').$disabled = false
              } else {
                $form.query('state').get('data').$disabled = true
              }
              // 资质附件审批状态=拟定、驳回时显示补充资质附件信息按钮
              if ((!data.extIsQualifiedStatus || ['DRAFT'].includes(data.extIsQualifiedStatus)) && !!data.companyCode) {
                $form.query('state').get('data').supplementAble = true
              }
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
            
            // 附件要删除的列表
            let curFile = data.fileUploads || []
            $form.query('state').get('data').deleFileUploads = curFile.map(item => item.sceneFileId)
            if ($form.query('state').get('data').supplementAble || $form.values.extIsQualifiedFileUpload == 'Y' || $form.values.dataSources == 'MANUALLY_CREATE') {
              setTimeout(() => {
                $form.query('fileUploads').take(field => {
                  field.componentProps.componentInstance.reLoadFileInfo()
                })
              }, 5000)
            }
            data.userInfo.lcCode = data.lcCode
            data.userInfo.accountGroup = data.accountGroup
            data.nickname = data.userInfo.nickname
            $form.setValues(data)
            if (!data.qualificationInfo || !data.qualificationInfo.length) {
              $initQualificationInfo($form)
            }
            $form.values.ceeaBusinessModel = data.ceeaBusinessModel ? data.ceeaBusinessModel.split(',') : []
            return data
          }`)},vendorRead:{immediate:!0,loading:!0,method:"read",ready:expression(`() => {
            setTimeout(() => {
              $form.query('state').get('data').overseasRelation = $props.formCompanyNature.value?.overseasRelation
            })
            $form.values.serviceRangeList = [{
              tableForm: {},
              list: [{}]
            }]
            $form.values.contactInfos = [{}]
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
              if (data.dataSources == 'MANUALLY_CREATE' && data.ifSupplierCompleteInfo != 'Y') {
                $form.query('state').get('data').$disabled = false
              } else {
                $form.query('state').get('data').$disabled = true
              }
              // 资质附件审批状态=拟定、驳回时显示补充资质附件信息按钮
              if ((!data.extIsQualifiedStatus || ['DRAFT'].includes(data.extIsQualifiedStatus)) && !!data.companyCode) {
                $form.query('state').get('data').supplementAble = true
              }
            } else {
              $form.query('state').get('data').$disabled = false
            }
            const state = $form.query('state').get('data')
            state.status = status
            const mainHerder = $form.query('.mainHerder').take()
            if (status) {
              mainHerder.componentProps.status = status
              mainHerder.componentProps.flowRemark = data.flowRemark
              if (status == 'SUBMITTED') {
                mainHerder.componentProps.stepsActive = 4
              }
              if (status == 'APPROVED') {
                mainHerder.componentProps.stepsActive = 6
                if (!data.extIsQualifiedStatus || ['DRAFT'].includes(data.extIsQualifiedStatus)) {
                  if (!!data.extRejectQualificationReason) {
                    mainHerder.componentProps.status = 'REJECTED'
                    mainHerder.componentProps.fileStatus = 'REJECTED'
                    mainHerder.componentProps.flowRemark = data.extRejectQualificationReason
                  }
                } else if (['APPROVED'].includes(data.extIsQualifiedStatus)) {
                  mainHerder.componentProps.fileStatus = status
                } else {
                  mainHerder.componentProps.status = 'SUBMITTED'
                  mainHerder.componentProps.fileStatus = 'SUBMITTED'
                }
              }
            }
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
            data.serviceRangeList = serviceRange
            if (!$props.formCompanyNature.value?.overseasRelation) {
              $form.query('state').get('data').overseasRelation = data.overseasRelation
              const overseasRelation = state.overseasRelation
            }
            if ($props.formCompanyNature.value?.overseasRelation) {
              data.overseasRelation = $props.formCompanyNature.value?.overseasRelation
              // data.companyType = $props.formCompanyNature.value?.companyType
            }
            data.userInfo.lcCode = data.lcCode
            data.userInfo.accountGroup = data.accountGroup
            data.nickname = data.userInfo.nickname

            $form.setValues({
              ...data
            })
            // 附件要删除的列表
            let curFile = data.fileUploads || []
            $form.query('state').get('data').deleFileUploads = curFile.map(item => item.sceneFileId)
            if ($form.query('state').get('data').supplementAble || $form.values.extIsQualifiedFileUpload == 'Y' || $form.values.dataSources == 'MANUALLY_CREATE') {
              setTimeout(() => {
                $form.query('fileUploads').take(field => {
                  field.componentProps.componentInstance.reLoadFileInfo()
                })
              }, 5000)
            }
            $form.values.ceeaBusinessModel = data.ceeaBusinessModel ? data.ceeaBusinessModel.split(',') : []
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
                businessScope,
                companyCountry,
                companyProvince,
                companyCity,
                companyAddress,
                businessStartDate,
                businessEndDate,
                lcCode,
                enterpriseNo
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
                businessScope,
                companyCountry,
                companyProvince,
                companyCity,
                companyAddress,
                lcCode,
                enterpriseNo
              }
            }
            if (!data.qualificationInfo || !data.qualificationInfo.length) {
              $initQualificationInfo($form)
            }
          }`)},vendorSave:{method:"read",autoFormatResult:!1,cascadeDeletion:!0,loading:!0},vendorWithdraw:{autoFormatResult:!1,loading:!0},vendorSubmit:{autoFormatResult:!1,cascadeDeletion:!0,loading:!0}}},properties:{layout:{type:"void","x-component":"FormContainer",items:{type:"object",properties:{prevOne:{type:"void","x-content":i18nExpression("common.prevOne"),"x-component":"Button","x-visible":expression('["", "DRAFT", "WITHDRAW", "REJECTED"].includes($form.query("state").get("data").status)'),"x-component-props":{type:"default","@click":expression(`async (values) => {
                  let overseasRelation = null
                  overseasRelation = $form.query('state').get('data').overseasRelation
                  app.$emit('whatOverseasRelation', overseasRelation)
                }`)}},staging:{type:"void","x-content":i18nExpression("common.staging"),"x-component":"Button","x-visible":expression(`
              ['', 'DRAFT', 'WITHDRAW'].includes($form.query('state').get('data').status) ||
              !$form.query('state').get('data').$disabled
              `),"x-component-props":{type:"default","@click":expression(`async (values) => {
                  $saveBill('staging', $form, $queryEngine, $confirm, $message, $bus, $t)
                }`)}},submit:{type:"void","x-content":i18nExpression("common.submit"),"x-component":"Button","x-visible":expression(`
                ['', 'DRAFT', 'WITHDRAW', 'REJECTED'].includes($form.query('state').get('data').status) ||
                !$form.query('state').get('data').$disabled
                `),"x-component-props":{"@click":expression(`async (values) => {
                  $saveBill('submit', $form, $queryEngine, $confirm, $message, $bus, $t)
                }`)}},recall:{type:"void","x-content":i18nExpression("common.recall"),"x-component":"Button","x-visible":expression('["SUBMITTED"].includes($form.query("state").get("data").status)'),"x-component-props":{"@click":expression(`async (values) => {
                  $saveBill('recall', $form, $queryEngine, $confirm, $message, $bus, $t)
                }`)}},supplementQualifications:{type:"void","x-content":i18nExpression("cusEntry.vendorMod.supplementQualifications"),"x-component":"Button","x-visible":expression("$form.query('state').get('data').supplementAble && $form.values.dataSources != 'MANUALLY_CREATE'"),"x-component-props":{"@click":expression(`async (values) => {
                  let validate = true
                  await $form.validate().then().catch(eq => {
                    app.$message.error(eq[0].messages[0])
                    validate = false
                  })
                  if (!validate) {
                    return false
                  }
                  const { qualificationInfo, ceeaBusinessModel } = $form.values
                  let valid = qualificationInfo.findIndex(item => item.extIsMandatory == 'Y' && !item.fileuploadId)
                  if (valid > -1) {
                    // 资质信息第index行请上传附件！
                    $message.warning($t('cusEntry.vendorMod.qualificationInformation') + $t('bidMod.warningMessage', { index: valid + 1, message: $t('bidMod.pleaseUploadFile')}))
                    return
                  }

                  let bol = false
                  let bolMsg = ''
                  qualificationInfo.some((item, index) => {
                    if (item.extIfEndDateRequired === 'Y' && !item.endDate) {
                      bol = true
                      // 资质信息第index行 有效截止时间必填
                      bolMsg = $t('cusEntry.vendorMod.qualificationInformation') + $t('bidMod.warningMessage', { index: index + 1, message: $t('cusEntry.vendorMod.endTime') + $t('contract_mod.required')})
                      return true
                    }
                  })
                  if (bol) {
                    $message.warning(bolMsg)
                    return
                  }

                  $form.values.ceeaBusinessModel = ceeaBusinessModel?.length ? ceeaBusinessModel.join() : null
                  if ($form.query('state').get('data')?.deleFileUploads) {
                    $form.query('state').get('data').deleFileUploads.forEach(item => {
                      let obj = $form.values.fileUploads.find(e => e.sceneFileId == item)
                      if (!obj) {
                        $form.values.fileUploads.push({ $delete: item })
                      }
                    })
                  }
                  $queryEngine.request.save({ ...$form.values }, { query: query, action: 'vendorQualifiedSubmit' }).then((res) => {
                    $message.success($t('common.success'))
                    location.reload()
                  })
                }`)}}}},properties:{mainHerder:{type:"void","x-component":"MainHerder","x-component-props":{stepsActive:3,flowRemark:"",registered:expression('$form.query("state").get("data").type == "registered"')}},collapse:{type:"void","x-component":"Collapse","x-component-props":{defaultOpenPanelCount:1},properties:{...userInfoForm,...companyType,...companyInfo,...companyBaseInfo,...personBaseInfo,...contactInfoList,...serviceRange,...vendorSiteInfoList,...authInfo,...qualificationInformation,fileUploadsList:{type:"void","x-component":"CollapseItem","x-component-props":{title:`{{observer({
                    render(h) {
                      return h($$components.Note, {
                        props: {
                          title: t('vendorMod.sceneAttachmentInfo2'),
                          value: $form.values.extRejectAttribute11,
                          readonly: true
                        }
                      })
                    }
                  })}}`},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").supplementAble||$form.values.extIsQualifiedFileUpload=="Y"||$form.values.dataSources=="MANUALLY_CREATE"),"x-query-engine-skip":!0,properties:{fileUploads:{"x-query-engine-relation":"fileUploads:*",type:"array","x-component":"FileDynamic","x-component-props":{"scene-module-code":"SCENE_SUPPLIER_ATTACHMENT",businessId:expression('$form.query("state").get("data").companyId || null'),editable:expression("$form.query('state').get('data').supplementAble || ($form.values.dataSources == 'MANUALLY_CREATE' && $form.values.ifSupplierCompleteInfo != 'Y')"),"need-init":!1}}}}}}}},CFillProgress:{type:"void","x-component":"CFillProgress","x-component-props":{class:"contract-progress",ref:"contractProgress",nodeName:i18nExpression("logisticsMod.contractInfo"),data:expression("$nodeList($form)"),percentage:"{{true}}","@index-click":`{{ (code) => {
              let anchorEle = document.querySelector('#collapse_' + code)
              if (anchorEle) {
                anchorEle.scrollIntoView(true)
              }
          } }}`}}}}}),$saveBill=async(type,$form,$queryEngine,$confirm,$message,$bus,$t)=>{const overseasRelation=$form.values.overseasRelation||$form.query("state").get("data").overseasRelation;let{personBaseInfo:personBaseInfo2={},...values}=JSON.parse(JSON.stringify($form.values));if(personBaseInfo2.validityPeriodOfCard){const[businessStartDate,businessEndDate]=personBaseInfo2.validityPeriodOfCard;personBaseInfo2.businessStartDate=businessStartDate,personBaseInfo2.businessEndDate=businessEndDate}values.lcCode=values.userInfo.lcCode,values.accountGroup=values.userInfo.accountGroup,values.userInfo.nickname=values.nickname;const serciceCustomDelList=$form.query("state").get("data").serciceCustomDelList||[];let serviceRange2=$form.query("serviceRangeList").get("value");if(values.cateJournalList=serviceRange2?serviceRange2.map(item=>{const{list,tableForm}=item;return{...tableForm,npmSerciceCustoms:[...list,...serciceCustomDelList]}}):[],values.contactInfos.length>1){let num=0;if(values.contactInfos.forEach((e,index)=>{e.ceeaDefaultContact=="Y"&&num++}),num>1)return app.$message.error($t("dataConfMod.isDefaultMsg")),!1}if($form.query("state").get("data").supplementAble&&(values.qualificationInfo=$form.query("qualificationInfo").get("value"),values.fileUploads=$form.query("fileUploads").take().value),type=="submit"){let validate=!0;if(await $form.validate().then().catch(eq=>{app.$message.error(eq[0].messages[0]),validate=!1}),!validate)return!1;let overseasRelation2=$form.query("state").get("data").overseasRelation;if(overseasRelation2!=="PERSONAL"&&!$form.query(".businessLicenseFileId").take().value)return app.$message({message:$t("vendorMod.msgBusinessLicense"),type:"error"}),!1;let str="";if(!values.registCurrency&&overseasRelation2!=="PERSONAL"&&(str+=$t("vendorMod.msgCurrencyCode")+`
`),serviceRange2){const tableFormList=serviceRange2.map(item=>item.tableForm),categoryIdList=new Set(tableFormList.map(item=>item.categoryId));if(tableFormList.length!==categoryIdList.size){let nameRecords=[];for(let id of categoryIdList){const record=tableFormList.filter(item=>item.categoryId===id);record.length>1&&nameRecords.push(record[0].categoryName)}return $message.warning($t("cusEntry.tipMessage.serviceRangeCategoryRepeat",{name:nameRecords.join(";")})),!1}}if((validate||values.contactInfos.length===0)&&values.contactInfos.length===0&&(str+=$t("dashboard.addContactInformation")+`
`),str.length)return $message.error(str),!1;const{lcCode,idNumber}={...values,...personBaseInfo2},res=await integritySystem(lcCode||idNumber);if(res.data===integritySystemResultMap.get("forbid"))return $message.warning($t("cusEntry.tipMessage.blackForbid")),!1;if(res.data===integritySystemResultMap.get("focus")&&(values.focusFlag="Y",values.npmCompanyExceptionInfos.push({exceptionType:"FOCUS_FLAG"})),values.dataSources==="MANUALLY_CREATE"){let valid=values.qualificationInfo.findIndex(item=>item.extIsMandatory==="Y"&&!item.fileuploadId);if(valid>-1){$message.warning($t("cusEntry.vendorMod.qualificationInformation")+$t("bidMod.warningMessage",{index:valid+1,message:$t("bidMod.pleaseUploadFile")}));return}let bol=!1,bolMsg="";if(values.qualificationInfo.some((item,index)=>{if(item.extIfEndDateRequired==="Y"&&!item.endDate)return bol=!0,bolMsg=$t("cusEntry.vendorMod.qualificationInformation")+$t("bidMod.warningMessage",{index:index+1,message:$t("cusEntry.vendorMod.endTime")+$t("contract_mod.required")}),!0}),bol){$message.warning(bolMsg);return}}}values.supplierType==""&&(values.supplierType=null);const companyId=app.$store.getters.userInfo.companyId||null,status=$form.query(".status").take().value;values.ceeaBusinessModel=values.ceeaBusinessModel?.length?values.ceeaBusinessModel.join():null,type=="staging"?[null,void 0,"DRAFT"].includes(status)?(values.status="DRAFT",$queryEngine.request.save({...values,...personBaseInfo2,overseasRelation},{query,tree:!0,action:"vendorSave"}).then(res=>{if($message.success($t("common.successSave")),!app.$store.getters.userInfo.companyId){let userInfo=app.$store.getters.userInfo;userInfo.companyId=res[0].companyId,app.$store.commit("user/SET_USER_INFO",userInfo),app.$store.commit("user/SET_COMPANYID",res[0].companyId)}$queryEngine.request.baseRequest({action:"vendorRead"})}).catch(err=>{$form.values.ceeaBusinessModel=values.ceeaBusinessModel?values.ceeaBusinessModel.split(","):[]})):$queryEngine.request.save({...values,...personBaseInfo2,overseasRelation},{query,action:"vendorSave"}).then(res=>{if($message.success($t("common.successSave")),!app.$store.getters.userInfo.companyId){let userInfo=app.$store.getters.userInfo;userInfo.companyId=res[0].companyId,app.$store.commit("user/SET_USER_INFO",userInfo),app.$store.commit("user/SET_COMPANYID",res[0].companyId)}$queryEngine.request.baseRequest({action:"vendorRead"}).catch(err=>{$form.values.ceeaBusinessModel=values.ceeaBusinessModel?values.ceeaBusinessModel.split(","):[]})}):type=="recall"?app.$prompt("",$t("bidMod.withdrawReason"),{confirmButtonText:$t("common.confirm"),cancelButtonText:$t("components.common.cancel"),inputType:"textarea"}).then(({value})=>{let obj={companyId,flowRemark:value};$queryEngine.request.save(obj,{query,action:"vendorWithdraw"}).then(async res=>{app.$message({message:$t("dashboard.withdrawSuccess"),type:"success"}),$queryEngine.request.baseRequest({action:"vendorRead"})})}):(app.$store.getters.userInfo!="BUYER"&&(values.potentialFlag="Y"),$queryEngine.request.save({...values,...personBaseInfo2,overseasRelation},{query,tree:!0,action:"vendorSubmit"}).then(async res=>{if(!app.$store.getters.userInfo.companyId){let userInfo=app.$store.getters.userInfo;userInfo.companyId=res[0].companyId,app.$store.commit("user/SET_USER_INFO",userInfo),app.$store.commit("user/SET_COMPANYID",res[0].companyId)}$form.values.companyId=res[0].companyId;const{companyId:companyId2,companyName,companyCode}=$form.values;await $monitorIpAddress({supplierId:companyId2,supplierCode:companyCode,supplierName:companyName,source:source.get("registerSubmit")}),app.$emit("saveAll")}).catch(e=>{$form.values.ceeaBusinessModel=values.ceeaBusinessModel?values.ceeaBusinessModel.split(","):[]}))},$nodeList=$form=>{let userType=$form.query("state").get("data").overseasRelation,nodeList1=[{code:"qualificationInformation",name:t("cusEntry.vendorMod.qualificationInformation"),percentage:0},{code:"fileUploadsList",name:t("vendorMod.sceneAttachmentInfo2"),percentage:0}],company=[{code:"userInfoForm",name:t("vendorMod.vendorUserInfo"),percentage:0},{code:"companyTypeAll",name:t("vendorMod.companyType"),percentage:0},{code:"companyInfo",name:t("vendorMod.enterpriseThreeCertificates"),percentage:0},{code:"companyBaseInfo",name:t("vendorMod.companyBaseInfo"),percentage:0},{code:"contactInfoList",name:t("vendorMod.contactInfo"),percentage:0},{code:"serviceRange",name:t("cusEntry.vendorMod.serviceRange"),percentage:0},{code:"vendorSiteInfo",name:t("vendorMod.vendorSiteInfos2"),percentage:0},{code:"authInfo",name:t("cusEntry.vendorMod.authInfo"),percentage:0}],person=[{code:"userInfoForm",name:t("vendorMod.vendorUserInfo"),percentage:0},{code:"person",name:t("cusEntry.vendorMod.baseInfo"),percentage:0},{code:"contactInfoList",name:t("vendorMod.contactInfo"),percentage:0},{code:"serviceRange",name:t("cusEntry.vendorMod.serviceRange"),percentage:0},{code:"vendorSiteInfo",name:t("vendorMod.vendorSiteInfos2"),percentage:0}];return($form.query("state").get("data").supplementAble||$form.values.extIsQualifiedFileUpload=="Y"||$form.values.dataSources=="MANUALLY_CREATE")&&(company=company.concat(nodeList1),person=person.concat(nodeList1)),userType==="PERSONAL"?person:company},integritySystem=lcCode=>http({url:"/api-sup/pj/companyInfo/queryIfBlackCompany",method:"POST",data:{lcCode}}),$monitorIpAddress=data=>http({url:"/api-sou/bids/ip/address/ipAddress/save",method:"POST",data}),integritySystemResultMap=new Map([["forbid","禁止合作"],["focus","重点关注"]]),source=new Map([["registerSubmit","注册提交"],["registerUndo","注册撤回"]]),$showSunFile=$self=>{const fileList=[];if($self.value){const fileIdList=$self.value?.split(","),fileNameList=$self.query(".sunshineFileName").get("value")?.split(",");fileIdList.forEach((item,index)=>{fileList.push({fileId:item,fileName:fileNameList?.[index]})})}$self.setComponentProps({fileList})},$initQualificationInfo=async $form=>{let cur=$form.query("state").get("data").overseasRelation,dictCode=`CERTIFICATE_TYPE_${cur}`,res=DictClass.getDict(dictCode);cur&&!res&&(res=(await http({url:"/api-base/dict/base-dict-item/listByDictCode",method:"POST",data:[dictCode]})).data||[]),$form.values.qualificationInfo=res?res.map(item=>({authNum:item.dictItemCode,extIsMandatory:item.itemDescription||"N"})):[]},scope={app,http,t,$props:props,$attrs:attrs,performPlanService,$disabled,emitTabRemove,$saveBill,DictSelect,observer,$managementChange,query,validEmail,validatePhone,$nodeList,sceneFileApi,integritySystem,integritySystemResultMap,$showSunFile,source,$monitorIpAddress,$initQualificationInfo,$taxDictClass:createDictClass({country:[]})};return{__sfc:!0,props,app,emitTabRemove,t,vendor,http,attrs,workflowStatus,$disabled,newAddress,query,$managementChange,schema,$saveBill,$nodeList,integritySystem,$monitorIpAddress,integritySystemResultMap,source,$showSunFile,$initQualificationInfo,scope,components:{SrmCommonFile:CommonFile,CAddress,CCategorySelect,FileDynamic,CFillProgress,newAddress,MainHerder,Note},RenderEngine}}});var _sfc_render$1=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{schemaKey:"companyInfoMain",pageAttrs:_vm.$attrs,schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns$1=[],__component__$1=normalizeComponent(_sfc_main$1,_sfc_render$1,_sfc_staticRenderFns$1,!1,null,null,null,null);const mainEngine=__component__$1.exports,_sfc_main=defineComponent({__name:"index",props:{type:{type:String,default:()=>""}},setup(__props){const props=__props,{app,emitTabRemove,t,vendor}=usePageHelper(),schema=defineSchemas({state:{type:"void","x-component":"Fragment","x-hidden":!0,"x-data":{companyId:app.$store.getters.userInfo.companyId||null,status:null,wheres:"",type:"",userType:"",flowRemark:""}},CompanyInfo:{type:"void","x-decorator":"QueryEngine","x-query-engine":{service:"sup",actions:{query:{immediate:!0,ready:expression(`() => {
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
