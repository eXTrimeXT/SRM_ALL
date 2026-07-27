<template>
  <el-container
    class="flex-container-notab the_roleFunctionSet_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        @getFormData="getQuerydata"
      />
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <el-button
            type="primary"
            @click="addOne"
          >
            {{ $t("common.add") }}
          </el-button>
        </template>
      </MainHeader>

      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :page-size="pageSize"
        url="/api-rbac/role/roleFuncSet/listPage"
      />
      <!-- 弹框区域-->
      <srm-dialog
        :title="dialogTitle"
        size="middle"
        :visible.sync="dialogFormVisible"
        :close-on-click-modal="false"
      >
        <el-form
          ref="form"
          :model="form"
          :rules="rules"
          label-position="top"
        >
          <el-row :gutter="32">
            <el-col :span="12">
              <!-- 角色编码 -->
              <el-form-item
                :label="$t('dataConfMod.roleCode')"
                prop="roleType"
              >
                <QuickSearch
                  :disabled="isEdit"
                  :show-input="form.roleCode"
                  show-key="roleCode"
                  :scope-data="form"
                  name="scc_rbac_role_info"
                  @close-quicksearch="getRoleObj"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <!-- 功能模块 -->
              <el-form-item
                :label="$t('dataConfMod.functionModule')"
                prop="functionCode"
              >
                <QuickSearch
                  :show-input="form.functionName"
                  show-key="functionName"
                  :scope-data="form"
                  name="scc_rbac_function_info"
                  @close-quicksearch="getFuncObj"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <!-- 权限控制 -->
              <el-form-item
                :label="$t('dataConfMod.accessCtrl')"
                prop="roleFuncSetType"
              >
                <DictSelect
                  v-model="form.roleFuncSetType"
                  code="ROLE_FUNC_SET_TYPE"
                  clearable
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <!-- 是否启用 -->
              <el-form-item :label="$t('dataConfMod.enabledUse')">
                <el-checkbox
                  v-model="form.enableFlag"
                  true-label="Y"
                  false-label="N"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <div
          slot="footer"
          class="dialog-footer"
        >
          <el-button @click="dialogFormVisible = false">
            {{ $t("common.cancel") }}
          </el-button>
          <el-button
            type="primary"
            @click="saveData"
          >
            {{ $t("common.confirm") }}
          </el-button>
        </div>
      </srm-dialog>
    </el-main>
  </el-container>
</template>
<script>
import QuickSearch from 'lib@/components/QuickSearch' // 快速查询组件
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { parseTime } from '@/utils'
import { isMobile, isEmail } from 'lib@/utils/validate'
import { accessApi, roleSetApi } from 'modb@/accountAccess/api'

