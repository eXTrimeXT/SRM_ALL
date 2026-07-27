<template>
  <el-container class="flex-container innerouterrelation_list_wrapper" direction="vertical">
    <el-main>
      <FormWrapper :formArray="filterConfig" @getFormData="getQuerydata" />
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
            <!-- 未绑送货单明细的外箱条码 -->
            <el-collapse-item :title="$t('cusEntry.supplement20250211.unboundDeliveryDetailOuterBoxBarcode')" name="1" prop="outerBoxCode">
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
                    <!-- 物料编号 -->
                    <el-form-item :label="$t('orderMod.materialCode')" prop="materialCode">
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
                      <!-- 查询 -->
                      {{ $t("components.common.search") }}
                    </el-button>
                    <!-- <el-button
                          type="primary"
                          style="margin-top: 27px"
                          @click="restInput('outInnerRef3')"
                          >重置</el-button
                        > -->
                    <el-button type="primary" style="margin-top: 27px" @click="addOuterBox">
                      <!-- 确定选择 -->
                      {{ $t("cusEntry.supplement20250211.determineSelection") }}
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
                <!-- 外箱编码 -->
                  <el-table-column
                    align="center"
                    prop="outerBoxCode"
                    :label="$t('orderMod.outerBoxCode')"
                    :show-overflow-tooltip="true"
                    width="350"
                  />
                <!-- 物料编码 -->
                  <el-table-column
                    align="center"
                    prop="materialCode"
                    :label="$t('common.materialCode')"
                    :show-overflow-tooltip="true"
                    width="180"
                  />
                  <!-- 物料名称 -->
                  <el-table-column
                    align="center"
                    prop="materialName"
                    :label="$t('common.materialName')"
                    :show-overflow-tooltip="true"
                    width="180"
                  />
                  <!-- 供应商编码 -->
                  <el-table-column
                    align="center"
                    prop="vendorCode"
                    :label="$t('common.vendorCode')"
                    :show-overflow-tooltip="true"
                    width="150"
                  />
                  <!-- 供应商名称 -->
                  <el-table-column
                    align="center"
                    prop="vendorName"
                    :label="$t('common.companyName')"
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
                  <!-- 外箱条码 -->
                  <el-form-item :label="$t('orderMod.outerBoxBarcode')">
                    <!-- <QuickSearch :showInput="bindQueryA.outerBoxCode" show-key="outerBoxCode" name="scc_base_outer_box_code" :scopeData="bindQueryA" @close-quicksearch="getOuterBox" /> -->
                    <el-input v-model="bindQueryA.outerBoxCode" disabled />
                  </el-form-item>
                </srm-col>

                <srm-col :span="6">
                  <!-- 供应商名称 -->
                  <el-form-item :label="$t('common.companyName')" prop="vendorName">
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
                  <!-- 关联内箱数 -->
                  <el-form-item :label="$t('orderMod.numberOfAssociatedInnerBoxes')" prop="relationBoxCount">
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
            <!-- 内箱条码明细 -->
            <el-collapse-item :title="$t('orderMod.innerBoxBarcodeDetails')" name="2">
              <srm-row :gutter="32">
                <srm-col :span="6">
                  <!-- 内箱条码 -->
                  <el-form-item :label="$t('orderMod.innerBoxBarcode')" prop="innerBoxCode">
                    <el-input v-model="bindQuery.innerBoxCode" clearable />
                  </el-form-item>
                </srm-col>
                <srm-col :span="6">
                  <!-- 绑定状态 -->
                  <el-form-item :label="$t('orderMod.bindingState')" prop="bindStatus">
                    <el-select v-model="bindQuery.bindStatus">
                      <!-- 所有 -->
                      <el-option :label="$t('orderMod.all')" value="" />
                      <!-- 未绑定 -->
                      <el-option :label="$t('orderMod.unbound')" value="unBind" />
                      <!-- 已绑定 -->
                      <el-option :label="$t('buyerDeliveryOrder.bound')" value="bind" />
                    </el-select>
                  </el-form-item>
                </srm-col>
                <srm-col :span="6">
                  <!-- 物料编号 -->
                  <el-form-item :label="$t('orderMod.materialCode')" prop="materialCode">
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
                    <!-- 查询 -->
                    {{ $t("components.common.search") }}
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
                <!-- 内箱条码 -->
                <el-table-column
                  align="center"
                  prop="innerBoxCode"
                  :label="$t('orderMod.innerBoxBarcode')"
                  :show-overflow-tooltip="true"
                  width="280"
                />
                <!-- 数量 -->
                <el-table-column
                  align="center"
                  prop="currentBoxQuantity"
                  :label="$t('bid_mod.quantity')"
                  :show-overflow-tooltip="true"
                  width="100"
                />
                <!-- 物料编码 -->
                <el-table-column
                  align="center"
                  prop="materialCode"
                  :label="$t('common.materialCode')"
                  :show-overflow-tooltip="true"
                  width="180"
                />
                <!-- 物料名称 -->
                <el-table-column
                  align="center"
                  prop="materialName"
                  :label="$t('common.materialName')"
                  :show-overflow-tooltip="true"
                  width="180"
                />
                <!-- 外箱编码 -->
                <el-table-column
                  align="center"
                  prop="bindOuterBoxCode"
                  :label="$t('orderMod.outerBoxCode')"
                  :show-overflow-tooltip="true"
                  width="280"
                />
                <!-- 绑定时间 -->
                <el-table-column
                  align="center"
                  prop="bindDate"
                  :label="$t('orderMod.bindDate')"
                  :show-overflow-tooltip="true"
                  :formatter="(row, column, cellValue) => $parseTime(cellValue)"
                  width="150"
                />
                <!-- 操作 -->
                <el-table-column :label="$t('components.headers.operation')" width="80" fixed="right">
                  <template slot-scope="scope">
                    <el-button
                      v-if="bindQueryA.outerBoxCode == scope.row.bindOuterBoxCode"
                      v-show="scope.row.bindStatus=='bind'"
                      type="text"
                      @click="unBind(scope.$index, scope.row)"
                    >
                      <!-- 解绑 -->
                      {{ $t("orderMod.unbind") }}
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
          <!-- 绑定提交 -->
          {{ $t("orderMod.bindCommit") }}
        </el-button>
        <!-- <el-button type="primary" @click="unbindMore">解绑提交</el-button> -->
        <el-button @click="cancel">
          <!-- 关闭 -->
          {{ $t("base.tagsView.close") }}
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
import { innerOuterRelationApi } from 'modb@/barcodeManagement/api'

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
      dialogTitle: this.$t('vendorMod.particulars'),  // '详情'
      form: {
        id: this.$t('cusEntry.supplement20250211.idPrimaryKey'),  // 'ID主键'
        outerBoxId: this.$t('hierarchical.Outercarton'),  // '外箱条码ID'
        outerBoxCode: this.$t('orderMod.outerBoxCode2'),  // '外箱条码编号'
        innerBoxCode: this.$t('orderMod.innerBoxCode'),  // '内箱条码编号'
        innerBoxId: this.$t('hierarchical.Innerbox'),  // '内箱条码ID'
        bindDate: this.$t('orderMod.bindDate'),  // '绑定时间'
        bindStatus: this.$t('orderMod.bindingState'),  // '绑定状态'
        createdId: this.$t('cusEntry.supplement20250211.createrId'),  // '创建人id'
        createdBy: this.$t('purchaseDemand.createdFullName'),  // '创建人名称'
        creationDate: this.$t('common.creationDate'), // '创建日期'
        createdByIp: this.$t('cusEntry.supplement20250211.createIp'), // '创建ip'
        lastUpdatedId: this.$t('cusEntry.supplement20250211.updatePersonId'),  // '更新人id'
        lastUpdatedBy: this.$t('common.updatePeople'),  // '更新人'
        lastUpdateDate: this.$t('components.workedProcess.headers.fdEndDate'),  // '更新时间'
        lastUpdatedByIp: this.$t('cusEntry.supplement20250211.updateIp'),  // '更新ip'
        version: this.$t('dataConfMod.version'),  // '版本号'
        tenantId: 'TENANT_ID'
      },
      selectId: [],
      rules: {},
      tableHeader: [
        {
          prop: 'outerBoxCode',
          label: this.$t('orderMod.outerBoxCode2'),  // '外箱条码编号'
          width: 280
        },
        {
          prop: 'innerBoxCode',
          label: this.$t('orderMod.innerBoxCode'),  // '内箱条码编号'
          width: 280
        },
        {
          prop: 'bindDate',
          label: this.$t('orderMod.bindDate'),  // '绑定时间'
          width: 200,
          dataType: 'dateTime'
        },
        {
          prop: 'bindStatus',
          label: this.$t('orderMod.bindingState'),  // '绑定状态'
          width: 100
        },
        {
          prop: 'createdBy',
          label: this.$t('purchaseDemand.createdFullName'),  // '创建人名称'
          width: 120
        },
        {
          prop: 'creationDate',
          label: this.$t('common.creationDate'),  // '创建日期'
          width: 180,
          dataType: 'dateTime'
        },
        {
          prop: 'lastUpdatedBy',
          label: this.$t('common.updatePeople'),  // '更新人'
          width: 100
        }
      ],

      filterConfig: [
        { prop: 'outerBoxCode', label: this.$t('orderMod.outerBoxCode2') },  // '外箱条码编号'
        { prop: 'innerBoxCode', label: this.$t('orderMod.innerBoxCode') },  // '内箱条码编号'
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
      lineTitle: this.$t('cusEntry.supplement20250211.bindRecord'),  //  '绑定记录'
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
          message: this.$t('orderMod.outerBoxNotBindInfor')  // '所选内箱记录中没有绑定外箱信息'
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
              message: this.$t('orderMod.bindSuccess')  // '绑定成功'
            })
            this.queryBindInnerRecord()
          } else {
            // 失败
            console.log(this.$t('cusEntry.supplement20250211.queryBindInfoReturnException'))  // '查询绑定信息返回异常'
          }
        })
      } else {
        this.$message({
          type: 'warning',
          message: this.$t('orderMod.selectNeedBindRowInfor')  // '请先勾选需要绑定的行信息'
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
          // '绑定记录-'
          this.lineTitle = this.$t('cusEntry.supplement20250211.bindingRecord') + this.bindQuery.outerBoxCode
        } else {
          // 失败
          console.log(this.$t('cusEntry.supplement20250211.queryBindInfoReturnException'))  // '查询绑定信息返回异常'
        }
      })
    },

    // queryBindRecord
    queryBindInnerRecord () {
      if (!this.bindQueryA.outerBoxCode) {
        return this.$message({
          type: 'warning',
          message: this.$t('cusEntry.supplement20250211.selectOuterBoxBarcode')  // '请先选择外箱条码'
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
          console.log(this.$t('cusEntry.supplement20250211.queryBindInfoReturnException'))  // '查询绑定信息返回异常'
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
          message: this.$t('cusEntry.supplement20250211.confirmSingleOuterBoxInfo')  // '只能确认选择一条外箱信息'
        })
      } else {
        this.$message({
          type: 'warning',
          message: this.$t('cusEntry.supplement20250211.confirmSelectOneOuterBoxInfo')  // '请先确认选择一条外箱信息'
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
