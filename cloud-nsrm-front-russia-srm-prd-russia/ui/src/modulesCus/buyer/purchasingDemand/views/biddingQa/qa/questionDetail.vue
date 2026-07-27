<template>
  <SrmDialog
    :title="$t('bidMod.questionList')"
    size="large"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    destroy-on-close
    append-to-body
  >
    <el-form
      ref="questionForm"
      :model="questionFormData"
      label-position="top"
      :rules="questionFormRules"
      :disabled="readonly"
    >
      <SrmRow>
        <SrmCol :init-col="3">
          <!-- 质疑编号 -->
          <el-form-item :label="$t('bidMod.questionNum')">
            <el-input v-model="questionFormData.questionNum" disabled />
          </el-form-item>
        </SrmCol>

        <!-- 项目名称 -->
        <SrmCol :init-col="3">
          <el-form-item :label="$t('bidMod.bidingName')" prop="souName">
            <!-- <el-select
              v-if="userTypeFlag.isVendor"
              v-model="questionFormData.projectId"
              filterable
              remote
              :placeholder="$t('bidMod.msgKeyword')"
              :remote-method="remoteMethod"
              clearable
              automatic-dropdown
              @change="setSouProject"
            >
              <el-option
                v-for="item in souProjectOptions"
                :key="item.id"
                :label="item.label"
                :value="item.value"
              />
            </el-select> -->
            <el-input :value="questionFormData.souName" disabled />
          </el-form-item>
        </SrmCol>

        <SrmCol :init-col="3">
          <!-- 质疑标题 -->
          <el-form-item :label="$t('bidMod.questionTitle')" prop="questionTitle">
            <el-input v-model="questionFormData.questionTitle" :disabled="!userTypeFlag.isVendor" />
          </el-form-item>
        </SrmCol>

        <SrmCol :init-col="3">
          <!-- 质疑状态 -->
          <el-form-item :label="$t('bidMod.questionStatus')">
            <DictSelect
              v-model="questionFormData.questionStatus"
              code="QUERY_STATUS"
              disabled
            />
          </el-form-item>
        </SrmCol>

        <SrmCol :init-col="3">
          <el-form-item label="创建时间">
            <el-input v-model="questionFormData.creationDate" disabled />
          </el-form-item>
        </SrmCol>

        <SrmCol v-if="questionFormData.questionStatus==='ABANDON'" :init-col="1">
          <el-form-item label="废弃说明">
            <el-input v-model="questionFormData.reasonDesc" disabled />
          </el-form-item>
        </SrmCol>
      </SrmRow>
    </el-form>

    <!-- 质疑内容 -->
    <h3>{{ $t("bidMod.questionContent") }}</h3>
    <Tinymce
      id="questionTinymce"
      v-model="questionContent"
      :height="260"
      @setup="ready"
    />

    <!-- 附件 -->
    <h3>{{ $t("bidMod.attachment") }}</h3>
    <FileDynamic
      ref="sceneAttachment"
      v-model="sceneFiles"
      scene-module-code="SCENE_SOU_QUESTION_FILE_ATTACHMENT"
      :business-id="editRow.questionId || ''"
      :editable="!readonly && userTypeFlag.isVendor"
      :need-init="false"
    />

    <h3 v-if="!userTypeFlag.isVendor || extReplayContent">质疑回复</h3>
    <Tinymce
      v-if="!userTypeFlag.isVendor || extReplayContent"
      id="questionTinymce"
      v-model="extReplayContent"
      :height="260"
      @setup="ready2"
    />

    <h3 v-if="!userTypeFlag.isVendor || replayFiles?.length > 0">质疑回复附件</h3>
    <FileDynamic
      ref="sceneAttachment2"
      v-if="!userTypeFlag.isVendor || replayFiles?.length > 0"
      v-model="replayFiles"
      scene-module-code="SCENE_SOU_QUESTION_REPLAY_FILE_ATTACHMENT"
      :business-id="editRow.questionId || ''"
      :editable="!readonly && userTypeFlag.isBuyer"
      :need-init="false"
    />

    <div slot="footer">
      <!--取消-->
      <el-button @click="dialogVisible = false">
        {{ $t('common.cancel') }}
      </el-button>

      <template v-if="userTypeFlag.isBuyer && !readonly">
        <!--暂存-->
        <el-button type="primary" @click="tempSaveOrSubmit2('save', true)">
          {{ $t("common.staging") }}
        </el-button>

        <el-button type="primary" @click="tempSaveOrSubmit2('submit', true)">
          提交
        </el-button>
      </template>

      <!--供应商权限-->
      <template v-if="userTypeFlag.isVendor && !readonly">
        <!--暂存-->
        <el-button type="primary" @click="tempSaveOrSubmit('save')">
          {{ $t("common.staging") }}
        </el-button>

        <el-button type="primary" @click="tempSaveOrSubmit('submit')">
          提交
        </el-button>
      </template>
    </div>
  </SrmDialog>
