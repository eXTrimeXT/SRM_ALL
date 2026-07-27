<template>
  <div class="inquiry-detail-vendor">
    <p v-if="isOpenTender">
      {{ $t('bidMod.inviteOpenVendor') }}
    </p>
    <template v-if="!isOpenTender">
      <p>{{ $t("bidMod.inviteVendorMessage") }}</p>
      <OriginInviteSuppliers
        ref="inviteSuppliers"
        :business-type="BUSINESS_TYPE_ENUM.INQUIRY_LTS"
        :invite-suppliers-data="vendorsData"
        :material-data="items"
        :base-info="header"
        :is-readonly="readonly"
      />
    </template>
  </div>
</template>

<script>
/**
 * 邀请供应商
 */
import { BUSINESS_TYPE_ENUM, SOU_PUBLISH_SCOPE_ENUM } from 'lib@/composition/origin/enum'
import OriginInviteSuppliers from 'lib@/composition/origin/inviteSuppliers/index.vue'

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
    readonly: {
      type: Boolean,
      required: true
    }
  },

  data () {
    return {
      BUSINESS_TYPE_ENUM
    }
  },

  computed: {
    vendorsData: {
      get: function () {
        return this.vendors
      },
      set: function (val) {
        this.$emit('update:vendors', val)
      }
    },

    // 是否是公开招标
    isOpenTender () {
      return this.header.publishScope === SOU_PUBLISH_SCOPE_ENUM.OPEN_TENDER
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
            this.$message.warning(this.$t('bidMod.inpVendorInfo'))
            resolveStatus = false
          }

          const findIndex = dataList.findIndex(item => !item.vendorCode)
          if (findIndex >= 0) {
            // 供应商必填
            this.$message.warning(this.$t('bidMod.chooseLineVendor', { index: findIndex + 1 }))
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
