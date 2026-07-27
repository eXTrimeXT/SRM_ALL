<template>
  <div class="wrapper">
    <div class="header">
      <!-- <span class="title">综合评审结果</span> -->
      <span class="title">{{ $t("cusEntry.supplement20250205.comprehensiveReviewResults") }}</span>
      <el-button class="ml-20" @click="viewRisk">
        <!-- 查看供应商风险 -->
        {{ $t("cusEntry.supplement20250121.viewSupplierRisks") }}
        <!-- <span class="red">（{{ tableData.length }}）</span> -->
      </el-button>
    </div>
    <BaseTable
      stripe
      index
      :data="tableData"
      :columns="tableColumns"
      :empty-text="$t('components.noData')"
      border
    >
      <template #comprehensiveEvaluation="scope">
        <el-input v-if="!readonly" v-model="scope.row.comprehensiveEvaluation" />
        <span v-else>{{ scope.row.comprehensiveEvaluation }}</span>
      </template>
      <template #priceScore="scope">
        <el-input-number v-if="!readonly" v-model="scope.row.priceScore" style="width:100%;" />
        <span v-else>{{ scope.row.priceScore }}</span>
      </template>
      <template #techScore="scope">
        <el-input-number v-if="!readonly" v-model="scope.row.techScore" style="width:100%;" />
        <span v-else>{{ scope.row.techScore }}</span>
      </template>
      <template #bidTotalPrice="scope">
        <el-input v-if="!readonly" v-model="scope.row.bidTotalPrice" style="width:100%;" />
        <span v-else>{{ scope.row.bidTotalPrice }}</span>
      </template>
      <template #compositeScore="scope">
        <el-input-number v-if="!readonly" v-model="scope.row.compositeScore" style="width:100%;" />
        <span v-else>{{ scope.row.compositeScore }}</span>
      </template>
      <!-- <template #comprehensiveEvaluation="scope">
        <el-input v-if="!readonly" v-model="scope.row.comprehensiveEvaluation" style="width:100%;" />
        <span v-else>{{ scope.row.comprehensiveEvaluation }}</span>
      </template> -->
    </BaseTable>

    <!-- 查看供应商风险 -->
    <VendorRisk
      :visible.sync="vendorRiskVisible"
      :idList="vendorIdList"
      :applicantNo="applicantNo"
    />
  </div>
</template>
<script>
import BaseTable from 'lib@/components/BaseTable'
import VendorRisk from 'modcb@/supplierRecommend/views/recommendVendor/components/dialog/vendorRisk'

