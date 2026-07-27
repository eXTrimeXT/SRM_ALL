<template>
  <div class="bond-pay-info">
    <srm-row>
      <!--保证金金额（万元）-->
      <srm-col :init-col="3" class="info-col">
        <span>{{ $t('bidMod.bondAmount') }}: </span>{{ bondPayInfoData.bondAmount }}
      </srm-col>
      <!--保证金提交方式-->
      <srm-col :init-col="3" class="info-col">
        <span>{{ $t('bidMod.bondMethod') }}: </span>{{ $getDictLabel('BID_BOND_SUBMISSION', bondPayInfoData.bondMethod) }}
      </srm-col>
      <!--保证金提交截止时间-->
      <srm-col :init-col="3" class="info-col">
        <span>{{ $t('bidMod.bondEndDatetime') }}: </span>{{ bondPayInfoData.bondEndTime }}
      </srm-col>
      <!--账户名称-->
      <srm-col :init-col="3" class="info-col">
        <span>{{ $t('vendorMod.bankAccountName') }}: </span>{{ bondPayInfoData.bankAccountName }}
      </srm-col>
      <!--保证金缴纳账号-->
      <srm-col :init-col="3" class="info-col">
        <span>{{ $t('bidMod.bankAccountNum') }}: </span>{{ bondPayInfoData.bankAccountNum }}
      </srm-col>
      <!--开户支行-->
      <srm-col :init-col="3" class="info-col">
        <span>{{ $t('bidMod.bankBranchName') }}: </span>{{ bondPayInfoData.bankBranchName }}
      </srm-col>
      <!--其他说明-->
      <srm-col :init-col="3" class="info-col">
        <span>{{ $t('bidMod.bondDesc') }}: </span>{{ bondPayInfoData.bondDesc }}
      </srm-col>
      <!--缴纳时间-->
      <srm-col :init-col="3" class="info-col">
        <span>{{ $t('bidMod.common.payDateOrigin') }}: </span>{{ bondPayInfoData.payDate }}
      </srm-col>
      <!--缴纳证明-->
      <srm-col :init-col="3" class="info-col">
        <span>{{ $t('bidMod.common.payCert') }}: </span>

        <!--下载-->
        <SrmCommonFile
          :default-file="{
            fileId: bondPayInfoData.payDocId,
            fileName: bondPayInfoData.payFileName
          }"
          readonly
        />
      </srm-col>
    </srm-row>
  </div>
</template>

<script>
/**
 * 供应商查看保证金详情
 */
import { BUSINESS_TYPE, getApiServerNameByBusinessType } from 'lib@/composition/origin/composition'

export default {
  name: 'BondPayInfo',

  props: {
    // 业务类型
    businessType: {
      type: String,
      required: true,
      validator: value => BUSINESS_TYPE.includes(value)
    },
    // 单据基础信息 { id }
    baseInfo: {
      type: Object,
      required: true,
      default: () => {
        return {
          id: ''
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

      this.$http({
        url: `${getApiServerNameByBusinessType(this.businessType)}/bond-management/queryBondForVendor/${this.baseInfo.id}`,
        method: 'GET',
        loading: true
      }).then(data => {
        if (data && data.data) {
          this.bondPayInfoData = data.data
        }
      })
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
    }
  }
}
</style>
