#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.crudma;

import io.tesler.core.crudma.bc.BcIdentifier;
import io.tesler.core.crudma.bc.EnumBcIdentifier;
import io.tesler.core.crudma.bc.impl.AbstractEnumBcSupplier;
import io.tesler.core.crudma.bc.impl.BcDescription;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
public enum ServiceAssociation implements EnumBcIdentifier {

	// @formatter:off

	;

	// @formatter:on

	public static final EnumBcIdentifier.Holder<ServiceAssociation> Holder = new Holder<>(ServiceAssociation.class);

	private final BcDescription bcDescription;

	ServiceAssociation(String parentName, Class<?> serviceClass, boolean refresh) {
		this.bcDescription = buildDescription(parentName, serviceClass, refresh);
	}

	ServiceAssociation(String parentName, Class<?> serviceClass) {
		this(parentName, serviceClass, false);
	}

	ServiceAssociation(BcIdentifier parent, Class<?> serviceClass, boolean refresh) {
		this(parent == null ? null : parent.getName(), serviceClass, refresh);
	}

	ServiceAssociation(BcIdentifier parent, Class<?> serviceClass) {
		this(parent, serviceClass, false);
	}

	ServiceAssociation(Class<?> serviceClass, boolean refresh) {
		this((String) null, serviceClass, refresh);
	}

	ServiceAssociation(Class<?> serviceClass) {
		this((String) null, serviceClass, false);
	}

	@Component
	public static class BcSupplier extends AbstractEnumBcSupplier<ServiceAssociation> {

		public BcSupplier() {
			super(ServiceAssociation.Holder);
		}

	}

}
