package ${package}.model.workflow.entity;

import io.tesler.api.data.dictionary.LOV;
import io.tesler.model.core.entity.Project;
import io.tesler.model.core.entity.User;
import io.tesler.model.workflow.entity.WorkflowableTask;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Task extends WorkflowableTask {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PROJECT_ID")
	private Project project;

	@Column(name = "TASK_TYPE_CD")
	private LOV taskType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ASSIGNEE_USER_ID")
	private User assignee;

	@Column(name = "START_DATE_FACT")
	private LocalDateTime startDateFact;

	private LocalDateTime resolutionDate;


}
