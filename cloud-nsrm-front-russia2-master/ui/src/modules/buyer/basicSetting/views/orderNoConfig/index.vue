<template>
  <el-container
    class="flex-container-notab the_currency_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="queryForm"
        @getFormData="getQuerydata"
      />
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <el-button
            type="primary"
            @click="addNew"
          >
            {{ $t("common.add") }}
          </el-button>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableList"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :source="orderNoConfig.seqPageList"
      />
    </el-main>
    <!-- 新增 编辑弹框区域-->
    <srm-dialog
      :title="dialogTitle"
      :visible.sync="dialogFormVisible"
      :close-on-click-modal="false"
      size="middle"
    >
      <el-form
        ref="orderNoConfigForm"
        :model="submitModel.submitform"
        :rules="submitModel.rules"
        class="orderNoConfig"
      >
        <el-row :gutter="32">
          <el-col :span="12">
            <!-- 模板格式 -->
            <el-form-item
              :label="$t('dataConfMod.scopeDefinition')"
              prop="scopeDefinition"
            >
              <el-select v-model="submitModel.submitform.scopeDefinition">
                <el-option
                  v-for="item in scopeDefinitionList"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <!-- 预览效果： -->
            <div class="previewDiv">
              {{ $t("dataConfMod.previewEffect") + preview }}
            </div>
          </el-col>
        </el-row>
        <el-row :gutter="32">
          <el-col :span="12">
            <!-- 单据名称 -->
            <el-form-item
              :label="$t('dataConfMod.sequenceName')"
              prop="sequenceName"
            >
              <el-input
                v-model="submitModel.submitform.sequenceName"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <!-- 单据编码 -->
            <el-form-item
              :label="$t('dataConfMod.sequenceCode')"
              prop="sequenceCode"
            >
              <el-input
                v-model="submitModel.submitform.sequenceCode"
                :disabled="curOpt === 'edit'"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <!-- 前缀 -->
            <el-form-item
              :label="$t('dataConfMod.prefix')"
              prop="prefix"
            >
              <el-input v-model="submitModel.submitform.prefix" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <!-- 流水号位数 -->
            <el-form-item
              :label="$t('dataConfMod.serialNumDigits')"
              prop="length"
            >
              <div style="padding-top: 28px;">
                <el-input-number
                  v-model="submitModel.submitform.length"
                  style="display:block;width:100%"
                  :min="1"
                  :max="100"
                />
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <!-- 生效日期 -->
            <el-form-item
              :label="$t('dataConfMod.startDate')"
              prop="endDate"
            >
              <el-date-picker
                v-model="submitModel.submitform.startDate"
                type="date"
                :placeholder="$t('common.pleaseSelectDate')"
                :format="$formatDatePicker"
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
                v-model="submitModel.submitform.endDate"
                type="date"
                :placeholder="$t('common.pleaseSelectDate')"
                :format="$formatDatePicker"
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
          {{ $t("common.cancel") }}
        </el-button>
        <el-button
          type="primary"
          @click="comfirmSave"
        >
          {{ $t("common.confirm") }}
        </el-button>
      </div>
    </srm-dialog>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { getDictItem } from '@/api/common'
import { adaptDictData } from '@/utils'
import _omit from 'lodash/omit'
import { orderNoConfig } from 'modb@/basicSetting/api/basicSetting'

