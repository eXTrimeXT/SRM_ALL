<template>
  <SrmDialog
    title="查看招标资料"
    size="middle"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <el-table
      :data="fileList"
      border
      max-height="250"
    >
      <el-table-column
        align="center"
        type="index"
        width="50"
      />
      <!--附件名称-->
      <SrmCommonFile
        type="table-column"
        :table-column-options="{
          label: $t('cusEntry.competition.fileName'),
          prop: 'souDocId',
          nameProp: 'souFileName'
        }"
        readonly
      />
      <!--备注-->
      <el-table-column
        align="center"
        prop="souRemark"
        :label="$t('common.remark')"
      />
    </el-table>
    <div slot="footer">
      <el-button
        type="primary"
        @click="dialogVisible = false"
      >
        {{ $t('common.close') }}
      </el-button>
    </div>
  </SrmDialog>
</template>

<script>
import { carVendorHttp } from 'modcs@/competitionSupplier/api'
export default {
  name: 'BidFileDialog',
  props: {
    /* 弹窗显隐藏 */
    visible: {
      type: Boolean,
      default: false
    },
    /* 项目行id */
    projectId: {
      type: Number,
      default: null
    }
  },
  data () {
    return {
      fileList: []
    }
  },
  computed: {
    /* 弹窗显隐控制 */
    dialogVisible: {
      get () {
        return this.visible
      },
      set (value) {
        this.$emit('update:visible', value)
      }
    }
  },
  watch: {
    projectId: {
      handler (newValue, oldValue) {
        if (newValue !== oldValue) {
          newValue && this.getBidFile(newValue)
        }
      }
    }
  },
  methods: {
    /* 招标资料查看 */
    getBidFile (projectId) {
      carVendorHttp.order.getBidFileByProjectId(projectId).then(res => {
        if (res.data) {
          this.fileList = res.data
        }
      })
    }
  }
}
</script>
