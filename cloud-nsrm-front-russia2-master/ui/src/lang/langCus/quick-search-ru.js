export default {
  scc_npm_inspect_vendor_history: {
    scc_npm_inspect_vendor_history_title: 'История согласования',
    inspectTime: 'Время согласования',
    inspectNum: 'Номер согласования',
    bidingName: 'Номер проекта',
    comprehensiveEvaluation: 'Заключение о всесторонней оценке'
  },
  scc_base_organization_dep: {
    scc_base_organization_dep_title: 'Департамент',
    organizationCode: 'Код депортамента',
    organizationName: 'Название департамента'
  },
  scc_base_organization_dept: {
    scc_base_organization_dept_title: 'Департамент',
    organizationCode: 'Код депортамента',
    organizationName: 'Название департамента'
  },
  scc_base_organization_sector: {
    scc_base_organization_sector_title: 'Сектор',
    organizationCode: 'Код сектора',
    organizationName: 'Название сектора'
  },
  scc_base_organization_ou: {
    scc_base_organization_ou_title: 'Организация',
    organizationName: 'Название организации',
    organizationCode: 'Код организации'
  },
  scc_pj_contract_seal: {
    scc_pj_contract_seal_title: 'Печать',
    sealName: 'Название печати',
    sealId: 'ID Печати'
  },
  ca_scc_npm_sou_bid_price: {
    ca_scc_npm_sou_bid_price_title: 'История предложенной цены',
    souName: 'Название источника',
    projectNo: 'Номер заявочного проекта',
    vendorName: 'Выйгравший поставщик',
    itemDesc: 'Название',
    specification: 'Спецификация/Модель',
    brand: 'Бренд',
    priceTax: 'Цена единицу товара с НДС (RUB)',
    fixedPriceTax: 'Фиксированная цена за единицу товара с учетом НДС (RUB)',
    priceNoTax: 'Цена единицу товара без учета НДС (RUB)',
    priceSumNoTax: 'Общая цена без НДС (RUB)'
  },
  // Invoice Management - SAP Cost Center
  npm_invoice_cost_center: {
    npm_invoice_cost_center_title: 'SAP МВЗ',
    jyorgnumber: 'Код балансовой единицы',                  
    jyorgname: 'Название балансовой единицы',
    costnumber: 'Код МВЗ',
    costname: 'Название МВЗ'
  },
  ceea_storage_return: {
    ceea_storage_return_title: 'Выбор поставщика',
    vendorCode: 'Код поставщика',
    vendorName: 'Название поставщика'
  },
  sou_ch_design_plan:{
    sou_ch_design_plan_title: 'Выбор проекта',
    projectCode: 'Код проекта',
    projectName: 'Название проекта',
    status: 'Статус'
  },
  // Project Planning Proposal
  scc_npm_sou_purinq_project_fixprice: {
    scc_npm_sou_purinq_project_fixprice_title: 'Предложение по планированию проекта',
    designProjectCode: 'Номер проекта',
    designProjectName: 'Название проекта',
    souNo: 'Номер формы запроса'
  },
   /* Add Supplier */
  scc_sup_company_info2: {
    scc_sup_company_info2_title: 'Добавить поставщика',
    companyId: 'ID',
    status: 'Статус документа (Статус: Проект, Отправлен на согласование, Согласовано)',
    companyCode: 'Код поставщика',
    erpVendorId: 'SAP ID Поставщика (added by Meiyun, ERP returns this erpVendorId after successful NSrm push)',
    erpVendorCode: 'SAP Код Поставщика (added by Meiyun, ERP returns this erpVendorCode after successful NSrm push)',
    companyName: 'Название компании',
    companyEnName: 'Название компании (Английский)',
    overseasRelation: 'Внешние отношения',
    overseasRelationName: 'Название внешних отношений',
    companyType: 'Тип компании',
    companyTypeName: 'Название типа компании',
    companyShortName: 'Аббревиатура компании',
    lcCode: 'Единый код социального кредита',
    businessLicenseFileId: 'Загрузка ID файла (унифицированная лицензия)',
    businessLicense: 'Загрузите название фотографии сертификата "три в одном"',
    idNumber: 'ID номер',
    dunsCode: 'DUNS код',
    companyStatus: 'Деловой статус',
    companyStatusName: 'Название делового статуса',
    legalPerson: 'Законный представитель',
    registeredCapital: 'Уставный капитал',
    companyCreationDate: 'Дата создания компании',
    businessStartDate: 'Дата начала дейтельности',
    businessEndDate: 'Дата окончания деятельности',
    companyRegisteredDate: 'Дата регистрации компании',
    companyCountry: 'Юридический адрес (страна/регион)',
    companyProvince: 'Юридический адрес (провинция/штат)',
    companyCity: 'Юридический адрес (город)',
    companyAddress: 'Фактический адресс',
    materialCategory: 'Доступные категории материалов',
    businessScope: 'Сфера деятельности',
    registrationAuthority: 'Регистрирующий орган',
    registCurrency: 'Валюта зарегистрированного капитала',
    registCurrencyName: 'Название валюты зарегистрированного капитала',
    isBacklist: 'В черном списке',
    vendorClassification:
      'Классификация поставщика (Dictionary Code: SUPPLIER_CLASSIFICATION, STRATEGIC_SUPPLIER: Strategic Supplier; PARTNER_SUPPLIERS: Partner Supplier)',
    ifLongPeriod: 'Является долгосрочным поставщиком (Y: Да, N: Нет, По умолчанию: Нет)',
    backlistUpdatedBy: 'Черный список обновлен',
    backlistUpdatedDate: 'Дата обновления черного списка',
    dataSources: '1. Зарегистрированный пользователь 2. Пользователь зеленого канала',
    blackListEffectiveDate: 'Дата действия черного списка',
    approvingDate: 'Дата согласования',
    approvalInfo: 'Информация о согласовании',
    applicationNumber: 'Номер заявки на регистрацию',
    applicationDate: 'Дата подачи заявки на регистрацию',
    approvedDate: 'Дата согласования/Дата доступа',
    approvedBy: 'Согласующий',
    approver: 'Аккаунт согласующего',
    statusName: 'Название статуса',
    ceeaCompanyWebsite: 'Сайт компании',
    ceeaAgentBrand: 'Бренд агента',
    ceeaListedTime: 'Время включения в список',
    ceeaIfListed: 'Находится в списке',
    ceeaPlantArea: 'Территория завода',
    ceeaPlantType: 'Тип завода',
    ceeaMainCategoryName: 'Название основной категории',
    ceeaMainCategoryCode: 'Код основной категории',
    ceeaMainCategoryId: 'ID основной категории',
    ceeaCompanyIntro: 'Введение компании',
    ceeaIndustryType: 'Тип промышленности',
    ceeaBusinessModel: 'Бизнес-модель',
    ceeaSupBusinessType: 'Тип бизнесса Поставщика',
    email: 'Значение',
    nickname: 'Псевдоним',
    createdId: 'ID Создателя',
    createdBy: 'Создатель',
    creationDate: 'Дата создания',
    createdByIp: 'IP Создателя',
    lastUpdatedId: 'ID последнего обновления',
    lastUpdatedBy: 'Последние обновление',
    lastUpdateDate: 'Дата последнего обновления',
    lastUpdatedByIp: 'IP последнего обновления',
    tenantId: 'ID Арендатора',
    version: 'Номер версии'
  },
  /* Qualification Review Category Selection */
  ceea_sup_auth_cate_journal_supplier: {
    ceea_sup_auth_cate_journal_supplier_title: 'Выбор категории',
    categoryName: 'Название категории',
    categoryCode: 'Код категории',
    categoryFullName: 'Полный путь категории'
  },
  // Bidding Materials Expert Database
  scc_npm_sou_expert: {
    scc_npm_sou_expert_title: 'Выбор эксперта',
    expertFullName: 'Название эксперта',
    email: 'Электронная почта',
    ceeajobcodedescr: 'Должность'
  },
  scc_npm_sou_expert_senior: {
    scc_npm_sou_expert_senior_title: 'Выбор эксперта',
    expertFullName: 'Название эксперта',
    email: 'Электронная почта',
    ceeajobcodedescr: 'Должность'
  },
  // Clarification and Inquiry
  sou_answer_projet: {
    sou_answer_projet_title: 'Выбор проекта',
    souName: 'Название проекта',
    extProjectNo: 'Код проекта',
    createdFullName: 'Имя создателя',
    creationDate: 'Дата создания'
  },
  // Clarification and Inquiry 2
  answer_vendor_search: {
    answer_vendor_search_title: 'Выбор поставщика',
    vendorCode: 'Код поставщика',
    vendorName: 'Название поставщика'
  },
  // Applicable Section
  plate_scc_base_organization: {
    plate_scc_base_organization_title: 'Выбор отдела',
    organizationId: 'ID Отдела',
    organizationCode: 'Код отдела',
    organizationName: 'Название отдела'
  },
  // Quick Project Plan Lookup
  scc_npm_pr_project_plan_query: {
    scc_npm_pr_project_plan_query_title: 'План проекта',
    projectName: 'Название проекта',
    planNo: 'Код проекта'
  },
  // Quick Material Purchase Lookup
  scc_npm_pr_material_query: {
    scc_npm_pr_material_query_title: 'Информация о материале',
    materialCode: 'Код материала',
    materialName: 'Название материала',
    specification: 'Спецификация модели',
    categoryFullName: 'Категория покупки'
  },
  // Inspection Management Supplier Inquiry
  scc_npm_inspect_vendor: {
    scc_npm_inspect_vendor_title: 'Информация о поставщике',
    vendorCode: 'Код поставщика',
    vendorName: 'Название поставщика'
  },
  // Inspection Management, Borrowing Management Tender Project Inquiry
  scc_npm_sou_project: {
    scc_npm_sou_project_title: 'Информация о тендерном проекте',
    projectId: 'ID торгов',
    souNo: 'Номер заявки на участие в торгах',
    souName: 'Название проекта',
    extProjectNo: 'Номер проекта',
    createdFullName: 'Создал'
  },
  // Bidding Management - Bidding Work Team
  ext_sou_project_group: {
    ext_sou_project_group_title: 'Команда рабочей группы по торгам',
    ceeaEmpNo: 'ID Сотрудника',
    nickname: 'Имя',
    phone: 'Телефон',
    email: 'Электронная почта',
    groupRole: 'Роль',
    extExpertLevel: 'Уровень эксперта',
    ceeaJobcodeDescr: 'Должность',
    projectId: 'ID участника торгов'
  },
  sou_npm_expert_user_display: {
    sou_npm_expert_user_display_title: 'Назначенный эксперт',
    username: 'ID Сотрудника',
    nickname: 'Имя',
    phone: 'Мобильный телефон',
    email: 'Электронная почта',
    department: 'Департамент',
    expertLevel: 'Уровень эксперта',
    ceeaJobcodeDescr: 'Должность',
    extOfficePhone: 'Телефон офиса'
  },
  // Tender Document Submission
  pr_requirement_head: {
    pr_requirement_head_title: 'Номер заявки',
    requirementHeadNum: 'Номер заявки',
    applyBy: 'Согласующий',
    creationDate: 'Дата создания',
    projectName: 'Название проекта',
    applyUserName: 'Согласующий'
  },
  scc_pj_source_pubconfig: {
    scc_pj_source_pubconfig_title: 'Шаблон уведомления для поиска поставщиков',
    pubconfigName: 'Название Шаблона',
    organizationCode: 'Код сектора',              
    organizationName: 'Название сектора',
    status: 'Статус документа',
    bankName: 'Название банка',
  },
  sou_req_head: {
    sou_req_head_title: 'Номер заявки',
    reqHeadNo: 'Номер заявки',
    reqUserName: 'Создал',
    projectName: 'Название проекта',
    creationDate: 'Дата создания',
    responsibilityUserName: 'Эксперт по поставщикам'
  },
  sou_recommvendor_bid: {
    sou_recommvendor_bid_title: 'Выбор поставщика',
    companyCode: 'Код поставщика',
    companyName: 'Название поставщика',
    sourceType: 'Источник'
  },
  sou_recommvendor_sou: {
    sou_recommvendor_sou_title: 'Выбор поставщика',
    companyCode: 'Код поставщика',
    companyName: 'Название поставщика',
    sourceType: 'Источник'
  },
  scc_pj_organization_role: {
    scc_pj_organization_role_title: 'Пожалуйста, выберите верхнюю роль процесса',
    roleName: 'Роль процесса',
    roleCode: 'Код процесса',
  },
  // Do Not Publicly Recommend Supplier
  REQQIRE_TO_RECOMM_WITHOUT_PUBLIC: {
    REQQIRE_TO_RECOMM_WITHOUT_PUBLIC_title: 'Не публичная рекомендация',
    projectName: 'Название проекта',
    applyByNickname: 'Инициатор',
    souNo: 'Номер тендера'
  },
  // Publicly Recommend Supplier
  REQQIRE_TO_RECOMM_PUBLIC: {
    REQQIRE_TO_RECOMM_PUBLIC_title: 'Публичная рекомендация',
    projectName: 'Название проекта',
    applyByNickname: 'Инициатор',
    souNo: 'Номер тендера',
    applyDate: 'Дата согласования',
    reqHeadNo: 'Номер требования к поставщику'
  },
  // Do Not Publicly Invite Supplier
  pr_requirement_head2: {
    pr_requirement_head2_title: 'Номер согласования',
    requirementHeadNum: 'Номер согласования',
    applyBy: 'Инициатор',
    creationDate: 'Дата создания',
    projectName: 'Наименование проекта',
    applyUserName: 'Ининциатор',
    responsibilityUserName: 'Эксперт по поставщикам'
  },
  // Pre-bid Communication Application Number Quick Lookup
  pr_requirement_head3: {
    pr_requirement_head3_title: 'Выбор согласования',
    requirementHeadNum: 'Номер согласования',
    creationDate: 'Дата создания',
    applyUserName: 'Инициатор',
    projectName: 'Название проекта'
  },
  REQQIRE_TO_SOU_PROJECT: {
    REQQIRE_TO_SOU_PROJECT_title: 'Создать тендер',
    projectName: 'Название проекта',
    sendSouProfileStatus: 'Статус тендера',
    applyByNickname: 'Ининциатор',
    applyDate: 'Дата согласования',
    souNo: 'Номер рекомендованного поставщика'
  },
  scc_npm_sou_fix_price_pass: {
    scc_npm_sou_fix_price_pass_title: 'Детали контракта',
    fixPriceNo: 'Номер ценового листа',
    orgOuName: 'Название компании',
    itemCode: 'Код материала',
    itemDesc: 'Название материала',
    unit: 'Единица измерения',
    quantity: 'Количество',
    vendorName: 'Название поставщика',
    notaxPrice: 'Цена за единицу без учета НДС',
    notaxTotalPrice: 'Общая стоимость без учета НДС'
  },
  scc_base_material_item_contract: {
    scc_base_material_item_contract_title: 'Всплывающее окно отображения материала',
    extProductFlag: 'Есть ли материал в каталоге товаров (Портал закупок)?',
    categoryFullName: 'Название полной категории',
    orderQuantityMinimum: 'Минимальное количесвто товара',
    brand: 'Бренд',
    minimumPackagingQuantity: 'Минимальное количество товара (внутри коробки)',
    purchaseCycle: 'Цикл покупки',
    outboxMaxPackagingQuantity: 'Максимальное количество товара (снаружи коробки)',
    minimumSafetyInventory: 'Минимальный страховой запас',
    materialCode: 'Код материала',
    materialName: 'Название материала',
    categoryName: 'Название категории',
    unitName: 'Единица измерения',
    extMaterialModel: 'Спецификация модели',
    materialType: 'Спецификация модели'
  },
  scc_base_organization_invoice: {
    scc_base_organization_invoice_title: 'Выбор компании',
    organizationCode: 'Код компании',
    organizationName: 'Название компании'
  },
  pre_bid_notice: {
    pre_bid_notice_title: 'Выбор способа комуникации',
    bidNoticeNo: 'Способ комуникации',
    projectName: 'Название проекта',
    requirementHeadNo: 'Номер согласования',
    creationDate: 'Дата создания',
    createdFullName: 'Создал'
  },
  sou_ch_ledger: {
    sou_ch_ledger_title: 'Выбор проекта',
    projectName: 'Название проекта'
  },
  scc_base_purchase_category5: {
    scc_base_purchase_category5_title: 'Запрос по категории',
    categoryCode: 'Код категории',
    categoryName: 'Название категории'
  },
  sou_purfix_price_contract: {
    sou_purfix_price_contract_title: 'Детали контракта',
    designProjectCode: 'Номер ценового листа',
    createUserOrgOuName: 'Название контаркта',
    vendorName: 'Название поставщика',
    itemCode: 'Код материала',
    itemDesc: 'Название материала',
    model: 'Спецификация модели',
    requireQuantity: 'Рекомендованное количество',
    notaxPrice: 'Цена за единицу без учета НДС',
    notaxTotalPrice: 'Общая стоимость без учета НДС'
  },
  scc_sup_company_info_display_tz: {
    scc_sup_company_info_display_tz_title: 'Информация о поставщике',
    companyCode: 'Код поставщика',
    companyName: 'Название поставщика',
    ceeacontactmethod: 'Мобильный телефон',
    contactname: 'Контактное лицо'
  },
  scc_pj_bpm_incorporated_company: {
    scc_pj_bpm_incorporated_company_title: 'Организация-заказчик',
    companyName: 'Название компании',
    creditCode: 'Единый код социального кредита'
  },
  scc_base_material_item2: {
    scc_base_material_item2_title: 'Материальный запрос',
    materialCode: 'Код материала',
    materialName: 'Название материала',
    description: 'Нанесен ли он на карту'
  }
}
