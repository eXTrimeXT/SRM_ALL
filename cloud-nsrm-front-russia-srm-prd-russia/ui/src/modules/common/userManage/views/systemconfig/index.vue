<template>
  <el-container
    class="flex-container-notab the_systemconfig_wrapper"
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
          <el-button
            type="primary"
            @click="addOne"
          >
            {{ $t('common.add') }}
          </el-button>
          <el-button
            :disabled="selections.length==0"
            @click="saveDataHandle"
          >
            {{ $t('common.submit') }}
          </el-button>
        </template>
      </MainHeader>
      <el-container
        direction="vertical"
        class="tablePd"
        style="padding: 0;"
      >
        <el-main style="flex-grow: 1;display: flex;flex-direction: column;position:relative;">
          <el-form
            ref="systemConfigTable"
            class="tableForm"
            :model="systemConfigModle"
            :rules="systemConfigModle.rules"
          >
            <el-table
              ref="mtTable"
              v-loading="loading"
              stripe
              border
              height="100%"
              :data="systemConfigModle.tableData"
              style="height: 100%;"
              @selection-change="handleSelectionChange"
              @cell-click="cellClick"
            >
              <el-table-column type="selection" />
              <!-- 系统名称 -->
              <el-table-column
                prop="systemName"
                :label="$t('systemconfig.label1')"
                show-overflow-tooltip
                min-width="80"
              >
                <template slot="header">
                  <i class="toRequired">*</i>
                  <span>{{ $t('systemconfig.label1') }}</span>
                </template>
                <template slot-scope="scope">
                  <el-form-item
                    :prop="'tableData.' + scope.$index + '.systemName'"
                    :rules="systemConfigModle.rules.systemName"
                  >
                    <el-input
                      v-show="true"
                      v-model="scope.row.systemName"
                    />
                  </el-form-item>
                </template>
              </el-table-column>
              <!-- 传输类型 -->
              <el-table-column
                prop="type"
                :label="$t('interfacelog.label2')"
                min-width="60"
                show-overflow-tooltip
              >
                <template slot="header">
                  <i class="toRequired">*</i>
                  <span>{{ $t('interfacelog.label2') }}</span>
                </template>
                <template slot-scope="scope">
                  <el-form-item
                    :prop="'tableData.' + scope.$index + '.type'"
                    :rules="systemConfigModle.rules.type"
                  >
                    <DictSelect
                      v-model="scope.row.type"
                      code="INTERFACE_LOG_TYPE"
                    />
                  </el-form-item>
                </template>
              </el-table-column>
              <!-- 协议类型 -->
              <el-table-column
                prop="protocol"
                :label="$t('interfacelog.label1')"
                min-width="60"
                show-overflow-tooltip
              >
                <template slot="header">
                  <i class="toRequired">*</i>
                  <span>{{ $t('interfacelog.label1') }}</span>
                </template>
                <template slot-scope="scope">
                  <el-form-item
                    :prop="'tableData.' + scope.$index + '.protocol'"
                    :rules="systemConfigModle.rules.protocol"
                  >
                    <DictSelect
                      v-model="scope.row.protocol"
                      code="INTERFACE_LOG_SERVICE_TYPE"
                    />
                  </el-form-item>
                </template>
              </el-table-column>

              <!-- 系统地址 -->
              <el-table-column
                prop="systemUrl"
                :label="$t('systemconfig.label2')"
                show-overflow-tooltip
                min-width="200"
              >
                <template slot="header">
                  <i class="toRequired">*</i>
                  <span>{{ $t('systemconfig.label2') }}</span>
                </template>
                <template slot-scope="scope">
                  <el-form-item
                    :prop="'tableData.' + scope.$index + '.systemUrl'"
                    :rules="systemConfigModle.rules.systemUrl"
                  >
                    <el-input
                      v-show="true"
                      v-model="scope.row.systemUrl"
                    />
                  </el-form-item>
                </template>
              </el-table-column>

              <!-- 实现类 -->
              <el-table-column
                prop="systemClass"
                :label="$t('systemconfig.label3')"
                show-overflow-tooltip
                min-width="100"
              >
                <template slot="header">
                  <span>{{ $t('systemconfig.label3') }}</span>
                </template>
                <template slot-scope="scope">
                  <el-form-item
                    :prop="'tableData.' + scope.$index + '.systemClass'"
                    :rules="systemConfigModle.rules.systemClass"
                  >
                    <el-input
                      v-show="true"
                      v-model="scope.row.systemClass"
                    />
                  </el-form-item>
                </template>
              </el-table-column>

              <!-- 测试地址 -->
              <el-table-column
                prop="systemClass"
                :label="$t('systemconfig.label4')"
                show-overflow-tooltip
                min-width="100"
              >
                <template slot="header">
                  <span>{{ $t('systemconfig.label4') }}</span>
                </template>
                <template slot-scope="scope">
                  <el-form-item
                    :prop="'tableData.' + scope.$index + '.testUrl'"
                    :rules="systemConfigModle.rules.testUrl"
                  >
                    <el-input
                      v-show="true"
                      v-model="scope.row.testUrl"
                    />
                  </el-form-item>
                </template>
              </el-table-column>

              <!-- 操作 -->
              <el-table-column
                fixed="right"
                :label="$t('common.operation')"
                width="130"
              >
                <template slot-scope="scope">
                  <el-button

                    type="text"
                    @click="handleDelClick(scope.$index,scope.row)"
                  >
                    <!-- 删除 -->
                    {{ $t('common.delete') }}
                  </el-button>
                  <!--
                  <el-button
                    type="text"
                    @click="handleTestClick(scope.$index,scope.row)"
                  >
                    测试
                  </el-button> -->
                </template>
              </el-table-column>
            </el-table>
          </el-form>
        </el-main>
        <el-footer class="page-bar">
          <CPagination
            ref="queryPagination"
            class="c-query-table-pagination"
            :total="pageInfo.total"
            :page-num="pageInfo.pageNum"
            :page-size="pageInfo.pageSize"
            @current-change="handleCurrentChange"
            @size-change="handleSizeChange"
          />
        </el-footer>
      </el-container>
    </el-main>
  </el-container>
