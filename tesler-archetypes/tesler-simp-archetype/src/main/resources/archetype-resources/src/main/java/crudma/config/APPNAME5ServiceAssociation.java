#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.crudma.config;

import io.tesler.core.crudma.bc.BcIdentifier;
import io.tesler.core.crudma.bc.EnumBcIdentifier;
import io.tesler.core.crudma.bc.impl.AbstractEnumBcSupplier;
import io.tesler.core.crudma.bc.impl.BcDescription;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
public enum APPNAME5ServiceAssociation implements EnumBcIdentifier {

	// @formatter:off

	;

	// @formatter:on

	public static final EnumBcIdentifier.Holder<APPNAME5ServiceAssociation> Holder = new Holder<>(APPNAME5ServiceAssociation.class);

	private final BcDescription bcDescription;

	APPNAME5ServiceAssociation(String parentName, Class<?> serviceClass, boolean refresh) {
		this.bcDescription = buildDescription(parentName, serviceClass, refresh);
	}

	APPNAME5ServiceAssociation(String parentName, Class<?> serviceClass) {
		this(parentName, serviceClass, false);
	}

	APPNAME5ServiceAssociation(BcIdentifier parent, Class<?> serviceClass, boolean refresh) {
		this(parent == null ? null : parent.getName(), serviceClass, refresh);
	}

	APPNAME5ServiceAssociation(BcIdentifier parent, Class<?> serviceClass) {
		this(parent, serviceClass, false);
	}

	APPNAME5ServiceAssociation(Class<?> serviceClass, boolean refresh) {
		this((String) null, serviceClass, refresh);
	}

	APPNAME5ServiceAssociation(Class<?> serviceClass) {
		this((String) null, serviceClass, false);
	}

	@Component
	public static class APPNAME5BcSupplier extends AbstractEnumBcSupplier<APPNAME5ServiceAssociation> {

		public APPNAME5BcSupplier() {
			super(APPNAME5ServiceAssociation.Holder);
		}

	}

}
