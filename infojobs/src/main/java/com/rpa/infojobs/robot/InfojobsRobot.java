package com.rpa.infojobs.robot;

import com.rpa.infojobs.model.User;
import com.rpa.infojobs.model.Vaga;
import com.rpa.infojobs.repository.UserRepository;
import com.rpa.infojobs.repository.VagaRepository;
import com.rpa.infojobs.robot.pages.JobDetailsPage;
import com.rpa.infojobs.robot.pages.LoginPage;
import com.rpa.infojobs.robot.pages.SearchPage;
import com.rpa.infojobs.security.AesEncryptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

@Component
@Slf4j
public class InfojobsRobot {

    private static final Semaphore executionLock = new Semaphore(1);

    private String infojobsEmail;
    private String infojobsPassword;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AesEncryptionService aesEncryptionService;

    @Autowired
    private LoginPage loginPage;

    @Autowired
    private SearchPage searchPage;

    @Autowired
    private JobDetailsPage jobDetailsPage;

    @Autowired
    private VagaRepository vagaRepository;

    public void carregarCredenciais(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + userId));
        this.infojobsEmail = user.getInfojobsEmail();
        try {
            this.infojobsPassword = aesEncryptionService.decrypt(user.getInfojobsPassword());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao descriptografar senha do Infojobs para o usuário: " + userId, e);
        }
        log.info("Credenciais carregadas para o usuário: {} (Infojobs email: {})", userId, infojobsEmail);
    }

    public void executarParaUsuario(String userId) {
        carregarCredenciais(userId);
        executar();
    }

    public void executar() {
        if (infojobsEmail == null || infojobsPassword == null) {
            throw new RuntimeException("Credenciais não carregadas. Execute carregarCredenciais() primeiro.");
        }

        if (!executionLock.tryAcquire()) {
            log.warn("Robô já está em execução. Ignorando nova solicitação.");
            return;
        }

        log.info("═══════════════════════════════════════");
        log.info("INICIANDO ROBÔ INFOJOBS");
        log.info("═══════════════════════════════════════");

        try {
            loginPage.setCredentials(infojobsEmail, infojobsPassword);

            log.info("ETAPA 1: REALIZANDO LOGIN...");
            boolean logado = loginPage.fazerLogin();

            if (!logado) {
                log.error("Falha no login! Encerrando robô.");
                return;
            }

            log.info("Login realizado com sucesso!");

            log.info("ETAPA 2: NAVEGANDO PARA PÁGINA DE BUSCA...");
            searchPage.irParaPaginaBusca();

            log.info("ETAPA 3: REALIZANDO BUSCA DE VAGAS...");
            searchPage.buscarVagasComFallback();

            if (searchPage.temResultados()) {
                int totalVagas = searchPage.getNumeroVagasPagina();
                log.info("Encontradas {} vagas na página", totalVagas);

                int vagasProcessadas = 0;
                for (int i = 0; i < totalVagas; i++) {
                    boolean sucesso = processarVaga(i);
                    if (sucesso) {
                        vagasProcessadas++;
                    }
                }

                log.info("Resumo: {} de {} vagas processadas com sucesso", vagasProcessadas, totalVagas);
            } else {
                log.warn("Nenhuma vaga encontrada para os termos configurados");
            }

        } catch (Exception e) {
            log.error("Erro durante execução do robô: {}", e.getMessage(), e);
        } finally {
            executionLock.release();
            log.info("Lock liberado para próximas execuções");
        }

        log.info("═══════════════════════════════════════");
        log.info("ROBÔ FINALIZADO!");
        log.info("═══════════════════════════════════════");
    }

    public boolean processarVaga(int indice) {
        try {
            int totalVagas = searchPage.getNumeroVagasPagina();
            log.info("━".repeat(50));
            log.info("Processando vaga {}/{}", indice + 1, totalVagas);

            if (!searchPage.temResultados()) {
                log.info("Nenhuma vaga válida para processar");
                return false;
            }

            if (indice >= totalVagas) {
                log.warn("Índice {} inválido. Total de vagas: {}", indice, totalVagas);
                return false;
            }

            searchPage.clicarNaVaga(indice);

            if (!jobDetailsPage.isPaginaVagaValida()) {
                log.warn("Página inválida, pulando vaga...");
                searchPage.voltarParaLista();
                return false;
            }

            Vaga vaga = jobDetailsPage.extrairDadosVaga();

            if (vaga == null || vaga.getTitulo() == null || vaga.getTitulo().contains("Página inválida")) {
                log.warn("Vaga sem dados válidos, pulando...");
                searchPage.voltarParaLista();
                return false;
            }

            log.info("Vaga: {} | Empresa: {}", vaga.getTitulo(), vaga.getEmpresa());

            boolean candidatou = jobDetailsPage.candidatar();
            vaga.setCandidaturaRealizada(candidatou);

            if (candidatou) {
                vaga.setStatus(Vaga.StatusVaga.CANDIDATADA);
                log.info("Candidatura realizada para: {}", vaga.getTitulo());
            } else {
                vaga.setStatus(Vaga.StatusVaga.PENDENTE);
                log.info("Vaga salva para análise: {}", vaga.getTitulo());
            }

            salvarVagaNoBanco(vaga);
            searchPage.voltarParaLista();

            return true;

        } catch (Exception e) {
            log.error("Erro ao processar vaga {}: {}", indice, e.getMessage());
            try {
                searchPage.voltarParaLista();
            } catch (Exception ex) {
                log.error("Não foi possível voltar à lista após erro");
            }
            return false;
        }
    }

    private void salvarVagaNoBanco(Vaga vaga) {
        try {
            if (vaga.getUrl() != null && vagaRepository.existsByUrl(vaga.getUrl())) {
                log.info("Vaga já existe no banco: {}", vaga.getTitulo());
            } else {
                Vaga saved = vagaRepository.save(vaga);
                log.info("Vaga salva no banco: ID={} | {} | {} | Candidatura: {}",
                        saved.getId(),
                        saved.getTitulo(),
                        saved.getEmpresa(),
                        saved.getCandidaturaRealizada() ? "SIM" : "NÃO");
            }
        } catch (Exception e) {
            log.error("Erro ao salvar vaga no banco: {}", e.getMessage());
        }
    }
}
