<script setup lang="ts">
/**
 * 当前项目的统一入口，做一些基础组件的注册
 * 如果是常用的基础组件，可以放到这里去做全局注册，如果是业务专属组件，可以通过 components 传入拓展
 * 如果是常用，但是却是大组件，那么可以通过异步加载来减少首屏加载时间
 */
import { computed, ref } from 'vue-demi'
// import { RenderEngine } from '@meicloud/industry-package'
import {
  FieldModel,
  FragmentComponent,
  QueryEngineBlockContainer,
  QueryEngineContainer,
  transformSchemas
} from '@meicloud/render-engine'
import { srmComponents } from '../srm-components'

import {
  RenderButtonList as ButtonList,
  Cascader,
  Checkbox,
  DatePicker,
  Editable,
  FormButtonGroup,
  FormGrid,
  FormItem,
  FormLayout,
  Input,
  InputNumber,
  Password,
  QueryForm,
  QueryFormByQueryEngine,
  Radio,
  RDialog,
  RenderButtonList,
  // 从行业包的 RenderEngine 切换到 render-pix 的
  RenderEngine,
  Select,
  Space,
  Submit,
  SubmitGroupQueryEngineBlock,
  SubmitQueryEngine,
  SubmitQueryEngineBlock,
  SubmitQueryEngineByArrayRow,
  ArrayItems,
  FormCollapse
} from '@meicloud/render-pix'
// 按需加载
import '@meicloud/render-pix/dist/esm/all-style'
// import { RenderTable, RenderTableIndex, RenderTableButtonList, RenderTableLink } from './components/table'
import {
  RenderTable,
  RenderTableButtonList,
  RenderTableByQueryEngine,
  RenderTableIndex,
  RenderTableLink
} from '@meicloud/render-table'

import { usePageHelper } from '../composables/usePageHelper'
import { useDict } from '../srm-components/dict'
import { bus, BusEvent } from './components/bus'
import { DynamicGrid } from './components/dynamic-grid'
import { FormContainer } from './components/form-container'
import { HTMLElement } from './components/html-element'
import { PageContainer } from './components/page-container'
import { RequestProvider } from './components/request'
import { SchemaWorkflow } from './components/schema-workflow'
import { useRenderEngineConfigProvider } from './context'
import { renderEngineProps } from './props'

// eslint-disable-next-line no-undef
const props = defineProps({
  ...RenderEngine.props,
  ...renderEngineProps
})

const components = {
  DynamicGrid,
  Fragment: FragmentComponent,
  QueryForm,
  QueryFormByQueryEngine,
  // RenderTable,
  RenderTable: RenderTableByQueryEngine, // 耦合 QueryEngine 的 RenderTable
  NormalRenderTable: RenderTable, // 标准 RenderTable
  RenderTableIndex,
  RenderTableButtonList,
  RenderTableLink,
  FormButtonGroup,
  SubmitGroupQueryEngineBlock,
  QueryEngine: QueryEngineContainer,
  QueryEngineBlock: QueryEngineBlockContainer,
  SubmitQueryEngine,
  SubmitQueryEngineBlock,
  SubmitQueryEngineByArrayRow,
  RDialog,
  FormContainer,
  PageContainer,
  RequestProvider,
  BusEvent,
  HTMLElement,
  SchemaWorkflow,
  RenderButtonList,

  FormItem,
  FormGrid,
  FormLayout,
  Input,
  InputNumber,
  DatePicker,
  Cascader,
  Select,
  Password,
  Space,
  Editable,
  ButtonList,
  Button: Submit,
  Radio,
  Checkbox,
  ArrayItems,
  FormCollapse,

  ...srmComponents,
  ...props.components
}

const { baseRequest } = useRenderEngineConfigProvider(props)

const patchesRef = ref({})

const readyRef = ref(!props.schemaKey)

const getDynamicFormSchemaData = () => {
  // TODO 内置
  baseRequest({
    type: 'DynamicFormSchemaData',
    action: 'read',
    service: 'cm',
    payload: [props.schemaKey]
    // @ts-ignore
  }).then(res => {
    // @ts-ignore
    if (!res.data?.ref?.DynamicFormSchemaData) {
      return
    }

    // @ts-ignore
    const patches = JSON.parse(res.data.ref.DynamicFormSchemaData[props.schemaKey].schemaData)

    patchesRef.value = patches
  }).finally(() => {
    readyRef.value = true
  })
}

if (props.schemaKey) {
  getDynamicFormSchemaData()
}

