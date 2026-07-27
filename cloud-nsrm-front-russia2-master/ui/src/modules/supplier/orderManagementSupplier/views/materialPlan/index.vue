<template>
  <el-container
    class="flex-container-notab the_materialItem_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        form-label-width="120px"
        @getFormData="getQuerydata"
      />

      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <AuthorityButton
            type="primary"
            code="ce:materialPlan:export"
            @click="openDialogVisible('export')"
          >
            {{ $t('common.export') }}
          </AuthorityButton>
          <MImport
            ref="import"
            style="display: inline-block; margin: 0 15px"
            :title="$t('common.import')"
            :up-load-url="iModal.upLoadUrl"
            :extra-data="extraData"
            @downloadTemplate="openDialogVisible('template')"
            @handleSuccess="handleSuccess"
          />
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        url="/api-sup-ce/material/materialItem/listPage"
      />
      <srm-dialog
        :title="$t('orderMod.materialPlanDetail')"
        size="large"
        :visible.sync="dialogFormVisible"
        :close-on-click-modal="false"
      >
        <div>
          <el-form
            ref="filterForm"
            :model="filterForm"
            label-width="80px"
            label-position="top"
            class="form-incontainer"
          >
            <el-row type="flex">
              <el-col>
                <el-form-item
                  :label="$t('bid_mod.businessEntity')"
                  :label-width="formLabelWidth"
                >
                  <OrganizationSelector
                    ref="organizationSelector"
                    v-model="filterForm.orgId"
                    :parent-id="-1"
                    node-type="OU"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col>
                <el-form-item
                  :label="$t('bid_mod.inv')"
                  :label-width="formLabelWidth"
                >
                  <OrganizationSelector
                    ref="organizationSelector"
                    v-model="filterForm.organizationId"
                    :parent-id="-1"
                    node-type="INV"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col>
                <el-form-item
                  :label="$t('bid_mod.tradingLocations')"
                  :label-width="formLabelWidth"
                >
                  <el-input
                    v-model="filterForm.organizationSite"
                    disabled
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row type="flex">
              <el-col>
                <el-form-item
                  :label="$t('common.materialCode')"
                  :label-width="formLabelWidth"
                >
                  <el-input
                    v-model="filterForm.materialCode"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col>
                <el-form-item
                  :label="$t('orderMod.planMonth')"
                  :label-width="formLabelWidth"
                >
                  <el-input
                    v-model="filterForm.monthlySchDate"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col>
                <el-form-item
                  :label="$t('orderMod.planStatus')"
                  :label-width="formLabelWidth"
                >
                  <DictSelect
                    v-model="filterForm.schType"
                    code="MATERIA_LITEM_TYPE"
                    disabled
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row type="flex">
              <el-col>
                <el-form-item
                  :label="$t('orderMod.materialPlanNo')"
                  :label-width="formLabelWidth"
                >
                  <el-input
                    v-model="filterForm.materialSchNum"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col style="padding-top: 22px">
                <MImport
                  style="display: inline-block; margin: 0 15px"
                  :title="$t('common.import')"
                  :up-load-url="iModalDetail.upLoadUrl"
                  :extra-data="extraDataDetail"
                  @downloadTemplate="downloadTemplateDetail"
                  @handleSuccess="handleSuccessDetail"
                />
                <el-button
                  type="primary"
                  @click="exportData"
                >
                  {{
                    this.$t('common.export')
                  }}
                </el-button>
              </el-col>
              <el-col><p /></el-col>
            </el-row>
          </el-form>
        </div>

        <el-table
          :data="displayMaterialItem"
          style="width: 100%"
          border
          height="251px"
          highlight-current-row
        >
          <el-table-column
            align="center"
            type="index"
            width="50"
          />
          <el-table-column
            align="center"
            :formatter="formatDate"
            prop="schMonthlyDate"
            :label="$t('orderMod.planArrivalData')"
            min-width="150"
            :show-overflow-tooltip="true"
          />
          <el-table-column
            align="center"
            prop="requirementQuantity"
            :label="$t('orderMod.buyerOrderSynergy.requirementQuantity')"
            min-width="150"
            :show-overflow-tooltip="true"
          >
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.requirementQuantity"
                v-input-format="{ type: 'number' }"
                :disabled="isReadonly"
              />
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="remark"
            :label="$t('purchaseDemand.comments')"
            min-width="150"
            :show-overflow-tooltip="true"
          >
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.remark"
                :disabled="isReadonly"
              />
            </template>
          </el-table-column>
          <el-table-column
            :label="$t('common.operation')"
            width="60"
          >
            <template slot-scope="scope">
              <el-button
                type="text"
                :disabled="isReadonly"
                @click="saveOneItem(scope.$index, scope.row)"
              >
                {{ $t('common.save') }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <div
          slot="footer"
          class="dialog-footer"
        >
          <el-button @click="dialogFormVisible = false">
            {{ $t('common.close') }}
          </el-button>
        </div>
      </srm-dialog>
      <!--模版下载弹框-->
      <srm-dialog
        :title="$t('components.importOrExportDialog.filterConditions')"
        size="small"
        class="the_follow_tender_dialog"
        :visible.sync="dialogVisible"
        :close-on-click-modal="false"
      >
        <el-row>
          <el-col :span="18">
            <span>{{ $t('orderMod.planMonth') }}</span>
            <el-date-picker
              v-model="monthlySchDate"
              type="month"
              format="yyyy-MM"
              value-format="yyyy-MM"
            />
          </el-col>
        </el-row>
        <el-row>
          <el-col
            :span="12"
            :offset="12"
          >
            <el-button
              @click="dialogVisible = false"
            >
              {{
                $t('common.cancel')
              }}
            </el-button>
            <el-button
              type="primary"
              @click="downloadTemplate"
            >
              {{
                $t('common.confirm')
              }}
            </el-button>
          </el-col>
        </el-row>
      </srm-dialog>
    </el-main>
  </el-container>
</template>
<script>
import MImport from 'lib@/components/import'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { parseTime, adaptDictData } from '@/utils'
import OrganizationSelector from 'lib@/components/organization-selector'
import { downloadFileLink, downloadFileLinkByPost } from 'lib@/utils/file'

export default {
  name: 'MaterialPlan',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    OrganizationSelector,
    MImport
  },
  provide () {
    return { context: this }
  },
  data () {
    return {
      name: '',
      downType: null,
      monthlySchDate: null,
      dialogVisible: false,
      iModalDetail: {
        upLoadUrl: '/api-sup-ce/material/materialItem/importDetailExcel'
      },
      iModal: {
        title: this.$t('common.excelImport'),
        upLoadUrl: '/api-sup-ce/material/materialItem/importExcel'
      },
      extraData: {
        fileModular: 'sup-ce',
        fileFunction: 'vendorDeliverPlan',
        fileType: 'excel'
      },
      tableName: 'materialPlan_vendor',
      reviewFormNumber: '',
      gridData: [],
      pageSize: 15,
      gridId: 'list',
      selectList: [],
      currentRow: null,
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      statusList: [],
      globalMaterialItemId: null,
      form: {
        id: '',
        vendorCode: '',
        vendorCompanyName: '',
        reviewFormNumber: '',
        enabled: ''
      },
      filterForm: {
        materialCode: null,
        materialName: null,
        orgName: null,
        organizationName: null,
        orderNumber: null,
        schType: null,
        organizationSite: null,
        orderStatus: 'ACCEPT'
      },
      rules: {
        vendorCode: [{ required: true, message: this.$t('bidMod.msgDictCode') }],
        vendorCompanyName: [{ required: true, message: this.$t('bidMod.msgDictName') }]
      },
      isReadonly: false,
      displayMaterialItem: [],
      dialogFormVisible: false,
      formLabelWidth: '100px',
      preArr: [
        {
          prop: 'orgIds',
          label: this.$t('bid_mod.businessEntity'),
          type: 'OUorganizationSelector',
          multiple: true
        },
        {
          prop: 'organizationIds',
          parentId: 'orgIds',
          label: this.$t('bid_mod.inv'),
          type: 'INVorganizationSelector',
          multiple: true
        },
        {
          prop: 'organizationSite',
          label: this.$t('bid_mod.tradingLocations')
        },
        {
          prop: 'materialSchNum',
          label: () => this.$t('orderMod.materialPlanNo')
        },
        {
          prop: 'materialCode',
          label: () => this.$t('purchaseDemand.itemCode'),
          type: 'quicksearch',
          showKey: 'materialCode',
          name: 'scc_base_material_item'
        },
        {
          prop: 'categoryIds',
          label: this.$t('dataConfMod.categoryLittle'),
          type: 'quicksearch',
          showKey: 'categoryName',
          propKey: 'categoryId',
          name: 'scc_base_purchase_category2'
        },
        {
          prop: 'schType',
          label: this.$t('orderMod.planStatus'),
          type: 'dict',
          code: 'MATERIA_LITEM_TYPE'
        },
        {
          prop: 'monthlySchDate',
          label: this.$t('orderMod.planMonth'),
          type: 'month'
        }
      ],
      queryParam: {},
      ableSelectTreeNodes: [],
      userType: this.$store.getters.userType // VENDOR | BUYER
    }
  },
  computed: {
    extraDataDetail () {
      return {
        materialItemId: this.filterForm.materialItemId,
        fileModular: 'sup-ce',
        fileFunction: 'materialPlan',
        fileType: 'excel'
      }
    }
  },
  created () {
    let _this = this
    this.tableHeader = [
      {
        prop: 'materialSchNum',
        label: this.$t('orderMod.materialPlanNo'),
        width: 150
      },
      {
        prop: 'monthlySchDate',
        label: this.$t('orderMod.planMonth'),
        width: 100
      },
      {
        prop: 'schType',
        label: this.$t('orderMod.planStatus'),
        width: 100,
        dataType: 'dict',
        code: 'MATERIA_LITEM_TYPE'
      },
      {
        prop: 'vendorName',
        label: this.$t('orderMod.buyerOrderSynergy.vendorName'),
        minWidth: 150
      },
      { prop: 'orgName', label: this.$t('dataConfMod.orgId'), width: 150 },
      { prop: 'organizationName', label: this.$t('bid_mod.inv'), width: 150 },
      {
        prop: 'organizationSite',
        label: this.$t('bid_mod.tradingLocations'),
        width: 100
      },
      {
        prop: 'categoryName',
        label: this.$t('dataConfMod.categoryLittle'),
        width: 100
      },
      {
        prop: 'materialCode',
        label: () => this.$t('orderMod.buyerOrderSynergy.materialCode'),
        width: 100
      },
      {
        prop: 'materialName',
        label: () => this.$t('orderMod.buyerOrderSynergy.materialName'),
        minWidth: 150
      },
      { prop: 'unit', label: this.$t('bid_mod.unit'), width: 100 },
      {
        prop: 'schTotalQuantity',
        label: this.$t('orderMod.totalPlanQuantity'),
        width: 120
      },
      {
        prop: 'operation',
        label: () => this.$t('common.operation'),
        width: 120,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          {
            callback: function (row) {
              this.readDetail(row)
            }.bind(this),
            code: 'ce:materialPlan:readDetail',
            formattor (val) {
              return _this.$t('orderMod.viewDetail')
            }
          },
          {
            callback: function (row) {
              this.publishOne(row)
            }.bind(this),
            code: 'ce:materialPlan:publishOne',
            formattor (val) {
              return _this.$t('common.publish')
            }, // 状态不等于【已发布】
            show: (row) => row.schType !== 'ISSUED'
          }
        ]
      }
    ]
    this.defaultTableHeader = [...this.tableHeader]
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  mounted () {
    // 供应商看到的数据只能是【已发布】
    if (this.userType === 'VENDOR') {
      this.preArr = [
        { prop: 'orgId', label: this.$t('bid_mod.businessEntity') },
        {
          prop: 'organizationId',
          label: this.$t('dataConfMod.organizationId')
        },
        {
          prop: 'organizationSite',
          label: this.$t('bid_mod.tradingLocations')
        },
        { prop: 'materialSchNum', label: this.$t('orderMod.materialPlanNo') },
        { prop: 'categoryName', label: this.$t('dataConfMod.categoryLittle') },
        {
          prop: 'monthlySchDate',
          label: this.$t('orderMod.planMonth'),
          type: 'month'
        }
      ]
    }
  },
  methods: {
    exportData () {
      if (this.filterForm.materialItemId) {
        downloadFileLink(
          '/api-sup-ce/material/materialItem/exportDetail?materialItemId=' + this.filterForm.materialItemId,
          parseTime(new Date()) + this.$t('orderMod.materialPlanMaintainExp')
        ).catch((err) => {
          this.$message.error(err.message)
        })
      }
    },
    getQuerydata (v) {
      this.queryParam = v || {}
      if (this.userType === 'VENDOR') {
        // 供应商看到的数据只能是【已发布】
        this.queryParam.schType = 'ISSUED'
      }
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    saveOneItem (index, row) {
      this.$http({
        url: '/api-sup-ce/material/materialDetail/modify',
        method: 'POST',
        data: row,
        loading: true
      })
        .then((data) => {
          this.$message.success(this.$t('common.successSave'))
        })
        .catch((err) => {
          console.log(err)
        })
    },
    publishOne (row) {
      this.$http({
        url: '/api-sup-ce/material/materialItem/getmateriaPublish',
        method: 'GET',
        params: { id: row.materialItemId },
        loading: true
      })
        .then((data) => {
          this.$message.success(this.$t('common.success'))
          this.getQuerydata()
        })
        .catch((err) => {
          console.log(err)
        })
    },
    detailExport () {},
    handleSuccess (val) {
      this.getQuerydata()
    },
    handleSuccessDetail (val) {
      this.readDetail({ materialItemId: this.globalMaterialItemId })
    },
    openDialogVisible (type) {
      // 下载模板
      this.downType = type
      this.dialogVisible = true
    },
    downloadTemplate () {
      if (!this.monthlySchDate) {
        this.$message.error(this.$t('orderMod.msgSelPlanMonthly'))
        return
      }
      if (this.downType === 'export') {
        let params = Object.assign(this.queryParam, {
          monthlySchDate: this.monthlySchDate
        })
        downloadFileLinkByPost(
          '/api-sup-ce/material/materialItem/export',
          parseTime(new Date()) + this.$t('orderMod.materialPlanExp'),
          params
        ).catch(() => {
          this.$message.error(this.$t('components.eio.downloadFail'))
        })
      } else if (this.downType === 'template') {
        downloadFileLink(
          '/api-sup-ce/material/materialItem/importModelDownload?monthlySchDate=' +
            this.monthlySchDate,
          new Date().getTime() + this.$t('dataConfMod.expTemplateXLSX')
        ).catch((err) => {
          this.$message.error(err.message)
        })
      }
    },
    downloadTemplateDetail () {
      downloadFileLink(
        '/api-sup-ce/material/materialItem/importModelDetailDownload?materialItemId=' +
          this.filterForm.materialItemId,
        parseTime(new Date()) + this.$t('orderMod.materialPlanDetailImp')
      ).catch((err) => {
        this.$message.error(err.message)
      })
    },
    addNewOne () {
      this.dialogFormVisible = false
    },
    readDetail (row) {
      if (!row.materialItemId) return
      this.globalMaterialItemId = row.materialItemId
      this.$http({
        url: '/api-sup-ce/material/materialItem/getMaterialItemDetail',
        method: 'GET',
        params: { id: row.materialItemId },
        loading: true
      })
        .then((data) => {
          this.isReadonly = row.schType === 'ISSUED'
          this.filterForm = data.data.materialItem
          this.displayMaterialItem = data.data.materialDetailList
          this.dialogFormVisible = true
        })
        .catch((err) => {
          console.log(err)
        })
    }
  }
}
</script>
<style scoped lang="scss">
.the_materialItem_wrapper {
  .the_follow_tender_dialog .el-row {
    margin-bottom: 11px;
    .el-col > span {
      padding-right: 11px;
    }
  }
}
</style>
