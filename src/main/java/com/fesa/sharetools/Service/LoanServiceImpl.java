package com.fesa.sharetools.Service;

import com.fesa.sharetools.Model.Loan;
import com.fesa.sharetools.Model.Tool;
import com.fesa.sharetools.Model.User;
import com.fesa.sharetools.Repository.LoanRepository;
import com.fesa.sharetools.Repository.ToolRepository;
import com.fesa.sharetools.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
    @Transactional
    public void createLoan(Tool tool, User owner, User borrower) {
        // Primeira verificação: se a ferramenta está marcada como disponível para empréstimo
        // Isso é importante, pois impede que um empréstimo seja criado se a ferramenta já está indisponível.
        if (!tool.isAvailable()) {
            throw new IllegalStateException("A ferramenta '" + tool.getName() + "' não está disponível para empréstimo.");
        }

        // Segunda verificação (E MUITO IMPORTANTE para sua lógica de "tabela Loan só tem ativos"):
        // Verifique se a ferramenta já está registrada como emprestada na tabela Loan.
        // Se existsByTool retornar true, significa que já há um empréstimo ativo para essa ferramenta.
        if (loanRepository.existsByTool(tool)) {
            throw new IllegalStateException("A ferramenta '" + tool.getName() + "' já possui um empréstimo ativo.");
        }

        Loan loan = new Loan();
        loan.setTool(tool);
        loan.setOwner(owner);
        loan.setBorrower(borrower);
        loan.setLoanDate(LocalDate.now());
        loan.setReturnDate(LocalDate.now().plusDays(30)); // Data esperada de devolução

        loanRepository.save(loan); // Salva o novo registro de empréstimo ativo

        // Marca a ferramenta como indisponível após o empréstimo ser registrado com sucesso
        tool.setAvailable(false);
        toolService.save(tool);
    }

    @Override
    public List<Loan> findActiveLoansByBorrower(User borrower) {
        // Se a tabela Loan só guarda ativos, este método está correto
        return loanRepository.findByBorrower(borrower);
    }

    @Override
    @Transactional
    public void returnLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new RuntimeException("Empréstimo não encontrado."));
        Tool tool = loan.getTool();

        // Para "devolver" o empréstimo, nós o REMOVEMOS da tabela de empréstimos ativos.
        loanRepository.delete(loan); // <--- DELETA O REGISTRO DO EMPRÉSTIMO!

        // Marca a ferramenta como disponível após a devolução (exclusão do empréstimo)
        tool.setAvailable(true);
        toolService.save(tool);
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
        // Se a exclusão de um registro de empréstimo (por ID) significa que ele foi "finalizado"
        // e a ferramenta deve voltar a ficar disponível:
        Loan loan = loanRepository.findById(id).orElseThrow(() -> new RuntimeException("Empréstimo não encontrado"));
        Tool tool = loan.getTool();

        // Remove o registro do empréstimo.
        loanRepository.deleteById(id);

        // Se após a exclusão deste empréstimo, não houver mais nenhum empréstimo ativo para a ferramenta,
        // então ela pode ser marcada como disponível.
        // Usamos existsByTool() aqui. Se retornar FALSE, significa que não há mais empréstimos ativos para ela.
        if (!loanRepository.existsByTool(tool)) {
            tool.setAvailable(true);
            toolRepository.save(tool);
        }
    }

    @Override
    public Optional<Loan> findActiveLoanByTool(Tool tool) {
        return loanRepository.findByTool(tool);
    }

    @Override
    public boolean isToolCurrentlyLoaned(Tool tool) {
        return loanRepository.existsByTool(tool);
    }
}