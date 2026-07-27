<template>
  <el-container
    class="flex-container the_onlyofficeContractModeManagerList_wrapper"
    direction="vertical"
  >
    <el-main>
      <form-wrapper
        :form-array="preArr"
        form-label-width="120px"
        @getFormData="getQuerydata"
      />

      <main-header
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <AuthorityButton
            type="primary"
            code="cm:contractModeManager:add"
            @click="addNewMode"
          >
            {{
              $t('common.add')
            }}
          </AuthorityButton>
        </template>
      </main-header>
      <table-view
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :checkbox="false"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        :source="$api.cm.listPage"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import Edit from './edit'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import { getToken } from '@/utils/auth'

export default {
  name: 'OnlyofficeContractModeManagerList',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      tableName: 'onlyofficeContractModeManagerList',
      reviewFormNumber: '',
      gridData: [],
      pageSize: 15,
      gridId: 'list',
      selectList: [],
      currentRows: [],
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      form: {
        id: '',
        vendorCode: '',
        vendorCompanyName: '',
        reviewFormNumber: '',
        enabled: ''
      },
      rules: {
        vendorCode: [{ required: true, message: this.$t('bidMod.msgDictCode') }],
        vendorCompanyName: [{ required: true, message: this.$t('bidMod.msgDictName') }]
      },
      isModify: false,
      dialogFormVisible: false,
      formLabelWidth: '100px',
      preArr: [
        { prop: 'modelCode', label: this.$t('dataConfMod.templateCode') },
        { prop: 'modelName', label: this.$t('contractMod.templHeadId') },
        {
          prop: 'status',
          label: this.$t('common.status'),
          type: 'dict',
          code: 'CONTRACT_MODEL_STATUS'
        }
        // {
        //   prop: "materialCode",
        //   label: "物料编码",
        //   type: "quicksearch",
        //   showKey: "materialCode",
        //   name: "scc_base_material_item"
        // },
        // {
        //   prop: "organizationId",
        //   label: "采购组织",
        //   type:'OUorganizationSelector',
        // },
        // { prop: "orderNumber", label: "采购订单编号" },
        // { prop: "deliveryNumber", label: "送货单号" },
        // {
        //   prop: "vendorName",
        //   label: "供应商名称",
        //   type: "quicksearch",
        //   showKey: "companyName",
        //   name: "scc_sup_company_info_display"
        // },
        // { prop: "startReturnDate", label: "起始退货日期", type: "date" },
        // { prop: "endReturnDate", label: "截止退货日期", type: "date" }
      ],
      queryParam: {},
      deliveryTypes: [],
      retrunTypes: []
    }
  },
  computed: {},
  created () {
    let _this = this
    this.tableHeader = [
      {
        prop: 'modelCode',
        label: this.$t('dataConfMod.templateCode'),
        // showType: "button",
        // btnStyle: "text",
        // callback: row => this.suppleDelivery("edit", row),
        width: 130
      },
      {
        prop: 'modelName',
        label: this.$t('contractMod.templHeadId')
      },
      {
        prop: 'modelType',
        label: this.$t('contractMod.templType'),
        width: 130,
        dataType: 'dict',
        code: 'ELEM_CONTRACT_TYPE'
      },
      {
        prop: 'ceeaControlMethod',
        label: this.$t('contractMod.controlMethod'),
        width: 130,
        dataType: 'dict',
        code: 'MANAGEMENT_CONTROL_MODEL'
      },
      {
        prop: 'startDate',
        label: this.$t('contractMod.startDate'),
        width: 100
      },
      {
        prop: 'endDate',
        label: this.$t('contractMod.endDate'),
        width: 100
      },
      {
        prop: 'status',
        label: this.$t('contractMod.status'),
        width: 100,
        dataType: 'dict',
        code: 'CONTRACT_MODEL_STATUS'
      },
      {
        prop: 'operation',
        label: this.$t('common.operation'),
        showType: 'buttons',
        btnStyle: 'text',
        // fixed: "right",
        width: 165,
        buttons: [
          {
            callback: (row) => this.preview(row),
            // code: "pr:requirementApply:edit",
            // show: row => row.auditStatus === "DRAFT",
            code: 'cm:contractModeManager:preview',
            formattor: () => {
              // return this.$t("common.edit");
              return _this.$t('common.preview')
            }
          },
          {
            callback: (row) => this.edit(row, 'read'),
            code: 'cm:contractModeManager:read',
            // code: "pr:requirementApply:edit",
            show: (row) => !['DRAFT', 'FREEZE'].includes(row.status),
            formattor: () => {
              // return this.$t("common.edit");
              return _this.$t('common.view')
            }
          },
          {
            callback: (row) => this.edit(row, 'edit'),
            code: 'cm:contractModeManager:edit',
            // code: "pr:requirementApply:edit",
            show: (row) => ['DRAFT', 'FREEZE'].includes(row.status),
            formattor: () => {
              // return this.$t("common.edit");
              return _this.$t('common.edit')
            }
          },
          {
            callback: (row) =>
              this.$api.cm.buyer.main.takeEffect(row.modelHeadId).then((res) => {
                this.$message.success(res.message)
                this.getQuerydata()
              }),
            // code: "pr:requirementApply:edit",
            code: 'cm:contractModeManager:takeEffect',
            show: (row) => ['DRAFT', 'FREEZE'].includes(row.status),
            formattor: () => {
              // return this.$t("common.edit");
              return _this.$t('common.active')
            }
          },
          {
            callback: (row) =>
              this.$api.cm.buyer.main.failure(row.modelHeadId).then((res) => {
                this.$message.success(res.message)
                this.getQuerydata()
              }),
            code: 'cm:contractModeManager:failure',
            // code: "pr:requirementApply:edit",
            show: (row) => row.status === 'VALID',
            formattor: () => {
              // return this.$t("common.edit");
              return _this.$t('common.inactive')
            }
          },
          {
            callback: (row) =>
              this.$api.cm.buyer.main.freeze(row.modelHeadId).then((res) => {
                this.$message.success(res.message)
                this.getQuerydata()
              }),
            code: 'cm:contractModeManager:freeze',
            // code: "pr:requirementApply:edit",
            show: (row) => row.status === 'VALID',
            formattor: () => {
              // return this.$t("common.edit");
              return _this.$t('contractMod.freeze')
            }
          }
        ]
      }
    ]
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  mounted () {},
  methods: {
    dolayout () {
      this.$refs[this.gridId].doLayout()
    },
    addNewMode () {
      this.edit({}, 'add')
    },
    handleCurrentChange () {},
    getQuerydata (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    edit (row, flag) {
      const tab = {
        component: Edit,
        params: { row, flag },
        title: this.$t('common.edit') + `${row.modelName ? '-' + row.modelName : ''}`,
        name: `${flag}_${row.modelHeadId ? row.modelHeadId : ''}`
      }
      if (flag === 'add') {
        tab.title = this.$t('common.add')
        tab.name = 'add'
      }
      if (flag === 'read') {
        tab.title = this.$t('common.view')
      }
      this.$emit('tab-add', tab)
    },
    preview (row) {
      const { fileuploadId } = row
      const access_token = getToken()
      window.open(
        `${window.location.origin}/api-onlineview/EditorServlet?uploadId=${fileuploadId}&mode=embedded&access_token=${access_token}`
      )
    }
  }
}
</script>
<style scoped lang="scss">
.the_onlyofficeContractModeManagerList_wrapper {
}
</style>
