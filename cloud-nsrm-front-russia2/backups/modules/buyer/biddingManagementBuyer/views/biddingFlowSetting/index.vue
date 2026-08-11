<template>
  <el-container
    class="flex-container-notab the_biddingFlowSetting_wrapper"
    direction="vertical"
  >
    <el-main>
      <p>
        <AuthorityButton
          code="bid:biddingFlowSetting:add"
          type="primary"
          @click="editTab('add')"
        >
          {{ $t('bidMod.addNewTemp') }}
        </AuthorityButton>
      </p>

      <el-table
        :data="tableData"
        highlight-current-row
        class="the_show_table"
        height="350px"
        @current-change="handleCurrentChange"
      >
        <el-table-column
          align="center"
          type="index"
          :label="$t('bidMod.tableIndex')"
          width="50"
        />
        <el-table-column
          align="center"
          :label="$t('bidMod.bigNode')"
        >
          <el-table-column
            align="center"
            prop="processConfigName"
            :label="$t('bidMod.smallNode')"
            :show-overflow-tooltip="true"
          />
        </el-table-column>

        <!--招标范围-->
        <el-table-column
          align="center"
          :label="$t('bidMod.bidingScope')"
        >
          <el-table-column
            align="center"
            prop="bidingScope"
            :label="$t('bidMod.bidingScope')"
            width="85"
            show-overflow-tooltip
            :formatter=" (row, column, value) => $getDictLabel('BID_SCOPE', value)"
          />
        </el-table-column>

        <!--招标类型-->
        <el-table-column
          align="center"
          :label="$t('bidMod.bidingType')"
        >
          <el-table-column
            align="center"
            prop="bidingType"
            :label="$t('bidMod.bidingType')"
            width="85"
            show-overflow-tooltip
            :formatter="(row, column, cellValue) => $getDictLabel('BID_TYPE', cellValue)"
          />
        </el-table-column>

        <!--评分规则-->
        <el-table-column
          align="center"
          :label="$t('bidMod.evaluateMethod')"
        >
          <el-table-column
            align="center"
            prop="evaluateMethod"
            :label="$t('bidMod.evaluateMethod')"
            width="85"
            show-overflow-tooltip
            :formatter="(row, column, cellValue) => $getDictLabel('BID_GRADING', cellValue)"
          />
        </el-table-column>

        <el-table-column
          align="center"
          :label="$t('bidMod.addNewProj')"
        >
          <el-table-column
            align="center"
            prop="projectInformation"
            :label="$t('bidMod.projectInformation')"
            width="85"
          >
            <template v-slot="scope">
              <el-checkbox
                v-model="scope.row.projectInformation"
                true-label="Y"
                false-label="N"
              />
            </template>
          </el-table-column>

          <el-table-column
            align="center"
            prop="projectRequirement"
            :label="$t('bidMod.projectRequirement')"
            width="85"
          >
            <template v-slot="scope">
              <el-checkbox
                v-model="scope.row.projectRequirement"
                true-label="Y"
                false-label="N"
              />
            </template>
          </el-table-column>

          <el-table-column
            align="center"
            prop="inviteSupplier"
            :label="$t('bidMod.inviteSupplier')"
            width="85"
          >
            <template v-slot="scope">
              <el-checkbox
                v-model="scope.row.inviteSupplier"
                true-label="Y"
                false-label="N"
              />
            </template>
          </el-table-column>

          <el-table-column
            align="center"
            prop="scoringRule"
            :label="$t('bidMod.scoreRule')"
            width="85"
          >
            <template v-slot="scope">
              <el-checkbox
                v-model="scope.row.scoringRule"
                true-label="Y"
                false-label="N"
              />
            </template>
          </el-table-column>

          <el-table-column
            align="center"
            prop="processApproval"
            :label="$t('bidMod.processApproval')"
            width="85"
          >
            <template v-slot="scope">
              <el-checkbox
                v-model="scope.row.processApproval"
                true-label="Y"
                false-label="N"
              />
            </template>
          </el-table-column>
        </el-table-column>

        <!--保证金管理-->
        <el-table-column
          align="center"
          label="保证金管理"
        >
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
        </el-table-column>

        <el-table-column
          align="center"
          :label="$t('bidMod.entryManagement')"
        >
          <el-table-column
            align="center"
            prop="entryManagement"
            :label="$t('bidMod.entryManagement')"
            width="85"
          >
            <template v-slot="scope">
              <el-checkbox
                v-model="scope.row.entryManagement"
                true-label="Y"
                false-label="N"
              />
            </template>
          </el-table-column>
        </el-table-column>

        <el-table-column
          align="center"
          :label="$t('bidMod.bidingControl')"
        >
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
        </el-table-column>

        <el-table-column
          align="center"
          :label="$t('bidMod.bidOpening')"
        >
          <el-table-column
            align="center"
            prop="technicalManagement"
            :label="$t('bidMod.technicalManagement')"
            width="85"
          >
            <template v-slot="scope">
              <el-checkbox
                v-model="scope.row.technicalManagement"
                true-label="Y"
                false-label="N"
              />
            </template>
          </el-table-column>

          <el-table-column
            align="center"
            prop="commercialManagement"
            :label="$t('bidMod.commercialManagement')"
            width="85"
          >
            <template v-slot="scope">
              <el-checkbox
                v-model="scope.row.commercialManagement"
                true-label="Y"
                false-label="N"
              />
            </template>
          </el-table-column>

          <el-table-column
            align="center"
            prop="bidEvaluation"
            :label="$t('bidMod.bidEvaluation')"
            width="85"
          >
            <template v-slot="scope">
              <el-checkbox
                v-model="scope.row.bidEvaluation"
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
          prop="status"
          :label="$t('common.status')"
          width="75"
          :formatter="(row, column, value) => $getDictLabel('BASE_INFO_STATUS', value)"
        />

        <!--操作-->
        <el-table-column
          align="center"
          fixed="right"
          :label="$t('bidMod.operation')"
          width="160"
        >
          <template v-slot="scope">
            <!--生效-->
            <el-button
              v-if="['DRAFT', 'INVALID'].includes(scope.row.status)"
              type="text"
              @click="validRow(scope.row)"
            >
              {{ $t('common.active') }}
            </el-button>
            <!--失效-->
            <el-button
              v-if="scope.row.status === 'VALID'"
              type="text"
              @click="validRow(scope.row)"
            >
              {{ $t('common.inactive') }}
            </el-button>
            <!--修改模板-->
            <el-button
              v-if="scope.row.status === 'DRAFT'"
              type="text"
              @click="editTab('edit', scope.row)"
            >
              {{ $t('bidMod.editTemp') }}
            </el-button>
            <!--删除-->
            <el-button
              v-if="scope.row.status === 'DRAFT'"
              type="text"
              @click="delOne(scope.row)"
            >
              {{ $t('common.delete') }}
            </el-button>
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

      <!-- 弹框区域-->
      <BiddingFlowSettingDialog
        v-model="dialogFormVisible"
        :visible="dialogFormVisible"
        :dialog-type="dialogType"
        :edit-row="editRow"
        @getQueryData="getQuerydata"
      />
    </el-main>
  </el-container>
