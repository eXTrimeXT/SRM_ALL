<template>
  <el-container class="flex-container-aside-biding the_orgList_wrapper">
    <el-aside width="240px">
      <div class="tree-content">
        <el-tree
          ref="tree"
          node-key="companyDeptId"
          :data="deptTree.data"
          :props="deptTree.props"
        />
      </div>
    </el-aside>
    <el-container direction="vertical">
      <el-main
        style="
          flex-grow: 1;
          display: flex;
          flex-direction: column;
          position: relative;
        "
      >
        <FormWrapper
          :form-array="queryForm"
          :pre-form-obj="preFormObj"
          :col-length="colSpan"
          @getFormData="getQueryData"
        />

        <MainHeader
          :l-span="22"
          :r-span="2"
        >
          <template slot="left">
            <el-button
              type="primary"
              @click="orgControlHandle('add')"
            >
              <!-- 新增 -->
              {{ $t('common.add') }}
            </el-button>
          </template>
        </MainHeader>
        <TableView
          :ref="gridId"
          :table-data="depTableData"
          :table-header="tableHeader"
          :page-size="pageInfo.pageSize"
          :pre-query-data="queryParam"
          :com-active="$attrs['changeTab']"
          url="/api-base/base/org_company_dept/listPage"
        />

        <!-- 新增 编辑弹框区域-->
        <srm-dialog
          :title="dialogTitle"
          :visible.sync="dialogFormVisible"
          :close-on-click-modal="false"
          size="middle"
        >
          <el-form
            ref="orgform1"
            :model="orgDataForm"
            :rules="rules"
            label-position="top"
          >
            <el-row :gutter="32">
              <el-col :span="12">
                <!-- 部门编码 -->
                <el-form-item
                  :label="$t('dataConfMod.deptid')"
                  prop="deptCode"
                >
                  <el-input
                    v-model="orgDataForm.deptCode"
                    :disabled="curOpt === 'edit'"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <!-- 部门名称 -->
                <el-form-item
                  :label="$t('dataConfMod.descr')"
                  prop="deptName"
                >
                  <el-input v-model="orgDataForm.deptName" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <!-- 上级部门 -->
                <el-form-item
                  :label="$t('dataConfMod.partDescrChn')"
                  prop="parentDeptCode"
                >
                  <DictSelect
                    v-model="orgDataForm.parentDeptCode"
                    :code="organizationId"
                    :dict-class="dictClass"
                    @change-value="changeDeptHandle"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <!-- 生效日期 -->
                <el-form-item
                  :label="$t('dataConfMod.effectDate')"
                  prop="startDate"
                >
                  <el-date-picker
                    v-model="orgDataForm.startDate"
                    type="date"
                    style="width: 100%"
                    value-format="yyyy-MM-dd"
                    :placeholder="$t('dataConfMod.startDate')"
                    :format="$formatDatePicker"
                  />
                </el-form-item>
              </el-col>
              <!-- 生效状态 -->
              <el-col :span="12">
                <el-form-item
                  :label="$t('dataConfMod.region')"
                  prop="region"
                >
                  <dict-select
                    v-model="orgDataForm.effectiveStatus"
                    code="YES_OR_NO"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <!-- 失效日期 -->
                <el-form-item
                  :label="$t('dataConfMod.endDate')"
                  prop="endDate"
                >
                  <el-date-picker
                    v-model="orgDataForm.endDate"
                    type="date"
                    style="width: 100%"
                    value-format="yyyy-MM-dd"
                    :placeholder="$t('dataConfMod.endDate')"
                    :format="$formatDatePicker"
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
              <!-- 取 消 -->
              {{ $t('common.cancel') }}
            </el-button>
            <el-button
              type="primary"
              @click="confirmSave('orgform1')"
            >
              <!-- 确 定 -->
              {{ $t('common.confirm') }}
            </el-button>
          </div>
        </srm-dialog>
      </el-main>
    </el-container>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import createTreeClass from '@/utils/tree-utils'
import { parseTime } from '@/utils'
import { createDictClass } from '@/library/utils/dict/dict-utils'
import { organizationSetting } from 'modb@/basicSetting/api/basicSetting'

