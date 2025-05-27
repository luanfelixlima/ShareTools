package com.fesa.sharetools.Repository;

import com.fesa.sharetools.Model.Loan;
import com.fesa.sharetools.Model.Tool;
import com.fesa.sharetools.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByBorrowerAndReturnDateIsNull(User borrower);
    List<Loan> findByBorrower(User borrower);
    List<Loan> findByOwner(User owner);
    Optional<Loan> findByTool(Tool tool); // <--- Use este método para buscar o empréstimo ativo
    boolean existsByTool(Tool tool);       // <--- E este para uma verificação de existência mais eficiente
}
