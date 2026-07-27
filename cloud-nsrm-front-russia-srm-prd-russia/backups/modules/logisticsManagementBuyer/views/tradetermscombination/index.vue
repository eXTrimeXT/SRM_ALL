<template>
  <el-container
    direction="vertical"
    class="flex-container tradetermscombination_list_wrapper"
  >
    <form-wrapper
      :form-array="filterConfig"
      @getFormData="search"
    >
      <template #tradeTermsName="{ scope }">
        <dict-select
          v-model="scope.tradeTermsName"
          code="TRADE_TERM"
        />
      </template>
      <template #importExportName="{ scope }">
        <dict-select
          v-model="scope.importExportName"
          code="EXP_IMP"
        />
      </template>
      <template #legCode="{ scope }">
        <dict-select
          v-model="scope.legCode"
          code="LEG"
        />
      </template>
      <template #feeName="{ scope }">
        <dict-select
          v-model="scope.feeName"
          code="CHARGE_NAME"
        />
      </template>
      <template #logisticsStatus="{ scope }">
        <dict-select
          v-model="scope.logisticsStatus"
          code="LOGISTICS_STATUS"
        />
      </template>
    </form-wrapper>

    <el-main>
      <easy-table
        ref="table"
        :table-data="tableData"
        :table-header="tableHeader"
        :selection="true"
        :methods="methods"
        :columns="columns"
        :checkbox="true"
        :current-change="handleCurrentChange"
        :check-change="handleSelectionChange"
        row-key="tradeTermsCombinationId"
        table-name="tradetermscombination_table"
        :query-params.sync="queryParams"
        @selection-change="handleSelectionChange"
      >
        <template #btns>
          <AuthorityButton
            type="primary"
            code="logistics:tradeTermsCombination:add"
            @click="add"
          >
            {{ $t('common.add') }}
          </AuthorityButton>
          <AuthorityButton
            type="primary"
            code="logistics:tradeTermsCombination:save"
            @click="save"
          >
            {{ $t('common.save') }}
          </AuthorityButton>
          <AuthorityButton
            type="primary"
            code="logistics:tradeTermsCombination:batchDelete"
            @click="batchDelete"
          >
            {{ $t('common.delete') }}
          </AuthorityButton>
          <AuthorityButton
            type="primary"
            code="logistics:tradeTermsCombination:doBatchEffect"
            @click="doBatchEffect"
          >
            {{ $t('common.active') }}
          </AuthorityButton>
          <AuthorityButton
            type="primary"
            code="logistics:tradeTermsCombination:doBatchIneffect"
            @click="doBatchIneffect"
          >
            {{ $t('common.inactive') }}
          </AuthorityButton>
          <m-import
            style="display: inline-block;margin: 0 10px;"
            :title="$t('common.import')"
            up-load-url="/api-pd/logistics/tradetermscombination/importExcel"
            :extra-data="extraData"
            @downloadTemplate="downloadTemplate"
            @handleSuccess="handleSuccess"
          />
        </template>
        <template #tradeTermsName="{ scope }">
          <dict-select
            v-model="scope.row.tradeTermsName"
            code="TRADE_TERM"
          />
        </template>
        <template #importExportName="{ scope }">
          <dict-select
            v-model="scope.row.importExportName"
            code="EXP_IMP"
          />
        </template>
        <template #legCode="{ scope }">
          <dict-select
            v-model="scope.row.legCode"
            code="LEG"
          />
        </template>
        <template #feeName="{ scope }">
          <dict-select
            v-model="scope.row.feeName"
            code="CHARGE_NAME"
          />
        </template>
      </easy-table>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import EasyTable from 'lib@/components/BaseTable/EasyTable'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
