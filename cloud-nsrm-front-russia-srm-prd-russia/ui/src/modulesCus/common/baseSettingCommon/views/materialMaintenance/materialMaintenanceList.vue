<template>
  <el-container
    class="flex-container the_material_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        ref="formRef"
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
          <!-- 新增 -->
          <AuthorityButton
            v-if="userType === 'BUYER'"
            type="primary"
            code="base:materialMaintenance:add"
            @click="addMaterial"
          >
            {{ $t('common.add') }}
          </AuthorityButton>
          <!-- 导入 -->
          <MImport
            ref="import"
            code="base:materialMaintenance:import"
            :title="iModal.title"
            :up-load-url="iModal.upLoadUrl"
            :extra-data="extraData"
            type="default"
            @downloadTemplate="downloadTemplate"
          />
          <ExportExcel
            v-if="userType === 'BUYER'"
            :page-url="'/api-base/material/materialItem/listPageByCondition'"
            :filter-params="exportParam"
            :table-header="tableHeaderExport"
            :dict-codes="dictCodes"
            timeout="1000000"
            :export-size="30000"
            export-mode="front"
            type="default"
          />
          <!-- :disabled="disabledExportExcel" <el-tooltip
            v-if="userType === 'BUYER'"
            :content="$t('components.eio.msgFilterExoprt')"
            placement="top"
            effect="dark"
          >
          </el-tooltip> -->
        </template>
      </MainHeader>

      <TableView
        :ref="gridId"
        :table-data="materialModle.tableData"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :openCustomTable="true"
        :customTableKey="customTableKey"
        :comActive="$attrs['changeTab']"
        :url="dataUrl"
      />
    </el-main>

    <!-- 查看采购组织 -->
    <srm-dialog
      :title="$t('dataConfMod.viewPurchaseOrg')"
      size="large"
      :visible.sync="dialogFormVisible"
      :close-on-click-modal="false"
    >
      <div style="margin: 0 0 8px">
        <AuthorityButton
          code="base:materialMaintenance:addItemOra"
          type="primary"
          @click="addOneItem"
        >
          <!-- 新增 -->
          {{ $t('common.add') }}
        </AuthorityButton>
      </div>
      <el-table
        :data="displayItem"
        style="width: 100%"
        border
        max-height="300"
        @selection-change="checkChange"
      >
        <el-table-column
          type="selection"
          width="50"
        />
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
              :placeholder="$t('common.pleaseSelect')"
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
              :placeholder="$t('common.pleaseSelect')"
              :scope="scope.row"
              @select="selectHandler2"
            />
          </template>
        </el-table-column>
        <!-- 采购属性 -->
        <el-table-column
          align="center"
          prop="materialType"
          label="物料类型"
          min-width="100"
          :show-overflow-tooltip="true"
        >
          <template slot-scope="scope">
            <DictSelect
              v-model="scope.row.materialType"
              code="MATERIAL_TYPE"
              :disabled="!scope.row.orgId || !scope.row.organizationId"
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
        <!-- 操作 -->
        <el-table-column
          :label="$t('common.operation')"
          width="60"
        >
          <template slot-scope="scope">
            <AuthorityButton
              v-if="!scope.row.materialOrgId"
              code="base:materialMaintenance:handleDelClick"
              type="text"
              @click.stop.prevent="handleDelOrgClick(scope.$index, scope.row)"
            >
              <!-- 删除 -->
              {{ $t('common.delete') }}
            </AuthorityButton>
          </template>
        </el-table-column>
      </el-table>
      <div slot="footer" class="dialog-footer">
        <AuthorityButton
          code="base:materialMaintenance:handleSaveItemOra"
          type="primary"
          @click="handleSaveItem"
        >
          <!-- 提交 -->
          {{ $t('common.submit') }}
        </AuthorityButton>
        <!-- 关闭 -->
        <el-button
          @click="dialogFormVisible = false"
        >
          {{ $t('common.close') }}
        </el-button>
      </div>
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
        max-height="300"
        @selection-change="handleSelectionChange2"
      >
        <el-table-column
          type="selection"
          width="55"
        />
        <!-- 序号 -->
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
          :label="$t('materialMainData.materialCode')"
          min-width="150"
          :show-overflow-tooltip="true"
        />
        <!-- 物料名称 -->
        <el-table-column
          align="center"
          prop="itemDesc"
          :label="$t('materialMainData.materialDesc')"
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
        />
        <!-- 有效结束时间 -->
        <el-table-column
          align="center"
          prop="expirationDate"
          :label="$t('dataConfMod.expirationDate')"
          min-width="150"
          :show-overflow-tooltip="true"
        />
        <!-- 操作 -->
        <el-table-column
          :label="$t('common.operation')"
          width="100"
        >
          <template slot-scope="scope">
            <!-- 维护物料信息 -->
            <el-button
              type="text"
              @click="addOneItem2(scope.row)"
            >
              {{
                $t('dataConfMod.maintainMaterialInfo')
              }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </srm-dialog>
  </el-container>
</template>

<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import OrganizationSelector from 'lib@/components/organization-selector'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import CPagination from 'lib@/components/c-pagination'
import CCategorySelect from 'lib@/components/c-category-select'
import { parseTime, adaptDictData } from '@/utils'
import { getDictItem } from '@/api/common'
import ExportExcel from 'lib@/components/export-excel'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import materialMaintenanceDetail from './materialMaintenanceDetail'
import purchaseCatalogDetailNew from './purchaseCatalogDetailNew'
import { mapGetters } from 'vuex'
import cloneDeep from 'lodash/cloneDeep'
import { commonApi } from 'mod@/common/baseSettingCommon/api'

export default {
  name: 'MaterialMaintenanceBuyer',
  components: {
    CPagination,
    MainHeader,
    FormWrapper,
    CCategorySelect,
    ExportExcel,
    MImport,
    OrganizationSelector,
    TableView
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      dictCodes: {
        materialAttr: 'MATERIAL_ATTR',
        unit: 'unit'
      },
      gridId: 'list',
      tableData2: [],
      loading: false,
      pageSize: 15,
      currentRow: null,
      queryParam: {},
      extraData: {
        fileModular: 'base',
        fileFunction: 'material',
        fileType: 'excel'
      },
      disabledExportExcel: true,
      queryForm: [],
      formRules: {},
      purUnit: [],
      iModal: {
        title: this.$t('components.eio.importTitle'),
        upLoadUrl: '/api-base/material/materialItem/importMaterialItemExcel'
      },
      catList: [], // 采购分类
      materialModle: {
        tableData: [],
        rules: {
          materialCode: { type: 'string', required: true },
          materialName: { type: 'string', required: true },
          unit: { type: 'string', required: true },
          categoryFullName: { type: 'string', required: true }
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
      dialogFormVisible: false,
      dialogFormVisible2: false,
      globalMaterialId: null,
      displayItem: [],
      displayItem2: [],
      checkChangeOrg: [],
      preFormObj: { itemStatus: 'Y' },
      orgRow: {},
      tableHeader: [],
      customTableKey: ''
    }
  },
  computed: {
    ...mapGetters(['userType']),
    dataUrl () {
      if (this.userType === 'BUYER') {
        return '/api-base/material/materialItem/listPageByCondition'
      } else {
        return '/api-inq/price/priceLibrary/listForMaterialSecByVendor'
      }
    },
    exportParam () {
      let param = cloneDeep(this.queryParam)
      param.isExport = 'N'
      return param
    },
    tableHeaderExport () {
      return this.tableHeader.filter(item => item.prop !== 'operation')
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
          this.preFormObj = Object.assign(
            {},
            { ceeaMaterialStatus: this.$route.params.ceeaMaterialStatus }
          )
        }
      }
    }
  },
  created () {
    const _this = this
    if (this.userType === 'BUYER') {
      this.customTableKey = 'materialMaintenanceListBuyer'
      this.queryForm = [
        {
          prop: 'materialName',
          label: () => this.$t('common.materialName') // '物料名称'
        },
        {
          prop: 'materialCode',
          label: () => this.$t('common.materialCode') // '物料编码'
        },
        {
          prop: 'categoryName',
          label: () => this.$t('dataConfMod.category'), // '品类'
          type: 'catSelect',
          showKey: 'categoryName'
        },
        {
          prop: 'orgId',
          label: this.$t('dataConfMod.orgId'), // 业务实体
          type: 'OUorganizationSelector'
        },
        {
          prop: 'organizationId',
          label: this.$t('dataConfMod.organizationId'), // 库存组织
          type: 'INVorganizationSelector',
          parentId: 'orgId'
        },
        {
          prop: 'itemStatus',
          label: () => this.$t('dataConfMod.enabled'),
          type: 'select',
          options: [
            { label: this.$t('common.yes'), value: 'Y' },
            { label: this.$t('common.no'), value: 'N' }
          ]
        },
        // 订单日期
        {
          prop: 'dateList',
          label: () => this.$t('qualitySynergy.updateDate'),
          type: 'daterange'
        },
        {
          prop: 'materialType',
          label: () => '规格型号'
        },
        {
          prop: 'createdBy',
          label: () => this.$t('common.creator')
        }
      ]
      this.tableHeader = [
        {
          prop: 'materialName',
          label: () => this.$t('common.materialName'), // 物料名称
          minWidth: 120
        },
        {
          prop: 'materialCode',
          label: () => this.$t('common.materialCode'), // 物料编码
          minWidth: 100
        },
        {
          prop: 'unit',
          label: () => this.$t('dataConfMod.unit'), //  单位
          minWidth: 80,
          dataType: 'dict',
          code: 'unit'
        },
        {
          prop: 'description',
          label: () => '物料描述',
          minWidth: 150
        },
        {
          prop: 'materialType',
          label: () => '规格型号',
          minWidth: 150
        },
        {
          prop: 'categoryName',
          label: () => this.$t('purchaseDemand.materialCateSub'), // 物料小类
          minWidth: 150
        },
        {
          prop: 'categoryFullName',
          label: () => this.$t('purchaseDemand.categoryFullName'), // 品类全称
          width: 150
        },
        // {
        //   prop: 'orgNames',
        //   label: () => this.$t('dataConfMod.organization'), // 所属组织
        //   width: 150,
        //   showType: 'button',
        //   btnStyle: 'text',
        //   callback: function (row) {
        //     this.openOrganizationDialog(row)
        //   }.bind(this),
        //   formattor: (val) => {
        //     return this.$t('common.view')
        //   }
        // },
        // {
        //   prop: 'purchaseCycle',
        //   label: () => this.$t('dataConfMod.purchasingCycle'), // 采购周期
        //   width: 130
        // },
        // {
        //   prop: 'materialAttr',
        //   label: () => this.$t('dataConfMod.purchasingAttributes'), // 采购属性
        //   dataType: 'dict',
        //   code: 'MATERIAL_ATTR',
        //   width: 150
        // },
        // {
        //   prop: 'miniSplit',
        //   label: () => this.$t('dataConfMod.miniSplit'), // 最少拆单量
        //   width: 150
        // },
        // {
        //   prop: 'orderQuantityMinimum',
        //   label: () => this.$t('dataConfMod.orderQuantityMinimum'), // 最小起订量
        //   width: 150
        // },
        // {
        //   prop: 'minimumPackagingQuantity',
        //   label: () => this.$t('dataConfMod.MtInnerboxMinPackagingQuantity'), // 内箱最大包装数量
        //   width: 150
        // },
        // {
        //   prop: 'outboxMinPackagingQuantity',
        //   label: () => this.$t('dataConfMod.MtOutboxMinPackagingQuantity'), // 外箱最大包装数量
        //   width: 150
        // },
        // {
        //   prop: 'minimumSafetyInventory',
        //   label: () => this.$t('dataConfMod.minimumSafetyInventory'), // 最小安全库存
        //   width: 150
        // },
        // {
        //   prop: 'brand',
        //   label: () => this.$t('dataConfMod.band'), // 品牌
        //   width: 150
        // },
        {
          prop: 'createdBy',
          label: () => this.$t('common.creator'), // 创建人
          width: 150
        },
        {
          prop: 'creationDate',
          label: () => this.$t('common.creationTime'), // 创建时间
          width: 150
        },
        {
          prop: 'lastUpdatedBy',
          label: () => this.$t('dataConfMod.lastUpdatedBy'), // 更新人
          width: 150
        },
        {
          prop: 'lastUpdateDate',
          label: () => this.$t('dataConfMod.lastUpdateDate'), // 更新时间
          width: 150
        },
        {
          prop: 'operation',
          label: () => this.$t('common.operation'),
          width: 120,
          btnStyle: 'text',
          fixed: 'right',
          showType: 'buttons',
          buttons: [
            {
              callback: row => this.handleMaterialDetail(row),
              code: 'base:materialMaintenance:handleMaterialDetail',
              formattor: () => this.$t('common.edit')
            }
          ]
        }
      ]
    } else {
      this.customTableKey = 'materialMaintenanceListVendor'
      this.queryForm = [
        {
          prop: 'materialName',
          label: () => this.$t('common.materialName') // '物料名称'
        },
        {
          prop: 'materialCode',
          label: () => this.$t('common.materialCode') // '物料编码'
        },
        {
          prop: 'ceeaMaterialStatus',
          label: () => this.$t('common.status'), // 状态
          type: 'dict',
          code: 'CEEA_MATERIAL_STATUS'
        }
      ]
      this.tableHeader = [
        {
          prop: 'itemCode',
          label: () => this.$t('materialMainData.materialCode'), // 物料编码
          minWidth: 150
        },
        {
          prop: 'itemDesc',
          label: () => this.$t('materialMainData.materialDesc'), // 物料名称
          minWidth: 150
        },
        {
          prop: 'vendorCode',
          label: () => this.$t('common.vendorCode'), //  供应商编码
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
          formattor: (val) => {
            return this.$t('common.edit')
          }
        }
      ]
    }
  },
  mounted () {
    // 即将进行【导入物料】，您需要完成：1、导入企业管理的物料清单；2、维护物料对应的采购分类；
    const materialTip = localStorage.getItem('materialTip') || 'Y'
    if (materialTip === 'Y') {
      this.$confirm(this.$t('dataConfMod.materialMaintenanceAlert'), this.$t('common.tips'), {
        distinguishCancelAndClose: true,
        confirmButtonText: this.$t('common.start'),
        cancelButtonText: this.$t('common.toNotshowTip')
      }).then(() => {
        // 点击开始
      }).catch(() => {
        // 不再提示
        localStorage.setItem('materialTip', 'N')
      })
    }
    this.getQuerydata(this.preFormObj) //  查询数据
  },
  methods: {
    checkChange (val) {
      this.checkChangeOrg = val
    },
    syncFilterParams (values) {
      this.queryParam = values
      this.disabledExportExcel = JSON.stringify(this.queryParam) === '{}'
    },
    downloadTemplate () {
      // 物料维护导入模板.xlsx
      downloadFileLink(
        '/api-base/material/materialItem/importMaterialItemDownload',
        this.$t('dataConfMod.maMaintainImpXLSX')
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    },
    getQuerydata (obj) {
      const params = obj
      const { dateList, materialType, createdBy, extendConditions, ...rest } = obj || this.queryParam
      if (dateList) {
        params.lastUpdateDateBegin = dateList[0]
        params.lastUpdateDateEnd = dateList[1]
      }
      params.extendConditions = []
      if (materialType) {
        params.extendConditions.push({ 'field': 'materialType', 'operator': 'like', 'value': materialType })
      }
      if (createdBy) {
        params.extendConditions.push({ 'field': 'createdBy', 'operator': 'like', 'value': createdBy })
      }
      if (params.extendConditions.length === 0) {
        Reflect.deleteProperty(params, 'extendConditions')
      }
      this.queryParam = { ...rest, ...params }
      this.$nextTick(() => {
        this.$nextTick(() => {
          this.$refs[this.gridId].query()
        })
      })
    },
    handleSelectionChange (val) {
      this.selections = val
    },
    handleSelectionChange2 (val) {
      this.multiSelections = val
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
    openOrganizationDialog (row, sign) {
      this.orgRow = row
      this.$http({
        url: '/api-base/material/materialItem/findMaterialItemById',
        method: 'GET',
        params: { materialItemId: row.materialId },
        loading: true
      })
        .then(data => {
          this.globalMaterialId = row.materialId
          this.displayItem = data.data.materialOrgList
          sign ? (this.dialogFormVisible = false) : (this.dialogFormVisible = true)
        })
    },
    addOneItem () {
      this.displayItem.unshift({
        orgId: null,
        orgCode: null,
        orgName: null,
        organizationId: null,
        organizationCode: null,
        organizationName: null,
        itemStatus: 'N',
        materialId: this.globalMaterialId,
        materialType: null
      })
    },
    handleSaveItem () {
      if (!this.checkChangeOrg.length) {
        this.$message.error(this.$t('dataConfMod.msgPleaseSave')) // 请勾选保存的数据!
        return
      }
      if (this.checkChangeOrg.some(i => !i.orgId)) {
        this.$message.error(this.$t('dataConfMod.msgPleaseSelectOrg')) // 请选择业务实体!
        return
      }
      if (this.checkChangeOrg.some(i => !i.organizationId)) {
        this.$message.error(this.$t('dataConfMod.msgPSelectOrgza')) // 请选择库存组织!
        return
      }
      this.$http({
        url: '/api-base/base/materialOrg/batchSaveOrUpdateMaterialOrg',
        method: 'POST',
        data: this.checkChangeOrg,
        loading: true
      })
        .then(() => {
          this.$message.success(this.$t('common.success')) // 操作成功
          this.openOrganizationDialog(this.orgRow, 'save')
        })
    },
    // 行删除
    handleDelClick (index, row) {
      const itemId = row.materialId
      if (itemId) {
        commonApi.materialItemDel({ itemId }).then(res => {
          if (res) {
            this.getQuerydata()
          }
        })
      } else {
        this.materialModle.tableData.splice(index, 1)
      }
    },
    handleDelOrgClick (index) {
      this.displayItem.splice(index, 1)
    },
    addOne () {
      this.materialModle.tableData.unshift({
        add: true,
        status: 'Y',
        materialPictureFileId: null,
        materialPictureName: '',
        ceeaSupplierId: null,
        ceeaSupplierCode: '',
        ceeaSupplierName: '',
        categoryCode: '',
        categoryId: '',
        categoryFullName: '',
        ceeaMaterialStatus: 'NOT_NOTIFIED' // 新增 未通知状态
      })
      this.$nextTick(() => {
        this.$refs.mtTable.toggleRowSelection(this.materialModle.tableData[0], true)
      })
    },
    saveDataHandle () {
      if (this.selections.length > 0) {
        this.$refs.materialTable.validate(valid => {
          if (valid) {
            const subData = this.selections
            commonApi.saveOrUpdateMBatch(subData).then(res => {
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
        const submitData = this.selections
        commonApi.ceeaUpdateSupplier(submitData).then(res => {
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
        const submitData = this.selections
        const materialIds = []
        for (const elm of submitData) {
          if (!elm.ceeaSupplierCode) {
            this.$message.error(this.$t('dataConfMod.msgMaintainVendor')) // 请维护所选物料的供应商信息!
            return
          } else {
            materialIds.push(elm.materialId)
          }
        }
        commonApi.ceeaNotifyVendor(materialIds).then(res => {
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
          message: this.$t('components.tips.selectData'),
          type: 'warning'
        })
      }
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
        .then(data => {
          this.displayItem2 = data.data.list
          this.dialogFormVisible2 = true
        })
    },
    // 编辑物料详情
    handleMaterialDetail (row) {
      this.$emit('tab-add', {
        component: purchaseCatalogDetailNew,
        params: {
          flag: 'edit',
          tabName: 'purchaseCatalogDetailNew' + row.materialId,
          materialId: row.materialId
        },
        title: row.materialName,
        name: 'purchaseCatalogDetailNew' + row.materialId
      })
    },
    // 新增物料
    addMaterial () {
      this.$emit('tab-add', {
        component: purchaseCatalogDetailNew,
        params: {
          flag: 'add',
          tabName: 'purchaseCatalogDetailNew'
        },
        title: () => this.$t('dataConfMod.addMaterial'),
        name: 'purchaseCatalogDetailNew'
      })
    },
    // 编辑
    addOneItem2 (row) {
      this.dialogFormVisible2 = false
      this.$emit('tab-add', {
        component: materialMaintenanceDetail,
        params: {
          flag: 'edit',
          tabName: 'materialMaintenanceDetail' + row.itemId,
          materialId: row.itemId,
          row: row
        },
        title: row.itemDesc,
        name: 'materialMaintenanceDetail' + row.itemId
      })
    }
  }
}
</script>

<style scoped lang="scss">
.materialTableForm {
  position: absolute;
  top: 0;
  bottom: 0;
  height: 100%;
  width: 100%;
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
