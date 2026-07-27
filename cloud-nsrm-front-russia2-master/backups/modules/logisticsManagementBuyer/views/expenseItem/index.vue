<template>
  <el-container
    class="flex-container-notab the_contractPaymentTypeList_wrapper"
    direction="vertical"
  >
    <el-main>
      <form-wrapper
        :form-array="preArr"
        :init-active="true"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />
      <main-header
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <!--新增-->
          <el-button
            type="primary"
            @click="editTab('add')"
          >
            {{ $t("common.add") }}
          </el-button>

          <!--保存-->
          <el-button
            type="primary"
            @click="batchSave"
          >
            {{ $t("common.save") }}
          </el-button>

          <!--删除-->
          <el-button
            type="primary"
            @click="batchDelete"
          >
            {{ $t("common.delete") }}
          </el-button>

          <!--生效-->
          <el-button
            type="primary"
            @click="doBatchEffect"
          >
            {{ $t("common.active") }}
          </el-button>

          <!--失效-->
          <el-button
            type="primary"
            @click="doBatchIneffect"
          >
            {{ $t("common.inactive") }}
          </el-button>

          <!--导入-->
          <m-import
            ref="import"
            style="display: inline-block;margin: 0 10px;"
            :title="iModal.title"
            :up-load-url="iModal.upLoadUrl"
            :extra-data="extraData"
            @downloadTemplate="downloadTemplate"
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
        :checkbox="true"
        :check-change="handleSelectionChange"
        url="/api-pd/logistics/expense-item/listPageByParam"
        @afterQuery="afterQuery"
      >
        <template #businessModeCode="props">
          <DictSelect
            v-model="props.scope.row.businessModeCode"
            code="BUSINESS_MODE"
            :disabled="!(props.scope.row.editable || props.scope.row.status === 'DRAFT')"
          />
        </template>
        <template #transportModeCode="props">
          <DictSelect
            v-model="props.scope.row.transportModeCode"
            code="TRANSPORT_MODE"
            filterable
            clearable
            :disabled="!(props.scope.row.editable || props.scope.row.status === 'DRAFT')"
          />
        </template>
        <template #legCode="props">
          <DictSelect
            v-model="props.scope.row.legCode"
            code="LEG"
            :disabled="!(props.scope.row.editable || props.scope.row.status === 'DRAFT')"
          />
        </template>
        <template #templateHeadId="props">
          <el-select
            v-model="props.scope.row.templateHeadId"
            :disabled="!(props.scope.row.editable || props.scope.row.status === 'DRAFT')"
            @change="setchargeTemplate(props.scope.row)"
          >
            <el-option
              v-for="item in templateList"
              :key="item.value"
              :label="item.label"
              :value="item.templateHeadId"
            />
          </el-select>
        </template>
        <template #chargeCode="props">
          <DictSelect
            v-model="props.scope.row.chargeCode"
            code="CHARGE_NAME"
            :disabled="!(props.scope.row.editable || props.scope.row.status === 'DRAFT')"
            @change="setchargeName2(props.scope.row)"
          />
        </template>
      </table-view>

      <!-- 费用项定义 -->
      <srm-dialog
        v-el-drag-dialog
        :title="$t('route.expenseItem')"
        size="large"
        :visible.sync="dialogFormVisible"
        :close-on-click-modal="false"
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
              <!-- 业务模式 -->
              <el-form-item
                :label="$t('logisticsMod.businessMode')"
                :label-width="formLabelWidth"
                prop="businessModeCode"
              >
                <DictSelect
                  v-model="form.businessModeCode"
                  code="BUSINESS_MODE"
                  filterable
                  clearable
                />
              </el-form-item>
            </el-col>
            <el-col>
              <!-- 运输方式 -->
              <el-form-item
                :label="$t('bid_mod.transportType')"
                :label-width="formLabelWidth"
                prop="transportModeCode"
              >
                <DictSelect
                  v-model="form.transportModeCode"
                  code="TRANSPORT_MODE"
                  filterable
                  clearable
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row type="flex">
            <el-col>
              <el-form-item
                label="leg"
                :label-width="formLabelWidth"
                prop="legCode"
              >
                <DictSelect
                  v-model="form.legCode"
                  code="LEG"
                  filterable
                  clearable
                />
              </el-form-item>
            </el-col>
            <el-col>
              <!-- 费用项 -->
              <el-form-item
                :label="$t('logisticsMod.expenseItem')"
                :label-width="formLabelWidth"
                prop="chargeCode"
              >
                <DictSelect
                  v-model="form.chargeCode"
                  code="CHARGE_NAME"
                  filterable
                  clearable
                  @change="setchargeName"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <div
          slot="footer"
          class="dialog-footer"
        >
          <el-button
            type="primary"
            @click="addOneItem"
          >
            {{ $t("common.confirm") }}
          </el-button>
          <el-button @click="dialogFormVisible = false">
            {{ $t("common.cancel") }}
          </el-button>
        </div>
      </srm-dialog>
    </el-main>
  </el-container>
