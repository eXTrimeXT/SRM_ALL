<template>
  <el-container
    class="flex-container demoorder_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="filterConfig"
        @getFormData="getQuerydata"
      >
      </FormWrapper>
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <AuthorityButton
            type="primary"
            @click="addHandlePop"
          >
            {{
              $t("common.add")
            }}
          </AuthorityButton>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :adept-mei-ql="true"
        :source="payTypeApi.query"
      />
    </el-main>
    <srm-dialog
      :title="dialogTitle"
      :visible.sync="visible"
    >
      <div class="demoorderEdit">
        <el-form
          ref="form"
          :model="form"
          :rules="rules"
        >
          <el-row :gutter="32">
            <el-col :span="12">
              <el-form-item
                :label="$t('contractMod.payExplain')"
                prop="payExplain"
              >
                <el-input v-model="form.payExplain" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item
                prop="condFactorId"
                :label="$t('contractMod.condFactor')"
              >
              <dict-select
                v-model="form.condFactorId"
                code ="condFactorList"
                custom-select-type="condFactorList"
                multiple
                @change="condFactorChange"
              />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item
                prop="startDate"
                :label="$t('contractMod.startDate')"
              >
              <el-date-picker
                v-model="form.startDate"
                type="date"
                :format="$formatDatePicker"
                value-format="yyyy-MM-dd"
                :placeholder="$t('contractMod.startDate')"
              />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item
                prop="endDate"
                :label="$t('contractMod.endDate')"
              >
                <el-date-picker
                  v-model="form.endDate"
                  type="date"
                  :format="$formatDatePicker"
                  value-format="yyyy-MM-dd"
                  :placeholder="$t('vendorMod.datePicker')"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item
                prop="valueRange"
                :label="$t('contractMod.valueRange')"
              >
              <dict-select v-model="form.valueRange" code="valueRange" />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
      <template #footer class="dialog-footer">
        <el-button @click="cancel">
          {{ $t("common.cancel") }}
        </el-button>
        <el-button
          type="primary"
          @click="confirm"
        >
          {{ $t("common.confirm") }}
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
import { payTypeApi } from 'modb@/demo/api'
import { transformMQL } from '@/library/utils/util'
export default {
  name: 'MeiqlVue',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      payTypeApi: payTypeApi,
      name: 'demoorderList',
      tableName: 'demoorderTable',
      pageSize: 15,
      gridId: 'list',
      dialogTitle: this.$t('cusEntry.supplement20250211.billDetails'),  // '单据详情'
      currentRows: [],
      visible: false,
      form: {
        payExplain: '',
        condFactor: null,
        condFactorId: null,
        startDate: null,
        endDate: null,
        valueRange: ''
      },
      rules: {
        payExplain: [{required: true, message: this.$t('dataConfMod.required')}],
        condFactorId: [{required: true, message: this.$t('dataConfMod.required') }],
        valueRange: [{required: true, message: this.$t('dataConfMod.required') }]
      },
      filterParams: {},
      tableHeader: [
        {
          prop: 'payExplain',
          label: () => this.$t('contractMod.payExplain'),
          width: 100,
        },
        {
          prop: 'condFactor',
          label: () => this.$t('contractMod.condFactor'),
          width: 100
        },
        {
          prop: 'valueRange',
          label: () => this.$t('contractMod.valueRange'),
          width: 100,
          dataType: 'dict',
          code:'valueRange'
        },
        {
          label: () => this.$t('contractMod.startDate'),
          prop: 'startDate',
          width: 100
        },
        {
          label: () => this.$t('contractMod.endDate'),
          prop: 'endDate',
          width: 100,
          dataType: 'dateTime'
        },
        {
          label: () => this.$t('contractMod.createdBy'),
          prop: 'createdUserName',
          width: 100
        },
        {
          label: () => this.$t('contractMod.creationDate'),
          prop: 'creationDate',
          width: 100,
          dataType: 'dateTime'
        },
        {
          label: () => this.$t('contractMod.lastUpdatedBy'),
          prop: 'lastUpdatedUserName',
          width: 100
        },
        {
          label: () => this.$t('contractMod.lastUpdateDate'),
          prop: 'lastUpdateDate',
          dataType: 'dateTime',
          width: 100
        },
        {
          prop: 'operation',
          label: this.$t('components.headers.operation'),  // '操作'
          showType: 'buttons',
          btnStyle: 'text',
          fixed: 'right',
          width: 130,
          buttons: [
            {
              callback: (row) => this.editHandlePop(row),
              formattor: () => {
                return this.$t('bidMod.management')
              }
            }
          ]
        }
      ],
      filterConfig: [
        {
          prop: 'payExplain',
          label: () => this.$t('contractMod.payExplain')
        },
        {
          prop: 'condFactor',
          label: () => this.$t('contractMod.condFactor')
        },
        {
          prop: 'startDate',
          label: () => this.$t('contractMod.startDate'),
          type: 'date'
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
    condFactorChange (val) {
      this.form.condFactorId = val
    },
    // 弹框新增
    addHandlePop () {
      this.mode = 'add'
      this.form = {
        payExplain: '',
        condFactor: null,
        condFactorId: null,
        startDate: null,
        endDate: null,
        valueRange: ''
      }
      this.dialogTitle = this.$t('common.newDocument')  // '新增单据'
      this.visible = true
    },
    // 弹框编辑
    editHandlePop (row) {
      this.mode = 'edit'
      this.form = {
        ...row,
        condFactorId: (row.condFactorId).indexOf(',') > -1 ? row.condFactorId.split(',').filter(Boolean).map(i => Number(i)) : row.condFactorId
      }
      this.dialogTitle = this.$t('cusEntry.supplement20250211.editBill')  // '编辑单据'
      this.visible = true
    },
    cancel () {
      this.visible = false
    },
    confirm () {
      this.$refs.form.validate((result) => {
        if (result) {
          const flag = this.mode
          // 新增时不用提交主键值
          let formAll = {
            ...this.form,
            condFactorId: (this.form.condFactorId).toString(),
            condFactor: this.form.condFactorId.length > 0 ? this.form.condFactorId.map(i => this.$getDictLabel('condFactorList', i)).join(',') : null
          }
          const { payTypeId, ...rest } = formAll
          // 参数构造
          if (flag === 'add') {
            let formData = transformMQL.save('PayType',[rest])
            payTypeApi.save(formData).then((res) => {
              this.$message({
                type: 'success',
                message: res.message
              })
              this.visible = false
            })
          } else if (flag === 'edit') {
            let formData = transformMQL.save('PayType',[{...this.form}], 'update')
            payTypeApi.update(formData).then((res) => {
              this.$message({
                type: 'success',
                message: res.message
              })
              this.visible = false
            })
          }
          this.$nextTick(() => {
            this.getQuerydata()
          })
        }
      })
    },
    // 查询列表页数据
    getQuerydata (params) {
      this.queryParam = transformMQL.listGetData('PayType', params)
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    }
  }
}
</script>
