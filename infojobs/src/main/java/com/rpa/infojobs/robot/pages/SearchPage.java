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
import java.util.stream.Collectors;

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

            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("article.job-item, div.offer-item, div.job-card")));
            fecharPopupCookiesSeExistir();

        } catch (Exception e) {
            logger.error("Erro na busca: {}", e.getMessage());
            buscarPorUrlDireta(termos, local);
        }
    }

    private void buscarPorUrlDireta(String termo, String local) {
        try {
            String url = String.format(
                    "https://www.infojobs.com.br/empregos?keyword=%s&location=%s",
                    termo.replace(" ", "+"),
                    local.replace(" ", "+")
            );
            driver.get(url);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("article.job-item, div.offer-item, div.job-card")));
            fecharPopupCookiesSeExistir();
        } catch (Exception e) {
            logger.error("Erro na busca por URL direta: {}", e.getMessage());
        }
    }

    private WebElement encontrarCampoBusca() {
        String[] seletoresCSS = {
                "input[name='keyword']",
                "input[name='q']",
                "input[placeholder*='cargo' i]",
                "input[class*='search']"
        };

        for (String seletor : seletoresCSS) {
            try {
                WebElement elemento = driver.findElement(By.cssSelector(seletor));
                if (elemento.isDisplayed() && elemento.isEnabled()) {
                    return elemento;
                }
            } catch (Exception e) {
                // Próximo
            }
        }
        return null;
    }

    private WebElement encontrarCampoLocalizacao() {
        String[] seletores = {
                "input[name='location']",
                "input[placeholder*='local' i]",
                "input[class*='location']"
        };

        for (String seletor : seletores) {
            try {
                WebElement elemento = driver.findElement(By.cssSelector(seletor));
                if (elemento.isDisplayed() && elemento.isEnabled()) {
                    return elemento;
                }
            } catch (Exception e) {
                // Próximo
            }
        }
        return null;
    }

    private WebElement encontrarBotaoBuscar() {
        String[] seletores = {
                "button[type='submit']",
                "button[class*='search']",
                "input[type='submit']"
        };

        for (String seletor : seletores) {
            try {
                List<WebElement> botoes = driver.findElements(By.cssSelector(seletor));
                for (WebElement botao : botoes) {
                    if (botao.isDisplayed() && botao.isEnabled()) {
                        return botao;
                    }
                }
            } catch (Exception e) {
                // Próximo
            }
        }
        return null;
    }

    private List<WebElement> encontrarListaVagas() {
        String[] seletores = {
                "article.job-item",
                "div.offer-item",
                "div.job-card",
                "div[class*='job']",
                "div[class*='vaga']"
        };

        for (String seletor : seletores) {
            try {
                List<WebElement> elementos = driver.findElements(By.cssSelector(seletor));
                if (!elementos.isEmpty()) {
                    return elementos;
                }
            } catch (Exception e) {
                // Próximo
            }
        }
        return new ArrayList<>();
    }

    public boolean temResultados() {
        try {
            try {
                WebElement mensagem = driver.findElement(
                        By.xpath("//*[contains(text(), 'Nenhuma vaga') or contains(text(), 'Sem resultados')]")
                );
                if (mensagem.isDisplayed()) {
                    return false;
                }
            } catch (Exception e) {
                // OK
            }

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
            return url.contains("empregos") || url.contains("vagas") || url.contains("busca") || url.contains("jobs");
        } catch (Exception e) {
            return false;
        }
    }

    public void irParaPaginaBusca() {
        try {
            if (estaNaPaginaDeBusca()) {
                return;
            }

            String[] urlsBusca = {
                    "https://www.infojobs.com.br/empregos.aspx",
                    "https://www.infojobs.com.br/vagas",
                    "https://www.infojobs.com.br/empregos"
            };

            for (String url : urlsBusca) {
                try {
                    driver.get(url);
                    wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("article.job-item, div.offer-item, div.job-card")));
                    if (estaNaPaginaDeBusca()) {
                        fecharPopupCookiesSeExistir();
                        return;
                    }
                } catch (Exception e) {
                    logger.debug("Falha ao acessar: {}", url);
                }
            }
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

            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("article.job-item, div.offer-item, div.job-card")));
            fecharPopupCookiesSeExistir();
        } catch (Exception e) {
            logger.error("Erro ao voltar para lista: {}", e.getMessage());
        }
    }

    public void clicarNaVaga(int indice) {
        try {
            List<WebElement> linksVagas = driver.findElements(By.cssSelector(
                    "h2 a[href*='oferta'], h2 a[href*='vaga'], a[href*='oferta'], a[href*='vaga']"
            ));

            if (linksVagas.isEmpty()) {
                linksVagas = driver.findElements(By.xpath(
                        "//a[contains(@href, 'oferta') or contains(@href, 'vaga')]"
                ));
            }

            List<WebElement> linksFiltrados = linksVagas.stream()
                    .filter(link -> !link.getText().trim().isEmpty())
                    .collect(Collectors.toList());

            if (linksFiltrados.isEmpty() || indice >= linksFiltrados.size()) {
                logger.warn("Link inválido");
                return;
            }

            WebElement linkVaga = linksFiltrados.get(indice);
            String urlVaga = linkVaga.getAttribute("href");

            ((JavascriptExecutor) driver).executeScript("window.open(arguments[0], '_blank');", urlVaga);

            ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
            if (tabs.size() > 1) {
                driver.switchTo().window(tabs.get(tabs.size() - 1));
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("h2.h3.font-weight-bold.text-body")));
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
                sleep(1000);
            }
        } catch (Exception e) {
            // OK
        }
    }
}
