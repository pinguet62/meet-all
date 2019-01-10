import {Component, ViewChild} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {IonContent, LoadingController} from '@ionic/angular';
import {forkJoin} from 'rxjs';
import {delay, finalize, tap} from 'rxjs/operators';
import {processLoading} from '../../loading-controller.utils';
import {ConversationsService, Message, Profile} from '../conversations.service';

@Component({
    selector: 'app-conversation-messages',
    template: `
        <ion-header>
            <ion-toolbar>
                <ion-buttons slot="start">
                    <ion-back-button></ion-back-button>
                </ion-buttons>
                <div style="display: flex;">
                    <ion-avatar>
                        <img *ngIf="profile != null" [proxifiedSrc]="profile.avatars[0]">
                    </ion-avatar>
                    <ion-title *ngIf="profile != null">{{profile.name}}</ion-title>
                </div>
            </ion-toolbar>
        </ion-header>

        <ion-content>
            <ion-list lines="none">
                <ion-item *ngFor="let message of messages">
                    <ion-avatar *ngIf="!message.sent">
                        <img *ngIf="profile != null" [proxifiedSrc]="profile.avatars[0]">
                    </ion-avatar>
                    <ion-text [slot]="message.sent ? 'end' : ''"
                              [ngClass]="['message', message.sent ? 'sent' : 'received']">{{message.text}}</ion-text>
                </ion-item>
            </ion-list>
        </ion-content>

        <ion-footer>
            <ion-item>
                <ion-input [(ngModel)]="text" type="text" [disabled]="sendingMessage" required placeholder="Message..."></ion-input>
                <ion-button [disabled]="text === ''" (click)="sendMessage()">
                    <ion-icon *ngIf="!sendingMessage" name="send"></ion-icon>
                    <ion-spinner *ngIf="sendingMessage"></ion-spinner>
                </ion-button>
            </ion-item>
        </ion-footer>
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

    readonly conversationId: string;

    @ViewChild(IonContent) content: IonContent;

    profile: Profile = null;
    messages: Message[] = null;

    text = '';
    sendingMessage = false;

    constructor(
        route: ActivatedRoute,
        loadingController: LoadingController,
        private service: ConversationsService,
    ) {
        this.conversationId = route.snapshot.paramMap.get('conversationId');
        const profileId = route.snapshot.paramMap.get('profileId');
        processLoading(loadingController, forkJoin(
            service.getMessagesByConversation(this.conversationId)
                .pipe(tap(it => this.messages = it))
                .pipe(delay(10)) // wait for refresh
                .pipe(tap(() => this.content.scrollToBottom())),
            service.getProfile(profileId)
                .pipe(tap(it => this.profile = it)),
        )).subscribe();
    }

    public sendMessage() {
        this.sendingMessage = true;
        this.service.sendMessage(this.conversationId, this.text)
            .pipe(tap(() => this.text = ''))
            .pipe(tap(it => this.messages.push(it)))
            .pipe(delay(10)) // wait for refresh
            .pipe(tap(() => this.content.scrollToBottom()))
            .pipe(finalize(() => this.sendingMessage = false))
            .subscribe();
    }

}
