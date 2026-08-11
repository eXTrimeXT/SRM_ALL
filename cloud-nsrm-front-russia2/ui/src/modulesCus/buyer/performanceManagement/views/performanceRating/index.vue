<template>
  <el-container class="flex-container-notab the_currency_wrapper" direction="vertical">
    <el-main>
      <FormWrapper
        :form-array="queryForm"
        @synchronous-value="syncFilterParams"
        @getFormData="getQuerydata"
      />
      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <el-button type="primary" @click="addNew">
            {{ $t('common.add') }}
          </el-button>
          <!-- <MImport
            ref="import"
            style="display: inline-block; margin-left: 10px"
            :title="iModal.title"
            :up-load-url="iModal.upLoadUrl"
            :extra-data="extraData"
            @downloadTemplate="downloadTemplate"
            @handleSuccess="handleSuccess"
          /> -->
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableList"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :show-filter-bar="showFilterBar === 1"
        url="/api-pef/perfLevel/listPerfLevelPage"
      />
    </el-main>
    <!-- 新增 编辑弹框区域-->
    <el-dialog :title="dialogTitle" :visible.sync="dialogFormVisible" :close-on-click-modal="false">
      <el-form ref="ratingForm" :model="submitModel.submitform" :rules="submitModel.rules">
        <srm-row>
          <srm-col :initCol="2">
            <el-form-item :label="$t('perfMod.levelName')" prop="levelName">
              <el-input v-model="submitModel.submitform.levelName" />
            </el-form-item>
          </srm-col>
          <srm-col :initCol="2">
            <!-- 绩效得分>= -->
            <el-form-item :label="$t('cusEntry.supplement20250205.performanceScoreThreshold')" prop="scoreStart">
              <el-input v-model="submitModel.submitform.scoreStart" type="number" />
            </el-form-item>
          </srm-col>
          <srm-col :initCol="2">
            <!-- 绩效得分< -->
            <el-form-item :label="$t('cusEntry.supplement20250205.performanceScore')" prop="scoreEnd">
              <el-input v-model="submitModel.submitform.scoreEnd" type="number" />
            </el-form-item>
          </srm-col>
          <srm-col :initCol="2">
            <el-form-item :label="$t('perfMod.status')" prop="status">
              <DictSelect v-model="submitModel.submitform.status" code="GRAFE_STATUS" clearable />
            </el-form-item>
          </srm-col>
          <srm-col :initCol="1">
            <el-form-item :label="$t('perfMod.levelDescription')" prop="levelDescription">
              <el-input
                v-model="submitModel.submitform.levelDescription"
                type="textarea"
                :rows="2"
              />
            </el-form-item>
          </srm-col>
        </srm-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">
          {{ $t('common.cancel') }}
        </el-button>
        <el-button type="primary" @click="comfirmSave">
          {{ $t('common.confirm') }}
        </el-button>
      </div>
    </el-dialog>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import _omit from 'lodash/omit'
import OrganizationSelector from 'lib@/components/organization-selector'
import { performanceManagement } from 'modc@/buyer/performanceManagement/api/index'

