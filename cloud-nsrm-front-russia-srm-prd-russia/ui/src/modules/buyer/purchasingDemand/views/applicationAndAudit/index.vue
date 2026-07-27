<template>
  <el-container
    class="flex-container-notab the_applicationAndAudit_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        :select-dictionary="selectDictionary"
        form-label-width="120px"
        @getFormData="getQuerydata"
      />

      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <AuthorityButton
            code="pr:requirementApply:add"
            type="primary"
            @click="add"
          >
            {{ $t("purchaseDemand.add") }}
          </AuthorityButton>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :checkbox="false"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :source="applicationAndAuditApi.requirementHeadListPage"
      />
    </el-main>
    <srm-dialog
      :visible.sync="visible"
      :title="title"
      size="large"
      :close-on-click-modal="false"
    >
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <el-button
            v-if="!justShow"
            type="primary"
            @click="addItem"
          >
            {{ $t("common.add") }}
          </el-button>
          <MImport
            v-if="!justShow"
            ref="import"
            style="display: inline-block;margin-left: 15px;"
            :title="$t('common.import')"
            :up-load-url="upLoadUrl"
            @downloadTemplate="downloadTemplate"
            @handleSuccess="handleSuccess"
          />
          <!-- <el-button
            v-if="!justShow"
            type="primary"
            @click="exportItem"
            >{{ $t("common.import") }}</el-button
          > -->
          <!-- <el-button type="primary" @click="deleteItem">{{
            $t("common.delete")
          }}</el-button> -->
        </template>
      </MainHeader>
      <el-form
        :inline="true"
        :model="requirementHead"
      >
        <el-form-item
          :label="$t('purchaseDemand.requirementHeadNum')"
          prop="requirementHeadNum"
        >
          <el-input
            v-model="requirementHead.requirementHeadNum"
            disabled
          />
        </el-form-item>
        <el-form-item
          prop="auditStatus"
          :label="$t('purchaseDemand.auditStatus')"
        >
          <el-select
            v-model="requirementHead.auditStatus"
            disabled
          >
            <el-option
              v-for="item in orderForecastStatusOpts"
              :key="item.id"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item
          prop="createdFullName"
          :label="$t('purchaseDemand.createdFullName')"
        >
          <el-input
            v-model="requirementHead.createdFullName"
            disabled
          />
        </el-form-item>
      </el-form>
      <tableForm
        v-if="visible"
        ref="child"
        :table-data="tableData2"
        :just-show="justShow"
      />
      <div
        slot="footer"
        class="dialog-footer"
      >
        <el-button
          v-if="!justShow"
          type="primary"
          @click="saveItem"
        >
          {{ $t("common.submit") }}
        </el-button>
        <!-- <el-button v-if="!justShow" type="primary" @click="submitAudit">
          {{ $t("purchaseDemand.submitAudit") }}
        </el-button> -->
      </div>
    </srm-dialog>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { parseTime, adaptDictData } from '@/utils'
import { getDictItemList, getAllPurTax } from '@/api/common'
import tableForm from './tableForm'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import { orderForecastSupplierApi } from 'mods@/planManagementSupplier/api'
import { applicationAndAuditApi } from 'modb@/purchasingDemand/api'

