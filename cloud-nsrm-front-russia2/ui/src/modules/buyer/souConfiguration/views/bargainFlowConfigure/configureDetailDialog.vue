<template>
  <SrmDialog
    :title="$t('dataConfMod.projectInquiryProcess')"
    size="large"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <!--招标流程配置表单-->
    <el-form
      ref="configureForm"
      :model="configureFormData"
      :rules="configureFormRules"
      label-width="80px"
    >
      <SrmRow>
        <!--模板名称-->
        <SrmCol :init-col="4">
          <!-- 流程名称 -->
          <el-form-item :label="$t('themeConfig.processName')" prop="processConfigName">
            <el-input v-model.trim="configureFormData.processConfigName" maxlength="200" />
          </el-form-item>
        </SrmCol>

        <!--招标范围-->
        <SrmCol :init-col="4">
          <el-form-item :label="$t('bidMod.bidingScope')" prop="publishScope">
            <DictSelect
              v-model="configureFormData.publishScope"
              code="SOU_PUBLISH_SCOPE"
              @change="setFlowNode"
            />
          </el-form-item>
        </SrmCol>

        <!--招标类型-->
        <SrmCol :init-col="4">
          <el-form-item :label="$t('bidMod.bidingType')" prop="bargainType">
            <DictSelect
              v-model="configureFormData.bargainType"
              code="SOU_BRG_TYPE"
              @change="bargainTypeChange"
            />
          </el-form-item>
        </SrmCol>

        <!--评分规则-->
        <SrmCol :init-col="4">
          <el-form-item :label="$t('bidMod.evaluateMethod')" prop="scoreRuleType">
            <DictSelect
              ref="scoreRuleTypeDictSelect"
              v-model="configureFormData.scoreRuleType"
              :disabled="scoreRuleTypeDisabled"
              code="SOU_SCORE_RULE_TYPE"
              :transform-options="transformOptions"
              @change="setFlowNode"
            />
          </el-form-item>
        </SrmCol>
      </SrmRow>

      <SrmRow>
        <!--流程简述-->
        <SrmCol :init-col="1">
          <!-- 流程简述 -->
          <el-form-item :label="$t('themeConfig.processBrief')" prop="remark">
            <el-input
              v-model.trim="configureFormData.remark"
              type="textarea"
              :rows="2"
              maxlength="255"
              show-word-limit
            />
          </el-form-item>
        </SrmCol>
      </SrmRow>
    </el-form>

    <!--招标流程配置表格-->
    <el-table
      :data="flowTable"
      style="width: 100%"
      height="150"
    >
      <el-table-column
        align="center"
        type="index"
        width="30"
      />

      <!--模板名称-->
      <el-table-column
        align="center"
        :label="$t('bidMod.tempName')"
        show-overflow-tooltip
        :formatter="() => configureFormData.processConfigName"
      />

      <!--招标范围-->
      <el-table-column
        align="center"
        :label="$t('bidMod.bidingScope')"
        width="85"
        show-overflow-tooltip
        :formatter="() => $getDictLabel('SOU_PUBLISH_SCOPE', configureFormData.publishScope)"
      />

      <!--招标类型-->
      <el-table-column
        align="center"
        :label="$t('bidMod.bidingType')"
        width="85"
        show-overflow-tooltip
        :formatter="() => $getDictLabel('SOU_BRG_TYPE', configureFormData.bargainType)"
      />

      <!--评分规则-->
      <el-table-column
        align="center"
        :label="$t('bidMod.evaluateMethod')"
        width="85"
        show-overflow-tooltip
        :formatter="() => $getDictLabel('SOU_SCORE_RULE_TYPE', configureFormData.scoreRuleType)"
      />

      <!--招标立项-->
      <el-table-column align="center" :label="$t('bidMod.addNewProj')">
        <!--项目信息-->
        <el-table-column
          align="center"
          prop="projectInfo"
          :label="$t('bidMod.projectInformation')"
          width="85"
        >
          <template v-slot="scope">
            <el-checkbox
              v-model="scope.row.projectInfo"
              :disabled="flowNodeDisabled.projectInfo"
              true-label="Y"
              false-label="N"
            />
          </template>
        </el-table-column>

        <!--项目需求-->
        <el-table-column
          align="center"
          prop="requireInfo"
          :label="$t('bidMod.projectRequirement')"
          width="85"
        >
          <template v-slot="scope">
            <el-checkbox
              v-model="scope.row.requireInfo"
              :disabled="flowNodeDisabled.requireInfo"
              true-label="Y"
              false-label="N"
            />
          </template>
        </el-table-column>

        <!--邀请供应商-->
        <el-table-column
          align="center"
          prop="inviteVendor"
          :label="$t('bidMod.inviteSupplier')"
          width="85"
        >
          <template v-slot="scope">
            <el-checkbox
              v-model="scope.row.inviteVendor"
              :disabled="flowNodeDisabled.inviteVendor"
              true-label="Y"
              false-label="N"
            />
          </template>
        </el-table-column>

        <!--评分规则-->
        <el-table-column
          align="center"
          prop="scoreRule"
          :label="$t('bidMod.evaluateMethod')"
          width="85"
        >
          <template v-slot="scope">
            <el-checkbox
              v-model="scope.row.scoreRule"
              :disabled="flowNodeDisabled.scoreRule"
              true-label="Y"
              false-label="N"
            />
          </template>
        </el-table-column>

        <!--立项审批-->
        <el-table-column
          align="center"
          prop="createApproval"
          :label="$t('bidMod.lixiangWorkflow')"
          width="85"
        >
          <template v-slot="scope">
            <el-checkbox
              v-model="scope.row.createApproval"
              :disabled="flowNodeDisabled.createApproval"
              true-label="Y"
              false-label="N"
            />
          </template>
        </el-table-column>
      </el-table-column>

      <!--保证金管理-->
      <el-table-column
        align="center"
        prop="bondManagement"
        :label="$t('bidMod.bondManagement')"
        width="85"
      >
        <template v-slot="scope">
          <el-checkbox
            v-model="scope.row.bondManagement"
            :disabled="flowNodeDisabled.bondManagement"
            true-label="Y"
            false-label="N"
          />
        </template>
      </el-table-column>

      <!--报名管理-->
      <el-table-column
        align="center"
        prop="signUpManagement"
        :label="$t('bidMod.entryManagement')"
        width="85"
      >
        <template v-slot="scope">
          <el-checkbox
            v-model="scope.row.signUpManagement"
            :disabled="flowNodeDisabled.signUpManagement"
            true-label="Y"
            false-label="N"
          />
        </template>
      </el-table-column>

      <!--投标控制-->
      <el-table-column
        align="center"
        prop="bidingControl"
        :label="$t('bidMod.bidingControl')"
        width="85"
      >
        <template v-slot="scope">
          <el-checkbox
            v-model="scope.row.bidingControl"
            :disabled="flowNodeDisabled.bidingControl"
            true-label="Y"
            false-label="N"
          />
        </template>
      </el-table-column>

      <!--开评标-->
      <el-table-column align="center" :label="$t('bidMod.bidOpening')">
        <!--技术标管理-->
        <el-table-column
          align="center"
          prop="techManagement"
          :label="$t('bidMod.technicalManagement')"
          width="85"
        >
          <template v-slot="scope">
            <el-checkbox
              v-model="scope.row.techManagement"
              :disabled="flowNodeDisabled.techManagement"
              true-label="Y"
              false-label="N"
            />
          </template>
        </el-table-column>

        <!--商务标管理-->
        <el-table-column
          align="center"
          prop="businessManagement"
          :label="$t('bidMod.commercialManagement')"
          width="85"
        >
          <template v-slot="scope">
            <el-checkbox
              v-model="scope.row.businessManagement"
              :disabled="flowNodeDisabled.businessManagement"
              true-label="Y"
              false-label="N"
            />
          </template>
        </el-table-column>

        <!--评选-->
        <el-table-column
          align="center"
          prop="evaluation"
          :label="$t('bidMod.bidEvaluation')"
          width="85"
        >
          <template v-slot="scope">
            <el-checkbox
              v-model="scope.row.evaluation"
              :disabled="flowNodeDisabled.evaluation"
              true-label="Y"
              false-label="N"
            />
          </template>
        </el-table-column>
      </el-table-column>
    </el-table>

    <template #footer class="dialog-footer">
      <el-button @click="dialogVisible = false">
        {{ $t('common.cancel') }}
      </el-button>

      <el-button type="primary" @click="confirm">
        {{ $t('common.confirm') }}
      </el-button>
    </template>
  </SrmDialog>
