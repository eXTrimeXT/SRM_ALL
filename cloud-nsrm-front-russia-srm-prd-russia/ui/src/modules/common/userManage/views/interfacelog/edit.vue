<!--  -->
<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-main>
      <el-form
        ref="form"
        :model="form"
        label-width="100px"
        label-position="top"
        class="form-incontainer"
        style="padding-top: 10px;"
      >
        <!-- <el-collapse-item title="引入组织和品类" name="2"> -->
        <el-row :gutter="32">
          <el-col :span="6">
            <el-form-item :label="$t('vendorMod.interfaceName')">
              <el-input
                v-model="form.serviceName"
                disabled
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item :label="$t('interfacelog.label1')">
              <DictSelect
                v-model="form.serviceType"
                code="INTERFACE_LOG_SERVICE_TYPE"
                disabled
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item :label="$t('interfacelog.label2')">
              <DictSelect
                v-model="form.type"
                code="INTERFACE_LOG_TYPE"
                disabled
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item :label="$t('priceFormula.formulaStatus')">
              <DictSelect
                v-model="form.status"
                code="INTERFACE_LOG_STATUS"
                disabled
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item :label="$t('reApproval.receiptId')">
              <el-input
                v-model="form.billId"
                disabled
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item :label="$t('mould.mouldFlowType')">
              <DictSelect
                v-model="form.billType"
                code=""
                disabled
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item :label="$t('interfacelog.label3')">
              <el-input
                v-model="form.targetSys"
                disabled
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item :label="$t('interfacelog.label4')">
              <el-input
                v-model="form.dealTime"
                disabled
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item :label="$t('meeting.completeDate')">
              <el-input
                v-model="form.finishDate"
                disabled
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item :label="$t('vendorMod.content')">
              <el-input
                v-model="form.serviceInfo"
                type="textarea"
                :rows="10"
                disabled
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item :label="$t('interfacelog.label5')">
              <el-input
                v-model="form.returnInfo"
                type="textarea"
                :rows="10"
                disabled
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item :label="$t('dataConfMod.errorInfo')">
              <el-input
                v-model="form.errorInfo"
                type="textarea"
                :rows="3"
                disabled
              />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- </el-collapse-item> -->
      </el-form>
    </el-main>
  </el-container>
</template>

<script>
import { tabTodoMixin } from '@/utils/mixins'
import { interfaceApi } from 'mod@/common/userManage/api'
export default {
  name: 'InterfacelogEdit',
  mixins: [tabTodoMixin],
  props: {
    params: {
      type: Object,
      dafault: () => {}
    }
  },
  data () {
    return {
      form: {},
      // 字典相关
      serviceNameOpts: []
    }
  },
  computed: {},
  watch: {},
  created () {},
  mounted () {
    interfaceApi.getInterfaceLog({ logId: this.params.logId }).then(res => {
      this.form = res.data
    })
  },
  methods: {
  }
}
</script>
<style scoped lang="scss"></style>
