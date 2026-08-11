<template>
  <el-container class="flex-container-notab bargain-flow-configure-wrapper" direction="vertical">
    <el-main>
      <FormWrapper
        :form-array="formWrapperArray"
        form-label-width="120px"
        @getFormData="getQueryData"
      />

      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <!-- 新增 -->
          <AuthorityButton
            type="primary"
            @click="addTableRowDialog('add')"
          >
            {{ $t('bidMod.addNewTemp') }}
          </AuthorityButton>
        </template>
      </MainHeader>

      <BaseTable
        ref="tableRef"
        stripe
        :data="tableData"
        :columns="tableHeader"
        :empty-text="$t('components.noData')"
        border
        height="250px"
        style="width: 100%"
        @deleteRow="deleteRow"
        @editTableRow="editTableRow"
        @activeRow="activeRow"
        @inactiveRow="inactiveRow"
      >
        <!-- 项目信息 -->
        <template #projectInfo="{ column, row }">
          <el-checkbox
            v-model="row[column.property]"
            true-label="Y"
            false-label="N"
            :disabled="true"
          />
        </template>
        <!-- 项目需求 -->
        <template #requireInfo="{ column, row }">
          <el-checkbox
            v-model="row[column.property]"
            true-label="Y"
            false-label="N"
            :disabled="true"
          />
        </template>
        <!-- 邀请供应商 -->
        <template #inviteVendor="{ column, row }">
          <el-checkbox
            v-model="row[column.property]"
            true-label="Y"
            false-label="N"
            :disabled="true"
          />
        </template>
        <!-- 流程审批 -->
        <template #createApproval="{ column, row }">
          <el-checkbox
            v-model="row[column.property]"
            true-label="Y"
            false-label="N"
            :disabled="true"
          />
        </template>
        <!-- 保证金管理 -->
        <template #bondManagement="{ column, row }">
          <el-checkbox
            v-model="row[column.property]"
            true-label="Y"
            false-label="N"
            :disabled="true"
          />
        </template>
        <!-- 报名管理 -->
        <template #signUpManagement="{ column, row }">
          <el-checkbox
            v-model="row[column.property]"
            true-label="Y"
            false-label="N"
            :disabled="true"
          />
        </template>
        <!-- 商务标管理 -->
        <template #businessManagement="{ column, row }">
          <el-checkbox
            v-model="row[column.property]"
            true-label="Y"
            false-label="N"
            :disabled="true"
          />
        </template>
        <!-- 竞价大厅 -->
        <template #auctHall="{ column, row }">
          <el-checkbox
            v-model="row[column.property]"
            true-label="Y"
            false-label="N"
            :disabled="true"
          />
        </template>
        <!-- 评选 -->
        <template #evaluation="{ column, row }">
          <el-checkbox
            v-model="row[column.property]"
            true-label="Y"
            false-label="N"
            :disabled="true"
          />
        </template>
      </BaseTable>

      <el-footer class="page-bar">
        <CPagination
          class="c-query-table-pagination"
          :total="pageInfo.total"
          :page-num="pageInfo.pageNum"
          :page-size="pageInfo.pageSize"
          @current-change="handlePagerCurrentChange"
          @size-change="handlePagerSizeChange"
        />
      </el-footer>

      <!--新增/编辑/查看配置-->
      <ConfigureDetailDialog
        v-if="configureDialogVisible"
        :visible.sync="configureDialogVisible"
        :dialog-type="dialogType"
        :edit-row="editRow"
        @success="getQueryData"
      />
    </el-main>
  </el-container>
</template>

<script>
import { comBuyerHttp } from 'modb@/souConfiguration/api'
import CPagination from 'lib@/components/c-pagination'
import ConfigureDetailDialog from './configureDetailDialog'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import BaseTable from 'lib@/components/BaseTable'
import { transformMQL } from 'lib@/utils/util'