export default {
  name: 'OrderNoConfig',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  data () {
    return {
      orderNoConfig: orderNoConfig,
      pageSize: 15,
      gridId: 'orderNoConfig',
      currentRow: null,
      showFilterBar: 1,
      queryParam: {},
      dialogFormVisible: false,
      queryForm: [], // 查询条件
      tableHeader: [], // 表格列数据
      tableList: [],
      tableTotal: 0, // 分页数据
      curOpt: 'add',
      dialogTitle: this.$t('common.add'), // 新增
      tableSelection: [],
      langList: [],
      busTypeList: [],
      orgStatusList: [],
      catStatusList: [],
      scopeDefinitionList: [
        {
          value: '[@df_prefix][@df_seq]',
          label: this.$t('dataConfMod.scopeDefinitionList')[0] // "[@df_prefix][@df_seq]--前缀+流水号"
        },
        {
          value: '[@df_prefix][@df_date_yyMMdd][@df_seq]',
          label: this.$t('dataConfMod.scopeDefinitionList')[1] // "[@df_prefix][@df_date_yyMMdd][@df_seq]--前缀+6位日期+流水号"
        },
        {
          value: '[@df_prefix][@df_date_yyyyMMdd][@df_seq]',
          label: this.$t('dataConfMod.scopeDefinitionList')[2] // "[@df_prefix][@df_date_yyyyMMdd][@df_seq]--前缀+8位日期+流水号"
        }
      ],
      submitModel: {
        submitform: {
          scopeDefinition: '', // 魔板类型
          sequenceName: '', // 名称
          sequenceCode: '', // 编码
          prefix: '', // 前缀
          length: '', // 长度
          startDate: '',
          endDate: ''
        },
        rules: {
          sequenceName: [
            { required: true, message: this.$t('dataConfMod.msgSeqName') }
          ], // 请输入单据名称
          sequenceCode: [
            { required: true, message: this.$t('dataConfMod.msgSeqCode') }
          ], // 请输入单据编码
          prefix: [
            { required: true, message: this.$t('dataConfMod.msgPrefix') }
          ], // 请输入前缀
          length: [
            { required: true, message: this.$t('dataConfMod.msgDigits') }
          ] // 请输入流水长度
        }
      }
    }
  },
  computed: {
    preview () {
      let randomWord = function (randomFlag, min, max) {
        let str = ''
        let range = min
        const arr = ['0']
        // 随机产生
        if (randomFlag) {
          range = Math.round(Math.random() * (max - min)) + min
        }
        for (let i = 0; i < range; i++) {
          let pos = Math.round(Math.random() * (arr.length - 1))
          str += arr[pos]
        }
        return str
      }
      let scopeDefinition = this.submitModel.submitform.scopeDefinition
      let length = this.submitModel.submitform.length
      let prefix = this.submitModel.submitform.prefix
      let liusui = length ? randomWord(false, length - 1) + '1' : ''
      let str = ''
      let m = (new Date().getMonth() + 1).toString()
      let d = new Date().getDate().toString()
      let mm = m[1] ? m : '0' + m
      let dd = d[1] ? d : '0' + d
      if (scopeDefinition === '[@df_prefix][@df_seq]') {
        str = prefix + liusui
      } else if (scopeDefinition === '[@df_prefix][@df_date_yyMMdd][@df_seq]') {
        let year = new Date().getFullYear().toString()
        let yy = year.substring(year.length - 2)
        let date = yy + mm + dd
        str = prefix + date + liusui
      } else if (
        scopeDefinition === '[@df_prefix][@df_date_yyyyMMdd][@df_seq]'
      ) {
        let date = new Date().getFullYear().toString() + mm + dd
        str = prefix + date + liusui
      } else {
        str = ''
      }
      return str
    }
  },
  created () {
    let _this = this
    this.queryForm = [
      {
        prop: 'sequenceName',
        label: () => this.$t('dataConfMod.sequenceName')
      }, // 单据名称
      {
        prop: 'sequenceCode',
        label: () => this.$t('dataConfMod.sequenceCode')
      }, // 单据编号
      { prop: 'prefix', label: () => this.$t('dataConfMod.prefix') } // 前缀
    ]
    this.tableHeader = [
      {
        prop: 'sequenceName',
        label: () => this.$t('dataConfMod.sequenceName'), // 单据名称
        width: '150'
      },
      {
        prop: 'sequenceCode',
        label: () => this.$t('dataConfMod.sequenceCode') // 单据编号
      },
      {
        prop: 'prefix',
        label: () => this.$t('dataConfMod.prefix'), // 前缀
        width: '95',
        align: 'center'
      },
      {
        prop: 'length',
        label: () => this.$t('dataConfMod.serialNumDigits'), // 流水号位数
        width: '110',
        align: 'center'
      },
      {
        prop: 'scopeDefinition',
        label: () => this.$t('dataConfMod.scopeDefinition') // 模板格式
      },
      {
        prop: 'currentValue',
        label: () => this.$t('dataConfMod.usageCount'), // 使用次数
        width: '95',
        align: 'center'
      },
      {
        prop: 'startDate',
        label: () => this.$t('dataConfMod.startDate'), // 生效日期
        width: '100',
        formattor (val) {
          return val ? this.$parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'endDate',
        label: () => this.$t('dataConfMod.endDate'), // 失效日期
        width: '100',
        formattor (val) {
          return val ? this.$parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        label: () => this.$t('common.operation'), // 操作
        width: '160',
        fixed: 'right',
        editType: 'none',
        btnStyle: 'text',
        showType: 'buttons',
        buttons: [
          {
            callback: function (row) {
              this.editDetail(row)
            }.bind(this),
            formattor () {
              return _this.$t('common.edit') // 编辑
            }
          },
          {
            callback: function (row) {
              this.delRowData(row)
            }.bind(this),
            formattor () {
              return _this.$t('common.delete') // 删除
            }
          },
          {
            callback: function (row) {
              this.copyRowData(row)
            }.bind(this),
            formattor () {
              return _this.$t('common.copy') // 复制
            }
          }
        ]
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
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    // 获取数据字典
    fatchDictData () {
      // 组织状态
      getDictItem('ORG_STATUS').then(res => {
        this.orgStatusList = adaptDictData(res.data, 'dict')
      })
      // 品类状态
      getDictItem('CATEGORY_STATUS').then(res => {
        this.catStatusList = adaptDictData(res.data, 'dict')
      })
      // 是否
      getDictItem('GRAFE_STATUS').then(res => {
        this.yesOrNoList = adaptDictData(res.data, 'dict')
        this.queryForm[2].options = this.yesOrNoList
      })
    },
    // 选择组织
    addOrgHandle (e, id, scope) {
      scope.organizationId = e ? e.organizationId : null
      scope.organizationCode = e ? e.organizationCode : ''
      scope.organizationName = e ? e.organizationName : null
    },
    addNew () {
      this.curOpt = 'add'
      this.controlHandle(this.curOpt)
    },
    // 新增、编辑
    controlHandle (type) {
      if (type === 'add') {
        // 新增
        this.dialogTitle = this.$t('common.add') // 新增
        let formObj = this.submitModel.submitform
        Object.keys(formObj).forEach(key => (formObj[key] = ''))
        this.submitModel.submitform.startDate = new Date()
      } else {
        // 修改
        this.dialogTitle = this.$t('common.edit') // 编辑
      }
      this.dialogFormVisible = true
    },
    // 选中
    handleSelectionChange (value) {
      this.tableSelection = value
    },
    // 确认保存
    comfirmSave () {
      // 校验失效日期需要大于生效日期
      if (this.submitModel.submitform.startDate && this.submitModel.submitform.endDate) {
        if (this.submitModel.submitform.startDate >= this.submitModel.submitform.endDate) {
          this.$message.warning(this.$t('dataConfMod.expirationMustGreater'))
          return
        }
      }

      this.$refs.orderNoConfigForm.validate(valid => {
        if (!valid) {
          this.$message({
            message: this.$t('common.pleasefinishRequired'), // 请输入必填项
            type: 'success'
          })
          return false
        } else {
          this.saveOrUpdateHandle(this.curOpt)
        }
      })
    },
    // 新增编辑组织数据
    saveOrUpdateHandle (opt) {
      let submitData = this.submitModel.submitform
      if (opt === 'add') {
        // 新增
        orderNoConfig.seqAdd(submitData).then(res => {
          if (res) {
            this.$message({
              message: res.message,
              type: 'success'
            })
            this.dialogFormVisible = false
            this.getQuerydata()
          }
        })
      } else {
        orderNoConfig.seqUpdate(submitData).then(res => {
          if (res) {
            this.$message({
              message: res.message,
              type: 'success'
            })
            this.dialogFormVisible = false
            this.getQuerydata()
          }
        })
      }
    },
    editDetail (row) {
      this.curOpt = 'edit'
      this.controlHandle(this.curOpt)
      let sequenceId = row.sequenceId
      orderNoConfig.seqQueryById({ sequenceId }).then(res => {
        let formInfo = res.data
        this.submitModel.submitform = _omit(formInfo, [
          'creationDate',
          'lastUpdateDate',
          'lastUpdatedBy',
          'createdBy'
        ])
      })
      this.dialogFormVisible = true
    },
    delRowData (row) {
      let sequenceId = row.sequenceId
      // 当前操作将删除数据，确认是否删除数据？
      this.$confirm(this.$t('common.ifDeleteData'), {
        confirmButtonText: this.$t('common.affirm'), // 确认
        cancelButtonText: this.$t('common.cancel'), // 取消
        type: 'warning'
      })
        .then(() => {
          orderNoConfig.seqDelete({ sequenceId }).then(res => {
            this.$message({
              message: res.message,
              type: 'success'
            })
            this.getQuerydata()
          })
        })
    },
    copyRowData (row) {
      this.curOpt = 'add'
      this.controlHandle(this.curOpt)
      this.submitModel.submitform.scopeDefinition = row.scopeDefinition
      this.submitModel.submitform.prefix = row.prefix
      this.submitModel.submitform.length = row.length
      this.submitModel.submitform.sequenceName = ''
      this.submitModel.submitform.sequenceCode = ''
    }
  }
}
</script>

<style scoped lang="scss">
.previewDiv {
  font-size: 16px;
  color: #333;
  padding-top: 28px;
  font-weight: bold;
  line-height: 30px;
}
</style>
<style>
.orderNoConfig .el-input-number--mini .el-input-number__increase,
.orderNoConfig .el-input-number--mini .el-input-number__decrease {
  width: 28px;
  font-size: 12px;
  height: 30px !important;
  line-height: 30px !important;
}
</style>
