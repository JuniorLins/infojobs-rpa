package com.rpa.infojobs.robot.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LoginPage extends BasePage {

    private static final String URL_INFOJOBS = "https://www.infojobs.com.br";
    private static final String URL_LOGIN = "https://login.infojobs.com.br/Account/Login";

    private static final String XPATH_EMAIL           = "//*[@id='Email']";
    private static final String XPATH_SENHA           = "//*[@id='Password']";
    private static final String XPATH_BOTAO_CONTINUAR = "//*[@id='loginForm']/button/div/span/span[2]";
    private static final String XPATH_BOTAO_ACESSAR   = "//*[@id='loginForm']/button/div/span/span[1]";

    private String currentEmail;
    private String currentPassword;

    public void abrirPagina() {
        logger.info("Abrindo página de login do InfoJobs");
        try {
            driver.get(URL_LOGIN);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("Email")));
            logger.info("Página de login carregada: {}", driver.getTitle());
            fecharPopupCookies();
        } catch (Exception e) {
            logger.error("Erro ao abrir página de login: {}", e.getMessage());
            driver.get(URL_INFOJOBS);
            fecharPopupCookies();
        }
    }

    public void preencherEmail() {
        logger.info("Preenchendo email: {}", currentEmail);
        try {
            WebElement campo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(XPATH_EMAIL)));
            campo.clear();
            campo.sendKeys(currentEmail);
            logger.info("Email preenchido com XPath: {}", XPATH_EMAIL);
        } catch (Exception e) {
            // Fallback
            WebElement campo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[type='email']")));
            campo.clear();
            campo.sendKeys(currentEmail);
        }
    }

    public void clicarBotaoContinuar() {
        logger.info("Clicando no botão 'Continuar'...");
        WebElement botao = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(XPATH_BOTAO_CONTINUAR)));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", botao);
        logger.info("Clique no botão 'Continuar' realizado!");
    }

    public void preencherSenha() {
        logger.info("Preenchendo senha");
        try {
            WebElement campo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(XPATH_SENHA)));
            campo.clear();
            campo.sendKeys(currentPassword);
        } catch (Exception e) {
            // Fallback
            WebElement campo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[type='password']")));
            campo.clear();
            campo.sendKeys(currentPassword);
        }
    }

    public void clicarBotaoAcessar() {
        logger.info("Clicando no botão 'Acessar'...");
        WebElement botao = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(XPATH_BOTAO_ACESSAR)));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", botao);
        logger.info("Clique no botão 'Acessar' realizado!");
    }

    public boolean fazerLogin() {
        logger.info("═══════════════════════════════════════");
        logger.info("INICIANDO PROCESSO DE LOGIN");
        logger.info("═══════════════════════════════════════");

        try {
            abrirPagina();
            fecharPopupCookies();
            preencherEmail();
            clicarBotaoContinuar();

            preencherSenha();
            clicarBotaoAcessar();

            fecharModalPremium();

            boolean paginaCarregada = aguardarPaginaCandidato();

            if (!paginaCarregada) {
                logger.error("Página do candidato não carregou após o login");
                return false;
            }

            if (isLogado()) {
                logger.info("═══════════════════════════════════════");
                logger.info("LOGIN REALIZADO COM SUCESSO!");
                logger.info("═══════════════════════════════════════");
                return true;
            } else {
                logger.error("Login falhou — credenciais inválidas ou página inesperada");
                return false;
            }
        } catch (Exception e) {
            logger.error("Erro durante login: {}", e.getMessage(), e);
            return false;
        }
    }

    public boolean isLogado() {
        try {
            String currentUrl = driver.getCurrentUrl().toLowerCase();
            if (currentUrl.contains("candidate") ||
                    currentUrl.contains("minha-conta") ||
                    currentUrl.contains("home") ||
                    currentUrl.contains("dashboard")) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean aguardarPaginaCandidato() {
        try {
            int tentativas = 0;
            while (tentativas < 10) {
                String urlLower = driver.getCurrentUrl().toLowerCase();
                if (urlLower.contains("candidate") ||
                        urlLower.contains("minha-conta") ||
                        urlLower.contains("home") ||
                        urlLower.contains("dashboard")) {
                    return true;
                }
                sleep(1500);
                tentativas++;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public void fecharPopupCookies() {
        try {
            WebElement popup = driver.findElement(By.id("didomi-popup"));
            if (popup.isDisplayed()) {
                ((JavascriptExecutor) driver).executeScript("document.getElementById('didomi-popup').remove();");
                sleep(1000);
            }
        } catch (Exception e) {
            logger.debug("Popup de cookies não encontrado — OK");
        }
    }

    public void fecharModalPremium() {
        try {
            String[] xpathsModal = {
                    "/html/body/form/div[3]/div/div/span",
                    "//*[contains(@class, 'modal')]//span[contains(@class, 'close')]",
                    "//button[contains(text(), 'Fechar') or contains(text(), 'Agora não')]"
            };
            for (String xpath : xpathsModal) {
                try {
                    WebElement modal = driver.findElement(By.xpath(xpath));
                    if (modal.isDisplayed()) {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", modal);
                        return;
                    }
                } catch (Exception e) {
                    // Tenta o próximo
                }
            }
        } catch (Exception e) {
            logger.debug("Modal Premium não encontrado — OK");
        }
    }

    public void setCredentials(String email, String password) {
        this.currentEmail = email;
        this.currentPassword = password;
    }
}
