<template>
  <el-container class="flex-container the-quotaConfigDetail-detail" direction="vertical">
    <el-main>
      <div class="form-container2">
        <el-form
          ref="quota"
          :disabled="isReadOnly"
          :model="quota"
          label-width="80px"
          label-position="top"
          class="form-fill-style"
        >
          <el-collapse v-model="activeDims" class="tab-form-style">
            <!-- 配额配置详情 -->
            <el-collapse-item
              ref="quotaConfigDetail"
              :title="$t('quota.quotaConfigDetail')"
              name="1"
            >
              <srm-row>
                <srm-col>
                  <!-- 单据编号 -->
                  <el-form-item :label="$t('quota.orderNumber')" :label-width="formLabelWidth">
                    <el-input v-model="quota.quotaCode" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 单据标题 -->
                  <el-form-item :label="$t('quota.orderTitle')" :label-width="formLabelWidth">
                    <el-input v-model="quota.quotaName" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 创建人 -->
                  <el-form-item :label="$t('quota.createdBy')" :label-width="formLabelWidth">
                    <el-input v-model="quota.createdUserName" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 单据状态 -->
                  <el-form-item :label="$t('quota.orderStatus')" :label-width="formLabelWidth">
                    <DictSelect v-model="quota.quotaStatus" code="QUOTA_STATUS" disabled />
                  </el-form-item>
                </srm-col>

                <srm-col :initCol="1">
                  <!-- 备注 -->
                  <el-form-item :label="$t('quota.remark')" :label-width="formLabelWidth">
                    <el-input v-model="quota.remark" type="textarea" :rows="2" />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-collapse-item>
            <!-- 事业部 -->
            <el-collapse-item ref="department" :title="$t('quota.department')" name="2">
              <p class="btn_line">
                <el-button type="primary" class="detail-pbtn" @click="addDepartmentItem">
                  {{
                    $t('common.add')
                  }}
                </el-button>
              </p>
              <el-table :data="quotaBuDTOList" style="width: 100%" border max-height="251px">
                <el-table-column
                  align="center"
                  type="index"
                  :label="$t('purSettlementMod.tabindex')"
                  width="50"
                />
                <!-- 事业部名称 -->
                <el-table-column
                  align="center"
                  prop="buName"
                  :label="$t('quota.departmentName')"
                  min-width="150"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <DictSelect
                      v-model="scope.row.buCode"
                      code="DIVISION"
                      @change-value="(value, dictItem) => setBUOrgObj(dictItem, scope.row)"
                    />
                  </template>
                </el-table-column>
                <!-- 事业部编码 -->
                <el-table-column
                  align="center"
                  prop="buCode"
                  :label="$t('quota.departmentCode')"
                  min-width="150"
                  :show-overflow-tooltip="true"
                />
                <el-table-column :label="$t('common.operation')" width="60" fixed="right">
                  <template slot-scope="scope">
                    <el-button type="text" @click="deleteDepartmentItem(scope.$index, scope.row)">
                      {{
                        $t('common.delete')
                      }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
            <!-- 预设比例 -->
            <el-collapse-item ref="presetScale" :title="$t('quota.presetScale')" name="3">
              <p class="btn_line">
                <el-button type="primary" class="detail-pbtn" @click="addPresetScaleItem">
                  {{
                    $t('common.add')
                  }}
                </el-button>
              </p>
              <el-table :data="quotaPreinstallList" style="width: 100%" border max-height="700px">
                <el-table-column
                  align="center"
                  type="index"
                  :label="$t('purSettlementMod.tabindex')"
                  width="50"
                />
                <!-- 供应商数量 -->
                <el-table-column
                  align="center"
                  prop="supplierNumber"
                  :label="$t('quota.supplierNumber')"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  v-for="col in numList"
                  :key="col.value"
                  align="center"
                  :prop="'quotaPreinstallNumber' + col.value"
                  :label="$t('quota.presetScale') + col.value"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row['quotaPreinstallNumber' + col.value]"
                      v-input-format="{ type: 'number' }"
                      :disabled="col.value > scope.row.supplierNumber"
                      @change="checkPreinstallNumber(scope.row['quotaPreinstallNumber' + col.value])"
                    />
                  </template>
                </el-table-column>
                <el-table-column :label="$t('common.operation')" width="60">
                  <template slot-scope="scope">
                    <el-button type="text" @click="deleteOneContent3(scope.$index, scope.row)">
                      {{
                        $t('common.delete')
                      }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
            <!-- 配额上下限 -->
            <el-collapse-item ref="quotaLimit" :title="$t('quota.quotaLimit')" name="4">
              <p class="btn_line">
                <el-button type="primary" class="detail-pbtn" @click="addQuotaLimitItem">
                  {{
                    $t('common.add')
                  }}
                </el-button>
              </p>
              <el-table :data="quotaRestrictionsList" style="width: 100%" border max-height="251px">
                <el-table-column
                  align="center"
                  type="index"
                  :label="$t('purSettlementMod.tabindex')"
                  width="50"
                />
                <!-- 限额原因 -->
                <el-table-column
                  align="center"
                  prop="restrictionsType"
                  :label="$t('quota.quotaReason')"
                  min-width="150"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <DictSelect v-model="scope.row.restrictionsType" code="REASON_FOR_LIMIT" />
                  </template>
                </el-table-column>
                <!-- 运算符 -->
                <el-table-column
                  align="center"
                  prop="symbolType"
                  :label="$t('quota.operator')"
                  min-width="150"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <DictSelect v-model="scope.row.symbolType" code="SYMBOL_TYPE" />
                  </template>
                </el-table-column>
                <!-- 比例 -->
                <el-table-column
                  align="center"
                  prop="proportion"
                  :label="$t('quota.scale') + '%'"
                  min-width="150"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.proportion"
                      v-input-format="{ type: 'number' }"
                      @change="checkScale(scope.row.proportion)"
                    />
                  </template>
                </el-table-column>
                <el-table-column :label="$t('common.operation')" width="60" fixed="right">
                  <template slot-scope="scope">
                    <el-button type="text" @click="deleteQuotaLimitItem(scope.$index, scope.row)">
                      {{
                        $t('common.delete')
                      }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
            <!-- 协议比例 -->
            <el-collapse-item ref="agreementRatio" :title="$t('quota.agreementRatio')" name="5">
              <p class="btn_line">
                <el-button type="primary" class="detail-pbtn" @click="addAgreementRatioItem">
                  {{
                    $t('common.add')
                  }}
                </el-button>
              </p>
              <el-table :data="agreementRatioList" style="width: 100%" border max-height="251px">
                <el-table-column
                  align="center"
                  type="index"
                  :label="$t('purSettlementMod.tabindex')"
                  width="50"
                />
                <!-- 小类 -->
                <el-table-column
                  align="center"
                  prop="categoryName"
                  :label="$t('quota.subcategory')"
                  min-width="150"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <QuickSearch
                      :show-input="scope.row.categoryName"
                      show-key="categoryName"
                      :scope-data="scope.row"
                      name="scc_base_purchase_category2"
                      @close-quicksearch="getCategoryObj"
                    />
                  </template>
                </el-table-column>
                <!-- 供应商编码 -->
                <el-table-column
                  align="center"
                  prop="vendorCode"
                  :label="$t('quota.vendorCode')"
                  min-width="150"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <QuickSearch
                      :show-input="scope.row.vendorCode"
                      show-key="companyCode"
                      :scope-data="scope.row"
                      name="scc_sup_company_info"
                      @close-quicksearch="getVendorObj"
                    />
                  </template>
                </el-table-column>
                <!-- 供应商名称 -->
                <el-table-column
                  align="center"
                  prop="vendorName"
                  :label="$t('quota.vendorName')"
                  min-width="150"
                  :show-overflow-tooltip="true"
                />
                <!-- 运算符 -->
                <el-table-column
                  align="center"
                  prop="symbolType"
                  :label="$t('quota.operator')"
                  min-width="150"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <DictSelect v-model="scope.row.symbolType" code="SYMBOL_TYPE" />
                  </template>
                </el-table-column>
                <!-- 协议配额 -->
                <el-table-column
                  align="center"
                  prop="proportion"
                  :label="$t('quota.proportion') + '%'"
                  min-width="150"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.proportion"
                      v-input-format="{ type: 'number' }"
                      @change="checkScale(scope.row.proportion)"
                    />
                  </template>
                </el-table-column>
                <!-- 起始日期 -->
                <el-table-column
                  align="center"
                  prop="startDate"
                  :label="$t('quota.startDate')"
                  min-width="170"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <el-date-picker
                      v-model="scope.row.startDate"
                      type="date"
                      :format="$formatDatePicker"
                      value-format="yyyy-MM-dd"
                    />
                  </template>
                </el-table-column>
                <!-- 结束日期 -->
                <el-table-column
                  align="center"
                  prop="endDate"
                  :label="$t('quota.endDate')"
                  min-width="170"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <el-date-picker
                      v-model="scope.row.endDate"
                      type="date"
                      :format="$formatDatePicker"
                      value-format="yyyy-MM-dd"
                    />
                  </template>
                </el-table-column>
                <el-table-column :label="$t('common.operation')" width="60" fixed="right">
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      @click="deleteAgreementRatioItem(scope.$index, scope.row)"
                    >
                      {{
                        $t('common.delete')
                      }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
            <!-- 价差标准 -->
            <el-collapse-item ref="priceStandard" :title="$t('quota.priceStandard')" name="6">
              <p class="btn_line">
                <el-button type="primary" class="detail-pbtn" @click="addPriceStandardItem">
                  {{
                    $t('common.add')
                  }}
                </el-button>
              </p>
              <el-table :data="priceStandardList" style="width: 100%" border max-height="251px">
                <el-table-column
                  align="center"
                  type="index"
                  :label="$t('purSettlementMod.tabindex')"
                  width="50"
                />
                <!-- 品类 -->
                <el-table-column
                  align="center"
                  prop="categoryName"
                  :label="$t('quota.category')"
                  min-width="150"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <QuickSearch
                      :show-input="scope.row.categoryName"
                      show-key="categoryName"
                      :scope-data="scope.row"
                      name="scc_base_purchase_category"
                      @close-quicksearch="getCategoryObj"
                    />
                  </template>
                </el-table-column>
                <!-- 价差 -->
                <el-table-column
                  align="center"
                  prop="spread"
                  :label="$t('quota.spread') + '>%'"
                  min-width="150"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.spread"
                      v-input-format="{ type: 'number' }"
                      @change="checkScale(scope.row.spread)"
                    />
                  </template>
                </el-table-column>
                <!-- 比例 -->
                <el-table-column
                  align="center"
                  prop="proportion"
                  :label="$t('quota.scale') + '≤%'"
                  min-width="150"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.proportion"
                      v-input-format="{ type: 'number' }"
                      @change="checkScale(scope.row.proportion)"
                    />
                  </template>
                </el-table-column>
                <el-table-column :label="$t('common.operation')" width="60" fixed="right">
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      @click="deletePriceStandardItem(scope.$index, scope.row)"
                    >
                      {{
                        $t('common.delete')
                      }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
            <!-- 预估返利 -->
            <el-collapse-item ref="estimatedRebate" :title="$t('quota.estimatedRebate')" name="7">
              <p class="btn_line">
                <el-button type="primary" class="detail-pbtn" @click="addEstimatedRebateItem">
                  {{
                    $t('common.add')
                  }}
                </el-button>
              </p>
              <el-table :data="quotaRebateList" style="width: 100%" border max-height="251px">
                <el-table-column
                  align="center"
                  type="index"
                  :label="$t('purSettlementMod.tabindex')"
                  width="50"
                />
                <!-- 供应商 -->
                <el-table-column
                  align="center"
                  prop="vendorCode"
                  :label="$t('quota.vendor')"
                  min-width="150"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <QuickSearch
                      :show-input="scope.row.vendorCode"
                      show-key="companyCode"
                      :scope-data="scope.row"
                      name="scc_sup_company_info"
                      @close-quicksearch="getVendorObj"
                    />
                  </template>
                </el-table-column>
                <!-- 品类 -->
                <el-table-column
                  align="center"
                  prop="categoryName"
                  :label="$t('quota.category')"
                  min-width="150"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <QuickSearch
                      :show-input="scope.row.categoryName"
                      show-key="categoryName"
                      :scope-data="scope.row"
                      name="scc_base_purchase_category"
                      @close-quicksearch="getCategoryObj"
                    />
                  </template>
                </el-table-column>
                <!-- 预估返利 -->
                <el-table-column
                  align="center"
                  prop="proportion"
                  :label="$t('quota.estimatedRebate') + '%'"
                  min-width="150"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.proportion"
                      v-input-format="{ type: 'number' }"
                      @change="checkScale(scope.row.proportion)"
                    />
                  </template>
                </el-table-column>
                <el-table-column :label="$t('common.operation')" width="60" fixed="right">
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      @click="deleteEstimatedRebateItem(scope.$index, scope.row)"
                    >
                      {{
                        $t('common.delete')
                      }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
          </el-collapse>
        </el-form>
      </div>
      <CToolbar v-if="!isReadOnly">
        <template slot="right">
          <el-button type="primary" @click="saveBill">
            {{
              $t('common.submit')
            }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>
<script>
import CToolbar from 'lib@/components/c-toolbar'
import QuickSearch from 'lib@/components/QuickSearch'
import { tabTodoMixin } from '@/utils/mixins'

export default {
  name: 'QuotaConfigDetail',
  components: {
    CToolbar,
    QuickSearch
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      activeDims: ['1', '2', '3', '4', '5', '6', '7', '8'],
      quota: {
        // 基本信息
        quotaId: '', // 单据ID
        quotaCode: '', // 单据编号
        quotaName: '', // 单据标题
        createdUserName: '', // 创建人
        quotaStatus: 'DRAFT', // 单据状态
        remark: '' // 备注
      },
      quotaBuDTOList: [], // 事业部
      quotaPreinstallList: [], // 预设比例
      quotaRestrictionsList: [], // 配额上下限
      agreementRatioList: [], // 协议比例
      priceStandardList: [], // 价差标准
      quotaRebateList: [], // 预估返利
      isReadOnly: this.$attrs.params.flag == 'readOnly',
      formLabelWidth: '120px',
      numList: [{ label: 1, value: 1 }]
    }
  },
  created () {
    if (this.$attrs.params.flag !== 'add') {
      this.getFormDetail()
    }
  },
  methods: {
    // 获取表单详情
    getFormDetail () {
      this.$http({
        url: '/api-inq/inquiry/quota/getQuota',
        method: 'GET',
        params: { id: this.$attrs.params.row.quotaId },
        loading: true
      })
        .then(data => {
          if (data && data.data) {
            this.quota = data.data.quota // 基本信息
            this.quotaBuDTOList = data.data.quotaBuDTOList // 事业部
            this.quotaPreinstallList = data.data.quotaPreinstallList // 预设比例
            this.quotaRestrictionsList = data.data.quotaRestrictionsList // 配额上下限
            this.agreementRatioList = data.data.agreementRatioList // 协议比例
            this.priceStandardList = data.data.priceStandardList // 价差标准
            this.quotaRebateList = data.data.quotaRebateList // 预估返利
            this.getQuotaNumberHandle() // 预设比例集合get数据处理
          }
        })
        .catch(err => {
          console.log(err)
        })
    },
    getQuotaNumberHandle () {
      let listLength = this.quotaPreinstallList.length
      this.numList = []
      for (let i = 1; i < listLength + 1; i++) {
        this.numList.push({
          value: Number(i),
          label: Number(i)
        })
      }
      setTimeout(() => {
        for (let item of this.quotaPreinstallList) {
          item['quotaPreinstallNumber'] = item['quotaPreinstallNumber'].split(',')
          for (let i = 1; i <= item['supplierNumber']; i++) {
            item['quotaPreinstallNumber' + i] = item['quotaPreinstallNumber'][i - 1]
          }
        }
      }, 100)
    },

    // 新增事业部
    addDepartmentItem () {
      this.quotaBuDTOList.push({
        buId: null,
        buCode: null,
        buName: null
      })
    },
    setBUOrgObj (dictItem, row) {
      console.log(row)
      if (
        this.quotaBuDTOList.length &&
        this.quotaBuDTOList.find(item => item.buId === dictItem.id)
      ) {
        this.$message.warning(this.$t('quota.buOrgTips')) // 请选择不重复的事业部!
      } else {
        row.buId = dictItem.id
        row.buCode = dictItem.value
        row.buName = dictItem.label
      }
    },
    // 删除事业部
    deleteDepartmentItem (index, row) {
      if (row.id) {
        this.$http({
          url: '/api-inq/inquiry/quotaBu/delete',
          method: 'GET',
          params: { id: row.id },
          loading: true
        })
          .then(data => {
            // 删除成功!
            this.$message({
              type: 'success',
              message: this.$t('common.successDelete')
            })
            this.quotaBuDTOList.splice(index, 1)
          })
          .catch(err => {
            console.log(err)
          })
      } else {
        this.quotaBuDTOList.splice(index, 1)
      }
    },

    // 新增预设比例
    addPresetScaleItem () {
      if (this.quotaPreinstallList.length < 15) {
        this.quotaPreinstallList.push({
          quotaPreinstallId: null,
          quotaId: null,
          supplierNumber: this.quotaPreinstallList.length + 1,
          quotaPreinstallNumber: null
        })
        setTimeout(() => {
          let len = this.quotaPreinstallList.length
          this.numList = []
          for (let i = 1; i < len + 1; i++) {
            this.numList.push({
              value: Number(i),
              label: Number(i)
            })
          }
        }, 100)
      } else {
        this.$message.warning('预设比例不能超过15条！')
      }
    },
    checkPreinstallNumber (val) {
      if (val > 100) {
        // 每个预设比例不应大于100,每行预设比例之和应为100!
        this.$message.warning(this.$t('quota.checkNumberTips'))
      }
    },
    checkScale (val) {
      if (val < 0 || val > 100) {
        // 输入范围应为0~100!
        this.$message.warning(this.$t('quota.checkNumberValidTips'))
      }
    },
    // 删除预设比例
    deletePresetScaleItem (index, row) {
      if (row.quotaPreinstallId) {
        this.$http({
          url: '/api-inq/inquiry/quotaPreinstall/delete',
          method: 'GET',
          params: { id: row.quotaPreinstallId },
          loading: true
        })
          .then(data => {
            this.$message({
              type: 'success',
              message: this.$t('common.successDelete')
            })
            this.quotaPreinstallList.splice(index, 1)
          })
          .catch(err => {
            console.log(err)
          })
      } else {
        this.quotaPreinstallList.splice(index, 1)
      }
    },

    // 新增配额上下限
    addQuotaLimitItem () {
      this.quotaRestrictionsList.push({
        quotaRestrictionsId: null,
        quotaId: null,
        restrictionsType: null,
        symbolType: null,
        proportion: null
      })
    },
    // 删除配额上下限
    deleteQuotaLimitItem (index, row) {
      if (row.quotaRestrictionsId) {
        this.$http({
          url: '/api-inq/inquiry/quotaRestrictions/delete',
          method: 'GET',
          params: { id: row.quotaRestrictionsId },
          loading: true
        })
          .then(data => {
            this.$message({
              type: 'success',
              message: this.$t('common.successDelete')
            })
            this.quotaRestrictionsList.splice(index, 1)
          })
          .catch(err => {
            console.log(err)
          })
      } else {
        this.quotaRestrictionsList.splice(index, 1)
      }
    },

    // 新增协议比例
    addAgreementRatioItem () {
      this.agreementRatioList.push({
        agreementRatioId: null,
        quotaId: null,
        categoryId: null,
        categoryName: null,
        categoryCode: null,
        vendorId: null,
        vendorCode: null,
        vendorName: null,
        symbolType: null,
        proportion: null,
        startDate: null,
        endDate: null
      })
    },
    getCategoryObj (val, scope) {
      scope.categoryId = val ? val.categoryId : ''
      scope.categoryCode = val ? val.categoryCode : ''
      scope.categoryName = val ? val.categoryName : ''
    },
    getVendorObj (val, scope) {
      scope.vendorId = val ? val.companyId : ''
      scope.vendorCode = val ? val.companyCode : ''
      scope.vendorName = val ? val.companyName : ''
    },
    // 删除协议比例
    deleteAgreementRatioItem (index, row) {
      if (row.agreementRatioId) {
        this.$http({
          url: '/api-inq/inquiry/agreementRatio/delete',
          method: 'GET',
          params: { id: row.agreementRatioId },
          loading: true
        })
          .then(data => {
            this.$message({
              type: 'success',
              message: this.$t('common.successDelete')
            })
            this.agreementRatioList.splice(index, 1)
          })
          .catch(err => {
            console.log(err)
          })
      } else {
        this.agreementRatioList.splice(index, 1)
      }
    },

    // 新增价差标准
    addPriceStandardItem () {
      this.priceStandardList.push({
        priceStandardId: null,
        quotaId: null,
        categoryId: null,
        categoryName: null,
        spread: null,
        proportion: null
      })
    },
    // 删除价差标准
    deletePriceStandardItem (index, row) {
      if (row.priceStandardId) {
        this.$http({
          url: '/api-inq/inquiry/priceStandard/delete',
          method: 'GET',
          params: { id: row.priceStandardId },
          loading: true
        })
          .then(data => {
            this.$message({
              type: 'success',
              message: this.$t('common.successDelete')
            })
            this.priceStandardList.splice(index, 1)
          })
          .catch(err => {
            console.log(err)
          })
      } else {
        this.priceStandardList.splice(index, 1)
      }
    },

    // 新增预估返利
    addEstimatedRebateItem () {
      this.quotaRebateList.push({
        quotaRebateId: null,
        quotaId: null,
        vendorId: null,
        vendorCode: null,
        vendorName: null,
        categoryId: null,
        categoryName: null,
        proportion: null
      })
    },
    // 删除预估返利
    deleteEstimatedRebateItem (index, row) {
      if (row.quotaRebateId) {
        this.$http({
          url: '/api-inq/inquiry/quotaRebate/delete',
          method: 'GET',
          params: { id: row.quotaRebateId },
          loading: true
        })
          .then(data => {
            this.$message({
              type: 'success',
              message: this.$t('common.successDelete')
            })
            this.quotaRebateList.splice(index, 1)
          })
          .catch(err => {
            console.log(err)
          })
      } else {
        this.quotaRebateList.splice(index, 1)
      }
    },

    // 预设比例集合set数据处理
    setQuotaNumberHandle () {
      for (let item of this.quotaPreinstallList) {
        item['quotaPreinstallNumber'] = []
        for (let i = 1; i <= item['supplierNumber']; i++) {
          item['quotaPreinstallNumber'].push(item['quotaPreinstallNumber' + i] || 0)
        }
        item['quotaPreinstallNumber'] = item['quotaPreinstallNumber'].join(',')
      }
    },
    saveBill () {
      let buIdArr = this.quotaBuDTOList.map(v => v.buCode)
      let newArr = Array.from(new Set(buIdArr))
      if (buIdArr.length !== newArr.length) {
        return this.__jump_error__(
          'department',
          null,
          this.$t('quota.buOrgTips'), // 请选择不重复的事业部!
        )
      }
      // 比例数据输入范围0-100校验
      for (let item of this.quotaRestrictionsList) {
        if (item['proportion'] < 0 || item['proportion'] > 100) {
          return this.__jump_error__(
            'quotaLimit',
            null,
            this.$t('quota.proportionTips'), // 比例输入范围应为0~100!
          )
        }
      }
      for (let item of this.agreementRatioList) {
        if (item['proportion'] < 0 || item['proportion'] > 100) {
          return this.__jump_error__(
            'agreementRatio',
            null,
            this.$t('quota.proportionTips'), // 比例输入范围应为0~100!
          )
        }
      }
      for (let item of this.priceStandardList) {
        if (item['proportion'] < 0 || item['proportion'] > 100) {
          return this.__jump_error__(
            'priceStandard',
            null,
            this.$t('quota.proportionTips'), // 比例输入范围应为0~100!
          )
        }
      }
      for (let item of this.quotaRebateList) {
        if (item['proportion'] < 0 || item['proportion'] > 100) {
          return this.__jump_error__(
            'estimatedRebate',
            null,
            this.$t('quota.proportionTips'), // 比例输入范围应为0~100!
          )
        }
      }
      this.setQuotaNumberHandle() // 预设比例集合set数据处理
      for (let item of this.quotaPreinstallList) {
        let lineTotal = item['quotaPreinstallNumber']
          .split(',')
          .reduce((c, p) => Number(c) + Number(p))
        if (lineTotal != 100) {
          return this.__jump_error__(
            'presetScale',
            null,
            this.$t('quota.quotaPreinstallNumberTips'), // 预设比例每行之和应为100!
          )
        }
      }
      this.$http({
        url: '/api-inq/inquiry/quota/updateAndAdd',
        method: 'POST',
        data: {
          quota: this.quota, // 基本信息
          quotaBuList: this.quotaBuDTOList, // 事业部
          quotaPreinstallList: this.quotaPreinstallList, // 预设比例
          quotaRestrictionsList: this.quotaRestrictionsList, // 配额上下限
          agreementRatioList: this.agreementRatioList, // 协议比例
          priceStandardList: this.priceStandardList, // 价差标准
          quotaRebateList: this.quotaRebateList // 预估返利
        },
        loading: true
      })
        .then(data => {
          this.$message({
            message: this.$t('common.successSave'),
            type: 'success'
          })
          if (this.$attrs.params.flag == 'edit') {
            this.$emit('tab-remove', 'quotaConfigDetail' + this.$attrs.params.row.quotaCode)
          } else {
            this.$emit('tab-remove', 'quotaConfigDetail')
          }
          this.__setTabTodo('quotaConfigList.getQuerydata')
        })
        .catch(err => {
          console.log(err)
        })
    }
  }
}
</script>
<style scoped lang="scss">
.the-quotaConfigDetail-detail {
  .form-container2 {
    padding: 5px;
  }

  .el-table .el-date-editor {
    width: 135px;
  }

  .btn_line {
    margin: 0 0 10px 0;
  }
}
</style>
