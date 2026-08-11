<template>
  <div class="sign-up-info">
    <div class="the_display_content">
      <SrmRow>
        <!--报名状态-->
        <SrmCol :init-col="3">
          <span>{{ $t("bidMod.signUpStatus") }}</span>{{ $getDictLabel('SOU_SIGN_UP_STATUS', (signUp || {}).signUpStatus) }}
        </SrmCol>
        <!--驳回原因-->
        <SrmCol v-if="(signUp || {}).rejectReason" :init-col="3">
          <span>{{ $t("bidMod.dismissReason") }}</span>{{ (signUp || {}).rejectReason }}
        </SrmCol>
      </SrmRow>
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
          prop: 'souDocId',
          nameProp: 'souFileName',
          minHeight: '200'
        }"
        readonly
      />

      <!--采购商备注-->
      <el-table-column
        align="center"
        prop="souRemark"
        :label="$t('bidMod.vendorRemark')"
        show-overflow-tooltip
      />

      <!--报名资料-->
      <SrmCommonFile
        type="table-column"
        :table-column-options="{
          label: $t('bidMod.registrationInfo'),
          prop: 'signUpDocId',
          nameProp: 'signUpFileName',
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
import { bidSupplierHttp } from 'modcs@/biddingSupplier/api'

export default {
  name: 'RegisterInfo',

  props: {
    isCurrentTab: {
      type: Boolean,
      required: true
    },
    projectId: {
      type: [Number, String],
      required: true
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
    async getSignUpInfo () {
      const response = await bidSupplierHttp.order.signUpInfo(this.projectId)

      if (response && response.data) {
        const { vendor = {}, signUpFileList = [] } = response.data
        this.signUp = vendor
        this.signUpFiles = signUpFileList
      }
    }
  }
}
</script>
