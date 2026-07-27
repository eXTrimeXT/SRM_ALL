<template>
  <div class="work-info">
    <h3>{{ $t('dataConfMod.participateEvaluationProjects') }}</h3>

    <p v-if="!isReadonly">
      <el-button
        type="primary"
        @click="addRow"
      >
        {{ $t('common.add') }}
      </el-button>
    </p>

    <el-table
      :data="evaluateProjectListData"
      style="width: 100%;"
      border
      max-height="250px"
    >
      <el-table-column
        align="center"
        type="index"
        width="50"
      />

      <!--项目名称-->
      <el-table-column
        align="center"
        prop="businessName"
        :label="$t('perfMod.projectName')"
        min-width="150"
        show-overflow-tooltip
        :render-header="_addStarToColumn"
      >
        <template v-slot="{ row }">
          <el-input v-model="row.businessName" :disabled="isReadonly" />
        </template>
      </el-table-column>

      <!--评标类型-->
      <el-table-column
        align="center"
        prop="moduleName"
        :label="$t('dataConfMod.moduleName')"
        min-width="150"
        show-overflow-tooltip
        :render-header="_addStarToColumn"
      >
        <template v-slot="{ row }">
          <dict-select
            v-model="row.moduleName"
            code="SCORE_RULE_WAY"
            :disabled="isReadonly"
            :transform-options="transformOptions"
          />
        </template>
      </el-table-column>

      <!--评标角色-->
      <el-table-column
        align="center"
        prop="evaluationRole"
        :label="$t('dataConfMod.evaluationRole')"
        min-width="150"
        show-overflow-tooltip
      >
        <template v-slot="{ row }">
          <el-input v-model="row.evaluationRole" :disabled="isReadonly" />
        </template>
      </el-table-column>

      <el-table-column
        v-if="!isReadonly"
        fixed="right"
        align="center"
        :label="$t('bidMod.operation')"
        width="100"
      >
        <template v-slot="scope">
          <!--删除-->
          <el-button
            type="text"
            @click="deleteRow(scope.$index)"
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
 * 参与评标项目
 */
export default {
  name: 'PartakeProject',
  props: {
    evaluateProjectList: {
      type: [Array, Object],
      required: true
    },
    isReadonly: {
      type: Boolean,
      default: false
    }
  },
  computed: {
    evaluateProjectListData: {
      get: function () {
        return this.evaluateProjectList
      },
      set: function (val) {
        this.$emit('update:evaluateProjectList', val)
      }
    }
  },
  methods: {
    /* 编排字典下拉框过滤 */
    transformOptions (options) {
      // 暂时只要招投标、项目式询价
      return options.filter(item => ['BIDDING', 'BARGAIN'].includes(item.value))
    },

    /* 新增行 */
    addRow () {
      this.evaluateProjectListData.push({
        moduleName: '',
        businessName: '',
        evaluationRole: ''
      })
    },

    /* 删除行 */
    deleteRow (index) {
      this.evaluateProjectListData.splice(index, 1)
    }
  }
}
</script>
