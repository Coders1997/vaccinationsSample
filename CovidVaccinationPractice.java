package practise;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import io.github.bonigarcia.wdm.WebDriverManager;

public class CovidVaccinationPractice {

	public static void log(String output) throws IOException {
		String filepath = "C:\\Users\\User\\Downloads\\Selenium_Maven\\target\\SampleDocs\\VaccinationSample.txt";
		PrintWriter out = null;
		try {
			out = new PrintWriter(new FileWriter(filepath, true), true);
			out.println(output);

		} catch (IOException e) {
			System.out.println(e.getMessage());
			throw e;
		} finally {
			out.close();
		}
	}

	public static void main(String[] args) throws InterruptedException, IOException {
		// TODO Auto-generated method stub

	     WebDriverManager.chromedriver().setup();
		//System.setProperty("webdriver.chrome.driver", "C:\\Users\\User\\Documents\\ChromeDriver\\chromedriver.exe");
		WebDriver driver = new ChromeDriver();
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofMinutes(3));
		Actions actions = new Actions(driver);
		JavascriptExecutor jse = ((JavascriptExecutor) driver);
		driver.get("https://www.google.com/");
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(Duration.ofMinutes(2));
		String country = "United Kingdom";
		searchOnGoogle(driver, "covid cases in India");
		scrollBottomToVaccinationsGraph(driver, jse, actions, wait);
		country = selectCountry(driver, wait, country);
		log("Vaccinations data for country " + country);
		fetchVaccinationsData(driver, actions);

		// WebElement searchBox=driver.findElement(By.xpath("(//input[@aria-label='type
		// to filter locations'])[4]"));
		// Getting ElementNotInteractble Exception Have to check

