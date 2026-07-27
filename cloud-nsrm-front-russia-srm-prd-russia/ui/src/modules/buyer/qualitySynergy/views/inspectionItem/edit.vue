<template>
  <el-container class="flex-container the-inspectionItemDetail-detail" direction="vertical">
    <el-main>
      <div class="form-container2">
        <el-form
          ref="form"
          :model="form"
          label-width="80px"
          label-position="top"
          :rules="rules"
        >
          <srm-row>
            <!-- 检验项目 -->
            <srm-col>
              <el-form-item
                :label="$t('qualitySynergy.inspectionProjectName')"
                :label-width="formLabelWidth"
                prop="inspectionProjectName"
              >
                <el-input v-model="form.inspectionProjectName" maxlength="500" />
              </el-form-item>
            </srm-col>

            <!-- 检验工具 -->
            <srm-col>
              <el-form-item :label="$t('qualitySynergy.inspectionTool')" :label-width="formLabelWidth" prop="inspectionTool">
                <el-input v-model="form.inspectionTool" maxlength="200" />
              </el-form-item>
            </srm-col>

            <!-- 检验项目类别 -->
            <srm-col>
              <el-form-item
                :label="$t('qualitySynergy.inspectionCategory')"
                :label-width="formLabelWidth"
                prop="inspectionCategory"
              >
                <el-input v-model="form.inspectionCategory" maxlength="50" />
              </el-form-item>
            </srm-col>

            <!-- 特性分级 -->
            <srm-col>
              <el-form-item :label="$t('qualitySynergy.perfGrade')" :label-width="formLabelWidth" prop="perfGrade">
                <dict-select v-model="form.perfGrade" code="INS_PERF_GRADE" />
              </el-form-item>
            </srm-col>

            <!-- 项目特性 -->
            <srm-col>
              <el-form-item
                :label="$t('qualitySynergy.inspectionProjectPerf')"
                :label-width="formLabelWidth"
                prop="inspectionProjectPerf"
              >
                <dict-select v-model="form.inspectionProjectPerf" code="INS_PRO_PERF" />
              </el-form-item>
            </srm-col>

            <!-- 抽样标准 -->
            <srm-col>
              <el-form-item :label="$t('qualitySynergy.sampleStandard')" :label-width="formLabelWidth" prop="sampleStandard">
                <dict-select v-model="form.sampleStandard" code="INS_SAMPLE_STANDARD" />
              </el-form-item>
            </srm-col>

            <!-- 检验项目属性 -->
            <srm-col>
              <el-form-item
                :label="$t('qualitySynergy.inspectionProjectAttribute')"
                :label-width="formLabelWidth"
                prop="inspectionProjectAttribute"
              >
                <dict-select v-model="form.inspectionProjectAttribute" code="INS_PRO_ATTR" />
              </el-form-item>
            </srm-col>

            <!-- 检验依据 -->
            <srm-col>
              <el-form-item :label="$t('qualitySynergy.inspectionBasis')" :label-width="formLabelWidth" prop="inspectionBasis">
                <el-input v-model="form.inspectionBasis" maxlength="200" />
              </el-form-item>
            </srm-col>

            <!-- 检验水平 -->
            <srm-col>
              <el-form-item
                :label="$t('qualitySynergy.inspectionLevel')"
                :label-width="formLabelWidth"
                :rules="form.sampleStandard ==='GBT2828' ? rules.inspectionLevel : [{ required:false}]"
              >
                <dict-select v-model="form.inspectionLevel" code="INS_INSPECTION_LEVEL" />
              </el-form-item>
            </srm-col>

            <!-- 严格度 -->
            <srm-col>
              <el-form-item
                :label="$t('qualitySynergy.strictLevel')"
                :label-width="formLabelWidth"
                :rules="form.sampleStandard ==='GBT2828' ? rules.strictLevel : [{ required:false}]"
              >
                <dict-select v-model="form.strictLevel" code="INS_SEVERITY" />
              </el-form-item>
            </srm-col>

            <!-- 抽样方式 -->
            <srm-col>
              <el-form-item
                :label="$t('qualitySynergy.sampleMode')"
                :label-width="formLabelWidth"
                :rules="form.sampleStandard ==='GBT2828' ? rules.sampleMode : [{ required:false}]"
              >
                <dict-select v-model="form.sampleMode" code="INS_SAMPLING_METHOD" />
              </el-form-item>
            </srm-col>

            <!-- 检验类型 -->
            <srm-col>
              <el-form-item :label="$t('qualitySynergy.inspectionType')" :label-width="formLabelWidth" prop="inspectionType">
                <dict-select v-model="form.inspectionType" code="INS_TYPE" />
              </el-form-item>
            </srm-col>
          </srm-row>
        </el-form>
      </div>
      <CToolbar>
        <template slot="right">
          <!-- 关闭 -->
          <el-button @click="backOne">
            {{ $t("common.close") }}
          </el-button>
          <!-- 提交 -->
          <el-button type="primary" @click="submitOne">
            {{ $t('common.submit') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>
<script>
import CToolbar from 'lib@/components/c-toolbar'
import { tabTodoMixin } from '@/utils/mixins'
import { inspectionStandard } from 'modb@/qualitySynergy/api'

export default {
  name: 'InspectionItemDetail',
  components: {
    CToolbar
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      form: {
        inspectionProjectName: '',
        inspectionTool: '',
        inspectionCategory: '',
        perfGrade: '',
        inspectionProjectPerf: '',
        sampleStandard: '',
        inspectionProjectAttribute: '',
        inspectionBasis: '',
        inspectionLevel: '',
        strictLevel: '',
        sampleMode: '',
        inspectionType: ''
      },
      rules: {
        inspectionProjectName: [{ required: true, message: this.$t('qualitySynergy.inspectionProjectNameRequired') }], // 检验项目
        inspectionTool: [{ required: true, message: this.$t('qualitySynergy.inspectionToolRequired') }], // 检验工具
        inspectionCategory: [{ required: true, message: this.$t('qualitySynergy.inspectionCategoryRequired') }], // 检验项目类别
        perfGrade: [{ required: true, message: this.$t('qualitySynergy.perfGradeRequired') }], // 特性分级
        inspectionProjectPerf: [{ required: true, message: this.$t('qualitySynergy.inspectionProjectPerfRequired') }], // 项目特性
        sampleStandard: [{ required: true, message: this.$t('qualitySynergy.sampleStandardRequired') }], // 抽样标准
        inspectionProjectAttribute: [{ required: true, message: this.$t('qualitySynergy.inspectionProjectAttributeRequired') }], // 检验项目属性
        inspectionBasis: [{ required: true, message: this.$t('qualitySynergy.inspectionBasisRequired') }], // 检验依据
        inspectionLevel: [{ required: true, message: this.$t('qualitySynergy.inspectionLevelRequired') }], // 检验水平
        strictLevel: [{ required: true, message: this.$t('qualitySynergy.strictLevelRequired') }], // 严格度
        sampleMode: [{ required: true, message: this.$t('qualitySynergy.sampleModeRequired') }], // 抽样方式
        inspectionType: [{ required: true, message: this.$t('qualitySynergy.inspectionTypeRequired') }] // 检验类型
      },
      formLabelWidth: '120px'
    }
  },
  created () {
    const { flag, row } = this.$attrs.params
    if (flag === 'edit') {
      this.getFormDetail(row.inspectionProjectId)
    }
  },
  methods: {
    async getFormDetail (id) {
      let res = await inspectionStandard.inspectionItemDetail({ id })
      if (res.data) {
        this.form = res.data
      }
    },
    backOne () {
      if (this.$attrs.params.flag === 'add') {
        this.$emit('tab-remove', 'inspectionItemDetail')
      } else {
        this.$emit('tab-remove', this.$attrs.params.tabName)
      }
      this.__setTabTodo('inspectionItemList.getQuerydata')
    },
    async submitOne () {
      this.$refs.form.validate(async valid => {
        if (valid) {
          let url = `/api-pef/perf/inspectionproject/${
              this.$attrs.params.flag === 'add' ? 'submit' : 'update'
            }`
          await inspectionStandard.inspectionItemModify(url, { ...this.form, status: 'SUBMITTED' })
          this.$message.success(this.$t('common.success'))
          this.backOne()
        }
      })
    }
  }
}
</script>
<style lang="scss" scoped></style>
