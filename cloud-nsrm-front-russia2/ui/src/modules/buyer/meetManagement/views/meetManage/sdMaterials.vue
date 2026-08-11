<template>
  <el-container class="flex-container wrapper" direction="vertical">
    <el-main>
      <el-collapse v-model="activeDims">
        <!-- 基础信息 -->
        <el-collapse-item :title="$t('common.baseInfo')" name="1">
          <h3>
            <!-- 项目信息 -->
            {{ $t("vendorMod.itemInformation") }}
          </h3>
          <el-row>
            <el-col :span="6">
              <p>
                <!-- 项目代码 -->
                {{ $t("cusEntry.supplement20250211.projectCode") }}: <span>{{ projectCode }}</span></p>
            </el-col>
            <el-col :span="6">
              <p>
                <!-- 量产时间 -->
                {{ $t("cusEntry.supplement20250211.productionTime") }}: <span>{{ $parseTime(productTime) }}</span></p>
            </el-col>
            <el-col :span="6">
              <p>
                <!-- 规划生命周期产量 -->
                {{ $t("cusEntry.supplement20250211.plannedLifecycleOutput") }}: <span>{{ demandQuantity }}</span></p>
            </el-col>
            <el-col :span="6">
              <p>
                <!-- RFQ包名称 -->
                {{ $t("cusEntry.supplement20250211.rfqPackageName") }}: <span>{{ baseInfo.rfqName }}</span></p>
            </el-col>
            <el-col :span="6">
              <p>
                <!-- 询价单号 -->
                {{ $t("bidMod.inquiryNo") }}: <span>{{ baseInfo.inquiryNo }}</span></p>
            </el-col>
          </el-row>
          <h3>
            <!-- 团队成员 -->
            {{ $t("cusEntry.supplement20250211.teamMember") }}
          </h3>
          <el-row>
            <el-col :span="6">
              <p>
                <!-- 采购工程师 -->
                {{ $t("cusEntry.supplement20250211.purchaseEngineer") }}: <span>{{ purchaseList }}</span></p>
            </el-col>
            <el-col :span="6">
              <p>
                <!-- 研发工程师 -->
                {{ $t("cusEntry.supplement20250211.researchAndDevelopmentEngineer") }}: <span>{{ projectList }}</span></p>
            </el-col>
            <el-col :span="6">
              <p>
                <!-- 质量工程师 -->
                {{ $t("cusEntry.supplement20250211.qualityEngineer") }}: <span>{{ qualityList }}</span></p>
            </el-col>
            <el-col :span="6">
              <p>
                <!-- 成本核算 -->
                {{ $t("cusEntry.supplement20250211.costAccounting") }}: <span>{{ baseInfo.costUser }}</span></p>
            </el-col>
          </el-row>
        </el-collapse-item>
        <!-- 供应商评估 -->
        <el-collapse-item :title="$t('cusEntry.supplement20250211.supplierEvaluation')" name="2">
          <div style="min-height:300px;">
            <el-button
              type="primary"
              style="margin-bottom:16px;"
              @click="toReviewDetail"
            >
              <!-- 查看评审 -->
              {{ $t("cusEntry.supplement20250211.checkReview") }}
            </el-button>
            <BaseTable
              ref="table"
              style="height:300px;"
              :columns="columns"
              :data-source="dataSource"
              :initialize="false"
              row-key="vendorCode"
              border
              @asyncGetRealDataSource="asyncGetRealDataSource"
            >
              <template #isDisPoint="{ scope }">
                <DictSelect
                  v-model="scope.row.isDisPoint"
                  :lazy-init="lazyInit"
                  :disabled="baseInfo.topicStatus === 'FINISH' || scope.row.isNoQuoterVendor"
                  code="VENDER_ISPIONT"
                />
              </template>
            </BaseTable>
          </div>

          <!--<evalutation-charts v-if="currentRound" ref="charts" :round="currentRound" :inquiry-id="inquiryId" />-->
        </el-collapse-item>
      </el-collapse>
    </el-main>

    <CToolbar>
      <template slot="right">
        <el-button type="ghost" @click="back">
          {{ $t('common.close') }}
        </el-button>
        <el-button
          v-if="baseInfo.topicStatus !== 'FINISH'"
          type="primary"
          @click="save"
        >
          {{ $t('common.staging') }}
        </el-button>
      </template>
    </CToolbar>
  </el-container>
