package selenium.tests;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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
        List<WebElement> allFoundElements = new ArrayList<>();
        for (Map.Entry<String, String> entry : locators.entrySet()) {
            try {
                System.out.println("Trying locator: " + entry.getKey() + " with value: " + entry.getValue());
                List<WebElement> foundElements = context.findElements(By.xpath(entry.getValue()));
                if (foundElements != null && !foundElements.isEmpty()) {
                    System.out.println("Elements found using locator: " + entry.getKey());
                    allFoundElements.addAll(foundElements);
                }
            } catch (InvalidSelectorException e) {
                System.out.println("Invalid selector for locator: " + entry.getKey() + " with value: " + entry.getValue());
            }
        }
        return allFoundElements;
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
    
    public static WebElement waitFor(WebDriver driver, WebElement element) {
    	WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(120));
        return wait.until(ExpectedConditions.visibilityOf(element));

	}
	private static List<WebElement> waitFor(WebDriver driver, List<WebElement> foundElements) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(80));
		return wait.until(ExpectedConditions.visibilityOfAllElements(foundElements));
	}
	
    public static void main(String[] args) {
        // Setup WebDriver
        WebDriver driver = new EdgeDriver();

        // Navigate to the page
        driver.get("https://b24-ddhlno.bitrix24.in/");

        WebElement signinEmail = driver.findElement(By.id("login"));
        signinEmail.sendKeys("Test.mahesh1234@gmail.com");
        
        WebElement signinNext = driver.findElement(By.xpath("//button[text()='Next']"));
        signinNext.click();
        
        // Locate the element
        WebElement element = driver.findElement(By.xpath("//span[@title='Google']"));

        // Wait for element to be present
        waitFor(driver, element);
        
        element.click();
        
        // Get the original window handle
        String originalWindow = driver.getWindowHandle();

        // Get all window handles and store them in an ArrayList
        Set<String> windowHandles = driver.getWindowHandles();
        List<String> windowHandlesList = new ArrayList<>(windowHandles);

        // Switch to the new window using ArrayList index
        driver.switchTo().window(windowHandlesList.get(1));
        System.out.println("Switched to new window: " + windowHandlesList.get(1));
        
        WebElement email = driver.findElement(By.name("identifier"));
        email.sendKeys("Test.mahesh1234@gmail.com");
        
        WebElement next = driver.findElement(By.xpath("//span[text()='Next']"));
        next.click();
        
        // Use the custom ByAll locator
        List<WebElement> foundElements = driver.findElements(ByAll.byAll(element));
        
        waitFor(driver, foundElements);

        // Print the found elements' tag names as proof of correct identification
        for (WebElement foundElement : foundElements) {
            System.out.println("Found element Tag Name: " + foundElement.getText());
        }

        // Close the browser
//        driver.quit();
    }


}