</template>

<script>
/**
 * 新增/查看 质疑弹窗
 */
import { qaBuyerHttp, qaVendorHttp } from './api'
import { mapGetters } from 'vuex'
import { USER_TYPE_ENUM } from 'lib@/composition/origin/enum'
import Tinymce from '@/components/Tinymce/index.vue'
import FileDynamic from 'lib@/components/c-file-management/file-dynamic.vue'
import { transformMQL } from '@/library/utils/util'
import { qa } from 'modc@/buyer/purchasingDemand/api'

export default {
  name: 'QuestionDetail',

  components: {
    Tinymce,
    FileDynamic
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
    pageType: {
      type: String,
      default: 'add'
    },
    editRow: {
      type: Object,
      required: true
    }
  },

  data () {
    return {
      questionFormData: {
        questionNum: '',
        questionTitle: '',
        questionStatus: '',
        projectId: '',
        souName: '',
        souNo: ''
      },
      questionFormRules: {
        projectId: { required: true, message: this.$t('bidMod.msgSelProject') },
        questionTitle: {
          message: this.$t('bidMod.msgInputTitle'),
          required: true
        }
      },
      souProjectOptions: [],
      souProjectPageSize: 999,
      questionContent: null,
      extReplayContent: null,
      sceneFiles: [],
      replayFiles: []
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
      this.$nextTick(() => {
        this.$refs.sceneAttachment.loadFileInfo()
        console.log(this.editRow, 'editRow')
        this.$set(this.questionFormData, 'souNo', this.editRow.souNo)
        this.$set(this.questionFormData, 'souName', this.editRow.souName)
        this.$set(this.questionFormData, 'projectId', this.editRow.projectId)
        this.$set(this.questionFormData, 'extType', this.editRow.extType)

        // 获取项目列表
        // this.querySouProjectOrders({})
      })
    } else {
      this.getDetailData()
    }
  },

  methods: {
    /* 查询详情数据 */
    async getDetailData () {
      console.log(this.editRow, 'editRow')
      if (!this.editRow) {
        return
      }
      const allparam = { questionId: this.editRow.questionId }
      const query = {
        '*': {},
        'sceneFiles': {'*': {}},
        'replayFiles': {'*': {}},
      }

      if (this.userTypeFlag.isBuyer) {
        const saveData = transformMQL.save('Question', [allparam], 'read',query)
        qa.read(saveData).then((datas) => {
          console.log(datas.data[0], 'datas')
          const {questionContent, extReplayContent, sceneFiles, replayFiles, ...res} = datas.data[0]
          this.questionFormData = res
          this.questionContent = questionContent
          this.extReplayContent = extReplayContent
          this.sceneFiles = sceneFiles
          this.replayFiles = replayFiles
          this.$nextTick(() => {
            this.$refs.sceneAttachment.loadFileInfo()
            this.$refs.sceneAttachment2.loadFileInfo()
          })
        })
      } else {
        const saveData = transformMQL.save('VendorQuestion', [allparam], 'read',query)
        qa.readVendor(saveData).then((datas) => {
          console.log(datas.data[0], 'datas')
          const {questionContent, extReplayContent, sceneFiles, replayFiles, ...res} = datas.data[0]
          this.questionFormData = res
          this.questionContent = questionContent
          this.extReplayContent = extReplayContent
          this.sceneFiles = sceneFiles
          this.replayFiles = replayFiles
          this.$nextTick(() => {
            this.$refs.sceneAttachment.loadFileInfo()
            this.$refs.sceneAttachment2.loadFileInfo()
          })
        })
      }

      // if (response) {
      //
      //   // 查询项目信息
      //   await this.querySouProjectOrders({ souNo: rest.souNo })
      // }
    },

    /* 供应商 - 选择一个项目 */
    setSouProject (val) {
      const project = this.souProjectOptions.find(item => item.value === val)
      const { label = '', no = '' } = project || {}
      this.questionFormData.souNo = no
      this.questionFormData.souName = label
    },

    /* 查询寻源单列表 */
    async querySouProjectOrders ({ souName = '', souNo = '' }) {
      if (this.userTypeFlag.isBuyer) {
        // 采购商不能访问
        return
      }

      const response = await qaVendorHttp.order.page({
        souName,
        souNo,
        souType: this.souType,
        pageSize: this.souProjectPageSize,
        pageNum: 1
      })
      if (response && response.data) {
        if (response.data.total && response.data.total > this.souProjectPageSize) {
          // 9990
          this.souProjectPageSize = this.souProjectPageSize * 10
          await this.querySouProjectOrders({ souName, souNo })
          return
        }

        this.souProjectOptions = (response.data.list || []).map(item => ({
          id: item.projectId,
          no: item.souNo,
          value: item.projectId,
          label: item.souName
        }))
      }
    },

    /* 下拉框搜索 */
    remoteMethod (souName) {
      this.querySouProjectOrders({ souName })
    },

    /* 供应商 - 暂存/提交发布 质疑单 */
    async tempSaveOrSubmit (type, bol) {
      if (!bol) {
        if (!this.questionContent) {
          // 请填写质疑内容
          this.$message.warning(this.$t('bidMod.msgInputQuestion'))
          return false
        }

        // 校验
        const valid = await this.$refs.questionForm.validate().catch(() => { /* noting */ })
        if (!valid) {
          this.__focus_error__()
          return
        }
      }

      const allparam = {
        ...this.questionFormData,
        questionContent: this.questionContent,
        extReplayContent: this.extReplayContent,
        sceneFiles: this.sceneFiles,
        replayFiles: this.replayFiles,
        questionStatus: type == 'save' ? 'DRAFT' : 'SUBMITTED'
      }
      if (type == 'save') {
        const saveData = transformMQL.save('VendorQuestion', [allparam], 'save')
        qa.saveVendor(saveData).then((datas) => {
          this.$message.success(type === 'save' ? this.$t('common.successSave') : this.$t('common.successPublish'))

          this.$emit('refresh')
          this.dialogVisible = false
        })
      } else {
        const saveData = transformMQL.save('VendorQuestion', [allparam], 'submit')
        qa.submitVendor(saveData).then((datas) => {
          this.$message.success(type === 'save' ? this.$t('common.successSave') : this.$t('common.successPublish'))

          this.$emit('refresh')
          this.dialogVisible = false
        })
      }
    },

    async tempSaveOrSubmit2 (type, bol) {
      const allparam = {
        ...this.questionFormData,
        questionContent: this.questionContent,
        extReplayContent: this.extReplayContent,
        sceneFiles: this.sceneFiles,
        replayFiles: this.replayFiles,
      }
      if (type == 'save') {
        const saveData = transformMQL.save('Question', [allparam], 'save')
        qa.save(saveData).then((datas) => {
          this.$message.success(type === 'save' ? this.$t('common.successSave') : this.$t('common.successPublish'))

          this.$emit('refresh')
          this.dialogVisible = false
        })
      } else {
        const saveData = transformMQL.save('Question', [allparam], 'submit')
        qa.submit(saveData).then((datas) => {
          this.$message.success(type === 'save' ? this.$t('common.successSave') : this.$t('common.successPublish'))

          this.$emit('refresh')
          this.dialogVisible = false
        })
      }

    },

    ready (editorInstance) {
      if (this.readonly || this.userTypeFlag.isBuyer) {
        editorInstance.setMode('readonly')
      }
    },

    ready2 (editorInstance) {
      if (this.readonly || !this.userTypeFlag.isBuyer) {
        editorInstance.setMode('readonly')
      }
    }
  }
}
</script>
