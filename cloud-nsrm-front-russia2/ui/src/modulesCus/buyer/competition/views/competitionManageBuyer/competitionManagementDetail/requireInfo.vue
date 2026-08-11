<template>
  <div class="project-requirements-wrap">
    <!--需求明细-->
    <p>{{ $t('cusEntry.competition.requiredDetail') }}</p>
    <el-table
      ref="requirementTable"
      :data="requirementLineList"
      border
      @row-click="requirementRowClick"
    >
      <el-table-column
        align="center"
        type="index"
        width="50"
      />
      <!-- 物资名称 -->
      <el-table-column
        align="center"
        :label="$t('cusEntry.competition.materialName')"
        prop="itemDesc"
        min-width="120"
        show-overflow-tooltip
      />
      <!-- 组合 -->
      <el-table-column
        align="center"
        :label="$t('cusEntry.competition.combination')"
        prop="itemGroup"
        min-width="120"
        show-overflow-tooltip
      />
      <!-- 所属单位 -->
      <el-table-column
        align="center"
        :label="$t('cusEntry.competition.belongCompany')"
        prop="affiliatedUnit"
        min-width="120"
        show-overflow-tooltip
      />
      <!-- 履约保证金 -->
      <el-table-column
        align="center"
        :label="$t('cusEntry.competition.performanceBond')"
        prop="performanceBond"
        min-width="120"
        show-overflow-tooltip
      />
      <!-- 预付款 -->
      <el-table-column
        align="center"
        :label="$t('cusEntry.competition.advancePayment')"
        prop="advanceCharge"
        min-width="120"
        show-overflow-tooltip
      />
      <!-- 月约产量 -->
      <el-table-column
        align="center"
        :label="$t('cusEntry.competition.monthProduct')"
        prop="monthlyProduction"
        min-width="120"
        show-overflow-tooltip
      />
      <!-- 计量单位 -->
      <el-table-column
        align="center"
        :label="$t('cusEntry.competition.measurementUnit')"
        prop="meteringUnit"
        min-width="120"
        show-overflow-tooltip
      />
      <!-- 起拍价格 -->
      <el-table-column
        align="center"
        :label="$t('cusEntry.competition.startPrice')"
        prop="startPrice"
        min-width="120"
        show-overflow-tooltip
      />
      <!-- 梯次价格 -->
      <el-table-column
        align="center"
        :label="$t('cusEntry.competition.cascadePrice')"
        prop="echelonPrice"
        min-width="120"
        show-overflow-tooltip
      />
    </el-table>

    <!--批量维护付款条款-->
    <PaymentTypeDialog
      v-if="batchMaintainPaymentTypeDialogVisible"
      :visible.sync="batchMaintainPaymentTypeDialogVisible"
      :business-type="BUSINESS_TYPE_ENUM.COMPETITION"
      :edit-row="{ paymentList: purchasePaymentTermList }"
      @savePaymentType="saveBatchPaymentType"
    />
  </div>
</template>

<script>
/**
 * 项目需求
 */
import { BUSINESS_TYPE_ENUM } from 'lib@/composition/origin/enum'
import { carBuyerHttp } from 'modcb@/competition/api'
import { validateRequiredColumn } from 'lib@/mixins/addStarToColumn'
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
import RenderAsyncText from 'lib@/components/provice-city/renderAsyncText'
import PaymentTypeDialog from 'lib@/composition/origin/paymentTypeDialog'
import CCategorySelect from 'lib@/components/c-category-select'
import { transformMQL } from 'lib@/utils/util'

