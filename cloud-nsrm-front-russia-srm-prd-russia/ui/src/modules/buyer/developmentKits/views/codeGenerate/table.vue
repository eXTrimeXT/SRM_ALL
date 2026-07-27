<!-- 代码生成基础信息表格 -->
<template>
  <div>
    <el-table
      use-virtual
      :row-height="37"
      height="340px"
      :data="databaseFeilds"
      @row-click="rowClick"
    >
      <el-table-column
        align="center"
        label="字段"
      >
        <el-table-column
          align="center"
          show-overflow-tooltip
          prop="attr"
          label="字段"
          width="150"
        />
        <el-table-column
          align="center"
          show-overflow-tooltip
          prop="title"
          label="字段描述"
        />
        <el-table-column
          align="center"
          show-overflow-tooltip
          prop="typeName"
          label="数据类型"
        />
        <el-table-column
          align="center"
          show-overflow-tooltip
          prop="isMain"
          label="主键标识"
        >
          <template slot-scope="scope">
            <i
              v-if="scope.row.isMain === 'Y'"
              class="el-icon-circle-check green-circle-check"
            />
            <i
              v-else
              class="el-icon-circle-close red-circle-close"
            />
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          show-overflow-tooltip
          prop="nullAble"
          label="允许为空"
        >
          <template slot-scope="scope">
            <i
              v-if="scope.row.nullAble === 'Y'"
              class="el-icon-circle-check green-circle-check"
            />
            <i
              v-else
              class="el-icon-circle-close red-circle-close"
            />
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          show-overflow-tooltip
          prop="realTitle"
          label="界面展示名称"
        >
          <template slot-scope="scope">
            <el-input
              v-if="scope.row.isEditing"
              v-model.trim="scope.row.realTitle"
            />
            <span v-else>{{ scope.row.realTitle }}</span>
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          show-overflow-tooltip
          prop="realAttr"
          label="界面字段"
          width="150"
        >
          <template slot-scope="scope">
            <el-input
              v-if="scope.row.isEditing"
              v-model.trim="scope.row.realAttr"
            />
            <span v-else>{{ scope.row.realAttr }}</span>
          </template>
        </el-table-column>
        <el-table-column
          label="控件类型"
          prop="componentType"
          align="center"
          width="100"
        >
          <template #default="scope">
            <DictSelect
              v-if="scope.row.isEditing"
              v-model="scope.row.componentType"
              code="COMPONENT_TYPE"
              :dict-class="dictClass"
              clearable
              @input="componentChangeType(scope.row)"
            />
            <span v-else>{{ $getDictLabel('COMPONENT_TYPE', scope.row.componentType) }}</span>
          </template>
        </el-table-column>
      </el-table-column>
      <el-table-column
        align="center"
        label="列表"
      >
        <el-table-column
          align="center"
          show-overflow-tooltip
          prop="showTable"
          label="是否展示"
        >
          <template slot-scope="scope">
            <el-switch
              v-if="scope.row.isEditing"
              v-model="scope.row.showTable"
            />
            <i
              v-else-if="scope.row.showTable"
              class="el-icon-circle-check green-circle-check"
            />
            <i
              v-else
              class="el-icon-circle-close red-circle-close"
            />
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          show-overflow-tooltip
          prop="queryField"
          label="查询条件字段"
        >
          <template slot-scope="scope">
            <el-switch
              v-if="scope.row.isEditing"
              v-model="scope.row.queryField"
            />
            <i
              v-else-if="scope.row.queryField"
              class="el-icon-circle-check green-circle-check"
            />
            <i
              v-else
              class="el-icon-circle-close red-circle-close"
            />
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          show-overflow-tooltip
          prop="filterType"
          label="匹配方式"
          width="100"
        >
          <template #default="scope">
            <DictSelect
              v-if="scope.row.isEditing"
              v-model="scope.row.filterType"
              code="MATCH_QUERY_CONDITION"
              :dict-class="dictClass"
              clearable
            />
            <span v-else>{{ $getDictLabel('MATCH_QUERY_CONDITION', scope.row.filterType) }}</span>
          </template>
        </el-table-column>
      </el-table-column>
      <el-table-column
        align="center"
        label="表单"
      >
        <el-table-column
          align="center"
          show-overflow-tooltip
          prop="showForm"
          label="详情页展示字段"
        >
          <template slot-scope="scope">
            <el-switch
              v-if="scope.row.isEditing"
              v-model="scope.row.showForm"
            />
            <i
              v-else-if="scope.row.showForm"
              class="el-icon-circle-check green-circle-check"
            />
            <i
              v-else
              class="el-icon-circle-close red-circle-close"
            />
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          show-overflow-tooltip
          prop="division"
          label="分区"
        >
          <template slot-scope="scope">
            <el-input
              v-if="scope.row.isEditing"
              v-model.trim="scope.row.division"
            />
            <span v-else>{{ scope.row.division }}</span>
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          show-overflow-tooltip
          prop="dictCode"
          label="字典编码"
        >
          <template slot-scope="scope">
            <QuickSearch
              v-if="scope.row.isEditing"
              :show-input="scope.row.dictCode"
              show-key="dictCode"
              :scope-data="scope.row"
              name="scc_base_dict"
              @close-quicksearch="getObj"
            />
            <!-- <el-input
            v-if="scope.row.isEditing"
            v-model.trim="scope.row.dictCode"
            />-->
            <span v-else>{{ scope.row.dictCode }}</span>
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          show-overflow-tooltip
          prop="quickSearchCode"
          label="快速查询"
        >
          <template slot-scope="scope">
            <!-- <el-input v-if="scope.row.isEditing" clearable suffix-icon="el-icon-search"
              @focus="openQueryDialog(scope.row)" v-model="scope.row.quickSearchCode">
            </el-input>-->
            <PopSearch
              v-if="scope.row.isEditing"
              v-model="scope.row.quickSearchCode"
              :input-disabled="true"
              :disabled="false"
              @popSearch="openQueryDialog(scope.row)"
            />
            <span v-else>{{ scope.row.quickSearchCode }}</span>
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          show-overflow-tooltip
          prop="required"
          label="是否必填"
        >
          <template slot-scope="scope">
            <el-switch
              v-if="scope.row.isEditing"
              v-model="scope.row.required"
            />
            <i
              v-else-if="scope.row.required"
              class="el-icon-circle-check green-circle-check"
            />
            <i
              v-else
              class="el-icon-circle-close red-circle-close"
            />
          </template>
        </el-table-column>
      </el-table-column>
    </el-table>

    <!-- 快速查询查询弹框 -->
    <srm-dialog
      title="快速查询"
      size="large"
      :visible.sync="visibleDialog"
      :destroy-on-close="true"
      append-to-body
      :close-on-click-modal="false"
    >
      <el-main>
        <FormWrapper
          :form-array="filterConfig"
          @getFormData="getQuerydata"
        />
        <el-table
          ref="gridId"
          v-loading="loading"
          style="width: 100%"
          height="300px"
          border
          highlight-current-row
          :data="tableData"
        >
          <el-table-column
            prop="formSource"
            label="来源"
            align="center"
            width="455"
          >
            <template slot-scope="scope">
              <span>{{ scope.row.fromSource }}</span>
            </template>
          </el-table-column>
          <el-table-column
            prop="toSource"
            label="目标源"
            align="center"
            width="455"
          >
            <template slot-scope="scope">
              <el-select
                v-model="scope.row.toSourceVal"
                placeholder="请选择"
                @change="handleQuickChange(scope.row.fromSource,scope.row.toSourceVal)"
              >
                <el-option
                  v-for="item in scope.row.toSource"
                  :key="item.key"
                  :label="item.value"
                  :value="item.value"
                />
              </el-select>
            </template>
          </el-table-column>
        </el-table>
      </el-main>
      <div
        slot="footer"
        class="dialog-footer"
      >
        <template>
          <el-button
            type="primary"
            @click="selectConfirm"
          >
            确定
          </el-button>
          <el-button @click="visibleDialog = false">
            取消
          </el-button>
        </template>
      </div>
    </srm-dialog>

    <!-- 控件类型附件弹框 -->
    <srm-dialog
      title="附件"
      size="large"
      :visible.sync="visibleUploadDialog"
      :destroy-on-close="true"
      append-to-body
      :close-on-click-modal="false"
    >
      <div class="upload-tip">
        来源为上传附件后的回调信息，目标源为业务表需要存储的字段;id和name要选对.
      </div>
      <el-main>
        <el-table
          ref="gridId"
          v-loading="loading"
          style="width: 100%"
          height="300px"
          border
          highlight-current-row
          :data="fileUploadData"
        >
          <el-table-column
            prop="fileFromSource"
            label="来源"
            align="center"
            width="455"
          >
            <template slot-scope="scope">
              <span>{{ scope.row.fileFromSource }}</span>
            </template>
          </el-table-column>
          <el-table-column
            prop="fileToSource"
            label="目标源"
            align="center"
            width="455"
          >
            <template slot-scope="scope">
              <el-select
                v-model="scope.row.fileToSourceVal"
                placeholder="请选择"
                @change="handleUploadRowChange(scope.row.fileFromSource,scope.row.fileToSourceVal)"
              >
                <el-option
                  v-for="item in scope.row.fileToSource"
                  :key="item.key"
                  :label="item.value"
                  :value="item.value"
                />
              </el-select>
            </template>
          </el-table-column>
        </el-table>
      </el-main>
      <div
        slot="footer"
        class="dialog-footer"
      >
        <template>
          <el-button
            type="primary"
            @click="fileSelectConfirm"
          >
            确定
          </el-button>
          <el-button @click="visibleUploadDialog = false">
            取消
          </el-button>
        </template>
      </div>
    </srm-dialog>
  </div>
