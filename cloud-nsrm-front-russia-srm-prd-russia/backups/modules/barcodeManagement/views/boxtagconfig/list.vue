<template>
  <el-container
    class="flex-container boxtagconfig_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <form-wrapper
        :form-array="filterConfig"
        @getFormData="getQuerydata"
      />
      <main-header
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <AuthorityButton
            type="primary"
            @click="addHandle"
          >
            {{ $t('common.add') }}
          </AuthorityButton>
        </template>
      </main-header>
      <table-view
        :ref="gridId"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        :source="$api.generate.boxtagconfig.list"
      />
    </el-main>
    <!-- 弹窗 -->
    <srm-dialog
      :title="dialogTitle"
      size="large"
      :visible.sync="visible"
    >
      <div class="boxtagconfigEdit">
        <el-form
          ref="form"
          :model="form"
          :rules="rules"
        >
          <el-row :gutter="27">
            <el-col :span="8">
              <el-form-item
                prop="categoryName"
                label="品类"
              >
                <quick-search
                  :disable="isReadOnly"
                  :show-input="form.categoryName"
                  show-key="CATEGORY_NAME"
                  :scope-data="form"
                  name="scc_base_purchase_category"
                  @close-quicksearch="getCategoryByQuick"
                />
              </el-form-item>
            </el-col>

            <el-col :span="8">
              <el-form-item
                prop="fieldTypeCode"
                label="字段类型"
              >
                <el-select v-model="form.fieldTypeCode">
                  <el-option
                    v-for="item in fieldTypeOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </el-form-item>
            </el-col>

            <el-col :span="8">
              <el-form-item
                prop="fieldCode"
                label="拓展字段"
              >
                <el-select
                  v-model="form.fieldCode"
                  @change="selectField"
                >
                  <el-option
                    v-for="item in ruleOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </el-form-item>
            </el-col>

            <el-col :span="8">
              <el-form-item
                prop="isMust"
                label="是否必须"
              >
                <DictSelect
                  v-model="form.isMust"
                  code="YES_OR_NO"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
      <template
        #footer
        class="dialog-footer"
      >
        <el-button
          type="primary"
          @click="confirm"
        >
          {{ $t('common.confirm') }}
        </el-button>
        <el-button @click="cancel">
          {{ $t('common.cancel') }}
        </el-button>
      </template>
    </srm-dialog>
  </el-container>
