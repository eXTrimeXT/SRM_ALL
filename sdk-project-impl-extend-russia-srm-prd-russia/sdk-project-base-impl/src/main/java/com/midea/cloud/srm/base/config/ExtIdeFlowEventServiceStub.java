package com.midea.cloud.srm.base.config;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.flow.model.dto.ProcinstDetailDto;
import com.midea.cloud.flow.model.dto.WorkflowEventDto;
import com.midea.cloud.flow.service.IWorkflowEventService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Заглушка для обхода зависимости от IDE Flow SDK.
 * Этот бин требуется конфигурацией: oasis.restapi.client.type: extIdeFlowEventService
 */
@Service("extIdeFlowEventService")
public class ExtIdeFlowEventServiceStub implements IWorkflowEventService {
    @Override
    public WorkflowEventDto beforeProcess(Map<String, Object> mapParam, String businessId, String businessType) {
        return null;
    }

    @Override
    public List<Map<String, Object>> queryTodo(String businessType, Long userId, String userName) {
        return Collections.emptyList();
    }

    @Override
    public PageInfo<Map<String, Object>> queryTodoCurrent(Long pageSize, Long pageIndex, String businessType, Long userId, String userName, String title) {
        return null;
    }

    @Override
    public PageInfo<Map<String, Object>> queryDone(Long pageSize, Long pageIndex, Long userId, String userName, String title) {
        return null;
    }

    @Override
    public PageInfo<Map<String, Object>> queryStart(Long pageSize, Long pageIndex, Long userId, String userName) {
        return null;
    }

    @Override
    public ProcinstDetailDto getGrantedWorkflowInformation(String businessType, String businessId) {
        return null;
    }

    @Override
    public PageInfo<Map<String, Object>> queryCopyCurrent(Long pageSize, Long pageIndex, String businessType, Long userId, String userName) {
        return null;
    }

    @Override
    public List<Long> readTask(List<Long> idList) {
        return Collections.emptyList();
    }

    // ВАЖНО: После вставки этого кода нажмите Alt+Enter на имени класса
    // и выберите "Implement methods" для генерации всех методов интерфейса.
    // В каждом сгенерированном методе оставьте return null / return false / return Collections.emptyList()
}