</template>

<script>
import CToolbar from 'lib@/components/c-toolbar'
import { tabTodoMixin } from '@/utils/mixins'
import BaseTable from 'lib@/components/BaseTable/baseTable'
import resize from '@/library/composition/origin/vendorQoutePriceLineChart/resize'
// import reviewManagementDetail from 'modb@/inquiryBySimpleBuyer/views/reviewManagement/detail'
// import evalutationCharts from 'modb@/inquiryBySimpleBuyer/views/inquiryBySimpleListBuyer/inquiryBySimpleListBuyer/inquiryTrackingDetail/inquiryEvaluationTab/components/evaluationCharts'

// import 'echarts/theme/macarons'
export default {
  name: 'SsMaterials',

  components: {
    CToolbar,
    BaseTable
    // evalutationCharts
  },

  mixins: [tabTodoMixin, resize],

  data () {
    return {
      currentRound: null,
      lazyInit: true,
      columns: [],
      realDataSource: [],
      dataSource: [],
      width: '100%',
      height: '370px',
      inquiryId: null,
      topicId: null,
      inqMemberList: [],
      baseInfo: {},
      activeDims: ['1', '2', '3', '4']

    }
  },

  computed: {
    projectCode () {
      let result = ''
      result = this.getString('projectCode')
      return result
    },
    productTime () {
      let result = ''
      result = this.getString('productTime')
      return result
    },
    demandQuantity () {
      let result = ''
      result = this.getString('demandQuantity')
      return result
    },
    purchaseList () {
      let result = ''
      result = this.getMember('PURCHASE')
      return result
    },
    projectList () {
      let result = ''
      result = this.getMember('PROJECT')
      return result
    },
    qualityList () {
      let result = ''
      result = this.getMember('QUALITY')
      return result
    }
  },

  async created () {
    const { row } = this.$attrs.params
    this.inquiryId = row.inquiryId
    this.topicId = row.topicId
    this.getInfo(this.inquiryId)
    this.columns = [
      {
        attrs: {
          prop: 'vendorCode',
          // '供应商代码'
          label: this.$t('cusEntry.supplement20250211.supplierCode')  
        }
      },
      {
        attrs: {
          prop: 'vendorName',
          width: '120',
          // '供应商名称'
          label: this.$t('common.companyName')
        }
      },
      {
        attrs: {
          prop: 'ssDevelopmentOpinion',
          // '研发意见'
          label: this.$t('cusEntry.supplement20250211.researchOpinion'),
          formatter: cellValue => this.$getDictLabel('REVIEW_COMMENTS', cellValue)
        }

      },
      {
        attrs: {
          prop: 'ssQualityOpinion',
          // '质量意见'
          label: this.$t('cusEntry.supplement20250211.qualityOpinion'),
          formatter: cellValue => this.$getDictLabel('REVIEW_COMMENTS', cellValue)
        }
      },
      {
        attrs: {
          prop: 'isThreadPartie',
          // '是否签署第三方协议'
          label: this.$t('cusEntry.supplement20250211.isSignedThirdPartyAgreement'),
          formatter: cellValue => this.$getDictLabel('YES_OR_NO', cellValue)
        }
      },
      {
        attrs: {
          prop: 'paymentDays',
          // '账期'
          label: this.$t('bidMod.paymentDays'),
          width: '160',
          formatter: cellValue => this.$getDictLabel('PAYMENT_PERIOD', cellValue)
        }
      },
      {
        attrs: {
          prop: 'spotRate',
          // '现汇比例'
          label: this.$t('cusEntry.supplement20250211.cashExchangeRate')
        }
      },
      {
        attrs: {
          prop: 'qualityAssurance',
          width: '120',
          // '质保'
          label: this.$t('cusEntry.supplement20250211.qualityAssurance')
        }
      },
      {
        attrs: {
          prop: 'toolingPaymentType',
          width: '160',
          // '工装模具费用付款方式及摊销情况'
          label: this.$t('cusEntry.supplement20250211.wageToolingCostPaymentMethodAndAmortization')
        }
      },
      {
        attrs: {
          prop: 'developmentPaymentType',
          width: '160',
          // '研发试验费用支付方式及摊销情况'
          label: this.$t('cusEntry.supplement20250211.researchAndDevelopmentExpensePaymentMethodAndAmortizationSituation')
        }
      },
      {
        attrs: {
          prop: 'isDisPoint',
          // '定点供应商'
          label: this.$t('cusEntry.supplement20250211.fixedPointSupplier'),
          width: '100',
          formatter: cellValue => this.$getDictLabel('VENDER_ISPIONT', cellValue)
        },
        // '必填'
        rules: { required: true, message: this.$t('vendorMod.required') },
        slot: 'isDisPoint'
      }
    ]

    this.lazyInit = false
  },

  methods: {
    toReviewDetail () {
      let reviewId = this.baseInfo.reviewId
      if (reviewId) {
        // this.$emit('tab-add', {
        //   component: reviewManagementDetail,
        //   name: 'reviewManagementDetail' + this.baseInfo.inquiryId,
        //   title: this.baseInfo.rfqName,
        //   params: {
        //     flag: 'view',
        //     row: { reviewId: reviewId },
        //     tabName: 'reviewManagementDetail' + this.baseInfo.inquiryId
        //   }
        // })
      } else {
        // '未发起评审'
        this.$message.warning(this.$t('cusEntry.supplement20250211.noInitiateReview'))
      }
    },

    save () {
      let list = this.realDataSource.map(item => {
        return {
          inquiryId: item.inquiryId,
          // topicId:item.topicId,
          vendorId: item.vendorId,
          // vendorCode:item.vendorCode,
          round: item.round,
          isDisPoint: item.isDisPoint
        }
      })

      this.$refs.table.validate(res => {
        if (res) {
          this.$http({
            url: '/api-inq/inquiry/header/saveSd',
            method: 'POST',
            data: list,
            loading: true
          }).then(res => {
            this.$message({
              message: res.message,
              type: 'success'
            })
          })
        } else {
          this.__focus_error__()
        }
      })
    },

    back () {
      this.$emit('tab-remove', this.$attrs['tabName'])
    },

    getLabel (type, cellValue) {
      let result = ''
      if (cellValue === 'Y') return this.$t('flowMod.agree')  // '同意'
      if (cellValue === 'N') return this.$t('cusEntry.supplement20250205.generalText34')  // '不同意'
      return result
    },

    asyncGetRealDataSource (data) {
      this.realDataSource = data
    },

    getMember (type) {
      let result = ''
      if (this.inqMemberList && this.inqMemberList.length) {
        result = this.inqMemberList.filter(item => item.bidUser === type).map(item => item.nickname)
      }
      return result.toString()
    },

    getString (type) {
      let result = ''
      if (this.baseInfo.projectInfo && this.baseInfo.projectInfo.length) {
        let projectInfo = this.baseInfo.projectInfo
        result = projectInfo.map(item => item[type])
      }
      return result.toString()
    },

    getInfo () {
      this.$http({
        url: '/api-inq/inquiry/header/getInqInfoBySdInq',
        method: 'GET',
        params: { inquiryId: this.inquiryId, topicId: this.topicId },
        loading: true
      }).then(res => {
        const { inqMemberList, inqVendorMeetList, ...rest } = res.data
        this.inqMemberList = inqMemberList || []
        let dataSource = inqVendorMeetList || []
        this.currentRound = inqVendorMeetList[0].round
        this.dataSource = dataSource.map(item => {
          return {
            ...item,
            __edit_key__: true
          }
        })
        this.baseInfo = rest
      })
    }

  }
}
</script>
