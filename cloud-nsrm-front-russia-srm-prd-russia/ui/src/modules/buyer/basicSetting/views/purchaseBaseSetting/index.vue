<template>
  <NavTabs
    ref="tabs"
    :isFixedFirstItem="false"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import CurrencySetting from './CurrencySetting'
import RateSetting from './RateSetting'
import UnitSetting from './UnitSetting'
import ExchangeRateSetting from './ExchangeRateSetting'

export default {
  name: 'PurchaseBaseSetting',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'currency', // 当前激活标签  与name相同
      tabs: [
        // '币种设置'
        {
          title: () => this.$t('dataConfMod.currencySetting'),
          name: 'currency',
          component: CurrencySetting,
          closable: false
        },
        // 单位设置
        {
          title: () => this.$t('dataConfMod.unitSetting'),
          name: 'unit',
          component: UnitSetting,
          closable: false
        },
        // 税率设置
        {
          title: () => this.$t('dataConfMod.rateSetting'),
          name: 'rate',
          component: RateSetting,
          closable: false
        },
        // 汇率设置
        {
          title: () => this.$t('dataConfMod.exchangeRateSetting'),
          name: 'exchangeRate',
          component: ExchangeRateSetting,
          closable: false
        }
      ]
    }
  },
  mounted () {
    if (this.$route.params) {
      if (this.$route.params.tabName) {
        this.$refs.tabs.tabShow(this.$route.params.tabName)
      } else {
        this.$refs.tabs.tabShow('currency')
      }
    }
    // 即将进行【采购基础设置】，您需要完成：1、启用币种，根据企业实际业务所用的币种进行启用2、启用单位，根据企业实际业务所用的单位进行启用 3、启用税率，根据企业实际业务所用的税率进行启用
    // '提示'
    // '开始'
    let puschaseTip = localStorage.getItem('puschaseTip') || 'Y'
    if (puschaseTip === 'Y') {
      this.$confirm(
        this.$t('dataConfMod.purchaseBaseSettingAlert'),
        this.$t('common.tips'), {
          distinguishCancelAndClose: true,
          confirmButtonText: this.$t('common.start'),
          cancelButtonText: this.$t('common.toNotshowTip')
        }).then(() => {
        // 点击开始
      }).catch(() => {
        // 不再提示
        localStorage.setItem('puschaseTip', 'N')
      })
    }
  }
}
</script>
