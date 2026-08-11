<template>
  <div class="upload-file-common-demo">
    <!--默认形式-->
    <div>
      <h2>
        <!-- 默认形式 -->
        {{ $t("cusEntry.supplement20250211.defaultForm") }}
      </h2>
      <h2>
        <!-- 默认形式 -->
        {{ $t("cusEntry.supplement20250211.defaultForm") }}
      </h2>

      <SrmRow>
        <SrmCol :init-col="3">
          <h4>
            <!-- 允许上传 -->
            {{ $t("cusEntry.supplement20250211.allowUpload") }}
          </h4>
          <h4>
            <!-- 允许上传 -->
            {{ $t("cusEntry.supplement20250211.allowUpload") }}
          </h4>
          <SrmCommonFile
            :default-file="{ fileId, fileName }"
            :extra-data="extraData"
            @on-change="uploadFileChange"
          />
        </SrmCol>

        <SrmCol :init-col="3">
          <h4>
            <!-- 允许上传，利用默认插槽自定义触发上传的标签 -->
            {{ $t("cusEntry.supplement20250211.allowUploadCustomTriggerLabel") }}
          </h4>
          <SrmCommonFile
            :default-file="{ fileId: fileIdA, fileName: fileNameA }"
            @on-change="uploadFileChangeA"
          >
            <el-button
              type="primary"
              icon="el-icon-upload"
              circle
            />
          </SrmCommonFile>
        </SrmCol>

        <SrmCol :init-col="3">
          <h4>
            <!-- 支持多选且多文件上传，当前配置最多10个 -->
            {{ $t("cusEntry.supplement20250211.multiSelectAndMultiFileUploadLimit10") }}
          </h4>
          <SrmCommonFile
            multiple
            :limit="10"
            :file-list="fileList"
            @on-change="uploadFileListChange"
          />
        </SrmCol>
      </SrmRow>
    </div>

    <el-divider />

    <!--图片卡片-->
    <div>
      <h2>
        <!-- 图片卡片 -->
        {{ $t("cusEntry.supplement20250211.imageCard") }}
      </h2>

      <h4>
        <!-- 拖拽上传，当前配置1个 -->
        {{ $t("cusEntry.supplement20250211.dragAndDropUploadCurrentConfig1") }}
      </h4>
      <SrmRow>
        <SrmCol :init-col="3">
          <SrmCommonFile
            :default-file="{ fileId: fileIdD, fileName: fileNameD }"
            drag
            :limit="1"
            :dragger-options="{
              width: '100%',
              height: '200px'
            }"
            list-type="picture-card"
            @on-change="dragFileChange"
          />
        </SrmCol>
      </SrmRow>

      <h4>
        <!-- 拖拽上传，支持多选，当前配置3个 -->
        {{ $t("cusEntry.supplement20250211.dragDropUploadSupportMultiSelectCurrentConfig3") }}
      </h4>
      <SrmRow>
        <SrmCol :init-col="1">
          <!-- '上传文件' -->
          <!-- '点击或拖拽文件到此区域以上传，最多上传3个文件' -->
          <SrmCommonFile
            :file-list="fileListD"
            drag
            :limit="3"
            :dragger-options="{
              width: '280px',
              height: '230px',
              title: $t('dataConfMod.uploadFile'),
              tips: $t('cusEntry.supplement20250211.fileUploadArea')
            }"
            list-type="picture-card"
            @on-change="dragFileListChange"
          />
        </SrmCol>
      </SrmRow>

      <h4>
        <!-- 只读列表，自定义id和name，配置为空提示 -->
        {{ $t("cusEntry.supplement20250211.readOnlyListCustomIdAndNameConfigEmptyPrompt") }}
      </h4>
      <SrmRow>
        <SrmCol :init-col="1">
          <!-- 图片列表为空！ -->
          <SrmCommonFile
            type="link"
            list-type="picture-card"
            :file-list="fileListE"
            :file-key-options="{
              idKey: 'fileIdE',
              nameKey: 'fileNameE'
            }"
            :picture-style-options="{
              width: '280px',
              height: '230px'
            }"
            :empty-text="$t('cusEntry.supplement20250211.imageListEmpty')"
            readonly
          />
        </SrmCol>
      </SrmRow>
    </div>

    <el-divider />

    <!--表单形式-->
    <div>
      <h2>
        <!-- 表单形式 集成了el-form-item -->
        {{ $t("cusEntry.supplement20250211.formItemIntegration") }}
      </h2>
      <el-form
        ref="demoForm"
        :model="formData"
        :rules="formRules"
        label-position="top"
        label-width="130px"
      >
        <srm-row>
          <srm-col :init-col="3">
            <!-- '附件上传，不做格式限制' -->
            <SrmCommonFile
              type="form-item"
              :default-file="{ fileId: formData.fileId, fileName: formData.fileName }"
              :form-item-options="{
                label: $t('cusEntry.supplement20250211.fileUploadWithoutFormatRestriction'),
                prop: 'fileId',
                showUploadStatus: true
              }"
              @on-change="formUploadFileChange"
            />
          </srm-col>

          <srm-col :init-col="3">
            <!-- '附件上传，格式限制' -->
            <SrmCommonFile
              type="form-item"
              :default-file="{ fileId: formData.fileIdA, fileName: formData.fileNameA }"
              :form-item-options="{
                label: $t('cusEntry.supplement20250211.fileUploadFormatRestriction'),
                prop: 'fileIdA',
                showUploadStatus: true
              }"
              :upload-tips="$t('cusEntry.supplement20250211.fileUploadLimit')"
              :validate-options="{
                size: 1024 * 5,
                accept: ['.xlsx', '.doc']
              }"
              @on-change="formUploadFileAChange"
            />
          </srm-col>
        </srm-row>
      </el-form>

      <el-button
        type="primary"
        style="margin-top: 30px"
        @click="submit"
      >
        <!-- 提交 -->
        {{ $t("common.submit") }}
      </el-button>
    </div>

    <el-divider />

    <!--表格形式-->
    <div>
      <h2>
        <!-- 表格形式 集成了el-table-column -->
        {{ $t("cusEntry.supplement20250211.tableFormIntegratedElTableColumn") }}
      </h2>

      <el-table :data="tableData">
        <!-- 日期 -->
        <el-table-column
          align="center"
          prop="date"
          :label="$t('components.date')"
          width="100"
        />
        <!-- 附件可以上传下载预览，支持多文件上传 -->
        <el-table-column
          align="center"
          prop="address"
          :label="$t('cusEntry.supplement20250211.fileUploadDownloadPreviewSupportMulti')"
          min-width="180"
        >
          <template v-slot="scope">
            <SrmCommonFile
              multiple
              :limit="2"
              :file-list="scope.row.fileList"
              @on-change="value => tableUploadFileListChange(value, scope.$index)"
            />
          </template>
        </el-table-column>

        <SrmCommonFile
          type="table-column"
          :extra-data="extraData"
          :table-column-options="{
            label: $t('cusEntry.supplement20250211.fileUploadDownloadPreview'),
            prop: 'fileId',
            nameProp: 'fileName'
          }"
          :validate-options="{
            size: 1024 * 5,
            accept: ['.xlsx', '.doc']
          }"
          :upload-tips="$t('cusEntry.supplement20250211.fileUploadLimit')"
          @on-change="tableUploadFileChange"
        />
        <!-- 附件可以上传下载预览 -->
        <SrmCommonFile
          type="table-column"
          :table-column-options="{
            label: $t('cusEntry.supplement20250211.previewAttachmentDownloadOnly'),
            prop: 'fileIdRead',
            nameProp: 'fileNameRead',
            minWidth: '260'
          }"
          :upload-tips="$t('cusEntry.supplement20250211.downloadTemplateNotice')"
          :file-preview-options="{ target: 'blank' }"
          readonly
        />
        <!-- '图片卡片，配置title和tips为空只显示图标' -->
        <SrmCommonFile
          type="table-column"
          drag
          :table-column-options="{
            label: $t('cusEntry.supplement20250211.imageCardWithEmptyTitleAndTips'),
            prop: 'fileId2',
            nameProp: 'fileName2'
          }"
          :dragger-options="{
            width: '100px',
            height: '100px',
            title: '',
            tips: ''
          }"
          list-type="picture-card"
          @on-change="tableUploadFileChange2"
        />
      </el-table>
    </div>
  </div>
