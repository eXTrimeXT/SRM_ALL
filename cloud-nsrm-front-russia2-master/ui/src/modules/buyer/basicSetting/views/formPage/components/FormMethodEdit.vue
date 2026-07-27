<template>
  <srm-dialog
    :title="$t('dataConfMod.editCompProp')"
    :visible.sync="visible"
    :close-on-click-modal="false"
    :content-max-height-limit="false"
    fullscreen
    size="midden"
    @close="cancel"
    @closed="cancel"
  >
    <el-form ref="form">
      <el-row :gutter="32">
        <el-col :span="24">
          <el-form-item
            prop="methodName"
            :label="$t('reportSetting.methodName')"
          >
            <el-input v-model="formMethod.methodName" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item
            prop="methodDescribe"
            :label="$t('reportSetting.methodDescribe')"
          >
            <el-input v-model="formMethod.methodDescribe" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item
            prop="methodParams"
            :label="$t('reportMod.methodParamsDes')"
          >
            <el-input v-model="formMethod.methodParams" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item
            prop="methodBody"
            :label="$t('reportMod.methodBody')"
          >
            <xml-editor
              v-model="formMethod.methodBody"
              :show-btns="false"
              :lang="'zh'"
              :mode="{name: 'xml'}"
            />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button @click="cancel">
        {{ $t("common.cancel") }}
      </el-button>
      <el-button
        v-if="type === 'add' || type === 'edit'"
        type="primary"
        @click="saveConfig"
      >
        {{ $t("common.confirm") }}
      </el-button>
    </template>
  </srm-dialog>
</template>

<script>
import XmlEditor from '@/components/XmlEditor'
export default {
  name: 'FormMethodEdit',
  components: {
    XmlEditor
  },
  props: {
    visible: {
      type: Boolean,
      default: () => {
        return false
      }
    },
    type: {
      type: String,
      default: () => {
        return null
      }
    },
    formMethodConfig: {
      type: Object,
      default: () => {
        return {
          index: null,
          formLineId: null,
          lineType: 'METHOD',
          methodName: null,
          methodDescribe: null,
          methodParams: null,
          methodBody: null
        }
      }
    }
  },
  data () {
    return {
      formMethod: {
        index: null,
        formLineId: null,
        lineType: 'METHOD',
        methodName: null,
        methodDescribe: null,
        methodParams: null,
        methodBody: null
      }
    }
  },
  watch: {
    formMethodConfig: {
      handler () {
        this.formMethod = this.formMethodConfig
      },
      deep: true
    }
  },

  methods: {
    cancel () {
      this.$emit('cancel', this.formMethod, this.type)
    },
    saveConfig () {
      this.$emit('confirm', this.formMethod, this.type)
    }
  }
}
</script>