</template>
<script>
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import CPagination from 'lib@/components/c-pagination'
import { systemConfigApi } from 'mod@/common/userManage/api'

export default {
  name: 'Systemconfig',
  components: {
    CPagination,
    MainHeader,
    FormWrapper
  },
  data () {
    return {
      gridId: 'list',
      loading: false,
      pageSize: 15,
      currentRow: null,
      tableHeader: [],
      queryParam: {},
      queryForm: [],
      purUnit: [],
      catList: [], // 采购分类
      systemConfigModle: {
        tableData: [],
        rules: {
          systemName: { type: 'string', required: true },
          protocol: { type: 'string', required: true },
          type: { type: 'string', required: true },
          systemUrl: { type: 'string', required: true }
        }
      },
      pageInfo: {
        total: 0,
        pageNum: 1,
        pageSize: 15
      },
      selections: []
    }
  },
  created () {
    this.queryForm = [
      {
        prop: 'systemName',
        label: '系统名称' // '系统名称'
      },
      {
        prop: 'type',
        label: '传输类型', // 传输类型
        type: 'dict',
        code: 'INTERFACE_LOG_TYPE'
      },
      {
        prop: 'protocol',
        label: '协议类型', // '协议类型'
        type: 'dict',
        code: 'INTERFACE_LOG_SERVICE_TYPE'
      }
    ]
  },
  mounted () {
    this.getQuerydata() //  查询数据
  },
  methods: {
    cellClick (row) {
      this.$refs.mtTable.toggleRowSelection(row, true)
    },
    getQuerydata (v) {
      this.queryParam = v || {}
      this.$nextTick(() =>
        this.fatchListData(this.queryParam, { pageSize: 15 })
      )
    },
    // 查询列表数据
    fatchListData (p1, p2) {
      let queryObj = { ...p1, ...p2 }
      systemConfigApi.systemConfigList(queryObj).then(res => {
        if (res.data && res.data.list) {
          this.loading = false
          this.pageInfo.total = res.data.total
          this.pageInfo.pageNum = res.data.pageNum
          this.pageInfo.pageSize = res.data.pageSize
          this.systemConfigModle.tableData = res.data.list.map(i => ({
            ...i,
            edit: false
          }))
        }
      })
    },
    handleSelectionChange (val) {
      this.selections = val
    },
    // 行删除
    handleDelClick (index, row) {
      let systemId = row.systemId
      if (systemId) {
        systemConfigApi.systemconfigDel({ systemId }).then(res => {
          if (res) {
            this.getQuerydata()
          }
        })
      } else {
        this.systemConfigModle.tableData.splice(index, 1)
      }
    },
    handleTestClick (index, row) {
      let systemId = row.systemId
      if (systemId) {
        systemConfigApi.interfaceTest({ systemId }).then(res => {
          if (res) {
              this.$message({
                message: '调用成功！',
                type: 'success'
              })
              this.getQuerydata()
            }
        })
      }
    },
    addOne () {
      this.systemConfigModle.tableData.unshift({
        add: true,
        systemName: '',
        protocol: '',
        type: '',
        systemUrl: ''
      })
      this.$nextTick(() => {
        this.$refs.mtTable.toggleRowSelection(
          this.systemConfigModle.tableData[0],
          true
        )
      })
    },
    saveDataHandle () {
      if (this.selections.length > 0) {
        this.$refs['systemConfigTable'].validate((valid, systemConfigModle) => {
          if (valid) {
            let subData = this.selections
            systemConfigApi.systemconfigSaveOrUpdate(subData).then(res => {
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
    handleCurrentChange (num) {
      let page = {
        pageNum: num
      }
      this.loading = true
      this.fatchListData(this.queryParam, page)
    },
    handleSizeChange (size) {
      let page = {
        pageSize: size
      }
      this.loading = true
      this.fatchListData(this.queryParam, page)
    }
  }
}
</script>
<style scoped lang="scss">
.tableForm {
  position: absolute;
  top: 0;
  bottom: 0;
  height: 100%;
  width: 100%;
  .el-table {
    height: 100%;
  }
}
.the_systemConfig_wrapper {
}
.download-link-wrap {
  .download-link-item {
    color: #1890ff;
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
</style>
