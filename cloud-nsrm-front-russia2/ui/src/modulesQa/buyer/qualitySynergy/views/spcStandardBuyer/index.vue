<template>
  <el-container class="flex-container-notab the_inventory_wrapper" direction="vertical">
    <el-main>
      <FormWrapper :formArray="preArr" @getFormData="getQuerydata" />
      <MainHeader>
        <template slot="left">
          <!-- <el-button
            type="primary"
            @click="addNew"
          >
            新增
          </el-button>
          <el-button
            :disabled="selectList.length!==1"
            @click="editTab"
          >
            修改
          </el-button>
          <el-button
            :disabled="!selectShow"
            @click="deleteOne"
          >
            删除
          </el-button> -->
          <ExportExcel
            page-url="/api-qc/api-ql/SpcRuleStandardBuyer/query"
            :filter-params="computedQueryParam"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            export-mode="front"
            type="default"
          />
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :checkbox="false"
        :check-change="selectionChange"
        :preQueryData="queryParam"
        :adeptMeiQl="true"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-qc/api-ql/SpcRuleStandardBuyer/query"
      />
      <!--弹框区域-->
      <srm-dialog
        :close-on-click-modal="false"
        :visible.sync="dialogVisible"
        class="c-form-dialog"
        width="70%"
        :title="$t('quality.spcRuleStandard.spcAdd')"
      >
        <el-form
          ref="spcForm"
          :model="dialogModle.dialogForm"
          :rules="dialogModle.rules"
        >
          <srm-row :gutter="24">
            <srm-col :initCol="4">
              <el-form-item
                :label="$t('quality.spcRuleStandard.customerOrgName')"
                prop="customerOrgName"
                required
              >
                <el-input v-model="dialogModle.dialogForm.customerOrgName" disabled />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="4">
              <el-form-item
                :label="$t('quality.spcRuleStandard.productModel')"
                prop="productModel"
                required
              >
                <el-input v-model="dialogModle.dialogForm.productModel" />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="4">
              <el-form-item
                :label="$t('quality.productCode')"
                prop="productCode"
                required
              >
                <el-input v-model="dialogModle.dialogForm.productCode" />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="4">
              <el-form-item
                :label="$t('quality.spcRuleStandard.erpSupplierId')"
                prop="erpSupplierId"
                required
              >
                <el-input v-model="dialogModle.dialogForm.erpSupplierId" />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="4">
              <el-form-item
                :label="$t('quality.lineCode')"
              >
                <el-input v-model="dialogModle.dialogForm.companyName" />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="4">
              <el-form-item
                :label="$t('quality.project.workCenter')"
                prop="workCenter"
                required
              >
                <el-input v-model="dialogModle.dialogForm.workCenter" />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="4">
              <el-form-item
                :label="$t('quality.onLineEquipment')"
                prop="onLineEquipment"
                required
              >
                <el-input v-model="dialogModle.dialogForm.onLineEquipment" />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="4">
              <el-form-item
                :label="$t('quality.monitoringFeature')"
                prop="monitoringFeature"
                required
              >
                <el-input v-model="dialogModle.dialogForm.monitoringFeature" />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="4">
              <el-form-item
                :label="$t('quality.spUnit')"
              >
                <el-input v-model="dialogModle.dialogForm.companyName" />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="4">
              <el-form-item
                :label="$t('quality.standardMax')"
                prop="standardVersion.standardMax"
                required
              >
                <el-input v-model="dialogModle.dialogForm.standardVersion.standardMax" />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="4">
              <el-form-item
                :label="$t('quality.standardMin')"
                prop="standardVersion.standardMin"
                required
              >
                <el-input v-model="dialogModle.dialogForm.standardVersion.standardMin" />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="4">
              <el-form-item
                :label="$t('quality.targetValue')"
                prop="standardVersion.targetValue"
                required
              >
                <el-input v-model="dialogModle.dialogForm.standardVersion.targetValue" />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="4">
              <el-form-item
                :label="$t('quality.spcRuleStandard.avgUcl')"
                prop="standardVersion.avgUcl"
                required
              >
                <el-input v-model="dialogModle.dialogForm.standardVersion.avgUcl" />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="4">
              <el-form-item
                :label="$t('quality.spcRuleStandard.avgLcl')"
                prop="standardVersion.avgLcl"
                required
              >
                <el-input v-model="dialogModle.dialogForm.standardVersion.avgLcl" />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="4">
              <el-form-item
                :label="$t('quality.spcRuleStandard.avgMcl')"
                prop="standardVersion.avgMcl"
                required
              >
                <el-input v-model="dialogModle.dialogForm.standardVersion.avgMcl" />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="4">
              <el-form-item
                :label="$t('quality.spcRuleStandard.rangeUcl')"
                prop="standardVersion.rangeUcl"
                required
              >
                <el-input v-model="dialogModle.dialogForm.standardVersion.rangeUcl" />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="4">
              <el-form-item
                :label="$t('quality.spcRuleStandard.rangeLcl')"
                prop="standardVersion.rangeLcl"
                required
              >
                <el-input v-model="dialogModle.dialogForm.standardVersion.rangeLcl" />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="4">
              <el-form-item
                :label="$t('quality.spcRuleStandard.rangeMcl')"
                prop="standardVersion.rangeMcl"
                required
              >
                <el-input v-model="dialogModle.dialogForm.standardVersion.rangeMcl" />
              </el-form-item>
            </srm-col>
            <!-- <srm-col :initCol="4">
              <el-form-item
                label="版本号"
                prop="standardVersion.version"
                required
              >
                <el-input v-model="dialogModle.dialogForm.standardVersion.version" />
              </el-form-item>
            </srm-col> -->
          </srm-row>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false">
            {{ $t('common.cancel') }}
          </el-button>
          <el-button type="primary" @click="handleDialogSaveClick">
            {{ $t('common.confirm') }}
          </el-button>
        </div>
      </srm-dialog>
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import ExportExcel from 'lib@/components/export-excel'
import { qualityProject, spcStandard } from '@/modulesQa/buyer/qualitySynergy/api'
import { mapGetters } from 'vuex'
import { transformMQL } from '@/library/utils/util'
const { pageCondition } = qualityProject
const { deleteStandards, spcStandardAdd, spcStandardModify } = spcStandard

