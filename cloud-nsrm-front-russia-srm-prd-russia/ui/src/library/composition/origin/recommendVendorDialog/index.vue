<template>
  <srm-dialog
    :title="$t('bidMod.recommendVendor')"
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
    this.getAiRecommend()
  },

  methods: {
    /* 查询智能推荐供应商 */
    getAiRecommend () {
      if (!this.itemList.length) {
        this.$message.warning(this.$t('bidMod.common.itemAddMsg'))
        return
      }
      this.$http({
        url: '/api-sup/vendorOrgCategory/aiRecommend',
        method: 'POST',
        data: {
          // [业务实体ID: 需求品类ID] 键值对象
          orgCategoryList: this.itemList.map(row => {
            return {
              orgId: row.orgOuId.toString(),
              categoryId: row.categoryId
            }
          }),
          // this.itemList.reduce((prev, next, index) => {
          //   return {
          //     ...(index === 0 ? { [prev.orgOuId]: prev.categoryId } : prev),
          //     [next.orgOuId.toString()]: next.categoryId
          //   }
          // }, this.itemList[0]),
          // 是否需要排除黑名单供应商
          excludeBlackVendors: this.baseInfo.excludeBlackVendors,
          // 是否需要排除非本业务实体内的供应商
          excludeNoCurrentOrgVendors: this.baseInfo.excludeNoCurrentOrgVendors,
          // 是否需要排除业务实体退出的供应商
          excludeOrgQuitVendors: this.baseInfo.excludeOrgQuitVendors,
          // 是否需要排除指定品类状态的供应商
          excludeOrgCategoryStatus: this.baseInfo.excludeOrgCategoryStatus
        },
        loading: true
      }).then(data => {
        if (data && data.data) {
          this.vendorList = (data.data || []).map(item => {
            // 重构数据结构，只取需要的字段
            const vendor = item.vendor || {}
            return {
              vendorId: vendor.companyId,
              vendorCode: vendor.companyCode,
              vendorName: vendor.companyName,
              contactName: vendor.contactName,
              ceeaContactMethod: vendor.ceeaContactMethod,
              email: vendor.email,
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
