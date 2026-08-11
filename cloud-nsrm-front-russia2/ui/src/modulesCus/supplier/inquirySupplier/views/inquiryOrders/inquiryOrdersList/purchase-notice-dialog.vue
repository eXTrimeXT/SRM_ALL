<template>
  <SrmDialog
    :title="$t('cusEntry.inq.purchaseNotice')"
    size="small"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <el-form :model="form">
      <SrmCol :initCol="2">
        <el-form-item :label="$t('cusEntry.inq.file')">
          <SrmCommonFile
            :extra-data="fileInfo"
            :default-file="{
              fileId: form.fileId,
              fileName: form.fileName
            }"
            :readonly="true"
          />
        </el-form-item>
      </SrmCol>
      <SrmCol :initCol="2">
        <el-checkbox v-model="form.checked">
          {{ $t('cusEntry.inq.isAgree') }}
        </el-checkbox>
      </SrmCol>
    </el-form>
    <div slot="footer">
      <el-button @click="dialogVisible = false">
        {{ $t('common.cancel') }}
      </el-button>
      <el-button
        type="primary"
        @click="confirm"
      >
        {{ $t('common.confirm') }}
      </el-button>
    </div>
  </SrmDialog>
</template>

<script>
import { sceneFileApi } from 'modb@/basicSetting/api/basicSetting'
export default {
  name: 'PurchaseNoticeDialog',
  props: {
    /* 弹窗显示、隐藏属性 */
    visible: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      form: {
        fileId: null,
        fileName: '',
        checked: false
      },
      fileInfo: {
        fileModular: 'inq', // 文件所属模块 -》审批流程
        fileFunction: 'quote', //
        fileType: 'images' // 文件所属类型
      }
    }
  },
  computed: {
    dialogVisible: {
      get () {
        return this.visible
      },
      set (newValue) {
        this.$emit('update:visible', newValue)
      }
    }
  },
  created () {
    /* 获取报价需知附件 */
    const params = {
      pageSize: 15,
      pageNum: 1,
      sceneCode: 'SCENE_DEFAULT',
      sceneModuleCode: 'SCENE_DEFAULT_QUOTE_NOTICE'
    }
    sceneFileApi.listPage(params).then(res => {
      const {
        attachmentSourceName,
        templateFileId
      } = res.data?.list?.[0]
      this.form.fileId = templateFileId
      this.form.fileName = attachmentSourceName
    })
  },
  methods: {
    /* 确认 */
    confirm () {
      if (!this.form.checked) {
        this.$message.warning(this.$t('cusEntry.tipMessage.agreePurchaseMsg'))
        return false
      }
      this.dialogVisible = false
      this.$emit('confirm')
    }
  }
}
</script>
