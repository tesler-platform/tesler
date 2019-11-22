package io.tesler.constgen;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChildDto extends ParentDto {

	@DtoMetamodelIgnore
	private int ignoredField;

	private long childField;

}
