import{ak as defineComponent,aq as defineSchemas,ah as generateXindexInOrder,ae as i18nExpression,ad as expression,ai as editTableFormItemValid,af as yearMonthDaySelectorSegment,ar as RenderEngine,bU as $dayjs,n as normalizeComponent}from"./index-6b6051d8.js";import{p as publicOperationProperties}from"./public_operation_properties-dc6425ce.js";const _sfc_main=defineComponent({__name:"index",setup(__props){const scope={$dayjs},schema=defineSchemas({CondFactor:{type:"void","x-query-engine":{service:"cm",actions:{paginationQuery:{immediate:!0}}},"x-decorator":"QueryEngine","x-component":"PageContainer",properties:{query:{type:"object","x-query-engine-skip":!0,"x-component":"QueryFormByQueryEngine",properties:generateXindexInOrder({condFactor:{type:"string",title:i18nExpression("contractMod.condFactor"),"x-query-engine-query-operator":"contains"},menuName:{type:"string",title:i18nExpression("contractMod.menuName"),"x-query-engine-query-operator":"contains"},systemField:{type:"string",title:i18nExpression("contractMod.systemField"),"x-query-engine-query-operator":"contains"}})},toolbar:{type:"void","x-component":"Space","x-component-props":{style:"margin-bottom: 16px"},properties:{add:{type:"void",title:i18nExpression("common.add"),"x-component":"RButton","x-component-props":{type:"primary","@click":expression('({ rowIndex }) => $form.query(".table").take().componentProps.componentInstance.addRow("unshift")')}},submit:{type:"void",title:i18nExpression("common.submit"),"x-component":"RButton","x-component-props":{type:"normal","@click":expression(`() => {
                $form.validate().then(() => {
                  const $table = $form.query(".table").take().componentProps.componentInstance
                  const rows = $table.getUpdateRecords()

                  if (rows.length <= 0) {
                    $message.error($t('common.addOrUpdateRequired'))
                    return
                  }

                  for (let i = 0; i < rows.length; i += 1) {
                    const { startDate, endDate } = rows[i]

                    if (startDate && endDate && (!$dayjs(startDate).isBefore($dayjs(endDate)) && !$dayjs(startDate).isSame($dayjs(endDate)))) {
                      $message.error('失效日期应该大于生效日期')
                      return
                    }
                  }

                  $queryEngine.request.save(rows).then(() => {
                    $message.success($t('common.successSave'))
                    $table.clearAllEditStatus()
                    $queryEngine.state.paginationManagement.refresh()
                  })
                }).catch(err => {
                  console.log(err)
                  $message.warning($t('common.pleasefinishRequired'))
                })
              }
              `)}}}},table:{type:"array","x-component":"RenderTable","x-component-props":{class:"table-view-vxe-table",preColumns:"seq",editMode:"multi-row",openCustomTable:!0,dblclickEditable:!0},properties:generateXindexInOrder({condFactorId:{type:"string","x-hidden":!0},condFactor:{type:"string","x-render-table-column":{title:i18nExpression("contractMod.condFactor"),minWidth:150},...editTableFormItemValid},menuName:{type:"string","x-render-table-column":{title:i18nExpression("contractMod.menuName"),minWidth:150},...editTableFormItemValid},systemField:{type:"string","x-render-table-column":{title:i18nExpression("contractMod.systemField"),minWidth:150},...editTableFormItemValid},startDate:{...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.startDate, '{y}-{m}-{d}')
              }`)},"x-render-table-column":{title:i18nExpression("contractMod.startDate"),minWidth:150},...editTableFormItemValid},endDate:{"x-render-table-column":{title:i18nExpression("contractMod.endDate"),minWidth:150},...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.endDate, '{y}-{m}-{d}')
              }`)},...editTableFormItemValid},createdBy:{type:"string",title:i18nExpression("contractMod.createdBy"),"x-render-table-column":{minWidth:150,skipEditable:!0}},creationDate:{"x-query-engine-sort":"desc",title:i18nExpression("contractMod.creationDate"),...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.creationDate, '{y}-{m}-{d} {h}:{i}:{s}')
              }`)},"x-render-table-column":{minWidth:150,skipEditable:!0}},operation:{type:"void",title:i18nExpression("common.operation"),"x-render-table-column":{width:170,fixed:"right"},"x-component":"RenderTableButtonList","x-component-props":{max:2},properties:publicOperationProperties("condFactorId","condFactor",!0)}})}}}});return{__sfc:!0,scope,schema,RenderEngine}}});var _sfc_render=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{schemaKey:"conditionFactor",pageAttrs:_vm.$attrs,schema:_setup.schema,scope:_setup.scope}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const index=__component__.exports;export{index as default};
