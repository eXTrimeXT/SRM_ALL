<template>
  <el-container
    class="flex-container-notab the_functionMaintenance_wrapper"
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
          <AuthorityButton
            type="primary"
            code="rbac:roleMaintenance:reApprovalOne"
            @click="reApprovalOne"
          >
            <!-- 审批重推 -->
            {{ $t("route.reApproval") }}
          </AuthorityButton>
          <!-- :placeholder="请输入ERP申请单号" -->
          <el-input
            v-model="inputNumber"
            :placeholder="$t('reApproval.msgERPFormId')"
            class="input-with-select"
            style="width: 422px;margin-left: 11px;"
          >
            <el-button
              slot="append"
              type="primary"
              style="background-color: #1890ff;color:#fff"
              @click="reApprovalOneByFail"
            >
              <!-- ERP失败申请重推 -->
              {{ $t("reApproval.erpFailReApproval") }}
            </el-button>
          </el-input>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :page-size="pageSize"
        :check-change="handleCheckChange"
        :checkbox="true"
        :auto-query="false"
        url="/api-base/workflow/workflowPageList"
      />
    </el-main>
    <!-- 备注 -->
    <srm-dialog
      :title="$t('dataConfMod.remark')"
      :visible.sync="centerDialogVisible"
      size="middle"
      style="line-height:2em"
      center
    >
      <span>{{ getformRemark }}</span>
    </srm-dialog>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { accessApi } from 'modb@/accountAccess/api'

export default {
  name: 'ReApproval',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  data () {
    return {
      getformRemark: '',
      centerDialogVisible: false,
      roleTypes: [],
      inputNumber: null,
      currentRows: [],
      treeData: [],
      gridId: 'reApproval',
      curOpt: 'add',
      pageSize: 15,
      roleTitle: this.$t('dataConfMod.addRole'), // "新增角色"
      tableData: [],
      tableHeader: [],
      queryParam: {},
      preArr: [
        {
          prop: 'formInstanceId',
          label: () => this.$t('reApproval.receiptId')
        }, // 单据ID
        {
          prop: 'formTemplateId',
          label: () => this.$t('reApproval.formTemplateId'), // 流程模板ID
          type: 'dict',
          code: 'TempIdToModule'
        },
        {
          prop: 'srmOrderStatus',
          label: () => this.$t('reApproval.srmOrderStatus'),
          type: 'dict',
          code: 'TempIdToModule'
        }, // srm单据状态
        { prop: 'formRemark', label: () => this.$t('reApproval.formRemark') } // 备注信息
      ],

      form: {
        roleCode: null,
        startDate: null,
        endDate: null,
        roleType: null
      },
      langList: [],
      roleLanguages: [{ language: this.$store.getters.language, roleName: '' }],

      dialogFormVisible: false,
      ROLE_TYPE: {}
    }
  },
  created () {
    var _this = this
    this.tableHeader = [
      {
        prop: 'formInstanceId',
        label: () => this.$t('reApproval.receiptId'), // 单据ID
        width: 150,
        align: 'center'
      }, // "业务主键ID"
      {
        prop: 'formTemplateId',
        label: () => this.$t('reApproval.formTemplateId'), // "流程模板ID"
        width: 150,
        align: 'center',
        dataType: 'dict',
        code: 'TempIdToModule'

      },
      {
        prop: 'flowInstanceId',
        label: () => this.$t('reApproval.flowInstanceId'), // "流程ID"
        width: 300,
        align: 'center'
      },
      {
        prop: 'formRemark',
        label: () => this.$t('reApproval.formRemark'), // "备注信息"
        minWidth: 400,
        align: 'center',
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.readOne(row)
      },
      {
        prop: 'lastUpdateDate',
        label: () => this.$t('priceModel.costElement.lastUpdateDate'), // 更新日期
        minWidth: 300,
        align: 'center'
      },
      {
        prop: 'srmOrderStatus',
        label: () => this.$t('reApproval.srmOrderStatus'), // "srm单据状态"
        width: 150,
        align: 'center'
      }
    ]

    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    readOne (row) {
      this.centerDialogVisible = true
      this.getformRemark = row.formRemark || ''
    },
    reApprovalOneByFail (val) {
      if (!this.inputNumber) {
        return this.$message({
          type: 'error',
          message: this.$t('reApproval.msgERPFormId')
        }) // 请输入ERP申请单号
      }
      accessApi.getResetError({ number: this.inputNumber }).then(data => {
          if (data) {
            this.$message({
              type: 'success',
              message: this.$t('common.success')
            }) // 操作成功
          }
        })
        .catch(err => {
          console.log(err)
        })
    },
    reApprovalOne () {
      let ids = []
      this.currentRows.map(v => {
        ids.push(v.id)
      })
      accessApi.getHeavyPush(ids).then(data => {
          if (data) {
            this.$message({
              type: 'success',
              message: this.$t('common.success')
            }) // 操作成功
          }
        })
        .catch(err => {
          console.log(err)
        })
    },
    handleCheckChange (val) {
      this.currentRows = val
      // console.log("val", val);
    },
    getQuerydata (v) {
      this.queryParam = v
      // if (v && v.startDate) this.queryParam.startDate = new Date(v.startDate).getTime();
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    }
  }
}
</script>
<style scoped lang="scss">
.the_functionMaintenance_wrapper {
}
</style>
