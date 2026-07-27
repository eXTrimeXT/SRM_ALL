<template>
  <el-container
    class="barcoderuleheadEdit"
    direction="vertical"
  >
    <el-main>
      <div class="form-container">
        <el-form
          ref="form"
          :model="form"
          :rules="rules"
        >
          <el-row :gutter="27">
            <el-col :span="6">
              <el-form-item
                prop="ruleName"
                label="规则名称"
              >
                <el-input v-model="form.ruleName" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="ruleDesc"
                label="规则描述"
              >
                <el-input v-model="form.ruleDesc" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="startDate"
                label="开始日期"
              >
                <el-date-picker
                  v-model="form.startDate"
                  value-format="yyyy-MM-dd"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="endDate"
                label="结束时间"
              >
                <el-date-picker
                  v-model="form.endDate"
                  value-format="yyyy-MM-dd"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="businessBoxType"
                label="所属箱型业务"
              >
                <el-select
                  v-model="form.businessBoxType"
                  @change="selectBusinessBox"
                >
                  <el-option
                    v-for="item in businessBoxOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </el-form-item>
            </el-col>

            <el-col :span="6">
              <el-form-item
                prop="businessCodeType"
                label="所属条码业务"
              >
                <el-select v-model="form.businessCodeType">
                  <el-option
                    v-for="item in businessCodeOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </el-form-item>
            </el-col>

            <el-col :span="6">
              <el-form-item
                prop="categoryName"
                label="品类信息"
              >
                <quick-search
                  :disable="selectCategory"
                  :show-input="form.categoryName"
                  show-key="CATEGORY_NAME"
                  :scope-data="form"
                  name="scc_base_purchase_category"
                  @close-quicksearch="getCategoryByQuick"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
      <el-collapse v-model="activeLine">
        <el-collapse-item
          title="明细"
          name="1"
        >
          <el-container
            class="flex-container"
            style="height: 300px"
          >
            <el-main>
              <div style="padding: 12px 0">
                <el-button
                  class="detail-pbtn"
                  type="primary"
                  :disabled="!selectCategoryFlag"
                  @click="addLine"
                >
                  新增
                </el-button>
              </div>
              <base-table
                ref="table"
                :columns="columns"
                :data-source="dataSource"
                :initialize="false"
                row-key="ruleLineId"
                border
                @asyncGetRealDataSource="asyncGetRealDataSource"
              >
                <template #fieldEnum="{ scope }">
                  <el-select v-model="scope.row.fieldEnum">
                    <el-option
                      v-for="item in ruleOptionsForCategory"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </template>

                <template #createdBy="{ scope }">
                  <el-input
                    v-model="scope.row.createdBy"
                    disabled="true"
                  />
                </template>

                <template #creationDate="{ scope }">
                  <el-input
                    v-model="scope.row.creationDate"
                    disabled="true"
                  />
                </template>
              </base-table>
            </el-main>
          </el-container>
        </el-collapse-item>
      </el-collapse>
      <c-toolbar>
        <template #right>
          <el-button
            @click="cancelBill"
          >
            取消
          </el-button>
          <el-button
            type="primary"
            :disabled="readOnly"
            @click="save"
          >
            确认
          </el-button>
        </template>
      </c-toolbar>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import MainHeader from 'lib@/components/Table/MainHeader'
