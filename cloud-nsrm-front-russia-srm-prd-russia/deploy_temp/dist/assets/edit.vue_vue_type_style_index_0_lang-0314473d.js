import{cc as formGridSegment,af as i18nExpression,ae as expression,aC as requiredValidatorSegment,ah as radioGroupByYOrNSegment,ai as generateXindexInOrder,aB as generateCharExpressionByFunction,aj as editTableFormItemValid}from"./index-17d0ccd5.js";const formMain={type:"object","x-query-engine-skip":!0,...formGridSegment,properties:{changeId:{type:"number","x-hidden":!0,"x-decorator":"FormItem"},changeStatus:{type:"string","x-hidden":!0,"x-decorator":"FormItem"},changeApplyNo:{type:"string",title:i18nExpression("vendorMod.changeApplyNo"),"x-component-props":{disabled:!0},"x-decorator":"FormItem"},companyId:{type:"number","x-hidden":!0},companyCode:{type:"string","x-hidden":!0},companyName:{type:"string",title:i18nExpression("common.vendorName"),"x-component":"QuickSearchWrapper","x-component-props":{showKey:"companyName",name:"scc_sup_company_info_display_buyer",disabled:expression("!['',null,undefined].includes($attrs.params.row?.changeStatus || $form.values?.changeStatus)"),"read-pretty":"{{$form.readPretty}}","@close-quicksearch":expression(`(val) => {
          $form.query('state').get('data').companyId = val ? val.companyId : null
          $form.query('.form.companyId').take().value = val ? val.companyId : null
          $form.query('.form.companyCode').take().value = val ? val.companyCode : ''
          console.log(val, 'val')
          if (val?.companyId) {
            fatchCompanyData(val.companyId, $form) // 查询公司信息
            fatchCompanyData(val.companyId, $form) // 查询公司信息
          } else {
            $form.query('state').get('data').userType = ''
          }
        }`)},"x-decorator":"FormItem","x-validator":{required:!0,message:i18nExpression("common.requiredField")}},noticeById:{type:"string","x-hidden":!0,"x-decorator":"FormItem"},changeFileId:{type:"string","x-hidden":!0,"x-decorator":"FormItem"},changeFileName:{type:"string","x-decorator":"FormItem","x-hidden":!0},changeExplain:{type:"string",title:i18nExpression("vendorMod.changeExplain"),"x-component":"Input.TextArea","x-component-props":{autosize:{minRows:3,maxRows:4}},"x-decorator":"FormItem","x-decorator-props":{gridSpan:4},"x-validator":{required:!0,message:i18nExpression("common.requiredField")}}}},companyType={beforeChange:{type:"void","x-component":"div","x-component-props":{class:"formClassAllChange"},properties:{beforeChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.beforeChange"}},companyTypeBefore:{type:"object","x-query-engine-skip":!0,"x-component":"FormGrid","x-component-props":{class:"forms"},properties:{overseasRelation:{type:"string",title:i18nExpression("vendorMod.overseasRelation"),"x-component":"DictSelect","x-component-props":{code:"RELATION_NEW",disabled:!0},"x-decorator":"FormItem"},companyType:{type:"string",title:i18nExpression("cusEntry.vendorMod.vendorType"),"x-component-props":{disabled:!0},"x-decorator":"FormItem"},extUseType:{type:"string",title:i18nExpression("cusEntry.vendorMod.extUseType"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"SUPPLIER_USE",disabled:!0}}}}}},afterChange:{type:"void","x-component":"div","x-component-props":{class:"formClassAllChange"},properties:{afterChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.afterChange"}},companyTypeAfter:{type:"object","x-query-engine-skip":!0,"x-component":"FormGrid","x-component-props":{class:"forms"},properties:{overseasRelation:{type:"string",title:i18nExpression("vendorMod.overseasRelation"),"x-component":"DictSelect","x-component-props":{code:"RELATION_NEW",disabled:!0},"x-reactions":expression(`() => {
                const newData = $form.query('.companyTypeAfter.overseasRelation')?.take()?.value || null
                const oldData = $form.query('.companyTypeBefore.overseasRelation')?.take()?.value || null
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),"x-decorator":"FormItem",...requiredValidatorSegment},companyType:{type:"string",title:i18nExpression("cusEntry.vendorMod.vendorType"),"x-reactions":expression(`() => {
                const newData = $form.query('.companyTypeAfter.companyType')?.take()?.value || null
                const oldData = $form.query('.companyTypeBefore.companyType')?.take()?.value || null
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),"x-decorator":"FormItem",...requiredValidatorSegment},extUseType:{type:"string",title:i18nExpression("cusEntry.vendorMod.extUseType"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"SUPPLIER_USE"},"x-reactions":expression(`() => {
              const newData = $form.query('.companyTypeAfter.extUseType')?.take()?.value || null
              const oldData = $form.query('.companyTypeBefore.extUseType')?.take()?.value || null
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`)}}}}}},enterpriseThreeCertificates={beforeChange:{type:"void","x-component":"div","x-component-props":{class:"formClassAllChange"},properties:{beforeChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.beforeChange"}},enterpriseThreeCertificatesBefore:{type:"object","x-query-engine-skip":!0,"x-component":"FormGrid","x-component-props":{class:"forms"},properties:{businessLicenseFileId:{type:"string","x-hidden":!0},businessLicense:{type:"string","x-hidden":!0},businessLicenseFile:{type:"string",title:i18nExpression("vendorMod.businessLicense"),"x-component":"SrmCommonFile","x-component-props":{disabled:!0,"extra-data":{uploadType:"DEF",sourceType:"WEB_APP",fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},"default-file":{fileId:expression("$form.query('.enterpriseThreeCertificatesBefore.businessLicenseFileId').take()?.value"),fileName:expression("$form.query('.enterpriseThreeCertificatesBefore.businessLicense').take()?.value")}},"x-decorator":"FormItem"},companyName:{type:"string",title:i18nExpression("vendorMod.companyName"),"x-component-props":{disabled:!0},"x-decorator":"FormItem"},legalPerson:{type:"string",title:i18nExpression("vendorMod.legalPerson"),"x-component-props":{disabled:!0},"x-decorator":"FormItem"},lcCode:{type:"string",title:i18nExpression("vendorMod.lcCode"),"x-disabled":!0,"x-decorator":"FormItem"},registCurrency:{type:"string","x-hidden":!0,"x-decorator":"FormItem"},registeredCapital:{type:"number","x-visible":expression("$form.query('.companyTypeAfter.companyType').take()?.value != 'GETI'"),"x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.registeredCapital')"),"x-component-props":{disabled:!0,class:"input-with-select"},"x-content":{append:expression(`observer({
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
              })`)}},companyCreationDate:{type:"date",title:i18nExpression("vendorMod.creationDate"),"x-disabled":!0,"x-decorator":"FormItem"},ifLongPeriod:{type:"string",title:i18nExpression("cusEntry.vendorMod.ifLongTermSupplier"),"x-disabled":!0,"x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"},"x-decorator":"FormItem"},businessStartDate:{type:"date",title:i18nExpression("vendorMod.businessStartFrom"),"x-disabled":!0,"x-decorator":"FormItem"},businessEndDate:{type:"date",title:i18nExpression("vendorMod.businessEndAt"),"x-disabled":!0,"x-decorator":"FormItem"},companyShortName:{type:"string",title:i18nExpression("vendorMod.companyShortName"),"x-disabled":!0,"x-decorator":"FormItem","x-component-props":{maxlength:100,"show-word-limit":!0}},companyEnName:{type:"string",title:i18nExpression("cusEntry.vendorMod.companyEnName"),"x-disabled":!0,"x-decorator":"FormItem"},businessScope:{type:"string",title:i18nExpression("vendorMod.businessScope"),"x-component-props":{type:"textarea",maxlength:2e3},"x-disabled":!0,"x-decorator":"FormItem"}}}}},afterChange:{type:"void","x-component":"div","x-component-props":{class:"formClassAllChange"},properties:{afterChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.afterChange"}},enterpriseThreeCertificatesAfter:{type:"object","x-query-engine-skip":!0,"x-component":"FormGrid","x-component-props":{class:"forms"},properties:{businessLicenseFileId:{type:"string","x-hidden":!0},businessLicense:{type:"string",title:i18nExpression("vendorMod.businessLicense"),"x-component":"SrmCommonFile","x-component-props":{disabled:expression("$form.readPretty"),"extra-data":{uploadType:"DEF",sourceType:"WEB_APP",fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},"default-file":{fileId:expression("$form.query('.enterpriseThreeCertificatesAfter.businessLicenseFileId').take()?.value"),fileName:expression("$form.query('.enterpriseThreeCertificatesAfter.businessLicense').take()?.value")},"@on-change":expression(`(file) => {
                if (file) {
                  const { fileId, fileName } = file.file || {}
                  $form.query('.enterpriseThreeCertificatesAfter.businessLicenseFileId').take().value = fileId.toString()
                  $form.query('.enterpriseThreeCertificatesAfter.businessLicense').take().value = fileName
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
                    let form = $form.query('enterpriseThreeCertificatesAfter').get('value')
                    form.companyName = name
                    form.companyType = type
                    form.legalPerson = person
                    // form.lcCode = regNum
                    form.businessStartDate = businessStartDate
                    form.businessEndDate = businessEndDate
                    form.companyAddress = address
                    form.businessScope = business
                    const [year, month, day] = setDate.replace(/[^\\d]/g, '-').split('-')
                    const createDate = year + '-' + month + '-' + day
                    form.companyCreationDate = app.$dayjs(createDate).format('YYYY-MM-DD')
                  })
                  .catch(err => {
                    console.log(err)
                  })
                } else {
                  $form.query('.enterpriseThreeCertificatesAfter.businessLicenseFileId').take().value = null
                  $form.query('.enterpriseThreeCertificatesAfter.businessLicense').take().value = null
                }
              }`)},"x-decorator":"FormItem","x-reactions":expression(`() => {
              const newData = $form.query('.enterpriseThreeCertificatesAfter.businessLicenseFileId').take().value
              const oldData = $form.query('.enterpriseThreeCertificatesBefore.businessLicenseFileId').take().value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`),...requiredValidatorSegment},companyName:{type:"string",title:i18nExpression("vendorMod.companyName"),"x-component-props":{disabled:expression("$form.readPretty")},"x-reactions":expression(`() => {
                const newData = $form.query('.enterpriseThreeCertificatesAfter.companyName').take()?.value
                const oldData = $form.query('.enterpriseThreeCertificatesBefore.companyName').take()?.value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),"x-decorator":"FormItem",...requiredValidatorSegment},legalPerson:{type:"string",title:i18nExpression("vendorMod.legalPerson"),"x-component-props":{disabled:expression("$form.readPretty")},"x-reactions":expression(`() => {
                const newData = $form.query('.enterpriseThreeCertificatesAfter.legalPerson').take()?.value
                const oldData = $form.query('.enterpriseThreeCertificatesBefore.legalPerson').take()?.value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),"x-decorator":"FormItem",...requiredValidatorSegment},lcCode:{type:"string",title:i18nExpression("vendorMod.lcCode"),"x-reactions":expression(`() => {
                const newData = $form.query('.enterpriseThreeCertificatesAfter.lcCode').take().value
                const oldData = $form.query('.enterpriseThreeCertificatesBefore.lcCode').take().value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),"x-disabled":!0,"x-decorator":"FormItem",...requiredValidatorSegment},registCurrency:{type:"string","x-hidden":!0},registeredCapital:{type:"number","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.registeredCapital')"),"x-component-props":{disabled:expression("$form.readPretty"),class:"input-with-select","@change":expression(`(value) => {
                $self.value = value.replace(/[^\\d.]/g, '')
              }`)},"x-reactions":expression(`() => {
                const newData = $form.query('.enterpriseThreeCertificatesAfter.registeredCapital').take()?.value
                const oldData = $form.query('.enterpriseThreeCertificatesBefore.registeredCapital').take()?.value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),"x-content":{append:expression(`observer({
                render: (h) => {
                  const targetField = $form.query('.enterpriseThreeCertificatesAfter.registCurrency').take()
                  const beforeValue = $form.query('.enterpriseThreeCertificatesBefore.registCurrency').take().value
                  let className = redFunction(beforeValue, targetField.value)
                  return h(
                    $self.readPretty ? $$components.DictSelectPreview : $$components.DictSelect,
                    {
                      [$self.readPretty ? 'props' : 'attrs']: {
                        code: 'currency',
                        value: targetField.value,
                        class: className
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
              })`)},"x-validator":{required:expression("!['GETI','FEIYINGLI'].includes($form.query('.companyTypeAfter.companyType').take().value)"),message:i18nExpression("vendorMod.msgRegisteredCapital")}},companyCreationDate:{type:"date",title:i18nExpression("vendorMod.creationDate"),"x-disabled":expression("$form.readPretty"),"x-reactions":expression(`() => {
                const newData = $form.query('.enterpriseThreeCertificatesAfter.companyCreationDate').take().value
                const oldData = $form.query('.enterpriseThreeCertificatesBefore.companyCreationDate').take().value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),"x-decorator":"FormItem",...requiredValidatorSegment},ifLongPeriod:{type:"string",title:i18nExpression("cusEntry.vendorMod.ifLongTermSupplier"),"x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"},"x-reactions":expression(`() => {
              const newData = $form.query('.enterpriseThreeCertificatesAfter.ifLongPeriod').take().value
              const oldData = $form.query('.enterpriseThreeCertificatesBefore.ifLongPeriod').take().value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`),"x-decorator":"FormItem",...requiredValidatorSegment},businessStartDate:{type:"date",title:i18nExpression("vendorMod.businessStartFrom"),"x-reactions":expression(`() => {
                const newData = $form.query('.enterpriseThreeCertificatesAfter.businessStartDate').take().value
                const oldData = $form.query('.enterpriseThreeCertificatesBefore.businessStartDate').take().value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),"x-disabled":expression("$form.readPretty"),"x-decorator":"FormItem"},businessEndDate:{type:"date",title:i18nExpression("vendorMod.businessEndAt"),"x-reactions":expression(`() => {
              const newData = $form.query('.enterpriseThreeCertificatesAfter.businessEndDate').take().value
              const oldData = $form.query('.enterpriseThreeCertificatesBefore.businessEndDate').take().value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`),"x-disabled":expression("$form.readPretty"),"x-decorator":"FormItem"},companyShortName:{type:"string",title:i18nExpression("vendorMod.companyShortName"),"x-disabled":expression("$form.readPretty"),"x-reactions":expression(`() => {
                const newData = $form.query('.enterpriseThreeCertificatesAfter.companyShortName').take().value
                const oldData = $form.query('.enterpriseThreeCertificatesBefore.companyShortName').take().value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),"x-component-props":{maxlength:100,"show-word-limit":!0},"x-decorator":"FormItem"},companyEnName:{type:"string",title:i18nExpression("cusEntry.vendorMod.companyEnName"),"x-decorator":"FormItem","x-reactions":expression(`() => {
              const newData = $form.query('.enterpriseThreeCertificatesAfter.companyEnName').take().value
              const oldData = $form.query('.enterpriseThreeCertificatesBefore.companyEnName').take().value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`)},businessScope:{type:"string",title:i18nExpression("vendorMod.businessScope"),"x-reactions":expression(`() => {
              const newData = $form.query('.enterpriseThreeCertificatesAfter.businessScope').take().value
              const oldData = $form.query('.enterpriseThreeCertificatesBefore.businessScope').take().value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`),"x-disabled":expression("$form.readPretty"),"x-decorator":"FormItem","x-component-props":{type:"textarea",maxlength:2e3},...requiredValidatorSegment}}}}}},companyBaseInfo={beforeChange:{type:"void","x-component":"div","x-component-props":{class:"formClassAllChange"},properties:{beforeChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.beforeChange"}},companyBaseInfoBefore:{type:"object","x-query-engine-skip":!0,"x-component":"FormGrid","x-component-props":{class:"forms"},properties:{ceeaBusinessModel:{type:"string",title:i18nExpression("vendorMod.bizModel"),"x-component":"DictSelect","x-component-props":{code:"BIZ_MODEL",disabled:!0,multiple:!0},"x-decorator":"FormItem"},ceeaIfListed:{title:i18nExpression("vendorMod.ifListed"),...radioGroupByYOrNSegment,"x-component-props":{disabled:!0}},ceeaListedTime:{type:"date",default:null,"x-visible":"{{$form.query('.companyBaseInfoBefore.ceeaIfListed').take()?.value == 'Y'}}","x-decorator":"FormItem","x-component-props":{placeholder:i18nExpression("common.pleaseSelectDate"),format:"yyyy-MM-dd","value-format":"yyyy-MM-dd",disabled:!0},title:expression("$t('vendorMod.listedDate')"),"x-validator":{required:!0,message:i18nExpression("请选择上市时间")}},companyCountry:{type:"string",title:i18nExpression("components.address.country"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"country",placeholder:expression("$t('common.pleaseSelect')"),disabled:!0}},companyProvince:{type:"string",title:i18nExpression("components.address.area"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"PROVINCE","custom-select-type":"PROVINCE",placeholder:expression("$t('common.pleaseSelect')"),disabled:!0}},companyCity:{type:"string",title:i18nExpression("components.address.city"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:expression("$form.query('.companyBaseInfoBefore.companyProvince').take()?.value"),"custom-select-type":"CITY",placeholder:expression("$t('common.pleaseSelect')"),disabled:!0}},companyAddress:{type:"string",title:i18nExpression("cusEntry.vendorMod.detailAddress"),"x-disabled":!0,"x-decorator":"FormItem"},ceeaHasParentCompany:{title:i18nExpression("cusEntry.vendorMod.ifParentCompany"),...radioGroupByYOrNSegment,"x-component-props":{disabled:!0}},ceeaParentCompanyName:{type:"string","x-decorator":"FormItem","x-component":"Input","x-visible":"{{$form.query('.companyBaseInfoBefore.ceeaHasParentCompany').take()?.value == 'Y'}}",title:i18nExpression("cusEntry.vendorMod.parentCompanyName"),"x-component-props":{disabled:!0},"x-validator":{required:!0,message:i18nExpression("请输入集团名称")}},groupCountry:{type:"string",title:i18nExpression("cusEntry.vendorMod.parentCompanyCountry"),"x-visible":"{{$form.query('.companyBaseInfoBefore.ceeaHasParentCompany').take().value == 'Y'}}","x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"country",placeholder:expression("$t('common.pleaseSelect')"),disabled:!0},"x-validator":{required:!0,message:i18nExpression("cusEntry.tipMessage.parentCompanyCountryMsg")}},ceeaCompanyIntro:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.companyProfile')"),"x-component-props":{disabled:!0,type:"textarea",maxlength:2e3},"x-decorator-props":{gridSpan:3}}}}}},afterChange:{type:"void","x-component":"div","x-component-props":{class:"formClassAllChange"},properties:{afterChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.afterChange"}},companyBaseInfoAfter:{type:"object","x-query-engine-skip":!0,"x-component":"FormGrid","x-component-props":{class:"forms"},properties:{ceeaBusinessModel:{type:"string",title:i18nExpression("vendorMod.bizModel"),"x-component":"DictSelect","x-component-props":{code:"BIZ_MODEL",multiple:!0},"x-reactions":expression(`() => {
              const newData = $form.query('companyBaseInfoAfter.ceeaBusinessModel').take()?.value || []
              const oldData = $form.query('companyBaseInfoBefore.ceeaBusinessModel').take()?.value || []
              let className = ''
              const diff = new Set([...newData, ...oldData])
              if (diff.size !== oldData.length) {
                className = 'redColorFont'
              }
              $self.setComponentProps({ class: className })
            }`),"x-decorator":"FormItem"},ceeaIfListed:{title:i18nExpression("vendorMod.ifListed"),...radioGroupByYOrNSegment,"x-reactions":expression(`() => {
                const newData = $form.query('.companyBaseInfoAfter.ceeaIfListed').take()?.value
                const oldData = $form.query('.companyBaseInfoBefore.ceeaIfListed').take()?.value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),"x-component-props":{}},ceeaListedTime:{type:"date",default:null,"x-visible":"{{$form.query('.companyBaseInfoAfter.ceeaIfListed').take()?.value == 'Y'}}","x-decorator":"FormItem","x-component-props":{placeholder:i18nExpression("common.pleaseSelectDate"),format:"yyyy-MM-dd","value-format":"yyyy-MM-dd",disabled:expression("$form.readPretty")},"x-reactions":expression(`() => {
                const newData = $form.query('.companyBaseInfoAfter.ceeaListedTime').take()?.value
                const oldData = $form.query('.companyBaseInfoBefore.ceeaListedTime').take()?.value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),title:expression("$t('vendorMod.listedDate')"),"x-validator":{required:!0,message:i18nExpression("请选择上市时间")}},companyCountry:{type:"string",title:i18nExpression("components.address.country"),"x-component":"DictSelect","x-reactions":expression(`() => {
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
              }`)},"x-decorator":"FormItem"},companyProvince:{type:"string",title:i18nExpression("components.address.area"),"x-component":"DictSelect","x-reactions":expression(`() => {
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
                const newData = $form.query('.companyBaseInfoAfter.companyAddress').take()?.value
                const oldData = $form.query('.companyBaseInfoBefore.companyAddress').take()?.value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),"x-disabled":expression("$form.readPretty"),"x-decorator":"FormItem"},ceeaHasParentCompany:{title:i18nExpression("cusEntry.vendorMod.ifParentCompany"),...radioGroupByYOrNSegment,"x-reactions":expression(`() => {
                const newData = $form.query('.companyBaseInfoAfter.ceeaHasParentCompany').take().value
                const oldData = $form.query('.companyBaseInfoBefore.ceeaHasParentCompany').take().value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),"x-component-props":{}},ceeaParentCompanyName:{type:"string","x-decorator":"FormItem","x-component":"Input","x-visible":"{{$form.query('.companyBaseInfoAfter.ceeaHasParentCompany').take().value == 'Y'}}","x-reactions":expression(`() => {
                const newData = $form.query('.companyBaseInfoAfter.ceeaParentCompanyName').take().value
                const oldData = $form.query('.companyBaseInfoBefore.ceeaParentCompanyName').take().value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),title:i18nExpression("cusEntry.vendorMod.parentCompanyName"),"x-component-props":{disabled:expression("$form.readPretty")},"x-validator":{required:!0,message:i18nExpression("请输入集团名称")}},groupCountry:{type:"string",title:i18nExpression("cusEntry.vendorMod.parentCompanyCountry"),"x-visible":"{{$form.query('.companyBaseInfoAfter.ceeaHasParentCompany').take().value == 'Y'}}","x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"country",placeholder:expression("$t('common.pleaseSelect')")},"x-reactions":expression(`() => {
              const newData = $form.query('.companyBaseInfoAfter.groupCountry').take().value
              const oldData = $form.query('.companyBaseInfoBefore.groupCountry').take().value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`),"x-validator":{required:!0,message:i18nExpression("cusEntry.tipMessage.parentCompanyCountryMsg")}},ceeaCompanyIntro:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.companyProfile')"),"x-component-props":{disabled:expression("$form.readPretty"),type:"textarea",maxlength:2e3},"x-reactions":expression(`() => {
                const newData = $form.query('.companyBaseInfoAfter.ceeaCompanyIntro').take().value
                const oldData = $form.query('.companyBaseInfoBefore.ceeaCompanyIntro').take().value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),"x-decorator-props":{gridSpan:3}}}}}}},personBaseInfo={beforeChange:{type:"void","x-component":"div","x-component-props":{class:"formClassAllChange"},properties:{beforeChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.beforeChange"}},personBaseInfoBefore:{type:"object","x-query-engine-skip":!0,"x-component":"FormGrid","x-component-props":{class:"forms"},properties:{businessLicenseFileId:{type:"string",title:i18nExpression("cusEntry.vendorMod.frontOfIdCard"),"x-decorator":"FormItem","x-component":"SrmCommonFile","x-component-props":{"extra-data":{uploadType:"DEF",sourceType:"WEB_APP",fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},"default-file":{fileId:expression("$form.query('.personBaseInfoBefore').get('value').businessLicenseFileId"),fileName:expression("$form.query('.personBaseInfoBefore').get('value').businessLicense")},readonly:!0},...requiredValidatorSegment},extIdCardOppositeFileName:{type:"string","x-hidden":!0},extIdCardOppositeFileId:{type:"string",title:i18nExpression("cusEntry.vendorMod.backOfIdCard"),"x-decorator":"FormItem","x-component":"SrmCommonFile","x-component-props":{"extra-data":{uploadType:"DEF",sourceType:"WEB_APP",fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},"default-file":{fileId:expression("$form.query('.personBaseInfoBefore').get('value')?.extIdCardOppositeFileId"),fileName:expression("$form.query('.personBaseInfoBefore').get('value').extIdCardOppositeFileName")},disabled:!0},...requiredValidatorSegment},companyName:{type:"string","x-decorator":"FormItem",title:i18nExpression("cusEntry.vendorMod.companyNameOrPersonName"),"x-component-props":{disabled:!0},...requiredValidatorSegment},companyShortName:{type:"string","x-decorator":"FormItem",title:i18nExpression("cusEntry.vendorMod.personalAbbreviation"),"x-component-props":{disabled:!0},...requiredValidatorSegment},businessLicense:{type:"string","x-hidden":"true"},idNumber:{type:"string","x-decorator":"FormItem",title:i18nExpression("cusEntry.vendorMod.idNo"),"x-component-props":{disabled:!0},...requiredValidatorSegment},validityPeriodOfCard:{type:"date","x-decorator":"FormItem",title:i18nExpression("cusEntry.vendorMod.validityPeriodOfCard"),"x-component-props":{type:"daterange",disabled:!0},...requiredValidatorSegment},extSex:{type:"string","x-decorator":"FormItem",title:i18nExpression("cusEntry.vendorMod.sex"),"x-component":"DictSelect","x-component-props":{code:"GENDER",disabled:!0},...requiredValidatorSegment},businessScope:{type:"string",title:i18nExpression("cusEntry.vendorMod.mainBusinessScope"),"x-decorator":"FormItem","x-component-props":{disabled:!0},...requiredValidatorSegment},companyCountry:{type:"string",title:i18nExpression("components.address.country"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"country",placeholder:expression("$t('common.pleaseSelect')"),disabled:!0},...requiredValidatorSegment},companyProvince:{type:"string",title:i18nExpression("components.address.area"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"PROVINCE","custom-select-type":"PROVINCE",placeholder:expression("$t('common.pleaseSelect')"),disabled:!0},...requiredValidatorSegment},companyCity:{type:"string",title:i18nExpression("components.address.city"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:expression("$form.query('.personBaseInfoBefore.companyProvince').take()?.value"),"custom-select-type":"CITY",placeholder:expression("$t('common.pleaseSelect')"),disabled:!0},...requiredValidatorSegment},companyAddress:{type:"string",title:i18nExpression("cusEntry.vendorMod.detailAddress"),"x-disabled":!0,"x-decorator":"FormItem",...requiredValidatorSegment}}}}},afterChange:{type:"void","x-component":"div","x-component-props":{class:"formClassAllChange"},properties:{afterChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.afterChange"}},personBaseInfoAfter:{type:"object","x-query-engine-skip":!0,"x-component":"FormGrid","x-component-props":{class:"forms"},properties:{businessLicenseFileId:{type:"string",title:i18nExpression("cusEntry.vendorMod.frontOfIdCard"),"x-decorator":"FormItem","x-component":"SrmCommonFile","x-component-props":{"extra-data":{uploadType:"DEF",sourceType:"WEB_APP",fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},"default-file":{fileId:expression("$form.query('.personBaseInfoAfter').get('value').businessLicenseFileId"),fileName:expression("$form.query('.personBaseInfoAfter').get('value').businessLicense")},"@on-change":expression(`({file}) => {
                 const { fileId, fileName } = file || {}
                 $form.query($self.parent.address).get('value').businessLicenseFileId = fileId
                 $form.query($self.parent.address).get('value').businessLicense = fileName
              }`)},"x-reactions":expression(`() => {
              const newData = $form.query('.personBaseInfoAfter.businessLicenseFileId').take()?.value
              const oldData = $form.query('.personBaseInfoBefore.businessLicenseFileId').take()?.value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`),...requiredValidatorSegment},extIdCardOppositeFileName:{type:"string","x-hidden":!0},extIdCardOppositeFileId:{type:"string",title:i18nExpression("cusEntry.vendorMod.backOfIdCard"),"x-decorator":"FormItem","x-component":"SrmCommonFile","x-component-props":{"extra-data":{uploadType:"DEF",sourceType:"WEB_APP",fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},"default-file":{fileId:expression("$form.query('.personBaseInfoAfter').get('value')?.extIdCardOppositeFileId"),fileName:expression("$form.query('.personBaseInfoAfter').get('value').extIdCardOppositeFileName")},"@on-change":expression(`({file}) => {
                const { fileId, fileName } = file || {}
                $form.query('.personBaseInfoAfter').get('value').extIdCardOppositeFileId = fileId
                $form.query('.personBaseInfoAfter').get('value').extIdCardOppositeFileName = fileName
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
                    $form.query('personBaseInfoAfter').get('value').validityPeriodOfCard = [businessStartDate, businessEndDate]
                  })
                }
             }`)},"x-reactions":expression(`() => {
              const newData = $form.query('.personBaseInfoAfter.extIdCardOppositeFileId').take()?.value
              const oldData = $form.query('.personBaseInfoBefore.extIdCardOppositeFileId').take()?.value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`),...requiredValidatorSegment},companyName:{type:"string","x-decorator":"FormItem",title:i18nExpression("cusEntry.vendorMod.companyNameOrPersonName"),"x-reactions":expression(`() => {
              const newData = $form.query('.personBaseInfoAfter.companyName').take()?.value
              const oldData = $form.query('.personBaseInfoBefore.companyName').take()?.value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`),...requiredValidatorSegment},companyShortName:{type:"string","x-decorator":"FormItem",title:i18nExpression("cusEntry.vendorMod.personalAbbreviation"),"x-component-props":{"show-word-limit":!0,maxlength:100},...requiredValidatorSegment,"x-reactions":expression(`() => {
              const newData = $form.query('.personBaseInfoAfter.companyShortName').take()?.value
              const oldData = $form.query('.personBaseInfoBefore.companyShortName').take()?.value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`)},businessLicense:{type:"string","x-hidden":!0},idNumber:{type:"string","x-decorator":"FormItem",title:i18nExpression("cusEntry.vendorMod.idNo"),"x-component-props":{disabled:!0},"x-reactions":expression(`() => {
              const newData = $form.query('.personBaseInfoAfter.lcCode').take()?.value
              const oldData = $form.query('.personBaseInfoBefore.lcCode').take()?.value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`),...requiredValidatorSegment},validityPeriodOfCard:{type:"date","x-decorator":"FormItem",title:i18nExpression("cusEntry.vendorMod.validityPeriodOfCard"),"x-component-props":{type:"daterange"},"x-reactions":expression(`() => {
              const newData = $form.query('.personBaseInfoAfter.validityPeriodOfCard').take()?.value
              const oldData = $form.query('.personBaseInfoBefore.validityPeriodOfCard').take()?.value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`),...requiredValidatorSegment},extSex:{type:"string","x-decorator":"FormItem",title:i18nExpression("cusEntry.vendorMod.sex"),"x-component":"DictSelect","x-component-props":{code:"GENDER"},"x-reactions":expression(`() => {
              const newData = $form.query('.personBaseInfoAfter.extSex').take()?.value
              const oldData = $form.query('.personBaseInfoBefore.extSex').take()?.value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`),...requiredValidatorSegment},businessScope:{type:"string",title:i18nExpression("cusEntry.vendorMod.mainBusinessScope"),"x-decorator":"FormItem","x-reactions":expression(`() => {
              const newData = $form.query('.personBaseInfoAfter.businessScope').take()?.value
              const oldData = $form.query('.personBaseInfoBefore.businessScope').take()?.value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`),...requiredValidatorSegment},companyCountry:{type:"string",title:i18nExpression("components.address.country"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"country",placeholder:expression("$t('common.pleaseSelect')"),"@change":expression(`(val) => {
                // 选择国外就清理省市区，并且禁用
                if ($form.query('.personBaseInfoAfter.companyCountry').take().value !== 'CN') {
                  $form.query('.personBaseInfoAfter.companyProvince').take().value = ''
                  $form.query('.personBaseInfoAfter.companyCity').take().value = ''
                }
              }`)},"x-reactions":expression(`() => {
              const newData = $form.query('.personBaseInfoAfter.companyCountry').take()?.value
              const oldData = $form.query('.personBaseInfoBefore.companyCountry').take()?.value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`),...requiredValidatorSegment},companyProvince:{type:"string",title:i18nExpression("components.address.area"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"PROVINCE","custom-select-type":"PROVINCE",placeholder:expression("$t('common.pleaseSelect')")},"x-reactions":expression(`() => {
              const newData = $form.query('.personBaseInfoAfter.companyProvince').take()?.value
              const oldData = $form.query('.personBaseInfoBefore.companyProvince').take()?.value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`),...requiredValidatorSegment},companyCity:{type:"string",title:i18nExpression("components.address.city"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:expression("$form.query('.personBaseInfoAfter.companyProvince').take()?.value"),"custom-select-type":"CITY",placeholder:expression("$t('common.pleaseSelect')")},"x-reactions":expression(`() => {
              const newData = $form.query('.personBaseInfoAfter.companyCity').take()?.value
              const oldData = $form.query('.personBaseInfoBefore.companyCity').take()?.value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`),...requiredValidatorSegment},companyAddress:{type:"string",title:i18nExpression("cusEntry.vendorMod.detailAddress"),"x-decorator":"FormItem","x-reactions":expression(`() => {
              const newData = $form.query('.personBaseInfoAfter.companyAddress').take()?.value
              const oldData = $form.query('.personBaseInfoBefore.companyAddress').take()?.value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`),...requiredValidatorSegment}}}}}},contactData={beforeChange:{type:"void","x-component":"div","x-component-props":{class:""},properties:{beforeChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.beforeChange"}},contactDataBefore:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!1,maxHeight:400,pagination:!1,sortable:!1},"x-query-engine-skip":!0,properties:generateXindexInOrder({contactName:{type:"string",title:i18nExpression("vendorMod.nickname"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:!0},"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},ceeaGender:{type:"string",title:i18nExpression("vendorMod.sex"),"x-render-table-column":{minWidth:100},"x-component":"DictSelect","x-component-props":{code:"GENDER",disabled:!0}},ceeaDeptName:{type:"string",title:i18nExpression("vendorMod.department"),"x-render-table-column":{minWidth:100},"x-component-props":{disabled:!0}},position:{type:"string",title:i18nExpression("dataConfMod.position"),"x-render-table-column":{minWidth:100},"x-component":"DictSelect","x-component-props":{code:"POSITION",disabled:!0}},ceeaContactMethod:{type:"string",title:i18nExpression("vendorMod.contactMethod"),"x-render-table-column":{minWidth:100},"x-component-props":{disabled:!0}},email:{type:"string",title:i18nExpression("vendorMod.email"),"x-render-table-column":{minWidth:100},"x-component-props":{disabled:!0}},ceeaDefaultContact:{type:"string",title:i18nExpression("dataConfMod.isDefault"),"x-render-table-column":{minWidth:100},"x-component":"Checkbox","x-component-props":{"true-label":"Y","false-label":"N",disabled:!0}},socialSecurityCertificateFileId:{type:"string","x-render-table-column":{minWidth:100},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").userType!=="PERSONAL"),title:i18nExpression("cusEntry.vendorMod.socialSecurityCertificate"),"x-component":"SrmCommonFile","x-component-props":{"extra-data":{uploadType:"DEF",sourceType:"WEB_APP",fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},"default-file":{fileId:expression("$table.getRowByIndex($self.index)?.socialSecurityCertificateFileId"),fileName:expression("$table.getRowByIndex($self.index)?.socialSecurityCertificateFileName")},readonly:!0}},ceeaComments:{type:"string",title:i18nExpression("dataConfMod.remark"),"x-render-table-column":{minWidth:100},"x-component-props":{disabled:!0}}})}}},afterChange:{type:"void","x-component":"div","x-component-props":{class:""},properties:{afterChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.afterChange"}},toolbar:{type:"void","x-visible":expression("!$form.readPretty"),"x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void",title:i18nExpression("common.add"),"x-component-props":{type:"primary","@click":expression(`() => {
                 $self.query('contactInfoChanges')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
              }`)}}}},contactInfoChanges:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,maxHeight:400,primaryKey:"contactChangeId",cascadeDeletion:!0,pagination:!1,sortable:!1},"x-query-engine-skip":!0,properties:generateXindexInOrder({contactName:{type:"string",title:i18nExpression("vendorMod.nickname"),"x-render-table-column":{minWidth:120},"x-reactions":expression(`() => {
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
            }`),"x-component-props":{disabled:expression("$form.readPretty")}},position:{type:"string",title:i18nExpression("dataConfMod.position"),"x-render-table-column":{minWidth:100},"x-component":"DictSelect","x-reactions":expression(`() => {
                const oldData = $form.query('contactDataBefore').get('value')[$self.index]?.position || null
                let className = redFunction(oldData, $self?.value)
                $self.setComponentProps({ class: className })
            }`),"x-component-props":{code:"POSITION",disabled:expression("$form.readPretty")}},ceeaContactMethod:{type:"string",title:i18nExpression("vendorMod.contactMethod"),"x-render-table-column":{minWidth:100},"x-reactions":expression(`() => {
                const oldData = $form.query('contactDataBefore').get('value')[$self.index]?.ceeaContactMethod || null
                let className = redFunction(oldData, $self?.value)
                $self.setComponentProps({ class: className })
            }`),"x-component-props":{disabled:expression("$form.readPretty")},"x-validator":{required:!0,message:i18nExpression("common.requiredField"),validator:expression(`(value, rule) => {
                if(value && !validatePhone(value)) {
                  return '请输入格式正确的电话号码'
                }
              }`)}},email:{type:"string",title:i18nExpression("vendorMod.email"),"x-render-table-column":{minWidth:100},"x-reactions":expression(`() => {
                const oldData = $form.query('contactDataBefore').get('value')[$self.index]?.email || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),"x-component-props":{disabled:expression("$form.readPretty")},"x-validator":{required:!0,message:i18nExpression("common.requiredField"),validator:expression(`(value, rule) => {
                if(value && !validEmail(value)){
                  return '请输入格式正确的邮箱地址'
                }
              }`)}},ceeaDefaultContact:{type:"string",title:i18nExpression("dataConfMod.isDefault"),"x-render-table-column":{minWidth:100},"x-component":"Checkbox","x-reactions":expression(`() => {
                const oldData = $form.query('contactDataBefore').get('value')[$self.index]?.ceeaDefaultContact || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),"x-component-props":{"true-label":"Y","false-label":"N",disabled:expression("$form.readPretty"),"@change":expression(`() => {
                const row = $table.getRowByIndex($self.index)
                if(row.ceeaDefaultContact == 'Y'){
                  let data = $form.query('contactInfoChanges').get('value')
                  let index = 0;
                  for(let item of data){
                    if(index != $self.index){
                      item.ceeaDefaultContact = ''
                    }
                    index++
                  }
                }
              }`)}},socialSecurityCertificateFileId:{type:"string","x-render-table-column":{minWidth:100},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").userType!=="PERSONAL"),title:i18nExpression("cusEntry.vendorMod.socialSecurityCertificate"),"x-component":"SrmCommonFile","x-component-props":{"extra-data":{uploadType:"DEF",sourceType:"WEB_APP",fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},"default-file":{fileId:expression("$table.getRowByIndex($self.index)?.socialSecurityCertificateFileId"),fileName:expression("$table.getRowByIndex($self.index)?.socialSecurityCertificateFileName")},"@on-change":expression(`({file}) => {
                const { fileId, fileName } = file || {}
                $table.getRowByIndex($self.index).socialSecurityCertificateFileId = fileId
                $table.getRowByIndex($self.index).socialSecurityCertificateFileName = fileName
              }`)}},ceeaComments:{type:"string",title:i18nExpression("dataConfMod.remark"),"x-render-table-column":{minWidth:100},"x-reactions":expression(`() => {
                const oldData = $form.query('contactDataBefore').get('value')[$self.index]?.ceeaComments || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),"x-component-props":{disabled:expression("$form.readPretty")}},operation:{type:"void",title:"{{$t('common.operation')}}","x-visible":expression("!$form.readPretty"),"x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{disabled:expression("$form.readPretty"),type:"text","@click":expression(`({ row }) => {
                      $table.remove($self.index)
                  }`)}}}}})}}}},bankInfo={beforeChange:{type:"void","x-component":"div","x-component-props":{class:""},properties:{beforeChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.beforeChange"}},bankInfoBefore:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!1,maxHeight:400,pagination:!1,sortable:!1},"x-query-engine-skip":!0,properties:generateXindexInOrder({branchBankId:{type:"number","x-hidden":!0},bankCode:{type:"string",title:i18nExpression("components.bank.bankCode"),"x-render-table-column":{minWidth:120},"x-component":"QuickSearchWrapper","x-component-props":{showKey:"bankNum",propKey:"bankNum",name:"ceea_base_erp_branch_bank_info",disabled:!0,"@close-quicksearch":expression(`(val) => {
                let row = $table.getRowByIndex($self.index)
                row.branchBankId = val ? val.branchBankId : ''
                row.bankCode = val ? val.bankNum : ''
                row.bankName = val ? val.bankName : '' // 银行名称
                row.unionCode = val ? val.branchBankNum : '' // 分行编号
                row.openingBank = val ? val.branchBankName : '' // 分行名称[开户行名称]
              }
              `)},"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},bankName:{type:"string",title:i18nExpression("components.bank.bankName"),"x-render-table-column":{minWidth:120},"x-read-pretty":!0,"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},openingBank:{type:"string",title:i18nExpression("components.bank.branchBankName"),"x-render-table-column":{minWidth:120},"x-read-pretty":!0,"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},unionCode:{type:"string",title:i18nExpression("components.bank.unionCode"),"x-render-table-column":{minWidth:120},"x-read-pretty":!0,"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},bankAccountName:{type:"string",title:i18nExpression("components.bank.accountName"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:!0},"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},bankAccount:{type:"string",title:i18nExpression("components.bank.bankAccount"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:!0},"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},currencyCode:{type:"string",title:i18nExpression("vendorMod.currencyCode"),"x-render-table-column":{minWidth:120},"x-component":"DictSelect","x-component-props":{code:"currency",disabled:!0},"x-validator":{required:generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").userType!=="PERSONAL"),message:i18nExpression("common.requiredField")}},ceeaMainAccount:{type:"string",title:i18nExpression("components.bank.isMain"),"x-render-table-column":{minWidth:100},"x-component":"Checkbox","x-component-props":{"true-label":"Y","false-label":"N",disabled:!0}},ceeaEnabled:{type:"string",title:i18nExpression("components.bank.isActive"),"x-render-table-column":{minWidth:100},"x-component":"Checkbox","x-component-props":{"true-label":"Y","false-label":"N",disabled:!0}}})}}},afterChange:{type:"void","x-component":"div","x-component-props":{class:""},properties:{afterChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.afterChange"}},toolbar:{type:"void","x-visible":expression("!$form.readPretty"),"x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void",title:i18nExpression("common.add"),"x-component-props":{type:"primary","@click":expression(`() => {
                 $self.query('bankInfoAfter')
                   .take(field => {
                     field.componentProps.componentInstance.addRow('push', {
                       companyId: $form.values.form.companyId
                     })
                 })
              }`)}}}},bankInfoAfter:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,maxHeight:400,pagination:!1,sortable:!1,primaryKey:"bankChangeId",cascadeDeletion:!0},"x-query-engine-skip":!0,"x-query-engine-relation":"bankInfoChanges:*",properties:generateXindexInOrder({branchBankId:{type:"number","x-hidden":!0},bankCode:{type:"string",title:i18nExpression("components.bank.bankCode"),"x-render-table-column":{minWidth:120},"x-component":"QuickSearchWrapper","x-reactions":expression(`() => {
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
            }`),"x-component-props":{code:"currency",disabled:expression("$form.readPretty")},"x-validator":{required:generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").userType!=="OUT"),message:i18nExpression("common.requiredField")}},ceeaMainAccount:{type:"string",title:i18nExpression("components.bank.isMain"),"x-render-table-column":{minWidth:100},"x-component":"Checkbox","x-reactions":expression(`() => {
                const oldData = $form.query('bankInfoBefore').get('value')[$self.index]?.ceeaMainAccount || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),"x-component-props":{"true-label":"Y","false-label":"N",disabled:expression("$form.readPretty")}},ceeaEnabled:{type:"string",title:i18nExpression("components.bank.isActive"),"x-render-table-column":{minWidth:100},"x-reactions":expression(`() => {
                const oldData = $form.query('bankInfoBefore').get('value')[$self.index]?.ceeaEnabled || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),"x-component":"Checkbox","x-component-props":{"true-label":"Y","false-label":"N",disabled:expression("$form.readPretty")}},operation:{type:"void",title:"{{$t('common.operation')}}","x-visible":expression("!$form.readPretty"),"x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{disabled:expression("$form.readPretty"),type:"text","@click":expression(`({ row }) => {
                      $table.remove($self.index)
                  }`)}}}}})}}}},financeInfo={beforeChange:{type:"void","x-component":"div","x-component-props":{class:""},properties:{beforeChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.beforeChange"}},financeInfoBeforeForm:{type:"object","x-query-engine-skip":!0,properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:{totalAssets:{type:"string",title:i18nExpression("cusEntry.vendorMod.totalAssets"),"x-decorator":"FormItem","x-component-props":{disabled:!0}},currentAssets:{type:"string",title:i18nExpression("cusEntry.vendorMod.workingCapital"),"x-decorator":"FormItem","x-component-props":{disabled:!0}},fixedAssets:{type:"string",title:i18nExpression("cusEntry.vendorMod.fixedAssets"),"x-decorator":"FormItem","x-component-props":{disabled:!0}},avgAnnualOutput:{type:"string",title:i18nExpression("cusEntry.vendorMod.threeYearsOutput"),"x-decorator":"FormItem","x-component-props":{disabled:!0}},avgAnnualProfit:{type:"string",title:i18nExpression("cusEntry.vendorMod.threeYearsNetProfits"),"x-decorator":"FormItem","x-component-props":{disabled:!0}}}}}},financeInfoBeforeTag:{type:"void","x-component":"p","x-content":i18nExpression("cusEntry.vendorMod.threeYearsReportFile")},financeReport:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!1,maxHeight:400,pagination:!1,sortable:!1},"x-query-engine-skip":!0,properties:generateXindexInOrder({year:{type:"string",title:i18nExpression("cusEntry.vendorMod.year"),"x-render-table-column":{minWidth:120},"x-component":"DatePicker","x-component-props":{type:"year",format:"yyyy","value-format":"yyyy"}},remark:{type:"string",title:i18nExpression("cusEntry.vendorMod.remark"),"x-render-table-column":{minWidth:120}},fileId:{type:"string",title:i18nExpression("cusEntry.vendorMod.file"),"x-render-table-column":{minWidth:120},"x-component":"SrmCommonFile","x-component-props":{"extra-data":{uploadType:"DEF",sourceType:"WEB_APP",fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},"default-file":{fileId:expression("$table.getRowByIndex($self.index)?.fileId"),fileName:expression("$table.getRowByIndex($self.index)?.fileName")},readonly:!0}}})},financeInfoBeforeRemark:{type:"void","x-component":"p","x-content":i18nExpression("cusEntry.vendorMod.financeInfoRemark")}}},afterChange:{type:"void","x-component":"div","x-component-props":{class:""},properties:{afterChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.afterChange"}},financeInfoAfterForm:{type:"object","x-query-engine-skip":!0,properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:{totalAssets:{type:"number",title:i18nExpression("cusEntry.vendorMod.totalAssets"),"x-decorator":"FormItem","x-reactions":expression(`(field) => {
                  const newData = $form.query('.financeInfoAfterForm.totalAssets').take().value
                  const oldData = $form.query('.financeInfoBeforeForm.totalAssets').take().value
                  let className = redFunction(oldData, newData)
                  $self.setComponentProps({ class: className })
                  field.value = field?.value ? field?.value : null
                }`)},currentAssets:{type:"number",title:i18nExpression("cusEntry.vendorMod.workingCapital"),"x-decorator":"FormItem","x-reactions":expression(`(field) => {
                  const newData = $form.query('.financeInfoAfterForm.currentAssets').take().value
                  const oldData = $form.query('.financeInfoBeforeForm.currentAssets').take().value
                  let className = redFunction(oldData, newData)
                  $self.setComponentProps({ class: className })
                }`)},fixedAssets:{type:"number",title:i18nExpression("cusEntry.vendorMod.fixedAssets"),"x-decorator":"FormItem","x-reactions":expression(`(field) => {
                  const newData = $form.query('.financeInfoAfterForm.fixedAssets').take().value
                  const oldData = $form.query('.financeInfoBeforeForm.fixedAssets').take().value
                  let className = redFunction(oldData, newData)
                  $self.setComponentProps({ class: className })
                }`)},avgAnnualOutput:{type:"number",title:i18nExpression("cusEntry.vendorMod.threeYearsOutput"),"x-decorator":"FormItem","x-reactions":expression(`(field) => {
                  const newData = $form.query('.financeInfoAfterForm.avgAnnualOutput').take().value
                  const oldData = $form.query('.financeInfoBeforeForm.avgAnnualOutput').take().value
                  let className = redFunction(oldData, newData)
                  $self.setComponentProps({ class: className })
                }`)},avgAnnualProfit:{type:"string",title:i18nExpression("cusEntry.vendorMod.threeYearsNetProfits"),"x-decorator":"FormItem","x-reactions":expression(`(field) => {
                  const newData = $form.query('.financeInfoAfterForm.avgAnnualProfit').take().value
                  const oldData = $form.query('.financeInfoBeforeForm.avgAnnualProfit').take().value
                  let className = redFunction(oldData, newData)
                  $self.setComponentProps({ class: className })
                }`)}}}}},financeInfoAfterTag:{type:"void","x-component":"p","x-content":i18nExpression("cusEntry.vendorMod.threeYearsReportFile")},toolbar:{type:"void","x-visible":expression("!$form.readPretty"),"x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void",title:i18nExpression("common.add"),"x-component-props":{type:"primary","@click":expression(`() => {
                 $self.query('financeReportChange')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
              }`)}}}},financeReportChange:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,maxHeight:400,pagination:!1,sortable:!1,primaryKey:"financeChangeId",cascadeDeletion:!0},"x-query-engine-skip":!0,properties:generateXindexInOrder({year:{type:"string",title:i18nExpression("cusEntry.vendorMod.year"),"x-render-table-column":{minWidth:120},"x-component":"DatePicker","x-component-props":{type:"year",format:"yyyy","value-format":"yyyy"},"x-reactions":expression(`() => {
              const oldData = $form.query('financeReport').get('value')?.[$self.index]?.year || null
              let className = redFunction(oldData, $self?.value)
              $self.setComponentProps({ class: className })
            }`)},remark:{type:"string",title:i18nExpression("cusEntry.vendorMod.remark"),"x-render-table-column":{minWidth:120},"x-reactions":expression(`() => {
              const oldData = $form.query('financeReport').get('value')?.[$self.index]?.remark || null
              let className = redFunction(oldData, $self?.value)
              $self.setComponentProps({ class: className })
            }`)},fileId:{type:"string",title:i18nExpression("cusEntry.vendorMod.file"),"x-render-table-column":{minWidth:120},"x-component":"SrmCommonFile","x-component-props":{"extra-data":{uploadType:"DEF",sourceType:"WEB_APP",fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},"default-file":{fileId:expression("$table.getRowByIndex($self.index)?.fileId"),fileName:expression("$table.getRowByIndex($self.index)?.fileName")},"@on-change":expression(`({file}) => {
                const { fileId, fileName } = file || {}
                $table.getRowByIndex($self.index).fileId = fileId
                $table.getRowByIndex($self.index).fileName = fileName
              }`)},"x-reactions":expression(`() => {
              const oldData = $form.query('financeReport').get('value')?.[$self.index]?.fileId || null
              let className = redFunction(oldData, $self?.value)
              $self.setComponentProps({ class: className })
            }`)},operation:{type:"void",title:"{{$t('common.operation')}}","x-visible":expression("!$form.readPretty"),"x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{disabled:expression("$form.readPretty"),type:"text","@click":expression(`({ row }) => {
                      $table.remove($self.index)
                  }`)}}}}})},financeInfoAfterRemark:{type:"void","x-component":"p","x-content":i18nExpression("cusEntry.vendorMod.financeInfoRemark")}}}},siteInfos={beforeChange:{type:"void","x-component":"div","x-component-props":{class:""},properties:{beforeChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.beforeChange"}},siteInfosBefore:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!1,maxHeight:400,pagination:!1,sortable:!1},"x-query-engine-skip":!0,properties:generateXindexInOrder({totalNumber:{type:"string",title:i18nExpression("cusEntry.vendorMod.totalNum"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:!0}},socialSecurityNumber:{type:"string",title:i18nExpression("cusEntry.vendorMod.socialSecurity"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:!0}},managementNumber:{type:"string",title:i18nExpression("cusEntry.vendorMod.managerNum"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:!0}},developerNumber:{type:"string",title:i18nExpression("cusEntry.vendorMod.developmentNum"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:!0}},productionNumber:{type:"string",title:i18nExpression("cusEntry.vendorMod.productNum"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:!0}},overUndergraduateNumber:{type:"string",title:i18nExpression("cusEntry.vendorMod.bachelorDegreeOrAbove"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:!0}}})}}},afterChange:{type:"void","x-component":"div","x-component-props":{class:""},properties:{afterChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.afterChange"}},siteInfosAfter:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,maxHeight:400,pagination:!1,sortable:!1,primaryKey:"companySizeCahngeId",cascadeDeletion:!0},"x-query-engine-skip":!0,"x-query-engine-relation":"npmCompanySizeChanges:*",properties:generateXindexInOrder({totalNumber:{type:"number",title:i18nExpression("cusEntry.vendorMod.totalNum"),"x-render-table-column":{minWidth:120},"x-reactions":expression(`() => {
              const oldData = $form.query('siteInfosBefore').get('value')?.[$self.index]?.totalNumber || null
              let className = redFunction(oldData, $self?.value)
              $self.setComponentProps({ class: className })
            }`)},socialSecurityNumber:{type:"number",title:i18nExpression("cusEntry.vendorMod.socialSecurity"),"x-render-table-column":{minWidth:120},"x-reactions":expression(`() => {
              const oldData = $form.query('siteInfosBefore').get('value')?.[$self.index]?.socialSecurityNumber || null
              let className = redFunction(oldData, $self?.value)
              $self.setComponentProps({ class: className })
            }`)},managementNumber:{type:"number",title:i18nExpression("cusEntry.vendorMod.managerNum"),"x-render-table-column":{minWidth:120},"x-reactions":expression(`() => {
              const oldData = $form.query('siteInfosBefore').get('value')?.[$self.index]?.managementNumber || null
              let className = redFunction(oldData, $self?.value)
              $self.setComponentProps({ class: className })
            }`)},developerNumber:{type:"number",title:i18nExpression("cusEntry.vendorMod.developmentNum"),"x-render-table-column":{minWidth:120},"x-reactions":expression(`() => {
              const oldData = $form.query('siteInfosBefore').get('value')?.[$self.index]?.developerNumber || null
              let className = redFunction(oldData, $self?.value)
              $self.setComponentProps({ class: className })
            }`)},productionNumber:{type:"number",title:i18nExpression("cusEntry.vendorMod.productNum"),"x-render-table-column":{minWidth:120},"x-reactions":expression(`() => {
              const oldData = $form.query('siteInfosBefore').get('value')?.[$self.index]?.productionNumber || null
              let className = redFunction(oldData, $self?.value)
              $self.setComponentProps({ class: className })
            }`)},overUndergraduateNumber:{type:"number",title:i18nExpression("cusEntry.vendorMod.bachelorDegreeOrAbove"),"x-render-table-column":{minWidth:120},"x-reactions":expression(`() => {
              const oldData = $form.query('siteInfosBefore').get('value')?.[$self.index]?.overUndergraduateNumber || null
              let className = redFunction(oldData, $self?.value)
              $self.setComponentProps({ class: className })
            }`)},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{disabled:expression("$form.readPretty"),type:"text","@click":expression(`({ row }) => {
                      $table.remove($self.index)
                  }`)}}}}})}}}},serviceRange={beforeChange:{type:"void","x-component":"div","x-component-props":{class:""},properties:{beforeChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.beforeChange"}},serviceRangeBefore:{type:"array","x-component":"ArrayItems",items:{type:"void",properties:{tableForm:{type:"object",properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:{categoryName:{type:"string","x-decorator":"FormItem","x-component":"QuickSearchWrapper","x-component-props":{showKey:"categoryName",disabled:!0,name:"scc_base_purchase_category4","@close-quicksearch":expression(`val => {
                          let row = $table.getRowByIndex($self.index)
                          row.categoryCode = val?.categoryCode || ''
                          row.categoryName = val?.categoryName || ''
                          row.categoryId = val?.categoryId || ''
                        }`)},title:"{{t('cusEntry.vendorMod.category', {index: $self.index + 1})}}"}}}}},list:{type:"array","x-component":"RenderTable","x-query-engine-skip":!0,"x-component-props":{preColumns:"seq",editMode:!1,maxHeight:250,pagination:!1,sortable:!1},properties:generateXindexInOrder({performanceAmount:{type:"string",title:i18nExpression("cusEntry.vendorMod.performance"),"x-render-table-column":{minWidth:120}},mainCustom:{type:"string",title:i18nExpression("cusEntry.vendorMod.mainCustomer"),"x-render-table-column":{minWidth:120},"x-validator":{required:!0,message:i18nExpression("cusEntry.tipMessage.required")}},fileId:{type:"string",title:i18nExpression("cusEntry.vendorMod.achievement"),"x-render-table-column":{minWidth:120},"x-component":"SrmCommonFile","x-component-props":{extraData:{fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},defaultFile:{fileId:expression("$table.getRowByIndex($self.index)?.fileuploadId"),fileName:expression("$self?.value")},readonly:!0}}})}}}}}},afterChange:{type:"void","x-component":"div","x-component-props":{class:""},properties:{afterChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.afterChange"}},toolbar:{type:"void","x-visible":expression("!$form.readPretty"),"x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void",title:i18nExpression("common.add"),"x-component-props":{type:"primary","@click":expression(`() => {
                $form.query('serviceRangeAfter').take(field => {
                  field.invoke('add', 'push')
                })
              }`)}}}},serviceRangeAfter:{type:"array","x-component":"ArrayItems","x-query-engine-skip":!0,items:{type:"void",properties:{tableForm:{type:"object",properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:{categoryName:{type:"string","x-decorator":"FormItem","x-component":"QuickSearchWrapper","x-component-props":{dialogLabel:i18nExpression("cusEntry.vendorMod.categoryNameTitle"),showKey:"categoryName",name:"scc_base_purchase_category2","@close-quicksearch":expression(`val => {
                          let list = $form.query('serviceRangeAfter').get('value')
                          let flag = false
                          for(let item of list){
                            item.tableForm.categoryCode == val.categoryCode && (flag = true)
                          }
                          if(flag){
                            app.$message.error('服务范围内已经存在该品类')
                            $form.query('serviceRangeAfter').take(field => {
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
                        }`)},"x-reactions":expression(`() => {
                        const oldData = $form.query('serviceRangeBefore').get('value')?.[$self.index]?.tableForm?.categoryName || null
                        let className = redFunction(oldData, $self?.value)
                        $self.setComponentProps({ class: className })
                      }`),...requiredValidatorSegment,title:"{{t('cusEntry.vendorMod.category', {index: $self.index + 1})}}"},formBtn:{type:"void","x-visible":expression("!$form.readPretty"),"x-component":"ButtonList","x-component-props":{style:{"margin-top":"5px"}},properties:{add:{type:"void","x-component-props":{type:"primary","@click":expression(`() => {
                              $form.query($self.parent.parent.parent.parent.address.concat($self.index).concat('list')).take(field => {
                                field.componentProps.componentInstance.addRow()
                              })
                            }`)},title:i18nExpression("cusEntry.common.addCustomer")},delete:{type:"void",title:i18nExpression("cusEntry.common.deleteCategory"),"x-component-props":{type:"primary","@click":expression(`() => {
                              const { categoryJournalChangeId = null } = $form.query('serviceRangeAfter').get('value')[$self.index]?.tableForm || {}
                              $form.query('serviceRangeAfter').take(field => {
                                const name = 'npmCateJournalChanges'
                                if (categoryJournalChangeId) {
                                  if (!$queryEngine.dataCollection.value.relationTableCascadeDeletions[name]) {
                                    $queryEngine.dataCollection.value.relationTableCascadeDeletions[name] = new Set()
                                  }
                                  $queryEngine.dataCollection.value.relationTableCascadeDeletions[name].add(categoryJournalChangeId)
                                }
                                field.remove($self.index)
                              })
                            }`)}}}}}}}},list:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,maxHeight:250,pagination:!1,sortable:!1},"x-query-engine-skip":!0,properties:generateXindexInOrder({performanceAmount:{type:"number",title:i18nExpression("cusEntry.vendorMod.performance"),"x-render-table-column":{minWidth:120},"x-reactions":expression(`() => {
                    const parentIndex = $self.parent?.index
                    const oldData = $form.query('serviceRangeBefore').get('value')?.[parentIndex]?.list?.[$self.index]?.performanceAmount || null
                    let className = redFunction(oldData, $self?.value)
                    $self.setComponentProps({ class: className })
                  }`)},mainCustom:{type:"string",title:i18nExpression("cusEntry.vendorMod.mainCustomer"),"x-render-table-column":{minWidth:120},"x-validator":{required:!0,message:i18nExpression("cusEntry.tipMessage.required")},"x-reactions":expression(`() => {
                    const parentIndex = $self.parent?.index
                    const oldData = $form.query('serviceRangeBefore').get('value')?.[parentIndex]?.list?.[$self.index]?.mainCustom || null
                    let className = redFunction(oldData, $self?.value)
                    $self.setComponentProps({ class: className })
                  }`)},fileId:{type:"string",title:i18nExpression("cusEntry.vendorMod.achievement"),"x-render-table-column":{minWidth:120},"x-component":"SrmCommonFile","x-component-props":{extraData:{fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},defaultFile:{fileId:expression("$table.getRowByIndex($self.index)?.fileId"),fileName:expression("$table.getRowByIndex($self.index)?.fileName")},readonly:!1,"@on-change":expression(`({file}) => {
                      const { fileId = '', fileName = '' } = file || {}
                      let row = $table.getRowByIndex($self.index)
                      row.fileId = fileId
                      row.fileName = fileName
                    }`)},"x-reactions":expression(`() => {
                    const parentIndex = $self.parent?.index
                    const oldData = $form.query('serviceRangeBefore').get('value')?.[parentIndex]?.list?.[$self.index]?.fileId || null
                    let className = redFunction(oldData, $self?.value)
                    $self.setComponentProps({ class: className })
                  }`),...editTableFormItemValid},operation:{type:"void",title:i18nExpression("common.operation"),"x-visible":expression("!$form.readPretty"),"x-render-table-column":{width:60,fiexd:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:i18nExpression("common.delete"),"x-component-props":{type:"text","@click":expression(`() => {
                          const { serciceCustomChangeId = null } = $table.getRowByIndex($self.index) || {}
                          const name = 'npmSerciceCustomChanges'
                          if (serciceCustomChangeId) {
                            let serciceCustomDelList = $form.query('state').get('data').serciceCustomDelList
                            serciceCustomDelList.push({
                              $delete: serciceCustomChangeId
                            })
                          }
                          $table.remove($self.index)
                        }`)}}}}})}}}}}}},qualificationInformation={beforeChange:{type:"void","x-component":"div",properties:{beforeChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.beforeChange"}},qualificationInfoBefore:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!1,maxHeight:400,pagination:!1,sortable:!1},"x-query-engine-skip":!0,properties:generateXindexInOrder({authNum:{type:"string",title:i18nExpression("cusEntry.vendorMod.certificateType"),"x-render-table-column":{minWidth:120}},startDate:{type:"date",title:i18nExpression("cusEntry.vendorMod.startTime"),"x-render-table-column":{minWidth:120}},endDate:{type:"date",title:i18nExpression("cusEntry.vendorMod.endTime"),"x-render-table-column":{minWidth:120}},fileuploadId:{type:"string","x-component":"SrmCommonFile","x-component-props":{extraData:{fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},defaultFile:{fileId:expression("$table.getRowByIndex($self.index)?.fileuploadId"),fileName:expression("$table.getRowByIndex($self.index)?.authType")},readonly:!0},title:i18nExpression("cusEntry.vendorMod.file"),"x-render-table-column":{minWidth:120}},authDescription:{type:"string",title:i18nExpression("cusEntry.vendorMod.remark"),"x-render-table-column":{minWidth:120}}})}}},afterChange:{type:"void","x-component":"div","x-component-props":{class:""},properties:{afterChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.afterChange"}},toolbar:{type:"void","x-visible":expression("!$form.readPretty"),"x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void",title:i18nExpression("common.add"),"x-component-props":{type:"primary","@click":expression(`() => {
                $self.query('qualificationInfoAfter')
                  .take(field => {
                    field.componentProps.componentInstance.addRow('push', {})
                  })
              }`)}}}},qualificationInfoAfter:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,maxHeight:400,pagination:!1,sortable:!1,primaryKey:"managementAttachChangeId",cascadeDeletion:!0},"x-query-engine-skip":!0,"x-query-engine-relation":"managementAttachChanges:*",properties:generateXindexInOrder({authNum:{type:"string","x-component":"DictSelect","x-component-props":{code:"CERTIFICATE_TYPE"},title:i18nExpression("cusEntry.vendorMod.certificateType"),"x-reactions":expression(`() => {
              const oldData = $form.query('qualificationInfoBefore').get('value')?.[$self.index]?.authNum || null
              let className = redFunction(oldData, $self?.value)
              $self.setComponentProps({ class: className })
            }`),"x-render-table-column":{minWidth:120}},startDate:{type:"date",title:i18nExpression("cusEntry.vendorMod.startTime"),"x-render-table-column":{minWidth:120},"x-reactions":expression(`() => {
              const oldData = $form.query('qualificationInfoBefore').get('value')?.[$self.index]?.startDate || null
              let className = redFunction(oldData, $self?.value)
              $self.setComponentProps({ class: className })
            }`)},endDate:{type:"date",title:i18nExpression("cusEntry.vendorMod.endTime"),"x-render-table-column":{minWidth:120},"x-reactions":expression(`() => {
              const oldData = $form.query('qualificationInfoBefore').get('value')?.[$self.index]?.endDate || null
              let className = redFunction(oldData, $self?.value)
              $self.setComponentProps({ class: className })
            }`)},fileuploadId:{type:"string","x-component":"SrmCommonFile","x-component-props":{extraData:{fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},defaultFile:{fileId:expression("$table.getRowByIndex($self.index)?.fileuploadId"),fileName:expression("$table.getRowByIndex($self.index)?.authType")},readonly:!1,"@on-change":expression(`({file}) => {
                const { fileId = '', fileName = '' } = file || {}
                let row = $table.getRowByIndex($self.index)
                row.fileuploadId = fileId
                row.authType = fileName
              }`)},title:i18nExpression("cusEntry.vendorMod.file"),"x-render-table-column":{minWidth:120},"x-reactions":expression(`() => {
              const oldData = $form.query('qualificationInfoBefore').get('value')?.[$self.index]?.fileuploadId || null
              let className = redFunction(oldData, $self?.value)
              $self.setComponentProps({ class: className })
            }`)},authDescription:{type:"string",title:i18nExpression("cusEntry.vendorMod.remark"),"x-render-table-column":{minWidth:120},"x-reactions":expression(`() => {
              const oldData = $form.query('qualificationInfoBefore').get('value')?.[$self.index]?.remark || null
              let className = redFunction(oldData, $self?.value)
              $self.setComponentProps({ class: className })
            }`)},operation:{type:"void","x-visible":expression("!$form.readPretty"),title:i18nExpression("common.operation"),"x-render-table-column":{width:60,fixed:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:i18nExpression("common.delete"),"x-component-props":{type:"text","@click":expression(`() => {
                    $table.remove($self.index)
                  }`)}}}}})}}}},attachFile={beforeChange:{type:"void","x-component":"div","x-component-props":{class:""},properties:{beforeChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.beforeChange"}},attachFileBefore:{type:"array","x-component":"FileDynamic","x-component-props":{"scene-module-code":"SCENE_SUPPLIER_ATTACHMENT",businessId:expression("$form.query('state').get('data').companyId"),editable:!1,"need-init":!1}}}},afterChange:{type:"void","x-component":"div","x-component-props":{class:""},properties:{afterChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.afterChange"}},attachFileAfter:{type:"array","x-component":"FileDynamic","x-component-props":{"scene-module-code":"SCENE_SUPPLIER_ATTACHMENT",businessId:expression("$attrs.params?.changeId"),editable:expression("$attrs.params.flag != 'view'"),"need-init":!1}}}}},tabs={tab1:{type:"void","x-component":"FormTab.TabPane","x-component-props":{ref:"companyType",label:i18nExpression("vendorMod.companyType")},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").userType!=="PERSONAL"),properties:{div:{type:"void","x-component":"div","x-component-props":{class:"formClassWrap"},properties:{...companyType}}}},tab11:{type:"void","x-component":"FormTab.TabPane","x-component-props":{ref:"companyType",label:i18nExpression("cusEntry.vendorMod.baseInfo")},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").userType==="PERSONAL"),properties:{div:{type:"void","x-component":"div","x-component-props":{class:"formClassWrap"},properties:{...personBaseInfo}}}},tab2:{type:"void","x-component":"FormTab.TabPane","x-component-props":{ref:"enterpriseThreeCertificates",label:i18nExpression("vendorMod.enterpriseThreeCertificates")},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").userType!=="PERSONAL"),properties:{div:{type:"void","x-component":"div","x-component-props":{class:"formClassWrap"},properties:{...enterpriseThreeCertificates}}}},tab3:{type:"void","x-component":"FormTab.TabPane","x-component-props":{ref:"companyBaseInfo",label:i18nExpression("vendorMod.companyBaseInfo")},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").userType!=="PERSONAL"),properties:{div:{type:"void","x-component":"div","x-component-props":{class:"formClassWrap"},properties:{...companyBaseInfo}}}},tab4:{type:"void","x-component":"FormTab.TabPane","x-component-props":{ref:"contactInfo",label:i18nExpression("vendorMod.contactInfo"),class:""},properties:{...contactData}},tab5:{type:"void","x-component":"FormTab.TabPane","x-component-props":{ref:"bankInfo",label:i18nExpression("vendorMod.bankInfo"),class:""},properties:{...bankInfo}},tab6:{type:"void","x-component":"FormTab.TabPane","x-component-props":{ref:"financeInfo",label:i18nExpression("cusEntry.vendorMod.financeReport")},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").userType!=="PERSONAL"),properties:{...financeInfo}},tab7:{type:"void","x-component":"FormTab.TabPane","x-component-props":{ref:"companySize",label:i18nExpression("cusEntry.vendorMod.companySize")},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").userType!=="PERSONAL"),properties:{...siteInfos}},tab8:{type:"void","x-component":"FormTab.TabPane","x-component-props":{ref:"serviceRange",label:i18nExpression("cusEntry.vendorMod.serviceRange")},properties:{...serviceRange}},tab9:{type:"void","x-component":"FormTab.TabPane","x-component-props":{ref:"qualificationInformation",label:i18nExpression("cusEntry.vendorMod.qualificationInformation"),class:""},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").userType!=="PERSONAL"),properties:{...qualificationInformation}},tab10:{type:"void","x-component":"FormTab.TabPane","x-component-props":{ref:"financeInfo",label:i18nExpression("vendorMod.otherAttachInfo"),class:""},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").userType!=="PERSONAL"),properties:{...attachFile}}};export{formMain as f,tabs as t};
