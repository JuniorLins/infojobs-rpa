package com.rpa.infojobs.robot.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class SearchPage extends BasePage {

    @Value("${busca.termos.principal:Programador Jr}")
    private String termoPrincipal;

    @Value("${busca.termos.fallback1:Desenvolvedor Jr}")
    private String termoFallback1;

    @Value("${busca.termos.fallback2:Analista de Sistemas Jr}")
    private String termoFallback2;

    @Value("${busca.termos.fallback3:Dev Jr}")
    private String termoFallback3;

    @Value("${busca.localizacao:São Paulo}")
    private String localizacao;

    // SELETOR NOVO: Baseado no HTML atual do InfoJobs
    private static final String SELETOR_CARDS = "div.js_rowCard";

    public void buscarVagasComFallback() {
        List<String> termos = Arrays.asList(
                termoPrincipal,
                termoFallback1,
                termoFallback2,
                termoFallback3
        );

        logger.info("Iniciando busca com fallback...");

        for (String termo : termos) {
            logger.info("Tentando buscar por: '{}'", termo);

            if (!estaNaPaginaDeBusca()) {
                irParaPaginaBusca();
            }

            buscarVagasComTermos(termo, localizacao);

            if (temResultados()) {
                logger.info("ENCONTRADO! Termo '{}' retornou {} vagas", termo, getNumeroVagasPagina());
                return;
            }
        }

        logger.error("Nenhum termo encontrou resultados!");
    }

    public void buscarVagasComTermos(String termos, String local) {
        logger.info("Buscando: {} em {}", termos, local);
        try {
            fecharPopupCookiesSeExistir();

            WebElement campoBusca = encontrarCampoBusca();
            if (campoBusca != null) {
                wait.until(ExpectedConditions.elementToBeClickable(campoBusca));
                campoBusca.clear();
                campoBusca.sendKeys(termos);
            } else {
                buscarPorUrlDireta(termos, local);
                return;
            }

            WebElement campoLocal = encontrarCampoLocalizacao();
            if (campoLocal != null) {
                wait.until(ExpectedConditions.elementToBeClickable(campoLocal));
                campoLocal.clear();
                campoLocal.sendKeys(local);
            }

            WebElement botaoBuscar = encontrarBotaoBuscar();
            if (botaoBuscar != null) {
                wait.until(ExpectedConditions.elementToBeClickable(botaoBuscar));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", botaoBuscar);
            } else if (campoBusca != null) {
                campoBusca.sendKeys(Keys.ENTER);
            }

            // Espera os cards corretos aparecerem
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(SELETOR_CARDS)));
            fecharPopupCookiesSeExistir();

        } catch (Exception e) {
            logger.error("Erro na busca via interface: {}", e.getMessage());
            buscarPorUrlDireta(termos, local);
        }
    }

    private void buscarPorUrlDireta(String termo, String local) {
        try {
            // A URL correta do InfoJobs hoje é com .aspx e parâmetros palavra/provincia
            String url = String.format(
                    "https://www.infojobs.com.br/empregos.aspx?palabra=%s&provincia=64", // 64 = SP
                    termo.replace(" ", "+")
            );
            logger.info("Buscando via URL direta: {}", url);
            driver.get(url);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(SELETOR_CARDS)));
            fecharPopupCookiesSeExistir();
        } catch (Exception e) {
            logger.error("Erro na busca por URL direta: {}", e.getMessage());
        }
    }

    private WebElement encontrarCampoBusca() {
        String[] seletoresCSS = {
                "input[name='palabra']",
                "input[id='palabra']",
                "input[placeholder*='cargo' i]",
                "input[class*='search']"
        };
        for (String seletor : seletoresCSS) {
            try {
                WebElement elemento = driver.findElement(By.cssSelector(seletor));
                if (elemento.isDisplayed() && elemento.isEnabled()) return elemento;
            } catch (Exception ignored) {}
        }
        return null;
    }

    private WebElement encontrarCampoLocalizacao() {
        String[] seletores = {
                "input[name='provincia']",
                "input[id='provincia']",
                "input[placeholder*='local' i]"
        };
        for (String seletor : seletores) {
            try {
                WebElement elemento = driver.findElement(By.cssSelector(seletor));
                if (elemento.isDisplayed() && elemento.isEnabled()) return elemento;
            } catch (Exception ignored) {}
        }
        return null;
    }

    private WebElement encontrarBotaoBuscar() {
        String[] seletores = {
                "button[type='submit']",
                "button[class*='search']",
                "button[id='btnSearch']"
        };
        for (String seletor : seletores) {
            try {
                List<WebElement> botoes = driver.findElements(By.cssSelector(seletor));
                for (WebElement botao : botoes) {
                    if (botao.isDisplayed() && botao.isEnabled()) return botao;
                }
            } catch (Exception ignored) {}
        }
        return null;
    }

    // Metodo Atualizado: Filtra os cards duplicados
    private List<WebElement> encontrarListaVagas() {
        try {
            List<WebElement> elementos = driver.findElements(By.cssSelector(SELETOR_CARDS));
            List<WebElement> filtrados = new ArrayList<>();
            java.util.Set<String> urlsVistas = new java.util.HashSet<>();

            for (WebElement el : elementos) {
                if (!el.isDisplayed()) continue;

                // Pega a URL para evitar duplicatas
                String href = el.getAttribute("data-href");
                if (href == null || href.isEmpty()) {
                    try {
                        href = el.findElement(By.cssSelector("a")).getAttribute("href");
                    } catch (Exception ignored) {}
                }

                // Se a URL for válida e ainda não foi vista, adiciona à lista
                if (href != null && !href.isEmpty() && !urlsVistas.contains(href)) {
                    urlsVistas.add(href);
                    filtrados.add(el);
                }
            }

            if (!filtrados.isEmpty()) {
                logger.info("Lista de vagas filtrada: {} elementos únicos", filtrados.size());
                return filtrados;
            }
        } catch (Exception e) {
            logger.warn("Erro ao buscar elementos: {}", e.getMessage());
        }
        return new ArrayList<>();
    }

    public boolean temResultados() {
        try {
            // Verifica se há mensagem de "nenhum resultado"
            try {
                WebElement mensagem = driver.findElement(
                        By.xpath("//*[contains(text(), 'Nenhuma vaga') or contains(text(), 'Sem resultados') or contains(text(), 'não encontrou')]")
                );
                if (mensagem.isDisplayed()) {
                    return false;
                }
            } catch (Exception ignored) {}

            List<WebElement> vagas = encontrarListaVagas();
            return !vagas.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public int getNumeroVagasPagina() {
        return encontrarListaVagas().size();
    }

    public boolean estaNaPaginaDeBusca() {
        try {
            String url = driver.getCurrentUrl().toLowerCase();
            return url.contains("empregos") || url.contains("vagas") || url.contains("busca");
        } catch (Exception e) {
            return false;
        }
    }

    public void irParaPaginaBusca() {
        try {
            if (estaNaPaginaDeBusca()) return;

            driver.get("https://www.infojobs.com.br/empregos.aspx");
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(SELETOR_CARDS)));
            fecharPopupCookiesSeExistir();
        } catch (Exception e) {
            logger.error("Erro ao navegar para página de busca: {}", e.getMessage());
        }
    }

    public void voltarParaLista() {
        try {
            ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
            if (tabs.size() > 1) {
                driver.close();
                driver.switchTo().window(tabs.get(0));
            } else {
                driver.navigate().back();
            }
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(SELETOR_CARDS)));
            fecharPopupCookiesSeExistir();
        } catch (Exception e) {
            logger.error("Erro ao voltar para lista: {}", e.getMessage());
        }
    }

    // MÉTODO ATUALIZADO: Espera a URL da nova aba carregar
    public void clicarNaVaga(int indice) {
        try {
            List<WebElement> cards = encontrarListaVagas();

            if (cards.isEmpty() || indice >= cards.size()) {
                return;
            }

            WebElement card = cards.get(indice);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", card);
            sleep(500);

            String urlVaga = card.getAttribute("data-href");
            if (urlVaga == null || urlVaga.isEmpty()) {
                try {
                    WebElement link = card.findElement(By.cssSelector("a"));
                    urlVaga = link.getAttribute("href");
                } catch (Exception ignored) {}
            }

            if (urlVaga != null && !urlVaga.isEmpty()) {
                if (!urlVaga.startsWith("http")) {
                    urlVaga = "https://www.infojobs.com.br" + urlVaga;
                }
                logger.info("Abrindo vaga [{}] na URL: {}", indice, urlVaga);

                // Abre a nova aba
                ((JavascriptExecutor) driver).executeScript("window.open(arguments[0], '_blank');", urlVaga);
                sleep(1500);

                ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
                if (tabs.size() > 1) {
                    driver.switchTo().window(tabs.get(tabs.size() - 1));

                    // O SEGREDO ESTÁ AQUI: Espera a URL conter "vaga" ou "oferta" para prosseguir
                    wait.until(ExpectedConditions.or(
                            ExpectedConditions.urlContains("vaga"),
                            ExpectedConditions.urlContains("oferta")
                    ));
                    logger.info("Nova aba carregada: {}", driver.getCurrentUrl());
                }
            }
        } catch (Exception e) {
            logger.error("Erro ao clicar na vaga {}: {}", indice, e.getMessage());
        }
    }

    private void fecharPopupCookiesSeExistir() {
        try {
            WebElement popup = driver.findElement(By.id("didomi-popup"));
            if (popup.isDisplayed()) {
                ((JavascriptExecutor) driver).executeScript("document.getElementById('didomi-popup').remove();");
                sleep(500);
            }
        } catch (Exception ignored) {}
    }
}