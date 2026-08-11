<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <el-table
        border
        :data="bidNoticeList"
        max-height="250"
      >
        <el-table-column
          type="index"
          width="50"
          align="center"
        />
        <!-- 所属单位 -->
        <el-table-column
          align="center"
          :label="$t('cusEntry.competition.belongCompany')"
          prop="affiliatedUnit"
          min-width="120"
          show-overflow-tooltip
        />
        <!--备注-->
        <el-table-column
          align="center"
          :label="$t('cusEntry.competition.remark')"
          prop="winNoticeRemark"
          min-width="120"
          show-overflow-tooltip
        >
          <template slot-scope="scope">
            <el-input
              v-if="scope.row.winNoticeStatus !== 'SUBMITTED'"
              v-model="scope.row.winNoticeRemark"
            />
            <span v-else> {{ scope.row.winNoticeRemark }}</span>
          </template>
        </el-table-column>
        <!-- 审批状态 -->
        <el-table-column
          align="center"
          :label="$t('cusEntry.competition.approvalStatus')"
          prop="winNoticeStatus"
          min-width="120"
          show-overflow-tooltip
          :formatter="row => {
            return row.winNoticeStatus ? $getDictLabel('SOU_APPROVAL_STATUS', row.winNoticeStatus) : ''
          }"
        />
        <el-table-column
          :label="$t('cusEntry.competition.archivesName')"
          width="100"
        >
          <template slot-scope="scope">
            <el-button
              type="text"
              @click="downTemplateFile(scope.row)"
            >
              {{ $t('cusEntry.common.downFile') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-main>
  </el-container>
</template>

<script>
import { systemUrl } from '@/config/sysConfig'
export default {
  name: 'BidNotice',
  props: {
    bidNoticeList: {
      type: Array,
      default () {
        return []
      }
    }
  },
  data () {
    return {
      // 文件上传配置信息
      fileInfo: {
        fileModular: 'inq', // 文件所属模块 -》基础模块
        fileFunction: 'competition', // 文件所属功能
        fileType: 'images' // 文件所属类型
      }
    }
  },
  watch: {
    bidNoticeList: {
      immediate: true,
      deep: true,
      handler (newValue) {
        this.$emit('update:bidNoticeList', newValue)
      }
    }
  },
  methods: {
    /* 下载模板 */
    downTemplateFile (row) {
      const {
        affiliatedUnit,
        projectId
      } = row
      const xml = encodeURIComponent('database:中标通知.ureport.xml')
      const params = encodeURIComponent(`projectId=${projectId}&affiliatedUnit=${affiliatedUnit}`)
      const url = `${systemUrl}/#/pdfPrint?xml=${xml}&params=${params}`
      window.open(url)
    }
  }
}
</script>

<style lang="scss" scoped>

</style>