export default {
  name: 'SpcStandard',
  components: {
    TableView, MainHeader, FormWrapper, ExportExcel
  },
  data () {
    return {
      dictCodes: {
        state: 'SPC_STANDARD_STATE'
      },
      gridId: 'list',
      editTabTxt: '',
      selectList: [],
      selectDel: [],
      tableHeader: [],
      tableData: [],
      dialogVisible: false,
      dialogModle: {
        dialogForm: { // 基础信息
          companyName: '', // 企业名称
          'customerOrgId': 585,
          'customerOrgName': '',
          'erpSupplierId': '',
          'productCode': '',
          'productDesc': '',
          'productModel': '',
          'itemDesc': '',
          'workCenter': '',
          'onLineEquipment': '',
          'monitoringFeature': '',
          'standardVersion': {
            'standardMin': '',
            'standardMax': '',
            'targetValue': '',
            'avgLcl': '',
            'avgUcl': '',
            'avgMcl': '',
            'rangeLcl': '',
            'rangeUcl': '',
            'rangeMcl': '',
            'version': '',
            'action': 'Y'
          }
        },
        rules: {
          companyName: [{ required: true, message: this.$t('common.pleaseInput') }]
        }
      },
      tradeType: [
        {
          id: 1,
          dictItemCode: '是否CPK预警',
          dictItemName: '是否CPK预警'
        },
        {
          id: 2,
          dictItemCode: '与物料无关',
          dictItemName: '与物料无关'
        }
      ],
      queryParam: {
        'customerOrgId': '',
        'onLineEquipment': '',
        'lineCode': '',
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
        }, { prop: 'workCenter',
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
        },
        {
          prop: 'createdFullName',
          label: this.$t('common.creator')
        }
      ]
    }
  },
  computed: {
    ...mapGetters([
      'userInfo'
    ]),
    selectShow () {
      return this.selectList.length > 0
    },
    computedQueryParam () {
      let { pageNum, pageSize } = this.queryParam
      return {
        meiqlPayload: {
          ...this.queryParam
        },
        pageNum,
        pageSize
      }
    }
  },
  created () {
    this.getPageCondition()
    this.getQuerydata(this.queryParam)
    this.tableHeader = [
      { prop: 'state',
        label: this.$t('qualitySynergy.inspectionStandardStatus'),
        dataType: 'dict',
        code: 'SPC_STANDARD_STATE'
      },
      { prop: 'monitorListNo',
        label: this.$t('quality.monitorListNo'),
        minWidth: 160
      },
      { prop: 'customerOrgName',
        label: this.$t('quality.customerOrgName'),
        minWidth: 120
      }, { prop: 'itemCode',
        label: this.$t('quality.itemCode'),
        minWidth: 130
      }, { prop: 'itemDesc',
        label: this.$t('quality.itemDesc'),
        minWidth: 130
      },
      { prop: 'lineCode',
        label: this.$t('quality.lineCode')
      },
      { prop: 'workCenter',
        label: this.$t('quality.workCenter'),
        minWidth: 130
      }, { prop: 'onLineEquipment',
        label: this.$t('quality.onLineEquipment')
      }, { prop: 'monitoringFeature',
        label: this.$t('quality.monitoringFeature')
      }, { prop: 'spUnit',
        label: this.$t('quality.spUnit')
      },
      { prop: 'characterUnit',
        label: this.$t('quality.characterUnit')
      },
      { prop: 'drawingsArea',
        label: this.$t('quality.drawingsArea')
      },
      { prop: 'createdFullName',
        label: this.$t('common.creator')
      },
      { prop: 'standardMax',
        label: this.$t('quality.standardMax'),
        align: 'right'
      }, { prop: 'standardMin',
        label: this.$t('quality.standardMin'),
        align: 'right'
      }, { prop: 'targetValue',
        label: this.$t('quality.targetValue'),
        align: 'right'
      }, { prop: 'avgUcl',
        label: this.$t('quality.avgUcl'),
        minWidth: 130,
        align: 'right'
      }, { prop: 'avgLcl',
        label: this.$t('quality.avgLcl'),
        minWidth: 120,
        align: 'right'
      }, { prop: 'avgMcl',
        label: this.$t('quality.avgMcl'),
        minWidth: 110,
        align: 'right'
      }, { prop: 'rangeUcl',
        label: this.$t('quality.rangeUcl'),
        minWidth: 120,
        align: 'right'
      }, { prop: 'rangeLcl',
        label: this.$t('quality.rangeLcl'),
        minWidth: 120,
        align: 'right'
      }, { prop: 'rangeMcl',
        label: this.$t('quality.rangeMcl'),
        minWidth: 110,
        align: 'right'
      }
      // , {
      //   prop: 'controlDrawings',
      //   label: '控制图',
      //   formattor: (val, row) => this.$getDictLabel('SPC_CONTROL_DRAWINGS_TYPE', val)
      // }
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
    // 删除数据
    deleteOne () {
      deleteStandards(this.selectDel).then(response => {
        const { data } = response
        if (response.success) {
          this.$refs[this.gridId].query()
        }
      })
    },
    // 新增
    addNew () {
      this.editTabTxt = 'add'
      this.dialogVisible = true
    },
    // 导出
    leadingOut () {
      let params = ''
      Object.keys(this.queryParam).forEach(item => {
        if (this.queryParam[item]) params += `${item}=${encodeURIComponent(this.queryParam[item])}&`
      })
      let href = `/quality/front/standard/exportStandards?${params}`
      var a = document.createElement('a')
      a.href = href
      a.download()
      a.click()
    },
    // 特性维护弹窗确定按钮
    handleDialogSaveClick () {
      this.$refs.spcForm.validate((valid) => {
        if (valid) {
          if (this.editTabTxt === 'add') { // 新增
            spcStandardAdd(this.dialogModle.dialogForm).then(response => {
              const { data } = response
              if (response.success) {
                this.dialogVisible = false
                this.$refs[this.gridId].query()
              }
            })
          }
          if (this.editTabTxt === 'change') { // 修改
            spcStandardModify(this.dialogModle.dialogForm).then(response => {
              const { data } = response
              if (response.success) {
                this.dialogVisible = false
                this.$refs[this.gridId].query()
              }
            })
          }
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    // 修改
    editTab () {
      this.dialogModle.dialogForm = JSON.parse(JSON.stringify(this.selectList[0]))
      this.dialogVisible = true
      this.editTabTxt = 'change'
    },
    selectionChange (val) {
      this.selectList = val
      this.selectDel = []
      this.selectList.map(item => {
        this.selectDel.push(item.id)
      })
    }
  }
}
</script>
<style scoped lang="scss">

</style>
