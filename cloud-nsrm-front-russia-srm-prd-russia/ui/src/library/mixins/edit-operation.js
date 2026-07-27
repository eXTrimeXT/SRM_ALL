export default {
  components: {
  },
  data () {
    return {
      apiConfig: {
        updateApi: null,
        addApi: null,
        updateTempApi: null,
        addTempApi: null
      },
      configInfo: {
        listName: null
      },
      globalUserId: null
    }
  },
  created () {
    this.globalUserId = this.$store.getters.userId
  },
  computed: {
    isBuyer () {
      return this.$store.getters.userInfo.userType === 'BUYER'
    }
  },
  watch: {
  },
  mounted () {
  },
  methods: {
    updateInfo () {
      if (!this.apiConfig.updateApi) {
        this.$message.error('未配置更新方法')
        return
      }
      if (!this.configInfo.listName) {
        this.$message.error('未配置列表名称')
        return
      }

      this.$refs.form.validate(result => {
        if (result) {
          this.apiConfig.updateApi(this.form).then(res => {
            this.$message({
              type: 'success',
              message: res.message
            })
            this.cancelBill()
          })
        }
      })
    },
    addInfo () {
      if (!this.apiConfig.updateApi) {
        this.$message.error('未配置添加方法')
        return
      }
      if (!this.configInfo.listName) {
        this.$message.error('未配置列表名称')
        return
      }

      this.$refs.form.validate(result => {
        if (result) {
          this.apiConfig.addApi(this.form).then(res => {
            this.$message({
              type: 'success',
              message: res.message
            })
            this.cancelBill()
          }).catch((resData) => {
            let numReg = /^[0-9]*$/
            let numRe = new RegExp(numReg)
            if (numRe.test(resData.code)) {
              this.getDetail(resData.code)
            }
          })
        }
      })
    },
    updateTempInfo () {
      if (!this.apiConfig.updateTempApi) {
        this.$message.error('未配置更新方法')
        return
      }
      if (!this.configInfo.listName) {
        this.$message.error('未配置列表名称')
        return
      }

      this.$refs.form.validate(result => {
        if (result) {
          this.apiConfig.updateTempApi(this.form).then(res => {
            this.$message({
              type: 'success',
              message: res.message
            })
            this.getDetail(res.data)
          })
        }
      })
    },
    addTempInfo () {
      if (!this.apiConfig.addTempApi) {
        this.$message.error('未配置添加方法')
        return
      }
      if (!this.configInfo.listName) {
        this.$message.error('未配置列表名称')
        return
      }

      this.$refs.form.validate(result => {
        if (result) {
          this.apiConfig.addTempApi(this.form).then(res => {
            this.$message({
              type: 'success',
              message: res.message
            })
            this.getDetail(res.data)
          })
        }
      })
    },
    cancelBill () {
      if (!this.configInfo.listName) {
        this.$message.error('未配置列表名称')
        return
      }
      this.$emit('tab-remove', this.$attrs.tabName)
      this.__setTabTodo(this.configInfo.listName + '.getQueryData')
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
