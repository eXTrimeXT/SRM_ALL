<template>
  <el-container class="flex-container" direction="vertival">
    <el-main>
      <el-collapse v-model="activeNames" class="tab-form-style">
        <el-collapse-item name="1" :title="$t('cusEntry.sealManagement.baseInfo')">
          <el-form
            ref="form"
            :model="baseInfo"
            :rules="rules"
            :disabled="readonly"
          >
            <srm-row :gutter="50">
              <srm-col :init-col="3">
                <el-form-item prop="signCompanyName" :label="$t('cusEntry.sealManagement.signCompanyName')">
                  <el-input v-model="baseInfo.signCompanyName" />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="3">
                <el-form-item prop="sealName" :label="$t('cusEntry.sealManagement.sealName')">
                  <el-input v-model="baseInfo.sealName" />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="3">
                <el-form-item prop="sealId" :label="$t('cusEntry.sealManagement.sealId')">
                  <el-input v-model="baseInfo.sealId" />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="3">
                <el-form-item prop="creationDate" :label="$t('cusEntry.sealManagement.creationDate')">
                  <el-date-picker
                    v-model="baseInfo.creationDate"
                    type="date"
                    style="width: 100%"
                    :format="$formatDatePicker"
                    value-format="yyyy-MM-dd"
                    :disabled="true"
                  />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="3">
                <el-form-item prop="createdFullName" :label="$t('cusEntry.sealManagement.createdFullName')">
                  <el-input v-model="baseInfo.createdFullName" disabled />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-form>
        </el-collapse-item>
      </el-collapse>
    </el-main>
    <CToolbar>
      <template slot="right">
        <el-button
          @click="back"
        >
          {{ $t('common.backTo') }}
        </el-button>
        <el-button
          v-if="!readonly"
          type="primary"
          @click="save"
        >
          {{ $t('common.save') }}
        </el-button>
      </template>
    </CToolbar>
  </el-container>
</template>

<script>
import CToolbar from 'lib@/components/c-toolbar'
import { contractManagement } from 'modcb@/contractManagement/api'
import { tabTodoMixin } from '@/utils/mixins'
export default {
  name: 'SealManagementDetail',
  components: {
    CToolbar
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      activeNames: ['1'],
      baseInfo: {
        signCompanyName: '',
        sealName: '',
        sealId: null
      },
      rules: {
        signCompanyName: [{ required: true, message: this.$t('cusEntry.tipMessage.signCompanyNameMsg') }],
        sealId: [{ required: true, message: this.$t('cusEntry.tipMessage.sealIdMsg') }],
        sealName: [{ required: true, message: this.$t('cusEntry.tipMessage.sealNameMsg') }]
      }
    }
  },
  computed: {
    readonly () {
      return this.$attrs.params.type === 'view'
    }
  },
  created () {
    const type = this.$attrs.params.type
    if (type !== 'add') {
      const id = this.$attrs.params.row.contractSealId
      contractManagement.seal.get({ id }).then(res => {
        this.baseInfo = res.data
      })
    }
  },
  methods: {
    // 返回
    back () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      this.__setTabTodo('SealManagementList.getQueryData')
    },
    // 保存
    save () {
      this.$refs.form.validate(valid => {
        if (valid) {
          const type = this.$attrs.params.type === 'add' ? 'add' : 'update'
          contractManagement.seal[type](this.baseInfo).then(res => {
            this.$message.success(this.$t('common.successSave'))
            this.back()
          })
        }
      })
    }
  }
}
</script>
