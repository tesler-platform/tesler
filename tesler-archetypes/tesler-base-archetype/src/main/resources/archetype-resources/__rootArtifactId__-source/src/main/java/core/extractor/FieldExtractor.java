package ${package}.core.extractor;

import com.google.common.collect.Lists;
import io.tesler.core.ui.field.BaseFieldExtractor;
import io.tesler.core.ui.model.BcField;
import io.tesler.core.ui.model.json.FieldMeta;
import io.tesler.core.util.JsonUtils;
import io.tesler.model.ui.entity.Widget;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FieldExtractor extends BaseFieldExtractor {

	@Override
	public Set<BcField> extract(Widget widget) {
		final Set<BcField> widgetFields = new HashSet<>(extractFieldsFromTitle(widget, widget.getTitle()));
		for (final FieldMeta field : JsonUtils.readValue(FieldMeta[].class, widget.getFields())) {
			widgetFields.addAll(extract(widget, field));
		}
		return widgetFields;
	}

	@Override
	public List<String> getSupportedTypes() {
		return Lists.newArrayList(
				"Form"
		);
	}

	@Override
	public int getPriority() {
		return 1;
	}

}
