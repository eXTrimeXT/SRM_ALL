<!-- 动态表格 -->
<template>
  <el-container
    direction="vertical"
    class="easy-table"
  >
    <div class="main">
      <div
        v-if="Object.keys($scopedSlots).includes('btns')"
        class="btns-wrapper"
      >
        <slot name="btns" />
      </div>
      <div
        v-else
        class="other-wrapper"
      />
      <base-table
        ref="table"
        border
        :initialize="initialize"
        :loading="loading"
        :stripe="stripe"
        :index="index"
        :selection="selection"
        :editable="editable"
        v-bind="$attrs"
        :row-key="rowKey"
        :data-source="dataSource"
        :columns="realColumns"
        columns-name="realColumns"
        v-on="$listeners"
        @search="search"
        @reset="reset"
        @asyncGetRealDataSource="asyncGetRealDataSource"
      >
        <template
          v-for="name in tableColumns"
          #[name]="{ scope }"
        >
          <slot
            :name="name"
            :scope="scope"
          />
        </template>
      </base-table>
    </div>
    <el-footer class="page-bar">
      <el-row type="flex">
        <el-col :span="24">
          <c-pagination
            ref="queryPagination"
            style="margin: 0;padding-bottom: 4px;"
            class="c-query-table-pagination"
            :total="total"
            :page-num="currentPage"
            :page-size="pageSize"
            :page-sizes="pageSizes"
            @current-change="handleCurrentChange"
            @size-change="handleSizeChange"
          />
        </el-col>
      </el-row>
    </el-footer>
    <custom-table
      v-if="openCustomTable"
      class="custom-table"
      :pageViewConfigCode="pageViewConfigCode"
      @updataConfig="updataConfig"
    />
  </el-container>
</template>