export default {
  name: 'Tradetermscombination',
  components: {
    EasyTable,
    FormWrapper,
    MImport
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      queryParams: {},
      tableData: [],
      // 文件上传配置信息
      fileInfo: {
        uploadType: 'FASTDFS', // 固定参数
        sourceType: 'WEB_APP', // 固定参数
        fileModular: 'workFlow', // 文件所属模块 -》审批流程
        fileFunction: 'workflowReport', // 审批流相关文件
        fileType: 'images' // 文件所属类型
      },
      filterConfig: [
        {
          prop: 'combinationName',
          label: this.$t('logisticsMod.combinationName')
        }, // 组合名称
        {
          prop: 'tradeTermsName',
          label: this.$t('logisticsMod.tradeTermsName'), // 贸易术语名称
          type: 'slot',
          slot: 'tradeTermsName'
        },
        {
          prop: 'importExportName',
          label: this.$t('logisticsMod.importExportName'), // 进出口名称
          type: 'slot',
          slot: 'importExportName'
        },
        {
          prop: 'legCode',
          label: this.$t('logisticsMod.legCode'),
          type: 'slot',
          slot: 'legCode'
        }, // LEG编码
        {
          prop: 'feeName',
          label: this.$t('logisticsMod.expenseItem'),
          type: 'slot',
          slot: 'feeName'
        }, // 费用项
        {
          prop: 'logisticsStatus',
          label: this.$t('common.status'), // 状态
          type: 'slot',
          slot: 'logisticsStatus'
        }
      ],
      extraData: {
        sourceType: 'WEB_APP',
        uploadType: 'FASTDFS',
        fileModular: 'base',
        fileFunction: 'tradetermscombination',
        fileType: 'excel'
      },

      selectList: [],
      tableHeader: [],
      currentRow: null,
      methods: {
        listPage: async params => {
          const res = await this.$api.logistics.tradetermscombination.list(params)
          return res
        }
      },
      columns: [
        {
          attrs: {
            label: this.$t('logisticsMod.combinationName'), // 组合名称
            prop: 'combinationName'
          },
          slot: 'combinationName',
          rules: { required: 0, message: this.$t('dataConfMod.required') }
        },
        {
          attrs: {
            label: this.$t('logisticsMod.tradeTermsName'), // 贸易术语名称
            prop: 'tradeTermsName',
            formatter: value => this.$getDictLabel('TRADE_TERM', value)
          },
          slot: 'tradeTermsName',
          rules: { required: 0, message: this.$t('dataConfMod.required') }
        },
        {
          attrs: {
            label: this.$t('logisticsMod.importExportName'), // 进出口名称
            prop: 'importExportName',
            formatter: value => this.$getDictLabel('EXP_IMP', value)
          },
          slot: 'importExportName',
          rules: { required: 0, message: this.$t('dataConfMod.required') }
        },
        {
          attrs: {
            label: this.$t('common.status'), // 状态
            prop: 'logisticsStatus',
            formatter: value => this.$getDictLabel('LOGISTICS_STATUS', value)
          },
          slot: 'logisticsStatus',
          rules: { required: 0, message: this.$t('dataConfMod.required') }
        },
        {
          attrs: {
            label: this.$t('logisticsMod.legCode'), // LEG编码
            prop: 'legCode',
            formatter: value => this.$getDictLabel('LEG', value)
          },
          slot: 'legCode',
          rules: { required: 1, message: this.$t('dataConfMod.required') }
        },
        {
          attrs: {
            label: this.$t('logisticsMod.expenseItem'), // 费用项
            prop: 'feeName',
            formatter: value => this.$getDictLabel('CHARGE_NAME', value)
          },
          slot: 'feeName',
          rules: { required: 1, message: this.$t('dataConfMod.required') }
        },
        {
          attrs: {
            label: this.$t('common.creator'), // 创建人
            prop: 'createdBy'
          },
          slot: 'createdBy',
          rules: { required: 0, message: this.$t('dataConfMod.required') }
        },
        {
          attrs: {
            label: this.$t('common.creationTime'), // 创建时间
            prop: 'creationDate'
          },
          slot: 'creationDate',
          rules: { required: 0, message: this.$t('dataConfMod.required') }
        },
        {
          attrs: {
            label: this.$t('common.updatePeople'), // 更新人
            prop: 'lastUpdatedUserName'
          },
          slot: 'lastUpdatedUserName',
          rules: { required: 0, message: this.$t('dataConfMod.required') }
        },
        {
          attrs: {
            label: this.$t('contractMod.lastUpdateDate'), // 最后更新时间
            prop: 'lastUpdateDate'
          },
          slot: 'lastUpdateDate',
          rules: { required: 0, message: this.$t('dataConfMod.required') }
        },
        {
          attrs: {
            prop: 'operation',
            label: this.$t('common.operation'), // 操作
            width: 150,
            // showType: "buttons",
            fixed: 'right'
            // btnStyle: "text",
          },
          operations: [
            {
              event: 'deleteItem',
              name: this.$t('common.delete'),
              func: this.deleteItem
            },
            {
              event: 'doEffect',
              name: this.$t('common.active'),
              func: this.doEffect
            },
            {
              event: 'doIneffect',
              name: this.$t('common.inactive'),
              func: this.doIneffect
            }
          ]
        }
      ]
    }
  },
  activated () {
    this.$refs.table.doLayout()
  },
  methods: {
    syncFilterParams (values) {
      this.filterParams = values
    },
    handleSuccess () {
      this.getQuerydata()
    },
    handleCurrentChange (val) {
      this.currentRow = val
    },
    handleSelectionChange (val) {
      this.selectList = val
    },
    downloadTemplate () {
      downloadFileLink(
        '/api-pd/logistics/tradetermscombination/exportExcelTemplate',
        this.$t('logisticsMod.importTemplateXLSX')
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    },
    // 上传附件成功
    handleUploadSuccess (file, row, key) {
      const { id, name } = file
      row[key] = id.toString()
    },
    // 删除文件
    handleAttachmentRemove (row, key) {
      row[key] = ''
    },
    deleteItem (scope, data) {
      if (scope.row.tradeTermsCombinationId) {
        // 有主键ID
        if (scope.row.logisticsStatus == 'EFFECTIVE') {
          return this.$message.error(this.$t('logisticsMod.msgPurchaseApply[15]')) // 生效状态的不能删除！
        }
        this.$confirm(this.$t('common.deleteViews'), {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        })
          .then(() => {
            this.$api.logistics.tradetermscombination
              .delete(scope.row.tradeTermsCombinationId)
              .then(res => {
                this.$message.success(res.message)
                this.$refs.table.search(this.queryParams, true)
              })
          })
          .catch(() => {})
      } else {
        // 无主键ID
        data.splice(scope.$index, 1)
      }
    },
    search (params) {
      const { pageSize, pageNum } = this.queryParams
      this.$refs.table.search({ pageSize, pageNum, ...params }, true)
    },
    add () {
      this.$refs.table.add({ logisticsStatus: 'DRAFT' })
    },
    batchDelete () {
      if (this.selectList.length === 0) {
        return this.$message.error(this.$t('contractMod.msgSelData'))
      }
      if (this.selectList.some(v => v.logisticsStatus == 'EFFECTIVE')) {
        return this.$message.error(this.$t('logisticsMod.msgPurchaseApply[16]')) // 状态是生效的不能删除!
      }
      for (let row of this.selectList) {
        if (!row.tradeTermsCombinationId) {
          let index = this.$refs[this.table].tableData.indexOf(row)
          this.delOne(index)
        }
      }
      let idArr = this.selectList.map(v => v.tradeTermsCombinationId).filter(v => !!v)
      if (idArr.length === 0) return
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.$http({
            url: '/api-pd/logistics/tradetermscombination/bathDelete',
            method: 'POST',
            data: idArr,
            loading: true
          })
            .then(data => {
              this.$message.success(this.$t('common.success'))
              this.$refs.table.search(this.queryParams, true)
            })
            .catch(err => {
              console.log(err)
            })
        })
        .catch(() => {})
    },
    save () {
      const list = this.$refs.table.getUpdatedRows()
      this.$refs.table.validate(f => {
        if (f) {
          this.$api.logistics.tradetermscombination
            .batchSaveOrUpdate(
              list.map(({ tradeTermsCombinationId, ...rest }) => {
                if (!tradeTermsCombinationId) {
                  return rest
                } else {
                  return { tradeTermsCombinationId, ...rest }
                }
              })
            )
            .then(res => {
              this.$message.success(res.message)
              this.$refs.table.search(this.queryParams, true)
            })
        } else {
          this.$message({
            message: this.$t('vendorMod.pleasefinishRequired'),
            type: 'error'
          })
        }
      })
    }, //
    doBatchEffect () {
      if (this.selectList.length === 0) {
        return this.$message.error(this.$t('contractMod.msgSelData'))
      }
      if (
        this.selectList.some(
          v => v.logisticsStatus !== 'DRAFT' && v.logisticsStatus !== 'INEFFECTIVE'
        )
      ) {
        return this.$message.error(this.$t('logisticsMod.msgSelDraftOrInAData')) // 请选择拟定或失效状态的数据!
      }
      this.$http({
        url: '/api-pd/logistics/tradetermscombination/effectiveTradeTermsCombination',
        method: 'POST',
        data: this.selectList.map(v => v.tradeTermsCombinationId).filter(v => !!v),
        loading: true
      })
        .then(data => {
          this.$message.success(this.$t('common.success'))
          this.$refs.table.search(this.queryParams, true)
        })
        .catch(err => {
          console.log(err)
        })
    },
    doBatchIneffect () {
      if (this.selectList.length === 0) {
        return this.$message.error(this.$t('contractMod.msgSelData'))
      }
      if (this.selectList.some(v => v.logisticsStatus !== 'EFFECTIVE')) {
        return this.$message.error(this.$t('logisticsMod.msgSelActiveData'))
      }
      this.$http({
        url: '/api-pd/logistics/tradetermscombination/inEffectiveTradeTermsCombination',
        method: 'POST',
        data: this.selectList.map(v => v.tradeTermsCombinationId).filter(v => !!v),
        loading: true
      })
        .then(data => {
          this.$message.success(this.$t('common.success'))
          this.$refs.table.search(this.queryParams, true)
        })
        .catch(err => {
          console.log(err)
        })
    },
    doEffect (scope) {
      if (!scope.row.tradeTermsCombinationId) {
        return this.$message.error(this.$t('logisticsMod.msgPurchaseApply[17]')) // "无效数据!"
      }
      this.$http({
        url: '/api-pd/logistics/tradetermscombination/effectiveTradeTermsCombination',
        method: 'post',
        data: [scope.row.tradeTermsCombinationId],
        loading: true
      })
        .then(data => {
          this.$message.success(this.$t('common.success'))
          this.$refs.table.search(this.queryParams, true)
        })
        .catch(err => {
          console.log(err)
        })
    },
    doIneffect (scope) {
      if (!scope.row.tradeTermsCombinationId) {
        return this.$message.error(this.$t('logisticsMod.msgPurchaseApply[17]'))
      }
      this.$http({
        url: '/api-pd/logistics/tradetermscombination/inEffectiveTradeTermsCombination',
        method: 'post',
        data: [scope.row.tradeTermsCombinationId],
        loading: true
      })
        .then(data => {
          this.$message.success(this.$t('common.success'))
          this.$refs.table.search(this.queryParams, true)
        })
        .catch(err => {
          console.log(err)
        })
    }
  }
}
</script>
