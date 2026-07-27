<template>
  <el-container
    class="flex-container the_dictionary_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="queryForm"
        @getFormData="getQuerydata"
      />
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
<!--          <el-button-->
<!--            type="primary"-->
<!--            @click="openRiskDialog"-->
<!--          >-->
<!--            {{ $t('vendorMod.bulkUpdateRisks') }}-->
<!--          </el-button>-->
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :checkbox="true"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-sup/info/companyInfo/listPageByDTO"
      />

      <el-dialog
        :title="$t('vendorMod.listRiskInterfaces')"
        width="500px"
        :visible.sync="riskDialog"
        :close-on-click-modal="false"
      >
        <div class="the_display_table">
          <el-table
            :data="displayRiskList"
            style="width: 100%"
            border
            max-height="200px"
            @selection-change="setCurrentRows"
          >
            <el-table-column
              type="selection"
              width="55"
              fixed="left"
            />
            <el-table-column
              align="center"
              width="100"
              prop="value"
              :label="$t('vendorMod.interfaceCode')"
              show-overflow-tooltip
              fixed="left"
              sortable
            />
            <el-table-column
              align="center"
              min-width="100"
              prop="label"
              :label="$t('vendorMod.interfaceName')"
              show-overflow-tooltip
              fixed="left"
              sortable
            />
          </el-table>
        </div>
        <div
          slot="footer"
          class="dialog-footer"
        >
          <el-button
            type="primary"
            @click="batchUpdateVendor"
          >
            {{ $t('vendorMod.renewalProcess') }}
          </el-button>
        </div>
      </el-dialog>
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import vendorProfileDetail from './edit'
import { parseTime, adaptDictData } from '@/utils'
import { getDictItem } from '@/api/common'
export default {
  name: 'VendorProfileList',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  provide () {
    return { context: this }
  },
  data () {
    return {
      tableName: 'vendorProfileList',
      defaultTableHeader: [],
      name: '',
      reviewFormNumber: '',
      gridData: [],
      pageSize: 15,
      gridId: 'vendorProfileList',
      selectList: [],
      currentRow: null,
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      statusList: [],
      relations: [], // 境内外管理
      natureList: [], // 企业性质
      approveStatus: [], // 审批状态
      isModify: false,
      dialogFormVisible: false,
      formLabelWidth: '100px',
      queryForm: [],
      riskDialog: false,
      displayRiskList: [],
      currentBatchRisk: [],
      currentRows: [],
      queryParam: {}
    }
  },
  created () {
    let _this = this
    this.queryForm = [
      {
        prop: 'companyName',
        label: () => this.$t('common.vendorName'), // '供应商名称'
        type: 'quicksearch',
        showKey: 'companyName',
        name: 'scc_sup_company_info_all'
      },
      {
        prop: 'lcCode',
        label: () => this.$t('vendorMod.lcCode') // '社会统一信用代码'
      },

      {
        prop: 'isBacklist',
        label: () => this.$t('vendorMod.isBacklist'), // '是否黑名单'
        type: 'dict',
        code: 'YES_OR_NO'
      },
      {
        prop: 'overseasRelation',
        label: () => this.$t('vendorMod.overseasRelation'), // '境内外关系'
        type: 'dict',
        code: 'RELATION'
      },
      {
        prop: 'dateList',
        label: () => this.$t('vendorMod.permitDate'), // '准入日期'
        type: 'daterange'
      }
    ]
    _this.tableHeader = [
      {
        prop: 'companyCode',
        label: () => this.$t('common.vendorCode'), // '供应商编码'
        minWidth: 120
      },
      {
        prop: 'companyName',
        label: () => this.$t('common.vendorName'), // '供应商名称'
        minWidth: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.editTab('edit', row)
        }.bind(this)
      },
      {
        prop: 'overseasRelation',
        label: () => this.$t('vendorMod.overseasRelation'), // '境内外关系'
        width: 150,
		    dataType: 'dict',
        code: 'RELATION'

      },
      // {
      //   prop: "companyType",
      //   label: () => this.$t("vendorMod.companyType"), //'企业性质'
      //   width: 120,
      //   formattor(val) {
      //     return _this.$getDictLabelByValue(_this.natureList, val);
      //   },
      // },
      {
        prop: 'lcCode',
        label: () => this.$t('vendorMod.lcCode'), // '社会统一信用代码'
        width: 150
      },
      // {
      //   prop: "legalPerson",
      //   label: () => this.$t("vendorMod.legalPerson"), //'法定代表人'
      //   width: 100,
      // },
      {
        prop: 'isBacklist',
        label: () => this.$t('vendorMod.isBacklist'), // '是否黑名单'
        width: 120,
        dataType: 'dict',
        code: 'YES_OR_NO'
      },
      // {
      //   prop: "status",
      //   label: () => this.$t("vendorMod.approveStatus"), //'审批状态'
      //   width: 80,
      //   formattor(val) {
      //     return _this.$getDictLabelByValue(_this.approveStatus, val);
      //   },
      // },
      {
        prop: 'approvedDate',
        label: () => this.$t('vendorMod.permitDate'), // '准入日期'
        width: 120
      },
      {
        prop: 'dataSources',
        label: () => this.$t('vendorMod.sourceDocuments'), // '数据来源'
        minWidth: 100,
        dataType: 'dict',
        code: 'DATA_SOURCE'
      }
    ]
    this.defaultTableHeader = _this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    getQuerydata (v) {
      const { dateList, ...rest } = v || this.queryParam
      let params = { ...rest }
      if (dateList) {
        const [startDate, endDate] = dateList
        params = { ...rest, startDate, endDate }
      }
      this.queryParam = Object.assign({ dataSources: 'IMAGE' }, params)
      delete this.queryParam.dateList
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange (val) {
      this.currentRows = val
    },
    setCurrentRows (val) {
      this.currentBatchRisk = val
    },
    openRiskDialog () {
      if (this.currentRows.length === 0) {
        return this.$message.error(this.$t('purSettlementMod.tickData'))
      }
      getDictItem('RAIDER_CODES').then((res) => {
        this.displayRiskList = adaptDictData(res.data, 'dict')
        this.riskDialog = true
      })
    },
    batchUpdateVendor () {
      if (this.currentBatchRisk.length === 0) {
        return this.$message.error(this.$t('purSettlementMod.tickData'))
      }
      this.$http({
        url: '/api-sup/sup/raiderInfo/saveOrUpdateBatch',
        method: 'POST',
        data: {
          'companyIds': this.currentRows.map(c => c.companyId),
          'interfaceCodes': this.currentBatchRisk.map(v => v.value)
        },
        loading: true
      })
        .then(data => {
          this.$message.success(this.$t('common.success'))
          this.riskDialog = false
          this.getQuerydata()
        })
        .catch(err => {
          console.log(err)
        })
    },
    // 获取数据字典
    // 编辑编辑tab
    editTab (type, row) {
      // 修改
      let companyId = row.companyId
      const tab = {
        component: vendorProfileDetail,
        params: {
          row: row,
          flag: 'edit',
          companyId: companyId,
          tabName: 'vendorProfileDetail' + row.companyName
        },
        title: row.companyName,
        name: 'vendorProfileDetail' + row.companyName
      }
      this.$emit('tab-add', tab)
    }
  }
}
</script>
<style scoped lang="scss">
</style>
