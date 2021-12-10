package io.tesler.constgen;

import lombok.Getter;
import lombok.Setter;

@Setter
public class ChildDto extends ParentDto {

	@Getter
	@DtoMetamodelIgnore
	private int ignoredField;

	@Getter
	private long childField;

	public int getIgnoredField() {
		return this.ignoredField;
	}

	public long getChildField() {
		return this.childField;
	}

}
