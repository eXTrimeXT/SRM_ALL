<template>
  <el-container
    direction="vertical"
  >
    <el-main>
      <div class="form-container">
        <el-form
          ref="funForm"
          :model="form"
          class="form-incontainer"
          :rules="rules"
          label-position="top"
        >
          <el-row :gutter="32">
            <el-col :span="6">
              <!-- 功能编码 -->
              <el-form-item :label="$t('dataConfMod.functionCode')" prop="functionCode">
                <el-input v-model="form.functionCode" :disabled="curOpt === 'edit'" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <!-- 功能名称 -->
              <el-form-item :label="$t('dataConfMod.functionName')" prop="functionName">
                <el-input v-model="form.functionName" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <!-- 功能类型 -->
              <el-form-item :label="$t('dataConfMod.functionType')" prop="functionType">
                <el-select v-model="form.functionType">
                  <!-- 通用 -->
                  <el-option value="normal" :label="$t('dataConfMod.normal')" />
                  <!-- 报表 -->
                  <el-option value="report" :label="$t('dataConfMod.report')" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col v-if="form.functionType === 'report'" :span="6">
              <!-- 报表配置编码 -->
              <el-form-item required :label="$t('dataConfMod.sqlCode')" prop="sqlCode">
                <span slot="label">
                  {{ $t('dataConfMod.sqlCode') }}
                  <el-tooltip
                    :content="$t('dataConfMod.dynamicReport')"
                    placement="top"
                    effect="dark"
                  >
                    <span class="el-icon-question" />
                  </el-tooltip>
                </span>
                <el-input v-model="form.sqlCode" />
              </el-form-item>
            </el-col>
            <el-col v-else :span="6">
              <!-- 功能地址 -->
              <el-form-item :label="$t('dataConfMod.functionAddress')" prop="functionAddress">
                <el-input v-model="form.functionAddress" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <!-- 功能描述 -->
              <el-form-item :label="$t('dataConfMod.functionDesc')" prop="functionDesc">
                <el-input v-model="form.functionDesc" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <!-- 生效日期 -->
              <el-form-item :label="$t('dataConfMod.startDate')" prop="startDate">
                <el-date-picker
                  v-model="form.startDate"
                  type="date"
                  :placeholder="$t('common.pleaseSelectDate')"
                  format="yyyy-MM-dd"
                  value-format="yyyy-MM-dd"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <!-- 失效日期 -->
              <el-form-item :label="$t('dataConfMod.endDate')" prop="endDate">
                <el-date-picker
                  v-model="form.endDate"
                  type="date"
                  :placeholder="$t('common.pleaseSelectDate')"
                  format="yyyy-MM-dd"
                  value-format="yyyy-MM-dd"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <!-- 功能图标 -->
              <el-form-item :label="$t('dataConfMod.functionIcon')" prop="functionIcon">
                <el-input v-model="form.functionIcon" disabled>
                  <el-button slot="append" icon="el-icon-search" @click="getIconList" />
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <!-- 按钮接口权限 -->
          <el-collapse
            v-model="activeDims"
            class="borderStyle"
          >
            <!-- 按钮功能设置 -->
            <el-collapse-item
              name="1"
              :title="$t('dataConfMod.btnFuncSetting')"
            >
              <div style="margin-bottom: 16px">
                <el-button
                  type="primary"
                  class="detail-pbtn"
                  @click="addBtnSetting"
                >
                  {{ $t("common.add") }}
                </el-button>
              </div>
              <el-table
                :data="menuBtnSettingList"
                border
                max-height="260px"
              >
                <el-table-column
                  align="center"
                  type="index"
                  width="50"
                />
                <!-- 按钮名称 -->
                <el-table-column
                  align="center"
                  prop="permissionName"
                  :label="$t('dataConfMod.btnName')"
                  width="180"
                >
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.permissionName" />
                  </template>
                </el-table-column>
                <!-- 按钮标识 -->
                <el-table-column
                  align="center"
                  prop="permission"
                  :label="$t('dataConfMod.btnSign')"
                  min-width="200"
                >
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.permission" />
                  </template>
                </el-table-column>
                <!-- 操作 -->
                <el-table-column
                  align="center"
                  prop="operation"
                  :label="$t('common.operation')"
                  width="130"
                  fixed="right"
                >
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      @click="btnInterface(scope.row, scope.$index)"
                    >
                      接口维护
                    </el-button>
                    <el-button
                      type="text"
                      @click="deleteBtnSetting(scope)"
                    >
                      {{ $t("common.delete") }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
              <srm-dialog
                title="接口维护"
                :visible.sync="btnInterfaceDialogVisible"
                append-to-body
                size="middle"
                :close-on-click-modal="false"
              >
                <div style="margin-bottom: 8px">
                  <el-button
                    type="primary"
                    class="detail-pbtn"
                    @click="btnInterfaceDialogAdd"
                  >
                    {{ $t("common.add") }}
                  </el-button>
                </div>
                <el-table
                  ref="btnInterfaceDialogTable"
                  :data="btnInterfaceDialogTable"
                  border
                  max-height="350px"
                >
                  <el-table-column
                    align="center"
                    type="index"
                    width="50"
                  />
                  <!-- 接口名称 -->
                  <el-table-column
                    align="center"
                    prop="permissionName"
                    :label="$t('dataConfMod.interfaceName')"
                    width="150"
                  >
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.permissionName" />
                    </template>
                  </el-table-column>
                  <!-- 接口 -->
                  <el-table-column
                    align="center"
                    prop="permission"
                    min-width="200"
                    :label="$t('dataConfMod.interface')"
                  >
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.permission" />
                    </template>
                  </el-table-column>
                  <!-- 按钮标识 -->
                  <!-- 操作 -->
                  <el-table-column
                    align="center"
                    prop="operation"
                    :label="$t('common.operation')"
                    width="80"
                    fixed="right"
                  >
                    <template slot-scope="scope">
                      <el-button
                        type="text"
                        @click="btnInterfaceDialogDel(scope)"
                      >
                        {{ $t("common.delete") }}
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
                <div slot="footer" class="dialog-footer">
                  <el-button @click="btnInterfaceDialogClose">
                    {{ $t('common.cancel') }}
                  </el-button>
                  <el-button type="primary" @click="btnInterfaceDialogConfirm">
                    {{ $t('common.confirm') }}
                  </el-button>
                </div>
              </srm-dialog>
            </el-collapse-item>
            <!-- 接口设置 -->
            <el-collapse-item
              name="2"
              :title="$t('dataConfMod.interfaceSetting')"
            >
              <div style="margin-bottom: 16px">
                <el-button
                  type="primary"
                  class="detail-pbtn"
                  @click="addInterface"
                >
                  {{ $t("common.add") }}
                </el-button>
              </div>
              <el-table
                :data="menuSettingInterface"
                border
                max-height="260px"
              >
                <el-table-column
                  align="center"
                  type="index"
                  width="50"
                />
                <!-- 接口名称 -->
                <el-table-column
                  align="center"
                  prop="permissionName"
                  :label="$t('dataConfMod.interfaceName')"
                  width="180"
                >
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.permissionName" />
                  </template>
                </el-table-column>
                <!-- 接口 -->
                <el-table-column
                  align="center"
                  prop="permission"
                  min-width="200"
                  :label="$t('dataConfMod.interface')"
                >
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.permission" />
                  </template>
                </el-table-column>
                <!-- 按钮标识 -->
                <!-- 操作 -->
                <el-table-column
                  align="center"
                  prop="operation"
                  :label="$t('common.operation')"
                  width="60"
                  fixed="right"
                >
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      @click="deleteInterface(scope)"
                    >
                      {{ $t("common.delete") }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
          </el-collapse>
        </el-form>
      </div>
      <!-- 图标选择 -->
      <FunctionIconDialog
        v-if="iconDialogVisible"
        :visible.sync="iconDialogVisible"
        :icon="iconModel"
        @selectIcon="selectIcon"
      />
      <CToolbar>
        <template #right>
          <el-button
            type="primary"
            :loading="saveLoading"
            @click="saveData"
          >
            {{ $t('common.affirm') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin, tabTodoWatch } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import FunctionIconDialog from './functionIconDialog.vue'
import { functionApi } from 'modb@/accountAccess/api'
import { param2Obj } from '@/utils'
export default {
  name: 'FunctionMaintenanceEdit',
  components: {
    CToolbar,
    FunctionIconDialog
  },
  mixins: [tabTodoMixin, tabTodoWatch],
  data () {
    return {
      curOpt: '',
      form: {
        functionName: '',
        functionCode: '',
        functionDesc: '',
        functionAddress: '',
        functionType: 'normal',
        sqlCode: '',
        functionIcon: '',
        startDate: '',
        endDate: ''
      },
      rules: {
        // '请输入功能名称'
        functionName: [{ required: true, message: this.$t('dataConfMod.msgFunctionName') }],
        // '请输入功能编码'
        functionCode: [{ required: true, message: this.$t('dataConfMod.msgFunctionCode') }],
        // '请输入功能地址'
        functionAddress: [{ required: true, message: this.$t('dataConfMod.msgFunctionAddress') }],
        // '请输入生效日期'
        startDate: [{ required: true, message: this.$t('dataConfMod.msgStartDate') }]
      },
      dialogFormVisible: false,
      iconDialogVisible: false,
      iconModel: '',
      activeDims: ['1', '2'],
      menuBtnSettingList: [],
      menuSettingInterface: [],
      childPermissionsHis: null,
      hisRowIndex: null,
      btnInterfaceDialogVisible: false,
      btnInterfaceDialogTable: [],
      saveLoading: false
    }
  },
  created () {
    const { flag } = this.$attrs.params
    this.curOpt = flag
    // 新增
    if (this.curOpt == 'add') {
      for (let i in this.form) {
        this.form[i] = ''
      }
      this.form.functionType = 'normal'
      this.form.startDate = new Date()
      this.menuBtnSettingList = []
    } else {
      this.curOpt = 'edit'
      let row = this.$attrs.params.row
      this.getDetail(row)
    }
  },
  methods: {
    // 编辑
    async getDetail (row) {
      let functionId = row.functionId
      let { data } = await functionApi.functionGet({ id: functionId })
      this.form = data.function
      this.iconModel = data.function.functionIcon
      this.menuBtnSettingList = data.buttons
      this.menuSettingInterface = data.interfaces

      if (this.form.functionAddress.indexOf('/baseSettingCommon/dynamicReportPage') > -1) {
        const query = param2Obj(this.form.functionAddress) // .split('?')
        // const paramsStr = new URLSearchParams(query)
        this.form.sqlCode = query.sqlCode // params.get('sqlCode')
        this.form.functionType = 'report'
      } else {
        this.form.functionType = 'normal'
      }
    },
    // 保存数据
    saveData () {
      this.saveLoading = true
      this.$refs.funForm.validate(valid => {
        if (valid) {
          let submitData = this.form
          submitData.startDate = new Date(submitData.startDate).getTime()
          const { functionType, sqlCode } = this.form
          if (functionType === 'report') {
            submitData.functionAddress = `/baseSettingCommon/dynamicReportPage?sqlCode=${sqlCode}`
          }
          const params = {
            function: { ...submitData },
            buttons: this.menuBtnSettingList,
            interfaces: this.menuSettingInterface
          }
          try {
            if (this.curOpt === 'add') {
              // 新增
              delete params.function.functionId // 去掉ID
              functionApi.functionAdd(params).then(res => {
                this.saveLoading = false
                this.$message({
                  message: res.message,
                  type: 'success'
                })
              })
            } else {
              // 编辑
              functionApi.functionModify(params).then(res => {
                this.saveLoading = false
                this.$message({
                  message: res.message,
                  type: 'success'
                })
              })
            }
          } catch (e) {
            this.saveLoading = false
          }
          this.back()
        } else {
          this.saveLoading = false
          return false
        }
      })
    },
    // 查询图标
    getIconList () {
      this.iconDialogVisible = true
    },
    // 选择图标
    selectIcon (val) {
      this.form.functionIcon = val
    },
    // 添加按钮
    addBtnSetting () {
      const mockData = {
        permissionName: '',
        permissionCode: '',
        permission: '',
        childPermissions: []
      }
      this.menuBtnSettingList = this.menuBtnSettingList.concat(mockData)
    },
    // 删除
    deleteBtnSetting (scope) {
      this.menuBtnSettingList.splice(scope.$index, 1)
    },
    // 添加接口
    addInterface () {
      const mockData = {
        permissionName: '',
        permissionCode: '',
        permission: ''
      }
      this.menuSettingInterface = this.menuSettingInterface.concat(mockData)
    },
    // 删除接口
    deleteInterface (scope) {
      this.menuSettingInterface.splice(scope.$index, 1)
    },
    // 打开按钮接口维护
    btnInterface (row, index) {
      this.childPermissionsHis = JSON.stringify(row.childPermissions)
      this.hisRowIndex = index
      this.btnInterfaceDialogTable = row.childPermissions
      this.btnInterfaceDialogVisible = true
      this.$nextTick(() => {
        this.$refs.btnInterfaceDialogTable.doLayout()
      })
    },
    // 新增
    btnInterfaceDialogAdd () {
      const mockData = {
        permissionName: '',
        permissionCode: '',
        permission: ''
      }
      this.btnInterfaceDialogTable.push(mockData)
    },
    // 删除
    btnInterfaceDialogDel (scope) {
      this.btnInterfaceDialogTable.splice(scope.$index, 1)
    },
    // 取消
    btnInterfaceDialogClose () {
      this.btnInterfaceDialogVisible = false
      // 取消还原 原来数据
      this.menuBtnSettingList[this.hisRowIndex].childPermissions = JSON.parse(this.childPermissionsHis)
    },
    // 确认
    btnInterfaceDialogConfirm () {
      this.btnInterfaceDialogVisible = false
      this.menuBtnSettingList[this.hisRowIndex].childPermissions = this.btnInterfaceDialogTable
    },
    // 返回
    back () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      this.__setTabTodo('FunctionMaintenance.getQuerydata')
    }
  }
}
</script>
