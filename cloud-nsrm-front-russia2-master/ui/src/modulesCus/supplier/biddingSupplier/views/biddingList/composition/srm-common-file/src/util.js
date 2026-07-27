import { isNumber, isInteger, isPlainObject } from 'lodash'
import { FILE_DOWNLOAD, FILE_UPLOAD } from '@/api/common'
import { getToken, getEntranceType } from '@/utils/auth'
import { sysPrefix } from '@/config/ipConfig'
import { uploadConfig, systemUrl } from '@/config/sysConfig'
import Secret from '@/utils/secret'
import pdfPlaceholder from '@/assets/img/pdf-placeholder.png'

// 组件类型 默认 下载 表单 表格
export const COMPONENT_TYPE = {
  DEFAULT: 'default',
  LINK: 'link',
  FORM_ITEM: 'form-item',
  TABLE_COLUMN: 'table-column'
}

// 列表样式 文本 图片卡片
export const LIST_TYPE = {
  TEXT: 'text',
  PICTURE_CARD: 'picture-card'
  // PICTURE: 'picture'
}

// 组件使用场景，便于做精细化区分
export const SCENE_TYPE = {
  // 默认
  DEFAULT: 'DEFAULT',
  // 动态场景附件
  SCENE_FILE: 'SCENE_FILE'
}

/**
 * 判断文件后缀是否支持文件预览功能 大小写不敏感
 * @param name {string} 文件名
 * @returns {boolean}
 */
export const fileHasPreview = name => {
  return name && !!uploadConfig.accept.preview.find(item => name.toUpperCase().endsWith(item.toUpperCase()))
}

// 通用入参
export const propsMixin = {
  props: {
    // 组件类型 COMPONENT_TYPE
    type: {
      type: String,
      default: COMPONENT_TYPE.DEFAULT,
      validator: key => {
        let keyVal = key.replace(/-/g, '_') // - 替换成 _
        return COMPONENT_TYPE[keyVal.toUpperCase()]
      }
    },
    // 使用场景类型 默认 DEFAULT
    sceneType: {
      type: String,
      default: SCENE_TYPE.DEFAULT,
      validator: key => !!SCENE_TYPE[key]
    },
    /**
     * 文件字段映射，用于文件列表、表格、表单组件类型中
     * 默认文件ID为fileId，文件名称为fileName
     * 字符串类型是给场景附件用的
     */
    fileKeyOptions: {
      type: [Object, String],
      validator: value => {
        return (value && typeof value === 'string') || !(
          !value ||
          typeof value !== 'object' ||
          Object.keys(value).length !== 2 ||
          !value.idKey ||
          !value.nameKey ||
          typeof value.idKey !== 'string' ||
          typeof value.nameKey !== 'string'
        )
      },
      default: () => {
        return {
          idKey: 'fileId',
          nameKey: 'fileName'
        }
      }
    },
    // 是否只读，只读不能重新上传
    readonly: {
      type: Boolean,
      default: false
    },
    // 是否禁用，禁用后只能看到文件名，不能用disabled，会跟跟多乱七八糟的组件入参冲突
    fileDisabled: {
      type: Boolean,
      default: false
    },
    // 上传说明提示 不为空就显示
    uploadTips: {
      type: String,
      default: ''
    }
  }
}

/**
 * 上传组件入参
 * @type {{props: {buttonOptions: {default: (function(): {}), type: ObjectConstructor}, headers: {default: (function(): {Authorization: string, contentType: string}), type: ObjectConstructor}, extraData: {type: ObjectConstructor, required: boolean}, actionUrl: {default: string, type: StringConstructor}, multiple: {default: boolean, type: BooleanConstructor}, timeout: {default: number, type: NumberConstructor}, defaultFile: {type: ObjectConstructor, required: boolean}, limit: {default: number, validator: (function(*)), type: NumberConstructor}, validateOptions: {default: (function(): {}), validator: ((function(*): (boolean|*))|*), type: ObjectConstructor}, progress: {default: boolean, type: BooleanConstructor}, drag: {default: boolean, type: BooleanConstructor}, draggerOptions: {default: (function(): {}), type: ObjectConstructor}, fileList: {default: (function(): *[]), type: ArrayConstructor}}}}
 */
