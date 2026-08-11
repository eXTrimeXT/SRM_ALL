<template>
  <el-container
    class="flex-container-notab the_quickSearchDemo_wrapper"
    direction="vertical"
  >
    <el-main style="padding: 0px;width: 100%">
      <el-collapse
        v-model="activeNames"
        class="tab-form-style"
      >
        <!-- 快速查询 -->
        <el-collapse-item
          :title="$t('common.quickSearch')"
          name="1"
        >
          <fieldset class="demo-section">
            <legend class="demo-section__title">
              <!-- 快速查询 -->
              {{ $t("common.quickSearch") }}
            </legend>
            <el-row :gutter="20">
              <el-col :span="6">
                <div class="the_QuickSearch_wrapper">
                  <!-- 物料查询 -->
                  <span>{{ $t("bidMod.searchMaterial") }}</span>
                  <QuickSearch
                    :show-input="preform.materialCode"
                    show-key="materialCode"
                    :scope-data="preform"
                    multi-select
                    name="scc_base_material_item"
                    @close-quicksearch="getSupplyObj2"
                  />
                </div>
              </el-col>
              <el-col :span="6">
                <div class="the_QuickSearch_wrapper">
                  <!-- 用户查询 -->
                  <span>{{ $t("bidMod.searchUser") }} scc_rbac_user_display</span>
                  <QuickSearch
                    :show-input="preform.username"
                    show-key="username"
                    :scope-data="preform"
                    name="scc_rbac_user_display"
                    @close-quicksearch="getUserObj"
                  />
                </div>
              </el-col>
              <el-col :span="6">
                <div class="the_QuickSearch_wrapper">
                  <!-- 用户查询 -->
                  <span>{{ $t("bidMod.searchUser") }} scc_rbac_user_company_display</span>
                  <QuickSearch
                    :show-input="preform.username"
                    show-key="username"
                    :scope-data="preform"
                    name="scc_rbac_user_company_display"
                    @close-quicksearch="getUserObj"
                  />
                </div>
              </el-col>
              <el-col :span="12">
                <div class="the_QuickSearch_wrapper">
                  <!-- scc_base_purchase_category -->
                  <span>
                    <!-- 供应商查询单选 scc_base_purchase_category -->
                    {{ $t("cusEntry.supplement20250211.supplierQuerySingleChoiceSccBasePurchaseCategory") }}
                  </span>
                  <QuickSearch
                    :show-input="preform.companyCode"
                    show-key="companyCode"
                    :scope-data="preform"
                    name="scc_base_purchase_category"
                    @close-quicksearch="getSupplyObj"
                  />
                </div>
              </el-col>
              <el-col :span="12">
                <div class="the_QuickSearch_wrapper">
                  <!-- 供应商查询多选 -->
                  <span>
                    <!-- 供应商查询多选-input形式 scc_sup_company_info_for_component -->
                    {{ $t("cusEntry.supplement20250211.supplierMultiSelectInputForm") }}
                  </span>
                  <QuickSearch
                    :show-input="preform.companyName"
                    show-key="companyName"
                    :scope-data="preform"
                    multiSelect
                    name="scc_sup_company_info_for_component"
                    @close-quicksearch="getSupplyObj"
                  />
                </div>
              </el-col>
              <el-col :span="12">
                <div class="the_QuickSearch_wrapper">
                  <!-- 供应商查询多选 -->
                  <span>
                    <!-- 供应商查询多选-按钮形式 scc_sup_company_info_for_component -->
                    {{ $t("cusEntry.supplement20250211.supplierMultiSelectButtonForm") }}
                  </span>
                  <div>
                    <!-- 选择供应商 -->
                    <QuickSearch
                      :show-input="preform.companyName"
                      :btnTitle="$t('bidMod.addVendor')"
                      show-key="companyName"
                      :scope-data="preform"
                      showButton
                      multiSelect
                      name="scc_sup_company_info_for_component"
                      @close-quicksearch="getSupplyObj"
                    />
                  </div>
                </div>
              </el-col>
            </el-row>
          </fieldset>
        </el-collapse-item>
        <!-- 组织架构 -->
        <el-collapse-item
          :title="$t('componentDoc.organization')"
          name="2"
        >
          <fieldset class="demo-section">
            <legend class="demo-section__title">
              <!-- 组织架构选择-单独使用 -->
              {{ $t("componentDoc.orgSelectList")[0] }}
            </legend>
            <el-row :gutter="40">
              <el-col :span="6">
                <span class="demo-form-label">
                  <!-- 树形单选： -->
                  {{ $t("componentDoc.treeRadio") }}
                </span>
                <!-- 请选择组织 -->
                <OrganizationSelectTree
                  :placeholder="$t('common.msgSelectOrg')"
                />
              </el-col>
              <el-col :span="6">
                <span class="demo-form-label">
                  <!-- 树形多选： -->
                  {{ $t("componentDoc.treeSelection") }}
                </span>
                <!-- 请选择组织 -->
                <OrganizationSelectTree
                  :placeholder="$t('common.msgSelectOrg')"
                  multiple
                />
              </el-col>
            </el-row>
            <el-row :gutter="40">
              <el-col :span="6">
                <span class="demo-form-label">
                  <!-- 全路径下拉单选： -->
                  {{ $t("componentDoc.fullPathSingleSel") }}
                </span>
                <OrgSelector
                  v-model="orgValue"
                  @change="orgValueChange"
                />
              </el-col>
              <el-col :span="6">
                <span class="demo-form-label">
                  <!-- 全路径下拉多选： -->
                  {{ $t("componentDoc.fullPathMultiSel") }}
                </span>
                <OrgSelector
                  v-model="m_orgValue"
                  multiple
                  @change="orgValueChange"
                />
              </el-col>
            </el-row>
          </fieldset>

          <fieldset class="demo-section">
            <legend class="demo-section__title">
              <!-- 组织架构选择-拆分使用-有权限控制（前端算法） -->
              {{ $t("componentDoc.orgSelectList")[1] }}
            </legend>
            <el-row
              type="flex"
              :gutter="40"
            >
              <el-col
                :span="6"
                class="demo-form-item"
              >
                <span class="demo-form-label">
                  <!-- 集团： -->
                  {{ $t("componentDoc.group") }}
                </span>
                <OrganizationSelector
                  v-model="organizationSelectorValue1"
                  :parent-id="-1"
                  node-type="GROUP"
                  :placeholder="$t('common.pleaseSelect')"
                  @select="selectHandler1"
                />
              </el-col>
              <el-col
                :span="6"
                class="demo-form-item"
              >
                <span class="demo-form-label">
                  <!-- 采购组织： -->
                  {{ $t("componentDoc.purchaseOrg") }}
                </span>
                <OrganizationSelector
                  ref="organizationSelector2"
                  v-model="organizationSelectorValue2"
                  :parent-id="organizationSelectorValue1"
                  node-type="ORG"
                  :placeholder="$t('common.pleaseSelect')"
                  @select="selectHandler2"
                />
              </el-col>
              <el-col
                :span="6"
                class="demo-form-item"
              >
                <span class="demo-form-label">
                  <!-- 工厂： -->
                  {{ $t("componentDoc.factory") }}
                </span>
                <OrganizationSelector
                  ref="organizationSelector3"
                  v-model="organizationSelectorValue3"
                  :parent-id="organizationSelectorValue2"
                  node-type="OU"
                  :placeholder="$t('common.pleaseSelect')"
                  @select="selectHandler3"
                />
              </el-col>
            </el-row>
          </fieldset>
          <fieldset class="demo-section">
            <legend class="demo-section__title">
              <!-- 组织架构选择-拆分使用-无权限控制（前端算法） -->
              {{ $t("componentDoc.orgSelectList")[2] }}
            </legend>
            <el-row
              type="flex"
              :gutter="40"
            >
              <el-col
                :span="6"
                class="demo-form-item"
              >
                <span class="demo-form-label">
                  <!-- 集团： -->
                  {{ $t("componentDoc.group") }}
                </span>
                <OrganizationSelector
                  v-model="organizationSelectorValue11"
                  :parent-id="-1"
                  :limit="false"
                  node-type="GROUP"
                  :placeholder="$t('common.pleaseSelect')"
                  @select="selectHandler11"
                />
              </el-col>
              <el-col
                :span="6"
                class="demo-form-item"
              >
                <span class="demo-form-label">
                  <!-- 采购组织： -->
                  {{ $t("componentDoc.purchaseOrg") }}
                </span>
                <OrganizationSelector
                  ref="organizationSelector22"
                  v-model="organizationSelectorValue22"
                  :limit="false"
                  :placeholder="$t('common.pleaseSelect')"
                  :parent-id="organizationSelectorValue11"
                  node-type="ORG"
                  @select="selectHandler22"
                />
              </el-col>
              <el-col
                :span="6"
                class="demo-form-item"
              >
                <span class="demo-form-label">
                  <!-- 工厂： -->
                  {{ $t("componentDoc.factory") }}
                </span>
                <OrganizationSelector
                  ref="organizationSelector33"
                  v-model="organizationSelectorValue33"
                  :limit="false"
                  :parent-id="organizationSelectorValue22"
                  node-type="OU"
                  :placeholder="$t('common.pleaseSelect')"
                  @select="selectHandler33"
                />
              </el-col>
            </el-row>
          </fieldset>
          <fieldset class="demo-section">
            <legend class="demo-section__title">
              <!-- 组织架构选择-拆分使用-多选示例 -->
              {{ $t("componentDoc.orgSelectList")[3] }}
            </legend>
            <el-row
              type="flex"
              :gutter="40"
            >
              <el-col
                :span="6"
                class="demo-form-item"
              >
                <span class="demo-form-label">
                  <!-- 集团： -->
                  {{ $t("componentDoc.group") }}
                </span>
                <OrganizationSelector
                  v-model="m_organizationSelectorValue11"
                  :limit="false"
                  :parent-id="-1"
                  multiple
                  node-type="GROUP"
                  :placeholder="$t('common.pleaseSelect')"
                  @select="m_selectHandler11"
                />
              </el-col>
              <el-col
                :span="6"
                class="demo-form-item"
              >
                <span class="demo-form-label">
                  <!-- 采购组织： -->
                  {{ $t("componentDoc.purchaseOrg") }}
                </span>
                <OrganizationSelector
                  ref="m_organizationSelector22"
                  v-model="m_organizationSelectorValue22"
                  multiple
                  :limit="false"
                  :placeholder="$t('common.pleaseSelect')"
                  :parent-id="
                    m_organizationSelectorValue11
                      ? m_organizationSelectorValue11[0]
                      : null
                  "
                  node-type="ORG"
                  @select="m_selectHandler22"
                />
              </el-col>
              <el-col
                :span="6"
                class="demo-form-item"
              >
                <span class="demo-form-label">
                  <!-- 工厂： -->
                  {{ $t("componentDoc.factory") }}
                </span>
                <OrganizationSelector
                  ref="m_organizationSelector33"
                  v-model="m_organizationSelectorValue33"
                  multiple
                  :limit="false"
                  :placeholder="$t('common.pleaseSelect')"
                  :parent-id="
                    m_organizationSelectorValue22
                      ? m_organizationSelectorValue22[0]
                      : null
                  "
                  node-type="OU"
                  @select="m_selectHandler33"
                />
              </el-col>
            </el-row>
          </fieldset>

          <fieldset class="demo-section">
            <legend class="demo-section__title">
              <!-- 组织架构选择-拆分使用-有权限控制（使用后端接口） -->
              {{ $t("componentDoc.orgSelectList")[4] }}
            </legend>
            <el-row
              type="flex"
              :gutter="40"
            >
              <el-col
                :span="6"
                class="demo-form-item"
              >
                <span class="demo-form-label">
                  <!-- 集团： -->
                  {{ $t("componentDoc.group") }}
                </span>
                <OrganizationSelector
                  v-model="d_organizationSelectorValue1"
                  :parent-id="-1"
                  :placeholder="$t('common.pleaseSelect')"
                  remote
                  node-type="GROUP"
                  @select="d_selectHandler1"
                />
              </el-col>
              <el-col
                :span="6"
                class="demo-form-item"
              >
                <span class="demo-form-label">
                  <!-- 采购组织： -->
                  {{ $t("componentDoc.purchaseOrg") }}
                </span>
                <OrganizationSelector
                  ref="d_organizationSelector2"
                  v-model="d_organizationSelectorValue2"
                  :parent-id="d_organizationSelectorValue1"
                  node-type="ORG"
                  :placeholder="$t('common.pleaseSelect')"
                  remote
                  @select="d_selectHandler2"
                />
              </el-col>
              <el-col
                :span="6"
                class="demo-form-item"
              >
                <span class="demo-form-label">
                  <!-- 工厂： -->
                  {{ $t("componentDoc.factory") }}
                </span>
                <OrganizationSelector
                  ref="d_organizationSelector3"
                  v-model="d_organizationSelectorValue3"
                  :parent-id="d_organizationSelectorValue2"
                  node-type="OU"
                  :placeholder="$t('common.pleaseSelect')"
                  remote
                  @select="d_selectHandler3"
                />
              </el-col>
            </el-row>
          </fieldset>
          <fieldset class="demo-section">
            <legend class="demo-section__title">
              <!-- 组织架构选择-拆分使用-无权限控制（使用后端接口） -->
              {{ $t("componentDoc.orgSelectList")[5] }}
            </legend>
            <el-row
              type="flex"
              :gutter="40"
            >
              <el-col
                :span="6"
                class="demo-form-item"
              >
                <span class="demo-form-label">
                  <!-- 集团： -->
                  {{ $t("componentDoc.group") }}
                </span>
                <OrganizationSelector
                  v-model="d_organizationSelectorValue11"
                  :limit="false"
                  :parent-id="-1"
                  :placeholder="$t('common.pleaseSelect')"
                  remote
                  node-type="GROUP"
                  @select="d_selectHandler11"
                />
              </el-col>
              <el-col
                :span="6"
                class="demo-form-item"
              >
                <span class="demo-form-label">
                  <!-- 采购组织： -->
                  {{ $t("componentDoc.purchaseOrg") }}
                </span>
                <OrganizationSelector
                  ref="d_organizationSelector22"
                  v-model="d_organizationSelectorValue22"
                  :limit="false"
                  :placeholder="$t('common.pleaseSelect')"
                  :parent-id="d_organizationSelectorValue11"
                  node-type="ORG"
                  remote
                  @select="d_selectHandler22"
                />
              </el-col>
              <el-col
                :span="6"
                class="demo-form-item"
              >
                <span class="demo-form-label">
                  <!-- 工厂： -->
                  {{ $t("componentDoc.factory") }}
                </span>
                <OrganizationSelector
                  ref="d_organizationSelector33"
                  v-model="d_organizationSelectorValue33"
                  :limit="false"
                  :placeholder="$t('common.pleaseSelect')"
                  :parent-id="d_organizationSelectorValue22"
                  node-type="OU"
                  remote
                  @select="d_selectHandler33"
                />
              </el-col>
            </el-row>
          </fieldset>
        </el-collapse-item>
        <el-collapse-item
          :title="$t('common.category')"
          name="3"
        >
          <fieldset class="demo-section">
            <legend
              class="demo-section__title"
              v-html="$t('componentDoc.cateSelectTitle')"
            >
              <!-- 品类选择 <br />dataSource 默认 auto
              （组件调用可不用配置该属性）通过读取菜单配置里面“启用品类分工”的值判断读取层级设置的数据或者品类分工的数据
              <br />dataSource 传 catLevel 固定根据层级设置的所有数据
              <br />dataSource 传 catDivision 固定查询 品类分工的数据 -->
            </legend>
            <el-row>
              <el-col
                :span="8"
                class="demo-form-item"
              >
                <span class="category-form-label">
                  <!-- 按层级设置选择品类: -->
                  {{ $t("componentDoc.selCateByLevel") }}
                </span>
                <CCategorySelect
                  v-model="sampleInfoForm.category1"
                  :scope="sampleInfoForm"
                  :placeholder="$t('vendorMod.msgCategoryNormalizer')"
                  data-source="catLevel"
                />
              </el-col>
              <el-col
                :span="8"
                class="demo-form-item"
              >
                <span class="demo-form-label">
                  <!-- 按品类分工选择: -->
                  {{ $t("componentDoc.chooseByCate") }}
                </span>
                <CCategorySelect
                  v-model="sampleInfoForm.category2"
                  :scope="sampleInfoForm"
                  :placeholder="$t('vendorMod.msgCategoryNormalizer')"
                  data-source="catDivision"
                />
              </el-col>
            </el-row>
          </fieldset>
        </el-collapse-item>
        <!-- 人员选择 -->
        <el-collapse-item
          :title="$t('componentDoc.personnelSel')"
          name="4"
        >
          <fieldset class="demo-section">
            <legend
              class="demo-section__title"
              v-html="$t('componentDoc.personnelTitle')"
            >
              <!-- <br />userType 默认为查询采购商BUYER 查供应商设置
              userType="VENDOR" <br />multiSelect 默认多选 单选设置
              :multiSelect="false" <br />绩效考核-选择考核责任人
              -选择采购商/performanceManagement/performanceAssessment
              <br />公告管理-发布公告 -选择供应商 /userManage/announcements -->
            </legend>
            <el-row>
              <el-col
                :span="6"
                class="demo-form-item purchaser"
              >
                <span class="demo-form-label">
                  <!-- 选择采购商: -->
                  {{ $t("componentDoc.selBuyer") }}
                </span>
                <!-- 请选择采购商 -->
                <el-input
                  v-model="userBuyer"
                  :placeholder="$t('componentDoc.msgSelBuyer')"
                >
                  <el-button
                    slot="append"
                    icon="el-icon-search"
                    @click="selectPurchasers"
                  />
                </el-input>
              </el-col>
              <el-col
                :span="6"
                class="demo-form-item"
              >
                <span class="demo-form-label">
                  <!-- 选择供应商: -->
                  {{ $t("componentDoc.selVendor") }}
                </span>
                <!-- 请选择供应商 -->
                <el-input
                  v-model="userVendor"
                  :placeholder="$t('componentDoc.msgSelBuyer')"
                >
                  <el-button
                    slot="append"
                    icon="el-icon-search"
                    @click="selectSupplier"
                  />
                </el-input>
              </el-col>
            </el-row>
          </fieldset>
        </el-collapse-item>
        <!-- 根据层级查询物料示例 -->
        <el-collapse-item
          :title="$t('componentDoc.materialExample')"
          name="5"
        >
          <fieldset class="demo-section">
            <legend class="demo-section__title">
              <!-- 根据层级查询物料示例 -->
              {{ $t("componentDoc.materialExample") }}
            </legend>
            <p>
              <!-- 选择层级【大类】【中类】【小类】，再输入关键词，查询属于对应层级的并且模糊匹配层级名称的物料 -->
              {{ $t("componentDoc.exampleContent")[0] }}
            </p>
            <p>
              <span>
                <!-- 采购分类接口文档地址： -->
                {{ $t("componentDoc.exampleContent")[1] }} </span><b style="text-decoration: underline;color: #23adf4;">
                <!-- /showdoc/web/#/7?page_id=1732 -->
                {{ $t("componentDoc.linkList")[0] }}
              </b>
            </p>
            <p>
              <span>
                <!-- 物料查询接口文档地址： -->
                {{ $t("componentDoc.exampleContent")[2] }} </span><b style="text-decoration: underline;color: #23adf4;">
                <!-- /showdoc/web/#/7?page_id=1753 -->
                {{ $t("componentDoc.linkList")[1] }}
              </b>
            </p>
            <el-row
              type="flex"
              style="margin-bottom: 12px;"
              :gutter="45"
            >
              <el-col :span="6">
                <!-- 请输入内容 -->
                <el-input
                  v-model="input3"
                  :placeholder="$t('common.pleaseTypeContents')"
                  class="input-with-select"
                  clearable
                >
                  <!-- 请选择 -->
                  <el-select
                    slot="prepend"
                    v-model="select"
                    :placeholder="$t('common.pleaseSelect')"
                    class="prepend-select"
                    clearable
                  >
                    <!-- 大类 -->
                    <el-option
                      :id="1"
                      :label="$t('componentDoc.bigCategory')"
                      value="1"
                    />
                    <!-- 中类 -->
                    <el-option
                      :id="2"
                      :label="$t('componentDoc.midCategory')"
                      value="2"
                    />
                    <!-- 小类 -->
                    <el-option
                      :id="3"
                      :label="$t('componentDoc.smallCategory')"
                      value="3"
                    />
                  </el-select>
                </el-input>
              </el-col>
              <el-col :span="6">
                <el-button
                  type="primary"
                  @click="queryTableData"
                >
                  <!-- 查询 -->
                  {{ $t("common.search") }}
                </el-button>
              </el-col>
            </el-row>
            <BaseTable
              style="max-height: 300px;overflow: auto;"
              stripe
              :data="categoryTableData"
              :columns="columns"
              border
            />
          </fieldset>
        </el-collapse-item>
        <!-- 列表导入导出示例 -->
        <el-collapse-item
          :title="$t('componentDoc.impExpExample')"
          name="6"
        >
          <fieldset class="demo-section">
            <legend class="demo-section__title">
              <!-- 列表导入导出示例 -->
              {{ $t("componentDoc.impExpExample") }}
            </legend>
            <p>
              <!-- 导入先下载模板：【选择开始结束日期，或者动态的字段列，查询包含对应动态的字段的模板】；在根据模板填写数据进行导入。 -->
              {{ $t("componentDoc.impExpContent") }}
            </p>
            <el-row
              type="flex"
              style="margin-bottom: 12px;"
              :gutter="45"
            >
              <el-col :span="12">
                <div style="padding: 0 11px;float: left;">
                  <MImport
                    ref="import"
                    :title="iModal.title"
                    :extra-data="iModal.extraData"
                    :up-load-url="iModal.upLoadUrl"
                    :show-success-deal="true"
                    @downloadTemplate="openDialogVisible('template')"
                    @handleSuccess="uploadSuccess"
                  />
                </div>
                <el-button
                  type="primary"
                  @click="openDialogVisible('export')"
                >
                  <!-- 导出 -->
                  {{ $t("common.export") }}
                </el-button>
                <el-button
                  type="primary"
                  @click="openDialogVisible('queryData')"
                >
                  <!-- 查询 -->
                  {{ $t("common.search") }}
                </el-button>
              </el-col>
            </el-row>
            <div class="the_table_wrapper">
              <table class="the_list_table">
                <tr
                  v-for="(item, key) in categoryTableData2"
                  :key="key"
                >
                  <td
                    v-for="(val, key2) in item"
                    :key="key2"
                  >
                    <div :title="val">
                      {{ val }}
                    </div>
                  </td>
                </tr>
              </table>
            </div>
            <!--<base-table
              style="max-height: 300px;overflow: auto;"
              stripe
              :data="categoryTableData2"
              :columns="columns2"
              border
            ></base-table>-->
            <!--模版下载弹框-->
            <!-- 筛选过滤条件 -->
            <srm-dialog
              :title="$t('components.importOrExportDialog.filterConditions')"
              size="middle"
              class="the_follow_tender_dialog"
              :visible.sync="dialogVisible"
              :close-on-click-modal="false"
            >
              <el-row>
                <el-col :span="6">
                  <span>
                    <!-- 开始时间 -->
                    {{ $t("componentDoc.stratTime") }}
                  </span>
                  <el-date-picker
                    v-model="paramForm.startDate"
                    type="date"
                    :format="$formatDatePicker"
                    value-format="yyyy-MM-dd"
                  />
                </el-col>
                <el-col :span="6">
                  <p />
                </el-col>
                <el-col :span="6">
                  <span>
                    <!-- 结束时间 -->
                    {{ $t("componentDoc.endTime") }}
                  </span>
                  <el-date-picker
                    v-model="paramForm.endDate"
                    type="date"
                    :format="$formatDatePicker"
                    value-format="yyyy-MM-dd"
                  />
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="24">
                  <span>
                    <!-- 请选择字段 -->
                    {{ $t("componentDoc.msgSelField") }}
                  </span>
                  <el-select
                    v-model="paramForm.dicParams"
                    multiple
                    style="width:420px"
                  >
                    <el-option
                      v-for="item in dicParamsList"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="6">
                  <p />
                </el-col>
                <el-col :span="12">
                  <el-button
                    @click="dialogVisible = false"
                  >
                    {{
                      $t("common.cancel")
                    }}
                  </el-button>
                  <el-button
                    type="primary"
                    @click="downloadTemplate"
                  >
                    {{ $t("common.confirm") }}
                  </el-button>
                </el-col>
              </el-row>
            </srm-dialog>
          </fieldset>
        </el-collapse-item>
      </el-collapse>
      <CPeopleSelector
        ref="peopleSelector"
        :visible.sync="showPurchasers"
        user-type="BUYER"
        @on-confirm="getPeopleBuyer"
      />
      <CPeopleSelector
        ref="peopleSelector"
        :visible.sync="showSupplier"
        :multi-select="false"
        user-type="VENDOR"
        @on-confirm="getPeople"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import QuickSearch from 'lib@/components/QuickSearch'
