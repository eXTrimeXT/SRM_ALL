<template>
  <SrmDialog
    title="竞价流程配置"
    size="large"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <!--竞价流程配置表单-->
    <el-form
      ref="configureForm"
      :model="configureFormData"
      :rules="configureFormRules"
      label-width="80px"
    >
      <SrmRow>
        <!--模板名称-->
        <SrmCol :init-col="4">
          <el-form-item label="模板名称" prop="processConfigName">
            <el-input v-model.trim="configureFormData.processConfigName" maxlength="200" />
          </el-form-item>
        </SrmCol>

        <!--竞价范围-->
        <SrmCol :init-col="4">
          <el-form-item label="竞价范围" prop="publishScope">
            <DictSelect
              v-model="configureFormData.publishScope"
              code="SOU_PUBLISH_SCOPE"
              @change="setFlowNode"
            />
          </el-form-item>
        </SrmCol>

        <!--评分规则-->
        <SrmCol :init-col="4">
          <el-form-item :label="$t('bidMod.evaluateMethod')" prop="scoreRuleType">
            <DictSelect
              ref="scoreRuleTypeDictSelect"
              v-model="configureFormData.scoreRuleType"
              code="SOU_AUCT_SCORE_RULE_TYPE"
              @change="setFlowNode"
            />
          </el-form-item>
        </SrmCol>
      </SrmRow>

      <SrmRow>
        <!--模板简述-->
        <SrmCol :init-col="1">
          <el-form-item label="模板简述" prop="remark">
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

    <BaseTable
      ref="tableRef"
      stripe
      :data="flowTable"
      :columns="tableHeader"
      :empty-text="$t('components.noData')"
      border
      height="150px"
      style="width: 100%"
    >
      <!-- 项目信息 -->
      <template #projectInfo="{ column, row }">
        <el-checkbox
          v-model="row[column.property]"
          true-label="Y"
          false-label="N"
          disabled
        />
      </template>
      <!-- 项目需求 -->
      <template #requireInfo="{ column, row }">
        <el-checkbox
          v-model="row[column.property]"
          true-label="Y"
          false-label="N"
          disabled
        />
      </template>
      <!-- 邀请供应商 -->
      <template #inviteVendor="{ column, row }">
        <el-checkbox
          v-model="row[column.property]"
          true-label="Y"
          false-label="N"
          disabled
        />
      </template>
      <!-- 流程审批 -->
      <template #createApproval="{ column, row }">
        <el-checkbox
          v-model="row[column.property]"
          true-label="Y"
          false-label="N"
        />
      </template>
      <!-- 保证金管理 -->
      <template #bondManagement="{ column, row }">
        <el-checkbox
          v-model="row[column.property]"
          true-label="Y"
          false-label="N"
        />
      </template>
      <!-- 报名管理 -->
      <template #signUpManagement="{ column, row }">
        <el-checkbox
          v-model="row[column.property]"
          true-label="Y"
          false-label="N"
        />
      </template>
      <!-- 商务标管理 -->
      <template #businessManagement="{ column, row }">
        <el-checkbox
          v-model="row[column.property]"
          true-label="Y"
          false-label="N"
          disabled
        />
      </template>
      <!-- 竞价大厅 -->
      <template #auctHall="{ column, row }">
        <el-checkbox
          v-model="row[column.property]"
          true-label="Y"
          false-label="N"
          disabled
        />
      </template>
      <!-- 评选 -->
      <template #evaluation="{ column, row }">
        <el-checkbox
          v-model="row[column.property]"
          true-label="Y"
          false-label="N"
          disabled
        />
      </template>
    </BaseTable>

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
import { comBuyerHttp } from 'modb@/souConfiguration/api'
import { SOU_PUBLISH_SCOPE_ENUM, SOU_SCORE_RULE_TYPE_ENUM } from 'lib@/composition/origin/enum'
import { SOU_BRG_TYPE_ENUM } from 'lib@/composition/bargainLts/utils'
import BaseTable from 'lib@/components/BaseTable'
import { transformMQL } from 'lib@/utils/util'

