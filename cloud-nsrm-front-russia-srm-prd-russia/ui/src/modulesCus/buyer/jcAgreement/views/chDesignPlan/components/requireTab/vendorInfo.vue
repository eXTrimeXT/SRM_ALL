<template>
  <div class="wrapper">
    <div v-if="!readonly" class="btns mb-10">
      <el-button type="primary" @click="addNew">
        {{ $t('common.add') }}
      </el-button>
      <el-button :loading="saveLoading" @click="handleSave">
        {{ $t('common.save') }}
      </el-button>
    </div>
    <el-table
      border
      stripe
      :data="tableData"
      max-height="250px"
    >
      <el-table-column
        prop="supName"
        label="供应商名称"
        showOverflowTooltip
      >
        <template v-slot="{row,$index}">
          <QuickSearch
            v-if="!readonly"
            :show-input="row.supName"
            show-key="companyName"
            :scope-data="row"
            name="scc_sup_company_info_display"
            @close-quicksearch="getSupObj"
          />
          <span v-else>{{ row.supName }}</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="nature"
        label="性质"
        showOverflowTooltip
      >
        <template v-slot="{row,$index}">
          <!-- <DictSelect v-if="!readonly" v-model="row.nature" code="COMPANY_NATURE" /> -->
          <el-input v-if="!readonly" v-model="row.nature" />
          <span v-else>{{ row.nature }}</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="arrivalRate"
        label="到货及时率"
        showOverflowTooltip
      >
        <template v-slot="{row,$index}">
          <el-input v-if="!readonly" v-model="row.arrivalRate" :disabled="readonly" />
          <span v-else>{{ row.arrivalRate }}</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="remark"
        label="备注"
        showOverflowTooltip
      >
        <template v-slot="{row,$index}">
          <el-input v-if="!readonly" v-model="row.remark" :disabled="readonly" />
          <span v-else>{{ row.remark }}</span>
        </template>
      </el-table-column>
      <el-table-column
        v-if="!readonly"
        prop="operation"
        label="操作"
        width="100"
      >
        <template v-slot="{row,$index}">
          <el-button type="text" @click="deleteRow($index)">
            {{ $t('common.delete') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script>
import QuickSearch from 'lib@/components/QuickSearch'
import { designPlanHttp } from 'modcb@/jcAgreement/api'

export default {
  components: {
    QuickSearch
  },
  props: {
    value: {
      type: Array,
      default: () => []
    },
    readonly: {
      type: Boolean,
      default: false
    },
    form: {
      type: Object,
      default: () => {}
    }
  },
  emits: ['update:value'],
  data () {
    return {
      saveLoading: false
    }
  },
  computed: {
    tableData: {
      get () {
        return this.value
      },
      set (value) {
        this.$emit('update:value', value)
      }
    }
  },
  methods: {
    addNew () {
      this.tableData.push({
        supId: null,
        supCode: null,
        supName: null,
        nature: null,
        arrivalRate: null,
        remark: null
      })
    },
    deleteRow (index) {
      this.tableData.splice(index, 1)
    },
    async handleSave () {
      let params = {
        designId: this.form.designId,
        list: this.tableData
      }
      const response = await designPlanHttp.saveOrUpdateDemandSup(params)
      if (response) {
        this.$message.success(this.$t('common.success'))
      }
    },
    // 供应商快查
    getSupObj (val, scope) {
      scope.supId = val ? val.companyId : null
      scope.supCode = val ? val.companyCode : null
      scope.supName = val ? val.companyName : null
      // scope.nature = val ? val.companyType : null
    }
  }
}
</script>
<style lang="scss" scoped>
.mt-10 {
  margin-top: 10px;
}
.mb-10 {
  margin-bottom: 10px;
}
</style>
