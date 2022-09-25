package practise;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.bonigarcia.wdm.WebDriverManager;

public class SampleGraphAutomation {

	public static final String filepath = "C:\\Users\\User\\Downloads\\Selenium_Maven\\target\\SampleDocs\\Output.txt";
	public static final String jsonFilePath = "C:\\Users\\User\\Downloads\\Selenium_Maven\\src\\resources\\sample.json";
	public static final String complexJsonFilePath = "C:\\Users\\User\\Downloads\\Selenium_Maven\\src\\resources\\complex.json";
	public static final String jsonDumpFilePath = "C:\\Users\\User\\Downloads\\Selenium_Maven\\target\\SampleDocs\\jsonDump.txt";

	public static void iterateList(List<Map<String, Object>> jsonList) throws IOException {
		for (Map<String, Object> map : jsonList) {
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				log(jsonDumpFilePath, entry.getKey() + " ------> " + entry.getValue().toString());
			}
			log(jsonDumpFilePath, "----------------------------------------------------------------");

		}
		log(jsonDumpFilePath, "----------------------------------------------------------------");
	}

	public static void log(String jsonDump, String output) throws IOException {
		PrintWriter out = null;
		try {
			out = new PrintWriter(new FileWriter(jsonDump, true), true);
			out.println(output);

		} catch (IOException e) {
			System.out.println(e.getMessage());
			throw e;
		} finally {
			out.close();
		}
	}

	public static String extractDataFromJsonFile() throws IOException {
		String sampleJsonFile = FileUtils.readFileToString(new File(jsonFilePath), StandardCharsets.UTF_8);
		String jsonFile = FileUtils.readFileToString(new File(complexJsonFilePath), StandardCharsets.UTF_8);
		// For Simple Json or Array of JSON Only

		ObjectMapper mapper = new ObjectMapper();
		TypeReference<List<Map<String, Object>>> typeRef = new TypeReference<List<Map<String, Object>>>() {
		};
		List<Map<String, Object>> result = mapper.readValue(sampleJsonFile, typeRef);
		iterateList(result);

		// for complex json
		JSONObject json = new JSONObject(jsonFile);
		JSONArray data = json.getJSONArray("data");
		List<Map<String, Object>> jsonList = new LinkedList<Map<String, Object>>();
		log(jsonDumpFilePath, "Totally " + data.length() + " json objects are present in entries json array");
		for (int i = 0; i < data.length(); i++) {
			JSONObject jsonObject = (JSONObject) data.get(i);
			Set<String> keys = jsonObject.keySet();
			Map<String, Object> jsonMap = new LinkedHashMap<String, Object>();
			keys.forEach(key -> jsonMap.put(key, jsonObject.get(key)));
			jsonList.add(jsonMap);
		}

		iterateList(jsonList);
		JSONObject country = json.getJSONObject("country");
		String countryName = country.get("countryname").toString();
		log(jsonDumpFilePath, countryName);
		return countryName;
	}

	public static void scrollElementByAmount(WebDriver driver, JavascriptExecutor jse, int scrollBy)
			throws InterruptedException {
		WebElement countryScrollPane = driver.findElement(By.xpath("(//div[contains(@class,'RCU5Uc FPVe3d')])[1]"));
		String script = "document.querySelector(\"div.RCU5Uc.FPVe3d\").scrollTo({top:arguments[0].scrollHeight/arguments[1], behavior: 'smooth'})";
		jse.executeScript(script, countryScrollPane, scrollBy);
		Thread.sleep(3000);
	}

	public static void log(String output) throws IOException {
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

	public static void automateEmiCalculator(WebDriver driver, Actions actions, JavascriptExecutor jse,
			String parentWindow, WebDriverWait wait) throws IOException, InterruptedException {
		jse.executeScript("window.open('https://emicalculator.net/','_blank')");
		Set<String> windowHandles = driver.getWindowHandles();

		for (String window : windowHandles) {
			if (!window.equalsIgnoreCase(parentWindow)) {
				driver.switchTo().window(window);
				WebElement graph = wait
						.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div#emibarchart")));
				actions.moveToElement(graph).perform();
				jse.executeScript("arguments[0].scrollIntoView();", graph);
				WebElement toolTip = driver.findElement(By.xpath(
						"(//*[local-name()='svg' and @class='highcharts-root'])[2]//*[name()='text' and @x='8']"));
				List<WebElement> dataPoints = driver.findElements(By.xpath(
						"(//*[local-name()='svg' and @class='highcharts-root'])[2]//*[name()='g' and @class='highcharts-markers highcharts-series-2 highcharts-spline-series highcharts-tracker']//*[name()='path']"));
				log("=============DATA POINTS TEXT================");
				log("---------------------------------------------");
				wait.until(ExpectedConditions.visibilityOfAllElements(dataPoints));
				for (WebElement dataPoint : dataPoints) {
					actions.moveToElement(dataPoint).perform();
					log(toolTip.getText());
					Thread.sleep(2000);
				}
				actions.moveToElement(graph).perform();
				List<WebElement> orangeBars = driver.findElements(By.xpath(
						"((//*[local-name()='svg' and @class='highcharts-root'])[2]//*[name()='g' and @class='highcharts-series-group']//*[name()='g' and @opacity='1'])[1]//*[name()='rect']"));
				wait.until(ExpectedConditions.visibilityOfAllElements(orangeBars));
				log("=============ORANGE BARS TEXT================");
				log("---------------------------------------------");
				for (WebElement orangeBar : orangeBars) {
					actions.moveToElement(orangeBar).perform();
					log(toolTip.getText());
					Thread.sleep(2000);

				}
				List<WebElement> greenBars = driver.findElements(By.xpath(
						"((//*[local-name()='svg' and @class='highcharts-root'])[2]//*[name()='g' and @class='highcharts-series-group']//*[name()='g' and @opacity='0.2'])[1]//*[name()='rect']"));
				wait.until(ExpectedConditions.visibilityOfAllElements(greenBars));
				log("=============GREEN BARS TEXT================");
				log("---------------------------------------------");
				for (WebElement greenBar : greenBars) {
					actions.moveToElement(greenBar).perform();
					log(toolTip.getText());
					Thread.sleep(2000);

				}

			}

		}

	}

	public static void main(String[] args) throws InterruptedException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = new PrintWriter(new FileWriter(filepath, false), true);
		out.print(" ");
		WebDriverManager.chromedriver().setup();
		WebDriver driver = new ChromeDriver();
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofMinutes(5));
		Actions actions = new Actions(driver);
		JavascriptExecutor jse = ((JavascriptExecutor) driver);
		driver.get("https://www.google.com/");
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(Duration.ofMinutes(2));
		String country = extractDataFromJsonFile();
		String parentWindow = driver.getWindowHandle();
		automateEmiCalculator(driver, actions, jse, parentWindow, wait);
		driver.getWindowHandles().forEach(window -> {
			if (window.equalsIgnoreCase(parentWindow))
				driver.switchTo().window(parentWindow);
		});
		searchOnGoogle(driver, "covid cases in India");
		scrollBottomToVaccinationsGraph(driver, jse, actions, wait);
		for (int i = 4; i >= 1; i--) {
			scrollElementByAmount(driver, jse, i);
			Thread.sleep(3000);
			driver.findElement(By.xpath("(//div[@jsname='sxikpb'])[2]")).click();
			scrollBottomToVaccinationsGraph(driver, jse, actions, wait);
		}
		country = selectCountry(driver, wait, country);
		log("Vaccinations data for country " + country);
		fetchVaccinationsData(driver, actions);

		// WebElement searchBox=driver.findElement(By.xpath("(//input[@aria-label='type
		// to filter locations'])[4]"));
		// Getting ElementNotInteractble Exception Have to check

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

	public static void scrollBottomToVaccinationsGraph(WebDriver driver, JavascriptExecutor jse, Actions actions,
			WebDriverWait wait) throws InterruptedException {
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
		List<WebElement> points = driver.findElements(By.xpath("(//div[@class='uch-xa'])[2]/child::span"));
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
