<script setup lang="ts">
import {
  defineSchemas,
  expression,
  i18nExpression,
  observer
} from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'

import DictSelect from 'lib@/components/c-select/dict-select.vue'
import CAddress from 'lib@/components/c-address/index.vue'
import CCategorySelect from 'lib@/components/c-category-select/index.vue'
import { FileDynamic } from 'lib@/components/srm-components/file-dynamic'
import CFillProgress from 'lib@/components/c-fill-progress/index.vue'
import stepOne from './stepOne.vue'
import success from './success.vue'
import { companyNatureEngine } from './companyNatureEngine'
import mainEngine from './mainEngine.vue'
import natureChose from './components/natureChose'
const { app, emitTabRemove, t, vendor } = usePageHelper()
const props = defineProps({
  type: {
    type: String,
    default: () => ('')
  }
})
const schema = defineSchemas({
  state: {
    type: 'void',
    'x-component': 'Fragment',
    'x-hidden': true,
    'x-data': {
      companyId: app.$store.getters.userInfo.companyId || null,
      status: null,
      wheres: '',
      type: '',
      userType: '',
      flowRemark: ''
    }
  },
  CompanyInfo: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-query-engine': {
      service: 'sup',
      actions: {
        query: {
          immediate: true,
          ready: expression(`() => {
            const state = $form.query('state').get('data')
            if (!app.$store.getters.userInfo.companyId) {
              state.wheres = 'company-nature'
            }
            $form.query('state').get('data').type = $props.type
            if($props.type != 'registered' && app.$store.getters.userInfo.companyId) {
              state.wheres = 'main'
              return false
            }
            return app.$store.getters.userInfo.companyId && $buyer()
          }`),
          autoFormatResult: false,
          transformRequest: expression(`(data, headers) => {
            data.tree = true
            data.query = {
              status: {}
            }
            data.payload = {
              "filter": {
                  "companyId": {
                      eq: app.$store.getters.userInfo.companyId
                  }
              }
            }
            return data
          }`),
          onSuccess: expression(`(res) => {
            const data = res[0]
            const state = $form.query('state').get('data')
            state.status = data.status
            if (data.status == 'SUBMITTED') {
              state.wheres = 'success'
            } else {
              state.wheres = 'main'
            }
          }`)
        },
        vendorRead: {
          immediate: true,
          loading: true,
          method: 'read',
          ready: expression(`() => {
            const state = $form.query('state').get('data')
            if (!app.$store.getters.userInfo.companyId) {
              state.wheres = 'company-nature'
            }
            $form.query('state').get('data').type = $props.type
            if($props.type != 'registered' && app.$store.getters.userInfo.companyId) {
              state.wheres = 'main'
              return false
            }
            return app.$store.getters.userInfo.companyId && !$buyer()
          }`),
          autoFormatResult: false,
          transformRequest: expression(`(data, headers) => {
            data.tree = true
            data.action = 'vendorRead'
            data.query = {
              status: {},
              flowRemark: {}
            }
            data.payload = {
              "filter": {
                  "companyId": {
                      eq: app.$store.getters.userInfo.companyId
                  }
              }
            }
            return data
          }`),
          onSuccess: expression(`(res) => {
            const data = res[0]
            const state = $form.query('state').get('data')
            state.status = data.status
            state.flowRemark = data.flowRemark
            if (data.status == 'SUBMITTED' && $form.query('state').get('data').type == 'registered') { // 已提交的时候节点跳到第四个
              app.$emit('goToWhere', 4)
              app.$emit('companyInfoIsSuccess', 'Y')
              state.wheres = 'success'
              return false
            }
            if (['APPROVED','REJECTED'].includes(data.status) && $form.query('state').get('data').type == 'registered') { // 审批通过的时候节点跳到第五个
              app.$emit('goToWhere', 5)
              app.$emit('companyInfoIsSuccess', 'Y')
              state.wheres = 'success'
            } else {
              state.wheres = 'main'
            }
          }`)
        }
      }
    },
    'x-component': 'PageContainer',
    'x-component-props': {
      class: 'flex-container companyInfoMaintain',
      direction: 'vertical'
    },
    properties: {
      stepOne: {
        type: 'void',
        'x-hidden': expression('$form.query(\'state\').get(\'data\').wheres != \'stepOne\''),
        'x-component': 'stepOne',
        'x-component-props': {
          '@goToWhere': expression(`(where) => {
            $form.query('state').get('data').wheres = where
          }`)
        }
      },
      success: {
        type: 'void',
        'x-hidden': expression('$form.query(\'state\').get(\'data\').wheres != \'success\''),
        'x-component': 'success',
        'x-component-props': {
          status: expression('$form.query(\'state\').get(\'data\').status'),
          flowRemark: expression('$form.query(\'state\').get(\'data\').flowRemark'),
          '@goToWhere': expression(`(where) => {
            $form.query('state').get('data').wheres = where
            app.$emit('companyInfoIsSuccess', 'N')
          }`)
        }
      },
      companyNature: {
        type: 'void',
        'x-decorator': 'FormContainer',
        'x-decorator-props': {
          class: 'companyNature'
        },
        items: {
          type: 'object',
          properties: {
            // goBack: {
            //   type: 'void',
            //   'x-content': i18nExpression('common.backTo'),
            //   'x-component': 'Button',
            //   'x-component-props': {
            //     type: 'default',
            //     '@click': expression(`async (values) => {
            //       $form.query('state').get('data').wheres = 'stepOne'
            //     }`)
            //   }
            // },
            submit: {
              type: 'void',
              'x-content': i18nExpression('common.nextOne'),
              'x-component': 'Button',
              'x-component-props': {
                '@click': expression(`async (values) => {
                  $form.validate().then(e => {
                    $form.query('state').get('data').userType = $form.query('formCompanyNature.overseasRelation').get('value')
                    $form.query('state').get('data').wheres = 'main'
                    app.$emit('goToWhere', 3)
                  })
                }`)
              }
            }
          }
        },
        'x-hidden': expression('$form.query(\'state\').get(\'data\').wheres != \'company-nature\''),
        properties: {
          ...companyNatureEngine
        }
      },
      mainEngine: {
        type: 'void',
        'x-hidden': expression('$form.query(\'state\').get(\'data\').wheres != \'main\''),
        'x-component': 'mainEngine',
        'x-component-props': {
          '@goToWhere': expression(`(where) => {
            $form.query('state').get('data').wheres = where
          }`),
          '@saveAll': expression(`(where) => {
            app.$emit('goToWhere', 4)
            app.$emit('companyInfoIsSuccess', 'Y')
            $form.query('state').get('data').status = 'SUBMITTED'
            $form.query('state').get('data').wheres = 'success'
          }`),
          '@whatOverseasRelation': expression(`(overseasRelation) => {
            $form.query('state').get('data').wheres = 'company-nature'
            $form.query('.formCompanyNature.overseasRelation').take().props.value = overseasRelation
            app.$emit('goToWhere', 2)
          }`),
          formCompanyNature: expression('$form.query(\'.formCompanyNature\').take() || {}'),
          type: expression('$form.query(\'state\').get(\'data\').type'),
          status: expression('$form.query(\'state\').get(\'data\').status')
        }
      }
    }
  }
})

const scope = {
  $props: props,
  app,
  t,
  DictSelect,
  observer
}
const components = {
  CAddress,
  CCategorySelect,
  FileDynamic,
  CFillProgress,
  stepOne,
  success,
  mainEngine,
  natureChose
}

</script>

<template>
  <RenderEngine
    schemaKey="companyInfoMaintain"
    class="contractPaymentType"
    :schema="schema"
    :scope="scope"
    :components="components"
  />
</template>

<style lang="scss">
.companyNature{
  height: 100%;
  position: relative;
}
.boxs-row{
  width: 100%;
  margin-top: 0px;
  //position: absolute;
  //top: 40%;
  //left: 50%;
  //transform: translate(-50%,-50%);
}
.boxs-row .render-pix-form-item-label label{
  font-size: 16px;
  color: #000000;
}
.boxs-row .companyType{
  width: 33.3%;
  margin-top: 10px;
}
</style>
