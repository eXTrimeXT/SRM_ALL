import{n as normalizeComponent,ae as expression,af as i18nExpression,ag as yearMonthDaySelectorSegment,ah as radioGroupByYOrNSegment,ai as generateXindexInOrder,aj as editTableFormItemValid,ak as feedbackLayoutIsPopover,al as defineComponent,am as usePageHelper,an as useAttrs,ao as ref$1,ap as useAutoMountInstanceToField,aq as h,ar as defineSchemas,as as RenderEngine,at as performPlanService,au as DictSelect,av as observer,v as validEmail,a as validatePhone,aw as CommonFile,a6 as CCategorySelect}from"./index-17d0ccd5.js";import{C as CAddress}from"./index-38ab0095.js";import{F as FileDynamic}from"./file-dynamic-30cdd411.js";import{C as CFillProgress}from"./index-6af40985.js";import{r as resolve}from"./path-4ced4e54.js";import{t as transformMQL}from"./util-a92f9f8e.js";import{v as vendorOptCommonApi}from"./index-b8c9566a.js";/* empty css                                                              */import"./file-dynamic-ab2ff377.js";import"./MainHeader-ef959a5c.js";/* empty css                                                                   */import"./file-dynamic.vue_vue_type_style_index_0_scoped_516fdfc3_lang-e128b539.js";import"./tree-utils-7df6be59.js";import"./basicSetting-f3b18103.js";import"./BaseTableBind-53264a4f.js";import"./util-6482eb24.js";import"./index-4d512f00.js";/* empty css                                                                      */import"./fileApi-3f0be128.js";/* empty css                                              */const _sfc_main$5={components:{},data(){return{}},computed:{},async created(){},mounted(){},updated(){},methods:{goTo(where){this.$emit("goToWhere",where)}}};var _sfc_render$5=function(){var _vm=this,_c=_vm._self._c;return _c("el-container",[_c("el-main",{staticClass:"main-stepOne"},[_c("section",{staticClass:"boxs"},[_c("i",{staticClass:"el-icon-warning icon-red icon-big margin"}),_c("span",{staticClass:"margin mainFont"},[_vm._v(_vm._s(_vm.$t("vendorMod.companyInformationNotCertified")))]),_c("span",{staticClass:"margin greyFont"},[_vm._v(_vm._s(_vm.$t("vendorMod.sorryCompanyInformationNotCertified")))]),_c("button",{staticClass:"button-click margin",on:{click:function($event){return _vm.goTo("company-nature")}}},[_vm._v(" "+_vm._s(_vm.$t("vendorMod.continueFilling"))+" ")])])])],1)},_sfc_staticRenderFns$5=[],__component__$5=normalizeComponent(_sfc_main$5,_sfc_render$5,_sfc_staticRenderFns$5,!1,null,"a3ddec01",null,null);const stepOne=__component__$5.exports;const _sfc_main$4={components:{},props:{status:{type:String,default:"APPROVED"}},data(){return{}},computed:{},async created(){},mounted(){},updated(){},methods:{recall(){this.$prompt("","撤回原因",{confirmButtonText:"确定",cancelButtonText:"取消",inputType:"textarea"}).then(({value})=>{let saveData=transformMQL.save("CompanyInfo",[{companyId:this.$store.getters.userInfo.companyId,flowRemark:value}],"vendorWithdraw");vendorOptCommonApi.withdrawCompanyMQL(saveData).then(()=>{this.$message({message:"成功撤回",type:"success"}),this.backToMain()})})},backToSystem(){let saveData=transformMQL.save("CompanyInfo",[{companyId:this.$store.getters.userInfo.companyId}],"updateFirstLog");vendorOptCommonApi.saveCompanyMQL(saveData).then(()=>{this.$store.dispatch("user/initSystem").then(()=>{this.$router.push({path:resolve("/dashboard")})})})},backToMain(){this.$emit("goToWhere","main")}}};var _sfc_render$4=function(){var _vm=this,_c=_vm._self._c;return _c("el-container",{staticClass:"successContainer"},[_vm.status=="SUBMITTED"?_c("el-main",{staticClass:"main-stepOne"},[_c("section",{staticClass:"boxs"},[_c("span",{staticClass:"success-icon margin"},[_c("i",{staticClass:"el-icon-success"})]),_c("span",{staticClass:"margin mainFont"},[_vm._v(_vm._s(_vm.$t("提交成功")))]),_c("span",{staticClass:"margin greyFont"},[_vm._v(_vm._s(_vm.$t("企业信息已提交成功请等待审批")))]),_c("section",{staticClass:"flex"},[_c("el-button",{staticClass:"margin button-click",on:{click:_vm.recall}},[_vm._v(" "+_vm._s(_vm.$t("撤回提交信息"))+" ")]),_c("el-button",{staticClass:"margin button-click",attrs:{type:"primary"},on:{click:_vm.backToMain}},[_vm._v(" "+_vm._s(_vm.$t("查看企业信息"))+" ")])],1)])]):_vm._e(),_vm.status=="APPROVED"?_c("el-main",{staticClass:"main-stepOne"},[_c("section",{staticClass:"boxs"},[_c("span",{staticClass:"success-icon margin"},[_c("i",{staticClass:"el-icon-success"})]),_c("span",{staticClass:"margin mainFont"},[_vm._v(_vm._s(_vm.$t("common.successRegister")))]),_c("span",{staticClass:"margin greyFont"},[_vm._v(_vm._s(_vm.$t("vendorMod.companyInformationSuccess")))]),_c("el-button",{staticClass:"margin button-click",attrs:{type:"primary"},on:{click:_vm.backToSystem}},[_vm._v(" "+_vm._s(_vm.$t("vendorMod.enterSystem"))+" ")])],1)]):_vm._e(),_vm.status=="REJECTED"?_c("el-main",{staticClass:"main-stepOne"},[_c("section",{staticClass:"boxs"},[_c("i",{staticClass:"el-icon-error icon-grey icon-big margin"}),_c("span",{staticClass:"margin mainFont"},[_vm._v(_vm._s(_vm.$t("common.failedRegister")))]),_c("span",{staticClass:"margin greyFont"},[_vm._v(_vm._s(_vm.$t("vendorMod.companyInformationFailed")))]),_c("el-button",{staticClass:"margin button-click",attrs:{type:"primary"},on:{click:_vm.backToMain}},[_vm._v(" "+_vm._s(_vm.$t("vendorMod.backCompanyInformation"))+" ")])],1)]):_vm._e()],1)},_sfc_staticRenderFns$4=[],__component__$4=normalizeComponent(_sfc_main$4,_sfc_render$4,_sfc_staticRenderFns$4,!1,null,"427667e0",null,null);const success=__component__$4.exports,companyNatureEngine={steps:{type:"void","x-decorator":"div","x-decorator-props":{class:"comInfosteps"},"x-component":"Steps","x-component-props":{alignCenter:!0,finishStatus:"success"},"x-reactions":{dependencies:["status"],fulfill:{state:{"component[1].active":2}}},"x-visible":!1,properties:{step1:{type:"void","x-component":"el-step","x-component-props":{title:expression("$t('vendorMod.registrationPolicy')")}},step2:{type:"void","x-component":"el-step","x-component-props":{title:expression("$t('vendorMod.registerAccount')")}},step3:{type:"void","x-component":"el-step","x-component-props":{title:expression("$t('vendorMod.registrationType')")}},step4:{type:"void","x-component":"el-step","x-component-props":{title:expression("$t('vendorMod.authenticationInformation')")}},step5:{type:"void","x-component":"el-step","x-component-props":{title:expression("$t('vendorMod.pendingApproval')")}},step6:{type:"void","x-component":"el-step","x-component-props":{title:expression("$t('vendorMod.informationAuthentication')")}}}},formCompanyNature:{type:"object","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical",class:"boxs-row"},"x-component":"FormGrid","x-component-props":{maxColumns:1,columnGap:32,rowGap:0},"x-query-engine-skip":!0,properties:{overseasRelation:{type:"string",default:"INSIDE","x-decorator":"FormItem","x-component":"natureChose","x-component-props":{style:"margin:18px 0 15px 0;","@change":expression(`(who) => {
            $self.value = who
          }`)},title:expression("$t('vendorMod.overseasRelation')"),"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},companyType:{type:"string","x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"COMPANY_NATURE",class:"companyType"},"x-reactions":expression(`field => {
            const overseasRelation = $self.query('.overseasRelation').take().value
            if (overseasRelation == 'INSIDE') {
              field.visible = true
            } else {
              field.visible = false
            }
        }`),title:expression("$t('vendorMod.companyType')"),"x-validator":{required:!0,message:i18nExpression("common.requiredField")}}}}},companyType={companyTypeAll:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.companyType")},"x-visible":!0,"x-query-engine-skip":!0,properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:{flowRemark:{type:"string","x-decorator":"FormItem","x-hidden":!0},status:{type:"string",default:"DRAFT","x-hidden":!0},overseasRelation:{type:"string","x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{disabled:!0,code:"RELATION"},title:expression("$t('vendorMod.overseasRelation')"),"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},companyType:{type:"string","x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{disabled:!0,code:"COMPANY_NATURE","@change":expression(`() => {

              }`)},"x-reactions":{dependencies:["overseasRelation"],fulfill:{state:{visible:expression('$deps[0] == "INSIDE"')}}},title:expression("$t('vendorMod.companyType')"),"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},supplierType:{type:"string",default:null,"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),code:"SUPPLIER_TYPE","@change-value":expression(`(val) => {
                  if (val == '') {
                    $form.query('.supplierType').take().value = null
                  }
              }`)},title:expression("$t('supplierRating.supplierType')"),"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},dunsCode:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.dunsCode')"),"x-reactions":{dependencies:[".overseasRelation"],fulfill:{state:{visible:expression('$deps[0] == "OUT"')}}},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}}}}}}},companyInfo={companyInfo:{type:"void","x-component":"CollapseItem","x-visible":!0,"x-component-props":{title:i18nExpression("vendorMod.enterpriseThreeCertificates")},"x-query-engine-skip":!0,properties:{div:{type:"void","x-component":"div","x-component-props":{class:"companyInfo"},properties:{businessLicense:{type:"string","x-hidden":!0},businessLicenseFileId:{type:"string","x-component":"SrmCommonFile","x-component-props":{readonly:expression("$form.query('state').get('data').$disabled"),"list-type":"picture-card",style:{width:"33%","padding-right":"25px"},defaultFile:{fileId:expression("$self.value"),fileName:expression("$form.query('businessLicense').get('value')")},"dragger-options":{width:"100%",height:"345px"},limit:1,drag:"drag","@on-change":expression(`({ file }) => {
                  console.log(file, 'file')
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
                      url: '/api-base/ocr/recognizeLcImage',
                      method: 'GET',
                      params: { fileuploadId: fileId },
                      loading: true
                    })
                      .then(res => {
                        let licenseData = res.data
                        $form.query('.companyName').take().value = licenseData.companyName
                        $form.query('.legalPerson').take().value = licenseData.legalPerson
                        $form.query('.lcCode').take().value = licenseData.lcCode
                        $form.query('.registeredCapital').take().value = licenseData.registeredCapital
                        $form.query('.registCurrency').take().value = licenseData.registCurrency
                        $form.query('.companyAddress').take().value = licenseData.companyAddress
                        $form.query('.businessScope').take().value = licenseData.businessScope
                        $form.query('.registrationAuthority').take().value = licenseData.registrationAuthority
                        $form.query('.companyCreationDate').take().value = licenseData.companyCreationDate
                        // $form.query('.businessDate').take().value =[licenseData.businessStartDate, licenseData.businessEndDate]
                      })
                      .catch(err => {
                        console.log(err)
                      })
                  }
                  $form.query('.businessLicenseFileId').take().value = fileId.toString()
                  $form.query('.businessLicense').take().value = fileName
          }`)},title:""},layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical",style:{width:"67%","padding-left":"20px"}},"x-component":"FormGrid","x-component-props":{maxColumns:2,columnGap:32,rowGap:0},properties:{companyName:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.companyName')"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},"x-validator":{required:!0,message:i18nExpression("vendorMod.msgCompanyName")}},legalPerson:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.legalPerson')"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},"x-validator":{required:!0,message:i18nExpression("vendorMod.msgLegalPerson")}},lcCode:{type:"string","x-visible":expression("$form.query('.overseasRelation').take().value == 'INSIDE'"),"x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.lcCode')"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},"x-validator":{required:expression("$form.query('.overseasRelation').take().value == 'INSIDE'"),message:i18nExpression("vendorMod.msgLcCode")}},businessLicenseNo:{type:"string","x-visible":expression("$form.query('.overseasRelation').take().value == 'OUT'"),"x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.lcCode2')"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},registCurrency:{type:"string","x-hidden":!0},registeredCapital:{type:"number","x-visible":expression("$form.query('.companyType').take().value != 'GETI'"),"x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.registeredCapital')"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),class:"input-with-select"},"x-content":{append:expression(`observer(
                {
                render(h) {
                  const targetField = $self.query('.registCurrency').take()
                  return h(DictSelect, {
                    props: {
                      value: targetField.value,
                      code: 'currency'
                    }, on: {
                      'change-value': (value) => {
                        targetField.value = value
                      }
                    }
                  })
                }
              }
              )
            `)},"x-validator":{required:expression("!['GETI','FEIYINGLI'].includes($form.query('.companyType').take().value)"),message:i18nExpression("vendorMod.msgRegisteredCapital")}},companyCreationDate:{type:"date",default:null,"x-decorator":"FormItem","x-component-props":{placeholder:i18nExpression("common.pleaseSelectDate"),format:"yyyy-MM-dd","value-format":"yyyy-MM-dd",disabled:expression("$form.query('state').get('data').$disabled")},title:expression("$t('vendorMod.creationDate')"),"x-validator":{required:expression("!['FEIYINGLI'].includes($form.query('.companyType').take().value)"),message:i18nExpression("vendorMod.msgCreationDate")}},businessStartDate:{type:"date","x-hidden":!0},businessEndDate:{type:"date","x-hidden":!0},businessDate:{type:"string","x-component":"DatePicker","x-decorator":"FormItem","x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],type:"daterange",disabled:expression("$form.query('state').get('data').$disabled")},title:expression("$t('vendorMod.dateBusiness')"),"x-validator":{required:expression("!['FEIYINGLI'].includes($form.query('.companyType').take().value)"),message:i18nExpression("vendorMod.msgCreationDate")}},companyShortName:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.companyShortName')"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},registrationAuthority:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.registrationAuthority')"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},businessScope:{type:"string","x-decorator":"FormItem","x-component":"Input","x-decorator-props":{gridSpan:2},title:expression("$t('vendorMod.businessScope')"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),type:"textarea"}}}}}}}}},companyBaseInfo={companyBaseInfo:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.companyBaseInfo2")},"x-query-engine-skip":!0,properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:{ceeaAgentBrand:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.agencyBrand')"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},"x-validator":{required:!1,message:i18nExpression("vendorMod.msgAgencyBrand")}},categoryName:{type:"string","x-hidden":!0},cateJournalList:{type:"Array","x-hidden":!0},cateJournalListAll:{type:"Array","x-decorator":"FormItem","x-component":"CCategorySelect",title:expression("$t('vendorMod.mainCategory')"),"x-query-engine-skip":!0,"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),"select-type":"input",class:"categoryName",multiple:!0,"selected-lines":expression("$form.query('.cateJournalList').take().value"),placeholder:expression("$form.query('.categoryName').take().value"),"@select":expression(`(val) => {
                 if (val) {
                  console.log(val)
                  if (val.length > 3) {
                    app.$message.warning($t('vendorMod.msgMost3LittleCate')) // 请选择最多三个小类
                    return
                  }
                  let cateJournalList = $form.query('.cateJournalList').take().value
                  let deleAttr = []
                  if (cateJournalList) {
                    cateJournalList.forEach( (e) => {
                      if (e.categoryJournalId) {
                        deleAttr.push({$delete:e.categoryJournalId})
                      }
                    } )
                  }
                  $form.query('state').get('data').deleAttr.push(deleAttr)
                  console.log($form.query('state').get('data').deleAttr , 'deleAttr')
                  if (val.length > 0) {
                    let attr = []
                    for (let item of val) {
                      if (item.categoryCode) {
                        attr.push({
                          categoryId: item.categoryId,
                          categoryCode: item.categoryCode,
                          categoryName: item.categoryName
                        })
                      }
                    }
                    const categoryName = val.map(v => v.categoryName).join(',')
                    $form.query('.categoryName').take().value = categoryName
                    $form.query('.cateJournalList').take().value = attr
                  } else {
                    if (Array.isArray(val)) {
                      $form.query('.categoryName').take().value = null
                    } else {
                      $form.query('.cateJournalList').take().value = [
                        {
                          categoryId: val.categoryId,
                          categoryCode: val.categoryCode,
                          categoryName: val.categoryName
                        }
                      ]
                      $form.query('.categoryName').take().value = val.categoryName
                    }
                  }
                }
              }`)},"x-validator":{required:!1,message:i18nExpression("vendorMod.msgAgencyBrand")}},ceeaIfListed:{title:i18nExpression("vendorMod.ifListed"),...radioGroupByYOrNSegment,"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},ceeaListedTime:{type:"date",default:null,"x-visible":"{{$form.query('.ceeaIfListed').take().value == 'Y'}}","x-decorator":"FormItem","x-component-props":{placeholder:i18nExpression("common.pleaseSelectDate"),format:"yyyy-MM-dd","value-format":"yyyy-MM-dd",disabled:expression("$form.query('state').get('data').$disabled")},title:expression("$t('vendorMod.listedDate')"),"x-validator":{required:!0,message:i18nExpression("请选择上市时间")}},listedExchange:{type:"string","x-visible":"{{$form.query('.ceeaIfListed').take().value == 'Y'}}","x-decorator":"FormItem","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},title:expression("$t('vendorMod.listedExchange')"),"x-validator":{required:!0,message:i18nExpression("请选择上市交易所")}},ceeaBusinessModel:{type:"string","x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),code:"BIZ_MODEL"},title:expression("$t('vendorMod.bizModel')")},companyCountry:{type:"string",title:i18nExpression("components.address.country"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"country",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$form.query('state').get('data').$disabled"),"@change":expression(`(val) => {
                let row = $form.values
                // 选择国外就清理省市区，并且禁用
                if (row.companyCountry !== 'CN') {
                  row.companyProvince = null
                  row.companyCity = null
                }
              }`)}},companyProvince:{type:"string",title:i18nExpression("components.address.area"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"PROVINCE","custom-select-type":"PROVINCE",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$form.query('state').get('data').$disabled || $form.values.companyCountry!='CN'")}},companyCity:{type:"string",title:i18nExpression("components.address.city"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:expression("$form.values.companyProvince"),"custom-select-type":"CITY",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$form.query('state').get('data').$disabled || $form.values.companyCountry!='CN'")}},companyAddress:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('components.address.detailAddress2')"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},"x-validator":{required:!0,message:i18nExpression("vendorMod.msgDetailAddr")}},ceeaHasParentCompany:{title:i18nExpression("vendorMod.ifParentCompany"),...radioGroupByYOrNSegment,"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},ceeaParentCompanyName:{type:"string","x-decorator":"FormItem","x-component":"Input","x-visible":"{{$form.query('.ceeaHasParentCompany').take().value == 'Y'}}",title:expression("$t('vendorMod.parentCompanyName')"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},"x-validator":{required:!0,message:i18nExpression("请输入母公司名称")}},ceeaParentCompanyLcCode:{type:"string","x-decorator":"FormItem","x-component":"Input","x-visible":"{{$form.query('.ceeaHasParentCompany').take().value == 'Y'}}",title:expression("$t('vendorMod.parentCompanyLcCode')"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},"x-validator":{required:!0,message:i18nExpression("请输入母公司统一信用代码")}},ceeaCompanyIntro:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.companyProfile')"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},"x-decorator-props":{gridSpan:3},"x-validator":{required:!0,message:i18nExpression("vendorMod.msgCompanyProfile")}}}}}}},contactInfoList={contactInfoList:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.contactInfo")},"x-query-engine-skip":!0,properties:{toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void",title:i18nExpression("common.new"),"x-component-props":{type:"primary",disabled:expression("$form.query('state').get('data').$disabled"),"@click":expression(`() => {
                 $self.query('.contactInfos')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
              }`)}}}},contactInfos:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,maxHeight:400,pagination:!1,sortable:!1,primaryKey:"contactInfoId",cascadeDeletion:!0},"x-query-engine-skip":!0,properties:generateXindexInOrder({contactName:{type:"string",title:i18nExpression("vendorMod.nickname"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},...editTableFormItemValid},ceeaGender:{type:"string",title:i18nExpression("vendorMod.sex"),"x-render-table-column":{minWidth:100},"x-component":"DictSelect","x-component-props":{code:"GENDER",disabled:expression("$form.query('state').get('data').$disabled")}},ceeaDeptName:{type:"string",title:i18nExpression("vendorMod.department"),"x-render-table-column":{minWidth:100},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},position:{type:"string",title:i18nExpression("dataConfMod.position"),"x-render-table-column":{minWidth:100},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},ceeaContactMethod:{type:"string",title:i18nExpression("vendorMod.contactMethod"),"x-render-table-column":{minWidth:100},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},"x-validator":{message:i18nExpression("请输入格式正确的电话号码"),validator:expression(`(value, rule) => {
                if(value && !validatePhone(value)) {
                  return '请输入格式正确的电话号码'
                }
              }`)},...feedbackLayoutIsPopover},email:{type:"string",title:i18nExpression("vendorMod.email"),"x-render-table-column":{minWidth:100},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},"x-validator":{message:i18nExpression("请输入格式正确的邮箱地址"),validator:expression(`(value, rule) => {
                if(value && !validEmail(value)){
                  return '请输入格式正确的邮箱地址'
                }
              }`)},...feedbackLayoutIsPopover},ceeaDefaultContact:{type:"string",title:i18nExpression("dataConfMod.isDefault"),"x-render-table-column":{minWidth:100},"x-component":"Checkbox","x-component-props":{"true-label":"Y","false-label":"N",disabled:expression("$form.query('state').get('data').$disabled")}},ceeaComments:{type:"string",title:i18nExpression("dataConfMod.remark"),"x-render-table-column":{minWidth:100},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),type:"text","@click":expression(`({ row }) => {
                      $table.remove($self.index)
                  }`)}}}}})}}}},bankInfoList={bankInfoList:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.bankInfo")},"x-query-engine-skip":!0,properties:{toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void",title:i18nExpression("common.new"),"x-component-props":{type:"primary",disabled:expression("$form.query('state').get('data').$disabled"),"@click":expression(`() => {
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
              `)},...editTableFormItemValid},bankName:{type:"string",title:i18nExpression("components.bank.bankName"),"x-render-table-column":{minWidth:120},"x-read-pretty":!0,...editTableFormItemValid},unionCode:{type:"string",title:i18nExpression("components.bank.unionCode"),"x-render-table-column":{minWidth:120},"x-read-pretty":!0,...editTableFormItemValid},openingBank:{type:"string",title:i18nExpression("components.bank.branchBankName"),"x-render-table-column":{minWidth:120},"x-read-pretty":!0,...editTableFormItemValid},bankAccountName:{type:"string",title:i18nExpression("components.bank.accountName"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},...editTableFormItemValid},bankAccount:{type:"string",title:i18nExpression("components.bank.bankAccount"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},...editTableFormItemValid},currencyCode:{type:"string",title:i18nExpression("vendorMod.currencyCode"),"x-render-table-column":{minWidth:120},"x-component":"DictSelect","x-component-props":{code:"currency",disabled:expression("$form.query('state').get('data').$disabled")},...editTableFormItemValid},ceeaMainAccount:{type:"string",title:i18nExpression("components.bank.isMain"),"x-render-table-column":{minWidth:100},"x-component":"Checkbox","x-component-props":{"true-label":"Y","false-label":"N",disabled:expression("$form.query('state').get('data').$disabled")},...editTableFormItemValid},ceeaEnabled:{type:"string",title:i18nExpression("components.bank.isActive"),"x-render-table-column":{minWidth:100},"x-component":"Checkbox","x-component-props":{"true-label":"Y","false-label":"N",disabled:expression("$form.query('state').get('data').$disabled")}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),type:"text","@click":expression(`({ row }) => {
                      $table.remove($self.index)
                  }`)}}}}})}}}},factoryInfoList={factoryInfo:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.factoryInfo")},"x-query-engine-skip":!0,properties:{toolbar:{type:"void","x-component":"Space","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void",title:i18nExpression("common.new"),"x-component":"RButton","x-component-props":{type:"primary",disabled:expression("$form.query('state').get('data').$disabled"),"@click":expression(`() => {
                 $self.query('plantInfos')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
              }`)}}}},plantInfos:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,maxHeight:400,pagination:!1,sortable:!1,primaryKey:"plantId",cascadeDeletion:!0},"x-query-engine-skip":!0,properties:generateXindexInOrder({plantName:{type:"string",title:i18nExpression("vendorMod.factoryInfo"),"x-render-table-column":{minWidth:150},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},...editTableFormItemValid},plantNature:{type:"string",title:i18nExpression("vendorMod.factoryType"),"x-render-table-column":{minWidth:150},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},...editTableFormItemValid},plantArea:{type:"string",title:i18nExpression("vendorMod.factoryArea"),"x-render-table-column":{minWidth:150},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},...editTableFormItemValid},plantCountry:{type:"string",title:i18nExpression("components.address.country"),"x-render-table-column":{minWidth:120},"x-component":"DictSelect","x-component-props":{code:"country",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$form.query('state').get('data').$disabled"),"@change":expression(`(val) => {
                let row = $table.getRowByIndex($self.index)
                // 选择国外就清理省市区，并且禁用
                if (row.plantCountry !== 'CN') {
                  row.plantProvince = null
                  row.plantCity = null
                }
              }`)},...editTableFormItemValid},plantProvince:{type:"string",title:i18nExpression("components.address.area"),"x-render-table-column":{minWidth:120},"x-component":"DictSelect","x-component-props":{code:"PROVINCE","custom-select-type":"PROVINCE",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$form.query('state').get('data').$disabled || $table.getRowByIndex($self.index).plantCountry!='CN'")}},plantCity:{type:"string",title:i18nExpression("components.address.city"),"x-render-table-column":{minWidth:120},"x-component":"DictSelect","x-component-props":{code:expression("$table.getRowByIndex($self.index).plantProvince"),"custom-select-type":"CITY",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$form.query('state').get('data').$disabled || $table.getRowByIndex($self.index).plantCountry!='CN'")}},plantAddress:{type:"string",title:i18nExpression("vendorMod.factoryAddress"),"x-render-table-column":{minWidth:150},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},...editTableFormItemValid},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),type:"text","@click":expression(`({ row }) => {
                      $table.remove($self.index)
                  }`)}}}}})}}}},operatingPerformancesList={operatingPerformancesList:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.operatingResult2")},"x-query-engine-skip":!0,properties:{toolbar:{type:"void","x-component":"Space","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void",title:i18nExpression("common.new"),"x-component":"RButton","x-component-props":{type:"primary",disabled:expression("$form.query('state').get('data').$disabled"),"@click":expression(`() => {
                 $self.query('operatingPerformances')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
              }`)}}}},operatingPerformances:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,maxHeight:400,pagination:!1,sortable:!1,primaryKey:"id",cascadeDeletion:!0},"x-query-engine-skip":!0,properties:generateXindexInOrder({year:{type:"string",title:i18nExpression("dataConfMod.year"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},totalSales:{type:"string",title:i18nExpression("vendorMod.totalSales"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},totalProfit:{type:"string",title:i18nExpression("vendorMod.totalProfit"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},profitRate:{type:"string",title:i18nExpression("vendorMod.profitRate"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),type:"text","@click":expression(`({ row }) => {
                      $table.remove($self.index)
                  }`)}}}}})}}}},overallStrengths={overallStrengthList:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.overallStrength")},"x-query-engine-skip":!0,properties:{toolbar:{type:"void","x-component":"Space","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void",title:i18nExpression("common.new"),"x-component":"RButton","x-component-props":{type:"primary",disabled:expression("$form.query('state').get('data').$disabled"),"@click":expression(`() => {
                 $self.query('overallStrengths')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
              }`)}}}},overallStrengths:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,maxHeight:400,pagination:!1,sortable:!1,primaryKey:"id",cascadeDeletion:!0},"x-query-engine-skip":!0,properties:generateXindexInOrder({industryType:{type:"string",title:i18nExpression("vendorMod.industryType"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},year:{type:"string",title:i18nExpression("dataConfMod.year"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},industryRanking:{type:"string",title:i18nExpression("vendorMod.industryRank"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},marketShare:{type:"string",title:i18nExpression("vendorMod.marketShare"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},topThreeEnterprises:{type:"string",title:i18nExpression("vendorMod.top3CompanyName"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),type:"text","@click":expression(`({ row }) => {
                      $table.remove($self.index)
                  }`)}}}}})}}}},companySizesList={companySizesList:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.companySize")},"x-query-engine-skip":!0,properties:{companySizes:{type:"array","x-component":"RenderTable",default:expression("$form.values.companySizes?.length?null:[{type:'人数'},{type:'劳务费用(元/年)'}]"),"x-component-props":{preColumns:"seq",editMode:!0,height:120,pagination:!1,sortable:!1,primaryKey:"id",cascadeDeletion:!0},"x-query-engine-skip":!0,properties:generateXindexInOrder({type:{type:"string",title:i18nExpression(""),"x-render-table-column":{minWidth:120},"x-read-pretty":!0,"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},employee:{type:"string",title:i18nExpression("vendorMod.employee"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},manager:{type:"string",title:i18nExpression("vendorMod.manager"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},technician:{type:"string",title:i18nExpression("vendorMod.technician"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},production:{type:"string",title:i18nExpression("vendorMod.production"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}}})}}}},rdCapableList={rdCapableList:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.RandDCapable5")},"x-visible":expression("$form.query('state').get('data').overseasRelation !== 'PERSONAL'"),"x-query-engine-skip":!0,properties:{rdCapables:{type:"array","x-component":"RenderTable",default:expression("$form.values.rdCapables?.length?null:[{type:'人数'}]"),"x-component-props":{class:"rdCapableList",preColumns:"",editMode:!0,height:"100px",pagination:!1,sortable:!1},"x-query-engine-skip":!0,properties:generateXindexInOrder({type:{type:"string",title:i18nExpression(""),"x-render-table-column":{width:"90px"},"x-read-pretty":!0,"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},rdQuantity:{type:"string",title:i18nExpression("vendorMod.RDPersonNum"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},seniorEngineerQuantity:{type:"string",title:i18nExpression("vendorMod.seniorEngineerQuantity"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},engineerQuantity:{type:"string",title:i18nExpression("vendorMod.engineerQuantity"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}}})},rdCapableAdditionals:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:{ifTechnicalStandard:{type:"string","x-decorator":"FormItem","x-component":"Radio.Group",enum:[{label:i18nExpression("common.yes"),value:"Y"},{label:i18nExpression("common.no"),value:"N"}],"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},title:i18nExpression("vendorMod.ifTechnicalStandard")},productsTechnicalStandard:{type:"string","x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),code:"INS_TYPE"},title:i18nExpression("vendorMod.productsTechnicalStandard")},memo:{type:"string","x-decorator":"FormItem","x-component":"Input",title:i18nExpression("vendorMod.memo"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),type:"textarea"}}}}}}},qualityControlList={qualityControlList:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.qualityControl")},"x-visible":expression("$form.query('state').get('data').overseasRelation !== 'PERSONAL'"),"x-query-engine-skip":!0,properties:{toolbar:{type:"void","x-component":"Space","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void",title:i18nExpression("common.new"),"x-component":"RButton","x-component-props":{type:"primary",disabled:expression("$form.query('state').get('data').$disabled"),"@click":expression(`() => {
                 $self.query('qualityControls')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
              }`)}}}},qualityControls:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,maxHeight:400,pagination:!1,sortable:!1,primaryKey:"id",cascadeDeletion:!0},"x-query-engine-skip":!0,properties:generateXindexInOrder({mainTestEquipment:{type:"string",title:i18nExpression("vendorMod.mainTestEquipment"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},mainTestProject:{type:"string",title:i18nExpression("vendorMod.mainTestProject"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),type:"text","@click":expression(`({ row }) => {
                      $table.remove($self.index)
                  }`)}}}}})}}}},equipmentInformationList={equipmentInformationList:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.deviceInfo")},"x-visible":expression("$form.query('state').get('data').overseasRelation !== 'PERSONAL'"),"x-query-engine-skip":!0,properties:{toolbar:{type:"void","x-component":"Space","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void",title:i18nExpression("common.new"),"x-component":"RButton","x-component-props":{type:"primary",disabled:expression("$form.query('state').get('data').$disabled"),"@click":expression(`() => {
                 $self.query('equipmentInformations')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
              }`)}}}},equipmentInformations:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,maxHeight:400,pagination:!1,sortable:!1,primaryKey:"id",cascadeDeletion:!0},"x-query-engine-skip":!0,properties:generateXindexInOrder({equipmentType:{type:"string",title:i18nExpression("vendorMod.equipmentType"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},equipmentName:{type:"string",title:i18nExpression("vendorMod.equipmentName"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},equipmentSpecification:{type:"string",title:i18nExpression("vendorMod.specification"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},equipmentQuantity:{type:"string",title:i18nExpression("bid_mod.quantity"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},unitEquipmentCapacity:{type:"string",title:i18nExpression("vendorMod.equipmentCapacity"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},manufacturer:{type:"string",title:i18nExpression("vendorMod.manufacturer"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},yearsOfService:{type:"string",title:i18nExpression("vendorMod.serviceYear"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),type:"text","@click":expression(`({ row }) => {
                      $table.remove($self.index)
                  }`)}}}}})}}}},productCapableInfosList={productCapableInfosList:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.productCapableInfo")},"x-visible":expression("$form.query('state').get('data').overseasRelation !== 'PERSONAL'"),"x-query-engine-skip":!0,properties:{toolbar:{type:"void","x-component":"Space","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void",title:i18nExpression("common.new"),"x-component":"RButton","x-component-props":{type:"primary",disabled:expression("$form.query('state').get('data').$disabled"),"@click":expression(`() => {
                 $self.query('productCapableInfos')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
              }`)}}}},productCapableInfos:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,maxHeight:400,pagination:!1,sortable:!1,primaryKey:"id",cascadeDeletion:!0},"x-query-engine-skip":!0,properties:generateXindexInOrder({productionBase:{type:"string",title:i18nExpression("vendorMod.proBase"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},eproductName:{type:"string",title:i18nExpression("vendorMod.proName"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},productBrand:{type:"string",title:i18nExpression("vendorMod.proBrand"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},mainProcess:{type:"string",title:i18nExpression("vendorMod.mainTechnics"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},annualOutput:{type:"string",title:i18nExpression("vendorMod.yearOutput"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},capacityRatio:{type:"string",title:i18nExpression("vendorMod.supplyCapacityRate"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},passRate:{type:"string",title:i18nExpression("vendorMod.proQualifiedRate"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},annualSales:{type:"string",title:i18nExpression("vendorMod.yearTurnover"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},remark:{type:"string",title:i18nExpression("dataConfMod.remark"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),type:"text","@click":expression(`({ row }) => {
                      $table.remove($self.index)
                  }`)}}}}})}}}},ceeaAfterSalesAbility={ceeaAfterSalesAbilityList:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.afterSalesService")},"x-visible":expression("$form.query('state').get('data').overseasRelation !== 'PERSONAL'"),"x-query-engine-skip":!0,properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:{saleService:{type:"string","x-decorator":"FormItem","x-component":"Input",title:i18nExpression("vendorMod.afterSalesService"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),type:"textarea"}}}}}}},clientStatusList={clientStatus:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.clientStatus")},"x-visible":expression("$form.query('state').get('data').overseasRelation !== 'PERSONAL'"),"x-query-engine-skip":!0,properties:{toolbar:{type:"void","x-component":"Space","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void",title:i18nExpression("common.new"),"x-component":"RButton","x-component-props":{type:"primary",disabled:expression("$form.query('state').get('data').$disabled"),"@click":expression(`() => {
                 $self.query('clientStatusList')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
              }`)}}}},clientStatusList:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,maxHeight:400,pagination:!1,sortable:!1,primaryKey:"id",cascadeDeletion:!0},"x-query-engine-skip":!0,properties:generateXindexInOrder({customerName:{type:"string",title:i18nExpression("vendorMod.customerName"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},region:{type:"string",title:i18nExpression("vendorMod.area"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},salesQuantity:{type:"string",title:i18nExpression("vendorMod.preSalesVol"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},salesAmount:{type:"string",title:i18nExpression("vendorMod.preSalesAmount"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},remark:{type:"string",title:i18nExpression("dataConfMod.remark"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),type:"text","@click":expression(`({ row }) => {
                      $table.remove($self.index)
                  }`)}}}}})}}}},managementInfoList={managementInfoList:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.managementSystemInfo")},"x-visible":expression("$form.query('state').get('data').overseasRelation !== 'PERSONAL'"),"x-query-engine-skip":!0,properties:{managementInfo:{type:"object","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:1,columnGap:32,rowGap:0},"x-query-engine-skip":!0,properties:{ifIsoQuality:{type:"string",default:"N","x-decorator":"FormItem","x-component":"Radio.Group",enum:[{label:i18nExpression("common.yes"),value:"Y"},{label:i18nExpression("common.no"),value:"N"}],"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),"@change":expression("$managementChange($self.value, 'ISO9001质量体系认证', $form)")},title:i18nExpression("vendorMod.msgIfPass1")},ifIsoEnviron:{type:"string",default:"N","x-decorator":"FormItem","x-component":"Radio.Group",enum:[{label:i18nExpression("common.yes"),value:"Y"},{label:i18nExpression("common.no"),value:"N"}],"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),"@change":expression("$managementChange($self.value, 'ISO14001环境体系认证', $form)")},title:i18nExpression("vendorMod.msgIfPass2")},ifOhsasSafe:{type:"string",default:"N","x-decorator":"FormItem","x-component":"Radio.Group",enum:[{label:i18nExpression("common.yes"),value:"Y"},{label:i18nExpression("common.no"),value:"N"}],"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),"@change":expression("$managementChange($self.value, 'OHSAS18000职业、健康安全体系认证', $form)")},title:i18nExpression("vendorMod.msgIfPass3")},otherAuthSit:{type:"string","x-decorator":"FormItem","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},title:i18nExpression("vendorMod.msgIfPass4")}}},toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void",title:i18nExpression("common.new"),"x-component-props":{type:"primary",disabled:expression("$form.query('state').get('data').$disabled"),"@click":expression(`() => {
                 $self.query('managementAttaches')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
                 console.log($self.query('managementAttaches').take().value)
              }`)}}}},managementAttaches:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,maxHeight:200,pagination:!1,sortable:!1,primaryKey:"managementAttachId",cascadeDeletion:!0},"x-query-engine-skip":!0,properties:generateXindexInOrder({documentInspection:{type:"string",title:i18nExpression("vendorMod.certificateRequirements"),"x-render-table-column":{minWidth:120},"x-read-pretty":!0,"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},fileuploadId:{type:"string","x-hidden":!0},authType:{type:"string",title:i18nExpression("vendorMod.authType"),"x-render-table-column":{minWidth:120},"x-component":"SrmCommonFile","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),extraData:{fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},"default-file":{fileId:expression("$table.getRowByIndex($self.index)?.fileuploadId"),fileName:expression("$self.value")},"validate-options":{accept:["jpg","png","jpeg"]},readonly:!1,"@on-change":expression(`({file}) => {
                const { fileId = '', fileName = '' } = file || {}
                let row = $table.getRowByIndex($self.index)
                row.fileuploadId = fileId.toString()
                row.authType = fileName
              }`)}},authDescription:{type:"string",title:i18nExpression("vendorMod.authDesc"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},authNum:{type:"string",title:i18nExpression("vendorMod.authNum"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),"@change":expression(`() => {
                let row = $table.getRowByIndex($self.index)
                row.authNum = row.authNum.replace(/[\\W]/g, '')
              }`)}},authDate:{type:"date",default:null,title:i18nExpression("vendorMod.authDate"),"x-render-table-column":{minWidth:150},"x-component-props":{style:"width:120px",disabled:expression("$form.query('state').get('data').$disabled")}},authOrg:{type:"string",title:i18nExpression("vendorMod.authOrg"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},endDate:{type:"date",title:i18nExpression("vendorMod.certUntil"),"x-render-table-column":{minWidth:150},"x-component-props":{style:"width:120px",disabled:expression("$form.query('state').get('data').$disabled")}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-reactions":expression(`async (feild) => {
                  const row = $table.getRowByIndex($self.index)
                  if (row?.documentInspection) {
                    feild.visible = false
                  } else {
                    feild.visible = true
                  }
                }`),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),type:"text","@click":expression(`({ row }) => {
                      $table.remove($self.index)
                  }`)}}}}})}}}};const _sfc_main$3={components:{},props:{stepsActive:{type:Number},status:{type:String,default:"DRAFT"},flowRemark:{type:String},registered:{type:Boolean,default:!1}},data(){return{}},computed:{},async created(){},mounted(){},updated(){},methods:{goTo(where){this.$emit("goToWhere",where)}}};var _sfc_render$3=function(){var _vm=this,_c=_vm._self._c;return _c("div",[_vm.registered?_vm._e():_c("el-steps",{staticClass:"comInfosteps",attrs:{active:_vm.stepsActive,"finish-status":"success","align-center":!0}},[_c("el-step",{attrs:{title:_vm.$t("vendorMod.registrationPolicy")}}),_c("el-step",{attrs:{title:_vm.$t("vendorMod.registerAccount")}}),_c("el-step",{attrs:{title:_vm.$t("vendorMod.registrationType")}}),_c("el-step",{attrs:{title:_vm.$t("vendorMod.authenticationInformation")}}),_c("el-step",{attrs:{title:_vm.$t("vendorMod.pendingApproval")}}),_c("el-step",{attrs:{title:_vm.$t("vendorMod.informationAuthentication")}})],1),_vm.status=="APPROVED"?_c("section",{staticClass:"boxs_success boxs_heater"},[_c("div",[_c("i",{staticClass:"el-icon-circle-check"}),_c("span",[_vm._v(_vm._s(_vm.$t("mainHeater.boxsHeater")))])]),_c("div",{staticClass:"small_font"},[_vm._v(" "+_vm._s(_vm.$t("mainHeater.boxsHeater2"))+" ")])]):_vm._e(),_vm.status=="REJECTED"?_c("section",{staticClass:"boxs_rejected boxs_heater"},[_c("div",[_c("i",{staticClass:"el-icon-circle-close"}),_c("span",[_vm._v(_vm._s(_vm.$t("mainHeater.reject")))])]),_c("div",{staticClass:"small_font"},[_vm._v(" "+_vm._s(_vm.$t("mainHeater.reject2"))+" ")]),_c("div",{staticClass:"small_font"},[_vm._v(" "+_vm._s(_vm.$t("announcements.title4",{flowRemark:_vm.flowRemark}))+" ")])]):_vm._e(),_vm.status=="SUBMITTED"?_c("section",{staticClass:"boxs_submitted boxs_heater"},[_c("div",[_c("i",{staticClass:"el-icon-warning-outline"}),_c("span",[_vm._v(_vm._s(_vm.$t("mainHeater.approval")))])]),_c("div",{staticClass:"small_font"},[_vm._v(" "+_vm._s(_vm.$t("mainHeater.approval2"))+" ")])]):_vm._e()],1)},_sfc_staticRenderFns$3=[],__component__$3=normalizeComponent(_sfc_main$3,_sfc_render$3,_sfc_staticRenderFns$3,!1,null,"24232b34",null,null);const MainHerder=__component__$3.exports,personal={personal:{type:"void","x-component":"CollapseItem","x-visible":!1,"x-component-props":{title:i18nExpression("vendorMod.companyBaseInfo3")},"x-query-engine-skip":!0,properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:{flowRemark:{type:"string","x-decorator":"FormItem","x-hidden":!0},status:{type:"string",default:"DRAFT","x-hidden":!0},overseasRelation2:{type:"string","x-decorator":"FormItem",default:"PERSONAL","x-component":"DictSelect","x-component-props":{disabled:!0,code:"RELATION_NEW"},title:expression("$t('vendorMod.overseasRelation')"),"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},companyName2:{type:"string",title:expression("$t('vendorMod.vendorName')"),"x-decorator":"FormItem","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},companyShortName2:{type:"string",title:expression("$t('vendorMod.companyShortNameV')"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},"x-decorator":"FormItem"},companyEnName:{type:"string",title:expression("$t('vendorMod.companyEnName')"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},"x-decorator":"FormItem"},exportQualification:{type:"string","x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),code:"YES_OR_NO"},title:expression("$t('vendorMod.exportQualification')")},idNumber:{type:"string",title:expression("$t('vendorMod.idNumber')"),"x-decorator":"FormItem","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},idCardFileId:{type:"string","x-hidden":!0,"x-decorator":"FormItem"},idCardFileName:{type:"string",title:i18nExpression("vendorMod.cardFile"),"x-component":"SrmCommonFile","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),"extra-data":{uploadType:"DEF",sourceType:"WEB_APP",fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},"default-file":{fileId:expression("$form.query('.idCardFileId').take()?.value"),fileName:expression("$form.query('.idCardFileName').take()?.value")},"@on-change":expression(`(file) => {
                if (file) {
                  const { fileId, fileName } = file.file || {}
                  $form.query('.idCardFileId').take().value = fileId.toString()
                  $form.query('.idCardFileName').take().value = fileName
                } else {
                  $form.query('.idCardFileId').take().value = null
                  $form.query('.idCardFileName').take().value = null
                }
              }`)},"x-decorator":"FormItem","x-validator":{required:!0,message:i18nExpression("common.requiredField")}}}}}}},vendorSiteInfoList={vendorSiteInfo:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.vendorSiteInfos2")},"x-query-engine-skip":!0,"x-visible":expression("$form.query('state').get('data').overseasRelation == 'PERSONAL'"),properties:{toolbar:{type:"void","x-component":"Space","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void",title:i18nExpression("common.new"),"x-component":"RButton","x-component-props":{type:"primary",disabled:expression("$form.query('state').get('data').$disabled"),"@click":expression(`() => {
                 $self.query('siteInfos')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
              }`)}}}},siteInfos:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,maxHeight:400,pagination:!1,sortable:!1,primaryKey:"siteInfoId",cascadeDeletion:!0},"x-query-engine-skip":!0,properties:generateXindexInOrder({country:{type:"string",title:i18nExpression("components.address.country"),"x-render-table-column":{minWidth:120},"x-component":"DictSelect","x-component-props":{code:"country",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$form.query('state').get('data').$disabled"),"@change":expression(`(val) => {
                let row = $table.getRowByIndex($self.index)
                // 选择国外就清理省市区，并且禁用
                if (row.country !== 'CN') {
                  row.province = null
                  row.plantCity = null
                }
              }`)},"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},province:{type:"string",title:i18nExpression("components.address.area"),"x-render-table-column":{minWidth:120},"x-component":"DictSelect","x-component-props":{code:"PROVINCE","custom-select-type":"PROVINCE",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$form.query('state').get('data').$disabled || $table.getRowByIndex($self.index).country!='CN'")}},city:{type:"string",title:i18nExpression("components.address.city"),"x-render-table-column":{minWidth:120},"x-component":"DictSelect","x-component-props":{code:expression("$table.getRowByIndex($self.index).province"),"custom-select-type":"CITY",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$form.query('state').get('data').$disabled || $table.getRowByIndex($self.index).country!='CN'")}},addressDetail:{type:"string",title:i18nExpression("components.address.detailAddress"),"x-render-table-column":{minWidth:150},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},postCode:{type:"string",title:i18nExpression("components.address.postalCode"),"x-render-table-column":{minWidth:150},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},siteComment:{type:"string",title:i18nExpression("components.address.remark"),"x-render-table-column":{minWidth:150},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},enabledFlag:{type:"string",title:i18nExpression("common.enable"),"x-render-table-column":{minWidth:100},"x-component":"Checkbox","x-component-props":{"true-label":"Y","false-label":"N",disabled:expression("$form.query('state').get('data').$disabled")}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),type:"text","@click":expression(`({ row }) => {
                      $table.remove($self.index)
                  }`)}}}}})}}}},_sfc_main$2=defineComponent({__name:"mainEngine",props:{formCompanyNature:{type:Object,default:()=>({})},type:{type:String,default:()=>""}},setup(__props){const props=__props,{app,emitTabRemove,t,vendor}=usePageHelper(),attrs=useAttrs(),workflowStatus=ref$1("DRAFT");let $disabled=!1;const newAddress=defineComponent({name:"newAddress",props:CAddress.props,setup(props2,{listeners,attrs:attrs2,slots}){return useAutoMountInstanceToField(),()=>h(CAddress,{props:{...attrs2,...props2},on:listeners,ref:"address"},slots)}}),query={"*":{},bankInfos:{"*":{}},contactInfos:{"*":{}},orgCategorys:{"*":{}},orgInfos:{"*":{}},operationInfo:{"*":{}},plantInfos:{"*":{}},fileUploads:{"*":{}},operationQualities:{"*":{}},operationProducts:{"*":{}},operationEquipments:{"*":{}},managementInfo:{"*":{}},managementAttaches:{"*":{}},supplierLeaderList:{"*":{}},otherInfo:{"*":{}},siteInfos:{"*":{}},operatingLogList:{"*":{}},questSupplierList:{"*":{}},qualityControls:{"*":{}},equipmentInformations:{"*":{}},productCapableInfos:{"*":{}},clientStatusList:{"*":{}},overallStrengths:{"*":{}},companySizes:{"*":{}},rdCapables:{"*":{}},rdCapableAdditionals:{"*":{}},operatingPerformances:{"*":{}},cateJournalList:{"*":{}}},$managementChange=(value,name,$form)=>{try{if(value){let data=$form.query(".managementAttaches").take().value;if(value=="Y"){let bold=1;data.forEach(e=>{e.documentInspection==name&&(bold=0)}),bold&&$form.query(".managementAttaches").take().invoke("addRow","unshift",{documentInspection:name,managementAttachId:null,managementInfoId:null,companyId:null,fileuploadId:null,authType:"",authDescription:"",authNum:"",authDate:null,authOrg:"",endDate:null})}else data.forEach((e,index2)=>{e.documentInspection==name&&$form.query(".managementAttaches").take().invoke("remove",index2)})}}catch{}},$ifPersonal=$form=>{$form.query(".personal").take().visible=!0,$form.query(".companyTypeAll").take().visible=!1,$form.query(".companyInfo").take().visible=!1,$form.query(".companyBaseInfo").take().visible=!1,$form.query(".factoryInfo").take().visible=!1,$form.query(".operatingPerformancesList").take().visible=!1,$form.query(".overallStrengthList").take().visible=!1,$form.query(".companySizesList").take().visible=!1,$form.query(".rdCapableList").take().visible=!1,$form.query(".qualityControlList").take().visible=!1,$form.query(".equipmentInformationList").take().visible=!1,$form.query(".productCapableInfosList").take().visible=!1,$form.query(".ceeaAfterSalesAbilityList").take().visible=!1,$form.query(".clientStatus").take().visible=!1,$form.query(".managementInfoList").take().visible=!1,$form.query(".fileUploadsList").take().visible=!1},schema=defineSchemas({state:{type:"void","x-component":"Fragment","x-hidden":!0,"x-data":{companyId:app.$store.getters.userInfo.companyId||null,$disabled:!1,overseasRelation:"",status:"",deleAttr:[],deleFileUploads:[],type:"",progressData:[{code:"companyTypeAll",name:t("vendorMod.companyType"),percentage:0},{code:"companyInfo",name:t("vendorMod.enterpriseThreeCertificates"),percentage:0},{code:"companyBaseInfo",name:t("vendorMod.companyBaseInfo2"),percentage:0},{code:"contactInfoList",name:t("vendorMod.contactInfo"),percentage:0},{code:"bankInfoList",name:t("vendorMod.bankInfo"),percentage:0},{code:"factoryInfo",name:t("vendorMod.factoryInfo"),percentage:0},{code:"operatingPerformancesList",name:t("vendorMod.operatingResult"),percentage:0},{code:"overallStrengthList",name:t("vendorMod.overallStrength"),percentage:0},{code:"companySizesList",name:t("vendorMod.companySize"),percentage:0},{code:"rdCapableList",name:t("vendorMod.RandDCapable5"),percentage:0},{code:"qualityControlList",name:t("vendorMod.qualityControl"),percentage:0},{code:"equipmentInformationList",name:t("vendorMod.deviceInfo"),percentage:0},{code:"productCapableInfosList",name:t("vendorMod.productCapableInfo"),percentage:0},{code:"ceeaAfterSalesAbilityList",name:t("vendorMod.afterSalesService"),percentage:0},{code:"clientStatus",name:t("vendorMod.clientStatus"),percentage:0},{code:"managementInfoList",name:t("vendorMod.managementSystemInfo"),percentage:0},{code:"fileUploadsList",name:t("vendorMod.sceneAttachmentInfo2"),percentage:0}]}},CompanyInfo:{type:"void","x-decorator":"QueryEngine","x-component":"el-container","x-component-props":{class:expression("$form.query('state').get('data').type !== 'registered' ? 'flex-container companyInfos' : 'flex-container registered'"),direction:"vertical"},"x-query-engine":{service:"sup",actions:{query:{immediate:!0,loading:!0,ready:expression(`() => {
            setTimeout(() => {
             $form.query('state').get('data').overseasRelation = $props.formCompanyNature.value?.overseasRelation

             $form.query('state').get('data').type = $props.type

             if ($props.formCompanyNature.value?.overseasRelation == 'PERSONAL') {
              $form.query('.personal').take().visible = true
              $form.query('.companyTypeAll').take().visible = false
              $form.query('.companyInfo').take().visible = false
              $form.query('.companyBaseInfo').take().visible = false

              $form.query('.overseasRelation2').take().value = 'PERSONAL'
            } else {
              $form.query('.personal').take().visible = false
              $form.query('.companyTypeAll').take().visible = true
              $form.query('.companyInfo').take().visible = true
              $form.query('.companyBaseInfo').take().visible = true
            }
             $form.query('.overseasRelation').take().value = $props.formCompanyNature.value?.overseasRelation
             $form.query('.companyType').take().value = $props.formCompanyNature.value?.companyType
            })
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
                $form.query('.personal').take().visible = true
                $form.query('.companyTypeAll').take().visible = false
                $form.query('.companyInfo').take().visible = false
                $form.query('.companyBaseInfo').take().visible = false

                data.companyName2 = data.companyName
              } else {
                $form.query('.personal').take().visible = false
                $form.query('.companyTypeAll').take().visible = true
                $form.query('.companyInfo').take().visible = true
                $form.query('.companyBaseInfo').take().visible = true
              }
            }

            $form.setValues(data)
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
             if ($props.formCompanyNature.value?.overseasRelation == 'PERSONAL') {
              let state = $form.query('state').get('data')
              $ifPersonal($form)
              state.progressData = personProgressData
            }
            $form.query('.overseasRelation').take().value = $props.formCompanyNature.value?.overseasRelation
            $form.query('state').get('data').overseasRelation = $props.formCompanyNature.value?.overseasRelation
             $form.query('.companyType').take().value = $props.formCompanyNature.value?.companyType
            })
            console.log(app.$store.getters.userInfo.companyId, 'companyId')
            console.log(!$buyer(), '$buyer')
            if(!app.$store.getters.userInfo.companyId && !$buyer()){
              $form.query('fileUploads').take(field => {
                  field.visible = true
              })
            }
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

            data.ifTechnicalStandard = data.rdCapableAdditionals[data.rdCapableAdditionals?.length-1]?.ifTechnicalStandard
            data.productsTechnicalStandard = data.rdCapableAdditionals[data.rdCapableAdditionals?.length-1]?.productsTechnicalStandard
            data.memo = data.rdCapableAdditionals[data.rdCapableAdditionals?.length-1]?.memo

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

            if(data.rdCapables?.length == 0) {
              data.rdCapables = [{type:'人数'}]
            }
            const rdCapables = [data.rdCapables[data.rdCapables?.length-1]]
            data.rdCapables = rdCapables
            data.companySizes = [data.companySizes[data.companySizes?.length-2],data.companySizes[data.companySizes?.length-1]]

            if (!$props.formCompanyNature.value?.overseasRelation) {
              $form.query('state').get('data').overseasRelation = data.overseasRelation
              const overseasRelation = state.overseasRelation
              if (overseasRelation == 'PERSONAL') {
                state.progressData = personProgressData
                $ifPersonal($form)

                data.companyName2 = data.companyName
                data.companyShortName2 = data.companyShortName
              }
            }
            if ($props.formCompanyNature.value?.overseasRelation) {
              data.overseasRelation = $props.formCompanyNature.value?.overseasRelation
             data.overseasRelation2 = $props.formCompanyNature.value?.overseasRelation
             data.companyType = $props.formCompanyNature.value?.companyType
             data.companyName2 = data.companyName
             data.companyShortName2 = data.companyShortName
            }
            console.log(data, 'data')
            $form.setValues(data)

            if (data.businessStartDate) {
              $form.query('.businessDate').take().value = [data.businessStartDate, data.businessEndDate]
            }
              setTimeout(() => {
                $form.query('fileUploads').take(field => {
                  field.visible = true
                  field.componentProps.componentInstance.reLoadFileInfo()
                })
              }, 5000)

              const cateJournalList = $form.query('.cateJournalList').take().value
              if (cateJournalList?.length > 0) {
                 const categoryName = cateJournalList.map(v => v.categoryName).join(',')
                 $form.query('.categoryName').take().value = categoryName
              }
              let deleFileUploads = [] // 附件要删除的列表
              data.fileUploads.forEach(e => {
                deleFileUploads.push({$delete:e.sceneFileId})
              })
              $form.query('state').get('data').deleFileUploads = deleFileUploads

              // 处理供应商为个人类型的时候，公司规模与研发能力不显示的问题
              setTimeout(() => {

              })

          }`)},vendorSave:{method:"read",autoFormatResult:!1,cascadeDeletion:!0,loading:!0},vendorWithdraw:{autoFormatResult:!1,loading:!0},vendorSubmit:{autoFormatResult:!1,cascadeDeletion:!0,loading:!0}}},properties:{layout:{type:"void","x-component":"FormContainer",items:{type:"object",properties:{prevOne:{type:"void","x-content":i18nExpression("common.prevOne"),"x-component":"Button","x-visible":expression("['', 'DRAFT', 'WITHDRAW', 'REJECTED'].includes($form.query('state').get('data').status)"),"x-component-props":{type:"default","@click":expression(`async (values) => {
                      let overseasRelation = null
                      overseasRelation = $form.query('state').get('data').overseasRelation
                      app.$emit('whatOverseasRelation', overseasRelation)
                    }`)}},staging:{type:"void","x-content":i18nExpression("common.staging"),"x-component":"Button","x-visible":expression("['', 'DRAFT', 'WITHDRAW'].includes($form.query('state').get('data').status)"),"x-component-props":{type:"default","@click":expression(`async (values) => {
                      $saveBill('staging', $form, $queryEngine, $confirm, $message, $bus, $t)
                    }`)}},submit:{type:"void","x-content":i18nExpression("common.submit"),"x-component":"Button","x-visible":expression("['', 'DRAFT', 'WITHDRAW', 'REJECTED'].includes($form.query('state').get('data').status)"),"x-component-props":{"@click":expression(`async (values) => {
                      $saveBill('submit', $form, $queryEngine, $confirm, $message, $bus, $t)
                    }`)}},recall:{type:"void","x-content":i18nExpression("common.recall"),"x-component":"Button","x-visible":expression("['SUBMITTED'].includes($form.query('state').get('data').status)"),"x-component-props":{"@click":expression(`async (values) => {
                      $saveBill('recall', $form, $queryEngine, $confirm, $message, $bus, $t)
                    }`)}}}},properties:{mainHerder:{type:"void","x-component":"MainHerder","x-component-props":{stepsActive:3,flowRemark:"",registered:expression("$form.query('state').get('data').type == 'registered'")}},collapse:{type:"void","x-component":"Collapse","x-component-props":{defaultOpenPanelCount:1},properties:{...companyType,...personal,...companyInfo,...companyBaseInfo,...contactInfoList,...bankInfoList,...vendorSiteInfoList,...factoryInfoList,...operatingPerformancesList,...overallStrengths,...companySizesList,...rdCapableList,...qualityControlList,...equipmentInformationList,...productCapableInfosList,...ceeaAfterSalesAbility,...clientStatusList,...managementInfoList,fileUploadsList:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.sceneAttachmentInfo2")},"x-visible":expression("$form.query('state').get('data').overseasRelation !== 'PERSONAL'"),"x-query-engine-skip":!0,properties:{fileUploads:{"x-query-engine-relation":"fileUploads:*",type:"array","x-component":"FileDynamic","x-component-props":{"scene-module-code":"SCENE_SUPPLIER_ATTACHMENT",businessId:expression("$form.values.companyId || null"),editable:expression("!$form.query('state').get('data').$disabled"),"need-init":!1}}}}}}}},CFillProgress:{type:"void","x-component":"CFillProgress","x-component-props":{class:"contract-progress",ref:"contractProgress",nodeName:"$t('logisticsMod.contractInfo')",data:expression("$form.query('state').get('data').progressData"),percentage:"{{true}}","@index-click":`{{ (code) => {
              let anchorEle = document.querySelector('#collapse_' + code)
              if (anchorEle) {
                anchorEle.scrollIntoView(true)
              }
           } }}`}}}}}),$saveBill=async(type,$form,$queryEngine,$confirm,$message,$bus,$t)=>{let values=JSON.parse(JSON.stringify($form.values));values.rdCapableAdditionals=[{}],values.rdCapableAdditionals[0].ifTechnicalStandard=values.ifTechnicalStandard,values.rdCapableAdditionals[0].productsTechnicalStandard=values.productsTechnicalStandard,values.rdCapableAdditionals[0].memo=values.memo;const businessDate=$form.query(".businessDate").take()?.value;if(businessDate&&businessDate?.length>0&&(values.businessStartDate=businessDate[0],values.businessEndDate=businessDate[1]),values.overseasRelation2&&(values.overseasRelation=values.overseasRelation2),$form.query("state").get("data")?.deleAttr[0]&&$form.query("state").get("data").overseasRelation!=="PERSONAL"&&(values.cateJournalList=[...values.cateJournalList,...$form.query("state").get("data")?.deleAttr[0]]),$form.query("state").get("data")?.deleFileUploads&&$form.query("state").get("data").overseasRelation!=="PERSONAL"&&(values.fileUploads?.forEach(e=>{delete e.sceneFileId}),values.fileUploads=[...values.fileUploads,...$form.query("state").get("data")?.deleFileUploads]),type=="submit"){let validate=0;if(await $form.validate().then().catch(eq=>{app.$message.error(eq[0].messages[0]),validate=1}),validate)return!1;if($form.query("state").get("data").overseasRelation!=="PERSONAL"){if(values.plantInfos?.length==0)return app.$message({message:$t("vendorMod.msgAtLeastPlantInfos"),type:"error"}),!1;{let bol=!1;if(values.plantInfos?.forEach(e=>{(!e.plantName||!e.plantNature||!e.plantArea||!e.plantCountry)&&(bol=!0)}),bol)return app.$message({message:$t("vendorMod.msgAtLeastPlantInfos2"),type:"error"}),!1}let bolManage=!1;if(values.managementAttaches.forEach(e=>{e.documentInspection&&!e.fileuploadId&&(bolManage=e.documentInspection)}),bolManage)return app.$message.error("请上传"+bolManage+"附件"),!1}if(values.bankInfos?.length===0){app.$message.warning("请添加银行信息");return}const bankInfosRequiredKeys=[{key:"bankCode",message:"第$index行缺少银行代码"},{key:"bankAccountName",message:"第$index行缺少账号名称"},{key:"bankAccount",message:"第$index行缺少银行账号"},{key:"currencyCode",message:"第$index行缺少币种"}];for(const[index2,item]of new Map(values.bankInfos.map((item2,index22)=>[index22,item2]))){const errorItem=bankInfosRequiredKeys.find(keyItem=>!item[keyItem.key]);if(errorItem){app.$message.warning(`银行信息${errorItem.message.replace("$index",index2+1)}`);return}}}values.supplierType==""&&(values.supplierType=null),(values.companyName2||values.companyShortName2)&&(values.companyName=values.companyName2,values.companyShortName=values.companyShortName2);const companyId=app.$store.getters.userInfo.companyId||null,status=$form.query(".status").take().value;type=="staging"?[null,void 0,"DRAFT"].includes(status)?(values.status="DRAFT",$queryEngine.request.save(values,{query,tree:!0,action:"vendorSave"}).then(res=>{if($message.success($t("common.successSave")),!app.$store.getters.userInfo.companyId){let userInfo=app.$store.getters.userInfo;userInfo.companyId=res[0].companyId,app.$store.commit("user/SET_USER_INFO",userInfo),app.$store.commit("user/SET_COMPANYID",res[0].companyId)}$queryEngine.request.baseRequest({action:"vendorRead"})}).catch(err=>{})):$queryEngine.request.save(values,{query,action:"vendorSave"}).then(res=>{if($message.success($t("common.successSave")),!app.$store.getters.userInfo.companyId){let userInfo=app.$store.getters.userInfo;userInfo.companyId=res[0].companyId,app.$store.commit("user/SET_USER_INFO",userInfo),app.$store.commit("user/SET_COMPANYID",res[0].companyId)}$queryEngine.request.baseRequest({action:"vendorRead"})}).catch(err=>{}):type=="recall"?app.$prompt("","撤回原因",{confirmButtonText:"确定",cancelButtonText:"取消",inputType:"textarea"}).then(({value})=>{let obj={companyId,flowRemark:value};$queryEngine.request.save(obj,{query,action:"vendorWithdraw"}).then(res=>{app.$message({message:"成功撤回",type:"success"}),$form.query(".fileUploads").take(field=>{field.visible=!1}),$queryEngine.request.baseRequest({action:"vendorRead"})})}):(app.$store.getters.userInfo!="BUYER"&&(values.potentialFlag="Y"),$queryEngine.request.save(values,{query,tree:!0,action:"vendorSubmit"}).then(res=>{if(!app.$store.getters.userInfo.companyId){let userInfo=app.$store.getters.userInfo;userInfo.companyId=res[0].companyId,app.$store.commit("user/SET_USER_INFO",userInfo),app.$store.commit("user/SET_COMPANYID",res[0].companyId)}$form.values.companyId=res[0].companyId,app.$emit("saveAll")}))},personProgressData=[{code:"userInfoForm",name:t("vendorMod.vendorUserInfo"),percentage:0},{code:"companyInfo",name:t("vendorMod.enterpriseThreeCertificates"),percentage:0},{code:"contactInfoList",name:t("vendorMod.contactInfo"),percentage:0},{code:"bankInfoList",name:t("vendorMod.bankInfo"),percentage:0},{code:"financeInfo",name:t("vendorMod.financeInfo"),percentage:0},{code:"vendorSiteInfo",name:t("vendorMod.vendorSiteInfos"),percentage:0}];return{__sfc:!0,props,app,emitTabRemove,t,vendor,attrs,workflowStatus,$disabled,newAddress,query,$managementChange,$ifPersonal,schema,$saveBill,personProgressData,scope:{app,t,$props:props,$attrs:attrs,performPlanService,$disabled,emitTabRemove,$saveBill,DictSelect,observer,$managementChange,query,validEmail,validatePhone,personProgressData,$ifPersonal},components:{SrmCommonFile:CommonFile,CAddress,CCategorySelect,FileDynamic,CFillProgress,newAddress,MainHerder},RenderEngine}}});var _sfc_render$2=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{schemaKey:"companyInfoMain",pageAttrs:_vm.$attrs,schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns$2=[],__component__$2=normalizeComponent(_sfc_main$2,_sfc_render$2,_sfc_staticRenderFns$2,!1,null,null,null,null);const mainEngine=__component__$2.exports,__$_require_71f3b21f__="/srm/assets/domestic-87aa8782.svg",__$_require_f42902c8__="/srm/assets/abroad-a81170c1.svg",__$_require_9d4a9af0__="/srm/assets/person-1cfed7ec.svg";const _sfc_main$1={components:{},data(){return{}},props:{value:{type:String}},watch:{value:{handler(){this.value},deep:!0}},computed:{},async created(){},mounted(){},updated(){},methods:{clickOne(how){this.choseWhat=how,this.$emit("change",how)}}};var _sfc_render$1=function(){var _vm=this,_c=_vm._self._c;return _c("div",{staticClass:"box-warp"},[_c("div",{staticClass:"box-one",class:{clickClass:_vm.value=="INSIDE"},staticStyle:{"margin-left":"0"},on:{click:function($event){return _vm.clickOne("INSIDE")}}},[_c("img",{attrs:{src:__$_require_71f3b21f__,alt:""}}),_vm._m(0)]),_c("div",{staticClass:"box-one",class:{clickClass:_vm.value=="OUT"},on:{click:function($event){return _vm.clickOne("OUT")}}},[_c("img",{attrs:{src:__$_require_f42902c8__,alt:""}}),_vm._m(1)]),_c("div",{staticClass:"box-one",class:{clickClass:_vm.value=="PERSONAL"},on:{click:function($event){return _vm.clickOne("PERSONAL")}}},[_c("img",{attrs:{src:__$_require_9d4a9af0__,alt:""}}),_vm._m(2)])])},_sfc_staticRenderFns$1=[function(){var _vm=this,_c=_vm._self._c;return _c("div",{staticClass:"box-center"},[_c("div",{staticClass:"blueOne"},[_vm._v("境内企业")]),_c("div",{staticClass:"comments"},[_vm._v("境内企业是指在国内指在中国境内依法设立的企业，包括外商投资外商独资企业外商投资企业境内投资。")])])},function(){var _vm=this,_c=_vm._self._c;return _c("div",{staticClass:"box-center"},[_c("div",{staticClass:"blueOne"},[_vm._v("境外企业")]),_c("div",{staticClass:"comments"},[_vm._v("境外企业是指在中国境外依法设立的企业，不是依据我国的法律设立的一般都是境外企业。")])])},function(){var _vm=this,_c=_vm._self._c;return _c("div",{staticClass:"box-center"},[_c("div",{staticClass:"blueOne"},[_vm._v("个人")]),_c("div",{staticClass:"comments"},[_vm._v("个体户一般指个体工商户。个体工商户是指在法律允许的范围内，依法经核准登记，从事工商经营活动的自然人或家庭。")])])}],__component__$1=normalizeComponent(_sfc_main$1,_sfc_render$1,_sfc_staticRenderFns$1,!1,null,"8c529e3e",null,null);const natureChose=__component__$1.exports,_sfc_main=defineComponent({__name:"index",props:{type:{type:String,default:()=>""}},setup(__props){const props=__props,{app,emitTabRemove,t,vendor}=usePageHelper(),schema=defineSchemas({state:{type:"void","x-component":"Fragment","x-hidden":!0,"x-data":{companyId:app.$store.getters.userInfo.companyId||null,status:null,wheres:"",type:""}},CompanyInfo:{type:"void","x-decorator":"QueryEngine","x-query-engine":{service:"sup",actions:{query:{immediate:!0,ready:expression(`() => {
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
            console.log(app.$store.getters.userInfo.companyId, 'companyId')
            console.log(data, 'data')
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
            // if (data.status == 'SUBMITTED') {
            //   state.wheres = 'success'
            // } else {
              state.wheres = 'main'
            // }
          }`)},vendorRead:{immediate:!0,loading:!0,method:"read",ready:expression(`() => {
            const state = $form.query('state').get('data')
            console.log(app.$store.getters.userInfo.companyId, 'companyId')
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
            console.log(res, 'data')
            const state = $form.query('state').get('data')
            state.status = data.status
            console.log(state, 'state')
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
          }`)}},success:{type:"void","x-hidden":expression("$form.query('state').get('data').wheres != 'success'"),"x-component":"success","x-component-props":{status:expression("$form.query('state').get('data').status"),"@goToWhere":expression(`(where) => {
            $form.query('state').get('data').wheres = where
            app.$emit('companyInfoIsSuccess', 'N')
          }`)}},companyNature:{type:"void","x-decorator":"FormContainer","x-decorator-props":{class:"companyNature"},items:{type:"object",properties:{submit:{type:"void","x-content":i18nExpression("common.nextOne"),"x-component":"Button","x-component-props":{"@click":expression(`async (values) => {
                  $form.validate().then(e => {
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
          }`),formCompanyNature:expression("$form.query('.formCompanyNature').take() || {}"),type:expression("$form.query('state').get('data').type"),status:expression("$form.query('state').get('data').status")}}}}});return{__sfc:!0,app,emitTabRemove,t,vendor,props,schema,scope:{$props:props,app,t,DictSelect,observer},components:{CAddress,CCategorySelect,FileDynamic,CFillProgress,stepOne,success,mainEngine,natureChose},RenderEngine}}});var _sfc_render=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{staticClass:"contractPaymentType",attrs:{schemaKey:"companyInfoMaintain",schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const index=__component__.exports;export{index as default};