</template>

<script>
import ExportExcel from 'lib@/components/export-excel'
import { downloadFileLinkByPost } from 'lib@/utils/file'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { parseTime } from '@/utils'
import QuickSearch from 'lib@/components/QuickSearch'
import MImport from 'lib@/components/import'

export default {
  name: 'ExpenseItem',
  components: {
    TableView,
    MainHeader,
    ExportExcel,
    FormWrapper,
    QuickSearch,
    MImport
  },
  provide () {
    return { context: this }
  },
  data () {
    return {
      name: 'categoryAssignRuleTable',
      extraData: {
        sourceType: 'WEB_APP',
        uploadType: 'FASTDFS',
        fileModular: 'base',
        fileFunction: 'accountAccess',
        fileType: 'excel'
      },
      iModal: {
        title: this.$t('common.import'),
        upLoadUrl: '/api-pd/logistics/expense-item/importExcel'
      },
      pageSize: 15,
      gridId: 'list',
      selectList: [],
      currentRow: null,
      tableHeader: [],
      tableData: [],
      dialogFormVisible: false,
      formLabelWidth: '100px',
      preArr: [
        // 费用项编码
        { prop: 'chargeCode', label: this.$t('logisticsMod.chargeCode') },
        // 费用项名称
        { prop: 'chargeName', label: this.$t('logisticsMod.chargeName') },
        // 业务模式
        {
          prop: 'businessModeCode',
          label: this.$t('logisticsMod.businessMode'),
          type: 'dict',
          code: 'BUSINESS_MODE'
        },
        // 运输方式
        { prop: 'transportModeCode',
          label: this.$t('bid_mod.transportType'),
          type: 'dict',
          code: 'TRANSPORT_MODE'
        },
        { prop: 'legCode',
          label: 'leg',
          type: 'dict',
          code: 'LEG'
        },
        // 状态
        { prop: 'status',
          label: this.$t('common.status'),
          type: 'dict',
          code: 'LOGISTICS_STATUS'
        },
        // 模板名称
        { prop: 'templateName', label: this.$t('sourcingTemplate.name') }
      ],
      form: {
        expenseItemId: null,
        businessModeCode: null,
        transportModeCode: null,
        legCode: null,
        chargeCode: null,
        chargeName: null
      },
      rules: {
        // 请选择费用项编码
        chargeCode: [{ required: true, message: this.$t('logisticsMod.msgChargeCode') }],
        // 请选择业务模式
        businessModeCode: [{ required: true, message: this.$t('logisticsMod.msgBusinessMode') }],
        // 请选择运输方式
        transportModeCode: [{ required: true, message: this.$t('logisticsMod.msgTransportWay') }]
      },
      queryParam: {},
      tableList: [],
      paymentType: [],
      templateList: []
    }
  },
  created () {
    this.tableHeader = [
      {
        prop: 'businessModeCode',
        label: this.$t('logisticsMod.businessMode'), // 业务模式
        width: 120,
        showType: 'slot',
        slot: 'businessModeCode'
      },
      {
        prop: 'transportModeCode',
        label: this.$t('bid_mod.transportType'), // 运输方式
        width: 120,
        showType: 'slot',
        slot: 'transportModeCode'
      },
      {
        prop: 'legCode',
        label: 'leg',
        width: 120,
        showType: 'slot',
        slot: 'legCode'
      },
      {
        prop: 'templateHeadId',
        label: this.$t('sourcingTemplate.name'), // 模板名称
        minWidth: 120,
        showType: 'slot',
        slot: 'templateHeadId'
        // show: (row) => row.editable,
      },
      {
        prop: 'chargeCode',
        label: this.$t('logisticsMod.expenseItem'), // 费用项
        minWidth: 120,
        showType: 'slot',
        slot: 'chargeCode'
        // show: (row) => row.editable,
      },
      {
        prop: 'chargeName',
        label: this.$t('logisticsMod.chargeCode'), // 费用项编码
        minWidth: 200,
        formattor (val, row) {
          return row.chargeCode
        }
      },
      {
        prop: 'status',
        label: this.$t('common.status'), // 状态
        width: 100,
        dataType: 'dict',
        code: 'LOGISTICS_STATUS'
      },
      {
        prop: 'createdUserName', // createdBy
        label: this.$t('common.creator'),
width: 120
      },
      {
        prop: 'creationDate',
        label: this.$t('common.creationTime'),
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'lastUpdatedUserName', // lastUpdatedBy
        label: this.$t('common.updatePeople'),
        width: 120
      },
      {
        prop: 'lastUpdateDate',
        label: this.$t('common.updateTime'),
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'operation',
        label: this.$t('common.operation'),
        width: 150,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          // 生效
          {
            show: row => ['DRAFT', 'INEFFECTIVE'].includes(row.status) && !!row.expenseItemId,
            callback: row => this.doEffect(row),
            formattor: () => this.$t('common.active')
          },
          // 失效
          {
            show: row => row.status === 'EFFECTIVE',
            callback: row => this.doIneffect(row),
            formattor: () => this.$t('common.inactive')
          },
          // 删除
          {
            show: row => row.status === 'DRAFT',
            callback: row => this.deleteOne(row),
            formattor: () => this.$t('common.delete')
          }
        ]
      }
    ]
    this.defaultTableHeader = this.tableHeader
    this.gettemplateList()

    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    gettemplateList () {
      this.$http({
        url: '/api-pd/logistics/logistics-template-head/listPageByParam',
        method: 'POST',
        data: {
          pageNum: 1,
          pageSize: 15,
          status: 'EFFECTIVE'
        },
        loading: true
      }).then(data => {
        this.templateList = data.data.list.map(i => ({
          id: i.templateCode,
          label: i.templateName,
          value: i.templateCode,
          templateHeadId: i.templateHeadId
        }))
      })
    },
    afterQuery (data) {
      this.tableList = data
    },
    getQuerydata (v) {
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange (val) {
      this.currentRow = val
    },
    handleSelectionChange (val) {
      this.selectList = val
    },
    delOne (index) {
      this.$refs[this.gridId].deleteRow(index)
    },

    downloadTemplate () {
      downloadFileLinkByPost(
        '/api-pd/logistics/expense-item/importExpenseItemModelDownload',
        this.$t('logisticsMod.expenseItemImp')
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    },
    editTab (type, row) {
      if (type === 'add') {
        this.$refs[this.gridId].addOneEditableColumn({ status: 'DRAFT' })
      } else {
        row.editable = true
      }
    },
    doBatchEffect () {
      if (this.selectList.length === 0) {
        return this.$message.error(this.$t('contractMod.msgSelData'))
      }
      if (this.selectList.some(v => v.status !== 'DRAFT' && v.status !== 'INEFFECTIVE')) {
        // 请选择拟定或失效状态的数据!
        return this.$message.error(this.$t('logisticsMod.msgSelDraftOrInAData'))
      }
      this.$http({
        url: '/api-pd/logistics/expense-item/effectiveExpenseItems',
        method: 'POST',
        data: this.selectList.map(v => v.expenseItemId).filter(v => !!v),
        loading: true
      }).then(() => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    },
    doBatchIneffect () {
      if (this.selectList.length === 0) {
        return this.$message.error(this.$t('contractMod.msgSelData'))
      }
      if (this.selectList.some(v => v.status !== 'EFFECTIVE')) {
        return this.$message.error(this.$t('logisticsMod.msgSelActiveData'))
      }
      this.$http({
        url: '/api-pd/logistics/expense-item/inEffectiveExpenseItems',
        method: 'POST',
        data: this.selectList.map(v => v.expenseItemId).filter(v => !!v),
        loading: true
      }).then(() => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    },
    doEffect (row) {
      this.$http({
        url: '/api-pd/logistics/expense-item/effectiveExpenseItem',
        method: 'GET',
        params: { expenseItemId: row.expenseItemId },
        loading: true
      }).then(() => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    },
    doIneffect (row) {
      this.$http({
        url: '/api-pd/logistics/expense-item/inEffectiveExpenseItem',
        method: 'GET',
        params: { expenseItemId: row.expenseItemId },
        loading: true
      }).then(() => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    },
    syncFilterParams (values) {
      this.queryParam = values
    },
    setchargeName2 (row) {
      if (row.chargeCode) {
        row.chargeName = this.$getDictLabel('CHARGE_NAME', row.chargeCode)
      }
    },
    setchargeTemplate (row) {
      let obj = this.templateList.find(v => v.templateHeadId === row.templateHeadId) || {}
      row.templateName = obj.label
    },
    setchargeName (val) {
      this.form.chargeName = this.$getDictLabel('CHARGE_NAME', val)
    },
    batchSave () {
      this.$http({
        url: '/api-pd/logistics/expense-item/saveExpenseItems',
        method: 'POST',
        data: this.$refs[this.gridId].tableData.filter(v => v.status === 'DRAFT'),
        loading: true
      }).then(() => {
        this.$message.success(this.$t('common.successSave'))
        this.getQuerydata()
      })
    },
    batchDelete () {
      if (this.selectList.length === 0) {
        return this.$message.error(this.$t('contractMod.msgSelData'))
      }
      if (this.selectList.some(v => v.status !== 'DRAFT')) {
        return this.$message.error(this.$t('logisticsMod.msgNotDelete')) // 状态不是拟定的不能删除!
      }
      for (let row of this.selectList) {
        if (!row.expenseItemId) {
          let index = this.$refs[this.gridId].tableData.indexOf(row)
          this.delOne(index)
        }
      }
      let idArr = this.selectList.map(v => v.expenseItemId).filter(v => !!v)
      if (idArr.length === 0) return
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.$http({
            url: '/api-pd/logistics/expense-item/deleteByIds',
            method: 'POST',
            data: idArr,
            loading: true
          }).then(() => {
            this.$message.success(this.$t('common.success'))
            this.getQuerydata()
          })
        })
    },
    addOneItem () {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.$http({
            url: '/api-pd/logistics/expense-item/saveExpenseItem',
            method: 'POST',
            data: this.form,
            loading: true
          }).then(() => {
            this.dialogFormVisible = false
            this.$message.success(this.$t('common.successSave'))
            this.getQuerydata()
          })
        }
      })
    },
    deleteOne (row) {
      if (!row.expenseItemId) {
        let index = this.$refs[this.gridId].tableData.indexOf(row)
        this.delOne(index)
        return
      }
      if (!row.expenseItemId) return
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.$http({
            url: '/api-pd/logistics/expense-item/deleteById',
            method: 'GET',
            params: { expenseItemId: row.expenseItemId },
            loading: true
          }).then(() => {
            this.$message.success(this.$t('common.successDelete'))
            this.getQuerydata()
          })
        })
    }
  }
}
</script>
