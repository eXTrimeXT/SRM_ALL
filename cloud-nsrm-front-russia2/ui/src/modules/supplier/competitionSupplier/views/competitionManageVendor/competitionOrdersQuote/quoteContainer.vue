<template>
  <div class="quote-container">
    <div class="tabs-toolbar">
      <span class="tips">{{ $t('competition.remindCompetitionTimeTip') }}</span>
    </div>

    <el-tabs v-model="quoteTab" type="border-card">
      <!--报价-->
      <el-tab-pane :label="$t('bidMod.doBiding1')" name="qoute">
        <vxe-table
          ref="xTable"
          border
          show-overflow="tooltip"
          keep-source
          align="left"
          max-height="500"
          :data="itemListData"
          :valid-config="{ showMessage: false }"
          :edit-rules="vxeTableValidRules"
          :edit-config="{
            trigger: 'click',
            mode: 'row',
            autoClear: false,
            enabled: !readonly
          }"
        >
          <!--序号-->
          <vxe-column
            type="seq"
            :title="$t('common.sort')"
            width="60"
          />

          <!--物料编码-->
          <vxe-column
            field="itemCode"
            :title="$t('bidMod.targetNum')"
            width="150"
          />

          <!--物料名称-->
          <vxe-column
            field="itemDesc"
            :title="$t('bidMod.targetDesc')"
            min-width="150"
          />

          <!--规格型号-->
          <vxe-column
            field="typeSpecification"
            :title="$t('vendorMod.specification')"
            min-width="150"
          />

          <!--计价单位-->
          <vxe-column
            field="unit"
            :title="$t('competition.unitCom')"
            width="100"
          >
            <template #default="{ row }">
              {{ $getDictLabel('unit', row.unit) }}
            </template>
          </vxe-column>

          <!--计价数量-->
          <vxe-column
            field="pricingQuantity"
            :title="$t('competition.pricingQuantity')"
            width="100"
          />

          <!--需求数量-->
          <vxe-column
            field="requireQuantity"
            :title="$t('bidMod.demandQuantity2')"
            width="150"
          />

          <!--起拍价(未税)-->
          <vxe-column
            field="startingPrice"
            :title="$t('competition.startingPrice')"
            width="100"
          />

          <!--上次报价-->
          <vxe-column
            field="lastOrderPrice"
            :title="$t('competition.lastOrderPrice')"
            width="100"
          />

          <!--已报价次数-->
          <vxe-column
            field="orderCount"
            :title="$t('bidMod.competitionLts.orderCount')"
            width="100"
          />

          <!--未税单价-->
          <vxe-column
            field="orderNotaxPrice"
            :title="$t('bid_mod.untaxedPrice')"
            width="100"
            :edit-render="{}"
          >
            <template #edit="{ row }">
              <el-input
                v-model="row.orderNotaxPrice"
                v-input-format="{ type: 'float' }"
                :disabled="readonly"
              />
            </template>
            <template #default="{ row }">
              <el-input
                v-model="row.orderNotaxPrice"
                v-input-format="{ type: 'float' }"
                :disabled="readonly"
              />
            </template>
          </vxe-column>

          <!--涨降幅(%)-->
          <vxe-column
            field="floating"
            :title="`${getEvaluateMethodFlag(baseInfo.evaluateMethod)}(%)`"
            width="100"
          />

          <!--税率-->
          <vxe-column
            field="taxKey"
            :title="$t('components.ocr.commodityTaxRate')"
            min-width="150"
            :edit-render="{}"
          >
            <template #edit="{ row }">
              <DictSelect
                v-model="row.taxKey"
                code="tax"
                :disabled="readonly"
              />
            </template>
            <template #default="{ row }">
              <DictSelect
                v-model="row.taxKey"
                code="tax"
                :disabled="readonly"
              />
            </template>
          </vxe-column>

          <!--上传附件-->
          <vxe-column
            field="docId"
            :title="$t('components.upload.uploadFile')"
            min-width="150"
          >
            <template #default="{ row, $rowIndex }">
              <SrmCommonFile
                :default-file="{
                  fileId: row.docId,
                  fileName: row.fileName
                }"
                :readonly="readonly"
                @on-change="value => fileChange(value, $rowIndex)"
              />
            </template>
          </vxe-column>

          <!--操作-->
          <vxe-column
            field="operation"
            :title="$t('components.headers.operation')"
            width="90"
            fixed="right"
          >
            <template #default="{ row, $rowIndex }">
              <!--提交-->
              <el-button
                type="text"
                :disabled="readonly"
                @click="submitRow(row)"
              >
                {{ $t('common.submit') }}
              </el-button>
            </template>
          </vxe-column>
        </vxe-table>
      </el-tab-pane>

      <!--报价记录-->
      <el-tab-pane
        :label="$t('competition.qouteRecord')"
        name="qouteRecord"
        lazy
      >
        <QuoteRecord
          :item-list="itemList"
          :project-id="baseInfo.projectId"
          :is-active-tab="quoteTab === 'qouteRecord'"
        />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
