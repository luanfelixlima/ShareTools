package com.fesa.sharetools.Service;

import com.fesa.sharetools.Model.Loan;
import com.fesa.sharetools.Model.Tool;
import com.fesa.sharetools.Model.User;

import java.util.List;
import java.util.Optional;

public interface LoanService {
    void createLoan(Tool tool, User owner, User borrower);
    void returnLoan(Long loanId);
    List<Loan> findActiveLoansByBorrower(User borrower);
    List<Loan> getLoansByBorrower(User borrower);
    List<Loan> getLoansByOwner(User owner);
    void deleteLoanById(Long id);
    Optional<Loan> findActiveLoanByTool(Tool tool);
    boolean isToolCurrentlyLoaned(Tool tool);
}


