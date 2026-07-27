<template>
  <el-container
    class="flex-container sitereviewplanconfirm_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="filterConfig"
        @getFormData="getQuerydata"
      />
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <ExportExcel
            page-url="/api-sup/sup/sitereviewplanconfirm/listPage"
            :filter-params="queryParam"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            :title="$t('common.export')"
            export-mode="front"
          />
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        :source="siteReviewPlanConfirm.planList"
      />
      <srm-dialog
        :title="$t('dataConfMod.categoryDetails')"
        :visible.sync="dialogFormVisible2"
        :close-on-click-modal="false"
        size="middle"
      >
        <el-table
          :data="displayItemPL"
          style="width: 100%"
          border
          height="250px"
          highlight-current-row
        >
          <el-table-column
            align="center"
            type="index"
            :label="$t('contractMod.tabindex')"
            width="60"
          />
          <!-- 品类编码 -->
          <el-table-column
            align="center"
            prop="categoryCode"
            :label="$t('common.categoryCode')"
            min-width="150"
            :show-overflow-tooltip="true"
          >
            <template slot-scope="scope">
              <QuickSearch
                :show-input="scope.row.categoryCode"
                show-key="categoryCode"
                :scope-data="scope.row"
                name="scc_base_purchase_category2"
              />
            </template>
          </el-table-column>
          <!-- 品类名称 -->
          <el-table-column
            align="center"
            prop="categoryName"
            :label="$t('common.categoryName')"
            min-width="150"
            :show-overflow-tooltip="true"
          />
        </el-table>
        <template #footer>
          <el-button @click="dialogFormVisible2 = false">
            {{ $t('vendorMod.goBack') }}
          </el-button>
        </template>
      </srm-dialog>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import QuickSearch from 'lib@/components/QuickSearch'
import sitereviewplanconfirmEdit from './edit.vue'
import ExportExcel from 'lib@/components/export-excel'
import { siteReviewPlanConfirm } from 'mods@/vendorManagementSupplier/api/index'
import { supCommonApi } from 'modb@/vendorManagementBuyer/api/supApi'

