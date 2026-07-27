import{ae as i18nExpression,ah as generateXindexInOrder,aC as generateCharExpressionByFunction,ad as expression,af as yearMonthDaySelectorSegment,ag as radioGroupByYOrNSegment,aD as requiredValidatorSegment}from"./index-6b6051d8.js";const userInfoForm={userInfoForm:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.vendorUserInfo")},"x-query-engine-skip":!0,properties:{userInfo:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!1,maxHeight:250,pagination:!1,sortable:!1},"x-query-engine-skip":!0,properties:generateXindexInOrder({username:{type:"string",title:i18nExpression("vendorMod.username"),"x-render-table-column":{minWidth:120}},nickname:{type:"string",title:i18nExpression("cusEntry.vendorMod.nickname"),"x-render-table-column":{minWidth:120}},phone:{type:"string",title:i18nExpression("vendorMod.contactMethod"),"x-render-table-column":{minWidth:120}},email:{type:"string",title:i18nExpression("common.email"),"x-render-table-column":{minWidth:120}},ceeaJobcodeDescr:{type:"string",title:i18nExpression("bidMod.position"),"x-render-table-column":{minWidth:120}},accountGroup:{type:"string",title:i18nExpression("cusEntry.vendorMod.accountGroup"),"x-render-table-column":{minWidth:120},"x-read-pretty":!0},mainType:{type:"string",title:i18nExpression("cusEntry.vendorMod.isMainAccount"),"x-render-table-column":{minWidth:120},"x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"}}})}}}},companyType={companyTypeAll:{type:"void","x-component":"CollapseItem","x-component-props":{title:`{{observer(
        {
          render(h) {
            return h($$components.Note, {
              props: {
                title: t('vendorMod.companyType'),
                value: $form.values.extRejectAttribute1,
                readonly: !($form.values.status === 'SUBMITTED' && $form.values.dataSources !== 'MANUALLY_CREATE' && $attrs.params.flag === 'edit')
              },
              on: {
                change: value => {
                  $form.values.extRejectAttribute1 = value
                }
              }
            })
          }
        }
      )}}`},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").userType!=="PERSONAL"),"x-query-engine-skip":!0,properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:{status:{type:"string","x-hidden":!0},domesticAndForeignRelations:{type:"string","x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{disabled:expression("$disabled"),code:"RELATION_NEW"},title:expression("$t('cusEntry.vendorMod.domesticAndForeignRelations')"),"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},companyType:{type:"string","x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"COMPANY_NATURE_NEW",disabled:expression("$disabled")},title:expression("$t('cusEntry.vendorMod.vendorType')"),"x-validator":{required:!0,message:i18nExpression("common.requiredField")}}}}}}},companyInfo={companyInfo:{type:"void","x-component":"CollapseItem","x-component-props":{title:`{{observer(
        {
          render(h) {
            return h($$components.Note, {
              props: {
                title: t('vendorMod.enterpriseThreeCertificates'),
                value: $form.values.extRejectAttribute2,
                readonly: !($form.values.status === 'SUBMITTED' && $form.values.dataSources !== 'MANUALLY_CREATE' && $attrs.params.flag === 'edit')
              },
              on: {
                change: value => {
                  $form.values.extRejectAttribute2 = value
                }
              }
            })
          }
        }
      )}}`},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").userType!=="PERSONAL"),"x-query-engine-skip":!0,properties:{div:{type:"void","x-component":"div","x-component-props":{class:"companyInfo"},properties:{businessLicense:{type:"string","x-hidden":!0},businessLicenseFileId:{type:"string","x-component":"SrmCommonFile","x-component-props":{readonly:expression("$disabled"),"list-type":"picture-card",style:{width:"33%","padding-right":"25px"},defaultFile:{fileId:expression("$self.value"),fileName:expression("$form.query('businessLicense').get('value')")},"dragger-options":{width:"100%",height:"345px"},limit:1,drag:"drag","@on-change":expression(`({ file }) => {

          }`)},title:""},layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical",style:{width:"67%","padding-left":"20px"}},"x-component":"FormGrid","x-component-props":{maxColumns:2,columnGap:32,rowGap:0},properties:{companyName:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.companyName')"),"x-component-props":{disabled:expression("$disabled")},"x-validator":{required:!0,message:i18nExpression("vendorMod.msgCompanyName")}},companyShortName:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.companyShortName')"),"x-component-props":{disabled:expression("$disabled")}},companyEnName:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('cusEntry.vendorMod.companyEnName')"),"x-component-props":{disabled:expression("$disabled")}},lcCode:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.lcCode')"),"x-component-props":{disabled:expression("$disabled")},"x-validator":{required:!0,message:i18nExpression("vendorMod.msgLcCode")}},enterpriseNo:{type:"string",title:i18nExpression("cusEntry.vendorMod.enterpriseNo"),"x-decorator":"FormItem","x-component-props":{disabled:expression("$disabled")},"x-validator":{required:!0,message:i18nExpression("vendorMod.msgLegalPerson")}},extKpp:{type:"string",title:i18nExpression("cusEntry.vendorMod.extKpp"),"x-decorator":"FormItem","x-component-props":{disabled:expression("$disabled")}},legalPerson:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.legalPerson')"),"x-component-props":{disabled:expression("$disabled")},"x-validator":{required:!0,message:i18nExpression("vendorMod.msgLegalPerson")}},registeredCapital:{type:"number","x-visible":expression("$form.query('.companyType').take().value != 'GETI'"),"x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.registeredCapital')"),"x-component-props":{disabled:expression("$disabled"),class:"input-with-select",style:"pointer-events:none"},"x-content":{append:expression(`observer(
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
                                disabled: true,
                              },
                              on: {
                                'change-value': (value) => {
                                  // targetField.value = value
                                }
                              }
                            }),
                          ])
                        }
                      }
                    )
                  `)},"x-validator":{required:expression("!['GETI','FEIYINGLI'].includes($form.query('.companyType').take().value)"),message:i18nExpression("vendorMod.msgRegisteredCapital")}},registCurrency:{type:"string","x-hidden":!0},ifLongPeriod:{type:"string",title:i18nExpression("cusEntry.vendorMod.ifLongTermSupplier"),"x-disabled":!0,"x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"},"x-decorator":"FormItem","x-validator":{required:!0,message:i18nExpression("cusEntry.tipMessage.ifLongPeriodMsg")}},businessEndDate:{type:"date","x-hidden":!0},businessStartDate:{...yearMonthDaySelectorSegment,"x-decorator":"FormItem","x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],disabled:expression("$disabled")},title:expression("$t('vendorMod.dateBusiness')"),"x-validator":{required:expression("$form.query('ifLongPeriod').take().value === 'N'"),message:i18nExpression("vendorMod.msgCreationDate")}},companyCreationDate:{...yearMonthDaySelectorSegment,"x-decorator":"FormItem","x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],disabled:expression("$disabled")},title:expression("$t('vendorMod.creationDate')"),"x-validator":{required:expression("!['FEIYINGLI'].includes($form.query('.companyType').take().value)"),message:i18nExpression("vendorMod.msgCreationDate")}},businessScope:{type:"string","x-decorator":"FormItem","x-decorator-props":{gridSpan:2},"x-component":"Input",title:expression("$t('vendorMod.businessScope')"),"x-component-props":{disabled:expression("$disabled"),type:"textarea"}}}}}}}}},companyBaseInfo={companyBaseInfo:{type:"void","x-component":"CollapseItem","x-component-props":{title:`{{observer(
        {
          render(h) {
            return h($$components.Note, {
              props: {
                title: t('vendorMod.companyBaseInfo2'),
                value: $form.values.extRejectAttribute3,
                readonly: !($form.values.status === 'SUBMITTED' && $form.values.dataSources !== 'MANUALLY_CREATE' && $attrs.params.flag === 'edit')
              },
              on: {
                change: value => {
                  $form.values.extRejectAttribute3 = value
                }
              }
            })
          }
        }
      )}}`},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").userType!=="PERSONAL"),"x-query-engine-skip":!0,properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:{ceeaBusinessModel:{type:"string","x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{disabled:expression("$disabled"),code:"BIZ_MODEL"},title:expression("$t('vendorMod.bizModel')")},ceeaIfListed:{title:i18nExpression("vendorMod.ifListed"),...radioGroupByYOrNSegment,"x-component-props":{disabled:expression("$disabled")}},ceeaListedTime:{...yearMonthDaySelectorSegment,"x-visible":"{{$form.query('.ceeaIfListed').take().value == 'Y'}}","x-decorator":"FormItem","x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],disabled:expression("$disabled")},title:expression("$t('vendorMod.listedDate')"),"x-validator":{required:!0,message:i18nExpression("common.marketTime")}},companyCountry:{type:"string",title:i18nExpression("components.address.country"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"country",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$disabled"),"@change":expression(`(val) => {
                let row = $form.values
                row.companyProvince = null
                row.companyCity = null
              }`)},"x-reactions":expression(`() => {
              const data = $taxDictClass.getDictDetail('country', $form.values.companyCountry)
              $form.query('.companyProvince').take()?.setComponentProps({ code: data ? data.description : undefined })
            }`)},companyProvince:{type:"string",title:i18nExpression("components.address.area"),"x-decorator":"FormItem","x-component":"DictSelect","x-visible":"{{['CN', 'RU'].includes($form.values.companyCountry)}}","x-component-props":{"custom-select-type":"PROVINCE",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$disabled"),"@change":expression(`(val) => {
                $form.values.companyCity = null
              }`)}},companyCity:{type:"string",title:i18nExpression("components.address.city"),"x-decorator":"FormItem","x-component":"DictSelect","x-visible":"{{['CN', 'RU'].includes($form.values.companyCountry)}}","x-component-props":{code:expression("$form.values.companyProvince || ''"),"custom-select-type":"CITY",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$disabled || !$form.values.companyProvince")}},companyAddress:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('components.address.detailAddress2')"),"x-component-props":{disabled:expression("$disabled")},"x-validator":{required:!0,message:i18nExpression("vendorMod.msgDetailAddr")}},ceeaHasParentCompany:{title:i18nExpression("cusEntry.vendorMod.ifParentCompany"),...radioGroupByYOrNSegment,"x-component-props":{disabled:expression("$disabled")}},ceeaParentCompanyName:{type:"string","x-decorator":"FormItem","x-component":"Input","x-visible":"{{$form.query('.ceeaHasParentCompany').take().value == 'Y'}}",title:i18nExpression("cusEntry.vendorMod.parentCompanyName"),"x-component-props":{disabled:expression("$disabled")}},groupCountry:{type:"string",title:i18nExpression("cusEntry.vendorMod.parentCompanyCountry"),"x-visible":"{{$form.query('.ceeaHasParentCompany').take().value == 'Y'}}","x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"country",disabled:expression("$disabled"),placeholder:expression("$t('common.pleaseSelect')")}},ceeaCompanyIntro:{type:"string","x-decorator":"FormItem","x-component":"Input.TextArea",title:expression("$t('vendorMod.companyProfile')"),"x-component-props":{disabled:expression("$disabled")},"x-decorator-props":{gridSpan:3}},categoryName:{type:"string","x-hidden":!0},cateJournalList:{type:"Array","x-hidden":!0}}}}}},contactInfoList={contactInfoList:{type:"void","x-component":"CollapseItem","x-component-props":{title:`{{observer(
        {
          render(h) {
            return h($$components.Note, {
              props: {
                title: t('vendorMod.contactInfo'),
                value: $form.values.extRejectAttribute4,
                readonly: !($form.values.status === 'SUBMITTED' && $form.values.dataSources !== 'MANUALLY_CREATE' && $attrs.params.flag === 'edit')
              },
              on: {
                change: value => {
                  $form.values.extRejectAttribute4 = value
                }
              }
            })
          }
        }
      )}}`},"x-query-engine-skip":!0,properties:{contactInfos:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,maxHeight:250,pagination:!1,sortable:!1},"x-query-engine-skip":!0,properties:generateXindexInOrder({contactName:{type:"string",title:i18nExpression("vendorMod.nickname"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$disabled")},"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},ceeaDeptName:{type:"string",title:i18nExpression("vendorMod.department"),"x-render-table-column":{minWidth:100},"x-component-props":{disabled:expression("$disabled")}},position:{type:"string",title:i18nExpression("dataConfMod.position"),"x-render-table-column":{minWidth:100},"x-component-props":{disabled:expression("$disabled")}},ceeaContactMethod:{type:"string",title:i18nExpression("vendorMod.contactMethod"),"x-render-table-column":{minWidth:100},"x-component-props":{disabled:expression("$disabled")}},email:{type:"string",title:i18nExpression("vendorMod.email"),"x-render-table-column":{minWidth:100},"x-component-props":{disabled:expression("$disabled")}},ceeaDefaultContact:{type:"string",title:i18nExpression("dataConfMod.isDefault"),"x-render-table-column":{minWidth:100},"x-component":"Checkbox","x-component-props":{"true-label":"Y","false-label":"N",disabled:expression("$disabled")}},ceeaComments:{type:"string",title:i18nExpression("dataConfMod.remark"),"x-render-table-column":{minWidth:100},"x-component-props":{disabled:expression("$disabled")}}})}}}},personBaseInfo={person:{type:"void","x-component":"CollapseItem","x-component-props":{title:`{{observer(
        {
          render(h) {
            return h($$components.Note, {
              props: {
                title: t('cusEntry.vendorMod.baseInfo'),
                value: $form.values.extRejectAttribute3,
                readonly: !($form.values.status === 'SUBMITTED' && $form.values.dataSources !== 'MANUALLY_CREATE' && $attrs.params.flag === 'edit')
              },
              on: {
                change: value => {
                  $form.values.extRejectAttribute3 = value
                }
              }
            })
          }
        }
      )}}`},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").userType==="PERSONAL"),"x-query-engine-skip":!0,properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:{personBaseInfo:{type:"object","x-query-engine-skip":!0,properties:{businessLicense:{type:"string","x-hidden":!0},businessLicenseFileId:{type:"string",title:i18nExpression("cusEntry.vendorMod.frontOfIdCard"),"x-decorator":"FormItem","x-component":"SrmCommonFile","x-component-props":{"extra-data":{uploadType:"DEF",sourceType:"WEB_APP",fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},"default-file":{fileId:expression("$form.query('personBaseInfo').get('value').businessLicenseFileId"),fileName:expression("$form.query('personBaseInfo').get('value').businessLicense")},readonly:!0},...requiredValidatorSegment},extIdCardOppositeFileName:{type:"string","x-hidden":!0},extIdCardOppositeFileId:{type:"string",title:i18nExpression("cusEntry.vendorMod.backOfIdCard"),"x-decorator":"FormItem","x-component":"SrmCommonFile","x-component-props":{"extra-data":{uploadType:"DEF",sourceType:"WEB_APP",fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},"default-file":{fileId:expression("$form.query('personBaseInfo').get('value').extIdCardOppositeFileId"),fileName:expression("$form.query('personBaseInfo').get('value').extIdCardOppositeFileName")},readonly:!0},...requiredValidatorSegment},companyName:{type:"string","x-decorator":"FormItem","x-component-props":{disabled:!0},title:i18nExpression("cusEntry.vendorMod.companyNameOrPersonName"),...requiredValidatorSegment},companyShortName:{type:"string","x-decorator":"FormItem","x-component-props":{disabled:!0},title:i18nExpression("cusEntry.vendorMod.personalAbbreviation"),...requiredValidatorSegment},idNumber:{type:"string","x-decorator":"FormItem","x-component-props":{disabled:!0},title:i18nExpression("cusEntry.vendorMod.idNo"),...requiredValidatorSegment},validityPeriodOfCard:{...yearMonthDaySelectorSegment,"x-decorator":"FormItem",title:i18nExpression("cusEntry.vendorMod.validityPeriodOfCard"),"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],type:"daterange",disabled:!0},...requiredValidatorSegment},businessScope:{type:"string",title:i18nExpression("cusEntry.vendorMod.mainBusinessScope"),"x-decorator":"FormItem","x-component-props":{disabled:!0}},lcCode:{type:"string",title:i18nExpression("vendorMod.lcCode"),"x-decorator":"FormItem","x-component-props":{disabled:!0},...requiredValidatorSegment},enterpriseNo:{type:"string",title:i18nExpression("cusEntry.vendorMod.enterpriseNo"),"x-decorator":"FormItem","x-component-props":{disabled:!0},...requiredValidatorSegment},extKpp:{type:"string",title:i18nExpression("cusEntry.vendorMod.extKpp"),"x-decorator":"FormItem","x-component-props":{disabled:!0}},companyCountry:{type:"string",title:i18nExpression("components.address.country"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"country",disabled:!0,placeholder:expression("$t('common.pleaseSelect')"),"@change":expression(`(val) => {
                    $form.query('personBaseInfo.companyProvince').take().value = ''
                    $form.query('personBaseInfo.companyCity').take().value = ''
                  }`)},"x-reactions":expression(`() => {
                  const data = $taxDictClass.getDictDetail('country', $self.value)
                  $form.query('personBaseInfo.companyProvince').take()?.setComponentProps({ code: data ? data.description : undefined })
                }`),...requiredValidatorSegment},companyProvince:{type:"string",title:i18nExpression("components.address.area"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{disabled:!0,"custom-select-type":"PROVINCE",placeholder:expression("$t('common.pleaseSelect')"),"@change":expression(`(val) => {
                    $form.query('personBaseInfo.companyCity').take().value = ''
                  }`)},"x-visible":"{{ ['CN', 'RU'].includes($form.query('personBaseInfo.companyCountry').take().value) }}",...requiredValidatorSegment},companyCity:{type:"string",title:i18nExpression("components.address.city"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:expression("$form.query('personBaseInfo.companyProvince').take()?.value || ''"),"custom-select-type":"CITY",placeholder:expression("$t('common.pleaseSelect')"),disabled:!0},"x-visible":"{{ ['CN', 'RU'].includes($form.query('personBaseInfo.companyCountry').take().value) }}",...requiredValidatorSegment},companyAddress:{type:"string",title:i18nExpression("cusEntry.vendorMod.detailAddress"),"x-decorator":"FormItem","x-component-props":{disabled:!0},...requiredValidatorSegment}}}}}}}},serviceRange={serviceRange:{type:"void","x-component":"CollapseItem","x-component-props":{title:`{{observer(
        {
          render(h) {
            return h($$components.Note, {
              props: {
                title: t('cusEntry.vendorMod.serviceRange'),
                value: $form.values.extRejectAttribute8,
                readonly: !($form.values.status === 'SUBMITTED' && $form.values.dataSources !== 'MANUALLY_CREATE' && $attrs.params.flag === 'edit')
              },
              on: {
                change: value => {
                  $form.values.extRejectAttribute8 = value
                }
              }
            })
          }
        }
      )}}`},"x-query-engine-skip":!0,properties:{toolbar:{type:"void","x-visible":!1,"x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void",title:i18nExpression("common.add"),"x-component-props":{type:"primary","@click":expression(`() => {
                $form.query('serviceRangeList').take(field => {
                  field.invoke('add', 'push')
                })
              }`)}}}},serviceRangeList:{type:"array","x-component":"ArrayItems",items:{type:"void",properties:{tableForm:{type:"object","x-hidden":"{{ JSON.stringify($self.value) === '{}' }}",properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:{categoryName:{type:"string","x-decorator":"FormItem","x-component":"QuickSearchWrapper","x-component-props":{showKey:"categoryName",name:"scc_base_purchase_category4","@close-quicksearch":expression(`val => {
                          let form = $form.query($self.parent.parent.address.toString()).take().value
                          form.categoryCode = val?.categoryCode || ''
                          form.categoryName = val?.categoryName || ''
                          form.categoryId = val?.categoryId || ''
                        }`),disabled:!0},title:"{{t('cusEntry.vendorMod.category', {index: $self.index + 1})}}"},formBtn:{type:"void","x-visible":!1,"x-component":"ButtonList","x-component-props":{style:{"margin-top":"5px"}},properties:{add:{type:"void","x-component-props":{type:"primary","@click":expression(`() => {
                              $form.query($self.parent.parent.parent.parent.address.concat($self.index).concat('list')).take(field => {
                                field.componentProps.componentInstance.addRow()
                              })
                            }`)},title:i18nExpression("cusEntry.common.addCustomer")},delete:{type:"void",title:i18nExpression("cusEntry.common.deleteCategory"),"x-component-props":{type:"primary","@click":expression(`() => {
                              $form.query('serviceRangeList').take(field => {
                                field.remove($self.index)
                              })
                            }`)}}}}}}}},list:{type:"array","x-component":"RenderTable","x-query-engine-skip":!0,"x-component-props":{preColumns:"seq",editMode:!1,maxHeight:250,pagination:!1,sortable:!1},"x-hidden":"{{ JSON.stringify($self.query('.tableForm').take().value) === '{}' }}",properties:generateXindexInOrder({performanceAmount:{type:"string",title:i18nExpression("cusEntry.vendorMod.performance"),"x-render-table-column":{minWidth:120}},mainCustom:{type:"string",title:i18nExpression("cusEntry.vendorMod.mainCustomer"),"x-render-table-column":{minWidth:120}},fileId:{type:"string",title:i18nExpression("cusEntry.vendorMod.achievement"),"x-render-table-column":{minWidth:120},"x-component":"SrmCommonFile","x-component-props":{extraData:{fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},defaultFile:{fileId:expression("$table.getRowByIndex($self.index)?.fileId"),fileName:expression("$table.getRowByIndex($self.index)?.fileName")},readonly:!1,"@on-change":expression(`({file}) => {
                      const { fileId = '', fileName = '' } = file || {}
                      let row = $table.getRowByIndex($self.index)
                      row.fileId = fileId
                      row.fileName = fileName
                    }`)}},operation:{type:"void","x-visible":!1,title:i18nExpression("common.operation"),"x-render-table-column":{width:60,fiexd:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:i18nExpression("common.delete"),"x-component-props":{type:"text","@click":expression(`() => {
                          $table.remove($self.index)
                        }`)}}}}})}}}},pagination:{type:"void","x-component":"ElPagination","x-component-props":{pageSize:10,layout:"total, prev, pager, next",total:expression("$form.query('CompanyInfo').get('data')?.totalServiceRangeList?.length"),"@current-change":expression(`(value) => {
            const totalServiceRangeList = $form.query('CompanyInfo').get('data')?.totalServiceRangeList || []
            const showList = totalServiceRangeList.slice((value - 1) * 10, value * 10)
            // 暂时解决渲染引擎底层存在的bug
            if (showList.length !== 10) {
              for (let i = showList.length; i < 10; i++) {
                showList.push({ tableForm: {}, list: [] })
              }
            }
            $form.query('serviceRangeList').take().value = showList
          }`)}}}}},vendorSiteInfoList={vendorSiteInfo:{type:"void","x-component":"CollapseItem","x-component-props":{title:`{{observer(
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
      )}}`},"x-query-engine-skip":!0,"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").userType!=="PERSONAL"),properties:{companyAddressInfos:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,maxHeight:400,pagination:!1,sortable:!1,primaryKey:"companyAddressId",cascadeDeletion:!0},"x-query-engine-skip":!0,properties:generateXindexInOrder({country:{type:"string",title:i18nExpression("components.address.country"),"x-render-table-column":{minWidth:120},"x-component":"DictSelect","x-component-props":{code:"country",placeholder:expression("$t('common.pleaseSelect')"),disabled:!0,"@change":expression(`(val) => {
                let row = $table.getRowByIndex($self.index)
                row.area = null
                row.city = null
              }`)},"x-reactions":expression(`() => {
              const data = $taxDictClass.getDictDetail('country', $self.value)
              $form.query('CompanyInfo.SchemaWorkflow.layout.collapse.vendorSiteInfo.companyAddressInfos.' + [$self.index] + '.area').take()?.setComponentProps({ code: data ? data.description : undefined })
            }`),"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},area:{type:"string",title:i18nExpression("components.address.area"),"x-render-table-column":{minWidth:120},"x-component":"DictSelect","x-component-props":{"custom-select-type":"PROVINCE",placeholder:expression("$t('common.pleaseSelect')"),disabled:!0}},city:{type:"string",title:i18nExpression("components.address.city"),"x-render-table-column":{minWidth:120},"x-component":"DictSelect","x-component-props":{code:expression("$table.getRowByIndex($self.index).area || ''"),"custom-select-type":"CITY",placeholder:expression("$t('common.pleaseSelect')"),disabled:!0}},address:{type:"string",title:i18nExpression("components.address.detailAddress"),"x-render-table-column":{minWidth:150},"x-component-props":{disabled:!0},"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},postalCode:{type:"string",title:i18nExpression("components.address.postalCode"),"x-render-table-column":{minWidth:150},"x-component-props":{disabled:!0}},remark:{type:"string",title:i18nExpression("components.address.remark"),"x-render-table-column":{minWidth:150},"x-component-props":{disabled:!0}},isActive:{type:"string",title:i18nExpression("common.enable"),"x-render-table-column":{minWidth:100},"x-component":"Checkbox","x-component-props":{"true-label":"Y","false-label":"N",disabled:!0}}})}}}},authInfo={authInfo:{type:"void","x-component":"CollapseItem","x-component-props":{title:`{{observer(
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
      )}}`},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").userType!=="PERSONAL"),properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:{certifiedContact:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('cusEntry.vendorMod.certifiedContact')"),"x-component-props":{disabled:!0,maxlength:100}},certifiedContactPhone:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('cusEntry.vendorMod.certifiedContactPhone')"),"x-component-props":{disabled:!0},"x-validator":{message:i18nExpression("dataConfMod.msgContactPhone"),validator:expression(`(value, rule) => {
                if(value && !validatePhone(value)){
                  return $t('vendorMod.correctPhoneNumber')
                }
              }`)}}}}}}},relationSuppliers={relationSuppliers:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("cusEntry.vendorMod.relationSuppliers")},"x-query-engine-skip":!0,"x-visible":generateCharExpressionByFunction(({$attrs})=>["passRegister","view"].includes($attrs.params.flag)),properties:{relationSuppliersList:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,maxHeight:250,pagination:!1,sortable:!1},"x-query-engine-skip":!0,properties:generateXindexInOrder({vendorCodeA:{type:"string","x-render-table-column":{title:i18nExpression("cusEntry.vendorMod.aCompanyCode"),minWidth:120,customRender:!0},"x-component":"TableButton","x-component-props":{type:"text","@click":expression(`({row}) => {
                emitTabAdd({
                  component: relationSuppliersDetail,
                  name: 'relationSuppliersDetail' + row.vendorCodeA,
                  params: {
                    flag: 'view',
                    row,
                    tabName: 'relationSuppliersDetail' + row.vendorCodeA
                  },
                  title: row.vendorCodeA
                })
              }`)}},socialCreditCodeA:{type:"string","x-render-table-column":{title:i18nExpression("cusEntry.vendorMod.socialCreditCodeA"),minWidth:150},"x-read-pretty":!0},vendorNameA:{type:"string","x-render-table-column":{title:i18nExpression("cusEntry.vendorMod.aCompanyName"),minWidth:120},"x-read-pretty":!0},vendorCodeB:{type:"string","x-render-table-column":{title:i18nExpression("cusEntry.vendorMod.bCompanyCode"),minWidth:120,customRender:!0},"x-component":"TableButton","x-component-props":{type:"text","@click":expression(`({row}) => {
                emitTabAdd({
                  component: relationSuppliersDetail,
                  name: 'relationSuppliersDetail' + row.vendorCodeB,
                  params: {
                    flag: 'view',
                    row,
                    tabName: 'relationSuppliersDetail' + row.vendorCodeB
                  },
                  title: row.vendorCodeB
                })
              }`)}},socialCreditCodeB:{type:"string","x-render-table-column":{title:i18nExpression("cusEntry.vendorMod.socialCreditCodeB"),minWidth:150},"x-read-pretty":!0},vendorNameB:{type:"string","x-render-table-column":{title:i18nExpression("cusEntry.vendorMod.bCompanyName"),minWidth:120},"x-read-pretty":!0},associationRemark:{type:"string","x-render-table-column":{title:i18nExpression("cusEntry.vendorMod.relationRemark"),minWidth:120},"x-read-pretty":!0},createdUserName:{type:"string","x-render-table-column":{title:i18nExpression("common.creator"),minWidth:150},"x-read-pretty":!0},creationDate:{...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.creationDate, '{y}-{m}-{d}')
              }`)},"x-render-table-column":{title:i18nExpression("common.creationTime"),minWidth:150},"x-read-pretty":!0},lastUpdateDate:{type:"string","x-hidden":!0,"x-query-engine-sort":"desc"}})}}}},qualificationInformation={qualificationInformation:{type:"void","x-component":"CollapseItem","x-component-props":{title:`{{observer(
        {
          render(h) {
            return h($$components.Note, {
              props: {
                title: t('cusEntry.vendorMod.qualificationInformation'),
                value: $form.values.extRejectAttribute9,
                readonly: !($form.values.status === 'SUBMITTED' && $form.values.dataSources !== 'MANUALLY_CREATE' && $attrs.params.flag === 'edit')
              },
              on: {
                change: value => {
                  $form.values.extRejectAttribute9 = value
                }
              }
            })
          }
        }
      )}}`},"x-visible":generateCharExpressionByFunction(({$form,$attrs})=>["startFileApproval","approval","view"].includes($attrs.params.flag)),"x-query-engine-skip":!0,properties:{managementAttaches:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!1,height:350,pagination:!1,sortable:!1},"x-query-engine-skip":!0,properties:generateXindexInOrder({authNum:{type:"string","x-component":"DictSelect","x-component-props":{code:expression("'CERTIFICATE_TYPE_' + $form.query('state').get('data').userType")},title:i18nExpression("cusEntry.vendorMod.certificateType"),"x-render-table-column":{minWidth:200}},startDate:{...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                  parseTime(row.startDate, '{y}-{m}-{d}')
              }`)},title:i18nExpression("cusEntry.vendorMod.startTime"),"x-render-table-column":{minWidth:120}},endDate:{...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                  parseTime(row.endDate, '{y}-{m}-{d}')
              }`)},title:i18nExpression("cusEntry.vendorMod.endTime"),"x-render-table-column":{minWidth:120}},extCertificatePeriod:{type:"string",title:i18nExpression("cusEntry.vendorMod.extCertificatePeriod"),"x-render-table-column":{minWidth:120}},extIsMandatory:{type:"string","x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"},title:i18nExpression("dataConfMod.isRequested"),"x-render-table-column":{minWidth:120}},fileuploadId:{type:"number","x-component":"SrmCommonFile","x-component-props":{extraData:{fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},defaultFile:{fileId:expression("$table.getRowByIndex($self.index)?.fileuploadId"),fileName:expression("$table.getRowByIndex($self.index)?.authType")},readonly:!1,"@on-change":expression(`({file}) => {
                const { fileId = null, fileName = '' } = file || {}
                let row = $table.getRowByIndex($self.index)
                row.fileuploadId = fileId
                row.authType = fileName
              }`)},title:i18nExpression("cusEntry.vendorMod.file"),"x-render-table-column":{minWidth:120}},authDescription:{type:"string",title:i18nExpression("cusEntry.vendorMod.remark"),"x-render-table-column":{minWidth:120}}})}}}};export{companyInfo as a,companyBaseInfo as b,companyType as c,contactInfoList as d,authInfo as e,personBaseInfo as p,qualificationInformation as q,relationSuppliers as r,serviceRange as s,userInfoForm as u,vendorSiteInfoList as v};
