package com.midea.cloud.srm.base.ext.workbech;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.srm.base.todolist.service.TodolistService;
import com.midea.cloud.srm.base.workbench.service.WorkbenchService;
import com.midea.cloud.srm.model.base.todolist.dto.TodolistQuery;
import com.midea.cloud.srm.model.base.todolist.enums.TodolistConfigType;
import com.midea.cloud.srm.model.base.work.dto.WorkCount;
import com.midea.cloud.srm.model.flow.query.dto.WorkbenchMyTaskDTO;
import com.midea.cloud.srm.model.flow.vo.WorkbenchMyTaskVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@Service
@Primary
@Slf4j
public class ExtWorkbenchServiceImpl implements WorkbenchService {

    @Autowired
    private TodolistService todolistService;

    @Override
    public PageInfo<WorkbenchMyTaskVo> findMyRunningProcess(WorkbenchMyTaskDTO workbenchMyTaskDTO) throws Exception {
        return findProcess(workbenchMyTaskDTO, TodolistConfigType.WAIT_FORM);
    }

    @Override
    public PageInfo<WorkbenchMyTaskVo> findMyWorkedProcess(WorkbenchMyTaskDTO workbenchMyTaskDTO) throws Exception {
        return findProcess(workbenchMyTaskDTO, TodolistConfigType.DONE_FORM);
    }

    @Override
    public PageInfo<WorkbenchMyTaskVo> findMyStartProcess(WorkbenchMyTaskDTO workbenchMyTaskDTO) throws Exception {
        return findProcess(workbenchMyTaskDTO, TodolistConfigType.MY_START);
    }

    @Override
    public List<WorkCount> workCount(TodolistConfigType todolistConfigType) {
        TodolistQuery todolistQuery = new TodolistQuery();
        todolistQuery.setConfigType(todolistConfigType.name());

        List<Map<String, Object>> todoList = todolistService.statByConfigCode(todolistQuery);

        List<WorkCount> workList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(todoList)) {
            for (Map<String, Object> todoItem: todoList) {
                WorkCount workCount = new WorkCount();

                workCount.initByTodo(todoItem);

                workList.add(workCount);
            }
        }

        return workList;
    }

    private PageInfo<WorkbenchMyTaskVo> findProcess(WorkbenchMyTaskDTO workbenchMyTaskDTO, TodolistConfigType todolistConfigType) throws Exception {

        TodolistQuery todolistQuery = new TodolistQuery();
        todolistQuery.setPageNum(workbenchMyTaskDTO.getPageNum());
        todolistQuery.setPageSize(workbenchMyTaskDTO.getPageSize());
        todolistQuery.setConfigType(todolistConfigType.name());
        todolistQuery.setTitle(workbenchMyTaskDTO.getTitle());
        todolistQuery.setCreateName(workbenchMyTaskDTO.getCreateName());

        PageInfo<Map<String, Object>> todoPage = todolistService.listTodo(todolistQuery);

        PageInfo<WorkbenchMyTaskVo> pageInfo = new PageInfo<>();
        pageInfo.setPageNum(todoPage.getPageNum());
        pageInfo.setPageSize(todoPage.getPageSize());

        List<Map<String, Object>> todoList = todoPage.getList();
        List<WorkbenchMyTaskVo> workList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(todoList)) {
            for (Map<String, Object> todoItem: todoList) {
                WorkbenchMyTaskVo workbench = new WorkbenchMyTaskVo();

                workbench.setExtensions(todoItem);
                workbench.initByTodo(todoItem);

                if (StringUtils.isBlank(workbench.getResidenceTime()) && workbench.getCreationDate() != null) {
                    workbench.setResidenceTime(DateUtil.getTimeAndNowInterval(workbench.getCreationDate().getTime()));
                }

                workList.add(workbench);
            }
        }

        pageInfo.setList(workList);
        pageInfo.setTotal(todoPage.getTotal());

        return pageInfo;
    }
}
