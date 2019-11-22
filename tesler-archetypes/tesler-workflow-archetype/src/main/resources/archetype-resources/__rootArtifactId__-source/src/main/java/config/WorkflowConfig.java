package ${package}.config;

import io.tesler.engine.workflow.WorkflowSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ${package}.model.workflow.entity.Task;
import ${package}.crudma.dto.TaskDTO;

@Configuration
public class WorkflowConfig {

	@Bean
	public WorkflowSettings workflowSettings() {
		return WorkflowSettings.builder(Task.class, TaskDTO.class).build();
	}

}
