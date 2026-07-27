<template>
  <span>
    <el-button
      type="primary"
      @click="showDialog"
    >
      <!-- 测试异步导出 -->
      {{ $t('cusEntry.library.testAsyncExport') }}
    </el-button>
    <el-dialog
      :title="title"
      :visible.sync="show"
      width="800px"
    >
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
          >
            <template #executeType="{scope}">
              <el-select
                v-model="scope.executeType"
                :placeholder="$t('common.pleaseSelect')"
              >
                <el-option
                  v-for="item in [{ label: $t('common.import'), value: 'IMPORT' }, { label: $t('common.export'), value: 'EXPORT' }]"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </template>
          </FormWrapper>
          <div style="height:350px">
            <TableView
              :ref="table.gridId"
              style="padding:0;height:100%;"
              :table-data="table.data"
              :table-header="table.header"
              :pre-query-data="table.queryParam"
              :checkbox="false"
              :row-index="true"
              row-key="asyncFileInfoId"
              url="/api-base/async-file/page"
            >
              <template #fileName="{scope}">
                <template v-if="scope.row.fileId">
                  <div class="download-link-wrap">
                    <SrmCommonFile
                      :default-file="{
                        fileId: scope.row.fileId,
                        fileName: scope.row.fileName
                      }"
                      :readonly="true"
                    />
                    <span v-if="scope.row.fileSize">
                      &nbsp;&nbsp;&nbsp;&nbsp;
                      {{ scope.row.fileSize + ' KB' }}
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
    </el-dialog>
  </span>
</template>

<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
export default {
  name: 'AsyncFileShowList',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  props: {
    // 弹窗标题
    title: {
      type: String,
      default: this.$t('cusEntry.library.viewFileProcess') // 查看文件处理进度
    },
    // 指定业务模块(必填。查看异步上传、下载文件时，只能看对应模块的，不应该看到所有)
    model: {
      type: String
    }
  },
  data () {
    return {
      show: false, // 显示弹窗
      preArr: [
        {
          prop: 'executeType',
          label: this.$t('components.eio.zhixingleixing')
        }
      ],
      table: {
        gridId: 'asyncFileList',
        data: [],
        header: [],
        queryParam: []
      }
    }
  },
  created () {
    this.table.header = [
      // 文件名(加下载路径)
      { label: this.$t('components.eio.fileName'), width: 250, showType: 'slot', slot: 'fileName' },
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
      { prop: 'creationDate', label: this.$t('common.startTime'), width: 120, dataType: 'dateTime' },
      // 结束时间
      { prop: 'lastUpdateDate', label: this.$t('common.endTime'), width: 120, dataType: 'dateTime' },
      // 备注
      { prop: 'message', label: this.$t('common.remark'), width: 120 }
    ]

    this.$nextTick(() => {
      this.getQuerydata()
    })
    let _this = this
    setInterval(function () {
      _this.getQuerydata({}, _this)
    }, 8000)
  },
  methods: {
    // 展示弹窗
    showDialog () {
      this.show = true
      this.getQuerydata()
      this.$nextTick(() => {
        this.getQuerydata()
      })
    },
    getQuerydata (v, _this) {
      if (!_this) { _this = this }
      let queryData = {}
      if (v) {
        for (let i in v) {
          if (v[i]) {
            queryData[i] = v[i]
          }
        }
      }
      queryData.model = _this.model
      _this.queryParam = queryData
      _this.$nextTick(() => {
        _this.$refs[_this.table.gridId].query()
      })
    }
  }
}
</script>
<style>
.the_expert_pro{
    height: 400px;
}
</style>
