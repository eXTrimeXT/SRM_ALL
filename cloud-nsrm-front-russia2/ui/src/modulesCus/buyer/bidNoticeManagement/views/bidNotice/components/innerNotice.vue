<template>
  <div class="file-wrapper">
    <el-table
      border
      stripe
      :data="tableData"
      :column="tableColumns"
    >
      <!-- 序号 -->
      <el-table-column
        type="index"
        width="60"
        :label="$t('common.sort')"
      />
      <!-- 板块 -->
      <el-table-column
        :label="$t('cusEntry.common.plate')"
        prop="extOrgBuName"
        showshowOverflowTooltip
      />
      <!-- 公司 -->
      <el-table-column
        :label="$t('components.organization.COMPANY')"
        prop="extOrgOuName"
        showshowOverflowTooltip
      />
      <!-- 需求部门 -->
      <el-table-column
        :label="$t('purchaseDemand.requirementDepartment')"
        prop="demandDepartmentName"
        showshowOverflowTooltip
      />
      <!-- 附件模板 -->
      <!-- <el-table-column
        :label="$t('dataConfMod.attachTemplate')"
      >
        <template v-slot="scope">
          <el-button type="text" @click="downloadTemplate(scope.row)">
            {{ $t('dataConfMod.attachTemplate') }}
          </el-button>
        </template>
      </el-table-column> -->

      <!--附件名称  '通知书附件'-->
      <SrmCommonFile
        type="table-column"
        :table-column-options="{
          align: 'left',
          label: $t('cusEntry.supplement20250121.noticeAttachmentId'),
          prop: 'attachmentId',
          nameProp: 'attachmentName',
          renderHeader:_addStarToColumn
        }"
        :validateOptions="{
          accept:['.pdf'],
          size: null
        }"
        :readonly="readonly"
        @on-change="filesChange"
      />
    </el-table>
  </div>
</template>
<script>
import { transformMQL } from 'lib@/utils/util'
import bidNoticeHttp from '../api'
import { downloadFileLink } from 'lib@/utils/file'

export default {
  props: {
    readonly: {
      type: Boolean,
      default: false
    },
    value: {
      type: Array,
      default: () => []
    },
    form: {
      type: Object,
      default: () => {}
    }
  },
  data () {
    return {
      tableColumns: []
    }
  },
  computed: {
    tableData: {
      get () {
        return this.value
      },
      set (val) {
        this.$emit('update:value', val)
      }
    },
    showSend () {
      return this.form.status === 'APPROVED'
    }
  },
  methods: {
    addRows () {
      this.tableData.push({
        souDocId: '',
        souFileName: ''
      })
    },
    downloadTemplate (row) {
      downloadFileLink(
        `/api-sou/sou/api/v1/bidNotice/downloadProjectNoticeInternalTemplate?bidNoticeInternalId=${row.internalId}`
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
      })
    },
    /* 内部查看文件变更 */
    filesChange ({ file, $index }) {
      const { fileId = '', fileName = '' } = file || {}
      this.tableData[$index].attachmentId = fileId
      this.tableData[$index].attachmentName = fileName
    },
    deleteRows (index) {
      this.tableData.splice(index, 1)
    },
    getParamsData () {
      return this.tableData
    },
    async sendNotice (row) {
      let transformParams = transformMQL.save('BidNoticeInternal', [{ ...row }], 'send')
      const response = await bidNoticeHttp.internalSendNotice(transformParams)
      if (response) {
        this.$message.success(this.$t('common.success'))
        this.$emit('send')
      }
    }
  }
}
</script>
<style lang="scss" scoped>
.btns {
  margin-bottom: 10px;
}
</style>
