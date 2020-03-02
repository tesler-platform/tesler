package io.tesler.core.dto.multivalue;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SnapshotState {

	NEW("new"),
	DELETED("deleted"),
	NO_CHANGES("noChanges");

	private String value;

}
