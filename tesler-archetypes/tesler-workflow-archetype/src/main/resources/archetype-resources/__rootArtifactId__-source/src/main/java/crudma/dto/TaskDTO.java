package ${package}.crudma.dto;

import static io.tesler.api.data.dictionary.DictionaryType.TASK_TYPE;
import static io.tesler.core.util.filter.SearchParameterType.DATE;
import static io.tesler.core.util.filter.SearchParameterType.LOV;

import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.core.dto.Lov;
import io.tesler.core.util.filter.SearchParameter;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskDTO extends DataResponseDTO {

	private String project;

	@Lov(TASK_TYPE)
	@SearchParameter(type = LOV)
	private String taskType;

	@SearchParameter(type = DATE)
	private LocalDateTime resolutionDate;

	@SearchParameter(type = DATE)
	private LocalDateTime createdDate;

	@SearchParameter(name = "assignee.lastName")
	private String assignee;

	@SearchParameter(type = DATE)
	private LocalDateTime startDateFact;

}
