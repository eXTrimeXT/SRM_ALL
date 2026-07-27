<template>
  <div class="education-info" style="margin-bottom: 20px">
    <h3>{{ $t('dataConfMod.diplomaInfoTable') }}</h3>

    <p v-if="!isReadonly">
      <el-button
        type="primary"
        @click="addRow"
      >
        {{ $t('common.add') }}
      </el-button>
    </p>

    <el-table
      :data="educationListData"
      style="width: 100%;"
      border
      max-height="250px"
    >
      <el-table-column
        align="center"
        type="index"
        width="50"
      />

      <!--学历-->
      <el-table-column
        align="center"
        prop="education"
        :label="$t('dataConfMod.education')"
        width="150"
        :render-header="_addStarToColumn"
      >
        <template v-slot="{ row }">
          <dict-select
            v-model="row.education"
            code="EXPERT_EDUCATION"
            :disabled="isReadonly"
          />
        </template>
      </el-table-column>

      <!--就读院校-->
      <el-table-column
        align="center"
        prop="studyCollege"
        :label="$t('dataConfMod.studyCollege')"
        width="150"
        :render-header="_addStarToColumn"
      >
        <template v-slot="{ row }">
          <el-input v-model="row.studyCollege" :disabled="isReadonly" />
        </template>
      </el-table-column>

      <!--是否全日制-->
      <el-table-column
        align="center"
        prop="fullTimeStudy"
        :label="$t('dataConfMod.fullTimeStudy')"
        width="110"
        :render-header="_addStarToColumn"
      >
        <template v-slot="{ row }">
          <el-checkbox
            v-model="row.fullTimeStudy"
            true-label="Y"
            false-label="N"
            :disabled="isReadonly"
          />
        </template>
      </el-table-column>

      <!--就读时间-->
      <el-table-column
        align="center"
        prop="studyTime"
        :label="$t('dataConfMod.studyTime')"
        width="250"
        :render-header="_addStarToColumn"
      >
        <template v-slot="{ row }">
          <el-date-picker
            v-model="row.studyTime"
            type="daterange"
            :range-separator="$t('components.to')"
            :start-placeholder="$t('components.beginDate')"
            :end-placeholder="$t('components.dateClosed')"
            :disabled="isReadonly"
          />
        </template>
      </el-table-column>

      <!--主修专业-->
      <el-table-column
        align="center"
        prop="major"
        :label="$t('dataConfMod.major')"
        min-width="150"
        :render-header="_addStarToColumn"
      >
        <template v-slot="{ row }">
          <el-input v-model="row.major" :disabled="isReadonly" />
        </template>
      </el-table-column>

      <!--辅修专业-->
      <el-table-column
        align="center"
        prop="minor"
        :label="$t('dataConfMod.minor')"
        min-width="150"
      >
        <template v-slot="{ row }">
          <el-input v-model="row.minor" :disabled="isReadonly" />
        </template>
      </el-table-column>

      <!--学位证书-->
      <SrmCommonFile
        type="table-column"
        :extra-data="fileInfo"
        :table-column-options="{
          label: $t('dataConfMod.degreeCertificateDocId'),
          prop: 'degreeCertificateDocId',
          nameProp: 'degreeCertificateFileName'
        }"
        :readonly="isReadonly"
        @on-change="degreeCertificateFilesChange"
      />

      <!--毕业证书-->
      <SrmCommonFile
        type="table-column"
        :extra-data="fileInfo"
        :table-column-options="{
          label: $t('dataConfMod.graduationCertificateDocId'),
          prop: 'graduationCertificateDocId',
          nameProp: 'graduationCertificateFileName'
        }"
        :readonly="isReadonly"
        @on-change="graduationCertificateFilesChange"
      />

      <el-table-column
        v-if="!isReadonly"
        fixed="right"
        align="center"
        :label="$t('bidMod.operation')"
        width="100"
      >
        <template v-slot="{ $index }">
          <!--删除-->
          <el-button
            type="text"
            @click="deleteRow($index)"
          >
            {{ $t('common.delete') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
/**
 * 学历信息
 */
export default {
  name: 'EducationInfo',

  props: {
    educationList: {
      type: [Array, Object],
      required: true
    },
    isReadonly: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      fileInfo: {
        fileModular: 'bid',
        fileFunction: 'expertDatabase',
        fileType: 'images'
      }
    }
  },

  computed: {
    educationListData: {
      get: function () {
        return this.educationList
      },
      set: function (val) {
        this.$emit('update:educationList', val)
      }
    }
  },

  methods: {
    /* 学位证书文件变更 */
    degreeCertificateFilesChange ({ file, $index }) {
      const { fileId = '', fileName = '' } = file || {}
      this.educationListData[$index].degreeCertificateDocId = fileId
      this.educationListData[$index].degreeCertificateFileName = fileName
    },

    /* 毕业证书文件变更 */
    graduationCertificateFilesChange ({ file, $index }) {
      const { fileId = '', fileName = '' } = file || {}
      this.educationListData[$index].graduationCertificateDocId = fileId
      this.educationListData[$index].graduationCertificateFileName = fileName
    },

    /* 新增行 */
    addRow () {
      this.educationListData.push({
        education: '',
        studyCollege: '',
        fullTimeStudy: 'N',
        studyTime: '',
        studyTimeFrom: '',
        studyTimeTo: '',
        major: '',
        minor: '',
        degreeCertificateDocId: '',
        degreeCertificateFileName: '',
        graduationCertificateDocId: '',
        graduationCertificateFileName: ''
      })
    },

    /* 删除行 */
    deleteRow (index) {
      this.educationListData.splice(index, 1)
    }
  }
}
</script>
