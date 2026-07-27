<template>
  <el-container
    class="flex-container the_currency_wrapper"
    direction="vertical"
  >
    <el-main>
      <form-wrapper
        :form-array="queryForm"
        @getFormData="getQuerydata"
      />
      <main-header
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <AuthorityButton
            type="primary"
            code="sup:vendorAttributeSetting:attrOptHandle"
            @click="attrOptHandle"
          >
            <!-- 新增配置 -->
            {{ $t("common.add") }}
          </AuthorityButton>
        </template>
      </main-header>
      <table-view
        :ref="gridId"
        :table-data="tableList"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :com-active="$attrs['changeTab']"
        url="/api-sup/dim/dimTemplate/listPage"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import AttributeDimensionConf from './AttributeDimensionConf'
import AttributeConfPreview from './AttributeConfPreview'
import { tabTodoMixin, tabTodoWatch } from '@/utils/mixins'

export default {
  name: 'AttributeConfList',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  mixins: [tabTodoMixin, tabTodoWatch],
  data () {
    return {
      pageSize: 15,
      gridId: 'attrConfList',
      currentRow: null,
      showFilterBar: 1,
      queryParam: {},
      dialogFormVisible: false,
      queryForm: [], // 查询条件
      tableHeader: [], // 表格列数据
      tableList: [],
      tableTotal: 0, // 分页数据
      tableLoading: false
    }
  },
  created () {
    let _this = this
    this.queryForm = [
      {
        prop: 'templateVersion',
        label: () => this.$t('dataConfMod.version') // '版本号'
      },
      {
        prop: 'overseasRelation',
        label: () => this.$t('dataConfMod.overseasRelation'), // '境内外关系',
        type: 'dict',
        code: 'RELATION'
      },
      {
        prop: 'companyType',
        label: () => this.$t('dataConfMod.companyType'), // '企业性质',
        type: 'dict',
        code: 'COMPANY_NATURE'
      }
    ]
    this.tableHeader = [
      {
        prop: 'overseasRelation',
        label: () => this.$t('dataConfMod.overseasRelation'), // '境内外关系',
        minWidth: '160',
        dataType: 'dict',
        code: 'RELATION'
      },
      {
        prop: 'companyType',
        label: () => this.$t('dataConfMod.companyType'), // '企业性质',
        minWidth: '160',
        dataType: 'dict',
        code: 'COMPANY_NATURE'
      },
      {
        prop: 'templateVersion',
        label: () => this.$t('dataConfMod.version'), // '版本号',
        minWidth: '160'
      },
      {
        prop: 'lastUpdatedUserName', // lastUpdatedBy
        label: () => this.$t('common.updatePeople'), // '更新人'
        formattor (val, row) {
          return val || row.createdUserName
        }
      },
      {
        prop: 'lastUpdateDate',
        label: () => this.$t('common.updateTime') // '更新时间'
      },
      {
        label: () => this.$t('common.operation'), // '操作',
        width: '100',
        fixed: 'right',
        editType: 'none',
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.editDetail(row)
        }.bind(this),
        code: 'sup:vendorAttributeSetting:edit',
        formattor () {
          return _this.$t('common.edit') // '编辑'
        }
      }
    ]
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    dolayout () {
      this.$refs[this.gridId].doLayout()
    },
    getQuerydata (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    editDetail (row) {
      let tempId = row.templateId
      this.$emit('tab-add', {
        component: AttributeConfPreview,
        params: {
          flag: 'edit',
          templateId: tempId,
          tabName: 'AttributeConfPreview' + tempId
        },
        title: () =>
          row.templateVersion + this.$t('dataConfMod.attributeConfPreview'), // '属性配置预览',
        name: 'AttributeConfPreview' + tempId
      })
    },
    // 属性维度配置
    attrDimHandle () {
      // 打开tab页面--',
      this.$emit('tab-add', {
        component: AttributeDimensionConf,
        params: { flag: 'add' },
        title: () => this.$t('dataConfMod.attributeDimensionConf')[0], // '定义属性维度',
        name: 'AttributeDimensionConf'
      })
    },
    attrOptHandle () {
      // 打开tab页面--',
      this.$emit('tab-add', {
        component: AttributeConfPreview,
        params: { flag: 'add' },
        title: () => this.$t('dataConfMod.attributeConfPreview'), // '属性配置预览',
        name: 'AttributeConfPreview'
      })
    }
  }
}
</script>
<style scoped lang="scss"></style>
