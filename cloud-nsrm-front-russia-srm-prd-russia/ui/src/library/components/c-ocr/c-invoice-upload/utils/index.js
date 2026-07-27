/*
 * @Author: linyk7 && linyk7@meicloud.com
 * @Date: 2023-05-19 15:15:27
 * @LastEditors: linyk7 linyk7@meicloud.com
 * @LastEditTime: 2023-06-07 17:56:46
 * @FilePath: \ui\src\library\components\c-ocr\c-invoice-upload\utils\index.js
 * @Description: 发票上传相关组件方法抽离
 * Copyright (c) 2023 by ${git_name_email}, All Rights Reserved.
 */

export default {
  methods: {
    /**
     * @description 校验文件格式
     * @params file文件
     * @return { status: 状态（error/ok）, data: file, msg: 失败提示 }
     */
    checkFileFormat (file) {
      let nameSplitList = file.name.split('.')
      if (!file.type.includes('image') && !file.type.includes('pdf') && nameSplitList[nameSplitList.length - 1] !== 'ofd') {
        return {
          status: 'error',
          msg: `${file.name}: 上传失败,仅支持JPG、PNG、JPEG、PDF、OFD格式`
        }
      }

      if (file.size / 1024 / 1024 > 10) {
        return {
          status: 'error',
          msg: `${file.name}: 上传失败, ${this.$t('purSettlementMod.uploadTableMes2')}` // 上传文件大小不能超过 10MB!
        }
      }

      return {
        status: 'ok',
        data: file
      }
    },
    /**
     * @description 文件上传及校验
     * @params formData 上传表单参数
     * @params fileList 发票数组
     * @params loading 传入loading实例用于接口调用完关闭loading
     * @params event 原生input上传event
     */
    async handleUploadFile (formData, fileList, loading, event) {
      const { data = {} } = await this.$http({
        url: '/api-base/ocr/batchUploadAndVatInvoice', // ocr固定接口
        method: 'POST',
        data: formData
      }).catch(err => {
        console.warn(err, 'err')
        loading.close()
        loading = null // 重置loading
        event.target.value = ''
      })

      let enable = false
      let failItems = []
      if (data.data) {
        const result = data.data
        enable = data.data.enable
        // 获取成功数据
        if (result.successItems.length > 0) fileList.push.apply(fileList, result.successItems)
        // 获取识别失败数据
        if (result.failItems.length > 0) {
          failItems = result.failItems.map(item => {
            return {
              ...item.failItem,
              ...item.failMsgs[0]
            }
          })
          fileList.push.apply(fileList, failItems)
        }

        loading.close()
        loading = null // 重置loading
        event.target.value = ''
      }

      return {
        enable: enable,
        fileList: fileList.length > 0 ? fileList : [],
        failItems: failItems
      }
    },
    /**
     * @description 发票上传数组去重
     * @params arr 上传数组列表
     * @params field 过滤字段,条件字段为空不校验
     */
    uniqRepeatArr (arr, field) {
      const uniqueArr = arr.reduce((acc, cur) => {
        const index = acc.findIndex(item => {
          // 判断条件为空不做重复校验
          return item[field] && cur[field] && item[field] === cur[field]
        })
        if (index === -1) {
          acc.push(cur)
        }
        return acc
      }, [])

      return uniqueArr
    },
    /**
     * @description 校验重复，处理明细和弹窗文件
     * @params list 明细数据
     * @params fileList 文件上传数据
     * @params checkField 校验字段
     * @return hasRepeat 是否重复，fileList 上传文件列表
     */
    checkRepeat (list, fileList, checkField) {
      let hasRepeat = false
      const newArr = [...list, ...fileList]
      const uniqArr = this.uniqRepeatArr(newArr, checkField)
      const newFileList = this.uniqRepeatArr(fileList, checkField)

      // 如果融合数组或者弹窗上传数组和去重处理后数组长度不一样，则证明有重复
      if (newArr.length !== uniqArr.length || newFileList.length !== fileList.length) {
        hasRepeat = true
      }

      // 过滤下没有明细中的数据赋值给弹窗列表
      const ids = list.map(item => item[checkField])
      fileList = newFileList.filter(item => {
        return ids.indexOf(item[checkField]) === -1
      })

      return {
        hasRepeat,
        fileList
      }
    }
  }
}
