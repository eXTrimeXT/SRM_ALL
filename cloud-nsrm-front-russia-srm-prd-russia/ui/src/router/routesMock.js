// 左边菜单路由配置模拟接口返回数据配置
// permissionId -- id设置唯一值
// permissionCode -- 对应route下面的name值
// functionAddress  -- 对应route 下面的路由路径
// fdLangType -- 多语言类型
// iconPath  -- 对应的是icon 的class 阿里图标的 class值
// permissionName -- 对应配置route的mate.title值
export default [
  // 用户列表
  {
    'permissionId': '1116239393678790656',
    'permissionCode': 'userManage',
    'functionAddress': null,
    'fdLangType': 'zh-CN',
    'iconPath': 'icongerenzhongxin',
    'permissionName': '用户管理',
    'childPermissions': [
      {
        'permissionId': '1116239520441815040',
        'permissionCode': 'companyInfoMaintain',
        'functionAddress': '/userManage/companyInfoMaintain',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '企业信息维护',
        'childPermissions': []
      }
    ]
  },
  {
    'permissionId': '1129231030553337856',
    'permissionCode': 'accountAccess',
    'functionAddress': null,
    'fdLangType': 'zh-CN',
    'iconPath': 'iconscc-account-privileges',
    'permissionName': '账户权限',
    'childPermissions': [
      {
        'permissionId': '1202053239168286720',
        'permissionCode': 'functionMaintenance',
        'functionAddress': '/accountAccess/functionMaintenance',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '功能维护',
        'childPermissions': []
      },
      {
        'permissionId': '1149245321670967296',
        'permissionCode': 'menuMaintenance',
        'functionAddress': '/accountAccess/menuMaintenance',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '菜单维护',
        'childPermissions': []
      },
      {
        'permissionId': '114924532dd1676',
        'permissionCode': 'roleMaintenance',
        'functionAddress': '/accountAccess/roleMaintenance',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '角色维护',
        'childPermissions': []
      },
      {
        'permissionId': '1149245321dd676',
        'permissionCode': 'usersAccess',
        'functionAddress': '/accountAccess/usersAccess',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '用户权限(采购商)',
        'childPermissions': []
      },
      {
        'permissionId': '1140-6',
        'permissionCode': 'accountManagement',
        'functionAddress': '/accountAccess/accountManagement',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '账号管理(供应商)',
        'childPermissions': []
      }
    ]
  },
  {
    'permissionId': '11292310305dsds856',
    'permissionCode': 'baseSetting',
    'functionAddress': null,
    'fdLangType': 'zh-CN',
    'iconPath': 'iconxitongguanli',
    'permissionName': '基础设置',
    'childPermissions': [{
      'permissionId': '12020532391dss20',
      'permissionCode': 'settingGuide',
      'functionAddress': '/baseSetting/settingGuide',
      'fdLangType': 'zh-CN',
      'iconPath': null,
      'permissionName': '配置导引',
      'childPermissions': []
    },
    {
      'permissionId': '12020532dss20',
      'permissionCode': 'organizationSetting',
      'functionAddress': '/baseSetting/organizationSetting',
      'fdLangType': 'zh-CN',
      'iconPath': null,
      'permissionName': '组织设置',
      'childPermissions': []
    },
    {
      'permissionId': '120205323432kll000',
      'permissionCode': 'vendorAttributeControl',
      'functionAddress': '/baseSetting/vendorAttributeControl',
      'fdLangType': 'zh-CN',
      'iconPath': null,
      'permissionName': '供应商属性管理',
      'childPermissions': []
    },
    {
      'permissionId': '1202053234320',
      'permissionCode': 'vendorAttributeSetting',
      'functionAddress': '/baseSetting/vendorAttributeSetting',
      'fdLangType': 'zh-CN',
      'iconPath': null,
      'permissionName': '供应商属性配置',
      'childPermissions': []
    },
    {
      'permissionId': '1202053254230',
      'permissionCode': 'dictionaryMaintenance',
      'functionAddress': '/baseSetting/dictionaryMaintenance',
      'fdLangType': 'zh-CN',
      'iconPath': null,
      'permissionName': '字典维护',
      'childPermissions': []
    }, {
      'permissionId': '1202053254230',
      'permissionCode': 'messageMaintenance',
      'functionAddress': '/baseSetting/messageMaintenance',
      'fdLangType': 'zh-CN',
      'iconPath': null,
      'permissionName': '消息定义',
      'childPermissions': []
    },
    {
      'permissionId': '12020532d131s20',
      'permissionCode': 'manageLevelSetting',
      'functionAddress': '/baseSetting/manageLevelSetting',
      'fdLangType': 'zh-CN',
      'iconPath': null,
      'permissionName': '管理层级设置',
      'childPermissions': []
    },
    {
      'permissionId': '12020532dss20',
      'permissionCode': 'accessFlowSetting',
      'functionAddress': '/baseSetting/accessFlowSetting',
      'fdLangType': 'zh-CN',
      'iconPath': null,
      'permissionName': '准入流程配置',
      'childPermissions': []
    },
    {
      'permissionId': '12020532sdsds20',
      'permissionCode': 'businessStateControl',
      'functionAddress': '/baseSetting/businessStateControl',
      'fdLangType': 'zh-CN',
      'iconPath': null,
      'permissionName': '业务状态控制',
      'childPermissions': []
    },
    {
      'permissionId': '12020532dsssss20',
      'permissionCode': 'fileManagement',
      'functionAddress': '/baseSetting/fileManagement',
      'fdLangType': 'zh-CN',
      'iconPath': null,
      'permissionName': '附件管理',
      'childPermissions': []
    }, {
      'permissionId': '12020532dsswss20',
      'permissionCode': 'categoryDivision',
      'functionAddress': '/baseSetting/categoryDivision',
      'fdLangType': 'zh-CN',
      'iconPath': null,
      'permissionName': '品类分工',
      'childPermissions': []
    }, {
      'permissionId': '123ss20',
      'permissionCode': 'purchaseCategoryMaintenance',
      'functionAddress': '/baseSetting/purchaseCategoryMaintenance',
      'fdLangType': 'zh-CN',
      'iconPath': null,
      'permissionName': '采购分类维护',
      'childPermissions': []
    },
    {
      'permissionId': '1202053254320',
      'permissionCode': 'materialMaintenance',
      'functionAddress': '/baseSetting/materialMaintenance',
      'fdLangType': 'zh-CN',
      'iconPath': null,
      'permissionName': '物料维护',
      'childPermissions': []
    },
    {
      'permissionId': '12020532ds54320',
      'permissionCode': 'purchaseBaseSetting',
      'functionAddress': '/baseSetting/purchaseBaseSetting',
      'fdLangType': 'zh-CN',
      'iconPath': null,
      'permissionName': '采购基础设置',
      'childPermissions': []
    },
    {
      'permissionId': '1202053543320',
      'permissionCode': 'vendorDataImport',
      'functionAddress': '/baseSetting/vendorDataImport',
      'fdLangType': 'zh-CN',
      'iconPath': null,
      'permissionName': '供应商数据导入',
      'childPermissions': []
    },
      {
        'permissionId': '120205354234320',
        'permissionCode': 'workflowSetting',
        'functionAddress': '/baseSetting/workflowSetting',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '工作流配置',
        'childPermissions': []
      }, {
        'permissionId': '120205359934320',
        'permissionCode': 'quickSearchConfig',
        'functionAddress': '/baseSetting/quickSearchConfig',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '快速查询配置',
        'childPermissions': []
      }, {
        'permissionId': '120205359114320',
        'permissionCode': 'quickSearchDemo',
        'functionAddress': '/baseSetting/quickSearchDemo',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '快速查询展示',
        'childPermissions': []
      }
    ]
  },
  // 供应商管理模块
  {
    'permissionId': '111wew8790656',
    'permissionCode': 'vendorManagement',
    'functionAddress': null,
    'fdLangType': 'zh-CN',
    'iconPath': 'icongongyingshang1',
    'permissionName': '供应商管理',
    'childPermissions': [
      {
        'permissionId': '1116239520441815040',
        'permissionCode': 'quaOfReview',
        'functionAddress': '/vendorManagement/quaOfReview',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '资质审查',
        'childPermissions': []
      },
      {
        'permissionId': '1116239520441815040',
        'permissionCode': 'siteAssessment',
        'functionAddress': '/vendorManagement/siteAssessment',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '现场评审',
        'childPermissions': []
      },
      {
        'permissionId': '1116239520441815041',
        'permissionCode': 'vendorEffect',
        'functionAddress': '/vendorManagement/vendorEffect',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '供方生效',
        'childPermissions': []
      }, {
        'permissionId': '1116239520441815041',
        'permissionCode': 'sampleConfirmed',
        'functionAddress': '/vendorManagement/sampleConfirmed',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '样品确认',
        'childPermissions': []
      }, {
        'permissionId': '1116041',
        'permissionCode': 'materialTrial',
        'functionAddress': '/vendorManagement/materialTrial',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '物料试用',
        'childPermissions': []
      }, {
        'permissionId': '111623953041',
        'permissionCode': 'purchaseDirectory',
        'functionAddress': '/vendorManagement/purchaseDirectory',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '采购目录',
        'childPermissions': []
      }, {
        'permissionId': '11162393235041',
        'permissionCode': 'vendorOrgAndCatRel',
        'functionAddress': '/vendorManagement/vendorOrgAndCatRel',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '供应商组织与品类关系',
        'childPermissions': []
      }, {
        'permissionId': '110041',
        'permissionCode': 'vendorProfile',
        'functionAddress': '/vendorManagement/vendorProfile',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '供应商档案',
        'childPermissions': []
      }, {
        'permissionId': '11e41',
        'permissionCode': 'vendorGreenChannel',
        'functionAddress': '/vendorManagement/vendorGreenChannel',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '供应商绿色通道',
        'childPermissions': []
      }, {
        'permissionId': '1122e41',
        'permissionCode': 'vendorInfoChange',
        'functionAddress': '/vendorManagement/vendorInfoChange',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '供应商信息变更',
        'childPermissions': []
      }, {
        'permissionId': '1109-1',
        'permissionCode': 'vendorBankInfoChange',
        'functionAddress': '/vendorManagement/vendorBankInfoChange',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '财务银行信息变更',
        'childPermissions': []
      },
      {
        'permissionId': '11s-41',
        'permissionCode': 'cooperationEnded',
        'functionAddress': '/vendorManagement/cooperationEnded',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '合作终止',
        'childPermissions': []
      }

    ]
  },
  {
    'permissionId': '111-06',
    'permissionCode': 'sourceManagement',
    'functionAddress': null,
    'fdLangType': 'zh-CN',
    'iconPath': 'iconscc-sourcing',
    'permissionName': '寻源管理(采购方)',
    'childPermissions': [
      {
        'permissionId': '1116r40',
        'permissionCode': 'pollingSource',
        'functionAddress': '/sourceManagement/pollingSource',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '寻源需求',
        'childPermissions': []
      }
    ]
  },
  {
    'permissionId': '06--',
    'permissionCode': 'inquiryManagement',
    'functionAddress': null,
    'fdLangType': 'zh-CN',
    'iconPath': 'iconscc-financial',
    'permissionName': '询价管理(采购方)',
    'childPermissions': [
      {
        'permissionId': '1-',
        'permissionCode': 'inquiry',
        'functionAddress': '/inquiryManagement/inquiry',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '询比价',
        'childPermissions': []
      },
      {
        'permissionId': '1-',
        'permissionCode': 'priceApproval',
        'functionAddress': '/priceManagement/priceApproval',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '询价结果审批',
        'childPermissions': []
      },
      {
        'permissionId': '1--0',
        'permissionCode': 'priceCatalog',
        'functionAddress': '/inquiryManagement/priceCatalog',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '价格目录',
        'childPermissions': []
      },
      {
        'permissionId': '1-3-0',
        'permissionCode': 'inquiryBasicData',
        'functionAddress': '/inquiryManagement/inquiryBasicData',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '询价基础数据设置',
        'childPermissions': []
      }
    ]
  },
  {
    'permissionId': '1132-6',
    'permissionCode': 'biddingManagement',
    'functionAddress': null,
    'fdLangType': 'zh-CN',
    'iconPath': 'iconscc-sourcing',
    'permissionName': '招标管理(采购方)',
    'childPermissions': [
      {
        'permissionId': '140',
        'permissionCode': 'expertDatabase',
        'functionAddress': '/biddingManagement/expertDatabase',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '专家库',
        'childPermissions': []
      },
      {
        'permissionId': '-408',
        'permissionCode': 'technicalExchange',
        'functionAddress': '/biddingManagement/technicalExchange',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '技术交流',
        'childPermissions': []
      },
      {
        'permissionId': '-4067',
        'permissionCode': 'biddingFlowSetting',
        'functionAddress': '/biddingManagement/biddingFlowSetting',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '招标流程配置',
        'childPermissions': []
      }, {
        'permissionId': '-4077',
        'permissionCode': 'biddingProject',
        'functionAddress': '/biddingManagement/biddingProject',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '项目管理',
        'childPermissions': []
      },
      {
        'permissionId': '-450',
        'permissionCode': 'challengeClarification',
        'functionAddress': '/biddingManagement/challengeClarification',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '质疑/澄清',
        'childPermissions': []
      }

    ]
  },
  {
    'permissionId': '56',
    'permissionCode': 'vendorSourceSynergy',
    'functionAddress': null,
    'fdLangType': 'zh-CN',
    'iconPath': 'icongerenzhongxin',
    'permissionName': '寻源协同(供方)',
    'childPermissions': [
      {
        'permissionId': '110',
        'permissionCode': 'inquiryOrders',
        'functionAddress': '/vendorSourceSynergy/inquiryOrders',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '询价单',
        'childPermissions': []
      },
      {
        'permissionId': '1140',
        'permissionCode': 'materialSourceOrders',
        'functionAddress': '/vendorSourceSynergy/materialSourceOrders',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '物料寻源需求',
        'childPermissions': []
      },
      {
        'permissionId': '40',
        'permissionCode': 'categorySourceOrders',
        'functionAddress': '/vendorSourceSynergy/categorySourceOrders',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '品类寻源需求',
        'childPermissions': []
      },
      {
        'permissionId': '10',
        'permissionCode': 'quotationPrices',
        'functionAddress': '/vendorSourceSynergy/quotationPrices',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '报价单',
        'childPermissions': []
      },
      {
        'permissionId': '1033',
        'permissionCode': 'priceInquiry',
        'functionAddress': '/vendorSourceSynergy/priceInquiry',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '价格查询',
        'childPermissions': []
      },
      {
        'permissionId': '105',
        'permissionCode': 'vendorCategoryManagement',
        'functionAddress': '/vendorSourceSynergy/vendorCategoryManagement',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '供方品类管理',
        'childPermissions': []
      }
    ]
  },
  {
    'permissionId': '1106',
    'permissionCode': 'vendorPlanSynergy',
    'functionAddress': null,
    'fdLangType': 'zh-CN',
    'iconPath': 'icongerenzhongxin',
    'permissionName': '计划协同(供方)',
    'childPermissions': [
      {
        'permissionId': '14088',
        'permissionCode': 'marchRollingForecast',
        'functionAddress': '/vendorPlanSynergy/marchRollingForecast',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '三月滚动预测',
        'childPermissions': []
      },
      {
        'permissionId': '1s8',
        'permissionCode': 'planOrders',
        'functionAddress': '/vendorPlanSynergy/planOrders',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '计划订单',
        'childPermissions': []
      }
    ]
  },
  {
    'permissionId': '11w0631231',
    'permissionCode': 'buyerOrderSynergy',
    'functionAddress': null,
    'fdLangType': 'zh-CN',
    'iconPath': 'icongerenzhongxin',
    'permissionName': '订单协同(采购商)',
    'childPermissions': [
      {
        'permissionId': '14f8323',
        'permissionCode': 'buyerPurchaseOrder',
        'functionAddress': '/buyerOrderSynergy/buyerPurchaseOrder',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '采购订单',
        'childPermissions': []
      },
      {
        'permissionId': '1se832',
        'permissionCode': 'buyerDeliveryOrder',
        'functionAddress': '/buyerOrderSynergy/buyerDeliveryOrder',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '送货单',
        'childPermissions': []
      },
      {
        'permissionId': '1se873213',
        'permissionCode': 'deliveryAppointment',
        'functionAddress': '/buyerOrderSynergy/deliveryAppointment',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '送货预约',
        'childPermissions': []
      },
      {
        'permissionId': '1seh83213',
        'permissionCode': 'receivedGoodDetail',
        'functionAddress': '/buyerOrderSynergy/receivedGoodDetail',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '收货明细',
        'childPermissions': []
      },
      {
        'permissionId': '1sek83123',
        'permissionCode': 'returnedGoodsNotice',
        'functionAddress': '/buyerOrderSynergy/returnedGoodsNotice',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '退货通知',
        'childPermissions': []
      },
      {
        'permissionId': '1pk83244',
        'permissionCode': 'carInfoMaintenance',
        'functionAddress': '/buyerOrderSynergy/carInfoMaintenance',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '车辆信息维护',
        'childPermissions': []
      }
    ]
  },
  {
    'permissionId': '11w06',
    'permissionCode': 'vendorOrderSynergy',
    'functionAddress': null,
    'fdLangType': 'zh-CN',
    'iconPath': 'icongerenzhongxin',
    'permissionName': '订单协同(供方)',
    'childPermissions': [
      {
        'permissionId': '14f8',
        'permissionCode': 'vendorPurchaseOrder',
        'functionAddress': '/vendorOrderSynergy/vendorPurchaseOrder',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '采购订单',
        'childPermissions': []
      },
      {
        'permissionId': '1se8',
        'permissionCode': 'vendorDeliveryOrder',
        'functionAddress': '/vendorOrderSynergy/vendorDeliveryOrder',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '送货单',
        'childPermissions': []
      },
      {
        'permissionId': '1se87',
        'permissionCode': 'deliveryAppointments',
        'functionAddress': '/vendorOrderSynergy/deliveryAppointments',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '送货预约',
        'childPermissions': []
      },
      {
        'permissionId': '1seh8',
        'permissionCode': 'receivedGoodDetails',
        'functionAddress': '/vendorOrderSynergy/receivedGoodDetails',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '收货明细',
        'childPermissions': []
      },
      {
        'permissionId': '1sek8',
        'permissionCode': 'returnedGoodsNotices',
        'functionAddress': '/vendorOrderSynergy/returnedGoodsNotices',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '退货通知',
        'childPermissions': []
      },
      {
        'permissionId': '1pk8',
        'permissionCode': 'carInfoMaintenances',
        'functionAddress': '/vendorOrderSynergy/carInfoMaintenances',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '车辆信息维护',
        'childPermissions': []
      }
    ]
  },
  {
    'permissionId': '1oo',
    'permissionCode': 'vendorManagementSynergy',
    'functionAddress': null,
    'fdLangType': 'zh-CN',
    'iconPath': 'icongerenzhongxin',
    'permissionName': '管理协同(供方)',
    'childPermissions': [
      {
        'permissionId': '15y0l',
        'permissionCode': 'vendorSampleConfirmed',
        'functionAddress': '/vendorManagementSynergy/vendorSampleConfirmed',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '供方样品确认',
        'childPermissions': []
      },
      {
        'permissionId': '1uu0l',
        'permissionCode': 'vendorMaterialTrial',
        'functionAddress': '/vendorManagementSynergy/vendorMaterialTrial',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '供方物料试用',
        'childPermissions': []
      }
    ]
  },
  {
    'permissionId': '1oppbuyer',
    'permissionCode': 'buyerAccountCheckingSynergy',
    'functionAddress': null,
    'fdLangType': 'zh-CN',
    'iconPath': 'icongerenzhongxin',
    'permissionName': '对账协同(采购商)',
    'childPermissions': [
      {
        'permissionId': '13y0lbuyer1233213121',
        'permissionCode': 'buyerAccountsPayableUnbilled',
        'functionAddress': '/buyerAccountCheckingSynergy/buyerAccountsPayableUnbilled',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '应付款未开票明细',
        'childPermissions': []
      },
      {
        'permissionId': '13y0lbuyer1231',
        'permissionCode': 'buyerAccountsPayable',
        'functionAddress': '/buyerAccountCheckingSynergy/buyerAccountsPayable',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '应付款明细',
        'childPermissions': []
      },
      {
        'permissionId': '13y0lbuyer',
        'permissionCode': 'buyerNotSettlementAmount',
        'functionAddress': '/buyerAccountCheckingSynergy/buyerNotSettlementAmount',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '未结算数量对账',
        'childPermissions': []
      },
      {
        'permissionId': '1ublbuyer',
        'permissionCode': 'buyerSelfHelpBilling',
        'functionAddress': '/buyerAccountCheckingSynergy/buyerSelfHelpBilling',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '自助开票',
        'childPermissions': []
      },
      {
        'permissionId': '1ub44buyer',
        'permissionCode': 'buyerPaymentPlan',
        'functionAddress': '/buyerAccountCheckingSynergy/buyerPaymentPlan',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '付款计划',
        'childPermissions': []
      },
      {
        'permissionId': '1n4buyer',
        'permissionCode': 'buyerTicketOrders',
        'functionAddress': '/buyerAccountCheckingSynergy/buyerTicketOrders',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '罚扣款单',
        'childPermissions': []
      }
    ]
  },
  {
    'permissionId': '1opp',
    'permissionCode': 'vendorAccountCheckingSynergy',
    'functionAddress': null,
    'fdLangType': 'zh-CN',
    'iconPath': 'icongerenzhongxin',
    'permissionName': '对账协同(供方)',
    'childPermissions': [
      {
        'permissionId': '13y0232131',
        'permissionCode': 'vendorAccountsPayableUnbilled',
        'functionAddress': '/vendorAccountCheckingSynergy/vendorAccountsPayableUnbilled',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '应付款未开票明细',
        'childPermissions': []
      },
      {
        'permissionId': '13y02',
        'permissionCode': 'vendorAccountsPayable',
        'functionAddress': '/vendorAccountCheckingSynergy/vendorAccountsPayable',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '应付款明细',
        'childPermissions': []
      },
      {
        'permissionId': '13y02',
        'permissionCode': 'vendorStatementTracking',
        'functionAddress': '/vendorAccountCheckingSynergy/vendorStatementTracking',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '对账单跟踪',
        'childPermissions': []
      },
      {
        'permissionId': '13y0l',
        'permissionCode': 'notSettlementAmount',
        'functionAddress': '/vendorAccountCheckingSynergy/notSettlementAmount',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '未结算数量对账',
        'childPermissions': []
      },
      {
        'permissionId': '1ubl',
        'permissionCode': 'vendorSelfHelpBilling',
        'functionAddress': '/vendorAccountCheckingSynergy/vendorSelfHelpBilling',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '自助开票',
        'childPermissions': []
      },
      {
        'permissionId': '1ub44',
        'permissionCode': 'paymentPlan',
        'functionAddress': '/vendorAccountCheckingSynergy/buyerPaymentPlan',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '付款计划',
        'childPermissions': []
      },
      {
        'permissionId': '1n4',
        'permissionCode': 'ticketOrders',
        'functionAddress': '/vendorAccountCheckingSynergy/ticketOrders',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '罚扣款单',
        'childPermissions': []
      }
    ]
  },
  {
    'permissionId': '111-56',
    'permissionCode': 'vendorBiddingManagement',
    'functionAddress': null,
    'fdLangType': 'zh-CN',
    'iconPath': 'icongerenzhongxin',
    'permissionName': '招投标管理(供方)',
    'childPermissions': [
      {
        'permissionId': '1hh40',
        'permissionCode': 'vendorBiddingList',
        'functionAddress': '/vendorBiddingManagement/vendorBiddingList',
        'fdLangType': 'zh-CN',
        'iconPath': null,
        'permissionName': '招标项目列表',
        'childPermissions': []
      }
    ]
  }
]