export default {
  name: 'ApplicationAndAudit',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    tableForm,
    MImport
  },
  provide () {
    return { context: this }
  },
  data () {
    return {
      name: '',
      upLoadUrl: '/api-sup-ce/pr/requirementLine/excelImport',
      tableName: 'applicationAndAudit',
      reviewFormNumber: '',
      selectDictionary: {},
      gridData: [],
      taxList: [],
      pageSize: 15,
      title: this.$t('purchaseDemand.add'),
      gridId: 'list',
      selectList: [],
      currentRows: [],
      currentRow: null,
      showFilterBar: 1,
      tableHeader: [],
      requirementHead: {},
      tableData: [],
      tableData2: [],
      statusList: [],
      currentTableRows: [],
      orderForecastStatusOpts2: [],
      orderForecastStatusOpts: [],
      form: {
        id: '',
        vendorCode: '',
        vendorCompanyName: '',
        reviewFormNumber: '',
        enabled: ''
      },
      isModify: false,
      visible: false,
      justShow: false,
      formLabelWidth: '100px',
      preArr: [
        // {
        //   prop: "materialCode",
        //   label: () => this.$t("purchaseDemand.itemCode"),
        //   type: "quicksearch",
        //   showKey: "materialCode",
        //   name: "scc_base_material_item"
        // },
        // {
        //   prop: "materialName",
        //   label: () => this.$t("purchaseDemand.itemName"),
        //   type: "quicksearch",
        //   showKey: "materialName",
        //   name: "scc_base_material_item"
        // },
        { prop: 'auditStatus', label: () => this.$t('purchaseDemand.auditStatus'), type: 'select' },
        { prop: 'handleStatus', label: () => this.$t('purchaseDemand.handleStatus'), type: 'select' },
        { prop: 'createdFullName', label: () => this.$t('purchaseDemand.createdFullName') },
        { prop: 'requirementHeadNum', label: () => this.$t('purchaseDemand.requirementHeadNum') }
      ],
      queryParam: {}
    }
  },
  created () {
    this.tableHeader = [
      {
        prop: 'auditStatus',
        label: () => this.$t('purchaseDemand.auditStatus'),
        formattor: val => this.getLabel(this.orderForecastStatusOpts, val)
      },
      {
        prop: 'handleStatus',
        label: () => this.$t('purchaseDemand.handleStatus'),
        formattor: val => this.getLabel(this.orderForecastStatusOpts2, val)
      },
      {
        prop: 'requirementHeadNum',
        label: () => this.$t('purchaseDemand.requirementHeadNum'),
        width: 140,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.showDetail(row)
      },
      { prop: 'createdFullName', label: () => this.$t('purchaseDemand.createdFullName'), width: 100 },
      {
        prop: 'applyDate',
        label: () => this.$t('purchaseDemand.applyDate'),
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      // { prop: "rejectReason", label: () => this.$t("purchaseDemand.rejectReason") },
      {
        prop: 'operation',
        label: () => this.$t('common.operation'),
        showType: 'buttons',
        btnStyle: 'text',
        fixed: 'right',
        width: 200,
        buttons: [
          {
            callback: row => this.edit(row),
            code: 'pr:requirementApply:edit',
            show: row => row.auditStatus === 'DRAFT',
            formattor: () => {
              return this.$t('common.edit')
            }
          },
          {
            callback: row => this.submitAudit(row),
            code: 'pr:requirementApply:submitAudit',
            show: row => row.auditStatus === 'DRAFT',
            formattor: () => {
              return this.$t('purchaseDemand.submitAudit')
            }
          },
          {
            callback: row => this.deleteRow(row),
            code: 'pr:requirementApply:delete',
            show: row => row.auditStatus === 'ABANDONED',
            formattor: () => {
              return this.$t('common.delete')
            }
          },
          {
            callback: row => this.cancelRow(row),
            code: 'pr:requirementApply:cancel',
            show: row =>
              row.auditStatus !== 'AUDITED' && row.auditStatus !== 'ABANDONED',
            formattor: () => {
              return this.$t('common.cancel')
            }
          },
          {
            callback: row => this.confirm(row),
            code: 'pr:requirementApply:confirm',
            show: row => row.auditStatus === 'SUBMITTED',
            formattor: () => {
              return this.$t('purchaseDemand.confirm')
            }
          },
          {
            callback: row => this.refuse(row),
            code: 'pr:requirementApply:refuse',
            show: row => row.auditStatus === 'SUBMITTED',
            formattor: () => {
              return this.$t('purchaseDemand.refuse')
            }
          }
        ]
      }
    ]
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  mounted () {
    this.fetchDictionary()
  },
  methods: {
    handleSuccess ({ data }, file, fileList) {
      this.$refs.child.$emit('import', data)
    },
    downloadTemplate () {
      applicationAndAuditApi.excelExport(
        this.currentRow ? this.currentRow.requirementHeadId : null,
        this.$t('purchaseDemand.purchaseDemandTmportTemplate') + '.xls'
      )
    },
    refuse (row) {
      this.$prompt(
        this.$t('purchaseDemand.rejectPrompt'),
        this.$t('purchaseDemand.rejectPromptTitle'),
        {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel')
        }
      )
        .then(({ value }) => {
          // 因菜单失效，且接口缺失，暂时注释处理
          // this.$api.pr
          //   .reject({
          //     requirementHeadId: row.requirementHeadId,
          //     rejectReason: value
          //   })
          //   .then(res => {
          //     this.$message({
          //       type: 'success',
          //       message: res.message
          //     })
          //     this.getQuerydata()
          //   })
        })
        .catch(() => {})
    },
    confirm (row) {
      applicationAndAuditApi.approval(row.requirementHeadId).then(res => {
        this.$message({
          type: 'success',
          message: res.message
        })
        this.getQuerydata()
      })
    },
    add () {
      this.visible = true
      this.isModify = false
      this.justShow = false
      this.title = this.$t('purchaseDemand.addTitle')
      this.tableData2 = null
      this.requirementHead = {}
    },
    submitAudit (row) {
      applicationAndAuditApi.submitApproval(row.requirementHeadId).then(res => {
        this.$message({
          type: 'success',
          message: res.message
        })
        this.getQuerydata()
      })
    },
    edit (row) {
      this.title = this.$t('purchaseDemand.editTitle')
      this.isModify = true
      this.justShow = false
      // this.visible = true;
      this.currentRow = row
      this.completeInfo(row.requirementHeadId)
    },
    getLabel (dictionary = [], val) {
      const labelOpt = dictionary.find(i => i.value === val)
      if (labelOpt) return labelOpt.label
      return val
    },
    fetchDictionary () {
      // 获取所有税率
      getAllPurTax().then(res => {
        this.taxList = adaptDictData(res.data, 'tax')
      })
      const dictionaryCodes = ['APPROVAL_STATUS', 'PROCESSING_STATUS'].map(
        i => ({
          dictCode: i
        })
      )
      getDictItemList(dictionaryCodes).then(res => {
        const [APPROVAL_STATUS, PROCESSING_STATUS] = res.data
        this.orderForecastStatusOpts = adaptDictData(
          APPROVAL_STATUS.APPROVAL_STATUS
        )
        this.orderForecastStatusOpts2 = adaptDictData(
          PROCESSING_STATUS.PROCESSING_STATUS
        )
        this.selectDictionary = {
          auditStatus: this.orderForecastStatusOpts,
          handleStatus: this.orderForecastStatusOpts2
        }
      })
    },
    getQuerydata (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    checkChange (rows) {
      this.currentTableRows = rows
    },
    handleCurrentChange (val) {
      this.currentRows = val
    },
    submit () {
      const data = this.currentRows
      // 请选择要提交的计划！
      if (!data.length) {
        return this.$message({
          type: 'warning',
          message: this.$t('purchaseDemand.planSubmitTips')
        })
      }
      const submitStaus = ['PUBLISHED']
      if (
        this.currentRows.some(
          i => submitStaus.findIndex(j => j === i.status) === -1
        )
      ) {
        // 只有状态为已发布状态的才能提交！
        this.$message({
          type: 'warning',
          message: this.$t('purchaseDemand.statusSubmitTips')
        })
        return
      }
      const params = data.map(i => i.orderForecastId)
      orderForecastSupplierApi.comfirmBatch(params).then(res => {
        this.$message({ message: res.message, type: 'success' })
        this.getQuerydata()
      })
    },
    exportOne () {},
    addItem () {
      this.$refs.child.$emit('addItem')
    },
    exportItem () {},
    deleteItem (index, row) {
      this.tableData2.splice(index, 1)
    },
    deleteRow (row) {
      this.$confirm(this.$t('common.delRow'), this.$t('common.tips'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          applicationAndAuditApi.deleteByHeadId(row.requirementHeadId).then(() => {
            this.$message({
              type: 'success',
              message: this.$t('common.successDelete')
            })
            this.getQuerydata()
          })
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: this.$t('common.cancelDelete')
          })
        })
    },
    async cancelRow (row) {
      await applicationAndAuditApi.abandon(row.requirementHeadId)
      this.getQuerydata()
    },
    saveItem () {
      const requirementLineList = this.$refs.child.materialModle.tableData.map(
        ({ unit: unitCode, taxKey, ...rest }) => ({
          unitCode,
          taxKey,
          taxRate: (this.taxList.find(i => i.value === taxKey) || {}).key || '',
          ...rest
        })
      )
      console.log(requirementLineList)
      const flag = requirementLineList.some(
        i => i.totalAmount && i.totalAmount > i.budget
      )
      if (flag) {
        this.$message({
          type: 'error',
          message: this.$t('purchaseDemand.totalAmountTips')
        })
        return
      }
      this.$refs.child.$refs.materialTable.validate(status => {
        if (status) {
          if (this.isModify) {
            applicationAndAuditApi
              .modify({
                requirementHead: this.requirementHead,
                requirementLineList
              })
              .then(res => {
                this.$message({
                  type: 'success',
                  message: res.message
                })
                this.getQuerydata()
                this.visible = false
              })
          } else {
            applicationAndAuditApi
              .addPurchaseRequirement({
                requirementLineList
              })
              .then(res => {
                this.$message({
                  type: 'success',
                  message: res.message
                })
                this.getQuerydata()
                this.visible = false
              })
          }
        }
      })
    },
    showDetail (row) {
      this.justShow = true
      // this.visible = true;
      this.isModify = false
      this.title = this.$t('purchaseDemand.justShowTitle')
      this.completeInfo(row.requirementHeadId)
    },
    completeInfo (requirementHeadId) {
      applicationAndAuditApi.getByHeadId(requirementHeadId).then(res => {
        const { requirementLineList, requirementHead } = res.data
        this.tableData2 = requirementLineList
        this.requirementHead = requirementHead
        this.visible = true
      })
    }
  }
}
</script>
<style scoped lang="scss">
</style>
<style>
.vue-treeselect__portal-target {
  z-index: 9999 !important;
}
</style>
