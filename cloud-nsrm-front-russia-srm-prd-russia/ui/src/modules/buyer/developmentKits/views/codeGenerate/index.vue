<!-- 代码生成 -->
<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-main class="wrapper">
      <transition name="el-fade-in-linear">
        <div
          v-show="show"
          class="generate-type"
        >
          <el-form
            ref="form"
            :model="form"
          >
            <el-form-item
              label="生成方式"
              required
            >
              <el-radio-group v-model="form.generateType">
                <el-radio label="0">
                  代码
                </el-radio>
                <el-radio label="1">
                  接口
                </el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item
              v-if="form.generateType === '0'"
              label="表数量"
              required
            >
              <el-radio-group v-model="form.tableCount">
                <el-radio label="0">
                  单表
                </el-radio>
                <el-radio label="1">
                  头行表
                </el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item
              v-if="form.generateType === '0'"
              label="新增和修改方式"
              prop="saveUpdateType"
              required
            >
              <el-radio-group v-model="form.saveUpdateType">
                <el-radio
                  v-if="form.tableCount === '0'"
                  label="pop"
                >
                  弹窗
                </el-radio>
                <el-radio label="tap">
                  标签页
                </el-radio>
                <el-radio
                  v-if="form.tableCount === '0'"
                  label="append"
                >
                  追加
                </el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item
              label="作者邮箱"
              prop="author"
              :rules="rules"
            >
              <el-input v-model.trim="form.author" />
            </el-form-item>
            <el-form-item
              label="功能描述"
              prop="functionDesc"
              required
            >
              <el-input v-model.trim="form.functionDesc" />
            </el-form-item>
          </el-form>
        </div>
      </transition>
      <transition name="el-fade-in-linear">
        <CodeGeneratePage
          v-show="codeGenerateShow"
          :base-data="baseData"
          @close="close"
        />
      </transition>
    </el-main>
    <transition name="el-fade-in-linear">
      <CToolbar v-show="show">
        <template #right>
          <el-button
            type="primary"
            @click="clickHandler"
          >
            确定
          </el-button>
        </template>
      </CToolbar>
    </transition>
  </el-container>
</template>

<script>
import CodeGeneratePage from './code'
import CToolbar from 'lib@/components/c-toolbar'
import { isEmail } from 'lib@/utils/validate'

export default {
  name: 'CodeGenerate',
  components: { CodeGeneratePage, CToolbar },
  data () {
    return {
      codeGenerateShow: false,
      show: true,
      form: {
        generateType: '0',
        tableCount: '0',
        saveUpdateType: 'tap',
        author: '',
        functionDesc: ''
      },
      rules: [
        { required: true, message: this.$t('dataConfMod.msgMail') }, // "请输入邮箱"
        {
          validator: (rule, value, callback) => {
            if (!value) {
              callback(new Error(this.$t('dataConfMod.msgMail'))) // "请输入邮箱"
            } else if (!isEmail(value)) {
              callback(new Error(this.$t('dataConfMod.msgIllegalMail'))) // "邮箱格式不合法"
            } else {
              callback()
            }
          }
        }
      ]
    }
  },
  computed: {
    baseData () {
      return {
        functionDesc: this.form.functionDesc,
        addOrEditorMode: this.form.saveUpdateType,
        isMultipleTables: this.form.tableCount,
        author: this.form.author,
        generateType: this.form.generateType
      }
    }
  },
  watch: {},
  created () {},
  mounted () {},
  methods: {
    clickHandler () {
      this.$refs.form.validate(b => {
        if (b) {
          this.show = false
          this.codeGenerateShow = true
        }
      })
    },
    close () {
      this.show = true
      this.codeGenerateShow = false
    }
  }
}
</script>
<style scoped lang="scss">
.wrapper {
  position: relative;
}
.generate-type {
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  width: 400px;
}
</style>
