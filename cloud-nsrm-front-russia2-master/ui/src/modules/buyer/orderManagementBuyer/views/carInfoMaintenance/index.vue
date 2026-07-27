<template>
  <el-container
    class="flex-container-notab the_inventory_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        @getFormData="getQuerydata"
      />
      <!-- <main-header> -->
      <!-- <template slot="left">
          <el-button type="primary" @click="editTab('add')">
            {{ $t("common.add") }}
          </el-button>
          <el-button
            type="primary"
            :disabled="!currentRow"
            @click="editTab"
          >
            {{ $t("common.edit") }}
          </el-button>
          <el-button
            type="primary"
            :disabled="!currentRow"
            @click="deleteOne"
          >
            {{ $t("common.delete") }}
          </el-button>
        </template> -->
      <!-- </main-header> -->
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        url="/api-sup-ce/order/carInfo/listPage"
      />
      <!-- 弹框区域-->
      <!-- 车辆信息新增 -->
      <srm-dialog
        :title="$t('orderMod.addCarInfo')"
        :visible.sync="dialogFormVisible"
        :close-on-click-modal="false"
        size="middle"
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
            <!-- <el-col>
              <el-form-item
                label="采购组织"
                :label-width="formLabelWidth"
                prop="purOrg"
              >
                <OrganizationSelectTree
                  @select="treeselectChange"
                  v-model="form.purOrg"
                />
              </el-form-item>
            </el-col> -->
            <el-col>
              <el-form-item
                :label="$t('purchaseDemand.startDate')"
                :label-width="formLabelWidth"
                prop="effectiveDate"
              >
                <el-date-picker
                  v-model="form.effectiveDate"
                  type="date"
                  :format="$formatDatePicker"
                  :placeholder="$t('purchaseDemand.datePicker')"
                />
              </el-form-item>
            </el-col>
            <el-col>
              <el-form-item
                :label="$t('orderMod.buyerOrderSynergy.carType')"
                :label-width="formLabelWidth"
                prop="carType"
              >
                <DictSelect
                  v-model="form.carType"
                  code="CAR_TYPE"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row type="flex">
            <el-col>
              <el-form-item
                :label="$t('orderMod.buyerOrderSynergy.licensePlate2')"
                :label-width="formLabelWidth"
                prop="licensePlate"
              >
                <el-input v-model="form.licensePlate" />
              </el-form-item>
            </el-col>
            <el-col>
              <el-form-item
                :label="$t('orderMod.buyerOrderSynergy.expirationDate')"
                :label-width="formLabelWidth"
                prop="expirationDate"
              >
                <el-date-picker
                  v-model="form.expirationDate"
                  type="date"
                  :format="$formatDatePicker"
                  :placeholder="$t('vendorMod.datePicker')"
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
            {{ $t("common.cancel") }}
          </el-button>
          <el-button
            type="primary"
            @click="addOne"
          >
            {{ $t("common.confirm") }}
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
import { parseTime } from '@/utils'
import OrganizationSelectTree from 'lib@/components/organization-selector'

export default {
  name: 'AccessFlowSetting',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    OrganizationSelectTree
  },
  provide () {
    return { context: this }
  },
  data () {
    return {
      pageSize: 15,
      gridId: 'list',
      selectList: [],
      currentRow: null,
      tableHeader: [],
      tableData: [],
      form: {
        licensePlate: '',
        carType: '',
        effectiveDate: '',
        expirationDate: ''
      },
      accessTypeList: [
        { label: 'v1', value: this.$t('orderMod.buyerOrderSynergy.process1') },
        { label: 'v2', value: this.$t('orderMod.buyerOrderSynergy.process2') }
      ],
      rules: {
        licensePlate: [
          { required: true, message: this.$t('common.pleaseInput') }
        ],
        carType: [{ required: true, message: this.$t('common.pleaseInput') }],
        effectiveDate: [
          { required: true, message: this.$t('common.pleaseInput') }
        ],
        expirationDate: [
          { required: true, message: this.$t('common.pleaseInput') }
        ]
      },
      dialogFormVisible: false,
      queryParam: {},
      formLabelWidth: '100px',
      isActive: false,
      preArr: [
        {
          prop: 'status',
          label: () => this.$t('orderMod.buyerOrderSynergy.status'),
          type: 'dict',
          code: 'CAR_INFO_STATUS'
        },
        {
          prop: 'carType',
          label: () => this.$t('orderMod.buyerOrderSynergy.carType'),
          type: 'dict',
          code: 'CAR_TYPE'
        },
        {
          prop: 'licensePlate',
          label: () => this.$t('orderMod.buyerOrderSynergy.licensePlate')
        }
      ]
    }
  },
  created () {
    this.tableHeader = [
      {
        prop: 'lastUpdateDate',
        label: () => this.$t('orderMod.buyerOrderSynergy.lastUpdateDate'),
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'status',
        label: () => this.$t('orderMod.buyerOrderSynergy.status'),
        dataType: 'dict',
        code: 'CAR_INFO_STATUS'
      },
      {
        prop: 'licensePlate',
        label: () => this.$t('orderMod.buyerOrderSynergy.licensePlate2'),
        width: 100
      },
      {
        prop: 'carType',
        label: () => this.$t('orderMod.buyerOrderSynergy.carType'),
        dataType: 'dict',
        code: 'CAR_TYPE'
      },
      {
        prop: 'effectiveDate',
        label: () => this.$t('orderMod.buyerOrderSynergy.effectiveDate'),
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'expirationDate',
        label: () => this.$t('orderMod.buyerOrderSynergy.expirationDate'),
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      }
    ]
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => this.getQuerydata())
  },
  mounted () {
  },
  methods: {
    treeselectChange (node, id, scope) {},
    getQuerydata (v) {
      if (v) this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    // 编辑tab
    editTab (type) {
      if (type === 'add') {
        // 新增
        for (let i in this.form) {
          this.form[i] = ''
        }
      } else {
        // 修改
        for (let i in this.form) {
          this.form[i] = this.currentRow[i]
        }
      }
      this.dialogFormVisible = true
    },
    deleteOne (val) {
      this.$confirm(this.$t('common.delRow'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          // debugger;
        })
        .catch(() => {})
    },
    addOne () {
      // 验证form表单
      this.$refs.form.validate(valid => {
        if (valid) {
          // =====
        } else {
          return false
        }
      })
    },
    handleCurrentChange (val) {
      this.currentRow = val
    }
  }
}
</script>
<style scoped lang="scss"></style>
<style>
.vue-treeselect__portal-target {
  z-index: 9999 !important;
}
</style>
