<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-main class="el-main">
      <!--进来页面未认证第一步操作-->
      <stepOne v-if="goTo === 'stepOne'" @goToWhere="goToWhere" />
      <!--提交成功后展示的界面-->
      <SuccessE v-if="goTo === 'success'" @goToWhere="goToWhere" />
      <!--企业性质界面-->
      <CompanyNature
        v-if="goTo === 'company-nature'"
        :relModel="relModel"
        :allParam="allParam"
        @goToWhere="goToWhere"
        @switchBaseRules="switchBaseRules"
        @backData="backData"
      />
      <!--显示主要页面-->
      <MainView
        v-if="goTo === 'main'"
        :allParam="allParam"
        @goToWhere="goToWhere"
        @switchBaseRules="switchBaseRules"
      />
    </el-main>
  </el-container>
</template>
<script>
import stepOne from './stepOne'
import SuccessE from './success'
import CompanyNature from './companyNature'
import MainView from './main'
import { vendorOptCommonApi } from 'mod@/common/userManage/api'

export default {
  name: 'CompanyInfoMaintain',
  components: {
    stepOne,
    SuccessE,
    CompanyNature,
    MainView
  },
  data () {
    return {
      companyId: '',
      goTo: '',
      curRel: null,
      relModel: {
        // 境内外关系
        relform: {
          overseasRelation: '',
          companyType: '',
          orgId: '',
          orgCode: '',
          orgName: ''
        },
        rules: {
          overseasRelation: [
            {
              required: true,
              message: this.$t('vendorMod.msgOverseasRelation')
            }
          ], // '请选择境内外关系'
          companyType: [
            { required: true, message: this.$t('vendorMod.msgCompanyType') }
          ] // '请选择企业性质'
        }
      },
      allParam: {
        rules: {},
        plantInfos: [],
        orgInfos: [],
        orgCategorys: [],
        companyInfo: {
          companyId: null,
          status: '',
          overseasRelation: 'INSIDE',
          companyType: 'GUOYOU',
          businessLicenseFileId: '',
          businessLicense: '',
          companyName: '',
          categoryName: '',
          registeredCapital: '',
          registCurrency: '',
          registCurrencyName: '',
          companyCreationDate: '',
          businessDate: '',
          companyShortName: '',
          lcCode: '',
          legalPerson: '',
          businessStartDate: '',
          businessEndDate: '',
          supplierType: 'MATERIAL',
          ifLongPeriod: 'N',
          companyCountry: '',
          companyProvince: '',
          companyCity: '',
          companyAddress: '',
          dunsCode: '',
          registrationAuthority: '',
          ceeaBusinessModel: '',
          ceeaSupBusinessType: '',
          ceeaCompanyIntro: '',
          businessScope: '',
          categoryRels: '',
          ceeaPlantType: '',
          ceeaPlantArea: '',
          ceeaAgentBrand: '',
          ceeaIfListed: 'Y',
          ceeaListedTime: '',
          ceeaHasParentCompany: 'Y',
          ceeaParentCompanyName: '',
          ceeaParentCompanyLcCode: '',
          //
          staffQuantity: '',
          managerQuantity: '',
          technicistQuantity: '',
          productorQuantity: '',
          ifRad: 'Y',
          radStaffQuantity: '',
          businessRank: '',
          marketShare: '',
          internationalTopFive: '',
          //
          ceeaCompanyCreationDate: '',
          ceeaRegisteredCapital: '',
          ceeaYearTurnover: '',
          ceeaPreThreeYearsSale: '',
          ceeaPreThreeYearsProfit: '',
          ceeaPreThreeYearsAal: '',
          ceeaScopeBusinessRatio: '',
          ceeaIfHasSolarPower: 'Y',
          ceeaUpDownLayout: '',
          ceeaThreeScaleChangeExp: '',
          ceeaReducePurCostAdvise: '',
          ceeaProCostPlanStrategy: '',
          ceeaRdSaleRate: '',
          ceeaProGoodBad: '',
          ceeaProTechRoute: '',
          ceeaTeamShapeAbility: '',
          ceeaProPriceInscapeRate: '',
          ceeaReduceCostFactor: '',
          ceeaHowUpgradePrice: '',
          ceeaAfterSalesAbility: ''
        },
        companyInfoDetail: {
          companyId: null,
          staffQuantity: '',
          managerQuantity: '',
          technicistQuantity: '',
          productorQuantity: '',
          ifRad: 'Y',
          radStaffQuantity: '',
          businessRank: '',
          marketShare: '',
          internationalTopFive: ''
        },
        contactInfos: [],
        bankInfos: [],
        operationInfo: {
          opInfoId: null,
          companyId: null,
          ceeaCompanyCreationDate: '',
          ceeaRegisteredCapital: '',
          ceeaYearTurnover: '',
          ceeaPreThreeYearsSale: '',
          ceeaPreThreeYearsProfit: '',
          ceeaPreThreeYearsAal: '',
          ceeaScopeBusinessRatio: '',
          ceeaIfHasSolarPower: 'Y',
          ceeaUpDownLayout: '',
          ceeaThreeScaleChangeExp: '',
          ceeaReducePurCostAdvise: '',
          ceeaProCostPlanStrategy: '',
          ceeaRdSaleRate: '',
          ceeaProGoodBad: '',
          ceeaProTechRoute: '',
          ceeaTeamShapeAbility: '',
          ceeaProPriceInscapeRate: '',
          ceeaReduceCostFactor: '',
          ceeaHowUpgradePrice: '',
          ceeaAfterSalesAbility: ''
        },
        operationQualities: [],
        operationProducts: [],
        operationEquipments: [],
        businessInfos: [],
        managementInfo: {
          managementInfoId: null,
          companyId: null,
          ifIsoQuality: 'N',
          ifIsoEnviron: 'N',
          ifOhsasSafe: 'N',
          dataSources: '',
          otherAuthSit: ''
        },
        managementAttaches: []
      }
    }
  },
  computed: {

  },
  created () {
    this.companyId = this.$store.getters.companyId
  },
  mounted () {
    let companyId = this.companyId
    if (companyId) {
      vendorOptCommonApi.getCompanyForEdit({ companyId }).then(res => {
        this.allParam = res.data
        let status = res.data.companyInfo ? res.data.companyInfo.status : ''
        if (status == 'SUBMITTED') {
          this.goToWhere('success')
        } else {
          this.goToWhere('main')
        }
      })
    } else {
      this.goToWhere('stepOne')
    }
  },
  updated () {

  },
  methods: {
    backData (allParams) {
      this.allParam = allParams
    },
    goToWhere (where) {
      this.goTo = where
    },
    // 切换必填规则
    switchBaseRules () {
      if (this.curRel) {
        let rules = {}
        if (this.curRel === 'OUT') {
          // 境外
          rules = Object.assign(this.baseRules, this.outRules)
        } else if (this.curRel === 'INSIDE') {
          if (this.curType === 'GETI') {
            // 个体户
            rules = Object.assign(this.baseRules, this.sosoRules)
          } else if (this.curType === 'FEIYINGLI') {
            // 非盈利
            rules = Object.assign(this.baseRules, this.unProfitRules)
          } else {
            // 其他内部
            rules = Object.assign(this.baseRules, this.innerRules)
          }
        } else {
          rules = this.baseRules
        }
        this.allParam.rules = rules
      } else {
        this.allParam.rules = this.baseRules
      }
    }
  }
}
</script>
<style lang="scss" scoped>

</style>
