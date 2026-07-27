<template>
  <el-container class="flex-container innerouterrelation_list_wrapper" direction="vertical">
    <el-main>
      <FormWrapper :formArray="filterConfig" @getFormData="getQuerydata" />
      <MainHeader :lSpan="22" :rSpan="2">
        <template slot="left">
          <AuthorityButton type="primary" @click="addHandle">
            {{
              $t('common.add')
            }}
          </AuthorityButton>
          <AuthorityButton type="primary" @click="unBinditem">
            解绑
          </AuthorityButton>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :checkChange="handleCurrentChange"
        :page-size="pageSize"
        :preQueryData="queryParam"
        :openCustomTable="true"
        :comActive="$attrs['changeTab']"
        url="/api-base/base/innerouterrelation/listPage"
        :checkbox="true"
      />
    </el-main>
    <srm-dialog :title="dialogTitle" size="large" :visible.sync="visible" destroy-on-close>
      <!-- <template #header class="dialog-header"> -->

      <!-- </template> -->
      <div class="innerouterrelationEdit">
        <el-form ref="outInnerRef" :model="bindQuery" :rules="rules">
          <el-collapse v-model="activeNames">
            <el-collapse-item title="未绑送货单明细的外箱条码" name="1" prop="outerBoxCode">
              <!-- <srm-col :span="6">
                  <el-button
                  type="primary"
                  style="margin-bottom: 10px"
                  @click="openBindOuterBoxBarCode"
                  class="detail-pbtn"
                  >外箱条码</el-button>
                </srm-col> -->
              <el-form ref="outInnerRef3" :model="bindQuery">
                <srm-row :gutter="32">
                  <srm-col :span="8">
                    <el-form-item label="物料编号" prop="materialCode">
                      <QuickSearch
                        :showInput="materialCode"
                        show-key="materialName"
                        :scope-data="bindQuery"
                        name="scc_base_material_item"
                        @close-quicksearch="getMaterialByQuick"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col :span="8">
                    <el-button
                      style="margin-top: 27px"
                      type="primary"
                      icon="el-icon-search"
                      @click="queryUnOuterBoxList"
                    >
                      查询
                    </el-button>
                    <!-- <el-button
                          type="primary"
                          style="margin-top: 27px"
                          @click="restInput('outInnerRef3')"
                          >重置</el-button
                        > -->
                    <el-button type="primary" style="margin-top: 27px" @click="addOuterBox">
                      确定选择
                    </el-button>
                  </srm-col>
                </srm-row>
                <el-table
                  :data="unbindOuterBoxList"
                  style="width: 100%"
                  border
                  row-key="outerBoxCode"
                  @selection-change="handleUnbindOuterBoxList"
                >
                  <el-table-column type="selection" reserve-selection width="55" />

                  <el-table-column
                    align="center"
                    prop="outerBoxCode"
                    label="外箱编码"
                    :show-overflow-tooltip="true"
                    width="350"
                  />

                  <el-table-column
                    align="center"
                    prop="materialCode"
                    label="物料编码"
                    :show-overflow-tooltip="true"
                    width="180"
                  />

                  <el-table-column
                    align="center"
                    prop="materialName"
                    label="物料名称"
                    :show-overflow-tooltip="true"
                    width="180"
                  />
                  <el-table-column
                    align="center"
                    prop="vendorCode"
                    label="供应商编码"
                    :show-overflow-tooltip="true"
                    width="150"
                  />
                  <el-table-column
                    align="center"
                    prop="vendorName"
                    label="供应商名称"
                    :show-overflow-tooltip="true"
                    width="280"
                  />
                </el-table>
                <el-pagination
                  :page-sizes="pagesizeA"
                  layout="total, prev, pager, next, jumper"
                  :total="total"
                  @size-change="handleSizeChange"
                  @current-change="relateionCurrentChange2"
                />
              </el-form>

              <srm-row :gutter="32">
                <srm-col :span="12">
                  <el-form-item label="外箱条码">
                    <!-- <QuickSearch :showInput="bindQueryA.outerBoxCode" show-key="outerBoxCode" name="scc_base_outer_box_code" :scopeData="bindQueryA" @close-quicksearch="getOuterBox" /> -->
                    <el-input v-model="bindQueryA.outerBoxCode" disabled />
                  </el-form-item>
                </srm-col>

                <srm-col :span="6">
                  <el-form-item label="供应商名称" prop="vendorName">
                    <QuickSearch
                      disabled
                      :showInput="bindQueryA.vendorName"
                      show-key="vendorName"
                      :scope-data="bindQueryA"
                      name="scc_sup_company_info_all"
                      @close-quicksearch="getCompanyByQuick"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :span="6">
                  <el-form-item label="关联内箱数" prop="relationBoxCount">
                    <el-input v-model="bindQueryA.relationBoxCount" clearable disabled />
                  </el-form-item>
                </srm-col>
                <!-- <srm-col :span="6">
                  <el-form-item label="订单号" prop="orderNumber">
                    <el-input v-model="bindQueryA.orderNumber" clearable
                      disabled />
                  </el-form-item>
                </srm-col> -->
              </srm-row>
            </el-collapse-item>
            <el-collapse-item title="内箱条码明细" name="2">
              <srm-row :gutter="32">
                <srm-col :span="6">
                  <el-form-item label="内箱条码" prop="innerBoxCode">
                    <el-input v-model="bindQuery.innerBoxCode" clearable />
                  </el-form-item>
                </srm-col>
                <srm-col :span="6">
                  <el-form-item label="绑定状态" prop="bindStatus">
                    <el-select v-model="bindQuery.bindStatus">
                      <el-option label="所有" value="" />
                      <el-option label="未绑定" value="unBind" />
                      <el-option label="已绑定" value="bind" />
                    </el-select>
                  </el-form-item>
                </srm-col>
                <srm-col :span="6">
                  <el-form-item label="物料编号" prop="materialCode">
                    <el-input v-model="bindQuery.materialCode" clearable />
                  </el-form-item>
                </srm-col>
                <srm-col :span="6">
                  <el-button
                    style="margin-top: 27px"
                    type="primary"
                    icon="el-icon-search"
                    @click="queryBindInnerRecord"
                  >
                    查询
                  </el-button>
                </srm-col>
              </srm-row>
              <el-table
                :data="echoInnerBoxCodeList"
                style="width: 100%"
                border
                row-key="innerBoxCode"
                @selection-change="handleEchoInnerBoxCode"
              >
                <el-table-column type="selection" reserve-selection width="55" />
                <el-table-column type="index" width="55" />

                <el-table-column
                  align="center"
                  prop="innerBoxCode"
                  label="内箱条码"
                  :show-overflow-tooltip="true"
                  width="280"
                />
                <el-table-column
                  align="center"
                  prop="currentBoxQuantity"
                  label="数量"
                  :show-overflow-tooltip="true"
                  width="100"
                />
                <el-table-column
                  align="center"
                  prop="materialCode"
                  label="物料编码"
                  :show-overflow-tooltip="true"
                  width="180"
                />

                <el-table-column
                  align="center"
                  prop="materialName"
                  label="物料名称"
                  :show-overflow-tooltip="true"
                  width="180"
                />
                <el-table-column
                  align="center"
                  prop="bindOuterBoxCode"
                  label="外箱编码"
                  :show-overflow-tooltip="true"
                  width="280"
                />
                <el-table-column
                  align="center"
                  prop="bindDate"
                  label="绑定时间"
                  :show-overflow-tooltip="true"
                  width="150"
                />

                <el-table-column label="操作" width="80" fixed="right">
                  <template slot-scope="scope">
                    <el-button
                      v-if="bindQueryA.outerBoxCode == scope.row.bindOuterBoxCode"
                      v-show="scope.row.bindStatus=='bind'"
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
                layout="sizes, total, prev, pager, next, jumper"
                :total="MoDataLen"
                @size-change="handleSizeChange"
                @current-change="current_change"
              />
            </el-collapse-item>
          </el-collapse>
        </el-form>
      </div>

      <template #footer class="dialog-footer">
        <el-button type="primary" @click="bindRecordSubmit">
          绑定提交
        </el-button>
        <!-- <el-button type="primary" @click="unbindMore">解绑提交</el-button> -->
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
import CUploadFile from '@/library/components/c-upload-file'
import CDownloadLink from 'lib@/components/c-download-link'
import QuickSearch from 'lib@/components/QuickSearch' // 快速查询组件
import MImport from 'lib@/components/import'
import { innerOuterRelationApi } from 'mods@/barcodeSupplier/api'