const theTransformSchemaDefinitions = computed(
  () => props.schemaDefinitions && transformSchemas({ schemas: props.schemaDefinitions }),
)
// @ts-ignore
const theTransformSchema = computed(() => transformSchemas({ schemas: props.schema, patches: patchesRef.value }))

const { app, confirmDeleteMessage, confirmMessage, emitTabAdd, emitTabRemove, t, createdUserIsCurrentUserByRow, buyer, vendor, authorityVisible } = usePageHelper()
const attrs = props.pageAttrs

// 判断是否只读状态
const readOnly = props.readOnly ?? (
  ['view', 'read'].includes(attrs?.params?.flag) || !!attrs?.params?.isReadOnly
)

const dict = useDict()

const innerScope = computed(() => ({
  // TODO 内置到底层
  $getFieldParentFieldFormPath: function (field: FieldModel, num = 1) {
    return field.address.slice(0, field.address.length - num)
  },

  // @ts-ignore
  $t: (key, options) => t(key, options),
  $route: app.$route,
  $router: app.$router,
  $attrs: attrs,
  // @ts-ignore
  $store: app.$store,

  // 页面专属，后边看是否需要剥离出去内置
  // @ts-ignore
  $http: app.$http,
  $api: app.$api,
  $message: app.$message,
  $confirm: confirmMessage,
  $prompt: app.$prompt,
  $confirmDeleteMessage: confirmDeleteMessage,
  $emitTabAdd: emitTabAdd,
  $emitTabRemove: emitTabRemove,
  $bus: bus,
  $dict: dict,
  $authorityVisible: authorityVisible,
  // 业务专属
  $buyer: buyer,
  $vendor: vendor,
  $createdUserIsCurrentUserByRow: createdUserIsCurrentUserByRow,
  $readOnly: readOnly,

  ...props.scope
}))

const innerLayoutProps = computed(() => ({
  colon: true,
  style: 'height: 100%',
  layout: 'vertical',
  feedbackLayout: 'terse',
  gridRowGap: 0,
  // scrollToFirstError: true,
  ...props.layoutProps
}))

const className = computed(() => ({
  'render-engine': true,
  'page-read-pretty': readOnly,
  'detail-page-read-pretty': readOnly
}))
</script>

<template>
  <RenderEngine
    :layoutProps="innerLayoutProps"
    :previewTextPlaceholder="''"
    :class="className"
    :components="components"
    :schema="theTransformSchema"
    :schemaDefinitions="theTransformSchemaDefinitions"
    :viewModelProps="viewModelProps"
    :scope="innerScope"
    :schemaKey="schemaKey"
    :useGlobalDialog="useGlobalDialog"
    :events="events"
  />
</template>

<style lang="scss">
// unocss
:root {
  --mb-md: 16px;
  --h-md: 28px;
  --form-item-bottom: 20px;
}

.render-engine {
  height: 100%;
}

.patches .render-pix-form-item-label label {
  color: red;
}

