import {
  expression, generateXindexInOrder,
  i18nExpression
} from '@meicloud/render-engine'
import {
  requiredValidatorSegment,
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'

export const DialogMain = {
  type: 'void',
  'x-query-engine': {
    service: 'sup',
    type: 'SiteReviewPlan',
    actions: {
      save: {
        // 标记当前 action 需要消费底层储存的级联删除数据
        cascadeDeletion: true
      }
    }
  },
  'x-component': 'QueryEngine',
  properties: {
    Dialog: {
      type: 'void',
      title: '{{$t(\'vendorMod.detail\')}}',
      'x-component': 'RDialog',
      'x-component-props': {
        class: 'dialogMain',
        size: 'large',
        cancelText: '{{ $t("common.staging") }}',
        okText: '{{ $t("common.submit") }}',
        beforeClose: expression(`(done, type,closeLoading) => {
            const siteReviewPlanCategorys = $self.query('siteReviewPlanCategorys').take().value
            if ( type === 'ok') {
              if (!siteReviewPlanCategorys || siteReviewPlanCategorys.length == 0) {
                app.$message.warning($t('vendorMod.pleaseFillInTheCategory'))
                return false
              }
              return $self.query('*.*.Dialog.form').take().submit(values => {
                values.planProcessStatus = 'NOT_STARTED'
                return $queryEngine.request.save({
                  ...values,
                  siteReviewPlanCategorys: siteReviewPlanCategorys,
                  planStatus: 'SUBMITTED'
                }).then(() => {
                  app.$message.success($t('common.success'))
                  $queryEngine.state.paginationManagement.refresh()
                  done()
                }).catch(() => {closeLoading()})
              })
            } else if ( type === 'cancel' ) {
              return $self.query('*.*.Dialog.form').take().submit(values => {
                values.planProcessStatus = 'NOT_STARTED'
                return $queryEngine.request.save({
                  ...values,
                  siteReviewPlanCategorys: siteReviewPlanCategorys,
                  planStatus: 'DRAFT'
                }).then(() => {
                  app.$message.success($t('common.success'))
                  $queryEngine.state.paginationManagement.refresh()
                  done()
                })
              })
            } else {
              done()
            }
      }`)
      },
      properties: {
        form: {
          type: 'object',
          'x-component': 'FormGrid',
          'x-component-props': {
            maxColumns: 4,
            columnGap: 32,
            rowGap: 0
          },
          properties: {
            planName: {
              type: 'string',
              title: '{{$t(\'vendorMod.planName\')}}', // 计划名称
              required: true,
              'x-decorator': 'FormItem'
            },
            vendorName: {
              type: 'string',
              title: '{{$t(\'vendorMod.vendorId\')}}', // 供应商名称
              required: true,
              'x-component': 'QuickSearchWrapper',
              'x-component-props': {
                showKey: 'companyName',
                propKey: 'companyName',
                'name': 'scc_sup_company_info2',
                '@close-quicksearch': expression(`(val, scope) => {
              if (val) {
                $values.form.vendorId = val.companyId
                $values.form.vendorName = val.companyName
                $values.form.vendorCode = val.companyCode
              } else {
                $values.form.vendorId = null
                $values.form.vendorName = null
                $values.form.vendorCode = null
              }
            }`)
              },
              'x-decorator': 'FormItem'
            },
            orgId: {
              type: 'string',
              title: '{{$t(\'vendorMod.orgName\')}}', // 采购组织
              'x-component': 'OrganizationSelector',
              required: true,
              'x-component-props': {
                'node-type': 'OU',
                multiple: false,
                '@select': expression(`(node, value) => {
              $values.form.orgId = node.organizationId
              $values.form.orgCode = node.organizationCode
              $values.form.orgName = node.organizationName
            }`)
              },
              'x-decorator': 'FormItem'
            },
            planType: {
              type: 'string',
              title: '{{$t(\'vendorMod.planType\')}}', // 计划类型
              required: true,
              'x-component': 'DictSelect',
              'x-component-props': {
                code: 'CEEA_ASSESSMENT_TYPE'
              },
              'x-decorator': 'FormItem'
            },
            planStartDate: {
              title: i18nExpression('vendorMod.planStartDate'), // 计划开始时间
              'x-decorator': 'FormItem',
              ...yearMonthDaySelectorSegment,
              ...requiredValidatorSegment
            }
          }
        },
        toolbar: {
          type: 'void',
          'x-component': 'Space',
          'x-component-props': {
            style: 'margin-bottom: 16px'
          },
          properties: {
            dialogAdd: {
              type: 'void',
              title: '{{$t(\'common.add\')}}',
              'x-component': 'RButton',
              'x-component-props': {
                style: 'margin-top:12px',
                type: 'primary',
                '@click': expression(`() => {
              $form.query("siteReviewPlanCategorys").take().componentProps.componentInstance.addRow("unshift")
            }`)
              }
            }
          }
        },
        siteReviewPlanCategorys: {
          type: 'array',
          'x-component': 'RenderTable',
          'x-component-props': {
            // 如果都没有标记，那么默认使用 id 作为联表主键的 key
            primaryKey: 'reviewPlanCategoryId',
            // 启用级联删除的储值行为
            cascadeDeletion: true,
            preColumns: 'seq',
            pagination: false,
            editMode: 'multi-row'
          },
          'x-query-engine-skip': true,
          'x-query-engine-relation': 'siteReviewPlanCategorys:*',
          properties: generateXindexInOrder({
            categoryCode: {
              type: 'string',
              title: '{{$t(\'common.categoryCode\')}}', // 品类编码
              'x-component': 'QuickSearchWrapper',
              'x-component-props': {
                disabled: false,
                showKey: 'companyName',
                propKey: 'companyName',
                'name': 'scc_base_purchase_category2',
                '@close-quicksearch': expression(`(val, scope) => {
                const row = $table.getRowByIndex($self.index)
                row.categoryId = val ? val.categoryId : ''
                row.categoryCode = val ? val.categoryCode : ''
                row.categoryName = val ? val.categoryName : ''
            }`)
              },
              'x-render-table-column': {
                minWidth: 150
              }
            },
            categoryName: {
              type: 'string',
              title: '{{$t(\'common.categoryName\')}}', // 品类名称
              'x-render-table-column': {
                minWidth: 120,
                // 跳过行内编辑
                skipEditable: true
              }
            },
            operation: {
              type: 'void',
              title: '{{$t(\'common.operation\')}}',
              'x-render-table-column': {
                width: 150,
                fixed: 'right'
              },
              'x-component': 'RenderTableButtonList',
              properties: {
                delete: {
                  type: 'void',
                  title: '{{$t(\'common.delete\')}}',
                  'x-component-props': {
                    type: 'text',
                    '@click': expression(`({ row }) => {
                    $table.remove($self.index)
                  }`)
                  }
                }
              }
            }
          })
        }
      }
    }
  }
}
