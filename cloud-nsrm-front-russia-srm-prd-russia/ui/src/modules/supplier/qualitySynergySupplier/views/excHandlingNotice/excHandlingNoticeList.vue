<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />
      <MainHeader
        v-if="userType === 'BUYER'"
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <el-button
            type="primary"
            @click="addOne"
          >
            {{
              $t("common.add")
            }}
          </el-button>
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
        :comActive="$attrs['changeTab']"
        :url="queryUrl"
        @getFooter="getFooter"
        @getFooterSize="getFooterSize"
      />
    </el-main>
  </el-container>
</template>

<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { adaptDictData, parseTime } from '@/utils'
import ExportExcel from 'lib@/components/export-excel'
import excHandlingNoticeDetail from './excHandlingNoticeDetail.vue'
import CToolbar from 'lib@/components/c-toolbar'
import OrganizationSelector from 'lib@/components/organization-selector'
import { excHandlingNotice } from 'mods@/qualitySynergySupplier/api'
export default {
  name: 'ExcHandlingNoticeList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    excHandlingNoticeDetail,
    ExportExcel,
    OrganizationSelector,
    CToolbar
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      curRole: this.$store.getters.userType,
      queryUrl: '/api-pef/quasupplierenotice/listPage',
      name: 'excHandlingNoticeTable',
      tableName: 'excHandlingNoticeList',
      gridData: [],
      pageSize: 15,
      gridId: 'list',
      selectList: [],
      currentRow: null,
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      getFooterNum: null,
      getFooterSizeNum: null,
      isModify: false,
      globalNickname: null,
      statusList: [
        { value: 'draft', label: this.$t('qualitySynergy.draft') }, // 拟定
        { value: 'published', label: this.$t('qualitySynergy.published') }, // 已发布
        {
          value: 'vendorChecked',
          label: this.$t('qualitySynergy.vendorChecked')
        }, // 供应商已查阅
        { value: 'invalid', label: this.$t('qualitySynergy.invalid') } // 已作废
      ],
      preArr: [
        {
          prop: 'orgId',
          label: this.$t('dataConfMod.orgId'),
          type: 'OUorganizationSelector'
        }, // 业务实体
        {
          prop: 'purchaserName',
          label: this.$t('bidMod.quotePurchasor') // 采购员
        },
        {
          prop: 'releaseDate',
          label: this.$t('dataConfMod.publishTime'),
          type: 'date'
        }, // 发布日期
        {
          prop: 'vendorName',
          label: this.$t('common.vendor'), // 供应商
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_sup_company_info_display'
        },
        {
          prop: 'problemType',
          label: this.$t('qualitySynergy.problemType'), // 问题类别
          type: 'select',
          options: [
            {
              value: '交易延期',
              label: this.$t('qualitySynergy.problemTypeList[0]')
            }, // 交易延期
            {
              value: '质量问题',
              label: this.$t('qualitySynergy.problemTypeList[1]')
            }, // 质量问题
            {
              value: '质量事故',
              label: this.$t('qualitySynergy.problemTypeList[2]')
            }, //  质量事故
            {
              value: '有害物质超标',
              label: this.$t('qualitySynergy.problemTypeList[3]')
            }, // 有害物质超标
            {
              value: '供应商考核',
              label: this.$t('qualitySynergy.problemTypeList[4]')
            }, // 供应商考核
            {
              value: '其它',
              label: this.$t('qualitySynergy.problemTypeList[5]')
            } // 其它
          ]
        },
        {
          prop: 'orderStatus',
          label: this.$t('bidMod.billstatus'), // 单据状态
          type: 'dict',
          code: 'PERF_ITEM_EX_HANDLER_STATUS'
        }
      ],
      queryParam: {}
    }
  },
  computed: {
    userType () {
      return this.$store.getters.userType
    }
  },
  watch: {
    $route: {
      // 当前功能已经打开的时候监听是否是从其他功能跳转过来的
      deep: true,
      immediate: true,
      handler () {
        if (
          this.$route.params.from === 'fromFun' &&
          this.$route.params.funName === 'excHandlingNotice'
        ) {
          let noticeId = Number(this.$route.params.formId)
          let formNo = this.$route.params.formNo // 流程标题
          let row = {
            ...this.$route.params,
            noticeId,
            requirementHeadNum: formNo // tab 标题显示
          }
          this.readOne(row)
        }
      }
    }
  },
  created () {
    // this.queryUrl = this.curRole === 'BUYER' ? '/api-pef/quasupplierenotice/listPage' : '/api-pef/quasupplierenotice/listPage'
    let _this = this

    this.tableHeader = [
      {
        prop: 'noticeId',
        label: this.$t('qualitySynergy.excNoticeId'), // 异常问题处理通知单号
        minWidth: 165,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.readOne(row)
        }.bind(this),
        formattor (val) {
          return val || '--'
        }
      },
      {
        prop: 'vendorCode',
        label: this.$t('common.vendorCode'), // 供应商编码
        minWidth: 120
      },
      {
        prop: 'vendorName',
        label: this.$t('common.vendorName'), // 供应商名称
        minWidth: 120
      },
      {
        prop: 'problemType',
        label: this.$t('qualitySynergy.problemType'), // 问题类别
        minWidth: 100
      },
      {
        prop: 'fineAmount',
        label: this.$t('qualitySynergy.fineAmount'), // 处罚金额
        width: 100
      },
      {
        prop: 'releaseDate',
        label: this.$t('qualitySynergy.releaseDate'), // 发布时间
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'orgName',
        label: this.$t('dataConfMod.orgId'), // 业务实体
        minWidth: 120
      },
      {
        prop: 'orderStatus',
        label: this.$t('bidMod.billstatus'), // 单据状态
        width: 100,
        formattor (val) {
          return _this.$getDictLabelByValue(_this.statusList, val)
        }
      },
      {
        prop: 'supplierReadTime',
        label: this.$t('qualitySynergy.supplierReadTime'), // 供应商查阅时间
        width: 140,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'operation',
        label: this.$t('common.operation'),
        width: 120,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          {
            callback: function (row) {
              row.orderStatus === 'draft' ? this.editOne(row) : this.readOne(row)
            }.bind(this),
            formattor (val, row) {
              if (row.orderStatus === 'draft') {
                return _this.$t('common.edit')
              } else {
                return _this.$t('bidMod.management')
              }
            },
            show: row =>
              ['draft'].includes(row.orderStatus) && _this.curRole === 'BUYER'
          },
          {
            callback: function (row) {
              this.deleteOne(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.delete')
            },
            show: row => ['draft'].includes(row.orderStatus) && _this.curRole === 'BUYER'
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
    getQuerydata (v) {
      this.queryParam = Object.assign({}, v)
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    syncFilterParams (values) {
      this.queryParam = values
    },
    handleCurrentChange (val) {
      this.currentRow = val
    },
    getFooter (data) {
      this.getFooterNum = data.value
    },
    getFooterSize (data) {
      this.getFooterSizeNum = data.value
    },
    addOne () {
      this.$emit('tab-add', {
        component: excHandlingNoticeDetail,
        params: {
          flag: 'add',
          tabName: 'excHandlingNoticeDetail'
        },
        title: this.$t('qualitySynergy.addExcHandlingNotice'), // 创建异常问题处理单
        name: 'excHandlingNoticeDetail'
      })
    },
    editOne (row) {
      this.$emit('tab-add', {
        component: excHandlingNoticeDetail,
        params: {
          flag: 'edit',
          row: row,
          noticeId: row.noticeId,
          tabName: 'excHandlingNoticeDetail' + row.noticeId
        },
        title: row.noticeId,
        name: 'excHandlingNoticeDetail' + row.noticeId
      })
    },
    readOne (row) {
      this.$emit('tab-add', {
        component: excHandlingNoticeDetail,
        params: {
          flag: 'readOnly',
          row: row,
          noticeId: row.noticeId,
          tabName: 'excHandlingNoticeDetail' + row.noticeId
        },
        title: row.noticeId,
        name: 'excHandlingNoticeDetail' + row.noticeId
      })
    },
    deleteOne (row) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          excHandlingNotice.noticeDelete({ noticeId: row.noticeId })
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
