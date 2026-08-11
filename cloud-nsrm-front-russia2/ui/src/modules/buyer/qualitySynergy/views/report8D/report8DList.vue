<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <FormWrapper
        :form-array="preArr"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />
      <MainHeader v-if="this.$store.getters.userType === 'BUYER'">
        <template slot="left">
          <AuthorityButton type="primary" code="sup:report8D:add" @click="addNew">
            {{ $t('common.add') }}
          </AuthorityButton>
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
        :url="url"
        row-key="reportId"
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
import report8DDetail from './report8DDetail'
import { getDictItemList } from '@/api/common'
import { adaptDictData, parseTime } from '@/utils'
import ExportExcel from 'lib@/components/export-excel'
import { inspectionStandard } from 'modb@/qualitySynergy/api'
export default {
  name: 'Report8DList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    report8DDetail,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      url: '/api-pef/report8D/listPage',
      dict: {
        YES_OR_NO: [],
        REPORT_STATUS: []
      },
      curRole: this.$store.getters.userType,
      name: 'report8DTable',
      tableName: 'report8DList',
      reviewFormNumber: '',
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
      preArr: [
        {
          prop: 'vendorName',
          label: this.$t('common.vendor'), // 供应商
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_sup_company_info_display_buyer'
        },
        { prop: 'reportId', label: this.$t('qualitySynergy.reportNo') }, // 报告编号
        {
          prop: 'creationDate',
          label: this.$t('dataConfMod.startDay'),
          type: 'date'
        }, // 开始日期
        {
          prop: 'documentType',
          label: this.$t('qualitySynergy.sourceDocumentType'),
          type: 'dict', // 字典类型
          code: 'QUA_8D_REPORT_DOCUMENT_TYPE' // 字典code
        }
      ],
      queryParam: {},
      statusList: [],
      purchaseTypeList: []
    }
  },
  watch: {
    $route: {
      // 当前功能已经打开的时候监听是否是从其他功能跳转过来的
      deep: true,
      immediate: true,
      handler () {
        if (
          this.$route.params.from === 'incomingException' &&
          this.$route.params.funName === 'report8D'
        ) {
          let row = this.$route.params.fdSubject
          this.$emit('tab-add', {
            component: report8DDetail,
            params: {
              flag: 'incomingExceptionAdd',
              row: row,
              tabName: 'report8DDetail'
            },
            title: this.$t('qualitySynergy.create8DReport'), // 创建8D报告
            name: 'report8DDetail',
            ctrlHeight: true
          })
        } else if (
          this.$route.params.from === 'processException' &&
          this.$route.params.funName === 'report8D'
        ) {
          let row = this.$route.params.fdSubject
          this.$emit('tab-add', {
            component: report8DDetail,
            params: {
              flag: 'processExceptionAdd',
              row: row,
              tabName: 'report8DDetail'
            },
            title: this.$t('qualitySynergy.create8DReport'), // 创建8D报告
            name: 'report8DDetail',
            ctrlHeight: true
          })
        }
        if (this.$route.params.from === 'fromFun' && this.$route.params.funName === 'report8D') {
          let reportId = Number(this.$route.params.formId)
          let formNo = this.$route.params.formNo // 流程标题
          let row = {
            ...this.$route.params,
            reportId,
            requirementHeadNum: formNo // tab 标题显示
          }
          this.readOne(row)
        }
      }
    }
  },
  created () {
    // this.url = this.curRole === 'BUYER' ? '/api-pef/report8D/listPage' : '/api-pef/report8D/listPage'
    this.globalNickname = this.$store.getters.userInfo
      ? this.$store.getters.userInfo.username
      : null
    let _this = this
    this.tableHeader = [
      {
        prop: 'reportId',
        label: this.$t('qualitySynergy.reportNo'), // 报告编号
        minWidth: 150,
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
        prop: 'reportStatus',
        label: this.$t('qualitySynergy.reportStatus'), // 报告状态
        width: 100,
        dataType: 'dict',
        code: 'REPORT_STATUS'
      },
      {
        prop: 'isClosed',
        label: this.$t('qualitySynergy.isItClosed'),
        minWidth: 150,
        dataType: 'dict', // 数据类型为字典
        code: 'YES_OR_NO' // 字典code
      },
      {
        prop: 'documentType',
        label: this.$t('qualitySynergy.sourceDocumentType'),
        minWidth: 120,
        dataType: 'dict', // 数据类型为字典
        code: 'QUA_8D_REPORT_DOCUMENT_TYPE' // 字典code
      }, // 来源单据类型
      {
        prop: 'sendDate',
        label: this.$t('qualitySynergy.sendDate'), // 发出日期
        minWidth: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'responseDate',
        label: this.$t('qualitySynergy.responseDate'), // 回复日期
        minWidth: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'groupLeader',
        label: this.$t('qualitySynergy.groupLeader'), // 组长
        width: 80
      },
      {
        prop: 'groupMember',
        label: this.$t('qualitySynergy.groupMember'), // 成员
        width: 80
      },
      {
        prop: 'why1',
        label: this.$t('qualitySynergy.why1'), // 根本原因追查why1
        minWidth: 150
      },
      {
        prop: 'processOutflowReason',
        label: this.$t('qualitySynergy.processOutflowReason'), // 制程流出原因
        minWidth: 120
      },
      {
        prop: 'qualityOutflowReason',
        label: this.$t('qualitySynergy.qualityOutflowReason'), // 品质流出原因
        minWidth: 120
      },
      {
        prop: 'clientConfirmationTotal',
        label: this.$t('qualitySynergy.clientConfirmationTotal'), // 客户端确认数量
        width: 135
      },
      {
        prop: 'transitConfirmationTotal',
        label: this.$t('qualitySynergy.transitConfirmationTotal'), // 在途确认数量
        width: 120
      },
      {
        prop: 'organizationConfirmationTotal',
        label: this.$t('qualitySynergy.organizationConfirmationTotal'), // 仓库库存确认数量
        width: 150
      },
      {
        prop: 'clientHandleWay',
        label: this.$t('qualitySynergy.clientHandleWay'), // 客户端确认处理方式
        minWidth: 155
      },
      {
        prop: 'transitHandleWay',
        label: this.$t('qualitySynergy.transitHandleWay'), // 在途确认处理方式
        minWidth: 150
      },
      {
        prop: 'organizationHandleWay',
        label: this.$t('qualitySynergy.organizationHandleWay'), // 仓库库存处理方式
        minWidth: 150
      },
      {
        prop: 'interimMeasuresAgent',
        label: this.$t('qualitySynergy.interimMeasuresAgent'), // 临时措施责任人
        width: 150
      },
      {
        prop: 'interimMeasuresDate',
        label: this.$t('qualitySynergy.interimMeasuresDate'), // 临时措施处理日期
        width: 150,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'interimMeasuresFinishDate',
        label: this.$t('qualitySynergy.interimMeasuresFinishDate'), // 临时措施完成日期
        width: 150,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'countermeasurePerson',
        label: this.$t('qualitySynergy.countermeasurePerson'),
        width: 110
      }, // 永久对策者
      {
        prop: 'excuteDepartment',
        label: this.$t('qualitySynergy.excuteDepartment'), // 永久对策执行部门
        width: 150
      },
      {
        prop: 'countermeasureFinishDate',
        label: this.$t('qualitySynergy.countermeasureFinishDate'), // 永久对策完成日期
        width: 150,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'excutePerson',
        label: this.$t('qualitySynergy.excutePerson'),
        minWidth: 130
      }, // 永久对策执行者
      {
        prop: 'countermeasureRootReason',
        label: this.$t('qualitySynergy.countermeasureRootReason'), // 根本原因对策
        minWidth: 130
      },
      {
        prop: 'countermeasureOutflowReason',
        label: this.$t('qualitySynergy.countermeasureOutflowReason'), // 流出原因对策
        minWidth: 130
      },
      {
        prop: 'itemExceptionId1',
        label: this.$t('qualitySynergy.itemExceptionId1'),
        minWidth: 150
      }, // 来料检验报告单号1
      {
        prop: 'checkResult1',
        label: this.$t('qualitySynergy.checkResult1'),
        minWidth: 110
      }, // 检验结果1
      {
        prop: 'itemExceptionId2',
        label: this.$t('qualitySynergy.itemExceptionId2'),
        minWidth: 150
      }, // 来料检验报告单号2
      {
        prop: 'checkResult2',
        label: this.$t('qualitySynergy.checkResult2'),
        minWidth: 110
      }, // 检验结果2
      {
        prop: 'itemExceptionId3',
        label: this.$t('qualitySynergy.itemExceptionId3'),
        minWidth: 150
      }, // 来料检验报告单号3
      {
        prop: 'checkResult3',
        label: this.$t('qualitySynergy.checkResult3'),
        minWidth: 110
      }, // 检验结果3
      {
        prop: 'beforeImprovement',
        label: this.$t('qualitySynergy.beforeImprovement'), // 改善前流程
        minWidth: 110
      },
      {
        prop: 'afterImprovement',
        label: this.$t('qualitySynergy.afterImprovement'), // 改善后流程
        minWidth: 110
      },
      {
        prop: 'oldProcessAbolishDate',
        label: this.$t('qualitySynergy.oldProcessAbolishDate'), // 旧流程废除日期
        width: 130,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'newProcessAbolishDate',
        label: this.$t('qualitySynergy.newProcessAbolishDate'), // 新流程执行日期
        width: 130,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'parallelExpansion',
        label: this.$t('qualitySynergy.parallelExpansion'),
        minWidth: 100
      }, // 平行展开
      { prop: 'madeBy', label: this.$t('qualitySynergy.madeBy'), width: 100 }, /// 制定人
      {
        prop: 'report8DChecker',
        label: this.$t('qualitySynergy.reviewer'),
        width: 100
      }, // 审核人
      {
        prop: 'approvedBy',
        label: this.$t('qualitySynergy.approvedBy'),
        width: 100
      }, // 批准人
      {
        prop: 'operation',
        label: _this.$t('common.operation'),
        width: 140,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          {
            // 编辑
            callback: function (row) {
              this.editOne(row)
            }.bind(this),
            show: row =>
              row.isClosed !== 'Y' && row.reportStatus === 'DRAFT' && this.curRole === 'BUYER',
            formattor: (val, row) => {
              return _this.$t('common.edit')
            }
          },
          {
            // 删除
            callback: function (row) {
              this.deleteOne(row)
            }.bind(this),
            show: row =>
              row.isClosed !== 'Y' && row.reportStatus === 'DRAFT' && this.curRole === 'BUYER',
            formattor: val => {
              return _this.$t('common.delete')
            }
          },
          {
            callback: function (row) {
              this.editOne(row)
            }.bind(this),
            show: row =>
              row.isClosed !== 'Y' &&
              row.reportStatus === 'SUPPLIER_HAVE_FEEDBACK' &&
              this.curRole === 'BUYER' &&
              row.qualityCheckerBy === this.$store.getters.user.username,
            formattor: val => this.$t('common.approve')
          },
          {
            // 关闭
            callback: function (row) {
              this.closeOne(row)
            }.bind(this),
            show: row => row.isClosed !== 'Y' && this.curRole === 'BUYER' && !['DRAFT'].includes(row.reportStatus),
            formattor: (val, row) => {
              return this.$t('common.close')
            }
          },
          {
            callback: function (row) {
              this.editOne(row)
            }.bind(this),
            show: row =>
              row.isClosed !== 'Y' && row.reportStatus === 'PUBLISHED' && this.curRole === 'VENDOR',
            formattor: val => this.$t('qualitySynergy.retroaction')
          }
        ]
      }
    ]
    this.defaultTableHeader = this.tableHeader

    this.$nextTick(() => {
      this.getQuerydata()
      this.fetchDictData()
    })
  },
  methods: {
    addNew () {
      this.$emit('tab-add', {
        component: report8DDetail,
        params: {
          flag: 'add',
          row: {},
          tabName: 'report8DDetail'
        },
        title: this.$t('qualitySynergy.report8D'),
        name: 'report8DDetail',
        ctrlHeight: true
      })
    },
    async fetchDictData () {
      let keyList = Object.keys(this.dict)
      let res = await getDictItemList(
        keyList.map(key => {
          return { dictCode: key }
        }),
      )
      if (res.data) {
        keyList.forEach((key, index) => {
          this.dict[key] = adaptDictData(res.data[index][key])
        })
      }
    },
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
    exportList () {
      let params = {}
      if (this.getFooterNum && this.getFooterSizeNum) {
        params = Object.assign(
          {},
          this.queryParam,
          { pageNum: this.getFooterNum },
          { pageSize: this.getFooterSizeNum },
        )
      } else if (this.getFooterNum) {
        params = Object.assign(
          {},
          this.queryParam,
          { pageNum: this.getFooterNum },
          { pageSize: 15 },
        )
      } else if (this.getFooterSizeNum) {
        params = Object.assign(
          {},
          this.queryParam,
          { pageNum: 1 },
          { pageSize: this.getFooterSizeNum },
        )
      } else {
        params = Object.assign({}, this.queryParam, { pageNum: 1 }, { pageSize: 15 })
      }
    },
    addOne () {
      this.$emit('tab-add', {
        component: report8DDetail,
        params: {
          flag: 'add',
          tabName: 'report8DDetail'
        },
        title: this.$t('qualitySynergy.create8DReport'), // 创建8D报告
        name: 'report8DDetail',
        ctrlHeight: true
      })
    },
    editOne (row) {
      this.$emit('tab-add', {
        component: report8DDetail,
        params: {
          flag: 'edit',
          row: row,
          tabName: 'report8DDetail' + row.reportId
        },
        title: row.reportId,
        name: 'report8DDetail' + row.reportId,
        ctrlHeight: true
      })
    },
    closeOne (row) {
      this.$emit('tab-add', {
        component: report8DDetail,
        params: {
          flag: 'edit',
          row: row,
          tabName: 'report8DDetail' + row.reportId
        },
        title: row.reportId,
        name: 'report8DDetail' + row.reportId,
        ctrlHeight: true
      })
    },
    readOne (row) {
      this.$emit('tab-add', {
        component: report8DDetail,
        params: {
          flag: 'readOnly',
          row: row,
          tabName: 'report8DDetail' + row.reportId
        },
        title: row.reportId,
        name: 'report8DDetail' + row.reportId,
        ctrlHeight: true
      })
    },
    deleteOne (row) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          inspectionStandard.reportDelete({ reportId: row.reportId })
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