</template>

<script>
/**
 * index-new
 */
import SrmCommonFile from '@/library/components/srm-ui/packages/srm-common-file'

export default {
  name: 'IndexNew',

  components: {
    SrmCommonFile
  },

  data () {
    return {
      fileId: '',
      fileName: '',
      fileIdA: '',
      fileNameA: '',
      fileIdD: '',
      fileNameD: '',
      fileList: [
        { fileId: 358642365452416, fileName: '文件模板.xlsx' }
      ],
      fileListD: [
        // { fileId: 360938475217024, fileName: '竞价流程概览.jpg' }
      ],
      fileListE: [
        // { fileIdE: 360938475217024, fileNameE: '竞价流程概览.jpg' },
        // { fileIdE: 361103186286720, fileNameE: 'IMG20220331-135321982.png' }
      ],
      formData: {
        fileId: '',
        fileName: '',
        fileIdA: '',
        fileNameA: ''
      },
      formRules: {
        // '请上传附件！'
        fileId: [{ required: true, message: this.$t('bidMod.pleaseUploadFile'), type: 'number', trigger: ['change', 'blur'] }],
        // '请上传附件！'
        fileIdA: [{ required: true, message: this.$t('bidMod.pleaseUploadFile'), type: 'number', trigger: ['change', 'blur'] }]
      },
      extraData: {
        fileModular: 'base',
        fileFunction: 'UploadFileCommonDemo',
        fileType: 'excel'
      },
      tableData: [
        {
          date: '2016-05-02',
          fileId: 358642365452416,
          fileName: this.$t('cusEntry.supplement20250211.fileTemplate1Xlsx'),  // '文件模板(1).xlsx'
          fileIdRead: 358642365452416,
          fileNameRead: this.$t('cusEntry.supplement20250211.fileTemplate2'),  // '文件模板(2).xlsx'
          fileId2: 360938475217024,
          fileName2: this.$t('cusEntry.supplement20250211.bidProcessOverviewJpg')  // '竞价流程概览.jpg'
        },
        { date: '2016-05-04' }
      ]
    }
  },

  created () {
    // 模拟接口请求
    setTimeout(() => {
      // this.fileId = 358642365452416
      // this.fileName = '文件模板(1).xlsx'

      // this.fileIdD = 361070790004864
      // this.fileNameD = 'IMG20220331-135242101.jpeg'
      // this.fileIdD = 360938475217024
      // this.fileNameD = '竞价流程概览.jpg'

      this.fileListE = [
        { fileIdE: 360938475217024, fileNameE: this.$t('cusEntry.supplement20250211.bidProcessOverviewJpg') },  // '竞价流程概览.jpg'
        { fileIdE: 361103186286720, fileNameE: 'IMG20220331-135321982.png' }
      ]

      this.$set(this.tableData, 1, {
        ...this.tableData[1],
        fileId: 358642365452416,
        fileName: this.$t('cusEntry.supplement20250211.fileTemplate1Xlsx'),  // '文件模板(1).xlsx'
        fileIdRead: 358642365452416,
        fileNameRead: this.$t('cusEntry.supplement20250211.fileTemplate2')  // '文件模板(2).xlsx'
      })
    }, 3000)
  },

  methods: {
    /* 默认文件变更 */
    uploadFileChange ({ file }) {
      const { fileId = '', fileName = '' } = file || {}
      this.fileId = fileId
      this.fileName = fileName
    },

    /* 自定义插槽文件变更 */
    uploadFileChangeA ({ file }) {
      const { fileId = '', fileName = '' } = file || {}
      this.fileIdA = fileId
      this.fileNameA = fileName
    },

    /* 多文件上传文件变更 */
    uploadFileListChange ({ fileList }) {
      console.log(fileList)
      this.fileList = fileList
    },

    /* 拖拽上传文件变更 */
    dragFileChange ({ file }) {
      const { fileId = '', fileName = '' } = file || {}
      this.fileIdD = fileId
      this.fileNameD = fileName
    },

    /* 拖拽上传文件列表变更 */
    dragFileListChange ({ fileList }) {
      console.log(fileList)
      this.fileListD = fileList
    },

    /* 表单文件变更 */
    formUploadFileChange ({ file }) {
      const { fileId = '', fileName = '' } = file || {}
      this.formData.fileId = fileId
      this.formData.fileName = fileName
      if (file) {
        // 最好重新针对字段手动校验一次
        this.$refs.demoForm.validateField(['fileId'])
      }
    },

    /* 表单文件变更 */
    formUploadFileAChange ({ file }) {
      const { fileId = '', fileName = '' } = file || {}
      this.formData.fileIdA = fileId
      this.formData.fileNameA = fileName
      if (file) {
        // 最好重新针对字段手动校验一次
        this.$refs.demoForm.validateField(['fileIdA'])
      }
    },

    /* 提交 */
    async submit () {
      // '请检查必填项是否已填写！'
      const valid = await this.$refs.demoForm.validate().catch(() => this.__focus_error__(this.$t('cusEntry.supplement20250211.checkRequiredFieldsFilled')))

      if (!valid) {
        return
      }
      console.log(this.formData)
    },

    /* 表格多文件上传变更 */
    tableUploadFileListChange ({ fileList }, $index) {
      console.log({ fileList }, $index)
      this.tableData[$index].fileList = fileList || []
    },

    /* 表格文件变更 */
    tableUploadFileChange ({ file, $index }) {
      const { fileId = '', fileName = '' } = file || {}
      this.tableData[$index].fileId = fileId
      this.tableData[$index].fileName = fileName
    },

    /* 表格图片卡片文件变更 */
    tableUploadFileChange2 ({ file, $index }) {
      const { fileId = '', fileName = '' } = file || {}
      this.tableData[$index].fileId2 = fileId
      this.tableData[$index].fileName2 = fileName
    }
  }
}
</script>
