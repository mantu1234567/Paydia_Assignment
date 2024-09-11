package com.example.FetchApi.controller;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@RestController
public class NewsController {

    @GetMapping("/news")
    public List<NewsArticle> getNews(@RequestParam String query) {
        WebDriverManager.chromedriver().setup();

        // Setup headless mode for performance
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        WebDriver driver = new ChromeDriver(options);

        List<NewsArticle> articles = new ArrayList<>();

        try {
            // Load Google News and search
            driver.get("https://news.google.com/");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Find and interact with the search bar
            WebElement searchIcon = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[aria-label='Search']")));
            searchIcon.click();

            WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@aria-label='Search']")));
            searchBox.sendKeys(query);
            searchBox.submit();

            // Wait for results to load
            Thread.sleep(2000); // Replace with a better wait if needed

            // Collect URLs from search results
            List<WebElement> results = driver.findElements(By.cssSelector("article h3 a"));

            for (WebElement result : results) {
                String url = result.getAttribute("href");

                // Check if the result contains a valid URL
                if (url != null && !url.isEmpty()) {
                    articles.add(new NewsArticle(url));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }

        return articles;
    }

    // NewsArticle class to represent articles
    public static class NewsArticle {
        private String url;

        public NewsArticle(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
