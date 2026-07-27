<template>
  <el-container
    class="monitorbizlogEdit"
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
                prop="monitorBizLogId"
                :label="$t('monitorBizConfig.monitorBizLogId')"
              >
                <el-input v-model="form.monitorBizLogId" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="monitorBizConfigId"
                :label="$t('monitorBizConfig.monitorBizConfigId')"
              >
                <el-input v-model="form.monitorBizConfigId" />
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
                prop="executeStartTime"
                :label="$t('monitorBizConfig.executeStartTime')"
              >
                <el-input v-model="form.executeStartTime" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="executeEndTime"
                :label="$t('monitorBizConfig.executeEndTime')"
              >
                <el-input v-model="form.executeEndTime" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="bizStatus"
                :label="$t('monitorBizConfig.bizStatus')"
              >
                <el-input v-model="form.bizStatus" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="errorMsg"
                :label="$t('dataConfMod.errorInfo')"
              >
                <el-input v-model="form.errorMsg" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="dealStatus"
                :label="$t('perfMod.ProcessStatus')"
              >
                <el-input v-model="form.dealStatus" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="creationDate"
                :label="$t('qualitySynergy.creationDate')"
              >
                <el-input v-model="form.creationDate" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="createdId"
                :label="$t('monitorBizConfig.createdId')"
              >
                <el-input v-model="form.createdId" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="createdBy"
                :label="$t('dataConfMod.createdBy')"
              >
                <el-input v-model="form.createdBy" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="createdByIp"
                :label="$t('monitorBizConfig.createdByIp')"
              >
                <el-input v-model="form.createdByIp" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="lastUpdateDate"
                :label="$t('priceTemplate.lastUpdateDate')"
              >
                <el-input v-model="form.lastUpdateDate" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="lastUpdatedId"
                label="$t('monitorBizConfig.lastUpdatedId')"
              >
                <el-input v-model="form.lastUpdatedId" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="lastUpdatedBy"
                :label="$t('common.lastUpdatePeople')"
              >
                <el-input v-model="form.lastUpdatedBy" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="lastUpdatedByIp"
                :label="$t('monitorBizConfig.lastUpdatedByIp')"
              >
                <el-input v-model="form.lastUpdatedByIp" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="version"
                :label="$t('perfMod.version')"
              >
                <el-input v-model="form.version" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="tenantId"
                :label="$t('monitorBizConfig.tenantId')"
              >
                <el-input v-model="form.tenantId" />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
      <c-toolbar>
        <template #right>
          <el-button

            @click="cancelBill"
          >
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
      </c-toolbar>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import { monitorBizLog } from 'modb@/basicSetting/api/basicSetting'

export default {
  name: 'MonitorbizlogEdit',
  components: { CToolbar },
  mixins: [tabTodoMixin],
  data () {
    return {
      form: {
        monitorBizLogId: null,
        monitorBizConfigId: null,
        receiveMails: null,
        executeStartTime: null,
        executeEndTime: null,
        bizStatus: null,
        errorMsg: null,
        dealStatus: null,
        creationDate: null,
        createdId: null,
        createdBy: null,
        createdByIp: null,
        lastUpdateDate: null,
        lastUpdatedId: null,
        lastUpdatedBy: null,
        lastUpdatedByIp: null,
        version: null,
        tenantId: null
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
            monitorBizLog.add(rest).then(res => {
              this.$message({
                type: 'success',
                message: res.message
              })
              this.cancelBill()
            })
          } else if (flag === 'edit') {
            monitorBizLog.update(this.form).then(res => {
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
        this.$emit('tab-remove', 'monitorbizlogEdit')
      } else {
        this.$emit('tab-remove', 'monitorbizlogEdit' + row.monitorBizConfigId)
      }
      this.__setTabTodo('monitorbizlogList.getQuerydata')
    }
  }
}
</script>
<style scoped lang="scss">
.monitorbizlogEdit {
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
