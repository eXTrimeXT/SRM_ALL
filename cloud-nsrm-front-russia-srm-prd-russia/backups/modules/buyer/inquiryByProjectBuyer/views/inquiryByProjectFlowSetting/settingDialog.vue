<template>
  <srm-dialog
    :title="$t('dataConfMod.projectInquiryProcess')"
    size="large"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <div>
      <!--招标流程配置表单-->
      <el-form
        ref="form"
        :model="form"
        label-width="80px"
      >
        <srm-row>
          <!--模板名称-->
          <srm-col :init-col="4">
            <el-form-item
              :label="$t('bidMod.tempName')"
              prop="processConfigName"
            >
              <el-input
                v-model.trim="form.processConfigName"
                maxlength="200"
                show-word-limit
              />
            </el-form-item>
          </srm-col>
          <!--招标范围-->
          <srm-col :init-col="4">
            <el-form-item
              :label="$t('bidMod.bidingScope')"
              prop="bargainScope"
            >
              <dict-select
                v-model="form.bargainScope"
                code="BARGAIN_SCOPE"
                @change="getBargainScope"
              />
            </el-form-item>
          </srm-col>
          <!--招标类型-->
          <srm-col :init-col="4">
            <el-form-item
              :label="$t('bidMod.bidingType')"
              prop="bargainType"
            >
              <dict-select
                v-model="form.bargainType"
                code="BARGAIN_TYPE"
                @change="getBargainType"
              />
            </el-form-item>
          </srm-col>
          <!--评分规则-->
          <srm-col :init-col="4">
            <el-form-item
              v-if="form.bargainType !== 'Logistics'"
              :label="$t('bidMod.evaluateMethod')"
              prop="evaluateMethod"
            >
              <dict-select
                ref="evaluateMethodDictSelect"
                v-model="form.evaluateMethod"
                :disabled="evaluateMethodDisabled"
                code="BRG_EVALUATE_METHOD"
                :transform-options="transformOptions"
                @change="getEvaluateMethod"
              />
            </el-form-item>
          </srm-col>
        </srm-row>

        <srm-row>
          <!--模板简述-->
          <srm-col :init-col="1">
            <el-form-item
              :label="$t('bidMod.tempDesc')"
              prop="comments"
            >
              <el-input
                v-model.trim="form.comments"
                type="textarea"
                :rows="2"
                maxlength="800"
                show-word-limit
              />
            </el-form-item>
          </srm-col>
        </srm-row>
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
        <!--大小节点-->
        <el-table-column
          align="center"
          :label="$t('bidMod.bigNode')"
        >
          <el-table-column
            align="center"
            prop="processConfigName"
            :label="$t('bidMod.smallNode')"
            width="100"
            :show-overflow-tooltip="true"
          />
        </el-table-column>

        <!--招标范围-->
        <el-table-column
          align="center"
          :label="$t('bidMod.bidingScope')"
        >
          <el-table-column
            align="center"
            :label="$t('bidMod.bidingScope')"
            width="85"
            show-overflow-tooltip
            :formatter="() => $getDictLabel('BARGAIN_SCOPE', form.bargainScope)"
          />
        </el-table-column>

        <!--招标类型-->
        <el-table-column
          align="center"
          :label="$t('bidMod.bidingType')"
        >
          <el-table-column
            align="center"
            :label="$t('bidMod.bidingType')"
            width="85"
            show-overflow-tooltip
            :formatter="() => $getDictLabel('BARGAIN_TYPE', form.bargainType)"
          />
        </el-table-column>

        <!--评分规则-->
        <el-table-column
          align="center"
          :label="$t('bidMod.evaluateMethod')"
        >
          <el-table-column
            align="center"
            :label="$t('bidMod.evaluateMethod')"
            width="85"
            show-overflow-tooltip
            :formatter="() => $getDictLabel('BRG_EVALUATE_METHOD', form.evaluateMethod)"
          />
        </el-table-column>

        <!--招标立项-->
        <el-table-column
          align="center"
          :label="$t('bidMod.addNewProj')"
        >
          <!--项目信息-->
          <el-table-column
            align="center"
            prop="projectInformation"
            :label="$t('bidMod.projectInformation')"
            width="85"
          >
            <template v-slot="scope">
              <el-checkbox
                v-model="scope.row.projectInformation"
                :disabled="flowNodeDisabled.projectInformation"
                true-label="Y"
                false-label="N"
              />
            </template>
          </el-table-column>

          <!--项目需求-->
          <el-table-column
            align="center"
            prop="projectRequirement"
            :label="$t('bidMod.projectRequirement')"
            width="85"
          >
            <template v-slot="scope">
              <el-checkbox
                v-model="scope.row.projectRequirement"
                :disabled="flowNodeDisabled.projectRequirement"
                true-label="Y"
                false-label="N"
              />
            </template>
          </el-table-column>

          <!--邀请供应商-->
          <el-table-column
            align="center"
            prop="inviteSupplier"
            :label="$t('bidMod.inviteSupplier')"
            width="85"
          >
            <template v-slot="scope">
              <el-checkbox
                v-model="scope.row.inviteSupplier"
                :disabled="flowNodeDisabled.inviteSupplier"
                true-label="Y"
                false-label="N"
              />
            </template>
          </el-table-column>

          <!--评分规则-->
          <el-table-column
            align="center"
            prop="scoringRule"
            :label="$t('bidMod.evaluateMethod')"
            width="85"
          >
            <template v-slot="scope">
              <el-checkbox
                v-model="scope.row.scoringRule"
                :disabled="flowNodeDisabled.scoringRule"
                true-label="Y"
                false-label="N"
              />
            </template>
          </el-table-column>

          <!--流程审批-->
          <el-table-column
            align="center"
            prop="processApproval"
            :label="$t('bidMod.processApproval')"
            width="85"
          >
            <template v-slot="scope">
              <el-checkbox
                v-model="scope.row.processApproval"
                :disabled="flowNodeDisabled.processApproval"
                true-label="Y"
                false-label="N"
              />
            </template>
          </el-table-column>
        </el-table-column>

        <!--保证金管理-->
        <el-table-column
          align="center"
          label="保证金管理"
        >
          <el-table-column
            align="center"
            prop="bondManagement"
            label="保证金管理"
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
        </el-table-column>

        <!--报名管理-->
        <el-table-column
          align="center"
          :label="$t('bidMod.entryManagement')"
        >
          <el-table-column
            align="center"
            prop="entryManagement"
            :label="$t('bidMod.entryManagement')"
            width="85"
          >
            <template v-slot="scope">
              <el-checkbox
                v-model="scope.row.entryManagement"
                :disabled="flowNodeDisabled.entryManagement"
                true-label="Y"
                false-label="N"
              />
            </template>
          </el-table-column>
        </el-table-column>

        <!--投标控制-->
        <el-table-column
          align="center"
          :label="$t('bidMod.bidingControl')"
        >
          <el-table-column
            align="center"
            prop="bargainControl"
            :label="$t('bidMod.bidingControl')"
            width="85"
          >
            <template v-slot="scope">
              <el-checkbox
                v-model="scope.row.bargainControl"
                :disabled="flowNodeDisabled.bargainControl"
                true-label="Y"
                false-label="N"
              />
            </template>
          </el-table-column>
        </el-table-column>

        <!--开评标-->
        <el-table-column
          align="center"
          :label="$t('bidMod.bidOpening')"
        >
          <!--技术标管理-->
          <el-table-column
            align="center"
            prop="technicalManagement"
            :label="$t('bidMod.technicalManagement')"
            width="85"
          >
            <template v-slot="scope">
              <el-checkbox
                v-model="scope.row.technicalManagement"
                :disabled="flowNodeDisabled.technicalManagement"
                true-label="Y"
                false-label="N"
              />
            </template>
          </el-table-column>

          <!--商务标管理-->
          <el-table-column
            align="center"
            prop="commercialManagement"
            :label="$t('bidMod.commercialManagement')"
            width="85"
          >
            <template v-slot="scope">
              <el-checkbox
                v-model="scope.row.commercialManagement"
                :disabled="flowNodeDisabled.commercialManagement"
                true-label="Y"
                false-label="N"
              />
            </template>
          </el-table-column>
          <!--评选-->
          <el-table-column
            align="center"
            prop="brgEvaluation"
            :label="$t('bidMod.bidEvaluation')"
            width="85"
          >
            <template v-slot="scope">
              <el-checkbox
                v-model="scope.row.brgEvaluation"
                :disabled="flowNodeDisabled.brgEvaluation"
                true-label="Y"
                false-label="N"
              />
            </template>
          </el-table-column>
        </el-table-column>
      </el-table>
    </div>

    <template
      #footer
      class="dialog-footer"
    >
      <el-button
        @click="closeDialog"
      >
        {{ $t('common.cancel') }}
      </el-button>
      <el-button
        type="primary"
        @click="confirmAddNew"
      >
        {{ $t('common.confirm') }}
      </el-button>
    </template>
  </srm-dialog>
