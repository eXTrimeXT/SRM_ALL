import{N as NavTabs}from"./index-a035e78f.js";import{ae as expression,af as i18nExpression,ag as yearMonthDaySelectorSegment,ah as radioGroupByYOrNSegment,ai as generateXindexInOrder,aj as editTableFormItemValid,ak as feedbackLayoutIsPopover,al as defineComponent,am as usePageHelper,an as useAttrs,ao as ref$1,ap as useAutoMountInstanceToField,aq as h,bS as computed,ar as defineSchemas,as as RenderEngine,at as performPlanService,au as DictSelect,av as observer,v as validEmail,a as validatePhone,aw as CommonFile,a6 as CCategorySelect,n as normalizeComponent,bW as dataTimeSelectorSegment,bt as changeFieldVisibleByDeps}from"./index-17d0ccd5.js";import{C as CAddress}from"./index-38ab0095.js";import{F as FileDynamic}from"./file-dynamic-30cdd411.js";import{C as CFillProgress}from"./index-6af40985.js";/* empty css                                                              */import"./file-dynamic-ab2ff377.js";import"./MainHeader-ef959a5c.js";/* empty css                                                                   */import"./file-dynamic.vue_vue_type_style_index_0_scoped_516fdfc3_lang-e128b539.js";import"./tree-utils-7df6be59.js";import"./basicSetting-f3b18103.js";import"./BaseTableBind-53264a4f.js";import"./util-6482eb24.js";import"./index-4d512f00.js";/* empty css                                                                      */import"./fileApi-3f0be128.js";/* empty css                                              */const Steps={steps:{type:"void","x-decorator":"div","x-decorator-props":{class:"stepDiv"},"x-component":"Steps","x-component-props":{alignCenter:!0,finishStatus:"success"},"x-reactions":{dependencies:["status"],fulfill:{state:{"component[1].active":expression(`
            ['DRAFT', '', null, undefined,'WITHDRAW','REJECTED'].includes($deps[0])
            ? 0
            : ['SUBMITTED'].includes($deps[0])
            ? 1
            : ['APPROVED'].includes($deps[0])
            ? 3
            : 2
          `)}}},properties:{step1:{type:"void","x-component":"el-step","x-component-props":{title:expression("$t('vendorMod.finishCompanyInfo')")}},step2:{type:"void","x-component":"el-step","x-component-props":{title:expression("$t('common.successSubmit')")}},step3:{type:"void","x-component":"el-step","x-component-props":{title:expression("$t('vendorMod.approvalSuccess')")}}}}},userInfoForm={userInfoForm:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.vendorUserInfo")},"x-query-engine-skip":!0,properties:{userInfo:{type:"object","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},"x-query-engine-skip":!0,properties:{username:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.userName')"),"x-component-props":{disabled:expression("$disabled"),placeholder:i18nExpression("userInfo.onlyNumOrEn"),"@change":expression(`() => {
                                let values = $self.value
                                console.log($self)
                                $self.setValue(values.replace(/[\\W]/g, ''))
                              }`)},"x-validator":{required:!0,message:i18nExpression("请输入用户名")}},nickname:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('dataConfMod.userName')"),"x-component-props":{disabled:expression("$disabled")},"x-validator":{required:!0,message:i18nExpression("vendorMod.msgInputNickname")}},email:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('common.email')"),"x-component-props":{disabled:expression("$disabled")},"x-validator":{required:!0,message:i18nExpression("请输入邮箱"),validator:expression(`(value, rule) => {
                if(value && !validEmail(value)){
                  return '请输入格式正确的邮箱地址'
                }
              }`)}},phone:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.contactPhone')"),"x-component-props":{disabled:expression("$disabled")},"x-validator":{required:!0,message:i18nExpression("请输入联系人电话"),validator:expression(`(value, rule) => {
                if(value && !validatePhone(value)){
                  return '请输入格式正确的电话号码'
                }
              }`)}},position:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('components.orgPositionSel.position')"),"x-component-props":{disabled:expression("$disabled")}},greenChannelReason:{type:"string","x-decorator":"FormItem",title:expression("$t('vendorMod.greenChannelReason')"),"x-component-props":{disabled:expression("$disabled"),type:"textarea"},"x-decorator-props":{gridSpan:3},"x-validator":{required:!0}}}}}}},companyType={companyType:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.companyType")},"x-query-engine-skip":!0,properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:{status:{type:"string","x-hidden":!0},overseasRelation:{type:"string","x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{disabled:expression("$disabled"),code:"RELATION"},title:expression("$t('vendorMod.overseasRelation')"),"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},companyType:{type:"string","x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{disabled:expression("$disabled"),code:"COMPANY_NATURE","@change":expression(`() => {

              }`)},"x-reactions":{dependencies:["overseasRelation"],fulfill:{state:{visible:expression('$deps[0] == "INSIDE"')}}},title:expression("$t('vendorMod.companyType')"),"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},supplierType:{type:"string","x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),code:"SUPPLIER_TYPE"},title:expression("$t('supplierRating.supplierType')"),"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},dunsCode:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.dunsCode')"),"x-reactions":{dependencies:["overseasRelation"],fulfill:{state:{visible:expression('$deps[0] == "OUT"')}}},"x-component-props":{disabled:expression("$disabled")}}}}}}},companyInfo={companyInfo:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.enterpriseThreeCertificates")},"x-query-engine-skip":!0,properties:{div:{type:"void","x-component":"div","x-component-props":{class:"companyInfo"},properties:{businessLicense:{type:"string","x-hidden":!0},businessLicenseFileId:{type:"string","x-component":"SrmCommonFile","x-component-props":{readonly:expression("$disabled"),"list-type":"picture-card",style:{width:"33%","padding-right":"25px"},defaultFile:{fileId:expression("$self.value"),fileName:expression("$form.query('businessLicense').get('value')")},"dragger-options":{width:"100%",height:"345px"},limit:1,drag:"drag","@on-change":expression(`({ file }) => {
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
                        $form.query('.companyCreationDate').take().value = app.$dayjs(licenseData.companyCreationDate).format('YYYY-MM-DD')
                      })
                      .catch(err => {
                        console.log(err)
                      })
                  }
                  $form.query('.businessLicenseFileId').take().value = fileId.toString()
                  $form.query('.businessLicense').take().value = fileName
          }`)},title:""},layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical",style:{width:"67%","padding-left":"20px"}},"x-component":"FormGrid","x-component-props":{maxColumns:2,columnGap:32,rowGap:0},properties:{companyName:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.companyName')"),"x-component-props":{disabled:expression("$disabled")},"x-validator":{required:!0,message:i18nExpression("vendorMod.msgCompanyName")}},legalPerson:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.legalPerson')"),"x-component-props":{disabled:expression("$disabled")},"x-validator":{required:!0,message:i18nExpression("vendorMod.msgLegalPerson")}},lcCode:{type:"string","x-visible":expression("$form.query('.overseasRelation').take().value == 'INSIDE'"),"x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.lcCode')"),"x-component-props":{disabled:expression("$disabled")},"x-validator":{required:expression("$form.query('.overseasRelation').take().value == 'INSIDE'"),message:i18nExpression("vendorMod.msgLcCode")}},businessLicenseNo:{type:"string","x-visible":expression("$form.query('.overseasRelation').take().value == 'OUT'"),"x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.lcCode2')"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},registCurrency:{type:"string","x-hidden":!0},registeredCapital:{type:"number","x-visible":expression("$form.query('.companyType').take().value != 'GETI'"),"x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.registeredCapital')"),"x-component-props":{disabled:expression("$disabled"),class:"input-with-select"},"x-content":{append:expression(`observer(
                {
                render(h) {
                  const targetField = $self.query('.registCurrency').take()
                  return h(DictSelect, {
                    props: {
                      value: targetField.value,
                      code: 'currency'
                    },
                    attrs: {
                      disabled: $form.query('state').get('data').$disabled,
                    },
                    on: {
                      'change-value': (value) => {
                        targetField.value = value
                      }
                    }
                  })
                }
              }
              )
            `)},"x-validator":{required:expression("!['GETI','FEIYINGLI'].includes($form.query('.companyType').take().value)"),message:i18nExpression("vendorMod.msgRegisteredCapital")}},companyCreationDate:{type:"date",default:null,"x-decorator":"FormItem","x-component-props":{placeholder:i18nExpression("common.pleaseSelectDate"),format:"yyyy-MM-dd","value-format":"yyyy-MM-dd",disabled:expression("$disabled")},title:expression("$t('vendorMod.creationDate')"),"x-validator":{required:expression("!['FEIYINGLI'].includes($form.query('.companyType').take().value)"),message:i18nExpression("vendorMod.msgCreationDate")}},businessStartDate:{type:"date","x-hidden":!0},businessEndDate:{type:"date","x-hidden":!0},businessDate:{type:"string","x-component":"DatePicker","x-decorator":"FormItem","x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],type:"daterange",disabled:expression("$disabled")},title:expression("$t('vendorMod.dateBusiness')"),"x-validator":{required:expression("!['FEIYINGLI'].includes($form.query('.companyType').take().value)"),message:i18nExpression("vendorMod.msgCreationDate")}},companyShortName:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.companyShortName')"),"x-component-props":{disabled:expression("$disabled")}},registrationAuthority:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.registrationAuthority')"),"x-component-props":{disabled:expression("$disabled")}},businessScope:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.businessScope')"),"x-component-props":{disabled:expression("$disabled"),type:"textarea"}}}}}}}}},companyBaseInfo={companyBaseInfo:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.companyBaseInfo")},"x-query-engine-skip":!0,properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:{ceeaAgentBrand:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.agencyBrand')"),"x-component-props":{disabled:expression("$disabled")},"x-validator":{required:!1,message:i18nExpression("vendorMod.msgAgencyBrand")}},categoryName:{type:"string","x-hidden":!0},cateJournalList:{type:"Array","x-hidden":!0},cateJournalListAll:{type:"Array","x-decorator":"FormItem","x-component":"CCategorySelect",title:expression("$t('vendorMod.mainCategory')"),"x-query-engine-skip":!0,"x-component-props":{disabled:expression("$disabled"),"select-type":"input",class:"categoryName",multiple:!0,"selected-lines":expression("$form.query('.cateJournalList').take().value"),placeholder:expression("$form.query('.categoryName').take().value"),"@select":expression(`(val) => {
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
                    $form.query('.cateJournalList').take().value = []
                    $form.query('.categoryName').take().value = ''
                  }
                }
              }`)},"x-validator":{required:!1,message:i18nExpression("vendorMod.msgAgencyBrand")}},ceeaIfListed:{title:i18nExpression("vendorMod.ifListed"),...radioGroupByYOrNSegment,"x-component-props":{disabled:expression("$disabled")}},ceeaListedTime:{type:"date",default:null,"x-visible":"{{$form.query('.ceeaIfListed').take().value == 'Y'}}","x-decorator":"FormItem","x-component-props":{placeholder:i18nExpression("common.pleaseSelectDate"),format:"yyyy-MM-dd","value-format":"yyyy-MM-dd",disabled:expression("$disabled")},title:expression("$t('vendorMod.listedDate')"),"x-validator":{required:!0,message:i18nExpression("请选择上市时间")}},listedExchange:{type:"string","x-visible":"{{$form.query('.ceeaIfListed').take().value == 'Y'}}","x-decorator":"FormItem","x-component-props":{disabled:expression("$disabled")},title:expression("$t('vendorMod.listedExchange')"),"x-validator":{required:!0,message:i18nExpression("请选择上市交易所")}},ceeaBusinessModel:{type:"string","x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{disabled:expression("$disabled"),code:"BIZ_MODEL"},title:expression("$t('vendorMod.bizModel')")},companyCountry:{type:"string",title:i18nExpression("components.address.country"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"country",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$disabled"),"@change":expression(`(val) => {
                let row = $form.values
                // 选择国外就清理省市区，并且禁用
                if (row.companyCountry !== 'CN') {
                  row.companyProvince = null
                  row.companyCity = null
                }
              }`)}},companyProvince:{type:"string",title:i18nExpression("components.address.area"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"PROVINCE","custom-select-type":"PROVINCE",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$disabled || $form.values.companyCountry!='CN'")}},companyCity:{type:"string",title:i18nExpression("components.address.city"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:expression("$form.values.companyProvince"),"custom-select-type":"CITY",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$disabled || $form.values.companyCountry!='CN'")}},companyAddress:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('components.address.detailAddress2')"),"x-component-props":{disabled:expression("$disabled")},"x-validator":{required:!0,message:i18nExpression("vendorMod.msgDetailAddr")}},ceeaHasParentCompany:{title:i18nExpression("vendorMod.ifParentCompany"),...radioGroupByYOrNSegment,"x-component-props":{disabled:expression("$disabled")}},ceeaParentCompanyName:{type:"string","x-decorator":"FormItem","x-component":"Input","x-visible":"{{$form.query('.ceeaHasParentCompany').take().value == 'Y'}}",title:expression("$t('vendorMod.parentCompanyName')"),"x-component-props":{disabled:expression("$disabled")},"x-validator":{required:!0,message:i18nExpression("请输入母公司名称")}},ceeaParentCompanyLcCode:{type:"string","x-decorator":"FormItem","x-component":"Input","x-visible":"{{$form.query('.ceeaHasParentCompany').take().value == 'Y'}}",title:expression("$t('vendorMod.parentCompanyLcCode')"),"x-component-props":{disabled:expression("$disabled")},"x-validator":{required:!0,message:i18nExpression("请输入母公司统一信用代码")}},ceeaCompanyIntro:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.companyProfile')"),"x-component-props":{disabled:expression("$disabled")},"x-decorator-props":{gridSpan:3}}}}}}},contactInfoList={contactInfoList:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.contactInfo")},"x-query-engine-skip":!0,properties:{toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void",title:i18nExpression("common.new"),"x-component-props":{type:"primary",disabled:expression("$form.query('state').get('data').$disabled"),"@click":expression(`() => {
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
                  }`)}}}}})}}}},cooInfoList={cooInfo:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("supRisk.cooInfo")},"x-query-engine-skip":!0,properties:{toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void",title:i18nExpression("common.new"),"x-component-props":{type:"primary",disabled:expression("$form.query('state').get('data').$disabled"),"@click":expression(`() => {
                 $self.query('orgCategorys')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
              }`)}}}},orgCategorys:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,maxHeight:400,pagination:!1,sortable:!1,primaryKey:"orgCategoryId",cascadeDeletion:!0},"x-query-engine-skip":!0,properties:generateXindexInOrder({orgCode:{type:"number","x-hidden":!0},orgName:{type:"string","x-hidden":!0},orgId:{type:"number",title:i18nExpression("components.userSelection.orgName"),"x-render-table-column":{minWidth:120},default:null,"x-component":"OrganizationSelector","x-component-props":{"parent-id":-1,"node-type":"OU",placeholder:i18nExpression("common.pleaseSelect"),multiple:!1,disabled:expression("$disabled"),"@select":expression(`(node) => {
                let row = $table.getRowByIndex($self.index)
                row.orgId = node ? node.organizationId : null
                row.orgCode = node ? node.organizationCode : null
                row.orgName = node ? node.organizationName : null
              }`)},...editTableFormItemValid},categoryId:{type:"number","x-hidden":!0},categoryCode:{type:"number","x-hidden":!0},categoryName:{type:"string",title:i18nExpression("vendorMod.category"),"x-render-table-column":{minWidth:120},"x-component":"CCategorySelect","x-component-props":{showKey:"categoryName",placeholder:i18nExpression("vendorMod.msgCategoryNormalizer"),disabled:expression("$disabled"),"@select":expression(`(node) => {
                let row = $table.getRowByIndex($self.index)
                row.categoryId = node ? node.categoryId : null
                row.categoryName = node ? node.categoryName : ''
                row.categoryCode = node ? node.categoryCode : ''
              }`)},...editTableFormItemValid},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{disabled:expression("$disabled"),type:"text","@click":expression(`({ row }) => {
                      $table.remove($self.index)
                  }`)}}}}})}}}},financeInfoList={financeInfo:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.financeInfo")},"x-query-engine-skip":!0,properties:{toolbar:{type:"void","x-component":"Space","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void",title:i18nExpression("common.new"),"x-component":"RButton","x-component-props":{type:"primary",disabled:expression("$form.query('state').get('data').$disabled || $form.query('.orgCategorys').take().value.length == 0"),"@click":expression(`() => {
                 $self.query('financeInfos')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
              }`)}}}},financeInfos:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,maxHeight:400,pagination:!1,sortable:!1,primaryKey:"financeInfoId",cascadeDeletion:!0},"x-query-engine-skip":!0,properties:generateXindexInOrder({orgCode:{type:"string","x-hidden":!0},orgName:{type:"string","x-hidden":!0},orgId:{type:"string",title:i18nExpression("vendorMod.ceeaOrgName2"),"x-render-table-column":{minWidth:120},"x-component":"Select","x-component-props":{disabled:expression("$disabled"),"@change":expression(`(val) => {
                  const orgCategorys = $form.query('.orgCategorys').take().value
                  let datas = []
                  orgCategorys.forEach(resData => {
                    const objs = {
                      key:resData.orgId,
                      label:resData.orgName,
                      value:resData.orgId
                    }
                    datas.push(objs)
                  })
                  let dictItem = datas.find(i => i.orgId === val) || {}
                  let row = $table.getRowByIndex($self.index)
                  row.orgCode = dictItem.orgCode
                  row.orgName = dictItem.orgName
               }`)},"x-reactions":[expression(`(field) => {
                const orgCategorys = $form.query('.orgCategorys').take().value
                let datas = []
                orgCategorys.forEach(resData => {
                  const objs = {
                    key:resData.orgId,
                    label:resData.orgName,
                    value:resData.orgId
                  }
                  datas.push(objs)
                })
                // 去重
                let attrId = []
                let attr = []
                datas.forEach((dataE, index) => {
                  if (!attrId.includes(dataE.value) || index == 0) {
                    attrId.push(dataE.value)
                    attr.push(dataE)
                  }
                })
                $self.dataSource = attr
              }`)],...editTableFormItemValid},factoryCode:{type:"string",title:i18nExpression("vendorMod.factoryCode"),"x-render-table-column":{minWidth:150},"x-component-props":{disabled:expression("$disabled"),maxlength:"50","show-word-limit":!0,"@onKeyUp":"value=value.replace(/[^w\\/]/ig,'')"}},clearCurrency:{type:"string",title:i18nExpression("vendorMod.clearCurrency"),"x-render-table-column":{minWidth:150},"x-component":"DictSelect","x-component-props":{code:"BID_TENDER_CURRENCY",disabled:expression("$disabled")},...editTableFormItemValid},paymentMethod:{type:"string",title:i18nExpression("vendorMod.paymentMethod"),"x-render-table-column":{minWidth:150},"x-component":"DictSelect","x-component-props":{code:"PAYMENT_METHOD",disabled:expression("$disabled")},...editTableFormItemValid},paymentTerms:{type:"string",title:i18nExpression("vendorMod.paymentTerms"),"x-render-table-column":{minWidth:150},"x-component":"DictSelect","x-component-props":{code:"PAYMENT_TERMS",disabled:expression("$disabled")},...editTableFormItemValid},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{disabled:expression("$disabled"),type:"text","@click":expression(`({ row }) => {
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
                  }`)}}}}})}}}},vendorSiteInfoList={vendorSiteInfo:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.vendorSiteInfos")},"x-query-engine-skip":!0,properties:{toolbar:{type:"void","x-component":"Space","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void",title:i18nExpression("common.new"),"x-component":"RButton","x-component-props":{type:"primary",disabled:expression("$form.query('state').get('data').$disabled"),"@click":expression(`() => {
                 $self.query('siteInfos')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
              }`)}}}},siteInfos:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,maxHeight:400,pagination:!1,sortable:!1,primaryKey:"siteInfoId",cascadeDeletion:!0},"x-query-engine-skip":!0,properties:generateXindexInOrder({belongOprId:{type:"number","x-hidden":!0},orgCode:{type:"number","x-hidden":!0},orgName:{type:"string","x-hidden":!0},orgId:{type:"number",default:null,title:i18nExpression("dataConfMod.orgId"),"x-render-table-column":{minWidth:120},"x-component":"OrganizationSelector","x-component-props":{"parent-id":-1,"node-type":"OU",placeholder:i18nExpression("common.pleaseSelect"),multiple:!1,disabled:expression("$form.query('state').get('data').$disabled"),"@select":expression(`(node) => {
                let row = $table.getRowByIndex($self.index)
                row.orgId = node ? node.organizationId : null
                row.orgCode = node ? node.organizationCode : null
                row.orgName = node ? node.organizationName : null
                if (node) {
                  app.$http({
                    url: '/api-base/organization/organization/get',
                    method: 'GET',
                    params: { organizationId: node.organizationId },
                    loading: true
                  }).then(res => {
                    if (res.data) {
                      row.belongOprId = res.data.erpOrgId
                    }
                  })
                }
              }`)},...editTableFormItemValid},vendorSiteCode:{type:"string",title:i18nExpression("vendorMod.siteName"),"x-render-table-column":{minWidth:120},"x-component":"DictSelect","x-component-props":{code:"VENDOR_SITE_CODE",disabled:expression("$form.query('state').get('data').$disabled")},...editTableFormItemValid},country:{type:"string",title:i18nExpression("components.address.country"),"x-render-table-column":{minWidth:120},"x-component":"DictSelect","x-component-props":{code:"country",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$form.query('state').get('data').$disabled"),"@change":expression(`(val) => {
                let row = $table.getRowByIndex($self.index)
                // 选择国外就清理省市区，并且禁用
                if (row.country !== 'CN') {
                  row.province = null
                  row.plantCity = null
                }
              }`)},...editTableFormItemValid},province:{type:"string",title:i18nExpression("components.address.area"),"x-render-table-column":{minWidth:120},"x-component":"DictSelect","x-component-props":{code:"PROVINCE","custom-select-type":"PROVINCE",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$form.query('state').get('data').$disabled || $table.getRowByIndex($self.index).country!='CN'")}},city:{type:"string",title:i18nExpression("components.address.city"),"x-render-table-column":{minWidth:120},"x-component":"DictSelect","x-component-props":{code:expression("$table.getRowByIndex($self.index).province"),"custom-select-type":"CITY",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$form.query('state').get('data').$disabled || $table.getRowByIndex($self.index).country!='CN'")}},addressDetail:{type:"string",title:i18nExpression("components.address.detailAddress"),"x-render-table-column":{minWidth:150},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},...editTableFormItemValid},postCode:{type:"string",title:i18nExpression("components.address.postalCode"),"x-render-table-column":{minWidth:150},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},siteComment:{type:"string",title:i18nExpression("components.address.remark"),"x-render-table-column":{minWidth:150},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},enabledFlag:{type:"string",title:i18nExpression("common.enable"),"x-render-table-column":{minWidth:100},"x-component":"Checkbox","x-component-props":{"true-label":"Y","false-label":"N",disabled:expression("$form.query('state').get('data').$disabled")}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),type:"text","@click":expression(`({ row }) => {
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
                  }`)}}}}})}}}},companySizesList={companySizesList:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.companySize")},"x-query-engine-skip":!0,properties:{companySizes:{type:"array","x-component":"RenderTable",default:[{type:"人数"},{type:"劳务费用(元/年)"}],"x-component-props":{preColumns:"seq",editMode:!0,maxHeight:400,pagination:!1,sortable:!1,primaryKey:"id",cascadeDeletion:!0},"x-query-engine-skip":!0,properties:generateXindexInOrder({type:{type:"string",title:i18nExpression(""),"x-render-table-column":{minWidth:120},"x-read-pretty":!0,"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},employee:{type:"string",title:i18nExpression("vendorMod.employee"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},manager:{type:"string",title:i18nExpression("vendorMod.manager"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},technician:{type:"string",title:i18nExpression("vendorMod.technician"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},production:{type:"string",title:i18nExpression("vendorMod.production"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}}})}}}},rdCapableList={rdCapableList:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.RandDCapable5")},"x-query-engine-skip":!0,properties:{rdCapables:{type:"array","x-component":"RenderTable",default:[{type:"人数"}],"x-component-props":{class:"rdCapableList",preColumns:"",editMode:!0,height:"100px",pagination:!1,sortable:!1},"x-query-engine-skip":!0,properties:generateXindexInOrder({type:{type:"string",title:i18nExpression(""),"x-render-table-column":{width:"90px"},"x-read-pretty":!0,"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},rdQuantity:{type:"string",title:i18nExpression("vendorMod.RDPersonNum"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},seniorEngineerQuantity:{type:"string",title:i18nExpression("vendorMod.seniorEngineerQuantity"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},engineerQuantity:{type:"string",title:i18nExpression("vendorMod.engineerQuantity"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}}})},rdCapableAdditionals:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:{ifTechnicalStandard:{type:"string","x-decorator":"FormItem","x-component":"Radio.Group",enum:[{label:i18nExpression("common.yes"),value:"Y"},{label:i18nExpression("common.no"),value:"N"}],"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},title:i18nExpression("vendorMod.ifTechnicalStandard")},productsTechnicalStandard:{type:"string","x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),code:"INS_TYPE"},title:i18nExpression("vendorMod.productsTechnicalStandard")},memo:{type:"string","x-decorator":"FormItem","x-component":"Input",title:i18nExpression("vendorMod.memo"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),type:"textarea"}}}}}}},qualityControlList={qualityControlList:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.qualityControl")},"x-query-engine-skip":!0,properties:{toolbar:{type:"void","x-component":"Space","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void",title:i18nExpression("common.new"),"x-component":"RButton","x-component-props":{type:"primary",disabled:expression("$form.query('state').get('data').$disabled"),"@click":expression(`() => {
                 $self.query('qualityControls')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
              }`)}}}},qualityControls:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,maxHeight:400,pagination:!1,sortable:!1,primaryKey:"id",cascadeDeletion:!0},"x-query-engine-skip":!0,properties:generateXindexInOrder({mainTestEquipment:{type:"string",title:i18nExpression("vendorMod.mainTestEquipment"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},mainTestProject:{type:"string",title:i18nExpression("vendorMod.mainTestProject"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),type:"text","@click":expression(`({ row }) => {
                      $table.remove($self.index)
                  }`)}}}}})}}}},equipmentInformationList={equipmentInformationList:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.deviceInfo")},"x-query-engine-skip":!0,properties:{toolbar:{type:"void","x-component":"Space","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void",title:i18nExpression("common.new"),"x-component":"RButton","x-component-props":{type:"primary",disabled:expression("$form.query('state').get('data').$disabled"),"@click":expression(`() => {
                 $self.query('equipmentInformations')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
              }`)}}}},equipmentInformations:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,maxHeight:400,pagination:!1,sortable:!1,primaryKey:"id",cascadeDeletion:!0},"x-query-engine-skip":!0,properties:generateXindexInOrder({equipmentType:{type:"string",title:i18nExpression("vendorMod.equipmentType"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},equipmentName:{type:"string",title:i18nExpression("vendorMod.equipmentName"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},equipmentSpecification:{type:"string",title:i18nExpression("vendorMod.specification"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},equipmentQuantity:{type:"string",title:i18nExpression("bid_mod.quantity"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},unitEquipmentCapacity:{type:"string",title:i18nExpression("vendorMod.equipmentCapacity"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},manufacturer:{type:"string",title:i18nExpression("vendorMod.manufacturer"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},yearsOfService:{type:"string",title:i18nExpression("vendorMod.serviceYear"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),type:"text","@click":expression(`({ row }) => {
                      $table.remove($self.index)
                  }`)}}}}})}}}},productCapableInfosList={productCapableInfosList:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.productCapableInfo")},"x-query-engine-skip":!0,properties:{toolbar:{type:"void","x-component":"Space","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void",title:i18nExpression("common.new"),"x-component":"RButton","x-component-props":{type:"primary",disabled:expression("$form.query('state').get('data').$disabled"),"@click":expression(`() => {
                 $self.query('productCapableInfos')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
              }`)}}}},productCapableInfos:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,maxHeight:400,pagination:!1,sortable:!1,primaryKey:"id",cascadeDeletion:!0},"x-query-engine-skip":!0,properties:generateXindexInOrder({productionBase:{type:"string",title:i18nExpression("vendorMod.proBase"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},eproductName:{type:"string",title:i18nExpression("vendorMod.proName"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},productBrand:{type:"string",title:i18nExpression("vendorMod.proBrand"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},mainProcess:{type:"string",title:i18nExpression("vendorMod.mainTechnics"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},annualOutput:{type:"string",title:i18nExpression("vendorMod.yearOutput"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},capacityRatio:{type:"string",title:i18nExpression("vendorMod.supplyCapacityRate"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},passRate:{type:"string",title:i18nExpression("vendorMod.proQualifiedRate"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},annualSales:{type:"string",title:i18nExpression("vendorMod.yearTurnover"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},remark:{type:"string",title:i18nExpression("dataConfMod.remark"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),type:"text","@click":expression(`({ row }) => {
                      $table.remove($self.index)
                  }`)}}}}})}}}},ceeaAfterSalesAbility={ceeaAfterSalesAbilityList:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.afterSalesService")},"x-query-engine-skip":!0,properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:{saleService:{type:"string","x-decorator":"FormItem","x-component":"Input",title:i18nExpression("vendorMod.afterSalesService"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),type:"textarea"}}}}}}},clientStatusList={clientStatus:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.clientStatus")},"x-query-engine-skip":!0,properties:{toolbar:{type:"void","x-component":"Space","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void",title:i18nExpression("common.new"),"x-component":"RButton","x-component-props":{type:"primary",disabled:expression("$form.query('state').get('data').$disabled"),"@click":expression(`() => {
                 $self.query('clientStatusList')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
              }`)}}}},clientStatusList:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,maxHeight:400,pagination:!1,sortable:!1,primaryKey:"id",cascadeDeletion:!0},"x-query-engine-skip":!0,properties:generateXindexInOrder({customerName:{type:"string",title:i18nExpression("vendorMod.customerName"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},region:{type:"string",title:i18nExpression("vendorMod.area"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},salesQuantity:{type:"string",title:i18nExpression("vendorMod.preSalesVol"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},salesAmount:{type:"string",title:i18nExpression("vendorMod.preSalesAmount"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},remark:{type:"string",title:i18nExpression("dataConfMod.remark"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),type:"text","@click":expression(`({ row }) => {
                      $table.remove($self.index)
                  }`)}}}}})}}}},managementInfoList={managementInfoList:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.managementSystemInfo")},"x-query-engine-skip":!0,properties:{managementInfo:{type:"object","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:1,columnGap:32,rowGap:0},"x-query-engine-skip":!0,properties:{ifIsoQuality:{type:"string",default:"N","x-decorator":"FormItem","x-component":"Radio.Group",enum:[{label:i18nExpression("common.yes"),value:"Y"},{label:i18nExpression("common.no"),value:"N"}],"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),"@change":expression("$managementChange($self.value, 'ISO9001质量体系认证', $form)")},title:i18nExpression("vendorMod.msgIfPass1")},ifIsoEnviron:{type:"string",default:"N","x-decorator":"FormItem","x-component":"Radio.Group",enum:[{label:i18nExpression("common.yes"),value:"Y"},{label:i18nExpression("common.no"),value:"N"}],"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),"@change":expression("$managementChange($self.value, 'ISO14001环境体系认证', $form)")},title:i18nExpression("vendorMod.msgIfPass2")},ifOhsasSafe:{type:"string",default:"N","x-decorator":"FormItem","x-component":"Radio.Group",enum:[{label:i18nExpression("common.yes"),value:"Y"},{label:i18nExpression("common.no"),value:"N"}],"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),"@change":expression("$managementChange($self.value, 'OHSAS18000职业、健康安全体系认证', $form)")},title:i18nExpression("vendorMod.msgIfPass3")},otherAuthSit:{type:"string","x-decorator":"FormItem","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},title:i18nExpression("vendorMod.msgIfPass4")}}},toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void",title:i18nExpression("common.new"),"x-component-props":{type:"primary",disabled:expression("$form.query('state').get('data').$disabled"),"@click":expression(`() => {
                 $self.query('managementAttaches')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
              }`)}}}},managementAttaches:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,maxHeight:200,pagination:!1,sortable:!1,primaryKey:"managementAttachId",cascadeDeletion:!0},"x-query-engine-skip":!0,properties:generateXindexInOrder({documentInspection:{type:"string",title:i18nExpression("vendorMod.certificateRequirements"),"x-render-table-column":{minWidth:120},"x-read-pretty":!0,"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},fileuploadId:{type:"string","x-hidden":!0},authType:{type:"string",title:i18nExpression("vendorMod.authType"),"x-render-table-column":{minWidth:120},"x-component":"SrmCommonFile","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),extraData:{fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},"default-file":{fileId:expression("$table.getRowByIndex($self.index)?.fileuploadId"),fileName:expression("$self.value")},"validate-options":{accept:["jpg","png","jpeg"]},readonly:!1,"@on-change":expression(`({file}) => {
                const { fileId = '', fileName = '' } = file || {}
                let row = $table.getRowByIndex($self.index)
                row.fileuploadId = fileId.toString()
                row.authType = fileName
              }`)}},authDescription:{type:"string",title:i18nExpression("vendorMod.authDesc"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},authNum:{type:"string",title:i18nExpression("vendorMod.authNum"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),"@change":expression(`() => {
                let row = $table.getRowByIndex($self.index)
                row.authNum = row.authNum.replace(/[\\W]/g, '')
              }`)}},authDate:{type:"date",default:null,title:i18nExpression("vendorMod.authDate"),"x-render-table-column":{minWidth:150},"x-component-props":{style:"width:120px",disabled:expression("$form.query('state').get('data').$disabled")}},authOrg:{type:"string",title:i18nExpression("vendorMod.authOrg"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},endDate:{type:"date",default:null,title:i18nExpression("vendorMod.certUntil"),"x-render-table-column":{minWidth:150},"x-component-props":{style:"width:120px",disabled:expression("$form.query('state').get('data').$disabled")}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-reactions":expression(`async (feild) => {
                  const row = $table.getRowByIndex($self.index)
                  if (row?.documentInspection) {
                    feild.visible = false
                  } else {
                    feild.visible = true
                  }
                }`),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),type:"text","@click":expression(`({ row }) => {
                      $table.remove($self.index)
                  }`)}}}}})}}}},_sfc_main$2=defineComponent({__name:"vendorGreenChannelDetailEngine",setup(__props){const{app,emitTabRemove,t,vendor}=usePageHelper(),attrs=useAttrs(),workflowStatus=ref$1("DRAFT");let $disabled=["view","approve"].includes(attrs.params.flag||"");const newAddress=defineComponent({name:"newAddress",props:CAddress.props,setup(props,{listeners,attrs:attrs2,slots}){return useAutoMountInstanceToField(),()=>h(CAddress,{props:{...attrs2,...props},on:listeners,ref:"address"},slots)}}),customUpdateButton=computed(()=>!$disabled&&["SUPPLIER_SUBMITTED"].includes(workflowStatus.value)),viewUpdateButton=computed(()=>!$disabled&&!["APPROVED","SUPPLIER_SUBMITTED"].includes(workflowStatus.value)),disabledUpdateButton=computed(()=>["APPROVING"].includes(workflowStatus.value)),query={"*":{},bankInfos:{"*":{}},siteInfos:{"*":{}},contactInfos:{"*":{}},financeInfos:{"*":{}},orgCategorys:{"*":{}},orgInfos:{"*":{}},operationInfo:{"*":{}},cateJournalList:{"*":{}},plantInfos:{"*":{}},fileUploads:{"*":{}},operationQualities:{"*":{}},operationProducts:{"*":{}},operationEquipments:{"*":{}},managementInfo:{"*":{}},managementAttaches:{"*":{}},supplierLeaderList:{"*":{}},otherInfo:{"*":{}},operatingLogList:{"*":{}},questSupplierList:{"*":{}},qualityControls:{"*":{}},equipmentInformations:{"*":{}},productCapableInfos:{"*":{}},clientStatusList:{"*":{}},overallStrengths:{"*":{}},companySizes:{"*":{}},rdCapables:{"*":{}},rdCapableAdditionals:{"*":{}},operatingPerformances:{"*":{}},userInfo:{"*":{}}},initButtonConfig=$form=>{setTimeout(()=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;attrs.params,componentInstance.buttonConfigInfo.save.view=viewUpdateButton.value,componentInstance.buttonConfigInfo.submit.view=viewUpdateButton.value,componentInstance.buttonConfigInfo.save.disabled=disabledUpdateButton.value,componentInstance.buttonConfigInfo.submit.disabled=disabledUpdateButton.value,componentInstance.buttonConfigInfo.cancel.view=!$disabled,componentInstance.buttonConfigInfo.close.view=!1,componentInstance.setWorkflowBusinessId(attrs.params.companyId||""),componentInstance.setWorkflowTabDisabled([null,void 0,"DRAFT","WITHDRAW","REJECTED"].includes(attrs.params.row?.status)&&attrs.params.flag!="approve"),componentInstance.setWorkflowBusinessVariables({})})},updateButtonConfig=$form=>{setTimeout(()=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.buttonConfigInfo.save.view=viewUpdateButton.value,componentInstance.buttonConfigInfo.submit.view=viewUpdateButton.value,componentInstance.buttonConfigInfo.save.disabled=disabledUpdateButton.value,componentInstance.buttonConfigInfo.submit.disabled=disabledUpdateButton.value,componentInstance.buttonConfigInfo.close.view=!1},50)},$managementChange=(value,name,$form)=>{try{if(value){let data=$form.query(".managementAttaches").take().value;if(value=="Y"){let bold=1;data.forEach(e=>{e.documentInspection==name&&(bold=0)}),bold&&data.unshift({documentInspection:name,managementAttachId:null,managementInfoId:null,companyId:null,fileuploadId:null,authType:"",authDescription:"",authNum:"",authDate:"",authOrg:"",endDate:""})}else data.forEach((e,index2)=>{e.documentInspection==name&&data.splice(index2,1)});$form.query(".managementAttaches").take().value=data}}catch{}},schema=defineSchemas({state:{type:"void","x-component":"Fragment","x-hidden":!0,"x-data":{companyId:attrs.params.companyId||null,$disabled:!1,deleAttr:[],deleFileUploads:[]}},CompanyInfo:{type:"void","x-decorator":"QueryEngine","x-component":"el-container","x-component-props":{class:"flex-container vendorGreen",direction:"vertical"},"x-query-engine":{service:"sup",actions:{greenQuery:{immediate:!0,loading:!0,ready:expression(`() => {
            initButtonConfig($form)
            return $attrs.params && $attrs.params.companyId
          }`),method:"read",autoFormatResult:!1,transformRequest:expression(`(data, headers) => {
            data.action = 'greenQuery'
            data.tree = true
            data.loading = true
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
          }`),onSuccess:expression(`(res) => {
            const data = res.records[0]
            if (data.userInfo) {
              data.userInfo.greenChannelReason = data?.greenChannelReason
            }
            data.ifTechnicalStandard = data.rdCapableAdditionals[data.rdCapableAdditionals.length-1]?.ifTechnicalStandard
            data.productsTechnicalStandard = data.rdCapableAdditionals[data.rdCapableAdditionals.length-1]?.productsTechnicalStandard
            data.memo = data.rdCapableAdditionals[data.rdCapableAdditionals.length-1]?.memo
            if(data.rdCapables.length == 0) {
              data.rdCapables = [{type:'人数'}]
            }
            $form.setValues(data)

            const status = data.status
            if (['APPROVED', 'SUBMITTED', 'ABANDONED'].includes(status)) {
              $form.query('state').get('data').$disabled = true
            }
            if ($disabled) {
              $form.query('state').get('data').$disabled = true
            }

            if (data.businessStartDate) {
              $form.query('.businessDate').take().value = [data.businessStartDate, data.businessEndDate]
            }
              $form.query('fileUploads').take(field => {
                  field.componentProps.componentInstance.reLoadFileInfo()
              })
              const cateJournalList = $form.query('.cateJournalList').take().value
              if (cateJournalList.length > 0) {
                 const categoryName = cateJournalList.map(v => v.categoryName).join(',')
                 $form.query('.categoryName').take().value = categoryName
              }
            let deleFileUploads = [] // 附件要删除的列表
            data.fileUploads.forEach(e => {
              deleFileUploads.push({$delete:e.sceneFileId})
            })
            $form.query('state').get('data').deleFileUploads = deleFileUploads
            return data
          }`)},greenSave:{method:"read",cascadeDeletion:!0,loading:!0},greenSubmit:{method:"read",cascadeDeletion:!0,loading:!0}}},properties:{SchemaWorkflow:{type:"void","x-component":"SchemaWorkflow","x-component-props":{params:{activeWorkflowTab:expression('$attrs.params.flag != "view"')},"business-id":expression("$attrs.params?.companyId || null"),"business-type":"supplierGreenChannel","@click-handler":expression(`(type) => {
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus, $t)
          }`),"@submit-direct":expression(`(type) => {
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus, $t)
          }`),"@confirm":expression(`(type, comment) => {
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus, $t)
          }`),"@close-tab":expression(`() => {
            $back($bus)
          }`),"@update-integration-mode":expression(`(integrationMode) => {
            if (integrationMode.integrationMode == "None") {
              updateButtonConfig($form)
            }
          }`)},properties:{layout:{type:"void","x-component":"FormContainer",properties:{...Steps,collapse:{type:"void","x-component":"Collapse","x-component-props":{defaultOpenPanelCount:1},properties:{...userInfoForm,...companyType,...companyInfo,...companyBaseInfo,...contactInfoList,...bankInfoList,...cooInfoList,...financeInfoList,...factoryInfoList,...vendorSiteInfoList,...operatingPerformancesList,...overallStrengths,...companySizesList,...rdCapableList,...qualityControlList,...equipmentInformationList,...productCapableInfosList,...ceeaAfterSalesAbility,...clientStatusList,...managementInfoList,fileUploadsList:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.sceneAttachmentInfo2")},"x-query-engine-skip":!0,properties:{fileUploads:{"x-query-engine-relation":"fileUploads:*",type:"array","x-component":"FileDynamic","x-component-props":{"scene-module-code":"SCENE_SUPPLIER_ATTACHMENT",businessId:expression("$attrs.params.companyId || null"),editable:expression("!$disabled"),"need-init":!1}}}}}}}},CFillProgress:{type:"void","x-component":"CFillProgress","x-component-props":{class:"contract-progress",ref:"contractProgress",nodeName:"$t('logisticsMod.contractInfo')",data:`{{[
              {
                code: 'userInfoForm',
                name: $t('vendorMod.vendorUserInfo'),
                percentage: 0
              },
              {
                code: 'companyType',
                name: $t('vendorMod.companyType'),
                percentage: 0
              },
              {
                code: 'companyInfo',
                name: $t('vendorMod.enterpriseThreeCertificates'),
                percentage: 0
              },
              {
                code: 'companyBaseInfo',
                name: $t('vendorMod.companyBaseInfo'),
                percentage: 0
              },
              {
                code: 'contactInfoList',
                name: $t('vendorMod.contactInfo'),
                percentage: 0
              },
              {
                code: 'bankInfoList',
                name: $t('vendorMod.bankInfo'),
                percentage: 0
              },
              {
                code: 'cooInfo',
                name: $t('supRisk.cooInfo'),
                percentage: 0
              },
              {
                code: 'financeInfo',
                name: $t('vendorMod.financeInfo'),
                percentage: 0
              },
              {
                code: 'factoryInfo',
                name: $t('vendorMod.factoryInfo'),
                percentage: 0
              },
              {
                code: 'vendorSiteInfo',
                name: $t('vendorMod.vendorSiteInfos'),
                percentage: 0
              },
              {
                code: 'operatingPerformancesList',
                name: $t('vendorMod.operatingResult'),
                percentage: 0
              },
              {
                code: 'overallStrengthList',
                name: $t('vendorMod.overallStrength'),
                percentage: 0
              },
              {
                code: 'companySizesList',
                name: $t('vendorMod.companySize'),
                percentage: 0
              },
              {
                code: 'rdCapableList',
                name: $t('vendorMod.RandDCapable5'),
                percentage: 0
              },
              {
                code: 'qualityControlList',
                name: $t('vendorMod.qualityControl'),
                percentage: 0
              },
              {
                code: 'equipmentInformationList',
                name: $t('vendorMod.deviceInfo'),
                percentage: 0
              },
              {
                code: 'productCapableInfosList',
                name: $t('vendorMod.productCapableInfo'),
                percentage: 0
              },
              {
                code: 'ceeaAfterSalesAbilityList',
                name: $t('vendorMod.afterSalesService'),
                percentage: 0
              },
              {
                code: 'clientStatus',
                name: $t('vendorMod.clientStatus'),
                percentage: 0
              },
              {
                code: 'managementInfoList',
                name: $t('vendorMod.managementSystemInfo'),
                percentage: 0
              },
              {
                code: 'fileUploadsList',
                name: $t('vendorMod.sceneAttachmentInfo2'),
                percentage: 0
              }
            ]}}`,percentage:"{{true}}","@index-click":`{{ (code) => {
              let anchorEle = document.querySelector('#collapse_' + code)
              if (anchorEle) {
                anchorEle.scrollIntoView(true)
              }
           } }}`}}}}}}}),$back=$bus=>{emitTabRemove(attrs.tabName),$bus.$emit("green")},$saveBill=async(type,$form,$queryEngine,$confirm,$message,$bus,$t)=>{let values=$form.values;values.userInfo=$form.query(".userInfo").take().value,values.contactInfos=$form.query(".contactInfos").take().value,values.bankInfos=$form.query(".bankInfos").take().value,values.orgCategorys=$form.query(".orgCategorys").take().value,values.financeInfos=$form.query(".financeInfos").take().value,values.plantInfos=$form.query(".plantInfos").take().value,values.siteInfos=$form.query(".siteInfos").take().value,values.operatingPerformances=$form.query(".operatingPerformances").take().value,values.overallStrengths=$form.query(".overallStrengths").take().value,values.companySizes=$form.query(".companySizes").take().value,values.qualityControls=$form.query(".qualityControls").take().value,values.equipmentInformations=$form.query(".equipmentInformations").take().value,values.productCapableInfos=$form.query(".productCapableInfos").take().value,values.clientStatusList=$form.query(".clientStatusList").take().value,values.managementInfo=$form.query(".managementInfo").take().value,values.managementAttaches=$form.query(".managementAttaches").take().value,values.fileUploads=$form.query(".fileUploads").take().value,values.rdCapableAdditionals=[{}],values.rdCapableAdditionals[0].ifTechnicalStandard=values.ifTechnicalStandard,values.rdCapableAdditionals[0].productsTechnicalStandard=values.productsTechnicalStandard,values.rdCapableAdditionals[0].memo=values.memo,values.firstLoginFlag="N";const businessDate=$form.query(".businessDate").take()?.value;if(businessDate&&businessDate!==""&&businessDate.length>0&&(values.businessStartDate=$form.query(".businessDate").take()?.value[0],values.businessEndDate=$form.query(".businessDate").take()?.value[1]),values.greenChannelReason=values.userInfo.greenChannelReason,$form.query("state").get("data")?.deleAttr[0]&&(values.cateJournalList=[...values.cateJournalList,...$form.query("state").get("data")?.deleAttr[0]]),$form.query("state").get("data")?.deleFileUploads&&(values.fileUploads.forEach(e=>{delete e.sceneFileId}),values.fileUploads=[...values.fileUploads,...$form.query("state").get("data")?.deleFileUploads]),type!="SAVE"){let validate=0;if(await $form.validate().then().catch(eq=>{app.$message.error(eq[0].messages[0]),validate=1}),validate)return!1;if($form.query(".orgCategorys").take().value.length<=0)return app.$message.error("请输入合作信息"),!1;attrs.params?.companyId,$form.query(".status").take().value;let overseasRelation=$form.query(".overseasRelation").take().value,supplierType=$form.query(".supplierType").take().value;if(!overseasRelation||overseasRelation==""||!supplierType||supplierType=="")return $message.error($t("请输入完整企业性质数据")),!1;const orgCategorys=$form.query(".orgCategorys").take().value;if(orgCategorys.length>0){let orgBol=!1;if(orgCategorys.forEach(data=>{(["",null].includes(data.orgId)||["",null].includes(data.categoryId))&&(orgBol=!0)}),orgBol==!0)return $message.error($t("vendorMod.msgOrgCatTableInfo")),!1}}type=="SAVE"?[null,void 0,"DRAFT"].includes(status)?(values.status="DRAFT",$queryEngine.request.save(values,{query:{"*":{}},action:"greenSave"}).then(()=>{$message.success($t("common.successSave")),$bus.$emit("green"),emitTabRemove(attrs.tabName)}).catch(err=>{})):$queryEngine.request.save(values,{query:{"*":{}},action:"greenSave"}).then(()=>{$message.success($t("common.successSave")),$bus.$emit("green"),emitTabRemove(attrs.tabName)}).catch(err=>{}):$queryEngine.request.save(values,{query:{"*":{}},action:"greenSubmit"}).then(res=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.setWorkflowBusinessId(res.data[0]?.companyId||null),componentInstance.setWorkflowTabDisabled(!1),componentInstance.setWorkflowBusinessVariables({}),componentInstance.handlerAfter(type.toUpperCase(),()=>{$bus.$emit("green"),emitTabRemove(attrs.tabName)}),setTimeout(()=>{$form.readPretty=!0,$form.query("state").get("data").$disabled=!0,componentInstance.buttonConfigInfo.save.view=!1,componentInstance.buttonConfigInfo.submit.view=!1},100)})};return{__sfc:!0,app,emitTabRemove,t,vendor,attrs,workflowStatus,$disabled,newAddress,customUpdateButton,viewUpdateButton,disabledUpdateButton,query,initButtonConfig,updateButtonConfig,$managementChange,schema,$back,$saveBill,scope:{app,t,$attrs:attrs,updateButtonConfig,performPlanService,$disabled,emitTabRemove,initButtonConfig,$saveBill,$back,DictSelect,observer,$managementChange,query,validEmail,validatePhone},components:{SrmCommonFile:CommonFile,CAddress,CCategorySelect,FileDynamic,CFillProgress,newAddress},RenderEngine}}});var _sfc_render$2=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{schemaKey:"vendorGreenChannelDetail",pageAttrs:_vm.$attrs,schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns$2=[],__component__$2=normalizeComponent(_sfc_main$2,_sfc_render$2,_sfc_staticRenderFns$2,!1,null,null,null,null);const vendorGreenChannelDetail=__component__$2.exports,_sfc_main$1=defineComponent({__name:"vendorGreenChannelListEngine",setup(__props){const{emitTabAdd,app}=usePageHelper(),schema=defineSchemas({CompanyInfo:{type:"void","x-decorator":"QueryEngine","x-query-engine":{service:"sup",actions:{greenQuery:{immediate:!0,method:"paginationQuery",transformRequest:expression(`(data, headers) => {
              console.log(data)
              if (data.payload.filter) {
                data.payload.filter['dataSources'] = {
                  eq: 'MANUALLY_CREATE'
                }
              } else {
                data.payload = {
                  "filter": {
                      "dataSources": {
                          eq: 'MANUALLY_CREATE'
                      }
                  }
                }
              }

              return data
            }`)}}},"x-component":"el-container","x-component-props":{class:"flex-container the_contractTemplateList_wrapper",direction:"vertical"},properties:{bus:{type:"void","x-component":"BusEvent","x-component-props":{eventName:"green","@listener":expression(`() => {
            $queryEngine.state.paginationManagement.refresh()
          }`)}},query:{type:"object","x-query-engine-skip":!0,"x-component":"QueryFormByQueryEngine",properties:generateXindexInOrder({companyName:{type:"string",title:"{{$t('common.vendorName')}}","x-component":"QuickSearchWrapper","x-component-props":{showKey:"companyName",propKey:"companyName",name:"scc_sup_company_info_display_buyer"}},lcCode:{type:"string",title:"{{$t('vendorMod.lcCode')}}","x-query-engine-query-operator":"contains"},isBacklist:{type:"string",title:"{{$t('vendorMod.isBacklist')}}","x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"}},overseasRelation:{type:"string",title:"{{$t('vendorMod.overseasRelation')}}","x-component":"DictSelect","x-component-props":{code:"RELATION"}},companyType:{type:"string",title:"{{$t('vendorMod.companyType')}}","x-component":"DictSelect","x-component-props":{code:"COMPANY_NATURE"}},status:{type:"string",title:"{{$t('vendorMod.approveStatus')}}","x-component":"DictSelect","x-component-props":{code:"APPROVE_STATUS_TYPE"}},legalPerson:{type:"string",title:"{{$t('vendorMod.legalPerson')}}","x-query-engine-query-operator":"contains"},approvedDate:{title:"{{$t('vendorMod.permitDate')}}",...dataTimeSelectorSegment,"x-query-engine-query-operator":"between"},supplierType:{type:"string",title:"{{$t('supplierRating.supplierType')}}","x-component":"DictSelect","x-component-props":{code:"SUPPLIER_TYPE"}}})},toolbar:{type:"void","x-component":"Space","x-component-props":{style:"margin-bottom: 16px"},properties:{add:{type:"void",title:"{{$t('common.add')}}","x-component":"RButton","x-component-props":{type:"primary","@click":expression(`() => {
               tab = {
                  component: vendorGreenChannelDetail,
                  params: {
                    flag: 'add',
                    tabName: 'vendorGreenChannelDetail'
                  },
                  title: $t('vendorMod.addVendor'), // '新增供应商',
                  name: 'vendorGreenChannelDetail'
                }
               emitTabAdd(tab)
              }`)}},importExcel:{type:"void","x-component":"ImportExcel","x-component-props":{title:i18nExpression("vendorMod.importVendor"),type:"default",extraData:{fileModular:"sup",fileFunction:"companyInfoImportExcel",fileType:"excel"},upLoadUrl:"/api-sup/info/companyInfo/importExcel",downloadTemplateOptions:{downloadUrl:"/api-sup/info/companyInfo/importExcelTemplate",fileName:expression("$t('vendorMod.vendorImportTemplateXLXS')")},"@handleSuccess":expression(`() => {
                $queryEngine.state.paginationManagement.refresh()
              }`)}}}},table:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",class:"table-view-vxe-table",openCustomTable:!0},properties:generateXindexInOrder({companyId:{type:"string","x-hidden":!0},companyCode:{type:"string",title:"{{$t('common.vendorCode')}}","x-render-table-column":{width:120}},companyName:{"x-component":"TableButton","x-component-props":{type:"text","@click":expression(`({row}) => {
                let companyId = row.companyId
                let tab = {
                  component: vendorGreenChannelDetail,
                  params: {
                    flag: 'view',
                    companyId: companyId,
                    tabName: 'vendorGreenChannelDetail' + row.companyName,
                    row
                  },
                  title: row.companyName,
                  name: 'vendorGreenChannelDetail' + row.companyName
                }
                emitTabAdd(tab)
              }`)},"x-render-table-column":{title:i18nExpression("common.vendorName"),minWidth:150,customRender:!0}},supplierType:{type:"string",title:"{{$t('supplierRating.supplierType')}}","x-component":"DictSelect","x-component-props":{code:"SUPPLIER_TYPE"},"x-render-table-column":{width:150}},overseasRelation:{type:"string",title:"{{$t('vendorMod.overseasRelation')}}","x-component":"DictSelect","x-component-props":{code:"RELATION"},"x-render-table-column":{width:150}},companyType:{type:"string",title:"{{$t('vendorMod.companyType')}}","x-component":"DictSelect","x-component-props":{code:"COMPANY_NATURE"},"x-render-table-column":{width:100}},lcCode:{type:"string",title:"{{$t('vendorMod.lcCode')}}","x-render-table-column":{width:150}},legalPerson:{type:"string",title:"{{$t('vendorMod.legalPerson')}}","x-render-table-column":{width:150}},isBacklist:{type:"string",title:"{{$t('vendorMod.isBacklist')}}","x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"},"x-render-table-column":{width:120}},status:{type:"string",title:"{{$t('vendorMod.approveStatus')}}","x-component":"DictSelect","x-component-props":{code:"APPROVE_STATUS_TYPE"},"x-render-table-column":{width:100}},approvedDate:{title:"{{$t('vendorMod.permitDate')}}",...yearMonthDaySelectorSegment,"x-render-table-column":{width:130}},lastUpdateDate:{type:"string","x-hidden":!0,"x-query-engine-sort":"desc"},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList","x-component-props":{max:2},properties:{edit:{type:"void",title:"{{$t('common.edit')}}","x-reactions":changeFieldVisibleByDeps([".status"],"['DRAFT', 'WITHDRAW', 'REJECTED'].includes($deps[0])"),"x-component-props":{type:"text","@click":expression(`({ row }) => {
                    let companyId = row.companyId
                    let tab = {
                      component: vendorGreenChannelDetail,
                      params: {
                        flag: 'edit',
                        companyId: companyId,
                        tabName: 'vendorGreenChannelDetail' + row.companyName,
                        row
                      },
                      title: row.companyName,
                      name: 'vendorGreenChannelDetail' + row.companyName
                    }
                    emitTabAdd(tab)
                  }`)}},delete:{type:"void",title:"{{$t('common.delete')}}","x-reactions":changeFieldVisibleByDeps([".status"],"['DRAFT'].includes($deps[0])"),"x-component-props":{type:"text","@click":expression(`({ row }) => {
                    app.$confirm($t('common.confirmDelete'), {
                      confirmButtonText: $t('common.confirm'),
                      cancelButtonText: $t('common.cancel'),
                      type: 'warning'
                    })
                      .then(() => {
                        $queryEngine.request.delete(row.companyId).then(() => {
                          $message.success($t('common.successDelete'))
                          $queryEngine.state.paginationManagement.refresh()
                        })
                      })
                      .catch(() => {})
                  }`)}},doApproval:{type:"void",title:"{{$t('vendorMod.doApproval')}}","x-reactions":changeFieldVisibleByDeps([".status"],"['SUBMITTED'].includes($deps[0])"),"x-component-props":{type:"text","@click":expression(`({ row }) => {
                    let companyId = row.companyId
                    let tab = {
                      component: vendorGreenChannelDetail,
                      params: {
                        flag: 'approve',
                        companyId: companyId,
                        row,
                        tabName: 'vendorGreenChannelDetail' + row.companyName
                      },
                      title: row.companyName,
                      name: 'vendorGreenChannelDetail' + row.companyName
                    }
                    emitTabAdd(tab)
                  }`)}},abandon:{type:"void",title:"{{$t('common.abandon')}}","x-reactions":changeFieldVisibleByDeps([".status"],"['WITHDRAW', 'REJECTED'].includes($deps[0])"),"x-component-props":{type:"text","@click":expression(`({ row }) => {
                    let companyId = row.companyId
                    let tab = {
                      component: vendorGreenChannelDetail,
                      params: {
                        flag: 'approve',
                        companyId: companyId,
                        tabName: 'vendorGreenChannelDetail' + row.companyName,
                        row
                      },
                      title: row.companyName,
                      name: 'vendorGreenChannelDetail' + row.companyName
                    }
                    emitTabAdd(tab)
                  }`)}}}}})}}}});return{__sfc:!0,emitTabAdd,app,schema,scope:{emitTabAdd,app,i18nExpression,vendorGreenChannelDetail},components:{},RenderEngine}}});var _sfc_render$1=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{staticClass:"contractPaymentType",attrs:{schemaKey:"vendorGreenChannelList",schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns$1=[],__component__$1=normalizeComponent(_sfc_main$1,_sfc_render$1,_sfc_staticRenderFns$1,!1,null,null,null,null);const vendorGreenChannelListEngine=__component__$1.exports,_sfc_main={name:"VendorGreenChannel",components:{NavTabs},data(){return{activeTab:"vendorGreenChannelListEngine",tabs:[{title:()=>this.$t("vendorMod.vendorGreenChannel"),name:"vendorGreenChannelListEngine",component:vendorGreenChannelListEngine,closable:!1}]}},mounted(){this.$route.params.openSavePage&&this.openSavePage()},methods:{openSavePage(){}}};var _sfc_render=function(){var _vm=this,_c=_vm._self._c;return _c("NavTabs",{ref:"tabs",attrs:{"tabs-list":_vm.tabs,"cur-tab":_vm.activeTab}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const index=__component__.exports;export{index as default};
