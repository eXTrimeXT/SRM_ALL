<template>
  <SrmDialog
    :title="$t('bidMod.clarification')"
    size="large"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    destroy-on-close
    append-to-body
  >
    <!--表单-->
    <el-form
      ref="answerForm"
      :model="answerFormData"
      label-position="top"
      :rules="answerFormRules"
      :disabled="readonly"
    >
      <SrmRow>
        <!-- 澄清编号 -->
        <SrmCol :init-col="3">
          <el-form-item :label="$t('bidMod.answerNum')">
            <el-input v-model="answerFormData.answerNum" disabled />
          </el-form-item>
        </SrmCol>

        <!-- 澄清标题 -->
        <SrmCol :init-col="3">
          <el-form-item prop="answerTitle" :label="$t('bidMod.answerTitle')">
            <el-input v-model="answerFormData.answerTitle" />
          </el-form-item>
        </SrmCol>

        <!-- 项目名称 -->
        <SrmCol :init-col="3">
          <el-form-item :label="$t('bidMod.bidingName')" prop="souName">
            <QuickSearch
              :show-input="answerFormData.souName"
              :scope-data="answerFormData"
              show-key="souName"
              name="scc_sou_project"
              :pre-query-data="{ 't.sou_type': souType }"
              :disabled="readonly || isFromQuestion"
              @close-quicksearch="setSouProject"
            />
          </el-form-item>
        </SrmCol>

        <!-- 项目编号 -->
        <SrmCol :init-col="3">
          <el-form-item :label="$t('bidMod.bidingNum')">
            <el-input v-model="answerFormData.souNo" disabled />
          </el-form-item>
        </SrmCol>
      </SrmRow>
    </el-form>

    <!-- 澄清内容 -->
    <h3>{{ $t("bidMod.clarifyContent") }}</h3>
    <Tinymce
      id="answerTinymce"
      v-model="answerInfo"
      :height="260"
      @setup="ready"
    />

    <!-- 附件 -->
    <h3>{{ $t("bidMod.attachment") }}</h3>
    <FileDynamic
      ref="sceneAttachment"
      v-model="sceneFiles"
      scene-module-code="SCENE_SOU_ANSWER_FILE_ATTACHMENT"
      :business-id="editRow.answerId || ''"
      :editable="!readonly"
      :need-init="false"
    />

    <div slot="footer">
      <!--取消-->
      <el-button @click="dialogVisible = false">
        {{ $t("common.cancel") }}
      </el-button>

      <template v-if="!readonly">
        <!--采购商按钮-->
        <template v-if="userTypeFlag.isBuyer">
          <!--暂存 来源质疑单只能直接发布-->
          <el-button
            v-if="!isFromQuestion"
            type="primary"
            @click="tempSaveOrSubmit('save')"
          >
            {{ $t("common.staging") }}
          </el-button>

          <!--发布-->
          <el-button type="primary" @click="tempSaveOrSubmit('submit')">
            {{ $t('common.publish') }}
          </el-button>
        </template>
      </template>

      <!--供应商按钮-->
      <template v-if="userTypeFlag.isVendor">
        <!--接受澄清 存在质疑ID，并且已发布-->
        <el-button
          v-if="editRow.answerId && canAccept"
          type="primary"
          @click="acceptAnswer"
        >
          {{ $t("bidMod.acceptClarify") }}
        </el-button>
      </template>
    </div>
  </SrmDialog>
</template>

<script>
/**
 * 新增 / 查看 澄清弹窗
 */
import { qaBuyerHttp, qaVendorHttp } from './api'
import { mapGetters } from 'vuex'
import { USER_TYPE_ENUM } from 'lib@/composition/origin/enum'
import Tinymce from '@/components/Tinymce/index.vue'
import FileDynamic from 'lib@/components/c-file-management/file-dynamic.vue'
import QuickSearch from 'lib@/components/QuickSearch.vue'