		WebElement countryScrollPane = driver.findElement(By.xpath("(//div[contains(@class,'RCU5Uc FPVe3d')])[1]"));
		Thread.sleep(5000);
		// actions.moveToElement(countryScrollPane).clickAndHold().moveByOffset(0,
		// 75).release().perform();

	}

	public static String selectCountry(WebDriver driver, WebDriverWait wait, String countryText) {
		try {
			List<WebElement> countries = driver.findElements(By.xpath("//div[@jsname='Bi1nfb']/child::div[2]"));
			for (WebElement country : countries) {
				if (country.getText().equalsIgnoreCase(countryText)) {
					wait.ignoring(StaleElementReferenceException.class)
							.until(ExpectedConditions.elementToBeClickable(country)).click();
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());

		}
		WebElement countryValidation = driver.findElement(By.xpath(
				"//div[text()='Vaccinations']/ancestor::div[contains(@jsname,'Gp1doc')] /descendant::div[contains(@aria-label,'country')] //div[@jsname='c6cQV']"));
		if (countryValidation.getText().equalsIgnoreCase(countryText)) {
			WebElement vaccinationsGraph = wait.until(ExpectedConditions
					.presenceOfElementLocated(By.xpath("(//*[local-name()='svg' and @class='uch-psvg'])[2]")));
			Assert.assertTrue(vaccinationsGraph.isDisplayed() && vaccinationsGraph.isEnabled());
		}
		return countryText;
	}

	public static void searchOnGoogle(WebDriver driver, String search) throws InterruptedException {
		driver.findElement(By.xpath("//input[@name='q']")).sendKeys(search);
		Thread.sleep(5000);
		List<WebElement> searchlist = driver.findElements(By.cssSelector("div.wM6W7d span"));
		System.out.println(searchlist.size() + " options present in search list");
		searchlist.stream().filter(element -> element.getText().equalsIgnoreCase("covid cases in india"))
				.collect(Collectors.toList()).get(0).click();
		Thread.sleep(10000);
	}

	public static void scrollBottomToVaccinationsGraph(WebDriver driver, JavascriptExecutor jse, Actions actions, WebDriverWait wait)
			throws InterruptedException {
		WebElement element = driver.findElement(By.xpath(
				"//div[contains(text(),'Vaccinations')]/ancestor::div[contains(@class,'TzHB6b cLjAic')]/descendant::div[@jsaction='yjWrye' and contains(@aria-label,'country')]//div[@jsname='c6cQV']"));
		wait.until(ExpectedConditions.elementToBeClickable(element));

		actions.moveToElement(element).build().perform();
		jse.executeScript("arguments[0].scrollIntoView();", element);
		Thread.sleep(7000);
		jse.executeScript("arguments[0].click();", element);

		Thread.sleep(2000);
	}

	public static void fetchVaccinationsData(WebDriver driver, Actions actions) throws IOException {
		List<WebElement> points = driver.findElements(By.xpath("(//div[@class='uch-xa'])[2]/span"));
		for (WebElement point : points) {
			actions.moveToElement(point).perform();
			// Thread.sleep(10000);
			WebElement date = driver.findElement(By.xpath(
					"//div[@jsname='iXWWee']/following-sibling::div[@data-chart-mode='TOTAL_WITH_DOSAGES']/descendant::table[@class='F9Gkq']/descendant::tr[@class='TsQP2e']/child::td[@jsname='hXZdoc']"));
			WebElement total = driver.findElement(By.xpath(
					"//div[@jsname='iXWWee']/following-sibling::div[@data-chart-mode='TOTAL_WITH_DOSAGES']/descendant::table[@class='F9Gkq']/descendant::tr[@class='TsQP2e']/child::td[@class='sRjd3d'][1]"));
			WebElement percentage = driver.findElement(By.xpath(
					"//div[@jsname='iXWWee']/following-sibling::div[@data-chart-mode='TOTAL_WITH_DOSAGES']/descendant::table[@class='F9Gkq']/descendant::tr[@class='TsQP2e']/child::td[@class='sRjd3d'][2]"));
			WebElement singleDose = driver.findElement(By.xpath(
					"//div[@jsname='iXWWee']/following-sibling::div[@data-chart-mode='TOTAL_WITH_DOSAGES']/descendant::table[@class='F9Gkq']/descendant::tr[2]/td[1]"));
			WebElement singleDoseTotal = driver.findElement(By.xpath(
					"//div[@jsname='iXWWee']/following-sibling::div[@data-chart-mode='TOTAL_WITH_DOSAGES']/descendant::table[@class='F9Gkq']/descendant::tr[2]/td[@jsname='yiH27c']"));
			WebElement singleDosePercentage = driver.findElement(By.xpath(
					"//div[@jsname='iXWWee']/following-sibling::div[@data-chart-mode='TOTAL_WITH_DOSAGES']/descendant::table[@class='F9Gkq']/descendant::tr[2]/td[@jsname='wcnVif']"));
			WebElement fullyVaccinated = driver.findElement(By.xpath(
					"//div[@jsname='iXWWee']/following-sibling::div[@data-chart-mode='TOTAL_WITH_DOSAGES']/descendant::table[@class='F9Gkq']/descendant::tr[3]/td[1]"));
			WebElement fullyVaccinatedTotal = driver.findElement(By.xpath(
					"//div[@jsname='iXWWee']/following-sibling::div[@data-chart-mode='TOTAL_WITH_DOSAGES']/descendant::table[@class='F9Gkq']/descendant::tr[3]/td[@jsname='F7kuqc']"));
			WebElement fullyVaccinatedPercentage = driver.findElement(By.xpath(
					"//div[@jsname='iXWWee']/following-sibling::div[@data-chart-mode='TOTAL_WITH_DOSAGES']/descendant::table[@class='F9Gkq']/descendant::tr[3]/td[@jsname='dCwlvc']"));
			log("Date :" + date.getText() + "     " + total.getText() + " Vaccinations" + "     "
					+ percentage.getText());
			log(singleDose.getText() + "         " + singleDoseTotal.getText() + "                        "
					+ singleDosePercentage.getText());
			log(fullyVaccinated.getText() + "         " + fullyVaccinatedTotal.getText() + "                          "
					+ fullyVaccinatedPercentage.getText());

		}

	}

}
