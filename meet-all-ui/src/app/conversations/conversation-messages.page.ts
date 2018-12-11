import {Component} from '@angular/core';
import {LoadingController} from '@ionic/angular';
import {Location} from '@angular/common';
import {processLoading} from '../loading-controller.utils';
import {ConversationsService, Message, Profile} from './conversations.service';
import {forkJoin} from 'rxjs';
import {tap} from 'rxjs/operators';
import {ActivatedRoute} from '@angular/router';

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
                <div style="display: flex;">
                    <ion-avatar>
                        <img *ngIf="profile != null" [src]="profile.avatars[0]">
                    </ion-avatar>
                    <ion-title *ngIf="profile != null">{{profile.name}}</ion-title>
                </div>
            </ion-toolbar>
        </ion-header>

        <ion-content>
            <ion-list lines="none">
                <ion-item *ngFor="let message of messages">
                    <ion-avatar *ngIf="!message.sent">
                        <img *ngIf="profile != null" [src]="profile.avatars[0]">
                    </ion-avatar>
                    <ion-text [slot]="message.sent ? 'end' : ''"
                              [ngClass]="['message', message.sent ? 'sent' : 'received']">{{message.text}}</ion-text>
                </ion-item>
            </ion-list>
        </ion-content>
    `,
    styles: [
            `
            .message {
                max-width: 75%;
                white-space: pre-wrap;
            }

            .sent {
                background-color: rgb(0, 162, 216)
            }

            .received {
                background-color: rgb(225, 225, 225);
            }
        `
    ]
})
export class ConversationMessagesPage {

    profile: Profile = null;
    messages: Message[] = null;

    constructor(
        route: ActivatedRoute,
        public location: Location,
        loadingController: LoadingController,
        service: ConversationsService,
    ) {
        let conversationId = route.snapshot.paramMap.get('conversationId');
        console.log('conversationId', conversationId);
        let profileId = route.snapshot.paramMap.get('profileId');
        console.log('profileId', profileId);
        processLoading(loadingController, forkJoin(
            service.getMessagesByConversation(conversationId)
                .pipe(tap(it => this.messages = it)),
            service.getProfile(profileId)
                .pipe(tap(it => this.profile = it)),
        )).subscribe();
    }

}
