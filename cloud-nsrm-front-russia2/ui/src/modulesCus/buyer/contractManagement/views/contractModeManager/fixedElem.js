const fixedElem = [
  // {
  //   elemName: "备注",
  //   elemCode: "$contractRemark",
  //   key: "contractRemark",
  //   addMethod: "REFERENCE"
  // },
  // {
  //   elemName: "合同总金额(含税)",
  //   elemCode: "$includeTaxAmount",
  //   key: "includeTaxAmount",
  //   addMethod: "REFERENCE"
  // },
  // {
  //   elemName: "合同总金额(含税大写)",
  //   elemCode: "$amountUpperCase",
  //   key: "amountUpperCase",
  //   addMethod: "REFERENCE"
  // },
  // {
  //   elemName: "合同级别",
  //   elemCode: "$contractLevel",
  //   key: "contractLevel",
  //   dictionaryCode: "CONTARCT_LEVEL",
  //   addMethod: "REFERENCE"
  // },
  {
    // 合同编号
    elemName: this.$t('orderMod.buyerOrderSynergy.contractNo'),
    elemCode: '$contractCode',
    key: 'contractCode',
    addMethod: 'REFERENCE'
  },
  // {
  //   elemName: "合同类型",
  //   elemCode: "$contractClass",
  //   dictionaryCode: "ELEM_CONTRACT_TYPE",
  //   key: "contractClass",
  //   addMethod: "REFERENCE"
  // },
  // {
  //   elemName: "甲方名称",
  //   elemCode: "$partyA",
  //   key: "partyA",
  //   addMethod: "REFERENCE"
  // },
  // {
  //   elemName: "证书编号",
  //   elemCode: "$certificateNo",
  //   key: "certificateNo",
  //   addMethod: "REFERENCE"
  // },
  // {
  //   elemName: "币种",
  //   elemCode: "$currencyName",
  //   key: "currencyName",
  //   addMethod: "REFERENCE"
  // },
  // {
  //   elemName: "供应商名称",
  //   elemCode: "$vendorName",
  //   key: "vendorName",
  //   addMethod: "REFERENCE"
  // },
  // {
  //   elemName: "供应商编码",
  //   elemCode: "$vendorCode",
  //   key: "vendorCode",
  //   addMethod: "REFERENCE"
  // },
  {
    elemName: this.$t('contractMod.contractValidFrom'), //  '合同有效期从'
    elemCode: '$effectiveDateFrom',
    key: 'effectiveDateFrom',
    addMethod: 'REFERENCE'
  },
  {
    elemName: this.$t('contractMod.contractValidTo'),  // '合同有效期至'
    elemCode: '$effectiveDateTo',
    key: 'effectiveDateTo',
    addMethod: 'REFERENCE'
  },
  {
    elemName: this.$t('contractMod.owner'),  // '甲方'
    elemCode: '$party',
    key: 'party',
    addMethod: 'REFERENCE'
  },
  // {
  //   elemName: "甲方(盖章用)",
  //   elemCode: "$stampParty",
  //   key: "stampParty",
  //   addMethod: "REFERENCE"
  // },
  {
    elemName: this.$t('contractMod.partyB'),  // '乙方'
    elemCode: '$secondParty',
    key: 'secondParty',
    addMethod: 'REFERENCE'
  },
  // {
  //   elemName: '丙方',
  //   elemCode: '$thirdParty',
  //   key: 'thirdParty',
  //   addMethod: 'REFERENCE'
  // },
  {
    elemName: this.$t('cusEntry.supplement20250205.partyAContactPerson'), // '甲方联系人'
    elemCode: '$partyContacts',
    key: 'partyContacts',
    addMethod: 'REFERENCE'
  },
  {
    elemName: this.$t('cusEntry.supplement20250205.partyAPhoneNumber'),  // '甲方手机号'
    elemCode: '$partyPhone',
    key: 'partyPhone',
    addMethod: 'REFERENCE'
  },
  {
    elemName: this.$t('cusEntry.supplement20250205.partyAAddress'),  // '甲方地址'
    elemCode: '$partyAddress',
    key: 'partyAddress',
    addMethod: 'REFERENCE'
  },
  // {
  //   elemName: '甲方传真',
  //   elemCode: '$partyFax',
  //   key: 'partyFax',
  //   addMethod: 'REFERENCE'
  // },
  {
    elemName: this.$t('cusEntry.supplement20250205.secondPartyContactPerson'),  // '乙方联系人'
    elemCode: '$secondPartyContacts',
    key: 'secondPartyContacts',
    addMethod: 'REFERENCE'
  },
  {
    elemName: this.$t('cusEntry.supplement20250205.secondPartyPhoneNumber'),  // '乙方手机号'
    elemCode: '$secondPartyPhone',
    key: 'secondPartyPhone',
    addMethod: 'REFERENCE'
  },
  {
    elemName: this.$t('contractMod.secondSite'),  // '乙方地址'
    elemCode: '$secondPartyAddress',
    key: 'secondPartyAddress',
    addMethod: 'REFERENCE'
  },
  // {
  //   elemName: '乙方传真',
  //   elemCode: '$secondPartyFax',
  //   key: 'secondPartyFax',
  //   addMethod: 'REFERENCE'
  // },
  // {
  //   elemName: '丙方联系人',
  //   elemCode: '$thirdPartyContacts',
  //   key: 'thirdPartyContacts',
  //   addMethod: 'REFERENCE'
  // },
  // {
  //   elemName: '丙方手机号',
  //   elemCode: '$thirdPartyPhone',
  //   key: 'thirdPartyPhone',
  //   addMethod: 'REFERENCE'
  // },
  // {
  //   elemName: '丙方地址',
  //   elemCode: '$thirdPartyAddress',
  //   key: 'thirdPartyAddress',
  //   addMethod: 'REFERENCE'
  // },
  // {
  //   elemName: '丙方传真',
  //   elemCode: '$thirdPartyFax',
  //   key: 'thirdPartyFax',
  //   addMethod: 'REFERENCE'
  // },
  {
    elemName: this.$t('vendorMod.contractName'),  // '合同名称'
    elemCode: '$contractName',
    key: 'contractName',
    addMethod: 'REFERENCE'
  },
  {
    elemName: this.$t('contractMod.signingAddress'),  // '签约地址'
    elemCode: '$signingAddress',
    key: 'signingAddress',
    addMethod: 'REFERENCE'
  },
  {
    elemName: this.$t('cusEntry.supplement20250205.contractDeliveryDate'),  // '合同交期'
    elemCode: '$contractDeliveryDate',
    key: 'contractDeliveryDate',
    addMethod: 'REFERENCE'
  },
  {
    elemName: this.$t('cusEntry.supplement20250205.materialTotal'), // '物料合计'
    elemCode: '$totalItems',
    key: 'totalItems',
    addMethod: 'REFERENCE'
  },
  {
    elemName: this.$t('cusEntry.supplement20250205.materialAmountSumInWords'),  // '物料金额合计(大写)'
    elemCode: '$totalMaterialAmount',
    key: 'totalMaterialAmount',
    addMethod: 'REFERENCE'
  },
  {
    elemName: this.$t('cusEntry.supplement20250205.partyATaxpayerId'),  // '甲方-纳税人识别号'
    elemCode: '$partyTaxpayer',
    key: 'partyTaxpayer',
    addMethod: 'REFERENCE'
  },
  {
    elemName: this.$t('cusEntry.supplement20250205.partyAOpeningBank'), // '甲方-开户行'
    elemCode: '$partyBank',
    key: 'partyBank',
    addMethod: 'REFERENCE'
  },
  {
    elemName: this.$t('cusEntry.supplement20250205.partyABankAccount'),  // '甲方-银行账号'
    elemCode: '$partyBankAccount',
    key: 'partyBankAccount',
    addMethod: 'REFERENCE'
  },
  {
    elemName: this.$t('cusEntry.supplement20250205.partyATaxNumber'),  // '甲方-税号'
    elemCode: '$partyTax',
    key: 'partyTax',
    addMethod: 'REFERENCE'
  },
  {
    elemName: this.$t('cusEntry.supplement20250205.partyBTaxpayerId'),  // '乙方-纳税人识别号'
    elemCode: '$secondPartyTaxpayer',
    key: 'secondPartyTaxpayer',
    addMethod: 'REFERENCE'
  },
  {
    elemName: this.$t('cusEntry.supplement20250205.second_party_bank'),  // '乙方-开户行'
    elemCode: '$secondPartyBank',
    key: 'secondPartyBank',
    addMethod: 'REFERENCE'
  },
  {
    elemName: this.$t('cusEntry.supplement20250205.partyBBankAccount'),  // '乙方-银行账号'
    elemCode: '$secondPartyBankAccount',
    key: 'secondPartyBankAccount',
    addMethod: 'REFERENCE'
  },
  {
    elemName: this.$t('cusEntry.supplement20250205.partyBTaxNumber'),  // '乙方-税号'
    elemCode: '$secondPartyTax',
    key: 'secondPartyTax',
    addMethod: 'REFERENCE'
  }
  // {
  //   elemName: '丙方-纳税人识别号',
  //   elemCode: '$thirdPartyTaxpayer',
  //   key: 'thirdPartyTaxpayer',
  //   addMethod: 'REFERENCE'
  // },
  // {
  //   elemName: '丙方-开户行',
  //   elemCode: '$thirdPartyBank',
  //   key: 'thirdPartyBank',
  //   addMethod: 'REFERENCE'
  // },
  // {
  //   elemName: '丙方-银行账号',
  //   elemCode: '$thirdPartyBankAccount',
  //   key: 'thirdPartyBankAccount',
  //   addMethod: 'REFERENCE'
  // },
  // {
  //   elemName: '丙方-税号',
  //   elemCode: '$thirdPartyTax',
  //   key: 'thirdPartyTax',
  //   addMethod: 'REFERENCE'
  // }
]

export default fixedElem
