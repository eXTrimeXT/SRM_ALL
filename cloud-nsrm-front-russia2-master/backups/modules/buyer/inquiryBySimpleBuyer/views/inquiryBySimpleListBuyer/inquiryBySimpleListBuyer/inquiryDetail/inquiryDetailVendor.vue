<template>
  <div class="inquiry-detail-vendor">
    <p v-if="isOpenTender">
      邀标类型为“公开”，无需邀请供应商，请进行下一步评分设定！
    </p>
    <template v-if="!isOpenTender">
      <p>{{ $t("bidMod.inviteVendorMessage") }}</p>
      <OriginInviteSuppliers
        ref="inviteSuppliers"
        business-type="INQUIRY"
        :invite-suppliers-data="vendorsData"
        :material-data="items"
        :base-info="header"
        :is-readonly="readOnly"
      />
    </template>
  </div>
</template>

<script>
/**
 * 邀请供应商
 */
import OriginInviteSuppliers from 'lib@/composition/origin/inviteSuppliers'

export default {
  name: 'InquiryDetailVendor',

  components: { OriginInviteSuppliers },

  props: {
    vendors: {
      type: [Array, Object],
      required: true
    },
    items: {
      type: [Array, Object],
      required: true
    },
    header: {
      type: Object,
      required: true
    },
    readOnly: {
      type: Boolean,
      required: true
    }
  },

  computed: {
    headerData: {
      get: function () {
        return this.header
      },
      set: function (val) {
        this.$emit('update:header', val)
      }
    },
    vendorsData: {
      get: function () {
        return this.vendors
      },
      set: function (val) {
        this.$emit('update:vendors', val)
      }
    },
    isOpenTender () {
      return this.headerData.publishScope === 'OPEN_TENDER'
    }
  },
  methods: {
    /* 校验 */
    validateForm () {
      return new Promise(resolve => {
        let resolveStatus = true
        const dataList = this.$refs.inviteSuppliers.getSuppliersPermissionData()

        if (!this.isOpenTender) {
          // 不是公开招标
          if (dataList.length === 0) {
            // 邀请招标需要录入供应商信息
            this.$message.warning('请先录入供应商信息!')
            resolveStatus = false
          }

          const findIndex = dataList.findIndex(item => !item.vendorCode)
          if (findIndex >= 0) {
            // 供应商必填
            this.$message.warning(`表格第${findIndex + 1}行请选择一个供应商`)
            resolveStatus = false
          }
        }

        // 编排供应商权限
        this.vendorsData = dataList

        this.$nextTick(() => {
          resolve(resolveStatus)
        })
      })
    }
  }
}
</script>
