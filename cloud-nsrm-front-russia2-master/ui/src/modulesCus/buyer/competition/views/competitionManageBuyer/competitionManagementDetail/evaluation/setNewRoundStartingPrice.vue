<template>
  <SrmDialog
    size="large"
    :title="$t('bidMod.biddingControl.startNewRound')"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    append-to-body
  >
    <vxe-table
      ref="xTable"
      border
      show-overflow="tooltip"
      keep-source
      align="center"
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
      <vxe-column
        type="seq"
        :title="$t('common.sort')"
        width="60"
      />

      <!--物料编码-->
      <vxe-column
        field="itemCode"
        :title="$t('bidMod.targetNum')"
        min-width="130"
      />

      <!--物料名称-->
      <vxe-column
        field="itemDesc"
        :title="$t('bidMod.targetDesc')"
        min-width="150"
      />
      <!--采购品类-->
      <vxe-column
        field="categoryName"
        :title="$t('perfMod.categoryId')"
        min-width="150"
      />

      <!--单位-->
      <vxe-column
        field="unit"
        :title="$t('bidMod.appraisUnit')"
        min-width="150"
        :formatter="({ cellValue }) => $getDictLabel('unit', cellValue)"
      />

      <!--本轮最高/最低价-->
      <!-- <vxe-column
        field="lastOrderPrice"
        :title="baseInfo.scoreRuleType === 'REVERSE_COMP' ? $t('bidMod.lowestPrice') : $t('bidMod.maxPriceRound')"
        min-width="140"
      /> -->

      <!--上一轮最低价-->
      <vxe-column
        field="lastLowestPrice"
        title="上一轮最低价"
        min-width="140"
      />

      <!--新一轮起拍价-->
      <vxe-column
        field="orderStartPrice"
        :title="$t('bidMod.competitionLts.newStartingPrice')"
        min-width="125"
        :edit-render="{}"
      >
        <template #edit="{ row, $rowIndex }">
          <el-input v-model="row.orderStartPrice" v-input-format="{ type: 'float' }" />
        </template>
        <template #default="{ row }">
          <el-input v-model="row.orderStartPrice" v-input-format="{ type: 'float' }" />
        </template>
      </vxe-column>

      <!--新一轮流拍价-->
      <!-- <vxe-column
        field="startingClearancePrice"
        title="新一轮流拍价"
        min-width="140"
      /> -->
    </vxe-table>

    <template #footer>
      <el-button @click="dialogVisible = false">
        {{ $t('common.prevOne') }}
      </el-button>

      <el-button type="primary" @click="submit">
        {{ $t('common.publish') }}
      </el-button>
    </template>
  </SrmDialog>
</template>

<script>
/**
 * 设置新一轮起拍价
 */
import { indexWarningMessage } from 'lib@/composition/origin/composition'
import { compBuyerHttp } from 'modb@/competition/api'

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
    }
  },

  data () {
    return {

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
    }
  },

  created () {
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
    }
  }
}
</script>
