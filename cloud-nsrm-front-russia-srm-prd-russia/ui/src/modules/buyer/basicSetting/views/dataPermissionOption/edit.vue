<template>
  <el-container class="datapermissionvaroptionsEdit" direction="vertical">
    <el-main>
      <div class="form-container">
        <el-form ref="form" :model="form" :rules="rules">
          <srm-row>
            <srm-col>
              <el-form-item prop="varName" :label="$t('dataConfMod.varName')">
                <el-input v-model="form.varName" />
              </el-form-item>
            </srm-col>
            <srm-col>
              <el-form-item prop="varDesc" :label="$t('dataConfMod.varDesc')">
                <el-input v-model="form.varDesc" />
              </el-form-item>
            </srm-col>
            <srm-col>
              <el-form-item prop="varStatus" :label="$t('dataConfMod.permissionVariable')">
                <dict-select v-model="form.varStatus" code="PERMISSION_STATUS" />
              </el-form-item>
            </srm-col>
            <srm-col>
              <el-form-item prop="varType" :label="$t('dataConfMod.varType')">
                <dict-select v-model="form.varType" code="VAR_TYPE" />
              </el-form-item>
            </srm-col>
            <srm-col>
              <el-form-item prop="varValue" :label="$t('dataConfMod.varValue')">
                <el-input v-model="form.varValue" :disabled="form.varType !== 'CONSTANT' && form.varType !== 'SQL'" />
              </el-form-item>
            </srm-col>
            <srm-col>
              <el-form-item prop="permissionClassName" :label="$t('dataConfMod.permissionClassName')">
                <el-input v-model="form.permissionClassName" :disabled="form.varType !== 'REFLECT'" />
              </el-form-item>
            </srm-col>
            <srm-col>
              <el-form-item prop="permissionMethodName" :label="$t('dataConfMod.permissionMethodName')">
                <el-input v-model="form.permissionMethodName" :disabled="form.varType !== 'REFLECT'" />
              </el-form-item>
            </srm-col>
            <srm-col :init-col="1">
              <el-form-item prop="remark" :label="$t('common.remark')">
                <el-input v-model="form.remark" />
              </el-form-item>
            </srm-col>
          </srm-row>
        </el-form>
      </div>
      <CToolbar>
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
      </CToolbar>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import { dataPermissionOption } from 'modb@/basicSetting/api/basicSetting'

export default {
  name: 'DatapermissionvaroptionsEdit',
  components: { CToolbar },
  mixins: [tabTodoMixin],
  data () {
    return {
      form: {
        varName: null,
        varDesc: null,
        varType: null,
        varValue: null,
        permissionClassName: null,
        permissionMethodName: null,
        remark: null,
        varStatus: null
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
          const { dataPermissionVarOptionId, ...rest } = this.form
          if (flag === 'add') {
            dataPermissionOption.saveOrUpdate(rest).then(res => {
              this.$message({
                type: 'success',
                message: res.message
              })
              this.cancelBill()
            })
          } else if (flag === 'edit') {
            dataPermissionOption.saveOrUpdate(this.form).then(res => {
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
      this.$emit('tab-remove', this.$attrs.tabName)
      this.__setTabTodo('datapermissionvaroptionsList.getQueryData')
    }
  }
}
</script>
<style scoped lang="scss">
.datapermissionvaroptionsEdit {
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
