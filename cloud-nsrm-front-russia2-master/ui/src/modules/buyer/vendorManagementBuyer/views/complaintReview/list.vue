<template>
  <el-container
    class="flex-container complaintinfo_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="filterConfig"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      >
        <template #complaintType="{ scope }">
          <DictSelect
            v-model="scope.complaintType"
            code="COMPLAINT_TYPE"
          />
        </template>
        <template #complaintStatus="{ scope }">
          <DictSelect
            v-model="scope.complaintStatus"
            code="COMPLAINT_STATUS"
          />
        </template>
      </FormWrapper>
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <AuthorityButton
            type="primary"
            @click="setPersonLiable"
          >
            {{
              $t('vendorMod.setPersonLiable')
            }}
          </AuthorityButton>
          <ExportExcel
            style="margin-left: 5px"
            :filter-params="queryParam"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            export-mode="front"
            page-url="/api-sup-ce/sup/complaintinfo/listPage"
            type="default"
          />
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :checkbox="true"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        :source="complaintInfo.list"
      />
      <!-- 发布弹框 -->
      <srm-dialog
        size="small"
        :title="$t('vendorMod.setPersonLiable')"
        :visible.sync="liableFormVisible"
        :close-on-click-modal="false"
      >
        <el-form
          ref="pubForm"
          :model="liableForm"
          :rules="liableRules"
        >
          <el-form-item
            prop="beginQuote"
            :label="$t('qualitySynergy.responsible')"
          >
            <QuickSearch
              :show-input="liableForm.authUserName"
              show-key="username"
              :scope-data="liableForm"
              auto-query
              name="scc_rbac_user_display"
              @close-quicksearch="getUserObj"
            />
          </el-form-item>
        </el-form>
        <div
          slot="footer"
          class="dialog-footer"
        >
          <el-button
            @click="liableFormVisible = false"
          >
            {{
              $t('common.cancel')
            }}
          </el-button>
          <el-button
            type="primary"
            @click="comfirmSum"
          >
            {{
              $t('common.confirm')
            }}
          </el-button>
        </div>
      </srm-dialog>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import ExportExcel from 'lib@/components/export-excel'
import QuickSearch from 'lib@/components/QuickSearch'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import complaintinfoEdit from './edit.vue'
import MImport from 'lib@/components/import'
import { complaintInfo } from 'modb@/vendorManagementBuyer/api/supApi'