<script>
import BaseTable from 'lib@/components/BaseTable/baseTable'
import CPagination from 'lib@/components/c-pagination'
import CustomTable from 'lib@/components/custom-table'
import { ADD_KEY, UPDATE_KEY, EDITABLE_KEY } from './utils'
import cloneDeep from 'lodash/cloneDeep'
// 表格配置key前缀
const localStorageKeyPerfix = 'custom_table_key'
export default {
  name: 'EasyTable',
  components: { CPagination, BaseTable, CustomTable },
  props: {
    initialize: {
      type: Boolean,
      default: true
    },
    stripe: {
      type: Boolean,
      default: true
    },
    openCustomTable: {
      type: Boolean,
      default: true
    },
    methods: {
      type: Object,
      required: true,
      default: () => {}
    },
    tableName: {
      required: true,
      type: String
    },
    index: {
      type: Boolean,
      default: true
    },
    selection: {
      type: Boolean,
      default: true
    },
    editable: {
      type: Boolean,
      default: true
    },
    rowKey: {
      type: String
    },
    columns: {
      required: true,
      type: Array
    },
    pageSizes: {
      type: Array,
      default: () => [15, 30, 45, 60]
    },
    // 组件激活
    comActive: {
      type: [String, Number],
      default: null
    },
    // 表格配置key值，默认取当前路由name,如果一个路由页面有多个列表页需要开启配置，这个值必传
    customTableKey: {
      type: String,
      default: ''
    }
  },
  provide () {
    return { context: this }
  },
  data () {
    return {
      total: 0,
      currentPage: 1,
      loading: false,
      pageSize: 15,
      queryParams: {},
      dataSource: [],
      originColumns: [],
      realColumns: [],
      realDataSource: [],
      pageViewConfigCode: '' // 配置key 对应后台配置
    }
  },
  computed: {
    tableColumns () {
      return Object.keys(this.$scopedSlots).filter(slot => slot !== 'btns')
    },
    defaultTableHeader () {
      return this.originColumns
        .filter(i => !['selection', 'index'].includes(i.type))
        .map(i => ({
          label: typeof i.attrs.label === 'function' ? i.attrs.label() : i.attrs.label,
          prop: i.attrs.prop,
          width: i.attrs.width,
          minWidth: i.attrs.minWidth || '110px',
          fixed: i.attrs.fixed || undefined,
          show: typeof i.hidden === 'function' ? i.hidden() : i.hidden || true,
          version: 'v1'
        }))
    }
  },
  watch: {
    queryParams: {
      handler () {
        this.$emit('update:queryParams', this.queryParams)
      },
      deep: true,
      immediate: true
    },
    comActive: {
      handler: function (n, o) {
        if (n) {
          this.doLayout()
        }
      },
      deep: true
    }
  },
  activated () {
    this.doLayout()
  },
  async created () {
    this.pageViewConfigCode = this.customTableKey || this.$route.name
    this.originColumns = cloneDeep(this.columns)
    this.realColumns = cloneDeep(this.columns)
    await this.initComp() // 初始化表格配置
  },
  mounted () {},
  methods: {
    edit (scope, data) {
      this.$refs.table.rowDblclickClick(scope, data)
    },
    asyncGetRealDataSource (rows) {
      this.realDataSource = rows
    },
    getUpdatedRows () {
      if (!this.editable) return []
      return this.realDataSource
        .filter(row => row[ADD_KEY] || row[UPDATE_KEY])
        .map(
          ({
            [UPDATE_KEY]: updateKey,
            [ADD_KEY]: addKey,
            [EDITABLE_KEY]: editableKey,
            ...rest
          }) => ({ ...rest })
        )
    },
    add (row) {
      this.$refs.table.add(row)
    },
    doLayout () {
      this.$refs.table.tbDoLayout()
    },
    validate (callback) {
      this.$refs.table.validate(callback)
    },
    handleSizeChange (pageSize) {
      console.log(pageSize)
      this.pageSize = pageSize
      const allParams = {
        ...this.queryParams,
        pageSize
      }
      this.search(allParams, true)
    },
    handleCurrentChange (currentPage) {
      this.currentPage = currentPage
      const allParams = {
        ...this.queryParams,
        pageNum: currentPage
      }
      this.search(allParams, true)
    },
    reset (prop, params) {
      this.search({ ...params })
    },
    setQueryParams (params) {
      this.queryParams = params
    },
    async search (params, flag = false) {
      this.currentPage = flag ? this.currentPage : 1
      const allParams = {
        pageSize: this.pageSize,
        pageNum: this.currentPage,
        ...params
      }
      this.setQueryParams(allParams)
      this.loading = true
      try {
        const { data } = await this.methods.listPage(allParams)
        const { total, list } = data
        this.dataSource = list
        this.total = total
        this.loading = false
        this.doLayout()
      } catch (e) {
        this.loading = false
      }
    },
    clearSelection () {
      this.$refs.table.clearSelection()
    },
    // 表格自定义配置start
    // 配置信息
    getParams () {
      const userId = this.$store.getters.user.userId
      const key = `${localStorageKeyPerfix}_${userId}_${this.pageViewConfigCode}`
      return key
    },
    // 查询接口配置信息
    async fatchConfig () {
      let tableConfig = ''
      if (this.openCustomTable) {
        let query = { pageViewConfigCode: this.pageViewConfigCode }
        const { data = {} } = await this.$api.base.pageConfig.getCurrentConfig(query)
        tableConfig = data.tableConfig || ''
      }
      return tableConfig
    },
    // 查询配置
    async queryConfig () {
      const key = this.getParams()
      const JSON_CONFIG = localStorage.getItem(key) // 本地
      const JSON_CONFIG_SERVICE = await this.fatchConfig() // 获取后台配置信息
      const CONFIG_RES = JSON_CONFIG_SERVICE || JSON_CONFIG
      const config = CONFIG_RES ? JSON.parse(CONFIG_RES) : undefined
      let resConfig = await this.setColumn(config)
      return resConfig
    },
    // 设置显示列
    setColumn (configData) {
      const defaultTConfig = this.originColumns
      let meta = []
      if (configData) {
        let filterShow = configData.filter(i => i.show)
        meta = filterShow.map(({ prop, width, minWidth, fixed, version }) => {
          const target = defaultTConfig.find(i => i.attrs.prop === prop)
          let initFixed = fixed || target.fixed
          if (target.attrs.prop == 'operation') { // 操作列默认固定右侧
            initFixed = 'right'
          }
          return {
            ...target,
            version,
            attrs: {
              ...target.attrs,
              width: width,
              minWidth: width || '110px',
              fixed: initFixed
            }
          }
        })
      } else {
        meta = defaultTConfig.map(({ attrs, ...rest }) => {
          return {
            ...rest,
            attrs: {
              ...attrs,
              minWidth: attrs.width || '110px'
            }
          }
        })
      }
      return meta
    },
    // 初始化表格列
    async initComp () {
      if (this.openCustomTable) {
        let tHeader = await this.queryConfig()
        this.realColumns = tHeader
      } else {
        this.realColumns = this.originColumns
      }
    },
    // 配置后更新表格
    async updataConfig (isupdate) {
      if (isupdate) {
        await this.initComp()
        this.doLayout()
        this.$forceUpdate()
      }
    }
  }
}
</script>
<style scoped lang="scss">
.easy-table {
  height: 100%;
  min-height: 0;
  position: relative;
  .custom-table{
    position: absolute;
    right: 0px;
    top: 10px;
  }
}
.pagination {
  padding-top: 10px;
}
.main {
  display: flex;
  flex-direction: column;
  flex-grow: 1;
  min-height: 0;
}
.btns-wrapper {
  padding: 0px 120px 16px 0;
  display: flex;
}
.other-wrapper{
  margin-top: 0px;
}
.setting-button {
  display: flex;
  justify-content: center;
  align-items: center;
}
.el-table .cell.el-tooltip >span{
  max-width: 100%;
}

</style>
