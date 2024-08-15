package selenium.utils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ByAll extends By {
    private final Map<String, String> locators;

    public ByAll(WebElement element) {
        this.locators = getElementLocators(element);
    }

    public static ByAll byAll(WebElement element) {
        return new ByAll(element);
    }

    @Override
    public WebElement findElement(SearchContext context) {
        for (Map.Entry<String, String> entry : locators.entrySet()) {
            try {
                System.out.println("Trying locator: " + entry.getKey() + " with value: " + entry.getValue());
                WebElement foundElement = context.findElement(By.xpath(entry.getValue()));
                if (foundElement != null) {
                    System.out.println("Element found using locator: " + entry.getKey());
                    return foundElement;
                }
            } catch (NoSuchElementException e) {
                System.out.println("Element not found using locator: " + entry.getKey());
            } catch (InvalidSelectorException e) {
                System.out.println("Invalid selector for locator: " + entry.getKey() + " with value: " + entry.getValue());
            }
        }
        throw new NoSuchElementException("Element could not be located using any strategy.");
    }

    @Override
    public List<WebElement> findElements(SearchContext context) {
        throw new UnsupportedOperationException("findElements is not implemented");
    }

    @Override
    public String toString() {
        return "ByAll with strategies: " + locators.keySet();
    }

    public static Map<String, String> getElementLocators(WebElement element) {
        Map<String, String> locators = new HashMap<>();

        // Using ID
        String id = element.getAttribute("id");
        if (id != null && !id.isEmpty()) {
            locators.put("id", id);
        }

        // Using Name
        String name = element.getAttribute("name");
        if (name != null && !name.isEmpty()) {
            locators.put("name", name);
        }

        // Using Class
        String className = element.getAttribute("class");
        if (className != null && !className.isEmpty()) {
            // Convert space-separated classes into a CSS selector
            String cssSelector = "." + className.trim().replace(" ", ".");
            locators.put("css", cssSelector);
        }

        // Using Tag Name
        String tagName = element.getTagName();
        if (tagName != null && !tagName.isEmpty()) {
            locators.put("tagName", tagName);
        }

        // Using Link Text (for anchor tags)
        if ("a".equalsIgnoreCase(tagName)) {
            String linkText = element.getText();
            if (linkText != null && !linkText.isEmpty()) {
                locators.put("linkText", linkText);
            }
        }

        // XPath expressions
        if (id != null && !id.isEmpty()) {
            locators.put("xpath", "//*[@id='" + id + "']");
        }

        // XPath for specific tags
        if (id != null && !id.isEmpty()) {
            locators.put("xpath-div", "//div[@id='" + id + "']");
            locators.put("xpath-button", "//button[@id='" + id + "']");
            locators.put("xpath-span", "//span[@id='" + id + "']");
        }

        // XPath by text content
        String textContent = element.getText();
        if (textContent != null && !textContent.isEmpty()) {
            locators.put("xpath-text", "//*[text()='" + textContent + "']");
            locators.put("xpath-text-div", "//div[text()='" + textContent + "']");
            locators.put("xpath-text-button", "//button[text()='" + textContent + "']");
            locators.put("xpath-text-span", "//span[text()='" + textContent + "']");
            locators.put("xpath-text-i", "//i[text()='" + textContent + "']");
            locators.put("xpath-text-li", "//li[text()='" + textContent + "']");
            locators.put("xpath-text-ul", "//ul[text()='" + textContent + "']");
        }

        // XPath using all attributes
        StringBuilder xpathAllAttributes = new StringBuilder("//" + tagName);
        xpathAllAttributes.append("[");
        boolean firstAttribute = true;
        for (String attribute : new String[]{"id", "name", "class", "placeholder", "data-id", "data-lp-id"}) {
            String attrValue = element.getAttribute(attribute);
            if (attrValue != null && !attrValue.isEmpty()) {
                if (!firstAttribute) {
                    xpathAllAttributes.append(" and ");
                }
                xpathAllAttributes.append("@").append(attribute).append("='").append(attrValue).append("'");
                firstAttribute = false;
            }
        }
        xpathAllAttributes.append("]");
        locators.put("xpath-allattributes", xpathAllAttributes.toString());

        return locators;
    }

    public static void main(String[] args) {
        // Setup WebDriver
        WebDriver driver = new ChromeDriver();

        // Explicit wait setup
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Navigate to the page
        driver.get("https://naveenautomationlabs.com/opencart/index.php?route=account/register");

        // Locate the element
        WebElement element = driver.findElement(By.id("input-firstname"));

        // Wait for element to be present
        wait.until(ExpectedConditions.visibilityOf(element));

        // Use the custom ByAll locator
        WebElement foundElement = driver.findElement(ByAll.byAll(element));

        // Print the found element's tag name as proof of correct identification
        System.out.println("Found element Placeholder: " + foundElement.getAttribute("placeholder"));
        
        foundElement.sendKeys("testfnname");
        
        WebElement element1 = driver.findElement(By.id("input-firstname"));

        // Wait for element to be present
        wait.until(ExpectedConditions.visibilityOf(element1));

        // Use the custom ByAll locator
        WebElement foundElement1 = driver.findElement(ByAll.byAll(element1));

        // Print the found element's tag name as proof of correct identification
        System.out.println("Found element Placeholder: " + foundElement1.getText());
        

        // Close the browser
//        driver.quit();
    }
}