export default {
  name: 'DeptMaintain',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  data () {
    return {
      dictClass: createDictClass().setCustomSelectType('COMPANY_DEPT'),
      organizationId: null,
      organizationName: null,
      organizationCode: null,
      deptTree: {
        loading: false,
        data: [],
        props: {
          children: 'children',
          label: 'deptName'
        }
      },
      queryForm: [],
      preFormObj: {
        organizationName: null
      },
      colSpan: 2,
      gridId: 'deptMaintain',
      depTableData: [],
      tableHeader: [],
      pageInfo: {
        total: 0,
        pageNum: 1,
        pageSize: 15
      },
      showFilterBar: 1,
      orgDataForm: {
        organizationId: this.organizationId, // 组织Id
        organizationName: this.organizationName, // 组织名称
        organizationCode: this.organizationCode, // 组织编码
        companyDeptId: null, // 部门ID
        deptName: null, // 部门名称
        deptCode: null, // 部门编码
        deptNameEn: '', // 部门英文名
        deptNameShort: '', // 部门简称
        deptLevel: null, // 部门层级
        startDate: parseTime(
          new Date(),
          '{y}-{m}-{d}',
          true
        ), // 失效时间
        endDate: null, // 截止时间
        effectiveStatus: '', // 生效状态
        parentDeptName: '', // 上层部门名称
        parentDeptCode: '' // 上层部门编码
      },
      rules: {
        organizationCode: [{ required: true, message: this.$t('dataConfMod.msgOrgCode') }], // '请输入组织编码'
        organizationName: [{ required: true, message: this.$t('dataConfMod.msgOrgName') }], // '请输入组织名称'
        startDate: [{ required: true, message: this.$t('dataConfMod.msgStartDate') }], // '请输生效日期'
        effectiveStatus: [{ required: true, message: this.$t('dataConfMod.msgRegion') }] // 请选择生效状态
      },
      queryParam: {},
      curOpt: 'add',
      dialogTitle: this.$t('dataConfMod.addOrg'), // '新增组织'
      dialogFormVisible: false
    }
  },
  async created () {
    this.queryForm = [
      // '公司名称'
      {
        prop: 'organizationName',
        label: () => this.$t('dataConfMod.ceeaCompanyName'),
        disabled: true
      },
      // 部门名称
      {
        prop: 'deptName',
        label: () => this.$t('dataConfMod.descr')
      }
    ]
    this.tableHeader = [
      // '公司名称'
      {
        prop: 'organizationName',
        label: () => this.$t('dataConfMod.ceeaCompanyName'),
        minWidth: '160'
      },
      // '部门编码'
      {
        prop: 'deptCode',
        label: () => this.$t('dataConfMod.deptid'),
        minWidth: '160'
      },
      // '部门名称'
      {
        prop: 'deptName',
        label: () => this.$t('dataConfMod.descr'),
        minWidth: '160'
      },
      // '上级编码'
      {
        prop: 'parentDeptCode',
        label: () => this.$t('dataConfMod.partDeptidChn'),
        minWidth: '160'
      },
      // '上级名称'
      {
        prop: 'parentDeptName',
        label: () => this.$t('dataConfMod.parentDeptName'),
        minWidth: '160'
      },
      // '生效日期'
      {
        prop: 'startDate',
        label: () => this.$t('dataConfMod.startDate'),
        minWidth: '160',
        dataType: 'dateTime'
      },
      // '失效日期'
      {
        prop: 'endDate',
        label: () => this.$t('dataConfMod.endDate'),
        minWidth: '130',
        dataType: 'dateTime'
      },
      // '更新时间'
      {
        prop: 'lastUpdateDate',
        label: () => this.$t('common.updateTime'),
        minWidth: '130',
        dataType: 'dateTime'
      },
      // '更新人'
      {
        prop: 'lastUpdatedUserName',
        label: () => this.$t('common.updatePeople'),
        minWidth: '100'
      },
      // '操作'
      {
        label: () => this.$t('common.operation'),
        width: '120',
        fixed: 'right',
        editType: 'none',
        showType: 'buttons',
        btnStyle: 'text',
        buttons: [
          // 编辑
          {
            code: 'base:organizationSetting:editOrgData',
            callback: row => this.orgControlHandle('edit', row),
            formattor: () => this.$t('common.edit')
          }
        ]
      }
    ]

    this.organizationId = this.$attrs.params.organizationId
    this.organizationName = this.$attrs.params.organizationName
    this.organizationCode = this.$attrs.params.organizationCode
    this.preFormObj.organizationName = this.organizationName

    this.dictClass.loadCustomSelectType(this.organizationId)

    // 分页查询业务实体下的部门
    this.$nextTick(() => {
      this.getQueryData()
    })

    this.getDeptTree()
  },
  methods: {
    initOrgDataForm () {
      this.orgDataForm = {
        organizationId: this.organizationId, // 组织Id
        organizationName: this.organizationName, // 组织名称
        organizationCode: this.organizationCode, // 组织编码
        companyDeptId: null, // 部门ID
        deptName: null, // 部门名称
        deptCode: null, // 部门编码
        deptNameEn: '', // 部门英文名
        deptNameShort: '', // 部门简称
        deptLevel: null, // 部门层级
        startDate: parseTime(
          new Date(),
          '{y}-{m}-{d}',
          true
        ), // 失效时间
        endDate: null, // 截止时间
        effectiveStatus: '', // 生效状态
        parentDeptName: '', // 上层部门名称
        parentDeptCode: '' // 上层部门编码
      }
    },
    // 查询
    getQueryData (value) {
      this.queryParam = Object.assign({}, {}, value, {
        organizationId: this.organizationId
      })
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    // 加载子节点
    getDeptTree () {
      this.deptTree.loading = true
      organizationSetting.listAll({
        organizationId: this.organizationId
      })
        .then(res => {
          if (res && res.data) {
            const orgTreeData = res.data
            const treeClass = createTreeClass('deptCode', 'parentDeptCode')
            const treeData = treeClass.buildTree(orgTreeData)
            this.deptTree.data = treeData
          }
        })
        .finally(() => {
          this.deptTree.loading = false
        })
    },
    changeDeptHandle (deptCode, obj) {
      if (obj && obj.element) {
        this.orgDataForm.parentDeptName = obj.element.deptName
      } else {
        this.orgDataForm.parentDeptName = ''
      }
    },

    reloadDictClass () {
      this.dictClass.loadCustomSelectType(this.organizationId)
    },

    async orgControlHandle (type, data) {
      this.initOrgDataForm()
      this.reloadDictClass()
      if (type === 'add') {
        // 新增
        this.dialogTitle = this.$t('dataConfMod.addDeptInfo') // '新增部门信息'
        this.curOpt = 'add'

        this.dialogFormVisible = true
      } else {
        // 修改
        this.dialogTitle = this.$t('common.edit') // '编辑部门信息'
        this.curOpt = 'edit'
        this.getDataForEdit(data.companyDeptId)
        this.dialogFormVisible = true
      }
    },

    // 编辑之前先获取数据
    getDataForEdit (companyDeptId) {
      organizationSetting.get(companyDeptId).then(res => {
        if (res.data) {
          // 返回数据处理
          const orgDataForm = res.data
          orgDataForm.startDate = orgDataForm.startDate ? parseTime(
            this.$dayjs(orgDataForm.startDate).valueOf(),
            '{y}-{m}-{d}',
            true
          ) : null
          orgDataForm.endDate = orgDataForm.endDate ? parseTime(
            this.$dayjs(orgDataForm.endDate).valueOf(),
            '{y}-{m}-{d}',
            true
          ) : null
          this.orgDataForm = orgDataForm
        }
      })
    },

    async confirmSave (formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          if (this.curOpt === 'add') {
            organizationSetting.add(this.orgDataForm).then(res => {
              if (!res.data) {
                // 分页查询业务实体下的部门
                this.$nextTick(() => {
                  this.getQueryData()
                })

                this.getDeptTree()
                this.dialogFormVisible = false
              }
            })
          } else {
            organizationSetting.update(this.orgDataForm).then(res => {
              if (!res.data) {
                // 分页查询业务实体下的部门
                this.$nextTick(() => {
                  this.getQueryData()
                })

                this.getDeptTree()
                this.dialogFormVisible = false
              }
            })
          }
        } else {
          return false
        }
      })
    }
  }
}
</script>
<style scoped lang="scss"></style>
