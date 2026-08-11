<template>
  <!-- 这里弹窗查看异步上传、下载的进度 -->
  <el-container
    class="flex-container the_expert_pro"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        :col-length="2"
        form-label-width="200px"
        @getFormData="getQuerydata"
      />
      <div style="height: 350px">
        <TableView
          :ref="table.gridId"
          style="padding: 0; height: 100%"
          :table-data="table.data"
          :table-header="table.header"
          :pre-query-data="table.queryParam"
          :checkbox="false"
          :row-index="true"
          row-key="asyncFileInfoId"
          url="/api-base/async-file/page"
        >
          <template #fileName="{ scope }">
            <template v-if="scope.row.fileId">
              <div class="download-link-wrap">
                <SrmCommonFile
                  :default-file="{
                    fileId: scope.row.fileId,
                    fileName: scope.row.fileName
                  }"
                  :readonly="true"
                />
                <span
                  v-if="scope.row.fileSize"
                  style="color: gray"
                >
                  {{
                    scope.row.fileSize / 1024 > 1024
                      ? (scope.row.fileSize / 1024 / 1024).toFixed(2) + 'MB'
                      : (scope.row.fileSize / 1024).toFixed(2) + ' KB'
                  }}
                </span>
              </div>
            </template>
            <template v-else>
              <span>{{ scope.row.fileName }}</span>
            </template>
          </template>
        </TableView>
      </div>
    </el-main>
  </el-container>
</template>

<script>
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
export default {
  name: 'AsyncFileList',
  components: {
    TableView,
    FormWrapper
  },
  props: {
    // 指定业务模块(必填。查看异步上传、下载文件时，只能看对应模块的，不应该看到所有)
    model: {
      type: String
    }
  },
  data () {
    return {
      preArr: [
        {
          prop: 'executeType',
          label: this.$t('components.eio.zhixingleixing'),
          type: 'select',
          options: [
            { label: this.$t('common.import'), value: 'IMPORT' },
            { label: this.$t('common.export'), value: 'EXPORT' }
          ]
        }
      ],
      table: {
        gridId: 'asyncFileList',
        data: [],
        header: [],
        queryParam: {}
      },
      // 定时器
      timer: null
    }
  },
  created () {
    this.table.header = [
      // 文件名(加下载路径)
      { label: this.$t('components.eio.fileName'), width: 250, showType: 'slot', slot: 'fileName' },
      // 执行类型
      {
        prop: 'executeType',
        label: this.$t('components.eio.zhixingleixing'),
        width: 120,
        formattor (val) {
          switch (val) {
          case 'IMPORT':
            return this.$t('common.import')
          case 'EXPORT':
            return this.$t('common.export')
          default:
            return ''
          }
        }
      },
      // 执行状态
      {
        prop: 'executeStatus',
        label: this.$t('components.eio.zhixingStatus'),
        width: 120,
        formattor (val) {
          switch (val) {
          case 'EXECUTING':
            return this.$t('common.zhixing')
          case 'SUCCESS':
            return this.$t('dataConfMod.success')
          case 'ERROR':
            return this.$t('dataConfMod.fail')
          default:
            return ''
          }
        }
      },
      // 开始时间
      { prop: 'creationDate', label: this.$t('components.common.startTime'), width: 160, dataType: 'dateTime' },
      // 结束时间
      {
        prop: 'lastUpdateDate',
        label: this.$t('components.common.endTime'),
        width: 160,
        dataType: 'dateTime',
        formattor (val, row) {
          if (row.executeStatus === 'EXECUTING') {
            return ''
          } else {
            return val
          }
        }
      },
      // 备注
      { prop: 'message', label: this.$t('common.remark') }
    ]

    this.$nextTick(() => {
      this.getQuerydata()
    })
    this.timer = setInterval(() => {
      // 8秒刷新一次查询
      this.getQuerydata({})
    }, 8000)
  },
  beforeDestroy () {
    this.clearTimerInterval()
  },
  methods: {
    clearTimerInterval () {
      // 销毁定时器
      clearInterval(this.timer)
      this.timer = null
    },
    // 展示弹窗
    showDialog () {
      this.show = true
      this.$nextTick(() => {
        this.getQuerydata()
      })
    },
    getQuerydata (v) {
      let queryData = {}
      if (v) {
        for (let i in v) {
          if (v[i]) {
            queryData[i] = v[i]
          }
        }
      }
      queryData.model = this.model
      this.table.queryParam = queryData
      this.$nextTick(() => {
        if (this.$refs[this.table.gridId]) {
          this.$refs[this.table.gridId].query()
        }
      })
    }
  }
}
</script>

<style>
.the_expert_pro {
  height: 400px;
}
</style>