</template>

<script>
export default {
  name: 'SettingDialog',

  model: {
    prop: 'visible',
    event: 'changeVisible'
  },

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
      form: {
        processConfigName: '',
        bargainScope: '',
        bargainType: '',
        evaluateMethod: '',
        comments: ''
      },
      flowTable: [],
      // 控制节点是否禁用
      flowNodeDisabled: {
        projectInformation: true,
        projectRequirement: true,
        inviteSupplier: true,
        scoringRule: true,
        processApproval: false,
        supplierPerformance: false,
        entryManagement: false,
        bargainControl: true,
        technicalManagement: true,
        commercialManagement: true,
        brgEvaluation: true
      },
      evaluateMethodDisabled: false
    }
  },

  computed: {
    dialogVisible: {
      get: function () {
        return this.visible
      },
      set: function (val) {
        this.$emit('changeVisible', val)
      }
    }
  },

  watch: {
    visible: {
      handler (val) {
        if (val) {
          // 打开弹窗
          if (this.dialogType === 'add') {
            // 新增
            // 固化的节点为 [项目信息、项目需求、项目发布、投标控制、商务标管理、评选]
            this.flowTable = [
              {
                technologyExchange: 'N',
                projectInformation: 'Y',
                projectRequirement: 'Y',
                inviteSupplier: 'N',
                scoringRule: 'N',
                processApproval: 'N',
                supplierPerformance: 'N',
                targetPrice: 'N',
                entryManagement: 'N',
                questionClarification: 'N',
                bargainControl: 'Y',
                technicalManagement: 'N',
                commercialManagement: 'Y',
                brgEvaluation: 'Y',
                projectReport: 'N',
                projectApproval: 'N',
                bargainResult: 'N'
              }
            ]
            for (const i in this.form) {
              this.form[i] = ''
            }
          } else {
            // 修改
            this.flowTable = [this.editRow]
            for (const i in this.form) {
              this.form[i] = this.editRow[i]
            }
            if (this.form.bargainType === 'TECHNOLOGY_BUSINESS') {
              this.form.evaluateMethod = 'COMPOSITE_SCORE'
              this.evaluateMethodDisabled = true
            } else {
              this.evaluateMethodDisabled = false
            }
          }
        }
      },
      immediate: true
    }
  },

  methods: {
    /* 评分规则筛选 */
    transformOptions (options) {
      return options.map(item => {
        return {
          ...item,
          disabled: item.value === 'COMPOSITE_SCORE' && this.form.bargainType === 'BUSINESS'
        }
      })
    },

    getBargainScope () {
      this.setFlowNode()
    },

    getBargainType (val) {
      if (val === 'TECHNOLOGY_BUSINESS') {
        this.form.evaluateMethod = 'COMPOSITE_SCORE'
        this.evaluateMethodDisabled = true
      } else {
        this.evaluateMethodDisabled = false
      }

      if (val === 'BUSINESS') {
        // 手动初始化字典列表
        this.$refs.evaluateMethodDictSelect.initOption('INIT')
        if (this.form.evaluateMethod === 'COMPOSITE_SCORE') {
          // 选商务，不能选综合评分
          this.form.evaluateMethod = ''
        }
      }

      this.setFlowNode()
    },

    getEvaluateMethod () {
      this.setFlowNode()
    },

    /* 设置流程，设置打勾 */
    setFlowNode () {
      if (this.form.bargainType === 'Logistics') {
        // 招标类型---物流招标 已经没有这个选项，后续可以去掉
        return
      }

      // 招标范围
      if (this.form.bargainScope === 'OPEN_TENDER') {
        // 公开招标
        // 不可勾选邀请供应商
        this.flowTable[0].inviteSupplier = 'N'
      } else if (this.form.bargainScope === 'INVITE_TENDER') {
        // 邀请招标
        // 勾选邀请供应商且不能取消
        this.flowTable[0].inviteSupplier = 'Y'
      }

      // 招标类型
      if (this.form.bargainType === 'BUSINESS') {
        // 商务招标
        // 取消技术标
        this.flowTable[0].technicalManagement = 'N'
      } else if (this.form.bargainType === 'TECHNOLOGY_BUSINESS') {
        // 技术 + 商务
        // 勾选技术标
        this.flowTable[0].technicalManagement = 'Y'
      }

      // 评分规则
      if (this.form.evaluateMethod === 'COMPOSITE_SCORE') {
        // 综合评分法 勾上评分规则
        this.flowTable[0].scoringRule = 'Y'
      } else {
        // 非综合评分法 不勾
        this.flowTable[0].scoringRule = 'N'
      }
      // 强制更新表格
      this.flowTable.splice(0, 1, this.flowTable[0])
    },

    closeDialog () {
      this.$emit('changeVisible', false)
    },

    confirmAddNew () {
      if (!this.form.processConfigName) {
        this.$message.info(this.$t('bidMod.msgInputTemp')) // 请先输入模板名称!
        return
      }
      if (!this.form.bargainScope) {
        this.$message.info(this.$t('bidMod.msgSelBidScope')) // 请先选择招标范围!
        return
      }
      if (!this.form.bargainType) {
        this.$message.info(this.$t('bidMod.msgSelBidType')) // 请先选择招标类型!
        return
      }
      const params = Object.assign({}, this.flowTable[0], this.form)

      this.$api.brg.inquiryByProject.processConfigSave(params).then(() => {
        this.$message({
          message: this.$t('common.successSave'),
          type: 'success'
        })
        this.reset()
        this.$emit('getQueryData')
        this.closeDialog()
      })
    },

    reset () {
      for (const i in this.form) {
        this.form[i] = ''
      }
      this.flowTable = []
    }
  }
}
</script>
