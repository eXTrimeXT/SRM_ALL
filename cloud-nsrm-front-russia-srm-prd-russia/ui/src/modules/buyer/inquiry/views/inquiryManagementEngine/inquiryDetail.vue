<script setup lang="ts">
import $dayjs from 'dayjs'
import { useAttrs } from 'vue'
import { RenderEngine } from 'lib@/components/render-engine'
import {
  defineSchemas,
  i18nExpression,
  generateCharFunctionExpression,
  generateCharExpressionByFunction
} from '@meicloud/render-engine'
import { FormTab } from '@meicloud/render-pix'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import {
  SOU_SCORE_RULE_TYPE_ENUM,
  SOU_ORDER_TYPE_ENUM,
  SOU_PUBLISH_SCOPE_ENUM,
  SOU_TYPE_ENUM
} from 'lib@/compositionEngine/sourcing/enum'
import { AttrsParams } from 'lib@/compositionEngine/sourcing/types'
import { scoreRuleHttp } from 'modb@/inquiry/apiEngine'
import { getRateByCode } from 'lib@/compositionEngine/sourcing/quoteCurrency'
import { saveQuoteOrLadderPrice } from 'lib@/compositionEngine/inquiry/ladderPrice'
// @ts-ignore
import { sceneFileCompApi } from '@/api/fileApi'
import ProjectInfoSegment from './inquiryDetail/projectInfo'
import RequireInfoSegment, { requireInfo } from './inquiryDetail/requireInfo'
import InviteVendorSegment from './inquiryDetail/inviteVendor'
import ScoreRuleSegment from './inquiryDetail/scoreRule'

const attrs: any = useAttrs()
const attrsParams: AttrsParams = attrs.params
const { app, emitTabRemove: $emitTabRemove, getCurrentUserInfo, t: $t } = usePageHelper()

const $message: any = app.$message

/**
 * 获取当前tab页值
 * @param $form
 */
const $getEditableTabsValue = ($form: any): string => {
  return $form.query('inquiryDetailTab').take().componentProps.activeKey
}

/**
 * 步骤切换
 * @param $form
 * @param step
 */
const $stepTabSwitch = ($form: any, step: number) => {
  const panes = ['projectInfo', 'requireInfo', 'inviteVendor', 'scoreRule']
  // 当前位置
  const index = panes.findIndex(item => item === $getEditableTabsValue($form))
  $form.query('inquiryDetailTab').take().setComponentProps({ activeKey: panes[index + step] })
}

/**
 * 公共保存接口
 * @param $form
 * @param $queryEngine
 * @param $bus
 */
const $saveOrSubmitInq = async ($form: any, $queryEngine: any, $bus: any) => {
  // 改造当前方法为公共保存接口
  // 1. 获得当前步骤页面
  const tab = $getEditableTabsValue($form)
  const takeObj = $form.query(tab).take()
  return new Promise(async resolve => {
    // 2. 表单校验的前置条件
    // 邀请供应商
    if (tab === 'inviteVendor') {
      if ($form.values?.publishScope === SOU_PUBLISH_SCOPE_ENUM.OPEN_TENDER) {
        // 公开招标，不用保存和校验直接跳过
        resolve({ status: true })
        return
      }
    }

    // 3. 表单校验
    $form.validate(takeObj ? takeObj.address.concat('*') : '')
      .then(async () => {
        // 4. 根据不同页面组装请求参数
        // 剔除不需要的参数
        let {
          innerFile = [],
          outerFile = [],
          currencyList = [],
          itemFiles,
          authList,
          vendorList,
          ...formValues
        } = $form.values

        console.log(tab, 'tab')
        // 处理邀请供应商数据
        if (tab === 'inviteVendor') {
          let vendors: any = {}
          vendorList.forEach((item: any) => {
            vendors[item.vendorId] = item
          })

          const mapVendor = new Map(Object.entries(vendors))

          authList.forEach((obj: any) => {
            Object.keys(obj).forEach(key => {
              const item: any = mapVendor.get(key)
              if (item) {
                item.authList.forEach((last: any) => {
                  last.forbidPrice = obj[key]
                })
              }
            })
          })
        }

        let payload = {
          ...formValues,
          vendorList,
          createStep: tab,
          souType: 'inq',
          sequenceCode: 'SEQ_SOU_INQ_NO'
        }
        if (tab === 'projectInfo') {
          // 询价信息
          payload = {
            ...payload,
            currencyList: currencyList,
            fileList: [].concat(innerFile).concat(outerFile)
          }
        }

        // 5. 发起请求
        const response = await $queryEngine.request.save(
          payload,
          {
            customizeAction: 'editInitInfo',
            loading: true,
            query: {
              '*': {}
            }
          }
        )
        console.log(response, 'responese')

        // 6. 请求成功后的处理
        if (response) {
          if (tab === 'scoreRule') {
            // FIXME 目前暂时不对接审批流，评分规则页提交，关闭页面，刷新列表页
            $message.success($t('common.successSubmit'))
            $bus.$emit('paginationRefresh')
            $emitTabRemove(attrsParams.tabName)
            return
          }
          $message.success($t('common.successSave'))
          // 是否有必要刷新？
          $queryEngine.request.read()
          $form.values.projectId = response.data[0].projectId
          resolve({ status: true })
        } else {
          resolve({ status: false })
        }
      })
      .catch(() => {
        resolve({ status: false })
      })
  })
}