export default {
  name: 'AnswerDetail',

  components: {
    Tinymce,
    FileDynamic,
    QuickSearch
  },

  props: {
    visible: {
      type: Boolean,
      default: false
    },
    // 寻源类型
    souType: {
      type: String,
      required: true
    },
    editRow: {
      type: Object
    },
    pageType: {
      type: String,
      default: 'add'
    },
    // 是否来源质疑
    isFromQuestion: {
      type: Boolean,
      default: false
    }
  },

  data () {
    return {
      answerFormData: {
        projectId: '',
        souName: '',
        souNo: '',
        answerNum: '',
        answerTitle: ''
      },
      options: [],
      sceneFiles: [],
      answerInfo: null,
      answerFormRules: {
        // 请选择项目
        souName: { required: true, message: this.$t('bidMod.msgSelProject') },
        // 请填写标题
        answerTitle: { required: true, message: this.$t('bidMod.msgInputTitle') }
      },
      bankRowIndex: '',
      canAccept: false
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
    },

    ...mapGetters(['userType']),

    // api入口，用户类型转小写
    apiUserType () {
      return this.userType.toLowerCase()
    },

    userTypeFlag () {
      return {
        isBuyer: this.userType === USER_TYPE_ENUM.BUYER,
        isVendor: this.userType === USER_TYPE_ENUM.VENDOR
      }
    },

    // api入口
    qaHttp () {
      return this.userTypeFlag.isBuyer ? qaBuyerHttp : qaVendorHttp
    },

    readonly () {
      return this.pageType === 'view'
    }
  },

  mounted () {
    if (this.pageType === 'add') {
      // 新增
      if (this.isFromQuestion) {
        // 来自质疑
        this.answerFormData = {
          ...this.answerFormData,
          ...this.editRow
        }
      }
      this.$nextTick(() => {
        this.$refs.sceneAttachment.loadFileInfo()
      })
    } else {
      this.getDetail()
    }
  },

  methods: {
    /* 查询澄清详情 */
    async getDetail () {
      const response = await this.qaHttp.souAnswer.getDetail(this.souType, this.editRow.answerId)
      if (response && response.data) {
        const { answerContent, ...rest } = response.data

        this.answerFormData = { ...rest }
        this.answerInfo = answerContent

        // 已发布 供应商可以接受澄清
        this.canAccept = rest.answerStatus === 'ISSUED'
        this.$nextTick(() => {
          this.$refs.sceneAttachment.loadFileInfo()
        })
      }
    },

    /* 选择一个项目 */
    setSouProject (val) {
      const { projectId = '', souName = '', souNo = '' } = val || {}
      this.answerFormData.projectId = projectId
      this.answerFormData.souName = souName
      this.answerFormData.souNo = souNo
    },

    /* 供应商 - 接受澄清 */
    async acceptAnswer () {
      const response = await qaVendorHttp.souAnswer.vendorAccept(this.souType, this.editRow.answerId)
      if (response) {
        this.$message.success(response.message)
        this.$emit('refresh')
        this.dialogVisible = false
      }
    },

    /* 采购商 - 暂存/提交发布 澄清单 */
    async tempSaveOrSubmit (type) {
      if (!this.answerInfo) {
        // 请填写澄清内容
        this.$message.warning(this.$t('bidMod.msgClarifyContent'))
        return false
      }

      // 校验
      const valid = await this.$refs.answerForm.validate().catch(() => { /* noting */ })
      if (!valid) {
        this.__focus_error__()
        return
      }

      const response = await qaBuyerHttp.souAnswer.tempSaveOrSubmit(this.souType, {
        ...this.answerFormData,
        answerContent: this.answerInfo,
        sceneFiles: this.sceneFiles,
        tempSave: type === 'save'
      })
      if (response) {
        this.$message.success(type === 'save' ? this.$t('common.successSave') : this.$t('common.successPublish'))

        this.$emit('refresh')
        this.dialogVisible = false
      }
    },

    /* 富文本加载完毕 */
    ready (editorInstance) {
      if (this.readonly) {
        // 设置只读
        editorInstance.setMode('readonly')
      }
    }
  }
}
</script>
