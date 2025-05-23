package com.fesa.sharetools.Repository;

import com.fesa.sharetools.Model.Loan;
import com.fesa.sharetools.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    /*List<Loan> findByBorrower(User borrower);
    List<Loan> findByOwner(User owner);*/
    List<Loan> findByBorrowerAndReturnDateIsNull(User borrower);
    List<Loan> findByBorrower(User borrower);
    List<Loan> findByOwner(User owner);
}