export default {
  name: 'ComplaintinfoList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    MImport,
    QuickSearch,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      complaintInfo: complaintInfo,
      displayRiskList: [],
      pageSize: 15,
      gridId: 'list',
      liableRules: {},
      liableFormVisible: false,
      filterParams: {},
      dictCodes: {
        complaintType: 'COMPLAINT_TYPE',
        complaintStatus: 'COMPLAINT_STATUS'
      },
      currentRows: [],
      liableForm: {
        authUserId: '', // 汇总人id
        authUserName: '' // 汇总人昵称
      },
      tableHeader: [
        {
          prop: 'complaintNo',
          label: () => this.$t('vendorMod.complaintInfoId'),
          width: 130,
          showType: 'button',
          btnStyle: 'text',
          callback: (row) => {
            this.editHandle(row, 'view')
          }
        },
        {
          prop: 'complaintStatus',
          label: () => this.$t('vendorMod.complaintStatus'),
          width: 100,
          dataType: 'dict', // 数据类型为字典
          code: 'COMPLAINT_STATUS' // 字典code
        },
        {
          prop: 'orgName',
          label: () => this.$t('vendorMod.ceeaOrgName'),
          width: 100
        },
        {
          prop: 'categoryName',
          label: () => this.$t('vendorMod.categoryName'),
          width: 120
        },
        // {
        //   prop: 'complaintTheme',
        //   label: () => this.$t('vendorMod.complaintTheme'),
        //   width: 100
        // },
        {
          prop: 'complaintType',
          label: () => this.$t('vendorMod.complaintType'),
          width: 100,
          dataType: 'dict', // 数据类型为字典
          code: 'COMPLAINT_TYPE' // 字典code
        },
        {
          prop: 'authUserName',
          label: () => this.$t('vendorMod.PersonLiable'),
          width: 100
        },
        {
          prop: 'supplierName',
          label: () => this.$t('common.vendor'),
          width: 100
        },
        {
          prop: 'complaintUserName',
          label: () => this.$t('vendorMod.complaintUserName'),
          width: 120
        },
        {
          prop: 'supplierCode',
          label: () => this.$t('common.vendorCode'),
          width: 120
        },
        {
          prop: 'creationDate',
          label: () => this.$t('common.creationTime'),
          width: 160,
          dataType: 'dateTime'
        },
        {
          prop: 'lastAnswerDate',
          label: () => this.$t('vendorMod.lastAnswerDate'),
          width: 160,
          dataType: 'dateTime'
        },
        {
          prop: 'operation',
          label: () => this.$t('common.operation'),
          showType: 'buttons',
          btnStyle: 'text',
          fixed: 'right',
          width: 100,
          buttons: [
            {
              callback: (row) => this.editHandle(row, 'edit'),
              // code: "pr:requirementApply:edit",
              //  show: row => row.complaintStatus === "DRAFT",
              formattor: () => {
                return this.$t('common.view')
              }
            }
          ]
        }
      ],

      filterConfig: [
        { prop: 'complaintNo', label: () => this.$t('vendorMod.complaintInfoId') },
        {
          prop: 'orgId',
          type: 'OUorganizationSelector',
          label: () => this.$t('supplierRating.entity')
        },
        { prop: 'supplierName', label: () => this.$t('common.vendor') },
        {
          prop: 'complaintType',
          label: () => this.$t('vendorMod.complaintType'),
          type: 'slot',
          slot: 'complaintType'
        },
        {
          prop: 'complaintStatus',
          label: () => this.$t('vendorMod.complaintStatus'),
          type: 'slot',
          slot: 'complaintStatus'
        },
        { prop: 'creationDate', label: () => this.$t('vendorMod.creatTime2'), type: 'daterange' }
      ],
      queryParam: {}
    }
  },
  created () {
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    handleSuccess () {
      this.getQuerydata()
    },
    syncFilterParams (values) {
      this.filterParams = values
    },
    getQuerydata (params) {
      this.queryParam = JSON.parse(JSON.stringify(params || {}))
      let { creationDate } = this.queryParam
      if (creationDate && creationDate.length) {
        this.queryParam.complaintStartDate = creationDate[0]
        this.queryParam.complaintEndDate = creationDate[1]
      } else if (params && !params.creationDate) {
        delete this.queryParam.complaintStartDate
        delete this.queryParam.complaintEndDate
      }
      delete this.queryParam.creationDate
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    deleteHandle (row) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          complaintInfo.delete(row.complaintInfoId).then((res) => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
        })
        .catch(() => {})
    },

    editHandle (row, type) {
      const tab = {
        component: complaintinfoEdit,
        params: {
          row,
          flag: type
        },
        title: this.$t('route.complaintReview'),
        name: 'complaintinfoEdit' + row.complaintNo
      }
      this.$emit('tab-add', tab)
    },
    // 点击指派责任人按钮
    setPersonLiable () {
      this.liableForm.authUserName = ''
      this.liableForm.authUserId = ''

      if (this.currentRows.length > 0) {
        this.liableFormVisible = true
      } else {
        this.$message({
          type: 'warning',
          message: this.$t('oneStopShopping.createProjectMsg15')
        })
      }
    },
    getUserObj (val, scope) {
      scope.authUserId = val ? val.userId : ''
      scope.authUserName = val ? val.nickname : ''
    },
    // 提交责任人信息
    comfirmSum () {
      let submitData = {}
      let ids = []
      this.currentRows.forEach((elm) => {
        ids.push(elm.complaintInfoId)
      })

      submitData = {
        ...this.liableForm,
        ids: ids
      }

      // console.log("[submitData]", submitData);
      complaintInfo.setPersonLiable(submitData).then((res) => {
        if (res) {
          this.$message({ type: 'success', message: res.message })
          this.liableFormVisible = false
          this.getQuerydata()
        }
      })
    },
    handleCurrentChange (val) {
      this.currentRows = val
    },
    // 上传附件成功
    handleUploadSuccess (file, row, key) {
      const { id, name } = file
      row[key] = id.toString()
    },
    // 删除文件
    handleAttachmentRemove (row, key) {
      row[key] = ''
    }
  }
}
</script>
