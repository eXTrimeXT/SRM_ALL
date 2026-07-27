<template>
  <el-container
    class="flex-container the_material_wrapper"
    direction="vertical"
  >
    <el-main v-if="userType === 'BUYER'">
      <div style="padding-bottom: 12px">
        <FormWrapper
          :form-array="queryForm"
          :pre-form-obj="preFormObj"
          @getFormData="getQuerydata"
          @synchronous-value="syncFilterParams"
        />
      </div>

      <el-container
        direction="vertical"
        class="tablePd"
      >
        <el-main
          style="
            flex-grow: 1;
            display: flex;
            flex-direction: column;
            position: relative;
          "
        >
          <el-form
            ref="materialTable"
            class="tableForm"
            :model="materialModle"
            :rules="materialModle.rules"
          >
            <el-table
              ref="mtTable"
              v-loading="loading"
              stripe
              border
              height="100%"
              width="2000px"
              :data="materialModle.tableData"
              style="height: 100%"
              @selection-change="handleSelectionChange"
              @cell-click="cellClick"
            >
              <el-table-column type="selection" />
              <!-- 物料名称 -->
              <el-table-column
                prop="materialName"
                :label="$t('common.materialName')"
                show-overflow-tooltip
                min-width="140"
              >
                <template
                  slot="header"
                  slot-scope="scope"
                >
                  <i class="toRequired">*</i>{{ $t("common.materialName") }}
                </template>
                <template slot-scope="scope">
                  <template v-if="scope.row.add || scope.row.edit">
                    <el-form-item
                      :prop="'tableData.' + scope.$index + '.materialName'"
                      :rules="materialModle.rules.materialName"
                    >
                      <el-input
                        v-show="true"
                        v-model="scope.row.materialName"
                      />
                    </el-form-item>
                  </template>
                  <span v-else>{{ scope.row.materialName }}</span>
                </template>
              </el-table-column>
              <!-- 物料编码 -->
              <el-table-column
                prop="materialCode"
                :label="$t('common.materialCode')"
                min-width="100"
                align="center"
                show-overflow-tooltip
              >
                <template
                  slot="header"
                  slot-scope="scope"
                >
                  <i class="toRequired">*</i>{{ $t("common.materialCode") }}
                </template>
                <template slot-scope="scope">
                  <template v-if="scope.row.add">
                    <el-form-item
                      :prop="'tableData.' + scope.$index + '.materialCode'"
                      :rules="materialModle.rules.materialCode"
                    >
                      <el-input
                        v-show="true"
                        v-model="scope.row.materialCode"
                      />
                    </el-form-item>
                  </template>
                  <span v-else>{{ scope.row.materialCode }}</span>
                </template>
              </el-table-column>
              <!-- 型号与规格 -->
              <!-- <el-table-column
                prop="specification"
                :label="$t('dataConfMod.specification')"
                min-width="120"
                show-overflow-tooltip
              >
                <template slot-scope="scope">
                  <template v-if="scope.row.add || scope.row.edit">
                    <el-form-item
                      :prop="'tableData.' + scope.$index + '.specification'"
                    >
                      <el-input
                        v-show="true"
                        v-model="scope.row.specification"
                      />
                    </el-form-item>
                  </template>
                  <span v-else>{{ scope.row.specification }}</span>
                </template>
              </el-table-column> -->
              <!-- 单位 -->
              <el-table-column
                prop="unit"
                :label="$t('dataConfMod.unit')"
                min-width="100"
              >
                <template
                  slot="header"
                  slot-scope="scope"
                >
                  <i class="toRequired">*</i>{{ $t("dataConfMod.unit") }}
                </template>
                <template slot-scope="scope">
                  <template>
                    <el-form-item
                      :prop="'tableData.' + scope.$index + '.unit'"
                      :rules="materialModle.rules.unit"
                    >
                      <dict-select
                        v-model="scope.row.unit"
                        code="unit"
                        filterable
                        :disabled="!scope.row.add"
                        :placeholder="$t('dataConfMod.unit')"
                        @focus="selectFocus(scope.$index)"
                        @change-value="unitHandler"
                      />
                    </el-form-item>
                  </template>
                </template>
              </el-table-column>
              <!-- 所属物资大类 -->
              <el-table-column
                prop="bigCategoryName"
                :label="$t('dataConfMod.bigCategoryName')"
                show-overflow-tooltip
                min-width="120"
              />
              <!-- 所属物资中类 -->
              <el-table-column
                prop="middleCategoryName"
                :label="$t('dataConfMod.middleCategoryName')"
                show-overflow-tooltip
                min-width="120"
              />
              <!-- 物资分类 -->
              <el-table-column
                prop="categoryName"
                :label="$t('dataConfMod.itemCategory')"
                min-width="150px"
                align="center"
                show-overflow-tooltip
              >
                <template
                  slot="header"
                  slot-scope="scope"
                >
                  <i class="toRequired">*</i>{{ $t("dataConfMod.category") }}
                </template>
                <template slot-scope="scope">
                  <template v-if="scope.row.add || scope.row.edit">
                    <el-form-item
                      :prop="'tableData.' + scope.$index + '.categoryName'"
                      :rules="materialModle.rules.categoryName"
                    >
                      <CCategorySelect
                        v-model="scope.row.categoryName"
                        disabled
                        :scope="scope.row"
                        show-key="categoryName"
                        @select="comfirmSelect"
                      />
                    </el-form-item>
                  </template>
                  <span v-else>{{ scope.row.categoryFullName }}</span>
                </template>
              </el-table-column>
              <!-- 组织序号 -->
              <el-table-column
                prop="organizatonIndex"
                :label="$t('dataConfMod.organizatonIndex')"
                show-overflow-tooltip
                min-width="80"
              />
              <!-- 所属组织 -->
              <el-table-column
                prop="organizationName"
                :label="$t('dataConfMod.organization')"
                show-overflow-tooltip
                min-width="100"
              />
              <!-- 是否用于采购 -->
              <el-table-column
                prop="userPurchase"
                :label="$t('dataConfMod.userPurchase')"
                show-overflow-tooltip
                min-width="120"
              >
                <template slot-scope="{ row }">
                  <span>{{
                    row.itemStatus === "Y" ? $t("common.yes") : $t("common.no")
                  }}</span>
                </template>
              </el-table-column>
              <!-- 是否库存管理 -->
              <el-table-column
                prop="stockEnableFlag"
                :label="$t('dataConfMod.ifStockManage')"
                show-overflow-tooltip
                min-width="100"
              >
                <template slot-scope="{ row }">
                  <span>{{
                    row.itemStatus === "Y" ? $t("common.yes") : $t("common.no")
                  }}</span>
                </template>
              </el-table-column>
              <!-- 库存组织是否启用 -->
              <el-table-column
                prop="itemStatus"
                :label="$t('dataConfMod.ifOrgOpen')"
                show-overflow-tooltip
                min-width="120"
              >
                <template slot-scope="{ row }">
                  <span>{{
                    row.itemStatus === "Y" ? $t("common.yes") : $t("common.no")
                  }}</span>
                </template>
              </el-table-column>
              <!-- 创建时间 -->
              <el-table-column
                prop="creationDate"
                :label="$t('common.creationTime')"
                show-overflow-tooltip
                min-width="130"
                :formatter="(row, column, cellValue) => $parseTime(cellValue)"
              />
            </el-table>
          </el-form>
        </el-main>
        <el-footer class="page-bar">
          <CPagination
            ref="queryPagination"
            class="c-query-table-pagination"
            :total="pageInfo.total"
            :page-num="pageInfo.pageNum"
            :page-size="pageInfo.pageSize"
            @current-change="handleCurrentChange"
            @size-change="handleSizeChange"
          />
        </el-footer>
        <!-- 查看采购组织 -->
        <srm-dialog
          :title="$t('dataConfMod.viewPurchaseOrg')"
          size="large"
          :visible.sync="dialogFormVisible"
          :close-on-click-modal="false"
        >
          <p>
            <AuthorityButton
              code="base:materialMaintenance:addItemOra"
              type="primary"
              @click="addOneItem"
            >
              {{ $t("common.add") }}
            </AuthorityButton>
            <el-button
              @click="dialogFormVisible = false"
            >
              {{ $t("common.close") }}
            </el-button>
          </p>
          <el-table
            :data="displayItem"
            style="width: 100%"
            border
            height="381px"
          >
            <el-table-column
              align="center"
              type="index"
              width="50"
            />
            <!-- 业务实体 -->
            <el-table-column
              align="center"
              prop="orgId"
              :label="$t('dataConfMod.orgId')"
              min-width="150"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <OrganizationSelector
                  ref="organizationSelector"
                  v-model="scope.row.orgId"
                  :parent-id="-1"
                  node-type="OU"
                  :scope="scope.row"
                  @select="selectHandler"
                />
              </template>
            </el-table-column>
            <!-- 库存组织 -->
            <el-table-column
              align="center"
              prop="organizationId"
              :label="$t('dataConfMod.organizationId')"
              min-width="150"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <OrganizationSelector
                  ref="organizationSelector2"
                  v-model="scope.row.organizationId"
                  :parent-id="scope.row.orgId"
                  node-type="INV"
                  :scope="scope.row"
                  @select="selectHandler2"
                />
              </template>
            </el-table-column>
            <!-- 物料启用状态 -->
            <el-table-column
              align="center"
              prop="itemStatus"
              :label="$t('dataConfMod.itemStatus')"
              width="100"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <el-checkbox
                  v-model="scope.row.itemStatus"
                  true-label="Y"
                  false-label="N"
                />
              </template>
            </el-table-column>
            <!-- 是否可存储 -->
            <el-table-column
              align="center"
              prop="stockEnableFlag"
              :label="$t('dataConfMod.stockEnableFlag')"
              width="100"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <el-checkbox
                  v-model="scope.row.stockEnableFlag"
                  true-label="Y"
                  false-label="N"
                />
              </template>
            </el-table-column>
            <!-- 是否用于采购 -->
            <el-table-column
              align="center"
              prop="userPurchase"
              :label="$t('dataConfMod.userPurchase')"
              width="100"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <el-checkbox
                  v-model="scope.row.userPurchase"
                  true-label="Y"
                  false-label="N"
                />
              </template>
            </el-table-column>
            <el-table-column
              :label="$t('common.operation')"
              width="60"
            >
              <template slot-scope="scope">
                <AuthorityButton
                  code="base:materialMaintenance:handleSaveItemOra"
                  type="text"
                  @click="handleSaveItem(scope.$index, scope.row)"
                >
                  {{ $t("common.save") }}
                </AuthorityButton>
              </template>
            </el-table-column>
          </el-table>
        </srm-dialog>
        <!-- 上架物料维护 -->
        <srm-dialog
          :title="$t('dataConfMod.handleMaterialMaintain')"
          size="large"
          :visible.sync="dialogFormVisible2"
          :close-on-click-modal="false"
        >
          <el-table
            :data="displayItem2"
            style="width: 100%"
            border
            height="311px"
            @selection-change="handleSelectionChange2"
          >
            <el-table-column
              type="selection"
              width="55"
            />
            <el-table-column
              align="center"
              type="index"
              width="50"
              :label="$t('common.sort')"
            />
            <!-- 物料编码 -->
            <el-table-column
              align="center"
              prop="itemCode"
              :label="$t('common.materialCode')"
              min-width="150"
              :show-overflow-tooltip="true"
            />
            <!-- 物料名称 -->
            <el-table-column
              align="center"
              prop="itemDesc"
              :label="$t('common.materialName')"
              min-width="150"
              :show-overflow-tooltip="true"
            />
            <!-- 供应商编码 -->
            <el-table-column
              align="center"
              prop="vendorCode"
              :label="$t('common.vendorCode')"
              min-width="150"
              :show-overflow-tooltip="true"
            />
            <!-- 供应商名称 -->
            <el-table-column
              align="center"
              prop="vendorName"
              :label="$t('common.vendorName')"
              min-width="150"
              :show-overflow-tooltip="true"
            />
            <!-- 有效开始时间 -->
            <el-table-column
              align="center"
              prop="effectiveDate"
              :label="$t('dataConfMod.effectiveDate')"
              min-width="150"
              :show-overflow-tooltip="true"
              :formatter="(row, column, cellValue) => $parseTime(cellValue)"
            />
            <!-- 有效结束时间 -->
            <el-table-column
              align="center"
              prop="expirationDate"
              :label="$t('dataConfMod.expirationDate')"
              min-width="150"
              :show-overflow-tooltip="true"
              :formatter="(row, column, cellValue) => $parseTime(cellValue)"
            />
            <el-table-column
              :label="$t('common.operation')"
              width="100"
            >
              <template slot-scope="scope">
                <el-button
                  type="text"
                  @click="addOneItem2(scope.row)"
                >
                  {{ $t("dataConfMod.maintainMaterialInfo") }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </srm-dialog>
      </el-container>
    </el-main>
    <el-main v-if="userType === 'VENDOR'">
      <FormWrapper
        :form-array="queryForm"
        :pre-form-obj="preFormObj"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />

      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <MImport
            ref="import"
            :title="iModal.title"
            :up-load-url="iModal.upLoadUrl"
            :extra-data="extraData"
            @downloadTemplate="downloadTemplate"
            @handleSuccess="handleSuccess"
          />
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData2"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        url="/api-base/material/materialItem/listPageMaterialItemChart"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import CPagination from 'lib@/components/c-pagination'
import CCategorySelect from 'lib@/components/c-category-select'
import { parseTime, adaptDictData } from '@/utils'
import ExportExcel from 'lib@/components/export-excel'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import { purCatApi, planSupApi } from 'mods@/planManagementSupplier/api'

export default {
  name: 'MaterialMaintenance',
  components: {
    CPagination,
    MainHeader,
    FormWrapper,
    CCategorySelect,
    ExportExcel,
    MImport,
    OrganizationSelector,
    QuickSearch,
    TableView
  },
  data () {
    return {
      gridId: 'list',
      tableData2: [],
      userType: this.$store.getters.userInfo.userType,
      loading: false,
      pageSize: 15,
      currentRow: null,
      tableHeader: [],
      queryParam: {},
      extraData: {
        fileModular: 'base',
        fileFunction: 'material',
        fileType: 'excel'
      },
      disabledExportExcel: true,
      queryForm: [],
      iModal: {
        title: this.$t('components.eio.importTitle'),
        upLoadUrl: '/api-base/base/material-item-sec/importExcel'
      },
      catList: [], // 采购分类
      materialModle: {
        tableData: [],
        rules: {
          materialCode: { type: 'string', required: true },
          materialName: { type: 'string', required: true },
          unit: { type: 'string', required: true },
          categoryName: { type: 'string', required: true }
        }
      },
      pageInfo: {
        total: 0,
        pageNum: 1,
        pageSize: 15
      },
      queryPage: {
        pageNum: 1,
        pageSize: 15
      },
      selections: [],
      multiSelections: [],
      acceptFileType: ['jpg', 'png', 'jpeg'],
      dialogFormVisible: false,
      dialogFormVisible2: false,
      globalMaterialId: null,
      displayItem: [],
      displayItem2: [],
      preFormObj: {}
    }
  },
  watch: {
    $route: {
      // 当前功能已经打开的时候监听是否是从其他功能跳转过来的
      deep: true,
      immediate: true,
      handler () {
        if (
          this.$route.params.from === 'workCount' &&
          this.$route.params.funName === 'materialMaintenance'
        ) {
          // 供应商 工作台跳转
          this.queryParam.ceeaMaterialStatus = this.$route.params.ceeaMaterialStatus
          // this.firstLoad = false;
          this.preFormObj = Object.assign(
            {},
            { ceeaMaterialStatus: this.$route.params.ceeaMaterialStatus }
          )
        }
      }
    }
  },
  created () {
    let _this = this
    this.tableHeader = [
      {
        prop: 'itemCode',
        label: () => this.$t('common.materialCode'), // 物料编码
        minWidth: 150
      },
      {
        prop: 'itemDesc',
        label: () => this.$t('common.materialName'), // 物料名称
        minWidth: 150
      },
      {
        prop: 'vendorCode',
        label: () => this.$t('common.vendorCode'), // 供应商编码
        minWidth: 150
      },
      {
        prop: 'vendorName',
        label: () => this.$t('common.vendorName'), // 供应商名称
        minWidth: 150
      },
      {
        prop: 'effectiveDate',
        label: () => this.$t('dataConfMod.effectiveDate'), // 有效开始时间
        width: 120,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'expirationDate',
        label: () => this.$t('dataConfMod.expirationDate'), // 有效结束时间
        width: 120,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'operation',
        label: () => this.$t('common.operation'), // 操作
        width: 100,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.addOneItem2(row)
        }.bind(this),
        formattor (val) {
          return _this.$t('dataConfMod.maintainMaterialInfo') // 维护物料信息
        }
      }
    ]

    if (this.userType === 'BUYER') {
      this.queryForm = [
        {
          prop: 'materialCode',
          label: () => this.$t('common.materialCode'), // 物料编码
          type: 'input'
        },
        {
          prop: 'materialId',
          label: () => this.$t('common.materialName'), // '物料名称'
          type: 'quicksearch',
          showKey: 'materialName',
          propKey: 'materialId',
          name: 'scc_base_material_item'
        },
        {
          prop: 'bigCategoryId',
          label: () => this.$t('dataConfMod.bigCategoryId'), // 物资大类
          type: 'input'
        },
        {
          prop: 'middleCategoryId',
          label: () => this.$t('dataConfMod.middleCategoryId'), // 物资中类
          type: 'input'
        },
        {
          prop: 'startDate',
          label: () => this.$t('dataConfMod.createForm'), // 创建日期从
          type: 'date'
        },
        {
          prop: 'endDate',
          label: () => this.$t('dataConfMod.createTo'), // 创建日期至
          type: 'date'
        },
        {
          prop: 'ceeaMaterialStatus',
          label: () => this.$t('common.status'), //  状态
          type: 'dict',
          code: 'CEEA_MATERIAL_STATUS'
        },
        {
          prop: 'categoryId',
          label: () => this.$t('dataConfMod.itemCategory'), // 物资分类
          type: 'input'
        },
        {
          prop: 'organizationId',
          label: () => this.$t('dataConfMod.organization'), // 所属组织
          type: 'input'
        }
        // {
        //   prop: "ceeaSupplierName",
        //   label: '供应商名称', //'供应商名称'
        //   type: "quicksearch",
        //   showKey: "companyName",
        //   name: "scc_sup_company_info"
        // },
        // {
        //   prop: "ceeaIfCatalogMaterial",
        //   label: '是否目录化',
        //   type: "select",
        //   options: [
        //     {value:'Y',label:'是'},
        //     {value:'N',label:'否'}
        //   ]
        // },
        // {
        //   prop: "categoryName",
        //   label: () => this.$t("dataConfMod.category"), //'品类'
        //   type: "catSelect",
        //   showKey: "categoryName"
        // },
        // {
        //   prop: "ceeaContractNo",
        //   label: '合同编号', //'物料名称'
        //   type: "quicksearch",
        //   showKey: "contractCode",
        //   name: "scc_contract_head_confirmed"
        // },
      ]
    } else {
      this.queryForm = [
        {
          prop: 'materialName',
          label: () => this.$t('common.materialName'), // '物料名称'
          type: 'quicksearch',
          showKey: 'materialName',
          name: 'scc_base_material_item'
        },
        {
          prop: 'ceeaMaterialStatus',
          label: () => this.$t('common.status'), // 状态
          dataType: 'dict',
          code: 'CEEA_MATERIAL_STATUS'
        }
      ]
    }

    this.getQuerydata() //  查询数据
  },
  mounted () {
    // 即将进行【导入物料】，您需要完成：1、导入企业管理的物料清单；2、维护物料对应的采购分类；
    let materialTip = localStorage.getItem('materialTip') || 'Y'
    if (materialTip === 'Y') {
      this.$confirm(this.$t('dataConfMod.materialMaintenanceAlert'), this.$t('common.tips'), {
        distinguishCancelAndClose: true,
        confirmButtonText: this.$t('common.start'),
        cancelButtonText: this.$t('common.toNotshowTip')
      })
        .then(() => {
          // 点击开始
        })
        .catch((action) => {
          // 不再提示
          localStorage.setItem('materialTip', 'N')
        })
    }
  },
  methods: {
    handleSuccess (result) { },
    syncFilterParams (values) {
      // debugger
      this.queryParam = values
      if (JSON.stringify(this.queryParam) === '{}') {
        this.disabledExportExcel = true
      } else {
        this.disabledExportExcel = false
      }
    },
    downloadTemplate () {
      // 物料维护导入模板.xlsx
      downloadFileLink(
        '/api-base/base/material-item-sec/importModelDownload',
        this.$t('dataConfMod.maMaintainImpXLSX')
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
      })
    },
    cellClick (row) {
      this.$refs.mtTable.toggleRowSelection(row, true)
    },
    editTab () { },
    getQuerydata (v) {
      this.queryParam = Object.assign({}, v)
      if (this.userType === 'BUYER') {
        this.$nextTick(() => {
          this.fatchListData(this.queryParam, this.queryPage)
        })
      } else {
        this.$nextTick(() => {
          this.$refs[this.gridId].query()
        })
      }
    },
    // 适配单位数据
    adaptUnitData (data) {
      let arr = []
      if (data && data.length > 0) {
        data.forEach((element) => {
          arr.push({
            id: element.unitId,
            value: element.unitCode,
            label: element.unitName
          })
        })
      }
      return arr
    },
    selectFocus (index) {
      this.currentRow = index
    },
    // 事件切换选择
    unitHandler (value, dictItem) {
      this.materialModle.tableData[this.currentRow].unitName = dictItem.label
    },
    // 查询列表数据
    fatchListData (p1, p2) {
      let queryObj = { ...p1, ...p2 }
      if (this.userType === 'BUYER') {
        planSupApi.reportsTestItemList(queryObj).then((res) => {
          if (res.data && res.data.list) {
            this.loading = false
            this.pageInfo.total = res.data.total
            this.pageInfo.pageNum = res.data.pageNum
            this.pageInfo.pageSize = res.data.pageSize
            this.materialModle.tableData = res.data.list.map((i) => ({
              ...i,
              edit: false
            }))
          }
        })
      } else {
        this.$http({
          url: '/api-base/material/materialItem/listPageMaterialItemChart',
          method: 'POST',
          data: queryObj,
          loading: true
        })
          .then((res) => {
            if (res.data && res.data.list) {
              this.loading = false
              this.pageInfo.total = res.data.total
              this.pageInfo.pageNum = res.data.pageNum
              this.pageInfo.pageSize = res.data.pageSize
              this.materialModle.tableData = res.data.list.map((i) => ({
                ...i,
                edit: false
              }))
            }
          })
          .catch((err) => {
            console.log(err)
          })
      }
    },
    handleSelectionChange (val) {
      this.selections = val
    },
    handleSelectionChange2 (val) {
      this.multiSelections = val
    },
    // 确认选中的品类
    comfirmSelect (node, scope) {
      scope.categoryId = node ? node.categoryId : null
      scope.categoryCode = node ? node.categoryCode : ''
      scope.categoryName = node ? node.categoryName : ''
      scope.categoryFullName = node ? node.categoryFullName : ''
    },
    changeInputVal (node) {
      // this.materialModle.tableData[this.rowIndex].categoryId = node.categoryId
      // this.materialModle.tableData[this.rowIndex].categoryName = node.categoryName
    },
    selectHandler (node, value, scope) {
      scope.organizationId = null
      scope.organizationCode = null
      scope.organizationName = null
      scope.orgId = node.organizationId
      scope.orgCode = node.organizationCode
      scope.orgName = node.organizationName
    },
    selectHandler2 (node, value, scope) {
      scope.organizationId = node.organizationId
      scope.organizationCode = node.organizationCode
      scope.organizationName = node.organizationName
    },
    getSupplyObj (node, scope) {
      scope.ceeaSupplierId = node ? node.companyId : ''
      scope.ceeaSupplierCode = node ? node.companyCode : ''
      scope.ceeaSupplierName = node ? node.companyName : ''
    },
    openOrganizationDialog (row) {
      this.$http({
        url: '/api-base/material/materialItem/findMaterialItemById',
        method: 'GET',
        params: { materialItemId: row.materialId },
        loading: true
      })
        .then((data) => {
          this.globalMaterialId = row.materialId
          this.displayItem = data.data.materialOrgList
          this.dialogFormVisible = true
        })
        .catch((err) => {
          console.log(err)
        })
    },
    addOneItem () {
      this.displayItem.push({
        orgId: null,
        orgCode: null,
        orgName: null,
        organizationId: null,
        organizationCode: null,
        organizationName: null,
        userPurchase: 'N',
        materialId: this.globalMaterialId
      })
    },
    handleSaveItem (index, row) {
      if (!(row.orgId && row.organizationId)) {
        this.$message.warning(this.$t('dataConfMod.msgSelOrgAndOrganization')) // 请选择业务实体和库存组织！
        return
      }
      const url = row.materialOrgId
        ? '/api-base/base/materialOrg/updateMaterialOrg'
        : '/api-base/base/materialOrg/addMaterialOrg'
      this.$http({
        url: url,
        method: 'POST',
        data: row,
        loading: true
      })
        .then((data) => {
          this.$message.success(this.$t('common.success'))
          this.openOrganizationDialog(row)
        })
        .catch((err) => {
          console.log(err)
        })
    },
    // 行删除
    handleDelClick (index, row) {
      let itemId = row.materialId
      if (itemId) {
        planSupApi.materialItemDel({ itemId }).then((res) => {
          if (res) {
            this.getQuerydata()
          }
        })
      } else {
        this.materialModle.tableData.splice(index, 1)
      }
    },
    // 行编辑
    handleEditClick (index, row) {
      this.materialModle.tableData[index].edit = true
      // row.edit = true
      this.$nextTick(() => {
        // this.$refs.mtTable.doLayout()
      })
    },
    showPic () { },
    addOne () {
      this.materialModle.tableData.unshift({
        add: true,
        status: 'Y',
        materialPictureFileId: null,
        materialPictureName: '',
        ceeaSupplierId: null,
        ceeaSupplierCode: '',
        ceeaSupplierName: '',
        categoryId: null,
        categoryCode: '',
        categoryName: '',
        categoryFullName: '',
        ceeaMaterialStatus: 'NOT_NOTIFIED' // 新增 未通知状态
      })
      this.$nextTick(() => {
        this.$refs.mtTable.toggleRowSelection(this.materialModle.tableData[0], true)
      })
    },
    saveDataHandle () {
      if (this.selections.length > 0) {
        this.$refs['materialTable'].validate((valid, materialModle) => {
          if (valid) {
            // let subData = this.materialModle.tableData
            let subData = this.selections
            planSupApi.saveOrUpdateMBatch(subData).then((res) => {
              if (res) {
                this.$message({
                  message: res.message,
                  type: 'success'
                })
                this.getQuerydata()
              }
            })
          } else {
            this.$message({
              message: this.$t('common.pleasefinishRequired'), // '请输入必填项'
              type: 'warning'
            })
          }
        })
      } else {
        this.$message({
          message: this.$t('common.cannotSave'), // '请选择保存的数据'
          type: 'warning'
        })
      }
    },
    // 保存供应商信息
    saveVendorInfo () {
      if (this.selections.length > 0) {
        let submitData = this.selections
        purCatApi.ceeaUpdateSupplier(submitData).then((res) => {
          if (res) {
            this.$message({ type: 'success', message: res.message })
            this.getQuerydata()
          }
        })
      } else {
        this.$message({
          message: this.$t('common.cannotSave'), // '请选择保存的数据'
          type: 'warning'
        })
      }
    },
    // 通知供应商
    ceeaNotifyVendor () {
      if (this.selections.length > 0) {
        let submitData = this.selections
        let materialIds = []
        for (let elm of submitData) {
          if (!elm.ceeaSupplierCode) {
            this.$message.error(this.$t('dataConfMod.msgMaintainVendor')) // 请维护所选物料的供应商信息
            return
          } else {
            materialIds.push(elm.materialId)
          }
        }
        purCatApi.ceeaNotifyVendor(materialIds).then((res) => {
          if (res) {
            this.$message({
              type: 'success',
              message: res.data ? res.data : res.message
            })
            this.getQuerydata()
          }
        })
      } else {
        this.$message({
          message: this.$t('components.userSelection.selectData'), // 请选择数据
          type: 'warning'
        })
      }
    },
    importOne () { },
    exportOne () { },
    handleCurrentChange (num) {
      this.queryPage.pageNum = num
      this.loading = true
      this.fatchListData(this.queryParam, this.queryPage)
    },
    handleSizeChange (size) {
      this.queryPage.pageSize = size
      this.loading = true
      this.fatchListData(this.queryParam, this.queryPage)
    },
    handleMaterialMaintain (index, row) {
      let url = null
      let params = {}
      if (this.userType === 'BUYER') {
        url = '/api-inq/price/priceLibrary/listForMaterialSecByBuyer'
        params = { itemCode: row.materialCode }
      } else {
        url = '/api-inq/price/priceLibrary/listForMaterialSecByVendor'
        params = {}
      }
      this.$http({
        url: url,
        method: 'POST',
        data: params,
        loading: true
      })
        .then((data) => {
          this.displayItem2 = data.data.list
          this.dialogFormVisible2 = true
        })
        .catch((err) => {
          console.log(err)
        })
    },
    addOneItem2 (row) {
      this.dialogFormVisible2 = false
      // this.$emit('tab-add', {
      //   component: materialMaintenanceDetail,
      //   params: {
      //     flag: 'edit',
      //     tabName: 'materialMaintenanceDetail' + row.itemId,
      //     materialId: row.itemId,
      //     row: row
      //   },
      //   title: row.itemDesc,
      //   name: 'materialMaintenanceDetail' + row.itemId
      // })
    }
  }
}
</script>
<style scoped lang="scss">
.tableForm {
  position: absolute;
  top: 0;
  bottom: 0;
  height: 100%;
  width: 100%;
  .el-table {
    height: 100%;
  }
}
.the_material_wrapper {
}
.download-link-wrap {
  .download-link-item {
    color: #1890ff;
    cursor: pointer;
  }
  .close-icon {
    font-weight: bold;
    cursor: pointer;
  }
}
.toRequired {
  color: #ff4949;
  padding-right: 2px;
}
.itemPic {
  max-height: 480px;
}
</style>
