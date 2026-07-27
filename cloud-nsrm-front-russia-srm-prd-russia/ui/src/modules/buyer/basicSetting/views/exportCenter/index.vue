<!-- 导出中心 -->
<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-main>
      <form-wrapper
        :form-array="formArray"
        @getFormData="getFormData"
      />
      <easy-table
        ref="table"
        :selection="false"
        :methods="methods"
        :columns="columns"
        row-key="fileuploadId"
        table-name="export_center_table"
      />
    </el-main>
  </el-container>
</template>

<script>
import EasyTable from 'lib@/components/BaseTable/EasyTable'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { downloadWithParam } from 'lib@/utils/file'
import { exportCenter } from 'modb@/basicSetting/api/basicSetting'

export default {
  name: 'ExportCenter',
  components: { EasyTable, FormWrapper },
  data () {
    return {
      queryParams: {},
      formArray: [
        {
          prop: 'fileFunction',
          label: () => this.$t('dataConfMod.menuName') // 菜单名称
        },
        {
          prop: 'creationDateBegin',
          label: () => this.$t('dataConfMod.creationDateBegin'), // 创建时间从
          type: 'datetime'
        },
        {
          prop: 'creationDateEnd',
          label: () => this.$t('dataConfMod.creationDateEnd'), // 创建时间至
          type: 'datetime'
        },
        {
          prop: 'fileSourceName',
          label: () => this.$t('dataConfMod.fileSourceName') // 文件名称
        }
      ],
      methods: {
        listPage: async params => {
          const res = await exportCenter.listExportPage(params)
          return res
        }
      },
      columns: [
        {
          attrs: {
            label: () => this.$t('dataConfMod.menuName'), // 菜单名称
            prop: 'fileFunction',
            sortable: true
          }
        },
        {
          attrs: {
            label: () => this.$t('dataConfMod.fileSourceName'), // 文件名称
            prop: 'fileSourceName',
            sortable: true,
            width: 140
          }
        },
        {
          attrs: {
            label: () => this.$t('dataConfMod.fileSize'), // 文件大小
            prop: 'fileSize',
            sortable: true,
            width: 110
          }
        },
        {
          attrs: {
            label: () => this.$t('dataConfMod.fileExportType'), // 文件类型
            prop: 'fileType',
            sortable: true,
            width: 120,
            formatter: value => value && value === 'IMPORT_FILE' ? this.$t('components.eio.importFile') : this.$t('components.eio.exportFile')
          }
        },
        {
          attrs: {
            label: () => this.$t('common.status'), // 状态
            prop: 'comment',
            sortable: true
          }
        },
        {
          attrs: {
            label: () => this.$t('common.creationTime'), // 创建时间
            prop: 'creationDate',
            sortable: true
          }
        },
        {
          attrs: {
            label: () => this.$t('dataConfMod.endTime'), // 结束时间
            prop: 'lastUpdateDate',
            sortable: true
          }
        },
        {
          attrs: {
            prop: 'operation',
            label: () => this.$t('common.operation'), // 操作
            width: 150,
            fixed: 'right'
          },
          operations: [
            {
              event: 'downloadFile',
              name: this.$t('common.download'), // 下载
              show: ({ row }) => {
                if (row.fileType === 'IMPORT_FILE') {
                  return row.comment && (row.comment.indexOf(this.$t('components.import.importValiteError')) > -1)
                } else if (row.fileType === 'EXPORT_FILE') {
                  return row.comment && (row.comment.indexOf('完成') > -1)
                }
                return false
              },
              func: this.downloadFile
            }
          ]
        }
      ]
    }
  },
  methods: {
    getFormData (params) {
      this.queryParams = params ? { ...params } : {}
      this.$refs.table.search(this.queryParams)
    },
    downloadFile (scope) {
      if (scope.row.fileuploadId) {
        downloadWithParam(
          scope.row.fileuploadId,
          scope.row.fileSourceName
        ).catch(() => {
          this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
        })
      } else {
        throw new Error('FileuploadId is invaild.')
      }
    }
  }
}
</script>
