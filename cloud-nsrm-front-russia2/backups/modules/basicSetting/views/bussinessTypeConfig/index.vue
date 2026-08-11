<template>
  <el-container
    class="flex-container-notab the_contractPaymentTypeList_wrapper"
    direction="vertical"
  >
    <el-main>
      <form-wrapper
        :form-array="preArr"
        :init-active="initActive"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />
      <main-header>
        <template slot="left">
          <el-button
            type="primary"
            @click="editTab('add')"
          >
            {{ $t('common.add') }}
          </el-button>
          <m-import
            ref="import"
            :title="iModal.title"
            :extra-data="iModal.extraData"
            :up-load-url="iModal.upLoadUrl"
            :show-success-deal="true"
            @downloadTemplate="downloadTemplate"
            @handleSuccess="uploadSuccess"
          />
        </template>
      </main-header>

      <table-view
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        url="/api-base/businessType/listPageByParam"
      />
      <!-- 业务类型配置 -->
      <srm-dialog
        :title="$t('dataConfMod.businessTypeConf')"
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
          <el-row type="flex">
            <el-col>
              <!-- 供应商地点名称 -->
              <el-form-item
                :label="$t('dataConfMod.vendorSite')"
                :label-width="formLabelWidth"
                prop="vendorSiteCode"
              >
                <DictSelect
                  v-model="form.vendorSiteCode"
                  code="VENDOR_SITE_CODE"
                  filterable
                  clearable
                />
              </el-form-item>
            </el-col>
            <el-col>
              <!-- 事业部 -->
              <el-form-item
                :label="$t('dataConfMod.businessDivision')"
                :label-width="formLabelWidth"
                prop="divisionId"
              >
                <DictSelect
                  v-model="form.divisionId"
                  code="DIVISION"
                  filterable
                  clearable
                  @change="setDivision"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row type="flex">
            <el-col>
              <!-- 单据类型 -->
              <el-form-item
                :label="$t('bidMod.billType')"
                :label-width="formLabelWidth"
                prop="paymentDocumentType"
              >
                <DictSelect
                  v-model="form.paymentDocumentType"
                  code="PAYMENT_DOCUMENT_TYPE"
                  filterable
                  clearable
                />
              </el-form-item>
            </el-col>
            <el-col>
              <!-- 业务类型 -->
              <el-form-item
                :label="$t('dataConfMod.businessType')"
                :label-width="formLabelWidth"
                prop="businessType"
              >
                <DictSelect
                  v-model="form.businessType"
                  code="BUSINESS_TYPE"
                  filterable
                  clearable
                  @change="setBusinessType"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row type="flex">
            <el-col>
              <!-- 是否代付 -->
              <el-form-item
                :label="$t('dataConfMod.ifPayAgent')"
                :label-width="formLabelWidth"
              >
                <el-checkbox
                  v-model="form.ifPayAgent"
                  true-label="Y"
                  false-label="N"
                />
              </el-form-item>
            </el-col>
            <el-col>
              <!-- 是否启用 -->
              <el-form-item
                :label="$t('dataConfMod.enabledUse')"
                :label-width="formLabelWidth"
                prop="duty"
              >
                <el-checkbox
                  v-model="form.enabled"
                  true-label="Y"
                  false-label="N"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row type="flex">
            <el-col>
              <!-- 备注 -->
              <el-form-item
                :label="$t('dataConfMod.remark')"
                :label-width="formLabelWidth"
              >
                <el-input v-model="form.remarks" />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <div
          slot="footer"
          class="dialog-footer"
        >
          <el-button @click="dialogFormVisible = false">
            {{ $t('common.cancel') }}
          </el-button>
          <el-button
            type="primary"
            @click="addOneItem"
          >
            {{ $t('common.confirm') }}
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

