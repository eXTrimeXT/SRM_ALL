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
        :preQueryData="queryParam"
        :adeptMeiQl="true"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-qc/api-ql/SpcRuleStandardBuyer/query"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
// import SPCDataEnter from './SPCDataEnter'
import SPCDataHistory from './SPCDataHistory'
import { qualityProject, spcData } from '@/modulesQa/buyer/qualitySynergy/api'
import { transformMQL } from '@/library/utils/util'
import { mapGetters } from 'vuex'
const { pageCondition } = qualityProject

export default {
  name: 'Page2',
  components: {
    TableView, MainHeader, FormWrapper
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      gridId: 'list',
      selectList: [],
      currentRow: null,
      tableHeader: [],
      tableData: [],
      queryParam: {
        'customerCompanyId': '',
        'customerOrgId': '',
        'productModel': '',
        'onLineEquipment': '',
        'lineCode': '',
        'erpSupplierId': '',
        'workCenter': '',
        'monitoringFeature': ''
      },
      preArr: [
        { prop: 'monitorListNo',
          label: this.$t('quality.monitorListNo')
        },
        { prop: 'customerOrgId',
          label: this.$t('quality.customerOrgName'),
          type: 'OUorganizationSelector'
        },
        { prop: 'workCenter',
          label: this.$t('quality.workCenter'),
          type: 'select',
          options: []
        }, { prop: 'onLineEquipment',
          label: this.$t('quality.onLineEquipment'),
          type: 'select',
          options: []
        }, { prop: 'monitoringFeature',
          label: this.$t('quality.monitoringFeature'),
          type: 'select',
          options: []
        }, { prop: 'itemCode',
          label: this.$t('quality.itemCode'),
          type: 'quicksearch',
          showKey: 'materialCode',
          name: 'scc_base_material_item'
        },
        {
          prop: 'drawingsArea',
          label: this.$t('quality.drawingsArea')
        }
      ]
    }
  },
  computed: {
    ...mapGetters([
      'userInfo'
    ])
  },
  created () {
    this.getPageCondition()
    this.getQuerydata(this.queryParam)
    this.tableHeader = [
      { prop: 'state',
        label: this.$t('quality.state'),
        dataType: 'dict',
        code: 'SPC_STANDARD_STATE'
      },
      { prop: 'monitorListNo',
        label: this.$t('quality.monitorListNo'),
        minWidth: 160
      },
      { prop: 'customerOrgName',
        label: this.$t('quality.customerOrgName'),
        minWidth: 130
      },
      { prop: 'itemCode',
        label: this.$t('quality.itemCode'),
        minWidth: 130
      },
      { prop: 'itemDesc',
        label: this.$t('quality.itemDesc'),
        minWidth: 130
      },
      { prop: 'lineCode',
        label: this.$t('quality.lineCode')
      },
      { prop: 'workCenter',
        label: this.$t('quality.workCenter'),
        minWidth: 130
      },
      { prop: 'onLineEquipment',
        label: this.$t('quality.onLineEquipment')
      },
      { prop: 'monitoringFeature',
        label: this.$t('quality.monitoringFeature')
      },
      { prop: 'spUnit',
        label: this.$t('quality.spUnit'),
        minWidth: 90
      },
      { prop: 'characterUnit',
        label: this.$t('quality.characterUnit')
      },
      { prop: 'drawingsArea',
        label: this.$t('quality.drawingsArea')
      },
      { prop: 'operation',
        label: this.$t('common.operation'),
        minWidth: 140,
        showType: 'buttons',
        fixed: 'right',
        buttons: [
          {
            btnStyle: 'text',
            callback: function (row) {
              this.editTab('history', row)
            }.bind(this),
            formattor: (val) => {
              return this.$t('quality.spc.historyBtn')
            }
          }
        ]
      }
    ]
  },
  methods: {
    // 初始化条件查询
    getPageCondition () {
      let transformParams = transformMQL.save('spcMonitorListBuyer', {}, 'pageCondition')
      pageCondition(transformParams).then(response => {
        const data = response.data.records[0]
        let workCenterList = []; let onLineEquipmentList = []; let monitoringFeatureList = []
        // 工作中心下拉
        Object.keys(data.workCenterMap).forEach(item => {
          let newObj = {}
          newObj.label = item
          newObj.value = data.workCenterMap[item]
          workCenterList.push(newObj)
        })
        // 联机设备下拉
        Object.keys(data.onLineEquipmentMap).forEach(item => {
          let newObj = {}
          newObj.label = item
          newObj.value = data.onLineEquipmentMap[item]
          onLineEquipmentList.push(newObj)
        })
        // 监控特性下拉
        Object.keys(data.monitoringFeatureMap).forEach(item => {
          let newObj = {}
          newObj.label = item
          newObj.value = data.monitoringFeatureMap[item]
          monitoringFeatureList.push(newObj)
        })
        for (let item of this.preArr) {
          switch (item.prop) {
          case 'workCenter':
            item.options = workCenterList
            break
          case 'onLineEquipment':
            item.options = onLineEquipmentList
            break
          case 'monitoringFeature':
            item.options = monitoringFeatureList
            break
          }
        }
      })
    },
    // 查询列表
    getQuerydata (params = {}) {
      this.queryParam = transformMQL.listGetData('SpcRuleStandardBuyer', params, 'lastUpdateDate', undefined, 'query')
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    // 编辑tab
    editTab (type, param) {
      let tab = {}
      if (type === 'enter') {
        // SPC数据录入
        // tab = {
        //   component: SPCDataEnter,
        //   params: { flag: 'enter', param: param, tabName: 'SPCDataEnter' },
        //   title: 'SPC数据录入',
        //   name: 'SPCDataEnter'
        // }
      } else if (type === 'history') {
        // SPC历史数据分析
        console.log(param)
        tab = {
          ctrlHeight: true,
          component: SPCDataHistory,
          params: { flag: 'history', param: param, tabName: 'SPCDataHistory' + param.id },
          title: this.$t('quality.spc.spcDataHistory') + param.id,
          name: 'SPCDataHistory' + param.id
        }
      }
      this.$emit('tab-add', tab)
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
