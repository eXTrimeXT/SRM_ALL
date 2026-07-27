<template>
  <span>
    <el-button
      type="primary"
      @click="showDialog"
    >测试异步导出</el-button>
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
                placeholder="请选择"
              >
                <el-option
                  v-for="item in [{label: '导入', value: 'IMPORT'}, {label: '导出', value: 'EXPORT'}]"
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
      default: '查看文件处理进度'
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
          label: '执行类型'
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
      { label: '文件名称', width: 250, showType: 'slot', slot: 'fileName' },
      // 执行类型
      {
        prop: 'executeType',
        label: '执行类型',
        width: 120,
        formattor (val) {
          switch (val) {
          case 'IMPORT':
            return '导入'
          case 'EXPORT':
            return '导出'
          default:
            return ''
          }
        }
      },
      // 执行状态
      {
        prop: 'executeStatus',
        label: '执行状态',
        width: 120,
        formattor (val) {
          switch (val) {
          case 'EXECUTING':
            return '执行中'
          case 'SUCCESS':
            return '成功'
          case 'ERROR':
            return '失败'
          default:
            return ''
          }
        }
      },
      // 开始时间
      { prop: 'creationDate', label: '开始时间', width: 120 },
      // 结束时间
      { prop: 'lastUpdateDate', label: '结束时间', width: 120 },
      // 备注
      { prop: 'message', label: '备注', width: 120 }
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
