/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package NSU.ui;

import java.sql.*;
import java.util.ArrayList;

/**
 * @author Dave Syer
 */
public class MySQLRespository implements ShopRepository {
	private Connection con;

	public MySQLRespository() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		con = DriverManager.getConnection("jdbc:mysql://localhost:3306/shop?useUnicode=true&characterEncoding=utf8", "root", "pqypwz3v");
	}

	public void closeConnection() {
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	@Override
	public Iterable<Item> findAll() {
		ArrayList<Item> items = new ArrayList<>();
		String query = "Select * from `items`";
		try (Statement st = con.createStatement();
			 ResultSet rs = st.executeQuery(query)) {
			while (rs.next()) {
				Item student = new Item();
				setItem(rs, student);
				items.add(student);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return items;
	}

	private void setItem(ResultSet rs, Item item) throws SQLException {
		item.setId(rs.getLong("id"));
		item.setName(rs.getString("name"));
		item.setDescription(rs.getString("description"));
		item.setPrice(rs.getInt("price"));
	}






	@Override
	public Item save(Item item) {
		return null;
	}

	@Override
	public Item findItem(Long id) {
		return null;
	}
}