</template>

<script>
import BiddingFlowSettingDialog from './settingDialog'
import CPagination from 'lib@/components/c-pagination'

export default {
  name: 'BiddingFlowSetting',

  components: {
    CPagination,
    BiddingFlowSettingDialog
  },

  data () {
    return {
      tableData: [],
      currentRow: null,
      preform: {
        processConfigName: '',
        comments: ''
      },
      pageInfo: {
        total: 0,
        pageNum: 1,
        pageSize: 15
      },
      dialogFormVisible: false,
      bidProcessNameList: [],
      dialogType: 'add',
      editRow: {}
    }
  },

  created () {
    this.getQuerydata()
    this.getbidProcessNameList()
  },

  methods: {
    getQuerydata (v) {
      let filterParam = {
        pageNum: this.pageInfo.pageNum,
        pageSize: this.pageInfo.pageSize
      }
      if (v) {
        filterParam = {
          ...filterParam,
          processConfigName: v
        }
      }
      this.$http({
        url: '/api-bid/bidProcessConfig/bidProcessConfig/listPage',
        method: 'POST',
        data: filterParam,
        loading: true
      }).then(data => {
        if (data && data.data) {
          if (data.data.list === null || data.data.list.length === 0) {
            this.pageInfo.total = 0
            this.tableData = []
          } else {
            this.tableData = data.data.list
            this.pageInfo.total = data.data.total
            if (data.data.list.length === 1) {
              this.preform.comments = data.data.list[0].comments
            } else {
              this.preform.comments = ''
            }
          }
        }
      })
    },

    getbidProcessNameList () {
      this.$http({
        url: '/api-bid/bidProcessConfig/bidProcessConfig/listPage',
        method: 'POST',
        data: {},
        loading: true
      }).then(data => {
        if (data.data) {
          this.bidProcessNameList = []
          data.data.list.map(v => {
            this.bidProcessNameList.push({
              processConfigId: v.processConfigId,
              processConfigName: v.processConfigName
            })
          })
        }
      })
    },

    editTab (type, row) {
      this.dialogType = type
      if (type !== 'add') {
        // 修改
        this.editRow = row
      }
      this.dialogFormVisible = true
    },

    delOne (row) {
      this.$confirm(this.$t('common.delRow'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$http({
          url: '/api-bid/bidProcessConfig/bidProcessConfig/delete',
          method: 'GET',
          params: { id: row.processConfigId },
          loading: true
        }).then(() => {
          this.$message({
            message: this.$t('common.successDelete'), // 删除成功
            type: 'success'
          })
          this.getQuerydata()
        })
      })
    },

    handleCurrentChange (val) {
      this.currentRow = val
    },

    /* 生效失效 */
    validRow (row) {
      let url
      if (['DRAFT', 'INVALID'].includes(row.status)) {
        // 生效
        url = `/api-bid/bidProcessConfig/bidProcessConfig/valid/${row.processConfigId}`
      } else if (row.status === 'VALID') {
        // 失效
        url = `/api-bid/bidProcessConfig/bidProcessConfig/invalid/${row.processConfigId}`
      } else {
        return
      }
      this.$http({
        url,
        method: 'post',
        data: {},
        loading: true
      }).then(() => {
        this.$message({
          message: this.$t('common.success'),
          type: 'success'
        })
        this.getQuerydata()
      })
    },

    handlePagerCurrentChange (val) {
      this.pageInfo.pageNum = val
      this.getQuerydata()
    },

    handlePagerSizeChange (val) {
      this.pageInfo.pageSize = val
      this.getQuerydata()
    }
  }
}
</script>

<style scoped lang="scss">
:deep(.the_biddingFlowSetting_wrapper) {
  .the_clarification_dialog {
    height: 330px;
    overflow: auto;
    padding: 11px;
  }
  .the_show_table {
    width: 100%;
    overflow: auto;
    .el-checkbox {
      pointer-events: none;
    }
  }
}
</style>
