<template>
  <el-container class="flex-container-notab bargain-flow-configure-wrapper" direction="vertical">
    <el-main>
      <FormWrapper
        :form-array="formWrapperArray"
        form-label-width="120px"
        @getFormData="getQueryData"
      />

      <p>
        <el-button type="primary" @click="openConfigureDialog('add')">
          {{ $t('bidMod.addNewTemp') }}
        </el-button>
      </p>

      <el-table
        v-loading="loading"
        :data="tableData"
        highlight-current-row
        class="configure-table"
        height="350px"
      >
        <el-table-column
          align="center"
          type="index"
          :label="$t('bidMod.tableIndex')"
          width="65"
        />

        <!--流程名称-->
        <el-table-column
          align="center"
          prop="processConfigName"
          label="流程名称"
          show-overflow-tooltip
          min-width="120"
        />

        <!--招标范围-->
        <el-table-column
          align="center"
          prop="publishScope"
          :label="$t('bidMod.bidingScope')"
          min-width="85"
          show-overflow-tooltip
          :formatter=" (row, column, value) => $getDictLabel('SOU_PUBLISH_SCOPE', value)"
        />

        <!--招标类型-->
        <el-table-column
          align="center"
          prop="bargainType"
          :label="$t('bidMod.bidingType')"
          min-width="85"
          show-overflow-tooltip
          :formatter="(row, column, cellValue) => $getDictLabel('SOU_BRG_TYPE', cellValue)"
        />

        <!--评分规则-->
        <el-table-column
          align="center"
          prop="scoreRuleType"
          :label="$t('bidMod.evaluateMethod')"
          min-width="100"
          show-overflow-tooltip
          :formatter="(row, column, cellValue) => $getDictLabel('SOU_SCORE_RULE_TYPE', cellValue)"
        />

        <!--招标立项-->
        <el-table-column align="center" :label="$t('bidMod.addNewProj')">
          <!--项目信息-->
          <el-table-column
            align="center"
            prop="projectInfo"
            :label="$t('bidMod.projectInformation')"
            width="85"
          >
            <template v-slot="scope">
              <el-checkbox
                v-model="scope.row.projectInfo"
                true-label="Y"
                false-label="N"
              />
            </template>
          </el-table-column>

          <!--项目需求-->
          <el-table-column
            align="center"
            prop="requireInfo"
            :label="$t('bidMod.projectRequirement')"
            width="85"
          >
            <template v-slot="scope">
              <el-checkbox
                v-model="scope.row.requireInfo"
                true-label="Y"
                false-label="N"
              />
            </template>
          </el-table-column>

          <!--邀请供应商-->
          <el-table-column
            align="center"
            prop="inviteVendor"
            :label="$t('bidMod.inviteSupplier')"
            width="85"
          >
            <template v-slot="scope">
              <el-checkbox
                v-model="scope.row.inviteVendor"
                true-label="Y"
                false-label="N"
              />
            </template>
          </el-table-column>

          <!--评分规则-->
          <el-table-column
            align="center"
            prop="scoreRule"
            :label="$t('bidMod.scoreRule')"
            width="85"
          >
            <template v-slot="scope">
              <el-checkbox
                v-model="scope.row.scoreRule"
                true-label="Y"
                false-label="N"
              />
            </template>
          </el-table-column>

          <!--立项审批-->
          <el-table-column
            align="center"
            prop="createApproval"
            label="立项审批"
            width="85"
          >
            <template v-slot="scope">
              <el-checkbox
                v-model="scope.row.createApproval"
                true-label="Y"
                false-label="N"
              />
            </template>
          </el-table-column>
        </el-table-column>

        <!--保证金管理-->
        <el-table-column
          align="center"
          prop="bondManagement"
          label="保证金管理"
          width="85"
        >
          <template v-slot="scope">
            <el-checkbox
              v-model="scope.row.bondManagement"
              true-label="Y"
              false-label="N"
            />
          </template>
        </el-table-column>

        <!--报名管理-->
        <el-table-column
          align="center"
          prop="signUpManagement"
          :label="$t('bidMod.entryManagement')"
          width="85"
        >
          <template v-slot="scope">
            <el-checkbox
              v-model="scope.row.signUpManagement"
              true-label="Y"
              false-label="N"
            />
          </template>
        </el-table-column>

        <!--投标控制-->
        <el-table-column
          align="center"
          prop="bidingControl"
          :label="$t('bidMod.bidingControl')"
          width="85"
        >
          <template v-slot="scope">
            <el-checkbox
              v-model="scope.row.bidingControl"
              true-label="Y"
              false-label="N"
            />
          </template>
        </el-table-column>

        <!--开评标-->
        <el-table-column
          align="center"
          :label="$t('bidMod.bidOpening')"
        >
          <!--技术标管理-->
          <el-table-column
            align="center"
            prop="techManagement"
            :label="$t('bidMod.technicalManagement')"
            width="85"
          >
            <template v-slot="scope">
              <el-checkbox
                v-model="scope.row.techManagement"
                true-label="Y"
                false-label="N"
              />
            </template>
          </el-table-column>

          <!--商务标管理-->
          <el-table-column
            align="center"
            prop="businessManagement"
            :label="$t('bidMod.commercialManagement')"
            width="85"
          >
            <template v-slot="scope">
              <el-checkbox
                v-model="scope.row.businessManagement"
                true-label="Y"
                false-label="N"
              />
            </template>
          </el-table-column>

          <!--评选-->
          <el-table-column
            align="center"
            prop="evaluation"
            :label="$t('bidMod.bidEvaluation')"
            width="85"
          >
            <template v-slot="scope">
              <el-checkbox
                v-model="scope.row.evaluation"
                true-label="Y"
                false-label="N"
              />
            </template>
          </el-table-column>
        </el-table-column>

        <!--状态-->
        <el-table-column
          align="center"
          fixed="right"
          prop="processStatus"
          :label="$t('common.status')"
          width="75"
          :formatter="(row, column, value) => $getDictLabel('SOU_PROCESS_CONFIG_STATUS', value)"
        />

        <!--操作-->
        <el-table-column
          align="center"
          fixed="right"
          :label="$t('bidMod.operation')"
          width="150"
        >
          <template v-slot="{ row }">
            <!--生效-->
            <el-button
              v-if="['DRAFT', 'INVALID'].includes(row.processStatus)"
              type="text"
              @click="operationRow('valid',row)"
            >
              {{ $t('common.active') }}
            </el-button>

            <!--失效-->
            <el-button
              v-if="row.processStatus === 'VALID'"
              type="text"
              @click="operationRow('invalid', row)"
            >
              {{ $t('common.inactive') }}
            </el-button>

            <template v-if="row.processStatus === 'DRAFT'">
              <!--编辑-->
              <el-button type="text" @click="openConfigureDialog('edit', row)">
                {{ $t('common.edit') }}
              </el-button>

              <!--删除-->
              <el-button type="text" @click="operationRow('remove', row)">
                {{ $t('common.delete') }}
              </el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>

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
import { brgBuyerHttp } from 'modb@/souConfiguration/api'
import CPagination from 'lib@/components/c-pagination'
import ConfigureDetailDialog from './configureDetailDialog'
import FormWrapper from 'lib@/components/Table/FormWrapper'

