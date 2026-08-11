<template>
  <div class="sign-up-info">
    <div class="the_display_content">
      <srm-row>
        <!--报名状态-->
        <srm-col :init-col="3">
          <span>{{ $t("bidMod.signUpStatus") }}</span>{{ $getDictLabel('BIDDING_SIGNUP_STATES', (signUp || {}).signUpStatus) }}
        </srm-col>
        <!--驳回原因-->
        <srm-col
          v-if="(signUp || {}).rejectReason"
          :init-col="3"
        >
          <span>{{ $t("bidMod.dismissReason") }}</span>{{ (signUp || {}).rejectReason }}
        </srm-col>
      </srm-row>
    </div>

    <el-table
      :data="signUpFiles"
      style="width: 100%"
      border
      height="345px"
    >
      <el-table-column
        align="center"
        type="index"
        width="50"
      />

      <!--参考附件-->
      <SrmCommonFile
        type="table-column"
        :table-column-options="{
          label: $t('bidMod.refAttachment'),
          prop: 'requireDocId',
          nameProp: 'requireFileName',
          minWidth: '200'
        }"
        readonly
      />

      <!--采购商备注-->
      <el-table-column
        align="center"
        prop="requireComments"
        :label="$t('bidMod.vendorRemark')"
        show-overflow-tooltip
      />

      <!--报名资料-->
      <SrmCommonFile
        type="table-column"
        :table-column-options="{
          label: $t('bidMod.registrationInfo'),
          prop: 'vendorDocId',
          nameProp: 'vendorFileName',
          minWidth: '200'
        }"
        readonly
      />
    </el-table>
  </div>
</template>

<script>
/**
 * 报名信息
 */

export default {
  name: 'RegisterInfo',
  props: {
    isCurrentTab: {
      type: Boolean
    },
    bidingId: {
      type: [Number, String]
    }
  },
  data () {
    return {
      isGetDataStatus: false,
      signUp: {},
      signUpFiles: []
    }
  },
  watch: {
    isCurrentTab: {
      handler (newValue, oldValue) {
        if (newValue && !oldValue && !this.isGetDataStatus) {
          this.isGetDataStatus = true
          this.getSignUpInfo()
        }
      },
      immediate: true
    }
  },
  methods: {
    /* 查询报名信息 */
    getSignUpInfo () {
      this.$http({
        url: `/api-bid/supplierCooperate/orderHead/getBiding/signUpInfo/${this.bidingId}`,
        method: 'GET',
        loading: true
      }).then(data => {
        if (data && data.data) {
          this.signUp = data.data.signUp
          this.signUpFiles = data.data.signUpFiles || []
        }
      })
    }
  }
}
</script>
