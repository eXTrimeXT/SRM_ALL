<template>
  <div class="bond-pay-info">
    <SrmRow>
      <!--保证金金额（万元）-->
      <SrmCol :init-col="3" class="info-col">
        <span>{{ $t('bidMod.bondAmount') }}: </span>{{ bondPayInfoData.bondAmount }}
      </SrmCol>
      <!--保证金提交方式-->
      <SrmCol :init-col="3" class="info-col">
        <span>{{ $t('bidMod.bondMethod') }}: </span>{{ $getDictLabel('BID_BOND_SUBMISSION', bondPayInfoData.bondMethod) }}
      </SrmCol>
      <!--保证金提交截止时间-->
      <SrmCol :init-col="3" class="info-col">
        <span>{{ $t('bidMod.bondEndDatetime') }}: </span>{{ $parseTime(bondPayInfoData.bondEndTime) }}
      </SrmCol>
      <!--账户名称-->
      <SrmCol :init-col="3" class="info-col">
        <span>{{ $t('vendorMod.bankAccountName') }}: </span>{{ bondPayInfoData.bankAccountName }}
      </SrmCol>
      <!--保证金缴纳账号-->
      <SrmCol :init-col="3" class="info-col">
        <span>{{ $t('bidMod.bankAccountNum') }}: </span>{{ bondPayInfoData.bankAccountNum }}
      </SrmCol>
      <!--开户支行-->
      <SrmCol :init-col="3" class="info-col">
        <span>{{ $t('bidMod.bankBranchName') }}: </span>{{ bondPayInfoData.bankBranchName }}
      </SrmCol>
      <!--其他说明-->
      <SrmCol :init-col="3" class="info-col">
        <span>{{ $t('bidMod.bondDesc') }}: </span>{{ bondPayInfoData.bondDesc }}
      </SrmCol>
      <!--缴纳时间-->
      <SrmCol :init-col="3" class="info-col">
        <span>{{ $t('bidMod.common.payDateOrigin') }}: </span>{{ $parseTime(bondPayInfoData.payDate) }}
      </SrmCol>
      <!--缴纳证明-->
      <SrmCol
        :init-col="3"
        class="info-col"
        style="display: flex"
      >
        <span>{{ $t('bidMod.common.payCert') }}: </span>

        <!--下载-->
        <SrmCommonFile
          :default-file="{
            fileId: bondPayInfoData.payDocId,
            fileName: bondPayInfoData.payFileName
          }"
          readonly
        />
      </SrmCol>
    </SrmRow>
  </div>
</template>

<script>
/**
 * 供应商查看保证金详情
 */
import { validatorBusinessType } from 'lib@/composition/origin/composition'
import { getBondPayApiParams } from 'lib@/composition/origin/bondPay/utils'

export default {
  name: 'BondPayInfo',

  props: {
    // 业务类型
    businessType: {
      type: String,
      required: true,
      validator: value => validatorBusinessType(value)
    },
    // 单据基础信息 { id, idKey }
    baseInfo: {
      type: Object,
      required: true,
      default: () => {
        return {
          id: '',
          idKey: ''
        }
      }
    },
    isCurrentTab: {
      type: Boolean,
      default: false
    }
  },

  data () {
    return {
      bondPayInfoData: {}
    }
  },

  watch: {
    isCurrentTab: {
      handler (val) {
        if (val) {
          this.getBondData()
        }
      },
      immediate: true
    }
  },

  methods: {
    /* 获取保证金缴纳数据 */
    async getBondData () {
      if (!this.baseInfo.id) {
        return
      }

      const apiParams = getBondPayApiParams(this.businessType, this.baseInfo)
      if (!apiParams.queryUrl) {
        return
      }

      const response = await this.$api.utils.common(
        apiParams.queryUrl,
        { queryParams: apiParams.queryParams || {} }
      )

      if (response) {
        this.bondPayInfoData = response.data || {}
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.bond-pay-info {
  .el-row {
    margin-bottom: 11px;
    .info-col {
      font-size: 14px;
      position: relative;
      line-height: 30px;
      min-height: 30px;
      text-overflow: ellipsis;
      white-space: nowrap;
      overflow: hidden;
      span {
        padding-right: 10px;
      }
    }
  }
}
</style>
