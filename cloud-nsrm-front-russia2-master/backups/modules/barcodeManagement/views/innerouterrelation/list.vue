<template>
  <el-container
    class="flex-container innerouterrelation_list_wrapper"
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
            @click="unbindMoreByParentPage"
          >
            {{
              $t('common.delete')
            }}
          </AuthorityButton>
        </template>
      </main-header>
      <table-view
        :ref="gridId"
        :table-header="tableHeader"
        :checkbox="true"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :source="$api.generate.innerouterrelation.list"
      />
    </el-main>

    <srm-dialog
      :title="dialogTitle"
      size="large"
      :visible.sync="visible"
    >
      <!-- <template #header class="dialog-header"> -->
      <el-button
        type="primary"
        @click="bindRecordSubmit"
      >
        绑定提交
      </el-button>
      <el-button
        type="primary"
        @click="unbindMore"
      >
        解绑提交
      </el-button>
      <!-- 内外箱条码关联弹窗 -->
      <div class="innerouterrelationEdit">
        <el-form
          ref="outInnerRef"
          :model="bindQuery"
          :rules="rules"
        >
          <el-collapse v-model="activeNames">
            <el-collapse-item
              title="外箱条码查询"
              name="1"
              prop="outerBoxCode"
            >
              <el-row :gutter="27">
                <el-col :span="6">
                  <el-form-item label="外箱条码">
                    <quick-search
                      :show-input="bindQueryA.outerBoxCode"
                      show-key="outerBoxCode"
                      name="scc_base_outer_box_code"
                      :scope-data="bindQueryA"
                      @close-quicksearch="getOuterBox"
                    />
                    <!-- <el-input v-model="bindQueryA.outerBoxCode" clearable /> -->
                  </el-form-item>
                </el-col>

                <el-col :span="6">
                  <el-form-item
                    label="供应商名称"
                    prop="vendorName"
                  >
                    <quick-search
                      disabled="true"
                      :show-input="bindQueryA.vendorName"
                      show-key="vendorName"
                      :scope-data="bindQueryA"
                      name="scc_sup_company_info5"
                      @close-quicksearch="getCompanyByQuick"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    label="关联内箱数"
                    prop="relationBoxCount"
                  >
                    <el-input
                      v-model="bindQueryA.relationBoxCount"
                      clearable
                      disabled="true"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    label="订单号"
                    prop="orderNumber"
                  >
                    <el-input
                      v-model="bindQueryA.orderNumber"
                      clearable
                      disabled="true"
                    />
                  </el-form-item>
                </el-col>
              </el-row>
            </el-collapse-item>
            <el-collapse-item
              title="内箱条码明细"
              name="2"
            >
              <el-row :gutter="27">
                <el-col :span="6">
                  <el-form-item
                    label="内箱条码"
                    prop="innerBoxCode"
                  >
                    <el-input
                      v-model="bindQuery.innerBoxCode"
                      clearable
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    label="绑定状态"
                    prop="bindStatus"
                  >
                    <el-select v-model="bindQuery.bindStatus">
                      <el-option
                        label="所有"
                        value=""
                      />
                      <el-option
                        label="未绑定"
                        value="unBind"
                      />
                      <el-option
                        label="已绑定"
                        value="bind"
                      />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    label="物料编号"
                    prop="materialCode"
                  >
                    <el-input
                      v-model="bindQuery.materialCode"
                      clearable
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-button
                    style="margin-top: 27px"
                    type="primary"
                    icon="el-icon-search"
                    @click="queryBindInnerRecord"
                  >
                    查询
                  </el-button>
                </el-col>
              </el-row>

              <el-table
                :data="echoInnerBoxCodeList"
                style="width: 100%"
                border
                :row-key="innerBoxCode"
                @selection-change="handleEchoInnerBoxCode"
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
                  prop="innerBoxCode"
                  label="内箱条码"
                  :show-overflow-tooltip="true"
                  :disabled="isReadOnly"
                  width="280"
                />

                <el-table-column
                  align="center"
                  prop="materialCode"
                  label="物料编码"
                  :show-overflow-tooltip="true"
                  :disabled="isReadOnly"
                  width="180"
                />

                <el-table-column
                  align="center"
                  prop="materialName"
                  label="物料名称"
                  :show-overflow-tooltip="true"
                  :disabled="isReadOnly"
                  width="180"
                />
                <el-table-column
                  align="center"
                  prop="bindOuterBoxCode"
                  label="外箱编码"
                  :show-overflow-tooltip="true"
                  :disabled="isReadOnly"
                  width="280"
                />
                <el-table-column
                  align="center"
                  prop="bindDate"
                  label="绑定时间"
                  :show-overflow-tooltip="true"
                  :disabled="isReadOnly"
                  width="150"
                />

                <el-table-column
                  label="操作"
                  width="80"
                  fixed="right"
                >
                  <template slot-scope="scope">
                    <el-button
                      v-if="bindQueryA.outerBoxCode == scope.row.bindOuterBoxCode"
                      v-show="scope.row.bindStatus == 'bind'"
                      type="text"
                      @click="unBind(scope.$index, scope.row)"
                    >
                      解绑
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
              <el-pagination
                :page-sizes="pagesizeA"
                layout="total, prev, pager, next, jumper"
                :total="MoDataLen"
                @size-change="handleSizeChange"
                @current-change="current_change"
              />
            </el-collapse-item>
          </el-collapse>
        </el-form>
      </div>
    </srm-dialog>

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
                    @click="unBind(scope.$index, scope.row)"
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
        <el-button @click="cancel">
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
import QuickSearch from 'lib@/components/QuickSearch' // 快速查询组件
export default {
  name: 'InnerouterrelationList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    QuickSearch
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      name: 'innerouterrelationList',
      tableName: 'innerouterrelationTable',
      pageSize: 15,
      pagesizeA: 10,
      currentPage: 1,
      MoDataLen: null,

      // 送货单默认分页大小
      delieryPageSize: 10,
      // 送货单页码
      deliveryPage: 1,
      // 送货单总数量
      deliveryMoDataLen: null,
      // 送货单列表信息
      deliveryDetailList: [],
      gridId: 'list',
      gridIdInner: 'listInner',
      currentRows: [],
      selectInnerBoxCodeSubmit: [],
      visible: false,
      // 送货单弹窗显示开关
      deliveryVisible: false,
      mode: 'add',
      dialogTitle: '详情',
      form: {
        id: 'ID主键',
        outerBoxId: '外箱条码ID',
        outerBoxCode: '外箱条码编号',
        innerBoxCode: '内箱条码编号',
        innerBoxId: '内箱条码ID',
        bindDate: '绑定时间',
        bindStatus: '绑定状态',
        createdId: '创建人id',
        createdBy: '创建人名称',
        creationDate: '创建日期',
        createdByIp: '创建ip',
        lastUpdatedId: '更新人id',
        lastUpdatedBy: '更新人',
        lastUpdateDate: '更新时间',
        lastUpdatedByIp: '更新ip',
        version: '版本号',
        tenantId: 'TENANT_ID'
      },
      rules: {},
      tableHeader: [
        {
          prop: 'outerBoxCode',
          label: '外箱条码编号',
          width: 280
        },
        {
          prop: 'innerBoxCode',
          label: '内箱条码编号',
          width: 280
        },
        {
          prop: 'bindDate',
          label: '绑定时间',
          width: 200
        },
        {
          prop: 'bindStatus',
          label: '绑定状态',
          width: 100
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
          width: 130,
          buttons: [
            // 暂时不在这个页面绑定关联 --2021年8月20日16:36:58
            // {
            //   callback: row => this.openRelationDeliveryWindow(row),
            //   formattor: () => {
            //     return "关联送货单";
            //   }
            // },
            {
              callback: row => this.unBindByParentPage(row),
              formattor: () => {
                return '解绑'
              }
            }
          ]
        }
      ],

      filterConfig: [
        { prop: 'outerBoxCode', label: '外箱条码编号' },
        { prop: 'innerBoxCode', label: '内箱条码编号' }
      ],
      queryParam: {},
      activeDims: ['1', '2'],
      activeNames: ['1', '2'],
      activeNameDelivery: ['1'],
      bindQuery: {
        materialCode: null,
        bindStatus: null
      },
      bindQueryA: {
        outerBoxCode: '',
        vendorName: '',
        vendorId: null,
        vendorCode: ''
      },
      // 内外箱关联信息
      echoInnerBoxCodeList: [],
      // 绑定标题
      lineTitle: '绑定记录',

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
    getOuterBox (value, scope) {
      scope.outerBoxCode = value.outerBoxCode
      scope.relationBoxCount = value.relationBoxCount
      scope.categoryId = value.categoryId
      scope.categoryName = value.categoryName
      scope.vendorId = value.vendorId
      scope.vendorCode = value.vendorCode
      scope.vendorName = value.vendorName
      scope.orderNumber = value.orderNumber
    },
    cancel () {
      this.visible = false
      this.getQuerydata()
    },
    confirm () {
      this.$refs.form.validate(result => {
        if (result) {
          const flag = this.mode
          // 新增时不用提交主键值
          const { id, ...rest } = this.form
          if (flag === 'add') {
          } else if (flag === 'edit') {
            this.$api.generate.innerouterrelation.update(this.form).then(res => {
              this.$message({
                type: 'success',
                message: res.message
              })
              this.visible = false
              this.getQuerydata()
            })
          }
        }
      })
    },
    /**
     * 当前页面-查询
     */
    getQuerydata (params) {
      this.queryParam = params
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    // 弹窗新增关联
    addHandle (row) {
      for (let i in this.bindQuery) {
        this.bindQuery[i] = ''
      }
      this.dialogTitle = '内外箱关联新增'
      this.visible = true
      this.mode = 'add'
    },
    // 选择关联记录
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
    /**
     * 根據快查获取供应商信息
     */
    getCompanyByQuick (val, scope) {
      scope.vendorId = val ? val.companyId : ''
      scope.vendorName = val ? val.companyName : ''
      scope.vendorCode = val ? val.companyCode : ''
    },

    /**
     * 当前页面-批量解绑
     */
    unbindMoreByParentPage () {
      let param = []
      this.currentRows.forEach(item => {
        if (item.id != null) {
          param.push(item.id)
        }
      })
      if (param.length == 0) {
        return this.$message({
          type: 'warning',
          message: '请选择要删除的绑定记录'
        })
      }
      this.$api.generate.innerouterrelation.delete(param).then(res => {
        this.$message({
          type: 'success',
          message: res.message
        })
        this.getQuerydata()
      })
    },

    /**
     * 当前页面-单个解绑
     */
    unBindByParentPage (row) {
      let param = []
      param.push(row.id)

      this.$api.generate.innerouterrelation.delete(param).then(res => {
        this.$message({
          type: 'success',
          message: res.message
        })

        this.getQuerydata()
      })
    },

    /// ////////////////////////////--------------内外箱绑定页面-----------//////////////////////////////////////////
    /**
     * 内外箱绑定界面-批量解绑
     */
    unbindMore () {
      let param = []
      this.selectInnerBoxCodeSubmit.forEach(item => {
        if (item.innerOuterRelationId != null) {
          param.push(item.innerOuterRelationId)
        }
      })

      //
      if (param.length == 0) {
        return this.$message({
          type: 'warning',
          message: '所选内箱记录中没有绑定外箱信息'
        })
      }

      this.$api.generate.innerouterrelation.delete(param).then(res => {
        this.$message({
          type: 'success',
          message: res.message
        })
        this.queryBindInnerRecord()
      })
    },
    /**
     * 绑定内外箱关系
     */
    bindRecordSubmit () {
      if (this.selectInnerBoxCodeSubmit && this.selectInnerBoxCodeSubmit.length > 0) {
        let bindSubmitData = []
        this.selectInnerBoxCodeSubmit
          .map(i => i.innerBoxCode)
          .forEach(item => {
            let bindLine = {}
            bindLine.innerboxcode = item
            bindLine.outerBoxCode = this.bindQueryA.outerBoxCode
            bindSubmitData.push(bindLine)
          })
        // 绑定信息
        this.$http({
          url: '/api-base/base/innerouterrelation/bind',
          method: 'POST',
          data: bindSubmitData,
          laoding: true
        }).then(data => {
          if (data && data.data) {
            // 绑定成功，回显在下面
            this.$message({
              type: 'success',
              message: '绑定成功'
            })
            this.queryBindInnerRecord()
            this.queryOuterBoxList() // 更新外箱信息
          } else {
            // 失败
            console.log('查询绑定信息返回异常')
          }
        })
      } else {
        this.$message({
          type: 'warning',
          message: '请先勾选需要绑定的行信息'
        })
      }
    },
    /**
     * 查询绑定信息
     */
    queryBindRecord () {
      // let params = Object.assign({ pageSize: 15, pageNum: 1 }, this.paramForm);

      const { innerBoxCode, ...rest } = this.bindQuery
      this.$http({
        url: '/api-base/base/innerouterrelation/queryBindList',
        method: 'POST',
        data: rest,
        laoding: true
      }).then(data => {
        if (data && data.data) {
          this.echoInnerBoxCode = data.data
          this.lineTitle = '绑定记录-' + this.bindQuery.outerBoxCode
        } else {
          // 失败
          console.log('查询绑定信息返回异常')
        }
      })
    },

    /**
     * 查询内箱信息
     */
    queryBindInnerRecord () {
      if (!this.bindQueryA.outerBoxCode) {
        this.$message({
          type: 'warning',
          message: '请输入外箱条码'
        })
      }

      this.$http({
        url: '/api-base/base/innerboxcode/listByRelation',
        method: 'POST',
        data: {
          pageNum: this.currentPage || 1,
          pageSize: this.pagesize || 10,
          bindStatus: this.bindQuery.bindStatus || '',
          materialCode: this.bindQuery.materialCode || '',
          bindOuterBoxCode: this.bindQueryA.outerBoxCode || '',
          innerBoxCode: this.bindQuery.innerBoxCode || ''
        },
        loading: true
      }).then(data => {
        if (data && data.data) {
          this.echoInnerBoxCodeList = data.data.list
          this.MoDataLen = data.data.total
        } else {
          // 失败
          console.log('查询绑定信息返回异常')
        }
      })
    },
    /**
     * 内外箱绑定页面-单个解绑
     */
    unBind (index, row) {
      this.$api.generate.innerouterrelation.delete(row.id).then(res => {
        this.$message({
          type: 'success',
          message: res.message
        })
        // 绑定成功，回显在下面
        this.queryBindInnerRecord()
      })
    },
    /**
     * 内外箱绑定页面-选择内箱条码
     */
    handleEchoInnerBoxCode (data) {
      this.selectInnerBoxCodeSubmit = data
    },
    restInput (formName) {
      this.$refs[formName].resetFields()
    },
    current_change (currentPage) {
      this.currentPage = currentPage
      this.queryBindInnerRecord()
    },
    handleSizeChange (pagesize) {
      this.pagesize = pagesize
    },

    /// /////////////////////////////////////------------送货单页面--------------///////////////////////////////

    /**
     * 打开送货单关联弹窗
     */
    openRelationDeliveryWindow (scope) {
      this.deliveryVisible = true
      this.dialogTitle = '关联送货单'
    },
    /**
     * 查询送货单信息
     */
    queryDeliveryList () {
      let rest = this.deliveryQuery
      this.$http({
        url: '/api-sup-ce/order/deliveryNoteDetail/listPage',
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
    }
  }
}
</script>
<style scoped lang="scss">
.innerouterrelationEdit {
  margin-top: 10px;
}
</style>