/**
 * 报价容器
 */
import { compVendorHttp } from 'mods@/competitionSupplier/api'
import { getEvaluateMethodFlag } from 'lib@/composition/competition/utils'
import QuoteRecord from './quoteContainer/quoteRecord.vue'

export default {
  name: 'QuoteContainer',

  components: { QuoteRecord },

  props: {
    itemList: {
      type: Array,
      required: true
    },
    orderFileList: {
      type: Array,
      required: true
    },
    baseInfo: {
      type: Object,
      required: true
    },
    readonly: {
      type: Boolean,
      default: false
    }
  },

  data () {
    return {
      quoteTab: 'qoute',
      // 校验配置
      vxeTableValidRules: {
        requireQuantity: [{ required: true }]
      },
      getEvaluateMethodFlag,
      curBusinessVersion: null
    }
  },

  computed: {
    itemListData: {
      get: function () {
        return this.itemList
      },
      set: function (value) {
        this.$emit('update:itemList', value)
      }
    }
  },

  async created () {
    this.curBusinessVersion = await this.getBusinessVersion()
  },

  methods: {
    // 获取单据版本号
    async getBusinessVersion () {
      let businessVersion = null
      if (this.baseInfo.projectId) {
        const res = await this.$http({
          url: '/api-pj/pj/base/lock_business/getLockBusinessVersion',
          method: 'GET',
          params: {
            businessId: this.baseInfo.projectId,
            businessType: 'competitionSupplier_orders_quote' + this.baseInfo.projectId
          },
          loading: true
        })
        businessVersion = res ? res.data : null
      }
      return businessVersion
    },
    // 判断校验单据版本接口报错原因是否因为版本不一致，是则提示用户刷新单据信息
    async versionDiff (businessVersion) {
      if (this.curBusinessVersion != businessVersion) {
        // 单据已被其他账号修改，当前无法提交。是否同步最新单据信息？请注意，此操作将覆盖当前对单据未保存的修改！
        this.$confirm(this.$t('bidMod.esayRFQSupplierTips'), {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        })
          .then(() => {
            this.curBusinessVersion = businessVersion
            // 重新获取详情数据
            this.$emit('submit-success')
          })
          .catch(() => {})
      }
    },
    /* 刷新 */
    refresh () {
      this.$emit('refresh')
    },

    /* 文件变更 */
    fileChange ({ file }, $index) {
      const { fileId = '', fileName = '' } = file || {}
      this.itemListData[$index].docId = fileId
      this.itemListData[$index].fileName = fileName
    },

    /* 提交报价 */
    async submitRow (row) {
      let checkBusinessVersion = false
      try {
        // 校验单据版本号，同一版本号下不允许同时多个账号提交
        await this.$http({
          url: '/api-pj/pj/base/lock_business/checkBusinessVersion',
          method: 'GET',
          params: {
            businessId: this.baseInfo.projectId,
            businessType: 'competitionSupplier_orders_quote' + this.baseInfo.projectId,
            businessVersion: this.curBusinessVersion
          },
          loading: true
        })
      } catch (e) {
        checkBusinessVersion = true
      }
      const businessVersion = await this.getBusinessVersion()
      if (checkBusinessVersion) {
        this.versionDiff(businessVersion)
        return
      } else {
        this.curBusinessVersion = businessVersion
      }

      if (!row.orderNotaxPrice && row.orderNotaxPrice !== 0) {
        this.$message.warning(this.$t('competition.orderNotaxPriceTip'))
        return
      }

      if (!row.taxKey && row.taxKey !== 0) {
        // 请选择税率！
        this.$message.warning(this.$t('cusEntry.supplement20250205.taxRateSelection'))
        return
      }

      const response = await compVendorHttp.orderQuotation.editOrder({
        projectId: this.baseInfo.projectId,
        orderItemList: [{ ...row }],
        orderFileList: this.orderFileList
      })

      if (response) {
        this.$message.success(this.$t('common.successSubmit'))
        this.$emit('submit-success')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.quote-container {
  position: relative;
  margin-top: 15px;
  .tabs-toolbar {
    position: absolute;
    top: 2px;
    left: 190px;
    height: 28px;
    line-height: 28px;
    z-index: 1;
    .tips {
      color: red;
      margin-left: 15px;
    }
  }
}
</style>
