export default {
  components: {
  },
  data () {
    return {
      componentName: '',
      componentConfig: {
        add: null,
        edit: null,
        view: null,
        extend: null
      },
      pageSize: 15,
      gridId: 'list',
      operationFunction: {
        deleteInfo: null
      },
      exportParams: {
        url: null,
        dictCodes: {},
        timeout: 15000,
        exportMode: 'front'
      },
      fileInfo: {
        fileModular: 'workFlow', // 文件所属模块 -》审批流程
        fileFunction: 'workflowReport', // 审批流相关文件
        fileType: 'images' // 文件所属类型
      },
      extraData: {
        fileModular: 'base',
        fileFunction: this.componentName,
        fileType: 'excel'
      },
      queryParam: {},
      currentRows: [],
      globalUserId: null
    }
  },
  created () {
    this.globalUserId = this.$store.getters.userId
  },
  computed: {
  },
  watch: {
  },
  mounted () {
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQueryData()
    })
  },
  methods: {
    doLayout () {
      this.getQueryData()
    },
    getQueryData (params) {
      this.queryParam = params || this.queryParam
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    deleteHandle (id) {
      if (!this.operationFunction.deleteInfo) {
        this.$message.error('未配置删除方法')
        return
      }
      if (!id) {
        this.$message.error('未选择删除数据')
        return
      }
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.operationFunction.deleteInfo(id).then(res => {
          this.$message.success(res.message)
          this.getQueryData()
        })
      })
      .catch(() => {})
    },
    addHandle () {
      if (!this.componentConfig.add) {
        this.$message.error('未配置添加组件')
        return
      }
      this.mode = 'add'
      const tab = {
        component: this.componentConfig.add,
        ctrlHeight: true,
        params: {
          flag: this.mode
        },
        closable: true,
        title: this.$t(this.componentName + '.table.add'),
        name: this.componentName + 'Add' + new Date().getTime()
      }
      this.$emit('tab-add', tab)
    },
    editHandle (row, id) {
      if (!this.componentConfig.edit) {
        this.$message.error('未配置修改组件')
        return
      }
      this.mode = 'edit'
      const tab = {
        component: this.componentConfig.edit,
        ctrlHeight: true,
        params: {
          row,
          flag: this.mode
        },
        title: this.$t(this.componentName + '.table.edit') + ':' + id,
        name: this.componentName + 'Edit' + id
      }
      this.$emit('tab-add', tab)
    },
    viewHandle (row, id) {
      if (!this.componentConfig.view) {
        this.$message.error('未配置查看组件')
        return
      }
      this.mode = 'view'
      const tab = {
        component: this.componentConfig.view,
        params: {
          row,
          flag: this.mode,
          readOnly: true
        },
        title: this.$t(this.componentName + '.table.view') + ':' + id,
        name: this.componentName + 'View' + id
      }
      this.$emit('tab-add', tab)
    },
    extendHandle (row, id, mode) {
      if (!this.componentConfig.extend) {
        this.$message.error('未配置查看组件')
        return
      }
      if (!mode) {
        this.$message.error('操作模式未配置')
        return
      }
      this.mode = mode
      const tab = {
        component: this.componentConfig.extend,
        params: {
          row,
          flag: this.mode,
          readOnly: true
        },
        title: this.$t(this.componentName + '.table.extend') + ':' + id,
        name: this.componentName + 'Extend' + id
      }
      this.$emit('tab-add', tab)
    },
    handleCurrentChange (val) {
      this.currentRows = val
    },
    // 上传附件成功
    handleUploadSuccess (file, row, key) {
      const { id, name } = file
      row[key] = id.toString()
    },
    // 删除文件
    handleAttachmentRemove (row, key) {
      row[key] = ''
    }
  }
}