export default {
  name: 'ProjectRequirements',

  components: {
    QuickSearch,
    OrganizationSelector,
    RenderAsyncText,
    PaymentTypeDialog,
    CCategorySelect
  },

  props: {
    baseInfo: {
      type: Object,
      required: true
    },
    // 是否当前tab页
    isCurrentActiveTab: {
      type: Boolean,
      default: false
    },
    readonly: {
      type: Boolean,
      default: false
    }
  },

  data () {
    return {
      batchMaintainPaymentTypeDialogVisible: false,
      purchasePaymentTermList: [],
      requirementLineList: [],
      pickerOptions: {
        disabledDate (time) {
          const today = new Date()
          today.setHours(0)
          today.setMinutes(0)
          today.setSeconds(0)
          today.setMilliseconds(0)
          return time.getTime() < today.getTime()
        }
      },
      BUSINESS_TYPE_ENUM
    }
  },

  computed:{
    isDemandPool(){
      return !!(this.baseInfo.sourceFromType === 'PURCHASE_REQ')
    }
  },

  watch: {
    isCurrentActiveTab: {
      handler (newValue, oldValue) {
        // 切换到当前标签页
        if (newValue && !oldValue) {
          this.listRequireInfo()
        }
      },
      immediate: true
    }
  },

  methods: {
    // 确认选择品类
    comfirmSelect (node, scope) {
      scope.categoryId = node ? node.categoryId : ''
      scope.categoryName = node ? node.categoryName : ''
      scope.categoryCode = node ? node.categoryCode : ''
    },
    /* 查询需求详情 */
    async listRequireInfo () {
      if (!this.baseInfo.projectId) {
        return
      }

      // let transformParams = transformMQL.save('AuctSouProjectForBuyer', [{ projectId: this.baseInfo.projectId }], 'listRequireInfo')
      const response = await carBuyerHttp.init.listRequireInfo(this.baseInfo.projectId)
      if (response && response.data) {
        this.requirementLineList = response.data.map(item => {
          return {
            ...item,
            ...item.auctSouItem,
            projectId: this.baseInfo.projectId,
            editable: false
            // 默认基本信息中的价格有效期
            // priceStartTime: this.baseInfo.priceStartTime,
            // priceEndTime: this.baseInfo.priceEndTime
          }
        })

        // 付款条款全部统一
        if (this.requirementLineList.length > 0) {
          this.purchasePaymentTermList = this.requirementLineList[0].paymentList || []
        }
      }
    },

    /* 新增一行物料行 */
    addRow () {
      this.requirementLineList.push({
        projectId: this.baseInfo.projectId,
        editable: true,
        orgOuId: '',
        orgInvId: '',
        orgInvName: '',
        orgInvCode: '',
        categoryId: '',
        categoryName: '',
        itemGroup: '',
        itemCode: '',
        itemDesc: '',
        taxCurrentPrice: '',
        orderStartPrice: '',
        exchangeRateType: '',
        taxKey: null,
        requireQuantity: '',
        buyAmount: '',
        priceType: 'STANDARD',
        purchaseType: '',
        unit: '',
        remark: '',
        requireDate: '',
        rowType: '',
        // 默认基本信息中的价格有效期
        priceStartTime: this.baseInfo.priceStartTime,
        priceEndTime: this.baseInfo.priceEndTime
      })
    },

    /* 删除物料行 */
    deleteRow (index) {
      this.requirementLineList.splice(index, 1)
    },

    /* 批量维护付款条款 */
    // 打开
    openBatchMaintainPaymentTypeDialog () {
      if (this.requirementLineList.length === 0) {
        this.$message.warning('请至少添加一行需求！')
        return
      }
      this.batchMaintainPaymentTypeDialogVisible = true
    },
    // 保存
    saveBatchPaymentType (data) {
      this.purchasePaymentTermList = data
    },

    /* 行点击 */
    requirementRowClick (row) {
      if (this.readonly) {
        return
      }
      row.editable = true
    },

    /* 选择组织 */
    selectOrg (val, id, { row, $index }) {
      if (val && row.orgOuId === val.organizationId) {
        return
      }

      row.orgOuId = val ? val.organizationId : ''
      row.orgOuCode = val ? val.organizationCode : ''
      row.orgOuName = val ? val.organizationName : ''
      this.$refs[`inv_${$index}`].clearOptions()
      row.orgInvId = ''
      row.orgInvCode = ''
      row.orgInvName = ''

      // 重选业务实体，清空物料选择
      if (row.itemId) {
        // 非无料号
        this.selectItem(null, row)
      }
    },

    /* 选择库存组织 */
    selectInv (val, id, row) {
      if (val && row.orgInvId === val.organizationId) {
        return
      }

      row.orgInvId = val ? val.organizationId : ''
      row.orgInvCode = val ? val.organizationCode : ''
      row.orgInvName = val ? val.organizationName : ''

      // 重选库存组织，清空物料选择
      if (row.itemId) {
        // 非无料号
        this.selectItem(null, row)
      }
    },

    /* 是否无料号寻源勾选 */
    noCodeItemChange ({ row }) {
      // 无料号，清空原有选的料号相关数据
      row.itemId = ''
      row.itemCode = ''
      row.itemDesc = ''
      row.unit = ''
      row.categoryId = ''
      row.categoryCode = ''
      row.categoryName = ''
    },

    /* 选择物料 */
    selectItem (val, row) {
      row.itemId = val ? val.materialId : ''
      row.itemCode = val ? val.materialCode : ''
      row.itemDesc = val ? val.materialName : ''
      row.unit = val ? val.unit : ''
      row.categoryId = val ? val.categoryId : ''
      row.categoryCode = val ? val.categoryCode : ''
      row.categoryName = val ? val.categoryName : ''
    },

    /* 判断先选业务实体和库存组织才能选物料 */
    itemCodeQuickSearchBeforeOpen (row, callback) {
      if (!row.orgOuId || !row.orgInvId) {
        this.$message.warning('请先选择业务实体以及库存组织')
        callback(null)
      }
    },

    /* 选择税率 */
    selectTax (val, dictItem, row) {
      row.exchangeRateType = dictItem.key
    },

    /* 保存 */
    async saveRequirement (type) {
      // 校验表格必填项
      if (!validateRequiredColumn(
        this.$refs.requirementTable,
        this.requirementLineList,
        {
          validateScope: false,
          tableTitle: '需求明细',
          excludeProperty: ['itemCode']
        }
      )) {
        return { status: false }
      }

      for (let i = 0; i < this.requirementLineList.length; i++) {
        let item = this.requirementLineList[i]
        if (item.noCodeItem !== 'Y' && !item.itemCode) {
          this.$message.warning(`第${i + 1}行的物料编码不能为空`)
          return { status: false }
        }
      }

      // 请批量维护付款条款
      if (this.purchasePaymentTermList.length === 0) {
        this.$message.warning('请批量维护付款条款')
        return { status: false }
      }

      try {
        let params = {
          projectId: this.baseInfo.projectId,
          // 物料需求信息
          itemList: this.requirementLineList.map(item => {
            return {
              ...item,
              auctSouItem: {
                orderStartPrice: item.orderStartPrice,
                orderNoBidPrice: item.orderNoBidPrice,
                priceType: item.priceType,
                tradeTerm: item.tradeTerm,
                warrantyPeriod: item.warrantyPeriod
              },
              // 冗余付款条款
              paymentList: this.purchasePaymentTermList.concat()
            }
          }),
          // 是否暂存
          tempSave: type !== 'nextOne'
        }
        let transformParams = transformMQL.save('AuctSouProjectForBuyer', [params], 'editRequireInfo')
        const response = await carBuyerHttp.init.editRequireInfo(transformParams)

        if (response) {
          this.$message.success(this.$t('common.successSave'))
          return { status: true, data: response.data.records[0] }
        } else {
          return { status: false }
        }
      } catch (e) {
        return { status: false }
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.project-requirements-wrap {
  .table-toolbar-wrap {
    padding-bottom: 10px;
  }
}
</style>