export default {
  name: 'RateSetting',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    OrganizationSelector,
    MImport
  },
  data () {
    return {
      filterParams: {},
      extraData: {
        fileModular: 'perf',
        fileFunction: 'performanceRating',
        fileType: 'excel'
      },
      iModal: {
        title: this.$t('components.eio.importTitle'),
        upLoadUrl: '/api-pef/perfLevel/importExcelInsertLevel'
      },
      pageSize: 15,
      gridId: 'performanceRating',
      currentRow: null,
      showFilterBar: 1,
      queryParam: {},
      dialogFormVisible: false,
      queryForm: [], // 查询条件
      tableHeader: [], // 表格列数据
      tableList: [],
      tableTotal: 0, // 分页数据
      tableLoading: false,
      curOpt: 'add',
      dialogTitle: '',
      tableSelection: [],
      submitModel: {
        submitform: {
          levelName: '', // 等级名称
          scoreStart: '', // 开始分数
          scoreEnd: '', // 分数
          status: '', // 状态
          levelDescription: '',
          fullPathId: null,
        },
        rules: {
          levelName: [{ required: true, message: this.$t('perfMod.enterLavelName') }], // '请输入等级名称'
          scoreStart: [{ required: true, message: this.$t('perfMod.enterScore') }], // '请输入绩效得分'
          scoreEnd: [{ required: true, message: this.$t('perfMod.enterScore') }], // '请输入绩效得分'
          status: [{ required: true, message: this.$t('perfMod.selectLavelStatus') }], // '请选择等级状态'
          fullPathId: [{ required: true, message: this.$t('perfMod.selectOrg') }] // '请选择采购组织'
        }
      }
    }
  },
  created () {
    let _this = this
    this.queryForm = [
      {
        prop: 'levelName',
        label: () => _this.$t('perfMod.levelName') // '等级名称',
      },
      {
        prop: 'scoreStart',
        label: () => _this.$t('perfMod.score') // '综合绩效得分',
      },
      {
        prop: 'status',
        label: () => _this.$t('perfMod.status'), // '等级状态',
        type: 'dict', // 字典类型
        code: 'GRAFE_STATUS' // 字典code
      }
    ]
    this.tableHeader = [
      // {
      //   prop: 'organizationName',
      //   label: () => _this.$t('perfMod.orgName') // '等级名称',
      // },
      {
        prop: 'levelName',
        label: () => _this.$t('perfMod.levelName') // '等级名称',
      },
      {
        prop: 'levelDescription',
        label: () => _this.$t('perfMod.levelDescription') // '等级说明',
      },
      {
        prop: 'scoreStart',
        label: () => _this.$t('cusEntry.supplement20250205.performanceScoreThreshold') // '绩效得分>=',
      },
      {
        prop: 'scoreEnd',
        label: () => _this.$t('cusEntry.supplement20250205.performanceScore') // '绩效得分<',
      },
      {
        prop: 'creationDate',
        dataType: 'dateTime',
        label: () => _this.$t('common.creationTime') // '创建时间'
      },
      {
        prop: 'status',
        label: () => _this.$t('perfMod.status'), // '等级状态',
        dataType: 'dict', // 数据类型为字典
        code: 'GRAFE_STATUS' // 字典code
      },
      {
        label: () => _this.$t('common.operation'), // '操作',
        width: '160',
        fixed: 'right',
        editType: 'none',
        btnStyle: 'text',
        showType: 'buttons',
        buttons: [
          {
            callback: function (row) {
              this.editDetail(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.edit') // 编辑
            }
          },
          {
            callback: function (row) {
              this.delRowData(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.delete') // '删除'
            }
          },
          {
            callback: function (row) {
              this.handleRowData(row)
            }.bind(this),
            formattor (row) {
              return _this.$t('common.disable') // '禁用'
            },
            show: function (row) {
              if (row.status === 'Y') {
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: function (row) {
              this.handleRowData(row)
            }.bind(this),
            formattor (row) {
              return _this.$t('common.enable') // '启用'
            },
            show: function (row) {
              if (row.status === 'N') {
                return true
              } else {
                return false
              }
            }
          }
        ]
      }
    ]
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    syncFilterParams (values) {
      this.filterParams = values
    },
    downloadTemplate () {
      downloadFileLink(
        '/api-pef/perfLevel/exportPerfLevelModel',
        this.$t('perfMod.levelTemp') + '.xlsx', // 绩效等级模板
      ).catch(() => {
        this.$message.error(this.$t('perfMod.downLoadError')) // "下载失败"
      })
    },
    handleSuccess () {
      this.getQuerydata()
    },
    getQuerydata (v) {
      this.queryParam = v
      let params = v || {}
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    // 选择组织
    addOrgHandle (node, instanceId) {
      const { organizationCode, organizationName, organizationId } = node
      this.submitModel.submitform.organizationCode = organizationCode
      this.submitModel.submitform.organizationId = organizationId
      this.submitModel.submitform.organizationName = organizationName
    },
    addNew () {
      this.curOpt = 'add'
      this.controlHandle(this.curOpt)
    },
    // 新增、编辑
    controlHandle (type) {
      if (type === 'add') {
        // 新增
        this.dialogTitle = this.$t('perfMod.addPerfLavel') // '新增绩效等级'
        let formObj = this.submitModel.submitform
        Object.keys(formObj).forEach(key => (formObj[key] = ''))
      } else {
        // 修改
        this.dialogTitle = this.$t('perfMod.editPerfLavel') // '编辑绩效等级'
      }

      this.dialogFormVisible = true
    },
    // 选中
    handleSelectionChange (value) {
      this.tableSelection = value
    },
    // 确认保存
    comfirmSave () {
      this.$refs.ratingForm.validate(valid => {
        if (!valid) {
          this.$message({
            message: this.$t('perfMod.enterRequired'), // '请输入必填项',
            type: 'success'
          })
          return false
        } else {
          const scoreStart = parseFloat(this.submitModel.submitform.scoreStart)
          const scoreEnd = parseFloat(this.submitModel.submitform.scoreEnd)
          if (scoreStart > scoreEnd) {
            // 绩效评分区间配置有误请检查
            this.$message.error(this.$t('cusEntry.supplement20250205.performanceScoreRangeConfigurationError'))
            return false
          }
          this.saveOrUpdateHandle(this.curOpt)
        }
      })
    },
    // 新增编辑组织数据
    saveOrUpdateHandle (opt) {
      let submitData = this.submitModel.submitform
      if (opt === 'add') {
        // 新增
        performanceManagement.savePerfLevel(submitData).then(res => {
          if (res) {
            this.$message({
              message: res.message,
              type: 'success'
            })
            this.dialogFormVisible = false
            this.getQuerydata()
          }
        })
      } else {
        performanceManagement.updatePerfLevel(submitData).then(res => {
          if (res) {
            this.$message({
              message: res.message,
              type: 'success'
            })
            this.dialogFormVisible = false
            this.getQuerydata()
          }
        })
      }
    },
    editDetail (row) {
      this.curOpt = 'edit'
      let levelId = row.levelId
      performanceManagement.getPerfLevelById({ levelId }).then(res => {
        let formInfo = res.data
        this.submitModel.submitform = _omit(formInfo, [
          'creationDate',
          'lastUpdateDate',
          'lastUpdatedBy',
          'createdBy'
        ])

        this.controlHandle(this.curOpt)
      })
    },
    delRowData (row) {
      let levelId = row.levelId
      this.$confirm(this.$t('common.confirmDelete'), {
        // 当前操将永久删除此数据，确认删除此数据？
        confirmButtonText: this.$t('common.confirm'), // '确认',
        cancelButtonText: this.$t('common.cancel'), // '取消',
        type: 'warning'
      })
        .then(() => {
          performanceManagement.deletePerfLevel({ levelId }).then(res => {
            this.$message({
              message: res.message,
              type: 'success'
            })
            this.getQuerydata()
          })
        })
        .catch(() => {})
    },
    handleRowData (row) {
      let params = {}
      params.levelId = row.levelId
      let tips = ''
      if (row.status === 'Y') {
        // 启用状态
        params.status = 'N'
        tips = this.$t('perfMod.isDisabled') // '是否确认禁用当前指标？'
      } else {
        // 启用状态
        params.status = 'Y'
        tips = this.$t('perfMod.isEnabeled') // '是否确认启用当前指标？'
      }
      this.$confirm(tips, {
        confirmButtonText: this.$t('common.confirm'), // '确认',
        cancelButtonText: this.$t('common.cancel'), // '取消',
        type: 'warning'
      })
        .then(() => {
          performanceManagement.enablePerfLevel(params).then(res => {
            this.$message({
              message: res.message,
              type: 'success'
            })
            this.getQuerydata()
          })
        })
        .catch(() => {})
    }
  }
}
</script>
<style scoped lang="scss"></style>
