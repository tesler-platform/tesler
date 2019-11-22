package ${package}.crudma.impl;

import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.crudma.impl.VersionAwareResponseService;
import io.tesler.core.dto.rowmeta.ActionResultDTO;
import io.tesler.core.dto.rowmeta.CreateResult;
import org.springframework.stereotype.Service;
import ${package}.crudma.api.TaskService;
import ${package}.crudma.dto.TaskDTO;
import ${package}.crudma.meta.TaskFieldMetaBuilder;
import ${package}.model.workflow.entity.Task;

@Service
public class TaskServiceImpl extends VersionAwareResponseService<TaskDTO, Task> implements TaskService {


	public TaskServiceImpl() {
		super(TaskDTO.class, Task.class, null, TaskFieldMetaBuilder.class);
	}

	@Override
	protected CreateResult<TaskDTO> doCreateEntity(Task entity, BusinessComponent bc) {
		baseDAO.save(entity);
		return new CreateResult<>(entityToDto(bc, entity));
	}

	@Override
	protected ActionResultDTO<TaskDTO> doUpdateEntity(Task entity, TaskDTO data, BusinessComponent bc) {
		return new ActionResultDTO<>(entityToDto(bc, entity));
	}

}