export default {
  name: 'BussinessTypeConfig',

  components: {
    TableView,
    MainHeader,
    FormWrapper,
    MImport
  },

  provide () {
    return { context: this }
  },

  data () {
    return {
      name: 'categoryAssignRuleTable',
      tableName: 'categoryAssignRuleTable',
      iModal: {
        title: this.$t('common.import'),
        extraData: {
          sourceType: 'WEB_APP',
          uploadType: 'FASTDFS',
          fileModular: 'base',
          fileFunction: 'accountAccess',
          fileType: 'excel'
        },
        upLoadUrl: '/api-base/businessType/importExcel'
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
      initActive: true,
      dialogFormVisible: false,
      formLabelWidth: '100px',
      preArr: [
        {
          prop: 'vendorSiteCode',
          label: () => this.$t('dataConfMod.vendorSite'), // 供应商地点名称
          type: 'dict',
          code: 'VENDOR_SITE_CODE'
        },
        {
          prop: 'businessType',
          label: () => this.$t('dataConfMod.businessType'), // 业务类型
          type: 'dict',
          code: 'BUSINESS_TYPE'
        },
        {
          prop: 'divisionId',
          label: () => this.$t('dataConfMod.businessDivision'), // 事业部
          type: 'dict',
          code: 'DIVISION'
        }
      ],
      form: {
        bussinessTypeId: null,
        vendorSiteCode: null,
        paymentDocumentType: null,
        businessType: null,
        businessTypeName: null,
        division: null,
        divisionId: null,
        ifPayAgent: 'N',
        enabled: 'Y',
        remarks: null
      },
      rules: {
        vendorSiteCode: [
          {
            required: true,
            message: this.$t('dataConfMod.msgSelectVendorSite') // 请选择供应商地点名称
          }
        ],
        divisionId: [
          {
            required: true,
            message: this.$t('dataConfMod.msgSelDivision') // 请选择事业部
          }
        ],
        paymentDocumentType: [
          {
            required: true,
            message: this.$t('dataConfMod.msgSelPaymentDocType') // 请选择单据类型
          }
        ],
        businessType: [
          {
            required: true,
            message: this.$t('dataConfMod.msgBusinessType') // 请选择业务类型
          }
        ]
      },
      queryParam: {},
      dutyList: [],
      businessTypeList: [],
      pubRangeList: [],
      paymentType: [],
      projectTypeList: [],
      ableSelectTreeNodes: [],
      divisionList: []
    }
  },

  created () {
    let _this = this
    this.tableHeader = [
      {
        prop: 'vendorSiteCode',
        label: () => this.$t('dataConfMod.vendorSite'), // 供应商地点名称
        minWidth: 150
      },
      {
        prop: 'division',
        label: () => this.$t('dataConfMod.businessDivision'), // 事业部
        minWidth: 150
      },
      {
        prop: 'paymentDocumentType',
        label: () => this.$t('bidMod.billType'), // 单据类型
        width: 120,
        dataType: 'dict',
        code: 'PAYMENT_DOCUMENT_TYPE'
      },
      {
        prop: 'businessType',
        label: () => this.$t('dataConfMod.businessTypeCode'), // 业务类型编码
        minWidth: 120
      },
      {
        prop: 'businessTypeName',
        label: () => this.$t('dataConfMod.businessTypeDesc'), // 业务类型描述
        minWidth: 120
      },
      {
        prop: 'ifPayAgent',
        label: () => this.$t('dataConfMod.ifPayAgent'), // 是否代付
        width: 100,
        formattor: val => this.$getDictLabel('YES_OR_NO', val)
      },
      {
        prop: 'enabled',
        label: () => this.$t('dataConfMod.enabledUse'), // 是否启用
        width: 100,
        formattor: val => this.$getDictLabel('YES_OR_NO', val)
      },
      {
        prop: 'remarks',
        label: () => this.$t('dataConfMod.remark'), // 备注
        minWidth: 120
      },
      {
        prop: 'lastUpdateDate',
        label: () => this.$t('common.updateTime'), // 更新时间
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'operation',
        label: _this.$t('common.operation'),
        width: 100,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          {
            callback: row => this.editTab('edit', row),
            formattor: () => this.$t('common.edit')
          },
          {
            callback: row => this.deleteOne(row),
            formattor: () => this.$t('common.delete')
          }
        ]
      }
    ]
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },

  methods: {
    getQuerydata () {
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
        this.form.enabled = 'Y'
        this.form.ifPayAgent = 'N'
      } else {
        // 修改
        for (let i in this.form) {
          this.form[i] = row[i]
        }
      }
      this.dialogFormVisible = true
    },
    downloadTemplate () {
      // 下载模板
      downloadFileLink(
        '/api-base/businessType/importModelDownload',
        this.$t('dataConfMod.importTemplate') + `${new Date().getTime()}.xlsx`
      )
    },
    uploadSuccess (val) {
      if (val && val.code === '0') {
        this.getQuerydata()
      }
    },
    syncFilterParams (values) {
      this.queryParam = values
    },
    setDivision (val) {
      if (val) {
        this.form.division = this.$getDictLabel('DIVISION', val)
      }
    },
    setBusinessType (val) {
      if (val) {
        this.form.businessTypeName = this.$getDictLabel('BUSINESS_TYPE', val)
      }
    },
    addOneItem () {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.$http({
            url: '/api-base/businessType/saveOrUpdateBussinessType',
            method: 'POST',
            data: this.form,
            loading: true
          })
            .then(() => {
              this.dialogFormVisible = false
              this.$message.success(this.$t('common.successSave')) // 保存成功
              this.getQuerydata()
            })
        } else {
          return false
        }
      })
    },
    getCategoryObj (val, scope) {
      scope.categoryId = val ? val.categoryId : ''
      scope.categoryCode = val ? val.categoryCode : ''
      scope.categoryName = val ? val.categoryName : ''
    },
    getUserObj2 (val, scope) {
      scope.strategyUserId = val ? val.userId : ''
      scope.strategyUserName = val ? val.username : ''
      scope.strategyUserNickname = val ? val.nickname : ''
    },
    getUserObj3 (val, scope) {
      scope.personInChargeUserId = val ? val.userId : ''
      scope.personInChargeUsername = val ? val.username : ''
      scope.personInChargeNickname = val ? val.nickname : ''
    },
    deleteOne (row) {
      // 当前操作将删除数据，确认是否删除数据？
      this.$confirm(this.$t('common.ifDeleteData'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.$http({
            url: '/api-base/businessType/batchDelete',
            method: 'POST',
            data: [row.bussinessTypeId],
            loading: true
          })
            .then(() => {
              this.$message.success(this.$t('common.successDelete')) // 删除成功
              this.getQuerydata()
            })
        })
    }
  }
}
</script>