/**
 * 下一步
 * @param $form
 * @param $queryEngine
 * @param $bus
 */
const $nextStep = async ($form: any, $queryEngine: any, $bus: any) => {
  // 1. 分发保存
  const saveResult: any = await $saveOrSubmitInq($form, $queryEngine, $bus)
  // 2. 判断是否成功，成功就下一步
  if (saveResult.status) {
    $stepTabSwitch($form, 1)
  }
}

// 注入作用域
const scope = {
  $attrsParams: attrsParams,
  $projectId: attrsParams.flag === 'add' ? '' : attrsParams.row.projectId,
  $readonly: !!attrsParams.readonly,
  $dayjs,
  $emitTabRemove,
  $getCurrentUserInfo: getCurrentUserInfo,
  // 公共代码块注入的方法
  $getRateByCode: getRateByCode,
  $saveQuoteOrLadderPrice: saveQuoteOrLadderPrice,
  // 字典枚举
  $enum: {
    SOU_SCORE_RULE_TYPE_ENUM,
    SOU_ORDER_TYPE_ENUM,
    SOU_PUBLISH_SCOPE_ENUM,
    SOU_TYPE_ENUM
  },
  // http api
  $scoreRuleHttp: scoreRuleHttp,
  $sceneFileCompApi: sceneFileCompApi,
  $getEditableTabsValue,
  $stepTabSwitch,
  $saveOrSubmitInq,
  $nextStep,
  ...requireInfo
}

const components = { FormTab }

