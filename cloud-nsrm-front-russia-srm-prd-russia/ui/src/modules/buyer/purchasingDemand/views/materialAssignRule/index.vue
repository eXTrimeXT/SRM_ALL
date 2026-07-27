<template>
  <el-container
    class="flex-container-notab the_contractPaymentTypeList_wrapper"
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
            style="float:left;"
            @click="editTab('add')"
          >
            {{ $t("common.add") }}
          </el-button>
          <MImport
            ref="import"
            :title="iModal.title"
            :extra-data="iModal.extraData"
            :up-load-url="iModal.upLoadUrl"
            :show-success-deal="true"
            type="default"
            @downloadTemplate="downloadTemplate"
            @handleSuccess="uploadSuccess"
          />
        </template>
      </MainHeader>

      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        url="/api-sup-ce/division/divisionMaterial/listPageByParam"
      />
      <srm-dialog
        :title="$t('purchaseDemand.materialAssignRuleDl')"
        :visible.sync="dialogFormVisible"
        :close-on-click-modal="false"
        size="middle"
      >
        <el-form
          ref="form"
          :model="form"
          class="form-incontainer"
          :rules="rules"
          label-width="80px"
          label-position="top"
        >
          <srm-row>
            <srm-col :initCol="2">
              <!-- 业务实体 -->
              <el-form-item
                :label="$t('purchaseDemand.businessEntity')"
                :label-width="formLabelWidth"
                prop="orgId"
              >
                <OrganizationSelector
                  ref="organizationSelector"
                  v-model="form.orgId"
                  :parent-id="-1"
                  :placeholder="$t('common.pleaseSelect')"
                  node-type="OU"
                  @select="selectHandler"
                />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="2">
              <!-- 库存组织 -->
              <el-form-item
                :label="$t('purchaseDemand.invOrg')"
                :label-width="formLabelWidth"
                prop="organizationId"
              >
                <OrganizationSelector
                  ref="organizationSelector2"
                  v-model="form.organizationId"
                  :parent-id="form.orgId"
                  node-type="INV"
                  :placeholder="$t('common.pleaseSelect')"
                  @select="selectHandler2"
                />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="2">
              <!-- 物料编号 -->
              <el-form-item
                :label="$t('purchaseDemand.itemCode')"
                :label-width="formLabelWidth"
                prop="materialCode"
              >
                <QuickSearch
                  :show-input="form.materialCode"
                  show-key="materialCode"
                  :scope-data="form"
                  name="scc_base_material_item_display"
                  @close-quicksearch="getItemObj"
                />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="2">
              <!-- 供应商管理 -->
              <el-form-item
                :label="$t('purchaseDemand.supUserNickname')"
                :label-width="formLabelWidth"
                prop="supUserNickname"
              >
                <QuickSearch
                  :show-input="form.supUserNickname"
                  show-key="nickname"
                  :scope-data="form"
                  name="scc_rbac_user_display"
                  @close-quicksearch="getUserObj"
                />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="2">
              <!-- 策略负责 -->
              <el-form-item
                :label="$t('purchaseDemand.strategyUserNickname')"
                :label-width="formLabelWidth"
                prop="strategyUserNickname"
              >
                <QuickSearch
                  :show-input="form.strategyUserNickname"
                  show-key="nickname"
                  :scope-data="form"
                  name="scc_rbac_user_display"
                  @close-quicksearch="getUserObj2"
                />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="2">
              <!-- 采购履行 -->
              <el-form-item
                :label="$t('purchaseDemand.performUserNickname')"
                :label-width="formLabelWidth"
                prop="performUserNickname"
              >
                <QuickSearch
                  :show-input="form.performUserNickname"
                  show-key="nickname"
                  :scope-data="form"
                  name="scc_rbac_user_display"
                  @close-quicksearch="getUserObj3"
                />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="2">
              <el-form-item
                :label="$t('contractMod.startDate')"
                :label-width="formLabelWidth"
                prop="startDate"
              >
                <el-date-picker
                  v-model="form.startDate"
                  type="date"
                />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="2">
              <el-form-item
                :label="$t('contractMod.endDate')"
                :label-width="formLabelWidth"
                prop="endDate"
              >
                <el-date-picker
                  v-model="form.endDate"
                  type="date"
                />
              </el-form-item>
            </srm-col>
          </srm-row>
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
            @click="addOneItem"
          >
            {{ $t("common.confirm") }}
          </el-button>
        </div>
      </srm-dialog>
    </el-main>
  </el-container>
</template>
<script>
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { parseTime } from '@/utils'
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'

