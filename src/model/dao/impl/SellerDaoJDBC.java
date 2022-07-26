package model.dao.impl;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellerDaoJDBC implements SellerDao {

  private Connection conn;

  // ************ Constructor ***************
  public SellerDaoJDBC(Connection conn) {
    this.conn = conn;
  }

  // ************ Insert ********************
  @Override
  public void insert(Seller obj) {
    PreparedStatement st = null;
    try {
      st = conn.prepareStatement(
          "INSERT INTO seller "
              + "(Name, Email, BirthDate, BaseSalary, DepartmentId) "
              + "VALUES "
              + "(?, ?, ?, ?, ?) ",
          Statement.RETURN_GENERATED_KEYS
      );

      st.setString(1, obj.getName());
      st.setString(2, obj.getEmail());
      st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
      st.setDouble(4, obj.getBaseSalary());
      st.setInt(5, obj.getDepartment().getId());

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
  public void update(Seller obj) {
    PreparedStatement st = null;
    try {
      st = conn.prepareStatement(
          "UPDATE seller "
              + "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? "
              + "WHERE Id = ? ",
          Statement.RETURN_GENERATED_KEYS
      );

      st.setString(1, obj.getName());
      st.setString(2, obj.getEmail());
      st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
      st.setDouble(4, obj.getBaseSalary());
      st.setInt(5, obj.getDepartment().getId());
      st.setInt(6, obj.getId());

      st.executeUpdate();

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
          "DELETE FROM seller "
              + "WHERE Id = ?",
          Statement.RETURN_GENERATED_KEYS
      );

      st.setInt(1, obj);

      int row = st.executeUpdate();

      if(row == 0) {
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
  public Seller findById(Integer id) {
    PreparedStatement st = null;
    ResultSet rs = null;

    try {
      st = conn.prepareStatement(
          "SELECT seller.*,department.Name as DepName "
              + "FROM seller INNER JOIN department "
              + "ON seller.DepartmentId = department.Id "
              + "WHERE seller.Id = ? "
      );

      st.setInt(1, id);
      rs = st.executeQuery();

      if (rs.next()) {
        Department dep = instantiateDepartment(rs);
        return instantiateSeller(rs, dep);
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


  // ************ Instantiate of the Seller entity ******************
  private Seller instantiateSeller(ResultSet rs, Department dep) throws SQLException {
    Seller obj = new Seller();
    obj.setId(rs.getInt("Id"));
    obj.setName(rs.getString("Name"));
    obj.setEmail(rs.getString("Email"));
    obj.setBirthDate(rs.getDate("birthDate"));
    obj.setBaseSalary(rs.getDouble("baseSalary"));
    obj.setDepartment(dep);
    return obj;
  }

  // ************ Instantiate of the Department entity ******************
  private Department instantiateDepartment(ResultSet rs) throws SQLException {
    Department dep = new Department();
    dep.setId(rs.getInt("DepartmentId"));
    dep.setName(rs.getString("DepName"));
    return dep;
  }

  // ************ Find all *****************
  @Override
  public List<Seller> findAll() {
    PreparedStatement st = null;
    ResultSet rs = null;

    try {
      st = conn.prepareStatement(
          "SELECT seller.*,department.Name as DepName "
              + "FROM seller INNER JOIN department "
              + "ON seller.DepartmentId = department.Id "
              + "ORDER BY Name"
      );

      rs = st.executeQuery();

      List<Seller> list = new ArrayList<>();
      Map<Integer, Department> map = new HashMap<>();  // Map faz uma busca do id existente

      while (rs.next()) {
        Department dep = map.get(rs.getInt("DepartmentId"));  // retorna null ou o Id
        if (dep == null) {
          dep = instantiateDepartment(rs);
          map.put(rs.getInt("DepartmentId"), dep);
        }
        Seller obj = instantiateSeller(rs, dep);
        list.add(obj);
      }
      return list;
    } catch (SQLException e) {
      throw new DbException(e.getMessage());
    } finally {
      DB.closeStatement(st);
      DB.closeResultSet(rs);
    }
  }


  // ************ List by Department *******************
  @Override
  public List<Seller> findByDepartment(Department department) {
    PreparedStatement st = null;
    ResultSet rs = null;

    try {
      st = conn.prepareStatement(
          "SELECT seller.*,department.Name as DepName "
              + "FROM seller INNER JOIN department "
              + "ON seller.DepartmentId = department.Id "
              + "WHERE DepartmentId = ? "
              + "ORDER BY Name "
      );

      st.setInt(1, department.getId());
      rs = st.executeQuery();

      List<Seller> list = new ArrayList<>();
      Map<Integer, Department> map = new HashMap<>();  // Map faz uma busca do id existente

      while (rs.next()) {
        Department dep = map.get(rs.getInt("DepartmentId"));  // retorna null ou o Id
        if (dep == null) {
          dep = instantiateDepartment(rs);
          map.put(rs.getInt("DepartmentId"), dep);
        }
        Seller obj = instantiateSeller(rs, dep);
        list.add(obj);
      }
      return list;
    } catch (SQLException e) {
      throw new DbException(e.getMessage());
    } finally {
      DB.closeStatement(st);
      DB.closeResultSet(rs);
    }
  }
}