export default {
  name: 'SettingDialog',
  components: {
    BaseTable
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
      configureFormData: {
        processConfigName: '',
        publishScope: '',
        scoreRuleType: '',
        remark: ''
      },
      configureFormRules: {
        processConfigName: [{ required: true, message: this.$t('common.pleaseInput') }],
        publishScope: [{ required: true, message: this.$t('common.pleaseSelect') }],
        scoreRuleType: [{ required: true, message: this.$t('common.pleaseSelect') }]
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
      scoreRuleTypeDisabled: false,
      tableHeader: [
        // 模板名称
        {
          attrs: {
            type: 'index',
            label: '序号',
            width: '60'
          }
        },
        {
          attrs: {
            align: 'center',
            minWidth: '120',
            label: '模板名称',
            prop: 'processConfigName',
            formatter: () => this.configureFormData.processConfigName
          }
        },
        // 竞价范围
        {
          attrs: {
            align: 'center',
            minWidth: '120',
            label: '竞价范围',
            prop: 'publishScope',
            formatter: () => this.$getDictLabel('SOU_PUBLISH_SCOPE', this.configureFormData.publishScope)
          }
        },
        // 项目信息
        {
          attrs: {
            align: 'center',
            minWidth: '120',
            label: () => this.$t('bidMod.projectInformation'),
            prop: 'projectInfo'
          },
          slot: 'projectInfo'
        },
        // 项目需求
        {
          attrs: {
            align: 'center',
            minWidth: '120',
            label: () => this.$t('bidMod.projectRequirement'),
            prop: 'requireInfo'
          },
          slot: 'requireInfo'
        },
        // 邀请供应商
        {
          attrs: {
            align: 'center',
            minWidth: '120',
            label: () => this.$t('bidMod.inviteSupplier'),
            prop: 'inviteVendor'
          },
          slot: 'inviteVendor'
        },
        // 评分规则
        {
          attrs: {
            align: 'center',
            minWidth: '120',
            label: '评分规则',
            prop: 'scoreRuleType',
            formatter: () => this.$getDictLabel('SOU_AUCT_SCORE_RULE_TYPE', this.configureFormData.scoreRuleType)
          }
        },
        // 流程审批
        // {
        //   attrs: {
        //     align: 'center',
        //     minWidth: '120',
        //     label: '流程审批',
        //     prop: 'createApproval'
        //   },
        //   slot: 'createApproval'
        // },
        // 报名管理
        {
          attrs: {
            align: 'center',
            minWidth: '120',
            label: () => this.$t('bidMod.entryManagement'),
            prop: 'signUpManagement'
          },
          slot: 'signUpManagement'
        },
        // 保证金管理
        {
          attrs: {
            align: 'center',
            minWidth: '120',
            label: '保证金管理',
            prop: 'bondManagement'
          },
          slot: 'bondManagement'
        },
        // 商务标管理
        {
          attrs: {
            align: 'center',
            minWidth: '120',
            label: () => this.$t('bidMod.commercialManagement'),
            prop: 'businessManagement'
          },
          slot: 'businessManagement'
        },
        // 竞价大厅
        {
          attrs: {
            align: 'center',
            minWidth: '120',
            label: () => this.$t('bidMod.hall'),
            prop: 'auctHall'
          },
          slot: 'auctHall'
        },
        // 评选
        {
          attrs: {
            align: 'center',
            minWidth: '120',
            label: () => this.$t('bidMod.bidEvaluation'),
            prop: 'evaluation'
          },
          slot: 'evaluation'
        }
      ]
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
        // 固化的节点为 [项目信息、项目需求、邀请供应商、竞价大厅、评选]
        this.flowTable = [
          {
            projectInfo: 'Y',
            requireInfo: 'Y',
            inviteVendor: 'Y',
            businessManagement: 'Y',
            auctHall: 'Y',
            evaluation: 'Y'
          }
        ]
      } else {
        // 修改
        this.flowTable = [this.editRow]
        for (const i in this.configureFormData) {
          this.configureFormData[i] = this.editRow[i] || ''
        }
      }
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
      const { auctHall, bondManagement } = params
      console.log(params, 'params')

      let transformParams = transformMQL.save('AuctSouProjectForBuyer', [{ ...params, auctProcessConfig: { auctHall, bondManagement }, tempSave: true }], 'editProcessConfig')
      const response = await comBuyerHttp.process.editProcessConfig(transformParams)
      if (response) {
        this.$message.success(this.$t('common.successSave'))
        this.$emit('success')
        this.dialogVisible = false
      }
    }
  }
}
</script>