export default {
  name: 'InnerouterrelationList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    CUploadFile,
    CDownloadLink,
    QuickSearch,
    MImport
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      pageSize: 15,
      pagesizeA: [10, 20, 30, 40, 50, 60, 70, 80, 90, 100],
      currentPage: 1,
      currentPage2: 1,
      MoDataLen: 0,
      gridId: 'list',
      currentRows: [],
      selectInnerBoxCodeSubmit: [],
      visible: false,
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
      selectId: [],
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
          width: 120
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
        }

      ],

      filterConfig: [
        { prop: 'outerBoxCode', label: '外箱条码编号' },
        { prop: 'innerBoxCode', label: '内箱条码编号' },
        {
          prop: 'startCreationDate',
          label: () => this.$t('supplierRating.creationStartTime'),
          type: 'date'
        },
        {
          prop: 'endCreationDate',
          label: () => this.$t('supplierRating.creationEndTime'),
          type: 'date'
        }

      ],
      queryParam: {},
      activeNames: ['1', '2'],
      bindQuery: {
        materialCode: null,
        bindStatus: null
      },
      bindQueryA: {
        outerBoxCode: '',
        vendorName: '',
        vendorId: null,
        vendorCode: '',
        productionDate: ''
      },
      // 内外箱关联信息
      echoInnerBoxCodeList: [],
      // 绑定标题
      lineTitle: '绑定记录',
      materialCode: null,
      unbindOuterBoxList: [],
      selectUnOuterBoxList: [],
      total: 0
    }
  },
  created () {
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
    getQuerydata (params) {
      this.queryParam = params
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    // 清空对象属性值
    clearObjValue (obj) {
      for (let i in obj) {
        obj[i] = null
      }
    },
    addHandle (row) {
      this.clearObjValue(this.bindQuery)
      this.clearObjValue(this.bindQueryA)
      this.dialogTitle = '内外箱关联新增'
      this.visible = true
      this.mode = 'add'
      this.queryUnOuterBoxList()
    },
    // unbindHandle (row) {
    //   // for (let i in this.bindQuery) {
    //   //   this.bindQuery[i] = "";
    //   // }
    //   this.dialogTitle = "内外箱关联解绑";
    //   this.visible = true;  // 未送货订单 视图
    //   this.queryUnOuterBoxList();
    // },
    handleCurrentChange (val) {
      this.currentRows = val
      this.selectId = val.map(i => i.id)
      console.log('selectId', this.selectId)
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
     * 批量解绑
     */
    unbindMore () {
      let param = []
      //  this.selectInnerBoxCodeSubmit.forEach(item=>{
      //     if(item.innerOuterRelationId != null){
      //       param.push(item.innerOuterRelationId);
      //     }
      //   })
      this.selectInnerBoxCodeSubmit.forEach(item => {
        if (item.innerOuterRelationId != null) {
          param.push(item.innerOuterRelationId)
        }
      })
      console.log(param)
      //
      if (param.length == 0) {
        return this.$message({
          type: 'warning',
          message: '所选内箱记录中没有绑定外箱信息'
        })
      }
      innerOuterRelationApi.delete(param).then((res) => {
        this.$message({
          type: 'success',
          message: res.message
        })
        this.queryBindRecord()
      })
    },
    /**
    * 绑定内外箱关系
    */
    bindRecordSubmit () {
      if (this.selectInnerBoxCodeSubmit && this.selectInnerBoxCodeSubmit.length > 0) {
        let bindSubmitData = []
        this.selectInnerBoxCodeSubmit.map(i => i.innerBoxCode).forEach(item => {
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
          loading: true
        }).then(data => {
          if (data && data.data) {
            // 绑定成功，回显在下面
            this.$message({
              type: 'success',
              message: '绑定成功'
            })
            this.queryBindInnerRecord()
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
        data: { ...rest, outerBoxCode: this.bindQueryA.outerBoxCode },
        loading: true
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

    // queryBindRecord
    queryBindInnerRecord () {
      if (!this.bindQueryA.outerBoxCode) {
        return this.$message({
          type: 'warning',
          message: '请先选择外箱条码'
        })
      }

      this.$http({
        url: '/api-base/base/innerboxcode/listByRelation',
        method: 'POST',
        data: {
          pageNum: this.currentPage2 || 1,
          pageSize: this.pagesize || 10,
          bindStatus: this.bindQuery.bindStatus || '',
          materialCode: this.bindQuery.materialCode || '',
          innerBoxCode: this.bindQuery.innerBoxCode || '',
          vendorId: this.bindQueryA.vendorId || '',
          bindOuterBoxCode: this.bindQueryA.outerBoxCode,
          productionDate: this.bindQueryA.productionDate
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
     * 解绑 /api-base/base/innerouterrelation/delete
     */
    unBind (index, row) {
      let param = []
      param.push(row.innerOuterRelationId)
      innerOuterRelationApi.delete(param).then((res) => {
        this.$message({
          type: 'success',
          message: res.message
        })
        // 解绑成功, 回显在下面
        this.queryBindRecord()
      })
    },
    unBinditem () {
      let param = this.selectId
      console.log(param)
      if (param.length == 0) {
        this.$message({
          type: 'warning',
          message: '请勾选解绑行！！！'
        })
      } else {
        innerOuterRelationApi.delete(param).then((res) => {
          this.$message({
            type: 'success',
            message: res.message
          })
          // 解绑成功, 回显在下面
          this.getQuerydata()
        })
      }
    },
    handleEchoInnerBoxCode (data) {
      this.selectInnerBoxCodeSubmit = data
      console.log(data)
    },
    restInput (formName) {
      this.$refs[formName].resetFields()
    },
    current_change (currentPage) {
      this.currentPage2 = currentPage
      this.queryBindInnerRecord()
    },
    handleSizeChange (pagesize) {
      this.pagesize = pagesize
    },

    queryUnOuterBoxList () {
      // 根据物料与供应商得到未绑定外箱
      this.$http({
        url: '/api-base/base/outerboxcode/listUnOuterBox',
        method: 'POST',
        data: {
          pageNum: this.currentPage || 1,
          pageSize: this.pagesize || 10,
          materialCode: this.materialCode,
          vendorCode: this.$store.getters.userInfo.companyCode
        },
        loading: true
      })
        .then((data) => {
          this.unbindOuterBoxList = data.data.list
          this.total = data.data.total
        })
        .catch((err) => {
          console.log(err)
        })
    },
    relateionCurrentChange2 (currentPage) {
      this.currentPage = currentPage
      this.queryUnOuterBoxList()
    },
    /**
   * 根据快查获取物料信息
   */
    getMaterialByQuick (val, scope) {
      // scope.materialId = val ? val.materialId : "";
      // scope.materialCode = val ? val.materialCode : "";
      // scope.materialName = val ? val.materialName : "";
      this.materialCode = val ? val.materialCode : null
    },
    handleUnbindOuterBoxList (selection) {
      this.selectUnOuterBoxList = selection
    },
    addOuterBox () {
      console.log(this.selectUnOuterBoxList[0])
      if (this.selectUnOuterBoxList && this.selectUnOuterBoxList.length === 1) {
        this.bindQueryA.outerBoxCode = this.selectUnOuterBoxList[0].outerBoxCode
        this.bindQueryA.vendorName = this.selectUnOuterBoxList[0].vendorName
        this.bindQueryA.vendorId = this.selectUnOuterBoxList[0].vendorId
        this.bindQueryA.relationBoxCount = this.selectUnOuterBoxList[0].relationBoxCount
        this.bindQueryA.productionDate = this.selectUnOuterBoxList[0].productionDate
      } else if (
        this.selectUnOuterBoxList &&
        this.selectUnOuterBoxList.length > 1
      ) {
        this.$message({
          type: 'warning',
          message: '只能确认选择一条外箱信息'
        })
      } else {
        this.$message({
          type: 'warning',
          message: '请先确认选择一条外箱信息'
        })
      }
    }
  }
}
</script>
<style scoped lang="scss">
.innerouterrelationEdit {
  margin-top: 10px;
}
</style>
