<template>
  <srm-dialog
    :visible.sync="dialogVisible"
    :title="$t('cusEntry.biddingSettings.viewBidFile')"
    size="middle"
    append-to-body
    :close-on-click-modal="false"
  >
    <el-table
      border
      max-height="200"
      :data="bidFileData"
    >
      <el-table-column
        align="center"
        type="index"
        :label="$t('common.sort')"
        width="50"
      />
      <!--附件名称-->
      <SrmCommonFileBid
        type="table-column"
        :table-column-options="{
          label: $t('bidMod.attachmentName'),
          prop: 'souDocId',
          nameProp: 'souFileName'
        }"
        readonly
      />
      <el-table-column
        v-if="baseInfo.mergeFlag"
        align="center"
        prop="extPackageName"
        :label="$t('cusEntry.biddingSettings.bagName')"
        minWidth="100"
      />
      <el-table-column
        align="center"
        prop="souRemark"
        :label="$t('common.remark')"
        minWidth="100"
      />
    </el-table>
    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">
        {{ $t('common.close') }}
      </el-button>
    </div>
  </srm-dialog>
</template>
<script>
/**
 * 供应商查看招标文件
 */
import { validatorBusinessType } from 'lib@/composition/origin/composition'
import SrmCommonFileBid from './srm-common-file'

export default {
  name: 'BidFileDialog',
  components: { SrmCommonFileBid },
  props: {
    // 业务类型
    businessType: {
      type: String,
      required: true,
      validator: value => validatorBusinessType(value)
    },
    visible: {
      type: Boolean,
      default: false
    },
    // 单据基础信息 { id, idKey }
    baseInfo: {
      type: Object,
      required: true,
      default: () => {
        return {
          id: '',
          idKey: ''
        }
      }
    },
    // 只读
    readonly: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      bidFileData: [],
      isDeadline: false
    }
  },
  computed: {
    dialogVisible: {
      get: function () {
        return this.visible
      },
      set: function (val) {
        this.$emit('update:visible', val)
        if (!val) {
          this.$bus.$off('downLoadHandle')
        }
      }
    }
  },
  created () {
    this.getBidFile()
  },
  mounted () {
    this.$bus.$on('downLoadHandle', () => {
      this.$http({
        url: `/api-sou/ext/vendor/bid/updateBidFileDownloadTime?projectId=${this.baseInfo.id}`,
        method: 'GET',
        loading: true
      }).then(() => {})
    })
  },
  methods: {
    // 查看招标文件
    getBidFile () {
      this.$http({
        url: `/api-sou/ext/vendor/bid/getBidSouFileList?projectId=${this.baseInfo.id}`,
        method: 'GET',
        loading: true
      }).then(res => {
        if (res && res.data) {
          this.bidFileData = res.data.fileList
        }
      })
    }
  },
  beforeDestory () {
    this.$bus.$off('downLoadHandle')
  }
}
</script>
<style lang="scss" scoped>
</style>
