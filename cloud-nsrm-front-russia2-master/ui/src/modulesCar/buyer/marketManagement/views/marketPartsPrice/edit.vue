<template>
  <el-container class="flex-container wrapper" direction="vertical">
    <el-main>
      <el-collapse v-model="activeDims">
        <el-form ref="formRef" :rules="rules" :model="form" :disabled="disabledFlag">
          <!--基础信息-->
          <el-collapse-item :title="$t('meeting.baseInfo')" name="1">
            <srm-row>
              <srm-col>
                <!-- 年份 -->
                <el-form-item prop="year" :label="$t('marketBudget.budgetYear')">
                  <el-date-picker v-model="form.year" type="year" value-format="yyyy" />
                </el-form-item>
              </srm-col>
              <srm-col>
                <!-- 市况类型 -->
                <el-form-item prop="marketType" :label="$t('marketBudget.marketType')">
                  <DictSelect v-model="form.marketType" clearable code="COST_LINK_MARKET_TYPE" />
                </el-form-item>
              </srm-col>
              <srm-col>
                <!-- 应用场景 -->
                <el-form-item prop="sceneType" :label="$t('marketBudget.usageScene')">
                  <DictSelect v-model="form.sceneType" clearable code="COST_LINK_SCENE_TYPE" />
                </el-form-item>
              </srm-col>
              <!-- 库存组织 -->
              <srm-col>
                <el-form-item
                  :label="$t('common.invOrg')"
                  prop="orgInvId"
                >
                  <OrganizationSelector
                    ref="orgSelector"
                    v-model="form.orgInvId"
                    :placeholder="$t('common.pleaseSelect')"
                    :parent-id="-1"
                    node-type="INV"
                    @select="invSelectHandler"
                  />
                </el-form-item>
              </srm-col>
              <!-- 供应商 -->
              <srm-col>
                <el-form-item
                  prop="vendorId"
                  :label="$t('contractMod.vendorName')"
                >
                  <QuickSearch
                    :show-input="form.vendorName"
                    show-key="companyName"
                    :scope-data="form"
                    name="scc_sup_company_info"
                    @close-quicksearch="getVendorObj"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <!-- 币种 -->
                <el-form-item prop="currencyCode" :label="$t('vendorMod.currencyCode')">
                  <DictSelect v-model="form.currencyCode" clearable code="currency" />
                </el-form-item>
              </srm-col>
              <srm-col>
                <!-- 生效时间 -->
                <el-form-item prop="effectiveTime" :label="$t('marketBudget.effectiveTime')">
                  <el-date-picker
                    v-model="form.effectiveTime"
                    type="datetime"
                    value-format="yyyy-MM-dd HH:mm:ss"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <!-- 截止时间 -->
                <el-form-item prop="expiredTime" :label="$t('marketBudget.expiredTime')">
                  <el-date-picker
                    v-model="form.expiredTime"
                    type="datetime"
                    value-format="yyyy-MM-dd HH:mm:ss"
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-collapse-item>
          <el-collapse-item v-for="(item,i) in form.confList" :key="i" :title="item.priceMode=='STANDARD'?'基准价行情设置':'预估价行情设置'" :name="String(i+2)">
            <srm-row>
              <srm-col v-if="item.priceMode=='STANDARD'">
                <el-form-item label="行情选择" :prop="`confList.${i}.priceSelection`" :rules="rules.priceSelection">
                  <DictSelect v-model="item.priceSelection" clearable code="COST_LINK_PRICE_SELECTION" />
                </el-form-item>
              </srm-col>
              <template v-if="(item.priceMode === 'STANDARD' && item.priceSelection === 'STANDARD_PRICE') || item.priceMode === 'ESTIMATE'">
                <srm-col>
                  <!-- 行情开始时间 -->
                  <el-form-item
                    :label="$t('marketBudget.marketStartTime')"
                    :prop="`confList.${i}.effectiveTime`"
                    :rules="rules.effectiveTime"
                  >
                    <el-date-picker
                      v-model="item.effectiveTime"
                      type="datetime"
                      default-time="00:00:00"
                      value-format="yyyy-MM-dd HH:mm:ss"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 行情结束时间 -->
                  <el-form-item
                    :label="$t('marketBudget.marketEndTime')"
                    :prop="`confList.${i}.expiredTime`"
                    :rules="rules.expiredTime"
                  >
                    <el-date-picker
                      v-model="item.expiredTime"
                      type="datetime"
                      default-time="23:59:59"
                      value-format="yyyy-MM-dd HH:mm:ss"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 计算类型 -->
                  <el-form-item
                    :label="$t('marketBudget.computeType')"
                    :prop="`confList.${i}.computeType`"
                    :rules="rules.computeType"
                  >
                    <DictSelect
                      v-model="item.computeType"
                      clearable
                      code="COST_LINK_PRICE_COMPUTE_TYPE"
                    />
                  </el-form-item>
                </srm-col>
              </template>
              <srm-col v-if="(item.priceMode === 'STANDARD' && item.priceSelection === 'POINT_PRICE')">
                <el-form-item
                  label="原材料定点时的行情基准价"
                  :prop="`confList.${i}.notaxStandardBasePrice`"
                  :rules="rules.notaxStandardBasePrice"
                >
                  <el-input v-model="item.notaxStandardBasePrice" />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-collapse-item>
        </el-form>
      </el-collapse>
    </el-main>

    <CToolbar>
      <template slot="right">
        <!-- 计算 -->
        <!-- <el-button type="primary" @click="countResult"> 计算 </el-button> -->
        <!--返回-->
        <el-button type="ghost" @click="back">
          {{ $t('common.backTo') }}
        </el-button>
        <el-button v-if="editFlag" type="ghost" @click="save">
          {{ $t('common.save') }}
        </el-button>
        <el-button v-if="editFlag" type="primary" @click="submit">
          {{ $t('common.submit') }}
        </el-button>
      </template>
    </CToolbar>
  </el-container>