export default {
  name: 'BargainFlowConfigure',

  components: {
    CPagination,
    FormWrapper,
    MainHeader,
    BaseTable,
    ConfigureDetailDialog
  },

  data () {
    return {
      pageInfo: {
        total: 0,
        pageNum: 1,
        pageSize: 15
      },
      tableData: [],
      formWrapperArray: [
        // 模板名称
        { prop: 'processConfigName', label: this.$t('dataConfMod.templateName') }
      ],
      configureDialogVisible: false,
      dialogType: 'add',
      editRow: {},
      queryParam: {},
      tableHeader: [
        {
          attrs: {
            type: 'index',
            // '序号'
            label: this.$t('components.common.sort'),
            width: '60',
            fixed: 'left'
          }
        },
        // 模板名称
        {
          attrs: {
            align: 'center',
            minWidth: '120',
            // '模板名称'
            label: this.$t('dataConfMod.templateName'),
            prop: 'processConfigName'
          }
        },
        // 竞价范围
        {
          attrs: {
            align: 'center',
            minWidth: '120',
            // '竞价范围'
            label: this.$t('bidMod.scopeBidding'),
            prop: 'publishScope',
            formatter: (row, column, value) => this.$getDictLabel('SOU_PUBLISH_SCOPE', value)
          }
        },
        // 评分规则
        {
          attrs: {
            align: 'center',
            minWidth: '120',
            // '评分规则'
            label: this.$t('bidMod.evaluateMethod'),
            prop: 'scoreRuleType',
            formatter: (row, column, value) => this.$getDictLabel('SOU_AUCT_SCORE_RULE_TYPE', value)
          }
        },
        // 项目信息
        {
          attrs: {
            align: 'center',
            minWidth: '120',
            label: () => this.$t('bidMod.projectInformation'),
            prop: 'projectInfo'
          },
          slot: 'projectInfo'
        },
        // 项目需求
        {
          attrs: {
            align: 'center',
            minWidth: '120',
            label: () => this.$t('bidMod.projectRequirement'),
            prop: 'requireInfo'
          },
          slot: 'requireInfo'
        },
        // 邀请供应商
        {
          attrs: {
            align: 'center',
            minWidth: '120',
            label: () => this.$t('bidMod.inviteSupplier'),
            prop: 'inviteVendor'
          },
          slot: 'inviteVendor'
        },
        // 流程审批
        // {
        //   attrs: {
        //     align: 'center',
        //     minWidth: '120',
        //     label: '流程审批',
        //     prop: 'createApproval'
        //   },
        //   slot: 'createApproval'
        // },
        // 保证金管理
        {
          attrs: {
            align: 'center',
            minWidth: '120',
            // '保证金管理'
            label: this.$t('bidMod.bondManagement'),
            prop: 'bondManagement'
          },
          slot: 'bondManagement'
        },
        // 报名管理
        {
          attrs: {
            align: 'center',
            minWidth: '120',
            label: () => this.$t('bidMod.entryManagement'),
            prop: 'signUpManagement'
          },
          slot: 'signUpManagement'
        },
        // 商务标管理
        {
          attrs: {
            align: 'center',
            minWidth: '120',
            label: () => this.$t('bidMod.commercialManagement'),
            prop: 'businessManagement'
          },
          slot: 'businessManagement'
        },
        {
          attrs: {
            align: 'center',
            minWidth: '120',
            label: () => this.$t('bidMod.hall'),
            prop: 'auctHall'
          },
          slot: 'auctHall'
        },
        // 评选
        {
          attrs: {
            align: 'center',
            minWidth: '120',
            label: () => this.$t('bidMod.bidEvaluation'),
            prop: 'evaluation'
          },
          slot: 'evaluation'
        },
        {
          attrs: {
            align: 'center',
            label: (t) => t.$t('common.operation'),
            fixed: 'right',
            width: 130
          },
          operations: [
            {
              key: 'deleteRow',
              event: 'deleteRow',
              name: this.$t('common.delete'), // 删除
              show: (scope) => scope.row.processStatus === 'DRAFT',
              attrs: { type: 'text' }
            },
            {
              key: 'editTableRow',
              event: 'editTableRow',
              name: this.$t('common.edit'), // 编辑
              show: (scope) => scope.row.processStatus === 'DRAFT',
              attrs: { type: 'text' }
            },
            {
              key: 'activeRow',
              event: 'activeRow',
              name: this.$t('common.active'), // 生效
              show: (scope) => ['DRAFT', 'INVALID'].includes(scope.row.processStatus),
              attrs: { type: 'text' }
            },
            {
              key: 'inactiveRow',
              event: 'inactiveRow',
              name: this.$t('common.inactive'), // 失效
              show: (scope) => scope.row.processStatus === 'VALID',
              attrs: { type: 'text' }
            }
          ]
        }
      ]
    }
  },

  created () {
    this.getQueryData()
  },

  methods: {
    async getQueryData (val = {}) {
      let transformParams = transformMQL.listPageData({
        type: 'SouProcessConfig',
        params: {
          ...val,
          souType: 'auct'
        },
        filterOperator: {
          auct: 'eq'
        },
        query: {
          '*': {},
          'auctProcessConfig': {
            '*': {}
          }
        },
        action: 'query',
        pageNum: this.pageInfo.pageNum,
        pageSize: this.pageInfo.pageSize
      })
      const response = await comBuyerHttp.process.page(transformParams)
      if (response && response.data) {
        const { records = [], total = 0 } = response.data
        this.tableData = records.map(item => ({
          ...item,
          auctHall: item.auctProcessConfig?.auctHall,
          bondManagement: item.auctProcessConfig?.bondManagement
        }))
        this.pageInfo.total = total
      }
    },

    // 新增
    addTableRowDialog () {
      this.dialogType = 'add'
      this.editRow = {}
      this.configureDialogVisible = true
    },

    // 编辑
    editTableRow ({ row }) {
      this.dialogType = 'edit'
      this.editRow = row
      this.configureDialogVisible = true
    },

    // 删除
    async deleteRow ({ row }) {
      const confirmResult = await this.$confirm(this.$t('common.delRow'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => { /* nothing */ })

      if (confirmResult !== 'confirm') {
        return
      }

      this.operationRow('removeProcessConfig', row)
    },

    // 生效
    activeRow ({ row }) {
      this.operationRow('validProcessConfig', row)
    },

    // 失效
    inactiveRow ({ row }) {
      this.operationRow('invalidProcessConfig', row)
    },

    // 掉接口
    async operationRow (type, row) {
      console.log(type, row)
      let transformParams = transformMQL.save('AuctSouProjectForBuyer', [{ processConfigId: row.processConfigId }], type)
      const response = await comBuyerHttp.process[type](transformParams)
      if (response) {
        this.$message.success(this.$t('common.success'))
        await this.getQueryData()
      }
    },
    /* 页码改变 */
    handlePagerCurrentChange (val) {
      this.pageInfo.pageNum = val
      this.getQueryData()
    },

    /* 页码大小改变 */
    handlePagerSizeChange (val) {
      this.pageInfo.pageSize = val
      this.getQueryData()
    }
  }
}
</script>

<style scoped lang="scss">
.bargain-flow-configure-wrapper ::v-deep {
  .configure-table {
    width: 100%;
    overflow: auto;
    .el-checkbox {
      pointer-events: none;
    }
  }
}
</style>
