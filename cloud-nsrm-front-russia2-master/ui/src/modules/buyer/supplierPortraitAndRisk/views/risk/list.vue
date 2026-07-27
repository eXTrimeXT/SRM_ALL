<template>
  <el-container
    class="flex-container risk_list"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="queryForm"
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
            code="sup:risk:add"
            @click="edit({}, 'add')"
          >
            {{ $t("common.add") }}
          </AuthorityButton>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :row-index-fixed="false"
        :page-size="pageSize"
        :checkbox="false"
        :pre-query-data="queryParam"
        :source="riskApi.listPage"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import riskEdit from './edit'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import { riskApi } from 'modb@/supplierPortraitAndRisk/api'

export default {
  name: 'RiskList',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      riskApi: riskApi,
      defaultTableHeader: [],
      name: 'riskList',
      tableName: 'riskList',
      pageSize: 15,
      gridId: 'list',
      tableHeader: [
        {
          label: () => this.$t('supRisk.riskCode'),
          prop: 'riskCode',
          width: 150,
          showType: 'button',
          btnStyle: 'text',
          align: 'left',
          callback: row => this.readOnly(row)
        },
        {
          label: () => this.$t('supRisk.categoryId'),
          prop: 'categoryName',
          width: 150
        },
        {
          label: () => this.$t('supRisk.vendorId'),
          prop: 'vendorName',
          width: 150
        },
        {
          label: () => this.$t('supRisk.riskType'),
          prop: 'riskType',
          dataType: 'dict',
          code: 'RISK_TYPE',
          width: 150
        },
        {
          label: () => this.$t('supRisk.riskDescription'),
          prop: 'riskDescription',
          width: 150
        },
        {
          label: () => this.$t('supRisk.status'),
          prop: 'status',
          dataType: 'dict',
          code: 'RISK_MONITORING_STATUS',
          width: 150
        },
        {
          label: () => this.$t('supRisk.riskImplementDesc'),
          prop: 'riskImplementDesc',
          width: 150
        },
        {
          label: () => this.$t('supRisk.createdName'),
          prop: 'createdName',
          width: 150
        },
        {
          label: () => this.$t('supRisk.creationDate'),
          prop: 'creationDate',
          width: 150,
          dataType: 'dateTime'
        },
        {
          prop: 'operation',
          label: () => this.$t('common.operation'),
          showType: 'buttons',
          btnStyle: 'text',
          fixed: 'right',
          width: 130,
          buttons: [
            {
              callback: row => this.edit(row), // 编辑
              show: row => row.status === 'ADD',
              code: 'sup:risk:edit',
              formattor: () => {
                return this.$t('common.edit')
              }
            },
            {
              callback: row => this.riskUpdate(row),
              show: row => row.status === 'MONITORING',
              code: 'sup:risk:riskUpdate',
              formattor: () => {
                return this.$t('supRisk.riskUpdate') // 进度更新
              }
            },
            {
              callback: row => this.addPass(row),
              show: row => row.status === 'APPROVAL_ADD',
              code: 'sup:risk:addPass',
              formattor: () => {
                return this.$t('supRisk.addPass') // 新建审批通过
              }
            },
            {
              callback: row => this.closePass(row),
              show: row => row.status === 'APPROVAL_CLOSE',
              code: 'sup:risk:closePass',
              formattor: () => {
                return this.$t('supRisk.closePass') // 关闭审批通过
              }
            },
            {
              callback: row => this.riskClose(row),
              show: row => row.status === 'MONITORING',
              code: 'sup:risk:riskClose',
              formattor: () => {
                return this.$t('supRisk.riskClose') // 风险关闭
              }
            },
            {
              callback: row => this.deleteItem(row),
              code: 'sup:risk:deleteItem',
              show: row => row.status === 'ADD',
              formattor: () => {
                return this.$t('common.delete') // 删除
              }
            }
          ]
        }
      ],
      tableData: [],
      queryForm: [
        {
          prop: 'categoryId',
          type: 'quicksearch',
          showKey: 'categoryId',
          name: 'scc_base_purchase_category',
          label: () => this.$t('supRisk.categoryId')
        },
        {
          prop: 'vendorId',
          type: 'quicksearch',
          showKey: 'companyName',
          propKey: 'companyId',
          name: 'scc_sup_company_info',
          label: () => this.$t('supRisk.vendorId')
        },
        {
          prop: 'status',
          label: () => this.$t('supRisk.status'),
          type: 'dict',
          code: 'RISK_MONITORING_STATUS'
        },
        {
          prop: 'createdName',
          label: () => this.$t('supRisk.createdName'),
          type: 'quicksearch',
          showKey: 'nickname',
          propKey: 'nickname',
          name: 'scc_rbac_user_display'
        },
        {
          prop: 'riskType',
          label: () => this.$t('supRisk.riskType'),
          type: 'dict',
          code: 'RISK_TYPE'

        },
        {
          prop: 'riskLevel',
          label: () => this.$t('supRisk.riskLevel'),
          type: 'dict',
          code: 'RISK_LEVEL'
        },
        {
          prop: 'startDate',
          type: 'date',
          label: () => this.$t('supRisk.startDate')
        },
        {
          prop: 'endDate',
          type: 'date',
          label: () => this.$t('supRisk.endDate')
        }
      ],
      queryParam: {}
    }
  },
  computed: {
  },
  watch: {
    $route: {
      handler (nVal) {
        let { from, funName, row } = nVal.params
        if (from === 'fromFun' && funName === 'risk') {
          this.readOnly(row)
        }
      },
      immediate: true,
      deep: true
    }
  },
  provide () {
    return { context: this }
  },
  mounted () {
    this.getQuerydata()
  },
  created () {
    this.defaultTableHeader = this.tableHeader
  },
  methods: {
    riskUpdate (row) {
      this.edit(row, 'update')
    },
    addPass ({ riskMonitoringId }) {
      riskApi.addPass(riskMonitoringId).then(res => {
        this.$message.success(res.message)
        this.getQuerydata()
      })
    },
    closePass ({ riskMonitoringId }) {
      riskApi.closePass(riskMonitoringId).then(res => {
        this.$message.success(res.message)
        this.getQuerydata()
      })
    },
    riskClose (row) {
      this.edit(row, 'close')
    },
    readOnly (row) {
      let name = 'riskEdit' + row.riskCode
      const tab = {
        component: riskEdit,
        params: {
          row,
          flag: 'edit',
          readOnly: true,
          tabName: name
        },
        title: `${this.$t('supRisk.readOnly')}-${row.riskCode}`,
        name
      }
      this.$emit('tab-add', tab)
    },
    deleteItem ({ riskMonitoringId }) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          riskApi.deleteItem(riskMonitoringId).then(res => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
        })
        .catch(() => {})
    },
    edit (row, flag = 'edit') {
      let name = 'riskEdit' + (flag !== 'add' ? row.riskMonitoringId : '')
      const tab = {
        component: riskEdit,
        params: {
          row,
          flag,
          tabName: name
        },
        title: `${this.$t(`supRisk.${flag}`)}${
          flag === 'add' ? '' : `-${row.riskCode}`
        }`,
        name
      }
      this.$emit('tab-add', tab)
    },
    getQuerydata (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    }
  }
}
</script>
<style scoped lang="scss">
.risk_list {
  .order-uploader {
    display: inline-block;
    margin: 0 10px;
  }
  .block {
    display: flex;
    justify-content: center;
  }
}
</style>
