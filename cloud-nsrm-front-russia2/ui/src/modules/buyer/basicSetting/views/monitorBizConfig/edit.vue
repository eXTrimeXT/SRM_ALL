<template>
  <el-container
    class="monitorbizconfigEdit"
    direction="vertical"
  >
    <el-main>
      <div class="form-container">
        <el-form
          ref="form"
          :model="form"
          :rules="rules"
        >
          <el-row :gutter="32">
            <el-col :span="6">
              <el-form-item
                prop="monitorBizName"
                :label="$t('monitorBizConfig.monitorBizName')"
              >
                <el-input v-model="form.monitorBizName" />
              </el-form-item>
            </el-col>

            <el-col :span="6">
              <el-form-item
                prop="monitorBizType"
                :label="$t('monitorBizConfig.monitorBizType')"
              >
                <DictSelect
                  v-model="form.monitorBizType"
                  code="MONITOR_BIZ_TYPE"
                />
              </el-form-item>
            </el-col>

            <el-col :span="6">
              <el-form-item
                prop="monitorMode"
                :label="$t('monitorBizConfig.monitorMode')"
              >
                <DictSelect
                  v-model="form.monitorMode"
                  code="MONITOR_MODE"
                />
              </el-form-item>
            </el-col>

            <el-col :span="6">
              <el-form-item
                prop="queryModule"
                :label="$t('bidMod.queryModule')"
              >
                <DictSelect
                  v-model="form.queryModule"
                  code="MODULE_DIVISION"
                />
              </el-form-item>
            </el-col>

            <el-col :span="24">
              <el-form-item
                prop="sqlExpression"
                :label="$t('monitorBizConfig.sqlExpression')"
              >
                <el-input
                  v-model="form.sqlExpression"
                  type="textarea"
                />
              </el-form-item>
            </el-col>

            <el-col :span="6">
              <el-form-item
                prop="columnName"
                :label="$t('monitorBizConfig.columnName')"
              >
                <el-input v-model="form.columnName" />
              </el-form-item>
            </el-col>

            <el-col :span="6">
              <el-form-item
                prop="operationSymbol"
                :label="$t('contractMod.operationType')"
              >
                <DictSelect
                  v-model="form.operationSymbol"
                  code="OPERATION_SYMBOL"
                />
              </el-form-item>
            </el-col>

            <el-col :span="6">
              <el-form-item
                prop="dataValue"
                :label="$t('monitorBizConfig.dataValue')"
              >
                <el-input v-model="form.dataValue" />
              </el-form-item>
            </el-col>

            <el-col :span="6">
              <el-form-item
                prop="cycleType"
                :label="$t('monitorBizConfig.cycleType')"
              >
                <DictSelect
                  v-model="form.cycleType"
                  code="CYCLE_TYPE"
                />
              </el-form-item>
            </el-col>

            <el-col :span="6">
              <el-form-item
                prop="resultDataType"
                :label="$t('monitorBizConfig.resultDataType')"
              >
                <DictSelect
                  v-model="form.resultDataType"
                  code="RESULT_DATA_TYPE"
                />
              </el-form-item>
            </el-col>

            <el-col :span="6">
              <el-form-item
                prop="cronExpression"
                :label="$t('monitorBizConfig.cronExpression')"
              >
                <el-input v-model="form.cronExpression" />
              </el-form-item>
            </el-col>

            <el-col :span="6">
              <el-form-item
                prop="receiveMails"
                :label="$t('monitorBizConfig.receiveMails')"
              >
                <el-input v-model="form.receiveMails" />
              </el-form-item>
            </el-col>

            <el-col :span="6">
              <el-form-item
                prop="validStatus"
                :label="$t('monitorBizConfig.validStatus')"
              >
                <DictSelect
                  v-model="form.validStatus"
                  code="VALID_STATUS"
                />
              </el-form-item>
            </el-col>

            <el-col :span="6">
              <el-form-item
                prop="latestBizStatus"
                :label="$t('monitorBizConfig.latestBizStatus')"
              >
                <DictSelect
                  v-model="form.latestBizStatus"
                  code="MONITOR_BIZ_STATUS"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>

      <CToolbar>
        <template #right>
          <el-button @click="cancelBill">
            {{ $t('bidMod.cancel') }}
          </el-button>
          <el-button
            type="primary"
            :disabled="readOnly"
            @click="save"
          >
            {{ $t('orderMod.buyerOrderSynergy.confirm') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>

<script>
import { tabTodoMixin } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import DictSelect from 'lib@/components/c-select/dict-select'
import { monitorBizConfig } from 'modb@/basicSetting/api/basicSetting'
export default {
  name: 'MonitorbizconfigEdit',
  components: {
    CToolbar,
    DictSelect
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      form: {
        monitorBizConfigId: null,
        monitorBizName: null,
        monitorBizType: null,
        monitorMode: null,
        queryModule: null,
        sqlExpression: null,
        columnName: null,
        operationSymbol: null,
        dataValue: null,
        minValue: null,
        maxValue: null,
        cycleType: null,
        resultDataType: null,
        triggerName: null,
        cronExpression: null,
        receiveMails: null,
        validStatus: null,
        latestBizStatus: null
      },
      rules: {},
      readOnly: false
    }
  },
  mounted () {
    const { flag, row, readOnly = false } = this.$attrs.params
    this.readOnly = readOnly
    if (flag === 'edit') {
      this.form = row
    }
  },
  methods: {
    save () {
      this.$refs.form.validate(result => {
        if (result) {
          const { flag } = this.$attrs.params
          // 新增时不用提交主键值
          const { monitorBizConfigId, ...rest } = this.form
          if (flag === 'add') {
            monitorBizConfig.add(rest).then(res => {
              this.$message({
                type: 'success',
                message: res.message
              })
              this.cancelBill()
            })
          } else if (flag === 'edit') {
            monitorBizConfig.update(this.form).then(res => {
              this.$message({
                type: 'success',
                message: res.message
              })
              this.cancelBill()
            })
          }
        } else {
          this.__focus_error__()
        }
      })
    },
    cancelBill () {
      const { flag, row } = this.$attrs.params
      if (flag === 'add') {
        this.$emit('tab-remove', 'monitorbizconfigEdit')
      } else {
        this.$emit('tab-remove', 'monitorbizconfigEdit' + row.monitorBizConfigId)
      }
      this.__setTabTodo('monitorbizconfigList.getQuerydata')
    }
  }
}
</script>

<style scoped lang="scss">
.monitorbizconfigEdit {
  height: 100%;
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
