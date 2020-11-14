import {browser, By} from 'protractor';
import {waitPresenceOfAndFindElements} from '../protractor-utils';

export class ConversationPage {
    getPageTitle() {
        return browser.getTitle();
    }

    getToolbarTitle() {
        return browser.findElement(By.css('ion-toolbar > ion-title')).getText();
    }

    getConversations() {
        return waitPresenceOfAndFindElements(By.css('ion-list > ion-item'));
    }
}
