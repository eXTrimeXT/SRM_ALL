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
            {{ $t('orderMod.unbind') }}
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
            <!-- 未绑送货单明细的外箱条码 -->
            <el-collapse-item :title="$t('cusEntry.supplement20250211.outerBoxCode')" name="1" prop="outerBoxCode">
              <!-- <srm-col :span="6">
                  <el-button
                  type="primary"
                  style="margin-bottom: 10px"
                  @click="openBindOuterBoxBarCode"
                  class="detail-pbtn"
                  >{{ $t('orderMod.outerBoxBarcode') }}</el-button>
                </srm-col> -->
              <el-form ref="outInnerRef3" :model="bindQuery">
                <srm-row :gutter="32">
                  <srm-col :span="8">
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
                      {{ $t('components.common.search') }}
                    </el-button>
                    <!-- <el-button
                          type="primary"
                          style="margin-top: 27px"
                          @click="restInput('outInnerRef3')"
                          >{{ $t('components.common.reset') }}</el-button
                        > -->
                    <el-button type="primary" style="margin-top: 27px" @click="addOuterBox">
                      <!-- 确定选择 -->
                      {{ $t('cusEntry.supplement20250211.confirmSelect') }}
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
                    :label="$t('orderMod.outerBoxCode')"
                    :show-overflow-tooltip="true"
                    width="350"
                  />

                  <el-table-column
                    align="center"
                    prop="materialCode"
                    :label="$t('common.materialCode')"
                    :show-overflow-tooltip="true"
                    width="180"
                  />

                  <el-table-column
                    align="center"
                    prop="materialName"
                    :label="$t('common.materialName')"
                    :show-overflow-tooltip="true"
                    width="180"
                  />
                  <el-table-column
                    align="center"
                    prop="vendorCode"
                    :label="$t('common.vendorCode')"
                    :show-overflow-tooltip="true"
                    width="150"
                  />
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
                  <el-form-item :label="$t('orderMod.outerBoxBarcode')">
                    <!-- <QuickSearch :showInput="bindQueryA.outerBoxCode" show-key="outerBoxCode" name="scc_base_outer_box_code" :scopeData="bindQueryA" @close-quicksearch="getOuterBox" /> -->
                    <el-input v-model="bindQueryA.outerBoxCode" disabled />
                  </el-form-item>
                </srm-col>

                <srm-col :span="6">
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
                  <el-form-item :label="$t('orderMod.numberOfAssociatedInnerBoxes')" prop="relationBoxCount">
                    <el-input v-model="bindQueryA.relationBoxCount" clearable disabled />
                  </el-form-item>
                </srm-col>
                <!-- <srm-col :span="6">
                  <el-form-item :label="$t('orderMod.orderNumber2')" prop="orderNumber">
                    <el-input v-model="bindQueryA.orderNumber" clearable
                      disabled />
                  </el-form-item>
                </srm-col> -->
              </srm-row>
            </el-collapse-item>
            <el-collapse-item :title="$t('orderMod.innerBoxBarcodeDetails')" name="2">
              <srm-row :gutter="32">
                <srm-col :span="6">
                  <el-form-item :label="$t('orderMod.innerBoxBarcode')" prop="innerBoxCode">
                    <el-input v-model="bindQuery.innerBoxCode" clearable />
                  </el-form-item>
                </srm-col>
                <srm-col :span="6">
                  <el-form-item :label="$t('orderMod.bindingState')" prop="bindStatus">
                    <el-select v-model="bindQuery.bindStatus">
                      <el-option :label="$t('orderMod.all')" value="" />
                      <el-option :label="$t('orderMod.unbound')" value="unBind" />
                      <el-option :label="$t('buyerDeliveryOrder.bound')" value="bind" />
                    </el-select>
                  </el-form-item>
                </srm-col>
                <srm-col :span="6">
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
                    {{ $t('components.common.search') }}
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
                  :label="$t('orderMod.innerBoxBarcode')"
                  :show-overflow-tooltip="true"
                  width="280"
                />
                <el-table-column
                  align="center"
                  prop="currentBoxQuantity"
                  :label="$t('bid_mod.quantity')"
                  :show-overflow-tooltip="true"
                  width="100"
                />
                <el-table-column
                  align="center"
                  prop="materialCode"
                  :label="$t('common.materialCode')"
                  :show-overflow-tooltip="true"
                  width="180"
                />

                <el-table-column
                  align="center"
                  prop="materialName"
                  :label="$t('common.materialName')"
                  :show-overflow-tooltip="true"
                  width="180"
                />
                <el-table-column
                  align="center"
                  prop="bindOuterBoxCode"
                  :label="$t('orderMod.outerBoxCode')"
                  :show-overflow-tooltip="true"
                  width="280"
                />
                <el-table-column
                  align="center"
                  prop="bindDate"
                  :label="$t('orderMod.bindDate')"
                  :show-overflow-tooltip="true"
                  width="150"
                  :formatter="(row, column, cellValue) => $parseTime(cellValue)"
                />

                <el-table-column :label="$t('components.headers.operation')" width="80" fixed="right">
                  <template slot-scope="scope">
                    <el-button
                      v-if="bindQueryA.outerBoxCode == scope.row.bindOuterBoxCode"
                      v-show="scope.row.bindStatus=='bind'"
                      type="text"
                      @click="unBind(scope.$index, scope.row)"
                    >
                      {{ $t('orderMod.unbind') }}
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
          {{ $t('orderMod.bindCommit') }}
        </el-button>
        <!-- <el-button type="primary" @click="unbindMore">{{ $t('orderMod.unbindCommit') }}</el-button> -->
        <el-button @click="cancel">
          {{ $t('base.tagsView.close') }}
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
      dialogTitle: this.$t('vendorMod.particulars'),
      form: {
        id: null,
        outerBoxId: null,
        outerBoxCode: '',
        innerBoxCode: '',
        innerBoxId: null,
        bindDate: '',
        bindStatus: '',
        createdId: null,
        createdBy: '',
        creationDate: '',
        createdByIp: '',
        lastUpdatedId: null,
        lastUpdatedBy: '',
        lastUpdateDate: '',
        lastUpdatedByIp: '',
        version: '',
        tenantId: 'TENANT_ID'
      },
      selectId: [],
      rules: {},
      tableHeader: [
        {
          prop: 'outerBoxCode',
          label: this.$t('orderMod.outerBoxCode2'),
          width: 280
        },
        {
          prop: 'innerBoxCode',
          label: this.$t('orderMod.innerBoxCode'),
          width: 280
        },
        {
          prop: 'bindDate',
          label: this.$t('orderMod.bindDate'),
          width: 200,
          dataType: 'dateTime'
        },
        {
          prop: 'bindStatus',
          label: this.$t('orderMod.bindingState'),
          width: 100
        },
        {
          prop: 'createdBy',
          label: this.$t('purchaseDemand.createdFullName'),
          width: 120
        },
        {
          prop: 'creationDate',
          label: this.$t('common.creationDate'),
          width: 180,
          dataType: 'dateTime'
        },
        {
          prop: 'lastUpdatedBy',
          label: this.$t('common.updatePeople'),
          width: 100
        }

      ],

      filterConfig: [
        { prop: 'outerBoxCode', label: this.$t('orderMod.outerBoxCode2') },
        { prop: 'innerBoxCode', label: this.$t('orderMod.innerBoxCode') },
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
      lineTitle: this.$t('cusEntry.supplement20250211.bindRecord'), // 绑定记录
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
      this.dialogTitle = this.$t('cusEntry.supplement20250211.innerOuterBoxAssociationAdd') // 内外箱关联新增
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
          message: this.$t('orderMod.outerBoxNotBindInfor')
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
              message: this.$t('orderMod.bindSuccess')
            })
            this.queryBindInnerRecord()
          } else {
            // 失败
            console.log('error')
          }
        })
      } else {
        this.$message({
          type: 'warning',
          message:this.$t('orderMod.selectNeedBindRowInfor')
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
          this.lineTitle = this.$t('cusEntry.supplement20250211.bindRecord') + '-' + this.bindQuery.outerBoxCode
        } else {
          // 失败
          console.log('error')
        }
      })
    },

    // queryBindRecord
    queryBindInnerRecord () {
      if (!this.bindQueryA.outerBoxCode) {
        return this.$message({
          type: 'warning',
          message: this.$t('cusEntry.supplement20250211.message3') // 请先选择外箱条码
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
          console.log('error')
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
          message: this.$t('cusEntry.supplement20250211.checkUnbindRow') // 请勾选解绑行！！！
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
          // 只能确认选择一条外箱信息
          message: this.$t('cusEntry.supplement20250211.message8')
        })
      } else {
        this.$message({
          type: 'warning',
          // 请先确认选择一条外箱信息
          message: this.$t('cusEntry.supplement20250211.message9')
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
