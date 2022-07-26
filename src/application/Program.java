package application;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

import java.util.Date;
import java.util.List;

public class Program {

  public static void main(String[] args) {

    SellerDao sellerDao = DaoFactory.createSellerDao();

    System.out.println("===== Teste 1: seller findById =====");
    Seller seller = sellerDao.findById(2);

    System.out.println(seller);

    System.out.println("\n===== Teste 2: seller findByDepartment =====");
    Department department = new Department(2, null);
    List<Seller> list = sellerDao.findByDepartment(department);

    for (Seller result : list) {
      System.out.println(result);
    }

    System.out.println("\n===== Teste 3: seller findAll =====");
    list = sellerDao.findAll();

    for (Seller result : list) {
      System.out.println(result);
    }

    System.out.println("\n===== Teste 4: seller insert =====");
    Seller newSeller = new Seller(null, "Lucas", "lucas@gmail.com", new Date(), 4000.00, department);
    sellerDao.insert(newSeller);
    System.out.println("Inserted! New id = " + newSeller.getId() );


    System.out.println("\n===== Teste 5: seller update =====");
    seller = sellerDao.findById(1);
    seller.setName("Martha Rocha");
    sellerDao.update(seller);
    System.out.println("Update completed!");
  }
}
