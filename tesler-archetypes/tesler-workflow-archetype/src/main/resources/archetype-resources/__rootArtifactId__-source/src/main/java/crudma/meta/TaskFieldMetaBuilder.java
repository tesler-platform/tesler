package ${package}.crudma.meta;

import io.tesler.core.crudma.bc.impl.InnerBcDescription;
import io.tesler.core.dto.rowmeta.FieldsMeta;
import io.tesler.core.dto.rowmeta.RowDependentFieldsMeta;
import io.tesler.core.service.rowmeta.FieldMetaBuilder;
import org.springframework.stereotype.Service;
import ${package}.crudma.dto.TaskDTO;

@Service
public class TaskFieldMetaBuilder extends FieldMetaBuilder<TaskDTO> {

	@Override
	public void buildRowDependentMeta(RowDependentFieldsMeta<TaskDTO> fields, InnerBcDescription bcDescription, Long id,
			Long parentId) {

	}

	@Override
	public void buildIndependentMeta(FieldsMeta<TaskDTO> fields, InnerBcDescription bcDescription, Long parentId) {

	}

}
