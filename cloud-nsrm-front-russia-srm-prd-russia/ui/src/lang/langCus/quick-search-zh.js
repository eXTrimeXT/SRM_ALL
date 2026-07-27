export default {
  scc_pj_contract_seal: {
    scc_pj_contract_seal_title: '印章',
    sealName: '印章名称',
    sealId: '印章ID'
  },
  ca_scc_npm_sou_bid_price: {
    ca_scc_npm_sou_bid_price_title: '招标历史价格',
    souName: '寻源名称',
    projectNo: '招标项目编号',
    vendorName: '中标供应商',
    itemDesc: '名称',
    specification: '规格/型号',
    brand: '品牌',
    priceTax: '含税单价（万元）',
    fixedPriceTax: '固定含税单价（万元）'
  },
  // 开票管理-SAP成本中心
  npm_invoice_cost_center: {
    npm_invoice_cost_center_title: 'SAP成本中心',
    jyorgnumber: '经营体编码',
    jyorgname: '经营体名称',
    costnumber: '成本中心编码',
    costname: '成本中心名称'
  },
  ceea_storage_return: {
    ceea_storage_return_title: '选择供应商',
    vendorCode: '供应商编码',
    vendorName: '供应商名称'
  },
  sou_ch_design_plan:{
    sou_ch_design_plan_title: '项目选择',
    projectCode: '项目编码',
    projectName: '项目名称',
    status: '状态'
  },
  // 项目策划方案
  scc_npm_sou_purinq_project_fixprice: {
    scc_npm_sou_purinq_project_fixprice_title: '项目策划方案',
    designProjectCode: '项目编号',
    designProjectName: '项目名称',
    souNo: '询价单号'
  },
   /* 新增供应商 */
  scc_sup_company_info2: {
    scc_sup_company_info2_title: '新增供应商',
    companyId: 'ID',
    status: '单据状态（状态：拟定，已提交，已审批）',
    companyCode: '供应商编码',
    erpVendorId: 'ERP供应商Id（美云新增，NSrm推送Erp成功后，Erp回传的erpVendorId）',
    erpVendorCode: 'ERP供应商Code（美云新增，NSrm推送Erp成功后，Erp回传的erpVendorCode）',
    companyName: '供应商名称',
    companyEnName: '企业名称（英文）',
    overseasRelation: '境外关系',
    overseasRelationName: '境外关系名称',
    companyType: '企业性质',
    companyTypeName: '企业性质名称',
    companyShortName: '企业简称',
    lcCode: '统一社会信用代码',
    businessLicenseFileId: '上传(三证合一)文件ID',
    businessLicense: '上传三证合一照片名称',
    idNumber: '身份证号码',
    dunsCode: 'DUNS编号',
    companyStatus: '经营状态',
    companyStatusName: '经营状态名称',
    legalPerson: '法定代表人',
    registeredCapital: '注册资金',
    companyCreationDate: '企业成立日期',
    businessStartDate: '营业日期开始',
    businessEndDate: '营业日期结束',
    companyRegisteredDate: '企业注册日期',
    companyCountry: '营业地址（国家/地区）',
    companyProvince: '营业地址（省份/州）',
    companyCity: '营业地址（城市）',
    companyAddress: '详细地址',
    materialCategory: '可供物料品类',
    businessScope: '经营范围',
    registrationAuthority: '登记机关',
    registCurrency: '注册资金币种',
    registCurrencyName: '注册资金币种名称',
    isBacklist: '是否黑名单',
    vendorClassification:
      '供应商分级（字典编码：SUPPLIER_CLASSIFICATION，STRATEGIC_SUPPLIER：战略供应商；PARTNER_SUPPLIERS：合作供应商）',
    ifLongPeriod: '是否长期供应商（Y：是，N：否，默认否）',
    backlistUpdatedBy: '黑名单更新人',
    backlistUpdatedDate: '黑名单更新时间',
    dataSources: '1、注册用户 2、绿色通道用户',
    blackListEffectiveDate: '黑名单生效日期',
    approvingDate: '核准日期',
    approvalInfo: '审批信息',
    applicationNumber: '注册申请号',
    applicationDate: '注册申请日期',
    approvedDate: '审批日期/准入日期',
    approvedBy: '审批人',
    approver: '审批人账号',
    statusName: '状态名字',
    ceeaCompanyWebsite: '公司网站',
    ceeaAgentBrand: '代理品牌',
    ceeaListedTime: '上市时间',
    ceeaIfListed: '是否上市',
    ceeaPlantArea: '厂房面积',
    ceeaPlantType: '厂房性质',
    ceeaMainCategoryName: '主营品类名称',
    ceeaMainCategoryCode: '主营品类编码',
    ceeaMainCategoryId: '主营品类ID',
    ceeaCompanyIntro: '公司简介',
    ceeaIndustryType: '行业类型',
    ceeaBusinessModel: '商业模式',
    ceeaSupBusinessType: '供应商业务类型',
    email: '有效',
    nickname: '昵称',
    createdId: '创建人ID',
    createdBy: '创建人',
    creationDate: '创建时间',
    createdByIp: '创建人IP',
    lastUpdatedId: '最后更新人ID',
    lastUpdatedBy: '最后更新人',
    lastUpdateDate: '最后更新时间',
    lastUpdatedByIp: '最后更新人IP',
    tenantId: '租户ID',
    version: '版本号'
  },
  /* 资质审查品类选择 */
  ceea_sup_auth_cate_journal_supplier: {
    ceea_sup_auth_cate_journal_supplier_title: '品类选择',
    categoryName: '品类名称',
    categoryCode: '品类编码',
    categoryFullName: '品类全路径'
  },
  // 招标资料专家库
  scc_npm_sou_expert: {
    scc_npm_sou_expert_title: '专家选择',
    expertFullName: '专家名称'
  },
  // 质疑澄清
  sou_answer_projet: {
    sou_answer_projet_title: '项目选择',
    souName: '项目名称',
    extProjectNo: '项目编码',
    createdFullName: '创建人名称',
    creationDate: '创建日期'
  },
  // 质疑澄清2
  answer_vendor_search: {
    answer_vendor_search_title: '供应商选择',
    vendorCode: '供应商编码',
    vendorName: '供应商名称'
  },
  // 适用板块
  plate_scc_base_organization: {
    plate_scc_base_organization_title: '选择板块',
    organizationId: '板块-组织ID',
    organizationCode: '板块-组织编码',
    organizationName: '板块-组织名称'
  },
  // 项目计划快查
  scc_npm_pr_project_plan_query: {
    scc_npm_pr_project_plan_query_title: '项目计划',
    projectName: '项目名称',
    planNo: '项目编码'
  },
  // 采购物料快查
  scc_npm_pr_material_query: {
    scc_npm_pr_material_query_title: '物料信息查询',
    materialCode: '物料编码',
    materialName: '物料名称',
    specification: '规格型号',
    categoryFullName: '采购品类'
  },
  // 考察管理 查询供应商
  scc_npm_inspect_vendor: {
    scc_npm_inspect_vendor_title: '供应商信息',
    vendorCode: '供应商编码',
    vendorName: '供应商名称'
  },
  // 考察管理、借阅管理 查询招标项目
  scc_npm_sou_project: {
    scc_npm_sou_project_title: '招标项目信息',
    projectId: '招标ID',
    souNo: '招标单号',
    souName: '项目名称',
    extProjectNo: '招标项目编号',
    createdFullName: '创建人'
  },
  // 招标管理-招标工作小组
  ext_sou_project_group: {
    ext_sou_project_group_title: '招标工作组人员',
    ceeaEmpNo: '工号',
    nickname: '姓名',
    phone: '电话',
    email: '邮箱',
    groupRole: '角色',
    extExpertLevel: '专家等级',
    ceeaJobcodeDescr: '岗位',
    projectId: '招标ID'
  },
  sou_npm_expert_user_display: {
    sou_npm_expert_user_display_title: '指定专家',
    username: '工号',
    nickname: '姓名',
    phone: '手机',
    email: '邮箱',
    department: '部门',
    expertLevel: '专家等级',
    ceeaJobcodeDescr: '岗位',
    extOfficePhone: '办公电话'
  },
  // 招标资料提交
  pr_requirement_head: {
    pr_requirement_head_title: '申请单号',
    requirementHeadNum: '申请单号',
    applyBy: '申请人',
    creationDate: '创建日期',
    projectName: '项目名称',
    applyUserName: '申请人'
  },
  scc_pj_source_pubconfig: {
    scc_pj_source_pubconfig_title: '寻源公示模板',
    pubconfigName: '模板名称',
    organizationCode: '板块编码',
    organizationName: '板块名称',
    status: '单据状态',
    bankName: '开户银行',
  },
  sou_req_head: {
    sou_req_head_title: '申请单号',
    reqHeadNo: '申请单号',
    reqUserName: '创建人',
    projectName: '项目名称',
    creationDate: '创建日期',
    responsibilityUserName: '供应商负责人'
  },
  sou_recommvendor_bid: {
    sou_recommvendor_bid_title: '选择供应商',
    companyCode: '供应商编码',
    companyName: '供应商名称',
    sourceType: '来源'
  },
  sou_recommvendor_sou: {
    sou_recommvendor_sou_title: '选择供应商',
    companyCode: '供应商编码',
    companyName: '供应商名称',
    sourceType: '来源'
  },
  scc_pj_organization_role: {
    scc_pj_organization_role_title: '请选择上层流程角色',
    roleName: '流程角色',
    roleCode: '流程编码',
  },
  // 不公示推荐供应商
  REQQIRE_TO_RECOMM_WITHOUT_PUBLIC: {
    REQQIRE_TO_RECOMM_WITHOUT_PUBLIC_title: '不公示推荐',
    projectName: '项目名称',
    applyByNickname: '申请人',
    souNo: '招标单号'
  },
  // 公示推荐供应商
  REQQIRE_TO_RECOMM_PUBLIC: {
    REQQIRE_TO_RECOMM_PUBLIC_title: '公示推荐',
    projectName: '项目名称',
    applyByNickname: '申请人',
    souNo: '招标单号',
    applyDate: '申请日期',
    reqHeadNo: '寻源需求单号'
  },
  // 不公式邀请供应商
  pr_requirement_head2: {
    pr_requirement_head2_title: '申请单号',
    requirementHeadNum: '申请单号',
    applyBy: '申请人',
    creationDate: '创建日期',
    projectName: '项目名称',
    applyUserName: '申请人',
    responsibilityUserName: '供应商负责人'
  },
  // 标前交流 申请单号快查
  pr_requirement_head3: {
    pr_requirement_head3_title: '选择申请单',
    requirementHeadNum: '申请单号',
    creationDate: '创建日期',
    applyUserName: '申请人',
    projectName: '项目名称'
  },
  REQQIRE_TO_SOU_PROJECT: {
    REQQIRE_TO_SOU_PROJECT_title: '创建招标流程',
    projectName: '项目名称',
    sendSouProfileStatus: '招标资料状态',
    applyByNickname: '申请人',
    applyDate: '申请日期',
    souNo:'推荐供应商单号'
  },
  scc_npm_sou_fix_price_pass: {
    scc_npm_sou_fix_price_pass_title: '签约明细',
    fixPriceNo: '定价单号',
    orgOuName: '公司名称',
    itemCode: '物料编码',
    itemDesc: '物料名称',
    unit: '单位',
    quantity: '数量',
    vendorName: '供应商名称',
    notaxPrice: '未税单价',
    notaxTotalPrice:'未税总价'
  },
  scc_base_material_item_contract: {
    scc_base_material_item_contract_title: '物料展示弹框',
    categoryFullName: '品类全称',
    orderQuantityMinimum: '最小起订量',
    brand: '品牌',
    minimumPackagingQuantity: '最小包装量(内箱)',
    purchaseCycle: '采购周期',
    outboxMaxPackagingQuantity: '最大包装量(外箱)',
    minimumSafetyInventory: '最小安全库存',
    materialCode: '物料编码',
    materialName: '物料名称',
    categoryName: '品类名称',
    unitName: '单位',
    materialType: '规格型号'
  },
  scc_base_organization_invoice: {
    scc_base_organization_invoice_title: '选择公司',
    organizationCode: '公司编码',
    organizationName: '公司名称'
  },
  pre_bid_notice: {
    pre_bid_notice_title: '选择交流通知单',
    bidNoticeNo: '交流通知单',
    projectName: '项目名称',
    requirementHeadNo: '申请单号',
    creationDate: '创建时间',
    createdFullName: '创建人'
  },
  sou_ch_ledger: {
    sou_ch_ledger_title: '选择项目',
    projectName: '项目名称'
  },
  scc_base_purchase_category5: {
    scc_base_purchase_category5_title: '品类查询',
    categoryCode: '品类编码',
    categoryName: '品类名称'
  },
  sou_purfix_price_contract: {
    sou_purfix_price_contract_title: '签约明细',
    designProjectCode: '定价单号',
    createUserOrgOuName: '公司名称',
    vendorName: '供应商名称',
    itemCode: '物料编码',
    itemDesc: '物料名称',
    model: '规格型号',
    requireQuantity: '需求数量',
    notaxPrice: '未税单价',
    notaxTotalPrice: '未税总价'
  },
  scc_sup_company_info_display_tz: {
    scc_sup_company_info_display_tz_title: '供应商信息',
    companyCode: '供应商编码',
    companyName: '供应商名称',
    ceeacontactmethod: '联系电话',
    contactname: '联系人'
  },
  scc_pj_bpm_incorporated_company: {
    scc_pj_bpm_incorporated_company_title: '签约主体',
    companyName: '公司名称',
    creditCode: '统一社会信用代码'
  }
  ,scc_base_material_item2: {
    scc_base_material_item2_title: '物料查询',
    materialCode: '物料编码',
    materialName: '物料名称',
    description: '是否已映射'
  }
}