</template>

<script>
import QuickSearch from 'lib@/components/QuickSearch'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import PopSearch from './pop-search'

import { createDictClass } from '@/library/utils/dict/dict-utils'
const dictClass = createDictClass(
  {
    MATCH_QUERY_CONDITION: [
      { id: 1, label: '模糊匹配', value: '1' },
      { id: 0, label: '精确匹配', value: '0' }
    ],
    COMPONENT_TYPE: [
      { id: 0, label: '时间', value: 'date' },
      { id: 1, label: '附件', value: 'fileUpload' }
    ]
  },
  false
)

export default {
  name: 'DatabaseTable',
  components: {
    QuickSearch,
    FormWrapper,
    PopSearch
  },
  props: {
    databaseFeilds: {
      required: true,
      type: Array
    }
  },
  data () {
    return {
      dictClass: dictClass,
      visibleDialog: false,
      visibleUploadDialog: false,
      queryData: {},
      filterConfig: [
        {
          prop: 'name',
          label: '快查code',
          type: 'quicksearch',
          showKey: 'name',
          name: 'scc_base_quicksearch_config'
        }
      ],
      toSourceValueList: [],
      tableData: [],
      rowSelectData: [],
      bottomRowData: {},
      loading: false,
      fileUploadData: [], // 控件类型-附件数据
      uploadRowsSelect: [] // 控件类型-附件，目标源选择
    }
  },
  computed: {},
  watch: {},
  created () {},
  mounted () {},
  methods: {
    // 控件类型-附件确定方法
    fileSelectConfirm () {
      for (let i = 0; i < this.fileUploadData.length; i++) {
        if (
          this.fileUploadData[i].fileFromSource === 'name' &&
          !this.fileUploadData[i].fileToSourceVal
        ) {
          this.$message({
            type: 'warning',
            message: 'id和name必选！'
          })
          return
        }
      }

      let rowQuick = {}
      this.uploadRowsSelect.forEach((item) => {
        rowQuick[item.fileFromSource] = this.toHump(item.fileToSourceVal) || ''
      })
      Object.assign(this.bottomRowData, {
        fileUploadMap: rowQuick
      })
      this.visibleUploadDialog = false
    },
    fileUploadInit (componentRow) {
      this.bottomRowData = componentRow
      this.fileUploadData = []
      let i = 0
      let fromSource = [
        'id',
        'name',
        'fileExtendType',
        'fileFullname',
        'fileFunction',
        'fileModular',
        'filePath',
        'filePureName',
        'fileSize',
        'fileType',
        'fingerprint'
      ]
      let toSourceValueList = this.databaseFeilds.map((item) => {
        i++
        return { key: i, value: item.attr }
      })
      fromSource.forEach((row) => {
        let rowObj = {
          fileFromSource: row,
          fileToSource: toSourceValueList
        }
        if (row === 'id') {
          rowObj.fileToSourceVal = componentRow.attr
        }
        this.fileUploadData.push(rowObj)
      })
    },
    componentChangeType (row) {
      // 控件类型为附件则打开弹窗
      if (row.componentType === 'fileUpload') {
        this.visibleUploadDialog = true
        this.fileUploadInit(row)
      }
    },
    // 附件下拉值选择
    handleUploadRowChange (fileFromSource, fileToSourceVal) {
      this.uploadRowsSelect = this.fileUploadData.filter((item) => item.fileToSourceVal)
    },
    rowClick (row, column, event) {
      this.$emit('rowClick', row, column, event)
    },
    getObj (val, scope) {
      scope.dictCode = val ? val.dictCode : ''
    },
    // 打开快查弹窗
    openQueryDialog (row) {
      this.visibleDialog = true
      this.tableData = []
      this.bottomRowData = row
    },
    handleSelectRowChange (rows) {
      this.rowSelectData = rows
    },
    // 大写加下划线转驼峰
    toHump (name) {
      // eslint-disable-next-line no-useless-escape
      return name.toLowerCase().replace(/\_(\w)/g, (all, letter) => {
        return letter.toUpperCase()
      })
    },
    // 快查点击确定
    selectConfirm () {
      let rowQuick = {}
      this.rowSelectData.forEach((item) => {
        rowQuick[this.toHump(item.fromSource)] = this.toHump(item.toSourceVal) || ''
      })
      Object.assign(this.bottomRowData, {
        quickSearchCode: this.queryData.name,
        quickMap: rowQuick
      })
      this.visibleDialog = false
    },
    handleQuickChange (fromSource, toSourceVal) {
      this.rowSelectData = this.tableData.filter((item) => item.toSourceVal)
    },
    // 快速查询弹窗--查询数据
    getQuerydata (params) {
      if (!params.name) {
        this.$message({
          type: 'warning',
          message: '请先选择快查code！'
        })
        return
      }
      this.queryData = params
      this.loading = true
      this.$http({
        url: '/api-base/quicksearch/quicksearchConfig/getDetail',
        method: 'POST',
        data: params
      }).then((r) => {
        this.toSourceValueList = this.databaseFeilds.map((item, i) => {
          return {
            key: i++,
            value: item.attr
          }
        })
        this.toSourceValueList[0] = {
          key: 0,
          value: ''
        }
        let toMapSourceVals = this.toSourceValueList.map((item) => item.value)
        let attrConfigs = r.data.attrConfigs.map((item) => {
          let toSourceVal = ''
          if (toMapSourceVals.indexOf(item.attr) !== -1) {
            toSourceVal = item.attr || ''
          }
          return {
            fromSource: item.attr,
            toSource: this.toSourceValueList,
            toSourceVal
          }
        })
        this.tableData = attrConfigs
        this.loading = false
      })
    }
  }
}
</script>
<style scoped lang="scss">
.green-circle-check {
  color: #0af10a;
  font-size: 14px;
}

.red-circle-close {
  color: red;
  font-size: 14px;
}

.upload-tip {
  color: red;
  font-size: 12px;
  margin-bottom: 12px;
}
</style>
