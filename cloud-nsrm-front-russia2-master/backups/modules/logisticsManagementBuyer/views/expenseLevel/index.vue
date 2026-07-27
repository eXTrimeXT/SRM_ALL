<template>
  <el-container
    class="flex-container-notab the_contractPaymentTypeList_wrapper"
    direction="vertical"
  >
    <el-main>
      <form-wrapper
        :form-array="preArr"
        :init-active="true"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />
      :l-span="22"
      :r-span="2"
      >
      <template slot="left">
        <el-button
          type="primary"
          @click="editTab('add')"
        >
          {{
            $t("common.add")
          }}
        </el-button>
        <el-button
          type="primary"
          @click="batchSave"
        >
          {{
            $t("common.save")
          }}
        </el-button>
        <el-button
          type="primary"
          @click="batchDelete"
        >
          {{
            $t("common.delete")
          }}
        </el-button>
        <el-button
          type="primary"
          @click="doBatchEffect"
        >
          {{
            $t("common.active")
          }}
        </el-button>
        <el-button
          type="primary"
          @click="doBatchIneffect"
        >
          {{
            $t("common.inactive")
          }}
        </el-button>
      </template>
      </main-header>

      <table-view
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :checkbox="true"
        :check-change="handleSelectionChange"
        url="/api-pd/logistics/expense-level/listPageByParam"
        @afterQuery="afterQuery"
      >
        <template #subLevelCode="props">
          <el-input
            v-model="props.scope.row.subLevelCode"
            :disabled="
              !(props.scope.row.editable || props.scope.row.status === 'DRAFT')
            "
          />
        </template>
        <template #rangeFrom="props">
          <el-input
            v-model="props.scope.row.rangeFrom"
            :disabled="
              !(props.scope.row.editable || props.scope.row.status === 'DRAFT')
            "
          />
        </template>
        <template #rangeTo="props">
          <el-input
            v-model="props.scope.row.rangeTo"
            :disabled="
              !(props.scope.row.editable || props.scope.row.status === 'DRAFT')
            "
          />
        </template>
        <template #comments="props">
          <el-input
            v-model="props.scope.row.comments"
            :disabled="
              !(props.scope.row.editable || props.scope.row.status === 'DRAFT')
            "
          />
        </template>
      </table-view>
      <!-- 费用项定义 -->
      <srm-dialog
        v-el-drag-dialog
        :title="$t('route.expenseItem')"
        size="large"
        :visible.sync="dialogFormVisible"
        :close-on-click-modal="false"
      >
        <el-form
          ref="form"
          :model="form"
          class="form-incontainer"
          :rules="rules"
          label-width="80px"
          label-position="top"
        >
          <el-row type="flex">
            <el-col>
              <!-- 业务模式 -->
              <el-form-item
                :label="$t('logisticsMod.businessMode')"
                :label-width="formLabelWidth"
                prop="businessModeCode"
              >
                <el-select
                  v-model="form.businessModeCode"
                  filterable
                  clearable
                >
                  <el-option
                    v-for="item in businessTypeList"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col>
              <!-- 运输方式 -->
              <el-form-item
                :label="$t('bid_mod.transportType')"
                :label-width="formLabelWidth"
                prop="transportModeCode"
              >
                <el-select
                  v-model="form.transportModeCode"
                  filterable
                  clearable
                >
                  <el-option
                    v-for="item in transportTypeList"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row type="flex">
            <el-col>
              <el-form-item
                label="leg"
                :label-width="formLabelWidth"
                prop="legCode"
              >
                <el-select
                  v-model="form.legCode"
                  filterable
                  clearable
                >
                  <el-option
                    v-for="item in legCodeList"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col>
              <!-- 费用项 -->
              <el-form-item
                :label="$t('logisticsMod.expenseItem')"
                :label-width="formLabelWidth"
                prop="chargeCode"
              >
                <el-select
                  v-model="form.chargeCode"
                  filterable
                  clearable
                  @change="setchargeName"
                >
                  <el-option
                    v-for="item in chargeCodeList"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <div
          slot="footer"
          class="dialog-footer"
        >
          <el-button
            type="primary"
            @click="addOneItem"
          >
            {{ $t("common.confirm") }}
          </el-button>
          <el-button @click="dialogFormVisible = false">
            {{ $t("common.cancel") }}
          </el-button>
        </div>
      </srm-dialog>
    </el-main>
  </el-container>
</template>
<script>
import ExportExcel from 'lib@/components/export-excel'
import { downloadFileLink } from 'lib@/utils/file'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { parseTime } from '@/utils'
import QuickSearch from 'lib@/components/QuickSearch'

