package ${package}.crudma.dto;

import static io.tesler.api.data.dictionary.DictionaryType.TASK_TYPE;
import static java.util.Optional.ofNullable;

import io.tesler.constgen.DtoField;
import io.tesler.core.dto.mapper.DtoConstructor;
import io.tesler.core.dto.mapper.ValueSupplier;
import io.tesler.model.core.entity.Project;
import io.tesler.model.core.entity.User;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import ${package}.model.workflow.entity.Task;

@Service
public class TaskDtoConstructor extends DtoConstructor<Task, TaskDTO> {

	public TaskDtoConstructor() {
		super(Task.class, TaskDTO.class);
	}

	@Override
	protected Map<DtoField<? super TaskDTO, ?>, ValueSupplier<? super Task, ? super TaskDTO, ?>> buildValueSuppliers() {
		return ImmutableMap.<DtoField<? super TaskDTO, ?>, ValueSupplier<? super Task, ? super TaskDTO, ?>>builder()
				.put(TaskDTO_.project, (mapping, entity) -> ofNullable(entity.getProject())
						.map(Project::getName)
						.orElse(null)
				)
				.put(TaskDTO_.assignee, (mapping, entity) -> ofNullable(entity.getAssignee())
						.map(User::getUserNameInitials)
						.orElse(null)
				)
				.put(TaskDTO_.taskType, (mapping, entity) -> TASK_TYPE.lookupValue(entity.getTaskType()))
				.put(TaskDTO_.createdDate, (mapping, entity) -> entity.getCreatedDate())
				.put(TaskDTO_.startDateFact, (mapping, entity) -> entity.getStartDateFact())
				.put(TaskDTO_.resolutionDate, (mapping, entity) -> entity.getResolutionDate())
				.build();
	}

}
