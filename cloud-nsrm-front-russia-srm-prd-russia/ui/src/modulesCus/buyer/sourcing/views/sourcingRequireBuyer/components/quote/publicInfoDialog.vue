<template>
  <SrmDialog
    :title="readonly ? '查看公示修改详情' : '修改公示信息' "
    size="large"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    append-to-body
  >
    <el-form ref="form" :model="form" :rules="formRules">
      <SrmRow>
        <SrmCol :init-col="2">
          <h4>调整前信息</h4>
        </SrmCol>
        <SrmCol :init-col="2">
          <h4>调整后信息</h4>
        </SrmCol>
        <SrmCol :init-col="2">
          <el-form-item prop="beforeProjectName" label="项目名称">
            <el-input v-model="form.beforeProjectName" disabled />
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="2">
          <el-form-item prop="afterProjectName" label="项目名称">
            <el-input v-model="form.afterProjectName" :class="{red:copyForm.projectName}" :disabled="readonly" />
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="2">
          <el-form-item prop="beforeProjectScope" label="项目概况与招标范围">
            <el-input
              v-model="form.beforeProjectScope"
              disabled
              type="textarea"
              maxlength="3000"
              :autosize="{minRows:3,maxRows:5}"
            />
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="2">
          <el-form-item prop="afterProjectScope" label="项目概况与招标范围">
            <el-input
              v-model="form.afterProjectScope"
              :class="{red:copyForm.projectScope}"
              :disabled="readonly"
              type="textarea"
              maxlength="3000"
              :autosize="{minRows:3,maxRows:5}"
            />
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="2">
          <el-form-item prop="beforeTechnicalReq" label="技术要求">
            <el-input
              v-model="form.beforeTechnicalReq"
              disabled
              type="textarea"
              maxlength="3000"
              :autosize="{minRows:3,maxRows:5}"
            />
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="2">
          <el-form-item prop="afterTechnicalReq" label="技术要求">
            <el-input
              v-model="form.afterTechnicalReq"
              :class="{red:copyForm.technicalReq}"
              :disabled="readonly"
              type="textarea"
              maxlength="3000"
              :autosize="{minRows:3,maxRows:5}"
            />
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="2">
          <el-form-item prop="beforeVendorQualReq" label="供应商资质要求">
            <el-input v-model="form.beforeVendorQualReq" disabled type="textarea" :autosize="{minRows:3,maxRows:5}" />
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="2">
          <el-form-item prop="afterVendorQualReq" label="供应商资质要求">
            <el-input
              v-model="form.afterVendorQualReq"
              :class="{red:copyForm.vendorQualReq}"
              :disabled="readonly"
              type="textarea"
              :autosize="{minRows:3,maxRows:5}"
            />
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="2">
          <el-form-item prop="beforePerformanceReq" label="业绩要求">
            <el-input v-model="form.beforePerformanceReq" disabled type="textarea" :autosize="{minRows:3,maxRows:5}" />
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="2">
          <el-form-item prop="afterPerformanceReq" label="业绩要求">
            <el-input
              v-model="form.afterPerformanceReq"
              :class="{red:copyForm.performanceReq}"
              :disabled="readonly"
              type="textarea"
              :autosize="{minRows:3,maxRows:5}"
            />
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="1">
          <el-form-item prop="updateReason" label="修改原因">
            <el-input v-model="form.updateReason" :disabled="readonly" type="textarea" :autosize="{minRows:3,maxRows:5}" />
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="1">
          <el-form-item prop="archiveFileId" label="归档附件（不对外）">
            <SrmCommonFile
              :default-file="{
                fileId: form.archiveFileId,
                fileName: form.archiveFileName
              }"
              :readonly="readonly"
              @on-change="({file}) => fileChange(file) "
            />
          </el-form-item>
        </SrmCol>
      </SrmRow>
    </el-form>
    <div class="file">
      <h4>修改公示附件</h4>
      <FileDynamic
        v-if="!readonly"
        ref="sceneAttachment"
        v-model="sceneFileList"
        scene-module-code="SCENE_SOU_REQ_ATTACHMENT"
        :business-id="reqHeadId"
        :editable="true"
        :needInit="false"
      />
      <el-table
        v-else
        border
        stripe
        :data="fileUploads"
      >
        <el-table-column
          type="index"
          width="60"
          label="序号"
        />
        <!--附件名称-->
        <SrmCommonFile
          type="table-column"
          :table-column-options="{
            align: 'left',
            label: $t('bidMod.fileName'),
            prop: 'fileId',
            nameProp: 'fileName'
          }"
          :readonly="true"
        />
        <el-table-column
          prop="remark"
          label="备注"
        />
      </el-table>
    </div>
    <div slot="footer" class="dialog-footer">
      <template v-if="!readonly">
        <el-button @click="dialogVisible = false">
          {{ $t("common.cancel") }}
        </el-button>
        <el-button type="primary" @click="handleConfirm">
          {{ $t("common.confirm") }}
        </el-button>
      </template>
      <template v-else>
        <el-button @click="dialogVisible = false">
          {{ $t("common.close") }}
        </el-button>
      </template>
    </div>
  </SrmDialog>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { transformMQL } from 'lib@/utils/util'
