<template>
  <el-container
    class="flex-container the_currency_wrapper"
    direction="vertical"
  >
    <el-main>
      <form-wrapper
        :form-array="queryForm"
        @getFormData="getQuerydata"
      />
      <main-header
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <el-button
            type="primary"
            @click="addNew"
          >
            <!-- 新增 -->
            {{ $t('common.add') }}
          </el-button>
        </template>
      </main-header>
      <table-view
        :ref="gridId"
        :table-data="tableList"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :comActive="$attrs['changeTab']"
        url="/api-sup/review/siteConfigCate/listPageByParm"
      />
    </el-main>
    <!-- 新增 编辑弹框区域-->
    <srm-dialog
      :title="dialogTitle"
      :visible.sync="dialogFormVisible"
      :close-on-click-modal="false"
    >
      <el-form
        ref="orgform"
        :model="configVendorModel.configForm"
        :rules="configVendorModel.rules"
      >
        <el-row :gutter="50">
          <el-col :span="12">
            <!-- 品类名称 -->
            <el-form-item
              :label="$t('common.categoryName')"
              prop="categoryName"
            >
              <!-- <quick-search :showInput="configVendorModel.configForm.vendorName"
                show-key="companyName"
                :scope-data="configVendorModel.configForm"
                name="scc_sup_company_info_display" @close-quicksearch="getCompanyObj"/> -->
              <c-category-select
                v-model="configVendorModel.configForm.categoryName"
                :scope="configVendorModel.configForm"
                show-key="categoryName"
                @select="comfirmCategory"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <!-- 现场评审周期 -->
            <el-form-item
              :label="$t('dataConfMod.siteCycle')"
              prop="siteCycle"
            >
              <el-select
                v-model="configVendorModel.configForm.siteCycle"
              >
                <el-option
                  v-for="item in sitePeriod"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item
              :label="$t('dataConfMod.sqePerson')"
              prop="sqePerson"
            >
              <el-input v-model="configVendorModel.configForm.sqePerson" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item
              :label="$t('dataConfMod.startDate')"
              prop="startDate"
            >
              <el-date-picker
                v-model="configVendorModel.configForm.startDate"
                type="date"
                :placeholder="$t('common.pleaseSelectDate')"
                format="yyyy-MM-dd"
                value-format="yyyy-MM-dd"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item
              :label="$t('dataConfMod.endDate')"
              prop="endDate"
            >
              <el-date-picker
                v-model="configVendorModel.configForm.endDate"
                type="date"
                :placeholder="$t('common.pleaseSelectDate')"
                format="yyyy-MM-dd"
                value-format="yyyy-MM-dd"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div
        slot="footer"
        class="dialog-footer"
      >
        <el-button @click="dialogFormVisible = false">
          <!-- 取 消 -->
          {{ $t('common.cancel') }}
        </el-button>
        <el-button
          type="primary"
          @click="comfirmSave"
        >
          <!-- 确 定 -->
          {{ $t('common.confirm') }}
        </el-button>
      </div>
    </srm-dialog>
  </el-container>
</template>
<script>

import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import CCategorySelect from 'lib@/components/c-category-select'
import { parseTime, adaptDictData } from '@/utils'
import { getDictItemList } from '@/api/common'

