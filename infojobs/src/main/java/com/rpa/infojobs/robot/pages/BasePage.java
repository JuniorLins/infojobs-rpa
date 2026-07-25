package com.rpa.infojobs.robot.pages;

import jakarta.annotation.PostConstruct;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;

public abstract class BasePage {

    protected static final Logger logger = LoggerFactory.getLogger(BasePage.class);

    @Autowired
    protected WebDriver driver;

    protected WebDriverWait wait;
    protected WebDriverWait shortWait;

    @PostConstruct
    public void init() {
        if (driver == null) {
            logger.error("Driver NÃO FOI INJETADO em: {}", this.getClass().getSimpleName());
        } else {
            this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            this.shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            PageFactory.initElements(driver, this);
            logger.info("Driver injetado com sucesso em: {}", this.getClass().getSimpleName());
        }
    }

    protected void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.debug("Sleep interrompido: {}", e.getMessage());
        }
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public String getPageTitle() {
        return driver.getTitle();
    }
}