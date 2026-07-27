<template>
  <el-container class="flex-container the-inquiryDetail-detail" direction="vertical">
    <el-main class="inquiry-detail-border-card">
      <el-tabs v-model="editableTabsValue" type="border-card" style="padding-bottom: 44px;">
        <!--询价信息-->
        <el-tab-pane :label="$t('bidMod.inquiryInfo')" name="inquiryInfoTab" :disabled="!readonly">
          <InquiryDetailInfo
            ref="inquiryDetailInfo"
            :header.sync="allParams.header"
            :inner-files.sync="allParams.innerFiles"
            :outer-files.sync="allParams.outerFiles"
            :picker-options="pickerOptions"
            :currency-list="allParams.currencyList"
            :read-only="readonly"
            :page-flag="pageFlag"
          />
        </el-tab-pane>

        <!--需求信息-->
        <el-tab-pane
          :label="$t('bidMod.requireInfo')"
          name="requireInfoTab"
          :disabled="!readonly"
          lazy
        >
          <InquiryDetailRequireInfoTab
            ref="inquiryDetailRequireInfoTab"
            :is-current-active-tab="editableTabsValue === 'requireInfoTab'"
            :header.sync="allParams.header"
            :items.sync="allParams.items"
            :form-inquiry-id="inquiryId"
            :picker-options="pickerOptions"
            :readonly="readonly"
            @getFormDetail="getFormDetail"
          />
        </el-tab-pane>

        <!-- 邀请供应商 -->
        <el-tab-pane
          :label="$t('bidMod.inviteVendor')"
          name="inviteVendorTab"
          :disabled="!readonly"
          lazy
        >
          <InquiryDetailVendor
            ref="inquiryDetailVendor"
            :vendors.sync="allParams.vendors"
            :items="allParams.items"
            :header="allParams.header"
            :form-inquiry-id="inquiryId"
            :read-only="readonly"
          />
        </el-tab-pane>

        <!--评分规则-->
        <el-tab-pane :label="$t('bidMod.scoreRule')" name="evalRuleTab" :disabled="!readonly" lazy>
          <InquiryDetailScoreRule
            ref="inquiryDetailScoreRule"
            :is-current-active-tab="editableTabsValue === 'evalRuleTab'"
            :header.sync="allParams.header"
            :rule-items.sync="allParams.ruleItems"
            :score-rule-name.sync="allParams.scoreRuleName"
            :read-only="readonly"
          />
        </el-tab-pane>

        <!--审批流程-->
        <el-tab-pane
          :label="$t('bidMod.approvlaFlowing')"
          name="approvlaFlowingTab"
          :disabled="!readonly"
          lazy
        >
          <OrionWorkflowTab
            :scope-id="inquiryId"
            :is-current-active-tab="editableTabsValue === 'approvlaFlowingTab'"
            :params="{ activeWorkflowTab: true }"
            scope-prepare-status
            :workflow-model-id="workflowModelId"
            :workflow-enable="workflowEnable"
          />
        </el-tab-pane>
      </el-tabs>

      <!--底部按钮 审批页面不显示-->
      <CToolbar v-if="pageFlag !== 'approve' && editableTabsValue !== 'approvlaFlowingTab'">
        <template slot="right">
          <template v-if="!readonly">
            <!--保存-->
            <el-button type="primary" @click="saveTempInqAndNextTab('save')">
              {{ $t("common.save") }}
            </el-button>

            <!--上一步-->
            <el-button
              v-show="editableTabsValue !== 'inquiryInfoTab'"
              type="primary"
              @click="prevOne"
            >
              {{ $t("bidMod.prevOne") }}
            </el-button>

            <!--下一步-->
            <el-button
              v-show="editableTabsValue !== 'approvlaFlowingTab' && !showFlowButton"
              type="primary"
              @click="saveTempInqAndNextTab('next')"
            >
              {{ $t("bidMod.nextOne") }}
            </el-button>

            <!--提交审批 审批流关闭-->
            <el-button v-if="showFlowButton" type="primary" @click="saveBillTab">
              {{ $t("bidMod.submitapprovlaFlowing") }}
            </el-button>
          </template>

          <!--返回-->
          <el-button @click="backTo">
            {{ $t("bidMod.backTo") }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>

<script>
import { tabTodoMixin } from '@/utils/mixins'
import { getFlowByIdFromListPage } from 'lib@/composition/origin/composition'
import { QUOTE_TYPE_MAGIC } from '@/library/composition/inquiryBySimple/utils'
import CToolbar from 'lib@/components/c-toolbar'
import InquiryDetailScoreRule from './inquiryDetail/inquiryDetailScoreRule.vue'
import InquiryDetailRequireInfoTab from './inquiryDetail/inquiryDetailRequireInfoTab'
import InquiryDetailVendor from './inquiryDetail/inquiryDetailVendor'
import InquiryDetailInfo from './inquiryDetail/inquiryDetailInfo'
import OrionWorkflowTab from 'lib@/composition/origin/workflowTab'

export default {
  name: 'InquiryDetail',

  components: {
    CToolbar,
    InquiryDetailScoreRule,
    InquiryDetailRequireInfoTab,
    InquiryDetailVendor,
    InquiryDetailInfo,
    OrionWorkflowTab
  },

  mixins: [tabTodoMixin],

  data () {
    return {
      pageFlag: this.$attrs.params.flag,
      inquiryId: this.$attrs.params.flag === 'add' ? '' : this.$attrs.params.row.inquiryId,
      readonly: !!this.$attrs.params.readOnly,
      pickerOptions: {
        disabledDate (time) {
          const today = new Date()
          today.setHours(0)
          today.setMinutes(0)
          today.setSeconds(0)
          today.setMilliseconds(0)
          return time.getTime() < today.getTime()
        }
      },
      editableTabsValue: 'inquiryInfoTab',
      allParams: {
        header: {
          inquiryId: '',
          inquiryNo: '',
          inquiryTitle: '',
          organizationId: '',
          inquiryRule: '',
          quoteRule: '',
          inquiryType: '',
          createdBy: '',
          createdUserName: '',
          deadline: '',
          creationDate: '',
          beginQuote: '',
          status: '',
          remark: '',
          // 默认人民币
          currency: 'CNY',
          priceNum: 2,
          linkman: '',
          tel: '',
          email: '',
          priceConfirm: 'N',
          exchangeRateType: '',
          currencyExchangeDate: '',
          // 默认邀请招标
          publishScope: 'INVITE_TENDER',
          // 默认普通报价
          quoteType: QUOTE_TYPE_MAGIC.NORMAL,
          // 报价控制
          allowWithdrawBiding: 'N',
          allowPartBiding: 'N',
          // 密封报价
          needEncryptPrice: 'N',
          excludeBlackVendors: 'Y',
          excludeNoCurrentOrgVendors: 'N',
          excludeOrgQuitVendors: 'N',
          excludeOrgCategoryStatus: ''
        },
        scoreRuleName: '',
        currencyList: [],
        innerFiles: [],
        outerFiles: [],
        items: [], // 需求行数据tab
        vendors: [], // 邀请供应商tab
        ruleItems: [], // 评分规则tab
        quoteAuth: []
      },
      // 审批流开关
      workflowEnable: undefined,
      // 绑定的流程模板
      workflowModelId: 'INQUIRYCREATE'
    }
  },

  computed: {
    // 是否显示提交审批按钮
    showFlowButton () {
      // 非只读 && 已关闭审批流 && 在评分规则页
      return !this.readonly && !this.workflowEnable && this.editableTabsValue === 'evalRuleTab'
    }
  },

  async created () {
    if (this.pageFlag !== 'add') {
      await this.getFormDetail(this.inquiryId)
    }
    // 查询审批流开关
    this.workflowEnable = await getFlowByIdFromListPage(this.workflowModelId)
  },

  methods: {
    /* 获取详情 */
    getFormDetail (inquiryId) {
      return new Promise(async resolve => {
        const data = await this.$api.inq.getInqInfoById(inquiryId)
        if (data && data.data) {
          const responseData = data.data
          // 过滤掉外币列表中的本币
          const currencyList = (data.data.currencyList || []).filter(item => item.currencyCode !== responseData.header.currency)
          this.allParams = {
            header: responseData.header || {},
            innerFiles: responseData.innerFiles || [],
            outerFiles: responseData.outerFiles || [],
            currencyList: currencyList,
            items: responseData.items || [], // 需求行数据tab
            vendors: responseData.vendors || [], // 邀请供应商tab
            scoreRule: responseData.scoreRule || {}, // 评分规则tab
            ruleItems: responseData.ruleItems || [], // 评分规则tab
            quoteAuth: responseData.quoteAuth || [], // 报价权限
            scoreRuleName: responseData.scoreRuleName || ''
          }
          this.$nextTick(() => {
            this.$refs.inquiryDetailInfo.clearFormValidate()
          })
          if (!this.allParams.header.quoteType) {
            // 其他来源的单据，没有报价类型，给默认普通报价
            this.allParams.header.quoteType = QUOTE_TYPE_MAGIC.NORMAL
          }
          if (
            responseData.quoteAuth &&
            responseData.quoteAuth.length !== 0 &&
            this.editableTabsValue === 'inviteVendorTab'
          ) {
            for (const i of this.allParams.vendors) {
              i.quoteAuthArr = responseData.quoteAuth.filter(v => v.vendorId === i.vendorId)
            }
          }
        }
        resolve()
      })
    },

    /* 上一步 */
    prevOne () {
      const curtabName = this.editableTabsValue
      switch (curtabName) {
        case 'requireInfoTab':
          this.editableTabsValue = 'inquiryInfoTab'
          break
        case 'inviteVendorTab':
          this.editableTabsValue = 'requireInfoTab'
          break
        case 'evalRuleTab':
          this.editableTabsValue = 'inviteVendorTab'
          break
        case 'approvlaFlowingTab':
          this.editableTabsValue = 'evalRuleTab'
          break
      }
    },

    /* 编排询价提交数据 */
    getFormatAllParams () {
      // 解决浅拷贝问题
      const allParamsObj = JSON.parse(JSON.stringify(this.allParams))
      const { header, items = [], vendors = [] } = allParamsObj

      const allParamsOfItems = items.map(item => {
        // 删除冗余参数
        delete item.formulaValueList
        return item
      })

      return {
        ...this.allParams,
        items: allParamsOfItems,
        vendors,
        header,
        currencyList: this.$refs.inquiryDetailInfo.getParamsData().currencyList
      }
    },

    /* 根据新的物料信息更新报价权限 */
    updateQuoteAuthFromItems () {
      // 邀请招标，并且存在供应商，根据新的物料，更新报价权限 inquiryItemId
      if (this.allParams.header.publishScope === 'INVITE_TENDER' && this.allParams.vendors.length > 0) {
        this.allParams.vendors = this.allParams.vendors.map(item => {
          return {
            ...item,
            quoteAuthList: this.allParams.items.map(itemItem => {
              // 该供应商存在报价权限信息，遍历新的物料列表
              // 当物料存在该供应商的报价权限信息里，拿已存在的quoteForbid，如果不存在，默认'N'
              let quoteForbid = 'N'
              if (item.quoteAuthList && Array.isArray(item.quoteAuthList) && item.quoteAuthList.length > 0) {
                const authItem = item.quoteAuthList.find(quoteItem => quoteItem.itemCode === itemItem.itemCode)
                if (authItem) {
                  quoteForbid = authItem.quoteForbid || 'N'
                }
              }

              return {
                noCodeItem: itemItem.noCodeItem,
                itemCode: itemItem.itemCode,
                itemDesc: itemItem.itemDesc,
                categoryName: itemItem.categoryName,
                unit: itemItem.unit,
                demandQuantity: itemItem.demandQuantity,
                quoteForbid,
                vendorId: item.vendorId,
                inquiryItemId: itemItem.inquiryItemId || ''
              }
            })
          }
        })
      }
    },

    /* 下一步 / 保存 */
    async saveTempInqAndNextTab (type) {
      if (this.editableTabsValue === 'inquiryInfoTab') {
        /* 询价信息 */
        const validateResult = await this.$refs.inquiryDetailInfo.validateForm()
        if (validateResult) {
          // 校验通过，保存，进行下一步
          const responseData = await this.saveTempInq()
          if (responseData && responseData.data) {
            this.inquiryId = responseData.data
            this.allParams.header.inquiryId = responseData.data
            await this.getFormDetail(responseData.data)
            // 切换标签页需要放最后，否则getFormDetail未完成会导致整个逻辑流程错误
            if (type === 'next') {
              this.editableTabsValue = 'requireInfoTab'
            }
          }
        } else {
          return false
        }
      } else if (this.editableTabsValue === 'requireInfoTab') {
        /* 需求信息 */
        const result = await this.$refs.inquiryDetailRequireInfoTab.validateForm()
        // 校验不通过
        if (!result) {
          return
        }

        // 邀请招标，并且存在供应商，根据新的物料，更新报价权限 inquiryItemId
        this.updateQuoteAuthFromItems()

        await this.$nextTick()

        try {
          const responseData = await this.saveTempInq()
          if (responseData && responseData.data) {
            this.inquiryId = responseData.data
            await this.getFormDetail(responseData.data)
            if (type === 'next') {
              this.editableTabsValue = 'inviteVendorTab'
            }
          }
        } catch (e) {
          console.error(e)
        }
      } else if (this.editableTabsValue === 'inviteVendorTab') {
        /* 邀请供应商 */
        if (this.allParams.header.publishScope === 'OPEN_TENDER') {
          // 公开招标，不用保存和校验直接跳过
          this.editableTabsValue = 'evalRuleTab'
          return
        }
        const result = await this.$refs.inquiryDetailVendor.validateForm()
        if (result) {
          this.allParams.quoteAuth = []
          for (const i of this.allParams.vendors) {
            this.allParams.quoteAuth = this.allParams.quoteAuth.concat(
              i.quoteAuthArr || []
            )
          }
          // 保存，进行下一步
          const responseData = await this.saveTempInq()
          if (responseData) {
            await this.getFormDetail(responseData.data)
            if (type === 'next') {
              this.editableTabsValue = 'evalRuleTab'
            }
          }
        }
      } else if (this.editableTabsValue === 'evalRuleTab') {
        /* 评分规则 */
        const result = await this.$refs.inquiryDetailScoreRule.validateForm()
        if (!result) return
        const responseData = await this.saveTempInq()
        if (responseData) {
          if (type === 'next') {
            this.editableTabsValue = 'approvlaFlowingTab'
          }
        }
      }
    },

    /* 返回后端步骤 */
    getCreateStep () {
      let step = ''
      switch (this.editableTabsValue) {
        case 'inquiryInfoTab':
          // 询价信息
          step = 'INQUIRY_INFO'
          break
        case 'requireInfoTab':
          // 需求信息
          step = 'REQUIRE_INFO'
          break
        case 'inviteVendorTab':
          // 邀请供应商
          step = 'INVITE_SUPPLIER'
          break
        case 'evalRuleTab':
          // 评分规则
          step = 'SCORE_RULE'
          break
        case 'quotaInfo':
          // 报价明细
          step = 'INQUIRY_INFO'
          break
        default:
          step = 'INQUIRY_INFO'
      }
      return step
    },

    /* 保存询价信息 */
    saveTempInq () {
      let payload = this.getFormatAllParams()
      // 加入当前步骤信息
      payload.header.createStep = this.getCreateStep()

      return new Promise(resolve => {
        // this.$api.inq.inquiryBySimple.inquiryTempSave(payload).then(data => {
        this.$http({
          url: '/api-inq/inquiry/header/tempSaveInq',
          method: 'POST',
          data: payload,
          loading: true
        }).then(data => {
          this.$message.success(this.$t('common.successSave'))
          resolve(data)
        }).catch(() => {
          resolve(null)
        })
      })
    },

    /* 没有审批流程的提交 */
    saveBillTab () {
      // 二次确认
      this.$confirm('请确认是否要提交询价单？', {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        // this.$api.inq.inquiryBySimple.inquirySave(this.getFormatAllParams()).then(() => {
        this.$http({
          url: '/api-inq/inquiry/header/submitInq',
          method: 'POST',
          data: this.getFormatAllParams(),
          loading: true
        }).then(() => {
          this.$message.success(this.$t('common.successSubmit'))
          this.backTo('refresh')
        })
      })
    },

    /* 返回 */
    backTo (type) {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      if (type === 'refresh') {
        // 需要刷新
        this.__setTabTodo('InquiryList.getQueryData')
      }
    }
  }
}
</script>

<style scoped lang="scss">
:deep(.the-inquiryDetail-detail) {
  .inquiry-detail-border-card > .el-tabs--border-card {
    margin-right: 10px
  }
  .sub_header {
    padding: 4px 11px;
    background: #eee;
    span {
      padding-right: 11px;
    }
  }
  .el-table .el-date-editor {
    width: 135px;
  }
  .main-header {
    border: none;
  }
}
</style>
