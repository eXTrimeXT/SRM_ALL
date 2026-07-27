<template>
  <el-container class="flex-container the-quaOfReviewDetail-detail" direction="vertical">
    <el-main>
      <el-collapse v-model="activeDims" class="tab-form-style">
        <!-- 维护物料属性 -->
        <el-collapse-item
          ref="quotaSettingDetails"
          :title="$t('dataConfMod.quotaSettingDetails')"
          name="1"
        >
          <el-form ref="form" :model="form" :rules="rules" :disabled="curOpt === 'view'">
            <srm-row>
              <srm-col>
                <!-- 采购组织 -->
                <el-form-item
                  :label="$t('orderMod.buyerOrderSynergy.organizationName')"
                  prop="organizationId"
                >
                  <organization-selector
                    ref="organizationSelector"
                    v-model="form.organizationId"
                    :parent-id="-1"
                    node-type="OU"
                    :placeholder="$t('common.pleaseSelect')"
                    :scope="form"
                    @select="addOrgHandle"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <!-- 物料编码 -->
                <el-form-item :label="$t('common.materialCode')" prop="materialCode">
                  <quick-search
                    :show-input="form.materialCode"
                    show-key="materialCode"
                    :scope-data="form"
                    name="scc_base_material_item"
                    @close-quicksearch="getItemObj"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <!-- 物料名称 -->
                <el-form-item :label="$t('common.materialName')" prop="materialName">
                  <el-input v-model="form.materialName" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <!-- 品类 -->
                <el-form-item :label="$t('dataConfMod.category')" prop="categoryName">
                  <el-input v-model="form.categoryName" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <!-- 单位 -->
                <el-form-item :label="$t('dataConfMod.unit')" prop="unitName">
                  <el-input v-model="form.unitName" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <!-- 有效期起 -->
                <el-form-item :label="$t('dataConfMod.startDate')" prop="startDate">
                  <el-date-picker
                    v-model="form.startDate"
                    type="date"
                    format="yyyy-MM-dd"
                    value-format="yyyy-MM-dd"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <!-- 有效期止 -->
                <el-form-item :label="$t('dataConfMod.endDate')" prop="endDate">
                  <el-date-picker
                    v-model="form.endDate"
                    type="date"
                    format="yyyy-MM-dd"
                    value-format="yyyy-MM-dd"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <!-- 配额管理类型 -->
                <el-form-item :label="$t('dataConfMod.quotaManagementType')">
                  <el-input v-model="form.quotaManagementTypeName" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <!-- 最少拆单量 -->
                <el-form-item :label="$t('dataConfMod.miniSplit')" prop="miniSplit">
                  <el-input v-model="form.miniSplit" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <!-- 最大分配量 -->
                <el-form-item :label="$t('dataConfMod.maxAllocation')" prop="maxAllocation">
                  <el-input v-model="form.maxAllocation" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <!-- 创建人 -->
                <el-form-item :label="$t('dataConfMod.createdBy')" prop="createdUserName">
                  <el-input v-model="form.createdBy" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <!-- 创建时间 -->
                <el-form-item :label="$t('dataConfMod.creationDate')" prop="creationDate">
                  <el-input v-model="form.creationDate" disabled />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="1">
                <!-- 备注 -->
                <el-form-item :label="$t('dataConfMod.remark')" prop="remark">
                  <el-input v-model="form.remark" type="textarea" :rows="2" />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-form>
        </el-collapse-item>
        <!-- 配额明细 -->
        <el-collapse-item ref="quotaDetails" :title="$t('dataConfMod.quotaDetails')" name="2">
          <el-form
            ref="materialTable"
            :model="materialModle"
            :disabled="curOpt === 'view'"
            :show-message="false"
          >
            <srm-row type="flex" style="margin-bottom: 12px;">
              <srm-col :span="12">
                <el-button type="primary" class="detail-pbtn" @click="add">
                  {{ $t("common.add") }}
                </el-button>
              </srm-col>
            </srm-row>
            <el-table ref="mtTable" stripe border :data="materialModle.tableData">
              <el-table-column align="center" type="index" width="50" />
              <!-- 供应商编码 -->
              <el-table-column
                prop="companyCode"
                align="center"
                :label="$t('common.vendorCode')"
                show-overflow-tooltip
              >
                <template slot-scope="scope">
                  <el-form-item>
                    <quick-search
                      :show-input="scope.row.companyCode"
                      show-key="companyCode"
                      :scope-data="scope.row"
                      :disabled="curOpt === 'edit' && scope.row.allocatedAmount != 0"
                      name="scc_sup_company_info_display"
                      @close-quicksearch="getVendor"
                    />
                  </el-form-item>
                </template>
              </el-table-column>
              <!-- 供应商名称 -->
              <el-table-column
                prop="companyName"
                align="center"
                :label="$t('common.companyName')"
                show-overflow-tooltip
              />
              <!-- 配额比 -->
              <el-table-column
                prop="quota"
                align="center"
                :label="$t('dataConfMod.quota')"
                show-overflow-tooltip
              >
                <template slot-scope="scope">
                  <el-form-item>
                    <!-- 请输入 -->
                    <el-input
                      v-model="scope.row.quota"
                      :placeholder="$t('common.pleaseInput')"
                      type="number"
                    />
                  </el-form-item>
                </template>
              </el-table-column>
              <!-- 已分配量 -->
              <el-table-column
                prop="allocatedAmount"
                align="center"
                :label="$t('dataConfMod.allocatedAmount')"
                show-overflow-tooltip
              />
              <!-- 配额基数 -->
              <el-table-column
                prop="baseNum"
                align="center"
                :label="$t('dataConfMod.baseNum')"
                show-overflow-tooltip
              >
                <template slot-scope="scope">
                  <el-form-item>
                    <!-- 请输入 -->
                    <el-input
                      v-model="scope.row.baseNum"
                      placeholder="$t('common.pleaseInput')"
                      type="number"
                    />
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column :label="$t('common.operation')" fixed="right" width="140">
                <template slot-scope="scope">
                  <el-button type="text" @click="delOne(scope.$index, scope.row)">
                    {{ $t("common.delete") }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-form>
        </el-collapse-item>
      </el-collapse>
      <c-toolbar v-if="curOpt !== 'view'">
        <template slot="right">
          <el-button
            v-if="
              status === 'DRAFT' || status === 'REJECTED' || status === ''
            "
            @click="back"
          >
            {{ $t("common.backTo") }}
          </el-button>
          <el-button
            v-if=" status === 'DRAFT' || status === 'REJECTED' || status === ''"
            type="primary"
            @click="save"
          >
            {{ $t("common.submit") }}
          </el-button>
        </template>
      </c-toolbar>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
import { getDictItemList } from '@/api/common'
import { adaptDictData } from '@/utils'

export default {
  name: 'QuotaSettingDetail',
  components: {
    OrganizationSelector,
    QuickSearch,
    CToolbar
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      activeDims: ['1', '2'],
      form: {
        // 维护物料属性
        organizationId: null,
        organizationCode: null,
        organizationName: null,
        materialCode: null,
        materialName: null,
        materialId: null,
        fullPathId: null,
        categoryName: null,
        categoryId: null,
        unitName: null,
        unit: null,
        startDate: null,
        endDate: null,
        quotaManagementType: null,
        quotaManagementTypeName: null,
        miniSplit: null,
        maxAllocation: null,
        createdBy: null,
        creationDate: null,
        remark: null
      },
      materialModle: {
        tableData: []
      },
      tableName: 'quotaSettingDetail',
      curOpt: 'add',
      status: '', // 单据状态
      quotaManagementTypeList: [], // 状态
      rules: {
        organizationId: [{ required: true, message: this.$t('dataConfMod.msgSelectOrganation') }], // 请选择采购组织
        materialCode: [{ required: true, message: this.$t('dataConfMod.msgInputItemCode') }], // 请输入物料编码
        materialName: [{ required: true, message: this.$t('dataConfMod.msgInputItemName') }],
        startDate: [{ required: true, message: this.$t('dataConfMod.msgStartDate') }], // 请输入选择生效日期
        endDate: [{ required: true, message: this.$t('dataConfMod.msgEndDate') }] // 请输入选择失效日期
      }
    }
  },
  created () {
    this.fatchDictData() // 字典
    this.curOpt = this.$attrs.params.flag
    this.tableName = this.$attrs.params.tabName
    if (this.$attrs.params.flag === 'edit' || this.$attrs.params.flag === 'view') {
      this.getreviewFormDetail(this.$attrs.params.row.quotaHeadId)
    }
  },
  methods: {
    // 获取数据字典
    fatchDictData () {
      // 批量查询字典
      let dictParamsArr = [
        { dictCode: 'QUOTA_MANAGEMENT_TYPE' } // 配额管理类型
      ]
      getDictItemList(dictParamsArr).then(res => {
        const [QUOTA_MANAGEMENT_TYPE] = res.data
        this.quotaManagementTypeList = adaptDictData(QUOTA_MANAGEMENT_TYPE.QUOTA_MANAGEMENT_TYPE)
      })
    },
    // 查单据详情
    getreviewFormDetail (id) {
      this.$http({
        url: '/api-base/quotaorder/get',
        method: 'get',
        params: { quotaHeadId: id },
        loading: true
      })
        .then(res => {
          this.form = res.data
          this.form.quotaManagementTypeName = this.$getDictLabelByValue(
            this.quotaManagementTypeList,
            res.data.quotaManagementType,
          )
          this.materialModle.tableData = res.data.quotaLineList
        })
    },

    // 选择组织
    addOrgHandle (e, id, scope) {
      scope.organizationId = e ? e.organizationId : ''
      scope.organizationCode = e ? e.organizationCode : ''
      scope.organizationName = e ? e.organizationName : ''
      scope.fullPathId = id
    },
    // 选择物料回调
    getItemObj (val, scope) {
      scope.materialId = val ? val.materialId : ''
      scope.materialCode = val ? val.materialCode : ''
      scope.materialName = val ? val.materialName : ''
      scope.categoryName = val ? val.categoryName : ''
      scope.categoryId = val ? val.categoryId : ''
      scope.unit = val ? val.unit : ''
      scope.unitName = val ? val.unitName : ''
      scope.quotaManagementType = val ? val.quotaManagementType : ''
      scope.quotaManagementTypeName = this.$getDictLabelByValue(
        this.quotaManagementTypeList,
        val.quotaManagementType,
      )
      scope.maxAllocation = val ? val.maxAllocation : ''
      scope.miniSplit = val ? val.miniSplit : ''
    },

    add () {
      this.materialModle.tableData.unshift({
        companyCode: null,
        quota: null,
        companyName: null,
        baseNum: 1
      })
    },
    getVendor (val, scope) {
      scope.companyCode = val ? val.companyCode : ''
      scope.companyName = val ? val.companyName : ''
      scope.companyId = val ? val.companyId : ''
    },
    delOne (index) {
      this.materialModle.tableData.splice(index, 1)
    },

    back () {
      this.$emit('tab-remove', this.tableName)
    },
    save () {
      this.$refs.form.validate(valid => {
        if (valid) {
          if (!this.materialModle.tableData.length) {
            // '请至少添加一条配额明细'
            return this.__jump_error__('quotaDetails', null, this.$t('dataConfMod.msgQuotaDetails'))
          }

          let allQuota = 0
          this.materialModle.tableData.map(i => {
            allQuota += Number(i.quota)
          })
          if (allQuota != 100) {
            // '配额比之和必须等于100'
            return this.__jump_error__('quotaDetails', null, this.$t('dataConfMod.msgAllQuota'))
          }
          this.saveFetch()
        } else {
          // '请输入单据必填信息'
          return this.__jump_error__(
            'quotaSettingDetails',
            null,
            this.$t('vendorMod.pleasefinishRequired'),
          )
        }
      })
    },
    // 保存数据操作
    saveFetch () {
      let url = ''
      if (this.curOpt === 'add') {
        url = '/api-base/quotaorder/add'
      } else if (this.curOpt === 'edit') {
        url = '/api-base/quotaorder/modify'
      }
      this.$http({
        url: url,
        method: 'POST',
        data: {
          ...this.form,
          quotaLineList: this.materialModle.tableData
        },
        loading: true
      })
        .then(res => {
          this.$message({
            type: 'success',
            message: res.message
          })
          this.back()
        })
    }
  }
}
</script>
<style scoped lang="scss">
.the-quaOfReviewDetail-detail {
  .el-table .el-date-editor {
    width: 135px;
  }

  .el-collapse-item__content > .el-button {
    margin-bottom: 5px;
  }
}
</style>
