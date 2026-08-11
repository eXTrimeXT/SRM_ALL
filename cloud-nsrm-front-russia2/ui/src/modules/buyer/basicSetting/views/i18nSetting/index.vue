<template>
  <el-container
    class="flex-container-notab studyDemoPage"
    direction="vertical"
  >
    <el-main>
      <MainHeader>
        <template #left>
          <MImport
            ref="import"
            style="display: inline-block; margin-left: 10px; margin-right: 10px"
            :title="iModal.title"
            :up-load-url="iModal.upLoadUrl"
            :extra-data="extraData"
            @downloadTemplate="downloadTemplate"
            @handleSuccess="handleSuccess"
          />
          <ExportDirect
            style="display: inline-block;margin-right: 10px;"
            exprot-url="/api-base/base-anon/i18n/downloadI18nExcel"
            requst-type="POST"
            :btn-text="$t('common.export')"
            :filename="$t('dataConfMod.i18nPropFile')+'.xlsx'"
          />
        </template>
      </MainHeader>
    </el-main>
  </el-container>
</template>
<script>
import MainHeader from 'lib@/components/Table/MainHeader'
import MImport from 'lib@/components/import'
import ExportDirect from 'lib@/components/export-direct'
import { downloadFileLink, downloadWithParam } from 'lib@/utils/file'
export default {
  name: 'I18nSetting',
  components: { MainHeader, MImport, ExportDirect },
  data () {
    return {
      extraData: {},
      iModal: {
        // 导入多语言翻译文档
        title: this.$t('common.import'),
        upLoadUrl: '/api-base/base-anon/i18n/convertI18nProp'
      }
    }
  },
  methods: {
    downloadTemplate () {
      // 多语言导入模板.xlsx
      downloadFileLink(
        '/api-base/base-anon/i18n/downloadI18nExcel',
        this.$t('dataConfMod.i18nPropFile') + '.xlsx'
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
      })
    },
    handleSuccess (data) {
      let id = data.data
      if (id) {
        downloadWithParam(
          id,
          id + '.zip'
        ).catch(() => {
          this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
        })
      }
    },
    HandleUploadSuccess (file) {
      const { name } = file
      downloadFileLink(
        '/api-base/base-anon/i18n/downloadI18nExcel',
        name
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
      })
    }
  }
}
</script>
<style scoped lang="scss">
.studyDemoPage {
  .section {
    padding: 5px;
  }
  .formStyle {
    width: 400px;
  }
  .divRad {
    width: 100px;
    height: 50px;
    background: red;
  }
  .divBlue {
    background: blue;
  }
  .b {
    height: 200px;
    background: blue;
  }
  #a {
    height: 100px;
    background: blue;
  }
}
</style>
