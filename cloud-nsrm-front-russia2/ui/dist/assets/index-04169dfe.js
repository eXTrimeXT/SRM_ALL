import{N as NavTabs}from"./index-9a7f2446.js";import{n as normalizeComponent,cf as formGridSegment,ae as i18nExpression,ad as expression,aD as requiredValidatorSegment,af as yearMonthDaySelectorSegment,ag as radioGroupByYOrNSegment,ah as generateXindexInOrder,ai as editTableFormItemValid,aC as generateCharExpressionByFunction,ak as defineComponent,al as usePageHelper,am as useAttrs,an as ref$1,aq as defineSchemas,au as observer,at as DictSelect,v as validEmail,a as validatePhone,ac as createDictClass,ar as RenderEngine,cx as FormTab,b$ as dataTimeSelectorSegment,bD as changeFieldVisibleByDeps}from"./index-6b6051d8.js";import{s as supCommonApi}from"./supApi-98b2a23d.js";import{F as FileDynamic}from"./file-dynamic-25a093c4.js";import{v as vendorGreenApi}from"./vendorManagement-96246714.js";import{t as transformMQL}from"./util-d962b17f.js";import{s as sceneFileApi}from"./basicSetting-fc46a2d9.js";import"./file-dynamic-7fc2d358.js";import"./MainHeader-2d842985.js";/* empty css                                                                   */import"./file-dynamic.vue_vue_type_style_index_0_scoped_516fdfc3_lang-f2c1a82d.js";import"./tree-utils-7df6be59.js";import"./BaseTableBind-b1f76fc9.js";import"./util-1e55288f.js";import"./index-4d512f00.js";/* empty css                                                                      */import"./fileApi-05bbbbcc.js";const _sfc_main$3={name:"changeTitle",props:{language:{type:String,default:()=>""}}};var _sfc_render$3=function(){var _vm=this,_c=_vm._self._c;return _c("div",{staticClass:"changeTitle changeTitleTop"},[_c("i"),_vm._v(_vm._s(_vm.$t(_vm.language))+" ")])},_sfc_staticRenderFns$3=[],__component__$3=normalizeComponent(_sfc_main$3,_sfc_render$3,_sfc_staticRenderFns$3,!1,null,"9ad344c2",null,null);const changeTitle=__component__$3.exports,formMain={type:"object","x-query-engine-skip":!0,...formGridSegment,properties:{changeId:{type:"number","x-hidden":!0,"x-decorator":"FormItem"},changeStatus:{type:"string","x-hidden":!0,"x-decorator":"FormItem"},changeApplyNo:{type:"string",title:i18nExpression("vendorMod.changeApplyNo"),"x-component-props":{disabled:!0},"x-decorator":"FormItem"},companyId:{type:"number","x-hidden":!0},companyCode:{type:"string","x-hidden":!0},companyName:{type:"string",title:i18nExpression("common.vendorName"),"x-component":"QuickSearchWrapper","x-component-props":{showKey:"companyName",name:"scc_sup_company_info_display_buyer",disabled:!0,"read-pretty":"{{$form.readPretty}}"},"x-decorator":"FormItem","x-validator":{required:!0,message:i18nExpression("common.requiredField")}},noticeById:{type:"string","x-hidden":!0,"x-decorator":"FormItem"},changeFileId:{type:"string","x-hidden":!0,"x-decorator":"FormItem"},changeFileName:{type:"string","x-decorator":"FormItem","x-hidden":!0},changeExplain:{type:"string",title:i18nExpression("vendorMod.changeExplain"),"x-component":"Input.TextArea","x-component-props":{autosize:{minRows:3,maxRows:4}},"x-decorator":"FormItem","x-decorator-props":{gridSpan:4},"x-validator":{required:!0,message:i18nExpression("common.requiredField")}}}},companyType={beforeChange:{type:"void","x-component":"div","x-component-props":{class:"formClassAllChange"},properties:{beforeChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.beforeChange"}},companyTypeBefore:{type:"object","x-query-engine-skip":!0,"x-component":"FormGrid","x-component-props":{class:"forms"},properties:{domesticAndForeignRelations:{type:"string",title:i18nExpression("cusEntry.vendorMod.domesticAndForeignRelations"),"x-component":"DictSelect","x-component-props":{code:"RELATION_NEW",disabled:!0},"x-decorator":"FormItem"},companyType:{type:"string",title:i18nExpression("vendorMod.companyType"),"x-component":"DictSelect","x-component-props":{code:"COMPANY_NATURE_NEW",disabled:!0},"x-decorator":"FormItem"}}}}},afterChange:{type:"void","x-component":"div","x-component-props":{class:"formClassAllChange"},properties:{afterChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.afterChange"}},companyTypeAfter:{type:"object","x-query-engine-skip":!0,"x-component":"FormGrid","x-component-props":{class:"forms"},properties:{domesticAndForeignRelations:{type:"string",title:i18nExpression("cusEntry.vendorMod.domesticAndForeignRelations"),"x-component":"DictSelect","x-component-props":{code:"RELATION_NEW"},"x-reactions":expression(`() => {
                const newData = $form.query('.companyTypeAfter.overseasRelation')?.take()?.value || null
                const oldData = $form.query('.companyTypeBefore.overseasRelation')?.take()?.value || null
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),"x-decorator":"FormItem",...requiredValidatorSegment},companyType:{type:"string",title:i18nExpression("vendorMod.companyType"),"x-reactions":expression(`() => {
                const newData = $form.query('.companyTypeAfter.companyType')?.take()?.value || null
                const oldData = $form.query('.companyTypeBefore.companyType')?.take()?.value || null
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"COMPANY_NATURE_NEW"},...requiredValidatorSegment}}}}}},enterpriseThreeCertificates={beforeChange:{type:"void","x-component":"div","x-component-props":{class:"formClassAllChange"},properties:{beforeChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.beforeChange"}},enterpriseThreeCertificatesBefore:{type:"object","x-query-engine-skip":!0,"x-component":"FormGrid","x-component-props":{class:"forms"},properties:{businessLicense:{type:"string","x-hidden":!0},businessLicenseFileId:{type:"string",title:`{{{
              functional: true,
              render (h) {
                return h('span', [
                  $t('cusEntry.vendorMod.businessLicense'),
                  h('span', {
                    style: {
                      color: 'red'
                    }
                  }, $t('cusEntry.vendorMod.uploadFiles'))
                ])
              }
            }}}`,"x-component":"SrmCommonFile","x-component-props":{disabled:!0,"extra-data":{uploadType:"DEF",sourceType:"WEB_APP",fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},"default-file":{fileId:expression("$self.value"),fileName:expression("$form.query('.enterpriseThreeCertificatesBefore.businessLicense').take()?.value")}},"x-decorator":"FormItem"},companyName:{type:"string",title:i18nExpression("vendorMod.companyName"),"x-component-props":{disabled:!0},"x-decorator":"FormItem"},companyShortName:{type:"string",title:i18nExpression("vendorMod.companyShortName"),"x-disabled":!0,"x-decorator":"FormItem"},companyEnName:{type:"string",title:i18nExpression("cusEntry.vendorMod.companyEnName"),"x-disabled":!0,"x-decorator":"FormItem"},lcCode:{type:"string",title:i18nExpression("vendorMod.lcCode"),"x-disabled":!0,"x-decorator":"FormItem"},enterpriseNo:{type:"string",title:i18nExpression("cusEntry.vendorMod.enterpriseNo"),"x-decorator":"FormItem","x-component-props":{disabled:!0}},extKpp:{type:"string",title:i18nExpression("cusEntry.vendorMod.extKpp"),"x-decorator":"FormItem","x-component-props":{disabled:!0}},legalPerson:{type:"string",title:i18nExpression("vendorMod.legalPerson"),"x-component-props":{disabled:!0},"x-decorator":"FormItem"},registCurrency:{type:"string","x-hidden":!0,"x-decorator":"FormItem"},registeredCapital:{type:"number","x-visible":expression("$form.query('.companyTypeAfter.companyType').take()?.value != 'GETI'"),"x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.registeredCapital')"),"x-component-props":{disabled:!0,class:"input-with-select"},"x-content":{append:expression(`observer({
                  render(h) {
                    const targetField = $form.query('.enterpriseThreeCertificatesBefore.registCurrency').take()
                    return h("div", {class: "bzBox"}, [
                      h("label", {class: "bzTitle"}, "币种"),
                        h(DictSelect, {
                          props: {
                            code: 'currency',
                            value: targetField.value
                          },
                          attrs: {
                            disabled: true
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
              `)}},ifLongPeriod:{type:"string",title:i18nExpression("cusEntry.vendorMod.ifLongTermSupplier"),"x-disabled":!0,"x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"},"x-decorator":"FormItem"},businessStartDate:{...yearMonthDaySelectorSegment,title:i18nExpression("vendorMod.businessStartFrom"),"x-disabled":!0,"x-decorator":"FormItem"},businessEndDate:{type:"date","x-hidden":!0},companyCreationDate:{...yearMonthDaySelectorSegment,title:i18nExpression("vendorMod.creationDate"),"x-disabled":!0,"x-decorator":"FormItem"},businessScope:{type:"string",title:i18nExpression("vendorMod.businessScope"),"x-component-props":{type:"textarea",maxlength:2e3},"x-disabled":!0,"x-decorator":"FormItem"}}}}},afterChange:{type:"void","x-component":"div","x-component-props":{class:"formClassAllChange"},properties:{afterChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.afterChange"}},enterpriseThreeCertificatesAfter:{type:"object","x-query-engine-skip":!0,"x-component":"FormGrid","x-component-props":{class:"forms"},properties:{businessLicense:{type:"string","x-hidden":!0},businessLicenseFileId:{type:"string",title:`{{{
              functional: true,
              render (h) {
                return h('span', [
                  $t('cusEntry.vendorMod.businessLicense'),
                  h('span', {
                    style: {
                      color: 'red'
                    }
                  }, $t('cusEntry.vendorMod.uploadFiles'))
                ])
              }
            }}}`,"x-component":"SrmCommonFile","x-component-props":{disabled:expression("$form.readPretty"),"extra-data":{uploadType:"DEF",sourceType:"WEB_APP",fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},"default-file":{fileId:expression("$self.value"),fileName:expression("$form.query('.enterpriseThreeCertificatesAfter.businessLicense').take()?.value")},"@on-change":expression(`(file) => {
                if (file && file.file) {
                  const { fileId = null, fileName = null } = file.file || {}
                  $form.query('.enterpriseThreeCertificatesAfter.businessLicenseFileId').take().value = fileId.toString()
                  $form.query('.enterpriseThreeCertificatesAfter.businessLicense').take().value = fileName
                  // 读取图片信息
                  // app.$http({
                  //   url: '/api-pj/ocr/recognizeLcImage',
                  //   method: 'GET',
                  //   params: { fileuploadId: fileId },
                  //   loading: true
                  // }).then(res => {
                  //   const {
                  //     regNum,
                  //     person,
                  //     name,
                  //     address,
                  //     business,
                  //     businessEndDate,
                  //     businessStartDate,
                  //     capital,
                  //     period,
                  //     setDate,
                  //     type
                  //   } = res.data
                  //   let form = $form.query('enterpriseThreeCertificatesAfter').get('value')
                  //   form.companyName = name
                  //   form.companyType = type
                  //   form.legalPerson = person
                  //   // form.lcCode = regNum
                  //   form.businessStartDate = businessStartDate
                  //   form.businessEndDate = businessEndDate
                  //   form.companyAddress = address
                  //   form.businessScope = business
                  //   const [year, month, day] = setDate.replace(/[^\\d]/g, '-').split('-')
                  //   const createDate = year + '-' + month + '-' + day
                  //   form.companyCreationDate = app.$dayjs(createDate).format('YYYY-MM-DD')
                  // })
                  // .catch(err => {
                  //   console.log(err)
                  // })
                } else {
                  $form.query('.enterpriseThreeCertificatesAfter.businessLicenseFileId').take().value = null
                  $form.query('.enterpriseThreeCertificatesAfter.businessLicense').take().value = null
                }
              }`)},"x-decorator":"FormItem","x-reactions":expression(`() => {
              const newData = $form.query('.enterpriseThreeCertificatesAfter.businessLicenseFileId').take()?.value
              const oldData = $form.query('.enterpriseThreeCertificatesBefore.businessLicenseFileId').take()?.value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`),...requiredValidatorSegment},companyName:{type:"string",title:i18nExpression("vendorMod.companyName"),"x-component-props":{disabled:expression("$form.readPretty")},"x-reactions":expression(`() => {
                const newData = $form.query('.enterpriseThreeCertificatesAfter.companyName').take()?.value
                const oldData = $form.query('.enterpriseThreeCertificatesBefore.companyName').take()?.value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),"x-decorator":"FormItem",...requiredValidatorSegment},companyShortName:{type:"string",title:i18nExpression("vendorMod.companyShortName"),"x-disabled":expression("$form.readPretty"),"x-reactions":expression(`() => {
                const newData = $form.query('.enterpriseThreeCertificatesAfter.companyShortName').take().value
                const oldData = $form.query('.enterpriseThreeCertificatesBefore.companyShortName').take().value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),"x-decorator":"FormItem",...requiredValidatorSegment},companyEnName:{type:"string",title:i18nExpression("cusEntry.vendorMod.companyEnName"),"x-decorator":"FormItem","x-reactions":expression(`() => {
              const newData = $form.query('.enterpriseThreeCertificatesAfter.companyEnName').take().value
              const oldData = $form.query('.enterpriseThreeCertificatesBefore.companyEnName').take().value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`)},lcCode:{type:"string",title:i18nExpression("vendorMod.lcCode"),"x-reactions":expression(`() => {
                const newData = $form.query('.enterpriseThreeCertificatesAfter.lcCode').take().value
                const oldData = $form.query('.enterpriseThreeCertificatesBefore.lcCode').take().value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),"x-disabled":!0,"x-decorator":"FormItem",...requiredValidatorSegment},enterpriseNo:{type:"string",title:i18nExpression("cusEntry.vendorMod.enterpriseNo"),"x-reactions":expression(`() => {
              const newData = $form.query('.enterpriseThreeCertificatesAfter.enterpriseNo').take().value
              const oldData = $form.query('.enterpriseThreeCertificatesBefore.enterpriseNo').take().value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`),"x-disabled":expression("$form.readPretty"),"x-decorator":"FormItem",...requiredValidatorSegment},extKpp:{type:"string",title:i18nExpression("cusEntry.vendorMod.extKpp"),"x-decorator":"FormItem","x-component-props":{disabled:expression("$form.readPretty")},"x-reactions":expression(`() => {
                const newData = $form.query('.enterpriseThreeCertificatesAfter.extKpp').take()?.value
                const oldData = $form.query('.enterpriseThreeCertificatesBefore.extKpp').take()?.value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`)},legalPerson:{type:"string",title:i18nExpression("vendorMod.legalPerson"),"x-component-props":{disabled:expression("$form.readPretty")},"x-reactions":expression(`() => {
                const newData = $form.query('.enterpriseThreeCertificatesAfter.legalPerson').take()?.value
                const oldData = $form.query('.enterpriseThreeCertificatesBefore.legalPerson').take()?.value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),"x-decorator":"FormItem",...requiredValidatorSegment},registCurrency:{type:"string","x-hidden":!0},registeredCapital:{type:"number","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.registeredCapital')"),"x-component-props":{disabled:expression("$form.readPretty"),class:"input-with-select","@change":expression(`(value) => {
                $self.value = value.replace(/[^\\d.]/g, '')
              }`)},"x-reactions":expression(`() => {
                const newData = $form.query('.enterpriseThreeCertificatesAfter.registeredCapital').take()?.value
                const oldData = $form.query('.enterpriseThreeCertificatesBefore.registeredCapital').take()?.value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),"x-content":{append:expression(`observer({
                  render(h) {
                    const targetField = $form.query('.enterpriseThreeCertificatesAfter.registCurrency').take()
                    const beforeValue = $form.query('.enterpriseThreeCertificatesBefore.registCurrency').take().value
                    console.log($self.readPretty)
                    return h("div", {class: "bzBox"}, [
                      h("label", {class: "bzTitle"}, "币种"),
                        h(DictSelect, {
                          props: {
                            value: targetField.value,
                            code: 'currency',
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
              `)},"x-validator":{required:expression("!['GETI','FEIYINGLI'].includes($form.query('.companyTypeAfter.companyType').take().value)"),message:i18nExpression("vendorMod.msgRegisteredCapital")}},ifLongPeriod:{type:"string",title:i18nExpression("cusEntry.vendorMod.ifLongTermSupplier"),"x-component":"DictSelect","x-component-props":{code:"YES_OR_NO","@change":expression(`value => {
                if (value === 'Y') {
                  $form.query('enterpriseThreeCertificatesAfter.businessStartDate').take().value = null
                  $form.query('enterpriseThreeCertificatesAfter.businessEndDate').take().value = null
                }
              }`)},"x-reactions":expression(`() => {
              const newData = $form.query('.enterpriseThreeCertificatesAfter.ifLongPeriod').take().value
              const oldData = $form.query('.enterpriseThreeCertificatesBefore.ifLongPeriod').take().value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`),"x-decorator":"FormItem",...requiredValidatorSegment},businessStartDate:{...yearMonthDaySelectorSegment,title:i18nExpression("vendorMod.businessStartFrom"),"x-reactions":expression(`() => {
                const newData = $form.query('.enterpriseThreeCertificatesAfter.businessStartDate').take().value
                const oldData = $form.query('.enterpriseThreeCertificatesBefore.businessStartDate').take().value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),"x-disabled":expression("$form.readPretty"),"x-decorator":"FormItem"},businessEndDate:{type:"date","x-hidden":!0},companyCreationDate:{...yearMonthDaySelectorSegment,title:i18nExpression("vendorMod.creationDate"),"x-disabled":expression("$form.readPretty"),"x-reactions":expression(`() => {
                const newData = $form.query('.enterpriseThreeCertificatesAfter.companyCreationDate').take().value
                const oldData = $form.query('.enterpriseThreeCertificatesBefore.companyCreationDate').take().value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),"x-decorator":"FormItem",...requiredValidatorSegment},businessScope:{type:"string",title:i18nExpression("vendorMod.businessScope"),"x-reactions":expression(`() => {
              const newData = $form.query('.enterpriseThreeCertificatesAfter.businessScope').take().value
              const oldData = $form.query('.enterpriseThreeCertificatesBefore.businessScope').take().value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`),"x-disabled":expression("$form.readPretty"),"x-decorator":"FormItem","x-component-props":{type:"textarea",maxlength:2e3},...requiredValidatorSegment}}}}}},companyBaseInfo={beforeChange:{type:"void","x-component":"div","x-component-props":{class:"formClassAllChange"},properties:{beforeChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.beforeChange"}},companyBaseInfoBefore:{type:"object","x-query-engine-skip":!0,"x-component":"FormGrid","x-component-props":{class:"forms"},properties:{ceeaBusinessModel:{type:"string",title:i18nExpression("vendorMod.bizModel"),"x-component":"DictSelect","x-component-props":{code:"BIZ_MODEL",disabled:!0,multiple:!0},"x-decorator":"FormItem"},ceeaIfListed:{title:i18nExpression("vendorMod.ifListed"),...radioGroupByYOrNSegment,"x-component-props":{disabled:!0}},ceeaListedTime:{...yearMonthDaySelectorSegment,"x-visible":"{{$form.query('.companyBaseInfoBefore.ceeaIfListed').take()?.value == 'Y'}}","x-decorator":"FormItem","x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],disabled:!0},title:expression("$t('vendorMod.listedDate')"),"x-validator":{required:!0,message:i18nExpression("common.marketTime")}},companyCountry:{type:"string",title:i18nExpression("components.address.country"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"country",placeholder:expression("$t('common.pleaseSelect')"),disabled:!0},"x-reactions":expression(`() => {
              const data = $taxDictClass.getDictDetail('country', $self.value)
              $form.query('.companyBaseInfoBefore.companyProvince').take()?.setComponentProps({ code: data ? data.description : undefined })
            }`)},companyProvince:{type:"string",title:i18nExpression("components.address.area"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{"custom-select-type":"PROVINCE",placeholder:expression("$t('common.pleaseSelect')"),disabled:!0}},companyCity:{type:"string",title:i18nExpression("components.address.city"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:expression("$form.query('.companyBaseInfoBefore.companyProvince').take()?.value || ''"),"custom-select-type":"CITY",placeholder:expression("$t('common.pleaseSelect')"),disabled:!0}},companyAddress:{type:"string",title:i18nExpression("cusEntry.vendorMod.detailAddress"),"x-disabled":!0,"x-decorator":"FormItem"},ceeaHasParentCompany:{title:i18nExpression("cusEntry.vendorMod.ifParentCompany"),...radioGroupByYOrNSegment,"x-component-props":{disabled:!0}},ceeaParentCompanyName:{type:"string","x-decorator":"FormItem","x-component":"Input","x-visible":"{{$form.query('.companyBaseInfoBefore.ceeaHasParentCompany').take()?.value == 'Y'}}",title:i18nExpression("cusEntry.vendorMod.parentCompanyName"),"x-component-props":{disabled:!0}},groupCountry:{type:"string",title:i18nExpression("cusEntry.vendorMod.parentCompanyCountry"),"x-visible":"{{$form.query('.companyBaseInfoBefore.ceeaHasParentCompany').take().value == 'Y'}}","x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"country",placeholder:expression("$t('common.pleaseSelect')"),disabled:!0}},ceeaCompanyIntro:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.companyProfile')"),"x-component-props":{disabled:!0,type:"textarea",maxlength:2e3},"x-decorator-props":{gridSpan:3}}}}}},afterChange:{type:"void","x-component":"div","x-component-props":{class:"formClassAllChange"},properties:{afterChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.afterChange"}},companyBaseInfoAfter:{type:"object","x-query-engine-skip":!0,"x-component":"FormGrid","x-component-props":{class:"forms"},properties:{ceeaBusinessModel:{type:"string",title:i18nExpression("vendorMod.bizModel"),"x-component":"DictSelect","x-component-props":{code:"BIZ_MODEL",multiple:!0},"x-reactions":expression(`() => {
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
            }`),"x-component-props":{}},ceeaListedTime:{...yearMonthDaySelectorSegment,"x-visible":"{{$form.query('.companyBaseInfoAfter.ceeaIfListed').take()?.value == 'Y'}}","x-decorator":"FormItem","x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],disabled:expression("$form.readPretty")},"x-reactions":expression(`() => {
                const newData = $form.query('.companyBaseInfoAfter.ceeaListedTime').take()?.value
                const oldData = $form.query('.companyBaseInfoBefore.ceeaListedTime').take()?.value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),title:expression("$t('vendorMod.listedDate')"),"x-validator":{required:!0,message:i18nExpression("common.marketTime")}},companyCountry:{type:"string",title:i18nExpression("components.address.country"),"x-component":"DictSelect","x-reactions":expression(`() => {
              const newData = $form.query('.companyBaseInfoAfter.companyCountry').take().value
              const oldData = $form.query('.companyBaseInfoBefore.companyCountry').take().value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
              const data = $taxDictClass.getDictDetail('country', newData)
              $form.query('.companyBaseInfoAfter.companyProvince').take()?.setComponentProps({ code: data ? data.description : undefined })
            }`),"x-component-props":{code:"country",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$form.readPretty"),"@change":expression(`(val) => {
                $form.query('.companyBaseInfoAfter.companyProvince').take().value = ''
                $form.query('.companyBaseInfoAfter.companyCity').take().value = ''
              }`)},"x-decorator":"FormItem"},companyProvince:{type:"string",title:i18nExpression("components.address.area"),"x-component":"DictSelect","x-reactions":expression(`() => {
              const newData = $form.query('.companyBaseInfoAfter.companyProvince').take().value
              const oldData = $form.query('.companyBaseInfoBefore.companyProvince').take().value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`),"x-component-props":{"custom-select-type":"PROVINCE",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$form.readPretty || !['CN', 'RU'].includes($form.query('.companyBaseInfoAfter.companyCountry').take().value)"),"@change":expression(`(val) => {
                $form.query('.companyBaseInfoAfter.companyCity').take().value = ''
              }`)},"x-decorator":"FormItem"},companyCity:{type:"string",title:i18nExpression("vendorMod.city"),"x-component":"DictSelect","x-reactions":expression(`() => {
              const newData = $form.query('.companyBaseInfoAfter.companyCity').take().value
              const oldData = $form.query('.companyBaseInfoBefore.companyCity').take().value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`),"x-component-props":{code:expression("$form.query('.companyBaseInfoAfter.companyProvince').take().value || ''"),"custom-select-type":"CITY",emptyOptionCanCreat:!0,placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$form.readPretty || !$form.query('.companyBaseInfoAfter.companyProvince').take().value")},"x-decorator":"FormItem"},companyAddress:{type:"string",title:i18nExpression("components.address.detailAddress2"),"x-reactions":expression(`() => {
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
            }`),title:i18nExpression("cusEntry.vendorMod.parentCompanyName"),"x-component-props":{disabled:expression("$form.readPretty")},"x-validator":{required:!0,message:i18nExpression("cusEntry.vendorMod.parentCompanyNameEnterTips")}},groupCountry:{type:"string",title:i18nExpression("cusEntry.vendorMod.parentCompanyCountry"),"x-visible":"{{$form.query('.companyBaseInfoAfter.ceeaHasParentCompany').take().value == 'Y'}}","x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"country",placeholder:expression("$t('common.pleaseSelect')")},"x-reactions":expression(`() => {
              const newData = $form.query('.companyBaseInfoAfter.groupCountry').take().value
              const oldData = $form.query('.companyBaseInfoBefore.groupCountry').take().value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`),"x-validator":{required:!0,message:i18nExpression("cusEntry.tipMessage.parentCompanyCountryMsg")}},ceeaCompanyIntro:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.companyProfile')"),"x-component-props":{disabled:expression("$form.readPretty"),type:"textarea",maxlength:2e3},"x-reactions":expression(`() => {
                const newData = $form.query('.companyBaseInfoAfter.ceeaCompanyIntro').take().value
                const oldData = $form.query('.companyBaseInfoBefore.ceeaCompanyIntro').take().value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),"x-decorator-props":{gridSpan:3}}}}}}},personBaseInfo={beforeChange:{type:"void","x-component":"div","x-component-props":{class:"formClassAllChange"},properties:{beforeChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.beforeChange"}},personBaseInfoBefore:{type:"object","x-query-engine-skip":!0,"x-component":"FormGrid","x-component-props":{class:"forms"},properties:{businessLicenseFileId:{type:"string",title:i18nExpression("cusEntry.vendorMod.frontOfIdCard"),"x-decorator":"FormItem","x-component":"SrmCommonFile","x-component-props":{"extra-data":{uploadType:"DEF",sourceType:"WEB_APP",fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},"default-file":{fileId:expression("$form.query('.personBaseInfoBefore').get('value').businessLicenseFileId"),fileName:expression("$form.query('.personBaseInfoBefore').get('value').businessLicense")},readonly:!0}},extIdCardOppositeFileName:{type:"string","x-hidden":!0},extIdCardOppositeFileId:{type:"string",title:i18nExpression("cusEntry.vendorMod.backOfIdCard"),"x-decorator":"FormItem","x-component":"SrmCommonFile","x-component-props":{"extra-data":{uploadType:"DEF",sourceType:"WEB_APP",fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},"default-file":{fileId:expression("$form.query('.personBaseInfoBefore').get('value')?.extIdCardOppositeFileId"),fileName:expression("$form.query('.personBaseInfoBefore').get('value').extIdCardOppositeFileName")},disabled:!0}},companyName:{type:"string","x-decorator":"FormItem",title:i18nExpression("cusEntry.vendorMod.companyNameOrPersonName"),"x-component-props":{disabled:!0},...requiredValidatorSegment},companyShortName:{type:"string","x-decorator":"FormItem",title:i18nExpression("cusEntry.vendorMod.personalAbbreviation"),"x-component-props":{disabled:!0},...requiredValidatorSegment},businessLicense:{type:"string","x-hidden":"true"},idNumber:{type:"string","x-decorator":"FormItem",title:i18nExpression("cusEntry.vendorMod.idNo"),"x-component-props":{disabled:!0}},validityPeriodOfCard:{...yearMonthDaySelectorSegment,"x-decorator":"FormItem",title:i18nExpression("cusEntry.vendorMod.validityPeriodOfCard"),"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],type:"daterange",disabled:!0}},lcCode:{type:"string",title:i18nExpression("vendorMod.lcCode"),"x-decorator":"FormItem","x-component-props":{disabled:!0}},enterpriseNo:{type:"string",title:i18nExpression("cusEntry.vendorMod.enterpriseNo"),"x-decorator":"FormItem","x-component-props":{disabled:!0}},extKpp:{type:"string",title:i18nExpression("cusEntry.vendorMod.extKpp"),"x-decorator":"FormItem","x-component-props":{disabled:!0}},businessScope:{type:"string",title:i18nExpression("cusEntry.vendorMod.mainBusinessScope"),"x-decorator":"FormItem","x-component-props":{disabled:!0}},companyCountry:{type:"string",title:i18nExpression("components.address.country"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"country",placeholder:expression("$t('common.pleaseSelect')"),disabled:!0},"x-reactions":expression(`() => {
              const data = $taxDictClass.getDictDetail('country', $self.value)
              $form.query('.personBaseInfoBefore.companyProvince').take()?.setComponentProps({ code: data ? data.description : undefined })
            }`),...requiredValidatorSegment},companyProvince:{type:"string",title:i18nExpression("components.address.area"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{"custom-select-type":"PROVINCE",placeholder:expression("$t('common.pleaseSelect')"),disabled:!0},...requiredValidatorSegment},companyCity:{type:"string",title:i18nExpression("components.address.city"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:expression("$form.query('.personBaseInfoBefore.companyProvince').take()?.value"),"custom-select-type":"CITY",placeholder:expression("$t('common.pleaseSelect')"),disabled:!0},...requiredValidatorSegment},companyAddress:{type:"string",title:i18nExpression("cusEntry.vendorMod.detailAddress"),"x-disabled":!0,"x-decorator":"FormItem",...requiredValidatorSegment}}}}},afterChange:{type:"void","x-component":"div","x-component-props":{class:"formClassAllChange"},properties:{afterChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.afterChange"}},personBaseInfoAfter:{type:"object","x-query-engine-skip":!0,"x-component":"FormGrid","x-component-props":{class:"forms"},properties:{businessLicenseFileId:{type:"string",title:i18nExpression("cusEntry.vendorMod.frontOfIdCard"),"x-decorator":"FormItem","x-component":"SrmCommonFile","x-component-props":{disabled:!0,"extra-data":{uploadType:"DEF",sourceType:"WEB_APP",fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},"default-file":{fileId:expression("$form.query('.personBaseInfoAfter').get('value').businessLicenseFileId"),fileName:expression("$form.query('.personBaseInfoAfter').get('value').businessLicense")},"@on-change":expression(`({file}) => {
                 const { fileId, fileName } = file || {}
                 $form.query($self.parent.address).get('value').businessLicenseFileId = fileId
                 $form.query($self.parent.address).get('value').businessLicense = fileName
              }`)},"x-reactions":expression(`() => {
              const newData = $form.query('.personBaseInfoAfter.businessLicenseFileId').take()?.value
              const oldData = $form.query('.personBaseInfoBefore.businessLicenseFileId').take()?.value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`)},extIdCardOppositeFileName:{type:"string","x-hidden":!0},extIdCardOppositeFileId:{type:"string",title:i18nExpression("cusEntry.vendorMod.backOfIdCard"),"x-decorator":"FormItem","x-component":"SrmCommonFile","x-component-props":{disabled:!0,"extra-data":{uploadType:"DEF",sourceType:"WEB_APP",fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},"default-file":{fileId:expression("$form.query('.personBaseInfoAfter').get('value')?.extIdCardOppositeFileId"),fileName:expression("$form.query('.personBaseInfoAfter').get('value').extIdCardOppositeFileName")},"@on-change":expression(`({file}) => {
                const { fileId, fileName } = file || {}
                $form.query('.personBaseInfoAfter').get('value').extIdCardOppositeFileId = fileId
                $form.query('.personBaseInfoAfter').get('value').extIdCardOppositeFileName = fileName
             }`)},"x-reactions":expression(`() => {
              const newData = $form.query('.personBaseInfoAfter.extIdCardOppositeFileId').take()?.value
              const oldData = $form.query('.personBaseInfoBefore.extIdCardOppositeFileId').take()?.value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`)},companyName:{type:"string","x-decorator":"FormItem",title:i18nExpression("cusEntry.vendorMod.companyNameOrPersonName"),"x-reactions":expression(`() => {
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
            }`),...requiredValidatorSegment},validityPeriodOfCard:{...yearMonthDaySelectorSegment,"x-decorator":"FormItem",title:i18nExpression("cusEntry.vendorMod.validityPeriodOfCard"),"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],type:"daterange"},"x-reactions":expression(`() => {
              const newData = $form.query('.personBaseInfoAfter.validityPeriodOfCard').take()?.value
              const oldData = $form.query('.personBaseInfoBefore.validityPeriodOfCard').take()?.value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`)},lcCode:{type:"string",title:i18nExpression("vendorMod.lcCode"),"x-decorator":"FormItem","x-component-props":{disabled:!0,"x-reactions":expression(`() => {
                const newData = $form.query('.personBaseInfoAfter.lcCode').take()?.value
                const oldData = $form.query('.personBaseInfoBefore.lcCode').take()?.value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
              }`)}},enterpriseNo:{type:"string",title:i18nExpression("cusEntry.vendorMod.enterpriseNo"),"x-decorator":"FormItem","x-component-props":{disabled:!0},"x-reactions":expression(`() => {
              const newData = $form.query('.personBaseInfoAfter.enterpriseNo').take()?.value
              const oldData = $form.query('.personBaseInfoBefore.enterpriseNo').take()?.value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`)},extKpp:{type:"string",title:i18nExpression("cusEntry.vendorMod.extKpp"),"x-decorator":"FormItem","x-reactions":expression(`() => {
              const newData = $form.query('.personBaseInfoAfter.extKpp').take()?.value
              const oldData = $form.query('.personBaseInfoBefore.extKpp').take()?.value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`)},businessScope:{type:"string",title:i18nExpression("cusEntry.vendorMod.mainBusinessScope"),"x-decorator":"FormItem","x-reactions":expression(`() => {
              const newData = $form.query('.personBaseInfoAfter.businessScope').take()?.value
              const oldData = $form.query('.personBaseInfoBefore.businessScope').take()?.value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`)},companyCountry:{type:"string",title:i18nExpression("components.address.country"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"country",placeholder:expression("$t('common.pleaseSelect')"),"@change":expression(`(val) => {
                $form.query('.personBaseInfoAfter.companyProvince').take().value = ''
                $form.query('.personBaseInfoAfter.companyCity').take().value = ''
              }`)},"x-reactions":expression(`() => {
              const newData = $form.query('.personBaseInfoAfter.companyCountry').take()?.value
              const oldData = $form.query('.personBaseInfoBefore.companyCountry').take()?.value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
              const data = $taxDictClass.getDictDetail('country', newData)
              $form.query('.personBaseInfoAfter.companyProvince').take()?.setComponentProps({ code: data ? data.description : undefined })
            }`),...requiredValidatorSegment},companyProvince:{type:"string",title:i18nExpression("components.address.area"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{"custom-select-type":"PROVINCE",disabled:expression("$form.readPretty || !['CN', 'RU'].includes($form.query('.personBaseInfoAfter.companyCountry').take().value)"),placeholder:expression("$t('common.pleaseSelect')"),"@change":expression(`(val) => {
                $form.query('.personBaseInfoAfter.companyCity').take().value = ''
              }`)},"x-reactions":expression(`() => {
              const newData = $form.query('.personBaseInfoAfter.companyProvince').take()?.value
              const oldData = $form.query('.personBaseInfoBefore.companyProvince').take()?.value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`),...requiredValidatorSegment},companyCity:{type:"string",title:i18nExpression("components.address.city"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:expression("$form.query('.personBaseInfoAfter.companyProvince').take()?.value || ''"),"custom-select-type":"CITY",emptyOptionCanCreat:!0,disabled:expression("$form.readPretty || !$form.query('.personBaseInfoAfter.companyProvince').take().value"),placeholder:expression("$t('common.pleaseSelect')")},"x-reactions":expression(`() => {
              const newData = $form.query('.personBaseInfoAfter.companyCity').take()?.value
              const oldData = $form.query('.personBaseInfoBefore.companyCity').take()?.value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`),...requiredValidatorSegment},companyAddress:{type:"string",title:i18nExpression("cusEntry.vendorMod.detailAddress"),"x-decorator":"FormItem","x-reactions":expression(`() => {
              const newData = $form.query('.personBaseInfoAfter.companyAddress').take()?.value
              const oldData = $form.query('.personBaseInfoBefore.companyAddress').take()?.value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`),...requiredValidatorSegment}}}}}},contactData={beforeChange:{type:"void","x-component":"div","x-component-props":{class:""},properties:{beforeChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.beforeChange"}},contactDataBefore:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!1,maxHeight:400,pagination:!1,sortable:!1},"x-query-engine-skip":!0,properties:generateXindexInOrder({contactName:{type:"string",title:i18nExpression("vendorMod.nickname"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:!0},"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},ceeaDeptName:{type:"string",title:i18nExpression("vendorMod.department"),"x-render-table-column":{minWidth:100},"x-component-props":{disabled:!0}},position:{type:"string",title:i18nExpression("dataConfMod.position"),"x-render-table-column":{minWidth:100},"x-component-props":{disabled:!0}},ceeaContactMethod:{type:"string",title:i18nExpression("vendorMod.contactMethod"),"x-render-table-column":{minWidth:100},"x-component-props":{disabled:!0}},email:{type:"string",title:i18nExpression("vendorMod.email"),"x-render-table-column":{minWidth:100},"x-component-props":{disabled:!0}},ceeaDefaultContact:{type:"string",title:i18nExpression("dataConfMod.isDefault"),"x-render-table-column":{minWidth:100},"x-component":"Checkbox","x-component-props":{"true-label":"Y","false-label":"N",disabled:!0}},ceeaComments:{type:"string",title:i18nExpression("dataConfMod.remark"),"x-render-table-column":{minWidth:100},"x-component-props":{disabled:!0}}})}}},afterChange:{type:"void","x-component":"div","x-component-props":{class:""},properties:{afterChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.afterChange"}},toolbar:{type:"void","x-visible":expression("!$form.readPretty"),"x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void",title:i18nExpression("common.add"),"x-component-props":{type:"primary","@click":expression(`() => {
                 $self.query('contactInfoChanges')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
              }`)}}}},contactInfoChanges:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,maxHeight:400,primaryKey:"contactChangeId",cascadeDeletion:!0,pagination:!1,sortable:!1},"x-query-engine-skip":!0,properties:generateXindexInOrder({contactName:{type:"string",title:i18nExpression("vendorMod.nickname"),"x-render-table-column":{minWidth:120},"x-reactions":expression(`() => {
                const oldData = $form.query('contactDataBefore').get('value')[$self.index]?.contactName || null
                let className = redFunction(oldData, $self?.value)
                $self.setComponentProps({ class: className })
            }`),"x-component-props":{disabled:expression("$form.readPretty")},"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},ceeaDeptName:{type:"string",title:i18nExpression("vendorMod.department"),"x-render-table-column":{minWidth:100},"x-reactions":expression(`() => {
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
            }`),"x-component-props":{disabled:expression("$form.readPretty")},"x-validator":{required:!0,message:i18nExpression("common.requiredField"),validator:expression(`(value, rule) => {
                if(value && !validatePhone(value)) {
                  // 请输入格式正确的电话号码
                  return $t('vendorMod.correctPhoneNumber')
                }
              }`)}},email:{type:"string",title:i18nExpression("vendorMod.email"),"x-render-table-column":{minWidth:100},"x-reactions":expression(`() => {
                const oldData = $form.query('contactDataBefore').get('value')[$self.index]?.email || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),"x-component-props":{disabled:expression("$form.readPretty")},"x-validator":{required:!0,message:i18nExpression("common.requiredField"),validator:expression(`(value, rule) => {
                if(value && !validEmail(value)){
                  // 请输入格式正确的邮箱地址
                  return $t('vendorMod.correctEmail')
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
              }`)}},ceeaComments:{type:"string",title:i18nExpression("dataConfMod.remark"),"x-render-table-column":{minWidth:100},"x-reactions":expression(`() => {
                const oldData = $form.query('contactDataBefore').get('value')[$self.index]?.ceeaComments || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),"x-component-props":{disabled:expression("$form.readPretty")}},operation:{type:"void",title:"{{$t('common.operation')}}","x-visible":expression("!$form.readPretty"),"x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{type:"text","@click":expression(`({ row }) => {
                      $table.remove($self.index)
                  }`)}}}}})}}}},serviceRange={beforeChange:{type:"void","x-component":"div","x-component-props":{class:""},properties:{beforeChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.beforeChange"}},serviceRangeBefore:{type:"array","x-component":"ArrayItems",items:{type:"void",properties:{tableForm:{type:"object",properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:{categoryName:{type:"string","x-decorator":"FormItem","x-component":"QuickSearchWrapper","x-component-props":{showKey:"categoryName",disabled:!0,name:"scc_base_purchase_category4","@close-quicksearch":expression(`val => {
                          let row = $table.getRowByIndex($self.index)
                          row.categoryCode = val?.categoryCode || ''
                          row.categoryName = val?.categoryName || ''
                          row.categoryId = val?.categoryId || ''
                        }`)},title:"{{t('cusEntry.vendorMod.category', {index: $self.index + 1})}}"}}}}},list:{type:"array","x-component":"RenderTable","x-query-engine-skip":!0,"x-component-props":{preColumns:"seq",editMode:!1,maxHeight:250,pagination:!1,sortable:!1},properties:generateXindexInOrder({performanceAmount:{type:"string",title:i18nExpression("cusEntry.vendorMod.performance"),"x-render-table-column":{minWidth:120}},mainCustom:{type:"string",title:i18nExpression("cusEntry.vendorMod.mainCustomer"),"x-render-table-column":{minWidth:120}},fileId:{type:"string",title:i18nExpression("cusEntry.vendorMod.achievement"),"x-render-table-column":{minWidth:120},"x-component":"SrmCommonFile","x-component-props":{extraData:{fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},defaultFile:{fileId:expression("$self.value"),fileName:expression("$table.getRowByIndex($self.index)?.fileName")},readonly:!0}}})}}}}}},afterChange:{type:"void","x-component":"div","x-component-props":{class:""},properties:{afterChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.afterChange"}},toolbar:{type:"void","x-visible":expression("!$form.readPretty"),"x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void",title:i18nExpression("common.add"),"x-component-props":{type:"primary","@click":expression(`() => {
                $form.query('serviceRangeAfter').take(field => {
                  field.invoke('add', 'push')
                })
              }`)}}}},serviceRangeAfter:{type:"array","x-component":"ArrayItems","x-query-engine-skip":!0,items:{type:"void",properties:{tableForm:{type:"object",properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:{categoryName:{type:"string","x-decorator":"FormItem","x-component":"QuickSearchWrapper","x-component-props":{dialogLabel:i18nExpression("cusEntry.vendorMod.categoryNameTitle"),showKey:"categoryName",name:"scc_base_purchase_category4","@close-quicksearch":expression(`val => {
                          let list = $form.query('serviceRangeAfter').get('value')
                          let flag = false
                          for(let item of list){
                            item.tableForm.categoryCode == val.categoryCode && (flag = true)
                          }
                          if(flag){
                            // 服务范围内已经存在该品类
                            app.$message.error(t('cusEntry.vendorMod.serviceRangeCateTips'))
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
                      }`),title:"{{t('cusEntry.vendorMod.category', {index: $self.index + 1})}}"},formBtn:{type:"void","x-visible":expression("!$form.readPretty"),"x-component":"ButtonList","x-component-props":{style:{"margin-top":"5px"}},properties:{add:{type:"void","x-component-props":{type:"primary","@click":expression(`() => {
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
                  }`)},mainCustom:{type:"string",title:i18nExpression("cusEntry.vendorMod.mainCustomer"),"x-render-table-column":{minWidth:120},"x-reactions":expression(`() => {
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
                  }`)},operation:{type:"void",title:i18nExpression("common.operation"),"x-visible":expression("!$form.readPretty"),"x-render-table-column":{width:60,fiexd:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:i18nExpression("common.delete"),"x-component-props":{type:"text","@click":expression(`() => {
                          const { serciceCustomChangeId = null } = $table.getRowByIndex($self.index) || {}
                          const name = 'npmSerciceCustomChanges'
                          if (serciceCustomChangeId) {
                            let serciceCustomDelList = $form.query('state').get('data').serciceCustomDelList
                            serciceCustomDelList.push({
                              $delete: serciceCustomChangeId
                            })
                          }
                          $table.remove($self.index)
                        }`)}}}}})}}}}}}},qualificationInformation={beforeChange:{type:"void","x-component":"div",properties:{beforeChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.beforeChange"}},qualificationInfoBefore:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!1,maxHeight:400,pagination:!1,sortable:!1},"x-query-engine-skip":!0,properties:generateXindexInOrder({authNum:{type:"string",title:i18nExpression("cusEntry.vendorMod.certificateType"),"x-component":"DictSelect","x-component-props":{code:expression("'CERTIFICATE_TYPE_' + $form.query('state').get('data').userType")},"x-render-table-column":{minWidth:120}},startDate:{...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.startDate, '{y}-{m}-{d}')
              }`)},title:i18nExpression("cusEntry.vendorMod.startTime"),"x-render-table-column":{minWidth:120}},extIfEndDateRequired:{type:"string",title:i18nExpression("cusEntry.vendorMod.extIfEndDateRequired"),"x-render-table-column":{minWidth:130},"x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"}},endDate:{...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.endDate, '{y}-{m}-{d}')
              }`)},title:i18nExpression("cusEntry.vendorMod.endTime"),"x-render-table-column":{minWidth:120}},extCertificatePeriod:{type:"string",title:i18nExpression("cusEntry.vendorMod.extCertificatePeriod"),"x-render-table-column":{minWidth:120}},extIsMandatory:{type:"string","x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"},title:i18nExpression("dataConfMod.isRequested"),"x-render-table-column":{minWidth:120}},fileuploadId:{type:"number","x-component":"SrmCommonFile","x-component-props":{extraData:{fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},defaultFile:{fileId:expression("$table.getRowByIndex($self.index)?.fileuploadId"),fileName:expression("$table.getRowByIndex($self.index)?.authType")},readonly:!0},title:i18nExpression("cusEntry.vendorMod.file"),"x-render-table-column":{minWidth:120}},authDescription:{type:"string",title:i18nExpression("cusEntry.vendorMod.remark"),"x-render-table-column":{minWidth:120}}})}}},afterChange:{type:"void","x-component":"div","x-component-props":{class:""},properties:{afterChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.afterChange"}},qualificationInfoAfter:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,maxHeight:400,pagination:!1,sortable:!1,primaryKey:"managementAttachChangeId",cascadeDeletion:!0},"x-query-engine-skip":!0,"x-query-engine-relation":"managementAttachChanges:*",properties:generateXindexInOrder({authNum:{type:"string","x-component":"DictSelect","x-component-props":{code:expression("'CERTIFICATE_TYPE_' + $form.query('state').get('data').userType")},title:i18nExpression("cusEntry.vendorMod.certificateType"),"x-reactions":expression(`() => {
              const oldData = $form.query('qualificationInfoBefore').get('value')?.[$self.index]?.authNum || null
              let className = redFunction(oldData, $self?.value)
              $self.setComponentProps({ class: className })
            }`),"x-render-table-column":{minWidth:120},"x-read-pretty":!0},startDate:{...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
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
                  $table.getRowByIndex($self.index).extCertificatePeriod = 0 + $t('bidMod.heaven')
                  return
                }
                $table.getRowByIndex($self.index).extCertificatePeriod = dayDifference + $t('bidMod.heaven')
              }`)},title:i18nExpression("cusEntry.vendorMod.startTime"),"x-render-table-column":{minWidth:120},"x-reactions":expression(`() => {
              const oldData = $form.query('qualificationInfoBefore').get('value')?.[$self.index]?.startDate || null
              let className = redFunction(oldData, $self?.value)
              $self.setComponentProps({ class: className })
            }`),...editTableFormItemValid},extIfEndDateRequired:{type:"string",title:i18nExpression("cusEntry.vendorMod.extIfEndDateRequired"),"x-render-table-column":{minWidth:130},"x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"},...editTableFormItemValid},endDate:{type:"void","x-component":"Space",title:i18nExpression("cusEntry.vendorMod.endTime"),"x-render-table-column":{minWidth:120},properties:{endDate:{...yearMonthDaySelectorSegment,"x-visible":expression("$table.getRowByIndex($self.index).extIfEndDateRequired === 'Y'"),"x-component-props":{style:"width: 100%",...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                    parseTime(row.endDate, '{y}-{m}-{d}')
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
                      $table.getRowByIndex($self.index).extCertificatePeriod = 0 + $t('bidMod.heaven')
                      return
                    }
                    $table.getRowByIndex($self.index).extCertificatePeriod = dayDifference + $t('bidMod.heaven')
                  }`)},title:i18nExpression("cusEntry.vendorMod.endTime"),"x-render-table-column":{minWidth:120},"x-reactions":expression(`() => {
                  const oldData = $form.query('qualificationInfoBefore').get('value')?.[$self.index]?.endDate || null
                  let className = redFunction(oldData, $self?.value)
                  $self.setComponentProps({ class: className })
                }`)}}},extCertificatePeriod:{type:"string",title:i18nExpression("cusEntry.vendorMod.extCertificatePeriod"),"x-render-table-column":{minWidth:120},"x-read-pretty":!0},extIsMandatory:{type:"string","x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"},title:i18nExpression("dataConfMod.isRequested"),"x-render-table-column":{minWidth:120},"x-read-pretty":!0},fileuploadId:{type:"void","x-component":"Space",title:i18nExpression("cusEntry.vendorMod.file"),"x-render-table-column":{minWidth:120},properties:{starFlag:{type:"void","x-component":"Span","x-component-props":{style:"color: red"},"x-content":expression("$table.getRowByIndex($self.index).extIsMandatory === 'Y' ? '*' : null")},fileuploadId:{type:"number","x-component":"SrmCommonFile","x-component-props":{extraData:{fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},defaultFile:{fileId:expression("$table.getRowByIndex($self.index)?.fileuploadId"),fileName:expression("$table.getRowByIndex($self.index)?.authType")},readonly:!1,"@on-change":expression(`({file}) => {
                    const { fileId = null, fileName = '' } = file || {}
                    let row = $table.getRowByIndex($self.index)
                    row.fileuploadId = fileId
                    row.authType = fileName
                  }`)},title:i18nExpression("cusEntry.vendorMod.file"),"x-render-table-column":{minWidth:120},"x-reactions":expression(`() => {
                  const oldData = $form.query('qualificationInfoBefore').get('value')?.[$self.index]?.fileuploadId || null
                  let className = redFunction(oldData, $self?.value)
                  $self.setComponentProps({ class: className })
                }`)}}},authDescription:{type:"string",title:i18nExpression("cusEntry.vendorMod.remark"),"x-render-table-column":{minWidth:120},"x-reactions":expression(`() => {
              const oldData = $form.query('qualificationInfoBefore').get('value')?.[$self.index]?.remark || null
              let className = redFunction(oldData, $self?.value)
              $self.setComponentProps({ class: className })
            }`)}})}}}},attachFile={beforeChange:{type:"void","x-component":"div","x-component-props":{class:""},properties:{beforeChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.beforeChange"}},attachFileBefore:{type:"array","x-component":"FileDynamic","x-component-props":{"scene-module-code":"SCENE_SUPPLIER_ATTACHMENT",businessId:expression("$attrs.params.companyId || null"),editable:!1,"need-init":!1}}}},afterChange:{type:"void","x-component":"div","x-component-props":{class:""},properties:{afterChangeTitle:{type:"void","x-component":"changeTitle","x-component-props":{language:"supplierChange.afterChange"}},attachFileAfter:{type:"array","x-component":"FileDynamic","x-component-props":{"scene-module-code":"SCENE_SUPPLIER_ATTACHMENT",businessId:expression("$attrs.params?.changeId"),editable:expression("$attrs.params.flag != 'view'"),"need-init":!1}}}}},tabs={tab1:{type:"void","x-component":"FormTab.TabPane","x-component-props":{ref:"companyType",label:i18nExpression("vendorMod.companyType")},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").userType!=="PERSONAL"),properties:{div:{type:"void","x-component":"div","x-component-props":{class:"formClassWrap"},properties:{...companyType}}}},tab11:{type:"void","x-component":"FormTab.TabPane","x-component-props":{ref:"companyType",label:i18nExpression("cusEntry.vendorMod.baseInfo")},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").userType==="PERSONAL"),properties:{div:{type:"void","x-component":"div","x-component-props":{class:"formClassWrap"},properties:{...personBaseInfo}}}},tab2:{type:"void","x-component":"FormTab.TabPane","x-component-props":{ref:"enterpriseThreeCertificates",label:i18nExpression("vendorMod.enterpriseThreeCertificates")},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").userType!=="PERSONAL"),properties:{div:{type:"void","x-component":"div","x-component-props":{class:"formClassWrap"},properties:{...enterpriseThreeCertificates}}}},tab3:{type:"void","x-component":"FormTab.TabPane","x-component-props":{ref:"companyBaseInfo",label:i18nExpression("vendorMod.companyBaseInfo")},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").userType!=="PERSONAL"),properties:{div:{type:"void","x-component":"div","x-component-props":{class:"formClassWrap"},properties:{...companyBaseInfo}}}},tab4:{type:"void","x-component":"FormTab.TabPane","x-component-props":{ref:"contactInfo",label:i18nExpression("vendorMod.contactInfo"),class:""},properties:{...contactData}},tab8:{type:"void","x-component":"FormTab.TabPane","x-component-props":{ref:"serviceRange",label:i18nExpression("cusEntry.vendorMod.serviceRange")},properties:{...serviceRange}},tab9:{type:"void","x-component":"FormTab.TabPane","x-component-props":{ref:"qualificationInformation",label:i18nExpression("cusEntry.vendorMod.qualificationInformation"),class:""},properties:{...qualificationInformation}},tab10:{type:"void","x-component":"FormTab.TabPane","x-component-props":{ref:"otherAttachInfo",label:i18nExpression("vendorMod.otherAttachInfo"),class:""},properties:{...attachFile}}},_sfc_main$2=defineComponent({__name:"edit",setup(__props){const{app,emitTabRemove,t,vendor,http}=usePageHelper(),attrs=useAttrs(),workflowStatus=ref$1("DRAFT"),redFunction=(oldData,newData)=>{let className="";return oldData!=newData&&(className="redColorFont"),className},viewUpdateButton=$form=>{let bol;const changeStatus=attrs.params.row?.changeStatus||null;return changeStatus==="APPROVED"||changeStatus==="SUBMITTED"||changeStatus==="ABANDONED"||attrs.params?.flag==="view"?bol=!1:bol=!0,bol},initButtonConfig=($form,$t)=>{setTimeout(()=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance,changeStatus=attrs.params.row?.changeStatus||null;componentInstance.buttonConfigInfo.save.view=viewUpdateButton(),componentInstance.buttonConfigInfo.submit.view=viewUpdateButton()||changeStatus=="VENDOR_SUBMITTED",componentInstance.buttonConfigInfo.cancel.view=changeStatus!="VENDOR_SUBMITTED",componentInstance.buttonConfigInfo.close.view=!1;const approveStatus=attrs.params.row?.changeStatus||null;[null,"DRAFT"].includes(approveStatus)&&(componentInstance.buttonConfigInfo.save.name=$t("common.staging"),componentInstance.buttonConfigInfo.submit.name=$t("common.submit")),componentInstance.setWorkflowBusinessId(attrs.params.row?.changeId),componentInstance.setWorkflowTabDisabled(!["APPROVED","SUBMITTED","REJECTED","ABANDONED","VENDOR_SUBMITTED"].includes(attrs.params.row?.changeStatus)),componentInstance.setWorkflowBusinessVariables({})},50)},updateButtonConfig=$form=>{setTimeout(()=>{const componentInstance=$form.query(".SchemaWorkflow").take().componentProps.componentInstance;componentInstance.buttonConfigInfo.save.view=viewUpdateButton(),componentInstance.buttonConfigInfo.submit.view=viewUpdateButton(),componentInstance.buttonConfigInfo.cancel.view=!1,componentInstance.buttonConfigInfo.close.view=!1,componentInstance.setWorkflowBusinessId(attrs.params.row?.changeId),componentInstance.setWorkflowTabDisabled(!["APPROVED","SUBMITTED","REJECTED","ABANDONED"].includes(attrs.params.row?.changeStatus)),componentInstance.setWorkflowBusinessVariables({})},50)},schema=defineSchemas({state:{type:"void","x-component":"Fragment","x-hidden":!0,"x-data":{beforeChangeJson:null,companyChangeId:null,userType:"",serciceCustomDelList:[]}},InfoChange:{type:"void","x-decorator":"QueryEngine","x-component":"el-container","x-component-props":{class:"flex-container infoChange",direction:"vertical"},"x-query-engine":{service:"sup",actions:{query:{immediate:!0,tree:!0,ready:expression(`() => {
            initButtonConfig($form, t)
            const companyId = app.$store.getters.companyId
            fatchCompanyData(companyId, $form)
            return $attrs.params && $attrs.params?.changeId
          }`),autoFormatResult:!1,transformRequest:expression(`(data, headers) => {
            data.query = {
              "*":{},
              companyInfoChange: {'*': {}},
              contactInfoChanges: {'*': {}},
              siteInfoChanges: {'*': {}},
              fileuploadChanges: {'*': {}},
              operatingLogs: {'*': {}},
              npmCompanySizeChanges: { '*': {}},
              npmCateJournalChanges: { '*': {}, npmSerciceCustomChanges: { '*': {}} },
              managementAttachChanges: { '*': {}}
            }
            let req = {
              "filter": {
                  "changeId": {
                      eq: $attrs.params.changeId
                  }
              }
            }
            data.payload = req
            return data
          }`),transformResponse:expression(`(res) => {
            const ress = JSON.parse(res)
            const datas = ress.data.records[0]
            const isPerson = datas?.companyInfoChange?.overseasRelation === 'PERSONAL'
            const beforeChangeJson = JSON.parse(datas.beforeChangeJson)
            $form.query('state').get('data').beforeChangeJson = beforeChangeJson
            const {
              contactInfos,
              fileUploads,
              managementAttaches,
              cateJournalList,
              ...beforeCompanyInfo
            } = beforeChangeJson
            const {
              companyInfoChange,
              contactInfoChanges,
              siteInfoChanges,
              managementAttachChanges,
              fileuploadChanges,
              operatingLogs,
              npmCompanySizeChanges,
              npmCateJournalChanges,
              ...companyInfo
            } = datas
            const {
              totalAssets,
              currentAssets,
              fixedAssets,
              avgAnnualOutput,
              avgAnnualProfit,
              sunshineFileName,
              sunshineFileId
            } = companyInfoChange
            $form.query('form').take().value = companyInfo
            $form.query('state').get('data').userType = companyInfo.overseasRelation
            $form.query('state').get('data').companyChangeId = datas?.companyInfoChange?.companyChangeId
            setTimeout(() => {
              if ($attrs.params?.flag == 'view' || ['APPROVED', 'SUBMITTED', 'REJECTED', 'ABANDONED'].includes($attrs.params.row?.changeStatus)) {
                $form.readPretty = true
              }
              if (!isPerson) {
                $form.query('.companyTypeBefore').take().value = beforeCompanyInfo
                beforeCompanyInfo.ceeaBusinessModel = beforeCompanyInfo.ceeaBusinessModel ? beforeCompanyInfo.ceeaBusinessModel.split(',') : []
                $form.query('.companyBaseInfoBefore').take().value = beforeCompanyInfo
                $form.query('.attachFileBefore').take().value = fileUploads || []
                $form.query('.companyTypeAfter').take().value = companyInfoChange
                $form.query('.enterpriseThreeCertificatesAfter').take().value = companyInfoChange
                companyInfoChange.ceeaBusinessModel = companyInfoChange.ceeaBusinessModel ? companyInfoChange.ceeaBusinessModel.split(',') : []
                $form.query('.companyBaseInfoAfter').take().value = companyInfoChange
              } else {
                const {
                  businessStartDate,
                  businessEndDate
                } = beforeCompanyInfo
                beforeCompanyInfo.validityPeriodOfCard = [businessStartDate, businessEndDate]
                $form.query('.personBaseInfoBefore').take().value = beforeCompanyInfo
                companyInfoChange.validityPeriodOfCard = [companyInfoChange.businessStartDate, companyInfoChange.businessEndDate]
                $form.query('.personBaseInfoAfter').take().value = companyInfoChange
              }
              // $form.query('.authInfoBefore').take().value = { sunshineFileName: beforeCompanyInfo.sunshineFileName, sunshineFileId: beforeCompanyInfo.sunshineFileId }
              // $form.query('.authInfoAfter').take().value = { sunshineFileName, sunshineFileId }
              $form.query('.enterpriseThreeCertificatesBefore').take().value = beforeCompanyInfo
              $form.query('.contactDataBefore').take().value = contactInfos || []
              $form.query('.contactInfoChanges').take().value = contactInfoChanges
              $form.query('.qualificationInfoBefore').take().value = managementAttaches || []
              $form.query('.qualificationInfoAfter').take().value = managementAttachChanges
              $form.query('attachFileAfter').take(field => {
                field.componentProps.componentInstance.reLoadFileInfo()
              })
              $form.query('.attachFileBefore').take(field => {
                field.componentProps.componentInstance.reLoadFileInfo()
              })
              $form.query('.attachFileAfter').take().value = fileuploadChanges
              $form.query('.operatingLogsData').take().value = operatingLogs
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
              $form.query('.serviceRangeBefore').take().value = serviceRange || []
              $form.query('.serviceRangeAfter').take().value = npmCateJournalChanges.map(item => {
                const {
                  npmSerciceCustomChanges,
                  ...form
                } = item
                return { list: npmSerciceCustomChanges, tableForm: form}
              })
            })
            return ress
          }`)},vendorSubmit:{autoFormatResult:!1,cascadeDeletion:!0},saveTemporary:{autoFormatResult:!1,cascadeDeletion:!0},save:{cascadeDeletion:!0}}},properties:{SchemaWorkflow:{type:"void","x-component":"SchemaWorkflow","x-component-props":{"business-id":expression("$attrs.params.row?.planConfirmId || null"),"business-type":"SUPPLIERINFOCHANGE","@click-handler":expression(`(type) => {
            $submits(type, $form, $queryEngine, $message, $t, $bus)
          }`),"@submit-direct":expression(`(type) => {
            $submits(type, $form, $queryEngine, $message, $t, $bus)
          }`),"@confirm":expression(`(type, comment) => {
            $submits(type, $form, $queryEngine, $message, $t, $bus)
          }`),"@close-tab":expression(`() => {
            $back($bus)
          }`),"@update-integration-mode":expression(`(integrationMode) => {
            if (integrationMode?.integrationMode == "None") {
              updateButtonConfig($form)
            }
          }`)},properties:{layout:{type:"void","x-component":"FormContainer","x-component-props":{class:"vendorInfoChange"},properties:{collapse:{type:"void","x-component":"Collapse",properties:generateXindexInOrder({vendorInfo:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.vendorInfo")},"x-query-engine-skip":!0,properties:{form:{...formMain}}}})},layout:{type:"void","x-component":"FormContainer",properties:{tabs:{type:"void","x-component":"FormTab","x-component-props":{type:"card",class:"changeTab",activeKey:generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").userType!=="PERSONAL"?"tab1":"tab11")},properties:{...tabs}}}},collapse2:{type:"void","x-component":"Collapse",properties:generateXindexInOrder({operatingLogs:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("common.operationRecord")},"x-query-engine-skip":!0,properties:{operatingLogsData:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!1,maxHeight:400,pagination:!1,sortable:!1},"x-query-engine-skip":!0,properties:generateXindexInOrder({creationDate:{...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                                parseTime(row.creationDate, '{y}-{m}-{d}')
                              }`)},title:i18nExpression("common.operationTime"),"x-render-table-column":{minWidth:120}},operation:{type:"string",title:i18nExpression("contractMod.operationType"),"x-component":"DictSelect","x-component-props":{code:"OPERATING_TYPE"},"x-render-table-column":{minWidth:120}},reason:{type:"string",title:i18nExpression("common.explanationOfReasons"),"x-render-table-column":{minWidth:120}},createdFullName:{type:"string",title:i18nExpression("common.operator"),"x-render-table-column":{minWidth:120}}})}}}})}}}}}}}}),$back=$bus=>{emitTabRemove(attrs.tabName),$bus.$emit("ModelHead")},fatchCompanyData=async(companyId,$form)=>{if(companyId){const payload={filter:{companyId:{eq:companyId}}},query={"*":{},contactInfos:{"*":{}},fileUploads:{"*":{}},managementAttaches:{"*":{}},operatingLogs:{"*":{}},cateJournalList:{"*":{},npmSerciceCustoms:{"*":{}}}},transformParams=transformMQL.save("CompanyInfo",payload,"query",query),response=await vendorGreenApi.getCompanyInfo(transformParams);if(response&&response.data&&response.data.records.length){const{contactInfos,fileUploads,managementAttaches,cateJournalList,operatingLogs,...rest}=response.data.records[0],{companyId:companyId2,companyName,companyCode,totalAssets,currentAssets,fixedAssets,avgAnnualOutput,avgAnnualProfit,sunshineFileName,sunshineFileId}=rest;let stateData=$form.query("state").get("data");stateData.userType=rest.overseasRelation;const isPerson=rest.overseasRelation==="PERSONAL";if(stateData.beforeChangeJson=JSON.stringify(response.data.records[0]),$form.query("form").take().value={companyId:companyId2,companyName,companyCode},fileUploads){for(let i=0;i<fileUploads.length;i++){const fileItem=fileUploads[i];fileItem.originalBusinessId=fileItem.businessId,fileItem.sceneFileId=null,fileItem.businessId=null,fileItem.__edit_key__=!0,fileItem.__add_key__=!0,fileItem.__update_key__=!0}$form.query(".attachFileBefore").take().value=fileUploads,$form.query("attachFileBefore").take(field=>{field.componentProps.componentInstance.reLoadFileInfo()}),$form.query(".attachFileAfter").take().value=JSON.parse(JSON.stringify(fileUploads))}if(rest&&(isPerson||($form.query(".companyTypeAfter").take().value=JSON.parse(JSON.stringify(rest)),$form.query(".companyTypeBefore").take().value=rest,$form.query(".enterpriseThreeCertificatesAfter").take().value=JSON.parse(JSON.stringify(rest)),$form.query(".enterpriseThreeCertificatesBefore").take().value=rest),rest.ceeaBusinessModel=rest.ceeaBusinessModel?rest.ceeaBusinessModel.split(","):[],$form.query(".companyBaseInfoAfter").take().value=JSON.parse(JSON.stringify(rest)),$form.query(".companyBaseInfoBefore").take().value=rest),isPerson){const{businessStartDate,businessEndDate}=rest;rest.validityPeriodOfCard=[businessStartDate,businessEndDate],setTimeout(()=>{$form.query(".personBaseInfoBefore").take().value=rest,$form.query(".personBaseInfoAfter").take().value=JSON.parse(JSON.stringify(rest))})}$form.query(".qualificationInfoBefore").take().value=managementAttaches||[],$form.query(".qualificationInfoAfter").take().value=JSON.parse(JSON.stringify(managementAttaches))||[],$form.query(".contactDataBefore").take().value=contactInfos||[],$form.query(".contactInfoChanges").take().value=JSON.parse(JSON.stringify(contactInfos))||[];const serviceRange2=cateJournalList.map(item=>{const{npmSerciceCustoms,formId,...form}=item;return{list:npmSerciceCustoms,tableForm:{formId:null,...form}}});$form.query(".serviceRangeBefore").take().value=serviceRange2||[],$form.query(".serviceRangeAfter").take().value=JSON.parse(JSON.stringify(serviceRange2))||[]}}},$submits=async(type,$form,$queryEngine,$message,$t,$bus)=>{let values=$form.values;const isPerson=$form.query("state").get("data").userType==="PERSONAL",personFormAfter=isPerson&&$form.query(".personBaseInfoAfter").get("value")||{};if(personFormAfter?.validityPeriodOfCard?.length){const[businessStartDate,businessEndDate]=personFormAfter.validityPeriodOfCard;personFormAfter.businessStartDate=businessStartDate,personFormAfter.businessEndDate=businessEndDate}const companyTypeAfter=$form.query(".companyTypeAfter").get("value"),enterpriseThreeCertificatesAfter=$form.query(".enterpriseThreeCertificatesAfter").get("value"),serciceCustomDelList=$form.query("state").get("data").serciceCustomDelList||[];let serviceRange2=$form.query(".serviceRangeAfter").get("value").map(item=>{const{list,tableForm}=item;return{...tableForm,npmSerciceCustomChanges:[...list,...serciceCustomDelList]}});const managementAttachChanges=$form.query(".qualificationInfoAfter").get("value");let userType=values.form?.userType;(values.form?.userType==null||values.form?.userType==null)&&(userType=app.$store.getters.userType);let allData={beforeChangeJson:$form.query("state").get("data").beforeChangeJson,...values.form,userType,companyInfoChange:Object.assign({companyChangeId:$form.query("state").get("data")?.companyChangeId},companyTypeAfter,enterpriseThreeCertificatesAfter,personFormAfter),contactInfoChanges:$form.query(".contactInfoChanges").get("value"),fileuploadChanges:$form.query(".attachFileAfter").get("value"),npmCateJournalChanges:serviceRange2,managementAttachChanges};if(allData.changeId&&(allData.companyInfoChange.changeId=allData.changeId,allData.contactInfoChanges||[].forEach(e=>{e.changeId=allData.changeId}),allData.managementAttachChanges||[].forEach(e=>{e.changeId=allData.changeId}),allData.fileuploadChanges||[].forEach(e=>{e.businessId=allData.changeId}),allData.siteInfoChanges||[].forEach(e=>{e.changeId=allData.changeId})),!isPerson&&(allData.companyInfoChange.businessLicenseFileId==""||allData.companyInfoChange.businessLicenseFileId==null))return app.$message.warning($t("cusEntry.supplement20250314.businessLicenseInfoMsg")),!1;if(!isPerson&&(allData.companyInfoChange.registCurrency==""||allData.companyInfoChange.registCurrency==null))return app.$message.warning($t("vendorMod.enterCurrency")),!1;allData.companyInfoChange.ceeaBusinessModel=allData.companyInfoChange.ceeaBusinessModel.length?allData.companyInfoChange.ceeaBusinessModel.join():"";const changeStatus=attrs.params.row?.changeStatus||null;if(type=="SAVE")[null,"DRAFT"].includes(changeStatus)?(allData.changeStatus="DRAFT",$queryEngine.request.save(allData,{customizeAction:"saveTemporary",query:{"*":{}}}).then(()=>{$message.success($t("common.successSave")),$bus.$emit("vendorInfoChange"),emitTabRemove(attrs.tabName)}).catch(()=>{allData.companyInfoChange.ceeaBusinessModel=allData.companyInfoChange.ceeaBusinessModel?allData.companyInfoChange.ceeaBusinessModel.split(","):[]})):$queryEngine.request.save(allData,{customizeAction:"saveTemporary",query:{"*":{}}}).then(()=>{$message.success($t("common.successSave")),$bus.$emit("vendorInfoChange"),emitTabRemove(attrs.tabName)}).catch(()=>{allData.companyInfoChange.ceeaBusinessModel=allData.companyInfoChange.ceeaBusinessModel?allData.companyInfoChange.ceeaBusinessModel.split(","):[]});else{let validate=0;if(await $form.validate().then().catch(eq=>{app.$message.error(eq[0].messages[0]),validate=1}),validate)return!1;if($form.query("state").get("data").userType!=="OUT"){let ceeaDefaultList=allData.contactInfoChanges.filter(item=>item.ceeaDefaultContact==="Y");if(!ceeaDefaultList.length||ceeaDefaultList.length>1){app.$message.warning($t("cusEntry.vendorMod.defaultContactTips"));return}}const categoryIdList=new Set(serviceRange2.map(item=>item.categoryId));if(serviceRange2.length!==categoryIdList.size){let nameRecords=[];for(let id of categoryIdList){const record=serviceRange2.filter(item=>item.categoryId===id);record.length>1&&nameRecords.push(record[0].categoryName)}return $message.warning($t("cusEntry.tipMessage.serviceRangeCategoryRepeat",{name:nameRecords.join(";")})),!1}let valid=allData.managementAttachChanges.findIndex(item=>item.extIsMandatory==="Y"&&!item.fileuploadId);if(valid>-1){$message.warning($t("cusEntry.vendorMod.qualificationInformation")+$t("bidMod.warningMessage",{index:valid+1,message:$t("bidMod.pleaseUploadFile")}));return}let bol=!1,bolMsg="";if(allData.managementAttachChanges.some((item,index2)=>{if(item.extIfEndDateRequired==="Y"&&!item.endDate)return bol=!0,bolMsg=$t("cusEntry.vendorMod.qualificationInformation")+$t("bidMod.warningMessage",{index:index2+1,message:$t("cusEntry.vendorMod.endTime")+$t("contract_mod.required")}),!0}),bol){$message.warning(bolMsg);return}$queryEngine.request.save(allData,{customizeAction:"vendorSubmit",query:{"*":{}}}).then(async res=>{$message.success($t("common.successSave")),$bus.$emit("vendorInfoChange"),emitTabRemove(attrs.tabName);const{companyId,companyName,companyCode}=allData.companyInfoChange;await $monitorIpAddress({supplierId:companyId,supplierCode:companyCode,supplierName:companyName,source:$source.get("supplierChangeSubmit")})}).catch(()=>{allData.companyInfoChange.ceeaBusinessModel=allData.companyInfoChange.ceeaBusinessModel?allData.companyInfoChange.ceeaBusinessModel.split(","):[]})}},$monitorIpAddress=data=>http({url:"/api-sou/bids/ip/address/ipAddress/save",method:"POST",data}),$source=new Map([["supplierChangeSubmit",t("cusEntry.vendorMod.submitChange")],["supplierChangeUndo",t("cusEntry.vendorMod.withdrawChange")]]),$showSunFile=$self=>{setTimeout(()=>{const newData=$self.query(".sunshineFileName").get("value")?.split(",")||[],oldData=$self.query($self.parent.parent.parent.address.concat("beforeChange.authInfoBefore.sunshineFileName")).get("value")?.split(",")||[];let className="";(new Set([...newData,...oldData]).size!==oldData.length||newData.length!==oldData.length)&&(className="redColorFont");const fileList=[];if($self.value){const fileIdList=$self.value?.split(","),fileNameList=$self.query(".sunshineFileName").get("value")?.split(",");fileIdList.forEach((item,index2)=>{fileList.push({fileId:item,fileName:fileNameList?.[index2]})})}$self.setComponentProps({fileList,class:className})})},$showBeforeSunFile=$self=>{const fileList=[];if($self.value){const fileIdList=$self.value?.split(","),fileNameList=$self.query(".sunshineFileName").get("value")?.split(",");fileIdList.forEach((item,index2)=>{fileList.push({fileId:item,fileName:fileNameList?.[index2]})})}$self.setComponentProps({fileList})},scope={app,t,$attrs:attrs,emitTabRemove,initButtonConfig,$back,supCommonApi,$submits,observer,DictSelect,fatchCompanyData,redFunction,validEmail,validatePhone,sceneFileApi,$showSunFile,$showBeforeSunFile,$taxDictClass:createDictClass({country:[]})};return{__sfc:!0,app,emitTabRemove,t,vendor,http,attrs,workflowStatus,redFunction,viewUpdateButton,initButtonConfig,updateButtonConfig,schema,$back,fatchCompanyData,$submits,$monitorIpAddress,$source,$showSunFile,$showBeforeSunFile,scope,components:{FormTab,changeTitle,FileDynamic},RenderEngine}}});var _sfc_render$2=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{pageAttrs:_vm.$attrs,schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns$2=[],__component__$2=normalizeComponent(_sfc_main$2,_sfc_render$2,_sfc_staticRenderFns$2,!1,null,null,null,null);const detail=__component__$2.exports,_sfc_main$1=defineComponent({__name:"list",setup(__props){const{emitTabAdd,app,http}=usePageHelper(),schema=defineSchemas({InfoChangeVendor:{type:"void","x-decorator":"QueryEngine","x-query-engine":{service:"sup",actions:{vendorWithdraw:{autoFormatResult:!1}}},"x-component":"el-container","x-component-props":{class:"flex-container the_contractTemplateList_wrapper",direction:"vertical"},properties:{bus:{type:"void","x-component":"BusEvent","x-component-props":{eventName:"vendorInfoChange","@listener":expression(`() => {
            $queryEngine.state.paginationManagement.refresh()
          }`)}},query:{type:"object","x-query-engine-skip":!0,"x-component":"QueryFormByQueryEngine","x-component-props":{immediateQueryForm:!0},properties:generateXindexInOrder({changeApplyNo:{type:"string",title:i18nExpression("vendorMod.changeApplyNo"),"x-query-engine-query-operator":"contains"},creationDate:{title:i18nExpression("common.creationTime"),...dataTimeSelectorSegment,"x-query-engine-query-operator":"between"},changeStatus:{type:"string",title:i18nExpression("vendorMod.changeStatus"),"x-component":"DictSelect","x-component-props":{code:"INFO_CHANGE_STATUS"},"x-reactions":{effects:["onFieldInit"],fulfill:{state:{value:expression("app.$route?.params?.from === 'workCount' ? 'VENDOR_REJECTED' : ''")}}}},legalPerson:{type:"string",title:i18nExpression("cusEntry.vendorMod.legalPerson"),"x-query-engine-query-operator":"contains","x-query-engine-relation":"companyInfoChange","x-query-engine-relation-strict":!0},lcCode:{type:"string",title:i18nExpression("vendorMod.lcCode"),"x-query-engine-query-operator":"contains","x-query-engine-relation":"companyInfoChange","x-query-engine-relation-strict":!0}})},toolbar:{type:"void","x-component":"Space","x-component-props":{style:"margin-bottom: 16px"},properties:{add:{type:"void",title:i18nExpression("common.add"),"x-component":"RButton","x-component-props":{type:"primary","@click":expression(`() => {
               tab = {
                  component: detail,
                  params: {
                    flag: 'add',
                    tabName: 'detail'
                  },
                  title: $t('cusEntry.vendorMod.addInfoChange'), // '新增供应商',
                  name: 'detail'
                }
               emitTabAdd(tab)
              }`)}}}},table:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",class:"table-view-vxe-table",openCustomTable:!0},properties:generateXindexInOrder({changeId:{type:"string","x-hidden":!0},changeStatus:{type:"string",title:i18nExpression("vendorMod.changeStatus"),"x-component":"DictSelect","x-component-props":{code:"INFO_CHANGE_STATUS"},"x-render-table-column":{width:100}},changeApplyNo:{"x-component":"TableButton","x-component-props":{type:"text","@click":expression(`({row}) => {
                let changeId = row.changeId
                let tab = {
                  component: detail,
                  params: {
                    flag: 'view',
                    changeId,
                    tabName: 'detail' + row.companyName
                  },
                  title: row.companyName,
                  name: 'detail' + row.companyName
                }
                emitTabAdd(tab)
              }`)},"x-render-table-column":{title:i18nExpression("vendorMod.changeApplyNo"),minWidth:150,customRender:!0}},companyId:{type:"string","x-hidden":!0,"x-query-engine-relation":"companyInfoChange"},companyCode:{type:"string",title:i18nExpression("common.vendorCode"),"x-query-engine-relation":"companyInfoChange","x-render-table-column":{width:120}},companyName:{type:"string",title:i18nExpression("common.vendorName"),"x-query-engine-relation":"companyInfoChange","x-render-table-column":{width:150}},overseasRelation:{type:"string",title:i18nExpression("vendorMod.overseasRelation"),"x-query-engine-relation":"companyInfoChange","x-component":"DictSelect","x-component-props":{code:"RELATION"},"x-render-table-column":{width:150}},lcCode:{type:"string",title:i18nExpression("vendorMod.lcCode"),"x-query-engine-relation":"companyInfoChange","x-render-table-column":{width:160}},legalPerson:{type:"string",title:i18nExpression("cusEntry.vendorMod.legalPerson"),"x-query-engine-relation":"companyInfoChange","x-render-table-column":{width:120}},lastUpdateDate:{title:i18nExpression("vendorMod.changeApprovedDate"),...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.lastUpdateDate, '{y}-{m}-{d}')
              }`)},"x-query-engine-sort":"desc","x-render-table-column":{width:130}},createdFullName:{type:"string",title:i18nExpression("common.creator"),"x-render-table-column":{width:120}},creationDate:{title:i18nExpression("common.creationTime"),...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.creationDate, '{y}-{m}-{d}')
              }`)},"x-render-table-column":{width:130}},operation:{type:"void",title:i18nExpression("common.operation"),"x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList","x-component-props":{max:2},properties:{edit:{type:"void",title:i18nExpression("common.edit"),"x-reactions":changeFieldVisibleByDeps([".changeStatus"],"['VENDOR_WITHDRAW', 'VENDOR_REJECTED', 'DRAFT'].includes($deps[0])"),"x-component-props":{type:"text","@click":expression(`({ row }) => {
                    let changeId = row.changeId
                    let tab = {
                      component: detail,
                      params: {
                        flag: 'edit',
                        changeId,
                        tabName: 'detail' + row.companyName
                      },
                      title: row.companyName,
                      name: 'detail' + row.companyName
                    }
                    emitTabAdd(tab)
                  }`)}},delete:{type:"void",title:i18nExpression("common.delete"),"x-reactions":changeFieldVisibleByDeps([".changeStatus"],"['DRAFT'].includes($deps[0])"),"x-component-props":{popconfirm:{title:i18nExpression("common.confirmDeleteRow")},"@click":expression(`({ row }) => {
                     $queryEngine.request.delete(row.changeId).then(() => {
                       $message.success($t('common.successDelete'))
                       $queryEngine.state.paginationManagement.refresh()
                     })
                  }`)}},recall:{type:"void",title:i18nExpression("common.recall"),"x-reactions":changeFieldVisibleByDeps([".changeStatus"],"$vendor() && ['VENDOR_SUBMITTED'].includes($deps[0])"),"x-component-props":{type:"text","@click":expression(` ({ row }) => {
                      app.$prompt('', $t('bidMod.withdrawReason'), {
                        confirmButtonText: $t('common.confirm'),
                        cancelButtonText: $t('common.cancel'),
                        inputType: 'textarea',
                      }).then(({ value }) => {
                        let obj = {
                          changeId: row.changeId,
                          flowRemark: value
                        }
                        $queryEngine.request.save(obj, { customizeAction: 'vendorWithdraw' }).then(async (res) => {
                          app.$message({
                            message: $t('common.successWithdraw'),
                            type: 'success'
                          })
                          const {
                            companyId,
                            companyName,
                            companyCode
                          } = row
                          $queryEngine.state.paginationManagement.refresh() // 查询旧数据
                          await $monitorIpAddress({
                            supplierId: companyId,
                            supplierCode: companyCode,
                            supplierName: companyName,
                            source: $source.get('supplierChangeUndo')
                          })
                        })
                      })
                  }`)}}}}})}}}}),$monitorIpAddress=data=>http({url:"/api-sou/bids/ip/address/ipAddress/save",method:"POST",data}),$source=new Map([["supplierChangeSubmit",i18nExpression("cusEntry.vendorMod.submitChange")],["supplierChangeUndo",i18nExpression("cusEntry.vendorMod.withdrawChange")]]);return{__sfc:!0,emitTabAdd,app,http,schema,$monitorIpAddress,$source,scope:{emitTabAdd,app,i18nExpression,detail,http,$monitorIpAddress,$source},components:{},RenderEngine}}});var _sfc_render$1=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{staticClass:"contractPaymentType",attrs:{schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns$1=[],__component__$1=normalizeComponent(_sfc_main$1,_sfc_render$1,_sfc_staticRenderFns$1,!1,null,null,null,null);const vendorInfoChangeList=__component__$1.exports,_sfc_main={name:"VendorInfoChange",components:{NavTabs},data(){return{activeTab:"vendorInfoChangeList",tabs:[{title:()=>this.$t("vendorMod.vendorInfoChange"),name:"vendorInfoChangeList",component:vendorInfoChangeList,closable:!1}]}}};var _sfc_render=function(){var _vm=this,_c=_vm._self._c;return _c("NavTabs",{ref:"tabs",attrs:{"tabs-list":_vm.tabs,"cur-tab":_vm.activeTab}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const index=__component__.exports;export{index as default};