export default {
  name: 'BargainFlowConfigure',

  components: {
    CPagination,
    FormWrapper,
    ConfigureDetailDialog
  },

  data () {
    return {
      tableData: [],
      loading: false,
      formWrapperArray: [
        // 流程标题
        { prop: 'processConfigName', label: '流程标题' }
      ],
      pageInfo: {
        total: 0,
        pageNum: 1,
        pageSize: 15
      },
      configureDialogVisible: false,
      brgProcessNameList: [],
      dialogType: 'add',
      editRow: {}
    }
  },

  created () {
    this.getQueryData()
  },

  methods: {
    async getQueryData (val) {
      this.loading = true
      const response = await brgBuyerHttp.process.page({
        pageNum: this.pageInfo.pageNum,
        pageSize: this.pageInfo.pageSize,
        ...val
      })
      if (response && response.data) {
        const { list = [], total = 0 } = response.data
        this.tableData = list
        this.pageInfo.total = total
      }
      this.loading = false
    },

    /* 新增 / 查看 / 编辑弹窗 */
    openConfigureDialog (type, row) {
      this.dialogType = type
      this.editRow = type !== 'add' ? row : {}
      this.configureDialogVisible = true
    },

    /* 删除 / 生效 / 失效 */
    async operationRow (type, row) {
      if (type === 'remove') {
        const confirmResult = await this.$confirm(this.$t('common.delRow'), {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        }).catch(() => { /* nothing */ })

        if (confirmResult !== 'confirm') {
          return
        }
      }

      const response = await brgBuyerHttp.process[type](row.processConfigId)
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