import { adaptDictData, parseTime } from '@/utils'
import OrganizationSelector from 'lib@/components/organization-selector'
import OrganizationSelectTree from 'lib@/components/organization-cascader'
import OrgSelector from 'lib@/components/org-selector'
import CCategorySelect from 'lib@/components/c-category-select'
import CPeopleSelector from '@/library/components/c-people-selector'
import BaseTable from 'lib@/components/BaseTable'
import MImport from 'lib@/components/import'
import { downloadFileLinkByPost } from 'lib@/utils/file'
import { getDictItem } from '@/api/common'

export default {
  name: 'QuickSearchDemo',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    QuickSearch,
    OrganizationSelector,
    OrganizationSelectTree,
    OrgSelector,
    CCategorySelect,
    BaseTable,
    CPeopleSelector,
    MImport
  },
  data () {
    return {
      pageSize: 15,
      orgValue: null,
      m_orgValue: null,
      gridId: 'list',
      selectList: [],
      currentRow: null,
      showPurchasers: false,
      showSupplier: false,
      input3: null,
      select: null,
      iModal: {
        title: this.$t('common.import'), // 导入
        extraData: {
          fileModular: 'sup', // 文件所属模块 -》基础模块
          fileFunction: 'biddingProject', // 文件所属功能
          fileType: 'excel'
        },
        upLoadUrl: '/api-base/import-head/importExcel'
      },
      operationType: null,
      dialogVisible: false,
      paramForm: {
        startDate: null,
        endDate: null,
        dicParams: []
      },
      dicParamsList: [],
      m_organizationSelectorValue11: [],
      m_organizationSelectorValue22: [],
      m_organizationSelectorValue33: [],
      organizationSelectorValue1: null,
      organizationSelectorValue2: null,
      organizationSelectorValue3: null,
      organizationSelectorValue11: null,
      organizationSelectorValue22: null,
      organizationSelectorValue33: null,
      d_organizationSelectorValue1: null,
      d_organizationSelectorValue2: null,
      d_organizationSelectorValue3: null,
      d_organizationSelectorValue11: null,
      d_organizationSelectorValue22: null,
      d_organizationSelectorValue33: null,
      tableHeader: [],
      categoryTableData2: [],
      columns2: [
        {
          attrs: {
            minWidth: '100',
            align: 'center',
            prop: 'fixedOne',
            showOverflowTooltip: true,
            label: 'fixedOne'
          }
        },
        {
          attrs: {
            minWidth: '100',
            align: 'center',
            prop: 'fixedTwo',
            showOverflowTooltip: true,
            label: 'fixedTwo'
          }
        },
        {
          attrs: {
            minWidth: '100',
            align: 'center',
            prop: 'fixedThree',
            showOverflowTooltip: true,
            label: 'fixedThree'
          }
        },
        {
          attrs: {
            minWidth: '100',
            align: 'center',
            prop: 'fixedFour',
            showOverflowTooltip: true,
            label: 'fixedFour'
          }
        },
        {
          attrs: {
            minWidth: '100',
            align: 'center',
            prop: 'fixedFive',
            showOverflowTooltip: true,
            label: 'fixedFive'
          }
        }
      ],
      categoryTableData: [],
      columns: [
        {
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'categoryFullName',
            showOverflowTooltip: true,
            label: () => this.$t('componentDoc.categoryFullName') // 采购分类全称
          }
        },
        {
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'categoryName',
            showOverflowTooltip: true,
            label: () => this.$t('componentDoc.categoryName') // 采购分类名称
          }
        },
        {
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'materialName',
            showOverflowTooltip: true,
            label: () => this.$t('common.materialName') // 物料名称
          }
        },
        {
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'materialCode',
            label: () => this.$t('common.materialCode') // 物料编码
          }
        },
        {
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'unitName',
            label: () => this.$t('dataConfMod.unit') // 单位
          }
        }
      ],
      tableData: [],
      langList: [],
      activeNames: ['1', '2', '3', '4', '5', '6'],
      form: {
        dictId: '',
        dictCode: '',
        dictName: '',
        description: '',
        language: '',
        activeDate: '',
        inactiveDate: '',
        dictRole: ''
      },
      rules: {
        dictCode: [{ required: true, message: this.$t('bidMod.msgDictCode') }], // "请输入字典编码"
        dictName: [{ required: true, message: this.$t('bidMod.msgDictName') }], // "请输入字典名称"
        language: [
          { required: true, message: this.$t('bidMod.msgDictlanguage') }
        ], // "请输入字典语言"
        activeDate: [
          { required: true, message: this.$t('bidMod.msgActiveDate') }
        ] // "请输生效日期"
      },
      dialogFormVisible: false,
      formLabelWidth: '100px',
      isActive: false,
      preform: {
        dictCode: '',
        materialCode: '',
        companyCode: '',
        username: ''
      },
      sampleInfoForm: {
        category1: '',
        category2: ''
      },
      preArr: [
        {
          prop: 'purOrg',
          label: () => this.$t('common.orgName'),
          type: 'select',
          options: []
        }, // "采购组织"
        {
          prop: 'materialCode',
          label: () => this.$t('common.materialCode'), // "物料编码"
          type: 'select',
          options: []
        },
        {
          prop: 'status',
          label: () => this.$t('common.status'),
          type: 'select',
          options: []
        }, // "状态"
        { prop: 'vendorCode', label: () => this.$t('common.vendorCode') }, // "供应商编码"
        {
          prop: 'category',
          label: () => this.$t('common.category'),
          type: 'select',
          options: []
        }, // "品类"
        { prop: 'vendorCompanyName', label: () => this.$t('common.vendorName') } // "供应商名称"
      ],
      userBuyer: '',
      userVendor: ''
    }
  },
  created () {
    // 状态
    getDictItem('CUSTOM_EXPORT_FIELD').then(res => {
      this.dicParamsList = adaptDictData(res.data, 'dict')
    })
  },
  methods: {
    orgValueChange (value, node) {
      console.log('orgValue change: ', value, node)
      console.log('orgValue: ', this.orgValue)
    },
    openDialogVisible (type) {
      // 下载模板
      this.operationType = type
      this.dialogVisible = true
    },
    downloadTemplate () {
      // 下载模板
      if (
        (!this.paramForm.startDate && this.paramForm.endDate) ||
        (this.paramForm.startDate && !this.paramForm.endDate)
      ) {
        this.$message.error(this.$t('componentDoc.msgSelCompleteTime')) // 请选择完整的时间区间或全不选
        return
      }
      if (
        !this.paramForm.startDate &&
        !this.paramForm.endDate &&
        this.paramForm.dicParams.length === 0
      ) {
        this.$message.error(this.$t('componentDoc.msgSelTimeOrField')) // 请选择时间区间或字段
        return
      }
      if (this.operationType === 'template') {
        // 下载模板
        // _自定义模板.xlsx
        downloadFileLinkByPost(
          '/api-base/import-head/importModelDownload',
          new Date().getTime() + this.$t('componentDoc.customTemplate'),
          this.paramForm
        ).catch(err => {
          this.$message.error(err.message)
        })
      } else if (this.operationType === 'export') {
        // 导出
        // _自定义导出.xlsx
        downloadFileLinkByPost(
          '/api-base/import-head/exportExcel',
          new Date().getTime() + this.$t('componentDoc.customExport'),
          this.paramForm
        ).catch(err => {
          this.$message.error(err.message)
        })
      } else if (this.operationType === 'queryData') {
        // 查询
        this.queryAllData()
      }
      this.dialogVisible = false
    },
    uploadSuccess (val) {
      this.queryAllData()
    },
    queryAllData () {
      let params = Object.assign({ pageSize: 15, pageNum: 1 }, this.paramForm)
      this.$http({
        url: '/api-base/import-head/listPage',
        method: 'POST',
        data: params,
        laoding: true
      }).then(r => {
        this.categoryTableData2 = r.data.list
      })
    },
    queryTableData () {
      const params = {
        level: this.select,
        name: this.input3
      }
      this.$http({
        url:
          '/api-base/purchase/purchaseCategory/queryCategoryByType',
        method: 'GET',
        params: { ...params, enabled: 'Y' }
      }).then(res => {
        this.$http({
          url:
            '/api-base/material/materialItem/listMaterialByPurchaseCategory',
          method: 'POST',
          data: res.data,
          laoding: true
        }).then(r => {
          this.categoryTableData = r.data
        })
      })
    },
    m_selectHandler11 (node, value, scope) {
      console.log(node, value, scope)
      this.m_organizationSelectorValue22 = null
      this.m_organizationSelectorValue33 = null
      this.$refs.m_organizationSelector22.clearOptions()
      this.$refs.m_organizationSelector33.clearOptions()
    },
    m_selectHandler22 (node, value, scope) {
      console.log(node, value, scope)
      this.m_organizationSelectorValue33 = null
      this.$refs.m_organizationSelector33.clearOptions()
    },
    m_selectHandler33 () {},
    selectHandler1 (node, value, scope) {
      console.log(node, value, scope)
      this.organizationSelectorValue2 = null
      this.organizationSelectorValue3 = null
      this.$refs.organizationSelector2.clearOptions()
      this.$refs.organizationSelector3.clearOptions()
    },
    selectHandler2 (node, value, scope) {
      console.log(node, value, scope)
      this.organizationSelectorValue3 = null
      this.$refs.organizationSelector3.clearOptions()
    },
    selectHandler3 () {},
    selectHandler11 (node, value, scope) {
      console.log(node, value, scope)
      this.organizationSelectorValue22 = null
      this.organizationSelectorValue33 = null
      this.$refs.organizationSelector22.clearOptions()
      this.$refs.organizationSelector33.clearOptions()
    },
    selectHandler22 (node, value, scope) {
      console.log(node, value, scope)
      this.organizationSelectorValue33 = null
      this.$refs.organizationSelector33.clearOptions()
    },
    selectHandler33 () {},
    d_selectHandler1 (node, value, scope) {
      console.log(node, value, scope)
      this.d_organizationSelectorValue2 = null
      this.d_organizationSelectorValue3 = null
      this.$refs.d_organizationSelector2.clearOptions()
      this.$refs.d_organizationSelector3.clearOptions()
    },
    d_selectHandler2 (node, value, scope) {
      console.log(node, value, scope)
      this.d_organizationSelectorValue3 = null
      this.$refs.d_organizationSelector3.clearOptions()
    },
    d_selectHandler3 () {},
    d_selectHandler11 (node, value, scope) {
      console.log(node, value, scope)
      this.d_organizationSelectorValue22 = null
      this.d_organizationSelectorValue33 = null
      this.$refs.d_organizationSelector22.clearOptions()
      this.$refs.d_organizationSelector33.clearOptions()
    },
    d_selectHandler22 (node, value, scope) {
      this.d_organizationSelectorValue33 = null
      console.log(node, value, scope)
      this.$refs.d_organizationSelector33.clearOptions()
    },
    d_selectHandler33 () {},
    getSupplyObj (v) {
      this.companyCode = v ? v.companyCode : ''
    },
    getSupplyObj2 (v) {
      console.log(v)
      // debugger
      // this.materialCode = v ?v.materialCode :''
    },
    getUserObj (v) {
      // debugger
      this.preform.username = v ? v.username : ''
    },
    getQuerydata (v) {
      // this.queryParam = v;
      // let params = v||{}
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    // 编辑tab
    editTab (type) {
      if (type === 'add') {
        // 新增
        for (let i in this.form) {
          this.form[i] = ''
        }
      } else {
        // 修改
        for (let i in this.form) {
          this.form[i] = this.currentRow[i]
        }
      }
      this.dialogFormVisible = true
    },
    deleteOne (val) {
      this.$confirm(this.$t('common.delRow'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          // debugger
        })
        .catch(() => {})
    },
    addOne () {
      // 验证form表单
      this.$refs.form.validate(valid => {
        if (valid) {
          // =====
        } else {
          return false
        }
      })
    },
    handleCurrentChange (val) {
      this.currentRow = val
    },
    // 多选
    getPeopleBuyer (data) {
      let users = ''
      for (let i = 0; i < data.length; i++) {
        let user = ''
        if (i == data.length - 1) {
          user = data[i].username
        } else {
          user = data[i].username + ','
        }
        users += user
      }
      this.userBuyer = users
    },
    // 单选
    getPeople (data) {
      this.userVendor = data[0].username
    },
    selectPurchasers () {
      this.showPurchasers = true
    },
    selectSupplier () {
      this.showSupplier = true
    }
  }
}
</script>
<style>
.input-with-select .el-input-group__prepend {
  background-color: #fff;
}
</style>
<style scoped lang="scss">
.the_quickSearchDemo_wrapper {
  .prepend-select {
    width: 75px;
  }
  .demo-section {
    padding: 10px;
    border: solid 1px #dddddd;
    border-radius: 8px;
    margin-bottom: 10px;
    margin-top: 10px;
  }
  .demo-section__title {
    color: #807a7a;
  }
  .demo-form-item {
    display: flex;
    align-items: center;
  }
  .demo-form-label {
    width: 100px;
  }
  .category-form-label {
    width: 130px;
  }
  .the_QuickSearch_wrapper {
    // display: inline-flex;
    // width: 300px;
    // padding: 5px;
    span {
      line-height: 33px;
      padding-right: 8px;
    }
  }
  .purchaser {
    margin-right: 50px;
  }
  .the_follow_tender_dialog .el-row {
    margin-bottom: 11px;
    .el-col > span {
      padding-right: 11px;
    }
  }
  .the_table_wrapper {
    width: 1100px;
    max-height: 300px;
    border: 1px solid #dfe6ec;
    overflow: auto;
    padding: 5px;
  }
  .the_list_table {
    width: 100%;
    border-collapse: collapse;
    text-align: center;
    tr {
      td {
        border: 1px solid #dfe6ec;
        div {
          width: 100px;
          overflow: hidden;
          white-space: nowrap;
          text-overflow: ellipsis;
        }
      }
    }
  }
  .the_list_table tr:first-child td {
    background-color: #88c1f4 !important;
    color: #495060;
  }
}
</style>
