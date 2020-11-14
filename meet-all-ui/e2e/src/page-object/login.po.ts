import {browser, By} from 'protractor';

export class LoginPage {
    getPageTitle() {
        return browser.getTitle();
    }

    getToolbarTitle() {
        return browser.findElement(By.css('ion-toolbar > ion-title')).getText();
    }
}
