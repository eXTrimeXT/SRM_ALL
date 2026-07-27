<template>
  <el-container
    class="flex-container the_vendorEffect_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />
      <MainHeader>
        <template slot="left">
          <el-button
            type="primary"
            @click="addTab"
          >
            {{ $t('common.add') }}
          </el-button>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-sup/demotion/company-demotion/listPageByParam"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import vendorDemotionDetail from './vendorDemotionDetail'
import { performanceManagement } from 'modb@/performanceManagement/api/index'

export default {
  name: 'VendorDemotionList',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      globalNickname: '',
      tableName: 'vendorDemotionList',
      defaultTableHeader: [],
      indicatorsStatus: [], // 指标状态 INDICATORS_STATUS
      indicatorsType: [], // 指标类型 INDICATORS_TYPE
      indicatorsDim: [], // 指标维度
      demotionTypeList: [], // 供应商升降级类型
      pageSize: 15,
      gridId: 'vendorDemotionList',
      selectList: [],
      currentRow: null,
      tableHeader: [],
      tableData: [],
      statusList: [],
      preArr: [
        { prop: 'demotionNumber', label: () => this.$t('vendorMod.relegation.receiptNum') },
        { prop: 'demotionName', label: () => this.$t('vendorMod.relegation.billName') },
        {
          prop: 'categoryId',
          label: () => this.$t('vendorMod.relegation.dropCategory'),
          type: 'quicksearch',
          showKey: 'categoryName',
          propKey: 'categoryId',
          name: 'scc_base_purchase_category2'
        },
        {
          prop: 'status',
          label: () => this.$t('vendorMod.relegation.documentStatus'),
          type: 'dict', // 字典类型
          code: 'APPROVE_STATUS_TYPE'
        },
        {
          prop: 'companyId',
          label: () => this.$t('vendorMod.relegation.degradedSupplier'),
          type: 'quicksearch',
          showKey: 'companyName',
          propKey: 'companyId',
          name: 'scc_sup_company_info_all'
        },
        // { prop: 'performanceNumber', label: () => this.$t('vendorMod.relegation.associatedNum') },
        { prop: 'createdBy',
          label: () => this.$t('vendorMod.relegation.creator'),
          type: 'quicksearch',
          showKey: 'nickname',
          propKey: 'username',
          name: 'scc_rbac_user_display'
        },
        {
          prop: 'demotionType',
          label: () => this.$t('vendorMod.relegation.demotionType'),
          type: 'dict', // 字典类型
          code: 'DEMOTION_TYPE' // 字典code
        }
      ],
      queryParam: {}
    }
  },
  created () {
    this.globalNickname = this.$store.getters.userInfo
      ? this.$store.getters.userInfo.username
      : null
    let _this = this
    this.tableHeader = [
      {
        prop: 'demotionNumber',
        label: _this.$t('vendorMod.relegation.receiptNum'),
        width: 120,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.readTab(row)
        }.bind(this)
      },
      { prop: 'demotionName', label: _this.$t('vendorMod.relegation.billName'), minWidth: 120 },
      {
        prop: 'companyName',
        label: _this.$t('vendorMod.relegation.relegationVendor'),
        minWidth: 150
      },
      {
        prop: 'performanceNumber',
        label: _this.$t('vendorMod.relegation.associatedNum'),
        width: 120
      },
      {
        prop: 'demotionType',
        label: _this.$t('vendorMod.relegation.relegationType'),
        width: 120,
        dataType: 'dict', // 数据类型为字典
        code: 'DEMOTION_TYPE' // 字典code
      },
      {
        prop: 'status',
        label: _this.$t('vendorMod.relegation.state'),
        width: 100,
        dataType: 'dict', // 数据类型为字典
        code: 'APPROVE_STATUS_TYPE' // 字典code
      },
      {
        prop: 'createdUserName',
        label: _this.$t('vendorMod.relegation.creator'),
        width: 160,
        formattor: (val, row) => {
          return val ? `${row.createdFullName}(${val})` : ''
        }
      },
      { prop: 'creationDate', label: _this.$t('vendorMod.relegation.creationTime'), width: 150 },
      {
        prop: 'operation',
        label: () => this.$t('common.operation'),
        width: 150,
        btnStyle: 'text',
        fixed: 'right',
        showType: 'buttons',
        buttons: [
          {
            callback: function (row) {
              this.editRowData(row)
            }.bind(this),
            formattor (row) {
              return _this.$t('vendorMod.relegation.edit')
            },
            show: (row) =>
              ['DRAFT', 'REJECTED', 'WITHDRAW'].includes(row.status) &&
              row.createdBy === this.globalNickname
          },
          {
            callback: function (row) {
              this.delRowData(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('vendorMod.relegation.delete')
            },
            show: (row) => ['DRAFT'].includes(row.status) && row.createdBy === this.globalNickname
          },
          {
            callback: function (row) {
              this.approveRowData(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('vendorMod.relegation.examineApprove')
            },
            code: 'sup:vendorDemotion:approveRowData',
            show: (row) =>
              ['SUBMITTED'].includes(row.status) && row.createdBy === this.globalNickname
          },
          {
            callback: function (row) {
              this.handleRowData(row)
            }.bind(this),
            formattor (row) {
              return _this.$t('vendorMod.relegation.abandon')
            },
            show: function (row) {
              if (row.enableFlag === 'N' && row.createdBy === this.globalNickname) {
                return true
              } else {
                return false
              }
            }
          }
        ]
      }
    ]
    this.defaultTableHeader = _this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    getQuerydata (v) {
      this.queryParam = v || this.queryParam
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    syncFilterParams (values) {
      this.queryParam = values
    },
    // 编辑tab
    addTab () {
      this.$emit('tab-add', {
        component: vendorDemotionDetail,
        params: {
          flag: 'add',
          tabName: 'vendorDemotionDetail'
        },
        title: this.$t('vendorMod.relegation.newRelegation'),
        name: 'vendorDemotionDetail'
      })
    },
    readTab (row) {
      this.$emit('tab-add', {
        component: vendorDemotionDetail,
        params: {
          flag: 'view',
          orderId: row.companyDemotionId,
          tabName: 'vendorDemotionDetail' + row.companyDemotionId
        },
        title: row.demotionNumber,
        name: 'vendorDemotionDetail' + row.companyDemotionId
      })
    },
    editRowData (row) {
      this.$emit('tab-add', {
        component: vendorDemotionDetail,
        params: {
          flag: 'edit',
          orderId: row.companyDemotionId,
          tabName: 'vendorDemotionDetail' + row.companyDemotionId
        },
        title: row.demotionNumber,
        name: 'vendorDemotionDetail' + row.companyDemotionId
      })
    },
    // 删除数据
    delRowData (row) {
      this.$confirm(this.$t('vendorMod.relegation.deleteData'), {
        confirmButtonText: this.$t('vendorMod.relegation.affirm'),
        cancelButtonText: this.$t('vendorMod.relegation.abrogate'),
        type: 'warning'
      })
        .then(() => {
          this.$http({
            url: '/api-sup/demotion/company-demotion/deleteById',
            method: 'GET',
            params: { companyDemotionId: row.companyDemotionId },
            loading: true
          })
            .then((data) => {
              this.getQuerydata()
            })
            .catch((err) => {
              console.log(err)
            })
        })
        .catch(() => {})
    },
    approveRowData (row) {
      this.$http({
        url: '/api-sup/demotion/company-demotion/approve',
        method: 'GET',
        params: { companyDemotionId: row.companyDemotionId },
        loading: true
      })
        .then((data) => {
          this.getQuerydata()
        })
        .catch((err) => {
          console.log(err)
        })
    },
    handleRowData (row) {
      let params = {}
      params.companyDemotionId = row.companyDemotionId
      let tips = ''
      if (row.enableFlag === 'Y') {
        // 启用状态
        params.enableFlag = 'N'
        tips = this.$t('vendorMod.relegation.disableCurrentMetric')
      } else {
        // 启用状态
        params.enableFlag = 'Y'
        tips = this.$t('vendorMod.relegation.enableCurrentMetric')
      }
      this.$confirm(tips, {
        confirmButtonText: this.$t('vendorMod.relegation.affirm'),
        cancelButtonText: this.$t('vendorMod.relegation.abrogate'),
        type: 'warning'
      })
        .then(() => {
          performanceManagement.enableOrDisabledIndication(params).then((res) => {
            this.$message({
              message: res.message,
              type: 'success'
            })
            this.getQuerydata()
          })
        })
        .catch(() => {})
    },
    exportOne () {},
    handleCurrentChange (val) {
      this.currentRow = val
    }
  }
}
</script>
<style scoped lang="scss"></style>
