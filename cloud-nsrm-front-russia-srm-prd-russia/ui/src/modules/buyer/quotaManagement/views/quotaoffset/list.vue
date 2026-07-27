<template>
  <el-container
    class="flex-container quotaoffset_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="queryForm"
        @getFormData="getQuerydata"
      >
        <!-- @synchronous-value="syncFilterParams" -->
      </FormWrapper>
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <AuthorityButton
            type="primary"
            @click="addHandle"
          >
            {{
              $t('common.add')
            }}
          </AuthorityButton>
          <!-- <ExportExcel
            pageUrl="/api-sup/sup/quotaoffset/listPage"
            :filterParams="filterParams"
            :tableHeader="tableHeader"
            :dictCodes="dictCodes"
            exportMode="front"
          /> -->
          <!-- timeout="1000000" -->
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
        :source="quotaOffsetApi.listPage"
      />
    </el-main>
    <srm-dialog
      :title="dialogTitle"
      size="large"
      :visible.sync="visible"
    >
      <div class="quotaoffsetEdit">
        <el-form
          ref="form"
          :model="form"
          :rules="rules"
        >
          <srm-row>
            <srm-col>
              <el-form-item
                prop="offsetName"
                :label="$t('other.offsetName')"
              >
                <el-input v-model="form.offsetName" />
              </el-form-item>
            </srm-col>
            <srm-col>
              <el-form-item
                prop="queryStartDate"
                :label="$t('other.queryStartDate')"
              >
                <el-date-picker
                  v-model="form.queryStartDate"
                  type="date"
                  format="yyyy-MM-dd"
                  value-format="yyyy-MM-dd"
                  :placeholder="$t('purchaseDemand.datePicker')"
                />
              </el-form-item>
            </srm-col>
            <srm-col>
              <el-form-item
                prop="queryEndDate"
                :label="$t('other.queryEndDate')"
              >
                <el-date-picker
                  v-model="form.queryEndDate"
                  type="date"
                  format="yyyy-MM-dd"
                  value-format="yyyy-MM-dd"
                  :placeholder="$t('purchaseDemand.datePicker')"
                />
              </el-form-item>
            </srm-col>
          </srm-row>
          <div style="padding-bottom: 10px">
            <el-button
              class="detail-pbtn"
              type="primary"
              @click="addOuListFuction"
            >
              {{ $t('bidMod.affairsIncreased') }}
            </el-button>
          </div>
          <el-table
            :data="addOuList"
            style="width: 100%"
            border
            max-height="250px"
          >
            <el-table-column
              align="center"
              type="index"
              :label="$t('purSettlementMod.tabindex')"
              width="50"
            />
            <!-- 库存组织 -->
            <el-table-column
              align="center"
              prop="orgId"
              :label="$t('purchaseDemand.fullPathId')"
            >
              <template slot-scope="scope">
                <OrganizationSelector
                  ref="organizationSelector2"
                  v-model="scope.row.orgId"
                  node-type="INV"
                  :placeholder="$t('common.pleaseSelect')"
                  @select="getAgentObj($event,scope.row)"
                />
              </template>
            </el-table-column>
            <el-table-column
              :label="$t('common.operation')"
              width="60"
            >
              <template slot-scope="scope">
                <el-button
                  type="text"
                  @click="handleDelClick(scope.$index, scope.row)"
                >
                  {{
                    $t('common.delete')
                  }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-form>
      </div>
      <template
        #footer
        class="dialog-footer"
      >
        <el-button
          type="primary"
          @click="confirm"
        >
          {{ $t('common.confirm') }}
        </el-button>
        <el-button @click="cancel">
          {{ $t('common.cancel') }}
        </el-button>
      </template>
    </srm-dialog>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import QuickSearch from 'lib@/components/QuickSearch'
import ExportExcel from 'lib@/components/export-excel'
import quotaflowEdit from './edit.vue'
import OrganizationSelector from 'lib@/components/organization-selector'
import { quotaOffsetApi } from 'modb@/quotaManagement/api/quotaApi'

export default {
  name: 'QuotaoffsetList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    QuickSearch,
    ExportExcel,
    OrganizationSelector
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      quotaOffsetApi: quotaOffsetApi,
      addOuList: [],
      pageSize: 15,
      gridId: 'list',
      currentRows: [],
      visible: false,
      mode: 'add',
      dialogTitle: '详情',
      form: {
        offsetName: '',
        queryStartDate: '',
        queryEndDate: '',
        orgIdList: []
      },
      rules: {
        offsetName: [{ required: true, message: '必选' }],
        queryStartDate: [{ required: true, message: '必选' }],
        queryEndDate: [{ required: true, message: '必选' }]
      },
      dictCodes: {},
      filterParams: {},
      tableHeader: [
        {
          prop: 'offsetCode',
          label: '执行偏差编码',
          showType: 'button',
          btnStyle: 'text',
          callback: function (row) {
            this.checkDetails(row)
          }.bind(this)
        },
        {
          prop: 'offsetName',
          label: '执行偏差名称'
        },
        {
          prop: 'queryStartDate',
          label: '查询开始时间',
          width: 120
        },
        {
          prop: 'queryEndDate',
          label: '查询结束时间',
          width: 120
        },
        {
          prop: 'creationDate',
          label: '创建时间',
          width: 100
        },
        {
          prop: 'createdBy',
          label: '创建人'
        },
        {
          prop: 'offsetStatus',
          label: '单据状态',
          formattor: (val) => {
            switch (val) {
              case 'DRAFT':
                val = '拟定'
                break
              case 'CALCING':
                val = '计算中'
                break
              case 'CALCED':
                val = '已计算'
                break
              case 'TOBEAPPROVED':
                val = '待审批'
                break
              case 'APPROVAL':
                val = '已通过'
                break
              case 'REJECTED':
                val = '已驳回'
                break
              default:
                val = ''
                break
            }
            return val
          },
          width: 100
        }
        // {
        //   prop: "operation",
        //   label: "操作",
        //   showType: "buttons",
        //   btnStyle: "text",
        //   fixed: "right",
        //   width: 130,
        //   buttons: [
        //     {
        //       callback: row => this.checkDetails(row),
        //       // code: "pr:requirementApply:edit",
        //       show: row => row.approveStatus === "APPROVAL",
        //       formattor: () => {
        //         return this.$t("orderMod.viewDetail");
        //       }
        //     }
        //   ]
        // }
      ],

      queryForm: [
        {
          label: () => this.$t('common.creationTime'),
          type: 'daterange',
          prop: 'queryCreationDate'
        },
        {
          label: '查询时间',
          type: 'daterange',
          prop: 'queryTimeRange'
        },
        {
          label: () => this.$t('common.creator'),
          type: 'input',
          prop: 'createdBy'
        },
        {
          label: '执行偏差编码',
          type: 'input',
          prop: 'offsetCode'
        },
        {
          label: '执行偏差名称',
          type: 'input',
          prop: 'offsetName'
        }
      ],
      queryParam: {}
    }
  },
  created () {
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    // 点击查看详情
    checkDetails (row) {
      this.$emit('tab-add', {
        component: quotaflowEdit,
        params: {
          flag: 'edit',
          isReadonly: true,
          row: row
        },
        title: row.offsetCode,
        name: 'quotaflowEdit' + row.offsetCode
      })
    },
    // 行删除
    handleDelClick (index, row) {
      this.addOuList.splice(index, 1)
    },
    getAgentObj (val, scope) {
      scope.orgId = val ? val.organizationId : ''
    },
    addOuListFuction () {
      this.addOuList.push({
        orgId: ''
      })
    },
    cancel () {
      this.visible = false
    },
    confirm () {
      this.$refs.form.validate((result) => {
        if (result) {
          let listS = []
          this.addOuList.forEach((element) => {
            listS.push(element.orgId)
          })
          this.form.orgIdList = listS
          quotaOffsetApi.save(this.form).then((res) => {
            if (res.code == '0') {
              this.$message.success(res.message)
              this.visible = false
              this.getQuerydata()
            } else {
              this.$message.console.error(res.message)
            }
          })
        }
      })
    },

    getQuerydata (params) {
      this.queryParam = params
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    addHandle (row) {
      for (let i in this.form) {
        this.form[i] = ''
      }
      this.dialogTitle = '配额执行偏差报表新增'
      this.visible = true
      this.mode = 'add'
    },
    handleCurrentChange (val) {
      this.currentRows = val
    }
  }
}
</script>
