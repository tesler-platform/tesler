package ${package}.crudma.api;

import io.tesler.source.services.data.WorkflowableTaskService;
import ${package}.model.workflow.entity.Task;
import ${package}.crudma.dto.TaskDTO;


public interface TaskService extends WorkflowableTaskService<TaskDTO, Task> {

}
