<template>
  <SrmDialog
    size="large"
    :title="$t('bidMod.biddingControl.startNewRound')"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    append-to-body
  >
    <VxeTable
      ref="xTable"
      border
      show-overflow="tooltip"
      keep-source
      align="left"
      height="300"
      :data="requireInfoData"
      :valid-config="{ showMessage: false }"
      :edit-rules="vxeTableValidRules"
      :edit-config="{
        trigger: 'click',
        mode: 'row',
        autoClear: false,
        enabled: true
      }"
    >
      <!--序号-->
      <VxeColumn
        type="seq"
        :title="$t('common.sort')"
        width="60"
      />

      <!--物料编码-->
      <VxeColumn
        field="itemCode"
        :title="$t('bidMod.targetNum')"
        min-width="130"
      />

      <!--物料名称-->
      <VxeColumn
        field="itemDesc"
        :title="$t('bidMod.targetDesc')"
        min-width="150"
      />
      <!--采购品类-->
      <VxeColumn
        field="categoryName"
        :title="$t('perfMod.categoryId')"
        min-width="150"
      />

      <!--单位-->
      <VxeColumn
        field="unit"
        :title="$t('bidMod.appraisUnit')"
        min-width="150"
        :formatter="({ cellValue }) => $getDictLabel('unit', cellValue)"
      />

      <!--上一轮最低价-->
      <VxeColumn
        field="souItemId"
        :title="baseInfo.scoreRuleType === 'MIN_PRICE' ? '上一轮最低价' : '上一轮最高价'"
        min-width="140"
      >
        <template #default="{ row }">
          <span>{{ computedLatestPrice(row.souItemId) }}</span>
        </template>
      </VxeColumn>

      <!--新一轮起拍价-->
      <VxeColumn
        field="orderStartPrice"
        :title="$t('bidMod.competitionLts.newStartingPrice')"
        min-width="125"
        :edit-render="{}"
      >
        <template #edit="{ row, $rowIndex }">
          <ElInput v-model="row.orderStartPrice" v-input-format="{ type: 'float' }" />
        </template>
        <template #default="{ row }">
          <ElInput v-model="row.orderStartPrice" v-input-format="{ type: 'float' }" />
        </template>
      </VxeColumn>

      <!--新一轮流拍价-->
      <!-- <vxe-column
        field="startingClearancePrice"
        title="新一轮流拍价"
        min-width="140"
      /> -->
    </VxeTable>

    <template #footer>
      <ElButton @click="dialogVisible = false">
        {{ $t('common.prevOne') }}
      </ElButton>

      <ElButton type="primary" @click="submit">
        {{ $t('common.publish') }}
      </ElButton>
    </template>
  </SrmDialog>
</template>

<script>
/**
 * 设置新一轮起拍价
 */
import { indexWarningMessage } from 'lib@/composition/origin/composition'
import { carBuyerHttp } from 'modb@/competition/api'
import { transformMQL } from 'lib@/utils/util'

export default {
  name: 'SetNewRoundStartingPrice',

  props: {
    visible: {
      type: Boolean,
      required: true
    },
    baseInfo: {
      type: Object,
      default: () => ({})
    },
    requireInfoData: {
      type: Array,
      default: () => []
    },
    currentRoundSouItemHisMap: {
      type: Object,
      default: () => ({})
    }
  },

  data () {
    return {
      historyPriceList: []
    }
  },

  computed: {
    dialogVisible: {
      get: function () {
        return this.visible
      },
      set: function (val) {
        this.$emit('update:visible', val)
      }
    },

    // 校验
    vxeTableValidRules () {
      return {
        orderStartPrice: [{ required: true }]
      }
    },
    // 上一轮最低价/最高价
    computedLatestPrice (souItemId) {
      console.log('souItemId', souItemId)
      return (souItemId) => {
        let obj = this.historyPriceList.find(item => item.souItemId === souItemId)
        return obj ? obj.latestOrderNotaxPrice : null
      }
    }
  },

  created () {
    this.getLatestPrice()
  },

  methods: {
    /* 提交 */
    async submit () {
      const { fullData } = this.$refs.xTable.getTableData()

      for (const [index, item] of fullData.entries()) {
        if (!item.orderStartPrice) {
          // 请设置新一轮起拍价!
          indexWarningMessage(index, this.$t('bidMod.competitionLts.newStartingPriceTips'))
          return
        }
      }

      this.$emit('submit', {
        souItemList: fullData
      })
    },
    /** 获取上一轮最低价/最高价 */
    async getLatestPrice () {
      const { currentRound, projectId } = this.baseInfo
      let transformParams = transformMQL.save('AuctSouItemHis', {
        filter: {
          projectId: {
            eq: projectId
          },
          round: {
            eq: currentRound
          }
        }
      }, 'query')
      let response = await carBuyerHttp.init.getLatestPrice(transformParams)
      if (response.data.records?.length) {
        this.historyPriceList = response.data.records
      }
    }
  }
}
</script>
