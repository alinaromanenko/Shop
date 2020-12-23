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
        item.setImage(rs.getString("image"));
        item.setSellerId(rs.getLong("seller_id"));
    }


    @Override
    public Item save(Item item) {
        String query = "INSERT INTO `items`(`name`, `description`, `price`, `image`, `seller_id`) VALUES ('" +
                item.getName() + "', '" + item.getDescription() + "', '" + item.getPrice() + "', '" + item.getImage() + "', '" + item.getSellerId() +"')";
        try (Statement st = con.createStatement()) {
            st.executeUpdate(query);
            query = "select max(id) from `items`";
            try (ResultSet rsId = st.executeQuery(query)) {
                rsId.next();
                item.setId(rsId.getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return item;
    }

    @Override
    public Person savePerson(Person person) {
        String phone_query = "Select * from `person` where phone = '" + person.getPhone() + "';";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(phone_query)) {
            if (rs.first()) {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String query = "INSERT INTO `person`(`first_name`, `email`, `phone`, `password`, `seller`) VALUES ('" +
                person.getFirstName() + "', '" + person.getEmail() + "', '" + person.getPhone() + "', '" + person.getPassword() + "', '" + person.getIsSeller() + "')";
        try (Statement st = con.createStatement()) {
            st.executeUpdate(query);
            query = "select max(id) from `items`";
            try (ResultSet rsId = st.executeQuery(query)) {
                rsId.next();
                person.setId(rsId.getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return person;
    }

    @Override
    public Person findPerson(Person person) {
        String query = "select * from person where email='" + person.getEmail() + "' and password='" + person.getPassword() + "';";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            if (rs.first()) {
                Person fullPerson = new Person();
                setPerson(rs, fullPerson);
                return fullPerson;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void setPerson(ResultSet rs, Person person) throws SQLException {
        person.setId(rs.getLong("id"));
        person.setFirstName(rs.getString("first_name"));
        person.setEmail(rs.getString("email"));
        person.setPhone(rs.getString("phone"));
        person.setPassword(rs.getString("password"));
        person.setIsSeller(rs.getString("seller"));
    }


    @Override
    public Person findPersonById(Long id) {
        String query="SELECT * from person WHERE id='"+id+"'";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            if (rs.first()) {
                Person fullPerson = new Person();
                setPerson(rs, fullPerson);
                return fullPerson;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Item> findSellerItems(Person person) {
        ArrayList<Item> items = new ArrayList<>();
        String query = "Select * from `items` where seller_id = '" + person.getId() + "';";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                Item item = new Item();
                setItem(rs, item);
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;

    }

    @Override
    public void delete(Item item){
        System.out.println(item.getId());
        String query="delete from items WHERE id='"+item.getId()+"'";
        try (Statement st = con.createStatement();
             ) {st.executeUpdate(query);}
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Item findItem(Long id) {
        String query = "Select * from `items` where id = '" + id+"'";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            Item item = new Item();
            if (rs.next()) {
                setItem(rs, item);
                return item;
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void edit(Item item){
        System.out.println("price");
        System.out.println(item.getPrice());
        try (Statement st = con.createStatement();) {
            st.executeUpdate("update items set name= '" + item.getName()+"', description= '" + item.getDescription() +"', price='" + item.getPrice() +"', image='" + item.getImage() +"', seller_id='" + item.getSellerId() +"' where id='"+item.getId()+"'");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}