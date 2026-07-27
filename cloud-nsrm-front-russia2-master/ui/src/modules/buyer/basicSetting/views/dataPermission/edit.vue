<template>
  <el-container class="datapermissionEdit" direction="vertical">
    <el-main>
      <div class="form-container">
        <el-form ref="form" :model="form" :rules="rules" :disabled="readOnly">
          <el-collapse v-model="activeCollapse" class="tab-form-style">
            <el-collapse-item :title="$t('datapermission.baseSetting')" name="1">
              <srm-row>
                <srm-col :init-col="3">
                  <el-form-item
                    prop="dataPermissionName"
                    :label="$t('dataConfMod.dataPermissionName')"
                  >
                    <el-input v-model="form.dataPermissionName" />
                  </el-form-item>
                </srm-col>
                <srm-col :init-col="3">
                  <el-form-item prop="apiUrl" :label="$t('dataConfMod.apiUrl')">
                    <el-input v-model="form.apiUrl" />
                  </el-form-item>
                </srm-col>
                <srm-col :init-col="3">
                  <el-form-item prop="permissionStatus" :label="$t('dataConfMod.permissionStatus')">
                    <DictSelect v-model="form.permissionStatus" code="PERMISSION_STATUS" />
                  </el-form-item>
                </srm-col>
              </srm-row>
              <srm-row>
                <srm-col :init-col="3">
                  <el-form-item prop="permissionStatus" :label="$t('dataConfMod.permissionType')">
                    <el-select v-model="form.permissionType" :placeholder="$t('common.pleaseSelect')">
                      <el-option label="MyBatis" value="MYBATIS" />
                      <el-option label="MeiQL" value="MEIQL" />
                    </el-select>
                  </el-form-item>
                </srm-col>
              </srm-row>
              <srm-row>
                <srm-col :init-col="1">
                  <el-form-item v-if="form.permissionType=='MYBATIS'" prop="mybatisSqlId" label="MYBATIS_SQL_ID">
                    <el-input v-model="form.mybatisSqlId" type="textarea" :rows="2" />
                  </el-form-item>
                  <el-form-item v-if="form.permissionType=='MEIQL'" prop="modelInfo" :label="$t('dataConfMod.modelInfo')">
                    <el-input v-model="form.modelInfo" type="textarea" :rows="2" />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-collapse-item>

            <el-collapse-item :title="$t('datapermission.childQuery')" name="2">
              <p class="btn_line">
                <el-button type="primary" @click="addRuleList">
                  {{ $t('components.viewSwitcher.add') }}
                </el-button>
                <el-button type="primary" @click="delRuleList">
                  {{ $t('components.common.delete') }}
                </el-button>
              </p>
              <TableView
                ref="ruleRef"
                :checkbox="true"
                style="height: 300px"
                table-height="400"
                :table-infor="ruleTableList"
                :table-header="ruleTableHeader"
                :check-change="ruleSelectChange"
                :row-index-fixed="false"
                :page-enabled="false"
                :com-active="$attrs['changeTab']"
              >
                <template #ruleStatus="{ scope }">
                  <el-checkbox v-model="scope.row.ruleStatus" true-label="Y" false-label="N" />
                </template>
                <!-- 生效角色 -->
                <template #roleCode="{ scope }">
                  <CRoleSelector
                    v-model="scope.row.roleCode"
                    collapse-tags
                    value-key="roleCode"
                    :placeholder="$t('common.pleaseSelect')"
                  />
                </template>
                <!-- 子查询 -->
                <template #childSqlExpression="{ scope }">
                  <el-input v-model="scope.row.childSqlExpression" type="textarea" :maxlength="null" :autosize="{ minRows: 1, maxRows: 4}" />
                </template>
              </TableView>
            </el-collapse-item>
            <el-collapse-item :title="$t('datapermission.pramasSetting')" name="3">
              <p class="btn_line">
                <el-button type="primary" @click="addVarList">
                  {{ $t('components.viewSwitcher.add') }}
                </el-button>
                <el-button type="primary" @click="delVarList">
                  {{ $t('components.common.delete') }}
                </el-button>
                <el-button type="primary" @click="loadOptions">
                  {{ $t('datapermission.loadingItem') }}
                </el-button>
              </p>
              <TableView
                ref="varRef"
                :checkbox="true"
                style="height: 300px"
                table-height="400"
                :table-infor="varTableList"
                :table-header="varTableHeader"
                :check-change="varSelectChange"
                :row-index-fixed="false"
                :page-enabled="false"
                :com-active="$attrs['changeTab']"
              >
                <template #varStatus="{ scope }">
                  <el-checkbox v-model="scope.row.varStatus" true-label="Y" false-label="N" />
                </template>
                <template #slotVarValue="{ scope }">
                  <div>
                    <el-input v-model="scope.row.varValue" type="textarea" />
                  </div>
                </template>
              </TableView>
            </el-collapse-item>

            <el-collapse-item v-if="form.permissionType=='MYBATIS'" :title="$t('components.viewConfig.sortable')" name="4">
              <p class="btn_line">
                <el-button type="primary" @click="addOrderList">
                  {{ $t('components.viewSwitcher.add') }}
                </el-button>
                <el-button type="primary" @click="delOrderList">
                  {{ $t('components.common.delete') }}
                </el-button>

                <el-checkbox
                  v-model="form.coverOrder"
                  true-label="Y"
                  false-label="N"
                  style="margin-left: 15px;"
                >
                  {{ $t('datapermission.coverOrder') }}
                </el-checkbox>
              </p>
              <TableView
                ref="orderRef"
                :checkbox="true"
                style="height: 300px"
                table-height="400"
                :table-infor="orderTableList"
                :table-header="orderTableHeader"
                :check-change="orderSelectChange"
                :row-index-fixed="false"
                :page-enabled="false"
                :com-active="$attrs['changeTab']"
              >
                <template #ruleStatus="{ scope }">
                  <el-checkbox v-model="scope.row.ruleStatus" true-label="Y" false-label="N" />
                </template>
                <!-- 生效角色 -->
                <template #roleCode="{ scope }">
                  <CRoleSelector
                    v-model="scope.row.roleCode"
                    collapse-tags
                    value-key="roleCode"
                    :placeholder="$t('common.pleaseSelect')"
                  />
                </template>
                <template #ruleColumnName="{ scope }">
                  <div>
                    <el-input v-model="scope.row.ruleColumnName" />
                  </div>
                </template>
              </TableView>
            </el-collapse-item>
          </el-collapse>
        </el-form>
      </div>
      <CToolbar>
        <template #right>
          <el-button @click="cancelBill">
            {{ $t('bidMod.cancel') }}
          </el-button>
          <el-button type="primary" :disabled="readOnly" @click="save">
            {{ $t('orderMod.buyerOrderSynergy.confirm') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import TableView from 'lib@/components/Table/TableView'
import CRoleSelector from '@/library/components/c-role-selector'
import { dataPermission, dataPermissionOption } from 'modb@/basicSetting/api/basicSetting'

let seed = 0

function generateUniqueId () {
  const index = ++seed
  return `custom_unique_id_${index}`
}

export default {
  name: 'DatapermissionEdit',
  components: {
    CToolbar,
    TableView,
    CRoleSelector
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      ruleTableList: [],
      ruleTableHeader: [],
      ruleTableSelectList: [],

      orderTableList: [],
      orderTableHeader: [],
      orderTableSelectList: [],

      varTableList: [],
      varTableHeader: [],
      varTableSelectList: [],
      activeCollapse: ['1', '2', '3', '4'],
      form: {
        dataPermissionName: null,
        apiUrl: null,
        mybatisSqlId: null,
        permissionType: null,
        permissionStatus: null,
        modelInfo: null,
        childSqlList: null,
        orderList: null,
        dataPermissionVarList: null,
        coverOrder: null
      },
      rules: {},
      readOnly: false
    }
  },
  computed: {},
  watch: {},
  created () {
    // 子查询规则配置
    this.ruleTableHeader = [
      {
        prop: 'ruleStatus',
        label: () => this.$t('dataConfMod.enabled'),
        showType: 'slot',
        width: '100',
        editable: row => {
          return row.editable && !this.readOnly
        },
        slot: 'ruleStatus'
      },
      // '用户角色'
      {
        prop: 'roleCode',
        label: () => this.$t('datapermission.enableRole'),
        showType: 'slot',
        showOverflowTooltip: () => false,
        width: '200',
        editable: row => {
          return row.editable && !this.readOnly
        },
        slot: 'roleCode'
      },
      // '子查询表达式'
      {
        prop: 'childSqlExpression',
        label: () => this.$t('datapermission.childSqlExpression'),
        showType: 'slot',
        showOverflowTooltip: () => false,
        editable: row => {
          return row.editable && !this.readOnly
        },
        slot: 'childSqlExpression'
      }
    ]

    // 子查询规则配置
    this.orderTableHeader = [
      {
        prop: 'ruleStatus',
        label: () => this.$t('dataConfMod.enabled'),
        showType: 'slot',
        editable: row => {
          return row.editable
        },
        slot: 'ruleStatus'
      },
      // '用户角色'
      {
        prop: 'roleCode',
        label: () => this.$t('datapermission.enableRole'),
        showType: 'slot',
        showOverflowTooltip: () => false,
        editable: row => {
          return row.editable
        },
        slot: 'roleCode'
      },
      // '排序字段'
      {
        prop: 'ruleColumnName',
        label: () => this.$t('datapermission.ruleColumnName'),
        showType: 'slot',
        showOverflowTooltip: () => false,
        editable: row => {
          return row.editable
        },
        slot: 'ruleColumnName'
      }
    ]

    // 变量配置
    this.varTableHeader = [
      {
        prop: 'varStatus',
        label: () => this.$t('dataConfMod.enabled'),
        showType: 'slot',
        width: 100,
        editable: row => {
          return row.editable
        },
        slot: 'varStatus'
      },
      {
        prop: 'varName',
        label: () => this.$t('datapermission.varName'),
        width: 150,
        showType: 'input',
        editable: row => {
          return row.editable
        }
      },
      {
        prop: 'varDesc',
        label: () => this.$t('datapermission.varDesc'),
        width: 150,
        showType: 'input',
        editable: row => {
          return row.editable
        }
      },
      {
        prop: 'varType',
        label: () => this.$t('datapermission.varType'),
        width: 120,
        showType: 'dictSelect',
        editable: row => {
          return row.editable
        },
        code: 'VAR_TYPE'
      },
      {
        prop: 'varValue',
        label: () => this.$t('datapermission.varValue'),
        width: 300,
        showType: 'slot',
        slot: 'slotVarValue',
        editable: row => {
          return row.varType !== 'REFLECT' && row.editable
        }
      },
      {
        prop: 'permissionClassName',
        label: () => this.$t('datapermission.permissionClassName'),
        showType: 'input',
        editable: row => {
          return row.varType === 'REFLECT' && row.editable
        }
      },
      {
        prop: 'permissionMethodName',
        label: () => this.$t('dataConfMod.permissionMethodName'),
        showType: 'input',
        editable: row => {
          return row.varType === 'REFLECT' && row.editable
        }
      },
      {
        prop: 'remark',
        label: () => this.$t('common.remark'),
        showType: 'input',
        editable: row => {
          return row.editable
        }
      }
    ]
  },
  async mounted () {
    const { flag, row, readOnly = false } = this.$attrs.params
    this.readOnly = readOnly
    if (flag === 'edit' || flag === 'view') {
      let res = await dataPermission.get(row.dataPermissionId)
      let dataInfo = res.data
      this.form = dataInfo

      const ruleArray = dataInfo.childSqlList || []
      for (let i = 0; i < ruleArray.length; i++) {
        ruleArray[i]['$index'] = generateUniqueId()
        ruleArray[i]['editable'] = false // tableView 可编辑标识
      }

      const orderArray = dataInfo.orderList || []
      for (let i = 0; i < orderArray.length; i++) {
        orderArray[i]['$index'] = generateUniqueId()
        orderArray[i]['editable'] = false
      }

      const varArray = dataInfo.dataPermissionVarList || []
      for (let i = 0; i < varArray.length; i++) {
        varArray[i]['$index'] = generateUniqueId()
        varArray[i]['editable'] = false
      }

      this.ruleTableList = ruleArray // 子查询
      this.orderTableList = orderArray // 排序
      this.varTableList = varArray // 变量查询

      this.$nextTick(async () => {
        if (this.ruleTableList) {
          await this.$refs.ruleRef.loadDict(this.ruleTableList)
        }
        this.$refs.ruleRef.tableData = this.ruleTableList || []

        if (this.orderTableList) {
          await this.$refs.orderRef.loadDict(this.orderTableList)
        }
        this.$refs.orderRef.tableData = this.orderTableList || []

        if (this.varTableList) {
          await this.$refs.varRef.loadDict(this.varTableList)
        }
        this.$refs.varRef.tableData = this.varTableList || []
      })
    }
  },
  methods: {
    addRuleList () {
      this.$refs.ruleRef.addOneEditableColumn({
        $index: generateUniqueId(),
        ruleType: 'CHILD_SQL',
        ruleStatus: '',
        roleCode: '',
        childSqlExpression: '',
        editable: true
      })
    },
    addOrderList () {
      this.$refs.orderRef.addOneEditableColumn({
        $index: generateUniqueId(),
        ruleType: 'CHILD_ORDER',
        ruleStatus: '',
        roleCode: '',
        ruleColumnName: '',
        editable: true
      })
    },
    addVarList () {
      this.$refs.varRef.addOneEditableColumn({
        $index: generateUniqueId(),
        varStatus: '',
        varName: '',
        varDesc: '',
        varType: '',
        varValue: '',
        permissionClassName: '',
        permissionMethodName: '',
        remark: '',
        editable: true
      })
    },
    ruleSelectChange (select) {
      this.ruleTableSelectList = select
    },
    orderSelectChange (select) {
      this.orderTableSelectList = select
    },
    varSelectChange (select) {
      this.varTableSelectList = select
    },
    delRuleList () {
      this.deleteBySelect(this.ruleTableSelectList, this.ruleTableList)
    },
    delOrderList () {
      this.deleteBySelect(this.orderTableSelectList, this.orderTableList)
    },
    delVarList () {
      this.deleteBySelect(this.varTableSelectList, this.varTableList)
    },
    deleteBySelect (selectList, currentList) {
      for (let i = 0; i < selectList.length; i++) {
        for (let j = 0; j < currentList.length; j++) {
          if (selectList[i].$index === currentList[j].$index) {
            currentList.splice(j, 1)
            break
          }
        }
      }
    },
    save () {
      this.$refs.form.validate(result => {
        if (result) {
          const { flag } = this.$attrs.params
          // 新增时不用提交主键值
          this.form.childSqlList = this.ruleTableList
          this.form.orderList = this.orderTableList
          this.form.dataPermissionVarList = this.varTableList
          if (flag === 'add') {
            this.form.dataPermissionId = null
          }
          dataPermission.saveOrUpdate(this.form).then(res => {
            this.$message({
              type: 'success',
              message: res.message
            })
            this.cancelBill()
          })
        } else {
          this.__focus_error__()
        }
      })
    },
    cancelBill () {
      this.$emit('tab-remove', this.$attrs.tabName)
      this.__setTabTodo('datapermissionList.getQueryData')
    },
    loadOptions () {
      dataPermissionOption.listActive().then(res => {
        for (let i = 0; i < res.data.length; i++) {
          let optionItem = res.data[i]
          this.$refs.varRef.addOneEditableColumn({
            $index: generateUniqueId(),
            varStatus: 'N',
            varName: optionItem.varName,
            varDesc: optionItem.varDesc,
            varType: optionItem.varType,
            varValue: optionItem.varValue,
            permissionClassName: optionItem.permissionClassName,
            permissionMethodName: optionItem.permissionMethodName,
            remark: optionItem.remark
          })
        }
      })
    }
  }
}
</script>
<style scoped lang="scss">
.datapermissionEdit {
  height: 100%;

  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }

  .el-table .el-date-editor {
    width: 135px;
  }

  .base-form {
    padding: 15px 30px 0;
  }

  .toRequired {
    color: #ff4949;
    padding-right: 2px;
  }

  .edit_cond {
    color: #23adf4;
    cursor: pointer;
  }
}
</style>
