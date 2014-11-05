package test.quickstart.pages;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;

/**
 * Start page of application quickstart.
 */
public class Index {
	@Property
	@Inject
	@Symbol(SymbolConstants.TAPESTRY_VERSION)
	private String tapestryVersion;

	@InjectComponent
	private Zone zone;

	@Persist
	@Property
	private int clickCount;

	@Inject
	private AlertManager alertManager;

	public Date getCurrentTime() {
		return new Date();
	}

	public String getLala() throws NamingException, SQLException {
		StringBuilder result = new StringBuilder();
		InitialContext ic = new InitialContext();
		
		NamingEnumeration<NameClassPair> list = ic.list("java:comp");
		
		while (list.hasMore()) {
			result.append(list.next().getName()).append(", ");
		}
		
		
		Context iic = (Context)ic.lookup("java:comp/env");
		DataSource myDS = (DataSource) iic.lookup("jdbc/DSTest");

		Connection connection = myDS.getConnection();

		try {
			Statement statement = connection.createStatement();
			try {
				ResultSet resultSet = statement
						.executeQuery("SELECT * FROM dual");

				try {
					while (resultSet.next()) {
						if (result.length() > 0) {
							result.append(", ");
						}

						result.append(resultSet.getString(1));
					}
				} finally {
					resultSet.close();
				}
			} finally {
				statement.close();
			}
		} finally {
			connection.close();
		}

		return result.toString();
	}

	void onActionFromIncrement() {
		alertManager.info("Increment clicked");

		clickCount++;
	}

	Object onActionFromIncrementAjax() {
		clickCount++;

		alertManager.info("Increment (via Ajax) clicked");

		return zone;
	}
}