export default {
  name: 'ByCategory',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    CCategorySelect
  },
  data () {
    return {
      pageSize: 15,
      gridId: 'byCategoryList',
      currentRow: null,
      showFilterBar: 1,
      queryParam: {},
      dialogFormVisible: false,
      queryForm: [], // 查询条件
      tableHeader: [], // 表格列数据
      tableList: [],
      tableTotal: 0, // 分页数据
      tableLoading: false,
      curOpt: 'add',
      dialogTitle: this.$t('dataConfMod.dialogTitleAdd'), // '新增配置'
      tableSelection: [],
      sitePeriod: [],
      configVendorModel: {
        configForm: {
          categoryName: '',
          categoryCode: '',
          categoryId: null,
          siteCycle: '',
          sqePerson: '', // SQE/责任人
          startDate: '',
          endDate: ''
        },
        rules: {
          categoryName: [{ required: true, message: this.$t('dataConfMod.msgCategoryName') }], // '请输入品类'
          siteCycle: [{ required: true, message: this.$t('dataConfMod.msgSiteCycle') }], // '请选择现场评审周期'
          sqePerson: [{ required: true, message: this.$t('dataConfMod.msgSqePerson') }], // '请输入SQE/责任人'
          startDate: [{ required: true, message: this.$t('dataConfMod.msgStartDate') }]// '请输入生效日期'
        }
      }
    }
  },
  created () {
    let _this = this
    this.queryForm = [
      { prop: 'categoryName',
        label: () => this.$t('common.category'), // '品类'
        type: 'catSelect',
        showKey: 'categoryName'
      },
      { prop: 'siteCycle',
        label: () => this.$t('dataConfMod.siteCycle'), // '现场评审周期'
        type: 'select',
        options: []
      },
      { prop: 'enabled',
        label: () => this.$t('dataConfMod.enabled'), // '是否生效'
        type: 'select',
        options: [
          { label: this.$t('common.yes'), value: 'Y' }, // '是'
          { label: this.$t('common.no'), value: 'N' }// '否'
        ]
      },
      { prop: 'sqePerson',
        label: () => this.$t('dataConfMod.sqePerson')// 'SQE/责任人'
      }
    ]
    this.tableHeader = [
      {
        prop: 'categoryCode',
        label: () => this.$t('common.categoryCode')// '品类编码'
      },
      {
        prop: 'categoryName',
        label: () => this.$t('common.categoryName')// '品类名称'
      },
      {
        prop: 'siteCycle',
        label: () => this.$t('dataConfMod.siteCycle'), // '现场评审周期'
        formattor (val) {
          return _this.$getDictLabelByValue(_this.sitePeriod, val)
        }
      },
      {
        prop: 'sqePerson',
        label: () => this.$t('dataConfMod.sqePerson')// 'SQE/责任人'
      },
      { prop: 'startDate',
        label: () => this.$t('dataConfMod.startDate')// '生效日期'
      },
      { prop: 'endDate',
        label: () => this.$t('dataConfMod.endDate')// '失效日期'
      },
      { prop: 'lastUpdatedUserName', // lastUpdatedBy
        label: () => this.$t('common.updatePeople')// '更新人'
      },
      { prop: 'lastUpdateDate',
        label: () => this.$t('common.updateTime')// '更新时间'
      },
      {
        label: () => this.$t('common.operation'), // '操作'
        width: '100',
        fixed: 'right',
        editType: 'none',
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.editDetail(row)
        }.bind(this),
        formattor (val) {
          return _this.$t('common.edit')// '编辑'
        }
      }
    ]

    this.fatchDictData()
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    getQuerydata (v) {
      this.queryParam = v
      let params = v || {}
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    // 获取数据字典
    fatchDictData () {
      // 批量查询字典
      let dictParamsArr = [
        { dictCode: 'SITE_PERIOD' } // 现场评审周期
      ]
      getDictItemList(dictParamsArr).then(res => {
        const [SITE_PERIOD] = res.data
        this.sitePeriod = adaptDictData(SITE_PERIOD.SITE_PERIOD, 'dict')
        this.queryForm[1].options = this.sitePeriod
      })
    },
    // 选择品类回调
    comfirmCategory (val, data) {
      data.categoryId = val ? val.categoryId : null
      data.categoryCode = val ? val.categoryCode : ''
      data.categoryName = val ? val.categoryName : ''
    },
    addNew () {
      this.curOpt = 'add'
      this.controlHandle(this.curOpt)
    },
    // 新增、编辑
    controlHandle (type) {
      if (type === 'add') {
        // 新增
        this.dialogTitle = this.$t('dataConfMod.dialogTitleAdd')// '新增配置'
        let formObj = this.configVendorModel.configForm
        Object.keys(formObj).forEach(key => (formObj[key] = ''))
        this.configVendorModel.configForm.startDate = new Date()
      } else {
        // 修改
        this.dialogTitle = this.$t('dataConfMod.dialogTitleEdit')// '编辑配置'
      }
      this.dialogFormVisible = true
    },
    // 选中
    handleSelectionChange (value) {
      this.tableSelection = value
    },
    saveData () {
      this.saveOrUpdateHandle(this.curOpt)
    },
    // 新增编辑组织数据
    saveOrUpdateHandle (opt) {
      let submitData = this.configVendorModel.configForm
      if (opt === 'add') { // 新增
        delete submitData.configCateId
      }
      this.$api.base.basicSetting.siteConfigCateSaveOrUpdate(submitData).then(res => {
        if (res) {
          // 返回数据处理
          this.$message({
            message: res.message,
            type: 'success'
          })
          this.getQuerydata() // 重新查询数据
          this.dialogFormVisible = false
        }
      })
    },
    editDetail (row) {
      this.curOpt = 'edit'
      this.configVendorModel.configForm.categoryName = row.categoryName
      this.configVendorModel.configForm.categoryCode = row.categoryCode
      this.configVendorModel.configForm.categoryId = row.categoryId
      this.configVendorModel.configForm.siteCycle = row.siteCycle
      this.configVendorModel.configForm.sqePerson = row.sqePerson
      this.configVendorModel.configForm.startDate = row.startDate
      this.configVendorModel.configForm.endDate = row.endDate
      this.configVendorModel.configForm.configCateId = row.configCateId
      this.dialogFormVisible = true
      this.controlHandle(this.curOpt)
    },
    comfirmSave () {
      this.saveOrUpdateHandle(this.curOpt)
    }
  }
}
</script>
<style scoped lang="scss">

</style>
