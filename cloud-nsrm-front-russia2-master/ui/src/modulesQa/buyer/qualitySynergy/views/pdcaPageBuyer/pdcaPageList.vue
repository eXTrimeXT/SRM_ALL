<template>
  <el-container class="flex-container the_quick_list__outter_wrapper" direction="vertical">
    <el-main>
      <FormWrapper :formArray="preArr" @getFormData="getQuerydata" />
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :check-change="selectionChange"
        :page-size="pageSize"
        :preQueryData="queryParam"
        :adeptMeiQl="true"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-qc/api-ql/spcPdcaRecordBuyer/findDatasByConditions"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import PdcaEnter from './pdcaEnter'
import { pdcaPage } from '@/modulesQa/buyer/qualitySynergy/api'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import { transformMQL } from '@/library/utils/util'
import { parseTime } from '@/utils'
import { mapGetters } from 'vuex'
const { pcdaPageCondition } = pdcaPage
export default {
  name: 'PdcaPageList',
  components: {
    TableView, MainHeader, FormWrapper
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      pageSize: 15,
      gridId: 'list',
      selectList: [],
      currentRow: null,
      tableHeader: [],
      tableData: [],
      queryParam: {
        'customerCompanyId': '',
        'customerOrgId': '',
        'supplierId': '',
        'lineCode': '',
        'workCenter': '',
        'productModel': '',
        'monitoringFeature': '',
        'recordId': '',
        'status': '',
        'workGroup': '',
        'exceptionEvent': '',
        'exceptionCatelog': '',
        'result': '',
        'startDate': '',
        'endDate': ''
      },
      preArr: [
        { prop: 'customerOrgId',
          label: this.$t('quality.customerOrgName'),
          type: 'OUorganizationSelector'
        },
        { prop: 'lineCode',
          label: this.$t('quality.lineCode'),
          type: 'select',
          options: []
        },
        { prop: 'workCenter',
          label: this.$t('quality.workCenter'),
          type: 'select',
          options: []
        },
        { prop: 'productModel',
          label: this.$t('quality.productModel'),
          type: 'select',
          options: []
        },
        { prop: 'monitoringFeature',
          label: this.$t('quality.monitoringFeature'),
          type: 'select',
          options: []
        },
        { prop: 'pdcaRecordId',
          label: this.$t('quality.pdcaRecordId'),
          type: 'select',
          options: []
        },
        { prop: 'status',
          label: this.$t('quality.pdcaStatus'),
          type: 'select',
          options: []
        },
        { prop: 'exceptionEvent',
          label: this.$t('quality.exceptionEvent'),
          type: 'select',
          options: []
        },
        { prop: 'exceptionCatelog',
          label: this.$t('quality.exceptionCatelog'),
          type: 'select',
          options: []
        },
        { prop: 'result',
          label: this.$t('quality.result'),
          type: 'select',
          options: []
        },
        { prop: 'workGroup',
          label: this.$t('quality.workGroup'),
          type: 'select',
          options: []
        },
        { prop: 'startDate',
          label: this.$t('quality.startDate'),
          type: 'date'
        },
        { prop: 'endDate',
          label: this.$t('quality.endDate'),
          type: 'date'
        },
        {
          prop: 'drawingsArea',
          label: this.$t('quality.drawingsArea')
        }

      ],
      pcdaStatus: []
    }
  },
  computed: {
    ...mapGetters([
      'userInfo'
    ])
  },
  created () {
    // 带参数进入页面
    if (this.$route.query.pageParams) {
      this.queryParam.pdcaRecordId = this.$route.query.pageParams
      this.preArr[6].default = this.$route.query.pageParams
      if (!this.$route.query.fromSelf) {
        this.preArr.map(item => {
          item.disabled = true
        })
      }
    }
    this.getPageCondition()
    this.getQuerydata(this.queryParam)
  },
  mounted () {
    this.tableHeader = [
      { prop: 'recordId',
        label: this.$t('quality.pdcaRecordId'),
        minWidth: 140
      },
      { prop: 'datetimeCreated',
        label: this.$t('quality.datetimeCreated'),
        minWidth: 150,
        formattor: function (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : '--'
        }
      },
      { prop: 'rectificationDate',
        label: this.$t('quality.rectificationDate'),
        minWidth: 150,
        formattor: function (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : '--'
        }
      },
      { prop: 'productCode',
        label: this.$t('quality.productCode'),
        minWidth: 135
      },
      { prop: 'workCenter',
        label: this.$t('quality.workCenter'),
        minWidth: 120
      },
      { prop: 'monitoringFeature',
        label: this.$t('quality.monitoringFeature')
        // width:80,
      },
      {
        prop: 'characterUnit',
        label: this.$t('quality.characterUnit')
      },
      {
        prop: 'drawingsArea',
        label: this.$t('quality.drawingsArea')
      },
      { prop: 'exceptionCatelog',
        label: this.$t('quality.exceptionCatelog')
        // width:70,
      },
      { prop: 'exceptionEvent',
        label: this.$t('quality.exceptionEvent'),
        minWidth: 180
      },
      { prop: 'customerOrgName',
        label: this.$t('quality.customerOrgName'),
        minWidth: 120
      },
      { prop: 'lineCode',
        label: this.$t('quality.lineCode')
      },
      { prop: 'workGroup',
        label: this.$t('quality.workGroup')
      },
      { prop: 'onLineEquipment',
        label: this.$t('quality.onLineEquipment')
      },
      { prop: 'targetValue',
        label: this.$t('quality.targetValue'),
        align: 'right'
      },
      { prop: 'standardMax',
        label: this.$t('quality.standardMax'),
        align: 'right'
      },
      { prop: 'standardMin',
        label: this.$t('quality.standardMin'),
        align: 'right'
      },
      { prop: 'avgUcl',
        label: this.$t('quality.avgUcl2'),
        align: 'right'
      },
      { prop: 'avgLcl',
        label: this.$t('quality.avgLcl2'),
        align: 'right'
      },
      { prop: 'avgMcl',
        label: this.$t('quality.avgMcl2'),
        align: 'right'
      },
      { prop: 'exceptionRemark',
        label: this.$t('quality.exceptionRemark'),
        width: 120
      },
      { prop: 'reasonAnalysis',
        label: this.$t('quality.reasonAnalysis'),
        width: 120
      },
      { prop: 'improvePlan',
        label: this.$t('quality.improvePlan'),
        width: 120
      },
      { prop: 'improvedCompleteDate',
        label: this.$t('quality.improvedCompleteDate'),
        minWidth: 150,
        formattor: function (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : '--'
        }
      },
      { prop: 'improvedName',
        label: this.$t('quality.improvedName'),
        minWidth: 120
      },
      { prop: 'improvedDeptName',
        label: this.$t('quality.improvedDeptName'),
        minWidth: 100
      },
      { prop: 'verificationResult',
        label: this.$t('quality.verificationResult'),
        minWidth: 120
      },
      {
        prop: 'status',
        label: this.$t('quality.status'),
        formattor: (val, row) => this.$getDictLabelByValue(this.pcdaStatus, val)
      },
      { prop: 'operation',
        label: this.$t('common.operation'),
        minWidth: 130,
        showType: 'buttons',
        fixed: 'right',
        buttons: [
          {
            btnStyle: 'text',
            // disabled: function (row) {
            //   return row.status === 'UNFILLED'
            // },
            callback: function (row) {
              this.editTab('detail', row)
            }.bind(this),
            show: row => row.status === 'COMPLETE',
            formattor: (val) => {
              return this.$t('common.approve')
            }
          }
          // 审批按钮
        ]
      }
    ]
  },
  methods: {
    // 初始化条件查询
    getPageCondition () {
      let transformParams = transformMQL.save('spcPdcaRecordBuyer', {}, 'pageCondition')
      pcdaPageCondition(transformParams).then(response => {
        const data = response.data.records[0]
        let lineCodeList = []; let workCenterList = []; let productModelList = []; let onLineEquipmentList = []; let monitoringFeatureList = []; let pdcaRecordIdList = []; let statusList = []; let exceptionEventList = []
        let exceptionCatelogList = []; let resultList = []; let workGroupList = []
        // 产线下拉
        Object.keys(data.lineCodeMap).forEach(item => {
          let newObj = {}
          newObj.label = item
          newObj.value = data.lineCodeMap[item]
          lineCodeList.push(newObj)
        })
        // 工作中心下拉
        Object.keys(data.workCenterMap).forEach(item => {
          let newObj = {}
          newObj.label = item
          newObj.value = data.workCenterMap[item]
          workCenterList.push(newObj)
        })
        // 规格型号下拉
        Object.keys(data.productModelMap).forEach(item => {
          let newObj = {}
          newObj.label = item
          newObj.value = data.productModelMap[item]
          productModelList.push(newObj)
        })
        // 监控特性下拉
        Object.keys(data.monitoringFeatureMap).forEach(item => {
          let newObj = {}
          newObj.label = item
          newObj.value = data.monitoringFeatureMap[item]
          monitoringFeatureList.push(newObj)
        })
        // PDCA单号
        Object.keys(data.pdcaRecordIdMap).forEach(item => {
          let newObj = {}
          newObj.label = item
          newObj.value = data.pdcaRecordIdMap[item]
          pdcaRecordIdList.push(newObj)
        })
        // PDCA单状态
        Object.keys(data.statusMap).forEach(item => {
          let newObj = {}
          newObj.label = item
          newObj.value = data.statusMap[item]
          statusList.push(newObj)
        })
        this.pcdaStatus = statusList
        // 异常事件
        Object.keys(data.exceptionEventMap).forEach(item => {
          let newObj = {}
          newObj.label = item
          newObj.value = data.exceptionEventMap[item]
          exceptionEventList.push(newObj)
        })
        // 异常分类
        Object.keys(data.exceptionCatelogMap).forEach(item => {
          let newObj = {}
          newObj.label = item
          newObj.value = data.exceptionCatelogMap[item]
          exceptionCatelogList.push(newObj)
        })
        // 验证结果
        Object.keys(data.resultMap).forEach(item => {
          let newObj = {}
          newObj.label = item
          newObj.value = data.resultMap[item]
          resultList.push(newObj)
        })
        // 班组
        Object.keys(data.workGroupMap).forEach(item => {
          let newObj = {}
          newObj.label = item
          newObj.value = data.workGroupMap[item]
          workGroupList.push(newObj)
        })
        for (let item of this.preArr) {
          switch (item.prop) {
          case 'lineCode':
            item.options = lineCodeList
            break
          case 'workCenter':
            item.options = workCenterList
            break
          case 'productModel':
            item.options = productModelList
            break
          case 'monitoringFeature':
            item.options = monitoringFeatureList
            break
          case 'pdcaRecordId':
            item.options = pdcaRecordIdList
            break
          case 'status':
            item.options = statusList
            break
          case 'exceptionEvent':
            item.options = exceptionEventList
            break
          case 'exceptionCatelog':
            item.options = exceptionCatelogList
            break
          case 'result':
            item.options = resultList
            break
          case 'workGroup':
            item.options = workGroupList
            break
          }
        }
      })
    },
    getQuerydata (params = {}) {
      this.queryParam = transformMQL.listGetData('spcPdcaRecordBuyer', params, 'lastUpdateDate', undefined, 'findDatasByConditions', null, { startDate: 'ge', endDate: 'le' })
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    // 编辑tab
    editTab (type, param) {
      let tab = {}
      if (type == 'enter') {
        // PDCA报告录入
        tab = {
          component: PdcaEnter,
          params: { flag: 'enter', params: param },
          title: () => this.$t('quality.pdcdEnter'),
          name: 'PCDAEnter'
        }
      }
      if (type == 'detail') {
        // PDCA报告查看
        tab = {
          component: PdcaEnter,
          params: { flag: 'detail', params: param, tabName: 'PCDAEnter' + param.recordId },
          title: () => this.$t('quality.pdcaFlow') + param.recordId,
          name: 'PCDAEnter' + param.recordId
        }
      }
      this.$emit('tab-add', tab)
    },
    deleteOne () {

    },
    handleCurrentChange (val) {
      this.currentRow = val
    },
    selectionChange (val) {},
    // 双击行
    rowDblclick (row, event, column) {
      this.editTab('edit')
    }
  }
}
</script>
<style scoped lang="scss">

</style>
