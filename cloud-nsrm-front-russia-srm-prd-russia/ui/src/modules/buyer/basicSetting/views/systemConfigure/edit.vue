<template>
  <el-container class="systemconfigureEdit" direction="vertical">
    <el-main>
      <div class="form-container">
        <el-form ref="form" :model="form" :rules="rules">
          <srm-row>
            <srm-col :span="6">
              <el-form-item prop="paramKey" :label="$t('dataConfMod.paramKey')">
                <el-input v-model="form.paramKey" />
              </el-form-item>
            </srm-col>
            <srm-col>
              <el-form-item prop="paramValue" :label="$t('dataConfMod.paramValue')">
                <el-input v-model="form.paramValue" />
              </el-form-item>
            </srm-col>
            <srm-col>
              <el-form-item prop="paramDesc" :label="$t('dataConfMod.paramDesc')">
                <el-input v-model="form.paramDesc" />
              </el-form-item>
            </srm-col>
            <srm-col>
              <el-form-item prop="paramStatus" :label="$t('dataConfMod.paramStatus')">
                <DictSelect v-model="form.paramStatus" code="YES_OR_NO" />
              </el-form-item>
            </srm-col>
          </srm-row>
        </el-form>
      </div>
      <c-toolbar>
        <template #right>
          <el-button @click="cancelBill">
            {{ $t('common.cancel') }}
          </el-button>
          <el-button
            type="primary"
            :disabled="readOnly"
            @click="save"
          >
            {{ $t('common.affirm') }}
          </el-button>
        </template>
      </c-toolbar>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import { systemConfigure } from 'modb@/basicSetting/api/basicSetting'

export default {
  name: 'SystemconfigureEdit',
  components: {
    CToolbar
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      form: {
        paramKey: null,
        paramValue: null,
        paramDesc: null,
        paramStatus: null
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
          if (flag === 'add') {
            this.form.systemConfigureId = null
          }

          systemConfigure.saveOrUpdate(this.form).then(res => {
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
    },
    cancelBill () {
      this.$emit('tab-remove', this.$attrs.tabName)
      this.__setTabTodo('systemconfigureList.getQueryData')
    }
  }
}
</script>
<style scoped lang="scss">
.systemconfigureEdit {
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
