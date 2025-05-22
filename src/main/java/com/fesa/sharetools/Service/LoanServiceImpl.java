package com.fesa.sharetools.Service;

import com.fesa.sharetools.Model.Loan;
import com.fesa.sharetools.Model.Tool;
import com.fesa.sharetools.Model.User;
import com.fesa.sharetools.Repository.LoanRepository;
import com.fesa.sharetools.Repository.ToolRepository;
import com.fesa.sharetools.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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


    @Override
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
    }

    @Override
    public void returnLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado"));
        loanRepository.delete(loan);
    }
}

