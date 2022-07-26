package model.dao.impl;

import db.DB;
import db.DbException;
import model.dao.DepartmentDao;
import model.entities.Department;
import model.entities.Seller;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DepartmentDaoJDBC implements DepartmentDao {

  private Connection conn;

  // ************ Constructor ***************
  public DepartmentDaoJDBC(Connection conn) {
    this.conn = conn;
  }

  // ************ Insert ********************
  @Override
  public void insert(Department obj) {
    PreparedStatement st = null;

    try {
      st = conn.prepareStatement(
          "INSERT INTO department "
              + "(Name) "
              + "VALUES "
              + "(?)",
          Statement.RETURN_GENERATED_KEYS
      );

      st.setString(1, obj.getName());

      int rowsAffected = st.executeUpdate();

      if (rowsAffected > 0) {
        ResultSet rs = st.getGeneratedKeys();
        if (rs.next()) {
          int id = rs.getInt(1);
          obj.setId(id);
        }
        DB.closeResultSet(rs);
      } else {
        throw new DbException("Unexpected error! No rows affected!");
      }
    } catch (SQLException e) {
      throw new DbException(e.getMessage());
    } finally {
      DB.closeStatement(st);
    }
  }

  // ************ Update ***************
  @Override
  public void update(Department obj) {
    PreparedStatement st = null;
    try {
      st = conn.prepareStatement(
          "UPDATE department "
              + "SET Name = ? "
              + "WHERE Id = ? ",
          Statement.RETURN_GENERATED_KEYS
      );

      st.setString(1, obj.getName());
      st.setInt(2, obj.getId());

      int row = st.executeUpdate();

      if (row == 0) {
        throw new DbException("Department not found!");
      }
    } catch (SQLException e) {
      throw new DbException(e.getMessage());
    } finally {
      DB.closeStatement(st);
    }
  }

  // ************ Delete ***************
  @Override
  public void deleteById(Integer obj) {
    PreparedStatement st = null;
    try {
      st = conn.prepareStatement(
          "DELETE FROM department "
              + "WHERE Id = ?",
          Statement.RETURN_GENERATED_KEYS
      );

      st.setInt(1, obj);

      int row = st.executeUpdate();

      if (row == 0) {
        throw new DbException("Id not found!");
      }
    } catch (SQLException e) {
      throw new DbException(e.getMessage());
    } finally {
      DB.closeStatement(st);
    }

  }

  // ************ Find by Id ************
  @Override
  public Department findById(Integer id) {
    PreparedStatement st = null;
    ResultSet rs = null;

    try {
      st = conn.prepareStatement(
          "SELECT * FROM department WHERE id = ? "
      );

      st.setInt(1, id);
      rs = st.executeQuery();

      if (rs.next()) {
        return instantiateDepartment(rs);
      } else {
        throw new DbException("Id not found!");
      }
    } catch (SQLException e) {
      throw new DbException(e.getMessage());
    } finally {
      DB.closeStatement(st);
      DB.closeResultSet(rs);
    }
  }

  // ************ Instantiate of the Department entity ******************
  private Department instantiateDepartment(ResultSet rs) throws SQLException {
    Department dep = new Department();
    dep.setId(rs.getInt("Id"));
    dep.setName(rs.getString("Name"));
    return dep;
  }

  // ************ Find all *****************
  @Override
  public List<Department> findAll() {
    PreparedStatement st = null;
    ResultSet rs = null;

    try {
      st = conn.prepareStatement(
          "SELECT * FROM department "
             + "ORDER BY Name"
      );

      rs = st.executeQuery();

      List<Department> list = new ArrayList<>();
      Map<Integer, Department> map = new HashMap<>();  // Map faz uma busca do id existente

      while (rs.next()) {
        Department dep = map.get(rs.getInt("Id"));
        if (dep == null) {
          dep = instantiateDepartment(rs);
          map.put(rs.getInt("Id"), dep);    // Inserção por chave e valor
        }
        list.add(dep);    // Retorno da lista

      } return list;
    } catch (SQLException e) {     // tratamento
      throw new DbException(e.getMessage());
    } finally {
      DB.closeStatement(st);   // fechar statement
      DB.closeResultSet(rs);   // fechar resultset
    }
  }
}
