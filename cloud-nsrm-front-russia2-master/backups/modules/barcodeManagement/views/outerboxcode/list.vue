<template>
  <el-container
    class="flex-container outerboxcode_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <form-wrapper
        :form-array="filterConfig"
        @getFormData="getQuerydata"
      />
      <main-header
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <AuthorityButton
            type="primary"
            @click="addHandle"
          >
            {{
              $t('common.add')
            }}
          </AuthorityButton>
          <AuthorityButton
            type="primary"
            @click="openRelationDeliveryWindow"
          >
            关联送货单
          </AuthorityButton>
          <AuthorityButton
            type="primary"
            @click="unBindDelivery"
          >
            解绑送货单
          </AuthorityButton>
        </template>
      </main-header>
      <table-view
        :ref="gridId"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :checkbox="true"
        :open-custom-table="true"
        :source="$api.generate.outerboxcode.list"
      />
    </el-main>

    <!-- 选择送货单页面 -->
    <srm-dialog
      :title="dialogTitle"
      size="large"
      :visible.sync="deliveryVisible"
    >
      <div class="innerouterrelationEdit">
        <el-form :model="deliveryQuery">
          <el-collapse v-model="activeNameDelivery">
            <el-row :gutter="27">
              <el-col :span="6">
                <el-form-item
                  label="送货单号"
                  prop="deliveryNumber"
                >
                  <el-input
                    v-model="deliveryQuery.deliveryNumber"
                    clearable
                  />
                </el-form-item>
              </el-col>

              <el-col :span="6">
                <el-button
                  style="margin-top: 27px"
                  type="primary"
                  icon="el-icon-search"
                  @click="queryDeliveryList"
                >
                  查询
                </el-button>
              </el-col>
            </el-row>

            <el-table
              :data="deliveryDetailList"
              style="width: 100%"
              border
              :row-key="deliveryNoteDetailId"
              @selection-change="handleDeliveryDetail"
            >
              <el-table-column
                type="selection"
                width="55"
              />
              <el-table-column
                type="index"
                width="55"
              />
              <el-table-column
                align="center"
                prop="deliveryNumber"
                label="送货单号"
                :show-overflow-tooltip="true"
                :disabled="isReadOnly"
                width="200"
              />

              <el-table-column
                align="center"
                prop="lineNum"
                label="送货单行号"
                :show-overflow-tooltip="true"
                :disabled="isReadOnly"
                width="80"
              />

              <el-table-column
                align="center"
                prop="orderNumber"
                label="采购订单"
                :show-overflow-tooltip="true"
                :disabled="isReadOnly"
                width="180"
              />
              <el-table-column
                align="center"
                prop="orderLineNum"
                label="采购订单行号"
                :show-overflow-tooltip="true"
                :disabled="isReadOnly"
                width="80"
              />
              <el-table-column
                align="center"
                prop="vendorName"
                label="供应商名称"
                :show-overflow-tooltip="true"
                :disabled="isReadOnly"
                width="210"
              />

              <el-table-column
                label="操作"
                width="80"
                fixed="right"
              >
                <template slot-scope="scope">
                  <el-button
                    v-if="deliveryNumber == null"
                    type="text"
                    @click="deliveryBind(scope.$index, scope.row)"
                  >
                    绑定
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            <el-pagination
              :page-sizes="deliveryPage"
              layout="total, prev, pager, next, jumper"
              :total="deliveryMoDataLen"
              @size-change="handleSizeChangeDelivery"
              @current-change="current_change_delivery"
            />
          </el-collapse>
        </el-form>
      </div>

      <template
        #footer
        class="dialog-footer"
      >
        <el-button @click="cancelDeliveryVisible">
          关闭
        </el-button>
      </template>
    </srm-dialog>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import outerboxcodeEdit from './edit.vue'