const schema = defineSchemas({
  InqSouProjectForBuyer: {
    type: 'void',
    'x-component': 'FormContainer',
    'x-decorator': 'QueryEngine',
    'x-query-engine': {
      service: 'sou',
      actions: {
        read: {
          action: 'getInitInfo',
          immediate: true,
          tree: true,
          autoRelationTableMappingConversion: false,
          ready: generateCharFunctionExpression(({ $form, $readonly, $attrsParams }) => {
            $form.readPretty = $readonly
            $form.values.projectId = $attrsParams?.row?.projectId
            return !!$form.values.projectId
          }),
          transformRequest: generateCharFunctionExpression(({ $form, $attrsParams }, data) => {
            data.payload = [{
              projectId: $attrsParams?.row?.projectId || $form.values.projectId,
              souType: 'inq'
            }]
            data.tree = true
            data.query = {
              '*': {}
            }

            return data
          }),
          onSuccess: generateCharFunctionExpression(({ $form }, response) => {
            const dealData = response.data[0]
            console.log(dealData, '<=onSuccess')
            $form.setValues({
              ...dealData,
              authList: dealData.itemList,
              // 过滤掉外币列表中的本币
              currencyList: (dealData.currencyList || []).filter((item: any) => item.currencyCode !== dealData.standardCurrency)
            })
          })
        }
      }
    },
    properties: {
      inquiryDetailTab: {
        type: 'void',
        'x-component': 'FormTab',
        'x-component-props': {
          activeKey: 'projectInfo',
          type: 'border-card',
          style: 'padding-bottom: 44px;'
        },
        properties: {
          // 询价信息
          projectInfo: {
            type: 'void',
            'x-component': 'FormTab.TabPane',
            'x-component-props': {
              label: i18nExpression('bidMod.inquiryInfo'),
              disabled: generateCharExpressionByFunction(({ $readonly }) => !$readonly)
            },
            properties: {
              ...ProjectInfoSegment
            }
          },

          // 需求信息
          requireInfo: {
            type: 'void',
            'x-query-engine-skip': true,
            'x-component': 'FormTab.TabPane',
            'x-component-props': {
              label: i18nExpression('bidMod.requireInfo'),
              disabled: generateCharExpressionByFunction(({ $readonly }) => !$readonly),
              lazy: generateCharExpressionByFunction(({ $readonly }) => !$readonly)
            },
            properties: {
              ...RequireInfoSegment
            }
          },

          // 邀请供应商
          inviteVendor: {
            type: 'void',
            'x-query-engine-skip': true,
            'x-component': 'FormTab.TabPane',
            'x-component-props': {
              label: i18nExpression('bidMod.inviteVendor'),
              disabled: generateCharExpressionByFunction(({ $readonly }) => !$readonly),
              // disabled: false,
              lazy: generateCharExpressionByFunction(({ $readonly }) => !$readonly)
            },
            properties: {
              ...InviteVendorSegment(scope)
            }
          },

          // 评分规则
          scoreRule: {
            type: 'void',
            'x-query-engine-skip': true,
            'x-component': 'FormTab.TabPane',
            'x-component-props': {
              label: i18nExpression('bidMod.scoreRule'),
              disabled: generateCharExpressionByFunction(({ $readonly }) => !$readonly),
              lazy: generateCharExpressionByFunction(({ $readonly }) => !$readonly)
            },
            properties: {
              ...ScoreRuleSegment
            }
          }
        }
      }
    },
    // 底部按钮
    items: {
      type: 'void',
      'x-visible': generateCharExpressionByFunction(({ $attrsParams }) => $attrsParams.flag !== 'approve'),
      properties: {
        // 保存
        save: {
          type: 'void',
          // 公开类型，并且处于邀请供应商页，隐藏保存按钮
          'x-visible': generateCharExpressionByFunction(({ $form, $enum, $readonly }) => {
            return !(
              $getEditableTabsValue($form) === 'inviteVendor' &&
              $form.values?.publishScope === $enum.SOU_PUBLISH_SCOPE_ENUM.OPEN_TENDER
            ) &&
              $getEditableTabsValue($form) !== 'scoreRule' &&
              !$readonly
          }),
          'x-content': i18nExpression('common.save'),
          'x-component': 'Button',
          'x-component-props': {
            type: 'primary',
            loading: false,
            '@click': generateCharFunctionExpression(({ $form, $self, $queryEngine, $bus }) => {
              // console.log($form.values.itemList[0])
              $self.setComponentProps({ loading: true })
              $saveOrSubmitInq($form, $queryEngine, $bus)
            })
          }
        },

        // 上一步
        prevOne: {
          type: 'void',
          // 询价信息页不显示
          'x-visible': generateCharExpressionByFunction(({ $form, $readonly }) => {
            return $getEditableTabsValue($form) !== 'projectInfo' && !$readonly
          }),
          'x-content': i18nExpression('bidMod.prevOne'),
          'x-component': 'Button',
          'x-component-props': {
            type: 'primary',
            '@click': generateCharFunctionExpression(({ $form }) => {
              $stepTabSwitch($form, -1)
            })
          }
        },
        // 下一步
        nextOne: {
          type: 'void',
          // 评分规则页不显示
          'x-visible': generateCharExpressionByFunction(({ $form, $readonly }) => {
            return $getEditableTabsValue($form) !== 'scoreRule' && !$readonly
          }),
          'x-content': i18nExpression('bidMod.nextOne'),
          'x-component': 'Button',
          'x-component-props': {
            type: 'primary',
            '@click': generateCharFunctionExpression(({ $form, $queryEngine, $bus }) => {
              $nextStep($form, $queryEngine, $bus)
            })
          }
        },
        // 提交
        submit: {
          type: 'void',
          // 只在评分规则页显示
          'x-visible': generateCharExpressionByFunction(({ $form, $readonly }) => {
            return $getEditableTabsValue($form) === 'scoreRule' && !$readonly
          }),
          'x-content': i18nExpression('common.submit'),
          'x-component': 'Button',
          'x-component-props': {
            type: 'primary',
            '@click': generateCharFunctionExpression(({ $form, $queryEngine, $bus }) => {
              $saveOrSubmitInq($form, $queryEngine, $bus)
            })
          }
        },
        // 取消，返回
        goBack: {
          type: 'void',
          'x-content': generateCharExpressionByFunction(({ $readonly }) => $t($readonly ? 'common.backTo' : 'components.common.cancel')),
          'x-component': 'Button',
          'x-component-props': {
            type: 'default',
            '@click': generateCharFunctionExpression(({ $attrsParams }) => {
              $emitTabRemove($attrsParams.tabName)
            })
          }
        }
      }
    }
  }
})
</script>

<template>
  <RenderEngine schemaKey="inquiryDetail" :schema="schema" :scope="scope" :components="components" />
</template>
