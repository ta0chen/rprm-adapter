package com.polycom.sqa.utils;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SeleniumUtil {
	/**
	 * wait timeout seconds until element found clickable through by. 
	 * @param driver
	 * @param by
	 * @param timeOutInSeconds
	 * @return found element or null if timeout
	 */
	public static WebElement waitForElementClickable(WebDriver driver, By by, int timeOutInSeconds) {
		WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
		return wait.until(ExpectedConditions.elementToBeClickable(by));
	}
	
	/**
	 * waitforElementClickable with default timeout 10 seconds
	 * @param driver ebDriver
	 * @param webElement By
	 * @return found element or null if timeout
	 */
	public static WebElement waitForElementClickable(WebDriver driver, By by) {
		return SeleniumUtil.waitForElementClickable(driver, by, 10);
	}
	
	/**
	 * wait timeout seconds until element found clickable through element
	 * @param driver
	 * @param element
	 * @param timeOutInSeconds
	 * @return found element or null if timeout
	 */
	public static WebElement waitForElementClickable(WebDriver driver, WebElement element, int timeOutInSeconds) {
		WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
		return wait.until(ExpectedConditions.elementToBeClickable(element));
	}
	
	/**
	 * waitforElementClickable with default timeout 10 seconds
	 * @param driver
	 * @param element
	 * @return found element or null if timeout
	 */
	public static WebElement waitForElementClickable(WebDriver driver, WebElement element) {
		return SeleniumUtil.waitForElementClickable(driver, element, 10);
	}
	/**
	 * waitforElementClickable with default timeout 10 seconds
	 * @param driver
	 * @param element
	 * @return found element or null if timeout
	 */
	public static Alert waitForElementAlertIsPresent (WebDriver driver, int timeOutInSeconds)  {
		WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
		return wait.until(ExpectedConditions.alertIsPresent());
	}
		
}
