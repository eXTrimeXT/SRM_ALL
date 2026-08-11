import{ae as i18nExpression,bD as changeFieldVisibleByDeps,ad as expression}from"./index-6b6051d8.js";function publicOperationProperties(primaryKey,visibleKey,multiSave=!1){return{editRow:{type:"void",title:i18nExpression("common.save"),"x-reactions":changeFieldVisibleByDeps([`.${visibleKey}#editable`],`!${multiSave} && $deps[0]`),"x-component-props":{"@click":expression(`({ row, rowIndex }) => $queryEngine.request.save(row).then(() => {
          $table.cancelEditRow(rowIndex)
          $queryEngine.state.paginationManagement.refresh()
        })`)}},cancelEditRow:{type:"void",title:i18nExpression("common.cancel"),"x-reactions":changeFieldVisibleByDeps([`.${visibleKey}#editable`],"$deps[0]"),"x-component-props":{"@click":expression("({ rowIndex }) => $table.cancelEditRow(rowIndex)")}},editItem:{type:"void",title:i18nExpression("common.edit"),"x-reactions":changeFieldVisibleByDeps([`.${visibleKey}#editable`],"!$deps[0]"),"x-component-props":{"@click":expression(`({ rowIndex }) => {
          // const $table2 = $form.query("." + $table.tableName).take()
          $table.editRowByIndex(rowIndex)
        }`)}},delete:{type:"void",title:"{{$t('common.delete')}}","x-reactions":changeFieldVisibleByDeps([`.${visibleKey}#editable`],`${multiSave} && !$deps[0]`),"x-component-props":{popconfirm:{title:i18nExpression("common.confirmDeleteRow")},"@click":expression(`
          ({ row, rowIndex }) => $queryEngine.request.delete(row.${primaryKey})
              .then(() => {
                $message.success($t('common.successDelete'))
                $table.cancelEditRow(rowIndex)
                $queryEngine.state.paginationManagement.refresh()
              })
              .catch((err) => {
                console.log(err)
              })

        `)}}}}export{publicOperationProperties as p};
