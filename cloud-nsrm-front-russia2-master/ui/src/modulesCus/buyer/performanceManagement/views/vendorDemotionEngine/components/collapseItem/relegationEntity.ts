import {
  expression,
  i18nExpression,
  generateXindexInOrder,
  generateCharFunctionExpression,
} from '@meicloud/render-engine'

import {
  feedbackLayoutIsPopover,
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'

export default {
  type: 'void',
  'x-component': 'CollapseItem',
  'x-component-props': {
    title: i18nExpression('vendorMod.relegation.relegationEntity')
  },
  properties: {

    companyDemotionOrgs: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-component-props': {
        class: 'table-view-vxe-table',
        editMode: true,
        preColumns: expression('$form.readPretty ? \'seq\' : \'seq\''),
        pagination: false,
        sortable: false,
        // 联表主键的 key
        primaryKey: 'companyDemotionId',
        // 启用级联删除的储值行为
        cascadeDeletion: true
      },
      'x-query-engine-skip': true,
      'x-read-pretty': true,
      'x-query-engine-relation': 'companyDemotionOrgs:*',
      properties: generateXindexInOrder({
        companyDemotionId: {
          type: 'string',
          'x-hidden': true

        },
        categoryName: {
          type: 'string',
          title: "{{$t('vendorMod.relegation.relegationCategory')}}",
          'x-render-table-column': {
            minWidth: 120
          }
        },
        orgName: {
          type: 'string',
          title: "{{$t('vendorMod.relegation.relegationEntity')}}",
          'x-render-table-column': {
            minWidth: 120
          }
        },
        warningStatus: {
          type: 'string',
          title: i18nExpression('relegationEntity.key1'),
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'WARNING_STATUS',
          },
          'x-render-table-column': {
            minWidth: 120
          }
        },
        firstScore: {
          type: 'string',
          title: i18nExpression('relegationEntity.key2'),
          'x-render-table-column': {
            minWidth: 120
          }
        },
        secondScore: {
          type: 'string',
          title: i18nExpression('relegationEntity.key3'),
          'x-render-table-column': {
            minWidth: 120
          }
        },
        thirdScore: {
          type: 'string',
          title: i18nExpression('relegationEntity.key4'),
          'x-render-table-column': {
            minWidth: 120
          }
        },
        operation: {
          type: 'void',
          title: "{{$t('relegationEntity.key5')}}",
          'x-render-table-column': {
            width: 100,
          },
          'x-component': 'RenderTableButtonList',
          properties: {
            view: {
              type: 'void',
              title: i18nExpression('relegationEntity.key6'),
              'x-component-props': {
                type: 'text',
                '@click': expression(`
                    ({ row }) => {
                    $viewDetail(row,$values,$form)
                    }
                `)
              }
            }
          }
        }
      })
    },
    ScoreDialog: {
      type: 'void',
      title: i18nExpression('relegationEntity.key6'),
      'x-component': 'RDialog',
      'x-component-props': {
        'close-on-click-modal':false,
        'destroy-on-close': true,
        footerButtonList: expression(`(_, { cancelButton,okButton }) => {
          return [
            cancelButton
          ]
        }`),
        beforeClose: generateCharFunctionExpression(({ $form, $self}, done, type) => {
          if (!type || type === 'cancel') {
            done()
            return
          }
      }),
      },
      properties: {
        scoreTable: {
          type: 'array',
          'x-component': 'RenderTable',
          'x-component-props': {
            class: 'table-view-vxe-table',
            style: 'flex: 1',
            preColumns: 'seq',
            pagination:false,
            openCustomTable: false,
          },
          properties: generateXindexInOrder({
            projectName: {
              type: 'string',
              'x-render-table-column': {
                title: i18nExpression('relegationEntity.key7'),
                minWidth: 120,
              },
            },
            companyName: {
              type: 'string',
              'x-render-table-column': {
                title: i18nExpression('relegationEntity.key8'),
                minWidth: 120,
              },
            },
            organizationName: {
              type: 'string',
              'x-render-table-column': {
                title: i18nExpression('relegationEntity.key9'),
                minWidth: 120,
              },
            },
            perStartMonth: {
              type: 'string',
              'x-render-table-column': {
                title: i18nExpression('relegationEntity.key10'),
                minWidth: 120,
              },
            },
            perEndMonth: {
              type: 'string',
              'x-render-table-column': {
                title: i18nExpression('relegationEntity.key11'),
                minWidth: 120,
              },
            },
            categoryName: {
              type: 'string',
              'x-render-table-column': {
                title: i18nExpression('relegationEntity.key12'),
                minWidth: 120,
              },
            },
            scoreAttribute1: {
              type: 'string',
              'x-render-table-column': {
                title: i18nExpression('relegationEntity.key13'),
                minWidth: 120,
              },
            },
            scoreAttribute2: {
              type: 'string',
              'x-render-table-column': {
                title: i18nExpression('relegationEntity.key14'),
                minWidth: 120,
              },
            },
            scoreAttribute3: {
              type: 'string',
              'x-render-table-column': {
                title: i18nExpression('relegationEntity.key15'),
                minWidth: 120,
              },
            },
            scoreAttribute4: {
              type: 'string',
              'x-render-table-column': {
                title: i18nExpression('relegationEntity.key16'),
                minWidth: 120,
              },
            },
            scoreAttribute5: {
              type: 'string',
              'x-render-table-column': {
                title: i18nExpression('relegationEntity.key17'),
                minWidth: 120,
              },
            },
            score: {
              type: 'string',
              'x-render-table-column': {
                title: i18nExpression('relegationEntity.key18'),
                minWidth: 120,
              },
            },
            levelName: {
              type: 'string',
              'x-render-table-column': {
                title: i18nExpression('relegationEntity.key19'),
                minWidth: 120,
              },
            },
            rank: {
              type: 'string',
              'x-render-table-column': {
                title: i18nExpression('relegationEntity.key20'),
                minWidth: 120,
              },
            },
          })
      }
    }
  }
  }
}