export default {
  name: 'OuterboxcodeList',
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
      name: 'outerboxcodeList',
      tableName: 'outerboxcodeTable',
      pageSize: 15,
      gridId: 'list',
      currentRows: [],
      tableHeader: [
        {
          prop: 'outerBoxCode',
          label: '外箱编码',
          width: 210
        },
        {
          prop: 'printCount',
          label: '打印次数',
          width: 100
        },
        {
          prop: 'materialCode',
          label: '物料编码',
          width: 100
        },
        {
          prop: 'materialSign',
          label: '物料标签',
          width: 100
        },
        {
          prop: 'vendorCode',
          label: '供应商编码',
          width: 100
        },
        {
          prop: 'vendorName',
          label: '供应商名称',
          width: 210
        },
        {
          prop: 'orderNumber',
          label: '采购订单号',
          width: 180
        },
        {
          prop: 'batchNo',
          label: '批次号',
          width: 100
        },
        {
          prop: 'orderLineNum',
          label: '项次（订单行）',
          width: 100
        },
        {
          prop: 'deliveryNumber',
          label: '送货单号',
          width: 180
        },
        {
          prop: 'deliveryLine',
          label: '送货单行号',
          width: 100,
          formattor: val => {
            if (val == -1) {
              return ''
            } else {
              return val
            }
          }
        },
        {
          prop: 'productionDate',
          label: '生产日期',
          width: 180
        },
        {
          prop: 'createdBy',
          label: '创建人名称',
          width: 100
        },
        {
          prop: 'creationDate',
          label: '创建日期',
          width: 180
        },
        {
          prop: 'lastUpdatedBy',
          label: '更新人',
          width: 100
        },
        {
          prop: 'operation',
          label: '操作',
          showType: 'buttons',
          btnStyle: 'text',
          fixed: 'right',
          width: 100,
          buttons: [
            {
              callback: row => this.editHandle(row),
              // code: "pr:requirementApply:edit",
              // show: row => row.status === "DRAFT",
              formattor: () => {
                return this.$t('common.edit')
              }
            },
            {
              callback: row => this.deleteHandle(row),
              // code: "pr:requirementApply:edit",
              // show: row => row.status === "DRAFT",
              formattor: () => {
                return this.$t('common.delete')
              }
            }
            // {
            //   callback: row => this.openRelationDeliveryWindow(row),
            //   formattor: () => {
            //     return "关联送货单";
            //   }
            // },
            // {
            //   callback: row => this.unBindDelivery(row),
            //   formattor: () => {
            //     return "解绑送货单";
            //   }
            // },
          ]
        }
      ],

      filterConfig: [
        { prop: 'outerBoxCode', label: '外箱编码' },
        { prop: 'materialCode', label: '物料编码' }
      ],
      queryParam: {},

      // 送货单默认分页大小
      delieryPageSize: 10,
      // 送货单页码
      deliveryPage: 1,
      // 送货单总数量
      deliveryMoDataLen: null,
      // 送货单列表信息
      deliveryDetailList: [],
      // 送货单弹窗显示开关
      deliveryVisible: false,

      deliveryQuery: {
        deliveryNumber: null
      }
    }
  },
  created () {
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    getQuerydata (params) {
      this.queryParam = params
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    deleteHandle (row) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.$api.generate.outerboxcode.delete(row.outerBoxId).then(res => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
        })
        .catch(() => {})
    },
    addHandle (row) {
      this.mode = 'add'
      const tab = {
        component: outerboxcodeEdit,
        params: {
          row,
          flag: this.mode
        },
        title: '外箱条码管理新增',
        name: 'outerboxcodeEdit'
      }
      this.$emit('tab-add', tab)
    },
    editHandle (row) {
      this.mode = 'edit'
      const tab = {
        component: outerboxcodeEdit,
        params: {
          row,
          flag: this.mode
        },
        title: '外箱条码管理编辑',
        name: 'outerboxcodeEdit' + row.outerBoxId
      }
      this.$emit('tab-add', tab)
    },
    handleCurrentChange (val) {
      this.currentRows = val
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

    /// /////////////////////////////////////------------送货单页面--------------///////////////////////////////

    /**
     * 打开送货单关联弹窗
     */
    openRelationDeliveryWindow (scope) {
      // 没有勾选外箱信息
      if (!this.currentRows) {
        return this.$message({
          type: 'warning',
          message: '请勾选外箱记录'
        })
      }

      let bindRecordList = '外箱'
      let bindWarn = false
      this.currentRows.forEach(element => {
        if (element.deliveryNumber) {
          bindWarn = true
          bindRecordList += ' [' + element.outerBoxCode + ']'
        }
      })
      bindRecordList += '已经存在绑定记录，请先解绑'
      // 勾选到了有绑定记录的外箱信息
      if (bindWarn) {
        return this.$message({
          type: 'warning',
          message: bindRecordList
        })
      }

      this.deliveryVisible = true
      this.dialogTitle = '关联送货单'
    },
    /**
     * 查询送货单信息
     */
    queryDeliveryList () {
      let rest = this.deliveryQuery
      rest.pageSize = this.delieryPageSize
      rest.pageNum = this.deliveryPage
      this.$http({
        url: '/api-sup-ce/order/deliveryNoteDetail/listPageBuyer',
        method: 'POST',
        data: rest,
        laoding: true
      }).then(data => {
        if (data && data.data) {
          this.deliveryDetailList = data.data.list
          this.deliveryMoDataLen = data.data.total
        } else {
          // 失败
          console.log('查询绑定信息返回异常')
        }
      })
    },
    // 送货单翻页
    current_change_delivery (currentPage) {
      this.deliveryPage = currentPage
      this.queryDeliveryList()
    },
    // 送货单调整页码
    handleSizeChangeDelivery (pagesize) {
      this.delieryPageSize = pagesize
    },
    // 绑定送货单
    deliveryBind (index, row) {
      let rest = {}
      rest.outerBoxIds = []
      this.currentRows.forEach(element => {
        rest.outerBoxIds.push(element.outerBoxId)
      })

      rest.deliveryNumber = row.deliveryNumber
      rest.deliveryLine = row.lineNum
      this.$api.generate.outerboxcode.bindByDelivery(rest).then(res => {
        this.$message({
          type: 'success',
          message: res.message
        })
        // 刷新查询
        this.getQuerydata()
        // 关闭弹窗
        this.deliveryVisible = false
      })
    },
    /**
     * 解绑送货单
     */
    unBindDelivery (row) {
      let rest = {}
      rest.outerBoxIds = []
      this.currentRows.forEach(element => {
        if (element.deliveryNumber) {
          rest.outerBoxIds.push(element.outerBoxId)
        }
      })

      if (rest.length == 0) {
        return this.$message({
          type: 'warning',
          message: '请勾选存在绑定送货箱的记录'
        })
      }

      this.$api.generate.outerboxcode.unbindByDelivery(rest).then(res => {
        this.$message({
          type: 'success',
          message: res.message
        })
        // 刷新查询
        this.getQuerydata()
      })
    },
    /**
     * 关闭送货单弹窗页面
     */
    cancelDeliveryVisible () {
      this.deliveryVisible = false
    }
  }
}
</script>