import souHttp from '../../../../api'
import FileDynamic from '@/library/components/c-file-management/file-dynamic'

export default {
  name: 'PublicInfoDialog',
  components: {
    TableView,
    FormWrapper,
    FileDynamic
  },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    readonly: {
      type: Boolean,
      default: false
    },
    baseForm: {
      type: Object,
      default: () => {}
    },
    infoHistoryId: null
  },
  data () {
    return {
      form: {
        beforeProjectName: null,
        afterProjectName: null,
        beforeProjectScope: null,
        afterProjectScope: null,
        beforeVendorQualReq: null,
        afterVendorQualReq: null,
        beforeTechnicalReq: null,
        afterTechnicalReq: null,
        beforePerformanceReq: null,
        afterPerformanceReq: null,
        updateReason: null,
        archiveFileId: null,
        archiveFileName: null
      },
      formRules: {
        afterProjectName: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        afterProjectScope: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        afterVendorQualReq: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        afterTechnicalReq: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        afterPerformanceReq: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        updateReason: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }]
      },
      baseAttrs: ['projectName', 'projectScope', 'vendorQualReq', 'technicalReq', 'performanceReq'],
      copyForm: {},
      sceneFileList: [],
      originalFiles: [], // 用于比较是否附件有修改
      reqHeadId: null,
      fileUploads: []
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
  watch: {
    baseForm: {
      handler (nVal) {
        if (nVal) {
          let attrs = this.baseAttrs
          for (let key of attrs) {
            let newAttr = this.transformAttr('before', key)
            let afterAttr = this.transformAttr('after', key)
            this.form[newAttr] = nVal[key]
            this.form[afterAttr] = nVal[key]
          }
          const { fileUploads = [], reqHeadId } = nVal
          this.originalFiles = JSON.parse(JSON.stringify(fileUploads))
          this.sceneFileList = fileUploads
          this.reqHeadId = reqHeadId
          this.loadFileInfo()
        }
      },
      immediate: true,
      deep: true
    },
    infoHistoryId: {
      handler (nVal) {
        if (nVal) {
          this.getFormDetail()
        }
      },
      immediate: true,
      deep: true
    },
    form: {
      handler (nVal) {
        if (nVal) {
          for (let key of this.baseAttrs) {
            let newAttr = this.transformAttr('before', key)
            let afterAttr = this.transformAttr('after', key)
            if (nVal[newAttr] !== nVal[afterAttr]) {
              this.$set(this.copyForm, key, true)
            } else {
              this.$set(this.copyForm, key, false)
            }
          }
        }
      },
      deep: true
    }
  },
  methods: {
    transformAttr (pre, attr) {
      return pre + attr.slice(0, 1).toUpperCase() + attr.slice(1)
    },
    loadFileInfo (fileRef = 'sceneAttachment') {
      this.$nextTick(() => {
        this.$refs[fileRef].loadFileInfo()
      })
    },
    handleConfirm () {
      this.$refs.form.validate(valid => {
        if (valid) {
          for (let item of this.sceneFileList) {
            if (!item.fileuploadId) {
              return this.$message.warning('附件不能为空')
            }
          }
          let count = 0
          Object.keys(this.copyForm).forEach(key => {
            if (this.copyForm[key]) {
              count++
            }
          })
          console.log('originalFiles', this.originalFiles)
          console.log('sceneFileList', this.sceneFileList)
          if (JSON.stringify(this.sceneFileList.map(item => item.fileuploadId)) !== JSON.stringify(this.originalFiles.map(item => item.fileuploadId))) {
            count++
          }
          if (!count) {
            return this.$message.warning('公示信息未修改，不允许提交')
          }
          this.$emit('confirm', {
            ...this.form,
            sceneFileList: this.sceneFileList.map((item, index) => ({
              ...item,
              sortIndex: index + 1
            }))
          })
        }
      })
    },
    async getFormDetail () {
      let transformParams = transformMQL.save('SouInfoHistoryBuyer', [this.infoHistoryId], 'read', {
        '*': {},
        'fileUploads': {
          '*': {}
        }
      })
      const response = await souHttp.publicHisRead(transformParams)
      if (response.data.length) {
        const { fileUploads = [], ...rest } = response.data[0]
        this.form = rest
        this.fileUploads = fileUploads
      }
    },
    fileChange (file = {}) {
      console.log('file', file)
      const { fileId, fileName } = file
      this.form.archiveFileId = fileId
      this.form.archiveFileName = fileName
    },
    resetFields () {
      this.$nextTick(() => {
        this.$refs.form.resetFields()
      })
    }
  }
}
</script>
<style lang="scss" scoped>
.red {
  border: 1px solid red;
}
</style>
