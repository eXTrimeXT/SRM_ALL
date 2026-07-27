<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <el-form ref="form" :model="form" :rules="formRules">
        <el-collapse v-model="colValue" class="tab-form-style">
          <el-collapse-item title="基础信息" name="1">
            <BaseInfo
              ref="baseInfo"
              :form.sync="form"
              :readonly="disabledFlag"
            />
          </el-collapse-item>
          <el-collapse-item title="新增集采物资" name="2">
            <JcInfo
              ref="jcInfo"
              :form.sync="form"
              :readonly="disabledFlag"
              @moneyChages="moneyChages"
            />
          </el-collapse-item>
          <el-collapse-item title="原集采物资" name="3">
            <OldJcInfo
              ref="oldJcInfo"
              :form.sync="form"
              :readonly="disabledFlag"
              @moneyChages="moneyChages"
            />
          </el-collapse-item>
        </el-collapse>
      </el-form>
    </el-main>
    <CToolbar>
      <template slot="right">
        <el-button type="ghost" @click="back">
          {{ $t('bidMod.cancel') }}
        </el-button>
        <template v-if="!disabledFlag">
          <el-button type="primary" @click="saveBill('SAVE')">
            {{ $t('bidMod.temporaryStorage') }}
          </el-button>
          <el-button type="primary" @click="saveBill('SUBMIT')">
            {{ $t('problemManagement.submit') }}
          </el-button>
        </template>
      </template>
    </CToolbar>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import BaseInfo from './components/baseInfo'
import JcInfo from './components/jcInfo'
import OldJcInfo from './components/oldJcInfo'
import CToolbar from 'lib@/components/c-toolbar'
import { jcAccount } from 'modcb@/jcAgreement/api'

export default {
  name: 'JcAccountDetail',
  components: {
    BaseInfo,
    JcInfo,
    OldJcInfo,
    CToolbar
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      ledgerId: null,
      colValue: ['1', '2', '3'],
      form: {
        ledgerId: null,
        contractEndDate: null,
        contractStartDate: null,
        headPerson: null,
        projectTotalMoney: null,
        delayReason: null,
        nextSuggest: null,
        addNum: null,
        addBeforeMoney: null,
        addAfterMoney: null,
        addDecrementMoney: null,
        addDecrementRatio: null,
        aboBeforeMoney: null,
        aboAfterMoney: null,
        aboDecrementMoney: null,
        aboDecrementRatio: null
      },
      formRules: {
        projectName: [{ required: true, message: this.$t('common.requiredField') }],
        contractStartDate: [{ required: true, message: this.$t('common.requiredField') }],
        effectiveEndDate: [{ required: true, message: this.$t('common.requiredField') }],
        headPerson: [{ required: true, message: this.$t('common.requiredField') }],
        projectTotalMoney: [{ required: true, message: this.$t('common.requiredField') }]
      }
    }
  },
  computed: {
    urlParams () {
      return this.$attrs.params || {}
    },
    disabledFlag () {
      return !!['view', 'approval', 'manage'].includes(this.urlParams.flag)
    }
  },
  mounted () {
    const { ledgerId } = this.urlParams.row
    console.log(this.urlParams.row)
    if (ledgerId) {
      this.ledgerId = ledgerId
      this.getFormDetail()
    }
  },
  methods: {
    async getFormDetail () {
      const response = await jcAccount.getDetail({ledgerId: this.ledgerId})
      if (response.data) {
        this.form = response.data
      }
    },
    initParams () { // 参数
      let params = JSON.parse(JSON.stringify(this.form))
      console.log('params', params)
      return params
    },
    async validBill () {
      return new Promise(async (resolve) => {
        let validForm
        await this.$refs.form.validate(valid => { validForm = valid })
        resolve(validForm)
      })
    },
    async saveBill (type) {
      let params = this.initParams()
      if (type === 'SUBMIT') {
        const validForm = await this.validBill()
        if (!validForm) {
          this.__focus_error__()
        }

        // TODO 协议信息的校验
      }
      const response = await jcAccount.save(params)
      if (response.data) {
        this.form = response.data
        this.$message.success(this.$t('common.successSave'))
        // await this.getFormDetail()
        if (type === 'SUBMIT') {
          this.back()
        }
      }
    },
    moneyChages(obj){
      if (!obj?.a1 || !obj?.a2) {
        return false
      }
      const a3 = obj.a1-obj.a2
      this.$set(this.form, obj?.a3, a3)
      const a4 = (a3/obj.a1) * 100
      this.$set(this.form, obj?.a4, a4.toFixed(2))
    },
    back () {
      let { tabName } = this.$attrs.params
      this.$emit('tab-remove', tabName)
      this.__setTabTodo('JcAccountList.getQueryData')
    }
  }
}
</script>
