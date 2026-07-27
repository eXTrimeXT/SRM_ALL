<template>
  <!--招标保证金-->
  <srm-row>
    <srm-col :init-col="1">
      <el-form-item
        prop="extEarnestFlag"
        :label="$t('cusEntry.bidMod.hasPay')"
      >
        <el-switch
          v-model="biddingBaseInfo.extEarnestFlag"
          active-value="Y"
          inactive-value="N"
          :disabled="readOnly"
        />
      </el-form-item>
    </srm-col>
    <srm-col v-if="biddingBaseInfo.extEarnestFlag=='Y'" :init-col="3">
      <el-form-item
        prop="extEarnestAmount"
        :label="$t('cusEntry.biddingSettings.bondAmount')"
        :rules="[{ required: true, message: this.$t('common.pleaseInput') }]"
      >
        <el-input v-model="biddingBaseInfo.extEarnestAmount" />
      </el-form-item>
    </srm-col>
    <srm-col v-if="biddingBaseInfo.extEarnestFlag=='Y'" :init-col="3">
      <el-form-item
        prop="extBankName"
        :label="$t('cusEntry.biddingSettings.bank')"
        :rules="[{ required: true, message: this.$t('common.pleaseInput') }]"
      >
        <el-input v-model="biddingBaseInfo.extBankName" />
      </el-form-item>
    </srm-col>
    <srm-col v-if="biddingBaseInfo.extEarnestFlag=='Y'" :init-col="3">
      <el-form-item
        prop="extBankNumber"
        :label="$t('cusEntry.biddingSettings.bankNum')"
        :rules="[{ required: true, message: this.$t('common.pleaseInput') }]"
      >
        <el-input v-model="biddingBaseInfo.extBankNumber" />
      </el-form-item>
    </srm-col>
    <srm-col v-if="biddingBaseInfo.extEarnestFlag=='Y'" :init-col="3">
      <el-form-item
        prop="extBankAccount"
        :label="$t('cusEntry.biddingSettings.accountNum')"
        :rules="[{ required: true, message: this.$t('common.pleaseInput') }]"
      >
        <el-input v-model="biddingBaseInfo.extBankAccount" />
      </el-form-item>
    </srm-col>
    <srm-col v-if="biddingBaseInfo.extEarnestFlag=='Y'" :init-col="3">
      <el-form-item
        prop="extBankAccountName"
        :label="$t('cusEntry.biddingSettings.accountName')"
        :rules="[{ required: true, message: this.$t('common.pleaseInput') }]"
      >
        <el-input v-model="biddingBaseInfo.extBankAccountName" />
      </el-form-item>
    </srm-col>
  </srm-row>
</template>

<script>
/**
 * 制定招标计划
 */
export default {
  name: 'BiddingPlan',

  props: {
    biddingBaseInfo: {
      type: Object,
      default: () => {}
    },
    readOnly: {
      type: Boolean,
      default: false
    }
  },

  methods: {

  }
}
</script>