.el-button.patches {
  color: red !important;
}
.render-pix-form-item-label-content>label {
  text-align: right;
  float: none!important;
  display: inline-block!important;
  box-sizing: border-box;
  overflow: hidden;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

// 渲染引擎表单样式
// 单据维护区域弹性区域
.render-pix-form-grid{
  .render-pix-form-item{
    // 上下结构
    &.render-pix-form-item-layout-vertical{
      // 上下结构的form-item
      .render-pix-form-item-label{
        line-height: 22px;
        height: 22px;
        .render-pix-form-item-label-tooltip{
          height: 22px;
          line-height: 22px;
        }
        .render-pix-form-item-label-content{
          vertical-align: middle;
          align-items: center !important;
          // 星号
          .render-pix-form-item-asterisk{
            display: inline-block;
            vertical-align: top;
            line-height: 22px;
          }
        }
        * {
          line-height: 22px;
          min-height: 22px;
          height: 22px;
        }
      }
    }
    // 水平结构 行高和输入框一样
    &.render-pix-form-item-layout-horizontal{
      .render-pix-form-item-label{
        line-height: 28px;
        height: 28px;
        // * {
        //   line-height: 28px;
        //   min-height: 28px;
        // }
      }
    }
  }
}
.render-pix-form-default {
  .render-pix-form-grid {
    gap: 0 32px !important;
  }
  // 公共label颜色
  .render-pix-form-item-label label{
    color: #5B6065;
  }
  // 自读颜色行高
  .render-pix-form-item-control .render-pix-form-item-control-content .render-pix-form-item-control-content-component {
    width: 100%;
    min-height: 20px;
    line-height: 20px;
    color: #000;
  }
  &.render-pix-form-vertical{
    // 列表页查询区域表单高度28
    .query-form__wrap{
      .render-pix-form-grid{
        .render-pix-form-item{
          &.render-pix-form-item-layout-horizontal{
            .render-pix-form-item-label{
              .render-pix-form-item-label-content {
                height: 28px;
                align-items: center !important;
                label{
                  line-height: 14px!important;
                  white-space: normal;
                  vertical-align: text-bottom;
                  max-height: 28px;
                  display: inline-block !important;
                  overflow: hidden !important;
                  text-overflow: ellipsis !important;
                  -webkit-box-orient: vertical !important;
                  -webkit-line-clamp: 2 !important;
                  min-height: 0;
                  height: auto;
                  color: #161C24 !important;
                }
              }
            }
          }
        }
      }
      .query-form__toolbar >button{
        color: #51555B !important;
      }
    }

    // 页面查看状态
    .detail-page-read-pretty {
      .render-pix-form-item-label * {
        min-height: 22px !important;
        line-height: 22px !important;
        height: 22px !important;
      }
      .render-pix-form-item-asterisk {
        top: -6px;
        left: 0px;
        position: relative;
      }
      .render-pix-form-item-feedback-layout-terse {
        margin-bottom: 20px !important;
      }
      .render-pix-form-item-control .render-pix-form-item-control-content .render-pix-form-item-control-content-component {
        padding-top: 4px;
      }
      .vxe-table .render-pix-form-item-control .render-pix-form-item-control-content .render-pix-form-item-control-content-component {
        padding-top: 0;
      }
    }
    // 只读状态详情表单调整
    .page-read-pretty {
      .render-pix-form-item-feedback-layout-terse {
        margin-bottom: 16px;
      }
      .render-pix-form-item-label {
        line-height: 22px !important;
        height: 22px !important;
      }
      .render-pix-form-item-label-content {
        height: 20px !important;
        line-height: 20px !important;
      }
      .render-pix-form-item-label * {
        color: red !important;
        line-height: 20px !important;
        height: 20px !important;
      }

      .render-pix-form-item-control .render-pix-form-item-control-content .render-pix-form-item-control-content-component {
        color: blue !important;
        height: 20px !important;
        min-height: 20px !important;
        line-height: 20px !important;
        font-weight: 500;
      }
      .render-pix-form-default .render-pix-form-item-feedback-layout-loose {
        margin-bottom: 16px !important;
      }

    }
  }

  // 单据页label高度20
  // .render-pix-form-item-label-content {
  //   height: 20px;
  //   min-height: 0;
  // }

  .render-pix-form-item-feedback-layout-loose {
    margin-bottom: var(--mb-md);
  }

  .render-pix-form-item-feedback-layout-terse {
    margin-bottom: var(--form-item-bottom);
  }

  .render-pix-form-item-feedback-layout-terse.render-pix-form-item-feedback-has-text:not(.render-pix-form-item-inset) {
    margin-bottom: var(--form-item-bottom);
  }
  .render-pix-form-item-control {
    position: relative;
  }

  .render-pix-form-item-error-help {
    position: absolute;
    top: 100%;
    left: 0;
  }

  .list-form__toolbar {
    margin-bottom: var(--mb-md);

    .el-button + .base-import, .base-import + .export-excel, .export-excel + .el-button {
      margin-left: 0;
    }
  }

}

.render-pix-form-item-control {
  word-break: break-all !important;
}

// TODO 这段样式后边是用去掉的
.el-popover {
  .render-pix-form-item-error-help {
    padding: 4px 10px 4px 10px;
    background-color: rgba(0, 0, 0, .75) !important;
    color: #fff !important;
    border-radius: 4px !important;
  }
}

.el-select .el-input.is-disabled .el-input__inner {
  border-color: #DCDDDE !important;
}

.vxe-table--body .render-pix-space.render-pix-form-button-group .render-pix-space-item .el-button + .el-button {
  margin-left: 0 !important;
}
.button-list {
  min-height: 16px;
}
.query-form__toolbar {
  .button-list {
    height: fit-content;
  }
}

.button-list__popconfirm {
  min-width: 212px;

  .el-popconfirm__main {
    font-size: 14px;

    .el-popconfirm__icon {
      margin-right: 4px;
    }
  }

  .el-popconfirm__action {
    .el-button--mini{
      border-width: 1px;
      padding: 6px 15px;
    }
  }
}

.vxe-cell input:focus {
  // outline: none !important;
  box-shadow: 0 0!important;
}
</style>
