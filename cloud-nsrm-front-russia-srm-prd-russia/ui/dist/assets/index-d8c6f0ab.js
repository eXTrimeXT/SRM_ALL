import{N as NavTabs}from"./index-a035e78f.js";import{ae as expression,af as i18nExpression,aB as generateCharExpressionByFunction,ag as yearMonthDaySelectorSegment,ah as radioGroupByYOrNSegment,ai as generateXindexInOrder,aj as editTableFormItemValid,ak as feedbackLayoutIsPopover,aC as requiredValidatorSegment,n as normalizeComponent,al as defineComponent,am as usePageHelper,an as useAttrs,ao as ref$1,ap as useAutoMountInstanceToField,aq as h,bS as computed,ar as defineSchemas,as as RenderEngine,at as performPlanService,au as DictSelect,av as observer,v as validEmail,a as validatePhone,aw as CommonFile,a6 as CCategorySelect,bW as dataTimeSelectorSegment,bt as changeFieldVisibleByDeps}from"./index-17d0ccd5.js";import{C as CAddress}from"./index-38ab0095.js";import{F as FileDynamic}from"./file-dynamic-30cdd411.js";import{C as CFillProgress}from"./index-6af40985.js";import{s as sceneFileApi}from"./basicSetting-f3b18103.js";/* empty css                                                              */import"./file-dynamic-ab2ff377.js";import"./MainHeader-ef959a5c.js";/* empty css                                                                   */import"./file-dynamic.vue_vue_type_style_index_0_scoped_516fdfc3_lang-e128b539.js";import"./tree-utils-7df6be59.js";import"./BaseTableBind-53264a4f.js";import"./util-6482eb24.js";import"./index-4d512f00.js";/* empty css                                                                      */import"./fileApi-3f0be128.js";/* empty css                                              */const Steps={steps:{type:"void","x-decorator":"div","x-decorator-props":{class:"stepDiv"},"x-component":"Steps","x-component-props":{alignCenter:!0,finishStatus:"success"},"x-reactions":{dependencies:["status"],fulfill:{state:{"component[1].active":expression(`
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
              }`)}},position:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('components.orgPositionSel.position')"),"x-component-props":{disabled:expression("$disabled")}},greenChannelReason:{type:"string","x-decorator":"FormItem",title:expression("$t('vendorMod.greenChannelReason')"),"x-component-props":{disabled:expression("$disabled"),type:"textarea"},"x-decorator-props":{gridSpan:3},"x-validator":{required:!0}}}}}}},companyType={companyType:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.companyType")},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").overseasRelation!=="PERSONAL"),"x-query-engine-skip":!0,properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:{status:{type:"string","x-hidden":!0},overseasRelation:{type:"string","x-hidden":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").activeStep==="companyNature"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{disabled:!0,code:"RELATION_NEW"},title:expression("$t('vendorMod.overseasRelation')"),"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},companyType:{type:"string","x-decorator":"FormItem","x-component-props":{disabled:expression("$disabled")},title:expression("$t('cusEntry.vendorMod.vendorType')"),"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},extUseType:{type:"string",title:i18nExpression("cusEntry.vendorMod.extUseType"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"SUPPLIER_USE"}}}}}}},companyInfo={companyInfo:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.enterpriseThreeCertificates")},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").overseasRelation!=="PERSONAL"),"x-query-engine-skip":!0,properties:{div:{type:"void","x-component":"div","x-component-props":{class:"companyInfo"},properties:{businessLicense:{type:"string","x-hidden":!0},businessLicenseFileId:{type:"string","x-component":"SrmCommonFile","x-component-props":{readonly:expression("$disabled"),"list-type":"picture-card",style:{width:"33%","padding-right":"25px"},defaultFile:{fileId:expression("$self.value"),fileName:expression("$form.query('businessLicense').get('value')")},"dragger-options":{width:"100%",height:"345px"},limit:1,drag:"drag","@on-change":expression(`({ file }) => {
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
                    })
                    .catch(err => {
                      console.log(err)
                    })
                  }
                  $form.query('.businessLicenseFileId').take().value = fileId.toString()
                  $form.query('.businessLicense').take().value = fileName
          }`)},title:""},layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical",style:{width:"67%","padding-left":"20px"}},"x-component":"FormGrid","x-component-props":{maxColumns:2,columnGap:32,rowGap:0},properties:{companyName:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.companyName')"),"x-component-props":{disabled:expression("$disabled")},"x-validator":{required:!0,message:i18nExpression("vendorMod.msgCompanyName")}},legalPerson:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.legalPerson')"),"x-component-props":{disabled:expression("$disabled")},"x-validator":{required:!0,message:i18nExpression("vendorMod.msgLegalPerson")}},lcCode:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.lcCode')"),"x-component-props":{disabled:expression("$disabled")},"x-validator":{required:!0,message:i18nExpression("vendorMod.msgLcCode")}},businessLicenseNo:{type:"string","x-visible":expression("$form.query('.overseasRelation').take().value == 'OUT'"),"x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.lcCode2')"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},registCurrency:{type:"string","x-hidden":!0},registeredCapital:{type:"number","x-visible":expression("$form.query('.companyType').take().value != 'GETI'"),"x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.registeredCapital')"),"x-component-props":{disabled:expression("$disabled"),class:"input-with-select","@change":expression(`(value) => {
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
                  `)},"x-validator":{required:expression("!['GETI','FEIYINGLI'].includes($form.query('.companyType').take().value)"),message:i18nExpression("vendorMod.msgRegisteredCapital")}},companyCreationDate:{type:"date",default:null,"x-decorator":"FormItem","x-component-props":{placeholder:i18nExpression("common.pleaseSelectDate"),format:"yyyy-MM-dd","value-format":"yyyy-MM-dd",disabled:expression("$disabled")},title:expression("$t('vendorMod.creationDate')"),"x-validator":{required:expression("!['FEIYINGLI'].includes($form.query('.companyType').take().value)"),message:i18nExpression("vendorMod.msgCreationDate")}},businessStartDate:{type:"date","x-hidden":!0},businessEndDate:{type:"date","x-hidden":!0},businessDate:{type:"string","x-component":"DatePicker","x-decorator":"FormItem","x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],type:"daterange",disabled:expression("$disabled")},title:expression("$t('vendorMod.dateBusiness')"),"x-validator":{required:expression("!['FEIYINGLI'].includes($form.query('.companyType').take().value)"),message:i18nExpression("vendorMod.msgCreationDate")}},companyShortName:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.companyShortName')"),"x-component-props":{disabled:expression("$disabled"),"show-word-limit":!0,maxlength:100}},companyEnName:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('cusEntry.vendorMod.companyEnName')"),"x-component-props":{disabled:expression("$disabled")}},businessScope:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.businessScope')"),"x-component-props":{disabled:expression("$disabled"),type:"textarea",maxlength:2e3},"x-decorator-props":{gridSpan:3}}}}}}}}},companyBaseInfo={companyBaseInfo:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.companyBaseInfo")},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").overseasRelation!=="PERSONAL"),"x-query-engine-skip":!0,properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:{ceeaBusinessModel:{type:"string","x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{disabled:expression("$disabled"),code:"BIZ_MODEL",multiple:!0},title:expression("$t('vendorMod.bizModel')")},ceeaIfListed:{title:i18nExpression("vendorMod.ifListed"),...radioGroupByYOrNSegment,"x-component-props":{disabled:expression("$disabled")}},ceeaListedTime:{type:"date",default:null,"x-visible":"{{$form.query('.ceeaIfListed').take().value == 'Y'}}","x-decorator":"FormItem","x-component-props":{placeholder:i18nExpression("common.pleaseSelectDate"),format:"yyyy-MM-dd","value-format":"yyyy-MM-dd",disabled:expression("$disabled")},title:expression("$t('vendorMod.listedDate')"),"x-validator":{required:!0,message:i18nExpression("请选择上市时间")}},categoryName:{type:"string","x-hidden":!0},cateJournalList:{type:"Array","x-hidden":!0},companyCountry:{type:"string",title:i18nExpression("components.address.country"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"country",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$disabled"),"@change":expression(`(val) => {
                let row = $form.values
                // 选择国外就清理省市区，并且禁用
                if (row.companyCountry !== 'CN') {
                  row.companyProvince = null
                  row.companyCity = null
                }
              }`)},"x-validator":{required:!0,message:i18nExpression("vendorMod.msgSelCountry")}},companyProvince:{type:"string",title:i18nExpression("components.address.area"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"PROVINCE","custom-select-type":"PROVINCE",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$disabled || $form.values.companyCountry!='CN'")}},companyCity:{type:"string",title:i18nExpression("components.address.city"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:expression("$form.values.companyProvince"),"custom-select-type":"CITY",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$disabled || $form.values.companyCountry!='CN'")}},companyAddress:{type:"string","x-decorator":"FormItem","x-component":"Input",title:i18nExpression("cusEntry.vendorMod.detailAddress"),"x-component-props":{disabled:expression("$disabled")},"x-validator":{required:!0,message:i18nExpression("vendorMod.msgDetailAddr")}},ceeaHasParentCompany:{title:i18nExpression("cusEntry.vendorMod.ifParentCompany"),...radioGroupByYOrNSegment,"x-component-props":{disabled:expression("$disabled")}},ceeaParentCompanyName:{type:"string","x-decorator":"FormItem","x-component":"Input","x-visible":"{{$form.query('.ceeaHasParentCompany').take().value == 'Y'}}",title:i18nExpression("cusEntry.vendorMod.parentCompanyName"),"x-component-props":{disabled:expression("$disabled")},"x-validator":{required:!0,message:i18nExpression("请输入集团名称")}},groupCountry:{type:"string",title:i18nExpression("cusEntry.vendorMod.parentCompanyCountry"),"x-visible":"{{$form.query('.ceeaHasParentCompany').take().value == 'Y'}}","x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"country",disabled:expression("$disabled"),placeholder:expression("$t('common.pleaseSelect')")},"x-validator":{required:!0,message:i18nExpression("cusEntry.tipMessage.parentCompanyCountryMsg")}},ceeaCompanyIntro:{type:"string","x-decorator":"FormItem","x-component":"Input.TextArea",title:expression("$t('vendorMod.companyProfile')"),"x-component-props":{disabled:expression("$disabled"),maxlength:2e3},"x-decorator-props":{gridSpan:3}}}}}}},contactInfoList={contactInfoList:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.contactInfo")},"x-query-engine-skip":!0,properties:{toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},"x-visible":expression("!$disabled"),properties:{add:{type:"void",title:i18nExpression("common.new"),"x-component-props":{type:"primary",disabled:expression("$form.query('state').get('data').$disabled"),"@click":expression(`() => {
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
                console.log(row)
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
              }`)}},socialSecurityCertificateFileId:{type:"string","x-render-table-column":{minWidth:100},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").overseasRelation!=="PERSONAL"),title:i18nExpression("cusEntry.vendorMod.socialSecurityCertificate"),"x-component":"SrmCommonFile","x-component-props":{"extra-data":{uploadType:"DEF",sourceType:"WEB_APP",fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},"default-file":{fileId:expression("$table.getRowByIndex($self.index)?.socialSecurityCertificateFileId"),fileName:expression("$table.getRowByIndex($self.index)?.socialSecurityCertificateFileName")},"@on-change":expression(`(file) => {
                if (file) {
                  const { fileId, fileName } = file.file || {}
                  $table.getRowByIndex($self.index).socialSecurityCertificateFileId = fileId.toString()
                  $table.getRowByIndex($self.index).socialSecurityCertificateFileName = fileName
                } else {
                  $table.getRowByIndex($self.index).socialSecurityCertificateFileId = null
                  $table.getRowByIndex($self.index).socialSecurityCertificateFileName = null
                }
              }`),readonly:expression("$disabled")}},ceeaComments:{type:"string",title:i18nExpression("dataConfMod.remark"),"x-render-table-column":{minWidth:100},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:60,fixed:"right"},"x-visible":expression("!$disabled"),"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),type:"text","@click":expression(`({ row }) => {
                      $table.remove($self.index)
                  }`)}}}}})}}}},bankInfoList={bankInfoList:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.bankInfo")},"x-query-engine-skip":!0,properties:{toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},"x-visible":expression("!$disabled"),properties:{add:{type:"void",title:i18nExpression("common.new"),"x-component-props":{type:"primary",disabled:expression("$form.query('state').get('data').$disabled"),"@click":expression(`() => {
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
              `)},...feedbackLayoutIsPopover,"x-validator":{required:generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").overseasRelation!=="OUT"),message:i18nExpression("common.requiredField")}},bankName:{type:"string",title:i18nExpression("components.bank.bankName"),"x-render-table-column":{minWidth:120},"x-read-pretty":!0,...feedbackLayoutIsPopover,"x-validator":{required:generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").overseasRelation!=="OUT"),message:i18nExpression("common.requiredField")}},unionCode:{type:"string",title:i18nExpression("components.bank.unionCode"),"x-render-table-column":{minWidth:120},"x-read-pretty":!0,...feedbackLayoutIsPopover,"x-validator":{required:generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").overseasRelation!=="OUT"),message:i18nExpression("common.requiredField")}},openingBank:{type:"string",title:i18nExpression("components.bank.branchBankName"),"x-render-table-column":{minWidth:120},"x-read-pretty":!0,...feedbackLayoutIsPopover,"x-validator":{required:generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").overseasRelation!=="OUT"),message:i18nExpression("common.requiredField")}},bankAccountName:{type:"string",title:i18nExpression("components.bank.accountName"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},...feedbackLayoutIsPopover,"x-validator":{required:generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").overseasRelation!=="OUT"),message:i18nExpression("common.requiredField")}},bankAccount:{type:"string",title:i18nExpression("components.bank.bankAccount"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},...feedbackLayoutIsPopover,"x-validator":{required:generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").overseasRelation!=="OUT"),message:i18nExpression("common.requiredField")}},currencyCode:{type:"string",title:i18nExpression("vendorMod.currencyCode"),"x-render-table-column":{minWidth:120},"x-component":"DictSelect","x-component-props":{code:"currency",disabled:expression("$form.query('state').get('data').$disabled")},...feedbackLayoutIsPopover,"x-validator":{required:generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").overseasRelation!=="OUT"),message:i18nExpression("common.requiredField")}},ceeaMainAccount:{type:"string",title:i18nExpression("components.bank.isMain"),"x-render-table-column":{minWidth:100},"x-component":"Checkbox","x-component-props":{"true-label":"Y","false-label":"N",disabled:expression("$form.query('state').get('data').$disabled"),"@change":expression(`(value) => {
                if (value === 'Y') {
                  $self.query('.ceeaEnabled').take().value = 'Y'
                }
              }`)},...feedbackLayoutIsPopover,"x-validator":{required:generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").overseasRelation!=="OUT"),message:i18nExpression("common.requiredField")}},ceeaEnabled:{type:"string",title:i18nExpression("components.bank.isActive"),"x-render-table-column":{minWidth:100},"x-component":"Checkbox","x-component-props":{"true-label":"Y","false-label":"N",disabled:expression("$form.query('state').get('data').$disabled")}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:60,fixed:"right"},"x-visible":expression("!$disabled"),"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),type:"text","@click":expression(`({ row }) => {
                      $table.remove($self.index)
                  }`)}}}}})}}}},financeInfoList={financeInfo:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("cusEntry.vendorMod.financeReport")},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").overseasRelation!=="PERSONAL"),"x-query-engine-skip":!0,properties:{financeInfoForm:{type:"void","x-query-engine-skip":!0,properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:{totalAssets:{type:"number",title:i18nExpression("cusEntry.vendorMod.totalAssets"),"x-decorator":"FormItem","x-component-props":{disabled:expression("$disabled")}},currentAssets:{type:"number",title:i18nExpression("cusEntry.vendorMod.workingCapital"),"x-decorator":"FormItem","x-component-props":{disabled:expression("$disabled")}},fixedAssets:{type:"number",title:i18nExpression("cusEntry.vendorMod.fixedAssets"),"x-decorator":"FormItem","x-component-props":{disabled:expression("$disabled")}},avgAnnualOutput:{type:"number",title:i18nExpression("cusEntry.vendorMod.threeYearsOutput"),"x-decorator":"FormItem","x-component-props":{disabled:expression("$disabled")}},avgAnnualProfit:{type:"number",title:i18nExpression("cusEntry.vendorMod.threeYearsNetProfits"),"x-decorator":"FormItem","x-component-props":{disabled:expression("$disabled")}}}}}},financeInfoAfterTag:{type:"void","x-component":"p","x-content":i18nExpression("cusEntry.vendorMod.threeYearsReportFile")},toolbar:{"x-visible":expression("!$disabled"),type:"void","x-component":"Space","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void",title:i18nExpression("common.new"),"x-component":"RButton","x-component-props":{type:"primary","@click":expression(`() => {
                 $self.query('npmFinanceReports')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
              }`)}}}},npmFinanceReports:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:expression("!$disabled"),maxHeight:400,pagination:!1,sortable:!1,primaryKey:"financeInfoId",cascadeDeletion:!0},"x-query-engine-skip":!0,properties:generateXindexInOrder({year:{type:"string",title:i18nExpression("cusEntry.vendorMod.year"),"x-component":"DatePicker","x-component-props":{type:"year",format:"yyyy","value-format":"yyyy"},"x-render-table-column":{minWidth:120}},remark:{type:"string",title:i18nExpression("cusEntry.vendorMod.remark"),"x-render-table-column":{minWidth:120}},fileId:{type:"string",title:i18nExpression("cusEntry.vendorMod.file"),"x-render-table-column":{minWidth:120},"x-component":"SrmCommonFile","x-component-props":{"extra-data":{uploadType:"DEF",sourceType:"WEB_APP",fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},"default-file":{fileId:expression("$table.getRowByIndex($self.index)?.fileId"),fileName:expression("$table.getRowByIndex($self.index)?.fileName")},"@on-change":expression(`({file}) => {
                const { fileId, fileName } = file || {}
                $table.getRowByIndex($self.index).fileId = fileId
                $table.getRowByIndex($self.index).fileName = fileName
              }`)}},operation:{type:"void","x-visible":expression("!$disabled"),title:"{{$t('common.operation')}}","x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{disabled:expression("$form.readPretty"),type:"text","@click":expression(`({ row }) => {
                      $table.remove($self.index)
                  }`)}}}}})},financeInfoAfterRemark:{type:"void","x-component":"p","x-content":i18nExpression("cusEntry.vendorMod.financeInfoRemark")}}}},companySizesList={companySizesList:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.companySize")},"x-query-engine-skip":!0,"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").overseasRelation!=="PERSONAL"),properties:{companySizes:{type:"array","x-component":"RenderTable",default:[{type:"人数"}],"x-component-props":{preColumns:"seq",editMode:expression("!$disabled"),maxHeight:250,pagination:!1,sortable:!1,primaryKey:"id",cascadeDeletion:!0},"x-query-engine-skip":!0,properties:generateXindexInOrder({totalNumber:{type:"string",title:i18nExpression("cusEntry.vendorMod.totalNum"),"x-render-table-column":{minWidth:120},"x-component-props":{"v-input-format":{type:"integer",negative:!1}}},socialSecurityNumber:{type:"string",title:i18nExpression("cusEntry.vendorMod.socialSecurity"),"x-render-table-column":{minWidth:120},"x-component-props":{"v-input-format":{type:"integer",negative:!1}}},managementNumber:{type:"string",title:i18nExpression("cusEntry.vendorMod.managerNum"),"x-render-table-column":{minWidth:120},"x-component-props":{"v-input-format":{type:"integer",negative:!1}}},developerNumber:{type:"string",title:i18nExpression("cusEntry.vendorMod.developmentNum"),"x-render-table-column":{minWidth:120},"x-component-props":{"v-input-format":{type:"integer",negative:!1}}},productionNumber:{type:"string",title:i18nExpression("cusEntry.vendorMod.productNum"),"x-render-table-column":{minWidth:120},"x-component-props":{"v-input-format":{type:"integer",negative:!1}}},overUndergraduateNumber:{type:"string",title:i18nExpression("cusEntry.vendorMod.bachelorDegreeOrAbove"),"x-render-table-column":{minWidth:120},"x-component-props":{"v-input-format":{type:"integer",negative:!1}}}})}}}},cooInfoList={cooInfo:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("supRisk.cooInfo")},"x-query-engine-skip":!0,properties:{toolbar:{type:"void","x-visible":expression("!$disabled"),"x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void",title:i18nExpression("common.new"),"x-component-props":{type:"primary",disabled:expression("$form.query('state').get('data').$disabled"),"@click":expression(`() => {
                 $self.query('orgCategorys')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
              }`)}}}},orgCategorys:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:expression("!$disabled"),maxHeight:400,pagination:!1,sortable:!1,primaryKey:"orgCategoryId",cascadeDeletion:!0},"x-query-engine-skip":!0,properties:generateXindexInOrder({orgCode:{type:"number","x-hidden":!0},orgName:{type:"string","x-hidden":!0},orgId:{type:"number",title:i18nExpression("components.userSelection.orgName"),"x-render-table-column":{minWidth:120},default:null,"x-component":"OrganizationSelector","x-component-props":{"parent-id":-1,"node-type":"OU",placeholder:i18nExpression("common.pleaseSelect"),multiple:!1,disabled:expression("$disabled"),"@select":expression(`(node) => {
                let row = $table.getRowByIndex($self.index)
                row.orgId = node ? node.organizationId : null
                row.orgCode = node ? node.organizationCode : null
                row.orgName = node ? node.organizationName : null
              }`)},...editTableFormItemValid},categoryId:{type:"number","x-hidden":!0},categoryCode:{type:"number","x-hidden":!0},categoryName:{type:"string",title:i18nExpression("vendorMod.category"),"x-render-table-column":{minWidth:120},"x-component":"CCategorySelect","x-component-props":{showKey:"categoryName",placeholder:i18nExpression("vendorMod.msgCategoryNormalizer"),disabled:expression("$disabled"),"@select":expression(`(node) => {
                let row = $table.getRowByIndex($self.index)
                row.categoryId = node ? node.categoryId : null
                row.categoryName = node ? node.categoryName : ''
                row.categoryCode = node ? node.categoryCode : ''
              }`)},...editTableFormItemValid},operation:{type:"void","x-visible":expression("!$disabled"),title:"{{$t('common.operation')}}","x-render-table-column":{width:60,fixed:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{disabled:expression("$disabled"),type:"text","@click":expression(`({ row }) => {
                      $table.remove($self.index)
                  }`)}}}}})}}}},companyNatureEngine={companyNature:{type:"void","x-decorator":"FormContainer","x-decorator-props":{class:"companyNature"},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").activeStep==="companyNature"),items:{type:"object",properties:{submit:{type:"void","x-content":i18nExpression("common.nextOne"),"x-component":"Button","x-component-props":{"@click":expression(`async (values) => {
              $form.validate('CompanyInfo.companyNature.formCompanyNature.overseasRelation').then(e => {
                $form.query('state').get('data').overseasRelation = $form.values.overseasRelation
                $form.query('state').get('data').activeStep = 'main'
                initButtonConfig($form)
                setTimeout(() => {
                  $addScrollEvent($form)
                }, 1000)
              })
            }`)}}}},properties:{formCompanyNature:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical",class:"boxs-row"},"x-component":"FormGrid","x-component-props":{maxColumns:1,columnGap:32,rowGap:0},"x-query-engine-skip":!0,properties:{overseasRelation:{"x-hidden":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").activeStep!=="companyNature"),type:"string",default:"INSIDE","x-decorator":"FormItem","x-component":"natureChose","x-component-props":{style:"margin:18px 0 15px 0;","@change":expression(`(who) => {
                setTimeout(() => {
                  if (!$attrs.params.companyId) {
                    $form.values.contactInfos = who !== 'PERSONAL' ? [
                      {
                        position: 'SALES_MANAGER'
                      },
                      {
                        position: 'SENIOR_LEADER'
                      }
                    ] : [{ position: 'SALES_MANAGER' }]
                  }
                })
                $self.value = who
              }`)},title:i18nExpression("vendorMod.overseasRelation"),"x-validator":{required:!0,message:i18nExpression("common.requiredField")}}}}}}},serviceRange={serviceRange:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("cusEntry.vendorMod.serviceRange")},"x-query-engine-skip":!0,properties:{toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void","x-visible":expression("!$disabled"),title:i18nExpression("common.add"),"x-component-props":{type:"primary","@click":expression(`() => {
                $form.query('serviceRangeList').take(field => {
                  field.invoke('add', 'push')
                })
              }`)}}}},serviceRangeList:{type:"array","x-component":"ArrayItems",items:{type:"void",properties:{tableForm:{type:"object",properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:{categoryName:{type:"string","x-decorator":"FormItem","x-component":"QuickSearchWrapper","x-component-props":{showKey:"categoryName",dialogLabel:i18nExpression("cusEntry.vendorMod.categoryNameTitle"),name:"scc_base_purchase_category4","@close-quicksearch":expression(`val => {
                          let list = $form.query('serviceRangeList').get('value')
                          let flag = false
                          for(let item of list){
                            item.tableForm.categoryCode == val.categoryCode && (flag = true)
                          }
                          if(flag){
                            app.$message.error('服务范围内已经存在该品类')
                            $form.query('serviceRangeList').take(field => {
                              field.remove($self.index)
                            })
                            return
                          }
                          let form = $form.query($self.parent.parent.address.toString()).take().value
                          form.categoryCode = val?.categoryCode || ''
                          form.categoryName = val?.categoryName || ''
                          form.categoryId = val?.categoryId || ''
                          const [oneLevel, twoLevel] = val?.categoryFullName?.split('-')
                          form.categoryFullName = oneLevel + '-' + twoLevel || ''
                        }`),disabled:expression("$disabled")},...requiredValidatorSegment,title:"{{t('cusEntry.vendorMod.category', {index: $self.index + 1})}}"},formBtn:{type:"void","x-visible":expression("!$disabled"),"x-component":"ButtonList","x-component-props":{style:{"margin-top":"5px"}},properties:{add:{type:"void","x-component-props":{type:"primary","@click":expression(`() => {
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
                            }`)}}}}}}}},list:{type:"array","x-component":"RenderTable","x-query-engine-skip":!0,"x-component-props":{preColumns:"seq",editMode:expression("!$disabled"),maxHeight:250,pagination:!1,sortable:!1},properties:generateXindexInOrder({performanceAmount:{type:"number",title:i18nExpression("cusEntry.vendorMod.performance"),"x-render-table-column":{minWidth:120}},mainCustom:{type:"string",title:i18nExpression("cusEntry.vendorMod.mainCustomer"),"x-render-table-column":{minWidth:120},"x-validator":{required:!0,message:i18nExpression("cusEntry.tipMessage.required")}},fileId:{type:"string",title:i18nExpression("cusEntry.vendorMod.achievement"),"x-render-table-column":{minWidth:120},"x-component":"SrmCommonFile","x-component-props":{extraData:{fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},defaultFile:{fileId:expression("$table.getRowByIndex($self.index)?.fileId"),fileName:expression("$table.getRowByIndex($self.index)?.fileName")},readonly:!1,"@on-change":expression(`({file}) => {
                      const { fileId = '', fileName = '' } = file || {}
                      let row = $table.getRowByIndex($self.index)
                      row.fileId = fileId
                      row.fileName = fileName
                    }`)},...editTableFormItemValid},operation:{type:"void",title:i18nExpression("common.operation"),"x-render-table-column":{width:60,fiexd:"right"},"x-visible":expression("!$disabled"),"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:i18nExpression("common.delete"),"x-component-props":{type:"text","@click":expression(`() => {
                          const { serciceCustomId = null } = $table.getRowByIndex($self.index) || {}
                          if (serciceCustomId) {
                            let serciceCustomDelList = $form.query('state').get('data').serciceCustomDelList
                            serciceCustomDelList.push({
                              $delete: serciceCustomId
                            })
                          }
                          $table.remove($self.index)
                        }`)}}}}})}}}}}}},qualificationInformation={qualificationInformation:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("cusEntry.vendorMod.qualificationInformation")},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").overseasRelation!=="PERSONAL"),"x-query-engine-skip":!0,properties:{toolbar:{"x-visible":expression("!$disabled"),type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void",title:i18nExpression("common.add"),"x-component-props":{type:"primary","@click":expression(`() => {
                $self.query('qualificationInfo')
                  .take(field => {
                    field.componentProps.componentInstance.addRow('push', {})
                  })
              }`)}}}},qualificationInfo:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:expression("!$disabled"),maxHeight:400,pagination:!1,sortable:!1},"x-query-engine-skip":!0,properties:generateXindexInOrder({authNum:{type:"string","x-component":"DictSelect","x-component-props":{code:"CERTIFICATE_TYPE"},title:i18nExpression("cusEntry.vendorMod.certificateType"),"x-render-table-column":{minWidth:120}},startDate:{type:"date",title:i18nExpression("cusEntry.vendorMod.startTime"),"x-render-table-column":{minWidth:120}},endDate:{type:"date",title:i18nExpression("cusEntry.vendorMod.endTime"),"x-render-table-column":{minWidth:120}},fileuploadId:{type:"string","x-component":"SrmCommonFile","x-component-props":{extraData:{fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},defaultFile:{fileId:expression("$table.getRowByIndex($self.index)?.fileuploadId"),fileName:expression("$table.getRowByIndex($self.index)?.authType")},readonly:!1,"@on-change":expression(`({file}) => {
                const { fileId = '', fileName = '' } = file || {}
                let row = $table.getRowByIndex($self.index)
                row.fileuploadId = fileId
                row.authType = fileName
              }`)},title:i18nExpression("cusEntry.vendorMod.file"),"x-render-table-column":{minWidth:120}},authDescription:{type:"string",title:i18nExpression("cusEntry.vendorMod.remark"),"x-render-table-column":{minWidth:120}},operation:{type:"void","x-visible":expression("!$disabled"),title:i18nExpression("common.operation"),"x-render-table-column":{width:60,fixed:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:i18nExpression("common.delete"),"x-component-props":{type:"text","@click":expression(`() => {
                    $table.remove($self.index)
                  }`)}}}}})}}}},personBaseInfo={person:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("cusEntry.vendorMod.baseInfo")},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").overseasRelation==="PERSONAL"),properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:{personBaseInfo:{type:"object","x-query-engine-skip":!0,properties:{businessLicenseFileId:{type:"string",title:i18nExpression("cusEntry.vendorMod.frontOfIdCard"),"x-decorator":"FormItem","x-component":"SrmCommonFile","x-component-props":{"extra-data":{uploadType:"DEF",sourceType:"WEB_APP",fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},"default-file":{fileId:expression("$form.query('personBaseInfo').get('value')?.businessLicenseFileId"),fileName:expression("$form.query('personBaseInfo').get('value')?.businessLicense")},"@on-change":expression(`({file}) => {
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
                      })
                     }
                  }`),readonly:expression("$form.query('state').get('data').$disabled")},...requiredValidatorSegment},extIdCardOppositeFileName:{type:"string","x-hidden":!0},extIdCardOppositeFileId:{type:"string",title:i18nExpression("cusEntry.vendorMod.backOfIdCard"),"x-decorator":"FormItem","x-component":"SrmCommonFile","x-component-props":{"extra-data":{uploadType:"DEF",sourceType:"WEB_APP",fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},"default-file":{fileId:expression("$form.query('personBaseInfo').get('value')?.extIdCardOppositeFileId"),fileName:expression("$form.query('personBaseInfo').get('value')?.extIdCardOppositeFileName")},"@on-change":expression(`({file}) => {
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
                  }`),readonly:expression("$form.query('state').get('data').$disabled")},...requiredValidatorSegment},companyName:{type:"string","x-decorator":"FormItem",title:i18nExpression("cusEntry.vendorMod.companyNameOrPersonName"),...requiredValidatorSegment,"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},companyShortName:{type:"string","x-decorator":"FormItem",title:i18nExpression("cusEntry.vendorMod.personalAbbreviation"),...requiredValidatorSegment,"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),"show-word-limit":!0,maxlength:100}},businessLicense:{type:"string","x-hidden":!0},idNumber:{type:"string","x-decorator":"FormItem",title:i18nExpression("cusEntry.vendorMod.idNo"),...requiredValidatorSegment,"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},validityPeriodOfCard:{type:"date","x-decorator":"FormItem",title:i18nExpression("cusEntry.vendorMod.validityPeriodOfCard"),"x-component-props":{type:"daterange",disabled:expression("$form.query('state').get('data').$disabled")},...requiredValidatorSegment},extSex:{type:"string","x-decorator":"FormItem",title:i18nExpression("cusEntry.vendorMod.sex"),"x-component":"DictSelect","x-component-props":{code:"GENDER",disabled:expression("$form.query('state').get('data').$disabled")},...requiredValidatorSegment},businessScope:{type:"string",title:i18nExpression("cusEntry.vendorMod.mainBusinessScope"),"x-decorator":"FormItem",...requiredValidatorSegment,"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},companyCountry:{type:"string",title:i18nExpression("components.address.country"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"country",disabled:expression("$form.query('state').get('data').$disabled"),placeholder:expression("$t('common.pleaseSelect')"),"@change":expression(`(val) => {
                    // 选择国外就清理省市区，并且禁用
                    if ($form.query('personBaseInfo.companyCountry').take().value !== 'CN') {
                      $form.query('personBaseInfo.companyProvince').take().value = ''
                      $form.query('personBaseInfo.companyCity').take().value = ''
                    }
                  }`)},...requiredValidatorSegment},companyProvince:{type:"string",title:i18nExpression("components.address.area"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"PROVINCE",disabled:expression("$form.query('state').get('data').$disabled"),"custom-select-type":"PROVINCE",placeholder:expression("$t('common.pleaseSelect')")},...requiredValidatorSegment},companyCity:{type:"string",title:i18nExpression("components.address.city"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:expression("$form.query('personBaseInfo.companyProvince').take()?.value"),"custom-select-type":"CITY",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$form.query('state').get('data').$disabled")},...requiredValidatorSegment},companyAddress:{type:"string",title:i18nExpression("cusEntry.vendorMod.detailAddress"),"x-decorator":"FormItem",...requiredValidatorSegment,"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}}}}}}}}},__$_require_71f3b21f__="/srm/assets/domestic-87aa8782.svg",__$_require_f42902c8__="/srm/assets/abroad-a81170c1.svg",__$_require_9d4a9af0__="/srm/assets/person-1cfed7ec.svg";const _sfc_main$3={name:"NatureChose",components:{},props:{value:{type:String}},data(){return{}},methods:{clickOne(how){this.choseWhat=how,this.$emit("change",how)}}};var _sfc_render$3=function(){var _vm=this,_c=_vm._self._c;return _c("div",{staticClass:"box-warp"},[_c("div",{staticClass:"box-one",class:{clickClass:_vm.value=="INSIDE"},staticStyle:{"margin-left":"0"},on:{click:function($event){return _vm.clickOne("INSIDE")}}},[_c("img",{attrs:{src:__$_require_71f3b21f__,alt:""}}),_vm._m(0)]),_c("div",{staticClass:"box-one",class:{clickClass:_vm.value=="OUT"},on:{click:function($event){return _vm.clickOne("OUT")}}},[_c("img",{attrs:{src:__$_require_f42902c8__,alt:""}}),_vm._m(1)]),_c("div",{staticClass:"box-one",class:{clickClass:_vm.value=="PERSONAL"},on:{click:function($event){return _vm.clickOne("PERSONAL")}}},[_c("img",{attrs:{src:__$_require_9d4a9af0__,alt:""}}),_vm._m(2)])])},_sfc_staticRenderFns$3=[function(){var _vm=this,_c=_vm._self._c;return _c("div",{staticClass:"box-center"},[_c("div",{staticClass:"blueOne"},[_vm._v(" 境内企业 ")]),_c("div",{staticClass:"comments"},[_vm._v(" 境内企业是指在国内指在中国境内依法设立的企业，包括外商投资外商独资企业外商投资企业境内投资。 ")])])},function(){var _vm=this,_c=_vm._self._c;return _c("div",{staticClass:"box-center"},[_c("div",{staticClass:"blueOne"},[_vm._v(" 境外企业 ")]),_c("div",{staticClass:"comments"},[_vm._v(" 境外企业是指在中国境外依法设立的企业，不是依据我国的法律设立的一般都是境外企业。 ")])])},function(){var _vm=this,_c=_vm._self._c;return _c("div",{staticClass:"box-center"},[_c("div",{staticClass:"blueOne"},[_vm._v(" 个人 ")]),_c("div",{staticClass:"comments"},[_vm._v(" 个体户一般指个体工商户。个体工商户是指在法律允许的范围内，依法经核准登记，从事工商经营活动的自然人或家庭。 ")])])}],__component__$3=normalizeComponent(_sfc_main$3,_sfc_render$3,_sfc_staticRenderFns$3,!1,null,"4bc7308e",null,null);const natureChose=__component__$3.exports,_sfc_main$2=defineComponent({__name:"vendorGreenChannelDetailEngine",setup(__props){const{app,emitTabRemove,t,vendor,http}=usePageHelper(),attrs=useAttrs(),workflowStatus=ref$1("DRAFT");let $disabled=["view","approve"].includes(attrs.params.flag||"");const newAddress=defineComponent({name:"newAddress",props:CAddress.props,setup(props,{listeners,attrs:attrs2,slots}){return useAutoMountInstanceToField(),()=>h(CAddress,{props:{...attrs2,...props},on:listeners,ref:"address"},slots)}}),$nodeList=userType=>{const company=[{code:"userInfoForm",name:t("vendorMod.vendorUserInfo"),percentage:0},{code:"companyType",name:t("vendorMod.companyType"),percentage:0},{code:"companyInfo",name:t("vendorMod.enterpriseThreeCertificates"),percentage:0},{code:"companyBaseInfo",name:t("vendorMod.companyBaseInfo"),percentage:0},{code:"contactInfoList",name:t("vendorMod.contactInfo"),percentage:0},{code:"bankInfoList",name:t("vendorMod.bankInfo"),percentage:0},{code:"companySizesList",name:t("vendorMod.companySize"),percentage:0},{code:"cooInfo",name:t("supRisk.cooInfo"),percentage:0},{code:"financeInfo",name:t("cusEntry.vendorMod.financeReport"),percentage:0},{code:"serviceRange",name:t("cusEntry.vendorMod.serviceRange"),percentage:0},{code:"qualificationInformation",name:t("cusEntry.vendorMod.qualificationInformation"),percentage:0},{code:"fileUploadsList",name:t("vendorMod.sceneAttachmentInfo2"),percentage:0}],person=[{code:"userInfoForm",name:t("vendorMod.vendorUserInfo"),percentage:0},{code:"person",name:t("cusEntry.vendorMod.baseInfo"),percentage:0},{code:"contactInfoList",name:t("vendorMod.contactInfo"),percentage:0},{code:"bankInfoList",name:t("vendorMod.bankInfo"),percentage:0},{code:"serviceRange",name:t("cusEntry.vendorMod.serviceRange"),percentage:0},{code:"cooInfo",name:t("supRisk.cooInfo"),percentage:0},{code:"authInfo",name:t("cusEntry.vendorMod.authInfo"),percentage:0}];return userType==="PERSONAL"?person:company},customUpdateButton=computed(()=>!$disabled&&["SUPPLIER_SUBMITTED"].includes(workflowStatus.value)),viewUpdateButton=computed(()=>!$disabled&&!["APPROVED","SUPPLIER_SUBMITTED"].includes(workflowStatus.value)),disabledUpdateButton=computed(()=>["APPROVING"].includes(workflowStatus.value)),query={"*":{},userInfo:{"*":{}},bankInfos:{"*":{}},contactInfos:{"*":{}},orgCategorys:{"*":{}},orgInfos:{"*":{}},operationInfo:{"*":{}},fileUploads:{"*":{}},supplierLeaderList:{"*":{}},siteInfos:{"*":{}},npmCompanySizes:{"*":{}},managementAttaches:{"*":{}},cateJournalList:{"*":{},npmSerciceCustoms:{"*":{}}},npmFinanceReports:{"*":{}}},initButtonConfig=$form=>{setTimeout(()=>{$form.values.overseasRelation!="OUT"?$form.values.bankInfos=[{}]:$form.values.bankInfos=[];let businessType=$form.query("state").get("data").overseasRelation==="PERSONAL"?"supplierGreenChannelPersonal":"supplierGreenChannelCompany";const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.buttonConfigInfo.save.view=viewUpdateButton.value,componentInstance.buttonConfigInfo.submit.view=viewUpdateButton.value,componentInstance.buttonConfigInfo.save.disabled=disabledUpdateButton.value,componentInstance.buttonConfigInfo.submit.disabled=disabledUpdateButton.value,componentInstance.buttonConfigInfo.cancel.view=!1,componentInstance.buttonConfigInfo.close.view=!1,componentInstance.setWorkflowBusinessType(businessType),componentInstance.setWorkflowBusinessId(attrs.params.companyId||""),componentInstance.setWorkflowTabDisabled([null,void 0,"DRAFT","WITHDRAW","REJECTED"].includes(attrs.params.row?.status)&&attrs.params.flag!="approve"),componentInstance.setWorkflowBusinessVariables({})})},updateButtonConfig=$form=>{setTimeout(()=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.buttonConfigInfo.save.view=viewUpdateButton.value,componentInstance.buttonConfigInfo.submit.view=viewUpdateButton.value,componentInstance.buttonConfigInfo.save.disabled=disabledUpdateButton.value,componentInstance.buttonConfigInfo.submit.disabled=disabledUpdateButton.value,componentInstance.buttonConfigInfo.close.view=!1,(attrs.params.row?.status||null)=="SUBMITTED"&&componentInstance.workflowParamsInfo.integrationMode=="Push"&&(componentInstance.buttonConfigInfo.withdraw.view=!0)},50)},$managementChange=(value,name,$form)=>{try{if(value){let data=$form.query(".managementAttaches").take().value;if(value=="Y"){let bold=1;data.forEach(e=>{e.documentInspection==name&&(bold=0)}),bold&&data.unshift({documentInspection:name,managementAttachId:null,managementInfoId:null,companyId:null,fileuploadId:null,authType:"",authDescription:"",authNum:"",authDate:"",authOrg:"",endDate:""})}else data.forEach((e,index2)=>{e.documentInspection==name&&data.splice(index2,1)});$form.query(".managementAttaches").take().value=data}}catch{}},$showSunFile=$self=>{const fileList=[];if($self.value){const fileIdList=$self.value?.split(","),fileNameList=$self.query(".sunshineFileName").get("value")?.split(",");fileIdList.forEach((item,index2)=>{fileList.push({fileId:item,fileName:fileNameList?.[index2]})})}$self.setComponentProps({fileList})},schema=defineSchemas({state:{type:"void","x-component":"Fragment","x-hidden":!0,"x-data":{companyId:attrs.params.companyId||null,$disabled:!1,deleAttr:[],deleFileUploads:[],activeStep:expression("$attrs.params.activeStep"),overseasRelation:"",serciceCustomDelList:[],activeNavIndex:0}},CompanyInfo:{type:"void","x-decorator":"QueryEngine","x-component":"el-container","x-component-props":{class:"flex-container vendorGreen",direction:"vertical"},"x-query-engine":{service:"sup",actions:{greenQuery:{immediate:!0,loading:!0,ready:expression(`() => {
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
            const activeStep = $form.query('state').get('data').activeStep
            if (activeStep === 'main') {
              initButtonConfig($form)
            }
            $form.values.serviceRangeList = [{
              tableForm: {},
              list: [{}]
            }]
            setTimeout(() => {
              $form.values.contactInfos = $form.values.overseasRelation !== 'PERSONAL' ? [
                {
                  position: 'SALES_MANAGER'
                },
                {
                  position: 'SENIOR_LEADER'
                }
              ] : [{ position: 'SALES_MANAGER' }]
            })
            // setTimeout(() => {
            //   $addScrollEvent($form)
            // }, 1000)
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
            if (data.overseasRelation !== 'OUT') {
              data.bankInfos = data.bankInfos.length ? data.bankInfos : [{}]
            }
            $form.setValues(data)
            $form.values.ceeaBusinessModel = data.ceeaBusinessModel ? data.ceeaBusinessModel.split(',') : []
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
            $form.query('state').get('data').overseasRelation = data.overseasRelation
            if (data.overseasRelation === 'PERSONAL') {
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
            } else {
              if (data.businessStartDate) {
                $form.query('.businessDate').take().value = [data.businessStartDate, data.businessEndDate]
              }
              $form.query('companySizes').take().value = data.npmCompanySizes || []
              // console.info($form.query('companySizes').take().value)
              $form.query('qualificationInfo').take().value = data.managementAttaches
            }
            const status = data.status
            if (['APPROVED', 'SUBMITTED', 'ABANDONED'].includes(status)) {
              $form.query('state').get('data').$disabled = true
            }
            if ($disabled) {
              $form.query('state').get('data').$disabled = true
            }
            let deleFileUploads = [] // 附件要删除的列表
            data.fileUploads.forEach(e => {
              deleFileUploads.push({$delete:e.sceneFileId})
            })
            $form.query('state').get('data').deleFileUploads = deleFileUploads
            if ($form.query('state').get('data').activeStep === 'main') {
              setTimeout(() => {
                $addScrollEvent($form)
              }, 1000)
            }
            setTimeout(() => {
              $form.query('fileUploads').take(field => {
                field.visible = true
                field.componentProps.componentInstance.reLoadFileInfo()
              })
            }, 5000)
            return data
          }`)},greenSave:{method:"read",cascadeDeletion:!0,loading:!0},greenSubmit:{method:"read",cascadeDeletion:!0,loading:!0}}},properties:{...companyNatureEngine,SchemaWorkflow:{type:"void","x-component":"SchemaWorkflow","x-component-props":{params:{activeWorkflowTab:expression('$attrs.params.flag != "view"')},"business-id":expression("$attrs.params?.companyId || null"),"@click-handler":expression(`(type) => {
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus, $t)
          }`),"@submit-direct":expression(`(type) => {
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus, $t)
          }`),"@confirm":expression(`(type, comment) => {
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus, $t)
          }`),"@close-tab":expression(`() => {
            $back($bus)
          }`),"@update-integration-mode":expression(`(integrationMode) => {
            updateButtonConfig($form)
          }`)},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").activeStep==="main"),items:{type:"object","x-query-engine-skip":!0,properties:{preStep:{type:"void","x-visible":expression("!$disabled"),"x-content":i18nExpression("common.prevOne"),"x-component":"Button","x-component-props":{type:"primary",style:{"margin-right":"10px"},"@click":expression(`() => {
                  $form.query('state').get('data').activeStep = 'companyNature'
                  
                  app.$nextTick(() => {
                    console.log($form.query('state').get('data').overseasRelation)
                  })
                  setTimeout(() => {
                    $form.values.overseasRelation = $form.query('state').get('data').overseasRelation
                    console.log($form.values.overseasRelation)
                  })
                }`)}}}},properties:{layout:{type:"void","x-component":"FormContainer",properties:{...Steps,collapse:{type:"void","x-component":"Collapse","x-component-props":{defaultOpenPanelCount:1},properties:{...userInfoForm,...companyType,...companyInfo,...personBaseInfo,...companyBaseInfo,...contactInfoList,...bankInfoList,...companySizesList,...financeInfoList,...serviceRange,...cooInfoList,...qualificationInformation,fileUploadsList:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.sceneAttachmentInfo2")},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").overseasRelation!=="PERSONAL"),"x-query-engine-skip":!0,properties:{fileUploads:{"x-query-engine-relation":"fileUploads:*",type:"array","x-component":"FileDynamic","x-component-props":{"scene-module-code":"SCENE_SUPPLIER_ATTACHMENT",businessId:expression("$attrs.params.companyId || null"),editable:expression("!$disabled"),"need-init":!1}}}}}}}},CFillProgress:{type:"void","x-component":"CFillProgress","x-component-props":{activeNavIndex:expression("$form.query('state').get('data').activeNavIndex"),class:"contract-progress",ref:"contractProgress",nodeName:"$t('logisticsMod.contractInfo')",data:expression("$nodeList($form.query('state').get('data').overseasRelation)"),percentage:"{{true}}","@index-click":`{{ (code) => {
              let anchorEle = document.querySelector('#collapse_' + code)
              if (anchorEle) {
                anchorEle.scrollIntoView(true)
              }
           } }}`}}}}}}}),$back=$bus=>{emitTabRemove(attrs.tabName),$bus.$emit("green")},$saveBill=async(type,$form,$queryEngine,$confirm,$message,$bus,$t)=>{if(type=="WITHDRAW"){emitTabRemove(attrs.tabName),$bus.$emit("green");return}let{personBaseInfo:personBaseInfo2={},...values}=JSON.parse(JSON.stringify($form.values));if(personBaseInfo2.validityPeriodOfCard){const[businessStartDate,businessEndDate]=personBaseInfo2.validityPeriodOfCard;personBaseInfo2.businessStartDate=businessStartDate,personBaseInfo2.businessEndDate=businessEndDate}const businessDate=$form.query(".businessDate").take()?.value;businessDate&&businessDate?.length>0&&(values.businessStartDate=businessDate[0],values.businessEndDate=businessDate[1]);const serciceCustomDelList=$form.query("state").get("data").serciceCustomDelList||[];let serviceRange2=$form.query("serviceRangeList").get("value").map(item=>{const{list,tableForm}=item;return{...tableForm,npmSerciceCustoms:[...list,...serciceCustomDelList]}});values.cateJournalList=serviceRange2,values.firstLoginFlag="N",values.greenChannelReason=values.userInfo.greenChannelReason;let overseasRelation=$form.query("state").get("data").overseasRelation;if(overseasRelation!=="PERSONAL"&&(values.npmCompanySizes=$form.query("companySizes").get("value"),values.managementAttaches=$form.query("qualificationInfo").get("value"),values.fileUploads=$form.query("fileUploads").take().value),type!="SAVE"){let validate=0,validContact=!0,orgBol=!1,companyNameFlag=!1,ceeaEnabled="",positionList=[];await $form.validate().then().catch(eq=>{app.$message.error(eq[0].messages[0]),validate=1}),overseasRelation=="INSIDE"&&(values.contactInfos.some(item=>{if(item.ceeaDefaultContact=="Y"&&!item.socialSecurityCertificateFileId)return validContact=!1,!0}),positionList=values.contactInfos.filter(item=>!!item.position).map(itm=>itm.position)),attrs.params?.companyId;const orgCategorys=$form.query("orgCategorys").take().value;if(orgCategorys.length>0&&orgCategorys.forEach(data=>{(["",null].includes(data.orgId)||["",null].includes(data.categoryId))&&(orgBol=!0)}),overseasRelation!=="OUT"){const{companyName,bankInfos}={...values,...personBaseInfo2},mainAccountRow=bankInfos.find(item=>item.ceeaMainAccount==="Y"),mainAccountBankAccountName=mainAccountRow?.bankAccountName;companyNameFlag=companyName!==mainAccountBankAccountName,ceeaEnabled=mainAccountRow?.ceeaEnabled}let validFlag=!0;serviceRange2.some(item=>{if(!item.categoryId)return validFlag=!1,!0});let str="";if(overseasRelation=="INSIDE"&&!(positionList.includes("SALES_MANAGER")&&positionList.includes("SENIOR_LEADER"))&&(str+=$t("cusEntry.tipMessage.atLeastManageAndLeader")+`
