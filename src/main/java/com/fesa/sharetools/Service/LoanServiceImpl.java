package com.fesa.sharetools.Service;

import com.fesa.sharetools.Model.Loan;
import com.fesa.sharetools.Model.Tool;
import com.fesa.sharetools.Model.User;
import com.fesa.sharetools.Repository.LoanRepository;
import com.fesa.sharetools.Repository.ToolRepository;
import com.fesa.sharetools.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class LoanServiceImpl implements LoanService {

    @Autowired
    private ToolService toolService;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private ToolRepository toolRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void createLoan(Tool tool, User owner, User borrower) {
        if (!tool.isAvailable()) {
            throw new IllegalStateException("A ferramenta não está disponível para empréstimo.");
        }

        Loan loan = new Loan();
        loan.setTool(tool);
        loan.setOwner(owner);
        loan.setBorrower(borrower);
        loan.setLoanDate(LocalDate.now());
        loan.setReturnDate(LocalDate.now().plusDays(30));
        loanRepository.save(loan);

        tool.setAvailable(false);
        toolService.save(tool);
    }

    /*@Override
    public List<Loan> getLoansByBorrower(Long userId) {
        User borrower = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return loanRepository.findByBorrower(borrower);
    }

    @Override
    public List<Loan> getLoansByOwner(Long userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return loanRepository.findByOwner(owner);
    }*/

    @Override
    public List<Loan> findActiveLoansByBorrower(User borrower) {
        return loanRepository.findByBorrowerAndReturnDateIsNull(borrower);
    }

    @Override
    public void returnLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId).orElseThrow();
        Tool tool = loan.getTool();

        loan.setReturnDate(LocalDate.now());
        tool.setAvailable(true);

        toolRepository.save(tool);
        loanRepository.save(loan);
    }

    @Override
    public List<Loan> getLoansByBorrower(User borrower) {
        return loanRepository.findByBorrower(borrower);
    }

    @Override
    public List<Loan> getLoansByOwner(User owner) {
        return loanRepository.findByOwner(owner);
    }

    @Override
    @Transactional
    public void deleteLoanById(Long id) {
        Loan loan = loanRepository.findById(id).orElseThrow(() -> new RuntimeException("Empréstimo não encontrado"));

        Tool tool = loan.getTool();
        tool.setAvailable(true);
        toolRepository.save(tool);

        loanRepository.deleteById(id);
    }


}