export const uploadPropsMixin = {
  props: {
    /**
     * 上传按钮配置
     * @param {string} title - 按钮文本 上传附件
     * @param {string} type - 按钮类型
     * @param {string} icon - 按钮图标，支持element icon
     * @param {boolean} disabled - 是否禁用
     */
    buttonOptions: {
      type: Object,
      default: () => {
        return {
          // title: '上传附件',
          // type: 'default',
          // icon: 'el-icon-upload',
          // disabled: false
        }
      }
    },
    /**
     * 拖拽上传触发组件配置，
     * @param {string} width - 上传组件宽
     * @param {string} height - 上传组件高
     * @param {string} title - 标题
     * @param {string} icon - 按钮图标，支持element icon
     * @param {string} tips - 提示文本
     */
    draggerOptions: {
      type: Object,
      default: () => {
        return {
          // width: '200px',
          // height: '200px',
          // title: '上传附件',
          // icon: 'el-icon-upload',
          // tips: '点击或拖拽文件到此区域以上传'
        }
      }
    },
    /**
     * 上传前文件校验配置
     * @param {string[]} accept 文件类型只判断文件后缀 ['.doc', '.xlsx']
     * @param {Number} size 文件大小 单位KB 与MB换算 KB / 1024 = MB
     */
    validateOptions: {
      type: Object,
      validator: value => {
        if (!value || typeof value !== 'object') {
          return false
        }
        if (value.accept && !Array.isArray(value.accept)) {
          return false
        }
        return !(value.size && !isNumber(value.size))
      },
      default: () => {
        return {
          // 默认1.5G (1024 + 1024 / 2) * 1024 = 1572864
          // size: 1572864
        }
      }
    },
    // 上传超时 默认35000毫秒
    timeout: {
      type: Number,
      default: 350000
    },
    // 是否显示文件上传过程
    progress: {
      type: Boolean,
      default: true
    },
    // 文件上传附带参数
    extraData: {
      type: Object,
      required: false
    },
    // 上传头
    headers: {
      type: Object,
      default: () => {
        return {
          Authorization: `Bearer ${getToken()}`,
          contentType: 'form-data'
        }
      }
    },
    // 上传API路径
    actionUrl: {
      type: String,
      default: sysPrefix() + FILE_UPLOAD
    },
    // 是否支持上次时多选文件，当支持多选时，最大上传数要大于1
    multiple: {
      type: Boolean,
      default: false
    },
    // 多张图片展示
    multiplePicture: {
      type: Boolean,
      default: false
    },
    // 文件大小提示是否以MB提示，默认true, 如果上传图片小于1M，需要提示大小的需要传这个参数为false
    fileSizeTipWithMb: {
      type: Boolean,
      default: true
    },
    // 最大允许上传个数
    limit: {
      type: [Number, String],
      default: 1,
      validator: value => isInteger(Number(value)) && Number(value) > 0
    },
    // 是否拖拽上传
    drag: {
      type: Boolean,
      default: false
    },
    // 默认文件，只上传一个时使用，比fileList优先级高 { fileId, fileName }
    defaultFile: {
      type: Object,
      required: false
    },
    /**
     * 文件列表，支持上传多个时传
     * Array<{ [fileKeyOptions.idKey]: '', [fileKeyOptions.nameKey]: '' }>
     */
    fileList: {
      type: [Array, Object],
      default: () => []
    },
    // 多选时，附件列表绑定的数组key，如果配置了，fileList需要配置为行对象
    fileListKey: {
      type: String,
      default: ''
    }
  }
}

// 表单入参
export const formItemPropsMixin = {
  props: {
    /**
     * 表单组件配置
     * @param {string} label -  标签文本
     * @param {string} prop - 表单域 model 字段，用于表单校验，如已配置fileKeyOptions
     * @param {boolean} showUploadStatus - 是否展示上传状态
     */
    formItemOptions: {
      type: Object,
      default: () => {
        return {
          // label: '附件上传',
          // prop: 'fileId',
          // showUploadStatus: false
        }
      }
    }
  }
}

