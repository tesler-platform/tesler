#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.app.conf;

import io.tesler.model.core.config.PersistenceJPAConfig;
import java.util.ArrayList;
import java.util.List;


public class APPNAME5PersistenceConfig extends PersistenceJPAConfig {

	@Override
	protected List<String> getPackagesToScan() {
		List<String> result = new ArrayList<>(super.getPackagesToScan());
		result.add("${package}");
		return result;
	}

}