</template>

<script>
import { brgBuyerHttp } from 'modb@/souConfiguration/api'
import { SOU_PUBLISH_SCOPE_ENUM, SOU_SCORE_RULE_TYPE_ENUM } from 'lib@/composition/origin/enum'
import { SOU_BRG_TYPE_ENUM } from 'lib@/composition/bargainLts/utils'

export default {
  name: 'SettingDialog',

  props: {
    visible: {
      type: Boolean,
      required: true
    },
    dialogType: {
      type: String,
      required: true
    },
    editRow: {
      type: [Array, Object],
      required: true
    }
  },

  data () {
    return {
      configureFormData: {
        processConfigName: '',
        publishScope: '',
        bargainType: '',
        scoreRuleType: '',
        remark: ''
      },
      configureFormRules: {
        processConfigName: [{ required: true, message: this.$t('common.pleaseInput') }],
        publishScope: [{ required: true, message: this.$t('common.pleaseSelect') }],
        bargainType: [{ required: true, message: this.$t('common.pleaseSelect') }]
      },
      flowTable: [],
      // 控制节点是否禁用
      flowNodeDisabled: {
        projectInfo: true,
        requireInfo: true,
        inviteVendor: true,
        scoreRule: true,
        createApproval: false,
        signUpManagement: false,
        bidingControl: true,
        techManagement: true,
        businessManagement: true,
        evaluation: true
      },
      scoreRuleTypeDisabled: false
    }
  },

  computed: {
    dialogVisible: {
      get: function () {
        return this.visible
      },
      set: function (val) {
        this.$emit('update:visible', val)
      }
    }
  },

  mounted () {
    this.initData()
  },

  methods: {
    // 初始化数据
    initData () {
      // 打开弹窗
      if (this.dialogType === 'add') {
        // 新增
        // 固化的节点为 [项目信息、项目需求、立项审批、投标控制、商务标管理、评选]
        this.flowTable = [
          {
            projectInfo: 'Y',
            requireInfo: 'Y',
            createApproval: 'N',
            bidingControl: 'Y',
            businessManagement: 'Y',
            evaluation: 'Y',
            inviteVendor: 'N',
            scoreRule: 'N',
            signUpManagement: 'N',
            techManagement: 'N'
          }
        ]
      } else {
        // 修改
        this.flowTable = [this.editRow]
        for (const i in this.configureFormData) {
          this.configureFormData[i] = this.editRow[i] || ''
        }
        if (this.configureFormData.bargainType === SOU_BRG_TYPE_ENUM.TECHNOLOGY_BUSINESS) {
          this.configureFormData.scoreRuleType = SOU_SCORE_RULE_TYPE_ENUM.COMPOSITE_PRICE
          this.scoreRuleTypeDisabled = true
        } else {
          this.scoreRuleTypeDisabled = false
        }
      }
    },

    /* 评分规则筛选 */
    transformOptions (options) {
      return options.map(item => {
        return {
          ...item,
          disabled: item.value === SOU_SCORE_RULE_TYPE_ENUM.COMPOSITE_PRICE && this.configureFormData.bargainType === SOU_BRG_TYPE_ENUM.BUSINESS
        }
      })
    },

    /* 招标类型改变 */
    bargainTypeChange (val) {
      if (val === SOU_BRG_TYPE_ENUM.TECHNOLOGY_BUSINESS) {
        this.configureFormData.scoreRuleType = SOU_SCORE_RULE_TYPE_ENUM.COMPOSITE_PRICE
        this.scoreRuleTypeDisabled = true
      } else {
        this.scoreRuleTypeDisabled = false
      }

      if (val === SOU_BRG_TYPE_ENUM.BUSINESS) {
        // 手动初始化字典列表
        this.$refs.scoreRuleTypeDictSelect.initOption('INIT')
        if (this.configureFormData.scoreRuleType === SOU_SCORE_RULE_TYPE_ENUM.COMPOSITE_PRICE) {
          // 选商务，不能选综合评分
          this.configureFormData.scoreRuleType = ''
        }
      }

      this.setFlowNode()
    },

    /* 设置流程，设置打勾 */
    setFlowNode () {
      // 招标范围
      if (this.configureFormData.publishScope === SOU_PUBLISH_SCOPE_ENUM.OPEN_TENDER) {
        // 公开招标 不可勾选邀请供应商
        this.flowTable[0].inviteVendor = 'N'
      } else if (this.configureFormData.publishScope === SOU_PUBLISH_SCOPE_ENUM.INVITE_TENDER) {
        // 邀请招标 勾选邀请供应商且不能取消
        this.flowTable[0].inviteVendor = 'Y'
      }

      // 招标类型
      if (this.configureFormData.bargainType === SOU_BRG_TYPE_ENUM.BUSINESS) {
        // 商务招标 取消技术标
        this.flowTable[0].techManagement = 'N'
      } else if (this.configureFormData.bargainType === SOU_BRG_TYPE_ENUM.TECHNOLOGY_BUSINESS) {
        // 技术 + 商务 勾选技术标
        this.flowTable[0].techManagement = 'Y'
      }

      // 评分规则
      if (this.configureFormData.scoreRuleType === SOU_SCORE_RULE_TYPE_ENUM.COMPOSITE_PRICE) {
        // 综合评分法 勾上评分规则
        this.flowTable[0].scoreRule = 'Y'
      } else {
        // 非综合评分法 不勾
        this.flowTable[0].scoreRule = 'N'
      }

      this.flowTable.splice(0, 1, this.flowTable[0])
    },

    /* 确定 */
    async confirm () {
      // 校验
      const valid = await this.$refs.configureForm.validate().catch(() => { /* noting */ })
      if (!valid) {
        this.__focus_error__()
        return
      }

      const params = Object.assign({}, this.flowTable[0], this.configureFormData)

      const response = await brgBuyerHttp.process.editProcessConfig(params)
      if (response) {
        this.$message.success(this.$t('common.successSave'))
        this.$emit('success')
        this.dialogVisible = false
      }
    }
  }
}
</script>