// 表格入参
export const tableColumnPropsMixin = {
  props: {
    /**
     * 表格组件配置，下列是默认配置，同时支持所有el-table-column入参
     * @param {string} label - 列文本
     * @param {string} prop - 对应列内容的字段名
     * @param {string} nameProp - 对应列内容的字段名 - 文件名
     * @param {string} minWidth - 对应列的最小宽度
     * @param {string} align - 对齐方式
     */
    tableColumnOptions: {
      type: Object,
      default: () => {
        return {
          // label: '附件上传',
          // prop: 'fileId',
          // nameProp: 'fileName',
          // minWidth: '150',
          // align: 'center'
        }
      }
    }
  }
}

// 文件列表入参
export const listPropsMixin = {
  props: {
    // 列表样式
    listType: {
      type: String,
      default: LIST_TYPE.TEXT,
      validator: key => {
        let keyVal = key.replace(/-/g, '_') // - 替换成 _
        return LIST_TYPE[keyVal.toUpperCase()]
      }
    },
    /**
     * 图片样式入参。支持自定义宽高，如果存在拖拽上传组件，那么将忽略该配置以拖拽上传组件为准。
     * @param {string} width - 图片容器宽
     * @param {string} height - 图片容器高
     */
    pictureStyleOptions: {
      type: Object,
      default: () => {
        return {
          width: '200px',
          height: '200px'
        }
      }
    },
    // 文件列表
    fileList: {
      type: [Array, Object],
      default: () => []
    },
    fileListKey: {
      type: String,
      default: ''
    },
    // 默认文件，跟fileList二选一，当存在defaultFile时忽略fileList { fileId, fileName }
    defaultFile: {
      type: Object,
      required: false
    },
    // 空附件提示，存在提示才会显示，否则显示空
    emptyText: {
      type: String,
      default: ''
    },
    // 是否需要在线预览，默认true
    filePreview: {
      type: Boolean,
      default: true
    },
    // 在线预览入参
    filePreviewOptions: {
      type: Object,
      required: false
    }
  }
}

/**
 * 计算文件名的字符串长度，中文会计算两个长度
 * @param fileName
 * @returns {number|*}
 */
export const countFileNameLength = fileName => {
  if (!fileName) {
    return 0
  }
  // 转字符串
  fileName += ''
  // eslint-disable-next-line no-control-regex
  // return fileName.replace(/[^\x00-\xff]/g, '01').length
  return fileName.length
}

/**
 * 根据文件id 返回img url
 * @param fileId
 * @returns {*}
 */
export const getImgSrc = (fileId, fileName) => {
  // fileId
  const fileKey = Secret.getValue(fileId)
  // 获取登录类型 // inside 内部登录方式 || singlePoint 单点登录方式
  const entrance = getEntranceType()
  // pdf 类型的返回pdf占位图
  let fileNameArr = fileName ? fileName.split('.') : []
  let fileType = fileNameArr[fileNameArr.length - 1]
  if (fileType == 'pdf') {
    return pdfPlaceholder
  }
  // iam 单点不需要传 access_token
  return `${systemUrl}${sysPrefix()}${FILE_DOWNLOAD}?fileKey=${fileKey}` +
      `${entrance === 'singlePoint' ? '' : `&access_token=${getToken()}`}`
}

/**
 * 转化fileKeyOptions配置，因为场景附件的参数是字符串
 * @param val
 * @returns {string|{default: function(): {idKey: string, nameKey: string}, validator: function(*): *, type: [ObjectConstructor,StringConstructor]}|any}
 */
export const fileKeyOptionsConversion = val => {
  if (!val) {
    // 返回默认参数
    return propsMixin.props.fileKeyOptions.default()
  }
  if (typeof val === 'string') {
    try {
      return JSON.parse(val)
    } catch (e) {
      return val
    }
  }
  return val
}

// 处理转化fileListKey作为属性key 值
export const fileListAttrsKeyConversion = (value, fileListKey) => {
  if (isPlainObject(value) && fileListKey) {
    // 是对象，并且配置了list key，从对象里找
    return (value[fileListKey] || [])
  }

  return value
}
