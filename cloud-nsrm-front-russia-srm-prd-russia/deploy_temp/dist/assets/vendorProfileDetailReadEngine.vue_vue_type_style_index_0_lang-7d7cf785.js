import{af as i18nExpression,ai as generateXindexInOrder,aB as generateCharExpressionByFunction,ae as expression,ag as yearMonthDaySelectorSegment,ah as radioGroupByYOrNSegment,ak as feedbackLayoutIsPopover,aC as requiredValidatorSegment}from"./index-17d0ccd5.js";const userInfoForm={userInfoForm:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("vendorMod.vendorUserInfo")},"x-query-engine-skip":!0,properties:{userInfo:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!1,maxHeight:250,pagination:!1,sortable:!1},"x-query-engine-skip":!0,properties:generateXindexInOrder({username:{type:"string",title:i18nExpression("vendorMod.username"),"x-render-table-column":{minWidth:120}},nickname:{type:"string",title:i18nExpression("cusEntry.vendorMod.nickname"),"x-render-table-column":{minWidth:120}},phone:{type:"string",title:i18nExpression("vendorMod.contactMethod"),"x-render-table-column":{minWidth:120}},email:{type:"string",title:i18nExpression("common.email"),"x-render-table-column":{minWidth:120}},ceeaJobcodeDescr:{type:"string",title:i18nExpression("dataConfMod.position"),"x-render-table-column":{minWidth:120}},role:{type:"string",title:i18nExpression("cusEntry.vendorMod.role"),"x-render-table-column":{minWidth:120}},mainType:{type:"string",title:i18nExpression("cusEntry.vendorMod.isMainAccount"),"x-render-table-column":{minWidth:120},"x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"}}})}}}},companyType={companyTypeAll:{type:"void","x-component":"CollapseItem","x-component-props":{title:`{{observer(
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
      )}}`},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").userType!=="PERSONAL"),"x-query-engine-skip":!0,properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:{status:{type:"string","x-hidden":!0},overseasRelation:{type:"string","x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{disabled:expression("$disabled"),code:"RELATION_NEW"},title:expression("$t('vendorMod.overseasRelation')"),"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},companyType:{type:"string","x-decorator":"FormItem","x-component-props":{disabled:expression("$disabled")},"x-reactions":{dependencies:["overseasRelation"],fulfill:{state:{visible:expression('$deps[0] == "INSIDE"')}}},title:expression("$t('cusEntry.vendorMod.vendorType')"),"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},extUseType:{type:"string",title:i18nExpression("cusEntry.vendorMod.extUseType"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"SUPPLIER_USE",disabled:expression("$disabled")}}}}}}},companyInfo={companyInfo:{type:"void","x-component":"CollapseItem","x-component-props":{title:`{{observer(
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

          }`)},title:""},layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical",style:{width:"67%","padding-left":"20px"}},"x-component":"FormGrid","x-component-props":{maxColumns:2,columnGap:32,rowGap:0},properties:{companyName:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.companyName')"),"x-component-props":{disabled:expression("$disabled")},"x-validator":{required:!0,message:i18nExpression("vendorMod.msgCompanyName")}},legalPerson:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.legalPerson')"),"x-component-props":{disabled:expression("$disabled")},"x-validator":{required:!0,message:i18nExpression("vendorMod.msgLegalPerson")}},lcCode:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.lcCode')"),"x-component-props":{disabled:expression("$disabled")},"x-validator":{required:!0,message:i18nExpression("vendorMod.msgLcCode")}},registCurrency:{type:"string","x-hidden":!0},registeredCapital:{type:"number","x-visible":expression("$form.query('.companyType').take().value != 'GETI'"),"x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.registeredCapital')"),"x-component-props":{disabled:expression("$disabled"),class:"input-with-select",style:"pointer-events:none"},"x-content":{append:expression(`observer(
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
                  `)},"x-validator":{required:expression("!['GETI','FEIYINGLI'].includes($form.query('.companyType').take().value)"),message:i18nExpression("vendorMod.msgRegisteredCapital")}},companyCreationDate:{type:"date",default:null,"x-decorator":"FormItem","x-component-props":{placeholder:i18nExpression("common.pleaseSelectDate"),format:"yyyy-MM-dd","value-format":"yyyy-MM-dd",disabled:expression("$disabled")},title:expression("$t('vendorMod.creationDate')"),"x-validator":{required:expression("!['FEIYINGLI'].includes($form.query('.companyType').take().value)"),message:i18nExpression("vendorMod.msgCreationDate")}},ifLongPeriod:{type:"string",title:i18nExpression("cusEntry.vendorMod.ifLongTermSupplier"),"x-disabled":!0,"x-component":"DictSelect","x-component-props":{code:"YES_OR_NO"},"x-decorator":"FormItem","x-validator":{required:!0,message:i18nExpression("cusEntry.tipMessage.ifLongPeriodMsg")}},businessStartDate:{type:"date","x-hidden":!0},businessEndDate:{type:"date","x-hidden":!0},businessDate:{type:"string","x-component":"DatePicker","x-decorator":"FormItem","x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],type:"daterange",disabled:expression("$disabled")},title:expression("$t('vendorMod.dateBusiness')"),"x-validator":{required:expression("!['FEIYINGLI'].includes($form.query('.companyType').take().value)"),message:i18nExpression("vendorMod.msgCreationDate")}},companyShortName:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('vendorMod.companyShortName')"),"x-component-props":{disabled:expression("$disabled")}},businessScope:{type:"string","x-decorator":"FormItem","x-decorator-props":{gridSpan:2},"x-component":"Input",title:expression("$t('vendorMod.businessScope')"),"x-component-props":{disabled:expression("$disabled"),type:"textarea"}}}}}}}}},companyBaseInfo={companyBaseInfo:{type:"void","x-component":"CollapseItem","x-component-props":{title:`{{observer(
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
      )}}`},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").userType!=="PERSONAL"),"x-query-engine-skip":!0,properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:{ceeaBusinessModel:{type:"string","x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{disabled:expression("$disabled"),code:"BIZ_MODEL"},title:expression("$t('vendorMod.bizModel')")},ceeaIfListed:{title:i18nExpression("vendorMod.ifListed"),...radioGroupByYOrNSegment,"x-component-props":{disabled:expression("$disabled")}},ceeaListedTime:{type:"date",default:null,"x-visible":"{{$form.query('.ceeaIfListed').take().value == 'Y'}}","x-decorator":"FormItem","x-component-props":{placeholder:i18nExpression("common.pleaseSelectDate"),format:"yyyy-MM-dd","value-format":"yyyy-MM-dd",disabled:expression("$disabled")},title:expression("$t('vendorMod.listedDate')"),"x-validator":{required:!0,message:i18nExpression("请选择上市时间")}},companyCountry:{type:"string",title:i18nExpression("components.address.country"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"country",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$disabled"),"@change":expression(`(val) => {
                let row = $form.values
                // 选择国外就清理省市区，并且禁用
                if (row.companyCountry !== 'CN') {
                  row.companyProvince = null
                  row.companyCity = null
                }
              }`)}},companyProvince:{type:"string",title:i18nExpression("components.address.area"),"x-decorator":"FormItem","x-component":"DictSelect","x-visible":"{{$form.query('.companyCountry').take().value == 'CN'}}","x-component-props":{code:"PROVINCE","custom-select-type":"PROVINCE",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$disabled")}},companyCity:{type:"string",title:i18nExpression("components.address.city"),"x-decorator":"FormItem","x-component":"DictSelect","x-visible":"{{$form.query('.companyCountry').take().value == 'CN'}}","x-component-props":{code:expression("$form.values.companyProvince"),"custom-select-type":"CITY",placeholder:expression("$t('common.pleaseSelect')"),disabled:expression("$disabled")}},companyAddress:{type:"string","x-decorator":"FormItem","x-component":"Input",title:expression("$t('components.address.detailAddress2')"),"x-component-props":{disabled:expression("$disabled")},"x-validator":{required:!0,message:i18nExpression("vendorMod.msgDetailAddr")}},ceeaHasParentCompany:{title:i18nExpression("cusEntry.vendorMod.ifParentCompany"),...radioGroupByYOrNSegment,"x-component-props":{disabled:expression("$disabled")}},ceeaParentCompanyName:{type:"string","x-decorator":"FormItem","x-component":"Input","x-visible":"{{$form.query('.ceeaHasParentCompany').take().value == 'Y'}}",title:i18nExpression("cusEntry.vendorMod.parentCompanyName"),"x-component-props":{disabled:expression("$disabled")}},groupCountry:{type:"string",title:i18nExpression("cusEntry.vendorMod.parentCompanyCountry"),"x-visible":"{{$form.query('.ceeaHasParentCompany').take().value == 'Y'}}","x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"country",disabled:expression("$disabled"),placeholder:expression("$t('common.pleaseSelect')")}},ceeaAgentBrand:{type:"string","x-decorator":"FormItem","x-component":"Input","x-visible":generateCharExpressionByFunction(`({ $form }) => {
              return $attrs.params.flag === 'view'
            }`),title:expression("$t('cusEntry.vendorMod.agencyBrand')"),"x-component-props":{disabled:expression("$disabled")},"x-validator":{required:!1,message:i18nExpression("vendorMod.msgAgencyBrand")}},pjQualifications:{type:"string",title:i18nExpression("cusEntry.vendorMod.qualifications"),"x-decorator":"FormItem","x-component":"Input","x-visible":generateCharExpressionByFunction(`({ $form }) => {
              return $attrs.params.flag === 'view'
            }`),"x-component-props":{disabled:!0}},ceeaCompanyIntro:{type:"string","x-decorator":"FormItem","x-component":"Input.TextArea",title:expression("$t('vendorMod.companyProfile')"),"x-component-props":{disabled:expression("$disabled")},"x-decorator-props":{gridSpan:3}},categoryName:{type:"string","x-hidden":!0},cateJournalList:{type:"Array","x-hidden":!0}}}}}},contactInfoList={contactInfoList:{type:"void","x-component":"CollapseItem","x-component-props":{title:`{{observer(
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
      )}}`},"x-query-engine-skip":!0,properties:{contactInfos:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,maxHeight:250,pagination:!1,sortable:!1},"x-query-engine-skip":!0,properties:generateXindexInOrder({contactName:{type:"string",title:i18nExpression("vendorMod.nickname"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$disabled")},"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},ceeaGender:{type:"string",title:i18nExpression("vendorMod.sex"),"x-render-table-column":{minWidth:100},"x-component":"DictSelect","x-component-props":{code:"GENDER",disabled:expression("$disabled")}},ceeaDeptName:{type:"string",title:i18nExpression("vendorMod.department"),"x-render-table-column":{minWidth:100},"x-component-props":{disabled:expression("$disabled")}},position:{type:"string",title:i18nExpression("dataConfMod.position"),"x-render-table-column":{minWidth:100},"x-component-props":{disabled:expression("$disabled"),code:"POSITION"},"x-component":"DictSelect"},ceeaContactMethod:{type:"string",title:i18nExpression("vendorMod.contactMethod"),"x-render-table-column":{minWidth:100},"x-component-props":{disabled:expression("$disabled")}},email:{type:"string",title:i18nExpression("vendorMod.email"),"x-render-table-column":{minWidth:100},"x-component-props":{disabled:expression("$disabled")}},ceeaDefaultContact:{type:"string",title:i18nExpression("dataConfMod.isDefault"),"x-render-table-column":{minWidth:100},"x-component":"Checkbox","x-component-props":{"true-label":"Y","false-label":"N",disabled:expression("$disabled")}},socialSecurityCertificateFileId:{type:"string","x-render-table-column":{minWidth:100},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").userType!=="PERSONAL"),title:i18nExpression("cusEntry.vendorMod.socialSecurityCertificate"),"x-component":"SrmCommonFile","x-component-props":{"extra-data":{uploadType:"DEF",sourceType:"WEB_APP",fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},"default-file":{fileId:expression("$table.getRowByIndex($self.index)?.socialSecurityCertificateFileId"),fileName:expression("$table.getRowByIndex($self.index)?.socialSecurityCertificateFileName")},readonly:!0},...feedbackLayoutIsPopover,"x-validator":{required:generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").userType!=="OUT"),message:i18nExpression("common.requiredField")}},ceeaComments:{type:"string",title:i18nExpression("dataConfMod.remark"),"x-render-table-column":{minWidth:100},"x-component-props":{disabled:expression("$disabled")}}})}}}},bankInfoList={bankInfoList:{type:"void","x-component":"CollapseItem","x-component-props":{title:`{{observer(
        {
          render(h) {
            return h($$components.Note, {
              props: {
                title: t('vendorMod.bankInfo'),
                value: $form.values.extRejectAttribute5,
                readonly: !($form.values.status === 'SUBMITTED' && $form.values.dataSources !== 'MANUALLY_CREATE' && $attrs.params.flag === 'edit')
              },
              on: {
                change: value => {
                  $form.values.extRejectAttribute5 = value
                }
              }
            })
          }
        }
      )}}`},"x-query-engine-skip":!0,properties:{bankInfos:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,maxHeight:400,pagination:!1,sortable:!1},"x-query-engine-skip":!0,properties:generateXindexInOrder({branchBankId:{type:"number","x-hidden":!0},bankCode:{type:"string",title:i18nExpression("components.bank.bankCode"),"x-render-table-column":{minWidth:120},"x-component":"QuickSearchWrapper","x-component-props":{showKey:"bankNum",propKey:"bankNum",name:"ceea_base_erp_branch_bank_info",disabled:expression("$disabled")},"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},bankName:{type:"string",title:i18nExpression("components.bank.bankName"),"x-render-table-column":{minWidth:120},"x-read-pretty":!0,"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},unionCode:{type:"string",title:i18nExpression("components.bank.unionCode"),"x-render-table-column":{minWidth:120},"x-read-pretty":!0,"x-validator":{required:generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").overseasRelation!="OUT"),message:i18nExpression("common.requiredField")}},openingBank:{type:"string",title:i18nExpression("components.bank.branchBankName"),"x-render-table-column":{minWidth:120},"x-read-pretty":!0,"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},bankAccountName:{type:"string",title:i18nExpression("components.bank.accountName"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$disabled")},"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},bankAccount:{type:"string",title:i18nExpression("components.bank.bankAccount"),"x-render-table-column":{minWidth:120},"x-component-props":{disabled:expression("$disabled")},"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},currencyCode:{type:"string",title:i18nExpression("vendorMod.currencyCode"),"x-render-table-column":{minWidth:120},"x-component":"DictSelect","x-component-props":{code:"currency",disabled:expression("$disabled")},"x-validator":{required:!0,message:i18nExpression("common.requiredField")}},ceeaMainAccount:{type:"string",title:i18nExpression("components.bank.isMain"),"x-render-table-column":{minWidth:100},"x-component":"Checkbox","x-component-props":{"true-label":"Y","false-label":"N",disabled:expression("$disabled")}},ceeaEnabled:{type:"string",title:i18nExpression("components.bank.isActive"),"x-render-table-column":{minWidth:100},"x-component":"Checkbox","x-component-props":{"true-label":"Y","false-label":"N",disabled:expression("$disabled")}}})}}}},financeInfoList={financeInfo:{type:"void","x-component":"CollapseItem","x-component-props":{title:`{{observer(
        {
          render(h) {
            return h($$components.Note, {
              props: {
                title: t('cusEntry.vendorMod.financeReport'),
                value: $form.values.extRejectAttribute6,
                readonly: !($form.values.status === 'SUBMITTED' && $form.values.dataSources !== 'MANUALLY_CREATE' && $attrs.params.flag === 'edit')
              },
              on: {
                change: value => {
                  $form.values.extRejectAttribute6 = value
                }
              }
            })
          }
        }
      )}}`},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").userType!=="PERSONAL"),"x-query-engine-skip":!0,properties:{financeInfoForm:{type:"void","x-query-engine-skip":!0,properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:{totalAssets:{type:"string",title:i18nExpression("cusEntry.vendorMod.totalAssets"),"x-decorator":"FormItem","x-component-props":{disabled:!0}},currentAssets:{type:"string",title:i18nExpression("cusEntry.vendorMod.workingCapital"),"x-decorator":"FormItem","x-component-props":{disabled:!0}},fixedAssets:{type:"string",title:i18nExpression("cusEntry.vendorMod.fixedAssets"),"x-decorator":"FormItem","x-component-props":{disabled:!0}},avgAnnualOutput:{type:"string",title:i18nExpression("cusEntry.vendorMod.threeYearsOutput"),"x-decorator":"FormItem","x-component-props":{disabled:!0}},avgAnnualProfit:{type:"string",title:i18nExpression("cusEntry.vendorMod.threeYearsNetProfits"),"x-decorator":"FormItem","x-component-props":{disabled:!0}}}}}},financeInfoAfterTag:{type:"void","x-component":"p","x-content":i18nExpression("cusEntry.vendorMod.threeYearsReportFile")},npmFinanceReports:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!1,maxHeight:400,pagination:!1,sortable:!1,primaryKey:"financeInfoId",cascadeDeletion:!0},"x-query-engine-skip":!0,properties:generateXindexInOrder({year:{type:"string",title:i18nExpression("cusEntry.vendorMod.year"),"x-render-table-column":{minWidth:120,cellFormatter:"{{(_, {cellValue})=> new Date(cellValue).getFullYear()}}"}},remark:{type:"string",title:i18nExpression("cusEntry.vendorMod.remark"),"x-render-table-column":{minWidth:120}},fileId:{type:"string",title:i18nExpression("cusEntry.vendorMod.file"),"x-render-table-column":{minWidth:120},"x-component":"SrmCommonFile","x-component-props":{"extra-data":{uploadType:"DEF",sourceType:"WEB_APP",fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},"default-file":{fileId:expression("$table.getRowByIndex($self.index)?.fileId"),fileName:expression("$table.getRowByIndex($self.index)?.fileName")},"@on-change":expression(`({file}) => {
                const { fileId, fileName } = file || {}
                $table.getRowByIndex($self.index).fileId = fileId
                $table.getRowByIndex($self.index).fileName = fileName
              }`)}},operation:{"x-visible":!1,type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{disabled:expression("$form.readPretty"),type:"text","@click":expression(`({ row }) => {
                      $table.remove($self.index)
                  }`)}}}}})},financeInfoAfterRemark:{type:"void","x-component":"p","x-content":i18nExpression("cusEntry.vendorMod.financeInfoRemark")}}}},companySizesList={companySizesList:{type:"void","x-component":"CollapseItem","x-component-props":{title:`{{observer(
        {
          render(h) {
            return h($$components.Note, {
              props: {
                title: t('vendorMod.companySize'),
                value: $form.values.extRejectAttribute7,
                readonly: !($form.values.status === 'SUBMITTED' && $form.values.dataSources !== 'MANUALLY_CREATE' && $attrs.params.flag === 'edit')
              },
              on: {
                change: value => {
                  $form.values.extRejectAttribute7 = value
                }
              }
            })
          }
        }
      )}}`},"x-query-engine-skip":!0,"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").userType!=="PERSONAL"),properties:{companySizes:{type:"array","x-component":"RenderTable",default:[{type:"人数"}],"x-component-props":{preColumns:"seq",editMode:!1,maxHeight:250,pagination:!1,sortable:!1,primaryKey:"id",cascadeDeletion:!0},"x-query-engine-skip":!0,properties:generateXindexInOrder({totalNumber:{type:"string",title:i18nExpression("cusEntry.vendorMod.totalNum"),"x-render-table-column":{minWidth:120},"x-component-props":{"v-input-format":{type:"integer",negative:!1}}},socialSecurityNumber:{type:"string",title:i18nExpression("cusEntry.vendorMod.socialSecurity"),"x-render-table-column":{minWidth:120},"x-component-props":{"v-input-format":{type:"integer",negative:!1}}},managementNumber:{type:"string",title:i18nExpression("cusEntry.vendorMod.managerNum"),"x-render-table-column":{minWidth:120},"x-component-props":{"v-input-format":{type:"integer",negative:!1}}},developerNumber:{type:"string",title:i18nExpression("cusEntry.vendorMod.developmentNum"),"x-render-table-column":{minWidth:120},"x-component-props":{"v-input-format":{type:"integer",negative:!1}}},productionNumber:{type:"string",title:i18nExpression("cusEntry.vendorMod.productNum"),"x-render-table-column":{minWidth:120},"x-component-props":{"v-input-format":{type:"integer",negative:!1}}},overUndergraduateNumber:{type:"string",title:i18nExpression("cusEntry.vendorMod.bachelorDegreeOrAbove"),"x-render-table-column":{minWidth:120},"x-component-props":{"v-input-format":{type:"integer",negative:!1}}}})}}}},personBaseInfo={person:{type:"void","x-component":"CollapseItem","x-component-props":{title:`{{observer(
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
      )}}`},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").userType==="PERSONAL"),"x-query-engine-skip":!0,properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:{personBaseInfo:{type:"object","x-query-engine-skip":!0,properties:{companyName:{type:"string","x-decorator":"FormItem","x-component-props":{disabled:!0},title:i18nExpression("cusEntry.vendorMod.companyNameOrPersonName"),...requiredValidatorSegment},companyShortName:{type:"string","x-decorator":"FormItem","x-component-props":{disabled:!0},title:i18nExpression("cusEntry.vendorMod.personalAbbreviation"),...requiredValidatorSegment},businessLicense:{type:"string","x-hidden":!0},businessLicenseFileId:{type:"string",title:i18nExpression("cusEntry.vendorMod.frontOfIdCard"),"x-decorator":"FormItem","x-component":"SrmCommonFile","x-component-props":{"extra-data":{uploadType:"DEF",sourceType:"WEB_APP",fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},"default-file":{fileId:expression("$form.query('personBaseInfo').get('value').businessLicenseFileId"),fileName:expression("$form.query('personBaseInfo').get('value').businessLicense")},readonly:!0},...requiredValidatorSegment},extIdCardOppositeFileName:{type:"string","x-hidden":!0},extIdCardOppositeFileId:{type:"string",title:i18nExpression("cusEntry.vendorMod.backOfIdCard"),"x-decorator":"FormItem","x-component":"SrmCommonFile","x-component-props":{"extra-data":{uploadType:"DEF",sourceType:"WEB_APP",fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},"default-file":{fileId:expression("$form.query('personBaseInfo').get('value').extIdCardOppositeFileId"),fileName:expression("$form.query('personBaseInfo').get('value').extIdCardOppositeFileName")},readonly:!0},...requiredValidatorSegment},idNumber:{type:"string","x-decorator":"FormItem","x-component-props":{disabled:!0},title:i18nExpression("cusEntry.vendorMod.idNo"),...requiredValidatorSegment},validityPeriodOfCard:{type:"date","x-decorator":"FormItem",title:i18nExpression("cusEntry.vendorMod.validityPeriodOfCard"),"x-component-props":{type:"daterange",disabled:!0},...requiredValidatorSegment},extSex:{type:"string","x-decorator":"FormItem",title:i18nExpression("cusEntry.vendorMod.sex"),"x-component":"DictSelect","x-component-props":{code:"GENDER",disabled:!0},...requiredValidatorSegment},businessScope:{type:"string",title:i18nExpression("cusEntry.vendorMod.mainBusinessScope"),"x-decorator":"FormItem","x-component-props":{disabled:!0},...requiredValidatorSegment},companyCountry:{type:"string",title:i18nExpression("components.address.country"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"country",disabled:!0,placeholder:expression("$t('common.pleaseSelect')"),"@change":expression(`(val) => {
                    // 选择国外就清理省市区，并且禁用
                    if ($form.query('personBaseInfo.companyCountry').take().value !== 'CN') {
                      $form.query('personBaseInfo.companyProvince').take().value = ''
                      $form.query('personBaseInfo.companyCity').take().value = ''
                    }
                  }`)},...requiredValidatorSegment},companyProvince:{type:"string",title:i18nExpression("components.address.area"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"PROVINCE",disabled:!0,"custom-select-type":"PROVINCE",placeholder:expression("$t('common.pleaseSelect')")},...requiredValidatorSegment},companyCity:{type:"string",title:i18nExpression("components.address.city"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:expression("$form.query('personBaseInfo.companyProvince').take()?.value"),"custom-select-type":"CITY",placeholder:expression("$t('common.pleaseSelect')"),disabled:!0},...requiredValidatorSegment},companyAddress:{type:"string",title:i18nExpression("cusEntry.vendorMod.detailAddress"),"x-decorator":"FormItem","x-component-props":{disabled:!0},...requiredValidatorSegment}}}}}}}},serviceRange={serviceRange:{type:"void","x-component":"CollapseItem","x-component-props":{title:`{{observer(
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
                        }`),disabled:!0},title:"{{t('cusEntry.vendorMod.category', {index: $self.index + 1})}}",...requiredValidatorSegment},formBtn:{type:"void","x-visible":!1,"x-component":"ButtonList","x-component-props":{style:{"margin-top":"5px"}},properties:{add:{type:"void","x-component-props":{type:"primary","@click":expression(`() => {
                              $form.query($self.parent.parent.parent.parent.address.concat($self.index).concat('list')).take(field => {
                                field.componentProps.componentInstance.addRow()
                              })
                            }`)},title:i18nExpression("cusEntry.common.addCustomer")},delete:{type:"void",title:i18nExpression("cusEntry.common.deleteCategory"),"x-component-props":{type:"primary","@click":expression(`() => {
                              $form.query('serviceRangeList').take(field => {
                                field.remove($self.index)
                              })
                            }`)}}}}}}}},list:{type:"array","x-component":"RenderTable","x-query-engine-skip":!0,"x-component-props":{preColumns:"seq",editMode:!1,maxHeight:250,pagination:!1,sortable:!1},"x-hidden":"{{ JSON.stringify($self.query('.tableForm').take().value) === '{}' }}",properties:generateXindexInOrder({performanceAmount:{type:"string",title:i18nExpression("cusEntry.vendorMod.performance"),"x-render-table-column":{minWidth:120}},mainCustom:{type:"string",title:i18nExpression("cusEntry.vendorMod.mainCustomer"),"x-render-table-column":{minWidth:120},"x-validator":{required:!0,message:i18nExpression("cusEntry.tipMessage.required")}},fileId:{type:"string",title:i18nExpression("cusEntry.vendorMod.achievement"),"x-render-table-column":{minWidth:120},"x-component":"SrmCommonFile","x-component-props":{extraData:{fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},defaultFile:{fileId:expression("$table.getRowByIndex($self.index)?.fileId"),fileName:expression("$table.getRowByIndex($self.index)?.fileName")},readonly:!1,"@on-change":expression(`({file}) => {
                      const { fileId = '', fileName = '' } = file || {}
                      let row = $table.getRowByIndex($self.index)
                      row.fileId = fileId
                      row.fileName = fileName
                    }`)},"x-validator":{required:!0,message:i18nExpression("cusEntry.tipMessage.required")}},operation:{type:"void","x-visible":!1,title:i18nExpression("common.operation"),"x-render-table-column":{width:60,fiexd:"right"},"x-component":"RenderTableButtonList",properties:{delete:{type:"void",title:i18nExpression("common.delete"),"x-component-props":{type:"text","@click":expression(`() => {
                          $table.remove($self.index)
                        }`)}}}}})}}}},pagination:{type:"void","x-component":"ElPagination","x-component-props":{pageSize:10,layout:"total, prev, pager, next",total:expression("$form.query('CompanyInfo').get('data').totalServiceRangeList?.length"),"@current-change":expression(`(value) => {
            const totalServiceRangeList = $form.query('CompanyInfo').get('data').totalServiceRangeList
            const showList = totalServiceRangeList.slice((value - 1) * 10, value * 10)
            // 暂时解决渲染引擎底层存在的bug
            if (showList.length !== 10) {
              for (let i = showList.length; i < 10; i++) {
                showList.push({ tableForm: {}, list: [] })
              }
            }
            $form.query('serviceRangeList').take().value = showList
          }`)}}}}},specialControls={specialControls:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("cusEntry.vendorMod.specialControls")},"x-query-engine-skip":!0,properties:{layout:{type:"void","x-decorator":"FormLayout","x-decorator-props":{layout:"vertical"},"x-component":"FormGrid","x-component-props":{maxColumns:3,columnGap:32,rowGap:0},properties:{isBacklist:{type:"string",title:i18nExpression("vendorMod.isBacklist"),"x-component":"DictSelect","x-decorator":"FormItem","x-component-props":{code:"YES_OR_NO",disabled:!0}},focusFlag:{type:"string",title:i18nExpression("cusEntry.vendorMod.ifFocus"),"x-component":"DictSelect","x-decorator":"FormItem","x-component-props":{code:"YES_OR_NO",disabled:!0}},positionLimitFlag:{type:"string",title:i18nExpression("cusEntry.vendorMod.ifLimitUnit"),"x-component":"DictSelect","x-decorator":"FormItem","x-component-props":{code:"YES_OR_NO",disabled:!0}},categoryLimitFlag:{type:"string",title:i18nExpression("cusEntry.vendorMod.ifLimitCategory"),"x-component":"DictSelect","x-decorator":"FormItem","x-component-props":{code:"YES_OR_NO",disabled:!0}},timeLimitFlag:{type:"string",title:i18nExpression("cusEntry.vendorMod.ifLimitTime"),"x-component":"DictSelect","x-decorator":"FormItem","x-component-props":{code:"YES_OR_NO",disabled:!0}},limitDate:{type:"date",title:i18nExpression("cusEntry.vendorMod.limitDate"),"x-decorator":"FormItem","x-component-props":{disabled:!0}},contractVerification:{type:"string",title:i18nExpression("cusEntry.vendorMod.ifVertity"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"YES_OR_NO",disabled:!0}},allowClearWithoutSealFlag:{type:"string",title:i18nExpression("cusEntry.vendorMod.allowClearWithoutSealFlag"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"YES_OR_NO",disabled:!0}},allowBidWithoutSealFlag:{type:"string",title:i18nExpression("cusEntry.vendorMod.allowBidWithoutSealFlag"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"YES_OR_NO",disabled:!0}},allowQuotationWithoutSealFlag:{type:"string",title:i18nExpression("cusEntry.vendorMod.allowQuotationWithoutSealFlag"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"YES_OR_NO",disabled:!0}},biddingFlag:{type:"string",title:i18nExpression("cusEntry.vendorMod.isCompetition"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"YES_OR_NO",disabled:!0}},keySupervisionFlag:{type:"string",title:i18nExpression("cusEntry.vendorMod.keySupervisionFlag"),"x-decorator":"FormItem","x-component":"DictSelect","x-component-props":{code:"YES_OR_NO",disabled:!0}},accountGroup:{type:"string","x-decorator":"FormItem",title:i18nExpression("cusEntry.vendorMod.accountGroup"),"x-component-props":{disabled:!0}},partner:{type:"string","x-decorator":"FormItem",title:i18nExpression("cusEntry.vendorMod.tradePartner"),"x-component-props":{disabled:!0}},gscpStatus:{type:"string","x-decorator":"FormItem",title:i18nExpression("cusEntry.vendorMod.gscpStatus"),"x-component-props":{disabled:!0}}}}}}},qualificationInformation={qualificationInformation:{type:"void","x-component":"CollapseItem","x-component-props":{title:`{{observer(
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
      )}}`},"x-visible":generateCharExpressionByFunction(({$form})=>$form.query("state").get("data").userType!=="PERSONAL"),"x-query-engine-skip":!0,properties:{toolbar:{type:"void","x-visible":!1,"x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void",title:i18nExpression("common.add"),"x-component-props":{type:"primary","@click":expression(`() => {
                $self.query('qualificationInfo')
                  .take(field => {
                    field.componentProps.componentInstance.addRow('push', {})
                  })
              }`)}}}},managementAttaches:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!1,maxHeight:400,pagination:!1,sortable:!1},"x-query-engine-skip":!0,properties:generateXindexInOrder({authNum:{type:"string","x-component":"DictSelect","x-component-props":{code:"CERTIFICATE_TYPE"},title:i18nExpression("cusEntry.vendorMod.certificateType"),"x-render-table-column":{minWidth:120}},startDate:{type:"date",title:i18nExpression("cusEntry.vendorMod.startTime"),"x-render-table-column":{minWidth:120}},endDate:{type:"date",title:i18nExpression("cusEntry.vendorMod.endTime"),"x-render-table-column":{minWidth:120}},fileuploadId:{type:"string","x-component":"SrmCommonFile","x-component-props":{extraData:{fileModular:"sup",fileFunction:"companyInfoMaintain",fileType:"images"},defaultFile:{fileId:expression("$table.getRowByIndex($self.index)?.fileuploadId"),fileName:expression("$table.getRowByIndex($self.index)?.authType")},readonly:!1,"@on-change":expression(`({file}) => {
                const { fileId = '', fileName = '' } = file || {}
                let row = $table.getRowByIndex($self.index)
                row.fileuploadId = fileId
                row.authType = fileName
              }`)},title:i18nExpression("cusEntry.vendorMod.file"),"x-render-table-column":{minWidth:120}},authDescription:{type:"string",title:i18nExpression("cusEntry.vendorMod.remark"),"x-render-table-column":{minWidth:120}}})}}}},abnormalInfo={abnormalInfo:{type:"void","x-component":"CollapseItem","x-component-props":{title:i18nExpression("cusEntry.vendorMod.abnormalInfo")},"x-query-engine-skip":!0,properties:{npmCompanyExceptionInfos:{type:"array","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMod:!1,maxHeight:250,pagination:!1,sortable:!1},"x-query-engine-skip":!0,properties:generateXindexInOrder({exceptionType:{type:"string",title:i18nExpression("cusEntry.vendorMod.abnormalType"),"x-component":"DictSelect","x-component-props":{code:"SUPPLIER_EXCEPTION_TYPE"},"x-render-table-column":{minWidth:120}},exceptionInfo:{type:"string",title:i18nExpression("cusEntry.vendorMod.orgCategory"),"x-render-table-column":{minWidth:120}},exceptionRemark:{type:"string",title:i18nExpression("cusEntry.vendorMod.abnormalRemark"),"x-render-table-column":{minWidth:120}},creationDate:{type:"date",title:i18nExpression("cusEntry.vendorMod.createDate"),"x-render-table-column":{minWidth:120}}})}}}};export{companyInfo as a,companyBaseInfo as b,companyType as c,contactInfoList as d,bankInfoList as e,financeInfoList as f,companySizesList as g,specialControls as h,abnormalInfo as i,personBaseInfo as p,qualificationInformation as q,serviceRange as s,userInfoForm as u};
