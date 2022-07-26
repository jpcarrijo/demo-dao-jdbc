package application;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;
import model.entities.Seller;

import java.util.List;
import java.util.Scanner;

public class Program02 {

  public static void main(String[] args) {

    Scanner sc = new Scanner(System.in);

    DepartmentDao departmentDao = DaoFactory.createDepartmentDao();

    System.out.println("===== Teste 1: department findById =====");
    System.out.println("Enter by id: ");
    //int id = sc.nextInt();
    //Department department = departmentDao.findById(id);
    //System.out.println(department);

    System.out.println("===== Teste 2: department findAll =====");
    List<Department> list = departmentDao.findAll();

    for (Department result : list) {
      System.out.println(result);
    }

    System.out.println("\n===== Teste 2: department insert =====");
    Department newDepartment = new Department(null, "Music");
    //departmentDao.insert(newDepartment);
    System.out.println("Inserted! New id = " + newDepartment.getId());

    System.out.println("\n===== Teste 3: department update =====");
    System.out.print("Enter by id for update: ");
    int id = sc.nextInt();
    Department department = departmentDao.findById(id);
    department.setName("Soccer");
    departmentDao.update(department);
    System.out.println("Update completed!");
  }
}
