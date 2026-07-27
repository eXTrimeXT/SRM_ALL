import{ae as i18nExpression,aD as requiredValidatorSegment,ad as expression,af as yearMonthDaySelectorSegment,ag as radioGroupByYOrNSegment,ah as generateXindexInOrder}from"./index-6b6051d8.js";const companyType={beforeChange:{type:"void","x-component":"div","x-component-props":{class:"formClassAllChange"},properties:{beforeChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.beforeChange"}},companyTypeBefore:{type:"object","x-query-engine-skip":!0,"x-component":"FormGrid","x-component-props":{class:"forms"},properties:{overseasRelation:{type:"string",title:i18nExpression("vendorMod.overseasRelation"),"x-component":"DictSelect","x-component-props":{code:"RELATION_NEW",disabled:!0},"x-decorator":"FormItem",...requiredValidatorSegment},companyType:{type:"string",title:i18nExpression("vendorMod.companyType"),"x-component":"DictSelect","x-hidden":expression("$form.query('.companyTypeBefore.overseasRelation').take()?.value != 'INSIDE'"),"x-component-props":{code:"COMPANY_NATURE",disabled:!0},"x-decorator":"FormItem","x-validator":{required:expression("$form.query('.overseasRelation').take()?.value == 'INSIDE'"),message:i18nExpression("common.requiredField")}},supplierType:{type:"string",title:i18nExpression("supplierRating.supplierType"),"x-component":"DictSelect","x-component-props":{code:"SUPPLIER_TYPE",disabled:!0},"x-decorator":"FormItem",...requiredValidatorSegment}}}}},afterChange:{type:"void","x-component":"div","x-component-props":{class:"formClassAllChange"},properties:{afterChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.afterChange"}},companyTypeAfter:{type:"object","x-query-engine-skip":!0,"x-component":"FormGrid","x-component-props":{class:"forms"},properties:{overseasRelation:{type:"string",title:i18nExpression("vendorMod.overseasRelation"),"x-component":"DictSelect","x-component-props":{code:"RELATION_NEW",disabled:!0},"x-reactions":expression(`() => {
                const newData = $form.query('.companyTypeAfter.overseasRelation')?.take()?.value || null
                const oldData = $form.query('.companyTypeBefore.overseasRelation')?.take()?.value || null
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),"x-decorator":"FormItem",...requiredValidatorSegment},companyType:{type:"string",title:i18nExpression("vendorMod.companyType"),"x-component":"DictSelect","x-hidden":expression("$form.query('.companyTypeAfter.overseasRelation').take()?.value != 'INSIDE'"),"x-component-props":{code:"COMPANY_NATURE"},"x-reactions":expression(`() => {
                const newData = $form.query('.companyTypeAfter.companyType')?.take()?.value || null
                const oldData = $form.query('.companyTypeBefore.companyType')?.take()?.value || null
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),"x-decorator":"FormItem","x-validator":{required:expression("$form.query('.overseasRelation').take()?.value == 'INSIDE'"),message:i18nExpression("common.requiredField")}},supplierType:{type:"string",title:i18nExpression("supplierRating.supplierType"),"x-component":"DictSelect","x-component-props":{code:"SUPPLIER_TYPE"},"x-reactions":expression(`() => {
                const newData = $form.query('.companyTypeAfter.supplierType')?.take()?.value || null
                const oldData = $form.query('.companyTypeBefore.supplierType')?.take()?.value || null
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),"x-decorator":"FormItem",...requiredValidatorSegment}}}}}},enterpriseThreeCertificates={beforeChange:{type:"void","x-component":"div","x-component-props":{class:"formClassAllChange"},properties:{beforeChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.beforeChange"}},enterpriseThreeCertificatesBefore:{type:"object","x-query-engine-skip":!0,"x-component":"FormGrid","x-component-props":{class:"forms"},properties:{businessLicenseFileId:{type:"string","x-hidden":!0},businessLicense:{type:"string","x-hidden":!0},businessLicenseFile:{type:"string",title:i18nExpression("vendorMod.businessLicense"),"x-component":"SrmCommonFile","x-component-props":{disabled:!0,"extra-data":{uploadType:"DEF",sourceType:"WEB_APP",fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},"default-file":{fileId:expression("$form.query('.enterpriseThreeCertificatesBefore.businessLicenseFileId').take()?.value"),fileName:expression("$form.query('.enterpriseThreeCertificatesBefore.businessLicense').take()?.value")}},"x-decorator":"FormItem",...requiredValidatorSegment},companyName:{type:"string",title:i18nExpression("vendorMod.companyName"),"x-component-props":{disabled:!0},"x-decorator":"FormItem","x-validator":{required:expression("$form.query('.companyTypeBefore.overseasRelation').take()?.value == 'INSIDE'"),message:i18nExpression("common.requiredField")}},legalPerson:{type:"string",title:i18nExpression("vendorMod.legalPerson"),"x-component-props":{disabled:!0},"x-decorator":"FormItem"},businessLicenseNo:{type:"string","x-visible":expression("$form.query('.companyTypeBefore.overseasRelation').take()?.value == 'OUT'"),"x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.lcCode2')"),"x-component-props":{disabled:!0}},registCurrency:{type:"string","x-hidden":!0,"x-decorator":"FormItem"},registeredCapital:{type:"number","x-visible":expression("$form.query('.companyTypeAfter.companyType').take()?.value != 'GETI'"),"x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.registeredCapital')"),"x-component-props":{disabled:!0,class:"input-with-select"},"x-content":{append:expression(`observer({
                render: (h) => {
                  const targetField = $form.query('.enterpriseThreeCertificatesBefore.registCurrency').take()
                  return h(
                    $self.readPretty ? $$components.DictSelectPreview : $$components.DictSelect,
                    {
                      [$self.readPretty ? 'props' : 'attrs']: {
                        code: 'currency',
                        value: targetField?.value,
                        disabled: true
                      },
                      style: { 'padding-left': $self.readPretty ? '5px' : 0 },
                      on: {
                        'change': (v) => {
                          targetField.value = v
                        }
                      }
                    }
                  )
                }
              })`)},"x-validator":{required:expression("!['GETI','FEIYINGLI'].includes($form.query('.companyTypeAfter.companyType').take()?.value)"),message:i18nExpression("vendorMod.msgRegisteredCapital")}},companyCreationDate:{...yearMonthDaySelectorSegment,title:i18nExpression("vendorMod.creationDate"),"x-disabled":!0,"x-decorator":"FormItem"},companyShortName:{type:"string",title:i18nExpression("vendorMod.companyShortName"),"x-disabled":!0,"x-decorator":"FormItem"},lcCode:{type:"string","x-visible":expression("$form.query('.companyTypeBefore.overseasRelation').take()?.value == 'INSIDE' ? true : false"),title:i18nExpression("vendorMod.lcCode"),"x-disabled":!0,"x-decorator":"FormItem"},registrationAuthority:{type:"string",title:i18nExpression("vendorMod.registrationAuthority"),"x-disabled":!0,"x-decorator":"FormItem"},businessStartDate:{...yearMonthDaySelectorSegment,"x-hidden":expression("$form.query('.companyTypeAfter.companyType').take()?.value == 'GETI'"),title:i18nExpression("vendorMod.businessStartFrom"),"x-disabled":!0,"x-decorator":"FormItem"},businessEndDate:{...yearMonthDaySelectorSegment,"x-hidden":expression("$form.query('.companyTypeAfter.companyType').take()?.value == 'GETI'"),title:i18nExpression("common.pleaseSelectDate"),"x-disabled":!0,"x-decorator":"FormItem"},businessScope:{type:"string",title:i18nExpression("vendorMod.businessScope"),"x-disabled":!0,"x-decorator":"FormItem"}}}}},afterChange:{type:"void","x-component":"div","x-component-props":{class:"formClassAllChange"},properties:{afterChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.afterChange"}},enterpriseThreeCertificatesAfter:{type:"object","x-query-engine-skip":!0,"x-component":"FormGrid","x-component-props":{class:"forms"},properties:{businessLicenseFileId:{type:"string","x-hidden":!0},businessLicense:{type:"string","x-hidden":!0},businessLicenseFile:{type:"string",title:i18nExpression("vendorMod.businessLicense"),"x-component":"SrmCommonFile","x-component-props":{disabled:expression("$form.readPretty"),"extra-data":{uploadType:"DEF",sourceType:"WEB_APP",fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},"default-file":{fileId:expression("$form.query('.enterpriseThreeCertificatesAfter.businessLicenseFileId').take()?.value"),fileName:expression("$form.query('.enterpriseThreeCertificatesAfter.businessLicense').take()?.value")},"@on-change":expression(`(file) => {
                console.log(file)
                if (file) {
                  const { fileId, fileName } = file.file || {}
                  $form.query('.enterpriseThreeCertificatesAfter.businessLicenseFileId').take().value = fileId.toString()
                  $form.query('.enterpriseThreeCertificatesAfter.businessLicense').take().value = fileName
                } else {
                  $form.query('.enterpriseThreeCertificatesAfter.businessLicenseFileId').take().value = null
                  $form.query('.enterpriseThreeCertificatesAfter.businessLicense').take().value = null
                }
              }`)},"x-decorator":"FormItem",...requiredValidatorSegment},companyName:{type:"string",title:i18nExpression("vendorMod.companyName"),"x-component-props":{disabled:expression("$form.readPretty")},"x-reactions":expression(`() => {
                const newData = $form.query('.enterpriseThreeCertificatesAfter.companyName').take()?.value
                const oldData = $form.query('.enterpriseThreeCertificatesBefore.companyName').take()?.value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),"x-decorator":"FormItem","x-validator":{required:expression("$form.query('.companyTypeAfter.overseasRelation').take()?.value == 'INSIDE'"),message:i18nExpression("common.requiredField")}},legalPerson:{type:"string",title:i18nExpression("vendorMod.legalPerson"),"x-component-props":{disabled:expression("$form.readPretty")},"x-reactions":expression(`() => {
                const newData = $form.query('.enterpriseThreeCertificatesAfter.legalPerson').take()?.value
                const oldData = $form.query('.enterpriseThreeCertificatesBefore.legalPerson').take()?.value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),"x-decorator":"FormItem"},businessLicenseNo:{type:"string","x-visible":expression("$form.query('.companyTypeAfter.overseasRelation').take()?.value == 'OUT'"),"x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.lcCode2')"),"x-component-props":{disabled:expression("$form.readPretty")},"x-reactions":expression(`() => {
                const newData = $form.query('.enterpriseThreeCertificatesAfter.businessLicenseNo').take().value
                const oldData = $form.query('.enterpriseThreeCertificatesBefore.businessLicenseNo').take().value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`)},registCurrency:{type:"string","x-hidden":!0},registeredCapital:{type:"number","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.registeredCapital')"),"x-component-props":{disabled:expression("$form.readPretty"),class:"input-with-select"},"x-reactions":expression(`() => {
                const newData = $form.query('.enterpriseThreeCertificatesAfter.registeredCapital').take()?.value
                const oldData = $form.query('.enterpriseThreeCertificatesBefore.registeredCapital').take()?.value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),"x-content":{append:expression(`observer({
                render: (h) => {
                  const targetField = $form.query('.enterpriseThreeCertificatesAfter.registCurrency').take()
                  return h(
                    $self.readPretty ? $$components.DictSelectPreview : $$components.DictSelect,
                    {
                      [$self.readPretty ? 'props' : 'attrs']: {
                        code: 'currency',
                        value: targetField.value,
                      },
                      style: { 'padding-left': $self.readPretty ? '5px' : 0 },
                      on: {
                        'change': (v) => {
                          targetField.value = v
                        }
                      }
                    }
                  )
                }
              })`)},"x-validator":{required:expression("!['GETI','FEIYINGLI'].includes($form.query('.companyTypeAfter.companyType').take().value)"),message:i18nExpression("vendorMod.msgRegisteredCapital")}},companyCreationDate:{...yearMonthDaySelectorSegment,title:i18nExpression("vendorMod.creationDate"),"x-disabled":expression("$form.readPretty"),"x-reactions":expression(`() => {
                const newData = $form.query('.enterpriseThreeCertificatesAfter.companyCreationDate').take().value
                const oldData = $form.query('.enterpriseThreeCertificatesBefore.companyCreationDate').take().value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),"x-decorator":"FormItem"},companyShortName:{type:"string",title:i18nExpression("vendorMod.companyShortName"),"x-disabled":expression("$form.readPretty"),"x-reactions":expression(`() => {
                const newData = $form.query('.enterpriseThreeCertificatesAfter.companyShortName').take().value
                const oldData = $form.query('.enterpriseThreeCertificatesBefore.companyShortName').take().value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),"x-decorator":"FormItem"},lcCode:{type:"string","x-visible":expression("$form.query('.companyTypeAfter.overseasRelation').take().value == 'INSIDE' ? true : false"),title:i18nExpression("vendorMod.lcCode"),"x-reactions":expression(`() => {
                const newData = $form.query('.enterpriseThreeCertificatesAfter.lcCode').take().value
                const oldData = $form.query('.enterpriseThreeCertificatesBefore.lcCode').take().value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),"x-disabled":expression("$form.readPretty"),"x-decorator":"FormItem"},registrationAuthority:{type:"string",title:i18nExpression("vendorMod.registrationAuthority"),"x-reactions":expression(`() => {
                const newData = $form.query('.enterpriseThreeCertificatesAfter.registrationAuthority').take().value
                const oldData = $form.query('.enterpriseThreeCertificatesBefore.registrationAuthority').take().value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),"x-disabled":expression("$form.readPretty"),"x-decorator":"FormItem"},businessStartDate:{...yearMonthDaySelectorSegment,"x-hidden":expression("$form.query('.companyTypeAfter.companyType').take().value == 'GETI'"),title:i18nExpression("vendorMod.businessStartFrom"),"x-reactions":expression(`() => {
                const newData = $form.query('.enterpriseThreeCertificatesAfter.businessStartDate').take().value
                const oldData = $form.query('.enterpriseThreeCertificatesBefore.businessStartDate').take().value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),"x-disabled":expression("$form.readPretty"),"x-decorator":"FormItem"},businessEndDate:{...yearMonthDaySelectorSegment,"x-hidden":expression("$form.query('.companyTypeAfter.companyType').take().value == 'GETI'"),title:i18nExpression("common.pleaseSelectDate"),"x-reactions":expression(`() => {
                const newData = $form.query('.enterpriseThreeCertificatesAfter.businessEndDate').take().value
                const oldData = $form.query('.enterpriseThreeCertificatesBefore.businessEndDate').take().value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),"x-disabled":expression("$form.readPretty"),"x-decorator":"FormItem"},businessScope:{type:"string",title:i18nExpression("vendorMod.businessScope"),"x-reactions":expression(`() => {
                const newData = $form.query('.enterpriseThreeCertificatesAfter.businessScope').take().value
                const oldData = $form.query('.enterpriseThreeCertificatesBefore.businessScope').take().value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),"x-disabled":expression("$form.readPretty"),"x-decorator":"FormItem"}}}}}},companyBaseInfo={beforeChange:{type:"void","x-component":"div","x-component-props":{class:"formClassAllChange"},properties:{beforeChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.beforeChange"}},companyBaseInfoBefore:{type:"object","x-query-engine-skip":!0,"x-component":"FormGrid","x-component-props":{class:"forms"},properties:{ceeaAgentBrand:{type:"string",title:i18nExpression("vendorMod.agencyBrand"),"x-validator":{required:!1,message:i18nExpression("common.requiredField")},"x-disabled":!0,"x-decorator":"FormItem"},ceeaIfListed:{title:i18nExpression("vendorMod.ifListed"),...radioGroupByYOrNSegment,"x-component-props":{disabled:!0}},ceeaListedTime:{...yearMonthDaySelectorSegment,"x-visible":"{{$form.query('.companyBaseInfoBefore.ceeaIfListed').take()?.value == 'Y'}}","x-decorator":"FormItem","x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],disabled:!0},title:expression("$t('vendorMod.creationDate')"),"x-validator":{required:!0,message:i18nExpression("请选择上市时间")}},listedExchange:{type:"string","x-visible":"{{$form.query('.companyBaseInfoBefore.ceeaIfListed').take()?.value == 'Y'}}","x-decorator":"FormItem","x-component-props":{disabled:!0},title:expression("$t('vendorMod.listedExchange')"),"x-validator":{required:!0,message:i18nExpression("请选择上市交易所")}},ceeaBusinessModel:{type:"string",title:i18nExpression("vendorMod.bizModel"),"x-component":"DictSelect","x-component-props":{code:"BIZ_MODEL",disabled:!0},"x-decorator":"FormItem"},companyCountry:{type:"string",title:i18nExpression("components.address.country"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"country",placeholder:expression("$t('common.pleaseSelect')"),disabled:!0}},companyProvince:{type:"string",title:i18nExpression("components.address.area"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"PROVINCE","custom-select-type":"PROVINCE",placeholder:expression("$t('common.pleaseSelect')"),disabled:!0}},companyCity:{type:"string",title:i18nExpression("components.address.city"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:expression("$form.query('.companyBaseInfoBefore.companyProvince').take()?.value"),"custom-select-type":"CITY",placeholder:expression("$t('common.pleaseSelect')"),disabled:!0}},companyAddress:{type:"string",title:i18nExpression("components.address.detailAddress"),"x-disabled":!0,"x-decorator":"FormItem"},ceeaHasParentCompany:{title:i18nExpression("vendorMod.ifParentCompany"),...radioGroupByYOrNSegment,"x-component-props":{disabled:!0}},ceeaParentCompanyName:{type:"string","x-decorator":"FormItem","x-component":"Input","x-visible":"{{$form.query('.companyBaseInfoBefore.ceeaHasParentCompany').take()?.value == 'Y'}}",title:expression("$t('vendorMod.parentCompanyName')"),"x-component-props":{disabled:!0},"x-validator":{required:!0,message:i18nExpression("vendorMod.msgPCompany")}},dunsCode:{type:"string","x-visible":expression("$form.query('.companyTypeAfter.overseasRelation').take()?.value != 'INSIDE'"),title:"D-U-N-S","x-decorator":"FormItem"},ceeaParentCompanyLcCode:{type:"string","x-decorator":"FormItem","x-component":"Input","x-visible":"{{$form.query('.companyBaseInfoBefore.ceeaHasParentCompany').take()?.value == 'Y'}}",title:expression("$t('vendorMod.parentCompanyLcCode')"),"x-component-props":{disabled:!0},"x-validator":{required:!0,message:i18nExpression("cusEntry.supplement20250211.parentCompanyCreditCode")}},ceeaCompanyIntro:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.companyProfile')"),"x-component-props":{disabled:!0},"x-decorator-props":{gridSpan:3},"x-validator":{required:!0,message:i18nExpression("vendorMod.msgCompanyProfile")}}}}}},afterChange:{type:"void","x-component":"div","x-component-props":{class:"formClassAllChange"},properties:{afterChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.afterChange"}},companyBaseInfoAfter:{type:"object","x-query-engine-skip":!0,"x-component":"FormGrid","x-component-props":{class:"forms"},properties:{ceeaAgentBrand:{type:"string",title:i18nExpression("vendorMod.agencyBrand"),"x-validator":{required:!1,message:i18nExpression("common.requiredField")},"x-reactions":expression(`() => {
                const newData = $form.query('.companyBaseInfoAfter.ceeaAgentBrand').take()?.value
                const oldData = $form.query('.companyBaseInfoBefore.ceeaAgentBrand').take()?.value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),"x-disabled":expression("$form.readPretty"),"x-decorator":"FormItem"},ceeaIfListed:{title:i18nExpression("vendorMod.ifListed"),...radioGroupByYOrNSegment,"x-reactions":expression(`() => {
                const newData = $form.query('.companyBaseInfoAfter.ceeaIfListed').take()?.value
                const oldData = $form.query('.companyBaseInfoBefore.ceeaIfListed').take()?.value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),"x-component-props":{}},ceeaListedTime:{...yearMonthDaySelectorSegment,"x-visible":"{{$form.query('.companyBaseInfoAfter.ceeaIfListed').take()?.value == 'Y'}}","x-decorator":"FormItem","x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],disabled:expression("$form.readPretty")},"x-reactions":expression(`() => {
                const newData = $form.query('.companyBaseInfoAfter.ceeaListedTime').take()?.value
                const oldData = $form.query('.companyBaseInfoBefore.ceeaListedTime').take()?.value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),title:expression("$t('vendorMod.creationDate')"),"x-validator":{required:!0,message:i18nExpression("common.marketTime")}},listedExchange:{type:"string","x-visible":"{{$form.query('.companyBaseInfoAfter.ceeaIfListed').take().value == 'Y'}}","x-decorator":"FormItem","x-component-props":{disabled:expression("$form.readPretty")},title:expression("$t('vendorMod.listedExchange')"),"x-validator":{required:!0,message:i18nExpression("common.listingExchange")}},ceeaBusinessModel:{type:"string",title:i18nExpression("vendorMod.bizModel"),"x-component":"DictSelect","x-component-props":{code:"BIZ_MODEL"},"x-reactions":expression(`() => {
                const newData = $form.query('.companyTypeAfter.ceeaBusinessModel').take()?.value || null
                const oldData = $form.query('.companyTypeBefore.ceeaBusinessModel').take()?.value || null
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),"x-decorator":"FormItem"},companyCountry:{type:"string",title:i18nExpression("vendorMod.businessAddr"),"x-component":"DictSelect","x-reactions":expression(`() => {
                const newData = $form.query('.companyBaseInfoAfter.companyCountry').take().value
                const oldData = $form.query('.companyBaseInfoBefore.companyCountry').take().value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),"x-component-props":{code:"country",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$form.readPretty"),"@change":expression(`(val) => {
                // 选择国外就清理省市区，并且禁用
                if ($form.query('.companyBaseInfoAfter.companyCountry').take().value !== 'CN') {
                  $form.query('.companyBaseInfoAfter.companyProvince').take().value = ''
                  $form.query('.companyBaseInfoAfter.companyCity').take().value = ''
                }
              }`)},"x-decorator":"FormItem"},companyProvince:{type:"string",title:i18nExpression("vendorMod.province"),"x-component":"DictSelect","x-reactions":expression(`() => {
                const newData = $form.query('.companyBaseInfoAfter.companyProvince').take().value
                const oldData = $form.query('.companyBaseInfoBefore.companyProvince').take().value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),"x-component-props":{code:"PROVINCE","custom-select-type":"PROVINCE",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$form.readPretty || $form.query('.companyBaseInfoAfter.companyCountry').take().value !='CN'")},"x-decorator":"FormItem"},companyCity:{type:"string",title:i18nExpression("vendorMod.city"),"x-component":"DictSelect","x-reactions":expression(`() => {
                const newData = $form.query('.companyBaseInfoAfter.companyCity').take().value
                const oldData = $form.query('.companyBaseInfoBefore.companyCity').take().value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),"x-component-props":{code:expression("$form.query('.companyBaseInfoAfter.companyProvince').take().value || ''"),"custom-select-type":"CITY",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$form.readPretty || $form.query('.companyBaseInfoAfter.companyCountry').take().value !='CN'")},"x-decorator":"FormItem"},companyAddress:{type:"string",title:i18nExpression("components.address.detailAddress2"),"x-reactions":expression(`() => {
                const newData = $form.query('.enterpriseThreeCertificatesAfter.companyAddress').take()?.value
                const oldData = $form.query('.enterpriseThreeCertificatesBefore.companyAddress').take()?.value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),"x-disabled":expression("$form.readPretty"),"x-decorator":"FormItem"},ceeaHasParentCompany:{title:i18nExpression("vendorMod.ifParentCompany"),...radioGroupByYOrNSegment,"x-reactions":expression(`() => {
                const newData = $form.query('.companyBaseInfoAfter.ceeaHasParentCompany').take().value
                const oldData = $form.query('.companyBaseInfoBefore.ceeaHasParentCompany').take().value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),"x-component-props":{}},ceeaParentCompanyName:{type:"string","x-decorator":"FormItem","x-component":"Input","x-visible":"{{$form.query('.companyBaseInfoBefore.ceeaHasParentCompany').take().value == 'Y'}}","x-reactions":expression(`() => {
                const newData = $form.query('.companyBaseInfoAfter.ceeaParentCompanyName').take().value
                const oldData = $form.query('.companyBaseInfoBefore.ceeaParentCompanyName').take().value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),title:expression("$t('vendorMod.parentCompanyName')"),"x-component-props":{disabled:expression("$form.readPretty")},"x-validator":{required:!0,message:i18nExpression("vendorMod.msgPCompany")}},ceeaParentCompanyLcCode:{type:"string","x-decorator":"FormItem","x-component":"Input","x-visible":"{{$form.query('.companyBaseInfoBefore.ceeaHasParentCompany').take().value == 'Y'}}","x-reactions":expression(`() => {
                const newData = $form.query('.companyBaseInfoAfter.ceeaParentCompanyLcCode').take().value
                const oldData = $form.query('.companyBaseInfoBefore.ceeaParentCompanyLcCode').take().value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),title:expression("$t('vendorMod.parentCompanyLcCode')"),"x-component-props":{disabled:expression("$form.readPretty")},"x-validator":{required:!0,message:i18nExpression("cusEntry.supplement20250211.parentCompanyCreditCode")}},dunsCode:{type:"string","x-visible":expression("$form.query('.companyTypeAfter.overseasRelation').take().value != 'INSIDE'"),title:"D-U-N-S","x-reactions":expression(`() => {
                const newData = $form.query('.companyBaseInfoAfter.dunsCode').take().value
                const oldData = $form.query('.companyBaseInfoBefore.dunsCode').take().value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),"x-decorator":"FormItem"},ceeaCompanyIntro:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.companyProfile')"),"x-component-props":{disabled:expression("$form.readPretty")},"x-reactions":expression(`() => {
                const newData = $form.query('.companyBaseInfoAfter.ceeaCompanyIntro').take().value
                const oldData = $form.query('.companyBaseInfoBefore.ceeaCompanyIntro').take().value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),"x-decorator-props":{gridSpan:3},"x-validator":{required:!0,message:i18nExpression("vendorMod.msgCompanyProfile")}}}}}}},contactData={beforeChange:{type:"void","x-component":"div","x-component-props":{class:""},properties:{beforeChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.beforeChange"}},contactDataBefore:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!1,maxHeight:400,pagination:!1,sortable:!1},"x-query-engine-skip":!0,properties:generateXindexInOrder({contactName:{type:"string",title:i18nExpression("vendorMod.nickname"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:!0},"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},ceeaGender:{type:"string",title:i18nExpression("vendorMod.sex"),"x-render-table-column":{minWidth:100},"x-component":"DictSelect","x-component-props":{code:"GENDER",disabled:!0}},ceeaDeptName:{type:"string",title:i18nExpression("vendorMod.department"),"x-render-table-column":{minWidth:100},"x-component-props":{disabled:!0}},position:{type:"string",title:i18nExpression("dataConfMod.position"),"x-render-table-column":{minWidth:100},"x-component-props":{disabled:!0}},ceeaContactMethod:{type:"string",title:i18nExpression("vendorMod.contactMethod"),"x-render-table-column":{minWidth:100},"x-component-props":{disabled:!0}},email:{type:"string",title:i18nExpression("vendorMod.email"),"x-render-table-column":{minWidth:100},"x-component-props":{disabled:!0}},ceeaDefaultContact:{type:"string",title:i18nExpression("dataConfMod.isDefault"),"x-render-table-column":{minWidth:100},"x-component":"Checkbox","x-component-props":{"true-label":"Y","false-label":"N",disabled:!0}},ceeaComments:{type:"string",title:i18nExpression("dataConfMod.remark"),"x-render-table-column":{minWidth:100},"x-component-props":{disabled:!0}}})}}},afterChange:{type:"void","x-component":"div","x-component-props":{class:""},properties:{afterChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.afterChange"}},toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void",title:i18nExpression("common.add"),"x-component-props":{type:"primary","@click":expression(`() => {
                 $self.query('contactDataAfter')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
              }`)}}}},contactDataAfter:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,maxHeight:400,pagination:!1,sortable:!1},"x-query-engine-skip":!0,properties:generateXindexInOrder({contactName:{type:"string",title:i18nExpression("vendorMod.nickname"),"x-render-table-column":{minWidth:120},"x-reactions":expression(`() => {
                const oldData = $form.query('contactDataBefore').get('value')[$self.index]?.contactName || null
                let className = redFunction(oldData, $self?.value)
                $self.setComponentProps({ class: className })
            }`),"x-component-props":{disabled:expression("$form.readPretty")},"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},ceeaGender:{type:"string",title:i18nExpression("vendorMod.sex"),"x-render-table-column":{minWidth:100},"x-reactions":expression(`() => {
                const oldData = $form.query('contactDataBefore').get('value')[$self.index]?.ceeaGender || null
                let className = redFunction(oldData, $self?.value)
                $self.setComponentProps({ class: className })
            }`),"x-component":"DictSelect","x-component-props":{code:"GENDER",class:"",disabled:expression("$form.readPretty")}},ceeaDeptName:{type:"string",title:i18nExpression("vendorMod.department"),"x-render-table-column":{minWidth:100},"x-reactions":expression(`() => {
                const oldData = $form.query('contactDataBefore').get('value')[$self.index]?.ceeaDeptName || null
                let className = redFunction(oldData, $self?.value)
                $self.setComponentProps({ class: className })
            }`),"x-component-props":{disabled:expression("$form.readPretty")}},position:{type:"string",title:i18nExpression("dataConfMod.position"),"x-render-table-column":{minWidth:100},"x-reactions":expression(`() => {
                const oldData = $form.query('contactDataBefore').get('value')[$self.index]?.position || null
                let className = redFunction(oldData, $self?.value)
                $self.setComponentProps({ class: className })
            }`),"x-component-props":{disabled:expression("$form.readPretty")}},ceeaContactMethod:{type:"string",title:i18nExpression("vendorMod.contactMethod"),"x-render-table-column":{minWidth:100},"x-reactions":expression(`() => {
                const oldData = $form.query('contactDataBefore').get('value')[$self.index]?.ceeaContactMethod || null
                let className = redFunction(oldData, $self?.value)
                $self.setComponentProps({ class: className })
            }`),"x-component-props":{disabled:expression("$form.readPretty")}},email:{type:"string",title:i18nExpression("vendorMod.email"),"x-render-table-column":{minWidth:100},"x-reactions":expression(`() => {
                const oldData = $form.query('contactDataBefore').get('value')[$self.index]?.email || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),"x-component-props":{disabled:expression("$form.readPretty")}},ceeaDefaultContact:{type:"string",title:i18nExpression("dataConfMod.isDefault"),"x-render-table-column":{minWidth:100},"x-component":"Checkbox","x-reactions":expression(`() => {
                const oldData = $form.query('contactDataBefore').get('value')[$self.index]?.ceeaDefaultContact || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),"x-component-props":{"true-label":"Y","false-label":"N",disabled:expression("$form.readPretty")}},ceeaComments:{type:"string",title:i18nExpression("dataConfMod.remark"),"x-render-table-column":{minWidth:100},"x-reactions":expression(`() => {
                const oldData = $form.query('contactDataBefore').get('value')[$self.index]?.ceeaComments || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),"x-component-props":{disabled:expression("$form.readPretty")}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{disabled:expression("$form.readPretty"),type:"text","@click":expression(`({ row }) => {
                      $table.remove($self.index)
                  }`)}}}}})}}}},bankInfo={beforeChange:{type:"void","x-component":"div","x-component-props":{class:""},properties:{beforeChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.beforeChange"}},bankInfoBefore:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!1,maxHeight:400,pagination:!1,sortable:!1},"x-query-engine-skip":!0,properties:generateXindexInOrder({branchBankId:{type:"number","x-hidden":!0},bankCode:{type:"string",title:i18nExpression("components.bank.bankCode"),"x-render-table-column":{minWidth:120},"x-component":"QuickSearchWrapper","x-component-props":{showKey:"bankNum",propKey:"bankNum",name:"ceea_base_erp_branch_bank_info",disabled:!0,"@close-quicksearch":expression(`(val) => {
                let row = $table.getRowByIndex($self.index)
                row.branchBankId = val ? val.branchBankId : ''
                row.bankCode = val ? val.bankNum : ''
                row.bankName = val ? val.bankName : '' // 银行名称
                row.unionCode = val ? val.branchBankNum : '' // 分行编号
                row.openingBank = val ? val.branchBankName : '' // 分行名称[开户行名称]
              }
              `)},"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},bankName:{type:"string",title:i18nExpression("components.bank.bankName"),"x-render-table-column":{minWidth:120},"x-read-pretty":!0,"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},openingBank:{type:"string",title:i18nExpression("components.bank.branchBankName"),"x-render-table-column":{minWidth:120},"x-read-pretty":!0,"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},unionCode:{type:"string",title:i18nExpression("components.bank.unionCode"),"x-render-table-column":{minWidth:120},"x-read-pretty":!0,"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},bankAccountName:{type:"string",title:i18nExpression("components.bank.accountName"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:!0},"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},bankAccount:{type:"string",title:i18nExpression("components.bank.bankAccount"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:!0},"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},currencyCode:{type:"string",title:i18nExpression("vendorMod.currencyCode"),"x-render-table-column":{minWidth:120},"x-component":"DictSelect","x-component-props":{code:"currency",disabled:!0},"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},ceeaMainAccount:{type:"string",title:i18nExpression("components.bank.isMain"),"x-render-table-column":{minWidth:100},"x-component":"Checkbox","x-component-props":{"true-label":"Y","false-label":"N",disabled:!0}},ceeaEnabled:{type:"string",title:i18nExpression("components.bank.isActive"),"x-render-table-column":{minWidth:100},"x-component":"Checkbox","x-component-props":{"true-label":"Y","false-label":"N",disabled:!0}}})}}},afterChange:{type:"void","x-component":"div","x-component-props":{class:""},properties:{afterChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.afterChange"}},toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void",title:i18nExpression("common.add"),"x-component-props":{type:"primary","@click":expression(`() => {
                 $self.query('bankInfoAfter')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
              }`)}}}},bankInfoAfter:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,maxHeight:400,pagination:!1,sortable:!1,primaryKey:"bankInfoId",cascadeDeletion:!0},"x-query-engine-skip":!0,properties:generateXindexInOrder({branchBankId:{type:"number","x-hidden":!0},bankCode:{type:"string",title:i18nExpression("components.bank.bankCode"),"x-render-table-column":{minWidth:120},"x-component":"QuickSearchWrapper","x-reactions":expression(`() => {
                const oldData = $form.query('bankInfoBefore').get('value')[$self.index]?.bankCode || null
                let className = redFunction(oldData, $self?.value)
                $self.setComponentProps({ class: className })
            }`),"x-component-props":{showKey:"bankNum",propKey:"bankNum",name:"ceea_base_erp_branch_bank_info",disabled:expression("$form.readPretty"),preQueryData:expression("{'t.attr1': 'Y'}"),"@close-quicksearch":expression(`(val) => {
                let row = $table.getRowByIndex($self.index)
                row.branchBankId = val ? val.branchBankId : ''
                row.bankCode = val ? val.bankNum : ''
                row.bankName = val ? val.bankName : '' // 银行名称
                row.unionCode = val ? val.branchBankNum : '' // 分行编号
                row.openingBank = val ? val.branchBankName : '' // 分行名称[开户行名称]
              }
              `)},"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},bankName:{type:"string",title:i18nExpression("components.bank.bankName"),"x-render-table-column":{minWidth:120},"x-reactions":expression(`() => {
                const oldData = $form.query('bankInfoBefore').get('value')[$self.index]?.bankName || null
                let className = redFunction(oldData, $self?.value)
                $self.setComponentProps({ class: className })
            }`),"x-read-pretty":!0,"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},openingBank:{type:"string",title:i18nExpression("components.bank.branchBankName"),"x-render-table-column":{minWidth:120},"x-reactions":expression(`() => {
                const oldData = $form.query('bankInfoBefore').get('value')[$self.index]?.openingBank || null
                let className = redFunction(oldData, $self?.value)
                $self.setComponentProps({ class: className })
            }`),"x-read-pretty":!0,"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},unionCode:{type:"string",title:i18nExpression("components.bank.unionCode"),"x-render-table-column":{minWidth:120},"x-reactions":expression(`() => {
                const oldData = $form.query('bankInfoBefore').get('value')[$self.index]?.unionCode || null
                let className = redFunction(oldData, $self?.value)
                $self.setComponentProps({ class: className })
            }`),"x-read-pretty":!0,"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},bankAccountName:{type:"string",title:i18nExpression("components.bank.accountName"),"x-render-table-column":{minWidth:120},"x-reactions":expression(`() => {
                const oldData = $form.query('bankInfoBefore').get('value')[$self.index]?.bankAccountName || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),"x-component-props":{disabled:expression("$form.readPretty")},"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},bankAccount:{type:"string",title:i18nExpression("components.bank.bankAccount"),"x-render-table-column":{minWidth:120},"x-reactions":expression(`() => {
                const oldData = $form.query('bankInfoBefore').get('value')[$self.index]?.bankAccount || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),"x-component-props":{disabled:expression("$form.readPretty")},"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},currencyCode:{type:"string",title:i18nExpression("vendorMod.currencyCode"),"x-render-table-column":{minWidth:120},"x-component":"DictSelect","x-reactions":expression(`() => {
                const oldData = $form.query('bankInfoBefore').get('value')[$self.index]?.currencyCode || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),"x-component-props":{code:"currency",disabled:expression("$form.readPretty")},"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},ceeaMainAccount:{type:"string",title:i18nExpression("components.bank.isMain"),"x-render-table-column":{minWidth:100},"x-component":"Checkbox","x-reactions":expression(`() => {
                const oldData = $form.query('bankInfoBefore').get('value')[$self.index]?.ceeaMainAccount || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),"x-component-props":{"true-label":"Y","false-label":"N",disabled:expression("$form.readPretty")}},ceeaEnabled:{type:"string",title:i18nExpression("components.bank.isActive"),"x-render-table-column":{minWidth:100},"x-reactions":expression(`() => {
                const oldData = $form.query('bankInfoBefore').get('value')[$self.index]?.ceeaEnabled || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),"x-component":"Checkbox","x-component-props":{"true-label":"Y","false-label":"N",disabled:expression("$form.readPretty")}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{disabled:expression("$form.readPretty"),type:"text","@click":expression(`({ row }) => {
                      $table.remove($self.index)
                  }`)}}}}})}}}},siteInfos={beforeChange:{type:"void","x-component":"div","x-component-props":{class:""},properties:{beforeChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.beforeChange"}},siteInfosBefore:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!1,maxHeight:400,pagination:!1,sortable:!1},"x-query-engine-skip":!0,properties:generateXindexInOrder({belongOprId:{type:"number","x-hidden":!0},orgCode:{type:"number","x-hidden":!0},orgName:{type:"string","x-hidden":!0},orgId:{type:"number",title:i18nExpression("dataConfMod.orgId"),"x-render-table-column":{minWidth:120},"x-component":"OrganizationSelector","x-component-props":{"parent-id":-1,"node-type":"OU",placeholder:i18nExpression("common.pleaseSelect"),multiple:!1,disabled:!0,"read-pretty":!0,"@select":expression(`(node) => {
                let row = $table.getRowByIndex($self.index)
                row.orgId = node ? node.organizationId : null
                row.orgCode = node ? node.organizationCode : null
                row.orgName = node ? node.organizationName : null
                if (node) {
                  this.$http({
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
              }`)},"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},vendorSiteCode:{type:"string",title:i18nExpression("vendorMod.siteName"),"x-render-table-column":{minWidth:120},"x-component":"DictSelect","x-component-props":{code:"VENDOR_SITE_CODE",disabled:!0},"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},country:{type:"string",title:i18nExpression("components.address.country"),"x-render-table-column":{minWidth:120},"x-component":"DictSelect","x-component-props":{code:"country",placeholder:expression("$t('common.pleaseSelect')"),disabled:!0,"@change":expression(`(val) => {
                let row = $table.getRowByIndex($self.index)
                // 选择国外就清理省市区，并且禁用
                if (row.country !== 'CN') {
                  row.province = null
                  row.plantCity = null
                }
              }`)}},province:{type:"string",title:i18nExpression("components.address.area"),"x-render-table-column":{minWidth:120},"x-component":"DictSelect","x-component-props":{code:"PROVINCE","custom-select-type":"PROVINCE",placeholder:expression("$t('common.pleaseSelect')"),disabled:!0}},city:{type:"string",title:i18nExpression("components.address.city"),"x-render-table-column":{minWidth:120},"x-component":"DictSelect","x-component-props":{code:expression("$table.getRowByIndex($self.index).province"),"custom-select-type":"CITY",placeholder:expression("$t('common.pleaseSelect')"),disabled:!0}},addressDetail:{type:"string",title:i18nExpression("components.address.detailAddress"),"x-render-table-column":{minWidth:150},"x-component-props":{disabled:!0},"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},postCode:{type:"string",title:i18nExpression("components.address.postalCode"),"x-render-table-column":{minWidth:150},"x-component-props":{disabled:!0}},siteComment:{type:"string",title:i18nExpression("components.address.remark"),"x-render-table-column":{minWidth:150},"x-component-props":{disabled:!0}},enabledFlag:{type:"string",title:i18nExpression("common.enable"),"x-render-table-column":{minWidth:100},"x-component":"Checkbox","x-component-props":{"true-label":"Y","false-label":"N",disabled:!0}}})}}},afterChange:{type:"void","x-component":"div","x-component-props":{class:""},properties:{afterChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.afterChange"}},toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void",title:i18nExpression("common.add"),"x-component-props":{type:"primary","@click":expression(`() => {
                 $self.query('siteInfosAfter')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
              }`)}}}},siteInfosAfter:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,maxHeight:400,pagination:!1,sortable:!1},"x-query-engine-skip":!0,properties:generateXindexInOrder({belongOprId:{type:"number","x-hidden":!0},orgCode:{type:"number","x-hidden":!0},orgName:{type:"string","x-hidden":!0},orgId:{type:"number",title:i18nExpression("dataConfMod.orgId"),"x-render-table-column":{minWidth:120},"x-reactions":expression(`() => {
                const oldData = $form.query('siteInfosBefore').get('value')[$self.index]?.orgId || null
                let className = redFunction(oldData, $self?.value)
                $self.setComponentProps({ class: className })
            }`),"x-component":"OrganizationSelector","x-component-props":{"parent-id":-1,"node-type":"OU",placeholder:i18nExpression("common.pleaseSelect"),"read-pretty":expression("$form.readPretty"),multiple:!1,"@select":expression(`(node) => {
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
              }`)},"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},vendorSiteCode:{type:"string",title:i18nExpression("vendorMod.siteName"),"x-render-table-column":{minWidth:120},"x-reactions":expression(`() => {
                const oldData = $form.query('siteInfosBefore').get('value')[$self.index]?.vendorSiteCode || null
                let className = redFunction(oldData, $self?.value)
                $self.setComponentProps({ class: className })
            }`),"x-component":"DictSelect","x-component-props":{code:"VENDOR_SITE_CODE",disabled:expression("$form.readPretty")},"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},country:{type:"string",title:i18nExpression("components.address.country"),"x-render-table-column":{minWidth:120},"x-reactions":expression(`() => {
                const oldData = $form.query('siteInfosBefore').get('value')[$self.index]?.country || null
                let className = redFunction(oldData, $self?.value)
                $self.setComponentProps({ class: className })
            }`),"x-component":"DictSelect","x-component-props":{code:"country",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$form.readPretty"),"@change":expression(`(val) => {
                let row = $table.getRowByIndex($self.index)
                // 选择国外就清理省市区，并且禁用
                if (row.country !== 'CN') {
                  row.province = null
                  row.plantCity = null
                }
              }`)}},province:{type:"string",title:i18nExpression("components.address.area"),"x-render-table-column":{minWidth:120},"x-reactions":expression(`() => {
                const oldData = $form.query('siteInfosBefore').get('value')[$self.index]?.province || null
                let className = redFunction(oldData, $self?.value)
                $self.setComponentProps({ class: className })
            }`),"x-component":"DictSelect","x-component-props":{code:"PROVINCE","custom-select-type":"PROVINCE",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$form.readPretty || $table.getRowByIndex($self.index).country!='CN'")}},city:{type:"string",title:i18nExpression("components.address.city"),"x-render-table-column":{minWidth:120},"x-reactions":expression(`() => {
                const oldData = $form.query('siteInfosBefore').get('value')[$self.index]?.city || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),"x-component":"DictSelect","x-component-props":{code:expression("$table.getRowByIndex($self.index).province"),"custom-select-type":"CITY",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$form.readPretty || $table.getRowByIndex($self.index).country!='CN'")}},addressDetail:{type:"string",title:i18nExpression("components.address.detailAddress"),"x-render-table-column":{minWidth:150},"x-reactions":expression(`() => {
                const oldData = $form.query('siteInfosBefore').get('value')[$self.index]?.addressDetail || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),"x-component-props":{disabled:expression("$form.readPretty")},"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},postCode:{type:"string",title:i18nExpression("components.address.postalCode"),"x-render-table-column":{minWidth:150},"x-reactions":expression(`() => {
                const oldData = $form.query('siteInfosBefore').get('value')[$self.index]?.postCode || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),"x-component-props":{disabled:expression("$form.readPretty")}},siteComment:{type:"string",title:i18nExpression("components.address.remark"),"x-render-table-column":{minWidth:150},"x-reactions":expression(`() => {
                const oldData = $form.query('siteInfosBefore').get('value')[$self.index]?.siteComment || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),"x-component-props":{disabled:expression("$form.readPretty")}},enabledFlag:{type:"string",title:i18nExpression("common.enable"),"x-render-table-column":{minWidth:100},"x-reactions":expression(`() => {
                const oldData = $form.query('siteInfosBefore').get('value')[$self.index]?.enabledFlag || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),"x-component":"Checkbox","x-component-props":{"true-label":"Y","false-label":"N",disabled:expression("$form.readPretty")}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{disabled:expression("$form.readPretty"),type:"text","@click":expression(`({ row }) => {
                      $table.remove($self.index)
                  }`)}}}}})}}}},sceneAttachmentInfo={beforeChange:{type:"void","x-component":"div","x-component-props":{class:""},properties:{beforeChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.beforeChange"}},sceneAttachmentInfoBefore:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!1,maxHeight:400,pagination:!1,sortable:!1},"x-query-engine-skip":!0,properties:generateXindexInOrder({documentInspection:{type:"string",title:i18nExpression("vendorMod.certificateRequirements"),"x-render-table-column":{minWidth:120},"x-read-pretty":!0,"x-component-props":{disabled:!0}},authType:{type:"string",title:i18nExpression("vendorMod.authType"),"x-render-table-column":{minWidth:120},"x-component":"SrmCommonFile","x-component-props":{disabled:!0,extraData:{fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},defaultFile:{fileId:expression("$table.getRowByIndex($self.index)?.fileuploadId"),fileName:expression("$self?.value")},"validate-options":{accept:["jpg","png","jpeg"]},readonly:!1,"@on-change":expression(`({file}) => {
                const { fileId = '', fileName = '' } = file || {}
                let row = $table.getRowByIndex($self.index)
                row.fileuploadId = fileId.toString()
                row.authType = fileName
              }`)}},authDescription:{type:"string",title:i18nExpression("vendorMod.authDesc"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:!0}},authNum:{type:"string",title:i18nExpression("vendorMod.authNum"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:!0,"@change":expression(`() => {
                let row = $table.getRowByIndex($self.index)
                row.authNum = row.authNum.replace(/[\\W]/g, '')
              }`)}},authDate:{...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.authDate, '{y}-{m}-{d}')
              }`),disabled:!0},title:i18nExpression("vendorMod.authDate"),"x-render-table-column":{minWidth:210}},authOrg:{type:"string",title:i18nExpression("vendorMod.authOrg"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:!0}},endDate:{...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.endDate, '{y}-{m}-{d}')
              }`),disabled:!0},title:i18nExpression("vendorMod.certUntil"),"x-render-table-column":{minWidth:210}}})}}},afterChange:{type:"void","x-component":"div","x-component-props":{class:""},properties:{afterChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.afterChange"}},toolbar:{type:"void","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void",title:i18nExpression("common.add"),"x-component-props":{type:"primary","@click":expression(`() => {
                 $self.query('sceneAttachmentInfoAfter')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
              }`)}}}},sceneAttachmentInfoAfter:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,maxHeight:400,pagination:!1,sortable:!1},"x-query-engine-skip":!0,properties:generateXindexInOrder({documentInspection:{type:"string",title:i18nExpression("vendorMod.certificateRequirements"),"x-render-table-column":{minWidth:120},"x-reactions":expression(`() => {
                const oldData = $form.query('sceneAttachmentInfoBefore').get('value')[$self.index]?.documentInspection || null
                let className = redFunction(oldData, $self?.value)
                $self.setComponentProps({ class: className })
            }`),"x-read-pretty":!0,"x-component-props":{disabled:expression("$form.readPretty")}},authType:{type:"string",title:i18nExpression("vendorMod.authType"),"x-render-table-column":{minWidth:120},"x-reactions":expression(`() => {
                const oldData = $form.query('sceneAttachmentInfoBefore').get('value')[$self.index]?.authType || null
                let className = redFunction(oldData, $self?.value)
                $self.setComponentProps({ class: className })
            }`),"x-component":"SrmCommonFile","x-component-props":{disabled:expression("$form.readPretty"),extraData:{fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},defaultFile:{fileId:expression("$table.getRowByIndex($self.index)?.fileuploadId"),fileName:expression("$self?.value")},"validate-options":{accept:["jpg","png","jpeg"]},readonly:!1,"@on-change":expression(`({file}) => {
                const { fileId = '', fileName = '' } = file || {}
                let row = $table.getRowByIndex($self.index)
                row.fileuploadId = fileId.toString()
                row.authType = fileName
              }`)}},authDescription:{type:"string",title:i18nExpression("vendorMod.authDesc"),"x-render-table-column":{minWidth:120},"x-reactions":expression(`() => {
                const oldData = $form.query('sceneAttachmentInfoBefore').get('value')[$self.index]?.authDescription || null
                let className = redFunction(oldData, $self?.value)
                $self.setComponentProps({ class: className })
            }`),"x-component-props":{disabled:expression("$form.readPretty")}},authNum:{type:"string",title:i18nExpression("vendorMod.authNum"),"x-render-table-column":{minWidth:120},"x-reactions":expression(`() => {
                const oldData = $form.query('sceneAttachmentInfoBefore').get('value')[$self.index]?.authNum || null
                let className = redFunction(oldData, $self?.value)
                $self.setComponentProps({ class: className })
            }`),"x-component-props":{disabled:expression("$form.readPretty"),"@change":expression(`() => {
                let row = $table.getRowByIndex($self.index)
                row.authNum = row.authNum.replace(/[\\W]/g, '')
              }`)}},authDate:{...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.authDate, '{y}-{m}-{d}')
              }`),disabled:expression("$form.readPretty")},title:i18nExpression("vendorMod.authDate"),"x-render-table-column":{minWidth:220},"x-reactions":expression(`() => {
                const oldData = $form.query('sceneAttachmentInfoBefore').get('value')[$self.index]?.authDate || null
                let className = redFunction(oldData, app.$dayjs($self?.value).format('YYYY-MM-DD'))
                $self.setComponentProps({ class: className })
            }`)},authOrg:{type:"string",title:i18nExpression("vendorMod.authOrg"),"x-render-table-column":{minWidth:120},"x-reactions":expression(`() => {
                const oldData = $form.query('sceneAttachmentInfoBefore').get('value')[$self.index]?.authOrg || null
                let className = redFunction(oldData, $self?.value)
                $self.setComponentProps({ class: className })
            }`),"x-component-props":{disabled:expression("$form.readPretty")}},endDate:{...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.endDate, '{y}-{m}-{d}')
              }`),disabled:expression("$form.readPretty")},title:i18nExpression("vendorMod.certUntil"),"x-render-table-column":{minWidth:220},"x-reactions":expression(`() => {
                const oldData = $form.query('sceneAttachmentInfoBefore').get('value')[$self.index]?.endDate || null
                let className = redFunction(oldData, app.$dayjs($self?.value).format('YYYY-MM-DD'))
                $self.setComponentProps({ class: className })
            }`)},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{disabled:expression("$form.readPretty"),type:"text","@click":expression(`({ row }) => {
                      $table.remove($self.index)
                  }`)}}}}})}}}},attachFile={beforeChange:{type:"void","x-component":"div","x-component-props":{class:""},properties:{beforeChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.beforeChange"}},attachFileBefore:{type:"array","x-component":"FileDynamic","x-component-props":{"scene-module-code":"SCENE_SUPPLIER_ATTACHMENT",businessId:expression("$attrs.params.companyId || null"),editable:!1,"need-init":!1}}}},afterChange:{type:"void","x-component":"div","x-component-props":{class:""},properties:{afterChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.afterChange"}},attachFileAfter:{type:"array","x-component":"FileDynamic","x-component-props":{"scene-module-code":"SCENE_SUPPLIER_ATTACHMENT",businessId:expression("$attrs.params?.changeId"),editable:expression("$attrs.params.flag != 'view'"),"need-init":!0}}}}};export{companyBaseInfo as a,contactData as b,companyType as c,bankInfo as d,enterpriseThreeCertificates as e,sceneAttachmentInfo as f,attachFile as g,siteInfos as s};
