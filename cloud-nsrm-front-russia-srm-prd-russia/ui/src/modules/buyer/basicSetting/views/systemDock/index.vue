<template>
  <el-container
    class="flex-container-notab the_buyerDeliveryNotice_wrapper"
    direction="vertical"
  >
    <el-main>
      <el-form
        ref="form"
        :rules="rules"
        :model="form"
        label-width="80px"
        label-position="top"
        class="form-incontainer"
      >
        <el-collapse
          v-model="activeDims"
          class="tab-form-style"
        >
          <!-- 对接配置 -->
          <el-collapse-item
            :title="$t('dataConfMod.dockConfig')"
            name="1"
          >
            <srm-row>
              <srm-col>
                <!-- 对接主系统 -->
                <el-form-item
                  :label="$t('dataConfMod.dockMainSyetem')"
                  :label-width="formLabelWidth"
                  prop="mainSyetem"
                >
                  <el-select v-model="form.mainSyetem">
                    <el-option
                      v-for="item in selectDictionary.mainSyetem"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </el-form-item>
              </srm-col>
              <srm-col>
                <!-- 对接方式 -->
                <el-form-item
                  :label="$t('dataConfMod.dockType')"
                  :label-width="formLabelWidth"
                  prop="mainSyetemDockType"
                >
                  <el-select v-model="form.mainSyetemDockType">
                    <el-option
                      v-for="item in selectDictionary.dockType"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </el-form-item>
              </srm-col>
              <srm-col>
                <!-- 对接流程 -->
                <el-form-item
                  :label="$t('dataConfMod.dockFlow')"
                  :label-width="formLabelWidth"
                  prop="flow"
                >
                  <el-select v-model="form.flow">
                    <el-option
                      v-for="item in selectDictionary.flow"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </el-form-item>
              </srm-col>
              <srm-col>
                <!-- 对接方式 -->
                <el-form-item
                  :label="$t('dataConfMod.dockType')"
                  :label-width="formLabelWidth"
                  prop="flowDockType"
                >
                  <el-select v-model="form.flowDockType">
                    <el-option
                      v-for="item in selectDictionary.dockType"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-collapse-item>
        </el-collapse>
      </el-form>

      <c-toolbar>
        <template slot="right">
          <el-button
            type="primary"
            @click="submitBill"
          >
            <!-- 重置 -->
            {{ $t("common.save") }}
          </el-button>
        </template>
      </c-toolbar>
    </el-main>
  </el-container>
</template>
<script>
import CToolbar from 'lib@/components/c-toolbar'

export default {
  name: 'SystemDock',
  components: { CToolbar },
  data () {
    return {
      activeDims: ['1'],
      formLabelWidth: '120px',
      form: {
        mainSyetem: null,
        mainSyetemDockType: null,
        flow: null,
        flowDockType: null
      },
      rules: {
        mainSyetem: [
          { required: true, message: this.$t('dataConfMod.msgMainSystem') }
        ], // 请选择对接主系统
        mainSyetemDockType: [
          { required: true, message: this.$t('dataConfMod.msgDockType') }
        ], // 请选择对接方式
        flow: [{ required: true, message: this.$t('dataConfMod.msgDockFlow') }], // 请选择对接流程
        flowDockType: [
          { required: true, message: this.$t('dataConfMod.msgDockType') }
        ] // 请选择对接方式
      },
      selectDictionary: {
        mainSyetem: [],
        flow: [],
        dockType: []
      }
    }
  },
  methods: {
    submitBill () {
      this.$refs['form'].validate(valid => {
        if (valid) {
          alert('submit!')
        } else {
          return false
        }
      })
    }
  }
}
</script>
