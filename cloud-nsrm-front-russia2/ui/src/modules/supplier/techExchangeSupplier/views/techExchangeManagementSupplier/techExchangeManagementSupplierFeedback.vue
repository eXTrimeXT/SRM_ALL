<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-main>
      <!-- 交流详情 -->
      <h2>{{ $t('cusEntry.supplement20250211.communicationDetails') }}</h2>

      <!--报价截止倒计时-->
      <div class="cur-quote-deadline">
        <DynamicCutoffTime
          :label="$t('bidMod.beforeFeedbackDeadline')"
          :deadline-time="techExchangeData.technicalExchangeEndTime"
        />
      </div>

      <SrmRow
        :gutter="32"
        class="detail-info-wrap"
      >
        <SrmCol :init-col="4">
          <span>{{ $t('techExchange.technicalExchangeFormCode') }}: </span>{{ techExchangeData.technicalExchangeFormCode }}
        </SrmCol>
        <SrmCol :init-col="4">
          <span>{{ $t('bidMod.communicateTitle') }}: </span>{{ techExchangeData.technicalExchangeTitle }}
        </SrmCol>
        <SrmCol :init-col="4">
          <span>{{ $t('components.organization.ORG') }}: </span>{{ techExchangeData.orgOuName }}
        </SrmCol>
        <SrmCol :init-col="4">
          <span>{{ $t('bidMod.communicateType') }}: </span>{{ $getDictLabel('TECHNICAL_EXCHANGE_TYPE', techExchangeData.technicalExchangeType) }}
        </SrmCol>
      </SrmRow>

      <el-collapse
        v-model="activeDims"
        class="tab-form-style"
      >
        <!--方案附件-->
        <el-collapse-item
          :title="$t('bidMod.planAttachment')"
          name="1"
        >
          <FileDynamic
            ref="sceneAttachment"
            v-model="tecExcFilesSupplier"
            scene-module-code="SCENE_TECHNICAL_EXCHANGE_FEEDBACK_ATTACHMENT"
            :business-id="technicalExchangeFeedbackId"
            editable
            :need-init="false"
          />
        </el-collapse-item>

        <!--物料信息-->
        <el-collapse-item
          :title="$t('bidMod.itemInfo')"
          name="2"
        >
          <RequirementsInfo
            :info-data="tecExcMaterialItems"
            readonly
          />
        </el-collapse-item>

        <!--采购方技术要求附件-->
        <el-collapse-item
          :title="$t('bidMod.purchaserTechRequire')"
          name="3"
        >
          <FileDynamic
            ref="tecExcFilesFileDynamic"
            v-model="tecExcFiles"
            scene-module-code="SCENE_TECHNICAL_EXCHANGE_ATTACHMENT"
            :business-id="technicalExchangeId"
            :editable="false"
            :need-init="false"
          />
        </el-collapse-item>
      </el-collapse>

      <CToolbar>
        <template slot="right">
          <!--保存-->
          <el-button
            type="primary"
            @click="saveOrSubmit('SAVE')"
          >
            {{ $t('common.save') }}
          </el-button>

          <!--提交-->
          <el-button
            type="primary"
            @click="saveOrSubmit('SUBMIT')"
          >
            {{ $t('common.submit') }}
          </el-button>

          <!--返回-->
          <el-button @click="back">
            {{ $t('bidMod.backTo') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>

<script>
import { tabTodoMixin } from '@/utils/mixins'
import DynamicCutoffTime from 'lib@/components/dynamic-cutoff-time'
import FileDynamic from 'lib@/components/c-file-management/file-dynamic'
import RequirementsInfo from 'lib@/composition/techExchangeManagement/requirementsInfo'
import CToolbar from 'lib@/components/c-toolbar'
import { techExchangeSupApi } from 'mods@/techExchangeSupplier/api'
export default {
  name: 'TechExchangeManagementSupplierFeedback',

  components: {
    DynamicCutoffTime,
    FileDynamic,
    RequirementsInfo,
    CToolbar
  },

  mixins: [tabTodoMixin],

  data () {
    return {
      activeDims: ['1', '2', '3'],
      techExchangeData: {
        technicalExchangeEndTime: '',
        technicalExchangeFormCode: '',
        technicalExchangeTitle: '',
        orgOuName: '',
        technicalExchangeType: ''
      },
      tecExcFiles: [],
      tecExcFilesSupplier: [],
      tecExcMaterialItems: [],
      technicalExchangeId: this.$attrs.params.row.technicalExchangeId || '',
      // 反馈单据ID，用于反馈附件绑定
      technicalExchangeFeedbackId: ''
    }
  },

  created () {
    this.getExcInfo()
    this.$nextTick(() => {
      this.$refs.sceneAttachment.loadFileInfo()
    })
  },

  methods: {
    /* 查询单据 */
    async getExcInfo () {
      if (!this.technicalExchangeId) {
        return
      }

      const response = await techExchangeSupApi.getInfo(this.technicalExchangeId)

      if (response && response.data) {
        let formData = {}
        Object.keys(this.techExchangeData).forEach(item => {
          formData = {
            ...formData,
            [item]: response.data[item]
          }
        })

        this.techExchangeData = formData
        this.tecExcMaterialItems = response.data.tecExcMaterialItems
        this.technicalExchangeFeedbackId = response.data.technicalExchangeFeedbackId
        this.$nextTick(() => {
          // 更新采购方技术要求附件表格
          this.$refs.tecExcFilesFileDynamic.loadFileInfo()
        })
      }
    },

    /* 提交 / 保存 */
    async saveOrSubmit (type) {
      if (this.tecExcFilesSupplier.length === 0) {
        this.$message.warning(this.$t('bidMod.uploadPlanAttachmentInfo'))
        return
      }

      for (let i = 0; i < this.tecExcFilesSupplier.length; i++) {
        if (!this.tecExcFilesSupplier[i].fileuploadId) {
          // 方案附件第${i + 1}行请上传方案附件
          this.$message.warning(this.$t('cusEntry.supplement20250211.message22', { index: i + 1 }))
          return
        }
      }

      if (type === 'SUBMIT') {
        const confirm = await this.$confirm(
          this.$t('bidMod.sureSubmitFeedback'),
          this.$t('common.tips'),
          {
            confirmButtonText: this.$t('common.confirm'),
            cancelButtonText: this.$t('common.cancel'),
            type: 'warning'
          }
        )
        if (confirm !== 'confirm') {
          return
        }
      }

      try {
        const response = await techExchangeSupApi[type.toLowerCase()]({
          technicalExchangeId: this.technicalExchangeId,
          tecExcFiles: this.tecExcFilesSupplier
        })
        if (response) {
          if (type === 'SAVE') {
            this.$message.success(this.$t('common.successSave'))
            this.technicalExchangeFeedbackId = response.data.technicalExchangeFeedbackId
            // 查询
            await this.getExcInfo()
          }
          if (type === 'SUBMIT') {
            this.$message.success(this.$t('common.successSubmit'))
            this.back('refresh')
          }
        }
      } catch (e) {
        console.error(e)
      }
    },

    /* 返回 */
    back (type) {
      this.$emit('tab-remove', this.$attrs.tabName)
      if (type === 'refresh') {
        this.__setTabTodo('TechExchangeManagementSupplierList.getQueryData')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.detail-info-wrap.el-row {
  margin: 20px 0;
  span {
    padding-right: 11px;
    display: inline-block;
  }
}
</style>
