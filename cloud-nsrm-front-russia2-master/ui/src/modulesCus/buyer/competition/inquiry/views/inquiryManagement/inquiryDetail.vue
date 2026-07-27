<template>
  <el-container class="flex-container the-inquiryDetail-detail" direction="vertical">
    <el-main class="inquiry-detail-border-card">
      <el-tabs
        ref="editableTabs"
        v-model="editableTabsValue"
        type="border-card"
        style="padding-bottom: 44px;"
      >
        <!--询价信息-->
        <el-tab-pane
          name="inquiryInfoTab"
          :label="$t('bidMod.inquiryInfo')"
          :disabled="!readonly"
        >
          <InquiryDetailInfo
            ref="inquiryDetailInfo"
            :header.sync="allParams.header"
            :inner-files.sync="allParams.innerFiles"
            :outer-files.sync="allParams.outerFiles"
            :currency-list="allParams.currencyList"
            :readonly="readonly"
            :page-flag="pageFlag"
          />
        </el-tab-pane>

        <!--需求信息-->
        <el-tab-pane
          name="requireInfoTab"
          :label="$t('bidMod.requireInfo')"
          :disabled="!readonly"
          lazy
        >
          <InquiryDetailRequireInfoTab
            ref="inquiryDetailRequireInfoTab"
            :is-current-active-tab="editableTabsValue === 'requireInfoTab'"
            :header.sync="allParams.header"
            :items.sync="allParams.items"
            :form-project-id="projectId"
            :readonly="readonly"
            @refresh="getFormDetail"
          />
        </el-tab-pane>

        <!-- 邀请供应商 -->
        <el-tab-pane
          name="inviteVendorTab"
          :label="$t('bidMod.inviteVendor')"
          :disabled="!readonly"
          lazy
        >
          <InquiryDetailVendor
            ref="inquiryDetailVendor"
            :vendors.sync="allParams.vendors"
            :items="allParams.items"
            :header="allParams.header"
            :form-inquiry-id="projectId"
            :readonly="readonly"
          />
        </el-tab-pane>
      </el-tabs>

      <!--底部按钮 审批页面不显示-->
      <CToolbar v-if="editableTabsValue !== 'approvlaFlowingTab'">
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
              @click="stepTabSwitch(-1)"
            >
              {{ $t("bidMod.prevOne") }}
            </el-button>

            <!--下一步-->
            <el-button
              v-show="editableTabsValue !== 'approvlaFlowingTab'"
              type="primary"
              @click="saveTempInqAndNextTab('next')"
            >
              {{ editableTabsValue === 'inviteVendorTab' ? $t("common.submit") : $t("bidMod.nextOne") }}
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
import { inqBuyerHttp } from 'modcb@/inquiry/api'
import { tabTodoMixin } from '@/utils/mixins'
import { WORKFLOW_MODEL_ID } from 'lib@/composition/inquiry/utils'
import { SOU_PUBLISH_SCOPE_ENUM, SOU_ORDER_TYPE_ENUM } from 'lib@/composition/origin/enum'
import CToolbar from 'lib@/components/c-toolbar/index.vue'
import InquiryDetailScoreRule from './inquiryDetail/inquiryDetailScoreRule.vue'
import InquiryDetailRequireInfoTab from './inquiryDetail/inquiryDetailRequireInfoTab.vue'
import InquiryDetailVendor from './inquiryDetail/inquiryDetailVendor.vue'
import InquiryDetailInfo from './inquiryDetail/inquiryDetailInfo.vue'
import OrionWorkflowTab from 'lib@/composition/origin/workflowTab/index.vue'

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
      projectId: this.$attrs.params.flag === 'add' ? '' : this.$attrs.params.row.projectId,
      readonly: !!this.$attrs.params.readOnly,
      editableTabsValue: 'inquiryInfoTab',
      allParams: {
        header: {
          departmentName: '',
          extVendorPerformanceRank: '',
          extIsRandom: '',
          projectId: '',
          souNo: '',
          souName: '',
          organizationId: '',
          scoreRuleType: '',
          scoreTemplateId: '',
          orderWay: '',
          inquiryType: '',
          createdBy: '',
          createdUserName: '',
          orderEndTime: '',
          creationDate: '',
          orderStartTime: '',
          extProjectStatus: 'DRAFT',
          remark: '',
          // 报价币种
          standardCurrency: 'CNY',
          exchangeRateType: '',
          currencyExchangeDate: '',
          pricePrecision: 2,
          // 联系方式
          linkman: '',
          tel: '',
          email: '',
          // 默认邀请招标
          publishScope: SOU_PUBLISH_SCOPE_ENUM.INVITE_TENDER,
          // 默认普通报价
          orderType: SOU_ORDER_TYPE_ENUM.SIMPLE,
          quoteTempId: null,
          quoteTempName: null,
          // 报价控制
          allowWithdraw: 'Y',
          allowPartPrice: 'N',
          needEncryptPrice: 'Y',
          allowProxyOrder: 'N',
          excludeBlackVendors: 'Y',
          excludeNoCurrentOrgVendors: 'Y',
          excludeOrgQuitVendors: 'Y',
          excludeOrgCategoryStatus: ''
        },
        currencyList: [],
        innerFiles: [],
        outerFiles: [],
        items: [],
        vendors: []
      },
      // 绑定的流程模板
      workflowModelId: WORKFLOW_MODEL_ID
    }
  },

  async created () {
    if (this.pageFlag !== 'add') {
      await this.getFormDetail(this.projectId)
    }
  },
  mounted () {
    /* 监听供应商添加 */
    this.$bus.$on('saveInquirySuppliers', async () => {
      await this.saveTempInqAndNextTab('save')
      await this.getFormDetail(this.projectId)
    })
  },
  beforeDestory () {
    /* 解绑监听器 */
    this.$bus.$off('saveInquirySuppliers')
  },
  methods: {
    /* 获取详情 */
    getFormDetail (id) {
      return new Promise(async resolve => {
        const response = await inqBuyerHttp.init.getInqInfo(id)
        if (response && response.data) {
          const {
            projectInfo = {},
            requireInfo = [],
            vendorInfo = []
          } = response.data

          const {
            souFileList = [],
            currencyList = [],
            ...project
          } = projectInfo

          this.allParams = {
            header: {
              ...project,
              // 默认普通报价
              orderType: project.orderType || SOU_ORDER_TYPE_ENUM.SIMPLE,
              quoteTempId: project.quoteTempId ?? null,
              quoteTempName: project.quoteTempName ?? null
            },
            innerFiles: souFileList.filter(item => item.fileType === 'INNER'),
            outerFiles: souFileList.filter(item => item.fileType === 'OUTER'),
            // 过滤掉外币列表中的本币
            currencyList: (currencyList || []).filter(item => item.currencyCode !== project.currency),
            items: JSON.parse(JSON.stringify(requireInfo)),
            vendors: JSON.parse(JSON.stringify(vendorInfo))
          }
          this.$nextTick(() => {
            this.$refs.inquiryDetailInfo.clearFormValidate()
          })
        }
        resolve()
      })
    },

    /* 步骤切换 */
    stepTabSwitch (step) {
      // 找到tab页列表
      const panes = this.$refs.editableTabs.$data.panes.map(item => item.name)
      // 当前位置
      const index = panes.findIndex(item => item === this.editableTabsValue)
      this.editableTabsValue = panes[index + step]
    },

    /* 编排询价提交数据 */
    getFormatAllParams ({ isTempSave }) {
      const allParamsObj = JSON.parse(JSON.stringify(this.allParams))
      let { header, items = [], vendors = [] } = allParamsObj

      const allParamsOfItems = items.map(item => {
        // 删除冗余参数
        delete item.formulaValueList
        return item
      })

      const {
        currencyList = [],
        innerFiles = [],
        outerFiles = []
      } = this.$refs.inquiryDetailInfo.getParamsData()

      header = {
        ...header,
        // 冗余写死参数
        souType: 'INQ',
        sourceFromType: 'HAND_MAKE'
      }

      return {
        ...(this.projectId ? { projectId: this.projectId, souNo: header.souNo } : {}),
        projectInfo: {
          project: {
            ...header
          },
          currencyList: currencyList,
          outerFileList: outerFiles,
          innerFileList: innerFiles
        },
        requireInfo: {
          orderType: header.orderType,
          itemList: allParamsOfItems
        },
        vendorInfo: { vendorList: vendors },
        scoreInfo: {
          scoreRuleType: header.scoreRuleType,
          scoreTemplateId: header.scoreTemplateId
        },
        // 加入当前步骤信息
        createStep: this.getCreateStep(),
        isTempSave
      }
    },

    /* 根据新的物料信息更新报价权限 */
    updateQuoteAuthFromItems () {
      // 邀请招标，并且存在供应商，根据新的物料，更新报价权限
      if (this.allParams.header.publishScope === SOU_PUBLISH_SCOPE_ENUM.INVITE_TENDER && this.allParams.vendors.length > 0) {
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
                requireQuantity: itemItem.requireQuantity,
                quoteForbid,
                vendorId: item.vendorId,
                souItemId: itemItem.souItemId || ''
              }
            })
          }
        })
      }
    },

    /* 下一步 / 保存 */
    async saveTempInqAndNextTab (type) {
      const map = new Map([
        // 询价信息
        ['inquiryInfoTab', () => this.saveTempInquiryInfoTab(type)],
        // 需求信息
        ['requireInfoTab', () => this.saveTempRequireInfoTab(type)],
        // 邀请供应商
        ['inviteVendorTab', () => this.saveTempInviteVendorTab(type)],
        // 评分规则
        ['evalRuleTab', () => this.saveTempEvalRuleTab(type)]
      ])

      await map.get(this.editableTabsValue)()
    },

    /* 下一步/保存 START */
    // 询价信息
    async saveTempInquiryInfoTab (type) {
      const validateResult = await this.$refs.inquiryDetailInfo.validateForm()
      if (validateResult) {
        // 校验通过，保存，进行下一步
        const { data } = await this.saveOrSubmitInq({ isTempSave: true })
        if (data) {
          this.projectId = data
          this.allParams.header.projectId = data
          await this.getFormDetail(data)
          // 切换标签页需要放最后，否则getFormDetail未完成会导致整个逻辑流程错误
          if (type === 'next') {
            this.stepTabSwitch(1)
          }
        }
      }
    },
    // 需求信息
    async saveTempRequireInfoTab (type) {
      const result = await this.$refs.inquiryDetailRequireInfoTab.validateForm()
      // 校验不通过
      if (!result) {
        return
      }

      // 邀请招标，并且存在供应商，根据新的物料，更新报价权限
      // this.updateQuoteAuthFromItems()

      await this.$nextTick()
      try {
        const responseData = await this.saveOrSubmitInq({ isTempSave: true })
        if (responseData && responseData.data) {
          this.projectId = responseData.data
          await this.getFormDetail(responseData.data)
          if (type === 'next') {
            this.stepTabSwitch(1)
          }
        }
      } catch (e) {
        console.error(e)
      }
    },
    // 邀请供应商
    async saveTempInviteVendorTab (type) {
      // if (this.allParams.header.publishScope === SOU_PUBLISH_SCOPE_ENUM.OPEN_TENDER) {
      //   // 公开招标，不用保存和校验直接跳过
      //   this.stepTabSwitch(1)
      //   return
      // }
      const result = await this.$refs.inquiryDetailVendor.validateForm()
      if (result) {
        const params = type === 'save' ? { isTempSave: true } : { isTempSave: false, createStep: 'inviteVendor' }
        // 保存，进行下一步
        const responseData = await this.saveOrSubmitInq(params)
        if (responseData) {
          if (type === 'next') {
            await inqBuyerHttp.init.renderEngine({
              businessType: 'EXT_SOUINQCREATE',
              businessId: this.projectId
            })
            this.backTo('refresh')
          }
        }
      }
    },
    // 评分规则
    async saveTempEvalRuleTab (type) {
      /* 评分规则 */
      const result = await this.$refs.inquiryDetailScoreRule.validateForm()
      if (!result) {
        return
      }

      if (type === 'next') {
        // 调提交接口
        const responseData = await this.saveOrSubmitInq({ isTempSave: false })
        await this.getFormDetail(this.projectId)

        if (responseData) {
          this.stepTabSwitch(1)
        }
      } else {
        await this.saveOrSubmitInq({ isTempSave: true })
      }
    },
    /* 下一步/保存 END */

    /* 返回后端步骤 */
    getCreateStep () {
      const map = new Map([
        // 询价信息
        ['inquiryInfoTab', 'projectInfo'],
        // 需求信息
        ['requireInfoTab', 'requireInfo'],
        // 邀请供应商
        ['inviteVendorTab', 'inviteVendor'],
        // 评分规则
        ['evalRuleTab', 'scoreRule']
      ])
      return map.get(this.editableTabsValue)
    },

    /* 保存 / 提交 询价信息 */
    saveOrSubmitInq (params) {
      let payload = this.getFormatAllParams(params)

      return new Promise(async resolve => {
        const response = await inqBuyerHttp.init.editInq(payload)
        if (response) {
          this.$message.success(this.$t('common.successSave'))
          resolve(response)
        } else {
          resolve(null)
        }
      })
    },

    /* 审批流关闭的情况下，提交成功 */
    workflowSuccess () {
      this.backTo('refresh')
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
