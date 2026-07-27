// 合同固定元素
const fixed_element = [
  // {
  //   elemName: "备注",
  //   elemCode: "contractRemark",
  //   addMethod: 'REFERENCE'
  // },
  // {
  //   elemName: "合同总金额(含税)",
  //   elemCode: "includeTaxAmount",
  //   addMethod: 'REFERENCE'
  // },
  // {
  //   elemName: "合同总金额(含税大写)",
  //   elemCode: "amountUpperCase",
  //   addMethod: 'REFERENCE'
  // },
  // {
  //   elemName: "合同级别",
  //   elemCode: "contractLevel",
  //   dictionaryCode: "CONTARCT_LEVEL",
  //   addMethod: 'REFERENCE'
  // },
  {
    elemName: '合同编号',
    elemCode: 'contractCode',
    addMethod: 'REFERENCE'
  },
  // {
  //   elemName: "合同类型",
  //   elemCode: "contractClass",
  //   dictionaryCode: "ELEM_CONTRACT_TYPE",
  //   addMethod: 'REFERENCE'
  // },
  // {
  //   elemName: "甲方名称",
  //   elemCode: "partyA",
  //   addMethod: 'REFERENCE'
  // },
  // {
  //   elemName: "证书编号",
  //   elemCode: "certificateNo",
  //   addMethod: 'REFERENCE'
  // },
  // {
  //   elemName: "币种",
  //   elemCode: "currencyName",
  //   addMethod: 'REFERENCE'
  // },
  // {
  //   elemName: "供应商名称",
  //   elemCode: "vendorName",
  //   addMethod: 'REFERENCE'
  // },
  // {
  //   elemName: "供应商编码",
  //   elemCode: "vendorCode",
  //   addMethod: 'REFERENCE'
  // },
  // {
  //   elemName: "合同有效期从",
  //   elemCode: "effectiveDateFrom",
  //   addMethod: 'REFERENCE'
  // },
  // {
  //   elemName: "合同有效期至",
  //   elemCode: "effectiveDateTo",
  //   addMethod: 'REFERENCE'
  // },
  {
    elemName: '甲方',
    elemCode: 'party',
    addMethod: 'REFERENCE'
  },
  {
    elemName: '甲方(盖章用)',
    elemCode: 'stampParty',
    addMethod: 'REFERENCE'
  },
  {
    elemName: '乙方',
    elemCode: 'secondParty',
    addMethod: 'REFERENCE'
  },
  {
    elemName: '丙方',
    elemCode: 'thirdParty',
    addMethod: 'REFERENCE'
  },
  {
    elemName: '甲方联系人',
    elemCode: 'partyContacts',
    addMethod: 'REFERENCE'
  },
  {
    elemName: '甲方手机号',
    elemCode: 'partyPhone',
    addMethod: 'REFERENCE'
  },
  {
    elemName: '甲方地址',
    elemCode: 'partyAddress',
    addMethod: 'REFERENCE'
  },
  {
    elemName: '甲方传真',
    elemCode: 'partyFax',
    addMethod: 'REFERENCE'
  },
  {
    elemName: '乙方联系人',
    elemCode: 'secondPartyContacts',
    addMethod: 'REFERENCE'
  },
  {
    elemName: '乙方手机号',
    elemCode: 'secondPartyPhone',
    addMethod: 'REFERENCE'
  },
  {
    elemName: '乙方地址',
    elemCode: 'secondPartyAddress',
    addMethod: 'REFERENCE'
  },
  {
    elemName: '乙方传真',
    elemCode: 'secondPartyFax',
    addMethod: 'REFERENCE'
  },
  {
    elemName: '丙方联系人',
    elemCode: 'thirdPartyContacts',
    addMethod: 'REFERENCE'
  },
  {
    elemName: '丙方手机号',
    elemCode: 'thirdPartyPhone',
    addMethod: 'REFERENCE'
  },
  {
    elemName: '丙方地址',
    elemCode: 'thirdPartyAddress',
    addMethod: 'REFERENCE'
  },
  {
    elemName: '丙方传真',
    elemCode: 'thirdPartyFax',
    addMethod: 'REFERENCE'
  },
  {
    elemName: '合同名称',
    elemCode: 'contractName',
    addMethod: 'REFERENCE'
  },
  {
    elemName: '签约地址',
    elemCode: 'signingAddress',
    addMethod: 'REFERENCE'
  },
  {
    elemName: '合同交期',
    elemCode: 'contractDeliveryDate',
    addMethod: 'REFERENCE'
  },
  {
    elemName: '物料合计',
    elemCode: 'totalItems',
    addMethod: 'REFERENCE'
  },
  {
    elemName: '物料金额合计(大写)',
    elemCode: 'totalMaterialAmount',
    addMethod: 'REFERENCE'
  },
  {
    elemName: '甲方-纳税人识别号',
    elemCode: 'partyTaxpayer',
    addMethod: 'REFERENCE'
  },
  {
    elemName: '甲方-开户行',
    elemCode: 'partyBank',
    addMethod: 'REFERENCE'
  },
  {
    elemName: '甲方-银行账号',
    elemCode: 'partyBankAccount',
    addMethod: 'REFERENCE'
  },
  {
    elemName: '甲方-税号',
    elemCode: 'partyTax',
    addMethod: 'REFERENCE'
  },
  {
    elemName: '乙方-纳税人识别号',
    elemCode: 'secondPartyTaxpayer',
    addMethod: 'REFERENCE'
  },
  {
    elemName: '乙方-开户行',
    elemCode: 'secondPartyBank',
    addMethod: 'REFERENCE'
  },
  {
    elemName: '乙方-银行账号',
    elemCode: 'secondPartyBankAccount',
    addMethod: 'REFERENCE'
  },
  {
    elemName: '乙方-税号',
    elemCode: 'secondPartyTax',
    addMethod: 'REFERENCE'
  },
  {
    elemName: '丙方-纳税人识别号',
    elemCode: 'thirdPartyTaxpayer',
    addMethod: 'REFERENCE'
  },
  {
    elemName: '丙方-开户行',
    elemCode: 'thirdPartyBank',
    addMethod: 'REFERENCE'
  },
  {
    elemName: '丙方-银行账号',
    elemCode: 'thirdPartyBankAccount',
    addMethod: 'REFERENCE'
  },
  {
    elemName: '丙方-税号',
    elemCode: 'thirdPartyTax',
    addMethod: 'REFERENCE'
  }
]

export default fixed_element