export default {
  name: 'ContractPaymentTypeList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    QuickSearch,
    MImport,
    OrganizationSelector
  },
  // mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      name: 'materialAssignRuleTable',
      tableName: 'materialAssignRuleTable',
      iModal: {
        title: this.$t('common.import'),
        extraData: {},
        upLoadUrl: '/api-sup-ce/division/divisionMaterial/importExcel'
      },
      reviewFormNumber: '',
      gridData: [],
      pageSize: 15,
      gridId: 'list',
      selectList: [],
      currentRow: null,
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      dialogFormVisible: false,
      formLabelWidth: '100px',
      preArr: [],
      form: {
        divisionMaterialId: null,
        orgId: '',
        orgCode: '',
        orgName: '',
        materialId: '',
        materialCode: '',
        materialName: '',
        organizationId: '',
        organizationCode: '',
        organizationName: '',
        supUserId: '',
        supUserNickname: '',
        strategyUserId: '',
        strategyUserNickname: '',
        performUserId: '',
        performUserNickname: '',
        startDate: '',
        endDate: ''
      },
      rules: {
        orgId: [{ required: true, message: this.$t('purchaseDemand.orgIdTips') }], // 请输入业务实体
        organizationId: [{ required: true, message: this.$t('purchaseDemand.invOrg') }], // 请输入库存组织
        materialCode: [{ required: true, message: this.$t('purchaseDemand.materialCodeTips') }], // 请输入物料编号
        supUserNickname: [{ required: true, message: this.$t('purchaseDemand.supUserNicknameTips') }],
        strategyUserNickname: [{ required: true, message: this.$t('purchaseDemand.strategyUserNickname') }],
        performUserNickname: [{ required: true, message: this.$t('purchaseDemand.performUserNickname') }],
        startDate: [{ required: true, message: this.$t('purchaseDemand.startDate') }],
        endDate: [{ required: true, message: this.$t('purchaseDemand.endDate') }]
      },
      queryParam: {}
    }
  },
  created () {
    let _this = this
    this.tableHeader = [
      {
        prop: 'orgName',
        label: _this.$t('purchaseDemand.businessEntity'),
        width: 150
      }, // 业务实体
      {
        prop: 'organizationName',
        label: _this.$t('purchaseDemand.invOrg'),
        width: 150
      }, // 库存组织
      {
        prop: 'materialCode',
        label: _this.$t('purchaseDemand.itemCode'),
        width: 120
      }, // 物料编号
      {
        prop: 'materialName',
        label: _this.$t('purchaseDemand.itemName'),
        minWidth: 150
      }, // 物料名称
      {
        prop: 'supUserNickname',
        label: _this.$t('purchaseDemand.supUserNickname'),
        width: 110
      }, // 供应商管理
      {
        prop: 'strategyUserNickname',
        label: _this.$t('purchaseDemand.strategyUserNickname'),
        width: 100
      }, // 策略负责
      {
        prop: 'performUserNickname',
        label: _this.$t('purchaseDemand.performUserNickname'),
        width: 100
      }, // 采购履行
      {
        prop: 'startDate',
        label: _this.$t('purchaseDemand.startDate'), // 生效日期
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'endDate',
        label: _this.$t('purchaseDemand.endDate'), // 失效日期
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'lastUpdatedUserName', // lastUpdatedBy
        label: _this.$t('purchaseDemand.lastUpdatedBy'),
        width: 100
      }, // 更新人
      {
        prop: 'lastUpdateDate',
        label: () => _this.$t('purchaseDemand.lastUpdateDate'), // 更新时间
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      // { prop: "payExplain", label: _this.$t('contractMod.payExplain'), minWidth: 150 },
      {
        prop: 'operation',
        label: _this.$t('common.operation'),
        width: 100,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          {
            callback: function (row) {
              this.editTab('edit', row)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.edit')
            }
          },
          {
            callback: function (row) {
              this.deleteOne(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.delete')
            }
          }
        ]
      }
    ]
    this.defaultTableHeader = this.tableHeader

    this.$nextTick(() => {
      this.getQuerydata()
    })
    this.preArr = [
      {
        prop: 'orgId',
        label: this.$t('purchaseDemand.businessEntity'), // 业务实体
        type: 'OUorganizationSelector'
      },
      {
        prop: 'organizationId',
        label: this.$t('purchaseDemand.invOrg'), // 库存组织
        type: 'INVorganizationSelector',
        parentId: 'orgId'
      },
      {
        prop: 'materialCode',
        label: this.$t('purchaseDemand.itemCode'),
        type: 'quicksearch',
        showKey: 'materialCode',
        name: 'scc_base_material_item_display'
      }, // 物料编号
      {
        prop: 'supUserNickname',
        label: this.$t('purchaseDemand.supUserNickname'),
        type: 'quicksearch',
        showKey: 'nickname',
        name: 'scc_rbac_user_display'
      }, // 供应商管理
      {
        prop: 'strategyUserNickname',
        label: this.$t('purchaseDemand.strategyUserNickname'),
        type: 'quicksearch',
        showKey: 'nickname',
        name: 'scc_rbac_user_display'
      }, // 策略负责
      {
        prop: 'performUserNickname',
        label: this.$t('purchaseDemand.performUserNickname'),
        type: 'quicksearch',
        showKey: 'nickname',
        name: 'scc_rbac_user_display'
      }, // 采购履行
      {
        prop: 'enable',
        label: this.$t('purchaseDemand.enable'), // 是否生效
        type: 'dict',
        code: 'YES_OR_NO'

      }
    ]
  },
  methods: {
    getQuerydata (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange (val) {
      this.currentRow = val
    },
    editTab (type, row) {
      if (type === 'add') {
        // 新增
        for (let i in this.form) {
          this.form[i] = null
        }
        this.form.startDate = new Date()
      } else {
        // 修改
        for (let i in this.form) {
          this.form[i] = row[i]
        }
      }
      this.dialogFormVisible = true
    },
    // 选择业务实体
    addOrgObj (e, value, scope) {
      scope.orgId = e ? e.organizationId : ''
      scope.orgCode = e ? e.organizationCode : ''
      scope.orgName = e ? e.organizationName : ''
    },
    // 选择库存组织
    addOrgHandle (e, value, scope) {
      scope.organizationId = e ? e.organizationId : ''
      scope.organizationCode = e ? e.organizationCode : ''
      scope.organizationName = e ? e.organizationName : ''
    },
    selectHandler (node, value, scope) {
      this.form.orgId = node.organizationId
      this.form.orgCode = node.organizationCode
      this.form.orgName = node.organizationName
      this.form.organizationId = null
    },
    selectHandler2 (node, value, scope) {
      this.form.organizationId = node.organizationId
      this.form.organizationCode = node.organizationCode
      this.form.organizationName = node.organizationName
    },
    addOneItem () {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.form.startDate = this.form.startDate
            ? parseTime(this.form.startDate, '{y}-{m}-{d}')
            : null
          this.form.endDate = this.form.endDate
            ? parseTime(this.form.endDate, '{y}-{m}-{d}')
            : null
          let urlById =
            '/api-sup-ce/division/divisionMaterial/saveDivisionMaterial'
          if (this.form.divisionMaterialId) {
 urlById =
              '/api-sup-ce/division/divisionMaterial/updateDivisionMaterial'
}
          this.$http({
            url: urlById,
            method: 'POST',
            data: [this.form],
            loading: true
          })
            .then(data => {
              this.dialogFormVisible = false
              this.$message.success(this.$t('common.successSave'))
              this.getQuerydata()
            })
            .catch(err => {
              console.log(err)
            })
        } else {
          return false
        }
      })
    },
    getItemObj (val, scope) {
      scope.materialId = val ? val.materialId : ''
      scope.materialCode = val ? val.materialCode : ''
      scope.materialName = val ? val.materialName : ''
    },
    getUserObj (val, scope) {
      scope.supUserId = val ? val.userId : ''
      scope.supUserName = val ? val.username : ''
      scope.supUserNickname = val ? val.nickname : ''
    },
    getUserObj2 (val, scope) {
      scope.strategyUserId = val ? val.userId : ''
      scope.strategyUserName = val ? val.username : ''
      scope.strategyUserNickname = val ? val.nickname : ''
    },
    getUserObj3 (val, scope) {
      scope.performUserId = val ? val.userId : ''
      scope.performUserName = val ? val.username : ''
      scope.performUserNickname = val ? val.nickname : ''
    },
    downloadTemplate () {
      // 下载模板
      downloadFileLink(
        '/api-sup-ce/division/divisionMaterial/importModelDownload',
        `导入模板_${new Date().getTime()}.xls`
      )
    },
    uploadSuccess (val) {
      if (val && val.code === '0') this.getQuerydata()
    },
    deleteOne (row) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.$http({
            url:
              '/api-sup-ce/division/divisionMaterial/deleteDivisionMaterial',
            method: 'GET',
            params: { divisionMaterialId: row.divisionMaterialId },
            loading: true
          })
            .then(data => {
              this.$message.success(this.$t('common.successDelete'))
              this.getQuerydata()
            })
            .catch(err => {
              console.log(err)
            })
        })
        .catch(() => {})
    }
  }
}
</script>
<style scoped lang="scss"></style>