</template>
<script>
// 引入组件
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import QuickSearch from 'lib@/components/QuickSearch' // 快速查询组件
export default {
  name: 'BoxtagconfigList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    QuickSearch
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      name: 'boxtagconfigList',
      tableName: 'boxtagconfigTable',
      pageSize: 15,
      gridId: 'list',
      currentRows: [],
      visible: false,
      mode: 'add',
      dialogTitle: '详情',
      form: {
          id: 'ID',
          categoryId: '品类ID',
          categoryName: '品类名称',
          categoryCode: '品类编码',
          fieldTypeCode: '字段类型编码',
          fieldTypeName: '字段类型名称（文本、数字）',
          fieldName: '字段名字',
          fieldCode: '字段编码',
          isMust: '是否必须',
          fieldLength: '字段长度',
          createdId: '创建人id',
          createdBy: '创建人名称',
          creationDate: '创建日期',
          createdByIp: '创建ip',
          lastUpdatedId: '更新人id',
          lastUpdatedBy: '更新人',
          lastUpdateDate: '更新时间',
          lastUpdatedByIp: '更新ip',
          version: '版本号',
          tenantId: 'TENANT_ID'
      },
      rules: {
      },
      tableHeader: [
        {
          prop: 'categoryName',
          label: '品类名称',
          width: 100
        },
        {
          prop: 'categoryCode',
          label: '品类编码',
          width: 100
        },
        {
          prop: 'fieldTypeCode',
          label: '字段类型',
          width: 100,
          formattor: val => {
            const dict = this.fieldTypeOptions.find(i => i.value === val)
            return dict ? dict.label : val
          }
        },
        {
          prop: 'fieldName',
          label: '字段名字',
          width: 100
        },
        {
          prop: 'fieldCode',
          label: '字段编码',
          width: 100
        },
        {
          prop: 'isMust',
          label: '是否必须',
          width: 100,
          dataType: 'dict',
          code: 'YES_OR_NO'

        },
        {
          prop: 'fieldLength',
          label: '字段长度',
          width: 100
        },
        {
          prop: 'createdBy',
          label: '创建人名称',
          width: 100
        },
        {
          prop: 'creationDate',
          label: '创建日期',
          width: 100
        },
        {
          prop: 'lastUpdatedBy',
          label: '更新人',
          width: 100
        },
        {
          prop: 'operation',
          label: '操作',
          showType: 'buttons',
          btnStyle: 'text',
          fixed: 'right',
          width: 130,
          buttons: [
            {
              callback: row => this.editHandle(row),
              // code: "pr:requirementApply:edit",
              // show: row => row.status === "DRAFT",
              formattor: () => {
                return this.$t('common.edit')
              }
            },
            {
              callback: row => this.deleteHandle(row),
              // code: "pr:requirementApply:edit",
              // show: row => row.status === "DRAFT",
              formattor: () => {
                return this.$t('common.delete')
              }
            }
          ]
        }
      ],

      filterConfig: [
              { prop: 'categoryId', label: '品类ID' },
              { prop: 'fieldTypeCode', label: '字段类型编码' },
              { prop: 'fieldCode', label: '字段编码' }
      ],
      queryParam: {},

      // 字段类型下拉属性
      fieldTypeOptions: [
        { value: 'text', label: '文本' },
        { value: 'number', label: '数字' },
        { value: 'dateTime', label: '时间' },
        { value: 'date', label: '日期' }
      ],
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
      ]

    }
  },
  created () {
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
    for (var it in this.ruleOptions) {
      // console.log(this.ruleOptions[it].value);
      console.log(this.ruleOptions[it].label)
    }
  },

  methods: {
    /**
     * 選擇字段
     */
    selectField (val, scope) {
      const fieldVal = this.ruleOptions.find(i => i.value === val)
      this.form.fieldName = fieldVal ? fieldVal.label : ''
    },
    /**
     * 获取品类信息
     */
    getCategoryByQuick (val, scope) {
      scope.categoryId = val ? val.categoryId : ''
      scope.categoryCode = val ? val.categoryCode : ''
      scope.categoryName = val ? val.categoryName : ''
    },
    cancel () {
      this.visible = false
    },
    confirm () {
      this.$refs.form.validate(result => {
        if (result) {
          const flag = this.mode
          // 新增时不用提交主键值
          const { id, ...rest } = this.form
          if (flag === 'add') {
            this.$api.generate.boxtagconfig.add(rest).then(res => {
              this.$message({
                type: 'success',
                message: res.message
              })
              this.visible = false
              this.getQuerydata()
            })
          } else if (flag === 'edit') {
            this.$api.generate.boxtagconfig.update(this.form).then(res => {
              this.$message({
                type: 'success',
                message: res.message
              })
              this.visible = false
              this.getQuerydata()
            })
          }
        }
      })
    },

    getQuerydata (params) {
      this.queryParam = params
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    deleteHandle (row) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.$api.generate.boxtagconfig.delete(row.id).then(res => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
        })
        .catch(() => {})
    },
      addHandle (row) {
      for (let i in this.form) {
          this.form[i] = ''
      }
      this.dialogTitle = '装箱条码标签配置新增'
      this.visible = true
      this.mode = 'add'
      },
      editHandle (row) {
          this.form = row
          this.dialogTitle = '装箱条码标签配置编辑'
          this.visible = true
          this.mode = 'edit'
      },
    handleCurrentChange (val) {
      this.currentRows = val
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
