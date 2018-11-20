import {Component} from '@angular/core';
import {Message, Services} from '../services';
import {LoadingController} from '@ionic/angular';
import {Location} from '@angular/common';
import {processLoading} from "../loading-controller.utils";

@Component({
    selector: 'app-conversation-messages',
    template: `
        <ion-header>
            <ion-toolbar>
                <ion-buttons slot="start">
                    <ion-button (click)="location.back()">
                        <ion-icon name="close"></ion-icon>
                        Back
                    </ion-button>
                </ion-buttons>
                <ion-title>Messages</ion-title>
            </ion-toolbar>
        </ion-header>

        <ion-content>
            <ion-list>
                <ion-item *ngFor="let message of messages">

                </ion-item>
            </ion-list>
        </ion-content>
    `
})
export class ConversationMessagesPage {

    messages: Message[] = [];

    constructor(
        public location: Location,
        loadingController: LoadingController,
        services: Services
    ) {
        processLoading(loadingController, services.getMessagesByConversation(null)).subscribe(
            it => this.messages = it
        );
    }

}