import CToolbar from 'lib@/components/c-toolbar'
import BaseTable from 'lib@/components/BaseTable/baseTable'
import MImport from 'lib@/components/import'
import QuickSearch from 'lib@/components/QuickSearch' // 快速查询组件
export default {
  name: 'BarcoderuleheadEdit',
  components: {
    MainHeader,
    CToolbar,
    BaseTable,
    MImport,
    QuickSearch
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      realDataSource: [],
      dataSource: [],
      activeLine: ['1'],
      columns: [
        {
          attrs: {
            prop: 'fieldEnum',
            label: '字段类型',
            formatter: value => {
              return this.ruleOptions.find(v => v.value === value).label || ''
            }
          },
          slot: 'fieldEnum'
        },
        {
          attrs: {
            prop: 'createdBy',
            label: '创建人'
          },
          slot: 'createdBy'
        },
        {
          attrs: {
            prop: 'creationDate',
            label: '创建时间'
          },
          slot: 'creationDate'
        },
        {
          attrs: {
            prop: 'operation',
            label: '操作',
            width: 150,
            fixed: 'right'
          },
          operations: [
            {
              event: 'deleteItem',
              name: this.$t('common.delete'),
              func: this.deleteItem
            }
          ]
        }
      ],
      form: {
        ruleName: null,
        ruleDesc: null,
        startDate: null,
        endDate: null,
        business: null,
        isConfigCategory: null,
        categoryId: null,
        categoryName: null,
        createdBy: null,
        creationDate: null,
        lastUpdatedBy: null,
        businessCodeType: null,
        businessBoxType: null
      },
      rules: {},
      readOnly: false,
      // 是否属性
      yesOrNoOptions: [
        { value: 'Y', label: '是' },
        { value: 'N', label: '否' }
      ],
      // 所属业务
      businessBoxOptions: [
        { value: 'INNER_BOX', label: '内箱' },
        { value: 'OUTER_BOX', label: '外箱' }
      ],
      // 所属业务
      businessCodeOptions: [
        { value: 'BAR_CODE', label: '条码' },
        { value: 'QR_CODE', label: '二维码' }
      ],
      // 当前品类的扩展字段
      ruleOptionsForCategory: [],
      // 内外箱都具备的基础字段
      ruleOptionsForDefault: [
        { label: '流水号', value: 'RULE_SERIAL_NUMBER' },
        { label: '物料编号', value: 'RULE_MATERIAL_NO' },
        { label: '供应商编码', value: 'RULE_VENDOR_CODE' }
      ],
      selectCategoryFlag: false,
      // 可选规则类型
      ruleOptions: [
        { label: '订单号', value: 'RULE_PO' },
        { label: '日期', value: 'RULE_DATE' },
        { label: '物料编号', value: 'RULE_MATERIAL_NO' },
        { label: '物料数量', value: 'RULE_MATERIAL_QUANTITY' },
        { label: '流水号', value: 'RULE_SERIAL_NUMBER' },
        { label: '供应商编码', value: 'RULE_VENDOR_CODE' },
        { label: '品名', value: 'RULE_PRODUCT_NAME' },
        { label: '数量', value: 'RULE_QUANTITY' },
        { label: '规格型号', value: 'RULE_SPECIFICATION_MODEL' },
        { label: '重量', value: 'RULE_WEIGHT' },
        { label: '单重', value: 'RULE_SINGLE_WEIGHT' },
        { label: '材料', value: 'RULE_MATERIAL_TYPE' },
        { label: '材质', value: 'RULE_MATERIAL_QUALITY' },
        { label: '质量状态', value: 'RULE_QUALITY_STATUS' },
        { label: '硬度', value: 'RULE_HARDNESS' },
        { label: '毛重', value: 'RULE_GROSS_WEIGHT' },
        { label: '电镀规格', value: 'RULE_PLATING_SPECIFICATION' },
        { label: '电镀厂商', value: 'RULE_PLATING_MANUFACTURER' },
        { label: '电镀规格', value: 'RULE_PLATING_DATE' },
        { label: '检验状态', value: 'RULE_INSPECTION_STATUS' },
        { label: '触点批次', value: 'RULE_CONTACT_BATCH' },
        { label: '标签种类', value: 'RULE_LABLE_TYPE' },
        { label: '镀种', value: 'RULE_PLATING_TYPE' },
        { label: '送货仓库', value: 'RULE_DELIVERY_WAREHOUSE' },
        { label: '后处理', value: 'RULE_AFTER_PROCESS' },
        { label: '流程号', value: 'RULE_FLOW_NUMBER' },
        { label: '头部直径', value: 'RULE_HEAD_DIAMETER' },
        { label: '头部厚度', value: 'RULE_HEAD_THICKNESS' },
        { label: '根部直径', value: 'RULE_ROOT_DIAMETER' },
        { label: '根部厚度', value: 'RULE_ROOT_THICKNESS' },
        { label: '丝材寿命报告编号', value: 'RULE_WIRE_LIFE_REPORT_NUMBER' },
        { label: '寿命状态', value: 'RULE_LIFE_STATE' },
        { label: '颜色', value: 'RULE_COLOR' },
        { label: '巡检', value: 'RULE_INSPECTION' },
        { label: '模号', value: 'RULE_MODEL_NUMBER' },
        { label: '机台号', value: 'RULE_MACHINE_NUMBER' },
        { label: '水口添加比例', value: 'RULE_NOZZLE_ADDITION_RATIO' },
        { label: 'UL档案', value: 'RULE_UL_FILE' },
        { label: '材料厂商', value: 'RULE_MATERIAL_MANUFACTURER' },
        { label: '净重', value: 'RULE_NET_WEIGHT' },
        { label: '每轴净重', value: 'RULE_NET_WEIGHT_PER_AXLE' },
        { label: '检验结果', value: 'RULE_TEST_RESULT' },
        { label: '镀层', value: 'RULE_COATING' },
        { label: '性能', value: 'RULE_PERFORMANCE' },
        { label: '检验日期', value: 'RULE_INSPECTION_DATE' },
        { label: 'ROHS', value: 'RULE_ROHS' },
        { label: '线径', value: 'RULE_WIRE_DIAMETER' },
        { label: '轴编号', value: 'RULE_AXIS_NUMBER' },
        { label: '标准', value: 'RULE_STANDARD' }
      ],
      // 选择品类
      selectCategory: true
    }
  },
  computed: {},
  watch: {},
  mounted () {
    const { flag, row, readOnly = false } = this.$attrs.params
    this.readOnly = readOnly
    if (flag === 'edit') {
      this.getDetail()
    }
  },
  methods: {
    /**
     * 获取品类信息
     */
    getCategoryByQuick (val, scope) {
      scope.categoryId = val ? val.categoryId : ''
      scope.categoryName = val ? val.categoryName : ''

      // 获取品类配置的字段
      this.$api.generate.boxtagconfig.getByCategoryId(val.categoryId).then(res => {
        // 回显字段
        this.ruleOptionsForCategory = []

        for (let field of res.data) {
          let option = {}
          option.label = field.fieldName
          option.value = field.fieldCode
          this.ruleOptionsForCategory.push(option)
        }
        this.ruleOptionsForCategory.push(...this.ruleOptionsForDefault)
        if (this.ruleOptionsForCategory.length > 0) {
          this.selectCategoryFlag = true
        } else {
          this.selectCategoryFlag = false
        }
        console.log(this.ruleOptionsForCategory)
      })
    },

    selectBusinessBox () {
      if (this.form.businessBoxType == 'OUTER_BOX') {
        let poLabel = {}
        poLabel.label = '订单号'
        poLabel.value = 'RULE_PO'
        this.ruleOptionsForDefault.push(poLabel)

        // 没有配置PO单号的时候再放入下拉框中
        let indexM = this.ruleOptionsForCategory.findIndex(item => item.value === 'RULE_PO')
        if (indexM == -1) {
          this.ruleOptionsForCategory.push(poLabel)
        }
      } else {
        // 选择内箱业务，移除订单号选项
        let indexN = this.ruleOptionsForDefault.findIndex(item => item.value === 'RULE_PO')
        if (indexN > -1) {
          this.ruleOptionsForDefault.splice(indexN, 1)
        }

        let indexM = this.ruleOptionsForCategory.findIndex(item => item.value === 'RULE_PO')
        if (indexM > -1) {
          this.ruleOptionsForCategory.splice(indexM, 1)
        }
      }

      console.log(this.ruleOptionsForDefault)
    },
    getDetail () {
      this.$api.generate.barcoderulehead.getById(this.$attrs.params.row.ruleHeadId).then(res => {
        const { barcodeRuleLineList, ...rest } = res.data
        this.form = rest
        this.dataSource = barcodeRuleLineList
      })
    },
    handleSuccess () {
      this.getDetail()
    },
    save () {
      this.$refs.form.validate(result => {
        this.$refs.table.validate(res => {
          if (result && res) {
            const { flag } = this.$attrs.params
            const data = {
              ...this.form,
              barcodeRuleLineList: this.realDataSource
            }
            this.$api.generate.barcoderulehead.addOrUpdate(data).then(res => {
              this.$message({
                type: 'success',
                message: res.message
              })
              this.cancelBill()
            })
          } else {
            this.__focus_error__()
          }
        })
      })
    },
    asyncGetRealDataSource (data) {
      this.realDataSource = data
    },
    addLine () {
      this.$refs.table.add({})
    },
    deleteItem (scope, data) {
      data.splice(scope.$index, 1)
    },
    cancelBill () {
      const { flag, row } = this.$attrs.params
      if (flag === 'add') {
        this.$emit('tab-remove', 'barcoderuleheadEdit')
      } else {
        this.$emit('tab-remove', 'barcoderuleheadEdit' + row.ruleHeadId)
      }
      this.__setTabTodo('barcoderuleheadList.getQuerydata')
    },
    // 上传附件成功
    handleUploadSuccess (file, row, key) {
      const { id, name } = file
      row[key] = id.toString()
    },
    // 删除文件
    handleAttachmentRemove (row, key) {
      row[key] = ''
    }
  }
}
</script>
<style scoped lang="scss">
.barcoderuleheadEdit {
  height: 100%;
  padding-bottom: 50px;
  ::v-deep .table-wrapper {
    padding-left: 0;
    padding-right: 0;
  }
  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }
  .el-table .el-date-editor {
    width: 135px;
  }
  .base-form {
    padding: 15px 30px 0;
  }
  .toRequired {
    color: #ff4949;
    padding-right: 2px;
  }
  .edit_cond {
    color: #23adf4;
    cursor: pointer;
  }
}
</style>