export default {
  name: 'ExpenseLevel',
  components: {
    TableView,
    MainHeader,
    ExportExcel,
    FormWrapper,
    QuickSearch
  },
  // mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      name: 'categoryAssignRuleTable',
      tableName: 'expenseLevel',
      iModal: {
        title: this.$t('common.import'),
        extraData: {
          sourceType: 'WEB_APP',
          uploadType: 'FASTDFS',
          fileModular: 'base',
          fileFunction: 'accountAccess',
          fileType: 'excel'
        },
        upLoadUrl: '/api-base/businessType/importExcel'
      },
      reviewFormNumber: '',
      gridData: [],
      pageSize: 15,
      gridId: 'list',
      selectList: [],
      tableList: [],
      currentRow: null,
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      initActive: true,
      // collapseTagsBool: true,
      dialogFormVisible: false,
      formLabelWidth: '100px',
      preArr: [
        { prop: 'subLevelCode', label: this.$t('logisticsMod.subLevelCode') }, // 费用级别编码
        { prop: 'comments', label: this.$t('common.remark') }, // 备注
        { prop: 'status',
label: this.$t('common.status'),
          type: 'dict',
          code: 'LOGISTICS_STATUS'
        } // 状态
      ],
      form: {
        expenseLevelId: null,
        businessModeCode: null,
        transportModeCode: null,
        legCode: null,
        chargeCode: null,
        chargeName: null
      },
      rules: {
        chargeCode: [
          { required: true, message: this.$t('logisticsMod.msgChargeCode') }
        ], // 请选择费用项编码
        businessModeCode: [
          { required: true, message: this.$t('logisticsMod.msgBusinessMode') }
        ], // 请选择业务模式
        transportModeCode: [
          { required: true, message: this.$t('logisticsMod.msgTransportWay') }
        ] // 请选择运输方式
      },
      queryParam: {},
      dutyList: [],
      yesNoOptions: [
        { value: 'Y', label: this.$t('common.yes') },
        { value: 'N', label: this.$t('common.no') }
      ],
      businessTypeList: [],
      transportTypeList: [],
      pubRangeList: [],
      paymentType: [],
      projectTypeList: [],
      chargeCodeList: [],
      legCodeList: []
    }
  },
  created () {
    let _this = this
    this.tableHeader = [
      {
        prop: 'subLevelCode',
        label: this.$t('logisticsMod.subLevelCode'), // 费用级别编码
        minWidth: 200,
        showType: 'slot',
        slot: 'subLevelCode'
      },
      {
        prop: 'rangeFrom',
        label: this.$t('logisticsMod.rangeFrom'), // 范围  起（>)
        width: 120,
        showType: 'slot',
        slot: 'rangeFrom'
      },
      {
        prop: 'rangeTo',
        label: this.$t('logisticsMod.rangeTo'), // 范围  止(<=)
        width: 120,
        showType: 'slot',
        slot: 'rangeTo'
      },
      {
        prop: 'comments',
        label: this.$t('common.remark'), // 备注
        width: 120,
        showType: 'slot',
        slot: 'comments'
      },
      {
        prop: 'status',
        label: this.$t('common.status'), // 状态
        width: 100,
        dataType: 'dict',
        code: 'LOGISTICS_STATUS'

      },
      {
        prop: 'createdUserName', // createdBy
        label: this.$t('common.creator'),
width: 120
      },
      {
        prop: 'creationDate',
        label: this.$t('common.creationTime'),
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'lastUpdatedUserName', // lastUpdatedBy
        label: this.$t('common.updatePeople'),
        width: 120
      },
      {
        prop: 'lastUpdateDate',
        label: this.$t('common.updateTime'),
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'operation',
        label: _this.$t('common.operation'),
        width: 150,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          /* {
            callback: function(row) {
              this.editTab("edit", row);
            }.bind(this),
            formattor(val) {
              return "编辑";
            },
            show: row=>(row.status=== "DRAFT")
          }, */
          {
            callback: function (row) {
              this.doEffect(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.active')
            },
            show: row =>
              ['DRAFT', 'INEFFECTIVE'].includes(row.status) &&
              !!row.expenseLevelId
          },
          {
            callback: function (row) {
              this.doIneffect(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.inactive')
            },
            show: row => row.status === 'EFFECTIVE'
          },
          {
            callback: function (row) {
              this.deleteOne(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.delete')
            },
            show: row => row.status === 'DRAFT'
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
    afterQuery (data) {
      this.tableList = data
    },
    getQuerydata (v) {
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange (val) {
      this.currentRow = val
    },
    handleSelectionChange (val) {
      this.selectList = val
    },
    delOne (index) {
      this.$refs[this.gridId].deleteRow(index)
    },
    editTab (type, row) {
      if (type === 'add') {
        // 新增
        // for (let i in this.form) {
        //   this.form[i] = null;
        // }
        this.$refs[this.gridId].addOneEditableColumn({ status: 'DRAFT' })
      } else {
        // 修改
        // for (let i in this.form) {
        //   this.form[i] = row[i];
        // }
        row.editable = true
      }
      // this.dialogFormVisible = true;
    },
    doBatchEffect () {
      if (this.selectList.length === 0) {
        return this.$message.error(this.$t('contractMod.msgSelData')) // 请选择数据!
      }
      if (
        this.selectList.some(
          v => v.status !== 'DRAFT' && v.status !== 'INEFFECTIVE'
        )
      ) {
        return this.$message.error(
          this.$t('logisticsMod.msgSelDraftOrInAData')
        ) // 请选择拟定或失效状态的数据!
      }
      this.$http({
        url: '/api-pd/logistics/expense-level/effectiveExpenseLevels',
        method: 'POST',
        data: this.selectList.map(v => v.expenseLevelId).filter(v => !!v),
        loading: true
      })
        .then(data => {
          this.$message.success(this.$t('common.success'))
          this.getQuerydata()
        })
        .catch(err => {
          console.log(err)
        })
    },
    doBatchIneffect () {
      if (this.selectList.length === 0) {
        return this.$message.error(this.$t('contractMod.msgSelData'))
      }
      if (this.selectList.some(v => v.status !== 'EFFECTIVE')) {
        return this.$message.error(this.$t('logisticsMod.msgSelActiveData'))
      }
      this.$http({
        url:
          '/api-pd/logistics/expense-level/inEffectiveExpenseLevels',
        method: 'POST',
        data: this.selectList.map(v => v.expenseLevelId).filter(v => !!v),
        loading: true
      })
        .then(data => {
          this.$message.success(this.$t('common.success'))
          this.getQuerydata()
        })
        .catch(err => {
          console.log(err)
        })
    },
    doEffect (row) {
      this.$http({
        url: '/api-pd/logistics/expense-level/effectiveExpenseLevels',
        method: 'POST',
        data: [row.expenseLevelId],
        loading: true
      })
        .then(data => {
          this.$message.success(this.$t('common.success'))
          this.getQuerydata()
        })
        .catch(err => {
          console.log(err)
        })
    },
    doIneffect (row) {
      this.$http({
        url:
          '/api-pd/logistics/expense-level/inEffectiveExpenseLevels',
        method: 'POST',
        data: [row.expenseLevelId],
        loading: true
      })
        .then(data => {
          this.$message.success(this.$t('common.success'))
          this.getQuerydata()
        })
        .catch(err => {
          console.log(err)
        })
    },
    syncFilterParams (values) {
      this.queryParam = values
    },
    setchargeName2 (row) {
      let obj = this.chargeCodeList.find(v => v.value === row.chargeCode) || {}
      row.chargeName = obj.label
    },
    setchargeName (val) {
      let obj = this.chargeCodeList.find(v => v.value === val) || {}
      this.form.chargeName = obj.label
    },
    batchSave () {
      // if(this.selectList.length ===0) {
      //   return this.$message.error("请选择数据!");
      // }
      this.$http({
        url: '/api-pd/logistics/expense-level/saveExpenseLevels',
        method: 'POST',
        data: this.tableList.filter(v => v.status === 'DRAFT'),
        loading: true
      })
        .then(data => {
          // this.dialogFormVisible = false;
          this.$message.success(this.$t('common.successSave'))
          this.getQuerydata()
        })
        .catch(err => {
          console.log(err)
        })
    },
    batchDelete () {
      if (this.selectList.length === 0) {
        return this.$message.error(this.$t('contractMod.msgSelData'))
      }
      if (this.selectList.some(v => v.status !== 'DRAFT')) {
        return this.$message.error(this.$t('logisticsMod.msgNotDelete'))
      }
      for (let row of this.selectList) {
        if (!row.expenseLevelId) {
          let index = this.$refs[this.gridId].tableData.indexOf(row)
          this.delOne(index)
        }
      }
      let idArr = this.selectList.map(v => v.expenseLevelId).filter(v => !!v)
      if (idArr.length === 0) return
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.$http({
            url: '/api-pd/logistics/expense-level/deleteByIds',
            method: 'POST',
            data: idArr,
            loading: true
          })
            .then(data => {
              this.$message.success(this.$t('common.successDelete'))
              this.getQuerydata()
            })
            .catch(err => {
              console.log(err)
            })
        })
        .catch(() => {})
    },
    addOneItem () {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.$http({
            url: '/api-pd/logistics/expense-item/saveExpenseItem',
            method: 'POST',
            data: this.form,
            loading: true
          })
            .then(data => {
              this.dialogFormVisible = false
              this.$message.success(this.$t('common.successSave'))
              this.getQuerydata()
            })
            .catch(err => {
              console.log(err)
            })
        } else {
          return false
        }
      })
    },
    deleteOne (row) {
      if (!row.expenseLevelId) {
        let index = this.$refs[this.gridId].tableData.indexOf(row)
        this.delOne(index)
        return
      }
      if (!row.expenseLevelId) return
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.$http({
            url: '/api-pd/logistics/expense-level/deleteByIds',
            method: 'POST',
            data: [row.expenseLevelId],
            loading: true
          })
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
<style scoped lang="scss"></style>