export default {
  name: 'SitereviewplanconfirmList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    QuickSearch,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      siteReviewPlanConfirm: siteReviewPlanConfirm,
      dialogFormVisible2: false,
      dictCodes: {
        planType: 'planType',
        approveStatus: 'CONTRACT_STATUS'
      },
      filterParams: {},
      displayItemPL: [],
      name: 'sitereviewplanconfirmList',
      tableName: 'sitereviewplanconfirmTable',
      pageSize: 15,
      gridId: 'list',
      currentRows: [],
      tableHeader: [
        {
          prop: 'orgName',
          label: '采购组织',
          width: 100
        },
        {
          prop: 'categoryName',
          label: '品类',
          showType: 'button',
          btnStyle: 'text',
          callback: row => this.readOne(row),
          formattor: val => {
            return '查看'
          },
          width: 100
        },
        {
          prop: 'planType',
          label: '计划类型',
          width: 100,
          formattor: val => {
            let label = null
            this.planTypeAll.forEach(datas => {
              if (datas.value == val) {
                label = datas.label
              }
            })
            return label
          }
        },
        {
          prop: 'planName',
          label: '关联计划',
          width: 100
        },
        {
          prop: 'userAccounts',
          label: '工作小组成员',
          width: 130,
          showType: 'button',
          btnStyle: 'text',
          callback: row => this.editHandle2(row),
          formattor: val => {
            return '查看'
          }
        },
        {
          prop: 'vendorContact',
          label: '供应商联系人',
          showType: 'input',
          editable: row => row.approveStatus === 'RELEASED',
          width: 130
        },
        {
          prop: 'vendorContactTel',
          label: '供应商联系电话',
          showType: 'input',
          editable: row => row.approveStatus === 'RELEASED',
          width: 130
        },
        {
          prop: 'planSetOutTime',
          label: '计划出发时间',
          width: 130
        },
        {
          prop: 'planVisitTime',
          label: '计划到访时间',
          width: 130
        },
        {
          prop: 'visitDays',
          label: '计划到访天数',
          width: 130
        },
        {
          prop: 'approveStatus',
          label: '状态',
          width: 120,
          formattor: val => {
            let label = null
            this.planStatusAll.forEach(datas => {
              if (datas.value == val) {
                label = datas.label
              }
            })
            return label
          }
        },
        {
          prop: 'operation',
          label: '操作',
          showType: 'buttons',
          btnStyle: 'text',
          fixed: 'right',
          width: 130,
          buttons: [
            {
              callback: row => this.confirms(row, 'CF'),
              formattor: () => {
                return this.$t('components.approvalHead.button.confirm')
              },
              show: row => row.approveStatus === 'RELEASED'
            },
            {
              callback: row => this.confirms(row, 'RJ'),
              formattor: () => {
                return this.$t('components.approvalHead.headers.refuse')
              },
              show: row => row.approveStatus === 'RELEASED'
            }
          ]
        }
      ],
      planTypeAll: [
        {
          value: 'YEAR',
          label: '年度'
        },
        {
          value: 'HALF_YEAR',
          label: '半年度'
        },
        {
          value: 'QUARTER',
          label: '季度'
        },
        {
          value: 'ALLOW',
          label: '准入'
        },
        {
          value: 'MONTH',
          label: '月度'
        }
      ],
      planStatusAll: [
        {
          value: 'DRAFT',
          label: '拟定'
        },
        {
          value: 'RELEASED',
          label: '已发布'
        },
        {
          value: 'VENDOR_CONFIRMED',
          label: '供应商已确认'
        },
        {
          value: 'VENDOR_REJECT',
          label: '供应商已驳回'
        },
        {
          value: 'PASS',
          label: '审批已通过'
        },
        {
          value: 'REJECT',
          label: '审批已驳回'
        }
      ],
      planProcessStatusAll: [
        {
          value: 'NOT_STARTED',
          label: '未启动'
        },
        {
          value: 'ONGOING',
          label: '进行中'
        },
        {
          value: 'COMPLETED',
          label: '已完成'
        }
      ],
      filterConfig: [
        {
          prop: 'planName',
          label: '计划名称'
        },
        {
          prop: 'planType',
          label: '计划类型',
          type: 'select',
          options: () => this.planTypeAll
        },
        {
          prop: 'categoryCode',
          label: '品类名称',
          type: 'quicksearch',
          showKey: 'categoryName',
          propKey: 'categoryCode',
          name: 'scc_base_purchase_category2'
        },
        {
          prop: 'orgId',
          label: '采购组织',
          type: 'OUorganizationSelector'
        }
      ],
      queryParam: {
        approveStatusList: ['RELEASED', 'VENDOR_CONFIRMED', 'VENDOR_REJECT', 'PASS', 'REJECT']
      }
    }
  },
  watch: {
    $route: {
      // 当前功能已经打开的时候监听是否是从其他功能跳转过来的
      deep: true,
      immediate: true,
      handler () {
        console.log(this.$route.params)
        if (
          this.$route.params.from === 'fromFun'
        ) {
          let planConfirmId = Number(this.$route.params.formId)
          this.queryParam.planConfirmId = planConfirmId
          this.filterConfig.planConfirmId = planConfirmId
          this.$nextTick(() => {
            this.getQuerydata()
          })
        }
      }
    }
  },
  created () {
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    // 点击工作小组成员查看
    editHandle2 (row) {
      this.mode = 'edit'
      const tab = {
        component: sitereviewplanconfirmEdit,
        params: {
          readOnly: true,
          row,
          flag: this.mode
        },
        title: '计划落实管理查看',
        name: 'sitereviewplanconfirmEdit' + row.planConfirmId
      }
      this.$emit('tab-add', tab)
      this.getQuerydata()
    },
    // 点击列表确认或者驳回
    confirms (row, bol) {
      // bol代表确认还是驳回，确认为CF，驳回为RJ
      let status = ''
      if (bol == 'CF') {
        status = 'VENDOR_CONFIRMED'
      } else if (bol == 'RJ') {
        status = 'VENDOR_REJECT'
      }
      if (row.vendorContact && row.vendorContactTel) {
        const obj = {
          vendorContact: row.vendorContact,
          vendorContactTel: row.vendorContactTel,
          planConfirmId: row.planConfirmId,
          approveStatus: status
        }
        siteReviewPlanConfirm.planUpdateStatus(obj).then(res => {
          this.$message.success(res.message)
          this.getQuerydata()
        })
      } else {
        this.$message.error('请输入该行的联系人与电话')
      }
    },
    syncFilterParams (values) {
      this.filterParams = values
    },
    // 维护品类
    readOne (row) {
      this.displayItemPL = []
      supCommonApi.getCategoryList(row.siteReviewPlanId)
        .then(data => {
          this.displayItemPL = data.data.list
          this.dialogFormVisible2 = true
          this.siteReviewPlanId = row.siteReviewPlanId
        })
        .catch(err => {
          console.log(err)
        })
    },

    getQuerydata (params) {
      if (!params) {
        params = {}
      }
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
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
