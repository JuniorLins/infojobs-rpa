package com.rpa.infojobs.robot.pages;

import com.rpa.infojobs.model.Vaga;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class JobDetailsPage extends BasePage {

    private String tituloAtual;

    public boolean isPaginaVagaValida() {
        try {
            String url = driver.getCurrentUrl();
            // Como o clicarNaVaga já esperou a URL carregar, isso será instantâneo
            if (url.contains("oferta") || url.contains("vaga")) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public Vaga extrairDadosVaga() {
        Vaga vaga = new Vaga();
        try {
            if (!isPaginaVagaValida()) {
                vaga.setTitulo("Página inválida");
                vaga.setUrl(driver.getCurrentUrl());
                return vaga;
            }

            String titulo = extrairTitulo();
            vaga.setTitulo(titulo);
            this.tituloAtual = titulo;

            vaga.setEmpresa(extrairEmpresa());
            vaga.setLocalizacao(extrairLocalizacao());
            vaga.setSalario(extrairSalario());
            vaga.setDescricao(extrairDescricao());
            vaga.setDataPublicacao(extrairDataPublicacao());
            vaga.setUrl(driver.getCurrentUrl());
            vaga.setStatus(Vaga.StatusVaga.PENDENTE);
            vaga.setCandidaturaRealizada(false);

        } catch (Exception e) {
            logger.error("Erro ao extrair dados: {}", e.getMessage());
        }
        return vaga;
    }

    private String extrairTitulo() {
        // Seletores mais abrangentes, usando shortWait (5s) para não travar o robô
        String[] seletores = {
                "h2.h3.font-weight-bold.text-body",
                "h1",
                "h2",
                ".job-title"
        };
        for (String seletor : seletores) {
            try {
                WebElement elemento = shortWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(seletor)));
                if (elemento.isDisplayed() && !elemento.getText().isBlank()) {
                    return elemento.getText().trim();
                }
            } catch (Exception ignored) {}
        }
        return "Título não encontrado";
    }

    private String extrairEmpresa() {
        String[] seletores = {
                "a.text-body.text-decoration-none",
                ".company-name",
                ".company"
        };
        for (String seletor : seletores) {
            try {
                WebElement elemento = shortWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(seletor)));
                return elemento.getText().trim();
            } catch (Exception e) {
                // Próximo
            }
        }
        return "Empresa não informada";
    }

    private String extrairLocalizacao() {
        String[] seletores = {".location", ".city", "[class*='location']"};
        for (String seletor : seletores) {
            try {
                WebElement elemento = shortWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(seletor)));
                return elemento.getText().trim();
            } catch (Exception e) {
                // Próximo
            }
        }
        return "Localização não informada";
    }

    private String extrairSalario() {
        String[] seletores = {".salary", "[class*='salary']"};
        for (String seletor : seletores) {
            try {
                WebElement elemento = shortWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(seletor)));
                return elemento.getText().trim();
            } catch (Exception e) {
                // Próximo
            }
        }
        return "Salário não informado";
    }

    private String extrairDescricao() {
        String[] seletores = {
                ".job-description",
                ".offer-description",
                "[class*='description']"
        };
        for (String seletor : seletores) {
            try {
                WebElement elemento = shortWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(seletor)));
                String texto = elemento.getText().trim();
                if (texto.length() > 50) {
                    return texto;
                }
            } catch (Exception e) {
                // Próximo
            }
        }
        return "Descrição não disponível";
    }

    private LocalDate extrairDataPublicacao() {
        String[] seletores = {".date-published", ".publication-date"};
        for (String seletor : seletores) {
            try {
                WebElement elemento = shortWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(seletor)));
                String dataTexto = elemento.getText().trim();
                return parseData(dataTexto);
            } catch (Exception e) {
                // Próximo
            }
        }
        return LocalDate.now();
    }

    private LocalDate parseData(String dataTexto) {
        try {
            String numeros = dataTexto.replaceAll("[^0-9/]", "");
            if (numeros.contains("/")) {
                return LocalDate.parse(numeros, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            }
        } catch (Exception e) {
            // Fallback
        }
        return LocalDate.now();
    }

    public boolean candidatar() {
        try {
            logger.info("Iniciando candidatura para: {}", tituloAtual);

            try {
                WebElement botao = shortWait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//*[@id='VacancyHeader']/div[3]/div[1]/a")
                ));
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", botao);
                botao.click();
                return true;
            } catch (Exception e) {
                logger.debug("XPath específico não encontrado");
            }

            List<WebElement> spans = driver.findElements(By.xpath("//span[contains(text(), 'CANDIDATAR')]"));
            for (WebElement span : spans) {
                if (span.isDisplayed()) {
                    WebElement botao = span.findElement(By.xpath("./ancestor::button"));
                    if (botao.isDisplayed() && botao.isEnabled()) {
                        botao.click();
                        return true;
                    }
                }
            }

            logger.warn("Botão 'CANDIDATAR-ME' não encontrado");
            return false;

        } catch (Exception e) {
            logger.error("Erro durante candidatura: {}", e.getMessage());
            return false;
        }
    }
}

