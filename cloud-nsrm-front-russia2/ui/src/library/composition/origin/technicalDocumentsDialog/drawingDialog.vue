<template>
  <SrmDialog
    :title="$t('bidMod.technicalDocuments.drawingDialogTitlle')"
    size="large"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    :content-max-height-limit="false"
    append-to-body
  >
    <FormWrapper
      :form-array="formWrapperArray"
      form-label-width="120px"
      @getFormData="getQueryData"
    />

    <TableView
      ref="list"
      checkbox
      :table-data="tableData"
      :table-header="tableHeader"
      :pre-query-data="queryParam"
      :check-change="checkChange"
      :source="drawingsHeadApi.list"
      style="height: 300px"
    >
      <template #attachFile="{ scope }">
        <SrmCommonFile
          :default-file="{
            fileId: scope.row.fileuploadId,
            fileName: scope.row.attachName + '.' + scope.row.attachType
          }"
          readonly
        />
      </template>
    </TableView>

    <div slot="footer" class="dialog-footer">
      <!--取消-->
      <el-button @click="dialogVisible = false">
        {{ $t('common.cancel') }}
      </el-button>
      <!--确定-->
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
/**
 * 物料附件查询
 */
import FormWrapper from 'lib@/components/Table/FormWrapper'
import TableView from 'lib@/components/Table/TableView'
import { drawingsHeadApi } from 'modb@/basicSetting/api/basicSetting'

export default {
  name: 'DrawingDialog',

  components: {
    FormWrapper,
    TableView
  },

  props: {
    visible: {
      type: Boolean,
      required: true
    },
    params: {
      type: Object,
      default: () => { /* nothing */ }
    }
  },

  data () {
    return {
      drawingsHeadApi: drawingsHeadApi,
      formWrapperArray: [
        // 附件名称
        {
          prop: 'attachName',
          label: this.$t('bidMod.attachmentName')
        }
      ],
      tableData: [],
      tableHeader: [
        // 附件名称
        {
          prop: 'attachName',
          label: this.$t('bidMod.attachmentName'),
          minWidth: 120
        },
        // 附件
        {
          prop: 'attachFile',
          label: this.$t('bidMod.attachment'),
          minWidth: 150,
          showType: 'slot',
          slot: 'attachFile'
        },
        // 附件地址
        {
          prop: 'fileuploadAddress',
          label: this.$t('drawingshead.drawingAddress'),
          minWidth: 120
        },
        // 附件类型
        {
          prop: 'drawingsType',
          label: this.$t('dataConfMod.attachmentType'),
          minWidth: 120,
          dataType: 'dict',
          code: 'DRAWING_TYPE'
        },
        // 版本号
        {
          prop: 'drawingsVersion',
          label: this.$t('drawingshead.drawingVersion'),
          minWidth: 120
        }
      ],
      queryParam: {
        // 物料code
        materialCode: (this.params || {}).materialCode,
        // 有效
        drawingsStatus: 'Y'
      },
      selectRow: []
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

  mounted () {
    this.$nextTick(() => {
      this.getQueryData()
    })
  },

  methods: {
    getQueryData (val) {
      this.queryParam = {
        ...this.queryParam,
        ...(val || {})
      }

      this.$nextTick(() => {
        this.$refs.list.query()
      })
    },

    /* 选择 */
    checkChange (val) {
      this.selectRow = val || []
    },

    /* 确定 */
    confirm () {
      if (this.selectRow.length === 0) {
        this.$message.warning(this.$t('common.pleaseSelectMinOne'))
        return
      }

      this.dialogVisible = false
      this.$emit('confirm', this.selectRow.map(item => {
        // 只取部分字段
        return {
          attachName: item.attachName,
          fileName: item.attachName + '.' + item.attachType,
          fileuploadId: item.fileuploadId,
          fileuploadAddress: item.fileuploadAddress,
          drawingsType: item.drawingsType
        }
      }))
    }
  }
}
</script>
