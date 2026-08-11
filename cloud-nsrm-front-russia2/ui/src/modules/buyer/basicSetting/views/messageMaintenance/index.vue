<template>
  <el-container
    class="flex-container-notab the_material_wrapper"
    direction="vertical"
  >
    <el-main>
      <form-wrapper
        :form-array="queryForm"
        @getFormData="getQuerydata"
      />

      <main-header
        :l-span="22"
        :r-span="2"
        style="padding: 10px;"
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
      </main-header>
      <el-container
        direction="vertical"
        class="tablePd"
      >
        <el-main style="flex-grow: 1;display: flex;flex-direction: column;position:relative;">
          <el-form
            ref="messageTable"
            class="tableForm"
            :model="messageModle"
            :rules="messageModle.rules"
          >
            <el-table
              ref="mtTable"
              v-loading="loading"
              stripe
              border
              height="100%"
              :data="messageModle.tableData"
              style="height: 100%;"
              @selection-change="handleSelectionChange"
              @cell-click="cellClick"
            >
              <el-table-column
                type="selection"
              />
              <!-- 消息编码 -->
              <el-table-column
                prop="messageCode"
                :label="$t('dataConfMod.mcode')"
                show-overflow-tooltip
                min-width="120"
              >
                <template slot="header">
                  <em class="toRequired">*</em>{{ $t('dataConfMod.mcode') }}
                </template>
                <template v-slot="scope">
                  <template v-if="scope.row.add || scope.row.edit">
                    <el-form-item
                      :prop="'tableData.' + scope.$index + '.messageCode'"
                      :rules="messageModle.rules.messageCode"
                    >
                      <el-input
                        v-show="true"
                        v-model="scope.row.messageCode"
                      />
                    </el-form-item>
                  </template>
                  <span v-else>{{ scope.row.messageCode }}</span>
                </template>
              </el-table-column>
              <!-- 消息语言 -->
              <el-table-column
                prop="messageLanguage"
                :label="$t('dataConfMod.mlang')"
                min-width="100"
              >
                <template slot="header">
                  <em class="toRequired">*</em>{{ $t('dataConfMod.mlang') }}
                </template>
                <template v-slot="scope">
                  <template>
                    <el-form-item
                      :prop="'tableData.' + scope.$index + '.messageLanguage'"
                      :rules="messageModle.rules.messageLanguage"
                    >
                      <DictSelect
                        v-model="scope.row.messageLanguage"
                        :disabled="!scope.row.add && !scope.row.edit"
                        code="LANGUAGE_LIST"
                        custom-select-type="LANGUAGE_LIST"
                      />
                    </el-form-item>
                  </template>
                </template>
              </el-table-column>
              <!-- 消息值 -->
              <el-table-column
                prop="messageValue"
                :label="$t('dataConfMod.mname')"
                min-width="120"
                show-overflow-tooltip
              >
                <template v-slot="scope">
                  <template v-if="scope.row.add">
                    <el-form-item
                      :prop="'tableData.' + scope.$index + '.messageValue'"
                      :rules="messageModle.rules.messageValue"
                    >
                      <el-input
                        v-show="true"
                        v-model="scope.row.messageValue"
                      />
                    </el-form-item>
                  </template>
                  <span v-else>{{ scope.row.messageValue }}</span>
                </template>
              </el-table-column>
              <!-- 操作 -->
              <el-table-column
                fixed="right"
                :label="$t('common.operation')"
                width="130"
              >
                <template v-slot="scope">
                  <el-button
                    v-if="!scope.row.add"
                    type="text"
                    @click="handleEditClick(scope.$index, scope.row)"
                  >
                    {{ $t('common.edit') }}
                  </el-button>
                  <el-button
                    v-if="!scope.row.materialId"
                    type="text"
                    @click="handleDelClick(scope.$index,scope.row)"
                  >
                    {{ $t('common.delete') }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-form>
        </el-main>
        <el-footer class="page-bar">
          <c-pagination
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
import { messageMaintenance } from 'modb@/basicSetting/api/basicSetting'

export default {
  name: 'MessageMaintenance',
  components: {
    CPagination, MainHeader, FormWrapper
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
      messageModle: {
        tableData: [],
        rules: {
          messageCode: { type: 'string', required: true },
          messageLanguage: { type: 'string', required: true }
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
      { prop: 'messageCode',
        label: () => this.$t('dataConfMod.mcode')// '消息编码'
      },
      { prop: 'messageValue',
        label: () => this.$t('dataConfMod.mname')// '消息值'
      },
      { prop: 'messageLanguage',
        label: () => this.$t('dataConfMod.mlang'), // '消息语言'
        type: 'custom-dict',
        code: 'LANGUAGE_LIST',
        customSelectType: 'LANGUAGE_LIST'
      }
    ]
    this.getQuerydata() //  查询数据
  },
  methods: {
    cellClick (row) {
      this.$refs.mtTable.toggleRowSelection(row, true)
    },
    getQuerydata (v) {
      this.queryParam = v || {}
      this.$nextTick(() => this.fatchListData(this.queryParam, { pageSize: 15 }))
    },
    // 查询列表数据
    fatchListData (p1, p2) {
      let queryObj = { ...p1, ...p2 }
      messageMaintenance.getMessageList(queryObj).then(res => {
        if (res.data && res.data.list) {
          this.loading = false
          this.pageInfo.total = res.data.total
          this.pageInfo.pageNum = res.data.pageNum
          this.pageInfo.pageSize = res.data.pageSize
          this.messageModle.tableData = res.data.list.map(i => ({ ...i, edit: false }))
        }
      })
    },
    handleSelectionChange (val) {
      this.selections = val
    },
    // 行删除 this.$t("common.confirm")
    handleDelClick (index, row) {
      let messageId = row.messageId
      if (messageId) {
        messageMaintenance.messageItemDel({ messageId }).then(res => {
          if (res) {
            this.$message({
              message: res.message,
              type: 'success'
            })
            this.getQuerydata()
          }
        })
      } else {
        this.messageModle.tableData.splice(index, 1)
      }
    },
    // 行编辑
    handleEditClick (index) {
      this.messageModle.tableData[index].edit = true
    },
    addOne () {
      this.messageModle.tableData.unshift({
        add: true,
        messageValue: '',
        messageCode: '',
        messageLanguage: ''
      })
      this.$nextTick(() => {
        this.$refs.mtTable.toggleRowSelection(this.messageModle.tableData[0], true)
      })
    },
    saveDataHandle () {
      if (this.selections.length > 0) {
        this.$refs['messageTable'].validate(valid => {
          if (valid) {
            let subData = this.selections
            messageMaintenance.saveOrUpdateMessage(subData).then(res => {
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
.tableForm{
  position: absolute;
  top: 0;
  bottom: 0;
  height: 100%;
  width: 100%;
  .el-table{
    height: 100%;
  }
}
  .the_material_wrapper {

  }
  .download-link-wrap{
    .download-link-item{
      color:#1890ff;
    }
    .close-icon{
      font-weight: bold;
      cursor: pointer;
    }
  }
  .toRequired{
    color: #ff4949;
    padding-right: 2px;
  }
</style>