`),(validate||!validContact||orgBol||companyNameFlag||ceeaEnabled!=="Y"||serviceRange2.length===0||!validFlag)&&(overseasRelation=="INSIDE"&&!validContact&&(str=$t("cusEntry.tipMessage.socialSecurityCertificateMsg")+`
`),orgBol&&(str+=$t("vendorMod.msgOrgCatTableInfo")+`
`),overseasRelation!=="OUT"&&companyNameFlag&&(str+=$t("cusEntry.tipMessage.companyAndBankAccount")+`
`),overseasRelation!=="OUT"&&ceeaEnabled!=="Y"&&(str+=$t("cusEntry.tipMessage.ceeaEnabled")+`
`),serviceRange2.length===0&&(str+=$t("cusEntry.tipMessage.atLeastCategory")+`
`),!validFlag&&(str+=$t("cusEntry.tipMessage.serviceRangeCategoryRequired")+`
`)),str.length)return $message.error(str),!1;const{lcCode,idNumber}={...values,...personBaseInfo2},res=await integritySystem(lcCode||idNumber);if(res.data===integritySystemResultMap.get("forbid")){$message.warning($t("cusEntry.tipMessage.blackForbid"));return}else res.data===integritySystemResultMap.get("focus")&&(values.focusFlag="Y",values.npmCompanyExceptionInfos.push({exceptionType:"FOCUS_FLAG"}))}values.ceeaBusinessModel=values.ceeaBusinessModel?.length?values.ceeaBusinessModel.join():null,type=="SAVE"?[null,void 0,"DRAFT"].includes(status)?(values.status="DRAFT",$queryEngine.request.save({...values,...personBaseInfo2},{query:{"*":{}},action:"greenSave"}).then(()=>{$message.success($t("common.successSave")),$bus.$emit("green"),emitTabRemove(attrs.tabName)}).catch(err=>{$form.values.ceeaBusinessModel=values.ceeaBusinessModel?values.ceeaBusinessModel.split(","):[]})):$queryEngine.request.save({...values,...personBaseInfo2},{query:{"*":{}},action:"greenSave"}).then(()=>{$message.success($t("common.successSave")),$bus.$emit("green"),emitTabRemove(attrs.tabName)}).catch(err=>{$form.values.ceeaBusinessModel=values.ceeaBusinessModel?values.ceeaBusinessModel.split(","):[]}):$queryEngine.request.save({...values,...personBaseInfo2},{query:{"*":{}},action:"greenSubmit"}).then(res=>{const businessType=$form.query("state").get("data").overseasRelation==="PERSONAL"?"supplierGreenChannelPersonal":"supplierGreenChannelCompany";$form.query("SchemaWorkflow").take(field=>{field.componentProps.componentInstance.workflowParamsInfo.businessType=businessType});const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.setWorkflowBusinessId(res.data[0]?.companyId||null),componentInstance.setWorkflowTabDisabled(!1),componentInstance.setWorkflowBusinessVariables({}),componentInstance.handlerAfter(type.toUpperCase(),()=>{$bus.$emit("green"),emitTabRemove(attrs.tabName)}),setTimeout(()=>{$form.readPretty=!0,$form.query("state").get("data").$disabled=!0,componentInstance.buttonConfigInfo.save.view=!1,componentInstance.buttonConfigInfo.submit.view=!1},100)}).catch(err=>{$form.values.ceeaBusinessModel=values.ceeaBusinessModel?values.ceeaBusinessModel.split(","):[]})},integritySystem=lcCode=>http({url:"/api-sup/pj/companyInfo/queryIfBlackCompany",method:"POST",data:{lcCode}}),integritySystemResultMap=new Map([["forbid","禁止合作"],["focus","重点关注"]]),$addScrollEvent=$form=>{const navNodes=$nodeList($form.query("state").get("data").overseasRelation),offsetTopArr=[];navNodes.forEach(node=>{const element=document.getElementById(`collapse_${node.code}`);offsetTopArr.push(element.offsetTop)}),window.addEventListener("scroll",$throttle($scrollHandler,100,$form,offsetTopArr),!0)},$throttle=(fn,delay,$form,offsetTopArr)=>{let timer=null;return()=>{timer||setTimeout(()=>{fn($form,offsetTopArr),clearTimeout(timer),timer=null},delay)}},$scrollHandler=($form,offsetTopArr)=>{const scrollTop=document.getElementsByClassName("el-tabs__content")[2]?.scrollTop;let navIndex=0;offsetTopArr.findIndex(item=>item>=scrollTop);for(let n=0;n<offsetTopArr.length;n++)scrollTop>=offsetTopArr[n]&&(navIndex=n);$form.query("state").get("data")&&($form.query("state").get("data").activeNavIndex=navIndex)};return{__sfc:!0,app,emitTabRemove,t,vendor,http,attrs,workflowStatus,$disabled,newAddress,$nodeList,customUpdateButton,viewUpdateButton,disabledUpdateButton,query,initButtonConfig,updateButtonConfig,$managementChange,$showSunFile,schema,$back,$saveBill,integritySystem,integritySystemResultMap,$addScrollEvent,$throttle,$scrollHandler,scope:{app,t,$attrs:attrs,updateButtonConfig,performPlanService,$disabled,emitTabRemove,initButtonConfig,$saveBill,$back,DictSelect,observer,$managementChange,query,validEmail,validatePhone,$nodeList,sceneFileApi,integritySystem,integritySystemResultMap,$showSunFile,$addScrollEvent,$throttle,$scrollHandler},components:{SrmCommonFile:CommonFile,CAddress,CCategorySelect,FileDynamic,CFillProgress,newAddress,natureChose},RenderEngine}}});var _sfc_render$2=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{schemaKey:"vendorGreenChannelDetail",pageAttrs:_vm.$attrs,schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns$2=[],__component__$2=normalizeComponent(_sfc_main$2,_sfc_render$2,_sfc_staticRenderFns$2,!1,null,null,null,null);const vendorGreenChannelDetail=__component__$2.exports,_sfc_main$1=defineComponent({__name:"vendorGreenChannelListEngine",setup(__props){const{emitTabAdd,app}=usePageHelper(),schema=defineSchemas({CompanyInfo:{type:"void","x-decorator":"QueryEngine","x-query-engine":{service:"sup",actions:{greenQuery:{immediate:!0,method:"paginationQuery",transformRequest:expression(`(data, headers) => {
              // if (data.payload.filter) {
              //   data.payload.filter['dataSources'] = {
              //     eq: 'MANUALLY_CREATE'
              //   }
              // } else {
              //    data.payload.filter['dataSources'] = {
              //     eq: 'MANUALLY_CREATE'
              //   }
              //   data.payload = {
              //     "filter": {
              //         "dataSources": {
              //             eq: 'MANUALLY_CREATE'
              //         }
              //     }
              //   }
              // }
              return data
            }`)}}},"x-component":"el-container","x-component-props":{class:"flex-container the_contractTemplateList_wrapper",direction:"vertical"},properties:{bus:{type:"void","x-component":"BusEvent","x-component-props":{eventName:"green","@listener":expression(`() => {
            $queryEngine.state.paginationManagement.refresh()
          }`)}},query:{type:"object","x-query-engine-skip":!0,"x-component":"QueryFormByQueryEngine",properties:generateXindexInOrder({companyName:{type:"string",title:"{{$t('common.vendorName')}}","x-component":"QuickSearchWrapper","x-component-props":{showKey:"companyName",propKey:"companyName",name:"scc_sup_company_info_display_buyer"}},lcCode:{type:"string",title:"{{$t('vendorMod.lcCode')}}","x-query-engine-query-operator":"contains"},isBacklist:{type:"string",title:"{{$t('vendorMod.isBacklist')}}","x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"}},overseasRelation:{type:"string",title:"{{$t('vendorMod.overseasRelation')}}","x-component":"DictSelect","x-component-props":{code:"RELATION"}},companyType:{type:"string",title:"{{$t('vendorMod.companyType')}}","x-component":"DictSelect","x-component-props":{code:"COMPANY_NATURE"}},status:{type:"string",title:"{{$t('vendorMod.approveStatus')}}","x-component":"DictSelect","x-component-props":{code:"APPROVE_STATUS_TYPE"}},legalPerson:{type:"string",title:"{{$t('vendorMod.legalPerson')}}","x-query-engine-query-operator":"contains"},approvedDate:{title:"{{$t('vendorMod.permitDate')}}",...dataTimeSelectorSegment,"x-query-engine-query-operator":"between"}})},toolbar:{type:"void","x-component":"Space","x-component-props":{style:"margin-bottom: 16px"},properties:{add:{type:"void",title:"{{$t('common.add')}}","x-component":"RButton","x-component-props":{type:"primary","@click":expression(`() => {
               tab = {
                  component: vendorGreenChannelDetail,
                  params: {
                    flag: 'add',
                    tabName: 'vendorGreenChannelDetail',
                    activeStep: 'companyNature'
                  },
                  title: $t('vendorMod.addVendor'),
                  name: 'vendorGreenChannelDetail'
                }
               emitTabAdd(tab)
              }`)}},importPersonVendor:{type:"void","x-component":"ImportExcel","x-component-props":{title:i18nExpression("cusEntry.vendorMod.importPersonVendor"),type:"default",extraData:{fileModular:"sup",fileFunction:"companyInfoImportExcel",fileType:"excel"},upLoadUrl:"/api-sup/pj/info/companyInfo/person/importExcel",downloadTemplateOptions:{downloadUrl:"/api-sup/pj/info/companyInfo/person/importExcelTemplate",fileName:expression("$t('vendorMod.vendorImportTemplateXLXS')")},"@handleSuccess":expression(`() => {
                $queryEngine.state.paginationManagement.refresh()
              }`)},"x-visible":expression("$authorityVisible('sup:greenChannel:importPerson')")},importCompanyVendor:{type:"void","x-component":"ImportExcel","x-component-props":{title:i18nExpression("cusEntry.vendorMod.importCompanyVendor"),type:"default",extraData:{fileModular:"sup",fileFunction:"companyInfoImportExcel",fileType:"excel"},upLoadUrl:"/api-sup/pj/companyInfo/importExcel",downloadTemplateOptions:{downloadUrl:"/api-sup/pj/companyInfo/importExcelTemplate",fileName:expression("$t('vendorMod.vendorImportTemplateXLXS')")},"@handleSuccess":expression(`() => {
                $queryEngine.state.paginationManagement.refresh()
              }`)},"x-visible":expression("$authorityVisible('sup:greenChannel:importCompany')")}}},table:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",class:"table-view-vxe-table",openCustomTable:!0},properties:generateXindexInOrder({companyId:{type:"string","x-hidden":!0},companyCode:{type:"string",title:"{{$t('common.vendorCode')}}","x-render-table-column":{width:120}},companyName:{"x-component":"TableButton","x-component-props":{type:"text","@click":expression(`({row}) => {
                let companyId = row.companyId
                let tab = {
                  component: vendorGreenChannelDetail,
                  params: {
                    flag: 'view',
                    companyId: companyId,
                    tabName: 'vendorGreenChannelDetail' + row.companyName,
                    row,
                    activeStep: 'main'
                  },
                  title: row.companyName,
                  name: 'vendorGreenChannelDetail' + row.companyName
                }
                emitTabAdd(tab)
              }`)},"x-render-table-column":{title:i18nExpression("common.vendorName"),minWidth:150,customRender:!0}},overseasRelation:{type:"string",title:"{{$t('vendorMod.overseasRelation')}}","x-component":"DictSelect","x-component-props":{code:"RELATION_NEW"},"x-render-table-column":{width:150}},companyType:{type:"string",title:"{{$t('vendorMod.companyType')}}","x-component":"DictSelect","x-component-props":{code:"COMPANY_NATURE"},"x-render-table-column":{width:100}},lcCode:{type:"string",title:"{{$t('vendorMod.lcCode')}}","x-render-table-column":{width:150}},legalPerson:{type:"string",title:"{{$t('vendorMod.legalPerson')}}","x-render-table-column":{width:150}},isBacklist:{type:"string",title:"{{$t('vendorMod.isBacklist')}}","x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"},"x-render-table-column":{width:120}},status:{type:"string",title:"{{$t('vendorMod.approveStatus')}}","x-component":"DictSelect","x-component-props":{code:"APPROVE_STATUS_TYPE"},"x-render-table-column":{width:100}},approvedDate:{title:"{{$t('vendorMod.permitDate')}}",...yearMonthDaySelectorSegment,"x-render-table-column":{width:130}},lastUpdateDate:{type:"string","x-hidden":!0,"x-query-engine-sort":"desc"},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList","x-component-props":{max:2},properties:{edit:{type:"void",title:"{{$t('common.edit')}}","x-reactions":changeFieldVisibleByDeps([".status"],"['DRAFT', 'WITHDRAW', 'REJECTED'].includes($deps[0])"),"x-component-props":{type:"text","@click":expression(`({ row }) => {
                    let companyId = row.companyId
                    let tab = {
                      component: vendorGreenChannelDetail,
                      params: {
                        flag: 'edit',
                        companyId: companyId,
                        tabName: 'vendorGreenChannelDetail' + row.companyName,
                        row,
                        activeStep: 'main'
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
                  }`)}}}}})}}}}),$recallFlow=(app2,data)=>app2.$http({url:"/api-pj/external/bpm/public/flow/native/rollBackProcess",method:"POST",data,loading:!0});return{__sfc:!0,emitTabAdd,app,schema,$recallFlow,scope:{emitTabAdd,app,i18nExpression,vendorGreenChannelDetail,$recallFlow},components:{},RenderEngine}}});var _sfc_render$1=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{staticClass:"contractPaymentType",attrs:{schemaKey:"vendorGreenChannelList",schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns$1=[],__component__$1=normalizeComponent(_sfc_main$1,_sfc_render$1,_sfc_staticRenderFns$1,!1,null,null,null,null);const vendorGreenChannelListEngine=__component__$1.exports,_sfc_main={name:"VendorGreenChannel",components:{NavTabs},data(){return{activeTab:"vendorGreenChannelListEngine",tabs:[{title:()=>this.$t("vendorMod.vendorGreenChannel"),name:"vendorGreenChannelListEngine",component:vendorGreenChannelListEngine,closable:!1}]}},mounted(){this.$route.params.openSavePage&&this.openSavePage()},methods:{openSavePage(){}}};var _sfc_render=function(){var _vm=this,_c=_vm._self._c;return _c("NavTabs",{ref:"tabs",attrs:{"tabs-list":_vm.tabs,"cur-tab":_vm.activeTab}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const index=__component__.exports;export{index as default};