</template>

<script>
import { tabTodoMixin } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import OrganizationSelector from 'lib@/components/organization-selector'
import QuickSearch from 'lib@/components/QuickSearch'
import { transformMQL } from '@/library/utils/util'

export default {
  name: 'MarketPartsPriceDetail',

  components: {
    CToolbar,
    OrganizationSelector,
    QuickSearch
  },

  mixins: [tabTodoMixin],

  data () {
    return {
      activeDims: ['1', '2', '3'],
      form: {
        linkPriceId: null, // ID
        linkPriceNo: null,		// 联动行情编号 COST_LINK_PRICE_NO
        linkPriceName: null,		// 联动行情名称
        year: null,				// 年度
        marketType: null,			// 市况类型 COST_LINK_MARKET_TYPE
        sceneType: null,			// 应用场景 COST_LINK_SCENE_TYPE
        orgInvId: null,			// 库存组织ID
        orgInvCode: null,			// 库存组织编码
        orgInvName: null,			// 库存组织名称
        vendorId: null,			// 供应商ID
        vendorCode: null,			// 供应商编码
        vendorName: null,			// 供应商名称
        currencyCode: null,		// 币种编码
        executeStatus: null,		// 执行状态 COST_LINK_PRICE_EXECUTE_STATUS
        initApprovalStatus: null, // 立项审批状态 COST_LINK_PRICE_INIT_APPROVAL_STATUS
        executeProgress: null, // 执行进度百分比
        effectiveTime: null,
        expiredTime: null,
        confList: [
          {
            linkPriceConfId: null, // ID
            linkPriceId: null,			// 关联联动行情任务ID
            priceMode: 'STANDARD', // 价格方式 STANDARD 基准价
            priceSelection: null,			// 行情类型 POINT_PRICE 定点价 STANDARD_PRICE 基准价
            notaxStandardBasePrice: null, // 定点基准未税价
            effectiveTime: null,			// 行情开始时间
            expiredTime: null,			// 行情结束时间
            computeType: null			// 计算类型 COST_LINK_PRICE_COMPUTE_TYPE
          },
          {
            linkPriceConfId: null, // ID
            linkPriceId: null,			// 关联联动行情任务ID
            priceMode: 'ESTIMATE',				// 价格方式 ESTIMATE 预估价
            priceSelection: null,			// 行情类型 POINT_PRICE 定点价 STANDARD_PRICE 基准价
            notaxStandardBasePrice: null, // 定点基准未税价
            effectiveTime: null,			// 行情开始时间
            expiredTime: null,			// 行情结束时间
            computeType: null			// 计算类型 COST_LINK_PRICE_COMPUTE_TYPE
          }
        ]
      },
      rules: {
        year: [{ required: true, message: this.$t('common.pleaseSelect') }],
        marketType: [{ required: true, message: this.$t('common.pleaseSelect') }],
        sceneType: [{ required: true, message: this.$t('common.pleaseSelect') }],
        orgInvId: [{ required: true, message: this.$t('common.pleaseSelect') }],
        vendorId: [{ required: true, message: this.$t('common.pleaseSelect') }],
        currencyCode: [{ required: true, message: this.$t('common.pleaseSelect') }],
        priceSelection: [{ required: true, message: this.$t('common.pleaseSelect') }],
        effectiveTime: [{ required: true, message: this.$t('common.pleaseSelect') }],
        expiredTime: [{ required: true, message: this.$t('common.pleaseSelect') }],
        computeType: [{ required: true, message: this.$t('common.pleaseSelect') }],
        notaxStandardBasePrice: [{ required: true, message: this.$t('common.pleaseInput') }]

      }
    }
  },

  computed: {
    urlParams () {
      return this.$attrs.params || {}
    },
    disabledFlag () {
      return this.urlParams.flag === 'view'
    },
    editFlag () {
      return ['add', 'edit'].includes(this.urlParams.flag)
    }
  },

  created () {
    let { row, flag } = this.urlParams
    if (row && row.linkPriceId) {
      this.getFormDetail(row.linkPriceId)
    }
  },

  methods: {
    getFormDetail (linkPriceId) {
      let payload = [
        { linkPriceId: linkPriceId }
      ]
      let formData = transformMQL.save('CostLinkPrice', payload, 'getLinkPrice')
      this.$http({
        url: '/api-cost/api-ql/CostLinkPrice/getLinkPrice',
        method: 'POST',
        data: formData,
        loading: true
      })
        .then((res) => {
          if (res.data?.records) {
            this.form = res.data.records[0] || {}
          }
        })
        .catch((err) => {
          console.log(err)
        })
    },
    getVendorObj (val, scope) {
      scope.vendorId = val ? val.companyId : null
      scope.vendorCode = val ? val.companyCode : ''
      scope.vendorName = val ? val.companyName : ''
    },
    invSelectHandler (node, value, scope) {
      this.form.orgInvId = node ? node.organizationId : null
      this.form.orgInvCode = node ? node.organizationCode : null
      this.form.orgInvName = node ? node.organizationName : null
    },
    async save () {
      this.submitData(true)
    },
    async submit () {
      let timeCheck = this.form.confList.some(item => {
        return item.expiredTime && (new Date(item.expiredTime).getTime() < new Date(item.effectiveTime).getTime())
      })
      if (timeCheck) return this.$message.warning('行情结束时间需大于行情开始时间')
      const bol = await this.$refs.formRef.validate().catch(e => {
        console.log(e)
      })
      if (!bol) {
        this.__focus_error__()
        return
      }

      this.submitData(false)
    },
    submitData (tempSave) {
      let params = JSON.parse(JSON.stringify(this.form))
      params.tempSave = tempSave
      let formData = transformMQL.save('CostLinkPrice', [params], 'editLinkPrice')
      this.$http({
        url: '/api-cost/api-ql/CostLinkPrice/editLinkPrice',
        method: 'POST',
        data: formData,
        loading: true
      }).then(() => {
        this.$attrs.params.row?.getQueryData?.()
        this.back()
      })
    },
    back () {
      this.$emit('tab-remove', this.$attrs['active-tab'])
    },
    isFlag (start, end) {
      return new Date(start).getTime() > new Date(end).getTime()
    }
  }
}
</script>

<style lang="scss" scoped>
.mt-10 {
  margin-top: 10px;
}
.wrapper {
  padding-bottom: 40px;
  ::v-deep .blue .el-input__inner {
    color: #409eff;
  }
}
</style>
