import{ak as defineComponent,aq as defineSchemas,ah as generateXindexInOrder,ae as i18nExpression,c7 as yearMonthDayStartSelectorSegment,c8 as yearMonthDayEndSelectorSegment,ad as expression,ai as editTableFormItemValid,af as yearMonthDaySelectorSegment,c9 as yearMonthDayHourMinuteSecondSelectorSegment,ar as RenderEngine,bU as $dayjs,n as normalizeComponent}from"./index-6b6051d8.js";import{p as publicOperationProperties}from"./public_operation_properties-dc6425ce.js";import{F as FilterText}from"./filter-text-7b7bbaad.js";import"./enum-13a06f7c.js";import"./index-e416f1ab.js";import"./util-1e55288f.js";const _sfc_main=defineComponent({__name:"index",setup(__props){const scope={$dayjs},schema=defineSchemas({TypeRange:{type:"void","x-query-engine":{service:"cm",actions:{paginationQuery:{immediate:!0}}},"x-decorator":"QueryEngine","x-component":"PageContainer",properties:{query:{type:"object","x-query-engine-skip":!0,"x-component":"QueryFormByQueryEngine",properties:generateXindexInOrder({contractType:{type:"string",title:i18nExpression("contractMod.contractType"),"x-component":"DictSelect","x-component-props":{code:"ELEM_CONTRACT_TYPE"}},elemName:{type:"string",title:i18nExpression("contractMod.elemName"),"x-query-engine-query-operator":"contains"},startDate:{title:i18nExpression("contractMod.startDate"),"x-query-engine-query-operator":">=",...yearMonthDayStartSelectorSegment},endDate:{title:i18nExpression("contractMod.endDate"),"x-query-engine-query-operator":"<=",...yearMonthDayEndSelectorSegment}})},toolbar:{type:"void","x-component":"Space","x-component-props":{style:"margin-bottom: 16px"},properties:{add:{type:"void",title:i18nExpression("common.add"),"x-component":"RButton","x-component-props":{type:"primary","@click":expression('({ rowIndex }) => $form.query(".table").take().componentProps.componentInstance.addRow("unshift")')}},submit:{type:"void",title:i18nExpression("common.submit"),"x-component":"RButton","x-component-props":{type:"normal","@click":expression(`() => {
                $form.validate().then(() => {
                  const $table = $form.query(".table").take().componentProps.componentInstance
                  const rows = $table.getUpdateRecords()

                  if (rows.length <= 0) {
                    $message.error($t('common.addOrUpdateRequired'))
                    return
                  }

                  for (let i = 0; i < rows.length; i += 1) {
                    const { startDate, endDate } = rows[i]

                    if (startDate && endDate && !$dayjs(startDate).isBefore($dayjs(endDate))) {
                      $message.error('结束时间不能小于当前时间')
                      return
                    }
                  }

                  $queryEngine.request.save(rows).then(() => {
                    $message.success($t('common.successSave'))
                    $table.clearAllEditStatus()
                    $queryEngine.state.paginationManagement.refresh()
                  })
                }).catch(err => {
                  $message.warning($t('common.pleasefinishRequired'))
                })
              }
              `)}}}},table:{type:"array","x-component":"RenderTable","x-component-props":{class:"table-view-vxe-table",preColumns:"seq",editMode:"multi-row",openCustomTable:!0,dblclickEditable:!0},properties:generateXindexInOrder({createdFullName:{type:"string","x-hidden":!0},typeRangeId:{type:"string","x-hidden":!0},contractType:{type:"string","x-render-table-column":{title:i18nExpression("contractMod.contractType"),minWidth:150},"x-component":"DictSelect","x-component-props":{code:"ELEM_CONTRACT_TYPE"},...editTableFormItemValid},elemName:{type:"string","x-render-table-column":{title:i18nExpression("contractMod.elemName"),minWidth:150},"x-component":"DictSelect","x-component-props":{code:"ELEMNAME","custom-select-type":"ELEMNAME","@change-value":expression(`(_, item) => {
                const row = $table.getRowByIndex($self.index)
                row.elemCode = item.key
                row.elemMaintainId = item.id
              }`)},...editTableFormItemValid},elemCode:{type:"string","x-render-table-column":{title:i18nExpression("contractMod.elemCode"),minWidth:150,skipEditable:!0}},startDate:{...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.startDate, '{y}-{m}-{d}')
              }`)},"x-render-table-column":{title:i18nExpression("contractMod.startDate"),minWidth:150},...editTableFormItemValid},endDate:{...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.endDate, '{y}-{m}-{d}')
              }`)},"x-render-table-column":{title:i18nExpression("contractMod.endDate"),minWidth:150}},createdBy:{type:"string","x-hidden":!0},lastUpdatedBy:{type:"string","x-hidden":!0},createdUserName:{type:"string",title:i18nExpression("contractMod.createdBy"),"x-query-engine-skip":!0,"x-render-table-column":{minWidth:150,skipEditable:!0}},creationDate:{"x-query-engine-sort":"desc",...yearMonthDayHourMinuteSecondSelectorSegment,"x-component-props":{...yearMonthDayHourMinuteSecondSelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.creationDate, '{y}-{m}-{d} {h}:{i}:{s}')
              }`)},title:i18nExpression("contractMod.creationDate"),"x-render-table-column":{minWidth:150,skipEditable:!0}},operation:{type:"void",title:i18nExpression("common.operation"),"x-render-table-column":{width:135,fixed:"right"},"x-component":"RenderTableButtonList",properties:publicOperationProperties("typeRangeId","contractType",!0)}})}}}});return{__sfc:!0,scope,schema,components:{FilterText},RenderEngine}}});var _sfc_render=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{schemaKey:"contractTypeElement",pageAttrs:_vm.$attrs,schema:_setup.schema,components:_setup.components,scope:_setup.scope}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const index=__component__.exports;export{index as default};
