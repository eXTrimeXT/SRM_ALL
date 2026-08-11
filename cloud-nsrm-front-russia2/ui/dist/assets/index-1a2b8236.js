import{N as NavTabs}from"./index-9a7f2446.js";import{ad as expression,ae as i18nExpression,aC as generateCharExpressionByFunction,af as yearMonthDaySelectorSegment,ag as radioGroupByYOrNSegment,aD as requiredValidatorSegment,ah as generateXindexInOrder,ai as editTableFormItemValid,aj as feedbackLayoutIsPopover,ak as defineComponent,al as usePageHelper,am as useAttrs,an as ref$1,ao as useAutoMountInstanceToField,ap as h,bY as computed,aq as defineSchemas,as as performPlanService,at as DictSelect,au as observer,v as validEmail,a as validatePhone,ac as createDictClass,ar as RenderEngine,aE as DictClass,av as CommonFile,a6 as CCategorySelect,n as normalizeComponent,b$ as dataTimeSelectorSegment,bD as changeFieldVisibleByDeps}from"./index-6b6051d8.js";import{C as CAddress}from"./index-baa5f2f5.js";import{F as FileDynamic}from"./file-dynamic-25a093c4.js";import{C as CFillProgress}from"./index-2c71d18e.js";import{n as natureChose}from"./natureChose-d499cf51.js";import{s as sceneFileApi}from"./basicSetting-fc46a2d9.js";/* empty css                                                              */import"./file-dynamic-7fc2d358.js";import"./MainHeader-2d842985.js";/* empty css                                                                   */import"./file-dynamic.vue_vue_type_style_index_0_scoped_516fdfc3_lang-f2c1a82d.js";import"./tree-utils-7df6be59.js";import"./BaseTableBind-b1f76fc9.js";import"./util-1e55288f.js";import"./index-4d512f00.js";/* empty css                                                                      */import"./fileApi-05bbbbcc.js";/* empty css                                              */const Steps={steps:{type:"void","x-decorator":"div","x-decorator-props":{class:"stepDiv"},"x-component":"Steps","x-component-props":{alignCenter:!0,finishStatus:"success"},"x-reactions":{dependencies:["status"],fulfill:{state:{"component[1].active":expression(`
            ['DRAFT', '', null, undefined,'WITHDRAW','REJECTED'].includes($deps[0])
            ? 0
            : ['SUBMITTED'].includes($deps[0])
            ? 1
            : ['APPROVED'].includes($deps[0])
            ? 3
            : 2
          `)}}},properties:{step1:{type:"void","x-component":"el-step","x-component-props":{title:expression("$t('vendorMod.finishCompanyInfo')")}},step2:{type:"void","x-component":"el-step","x-component-props":{title:expression("$t('common.successSubmit')")}},step3:{type:"void","x-component":"el-step","x-component-props":{title:expression("$t('vendorMod.approvalSuccess')")}}}}},userInfoForm={userInfoForm:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.vendorUserInfo")},"x-query-engine-skip":!0,properties:{userInfo:{type:"object","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},"x-query-engine-skip":!0,properties:{username:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('common.vendorName') + '（' + $t('vendorMod.userName') + '）'"),"x-component-props":{disabled:expression("$disabled")},"x-validator":{required:!0,message:i18nExpression("vendorMod.enterUserName")}},phone:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.contactPhone')"),"x-component-props":{disabled:expression("$disabled")},"x-validator":{required:!0,message:i18nExpression("dataConfMod.msgContactPhone"),validator:expression(`(value, rule) => {
                if(value && !validatePhone(value)){
                  return $t('vendorMod.correctPhoneNumber')
                }
              }`)}},email:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('common.email')"),"x-component-props":{disabled:expression("$disabled")},"x-validator":{required:!0,message:i18nExpression("vendorMod.pleaseInputEmail"),validator:expression(`(value, rule) => {
                if(value && !validEmail(value)){
                  return $t('vendorMod.correctEmail')
                }
              }`)}},lcCode:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.lcCode')"),"x-component-props":{disabled:expression("$disabled"),"@blur":expression(`(value) => {
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
              }`)},"x-validator":{required:!0,message:i18nExpression("vendorMod.msgLcCode")}},accountGroup:{type:"string",default:"Z001","x-decorator":"FormItem","x-component":"Input",title:expression("$t('cusEntry.vendorMod.accountGroup')"),"x-component-props":{disabled:!0}},greenChannelReason:{type:"string","x-decorator":"FormItem",title:expression("$t('vendorMod.greenChannelReason')"),"x-component-props":{disabled:expression("$disabled"),type:"textarea"},"x-decorator-props":{gridSpan:3}}}}}}},companyType={companyType:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.companyType")},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").overseasRelation!=="PERSONAL"&&!$form.query("state").get("data").isSimple),"x-query-engine-skip":!0,properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:{status:{type:"string",default:"DRAFT","x-hidden":!0},domesticAndForeignRelations:{type:"string","x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"RELATION_NEW",disabled:expression("$form.query('state').get('data').$disabled")},title:expression("$t('cusEntry.vendorMod.domesticAndForeignRelations')"),"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},companyType:{type:"string","x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"COMPANY_NATURE_NEW",disabled:expression("$form.query('state').get('data').$disabled")},title:expression("$t('cusEntry.vendorMod.vendorType')"),"x-validator":{required:!0,message:i18nExpression("common.requiredField")}}}}}}},companyInfo={companyInfo:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.enterpriseThreeCertificates")},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").overseasRelation!=="PERSONAL"&&!$form.query("state").get("data").isSimple),"x-query-engine-skip":!0,properties:{div:{type:"void","x-component":"div","x-component-props":{class:"companyInfo"},properties:{businessLicense:{type:"string","x-hidden":!0},businessLicenseFileId:{type:"string","x-component":"SrmCommonFile","x-component-props":{readonly:expression("$disabled"),"list-type":"picture-card",style:{width:"33%","padding-right":"25px"},defaultFile:{fileId:expression("$self.value"),fileName:expression("$form.query('businessLicense').get('value')")},"dragger-options":{width:"100%",height:"345px"},limit:1,drag:"drag","@on-change":expression(`({ file }) => {
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
                  //   })
                  //   .catch(err => {
                  //     console.log(err)
                  //   })
                  // }
                  $form.query('.businessLicenseFileId').take().value = fileId.toString()
                  $form.query('.businessLicense').take().value = fileName
          }`)},title:""},layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical",style:{width:"67%","padding-left":"20px"}},"x-component":"FormGrid","x-component-props":{maxColumns:2,columnGap:32,rowGap:0},properties:{companyName:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.companyName')"),"x-component-props":{disabled:expression("$disabled")},"x-validator":{required:!0,message:i18nExpression("vendorMod.msgCompanyName")}},companyShortName:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.companyShortName')"),"x-component-props":{disabled:expression("$disabled"),maxlength:100,"show-word-limit":!0}},companyEnName:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('cusEntry.vendorMod.companyEnName')"),"x-component-props":{disabled:expression("$disabled")}},enterpriseNo:{type:"string",title:i18nExpression("cusEntry.vendorMod.enterpriseNo"),"x-decorator":"FormItem","x-component-props":{disabled:expression("$disabled")},"x-validator":{required:!0,message:i18nExpression("vendorMod.msgLegalPerson")}},legalPerson:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.legalPerson')"),"x-component-props":{disabled:expression("$disabled")},"x-validator":{required:!0,message:i18nExpression("vendorMod.msgLegalPerson")}},nickname:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('dataConfMod.userName')"),"x-component-props":{disabled:expression("$disabled")},"x-validator":{required:!0,message:i18nExpression("vendorMod.msgInputNickname")}},position:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('components.orgPositionSel.position')"),"x-component-props":{disabled:expression("$disabled")}},registCurrency:{type:"string","x-hidden":!0},registeredCapital:{type:"number","x-visible":expression("$form.query('.companyType').take().value != 'GETI'"),"x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.registeredCapital')"),"x-component-props":{disabled:expression("$disabled"),class:"input-with-select","@change":expression(`(value) => {
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
                  `)},"x-validator":{required:expression("!['GETI','FEIYINGLI'].includes($form.query('.companyType').take().value)"),message:i18nExpression("vendorMod.msgRegisteredCapital")}},ifLongPeriod:{type:"string",title:i18nExpression("cusEntry.vendorMod.ifLongTermSupplier"),"x-component":"DictSelect","x-component-props":{code:"YES_OR_NO",disabled:expression("$disabled")},default:"Y","x-decorator":"FormItem","x-validator":{required:!0,message:i18nExpression("cusEntry.tipMessage.ifLongPeriodMsg")}},businessEndDate:{type:"date","x-hidden":!0},businessStartDate:{...yearMonthDaySelectorSegment,"x-decorator":"FormItem","x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],disabled:expression("$disabled")},title:expression("$t('vendorMod.dateBusiness')"),"x-validator":{required:expression("$form.query('ifLongPeriod').take().value === 'N'"),message:i18nExpression("vendorMod.msgCreationDate")}},companyCreationDate:{...yearMonthDaySelectorSegment,"x-decorator":"FormItem","x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],disabled:expression("$disabled")},title:expression("$t('vendorMod.creationDate')"),"x-validator":{required:expression("!['FEIYINGLI'].includes($form.query('.companyType').take().value)"),message:i18nExpression("vendorMod.msgCreationDate")}},businessScope:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.businessScope')"),"x-component-props":{disabled:expression("$disabled"),type:"textarea",maxlength:2e3},"x-decorator-props":{gridSpan:3}}}}}}}}},companyBaseInfo={companyBaseInfo:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.companyBaseInfo")},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").overseasRelation!=="PERSONAL"&&!$form.query("state").get("data").isSimple),"x-query-engine-skip":!0,properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical",feedbackLayout:"terse"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:{ceeaBusinessModel:{type:"string","x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{disabled:expression("$disabled"),code:"BIZ_MODEL",multiple:!0},title:expression("$t('vendorMod.bizModel')")},ceeaIfListed:{title:i18nExpression("vendorMod.ifListed"),...radioGroupByYOrNSegment,"x-component-props":{disabled:expression("$disabled")}},ceeaListedTime:{...yearMonthDaySelectorSegment,"x-visible":"{{$form.query('.ceeaIfListed').take().value == 'Y'}}","x-decorator":"FormItem","x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],disabled:expression("$disabled")},title:expression("$t('vendorMod.listedDate')"),"x-validator":{required:!0,message:i18nExpression("common.marketTime")}},categoryName:{type:"string","x-hidden":!0},cateJournalList:{type:"Array","x-hidden":!0},companyCountry:{type:"string",title:i18nExpression("components.address.country"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"country",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$disabled"),"@change":expression(`(val) => {
                let row = $form.values
                row.companyProvince = null
                row.companyCity = null
              }`)},"x-reactions":expression(`() => {
              const data = $taxDictClass.getDictDetail('country', $form.values.companyCountry)
              $form.query('.companyProvince').take()?.setComponentProps({ code: data ? data.description : undefined })
            }`),...requiredValidatorSegment},companyProvince:{type:"string",title:i18nExpression("components.address.area"),"x-reactions":expression(`() => {
              $self.visible = ['CN', 'RU'].includes($form.values.companyCountry)
            }`),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{"custom-select-type":"PROVINCE",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$disabled"),"@change":expression(`(val) => {
                $form.values.companyCity = null
              }`)},...requiredValidatorSegment},companyCity:{type:"string",title:i18nExpression("components.address.city"),"x-reactions":expression(`() => {
              $self.visible = ['CN', 'RU'].includes($form.values.companyCountry)
            }`),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:expression("$form.values.companyProvince || ''"),"custom-select-type":"CITY",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$disabled || !$form.values.companyProvince")},...requiredValidatorSegment},companyAddress:{type:"string","x-decorator":"FormItem","x-component":"Input",title:i18nExpression("cusEntry.vendorMod.detailAddress"),"x-component-props":{disabled:expression("$disabled")},"x-validator":{required:!0,message:i18nExpression("vendorMod.msgDetailAddr")}},ceeaHasParentCompany:{title:i18nExpression("cusEntry.vendorMod.ifParentCompany"),...radioGroupByYOrNSegment,"x-component-props":{disabled:expression("$disabled")}},ceeaParentCompanyName:{type:"string","x-decorator":"FormItem","x-component":"Input","x-visible":"{{$form.query('.ceeaHasParentCompany').take().value == 'Y'}}",title:i18nExpression("cusEntry.vendorMod.parentCompanyName"),"x-component-props":{disabled:expression("$disabled")},"x-validator":{required:!0,message:i18nExpression("cusEntry.vendorMod.parentCompanyNameEnterTips")}},groupCountry:{type:"string",title:i18nExpression("cusEntry.vendorMod.parentCompanyCountry"),"x-visible":"{{$form.query('.ceeaHasParentCompany').take().value == 'Y'}}","x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"country",disabled:expression("$disabled"),placeholder:expression("$t('common.pleaseSelect')")},"x-validator":{required:!0,message:i18nExpression("cusEntry.tipMessage.parentCompanyCountryMsg")}},ceeaCompanyIntro:{type:"string","x-decorator":"FormItem","x-component":"Input.TextArea",title:expression("$t('vendorMod.companyProfile')"),"x-component-props":{disabled:expression("$disabled"),maxlength:2e3},"x-decorator-props":{gridSpan:3}}}}}}},contactInfoList={contactInfoList:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.contactInfo")},"x-visible":generateCharExpressionByFunction(({$form})=>!$form.query("state").get("data").isSimple),"x-query-engine-skip":!0,properties:{toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},"x-visible":expression("!$form.query('state').get('data').$disabled"),properties:{add:{type:"void",title:i18nExpression("common.new"),"x-component-props":{type:"primary","@click":expression(`() => {
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
                  }`)}}}}})}}}},companyNatureEngine={companyNature:{type:"void","x-decorator":"FormContainer","x-decorator-props":{class:"companyNature"},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").activeStep==="companyNature"),items:{type:"object",properties:{submit:{type:"void","x-content":i18nExpression("common.nextOne"),"x-component":"Button","x-component-props":{"@click":expression(`async (values) => {
              $form.validate('CompanyInfo.companyNature.formCompanyNature.overseasRelation').then(e => {
                $form.query('state').get('data').overseasRelation = $form.values.overseasRelation
                $form.query('state').get('data').activeStep = 'main'
                initButtonConfig($form)
                $initQualificationInfo($form)
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
              }`)},title:i18nExpression("vendorMod.overseasRelation"),"x-validator":{required:!0,message:i18nExpression("common.requiredField")}}}}}}},serviceRange={serviceRange:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("cusEntry.vendorMod.serviceRange")},"x-visible":generateCharExpressionByFunction(({$form})=>!$form.query("state").get("data").isSimple),"x-query-engine-skip":!0,properties:{toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},"x-visible":expression("!$form.query('state').get('data').$disabled"),properties:{add:{type:"void",title:i18nExpression("common.add"),"x-component-props":{type:"primary","@click":expression(`() => {
                $form.query('serviceRangeList').take(field => {
                  field.invoke('add', 'push')
                })
              }`)}}}},serviceRangeList:{type:"array","x-component":"ArrayItems",items:{type:"void",properties:{tableForm:{type:"object",properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:{categoryName:{type:"string","x-decorator":"FormItem","x-component":"QuickSearchWrapper","x-component-props":{dialogLabel:i18nExpression("cusEntry.vendorMod.categoryNameTitle"),showKey:"categoryName",name:"scc_base_purchase_category4","@close-quicksearch":expression(`val => {
                          let list = $form.query('serviceRangeList').get('value')
                          let flag = false
                          for(let item of list){
                            item.tableForm.categoryCode == val.categoryCode && (flag = true)
                          }
                          if(flag){
                            // 服务范围内已经存在该品类
                            app.$message.error($t('cusEntry.vendorMod.serviceRangeCateTips'))
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
                            }`)}}}}}}}},list:{type:"array","x-component":"RenderTable","x-query-engine-skip":!0,"x-component-props":{preColumns:"seq",editMode:expression("!$form.query('state').get('data').$disabled"),maxHeight:250,pagination:!1,sortable:!1},properties:generateXindexInOrder({performanceAmount:{type:"number",title:i18nExpression("cusEntry.vendorMod.performance"),"x-render-table-column":{minWidth:120}},mainCustom:{type:"string",title:i18nExpression("cusEntry.vendorMod.mainCustomer"),"x-render-table-column":{minWidth:120}},fileId:{type:"string",title:i18nExpression("cusEntry.vendorMod.achievement"),"x-render-table-column":{minWidth:120},"x-component":"SrmCommonFile","x-component-props":{extraData:{fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},defaultFile:{fileId:expression("$table.getRowByIndex($self.index)?.fileId"),fileName:expression("$table.getRowByIndex($self.index)?.fileName")},readonly:!1,"@on-change":expression(`({file}) => {
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
                        }`)}}}}})}}}}}}},qualificationInformation={qualificationInformation:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("cusEntry.vendorMod.qualificationInformation")},"x-query-engine-skip":!0,"x-visible":generateCharExpressionByFunction(({$form})=>!$form.query("state").get("data").isSimple),properties:{toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},properties:{tips:{type:"void","x-component":"div","x-component-props":{style:"color: red"},"x-visible":expression("$form.query('state').get('data').overseasRelation != 'OUT'"),"x-content":expression("$form.query('state').get('data').overseasRelation == 'PERSONAL' ? t('cusEntry.vendorMod.qualificationInformationTips1') : t('cusEntry.vendorMod.qualificationInformationTips2')")}}},qualificationInfo:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:expression("!$disabled"),height:350,pagination:!1,sortable:!1},"x-query-engine-skip":!0,properties:generateXindexInOrder({authNum:{type:"string","x-component":"DictSelect","x-component-props":{code:expression("'CERTIFICATE_TYPE_' + $form.query('state').get('data').overseasRelation")},title:i18nExpression("cusEntry.vendorMod.certificateType"),"x-render-table-column":{minWidth:200},"x-read-pretty":!0},startDate:{...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.startDate, '{y}-{m}-{d}')
              }`)},title:i18nExpression("cusEntry.vendorMod.startTime"),"x-render-table-column":{minWidth:120},...editTableFormItemValid},endDate:{...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.endDate, '{y}-{m}-{d}')
              }`)},title:i18nExpression("cusEntry.vendorMod.endTime"),"x-render-table-column":{minWidth:120},...editTableFormItemValid},extCertificatePeriod:{type:"string",title:i18nExpression("cusEntry.vendorMod.extCertificatePeriod"),"x-render-table-column":{minWidth:120},...editTableFormItemValid},extIsMandatory:{type:"string","x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"},title:i18nExpression("dataConfMod.isRequested"),"x-render-table-column":{minWidth:120},"x-read-pretty":!0},fileuploadId:{type:"string","x-component":"SrmCommonFile","x-component-props":{extraData:{fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},defaultFile:{fileId:expression("$table.getRowByIndex($self.index)?.fileuploadId"),fileName:expression("$table.getRowByIndex($self.index)?.authType")},readonly:expression("$disabled"),"@on-change":expression(`({file}) => {
                const { fileId = '', fileName = '' } = file || {}
                let row = $table.getRowByIndex($self.index)
                row.fileuploadId = fileId
                row.authType = fileName
              }`)},title:i18nExpression("cusEntry.vendorMod.file"),"x-render-table-column":{minWidth:120}},authDescription:{type:"string",title:i18nExpression("cusEntry.vendorMod.remark"),"x-render-table-column":{minWidth:120}}})}}}},authInfo={authInfo:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("cusEntry.vendorMod.authInfo")},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").overseasRelation!=="PERSONAL"&&!$form.query("state").get("data").isSimple),properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:{certifiedContact:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('cusEntry.vendorMod.certifiedContact')"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),maxlength:100}},certifiedContactPhone:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('cusEntry.vendorMod.certifiedContactPhone')"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},"x-validator":{message:i18nExpression("dataConfMod.msgContactPhone"),validator:expression(`(value, rule) => {
                if(value && !validatePhone(value)){
                  return $t('vendorMod.correctPhoneNumber')
                }
              }`)}}}}}}},personBaseInfo={person:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("cusEntry.vendorMod.baseInfo")},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").overseasRelation=="PERSONAL"&&!$form.query("state").get("data").isSimple),properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:{personBaseInfo:{type:"object","x-query-engine-skip":!0,properties:{businessLicenseFileId:{type:"string",title:i18nExpression("cusEntry.vendorMod.frontOfIdCard"),"x-decorator":"FormItem","x-component":"SrmCommonFile","x-component-props":{"extra-data":{uploadType:"DEF",sourceType:"WEB_APP",fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},"default-file":{fileId:expression("$form.query('personBaseInfo').get('value')?.businessLicenseFileId"),fileName:expression("$form.query('personBaseInfo').get('value')?.businessLicense")},"@on-change":expression(`({file}) => {
                     const { fileId = null, fileName = null } = file || {}
                     $form.query('personBaseInfo').get('value').businessLicenseFileId = fileId
                     $form.query('personBaseInfo').get('value').businessLicense = fileName
                  }`),readonly:expression("$form.query('state').get('data').$disabled")},...requiredValidatorSegment},extIdCardOppositeFileName:{type:"string","x-hidden":!0},extIdCardOppositeFileId:{type:"string",title:i18nExpression("cusEntry.vendorMod.backOfIdCard"),"x-decorator":"FormItem","x-component":"SrmCommonFile","x-component-props":{"extra-data":{uploadType:"DEF",sourceType:"WEB_APP",fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},"default-file":{fileId:expression("$form.query('personBaseInfo').get('value')?.extIdCardOppositeFileId"),fileName:expression("$form.query('personBaseInfo').get('value')?.extIdCardOppositeFileName")},"@on-change":expression(`({file}) => {
                    const { fileId = null, fileName = null } = file || {}
                    $form.query('personBaseInfo').get('value').extIdCardOppositeFileId = fileId
                    $form.query('personBaseInfo').get('value').extIdCardOppositeFileName = fileName
                  }`),readonly:expression("$form.query('state').get('data').$disabled")},...requiredValidatorSegment},companyName:{type:"string","x-decorator":"FormItem",title:i18nExpression("cusEntry.vendorMod.companyNameOrPersonName"),...requiredValidatorSegment,"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},companyShortName:{type:"string","x-decorator":"FormItem",title:i18nExpression("cusEntry.vendorMod.personalAbbreviation"),...requiredValidatorSegment,"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),maxlength:100,"show-word-limit":!0}},businessLicense:{type:"string","x-hidden":!0},idNumber:{type:"string","x-decorator":"FormItem",title:i18nExpression("cusEntry.vendorMod.idNo"),...requiredValidatorSegment,"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},validityPeriodOfCard:{type:"date","x-decorator":"FormItem",title:i18nExpression("cusEntry.vendorMod.validityPeriodOfCard"),"x-component-props":{type:"daterange",disabled:expression("$form.query('state').get('data').$disabled")},...requiredValidatorSegment},businessScope:{type:"string",title:i18nExpression("cusEntry.vendorMod.mainBusinessScope"),"x-decorator":"FormItem","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},enterpriseNo:{type:"string",title:i18nExpression("cusEntry.vendorMod.enterpriseNo"),"x-decorator":"FormItem","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},...requiredValidatorSegment},companyCountry:{type:"string",title:i18nExpression("components.address.country"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"country",disabled:expression("$form.query('state').get('data').$disabled"),placeholder:expression("$t('common.pleaseSelect')"),"@change":expression(`(val) => {
                    $form.query('personBaseInfo.companyProvince').take().value = ''
                    $form.query('personBaseInfo.companyCity').take().value = ''
                  }`)},"x-reactions":expression(`() => {
                  const data = $taxDictClass.getDictDetail('country', $self.value)
                  $form.query('personBaseInfo.companyProvince').take()?.setComponentProps({ code: data ? data.description : undefined })
                }`),...requiredValidatorSegment},companyProvince:{type:"string",title:i18nExpression("components.address.area"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),"custom-select-type":"PROVINCE",placeholder:expression("$t('common.pleaseSelect')"),"@change":expression(`(val) => {
                    $form.query('personBaseInfo.companyCity').take().value = ''
                  }`)},"x-visible":"{{ ['CN', 'RU'].includes($form.query('personBaseInfo.companyCountry').take().value) }}",...requiredValidatorSegment},companyCity:{type:"string",title:i18nExpression("components.address.city"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:expression("$form.query('personBaseInfo.companyProvince').take()?.value || ''"),"custom-select-type":"CITY",emptyOptionCanCreat:!0,placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$form.query('state').get('data').$disabled || !$form.query('personBaseInfo.companyProvince').take()?.value")},"x-visible":"{{ ['CN', 'RU'].includes($form.query('personBaseInfo.companyCountry').take().value) }}",...requiredValidatorSegment},companyAddress:{type:"string",title:i18nExpression("cusEntry.vendorMod.detailAddress"),"x-decorator":"FormItem",...requiredValidatorSegment,"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},position:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('components.orgPositionSel.position')"),"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}}}}}}}}},vendorSiteInfoList={vendorSiteInfo:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.vendorSiteInfos2")},"x-query-engine-skip":!0,"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").overseasRelation!=="PERSONAL"&&!$form.query("state").get("data").isSimple),properties:{toolbar:{type:"void","x-component":"Space","x-component-props":{class:"list-form__toolbar"},"x-visible":expression("!$form.query('state').get('data').$disabled"),properties:{add:{type:"void",title:i18nExpression("common.new"),"x-component":"RButton","x-component-props":{type:"primary","@click":expression(`() => {
                 $self.query('companyAddressInfos')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
              }`)}}}},companyAddressInfos:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,maxHeight:400,pagination:!1,sortable:!1,primaryKey:"companyAddressId",cascadeDeletion:!0},"x-query-engine-skip":!0,properties:generateXindexInOrder({country:{type:"string",title:i18nExpression("components.address.country"),"x-render-table-column":{minWidth:120},"x-component":"DictSelect","x-component-props":{code:"country",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$form.query('state').get('data').$disabled"),"@change":expression(`(val) => {
                let row = $table.getRowByIndex($self.index)
                row.area = null
                row.city = null
              }`)},"x-reactions":expression(`(f) => {
              const data = $taxDictClass.getDictDetail('country', $self.value)
              $form.query('CompanyInfo.SchemaWorkflow.layout.collapse.vendorSiteInfo.companyAddressInfos.' + [$self.index] + '.area').take()?.setComponentProps({ code: data ? data.description : undefined })
            }`),"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},area:{type:"string",title:i18nExpression("components.address.area"),"x-render-table-column":{minWidth:120},"x-component":"DictSelect","x-component-props":{"custom-select-type":"PROVINCE",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$form.query('state').get('data').$disabled || !['CN', 'RU'].includes($table.getRowByIndex($self.index).country)"),"@change":expression(`(val) => {
                $table.getRowByIndex($self.index).city = null
              }`)}},city:{type:"string",title:i18nExpression("components.address.city"),"x-render-table-column":{minWidth:120},"x-component":"DictSelect","x-component-props":{code:expression("$table.getRowByIndex($self.index).area || ''"),"custom-select-type":"CITY",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$form.query('state').get('data').$disabled || !$table.getRowByIndex($self.index).area")}},address:{type:"string",title:i18nExpression("components.address.detailAddress"),"x-render-table-column":{minWidth:150},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")},"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},postalCode:{type:"string",title:i18nExpression("components.address.postalCode"),"x-render-table-column":{minWidth:150},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},remark:{type:"string",title:i18nExpression("components.address.remark"),"x-render-table-column":{minWidth:150},"x-component-props":{disabled:expression("$form.query('state').get('data').$disabled")}},isActive:{type:"string",title:i18nExpression("common.enable"),"x-render-table-column":{minWidth:100},"x-component":"Checkbox","x-component-props":{"true-label":"Y","false-label":"N",disabled:expression("$form.query('state').get('data').$disabled")}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{disabled:expression("$form.query('state').get('data').$disabled"),type:"text","@click":expression(`({ row }) => {
                      $table.remove($self.index)
                  }`)}}}}})}}}},_sfc_main$2=defineComponent({__name:"vendorGreenChannelDetailEngine",setup(__props){const{app,emitTabRemove,t,vendor,http}=usePageHelper(),attrs=useAttrs(),workflowStatus=ref$1("DRAFT");let $disabled=["view","approve"].includes(attrs.params.flag||"");const newAddress=defineComponent({name:"newAddress",props:CAddress.props,setup(props,{listeners,attrs:attrs2,slots}){return useAutoMountInstanceToField(),()=>h(CAddress,{props:{...attrs2,...props},on:listeners,ref:"address"},slots)}}),$nodeList=$form=>{let userType=$form.query("state").get("data").overseasRelation,isSimple=$form.query("state").get("data").isSimple;const base=[{code:"userInfoForm",name:t("vendorMod.vendorUserInfo"),percentage:0}],company=[{code:"userInfoForm",name:t("vendorMod.vendorUserInfo"),percentage:0},{code:"companyType",name:t("vendorMod.companyType"),percentage:0},{code:"companyInfo",name:t("vendorMod.enterpriseThreeCertificates"),percentage:0},{code:"companyBaseInfo",name:t("vendorMod.companyBaseInfo"),percentage:0},{code:"contactInfoList",name:t("vendorMod.contactInfo"),percentage:0},{code:"serviceRange",name:t("cusEntry.vendorMod.serviceRange"),percentage:0},{code:"vendorSiteInfo",name:t("vendorMod.vendorSiteInfos2"),percentage:0},{code:"authInfo",name:t("cusEntry.vendorMod.authInfo"),percentage:0},{code:"qualificationInformation",name:t("cusEntry.vendorMod.qualificationInformation"),percentage:0},{code:"fileUploadsList",name:t("vendorMod.sceneAttachmentInfo2"),percentage:0}],person=[{code:"userInfoForm",name:t("vendorMod.vendorUserInfo"),percentage:0},{code:"person",name:t("cusEntry.vendorMod.baseInfo"),percentage:0},{code:"contactInfoList",name:t("vendorMod.contactInfo"),percentage:0},{code:"serviceRange",name:t("cusEntry.vendorMod.serviceRange"),percentage:0},{code:"qualificationInformation",name:t("cusEntry.vendorMod.qualificationInformation"),percentage:0},{code:"fileUploadsList",name:t("vendorMod.sceneAttachmentInfo2"),percentage:0}];return isSimple?base:userType==="PERSONAL"?person:company},viewUpdateButton=computed(()=>!$disabled&&!["APPROVED","SUPPLIER_SUBMITTED"].includes(workflowStatus.value)),disabledUpdateButton=computed(()=>["APPROVING"].includes(workflowStatus.value)),query={"*":{},userInfo:{"*":{}},contactInfos:{"*":{}},orgCategorys:{"*":{}},orgInfos:{"*":{}},operationInfo:{"*":{}},fileUploads:{"*":{}},supplierLeaderList:{"*":{}},companyAddressInfos:{"*":{}},qualificationInfo:{"*":{}},cateJournalList:{"*":{},npmSerciceCustoms:{"*":{}}}},initButtonConfig=$form=>{setTimeout(()=>{let businessType=$form.query("state").get("data").overseasRelation==="PERSONAL"?"supplierGreenChannelPersonal":"supplierGreenChannelCompany";const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.buttonConfigInfo.save.view=viewUpdateButton.value,componentInstance.buttonConfigInfo.submit.view=viewUpdateButton.value,componentInstance.buttonConfigInfo.save.disabled=disabledUpdateButton.value,componentInstance.buttonConfigInfo.submit.disabled=disabledUpdateButton.value,componentInstance.buttonConfigInfo.cancel.view=!1,componentInstance.buttonConfigInfo.close.view=!1,componentInstance.setWorkflowBusinessType(businessType),componentInstance.setWorkflowBusinessId(attrs.params.companyId||""),componentInstance.setWorkflowTabDisabled([null,void 0,"DRAFT","WITHDRAW","REJECTED"].includes(attrs.params.row?.status)&&attrs.params.flag!="approve"),componentInstance.setWorkflowBusinessVariables({})})},updateButtonConfig=$form=>{setTimeout(()=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.buttonConfigInfo.save.view=viewUpdateButton.value,componentInstance.buttonConfigInfo.submit.view=viewUpdateButton.value,componentInstance.buttonConfigInfo.save.disabled=disabledUpdateButton.value,componentInstance.buttonConfigInfo.submit.disabled=disabledUpdateButton.value,componentInstance.buttonConfigInfo.close.view=!1,(attrs.params.row?.status||null)=="SUBMITTED"&&componentInstance.workflowParamsInfo.integrationMode=="Push"&&(componentInstance.buttonConfigInfo.withdraw.view=!0)},50)},$managementChange=(value,name,$form)=>{try{if(value){let data=$form.query(".managementAttaches").take().value;if(value=="Y"){let bold=1;data.forEach(e=>{e.documentInspection==name&&(bold=0)}),bold&&data.unshift({documentInspection:name,managementAttachId:null,managementInfoId:null,companyId:null,fileuploadId:null,authType:"",authDescription:"",authNum:"",authDate:"",authOrg:"",endDate:""})}else data.forEach((e,index2)=>{e.documentInspection==name&&data.splice(index2,1)});$form.query(".managementAttaches").take().value=data}}catch{}},$showSunFile=$self=>{const fileList=[];if($self.value){const fileIdList=$self.value?.split(","),fileNameList=$self.query(".sunshineFileName").get("value")?.split(",");fileIdList.forEach((item,index2)=>{fileList.push({fileId:item,fileName:fileNameList?.[index2]})})}$self.setComponentProps({fileList})},schema=defineSchemas({state:{type:"void","x-component":"Fragment","x-hidden":!0,"x-data":{companyId:attrs.params.companyId||null,$disabled:!1,deleAttr:[],deleFileUploads:[],activeStep:expression("$attrs.params.activeStep"),overseasRelation:"",serciceCustomDelList:[],activeNavIndex:0,isSimple:!0}},CompanyInfo:{type:"void","x-decorator":"QueryEngine","x-component":"el-container","x-component-props":{class:"flex-container vendorGreen",direction:"vertical"},"x-query-engine":{service:"sup",actions:{greenQuery:{immediate:!0,loading:!0,ready:expression(`() => {
            const activeStep = $form.query('state').get('data').activeStep
            if (activeStep === 'main') {
              initButtonConfig($form)
            }
            $form.values.serviceRangeList = [{
              tableForm: {},
              list: [{}]
            }]
            setTimeout(() => {
              $form.values.contactInfos = []
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
              data.userInfo.lcCode = data.lcCode
              data.userInfo.accountGroup = data.accountGroup
              data.nickname = data.userInfo.nickname
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
            if ($form.query('serviceRangeList').take()) $form.query('serviceRangeList').take().value = serviceRange
            $form.query('state').get('data').overseasRelation = data.overseasRelation
            if (!data.qualificationInfo || !data.qualificationInfo.length) {
              $initQualificationInfo($form)
            }
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
                extSex,
                businessScope,
                companyCountry,
                companyProvince,
                companyCity,
                companyAddress,
                lcCode,
                enterpriseNo
              }
            }
            const status = data.status
            if (['APPROVED', 'SUBMITTED', 'ABANDONED'].includes(status)) {
              $form.query('state').get('data').$disabled = true
            }
            if ($disabled) {
              $form.query('state').get('data').$disabled = true
            }
            // 供应商已补充完信息时，取消简易模式，展示所有信息
            $form.query('state').get('data').isSimple = data.ifSupplierCompleteInfo != 'Y'
            // 附件要删除的列表
            let curFile = data.fileUploads || []
            $form.query('state').get('data').deleFileUploads = curFile.map(item =>item.sceneFileId)
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
            $saveBillSimple(type, $form, $queryEngine, $confirm, $message, $bus, $t)
          }`),"@submit-direct":expression(`(type) => {
            $saveBillSimple(type, $form, $queryEngine, $confirm, $message, $bus, $t)
          }`),"@confirm":expression(`(type, comment) => {
            $saveBillSimple(type, $form, $queryEngine, $confirm, $message, $bus, $t)
          }`),"@close-tab":expression(`() => {
            $back($bus)
          }`),"@update-integration-mode":expression(`(integrationMode) => {
            updateButtonConfig($form)
          }`)},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").activeStep==="main"),items:{type:"object","x-query-engine-skip":!0,properties:{preStep:{type:"void","x-visible":expression("!$disabled"),"x-content":i18nExpression("common.prevOne"),"x-component":"Button","x-component-props":{type:"primary",style:{"margin-right":"10px"},"@click":expression(`() => {
                  $form.query('state').get('data').activeStep = 'companyNature'
                  setTimeout(() => {
                    $form.values.overseasRelation = $form.query('state').get('data').overseasRelation
                  })
                }`)}}}},properties:{layout:{type:"void","x-component":"FormContainer",properties:{...Steps,collapse:{type:"void","x-component":"Collapse","x-component-props":{defaultOpenPanelCount:1},properties:{...userInfoForm,...companyType,...companyInfo,...personBaseInfo,...companyBaseInfo,...contactInfoList,...serviceRange,...vendorSiteInfoList,...authInfo,...qualificationInformation,fileUploadsList:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.sceneAttachmentInfo2")},"x-query-engine-skip":!0,"x-visible":generateCharExpressionByFunction(({$form})=>!$form.query("state").get("data").isSimple),properties:{fileUploads:{"x-query-engine-relation":"fileUploads:*",type:"array","x-component":"FileDynamic","x-component-props":{"scene-module-code":"SCENE_SUPPLIER_ATTACHMENT",businessId:expression("$attrs.params.companyId || null"),editable:expression("!$disabled"),"need-init":!1}}}}}}}},CFillProgress:{type:"void","x-component":"CFillProgress","x-component-props":{activeNavIndex:expression("$form.query('state').get('data').activeNavIndex"),class:"contract-progress",ref:"contractProgress",nodeName:"$t('logisticsMod.contractInfo')",data:expression("$nodeList($form)"),percentage:"{{true}}","@index-click":`{{ (code) => {
                let anchorEle = document.querySelector('#collapse_' + code)
                if (anchorEle) {
                  anchorEle.scrollIntoView(true)
                }
              }}}`}}}}}}}),$back=$bus=>{emitTabRemove(attrs.tabName),$bus.$emit("green")},$saveBillSimple=async(type,$form,$queryEngine,$confirm,$message,$bus,$t)=>{if(type=="WITHDRAW"){emitTabRemove(attrs.tabName),$bus.$emit("green");return}let values=JSON.parse(JSON.stringify($form.values));if(values.contactInfos=null,values.lcCode=values.userInfo.lcCode,values.accountGroup=values.userInfo.accountGroup,values.userInfo.nickname=values.nickname,values.firstLoginFlag="N",type=="SUBMIT"){let validate=!0;if(await $form.validate().then().catch(eq=>{app.$message.error(eq[0].messages[0]),validate=!1}),!validate)return!1}type=="SAVE"?([null,void 0,"DRAFT"].includes(values.status)&&(values.status="DRAFT"),$queryEngine.request.save({...values},{query:{"*":{}},action:"pjGreenSave"}).then(()=>{$message.success($t("common.successSave")),$bus.$emit("green"),emitTabRemove(attrs.tabName)}).catch(err=>{})):$queryEngine.request.save({...values},{query:{"*":{}},action:"pjGreenSubmit"}).then(res=>{$message.success($t("common.success")),$bus.$emit("green"),emitTabRemove(attrs.tabName)}).catch(err=>{})},$saveBill=async(type,$form,$queryEngine,$confirm,$message,$bus,$t)=>{if(type=="WITHDRAW"){emitTabRemove(attrs.tabName),$bus.$emit("green");return}let{personBaseInfo:personBaseInfo2={},...values}=JSON.parse(JSON.stringify($form.values));if(personBaseInfo2.validityPeriodOfCard){const[businessStartDate,businessEndDate]=personBaseInfo2.validityPeriodOfCard;personBaseInfo2.businessStartDate=businessStartDate,personBaseInfo2.businessEndDate=businessEndDate}values.lcCode=values.userInfo.lcCode,values.accountGroup=values.userInfo.accountGroup,values.userInfo.nickname=values.nickname;const serciceCustomDelList=$form.query("state").get("data").serciceCustomDelList||[];let serviceRange2=$form.query("serviceRangeList").get("value").map(item=>{const{list,tableForm}=item;return{...tableForm,npmSerciceCustoms:[...list,...serciceCustomDelList]}});if(values.cateJournalList=serviceRange2,values.firstLoginFlag="N",values.greenChannelReason=values.userInfo.greenChannelReason,values.qualificationInfo=$form.query("qualificationInfo").get("value"),values.fileUploads=$form.query("fileUploads").take().value,$form.query("state").get("data")?.deleFileUploads&&$form.query("state").get("data").deleFileUploads.forEach(item=>{values.fileUploads.find(e=>e.sceneFileId==item)||values.fileUploads.push({$delete:item})}),type=="SUBMIT"){let validate=!0;if(await $form.validate().then().catch(eq=>{app.$message.error(eq[0].messages[0]),validate=!1}),!validate)return!1;const{lcCode,idNumber}={...values,...personBaseInfo2},res=await integritySystem(lcCode||idNumber);if(res.data===integritySystemResultMap.get("forbid")){$message.warning($t("cusEntry.tipMessage.blackForbid"));return}else res.data===integritySystemResultMap.get("focus")&&(values.focusFlag="Y",values.npmCompanyExceptionInfos.push({exceptionType:"FOCUS_FLAG"}));let valid=values.qualificationInfo.findIndex(item=>item.extIsMandatory=="Y"&&!item.fileuploadId);if(valid>-1){$message.warning($t("cusEntry.vendorMod.qualificationInformation")+$t("bidMod.warningMessage",{index:valid+1,message:$t("bidMod.pleaseUploadFile")}));return}}values.ceeaBusinessModel=values.ceeaBusinessModel?.length?values.ceeaBusinessModel.join():null,type=="SAVE"?[null,void 0,"DRAFT"].includes(status)?(values.status="DRAFT",$queryEngine.request.save({...values,...personBaseInfo2},{query:{"*":{}},action:"greenSave"}).then(()=>{$message.success($t("common.successSave")),$bus.$emit("green"),emitTabRemove(attrs.tabName)}).catch(err=>{$form.values.ceeaBusinessModel=values.ceeaBusinessModel?values.ceeaBusinessModel.split(","):[]})):$queryEngine.request.save({...values,...personBaseInfo2},{query:{"*":{}},action:"greenSave"}).then(()=>{$message.success($t("common.successSave")),$bus.$emit("green"),emitTabRemove(attrs.tabName)}).catch(err=>{$form.values.ceeaBusinessModel=values.ceeaBusinessModel?values.ceeaBusinessModel.split(","):[]}):$queryEngine.request.save({...values,...personBaseInfo2},{query:{"*":{}},action:"greenSubmit"}).then(res=>{const businessType=$form.query("state").get("data").overseasRelation==="PERSONAL"?"supplierGreenChannelPersonal":"supplierGreenChannelCompany";$form.query("SchemaWorkflow").take(field=>{field.componentProps.componentInstance.workflowParamsInfo.businessType=businessType});const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.setWorkflowBusinessId(res.data[0]?.companyId||null),componentInstance.setWorkflowTabDisabled(!1),componentInstance.setWorkflowBusinessVariables({}),componentInstance.handlerAfter(type.toUpperCase(),()=>{$bus.$emit("green"),emitTabRemove(attrs.tabName)}),setTimeout(()=>{$form.readPretty=!0,$form.query("state").get("data").$disabled=!0,componentInstance.buttonConfigInfo.save.view=!1,componentInstance.buttonConfigInfo.submit.view=!1},100)}).catch(err=>{$form.values.ceeaBusinessModel=values.ceeaBusinessModel?values.ceeaBusinessModel.split(","):[]})},integritySystem=lcCode=>http({url:"/api-sup/pj/companyInfo/queryIfBlackCompany",method:"POST",data:{lcCode}}),integritySystemResultMap=new Map([["forbid",t("cusEntry.vendorMod.forbid")],["focus",t("cusEntry.vendorMod.focus")]]),$addScrollEvent=$form=>{const navNodes=$nodeList($form),offsetTopArr=[];navNodes.forEach(node=>{const element=document.getElementById(`collapse_${node.code}`);element&&offsetTopArr.push(element.offsetTop)}),window.addEventListener("scroll",$throttle($scrollHandler,100,$form,offsetTopArr),!0)},$throttle=(fn,delay,$form,offsetTopArr)=>{let timer=null;return()=>{timer||setTimeout(()=>{fn($form,offsetTopArr),clearTimeout(timer),timer=null},delay)}},$scrollHandler=($form,offsetTopArr)=>{const scrollTop=document.getElementsByClassName("el-tabs__content")[2]?.scrollTop;let navIndex=0;offsetTopArr.findIndex(item=>item>=scrollTop);for(let n=0;n<offsetTopArr.length;n++)scrollTop>=offsetTopArr[n]&&(navIndex=n);$form.query("state").get("data")&&($form.query("state").get("data").activeNavIndex=navIndex)},$initQualificationInfo=async $form=>{let cur=$form.query("state").get("data").overseasRelation,dictCode=`CERTIFICATE_TYPE_${cur}`,res=DictClass.getDict(dictCode);cur&&!res&&(res=(await http({url:"/api-base/dict/base-dict-item/listByDictCode",method:"POST",data:[dictCode]})).data||[]),$form.values.qualificationInfo=res?res.map(item=>({authNum:item.dictItemCode,extIsMandatory:item.itemDescription||"N"})):[]},scope={app,t,$attrs:attrs,updateButtonConfig,performPlanService,$disabled,emitTabRemove,initButtonConfig,$saveBill,$saveBillSimple,$back,DictSelect,observer,$managementChange,query,validEmail,validatePhone,$nodeList,sceneFileApi,integritySystem,integritySystemResultMap,$showSunFile,$addScrollEvent,$throttle,$scrollHandler,$initQualificationInfo,$taxDictClass:createDictClass({country:[]})};return{__sfc:!0,app,emitTabRemove,t,vendor,http,attrs,workflowStatus,$disabled,newAddress,$nodeList,viewUpdateButton,disabledUpdateButton,query,initButtonConfig,updateButtonConfig,$managementChange,$showSunFile,schema,$back,$saveBillSimple,$saveBill,integritySystem,integritySystemResultMap,$addScrollEvent,$throttle,$scrollHandler,$initQualificationInfo,scope,components:{SrmCommonFile:CommonFile,CAddress,CCategorySelect,FileDynamic,CFillProgress,newAddress,natureChose},RenderEngine}}});var _sfc_render$2=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{schemaKey:"vendorGreenChannelDetail",pageAttrs:_vm.$attrs,schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns$2=[],__component__$2=normalizeComponent(_sfc_main$2,_sfc_render$2,_sfc_staticRenderFns$2,!1,null,null,null,null);const vendorGreenChannelDetail=__component__$2.exports,_sfc_main$1=defineComponent({__name:"vendorGreenChannelListEngine",setup(__props){const{emitTabAdd,app}=usePageHelper(),schema=defineSchemas({CompanyInfo:{type:"void","x-decorator":"QueryEngine","x-query-engine":{service:"sup",actions:{greenQuery:{immediate:!0,method:"paginationQuery",transformRequest:expression(`(data, headers) => {
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
            }`),onSuccess:expression(`(res) => {
            let data = res.data
            data.forEach(item => {
              item.companyName = item.companyName || '--'
            })
            return data
          }`)}}},"x-component":"el-container","x-component-props":{class:"flex-container the_contractTemplateList_wrapper",direction:"vertical"},properties:{bus:{type:"void","x-component":"BusEvent","x-component-props":{eventName:"green","@listener":expression(`() => {
            $queryEngine.state.paginationManagement.refresh()
          }`)}},query:{type:"object","x-query-engine-skip":!0,"x-component":"QueryFormByQueryEngine",properties:generateXindexInOrder({companyName:{type:"string",title:"{{$t('common.vendorName')}}","x-component":"QuickSearchWrapper","x-component-props":{showKey:"companyName",propKey:"companyName",name:"scc_sup_company_info_display_buyer"}},lcCode:{type:"string",title:"{{$t('vendorMod.lcCode')}}","x-query-engine-query-operator":"contains"},isBacklist:{type:"string",title:"{{$t('vendorMod.isBacklist')}}","x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"}},domesticAndForeignRelations:{type:"string",title:"{{$t('cusEntry.vendorMod.domesticAndForeignRelations')}}","x-component":"DictSelect","x-component-props":{code:"RELATION_NEW"}},companyType:{type:"string",title:"{{$t('vendorMod.companyType')}}","x-component":"DictSelect","x-component-props":{code:"COMPANY_NATURE_NEW"}},extGreenChannelStatus:{type:"string",title:"{{$t('common.status')}}","x-component":"DictSelect","x-component-props":{code:"APPROVE_STATUS_TYPE"}},legalPerson:{type:"string",title:"{{$t('vendorMod.legalPerson')}}","x-query-engine-query-operator":"contains"},approvedDate:{title:"{{$t('vendorMod.permitDate')}}",...dataTimeSelectorSegment,"x-query-engine-query-operator":"between"}})},toolbar:{type:"void","x-component":"Space","x-component-props":{style:"margin-bottom: 16px"},properties:{add:{type:"void",title:"{{$t('common.add')}}","x-component":"RButton","x-component-props":{type:"primary","@click":expression(`() => {
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
              }`)},"x-visible":expression("$authorityVisible('sup:greenChannel:importCompany')")},importCompanyVendor1:{type:"void","x-component":"ImportExcel","x-component-props":{title:i18nExpression("cusEntry.vendorMod.importCompanyVendor1"),type:"default",extraData:{fileModular:"sup",fileFunction:"companyInfoImportExcel",fileType:"excel"},upLoadUrl:"/api-sup/pj/companyInfo/importExcel",downloadTemplateOptions:{downloadUrl:"/api-sup/pj/companyInfo/importExcelTemplate",fileName:expression("$t('vendorMod.vendorImportTemplateXLXS')")},"@handleSuccess":expression(`() => {
                $queryEngine.state.paginationManagement.refresh()
              }`)},"x-visible":expression("$authorityVisible('sup:greenChannel:importCompany1')")}}},table:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",class:"table-view-vxe-table",openCustomTable:!0},properties:generateXindexInOrder({companyId:{type:"string","x-hidden":!0},companyCode:{type:"string",title:"{{$t('common.vendorCode')}}","x-render-table-column":{width:120}},companyName:{"x-component":"TableButton","x-component-props":{type:"text","@click":expression(`({row}) => {
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
              }`)},"x-render-table-column":{title:i18nExpression("common.vendorName"),minWidth:150,customRender:!0}},domesticAndForeignRelations:{type:"string",title:"{{$t('cusEntry.vendorMod.domesticAndForeignRelations')}}","x-component":"DictSelect","x-component-props":{code:"RELATION_NEW"},"x-render-table-column":{width:150}},overseasRelation:{type:"string",title:"{{$t('vendorMod.overseasRelation')}}","x-component":"DictSelect","x-component-props":{code:"RELATION"},"x-render-table-column":{width:150}},companyType:{type:"string",title:"{{$t('vendorMod.companyType')}}","x-component":"DictSelect","x-component-props":{code:"COMPANY_NATURE_NEW"},"x-render-table-column":{width:100}},lcCode:{type:"string",title:"{{$t('vendorMod.lcCode')}}","x-render-table-column":{width:150}},legalPerson:{type:"string",title:"{{$t('vendorMod.legalPerson')}}","x-render-table-column":{width:150}},isBacklist:{type:"string",title:"{{$t('vendorMod.isBacklist')}}","x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"},"x-render-table-column":{width:120}},extGreenChannelStatus:{type:"string",title:"{{$t('common.status')}}","x-component":"DictSelect","x-component-props":{code:"APPROVE_STATUS_TYPE"},"x-render-table-column":{width:100}},createdFullName:{type:"string",title:"{{$t('common.creator')}}","x-render-table-column":{width:120}},approvedDate:{title:"{{$t('vendorMod.permitDate')}}",...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.approvedDate, '{y}-{m}-{d}')
              }`)},"x-render-table-column":{width:130}},lastUpdateDate:{type:"string","x-hidden":!0,"x-query-engine-sort":"desc"},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:120,fixed:"right"},"x-component":"RenderTableButtonList","x-component-props":{max:2},properties:{edit:{type:"void",title:"{{$t('common.edit')}}","x-reactions":changeFieldVisibleByDeps([".extGreenChannelStatus"],"['DRAFT'].includes($deps[0])"),"x-component-props":{type:"text","@click":expression(`({ row }) => {
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
                  }`)}},delete:{type:"void",title:"{{$t('common.delete')}}","x-reactions":changeFieldVisibleByDeps([".extGreenChannelStatus"],"['DRAFT'].includes($deps[0])"),"x-component-props":{type:"text","@click":expression(`({ row }) => {
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
                  }`)}}}}})}}}}),$recallFlow=(app2,data)=>app2.$http({url:"/api-pj/external/bpm/public/flow/native/rollBackProcess",method:"POST",data,loading:!0});return{__sfc:!0,emitTabAdd,app,schema,$recallFlow,scope:{emitTabAdd,app,i18nExpression,vendorGreenChannelDetail,$recallFlow},components:{},RenderEngine}}});var _sfc_render$1=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{staticClass:"contractPaymentType",attrs:{schemaKey:"vendorGreenChannelList",schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns$1=[],__component__$1=normalizeComponent(_sfc_main$1,_sfc_render$1,_sfc_staticRenderFns$1,!1,null,null,null,null);const vendorGreenChannelListEngine=__component__$1.exports,_sfc_main={name:"VendorGreenChannel",components:{NavTabs},data(){return{activeTab:"vendorGreenChannelListEngine",tabs:[{title:()=>this.$t("vendorMod.vendorGreenChannel"),name:"vendorGreenChannelListEngine",component:vendorGreenChannelListEngine,closable:!1}]}},mounted(){this.$route.params.openSavePage&&this.openSavePage()},methods:{openSavePage(){}}};var _sfc_render=function(){var _vm=this,_c=_vm._self._c;return _c("NavTabs",{ref:"tabs",attrs:{"tabs-list":_vm.tabs,"cur-tab":_vm.activeTab}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const index=__component__.exports;export{index as default};
