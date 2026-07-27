<template>
  <srm-dialog
    :title="addType === 'recommendVendor' ? $t('cusEntry.bidMod.recommendVendor') : $t('cusEntry.bidMod.historyLowPrice')"
    size="middle"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    append-to-body
  >
    <el-table
      :data="vendorList"
      style="width: 100%"
      border
      height="333px"
      highlight-current-row
      @selection-change="handleVendorSelection"
    >
      <el-table-column
        type="index"
        :label="$t('common.sort')"
        width="50"
      />
      <el-table-column
        type="selection"
        width="55"
      />
      <el-table-column
        prop="vendorCode"
        :label="$t('bidMod.vendorCode')"
        width="150"
        show-overflow-tooltip
      />
      <el-table-column
        prop="vendorName"
        :label="$t('bidMod.vendorName')"
        min-width="150"
        show-overflow-tooltip
      />
    </el-table>

    <div
      slot="footer"
      class="dialog-footer"
    >
      <el-button @click="dialogVisible = false">
        {{ $t("common.cancel") }}
      </el-button>
      <el-button
        type="primary"
        @click="saveRecommendVendor"
      >
        {{ $t("common.confirm") }}
      </el-button>
    </div>
  </srm-dialog>
</template>

<script>
/**
 * 智能推荐供应商
 */
export default {
  name: 'RecommendVendorDialog',

  props: {
    visible: {
      type: Boolean
    },
    // 物料列表
    itemList: {
      type: [Array, Object]
    },
    // 单据基础信息
    baseInfo: {
      type: Object,
      default: () => {}
    },
    // 已添加的供应商
    vendorsData: {
      type: Array
    },
    addType: {
      type: String,
      default: 'recommendVendor'
    }
  },

  data () {
    return {
      vendorList: [],
      multipleVendorSelection: []
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
    }
  },
  created () {
    this.getVendor()
  },
  methods: {
    /* 查询供应商 */
    getVendor () {
      if (!this.itemList.length) {
        this.$message.warning(this.$t('bidMod.common.itemAddMsg'))
        return
      }
      const url = this.addType === 'recommendVendor' ? `/api-sou/npm/buyer/inq/init/getVendorAiRecommend/${this.baseInfo.projectId}` : `/api-sou/npm/buyer/inq/init/getHistoryMinPriceVendors/${this.baseInfo.projectId}`
      this.$http({
        url,
        method: 'GET',
        loading: true
      }).then(data => {
        if (data && data.data) {
          this.vendorList = (data.data || []).map(item => {
            // 重构数据结构，只取需要的字段
            return {
              vendorId: item.companyId,
              vendorCode: item.companyCode,
              vendorName: item.companyName,
              contactName: item.contactName,
              ceeaContactMethod: item.ceeaContactMethod,
              email: item.email,
              availableCategoryIds: item.availableCategoryIds || [],
              availableOrgIds: item.availableOrgIds || []
            }
          })
        }
      })
    },

    /* 记录选择 */
    handleVendorSelection (val) {
      this.multipleVendorSelection = val
    },

    /* 提交供应商 */
    saveRecommendVendor () {
      if (this.multipleVendorSelection.length === 0) {
        this.$message.warning(this.$t('bidMod.common.vendorSelectMsg'))
        return
      }

      for (let o = 0; o < this.multipleVendorSelection.length; o++) {
        const id = this.multipleVendorSelection[o].vendorId
        if (id && (this.vendorsData || []).map(v => v.vendorId).includes(id)) {
          const name = this.multipleVendorSelection[o].vendorName
          this.$message.warning(`${name}${this.$t('bidMod.common.repeatMsg')}`)
          return
        }
      }

      this.$emit('saveRecommendVendor', this.multipleVendorSelection.map(item => {
        return {
          vendorId: item.vendorId,
          vendorCode: item.vendorCode,
          vendorName: item.vendorName,
          contactName: item.contactName,
          ceeaContactMethod: item.ceeaContactMethod,
          email: item.email,
          availableCategoryIds: item.availableCategoryIds || [],
          availableOrgIds: item.availableOrgIds || []
        }
      }))
      this.dialogVisible = false
    }
  }
}
</script>
