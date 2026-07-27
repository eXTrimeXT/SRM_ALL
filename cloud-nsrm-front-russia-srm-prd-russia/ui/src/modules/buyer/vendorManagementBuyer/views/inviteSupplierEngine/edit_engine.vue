<!-- eslint-disable quotes -->
<script setup lang="ts">
import { i18nExpression, expression, defineSchemas, markRaw, changeFieldVisibleByDeps } from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import { usePageHelper } from "lib@/components/composables/usePageHelper"
import {
  requiredValidatorSegment
} from 'lib@/components/render-engine/schema-segments'
import { isMobile, isEmail } from 'lib@/utils/validate'

const { emitTabRemove, emitTabAdd, t, app } = usePageHelper()

const $validate = (values:any, $message:any) => {
  if (!isEmail(values.contactEmail)) {
    $message(t('vendorMod.emailFormatIsWrong'))
    return true
  }
  if (values.phoneNumber && !isMobile(values.phoneNumber)) {
    $message(t('vendorMod.phoneFormatIsWrong'))
    return true
  }
  return false
}

const scope = {
  emitTabRemove,
  app,
  emitTabAdd,
  $validate
}

const schema = defineSchemas({
  InviteVendor: {
    type: 'void',
    'x-component': 'FormContainer',
    'x-decorator': 'QueryEngine',
    'x-query-engine': {
      service: 'sup',
      actions: {
        read: {
          immediate: true,
          ready: expression(`() => {
            $form.readPretty = $readOnly
            let id = $attrs.params.row.inviteVendorId
            $form.values.inviteVendorId = id
            return !!id
          }`),
          transformRequest: expression(`(data,headers) => {
            data.payload = [$form.values.inviteVendorId]
          }`),
          onSuccess: expression(`(res) => {
            console.log('res::',res)
            const value = res.data[0]
            $form.setValues({
              ...value
            })
          }`)
        }
      }
    },
    items: {
      type: 'object',
      properties: {
        goBack: {
          type: 'void',
          'x-content': expression(`$t($readOnly ? 'common.backTo' : 'components.common.cancel')`),
          'x-component': 'Button',
          'x-component-props': {
            type: 'default',
            '@click': expression(`async (values) => {
              if($readOnly){
                emitTabRemove($attrs.tabName)
                return
              }
              app.$confirm('此次修改并未保存是否取消', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
              }).then(() => {
                emitTabRemove($attrs.tabName)
              }).catch(() => {
              });
            }`)
          }
        },
        save: {
          type: 'void',
          'x-content': i18nExpression('common.staging'),
          'x-component': 'Button',
          'x-reactions': changeFieldVisibleByDeps(
            ['.inviteStatus'],
            `!$readOnly`
          ),
          'x-component-props': {
            type: 'default',
            plain: 'plain',
           '@submit': expression(`async (values) => {
              if($validate(values,$message)) return
              $queryEngine.request.save({
                ...values,
                inviteStatus: values.inviteStatus || 'DRAFT',
              }).then(() => {
                $message.success($t('common.successSave'))
                $bus.$emit('InviteVendor')
                emitTabRemove($attrs.tabName)
              })
           }`)
          }
        },
        publish: {
          type: 'void',
          'x-content': i18nExpression('common.publish'),
          'x-component': 'Button',
          'x-reactions': changeFieldVisibleByDeps(
            ['.inviteStatus'],
            `!$readOnly`
          ),
          'x-component-props': {
            '@submit': expression(`async (values) => {
              if($validate(values,$message)) return
              $queryEngine.request.save({
                ...values,
                inviteStatus: 'PUBLISH'
              },{
                customizeAction:'publish'
              }).then(() => {
                $message.success($t('common.successSave'))
                $bus.$emit('InviteVendor')
                emitTabRemove($attrs.tabName)
              })
           }`)
          }
        }
      }
    },
    properties: {
      collapse: {
        type: 'void',
        'x-component': 'Collapse',
        'x-component-props': {
          defaultOpenPanelCount: 1
        },
        properties: {
          // 单据信息
          receiptInfo: {
            type: 'void',
            'x-query-engine-skip': true,
            'x-component': 'CollapseItem',
            'x-component-props': {
              title: i18nExpression('vendorMod.receiptInfo')
            },
            properties: {
              orderInfo: {
                type: 'void',
                'x-decorator': 'FormLayout',
                'x-decorator-props': {
                   layout: 'vertical'
                },
                'x-component': 'FormGrid',
                'x-component-props': {
                    maxColumns: 4,
                    columnGap: 32,
                    rowGap: 0
                },
                properties: {
                  inviteVendorNo: {
                    type: 'string',
                    'x-decorator': 'FormItem',
                    title: i18nExpression('vendorMod.inviteVendorNo'),
                    'x-component-props': {
                      'disabled': true
                    }
                  },
                  inviteReason: {
                    type: 'string',
                    'x-decorator': 'FormItem',
                    title: i18nExpression('vendorMod.inviteReason'),
                    'x-component-props': {
                      disabled: `{{$readOnly}}`
                    },
                    ...requiredValidatorSegment
                  },
                  remark: {
                    type: 'string',
                    'x-decorator': 'FormItem',
                    title: i18nExpression('vendorMod.remark'),
                    'x-component-props': {
                      disabled: `{{$readOnly}}`
                    }
                  }
                }
              }
            }
          },
          // 联系人信息
          contactInfo: {
            type: 'void',
            'x-query-engine-skip': true,
            'x-component': 'CollapseItem',
            'x-component-props': {
              title: i18nExpression('vendorMod.contactInfo')
            },
            properties: {
              orderInfo: {
                type: 'void',
                'x-decorator': 'FormLayout',
                'x-decorator-props': {
                   layout: 'vertical'
                },
                'x-component': 'FormGrid',
                'x-component-props': {
                    maxColumns: 4,
                    columnGap: 32,
                    rowGap: 0
                },
                properties: {
                  contactPerson: {
                    type: 'string',
                    'x-decorator': 'FormItem',
                    title: i18nExpression('vendorMod.contactPerson'),
                    'x-component-props': {
                      'disabled': `{{$readOnly}}`
                    },
                    ...requiredValidatorSegment
                  },
                  contactEmail: {
                    type: 'string',
                    'x-decorator': 'FormItem',
                    title: i18nExpression('vendorMod.contactEmail'),
                    'x-component-props': {
                      disabled: `{{$readOnly}}`
                    },
                    ...requiredValidatorSegment
                  },
                  phoneNumber: {
                    type: 'string',
                    'x-decorator': 'FormItem',
                    title: i18nExpression('vendorMod.mobilePhone'),
                    'x-component-props': {
                      disabled: `{{$readOnly}}`
                    }
                  }
                }
              }
            }
          },
          // 供应商信息
          vendorInfo: {
            type: 'void',
            'x-query-engine-skip': true,
            'x-component': 'CollapseItem',
            'x-component-props': {
              title: i18nExpression('vendorMod.vendorInfo')
            },
            properties: {
              orderInfo: {
                type: 'void',
                'x-decorator': 'FormLayout',
                'x-decorator-props': {
                   layout: 'vertical'
                },
                'x-component': 'FormGrid',
                'x-component-props': {
                    maxColumns: 4,
                    columnGap: 32,
                    rowGap: 0
                },
                properties: {
                  vendorName: {
                    type: 'string',
                    'x-decorator': 'FormItem',
                    title: i18nExpression('vendorMod.vendorName'),
                    'x-component-props': {
                      'disabled': `{{$readOnly}}`
                    },
                    ...requiredValidatorSegment
                  },
                  socialCreditCode: {
                    type: 'string',
                    'x-decorator': 'FormItem',
                    title: i18nExpression('vendorMod.socialCreditCode'),
                    'x-component-props': {
                      disabled: `{{$readOnly}}`
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
})

</script>

<template>
  <RenderEngine schemaKey="inviteSupplierDetail" :pageAttrs="$attrs" :schema="schema" :scope="scope" />
</template>

<style>
.el-button--primary.is-plain{
  border: 1px #0077ff solid;
}
</style>