export default {
  components: {
    BaseTable,
    VendorRisk
  },
  props: {
    applicantNo: {
      type: String,
      default: ''
    },
    readonly: {
      type: Boolean,
      default: false
    },
    value: {
      type: Array,
      default: () => []
    },
    // BPM 审批回写
    isWrite: {
      type: Boolean,
      default: false
    },
    extScoreRule: {
      type: String,
      default: ''
    }
  },
  data () {
    return {
      vendorRiskVisible: false,
      tableColumns: [
        {
          attrs: {
            // label: '序号',
            label: () => this.$t('components.common.sort'),
            type: 'index',
            width: 60
          }
        },
        {
          attrs: {
            prop: 'vendorName',
            // label: '供应商名称',
            label: () => this.$t('common.companyName'),
            align: 'center',
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            prop: 'bidTotalPrice',
            // label: '投标含税总价（万元）',
            label: () => this.$t('cusEntry.supplement20250205.bidTotalPriceIncludingTax'),
            align: 'center',
            showOverflowTooltip: true
          },
          slot: 'bidTotalPrice'
        },
        {
          attrs: {
            prop: 'techScore',
            // label: '技术得分',
            label: () => this.$t('bidMod.technicalMerit'),
            align: 'center',
            showOverflowTooltip: true
          },
          slot: 'techScore'
        },
        {
          attrs: {
            prop: 'priceScore',
            // label: '价格得分',
            label: () => this.$t('bid_mod.priceScore'),
            align: 'center',
            showOverflowTooltip: true
          },
          slot: 'priceScore'
        },
        {
          attrs: {
            prop: 'compositeScore',
            // label: '综合得分',
            label: () => this.$t('bidMod.compositeScore'),
            align: 'center',
            showOverflowTooltip: true
          },
          slot: 'compositeScore'
        },
        {
          attrs: {
            prop: 'comprehensiveEvaluation',
            // label: '综合评定',
            label: () => this.$t('cusEntry.supplement20250205.comprehensiveEvaluation'),
            showOverflowTooltip: true,
            align: 'center',
            renderHeader: this._addStarToColumn
          },
          slot: 'comprehensiveEvaluation'
        }
      ]
    }
  },
  computed: {
    tableData: {
      get () {
        return this.value
      },
      set (val) {
        this.$emit('update:value', val)
      }
    },
    vendorIdList () {
      return this.tableData.filter(item => item.vendorId).map(item => item.vendorId)
    }
  },
  watch: {
    extScoreRule: {
      handler (val) {
        if (val != 'LOW_PRICE') {
          this.tableColumns = [
            {
              attrs: {
                // label: '序号',
                label: () => this.$t('components.common.sort'),
                type: 'index',
                width: 60
              }
            },
            {
              attrs: {
                prop: 'vendorName',
                // label: '供应商名称',
                label: () => this.$t('common.companyName'),
                align: 'center',
                showOverflowTooltip: true
              }
            },
            {
              attrs: {
                prop: 'bidTotalPrice',
                // label: '投标含税总价（万元）',
                label: () => this.$t('cusEntry.supplement20250205.bidTotalPriceIncludingTax'),
                align: 'center',
                showOverflowTooltip: true
              },
              slot: 'bidTotalPrice'
            },
            {
              attrs: {
                prop: 'techScore',
                // label: '技术得分',
                label: () => this.$t('bidMod.technicalMerit'),
                align: 'center',
                showOverflowTooltip: true
              },
              slot: 'techScore'
            },
            {
              attrs: {
                prop: 'priceScore',
                // label: '价格得分',
                label: () => this.$t('bid_mod.priceScore'),
                align: 'center',
                showOverflowTooltip: true
              },
              slot: 'priceScore'
            },
            {
              attrs: {
                prop: 'compositeScore',
                // label: '综合得分',
                label: () => this.$t('bidMod.compositeScore'),
                align: 'center',
                showOverflowTooltip: true
              },
              slot: 'compositeScore'
            },
            {
              attrs: {
                prop: 'comprehensiveEvaluation',
                // label: '综合评定',
                label: () => this.$t('cusEntry.supplement20250205.comprehensiveEvaluation'),
                showOverflowTooltip: true,
                align: 'center',
                renderHeader: this._addStarToColumn
              },
              slot: 'comprehensiveEvaluation'
            }
          ]
        } else {
          this.tableColumns = [
            {
              attrs: {
                // label: '序号',
                label: () => this.$t('components.common.sort'),
                type: 'index',
                width: 60
              }
            },
            {
              attrs: {
                prop: 'vendorName',
                // label: '供应商名称',
                label: () => this.$t('common.companyName'),
                align: 'center',
                showOverflowTooltip: true
              }
            },
            {
              attrs: {
                prop: 'bidTotalPrice',
                // label: '投标含税总价（万元）',
                label: () => this.$t('cusEntry.supplement20250205.bidTotalPriceIncludingTax'),
                align: 'center',
                showOverflowTooltip: true
              },
              slot: 'bidTotalPrice'
            },
            {
              attrs: {
                prop: 'techScore',
                // label: '技术得分',
                label: () => this.$t('bidMod.technicalMerit'),
                align: 'center',
                showOverflowTooltip: true
              },
              slot: 'techScore'
            },
            {
              attrs: {
                prop: 'comprehensiveEvaluation',
                // label: '综合评定',
                label: () => this.$t('cusEntry.supplement20250205.comprehensiveEvaluation'),
                align: 'center',
                showOverflowTooltip: true,
                renderHeader: this._addStarToColumn
              },
              slot: 'comprehensiveEvaluation'
            }
          ]
        }
      },
      immediate: true,
      deep: true
    }
  },
  methods: {
    viewRisk () {
      this.vendorRiskVisible = true
    }
  }
}
</script>
<style lang="scss" scoped>
.red {
  color: red;
}
.header {
  margin:10px 0;
  .title {
    font-weight:bold;
  }
}
.ml-20 {
  margin-left: 20px;
}
</style>
