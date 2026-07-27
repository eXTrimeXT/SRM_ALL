<template>
  <el-container
    class="flex-container-notab the_inventory_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="queryForm"
        @getFormData="getQuerydata"
      />

      <MainHeader>
        <template slot="left">
          <AuthorityButton
            type="primary"
            code="sup:accessFlowSetting:addNew"
            @click="addNew"
          >
            {{ $t('common.add') }}
          </AuthorityButton>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :checkbox="true"
        :pre-query-data="queryParam"
        url="/api-base/base/statusConfig/listPage"
      />
      <!-- 弹框区域-->
      <!-- 详情 -->
      <srm-dialog
        :title="$t('dataConfMod.detail')"
        :visible.sync="dialogFormVisible"
        :close-on-click-modal="false"
        size="large"
      >
        <div class="statusConfigEventList">
          <el-form
            ref="form"
            :model="form"
            :rules="rules"
          >
            <srm-row :gutter="32">
              <!-- 模块名称 -->
              <srm-col :span="8">
                <el-form-item
                  prop="moudleName"
                  :label="$t('statusConfig.moudleName')"
                >
                  <el-input v-model="form.moudleName" />
                </el-form-item>
              </srm-col>
              <srm-col :span="8">
                <el-form-item
                  prop="configCode"
                  :label="$t('statusConfig.configCode')"
                >
                  <el-input v-model="form.configCode" />
                </el-form-item>
              </srm-col>
              <srm-col :span="8">
                <el-form-item
                  prop="attrName"
                  :label="$t('statusConfig.attrName')"
                >
                  <el-input v-model="form.attrName" />
                </el-form-item>
              </srm-col>
              <srm-col :span="8">
                <el-form-item
                  prop="dictCode"
                  :label="$t('statusConfig.dictCode')"
                >
                  <QuickSearch
                    :show-input="form.dictCode"
                    show-key="dictCode"
                    :scope-data="form"
                    name="scc_base_dict"
                    @close-quicksearch="getDictObj"
                  />
                </el-form-item>
              </srm-col>
              <srm-col :span="8">
                <el-form-item
                  prop="dictName"
                  :label="$t('statusConfig.dictName')"
                >
                  <el-input v-model="form.dictName" disabled />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-form>
          <div class="mb10">
            <el-button
              type="primary"
              class="detail-pbtn"
              @click="addOneItem"
            >
              {{
                $t('common.add')
              }}
            </el-button>
          </div>
          <el-table
            :data="form.eventList"
            style="width: 100%"
            border
            height="250px"
            highlight-current-row
          >
            <el-table-column
              align="center"
              type="index"
              width="60"
            />
            <!-- 排序 -->
            <el-table-column
              align="center"
              prop="sortNum"
              :label="$t('statusConfig.sortNum')"
              min-width="150"
            >
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.sortNum"
                  clearable
                />
              </template>
            </el-table-column>
            <!-- 事件编码 -->
            <el-table-column
              align="center"
              prop="eventCode"
              :label="$t('statusConfig.eventCode')"
              min-width="150"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.eventCode"
                  clearable
                />
              </template>
            </el-table-column>
            <!-- 当前状态 -->
            <el-table-column
              align="center"
              prop="currentStatus"
              :label="$t('statusConfig.currentStatus')"
              min-width="150"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <DictSelect
                  v-model="scope.row.currentStatus"
                  :code="form.dictCode"
                />
              </template>
            </el-table-column>
            <!-- 目标状态 -->
            <el-table-column
              align="center"
              prop="targetStatus"
              :label="$t('statusConfig.targetStatus')"
              min-width="150"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <DictSelect
                  v-model="scope.row.targetStatus"
                  :code="form.dictCode"
                />
              </template>
            </el-table-column>
            <!-- 事件描述 -->
            <el-table-column
              align="center"
              prop="eventName"
              :label="$t('statusConfig.eventName')"
              min-width="150"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.eventName"
                  clearable
                />
              </template>
            </el-table-column>
            <!-- 操作 -->
            <el-table-column
              :label="$t('common.operation')"
              width="60"
            >
              <template slot-scope="scope">
                <!-- 删除 -->
                <el-button
                  type="text"
                  @click="deleteOneContent(scope.$index, scope.row)"
                >
                  {{
                    $t('common.delete')
                  }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <template
          #footer
          class="dialog-footer"
        >
          <el-button @click="dialogFormVisible = false">
            <!-- 取 消 -->
            {{ $t('common.cancel') }}
          </el-button>
          <el-button
            type="primary"
            @click="saveOrUpdate"
          >
            {{ $t('common.submit') }}
          </el-button>
        </template>
      </srm-dialog>
      <!-- 状态场景配置 -->
      <srm-dialog
        :title="$t('statusConfig.statusCondition')"
        :visible.sync="dialogFormVisible2"
        :close-on-click-modal="false"
        size="large"
      >
        <div class="statusConfigConditionList">
          <div class="mb10">
            <el-button
              type="primary"
              class="detail-pbtn"
              @click="addOneItemCondition"
            >
              {{
                $t('common.add')
              }}
            </el-button>
          </div>
          <el-table
            :data="form.conditionList"
            style="width: 100%"
            border
            height="250px"
            highlight-current-row
          >
            <el-table-column
              align="center"
              type="index"
              width="60"
            />
            <!-- 场景编码 -->
            <el-table-column
              align="center"
              prop="conditionCode"
              :label="$t('statusConfig.conditionCode')"
              min-width="150"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.conditionCode"
                  clearable
                />
              </template>
            </el-table-column>
            <!-- 描述 -->
            <el-table-column
              align="center"
              prop="conditionName"
              :label="$t('statusConfig.conditionName')"
              min-width="150"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.conditionName"
                  clearable
                />
              </template>
            </el-table-column>
            <!-- 规则逻辑 -->
            <el-table-column
              align="center"
              prop="regularCodes"
              :label="$t('statusConfig.regularCodes')"
              min-width="150"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <DictSelect
                  v-model="scope.row.regularShowList"
                  :code="form.dictCode"
                  multiple
                />
              </template>
            </el-table-column>
            <!-- 操作 -->
            <el-table-column
              :label="$t('common.operation')"
              width="60"
            >
              <template slot-scope="scope">
                <!-- 删除 -->
                <el-button
                  type="text"
                  @click="deleteOneContentCondition(scope.$index, scope.row)"
                >
                  {{
                    $t('common.delete')
                  }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <template
          #footer
          class="dialog-footer"
        >
          <el-button
            type="primary"
            @click="saveOrUpdateCondition"
          >
            {{ $t('common.submit') }}
          </el-button>
        </template>
      </srm-dialog>
    </el-main>
  </el-container>
</template>

<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import CCategorySelect from 'lib@/components/c-category-select'
import MImport from 'lib@/components/import'
import { getDictItemList } from '@/api/common'
import { adaptDictData } from '@/utils'
import QuickSearch from '@/library/components/QuickSearch'
import { statusSetting } from 'modb@/basicSetting/api/basicSetting'

export default {
  name: 'StatusConfig',

  components: {
    QuickSearch,
    TableView,
    MainHeader,
    FormWrapper,
    MImport,
    CCategorySelect
  },

  data () {
    return {
      form: {
        moudleName: '',
        eventList: [],
        conditionList: []
      },
      rules: {
        moudleName: [{ required: true, message: this.$t('common.pleaseInput') }],
        configCode: [{ required: true, message: this.$t('common.pleaseInput') }],
        attrName: [{ required: true, message: this.$t('common.pleaseInput') }],
        dictCode: [{ required: true, message: this.$t('common.pleaseInput') }]
      },
      pageNum: 1,
      totalPages: 0,
      gridId: 'tableViewList',
      tableHeader: [],
      tableData: [],
      quaReviewType: [],
      queryForm: [],
      dialogFormVisible: false,
      dialogFormVisible2: false,
      queryParam: {}
    }
  },

  computed: {},

  created () {
    this.queryForm = [
      {
        prop: 'moudleName',
        label: () => this.$t('statusConfig.moudleName') // 模块
      },
      {
        prop: 'configCode',
        label: () => this.$t('statusConfig.configCode') // 配置编码
      }
    ]
    this.tableHeader = [
      {
        prop: 'moudleName',
        label: () => this.$t('statusConfig.moudleName'), // 模板
        width: '150'
      },
      {
        prop: 'configCode',
        label: () => this.$t('statusConfig.configCode'), // '配置编码'
        minWidth: '150'
      },
      {
        prop: 'attrName',
        label: () => this.$t('statusConfig.attrName'), // '字段名'
        minWidth: '60'
      },
      {
        prop: 'dictName',
        label: () => this.$t('statusConfig.dictName'), // '字典名称'
        minWidth: '120'
      },
      {
        prop: 'operation',
        label: () => this.$t('common.operation'), // '操作'
        width: 200,
        fixed: 'right',
        showType: 'buttons',
        buttons: [
          // 编辑
          {
            callback: (row, scope) => this.showDetail(row),
            formattor: () => this.$t('common.edit')
          },
          // 状态场景配置
          {
            callback: (row, scope) => this.showStatusConditionDetail(row),
            formattor: () => this.$t('statusConfig.statusCondition')
          },
          // 删除
          {
            callback: (row, scope) => this.delRowData(row, scope),
            formattor: () => this.$t('common.delete')
          }
        ]
      }
    ]
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },

  mounted () {
    this.fatchDictData()
  },

  methods: {
    getDictObj (val, scope) {
      this.form.dictCode = val ? val.dictCode : ''
      this.form.dictName = val ? val.dictName : ''
    },
    // 状态场景配置
    showStatusConditionDetail (row) {
      // row.statusConfigId
      statusSetting.getStatusConfigDetail({ id: row.statusConfigId }).then(res => {
        this.form = {
          ...res.data,
          conditionList: res.data.conditionList.map(item => {
            return {
              ...item,
              regularShowList: item.regularCodes.split(',')
            }
          })
        }
      })
      this.dialogFormVisible2 = true
    },
    showDetail (row) {
      // row.statusConfigId
      statusSetting.getStatusConfigDetail({ id: row.statusConfigId }).then(res => {
        this.form = res.data
      })
      this.dialogFormVisible = true
    },
    delRowData (row, scope) {
      let index = scope.$index
      if (!row.createdId) {
        this.$refs.tableViewList.deleteRow(index)
        return false
      }
      // 当前操将永久删除这条数据，确认删除这条数据？
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.$http({
            url: '/api-base/base/statusConfig/delete',
            method: 'GET',
            params: { id: row.statusConfigId },
            loading: true
          })
            .then(() => {
              this.$message.success(this.$t('common.success')) // 操作成功
              this.getQuerydata()
            })
        })
    },
    saveOrUpdate () {
      this.$refs.form.validate(result => {
        if (result) {
          // saveOrUpdateStatusConfig
          statusSetting.saveOrUpdateStatusConfig(this.form).then(res => {
            this.dialogFormVisible = false
            this.$message.success(res.message)
            this.getQuerydata()
          })
        }
      })
    },
    saveOrUpdateCondition () {
      // saveOrUpdateStatusConfig
      this.form.conditionList.forEach(item => { item.regularCodes = item.regularShowList.join(',') })
      statusSetting.saveOrUpdateStatusConfigCondition(this.form).then(res => {
        this.dialogFormVisible2 = false
        this.$message.success(res.message)
      })
    },
    deleteOneContent (index, row) {
      this.form.eventList.splice(index, 1)
    },
    deleteOneContentCondition (index, row) {
      this.form.conditionList.splice(index, 1)
    },
    // 新增明细行
    addOneItem () {
      if (undefined === this.form.dictCode || this.form.dictCode === '') {
        this.$message.error(this.$t('dataConfMod.selectDictCode'))
        return
      }
      this.form.eventList.push({
        eventCode: null,
        currentStatus: null,
        targetStatus: null,
        eventName: null
      })
    },
    addOneItemCondition () {
      this.form.conditionList.push({
        conditionCode: null,
        conditionName: null,
        regularCodes: null
      })
    },
    addNew () {
      this.form = {
        eventList: [],
        conditionList: []
      }
      this.dialogFormVisible = true
    },
    // 获取数据字典
    fatchDictData () {
      let dictParamsArr = [
        { dictCode: 'QUA_REVIEW_TYPE' }, // 供方准入类型
        { dictCode: 'ACCESS_PROCESS_TYPE' }, // 准入流程
        { dictCode: 'MATERIAL_TRIAL' }, //
        { dictCode: 'ENTRY_FILE_TYPE' } // 准入场景类型
      ]
      getDictItemList(dictParamsArr).then(res => {
        const [QUA_REVIEW_TYPE, ACCESS_PROCESS_TYPE, MATERIAL_TRIAL, ENTRY_FILE_TYPE] = res.data
        this.quaReviewType = adaptDictData(QUA_REVIEW_TYPE.QUA_REVIEW_TYPE)
      })
    },
    getQuerydata (v) {
      this.queryParam = Object.assign({}, v)
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    }
  }
}
</script>

<style scoped lang="scss">
.mb10 {
  margin-bottom: 5px;
}
</style>