export default {
  name: 'RoleFunctionSet',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    QuickSearch
  },
  data () {
    return {
      gridId: 'account',
      curOpt: 'add',
      pageSize: 15,
      dialogTitle: '',
      tableData: [],
      tableHeader: [],
      queryParam: {},
      roleUsers: [],
      userTypes: [],
      preArr: [
        {
          prop: 'roleCode',
          label: () => this.$t('dataConfMod.roleCode'), // 角色编码
          type: 'quicksearch',
          showKey: 'roleCode',
          name: 'scc_rbac_role_info'
        },
        {
          prop: 'functionCode',
          label: () => this.$t('dataConfMod.functionModule'), // 功能模块
          type: 'quicksearch',
          showKey: 'functionName',
          propKey: 'functionCode',
          name: 'scc_rbac_function_info'
        },
        {
          prop: 'roleFuncSetType',
          label: () => this.$t('dataConfMod.accessCtrl'), // 权限控制
          type: 'dict',
          code: 'ROLE_FUNC_SET_TYPE'

        }
      ],
      form: {
        roleType: null,
        functionCode: null,
        roleFuncSetType: null,
        roleFuncSetId: null,
        enableFlag: 'Y'
      },
      rules: {
        roleType: [
          { required: true, message: this.$t('dataConfMod.msgRoleCode') }
        ], // 请输入角色编码
        functionCode: [
          { required: true, message: this.$t('dataConfMod.msgFuncModule') }
        ] // 请输入功能模块
      },
      dialogFormVisible: false
    }
  },
  computed: {
    isEdit () {
      return !!this.form.roleFuncSetId
    }
  },
  created () {
    var _this = this
    this.tableHeader = [
      {
        prop: 'roleCode',
        label: () => _this.$t('dataConfMod.roleCode'),
        minWidth: 150
      }, // 角色编码
      {
        prop: 'roleType',
        label: () => _this.$t('dataConfMod.roleType'),
        minWidth: 150
      }, // 角色类型
      {
        prop: 'functionName',
        label: () => _this.$t('dataConfMod.functionModule'),
        minWidth: 150
      }, // 功能模块
      {
        prop: 'roleName',
        label: () => _this.$t('dataConfMod.roleName'),
        minWidth: 150
      }, // 角色名称
      {
        prop: 'roleFuncSetType',
        label: () => _this.$t('dataConfMod.accessCtrl'), // 权限控制
        minWidth: 150,
        dataType: 'dict',
        code: 'ROLE_FUNC_SET_TYPE'
      },
      {
        prop: 'enableFlag',
        label: () => _this.$t('dataConfMod.enabledUse'), // 是否启用
        width: 100,
        dataType: 'dict',
        code: 'YES_OR_NO'

      },
      {
        prop: 'lastUpdateDate',
        label: () => _this.$t('qualitySynergy.updateDate'), // 更新日期
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'operation',
        label: () => _this.$t('common.operation'), // '操作'
        width: 120,
        btnStyle: 'text',
        fixed: 'right',
        showType: 'buttons',
        buttons: [
          {
            callback: function (row) {
              this.editTab(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.edit') // '编辑'
            }
          },
          {
            callback: function (row) {
              this.delRowData(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.delete') // '删除'
            }
          }
        ]
      }
    ]
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  mounted () {
    // FIXME: 没有全量返回的接口，hardcode pageSize 999
    accessApi.roleListHttp({ pageSize: 999, pageNum: 1 }).then(res => {
      this.roleList = res.data.list.filter(i => {
        const nowIsAfterStartDate = this.$dayjs().isAfter(
          this.$dayjs(i.startDate)
        )
        if (!i.endDate) {
          return nowIsAfterStartDate
        }
        const nowIsBeforeEndDate = this.$dayjs().isBefore(
          this.$dayjs(i.endDate)
        )
        return nowIsAfterStartDate && nowIsBeforeEndDate
      })
    })
  },
  methods: {
    bankeDel (index) {
      this.roleUsers.splice(index, 1)
    },
    getFuncObj (val, scope) {
      scope.functionId = val ? val.functionId : ''
      scope.functionCode = val ? val.functionCode : ''
      scope.functionName = val ? val.functionName : ''
      scope.functionAddress = val ? val.functionAddress : ''
    },
    getRoleObj (val, scope) {
      scope.roleId = val ? val.roleId : null
      scope.roleCode = val ? val.roleCode : null
      scope.roleType = val ? val.roleType : null
    },
    addOne () {
      this.dialogTitle = this.$t('dataConfMod.addUser') // "新增用户"
      this.curOpt = 'add'
      for (let i in this.form) {
        this.form[i] = null
      }
      this.form.enableFlag = 'Y'
      this.dialogFormVisible = true
    },
    editTab (row) {
      this.curOpt = 'edit'
      this.dialogTitle = this.$t('dataConfMod.editUser') // "编辑用户"
      /* for (let i in this.form) {
        this.form[i] = row[i];
      } */
      this.form = row
      this.dialogFormVisible = true
    },
    getQuerydata (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    transfromDate (target) {
      if (Array.isArray(target) && target.length) {
        return target.map(item => ({
          ...item,
          startDate: this.date2timestamp(item.startDate),
          endDate: this.date2timestamp(item.endDate)
        }))
      }
      return target
    },
    date2timestamp (time) {
      return new Date(time).getTime()
    },
    delRowData (row) {
      roleSetApi.roleFuncSetDelete({ id: row.roleFuncSetId }).then(res => {
        this.$message({
          message: res.message,
          type: 'success'
        })
        this.getQuerydata()
      })
    },
    // 保存数据
    saveData () {
      this.$refs.form.validate(valid => {
        if (valid) {
          if (!this.form.roleFuncSetId) {
            // 新增
            roleSetApi.roleFuncSetAdd(this.form).then(res => {
              this.$message({
                message: res.message,
                type: 'success'
              })
              this.getQuerydata()
              this.dialogFormVisible = false
            })
          } else {
            // 编辑
            roleSetApi.roleFuncSetModify(this.form).then(res => {
              this.$message({
                message: res.message,
                type: 'success'
              })
              this.getQuerydata()
              this.dialogFormVisible = false
            })
          }
        } else {
          return false
        }
      })
    },
    addOneRole () {
      this.roleUsers.push({ startDate: new Date().getTime() })
    }
  }
}
</script>
<style scoped lang="scss">
.the_roleFunctionSet_wrapper {
}
</style>
