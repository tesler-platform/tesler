package ${package}.services;

import io.tesler.engine.workflow.WorkflowSettings;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.model.workflow.entity.WorkflowableTask;
import io.tesler.source.services.AbstractWorkflowableTaskDao;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import ${package}.model.workflow.entity.Task;

@Service
public class TaskDao extends AbstractWorkflowableTaskDao<Task> {

	public TaskDao(WorkflowSettings<Task> workflowSettings, JpaDao jpaDao) {
		super(workflowSettings, jpaDao);
	}

	@Override
	public List<Task> getTasksWithStepTermOverdue(LocalDateTime date) {
		return new ArrayList<>();
	}

	@Override
	public List<Task> findAllLinksWithAutoClosed(WorkflowableTask task) {
		return new ArrayList<>();
	}

	@Override
	public boolean isClosedChild(WorkflowableTask task) {
		return false;
	}

}
